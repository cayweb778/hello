<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">客户组</b>
        </div>
        <div class="ant-btn-group" style="float: right;">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="modalShow()"
          ><span>新增</span></button>
          <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false" @click="delRowData"><span>删除</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/one/home/welcome')"><span>退出</span></button>
        </div>
      </div>
      <div style="clear: none"/>
      <div style="margin-top: -30px;margin-left: 80px;">
        <div style="display: inline-block;float: left;margin-top: -25px;">
          <AccountPicker theme="three" @reloadTable="dynamicAdReload"/>
        </div>
<!--        <div style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">共 {{custCountAllList}} 条记录</div>-->
        <div style="float: right; margin-left: 10px;">
          <a-button @click="pageReload">
            <SyncOutlined :style="{ fontSize: '14px' }" />
          </a-button>
        </div>
        <div style="float: right; position: relative">
          <span style="float: left; position: relative">
            <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 130px;" class="special_select">
              <template v-for="item in searchConditonList">
                <a-select-option :value="item.dataIndex">
                  {{ item.title }}
                </a-select-option>
              </template>
            </a-select>
            <a-input-search
              placeholder=""
              v-model:value="pageParameter.searchConditon.value"
              @search="findByCusAuthData"
              style="width: 150px;border-radius: 4px"
            />
          </span>
        </div>
      </div>
      <div style="clear:both"/>
    </div>
    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <div class="bg-white m-4 mr-0 overflow-hidden" style="margin-top: -0.5px;margin-left: -5px;">
          <div style="width: 100%; height: 32px;background-color: #d9d9d9;padding-top:5px;font-weight: bold;text-align: center;">
            客户组名称
            <div style="float: right;">
              <a style="color: black;font-size: 14px;" @click="openTreeAddPage()"><PlusCircleTwoTone /></a>&emsp;
              <a style="color: black;font-size: 14px;" @click="delGroup(selectedKeys2)"><MinusCircleTwoTone /></a>&emsp;
            </div>
          </div>
          <BasicTree
            defaultExpandAll
            :click-row-to-expand="false"
            :tree-data="treeData"
            v-if="treeData.length"
            v-model:selectedKeys="selectedKeys2"
            v-model:expandedKeys="expandedKeys"
            @select="handleSelect"
          />
        </div>
        <div style="width: calc(100% - 250px); float: right;margin-left: 5px;">
          <BasicTable
            ref="tableRef"
            :bordered="true"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :scroll="{ y: windowHeight }"
            :row-selection="{ type: 'checkbox',getCheckboxProps:rowSelection.getCheckboxProps }"
            @register="registerTable"
            @row-click="rowClick"
          >
            <template #manageType="{ record }">
              <span v-if="record.manageType==='0'">内部客户</span>
              <span v-if="record.manageType==='1'">外部客户</span>
            </template>
            <template #custCode="{ record }">
              <a style="cursor: pointer;" @click="openLook(record,false)">{{ record.custCode}}</a>
            </template>

            <template #custName="{ record }">
              <a style="cursor: pointer;" @click="openLook(record,false)">{{ record.custName}}</a>
            </template>
          </BasicTable>
          <div class="pagination-text" :style="{top: (windowHeight+45)+'px'}" v-show="showPaginationText">
            {{`共 ${custCountAllList}条记录&emsp;每页 200 条`}}
          </div>
        </div>
      </PageWrapper>
    </div>
    <Edit
      @register="registerEditPage"
    />
    <GroupSave @save="fetch" @register="registerGroupSavePage" />
    <CustModalPop @throwData="modalData" @register="registerCustModalPopPage" />

  </div>
</template>
<script setup lang="ts">
import router from "/@/router";
import {BasicTree} from '/@/components/Tree';
import {BasicTable, useTable} from '/@/components/Table';
import {PageWrapper} from '/@/components/Page';
import {useModal} from '/@/components/Modal';
import {
  MinusCircleTwoTone,
  PlusCircleTwoTone,
  ProfileOutlined,
  SyncOutlined
} from '@ant-design/icons-vue';
import {editCusFlag, findAll} from '/@/api/record/costomer_data/customer';
import {delCusGroup} from '/@/api/record/system/customer_group';
import Edit from './popup/edit.vue';
import CustModalPop from '/@/views/boozsoft/system/customer_info/popup/modalPop.vue';
import GroupSave from './popup/group_save.vue';
import {
  Input as AInput,
  message,
  Radio as ARadio,
  Select as ASelect,
  Statistic as AStatistic,
  Tabs as ATabs,
  Tag as ATag,
} from 'ant-design-vue';
import {useUserStoreWidthOut} from '/@/store/modules/user';
import {reactive, ref} from 'vue';
import {initDynamics} from "/@/views/boozsoft/system/customer_info_group/data";
import {useMessage} from "/@/hooks/web/useMessage";
import {getCurrentAccountName, hasBlank} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import {useTabs} from "/@/hooks/web/useTabs";
import {useRoute} from "vue-router";
import {
  delByStockCustCcode, deleteByCcodeGroupCcode, delStockCustGroupsByCustCode,
  findAllStockCust, findAllStockCustGroups,
  findAllStockCustGroupsCustCode, saveStockCustGroupsList
} from "/@/api/record/system/customer_zu";

