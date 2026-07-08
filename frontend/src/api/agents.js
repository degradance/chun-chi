import api from './index.js'

export const agentsApi = {
  list: () => api.get('/agents'),
  get: (id) => api.get(`/agents/${id}`),
  create: (data) => api.post('/agents', data),
  update: (id, data) => api.put(`/agents/${id}`, data),
  remove: (id) => api.delete(`/agents/${id}`),
  pause: (id) => api.patch(`/agents/${id}/pause`),
  resume: (id) => api.patch(`/agents/${id}/resume`),
  stop: (id) => api.patch(`/agents/${id}/stop`),
  updateStatus: (id, status) => api.patch(`/agents/${id}/status`, null, { params: { status } }),
}

export const cyclesApi = {
  list: (agentId, params) => api.get(`/agents/${agentId}/cycles`, { params }),
  get: (agentId, cycleId) => api.get(`/agents/${agentId}/cycles/${cycleId}`),
  run: (agentId, prompt) => api.post(`/agents/${agentId}/cycles`, { prompt }),
  cancel: (agentId, cycleId) => api.post(`/agents/${agentId}/cycles/${cycleId}/cancel`),
}

export const llmApi = {
  listModels: () => api.get('/llm/models'),
  chat: (model, systemMessage, userMessage) =>
    api.post('/llm/chat', { model, systemMessage, userMessage }),
}
