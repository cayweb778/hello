<script setup lang="ts">
import { computed, defineComponent, getCurrentInstance, onMounted, reactive, ref, toRefs, unref } from 'vue'
import { BasicTable, useTable, TableAction } from '/@/components/Table'
import { useModal } from '/@/components/Modal'
import type { BasicColumn } from '/@/components/Table/src/types/table.ts'
import { deleteUser, getUserListById, saveUser } from '/@/api/record/system/user'
import { getRoleListById } from '/@/api/record/system/role'
import { useDrawer, useDrawerInner } from '/@/components/Drawer'
import { deleteMenu } from '/@/api/record/system/menu'
import { GetMenuTree, menuIdsByRoleId, saveMenusByRoleId } from '/@/api/sys/menu'
import { BasicTree } from '/@/components/Tree/index'
import { CollapseContainer } from '/@/components/Container/index'
import { useAsyncState } from '@vueuse/core'
// CRUD接口
const CrudApi = {
  list: GetMenuTree,
  columns: [
    { title: 'id', dataIndex: 'id', ellipsis: true },
    { title: '上级ID', dataIndex: 'parentId', ellipsis: true },
    {
      title: '菜单名称',
      dataIndex: 'name',
      ellipsis: true
    },
    {
      title: '菜单别名',
      dataIndex: 'alias'

    },
    {
      title: '路径',
      dataIndex: 'path'
    },
    {
      title: '菜单资源',
      dataIndex: 'source'
    },
    {
      title: '排序',
      dataIndex: 'sort'
    }
    // {
    //   title: '菜单类型',
    //   dataIndex: 'category',
    //   width: 100
    // },
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
const state = { roleId: 0 }
getCurrentInstance().emit('register', {
  state,
  instance: getCurrentInstance()
})
const menuTree = ref([])
const checkedKeys = ref([])
onMounted(async() => {
  menuTree.value = await GetMenuTree()

  const a = await menuIdsByRoleId({ id: state.roleId })
  console.log(1)
  console.log(a)
  console.log(menuTree)
  console.log(2)
  checkedKeys.value = a
  const processMenu = (list) => {
    list.map(item => {
      if (item.hasOwnProperty('children')) {
        processMenu(item.children)
      }
      const key = item.id
      const title = item.name

      const children = item.children == null ? null : JSON.parse(JSON.stringify(item.children))
      for (const key in item) {
        delete item[key]
      }
      item.key = key
      item.title = title
      item.children = children
    })
  }
  processMenu(menuTree.value)
})

const roleId = ref([])

const [register] = useDrawerInner((data) => {
  roleId.value = data.data
})

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

const treeRef = ref(null)
async function getData() {
  const { getCheckedKeys } = unref(treeRef)
  debugger
  await saveMenusByRoleId({ roleId: state.roleId, menuIds: getCheckedKeys() })
}

</script>
<template>
  所拥有的权限：
  <BasicTree
    ref="treeRef"
    :tree-data="menuTree"
    :checkable="true"
    :checked-keys="checkedKeys"
  />
  <a-button @click="getData">保存</a-button>
</template>
