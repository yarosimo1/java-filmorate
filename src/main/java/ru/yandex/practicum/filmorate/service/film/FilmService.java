package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmDBStorage;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmUpdateDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.rating.RatingDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@AllArgsConstructor
public class FilmService {
    private final FilmDBStorage filmDBStorage;

    private final GenreService genreService;
    private final RatingService ratingService;
    private final UserService userService;

    public List<FilmDto> getAllFilms() {
        log.info("Получен запрос на получение всех фильмов");
        return filmDBStorage.findAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public FilmDto getFilmById(Long id) {
        log.info("Получен запрос на получение фильма id={}", id);
        Film film = filmDBStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto createFilm(FilmCreateDto dto) {
        log.info("Создание фильма: {}", dto);

        RatingDto ratingDto = ratingService.getRatingById(dto.getMpa().getId());
        Rating rating = RatingMapper.mapToRating(ratingDto);

        Film film = FilmMapper.mapToFilm(dto);
        film.setMpa(rating);

        Film savedFilm = filmDBStorage.add(film);

        Set<Genre> genres = resolveGenres(dto.getGenres());
        filmDBStorage.updateFilmGenres(savedFilm.getId(), genres);

        Film fullFilm = filmDBStorage.findById(savedFilm.getId())
                .orElseThrow(() -> new IllegalStateException("Ошибка при загрузке фильма после создания"));

        return FilmMapper.mapToFilmDto(fullFilm);
    }

    public FilmDto updateFilm(FilmUpdateDto dto) {
        log.info("Обновление фильма {}", dto);

        Film existingFilm = filmDBStorage.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + dto.getId() + " не найден"));

        Rating rating = dto.getMpa() != null
                ? RatingMapper.mapToRating(ratingService.getRatingById(dto.getMpa().getId()))
                : existingFilm.getMpa();

        Set<Genre> genres = dto.getGenres() != null
                ? resolveGenres(dto.getGenres())
                : existingFilm.getGenres();

        FilmMapper.updateFilmFields(existingFilm, dto, rating, genres);

        filmDBStorage.update(existingFilm);

        filmDBStorage.updateFilmGenres(existingFilm.getId(), genres);

        return FilmMapper.mapToFilmDto(existingFilm);
    }


    public FilmDto deleteFilm(Long id) {
        log.info("Удаление фильма id={}", id);

        Film deleted = filmDBStorage.delete(id);

        return FilmMapper.mapToFilmDto(deleted);
    }

    public FilmDto addLike(Long filmId, Long userId) {
        log.info("Добавление лайка filmId={}, userId={}", filmId, userId);

        validateIds(filmId, userId);

        userService.getUserDBStorage().findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Film film = filmDBStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        filmDBStorage.addLike(filmId, userId);

        film.addLike(userId);

        return FilmMapper.mapToFilmDto(film);
    }


    public FilmDto deleteLike(Long filmId, Long userId) {
        log.info("Удаление лайка filmId={}, userId={}", filmId, userId);

        validateIds(filmId, userId);

        userService.getUserDBStorage().findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Film film = filmDBStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));

        filmDBStorage.deleteLike(filmId, userId);

        film.deleteLike(userId);

        return FilmMapper.mapToFilmDto(film);
    }

    public List<FilmDto> getPopularFilms(int count, Long genreId, Integer year) {
        log.info("Получен запрос на популярные фильмы count={}", count);

        return filmDBStorage.getFilms()
                .values()
                .stream()
                .filter(film -> genreId == null || film.getGenres().stream().anyMatch(g -> g.getId().equals(genreId)))
                .filter(film -> year == null || film.getReleaseDate().getYear() == year)
                .sorted((a, b) -> b.getWhoLikes().size() - a.getWhoLikes().size())
                .limit(count)
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

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
}