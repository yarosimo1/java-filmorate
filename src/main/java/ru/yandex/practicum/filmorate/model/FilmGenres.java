package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

@Data
@Builder
public class FilmGenres {
    @NotNull(groups = {OnUpdate.class, OnCreate.class}, message = "Id должен быть указан")
    private Long genreId;
    @NotNull(groups = {OnUpdate.class, OnCreate.class}, message = "Id должен быть указан")
    private Long filmId;
}
