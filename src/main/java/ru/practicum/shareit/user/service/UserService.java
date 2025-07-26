package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    UserDto create(UserDto userDto);
    UserDto update(Long userId, UserDto userDto);
    Optional<User> getUserById(Long userId);
    UserDto getById(Long userId);
    List<UserDto> getAll();
    void delete(Long userId);
}
