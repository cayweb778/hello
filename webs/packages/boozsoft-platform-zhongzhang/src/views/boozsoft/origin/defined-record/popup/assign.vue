<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="组织授权分配"
    wrapClassName="head-title"
    @register="register"
  >
    <template #title>
      <div style="display: flex;height:30px;margin-top: 10px;" class="vben-basic-title">
        <span style="font-size: 24px;line-height:30px;">
          <ShareAltOutlined style="font-size: 28px;font-weight: bold"/>&nbsp;&nbsp;组织授权分配 － 自定义项档案
        </span>
      </div>
    </template>
    <div class="nc-open-content">
      <div class="open-content-up" style="margin-top: 10px;">
        <div style="background:#0096c7;padding:10px;border-radius: 2px;margin-bottom:10px;display: flex;justify-content : space-between;">
          <div class="a1" style="margin-top: 5px;">
            <span style="color: #ffffff;font-weight: bold;">账套</span>
            <a-select v-model:value="accId" @change="changeOrigin()" style="width: 250px;margin-top: -10px;margin-left: 10px">
              <a-select-option v-for="item in accountList" :key="item.accId" :value="item.accId">
                {{ item.accName }}
              </a-select-option>
            </a-select>
          </div>
          <div class="a1">
            <a-select v-model:value="formItems.selectType" style="width: 130px;font-size: 12px;" class="special_select">
              <a-select-option style="font-size: 12px;" value="recordCode">自定义档案编码</a-select-option>
              <a-select-option style="font-size: 12px;" value="recordName">自定义档案名称</a-select-option>
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
              @click="assignData()"
              v-if="activeKey!='1'"
            ><span>分配</span></button>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              @click="unAssignData()"
              v-if="activeKey=='1'"
            ><span>取消分配</span></button>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              @click="reloadDefinedRecord()"
            ><span>刷新</span></button>
          </div>
        </div>

        <div style="height: 350px;padding:0;display: flex;justify-content : space-between;">
          <a-tabs size="small" tab-position="left" v-model:activeKey="activeKey" @change="changeOrigin">
            <a-tab-pane key="1" tab="已分配">
              <div style="margin-top: -10px;overflow:auto;max-height: 350px;margin-left: -15px;">
                <BasicTable
                  :row-selection="{ type: 'checkbox', selectedRowKeys: state1.selectedRowKeys, onChange: onSelectChange1 }"
                  :scroll="{ y: 270 }"
                  @register="registerTable1"
                  :dataSource="tableData"
                  class="tables"
                >
                  <template #flag="{ record }">
                      <span>
                        <a-tag :color="record.flag == '1' ? 'green' : 'volcano'">
                          {{ record.flag == '1' ? '已引入' : '未引入' }}
                        </a-tag>
                      </span>
                  </template>
                </BasicTable>
              </div>
            </a-tab-pane>
            <a-tab-pane key="2" tab="未分配">
              <div style="margin-top: -10px;overflow:auto;max-height: 350px;margin-left: -15px;">
                <BasicTable
                  :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
                  :scroll="{ y: 270 }"
                  @register="registerTable"
                  :dataSource="tableData"
                  class="tables"
                >
                </BasicTable>
              </div>
            </a-tab-pane>
          </a-tabs>
        </div>
      </div>
    </div>

    <template #footer>
      <div>
        <a-button @click="closeModal()" type="primary">关闭</a-button>
      </div>
    </template>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import { PageWrapper } from '/@/components/Page'
import {nextTick, onMounted, reactive, ref, unref} from 'vue'
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import {
  ShareAltOutlined
} from '@ant-design/icons-vue'
import {Select as ASelect, Input as AInput, Checkbox as ACheckbox,Button as AButton,Tabs as ATabs,Tag as ATag , message} from 'ant-design-vue'

