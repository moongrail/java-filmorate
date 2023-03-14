package ru.yandex.practicum.filmorate.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FilmDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateFilm {
    String message() default "дата релиза должна быть не раньше 28 декабря 1895 года";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default  {};
}
