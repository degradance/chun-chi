package com.chunchi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Chun-Chi – LLM Agent Harness API")
                        .description("REST API for managing local LLM agents and their execution cycles (qwen3 via Ollama or LM Studio)")
                        .version("0.1.0")
                        .license(new License().name("MIT")));
    }
}
