package io.davlac.checkoutsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@EnableJpaAuditing
@Configuration
public class ApplicationConfig {

    @Bean
    public Docket demoApi() {
        // remove basic error controller from swagger
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("io.davlac.checkoutsystem"))
                .build();
    }

}
