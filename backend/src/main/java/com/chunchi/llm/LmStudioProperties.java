package com.chunchi.llm;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "llm.lmstudio")
@ConditionalOnProperty(name = "llm.provider", havingValue = "lmstudio")
@Getter
@Setter
public class LmStudioProperties {
    private String baseUrl = "http://localhost:1234";
    private String model = "qwen3";
    private int timeoutSeconds = 120;
}
