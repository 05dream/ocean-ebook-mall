<template>
  <view class="collection-box">
    <view class="navBar">
      <view class="left" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <text class="title">❤️ 我的收藏</text>
      <view class="right"></view>
    </view>
    
    <view class="colorBg"></view>
    
    <view class="goodsList">
      <view class="item" v-for="item in collections" :key="item.docId" @click="toDetail(item.docId)">
        <image :src="item.image || defaultImage" mode="widthFix"></image>
        <view class="goodsName-price">
          <view class="goodsName">{{ item.docTitle }}</view>
          <view class="goodsDesc">{{ item.docDesc }}</view>
          <view class="goodsMeta">
            <text class="category-tag">{{ item.category }}</text>
            <text class="views">阅读 {{ item.views }}</text>
          </view>
        </view>
        <view class="delete-btn" @click.stop="removeCollection(item.docId)">🗑️</view>
      </view>
    </view>
    
    <view class="empty-tip" v-if="collections.length === 0">
      <text class="empty-icon">📚</text>
      <text class="empty-text">暂无收藏</text>
    </view>
  </view>
</template>

<script>
import { getBaseUrl } from '@/utils/api.js'
export default {
  data() {
    return {
      collections: [],
      defaultImage: 'https://picsum.photos/400/300?random=1'
    }
  },
  onShow() {
    this.getCollections()
  },
  methods: {
    goBack() {
      uni.navigateBack({ delta: 1 })
    },
    getCollections() {
      const token = uni.getStorageSync('token')
      if (!token) {
        uni.showToast({ icon: 'none', title: '请先登录' })
        return
      }
      
      uni.showLoading({ title: '加载中...' })
      uni.request({
        url: getBaseUrl() + "/collection/list",
        method: 'GET',
        header: {
          'Authorization': token
        },
        success: res => {
          uni.hideLoading()
          if (res.data && res.data.code === 200 && Array.isArray(res.data.data)) {
            this.collections = res.data.data
          }
        },
        fail: () => {
          uni.hideLoading()
          uni.showToast({ icon: 'none', title: '网络请求失败' })
        }
      })
    },
    toDetail(id) {
      uni.navigateTo({ url: '/pages/doc-detail/doc-detail?id=' + id })
    },
    removeCollection(docId) {
      const token = uni.getStorageSync('token')
      if (!token) {
        uni.showToast({ icon: 'none', title: '请先登录' })
        return
      }
      
      uni.showModal({
        title: '提示',
        content: '确定取消收藏吗？',
        success: (res) => {
          if (res.confirm) {
            uni.request({
              url: getBaseUrl() + "/collection/remove",
              method: 'POST',
              header: {
                'Authorization': token,
                'content-type': 'application/json'
              },
              data: {
                docId: docId
              },
              success: res => {
                if (res.data && res.data.code === 200) {
                  this.collections = this.collections.filter(item => item.docId !== docId)
                  uni.showToast({ icon: 'success', title: '取消收藏成功' })
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
.collection-box {
  width: 100%;
  min-height: 100vh;
  background: #f0f8ff;
}
.navBar {
  display: flex;
  align-items: center;
  padding: 0 20rpx;
  padding-top: 100rpx;
  background: linear-gradient(to right, #1E90FF, #87CEEB);
  height: 180rpx;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
}
.navBar .left {
  width: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
.navBar .back-icon {
  font-size: 50rpx;
  color: #fff;
}
.navBar .title {
  flex: 1;
  text-align: center;
  font-size: 36rpx;
  color: #fff;
  font-weight: bold;
}
.navBar .right {
  width: 80rpx;
}
.colorBg {
  height: 180rpx;
}
.goodsList {
  padding: 20rpx;
}
.goodsList .item {
  display: flex;
  background: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
  position: relative;
}
.goodsList .item image {
  width: 180rpx;
  height: 135rpx;
  border-radius: 12rpx;
  margin-right: 20rpx;
}
.goodsName-price {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.goodsName {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 8rpx;
}
.goodsDesc {
  font-size: 24rpx;
  color: #999;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  margin-bottom: 10rpx;
}
.goodsMeta {
  display: flex;
  justify-content: space-between;
  font-size: 22rpx;
  color: #666;
}
.category-tag {
  background: #E3F2FD;
  color: #1E90FF;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
  font-size: 20rpx;
}
.delete-btn {
  position: absolute;
  right: 20rpx;
  bottom: 20rpx;
  font-size: 36rpx;
}
.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx;
}
.empty-icon {
  font-size: 80rpx;
  margin-bottom: 20rpx;
}
.empty-text {
  font-size: 28rpx;
  color: #999;
}
</style>