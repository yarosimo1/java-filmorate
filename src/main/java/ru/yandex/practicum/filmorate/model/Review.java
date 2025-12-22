package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

import java.util.Map;

@Data
@Builder
public class Review {
    // рейтинг полезности отзыва
    private Integer useful;
    // userId - true=like, false=dislike
    private Map<Long, Boolean> reactions;

    @NotNull(groups = OnUpdate.class, message = "Id должен быть указан при обновлении")
    private Long reviewId;

    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "Содержание отзыва не может быть пустым")
    @Size(groups = {OnCreate.class, OnUpdate.class}, max = 500)
    private String content;

    @NotNull(groups = OnCreate.class, message = "Тип отзыва обязателен")
    private Boolean isPositive;

    @NotNull(groups = OnCreate.class, message = "UserId должен быть указан при создании")
    private Long userId;

    @NotNull(groups = OnCreate.class, message = "FilmId должен быть указан при создании")
    private Long filmId;
}
