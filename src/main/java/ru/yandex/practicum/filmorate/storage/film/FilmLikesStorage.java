package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.FilmLikes;

import java.util.Map;

public interface FilmLikesStorage {
    FilmLikes add(FilmLikes filmLikes);

    FilmLikes update(FilmLikes newFilmLikes);

    FilmLikes delete(Long id);

    Map<Long, FilmLikes> getFilms();
}
