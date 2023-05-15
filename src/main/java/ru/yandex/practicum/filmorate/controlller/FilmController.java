package ru.yandex.practicum.filmorate.controlller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Текущее количество фильмов: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        filmValidation(film);
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            filmValidation(film);
            films.put(film.getId(), film);
            log.info("Обновлен фильм: {}", film);
            return film;
        } else {
            log.info("Фильм с id {} не найден", film.getId());
            throw new ValidationException("Фильма с id " + film.getId() + " не существует.");
        }
    }

    private int generateId() {
        return id++;
    }

    private void filmValidation(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            log.info("Дата релиза {} должна быть не раньше {} {} {}", film.getReleaseDate(), CINEMA_BIRTHDAY.getDayOfMonth(), CINEMA_BIRTHDAY.getMonth(), CINEMA_BIRTHDAY.getYear());
            throw new ValidationException("Дата релиза — не раньше " + CINEMA_BIRTHDAY.getDayOfMonth() + " " + CINEMA_BIRTHDAY.getMonth()
                    + " " + CINEMA_BIRTHDAY.getYear());
        }
    }
}