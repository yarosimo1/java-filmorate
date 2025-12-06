package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.RatingDBStorage;
import ru.yandex.practicum.filmorate.dto.rating.RatingCreateDto;
import ru.yandex.practicum.filmorate.dto.rating.RatingDto;
import ru.yandex.practicum.filmorate.dto.rating.RatingUpdateDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.rating.RatingStorage;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class RatingService {

    @Qualifier("InMemoryRatingStorage")
    private final RatingStorage inMemoryRatingStorage;

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

    public RatingDto postRating(RatingCreateDto dto) {
        log.info("Получен запрос на добавление рейтинга {}", dto.getName());

        // Проверка на дубликат
        Optional<Rating> alreadyExist = ratingDBStorage.findByName(dto.getName());
        if (alreadyExist.isPresent()) {
            throw new DuplicatedDataException("Данный рейтинг уже существует");
        }

        Rating rating = RatingMapper.mapToRating(dto);
        Rating createdRating = ratingDBStorage.add(rating);

        log.info("Добавлен новый рейтинг: id={}, name={}", createdRating.getId(), createdRating.getName());
        return RatingMapper.mapToDto(createdRating);
    }

    public RatingDto putRating(RatingUpdateDto dto) {
        log.info("Получен запрос на обновление рейтинга id={}", dto.getId());

        Rating rating = ratingDBStorage.findById(dto.getId()).orElseThrow(() -> new NoSuchElementException("Рейтинга не существует"));

        RatingMapper.updateRatingFields(rating, dto);

        ratingDBStorage.update(rating);
        inMemoryRatingStorage.update(rating);

        log.info("Рейтинг id={} успешно обновлен", dto.getId());
        return RatingMapper.mapToDto(rating);
    }

    public RatingDto deleteRating(Long id) {
        log.info("Получен запрос на удаление рейтинга id={}", id);

        if (id == null || id <= 0) {
            throw new NoSuchElementException("Рейтинг с id=" + id + " не найден");
        }

        Rating deleted = ratingDBStorage.delete(id);

        log.info("Рейтинг id={} успешно удален", deleted.getId());
        return RatingMapper.mapToDto(deleted);
    }
}
