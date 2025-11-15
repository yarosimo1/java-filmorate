package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.PositiveDuration;
import ru.yandex.practicum.filmorate.annotation.ReleaseDateConstraint;
import ru.yandex.practicum.filmorate.json.DurationToSecondsSerializer;
import ru.yandex.practicum.filmorate.json.SecondsToDurationDeserializer;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {

    private final Set<Long> whoLikes = new HashSet<>();
    private final Set<Long> genre = new HashSet<>();
    @NotNull(groups = OnUpdate.class, message = "Id должен быть указан при обновлении")
    private Long id;
    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "Название не может быть пустым")
    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "Название не может быть пустым")
    private String name;
    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "Описание не может быть пустым")
    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "Описание не может быть пустым")
    @Size(groups = {OnCreate.class, OnUpdate.class}, max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;
    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "Дата релиза обязательна")
    @ReleaseDateConstraint(groups = {OnCreate.class, OnUpdate.class}, message = "Дата релиза — не раньше 28 декабря 1895 года")
    private LocalDate releaseDate;
    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "Продолжительность обязательна")
    @PositiveDuration(groups = {OnCreate.class, OnUpdate.class}, message = "Продолжительность фильма должна быть положительным числом")
    @JsonSerialize(using = DurationToSecondsSerializer.class)
    @JsonDeserialize(using = SecondsToDurationDeserializer.class)
    private Duration duration;
    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "Возрастной рейтинг фильма обязателен")
    private Long ratingId;

    public boolean addLike(@NotNull @Positive Long id) {
        return whoLikes.add(id);
    }

    public boolean deleteLike(@NotNull @Positive Long id) {
        return whoLikes.remove(id);
    }

    public boolean addGenre(@NotNull @Positive Long id) {
        return genre.add(id);
    }

    public boolean deleteGenre(@NotNull @Positive Long id) {
        return genre.remove(id);
    }
}

