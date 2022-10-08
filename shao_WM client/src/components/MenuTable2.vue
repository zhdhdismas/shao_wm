<template>
  <div>
    <div style="display: flex; justify-content: flex-start">
      <el-input
        placeholder="通过菜品搜索该分类下的菜"
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
      <el-col :span="6"></el-col>
      <el-col :span="2" style="color: black">原价总和</el-col>
      <el-col :span="1" style="color: red">{{ sumPrice }}</el-col>
      <el-col :span="1" style="color: black">元</el-col>
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
    >
      <el-table-column
        type="selection"
        :selectable="checkboxSelect"
        width="35"
        align="left"
        :reserve-selection="true"
      >
      </el-table-column>
      <el-table-column label="菜品" width="200" align="left">
        <template slot-scope="scope"
          ><span
            style="color: #409eff; cursor: pointer"
            @click="itemClick(scope.row)"
            >{{ scope.row.title }}</span
          >
        </template>
      </el-table-column>
      <el-table-column label="是否有货" width="160" align="center">
        <template slot-scope="scope">
          <span v-show="scope.row.status == true" style="color: green"
            >有货</span
          >
          <span v-show="scope.row.status == false" style="color: red"
            >缺货中</span
          >
        </template>
      </el-table-column>
      <el-table-column label="价格" width="140" align="left">
        <template slot-scope="scope">{{ scope.row.price }}</template>
      </el-table-column>

      <el-table-column prop="img" label="图片" width="150" align="center">
        <template v-slot:default="scope">
          <el-image :src="scope.row.location" />
        </template>
      </el-table-column>

      <el-table-column
        prop="introduction"
        label="介绍"
        width="400"
        align="center"
      >
      </el-table-column>
      <el-table-column label="份数" width="250" align="center">
        <template slot-scope="scope">
          <el-input-number
            v-model="nums[scope.row.mid]"
            @change="changeNum"
            :disabled="showadd[scope.row.mid] != 1"
            :min="1"
            :max="10"
          ></el-input-number>
        </template>
      </el-table-column>
      
    </el-table>
    <div class="menu_table_footer">
      <span class="menus">shao_菜馆 <i class="el-icon-knife-fork"></i></span>
      <el-pagination
        background
        :page-size="pageSize"
        layout="prev, pager, next"
        :total="totalCount"
        @current-change="currentChange"
        v-show="menus.length > 0"
      >
      </el-pagination>
    </div>
  </div>
</template>
 

<script>
import { getRequest } from "../utils/api";

export default {
  data() {
    return {
      values: [],
      menuIdMax: 2000,

      showadd: [],
      num: 4,
      nums: [],
      menus: [],
      selItems: [],
      loading: false,
      currentPage: 1,
      totalCount: -1,
      pageSize: 6,
      keywords: "",
      //
      temp: 0,
    };
  },

  mounted: function () {
    var arr = [];
    for (var i = 0; i < this.menuIdMax; i++) {
      arr[i] = 1;
    }
    this.nums = arr;
    this.loading = true;

    this.loadMenus(1, this.pageSize);
  },

  methods: {
    checkboxSelect(row, rowIndex) {
        //禁用整一行数据
        //   :data="menus.filter((v)=>v.status==true)"

      //绑定参数表格禁用
      return row.status == true; //当满足什么条件时启用
    },
    changeNum() {
      this.handleSelectionChange(this.selItems);
    },
    getRowKey(row) {
      return row.mid;
    },
    deleteRow() {
      this.$refs.multipleTable.clearSelection();
    },
    //search
    searchClick() {
      this.loadMenus(1, this.pageSize);
    },
    itemClick(row) {
      this.$router.push({
        path: "/MenuDetail",
        query: { mid: row.mid, img: row.location, category: this.category },
      });
    },
    //submit 按钮

    //翻页
    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.loading = true;
      this.loadMenus(currentPage, this.pageSize);
    },
    //加载页面
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
        .then(
          (resp) => {
            _this.loading = false;
            if (resp.status == 200) {
              _this.menus = resp.data.menus;
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
    handleSelectionChange(val) {
      this.selItems = val;
      this.showadd = [];
      var c = { cost: 0, price: 0, mtf: [], state: this.state };
      for (var i = 0; i < this.selItems.length; i++) {
        var x = [];
        x[0] = this.selItems[i].mid;
        x[1] = this.selItems[i].title;
        x[2] = this.nums[this.selItems[i].mid];
        c.mtf.push(x);
        c.price += this.selItems[i].price * x[2];
        c.cost += this.selItems[i].cost * x[2];
        this.showadd[this.selItems[i].mid] = 1;
      }
      this.$emit("mget", c);
    },
  },

  props: ["state", "sumPrice", "category"],
};
</script>
<style type="text/css">
.menu_table_footer {
  display: flex;
  box-sizing: content-box;
  padding-top: 10px;
  padding-bottom: 0px;
  /* 下方菜品样式 */
  padding-left: 40px;
  margin-bottom: 0px;
  text-align: center;
  justify-content: space-between;
  color: sienna;
}
</style>
