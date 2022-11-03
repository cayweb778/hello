<template>
  <BasicModal
    width="900px"
    :minHeight="500"
    :height="500"
    v-bind="$attrs"
    title="选择科目"
    @ok="handleOk()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height: 25px;font-size: 20px;">
          <SearchOutlined style="width:25px;margin-right: 10px;"/>
          选择客户
        </span>
      </div>
    </template>
    <div style="background-color: #158eb8;height: 50px;padding-top: 10px;padding-left:10px;border-radius: 5px;">
      <a-select style="width: 100px;" v-model:value="pageParameter.searchConditon.requirement">
        <a-select-option value="custCode">客户编码</a-select-option>
        <a-select-option value="custName">客户全称</a-select-option>
        <a-select-option value="custAbbname">客户简称</a-select-option>
        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
      </a-select>
      <a-input-search placeholder="" v-model:value="pageParameter.searchConditon.value" style="width: 200px; border-radius: 4px" @search="reload()"/>
    </div>
    <div class="bg-white" style="display: inline;width: 30%;float: left;margin-top: 5px;">
      <div style="width: 100%; height: 26px;text-align: center;background-color: rgb(216 216 216);">
        <label style="font-size: 14px;font-weight: bold;">客户分类</label>
      </div>
      <BasicTree
        defaultExpandAll
        :click-row-to-expand="false"
        :tree-data="treeData"
        :loading="loading"
        v-model:selectedKeys="selectedKeys2"
        v-model:expandedKeys="expandedKeys"
        @select="handleSelect"
      />
    </div>
    <div style="display: inline;width: 69.5%;float: right;margin-top: 5px;">
      <BasicTable @register="registerTable" :scroll="{ y: 400 }" :class="'a-table-font-size-12'" :row-selection="{ type: 'radio',selectedRowKeys: selectedRowKeys, onChange: onSelectChange}" @row-dbClick="rowDbClick">
        <template #manageType="{ record }">
          <span v-if="record.manageType==='0'">内部客户</span>
          <span v-if="record.manageType==='1'">外部客户</span>
        </template>
      </BasicTable>
    </div>
    <AddPage @save="saveData"
             @dulisave="dulisaveData"
             @register="registerSavePage" />
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
import {CaretDownOutlined, SearchOutlined} from '@ant-design/icons-vue'
import AddPage from './edit.vue';
import {BasicTable, useTable} from '/@/components/Table';
import {BasicTree} from '/@/components/Tree';
import {reactive, ref, unref} from 'vue';
import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
import {
  Form as AForm,
  Input as AInput,
  Select as ASelect,
  Statistic as AStatistic,
} from 'ant-design-vue';
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {useMessage} from "/@/hooks/web/useMessage";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {GetCustomerClassTree} from "/@/api/record/system/customer_class_group";
import {findAll} from "/@/api/record/system/customer_group";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";


const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const AFormItem = AForm.Item;
const { createErrorModal } = useMessage()
const emit=defineEmits(['register']);
const database = ref(getCurrentAccountName(true));
// 1独立账套。0集团账套
const independent = ref('0');
const accId = ref('');
const loading = ref(true);
const treeData:any = ref([]);
const selectedKeys2:any = ref([]);
const expandedKeys:any = ref([]);
const dataSource:any = ref([]);
const columns = [
  {
    title: '客户编码',
    dataIndex: 'custCode',
    key: 'custCode',
    ellipsis:true,
    align:'left',
    width:150
  },
  {
    title: '客户全称',
    dataIndex: 'custName',
    key: 'custName',
    align:'left',
    ellipsis:true
  },{
    title: '客户简称',
    dataIndex: 'custAbbname',
    key: 'custAbbname',
    ellipsis:true,align:'left',
  },{
    title: '税号',
    dataIndex: 'custSregcode',
    key: 'custSregcode',
    ellipsis:true,align:'left',
  },{
    title: '分类名称',
    dataIndex: 'uniqueCustclassName',
    key: 'uniqueCustclassName',
    ellipsis:true,width:80,align:'left',
  },{
    title: '管理类型',
    dataIndex: 'manageType',
    key: 'manageType',align:'left',
    ellipsis:true,width:80,slots: { customRender: 'manageType' },
  }
];
const selectedRowKeys:any = ref([]);
const selectedRowData:any = ref({});
const styleList:any = ref([]);

const pageParameter = reactive({
  searchConditon: {
    requirement: 'custName',
    value: '',
  },
  showRulesSize: 'MIN',
  uniqueCustclass:'0',
  database: useCompanyOperateStoreWidthOut().getSchemaName,
  username: useUserStoreWidthOut().getUserInfo.username,
  flag:'1'
})
const CrudApi = {
  list: findAll,
  columns: [
    {
      title: '客户编码',
      dataIndex: 'custCode',
      ellipsis: true,
      slots: { customRender: 'custCode' }
    },
    {
      title: '客户全称',
      dataIndex: 'custName',
      ellipsis: true,
      slots: { customRender: 'custName' },
    },
    {
      title: '客户简称',
      dataIndex: 'custAbbname',
      ellipsis: true,
    },
    {
      title: '税号',
      dataIndex: 'custSregcode',
      ellipsis: true,
    },
    {
      title: '分类名称',
      dataIndex: 'uniqueCustclassName',
      ellipsis: true,
    },
    {
      title: '管理类型',
      dataIndex: 'manageType',
      ellipsis: true,
      slots: { customRender: 'manageType' },
    },{
      title: '行业性质',
      dataIndex: 'industryclassName',
      ellipsis: true,
    }
  ],
};
const [registerSavePage, { openModal: openSavePage }] = useModal();
// 这是示例组件
const [registerTable, { reload,getSelectRows,getDataSource,setTableData,setPagination }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  pagination:{ pageSize: 50,showSizeChanger: true, pageSizeOptions: ['50','100'],showTotal: t => `总共${t}条数据` },
  searchInfo: pageParameter,
});
const [register, { closeModal }] = useModalInner( async (data) => {
  fetch()
});

function onSelectChange(a,b) {
  if(a.length>0){
    selectedRowKeys.value=a
    selectedRowData.value=b
  }
}
function fetchsuccess(obj) {
  let arr=obj.items.filter(v=>v.isDel!=='1')
  setTableData(arr)
  setPagination({total: arr.length})
}

// tree
async function fetch() {
  treeData.value=[]
  const deptTree = await GetCustomerClassTree()
  function a(customerTree) {
    customerTree.forEach((item) => {
      if (item.children != null) {
        a(item.children);
      }
      item.title = '[' + item.cusClass + '] ' + item.cusCclassName;
      item.key = item.uniqueCustclass;
    });
  }

  a(deptTree);
  treeData.value = []
  treeData.value.push({title: '全部',key:'0',children: deptTree})
  selectedKeys2.value=['0']
  expandedKeys.value=['0']
}


// 选中分类
function handleSelect(obj) {
  if(obj.toString()!==''){
    pageParameter.uniqueCustclass=obj.toString()
  }else{
    pageParameter.uniqueCustclass='0'
    selectedKeys2.value='0'
  }
  reload();
}
async function handleOk() {
  if(selectedRowData.value!==''){
    emit('throwData', unref(selectedRowData.value[0]));
    closeModal();
  }
}

function rowDbClick(data) {
  selectedRowData.value=[data]
  handleOk()
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
