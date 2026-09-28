<template>
  <view class="order-box">
    <view class="header">
      <text class="title">订单详情</text>
    </view>
    
    <view class="content" v-if="order">
      <view class="order-info">
        <view class="info-row">
          <text class="label">订单号：</text>
          <text class="value">{{ order.orderId }}</text>
        </view>
        <view class="info-row">
          <text class="label">总价：</text>
          <text class="value price">¥{{ order.totalPrice.toFixed(2) }}</text>
        </view>
        <view class="info-row">
          <text class="label">状态：</text>
          <text class="value" :class="order.paymentStatus === 'success' ? 'success' : 'fail'">
            {{ order.paymentStatus === 'success' ? '已支付' : '未支付' }}
          </text>
        </view>
        <view class="info-row">
          <text class="label">创建时间：</text>
          <text class="value">{{ formatTime(order.createTime) }}</text>
        </view>
        <view class="info-row" v-if="order.address">
          <text class="label">收货地址：</text>
          <text class="value">{{ order.address }}</text>
        </view>
      </view>
      
      <view class="items-section">
        <text class="section-title">商品列表</text>
        <view class="item-list">
          <view class="item" v-for="item in orderItems" :key="item.itemId">
            <image :src="item.image || defaultImage" mode="widthFix"></image>
            <view class="item-info">
              <text class="item-name">{{ item.name }}</text>
              <text class="item-qty">数量：{{ item.quantity }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <view class="btn-area" v-if="order">
      <button v-if="order.paymentStatus === 'fail'" class="cancel-btn" @click="cancelOrder">取消订单</button>
      <button v-if="order.paymentStatus === 'fail'" class="pay-btn" @click="payOrder">去支付</button>
      <button v-if="order.paymentStatus === 'success'" class="back-btn" @click="goBack">返回首页</button>
    </view>
    
    <view class="empty" v-else>
      <text>加载中...</text>
    </view>
  </view>
</template>

<script>
import { getBaseUrl } from '@/utils/api.js'
export default {
  data() {
    return {
      orderId: 0,
      order: null,
      orderItems: [],
      defaultImage: 'https://picsum.photos/400/300?random=1'
    }
  },
  onLoad(options) {
    if (options && options.id) {
      this.orderId = parseInt(options.id)
      this.getOrderDetail()
      this.getOrderItems()
    }
  },
  methods: {
    getOrderDetail() {
      const token = uni.getStorageSync('token')
      if (!token) {
        uni.showToast({ icon: 'none', title: '请先登录' })
        return
      }
      
      uni.request({
        url: getBaseUrl() + "/order/detail",
        method: 'GET',
        header: {
          'Authorization': token
        },
        data: {
          orderId: this.orderId
        },
        success: res => {
          if (res.data && res.data.code === 200) {
            this.order = res.data.data
          } else {
            uni.showToast({ icon: 'none', title: res.data.msg || '获取失败' })
          }
        },
        fail: () => {
          uni.showToast({ icon: 'none', title: '网络错误' })
        }
      })
    },
    getOrderItems() {
      const token = uni.getStorageSync('token')
      if (!token) return
      
      uni.request({
        url: getBaseUrl() + "/order/items",
        method: 'GET',
        header: {
          'Authorization': token
        },
        data: {
          orderId: this.orderId
        },
        success: res => {
          if (res.data && res.data.code === 200) {
            this.orderItems = res.data.data
          }
        },
        fail: () => {}
      })
    },
    cancelOrder() {
      const token = uni.getStorageSync('token')
      if (!token) return
      
      uni.showModal({
        title: '提示',
        content: '确定取消订单吗？',
        success: (res) => {
          if (res.confirm) {
            uni.request({
              url: getBaseUrl() + "/order/cancel",
              method: 'POST',
              header: {
                'Authorization': token,
                'content-type': 'application/json'
              },
              data: {
                orderId: this.orderId
              },
              success: res => {
                if (res.data && res.data.code === 200) {
                  uni.showToast({ icon: 'success', title: '取消成功' })
                  setTimeout(() => {
                    uni.navigateBack()
                  }, 1500)
                } else {
                  uni.showToast({ icon: 'none', title: res.data.msg || '取消失败' })
                }
              },
              fail: () => {
                uni.showToast({ icon: 'none', title: '网络错误' })
              }
            })
          }
        }
      })
    },
    payOrder() {
      const subject = this.orderItems.length > 0 ? this.orderItems[0].name : '订单支付'
      uni.navigateTo({ url: '/pages/payForm/payForm?orderId=' + this.orderId + '&subject=' + encodeURIComponent(subject) })
    },
    goBack() {
      uni.switchTab({ url: '/pages/index/index' })
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
.order-box {
  min-height: 100vh;
  background: #f5f5f5;
}
.header {
  background: #1E90FF;
  padding: 60rpx 30rpx;
  text-align: center;
}
.header .title {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
}
.content {
  padding: 30rpx;
}
.order-info {
  background: #fff;
  padding: 30rpx;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
}
.info-row {
  display: flex;
  padding: 15rpx 0;
}
.info-row .label {
  font-size: 28rpx;
  color: #999;
  width: 160rpx;
}
.info-row .value {
  font-size: 28rpx;
  color: #333;
}
.info-row .value.price {
  color: #FF4500;
  font-weight: bold;
}
.info-row .value.success {
  color: #00CD00;
}
.info-row .value.fail {
  color: #FF4500;
}
.items-section {
  background: #fff;
  padding: 30rpx;
  border-radius: 16rpx;
}
.section-title {
  display: block;
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}
.item-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}
.item {
  display: flex;
  gap: 20rpx;
}
.item image {
  width: 150rpx;
  height: 120rpx;
  border-radius: 12rpx;
}
.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.item-name {
  font-size: 28rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 10rpx;
}
.item-qty {
  font-size: 24rpx;
  color: #999;
}
.btn-area {
  padding: 30rpx;
  display: flex;
  gap: 20rpx;
}
.cancel-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  background: #fff;
  color: #666;
  font-size: 32rpx;
  border-radius: 40rpx;
  border: 2rpx solid #ccc;
}
.pay-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  background: #FF4500;
  color: #fff;
  font-size: 32rpx;
  border-radius: 40rpx;
  border: none;
}
.back-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  background: #1E90FF;
  color: #fff;
  font-size: 32rpx;
  border-radius: 40rpx;
  border: none;
}
.empty {
  text-align: center;
  padding: 100rpx;
  color: #999;
}
</style>