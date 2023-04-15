package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Rating {
    private Integer id;
    private RatingType name;

 public enum RatingType {
    G, PG, PG13, R, NC17;
 }
}
