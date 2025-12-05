package ru.yandex.practicum.filmorate.dto.rating;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RatingUpdateDto {
    @NotNull
    private Long id;

    @NotBlank
    private String name;
}