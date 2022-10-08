<template>
  <div>
    <div style="display: flex; justify-content: flex-start">
      <el-input
        placeholder="通过博客名称搜索该分类下的博客"
        prefix-icon="el-icon-search"
        v-model="keywords"
        style="width: 400px; margin-left: auto"
        size="mini"
      />
      <el-button
        type="primary"
        icon="el-icon-search"
        size="mini"
        style="margin-left: 20px"
        @click="searchClick"
        >搜索
      </el-button>
    </div>
    <el-table
      :data="blogs"
      style="width: 100%"
      :row-key="getRowKey"
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        label="最近修改日期"
        prop="date"
        v-loading="loading"
        width="200"
      >
        <template slot-scope="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="点赞博客" width="140" align="center">
        <template slot-scope="scope">
          <i @click="loveVote(scope.row)">{{ scope.row.like ? "♥" : "♡" }}</i>
          {{ scope.row.liked }}
        </template>
      </el-table-column>

      <el-table-column label="博客名称">
        <template slot-scope="scope">
          <span
            style="color: #409eff; cursor: pointer"
            @click="itemClick(scope.row)"
            >{{ scope.row.blogTitle }}</span
          >
        </template>
      </el-table-column>
      <el-table-column
        prop="user.username"
        label="作者"
        width="400"
        align="center"
      >
      </el-table-column>
      <el-table-column
        v-if="state == 1"
        type="selection"
        width="60"
        align="left"
        :reserve-selection="true"
      >
      </el-table-column>
      <el-table-column align="right">
        <template slot-scope="scope">
          <el-button
            size="mini"
            @click="handleEdit(scope.row)"
            v-show="state == 1"
            >编辑</el-button
          >
          <el-button
            size="mini"
            v-show="state == 1"
            type="danger"
            @click="handleDelete(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <br />
    <div class="menu_table_footer">
      <el-button
        type="danger"
        size="mini"
        style="margin-right: 1115px"
        v-show="this.blogs.length > 0 && this.state == 1"
        :disabled="this.selItems.length == 0"
        @click="deleteMany"
        >批量删除
      </el-button>
      <br />
      <span></span>
      <el-pagination
        background
        :page-size="pageSize"
        layout="prev, pager, next"
        :total="totalCount"
        @current-change="currentChange"
        v-show="this.blogs.length > 0"
        style="float: center"
      >
      </el-pagination>
    </div>
  </div>
</template>
  
  <script>
import moment from "moment";
import { deleteRequest, getRequest, postRequest } from "../utils/api";
export default {
  mounted() {
    var _this = this;
    window.$bus.$on("TableReload", () => {
      _this.loadBlogs(_this.currentPage, _this.pageSize);
    });
    this.loadBlogs(this.currentPage, this.pageSize);
  },
  data() {
    return {
      dustbinData: [],
      blogs: [],
      loading: false,
      currentPage: 1,
      totalCount: -1,
      pageSize: 6,
      keywords: "",
      selItems: [],
      search: "",
    };
  },
  methods: {
    reload(){
        window.$bus.$emit("TableReload");
    },
    loveVote(row) {
        if(row.like){
            row.liked-=1;
        }else{
            row.liked+=1;
        }
      row.like=!row.like;
      postRequest("/blog/likeBlog", { bid: row.bid }).then((resp) => {
      });
    },
    deleteMany() {
      var selItems = this.selItems;
      for (var i = 0; i < selItems.length; i++) {
        this.dustbinData.push(selItems[i].bid);
      }
      var _this = this;
      this.$confirm("是否删除该类别下选中的菜品", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          _this.loading = true;
          var url = "/blog/dustbin";
          deleteRequest(url, { bids: _this.dustbinData }).then(
            (resp) => {
              if (resp.status == 200) {
                var data = resp.data;
                _this.$message({ type: data.status, message: data.msg });
                if (data.status == "success") {
                  _this.currentPage = 1;
                  _this.loadBlogs(_this.currentPage, _this.pageSize);
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

    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.loading = true;
      this.loadBlogs(currentPage, this.pageSize);
    },
    searchClick() {
      this.loadBlogs(1, this.pageSize);
    },
    itemClick(row) {
      this.$router.push({
        path: "/BlogDetail",
        query: {
          blog: row,
          bid: row.bid,
          username: row.user.username,
          phone: row.user.phone,
          email: row.user.email,
          avatar: row.user.avatar,
        },
      });
    },
    guid() {
      return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(
        /[xy]/g,
        function (c) {
          var r = (Math.random() * 16) | 0,
            v = c == "x" ? r : (r & 0x3) | 0x8;
          return v.toString(16);
        }
      );
    },
    getRowKey(row) {
      return row.bid;
    },
    loadBlogs(page, count) {
      var url = "";
      if (this.state == 0) {
        url =
          "/blog/all?page=" +
          page +
          "&count=" +
          count +
          "&keywords=" +
          this.keywords;
      } else {
        url =
          "/blog/getMyBlogs?page=" +
          page +
          "&count=" +
          count +
          "&keywords=" +
          this.keywords;
      }
      getRequest(url).then((resp) => {
        this.totalCount = resp.data.totalCount;
        this.blogs = resp.data.blogs;
        this.loading = false;
      });
    },
    handleSelectionChange(val) {
      this.selItems = val;
    },
    formatDate(time) {
      return moment(time).format("YYYY-MM-DD HH:mm:ss");
    },
    handleEdit(row) {
      this.$router.push({
        path: "/PostBlog",
        query: { from: row.bid },
      });
    },
    handleDelete(row) {
      var _this = this;
      this.$confirm("是否删除该类别下选中的菜品", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          _this.loading = true;
          var url = "/blog/dustbin";
          deleteRequest("/blog/dustbinOne", { bid: row.bid }).then(
            (resp) => {
              if (resp.status == 200) {
                var data = resp.data;
                _this.$message({ type: data.status, message: data.msg });
                if (data.status == "success") {
                  window.$bus.$emit("TableReload");
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
  },
  props: ["state"],
};
</script>