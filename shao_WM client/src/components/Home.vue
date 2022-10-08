<template>
  <el-container class="home_container">
   
    <el-aside width="200px">
        <el-menu
          default-active="0"
          class="el-menu-vertical-demo"
          style="background-color: ghostwhite"
          router
        >
          <template
            v-for="(item, index) in this.$router.options.routes"
            v-if="!item.hidden"
          >
            <!-- 走到这，你刚刚那边定义的没有children,所以这边就会报错 -->
            <el-submenu
              :index="index + ''"
              v-if="item.children.length > 1"
              :key="index"
            >
              <template slot="title">
                <i :class="item.iconCls"></i>
                <span>{{ item.name }}</span>
              </template>
              <el-menu-item
                v-for="child in item.children"
                v-if="!child.hidden"
                :index="child.path"
                :key="child.path"
              >
                {{ child.name }}
              </el-menu-item>
            </el-submenu>
            <template v-else>
              <el-menu-item :index="item.children[0].path">
                <i :class="item.children[0].iconCls"></i>
                <span slot="title">{{ item.children[0].name }}</span>
              </el-menu-item>
            </template>
          </template>
        </el-menu>
      </el-aside>
    <el-container>
        <el-header style="height:80px">
      <el-dialog :visible.sync="yulan">
        <viewer>
          <img width="100%" :src="this.imgAvatar" />
        </viewer>
      </el-dialog>

      <div class="home_title">外卖系统</div>
      <div class="home_userinfoContainer">
        <div style="float: left"></div>
        &emsp;<i style="padding-right: 40px;color:aliceblue">
          {{ nowTime }}
        </i>
        <el-dropdown @command="handleCommand">
          <span style="color: white">
            <i style="padding-right: 20px">
              {{ currentUserName }}
            </i>
            <img
              :src="this.imgAvatar"
              width="40"
              height="40"
              @click="yulan = true"
            />
            <i class="el-icon-arrow-down el-icon--right home_userinfo"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <router-link to="/MenuList">
              <el-dropdown-item command="diancai" style="text-decoration: none"
                >点菜大厅</el-dropdown-item
              >
            </router-link>
            <router-link to="/ms">
              <el-dropdown-item>个人信息</el-dropdown-item>
            </router-link>
            <div @click="toMsg()">
              <el-dropdown-item>个人主页</el-dropdown-item>
            </div>

            <el-dropdown-item command="logout" divided
              >退出登录</el-dropdown-item
            >
            <el-dropdown-item command="getTouxiang" divided
              >上传头像</el-dropdown-item
            >
          </el-dropdown-menu>
        </el-dropdown>
      </div>
      <el-dialog title="上传头像" :visible.sync="dialogVisible">
        <avatar_cropper
          @closeAvatarDialog="closeAvatarDialog"
          @avatar="getAvatar"
        ></avatar_cropper>

        <el-button style="margin-left: 150px" @click="dialogVisible = false"
          >取消</el-button
        >&emsp; &emsp;
        <el-button type="success" @click="submitAvatar">确认上传</el-button>
      </el-dialog>
    </el-header>
      <el-container>
        <el-main>
          <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="{ path: '/home' }"
              >首页</el-breadcrumb-item
            >
            <el-breadcrumb-item
              v-text="this.$router.currentRoute.name"
            ></el-breadcrumb-item>
          </el-breadcrumb>
          <el-carousel
            :interval="4000"
            type="card"
            height="400px"
            v-show="this.$router.currentRoute.name == '首页'"
          >
            <el-carousel-item v-for="item in imgUrls" :key="item">
              <img :src="item" />
            </el-carousel-item>
          </el-carousel>
          <keep-alive>
            <router-view v-if="this.$route.meta.keepAlive"></router-view>
          </keep-alive>
          <router-view v-if="!this.$route.meta.keepAlive"></router-view>
        </el-main>
      </el-container>
    </el-container>
  </el-container>
</template>

 
<script>
import { getRequest, putRequest } from "../utils/api";
import Viewer from "v-viewer";

