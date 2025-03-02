package ru.practicum.controller.adminreq;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.NewUserDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Collection<UserDto>> findUsers(@RequestParam(required = false) List<Long> ids,
                                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Поступил запрос пользователей от индекса {} количеством {}", from, size);
        return ResponseEntity.ok().body(userService.findPortion((ids == null || ids.isEmpty() ? null : ids), from, size));
    }

    @PostMapping
    public ResponseEntity<UserDto> addNewUser(@RequestBody @Valid NewUserDto userDto) {
        log.info("Поступил запрос на создание пользователя с телом {}", userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userDto));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Получен запрос на удаление пользователя с ID {}", userId);
        userService.delete(userId);
    }
}
