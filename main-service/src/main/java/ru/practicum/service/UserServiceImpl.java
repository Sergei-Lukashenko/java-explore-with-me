package ru.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.storage.UserRepository;
import ru.practicum.dto.user.NewUserDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.dto.user.UserMapper;
import ru.practicum.model.User;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> findPortion(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size,
                Sort.by(Sort.Direction.ASC, "id"));
        return userRepository.findAllByFilter(ids, pageable).stream()
                .map(UserMapper.INSTANCE::toUserDto)
                .toList();
    }

    @Override
    @Transactional
    public UserDto create(NewUserDto dto) {
        User user = userRepository.save(UserMapper.INSTANCE.toUser(dto));
        log.info("Добавлен новый пользователь с ID {}", user.getId());
        return UserMapper.INSTANCE.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto delete(Long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ид. %d не найден".formatted(userId)));
        userRepository.deleteById(userId);
        log.info("Удален пользователь с ID {}, его содержимое {}", userId, user);
        return UserMapper.INSTANCE.toUserDto(user);
    }
}
