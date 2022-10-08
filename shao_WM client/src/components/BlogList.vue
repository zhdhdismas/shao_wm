<template>
      <el-container class="menu_list">
        <el-main class="main">
          <el-tabs  type="border-card" @tab-click="load">
            <el-tab-pane label="博客大厅">
                <blog_table :state="0" ref="reload"></blog_table>
            </el-tab-pane>
            <el-tab-pane label="我的博客">
                <MyBlogTableVue :state="1"  ></MyBlogTableVue>
            </el-tab-pane>
          </el-tabs>
        </el-main>
      </el-container>
  </template>
  <script>
  import BlogTable from "@/components/BlogTable";
  import MyBlogTableVue from "@/components/MyBlogTable";
  export default {
    mounted: function () {
   
    },
    data() {
      return {
         isAdmin:false,   
      };
    },
    methods:{
        load(){
            this.$refs.reload.reload();
        }
    },
 
  
    components: {
      blog_table: BlogTable,
      MyBlogTableVue
    },
  };
  </script>
  <style>
  .menu_list > .header,
  .footer {
    background-color: #ececec;
    margin-top: 10px;
    padding-left: 5px;
    display: flex;
    justify-content: flex-start;
  }
  
  .article_list > .main {
    /*justify-content: flex-start;*/
    display: flex;
    flex-direction: column;
    padding-left: 0px;
    background-color: #fff;
    padding-top: 0px;
    margin-top: 8px;
  }
  </style>
  