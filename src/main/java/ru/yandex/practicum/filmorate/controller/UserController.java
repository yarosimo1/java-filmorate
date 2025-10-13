package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceprion.NotFoundException;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Получен запрос на получение всех пользователей {}", users.size());
        return users.values();
    }

    @PostMapping
    public User postUser(@Validated(OnCreate.class) @RequestBody User user) {
        log.info("Получен запрос на добавление пользователя с User={}", user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя не указано, подставлен логин '{}'", user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: id={}, email={}, login={}",
                user.getId(), user.getEmail(), user.getLogin());

        return user;
    }

    @PutMapping
    public User putUser(@Validated(OnUpdate.class) @RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя с id={}", newUser.getId());

        User oldUser = users.get(newUser.getId());
        if (oldUser == null) {
            log.warn("Ошибка обновления: пользователь с id={} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            oldUser.setEmail(newUser.getEmail());
        }

        if (newUser.getLogin() != null && !newUser.getLogin().isBlank()) {
            oldUser.setLogin(newUser.getLogin());
        }

        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            oldUser.setName(newUser.getName());
        }

        if (newUser.getBirthday() != null) {
            oldUser.setBirthday(newUser.getBirthday());
        }

        log.info("Пользователь id={} успешно обновлён", newUser.getId());

        return oldUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}