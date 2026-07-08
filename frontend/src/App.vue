<template>
  <div class="app-layout">
    <nav class="sidebar">
      <div class="sidebar-logo">
        <span class="logo-icon">🧠</span>
        <span class="logo-text">Chun-Chi</span>
        <span class="logo-sub">LLM Harness</span>
      </div>
      <ul class="nav-links">
        <li>
          <RouterLink to="/" exact-active-class="active">
            <span>📊</span> Dashboard
          </RouterLink>
        </li>
        <li>
          <RouterLink to="/agents" active-class="active">
            <span>🤖</span> Agents
          </RouterLink>
        </li>
        <li>
          <RouterLink to="/chat" active-class="active">
            <span>💬</span> LLM Chat
          </RouterLink>
        </li>
      </ul>
      <div class="sidebar-footer">
        <span class="model-indicator">qwen3 · Ollama</span>
      </div>
    </nav>

    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<style>
/* Global resets & tokens */
*, *::before, *::after { box-sizing: border-box; }

:root {
  --color-bg: #f5f7fa;
  --color-sidebar: #1e1e2e;
  --color-sidebar-text: #cdd6f4;
  --color-sidebar-active: #3b82f6;
  --color-text: #1a1a2e;
  --color-text-muted: #888;
  font-family: 'Inter', 'Segoe UI', system-ui, sans-serif;
}

body { margin: 0; background: var(--color-bg); color: var(--color-text); }

/* Badge helpers */
.badge { display: inline-block; font-size: 0.7rem; font-weight: 700; padding: 2px 7px; border-radius: 999px; text-transform: uppercase; letter-spacing: 0.5px; }
.badge-blue   { background: #dbeafe; color: #1d4ed8; }
.badge-green  { background: #dcfce7; color: #166534; }
.badge-yellow { background: #fef9c3; color: #854d0e; }
.badge-red    { background: #fee2e2; color: #991b1b; }
.badge-gray   { background: #f3f4f6; color: #374151; }

/* Button helpers */
.btn {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.4rem 0.9rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background: #fff;
  cursor: pointer;
  font-size: 0.9rem;
  font-family: inherit;
  transition: background 0.15s;
}
.btn:hover { background: #f9fafb; }
.btn-primary { background: #3b82f6; color: #fff; border-color: #3b82f6; }
.btn-primary:hover { background: #2563eb; }
.btn-primary:disabled { background: #93c5fd; border-color: #93c5fd; cursor: not-allowed; }
.btn-danger { background: #ef4444; color: #fff; border-color: #ef4444; }
.btn-danger:hover { background: #dc2626; }
</style>

<style scoped>
.app-layout { display: flex; min-height: 100vh; }

.sidebar {
  width: 220px;
  flex-shrink: 0;
  background: var(--color-sidebar);
  color: var(--color-sidebar-text);
  display: flex;
  flex-direction: column;
  padding: 1.25rem 0;
}

.sidebar-logo {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 1rem 1.5rem;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.logo-icon { font-size: 2rem; }
.logo-text { font-size: 1.1rem; font-weight: 700; margin-top: 0.25rem; }
.logo-sub { font-size: 0.7rem; opacity: 0.5; letter-spacing: 0.5px; }

.nav-links {
  list-style: none;
  margin: 1rem 0 0;
  padding: 0;
  flex: 1;
}
.nav-links li a {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  padding: 0.6rem 1.25rem;
  color: var(--color-sidebar-text);
  text-decoration: none;
  border-radius: 6px;
  margin: 0 0.5rem 0.2rem;
  font-size: 0.9rem;
  transition: background 0.15s;
  opacity: 0.75;
}
.nav-links li a:hover { background: rgba(255,255,255,0.08); opacity: 1; }
.nav-links li a.active { background: rgba(59,130,246,0.25); color: #93c5fd; opacity: 1; }

.sidebar-footer {
  padding: 1rem 1.25rem 0;
  border-top: 1px solid rgba(255,255,255,0.1);
  font-size: 0.72rem;
  opacity: 0.45;
  text-align: center;
}

.main-content {
  flex: 1;
  padding: 2rem;
  overflow-y: auto;
}

/* Modal styles (used via Teleport) */
:global(.modal-overlay) {
  position: fixed; inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex; align-items: center; justify-content: center;
  z-index: 1000;
}
:global(.modal) {
  background: #fff;
  border-radius: 10px;
  padding: 1.5rem 2rem;
  min-width: 420px;
  max-width: 90vw;
  box-shadow: 0 8px 32px rgba(0,0,0,0.18);
}
:global(.modal h2) { margin: 0 0 1.25rem; }
</style>
