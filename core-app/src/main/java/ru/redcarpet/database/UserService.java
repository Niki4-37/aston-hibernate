package ru.redcarpet.database;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import ru.redcarpet.util.AppConst;
import ru.redcarpet.database.dto.UserDto;
import ru.redcarpet.database.entity.User;
import ru.redcarpet.database.mapper.UserMapper;
import ru.redcarpet.database.repository.UserRepository;
import ru.redcarpet.kafka.dto.KafkaUser;
import ru.redcarpet.kafka.enums.OperationType;

import java.time.Instant;
import java.time.LocalDate;

@Service
public class UserService {

    private final UserRepository repo;
    private final UserMapper mapper;
    private final KafkaTemplate<String, KafkaUser> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository repo, UserMapper mapper, KafkaTemplate<String, KafkaUser> kafkaTemplate) {
        this.repo = repo;
        this.mapper = mapper;
        this.kafkaTemplate = kafkaTemplate;
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
        sendToKafka(OperationType.CREATE, savedUser);
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
        sendToKafka(OperationType.DELETE, deletedUser);
        return deletedUser;
    }

    private void sendToKafka(OperationType type, UserDto user) {
        var kafkaUser = new KafkaUser(type.toString(), user.email(), user.id(), Instant.now());

        kafkaTemplate.send(AppConst.TOPIC, String.valueOf(user.id()), kafkaUser)
            .thenAccept(result -> {
            RecordMetadata meta = result.getRecordMetadata();
            log.info("Kafka: {} event sent (partition {} offset {})",
                    type.toString(), meta.partition(), meta.offset());})
            .exceptionally(ex -> {
                log.error(("Kafka: failed to send {} event, cause {}"), type.toString(), ex.getCause());
                return null;
        });        
    }
}
