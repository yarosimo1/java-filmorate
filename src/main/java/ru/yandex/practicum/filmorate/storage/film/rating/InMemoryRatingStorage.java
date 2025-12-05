package ru.yandex.practicum.filmorate.storage.film.rating;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class InMemoryRatingStorage implements RatingStorage {
    private Map<Long, Rating> ratings = new HashMap<>();

    @Override
    public Rating add(Rating rating) {
        log.info("Добавление рейтинга Rating={}", rating);
        ratings.put(rating.getId(), rating);
        return rating;
    }

    @Override
    public Rating update(Rating newRating) {
        log.info("Обновление рейтинга Rating={}, на \n newRating={}",
                ratings.get(newRating.getId()), newRating);
        return ratings.put(newRating.getId(), newRating);
    }

    @Override
    public Rating delete(Long id) {
        log.info("Удаление рейтинга Rating={}", ratings.get(id));
        return ratings.remove(id);
    }

    @Override
    public Map<Long, Rating> getRating() {
        return Map.of();
    }
}
