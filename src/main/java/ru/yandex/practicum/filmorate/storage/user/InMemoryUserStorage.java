package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Data
@AllArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    Map<Long, User> users = new HashMap<>();

    @Override
    public User add(User user) {
        log.info("Добавление ползователя User={}", user);
        user.setId(getNextId());
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

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
