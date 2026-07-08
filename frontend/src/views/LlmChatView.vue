<template>
  <div class="llm-chat">
    <div class="chat-header">
      <h1>LLM Chat</h1>
      <div class="model-select">
        <label for="model-sel">Model:</label>
        <select id="model-sel" v-model="llmStore.selectedModel" :disabled="!llmStore.models.length">
          <option v-if="!llmStore.models.length" value="">Loading models…</option>
          <option v-for="m in llmStore.models" :key="m.name" :value="m.name">{{ m.name }}</option>
        </select>
        <button class="btn-sm" @click="llmStore.clearHistory()">Clear</button>
      </div>
    </div>

    <div class="chat-body" ref="chatBodyRef">
      <div
        v-for="(msg, idx) in llmStore.chatHistory"
        :key="idx"
        class="message"
        :class="msg.role"
      >
        <span class="role-label">{{ roleLabel(msg.role) }}</span>
        <pre class="content">{{ msg.content }}</pre>
      </div>
      <div v-if="llmStore.loading" class="message assistant loading-msg">
        <span class="role-label">Assistant</span>
        <span class="content typing">▋</span>
      </div>
      <div v-if="!llmStore.chatHistory.length && !llmStore.loading" class="chat-empty">
        Start a conversation with <strong>{{ llmStore.selectedModel || 'the model' }}</strong>.
      </div>
    </div>

    <div class="chat-input-row">
      <textarea
        v-model="inputText"
        class="chat-input"
        placeholder="Type a message… (Shift+Enter for newline)"
        rows="3"
        @keydown.enter.exact.prevent="handleSend"
      ></textarea>
      <button
        class="btn btn-primary send-btn"
        :disabled="llmStore.loading || !inputText.trim()"
        @click="handleSend"
      >
        Send
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue'
import { useLlmStore } from '../stores/llm.js'

const llmStore = useLlmStore()
const inputText = ref('')
const chatBodyRef = ref(null)

onMounted(() => llmStore.fetchModels())

async function handleSend() {
  const msg = inputText.value.trim()
  if (!msg || llmStore.loading) return
  inputText.value = ''
  await llmStore.sendMessage(msg)
}

watch(
  () => llmStore.chatHistory.length,
  async () => {
    await nextTick()
    if (chatBodyRef.value) {
      chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight
    }
  },
)

function roleLabel(role) {
  return { user: 'You', assistant: 'Assistant', error: 'Error' }[role] ?? role
}
</script>

<style scoped>
.llm-chat {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 120px);
}
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}
.chat-header h1 { margin: 0; }
.model-select { display: flex; align-items: center; gap: 0.5rem; }
.model-select select { padding: 4px 8px; border-radius: 4px; border: 1px solid #ccc; }
.btn-sm { background: none; border: 1px solid #ccc; border-radius: 4px; padding: 2px 8px; cursor: pointer; font-size: 0.85rem; }
.btn-sm:hover { background: #f0f0f0; }

.chat-body {
  flex: 1;
  overflow-y: auto;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 1rem;
  background: #fafafa;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}
.chat-empty { text-align: center; color: #888; margin: auto; }
.message { display: flex; flex-direction: column; max-width: 85%; }
.message.user { align-self: flex-end; }
.message.assistant { align-self: flex-start; }
.message.error { align-self: flex-start; }
.role-label { font-size: 0.7rem; font-weight: 600; text-transform: uppercase; color: #888; margin-bottom: 2px; }
.message.user .role-label { text-align: right; }
.content {
  margin: 0;
  padding: 0.5rem 0.75rem;
  border-radius: 8px;
  white-space: pre-wrap;
  font-family: inherit;
  font-size: 0.95rem;
  line-height: 1.5;
}
.message.user .content { background: #3b82f6; color: #fff; border-bottom-right-radius: 2px; }
.message.assistant .content { background: #fff; border: 1px solid #e0e0e0; border-bottom-left-radius: 2px; }
.message.error .content { background: #fef2f2; border: 1px solid #fca5a5; color: #b91c1c; }
.typing { animation: blink 1s step-end infinite; }
@keyframes blink { 50% { opacity: 0; } }

.chat-input-row {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.75rem;
}
.chat-input {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-family: inherit;
  font-size: 0.95rem;
  resize: none;
}
.send-btn { align-self: flex-end; }
</style>
