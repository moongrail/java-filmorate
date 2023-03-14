package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validate.ValidBirthDayUser;
import ru.yandex.practicum.filmorate.validate.ValidLoginUser;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @NotNull
    private Long id;
    @NotBlank
    @Email
    private String email;
    @NotEmpty
    @ValidLoginUser
    private String login;
    private String name;
    @ValidBirthDayUser
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate birthday;

}
