<template>
  <Edit
    @save="saveRole2"
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
          },
          {
            label: '授权',
            icon: 'ic:outline-delete-outline',
            onClick: openModifyPage.bind(null, record),
          },
          {
            label: '删除',
            icon: 'ic:outline-delete-outline',
            onClick: delRole.bind(null, record.id),
          },
        ]"
      />
    </template>
  </BasicTable>
</template>

<script setup lang="ts">
import { defineAsyncComponent, defineComponent, getCurrentInstance, reactive, toRefs } from 'vue'
import { BasicTable, useTable, TableAction } from '/@/components/Table'
import { useModal } from '/@/components/Modal'
import { deleteRole, getRoleListById, saveRole } from '/@/api/record/system/role'
const Edit = defineAsyncComponent(() => import('./popup/edit.vue'))

const columns = [
  { title: 'id', dataIndex: 'id', ellipsis: true },
  { title: '租户ID', dataIndex: 'tenantId', ellipsis: true },
  // { title: '租金编号', dataIndex: 'rentId', ellipsis: true },
  { title: '角色名称', dataIndex: 'roleName', ellipsis: true },
  { title: '分类', dataIndex: 'sort', ellipsis: true },
  { title: '角色别名', dataIndex: 'roleAlias', ellipsis: true }
  // { title: '是否被删除', dataIndex: 'isDeleted', ellipsis: true }
]

const val = {
  id: '',
  tenantId: '',
  rentId: '',
  roleName: '',
  sort: '',
  roleAlias: '',
  isDeleted: ''
}
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
  api: getRoleListById,
  columns: columns,
  bordered: true,
  actionColumn: {
    width: 200,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  }
})
const [registerEditPage, { openModal: openEditPage }] = useModal()

async function saveRole2(data) {
  console.log(data)
  await saveRole(data)
  // createGroupInfo(a)
  reload(data)
}

function openAddPage() {
  openEditPage(true, {
    data: val
  })
}

function openModifyPage(a, b) {
  openEditPage(true, {
    data: a
  })
}
async function delRole(id) {
  await deleteRole(id)
  alert('删除成功！')
  reload()
}
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
