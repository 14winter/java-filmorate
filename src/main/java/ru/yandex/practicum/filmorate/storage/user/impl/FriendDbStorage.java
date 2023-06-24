package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendStorage;

import java.util.List;

@Component
@Slf4j
public class FriendDbStorage implements FriendStorage {
    
    private final JdbcTemplate jdbcTemplate;

    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sqlQuery = "INSERT INTO friends(user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        String sqlQuery = "DELETE FROM friends " +
                "WHERE user_id = ? " +
                "AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> findFriends(Long userId) {
        String sqlQuery = "SELECT * FROM users u, friends f " +
                "WHERE u.user_id = f.friend_id " +
                "AND f.user_id = ?";

        return jdbcTemplate.query(sqlQuery, UserDbStorage::makeUser, userId);
    }

    @Override
    public List<User> findCommonFriends(Long userId, Long friendId) {
        String sqlQuery = "SELECT u.* FROM users u " +
                "JOIN friends f1 ON u.user_id = f1.friend_id AND f1.user_id = ? " +
                "JOIN friends f2 ON u.user_id = f2.friend_id AND f2.user_id = ?";

        return jdbcTemplate.query(sqlQuery, UserDbStorage::makeUser, userId, friendId);
    }
}
