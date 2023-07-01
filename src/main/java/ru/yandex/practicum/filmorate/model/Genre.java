package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Genre {
    private final Integer id;
    @NotBlank
    private final String name;
}
