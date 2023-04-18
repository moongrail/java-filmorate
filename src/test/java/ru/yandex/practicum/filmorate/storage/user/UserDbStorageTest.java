package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    void testSaveCorrect() {
        User build = User.builder()
                .id(1L)
                .name("Test")
                .email("test@mail.ru")
                .login("testLogin")
                .birthday(LocalDate.EPOCH)
                .build();

        Optional<User> save = userStorage.save(build);

        assertThat(Objects.equals(build, save.get())).isTrue();
    }

    @Test
    void testSaveIncorrect() {
        User build = User.builder()
                .name("Test")
                .login("test Login")
                .birthday(LocalDate.EPOCH)
                .build();

        Optional<User> save = userStorage.save(build);

        assertThat(save.isEmpty()).isTrue();
    }

    @Test
    void getAll() {
        addUsersInDb();
        List<User> all = userStorage.getAll();
        assertThat(all.size()).isEqualTo(6);
    }

    @Test
    void getAllEmpty() {
        List<User> all = userStorage.getAll();
        assertThat(all.size()).isEqualTo(0);
    }

    @Test
    void updateCorrect() {
        addUsersInDb();

        User updateUser = User.builder()
                .id(1L)
                .name("updateUser")
                .email("updateUser@mail.ru")
                .login("updateUserLogin")
                .birthday(LocalDate.EPOCH)
                .build();

        Optional<User> update = userStorage.update(updateUser);

        assertThat(Objects.equals(updateUser, update.get())).isTrue();
    }

    @Test
    void updateIncorrect() {
        addUsersInDb();

        User updateUser = User.builder()
                .id(666L)
                .name("updateUser")
                .email("updateUser@mail.ru")
                .login("updateUserLogin")
                .birthday(LocalDate.EPOCH)
                .build();

        Optional<User> update = userStorage.update(updateUser);

        assertThat(update.isEmpty()).isTrue();
    }

    @Test
    void deleteCorrect() {
        addUsersInDb();

        userStorage.delete(1L);

        assertThat(userStorage.getById(1L).isEmpty()).isTrue();
    }

    @Test
    void deleteIncorrect() {
        addUsersInDb();

        userStorage.delete(1421421412L);

        assertThat(userStorage.getById(1L).isPresent()).isTrue();
    }

    @Test
    void getByIdCorrect() {
        addUsersInDb();

        boolean present = userStorage.getById(1L).isPresent();

        assertThat(present).isTrue();
    }

    @Test
    void getByIdIncorrect() {
        boolean present = userStorage.getById(1L).isPresent();

        assertThat(present).isFalse();
    }

    @Test
    void addFriendCorrect() {
        addUsersInDb();

        userStorage.addFriend(5L, 6L);
        userStorage.addFriend(6L, 5L);

        assertThat(userStorage.getFriends(5L).size()).isEqualTo(1);
        assertThat(userStorage.getFriends(6L).size()).isEqualTo(1);
    }

    @Test
    void addFriendIncorrect() {
        addUsersInDb();

        userStorage.addFriend(3L, 555L);

        assertThat(userStorage.getFriends(3L).isEmpty()).isTrue();
    }

    @Test
    void getCommonFriends() {
        addUsersInDb();

        userStorage.addFriend(3L, 4L);
        userStorage.addFriend(1L, 4L);

        List<User> commonFriends = userStorage.getCommonFriends(1L, 3L);

        assertThat(commonFriends.size()).isEqualTo(1);
    }

    @Test
    void removeFriend() {
        addUsersInDb();

        userStorage.removeFriend(3L,   4L);

        assertThat(userStorage.getFriends(3L).isEmpty()).isTrue();
        assertThat(userStorage.getFriends(4L).isEmpty()).isTrue();
    }

    @Test
    void getFriends() {
        addUsersInDb();

        userStorage.addFriend(1L, 2L);

        assertThat(userStorage.getFriends(1L).size()).isEqualTo(1);
    }

    @Test
    void getFriendsIsEmpty() {
        addUsersInDb();

        assertThat(userStorage.getFriends(1L).isEmpty()).isTrue();
    }

    private void addUsersInDb() {
        User test1 = User.builder()
                .name("Test1")
                .email("test1@mail.ru")
                .login("test1Login")
                .birthday(LocalDate.EPOCH)
                .build();

        User test2 = User.builder()
                .name("Test2")
                .email("test2@mail.ru")
                .login("test2Login")
                .birthday(LocalDate.EPOCH)
                .build();

        User test3 = User.builder()
                .name("Test1")
                .email("test1@mail.ru")
                .login("test1Login")
                .birthday(LocalDate.EPOCH)
                .build();

        User test4 = User.builder()
                .name("Test2")
                .email("test2@mail.ru")
                .login("test2Login")
                .birthday(LocalDate.EPOCH)
                .build();

        User test5 = User.builder()
                .name("Test1")
                .email("test1@mail.ru")
                .login("test1Login")
                .birthday(LocalDate.EPOCH)
                .build();

        User test6 = User.builder()
                .name("Test2")
                .email("test2@mail.ru")
                .login("test2Login")
                .birthday(LocalDate.EPOCH)
                .build();

        userStorage.save(test1);
        userStorage.save(test2);
        userStorage.save(test5);
        userStorage.save(test6);
        userStorage.save(test3);
        userStorage.save(test4);
    }
}