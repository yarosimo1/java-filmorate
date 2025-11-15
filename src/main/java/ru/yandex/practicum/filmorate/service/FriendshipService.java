package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.friendship.InMemoryFriendshipStorage;

import java.util.Collection;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class FriendshipService {
    private final FriendshipStorage inMemoryFriendshipStorage;

    public FriendshipService(InMemoryFriendshipStorage inMemoryFriendshipStorage) {
        this.inMemoryFriendshipStorage = inMemoryFriendshipStorage;
    }

    public Collection<Friendship> getFriendship() {
        log.info("Получен запрос на получение всех друзей {}", inMemoryFriendshipStorage.getFriendship().size());
        return inMemoryFriendshipStorage.getFriendship().values();
    }

    public Friendship postFriendship(Friendship friendship) {
        log.info("Получен запрос на добавление дружбы {}", friendship);

        Friendship createdFriendship = inMemoryFriendshipStorage.add(friendship);
        log.info("Добавлена новая дружба: friendshipId={}, userId={}, friendId={}, friendshipStatusId={}",
                friendship.getFriendshipId(),
                friendship.getUserId(),
                friendship.getFriendId(),
                friendship.getFriendshipStatusId());

        return createdFriendship;
    }

    public Friendship putFriendship(Friendship newFriendship) {
        log.info("Получен запрос на обновление дружбы с id={}", newFriendship.getFriendshipId());

        Friendship oldFriendship = inMemoryFriendshipStorage.getFriendship().get(newFriendship.getFriendshipId());

        if (oldFriendship == null) {
            log.warn("Ошибка обновления: дружба с id={} не найден", newFriendship.getFriendshipId());
            throw new NoSuchElementException("Статус с id=" + newFriendship.getFriendshipId() + " не найден");
        }

        oldFriendship.setUserId(newFriendship.getUserId());
        oldFriendship.setFriendId(newFriendship.getFriendId());
        oldFriendship.setFriendshipStatusId(newFriendship.getFriendshipStatusId());

        inMemoryFriendshipStorage.update(oldFriendship);
        log.info("Дружба с id={} успешно обновлёна", newFriendship.getFriendshipId());

        return oldFriendship;
    }

    public Friendship deleteFriendship(Long id) {
        log.info("Получен запрос на удаление дружбы с id={}", id);

        if (id == null || id == 0 || id < 0) {
            log.warn("Ошибка удаления: дружбы с id={} не найдена", id);
            throw new NoSuchElementException("Дружба с id=" + id + " не найдена");
        }

        Friendship deletedFriendship = inMemoryFriendshipStorage.delete(id);

        log.info("Дружба id={} успешно удалена", deletedFriendship.getFriendshipId());

        return deletedFriendship;
    }
}
