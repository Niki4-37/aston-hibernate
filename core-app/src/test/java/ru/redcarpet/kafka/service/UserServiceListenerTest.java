package ru.redcarpet.kafka.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import ru.redcarpet.email.EmailService;
import ru.redcarpet.kafka.dto.KafkaUser;
import ru.redcarpet.kafka.enums.OperationType;
import ru.redcarpet.util.AppConst;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { AppConst.TOPIC })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceListenerTest {
    
    @Autowired
    KafkaTemplate<String, KafkaUser> kafkaTemplate;
    @MockitoBean
    EmailService service;

    KafkaUser testUser;

    @BeforeEach
    void setUp() {
        testUser = new KafkaUser(
            "none", 
            "lightning@example.com", 
            1L, 
            Instant.now()
        );
    }

    @ParameterizedTest(name = "{index} => operation={0}, subject={1}, body={2}")
    @MethodSource(value = "sendlerProvider")
    void sendEmailByOperation(String operation, String subject, String body) {
        testUser.setOperation(operation);

        CompletableFuture<Void> future = new CompletableFuture<>();

        doAnswer(invocation -> {
            future.complete(null);
            return null;
        }).when(service).sendEmail(anyString(), anyString(), anyString());

        kafkaTemplate.send(
            new ProducerRecord<String,KafkaUser>(
                AppConst.TOPIC, 
                testUser.getUserId().toString(), 
                testUser
        ));

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            throw new RuntimeException("Thread was interrupted while waiting for email", e);
        }

        verify(service).sendEmail(
            eq(testUser.getEmail()),
            eq(subject),
            contains(body));
    }

    static Stream<Arguments> sendlerProvider() {
        return Stream.of(
            Arguments.of(OperationType.CREATE.toString(), "CREATE account", "Your account on our website has been successfully created"),
            Arguments.of(OperationType.DELETE.toString(), "DELETE account", "Your account has been deleted")
        );
    }
}
