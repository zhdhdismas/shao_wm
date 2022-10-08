import Vue from 'vue'
import Router from 'vue-router'
import LoginList from '@/components/LoginList'
import Home from '@/components/Home'
import MenuList2 from '@/components/MenuList2'
import MyOrder from '@/components/MyOrder'
import MenuDetail from '@/components/MenuDetail'
import Register from '@/components/Register'
import Message from '@/components/Message'
import ReceiveList from '@/components/ReceiveList'
import MenuMangeList from '@/components/MenuMangeList'
import MyCurOrder from '@/components/MyCurOrder'
import OrdersManage from '@/components/OrdersManage'
import MoneyManage from '@/components/MoneyManage'
import CateMana from '@/components/CateMana'
import Voucher from '@/components/Voucher'
import PostBlog from '@/components/PostBlog'
import BlogList from '@/components/BlogList'
import BlogDetail from '@/components/BlogDetail'
import DateSign from '@/components/DateSign'
import Example from '@/components/Example'
import Map from '@/components/Map'

Vue.use(Router)

export default new Router({
  routes: [{
    path: '/ms',
    name: '登录',
    hidden: true,
    component: Message
  },
  {
    path: '/',
    name: '登录',
    hidden: true,
    component: LoginList
  }, {
    path: '/re',
    component: Register,
    name: '注册',
    hidden: true
  }, {
    path: '/home',
    name: '首页',
    component: Home,
    hidden: true
  },

  {
    path: '/home',
    component: Home,
    name: '点菜服务',

    children: [{
      
      path: '/MenuList',
      name: '点菜大厅',
      keepAlive: true,
      component: MenuList2,
      meta: {
        keepAlive: false
      }
    }, {
      path: '/MenuDetail',
      name: '菜品详情',
      component: MenuDetail,
      hidden: true,

      meta: {
        keepAlive: false
      }
    }, {
      path: '/Voucher',
      name: '优惠券抢购',
      component: Voucher,
      meta: {
        keepAlive: false
      }
    }]
  },

  {
    path: '/Home',
    component: Home,
    children: [{
      path: '/MyCurOrder',
      name: '我的订单(进行中)',
      component: MyCurOrder
    }]
  },
  {
    path: '/Home',
    component: Home,
    children: [{
      path: '/MyOrder',
      name: '已支付订单',
      component: MyOrder
    }]
  },

  {
    path: '/home',
    component: Home,
    children: [{
      path: '/jiedan',
      name: '骑手接单',
      component: ReceiveList
    }]
  },
  {
    path: '/Home',
    component: Home,
    children: [{
      path: '/MenuMangeList',
      name: '菜品管理',
      keepAlive: false,
      component: MenuMangeList
    }]
  },
  {
    path: '/Home',
    component: Home,
    children: [{
      path: '/OrdersManage',
      name: '订单管理',
      component: OrdersManage
    }]
  },
  {
    path: '/Home',
    component: Home,
    children: [{
      path: '/MoneyManage',
      name: '财务管理',
      component: MoneyManage
    }]
  },
  {
    path: '/Home',
    component: Home,
    children: [{
      path: '/CateMana',
      name: '分类管理',
      component: CateMana
    }]
  },
  {
    path: '/Home',
    component: Home,
    children: [{
      path: '/PostBlog',
      name: '发表博客',
      component: PostBlog
    }]
  },
  {
    path: '/Home',
    component: Home,
    children: [{
      path: '/BlogList',
      name: '博客大厅',
      keepAlive: true,
      component: BlogList
    }]
  }, {
    path: '/BlogDetail',
    name: 'BlogDetail',
    hidden: true,
    component: BlogDetail
  },
  {
    path: '/Home',
    component: Home,
    children: [{
      path: '/DateSign',
      name: '签到日历',
      keepAlive: true,
      component: DateSign
    }]
  },
  {

    path: '/Example',
    name: 'Example',
    keepAlive: true,
    hidden: true,
    keepAlive: true,
    component: Example

  },
 
  {
    path: '/Map',
    component: Map,
    children: [{
      path: '/Map',
      name: '门店管理',
      keepAlive: true,
      component: Map
    }]
  } 

  ]
})