package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

@Data
public class FilmDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;
    private Long mpaId;
    private Set<Long> genreIds;
    private Set<Long> whoLikes;
}
