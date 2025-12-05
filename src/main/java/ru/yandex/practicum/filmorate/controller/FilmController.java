package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
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
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilms(@PathVariable("id") Long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    @PostMapping
    public Film postFilm(@Validated(OnCreate.class) @RequestBody Film film) {
        return filmService.postFilm(film);
    }

    @PutMapping
    public Film putFilm(@Validated(OnUpdate.class) @RequestBody Film newFilm) {
        return filmService.putFilm(newFilm);
    }

    @DeleteMapping("/{filmId}")
    public Film deleteFilm(@PathVariable("filmId") Long filmId) {
        return filmService.deleteFilm(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable("filmId") Long filmId, @PathVariable("userId") Long userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film deleteLike(@PathVariable("filmId") Long filmId, @PathVariable("userId") Long userId) {
        return filmService.deleteLike(filmId, userId);
    }
}
