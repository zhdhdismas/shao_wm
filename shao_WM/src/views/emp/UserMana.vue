<template>
  <div v-loading="loading">
    <el-dialog title="添加为公司员工" :visible.sync="dialogVisible2">
      <div style="float: left; font-size: 15px; font-weight: bolder">
        姓名:<span style="font-weight: normal"> {{ username }}</span
        >&emsp; 性别:<el-select
          v-model="staff.gender"
          style="width: 100px"
          placeholder="请选择"
        >
          <el-option
            v-for="(item, index) in ['男', '女']"
            :key="index"
            :value="item"
          >
          </el-option>
        </el-select>
        &emsp; 年龄:<el-input
          v-model="staff.age"
          style="width: 100px"
        ></el-input>
        &emsp; 学历:<el-select v-model="staff.education" placeholder="请选择">
          <el-option
            v-for="(item, index) in [
              '大专',
              '本科',
              '研究生',
              '高中',
              '博士',
              '初中',
              '小学',
            ]"
            :key="index"
            :value="item"
          >
          </el-option>
        </el-select>
      </div>
      <br />
      <br />
      <br />
      <div style="float: left; font-size: 15px; font-weight: bolder">
        毕业院校:<el-input
          v-model="staff.college"
          style="width: 200px"
        ></el-input>
        &emsp; 专业:<el-input
          v-model="staff.major"
          style="width: 200px"
        ></el-input>
      </div>
      <br />
      <br />
      <br />
      <div style="float: left; font-size: 15px; font-weight: bolder">
        联系地址:<el-input
          v-model="staff.address"
          style="width: 500px"
        ></el-input>
      </div>
      <br />
      <br />
      <br />
      <h3 style="float: left">
        个人职位:<el-select v-model="curcate" placeholder="请选择">
          <el-option
            v-for="(item, index) in positions"
            :key="index"
            :value="item.name"
          >
          </el-option>
        </el-select>
      </h3>
      <h3 style="float: left">
        &emsp;&emsp;个人等级:<el-select v-model="curcate2" placeholder="请选择">
          <el-option
            v-for="(item, index) in categories"
            :key="index"
            :value="item.levelName"
          >
          </el-option>
        </el-select>
      </h3>
      <br />
      <br />
      <br />
      <div style="float: left; font-size: 15px; font-weight: bolder">
        所属部门:<el-select
          filterable
          v-model="staff.department"
          placeholder="请选择"
        >
          <el-option
            v-for="(item, index) in deps"
            :key="index"
            :value="item.name"
          >
          </el-option>
        </el-select>
        &emsp;账套选择:<el-select
          filterable
          v-model="cursalary"
          placeholder="请选择"
        >
          <el-option
            v-for="(item, index) in salaries"
            :key="index"
            :value="item.name"
          >
          </el-option>
        </el-select>
      </div>
      <br />
      <br />
      <br />
      <h3 style="float: left">个人简历</h3>
      <br />
      <br />
      <el-input type="textarea" v-model="staff.description"></el-input>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible2 = false">取 消</el-button>
        <el-button type="primary" @click="addOneStaff()">确 定</el-button>
      </span>
    </el-dialog>
    <div
      style="
        margin-top: 10px;
        margin-bottom: 25px;
        display: flex;
        justify-content: center;
      "
    >
      <el-input
        placeholder="默认展示部分用户，可以通过用户名搜索用户..."
        prefix-icon="el-icon-search"
        v-model="keywords"
        style="width: 400px"
        size="small"
      >
      </el-input>
      <el-button
        type="primary"
        icon="el-icon-search"
        size="small"
        style="margin-left: 3px"
        @click="searchClick"
        >搜索
      </el-button>
    </div>
    <div style="display: flex; justify-content: space-around; flex-wrap: wrap">
      <el-card
        style="width: 330px; margin-top: 10px"
        v-for="(user, index) in users"
        :key="index"
      >
        <div slot="header" style="text-align: left">
          <span>{{ user.username }}</span>
          <el-button
            style="float: right; padding: 3px 0; color: #ff0509"
            type="text"
            icon="el-icon-delete"
            @click="deleteUser(user.uid)"
            >删除
          </el-button>
        </div>
        <div>
          <div>
            <img :src="user.avatar" style="width: 70px; height: 70px" @error="setDefaultImage" />
          </div>
          <div
            style="
              text-align: left;
              color: #20a0ff;
              font-size: 12px;
              margin-top: 13px;
            "
          >
            <span>用户名:</span><span>{{ user.username }}</span>
          </div>
          <div
            style="
              text-align: left;
              color: #20a0ff;
              font-size: 12px;
              margin-top: 13px;
            "
          >
            <span>电话:</span><span>{{ user.phone }}</span>
          </div>
          <div
            style="
              text-align: left;
              color: #20a0ff;
              font-size: 12px;
              margin-top: 13px;
            "
          >
            <span>电子邮箱:</span>
            <el-popover
              placement="top-start"
              width="68"
              trigger="click"
              style="cursor: pointer"
            >
              <span>{{ user.email }}</span>
              <span slot="reference">{{
                user.email.length > 20
                  ? user.email.substr(0, 20) + "..."
                  : user.email.substr(0, 20)
              }}</span>
            </el-popover>
          </div>
          <div
            style="
              text-align: left;
              color: #20a0ff;
              font-size: 12px;
              margin-top: 13px;
            "
          >
            <span>注册时间:</span><span>{{ formatDate(user.regTime) }} </span>
          </div>
          <div
            style="
              text-align: left;
              color: #20a0ff;
              font-size: 12px;
              margin-top: 13px;
              display: flex;
              align-items: center;
            "
          >
            <span>用户状态:</span>
            <el-switch
              v-model="user.enabled"
              active-text="启用"
              active-color="#13ce66"
              @change="enabledChange(user.enabled, user.uid, index)"
              inactive-text="禁用"
              style="font-size: 12px"
            >
            </el-switch>
          </div>
          <div
            style="
              text-align: left;
              color: #20a0ff;
              font-size: 12px;
              margin-top: 13px;
              display: flex;
              align-items: center;
            "
          >
            <span>是否为公司员工:</span>&emsp;
            <el-tag size="mini" v-show="!user.staff">否</el-tag>
            <el-tag size="mini" type="danger" v-show="user.staff">是</el-tag>
            &emsp;
            <span
              style="
                text-align: left;
                color: #20a0ff;
                font-size: 22px;
                align-items: center;
              "
              v-show="user.staff == false"
            >
              <i
                class="el-icon-circle-plus-outline"
                style="cursor: pointer"
                @click="open(user, index)"
              ></i
            ></span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>
  <script>