const { createConfirm,createWarningModal,createMessage } = useMessage();
const searchConditonList = ref([
  {
    title: '客户编码',
    dataIndex: 'custCode',
    ellipsis: true,
    align:'left',
    slots: { customRender: 'custCode' },
  },
  {
    title: '客户全称',
    dataIndex: 'custName',
    ellipsis: true,
    slots: { customRender: 'custName' },
    align:'left',
  },
  {
    title: '客户简称',
    dataIndex: 'custAbbname',
    ellipsis: true,align:'left',
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
  },
])
const treeData:any = ref([]);
const selectedKeys2:any = ref<string[]>([])
const expandedKeys:any = ref([]);
const tableshow = ref(false);
const windowWidth  = (window.innerWidth -(70+280))
const windowHeight = (window.innerHeight - (310))
const totalColumnWidth = ref(0)
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});
const tableRef = ref(null)
const visible = ref(false);
const lanMuData = {'accId':'','menuName':'客户信息','type':'账套',objects: '',username:useUserStoreWidthOut().getUserInfo.username}
const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const ATabPane = ATabs.TabPane;
const independent = ref(true);
const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
const database=ref(getCurrentAccountName(true))
const accId=ref('')
const accountInfo=ref({})
const databaseName=ref('')
const defaultPage = ref(true)   // 先判断是否独立账套,true 是；false 否
const loadMark =ref(false)
const custCountAllList = ref(0)
const targetKeys = ref([])
const selectedKeys = ref([])
const { closeCurrent } =useTabs();
const CrudApi = {
  columns: [
    {
      title: '客户组名称',
      dataIndex: 'custGroupName',
      ellipsis: true,
      align:'left',
      width: 150,
    },
    {
      title: '客户编码',
      dataIndex: 'custCode',
      ellipsis: true,
      align:'left',
      width: 150,
      slots: { customRender: 'custCode' },
    },
    {
      title: '客户全称',
      dataIndex: 'custName',
      ellipsis: true,
      slots: { customRender: 'custName' },
      align:'left',
    },
    {
      title: '客户简称',
      dataIndex: 'custAbbname',
      ellipsis: true,align:'left',
    },
    {
      title: '税号',
      dataIndex: 'custSregcode',
      ellipsis: true,width: 150,
    },
    {
      title: '分类名称',
      dataIndex: 'uniqueCustclassName',
      ellipsis: true,width: 100,
    },
    {
      title: '管理类型',
      dataIndex: 'manageType',
      ellipsis: true,width: 100,
      slots: { customRender: 'manageType' },
    },{
      title: '备注',
      dataIndex: 'remarks',
    },
  ],
};
const pageParameter = reactive({
  showRulesSize: 'MIN',
  total: 0,
  ccodeGroupCcode:'',
  searchConditon: {
    requirement: 'custCode',
    value: '',
  },
})

const [registerEditPage, { openModal: openEditPage }] = useModal();
const [registerGroupSavePage, { openModal: openGroupSavePage }] = useModal();
const [registerCustModalPopPage, { openModal: openrCustModalPage }] = useModal();

// 这是示例组件
const [registerTable, { deleteSelectRowByKey,reload,getColumns,setColumns,setTableData,getPaginationRef,setPagination,getSelectRows,getDataSource,clearSelectedRowKeys }] = useTable({
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: true,
  indexColumnProps: {fixed: true},
  pagination: {
    pageSize: 200,
    simple: true
  },
});
function rowClick(a) {
  if(hasBlank(a.custCode)){
    deleteSelectRowByKey(a.key)
  }
}
const rowSelection = {
  getCheckboxProps: (record) => ({
    disabled: record.custCode==undefined
  }),
};

const openLook = async (data: any,flag) => {
  openEditPage(true, {
    data: data,
    type: 'look',
    defaultPage:defaultPage.value,
    database:database.value,
    isEdit:flag,
    accountInfo:accountInfo.value
  });
};


async function handleSelect(obj: any) {
  if(obj.toString()!==''){
    pageParameter.ccodeGroupCcode=obj.toString()
  }else{
    pageParameter.ccodeGroupCcode='0'
    selectedKeys2.value='0'
  }
  findByCusAuthData()
}
async function fetch() {
  const deptTree = await useRouteApi(findAllStockCust,{schemaName: database})('')
  function a(customerTree) {
    customerTree.forEach((item) => {
      item.title ='[' + item.ccode + '] '+ item.custGroupName;
      item.key = item.ccode;
    });
  }

  a(deptTree);
  treeData.value = []
  treeData.value.push({title: '全部',key:'0',children: deptTree})
  selectedKeys2.value='0'
  tableshow.value=true

  setTimeout(()=>{
    findByCusAuthData()
  },200)
}

