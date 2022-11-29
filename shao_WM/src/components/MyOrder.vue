<template>
  <div>
    <br />
    <el-dialog
      title="支付详情"
      :visible.sync="dialogFormVisible"
      width="500px"
      :modal="false"
    >
      <span>订单号:{{ tradeNo }}</span
      ><br />
      <span>支付宝交易凭证号:{{ aliPaytradeNo }}</span
      ><br />
      <span>买家付款时间:{{ gmtPayment }}</span>
    </el-dialog>
    <el-table
      :data="orders"
      style="width: 100%"
      :default-sort="{ prop: 'date', order: 'descending' }"
      :border="true"
      :stripe="true"
    >
      <el-table-column prop="publishDate" label="订购日期" sortable width="180">
        <template slot-scope="scope">{{
          formatDate(scope.row.publishDate)
        }}</template>
      </el-table-column>
      <el-table-column prop="content" label="订单" width="320">
        <template slot-scope="scope">
          <el-popover placement="top-start" width="220" trigger="click" style="cursor: pointer">
            <span>{{ scope.row.content }}</span>
            <span slot="reference">{{
              scope.row.content.length > 20
                ? scope.row.content.substr(0, 20) + "..."
                : scope.row.content.substr(0, 20)
            }}</span>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column
        prop="expire"
        label="支付情况"
        width="180"
        align="center"
      >
        <template slot-scope="scope">
          <span>{{ scope.row.pay }} </span>
          <span
            style="color: #409eff; cursor: pointer"
            @click="payDetail(scope.row)"
            >支付详情</span
          >
        </template>
      </el-table-column>
      <el-table-column prop="customer" label="姓名" sortable width="120">
        <template slot-scope="scope">
          <el-popover placement="top-start" width="68" trigger="click" style="cursor: pointer" >
            <span>{{ scope.row.customer }}</span>
            <span slot="reference">{{
              scope.row.customer.length > 5
                ? scope.row.customer.substr(0, 5) + "..."
                : scope.row.customer.substr(0, 5)
            }}</span>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="电话"> </el-table-column>
      <el-table-column prop="address" label="地址" width="200">
        <template slot-scope="scope">
          <el-popover placement="top-start" width="138" trigger="click" style="cursor: pointer" >
            <span>{{ scope.row.address }}</span>
            <span slot="reference">{{
              scope.row.address.length > 12
                ? scope.row.address.substr(0, 12) + "..."
                : scope.row.address.substr(0, 12)
            }}</span>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" sortable width="120">
      </el-table-column>
      <el-table-column prop="geoname" label="门店名" width="200">
        <template slot-scope="scope">
          <el-popover placement="top-start" width="150" trigger="click" style="cursor: pointer" >
            <span>{{ scope.row.geoname }}</span>
            <span slot="reference">{{
              scope.row.geoname.length > 10
                ? scope.row.geoname.substr(0, 10) + "..."
                : scope.row.geoname.substr(0, 10)
            }}</span>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column
        prop="distance"
        label="离门店的距离(km)"
        width="180"
        align="center"
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
      v-show="this.orders.length > 0"
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

    this.loadOrders(1, this.pageSize);
  },
  data() {
    return {
      dialogFormVisible: false,
      aliPaytradeNo: "",
      gmtPayment: "",
      tradeNo: "",
      currentPage: 1,
      totalCount: -1,
      pageSize: 6,
      orders: [],
    };
  },
  methods: {
    payDetail(row) {
      console.log(row);
      this.dialogFormVisible = true;
      this.aliPaytradeNo = row.aliPaytradeNo;
      this.gmtPayment = row.gmtPayment;
      this.tradeNo = row.oid;
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
      var _this = this;
      var url = "/myHistoryOrders?page=" + page + "&count=" + count;
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
