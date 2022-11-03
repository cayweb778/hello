<!--<template>-->
<!--  <div class="p-4">-->
<!--    <Edit-->
<!--      @save="saveMenu"-->
<!--      @register="registerEditPage"-->
<!--    />-->
<!--    <a-button type="primary" class="my-4" @click="openAddPage()">新增</a-button>-->
<!--    <BasicTable-->
<!--      :row-selection="{ type: 'checkbox' ,fixed: true}"-->
<!--      title="菜单档案"-->
<!--      title-help-message="树形组件不能和序列号列同时存在"-->
<!--      :columns="columns"-->
<!--      :data-source="data"-->
<!--      :pagination="{ current: 2 }"-->
<!--      row-key="id"-->
<!--      :indent-size="20"-->
<!--      @register="registerTable"-->
<!--    >-->
<!--      <template #action="{ record }">-->
<!--        <TableAction-->
<!--          :width="'500px'"-->
<!--          :actions="[-->
<!--            {-->
<!--              label: '编辑',-->
<!--              icon: 'ic:outline-delete-outline',-->
<!--              onClick: openModifyPage.bind(null, record),-->
<!--            },{-->
<!--              label: '删除',-->
<!--              icon: 'ic:outline-delete-outline',-->
<!--              // bind第二个参数对应你的方法第一个参数，第3个参数对应你的方法第二参数 你parentId只是参数声名 又没穿  bye-->
<!--              onClick: delMenu.bind(null, record),-->
<!--            },-->
<!--          ]"-->
<!--        />-->
<!--      </template>-->
<!--    </BasicTable>-->
<!--  </div>-->
<!--</template>-->
<!--<script lang="ts">-->
<!--import { defineComponent, getCurrentInstance, reactive, toRefs } from 'vue'-->
<!--import { BasicTable, useTable } from '/@/components/Table'-->
<!--import { getMenuListById, GetMenuTree } from '/@/api/sys/menu'-->
<!--import TableAction from '/@/components/Table/src/components/TableAction.vue'-->
<!--import { deleteMenu, saveMenu } from '/@/api/record/system/menu'-->
<!--import Edit from '/@/views/boozsoft/prod/system/modules/menu/popup/copy.vue'-->
<!--import { useModal } from '/@/components/Modal'-->

<!--const columns = [-->
<!--  { title: 'id', dataIndex: 'id', ellipsis: true },-->
<!--  { title: '上级ID', dataIndex: 'parentId', ellipsis: true },-->
<!--  {-->
<!--    title: '菜单名称',-->
<!--    dataIndex: 'name',-->
<!--    ellipsis: true-->
<!--  },-->
<!--  {-->
<!--    title: '菜单别名',-->
<!--    dataIndex: 'alias'-->

<!--  },-->
<!--  {-->
<!--    title: '路径',-->
<!--    dataIndex: 'path'-->
<!--  },-->
<!--  {-->
<!--    title: '菜单资源',-->
<!--    dataIndex: 'source'-->
<!--  },-->
<!--  {-->
<!--    title: '排序',-->
<!--    dataIndex: 'sort'-->
<!--  }-->
<!--  // {-->
<!--  //   title: '菜单类型',-->
<!--  //   dataIndex: 'category',-->
<!--  //   width: 100-->
<!--  // },-->
<!--  // {-->
<!--  //   title: '操作按钮类型',-->
<!--  //   dataIndex: 'action',-->
<!--  //   width: 100-->
<!--  // },-->
<!--  // {-->
<!--  //   title: '是否打开新页面',-->
<!--  //   dataIndex: 'isopen',-->
<!--  //   width: 100-->
<!--  // },-->
<!--  // {-->
<!--  //   title: '备注',-->
<!--  //   dataIndex: 'remark',-->
<!--  //   width: 100-->
<!--  // }-->

<!--]-->

<!--const val = {-->
<!--  id: '',-->
<!--  parentId: '',-->
<!--  code: '',-->
<!--  name: '',-->
<!--  alias: '',-->
<!--  path: '',-->
<!--  source: '',-->
<!--  sort: '',-->
<!--  category: '',-->
<!--  action: '',-->
<!--  isOpen: '',-->
<!--  remark: '',-->
<!--  isDeleted: ''-->
<!--}-->
<!--export default defineComponent({-->
<!--  components: { Edit, BasicTable, TableAction },-->
<!--  setup() {-->
<!--    const state = reactive({-->
<!--      data: [],-->
<!--      editData: {-->
<!--        id: 3333-->
<!--      }-->
<!--    })-->
<!--    const { ctx } = getCurrentInstance()-->

<!--    const [registerTable, { instance, formInstance, reload }] = useTable({-->
<!--      api: GetMenuTree,-->
<!--      columns: columns,-->
<!--      bordered: true,-->
<!--      actionColumn: {-->
<!--        width: 240,-->
<!--        title: '操作',-->
<!--        dataIndex: 'action',-->
<!--        slots: { customRender: 'action' }-->
<!--      }-->
<!--    })-->
<!--    // 这是示例组件-->
<!--    const [registerEditPage, { openModal: openEditPage }] = useModal()-->
<!--    return {-->
<!--      registerTable,-->
<!--      registerEditPage,-->
<!--      async saveMenu(data) {-->
<!--        console.log(data)-->
<!--        await saveMenu(data)-->
<!--        // createGroupInfo(a)-->
<!--        reload(data)-->
<!--      },-->
<!--      ...toRefs(state),-->
<!--      openAddPage() {-->
<!--        openEditPage(true, {-->
<!--          data: val-->
<!--        })-->
<!--      },-->
<!--      openModifyPage(a, b) {-->
<!--        openEditPage(true, {-->
<!--          data: a-->
<!--        })-->
<!--      },-->
<!--      async delMenu(record) {-->
<!--        if (record.children.length > 0) {-->
<!--          alert('该菜单已存在子级带单,不能删除')-->
<!--        } else {-->
<!--          deleteMenu(record.id)-->
<!--        }-->
<!--      }-->

<!--    }-->
<!--  }-->
<!--})-->
<!--</script>-->
