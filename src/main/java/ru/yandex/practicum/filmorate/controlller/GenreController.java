package ru.yandex.practicum.filmorate.controlller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/genres")
public class GenreController {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreController(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @GetMapping
    public Collection<Genre> findAll() {
        log.info("Получен Get запрос к /genres на получение списка жанров.");
        return genreStorage.findAll();
    }

    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable Integer id) {
        log.info("Получен Get запрос к /genres/{id}: id{}", id);
        return genreStorage.getGenre(id);
    }
}
