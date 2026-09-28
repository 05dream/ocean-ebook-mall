<template>
  <view class="order-list-box">
    <view class="navBar">
      <text class="title">我的订单</text>
      <view class="edit-btn" @click="toggleEdit">
        {{ isEdit ? '完成' : '编辑' }}
      </view>
    </view>

    <view class="order-list">
      <view class="order-item" v-for="order in orders" :key="order.orderId" @click="goDetail(order.orderId)">
        <view class="checkbox" v-if="isEdit" @click.stop="toggleSelect(order.orderId)">
          <text :class="selectedIds.includes(order.orderId) ? 'checked' : 'unchecked'">
            {{ selectedIds.includes(order.orderId) ? '✓' : '' }}
          </text>
        </view>
        <view class="order-content">
          <view class="order-header">
            <text class="order-id">订单号：{{ order.orderId }}</text>
            <text class="order-status" :class="order.paymentStatus === 'success' ? 'success' : 'fail'">
              {{ order.paymentStatus === 'success' ? '已支付' : '未支付' }}
            </text>
          </view>
          <view class="order-info">
            <text class="order-price">总价：¥{{ order.totalPrice.toFixed(2) }}</text>
            <text class="order-time">{{ formatTime(order.createTime) }}</text>
          </view>
        </view>
      </view>
    </view>

    <view class="empty" v-if="orders.length === 0">
      <text class="empty-icon">📋</text>
      <text class="empty-text">暂无订单</text>
    </view>

    <!-- 编辑模式底部操作栏 -->
    <view class="action-bar" v-if="isEdit">
      <view class="select-all" @click="toggleSelectAll">
        <text :class="isAllSelected ? 'checked' : 'unchecked'">
          {{ isAllSelected ? '✓' : '' }}
        </text>
        <text>全选</text>
      </view>
      <button class="batch-cancel-btn" @click="batchCancel">批量取消({{ selectedIds.length }})</button>
    </view>
  </view>
</template>

