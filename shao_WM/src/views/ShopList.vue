<template>
  <div>
    <el-container class="menu_list">
      <el-main class="main">
        <el-tabs type="border-card">

          <el-tab-pane label="附近5km内">
            <shop_table :x="x" :y="y" :state="0" @transExp="transExp"></shop_table>
          </el-tab-pane>
          <el-tab-pane label="附近10km内">
            <shop_table :x="x" :y="y" :state="1" @transExp="transExp"></shop_table>
          </el-tab-pane>
          <el-tab-pane label="全部门店">
            <shop_table :x="x" :y="y" :state="2"></shop_table>
          </el-tab-pane>
          
        </el-tabs>
      </el-main>
    </el-container>
  </div>
</template>
  <script>
import ShopTable from "@/views/ShopTable";
export default {
  mounted: function () {
    console.log(this.x+","+this.y);
  },
  methods: {
    transExp(data){
        this.$emit("transExp",data);
    }
  },
  data() {
    return {};
  },

  components: {
    shop_table: ShopTable,
  },
  props: ["x", "y"],
};
</script>
  <style scoped>
</style>
  