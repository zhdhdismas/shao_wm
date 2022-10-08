<style type="text/css">
.menu_table_footer {
  display: flex;
  box-sizing: content-box;
  padding-top: 10px;
  padding-bottom: 0px;
  margin-bottom: 0px;
  justify-content: space-between;
}
</style>
<template>
  <div>
    <el-dialog title="修改菜品" :visible.sync="dialogFormVisible">
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
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="updateMenu()">确 定</el-button>
      </div>
    </el-dialog>
    <div style="display: flex; justify-content: flex-start">
      <el-input
        placeholder="通过菜品搜索该分类下的菜..."
        prefix-icon="el-icon-search"
        v-model="keywords"
        style="width: 400px"
        size="mini"
      >
      </el-input>
      <el-button
        type="primary"
        icon="el-icon-search"
        size="mini"
        style="margin-left: 3px"
        @click="searchClick"
        >搜索
      </el-button>
    </div>
    <!--<div style="width: 100%;height: 1px;background-color: #20a0ff;margin-top: 8px;margin-bottom: 0px"></div>-->
    <el-table
      ref="multipleTable"
      :data="menus"
      :row-key="getRowKey"
      tooltip-effect="dark"
      style="width: 100%; overflow-x: hidden; overflow-y: hidden"
      max-height="390"
      @selection-change="handleSelectionChange"
      v-loading="loading"
      :default-sort="{ prop: 'date', order: 'ascending' }"
    >
      <el-table-column
        type="selection"
        width="35"
        align="left"
        :reserve-selection="true"
      >
      </el-table-column>
      <el-table-column label="菜品" width="200" align="left">
        <template slot-scope="scope"
          ><span style="color: #409eff; cursor: pointer" @click="itemClick(scope.row)">{{
            scope.row.title
          }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="价格"
        width="140"
        align="left"
        sortable
        prop="price"
      >
        <template slot-scope="scope">{{ scope.row.price }}</template>
      </el-table-column>

      <el-table-column
        label="成本"
        width="140"
        align="left"
        sortable
        prop="cost"
      >
        <template slot-scope="scope">{{ scope.row.cost }}</template>
      </el-table-column>

      <el-table-column prop="img" label="图片" width="150" align="center">
        <template v-slot:default="scope">
          <el-image :src="scope.row.location" />
        </template>
      </el-table-column>

      <el-table-column
        prop="introduction"
        label="介绍"
        width="350"
        align="center"
      >
      </el-table-column>

      <el-table-column label="操作" align="center">
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

      <el-table-column label="是否有货" align="center">
        <template slot-scope="scope">
          <el-switch
            v-model="values[scope.row.mid]"
            @change="updateStatus(scope.row)"
            active-color="#13ce66"
            inactive-color="#ff4949"
          >
          </el-switch>
        </template>
      </el-table-column>
    </el-table>
    <div class="menu_table_footer">
      <el-button
        type="danger"
        size="mini"
        style="margin: 0px"
        v-show="this.menus.length > 0"
        :disabled="this.selItems.length == 0"
        @click="deleteMany"
        >批量删除
      </el-button>
      <span></span>
      <el-pagination
        background
        :page-size="pageSize"
        layout="prev, pager, next"
        :total="totalCount"
        @current-change="currentChange"
        v-show="this.menus.length > 0"
      >
      </el-pagination>
    </div>
  </div>
</template>
 

<script>
import { putRequest } from "../utils/api";
import { getRequest } from "../utils/api";
import { deleteRequest } from "../utils/api";
//  import Vue from 'vue'
//  var bus = new Vue()

export default {
  data() {
    return {
      values: [],
      categoryIdMax: 500,
      menuIdMax: 2000,
      stoLa: [],
      states: [],
      label: [],
      menus: [],
      selItems: [],
      loading: false,
      currentPage: 1,
      totalCount: -1,
      pageSize: 8,
      keywords: "",
      dustbinData: [],
      curtype: "",
      //
      menu: {
        title: "",
        price: "",
        cost: "",
        introduction: "",
        mid: "",
        location: "",
      },
      dialogFormVisible: false,

      dialogFormVisible: false,
    };
  },
  mounted: function () {
    var _this = this;
    this.loading = true;
    this.loadMenus(1, this.pageSize);
    var _this = this;

    getRequest("/admin/category/all").then((resp) => {
      var json = resp.data;

      //   for (var i = 0; i < this.categoryIdMax; i++) {
      //     this.stoLa.push("");
      //   }
      for (var i = 0; i < json.length; i++) {
        this.label.push(json[i].name);
        this.states.push(json[i].id);
        this.stoLa[json[i].id] = json[i].name;
      }
    });
  },
  methods: {
    itemClick(row) {
      this.$router.push({
        path: "/MenuDetail",
        query: { mid: row.mid, img: row.location, category: this.category },
      });
    },
    updateStatus(x) {
        putRequest("/menus/updateStatus",{
            mid:x.mid,
            status:this.values[x.mid]
        }).then(resp=>{
            if(resp.data=="success"){
                this.$message({type:"success",message:resp.data.msg});
            }
        })
    },
    updateMenu() {
      var _this = this;
      var i = 0;
      for (; i < this.label.length; i++) {
        if (this.label[i] == _this.curtype) {
          break;
        }
      }
      putRequest("/menus/updateThisMenus", {
        title: _this.menu.title,
        price: _this.menu.price,
        cost: _this.menu.cost,
        introduction: _this.menu.introduction,
        tid: _this.states[i],
        mid: _this.menu.mid,
        location: _this.menu.location,
      }).then(
        (resp) => {
          if (resp.status == 200) {
            var data = resp.data;

            if (data.status == "success") {
              // window.$bus.$emit('tableReload')
              _this.loadMenus(_this.currentPage, _this.pageSize);
              _this.dialogFormVisible = false;
              _this.$message({ type: data.status, message: data.msg });
            } else {
              _this.$message({ type: "error", message: data.msg });
            }
          } else {
            _this.$message({ type: "error", message: "输入信息不得为空" });
            this.dialogFormVisible = false;
          }
          _this.loading = false;
        },
        (resp) => {
          _this.loading = false;
          _this.$message({ type: "error", message: "输入信息不得为空!" });
        }
      );
    },
    getRowKey(row) {
      return row.mid;
    },

    deleteRow() {
      this.$refs.multipleTable.clearSelection();
    },
    searchClick() {
      this.currentPage = 1;
      this.loadMenus(1, this.pageSize);
    },

    handleSelectionChange(val) {
      this.selItems = val;
    },
    handleEdit(row) {
      this.dialogFormVisible = true;
      var _this = this;
      getRequest("/menus/getAMenu/" + row.mid)
        .then(
          (resp) => {
            _this.loading = false;
            if (resp.status == 200) {
              _this.menu = resp.data;
              _this.curtype = _this.stoLa[this.state];
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
          _this.loading = false;
        });
    },
    handleDelete(index, row) {
      var _this = this;
      this.$confirm("是否删除该类别下这个的菜品", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          _this.loading = true;
          var url = "/menus/dustbinOne";
          deleteRequest(url, { mid: row.mid }).then(
            (resp) => {
              if (resp.status == 200) {
                var data = resp.data;
                _this.$message({ type: data.status, message: data.msg });
                if (data.status == "success") {
                  // window.$bus.$emit('tableReload')//通过选项卡都重新加载数据
                  _this.loadMenus(_this.currentPage, _this.pageSize);
                }
              } else {
                _this.$message({ type: "error", message: "删除失败!" });
              }
              _this.loading = false;
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
    deleteMany() {
      var selItems = this.selItems;
      for (var i = 0; i < selItems.length; i++) {
        this.dustbinData.push(selItems[i].mid);
      }
      var _this = this;
      this.$confirm("是否删除该类别下选中的菜品", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          _this.loading = true;
          var url = "/menus/dustbin";
          deleteRequest(url, { mids: _this.dustbinData }).then(
            (resp) => {
              if (resp.status == 200) {
                var data = resp.data;
                _this.$message({ type: data.status, message: data.msg });
                if (data.status == "success") {
                  // window.$bus.$emit('tableReload')//通过选项卡都重新加载数据
                  _this.currentPage = 1;
                  _this.loadMenus(_this.currentPage, _this.pageSize);
                }
              } else {
                _this.$message({ type: "error", message: "删除失败!" });
              }
              _this.loading = false;
              _this.dustbinData = [];
            },
            (resp) => {
              _this.loading = false;
              _this.$message({ type: "error", message: "删除失败!" });
              _this.dustbinData = [];
            }
          );
        })
        .catch(() => {
          _this.$message({
            type: "info",
            message: "已取消删除",
          });
          _this.dustbinData = [];
        });
    },
    //翻页
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.loading = true;
      this.loadMenus(currentPage, this.pageSize);
    },
    loadMenus(page, count) {
      var _this = this;
      var url =
        "/menus/all?state=" +
        this.state +
        "&page=" +
        page +
        "&count=" +
        count +
        "&keywords=" +
        this.keywords;
      getRequest(url)
        .then((resp) => {
          _this.loading = false;
          if (resp.status == 200) {
            _this.menus = resp.data.menus;
            // console.log(this.historymenus);
            for (var i = 0; i < this.menus.length; i++)
              this.values[this.menus[i].mid] = this.menus[i].status;

            _this.totalCount = resp.data.totalCount;
          }
        })
        .catch((resp) => {
          //压根没见到服务器
          _this.loading = false;
        });
    },

    deleteToDustBin(state) {},
  },
  props: ["state","category"],
};
</script>
