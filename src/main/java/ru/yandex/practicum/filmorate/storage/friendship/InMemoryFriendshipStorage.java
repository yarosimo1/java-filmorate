package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class InMemoryFriendshipStorage implements FriendshipStorage {
    Map<Long, Friendship> friendships = new HashMap<>();

    @Override
    public Friendship add(Friendship friendship) {
        log.info("Добавление дружбы friendship={}", friendship);
        friendship.setFriendshipId(getNextId());
        friendships.put(friendship.getFriendshipId(), friendship);
        return friendship;
    }

    @Override
    public Friendship update(Friendship newFriendship) {
        log.info("Обновление дружбы Friendship={}, на \n newFriendship={}",
                friendships.get(newFriendship.getFriendshipId()), newFriendship);
        return friendships.put(newFriendship.getFriendshipId(), newFriendship);
    }

    @Override
    public Friendship delete(Long id) {
        log.info("Удаление дружбы Friendship={}", friendships.get(id));
        return friendships.remove(id);
    }

    @Override
    public Map<Long, Friendship> getFriendship() {
        return friendships;
    }

    private long getNextId() {
        long currentMaxId = friendships.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
