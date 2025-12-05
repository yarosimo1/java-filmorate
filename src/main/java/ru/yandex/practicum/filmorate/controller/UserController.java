package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public User postUser(@Validated(OnCreate.class) @RequestBody User user) {
        return userService.postUser(user);
    }

    @PutMapping
    public User putUser(@Validated(OnUpdate.class) @RequestBody User newUser) {
        return userService.putUser(newUser);
    }

    @DeleteMapping("/{userId}")
    public User deleteUser(@PathVariable("userId") Long id) {
        return userService.deleteUser(id);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public List<User> addFriend(@PathVariable("userId") Long userId, @PathVariable("friendId") Long friendId) {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User deleteFriend(@PathVariable("userId") Long userId, @PathVariable("friendId") Long friendId) {
        return userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getUserFriends(@PathVariable("userId") Long userId) {
        return userService.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("userId") Long userid, @PathVariable("otherId") Long otherId) {
        return userService.getCommonFriends(userid, otherId);
    }
}