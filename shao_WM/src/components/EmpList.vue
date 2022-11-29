<template>
  <div v-show="isAdmin">
    <el-container>
      <el-main style="width: 100px">
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane label="部门管理" name="depmana">
            <DepartmentManaVue v-if="states[0]==1"></DepartmentManaVue>
          </el-tab-pane>
          <el-tab-pane label="员工管理" name="empmana">
            <EmployeeManaVue v-if="states[1]==1"></EmployeeManaVue>
          </el-tab-pane>
          <el-tab-pane label="职位管理" name="posmana">
            <PositionManaVue v-if="states[2]==1"></PositionManaVue>
          </el-tab-pane>
          <el-tab-pane label="等级管理" name="levelmana">
            <LevelManaVue v-if="states[3]==1"></LevelManaVue>
          </el-tab-pane>
          <el-tab-pane label="账套管理" name="salarymana">
            <SalaryManaVue v-if="states[4]==1"></SalaryManaVue>
          </el-tab-pane>
          <el-tab-pane label="用户管理" name="usermana">
            <UserManaVue v-if="states[5]==1"></UserManaVue>
          </el-tab-pane>
        </el-tabs>
      </el-main>
    </el-container>
  </div>
</template>
<script>
import { getRequest } from "../utils/api";
import DepartmentManaVue from "@/views/emp/DepartmentMana.vue";
import EmployeeManaVue from "@/views/emp/EmployeeMana.vue";
import SalaryManaVue from "@/views/emp/SalaryMana.vue";
import UserManaVue from "@/views/emp/UserMana.vue";
import PositionManaVue from "@/views/emp/PositionMana.vue";
import LevelManaVue from "@/views/emp/LevelMana.vue";
export default {
  mounted() {
    var _this = this;
    this.loading = true;
    getRequest("/isAdmin").then((resp) => {
      if (resp.status == 200) {
        _this.isAdmin = resp.data;
        if (_this.isAdmin == false)
          _this.$message({ type: "error", message: "权限不足,请联系管理员" });
      }
    });
  },
  data() {
    
    return {
        states:[1,0,0,0,0,0],
        titles:['depmana','empmana','posmana','levelmana','salarymana','usermana'],
        labels:['部门管理','员工管理','职位管理','等级管理','账套管理','用户管理'],
        activeName: 'depmana',
        isAdmin: false,
    };
  },
  methods: {
    handleClick(tab) {
      for(var i=0;i<this.labels.length;i++){
        if(tab.label==this.labels[i]){
            for(var j=0;j<this.states.length;j++){
                this.states[j]=0;
            }
            this.states[i]=1;
            break;
        }
      }
    }

  },
  components: {
    LevelManaVue,
    PositionManaVue,
    DepartmentManaVue,
    EmployeeManaVue,
    SalaryManaVue,
    UserManaVue,
  },
};
</script>