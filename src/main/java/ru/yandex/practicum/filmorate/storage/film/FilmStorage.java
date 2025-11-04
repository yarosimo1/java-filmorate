package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film newFilm);

    Film delete(Long id);

    Map<Long, Film> getFilms();
}
