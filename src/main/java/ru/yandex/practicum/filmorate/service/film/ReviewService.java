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
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.user.EventService;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.time.Instant;
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
    private final EventService eventService;

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

        eventService.addEventToUser(
                Event.builder()
                        .timestamp(Instant.now().toEpochMilli())
                        .userId(review.getUserId())
                        .eventType("REVIEW")
                        .operation("ADD")
                        .entityId(review.getReviewId())
                        .build()
        );
        return ReviewMapper.mapToReviewDto(createdReview);
    }

    public ReviewDto updateReview(ReviewUpdateDto dto) {
        log.info("Обновление отзыва DTO={}", dto);

        Review existingReview = reviewDBStorage.findById(dto.getReviewId())
                .orElseThrow(() -> new NotFoundException("Ревью с id=" + dto.getReviewId() + " не найден"));

        ReviewMapper.updateReviewFields(existingReview, dto);

        reviewDBStorage.update(existingReview);

        eventService.addEventToUser(
                Event.builder()
                        .timestamp(Instant.now().toEpochMilli())
                        .userId(existingReview.getUserId())
                        .eventType("REVIEW")
                        .operation("UPDATE")
                        .entityId(existingReview.getReviewId())
                        .build()
        );
        return ReviewMapper.mapToReviewDto(existingReview);
    }

    public void deleteReview(Long id) {
        log.info("Удаление ревью с id={}", id);
        Review existingReview = reviewDBStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Ревью с id=" + id + " не найден"));

        reviewDBStorage.delete(id);

        eventService.addEventToUser(
                Event.builder()
                        .timestamp(Instant.now().toEpochMilli())
                        .userId(existingReview.getUserId())
                        .eventType("REVIEW")
                        .operation("REMOVE")
                        .entityId(existingReview.getReviewId())
                        .build()
        );
    }

    public ReviewDto addLike(Long reviewId, Long userId) {
        log.info("Добавление лайка пользователя {} отзыва {}", userId, reviewId);

        Review review = reviewDBStorage.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        userService.getUserById(userId);  // проверка существования пользователя

        // Проверяем была ли предыдущая реакция
        Boolean previousReaction = reviewDBStorage.getReaction(reviewId, userId);

        // Если уже лайк - ничего не делаем
        if (previousReaction != null && previousReaction) {
            return ReviewMapper.mapToReviewDto(review);
        }

        reviewDBStorage.addReaction(reviewId, userId, true);

        // а здесь уже обновляем useful
        int useful = review.getUseful() + calculateUsefulReaction(previousReaction, true);

        reviewDBStorage.updateUseful(reviewId, useful);
        review.setUseful(useful);

        eventService.addEventToUser(
                Event.builder()
                        .timestamp(Instant.now().toEpochMilli())
                        .userId(userId)
                        .eventType("LIKE")
                        .operation("ADD")
                        .entityId(reviewId)
                        .build()
        );

        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto addDislike(Long reviewId, Long userId) {
        log.info("Добавление дизлайка пользователя {} отзыва {}", userId, reviewId);

        Review review = reviewDBStorage.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        userService.getUserById(userId);  // проверка существования пользователя

        // Проверяем была ли предыдущая реакция
        Boolean previousReaction = reviewDBStorage.getReaction(reviewId, userId);

        // Если уже дизлайк - ничего не делаем
        if (previousReaction != null && !previousReaction) {
            return ReviewMapper.mapToReviewDto(review);
        }

        reviewDBStorage.addReaction(reviewId, userId, false);

        // а здесь уже обновляем useful
        int useful = review.getUseful() + calculateUsefulReaction(previousReaction, false);

        reviewDBStorage.updateUseful(reviewId, useful);
        review.setUseful(useful);

        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto removeLike(Long reviewId, Long userId) {
        log.info("Удаление лайка пользователя {} отзыва {}", userId, reviewId);

        Review review = reviewDBStorage.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        userService.getUserById(userId);  // проверка существования пользователя

        Boolean previousReaction = reviewDBStorage.getReaction(reviewId, userId);

        // Если реакции не было или это дизлайк - ничего не удаляем
        if (previousReaction == null || !previousReaction) {
            return ReviewMapper.mapToReviewDto(review);
        }

        reviewDBStorage.removeReaction(reviewId, userId);

        // а здесь уже обновляем useful
        int useful = review.getUseful() - 1;
        reviewDBStorage.updateUseful(reviewId, useful);
        review.setUseful(useful);

        eventService.addEventToUser(
                Event.builder()
                        .timestamp(Instant.now().toEpochMilli())
                        .userId(userId)
                        .eventType("LIKE")
                        .operation("REMOVE")
                        .entityId(reviewId)
                        .build()
        );

        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto removeDislike(Long reviewId, Long userId) {
        log.info("Удаление дизлайка пользователя {} отзыва {}", userId, reviewId);

        Review review = reviewDBStorage.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        userService.getUserById(userId);  // проверка существования пользователя

        Boolean previousReaction = reviewDBStorage.getReaction(reviewId, userId);

        // Если реакции не было или это лайк - ничего не удаляем
        if (previousReaction == null || previousReaction) {
            return ReviewMapper.mapToReviewDto(review);
        }

        reviewDBStorage.removeReaction(reviewId, userId);

        // а здесь уже обновляем useful
        int useful = review.getUseful() + 1;
        reviewDBStorage.updateUseful(reviewId, useful);
        review.setUseful(useful);

        return ReviewMapper.mapToReviewDto(review);
    }

    private int calculateUsefulReaction(Boolean previousReaction, boolean isLike) {
        if (previousReaction == null)
            // значит реакции еще не было
            return isLike ? 1 : -1;

        // та же реакция, ничего не меняем
        if (previousReaction == isLike)
            return 0;

        // Смена реакции: лайк - дизлайк = -2, дизлайк - лайк = +2
        return isLike ? 2 : -2;
    }
}
