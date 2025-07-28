package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody @Valid UserDto userDto) {
        log.info("Создание пользователя с данными: {}", userDto);
        UserDto createdUser = userService.create(userDto);
        log.info("Пользователь создан с id: {}", createdUser.getId());
        return ResponseEntity.ok(createdUser);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(
            @PathVariable Long userId,
            @RequestBody UserDto userDto) {
        log.info("Обновление пользователя id = {} с данными: {}", userId, userDto);
        UserDto updatedUser = userService.update(userId, userDto);
        log.info("Пользователь id = {} обновлен", userId);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getById(@PathVariable Long userId) {
        log.info("Получение пользователя по id = {}", userId);
        UserDto user = userService.getById(userId);
        log.info("Пользователь получен: {}", user);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        log.info("Получение списка всех пользователей");
        List<UserDto> users = userService.getAll();
        log.info("Получено {} пользователей", users.size());
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        log.info("Удаление пользователя с id = {}", userId);
        userService.delete(userId);
        log.info("Пользователь с id = {} удален", userId);
        return ResponseEntity.ok().build();
    }
}
