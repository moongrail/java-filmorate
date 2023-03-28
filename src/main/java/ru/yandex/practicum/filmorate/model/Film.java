package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.yandex.practicum.filmorate.validate.ValidDateFilm;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "usersWhoLike")
public class Film {
    private Long id;
    @NotEmpty
    private String name;
    @Size(max = 200, message = "Длина описания не должна быть больше 200.")
    private String description;
    @ValidDateFilm
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @Positive
    @Max(value = 6500, message = "должно быть не больше 6500 минут.")
    private Long duration;
    private Set<Long> usersWhoLike;
    private long likes;
}
