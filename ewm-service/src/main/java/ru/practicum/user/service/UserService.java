package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {
    UserDto save(User user);

    UserDto update(Long id, User user);

    boolean deleteById(Long id);

    UserDto findById(Long id);

    List<UserDto> findAll(List<String> ids, int from, int size);
}
