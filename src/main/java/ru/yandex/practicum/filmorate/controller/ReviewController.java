package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.ReviewCreateDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewUpdateDto;
import ru.yandex.practicum.filmorate.service.film.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{id}")
    public ReviewDto getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<ReviewDto> getReviewsByFilm(@RequestParam(required = false) Long filmId,
                                            @RequestParam(defaultValue = "10") int count) {
        return reviewService.getReviewsByFilm(filmId, count);
    }

    @PostMapping
    public ReviewDto createReview(@RequestBody ReviewCreateDto dto) {
        return reviewService.createReview(dto);
    }

    @PutMapping
    public ReviewDto updateReview(@RequestBody ReviewUpdateDto dto) {
        return reviewService.updateReview(dto);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public ReviewDto addLike(@PathVariable("id") Long reviewId, @PathVariable("userId") Long userId) {
        return reviewService.addLike(reviewId, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ReviewDto addDislike(@PathVariable("id") Long reviewId, @PathVariable("userId") Long userId) {
        return reviewService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ReviewDto removeLike(@PathVariable("id") Long reviewId, @PathVariable("userId") Long userId) {
        return reviewService.removeLike(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ReviewDto removeDislike(@PathVariable("id") Long reviewId, @PathVariable("userId") Long userId) {
        return reviewService.removeDislike(reviewId, userId);
    }
}
