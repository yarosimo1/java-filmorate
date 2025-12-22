package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

@Data
public class ReviewUpdateDto {
    @NotNull(groups = OnUpdate.class, message = "Id должен быть указан при обновлении")
    private Long reviewId;

    @NotBlank(groups = OnUpdate.class, message = "Содержание отзыва не может быть пустым")
    @Size(groups = OnUpdate.class, max = 500)
    private String content;

    @NotNull(groups = OnUpdate.class, message = "Тип отзыва обязателен")
    private Boolean isPositive;
}
