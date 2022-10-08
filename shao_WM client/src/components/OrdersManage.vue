<template>
  <div v-show="isAdmin">
    
    <el-select
      v-model="keyword"
      placeholder="默认为搜索全部门店的订单信息,请选择一个门店"
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
      v-model="value"
      placeholder="请选择一种状态"
      style="width: 200px; margin-right: 20px"
    >
      <el-option v-for="(item, index) in type" :key="index" :value="item">{{
        item
      }}</el-option>
    </el-select>
    <el-button type="primary" @click="search">搜索</el-button>
    <el-button type="info"   @click="clearsearch">重置</el-button>
    <br>
    <br>
    <el-table
      :data="orders"
      style="width: 100%"
      :default-sort="{ prop: 'date', order: 'descending' }"
      :cell-style="cellStyle"
   
    >
      <el-table-column prop="publishDate" label="订购日期" sortable width="160">
        <template slot-scope="scope">{{
          formatDate(scope.row.publishDate)
        }}</template>
      </el-table-column>
      <el-table-column prop="content" label="订单" width="320">
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
      <el-table-column prop="customer" label="姓名" sortable width="100">
        <template slot-scope="scope">
          <el-popover placement="top-start" width="68" trigger="click">
            <span>{{ scope.row.customer }}</span>
            <span slot="reference">{{
              scope.row.customer.length > 4
                ? scope.row.customer.substr(0, 4) + "..."
                : scope.row.customer.substr(0, 4)
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
      <el-table-column prop="price" label="价格" sortable width="100">
      </el-table-column>
      <el-table-column prop="limitTime" label="预计时间" sortable width="100"  align="center">
      </el-table-column>
      <el-table-column label="状态" width="80" align="left">
        <template slot-scope="scope">{{ type[scope.row.status] }}</template>
      </el-table-column>
      <el-table-column
        label="接单人信息"
        width="320"
        align="left"
        :show-overflow-tooltip="true"
      >
        <template slot-scope="scope"
          >接单人姓名:{{ scope.row.user.username }}&emsp;接单人电话:{{
            scope.row.user.phone
          }}</template
        >
      </el-table-column>
    </el-table>

    <br />
    <el-pagination
      background
      :page-size="pageSize"
      layout="prev, pager, next"
      :total="totalCount"
      @current-change="currentChange"
      v-show="orders.length > 0"
    >
    </el-pagination>
  </div>
</template>

<script>
import { getRequest } from "../utils/api";
import moment from "moment";
export default {
  mounted() {
    var _this = this;
    this.loading = true;
    getRequest("/isAdmin").then((resp) => {
      if (resp.status == 200) {
        _this.isAdmin = resp.data;
        if (_this.isAdmin == false)
          _this.$message({ type: "error", message: "您不是管理员" });
      }
    });
    this.loadOrders(1, this.pageSize, this.keyword);
    if (this.timer) {
      clearInterval(this.timer);
    } else {
      this.timer = setInterval(() => {
        this.queryInfo();
      }, 5000);
    }
    var url = "/geo/aroundShop?state=2";
    getRequest(url).then((resp) => {
      this.shops = resp.data.list;
    });
  },

  data() {
    return {
      value: "",
      shops: [],
      keyword: "",
      currentPage: 1,
      totalCount: -1,
      pageSize: 6,
      orders: [],
      type: ["暂未配送", "配送中", "已送达"],

      timer: null,
      isAdmin: false,
    };
  },
  destroyed() {
    clearInterval(this.timer);
  },

  methods: {
    clearsearch(){
        this.keyword="";
        this.value="";
        this.loadOrders(this.currentPage, this.pageSize);
    },
    search() {
      this.loadOrders(1, this.pageSize);
    },
    queryInfo() {
      //数据请求
      this.loadOrders(this.currentPage, this.pageSize);
    },
    cellStyle(row, column, rowIndex, columnIndex) {
      if (row.column.label === "状态" && row.row.status == 1) {
        return "color:blue"; //颜色可以自己选择
      }

      if (row.column.label === "状态" && row.row.status == 2) {
        return "color:green";
      }
      if (row.column.label === "状态" && row.row.status == 0) {
        return "color:red";
      }
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
      this.sumItems = this.selItems;
      this.loadOrders(currentPage, this.pageSize);
    },
    loadOrders(page, count) {
      var i = 0;
      for (; i < this.type.length; i++) {
        if (this.value == this.type[i]) {
          break;
        }
      }
      if (i == this.type.length) {
        i = -1;
      }
      var _this = this;
      var url =
        "/manage/getAllOrders?page=" +
        page +
        "&count=" +
        count +
        "&keyword=" +
        this.keyword+"&status="+i;
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
};
</script>
 
 
 