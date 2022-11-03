<template>
  <div style="padding: 5px 10px;">
    <BasicTable
      ref="tableRef"
      class="alone-basic-table"
      :scroll="{ y: windowHeight }"
      size="small"
      :row-selection="{ type: 'radio',fixed: true,onChange: selectChange }"
      @register="registerTable"
    >
<!--      <template #ibook="{ record }">
        <a-tag :color="record.ibook === '1' ? 'green' : 'red'">
          {{ record.ibook === '1' ? '已记账' : '未记账' }}
        </a-tag>
      </template>-->
    </BasicTable>
  </div>
</template>

<script setup="props, {content}" lang="ts">
import {inject, onMounted, ref, unref} from 'vue';
import {BasicTable, useTable} from '/@/components/Table'
const emit=defineEmits(['edit','del'])
const pageParameter = inject('pageParameter')
const tableSelectedRowObjs = inject('tableSelectedRowObjs')
const windowWidth = (document.documentElement.clientWidth - 70)
const windowHeight = (window.innerHeight - 290)
const tableSelectedRowKeys = ref([])
// 组件实例区
const [registerTable,{setTableData}] = useTable({
  columns: [ {
    title: '编码',
    dataIndex: 'templateNum',
    width: '100px',
  }, {
    title: '名称',
    dataIndex: 'templateName',
    width: '300px',
    align: 'center',
  }, {
    title: '转账类型',
    dataIndex: 'carryOverType',
    width: '150px',
    format: (t)=> t == '1'?'公式结转':'对应结转'
  }, {
    title: '设立人',
    dataIndex: 'createMan',
    width: '200px',
  }, {
    title: '设立时间',
    dataIndex: 'createDate',
    width: '200px',
  },],
  bordered: true,
  immediate: false,
  canResize: true,
  showIndexColumn: true, //显示序号列
  indexColumnProps: {width: 60,fixed: 'left'},
  pagination: {
    pageSize: 50,
    simple: true,
  }
})
const selectChange = (k,o) => {
  tableSelectedRowObjs.value = o
}
onMounted(()=>setTableData(pageParameter.templateList))
</script>
<style lang="less" scoped="scoped">
@import '/@/assets/styles/alone-basic-table.less';
:deep(.vben-basic-table) .ant-pagination {
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
}
</style>
