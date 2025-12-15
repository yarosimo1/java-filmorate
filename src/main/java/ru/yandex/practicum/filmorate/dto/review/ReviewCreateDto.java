package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;

@Data
public class ReviewCreateDto {
    @NotBlank(groups = OnCreate.class, message = "Содержание отзыва не может быть пустым")
    @Size(groups = OnCreate.class, max = 500)
    private String content;
    @NotNull(groups = OnCreate.class, message = "Тип отзыва обязателен")
    private Boolean isPositive;
    @NotNull(groups = OnCreate.class, message = "UserId должен быть указан")
    private Long userId;
    @NotNull(groups = OnCreate.class, message = "FilmId должен быть указан")
    private Long filmId;
}
