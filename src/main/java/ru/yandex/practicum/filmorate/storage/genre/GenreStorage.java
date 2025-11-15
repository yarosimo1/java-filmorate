package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Map;

public interface GenreStorage {
    Genre add(Genre genre);

    Genre update(Genre newGenre);

    Genre delete(Long id);

    Map<Long, Genre> getGenre();
}
