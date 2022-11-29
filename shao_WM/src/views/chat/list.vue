<template>
  <div id="list">
    <ul style="padding-left: 0px; scroll-margin-top: 50px">
      <li
        v-for="item in admins"
        :class="{
          active: currentSession
            ? item.username === currentSession.username
            : false,
        }"
        v-on:click="changeCurrentSession(item)"
      >
        <!-- :class="[item.id === currentSession ? 'active':'']" -->
        <img
          class="avatar"
          :src="item.avatar"
          :alt="item.username"
          @error="setDefaultImage"
        />
        <p class="name">{{ item.username }}</p>
      </li>
    </ul>
  </div>
</template>
  
  <script>
import { mapState } from "vuex";
import defaultImage from "@/assets/R-C.jpg";
export default {
  name: "list",
  data() {
    return {};
  },
  computed: mapState(["admins", "currentSession"]),
  methods: {
    
    setDefaultImage(e) {
      e.target.src = defaultImage;
    },
    changeCurrentSession: function (id) {
      this.$store.commit("changeCurrentSessionId", id);
    },
  },
};
</script>
  
  <style lang="scss" scoped>
#list {
  li {
    padding: 0 15px;
    border-bottom: 12px solid #292c33;
    cursor: pointer;
    &:hover {
      background-color: rgba(255, 255, 255, 0.03);
    }
  }
  li.active {
    /*注意这个是.不是冒号:*/
    // background-color: rgba(255, 255, 255, 0.1);
    background-color: 	#778899   ;
  }
  .avatar {
    border-radius: 2px;
    width: 30px;
    height: 30px;
    vertical-align: middle;
  }
  .name {
    display: inline-block;
    margin-left: 15px;
  }
}
</style>