package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validate.ValidDateFilm;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    @NotNull
    private Long id;
    @NotEmpty
    private String name;
    @Size(max = 200, message = "Длина описания не должна быть больше 200.")
    private String description;
    @ValidDateFilm
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate releaseDate;
    @Positive
    @Max(value = 6500, message = "должно быть не больше 6500 минут.")
    private Long duration;
}
