<template>
  <div v-show="isAdmin">
    <!-- Form -->
    <el-dialog title="新增菜品" :visible.sync="dialogFormVisible">
      <el-form :model="menu">
        <el-form-item label="菜名">
          <el-input v-model="menu.title" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="价格">
          <el-input v-model="menu.price" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="成本">
          <el-input v-model="menu.cost" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="介绍">
          <el-input v-model="menu.introduction" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="菜品类型">
          <el-select v-model="curtype" placeholder="请选择菜品类型">
            <el-option
              v-for="(item, index) in label"
              :key="index"
              :value="item"
            ></el-option>
          </el-select>
        </el-form-item>
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
        <el-button type="primary" @click="addMenu()" :disabled="flag2 == 1"
          >确 定</el-button
        >
      </div>
    </el-dialog>
    <!-- Form2 -->
    <el-dialog title="更改主页图片" :visible.sync="dialogFormVisible2">
      <br />
      <p style="text-align: left">上传图片(限加五张)</p>
      <br />
      <el-upload
        ref="elupload"
        action=""
        :limit="5"
        :on-remove="handleRemove2"
        :on-change="isDisable"
        :auto-upload="false"
        :http-request="handleupload"
        list-type="picture-card"
      >
        <i class="el-icon-plus"></i>
      </el-upload>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible2 = false">取 消</el-button>
        <el-button type="primary" @click="uploadImgMany()" :disabled="flag == 1"
          >确 定</el-button
        >
      </div>
    </el-dialog>
    <el-container class="menu_list">
      <el-main class="main">
        <el-tabs @tab-click="handleClick" type="border-card">
          <el-tab-pane :label="lab" v-for="(lab, index) in label" :key="index">
            <menu_table
              :state="states[index]"
              ref="load"
              :category="lab"
            ></menu_table>
          </el-tab-pane>
        </el-tabs>
      </el-main>
      <el-footer>
        <el-button type="danger" size="mini" @click="dialogFormVisible = true"
          >新增菜品</el-button
        >
        <el-button type="primary" size="mini" @click="dialogFormVisible2 = true"
          >更改主页图片</el-button
        >

        <br />
      </el-footer>
    </el-container>
  </div>
</template>
<script>
import MenuManage from "@/components/MenuManage";
import {
  deleteRequest,
  getRequest,
  postRequest,
  postRequest2,
} from "../utils/api";
import { uploadFileRequest } from "../utils/api";
export default {
  mounted: function () {
    var _this = this;
    getRequest("/isAdmin").then((resp) => {
      if (resp.status == 200) {
        _this.isAdmin = resp.data;
        if (_this.isAdmin == false)
          _this.$message({ type: "error", message: "您不是管理员" });
      }
    });
    getRequest("/admin/category/all").then((resp) => {
      var json = resp.data;
      for (var i = 0; i < json.length; i++) {
        this.label.push(json[i].name);
        this.states.push(json[i].id);
      }
    });
  },
  data() {
    return {
      states: [],
      label: [],
      fileList: [],
      carousels: {},
      centerDialogVisible: false,
      isAdmin: false,
      flag: 0,
      flag2: 0,
      curtype: "",
      //
      menu: {
        title: "",
        price: "",
        cost: "",
        introduction: "",
      },
      dialogFormVisible: false,
      dialogFormVisible2: false,
      imgurl: "",
      imgurls: [],
    };
  },
  methods: {
    handleChange(file, fileList) {
      var formdata = new FormData();
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

      fileList.map((item) => {
        //fileList本来就是数组，就不用转为真数组了
        formdata.append("file", item.raw);
        //将每一个文件图片都加进formdata
      });
      uploadFileRequest("/upload/img", formdata).then((res) => {
        this.imgurl = res.data.msg;
      });
    },

    addMenu() {
      var _this = this;
      var i = 0;
      for (; i < this.label.length; i++) {
        if (this.label[i] == this.curtype) {
          break;
        }
      }
      postRequest2("/menus/addOne", {
        title: _this.menu.title,
        price: _this.menu.price,
        cost: _this.menu.cost,
        introduction: _this.menu.introduction,
        tid: _this.states[i],
        location: _this.imgurl,
      }).then(
        (resp) => {
          if (resp.status == 200) {
            var data = resp.data;
            _this.$message({ type: data.status, message: data.msg });
            if (data.status == "success") {
              this.$refs.upload.clearFiles();
              this.menu = [];
              this.imgurl = "";
              this.dialogFormVisible = false;
              this.$refs.load[i].loadMenus(1, 8);
            }
          } else {
            _this.$message({ type: data.status, message: data.msg });
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

    //删除菜品
    // handleClose0(tag) {
    //   this.recai.splice(this.recai.indexOf(tag), 1);
    //   this.$refs.recaitable.deleteRow(tag);
    // },
    //获取菜品

    handleClick() {},
    // 文件列表移除文件时的钩子
    handleRemove(file, fileList) {
      // 当删除照片时，显示上传组件
      if (this.flag2 == 0)
        deleteRequest("/upload/delete", { imgUrl: this.imgurl });
      this.imgurl = "";
      this.flag2 = 0;
    },
    uploadImgMany() {
      let formdata = new FormData();
      this.$refs.elupload.submit(); // 这里是执行文件上传的函数，其实也就是获取我们要上传的文件
      if (this.fileList.length == 0) {
        this.$message({ type: "error", message: "图片不得为空" });
        return false;
      }
      this.$refs.elupload.clearFiles();
      this.fileList.forEach((item) => {
        formdata.append("file", item); //将每一个文件图片都加进formdata
      });
      formdata.append("score", 4);
      uploadFileRequest("/upload/imgMany", formdata).then((resq) => {
        this.fileList = [];
        this.dialogFormVisible2 = false;

        deleteRequest("/img/deleteAll").then((res) => {
          postRequest("/img/saveAll", { imgUrls: resq.data }).then((res) => {
            this.$message({ type: "success", message: "修改成功" });
          });
        });
      });
      this.fileList = [];
    },
    handleupload(param) {
      this.fileList.push(param.file); // 一般情况下是在这里创建FormData对象，但我们需要上传多个文件，为避免发送多次请求，因此在这里只进行文件的获取，param可以拿到文件上传的所有信息
    },

    isDisable(file) {
      var _this = this;
      if (this.flag == 1) {
        _this.$message.error("请检查已添加图片格式和大小");
        return false;
      }
      var isLt10M = file.size / 1024 / 1024 < 10;
      var s = file.name.substr(file.name.lastIndexOf(".") + 1);
      if ("jpg,jpeg,gif,png".indexOf(s) == -1) {
        _this.$message.error("请上传正确的图片格式");
        _this.flag = 1;
        return false;
      }
      if (!isLt10M) {
        _this.flag = 1;
        _this.$message.error("上传图片大小不能超过10MB哦!");
        return false;
      }
    },
    handleRemove2() {
      this.$refs.elupload.submit();
      var can = true;
      this.fileList.forEach((file) => {
        var isLt10M = file.size / 1024 / 1024 < 10;
        if (["image/jpeg", "image/png"].indexOf(file.type) == -1) {
          can = false;
        }
        if (!isLt10M) {
          can = false;
        }
      });
      if (can) this.flag = 0;
      this.fileList = [];
    },
  },

  components: {
    menu_table: MenuManage,
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
</style>
