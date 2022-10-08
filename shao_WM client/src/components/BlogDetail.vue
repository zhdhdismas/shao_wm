 
<template>
  <div>
    <el-row>
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

      <el-col :span="24">
        <div>
          <div>
            <h3 style="margin-top: 0px; margin-bottom: 0px; text-align: center">
              {{ blog.blogTitle }}
            </h3>
          </div>
          <div
            style="
              width: 100%;
              margin-top: 5px;
              display: flex;
              justify-content: flex-end;
              align-items: center;
            "
          >
            <span>作者:{{ username }}</span
            >&emsp;
            <span style="margin: 0px 50px 0px 0px">电话:{{ phone }}</span>
            <p>邮箱:{{ email }}</p>
          </div>
          <div
            style="
              width: 100%;
              margin-top: 5px;
              display: flex;
              justify-content: flex-end;
              align-items: center;
            "
          >
            <span
              ><i @click="loveVote()"
                >点赞博客:&emsp;{{ blog.like ? "♥" : "♡" }}</i
              >
              {{ blog.liked }}</span
            >&emsp;&emsp;
            <p style="font-size: small; color: grey">按时间顺序前十点赞用户:</p>
            <div
              v-for="(item, index) in userDTO"
              :key="index"
              style="
                display: inline;
                color: #20a0ff;
                margin-right: 5px;
                font-size: 12px;
              "
            >
              <div>
                <img
                  :src="item.avatar"
                  width="30"
                  height="30"
                  @click="toMsg(item)"
                />
                <br />
                <span @click="toMsg(item)">{{ item.username }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <!--  -->
    </el-row>
    <el-col>
      <mavon_editor
        class="markdown-body"
        :codeStyle="blog.code"
        v-html="blog.blogContent"
      ></mavon_editor>
    </el-col>
  </div>
</template>
   <style scoped>
.commentCon {
  width: 100%;
  word-wrap: break-word;
}
</style>
  <script>
import "mavon-editor/dist/css/index.css";
import moment from "moment";
import { mavonEditor } from "mavon-editor";
import "mavon-editor/dist/css/index.css";
import { getRequest, postRequest } from "../utils/api";

export default {
  components: {
    mavon_editor: mavonEditor,
  },
  mounted() {
    this.blog = this.$route.query.blog;
    var bid = this.$route.query.bid;
    this.username = this.$route.query.username;
    this.email = this.$route.query.email;
    this.phone = this.$route.query.phone;
    this.avatar = this.$route.query.avatar;
    getRequest("/blog/getABlog/" + bid).then((resp) => {
      this.blog = resp.data;
    });
    this.loadUserFans(bid);
  },
  methods: {
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
    loadUserFans(bid) {
      getRequest("/blog/blogLikes/" + bid).then((resp) => {
        this.userDTO = resp.data;
      });
    },
    loveVote() {
      postRequest("/blog/likeBlog", { bid: this.blog.bid }).then((resp) => {
        this.blog.like = !this.blog.like;
        if (this.blog.like) {
          this.blog.liked = this.blog.liked + 1;
        } else {
          this.blog.liked = this.blog.liked - 1;
        }
        this.loadUserFans(this.blog.bid);
      });
    },
    // 格式化时间
    formatDate(time) {
      return moment(time).format("YYYY-MM-DD HH:mm:ss");
    },

    goBack() {
      this.$router.go(-1);
    },
  },
  data() {
    return {
      userDTO: {},
      bid: 0,
      username: "",
      avatar: "",
      phone: "",
      email: "",
      blog: {
        like: false,
      },
    };
  },
};
</script>
  