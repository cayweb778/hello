<template>
  <div>
    <!--    <div style="float:left;width:10%;height:500px;background: white;border-right:solid 1px black ">集团</div>-->
    <!--    <div style="float:left;width:10%;height:500px;background: white;border-right:solid 1px black ">组织</div>-->
    <!--    <div style="float:left;width:10%;height:500px;background: white;border-right:solid 1px black ">公司</div>-->
    <!--    -->
    <!--    <div style="float:left;width:70%">-->
    <div class="p-4">

      <Edit
        @save="saveJigou"
        @register="registerEditPage"
      />
      <div>
        <a-button type="primary" class="my-4" @click="openAddPage()">新增</a-button>
        <div style="float: right">
          <select>
            <option>集团</option>
            <option>组织</option>
            <option>公司</option>
            <option>工厂</option>
            <option>组织架构</option>
          </select>
        </div>
      </div>
      <BasicTable
        :row-selection="{ type: 'checkbox' ,fixed: true}"
        title="机构档案"
        title-help-message="树形组件不能和序列号列同时存在"
        :columns="columns"
        :data-source="data"
        :pagination="{ current: 2 }"
        row-key="id"
        :indent-size="20"
        @register="registerTable"
      >
        <template #action="{ record }">
          <TableAction
            :width="'500px'"
            :actions="[
              {
                label: '编辑',
                icon: 'ic:outline-delete-outline',
                onClick: openModifyPage.bind(null,record),
              },{
                label: '删除',
                icon: 'ic:outline-delete-outline',
                onClick: delJigou.bind(null, record),
              },
            ]"
          />
        </template>
      </BasicTable>
    </div>
    <!--    </div>-->
  </div>
</template>
<script lang="ts">
import { defineComponent, getCurrentInstance, reactive, toRefs } from 'vue'
import { BasicTable, useTable } from '/@/components/Table'
import { getJigouListById, GetJigouTree } from '/@/api/sys/jigou'
import TableAction from '/@/components/Table/src/components/TableAction.vue'
import { deleteJigou, saveJigou } from '/@/api/record/system/jigou'
import Edit from './popup/edit.vue'
import { useModal } from '/@/components/Modal'

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    ellipsis: true
  }, {
    title: '上级ID',
    dataIndex: 'parentId',
    ellipsis: true
  }, {
    title: '租户ID',
    dataIndex: 'tenantId',
    ellipsis: true
  }, {
    title: '机构名称',
    dataIndex: 'deptName',
    ellipsis: true
  },
  {
    title: '部门全称',
    dataIndex: 'fullName',
    ellipsis: true
  },
  {
    title: '排序',
    dataIndex: 'sort',
    ellipsis: true
  },
  {
    title: '类型',
    dataIndex: 'deptType',
    ellipsis: true
  }
  // ,
  // {
  //   title: '备注',
  //   dataIndex: 'remark',
  //   ellipsis: true
  // }
]
const val = {
  id: '',
  tenantId: '',
  parentId: '',
  deptName: '',
  deptType: '',
  fullName: '',
  sort: '',
  remark: '',
  isDeleted: ''

}
export default defineComponent({
  components: { Edit, TableAction, BasicTable },
  setup() {
    const state = reactive({
      data: [],
      editData: {
        id: 3333
      }
    })
    const { ctx } = getCurrentInstance()

    const [registerTable, { instance, formInstance, reload }] = useTable({
      api: GetJigouTree,
      columns: columns,
      bordered: true,
      actionColumn: {
        width: 240,
        title: '操作',
        dataIndex: 'action',
        slots: { customRender: 'action' }
      }
    })
    // 这是示例组件
    const [registerEditPage, { openModal: openEditPage }] = useModal()
    return {
      registerTable,
      registerEditPage,
      ...toRefs(state),
      async saveJigou(data) {
        console.log('123456')
        await saveJigou(data)
        // createGroupInfo(a)
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
      async delJigou(record) {
        if (record.parentId == 0) {
          alert('该菜单已存在子级带单,不能删除')
        } else {
          await deleteJigou(record.id)
          reload(data)
        }
      }
    }
  }
})
</script>
