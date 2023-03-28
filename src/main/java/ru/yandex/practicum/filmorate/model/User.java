package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.yandex.practicum.filmorate.validate.ValidBirthDayUser;
import ru.yandex.practicum.filmorate.validate.ValidLoginUser;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "friendsId")
public class User {
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
    private LocalDate birthday;
    private Set<Long> friendsId = new HashSet<>();
}
