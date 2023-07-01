package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private Long id = 1L;

    @Override
    public Collection<Film> findAll() {
        log.info("Текущее количество фильмов: {}", films.size());
        return films.values();
    }

    @Override
    public Film create(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Optional<Film> update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Обновлен фильм: {}", film);
            return Optional.of(film);
        } else {
            log.info("Фильм с id {} не найден", film.getId());
            throw new FilmNotFoundException("Фильма с id " + film.getId() + " не существует.");
        }
    }

    @Override
    public Optional<Film> getFilm(Long id) {
        if (id <= 0) {
            log.info("id {} должен быть больше ноля", id);
            throw new FilmNotFoundException("id должен быть больше ноля");
        }
        Film film = films.get(id);
        if (film == null) {
            log.info("Фильм с id {} не найден", id);
            throw new FilmNotFoundException("Фильма с id " + id + " не существует.");
        }
        return Optional.of(film);
    }

    private Long generateId() {
        return id++;
    }

    @Override
    public List<Film> getPopularFilms(long size) {
        log.info("Получить список популярных фильмов: {}", size);
        return findAll()
                .stream()
                .sorted(this::compare)
                .limit(size)
                .collect(Collectors.toList());
    }

    private int compare(Film p0, Film p1) {
        return p1.getLikes().size() - p0.getLikes().size();
    }
}
