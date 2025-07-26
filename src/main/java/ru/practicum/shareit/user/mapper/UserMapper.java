package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


@Component
public class UserMapper {

    public UserDto toUserDto(User user) {
        if (user == null) return null;
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User toUser(UserDto dto) {
        if (dto == null) return null;
        return new User(dto.getId(), dto.getName(), dto.getEmail());
    }
}
