<template>
    <BasicModal
      width="900px"
      v-bind="$attrs"
      title="生效审批"
      @ok="handleOk()"
      @cancel="handleOk()"
      wrapClassName="head-title"
      @register="register"
    >
      <template #title>
        <div style="display: flex;height:30px;margin-top: 10px;" class="vben-basic-title">
          <span style="font-size: 24px;line-height:30px;">
            <FormOutlined style="font-size: 28px;font-weight: bold"/>&nbsp;&nbsp;生效审批 － 人员信息
          </span>
        </div>
      </template>
      <div class="nc-open-content">
        <div class="open-content-up" style="margin-top: 10px;">
          <div style="background:#0096c7;padding:10px;border-radius: 2px;margin-bottom:10px;display: flex;justify-content : space-between;">
            <div class="a1">
              <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
                <a-select-option style="font-size: 12px;" value="psnCode">人员编码</a-select-option>
                <a-select-option style="font-size: 12px;" value="psnName">人员姓名</a-select-option>
              </a-select>
              <a-input-search
                placeholder=""
                style="width: 200px; border-radius: 4px"
                v-model:value="formItems.selectValue"
                @search="onSearch"
              />
            </div>
            <div class="a2">
              <button
                type="button"
                class="ant-btn ant-btn-me"
                @click="showOpen()"
              ><span>查看</span></button>
              <button
                type="button"
                class="ant-btn ant-btn-me"
                @click="approveList()"
              ><span>同意</span></button>
              <button
                type="button"
                class="ant-btn ant-btn-me"
                @click="notApproveList()"
              ><span>驳回</span></button>
              <button
                type="button"
                class="ant-btn ant-btn-me"
                @click="delList()"
              ><span>删除</span></button>
            </div>
          </div>

          <div style="height: 400px;padding:0;display: flex;justify-content : space-between;">
              <BasicTable
                :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
                @row-click="condClick"
                :scroll="{ y: 290 }"
                @register="registerTable"
                :dataSource="tableData"
                class="tables"
              >
                <template #successState="{ record }">
                  <span>
                    <a-tag :color="record.successState == '0' ? 'warning' : 'volcano'">
                      {{ record.successState == '0' ? '未生效' : '已驳回' }}
                    </a-tag>
                  </span>
                </template>
                <template #psnName="{ record }">
                  <a class="tableUStyle" @click="openEdit(record,false)">{{ record.psnName }}</a>
                </template>
                <template #uniquePsnType="{ record }"> {{ formatUniquePsnType(record.uniquePsnType) }}</template>
                <template #psnSex="{ record }"> {{ formatPsnSex(record.psnSex) }}</template>
                <template #psnType="{ record }"> {{ formatPsnType(record.psnType) }}</template>
                <template #cellPhoneNum="{ record }">
                  <span v-if="record.cellPhoneNum!=null&&record.cellPhoneNum!=''">{{plusStr(record.cellPhoneNum,3,4,'****')}}</span>
                </template>
                <template #ctype="{ record }">
                  {{record.ctype=='1'?'组织':'账套'}}
                </template>
                <template #originId="{ record }">
                  {{ formatOriginId(record) }}
                </template>
              </BasicTable>
          </div>
        </div>
      </div>

      <template #footer>
        <div>
          <a-button @click="handleOk()" type="primary">关闭</a-button>
        </div>
      </template>

      <Edit
        @save="saveData"
        @register="registerEditPage"
      />
      <ApproveReason
        @save="saveApproveReasonData"
        @register="registerApproveReasonPage"
      />

    </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import { PageWrapper } from '/@/components/Page'
import {nextTick, onMounted, reactive, ref, unref} from 'vue'
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import {
  FormOutlined
} from '@ant-design/icons-vue'
import {
  Select as ASelect,
  Input as AInput,
  Checkbox as ACheckbox,
  Button as AButton,
  Tag as ATag,
  message
} from 'ant-design-vue'

