package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mpa {
    private Long id;
    @NotBlank
    private String name;
}
