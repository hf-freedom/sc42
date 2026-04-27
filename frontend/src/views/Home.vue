<template>
  <div class="home-page">
    <el-card class="page-card">
      <template #header>
        <div class="page-title">系统概览</div>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon book-icon">
              <el-icon size="30"><Notebook /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.books }}</div>
              <div class="stat-label">图书总数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon user-icon">
              <el-icon size="30"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.users }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon borrow-icon">
              <el-icon size="30"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.borrows }}</div>
              <div class="stat-label">借阅中</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-icon fine-icon">
              <el-icon size="30"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.blacklisted }}</div>
              <div class="stat-label">黑名单用户</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="page-card">
      <template #header>
        <div class="page-title">快捷操作</div>
      </template>
      <el-row :gutter="20">
        <el-col :span="4">
          <el-button type="primary" @click="handleInitData" style="width: 100%; height: 60px;">
            <el-icon><Plus /></el-icon>
            初始化数据
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="refreshData" style="width: 100%; height: 60px;">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="page-card">
          <template #header>
            <div class="page-title">最近借阅记录</div>
          </template>
          <el-table :data="recentBorrows" style="width: 100%">
            <el-table-column prop="id" label="记录ID" width="80" />
            <el-table-column prop="userId" label="用户ID" width="80" />
            <el-table-column prop="bookId" label="图书ID" width="80" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="borrowTime" label="借阅时间" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="page-card">
          <template #header>
            <div class="page-title">最近流水记录</div>
          </template>
          <el-table :data="recentLogs" style="width: 100%">
            <el-table-column prop="id" label="记录ID" width="80" />
            <el-table-column prop="userId" label="用户ID" width="80" />
            <el-table-column prop="flowType" label="类型" width="100">
              <template #default="scope">
                <el-tag :type="getFlowType(scope.row.flowType)">{{ getFlowTypeText(scope.row.flowType) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" />
            <el-table-column prop="createdAt" label="时间" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  bookApi,
  userApi,
  borrowApi,
  flowLogApi,
  initApi
} from '../api'
import {
  Notebook,
  User,
  Document,
  Warning,
  Plus,
  Refresh
} from '@element-plus/icons-vue'

const stats = ref({
  books: 0,
  users: 0,
  borrows: 0,
  blacklisted: 0
})

const recentBorrows = ref([])
const recentLogs = ref([])

const loadStats = async () => {
  try {
    const [booksRes, usersRes, borrowsRes] = await Promise.all([
      bookApi.getAll(),
      userApi.getAll(),
      borrowApi.getAll()
    ])
    
    stats.value.books = booksRes.data.data?.length || 0
    stats.value.users = usersRes.data.data?.length || 0
    
    const allBorrows = borrowsRes.data.data || []
    stats.value.borrows = allBorrows.filter(b => b.status === 'BORROWED').length
    
    const allUsers = usersRes.data.data || []
    stats.value.blacklisted = allUsers.filter(u => u.blacklistStatus === 'BLACKLISTED').length
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
}

const loadRecentBorrows = async () => {
  try {
    const res = await borrowApi.getAll()
    const data = res.data.data || []
    recentBorrows.value = data.slice(-5).reverse()
  } catch (e) {
    console.error('加载借阅记录失败', e)
  }
}

const loadRecentLogs = async () => {
  try {
    const res = await flowLogApi.getAll()
    const data = res.data.data || []
    recentLogs.value = data.slice(0, 5)
  } catch (e) {
    console.error('加载流水记录失败', e)
  }
}

const refreshData = () => {
  loadStats()
  loadRecentBorrows()
  loadRecentLogs()
  ElMessage.success('数据已刷新')
}

const handleInitData = async () => {
  try {
    const res = await initApi.initData()
    if (res.data.code === 200) {
      ElMessage.success('数据初始化成功')
      refreshData()
    } else {
      ElMessage.error(res.data.message || '初始化失败')
    }
  } catch (e) {
    ElMessage.error('初始化失败: ' + (e.response?.data?.message || e.message))
  }
}

const getStatusType = (status) => {
  const map = {
    'BORROWED': 'warning',
    'RETURNED': 'success',
    'RENEWED': 'primary',
    'DAMAGED': 'danger',
    'LOST': 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'BORROWED': '借阅中',
    'RETURNED': '已归还',
    'RENEWED': '已续借',
    'DAMAGED': '损坏归还',
    'LOST': '丢失'
  }
  return map[status] || status
}

const getFlowType = (type) => {
  const map = {
    'BORROW': 'warning',
    'RETURN': 'success',
    'RENEW': 'primary',
    'RESERVE': 'info',
    'FINE': 'danger',
    'PAY_FINE': 'success',
    'COMPENSATION': 'danger',
    'PAY_COMPENSATION': 'success',
    'BLACKLIST_ADD': 'danger',
    'BLACKLIST_REMOVE': 'success'
  }
  return map[type] || 'info'
}

const getFlowTypeText = (type) => {
  const map = {
    'BORROW': '借阅',
    'RETURN': '归还',
    'RENEW': '续借',
    'RESERVE': '预约',
    'CANCEL_RESERVE': '取消预约',
    'FINE': '罚金',
    'PAY_FINE': '支付罚金',
    'COMPENSATION': '赔偿',
    'PAY_COMPENSATION': '支付赔偿',
    'BLACKLIST_ADD': '加入黑名单',
    'BLACKLIST_REMOVE': '移出黑名单'
  }
  return map[type] || type
}

onMounted(() => {
  refreshData()
})
</script>

<style scoped>
.home-page {
  padding: 0;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 15px;
  border: none;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.book-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.user-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.borrow-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.fine-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}
</style>