const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
import {useMessage} from "/@/hooks/web/useMessage";
import {BasicTable, useTable} from "/@/components/Table";
import {
  deletePsn,
  findByNotSuccessState,
  findByPsnNameAndCellPhoneNumAndSuccessState,
  savePsn
} from "/@/api/record/system/group-psn";
import {psnTypeFindAll} from "/@/api/record/system/group-psn-type";
import {savePsn as originSavePsn} from "/@/api/record/system/origin-psn";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {save} from "/@/api/record/system/group-psn-account";

const {
  createErrorModal,
  createConfirm,
  createWarningModal
} = useMessage()

const emit = defineEmits(['register', 'save'])

const formItems: any = ref({
  selectType: 'psnCode',
  selectValue: ''
})
const accountList: any = ref([])
const tableData:any = ref([]);
const tableDataAll:any = ref([]);
const [register, {closeModal,setModalProps}] = useModalInner(async(data) => {
  await reloadPsn()
  state.selectedRowKeys = []
  checkRow.value = []
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
    width: 150,
  },
  /*{
    title: '人员编码',
    dataIndex: 'psnCode',
    ellipsis: true,
    width: 100
  },*/
  {
    title: '人员姓名',
    dataIndex: 'psnName',
    align: 'left',
    ellipsis: true,
    width: 150,
    slots: {customRender: 'psnName'}
  },
  /*{
    title: '人员类别',
    dataIndex: 'uniquePsnType',
    width: 100,
    ellipsis: true,
    slots: {customRender: 'uniquePsnType'}
  },*/
  {
    title: '性别',
    dataIndex: 'psnSex',
    width: 100,
    ellipsis: true,
    slots: {customRender: 'psnSex'}
  },
  {
    title: '手机号',
    dataIndex: 'cellPhoneNum',
    width: 120,
    ellipsis: true,
    slots: {customRender: 'cellPhoneNum'}
  },
  {
    title: '人员属性',
    dataIndex: 'psnType',
    width: 100,
    ellipsis: true,
    slots: {customRender: 'psnType'}
  },
  {
    title: '申请类型',
    dataIndex: 'ctype',
    width: 100,
    ellipsis: true,
    slots: {customRender: 'ctype'}
  },
  {
    title: '申请来源',
    dataIndex: 'originId',
    width: 150,
    align: 'left',
    ellipsis: true,
    slots: {customRender: 'originId'}
  },
  {
    title: '申请人',
    dataIndex: 'applyUser',
    width: 100,
    ellipsis: true,
    slots: {customRender: 'applyUser'}
  },
  {
    title: '申请时间',
    dataIndex: 'applyDate',
    width: 150,
    ellipsis: true,
  },
]

const psnTypeList:any = ref([])
const organizeList:any = ref([])
onMounted(async () => {
  const psnTypeList1 = await psnTypeFindAll()
  psnTypeList.value = psnTypeList1.items
  organizeList.value = await getOrganizeAll()
  accountList.value = await findByAllSysAccPeriodGroupDataBase()
})

function formatUniquePsnType(uniquePsnType: any){
  let str = uniquePsnType
  psnTypeList.value.forEach(item=>{
    if (item.uniqueCode == uniquePsnType) {
      str = item.psnTypeName
    }
  })
  return str
}

function formatPsnSex(sex: String) {
  let str = '男'
  switch (sex) {
    case '0':
      str = '未知的性别'
      break
    case '1':
      str = '男'
      break
    case '2':
      str = '女'
      break
    case '9':
      str = '未说明的性别'
  }
  return str
}

function formatPsnType(type: any) {
  let str = '内部员工'
  switch (type) {
    case '1':
      str = '内部员工'
      break
    case '2':
      str = '外部员工'
      break
  }
  return str
}

function formatOriginId(data){
  let str = ''
  if (data.ctype=='1'){
    //组织
    organizeList.value.forEach(item=>{
      if (data.originId==item.uniqueCode){
        str = item.orgSimpName
      }
    })
  } else {
    //账套
    accountList.value.forEach(item=>{
      if (data.tenantId==item.accId){
        str = item.accName
      }
    })
  }
  return str
}

// 这是示例组件
const [registerTable, {reload,getColumns,setPagination}] = useTable({
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  pagination: {
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['10', '30', '50', '100'],
    showTotal: t => `总共${t}条数据`
  },
})

