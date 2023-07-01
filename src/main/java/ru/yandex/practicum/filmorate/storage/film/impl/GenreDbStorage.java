package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.util.*;
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
    public List<Genre> getFilmGenresById(Long id) {
        String sqlQuery = "SELECT g.genre_id, g.name " +
                "FROM movie_genres mg JOIN genres g " +
                "ON mg.genre_id = g.genre_id " +
                "WHERE mg.film_id = ?";
        List<Genre> filmGenres = new ArrayList<>();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        while (genreRows.next()) {
            filmGenres.add(makeGenre(genreRows));
        }
        return filmGenres.stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toList());
    }

    public Map<Long, Set<Genre>> getFilmGenres(Collection<Long> filmIds) {
        String sqlQuery = "SELECT mg.film_id, g.genre_id, g.name " +
                "FROM movie_genres mg " +
                "JOIN genres g ON mg.genre_id = g.genre_id " +
                "WHERE mg.film_id IN (" + String.join(",", Collections.nCopies(filmIds.size(), "?")) + ")";

        Map<Long, Set<Genre>> filmGenresMap = new HashMap<>();
        jdbcTemplate.query(sqlQuery, preparedStatement -> {
            int i = 1;
            for (Long filmId : filmIds) {
                preparedStatement.setLong(i++, filmId);
            }
        }, resultSet -> {
            Long filmId = resultSet.getLong("film_id");
            Genre genre = new Genre(resultSet.getInt("genre_id"), resultSet.getString("name"));
            Set<Genre> genres = filmGenresMap.computeIfAbsent(filmId, k -> new LinkedHashSet<>());
            genres.add(genre);
        });

        return filmGenresMap;
    }

    protected static Genre makeGenre(SqlRowSet genreRow) {
        return new Genre(
                genreRow.getInt("genre_id"),
                genreRow.getString("name")
        );
    }
}