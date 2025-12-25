package ru.redcarpet.springdoc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public Info apiInfo() {
        return new Info()
        .title("Aston-application")
        .description("Docs for application");
    }
}
