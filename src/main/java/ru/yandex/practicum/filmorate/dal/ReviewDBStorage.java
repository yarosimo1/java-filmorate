package ru.yandex.practicum.filmorate.dal;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class ReviewDBStorage extends BaseRepository<Review> implements ReviewStorage {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM REVIEW WHERE REVIEW_ID = ?";
    private static final String FIND_BY_REVIEW_FILM_QUERY = "SELECT * FROM REVIEW WHERE FILM_ID = ? " +
            "ORDER BY USEFUL DESC LIMIT ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM REVIEW ORDER BY USEFUL DESC LIMIT ?";
    private static final String INSERT_QUERY = "INSERT INTO REVIEW (CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE REVIEW SET CONTENT = ?, IS_POSITIVE = ? " +
            "WHERE REVIEW_ID = ?";
    private static final String DELETE_QUERY = "DELETE FROM REVIEW WHERE REVIEW_ID = ?";

    private static final String INSERT_REACTION_QUERY = "MERGE INTO REVIEW_REACTIONS (REVIEW_ID, USER_ID, IS_LIKE) " +
            "VALUES (?, ? ,?)";
    private static final String REMOVE_REACTION_QUERY = "DELETE FROM REVIEW_REACTIONS " +
            "WHERE REVIEW_ID = ? AND USER_ID = ?";
    private static final String UPDATE_REACTION_QUERY = "UPDATE REVIEW SET USEFUL = ? WHERE REVIEW_ID = ?";
    private static final String GET_REACTION_QUERY =
            "SELECT IS_LIKE FROM REVIEW_REACTIONS WHERE REVIEW_ID = ? AND USER_ID = ?";

    public ReviewDBStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    public Optional<Review> findById(long reviewId) {
        return findOne(FIND_BY_ID_QUERY, reviewId);
    }

    public Collection<Review> findAll(int count) {
        return findMany(FIND_ALL_QUERY, count);
    }

    @Override
    public Review add(Review review) {
        long id = insert(INSERT_QUERY, review.getContent(), review.getIsPositive(), review.getUserId(),
                review.getFilmId(), review.getUseful());
        review.setReviewId(id);
        return review;
    }

    @Override
    public Review update(Review updateReview) {
        update(UPDATE_QUERY, updateReview.getContent(),
                updateReview.getIsPositive(),
                updateReview.getReviewId());
        return updateReview;
    }

    @Override
    public void delete(Long id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public void addReaction(Long reviewId, Long userId, boolean isLike) {
        update(INSERT_REACTION_QUERY, reviewId, userId, isLike);
    }

    @Override
    public void removeReaction(Long reviewId, Long userId) {
        update(REMOVE_REACTION_QUERY, reviewId, userId);
    }

    @Override
    public void updateUseful(Long reviewId, int useful) {
        update(UPDATE_REACTION_QUERY, useful, reviewId);
    }

    public Collection<Review> getReviewsByFilm(Long filmId, int count) {
        return findMany(FIND_BY_REVIEW_FILM_QUERY, filmId, count);
    }

    public Boolean getReaction(Long reviewId, Long userId) {
        try {
            return jdbc.queryForObject(GET_REACTION_QUERY, Boolean.class, reviewId, userId);
        } catch (EmptyResultDataAccessException e) {
            return null;  // реакции не было
        }
    }
}
