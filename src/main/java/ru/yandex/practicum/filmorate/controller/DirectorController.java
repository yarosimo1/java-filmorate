package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.director.DirectorCreateDto;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.DirectorUpdateDto;
import ru.yandex.practicum.filmorate.service.film.DirectorService;
import ru.yandex.practicum.filmorate.validation.method.OnCreate;
import ru.yandex.practicum.filmorate.validation.method.OnUpdate;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public List<DirectorDto> getAllDirectors() {
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    public DirectorDto getDirectorById(@PathVariable long id) {
        return directorService.findById(id);
    }

    @PostMapping
    public DirectorDto addDirector(@Validated(OnCreate.class) @RequestBody DirectorCreateDto directorDto) {
        return directorService.addDirector(directorDto);
    }

    @PutMapping
    public DirectorDto updateDirector(@Validated(OnUpdate.class) @RequestBody DirectorUpdateDto directorDto) {
        return directorService.updateDirector(directorDto);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable long id) {
        directorService.deleteDirector(id);
    }
}
