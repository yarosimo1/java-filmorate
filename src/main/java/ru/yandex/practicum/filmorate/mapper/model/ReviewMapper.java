package ru.yandex.practicum.filmorate.mapper.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.review.ReviewCreateDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewUpdateDto;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.HashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReviewMapper {
    public static Review mapToReview(ReviewCreateDto dto) {
        return Review.builder()
                .content(dto.getContent())
                .isPositive(dto.getIsPositive())
                .userId(dto.getUserId())
                .filmId(dto.getFilmId())
                .useful(0)
                .reactions(new HashMap<>())
                .build();
    }

    public static ReviewDto mapToReviewDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(review.getReviewId());
        dto.setContent(review.getContent());
        dto.setIsPositive(review.getIsPositive());
        dto.setUserId(review.getUserId());
        dto.setFilmId(review.getFilmId());
        dto.setUseful(review.getUseful());
        return dto;
    }

    public static Review updateReviewFields(Review review, ReviewUpdateDto dto) {
        if (dto.getContent() != null)
            review.setContent(dto.getContent());
        if (dto.getIsPositive() != null)
            review.setIsPositive(dto.getIsPositive());
        return review;
    }
}