import { getRequest, postRequest2 } from "../../utils/api";
import { putRequest } from "../../utils/api";
import { deleteRequest } from "../../utils/api";
import moment from "moment";
import { isNotNullORBlank } from "../../utils/utils";
import defaultImage from "@/assets/R-C.jpg";
export default {
    name:"usermana",
  mounted: function () {
    this.loading = true;
    this.loadUserList();
    getRequest("/admin/level/all").then((resp) => {
      this.categories = resp.data;
    });
    getRequest("/admin/position/all").then((resp) => {
      this.positions = resp.data;
    });
    getRequest("/admin/department/allChild").then((resp) => {
      this.deps = resp.data;
    });
    getRequest("/admin/salary/all").then((resp) => {
      this.salaries = resp.data;
    });
  },
  methods: {
    setDefaultImage(e) {
      e.target.src = defaultImage;
    },
    initStaff() {
      this.staff = {
        description: "",
        gender: "",
        college: "",
        education: "",
        age: "",
        major: "",
        address: "",
        department: "",
      };
    },
    addOneStaff() {
      if (this.staff.age < 0 || this.staff.age > 100) {
        this.$message({ type: "error", message: "年龄必须合法!" });
        return;
      }
      if (
        !isNotNullORBlank(
          this.curcate,
          this.curcate2,
          this.staff.description,
          this.staff.college,
          this.staff.education,
          this.staff.major,
          this.staff.gender,
          this.staff.department,
          this.cursalary
        )
      ) {
        this.$message({ type: "error", message: "数据不能为空!" });
        return;
      }
      var i = 0;
      for (; i < this.positions.length; i++) {
        if (this.positions[i].name == this.curcate) {
          break;
        }
      }
      var pid = this.positions[i].id;
      i = 0;
      for (; i < this.categories.length; i++) {
        if (this.categories[i].levelName == this.curcate2) {
          break;
        }
      }
      var lid = this.categories[i].id;
      postRequest2("/admin/staff/addOne", {
        pid: pid,
        lid: lid,
        uid: this.uid,
        description: this.staff.description,
        college: this.staff.college,
        major: this.staff.major,
        gender: this.staff.gender,
        age: this.staff.age,
        education: this.staff.education,
        address: this.staff.address,
        department: this.staff.department,
        salary: this.cursalary,
      }).then((resp) => {
        if (resp.data.status == "success") {
          this.$message({ type: "success", message: resp.data.msg });
          this.loadOneUserById(this.uid, this.index);
          this.initStaff();
          this.cursalary = "";
          this.curcate = "";
          this.curcate2 = "";
          this.dialogVisible2 = false;
        }
      });
    },
    open(data, index) {
      this.index = index;
      this.uid = data.uid;
      this.username = data.username;

      this.dialogVisible2 = true;
    },
    // 格式化时间
    formatDate(time) {
      return moment(time).format("YYYY-MM-DD HH:mm:ss");
    },
    deleteUser(id) {
      var _this = this;
      this.$confirm("删除该用户, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          _this.loading = true;
          deleteRequest("/admin/user/" + id).then(
            (resp) => {
              if (resp.status == 200 && resp.data.status == "success") {
                _this.$message({ type: "success", message: resp.data.msg });
                _this.loadUserList();
                return;
              }
              _this.loading = false;
              _this.$message({ type: "error", message: resp.data.msg });
            },
            (resp) => {
              _this.loading = false;
              _this.$message({ type: "error", message: "删除失败!" });
            }
          );
        })
        .catch(() => {
          _this.$message({
            type: "info",
            message: "已取消删除",
          });
        });
    },
    enabledChange(enabled, id, index) {
      var _this = this;
      putRequest("/admin/user/enabled", { enabled: enabled, uid: id }).then(
        (resp) => {
          if (resp.status != 200) {
            _this.$message({ type: "error", message: "更新失败!" });
            return;
          }
          _this.$message({ type: "success", message: "更新成功!" });
        },
        (resp) => {
          _this.$message({ type: "error", message: "更新失败!" });
        }
      );
    },

    loadOneUserById(id, index) {
      var _this = this;
      getRequest("/admin/user/" + id).then((resp) => {
        if (resp.status == 200) {
          //从index位置删除1个元素插入resp.data
          _this.users.splice(index, 1, resp.data);
        } else {
          _this.$message({ type: "error", message: "数据加载失败!" });
        }
      });
    },
    loadUserList() {
      var _this = this;
      getRequest("/admin/user/all?username=" + this.keywords).then(
        (resp) => {
          _this.loading = false;
          if (resp.status == 200) {
            _this.users = resp.data;
          } else {
            _this.$message({ type: "error", message: "数据加载失败!" });
          }
        },
        (resp) => {
          _this.loading = false;
          if (resp.response.status == 403) {
            var data = resp.response.data;
            _this.$message({ type: "error", message: data });
          }
        }
      );
    },
    searchClick() {
      this.loading = true;
      this.loadUserList();
    },
  },
  data() {
    return {
      deps: [],
      username: "",
      staff: {
        description: "",
        gender: "",
        college: "",
        education: "",
        age: "",
        major: "",
        address: "",
        department: "",
      },
      index: "",
      id: "",
      uid: "",
      categories: [],
      positions: [],
      curcate: "",
      curcate2: "",
      dialogVisible2: false,
      loading: false,
      keywords: "",
      users: [],
      salaries: [],
      cursalary: "",
    };
  },
};
</script>