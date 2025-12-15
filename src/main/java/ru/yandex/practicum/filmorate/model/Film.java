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
    @NotNull(groups = OnUpdate.class, message = "Id должен быть указан при обновлении")
    private Long id;
    private final Set<Long> whoLikes = new HashSet<>();
    private Set<Director> directors = new HashSet<>();
    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "Жанр не может быть пустым")
    private Set<Genre> genres = new HashSet<>();
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
    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "Рейтинг не может быть пустым")
    private Rating mpa;

    public boolean addLike(@NotNull @Positive Long id) {
        return whoLikes.add(id);
    }

    public boolean deleteLike(@NotNull @Positive Long id) {
        return whoLikes.remove(id);
    }

    public boolean addGenre(@NotNull @Positive Genre genre) {
        return genres.add(genre);
    }

    public boolean deleteGenre(@NotNull @Positive Genre genre) {
        return genres.remove(genre);
    }
}

