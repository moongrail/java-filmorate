package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Director {
    private long id;
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