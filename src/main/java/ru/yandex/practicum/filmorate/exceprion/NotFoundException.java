package ru.yandex.practicum.filmorate.exceprion;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
