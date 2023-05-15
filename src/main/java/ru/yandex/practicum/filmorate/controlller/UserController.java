package ru.yandex.practicum.filmorate.controlller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Текущее количество пользователей: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        userValidation(user);
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            userValidation(user);
            users.put(user.getId(), user);
            log.info("Обновлен пользователь: {}", user);
            return user;
        } else {
            log.info("Пользователь с id {} не найден", user.getId());
            throw new ValidationException("Пользователя с id " + user.getId() + " не существует.");
        }
    }

    private int generateId() {
        return id++;
    }

    private void userValidation(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Имя для отображения пустое — в таком случае будет использован логин: {}", user.getLogin());
        }
    }
}
