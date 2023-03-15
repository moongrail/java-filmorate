package ru.yandex.practicum.filmorate.util;

import lombok.experimental.UtilityClass;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public  class BindingResultErrorsUtil {
    public static List<String> getErrors(BindingResult errors) {
        List<String> errorsList = new ArrayList<>();
        FieldError fieldError = null;
        ObjectError objectError = null;

        for (Object object : errors.getAllErrors()) {
            if(object instanceof FieldError) {
                fieldError = (FieldError) object;
            }
            if(object instanceof ObjectError) {
                 objectError = (ObjectError) object;

            }
            errorsList.add(String.format("Ошибка валидации в поле - %s, причина - %s",
                    fieldError.getField(),
                    objectError.getDefaultMessage()));
        }

        return errorsList;
    }
}
