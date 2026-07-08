<template>
  <form class="agent-form" @submit.prevent="handleSubmit">
    <div class="form-group">
      <label for="name">Name *</label>
      <input id="name" v-model="form.name" type="text" placeholder="my-agent" required />
    </div>

    <div class="form-group">
      <label for="model">Model *</label>
      <input id="model" v-model="form.model" type="text" placeholder="qwen3:latest" required />
    </div>

    <div class="form-group">
      <label for="system-prompt">System Prompt</label>
      <textarea
        id="system-prompt"
        v-model="form.systemPrompt"
        rows="5"
        placeholder="You are a helpful assistant…"
      ></textarea>
    </div>

    <p v-if="error" class="form-error">{{ error }}</p>

    <div class="form-actions">
      <button type="button" class="btn" @click="$emit('cancel')">Cancel</button>
      <button type="submit" class="btn btn-primary" :disabled="submitting">
        {{ submitting ? 'Creating…' : 'Create Agent' }}
      </button>
    </div>
  </form>
</template>

<script setup>
import { reactive, ref } from 'vue'

const emit = defineEmits(['submit', 'cancel'])

const form = reactive({ name: '', model: 'qwen3:latest', systemPrompt: '' })
const submitting = ref(false)
const error = ref(null)

async function handleSubmit() {
  submitting.value = true
  error.value = null
  try {
    await emit('submit', { ...form })
  } catch (e) {
    error.value = e.message
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.agent-form { display: flex; flex-direction: column; gap: 1rem; min-width: 360px; }
.form-group { display: flex; flex-direction: column; gap: 0.25rem; }
.form-group label { font-size: 0.85rem; font-weight: 600; color: #444; }
.form-group input,
.form-group textarea {
  padding: 0.4rem 0.6rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-family: inherit;
  font-size: 0.95rem;
}
.form-group textarea { resize: vertical; }
.form-error { color: #e74c3c; font-size: 0.85rem; }
.form-actions { display: flex; justify-content: flex-end; gap: 0.5rem; }
</style>
