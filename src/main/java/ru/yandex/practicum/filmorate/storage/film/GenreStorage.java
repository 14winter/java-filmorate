package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GenreStorage {

    Collection<Genre> findAll();

    Genre getGenre(Integer id);

    Map<Long, Set<Genre>> getFilmGenres(Collection<Long> filmIds);

    List<Genre> getFilmGenresById(Long id);
}
