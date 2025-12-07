package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.RatingDBStorage;
import ru.yandex.practicum.filmorate.dto.rating.RatingDto;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class RatingService {
    @Qualifier("RatingDBStorage")
    private final RatingDBStorage ratingDBStorage;

    public Collection<RatingDto> getRatings() {
        log.info("Получен запрос на получение всех рейтингов");
        return ratingDBStorage.findAll().stream().map(RatingMapper::mapToDto).collect(Collectors.toList());
    }

    public RatingDto getRatingById(Long id) {
        log.info("Получен запрос на получение рейтинга по id={}", id);
        Rating rating = ratingDBStorage.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Указанного рейтинга не существует"));
        return RatingMapper.mapToDto(rating);
    }
}
