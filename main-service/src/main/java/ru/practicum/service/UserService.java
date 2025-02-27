package ru.practicum.service;

import ru.practicum.dto.user.NewUserDto;
import ru.practicum.dto.user.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {

    UserDto create(NewUserDto dto);

    Collection<UserDto> getAll(List<Long> ids, Integer from, Integer size);

    UserDto delete(Long userId);
}
