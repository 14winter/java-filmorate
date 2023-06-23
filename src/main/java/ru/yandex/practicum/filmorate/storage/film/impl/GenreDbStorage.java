package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAll() {
        List<Genre> genres = new ArrayList<>();
        String sqlQuery = "SELECT * FROM genres ORDER by genre_id";
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet(sqlQuery);
        while (genreRow.next()) {
            genres.add(makeGenre(genreRow));
        }
        return genres;
    }

    @Override
    public Genre getGenre(Integer id) {
        String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?";

        SqlRowSet genreRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (genreRow.next()) {
            return makeGenre(genreRow);
        } else {
            log.info("Жанр с id {} не найден", id);
            throw new FilmNotFoundException("Жанра с id " + id + " не существует.");
        }
    }

    @Override
    public LinkedHashSet<Genre> getFilmGenres(Long id) {
        String sqlQuery = "SELECT g.genre_id, g.name " +
                "FROM movie_genres mg JOIN genres g " +
                "ON mg.genre_id = g.genre_id " +
                "WHERE mg.film_id = ?";
        LinkedHashSet<Genre> filmGenres = new LinkedHashSet<>();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        while (genreRows.next()) {
            filmGenres.add(makeGenre(genreRows));
        }
        return filmGenres.stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    protected static Genre makeGenre(SqlRowSet genreRow) {
        return new Genre(
                genreRow.getInt("genre_id"),
                genreRow.getString("name")
        );
    }
}