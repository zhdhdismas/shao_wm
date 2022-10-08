# shao_wm
一个后台外卖项目
### 页面
![image](https://github.com/zhdhdismas/README_IMG/blob/main/Snipaste_2022-10-08_14-01-53.png)
![image](https://github.com/zhdhdismas/README_IMG/blob/main/Snipaste_2022-10-08_14-23-45.png)
![image](https://github.com/zhdhdismas/README_IMG/blob/main/Snipaste_2022-10-08_14-28-27.png)
![image](https://github.com/zhdhdismas/README_IMG/blob/main/Snipaste_2022-10-08_14-27-02.png)
![image](https://github.com/zhdhdismas/README_IMG/blob/main/Snipaste_2022-10-08_14-29-16.png)
![image](https://github.com/zhdhdismas/README_IMG/blob/main/charts.png)
### 功能
主要功能:
1. 客户点菜，付款
2.骑手接单 
3.商户查看订单信息，状态，接单人信息，客户所点菜品等信息

其他功能:

秒杀抢购优惠券, 发表博客，个人关注，个人朋友圈，地图添加商户，签到，日历记事本
### 技术栈
springboot,vue 2.0,element-ui,echarts,springsecurity,jwt,mybatis-plus,redis,mysql
### 集成第三方技术
支付宝沙箱支付，高德地图API，oss

### 开发环境
jdk: 17

node: 8.16.0

redis: 6.2.6

mysql: 8.26

IDE: IDEA 2021.1,VScode

### 项目启动
git clone https://github.com/zhdhdismas/shao_wm.git 或者直接下载
后端在主目录下找到ShaoWmApplication类来启动

前端 下载配置node.js 通过命令 npm install -> npm run build -> npm run start/npm run dev进行启动

注意: 需要创建redis stream (redis的消息队列) 通过命令 xgroup create sss ger 0 MKSTREAM 否则会无法启动一直报错

ger为组名,sss为队列名,详情见VoucherOrderServiceImpl


