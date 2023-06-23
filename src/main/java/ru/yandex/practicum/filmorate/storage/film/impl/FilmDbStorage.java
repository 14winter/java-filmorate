package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "SELECT m.*, r.name name FROM movies m JOIN mpa r ON m.mpa_id = r.mpa_id";

        List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeFilm);

        for (Film film : films) {
            LinkedHashSet<Genre> genres = genreStorage.getFilmGenres(film.getId());
            if (genres.isEmpty()) {
                film.setGenres(new LinkedHashSet<>());
            } else {
                film.setGenres(genres);
            }
        }
        return films;
    }

    @Override
    public Film create(Film film) {
        final String sqlQuery = "INSERT INTO movies(title, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        if (film.getGenres() != null) {
            addGenre(film);
        }
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    private void addGenre(Film film) {
        jdbcTemplate.update("DELETE FROM movie_genres WHERE film_id = ?", film.getId());
        final String sqlQuery = "INSERT INTO movie_genres (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
        }
    }

    @Override
    public Optional<Film> update(Film film) {
        final String sqlQuery = "UPDATE movies SET title = ?, description = ?, release_date = ?, duration = ?, " +
                "mpa_id = ? WHERE film_id = ?";
        int updateCount = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            stmt.setLong(6, film.getId());
            return stmt;
        });
        if (updateCount <= 0) {
            return Optional.empty();
        } else {
            if (film.getGenres() != null) {
                addGenre(film);
            }
            log.info("Обновлен фильм: {}", film);
            return Optional.of(film);
        }
    }

    @Override
    public Optional<Film> getFilm(Long id) {
        String sqlQuery = "SELECT * FROM movies m " +
                "JOIN mpa r on m.mpa_id = r.mpa_id " +
                "WHERE m.film_id = ? ";
        List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeFilm, id);

        if (films.isEmpty()) {
            return Optional.empty();
        } else {
            Film film = films.get(0);
            LinkedHashSet<Genre> genres = genreStorage.getFilmGenres(id);
            if (genres.isEmpty()) {
                film.setGenres(new LinkedHashSet<>());
            } else {
                film.setGenres(genres);
            }
            log.info("Найден фильм: {}", film);
            return Optional.of(film);
        }
    }

    @Override
    public Collection<Film> getPopularFilms(long size) {
        String sqlQuery = "SELECT m.*, r.name, COUNT(l.user_id) AS likes_count " +
                "FROM movies m " +
                "JOIN mpa r ON m.mpa_id = r.mpa_id " +
                "LEFT JOIN likes l ON m.film_id = l.film_id " +
                "GROUP BY m.film_id " +
                "ORDER BY likes_count DESC LIMIT ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeFilm, size);
        for (Film film : films) {
            LinkedHashSet<Genre> genres = genreStorage.getFilmGenres(film.getId());
            if (genres.isEmpty()) {
                film.setGenres(new LinkedHashSet<>());
            } else {
                film.setGenres(genres);
            }
        }
        return films;
    }

    protected static Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("title"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(Mpa.builder().id(rs.getInt("mpa_id")).name(rs.getString("name")).build())
                .build();
    }
}