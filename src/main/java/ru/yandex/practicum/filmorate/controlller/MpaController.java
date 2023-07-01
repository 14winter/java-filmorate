package ru.yandex.practicum.filmorate.controlller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/mpa")
public class MpaController {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaController(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @GetMapping
    public Collection<Mpa> findAll() {
        log.info("Получен Get запрос на получение списка Mpa.");
        return mpaStorage.findAll();
    }

    @GetMapping("/{id}")
    public Mpa getMpa(@PathVariable Integer id) {
        log.info("Получен Get запрос к /mpa/{id}: id{}", id);
        return mpaStorage.getMpa(id);
    }
}