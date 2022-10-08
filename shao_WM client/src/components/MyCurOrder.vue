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
    <el-dialog
      title="支付订单"
      :visible.sync="dialogFormVisible2"
      width="500px"
      @close="closeExpire"
      :modal="false"
      :append-to-body="true"
    >
      <span>还剩</span><span style="color: red">{{ min }}</span
      >分钟:<span style="color: red">{{ sec }}</span
      ><span>秒 将自动取消订单</span><br />
      <span>订单:{{ rowData.content }}</span>
      <br />
      <span>订购时间:{{ formatDate(rowData.publishDate) }}</span>
      <br />
      <br />
      <el-button type="primary" size="mini" @click="payMoney(rowData)"
        >支付宝入口</el-button
      >
    </el-dialog>
    <el-table
      :data="orders"
      style="width: 100%"
      :default-sort="{ prop: 'date', order: 'descending' }"
      :border="true"
      :cell-style="cellStyle"
    >
      <el-table-column prop="publishDate" label="订购日期" sortable width="180">
        <template slot-scope="scope">{{
          formatDate(scope.row.publishDate)
        }}</template>
      </el-table-column>
      <el-table-column prop="content" label="订单" width="160">
        <template slot-scope="scope">
          <el-popover placement="top-start" width="220" trigger="hover">
            <span>{{ scope.row.content }}</span>
            <span slot="reference">{{
              scope.row.content.length > 10
                ? scope.row.content.substr(0, 10) + "..."
                : scope.row.content.substr(0, 10)
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
          <span>{{ scope.row.pay }}</span>
          <span
            v-show="scope.row.pay == '已支付'"
            style="color: #409eff; cursor: pointer"
            @click="payDetail(scope.row)"
            >支付详情</span
          >
          <el-button
            v-show="scope.row.pay == '未支付'"
            type="primary"
            size="mini"
            @click="openGo(scope.row)"
            >去支付</el-button
          >
        </template>
      </el-table-column>
      <el-table-column
        prop="limitTime"
        label="预计时间"
        width="100"
        align="center"
      >
      </el-table-column>
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
      <el-table-column prop="phone" label="电话" width="125"> </el-table-column>
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
      <el-table-column prop="geoname" label="门店名" width="200">
        <template slot-scope="scope">
          <el-popover placement="top-start" width="138" trigger="click">
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
        width="160"
        align="center"
      >
      </el-table-column>
      <!-- <el-button type="danger" size="mini" style="margin: 0px;" v-show="this.menus.length>0"
                 :disabled="this.selItems.length==0" @click="deleteMany">批量删除
      </el-button> -->
    </el-table>
  </div>
</template>

<script>
import { getRequest } from "../utils/api";
import moment from "moment";
import exp from "constants";
export default {
  mounted() {
    var _this = this;
    this.loading = true;
    for (var i = 0; i < this.curOrderMax; i++) {
      this.timeArr.push(this.timeArr[0]);
    }
    this.loadOrders(1, this.pageSize);
  },

  data() {
    return {
        loading:false,
      flag: false,
      dialogFormVisible2: false,
      dialogFormVisible: false,
      currentPage: 1,
      totalCount: -1,
      pageSize: 6,
      orders: [],
      day: 0,
      hr: 0,
      min: 0,
      sec: 0,
      time: 0,
      aliPaytradeNo: "",
      gmtPayment: "",
      tradeNo: "",
      //  1:23:21  5531   /3600
      expire: -1,
      rowData: [],
      // 5001
    };
  },
  methods: {
    closeExpire() {
      console.log("###");
      this.expire = -1;
 
      this.flag = true;
    },
    openGo(row) {
      setTimeout(() => {
        this.flag = false;
      this.dialogFormVisible2 = true;
      this.rowData = row;
      if (this.expire == -1) {
        getRequest("/getAExpire/" + row.oid).then((resp) => {
          this.expire = resp.data;
          this.countdown(this.expire);
        });
      }

      }, 100);
      
    },

    payDetail(row) {
      console.log(row);
      this.dialogFormVisible = true;
      this.aliPaytradeNo = row.aliPaytradeNo;
      this.gmtPayment = row.gmtPayment;
      this.tradeNo = row.oid;
    },
    payMoney(row) {
      var sub = "";
      if (row.content.length > 40) {
        sub = row.content.substring(0, 40) + "...(剩下菜品请去我的订单查看)";
      } else {
        sub = row.content;
      }
      var subject = sub;
      var traceNo = row.oid;
      var url =
        "/alipay/pay?subject=" +
        subject +
        "&traceNo=" +
        traceNo +
        "&totalAmount=" +
        row.price;
      getRequest(url).then((resp) => {
        window.open(
          `http://localhost:8081/alipay/pay?subject=${subject}&traceNo=${traceNo}&totalAmount=${row.price}`
        );
        this.dialogFormVisible2 = false;
        this.$alert("请尽快完成付款,如果您已完成付款,忽略这条消息", "提示", {
          confirmButtonText: "知道了",
          callback: (action) => {
            this.loadOrders(this.currentPage, this.pageSize);
          },
        });
      });
    },
    cellStyle(row, column, rowIndex, columnIndex) {
      if (row.column.label === "支付情况" && row.row.pay == "已支付") {
        return "color:green"; //颜色可以自己选择
      }

      if (row.column.label === "支付情况" && row.row.pay == "未支付") {
        return "color:red";
      }
    },
    countdown(msec) {
      if (this.flag) {
        return;
      }
      if (msec < 0) return;

      let day = parseInt(msec / 1000 / 60 / 60 / 24);
      let hr = parseInt((msec / 1000 / 60 / 60) % 24);
      let min = parseInt((msec / 1000 / 60) % 60);
      let sec = parseInt((msec / 1000) % 60);
      this.day = day;
      this.hr = hr > 9 ? hr : "0" + hr;
      this.min = min > 9 ? min : "0" + min;
      this.sec = sec > 9 ? sec : "0" + sec;

      if (this.flag) {
        return;
      }
      const that = this;
      if (min >= 0 && sec >= 0) {
        //倒计时结束关闭订单
        if (min <= 0 && sec <= 0) {
          this.dialogFormVisible2 = false;
          this.loadOrders();
          return;
        }
        if (this.flag) {
          return;
        }
        setTimeout(function () {
          that.countdown(msec - 100);
        }, 100);
      }
    },
    formatDate(time) {
      return moment(time).format("YYYY-MM-DD HH:mm:ss");
    },
    formatter(row, column) {
      return row.address;
    },

    loadOrders(page, count) {
      var _this = this;
      var url = "/getMyOrders";
      getRequest(url)
        .then(
          (resp) => {
            _this.loading = false;
            if (resp.status == 200) {
              _this.orders = resp.data.orders;
              _this.time = resp.data.orders.limitTime;
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
 
