package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Collection<Film> getFilms() {
        log.info("Получен запрос на получение всех фильмов {}", inMemoryFilmStorage.getFilms().size());
        return inMemoryFilmStorage.getFilms().values();
    }

    public Film postFilm(Film film) {
        log.info("Получен запрос на добавление фильма {}", film);

        Film createdFilm = inMemoryFilmStorage.add(film);
        log.info("Добавлен новый фильм: id={}, name={}, description={}, releasDate={}, duration={}",
                                                        createdFilm.getId(),
                                                        createdFilm.getName(),
                                                        createdFilm.getDescription(),
                                                        createdFilm.getReleaseDate(),
                                                        createdFilm.getDuration());

        return createdFilm;
    }

    public Film putFilm(Film newFilm) {
        log.info("Получен запрос на обновление фильма с id={}", newFilm.getId());

        Film oldFilm = inMemoryFilmStorage.getFilms().get(newFilm.getId());

        if (oldFilm == null) {
            log.warn("Ошибка обновления: фильма с id={} не найден", newFilm.getId());
            throw new NoSuchElementException("Фильм с id=" + newFilm.getId() + " не найден");
        }

        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());

        inMemoryFilmStorage.update(oldFilm);
        log.info("Фильм id={} успешно обновлён", newFilm.getId());

        return oldFilm;
    }

    public Film deleteFilm(Long id) {
        log.info("Получен запрос на удаление фильма с id={}", id);

        if (id == null || id == 0 || id < 0) {
            log.warn("Ошибка удаления: фильма с id={} не найден", id);
            return null;
        }

        Film deletedFilm = inMemoryFilmStorage.delete(id);

        log.info("Фильм id={} успешно удален", deletedFilm.getId());

        return deletedFilm;
    }

    public Film addLike(Long filmId,
                           Long userId) {
        log.info("Получен запрос на добавления лайка для фильм с id={} от пользователя с id={}",filmId, userId);

        if ((userId == null || userId < 0) || (filmId == null || filmId < 0) ) {
            log.warn("Ошибка добавления лайка: неверно указан id");
            throw new ValidationException("Ошибка валидации id лайков");
        }

        if (!inMemoryUserStorage.getUsers().containsKey(userId) ||
                !inMemoryFilmStorage.getFilms().containsKey(filmId)) {
            log.warn("Ошибка добавления лайков: пользователем userId={} фильму filmId={}",userId, filmId);
            throw new NoSuchElementException("Лайк не может быть добавлен");
        }

        inMemoryFilmStorage.getFilms().get(filmId).addLike(userId);

        log.info("Фильму id={} успешно добавлен лайк от пользователя с id={}",filmId, userId);

        return inMemoryFilmStorage.getFilms().get(filmId);
    }

    public Film deleteLike(Long filmId,
                              Long userId) {
        log.info("Получен запрос на удаление лайка для фильм с id={} от пользователя с id={}",filmId, userId);

        if ((userId == null || userId < 0) || (filmId == null || filmId < 0) ) {
            log.warn("Ошибка удаления лайка: неверно указан id");
            throw new ValidationException("Ошибка валидации id лайков");
        }

        if (!inMemoryUserStorage.getUsers().containsKey(userId) ||
                !inMemoryFilmStorage.getFilms().containsKey(filmId)) {
            log.warn("Ошибка удаления лайков: пользователем userId={} фильму filmId={}",userId, filmId);
            throw new NoSuchElementException("Лайк не может быть удален");
        }

        inMemoryFilmStorage.getFilms().get(filmId).deleteLike(userId);

        log.info("У фильма успешно удален лайк id={} от пользователя с id={}",filmId, userId);

        return inMemoryFilmStorage.getFilms().get(filmId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получен запрос на получение популярных фильмов");

        int limit = (count == 0) ? 10 : count;

        return inMemoryFilmStorage.getFilms().values()
                    .stream()
                    .sorted((f1, f2) -> Integer.compare(f2.getWhoLikes().size(), f1.getWhoLikes().size()))
                    .limit(limit)
                    .toList();
    }
}