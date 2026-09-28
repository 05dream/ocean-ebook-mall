<template>
  <view class="detail-box">
    <view class="navBar">
      <view class="left" @click="toBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="title">商品详情</text>
      <view class="right">
        <text class="collect-icon" @click="toggleCollect">{{ isCollected ? '❤️' : '🤍' }}</text>
        <text class="cart-icon" @click="goCart">🛒</text>
        <text class="cart-badge" v-if="cartCount > 0">{{ cartCount }}</text>
      </view>
    </view>
    
    <scroll-view class="content" scroll-y>
      <view class="image-section" @click="previewImage">
        <image :src="doc.image || defaultImage" mode="widthFix"></image>
      </view>
      
      <view class="info-card">
        <view class="price-section">
          <text class="price-symbol">¥</text>
          <text class="price-integer">{{ priceParts[0] }}</text>
          <text class="price-decimal">.{{ priceParts[1] }}</text>
        </view>
        <view class="title-section">
          <text class="goods-name">{{ doc.docTitle }}</text>
        </view>
        <view class="spec-section">
          <view class="spec-row">
            <text class="spec-label">分类：</text>
            <text class="spec-value">{{ doc.category }}</text>
          </view>
          <view class="spec-row">
            <text class="spec-label">作者：</text>
            <text class="spec-value">{{ doc.author }}</text>
          </view>
          <view class="spec-row">
            <text class="spec-label">阅读量：</text>
            <text class="spec-value">{{ doc.views }}</text>
          </view>
        </view>
      </view>
      
      <view class="desc-card">
        <text class="desc-title">内容介绍</text>
        <text class="desc-content">{{ doc.docDesc }}</text>
      </view>
      
      <view class="quantity-card">
        <text class="quantity-label">购买数量</text>
        <view class="quantity-control">
          <view class="qty-btn minus" @click="decreaseQty">-</view>
          <text class="qty-num" @click="showQtyModal">{{ quantity }}</text>
          <view class="qty-btn plus" @click="increaseQty">+</view>
        </view>
      </view>
    </scroll-view>
    
    <view class="bottom-bar">
      <view class="bottom-left">
        <view class="bottom-item" @click="goHome">
          <text class="bottom-icon">🏠</text>
          <text class="bottom-text">首页</text>
        </view>
        <view class="bottom-item" @click="goCart">
          <text class="bottom-icon">🛒</text>
          <text class="bottom-text">购物车</text>
          <text class="bottom-badge" v-if="cartCount > 0">{{ cartCount }}</text>
        </view>
      </view>
      <view class="bottom-right">
        <button class="cart-btn" @click="addCart">加入购物车</button>
        <button class="buy-btn" @click="buyNow">立即购买</button>
      </view>
    </view>
    
    <view class="modal-overlay" v-if="showModal" @click="closeModal">
      <view class="modal-content" @click.stop>
        <text class="modal-title">输入数量</text>
        <input type="number" v-model="inputQty" class="modal-input" @confirm="confirmQty" />
        <view class="modal-tip">库存数量：{{ doc.stock || 100 }}</view>
        <view class="modal-btn-area">
          <button class="modal-cancel" @click="closeModal">取消</button>
          <button class="modal-confirm" @click="confirmQty">确定</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getBaseUrl } from '@/utils/api.js'
