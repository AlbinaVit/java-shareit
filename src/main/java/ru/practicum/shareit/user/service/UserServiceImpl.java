package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        checkEmailUniqueness(userDto.getEmail());

        User user = userMapper.toUser(userDto);
        user.setId(idCounter.getAndIncrement());
        users.put(user.getId(), user);

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User existingUser = findUserById(userId);

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existingUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            if (!existingUser.getEmail().equals(userDto.getEmail())) {
                checkEmailUniqueness(userDto.getEmail());
            }
            existingUser.setEmail(userDto.getEmail());
        }

        return userMapper.toUserDto(existingUser);
    }

    @Override
    public User findUserById(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        return user;
    }

    @Override
    public UserDto getById(Long userId) {
        User user = findUserById(userId);
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return users.values().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) {
        findUserById(userId);
        users.remove(userId);
    }

    private void checkEmailUniqueness(String email) {
        boolean emailExists = users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
        if (emailExists) {
            throw new ConflictException("Email уже существует");
        }
    }
}
