package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
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
import java.util.stream.Collectors;

@Component
@Primary
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    public Collection<Film> findAll() {
        String sqlQuery = "SELECT m.*, r.name " +
                "FROM movies m " +
                "JOIN mpa r ON m.mpa_id = r.mpa_id";
        List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::makeFilm);
        setGenresForFilms(films);
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
        addGenre(film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    private void addGenre(Film film) {
        if (film.getGenres() != null) {
            String sqlQuery = "DELETE FROM movie_genres WHERE film_id = ?";
            jdbcTemplate.update(sqlQuery, film.getId());

            List<Genre> genres = film.getGenres().stream()
                    .distinct()
                    .collect(Collectors.toList());
            film.setGenres(genres);
            this.jdbcTemplate.batchUpdate("INSERT INTO movie_genres (film_id, genre_id) VALUES (?, ?)",
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement stmt, int i) throws SQLException {
                            stmt.setLong(1, film.getId());
                            stmt.setInt(2, genres.get(i).getId());
                        }

                        public int getBatchSize() {
                            return genres.size();
                        }
                    });
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
            addGenre(film);
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
            List<Genre> genres = genreStorage.getFilmGenresById(id)
                    .stream()
                    .distinct()
                    .collect(Collectors.toList());
            if (genres.isEmpty()) {
                film.setGenres(new ArrayList<>());
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
        setGenresForFilms(films);
        return films;
    }

    private void setGenresForFilms(List<Film> films) {
        Set<Long> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toSet());

        Map<Long, Set<Genre>> filmGenresMap = genreStorage.getFilmGenres(filmIds);

        for (Film film : films) {
            List<Genre> genres = new ArrayList<>(filmGenresMap.getOrDefault(film.getId(), Collections.emptySet()));
            film.setGenres(genres
                    .stream()
                    .distinct()
                    .collect(Collectors.toList()));
        }
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