package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
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
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Добавлены в друзья: {} и {}", user.getName(), friend.getName());
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Удалены из друзей: {} и {}", user.getName(), friend.getName());
    }

    public List<User> findCommonFriends(Long userId, Long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        List<User> friends = user.getFriends().stream()
                .filter(x -> friend.getFriends().contains(x))
                .map(userStorage::getUser)
                .collect(Collectors.toList());

        log.info("Найти общих друзей: {} и {}", user.getName(), friend.getName());
        return friends;
    }

    public List<User> findFriends(Long userId) {
        User user = userStorage.getUser(userId);
        List<User> friends = user.getFriends().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());

        log.info("Найти друзей пользователя: {}", user.getName());
        return friends;
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
        return userStorage.getUser(id);
    }

    private void userValidation(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            log.info("Имя для отображения пустое — в таком случае будет использован логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }
}