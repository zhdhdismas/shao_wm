<template>
  <el-container class="menu_list">
    <el-main class="main">
      <el-tabs @tab-click="handleClick" type="border-card">
        <el-tab-pane :label="lab" v-for="(lab, index) in label" :key="index">
          <menu_table
            :state="states[index]"
            :category="lab"
            :sumPrice="sumPrice"
            ref="mclear"
            @mget="mget"
          ></menu_table>
        </el-tab-pane>
      </el-tabs>
    </el-main>
    <el-footer>
      <el-tag
        :key="index"
        v-for="(title, index) in sumTitle"
        :disable-transitions="false"
        style="margin-left: 20px"
      >
        {{ index+1+'、'+title }}
      </el-tag>
      <!-- Form -->
      <el-dialog
        title="修改个人信息"
        width="50%"
        top="5vh"
        :visible.sync="dialogFormVisible"
      >
        <el-form :model="updatemsg">
          <el-form-item label="用户名">
            <el-input
              v-model="updatemsg.username"
              autocomplete="off"
            ></el-input>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="updatemsg.phone" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="联系地址">
            <el-select
              v-model="curaddress"
              filterable
              allow-create
              default-first-option
              clearable
              placeholder="请选择或输入地址"
            >
              <el-option
                v-for="(item, index) in updatemsg.addresses"
                :key="index"
                :value="item.address"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>

        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取 消</el-button>
          <el-button type="primary" @click="updateAddress(curaddress)"
            >确 定</el-button
          >
        </div>
      </el-dialog>

      <el-descriptions title="用户信息">
        <el-descriptions-item label="用户名">{{
          usermsg.username
        }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{
          usermsg.phone
        }}</el-descriptions-item>

        <el-descriptions-item label="联系地址">{{
          firstaddress
        }}</el-descriptions-item>
      </el-descriptions>
      <div style="text-align: left">
        <el-button
          @click="openDrawer()"
          type="text"
          icon="el-icon-caret-right
"
        >
          选择最近门店
        </el-button>
        &emsp;

        <span v-show="this.getTransExp.name != ''">您选择的门店为:</span>
        <span v-show="this.getTransExp.name != ''">{{ getTransExp.name }}</span>
        <span v-show="this.getTransExp.name != ''" style="color: blue"
          >运费</span
        >
        <span v-show="this.getTransExp.name != ''" style="color: red">{{
          getTransExp.exp
        }}</span>
        <span v-show="this.getTransExp.name != ''">元</span>
      </div>

      <el-drawer :visible.sync="drawer" :direction="direction" :modal="false">
        <shop_list
          :x="x"
          :y="y"
          :key="this.x + this.y"
          @transExp="transExp"
        ></shop_list>
      </el-drawer>
      <el-button type="danger" size="mini" @click="clearmenus"
        >清空重新选菜</el-button
      >
      <el-button type="danger" size="mini" @click="openPersonMessage"
        >修改个人信息</el-button
      >
      <el-button type="primary" size="mini" @click="submitMenus"
        >提交订单</el-button
      >

      <el-button type="text" @click="openVoucher">选择我的优惠券</el-button>
      <span style="margin-left: 50px">已优惠</span
      ><span style="color: red"> {{ discountSum }}</span>
      <span>元</span>
      <i class="el-icon-d-arrow-right"></i>
      <i class="el-icon-d-arrow-right"></i>
      <i class="el-icon-d-arrow-right"></i>
      <span style="margin-left: 20px">总计</span
      ><span style="color: red">
        {{
          sumPrice + transportMoney - discountSum < 0
            ? 0
            : sumPrice + transportMoney - discountSum
        }}</span
      >
      <span>元</span>

      <br />
      <br />

      <br />
    </el-footer>
    <el-dialog title="选择我的优惠券" :visible.sync="isVoucher" :modal="false">
      <p v-show="this.vouchers.length == 0">暂无可用优惠券</p>
      <el-checkbox-group v-model="selectVouchers" @change="discount">
        <el-checkbox-button
          v-for="(v, index) in vouchers"
          :label="v.title"
          :key="index"
          >{{ v.title }}
          <br />
          <br />
          <span>优惠</span>
          <span>{{ v.price }}</span>
          <span>元</span>
        </el-checkbox-button>
      </el-checkbox-group>
    </el-dialog>
  </el-container>
