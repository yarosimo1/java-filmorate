package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.service.user.FriendshipService;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

import java.util.Collection;

@RestController
@RequestMapping("/frinedship")
public class FriendshipController {
    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @GetMapping
    public Collection<Friendship> getFriendship() {
        return friendshipService.getFriendship();
    }

    @PostMapping
    public Friendship postFriendship(@Validated(OnCreate.class) @RequestBody Friendship friendship) {
        return friendshipService.createFriendship(friendship);
    }

    @PutMapping
    public Friendship putFriendship(@Validated(OnUpdate.class) @RequestBody Friendship newFriendship) {
        return friendshipService.updateFriendship(newFriendship);
    }
}
