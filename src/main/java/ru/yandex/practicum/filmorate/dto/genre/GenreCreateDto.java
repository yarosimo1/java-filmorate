package ru.yandex.practicum.filmorate.dto.genre;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenreCreateDto {
    @NotBlank
    private String name;
}
