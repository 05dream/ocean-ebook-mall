<template>
  <view class="page-box">
    <view class="navBar">
      <view class="left">🐬 海洋书城</view>
      <view class="right" @click="toSearch">
        <input class="search-input" type="text" v-model="searchKey" placeholder="搜索海洋书籍" @confirm="search" />
        <text class="search-icon">🔍</text>
      </view>
    </view>

    <swiper class="swiper" :indicator-dots="true" :autoplay="true" :interval="3000" :duration="1000" v-if="banners.length > 0">
      <swiper-item v-for="(item,index) in banners" :key="index" @click="handleBannerClick">
        <image :src="item.imgurl" mode="widthFix"></image>
      </swiper-item>
    </swiper>

    <view class="quick-entry">
      <view class="entry-item" v-for="item in quickEntries" :key="item.id" @click="handleEntry(item)">
        <view class="entry-icon">{{ item.icon }}</view>
        <text class="entry-name">{{ item.name }}</text>
      </view>
    </view>

    <view class="section-title">🐋 畅销书籍</view>

    <view class="docList" v-if="hotDocs.length > 0">
      <view class="item" v-for="item in hotDocs" :key="item.docId" @click="toDetail(item.docId)">
        <image :src="item.image || defaultImage" mode="widthFix"></image>
        <view class="docInfo">
          <view class="docTitle">{{ item.docTitle }}</view>
          <view class="docDesc">{{ item.docDesc }}</view>
          <view class="docMeta">
            <text class="category-tag">{{ item.category || '未分类' }}</text>
            <text class="price">¥{{ item.price ? item.price.toFixed(2) : '0.00' }}</text>
          </view>
        </view>
      </view>
    </view>
    <view class="empty-section" v-else>暂无数据</view>

    <view class="section-title">⭐ 精选推荐</view>

    <view class="featured-list" v-if="featuredDocs.length > 0">
      <view class="featured-item" v-for="item in featuredDocs" :key="item.docId" @click="toDetail(item.docId)">
        <image :src="item.image || defaultImage" mode="widthFix"></image>
        <view class="featured-info">
          <view class="featured-title">{{ item.docTitle }}</view>
          <view class="featured-desc">{{ item.docDesc }}</view>
          <view class="featured-meta">
            <text class="tag">精选</text>
            <text>{{ item.author || '未知作者' }}</text>
            <text class="meta-gap">阅读 {{ item.views || 0 }}</text>
          </view>
        </view>
      </view>
    </view>
    <view class="empty-section" v-else>暂无数据</view>

    <view class="section-title">🌊 最新上架</view>

    <view class="latest-list" v-if="latestDocs.length > 0">
      <view class="latest-item" v-for="item in latestDocs" :key="item.docId" @click="toDetail(item.docId)">
        <view class="latest-info">
          <view class="latest-title">{{ item.docTitle }}</view>
          <view class="latest-desc">{{ item.docDesc }}</view>
          <view class="latest-meta">
            <text>{{ item.author || '未知作者' }}</text>
            <text class="meta-gap">已售 {{ item.sales || 0 }}</text>
            <text>阅读 {{ item.views || 0 }}</text>
          </view>
        </view>
        <image :src="item.image || defaultImage" mode="widthFix"></image>
      </view>
    </view>
    <view class="empty-section" v-else>暂无数据</view>

    <view class="load-more" v-if="loading">加载中...</view>
  </view>
</template>

