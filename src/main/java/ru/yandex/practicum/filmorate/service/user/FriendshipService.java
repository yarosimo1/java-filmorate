package ru.yandex.practicum.filmorate.service.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FriendshipDBStorage;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Collection;
import java.util.NoSuchElementException;

@Slf4j
@Service
@AllArgsConstructor
@Getter
public class FriendshipService {
    FriendshipDBStorage friendshipDBStorage;

    public Collection<Friendship> getFriendship() {
        log.info("Получен запрос на получение всех дружб");
        return friendshipDBStorage.findAll();
    }

    public Friendship getFriendshipById(Long userId, Long friendId) {
        return friendshipDBStorage.findById(userId, friendId).orElseThrow(() -> new NoSuchElementException("Дружбы не существует"));
    }

    public Friendship createFriendship(Friendship friendship) {
        log.info("Получен запрос на добавление дружбы {}", friendship);
        friendshipDBStorage.add(friendship);
        log.info("Добавлена новая дружба: userId={}, friendId={}, friendshipStatusId={}", friendship.getUserId(), friendship.getFriendId(), friendship.getFriendshipStatusId());
        return friendship;
    }

    public Friendship updateFriendship(Friendship newFriendship) {
        log.info("Получен запрос на обновление дружбы {}", newFriendship);

        Friendship oldFriendship = friendshipDBStorage.findById(newFriendship.getUserId(), newFriendship.getFriendId()).orElseThrow(() -> new NoSuchElementException("Дружба не найдена"));

        oldFriendship.setFriendshipStatusId(newFriendship.getFriendshipStatusId());
        friendshipDBStorage.update(oldFriendship);

        log.info("Дружба успешно обновлена: {}", oldFriendship);
        return oldFriendship;
    }

    public void deleteFriendship(Long userId, Long friendId) {
        // проверяем, есть ли запись дружбы
        if (friendshipDBStorage.findById(userId, friendId).isPresent()) {
            friendshipDBStorage.delete(friendId);
            log.info("Дружба userId={} → friendId={} удалена", userId, friendId);
        } else {
            log.warn("Попытка удалить несуществующую дружбу userId={} → friendId={}", userId, friendId);
        }
    }
}
