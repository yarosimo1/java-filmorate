package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

@Data
@Builder
public class Friendship {
    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "Id должен быть указан")
    private Long userId;
    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "Id должен быть указан")
    private Long friendId;
    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "Id должен быть указан")
    private Long friendshipStatusId;
}
