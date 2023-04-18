package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaStorageDbTest {
    private final MpaStorageDb mpaStorageDb;

    @Test
    void getAll() {
        List<Mpa> all = mpaStorageDb.getAll();

        assertThat(all.size()).isEqualTo(5);
    }

    @Test
    void getById() {
        Optional<Mpa> byId = mpaStorageDb.getById(1L);

        assertThat(byId.isPresent()).isTrue();
        assertThat(byId.get().getId()).isEqualTo(1);
        assertThat(byId.get().getName()).isEqualTo("G");
    }

    @Test
    void getByIdEmpty() {
        Optional<Mpa> byId = mpaStorageDb.getById(3131L);

        assertThat(byId.isPresent()).isFalse();
    }

    @Test
    void containsMpa() {
        assertThat(mpaStorageDb.containsMpa(1L)).isTrue();
    }

    @Test
    void notContainsMpa() {
        assertThat(mpaStorageDb.containsMpa(7666L)).isFalse();
    }
}