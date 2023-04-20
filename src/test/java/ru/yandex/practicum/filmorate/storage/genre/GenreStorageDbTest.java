package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreStorageDbTest {
    private final GenreStorageDb genreStorageDb;

    @Test
    void getAll() {
        List<Genre> all = genreStorageDb.getAll();

        assertThat(all.size()).isEqualTo(6);
    }

    @Test
    void getById() {
        Optional<Genre> byId = genreStorageDb.getById(1L);

        assertThat(byId.isPresent()).isTrue();
        assertThat(byId.get().getId()).isEqualTo(1L);
        assertThat(byId.get().getName()).isEqualTo("Комедия");
    }

    @Test
    void getByIdIsEmpty() {
        Optional<Genre> byId = genreStorageDb.getById(234121L);

        assertThat(byId.isPresent()).isFalse();
    }
}