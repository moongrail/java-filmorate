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
import ru.yandex.practicum.filmorate.model.User;

import java.net.URI;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    private static final URI TEST_URL = URI.create("http://localhost:8080/users");

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
        User build = User.builder()
                .id(1L)
                .name("test")
                .login("test")
                .email("test@example.com")
                .birthday(LocalDate.of(2012, 12, 12))
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
    public void whenUserPostNoId() {
        User build = User.builder()
                .name("test")
                .login("test")
                .email("test@example.com")
                .birthday(LocalDate.of(2012, 12, 12))
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
    public void whenUserPostIncorrectEmail() {
        User build = User.builder()
                .id(2L)
                .name("test")
                .login("test")
                .email("testexample.com")
                .birthday(LocalDate.of(2012, 12, 12))
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
    public void whenUserPostIncorrectBirthDay() {
        User build = User.builder()
                .id(122L)
                .name("test")
                .login("test")
                .email("teste@xample.com")
                .birthday(LocalDate.of(2212, 12, 12))
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
    public void whenUserPostIncorrectLoginBlank() {
        User build = User.builder()
                .id(200L)
                .name("test")
                .login("  ")
                .email("teste@xample.com")
                .birthday(LocalDate.of(2012, 12, 12))
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
    public void whenUserPostIncorrectLoginBlankTwo() {
        User build = User.builder()
                .id(2L)
                .name("test")
                .login(" A B ")
                .email("teste@xample.com")
                .birthday(LocalDate.of(2012, 12, 12))
                .build();

        String json = gson.toJson(build);

        mockMvc.perform(post(TEST_URL)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(json));
    }
}