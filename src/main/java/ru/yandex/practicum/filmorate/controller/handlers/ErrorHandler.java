package ru.yandex.practicum.filmorate.controller.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@RestControllerAdvice(basePackages = "ru.yandex.practicum.filmorate.controller")
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameterException(final IncorrectParameterException e) {
        return new ErrorResponse(
                String.format("Ошибка с полем \"%s\".", e.getParameter())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlePostNotFoundException(final FilmNotFoundException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlePostNotFoundException(final UserNotFoundException e) {
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(MpaNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerValidationException(MpaNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(GenreNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerValidationException(GenreNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
}
