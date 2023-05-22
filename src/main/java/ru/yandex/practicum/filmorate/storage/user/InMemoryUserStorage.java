package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
        userValidation(user);
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            userValidation(user);
            users.put(user.getId(), user);
            log.info("Обновлен пользователь: {}", user);
            return user;
        } else {
            log.info("Пользователь с id {} не найден", user.getId());
            throw new UserNotFoundException("Пользователя с id " + user.getId() + " не существует.");
        }
    }

    @Override
    public User getUser(Long id) {
        if (users.get(id) == null) {
            log.info("Пользователь с id {} не найден", id);
            throw new UserNotFoundException("Пользователя с id " + id + " не существует.");
        }
        return users.get(id);
    }

    private Long generateId() {
        return id++;
    }

    private void userValidation(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Имя для отображения пустое — в таком случае будет использован логин: {}", user.getLogin());
        }
    }
}
