package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Feed {

    @NotNull
    private Long timestamp;
    @NotNull
    private Long userId;
    @NotNull
    private EventType eventType;
    @NotNull
    private Operation operation;
    @NotNull
    private Long eventId;
    @NotNull
    private Long entityId;
}