import AvatarCropper from "@/components/AvatarCropper";
export default {
  components: {
    avatar_cropper: AvatarCropper,
  },
  methods: {
    toMsg() {
      this.$router.push({
        path: "/Example",
        query: {
          username: this.msg.username,
          phone: this.msg.phone,
          avatar: this.msg.avatar,
          email: this.msg.email,
          uid: this.msg.uid,
        },
      });
    },
    getAvatar(data) {
      this.avatar = data;
    },
    submitAvatar() {
      putRequest("/addAvatar", { avatar: this.avatar }).then((resp) => {
       
        this.imgAvatar = resp.data;
        this.dialogVisible = false;
      });
    },
    closeAvatarDialog(data) {
      console.log(data);
      this.dialogVisible = false;
    },
    timeFormate(timeStamp) {
      let year = new Date(timeStamp).getFullYear();
      let month =
        new Date(timeStamp).getMonth() + 1 < 10
          ? "0" + (new Date(timeStamp).getMonth() + 1)
          : new Date(timeStamp).getMonth() + 1;
      let date =
        new Date(timeStamp).getDate() < 10
          ? "0" + new Date(timeStamp).getDate()
          : new Date(timeStamp).getDate();
      let hh =
        new Date(timeStamp).getHours() < 10
          ? "0" + new Date(timeStamp).getHours()
          : new Date(timeStamp).getHours();
      let mm =
        new Date(timeStamp).getMinutes() < 10
          ? "0" + new Date(timeStamp).getMinutes()
          : new Date(timeStamp).getMinutes();
      let ss =
        new Date(timeStamp).getSeconds() < 10
          ? "0" + new Date(timeStamp).getSeconds()
          : new Date(timeStamp).getSeconds();
      let week = new Date(timeStamp).getDay();
      let weeks = ["日", "一", "二", "三", "四", "五", "六"];
      let getWeek = "星期" + weeks[week];
      this.nowTime =
        year +
        "年" +
        month +
        "月" +
        date +
        "日" +
        " " +
        hh +
        ":" +
        mm +
        ":" +
        ss +
        getWeek;
    }, // 实时刷新当前时间，格式化
    nowTimes() {
      this.timeFormate(new Date());
      setInterval(this.nowTimes, 1000);
      this.clear();
    },
    clear() {
      clearInterval(this.nowTimes);
      this.nowTimes = null;
    },
    beforeDestroy() {
      if (this.timer) {
        clearInterval(this.timer); // 在Vue实例销毁前，清除我们的定时器
      }
    },
    handleCommand(command) {
      var _this = this;
      if (command == "getTouxiang") {
        this.dialogVisible = true;
      }
      if (command == "logout") {
        this.$confirm("注销登录吗?", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }).then(
          function () {
            getRequest("/logout");
            _this.currentUserName = "游客";
            _this.$router.replace({ path: "/" });
          },
          function () {
            //取消
          }
        );
      }
    },
  },
  mounted: function () {
    var _this = this;
    getRequest("/img/homepageImg").then((res) => {
         
      _this.imgUrls = res.data;
    });
    getRequest("/myAvatar").then((resp) => {
      _this.imgAvatar = resp.data;
    });

    var _this = this;
    this.nowTimes();

    getRequest("/currentUserName")
      .then((resp) => {
        if (resp.data.status == "success") {
          _this.currentUserName = resp.data.msg;
        } else {
          _this.$message({ type: "error", message: "尚未登录,请先登录!" });
          _this.$router.replace({ path: "/" });
        }
      })
      
    getRequest("/currentUserMsg").then((resp) => {
      this.msg = resp.data;
    });
  },
  beforeDestroy() {
    this.clear();
  },
  data() {
    return {
      msg: {},
      yulan: false,
      imgAvatar: "",
      avatar: "",
      dialogVisible: false,
      imgUrls: [],
      currentUserName: "",
      nowTime: "",
    };
  },
};
</script>
<style>
a {
  text-decoration: none;
}
li {
  text-align: center;
}
.home_container {
  height: 100%;
  position: absolute;
  top: 0px;
  left: 0px;
  width: 100%;
}

.el-header {
  background-color: rgb(163, 114, 114);
  color: #333;
  text-align: center;
  display: flex;
  height: 20px;
  align-items: center;
  justify-content: space-between;
}

.el-aside {
  background-color: rgb(248, 241, 241);
}

.el-main {
  background-color: #ffffff;
  color: #000;
  text-align: center;
}

.home_title {
  color: #fff;
  font-size: 22px;
  display: inline;
}

.home_userinfo {
  color: #fff;
  cursor: pointer;
}

.home_userinfoContainer {
  display: inline;
  margin-right: 20px;
}

/* 走马灯 */
.el-carousel__item h3 {
  color: #475669;
  font-size: 14px;
  opacity: 0.75;
  line-height: 400px;
  margin: 0;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #d3dce6;
}
</style>
 
