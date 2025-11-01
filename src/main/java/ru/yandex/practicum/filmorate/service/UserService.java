package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@Service
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Collection<User> getUsers() {
        log.info("Получен запрос на получение всех пользователей {}", inMemoryUserStorage.getUsers().size());
        return inMemoryUserStorage.getUsers().values();
    }

    public User postUser(User user) {
        log.info("Получен запрос на добавление пользователя User={}", user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя не указано, подставлен логин '{}'", user.getLogin());
        }

        User createdUser = inMemoryUserStorage.add(user);

        if (createdUser == null) {
            log.error("Ошибка при добавлении пользователя — хранилище вернуло null");
            throw new IllegalStateException("Ошибка добавления пользователя");
        }

        log.info("Добавлен новый пользователь: id={}, email={}, login={}",
                                                        createdUser.getId(),
                                                        createdUser.getEmail(),
                                                        createdUser.getLogin());

        return createdUser;
    }

    public User putUser(User newUser) {
        log.info("Получен запрос на обновление пользователя с id={}", newUser.getId());

        User oldUser = inMemoryUserStorage.getUsers().get(newUser.getId());

        if (oldUser == null) {
            log.warn("Ошибка обновления: пользователь с id={} не найден", newUser.getId());
            throw new NoSuchElementException("Пользователь с id=" + newUser.getId() + " не найден");
        }
        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(newUser.getName());
        oldUser.setBirthday(newUser.getBirthday());

        inMemoryUserStorage.update(oldUser);
        log.info("Пользователь id={} успешно обновлён", newUser.getId());

        return oldUser;
    }

    public User deleteUser(Long id) {
        log.info("Получен запрос на удаление пользователя с id={}", id);

        if (id == null || id < 0) {
            log.warn("Ошибка удаления: неверно указан id");
            throw new ValidationException("Ошибка валидации id");
        }

        if (!inMemoryUserStorage.getUsers().containsKey(id)) {
            log.warn("Ошибка удаления: пользователя с id={} не найден", id);
            throw new NoSuchElementException("Пользователь не найден");
        }

        User deletedUser = inMemoryUserStorage.delete(id);

        log.info("Пользователь id={} успешно удален", deletedUser.getId());

        return deletedUser;
    }

    public User addFriend(Long userId,
                             Long friendId) {
        log.info("Получен запрос на добавление в друзья пользователя friendId={}",friendId);

        if ((userId == null || userId < 0) || (friendId == null || friendId < 0) ) {
            log.warn("Ошибка добавления в друзья: неверно указан id");
            throw new ValidationException("Ошибка валидации id пользователей");
        }

        if (!inMemoryUserStorage.getUsers().containsKey(userId) ||
                !inMemoryUserStorage.getUsers().containsKey(friendId)) {
            log.warn("Ошибка добавления в друзья: пользователем userId={} друга friendId={}",userId, friendId);
            throw new NoSuchElementException("Друг не может быть добавлен — пользователь не найден");
        }

        inMemoryUserStorage.getUsers().get(userId).addFriend(friendId);
        inMemoryUserStorage.getUsers().get(friendId).addFriend(userId);

        log.info("Друг добавлен friendId={} пользователем userId={}",friendId, userId);

        return inMemoryUserStorage.getUsers().get(userId);
    }

    public User deleteFriend(Long userId,
                                Long friendId) {
        log.info("Получен запрос на удаление из друзей пользователя friendId={}",friendId);

        if ((userId == null || userId < 0) || (friendId == null || friendId < 0) ) {
            log.warn("Ошибка уделения из друзей: неверно указан id");
            throw new ValidationException("Ошибка валидации пользователей");
        }

        if (!inMemoryUserStorage.getUsers().containsKey(userId) ||
                !inMemoryUserStorage.getUsers().containsKey(friendId)) {
            log.warn("Ошибка удаления из друзей: пользователем userId={} друга friendId={}",userId, friendId);
            throw new NoSuchElementException("Друг не может быть удален");
        }

        inMemoryUserStorage.getUsers().get(userId).deleteFriend(friendId);
        inMemoryUserStorage.getUsers().get(friendId).deleteFriend(userId);

        log.info("Друг удален friendId={} пользователем userId={}",friendId, userId);

        return inMemoryUserStorage.getUsers().get(userId);
    }

    public List<User> getFriends(Long userId) {
        log.info("Получен запрос на получение списка друзей");

        if (userId == null || userId < 0) {
            log.warn("Ошибка получения: неверно указан id");
            throw new ValidationException("Ошибка валидации id");
        }

        if (!inMemoryUserStorage.getUsers().containsKey(userId)) {
            log.warn("Ошибка получения: пользователя с id={} не найден", userId);
            throw new NoSuchElementException("Пользователь не найден");
        }

        User user = inMemoryUserStorage.getUsers().get(userId);

        return user.getFriends().stream()
                .map(id -> inMemoryUserStorage.getUsers().get(id))
                .filter(Objects::nonNull)
                .toList();
    }

    public List<User> getCommonFriends(Long userId,
                                       Long otherId) {
        log.info("Получен запрос на получение списка общих друзей");

        List<User> mutualFriends = inMemoryUserStorage.getUsers().get(userId)
                .getFriends()
                .stream()
                .filter(inMemoryUserStorage.getUsers().get(otherId).getFriends()::contains)
                .map(id -> inMemoryUserStorage.getUsers().get(id))
                .toList();

        return mutualFriends;
    }
 }
