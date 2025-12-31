package ru.redcarpet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@SpringBootApplication
@OpenAPIDefinition
@EnableDiscoveryClient
public class HibernateRunner{

    public static void main(String[] args) {
        SpringApplication.run(HibernateRunner.class, args);        
    }
}