package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.rating.RatingCreateDto;
import ru.yandex.practicum.filmorate.dto.rating.RatingDto;
import ru.yandex.practicum.filmorate.dto.rating.RatingUpdateDto;
import ru.yandex.practicum.filmorate.model.Rating;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RatingMapper {

    public static Rating mapToRating(RatingCreateDto dto) {
        Rating rating = new Rating();
        rating.setName(dto.getName());
        return rating;
    }

    public static RatingDto mapToDto(Rating rating) {
        RatingDto dto = new RatingDto();
        dto.setId(rating.getId());
        dto.setName(rating.getName());
        return dto;
    }

    public static Rating updateRatingFields(Rating rating, RatingUpdateDto dto) {
        if (dto.getName() != null) rating.setName(dto.getName());
        return rating;
    }
}
