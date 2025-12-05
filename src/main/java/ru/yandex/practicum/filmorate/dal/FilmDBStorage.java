package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
public class FilmDBStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM FILM";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM FILM WHERE FILM_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO FILM (DESCRIPTION, NAME, RELEAS_DATE, DURATION, RATING_MPA_ID) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE FILM SET DESCRIPTION = ?, NAME = ?, RELEAS_DATE = ?, DURATION = ?, RATING_MPA_ID = ? WHERE FILM_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM FILM WHERE FILM_ID = ?";

    public FilmDBStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public Optional<Film> findById(long filmId) {
        Optional<Film> film = findOne(FIND_BY_ID_QUERY, filmId);
        return film;
    }

    public List<Film> findAll() {
        List<Film> films = findMany(FIND_ALL_QUERY);
        return films;
    }

    @Override
    public Film add(Film film) {
        long id = insert(INSERT_QUERY, film.getDescription(), film.getName(), film.getReleaseDate(), film.getDuration().toMinutes(), film.getMpa() != null ? film.getMpa().getId() : null);

        film.setId(id);
        return film;
    }

    @Override
    public Film update(Film film) {
        update(UPDATE_QUERY, film.getDescription(), film.getName(), film.getReleaseDate(), film.getDuration().toMinutes(), film.getMpa() != null ? film.getMpa().getId() : null, film.getId());
        return film;
    }

    @Override
    public Film delete(Long id) {
        Film film = findById(id).orElseThrow(() -> new NoSuchElementException("Фильм не найден"));
        delete(DELETE_QUERY, id);
        return film;
    }

    @Override
    public Map<Long, Film> getFilms() {
        return Map.of();
    }
}