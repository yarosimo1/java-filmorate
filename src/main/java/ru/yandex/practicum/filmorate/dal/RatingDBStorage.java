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
public class RatingDBStorage extends BaseRepository<Rating> implements RatingStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM RATING_MPA";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM RATING_MPA WHERE RATING_ID = ?";
    private static final String FIND_BY_GENRE_NAME_QUERY = "SELECT * FROM RATING_MPA WHERE NAME = ?";
    private static final String INSERT_QUERY = "INSERT INTO RATING_MPA (NAME) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE RATING_MPA SET NAME = ? WHERE RATING_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM RATING_MPA WHERE RATING_ID = ?";

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

    @Override
    public Rating add(Rating rating) {
        long id = insert(INSERT_QUERY, rating.getName());
        rating.setId(id);
        return rating;
    }

    @Override
    public Rating update(Rating newRating) {
        update(UPDATE_QUERY, newRating.getName(), newRating.getId());
        return newRating;
    }

    @Override
    public Rating delete(Long id) {
        Rating rating = findById(id).orElseThrow(() -> new NoSuchElementException("Рейтинг не найден"));
        delete(DELETE_QUERY, id);
        return rating;
    }

    @Override
    public Map<Long, Rating> getRating() {
        return Map.of();
    }
}
