<template>
  <div class="compensations-page">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span class="page-title">赔偿管理</span>
        </div>
      </template>

      <el-table :data="compensations" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="赔偿ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="borrowRecordId" label="借阅记录ID" width="120" />
        <el-table-column prop="bookCopyId" label="副本ID" width="80" />
        <el-table-column prop="amount" label="赔偿金额" width="100">
          <template #default="scope">
            ¥{{ scope.row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="原因" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdDate" label="创建日期" width="180" />
        <el-table-column prop="paidDate" label="支付日期" width="180" />
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'PENDING'"
              type="success"
              size="small"
              @click="handlePay(scope.row)"
            >
              支付
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { compensationApi } from '../api'

const loading = ref(false)
const compensations = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await compensationApi.getAll()
    compensations.value = res.data.data || []
  } catch (e) {
    ElMessage.error('加载数据失败: ' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

const handlePay = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要支付赔偿金 ¥${row.amount} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await compensationApi.pay(row.id)
    if (res.data.code === 200) {
      ElMessage.success('支付成功')
      loadData()
    } else {
      ElMessage.error(res.data.message || '支付失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('支付失败: ' + (e.response?.data?.message || e.message))
    }
  }
}

const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'PAID': 'success',
    'WAIVED': 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '待赔偿',
    'PAID': '已赔偿',
    'WAIVED': '已减免'
  }
  return map[status] || status
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.compensations-page {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>