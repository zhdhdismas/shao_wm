
<template>
  <div>
    <el-header class="cate_mana_header">
      <el-button
        type="primary"
        @click="dialogFormVisible = true"
        style="margin-left: 10px"
        v-show="this.isAdmin"
        >新增优惠券</el-button
      >
    </el-header>
    <div style="display: flex; justify-content: space-around; flex-wrap: wrap">
      <el-card
        :body-style="{ padding: '0px' }"
        :span="8"
        v-for="(o, index) in vouchers"
        :key="index"
        style="width: 330px; margin-top: 10px"
      >
        <img :src="o.imgUrl" class="image" />
        <div style="padding: 14px">
          <span style="font-weight: bold">{{ o.title }}</span>
          <p>{{ o.description }}</p>
          <el-col :span="24" style="color: red"
            >开始抢购时间: {{ formatDate(o.createTime) }}</el-col
          >
          <el-col :span="24" style="color: red"
            >结束抢购时间: {{ formatDate(o.endTime) }}</el-col
          >
          <el-row>
            <el-col :span="4" style="margin-left: 90px">还剩</el-col>
            <el-col :span="2" style="color: crimson">{{ o.stock }}</el-col>
            <el-col :span="2">张</el-col>
            &emsp;
          </el-row>
          <el-row>
            <el-col :span="8" style="margin-left: 90px">优惠券金额</el-col>
            <el-col :span="2" style="color: red">{{ o.price }}</el-col>
            <el-col :span="2">元</el-col>
            &emsp;
          </el-row>
          <el-col :span="24" style="color: blueviolet"
            >使用截止到期时间: {{ formatDate(o.deadTime) }}</el-col
          >
          <br />
          <br />
          <el-button
            type="success"
            @click="seckill(o.vid)"
            :disabled="timeIn[o.vid] != 1"
            >抢购</el-button
          >

          <el-button
            type="danger"
            style="margin-left: 20px"
            v-show="isAdmin"
            @click="deleteOne(o.vid)"
            >删除优惠券</el-button
          >
          <p style="color: red" v-show="timeIn[o.vid] != 1">
            当前时间不在抢购时间段内
          </p>
        </div>
      </el-card>
    </div>
    <el-dialog title="新增优惠券" :visible.sync="dialogFormVisible">
      <el-form>
        <el-form-item label="优惠券名称">
          <el-input v-model="voucher.title" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="优惠券描述">
          <el-input v-model="voucher.description" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="发放优惠券数量" style="text-align: left">
          <el-input-number
            v-model="voucher.stock"
            :min="10"
            :max="500"
            label=""
          ></el-input-number>
        </el-form-item>
        <el-form-item label="单张优惠券金额" style="text-align: left">
          <el-input-number
            v-model="voucher.price"
            :min="1"
            :max="20"
            label="优惠券数量"
          ></el-input-number>
        </el-form-item>
        <p style="text-align: left">选择日期范围</p>
        <el-date-picker
          v-model="voucher.dates"
          type="datetimerange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd HH:mm:ss"
          :default-time="['00:00:00']"
        >
          <p style="text-align: left">选择抢购日期</p>
        </el-date-picker>
        <p style="text-align: left">选择结束使用日期</p>
        <el-date-picker
          v-model="value1"
          type="datetime"
          placeholder="选择日期时间"
        >
        </el-date-picker>
      </el-form>
      <br />
      <p style="text-align: left">上传图片(限加一张)</p>
      <br />
      <el-upload
        action=""
        ref="upload"
        :on-change="handleChange"
        :on-remove="handleRemove"
        :auto-upload="false"
        list-type="picture-card"
        :limit="1"
      >
        <i class="el-icon-plus"></i>
      </el-upload>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="addVoucher()" :disabled="flag2 == 1"
          >确 定</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { deleteRequest, getRequest, postRequest } from "../utils/api";
