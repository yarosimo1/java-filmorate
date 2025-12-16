package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {
    Review add(Review review);

    Review update(Review newReview);

    void delete(Long id);

    Optional<Review> findById(long reviewId);

    Collection<Review> getReviewsByFilm(Long filmId, int count);

    void addReaction(Long reviewId, Long userId, boolean isLike);

    void removeReaction(Long reviewId, Long userId);

    void updateUseful(Long reviewId, int useful);
}