const openTreeAddPage = () => {
  openGroupSavePage(true, {
    database:database.value,
  });
}
async function delGroup(val) {
  if (val.toString()=='0'){
    return createWarningModal({ content: '请选择客户组名称' });
  }
  createConfirm({
    iconType: 'error',
    title: '警告',
    content: '确定删除客户组名称及客户信息吗？',
    onOk: async () => {
      await useRouteApi(delByStockCustCcode,{schemaName: database})(val.toString())
      await useRouteApi(deleteByCcodeGroupCcode,{schemaName: database})(val.toString())
      fetch()
    }
  })
}
const route = useRoute();
const dynamicAdReload = async (obj) => {
  accountInfo.value=obj
  accId.value=obj.accId
  database.value =obj.accountMode
  databaseName.value=obj.baseName
  fetch()
}

const showPaginationText=ref(false)
async function findByCusAuthData() {
    pageParameter.ccodeGroupCcode=selectedKeys2.value[0]
    let temp=await useRouteApi(findAllStockCustGroups,{schemaName: database})(pageParameter)
    custCountAllList.value=temp.length
    showPaginationText.value=true
    if(temp.length<50){
      for (let i =  temp.length; i < 50; i++) {
        temp.push({})
      }
    }
    setTableData(temp)
}
const pageReload =  () =>{
  clearSelectedRowKeys()
  fetch()
}

async function delRowData() {
  if(getSelectRows().length==0){
    return createWarningModal({ content: '请选择客户信息' });
  }
  createConfirm({
    iconType: 'error',
    title: '警告',
    content: '确定删除客户信息吗？',
    onOk: async () => {
      await useRouteApi(delStockCustGroupsByCustCode,{schemaName: database})({params:getSelectRows().map(a=>a.uniqueCode),ccodeGroupCcode:selectedKeys2.value.toString()})
      clearSelectedRowKeys()
      findByCusAuthData()
    }
  })
}
const modalShow = () => {
  if(selectedKeys2.value.toString()=='0'){
    return createWarningModal({ content: '请选择客户组名称' });
  }
  openrCustModalPage(true, {
    database: database.value,
    accId: accId.value,
  });
}
// 弹选回调
const modalData = async (data) => {
  // 客户组名称下已经添加的客户
  let allCustCodeUnique=await useRouteApi(findAllStockCustGroupsCustCode,{schemaName: database})(selectedKeys2.value.toString())

  let list=[]
  // 过滤已经增加的
  let newdata=allCustCodeUnique.length>0?data.filter(a=>allCustCodeUnique.indexOf(a.uniqueCode)<0):data
  for (let i = 0; i < newdata.length; i++) {
    list.push({ccodeGroupCcode:selectedKeys2.value.toString(),custCode:newdata[i].uniqueCode})
  }
  await useRouteApi(saveStockCustGroupsList,{schemaName: database})(list)
  .then(()=>{
    clearSelectedRowKeys()
    findByCusAuthData()
  })
}
</script>
<style src="./global-menu-index.less" lang="less" scoped></style>
<style lang="less" scoped>
a {
  color: #0096c7;
  text-decoration: none;
  cursor: pointer;
}
.bg-white{
  width: 250px;
  min-height: 230px ;/**/
  height: calc(100% - 237px);
  border: 1px #cccccc solid;
  background:white ;
  margin-top: -0.5%;
  text-align: left;
}
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
  border-color: #aaaaaa !important;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 13px !important;
  padding: 2px 8px !important;
  border-color: #aaaaaa !important;
}

.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
}
.app-container:nth-of-type(2) {
  padding: 0px;
  margin: 5px 10px;
  background: #b4c8e3 !important;
  position: relative;
  .pagination-text {
    position: absolute;
    right: 10%;
    font-size: 13px;
    color: black;
    z-index: 99999999;
  }
}

:deep(.ant-table-thead) th{
  text-align: center !important;
  font-weight: bold;
  //background-color: #f2f2f2 !important;
  background-color: #cccccc !important;
  border-color: #aaaaaa !important;
}

:deep(.vben-basic-table) .ant-table-wrapper {
  padding: 0px;
}

:deep(.vben-basic-table) .ant-table {
  width: 100%;
  margin: 0;
  overflow-x: hidden;
  height: calc(100% - 160px);
  min-height: 500px;
}

:deep(.vben-basic-table) .ant-pagination {
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
}

:deep(.vben-basic-table){
  min-height: 500px;
  height: calc(100% - 160px);
  margin-bottom: 20px;
}

:deep(.ant-input),:deep(.ant-select),:deep(.ant-btn){
  border: 1px solid #c9c9c9;
}

:deep(.ant-table-measure-row){
  td{
    padding: 0 !important;
  }
}
</style>
