package ru.yandex.practicum.filmorate.dto.rating;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

@Data
public class RatingCreateDto {
    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "Название жанра не может быть пустым")
    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "Название жанра не может быть пустым")
    private String name;
}
