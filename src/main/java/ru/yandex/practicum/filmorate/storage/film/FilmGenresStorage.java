package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.FilmGenres;

import java.util.Map;

public interface FilmGenresStorage {
    FilmGenres add(FilmGenres filmGenres);

    FilmGenres update(FilmGenres newFilmGenres);

    FilmGenres delete(Long id);

    Map<Long, FilmGenres> getFilms();
}
