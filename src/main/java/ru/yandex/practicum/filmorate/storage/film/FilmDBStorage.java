package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.mapper.row.DirectorRowMapper;
import ru.yandex.practicum.filmorate.mapper.row.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional
public class FilmDBStorage extends BaseRepository<Film> implements FilmStorage {

    // ---------- SQL ----------

    private static final String SELECT_FILM = """
            SELECT f.FILM_ID,
                   f.NAME,
                   f.DESCRIPTION,
                   f.RELEAS_DATE,
                   f.DURATION,
                   r.RATING_ID,
                   r.NAME AS RATING_NAME
            FROM FILM f
            LEFT JOIN RATING_MPA r ON f.RATING_MPA_ID = r.RATING_ID
            """;

    private static final String FIND_ALL = SELECT_FILM + " ORDER BY f.FILM_ID";
    private static final String FIND_BY_ID = SELECT_FILM + " WHERE f.FILM_ID = ?";

    private static final String INSERT_FILM = """
            INSERT INTO FILM (DESCRIPTION, NAME, RELEAS_DATE, DURATION, RATING_MPA_ID)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_FILM = """
            UPDATE FILM
            SET DESCRIPTION = ?, NAME = ?, RELEAS_DATE = ?, DURATION = ?, RATING_MPA_ID = ?
            WHERE FILM_ID = ?
            """;

    private static final String DELETE_FILM = "DELETE FROM FILM WHERE FILM_ID = ?";

    private static final String DELETE_FILM_GENRES = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
    private static final String INSERT_FILM_GENRES = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)";

    private static final String INSERT_LIKE = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
    private static final String DELETE_LIKE = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?";

    private static final String INSERT_FILM_DIRECTORS = "INSERT INTO DIRECTORS_FILMS (FILM_ID, DIRECTOR_ID) " +
            "VALUES (?, ?)";
    private static final String DELETE_FILM_DIRECTORS = "DELETE FROM DIRECTORS_FILMS WHERE FILM_ID = ?";

    private static final String LOAD_GENRES = """
            SELECT g.GENRE_ID, g.NAME
            FROM FILMS_GENRES fg
            JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID
            WHERE fg.FILM_ID = ?
            ORDER BY g.GENRE_ID
            """;

    private static final String LOAD_LIKES = """
            SELECT USER_ID
            FROM FILM_LIKES
            WHERE FILM_ID = ?
            """;

    private static final String LOAD_DIRECTORS = "SELECT d.DIRECTOR_ID, d.NAME FROM DIRECTORS_FILMS df " +
            "JOIN DIRECTORS d ON d.DIRECTOR_ID = df.DIRECTOR_ID " +
            "WHERE df.FILM_ID = ?";

    // ---------- deps ----------

    private final GenreRowMapper genreRowMapper;

    public FilmDBStorage(JdbcTemplate jdbc,
                         RowMapper<Film> filmRowMapper,
                         GenreRowMapper genreRowMapper) {
        super(jdbc, filmRowMapper);
        this.genreRowMapper = genreRowMapper;
    }

    // ---------- CRUD ----------

    @Override
    public Film add(Film film) {
        long id = super.insert(
                INSERT_FILM,
                film.getDescription(),
                film.getName(),
                film.getReleaseDate(),
                film.getDuration().toSeconds(),
                film.getMpa() != null ? film.getMpa().getId() : null
        );

        film.setId(id);
        saveGenres(film);
        saveDirectors(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        super.update(
                UPDATE_FILM,
                film.getDescription(),
                film.getName(),
                film.getReleaseDate(),
                film.getDuration().toSeconds(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );

        saveGenres(film);
        saveDirectors(film);
        return film;
    }

    @Override
    public Film delete(Long id) {
        Film film = findById(id).orElseThrow(() -> new NoSuchElementException("Фильм не найден"));

        super.delete(DELETE_FILM, id);

        return film;
    }

    public Optional<Film> findById(long id) {
        return super.findOne(FIND_BY_ID, id).map(this::enrich);
    }

    public List<Film> findAll() {
        List<Film> films = super.findMany(FIND_ALL);
        films.forEach(this::enrich);
        return films;
    }

    // ---------- Likes ----------

    public void addLike(long filmId, long userId) {
        jdbc.update(INSERT_LIKE, filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        jdbc.update(DELETE_LIKE, filmId, userId);
    }

    // ---------- Helpers ----------

    private Film enrich(Film film) {
        film.setGenres(loadGenres(film.getId()));
        film.getWhoLikes().addAll(loadLikes(film.getId()));
        film.setDirectors(loadDirectors(film.getId()));
        return film;
    }

    private void saveGenres(Film film) {
        jdbc.update(DELETE_FILM_GENRES, film.getId());

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        jdbc.batchUpdate(
                INSERT_FILM_GENRES,
                film.getGenres(),
                film.getGenres().size(),
                (ps, genre) -> {
                    ps.setLong(1, film.getId());
                    ps.setLong(2, genre.getId());
                }
        );
    }

    private void saveDirectors(Film film) {
        jdbc.update(DELETE_FILM_DIRECTORS, film.getId());

        if (film.getDirectors() == null || film.getDirectors().isEmpty()) {
            return;
        }

        jdbc.batchUpdate(
                INSERT_FILM_DIRECTORS,
                film.getDirectors(),
                film.getDirectors().size(),
                (ps, director) -> {
                    ps.setLong(1, film.getId());
                    ps.setLong(2, director.getId());
                }
        );
    }

    private Set<Genre> loadGenres(long filmId) {
        return new HashSet<>(jdbc.query(LOAD_GENRES, genreRowMapper, filmId));
    }

    private Set<Long> loadLikes(long filmId) {
        return new HashSet<>(jdbc.queryForList(LOAD_LIKES, Long.class, filmId));
    }

    private Set<Director> loadDirectors(long filmId) {
        return new HashSet<>(jdbc.query(LOAD_DIRECTORS, new DirectorRowMapper(), filmId));
    }

    @Override
    public Map<Long, Film> getFilms() {
        return findAll().stream().collect(Collectors.toMap(Film::getId, f -> f));
    }
}