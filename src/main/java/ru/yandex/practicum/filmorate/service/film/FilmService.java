package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmDBStorage;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmUpdateDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.rating.RatingDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
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

    private final GenreService genreService;
    private final RatingService ratingService;
    private final FilmGenresService filmGenresService;
    private final FilmLikesService filmLikesService;
    private final UserService userService;

    public List<FilmDto> getAllFilms() {
        log.info("Получен запрос на получение всех фильмов");
        return inMemoryFilmStorage.getFilms()
                .values()
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto getFilmById(Long id) {
        log.info("Получен запрос на получение фильма id={}", id);
        Film film = inMemoryFilmStorage.getFilms().get(id);

        if (film == null)
            throw new NoSuchElementException("Фильм не найден");

        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto createFilm(FilmCreateDto dto) {
        log.info("Создание фильма: {}", dto);

        RatingDto ratingDto = ratingService.getRatingById(dto.getMpa().getId());
        Rating rating = RatingMapper.mapToRating(ratingDto);

        Film film = FilmMapper.mapToFilm(dto);
        film.setMpa(rating);

        Film savedFilm = filmDBStorage.add(film);

        // Жанры
        Set<Genre> genres = resolveGenres(dto.getGenres());
        savedFilm.setGenres(genres);
        saveFilmGenres(savedFilm.getId(), genres);

        // Кэш
        inMemoryFilmStorage.add(savedFilm);

        return FilmMapper.mapToFilmDto(savedFilm);
    }

    // =================== UPDATE ===================

    public FilmDto updateFilm(FilmUpdateDto dto) {
        log.info("Обновление фильма {}", dto);

        Film existingFilm = inMemoryFilmStorage.getFilms().get(dto.getId());
        if (existingFilm == null)
            throw new NoSuchElementException("Фильм не найден");

        Rating rating = dto.getMpa() != null ?
                RatingMapper.mapToRating(ratingService.getRatingById(dto.getMpa().getId()))
                : existingFilm.getMpa();

        Set<Genre> genres = dto.getGenres() != null ?
                resolveGenres(dto.getGenres())
                : existingFilm.getGenres();

        FilmMapper.updateFilmFields(existingFilm, dto, rating, genres);

        filmDBStorage.update(existingFilm);

        filmGenresService.deleteGenresByFilmId(existingFilm.getId());
        saveFilmGenres(existingFilm.getId(), genres);

        // Кэш обновляем
        inMemoryFilmStorage.update(existingFilm);

        return FilmMapper.mapToFilmDto(existingFilm);
    }

    public FilmDto deleteFilm(Long id) {
        log.info("Удаление фильма id={}", id);

        Film deleted = filmDBStorage.delete(id);
        filmGenresService.deleteGenresByFilmId(id);

        // Убираем из кэша
        inMemoryFilmStorage.getFilms().remove(id);

        return FilmMapper.mapToFilmDto(deleted);
    }

    public FilmDto addLike(Long filmId, Long userId) {
        log.info("Добавление лайка filmId={}, userId={}", filmId, userId);

        validateIds(filmId, userId);

        userService.getUserDBStorage().findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        Film film = inMemoryFilmStorage.getFilms().get(filmId);
        if (film == null)
            throw new NoSuchElementException("Фильм не найден");

        film.addLike(userId);
        filmLikesService.create(filmId, userId);

        inMemoryFilmStorage.update(film);

        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto deleteLike(Long filmId, Long userId) {
        log.info("Удаление лайка filmId={}, userId={}", filmId, userId);

        validateIds(filmId, userId);

        userService.getUserDBStorage().findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        Film film = inMemoryFilmStorage.getFilms().get(filmId);
        if (film == null)
            throw new NoSuchElementException("Фильм не найден");

        film.deleteLike(userId);
        filmLikesService.deleteLikes(filmId, userId);

        inMemoryFilmStorage.update(film);

        return FilmMapper.mapToFilmDto(film);
    }

    public List<FilmDto> getPopularFilms(int count) {
        log.info("Получен запрос на популярные фильмы count={}", count);

        return inMemoryFilmStorage.getFilms()
                .values()
                .stream()
                .sorted((a, b) -> b.getWhoLikes().size() - a.getWhoLikes().size())
                .limit(count)
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    // =================== HELPERS ===================

    private void validateIds(Long filmId, Long userId) {
        if (filmId == null || filmId <= 0 || userId == null || userId <= 0) {
            throw new ValidationException("Некорректный id");
        }
    }

    private Set<Genre> resolveGenres(Set<GenreDto> genreDtos) {
        if (genreDtos == null || genreDtos.isEmpty()) return Collections.emptySet();

        return genreDtos.stream()
                .map(dto -> {
                    GenreDto loaded = genreService.getGenreById(dto.getId());
                    return Genre.builder()
                            .id(loaded.getId())
                            .name(loaded.getName())
                            .build();
                })
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void saveFilmGenres(Long filmId, Set<Genre> genres) {
        for (Genre g : genres) {
            filmGenresService.create(
                    FilmGenres.builder().filmId(filmId).genreId(g.getId()).build()
            );
        }
    }
}