import { uploadFileRequest } from "../utils/api";
import moment from "moment";
export default {
  provide() {
    return {
      reload: this.reload,
    };
  },
  data() {
    return {
      isRouterAlive: true,
      isAdmin: false,
      loading: false,
      imgurl: "",
      value1: "",
      timeIn: [],
      timeStart: [],
      timeEnd: [],
      voucher: {
        dates: [],
        price: 1,
        stock: 10,
        title: "",
        description: "",
      },
      dialogFormVisible: false,
      flag2: 0,
      vouchers: "",
    };
  },
  mounted() {
    this.loadVoucher();
    window.$bus.$on("tableReload", () => {
      this.loadVoucher();
    });
    var _this = this;
    getRequest("/isAdmin").then((resp) => {
    
      if (resp.status == 200) {
        _this.isAdmin = resp.data;
      }
    });
  },
  methods: {
    reload() {
      this.$nextTick(() => {
        this.isRouterAlive = true;
      });
    },
    seckill(val) {
      console.log(new Date());
      if (
        new Date().getTime() < new Date(this.timeStart[val]).getTime() &&
        new Date().getTime() > new Date(this.timeEnd[val]).getTime()
      ) {
        this.$message({ type: "error", message: "当前时间不在抢购时间段内" });
        return false;
      }
      postRequest("/seckill/" + val).then((resp) => {
        var data = resp.data;
        if (data.status == "success") {
          this.$message({
            type: "success",
            message: data.msg + ",点击‘选择我的优惠券’查看",
          });
          this.$router.push({ path: "/MenuList", query: { isVoucher: true } });
        } else {
          this.$message({ type: "error", message: data.msg });
        }
      });
    },
    deleteOne(val) {
      var _this = this;
      this.$confirm("是否删除该优惠券", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          _this.loading = true;
          deleteRequest("/deleteVoucher", { vid: val }).then((resp) => {
            var data = resp.data;
            if (data.status == "success") {
              this.$message({ type: "success", message: "删除成功" });
              this.loadVoucher();
            } else {
              this.$message({ type: "error", message: "删除失败" });
            }
          });
        })
        .catch(() => {
          _this.$message({
            type: "info",
            message: "已取消删除",
          });
        });
    },
    loadVoucher() {
      getRequest("/allVoucher").then((resp) => {
        this.vouchers = resp.data;
        for (var i = 0; i < this.vouchers.length; i++) {
          var a = this.vouchers[i].createTime;
          var b = this.vouchers[i].endTime;
          this.timeStart[this.vouchers[i].vid] = a;
          this.timeEnd[this.vouchers[i].vid] = b;
          if (
            new Date().getTime() >= new Date(a).getTime() &&
            new Date().getTime() <= new Date(b).getTime()
          ) {
            this.timeIn[this.vouchers[i].vid] = 1;
          }
        }
      });
    },
    formatDate(time) {
      return moment(time).format("YYYY-MM-DD HH:mm:ss");
    },
    addVoucher() {
      if (
        this.voucher.title == undefined ||
        this.voucher.description == undefined
      ) {
        this.$message({
          type: "error",
          message: "输入不得为空",
        });
        return false;
      }
      if (
        this.voucher.dates[0] == undefined ||
        this.voucher.dates[1] == undefined ||
        this.value1 == undefined
      ) {
        this.$message({
          type: "error",
          message: "所有日期时间不得为空",
        });
        return false;
      }
      if (
        new Date(this.voucher.dates[1]).getTime() >
        new Date(this.value1).getTime()
      ) {
        this.$message({
          type: "error",
          message: "使用日期截止时间应该在抢购日期截止时间之后",
        });
        return false;
      }
      if (this.imgurl == "") {
        this.$message({
          type: "error",
          message: "图片不得为空",
        });
        return false;
      }
      var _this = this;
      _this.loading = true;
      postRequest("/addVoucher", {
        title: _this.voucher.title,
        stock: _this.voucher.stock,
        description: _this.voucher.description,
        createTime: this.formatDate(_this.voucher.dates[0]),
        endTime: this.formatDate(_this.voucher.dates[1]),
        deadTime: this.formatDate(_this.value1),
        imgUrl: this.imgurl,
        price: this.voucher.price,
      }).then(
        (resp) => {
          if (resp.status == 200) {
            var data = resp.data;

            if (data.status == "success") {
                this.$refs.upload.clearFiles();
                _this.voucher = [];
                _this.imgurl = "";
                _this.value1 = "";
                _this.loadVoucher();
                _this.$message({ type: data.status, message: data.msg });
                this.dialogFormVisible = false;
                this.imgurl = "";

            }else {
                _this.$message({ type: data.status, message: data.msg });
            }
          } 
            _this.loading = false;
        },
        (resp) => {
          _this.loading = false;
          _this.$message({
            type: "error",
            message: "输入信息不得为空,输入信息必须合法!",
          });
        }
      );
    },
    // 文件列表移除文件时的钩子
    handleRemove(file, fileList) {
      // 当删除照片时，显示上传组件
      if (this.flag2 == 0)
        deleteRequest("/upload/delete", { imgUrl: this.imgurl });
      this.imgurl = "";
      this.flag2 = 0;
    },
    handleChange(file, fileList) {
      this.$forceUpdate();
      var formdata = new FormData();
      // let file = event.target.files[0];
      var _this = this;
      var isLt10M = file.size / 1024 / 1024 < 10;
      var i = file.name.lastIndexOf(".");
      var s = file.name.substr(i + 1);
      if ("jpg,jpeg,png".indexOf(s) == -1) {
        _this.$message.error("请上传正确的图片格式");
        _this.flag2 = 1;
        return false;
      }
      if (!isLt10M) {
        _this.flag2 = 1;
        _this.$message.error("上传图片大小不能超过10MB哦!");
        return false;
      }
      console.log(file);
      fileList.map((item) => {
        //fileList本来就是数组，就不用转为真数组了
        formdata.append("file", item.raw);
        //将每一个文件图片都加进formdata
      });
      console.log(formdata);
      uploadFileRequest("/upload/img", formdata).then((res) => {
        this.imgurl = res.data.msg;
      });
    },
  },
};
</script>

  
  <style>
.time {
  font-size: 13px;
  color: #999;
}

.bottom {
  margin-top: 13px;
  line-height: 12px;
}

.button {
  padding: 0;
  float: right;
}

.image {
  width: 100%;
  display: block;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both;
}
.cate_mana_header {
  background-color: white;
  margin-top: 20px;
  padding-left: 5px;
  display: flex;
  justify-content: flex-start;
}
</style>
  
 