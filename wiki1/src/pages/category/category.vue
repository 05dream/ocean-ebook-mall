<template>
  <view class="category-box">
    <view class="navBar">
      <text class="title">🐠 书籍分类</text>
      <view class="search-icon" @click="toSearch">🔍</view>
    </view>
    
    <view class="content">
      <scroll-view class="category-scroll" scroll-y :style="{height: windowHeight + 'px'}">
        <view 
          class="category-item" 
          :class="{active: currentCategory === item}"
          v-for="item in categoryList" 
          :key="item" 
          @click="switchCategory(item)"
        >
          {{ item }}
        </view>
      </scroll-view>
      
      <scroll-view class="goods-scroll" scroll-y :style="{height: windowHeight + 'px'}">
        <view class="goods-header">
          <text class="category-name">{{ currentCategory || '全部' }}</text>
          <text class="goods-count">{{ goodsList.length }}本图书</text>
        </view>
        
        <view class="goodsList">
          <view class="item" v-for="item in goodsList" :key="item.docId" @click="toDetail(item.docId)">
            <image :src="item.image || defaultImage" mode="widthFix"></image>
            <view class="goodsInfo">
              <view class="goodsName">{{ item.docTitle }}</view>
              <view class="goodsDesc">{{ item.docDesc }}</view>
              <view class="goodsMeta">
                <text class="author">{{ item.author }}</text>
                <text class="views">阅读 {{ item.views }}</text>
              </view>
              <view class="goodsBottom">
                <text class="price">¥{{ item.price ? item.price.toFixed(2) : '0.00' }}</text>
                <text class="stock">库存 {{ item.stock || 0 }}</text>
              </view>
            </view>
          </view>
        </view>
        
        <view class="empty-tip" v-if="goodsList.length === 0">
          <text class="empty-icon">📚</text>
          <text>暂无该分类下的书籍</text>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script>
import { getBaseUrl } from '@/utils/api.js'
export default {
  data() {
    return {
      categoryList: [],
      goodsList: [],
      currentCategory: '',
      windowHeight: 600,
      defaultImage: 'https://picsum.photos/400/300?random=1'
    }
  },
  onLoad() {
    const systemInfo = uni.getSystemInfoSync()
    this.windowHeight = systemInfo.windowHeight - 80
    this.getCategoryList()
  },
  methods: {
    toSearch() {
      uni.navigateTo({ url: '/pages/search/search' })
    },
    toDetail(id) {
      uni.navigateTo({ url: '/pages/doc-detail/doc-detail?id=' + id })
    },
    getCategoryList() {
      const token = uni.getStorageSync("token")
      if (!token) {
        uni.showToast({ icon: "error", title: '请先登录' })
        return
      }
      
      uni.request({
        url: getBaseUrl() + "/doc/findCategoryList",
        method: 'GET',
        header: {
          'Authorization': token
        },
        success: res => {
          if (res.data && res.data.code === 200 && Array.isArray(res.data.data)) {
            this.categoryList = res.data.data
            if (this.categoryList.length > 0) {
              this.currentCategory = this.categoryList[0]
              this.findByCategory(this.currentCategory)
            }
          } else {
            this.categoryList = []
          }
        },
        fail: () => {
          uni.showToast({ icon: "none", title: '获取分类失败' })
          this.categoryList = []
        }
      })
    },
    switchCategory(category) {
      this.currentCategory = category
      this.findByCategory(category)
    },
    findByCategory(category) {
      const token = uni.getStorageSync("token")
      if (!token) {
        uni.showToast({ icon: "error", title: '请先登录' })
        return
      }
      
      uni.showLoading({ title: '加载中...' })
      uni.request({
        url: getBaseUrl() + "/doc/findByCategory",
        method: 'GET',
        header: {
          'Authorization': token
        },
        data: {
          category: category
        },
        success: res => {
          uni.hideLoading()
          if (res.data && res.data.code === 200 && Array.isArray(res.data.data)) {
            this.goodsList = res.data.data
          } else {
            this.goodsList = []
          }
        },
        fail: () => {
          uni.hideLoading()
          uni.showToast({ icon: "none", title: '获取数据失败' })
          this.goodsList = []
        }
      })
    }
  }
}
</script>

<style>
.category-box {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(to bottom, #E0F4FF, #B8E6FF);
  display: flex;
  flex-direction: column;
}
.navBar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 60rpx 30rpx 20rpx;
  background: linear-gradient(to right, #0066CC, #00BFFF);
}
.navBar .title {
  font-size: 38rpx;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 2rpx 4rpx rgba(0,0,0,0.2);
}
.navBar .search-icon {
  font-size: 36rpx;
}
.content {
  flex: 1;
  display: flex;
  overflow: hidden;
}
.category-scroll {
  width: 220rpx;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
}
.category-item {
  padding: 32rpx 20rpx;
  font-size: 28rpx;
  color: #555;
  text-align: center;
  border-left: 6rpx solid transparent;
  border-bottom: 1rpx solid rgba(0, 102, 204, 0.08);
  transition: all 0.2s;
}
.category-item.active {
  background: #fff;
  color: #0066CC;
  font-weight: bold;
  border-left-color: #00BFFF;
  box-shadow: 0 2rpx 12rpx rgba(0, 102, 204, 0.1);
}
.goods-scroll {
  flex: 1;
  background: rgba(255, 255, 255, 0.9);
}
.goods-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  border-bottom: 1rpx solid rgba(0, 102, 204, 0.1);
  background: linear-gradient(to right, #E0F4FF, #fff);
}
.category-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #0066CC;
}
.goods-count {
  font-size: 24rpx;
  color: #888;
}
.goodsList {
  padding: 20rpx;
}
.goodsList .item {
  display: flex;
  background: #fff;
  border-radius: 20rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 102, 204, 0.06);
  transition: transform 0.2s;
}
.goodsList .item:active {
  transform: scale(0.98);
}
.goodsList .item image {
  width: 180rpx;
  height: 135rpx;
  border-radius: 16rpx;
  margin-right: 20rpx;
}
.goodsName-price {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.goodsInfo {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.goodsName {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}
.goodsDesc {
  font-size: 24rpx;
  color: #888;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  margin-bottom: 12rpx;
}
.goodsMeta {
  display: flex;
  justify-content: space-between;
  font-size: 22rpx;
  color: #666;
  margin-bottom: 12rpx;
}
.goodsBottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.goodsBottom .price {
  font-size: 34rpx;
  color: #FF6B35;
  font-weight: bold;
}
.goodsBottom .stock {
  font-size: 22rpx;
  color: #999;
  background: #f5f5f5;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}
.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx;
  color: #999;
  font-size: 28rpx;
}
.empty-icon {
  font-size: 80rpx;
  margin-bottom: 20rpx;
}
</style>