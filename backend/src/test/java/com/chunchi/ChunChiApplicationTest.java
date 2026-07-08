package com.chunchi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "llm.provider=ollama",
        "llm.ollama.base-url=http://localhost:11434"
})
class ChunChiApplicationTest {

    @Test
    void contextLoads() {
        // Verifies the Spring application context starts successfully
    }
}
