package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    User add(User user);

    User update(User newUser);

    User delete(Long id);

    Map<Long, User> getUsers();
}