const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const ATabPane = ATabs.TabPane
import {useMessage} from "/@/hooks/web/useMessage";
import {BasicTable, useTable} from "/@/components/Table";
import {getDefinedRecordList} from "/@/api/record/system/origin-defined-record";
import {
  deleteById,
  findByOriginId, findByTenantId,
  save
} from "/@/api/record/system/group-defined-record-account";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {findAllByAccGroup, findDataBase} from "/@/api/record/system/account";
import {
  findByTenantIdAndUniqueCode,
  save as saveDefinedFileAccount
} from "/@/api/record/system/group-defined-file-account";

const {
  createErrorModal,
  createConfirm,
  createWarningModal
} = useMessage()

const emit = defineEmits(['register', 'save'])

const formItems: any = ref({
  selectType: 'recordCode',
  selectValue: ''
})

const activeKey = ref(1)

const accountList: any = ref([])
const tableData:any = ref([]);
const tableDataAll:any = ref([]);
//查询组织
const organizeList:any = ref([])
const originId:any = ref('')
const accId:any = ref('')
const tenantId:any = ref('')
const definedCode:any = ref('')
const [register, {closeModal,setModalProps}] = useModalInner(async(data) => {
  originId.value = data.originId
  definedCode.value = data.definedCode
  accountList.value = await findAllByAccGroup(originId.value)
  if (accountList.value.length>0){
    accId.value = accountList.value[0].accId
    const dataBase = await findDataBase(accId.value, accountList.value[0].yearStartDate.substring(0,4))
    tenantId.value = dataBase.accountMode
    // console.log(tenantId.value)
  }
  const res:any = await getDefinedRecordList(data.definedCode,data.originId)
  definedRecordList.value = res
  await reloadDefinedRecord()
  state.selectedRowKeys = []
  checkRow.value = []
})

//改变账套
async function changeOrigin() {
  for (const item of accountList.value) {
    if (item.accId == accId.value){
      const dataBase = await findDataBase(accId.value, item.yearStartDate.substring(0,4))
      tenantId.value = dataBase.accountMode
    }
  }
  await reloadDefinedRecord()
}

const columns = [
  {
    title: '自定义档案编码',
    dataIndex: 'recordCode',
    width: 150,
    ellipsis: true
  },
  {
    title: '自定义档案名称',
    dataIndex: 'recordName',
    ellipsis: true,
    align: 'left',
  }
]

// 这是示例组件
const [registerTable, {reload,getColumns,setPagination}] = useTable({
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  pagination: false,
})

const columns1 = [
  {
    title: '自定义档案编码',
    dataIndex: 'recordCode',
    width: 150,
    ellipsis: true
  },
  {
    title: '自定义档案名称',
    dataIndex: 'recordName',
    ellipsis: true,
    align: 'left',
  },
  {
    title: '使用状态',
    dataIndex: 'flag',
    width: 100,
    ellipsis: true,
    slots: { customRender: 'flag'}
  },
]

// 这是示例组件
const [registerTable1, {reload: reload1, getColumns: getColumns1,setPagination: setPagination1}] = useTable({
  columns: columns1,
  bordered: true,
  showIndexColumn: false,
  pagination: false,
})

const definedRecordAccountList:any = ref([])
const definedRecordList:any = ref([])
async function reloadDefinedRecord(){
  checkRow.value = []
  checkRow1.value = []
  state.selectedRowKeys = []
  state1.selectedRowKeys = []
  const definedRecordAccount = await findByTenantId(tenantId.value,definedCode.value)
  definedRecordAccountList.value = definedRecordAccount.items
  if(activeKey.value==1){
    tableDataAll.value = definedRecordAccountList.value
  } else {
    tableDataAll.value = definedRecordList.value.filter(item=>{
      return definedRecordAccount.items.map(aa=>aa.uniqueCode).indexOf(item.recordCode) ==-1
    })
  }
  tableData.value = tableDataAll.value
  if (activeKey.value==1){
    await setPagination1({
      total: tableData.value.length
    })
  } else {
    await setPagination({
      total: tableData.value.length
    })
  }
}

