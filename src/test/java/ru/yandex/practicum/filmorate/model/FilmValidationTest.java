package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTest {

    private static Validator validator;

    @BeforeAll
    public static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Валидация должна пройти при корректных данных")
    public void shouldValidateCorrectFilm() {
        Film film = new Film();
        film.setName("film");
        film.setDescription("Sample description for film");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(Duration.ofMinutes(120));
        film.setMpa(createSampleRating(1L, "ratingName"));
        film.getGenres().add(createSampleGenre(1L, "Drama"));

        Set<ConstraintViolation<Film>> violations = validator.validate(film, OnCreate.class);

        assertTrue(violations.isEmpty(), "Ожидалось отсутствие ошибок валидации");
    }

    @Test
    @DisplayName("Пустой фильм должен вызвать ошибки валидации")
    public void shouldFailOnEmptyFilm() {
        Film film = new Film();
        Set<ConstraintViolation<Film>> violations = validator.validate(film, OnCreate.class);

        assertFalse(violations.isEmpty());
        assertEquals(6, violations.size(), "Ожидалось 6 ошибок");
    }

    @Test
    @DisplayName("Описание длиннее 200 символов — ошибка")
    public void shouldFailIfDescriptionTooLong() {
        Film film = new Film();
        film.setName("Длинный фильм");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(Duration.ofMinutes(100));

        Set<ConstraintViolation<Film>> violations = validator.validate(film, OnCreate.class);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("200 символов")));
    }

    @Test
    @DisplayName("Дата релиза раньше 28.12.1895 — ошибка")
    public void shouldFailIfReleaseDateTooEarly() {
        Film film = new Film();
        film.setName("Исторический фильм");
        film.setDescription("О старом кино");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(Duration.ofMinutes(50));

        Set<ConstraintViolation<Film>> violations = validator.validate(film, OnCreate.class);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("1895")));
    }

    @Test
    @DisplayName("Нулевая или отрицательная продолжительность — ошибка")
    public void shouldFailIfDurationNotPositive() {
        Film film = new Film();
        film.setName("Короткий фильм");
        film.setDescription("Тест продолжительности");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(Duration.ZERO);

        Set<ConstraintViolation<Film>> violations = validator.validate(film, OnCreate.class);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Продолжительность фильма")));
    }

    private Film createSampleFilm(String name, long durationMinutes, Long ratingId, String ratingName) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Sample description for " + name);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(Duration.ofMinutes(durationMinutes));
        film.setMpa(createSampleRating(ratingId, ratingName));
        film.getGenres().add(createSampleGenre(1L, "Drama")); // Добавляем хотя бы один жанр
        return film;
    }

    private Rating createSampleRating(Long ratingId, String ratingName) {
        return Rating.builder()
                .id(ratingId)
                .name(ratingName)
                .build();
    }

    private Genre createSampleGenre(Long id, String name) {
        return Genre.builder()
                .id(id)
                .name(name)
                .build();
    }
}
