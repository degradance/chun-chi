import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from '../views/DashboardView.vue'
import AgentsView from '../views/AgentsView.vue'
import AgentDetailView from '../views/AgentDetailView.vue'
import LlmChatView from '../views/LlmChatView.vue'

const routes = [
  {
    path: '/',
    name: 'dashboard',
    component: DashboardView,
    meta: { title: 'Dashboard' },
  },
  {
    path: '/agents',
    name: 'agents',
    component: AgentsView,
    meta: { title: 'Agents' },
  },
  {
    path: '/agents/:id',
    name: 'agent-detail',
    component: AgentDetailView,
    meta: { title: 'Agent Detail' },
    props: true,
  },
  {
    path: '/chat',
    name: 'llm-chat',
    component: LlmChatView,
    meta: { title: 'LLM Chat' },
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.afterEach((to) => {
  document.title = to.meta.title ? `${to.meta.title} · Chun-Chi` : 'Chun-Chi'
})

export default router
