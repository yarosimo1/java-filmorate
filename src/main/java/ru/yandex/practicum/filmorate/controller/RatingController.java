package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.film.RatingService;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

import java.util.Collection;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/mpa")
@AllArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @GetMapping
    public Collection<Rating> getRatings() {
        return ratingService.getRatings();
    }

    @GetMapping("/{id}")
    public Rating getRatingByID(@PathVariable("id") Long id) {
        return ratingService.getRatingById(id).orElseThrow(() -> new NoSuchElementException("Указанного рейтинга не существует"));
    }

    @PostMapping
    public Rating postRating(@Validated(OnCreate.class) @RequestBody Rating rating) {
        return ratingService.postRating(rating);
    }

    @PutMapping
    public Rating putRating(@Validated(OnUpdate.class) @RequestBody Rating newRating) {
        return ratingService.putRating(newRating);
    }

    @DeleteMapping("/{id}")
    public Rating deleteRating(@PathVariable("id") Long id) {
        return ratingService.deleteRating(id);
    }
}
