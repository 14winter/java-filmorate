package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.LinkedHashSet;

public interface GenreStorage {

    Collection<Genre> findAll();

    Genre getGenre(Integer id);

    LinkedHashSet<Genre> getFilmGenres(Long id);
}
