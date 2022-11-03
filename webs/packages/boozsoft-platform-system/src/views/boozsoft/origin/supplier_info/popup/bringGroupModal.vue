<template>
  <BasicModal
    width="900px"
    :minHeight="500"
    :height="500"
    v-bind="$attrs"
    :closable="false"
    title="引入集团供应商信息"
    okText="开始引入"
    @ok="handleOk()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height: 25px;font-size: 20px;">
          <SearchOutlined style="width:25px;margin-right: 10px;"/>
          引入集团供应商信息
        </span>
      </div>
    </template>
    <div style="background-color: #158eb8;height: 50px;padding-top: 10px;padding-left:10px;border-radius: 5px;">
      <div style="float: right;margin-right: 5px;">
        <button type="button" class="ant-btn ant-btn-me" @click="reload"><span>刷新</span></button>
      </div>
    </div>
    <div style="display: inline;width: 100%;float: right;margin-top: 5px;">
      <BasicTable @register="registerTable" :scroll="{ y: 400 }"
                  :class="'a-table-font-size-12'" >

      </BasicTable>
    </div>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
import {BasicTable, useTable} from '/@/components/Table';
import {SearchOutlined} from '@ant-design/icons-vue';
import {onMounted, reactive, ref, toRaw, unref} from 'vue';
import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
import {
  Select as ASelect,
  Form as AForm,
  Input as AInput,
  Statistic as AStatistic,
} from 'ant-design-vue';
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  editFlagByCtypeAndOrgUnique,
  findAllByOrgUniqueAndCtypeAndFlag
} from "/@/api/record/system/supplier_group";


const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const AFormItem = AForm.Item;
const {createWarningModal} = useMessage()
const emit = defineEmits(['register']);
const database = ref(getCurrentAccountName(true));
const accId = ref('');
const loading = ref(true);
const orgClassDataList: any = ref([]);
const columns = [
  {
    title: '供应商编码',
    dataIndex: 'custCode',
    key: 'custCode',
    ellipsis: true,
    align: 'left',
    width: 150
  },
  {
    title: '供应商全称',
    dataIndex: 'custName',
    key: 'custName',
    align: 'left',
    ellipsis: true
  }, {
    title: '供应商简称',
    dataIndex: 'custAbbname',
    key: 'custAbbname',
    ellipsis: true, align: 'left',
  }
];

const pageParameter = reactive({
  orgUnique: '',
  ctype: '1',
  flag: '0',
})

// 这是示例组件
const [registerTable, {reload, getSelectRows, getDataSource, setTableData,clearSelectedRowKeys}] = useTable({
  api: findAllByOrgUniqueAndCtypeAndFlag,
  columns: columns,
  loading: loading.value,
  bordered: true,
  rowSelection: { type: 'checkbox' },
  pagination: {
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['50', '100'],
    showTotal: t => `总共${t}条数据`
  },
  searchInfo: pageParameter
});
const [register, {closeModal}] = useModalInner(async (data) => {
  pageParameter.orgUnique=data.orgUnique
  clearSelectedRowKeys()
  reload()
});
async function handleOk() {
  if(getSelectRows().length>0){
    let map={
      flag:'1',
      ctype:'1',
      orgUnique:pageParameter.orgUnique,
      list:[...new Set(getSelectRows().map(a=>a.uniqueCode))],
    }
    // 批量修改集团引入状态
    await editFlagByCtypeAndOrgUnique(map)
    emit('throwData', unref(getSelectRows()));
    closeModal();
  }
}
</script>
<style>
.ant-modal-title {
  margin-top: -10px;
}

.ant-modal-header {
  height: 10px;
}

.scrollbar__view {
  overflow-y: hidden;
}

.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}

.scroll-container .scrollbar__wrap {
  margin-bottom: -5px !important;
}
</style>
<style lang="less" scoped>
:deep(.ant-table-thead th) {
  background-color: #d8d8d8 !important;
  font-weight: bold !important;
  border-left: 1px solid #d8d8d8 !important;
  border-bottom: 1px solid #d8d8d8 !important;
  border-top: 1px solid #d8d8d8 !important;
}

.bg-white {
  width: 220px;
  min-height: 462px;
  height: calc(100% - 462px);
  border: 1px #cccccc solid;
  background: white;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}

:deep(.ant-tree .ant-tree-node-content-wrapper.ant-tree-node-selected),
:deep(.ant-table-tbody > tr.ant-table-row-selected > td) {
  background-color: #1488b1;
  color: white;
}
</style>
