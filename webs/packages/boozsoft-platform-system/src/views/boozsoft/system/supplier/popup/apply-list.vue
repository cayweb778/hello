<template>
    <BasicModal
      width="900px"
      v-bind="$attrs"
      title="新增申请"
      @cancel="handleOk()"
      wrapClassName="head-title"
      @register="register"
    >
      <template #title>
        <div style="display: flex;height:30px;margin-top: 10px;" class="vben-basic-title">
        <span style="font-size: 24px;line-height:30px;">
          <PlusCircleOutlined style="font-size: 28px;font-weight: bold"/>&nbsp;&nbsp;新增申请 － 供应商信息
        </span>
        </div>
      </template>
      <div class="nc-open-content">
        <div class="open-content-up" style="margin-top: 10px;">
          <div style="background:#0096c7;padding:10px;border-radius: 2px;margin-bottom:10px;display: flex;justify-content : space-between;">
            <div class="a1">
              <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 120px;font-size: 12px;" class="special_select">
                <a-select-option style="font-size: 12px;" value="custCode">供应商编码</a-select-option>
                <a-select-option style="font-size: 12px;" value="custName">供应商全称</a-select-option>
              </a-select>
              <a-input-search
                v-model:value="pageParameter.searchConditon.value"
                placeholder=""
                style="width: 200px; border-radius: 4px"
                @search="reload"
              />
            </div>
            <div class="a2">
              <button type="button" class="ant-btn ant-btn-me" @click="openAdd()" ><span>新增</span></button>
              <button type="button" class="ant-btn ant-btn-me" @click="editOpen()" ><span>修改</span></button>
              <button type="button" class="ant-btn ant-btn-me" @click="openImprot()" ><span>导入</span></button>
              <button type="button" class="ant-btn ant-btn-me" @click="delList()" ><span>删除</span></button>
            </div>
          </div>

          <div style="height: 350px;padding:0;display: flex;justify-content : space-between;">
            <BasicTable
              :row-selection="{ type: 'checkbox',}"
              :scroll="{ y: 270 }"
              @register="registerTable"
              class="tables"
            >
              <template #successState="{ record }">
                <span>
                  <a-tag :color="record.successState == '0' ? 'warning' : 'volcano'">
                    {{ record.successState == '0' ? '未生效' : '已驳回' }}
                  </a-tag>
                </span>
              </template>
              <template #custCode="{ record }"> <a @click="editOpen2(record,false)">{{ record.custCode }}</a> </template>
              <template #custName="{ record }"> <a @click="editOpen2(record,false)">{{ record.custName }}</a> </template>
              <template #custAbbname="{ record }"> <a @click="editOpen2(record,false)">{{ record.custAbbname }}</a> </template>
            </BasicTable>
          </div>
        </div>
      </div>

      <Edit @save="editData" @register="registerEditPage" />
      <Add @save="saveData" @closeOk="reload" @register="registerAddPage" />
      <!-- 集团供应商导入页面 -->
      <ImprotExcel @save="reload" @register="registerImprotExcelPage" />
      <template #footer>
        <div>
          <a-button @click="handleOk()" type="primary">关闭</a-button>
        </div>
      </template>
    </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import { useUserStoreWidthOut } from '/@/store/modules/user';
import {nextTick, onMounted, reactive, ref, unref} from 'vue'
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import { PlusCircleOutlined } from '@ant-design/icons-vue'
import {
  Select as ASelect,
  Input as AInput,
  Checkbox as ACheckbox,
  Button as AButton,
  Tag as ATag,
  message
} from 'ant-design-vue'
import {useMessage} from "/@/hooks/web/useMessage";
import {BasicTable, useTable} from "/@/components/Table";
import Edit from '/@/views/boozsoft/system/supplier/popup/apply-edit.vue';
import Add from '/@/views/boozsoft/system/supplier/popup/apply-save.vue';
import ImprotExcel from '/@/views/boozsoft/system/supplier/popup/apply-improtExcel.vue';
import {
  GroupCustomerAddApi,
  delCusArrGroup, groupCusAssignOrgSave, findGroupCusByTenantId
} from "/@/api/record/system/supplier_group";
import {
  findByUserIdAndTenantId
} from "/@/api/record/system/group-permission";
import {saveLog} from "/@/api/record/system/group-sys-login-log";
import {originCustomerSaveAll} from "/@/api/org/cus/org_cus";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findAllByCusBendEq1} from "/@/api/record/system/supplier_class";
import {duLiSaveApi} from "/@/api/record/supplier_data/supplier";
import {findByUniqueCode} from "/@/api/record/group/im-organize";



