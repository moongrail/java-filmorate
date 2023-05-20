package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class Director {
    private Long id;
    private String name;
}