<script>
import { getBaseUrl } from '@/utils/api.js'
export default {
  data() {
    return {
      orders: [],
      isEdit: false,
      selectedIds: []
    }
  },
  computed: {
    // 只有未支付的订单可以取消
    cancellableOrders() {
      return this.orders.filter(o => o.paymentStatus !== 'success')
    },
    isAllSelected() {
      return this.cancellableOrders.length > 0 &&
        this.cancellableOrders.every(o => this.selectedIds.includes(o.orderId))
    }
  },
  onShow() {
    this.getOrders()
    this.refreshPaymentStatus()
  },
  methods: {
    getOrders() {
      const token = uni.getStorageSync('token')
      if (!token) {
        uni.showToast({ icon: 'none', title: '请先登录' })
        return
      }
      uni.request({
        url: getBaseUrl() + "/order/list",
        method: 'GET',
        header: { 'Authorization': token },
        success: res => {
          if (res.data && res.data.code === 200 && Array.isArray(res.data.data)) {
            this.orders = res.data.data
          }
        },
        fail: () => {
          uni.showToast({ icon: 'none', title: '网络错误' })
        }
      })
    },
    refreshPaymentStatus() {
      const token = uni.getStorageSync('token')
      if (!token || this.orders.length === 0) return
      const orderIds = this.orders.map(o => o.orderId)
      uni.request({
        url: getBaseUrl() + "/order/updateStatuses",
        method: 'POST',
        header: {
          'Authorization': token,
          'content-type': 'application/json'
        },
        data: orderIds,
        success: res => {
          if (res.data && res.data.code === 200) {
            this.getOrders()
          }
        }
      })
    },
    toggleEdit() {
      this.isEdit = !this.isEdit
      this.selectedIds = []
    },
    toggleSelect(orderId) {
      const order = this.orders.find(o => o.orderId === orderId)
      if (order && order.paymentStatus === 'success') {
        uni.showToast({ icon: 'none', title: '已支付订单不能取消' })
        return
      }
      const index = this.selectedIds.indexOf(orderId)
      if (index > -1) {
        this.selectedIds.splice(index, 1)
      } else {
        this.selectedIds.push(orderId)
      }
    },
    toggleSelectAll() {
      if (this.isAllSelected) {
        this.selectedIds = []
      } else {
        this.selectedIds = this.cancellableOrders.map(o => o.orderId)
      }
    },
    batchCancel() {
      if (this.selectedIds.length === 0) {
        uni.showToast({ icon: 'none', title: '请选择订单' })
        return
      }
      const token = uni.getStorageSync('token')
      if (!token) return
      uni.showModal({
        title: '提示',
        content: `确定取消选中的 ${this.selectedIds.length} 个订单吗？`,
        success: (res) => {
          if (res.confirm) {
            uni.showLoading({ title: '取消中...' })
            uni.request({
              url: getBaseUrl() + "/order/cancelBatch",
              method: 'POST',
              header: {
                'Authorization': token,
                'content-type': 'application/json'
              },
              data: this.selectedIds,
              success: res => {
                uni.hideLoading()
                if (res.data && res.data.code === 200) {
                  uni.showToast({ icon: 'success', title: res.data.msg || '操作成功' })
                  this.selectedIds = []
                  this.isEdit = false
                  this.getOrders()
                } else {
                  uni.showToast({ icon: 'none', title: res.data.msg || '操作失败' })
                }
              },
              fail: () => {
                uni.hideLoading()
                uni.showToast({ icon: 'none', title: '网络错误' })
              }
            })
          }
        }
      })
    },
    goDetail(orderId) {
      if (this.isEdit) return
      uni.navigateTo({ url: '/pages/order/order?id=' + orderId })
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return `${date.getFullYear()}-${String(date.getMonth()+1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    }
  }
}
</script>

<style>
.order-list-box {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}
.navBar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(to right, #1E90FF, #87CEEB);
  padding: 100rpx 30rpx 30rpx;
}
.navBar .title {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
}
.edit-btn {
  font-size: 26rpx;
  color: #fff;
  padding: 10rpx 20rpx;
  border: 2rpx solid #fff;
  border-radius: 20rpx;
}
.order-list {
  padding: 20rpx;
}
.order-item {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
  gap: 20rpx;
}
.checkbox {
  width: 44rpx;
  height: 44rpx;
  border-radius: 50%;
  border: 2rpx solid #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.checked {
  background: linear-gradient(to right, #1E90FF, #87CEEB);
  color: #fff;
  font-size: 24rpx;
  border: none;
}
.unchecked {
  background: transparent;
}
.order-content {
  flex: 1;
}
.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20rpx;
}
.order-id {
  font-size: 28rpx;
  color: #333;
  font-weight: bold;
}
.order-status {
  font-size: 26rpx;
  padding: 6rpx 16rpx;
  border-radius: 12rpx;
}
.order-status.success {
  background: #E8F5E9;
  color: #4CAF50;
}
.order-status.fail {
  background: #FFEBEE;
  color: #F44336;
}
.order-info {
  display: flex;
  justify-content: space-between;
  font-size: 26rpx;
  color: #666;
}
.order-price {
  color: #FF4500;
  font-weight: bold;
}
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx;
}
.empty-icon {
  font-size: 100rpx;
  margin-bottom: 30rpx;
}
.empty-text {
  font-size: 32rpx;
  color: #999;
}
.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  display: flex;
  align-items: center;
  border-top: 1rpx solid #eee;
  box-shadow: 0 -4rpx 16rpx rgba(0,0,0,0.05);
}
.select-all {
  display: flex;
  align-items: center;
  gap: 10rpx;
  font-size: 28rpx;
  color: #333;
}
.batch-cancel-btn {
  margin-left: auto;
  background: #F44336;
  color: #fff;
  font-size: 28rpx;
  padding: 16rpx 36rpx;
  border-radius: 30rpx;
  border: none;
}
</style>
