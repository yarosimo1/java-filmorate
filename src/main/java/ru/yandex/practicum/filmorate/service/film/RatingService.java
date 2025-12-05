package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.RatingDBStorage;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.rating.RatingStorage;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class RatingService {
    @Qualifier("InMemoryRatingStorage")
    private final RatingStorage inMemoryRatingStorage;
    @Qualifier("RatingDBStorage")
    private final RatingDBStorage ratingDBStorage;

    public Collection<Rating> getRatings() {
        log.info("Получен запрос на получение всех рейтингов {}");
        return ratingDBStorage.findAll();
    }

    public Optional<Rating> getRatingById(Long id) {
        log.info("Получен запрос на получение рейтинга {}");
        return ratingDBStorage.findById(id);
    }

    public Rating postRating(Rating rating) {
        log.info("Получен запрос на добавление рейтинга {}", rating);

        Optional<Rating> alreadyExistUser = ratingDBStorage.findByName(rating.getName());

        if (alreadyExistUser.isPresent()) {
            throw new DuplicatedDataException("Данный рейтинг уже существует");
        }

        Rating createdRating = ratingDBStorage.add(rating);
        log.info("Добавлен новый фильм: id={}, name={}", createdRating.getId(), createdRating.getName());

        return createdRating;
    }

    public Rating putRating(Rating newRating) {
        log.info("Получен запрос на обновление рейтинга с id={}", newRating.getId());

        Rating oldRating = ratingDBStorage.findById(newRating.getId()).orElseThrow(() -> new NoSuchElementException("Рейтинга не существует"));

        oldRating.setName(newRating.getName());

        inMemoryRatingStorage.update(oldRating);
        log.info("Рейтинг id={} успешно обновлён", newRating.getId());

        return oldRating;
    }

    public Rating deleteRating(Long id) {
        log.info("Получен запрос на удаление рейтинга с id={}", id);

        if (id == null || id == 0 || id < 0) {
            log.warn("Ошибка удаления: фильма с id={} не найден", id);
            throw new NoSuchElementException("Рейтинг с id=" + id + " не найден");
        }

        Rating deletedRating = ratingDBStorage.delete(id);

        log.info("Рейтинг id={} успешно удален", deletedRating.getId());

        return deletedRating;
    }
}
