import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import '@/styles/index.css'
import 'font-awesome/css/font-awesome.min.css'
import * as echarts from 'echarts'
import store from './store'

Vue.prototype.$echarts = echarts //挂载在原型，组件内使用直接this.$echarts调用 

 
Vue.use(ElementUI)
Vue.config.productionTip = false


window.$bus = new Vue();


new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
