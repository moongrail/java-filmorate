package ru.yandex.practicum.filmorate.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserNotSpacesInLoginValidator implements ConstraintValidator<ValidLoginUser, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !s.contains(" ");
    }
}
