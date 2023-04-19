package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userStorage;

    @Test
    void saveCorrect() {
        Film film = getFilm();

        Optional<Film> save = filmDbStorage.save(film);

        assertThat(Objects.equals(film, save.get())).isTrue();
    }



    @Test
    void saveInCorrect() {
        Film film = getFilmError();

        Optional<Film> save = filmDbStorage.save(film);

        assertThat(save.isEmpty()).isTrue();
    }

    @Test
    void getFilmCorrect() {
        addFilmsInDb();

        Film filmFull = filmDbStorage.getFilmFull(1L);

        assertThat(filmFull.getId()).isEqualTo(1L);
        assertThat(filmFull.getName()).isEqualTo("test1");
        assertThat(filmFull.getDuration()).isEqualTo(100);

        assertThat(filmFull.getMpa().getId()).isEqualTo(1L);
        assertThat(filmFull.getMpa().getName()).isEqualTo("G");
    }

    @Test
    void getFilmFullIncorrect() {
        Film filmFull = filmDbStorage.getFilmFull(1L);

        assertThat(filmFull).isEqualTo(null);
    }

    @Test
    void getAll() {
        addFilmsInDb();

        List<Film> all = filmDbStorage.getAll();

        assertThat(all.size()).isEqualTo(3);
    }

    @Test
    void getAllEmpty() {
        List<Film> all = filmDbStorage.getAll();

        assertThat(all.size()).isEqualTo(0);
    }

    @Test
    void updateCorrect() {
        addFilmsInDb();

        Film film = getFilmUpdate();

        Optional<Film> update = filmDbStorage.update(film);

        assertThat(Objects.equals(film, update.get())).isTrue();
    }



    @Test
    void updateIncorrect() {
        addFilmsInDb();

        Film film = getFilmUpdateError();

        Optional<Film> update = filmDbStorage.update(film);

        assertThat(update.isEmpty()).isTrue();
    }

    @Test
    void deleteCorrect() {
        addFilmsInDb();

        filmDbStorage.delete(1L);

        assertThat(filmDbStorage.getById(1L).isEmpty()).isTrue();
    }

    @Test
    void getByIdCorrect() {
        addFilmsInDb();

        Optional<Film> byId = filmDbStorage.getById(1L);

        assertThat(byId.isPresent()).isTrue();
        assertThat(byId.get().getId()).isEqualTo(1L);
        assertThat(byId.get().getRate()).isEqualTo(100);
        assertThat(byId.get().getName()).isEqualTo("test1");
    }

    @Test
    void getByIdIncorrect() {
        Optional<Film> byId = filmDbStorage.getById(12L);

        assertThat(byId.isPresent()).isFalse();
    }

    @Test
    void getTheMostPopularFilms() {
        addFilmsInDb();

        List<Film> theMostPopularFilms = filmDbStorage.getTheMostPopularFilms(2);

        assertThat(theMostPopularFilms.size()).isEqualTo(2);
        assertThat(theMostPopularFilms.get(0).getRate()).isEqualTo(600);
    }

    @Test
    void addLike() {
        addFilmsInDb();
        addUsersInDb();

        filmDbStorage.addLike(1L, 1L);

        Optional<Film> byId = filmDbStorage.getById(1L);

        assertThat(byId.get().getRate()).isEqualTo(101);
    }

    @Test
    void removeLike() {
        addFilmsInDb();
        addUsersInDb();

        filmDbStorage.removeLike(1L, 1L);

        Optional<Film> byId = filmDbStorage.getById(1L);

        assertThat(byId.get().getRate()).isEqualTo(99);
    }

    @Test
    void containsFilm() {
    }

    private void addFilmsInDb() {
        Film film1 = Film.builder()
                .name("test1")
                .description("test description")
                .releaseDate(LocalDate.EPOCH)
                .duration(100)
                .rate(100)
                .mpa(Mpa.builder()
                        .id(1L)
                        .name("G")
                        .build())
                .build();

        Film film2 = Film.builder()
                .name("test2")
                .description("test description2")
                .releaseDate(LocalDate.EPOCH)
                .duration(102)
                .rate(102)
                .mpa(Mpa.builder()
                        .id(1L)
                        .name("G")
                        .build())
                .build();

        Film film4 = Film.builder()
                .name("test2")
                .description("test description2")
                .releaseDate(LocalDate.EPOCH)
                .duration(102)
                .rate(600)
                .mpa(Mpa.builder()
                        .id(1L)
                        .name("G")
                        .build())
                .build();

        filmDbStorage.save(film1);
        filmDbStorage.save(film2);
        filmDbStorage.save(film4);
    }

    private void addUsersInDb() {
        User test1 = User.builder()
                .name("Test1")
                .email("test1@mail.ru")
                .login("test1Login")
                .birthday(LocalDate.EPOCH)
                .build();

        userStorage.save(test1);
    }

    private static Film getFilm() {
        Film film = Film.builder()
                .name("test")
                .description("test description")
                .releaseDate(LocalDate.EPOCH)
                .duration(100)
                .rate(100)
                .mpa(Mpa.builder()
                        .id(1L)
                        .name("G")
                        .build())
                .build();
        return film;
    }

    private static Film getFilmError(){
        return Film.builder()
                .name("test")
                .description("test description")
                .duration(100)
                .mpa(Mpa.builder()
                        .id(1L)
                        .name("G")
                        .build())
                .build();
    }

    private Film getFilmUpdate() {
        return  Film.builder()
                .id(1L)
                .name("update")
                .description("update description")
                .releaseDate(LocalDate.EPOCH)
                .duration(100)
                .rate(100)
                .mpa(Mpa.builder()
                        .id(2L)
                        .name("PG")
                        .build())
                .build();
    }
    private Film getFilmUpdateError() {
        return  Film.builder()
                .id(3331L)
                .name("update")
                .description("update description")
                .releaseDate(LocalDate.EPOCH)
                .duration(100)
                .rate(100)
                .mpa(Mpa.builder()
                        .id(2L)
                        .name("PG")
                        .build())
                .build();
    }
}