<template>
    <BasicModal
      width="900px"
      v-bind="$attrs"
      title="人员回收站"
      @ok="handleOk()"
      @cancel="handleOk()"
      wrapClassName="head-title"
      @register="register"
    >
      <template #title>
        <div style="display: flex;" class="vben-basic-title">
        <span style="font-size: 24px;line-height:30px;margin-top: 10px;margin-left: 10px;">
          <DeleteOutlined style="font-size: 28px;font-weight: bold"/>&nbsp;&nbsp;人员回收站
        </span>
        </div>
      </template>
      <div class="nc-open-content" style="margin:0;padding:0;">
        <div class="open-content-up" style="margin:0;padding:0;">
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
                @click="recoverList()"
              ><span>还原</span></button>
              <button
                type="button"
                class="ant-btn ant-btn-me"
                @click="delList()"
              ><span>彻底删除</span></button>
              <button
                type="button"
                class="ant-btn ant-btn-me"
                @click="exportExcel()"
              ><span>导出</span></button>
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
                <template #psnSex="{ record }"> {{ formatPsnSex(record.psnSex) }}</template>
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

    </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import { PageWrapper } from '/@/components/Page'
import {nextTick, onMounted, reactive, ref, unref} from 'vue'
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import {
  DeleteOutlined
} from '@ant-design/icons-vue'
import {
  Select as ASelect,
  Input as AInput,
  Checkbox as ACheckbox,
  Button as AButton,
  message
} from 'ant-design-vue'

const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
import {useMessage} from "/@/hooks/web/useMessage";
import {BasicTable, useTable} from "/@/components/Table";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {deletePsn, findPsnDelList, savePsn} from "/@/api/record/system/psn";
// import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
const aoaToSheetXlsx=null

const {
  createErrorModal,
  createConfirm,
  createWarningModal
} = useMessage()

const emit = defineEmits(['register', 'save'])

const formItems: any = ref({
  selectType: 'psnName',
  selectValue: ''
})
const accountList: any = ref([])
const dynamicTenantId = ref('')
const tableData:any = ref([]);
const tableDataAll:any = ref([]);
const [register, {closeModal,setModalProps}] = useModalInner(async(data) => {
  dynamicTenantId.value = data.dynamicTenantId
  await reloadPsn()
  state.selectedRowKeys = []
  checkRow.value = []
})

const columns = [
  {
    title: '人员编码',
    dataIndex: 'psnCode',
    ellipsis: true,
    width: 100
  },
  {
    title: '人员姓名',
    dataIndex: 'psnName',
    align: 'left',
    ellipsis: true,
    width: 150,
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
    title: '删除人',
    dataIndex: 'delName',
    width: 120,
    ellipsis: true
  },
  {
    title: '删除时间',
    dataIndex: 'delDate',
    width: 150,
    ellipsis: true
  }
]

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

// 这是示例组件
const [registerTable, {reload,getColumns,setPagination}] = useTable({
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  pagination: false,
  searchInfo: {
    accId: '',
    flag: '0',
  }
})

const psnList:any = ref([])
const thisCheckKey:any = ref('')
async function reloadPsn(){
  const res:any = await useRouteApi(findPsnDelList,{schemaName: dynamicTenantId})({})
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

async function recoverList() {
  if (checkRow.value.length > 0) {
    createConfirm({
      iconType: 'warning',
      title: '提示',
      content: '你确认要还原吗?',
      onOk: async () => {
        for (let i = 0; i < checkRow.value.length; i++) {
          const item = checkRow.value[i]
          item.isDel = "0"
          item.delName = null
          item.delDate = null
          await useRouteApi(savePsn, {schemaName: dynamicTenantId})(item)
          // await useRouteApi(deletePsn, {schemaName: dynamicTenantId})(item)
        }
        checkRow.value = []
        message.success('恢复成功！')
        await reloadPsn()
      },
      onCancel: () => {
        return false
      }
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '恢复',
      content: '请选择需要还原的内容！'
    })
  }
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
          // await useRouteApi(savePsn, {schemaName: dynamicTenantId})(item)
          await useRouteApi(deletePsn, {schemaName: dynamicTenantId})(item)
        }
        checkRow.value = []
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

//文件导出
function exportExcel(){
  const lanmu:any = getColumns()
  const list = psnList.value
  const arrData = list.map(item => {
    item.psnSex=formatPsnSex(item.psnSex)
    item.psnType=item.psnType=='1'?'内部人员':'外部人员'
    item.cellPhoneNum = item.cellPhoneNum==''?'':item.cellPhoneNum.length>10?plusStr(item.cellPhoneNum,3,4,'****'):plusStr(item.cellPhoneNum,3,0,'******')
    return item
  }).map(item=>lanmu.map(column=>item[column.dataIndex]))
  aoaToSheetXlsx({
    data: arrData,
    header: lanmu.map(item=>item.title),
    filename: '人员信息模板.xlsx',
  });
}

function plusStr(str, frontLen, endLen,cha) {
  return str.substring(0, frontLen) + cha + str.substring(str.length - endLen);
}
</script>
<style scoped="scoped" lang="less">
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

/*.tables :deep(td),
.tables :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}*/

.tables :deep(th) {
  text-align: center !important;
  font-weight: bold;
  background-color: #cccccc;
  line-height:20px;
  padding: 0 !important;
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

.head-title .tables td,.head-title .tables th{
  display:table-cell;
  font-size: 14px !important;
  padding: 0 !important;
  margin: 0 !important;
}

.head-title .tables .ant-checkbox-wrapper{
  width: 100px;
  vertical-align:middle;
  text-align:center;
  height: 30px;
  margin-left: 20px !important;
  margin-top: 5px !important;
  margin-bottom: 0 !important;
}

</style>
