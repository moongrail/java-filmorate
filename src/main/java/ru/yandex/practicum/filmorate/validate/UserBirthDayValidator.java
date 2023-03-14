package ru.yandex.practicum.filmorate.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class UserBirthDayValidator implements ConstraintValidator<ValidBirthDayUser, LocalDate> {
    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate.isBefore(LocalDate.now());
    }
}
