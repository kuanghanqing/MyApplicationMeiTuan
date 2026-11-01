# MyApplicationMeiTuan

仿美团外卖 Android 课程设计 App（基于 Java + AndroidX）。

## 功能

- 店铺列表：搜索（中文/拼音全拼/拼音首字母）、分类筛选、活动 Banner
- 店铺点餐：7 家店铺、35 道菜品，每店菜单独立不重复
- 购物车：底部金额栏、数量加减、起送价判断、明细弹窗
- 结算支付：默认地址联动、手机号脱敏、三种支付方式、模拟支付成功页
- 个人中心：编辑资料（昵称/手机号/性别）、收货地址增删改、设默认地址、退出登录

## 技术栈

- 语言：Java
- 最低 SDK：API 24，目标 SDK：API 34
- UI：XML 布局 + RecyclerView
- 图片加载：Glide
- 本地存储：SharedPreferences + JSON 序列化

## 项目结构

```
app/src/main/java/com/example/myapplicationmeituan/
├── bean/          # Shop / Dish / CartItem / Address 数据模型
├── storage/       # ProfileStorage（SharedPreferences 封装）
├── ui/shop/      # ShopListActivity / ShopActivity / CheckoutActivity
└── ui/profile/   # ProfileActivity / EditProfileActivity / AddressListActivity
```

## 运行

Android Studio 打开本项目，等待 Gradle 同步完成后运行到模拟器或真机。
