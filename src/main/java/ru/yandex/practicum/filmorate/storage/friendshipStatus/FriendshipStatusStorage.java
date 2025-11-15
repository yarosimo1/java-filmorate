package ru.yandex.practicum.filmorate.storage.friendshipStatus;

import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.Map;

public interface FriendshipStatusStorage {
    FriendshipStatus add(FriendshipStatus friendshipStatus);

    FriendshipStatus update(FriendshipStatus newFriendshipStatus);

    FriendshipStatus delete(Long id);

    Map<Long, FriendshipStatus> getFriendshipStatus();
}
