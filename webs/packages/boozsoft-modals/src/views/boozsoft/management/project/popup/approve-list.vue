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
            <FormOutlined style="font-size: 28px;font-weight: bold"/>&nbsp;&nbsp;生效审批 － 项目信息
          </span>
        </div>
      </template>
      <div class="nc-open-content">
        <div class="open-content-up" style="margin-top: 10px;">
          <div style="background:#0096c7;padding:10px;border-radius: 2px;margin-bottom:10px;display: flex;justify-content : space-between;">
            <div class="a1">
              <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
                <a-select-option style="font-size: 12px;" value="projectCode">项目编码</a-select-option>
                <a-select-option style="font-size: 12px;" value="projectName">项目名称</a-select-option>
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
                <template #projectName="{ record }">
                  <a class="tableUStyle" @click="openEdit(record,false)">{{ record.projectName }}</a>
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
  deleteProject, findByCode, findByName,
  findByNotSuccessState,
  saveProject
} from "/@/api/record/system/group-project";
import {saveProject as originSaveProject} from "/@/api/record/system/origin-project";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {save} from "/@/api/record/system/group-project-account";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {saveProject as sysSaveProject} from "/@/api/record/system/project";
import {getOrganizeAll} from "/@/api/record/group/im-organize";
import {findByAllSysAccPeriodGroupDataBase} from "/@/api/record/system/account";

const {
  createErrorModal,
  createConfirm,
  createWarningModal
} = useMessage()

const emit = defineEmits(['register', 'save'])

const formItems: any = ref({
  selectType: 'projectCode',
  selectValue: ''
})
const accountList: any = ref([])
const tableData:any = ref([]);
const tableDataAll:any = ref([]);
const [register, {closeModal,setModalProps}] = useModalInner(async(data) => {
  await reloadProject()
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
  {
    title: '项目编码',
    dataIndex: 'projectCode',
    ellipsis: true,
    width: 100
  },
  {
    title: '项目名称',
    dataIndex: 'projectName',
    align: 'left',
    ellipsis: true,
    width: 150,
    slots: {customRender: 'projectName'}
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

const organizeList:any = ref([])
onMounted(async () => {
  organizeList.value = await getOrganizeAll()
  accountList.value = await findByAllSysAccPeriodGroupDataBase()
})

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

const projectList:any = ref([])
const thisCheckKey:any = ref('')
async function reloadProject(){
  const res:any = await findByNotSuccessState()
  projectList.value = res
  // console.log(thisCheckKey.value)
  tableDataAll.value = projectList.value
  tableData.value = tableDataAll.value
  await setPagination({
    total: tableData.value.length
  })
}

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
    if (formItems.value.selectType == 'projectCode' && formItems.value.selectValue != '') {
      return item.projectCode.indexOf(formItems.value.selectValue) != -1
    }
    //通过部门名称过滤
    if (formItems.value.selectType == 'projectName' && formItems.value.selectValue != '') {
      return item.projectName.indexOf(formItems.value.selectValue) != -1
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
      let list:any = []
      const list1 = await findByCode({projectCode: item.projectCode,projectClassCode: item.projectClassCode})
      const list2 = await findByName({projectCode: item.projectCode,projectClassCode: item.projectClassCode})
      list.push(list1)
      list.push(list2)
      if (list.length>0) {
        if (checkRow.value.length > 1){
          createWarningModal({
            iconType: 'warning',
            title: '审批',
            content: item.psnName + '在项目信息中重复不能进行批量审批！'
          })
        } else {
          const project = list[0]
          createConfirm({
            iconType: 'error',
            title: '审批',
            content: item.psnName + '在项目信息中重复,审核后将同步人员信息,你确定要审核吗?',
            onOk: async () => {
              project.isDel = '0'
              project.successState = '1'
              //往集团表插入数据
              await saveProject(project)
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
              project.id = null
              await originSaveProject(project)
              if (item.ctype=='2') {
                //账套表插入数据
                await useRouteApi(sysSaveProject,{schemaName:item.tenantId})(project)
              }
              //删除申请记录
              await deleteProject(item)
              message.success('审批成功！')
              await reloadProject()
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
      const project = await saveProject(item)
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
      project.id = null
      await originSaveProject(project)
      if (item.ctype=='2') {
        //账套表插入数据
        await useRouteApi(sysSaveProject,{schemaName:item.tenantId})(project)
      }
    }
    checkRow.value = []
    state.selectedRowKeys = []
    message.success('审批成功！')
    await reloadProject()
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
    await saveProject(item)
  }
  await reloadProject()
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
          await deleteProject(item)
        }
        checkRow.value = []
        state.selectedRowKeys = []
        message.success('删除成功！')
        await reloadProject()
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
const [registerEditPage, {openModal: openEditPage}] = useModal()

const openEdit = (data: any, flag) => {
  data.isEdit = flag
  data.isState = '2'
  openEditPage(true, {
    data: data
  })
}

const showOpen = () => {
  if (checkRow.value.length == 1) {
    checkRow.value[0].isEdit = true
    checkRow.value[0].isState = '2'
    openEditPage(true, {
      data: checkRow.value[0]
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
  await reloadProject()
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
