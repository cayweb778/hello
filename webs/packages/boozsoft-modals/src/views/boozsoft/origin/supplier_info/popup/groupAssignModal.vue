<template>
  <BasicModal
    width="900px"
    :minHeight="500"
    :height="500"
    v-bind="$attrs"
    title="组织-供应商信息分配"
    :showOkBtn="false"
    :closable="false"
    @ok="handleOk()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height: 25px;font-size: 20px;">
          <SearchOutlined style="width:25px;margin-right: 10px;"/>
          组织-供应商信息分配
        </span>
      </div>
    </template>
    <div
      style="background-color: #158eb8;height: 50px;padding-top: 10px;padding-left:10px;border-radius: 5px;">
      <span style="color: white;">账套：</span>
      <a-select
          v-model:value="accId"
          option-filter-prop="children"
          show-search
          style="width: 25%"
          @change="tabsCheck(tabsflag)"
        >
          <a-select-option
            v-for="item in acclist"
            :key="item.accId"
            :value="item.accId"
          >
            {{ item.accName }}
          </a-select-option>
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </a-select>
      <div style="float: right;margin-right: 5px;">
        <button type="button" class="ant-btn ant-btn-me" @click="handleOk" v-if="tabsflag=='0'"><span>分配</span></button>
        <button type="button" class="ant-btn ant-btn-me" @click="delGiveOrgData" v-else><span>取消分配</span></button>
        <button type="button" class="ant-btn ant-btn-me" @click="reload"><span>刷新</span></button>
      </div>
    </div>

    <div style="display: inline;width: 11%;height:400px;float: left;margin-top: 5px;">
      <a-tabs v-model:activeKey="tabsflag" @change="tabsCheck" tab-position="left">
        <a-tab-pane v-for="(item, i) in typylist" :key="item.value" :tab="`${item.title}`"/>
      </a-tabs>
    </div>
    <div style="display: inline;width: 89%;float: right;margin-top: 5px;">
      <BasicTable :loading="loading" @register="registerTable" :scroll="{ y: 400 }"
                  :class="'a-table-font-size-12'" >
        <template #cusCclassName="{ record }">
          {{ record.cusCclassName }}
        </template>
        <template #flag="{ record }">
          <a-tag :color="record.flag == '1' ? 'green' : 'volcano'">
            {{ record.flag == '1' ? '已引入' : '未引入' }}
          </a-tag>
        </template>
      </BasicTable>
    </div>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
import {BasicTable, useTable} from '/@/components/Table';
import {SearchOutlined,CaretDownOutlined,QuestionCircleOutlined} from '@ant-design/icons-vue';
import {onMounted, reactive, ref, toRaw, unref} from 'vue';
import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
import {
  Tag as ATag,
  Tabs as ATabs,
  Select as ASelect,
  Form as AForm,
  Input as AInput,
  Statistic as AStatistic,
} from 'ant-design-vue';
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {useMessage} from "/@/hooks/web/useMessage";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {findAllByAccGroup, findByAccId} from "/@/api/record/system/account";
import {originCustomerFindAll} from "/@/api/org/sup/org_sup";
import {
  findByOrgAssigAccount,
  groupCusAssignDelByIdAndTenantId,
  groupCusAssignOrgSave
} from "/@/api/record/system/supplier_group";
import {findAllOrgCusClass} from "/@/api/org/sup_class/org_sup_class";
import {
  assignOrgSave,findByOrgAssigTenantId
} from "/@/api/record/system/supplier_class_group";
import {saveLog} from "/@/api/record/system/group-sys-login-log";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {customerSaveCusClass} from "/@/api/record/system/customer_class";
import {duLiSaveApi} from "/@/api/record/supplier_data/supplier";
import {
  findByUserIdAndTenantId
} from "/@/api/record/system/group-permission";
import {findByUniqueCode} from "/@/api/record/group/im-organize";


const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const AFormItem = AForm.Item;
const ATabPane = ATabs.TabPane;

const typylist=reactive([
  {title:'已分配',value:'1'},
  {title:'待分配',value:'0'},
])
const tabsflag = ref('1');

