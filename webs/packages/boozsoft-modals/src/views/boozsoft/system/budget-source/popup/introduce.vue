<template>
  <BasicModal
    width="900px"
    :minHeight="500"
    :height="500"
    v-bind="$attrs"
    title="回收站"
    @ok="handleOk()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height: 25px;font-size: 20px;">
          <SearchOutlined style="width:25px;margin-right: 10px;"/>
          预算来源引入
        </span>
      </div>
    </template>
    <div style="background-color: #158eb8;height: 50px;padding-top: 10px;padding-left:10px;border-radius: 5px;">
      <a-select style="width: 100px;" v-model:value="pageParameter.searchConditon.requirement">
        <a-select-option value="bsCode">来源编码</a-select-option>
        <a-select-option value="bsName">来源名称</a-select-option>
        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
      </a-select>
      <a-input-search placeholder="" v-model:value="pageParameter.searchConditon.value" style="width: 200px; border-radius: 4px" @search="reload()"/>
      <div style="float: right;margin-right: 5px;">
        <button type="button" class="ant-btn ant-btn-me" @click="add"><span>引入</span></button>
        <button type="button" class="ant-btn ant-btn-me" @click="reload"><span>刷新</span></button>
      </div>
    </div>

    <div style="display: inline;width: 100%;float: right;margin-top: 5px;">
      <BasicTable @register="registerTable" :scroll="{ y: 400 }"
                  :class="'a-table-font-size-12'"
                  :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }">
        <template #bsCode="{ record }" class="a-table-money-font-size a-table-font-arial">
          <span style="float: left" >{{ record.bsCode }}</span>
        </template>
        <template #bsName="{ record }" class="a-table-money-font-size a-table-font-arial">
          <span style="float: left" >{{ record.bsName }}</span>
        </template>
        <template #flag="{ record }">
            <span>
              <a-tag :color="record.flag === '1' ? 'green' : 'volcano'">
                {{ record.flag === '1' ? '启用' : '停用' }}
              </a-tag>
            </span>
        </template>
      </BasicTable>
    </div>

    <template #footer>
      <a-button @click="handleOk()" type="primary">关闭</a-button>
    </template>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
import {BasicTable, useTable} from '/@/components/Table';
import { SearchOutlined,CaretDownOutlined,DeleteOutlined  } from '@ant-design/icons-vue';
import { onMounted, reactive, ref, toRaw, unref } from 'vue';
import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
import {
  Select as ASelect,
  Form as AForm,
  Input as AInput,
  Statistic as AStatistic, message, Tag as ATag
} from 'ant-design-vue';
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useMessage} from "/@/hooks/web/useMessage";
import {findAllByAccCountPartColumn} from "/@/api/record/system/account";
import {useUserStoreWidthOut} from "/@/store/modules/user";
/*import {
  findDelAll,
  deleteList,
  reductionList,
  deleteSettModes, saveSettModes, findSettModesAll
} from "/@/api/record/system/group-budget-source";*/

import { introduceData, findSettModesInAll } from "/@/api/record/system/group-budget-source";

import {aoaToSheetXlsx} from "/@/components/Excel";
import {columnProps} from "ant-design-vue/es/table/interface";
const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const AFormItem = AForm.Item;
const { createErrorModal } = useMessage()
const emit=defineEmits(['register']);
const database = ref(getCurrentAccountName(true));
const loading = ref(true);
const treeData:any = ref([]);
const selectedKeys2:any = ref([]);
const expandedKeys:any = ref([]);
const dataSource:any = ref([]);

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    defaultHidden: true,
    ellipsis: true
  },
  {
    title: '预算来源编码',
    dataIndex: 'bsCode',
    width: '15%',
    slots: { customRender: 'bsCode' },
    customHeaderCell: () => {		// 表头加粗
      return {style: {'font-weight':'bold'}} // return 想要设置的样式
    },
    ellipsis: true
  },
  {
    title: '预算来源名称',
    dataIndex: 'bsName',
    width: '40%',
    customHeaderCell: () => {		// 表头加粗
      return {style: {'font-weight':'bold'}} // return 想要设置的样式
    },
    ellipsis: true,
    slots: {customRender: 'bsName'}
  },
  {
    title: '是否启用',
    dataIndex: 'flag',
    width: '15%',
    ellipsis: true,
    customHeaderCell: () => {		// 表头加粗
      return {style: {'font-weight':'bold'}} // return 想要设置的样式
    },
    slots: {customRender: 'flag'}
  }
]

