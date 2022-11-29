<template>
  <div v-show="isAdmin">
    <el-select
      v-model="keyword"
      placeholder="默认为搜索全部门店的营业额和利润,请选择一个门店"
      style="width: 500px; margin-right: 20px"
    >
      <el-option
        v-for="(item, index) in shops"
        :key="index"
        :value="item.name"
        >{{ item.name }}</el-option
      >
    </el-select>
    <el-select
      v-model="year"
      placeholder="请选择年份(默认为今年)"
      style="width: 200px; margin-right: 20px"
    >
      <el-option v-for="(item, index) in yearArr" :key="index" :value="item">{{
        item + "年"
      }}</el-option>
    </el-select>
    <el-button type="primary" @click="search">搜索</el-button>
    <el-button type="info" @click="clearsearch">重置</el-button>
    <br />
    <br />
    <div class="view-content">
      <div
        id="myChart"
        :style="{
          width: '700px',
          height: '350px',
          display: 'inline-block',
        }"
      ></div>
      <div
        id="myChart2"
        :style="{
          width: '700px',
          height: '350px',
          display: 'inline-block',
        }"
      ></div>
    </div>
    <el-divider></el-divider>
    <el-select
      v-model="keyword2"
      placeholder="默认为搜索全部门店的营业额和利润,请选择一个门店"
      style="width: 500px; margin-right: 20px"
    >
      <el-option
        v-for="(item, index) in shops"
        :key="index"
        :value="item.name"
        >{{ item.name }}</el-option
      >
    </el-select>
    <el-select
      v-model="year2"
      placeholder="请选择年份(默认为今年)"
      style="width: 200px; margin-right: 20px"
    >
      <el-option v-for="(item, index) in yearArr" :key="index" :value="item">{{
        item + "年"
      }}</el-option>
    </el-select>
    <el-select
      v-model="month"
      placeholder="请选择月份(默认为这个月)"
      style="width: 200px; margin-right: 20px"
    >
      <el-option v-for="(item, index) in 12" :key="index" :value="item">{{
        item + "月"
      }}</el-option>
    </el-select>
    <el-button type="primary" @click="daySearch">搜索</el-button>
    <el-button type="info" @click="clearsearch2">重置</el-button>
    <br />
    <br />
    <div
      class="view-content"
      id="myChart3"
      :style="{
        width: '1600px',
        height: '350px',
      }"
    ></div>
    <div
      class="view-content"
      id="myChart4"
      :style="{
        width: '1600px',
        height: '350px',
      }"
    ></div>

    <el-divider></el-divider>
    <span class="demonstration">选择日期范围</span>
    &emsp;
    <el-date-picker
      v-model="value1"
      type="datetimerange"
      start-placeholder="开始日期"
      end-placeholder="结束日期"
      value-format="yyyy-MM-dd HH:mm:ss"
      :default-time="['00:00:00']"
    >
    </el-date-picker>
    &emsp;
    <el-button @click="what">查询</el-button>
    <i class="el-icon-info" @click="open2"></i>
    <br />
    <br />
    <span></span>
    营业额:<span style="color: red">&emsp;{{ sumPrice }}&emsp;</span>元
    <br />
    利润:<span style="color: red">&emsp;{{ profit }}&emsp;</span>元
    <br />
    <br />
    <el-table :data="userList" :border="true" style="width: 100%">
      <el-table-column prop="username" label="用户名"> </el-table-column>
      <el-table-column prop="phone" label="手机号"> </el-table-column>
      <el-table-column prop="count" label="完成单数"> </el-table-column>
    </el-table>
    <el-pagination
      background
      :page-size="pageSize"
      layout="prev, pager, next"
      :total="totalCount"
      @current-change="currentChange"
      v-show="userList.length > 0"
    >
    </el-pagination>
  </div>
