package io.davlac.checkoutsystem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@TestConfiguration
public class TestConfig {

    @Bean
    public ObjectMapper objectMapperWithTimeModule() {
        // to deserialize Instant/Date objects
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public Validator validator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }

}