const selectedRowKeys:any = ref([]);
const selectedRowData:any = ref({});
const styleList:any = ref([]);

//选中内容
type Key = columnProps['id'];

const state = reactive<{
  selectedRowKeys: Key[];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const checkRow: any = ref([])

const onSelectChange = (selectedRowKeys, row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
};

const pageParameter = reactive({
  showRulesSize: 'MIN',
  companyCode: '',
  companyName: '',
  ifUnit: false,
  total: 0,
  thisAdInfo: {},
  thisLeftKey: '',
  flag:'1',
  database: database.value,
  username: useUserStoreWidthOut().getUserInfo.username,
  uniqueCustclass:'',
  searchConditon: {
    requirement: 'bsCode',
    value: '',
  },
})

const [registerSavePage, { openModal: openSavePage }] = useModal();
// 这是示例组件
const [registerTable, { reload,getSelectRows,getDataSource,setTableData }] = useTable({
  api: useRouteApi(findSettModesInAll,{schemaName: database}),
  columns: columns,
  loading: loading.value,
  bordered: true,
  pagination:{ pageSize: 50,showSizeChanger: true, pageSizeOptions: ['50','100'],showTotal: t => `总共${t}条数据` },
  searchInfo: pageParameter
});
const [register, { closeModal }] = useModalInner( async (data) => {
  database.value = data.database
  reload()
});

async function handleOk() {
  if(selectedRowData.value!==''){
    emit('throwData', unref(selectedRowData.value[0]));
    closeModal();
  }
}

const val = {
  id: '',
  uniqueCode: '',
  custName: '',
  uniqueCustclass: '',
  custCode: '',
  custAbbname: '',
  custSregcode: '',
  manageType: '',
  uniqueCodeCcus: '',
  uniqueCodeSupplier: '',
  address1: '',
  contacts: '',
  address2: '',
  telephone: '',
  countryName: '',
  cellPhoneNum: '',
  website: '',
  email: '',
  industryclassName: '',
  successState: '',
};

async function add() {
  if (checkRow.value.length > 0) {
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '你确认要引用选中数据吗?',
      onOk: async () => {
        let arr = []
        for (let i = 0; i < checkRow.value.length; i++) {
          arr.push(checkRow.value[i].id)
        }
        console.log(database.value)
        await useRouteApi(introduceData,{schemaName: database})(checkRow.value)
        checkRow.value = []
        state.selectedRowKeys = []
        message.success('引用成功！')
        await reload()
      },
      onCancel: () => {
        return false
      }
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '引用',
      content: '请选择需要引用的数据！'
    })
  }
}

//js切割字符串
function setString(str, len) {
  var strlen = 0;
  var s = '';
  for (var i = 0; i < str.length; i++) {
    if (str.charCodeAt(i) > 128) {
      strlen += 2;
    } else {
      strlen += 1.2;
    }
    s += str.charAt(i);
    if (strlen >= len) {
      return s + '...';
    }
  }
  return s;
}

function createErrorPop(text) {
  createErrorModal({
    iconType: 'warning',
    title: '提示',
    content: text
  })
  return false
}

const {
  createConfirm,
  createWarningModal
} = useMessage()



function formatFlag(flag:any) {
  let str = '启用'
  switch (flag) {
    case '1':
      str = '启用'
      break
    case '0':
      str = '停用'
      break
  }
  return str
}


</script>
<style>
.ant-modal-title{
  margin-top: -10px;
}
.ant-modal-header{
  height: 10px;
}
.scrollbar__view{
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
.bg-white{
  width: 220px ;
  min-height: 462px ;
  height: calc(100% - 462px);
  border: 1px #cccccc solid;
  background:white ;
}
.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}
:deep(.ant-tree .ant-tree-node-content-wrapper.ant-tree-node-selected),
:deep(.ant-table-tbody > tr.ant-table-row-selected > td){
  background-color: #1488b1;
  color: white;
}
</style>
