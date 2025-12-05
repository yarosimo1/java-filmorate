package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserCreateDto {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\S+$")
    private String login;

    private String name;

    @NotNull
    @PastOrPresent
    private LocalDate birthday;
}
