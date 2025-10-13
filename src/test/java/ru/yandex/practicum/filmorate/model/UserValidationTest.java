package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Корректный пользователь проходит валидацию")
    void shouldValidateCorrectUser() {
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("login");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user, OnCreate.class);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Пустой пользователь — ошибки по всем обязательным полям")
    void shouldFailOnEmptyUser() {
        User user = new User();
        Set<ConstraintViolation<User>> violations = validator.validate(user, OnCreate.class);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Электронная почта")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Логин")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Дата рождения")));
    }

    @Test
    @DisplayName("Email без @ — ошибка")
    void shouldFailIfEmailInvalid() {
        User user = new User();
        user.setEmail("invalid.email");
        user.setLogin("user");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user, OnCreate.class);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("формат электронной почты")));
    }

    @Test
    @DisplayName("Логин с пробелами — ошибка")
    void shouldFailIfLoginContainsSpaces() {
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("user name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user, OnCreate.class);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("пробелов")));
    }

    @Test
    @DisplayName("Дата рождения в будущем — ошибка")
    void shouldFailIfBirthdayInFuture() {
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("user");
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user, OnCreate.class);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("в будущем")));
    }
}
