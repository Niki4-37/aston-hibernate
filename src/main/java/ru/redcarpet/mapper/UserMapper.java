package ru.redcarpet.mapper;

import ru.redcarpet.dto.UserDto;
import ru.redcarpet.entity.User;

public final class UserMapper {

    public User toEntity(UserDto userDto) {
        return new User(
            userDto.id(),
            userDto.name(),
            userDto.email(),
            userDto.birthDate(),
            userDto.createdAt()
        );
    }

    public UserDto toDTO(User user) {
        return new UserDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getBirthDate(),
            user.getCreatedAt()
        );
    }

}
