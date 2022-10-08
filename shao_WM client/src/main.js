import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import '@/styles/index.css'
import 'font-awesome/css/font-awesome.min.css'
import './utils/filter_utils.js'
import * as echarts from 'echarts'
Vue.prototype.$echarts = echarts //挂载在原型，组件内使用直接this.$echarts调用 
 
 
 
Vue.use(ElementUI)
Vue.config.productionTip = false


window.$bus = new Vue();

new Vue({
  el: '#app',
  router,
  template: '<App/>',
  components: {App}
})


