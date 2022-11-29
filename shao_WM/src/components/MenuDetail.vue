<template>
  <div>
    <el-row v-loading="loading">
      <el-col :span="24">
        <div style="text-align: left">
          <el-button
            type="text"
            icon="el-icon-back"
            @click="goBack"
            style="padding-bottom: 0px"
            >返回</el-button
          >
        </div>
      </el-col>
      <div style="display: inline-block; float: left; margin-left: 100px">
        <img :src="this.$route.query.img" width="500" height="500" />
      </div>
      <div
        style="display: inline-block; width: auto; height: 500px; float: left"
      >
        <el-dialog
          title="修改菜品信息"
          width="50%"
          top="5vh"
          :visible.sync="dialogFormVisible"
          :close-on-click-modal="false"
          :show-close="false"
        >
          <span>主料:</span
          ><el-input
            v-model="zhuliao"
            style="width: 200px"
            autocomplete="off"
          ></el-input>
          <span>味道:</span>
          <el-input
            v-model="weidao"
            style="width: 200px"
            autocomplete="off"
          ></el-input>
          <br />
          <br />
          <span>做菜方式:</span
          ><el-input
            v-model="way"
            style="width: 200px"
            autocomplete="off"
          ></el-input>
          <span>做菜时间:</span>
          <el-input
            v-model="time"
            style="width: 200px"
            autocomplete="off"
          ></el-input>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="updateMenuDetail">确 定</el-button>
          </div>
        </el-dialog>
        <el-descriptions
          title="菜品详情"
          style="margin-left: 20px"
          direction="vertical"
          :column="4"
          :labelStyle="{ width: '160px', height: '68px' }"
          :contentStyle="{ width: '160px', height: '75px' }"
          border
        >
          <template slot="extra">
            <el-button
              type="primary"
              size="small"
              @click="dialogFormVisible = true"
              v-show="isAdmin"
              >操作</el-button
            >
          </template>
          <el-descriptions-item label="菜名" :span="2">{{
            menu.title
          }}</el-descriptions-item>
          <el-descriptions-item label="主料">{{
            zhuliao
          }}</el-descriptions-item>
          <el-descriptions-item label="味道">{{ weidao }}</el-descriptions-item>
          <el-descriptions-item label="菜品类型">
            <el-tag size="small">{{ category }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="菜品价格">
            <span style="color: red">{{ menu.price + " " }}</span
            ><span>元</span>
          </el-descriptions-item>
          <el-descriptions-item label="做菜方式">{{
            way
          }}</el-descriptions-item>
          <el-descriptions-item label="做菜时间">{{
            time
          }}</el-descriptions-item>
          <el-descriptions-item label="菜品描述" :span="2"
            >{{ menu.introduction }}
          </el-descriptions-item>
          <el-descriptions-item label="平均评分">
            <el-rate
              v-model="value"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value} 分"
            >
            </el-rate>
          </el-descriptions-item>
          <el-descriptions-item label="给出你的评分"
            ><div class="block">
              <el-button
                type="primary"
                size="mini"
                style="margin-left: 25px; float: right"
                v-show="!isShow"
                @click="rateThisMenu"
                >评价</el-button
              >
              <el-button
                type="info"
                size="mini"
                style="margin-left: 60px"
                v-show="isShow"
                :disabled="true"
                >已评价</el-button
              >
              <br />
              <br />
              <el-rate
                v-model="point"
                v-show="!isShow"
                style="margin-bottom: 5px; float: left"
              ></el-rate>
              <el-rate
                v-model="myRate"
                v-show="isShow"
                disabled
                style="margin-bottom: 5px"
              ></el-rate></div
          ></el-descriptions-item>
        </el-descriptions>
      </div>
    </el-row>
    <br />
    <el-divider style="border-color: red"></el-divider>
    <br />

    <el-row style="margin-bottom: 20px" :gutter="10">
      <el-input
        type="text"
        v-model="comment.content"
        placeholder="添加评论"
        style="width: 800px; float: left"
      ></el-input>
      <el-button
        type="primary"
        style="margin-left: 20px; float: left"
        @click="addComment"
        >添加评论
      </el-button>
    </el-row>
    <!-- 评论区 -->
    <div
      style="text-align: left"
      v-for="(comment, index) in comments"
      :key="index"
    >
      <div class="commentCon">{{ comment.content }}</div>
      <el-divider content-position="left">{{
        formatDate(comment.publishDate)
      }}</el-divider>
      <img
        :src="comment.userDTO.avatar"
        style="height: 30px; width: 30px"
        @click="toMsg(comment.userDTO)"
      />
      <span style="color: cornflowerblue" @click="toMsg(comment.userDTO)">{{
        comment.userDTO.username
      }}</span>
      &numsp;&numsp;
      <el-button
        v-show="canShow(comment.userDTO.username)"
        @click="chehui(comment.cid)"
        >撤回</el-button
      >

      <el-divider><i class="el-icon-s-platform"></i></el-divider>
    </div>
    <el-pagination
      background
      :page-size="pageSize"
      layout="prev, pager, next"
      :total="totalCount"
      @current-change="currentChange"
      v-show="this.comments.length > 0"
    >
    </el-pagination>
  </div>
</template>
 <style scoped>
