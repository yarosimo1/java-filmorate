package ru.yandex.practicum.filmorate.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

@Data
@Builder
public class EventDto {
    @NotNull
    Long timestamp;
    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "id пользователя не может быть пустой")
    private Long userId;
    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "Тип события не может быть пустой")
    private EventType eventType;
    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "Операция не может быть пустой")
    private Operation operation;
    @NotNull(groups = OnCreate.class, message = "id не может быть пустой")
    private Long eventId;
    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "id сущности не может быть пустой")
    private Long entityId;
}
