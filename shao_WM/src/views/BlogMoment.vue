<template>
  <div>
    <div style="text-align: left">
      <el-button
        type="text"
        icon="el-icon-refresh-left"
        @click="refresh()"
        style="margin-left: 10px"
        >刷新</el-button
      >
    </div>

    <ul
      class="infinite-list"
      v-infinite-scroll="load"
      infinite-scroll-immediate="true"
      infinite-scroll-distance="1"
      style="overflow: auto"
    >
      <div
        v-for="(item, index) in arr"
        :key="index"
        style="height: 250px; margin-top: 5px"
        class="infinite-list-item"
      >
        <div style="text-align: center">
          <span style="color: blue">{{ item.blogTitle }}</span>
        </div>
        <br />

        <div>
          <span>{{ item.mdContent.substring(0, 450) }}</span>
          <span
            style="color: blueviolet; text-decoration: underline"
            @click="itemClick(item)"
            >点击查看详情</span
          >
        </div>
        <br />
        <div>
          <el-row style="text-align: right">
            <img
              :src="item.user.avatar"
              style="width: 30px; height: 30px"
              @click="toMsg(item.user)"
            />
            <span
              style="margin-left: 5px; color: brown"
              @click="toMsg(item.user)"
              >{{ item.user.username }}</span
            >
            <span style="margin-left: 10px">电话:{{ item.user.phone }}</span>
            <span style="margin-left: 10px">邮箱:{{ item.user.email }}</span>
          </el-row>
        </div>

        <el-divider></el-divider>
      </div>
    </ul>
  </div>
</template>
  
  <script>
import { getRequest } from "../utils/api";
export default {
  mounted() {
    this.lastId = new Date().getTime();
    this.offset = 0;
    this.load();
  },

  data() {
    return {
      arr: [],
      msg: [],
      count: 0,
      offset: 0,
      lastId: 0,
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
    itemClick(row) {
      this.$router.push({
        path: "/BlogDetail",
        query: {
          blog: row,
          bid: row.bid,
          username: row.user.username,
          phone: row.user.phone,
          email: row.user.email,
          avatar: row.user.avatar,
        },
      });
    },
    refresh() {
      this.offset = 0;
      this.lastId = new Date().getTime();
      this.load(); //不可以直接给this.arr赋值为空,会导致方法中的方法不能获取已改变值的arr
      this.$message({ type: "success", message: "已刷新" });
    },
    load() {
      var arr=[];
      if (this.offset != 0) {
        arr = this.arr;
      }
      var _this = this;
      if (this.lastId == undefined && this.offset == undefined) return false;
      var url =
        "/blog/of/follow?offset=" + this.offset + "&lastId=" + this.lastId;
      getRequest(url).then((resp) => {
        _this.msg = resp.data.list;
        _this.lastId = resp.data.minTime;
        _this.offset = resp.data.offset;
        for (var i = 0; i < _this.msg.length; i++) {
          arr.push(_this.msg[i]);
        }
        this.arr = arr;
      });
    },
  },
  props: ["uid"],
};
</script>
  
  <style scoped>
.infinite-list {
  height: 300px;
}
</style>
   