<template>
  <div>
    <div style="text-align: center">
      {{ myuid == uid ? "我的主页" : "TA的主页" }}
    </div>
    <el-col :span="24">
      <div style="text-align: right">
        <el-button type="success" icon="el-icon-back" @click="goBack()"
          >返回</el-button
        >
      </div>
      <br />
    </el-col>
    <br />
    <el-header class="cate_mana_header">
      <img
        :src="userMsg.avatar"
        style="width: 50px; height: 50px; margin-left: 20px"
      />
      <span style="margin-left: 5px"> {{ userMsg.username }}</span>
      <span style="margin-left: 15px">电话:{{ userMsg.phone }}</span>
      <span style="margin-left: 15px">邮箱:{{ userMsg.email }}</span>
      <el-button
        type="primary"
        size="medium"
        style="margin-left: 10px"
        @click="addNewFriend()"
        >{{ isFollow ? "已关注" : "关注" }}</el-button
      >
    </el-header>
    <el-container class="menu_list">
      <el-main class="main">
        <el-tabs type="border-card">
          <el-tab-pane label="我的关注" v-if="myuid == uid">
            <follow_table
              :uid="uid"
              :key="new Date().getTime()"
              state="0"
            ></follow_table>
          </el-tab-pane>
          <el-tab-pane label="TA的关注" v-if="myuid != uid">
            <follow_table
              :uid="uid"
              :key="new Date().getTime()"
              state="0"
            ></follow_table>
          </el-tab-pane>
          <el-tab-pane label="共同关注" v-if="myuid != uid">
            <follow_table
              :uid="userMsg.uid"
              :key="new Date().getTime()"
              state="1"
              v-if="myuid != uid"
            ></follow_table>
          </el-tab-pane>
          <el-tab-pane label="博客朋友圈" v-if="myuid == uid">
            <moment_table :key="new Date().getTime()"></moment_table>
          </el-tab-pane>
        </el-tabs>
      </el-main>
    </el-container>
  </div>
</template>
<script>
import UserFollow from "@/components/UserFollow";
import { getRequest, putRequest } from "../utils/api";
import BlogMoment from "./BlogMoment.vue";
export default {
  mounted: function () {
    this.userMsg.username = this.$route.query.username;
    this.userMsg.uid = this.$route.query.uid;
    this.userMsg.email = this.$route.query.email;
    this.userMsg.avatar = this.$route.query.avatar;
    this.userMsg.phone = this.$route.query.phone;
    this.uid = this.userMsg.uid;

    getRequest("/or/not/" + this.userMsg.uid).then((resp) => {
       
      this.isFollow = resp.data;
    });
    getRequest("/currentUserId").then((resp) => {
      this.myuid = resp.data;
    });
  },
  watch: {
    $route() {
      this.$router.go(0);
    },
  },
  methods: {
    goBack() {
      this.$router.go(-1);
    },
    addNewFriend() {
      putRequest("/" + this.userMsg.uid + "/" + this.isFollow).then((resp) => {
        this.isFollow = !this.isFollow;
        this.$message({ type: resp.data.status, message: resp.data.msg });
      });
    },
  },
  data() {
    return {
      flag: -1,
      myuid: 0,
      uid: 0,
      isFollow: false,
      isAdmin: false,
      userMsg: {
        uid: 0,
        username: "",
        email: "",
        phone: "",
        avatar: "",
      },
    };
  },

  components: {
    follow_table: UserFollow,
    moment_table: BlogMoment,
  },
};
</script>
<style scoped>
.cate_mana_header {
  background-color: #ececec;
  margin-top: 20px;
  padding-left: 5px;
  display: flex;
  justify-content: flex-start;
}
</style>
