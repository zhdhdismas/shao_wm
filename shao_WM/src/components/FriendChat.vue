<template>
  <div id="friendChat">
    <div class="sidebar">
      <card></card>
      <list></list>
    </div>
    <div class="main">
      <message></message>
      <usertext></usertext>
    </div>
  </div>
</template>

<script>
import card from "@/views/chat/card";
import list from "@/views/chat/list";
import message from "@/views/chat/message";
import usertext from "@/views/chat/usertext";
import { getRequest } from "@/utils/api";
export default {
  name: "FriendChat",
  data() {
    return {};
  },
  mounted: function () {
    this.$store.dispatch("initData");
  },
  components: {
    card,
    list,
    message,
    usertext,
  },
};
</script>

<style lang="scss" scope>
#friendChat {
  margin: 20px auto;
  // width: 800px;
  // height: 600px;
  width: 1200px;
  height: 800px;
  overflow: hidden;
  border-radius: 10px;
  .sidebar,
  .main {
    height: 100%;
  }
  .sidebar {
    float: left;
    color: #f4f4f4;
    background-color: #2e3238;
    width: 200px;
  }
  .main {
    position: relative;
    overflow: hidden;
    background-color: #eee;
  }
}
</style>
