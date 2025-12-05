package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.storage.user.friendship.friendshipStatus.FriendshipStatusStorage;
import ru.yandex.practicum.filmorate.storage.user.friendship.friendshipStatus.InMemoryFriendshipStatusStorage;

import java.util.Collection;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class FriendshipStatusService {
    private final FriendshipStatusStorage inMemoryFriendshipStatusStorage;

    public FriendshipStatusService(InMemoryFriendshipStatusStorage inMemoryFriendshipStatusStorage) {
        this.inMemoryFriendshipStatusStorage = inMemoryFriendshipStatusStorage;
    }

    public Collection<FriendshipStatus> getFrinedshipStatus() {
        log.info("Получен запрос на получение всех статусов дружбы {}", inMemoryFriendshipStatusStorage.getFriendshipStatus().size());
        return inMemoryFriendshipStatusStorage.getFriendshipStatus().values();
    }

    public FriendshipStatus postFriendshipStatus(FriendshipStatus friendshipStatus) {
        log.info("Получен запрос на добавление статуса дружбы {}", friendshipStatus);

        FriendshipStatus createdFriendshipStatus = inMemoryFriendshipStatusStorage.add(friendshipStatus);
        log.info("Добавлен новый статус дружбы: id={}, name={}, isAccepted={}", friendshipStatus.getId(), friendshipStatus.getName(), friendshipStatus.getIsAccepted());

        return createdFriendshipStatus;
    }

    public FriendshipStatus putFriendshipStatus(FriendshipStatus newFriendshipStatus) {
        log.info("Получен запрос на обновление статуса дружбы с id={}", newFriendshipStatus.getId());

        FriendshipStatus oldFriendshipStatus = inMemoryFriendshipStatusStorage.getFriendshipStatus().get(newFriendshipStatus.getId());

        if (oldFriendshipStatus == null) {
            log.warn("Ошибка обновления: статуса дружбы с id={} не найден", newFriendshipStatus.getId());
            throw new NoSuchElementException("Статус с id=" + newFriendshipStatus.getId() + " не найден");
        }

        oldFriendshipStatus.setName(newFriendshipStatus.getName());
        oldFriendshipStatus.setIsAccepted(newFriendshipStatus.getIsAccepted());

        inMemoryFriendshipStatusStorage.update(oldFriendshipStatus);
        log.info("Статус с id={} успешно обновлён", newFriendshipStatus.getId());

        return oldFriendshipStatus;
    }

    public FriendshipStatus deleteFriendshipStatus(Long id) {
        log.info("Получен запрос на удаление статуса с id={}", id);

        if (id == null || id == 0 || id < 0) {
            log.warn("Ошибка удаления: статуса с id={} не найден", id);
            throw new NoSuchElementException("Статус с id=" + id + " не найден");
        }

        FriendshipStatus deletedFriendshipStatus = inMemoryFriendshipStatusStorage.delete(id);

        log.info("Статус id={} успешно удален", deletedFriendshipStatus.getId());

        return deletedFriendshipStatus;
    }
}
