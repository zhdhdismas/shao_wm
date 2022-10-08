<template>
  <el-form
    :model="ruleForm"
    status-icon
    :rules="rules"
    ref="ruleForm"
    label-width="100px"
    class="demo-ruleForm"
  >
    <span style="margin-left: 100px; font-size: 25px">注册账号</span>
    <br />
    <br />
    <el-form-item label="用户名" prop="username">
      <el-input v-model="ruleForm.username"></el-input>
    </el-form-item>
    <el-form-item label="电话" prop="phone">
      <el-input v-model="ruleForm.phone"></el-input>
    </el-form-item>
    <el-form-item label="密码"  prop="password">
      <el-input
        type="password"
        v-model="ruleForm.password"
      ></el-input>
    </el-form-item>
    <el-form-item :model="ruleForm" label="确认密码" prop="checkPass">
      <el-input
        type="password"
        v-model="ruleForm.checkPass"
        autocomplete="off"
      ></el-input>
    </el-form-item>
    <el-form-item :model="ruleForm" label="邮箱" prop="email">
      <el-input v-model="ruleForm.email"></el-input>
    </el-form-item>
    <el-radio-group v-model="radio" style="margin-left: 100px">
      <el-radio :label="0">用户</el-radio>
      <el-radio :label="1">骑手</el-radio>
      <el-radio :label="2">管理</el-radio>
    </el-radio-group>
    <br />
    <br />

    <el-form-item>
      <el-button type="primary" @click="submitForm('ruleForm')">提交</el-button>
      <el-button @click="resetForm('ruleForm')">重置</el-button>
      <el-button @click="goBack">返回</el-button>
    </el-form-item>
  </el-form>
</template>
<script>
import { postRequest } from "../utils/api";
export default {
  data () {
    var validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else {
        if (this.ruleForm.checkPass !== '') {
          this.$refs.ruleForm.validateField('checkPass');
        }
        callback();
      }
    };
    var validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.ruleForm.password) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      ruleForm: {
        password: '',
        username: '',
        phone: '',
        email: '',
      },
      radio: 0,
      rules: {
        email: [
          { required: true, message: '请填写邮箱', trigger: 'blur' },
          {
            type: 'string',
            message: '邮箱格式不正确',
            trigger: 'blur',
            transform (value) {
              if (!/^\w+((-\w+)|(\.\w+))*@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/.test(value)) {
                return true
              } else {
              }
            }
          },
          { type: 'string', message: '长度不能超过30位', trigger: 'blur', max: 30 }
        ],
        phone: [
          { required: true, message: '请输入电话', trigger: 'blur' },
          { min: 11, max: 11, message: '长度在11位', trigger: 'blur' }
        ],
        // 130 7183 8571

        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 15, message: '长度在 3 到 15 个字符', trigger: 'blur' }
        ],
        pass: [
          { validator: validatePass, trigger: 'blur' }
        ],
        checkPass: [
          { validator: validatePass2, trigger: 'blur' }
        ],

      }
    };
  },
  methods: {

    submitForm (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          var _this = this;
          postRequest('/reg', {
            username: this.ruleForm.username,
            password: this.ruleForm.password,
            phone: this.ruleForm.phone,
            email: this.ruleForm.email,
            roleName: this.radio
          }).then(resp => {
            if (resp.status == 200) {
              if (resp.data.status == 'success') {
                _this.$alert(resp.data.msg, '成功');
                _this.$router.replace({ path: '/' });
              } else {
                _this.$alert(resp.data.msg, '失败');
              }
            } else {
              _this.$alert('登陆失败', '失败');
            }
          })
        } else {
          _this.$alert('找不到服务器！！！', '失败')
          return false;
        }
      });
    },
    resetForm (formName) {
      this.$refs[formName].resetFields();
    },
    goBack () {
      // this.$router.replace({ path: '/home' });
      this.$router.replace({ path: '/' });
    }
  },



}

</script>
<style scoped>
</style>