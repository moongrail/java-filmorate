package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@ToString
//В тестах приходят голые айди, это ломает сортировку, если не убрать поле имя.
@EqualsAndHashCode(exclude = "name")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {
    private Long id;
    private String name;
}
