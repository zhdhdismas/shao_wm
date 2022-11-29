<template>
  <div v-show="isRider">
    <br />

    <div>
      <el-button :disabled="time > 0" @click="btnClick" style="float: left">
        {{ time > 0 ? `${time}秒后可重新点击` : "刷新列表" }}
      </el-button>
    </div>
    <br />
    &emsp; &emsp;
    <span style="float: left; margin-left: 20px"
      >上次刷新时间:{{ thisTime }}</span
    >

    <el-table
      :data="orders"
      tooltip-effect="dark"
      style="width: 100%; overflow-x: hidden; overflow-y: hidden"
      v-loading="loading"
      :default-sort="{ prop: 'date', order: 'descending' }"
    >
      <el-table-column prop="publishDate" label="订购日期" sortable width="180">
        <template slot-scope="scope">{{
          formatDate(scope.row.publishDate)
        }}</template>
      </el-table-column>
      <el-table-column prop="content" label="订单" width="240">
        <template slot-scope="scope">
          <el-popover placement="top-start" width="220" trigger="click">
            <span>{{ scope.row.content }}</span>
            <span slot="reference">{{
              scope.row.content.length > 15
                ? scope.row.content.substr(0, 15) + "..."
                : scope.row.content.substr(0, 15)
            }}</span>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column prop="limitTime" label="预计时间(min)" width="120" align="center">
      </el-table-column>
      <el-table-column prop="customer" label="姓名" sortable width="130">
        <template slot-scope="scope">
          <el-popover placement="top-start" width="68" trigger="click">
            <span>{{ scope.row.customer }}</span>
            <span slot="reference">{{
              scope.row.customer.length > 5
                ? scope.row.customer.substr(0, 5) + "..."
                : scope.row.customer.substr(0, 5)
            }}</span>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="电话" width="120"> </el-table-column>
      <el-table-column prop="address" label="地址" width="200">
        <template slot-scope="scope">
          <el-popover placement="top-start" width="138" trigger="click">
            <span>{{ scope.row.address }}</span>
            <span slot="reference">{{
              scope.row.address.length > 12
                ? scope.row.address.substr(0, 12) + "..."
                : scope.row.address.substr(0, 12)
            }}</span>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" sortable width="80">
      </el-table-column>
      <el-table-column prop="geoname" label="门店名"  width="200">
        <template slot-scope="scope">
          <el-popover placement="top-start" width="150" trigger="click">
            <span>{{ scope.row.geoname }}</span>
            <span slot="reference">{{
              scope.row.geoname.length > 12
                ? scope.row.geoname.substr(0, 12) + "..."
                : scope.row.geoname.substr(0, 12)
            }}</span>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column prop="distance" label="离门店的距离(km)"  width="135" align="center">
    </el-table-column>
      <el-table-column label="操作" align="left">
        <template slot-scope="scope">
          <el-button size="mini" @click="jiedan(scope.row)">接单 </el-button>
        </template>
      </el-table-column>
    </el-table>

    <br />
    <el-pagination
      background
      :page-size="pageSize"
      layout="prev, pager, next"
      :total="totalCount"
      @current-change="currentChange"
      style="float: right"
      v-show="this.orders.length > 0"
    >
    </el-pagination>
    <br />
    <br />
    <el-container >
      <el-main style="width: 100px">
        <el-tabs>
          <el-tab-pane  label="我的接单（进行中）" >
            <ride_table  state="0" ></ride_table>
          </el-tab-pane>
          <el-tab-pane label="已完成订单" >
            <ride_table  state="1" ></ride_table>
          </el-tab-pane>
        </el-tabs>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { putRequest } from "../utils/api";
import { getRequest } from "../utils/api";
import moment from "moment";
import RiderTable from "@/views/RiderTable";
export default {
  mounted() {
    var _this = this;
    this.loading = true;
    this.thisTime = this.formatDate();
    getRequest("/isRider").then((resp) => {
   
      if (resp.status == 200) {
        _this.isRider = resp.data;
        if (_this.isRider == false)
          _this.$message({ type: "error", message: "您不是骑手" });
      }
    });
    this.loadOrders(1, this.pageSize);
    window.$bus.$on("OrderReload", function () {
      _this.loading = true;
      _this.loadOrders(_this.currentPage, _this.pageSize);
    });
  },
  data() {
    return {
        jiedanName:'我的接单（进行中）',
      state: [0, 1],
      thisTime: 0,
      isRider: false,

      loading: false,
      currentPage: 1,
      totalCount: -1,
      pageSize: 6,
      orders: [],
      time: 0,
    };
  },
  methods: {
    btnClick() {
      window.$bus.$emit("OrderReload");
      // 设定时间
      this.time = 5;
      this.thisTime = this.formatDate();
      // 开启定时
      const interval = setInterval(() => {
        // 减号执行的位置决定if中的判断条件
        this.time--;
        // 停止定时器
        if (this.time < 0) {
          clearInterval(interval);
        }
      }, 1000);
    },
    jiedan(row) {
      var _this = this;
      this.$confirm("是否接下这单", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          _this.loading = true;
          var url = "/rider/receiveOrder";
          putRequest(url, { oid: row.oid }).then(
            (resp) => {
              if (resp.status == 200) {
                var data = resp.data;
                _this.$message({ type: data.status, message: data.msg });
                if (data.status == "success") {
                  window.$bus.$emit("OrderReload"); //通过选项卡都重新加载数据
                }
              } else {
                _this.$message({ type: "error", message: data.msg });
              }
              _this.loading = false;
            },
            (resp) => {
              _this.loading = false;
              _this.$message({ type: "error", message: "接单失败!" });
            }
          );
        })
        .catch(() => {
          _this.$message({
            type: "info",
            message: "已取消接单",
          });
        });
    },

    formatDate(time) {
      return moment(time).format("YYYY-MM-DD HH:mm:ss");
    },
    formatter(row, column) {
      return row.address;
    },
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.loading = true;
      this.loadOrders(currentPage, this.pageSize);
    },
    loadOrders(page, count) {
      var _this = this;
      var url = "/rider/getAllOrderNone?page=" + page + "&count=" + count;
      getRequest(url)
        .then(
          (resp) => {
            _this.loading = false;
            if (resp.status == 200) {
              _this.orders = resp.data.orders;
              _this.totalCount = resp.data.totalCount;
            }
          },
          (resp) => {
            _this.loading = false;
          }
        )
        .catch((resp) => {
          //压根没见到服务器
          _this.loading = false;
        });
    },
  },
  components: {
    ride_table: RiderTable,
  },
};
</script>
<style scoped>
.menu_table_footer {
  display: flex;
  box-sizing: content-box;
  padding-top: 10px;
  padding-bottom: 0px;
  /* 下方菜品样式 */
  padding-left: 40px;
  margin-bottom: 0px;
  text-align: center;
  justify-content: space-between;
  color: sienna;
}
</style>
