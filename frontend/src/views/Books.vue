<template>
  <div class="books-page">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span class="page-title">图书管理</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            添加图书
          </el-button>
        </div>
      </template>

      <el-table :data="books" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="书名" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="totalCopies" label="总册数" width="100" />
        <el-table-column prop="availableCopies" label="可借册数" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.availableCopies > 0 ? 'success' : 'danger'">
              {{ scope.row.availableCopies }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="damagedCopies" label="损坏册数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewCopies(scope.row)">
              查看副本
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showAddDialog" title="添加图书" width="500px">
      <el-form :model="bookForm" label-width="100px">
        <el-form-item label="书名" required>
          <el-input v-model="bookForm.title" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="bookForm.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="计算机科学" value="计算机科学" />
            <el-option label="文学经典" value="文学经典" />
            <el-option label="历史传记" value="历史传记" />
            <el-option label="科技读物" value="科技读物" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="册数">
          <el-input-number v-model="bookForm.copyCount" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="馆区">
          <el-select v-model="bookForm.libraryBranch" placeholder="请选择馆区" style="width: 100%">
            <el-option label="总馆" value="总馆" />
            <el-option label="分馆A" value="分馆A" />
            <el-option label="分馆B" value="分馆B" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addBook">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showCopiesDialog" title="图书副本列表" width="800px">
      <el-table :data="currentCopies" style="width: 100%">
        <el-table-column prop="id" label="副本ID" width="80" />
        <el-table-column prop="barcode" label="条码" width="150" />
        <el-table-column prop="libraryBranch" label="馆区" width="120" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getCopyStatusType(scope.row.status)">{{ getCopyStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { bookApi } from '../api'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const books = ref([])
const showAddDialog = ref(false)
const showCopiesDialog = ref(false)
const currentCopies = ref([])

const bookForm = ref({
  title: '',
  category: '其他',
  copyCount: 1,
  libraryBranch: '总馆'
})

const loadBooks = async () => {
  loading.value = true
  try {
    const res = await bookApi.getAll()
    books.value = res.data.data || []
  } catch (e) {
    ElMessage.error('加载图书失败: ' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

const addBook = async () => {
  if (!bookForm.value.title) {
    ElMessage.warning('请输入书名')
    return
  }
  try {
    const res = await bookApi.create(bookForm.value)
    if (res.data.code === 200) {
      ElMessage.success('添加成功')
      showAddDialog.value = false
      loadBooks()
      bookForm.value = {
        title: '',
        category: '其他',
        copyCount: 1,
        libraryBranch: '总馆'
      }
    } else {
      ElMessage.error(res.data.message || '添加失败')
    }
  } catch (e) {
    ElMessage.error('添加失败: ' + (e.response?.data?.message || e.message))
  }
}

const viewCopies = async (book) => {
  try {
    const res = await bookApi.getStatus(book.id)
    currentCopies.value = res.data.data?.copies || []
    showCopiesDialog.value = true
  } catch (e) {
    ElMessage.error('获取副本信息失败: ' + (e.response?.data?.message || e.message))
  }
}

const getStatusType = (status) => {
  const map = {
    'AVAILABLE': 'success',
    'FULLY_BORROWED': 'warning',
    'RESERVED_ONLY': 'info',
    'MAINTENANCE': 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'AVAILABLE': '可借',
    'FULLY_BORROWED': '已借完',
    'RESERVED_ONLY': '仅预约',
    'MAINTENANCE': '维护中'
  }
  return map[status] || status
}

const getCopyStatusType = (status) => {
  const map = {
    'AVAILABLE': 'success',
    'BORROWED': 'warning',
    'DAMAGED': 'danger',
    'LOST': 'danger',
    'MAINTENANCE': 'info'
  }
  return map[status] || 'info'
}

const getCopyStatusText = (status) => {
  const map = {
    'AVAILABLE': '可借',
    'BORROWED': '已借出',
    'DAMAGED': '损坏',
    'LOST': '丢失',
    'MAINTENANCE': '维护中'
  }
  return map[status] || status
}

onMounted(() => {
  loadBooks()
})
</script>

<style scoped>
.books-page {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>