onMounted(async () => {
})

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
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
};

//选中内容
const state1 = reactive<{
  selectedRowKeys: [];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const checkRow1: any = ref([])
const onSelectChange1 = (selectedRowKeys, row) => {
  // console.log('selectedRowKeys changed: ', row);
  state1.selectedRowKeys = selectedRowKeys;
  checkRow1.value = row
};

async function handleOk() {
  emit('save', unref(formItems));
  closeModal();
}

async function onSearch(){
  tableData.value = tableDataAll.value.filter(item => {
    if (formItems.value.selectType == 'recordCode' && formItems.value.selectValue != '') {
      return item.recordCode.indexOf(formItems.value.selectValue) != -1
    }
    if (formItems.value.selectType == 'recordName' && formItems.value.selectValue != '') {
      return item.recordName.indexOf(formItems.value.selectValue) != -1
    }
    return item
  })
  await setPagination({
    total: tableData.value.length
  })
}

async function assignData() {
  if (checkRow.value.length > 0) {
    let dateTime = new_Date()
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      const item1 = {
        id: null,
        uniqueCode: item.recordCode,
        ctype: '2',
        originId: originId.value,
        tenantId: tenantId.value,
        assignUser: useUserStoreWidthOut().getUserInfo.id,
        assignDate: dateTime,
        flag: '0',
        acceptUser: '',
        acceptDate: '',
        definedCode: item.definedCode,
      }
      await save(item1)
      const res = await findByTenantIdAndUniqueCode(tenantId.value,item.definedCode)
      if (res.length==0) {
        const item2 = {
          id: null,
          uniqueCode: item.definedCode,
          ctype: '2',
          originId: originId.value,
          tenantId: tenantId.value,
          assignUser: useUserStoreWidthOut().getUserInfo.id,
          assignDate: dateTime,
          flag: '0',
          acceptUser: '',
          acceptDate: '',
        }
        await saveDefinedFileAccount(item2)
      }
    }
    message.success('分配成功!')
    await reloadDefinedRecord()
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '分配',
      content: '请选择需要分配的内容！'
    })
  }
}

async function unAssignData() {
  if (checkRow1.value.length > 0) {
    for (let i = 0; i < checkRow1.value.length; i++) {
      const item = checkRow1.value[i]
      if (item.flag=='1'){
        createWarningModal({
          iconType: 'warning',
          title: '取消分配',
          content: '授权已使用，不能取消分配！'
        })
        return false
      }
    }
    for (let i = 0; i < checkRow1.value.length; i++) {
      const item = checkRow1.value[i]
      await deleteById(item.id)
    }
    message.success('取消分配成功!')
    await reloadDefinedRecord()
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '取消分配',
      content: '请选择需要取消分配的内容！'
    })
  }
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

.tables :deep(td),
.tables :deep(th) {
  font-size: 14px !important;
  padding: 0 5px !important;
}

.tables :deep(th) {
  text-align: center !important;
  font-weight: bold;
  background-color: #cccccc;
  line-height:30px;
}

:deep(.ant-table-row-selected) td{
  background: #0096c7 !important;
}

:deep(.ant-tabs-left) > .ant-tabs-nav{
  padding-top: 10px;
  width: 120px;
  text-align: center;
  background-color: #eeeeee;
  font-weight: bold;
  font-size: 14px;
}
</style>
<style lang="less">
.nc-open-content {
  label {
    text-align: left;
    width: 110px;
    display: inline-block;
    padding-top: 2px;
    padding-bottom: 2px;
    color: #535353;
  }
}
.ant-select,.ant-input{
  line-height: 25px;
  //border: 1px solid #000000;
}
</style>
<style>
.head-title .scroll-container .scrollbar__wrap {
  margin-bottom: 0 !important;
}

</style>