<script>
import { getBaseUrl } from '@/utils/api.js'
export default {
  data() {
    return {
      banners: [],
      loading: true,
      searchKey: "",
      defaultImage: 'https://picsum.photos/400/300?random=1',
      quickEntries: [
        { id: 1, icon: '🔍', name: '搜索', type: 'search' },
        { id: 2, icon: '📁', name: '分类', type: 'category' },
        { id: 3, icon: '❤️', name: '收藏', type: 'collection' },
        { id: 4, icon: '📋', name: '订单', type: 'order' },
        { id: 5, icon: '🛒', name: '购物车', type: 'cart' },
        { id: 6, icon: '👤', name: '我的', type: 'profile' }
      ],
      hotDocs: [],
      featuredDocs: [],
      latestDocs: []
    }
  },
  onLoad() {
    this.getHomeData()
  },
  onPullDownRefresh() {
    this.getHomeData()
  },
  methods: {
    // 一次请求获取全部数据，本地分片成三个板块
    getHomeData() {
      this.loading = true
      let done = 0
      const finish = () => {
        done++
        if (done >= 2) {
          this.loading = false
          uni.stopPullDownRefresh()
        }
      }
      this.getBanner(finish)
      this.getDocs(finish)
    },
    getBanner(callback) {
      uni.request({
        url: getBaseUrl() + "/banner/findBanners",
        method: "GET",
        success: res => {
          if (res.data && res.data.code === 200 && Array.isArray(res.data.data)) {
            this.banners = res.data.data
          }
          callback && callback()
        },
        fail: () => {
          callback && callback()
        }
      })
    },
    getDocs(callback) {
      uni.request({
        url: getBaseUrl() + "/doc/list",
        method: "GET",
        success: res => {
          if (res.data && res.data.code === 200 && Array.isArray(res.data.data) && res.data.data.length > 0) {
            const list = res.data.data
            // 畅销：按销量排序取前8
            this.hotDocs = [...list].sort((a, b) => (b.sales || 0) - (a.sales || 0)).slice(0, 8)
            // 精选：按浏览量排序取前4
            this.featuredDocs = [...list].sort((a, b) => (b.views || 0) - (a.views || 0)).slice(0, 4)
            // 最新：按ID倒序（ID越大越新）取前5
            this.latestDocs = [...list].sort((a, b) => b.docId - a.docId).slice(0, 5)
          }
          callback && callback()
        },
        fail: () => {
          callback && callback()
        }
      })
    },
    search() {
      if (!this.searchKey.trim()) {
        uni.showToast({ icon: "none", title: '请输入搜索关键词' })
        return
      }
      uni.navigateTo({ url: '/pages/search/search?keyword=' + encodeURIComponent(this.searchKey) })
    },
    // 检查登录态，未登录跳转登录页
    checkLogin() {
      const token = uni.getStorageSync('token')
      if (!token) {
        uni.showToast({ icon: 'none', title: '请先登录' })
        setTimeout(() => {
          uni.navigateTo({ url: '/pages/login/login' })
        }, 800)
        return false
      }
      return true
    },
    handleEntry(entry) {
      switch (entry.type) {
        case 'search':
          uni.navigateTo({ url: '/pages/search/search' })
          break
        case 'category':
          uni.switchTab({ url: '/pages/category/category' })
          break
        case 'collection':
          if (this.checkLogin()) {
            uni.navigateTo({ url: '/pages/profile/collection' })
          }
          break
        case 'order':
          if (this.checkLogin()) {
            uni.navigateTo({ url: '/pages/orderList/orderList' })
          }
          break
        case 'cart':
          uni.switchTab({ url: '/pages/cart/cart' })
          break
        case 'profile':
          uni.switchTab({ url: '/pages/profile/profile' })
          break
      }
    },
    // 点击轮播图跳转分类页
    handleBannerClick() {
      uni.switchTab({ url: '/pages/category/category' })
    },
    toSearch() {
      uni.navigateTo({ url: '/pages/search/search' })
    },
    toDetail(id) {
      uni.navigateTo({ url: '/pages/doc-detail/doc-detail?id=' + id })
    }
  }
}
</script>

<style>
.page-box {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(to bottom, #E0F4FF, #B8E6FF, #E0F4FF);
  padding-bottom: 120rpx;
}
.navBar {
  height: 500rpx;
  padding: 0 30rpx;
  padding-top: 100rpx;
  overflow: hidden;
  background: linear-gradient(to bottom, #0066CC, #00BFFF, #B8E6FF);
  position: relative;
}
.navBar::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60rpx;
  background: linear-gradient(to bottom, rgba(184, 230, 255, 0), #E0F4FF);
}
.navBar .left {
  flex: 1;
  color: white;
  font-size: 44rpx;
  font-weight: 800;
  text-shadow: 0 2rpx 4rpx rgba(0,0,0,0.2);
}
.navBar .right {
  position: relative;
  margin-top: 30rpx;
  flex: 2;
}
.navBar .search-input {
  padding: 16rpx 0rpx;
  padding-left: 60rpx;
  border: 2rpx solid rgba(255,255,255,0.6);
  border-radius: 50rpx;
  font-size: 28rpx;
  background-color: rgba(255,255,255,0.95);
  box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.1);
}
.navBar .search-icon {
  position: absolute;
  left: 20rpx;
  top: 22rpx;
  font-size: 30rpx;
}

.swiper {
  height: 550rpx;
  margin: 0 20rpx;
  margin-top: -320rpx;
  border-radius: 32rpx;
  overflow: hidden;
  box-shadow: 0 12rpx 40rpx rgba(0, 102, 204, 0.15);
  padding-bottom: 20rpx;
}
.swiper image {
  width: 100%;
  border-radius: 32rpx;
}

.quick-entry {
  display: flex;
  flex-wrap: wrap;
  background: rgba(255, 255, 255, 0.95);
  margin: -50rpx 20rpx 30rpx;
  padding: 30rpx;
  border-radius: 24rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 102, 204, 0.08);
  backdrop-filter: blur(10px);
}
.entry-item {
  width: 33.33%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24rpx 0;
}
.entry-icon {
  font-size: 56rpx;
  margin-bottom: 12rpx;
  transition: transform 0.2s;
}
.entry-item:active .entry-icon {
  transform: scale(1.2);
}
.entry-name {
  font-size: 26rpx;
  color: #333;
  font-weight: 500;
}