const {createSuccessModal,createWarningModal} = useMessage()
const emit = defineEmits(['register']);
const database = ref(getCurrentAccountName(true));
const accId = ref('');
const loading = ref(true);
const treeData: any = ref([]);
const selectedKeys2: any = ref([]);
const expandedKeys: any = ref([]);
const dataSource: any = ref([]);
const columns = [
  {
    title: '供应商编码',
    dataIndex: 'custCode',
    ellipsis: true,
    slots: { customRender: 'custCode' },fixed: 'left'
  },
  {
    title: '供应商全称',
    dataIndex: 'custName',
    ellipsis: true,
    slots: { customRender: 'custName' },fixed: 'left'
  },
  {
    title: '供应商简称',
    dataIndex: 'custAbbname',
    ellipsis: true,fixed: 'left'
  },{
    title: '分类名称',
    dataIndex: 'uniqueCustclassName',
    ellipsis: true,
  },
];
const columns2 = [
  {
    title: '供应商编码',
    dataIndex: 'custCode',
    ellipsis: true,
    slots: { customRender: 'custCode' },fixed: 'left'
  },
  {
    title: '供应商全称',
    dataIndex: 'custName',
    ellipsis: true,
    slots: { customRender: 'custName' },fixed: 'left'
  },
  {
    title: '供应商简称',
    dataIndex: 'custAbbname',
    ellipsis: true,fixed: 'left'
  },{
    title: '分类名称',
    dataIndex: 'uniqueCustclassName',
    ellipsis: true,
  }, {
    title: '使用状态',
    dataIndex: 'flag',
    key: 'flag',
    ellipsis: true,width: 80, slots: {customRender: 'flag'},
  }
];
const acclist: any = ref([]);
const orgUnique: any = ref('');
// 是否强制添加到组织供应商分类表
const orgSaveFlag: any = ref('0');
const pageParameter = reactive({
  searchConditon: {
    requirement: 'custCode',
    value: '',
  },
  showRulesSize: 'MIN',
  uniqueCustclass:'0',
  database: useCompanyOperateStoreWidthOut().getSchemaName,
  username: useUserStoreWidthOut().getUserInfo.username,
  flag:'1',
  orgUnique:'',
  searchValue: '',
  superid: '',
  size:'1000000',
  page:'1'
})

// 这是示例组件
const [registerTable, {reload, getSelectRows, getDataSource, setTableData,setColumns,clearSelectedRowKeys,setSelectedRowKeys,setPagination}] = useTable({
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
  orgUnique.value=data.orgUnique
  pageParameter.orgUnique=data.orgUnique


  findByOrgAccount()
  clearSelectedRowKeys()
  setColumns(columns2)

  setTimeout(()=>{
    if(accId.value!==''){
      findByGiveOrgData()
      getAccId()
      tabsCheck('1')
      orgCusPermission()
    }else{
      loading.value=false
    }
  },200)
});
async function delGiveOrgData() {
  if(getSelectRows().length==0){
    warningModal('请选择供应商信息')
    return false
  }

  let a=0
  for (let i = 0; i < getSelectRows().length; i++) {
    if(getSelectRows()[i].flag=='1'){
      a++
      warningModal('供应商信息已被引用,不能取消分配!!')
      break;
    }
  }
  if(a==0){
    let orgInfo=await findByUniqueCode(orgUnique.value)
    getSelectRows().forEach(async (a)=>{
      /************** 记录操作日志 ****************/
      let map={
        loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
        userId: useUserStoreWidthOut().getUserInfo.username,
        userName: useUserStoreWidthOut().getUserInfo.realName,
        optModule:'org',
        optFunction:'供应商',
        optRange:'0',
        optAction:'分配',
        optContent:'操作内容【取消分配】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,供应商编码【'+a.custCode+'】,供应商全称【'+a.custName+'】,供应商简称【'+a.custAbbname+'】',
      }
      await saveLog(map)
      /************** 记录操作日志 END ****************/
    })
    await groupCusAssignDelByIdAndTenantId(orgUnique.value,'2',accId.value,getSelectRows().map(a=>a.uniqueCode).join(',')).then(()=>{
      createSuccessModal({
        iconType: 'success',
        title: '提示',
        content: '取消分配成功',
        onOk: async () => {
          setTableData([])
          findByGiveOrgData()
        }
      })
      clearSelectedRowKeys()
    })
  }
}
async function orgCusPermission() {
  // 集团档案权限分配表-集团供应商
  let findByPermission=await findByUserIdAndTenantId(useUserStoreWidthOut().getUserInfo.id,accId.value,'supplier')
  if(findByPermission.length>0){
    orgSaveFlag.value=findByPermission[0].isAuto
  }
}
async function getAccId() {
  let acc=await findByAccId(accId.value)
  database.value=accId.value+'-'+acc.yearStartDate.split('-')[0]
}

