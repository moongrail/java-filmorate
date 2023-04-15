package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@Qualifier("userDbStorage")
@RequiredArgsConstructor
@Primary
public class UserDbStorage implements UserStorage {
    private static final String INSERT_USER = "INSERT INTO users(email,login,name,birthday) VALUES (?,?,?,?)";
    private static final String GET_ALL_USERS = "SELECT * FROM users";
    private static final String FIND_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE user_id = ?";
    private static final String UPDATE_USER = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ?" +
            " WHERE user_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<User> save(User user) {
        try {
            long idUser = saveAndReturnId(user, INSERT_USER);
            user.setId(idUser);

            return Optional.of(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private long saveAndReturnId(User user, String sql) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public List<User> getAll() {
        List<User> userList = jdbcTemplate.query(GET_ALL_USERS, (rs, row) -> userMapper(rs));
        return userList;
    }

    @Override
    public Optional<User> update(User user) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("Select * from users where user_id = ?", user.getId());
        if (userRows.next()) {
            jdbcTemplate.update(UPDATE_USER,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> updateById(Long id, User user) {
        return Optional.empty();
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update(DELETE_USER, id);
    }

    @Override
    public Optional<User> getById(Long id) {
        SqlRowSet userRs = jdbcTemplate.queryForRowSet(FIND_USER_BY_ID, id);
        if (userRs.next()) {
            User user = jdbcTemplate.queryForObject(FIND_USER_BY_ID, (rs, row) -> userMapper(rs), id);
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    private User userMapper(ResultSet rs) throws SQLException {
        String sqlSetFriend = "select friend_id from friend where user_id = ?";
        Long id = rs.getLong("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        User user = User.builder()
                .id(id)
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .build();

        user.setFriendsId(new HashSet<>(jdbcTemplate.query(sqlSetFriend,
                (rsFriends, rowNum) -> rs.getLong("friend_id"), id)));

        return user;
    }
 //дописать методы для добавления друзей и удаления
}
