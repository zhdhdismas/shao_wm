<template>
  <el-form
    class="login-container"
    label-position="left"
    label-width="0px"
    :model="loginForm"
    :rules="rules"
    ref="loginForm"
    v-loading="loading"
    style="margin-top:20px;margin-left:20px"
  >
    <h3 class="login_title">邮箱登录</h3>
    <el-form-item prop="email">
      <el-input
        type="text"
        v-model="loginForm.email"
        auto-complete="off"
        placeholder="邮箱"
      ></el-input>
    </el-form-item>
    <el-form-item prop="code">
      <span>
        <el-input
          type="text"
          v-model="loginForm.code"
          auto-complete="off"
          placeholder="验证码"
          style="width: 130px"
        ></el-input>
      </span>
      &emsp;
      <span>
        <el-button :disabled="time > 0" @click="btnClick">
          {{ time > 0 ? `${time}秒后可重新发送` : "点击发送" }}
        </el-button>
      </span>
    </el-form-item>
    <el-dialog title="邮箱登录注意事项" :visible.sync="dialogVisible" :append-to-body="true">
      <p>1. 如果此邮箱未使用密码注册过,将为您自动注册一个账号,您可以通过主页的个人信息修改您的电话号码或用户名等个人信息</p>
        <p>2. 如果此邮箱已经使用密码注册过,您邮箱登录的账号就为此账号,您仍能用密码登录</p>
        <p>3. 为了保证您的账号安全,验证码默认5分钟过期,请不要将验证码泄露给他人</p>
        <p>4. 由于服务器成本,您在1分钟以内肯定会收到验证码,请耐心等待</p>
    </el-dialog>
    <el-checkbox v-model="checked">已阅读注意事项</el-checkbox
    >&emsp;<span @click="dialogVisible = true" style="color:blue;text-decoration: underline;">详情</span>
    <Vcode :show="isShow" @success="success" @close="close" />
     
    <el-form-item style="width: 100%;margin-top:12px">
      <el-button
        type="primary"
        @click.native.prevent="submit"
        style="width: 100%"
        >登录</el-button
      >
    </el-form-item>
  </el-form>
</template>
  
  <script>
import { postRequest } from "../utils/api";
import Vcode from "vue-puzzle-vcode";
export default {
  components: {
    Vcode,
  },

  data() {
    return {
      dialogVisible: false,
      isShow: false,
      time: 0,
      why: "",
      checked: false,
      loginForm: {
        email: "",
        code: "",
      },
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
      },
      loading: false,
    };
  },
  mounted() {},

  methods: {
    btnClick() {
      this.$refs["loginForm"].validate((valid) => {
        if (valid) {
          this.sendCode();
          // 设定时间
          this.time = 120;
          // 开启定时
          const interval = setInterval(() => {
            // 减号执行的位置决定if中的判断条件
            this.time--;
            // 停止定时器
            if (this.time < 0) {
              clearInterval(interval);
            }
          }, 1000);
        } else {
          return false;
        }
      });
    },
    submit() {
      if (!this.checked) {
        this.$message({ type: "error", message: "请点击已阅读注意事项" });
        return false;
      }
      this.isShow = true;
    },
    success(msg) {
      this.submitClick();
      this.isShow = false; // 通过验证后，需要手动隐藏模态框
    },
    // 用户点击遮罩层，应该关闭模态框
    close() {
      this.isShow = false;
    },
    sendCode() {
      this.$message({
        type: "success",
        message: "发送成功",
      });
      postRequest("/email/send", {
        email: this.loginForm.email,
        code: this.loginForm.code,
      }).then((resp) => {});
    },
    submitClick() {
      var _this = this;
      this.loading = true;
      this.$refs["loginForm"].validate((valid) => {
        if (valid) {
          postRequest("/email/login", {
            email: this.loginForm.email,
            code: this.loginForm.code,
          }).then(
            (resp) => {
              _this.loading = false;
              if (resp.status == 200) {
                //成功
                var json = resp.data;
                if (json.status == "success") {
                  _this.$router.replace({ path: "/home" });
                  sessionStorage.setItem("token", json.msg);
                } else {
                  _this.$alert(resp.data.msg, "失败!");
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
        } else {
          this.loading = false;
          return false;
        }
      });
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
  