package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmLikesDBStorage;
import ru.yandex.practicum.filmorate.model.FilmLikes;

import java.util.Collection;

@Slf4j
@Service
@AllArgsConstructor
public class FilmLikesService {
    private final FilmLikesDBStorage filmLikesDBStorage;

    public Collection<FilmLikes> getFilmLikes() {
        return filmLikesDBStorage.findAll();
    }

    public void create(Long filmId, Long userId) {
        filmLikesDBStorage.add(filmId, userId);
    }

    public void deleteLikes(Long filmId, Long userId) {
        filmLikesDBStorage.delete(filmId, userId);
    }
}
