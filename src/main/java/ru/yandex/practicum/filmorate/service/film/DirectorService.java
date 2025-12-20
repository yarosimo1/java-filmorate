package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.director.DirectorCreateDto;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.DirectorUpdateDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.model.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.DirectorDBStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorDBStorage directorDBStorage;

    public List<DirectorDto> findAll() {
        return directorDBStorage.findAll().stream()
                .map(DirectorMapper::mapToDto)
                .toList();
    }

    public DirectorDto findById(Long id) {
        Director director = directorDBStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Такого режиссера нет"));
        return DirectorMapper.mapToDto(director);
    }

    public DirectorDto addDirector(DirectorCreateDto directorDto) {
        Director director = DirectorMapper.mapToDirector(directorDto);
        return DirectorMapper.mapToDto(directorDBStorage.add(director));
    }

    public DirectorDto updateDirector(DirectorUpdateDto directorDto) {
        Director director = DirectorMapper.mapToDirector(directorDto);
        return DirectorMapper.mapToDto(directorDBStorage.update(director));
    }

    public void deleteDirector(Long id) {
        directorDBStorage.delete(id);
    }
}