export default {
  data() {
    return {
      docId: 0,
      quantity: 1,
      inputQty: 1,
      showModal: false,
      cartCount: 0,
      isCollected: false,
      doc: {
        docTitle: '加载中...',
        category: '',
        author: '',
        views: 0,
        docDesc: '',
        image: '',
        price: 0,
        stock: 100
      },
      defaultImage: 'https://picsum.photos/400/300?random=1'
    }
  },
  onLoad(options) {
    if (options && options.id) {
      this.docId = parseInt(options.id)
      this.getDocDetail()
    }
  },
  onShow() {
    this.getCartCount()
    this.checkCollectStatus()
  },
  computed: {
    priceParts() {
      return Number(this.doc.price || 0).toFixed(2).split('.')
    }
  },
  methods: {
    toBack() {
      uni.navigateBack({ delta: 1 })
    },
    goHome() {
      uni.switchTab({ url: '/pages/index/index' })
    },
    goCart() {
      uni.switchTab({ url: '/pages/cart/cart' })
    },
    previewImage() {
      const urls = [this.doc.image || this.defaultImage]
      uni.previewImage({
        current: 0,
        urls: urls
      })
    },
    getDocDetail() {
      uni.request({
        url: getBaseUrl() + "/doc/getById",
        method: 'GET',
        data: {
          id: this.docId
        },
        success: res => {
          if (res.data && res.data.code === 200) {
            this.doc = res.data.data
          } else {
            uni.showToast({ icon: 'none', title: '获取失败' })
          }
        },
        fail: () => {
          uni.showToast({ icon: 'none', title: '网络错误' })
        }
      })
    },
    getCartCount() {
      const token = uni.getStorageSync('token')
      if (!token) return
      
      uni.request({
        url: getBaseUrl() + "/cart/count",
        method: 'GET',
        header: {
          'Authorization': token
        },
        success: res => {
          if (res.data && res.data.code === 200) {
            this.cartCount = res.data.data || 0
          }
        }
      })
    },
    checkCollectStatus() {
      const token = uni.getStorageSync('token')
      if (!token) {
        this.isCollected = false
        return
      }
      
      uni.request({
        url: getBaseUrl() + "/collection/check",
        method: 'GET',
        header: {
          'Authorization': token
        },
        data: {
          docId: this.docId
        },
        success: res => {
          if (res.data && res.data.code === 200) {
            this.isCollected = res.data.data || false
          }
        }
      })
    },
    toggleCollect() {
      const token = uni.getStorageSync('token')
      if (!token) {
        uni.showToast({ icon: 'none', title: '请先登录' })
        uni.navigateTo({ url: '/pages/login/login' })
        return
      }
      
      const url = this.isCollected 
        ? getBaseUrl() + "/collection/remove"
        : getBaseUrl() + "/collection/add"
      
      uni.request({
        url: url,
        method: 'POST',
        header: {
          'Authorization': token,
          'content-type': 'application/json'
        },
        data: {
          docId: this.docId
        },
        success: res => {
          if (res.data && res.data.code === 200) {
            this.isCollected = !this.isCollected
            uni.showToast({ 
              icon: 'success', 
              title: this.isCollected ? '收藏成功' : '取消收藏成功' 
            })
          } else {
            uni.showToast({ icon: 'none', title: res.data.msg || '操作失败' })
          }
        },
        fail: () => {
          uni.showToast({ icon: 'none', title: '网络错误' })
        }
      })
    },
    decreaseQty() {
      if (this.quantity > 1) {
        this.quantity--
      }
    },
    increaseQty() {
      const stock = this.doc.stock || 100
      if (this.quantity < stock) {
        this.quantity++
      } else {
        uni.showToast({ icon: 'none', title: '库存不足' })
      }
    },
    showQtyModal() {
      this.inputQty = this.quantity
      this.showModal = true
    },
    closeModal() {
      this.showModal = false
    },
    confirmQty() {
      const stock = this.doc.stock || 100
      const qty = parseInt(this.inputQty)
      if (!isNaN(qty) && qty > 0 && qty <= stock) {
        this.quantity = qty
        this.showModal = false
      } else {
        uni.showToast({ icon: 'none', title: '请输入有效数量' })
      }
    },
    addCart() {
      const token = uni.getStorageSync('token')
      if (!token) {
        uni.showToast({ icon: 'none', title: '请先登录' })
        uni.navigateTo({ url: '/pages/login/login' })
        return
      }
      
      uni.showLoading({ title: '添加中...' })
      uni.request({
        url: getBaseUrl() + "/cart/add",
        method: 'POST',
        header: {
          'Authorization': token,
          'content-type': 'application/json'
        },
        data: {
          goodsId: this.docId,
          quantity: this.quantity
        },
        success: res => {
          uni.hideLoading()
          if (res.data && res.data.code === 200) {
            uni.showToast({ icon: 'success', title: res.data.msg })
            this.getCartCount()
          } else {
            uni.showToast({ icon: 'none', title: res.data.msg || '添加失败' })
          }
        },
        fail: () => {
          uni.hideLoading()
          uni.showToast({ icon: 'none', title: '网络错误' })
        }
      })
    },
    buyNow() {
      const token = uni.getStorageSync('token')
      if (!token) {
        uni.showToast({ icon: 'none', title: '请先登录' })
        uni.navigateTo({ url: '/pages/login/login' })
        return
      }

      // 先获取用户保存的地址
      uni.request({
        url: getBaseUrl() + '/user/info',
        method: 'GET',
        header: { 'Authorization': 'Bearer ' + token },
        success: res => {
          let address = ''
          if (res.data && res.data.code === 200 && res.data.data) {
            address = res.data.data.address || ''
          }
          if (!address) {
            uni.showToast({ icon: 'none', title: '请先在个人中心填写地址' })
            return
          }
          this.createOrder(address)
        },
        fail: () => {
          uni.showToast({ icon: 'none', title: '获取地址失败' })
        }
      })
    },
    createOrder(address) {
      const token = uni.getStorageSync('token')
      const cartList = [{ goodsId: this.docId, quantity: this.quantity }]
      const totalPrice = (this.doc.price || 0) * this.quantity

      uni.showLoading({ title: '下单中...' })
      uni.request({
        url: getBaseUrl() + "/order/add",
        method: 'POST',
        header: {
          'Authorization': token,
          'content-type': 'application/json'
        },
        data: {
          cartListJson: JSON.stringify(cartList),
          totalPrice: totalPrice,
          address: address
        },
        success: res => {
          uni.hideLoading()
          if (res.data && res.data.code === 200) {
            uni.showToast({ icon: 'success', title: '下单成功' })
            uni.navigateTo({ url: '/pages/order/order?id=' + res.data.data })
          } else {
            uni.showToast({ icon: 'none', title: res.data.msg || '下单失败' })
          }
        },
        fail: () => {
          uni.hideLoading()
          uni.showToast({ icon: 'none', title: '网络错误' })
        }
      })
    }
  }
}
</script>

