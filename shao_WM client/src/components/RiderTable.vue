 
</style>
<template>
  <div>
    <!--<div style="width: 100%;height: 1px;background-color: #20a0ff;margin-top: 8px;margin-bottom: 0px"></div>-->
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
      <el-table-column prop="content" label="订单"  width="320">
        <template slot-scope="scope">
          <el-popover placement="top-start" width="220" trigger="click">
            <span>{{ scope.row.content }}</span>
            <span slot="reference">{{
              scope.row.content.length > 20
                ? scope.row.content.substr(0, 20) + "..."
                : scope.row.content.substr(0, 20)
            }}</span>
          </el-popover>
        </template>
         </el-table-column>
         <el-table-column prop="limitTime" label="预计时间(min)" width="120" align="center"></el-table-column>
      <el-table-column prop="customer" label="姓名" sortable width="120">
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
      <el-table-column prop="price" label="价格" sortable width="110" align="center">
      </el-table-column>
      <el-table-column label="操作" align="center" width="150" v-if="state==0" >
        <template slot-scope="scope">
          <el-button size="mini" @click="songda(scope.row)"  >确认送达 </el-button>
        </template>
      </el-table-column>
    </el-table>

    <br />
    <span v-show="state==1">总接单数:</span>
     <span style="color:red" v-show="state==1">{{totalCount}}</span>
      <span v-show="state==1">单</span>
    &emsp;
    &emsp;
    <span v-show="state==1">本月接单数: </span>
    <span style="color:red" v-show="state==1">{{mouthCount}}</span>
    <span v-show="state==1">单</span>
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
    
  </div>
</template>
 

<script>
import { putRequest } from "../utils/api";
import { getRequest } from "../utils/api";
import moment from "moment";
export default {
  data() {
    return {
      loading: false,
      currentPage: 1,
      totalCount: -1,
      pageSize: 6,
      orders: [],
      mouthCount: 0,
    };
  },
  mounted() {
    var _this = this;
    this.loading = true;
    this.loadOrders(1, this.pageSize);
    window.$bus.$on("OrderReload", function () {
      _this.loading = true;
      _this.loadOrders(_this.currentPage, _this.pageSize);
      if(_this.state==1){
        getRequest("/rider/getMyMonthAchieved").then((resp) => {
            _this.mouthCount = resp.data;
        });
      }
      
    });
    if(_this.state==1){
        getRequest("/rider/getMyMonthAchieved").then((resp) => {
            this.mouthCount = resp.data;
        });
    }
    
  },
  methods: {
    songda(row) {
      var _this = this;
      this.$confirm("是否确认送达", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          _this.loading = true;
          var url = "/rider/confirmOrder";
          putRequest(url, { oid: row.oid }).then(
            (resp) => {
              if (resp.status == 200) {
                var data = resp.data;
                _this.$message({ type: data.status, message: data.msg });
                if (data.status == "success") {
                  window.$bus.$emit("OrderReload");
                  /* this.loadOrders(1, this.pageSize); */
                }
              } else {
                _this.$message({ type: "error", message: data.msg });
              }
              _this.loading = false;
            },
            (resp) => {
              _this.loading = false;
              _this.$message({ type: "error", message: "确认失败!" });
            }
          );
        })
        .catch(() => {
          _this.$message({
            type: "info",
            message: "已取消送达",
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
      var url = "";
      if (this.state == 0) {
        url = "/rider/getMyCurReceiveOrder?page=" + page + "&count=" + count;
      } else {
        url =
          "/rider/getMyReceiveOrderAchieved?page=" + page + "&count=" + count;
      }
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
  props: ["state"],
};
</script>
