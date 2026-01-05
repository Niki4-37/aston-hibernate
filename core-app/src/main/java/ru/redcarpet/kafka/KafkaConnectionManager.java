package ru.redcarpet.kafka;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

import org.springframework.stereotype.Service;

@Service

public class KafkaConnectionManager {

    private final KafkaListenerEndpointRegistry registry;
    private final ApplicationEventPublisher publisher; 

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    private volatile boolean lastKnownStatus = true;
    private final ReentrantLock lock = new ReentrantLock();
    
    private static final Logger log = LoggerFactory.getLogger(KafkaConnectionManager.class);

    public KafkaConnectionManager(
        KafkaListenerEndpointRegistry registry, 
        ApplicationEventPublisher publisher
    ) {
        this.registry = registry;
        this.publisher = publisher;
    }

    public boolean checkKafkaAvailability() {
        
        log.info("bootstrap servers: {}", bootstrapServers);
        String[] parts = bootstrapServers.split(":");
        if (parts.length != 2) {
            log.error("Invalid bootstrap-servers format: {}", bootstrapServers);
            return false;
        }

        String host = parts[0];
        int port;
        try {
            port = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            log.error("Invalid port in bootstrap-servers: {}", parts[1]);
            return false;
        }

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000);
            return true;
        } catch (IOException e) {
            log.warn("Kafka broker not reachable: {}:{}", host, port);
            return false;
        }
    }

    public void updateListeners() {
        try {
            if (!lock.tryLock(1, TimeUnit.SECONDS)) {
                log.debug("Could not acquire lock. Skipping updateListeners().");
                return;
            }
            boolean currentStatus = checkKafkaAvailability();
            if (currentStatus != lastKnownStatus) {
                lastKnownStatus = currentStatus;
                publishStatusEvent(currentStatus);
                
                if (currentStatus) {
                    startListeners();
                } else {
                    stopListeners();
                }
            } 
        } catch (InterruptedException e) {
            log.warn("Interrupted while waiting for lock", e);
            return;
        } finally {
            lock.unlock();
        }
    }

    private void publishStatusEvent(boolean isAvailable) {
        publisher.publishEvent(new KafkaStatusEvent(this, isAvailable));
    }

    private void startListeners() { 
        registry.getListenerContainers().forEach(container -> {
            if (!container.isRunning()) {
                container.start();
                log.info("Kafka available. Listeners started.");
            }
        });    
    }

    private void stopListeners() {
        registry.getListenerContainers().forEach(container -> {
            if (container.isRunning()) {
                container.stop();
                log.warn("Kafka unavailable. Listeners stopped.");
            }
        });
    }
}
