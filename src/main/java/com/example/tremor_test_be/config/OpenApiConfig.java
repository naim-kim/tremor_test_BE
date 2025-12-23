package com.example.tremor_test_be.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tremorTestOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tremor Test API")
                        .description("API for managing users and tremor test results")
                        .version("v1.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("Swagger UI")
                        .url("/swagger-ui/index.html"));
    }
}