.section-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #0066CC;
  padding: 30rpx 20rpx;
  display: flex;
  align-items: center;
  gap: 10rpx;
}
.section-title::before {
  content: '';
  width: 8rpx;
  height: 36rpx;
  background: linear-gradient(to bottom, #00BFFF, #0066CC);
  border-radius: 4rpx;
}

.empty-section {
  text-align: center;
  padding: 60rpx;
  color: #999;
  font-size: 26rpx;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 16rpx;
  margin: 0 20rpx 20rpx;
}

.docList {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  margin: 0 20rpx;
  margin-bottom: 20rpx;
}
.docList .item {
  box-sizing: border-box;
  margin-top: 25rpx;
  width: 345rpx;
  height: 540rpx;
  border-radius: 24rpx;
  background-color: white;
  overflow: hidden;
  box-shadow: 0 8rpx 24rpx rgba(0, 102, 204, 0.08);
  transition: transform 0.2s, box-shadow 0.2s;
}
.docList .item:active {
  transform: scale(0.98);
  box-shadow: 0 4rpx 12rpx rgba(0, 102, 204, 0.12);
}
.docList image {
  width: 100%;
  height: 280rpx;
  border-radius: 24rpx 24rpx 0 0;
}
.docList .docInfo {
  padding: 24rpx;
}
.docList .docInfo .docTitle {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 12rpx;
}
.docList .docInfo .docDesc {
  font-size: 24rpx;
  color: #888;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  margin-bottom: 16rpx;
}
.docList .docInfo .docMeta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 22rpx;
  color: #666;
}
.docList .docInfo .docMeta .category-tag {
  background: linear-gradient(to right, #E0F4FF, #B8E6FF);
  color: #0066CC;
  padding: 6rpx 14rpx;
  border-radius: 8rpx;
  font-size: 20rpx;
}
.docList .docInfo .docMeta .price {
  color: #FF6B35;
  font-weight: bold;
  font-size: 28rpx;
}
.featured-list {
  margin: 0 20rpx;
  margin-bottom: 20rpx;
}
.featured-item {
  background: #fff;
  border-radius: 20rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}
.featured-item image {
  width: 100%;
  height: 300rpx;
}
.featured-info {
  padding: 20rpx;
}
.featured-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}
.featured-desc {
  font-size: 26rpx;
  color: #999;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  margin-bottom: 15rpx;
}
.featured-meta {
  display: flex;
  align-items: center;
  font-size: 24rpx;
  color: #666;
}
.featured-meta .tag {
  background: #1E90FF;
  color: #fff;
  padding: 4rpx 16rpx;
  border-radius: 20rpx;
  margin-right: 20rpx;
}
.meta-gap {
  margin: 0 16rpx;
}

.latest-list {
  margin: 0 20rpx;
  margin-bottom: 20rpx;
}
.latest-item {
  display: flex;
  background: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
  margin-bottom: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}
.latest-item image {
  width: 180rpx;
  height: 130rpx;
  border-radius: 12rpx;
  margin-left: 20rpx;
}
.latest-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.latest-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 8rpx;
}
.latest-desc {
  font-size: 24rpx;
  color: #999;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  margin-bottom: 10rpx;
}
.latest-meta {
  display: flex;
  align-items: center;
  font-size: 22rpx;
  color: #666;
}

.load-more {
  text-align: center;
  padding: 30rpx;
  color: #999;
  font-size: 26rpx;
}
</style>
