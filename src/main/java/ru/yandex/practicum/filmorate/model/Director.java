package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

@Data
@Builder
public class Director {
    @NotNull(groups = OnUpdate.class, message = "Id должен быть указан при обновлении")
    private Long id;
    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "Имя не может быть пустым")
    private String name;
}
