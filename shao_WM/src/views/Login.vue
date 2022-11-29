<template>
  <el-form
    class="login-container"
    label-position="left"
    label-width="0px"
    v-loading="loading"
    style="margin-top:20px;margin-left:20px"
  >
    <h3 class="login_title">密码登录</h3>
    <el-form-item prop="account">
      <el-input
        type="text"
        v-model="loginForm.username"
        auto-complete="off"
        placeholder="用户名"
      ></el-input>
    </el-form-item>
    <el-form-item prop="checkPass">
      <el-input
        type="password"
        v-model="loginForm.password"
        auto-complete="off"
        placeholder="密码"
      ></el-input>
    </el-form-item>
    <el-checkbox
      v-model="checked"
      name="remember-me"
      style="color: #a0a0a0; margin: 0 0 20px 0"
      >记住密码</el-checkbox
    >
    <Vcode :show="isShow" @success="success" @close="close" />
    <el-form-item style="width: 100%">
      <el-button
        type="primary"
        @click.native.prevent="submit"
        style="width: 100%"
        >登录</el-button
      >
    </el-form-item>
    <router-link to="/re">
      <el-button type="success" style="width: 100%">注册</el-button>
    </router-link>
  </el-form>
</template>

<script>
import { postRequest } from "../utils/api";
import Vcode from "vue-puzzle-vcode";
const Base64 = require("js-base64").Base64; //引入加密方式
export default {
  components: {
    Vcode,
  },

  data() {
    return {
      isShow: false,
      mici: 5,
      salt: "shz",
        id:0,
      checked: true,
      loginForm: {
        username: "",
        password: "",
      },
      loading: false,
    };
  },
  mounted() {
    
    this.account(); //获取cookie的方法
  },
 
  methods: {
    submit() {
        this.isShow = true;
    },
    success(msg) {
      this.isShow = false; // 通过验证后，需要手动隐藏模态框
      this.submitClick();
    },
    // 用户点击遮罩层，应该关闭模态框
    close() {
      this.isShow = false;
    },
    account() {
      if (document.cookie.length >= 0) {
        this.loginForm.username = this.getCookie("username");
        this.loginForm.password = this.getCookie("password");
      }
    },
    setCookie(c_name, c_pwd, exdate) {
      //账号，密码 ，过期的天数
      var currentDate = new Date();
      currentDate.setTime(currentDate.getTime() + 24 * 60 * 60 * 1000 * exdate); //保存的天数
      document.cookie =
        "username=" + c_name + ";path=/;expires=" + currentDate.toGMTString();
      document.cookie =
        "password=" + c_pwd + ";path=/;expires=" + currentDate.toGMTString();
    },
    getCookie(name) {
      var arr = document.cookie.split(";");
      //["_ga=GA1.1.1756734561.1561034020", " mobile=123" password=456"]
      for (var i = 0; i < arr.length; i++) {
        var arr2 = arr[i].split("="); // ["_ga", "GA1.1.1756734561.1561034020"]
        if (arr2[0].trim() == name) {
          var decode = arr2[1];
          for (var j = 0; j < this.mici; j++) {
            decode = Base64.decode(decode);
          }
          var index = decode.indexOf("|");
          return decode.substring(0, index);
        }
      }
    },
    clearCookie() {
      this.setCookie("", "", -1); //清除cookie
    },

    submitClick() {
      var _this = this;
      this.loading = true;
      if (_this.checked == true) {
        //存入cookie
        let username = _this.loginForm.username + "|" + this.salt;
        let password = _this.loginForm.password + "|" + this.salt;
        for (let i = 0; i < this.mici; i++) {
          username = Base64.encode(username);
          password = Base64.encode(password);
        }

        _this.setCookie(username, password, 7); //保存7天
      } else {
        _this.clearCookie();
      }
      postRequest("/login", {
        username: this.loginForm.username,
        password: this.loginForm.password,
        rememberMe: this.checked,
      }).then(
        (resp) => {
          _this.loading = false;
          if (resp.status == 200) {
            // sessionStorage.setItem("token", resp.data.token);
            localStorage.setItem("token",resp.data.token);
            //成功
            var json = resp.data;
            if (json.status == "success") {
              _this.$router.replace({ path: "/home" });
            } else {
              _this.$alert("用户名密码错误", "失败!");
            }
          } else {
            //失败
            _this.$alert("登录失败!", "失败!");
          }
        },
        (resp) => {
          _this.loading = false;
          _this.$alert("找不到服务器⊙﹏⊙∥!", "失败!");
        }
      );
    },
  },
};
</script>
<style>
.login-container {
  border-radius: 15px;
  background-clip: padding-box;
  margin: 180px auto;
  width: 350px;
  padding: 35px 35px 15px 35px;
  background: #fff;
  border: 1px solid #eaeaea;
  box-shadow: 0 0 25px #cac6c6;
}

.login_title {
  margin: 0px auto 40px auto;
  text-align: center;
  color: #505458;
}

.login_remember {
  margin: 0px 0px 35px 0px;
  text-align: left;
}
</style>
