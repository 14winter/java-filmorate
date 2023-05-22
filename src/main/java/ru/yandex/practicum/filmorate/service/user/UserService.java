package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        userValidation(userId, friendId);
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Добавлены в друзья: {} и {}", user.getName(), friend.getName());
    }

    public void deleteFriend(Long userId, Long friendId) {
        userValidation(userId, friendId);
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Удалены из друзей: {} и {}", user.getName(), friend.getName());
    }

    public List<User> findCommonFriends(Long userId, Long friendId) {
        userValidation(userId, friendId);
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        List<Long> friendsId = user.getFriends().stream()
                .filter(x -> friend.getFriends().contains(x))
                .collect(Collectors.toList());

        List<User> friends = new ArrayList<>();
        for (Long id : friendsId) {
            User commonFriend = userStorage.getUser(id);
            friends.add(commonFriend);
        }
        log.info("Найти общих друзей: {} и {}", user.getName(), friend.getName());
        return friends;
    }

    public List<User> findFriends(Long userId) {
        if (userStorage.getUser(userId) == null) {
            throw new UserNotFoundException("Пользователя с id " + userId + " не существует.");
        }
        Set<Long> friendsId = userStorage.getUser(userId).getFriends();
        List<User> friends = new ArrayList<>();
        for (Long id : friendsId) {
            User user = userStorage.getUser(id);
            friends.add(user);
        }
        log.info("Найти друзей пользователя: {}", userStorage.getUser(userId).getName());
        return friends;
    }

    private void userValidation(Long userId, Long friendId) {
        if (userId <= 0 || friendId <= 0) {
            throw new UserNotFoundException("id должен быть больше ноля");
        }
        if (userStorage.getUser(userId) == null) {
            throw new UserNotFoundException("Пользователя с id " + userId + " не существует.");
        }
        if (userStorage.getUser(friendId) == null) {
            throw new UserNotFoundException("Пользователя с id " + friendId + " не существует.");
        }
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User getUser(Long id) {
        return userStorage.getUser(id);
    }
}