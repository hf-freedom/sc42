<template>
  <div class="reservations-page">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span class="page-title">预约管理</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            新增预约
          </el-button>
        </div>
      </template>

      <el-table :data="reservations" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="预约ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="bookId" label="图书ID" width="80" />
        <el-table-column prop="queuePosition" label="排队位置" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reservedTime" label="预约时间" width="180" />
        <el-table-column prop="assignedTime" label="分配时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'PENDING'"
              type="warning"
              size="small"
              @click="handleCancel(scope.row)"
            >
              取消预约
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showAddDialog" title="新增预约" width="500px">
      <el-form :model="reservationForm" label-width="80px">
        <el-form-item label="用户">
          <el-select v-model="reservationForm.userId" placeholder="请选择用户" filterable style="width: 100%">
            <el-option
              v-for="user in users"
              :key="user.id"
              :label="`${user.realName || user.username} (ID: ${user.id})`"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="图书">
          <el-select v-model="reservationForm.bookId" placeholder="请选择图书" filterable style="width: 100%">
            <el-option
              v-for="book in books"
              :key="book.id"
              :label="`${book.title} (可借: ${book.availableCopies}/${book.totalCopies})`"
              :value="book.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleReserve">确定预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { reservationApi, bookApi, userApi } from '../api'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const reservations = ref([])
const users = ref([])
const books = ref([])
const showAddDialog = ref(false)

const reservationForm = ref({
  userId: null,
  bookId: null
})

const loadData = async () => {
  loading.value = true
  try {
    const [reservationsRes, usersRes, booksRes] = await Promise.all([
      reservationApi.getAll(),
      userApi.getAll(),
      bookApi.getAll()
    ])
    reservations.value = reservationsRes.data.data || []
    users.value = usersRes.data.data || []
    books.value = booksRes.data.data || []
  } catch (e) {
    ElMessage.error('加载数据失败: ' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

const handleReserve = async () => {
  if (!reservationForm.value.userId || !reservationForm.value.bookId) {
    ElMessage.warning('请选择用户和图书')
    return
  }
  try {
    const res = await reservationApi.reserve(reservationForm.value.userId, reservationForm.value.bookId)
    if (res.data.code === 200) {
      ElMessage.success('预约成功')
      showAddDialog.value = false
      loadData()
      reservationForm.value = {
        userId: null,
        bookId: null
      }
    } else {
      ElMessage.error(res.data.message || '预约失败')
    }
  } catch (e) {
    ElMessage.error('预约失败: ' + (e.response?.data?.message || e.message))
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消该预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await reservationApi.cancel(row.id)
    if (res.data.code === 200) {
      ElMessage.success('取消成功')
      loadData()
    } else {
      ElMessage.error(res.data.message || '取消失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('取消失败: ' + (e.response?.data?.message || e.message))
    }
  }
}

const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'ASSIGNED': 'success',
    'CANCELLED': 'info',
    'EXPIRED': 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '等待中',
    'ASSIGNED': '已分配',
    'CANCELLED': '已取消',
    'EXPIRED': '已过期'
  }
  return map[status] || status
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.reservations-page {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>