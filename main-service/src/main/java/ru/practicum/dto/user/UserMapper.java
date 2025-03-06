package ru.practicum.dto.user;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.model.User;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toUser(NewUserDto newUserDto);

    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);
}