<style>
.detail-box {
  min-height: 100vh;
  background: #f5f5f5;
  display: flex;
  flex-direction: column;
}
.navBar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 60rpx 30rpx 20rpx;
  background: #fff;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
}
.navBar .left {
  width: 80rpx;
}
.back-icon {
  font-size: 50rpx;
  color: #333;
}
.navBar .title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}
.navBar .right {
  width: 120rpx;
  position: relative;
  text-align: right;
}
.collect-icon {
  font-size: 36rpx;
  margin-right: 10rpx;
}
.cart-icon {
  font-size: 36rpx;
}
.cart-badge {
  position: absolute;
  top: -10rpx;
  right: -10rpx;
  background: #FF4500;
  color: #fff;
  font-size: 20rpx;
  padding: 4rpx 10rpx;
  border-radius: 20rpx;
}
.content {
  flex: 1;
  padding-top: 140rpx;
  padding-bottom: 140rpx;
}
.image-section {
  background: #fff;
  padding: 20rpx;
}
.image-section image {
  width: 100%;
  border-radius: 16rpx;
}
.info-card {
  background: #fff;
  margin-top: 20rpx;
  padding: 30rpx;
}
.price-section {
  display: flex;
  align-items: baseline;
  margin-bottom: 20rpx;
}
.price-symbol {
  font-size: 32rpx;
  color: #FF4500;
  font-weight: bold;
}
.price-integer {
  font-size: 48rpx;
  color: #FF4500;
  font-weight: bold;
}
.price-decimal {
  font-size: 28rpx;
  color: #FF4500;
  font-weight: bold;
}
.title-section {
  margin-bottom: 20rpx;
}
.goods-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  line-height: 1.5;
}
.spec-section {
  background: #fafafa;
  padding: 20rpx;
  border-radius: 12rpx;
}
.spec-row {
  display: flex;
  padding: 10rpx 0;
}
.spec-label {
  font-size: 26rpx;
  color: #999;
  width: 120rpx;
}
.spec-value {
  font-size: 26rpx;
  color: #333;
}
.desc-card {
  background: #fff;
  margin-top: 20rpx;
  padding: 30rpx;
}
.desc-title {
  display: block;
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}
.desc-content {
  font-size: 28rpx;
  color: #666;
  line-height: 1.8;
}
.quantity-card {
  background: #fff;
  margin-top: 20rpx;
  padding: 30rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.quantity-label {
  font-size: 30rpx;
  color: #333;
}
.quantity-control {
  display: flex;
  align-items: center;
  gap: 40rpx;
}
.qty-btn {
  width: 70rpx;
  height: 70rpx;
  background: #f5f5f5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  color: #333;
}
.qty-btn.minus:active, .qty-btn.plus:active {
  background: #ddd;
}
.qty-num {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  min-width: 80rpx;
  text-align: center;
}
.bottom-bar {
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
}
.bottom-left {
  display: flex;
  gap: 40rpx;
}
.bottom-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}
.bottom-icon {
  font-size: 36rpx;
}
.bottom-text {
  font-size: 22rpx;
  color: #666;
}
.bottom-badge {
  position: absolute;
  top: -10rpx;
  right: -15rpx;
  background: #FF4500;
  color: #fff;
  font-size: 20rpx;
  padding: 4rpx 10rpx;
  border-radius: 20rpx;
}
.bottom-right {
  flex: 1;
  display: flex;
  gap: 20rpx;
  margin-left: 40rpx;
}
.cart-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  background: #fff;
  color: #1E90FF;
  font-size: 32rpx;
  border-radius: 40rpx;
  border: 2rpx solid #1E90FF;
}
.buy-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  background: #1E90FF;
  color: #fff;
  font-size: 32rpx;
  border-radius: 40rpx;
  border: none;
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
}
.modal-content {
  background: #fff;
  width: 500rpx;
  border-radius: 20rpx;
  padding: 40rpx;
}
.modal-title {
  display: block;
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  text-align: center;
  margin-bottom: 30rpx;
}
.modal-input {
  width: 100%;
  height: 80rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  padding: 0 20rpx;
  font-size: 32rpx;
  text-align: center;
}
.modal-tip {
  font-size: 24rpx;
  color: #999;
  text-align: center;
  margin-top: 20rpx;
}
.modal-btn-area {
  display: flex;
  gap: 20rpx;
  margin-top: 30rpx;
}
.modal-cancel {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  background: #f5f5f5;
  color: #666;
  font-size: 30rpx;
  border-radius: 40rpx;
  border: none;
}
.modal-confirm {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  background: #1E90FF;
  color: #fff;
  font-size: 30rpx;
  border-radius: 40rpx;
  border: none;
}
</style>