package ru.yandex.practicum.filmorate.service.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserDBStorage;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
@Getter
@AllArgsConstructor
public class UserService {
    @Qualifier("InMemoryUserStorage")
    private final UserStorage inMemoryUserStorage;
    @Qualifier("userDBStorage")
    private final UserDBStorage userDBStorage;
    private final FriendshipService friendshipService;

    public Collection<User> getUsers() {
        log.info("Получен запрос на получение всех пользователей {}");
        return inMemoryUserStorage.getUsers().values();
    }

    public User getUserById(long userId) {
        return userDBStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));
    }

    public User postUser(User user) {
        log.info("Получен запрос на добавление пользователя User={}", user);

        Optional<User> alreadyExistUser = userDBStorage.findByEmail(user.getEmail());

        if (alreadyExistUser.isPresent()) {
            throw new DuplicatedDataException("Данный имейл уже используется");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя не указано, подставлен логин '{}'", user.getLogin());
        }

        User createdUser = inMemoryUserStorage.add(userDBStorage.add(user));

        if (createdUser == null) {
            log.error("Ошибка при добавлении пользователя — хранилище вернуло null");
            throw new IllegalStateException("Ошибка добавления пользователя");
        }

        log.info("Добавлен новый пользователь: id={}, email={}, login={}", createdUser.getId(), createdUser.getEmail(), createdUser.getLogin());

        return createdUser;
    }

    public User putUser(User newUser) {
        log.info("Получен запрос на обновление пользователя с id={}", newUser.getId());

        User oldUser = userDBStorage.findById(newUser.getId()).orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(newUser.getName());
        oldUser.setBirthday(newUser.getBirthday());

        inMemoryUserStorage.update(userDBStorage.update(oldUser));
        log.info("Пользователь id={} успешно обновлён", newUser.getId());

        return oldUser;
    }

    public User deleteUser(Long id) {
        log.info("Получен запрос на удаление пользователя с id={}", id);

        if (id == null || id < 0) {
            log.warn("Ошибка удаления: неверно указан id");
            throw new ValidationException("Ошибка валидации id");
        }

        User deletedUser = userDBStorage.delete(id);

        log.info("Пользователь id={} успешно удален", id);

        return deletedUser;
    }

    public List<User> addFriend(Long userId, Long friendId) {

        if (userId == null || friendId == null || userId <= 0 || friendId <= 0 || userId.equals(friendId)) {
            throw new ValidationException("Ошибка валидации id или попытка добавить себя в друзья");
        }

        User user = inMemoryUserStorage.getUsers().get(userId);
        User friend = inMemoryUserStorage.getUsers().get(friendId);

        if (user == null || friend == null) {
            throw new NoSuchElementException("Пользователь не найден");
        }

        Optional<Friendship> existing = friendshipService.getFriendship().stream().filter(f -> (f.getUserId().equals(userId) && f.getFriendId().equals(friendId)) || (f.getUserId().equals(friendId) && f.getFriendId().equals(userId))).findFirst();

        if (existing.isPresent()) {
            Friendship f = existing.get();
            if (f.getUserId().equals(userId)) {
                throw new ValidationException("Эти пользователи уже дружат");
            } else {
                f.setFriendshipStatusId(2L);
                friendshipService.updateFriendship(f);
            }
        } else {
            friendshipService.createFriendship(Friendship.builder().userId(userId).friendId(friendId).friendshipStatusId(1L).build());
        }

        user.addFriend(friendId);

        return List.of(user, friend);
    }


    public User deleteFriend(Long userId, Long friendId) {

        if (userId == null || friendId == null || userId <= 0 || friendId <= 0) {
            throw new ValidationException("Ошибка валидации id пользователей");
        }

        if (!inMemoryUserStorage.getUsers().containsKey(userId) || !inMemoryUserStorage.getUsers().containsKey(friendId)) {
            throw new NoSuchElementException("Пользователь не найден");
        }

        inMemoryUserStorage.getUsers().get(userId).deleteFriend(friendId);

        friendshipService.deleteFriendship(userId, friendId);

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

        return user.getFriendships().stream().map(id -> inMemoryUserStorage.getUsers().get(id)).filter(Objects::nonNull).toList();
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        log.info("Получен запрос на получение списка общих друзей");

        List<User> mutualFriends = inMemoryUserStorage.getUsers().get(userId).getFriendships().stream().filter(inMemoryUserStorage.getUsers().get(otherId).getFriendships()::contains).map(id -> inMemoryUserStorage.getUsers().get(id)).toList();

        return mutualFriends;
    }
}
