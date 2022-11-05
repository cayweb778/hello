<template>
  <BasicModal
    destroyOnClose
    width="800px"
    v-bind="$attrs"
    @register="register"
  >
    <BasicTable
      ref="tableRef"
      :row-selection="{ type: 'checkbox'}"
      :class="'a-table-font-size-12'"
      :scroll="{ y: 350 }"
      size="small"
      :dataSource="tableData"
      @register="registerTable"
    >

    </BasicTable>
    <template #footer>
      <a-button @click="closeModal()" >取消</a-button>
      <a-button @click="handleOk" type="primary">确定</a-button>
    </template>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
import {ref} from 'vue';
import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
import {
  Input as AInput,
  Radio as ARadio,
  Select as ASelect,
  Statistic as AStatistic,
  Tabs as ATabs,message
} from 'ant-design-vue';
import {useMessage} from "/@/hooks/web/useMessage";
import {BasicTable, useTable} from "/@/components/Table";

const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const AStatisticCountdown = AStatistic.Countdown;
  const ATabPane = ATabs.TabPane;
  const ARadioGroup = ARadio.Group
  const emit=defineEmits(['register','save']);
  // ======================= 参数 ====================
  const btnType = ref('');
  const tableData = ref([]);
  const fuzhuList = ref([]);
  const col:any = ref([
    {
      title: '辅助项编码',
      dataIndex: 'ccode',
      width: 100,
    },
    {
      title: '辅助项名称',
      dataIndex: 'cname',
      width: 100,
    },
  ]);

  const {createConfirm, createWarningModal, createMessage} = useMessage();
  const [registerCodeAllPopPage, { openModal: openCodeAllPopPage }] = useModal();
  const [register, { closeModal }] = useModalInner(async (data) => {
    btnType.value=data.btnType
    fuzhuList.value=data.fuzhuList
    let newTableData=data.tableData
    for (let i = 0; i < newTableData.length; i++) {
      newTableData[i].key='booz'+(i+1)
    }
    if(data.btnType=='add' && data.fuzhuList.length>0){
      // es6 去除两个对象相同项
      newTableData=data.tableData.filter((item)=> !data.fuzhuList.some((ele)=>ele.id===item.id))
    }
    else if(data.btnType=='del'){
      setSelectedRowKeys(newTableData.map(a=>a.key))
    }
    tableData.value=newTableData
  });

  function handleOk() {
    emit('throwData', {data:getSelectRows(),btnType:btnType.value});
    closeModal();
  }
  function fetchSuccess(data) {
  if(data.length<50){
    for (let i =  data.length; i < 50; i++) {
      data.push({flag: '11'})
    }
  }
  setTableData(data)
}
  // 组件实例区
  const [
    registerTable,
    {
      reload,
      getSelectRows,
      setTableData,
      setSelectedRowKeys
    },
  ] =useTable({
    columns: col,
    bordered: true,
    showIndexColumn: true, //显示序号列
    indexColumnProps:{ fixed:true },
    pagination: {
      pageSize: 200,
      simple:true
    },
  });
</script>
<style lang="less" scoped>

  .a-table-font-size-12 :deep(td),
  .a-table-font-size-12 :deep(th) {
    font-size: 14px !important;
    padding: 2px 8px !important;
  }

  :deep(.ant-table-thead th) {
    background-color: #d8d8d8 !important;
    font-weight: bold !important;
    border-left: 1px solid #d8d8d8 !important;
    border-bottom: 1px solid #d8d8d8 !important;
    border-top: 1px solid #d8d8d8 !important;
  }
</style>
