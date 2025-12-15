package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

@Repository
public class DirectorDBStorage extends BaseRepository<Director> {
    private static final String FIND_ALL_QUERY = "SELECT DIRECTOR_ID, NAME FROM DIRECTORS";
    private static final String FIND_BY_ID_QUERY = "SELECT DIRECTOR_ID, NAME FROM DIRECTORS WHERE DIRECTOR_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO DIRECTORS (NAME) " +
            "VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE DIRECTORS SET NAME = ? WHERE DIRECTOR_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";
    private static final String GET_DIRECTOR_FILMS_QUERY = "SELECT f.FILM_ID, " +
            "f.NAME, " +
            "f.DESCRIPTION, " +
            "f.DURATION, " +
            "f.RELEAS_DATE, " +
            "r.RATING_ID, " +
            "r.RATING_NAME " +
            "FROM FILMS as f LEFT JOIN RATINGS as r ON f.RATING_ID = r.RATING_ID " +
            "WHERE f.DIRECTOR_ID = ?";

    public DirectorDBStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    public List<Director> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Director> findById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Director add(Director director) {
        long id = insert(INSERT_QUERY, director.getName());

        Optional<Director> directorOpt = findById(id);

        return directorOpt.orElseThrow(() -> new RuntimeException("Произошла ошибка при добавлении режиссера"));
    }

    public Director update(Director director) {
        Optional<Director> directorOpt = findById(director.getId());
        if (directorOpt.isEmpty())
            throw new NotFoundException("Такого режиссера нет");

        update(UPDATE_QUERY, director.getName(), director.getId());

        Optional<Director> directorOptUpdated = findById(director.getId());

        return directorOptUpdated.orElseThrow(() ->
                new RuntimeException("Обновление записи изменило идентификатор(как? не знаю)"));
    }

    public void delete(Long id) {
        delete(DELETE_QUERY, id);
    }

    public List<Film> getDirectorFilms(Long id) {
        return jdbc.query(GET_DIRECTOR_FILMS_QUERY, new FilmRowMapper(), id);
    }
}
