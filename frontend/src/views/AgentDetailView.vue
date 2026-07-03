<template>
  <div class="agent-detail">
    <div class="back-row">
      <RouterLink to="/agents">← Back to Agents</RouterLink>
    </div>

    <div v-if="!agent" class="empty">
      <p v-if="loadingAgent">Loading agent…</p>
      <p v-else>Agent not found.</p>
    </div>

    <template v-else>
      <div class="agent-header">
        <div>
          <h1>{{ agent.name }}</h1>
          <span class="badge" :class="statusClass(agent.status)">{{ agent.status }}</span>
          <span class="model-tag">{{ agent.model }}</span>
        </div>
        <div class="controls">
          <button v-if="agent.status === 'RUNNING'" class="btn" @click="agentStore.pauseAgent(agent.id)">⏸ Pause</button>
          <button v-if="agent.status === 'PAUSED'" class="btn btn-primary" @click="agentStore.resumeAgent(agent.id)">▶ Resume</button>
          <button v-if="agent.status !== 'STOPPED'" class="btn btn-danger" @click="agentStore.stopAgent(agent.id)">⏹ Stop</button>
        </div>
      </div>

      <div v-if="agent.systemPrompt" class="system-prompt-box">
        <strong>System Prompt:</strong>
        <pre>{{ agent.systemPrompt }}</pre>
      </div>

      <!-- Run a cycle -->
      <div class="run-section">
        <h2>Run Cycle #{{ (cycles.length ?? 0) + 1 }}</h2>
        <textarea
          v-model="promptInput"
          class="prompt-input"
          placeholder="Enter your prompt…"
          rows="4"
        ></textarea>
        <button
          class="btn btn-primary"
          :disabled="cycleStore.loading || !promptInput.trim()"
          @click="handleRunCycle"
        >
          {{ cycleStore.loading ? 'Running…' : 'Run Cycle' }}
        </button>
        <p v-if="cycleStore.error" class="error">{{ cycleStore.error }}</p>
      </div>

      <!-- Cycle history -->
      <div class="cycles-section">
        <div class="cycles-header">
          <h2>Cycle History ({{ cycles.length }})</h2>
          <button class="btn-sm" @click="cycleStore.fetchCycles(props.id)">↺ Refresh</button>
        </div>
        <p v-if="cycleStore.loading && !cycles.length" class="empty">Loading cycles…</p>
        <p v-else-if="!cycles.length" class="empty">No cycles yet.</p>
        <CycleCard
          v-for="cycle in cycles"
          :key="cycle.id"
          :cycle="cycle"
          @cancel="cycleStore.cancelCycle(props.id, cycle.id)"
        />
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAgentStore, useCycleStore } from '../stores/agents.js'
import CycleCard from '../components/CycleCard.vue'

const props = defineProps({ id: { type: String, required: true } })

const agentStore = useAgentStore()
const cycleStore = useCycleStore()

const loadingAgent = ref(true)
const promptInput = ref('')

const agent = computed(() => agentStore.agents.find((a) => a.id === Number(props.id)))
const cycles = computed(() => cycleStore.cyclesFor(Number(props.id)))

onMounted(async () => {
  await agentStore.fetchAgents()
  await cycleStore.fetchCycles(Number(props.id))
  loadingAgent.value = false
})

async function handleRunCycle() {
  if (!promptInput.value.trim()) return
  await cycleStore.runCycle(Number(props.id), promptInput.value.trim())
  await agentStore.fetchAgents() // refresh status
  promptInput.value = ''
}

function statusClass(status) {
  return {
    IDLE: 'badge-blue',
    RUNNING: 'badge-green',
    PAUSED: 'badge-yellow',
    ERROR: 'badge-red',
    STOPPED: 'badge-gray',
  }[status] ?? 'badge-gray'
}
</script>

<style scoped>
.agent-detail { max-width: 900px; margin: 0 auto; }
.back-row { margin-bottom: 1rem; }
.agent-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1rem; }
.agent-header h1 { margin: 0 0 0.25rem; }
.controls { display: flex; gap: 0.5rem; }
.model-tag { font-size: 0.75rem; background: #f0f0f0; padding: 2px 8px; border-radius: 4px; margin-left: 0.5rem; }
.system-prompt-box { background: #f8f8f8; border: 1px solid #e0e0e0; border-radius: 6px; padding: 0.75rem 1rem; margin-bottom: 1.5rem; }
.system-prompt-box pre { margin: 0.25rem 0 0; white-space: pre-wrap; font-size: 0.9rem; }
.run-section { margin-bottom: 2rem; }
.run-section h2 { margin-bottom: 0.5rem; }
.prompt-input { width: 100%; padding: 0.5rem; border: 1px solid #ccc; border-radius: 6px; font-family: inherit; font-size: 0.95rem; resize: vertical; box-sizing: border-box; margin-bottom: 0.5rem; }
.cycles-section { margin-top: 2rem; }
.cycles-header { display: flex; align-items: center; gap: 1rem; margin-bottom: 0.75rem; }
.btn-sm { background: none; border: 1px solid #ccc; border-radius: 4px; padding: 2px 8px; cursor: pointer; font-size: 0.85rem; }
.btn-sm:hover { background: #f0f0f0; }
.empty { color: var(--color-text-muted); }
.error { color: #e74c3c; }
</style>
