<template>
  <div class="card-container">
    <a-tabs :default-active-key="checkedId" tab-position="left" type="card" @change="handleRouter">
      <a-tab-pane v-for="(row,k) in children" :key="k" :tab="row.title">
        <router-view />
      </a-tab-pane>

    </a-tabs>
  </div>

</template>
<script lang="ts">
import { defineComponent, getCurrentInstance, reactive, toRefs } from 'vue'
import { BasicTable, useTable, TableAction } from '/@/components/Table'
import { getGroupListById } from '/@/api/record/group'
import { useModal } from '/@/components/Modal'

const columns = [
  {
    title: '集团唯一码',
    dataIndex: 'groupId'
  }, {
    title: '集团名称',
    dataIndex: 'groupName'
  },
  {
    title: '集团简介',
    dataIndex: 'explain'
  },
  {
    title: '集团地址',
    dataIndex: 'addr',
    ellipsis: true
  },
  {
    title: '电话',
    dataIndex: 'tel',
    ellipsis: true
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    ellipsis: true
  },
  {
    title: '网址',
    dataIndex: 'www',
    ellipsis: true
  }
]
const val = {
  id: '',
  groupId: '',
  groupName: '',
  groupExplain: '',
  addr: '',
  tel: '',
  fax: '',
  www: '',
  email: '',
  wachatAccounts: ''
}
export default defineComponent({
  components: { BasicTable, TableAction },

  setup() {
    const state = reactive({
      editData: {
        id: 3333
      },
      children: [],
      checkedId: 0
    })

    const { ctx } = getCurrentInstance()

    function getChildren() {
      const arr = [] = state.children
      const matched = ctx.$router.currentRoute.value.matched
      const children = matched[matched.length - 2].children
      for (let i = 0; i < children.length; i++) {
        arr.push({
          title: children[i].meta.title,
          path: children[i].path
        })
        if (ctx.$router.currentRoute.value.name == children[i].name) {
          state.checkedId = i
        }
      }
      console.log(arr)
      console.log(arr)
    }

    getChildren()

    const [registerTable, { instance, formInstance }] = useTable({
      api: getGroupListById,
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
      abbb(a) {
        console.log(a)
      },
      ...toRefs(state),
      registerEditPage,
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
      edit(data) {
        console.log(data)
      },
      del() {
        console.log('13212312')
      },
      handleRouter(i) {
        ctx.$router.push(state.children[i].path)
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
