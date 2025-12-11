package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FilmDBStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = """
            SELECT f.FILM_ID,
                   f.NAME,
                   f.DESCRIPTION,
                   f.RELEAS_DATE,
                   f.DURATION,
                   m.RATING_ID,
                   m.NAME AS RATING_NAME
            FROM FILM f
            LEFT JOIN RATING_MPA m ON f.RATING_MPA_ID = m.RATING_ID
            ORDER BY f.FILM_ID
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT f.FILM_ID,
                   f.NAME,
                   f.DESCRIPTION,
                   f.RELEAS_DATE,
                   f.DURATION,
                   m.RATING_ID,
                   m.NAME AS RATING_NAME
            FROM FILM f
            LEFT JOIN RATING_MPA m ON f.RATING_MPA_ID = m.RATING_ID
            WHERE f.FILM_ID = ?
            """;
    private static final String INSERT_QUERY = """
            INSERT INTO FILM (DESCRIPTION, NAME, RELEAS_DATE, DURATION, RATING_MPA_ID)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_QUERY = """
            UPDATE FILM SET DESCRIPTION = ?, NAME = ?, RELEAS_DATE = ?, DURATION = ?, RATING_MPA_ID = ?
            WHERE FILM_ID = ?
            """;
    private static final String DELETE_QUERY = """
            DELETE FROM FILM WHERE FILM_ID = ?
            """;
    private static final String DELETE_FILM_GENRES = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
    private static final String DELETE_FILM_LIKES = "DELETE FROM FILM_LIKES WHERE FILM_ID = ?";
    private static final String DELETE_FILM_LIKE = " DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?";
    private static final String INSERT_FILM_GENRE = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";
    private static final String INSERT_FILM_LIKES = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
    private static final String LOAD_GENRES = """
            SELECT fg.FILM_ID, g.GENRE_ID, g.NAME
            FROM FILMS_GENRES fg
            JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID
            ORDER BY g.GENRE_ID
            """;
    private static final String LOAD_LIKES = "SELECT FILM_ID, USER_ID FROM FILM_LIKES";

    private static final String FIND_COMMON_FILMS = """
    SELECT f.FILM_ID,
        f.NAME,
        f.DESCRIPTION,
        f.RELEAS_DATE,
        f.DURATION,
        m.RATING_ID,
        m.NAME AS RATING_NAME
    FROM FILM f
    JOIN FILM_LIKES fl1 ON f.FILM_ID = fl1.FILM_ID AND fl1.USER_ID = ?
    JOIN FILM_LIKES fl2 ON f.FILM_ID = fl2.FILM_ID AND fl2.USER_ID = ?
    LEFT JOIN FILM_LIKES fl_all ON f.FILM_ID = fl_all.FILM_ID
    LEFT JOIN RATING_MPA m ON f.RATING_MPA_ID = m.RATING_ID
    GROUP BY f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEAS_DATE, f.DURATION, m.RATING_ID, m.NAME
    ORDER BY COUNT(fl_all.USER_ID) DESC
    """;

    public FilmDBStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    // ----------- LOADING HELPERS ------------

    private Map<Long, Set<Genre>> loadGenres() {

        return jdbc.query(LOAD_GENRES, rs -> {
            Map<Long, Set<Genre>> map = new HashMap<>();

            while (rs.next()) {
                long filmId = rs.getLong("FILM_ID");

                map.computeIfAbsent(filmId, id -> new HashSet<>()).add(Genre.builder().id(rs.getLong("GENRE_ID")).name(rs.getString("NAME")).build());
            }
            return map;
        });
    }

    private Map<Long, Set<Long>> loadLikes() {
        return jdbc.query(LOAD_LIKES, rs -> {
            Map<Long, Set<Long>> map = new HashMap<>();

            while (rs.next()) {
                long filmId = rs.getLong("FILM_ID");
                long userId = rs.getLong("USER_ID");

                map.computeIfAbsent(filmId, f -> new HashSet<>()).add(userId);
            }
            return map;
        });
    }

    private void enrichFilms(List<Film> films) {
        Map<Long, Set<Genre>> genres = loadGenres();
        Map<Long, Set<Long>> likes = loadLikes();

        for (Film film : films) {
            film.setGenres(genres.getOrDefault(film.getId(), new HashSet<>()));
            film.getWhoLikes().addAll(likes.getOrDefault(film.getId(), Collections.emptySet()));
        }
    }

    // ------------ CRUD ------------

    public List<Film> findAll() {
        List<Film> films = findMany(FIND_ALL_QUERY);
        enrichFilms(films);
        return films;
    }

    public Optional<Film> findById(long id) {
        Optional<Film> maybeFilm = findOne(FIND_BY_ID_QUERY, id);

        if (maybeFilm.isEmpty()) {
            return Optional.empty();
        }

        Film film = maybeFilm.get();

        // Догружаем только для одного фильма
        film.setGenres(loadGenres().getOrDefault(id, new HashSet<>()));
        film.getWhoLikes().addAll(loadLikes().getOrDefault(id, new HashSet<>()));

        return Optional.of(film);
    }

    public void updateFilmGenres(long filmId, Set<Genre> genres) {
        jdbc.update(DELETE_FILM_GENRES, filmId);

        if (genres == null || genres.isEmpty()) {
            return;
        }

        List<Genre> genreList = new ArrayList<>(genres);

        jdbc.batchUpdate(INSERT_FILM_GENRE, genreList, genreList.size(), (ps, genre) -> {
            ps.setLong(1, filmId);
            ps.setLong(2, genre.getId());
        });
    }

    public List<Film> findCommonFilms(long userId, long friendId) {
        List<Film> films = findMany(FIND_COMMON_FILMS, userId, friendId);

        enrichFilms(films);

        return films;
    }



    @Override
    public Film add(Film film) {
        long id = insert(INSERT_QUERY, film.getDescription(), film.getName(), film.getReleaseDate(), film.getDuration().toSeconds(), film.getMpa() != null ? film.getMpa().getId() : null);

        film.setId(id);

        return film;
    }

    public void addLike(long filmId, long userId) {
        jdbc.update(INSERT_FILM_LIKES, filmId, userId);

    }

    public void deleteLike(long filmId, long userId) {
        jdbc.update(DELETE_FILM_LIKE, filmId, userId);
    }

    @Override
    public Film update(Film film) {
        update(UPDATE_QUERY, film.getDescription(), film.getName(), film.getReleaseDate(), film.getDuration().toSeconds(), film.getMpa() != null ? film.getMpa().getId() : null, film.getId());

        return film;
    }

    @Override
    public Film delete(Long id) {
        Film film = findById(id).orElseThrow(() -> new NoSuchElementException("Фильм не найден"));

        jdbc.update(DELETE_FILM_GENRES, id);

        jdbc.update(DELETE_FILM_LIKES, id);

        delete(DELETE_QUERY, id);

        return film;
    }

    @Override
    public Map<Long, Film> getFilms() {
        return findAll().stream().collect(Collectors.toMap(Film::getId, f -> f));
    }
}