package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.rating.RatingStorage;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
public class RatingDBStorage extends BaseRepository<Rating> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM RATING_MPA";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM RATING_MPA WHERE RATING_ID = ?";
    private static final String FIND_BY_GENRE_NAME_QUERY = "SELECT * FROM RATING_MPA WHERE NAME = ?";

    public RatingDBStorage(JdbcTemplate jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    public Optional<Rating> findById(long genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId);
    }

    public List<Rating> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Rating> findByName(String name) {
        return findOne(FIND_BY_GENRE_NAME_QUERY, name);
    }

}
