package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users = new HashMap<>();

    @Override
    public User add(User user) {
        log.info("Добавление ползователя User={}", user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        log.info("Обновление пользователя User={}, на \n newUser={}", users.get(newUser.getId()), newUser);
        return users.put(newUser.getId(), newUser);
    }

    @Override
    public User delete(Long id) {
        log.info("Удаление пользователя User={}", users.get(id));
        return users.remove(id);
    }

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }
}
