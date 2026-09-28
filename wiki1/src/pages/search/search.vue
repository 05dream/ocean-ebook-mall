<template>
  <view class="search-box">
    <view class="navBar">
      <view class="left" @click="toBack">
        <text class="back-icon">‹</text>
      </view>
      <view class="center">
        <input type="text" v-model="searchKey" placeholder="输入海洋生物名称" @confirm="search" />
        <text class="search-icon" @click="search">🔍</text>
      </view>
      <view class="right" @click="search">搜索</view>
    </view>
    
    <view class="colorBg"></view>
    
    <view class="result-count" v-if="searchKey">搜索结果：{{ total }}种生物</view>
    
    <view class="goodsList">
      <view class="item" v-for="item in goodsList" :key="item.docId" @click="toDetail(item.docId)">
        <image :src="item.image || defaultImage" mode="widthFix"></image>
        <view class="goodsName-price">
          <view class="goodsName">{{ item.docTitle }}</view>
          <view class="goodsDesc">{{ item.docDesc }}</view>
          <view class="goodsBottom">
            <text class="price">¥{{ item.price ? item.price.toFixed(2) : '0.00' }}</text>
            <view class="goodsMeta">
              <text class="category">{{ item.category }}</text>
              <text class="views">阅读 {{ item.views }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <view class="empty" v-if="goodsList.length === 0 && !loading">
      <text class="empty-icon">🔍</text>
      <text class="empty-text">{{ searchKey ? '未找到相关海洋生物' : '请输入关键词搜索' }}</text>
    </view>
    
    <uni-load-more :status="status" :icon-size="16" :content-text="contentText" v-if="goodsList.length > 0"></uni-load-more>
  </view>
</template>

<script>
import { getBaseUrl } from '@/utils/api.js'
export default {
  data() {
    return {
      goodsList: [],
      currentPage: 1,
      pageSize: 6,
      totalPages: 1,
      total: 0,
      searchKey: "",
      status: "no-more",
      loading: false,
      contentText: {
        contentdown: "上拉加载更多",
        contentrefresh: "正在加载",
        contentnomore: "没有更多数据"
      },
      defaultImage: 'https://picsum.photos/400/300?random=1'
    }
  },
  onLoad(options) {
    if (options && options.keyword) {
      this.searchKey = decodeURIComponent(options.keyword)
      this.search()
    } else {
      this.getPageQueryByGoods()
    }
  },
  onReachBottom() {
    if (this.loading) return
    if (this.currentPage >= this.totalPages) {
      this.status = "no-more"
      return
    }
    this.currentPage++
    this.getPageQueryByGoods()
  },
  methods: {
    toBack() {
      uni.navigateBack({ delta: 1 })
    },
    toDetail(id) {
      uni.navigateTo({ url: '/pages/doc-detail/doc-detail?id=' + id })
    },
    search() {
      if (!this.searchKey.trim()) {
        uni.showToast({ icon: "none", title: '请输入搜索关键词' })
        return
      }
      this.currentPage = 1
      this.goodsList = []
      this.getPageQueryByGoods()
    },
    getPageQueryByGoods() {
      const token = uni.getStorageSync("token")
      if (!token) {
        uni.showToast({ icon: "error", title: '请先登录' })
        return
      }
      
      this.loading = true
      this.status = "loading"
      
      uni.request({
        url: getBaseUrl() + "/doc/getPageQueryByDoc",
        method: 'GET',
        header: {
          'Authorization': token
        },
        data: {
          keyword: this.searchKey,
          current: this.currentPage,
          pageSize: this.pageSize
        },
        success: res => {
          this.loading = false
          if (res.data && res.data.code === 200) {
            const data = res.data.data
            if (data && data.records && Array.isArray(data.records)) {
              this.total = data.total || 0
              this.totalPages = data.pages || 1
              
              if (this.currentPage === 1) {
                this.goodsList = [...data.records]
              } else {
                this.goodsList = this.goodsList.concat(data.records)
              }
              
              this.status = this.currentPage >= this.totalPages ? "no-more" : "contentdown"
            } else {
              this.status = "no-more"
            }
          } else {
            uni.showToast({ icon: "none", title: res.data.msg || '获取数据失败' })
            this.status = "no-more"
          }
        },
        fail: () => {
          this.loading = false
          uni.showToast({ icon: "none", title: '网络请求失败' })
          this.status = "no-more"
        }
      })
    }
  }
}
</script>

<style>
.search-box {
  width: 100%;
  min-height: 100vh;
  background: #f0f8ff;
}
.navBar {
  display: flex;
  align-items: center;
  padding: 0 20rpx;
  padding-top: 100rpx;
  background: #fff;
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
  color: #1E90FF;
}
.navBar .center {
  flex: 1;
  position: relative;
  background: #f5f5f5;
  border-radius: 50rpx;
  padding: 0 30rpx;
  height: 70rpx;
  display: flex;
  align-items: center;
}
.navBar .center input {
  flex: 1;
  font-size: 28rpx;
  background: transparent;
}
.navBar .search-icon {
  font-size: 32rpx;
}
.navBar .right {
  width: 100rpx;
  text-align: center;
  color: #1E90FF;
  font-size: 30rpx;
}
.colorBg {
  height: 180rpx;
}
.result-count {
  padding: 20rpx;
  font-size: 26rpx;
  color: #999;
}
.goodsList {
  padding: 0 20rpx;
}
.goodsList .item {
  display: flex;
  background: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
}
.goodsList .item image {
  width: 200rpx;
  height: 150rpx;
  border-radius: 12rpx;
  margin-right: 20rpx;
}
.goodsName-price {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.goodsName {
  font-size: 30rpx;
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
.goodsBottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.goodsBottom .price {
  font-size: 32rpx;
  color: #FF4500;
  font-weight: bold;
}
.goodsMeta {
  display: flex;
  align-items: center;
  gap: 10rpx;
  font-size: 22rpx;
  color: #666;
}
.category {
  background: #E0F4FF;
  color: #1E90FF;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 200rpx;
}
.empty-icon {
  font-size: 100rpx;
  margin-bottom: 30rpx;
}
.empty-text {
  font-size: 32rpx;
  color: #999;
}
</style>