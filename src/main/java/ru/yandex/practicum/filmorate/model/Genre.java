package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Genre {
    private Integer id;
    private String name;

    public enum GenreType {
        Комедия,
        Драма,
        Мультфильм,
        Триллер,
        Документальный,
        Боевик
    }
}
