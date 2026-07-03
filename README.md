# Chun-Chi — LLM Agent Harness

A web GUI for managing local LLM agents powered by **qwen3** (via [Ollama](https://ollama.com)).  
Built with **Vue 3** (frontend) and **Spring Boot 3** (backend).

## Features

- 🤖 **Agent management** — create, configure, pause, resume, and stop LLM agents
- 🔄 **Agent cycle tracking** — run prompt→response cycles and browse full history
- 💬 **Direct LLM chat** — send messages to any locally available Ollama model
- 📊 **Dashboard** — real-time overview of agent statuses
- 🗂 **OpenAPI / Swagger UI** — interactive API docs at `http://localhost:8080/swagger-ui.html`

## Architecture

```
chun-chi/
├── backend/          # Spring Boot 3 + Java 21 REST API
│   ├── src/main/java/com/chunchi/
│   │   ├── agent/    # Agent & AgentCycle domain (JPA, Service, Controller)
│   │   ├── llm/      # Ollama HTTP client & LLM service
│   │   └── config/   # CORS, OpenAPI, WebClient, error handling
│   └── pom.xml
├── frontend/         # Vue 3 + Vite + Pinia + Vue Router SPA
│   ├── src/
│   │   ├── api/      # Axios API clients
│   │   ├── stores/   # Pinia stores (agents, cycles, llm)
│   │   ├── views/    # Dashboard, Agents, AgentDetail, LlmChat
│   │   └── components/  # AgentCard, CycleCard, StatCard, AgentForm
│   └── vite.config.js
└── docker-compose.yml
```

## Quick Start

### Prerequisites

- Java 21+
- Node.js 22+
- [Ollama](https://ollama.com) running locally with the qwen3 model pulled:
  ```bash
  ollama pull qwen3:latest
  ```

### Run locally (development)

**Backend:**
```bash
cd backend
JAVA_HOME=/path/to/jdk21 mvn spring-boot:run
# API available at http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
# H2 console: http://localhost:8080/h2-console
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
# App available at http://localhost:5173
```

### Run with Docker Compose

```bash
docker-compose up --build
```

Then pull the model inside the Ollama container:
```bash
docker exec -it chun-chi-ollama ollama pull qwen3:latest
```

| Service  | URL                                    |
|----------|----------------------------------------|
| Frontend | http://localhost:5173                  |
| Backend  | http://localhost:8080                  |
| Swagger  | http://localhost:8080/swagger-ui.html  |
| Ollama   | http://localhost:11434                 |

## API Overview

| Method | Path                                    | Description              |
|--------|-----------------------------------------|--------------------------|
| GET    | `/api/agents`                           | List all agents          |
| POST   | `/api/agents`                           | Create an agent          |
| GET    | `/api/agents/{id}`                      | Get agent details        |
| PUT    | `/api/agents/{id}`                      | Update an agent          |
| DELETE | `/api/agents/{id}`                      | Delete an agent          |
| PATCH  | `/api/agents/{id}/pause`                | Pause agent              |
| PATCH  | `/api/agents/{id}/resume`               | Resume agent             |
| PATCH  | `/api/agents/{id}/stop`                 | Stop agent               |
| GET    | `/api/agents/{id}/cycles`               | List agent cycles        |
| POST   | `/api/agents/{id}/cycles`               | Run a new cycle          |
| POST   | `/api/agents/{id}/cycles/{cid}/cancel`  | Cancel a running cycle   |
| GET    | `/api/llm/models`                       | List available models    |
| POST   | `/api/llm/chat`                         | Direct LLM chat          |

## Configuration

Backend configuration is in `backend/src/main/resources/application.yml`:

```yaml
llm:
  ollama:
    base-url: http://localhost:11434   # Ollama endpoint
    model: qwen3:latest                # Default model
    timeout-seconds: 120
```

Frontend env in `frontend/.env`:
```
VITE_API_BASE_URL=http://localhost:8080/api
```

## Running Tests

```bash
# Backend
cd backend
JAVA_HOME=/path/to/jdk21 mvn test

# Frontend (build verification)
cd frontend
npm run build
```
