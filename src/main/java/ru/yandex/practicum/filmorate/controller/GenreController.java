package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.genre.GenreCreateDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreUpdateDto;
import ru.yandex.practicum.filmorate.service.film.GenreService;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public Collection<GenreDto> getGenres() {
        return genreService.getGenres();
    }

    @GetMapping("/{genreId}")
    public GenreDto getGenreById(@PathVariable Long genreId) {
        return genreService.getGenreById(genreId);
    }

    @PostMapping
    public GenreDto postGenre(@Validated(OnCreate.class) @RequestBody GenreCreateDto dto) {
        return genreService.postGenre(dto);
    }

    @PutMapping
    public GenreDto putGenre(@Validated(OnUpdate.class) @RequestBody GenreUpdateDto dto) {
        return genreService.putGenre(dto);
    }

    @DeleteMapping("/{genreId}")
    public GenreDto deleteGenre(@PathVariable Long genreId) {
        return genreService.deleteGenre(genreId);
    }
}
