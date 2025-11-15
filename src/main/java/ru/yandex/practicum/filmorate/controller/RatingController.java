package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

import java.util.Collection;

@RestController
@RequestMapping("/rating")
public class RatingController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public Collection<Rating> getRating() {
        return ratingService.getRating();
    }

    @PostMapping
    public Rating posRating(@Validated(OnCreate.class) @RequestBody Rating rating) {
        return ratingService.postRating(rating);
    }

    @PutMapping
    public Rating putRating(@Validated(OnUpdate.class) @RequestBody Rating newRating) {
        return ratingService.putRating(newRating);
    }

    @DeleteMapping("/{ratingId}")
    public Rating deleteRating(@PathVariable("ratingId") Long ratingId) {
        return ratingService.deleteRating(ratingId);
    }
}
