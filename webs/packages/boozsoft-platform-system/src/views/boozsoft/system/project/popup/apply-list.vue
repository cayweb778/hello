<template>
    <BasicModal
      width="900px"
      v-bind="$attrs"
      title="新增申请"
      @ok="handleOk()"
      @cancel="handleOk()"
      wrapClassName="head-title"
      @register="register"
    >
      <template #title>
        <div style="display: flex;height:30px;margin-top: 10px;margin-left: 10px;" class="vben-basic-title">
        <span style="font-size: 24px;line-height:30px;">
          <PlusCircleOutlined style="font-size: 28px;font-weight: bold"/>&nbsp;&nbsp;新增申请 － 项目信息
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
                @click="openAdd()"
              ><span>新增</span></button>
              <button
                type="button"
                class="ant-btn ant-btn-me"
                @click="openExcel()"
              ><span>导入</span></button>
              <button
                type="button"
                class="ant-btn ant-btn-me"
                @click="editOpen()"
              ><span>修改</span></button>
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
              </BasicTable>
          </div>
        </div>
      </div>

      <template #footer>
        <div>
          <a-button @click="handleOk()" type="primary">关闭</a-button>
        </div>
      </template>

      <Add
        @save="saveData"
        @register="registerAddPage"
      />
      <Edit
        @save="editData"
        @register="registerEditPage"
      />

      <Excel @save="saveExcel" @register="registerExcelPage"/>

    </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import { PageWrapper } from '/@/components/Page'
import {nextTick, onMounted, reactive, ref, unref} from 'vue'
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import {
  PlusCircleOutlined
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
  deleteProject,
  findByCtypeAndOriginId, findByCtypeAndTenantId, saveProject
} from "/@/api/record/system/group-project";

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
const dynamicTenantId:any = ref('')
const originId:any = ref('')
const projectClassCtl:any = ref('')
const itemCode:any = ref('')
const cateCode:any = ref('')
const [register, {closeModal,setModalProps}] = useModalInner(async(data) => {
  dynamicTenantId.value = data.dynamicTenantId
  originId.value = data.originId
  projectClassCtl.value = data.projectClassCtl
  itemCode.value = data.itemCode
  cateCode.value = data.cateCode
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
    width: 250,
    slots: {customRender: 'projectName'}
  },
]

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
  const res:any = await findByCtypeAndTenantId(originId.value)
  projectList.value = res
  // console.log(thisCheckKey.value)
  tableDataAll.value = projectList.value
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
    if (formItems.value.selectType == 'projectCode' && formItems.value.selectValue != '') {
      return item.projectCode.indexOf(formItems.value.selectValue) != -1
    }
    if (formItems.value.selectType == 'projectName' && formItems.value.selectValue != '') {
      return item.projectName.indexOf(formItems.value.selectValue) != -1
    }
    return item
  })
  await setPagination({
    total: tableData.value.length
  })
}

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

import Add from './apply-add.vue';
import Edit from './apply-edit.vue';
const [registerAddPage, {openModal: openAddPage}] = useModal()
const [registerEditPage, {openModal: openEditPage}] = useModal()
const val: any = {
  id: null,
  uniqueCode: '',
  projectCode: '',
  projectName: '',
  projectCateCode: '',
  projectClassCode: '',
  jiesuan: '0',
  startDate: null,
  endDate: null,
  psnInCharge: '',
  deptCode: '',
  isEdit: true,
  itemCode: '',
  isState: '0',
  originId: ''
}
const openAdd = () => {
  val.tenantId = dynamicTenantId.value
  val.originId = originId.value
  val.itemCode = itemCode.value
  val.projectCateCode = cateCode.value
  val.projectClassCtl = projectClassCtl.value
  val.isEdit = true
  val.isState = '0'
  openAddPage(true, {
    data: val
  })
}
const openEdit = (data: any, flag) => {
  data.tenantId = dynamicTenantId.value
  data.originId = originId.value
  data.itemCode = itemCode.value
  data.projectCateCode = cateCode.value
  data.projectClassCtl = projectClassCtl.value
  data.isEdit = flag
  data.isState = '2'
  openEditPage(true, {
    data: data
  })
}

async function saveData(data: any) {
  await reloadProject()
}

const editOpen = () => {
  if (checkRow.value.length == 1) {
    checkRow.value[0].tenantId = dynamicTenantId.value
    checkRow.value[0].originId = originId.value
    checkRow.value[0].itemCode = itemCode.value
    checkRow.value[0].projectCateCode = cateCode.value
    checkRow.value[0].projectClassCtl = projectClassCtl.value
    checkRow.value[0].isEdit = true
    checkRow.value[0].isState = '1'
    openEditPage(true, {
      data: checkRow.value[0],
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '修改',
      content: '请选择一条进行修改！'
    })
  }
}

async function editData(data) {
  data.successState = '0'
  await saveProject(data)
  await reloadProject()
}

import Excel from './apply-excel.vue';
const [registerExcelPage, {openModal: openExcelPage}] = useModal()
const openExcel = () => {
  openExcelPage(true, {
    originId: originId.value,
    dynamicTenantId: dynamicTenantId.value,
    itemCode: itemCode.value,
    projectCateCode: cateCode.value,
    projectClassCtl: projectClassCtl.value,
  })
}
async function saveExcel(data) {
  await reloadProject()
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
