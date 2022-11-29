import Vue from 'vue'
import Vuex from 'vuex'
import {getRequest,postRequest2} from "../utils/api";
import Stomp from 'stompjs'
import SockJS from 'sockjs-client'
 
Vue.use(Vuex)

const now = new Date();

const store = new Vuex.Store({
  state: {
    routes: [],
    sessions: {},
    currentAdmin: JSON.parse(window.sessionStorage.getItem('user')),
    admins: [],
    currentSession: null,
    filterKey: '',
    stomp: null
  },
  mutations: {
    INIT_CURRENTAdmin(state, admin) {
      state.currentAdmin = admin;
    },
    initRoutes(state, data) {
      state.routes = data;
    },
    changeCurrentSessionId(state, currentSession) {
      state.currentSession = currentSession;
    },
    addMessage(state, msg) {
      let mss = state.sessions[state.currentAdmin.username + '#' + msg.to];
      if (!mss) {
        Vue.set(state.sessions, state.currentAdmin.username + '#' + msg.to, []);
      }
      state.sessions[state.currentAdmin.username + '#' + msg.to].push({
        content: msg.content,
        date: new Date(),
        self: !msg.notSelf
      });
    },
    INIT_DATA(state) {
    //   浏览器本地的历史聊天记录可以在这里完成
      let data = window.localStorage.getItem('vue-chat-session');
      if (data) {
        state.sessions = JSON.parse(data);
      }
    },
    INIT_ADMIN(state, data) {
      state.admins = data;
    }
  },
  actions: {
    connect(context) {
      context.state.stomp = Stomp.over(new SockJS('/ws/ep'));
      let token = window.localStorage.getItem("token");
      let authToken='Bearer '+token;
      context.state.stomp.connect({'Auth-Token': authToken}, success => {
        context.state.stomp.subscribe('/user/queue/chat', msg => {
            let receiveMsg = JSON.parse(msg.body);
            receiveMsg.notSelf = true;
            console.log(receiveMsg.to+":"+receiveMsg.from);
            receiveMsg.to = receiveMsg.from;
            console.log(receiveMsg.to+":"+receiveMsg.from);
            context.commit('addMessage', receiveMsg);
            
        })
      }, error => {
      })
    },
    initData(context) {
        context.commit('INIT_DATA');
        let url='/chat/all?username=';
        url+=this.state.filterKey;
      getRequest(url).then(resp=>{
        if (resp){
          context.commit('INIT_ADMIN', resp.data);
        }
      })
    }
  }
})

store.watch(function (state) {
  return state.sessions
}, function (val) {
    // console.log('CHANGE: ',val);
    // console.log('CHANGE: ', JSON.stringify(val));
    window.localStorage.setItem('vue-chat-session', JSON.stringify(val));
    //监控太慢了只能进行实时前端操作数据，如果发送请求会丢失监听到的数据
        
}, {
  deep: true/*这个貌似是开启watch监测的判断,官方说明也比较模糊*/
})
 
export default store;
/*
state ：全局state对象,用于保存所有组件的公共数据
getters ：监听state值的最新状态（计算属性）
mutations ：唯一可以改变state值的方法(同步执行)
actions ：异步执行mutations方法
* */