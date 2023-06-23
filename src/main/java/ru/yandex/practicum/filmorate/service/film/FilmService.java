package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikeStorage likeStorage;
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        filmValidation(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        filmValidation(film);
        return filmStorage.update(film)
                .orElseThrow(() -> {
                    log.info("Фильм с id {} не найден", film.getId());
                    throw new FilmNotFoundException("Фильма с id " + film.getId() + " не существует.");
                });
    }

    public Film getFilm(Long id) {
        return filmStorage.getFilm(id)
                .orElseThrow(() -> {
                    log.info("Фильм с id {} не найден", id);
                    throw new FilmNotFoundException("Фильма с id " + id + " не существует.");
                });
    }

    public Collection<Film> getPopularFilms(long size) {
        log.info("Получить список популярных фильмов: {}", size);
        return filmStorage.getPopularFilms(size);
    }

    public void addLike(Long filmId, Long userId) {
        Film film = getFilm(filmId);
        User user = userService.getUser(userId);
        likeStorage.addLike(filmId, userId);
        log.info("Пользователь {} поставил Лайк фильму {}", user.getName(), film.getName());

    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = getFilm(filmId);
        User user = userService.getUser(userId);
        likeStorage.deleteLike(filmId, userId);
        log.info("Пользователь {} удалил Лайк с фильма {}", user.getName(), film.getName());

    }

    private void filmValidation(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            log.info("Дата релиза {} должна быть не раньше {} {} {}", film.getReleaseDate(), CINEMA_BIRTHDAY.getDayOfMonth(), CINEMA_BIRTHDAY.getMonth(), CINEMA_BIRTHDAY.getYear());
            throw new ValidationException("Дата релиза — не раньше " + CINEMA_BIRTHDAY.getDayOfMonth() + " " + CINEMA_BIRTHDAY.getMonth()
                    + " " + CINEMA_BIRTHDAY.getYear());
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.info("Название фильма c не может быть пустым.");
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            log.info("Описание фильма не может быть пустым.");
            throw new ValidationException("Описание фильма не может быть пустым.");
        }
        if (film.getReleaseDate() == null) {
            log.info("Дата релиза фильма не может быть пустым.");
            throw new ValidationException("Дата релиза фильма не может быть пустым.");
        }
        if (film.getDuration() <= 0) {
            log.info("Продолжительность фильма не может быть пустым.");
            throw new ValidationException("Продолжительность фильма не может быть пустым.");
        }
    }
}