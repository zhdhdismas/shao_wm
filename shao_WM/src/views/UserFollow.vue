<template>
  <div>
    <div
      v-for="(item, index) in msg"
      :key="index"
      style="width: 100%; height: 70px; margin-top: 20px; padding-left: 5px"
      class="infinite-list-item"
    >
      <span>
        <img :src="item.avatar" style="width: 50px; height: 50px" @click="toMsg(item)"/>
      </span>

      <span style="margin-left: 5px" @click="toMsg(item)">{{ item.username }}</span>
      <span style="margin-left: 5px">电话:{{ item.phone }}</span>
      <span style="margin-left: 5px">邮箱:{{ item.email }}</span>
    </div>
  </div>
</template>

<script>
import { getRequest } from "../utils/api";
export default {
  mounted() {
    var _this = this;
    if(_this.uid==0) return false;
    if (this.state == 0) {
      getRequest("/follow/" + this.uid).then((resp) => {
       
        _this.msg = resp.data;
      });
    } else {
      getRequest("/common/" + this.uid).then((resp) => {
      
        _this.msg = resp.data;
      });
    }
  },

  data() {
    return {
      msg: {},
    };
  },
  methods: {
    toMsg(msg) {
      this.$router.push({
        path: "/Example",
        query: {
          username: msg.username,
          phone: msg.phone,
          avatar: msg.avatar,
          email: msg.email,
          uid: msg.uid,
        },
      });
    },
  },
  props: ["uid", "state"],
};
</script>

<style scoped>
.infinite-list {
  height: 300px;
}
</style>
 