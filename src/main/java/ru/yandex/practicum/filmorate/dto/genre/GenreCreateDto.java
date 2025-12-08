package ru.yandex.practicum.filmorate.dto.genre;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

@Data
public class GenreCreateDto {
    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "Название жанра не может быть пустым")
    private String name;
}
