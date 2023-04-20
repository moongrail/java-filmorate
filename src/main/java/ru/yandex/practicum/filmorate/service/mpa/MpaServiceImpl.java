package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaStorage;

    @Override
    public List<Mpa> getAll() {
        List<Mpa> all = mpaStorage.getAll();
        return all;
    }

    @Override
    public Mpa getMpaById(Long id) {
        Optional<Mpa> byId = mpaStorage.getById(id);

        if (byId.isEmpty()) {
            throw new MpaNotFoundException("Mpa not found");
        }

        return byId.get();
    }
}
