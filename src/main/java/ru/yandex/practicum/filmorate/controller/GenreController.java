package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.film.GenreService;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@AllArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> getGenres() {
        return genreService.getGenres();
    }

    @GetMapping("/{generId}")
    public Genre getGenreById(@PathVariable("generId") Long genreId) {
        return genreService.getGenreById(genreId);
    }

    @PostMapping
    public Genre postGenre(@Validated(OnCreate.class) @RequestBody Genre genre) {
        return genreService.postGenre(genre);
    }

    @PutMapping
    public Genre putGenre(@Validated(OnUpdate.class) @RequestBody Genre newGenre) {
        return genreService.putGenre(newGenre);
    }

    @DeleteMapping("/{genreId}")
    public Genre deleteGenre(@PathVariable("genreId") Long genreId) {
        return genreService.deleteGenre(genreId);
    }
}
