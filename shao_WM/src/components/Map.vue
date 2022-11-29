<template>
  <div>
    <div style="text-align: left; margin-left: 20px">
      <el-button type="success" @click="goBack">返回</el-button>
    </div>
    <br />
    <br />

    <div style="margin-left: 20px">
      <el-input
        v-model="keywords"
        placeholder="输入具体地址添加门店"
        style="width: 600px"
      ></el-input>
      <el-button type="primary" @click="getMsg()">搜索</el-button>
      <el-button type="primary" @click="getAll()">获取所有门店</el-button>
    </div>

    <br />
    <br />
    <span style="margin-left: 20px">请选择地图缩放大小:</span>
    <el-select
      v-model="level"
      placeholder="请选择地图缩放大小"
      @change="setlevel"
    >
      <el-option v-for="(i, index) in 15" :key="index" :label="i" :value="i">
      </el-option>
    </el-select>
    <el-dialog title="添加门店" :visible.sync="dialogFormVisible">
      <el-select v-model="value" placeholder="请选择最近的地理位置">
        <el-option
          v-for="(item, index) in msg"
          :key="index"
          :value="item.district + item.name"
          >{{ item.district + item.name }}</el-option
        >
      </el-select>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="addOne()">确 定</el-button>
      </div>
    </el-dialog>
    <!-- Form -->
    <el-dialog title="所有门店" :visible.sync="dialogFormVisible2">
      <div v-for="(item, index) in shops" :key="index + 1">
        {{ index + 1 + ". " + item.name + "门店" }}&emsp;&emsp;<el-button
          size="mini"
          type="danger"
          @click="handleDelete(item)"
          >删除
        </el-button>
        <el-divider></el-divider>
      </div>
    </el-dialog>

    <div>
      <div id="container"></div>
    </div>
  </div>
</template>
   
  <script>
import AMapLoader from "@amap/amap-jsapi-loader";
import { deleteRequest, getRequest, postRequest } from "../utils/api";

export default {
  name: "gaode",
  data() {
    return {
      level: 10,
      shops: [],
      map: null, //初始化 map 对象
      autoComplete: null,
      keywords: "",
      msg: [],
      state1: "",
      dialogFormVisible: false,
      dialogFormVisible2: false,
      value: "",
      a: 120.073221,
      b: 30.312392,
    };
  },
  mounted() {
    //DOM初始化完成进行地图初始化
    var _this = this;
    this.initMap(_this.a, _this.b, _this.level);
  },
  methods: {
    setlevel() {
      var _this = this;
      this.initMap(_this.a, _this.b, _this.level);
    },
    handleDelete(item) {
      var _this = this;
      console.log(item.longitude + "," + item.latitude);
      deleteRequest("/geo/deleteOne/" + item.gid).then((resp) => {
        this.$message({ type: resp.data.status, message: resp.data.msg });
        this.initMap(_this.a, _this.b, _this.level);
        this.dialogFormVisible2 = false;
      });
    },
    goBack() {
      this.$router.go(-1);
    },
    getMsg() {
      this.dialogFormVisible = true;
      getRequest("/map/getSearch/" + this.keywords).then((resp) => {
        this.msg = resp.data.tips;
      });
    },
    getAll() {
      this.dialogFormVisible2 = true;
    },
    addOne() {
      if (this.value == null) {
        this.$message({ type: "error", message: "请选择附近门店" });
      }
      console.log(this.value);
      var i = 0;
      for (; i < this.msg.length; i++) {
        if (this.value == this.msg[i].district + this.msg[i].name) {
          break;
        }
      }
      var cur = this.msg[i].location;
      var _this = this;
      if (cur == "undefined" || cur == null) {
        this.$message({ type: "error", message: "请输入更详细一些的地址" });
        return false;
      }
      console.log(cur);
      var index = cur.indexOf(",");
      var a = cur.substring(0, index);
      var b = cur.substring(index + 1);
      postRequest("/geo/addOne", {
        longitude: a,
        latitude: b,
        name: this.msg[i].district + this.msg[i].name,
      }).then((resp) => {
        this.$message({ type: resp.data.status, message: resp.data.msg });
      });
      this.addPoint(a, b);
      this.initMap(a, b, _this.level);
      this.dialogFormVisible = false;
      this.value = "";
    },
    addPoint(a, b) {
      // 构造点标记
      var marker = new AMap.Marker({
        icon: "https://webapi.amap.com/theme/v1.3/markers/n/mark_b.png",
        position: [a, b],
      });

      this.map.add(marker);
    },
    initMap(a, b, level) {
      AMapLoader.load({
        key: "6192f4511a76dd1436cfd9644a26f3e1", //此处填入我们注册账号后获取的Key
        version: "2.0", //指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
        plugins: ["AMap.Geolocation"], //需要使用的的插件列表，如比例尺'AMap.Scale'等
        terrain: true, // 开启地形图
      })
        .then((AMap) => {
          this.map = new AMap.Map("container", {
            //设置地图容器id
            viewMode: "3D", //是否为3D地图模式
            zoom: level, //初始化地图级别
            center: [a, b], //初始化地图中心点位置
            terrain: true,
          });
          var url = "/geo/aroundShop?state=2";
          getRequest(url).then((resp) => {
             
            this.shops = resp.data.list;
            for (var i = 0; i < this.shops.length; i++)
              this.addPoint(this.shops[i].longitude, this.shops[i].latitude);
          });
        })
        .catch((e) => {
          console.log(e);
        });
    },
  },
};
</script>
   

<style scoped>
#container {
  width: 80%;
  height: 800px;
  margin: 20px auto;
  border: 1px solid rgb(75, 67, 67);
}
</style>