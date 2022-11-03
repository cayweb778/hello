<script setup lang="ts">
import { defineComponent, getCurrentInstance, reactive, toRefs } from 'vue'
import { BasicTable, useTable, TableAction } from '/@/components/Table'
import { useModal } from '/@/components/Modal'
import Edit from './popup/edit.vue'
import MenuEditDrawer from './popup/menu-edit-drawer.vue'
import type { BasicColumn } from '/@/components/Table/src/types/table.ts'
import { deleteUser, getUserListById, saveUser } from '/@/api/record/system/user'
import { getRoleListById } from '/@/api/record/system/role'
import { useDrawer } from '/@/components/Drawer'
import {deleteMenu, saveMenu} from '/@/api/record/system/menu'
import { GetMenuTree } from '/@/api/sys/menu'

// CRUD接口
const CrudApi = {
  list: GetMenuTree,
  columns: [
    { title: 'id', dataIndex: 'id', defaultHidden: true },
    {
      title: '菜单名称',
      dataIndex: 'name',
      width: 170
    },
    { title: '上级ID',required:true, dataIndex: 'parentId', defaultHidden: true, ellipsis: true },
    // {
    //   title: '菜单别名',
    //   dataIndex: 'alias'
    //
    // },
    {
      title: '访问路径',
      dataIndex: 'path',
      width: 100
    },
    {
      title: '菜单组件',
      dataIndex: 'component',
      width: 120
    },
    // {
    //   title: '排序',
    //   dataIndex: 'sort'
    // }
    {
      title: '菜单类型',
      dataIndex: 'category',
      width: 100
    },
    {
      title: '菜单图标',
      dataIndex: 'icon',
      width: 100
    },
    // {
    //   title: '操作按钮类型',
    //   dataIndex: 'action',
    //   width: 100
    // },
    // {
    //   title: '是否打开新页面',
    //   dataIndex: 'isopen',
    //   width: 100
    // },
    // {
    //   title: '备注',
    //   dataIndex: 'remark',
    //   width: 100
    // }

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
  showIndexColumn: false,
  actionColumn: {
    width: 270,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  }

})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const [
  registerMenuPage,
  {
    openDrawer: openMenuPage2
    // ransferDrawerData
  }
] = useDrawer()
async function delMenu(record) {
  if (record.children.length > 0) {
    alert('该菜单已存在子级带单,不能删除')
  } else {
    deleteMenu(id)
  }
}
const del = async(id) => {
  await deleteUser(id)
  alert('删除成功！')
  reload()
}
const saveMenuMet = async(data) => {
  await saveMenu(data)
  reload(data)
}

function openModifyPage(a, b) {
  openEditPage(true, {
    data: a
  })
}
const [
  registerMenuDrawer33,
  {
    openDrawer: openMenuPage2333
    // ransferDrawerData
  }
] = useDrawer()
</script>
<template>
  <Edit
    @save="saveMenuMet"
    @register="registerEditPage"
  />
  <MenuEditDrawer @register="registerMenuDrawer33" />
  <a-button type="primary" class="my-4" @click="openEditPage(true, {parentId:0,category:0})">新增</a-button>
  <BasicTable
    :row-selection="{ type: 'checkbox' ,fixed: true}"
    title="菜单档案"
    title-help-message="树形组件不能和序列号列同时存在"
    :columns="CrudApi.columns"
    :data-source="data"
    :pagination="{ current: 2 }"
    row-key="'12312'"
    :indent-size="20"
    @register="registerTable"
  >
    <template #action="{ record }">
      <TableAction
        :actions="[
          {
            label: '添加下级',
            icon: 'ic:outline-delete-outline',
            onClick: openEditPage.bind(null, true,{parentId:record.id,category:1}),
          },{
            label: '编辑',
            icon: 'ic:outline-delete-outline',
            onClick: openMenuPage2333.bind(null, record),
          },{
            label: '删除',
            icon: 'ic:outline-delete-outline',
            // bind第二个参数对应你的方法第一个参数，第3个参数对应你的方法第二参数 你parentId只是参数声名 又没穿  bye
            onClick: delMenu.bind(null, record),
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
