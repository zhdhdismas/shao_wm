<template>
  <div>
    <ul
      class="infinite-list"
      v-infinite-scroll="load"
      infinite-scroll-immediate="false"
      infinite-scroll-distance="1"
      style="overflow: auto"
    >
      <div
        v-for="(item, index) in arr"
        :key="index"
        style="height: 100px; margin-top: 5px"
        class="infinite-list-item"
      >
        <span>{{ item.name }}</span>
        <br />
        <div v-show="state != 2">
            <span>{{
          "距离我:" + (item.distance / 1000).toFixed(2) + " km"
        }}</span>&emsp;<span>运费为</span><span style="color:red">{{(item.distance / 1000).toFixed(0)*2}}</span><span>元</span>
        &emsp;<el-button type="success" size="mini" @click="transExp(item)">选择</el-button>
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
      this.load();
  },

  data() {
    return {
      arr: [],
      msg: [1],
      start: 1,
      count: 3,
    };
  },

  methods: {
    transExp(item){
       var dis=(item.distance / 1000).toFixed(2);
       var exp=(item.distance / 1000).toFixed(0)*2;
       var c={name:item.name,distance:dis,exp:exp,gid:item.gid};
       this.$emit("transExp",c);
    },
    load() {
      var _this = this;
      if (this.msg == null || this.msg.length == 0) return false;
      var url = "";
      if (this.state == 2) {
        url =
          "/geo/aroundShop?state=" +
          this.state +
          "&count=" +
          this.count +
          "&start=" +
          this.start;
      } else {
        url =
          "/geo/aroundShop?x=" +
          parseFloat(this.x) +
          "&y=" +
          parseFloat(this.y) +
          "&start=" +
          this.start +
          "&count=" +
          this.count +
          "&state=" +
          this.state;
      }

      getRequest(url).then((resp) => {
    
        _this.msg = resp.data.list;
        _this.start += 1;
        for (var i = 0; i < _this.msg.length; i++) {
          this.arr.push(_this.msg[i]);
        }
      });
    },
  },
  props: ["x", "y", "state"],
};
</script>
    
    <style scoped>
.infinite-list {
  height: 300px;
}
</style>
     