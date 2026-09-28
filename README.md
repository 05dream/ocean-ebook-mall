# 海洋书城（微信小程序 + Spring Boot 后端）

一个海洋主题的网上书城微信小程序，包含商品浏览、搜索、购物车、支付宝沙箱支付、订单管理（含批量取消、收货地址）、收藏、头像上传等功能。

## 项目结构

```
├── wiki1/     小程序前端（uni-app + Vue3 + Vite）
└── wikipro/   后端服务（Spring Boot 2.6 + MyBatis-Plus + MySQL + 支付宝沙箱）
```

## 运行前准备

1. **数据库**：安装 MySQL 8.x，创建数据库 `shopdb`，导入表结构（cart、collection、doc、order、order_item、wx_banner、wx_user）
2. **后端配置**：编辑 `wikipro/src/main/resources/application.properties`，填写：
   - 数据库密码
   - 支付宝沙箱 APPID、应用私钥、支付宝公钥（在支付宝开放平台沙箱环境申请）
3. **前端配置**：编辑 `wiki1/src/utils/api.js`，把 `getBaseUrl()` 里的 IP 改成你电脑的局域网 IP

## 启动

```bash
# 启动后端（在 wikipro 目录）
mvn spring-boot:run

# 启动前端编译（在 wiki1 目录）
npm install
npm run dev:mp-weixin
```

然后用微信开发者工具导入 `wiki1/dist/dev/mp-weixin` 目录运行。

## 注意事项

- 支付宝沙箱支付需要一个公网回调地址，可用 natapp 等内网穿透工具，拿到公网域名后替换 `application.properties` 中的 notify-url / return-url，以及前端 `payForm.vue` 中的域名
- 密钥、密码等敏感信息请勿提交到公开仓库
