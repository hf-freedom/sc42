<template>
  <div class="users-page">
    <el-card class="page-card">
      <template #header>
        <div class="card-header">
          <span class="page-title">用户管理</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            添加用户
          </el-button>
        </div>
      </template>

      <el-table :data="users" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="level" label="会员等级" width="120">
          <template #default="scope">
            <el-tag :type="getLevelType(scope.row.level)">{{ getLevelText(scope.row.level) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="maxBorrowCount" label="最大借阅数" width="100" />
        <el-table-column prop="depositBalance" label="押金余额" width="100">
          <template #default="scope">
            ¥{{ scope.row.depositBalance }}
          </template>
        </el-table-column>
        <el-table-column prop="blacklistStatus" label="黑名单状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.blacklistStatus === 'BLACKLISTED' ? 'danger' : 'success'">
              {{ getBlacklistText(scope.row.blacklistStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="overdueCount" label="逾期次数" width="100" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button
              v-if="scope.row.blacklistStatus !== 'BLACKLISTED'"
              type="danger"
              size="small"
              @click="addToBlacklist(scope.row)"
            >
              加入黑名单
            </el-button>
            <el-button
              v-else
              type="success"
              size="small"
              @click="removeFromBlacklist(scope.row)"
            >
              移出黑名单
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showAddDialog" title="添加用户" width="500px">
      <el-form :model="userForm" label-width="100px">
        <el-form-item label="用户名" required>
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="userForm.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="会员等级">
          <el-select v-model="userForm.level" placeholder="请选择等级" style="width: 100%">
            <el-option label="青铜会员" value="BRONZE" />
            <el-option label="白银会员" value="SILVER" />
            <el-option label="黄金会员" value="GOLD" />
            <el-option label="白金会员" value="PLATINUM" />
          </el-select>
        </el-form-item>
        <el-form-item label="押金">
          <el-input-number v-model="userForm.depositBalance" :min="0" :precision="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addUser">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '../api'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const users = ref([])
const showAddDialog = ref(false)

const userForm = ref({
  username: '',
  realName: '',
  level: 'BRONZE',
  depositBalance: 0
})

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await userApi.getAll()
    users.value = res.data.data || []
  } catch (e) {
    ElMessage.error('加载用户失败: ' + (e.response?.data?.message || e.message))
  } finally {
    loading.value = false
  }
}

const addUser = async () => {
  if (!userForm.value.username) {
    ElMessage.warning('请输入用户名')
    return
  }
  try {
    const res = await userApi.create(userForm.value)
    if (res.data.code === 200) {
      ElMessage.success('添加成功')
      showAddDialog.value = false
      loadUsers()
      userForm.value = {
        username: '',
        realName: '',
        level: 'BRONZE',
        depositBalance: 0
      }
    } else {
      ElMessage.error(res.data.message || '添加失败')
    }
  } catch (e) {
    ElMessage.error('添加失败: ' + (e.response?.data?.message || e.message))
  }
}

const addToBlacklist = async (user) => {
  try {
    await ElMessageBox.confirm(`确定要将用户 "${user.realName || user.username}" 加入黑名单吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await userApi.addToBlacklist(user.id, '管理员操作')
    if (res.data.code === 200) {
      ElMessage.success('已加入黑名单')
      loadUsers()
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('操作失败: ' + (e.response?.data?.message || e.message))
    }
  }
}

const removeFromBlacklist = async (user) => {
  try {
    await ElMessageBox.confirm(`确定要将用户 "${user.realName || user.username}" 移出黑名单吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const res = await userApi.removeFromBlacklist(user.id)
    if (res.data.code === 200) {
      ElMessage.success('已移出黑名单')
      loadUsers()
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('操作失败: ' + (e.response?.data?.message || e.message))
    }
  }
}

const getLevelType = (level) => {
  const map = {
    'BRONZE': 'info',
    'SILVER': '',
    'GOLD': 'warning',
    'PLATINUM': 'primary'
  }
  return map[level] || 'info'
}

const getLevelText = (level) => {
  const map = {
    'BRONZE': '青铜会员',
    'SILVER': '白银会员',
    'GOLD': '黄金会员',
    'PLATINUM': '白金会员'
  }
  return map[level] || level
}

const getBlacklistText = (status) => {
  const map = {
    'NORMAL': '正常',
    'BLACKLISTED': '黑名单',
    'RESTRICTED': '限制借阅'
  }
  return map[status] || status
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.users-page {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>