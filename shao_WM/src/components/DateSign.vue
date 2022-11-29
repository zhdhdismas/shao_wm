
<template>
  <div>
    <el-dialog title="添加工作内容" :visible.sync="dialogFormVisible">
      <el-input
        type="textarea"
        :rows="2"
        placeholder="请输入内容"
        v-model="textarea"
      >
      </el-input>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="updateText()">确 定</el-button>
      </div>
    </el-dialog>
    <el-calendar v-model="value">
      <template slot="dateCell" slot-scope="{ data }">
        {{ data.day.split("-").slice(1).join("-") }}
        <i class="el-icon-s-release" @click="oneForm(data)"></i>
        <span v-for="(item, index) in calendarData" :key="index">
          <span v-if="data.day == item"> ✔️ </span>
        </span>
        <br />
        <br />
        <div v-for="(item, index) in calendarText" :key="index + 1000">
          <div v-if="data.day == item.createTime">
            <i
              class="el-icon-s-comment"
              @click="open(data)"
              style="color: blue"
            ></i>
            &emsp;
            <el-popconfirm
              title="这是一段内容确定删除吗? "
              @confirm="deleteText(data)"
            >
              <i slot="reference" class="el-icon-delete"></i>
            </el-popconfirm>
          </div>
        </div>
      </template>
    </el-calendar>
    <el-button type="success" @click="submit()">签到</el-button>
    &emsp; &emsp;
    <span>本月已连续签到</span>
    <span style="color: red"> {{ count }} </span>
    <span>天</span>
  </div>
</template>
<script>
import moment from "moment";
import {
  deleteRequest,
  getRequest,
  postRequest,
  putRequest,
} from "../utils/api";
export default {
  mounted() {
    this.value = this.formatDate(new Date());
    this.loadCalendar();
    this.loadKeepSign();
  },
  data() {
    return {
      isShow: false,
      count: 0,
      flag: false,
      curDate: "",
      textarea: "",
      value: new Date(),
      dialogFormVisible: false,
      calendarData: [],
      calendarText: [],
    };
  },
  methods: {
    deleteText(item) {
      deleteRequest("/deleteText", { createTime: item.day }).then((resp) => {
        this.$message({ type: resp.data.status, message: resp.data.msg });
        this.loadCalendar();
      });
    },
    loadKeepSign() {
      getRequest("/sign/count").then((resp) => {
        this.count = resp.data;
      });
    },
    oneForm(item) {
      this.dialogFormVisible = true;
      this.textarea = "";
      this.flag = false;
      for (var i = 0; i < this.calendarText.length; i++) {
        if (this.calendarText[i].createTime == item.day) {
          this.textarea = this.calendarText[i].text;
          this.flag = true;
          break;
        }
      }
      this.curDate = item.day;
      console.log(this.curDate);
    },
    updateText() {
      if (this.textarea.length == 0) {
        this.$message({ type: "error", message: "输入不得为空" });
        return false;
      }
      var url = "";
      if (!this.flag) {
        url = "/text";
        postRequest(url, {
          text: this.textarea,
          createTime: this.curDate,
        }).then((resp) => {
          this.$message({ type: resp.data.status, message: resp.data.msg });
          this.loadCalendar();
        });
      } else {
        url = "/updateText";
        putRequest(url, {
          text: this.textarea,
          createTime: this.curDate,
        }).then((resp) => {
          this.$message({ type: resp.data.status, message: resp.data.msg });
          this.loadCalendar();
        });
      }
      this.dialogFormVisible = false;
    },
    open(item) {
      var msg = "";
      for (var i = 0; i < this.calendarText.length; i++) {
        if (this.calendarText[i].createTime == item.day) {
          msg = this.calendarText[i].text;
          break;
        }
      }
      this.$notify({
        title: item.day,
        offset: 100,
        message: this.$createElement(
          "div",
          { style: " word-wrap: break-word;word-break:break-all;color:teal" },
          msg
        ),
        duration: 0,
      });
    },
    formatDate(time) {
      return moment(time).format("YYYY-MM-DD");
    },
    submit() {
      postRequest("/sign").then((resp) => {
        this.$message({ type: resp.data.status, message: resp.data.msg });
        this.value = this.formatDate(new Date());
        this.loadCalendar();
        this.loadKeepSign();
      });
    },
    loadCalendar() {
      getRequest("/getSign").then((resp) => {
        
        this.calendarData = resp.data.key;
        this.calendarText = resp.data.value;
      });
    },
    // 多个标记示例
  },
};
</script>
 
