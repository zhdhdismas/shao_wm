<template>
  <div style="width: 700px">
    <el-input
      placeholder="请输入部门名称进行搜索..."
      prefix-icon="el-icon-search"
      v-model="filterText"
    >
    </el-input>
    <br>
    <br>

    <el-tree
      :data="deps"
      :props="defaultProps"
      :filter-node-method="filterNode"
      ref="tree"
    >
      <span
        class="custom-tree-node"
        style="display: flex; justify-content: space-between; width: 100%"
        slot-scope="{ node, data }"
      >
        <span>{{ node.label }}</span>
        <span>
          <el-button
            type="primary"
            size="mini"
            class="depBtn"
            @click="() => showAddDepView(data)"
          >
            添加部门
          </el-button>
          <el-button
            type="danger"
            size="mini"
            class="depBtn"
            @click="() => deleteDep(data)"
          >
            删除部门
          </el-button>
        </span>
      </span>
    </el-tree>
    <el-dialog title="添加部门" :visible.sync="dialogVisible" width="30%">
      <div>
        <table>
          <tr>
            <td>
              <el-tag>上级部门</el-tag>
            </td>
            <td>{{ pname }}</td>
          </tr>
          <tr>
            <td>
              <el-tag>部门名称</el-tag>
            </td>
            <td>
              <el-input
                v-model="dep.name"
                placeholder="请输入部门名称..."
              ></el-input>
            </td>
          </tr>
        </table>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doAddDep">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
  
  <script>
import { getRequest, postRequest, deleteRequest } from "../../utils/api";
export default {
    name:"depmana",
  data() {
    return {
      filterText: "",
      deps: [],
      defaultProps: {
        children: "children",
        label: "name",
      },
      dep: {
        name: "",
        parentId: -1,
      },
      pname: "",
      dialogVisible: false,
    };
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    },
  },
  mounted() {
    this.initDeps();
  },
  methods: {
    initDep() {
      this.dep = {
        name: "",
        parentId: -1,
      };
      this.pname = "";
    },
    initDeps() {
      getRequest("/admin/department/all").then((resp) => {
        this.deps = resp.data;
      });
    },
    removeDepFromDeps(p, deps, id) {
      for (let i = 0; i < deps.length; i++) {
        let d = deps[i];
        if (d.id == id) {
          deps.splice(i, 1);
          if (deps.length == 0) {
            p.isParent = false;
          }
          return;
        } else {
          this.removeDepFromDeps(d, d.children, id);
        }
      }
    },
    addDep2Deps(deps, dep) {
      for (let i = 0; i < deps.length; i++) {
        let d = deps[i];
        console.log(d.name);
        if (d.id == dep.parentId) {
          d.children.push(dep);
          d.isParent = true;
          return;
        } else {
          this.addDep2Deps(d.children, dep);
        }
      }
    },
    doAddDep() {
      postRequest("/admin/department/add", {
        name: this.dep.name,
        parentId: this.dep.parentId,
      }).then((resp) => {
        this.dialogVisible = false;
        this.addDep2Deps(this.deps, resp.data.obj);
        this.initDep();
      });
    },
    showAddDepView(data) {
      this.pname = data.name;
      this.dep.parentId = data.id;
      this.dialogVisible = true;
    },
    deleteDep(data) {
      if (data.id == 1) {
        this.$message.error("公司无法被失败！");
        return;
      }
      if (data.isParent) {
        this.$message.error("父部门删除失败！");
      } else {
        this.$confirm(
          "此操作将永久删除该【" + data.name + "】部门, 是否继续?",
          "提示",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning",
          }
        )
          .then(() => {
            deleteRequest("/admin/department/" + data.id).then((resp) => {
              if (resp.data.status == "success") {
                this.$message({
                  type: resp.data.status,
                  message: resp.data.msg,
                });
                this.removeDepFromDeps(null, this.deps, data.id);
              }
            });
          })
          .catch(() => {
            this.$message({
              type: "info",
              message: "已取消删除",
            });
          });
      }
    },
    filterNode(value, data) {
      if (!value) {
        return true;
      }

      //   console.log(data.name);
      //   console.log(data.name.indexOf(value) !== -1);
      return data.name.indexOf(value) !== -1;
    },
  },
};
</script>
  
  <style scoped>
.depBtn {
  padding: 2px;
}
</style>