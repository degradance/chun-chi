<template>
  <div class="agents-view">
    <div class="header-row">
      <h1>Agents</h1>
      <button class="btn btn-primary" @click="showCreateModal = true">+ New Agent</button>
    </div>

    <p v-if="loading" class="empty">Loading…</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <p v-else-if="!agents.length" class="empty">No agents yet. Create your first agent.</p>

    <div v-else class="agent-list">
      <AgentCard
        v-for="agent in agents"
        :key="agent.id"
        :agent="agent"
        :show-link="true"
        @pause="handlePause(agent)"
        @resume="handleResume(agent)"
        @stop="handleStop(agent)"
        @delete="handleDelete(agent)"
      />
    </div>

    <!-- Create Agent Modal -->
    <Teleport to="body">
      <div v-if="showCreateModal" class="modal-overlay" @click.self="showCreateModal = false">
        <div class="modal">
          <h2>New Agent</h2>
          <AgentForm @submit="handleCreate" @cancel="showCreateModal = false" />
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useAgentStore } from '../stores/agents.js'
import AgentCard from '../components/AgentCard.vue'
import AgentForm from '../components/AgentForm.vue'

const agentStore = useAgentStore()
const { agents, loading, error } = agentStore
const showCreateModal = ref(false)

onMounted(() => agentStore.fetchAgents())

async function handleCreate(payload) {
  await agentStore.createAgent(payload)
  showCreateModal.value = false
}

async function handleDelete(agent) {
  if (confirm(`Delete agent "${agent.name}"?`)) {
    await agentStore.deleteAgent(agent.id)
  }
}

const handlePause = (a) => agentStore.pauseAgent(a.id)
const handleResume = (a) => agentStore.resumeAgent(a.id)
const handleStop = (a) => agentStore.stopAgent(a.id)
</script>

<style scoped>
.agents-view { max-width: 960px; margin: 0 auto; }
.header-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
.agent-list { display: flex; flex-direction: column; gap: 0.75rem; }
.empty, .error { color: var(--color-text-muted); }
.error { color: #e74c3c; }
</style>
