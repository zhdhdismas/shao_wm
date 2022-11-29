<template>
  <el-container>
    <el-header class="cate_mana_header">
      <el-input
        placeholder="请输入菜品种类名称"
        v-model="cateName"
        style="width: 200px"
      >
      </el-input>
      <el-button
        type="primary"
        size="medium"
        style="margin-left: 10px"
        @click="addNewCate"
        >新增菜品种类</el-button
      >
    </el-header>
    <el-main class="cate_mana_main">
      <el-table
        ref="multipleTable"
        :data="categories"
        tooltip-effect="dark"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        v-loading="loading"
      >
        <el-table-column type="selection" width="55" align="center">
        </el-table-column>
        <el-table-column
          label="编号"
          prop="id"
          type="index"
          width="120"
          align="center"
        >
        </el-table-column>
        <el-table-column
          label="菜品分类名称"
          prop="name"
          width="120"
          align="center"
        >
        </el-table-column>
        <el-table-column prop="createTime" label="启用时间" align="center">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="left">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleEdit(scope.$index, scope.row)"
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
      <el-button
        type="danger"
        :disabled="this.selItems.length == 0"
        style="margin-top: 10px; width: 100px; float: left"
        @click="deleteAll"
        v-if="this.categories.length > 0"
        >批量删除
      </el-button>
    </el-main>
  </el-container>
</template>
<script>
import { postRequest } from "../utils/api";
import { putRequest } from "../utils/api";
import { deleteRequest } from "../utils/api";
import { getRequest } from "../utils/api";
import moment from "moment";

export default {
  mounted() {
    this.loading = true;
    this.refresh();
    var _this = this;
    getRequest("/isAdmin").then((resp) => {
      if (resp.status == 200) {
        _this.isAdmin = resp.data;
      }
    });
  },
  methods: {
    // 格式化时间
    formatDate(time) {
      return moment(time).format("YYYY-MM-DD HH:mm:ss");
    },
    addNewCate() {
      this.loading = true;
      var _this = this;
      if (this.categories.length >= this.maxLimitCategory) {
        this.$message({ type: "error", message: "菜品种类已达上限" });
        _this.loading = false;
        return false;
      }
      if (
        this.cateName.length == 0 ||
        "        ".indexOf(this.cateName) != -1
      ) {
        this.$message({ type: "error", message: "菜品种类名不得为空" });
        _this.loading = false;
        return false;
      } else if (this.cateName.length >= 6) {
        this.$message({ type: "error", message: "菜品种类名长度不得超过5个" });
        _this.loading = false;
        return false;
      }else if(!_this.isAdmin){
            this.$message({type:"error",message:"权限不足,请联系管理员!!!"});
            _this.loading = false;
            return ;
        }
      postRequest("/admin/category/add", { name: this.cateName }).then(
        (resp) => {
          if (resp.status == 200) {
            var json = resp.data;
            _this.$message({ type: json.status, message: json.msg });
            _this.cateName = "";
            _this.refresh();
          }
          _this.loading = false;
        } 
      );
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
          for (var i = 0; i < selItems.length; i++) {
            ids += selItems[i].id + ",";
          }
          _this.deleteCate(ids.substring(0, ids.length - 1));
        })
        .catch(() => {
          //取消
          _this.loading = false;
        });
    },
    handleSelectionChange(val) {
      this.selItems = val;
    },
    handleEdit(index, row) {
      var _this = this;
      this.$prompt("请输入新名称", "编辑", {
        confirmButtonText: "更新",
        inputValue: row.name,
        cancelButtonText: "取消",
      }).then(({ value }) => {
        //value就是输入值
        if (value == null || value.length == 0) {
          _this.$message({
            type: "info",
            message: "数据不能为空!",
          });
        } else {
          _this.loading = true;
          if(!_this.isAdmin){
            this.$message({type:"error",message:"权限不足,请联系管理员!!!"});
            _this.loading = false;
            return ;
          }
          putRequest("/admin/category/update", {
            id: row.id,
            name: value,
          }).then(
            (resp) => {
              var json = resp.data;
              _this.$message({
                type: json.status,
                message: json.msg,
              });
              _this.refresh();
            },
            (resp) => {
              if (resp.response.status == 403) {
                _this.$message({
                  type: "error",
                  message: "权限不足,请联系管理员",
                });
              }
              _this.loading = false;
            }
          );
        }
      });
    },
    handleDelete(index, row) {
      let _this = this;
      this.$confirm("确认删除 " + row.name + " ?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          _this.deleteCate(row.id);
        })
        .catch(() => {
          //取消
          _this.loading = false;
        });
    },
    deleteCate(ids) {
      var _this = this;
      this.loading = true;
      //删除
      deleteRequest("/admin/category/delete/" + ids).then(
        (resp) => {
          var json = resp.data;
          _this.$message({
            type: json.status,
            message: json.msg,
          });
          _this.refresh();
        },
        (resp) => {
          _this.loading = false;
          if (resp.response.status == 403) {
            _this.$message({
              type: "error",
              message: "权限不足,请联系管理员",
            });
          } else if (resp.response.status == 500) {
            _this.$message({
              type: "error",
              message: "该栏目下尚有文章，删除失败!",
            });
          }
        }
      );
    },
    refresh() {
      let _this = this;
      getRequest("/admin/category/all").then((resp) => {
        _this.categories = resp.data;
        _this.loading = false;
      });
    },
  },
 
  data() {
    return {
        isAdmin:false,
      maxLimitCategory: 10,
      cateName: "",
      selItems: [],
      categories: [],
      loading: false,
    };
  },
};
</script>
<style>
.cate_mana_header {
  background-color: white;
  margin-top: 20px;
  padding-left: 5px;
  display: flex;
  justify-content: flex-start;
}

.cate_mana_main {
  /*justify-content: flex-start;*/
  display: flex;
  flex-direction: column;
  padding-left: 5px;
  background-color: white;
  margin-top: 20px;
  padding-top: 10px;
}
</style>
