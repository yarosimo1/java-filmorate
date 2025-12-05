package ru.yandex.practicum.filmorate.storage.user.friendship.friendshipStatus;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class InMemoryFriendshipStatusStorage implements FriendshipStatusStorage {
    private Map<Long, FriendshipStatus> status = new HashMap<>();

    @Override
    public FriendshipStatus add(FriendshipStatus friendshipStatus) {
        log.info("Добавление статуса дружбы FriendshipStatus={}", friendshipStatus);
        status.put(friendshipStatus.getId(), friendshipStatus);
        return friendshipStatus;
    }

    @Override
    public FriendshipStatus update(FriendshipStatus newFriendshipStatus) {
        log.info("Обновление статуса дружбы FriendshipStatus={}, на \n newFriendshipStatus={}",
                status.get(newFriendshipStatus.getId()), newFriendshipStatus);
        return status.put(newFriendshipStatus.getId(), newFriendshipStatus);
    }

    @Override
    public FriendshipStatus delete(Long id) {
        log.info("Удаление статуса дружбы FriendshipStatus={}", status.get(id));
        return status.remove(id);
    }

    @Override
    public Map<Long, FriendshipStatus> getFriendshipStatus() {
        return status;
    }
}
