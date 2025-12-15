package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.ReviewDBStorage;
import ru.yandex.practicum.filmorate.dto.review.ReviewCreateDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewUpdateDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewDBStorage reviewDBStorage;
    private final UserService userService;
    private final FilmService filmService;

    public ReviewDto getReviewById(Long id) {
        log.info("Получен запрос на получение отзыва id={}", id);
        Review review = reviewDBStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));

        return ReviewMapper.mapToReviewDto(review);
    }

    public List<ReviewDto> getReviewsByFilm(Long filmId, int count) {
        log.info("Получен запрос на получение отзывов фильма filmId={}", filmId);

        Collection<Review> reviews;
        if (filmId == null)
            reviews = reviewDBStorage.findAll(count); // вернуть все если null
        else
            reviews = reviewDBStorage.getReviewsByFilm(filmId, count);
        return reviews.stream()
                .map(ReviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }

    public ReviewDto createReview(ReviewCreateDto dto) {
        log.info("Добавление отзыва DTO={}", dto);

        // Проверяем существование пользователя и фильма
        userService.getUserById(dto.getUserId());
        filmService.getFilmById(dto.getFilmId());

        Review review = ReviewMapper.mapToReview(dto);
        Review createdReview = reviewDBStorage.add(review);
        return ReviewMapper.mapToReviewDto(createdReview);
    }

    public ReviewDto updateReview(ReviewUpdateDto dto) {
        log.info("Обновление отзыва DTO={}", dto);

        Review existingReview = reviewDBStorage.findById(dto.getReviewId())
                .orElseThrow(() -> new NotFoundException("Ревью с id=" + dto.getReviewId() + " не найден"));

        ReviewMapper.updateReviewFields(existingReview, dto);

        reviewDBStorage.update(existingReview);
        return ReviewMapper.mapToReviewDto(existingReview);
    }

    public void deleteReview(Long id) {
        log.info("Удаление ревью с id={}", id);
        reviewDBStorage.delete(id);
    }

    public ReviewDto addLike(Long reviewId, Long userId) {
        log.info("Добавление лайка пользователя {} отзыва {}", userId, reviewId);

        Review review = reviewDBStorage.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        userService.getUserById(userId);  // проверка существования пользователя

        // Проверяем была ли предыдущая реакция
        Boolean previousReaction = reviewDBStorage.getReaction(reviewId, userId);

        reviewDBStorage.addReaction(reviewId, userId, true);

        // а здесь уже обновляем useful
        int useful = review.getUseful();

        if (previousReaction != null && !previousReaction) {
            // Был дизлайк, убираем его и добавляем лайк = 2
            useful += 2;
        } else if (previousReaction == null) {
            // Реакции не было, просто лайк = +1
            useful += 1;
        }

        reviewDBStorage.updateUseful(reviewId, useful);
        review.setUseful(useful);

        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto addDislike(Long reviewId, Long userId) {
        log.info("Добавление дизлайка пользователя {} отзыва {}", userId, reviewId);

        Review review = reviewDBStorage.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        userService.getUserById(userId);  // проверка существования пользователя

        // Проверяем была ли предыдущая реакция
        Boolean previousReaction = reviewDBStorage.getReaction(reviewId, userId);

        reviewDBStorage.addReaction(reviewId, userId, false);

        // а здесь уже обновляем useful
        int useful = review.getUseful();

        if (previousReaction != null && previousReaction) {
            // Был лайк, убираем его (-1) и добавляем дизлайк (-1) = -2
            useful -= 2;
        } else if (previousReaction == null) {
            // Реакции не было, просто дизлайк = -1
            useful -= 1;
        }

        reviewDBStorage.updateUseful(reviewId, useful);
        review.setUseful(useful);

        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto removeLike(Long reviewId, Long userId) {
        log.info("Удаление лайка пользователя {} отзыва {}", userId, reviewId);

        Review review = reviewDBStorage.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        userService.getUserById(userId);  // проверка существования пользователя

        reviewDBStorage.removeReaction(reviewId, userId);

        // а здесь уже обновляем useful
        int useful = review.getUseful() - 1;
        reviewDBStorage.updateUseful(reviewId, useful);
        review.setUseful(useful);

        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto removeDislike(Long reviewId, Long userId) {
        log.info("Удаление дизлайка пользователя {} отзыва {}", userId, reviewId);

        Review review = reviewDBStorage.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        userService.getUserById(userId);  // проверка существования пользователя

        reviewDBStorage.removeReaction(reviewId, userId);

        // а здесь уже обновляем useful
        int useful = review.getUseful() + 1;
        reviewDBStorage.updateUseful(reviewId, useful);
        review.setUseful(useful);

        return ReviewMapper.mapToReviewDto(review);
    }
}
