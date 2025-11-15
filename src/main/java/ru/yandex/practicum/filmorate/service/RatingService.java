package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.InMemoryRatingStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.Collection;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class RatingService {
    private final RatingStorage inMemoryRatingStorage;

    public RatingService(InMemoryRatingStorage inMemoryRatingStorage) {
        this.inMemoryRatingStorage = inMemoryRatingStorage;
    }

    public Collection<Rating> getRating() {
        log.info("Получен запрос на получение всех рейтингов {}", inMemoryRatingStorage.getRating().size());
        return inMemoryRatingStorage.getRating().values();
    }

    public Rating postRating(Rating rating) {
        log.info("Получен запрос на добавление рейтинга {}", rating);

        Rating createdRating = inMemoryRatingStorage.add(rating);
        log.info("Добавлен новый фильм: id={}, name={}", createdRating.getId(), createdRating.getName());

        return createdRating;
    }

    public Rating putRating(Rating newRating) {
        log.info("Получен запрос на обновление рейтинга с id={}", newRating.getId());

        Rating oldRating = inMemoryRatingStorage.getRating().get(newRating.getId());

        if (oldRating == null) {
            log.warn("Ошибка обновления: рейтинга с id={} не найден", newRating.getId());
            throw new NoSuchElementException("Рейтинг с id=" + newRating.getId() + " не найден");
        }

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

        Rating deletedRating = inMemoryRatingStorage.delete(id);

        log.info("Рейтинг id={} успешно удален", deletedRating.getId());

        return deletedRating;
    }
}
