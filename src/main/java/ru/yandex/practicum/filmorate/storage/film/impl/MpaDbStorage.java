package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> findAll() {
        String sqlQuery = "SELECT * FROM mpa ORDER by mpa_id";

        return jdbcTemplate.query(sqlQuery, MpaDbStorage::makeMpa);
    }

    @Override
    public Mpa getMpa(Integer id) {
        String sqlQuery = "SELECT * FROM mpa WHERE mpa_id = ?";

        return jdbcTemplate.query(sqlQuery, MpaDbStorage::makeMpa, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> {
                    log.info("Mpa с id {} не найден", id);
                    throw new FilmNotFoundException("Mpa с id " + id + " не существует.");
                });
    }

    private static Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("name"))
                .build();
    }
}