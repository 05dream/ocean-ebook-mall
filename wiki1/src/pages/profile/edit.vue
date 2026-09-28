<template>
  <view class="edit-box">
    <view class="section">
      <text class="section-title">👤 基本信息</text>
      
      <view class="input-wrap">
        <text class="input-label">昵称</text>
        <input class="uni-input" v-model="form.username" placeholder="请输入昵称"/>
      </view>
      
      <view class="input-wrap">
        <text class="input-label">姓名</text>
        <input class="uni-input" v-model="form.realName" placeholder="请输入真实姓名"/>
      </view>
      
      <view class="input-wrap">
        <text class="input-label">性别</text>
        <view class="gender-select">
          <view class="gender-item" :class="{ active: form.gender === '1' }" @click="form.gender = '1'">
            <text>男</text>
          </view>
          <view class="gender-item" :class="{ active: form.gender === '0' }" @click="form.gender = '0'">
            <text>女</text>
          </view>
        </view>
      </view>
      
      <view class="input-wrap">
        <text class="input-label">出生日期</text>
        <picker mode="date" :value="form.birthday" @change="onDateChange">
          <view class="picker-value">
            {{ form.birthday || '请选择出生日期' }}
            <text class="picker-arrow">›</text>
          </view>
        </picker>
      </view>
    </view>
    
    <view class="section">
      <text class="section-title">📞 联系信息</text>
      
      <view class="input-wrap">
        <text class="input-label">手机号</text>
        <input class="uni-input" v-model="form.phone" placeholder="请输入手机号" type="number"/>
      </view>
      
      <view class="input-wrap">
        <text class="input-label">邮箱</text>
        <input class="uni-input" v-model="form.email" placeholder="请输入邮箱"/>
      </view>
      
      <view class="input-wrap">
        <text class="input-label">地址</text>
        <textarea class="uni-textarea" v-model="form.address" placeholder="请输入地址"/>
      </view>
    </view>
    
    <view class="action-btn">
      <button class="save-btn" @click="saveProfile">保存修改</button>
    </view>
  </view>
</template>

<script>
import { getBaseUrl } from '@/utils/api.js'
export default {
  data() {
    return {
      form: {
        username: '',
        realName: '',
        gender: '',
        birthday: '',
        phone: '',
        email: '',
        address: ''
      }
    }
  },
  onLoad() {
    this.getUserInfo()
  },
  methods: {
    getUserInfo() {
      const token = uni.getStorageSync('token')
      if (!token) return
      
      uni.request({
        url: getBaseUrl() + '/user/info',
        method: 'GET',
        header: {
          'Authorization': 'Bearer ' + token
        },
        success: res => {
          if (res.data && res.data.code === 200) {
            const data = res.data.data
            this.form.username = data.username || ''
            this.form.realName = data.realName || ''
            this.form.gender = data.gender || ''
            this.form.birthday = data.birthday || ''
            this.form.phone = data.phone || ''
            this.form.email = data.email || ''
            this.form.address = data.address || ''
          }
        }
      })
    },
    onDateChange(e) {
      this.form.birthday = e.detail.value
    },
    saveProfile() {
      const token = uni.getStorageSync('token')
      if (!token) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        return
      }
      
      uni.showLoading({ title: '保存中...' })
      
      uni.request({
        url: getBaseUrl() + '/user/update',
        method: 'POST',
        header: {
          'Authorization': 'Bearer ' + token,
          'content-type': 'application/json;charset=utf-8'
        },
        data: this.form,
        success: res => {
          uni.hideLoading()
          if (res.data && res.data.code === 200) {
            uni.showToast({ title: res.data.msg, icon: 'success' })
            setTimeout(() => {
              uni.navigateBack()
            }, 1500)
          } else {
            uni.showToast({ title: res.data ? res.data.msg : '保存失败', icon: 'none' })
          }
        },
        fail: err => {
          uni.hideLoading()
          uni.showToast({ title: '网络请求失败', icon: 'none' })
        }
      })
    }
  }
}
</script>

<style>
.edit-box {
  width: 100%;
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}
.section {
  background: #fff;
  margin: 30rpx;
  border-radius: 20rpx;
  padding: 30rpx;
}
.section-title {
  display: block;
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}
.input-wrap {
  margin-bottom: 30rpx;
}
.input-wrap:last-child {
  margin-bottom: 0;
}
.input-label {
  display: block;
  font-size: 26rpx;
  color: #999;
  margin-bottom: 10rpx;
}
.uni-input {
  width: 100%;
  height: 80rpx;
  background: #fafafa;
  border-radius: 12rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}
.uni-textarea {
  width: 100%;
  height: 160rpx;
  background: #fafafa;
  border-radius: 12rpx;
  padding: 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}
.gender-select {
  display: flex;
  gap: 30rpx;
}
.gender-item {
  flex: 1;
  height: 80rpx;
  background: #fafafa;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  color: #666;
  border: 2rpx solid transparent;
}
.gender-item.active {
  background: #fff0f5;
  border-color: #ff789c;
  color: #ff789c;
}
.picker-value {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 80rpx;
  background: #fafafa;
  border-radius: 12rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  color: #333;
}
.picker-arrow {
  color: #999;
}
.action-btn {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 30rpx;
  background: #fff;
  border-top: 1rpx solid #f0f0f0;
}
.save-btn {
  width: 100%;
  background: linear-gradient(to right, #ffb399, #ff789c);
  color: #fff;
  border: none;
  border-radius: 50rpx;
  padding: 28rpx;
  font-size: 32rpx;
}
</style>
