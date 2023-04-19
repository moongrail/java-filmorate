package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = "name")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genre {
    private Long id;
    private String name;
}
