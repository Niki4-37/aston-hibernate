package ru.redcarpet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@SpringBootApplication
@OpenAPIDefinition
public class HibernateRunner{

    public static void main(String[] args) {
        SpringApplication.run(HibernateRunner.class, args);        
    }
}