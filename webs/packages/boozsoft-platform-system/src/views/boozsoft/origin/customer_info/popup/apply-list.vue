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
          <PlusCircleOutlined style="font-size: 28px;font-weight: bold"/>&nbsp;&nbsp;新增申请 － 客户信息
        </span>
        </div>
      </template>
      <div class="nc-open-content">
        <div class="open-content-up" style="margin-top: 10px;">
          <div style="background:#0096c7;padding:10px;border-radius: 2px;margin-bottom:10px;display: flex;justify-content : space-between;">
            <div class="a1">
              <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 120px;font-size: 12px;" class="special_select">
                <a-select-option style="font-size: 12px;" value="custCode">客户编码</a-select-option>
                <a-select-option style="font-size: 12px;" value="custName">客户全称</a-select-option>
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
      <!-- 集团客户导入页面 -->
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
import Edit from '/@/views/boozsoft/origin/customer_info/popup/edit.vue';
import Add from '/@/views/boozsoft/origin/customer_info/popup/save.vue';
import ImprotExcel from '/@/views/boozsoft/origin/customer_info/popup/improtExcel.vue';
import {
  GroupCustomerAddApi,
  findGroupCusByOrg,
  delCusArrGroup, groupCusAssignOrgSave
} from "/@/api/record/system/customer_group";
import {findByUserIdAndOriginId} from "/@/api/record/system/group-permission";
import {findAllOrgCusClass} from "/@/api/org/cus_class/org_cus_class";
import {saveLog} from "/@/api/record/system/group-sys-login-log";
import {originCustomerSaveAll} from "/@/api/org/cus/org_cus";
import {findByUniqueCode} from "/@/api/record/group/im-organize";



const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const {createErrorModal,createConfirm,createWarningModal} = useMessage()
const [registerEditPage, { openModal: openEditPage }] = useModal();
const [registerAddPage, { openModal: openAddPage }] = useModal();
const [registerImprotExcelPage, { openModal: openImprotExcelPage }] = useModal();

const emit = defineEmits(['register', 'save'])

const orgUnique=ref('')
const pageParameter = reactive({
  searchConditon: {
    requirement: 'custName',
    value: '',
  },
  orgUnique:orgUnique.value,
})

const [register, {closeModal,setModalProps}] = useModalInner(async(data) => {
  orgUnique.value=data.orgUnique
  pageParameter.orgUnique=data.orgUnique
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
    title: '客户编码',
    dataIndex: 'custCode',align:'left',
    ellipsis: true,slots: { customRender: 'custCode'}
  },
  {
    title: '客户全称',
    dataIndex: 'custName',align:'left',
    ellipsis: true,slots: { customRender: 'custName'}
  },
  {
    title: '客户简称',
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
  api:findGroupCusByOrg,
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  pagination: false,
  searchInfo:pageParameter
})

// 组织客户分类
async function findByOrgClass() {
  let map={
    searchValue: '',
    superid: '',
    orgUnique: orgUnique.value,
  }
  let temp=await findAllOrgCusClass(map)
  return temp.items.length
}
async function openAdd() {
  if(await findByOrgClass()==0){
    return createWarningModal({ content: '请先引入客户分类！' });
  }
  let orgInfo=await findByUniqueCode(orgUnique.value)
  /************** 记录操作日志 ****************/
  let map={
    loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
    userId: useUserStoreWidthOut().getUserInfo.username,
    userName: useUserStoreWidthOut().getUserInfo.realName,
    optModule:'org',
    optFunction:'客户',
    optRange:'0',
    uniqueCode:orgUnique.value,
    optAction:'新增申请',
    optContent:'操作内容【点击新增申请-新增】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】',
  }
  await saveLog(map)
  /************** 记录操作日志 END ****************/
  openAddPage(true, {
    orgUnique:orgUnique.value,
  });
}
async function editOpen() {
  if(getSelectRows().length!=1){
    return createWarningModal({content:'请选择一条数据编辑！'})
  }
  editOpen2(getSelectRows()[0],true)
}
async function editOpen2(data,isEdit) {
  let orgInfo=await findByUniqueCode(orgUnique.value)
  /************** 记录操作日志 ****************/
  let map={
    loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
    userId: useUserStoreWidthOut().getUserInfo.username,
    userName: useUserStoreWidthOut().getUserInfo.realName,
    optModule:'org',
    optFunction:'客户',
    optRange:'0',
    uniqueCode:orgUnique.value,
    optAction:'新增申请',
    optContent:'操作内容【点击新增申请-查看】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】',
  }
  await saveLog(map)
  /************** 记录操作日志 END ****************/
  openEditPage(true, {
    data:data,
    orgUnique:orgUnique.value,
    isEdit:isEdit
  });
}
async function openImprot() {
  if(await findByOrgClass()==0){
    return createWarningModal({ content: '请先引入客户分类！' });
  }
  openImprotExcelPage(true, {
    orgUnique:orgUnique.value,
  });
}


