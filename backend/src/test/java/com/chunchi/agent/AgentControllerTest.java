package com.chunchi.agent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
        "llm.ollama.base-url=http://localhost:11434"
})
class AgentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgentRepository agentRepository;

    @BeforeEach
    void setUp() {
        agentRepository.deleteAll();
    }

    @Test
    void createAgent_returnsCreated() throws Exception {
        mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "test-agent",
                                  "model": "qwen3:latest",
                                  "systemPrompt": "You are a helpful assistant."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test-agent"))
                .andExpect(jsonPath("$.status").value("IDLE"))
                .andExpect(jsonPath("$.totalCycles").value(0));
    }

    @Test
    void createAgent_duplicateName_returnsBadRequest() throws Exception {
        String body = """
                {"name": "dup-agent", "model": "qwen3:latest"}
                """;
        mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listAgents_empty_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/agents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAgent_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/agents/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAgent_returnsNoContent() throws Exception {
        // First create an agent
        String createResponse = mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "delete-me", "model": "qwen3:latest"}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extract the id using simple string manipulation
        long id = Long.parseLong(createResponse.replaceAll(".*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(delete("/api/agents/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/agents/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAgentStatus_pause_returnsUpdated() throws Exception {
        String createResponse = mockMvc.perform(post("/api/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "status-agent", "model": "qwen3:latest"}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long id = Long.parseLong(createResponse.replaceAll(".*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(patch("/api/agents/" + id + "/pause"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAUSED"));
    }
}
