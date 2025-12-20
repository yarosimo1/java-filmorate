package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.event.EventDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.user.UserCreateDto;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.dto.user.UserUpdateDto;
import ru.yandex.practicum.filmorate.service.user.RecommendationService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final RecommendationService recommendationService;

    @GetMapping
    public Collection<UserDto> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public UserDto postUser(@Validated(OnCreate.class) @RequestBody UserCreateDto userDto) {
        return userService.postUser(userDto);
    }

    @PutMapping
    public UserDto putUser(@Validated(OnUpdate.class) @RequestBody UserUpdateDto updateDto) {
        return userService.putUser(updateDto);
    }

    @DeleteMapping("/{userId}")
    public UserDto deleteUser(@PathVariable("userId") Long id) {
        return userService.deleteUser(id);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public List<UserDto> addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        return userService.addFriend(userId, friendId);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") Long userId) {
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public UserDto deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        return userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<UserDto> getUserFriends(@PathVariable Long userId) {
        return userService.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(@PathVariable Long userId, @PathVariable Long otherId) {
        return userService.getCommonFriends(userId, otherId);
    }

    @GetMapping("{userId}/recommendations")
    public List<FilmDto> getRecommendations(@PathVariable Long userId) {
        return recommendationService.getRecommendations(userId);
    }

    @GetMapping("/{userId}/feed")
    public List<EventDto> getEvents(@PathVariable Long userId) {
        return userService.getEvents(userId);
    }
}