const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const {createErrorModal,createConfirm,createWarningModal} = useMessage()
const [registerEditPage, { openModal: openEditPage }] = useModal();
const [registerAddPage, { openModal: openAddPage }] = useModal();
const [registerImprotExcelPage, { openModal: openImprotExcelPage }] = useModal();

const emit = defineEmits(['register', 'save'])

const accountInfo=ref({})
const tenantId=ref('')
const orgUnique=ref('')
const pageParameter = reactive({
  searchConditon: {
    requirement: 'custName',
    value: '',
  },
  tenantId:'',
})

const [register, {closeModal,setModalProps}] = useModalInner(async(data) => {
  accountInfo.value=data.accountInfo
  tenantId.value=data.tenantId
  orgUnique.value=data.accGroup
  pageParameter.tenantId=data.tenantId.substring(0,data.tenantId.length-5)
  reload()
})

const columns = [
  {
    title: '生效状态',
    dataIndex: 'successState',
    ellipsis: true,
    width: 100,
    slots: { customRender: 'successState'}
  },
  {
    title: '审批情况',
    dataIndex: 'reason',
    ellipsis: true,
  },
  {
    title: '供应商编码',
    dataIndex: 'custCode',align:'left',
    ellipsis: true,slots: { customRender: 'custCode'}
  },
  {
    title: '供应商全称',
    dataIndex: 'custName',align:'left',
    ellipsis: true,slots: { customRender: 'custName'}
  },
  {
    title: '供应商简称',
    dataIndex: 'custAbbname',align:'left',
    ellipsis: true,slots: { customRender: 'custAbbname'}
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
]

// 这是示例组件
const [registerTable, {reload,getColumns,setPagination,getSelectRows,clearSelectedRowKeys}] = useTable({
  api:findGroupCusByTenantId,
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  pagination: false,
  searchInfo:pageParameter
})

async function openImprot() {
  if(await findByClass()==0){
    return createWarningModal({ content: '请先引入供应商分类！' });
  }
  openImprotExcelPage(true, {
    orgUnique:orgUnique.value,
    tenantId:tenantId.value,
    accountInfo:accountInfo.value,
  });
}
function delList() {
  if(getSelectRows().length==0){
    return createWarningModal({content:'请选择需要删除的数据'})
  }
  createConfirm({
    iconType: 'warning',
    title: '提示',
    content: '是否删除选中数据,确定继续吗？',
    onOk: async () => {
      await delCusArrGroup(getSelectRows().map(a=>a.id).join(',')).then(()=>{
        reload()
      })
    }
  })
}

async function editData(data) {
  await GroupCustomerAddApi(data).then(()=>{
    clearSelectedRowKeys()
    reload()
  })
}
async function editOpen() {
  if(getSelectRows().length!=1){
    return createWarningModal({content:'请选择一条数据编辑！'})
  }
  editOpen2(getSelectRows()[0],true)
}
async function editOpen2(data,isEdit) {
  /************** 记录操作日志 ****************/
  let map={
    loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
    userId: useUserStoreWidthOut().getUserInfo.username,
    userName: useUserStoreWidthOut().getUserInfo.realName,
    optModule:'master_data',
    optFunction:'供应商',
    optRange:'1',
    uniqueCode:tenantId.value,
    optAction:'新增申请',
    optContent:'操作内容【点击新增申请-修改】,账套代码【'+accountInfo.value.coCode+'】,账套名称【'+accountInfo.value.companyName+'】',
  }
  await saveLog(map)
  /************** 记录操作日志 END ****************/
  openEditPage(true, {
    data:data,
    accountInfo:accountInfo.value,
    tenantId:tenantId.value,
    orgUnique:orgUnique.value,
    isEdit:isEdit
  });
}
async function saveData(data) {
  // 集团档案权限分配表-集团供应商
  let orgSaveFlag='0'
  let findByPermission=await findByUserIdAndTenantId(useUserStoreWidthOut().getUserInfo.id,tenantId.value,'supplier')
  if(findByPermission.length>0){
    orgSaveFlag=findByPermission[0].isAuto
  }

  // 0:走集团审批。1:自动分配【集团、组织、组织下属账套。】
  if(orgSaveFlag=='0'){
    data.successState='0'
    data.applyUser=useUserStoreWidthOut().getUserInfo.name
    data.applyDate=getNowDate()
    data.originUnique=orgUnique.value
    data.tenantId=pageParameter.tenantId
    data.ctype='2'
    await GroupCustomerAddApi(data).then(async (a)=>{
      /************** 记录操作日志 ****************/
      let map={
        loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
        userId:useUserStoreWidthOut().getUserInfo.username,
        userName:useUserStoreWidthOut().getUserInfo.realName,
        optModule:'master_data',
        optFunction:'供应商',
        optRange:'1',
        uniqueCode:tenantId.value.substring(0,tenantId.value.length-5),
        optAction:'新增申请',
        optContent:'操作内容【新增供应商信息】,账套代码【'+accountInfo.value.coCode+'】,账套名称【'+accountInfo.value.companyName+'】,供应商编码【'+a.custCode+'】,供应商全称【'+a.custName+'】,供应商简称【'+a.custAbbname+'】',
      }
      await saveLog(map)
      /************** 记录操作日志 End ****************/
    })
  }
  else{
    // 集团存在引用的
    if(data.id!==undefined){
      data.isDel='0'
      data.delName=''
      data.delDate=''
      data.reason=''
    }else{
      data.applyUser=useUserStoreWidthOut().getUserInfo.name
      data.applyDate=getNowDate()
      data.successState='1'
    }
    data.ctype='2'
    data.tenantId=pageParameter.tenantId
    data.orgUnique=orgUnique.value
    data.originUnique=orgUnique.value
    await GroupCustomerAddApi(data).then(async (a)=>{
      /************** 记录操作日志 ****************/
      let map={
        loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
        userId:useUserStoreWidthOut().getUserInfo.username,
        userName:useUserStoreWidthOut().getUserInfo.realName,
        optModule:'group',
        optFunction:'供应商',
        optRange:'2',
        uniqueCode:orgUnique.value,
        optAction:'新增',
        optContent:'操作内容【新增供应商信息】,账套代码【'+accountInfo.value.coCode+'】,账套名称【'+accountInfo.value.companyName+'】,供应商编码【'+a.custCode+'】,供应商全称【'+a.custName+'】,供应商简称【'+a.custAbbname+'】',
      }
      // 集团不存在的需要加日志
      if(data.id==undefined){
        await saveLog(map)
      }
      /************** 记录操作日志 End ****************/
      data.dataUnique=a.uniqueCode
      data.uniqueCode=a.uniqueCode
    })

    data.assignUserId=useUserStoreWidthOut().getUserInfo.id
    data.assignDate=getNowDate()
    data.acceptUserId=useUserStoreWidthOut().getUserInfo.id
    data.acceptDate=getNowDate()
    data.id=null

    // 集团供应商信息分配记录表-账套
    data.ctype='2'
    await groupCusAssignOrgSave([data]).then(async (a)=>{
      /************** 记录操作日志 ****************/
      let map2={
        loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
        userId:useUserStoreWidthOut().getUserInfo.username,
        userName:useUserStoreWidthOut().getUserInfo.realName,
        optModule:'master_data',
        optFunction:'供应商',
        optRange:'1',
        uniqueCode:orgUnique.value,
        optAction:'分配',
        optContent:'操作内容【分配供应商信息】,账套代码【'+accountInfo.value.coCode+'】,账套名称【'+accountInfo.value.companyName+'】,供应商编码【'+a.custCode+'】,供应商全称【'+a.custName+'】,供应商简称【'+a.custAbbname+'】',
      }
      await saveLog(map2)
      /************** 记录操作日志 End ****************/
    })
    // 集团供应商信息分配记录表-组织
    data.ctype='1'
    data.tenantId=null
    let orgInfo=await findByUniqueCode(orgUnique.value)
    await groupCusAssignOrgSave([data]).then(async (a)=>{
      /************** 记录操作日志 ****************/
      let map={
        loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
        userId:useUserStoreWidthOut().getUserInfo.username,
        userName:useUserStoreWidthOut().getUserInfo.realName,
        optModule:'org',
        optFunction:'供应商',
        optRange:'0',
        uniqueCode:orgUnique.value,
        optAction:'分配',
        optContent:'操作内容【分配供应商信息】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,供应商编码【'+a.custCode+'】,供应商全称【'+a.custName+'】,供应商简称【'+a.custAbbname+'】',
      }
      await saveLog(map)
      /************** 记录操作日志 End ****************/
    })

    // 添加到组织供应商信息表
    await originCustomerSaveAll([data]).then(async (a)=>{
      /************** 记录操作日志 ****************/
      let map={
        loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
        userId:useUserStoreWidthOut().getUserInfo.username,
        userName:useUserStoreWidthOut().getUserInfo.realName,
        optModule:'org',
        optFunction:'供应商',
        optRange:'0',
        uniqueCode:orgUnique.value,
        optAction:'新增',
        optContent:'操作内容【新增供应商信息】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,供应商编码【'+a.custCode+'】,供应商全称【'+a.custName+'】,供应商简称【'+a.custAbbname+'】',
      }
      await saveLog(map)
      /************** 记录操作日志 End ****************/
    })

    // 添加到账套供应商信息表
    await useRouteApi(duLiSaveApi,{schemaName: tenantId})(data).then(async (a)=>{
      /************** 记录操作日志 ****************/
      let map={
        loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
        userId:useUserStoreWidthOut().getUserInfo.username,
        userName:useUserStoreWidthOut().getUserInfo.realName,
        optModule:'master_data',
        optFunction:'供应商',
        optRange:'1',
        uniqueCode:orgUnique.value,
        optAction:'新增',
        optContent:'操作内容【新增供应商信息】,账套代码【'+accountInfo.value.coCode+'】,账套名称【'+accountInfo.value.companyName+'】,供应商编码【'+a.custCode+'】,供应商全称【'+a.custName+'】,供应商简称【'+a.custAbbname+'】',
      }
      await saveLog(map)
      /************** 记录操作日志 End ****************/
    })
  }
  reload()
  // 继续弹出新增页面
  if(data.closeflag==='add'){
    openAddPage(true, {
      tenantId:tenantId.value,
    });
  }
}
// 供应商分类
async function findByClass() {
  // 全部供应商分类
  let res=await useRouteApi(findAllByCusBendEq1,{schemaName: tenantId})('')
  return res.items.length;
}

async function openAdd() {
  if(await findByClass()==0){
    return createWarningModal({ content: '请先引入供应商分类！' });
  }
  /************** 记录操作日志 ****************/
  let map={
    loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
    userId: useUserStoreWidthOut().getUserInfo.username,
    userName: useUserStoreWidthOut().getUserInfo.realName,
    optModule:'master_data',
    optFunction:'账套',
    optRange:'1',
    uniqueCode:orgUnique.value,
    optAction:'新增申请',
    optContent:'操作内容【点击新增申请-新增】,账套代码【'+accountInfo.value.coCode+'】,账套名称【'+accountInfo.value.companyName+'】',
  }
  await saveLog(map)
  /************** 记录操作日志 END ****************/
  openAddPage(true, {
    tenantId:tenantId.value,
    accountInfo:accountInfo.value
  });
}
// 当前时间
function getNowDate() {
  return new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ");
}
async function handleOk() {
  /************** 记录操作日志 ****************/
  let map={
    loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
    userId: useUserStoreWidthOut().getUserInfo.username,
    userName: useUserStoreWidthOut().getUserInfo.realName,
    optModule:'master_data',
    optFunction:'账套',
    optRange:'1',
    uniqueCode:orgUnique.value,
    optAction:'新增申请',
    optContent:'操作内容【关闭新增申请】,账套代码【'+accountInfo.value.coCode+'】,账套名称【'+accountInfo.value.companyName+'】',
  }
  await saveLog(map)
  /************** 记录操作日志 ****************/
  emit('closeOk', '');
  closeModal();
}
</script>
<style>
.vben-basic-title {
  color: #0096c7 !important;
  border:none !important;
}

.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
  border-top: none !important;
}
.ant-modal-header{
  border: none !important;
}
</style>
<style scoped lang="less">
.ant-modal-header{
  border: none !important;
}
.vben-basic-title {
  color: #0096c7 !important;
  border: none !important;
  margin-bottom: -20px !important;
}

:global(.ant-modal-body) {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
  margin-bottom: 0!important;
  .scrollbar{
    padding: 0px;
    .scroll-container{
      margin-bottom: 0!important;
    }
  }
}

.nc-open-content {
  input:not(.ant-select-selection-search-input,.ant-input){
    width: 50%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  .ant-select-selector {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label {
    text-align: left;
    width: 110px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    color: #535353;
  }
}

.search input {
  width: 100%;
  border: none !important;
}

.tables :deep(td),
.tables :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}

.tables :deep(th) {
  text-align: center !important;
  font-weight: bold;
  background-color: #cccccc;
  line-height:30px;
}

.bg-white{
  width: 250px !important;
  min-height: 385px !important;
  height: calc(100%);
  border: 1px #cccccc solid;
  background:white !important;
}

:deep(.ant-table-row-selected) td{
  background: #0096c7 !important;
  a {
    color: #ffffff;
  }
}

:deep(.ant-tree-list){
  margin: 0 !important;
  padding: 0 !important;
}



</style>
<style>
.head-title .scroll-container .scrollbar__wrap {
  margin-bottom: -45px !important;
}

</style>
