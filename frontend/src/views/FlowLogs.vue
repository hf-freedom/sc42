<template>
  <div class="flow-logs-page">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span class="page-title">流水记录</span>
        </div>
      </template>

      <el-table :data="logs" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="记录ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="flowType" label="类型" width="120">
          <template #default="scope">
            <el-tag :type="getFlowType(scope.row.flowType)">{{ getFlowTypeText(scope.row.flowType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="relatedId" label="关联ID" width="100" />
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="scope">
            <span v-if="scope.row.amount !== null">¥{{ scope.row.amount }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { flowLogApi } from '../api'

const loading = ref(false)
const logs = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await flowLogApi.getAll()
    logs.value = res.data.data || []
  } catch (e) {
    ElMessage.error('加载数据失败: ' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

const getFlowType = (type) => {
  const map = {
    'BORROW': 'warning',
    'RETURN': 'success',
    'RENEW': 'primary',
    'RESERVE': 'info',
    'CANCEL_RESERVE': 'info',
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
  loadData()
})
</script>

<style scoped>
.flow-logs-page {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>