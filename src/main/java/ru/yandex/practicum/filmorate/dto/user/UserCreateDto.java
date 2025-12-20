package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

import java.time.LocalDate;

@Data
public class UserCreateDto {
    private String name;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "Электронная почта не может быть пустой")
    @Email(groups = {OnCreate.class, OnUpdate.class}, message = "Некорректный формат электронной почты")
    private String email;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "Логин не может быть пустым")
    @Pattern(groups = {OnCreate.class, OnUpdate.class}, regexp = "^\\S+$", message = "Логин не должен содержать пробелов")
    private String login;

    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "Дата рождения обязательна")
    @PastOrPresent(groups = {OnCreate.class, OnUpdate.class}, message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
