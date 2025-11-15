package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage inMemoryFilmStorage;
    private final UserService userService;
    private final GenreService genreService;

    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, UserService userService, GenreService genreService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.userService = userService;
        this.genreService = genreService;
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
            throw new NoSuchElementException("Фильм с id=" + id + " не найден");
        }

        Film deletedFilm = inMemoryFilmStorage.delete(id);

        log.info("Фильм id={} успешно удален", deletedFilm.getId());

        return deletedFilm;
    }

    public Film addLike(Long filmId, Long userId) {
        log.info("Получен запрос на добавления лайка для фильм с id={} от пользователя с id={}", filmId, userId);

        if ((userId == null || userId < 0) || (filmId == null || filmId < 0)) {
            log.warn("Ошибка добавления лайка: неверно указан id");
            throw new ValidationException("Ошибка валидации id лайков");
        }

        if (!userService.getInMemoryUserStorage().getUsers().containsKey(userId) || !inMemoryFilmStorage.getFilms().containsKey(filmId)) {
            log.warn("Ошибка добавления лайков: пользователем userId={} фильму filmId={}", userId, filmId);
            throw new NoSuchElementException("Лайк не может быть добавлен");
        }

        inMemoryFilmStorage.getFilms().get(filmId).addLike(userId);

        log.info("Фильму id={} успешно добавлен лайк от пользователя с id={}", filmId, userId);

        return inMemoryFilmStorage.getFilms().get(filmId);
    }

    public Film deleteLike(Long filmId, Long userId) {
        log.info("Получен запрос на удаление лайка для фильм с id={} от пользователя с id={}", filmId, userId);

        if ((userId == null || userId < 0) || (filmId == null || filmId < 0)) {
            log.warn("Ошибка удаления лайка: неверно указан id");
            throw new ValidationException("Ошибка валидации id лайков");
        }

        if (!userService.getInMemoryUserStorage().getUsers().containsKey(userId) || !inMemoryFilmStorage.getFilms().containsKey(filmId)) {
            log.warn("Ошибка удаления лайков: пользователем userId={} фильму filmId={}", userId, filmId);
            throw new NoSuchElementException("Лайк не может быть удален");
        }

        inMemoryFilmStorage.getFilms().get(filmId).deleteLike(userId);

        log.info("У фильма успешно удален лайк id={} от пользователя с id={}", filmId, userId);

        return inMemoryFilmStorage.getFilms().get(filmId);
    }

    public Film addGenre(Long filmId, Long genreId) {
        log.info("Получен запрос на добавления жанра с id={} для фильма с id={} ", filmId, genreId);

        if ((genreId == null || genreId < 0) || (filmId == null || filmId < 0)) {
            log.warn("Ошибка добавления жанра: неверно указан id");
            throw new ValidationException("Ошибка валидации id жанров");
        }

        if (!genreService.getInMemoryGenreStorage().getGenre().containsKey(genreId) || !inMemoryFilmStorage.getFilms().containsKey(filmId)) {
            log.warn("Ошибка добавления жанра: жанр genreId={} фильму filmId={}", genreId, filmId);
            throw new NoSuchElementException("Лайк не может быть добавлен");
        }

        inMemoryFilmStorage.getFilms().get(filmId).addGenre(genreId);

        log.info("Фильму id={} успешно добавлен жанр id={}", filmId, genreId);

        return inMemoryFilmStorage.getFilms().get(filmId);
    }

    public Film deleteGenre(Long filmId, Long genreId) {
        log.info("Получен запрос на удаление жанра с id={} для фильма с id={}", genreId, filmId);

        if ((genreId == null || genreId < 0) || (filmId == null || filmId < 0)) {
            log.warn("Ошибка удаления жанра: неверно указан id");
            throw new ValidationException("Ошибка валидации id жанра");
        }

        if (!genreService.getInMemoryGenreStorage().getGenre().containsKey(genreId) || !inMemoryFilmStorage.getFilms().containsKey(filmId)) {
            log.warn("Ошибка удаления жанров: жанра genreId={} у фильма filmId={}", genreId, filmId);
            throw new NoSuchElementException("Жанр не может быть удален");
        }

        inMemoryFilmStorage.getFilms().get(filmId).deleteGenre(genreId);

        log.info("У фильма с id={} успешно удален жанр с id={}", filmId, genreId);

        return inMemoryFilmStorage.getFilms().get(filmId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получен запрос на получение популярных фильмов");

        return inMemoryFilmStorage.getFilms().values().stream().sorted((f1, f2) -> Integer.compare(f2.getWhoLikes().size(), f1.getWhoLikes().size())).limit(count).toList();
    }
}