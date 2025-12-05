package ru.yandex.practicum.filmorate.dto.genre;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GenreUpdateDto {
    @NotNull
    private Long id;

    @NotBlank
    private String name;
}
