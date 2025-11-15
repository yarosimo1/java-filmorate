package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

@Data
public class Friendship {
    @NotNull(groups = OnUpdate.class, message = "Id должен быть указан при обновлении")
    private Long friendshipId;
    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "Id должен быть указан")
    private Long userId;
    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "Id должен быть указан")
    private Long friendId;
    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "Id должен быть указан")
    private Long friendshipStatusId;
}