// 分配给组织的
async function findByGiveOrgData() {
  loading.value=true
  setColumns(columns2)
  // 组织供应商信息
  let orgSourse=await originCustomerFindAll(pageParameter)
  // 已分配给账套
  let temp=await findByOrgAssigAccount(orgUnique.value,'2')
  if(temp.length>0){
    let findByAcc=temp.filter(a=>a.tenantId==accId.value)
    let arr=getArrDifSameValue(findByAcc,orgSourse.items)
    setTableData(arr)
    loading.value=false
  }else{
    loading.value=false
  }
}
// 没分配给组织的
async function findByNoGiveOrgData() {
  loading.value=true
  // 组织供应商信息
  let orgSourse=await originCustomerFindAll(pageParameter)
  // 已分配给账套
  let temp=await findByOrgAssigAccount(orgUnique.value,'2')
  let findByAcc=temp.filter(a=>a.tenantId==accId.value)
  setTableData(orgSourse.items.filter(a=>findByAcc.map(a=>a.dataUnique).indexOf(a.uniqueCode)==-1))
  loading.value=false
}
function tabsCheck(a) {
  tabsflag.value=a
  if(tabsflag.value==='1'){
    setTableData([])
    findByGiveOrgData()
  }else{
    setColumns(columns)
    findByNoGiveOrgData()
  }
}
// 获取组织下账套
async function findByOrgAccount() {
  let temp=await findAllByAccGroup(orgUnique.value)
  acclist.value=temp
  if(temp.length>0){
    accId.value=temp[0].accId
  }
}
// 当前时间
function getNowDate() {
  return new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ");
}
function getArrDifSameValue(arr1,arr2){
  var result:any = [];
  for(var i = 0; i < arr2.length; i++){
    var obj:any = arr2[i];
    var id = obj.uniqueCode;
    var isExist = false;
    for(var j = 0; j < arr1.length; j++){
      var aj = arr1[j];
      var n = aj.dataUnique;
      if(n == id){
        isExist = true;
        break;
      }
    }
    // !isExist 去除已有的。isExist 获取已有的
    if(isExist){
      obj.flag=aj.flag
      obj.tenantId=aj.tenantId
      result.push(obj);
    }
  }
  return result;
}
async function handleOk() {
  if(getSelectRows().length==0){
    warningModal('请选择供应商信息!!')
    return false
  }
  let cusClassUnique:any=[]
  // 组织供应商分类
  let map={
    searchValue: '',
    superid: '',
    orgUnique: orgUnique.value,
    page: '1',
    size: '10000000',
  }
  let dataSource= await findAllOrgCusClass(map)
  // 查询已经分配的数据
  let assignData=await findByOrgAssigTenantId(orgUnique.value,'2',accId.value)
  // 集团供应商档案分配表数据
  let groupAssignData:any=[]
  let orgInfo=await findByUniqueCode(orgUnique.value)
  getSelectRows().forEach(async (a)=>{
    // 所属分类是否分配给账套：没有 查询分类 一并分配
    let findByOrgCusLcass= assignData.filter(cl=>cl.dataUnique==a.uniqueCustclass)
    if(findByOrgCusLcass.length==0){
      let aa=dataSource.items.filter(f=>f.uniqueCustclass==a.uniqueCustclass)
      cusClassUnique.push(aa[0])
    }
    a.dataUnique=a.uniqueCode
    a.id=null
    a.orgUnique=orgUnique.value
    a.tenantId=accId.value
    a.flag=orgSaveFlag.value
    a.ctype='2'
    a.assignUserId=useUserStoreWidthOut().getUserInfo.id
    a.assignDate=getNowDate()
    a.acceptUserId=useUserStoreWidthOut().getUserInfo.id
    a.acceptDate=getNowDate()
    groupAssignData.push(a)

    /************** 记录操作日志 ****************/
    let map3={
      loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
      userId: useUserStoreWidthOut().getUserInfo.username,
      userName: useUserStoreWidthOut().getUserInfo.realName,
      optModule:'org',
      optFunction:'供应商分类',
      uniqueCode:orgUnique.value,
      optRange:'0',
      optAction:'分配',
      optContent:'【组织供应商分类分配】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,分类编码【'+a.cusClass+'】,供应商名称【'+a.cusCclassName+'】',
    }
    await saveLog(map3)
    /************** 记录操作日志 END ****************/
  })
  //****************** 查找分类 ****************
  let data:any=[]
  cusClassUnique.forEach(a=>{
    if(a.cusClassGrade=='1'){
      data.push(a)
    }
    else{
      if(a.cusClassGrade=='2'){
        // 2找1级
        let temp= dataSource.items.filter(b=>b.uniqueCustclass==a.parentId)
        let t=assignData.filter(c=>c.dataUnique==temp[0].uniqueCustclass)
        if(t.length==0){
          data.push(temp[0])
        }
      }
      else if(a.cusClassGrade=='3'){
        // 3找2级
        let temp= dataSource.items.filter(b=>b.uniqueCustclass==a.parentId)
        let t=assignData.filter(c=>c.dataUnique==temp[0].uniqueCustclass)
        if(t.length==0){
          data.push(temp[0])
        }
        // 2找1级
        let temp2= dataSource.items.filter(b=>b.uniqueCustclass==temp[0].parentId)
        let t2=assignData.filter(c=>c.dataUnique==temp2[0].uniqueCustclass)
        if(t2.length==0){
          data.push(temp2[0])
        }
      }
    }
  })

  // 往账套供应商分类表添加
  let accCusClassAssignArr= data.concat(cusClassUnique)
  if(accCusClassAssignArr.length>0){
    accCusClassAssignArr.forEach(async (a)=>{
      a.dataUnique=a.uniqueCustclass
      a.id=null
      a.tenantId=accId.value
      a.orgUnique=orgUnique.value
      a.flag='1'
      a.ctype='2'
      a.assignUserId=useUserStoreWidthOut().getUserInfo.id
      a.assignDate=getNowDate()
      a.acceptUserId=useUserStoreWidthOut().getUserInfo.id
      a.acceptDate=getNowDate()

      /************** 记录操作日志 ****************/
      let map3={
        loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
        userId: useUserStoreWidthOut().getUserInfo.username,
        userName: useUserStoreWidthOut().getUserInfo.realName,
        optModule:'org',
        optFunction:'供应商分类',
        uniqueCode:orgUnique.value,
        optRange:'0',
        optAction:'分配',
        optContent:'操作内容【组织供应商信息分配给账套】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,供应商编码【'+a.custCode+'】,供应商全称【'+a.custName+'】,供应商简称【'+a.custAbbname+'】',
      }
      await saveLog(map3)
      /************** 记录操作日志 END ****************/
    })
    // 添加集团供应商分类分配表
    let map={ list:JSON.stringify([...new Set(accCusClassAssignArr)])}
    await assignOrgSave(map)
    // 往账套供应商分类表添加
    await useRouteApi(customerSaveCusClass,{schemaName: database})(accCusClassAssignArr)
    //****************** 查找分类END ****************
  }
  // 集团供应商信息分配记录表
  await groupCusAssignOrgSave(groupAssignData)
  if(orgSaveFlag.value==='1'){
    getSelectRows().forEach(async (a)=>{
      await useRouteApi(duLiSaveApi,{schemaName: database})(a)
    })
  }
  createSuccessModal({
    iconType: 'success',
    title: '提示',
    content: '分配成功',
    onOk: async () => {
      findByNoGiveOrgData()
    }
  })
  clearSelectedRowKeys()
}
function warningModal(text) {
  createWarningModal({
    iconType: 'warning',
    title: '提示',
    content: text,
    onOk: async () => {}
  })
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
