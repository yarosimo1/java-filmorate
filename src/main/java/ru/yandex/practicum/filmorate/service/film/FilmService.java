package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.film.FilmDBStorage;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmUpdateDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.model.FilmMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.user.EventService;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.time.Instant;
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
    private final SearchService searchService;
    private final EventService eventService;

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

        eventService.addEventToUser(
                Event.builder()
                        .timestamp(Instant.now().toEpochMilli())
                        .userId(userId)
                        .eventType(EventType.LIKE)
                        .operation(Operation.ADD)
                        .entityId(filmId)
                        .build()
        );

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

        eventService.addEventToUser(
                Event.builder()
                        .timestamp(Instant.now().toEpochMilli())
                        .userId(userId)
                        .eventType(EventType.LIKE)
                        .operation(Operation.REMOVE)
                        .entityId(filmId)
                        .build()
        );

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

    public List<FilmDto> getDirectorFilmsSorted(long id, String sortBy) {
        DirectorDto directorDto = directorService.findById(id);
        return switch (sortBy) {
            case "year" -> sortFilmsByYear(getDirectorFilmsRaw(directorDto.getId()));
            case "likes" -> sortFilmsByLikes(getDirectorFilmsRaw(directorDto.getId()));
            default -> throw new ConditionsNotMetException("Неверные параметры запроса");
        };
    }

    public List<FilmDto> getFilmsBySearch(String query, String by) {
        if (query == null && by == null)
            return sortFilmsByLikes(getAllFilms());

        List<FilmDto> queriedFilms = searchService.getFilmsByQuery(query, by).stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();

        return sortFilmsByLikes(queriedFilms);
    }

    private List<FilmDto> sortFilmsByYear(List<FilmDto> films) {
        return films.stream()
                .sorted(Comparator.comparingInt(f -> f.getReleaseDate().getYear()))
                .toList();
    }

    private List<FilmDto> sortFilmsByLikes(List<FilmDto> films) {
        return films.stream()
                .sorted((a, b) -> b.getLikes().size() - a.getLikes().size())
                .toList();
    }

    private List<FilmDto> getDirectorFilmsRaw(long id) {
        return getAllFilms().stream()
                .filter(film -> film.getDirectors().stream().anyMatch(d -> d.getId().equals(id)))
                .toList();
    }

    private void validateIds(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            throw new ValidationException("Некорректный id");
        }
    }

    private void checkGenre(Set<GenreDto> genreDtos) {
        for (GenreDto genreDto : genreDtos) {
            genreService.getGenreById(genreDto.getId());
        }
    }
}
