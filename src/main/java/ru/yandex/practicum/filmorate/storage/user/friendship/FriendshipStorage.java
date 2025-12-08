package ru.yandex.practicum.filmorate.storage.user.friendship;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Map;

public interface FriendshipStorage {
    Friendship add(Friendship friendship);

    Friendship update(Friendship newfriendship);

    Friendship delete(Long id);

    Map<Long, Friendship> getFriendship();
}
