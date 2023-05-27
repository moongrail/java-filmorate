package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = "name")
@AllArgsConstructor
@Builder
public class Director {
    private long id;
    @NonNull
    @NotBlank
    private String name;
    private final Set<Film> films = new HashSet<>();

    public List<Film> getFilms() {
        return films.stream()
                .sorted(Comparator.comparing(Film::getId))
                .collect(Collectors.toList());
    }

    public Map<String, Object> directors() {
        Map<String, Object> values = new HashMap<>();
        values.put("director_name", name);
        return values;
    }
}