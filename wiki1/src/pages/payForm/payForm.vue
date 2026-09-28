<template>
  <view class="pay-form-box">
    <view class="loading" v-if="loading">
      <text>正在准备支付...</text>
    </view>
    <view v-else-if="errorMsg" class="error">
      <text>{{ errorMsg }}</text>
      <button class="retry-btn" @click="getPayForm">重新支付</button>
    </view>
    <web-view v-else-if="payUrl" :src="payUrl" @message="handleMessage"></web-view>
    <view v-else-if="payForm" class="form-container">
      <view v-html="payForm"></view>
    </view>
    <view v-else class="error">
      <text>支付失败，请重试</text>
      <button class="retry-btn" @click="getPayForm">重新支付</button>
    </view>
  </view>
</template>

<script>
import { getBaseUrl } from '@/utils/api.js'
export default {
  data() {
    return {
      loading: true,
      payForm: '',
      payUrl: '',
      orderId: null,
      subject: '',
      errorMsg: ''
    }
  },
  onLoad(options) {
    console.log('payForm onLoad:', options)
    if (options && options.orderId) {
      this.orderId = parseInt(options.orderId)
      this.subject = options.subject || '订单支付'
      this.getPayForm()
    } else {
      this.loading = false
      this.errorMsg = '订单参数错误'
    }
  },
  methods: {
    getPayForm() {
      const token = uni.getStorageSync('token')
      console.log('token:', token ? '存在' : '不存在')
      if (!token) {
        this.loading = false
        this.errorMsg = '请先登录'
        uni.showToast({ icon: 'none', title: '请先登录' })
        return
      }

      console.log('请求支付表单, orderId:', this.orderId, 'subject:', this.subject)

      uni.request({
        url: getBaseUrl() + "/alipay/pay",
        method: 'POST',
        header: {
          'Authorization': token,
          'content-type': 'application/json'
        },
        data: {
          orderId: this.orderId,
          subject: this.subject
        },
        success: res => {
          this.loading = false
          console.log('支付接口返回:', res)
          if (res.data && res.data.code === 200 && res.data.data) {
            const formHtml = res.data.data
            console.log('获取到支付表单, 长度:', formHtml.length)
            const platform = uni.getSystemInfoSync().platform
            if (platform === 'h5') {
              this.payForm = formHtml
              this.$nextTick(() => {
                const forms = document.querySelectorAll('form')
                console.log('找到表单数量:', forms.length)
                if (forms.length > 0) {
                  console.log('提交表单...')
                  forms[0].submit()
                } else {
                  this.errorMsg = '支付表单解析失败'
                }
              })
            } else {
              this.payUrl = 'http://你的公网域名/alipay/payPage?orderId=' + this.orderId + '&subject=' + encodeURIComponent(this.subject)
            console.log('小程序支付URL:', this.payUrl)
            }
          } else {
            const msg = res.data ? (res.data.msg || '获取支付表单失败') : '请求失败'
            console.log('获取支付表单失败:', msg)
            this.errorMsg = msg
            uni.showToast({ icon: 'none', title: msg })
          }
        },
        fail: (err) => {
          this.loading = false
          console.log('支付接口请求失败:', err)
          this.errorMsg = '网络请求失败'
          uni.showToast({ icon: 'none', title: '网络错误' })
        }
      })
    },
    handleMessage(e) {
      console.log('web-view message:', e)
      if (e.detail.data && e.detail.data.length > 0) {
        const data = e.detail.data[e.detail.data.length - 1]
        if (data.type === 'paySuccess') {
          uni.showToast({ icon: 'success', title: '支付成功' })
          setTimeout(() => {
            uni.navigateBack()
          }, 1500)
        }
      }
    }
  }
}
</script>

<style>
.pay-form-box {
  min-height: 100vh;
  background: #f5f5f5;
}
.form-container {
  width: 100%;
  min-height: 100vh;
}
.form-container form {
  display: none;
}
.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  color: #666;
  font-size: 32rpx;
}
.error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  color: #f00;
  font-size: 32rpx;
  gap: 30rpx;
}
.retry-btn {
  padding: 20rpx 60rpx;
  background: #1E90FF;
  color: #fff;
  border-radius: 40rpx;
  font-size: 28rpx;
}
</style>