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
        <div style="display: flex;height:30px;margin-top: 10px;" class="vben-basic-title">
        <span style="font-size: 24px;line-height:30px;">
          <PlusCircleOutlined style="font-size: 28px;font-weight: bold"/>&nbsp;&nbsp;新增申请 － 人员信息
        </span>
        </div>
      </template>
      <div class="nc-open-content">
        <div class="open-content-up" style="margin-top: 10px;">
          <div style="background:#0096c7;padding:10px;border-radius: 2px;margin-bottom:10px;display: flex;justify-content : space-between;">
            <div class="a1">
              <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
                <a-select-option style="font-size: 12px;" value="psnCode">人员编码</a-select-option>
                <a-select-option style="font-size: 12px;" value="psnName">人员名称</a-select-option>
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
                <template #psnName="{ record }">
                  <a class="tableUStyle" @click="openEdit(record,false)">{{ record.psnName }}</a>
                </template>
                <template #uniquePsnType="{ record }"> {{ formatUniquePsnType(record.uniquePsnType) }}</template>
                <template #psnSex="{ record }"> {{ formatPsnSex(record.psnSex) }}</template>
                <template #psnType="{ record }"> {{ formatPsnType(record.psnType) }}</template>
                <template #cellPhoneNum="{ record }">
                  <span v-if="record.cellPhoneNum!=null&&record.cellPhoneNum!=''">{{plusStr(record.cellPhoneNum,3,4,'****')}}</span>
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
  deletePsn,
  findByCtypeAndOriginId,
  savePsn
} from "/@/api/record/system/group-psn";
import {psnTypeFindAll} from "/@/api/record/system/group-psn-type";

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
const originId:any = ref('')
const [register, {closeModal,setModalProps}] = useModalInner(async(data) => {
  originId.value = data.originId
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
    title: '人员名称',
    dataIndex: 'psnName',
    align: 'left',
    ellipsis: true,
    width: 150,
    slots: {customRender: 'psnName'}
  },
  {
    title: '人员类别',
    dataIndex: 'uniquePsnType',
    width: 100,
    ellipsis: true,
    slots: {customRender: 'uniquePsnType'}
  },
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
]

const psnTypeList:any = ref([])
onMounted(async () => {
  const psnTypeList1 = await psnTypeFindAll()
  psnTypeList.value = psnTypeList1.items
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
  const res:any = await findByCtypeAndOriginId(originId.value)
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

import Add from './apply-add.vue';
import Edit from './apply-edit.vue';
const [registerAddPage, {openModal: openAddPage}] = useModal()
const [registerEditPage, {openModal: openEditPage}] = useModal()
const val: any = {
  id: null,
  uniqueCode: '',
  psnName: '',
  psnSex: '0',
  psnType: '1',
  psnCode: '',
  uniqueCodeDept: '',
  jobNum: '',
  psnPost: '',
  uniquePsnType: '',
  cellPhoneNum: '',
  countryId: '',
  psnEmail: '',
  psnAddress: '',
  province: '',
  city: '',
  district: '',
  psnQq: '',
  psnWechat: '',
  documentType: '',
  documentCode: '',
  psnStation: '',
  entryDate: null,
  psnBank: '',
  bankArea: '',
  bankAccount: '',
  bankNum: '',
  flag: '1',
  originId: '',
}
const openAdd = () => {
  val.originId = originId.value
  val.isEdit = true
  openAddPage(true, {
    data: val,
    isState: '0'
  })
}
const openEdit = (data: any, flag) => {
  data.isEdit = flag
  openEditPage(true, {
    data: data,
    isState: '2'
  })
}

async function saveData(data: any) {
  // await savePsn(data)
  await reloadPsn()
}

const editOpen = () => {
  if (checkRow.value.length == 1) {
    checkRow.value[0].isEdit = true
    openEditPage(true, {
      data: checkRow.value[0],
      isState: '1'
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
  await savePsn(data)
  await reloadPsn()
}

import Excel from './apply-excel.vue';
const [registerExcelPage, {openModal: openExcelPage}] = useModal()
const openExcel = () => {
  openExcelPage(true, {
    originId: originId.value
  })
}
async function saveExcel(data) {
  await reloadPsn()
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
