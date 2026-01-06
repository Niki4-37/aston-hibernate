package ru.redcarpet.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import ru.redcarpet.OperationType;
import ru.redcarpet.database.dto.UserDto;
import ru.redcarpet.database.entity.User;
import ru.redcarpet.database.mapper.UserMapper;
import ru.redcarpet.database.repository.UserRepository;

import ru.redcarpet.kafka.service.KafkaMessageService;

import java.time.LocalDate;

@Service
public class UserService {

    private final UserRepository repo;
    private final UserMapper mapper;
    private final KafkaMessageService messageService;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(
        UserRepository repo, 
        UserMapper mapper, 
        KafkaMessageService messageService
    ) {
        this.repo = repo;
        this.mapper = mapper;
        this.messageService = messageService;
    }

    public UserDto getUserById(Long id) {
        User userEntity = repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Can't find user with such ID:" + id)); 

        return mapper.toDTO(userEntity);
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        User entityToSave = mapper.toEntity(userDto);
        entityToSave.setId(null);
        entityToSave.setCreatedAt(LocalDate.now());
        User savedEntity = null;
        try {
            savedEntity = repo.save(entityToSave);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.info("Can't create user with same e-mail");
            throw e;
        }
        var savedUser = mapper.toDTO(savedEntity);
        messageService.sendToKafka(OperationType.CREATE, savedUser);
        return savedUser;
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

    @Transactional
    public UserDto deleteUser(Long id) {
        var deletedUser = getUserById(id);
        repo.deleteById(id);
        messageService.sendToKafka(OperationType.DELETE, deletedUser);
        return deletedUser;
    }
}
