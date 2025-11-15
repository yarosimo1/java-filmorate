package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public Collection<Genre> getGenre() {
        return genreService.getGenre();
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
