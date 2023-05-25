/*
package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controlller.FilmController;
import ru.yandex.practicum.filmorate.controlller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FilmorateApplicationTests {

    @Test
    void contextLoads() {
    }

    FilmController filmController = new FilmController();
    UserController userController = new UserController();

    @Test
    void filmControllerTest() {
        Film film1 = new Film("Фильм-1", "1-й фильм", LocalDate.of(2023, 5, 13), 120);
        filmController.create(film1);

        assertNotNull(film1);
        assertEquals(1, film1.getId());

        Film film2 = new Film("Фильм-2", "2-й фильм", LocalDate.of(2023, 5, 13), 120);
        film2.setId(2L);

        Throwable thrown = assertThrows(ValidationException.class, () -> filmController.update(film2));
        Assertions.assertNotNull(thrown.getMessage());
        assertEquals(1, filmController.findAll().size());
    }

    @Test
    void userControllerTest() {
        User user1 = new User("mail1@mail.ru", "User1", "Name1", LocalDate.of(2023, 5, 13));
        userController.create(user1);

        assertNotNull(user1);
        assertEquals(1, user1.getId());

        User user2 = new User("mail2@mail.ru", "User2", "Name2", LocalDate.of(2023, 5, 13));
        user2.setId(2L);

        Throwable thrown = assertThrows(ValidationException.class, () -> userController.update(user2));
        Assertions.assertNotNull(thrown.getMessage());
        assertEquals(1, userController.findAll().size());

        User user3 = new User("mail1@mail.ru", "User3", "", LocalDate.of(2023, 5, 13));
        userController.create(user3);

        assertEquals(user3.getName(), user3.getLogin());
        assertEquals(2, userController.findAll().size());
    }
}*/
