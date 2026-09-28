<template>
  <view class="splash-box">
    <view class="logo-section">
      <view class="logo">🐬</view>
      <text class="app-name">海洋书城</text>
      <text class="app-desc">探索海洋知识，开启阅读之旅</text>
    </view>
    
    <view class="action-section">
      <button class="login-btn" @click="goLogin">登录</button>
      <button class="register-btn" @click="goRegister">注册</button>
    </view>
    
    <view class="copyright">Ocean Encyclopedia © 2024</view>
  </view>
</template>

<script>
export default {
  data() {
    return {}
  },
  onLoad() {
    const token = uni.getStorageSync('token')
    if (token) {
      setTimeout(() => {
        uni.switchTab({ 
          url: '/pages/index/index',
          fail: () => {
            uni.reLaunch({ url: '/pages/index/index' })
          }
        })
      }, 500)
    }
  },
  methods: {
    goLogin() {
      uni.navigateTo({ url: '/pages/login/login' })
    },
    goRegister() {
      uni.navigateTo({ url: '/pages/register/register' })
    }
  }
}
</script>

<style>
.splash-box {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #87CEEB, #1E90FF, #00BFFF);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.logo-section {
  text-align: center;
  margin-bottom: 100rpx;
}
.logo {
  font-size: 120rpx;
  margin-bottom: 40rpx;
  animation: bounce 2s infinite;
}
@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20rpx); }
}
.app-name {
  display: block;
  font-size: 48rpx;
  color: #fff;
  font-weight: bold;
  margin-bottom: 20rpx;
}
.app-desc {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.85);
}
.action-section {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
  width: 80%;
}
.login-btn {
  background: #fff;
  color: #1E90FF;
  border: none;
  border-radius: 50rpx;
  padding: 28rpx;
  font-size: 32rpx;
  font-weight: bold;
}
.register-btn {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  border: 2rpx solid rgba(255, 255, 255, 0.5);
  border-radius: 50rpx;
  padding: 28rpx;
  font-size: 32rpx;
}
.copyright {
  position: fixed;
  bottom: 60rpx;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.7);
}
</style>
