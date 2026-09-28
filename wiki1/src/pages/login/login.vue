<template>
  <view class="login-box">
    <view class="login-card">
      <view class="title">🐬 用户登录</view>
      <view class="input-wrap">
        <input class="uni-input" v-model="username" placeholder="请输入用户名"/>
      </view>
      <view class="input-wrap">
        <input class="uni-input" v-model="password" placeholder="请输入密码" type="password"/>
      </view>
      <button class="login-btn" @click="loginBtn">登录</button>
      <view class="reg-tip" @click="goRegister">没有账号，去注册</view>
    </view>
  </view>
</template>

<script>
import { getBaseUrl } from '@/utils/api.js'
export default {
  data() {
    return {
      username: "",
      password: ""
    }
  },
  methods: {
    goRegister() {
      uni.navigateTo({ url: "/pages/register/register" })
    },
    loginBtn() {
      if (!this.username) {
        uni.showToast({ title: "请输入用户名", icon: "none" })
        return
      }
      if (!this.password) {
        uni.showToast({ title: "请输入密码", icon: "none" })
        return
      }
      
      uni.showLoading({ title: "登录中..." })
      
      uni.request({
        url: getBaseUrl() + "/user/login",
        method: "POST",
        header: {
          "content-type": "application/json"
        },
        data: {
          username: this.username,
          password: this.password
        },
        withCredentials: true,
        success: res => {
          uni.hideLoading()
          console.log("登录响应:", res)
          if (res.data && res.data.code === 200) {
            uni.showToast({ title: res.data.msg, icon: "success" })
            uni.setStorageSync("token", res.data.data)
            setTimeout(() => {
              uni.switchTab({ url: "/pages/index/index" })
            }, 1000)
          } else {
            uni.showToast({ title: res.data ? res.data.msg : "登录失败", icon: "none" })
          }
        },
        fail: err => {
          uni.hideLoading()
          console.error("登录请求失败:", err)
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
.login-box {
  width: 100%;
  height: 100vh;
  background: linear-gradient(#87CEEB, #1E90FF);
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-card {
  width: 80%;
  background: #fff;
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
.login-btn {
  width: 100%;
  background: linear-gradient(to right, #1E90FF, #87CEEB);
  color: #fff;
  border: none;
  border-radius: 50rpx;
  padding: 24rpx;
  font-size: 30rpx;
  margin-top: 20rpx;
}
.reg-tip {
  text-align: center;
  margin-top: 30rpx;
  font-size: 26rpx;
  color: #1E90FF;
}
</style>
