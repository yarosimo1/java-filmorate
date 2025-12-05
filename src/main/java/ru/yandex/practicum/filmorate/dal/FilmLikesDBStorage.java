package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmLikes;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmLikesDBStorage extends BaseRepository<FilmLikes> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM FILM_LIKES";
    private static final String FIND_BY_FILM_ID_QUERY = "SELECT * FROM FILM_LIKES WHERE FILM_ID = ?";
    private static final String FIND_BY_LIKE_ID_QUERY = "SELECT * FROM FILM_LIKES WHERE USER_ID = ?";
    private static final String INSERT_QUERY = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
    private static final String UPDATE_QUERY = "UPDATE FILM_LIKES SET ";
    private static final String DELETE_QUERY = "DELETE FROM FILM_LIKES WHERE FILM_ID = ? AND USER_ID = ?";


    public FilmLikesDBStorage(JdbcTemplate jdbc, RowMapper<FilmLikes> mapper) {
        super(jdbc, mapper);
    }

    public Optional<FilmLikes> findByFilmId(long filmId) {
        Optional<FilmLikes> filmLikes = findOne(FIND_BY_FILM_ID_QUERY, filmId);
        return filmLikes;
    }

    public Optional<FilmLikes> findByGenreId(long userId) {
        Optional<FilmLikes> filmLikes = findOne(FIND_BY_LIKE_ID_QUERY, userId);
        return filmLikes;
    }

    public List<FilmLikes> findAll() {
        List<FilmLikes> filmLikes = findMany(FIND_ALL_QUERY);
        return filmLikes;
    }

    public void add(Long filmId, Long userId) {
        update(INSERT_QUERY, filmId, userId);
    }

    public FilmLikes update(FilmLikes newFilmLikes) {
        return null;
    }

    public void delete(Long filmId, Long userId) {
        update(DELETE_QUERY, filmId, userId);
    }
}
