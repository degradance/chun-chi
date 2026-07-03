<template>
  <div class="cycle-card" :class="`cycle-card--${cycle.status.toLowerCase()}`">
    <div class="cycle-header">
      <span class="cycle-num">#{{ cycle.cycleNumber }}</span>
      <span class="badge" :class="statusClass">{{ cycle.status }}</span>
      <span class="cycle-duration" v-if="cycle.durationMs">{{ cycle.durationMs }}ms</span>
      <span class="cycle-time">{{ formatTime(cycle.startedAt) }}</span>
      <button
        v-if="cycle.status === 'RUNNING'"
        class="btn-sm btn-danger-sm"
        @click="$emit('cancel')"
      >Cancel</button>
    </div>

    <div class="cycle-body">
      <div v-if="cycle.inputPrompt" class="cycle-section">
        <strong>Prompt</strong>
        <pre>{{ cycle.inputPrompt }}</pre>
      </div>
      <div v-if="cycle.rawResponse" class="cycle-section">
        <strong>Response</strong>
        <pre>{{ cycle.rawResponse }}</pre>
      </div>
      <div v-if="cycle.errorMessage" class="cycle-section error-section">
        <strong>Error</strong>
        <pre>{{ cycle.errorMessage }}</pre>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  cycle: { type: Object, required: true },
})
defineEmits(['cancel'])

const statusClass = computed(() => ({
  PENDING: 'badge-gray',
  RUNNING: 'badge-green',
  COMPLETED: 'badge-blue',
  FAILED: 'badge-red',
  CANCELLED: 'badge-yellow',
}[props.cycle.status] ?? 'badge-gray'))

function formatTime(iso) {
  if (!iso) return ''
  return new Date(iso).toLocaleTimeString()
}
</script>

<style scoped>
.cycle-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  margin-bottom: 0.75rem;
  overflow: hidden;
  background: #fff;
}
.cycle-card--failed { border-left: 3px solid #ef4444; }
.cycle-card--completed { border-left: 3px solid #22c55e; }
.cycle-card--running { border-left: 3px solid #3b82f6; }
.cycle-card--cancelled { border-left: 3px solid #eab308; }

.cycle-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 0.75rem;
  background: #f8f8f8;
  border-bottom: 1px solid #e0e0e0;
}
.cycle-num { font-weight: 700; font-size: 0.9rem; color: #444; }
.cycle-duration { font-size: 0.75rem; color: #888; }
.cycle-time { font-size: 0.75rem; color: #aaa; margin-left: auto; }

.cycle-body { padding: 0.75rem; display: flex; flex-direction: column; gap: 0.5rem; }
.cycle-section strong { display: block; font-size: 0.75rem; text-transform: uppercase; color: #888; margin-bottom: 2px; }
.cycle-section pre {
  margin: 0;
  white-space: pre-wrap;
  font-family: 'Fira Code', 'Cascadia Code', monospace;
  font-size: 0.85rem;
  background: #f4f4f4;
  padding: 0.4rem 0.6rem;
  border-radius: 4px;
  max-height: 300px;
  overflow-y: auto;
}
.error-section pre { background: #fef2f2; color: #b91c1c; }

.btn-sm { font-size: 0.8rem; padding: 2px 8px; border: 1px solid #ccc; border-radius: 4px; cursor: pointer; background: none; }
.btn-danger-sm { border-color: #fca5a5; color: #b91c1c; }
.btn-danger-sm:hover { background: #fef2f2; }
</style>
