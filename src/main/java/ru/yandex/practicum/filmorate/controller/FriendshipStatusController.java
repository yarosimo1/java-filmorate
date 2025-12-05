package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.service.user.FriendshipStatusService;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

import java.util.Collection;

@RestController
@RequestMapping("/frinedshipStatus")
public class FriendshipStatusController {
    private final FriendshipStatusService friendshipStatusService;

    public FriendshipStatusController(FriendshipStatusService friendshipStatusService) {
        this.friendshipStatusService = friendshipStatusService;
    }

    @GetMapping
    public Collection<FriendshipStatus> getFriendshipStatus() {
        return friendshipStatusService.getFrinedshipStatus();
    }

    @PostMapping
    public FriendshipStatus postFriendshipStatus(@Validated(OnCreate.class) @RequestBody FriendshipStatus friendshipStatus) {
        return friendshipStatusService.postFriendshipStatus(friendshipStatus);
    }

    @PutMapping
    public FriendshipStatus putFriendshipStatus(@Validated(OnUpdate.class) @RequestBody FriendshipStatus newFriendshipStatus) {
        return friendshipStatusService.putFriendshipStatus(newFriendshipStatus);
    }

    @DeleteMapping("/{frinedshipStatusId}")
    public FriendshipStatus deleteFriendshipStatus(@PathVariable("frinedshipStatusId") Long frinedshipStatusId) {
        return friendshipStatusService.deleteFriendshipStatus(frinedshipStatusId);
    }
}
