<template>
  <el-container class="app-container">
    <el-header class="app-header">
      <div class="logo">
        <el-icon size="24"><Reading /></el-icon>
        <span>图书馆管理系统</span>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px" class="app-aside">
        <el-menu
          :default-active="activeMenu"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/books">
            <el-icon><Notebook /></el-icon>
            <span>图书管理</span>
          </el-menu-item>
          <el-menu-item index="/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/borrows">
            <el-icon><Document /></el-icon>
            <span>借阅管理</span>
          </el-menu-item>
          <el-menu-item index="/reservations">
            <el-icon><Clock /></el-icon>
            <span>预约管理</span>
          </el-menu-item>
          <el-menu-item index="/fines">
            <el-icon><Money /></el-icon>
            <span>罚金管理</span>
          </el-menu-item>
          <el-menu-item index="/compensations">
            <el-icon><Warning /></el-icon>
            <span>赔偿管理</span>
          </el-menu-item>
          <el-menu-item index="/flow-logs">
            <el-icon><List /></el-icon>
            <span>流水记录</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  HomeFilled,
  Notebook,
  User,
  Document,
  Clock,
  Money,
  Warning,
  List,
  Reading
} from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.app-container {
  height: 100vh;
}

.app-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  padding: 0 20px;
  color: white;
}

.app-header .logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: bold;
}

.app-aside {
  background-color: #304156;
}

.app-main {
  background-color: #f0f2f5;
  padding: 20px;
}

.page-card {
  margin-bottom: 20px;
}

.page-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
}
</style>