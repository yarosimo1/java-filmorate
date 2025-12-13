package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmCreateDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmUpdateDto;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<FilmDto> getFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public FilmDto getFilms(@PathVariable("id") Long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") int count,
                                         @RequestParam(required = false) Long genreId,
                                         @RequestParam(required = false) Integer year) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDto> getDirectorFilms(@PathVariable long id,
            @RequestParam(required = false) String sortBy) {
        return filmService.getDirectorFilms(id, sortBy);
    }

    @PostMapping
    public FilmDto postFilm(@Validated(OnCreate.class) @RequestBody FilmCreateDto film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public FilmDto putFilm(@Validated(OnUpdate.class) @RequestBody FilmUpdateDto newFilm) {
        return filmService.updateFilm(newFilm);
    }

    @DeleteMapping("/{filmId}")
    public FilmDto deleteFilm(@PathVariable("filmId") Long filmId) {
        return filmService.deleteFilm(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public FilmDto addLike(@PathVariable("filmId") Long filmId, @PathVariable("userId") Long userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public FilmDto deleteLike(@PathVariable("filmId") Long filmId, @PathVariable("userId") Long userId) {
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/common")
    public List<FilmDto> getCommonFilms(@RequestParam Long userId,
                                        @RequestParam Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}