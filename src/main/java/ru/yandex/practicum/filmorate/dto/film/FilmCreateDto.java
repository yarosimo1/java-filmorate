package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.json.DurationToSecondsSerializer;
import ru.yandex.practicum.filmorate.json.SecondsToDurationDeserializer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

@Data
public class FilmCreateDto {
    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 200)
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @NotNull
    @Positive
    @JsonSerialize(using = DurationToSecondsSerializer.class)
    @JsonDeserialize(using = SecondsToDurationDeserializer.class)
    private Duration duration;

    @NotNull
    private Long mpaId;

    private Set<Long> genreIds;
}