.commentCon {
  width: 100%;
  word-wrap: break-word;
}
</style>
<script>
import { getRequest, putRequest, deleteRequest } from "../utils/api";
import moment from "moment";
import { postRequest } from "../utils/api";
export default {
  mounted: function () {
    var _this = this;
    this.loading = true;
    this.category = this.$route.query.category;
    this.loadComments(this.currentPage, this.pageSize);
    getRequest("/isAdmin").then((resp) => {
      this.isAdmin = resp.data;
    });
    getRequest("/currentUserName").then(resp=>{
        if(resp.data.status=="success")
        this.currentUserName=resp.data.msg;
    })
    getRequest("/currentUserId").then(resp=>{
        this.uid=resp.data;
    })
    var mid = this.$route.query.mid;
    getRequest("/menus/getAMenu/" + mid).then((resp) => {
      this.menu = resp.data;
      
    });

    this.average();
    this.isRate();
    this.getAMenuDetail();
  },
  methods: {
    
    getAMenuDetail(){
        getRequest("/menus/getAMenuDetail/"+this.$route.query.mid).then(resp=>{
            this.zhuliao=resp.data.zhuliao;
            this.weidao=resp.data.weidao;
            this.way=resp.data.way;
            this.time=resp.data.time;
        })
    },
    updateMenuDetail(){
        putRequest("/menus/updateDetail",{
            weidao:this.weidao,
           zhuliao: this.zhuliao,
           way:this.way,
           time:this.time,
           mid:this.$route.query.mid
        }).then(resp=>{
            this.$message({type:resp.data.status,message:resp.data.msg});
            this.getAMenuDetail();
            this.dialogFormVisible=false;
        })
    },
    average() {
      var mid = this.$route.query.mid;
      getRequest("/menus/average/" + mid).then((resp) => {
            console.log(resp.data);
            this.value =  parseFloat(resp.data.toFixed(2));
      });
    },
    isRate() {
      var mid = this.$route.query.mid;
      getRequest("/menus/isRate/" + mid).then((resp) => {
        this.isShow = resp.data.isRate;
        if (this.isShow) {
          this.myRate = resp.data.myRate;
        }
      });
    },
    rateThisMenu() {
      var mid = this.$route.query.mid;
      postRequest("/menus/rate", {
        point: this.point,
        mid: mid,
      }).then((resp) => {
        this.$message({ type: resp.data.status, message: resp.data.msg });
        this.isRate();
        this.average();
      });
    },
    toMsg(msg) {
      this.$router.push({
        path: "/Example",
        query: {
          username: msg.username,
          phone: msg.phone,
          avatar: msg.avatar,
          email: msg.email,
          uid: msg.uid,
        },
      });
    },
    chehui(id) {
      var _this = this;
      var url = "/deleteMyThisComment";
      var mid = this.$route.query.mid;
      deleteRequest(url, { cid: id, uid: _this.uid, mid: mid }).then((resp) => {
        var data = resp.data;
        if (data.status == "success") {
          _this.$message({ type: "success", message: "删除成功" });
          _this.loadComments(this.currentPage, this.pageSize);
        } else {
          _this.$message({ type: "success", message: "删除失败" });
        }
      });
    },
    canShow(name) {
      var currentUserName = this.currentUserName;
      if (name == currentUserName) {
        return true;
      } else {
        return false;
      }
    },
    // 格式化时间
    formatDate(time) {
      return moment(time).format("YYYY-MM-DD HH:mm:ss");
    },
    loadComments(page, count) {
      var id = this.$route.query.mid;
      var _this = this;
      getRequest(
        "/getComments/" + id + "?page=" + page + "&count=" + count
      ).then(
        (resp) => {
          if (resp.status == 200) {
            _this.comments = resp.data.comments;
            _this.totalCount = resp.data.totalCount;
          }
          _this.loading = false;
        },
        (resp) => {
          _this.loading = false;
        }
      );
    },

    currentChange(currentPage) {
      this.currentPage = currentPage;
      this.loading = true;
      this.loadComments(currentPage, this.pageSize);
    },
    addComment() {
      var mid = this.$route.query.mid;
      var _this = this;
      postRequest("/addComment", {
        content: this.comment.content,
        mid: mid,
      }).then(
        (resp) => {
          _this.loading = false;
          if (resp.status == 200) {
            if (resp.data.status == "success") {
              _this.$message({ type: "success", message: "发表成功" });
              _this.loadComments(this.currentPage, this.pageSize);
              _this.comment.content = "";
            } else {
              _this.$message({ type: "success", message: "发表失败" });
            }
          } else {
            _this.$alert(resp.data.msg, "失败!");
          }
        },
        (resp) => {
          _this.loading = false;
          _this.$alert("找不到服务器⊙﹏⊙∥!", "失败!");
        }
      );
    },
    goBack() {
      this.$router.go(-1);
    },
  },
  data() {
    return {
      dialogFormVisible: false,
      isAdmin: false,
      myRate: 0,
      isShow: false,
      value: 0,
      point: 0,
      zhuliao: "",
      weidao: "",
      way: "",
      time: "",
      category: "",
      menu: [],
      uid: -1,
      currentUserName: "",
      comment: {
        content: "",
      },
      article: {},
      loading: false,
      currentPage: 1,
      pageSize: 2,
      totalCount: -1,
      activeName: "",
      comments: {},
    };
  },
};
</script>
