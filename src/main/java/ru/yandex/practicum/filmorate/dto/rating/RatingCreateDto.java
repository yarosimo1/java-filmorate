package ru.yandex.practicum.filmorate.dto.rating;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RatingCreateDto {
    @NotBlank
    private String name;
}
