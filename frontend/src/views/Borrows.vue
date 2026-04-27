<template>
  <div class="borrows-page">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span class="page-title">借阅管理</span>
          <el-button type="primary" @click="showBorrowDialog = true">
            <el-icon><Plus /></el-icon>
            借书
          </el-button>
        </div>
      </template>

      <el-table :data="borrows" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="记录ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="bookId" label="图书ID" width="80" />
        <el-table-column prop="bookCopyId" label="副本ID" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="borrowTime" label="借书时间" width="180" />
        <el-table-column prop="dueTime" label="到期时间" width="180">
          <template #default="scope">
            <span :style="{ color: isOverdue(scope.row) ? '#f56c6c' : '' }">
              {{ scope.row.dueTime }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="returnTime" label="归还时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'BORROWED'"
              type="success"
              size="small"
              @click="handleReturn(scope.row)"
            >
              归还
            </el-button>
            <el-button
              v-if="scope.row.status === 'BORROWED'"
              type="primary"
              size="small"
              @click="handleRenew(scope.row)"
            >
              续借
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showBorrowDialog" title="借书" width="500px">
      <el-form :model="borrowForm" label-width="80px">
        <el-form-item label="用户">
          <el-select v-model="borrowForm.userId" placeholder="请选择用户" filterable style="width: 100%">
            <el-option
              v-for="user in users"
              :key="user.id"
              :label="`${user.realName || user.username} (ID: ${user.id})`"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="图书">
          <el-select v-model="borrowForm.bookId" placeholder="请选择图书" filterable style="width: 100%">
            <el-option
              v-for="book in availableBooks"
              :key="book.id"
              :label="`${book.title} (可借: ${book.availableCopies}/${book.totalCopies})`"
              :value="book.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showBorrowDialog = false">取消</el-button>
        <el-button type="primary" @click="handleBorrow">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showReturnDialog" title="归还图书" width="500px">
      <el-form :model="returnForm" label-width="80px">
        <el-form-item label="图书损坏">
          <el-switch v-model="returnForm.isDamaged" />
        </el-form-item>
        <el-alert
          v-if="returnForm.isDamaged"
          title="提示"
          type="warning"
          show-icon
        >
          损坏归还将生成赔偿单
        </el-alert>
      </el-form>
      <template #footer>
        <el-button @click="showReturnDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmReturn">确定归还</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { borrowApi, bookApi, userApi } from '../api'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const borrows = ref([])
const users = ref([])
const books = ref([])
const showBorrowDialog = ref(false)
const showReturnDialog = ref(false)
const currentBorrow = ref(null)

const borrowForm = ref({
  userId: null,
  bookId: null
})

const returnForm = ref({
  isDamaged: false
})

const availableBooks = computed(() => {
  return books.value.filter(book => book.availableCopies > 0)
})

const loadData = async () => {
  loading.value = true
  try {
    const [borrowsRes, usersRes, booksRes] = await Promise.all([
      borrowApi.getAll(),
      userApi.getAll(),
      bookApi.getAll()
    ])
    borrows.value = borrowsRes.data.data || []
    users.value = usersRes.data.data || []
    books.value = booksRes.data.data || []
  } catch (e) {
    ElMessage.error('加载数据失败: ' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

const handleBorrow = async () => {
  if (!borrowForm.value.userId || !borrowForm.value.bookId) {
    ElMessage.warning('请选择用户和图书')
    return
  }
  try {
    const res = await borrowApi.borrow(borrowForm.value.userId, borrowForm.value.bookId)
    if (res.data.code === 200) {
      ElMessage.success('借书成功')
      showBorrowDialog.value = false
      loadData()
      borrowForm.value = {
        userId: null,
        bookId: null
      }
    } else {
      ElMessage.error(res.data.message || '借书失败')
    }
  } catch (e) {
    ElMessage.error('借书失败: ' + (e.response?.data?.message || e.message))
  }
}

const handleReturn = (row) => {
  currentBorrow.value = row
  returnForm.value.isDamaged = false
  showReturnDialog.value = true
}

const confirmReturn = async () => {
  if (!currentBorrow.value) return
  try {
    const res = await borrowApi.return(currentBorrow.value.id, returnForm.value.isDamaged)
    if (res.data.code === 200) {
      ElMessage.success('归还成功')
      showReturnDialog.value = false
      loadData()
    } else {
      ElMessage.error(res.data.message || '归还失败')
    }
  } catch (e) {
    ElMessage.error('归还失败: ' + (e.response?.data?.message || e.message))
  }
}

const handleRenew = async (row) => {
  try {
    await ElMessageBox.confirm('确定要续借该图书吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await borrowApi.renew(row.id)
    if (res.data.code === 200) {
      ElMessage.success('续借成功')
      loadData()
    } else {
      ElMessage.error(res.data.message || '续借失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('续借失败: ' + (e.response?.data?.message || e.message))
    }
  }
}

const isOverdue = (row) => {
  if (row.status !== 'BORROWED' || !row.dueTime) return false
  return new Date(row.dueTime) < new Date()
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

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.borrows-page {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>