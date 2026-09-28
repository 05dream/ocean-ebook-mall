<template>
  <view class="cart-box">
    <view class="navBar">
      <text class="title">🛒 购物车</text>
      <view class="edit-btn" @click="toggleEdit">
        {{ isEdit ? '完成' : '编辑' }}
      </view>
    </view>

    <view class="cart-list">
      <view class="cart-item" v-for="item in cartList" :key="item.cartId">
        <view class="checkbox" @click="toggleSelect(item.cartId)">
          <text :class="selectedIds.includes(item.cartId) ? 'checked' : 'unchecked'">
            {{ selectedIds.includes(item.cartId) ? '✓' : '' }}
          </text>
        </view>
        <image :src="item.image || defaultImage" mode="widthFix"></image>
        <view class="item-info">
          <text class="item-name">{{ item.name }}</text>
          <text class="item-category">{{ item.category }}</text>
          <view class="item-bottom">
            <text class="item-price">¥{{ item.price ? item.price.toFixed(2) : '0.00' }}</text>
            <view class="qty-control">
              <view class="qty-btn" @click="decreaseQty(item)">-</view>
              <text class="qty-num">{{ item.quantity }}</text>
              <view class="qty-btn" @click="increaseQty(item)">+</view>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view class="empty" v-if="cartList.length === 0">
      <text class="empty-icon">🐟</text>
      <text class="empty-text">购物车空空如也</text>
      <button class="go-shop-btn" @click="goShop">去逛逛</button>
    </view>

    <!-- 收货地址 -->
    <view class="address-section" v-if="cartList.length > 0">
      <view class="address-title">📍 收货地址</view>
      <textarea
        class="address-input"
        v-model="userAddress"
        placeholder="请输入收货地址（姓名、电话、详细地址）"
        maxlength="200"
      />
    </view>

    <view class="footer">
      <view class="select-all" @click="toggleSelectAll">
        <text :class="isAllSelected ? 'checked' : 'unchecked'">
          {{ isAllSelected ? '✓' : '' }}
        </text>
        <text>全选</text>
      </view>
      <view class="total">
        <text>合计：</text>
        <text class="price">¥{{ totalPrice.toFixed(2) }}</text>
      </view>
      <button v-if="!isEdit" class="checkout-btn" @click="checkout">去结算({{ selectedIds.length }})</button>
      <button v-else class="delete-btn" @click="batchDelete">删除({{ selectedIds.length }})</button>
    </view>
  </view>
</template>

