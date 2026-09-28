<template>
  <view class="profile-box">
    <view class="header">
      <view class="avatar-wrap" @click="chooseAvatar">
        <image class="avatar" :src="getAvatarUrl()" mode="aspectFill"/>
        <view class="edit-icon">✏️</view>
      </view>
      <view class="user-info">
        <text class="username">{{ userInfo.username || '未登录' }}</text>
        <text class="user-desc">{{ userInfo.realName || '海洋探险家' }}</text>
      </view>
    </view>
    
    <view v-if="!isLoggedIn" class="login-tip-box">
      <button class="login-btn" @click="goLogin">请登录</button>
    </view>
    
    <view v-else>
      <view class="info-card">
        <view class="info-section">
          <text class="section-title">👤 基本信息</text>
          <view class="info-list">
            <view class="info-item">
              <text class="info-label">昵称</text>
              <text class="info-value">{{ userInfo.username || '-' }}</text>
            </view>
            <view class="info-item">
              <text class="info-label">姓名</text>
              <text class="info-value">{{ userInfo.realName || '-' }}</text>
            </view>
            <view class="info-item">
              <text class="info-label">性别</text>
              <text class="info-value">{{ formatGender(userInfo.gender) }}</text>
            </view>
            <view class="info-item">
              <text class="info-label">出生日期</text>
              <text class="info-value">{{ userInfo.birthday || '-' }}</text>
            </view>
          </view>
        </view>
        
        <view class="info-section">
          <text class="section-title">📞 联系信息</text>
          <view class="info-list">
            <view class="info-item">
              <text class="info-label">手机号</text>
              <text class="info-value">{{ userInfo.phone || '-' }}</text>
            </view>
            <view class="info-item">
              <text class="info-label">邮箱</text>
              <text class="info-value">{{ userInfo.email || '-' }}</text>
            </view>
            <view class="info-item">
              <text class="info-label">地址</text>
              <text class="info-value">{{ userInfo.address || '-' }}</text>
            </view>
          </view>
        </view>
      </view>
      
      <view class="stats-card">
        <view class="stat-item">
          <text class="stat-value">{{ collectionCount }}</text>
          <text class="stat-label">我的收藏</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ orderCount }}</text>
          <text class="stat-label">我的订单</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ cartCount }}</text>
          <text class="stat-label">购物车</text>
        </view>
      </view>
      
      <view class="menu-card">
        <view class="menu-item" @click="handleMenuClick('collection')">
          <view class="menu-icon">❤️</view>
          <text class="menu-text">我的收藏</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="handleMenuClick('order')">
          <view class="menu-icon">📋</view>
          <text class="menu-text">我的订单</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="handleMenuClick('cart')">
          <view class="menu-icon">🛒</view>
          <text class="menu-text">购物车</text>
          <text class="menu-arrow">›</text>
        </view>
        <view class="menu-item" @click="goEditProfile">
          <view class="menu-icon">✏️</view>
          <text class="menu-text">编辑资料</text>
          <text class="menu-arrow">›</text>
        </view>
      </view>
      
      <view class="action-btn">
        <button class="logout-btn" @click="logout">退出登录</button>
      </view>
    </view>
  </view>
</template>

<script>
import { getBaseUrl } from '@/utils/api.js'
export default {
  data() {
    return {
      userInfo: {},
      isLoggedIn: false,
      collectionCount: 0,
      orderCount: 0,
      cartCount: 0,
      defaultAvatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&h=200&fit=crop'
    }
  },
  onShow() {
    this.getUserInfo()
    this.getCounts()
  },
  methods: {
    // 获取头像完整地址（后端返回的可能是相对路径，需要拼接后端地址）
    getAvatarUrl() {
      const img = this.userInfo.userImg
      if (!img) return this.defaultAvatar
      if (img.startsWith('http')) return img
      return getBaseUrl() + img
    },
    // 选择并上传头像
    chooseAvatar() {
      if (!this.isLoggedIn) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        return
      }
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: res => {
          const tempFilePath = res.tempFilePaths[0]
          const token = uni.getStorageSync('token')
          uni.showLoading({ title: '上传中...' })
          uni.uploadFile({
            url: getBaseUrl() + '/user/uploadAvatar',
            filePath: tempFilePath,
            name: 'file',
            header: { 'Authorization': 'Bearer ' + token },
            success: uploadRes => {
              uni.hideLoading()
              try {
                const data = JSON.parse(uploadRes.data)
                if (data.code === 200) {
                  this.userInfo.userImg = data.data
                  uni.showToast({ title: '头像更新成功', icon: 'success' })
                } else {
                  uni.showToast({ title: data.msg || '上传失败', icon: 'none' })
                }
              } catch (e) {
                uni.showToast({ title: '上传失败', icon: 'none' })
              }
            },
            fail: () => {
              uni.hideLoading()
              uni.showToast({ title: '网络错误', icon: 'none' })
            }
          })
        }
      })
    },
    formatGender(gender) {
      if (!gender) return '-'
      return gender === '1' ? '男' : gender === '0' ? '女' : gender
    },
    getUserInfo() {
      const token = uni.getStorageSync('token')
      this.isLoggedIn = !!token
      
      if (!token) {
        this.userInfo = {}
        return
      }
      
      uni.request({
        url: getBaseUrl() + '/user/info',
        method: 'GET',
        header: {
          'Authorization': 'Bearer ' + token
        },
        success: res => {
          if (res.data && res.data.code === 200) {
            this.userInfo = res.data.data
          }
        },
        fail: err => {
          console.error('获取用户信息失败:', err)
        }
      })
    },
    getCounts() {
      const token = uni.getStorageSync('token')
      if (!token) return
      
      uni.request({
        url: getBaseUrl() + "/collection/list",
        method: 'GET',
        header: { 'Authorization': token },
        success: res => {
          if (res.data && res.data.code === 200) {
            this.collectionCount = Array.isArray(res.data.data) ? res.data.data.length : 0
          }
        }
      })
      
      uni.request({
        url: getBaseUrl() + "/order/list",
        method: 'GET',
        header: { 'Authorization': token },
        success: res => {
          if (res.data && res.data.code === 200) {
            this.orderCount = Array.isArray(res.data.data) ? res.data.data.length : 0
          }
        }
      })
      
      uni.request({
        url: getBaseUrl() + "/cart/count",
        method: 'GET',
        header: { 'Authorization': token },
        success: res => {
          if (res.data && res.data.code === 200) {
            this.cartCount = res.data.data || 0
          }
        }
      })
    },
    goLogin() {
      uni.navigateTo({ url: '/pages/login/login' })
    },
    goEditProfile() {
      uni.navigateTo({ url: '/pages/profile/edit' })
    },
    handleMenuClick(type) {
      if (type === 'collection') {
        uni.navigateTo({ url: '/pages/profile/collection' })
      } else if (type === 'order') {
        uni.navigateTo({ url: '/pages/orderList/orderList' })
      } else if (type === 'cart') {
        uni.switchTab({ url: '/pages/cart/cart' })
      } else {
        uni.showToast({
          title: '功能开发中',
          icon: 'none'
        })
      }
    },
    logout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            uni.removeStorageSync('token')
            this.isLoggedIn = false
            this.userInfo = {}
            uni.showToast({ title: '已退出登录', icon: 'success' })
          }
        }
      })
    }
  }
}
</script>

