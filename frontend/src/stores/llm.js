import { defineStore } from 'pinia'
import { ref } from 'vue'
import { llmApi } from '../api/agents.js'

export const useLlmStore = defineStore('llm', () => {
  const models = ref([])
  const chatHistory = ref([])
  const loading = ref(false)
  const error = ref(null)
  const selectedModel = ref('')

  async function fetchModels() {
    loading.value = true
    error.value = null
    try {
      const res = await llmApi.listModels()
      models.value = res.data
      if (models.value.length && !selectedModel.value) {
        selectedModel.value = models.value[0].name
      }
    } catch (e) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }

  async function sendMessage(userMessage, systemMessage = '') {
    const userEntry = { role: 'user', content: userMessage, ts: Date.now() }
    chatHistory.value.push(userEntry)
    loading.value = true
    error.value = null
    try {
      const res = await llmApi.chat(selectedModel.value, systemMessage, userMessage)
      const assistantEntry = { role: 'assistant', content: res.data.response, ts: Date.now() }
      chatHistory.value.push(assistantEntry)
      return assistantEntry
    } catch (e) {
      error.value = e.message
      const errEntry = { role: 'error', content: e.message, ts: Date.now() }
      chatHistory.value.push(errEntry)
    } finally {
      loading.value = false
    }
  }

  function clearHistory() {
    chatHistory.value = []
  }

  return { models, chatHistory, loading, error, selectedModel, fetchModels, sendMessage, clearHistory }
})