<script>
import { getBaseUrl } from '@/utils/api.js'
export default {
  data() {
    return {
      cartList: [],
      selectedIds: [],
      isEdit: false,
      userAddress: '',
      defaultImage: 'https://picsum.photos/400/300?random=1'
    }
  },
  computed: {
    isAllSelected() {
      return this.cartList.length > 0 && this.selectedIds.length === this.cartList.length
    },
    totalPrice() {
      return this.cartList
        .filter(item => this.selectedIds.includes(item.cartId))
        .reduce((sum, item) => sum + item.quantity * (item.price || 0), 0)
    }
  },
  onShow() {
    this.getCartList()
    this.getUserAddress()
  },
  methods: {
    getCartList() {
      const token = uni.getStorageSync('token')
      if (!token) {
        uni.showToast({ icon: 'none', title: '请先登录' })
        return
      }

      uni.request({
        url: getBaseUrl() + "/cart/list",
        method: 'GET',
        header: {
          'Authorization': token
        },
        success: res => {
          if (res.data && res.data.code === 200 && Array.isArray(res.data.data)) {
            this.cartList = res.data.data
          }
        },
        fail: () => {
          uni.showToast({ icon: 'none', title: '网络错误' })
        }
      })
    },
    getUserAddress() {
      const token = uni.getStorageSync('token')
      if (!token) return
      uni.request({
        url: getBaseUrl() + '/user/info',
        method: 'GET',
        header: { 'Authorization': 'Bearer ' + token },
        success: res => {
          if (res.data && res.data.code === 200 && res.data.data) {
            this.userAddress = res.data.data.address || ''
          }
        }
      })
    },
    toggleEdit() {
      this.isEdit = !this.isEdit
    },
    toggleSelect(id) {
      const index = this.selectedIds.indexOf(id)
      if (index > -1) {
        this.selectedIds.splice(index, 1)
      } else {
        this.selectedIds.push(id)
      }
    },
    toggleSelectAll() {
      if (this.isAllSelected) {
        this.selectedIds = []
      } else {
        this.selectedIds = this.cartList.map(item => item.cartId)
      }
    },
    decreaseQty(item) {
      if (item.quantity > 1) {
        this.updateQty(item.cartId, item.quantity - 1)
      }
    },
    increaseQty(item) {
      this.updateQty(item.cartId, item.quantity + 1)
    },
    updateQty(goodsId, quantity) {
      const token = uni.getStorageSync('token')
      if (!token) return

      uni.request({
        url: getBaseUrl() + "/cart/update",
        method: 'POST',
        header: {
          'Authorization': token,
          'content-type': 'application/json'
        },
        data: {
          cartId: goodsId,
          quantity: quantity
        },
        success: res => {
          if (res.data && res.data.code === 200) {
            this.getCartList()
          }
        }
      })
    },
    checkout() {
      if (this.selectedIds.length === 0) {
        uni.showToast({ icon: 'none', title: '请选择商品' })
        return
      }
      if (!this.userAddress || this.userAddress.trim() === '') {
        uni.showToast({ icon: 'none', title: '请填写收货地址' })
        return
      }

      const token = uni.getStorageSync('token')
      if (!token) {
        uni.showToast({ icon: 'none', title: '请先登录' })
        return
      }

      const cartItems = this.cartList
        .filter(item => this.selectedIds.includes(item.cartId))
        .map(item => ({
          goodsId: item.id,
          quantity: item.quantity
        }))

      uni.showLoading({ title: '下单中...' })
      uni.request({
        url: getBaseUrl() + "/order/add",
        method: 'POST',
        header: {
          'Authorization': token,
          'content-type': 'application/json'
        },
        data: {
          cartListJson: JSON.stringify(cartItems),
          totalPrice: this.totalPrice,
          address: this.userAddress.trim()
        },
        success: res => {
          uni.hideLoading()
          if (res.data && res.data.code === 200) {
            uni.showToast({ icon: 'success', title: '下单成功' })
            this.selectedIds = []
            this.getCartList()
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
    },
    goShop() {
      uni.switchTab({ url: '/pages/index/index' })
    },
    batchDelete() {
      if (this.selectedIds.length === 0) {
        uni.showToast({ icon: 'none', title: '请选择商品' })
        return
      }

      const token = uni.getStorageSync('token')
      if (!token) return

      uni.showModal({
        title: '提示',
        content: '确定删除选中商品吗？',
        success: (res) => {
          if (res.confirm) {
            uni.request({
              url: getBaseUrl() + "/cart/batchDelete",
              method: 'POST',
              header: {
                'Authorization': token,
                'content-type': 'application/json'
              },
              data: this.selectedIds,
              success: res => {
                if (res.data && res.data.code === 200) {
                  uni.showToast({ icon: 'success', title: '删除成功' })
                  this.selectedIds = []
                  this.getCartList()
                }
              }
            })
          }
        }
      })
    }
  }
}
</script>

<style>
.cart-box {
  min-height: 100vh;
  background: linear-gradient(to bottom, #E0F4FF, #B8E6FF);
}
.navBar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(to right, #0066CC, #00BFFF);
  padding: 100rpx 30rpx 30rpx;
}
.navBar .title {
  font-size: 38rpx;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 2rpx 4rpx rgba(0,0,0,0.2);
}
.edit-btn {
  font-size: 28rpx;
  color: rgba(255,255,255,0.9);
  padding: 10rpx 20rpx;
  border: 1rpx solid rgba(255,255,255,0.5);
  border-radius: 20rpx;
}
.cart-list {
  padding: 20rpx;
}
.cart-item {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 20rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  gap: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 102, 204, 0.06);
}
.checkbox {
  width: 50rpx;
  height: 50rpx;
  border-radius: 50%;
  border: 2rpx solid #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.checked {
  background: linear-gradient(to right, #00BFFF, #0066CC);
  color: #fff;
  font-size: 28rpx;
  border: none;
}
.unchecked {
  background: transparent;
}
.cart-item image {
  width: 160rpx;
  height: 120rpx;
  border-radius: 16rpx;
  flex-shrink: 0;
}
.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}
.item-name {
  font-size: 30rpx;
  color: #333;
  font-weight: bold;
}
.item-category {
  font-size: 24rpx;
  color: #888;
}
.item-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.item-price {
  font-size: 32rpx;
  color: #FF6B35;
  font-weight: bold;
}
.qty-control {
  display: flex;
  align-items: center;
  gap: 16rpx;
}
.qty-btn {
  width: 50rpx;
  height: 50rpx;
  background: linear-gradient(to right, #E0F4FF, #B8E6FF);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  color: #0066CC;
}
.qty-num {
  font-size: 28rpx;
  color: #333;
  min-width: 60rpx;
  text-align: center;
}
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 150rpx;
}
.address-section {
  margin: 20rpx;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
}
.address-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 16rpx;
}
.address-input {
  width: 100%;
  min-height: 120rpx;
  background: #fafafa;
  border-radius: 12rpx;
  padding: 16rpx;
  font-size: 26rpx;
  color: #333;
  box-sizing: border-box;
}
.empty-icon {
  font-size: 120rpx;
  margin-bottom: 30rpx;
}
.empty-text {
  font-size: 32rpx;
  color: #888;
  margin-bottom: 40rpx;
}
.go-shop-btn {
  background: linear-gradient(to right, #00BFFF, #0066CC);
  color: #fff;
  font-size: 30rpx;
  padding: 20rpx 60rpx;
  border-radius: 30rpx;
  border: none;
}
.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  display: flex;
  align-items: center;
  border-top: 1rpx solid rgba(0, 102, 204, 0.1);
  box-shadow: 0 -4rpx 16rpx rgba(0,0,0,0.05);
}
.select-all {
  display: flex;
  align-items: center;
  gap: 10rpx;
  font-size: 28rpx;
  color: #333;
}
.total {
  flex: 1;
  text-align: right;
  font-size: 28rpx;
  color: #333;
}
.price {
  font-size: 38rpx;
  color: #FF6B35;
  font-weight: bold;
}
.checkout-btn {
  background: linear-gradient(to right, #FF6B35, #FF8C42);
  color: #fff;
  font-size: 28rpx;
  padding: 20rpx 40rpx;
  border-radius: 30rpx;
  border: none;
  margin-left: 20rpx;
}
.delete-btn {
  background: #999;
  color: #fff;
  font-size: 28rpx;
  padding: 20rpx 40rpx;
  border-radius: 30rpx;
  border: none;
  margin-left: 20rpx;
}
</style>