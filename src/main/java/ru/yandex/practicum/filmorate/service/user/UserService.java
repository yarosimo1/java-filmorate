package ru.yandex.practicum.filmorate.service.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserDBStorage;
import ru.yandex.practicum.filmorate.dto.user.UserCreateDto;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.dto.user.UserUpdateDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Collection<UserDto> getUsers() {
        return userDBStorage.findAll().stream().map(UserMapper::mapToDto).collect(Collectors.toList());
    }

    public UserDto getUserById(long userId) {
        User user = userDBStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));
        return UserMapper.mapToDto(user);
    }

    public UserDto postUser(UserCreateDto dto) {
        log.info("Добавление пользователя DTO={}", dto);

        userDBStorage.findByEmail(dto.getEmail()).ifPresent(u -> {
            throw new DuplicatedDataException("Данный имейл уже используется");
        });

        User user = UserMapper.mapToUser(dto);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        User createdDbUser = userDBStorage.add(user);
        User createdUser = inMemoryUserStorage.add(createdDbUser);

        log.info("Пользователь добавлен: id={}", createdUser.getId());
        return UserMapper.mapToDto(createdUser);
    }

    public UserDto putUser(UserUpdateDto dto) {
        log.info("Обновление пользователя id={}", dto.getId());

        User existing = userDBStorage.findById(dto.getId())
                .orElseThrow(() ->  new NotFoundException("Пользователь не найден")
                );

        UserMapper.updateUserFields(existing, dto);

        User updatedDbUser = userDBStorage.update(existing);
        inMemoryUserStorage.update(updatedDbUser);

        return UserMapper.mapToDto(updatedDbUser);
    }

    public UserDto deleteUser(Long id) {
        if (id == null || id < 0) {
            throw new ValidationException("Некорректный id пользователя");
        }

        User deleted = userDBStorage.delete(id);
        inMemoryUserStorage.delete(id);

        return UserMapper.mapToDto(deleted);
    }

    public List<UserDto> addFriend(Long userId, Long friendId) {
        validateUserIds(userId, friendId);

        User user = inMemoryUserStorage.getUsers().get(userId);
        User friend = inMemoryUserStorage.getUsers().get(friendId);

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

        return List.of(UserMapper.mapToDto(user), UserMapper.mapToDto(friend));
    }

    public UserDto deleteFriend(Long userId, Long friendId) {
        validateUserIds(userId, friendId);

        inMemoryUserStorage.getUsers().get(userId).deleteFriend(friendId);
        friendshipService.deleteFriendship(userId, friendId);

        return UserMapper.mapToDto(inMemoryUserStorage.getUsers().get(userId));
    }

    public List<UserDto> getFriends(Long userId) {
        if (!inMemoryUserStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        User user = inMemoryUserStorage.getUsers().get(userId);

        return user.getFriendships().stream().map(id -> inMemoryUserStorage.getUsers().get(id)).filter(Objects::nonNull).map(UserMapper::mapToDto).collect(Collectors.toList());
    }

    public List<UserDto> getCommonFriends(Long userId, Long otherId) {
        User user = inMemoryUserStorage.getUsers().get(userId);
        User other = inMemoryUserStorage.getUsers().get(otherId);

        return user.getFriendships().stream().filter(other.getFriendships()::contains).map(id -> inMemoryUserStorage.getUsers().get(id)).filter(Objects::nonNull).map(UserMapper::mapToDto).collect(Collectors.toList());
    }

    private void validateUserIds(Long userId, Long otherId) {
        if (userId == null || otherId == null || userId <= 0 || otherId <= 0 || userId.equals(otherId)) {
            throw new ValidationException("Некорректные id пользователя");
        }
        if (!inMemoryUserStorage.getUsers().containsKey(userId) || !inMemoryUserStorage.getUsers().containsKey(otherId)) {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}
