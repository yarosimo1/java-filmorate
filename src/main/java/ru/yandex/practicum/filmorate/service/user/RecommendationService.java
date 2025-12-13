package ru.yandex.practicum.filmorate.service.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RecommendationService {
    private final FilmService filmService;
    private final UserService userService;

    public List<FilmDto> getRecommendations(Long userId) {
        if (userService.getUserById(userId) == null) {
            return List.of(); // возвращаем пустой список
        }

        List<FilmDto> films = filmService.getAllFilms();
        // 1. Сбор лайков по пользователям
        Map<Long, Set<Long>> likesByUser = buildLikesMap(films);

        // 2. Находим пользователя с максимальным пересечением
        Long similarUserId = findMostSimilarUser(userId, likesByUser);

        if (similarUserId == null) {
            return List.of();
        }

        // 3. Формируем список фильмов, которые лайкнул похожий пользователь, а текущий — нет
        Set<Long> recommendedIds = new HashSet<>(likesByUser.get(similarUserId));
        recommendedIds.removeAll(likesByUser.getOrDefault(userId, Set.of()));

        return films.stream()
                .filter(f -> recommendedIds.contains(f.getId()))
                .collect(Collectors.toList());
    }

    private Map<Long, Set<Long>> buildLikesMap(Collection<FilmDto> films) {
        Map<Long, Set<Long>> likesByUser = new HashMap<>();

        for (FilmDto film : films) {
            for (Long userId : film.getLikes()) {
                likesByUser
                        .computeIfAbsent(userId, k -> new HashSet<>())
                        .add(film.getId());
            }
        }

        return likesByUser;
    }

    private Long findMostSimilarUser(Long targetUserId, Map<Long, Set<Long>> likesByUser) {
        Set<Long> targetLikes = likesByUser.getOrDefault(targetUserId, Set.of());

        Long bestUser = null;
        int maxIntersection = -1;

        for (Map.Entry<Long, Set<Long>> entry : likesByUser.entrySet()) {
            Long otherUserId = entry.getKey();
            if (otherUserId.equals(targetUserId)) continue;

            Set<Long> otherLikes = entry.getValue();
            Set<Long> intersection = new HashSet<>(targetLikes);
            intersection.retainAll(otherLikes);

            if (!intersection.isEmpty() && intersection.size() > maxIntersection) {
                maxIntersection = intersection.size();
                bestUser = otherUserId;
            }
        }

        return bestUser;
    }

}
