<template>
  <div id="building">
    <div style="margin-top: 140px; width: auto; text-align: center">
        <div style="margin-left: 450px;">
            <el-tabs tab-position="left"   >
        <el-tab-pane label="密码登录">
          <login_table ></login_table>
        </el-tab-pane>
        <el-tab-pane label="邮件登录">
          <email_login ></email_login>
        </el-tab-pane>
      </el-tabs>
        </div>
      
    </div>
  </div>
</template>
    <script>
import ShopTable from "@/components/ShopTable";
import Login from "@/components/Login";
import EmailLogin from "@/components/EmailLogin";
export default {
  mounted: function () {
    var x = this.$route.query.over;
    if (x == 1) {
      this.$message.closeAll();
      this.$message({
        type: "error",
        message: "您的账号在别处登录了,请确保您的账户是否被盗用!!!",
      });
    }
  },
  methods: {},
  data() {
    return {};
  },

  components: {
    login_table: Login,
    email_login: EmailLogin,
  },
};
</script>
    <style scoped>
#building {
  background: url("../assets/136281719.jpg");
  background-size: cover;
  width: 100%;
  height: 100%;
  position: fixed;
  background-size: 100% 100%;
}   
</style>
    