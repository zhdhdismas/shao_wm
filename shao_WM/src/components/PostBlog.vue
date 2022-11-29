<template>
  <el-container v-loading="loading" class="post-article">
    <el-header class="header">
      <el-input
        v-model="blog.blogTitle"
        placeholder="请输入标题..."
        style="width: 400px; margin-left: 10px"
      ></el-input>
      <el-select
        v-model="curStyle"
        placeholder="请选择代码风格"
        style="margin-left: 40px"
      >
        <el-option
          v-for="(item, index) in codeStyle"
          :key="index"
          :value="item"
        ></el-option>
      </el-select>
    </el-header>
    <el-main class="main">
      <div id="editor">
        <mavon-editor
          style="height: 100%; width: 100%"
          ref="md"
          @imgAdd="imgAdd"
          @imgDel="imgDel"
          :ishljs="true"
          :externalLink="externalLink"
          v-model="blog.mdContent"
          :codeStyle="curStyle"
        ></mavon-editor>
      </div>
      <div
        style="display: flex;align-items: center;margin-top: 15px;justify-content: flex-end;"
      >
        <el-button @click="cancelEdit" v-if="from != undefined"
          >放弃修改</el-button
        >
        <el-button type="primary" @click="saveBlog()" v-if="from != undefined"
          >保存修改</el-button
        >
        <el-button type="primary" @click="saveBlog()" v-if="from == undefined"
          >发表博客</el-button
        >
      </div>
    </el-main>
  </el-container>
</template>
  <script>
import { postRequest, postRequest2 } from "../utils/api";
import { putRequest } from "../utils/api";
import { deleteRequest } from "../utils/api";
import { getRequest } from "../utils/api";
import { uploadFileRequest } from "../utils/api";
// Local Registration
import { mavonEditor } from "mavon-editor";
// 可以通过 mavonEditor.markdownIt 获取解析器markdown-it对象
import "mavon-editor/dist/css/index.css";
import { isNotNullORBlank } from "../utils/utils";

export default {
  mounted: function () {
    var from = this.$route.query.from;
    console.log(from);
    this.from = from;
    if (from != null && from != "" && from != undefined) {
      getRequest("/blog/getABlog/" + from).then((resp) => {
        this.blog = resp.data;
        this.curStyle = this.blog.code;
      });
    }
  },
  components: {
    mavonEditor,
  },
  methods: {
    cancelEdit() {
      this.$router.go(-1);
    },
    saveBlog() {
      this.blog.blogContent = this.$refs.md.d_render;
      if (!isNotNullORBlank(this.blog.blogTitle, this.blog.blogContent)) {
        this.$message({ type: "error", message: "数据不能为空!" });
        return;
      }
      var _this = this;
      _this.loading = true;
      var url = "";
      if (this.from == undefined) {
        url = "/blog/saveBlog";
        postRequest2(url, {
          blogTitle: _this.blog.blogTitle,
          blogContent: _this.blog.blogContent,
          mdContent: _this.blog.mdContent,
          code: this.curStyle,
        }).then((resp) => {
          _this.loading = false;
          if (resp.data.status == "success")
            this.$router.replace({ path: "/BlogList" });
          _this.$message({
            type: resp.data.status,
            message: resp.data.msg,
          });
        });
      } else {
        url = "/blog/updateBlog";
        putRequest(url, {
          bid: _this.blog.bid,
          blogTitle: _this.blog.blogTitle,
          blogContent: _this.blog.blogContent,
          mdContent: _this.blog.mdContent,
          liked: _this.blog.liked,
          code: this.curStyle,
          uid: _this.blog.uid,
        }).then((resp) => {
          _this.loading = false;
          this.$router.replace({ path: "/BlogList" });
          _this.$message({
            type: resp.data.status,
            message: resp.data.msg,
          });
        });
      }
    },
    imgAdd(pos, $file) {
      var _this = this;
      // 第一步.将图片上传到服务器.
      var formdata = new FormData();
      formdata.append("file", $file);
      uploadFileRequest("/upload/img", formdata)
        .then((res) => {
          _this.$refs.md.$img2Url(pos, this.oss + res.data.msg);
        })
        .catch((e) => {});
    },
    imgDel(pos) {},
  },
  data() {
    return {
      curStyle: "",
      codeStyle: [
        "agate",
        "vs",
        "monokai",
        "idea",
        "dark",
        "github",
        "ocean",
        "androidstudio",
        "xcode",
      ],
      externalLink: {
        markdown_css: function () {
          // 这是你的markdown css文件路径
          return "mavon-editor/markdown/github-markdown.min.css";
        },
        hljs_js: function () {
          // 这是你的hljs文件路径
          return "/mavon-editor/highlightjs/highlight.min.js";
          // return 'public/mavon-editor/';
        },
        hljs_css: function (css) {
          // 这是你的代码高亮配色文件路径
          return "/mavon-editor/highlightjs/styles/" + css + ".min.css";
        },
        hljs_lang: function (lang) {
          // 这是你的代码高亮语言解析路径
          return (
            "/mavon-editor/highlightjs/languages/" + lang + ".min.js"
          );
        },
        katex_css: function () {
          // 这是你的katex配色方案路径路径
          return "/mavon-editor/katex/katex.min.css";
        },
        katex_js: function () {
          // 这是你的katex.js路径
          return "/mavon-editor/katex/katex.min.js";
        },
      },
      loading: false,
      from: "",
      search: "",
      oss: "http://shzzz.oss-cn-hangzhou.aliyuncs.com/",
      blog: {
        bid: -1,
        blogContent: "",
        blogTitle: "",
        mdContent: "",
        liked: 0,
        uid: 0,
      },
    };
  },
};
</script>
  <style>
.post-article > .main > #editor {
  width: 100%;
  height: 450px;
  text-align: left;
}

.post-article > .header {
  background-color: #997373;
  margin-top: 10px;
  padding-left: 5px;
  display: flex;
  justify-content: flex-start;
}

.post-article > .main {
  /*justify-content: flex-start;*/
  display: flex;
  flex-direction: column;
  padding-left: 5px;
  background-color: #805d5d;
  padding-top: 0px;
}
</style>
  