<template>
  <div class="stat-card" :class="`stat-card--${color}`">
    <span class="icon">{{ icon }}</span>
    <div class="info">
      <span class="value">{{ value }}</span>
      <span class="label">{{ label }}</span>
    </div>
  </div>
</template>

<script setup>
defineProps({
  label: { type: String, required: true },
  value: { type: [Number, String], required: true },
  icon: { type: String, default: '📊' },
  color: { type: String, default: 'default' },
})
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 1.25rem;
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}
.icon { font-size: 1.75rem; }
.info { display: flex; flex-direction: column; }
.value { font-size: 1.6rem; font-weight: 700; line-height: 1; }
.label { font-size: 0.8rem; color: #666; margin-top: 2px; }
.stat-card--green { border-left: 4px solid #22c55e; }
.stat-card--blue  { border-left: 4px solid #3b82f6; }
.stat-card--red   { border-left: 4px solid #ef4444; }
.stat-card--yellow{ border-left: 4px solid #eab308; }
</style>
