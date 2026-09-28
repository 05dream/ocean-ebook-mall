<template>
  <view class="register-box">
    <view class="register-card">
      <view class="title">🐬 欢迎注册</view>
      <view class="input-wrap">
        <input class="uni-input" v-model="username" placeholder="请输入用户名"/>
      </view>
      <view class="input-wrap">
        <input class="uni-input" v-model="password" placeholder="请输入密码" type="password"/>
      </view>
      <view class="input-wrap">
        <input class="uni-input" v-model="confirmPassword" placeholder="请输入确认密码" type="password"/>
      </view>
      <button class="register-btn" @click="registerBtn">立即注册</button>
      <view class="login-tip" @click="goLogin">已有账号，去登录</view>
      <view class="line-box">——其他账号登录——</view>
      <view class="other-login">
        <text>🐧</text>
        <text>💬</text>
      </view>
    </view>
    <view class="copyright">Ocean Encyclopedia 2024</view>
  </view>
</template>

<script>
import { getBaseUrl } from '@/utils/api.js'
export default {
  data() {
    return {
      username: "",
      password: "",
      confirmPassword: ""
    }
  },
  methods: {
    goLogin() {
      uni.navigateTo({ url: "/pages/login/login" })
    },
    registerBtn() {
      if (!this.username) {
        uni.showToast({ title: "用户名不能为空", icon: "none" })
        return
      }
      if (!this.password) {
        uni.showToast({ title: "密码不能为空", icon: "none" })
        return
      }
      if (this.password !== this.confirmPassword) {
        uni.showToast({ title: "两次输入密码不一致", icon: "none" })
        return
      }
      
      uni.showLoading({ title: "注册中..." })
      
      uni.request({
        url: getBaseUrl() + "/user/register",
        method: "POST",
        header: {
          "content-type": "application/json;charset=utf-8"
        },
        data: {
          username: this.username,
          password: this.password
        },
        success: res => {
          uni.hideLoading()
          console.log("注册响应:", res)
          if (res.data && res.data.code === 200) {
            uni.showToast({ title: res.data.msg, icon: "success" })
            setTimeout(() => {
              uni.navigateTo({ url: "/pages/login/login" })
            }, 1000)
          } else {
            uni.showToast({ title: res.data ? res.data.msg : "注册失败", icon: "none" })
          }
        },
        fail: err => {
          uni.hideLoading()
          console.error("注册请求失败:", err)
          uni.showToast({ title: "网络请求失败，请检查后端服务是否启动", icon: "none", duration: 3000 })
        },
        complete: () => {
          uni.hideLoading()
        }
      })
    }
  }
}
</script>

<style>
.register-box {
  width: 100%;
  height: 100vh;
  background: linear-gradient(#87CEEB, #1E90FF);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.register-card {
  width: 80%;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 60rpx 40rpx;
  box-shadow: 0 8rpx 32rpx rgba(0,0,0,0.1);
}
.title {
  font-size: 36rpx;
  color: #1E90FF;
  text-align: center;
  margin-bottom: 40rpx;
}
.input-wrap {
  width: 100%;
  box-sizing: border-box;
  border: 1rpx solid #eee;
  border-radius: 10rpx;
  padding: 0 20rpx;
  margin-bottom: 24rpx;
}
.uni-input {
  width: 100%;
  height: 80rpx;
  font-size: 28rpx;
}
.register-btn {
  width: 100%;
  background: linear-gradient(to right, #1E90FF, #87CEEB);
  color: #fff;
  border: none;
  border-radius: 50rpx;
  padding: 24rpx;
  font-size: 30rpx;
  margin-top: 20rpx;
}
.login-tip {
  text-align: center;
  margin: 30rpx 0;
  font-size: 26rpx;
  color: #1E90FF;
}
.line-box {
  text-align: center;
  color: #999;
  font-size: 24rpx;
  margin: 40rpx 0 20rpx;
}
.other-login {
  display: flex;
  justify-content: center;
  gap: 60rpx;
  font-size: 48rpx;
}
.copyright {
  position: fixed;
  bottom: 60rpx;
  font-size: 24rpx;
  color: #fff;
  pointer-events: none;
}
</style>
