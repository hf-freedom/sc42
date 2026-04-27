import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Books from '../views/Books.vue'
import Users from '../views/Users.vue'
import Borrows from '../views/Borrows.vue'
import Reservations from '../views/Reservations.vue'
import Fines from '../views/Fines.vue'
import Compensations from '../views/Compensations.vue'
import FlowLogs from '../views/FlowLogs.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { title: '首页' }
  },
  {
    path: '/books',
    name: 'Books',
    component: Books,
    meta: { title: '图书管理' }
  },
  {
    path: '/users',
    name: 'Users',
    component: Users,
    meta: { title: '用户管理' }
  },
  {
    path: '/borrows',
    name: 'Borrows',
    component: Borrows,
    meta: { title: '借阅管理' }
  },
  {
    path: '/reservations',
    name: 'Reservations',
    component: Reservations,
    meta: { title: '预约管理' }
  },
  {
    path: '/fines',
    name: 'Fines',
    component: Fines,
    meta: { title: '罚金管理' }
  },
  {
    path: '/compensations',
    name: 'Compensations',
    component: Compensations,
    meta: { title: '赔偿管理' }
  },
  {
    path: '/flow-logs',
    name: 'FlowLogs',
    component: FlowLogs,
    meta: { title: '流水记录' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 图书馆管理系统` : '图书馆管理系统'
  next()
})

export default router