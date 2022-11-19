<template>
  <BasicModal
    width="900px"
    :minHeight="500"
    :height="500"
    v-bind="$attrs"
    title=""
    @ok="handleOk()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height: 25px;font-size: 20px;">
          <SearchOutlined style="width:25px;margin-right: 10px;"/>
          政府经济支出分类
        </span>
      </div>
    </template>
    <div style="background-color: #158eb8;height: 50px;padding-top: 10px;padding-left:10px;border-radius: 5px;">
      <a-select style="width: 100px;" v-model:value="pageParameter.searchConditon.requirement">
        <a-select-option value="ecCode">分类编码</a-select-option>
        <a-select-option value="ecName">分类名称</a-select-option>
        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
      </a-select>
      <a-input-search placeholder="" v-model:value="pageParameter.searchConditon.value" style="width: 200px; border-radius: 4px" @search="query"/>
      <div style="float: right;margin-right: 5px;">
        <button type="button" class="ant-btn ant-btn-me" @click="openAddPage"><span>添加</span></button>
        <button type="button" class="ant-btn ant-btn-me" @click="reload"><span>刷新</span></button>
      </div>
    </div>

    <div class="bg-white" style="display: inline;width: 30%;float: left;margin-top: 5px;">
      <div style="width: 100%; height: 26px;text-align: center;background-color: rgb(216 216 216);">
        <label style="font-size: 14px;font-weight: bold;">政府经济支出分类</label>
      </div>
      <BasicTree
        title=""
        :click-row-to-expand="false"
        :tree-data="treeData"
        v-model:selectedKeys="selectedKeys"
        v-model:expandedKeys="expandedKeys"
        @select="handleSelect"
      />
    </div>

    <div style="display: inline;width: 69.5%;float: right;margin-top: 5px;">
      <BasicTable @register="registerTable" :scroll="{ y: 400 }"
                  :class="'a-table-font-size-12'"
                  :row-selection="{ type: 'radio', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
                  @row-dbClick="rowDbClick">
        <template #ecCode="{ record }" class="a-table-money-font-size a-table-font-arial">
          <span style="float: left" >{{ record.ecCode }}</span>
        </template>
        <template #ecName="{ record }" class="a-table-money-font-size a-table-font-arial">
          <span style="float: left" >{{ record.ecName }}</span>
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

    <AddPage @save="saveData"
             @register="registerSavePage" />
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
import AddPage from './save.vue';
import {BasicTable, useTable} from '/@/components/Table';
import { BasicTree } from '/@/components/Tree';
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
import {
  deleteDept,
  saveDept,
  editFlag,
  getDeptListById,
  GetDeptTree,
  getTotalData,
  GetDeptTreeByFlag
} from '/@/api/record/system/zf-class'
import {columnProps} from "ant-design-vue/es/table/interface";
import {aoaToSheetXlsx} from "/@/components/Excel";
const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const AFormItem = AForm.Item;
const { createErrorModal } = useMessage()
const emit=defineEmits(['register']);
const database = ref(getCurrentAccountName(true));
const accId = ref('');
const loading = ref(true);
const treeData:any = ref([]);
const selectedKeys = ref<string[]>([])
const selectedKeys2:any = ref([]);
const expandedKeys:any = ref([]);
const dataSource:any = ref([]);

