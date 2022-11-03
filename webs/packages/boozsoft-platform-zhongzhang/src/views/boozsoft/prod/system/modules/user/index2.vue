<template>
  <Edit
    @save="saveUser"
    @register="registerEditPage"
  />
  <a-button type="primary" class="my-4" @click="openAddPage()">新增</a-button>
  <BasicTable @register="registerTable">
    <template #action="{ record }">
      <TableAction
        :width="'500px'"
        :actions="[
          {
            label: '编辑',
            icon: 'ic:outline-delete-outline',
            onClick: openModifyPage.bind(null, record),
          },{
            label: '删除',
            icon: 'ic:outline-delete-outline',
            onClick: del.bind(null, record.id),
          },
        ]"
      />
    </template>
  </BasicTable>
</template>
<script lang="ts">
import { defineComponent, getCurrentInstance, reactive, toRefs } from 'vue'
import { BasicTable, useTable, TableAction } from '/@/components/Table'
import { useModal } from '/@/components/Modal'
import Edit from './popup/edit.vue'
import { deleteUser, getUserListById, saveUser } from '/@/api/record/system/user'

const columns = [
  { title: 'id', dataIndex: 'id', ellipsis: true },
  // { title: '部门ID', dataIndex: 'deptId', ellipsis: true },
  { title: '部门', dataIndex: 'deptName', ellipsis: true },
  { title: '角色', dataIndex: 'roleName', ellipsis: true },
  { title: '真实名字', dataIndex: 'realName', ellipsis: true },
  { title: '名字', dataIndex: 'name', ellipsis: true },
  // { title: '角色ID', dataIndex: 'roleId', ellipsis: true },
  // { title: '角色名称', dataIndex: 'roleId', ellipsis: true },
  // { title: '邮箱ID', dataIndex: 'postId', ellipsis: true },
  { title: '手机', dataIndex: 'phone', ellipsis: true },
  // { title: '生日', dataIndex: 'birthday', ellipsis: true },
  // { title: '性别', dataIndex: 'sex', ellipsis: true },
  // { title: '代码', dataIndex: 'code', ellipsis: true },
  { title: '用户名', dataIndex: 'username', ellipsis: true },
  { title: '密码', dataIndex: 'password', ellipsis: true }
  // { title: '头像', dataIndex: 'avatar', ellipsis: true },
  // { title: '邮箱', dataIndex: 'email', ellipsis: true }

]

const val = {
  id: '',
  code: '',
  deptName: '',
  roleName: '',
  username: '',
  password: '',
  name: '',
  realNa: '',
  avatar: '',
  email: '',
  phone: '',
  birthday: '',
  sex: '',
  roleId: '',
  deptId: '',
  postId: ''
}
export default defineComponent({
  components: { Edit, BasicTable, TableAction },

  setup() {
    const state = reactive({
      editData: {
        id: 3333
      },
      children: []
    })

    const { ctx } = getCurrentInstance()

    function getChildren() {
      const arr = [] = state.children
      const matched = ctx.$router.currentRoute.value.matched
      const parent = matched[matched.length - 2]
      const children = parent.children
      for (let i = 0; i < children.length; i++) {
        arr.push({
          title: children[i].meta.title,
          path: parent.path + '/' + children[i].path
        })
      }
    }

    getChildren()

    const [registerTable, { instance, formInstance, reload }] = useTable({
      api: getUserListById,
      columns: columns,
      bordered: true,
      actionColumn: {
        width: 160,
        title: '操作',
        dataIndex: 'action',
        slots: { customRender: 'action' }
      }
    })

    const [registerEditPage, { openModal: openEditPage }] = useModal()

    return {
      async del(id) {
        await deleteUser(id)
        alert('删除成功！')
        reload()
      },
      registerEditPage,
      async saveUser(data) {
        console.log(data)
        await saveUser(data)
        reload(data)
      },
      ...toRefs(state),
      openAddPage() {
        openEditPage(true, {
          data: val
        })
      },
      openModifyPage(a, b) {
        openEditPage(true, {
          data: a
        })
      },
      registerTable
    }
  }
})
</script>
<style>
.ant-tabs .ant-tabs-left-content {
  padding-left: 0 !important;
}

.card-container {
  background: #f5f5f5;
  overflow: hidden;
  padding: 24px;
}

.card-container > .ant-tabs-card > .ant-tabs-content {
  /*height: 120px;*/
  margin-top: -16px;
}

.card-container > .ant-tabs-card > .ant-tabs-content > .ant-tabs-tabpane {
  background: #fff;
  padding: 16px;
}

.card-container > .ant-tabs-card > .ant-tabs-bar {
  border-color: #fff;
}

.card-container > .ant-tabs-card > .ant-tabs-bar .ant-tabs-tab {
  border-color: transparent;
  background: transparent;
}

.card-container > .ant-tabs-card > .ant-tabs-bar .ant-tabs-tab-active {
  border-color: #fff;
  background: #fff;
}
</style>
