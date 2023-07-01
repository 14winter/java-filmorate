package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public void addFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        friendStorage.addFriend(userId, friendId);
        log.info("Добавлены в друзья: {} и {}", user.getName(), friend.getName());
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        friendStorage.deleteFriend(userId, friendId);
        log.info("Удалены из друзей: {} и {}", user.getName(), friend.getName());
    }

    public List<User> findCommonFriends(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        log.info("Найти общих друзей: {} и {}", user.getName(), friend.getName());
        return friendStorage.findCommonFriends(userId, friendId);
    }

    public Collection<User> findFriends(Long userId) {
        User user = getUser(userId);
        log.info("Найти друзей пользователя: {}", user.getName());
        return friendStorage.findFriends(userId);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        userValidation(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        userValidation(user);
        return userStorage.update(user);
    }

    public User getUser(Long id) {
        return userStorage.getUser(id)
                .orElseThrow(() -> {
                    log.info("Пользователь с id {} не найден", id);
                    return new UserNotFoundException(String.format("Пользователя с id " + id + " не существует."));
                });
    }

    private void userValidation(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            log.info("Имя для отображения пустое — в таком случае будет использован логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }
}