const columns: any = [
  {
    title: 'ID',
    dataIndex: 'id',
    defaultHidden: true,
    ellipsis: true
  },
  {
    title: '分类编码',
    dataIndex: 'ecCode',
    ellipsis: true,
    width: '20%',
    slots: { customRender: 'ecCode' },
    customHeaderCell: () => {		// 表头加粗
      return {style: {'font-weight':'bold'}} // return 想要设置的样式
    },
    customCell: () => {		// 在此处可以修改单元格中的样式
      return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
    },
  },
  {
    title: '分类名称',
    dataIndex: 'ecName',
    ellipsis: true,
    align: 'left',
    width: '50%',
    customHeaderCell: () => {		// 表头加粗
      return {style: {'font-weight':'bold'}} // return 想要设置的样式
    },
    slots: {customRender: 'ecName'},
    customCell: () => {		// 在此处可以修改单元格中的样式
      return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
    },
  },
  {
    title: '政府名称',
    dataIndex: 'zfname',
    ellipsis: true,
    align: 'left',
    width: '50%',
    customHeaderCell: () => {		// 表头加粗
      return {style: {'font-weight':'bold'}} // return 想要设置的样式
    },
    slots: {customRender: 'zfname'},
    customCell: () => {		// 在此处可以修改单元格中的样式
      return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
    },
  },
  {
    title: '创建日期',
    dataIndex: 'createDate',
    width: '20%',
    ellipsis: true,
    customHeaderCell: () => {		// 表头加粗
      return {style: {'font-weight':'bold'}} // return 想要设置的样式
    },
    slots: {customRender: 'createDate'}
  },
  {
    title: '状态',
    dataIndex: 'flag',
    width: '15%',
    ellipsis: true,
    customHeaderCell: () => {		// 表头加粗
      return {style: {'font-weight':'bold'}} // return 想要设置的样式
    },
    slots: { customRender: 'flag' }
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
  accId: accId.value,
  username: useUserStoreWidthOut().getUserInfo.username,
  uniqueCustclass:'',
  searchConditon: {
    requirement: 'ecCode',
    value: '',
  },
})

const [registerSavePage, { openModal: openSavePage }] = useModal();
// 这是示例组件
const [registerTable, { reload,getSelectRows,getDataSource,setTableData }] = useTable({
  api: useRouteApi(getDeptListById,{schemaName: database}),
  columns: columns,
  loading: loading.value,
  bordered: true,
  pagination:{ pageSize: 50,showSizeChanger: true, pageSizeOptions: ['50','100'],showTotal: t => `总共${t}条数据` },
  searchInfo: pageParameter
});
const [register, { closeModal }] = useModalInner( async (data) => {
  database.value=data.database
  accId.value=data.accId
  reload()
  await fetch()
});


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
async function openAddPage() {
  let temp=await findAllByAccCountPartColumn(accId.value)
  openSavePage(true, {
    data: val,
    type: 'add',
    defaultPage:temp.independent=='1'?true:false, // 是否独立账套
    database:database.value,
    dynamicTenantId:database.value,
    isEdit:true
  });
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


const exportExcelNow = async () => {
  const data = JSON.parse(JSON.stringify(getDataSource()))
  // debugger
  const arrHeader = ['分类编码','分类名称','上级名称','状态'];
  const columnList = ['ecCode','ecName','pname','flag']
  data.forEach(v=>{
    v.flag = formatFlag(v.flag )
  })
  const arrData = data.map((item) => columnList.map(column=>item[column]));
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: arrData,
    header: arrHeader,
    filename: '政府支出功能分类.xlsx',
  });
}
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

async function fetch() {
  let deptTree = await useRouteApi(GetDeptTreeByFlag,{schemaName: database})({})
  function a(deptTree: any) {
    deptTree.forEach((item: any) => {
      if (item.children != null) {
        a(item.children)
      }
      item.title = '[' + item.ecCode + ']  ' + item.ecName
      item.key = item.ecCode;
    })
  }

  a(deptTree)

  treeData.value = []
  treeData.value.push({title: '全部',key:'0',children: deptTree})
  selectedKeys.value=['0']
  expandedKeys.value=['0']
}

// 选中分类
async function handleSelect(obj) {
  console.log(obj[0])
  if(obj[0].toString()!==''){
   let d = await useRouteApi(getDeptListById,{schemaName: database})(pageParameter)
    setTableData(d.items.filter(item=> {
      return item.ecCode.indexOf(obj[0])!=-1
    }))
  }else{
    pageParameter.uniqueCustclass='0'
    selectedKeys2.value='0'
  }
}

async function saveData(data) {
  await useRouteApi(saveDept, { schemaName: database })(data);
  reload()
  await fetch()
  if (data.closeflag === 'add') {
    openAddPage()
  }
}

async function query(data) {
  console.log(data)
  let d = await useRouteApi(getDeptListById,{schemaName: database})(pageParameter)
  setTableData(d.items.filter(item=> {
    if(pageParameter.searchConditon.requirement === 'ecCode'){
      return item.ecCode.indexOf(data)!=-1
    }
    if(pageParameter.searchConditon.requirement === 'ecName'){
      return item.ecName.indexOf(data)!=-1
    }
    return false
  }))
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
