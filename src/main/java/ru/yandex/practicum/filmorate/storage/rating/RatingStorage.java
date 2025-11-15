package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Map;

public interface RatingStorage {
    Rating add(Rating rating);

    Rating update(Rating newRating);

    Rating delete(Long id);

    Map<Long, Rating> getRating();
}