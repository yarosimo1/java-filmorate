package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

import java.time.LocalDate;

@Data
public class User {

    @NotNull(groups = OnUpdate.class, message = "Id должен быть указан при обновлении")
    private Long id;

    @NotNull(groups = OnCreate.class, message = "Электронная почта обязательна")
    @NotBlank(groups = OnCreate.class, message = "Электронная почта не может быть пустой")
    @Email(groups = OnCreate.class, message = "Некорректный формат электронной почты")
    private String email;


    @NotNull(groups = OnCreate.class, message = "Логин обязателен")
    @NotBlank(groups = OnCreate.class, message = "Логин не может быть пустым")
    @Pattern(groups = OnCreate.class, regexp = "^\\S+$", message = "Логин не должен содержать пробелов")
    private String login;

    private String name;

    @NotNull(groups = OnCreate.class, message = "Дата рождения обязательна")
    @PastOrPresent(groups = OnCreate.class, message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}

