package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmDBStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenres;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService {
    @Qualifier("InMemoryFilmStorage")
    private final FilmStorage inMemoryFilmStorage;
    @Qualifier("FilmDBStorage")
    private final FilmDBStorage filmDBStorage;
    private final UserService userService;
    private final GenreService genreService;
    private final FilmGenresService filmGenresService;
    private final FilmLikesService filmLikesService;
    private final RatingService ratingService;

    public Collection<Film> getFilms() {
        log.info("Получен запрос на получение всех фильмов");
        return inMemoryFilmStorage.getFilms().values();
    }

    public Film getFilmById(Long id) {
        log.info("Получен запрос на получение всех фильмов");
        return inMemoryFilmStorage.getFilms().get(id);
    }

    public Film postFilm(Film film) {
        log.info("Получен запрос на добавление фильма {}", film);

        Rating rating = ratingService.getRatingById(film.getMpa().getId()).orElseThrow(() -> {
            return new NoSuchElementException("Не существует указанного рейтинга");
        });

        Film savedFilm = filmDBStorage.add(film);

        log.info("Фильм добавлен {}", savedFilm);

        Set<Genre> resolvedGenres = resolvedGenres(film);
        savedFilm.setGenres(resolvedGenres);

        setGnresFilms(resolvedGenres, savedFilm);

        savedFilm.setMpa(rating);

        log.info("Добавлен новый фильм: id={}", savedFilm.getId());

        inMemoryFilmStorage.add(savedFilm);
        return savedFilm;
    }

    private Set<Genre> resolvedGenres(Film film) {
        return film.getGenres().stream().map(g -> genreService.getGenreById(g.getId())).sorted(Comparator.comparing(Genre::getId)).collect(Collectors.toCollection(LinkedHashSet::new)); // сохраняет порядок!
    }

    private void setGnresFilms(Set<Genre> genres, Film film) {
        for (Genre genre : genres) {
            FilmGenres fg = FilmGenres.builder().filmId(film.getId()).genreId(genre.getId()).build();

            filmGenresService.create(fg);
        }
    }

    public Film putFilm(Film newFilm) {
        log.info("Получен запрос на обновление фильма с id={}", newFilm.getId());

        Film oldFilm = inMemoryFilmStorage.getFilms().get(newFilm.getId());

        // Обновляем поля
        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());
        oldFilm.setMpa(ratingService.getRatingById(newFilm.getMpa().getId()).orElseThrow(() -> {
            return new NoSuchElementException("Указанного рейтинга не существует");
        }));

        Set<Genre> resolvedGenres = resolvedGenres(newFilm);
        oldFilm.setGenres(resolvedGenres);

        // 1. Обновляем запись фильма
        filmDBStorage.update(oldFilm);

        // 2. Удаляем старые жанры
        filmGenresService.deleteGenresByFilmId(oldFilm.getId());

        // 3. Добавляем новые жанры
        if (oldFilm.getGenres() != null) {
            setGnresFilms(oldFilm.getGenres(), oldFilm);
        }

        inMemoryFilmStorage.update(oldFilm);
        log.info("Фильм id={} и его жанры успешно обновлены", oldFilm.getId());

        return oldFilm;
    }

    public Film deleteFilm(Long id) {
        log.info("Получен запрос на удаление фильма с id={}", id);

        if (id == null || id == 0 || id < 0) {
            log.warn("Ошибка удаления: фильма с id={} не найден", id);
            throw new NoSuchElementException("Фильм с id=" + id + " не найден");
        }

        Film deletedFilm = filmDBStorage.delete(id);

        log.info("Фильм id={} успешно удален", deletedFilm.getId());

        return deletedFilm;
    }

    public Film addLike(Long filmId, Long userId) {
        log.info("Получен запрос на добавления лайка для фильма id={} от пользователя id={}", filmId, userId);

        // Валидация id
        if (userId == null || userId < 0 || filmId == null || filmId < 0) {
            log.warn("Ошибка добавления лайка: неверно указан filmId={} или userId={}", filmId, userId);
            throw new ValidationException("Ошибка валидации id лайков");
        }

        userService.getUserDBStorage().findById(userId).orElseThrow(() -> {
            log.warn("Ошибка добавления лайка: пользователь userId={} не найден", userId);
            return new NoSuchElementException("Пользователь не найден");
        });

        filmDBStorage.findById(filmId).orElseThrow(() -> {
            log.warn("Ошибка добавления лайка: фильм filmId={} не найден", filmId);
            return new NoSuchElementException("Фильм не найден");
        });

        Film film = inMemoryFilmStorage.getFilms().get(filmId);
        film.addLike(userId);
        filmLikesService.create(filmId, userId);
        inMemoryFilmStorage.update(film);

        log.info("Фильму id={} успешно добавлен лайк от пользователя id={}", filmId, userId);

        return film;
    }

    public Film deleteLike(Long filmId, Long userId) {
        log.info("Получен запрос на удаление лайка для фильма id={} от пользователя id={}", filmId, userId);

        if (userId == null || userId < 0 || filmId == null || filmId < 0) {
            log.warn("Ошибка удаления лайка: неверно указан filmId={} или userId={}", filmId, userId);
            throw new ValidationException("Ошибка валидации id лайков");
        }

        userService.getUserDBStorage().findById(userId).orElseThrow(() -> {
            log.warn("Ошибка удаления лайка: пользователь userId={} не найден", userId);
            return new NoSuchElementException("Пользователь не найден");
        });

        filmDBStorage.findById(filmId).orElseThrow(() -> {
            log.warn("Ошибка удаления лайка: фильм filmId={} не найден", filmId);
            return new NoSuchElementException("Фильм не найден");
        });

        Film film = filmDBStorage.findById(filmId).get();
        film.deleteLike(userId);
        filmLikesService.deleteLikes(filmId, userId);

        log.info("У фильма id={} успешно удален лайк от пользователя id={}", filmId, userId);

        return film;
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получен запрос на получение популярных фильмов");

        return inMemoryFilmStorage.getFilms().values().stream().sorted((f1, f2) -> Integer.compare(f2.getWhoLikes().size(), f1.getWhoLikes().size())).limit(count).toList();
    }
}