package ru.yandex.practicum.filmorate.exception;

public class ConditionsNotMetExceptions extends RuntimeException {
    public ConditionsNotMetExceptions(String message) {
        super(message);
    }
}
