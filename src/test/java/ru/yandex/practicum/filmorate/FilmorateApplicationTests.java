package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmorateApplicationTests {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE IF NOT EXISTS users (" +
            "id SERIAL PRIMARY KEY," +
            "email VARCHAR(255) NOT NULL," +
            "login VARCHAR(255) NOT NULL," +
            "name VARCHAR(255) NOT NULL," +
            "birthday DATE NOT NULL" + ");";
    private static final String CREATE_MOVIES_TABLE =
            "CREATE TABLE IF NOT EXISTS movies (" +
            "id SERIAL PRIMARY KEY," +
            "title VARCHAR(255) NOT NULL," +
            "description TEXT," +
            "release_date DATE NOT NULL," +
            "duration INTEGER," +
            "mpa_id INTEGER NOT NULL" + ");";

    @BeforeAll
    static void beforeAll(@Autowired JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute(CREATE_USERS_TABLE);
        jdbcTemplate.execute(CREATE_MOVIES_TABLE);

        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) " +
                "VALUES (?, ?, ?, ?), (?, ?, ?, ?);",
                "mail@mail.ru", "dolore", "Nick Name", LocalDate.of(1946, 8, 20),
                "yandex@mail.ru", "dolore2", "Nick Name2", LocalDate.of(1946, 8, 22));

        jdbcTemplate.update("INSERT INTO movies (title, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?), (?, ?, ?, ?, ?);",
                "Аватар", "Синие люди", LocalDate.of(2009, 12, 17), 162, 3,
                "Аватар 2", "Синие люди в воде", LocalDate.of(2022, 12, 15), 192, 3);
    }

    @AfterAll
    static void afterAll(@Autowired JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("DROP TABLE IF EXISTS users CASCADE;");
        jdbcTemplate.execute("DROP TABLE IF EXISTS movies CASCADE;");
    }

    @Test
    @Order(1)
    public void testFindUserById() {
        Optional<User> userOptional = userStorage.getUser(1L);
        assertTrue(userOptional.isPresent());
        User user = userOptional.get();

        assertNotNull(user);
        assertEquals(user.getEmail(), "mail@mail.ru");
        assertEquals(user.getLogin(), "dolore");
        assertEquals(user.getName(), "Nick Name");
        assertEquals(user.getBirthday(), LocalDate.of(1946, 8, 20));
    }

    @Test
    @Order(2)
    public void testCreateUser() {
        User newUser = User.builder().email("gmail@gmail.com").login("dolore3").name("Nick Name3").birthday(LocalDate.of(1946, 8, 23)).build();

        User savedUser = userStorage.create(newUser);

        Optional<User> userOptional = userStorage.getUser(savedUser.getId());
        assertTrue(userOptional.isPresent());
        User retrievedUser = userOptional.get();

        assertEquals(retrievedUser.getEmail(), "gmail@gmail.com");
        assertEquals(retrievedUser.getLogin(), "dolore3");
        assertEquals(retrievedUser.getName(), "Nick Name3");
        assertEquals(retrievedUser.getBirthday(), LocalDate.of(1946, 8, 23));
    }

    @Test
    @Order(3)
    public void testUpdateUser() {
        User oldUser = userStorage.getUser(2L).orElseThrow(IllegalArgumentException::new);

        oldUser.setLogin("dolore2 Update");
        oldUser.setName("Nick Name2 Update");
        oldUser.setBirthday(LocalDate.of(2000, 1, 1));
        userStorage.update(oldUser);
        User updatedUser = userStorage.getUser(2L).orElseThrow(IllegalArgumentException::new);

        assertEquals(updatedUser.getLogin(), "dolore2 Update");
        assertEquals(updatedUser.getName(), "Nick Name2 Update");
        assertEquals(updatedUser.getBirthday(), LocalDate.of(2000, 1, 1));
    }

    @Test
    @Order(4)
    public void testFindAllUser() {
        Collection<User> users = userStorage.findAll();

        assertNotNull(users);
        assertEquals(3, users.size());
    }

    @Test
    @Order(5)
    public void testFindFilmById() {
        Optional<Film> filmOptional = filmStorage.getFilm(1L);
        assertTrue(filmOptional.isPresent());
        Film film = filmOptional.get();

        assertEquals(film.getName(), "Аватар");
        assertEquals(film.getDescription(), "Синие люди");
        assertEquals(film.getReleaseDate(), LocalDate.of(2009, 12, 17));
        assertEquals(film.getDuration(), 162);
        assertEquals(film.getMpa().getId(), 3);
    }

    @Test
    @Order(6)
    public void testCreateFilm() {
        Film newFilm = Film.builder().name("Аватар 3").description("Синие люди в огне").releaseDate(LocalDate.of(2025, 12, 19)).duration(0).mpa(Mpa.builder().id(3).build()).build();

        Film savedFilm = filmStorage.create(newFilm);

        Optional<Film> filmOptional = filmStorage.getFilm(savedFilm.getId());
        assertTrue(filmOptional.isPresent());
        Film retrievedFilm = filmOptional.get();

        assertEquals(retrievedFilm.getName(), "Аватар 3");
        assertEquals(retrievedFilm.getDescription(), "Синие люди в огне");
        assertEquals(retrievedFilm.getReleaseDate(), LocalDate.of(2025, 12, 19));
        assertEquals(retrievedFilm.getDuration(), 0);
        assertEquals(retrievedFilm.getMpa().getId(), 3);
    }

    @Test
    @Order(7)
    public void testUpdateFilm() {
        Film oldFilm = filmStorage.getFilm(2L).orElseThrow(IllegalArgumentException::new);

        oldFilm.setName("Аватар II");
        oldFilm.setDescription("Синие люди плавают в воде");

        filmStorage.update(oldFilm);
        Film updatedFilm = filmStorage.getFilm(2L).orElseThrow(IllegalArgumentException::new);

        assertEquals(updatedFilm.getName(), "Аватар II");
        assertEquals(updatedFilm.getDescription(), "Синие люди плавают в воде");
    }

    @Test
    @Order(8)
    public void testFindAllMovies() {
        Collection<Film> films = filmStorage.findAll();

        assertNotNull(films);
        assertEquals(3, films.size());
    }
}