const psnList:any = ref([])
const thisCheckKey:any = ref('')
async function reloadPsn(){
  const res:any = await findByNotSuccessState()
  psnList.value = res
  // console.log(thisCheckKey.value)
  tableDataAll.value = psnList.value.filter(item=> {
    if (thisCheckKey.value!=null && thisCheckKey.value!='0' && thisCheckKey.value!='' && thisCheckKey.value!='undefined') {
      return thisCheckKey.value.indexOf(item.id)!=-1
    }
    return item
  })
  tableData.value = tableDataAll.value
  await setPagination({
    total: tableData.value.length
  })
}

onMounted(async () => {
})

const condClick = () => {

}

//选中内容
const state = reactive<{
  selectedRowKeys: [];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const checkRow: any = ref([])
const onSelectChange = (selectedRowKeys, row) => {
  // console.log('selectedRowKeys changed: ', row);
  // if(selectedRowKeys.length>0){
    state.selectedRowKeys = selectedRowKeys;
    checkRow.value = row
  // }
};

async function handleOk() {
  emit('save', unref(formItems));
  closeModal();
}

async function onSearch(){
  tableData.value = tableDataAll.value.filter(item => {
    //通过部门编码过滤
    if (formItems.value.selectType == 'psnCode' && formItems.value.selectValue != '') {
      return item.psnCode.indexOf(formItems.value.selectValue) != -1
    }
    //通过部门名称过滤
    if (formItems.value.selectType == 'psnName' && formItems.value.selectValue != '') {
      return item.psnName.indexOf(formItems.value.selectValue) != -1
    }
    return item
  })
  await setPagination({
    total: tableData.value.length
  })
}

//审批
async function approveList() {
  if (checkRow.value.length > 1) {
    let dateTime = new_Date()
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      const list = await findByPsnNameAndCellPhoneNumAndSuccessState(item.psnName, item.cellPhoneNum)
      if (list.length>0) {
        if (checkRow.value.length > 1){
          createWarningModal({
            iconType: 'warning',
            title: '审批',
            content: item.psnName + '在人员信息中重复不能进行批量审批！'
          })
        } else {
          const psn = list[0]
          createConfirm({
            iconType: 'error',
            title: '审批',
            content: item.psnName + '在人员信息中重复,审核后将同步人员信息,你确定要审核吗?',
            onOk: async () => {
              psn.flag = '1'
              psn.isDel = '0'
              psn.successState = '1'
              //往集团表插入数据
              await savePsn(psn)
              //分配表插入数据
              const item1 = {
                id: null,
                uniqueCode: item.uniqueCode,
                ctype: '1',
                originId: item.originId,
                assignUser: useUserStoreWidthOut().getUserInfo.id,
                assignDate: dateTime,
                flag: '1',
                acceptUser: useUserStoreWidthOut().getUserInfo.id,
                acceptDate: dateTime,
              }
              await save(item1)
              if (item.ctype=='2') {
                const item2 = {
                  id: null,
                  uniqueCode: item.uniqueCode,
                  ctype: '2',
                  originId: item.originId,
                  tenantId: item.tenantId,
                  assignUser: useUserStoreWidthOut().getUserInfo.id,
                  assignDate: dateTime,
                  flag: '1',
                  acceptUser: useUserStoreWidthOut().getUserInfo.id,
                  acceptDate: dateTime,
                }
                await save(item2)
              }
              //组织表插入数据
              psn.id = null
              await originSavePsn(psn)
              if (item.ctype=='2') {
                //账套表插入数据
                await useRouteApi(saveSysPsn,{schemaName:item.tenantId})(psn)
              }
              //删除申请记录
              await deletePsn(item)
              message.success('审批成功！')
              await reloadPsn()
            },
            onCancel: () => {
              return false
            }
          })
        }
        return false
      }
    }
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      //修改生效状态
      item.successState = '1'
      item.approveUser = useUserStoreWidthOut().getUserInfo.realName
      item.approveDate = dateTime
      const psn = await savePsn(item)
      //分配表添加记录
      const item1 = {
        id: null,
        uniqueCode: item.uniqueCode,
        ctype: '1',
        originId: item.originId,
        assignUser: useUserStoreWidthOut().getUserInfo.id,
        assignDate: dateTime,
        flag: '1',
        acceptUser: useUserStoreWidthOut().getUserInfo.id,
        acceptDate: dateTime,
      }
      await save(item1)
      if (item.ctype=='2') {
        const item2 = {
          id: null,
          uniqueCode: item.uniqueCode,
          ctype: '2',
          originId: item.originId,
          tenantId: item.tenantId,
          assignUser: useUserStoreWidthOut().getUserInfo.id,
          assignDate: dateTime,
          flag: '1',
          acceptUser: useUserStoreWidthOut().getUserInfo.id,
          acceptDate: dateTime,
        }
        await save(item2)
      }
      //组织表添加记录
      psn.id = null
      await originSavePsn(psn)
      if (item.ctype=='2') {
        //账套表插入数据
        await useRouteApi(saveSysPsn,{schemaName:item.tenantId})(psn)
      }
    }
    checkRow.value = []
    state.selectedRowKeys = []
    message.success('审批成功！')
    await reloadPsn()
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '审批',
      content: '请选择需要审批的内容！'
    })
  }
}

