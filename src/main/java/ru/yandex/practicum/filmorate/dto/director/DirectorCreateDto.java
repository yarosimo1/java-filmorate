package ru.yandex.practicum.filmorate.dto.director;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

@Data
public class DirectorCreateDto {
    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "Имя режиссера не может быть пустым")
    private String name;
}
