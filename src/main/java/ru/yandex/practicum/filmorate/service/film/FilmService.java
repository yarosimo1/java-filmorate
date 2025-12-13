package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmDBStorage;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmUpdateDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetExceptions;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
@AllArgsConstructor
public class FilmService {
    private final FilmDBStorage filmDBStorage;

    private final GenreService genreService;
    private final RatingService ratingService;
    private final UserService userService;
    private final DirectorService directorService;

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

    public List<FilmDto> getCommonFilms(Long userId, Long friendId) {
        log.info("Получен запрос на получение общих фильмов для users {} и {}", userId, friendId);

        userService.getUserDBStorage().findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        userService.getUserDBStorage().findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + friendId + " не найден"));

        Map<Long, Film> films = filmDBStorage.getFilms();

        return films.values().stream()
                .filter(film -> film.getWhoLikes().contains(userId)
                        && film.getWhoLikes().contains(friendId))
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto createFilm(FilmCreateDto dto) {
        log.info("Создание фильма: {}", dto);

        //для проеврки на существовующий рейтинг и жанр
        ratingService.getRatingById(dto.getMpa().getId());
        checkGenre(dto.getGenres());

        Film film = FilmMapper.mapToFilm(dto);

        Film savedFilm = filmDBStorage.add(film);

        Film fullFilm = filmDBStorage.findById(savedFilm.getId())
                .orElseThrow(() -> new IllegalStateException("Ошибка при загрузке фильма после создания"));

        return FilmMapper.mapToFilmDto(fullFilm);
    }

    public FilmDto updateFilm(FilmUpdateDto dto) {
        log.info("Обновление фильма {}", dto);

        Film existingFilm = filmDBStorage.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + dto.getId() + " не найден"));

        FilmMapper.updateFilmFields(existingFilm, dto);

        filmDBStorage.update(existingFilm);

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

    public List<FilmDto> getDirectorFilms(long id, String sortBy) {
        return switch (sortBy) {
            case "year" -> getDirectorFilmsByYear(id);
            case "likes" -> getDirectorFilmsByLikes(id);
            default -> throw new ConditionsNotMetExceptions("Неверные параметры запроса");
        };
    }

    private List<FilmDto> getDirectorFilmsByYear(long id) {
        return getAllFilms().stream()
                .filter(film -> film.getDirectors().stream().anyMatch(d -> d.getId().equals(id)))
                .sorted(Comparator.comparingInt(a -> a.getReleaseDate().getYear()))
                .toList();
    }

    private List<FilmDto> getDirectorFilmsByLikes(long id) {
        return getAllFilms().stream()
                .filter(film -> film.getDirectors().stream().anyMatch(d -> d.getId().equals(id)))
                .sorted((a, b) -> b.getLikes().size() - a.getLikes().size())
                .toList();
    }

    private void validateIds(Long filmId, Long userId) {
        if (filmId == null || filmId <= 0 || userId == null || userId <= 0) {
            throw new ValidationException("Некорректный id");
        }
    }

    private void checkGenre(Set<GenreDto> genreDtos) {
        for (GenreDto genreDto : genreDtos) {
            genreService.getGenreById(genreDto.getId());
        }
    }
}