</template>
<script>
import { getRequest } from "../utils/api";
export default {
  mounted: function () {
    var _this = this;
    this.loading = true;
    getRequest("/isAdmin").then((resp) => {
      if (resp.status == 200) {
        _this.isAdmin = resp.data;
        if (_this.isAdmin == false)
          _this.$message({ type: "error", message: "您不是管理员" });
      }
    });
    var url = "/geo/aroundShop?state=2";
    getRequest(url).then((resp) => {
      this.shops = resp.data.list;
    });
    var x = new Date().getFullYear();
    this.yearArr.push(x);
    this.yearArr.push(x - 1);
    this.yearArr.push(x - 2);
    this.year = x;
    this.year2 = x;
    this.month = new Date().getMonth() + 1;
    this.search();
    this.daySearch();
    // this.loadMenus(1, this.pageSize);
  },
  data() {
    return {
      x: [],
      keyword2: "",
      year2: "",
      sumOrder2: [],
      sumMonthPrice: [],
      sumMonthProfit: [],
      month: "",

      yearArr: [],
      year: "",
      keyword: "",
      sumOrder: [],
      sumYearPrice: [],
      sumYearProfit: [],
      shops: [],
      value1: [],
      loading: false,
      currentPage: 1,
      pageSize: 3,
      totalCount: -1,

      userList: [],
      sumPrice: "",
      profit: "",

      isAdmin: false,
      why: [1, 2, 3],
    };
  },
  methods: {
    clearsearch2() {
      this.keyword2 = "";
      this.daySearch();
    },
    daySearch() {
      var url = "";
      if (this.keyword2 == "") {
        url =
          "/manage/getMoneyOfMonth?year=" + this.year2 + "&month=" + this.month;
      } else {
        var i = 0;
        for (; i < this.shops.length; i++) {
          if (this.shops[i].name == this.keyword2) {
            break;
          }
        }
        url =
          "/manage/getMoneyOfMonth?year=" +
          this.year2 +
          "&month=" +
          this.month +
          "&gid=" +
          this.shops[i].gid;
      }
      this.sumMonthPrice = [];
      this.sumMonthProfit = [];
      this.sumOrder2 = [];
      this.x = [];
      getRequest(url).then((resp) => {
        console.log(resp.data.length);
        for (var i = 0; i < resp.data.length; i++) {
          this.x.push(this.month + "-" + (i + 1));
          this.sumMonthPrice.push(resp.data[i].sumPrice);
          this.sumMonthProfit.push(resp.data[i].profit);
          this.sumOrder2.push(resp.data[i].totalCount);
        }
        this.drawLine3(this.sumMonthPrice, this.sumMonthProfit);
        this.drawLine4(this.sumOrder2);
      });
    },
    clearsearch() {
      this.keyword = "";
      this.search();
    },
    search() {
      var url = "";
      if (this.keyword == "") {
        url = "/manage/getMoneyOfYear?year=" + this.year;
      } else {
        var i = 0;
        for (; i < this.shops.length; i++) {
          if (this.shops[i].name == this.keyword) {
            break;
          }
        }
        url =
          "/manage/getMoneyOfYear?year=" +
          this.year +
          "&gid=" +
          this.shops[i].gid;
      }
      this.sumYearPrice = [];
      this.sumOrder = [];
      this.sumYearProfit = [];
      getRequest(url).then((resp) => {
        for (var i = 0; i < resp.data.length; i++) {
          this.sumYearPrice.push(resp.data[i].sumPrice);
          this.sumYearProfit.push(resp.data[i].profit);
          this.sumOrder.push(resp.data[i].totalCount);
        }
        this.drawLine(this.sumYearPrice, this.sumYearProfit);
        this.drawLine2(this.sumOrder);
      });
    },
    drawLine(sumYearPrice, sumYearProfit) {
      // 基于准备好的dom，初始化echarts实例
      let myChart = this.$echarts.init(document.getElementById("myChart"));
      // 绘制图表配置
      console.log(myChart);
      let option = {
        color: "#c23531",
        title: {
          text: "年营业额利润表",
          padding: [20, 20, 100, 100],
        },

        toolbox: {
          padding: [10, 60, 100, 100],
          show: true,
          feature: {
            mark: {
              // '辅助线开关'
              show: true,
            },
            dataView: {
              //数据视图工具，可以展现当前图表所用的数据，编辑后可以动态更新
              show: true, //是否显示该工具。
              title: "数据视图",
              readOnly: true, //是否不可编辑（只读）
              lang: ["年营业额利润表", "关闭", "刷新"], //数据视图上有三个话术，默认是['数据视图', '关闭', '刷新']
              backgroundColor: "#f4ebc8", //数据视图浮层背景色。
              textareaColor: "#f7f3e3", //数据视图浮层文本输入区背景色
              textareaBorderColor: "#333", //数据视图浮层文本输入区边框颜色
              textColor: "#000", //文本颜色。
              buttonColor: "#c23531", //按钮颜色。
              buttonTextColor: "#fff", //按钮文本颜色。
            },

            dataZoom: {
              yAxisIndex: "none",
            },

            magicType: {
              type: ["line", "bar"],
            },
            restore: {},
            saveAsImage: {},
            zlevel: 0, //所属图形的Canvas分层，zlevel 大的 Canvas 会放在 zlevel 小的 Canvas 的上面
            z: 2, //所属组件的z分层，z值小的图形会被z值大的图形覆盖
            left: "center", //组件离容器左侧的距离,'left', 'center', 'right','20%'
            top: "top", //组件离容器上侧的距离,'top', 'middle', 'bottom','20%'
            right: "auto", //组件离容器右侧的距离,'20%'
            bottom: "auto", //组件离容器下侧的距离,'20%'
            width: "auto", //图例宽度
            height: "auto",
          },
        },
        tooltip: {},
        xAxis: {
          data: [
            "1月",
            "2月",
            "3月",
            "4月",
            "5月",
            "6月",
            "7月",
            "8月",
            "9月",
            "10月",
            "11月",
            "12月",
          ],
        },
        yAxis: {},
        series: [
          {
            name: "营业额",
            type: "bar",
            data: sumYearPrice,
            color: "#c23531",
            //z: "-1", //改变这根柱子的层级使这根柱子在下面
          },
          {
            name: "利润",
            data: sumYearProfit,
            type: "bar",
            barGap: "-100%", //移动第二个柱子的位置实现重叠
            color: "green",
          },
        ],
      };
      // 窗口大小自适应方案
      myChart.setOption(option);
    },
    drawLine2(sumOrder) {
      // 基于准备好的dom，初始化echarts实例
      let myChart2 = this.$echarts.init(document.getElementById("myChart2"));
      // 绘制图表配置
      let option = {
        color: "#c23531",
        title: {
          text: "年销量表",
          padding: [20, 20, 100, 100],
        },

        toolbox: {
          padding: [10, 60, 100, 100],
          show: true,
          feature: {
            mark: {
              // '辅助线开关'
              show: true,
            },
            dataView: {
              //数据视图工具，可以展现当前图表所用的数据，编辑后可以动态更新
              show: true, //是否显示该工具。
              title: "数据视图",
              readOnly: true, //是否不可编辑（只读）
              lang: ["年销量表", "关闭", "刷新"], //数据视图上有三个话术，默认是['数据视图', '关闭', '刷新']
              backgroundColor: "#f4ebc8", //数据视图浮层背景色。
              textareaColor: "#f7f3e3", //数据视图浮层文本输入区背景色
              textareaBorderColor: "#333", //数据视图浮层文本输入区边框颜色
              textColor: "#000", //文本颜色。
              buttonColor: "#c23531", //按钮颜色。
              buttonTextColor: "#fff", //按钮文本颜色。
            },

            dataZoom: {
              yAxisIndex: "none",
            },

            magicType: {
              type: ["line", "bar"],
            },
            restore: {},
            saveAsImage: {},
            zlevel: 0, //所属图形的Canvas分层，zlevel 大的 Canvas 会放在 zlevel 小的 Canvas 的上面
            z: 2, //所属组件的z分层，z值小的图形会被z值大的图形覆盖
            left: "center", //组件离容器左侧的距离,'left', 'center', 'right','20%'
            top: "top", //组件离容器上侧的距离,'top', 'middle', 'bottom','20%'
            right: "auto", //组件离容器右侧的距离,'20%'
            bottom: "auto", //组件离容器下侧的距离,'20%'
            width: "auto", //图例宽度
            height: "auto",
          },
        },
        tooltip: {},
        xAxis: {
          data: [
            "1月",
            "2月",
            "3月",
            "4月",
            "5月",
            "6月",
            "7月",
            "8月",
            "9月",
            "10月",
            "11月",
            "12月",
          ],
        },
        yAxis: {},
        series: [
          {
            name: "订单量",
            type: "bar",
            data: sumOrder,
            color: "#c23531",
          },
        ],
      };
      // 窗口大小自适应方案
      myChart2.setOption(option);
    },
    drawLine3(sumYearPrice, sumYearProfit) {
      console.log(this.x);
      // 基于准备好的dom，初始化echarts实例
      let myChart3 = this.$echarts.init(document.getElementById("myChart3"));
      // 绘制图表配置
      let option = {
        color: "#c23531",
        title: {
          text: "月营业额利润表",
          padding: [20, 20, 100, 200],
        },

        toolbox: {
          padding: [10, 160, 100, 100],
          show: true,
          feature: {
            mark: {
              // '辅助线开关'
              show: true,
            },
            dataView: {
              //数据视图工具，可以展现当前图表所用的数据，编辑后可以动态更新
              show: true, //是否显示该工具。
              title: "数据视图",
              readOnly: true, //是否不可编辑（只读）
              lang: ["月营业额利润表", "关闭", "刷新"], //数据视图上有三个话术，默认是['数据视图', '关闭', '刷新']
              backgroundColor: "#f4ebc8", //数据视图浮层背景色。
              textareaColor: "#f7f3e3", //数据视图浮层文本输入区背景色
              textareaBorderColor: "#333", //数据视图浮层文本输入区边框颜色
              textColor: "#000", //文本颜色。
              buttonColor: "#c23531", //按钮颜色。
              buttonTextColor: "#fff", //按钮文本颜色。
            },

            dataZoom: {
              yAxisIndex: "none",
            },

            magicType: {
              type: ["line", "bar"],
            },
            restore: {},
            saveAsImage: {},
            zlevel: 0, //所属图形的Canvas分层，zlevel 大的 Canvas 会放在 zlevel 小的 Canvas 的上面
            z: 2, //所属组件的z分层，z值小的图形会被z值大的图形覆盖
            left: "center", //组件离容器左侧的距离,'left', 'center', 'right','20%'
            top: "top", //组件离容器上侧的距离,'top', 'middle', 'bottom','20%'
            right: "auto", //组件离容器右侧的距离,'20%'
            bottom: "auto", //组件离容器下侧的距离,'20%'
            width: "auto", //图例宽度
            height: "auto",
          },
        },
        tooltip: {},
        xAxis: {
          axisLabel: {
            show: true,
            textStyle: {
              color: "#142468",
            },
            rotate: 30, // 设置rotate属性值，文字倾斜
          },
          data: this.x,
        },
        yAxis: {},
        series: [
          {
            name: "营业额",
            type: "bar",
            data: sumYearPrice,
            color: "#c23531",
            //z: "-1", //改变这根柱子的层级使这根柱子在下面
          },
          {
            name: "利润",
            data: sumYearProfit,
            type: "bar",
            barGap: "-100%", //移动第二个柱子的位置实现重叠
            color: "green",
          },
        ],
      };
      // 窗口大小自适应方案
      myChart3.setOption(option);
    },
    drawLine4(sumOrder) {
      // 基于准备好的dom，初始化echarts实例
      let myChart4 = this.$echarts.init(document.getElementById("myChart4"));
      // 绘制图表配置
      let option = {
        color: "#c23531",
        title: {
          text: "月销量表",
          padding: [20, 20, 100, 200],
        },

        toolbox: {
          padding: [10, 160, 100, 100],
          show: true,
          feature: {
            mark: {
              // '辅助线开关'
              show: true,
            },
            dataView: {
              //数据视图工具，可以展现当前图表所用的数据，编辑后可以动态更新
              show: true, //是否显示该工具。
              title: "数据视图",
              readOnly: true, //是否不可编辑（只读）
              lang: ["月销量表", "关闭", "刷新"], //数据视图上有三个话术，默认是['数据视图', '关闭', '刷新']
              backgroundColor: "#f4ebc8", //数据视图浮层背景色。
              textareaColor: "#f7f3e3", //数据视图浮层文本输入区背景色
              textareaBorderColor: "#333", //数据视图浮层文本输入区边框颜色
              textColor: "#000", //文本颜色。
              buttonColor: "#c23531", //按钮颜色。
              buttonTextColor: "#fff", //按钮文本颜色。
            },

            dataZoom: {
              yAxisIndex: "none",
            },

            magicType: {
              type: ["line", "bar"],
            },
            restore: {},
            saveAsImage: {},
            zlevel: 0, //所属图形的Canvas分层，zlevel 大的 Canvas 会放在 zlevel 小的 Canvas 的上面
            z: 2, //所属组件的z分层，z值小的图形会被z值大的图形覆盖
            left: "center", //组件离容器左侧的距离,'left', 'center', 'right','20%'
            top: "top", //组件离容器上侧的距离,'top', 'middle', 'bottom','20%'
            right: "auto", //组件离容器右侧的距离,'20%'
            bottom: "auto", //组件离容器下侧的距离,'20%'
            width: "auto", //图例宽度
            height: "auto",
          },
        },
        tooltip: {},
        xAxis: {
          axisLabel: {
            show: true,
            textStyle: {
              color: "#142468",
            },
            rotate: 30, // 设置rotate属性值，文字倾斜
          },
          data: this.x,
        },
        yAxis: {},
        series: [
          {
            name: "销量",
            type: "bar",
            data: sumOrder,
            color: "#c23531",
            //z: "-1", //改变这根柱子的层级使这根柱子在下面
          },
        ],
      };
      // 窗口大小自适应方案
      myChart4.setOption(option);
    },
    open2() {
      this.$notify({
        title: "提示",
        message: "可以用来查询这段时间骑手完成的订单数,和总的营业额，利润",
        duration: 0,
      });
    },
    what() {
      this.loadMenus(this.currentPage, this.pageSize);
    },
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.loading = true;
      this.loadMenus(currentPage, this.pageSize);
    },
    //加载页面
    loadMenus(page, count) {
      var _this = this;
      var url =
        "/manage/whatMoney?startDate=" +
        this.value1[0] +
        "&endDate=" +
        this.value1[1] +
        "&page=" +
        page +
        "&count=" +
        count;
      getRequest(url)
        .then(
          (resp) => {
            _this.loading = false;
            if (resp.status == 200) {
              _this.userList = resp.data.userList;
              _this.profit = resp.data.profit;
              _this.sumPrice = resp.data.sumPrice;
              _this.totalCount = resp.data.totalCount;
            }
          },
          (resp) => {
            _this.loading = false;
            if (resp.response.status == 403) {
              _this.$message({ type: "error", message: resp.response.data });
            }
          }
        )
        .catch((resp) => {
          //压根没见到服务器
          _this.loading = false;
        });
    },
  },
};
</script>

