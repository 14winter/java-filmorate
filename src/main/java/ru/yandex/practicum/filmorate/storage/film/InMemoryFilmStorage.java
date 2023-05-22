package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private Long id = 1L;
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);

    @Override
    public Collection<Film> findAll() {
        log.info("Текущее количество фильмов: {}", films.size());
        return films.values();
    }

    @Override
    public Film create(Film film) {
        filmValidation(film);
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            filmValidation(film);
            films.put(film.getId(), film);
            log.info("Обновлен фильм: {}", film);
            return film;
        } else {
            log.info("Фильм с id {} не найден", film.getId());
            throw new FilmNotFoundException("Фильма с id " + film.getId() + " не существует.");
        }
    }

    @Override
    public Film getFilm(Long id){
        if (films.get(id) == null) {
            log.info("Фильм с id {} не найден", id);
            throw new FilmNotFoundException("Фильма с id " + id + " не существует.");
        }
        return films.get(id);
    }

    private Long generateId() {
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
