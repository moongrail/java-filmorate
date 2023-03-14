package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.adapter.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.Film;

import java.net.URI;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    private static final URI TEST_URL = URI.create("http://localhost:8080/films");

    @Autowired
    private MockMvc mockMvc;

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @SneakyThrows
    @Test
    public void whenFilmMapNotEmpty() {
        mockMvc.perform(get(TEST_URL))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void whenPostIsCorrect() {
        Film build = Film.builder()
                .id(1123L)
                .name("test")
                .description("test")
                .duration(100L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        String json = gson.toJson(build);

        mockMvc.perform(post(TEST_URL)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    @SneakyThrows
    public void whenPostNotId() {
        Film build = Film.builder()
                .name("test")
                .description("test")
                .duration(100L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        String json = gson.toJson(build);

        mockMvc.perform(post(TEST_URL)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void whenDescriptionMoreThan200() {
        Film build = Film.builder()
                .id(2L)
                .name("test")
                .description("Организационная структура регулятивного органа представлена Аудиторским департаментом и Советом директоров, во главе которых находится Исполнительный директор. Организация имеет несколько отделов, каждый из которых занят выполнением своих прямых обязанностей: Надзор за деятельностью компаний, Регистрация и лицензирование, Отдел по вопросам международной торговой зоны, Исследовательский отдел, Страховой, Служба поддержки. Следует заметить, что решение вопросов, касающихся компаний, которые ведут деятельность, связанную с азартными играми, возлагается на отдельный соответствующий отдел.")
                .duration(100L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        String json = gson.toJson(build);

        mockMvc.perform(post(TEST_URL)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(json));
    }

    @Test
    @SneakyThrows
    public void whenIncorrectDate() {
        Film build = Film.builder()
                .id(3L)
                .name("test")
                .description("test description")
                .duration(100L)
                .releaseDate(LocalDate.of(1000, 12, 12))
                .build();

        String json = gson.toJson(build);

        mockMvc.perform(post(TEST_URL)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(json));
    }

    @Test
    @SneakyThrows
    public void durationMustBePositive() {
        Film build = Film.builder()
                .id(3L)
                .name("test")
                .description("test description")
                .duration(-9L)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .build();

        String json = gson.toJson(build);

        mockMvc.perform(post(TEST_URL)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(json));
    }

    @Test
    @SneakyThrows
    public void whenFilmAlreadyExists() {
        Film build = Film.builder()
                .id(1L)
                .name("test")
                .description("test")
                .duration(100L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        String json = gson.toJson(build);

        mockMvc.perform(post(TEST_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post(TEST_URL)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content()
                        .string("Такой фильм уже существует, попробуйте обновить данные о нём."));
    }


    @Test
    public void whenUpdateCorrectExists() throws Exception {
        Film build = Film.builder()
                .id(1L)
                .name("test")
                .description("test")
                .duration(100L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        Film update = Film.builder()
                .id(1L)
                .name("test33")
                .description("test")
                .duration(1330L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();

        String json = gson.toJson(build);
        String jsonUpdate = gson.toJson(update);

        mockMvc.perform(post(TEST_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(put(TEST_URL)
                        .content(jsonUpdate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(jsonUpdate));
    }

    @Test
    public void whenUpdateFilmNotExists() throws Exception {
        Film build = Film.builder()
                .id(1124312L)
                .name("test")
                .description("test")
                .duration(100L)
                .releaseDate(LocalDate.of(2012, 12, 12))
                .build();


        String json = gson.toJson(build);

        mockMvc.perform(put(TEST_URL)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json(json));
    }
}