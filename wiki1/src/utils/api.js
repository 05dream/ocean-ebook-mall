/**
 * 全局 API 工具：统一管理后端地址和请求
 * 以后改后端 IP 只改这里一处
 */

// 后端基础地址（小程序端用局域网 IP，H5 端用 localhost）
// 使用前请把下面的 IP 改成你电脑的局域网 IP（cmd 运行 ipconfig 查看 IPv4 地址）
export function getBaseUrl() {
  const platform = uni.getSystemInfoSync().platform
  if (platform === 'h5') {
    return 'http://localhost:8080'
  }
  return 'http://192.168.1.100:8080'
}

/**
 * 统一请求封装
 * @param {Object} options - { url, method, data, header }
 * @returns {Promise}
 */
export function request(options) {
  const token = uni.getStorageSync('token')
  return new Promise((resolve, reject) => {
    uni.request({
      url: getBaseUrl() + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: Object.assign(
        { 'content-type': 'application/json' },
        token ? { Authorization: token } : {},
        options.header || {}
      ),
      success: res => resolve(res),
      fail: err => reject(err)
    })
  })
}
