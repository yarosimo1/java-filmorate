package ru.yandex.practicum.filmorate.service.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.user.UserDBStorage;
import ru.yandex.practicum.filmorate.dto.event.EventDto;
import ru.yandex.practicum.filmorate.dto.user.UserCreateDto;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.dto.user.UserUpdateDto;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.enums.Operation;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.model.UserMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Getter
@AllArgsConstructor
public class UserService {
    private final UserDBStorage userDBStorage;
    private final FriendshipService friendshipService;
    private final EventService eventService;

    public Collection<UserDto> getUsers() {
        return userDBStorage.findAll().stream()
                .map(UserMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(long userId) {
        User user = userDBStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));

        return UserMapper.mapToDto(user);
    }

    public List<EventDto> getEvents(long userId) {
        return eventService.getEventsByIdUser(userId);
    }

    public UserDto postUser(UserCreateDto dto) {
        log.info("Добавление пользователя DTO={}", dto);

        userDBStorage.findByEmail(dto.getEmail()).ifPresent(u -> {
            throw new DuplicatedDataException("Этот email уже занят");
        });

        User user = UserMapper.mapToUser(dto);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        User created = userDBStorage.add(user);
        return UserMapper.mapToDto(created);
    }

    public UserDto putUser(UserUpdateDto dto) {
        log.info("Обновление пользователя id={}", dto.getId());

        User existing = userDBStorage.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        UserMapper.updateUserFields(existing, dto);

        User updated = userDBStorage.update(existing);
        return UserMapper.mapToDto(updated);
    }

    public UserDto deleteUser(Long id) {
        if (id == null || id < 0) {
            throw new ValidationException("Некорректный ID");
        }

        User deleted = userDBStorage.delete(id);
        return UserMapper.mapToDto(deleted);
    }

    public List<UserDto> addFriend(Long userId, Long friendId) {

        validateUserIds(userId, friendId);

        friendshipService.createFriendship(Friendship.builder()
                .userId(userId)
                .friendId(friendId)
                .friendshipStatusId(2L).build());

        User user = userDBStorage.findById(userId).get();
        User friend = userDBStorage.findById(friendId).get();

        eventService.addEventToUser(
                Event.builder()
                        .timestamp(Instant.now().toEpochMilli())
                        .userId(userId)
                        .eventType(EventType.FRIEND)
                        .operation(Operation.ADD)
                        .entityId(friendId)
                        .build()
        );

        return List.of(UserMapper.mapToDto(user), UserMapper.mapToDto(friend));
    }

    public UserDto deleteFriend(Long userId, Long friendId) {

        validateUserIds(userId, friendId);

        friendshipService.deleteFriendship(userId, friendId);

        User user = userDBStorage.findById(userId).get();

        eventService.addEventToUser(
                Event.builder()
                        .timestamp(Instant.now().toEpochMilli())
                        .userId(userId)
                        .eventType(EventType.FRIEND)
                        .operation(Operation.REMOVE)
                        .entityId(friendId)
                        .build()
        );
        return UserMapper.mapToDto(user);
    }

    public List<UserDto> getFriends(Long userId) {

        User user = userDBStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Set<Long> friendIds = user.getFriendships();

        return friendIds.stream().map(id ->
                        userDBStorage.findById(id)
                                .orElse(null))
                .filter(Objects::nonNull)
                .map(UserMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getCommonFriends(Long userId, Long otherId) {
        User user = userDBStorage.getUsers().get(userId);
        User other = userDBStorage.getUsers().get(otherId);

        return user.getFriendships().stream()
                .filter(other.getFriendships()::contains)
                .map(id -> userDBStorage.getUsers().get(id))
                .filter(Objects::nonNull).map(UserMapper::mapToDto)
                .collect(Collectors.toList());
    }

    private void validateUserIds(Long userId, Long otherId) {
        if (userId == null || otherId == null || Objects.equals(userId, otherId)) {
            throw new ValidationException("Некорректные ID");
        }

        if (userDBStorage.findById(userId).isEmpty()
                || userDBStorage.findById(otherId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}