import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { agentsApi, cyclesApi } from '../api/agents.js'

export const useAgentStore = defineStore('agents', () => {
  const agents = ref([])
  const loading = ref(false)
  const error = ref(null)

  const idleAgents = computed(() => agents.value.filter((a) => a.status === 'IDLE'))
  const runningAgents = computed(() => agents.value.filter((a) => a.status === 'RUNNING'))

  async function fetchAgents() {
    loading.value = true
    error.value = null
    try {
      const res = await agentsApi.list()
      agents.value = res.data
    } catch (e) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }

  async function createAgent(payload) {
    const res = await agentsApi.create(payload)
    agents.value.push(res.data)
    return res.data
  }

  async function updateAgent(id, payload) {
    const res = await agentsApi.update(id, payload)
    const idx = agents.value.findIndex((a) => a.id === id)
    if (idx !== -1) agents.value[idx] = res.data
    return res.data
  }

  async function deleteAgent(id) {
    await agentsApi.remove(id)
    agents.value = agents.value.filter((a) => a.id !== id)
  }

  async function pauseAgent(id) {
    const res = await agentsApi.pause(id)
    _updateLocal(res.data)
    return res.data
  }

  async function resumeAgent(id) {
    const res = await agentsApi.resume(id)
    _updateLocal(res.data)
    return res.data
  }

  async function stopAgent(id) {
    const res = await agentsApi.stop(id)
    _updateLocal(res.data)
    return res.data
  }

  function _updateLocal(agent) {
    const idx = agents.value.findIndex((a) => a.id === agent.id)
    if (idx !== -1) agents.value[idx] = agent
  }

  return {
    agents,
    loading,
    error,
    idleAgents,
    runningAgents,
    fetchAgents,
    createAgent,
    updateAgent,
    deleteAgent,
    pauseAgent,
    resumeAgent,
    stopAgent,
  }
})

export const useCycleStore = defineStore('cycles', () => {
  const cyclesByAgent = ref({})
  const loading = ref(false)
  const error = ref(null)

  function cyclesFor(agentId) {
    return cyclesByAgent.value[agentId] ?? []
  }

  async function fetchCycles(agentId) {
    loading.value = true
    error.value = null
    try {
      const res = await cyclesApi.list(agentId, { sort: 'cycleNumber,desc', size: 50 })
      cyclesByAgent.value[agentId] = res.data.content ?? res.data
    } catch (e) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }

  async function runCycle(agentId, prompt) {
    const res = await cyclesApi.run(agentId, prompt)
    const existing = cyclesByAgent.value[agentId] ?? []
    cyclesByAgent.value[agentId] = [res.data, ...existing]
    return res.data
  }

  async function cancelCycle(agentId, cycleId) {
    const res = await cyclesApi.cancel(agentId, cycleId)
    const list = cyclesByAgent.value[agentId] ?? []
    const idx = list.findIndex((c) => c.id === cycleId)
    if (idx !== -1) list[idx] = res.data
    return res.data
  }

  return { cyclesByAgent, loading, error, cyclesFor, fetchCycles, runCycle, cancelCycle }
})