</template>
<script>
import MenuTable2 from "@/views/MenuTable2";
import { deleteRequest, getRequest, getUUID, postRequest2 } from "../utils/api";
import { postRequest } from "../utils/api";
import ShopList from "@/views/ShopList";
export default {
  mounted: function () {
    for (var i = 0; i < this.categoryIdMax; i++) {
      this.menus.push(this.menus[0]);
    }
    var _this = this;
    getRequest("/isAdmin").then((resp) => {
      if (resp.status == 200) {
        _this.isAdmin = resp.data;
      }
    });
    getRequest("/admin/category/all").then((resp) => {
      var json = resp.data;
      var arr = [];
      var arr2 = [];
      for (var i = 0; i < json.length; i++) {
        arr.push(json[i].name);
        arr2.push(json[i].id);
      }
      _this.label = arr;
      _this.states = arr2;
    });
    getRequest("/currentUserMsg").then(
      function (msg) {
        _this.usermsg = msg.data;
        console.log(msg.data.addresses[0].address);
        _this.firstaddress = msg.data.addresses[0].address;
      },
      function (msg) {}
    );
    var url = "/geo/aroundShop?state=2";
    getRequest(url).then((resp) => {
      this.shops = resp.data.list;
    });
  },
  data() {
    return {
      updatemsg: {
        username: "",
        phone: "",
        addresses: "",
      },
      shops: [],
      getTransExp: {
        name: "",
        exp: "",
        distance: "",
      },
      transportMoney: 0,

      drawer: false,
      direction: "rtl",
      msg: [],
      x: 0,
      y: 0,
      discountSum: 0,
      curlen: 0,
      vids: [],
      selectVouchers: [],
      vouchers: [],
      isVoucher: false,
      categoryIdMax: 500,
      states: [],
      label: [],
      sumTitle: [],
      menus: [{ cost: 0, price: 0, mtf: [], state: -1 }],
      centerDialogVisible: false,

      sumPrice: 0,
      sumCost: 0,

      isAdmin: false,
      //
      usermsg: {
        username: "",
        phone: "",
        addresses: "",
      },
      firstaddress: "",
      curaddress: "",
      dialogFormVisible: false,
      formLabelWidth: "120px",
    };
  },
  methods: {
    openPersonMessage() {
      this.curaddress = this.firstaddress;
      Object.assign(this.updatemsg, this.usermsg);
      this.dialogFormVisible = true;
    },
    openVoucher() {
      getRequest("/getMyVoucher").then((resp) => {
        this.vouchers = resp.data;
      });
      this.isVoucher = true;
    },
    transExp(data) {
      this.getTransExp = data;
      this.transportMoney = this.getTransExp.exp;
      this.drawer = false;
    },
    openDrawer() {
      if (this.firstaddress.length == 0) {
        this.$message({ type: "error", message: "请输入地址" });
        return false;
      }
      getRequest("/map/getSearch/" + this.firstaddress).then((resp) => {
        this.msg = resp.data.tips;

        if (this.msg == null || this.msg.length == 0) {
          this.$message({ type: "error", message: "请输入有效地址" });
          return false;
        }

        var cur = this.msg[0].location;
        if (cur == null || cur == "undefined" || cur.length == 0) {
          this.$message({ type: "error", message: "请输入更详细一些的地址" });
          return false;
        }
        console.log(cur);

        var index = cur.indexOf(",");
        this.x = cur.substring(0, index);
        this.y = cur.substring(index + 1);
        this.drawer = true;
      });
    },

    clearVoucher() {
      this.$message({ type: "success", message: "优惠券选择太多,无法免单" });
    },
    discount() {
      var flag = true;
      if (this.selectVouchers.length < this.curlen) {
        flag = false;
      }
      var sum = 0;
      var vids = [];
      this.curlen = this.selectVouchers.length;
      for (var i = 0; i < this.curlen; i++) {
        for (var j = 0; j < this.vouchers.length; j++) {
          if (this.vouchers[j].title == this.selectVouchers[i]) {
            vids.push(this.vouchers[j].vid);
            sum += this.vouchers[j].price;
            break;
          }
        }
      }
      this.vids = vids;
      this.discountSum = sum;
      if (flag)
        this.$message({ type: "success", message: "已减免" + sum + "元" });
    },
    mget(data) {
      var state = data.state;
      this.menus[state] = data;
      var sumPrice = 0;
      var sumCost = 0;
      this.sumTitle = [];
      // var title=[];
      for (var i = 0; i < this.categoryIdMax; i++) {
        for (var j = 0; j < this.menus[i].mtf.length; j++)
          this.sumTitle.push(
            this.menus[i].mtf[j][1] + "X" + this.menus[i].mtf[j][2]
          );
        sumPrice += this.menus[i].price;
        sumCost += this.menus[i].cost;
      }
      this.sumPrice = sumPrice;
      this.sumCost = sumCost;

      // this.sumTitle=title;
    },
    clearmenus() {
      for (var i = 0; i < 20; i++) {
        if (this.$refs.mclear[i] != null) this.$refs.mclear[i].deleteRow();
      }
    },
    submitMenus() {
      if (this.sumPrice < this.discountSum || this.sumPrice == 0) {
        this.$message({ type: "error", message: "请先选择菜" });
        return false;
      }
      if (this.getTransExp.name == "") {
        this.$message({ type: "error", message: "请选择门户" });
        return false;
      }
      var _this = this;
      if (
        _this.usermsg.phone.length != 11 ||
        _this.firstaddress.length == 0 ||
        _this.usermsg.username.length == 0
      ) {
        _this.$message({
          type: "error",
          message: "用户信息不得为空,请输入有效信息（电话号码为11位）",
        });
        return false;
      }
      this.$confirm("是否提交订单?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          _this.loading = true;
          deleteRequest("/deleteMyVouchers", { vids: this.vids });
          //   localhost:8081/alipay/pay?subject=23423&traceNo=12334&totalAmount=1000
          var sumPrice =
            _this.sumPrice - _this.discountSum + _this.transportMoney;

          var content = "";
          this.sumTitle.forEach((item) => {
            content += item;
            content += "+";
          });
          var cont = content.substring(0, content.length - 1);
          var i = 0;
          for (; i < _this.shops.length; i++) {
            if (_this.shops[i].name == _this.getTransExp.name) {
              break;
            }
          }

          postRequest2("/addOrder", {
            address: _this.firstaddress,
            content: cont,
            customer: _this.usermsg.username,
            price: sumPrice,
            cost: _this.sumCost,
            phone: _this.usermsg.phone,
            gid: _this.shops[i].gid,
            geoname: _this.getTransExp.name,
            distance: _this.getTransExp.distance,
          }).then(
            (resp) => {
              if (resp.status == 200) {
                var msg = resp.data.object;
                console.log(msg);
                var mesg = msg == -1 ? "下单失败" : "下单成功";
                _this.$message({ type: resp.data.status, message: mesg });
                if (resp.data.status == "success") {
                  var sub = "";
                  if (cont.length > 40) {
                    sub =
                      cont.substring(0, 40) + "...(剩下菜品请去我的订单查看)";
                  } else {
                    sub = cont;
                  }
                  var subject = sub;
                  var traceNo = msg;
                  var url =
                    "/alipay/pay?subject=" +
                    subject +
                    "&traceNo=" +
                    traceNo +
                    "&totalAmount=" +
                    sumPrice;
                  getRequest(url).then((resp) => {
                    window.open(
                      `http://localhost:8081/alipay/pay?subject=${subject}&traceNo=${traceNo}&totalAmount=${sumPrice}`
                    );
                  });
                  this.clearmenus();
                  this.discount = 0;
                  this.transportMoney = 0;
                  this.getTransExp = {
                    name: "",
                    exp: "",
                    distance: "",
                  };
                }
              } else {
                _this.$message({ type: "error", message: "提交失败!" });
              }
              _this.loading = false;
            },
            (resp) => {
              _this.loading = false;
              _this.$message({ type: "error", message: "提交失败!" });
            }
          );
        })
        .catch(() => {
          _this.$message({
            type: "info",
            message: "已取消提交",
          });
        });
    },
    updateAddress(address) {
      if (address.length == 0) {
        // 地址不得为空
        this.$message({ type: "error", message: "地址不得为空" });
        return false;
      }
      this.firstaddress = this.curaddress;
      Object.assign(this.usermsg, this.updatemsg);
      var _this = this;
      postRequest("/addOneAddress", {
        address: _this.curaddress,
      }).then((resp) => {
        if (resp.status == 200) {
          if (resp.data.status == "success") {
            this.$message({ type: "success", message: "修改成功" });
          } else {
            this.$message({ type: "error", message: "修改失败" });
          }
          this.getTransExp = {
            name: "",
            exp: "",
            distance: "",
          };
          this.dialogFormVisible = false;
        } else {
        }
      });
    },

    handleClick() {},
  },
  components: {
    menu_table: MenuTable2,
    shop_list: ShopList,
  },
};
</script>
<style>
.menu_list > .header,
.footer {
  background-color: #ececec;
  margin-top: 10px;
  padding-left: 5px;
  display: flex;
  justify-content: flex-start;
}

.article_list > .main {
  /*justify-content: flex-start;*/
  display: flex;
  flex-direction: column;
  padding-left: 0px;
  background-color: #fff;
  padding-top: 0px;
  margin-top: 8px;
}
#building {
  background: url("../assets/logo.png");
  width: 100%;
  height: 100%;
  position: fixed;
  background-size: 100% 100%;
}
</style>
