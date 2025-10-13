package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceprion.NotFoundException;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private Map<Long, Film> films = new HashMap<>();
    private static final Instant MIN_DATE_RELEAS_FILM = LocalDate.of(1895, 12, 28)
                                                                    .atStartOfDay()
                                                                    .toInstant(ZoneOffset.UTC);

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Получен запрос на получение всех фильмов {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film postFilm(@Validated(OnCreate.class) @RequestBody Film film) {
        log.info("Получен запрос на добавление фильма {}", film);

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: id={}, name={}, description={}, releasDate={}, duration={}",
                film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());

        return film;
    }

    @PutMapping
    public Film putFilm(@Validated(OnUpdate.class) @RequestBody Film newFilm) {
        log.info("Получен запрос на обновление пользователя с id={}", newFilm.getId());

        Film oldFilm = films.get(newFilm.getId());
        if (oldFilm == null) {
            log.warn("Ошибка обновления: фильма с id={} не найден", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        if (newFilm.getName() != null && !newFilm.getName().isBlank()) {
            oldFilm.setName(newFilm.getName());
        }

        if (newFilm.getDescription() != null && !newFilm.getDescription().isBlank()) {
            oldFilm.setDescription(newFilm.getDescription());
        }

        if (newFilm.getReleaseDate() != null) {
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }

        if (newFilm.getDuration() != null && !newFilm.getDuration().isZero()) {
            oldFilm.setDuration(newFilm.getDuration());
        }

        log.info("Пользователь id={} успешно обновлён", newFilm.getId());

        return oldFilm;
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
