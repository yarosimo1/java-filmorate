package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.rating.RatingCreateDto;
import ru.yandex.practicum.filmorate.dto.rating.RatingDto;
import ru.yandex.practicum.filmorate.dto.rating.RatingUpdateDto;
import ru.yandex.practicum.filmorate.service.film.RatingService;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@AllArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping
    public Collection<RatingDto> getRatings() {
        return ratingService.getRatings();
    }

    @GetMapping("/{id}")
    public RatingDto getRatingById(@PathVariable("id") Long id) {
        return ratingService.getRatingById(id);
    }

    @PostMapping
    public RatingDto postRating(@Validated(OnCreate.class) @RequestBody RatingCreateDto dto) {
        return ratingService.postRating(dto);
    }

    @PutMapping
    public RatingDto putRating(@Validated(OnUpdate.class) @RequestBody RatingUpdateDto dto) {
        return ratingService.putRating(dto);
    }

    @DeleteMapping("/{id}")
    public RatingDto deleteRating(@PathVariable("id") Long id) {
        return ratingService.deleteRating(id);
    }
}
