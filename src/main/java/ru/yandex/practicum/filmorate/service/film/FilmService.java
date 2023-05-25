package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER, 28);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);
        film.getLikes().add(userId);
        log.info("Пользователь {} поставил Лайк фильму {}", userStorage.getUser(userId).getName(), film.getName());
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);
        film.getLikes().remove(userId);
        log.info("Пользователь {} удалил Лайк с фильма {}", userStorage.getUser(userId).getName(), film.getName());
    }

    public List<Film> getPopularFilms(long size) {
        log.info("Получить список популярных фильмов: {}", size);
        return filmStorage.findAll()
                .stream()
                .sorted(this::compare)
                .limit(size)
                .collect(Collectors.toList());
    }

    private int compare(Film p0, Film p1) {
        return p1.getLikes().size() - p0.getLikes().size();
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        filmValidation(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        filmValidation(film);
        return filmStorage.update(film);
    }

    public Film getFilm(Long id) {
        return filmStorage.getFilm(id);
    }

    private void filmValidation(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            log.info("Дата релиза {} должна быть не раньше {} {} {}", film.getReleaseDate(), CINEMA_BIRTHDAY.getDayOfMonth(), CINEMA_BIRTHDAY.getMonth(), CINEMA_BIRTHDAY.getYear());
            throw new ValidationException("Дата релиза — не раньше " + CINEMA_BIRTHDAY.getDayOfMonth() + " " + CINEMA_BIRTHDAY.getMonth()
                    + " " + CINEMA_BIRTHDAY.getYear());
        }
    }
}
