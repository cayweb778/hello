<script setup lang="ts">
import { defineComponent, getCurrentInstance, reactive, toRefs } from 'vue'
import { BasicTable, useTable, TableAction } from '/@/components/Table'
import { useModal } from '/@/components/Modal'
import Edit from './popup/edit.vue'
import MenuDrawer from './popup/menu-drawer.vue'
import type { BasicColumn } from '/@/components/Table/src/types/table.ts'
import { deleteUser, getUserListById, saveUser } from '/@/api/record/system/user'
import { getRoleListById } from '/@/api/record/system/role'
import { useDrawer } from '/@/components/Drawer'

// CRUD接口
const CrudApi = {
  list: getRoleListById,
  columns: [
    { title: 'id', dataIndex: 'id', defaultHidden: true, ellipsis: true },
    { title: '租户ID', dataIndex: 'tenantId', defaultHidden: true, ellipsis: true },
    // { title: '租金编号', dataIndex: 'rentId', ellipsis: true },
    { title: '角色名称', dataIndex: 'roleName', ellipsis: true },
    { title: '分类', dataIndex: 'sort', ellipsis: true },
    { title: '角色别名', dataIndex: 'roleAlias', ellipsis: true }
    // { title: '是否被删除', dataIndex: 'isDeleted', ellipsis: true }
  ],
  editData: {
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
}
// @ts-ignore
const [registerTable, { instance, formInstance, reload }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  actionColumn: {
    width: 160,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  }

})
const [registerEditPage, { openModal: openEditPage }] = useModal()

const openAddPage = () => {
  openEditPage(true, {
    data: CrudApi.editData
  })
}
const openModifyPage = (a, b) => {
  openEditPage(true, {
    data: CrudApi.editData
  })
}
const [
  registerMenuDrawer,
  {
    openDrawer: openMenuPage2
    // ransferDrawerData
  }
] = useDrawer()
const openMenuPage = (record) => {
  openMenuPage2(true, {
    data: record.id
  })
}
const del = async(id) => {
  await deleteUser(id)
  alert('删除成功！')
  reload()
}
const saveUserMet = async(data) => {
  await saveUser(data)
  reload(data)
}

</script>
<template>
  <Edit
    @save="saveUserMet"
    @register="registerEditPage"
  />
  <MenuDrawer @register="registerMenuDrawer" />
  <a-button type="primary" class="my-4" @click="openAddPage()">新增</a-button>
  <BasicTable @register="registerTable">
    <template #action="{ record }">
      <TableAction
        :actions="[
          {
            label: '编辑',
            icon: 'ic:outline-delete-outline',
            onClick: openModifyPage.bind(null, record),
          },{
            label: '授权',
            icon: 'ic:outline-delete-outline',
            onClick: openMenuPage.bind(null, record),
          },{
            label: '删除',
            icon: 'ic:outline-delete-outline',
            onClick: del.bind(null, record.id)
          },
        ]"
      />
    </template>
  </BasicTable>
</template>

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
