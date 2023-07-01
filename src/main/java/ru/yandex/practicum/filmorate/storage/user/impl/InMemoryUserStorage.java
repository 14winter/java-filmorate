package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public Collection<User> findAll() {
        log.info("Текущее количество пользователей: {}", users.size());
        return users.values();
    }

    @Override
    public User create(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Обновлен пользователь: {}", user);
            return user;
        } else {
            log.info("Пользователь с id {} не найден", user.getId());
            throw new UserNotFoundException("Пользователя с id " + user.getId() + " не существует.");
        }
    }

    @Override
    public Optional<User> getUser(Long id) {
        if (id <= 0) {
            log.info("id {} должен быть больше ноля", id);
            throw new UserNotFoundException("id должен быть больше ноля");
        }
        User user = users.get(id);
        if (user == null) {
            log.info("Пользователь с id {} не найден", id);
            throw new UserNotFoundException("Пользователя с id " + id + " не существует.");
        }
        return Optional.of(user);
    }

    private Long generateId() {
        return id++;
    }
}