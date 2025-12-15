package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.director.DirectorCreateDto;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.DirectorUpdateDto;
import ru.yandex.practicum.filmorate.model.Director;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DirectorMapper {
    public static DirectorDto mapToDto(Director director) {
        return DirectorDto.builder()
                .id(director.getId())
                .name(director.getName())
                .build();
    }

    public static DirectorDto mapToDto(DirectorCreateDto directorCreateDto) {
        return DirectorDto.builder()
                .name(directorCreateDto.getName())
                .build();
    }

    public static DirectorDto mapToDto(DirectorUpdateDto directorUpdateDto) {
        return DirectorDto.builder()
                .id(directorUpdateDto.getId())
                .name(directorUpdateDto.getName())
                .build();
    }

    public static Director mapToDirector(DirectorDto directorDto) {
        return Director.builder()
                .id(directorDto.getId())
                .name(directorDto.getName())
                .build();
    }

    public static Director mapToDirector(DirectorCreateDto directorCreateDto) {
        return Director.builder()
                .name(directorCreateDto.getName())
                .build();
    }

    public static Director mapToDirector(DirectorUpdateDto directorUpdateDto) {
        return Director.builder()
                .id(directorUpdateDto.getId())
                .name(directorUpdateDto.getName())
                .build();
    }
}