<style>
.profile-box {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(to bottom, #E0F4FF, #B8E6FF);
}
.header {
  background: linear-gradient(to right, #0066CC, #00BFFF);
  padding: 60rpx 40rpx 80rpx;
  display: flex;
  align-items: center;
  gap: 30rpx;
}
.avatar-wrap {
  position: relative;
}
.avatar {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  border: 6rpx solid rgba(255, 255, 255, 0.8);
}
.edit-icon {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 48rpx;
  height: 48rpx;
  background: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.2);
}
.user-info {
  flex: 1;
}
.username {
  display: block;
  font-size: 40rpx;
  color: #fff;
  font-weight: bold;
  margin-bottom: 10rpx;
  text-shadow: 0 2rpx 4rpx rgba(0,0,0,0.2);
}
.user-desc {
  display: block;
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.9);
}
.login-tip-box {
  margin: -40rpx 30rpx 30rpx;
  padding: 40rpx;
  background: rgba(255,255,255,0.95);
  border-radius: 24rpx;
  text-align: center;
  box-shadow: 0 8rpx 24rpx rgba(0, 102, 204, 0.1);
}
.login-btn {
  background: linear-gradient(to right, #00BFFF, #0066CC);
  color: #fff;
  border: none;
  border-radius: 50rpx;
  padding: 24rpx 60rpx;
  font-size: 30rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 102, 204, 0.3);
}
.info-card {
  margin: -40rpx 30rpx 30rpx;
  background: rgba(255,255,255,0.95);
  border-radius: 24rpx;
  padding: 30rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 102, 204, 0.1);
}
.info-section {
  margin-bottom: 30rpx;
}
.info-section:last-child {
  margin-bottom: 0;
}
.section-title {
  display: block;
  font-size: 32rpx;
  font-weight: bold;
  color: #0066CC;
  margin-bottom: 20rpx;
}
.info-list {
  background: linear-gradient(to bottom, #E0F4FF, #fff);
  border-radius: 16rpx;
  overflow: hidden;
}
.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  border-bottom: 1rpx solid rgba(0, 102, 204, 0.08);
}
.info-item:last-child {
  border-bottom: none;
}
.info-label {
  font-size: 28rpx;
  color: #888;
}
.info-value {
  font-size: 28rpx;
  color: #333;
}
.stats-card {
  margin: 0 30rpx 30rpx;
  background: rgba(255,255,255,0.95);
  border-radius: 24rpx;
  padding: 30rpx;
  display: flex;
  justify-content: space-around;
  box-shadow: 0 8rpx 24rpx rgba(0, 102, 204, 0.1);
}
.stat-item {
  text-align: center;
}
.stat-value {
  display: block;
  font-size: 44rpx;
  font-weight: bold;
  color: #00BFFF;
  margin-bottom: 8rpx;
}
.stat-label {
  font-size: 26rpx;
  color: #888;
}
.menu-card {
  margin: 0 30rpx 30rpx;
  background: rgba(255,255,255,0.95);
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 24rpx rgba(0, 102, 204, 0.1);
}
.menu-item {
  display: flex;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid rgba(0, 102, 204, 0.06);
}
.menu-item:last-child {
  border-bottom: none;
}
.menu-icon {
  font-size: 44rpx;
  margin-right: 24rpx;
}
.menu-text {
  flex: 1;
  font-size: 30rpx;
  color: #333;
}
.menu-arrow {
  font-size: 40rpx;
  color: #ccc;
}
.action-btn {
  padding: 30rpx;
}
.logout-btn {
  width: 100%;
  background: #fff;
  color: #0066CC;
  border: 2rpx solid #00BFFF;
  border-radius: 50rpx;
  padding: 24rpx;
  font-size: 30rpx;
}
</style>
