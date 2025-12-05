package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

@Data
public class FilmLikes {
    @NotNull(groups = {OnUpdate.class, OnCreate.class}, message = "Id должен быть указан")
    private Long userId;
    @NotNull(groups = {OnUpdate.class, OnCreate.class}, message = "Id должен быть указан")
    private Long filmId;
}