//驳回
import ApproveReason from './approve-reason.vue';
const [registerApproveReasonPage, {openModal: openApproveReasonPage}] = useModal()
async function notApproveList() {
  if (checkRow.value.length > 0) {
    openApproveReasonPage(true, {})
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '驳回',
      content: '请选择需要驳回的内容！'
    })
  }
}

async function saveApproveReasonData(data: any) {
  let dateTime = new_Date()
  for (let i = 0; i < checkRow.value.length; i++) {
    const item = checkRow.value[i]
    //修改生效状态
    item.successState = '-1'
    item.approveUser = useUserStoreWidthOut().getUserInfo.realName
    item.approveDate = dateTime
    item.reason = data.reason
    await savePsn(item)
  }
  // await savePsn(data)
  await reloadPsn()
}

//删除
async function delList() {
  if (checkRow.value.length > 0) {
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        for (let i = 0; i < checkRow.value.length; i++) {
          const item = checkRow.value[i]
          await deletePsn(item)
        }
        checkRow.value = []
        state.selectedRowKeys = []
        message.success('删除成功！')
        await reloadPsn()
      },
      onCancel: () => {
        return false
      }
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '删除',
      content: '请选择需要删除的内容！'
    })
  }
}

function plusStr(str, frontLen, endLen,cha) {
  return str.substring(0, frontLen) + cha + str.substring(str.length - endLen);
}

import Edit from './edit.vue';
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {savePsn as saveSysPsn} from "/@/api/record/system/psn";
import {getOrganizeAll} from "/@/api/record/group/im-organize";
import {findByAllSysAccPeriodGroupDataBase} from "/@/api/record/system/account";
const [registerEditPage, {openModal: openEditPage}] = useModal()

const openEdit = (data: any, flag) => {
  data.isEdit = flag
  openEditPage(true, {
    data: data,
    isState: '2'
  })
}

const showOpen = () => {
  if (checkRow.value.length == 1) {
    checkRow.value[0].isEdit = true
    openEditPage(true, {
      data: checkRow.value[0],
      isState: '2'
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '查看',
      content: '请选择一条进行查看！'
    })
  }
}

async function saveData(data: any) {
  // await savePsn(data)
  await reloadPsn()
}

//获取当年月日
function new_Date() {
  let dateTime
  let yy = new Date().getFullYear()
  let mm = new Date().getMonth() + 1 < 10 ? '0' + (new Date().getMonth()+ 1) : (new Date().getMonth()+ 1)
  let dd = new Date().getDate() < 10 ? '0' + new Date().getDate() : new Date().getDate()
  let hh = new Date().getHours() < 10 ? '0' + new Date().getHours() : new Date().getHours()
  let mf = new Date().getMinutes() < 10 ? '0' + new Date().getMinutes() : new Date().getMinutes()
  let ss = new Date().getSeconds() < 10 ? '0' + new Date().getSeconds() : new Date().getSeconds()
  dateTime = yy + '-' + mm + '-' + dd + ' ' + hh + ':' + mf + ':' + ss
  // console.log(dateTime)
  return dateTime
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
