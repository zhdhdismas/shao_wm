<template>
  <el-container>
    <el-dialog title="修改公司员工信息" :visible.sync="dialogVisible">
      <div style="float: left; font-size: 15px; font-weight: bolder">
        姓名:<span style="font-weight: normal"> {{ username }}</span
        >&emsp; 性别:<el-select
          v-model="staffOne.gender"
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
          v-model="staffOne.age"
          style="width: 100px"
        ></el-input>
        &emsp; 学历:<el-select
          v-model="staffOne.education"
          placeholder="请选择"
        >
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
          v-model="staffOne.college"
          style="width: 200px"
        ></el-input>
        &emsp; 专业:<el-input
          v-model="staffOne.major"
          style="width: 200px"
        ></el-input>
      </div>
      <br />
      <br />
      <br />
      <div style="float: left; font-size: 15px; font-weight: bolder">
        联系地址:<el-input
          v-model="staffOne.address"
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
          v-model="staffOne.department"
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
      <el-input type="textarea" v-model="staffOne.description"></el-input>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="updateStaff()">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="个人简历" :visible.sync="dialogVisible2">
      {{ personMsg }}
    </el-dialog>
    <el-main class="cate_mana_main">
      <div
        style="
          display: flex;
          justify-content: space-between;
          margin-bottom: 20px;
        "
      >
        <div>
          <el-input
            prefix-icon="el-icon-search"
            placeholder="请输入员工名进行搜索..."
            v-model="keywords"
            @keydown.enter.native="searchClick"
            clearable
            @clear="searchClick"
            style="width: 300px; margin-right: 10px"
          ></el-input>
          <el-button type="primary" icon="el-icon-search" @click="searchClick"
            >搜索
          </el-button>
        </div>
        <div>
          <el-upload
            action="/admin/staff/import"
            :before-upload="beforeUpload"
            :on-success="onSuccess"
            :on-error="onError"
            :disabled="importDataDisabled"
            style="display: inline-flex; margin-right: 8px"
            :show-file-list="false"
          >
            <el-button
              :disabled="importDataDisabled"
              type="success"
              :icon="importDataBtnIcon"
            >
              {{ importDataBtnText }}
            </el-button>
          </el-upload>
          <el-button type="success" @click="exportData" icon="el-icon-download">
            导出数据
          </el-button>
        </div>
      </div>
      <el-table
        ref="multipleTable"
        :data="staff"
        tooltip-effect="dark"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        v-loading="loading"
        :border="true"
        :stripe="true"
      >
        <el-table-column type="selection" width="55" align="center">
        </el-table-column>
        <el-table-column
          label="编号"
          prop="id"
          type="index"
          :index="hIndex"
          width="50"
          align="center"
          fixed="left"
        >
        </el-table-column>

        <el-table-column prop="username" label="姓名" width="120" fixed="left">
          <template slot-scope="scope">
            <el-popover
              placement="top-start"
              width="68"
              trigger="click"
              style="cursor: pointer"
            >
              <span>{{ scope.row.username }}</span>
              <span slot="reference">{{
                scope.row.username.length > 5
                  ? scope.row.username.substr(0, 5) + "..."
                  : scope.row.username.substr(0, 5)
              }}</span>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="电话" prop="phone" width="120" align="center">
        </el-table-column>
        <el-table-column prop="email" label="邮箱" width="200">
          <template slot-scope="scope">
            <el-popover
              placement="top-start"
              width="68"
              trigger="click"
              style="cursor: pointer"
            >
              <span>{{ scope.row.email }}</span>
              <span slot="reference">{{
                scope.row.email.length > 20
                  ? scope.row.email.substr(0, 20) + "..."
                  : scope.row.email.substr(0, 20)
              }}</span>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="性别" prop="gender" width="80" align="center">
        </el-table-column>
        <el-table-column label="年龄" prop="age" width="80" align="center">
        </el-table-column>
        <el-table-column
          label="学历"
          prop="education"
          width="100"
          align="center"
        >
        </el-table-column>
        <el-table-column prop="address" label="联系地址" width="320">
          <template slot-scope="scope">
            <el-popover
              placement="top-start"
              width="68"
              trigger="click"
              style="cursor: pointer"
            >
              <span>{{ scope.row.address }}</span>
              <span slot="reference">{{
                scope.row.address.length > 20
                  ? scope.row.address.substr(0, 20) + "..."
                  : scope.row.address.substr(0, 20)
              }}</span>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="college" label="毕业院校" width="200">
          <template slot-scope="scope">
            <el-popover
              placement="top-start"
              width="68"
              trigger="click"
              style="cursor: pointer"
            >
              <span>{{ scope.row.college }}</span>
              <span slot="reference">{{
                scope.row.college.length > 10
                  ? scope.row.college.substr(0, 10) + "..."
                  : scope.row.college.substr(0, 10)
              }}</span>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="major" label="专业" width="200">
          <template slot-scope="scope">
            <el-popover
              placement="top-start"
              width="68"
              trigger="click"
              style="cursor: pointer"
            >
              <span>{{ scope.row.major }}</span>
              <span slot="reference">{{
                scope.row.major.length > 10
                  ? scope.row.major.substr(0, 10) + "..."
                  : scope.row.major.substr(0, 10)
              }}</span>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="职位" prop="name" width="200" align="center">
        </el-table-column>
        <el-table-column
          label="等级"
          prop="levelName"
          width="200"
          align="center"
        >
        </el-table-column>
        <el-table-column prop="department" label="所属部门" width="200">
          <template slot-scope="scope">
            <el-popover
              placement="top-start"
              width="68"
              trigger="click"
              style="cursor: pointer"
            >
              <span>{{ scope.row.department }}</span>
              <span slot="reference">{{
                scope.row.department.length > 10
                  ? scope.row.department.substr(0, 10) + "..."
                  : scope.row.department.substr(0, 10)
              }}</span>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column
          prop="description"
          label="个人简历"
          align="center"
          width="100px"
        >
          <template slot-scope="scope">
            <el-button
              type="primary"
              size="mini"
              @click="toMsg(scope.row.description)"
              >查看</el-button
            >
          </template>
        </el-table-column>
        <el-table-column
          prop="salary"
          label="工资账套"
          align="center"
          width="180px"
        >
          <template slot-scope="scope">
            <el-tag>{{ scope.row.salary }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="updateTime"
          label="上次修改时间"
          align="center"
          width="200px"
          sortable
        >
          <template slot-scope="scope">
            {{ formatDate(scope.row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="left" fixed="right" width="200px">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleEdit(scope.row)"
              >编辑
            </el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.$index, scope.row)"
              >删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <br />
      <el-pagination
        background
        :page-size="pageSize"
        layout="sizes, prev, pager, next, jumper, ->, total, slot"
        :total="totalCount"
        @current-change="currentChange"
        @size-change="sizeChange"
        v-show="staff.length > 0"
      >
      </el-pagination>

      <el-button
        type="danger"
        :disabled="this.selItems.length == 0"
        style="margin-top: 10px; width: 100px; float: left"
        @click="deleteAll"
        v-if="this.staff.length > 0"
        >批量删除
      </el-button>
    </el-main>
  </el-container>
</template>
  <script>
import { putRequest } from "../../utils/api";
import { deleteRequest } from "../../utils/api";
import { getRequest } from "../../utils/api";
import { downloadRequest } from "../../utils/download";
import moment from "moment";
import { isNotNullORBlank } from "../../utils/utils";
export default {
  name: "empmana",
  mounted() {
    this.loading = true;
    this.refresh(1, this.pageSize);
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
    onError() {
      this.importDataBtnText = "导入数据";
      this.importDataBtnIcon = "el-icon-upload2";
      this.importDataDisabled = false;
    },
    onSuccess(resp) {
        if(resp.status=="error"){
            this.$message({ type: "error", message: resp.msg+",请检查文件格式,文件大小且导入列名不得为空！     " });
        }else{
            this.$message({ type: "success", message: "导入成功" });
        }
      this.importDataBtnText = "导入数据";
      this.importDataBtnIcon = "el-icon-upload2";
      this.importDataDisabled = false;
      this.refresh(this.currentPage, this.pageSize);
    },
    beforeUpload() {
      this.importDataBtnText = "正在导入";
      this.importDataBtnIcon = "el-icon-loading";
      this.importDataDisabled = true;
    },
    exportData() {
      downloadRequest("/admin/staff/export");
    },
    updateStaff() {
      console.log(
        this.curcate,
        this.curcate2,
        this.staffOne.description,
        this.staffOne.college,
        this.staffOne.education,
        this.staffOne.major,
        this.staffOne.gender
      );
      if (
        !isNotNullORBlank(
          this.curcate,
          this.curcate2,
          this.staffOne.description,
          this.staffOne.college,
          this.staffOne.education,
          this.staffOne.major,
          this.staffOne.gender,
          this.staffOne.department,
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
      putRequest("/admin/staff/updateThisStaff", {
        id: this.staffOne.id,
        uid: this.staffOne.uid,
        pid: pid,
        lid: lid,
        description: this.staffOne.description,
        college: this.staffOne.college,
        major: this.staffOne.major,
        gender: this.staffOne.gender,
        age: this.staffOne.age,
        education: this.staffOne.education,
        department: this.staffOne.department,
        salary: this.cursalary,
      }).then((resp) => {
        if (resp.data.status == "success") {
          this.$message({ type: "success", message: resp.data.msg });
          this.dialogVisible = false;
          this.refresh(this.currentPage, this.pageSize);
        }
      });
    },
    toMsg(data) {
      this.dialogVisible2 = true;
      this.personMsg = data;
    },
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.loading = true;
      this.refresh(currentPage, this.pageSize);
    },
    sizeChange(pageSize) {
      this.pageSize = pageSize;
      this.loading = true;
      this.refresh(this.currentPage, this.pageSize);
    },
    // 格式化时间
    formatDate(time) {
      return moment(time).format("YYYY-MM-DD HH:mm:ss");
    },

    deleteAll() {
      var _this = this;
      this.$confirm("确认删除这 " + this.selItems.length + " 条数据?", "提示", {
        type: "warning",
        confirmButtonText: "确定",
        cancelButtonText: "取消",
      })
        .then(() => {
          var selItems = _this.selItems;
          var ids = "";
          var uids = "";
          for (var i = 0; i < selItems.length; i++) {
            ids += selItems[i].id + ",";
            uids += selItems[i].uid + ",";
          }
          _this.deleteCate(
            ids.substring(0, ids.length - 1),
            uids.substring(0, uids.length - 1)
          );
        })
        .catch(() => {
          //取消
          _this.loading = false;
        });
    },
    handleSelectionChange(val) {
      this.selItems = val;
    },
    handleEdit(data) {
      this.cursalary = data.salary;
      this.username = data.username;
      this.curcate = data.name;
      this.curcate2 = data.levelName;
      Object.assign(this.staffOne, data);
      this.dialogVisible = true;
    },
    handleDelete(index, row) {
      let _this = this;
      this.$confirm("确认删除 " + row.username + " 这个员工吗 ?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          _this.deleteCate(row.id, row.uid);
        })
        .catch(() => {
          //取消
          _this.loading = false;
        });
    },
    deleteCate(ids, uids) {
      var _this = this;
      this.loading = true;
      //删除
      deleteRequest("/admin/staff/delete/" + ids + "/" + uids).then((resp) => {
        var json = resp.data;
        _this.$message({
          type: json.status,
          message: json.msg,
        });
        _this.refresh(_this.currentPage, _this.pageSize);
      });
    },
    refresh(page, count) {
      this.loading = true;
      var _this = this;
      var url =
        "/admin/staff/all?username=" +
        this.keywords +
        "&page=" +
        page +
        "&count=" +
        count;
      getRequest(url)
        .then(
          (resp) => {
            _this.loading = false;
            if (resp.status == 200) {
              _this.staff = resp.data.staff;
              _this.totalCount = resp.data.totalCount;
            }
          },
          (resp) => {
            _this.loading = false;
            if (resp.response.status == 403) {
              _this.$message({ type: "error", message: resp.response.data });
            }
          }
        )
        .catch((resp) => {
          //压根没见到服务器
          _this.loading = false;
        });
    },
    searchClick() {
      this.loading = true;
      this.currentPage = 1;
      this.refresh(1, this.pageSize);
    },
  },
  computed: {
    hIndex() {
      // 当前页数 - 1 * 每页数据条数 + 1
      return (this.currentPage - 1) * this.pageSize + 1;
    },
  },

  data() {
    return {
      importDataBtnText: "导入数据",
      importDataBtnIcon: "el-icon-upload2",
      importDataDisabled: false,
      deps: "",
      username: "",
      positions: [],
      curcate: "",
      curcate2: "",
      categories: [],
      staffOne: {
        description: "",
        gender: "",
        college: "",
        education: "",
        age: "",
        major: "",
        address: "",
        department: "",
      },
      updatePersonMsg: "",
      personMsg: "",
      dialogVisible: false,
      dialogVisible2: false,
      isAdmin: false,
      selItems: [],
      loading: false,
      currentPage: 1,
      totalCount: -1,
      pageSize: 10,
      keywords: "",
      staff: [],
      salaries: [],
      cursalary: "",
    };
  },
};
</script>
  <style scoped>
.cate_mana_main {
  /*justify-content: flex-start;*/
  display: flex;
  flex-direction: column;
  padding-left: 5px;
  background-color: white;
  margin-top: 5px;
  padding-top: 10px;
}
</style>
  