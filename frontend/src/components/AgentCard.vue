<template>
  <div class="agent-card">
    <div class="agent-info">
      <div class="agent-name-row">
        <RouterLink v-if="showLink" :to="`/agents/${agent.id}`" class="agent-name">
          {{ agent.name }}
        </RouterLink>
        <span v-else class="agent-name">{{ agent.name }}</span>
        <span class="badge" :class="statusClass">{{ agent.status }}</span>
      </div>
      <div class="agent-meta">
        <span class="model-tag">{{ agent.model }}</span>
        <span class="cycles-count">{{ agent.totalCycles }} cycle{{ agent.totalCycles !== 1 ? 's' : '' }}</span>
      </div>
    </div>

    <div class="agent-actions">
      <button
        v-if="agent.status === 'IDLE' || agent.status === 'PAUSED'"
        class="btn-action"
        title="Run a cycle"
        @click="$router.push(`/agents/${agent.id}`)"
      >▶</button>
      <button
        v-if="agent.status === 'RUNNING'"
        class="btn-action"
        title="Pause"
        @click="$emit('pause')"
      >⏸</button>
      <button
        v-if="agent.status === 'PAUSED'"
        class="btn-action"
        title="Resume"
        @click="$emit('resume')"
      >▶</button>
      <button
        v-if="agent.status !== 'STOPPED'"
        class="btn-action btn-danger-sm"
        title="Stop"
        @click="$emit('stop')"
      >⏹</button>
      <button
        v-if="showLink"
        class="btn-action btn-danger-sm"
        title="Delete"
        @click="$emit('delete')"
      >🗑</button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  agent: { type: Object, required: true },
  showLink: { type: Boolean, default: false },
})

defineEmits(['pause', 'resume', 'stop', 'delete'])

const statusClass = computed(() => ({
  IDLE: 'badge-blue',
  RUNNING: 'badge-green',
  PAUSED: 'badge-yellow',
  ERROR: 'badge-red',
  STOPPED: 'badge-gray',
}[props.agent.status] ?? 'badge-gray'))
</script>

<style scoped>
.agent-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}
.agent-info { flex: 1; min-width: 0; }
.agent-name-row { display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.2rem; }
.agent-name { font-weight: 600; font-size: 1rem; text-decoration: none; color: inherit; }
a.agent-name:hover { color: #3b82f6; }
.agent-meta { display: flex; gap: 0.75rem; font-size: 0.8rem; color: #666; }
.model-tag { background: #f0f0f0; padding: 1px 6px; border-radius: 4px; }
.agent-actions { display: flex; gap: 0.25rem; margin-left: 1rem; }
.btn-action {
  background: none;
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 4px 8px;
  cursor: pointer;
  font-size: 0.9rem;
  line-height: 1;
}
.btn-action:hover { background: #f0f0f0; }
.btn-danger-sm { border-color: #fca5a5; }
.btn-danger-sm:hover { background: #fef2f2; }
</style>
