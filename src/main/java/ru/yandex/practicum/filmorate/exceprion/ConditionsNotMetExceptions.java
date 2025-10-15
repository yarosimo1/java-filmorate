package ru.yandex.practicum.filmorate.exceprion;

public class ConditionsNotMetExceptions extends RuntimeException {
    public ConditionsNotMetExceptions(String message) {
        super(message);
    }
}