async function saveData(data) {
  // 集团档案权限分配表-集团客户
  let orgSaveFlag='0'
  let findByPermission=await findByUserIdAndOriginId(useUserStoreWidthOut().getUserInfo.id,orgUnique.value,'customer')
  if(findByPermission.length>0){
    orgSaveFlag=findByPermission[0].isAuto
  }
  let orgInfo=await findByUniqueCode(orgUnique.value)
  // 0:走集团审批。1:自动分配【集团、组织、组织下属账套。】
  if(orgSaveFlag=='0'){
    data.successState='0'
    data.applyUser=useUserStoreWidthOut().getUserInfo.name
    data.applyDate=getNowDate()
    data.originUnique=orgUnique.value
    data.ctype='1'
    await GroupCustomerAddApi(data).then(async (a)=>{
      /************** 记录操作日志 ****************/
      let map={
        loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
        userId:useUserStoreWidthOut().getUserInfo.username,
        userName:useUserStoreWidthOut().getUserInfo.realName,
        optModule:'org',
        optFunction:'客户',
        optRange:'0',
        uniqueCode:orgUnique.value,
        optAction:'新增申请',
        optContent:'操作内容【新增客户信息】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,客户编码【'+a.custCode+'】,客户全称【'+a.custName+'】,客户简称【'+a.custAbbname+'】',
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
    data.ctype='1'
    data.orgUnique=orgUnique.value
    data.originUnique=orgUnique.value

    await GroupCustomerAddApi(data).then(async (a)=>{
      /************** 记录操作日志 ****************/
      let map={
        loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
        userId:useUserStoreWidthOut().getUserInfo.username,
        userName:useUserStoreWidthOut().getUserInfo.realName,
        optModule:'group',
        optFunction:'客户',
        optRange:'2',
        uniqueCode:orgUnique.value,
        optAction:'新增',
        optContent:'操作内容【新增客户信息】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,客户编码【'+a.custCode+'】,客户全称【'+a.custName+'】,客户简称【'+a.custAbbname+'】',
      }
      // 集团不存在的需要加日志
      if(data.id==undefined){
        await saveLog(map)
      }
      /************** 记录操作日志 End ****************/
      data.dataUnique=a.result.uniqueCode
      data.uniqueCode=a.result.uniqueCode
    })

    data.assignUserId=useUserStoreWidthOut().getUserInfo.id
    data.assignDate=getNowDate()
    data.acceptUserId=useUserStoreWidthOut().getUserInfo.id
    data.acceptDate=getNowDate()
    data.id=null
    // 集团客户信息分配记录表
    await groupCusAssignOrgSave([data]).then(async (a)=>{
      /************** 记录操作日志 ****************/
      let map={
        loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
        userId:useUserStoreWidthOut().getUserInfo.username,
        userName:useUserStoreWidthOut().getUserInfo.realName,
        optModule:'group',
        optFunction:'客户',
        optRange:'2',
        uniqueCode:orgUnique.value,
        optAction:'分配',
        optContent:'操作内容【分配客户信息】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,客户编码【'+a.custCode+'】,客户全称【'+a.custName+'】,客户简称【'+a.custAbbname+'】',
      }
      await saveLog(map)
      /************** 记录操作日志 End ****************/
    })
    // 添加到组织客户信息表
    await originCustomerSaveAll([data]).then(async (a)=>{
      /************** 记录操作日志 ****************/
      let map={
        loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
        userId:useUserStoreWidthOut().getUserInfo.username,
        userName:useUserStoreWidthOut().getUserInfo.realName,
        optModule:'org',
        optFunction:'客户',
        optRange:'0',
        uniqueCode:orgUnique.value,
        optAction:'新增',
        optContent:'操作内容【新增客户信息】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,客户编码【'+a.custCode+'】,客户全称【'+a.custName+'】,客户简称【'+a.custAbbname+'】',
      }
      await saveLog(map)
      /************** 记录操作日志 End ****************/
    })
  }
  reload()

  if(data.closeflag==='add'){
    openAddPage(true, {
      orgUnique:orgUnique.value,
    });
  }
}
async function editData(data) {
  await GroupCustomerAddApi(data).then(()=>{
    clearSelectedRowKeys()
    reload()
  })
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
      let orgInfo=await findByUniqueCode(orgUnique.value)
      for (let i = 0; i < getSelectRows().length; i++) {
        await delCusArrGroup(getSelectRows()[i].id).then(async ()=>{
          /************** 记录操作日志 ****************/
          let map={
            loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
            userId: useUserStoreWidthOut().getUserInfo.username,
            userName: useUserStoreWidthOut().getUserInfo.realName,
            optModule:'org',
            optFunction:'客户',
            optRange:'0',
            uniqueCode:orgUnique.value,
            optAction:'新增申请',
            optContent:'操作内容【删除新增申请】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】,客户编码【'+getSelectRows()[i].custCode+'】,客户全称【'+getSelectRows()[i].custName+'】,客户简称【'+getSelectRows()[i].custAbbname+'】',
          }
          await saveLog(map)
        })
      }
      reload()
    }
  })
}

// 当前时间
function getNowDate() {
  return new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ");
}
async function handleOk() {
  let orgInfo=await findByUniqueCode(orgUnique.value)
  /************** 记录操作日志 ****************/
  let map={
    loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
    userId: useUserStoreWidthOut().getUserInfo.username,
    userName: useUserStoreWidthOut().getUserInfo.realName,
    optModule:'org',
    optFunction:'客户',
    optRange:'0',
    uniqueCode:orgUnique.value,
    optAction:'新增申请',
    optContent:'操作内容【关闭新增申请】,组织代码【'+orgInfo.orgCode+'】,组织名称【'+orgInfo.orgName+'】',
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
