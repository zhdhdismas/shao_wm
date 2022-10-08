<template>
  <!--    添加对话框-->
  <el-dialog title="个人信息" :visible.sync="dialogVisible" width="30%">
    <el-row>
      <el-col :span="24">
        <div style="text-align: left">
          <el-button
            type="text"
            icon="el-icon-back"
            @click="goBack"
            style="padding-bottom: 0px"
            >返回</el-button
          >
        </div>
      </el-col>
      <img :src="this.$route.query.img" />
    </el-row>
    <br />
    <!--        添加对话框信息-->
    <el-form
      :model="ruleForm"
      status-icon
      :rules="rules"
      ref="ruleForm"
      label-width="100px"
      class="demo-ruleForm"
    >
      <el-form-item label="用户名" prop="username">
        <el-input v-model="ruleForm.username"></el-input>
      </el-form-item>
      <el-form-item label="电话号码" prop="phone">
        <el-input v-model="ruleForm.phone"></el-input>
      </el-form-item>
      <el-form-item label="原密码" prop="password" v-show="isShow">
        <el-input type="password" v-model="ruleForm.password"></el-input>
      </el-form-item>
      <el-form-item label="新密码" prop="curpassword" v-show="isShow">
        <el-input
          type="password"
          v-model="ruleForm.curpassword"
          autocomplete="off"
        ></el-input>
      </el-form-item>

      <el-form-item :model="ruleForm" label="邮箱" prop="email" v-show="isShow">
        <el-input v-model="ruleForm.email"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitForm('ruleForm')"
          >修改</el-button
        >
        <el-button @click="goBack">取消</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>
<script>
import { getRequest } from "../utils/api";
import { putRequest } from "../utils/api";
export default {
  data() {
    return {
      isShow: false,
      ruleForm: {
        username: "",
        password: "",
        curpassword: "",
        phone: "",
        email: "",
      },
      dialogVisible: true,
      rules: {
        email: [
          { required: true, message: "请填写邮箱", trigger: "blur" },
          {
            type: "string",
            message: "邮箱格式不正确",
            trigger: "blur",
            transform(value) {
              if (
                !/^\w+((-\w+)|(\.\w+))*@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/.test(
                  value
                )
              ) {
                return true;
              } else {
              }
            },
          },
          {
            type: "string",
            message: "长度不能超过30位",
            trigger: "blur",
            max: 30,
          },
        ],
        phone: [
          { required: true, message: "请输入电话号码", trigger: "blur" },
          { min: 11, max: 11, message: "长度为11个字符", trigger: "blur" },
        ],
        password: [
          { required: false, message: "请输入原密码", trigger: "blur" },
          { min: 3, max: 11, message: "长度为3到11个字符", trigger: "blur" },
        ],
        curpassword: [
          { required: false, message: "请输入新密码", trigger: "blur" },
          { min: 3, max: 11, message: "长度为3到11个字符", trigger: "blur" },
        ],
        username: [
          { required: true, message: "请输入用户名", trigger: "blur" },
          {
            min: 3,
            max: 15,
            message: "长度在 3 到 15 个字符",
            trigger: "blur",
          },
        ],
      },
    };
  },
  mounted() {
    this.selectUserMsg();
    getRequest("/isExistPassword").then((resp) => {
      this.isShow = resp.data;
    });
  },
  methods: {
    selectUserMsg: function () {
      var _this = this;
      getRequest("/currentUserMsg").then((resp) => {
        _this.ruleForm = resp.data;
      }),
        (resp) => {
          if (resp.response.status == 403) {
            _this.$message({
              type: "error",
              message: resp.response.data,
            });
          }
        };
    },
    submitForm(formName) {
      console.log(this.ruleForm.curpassword);
        if(this.ruleForm.curpassword==undefined||this.ruleForm.password==undefined){
            this.$message({type:"error",message:"密码不得为空"});
            return false;
        }
      var _this = this;
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (_this.isShow) {
            putRequest("/updateMsg", {
              username: _this.ruleForm.username,
              password: _this.ruleForm.password,
              curpassword: _this.ruleForm.curpassword,
              phone: _this.ruleForm.phone,
              email: _this.ruleForm.email,
            }).then((resp) => {
              if (resp.status == 200) {
                if (resp.data.status == "success") {
                  _this.$alert(resp.data.msg, "修改成功");
                  getRequest("/logout");
                  _this.currentUserName = "游客";
                  _this.$router.replace({ path: "/" });
                } else {
                  _this.$alert(resp.data.msg, "失败");
                }
              } else {
                _this.$alert("找不到服务器！！！", "失败");
              }
            });
          }else{
            putRequest("/updateMsg", {
              username: _this.ruleForm.username,
              phone: _this.ruleForm.phone,
              email: _this.ruleForm.email,
            }).then((resp) => {
              if (resp.status == 200) {
                if (resp.data.status == "success") {
                  _this.$alert(resp.data.msg, "修改成功");
                  getRequest("/logout");
                  _this.currentUserName = "游客";
                  _this.$router.replace({ path: "/" });
                } else {
                  _this.$alert(resp.data.msg, "失败");
                }
              } else {
                _this.$alert("找不到服务器！！！", "失败");
              }
            });
          }
        } else {
          return false;
        }
      });
    },
    goBack() {
      // this.$router.replace({ path: '/home' });
      this.$router.go(-1);
    },
  },
};
</script>
 