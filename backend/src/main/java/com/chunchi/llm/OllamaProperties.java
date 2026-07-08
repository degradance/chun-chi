package com.chunchi.llm;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "llm.ollama")
@Getter
@Setter
public class OllamaProperties {
    private String baseUrl = "http://localhost:11434";
    private String model = "qwen3:latest";
    private int timeoutSeconds = 120;
}
