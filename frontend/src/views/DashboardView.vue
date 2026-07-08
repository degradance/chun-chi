<template>
  <div class="dashboard">
    <h1>Dashboard</h1>

    <div class="stats-grid">
      <StatCard label="Total Agents" :value="agents.length" icon="🤖" />
      <StatCard label="Running" :value="runningAgents.length" icon="▶️" color="green" />
      <StatCard label="Idle" :value="idleAgents.length" icon="⏸️" color="blue" />
      <StatCard
        label="Error"
        :value="errorAgents.length"
        icon="⚠️"
        color="red"
      />
    </div>

    <div class="section">
      <h2>Running Agents</h2>
      <p v-if="!runningAgents.length" class="empty">No agents currently running.</p>
      <AgentCard
        v-for="agent in runningAgents"
        :key="agent.id"
        :agent="agent"
        @pause="agentStore.pauseAgent(agent.id)"
        @stop="agentStore.stopAgent(agent.id)"
      />
    </div>

    <div class="section">
      <h2>Recent Agents</h2>
      <p v-if="loading" class="empty">Loading…</p>
      <p v-else-if="!agents.length" class="empty">
        No agents yet.
        <RouterLink to="/agents">Create one →</RouterLink>
      </p>
      <AgentCard
        v-for="agent in agents.slice(0, 5)"
        :key="agent.id"
        :agent="agent"
        @pause="agentStore.pauseAgent(agent.id)"
        @resume="agentStore.resumeAgent(agent.id)"
        @stop="agentStore.stopAgent(agent.id)"
      />
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useAgentStore } from '../stores/agents.js'
import StatCard from '../components/StatCard.vue'
import AgentCard from '../components/AgentCard.vue'

const agentStore = useAgentStore()
const { agents, loading, idleAgents, runningAgents } = agentStore

const errorAgents = computed(() => agentStore.agents.filter((a) => a.status === 'ERROR'))

onMounted(() => agentStore.fetchAgents())
</script>

<style scoped>
.dashboard { max-width: 960px; margin: 0 auto; }
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}
.section { margin-bottom: 2rem; }
.section h2 { margin-bottom: 0.75rem; }
.empty { color: var(--color-text-muted); }
</style>
