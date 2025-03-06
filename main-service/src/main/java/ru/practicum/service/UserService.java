package ru.practicum.service;

import ru.practicum.dto.user.NewUserDto;
import ru.practicum.dto.user.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {
    Collection<UserDto> findPortion(List<Long> ids, Integer from, Integer size);

    UserDto create(NewUserDto dto);

    UserDto delete(Long userId);
}
