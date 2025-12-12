package ru.redcarpet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ru.redcarpet.dao.UserRepository;
import ru.redcarpet.dto.UserDto;
import ru.redcarpet.entity.User;
import ru.redcarpet.exception.AppException;
import ru.redcarpet.mapper.UserMapper;

@Service
public class UserService {

    private final UserRepository repo;
    private final UserMapper mapper;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository repo, UserMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public UserDto getUserById(Long id) {
        User userEntity = repo.findById(id)
            .orElseThrow(() -> new AppException("Can't find user with such ID:" + id)); 

        return mapper.toDTO(userEntity);
    }

    public UserDto createUser(UserDto userDto) {
        User entityToSave = mapper.toEntity(userDto);
        entityToSave.setId(null);
        User savedEntity = null;
        try {
            savedEntity = repo.save(entityToSave);
            log.info("created user with id = {}", savedEntity.getId());
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.info("Can't create user with same e-mail");
            throw new AppException("Can't create user. User with this e-mail already exists", e);
        }
        return mapper.toDTO(savedEntity);
    }

    public UserDto updateUser(Long id, UserDto updatedUserDto) {
        UserDto userFromDb = getUserById(id);
        User entityToSave = mapper.toEntity(updatedUserDto);
        entityToSave.setId(id);
        entityToSave.setCreatedAt(userFromDb.createdAt());
        User updatedEntity = repo.save(entityToSave);
        log.info("updated user with id = {}", updatedEntity.getId());
        return mapper.toDTO(updatedEntity);
    }

    public UserDto deleteUser(Long id) {
        var deletedUser = getUserById(id);
        repo.deleteById(id);
        log.info("deleted user with id = {}", deletedUser.id());
        return deletedUser;
    }
}
