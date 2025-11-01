package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Data
@AllArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    Map<Long, Film> films = new HashMap<>();

    @Override
    public Film add(Film film) {
        log.info("Добавление фильма Film={}", film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        log.info("Обновление фильма Film={}, на \n newFilm={}", films.get(newFilm.getId()), newFilm);
        return films.put(newFilm.getId(), newFilm);
    }

    @Override
    public Film delete(Long id) {
        log.info("Удаление пользователя Film={}", films.get(id));
        return films.remove(id);
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
