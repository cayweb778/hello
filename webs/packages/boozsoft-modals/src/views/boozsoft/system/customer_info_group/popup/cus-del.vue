<template>
    <BasicModal
      width="900px"
      v-bind="$attrs"
      title="人员回收站"
      @ok="handleOk()"
      @cancel="handleOk()"
      :closable="false"
      wrapClassName="head-title"
      @register="register"
    >
      <template #title>
        <div style="display: flex;height:30px;" class="vben-basic-title">
        <span style="font-size: 24px;line-height:30px;margin-top: -5px;">
          <DeleteOutlined style="font-size: 28px;font-weight: bold"/>&nbsp;&nbsp;回收站 － 客户信息
        </span>
        </div>
      </template>
      <div class="nc-open-content" style="margin:0;padding:0;">
        <div class="open-content-up" style="margin:0;padding:0;">
          <div style="background:#0096c7;padding:10px;border-radius: 2px;margin-bottom:10px;display: flex;justify-content : space-between;">
            <div class="a1">
              <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 120px;font-size: 12px;" class="special_select">
                <a-select-option style="font-size: 12px;" value="custCode">客户编码</a-select-option>
                <a-select-option style="font-size: 12px;" value="custName">客户名称</a-select-option>
              </a-select>
              <a-input-search
                placeholder=""
                style="width: 200px; border-radius: 4px"
                v-model:value="pageParameter.searchConditon.value"
                @search="reloadCus"
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

          <div style="height: 350px;padding:0;display: flex;justify-content : space-between;">
              <BasicTable
                :row-selection="{ type: 'checkbox' }"
                :scroll="{ y: 350 }"
                @register="registerTable"
                :dataSource="tableData"
                class="tables"
              >

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
import {reactive, ref, unref} from 'vue'
import {BasicModal,useModalInner} from '/@/components/Modal'
import {
  DeleteOutlined
} from '@ant-design/icons-vue'
import {
  Select as ASelect,
  Input as AInput,
  Button as AButton,
} from 'ant-design-vue'
import {useMessage} from "/@/hooks/web/useMessage";
import {BasicTable, useTable} from "/@/components/Table";
import {
  findAll,
  recoverCus,
  delCusArrGroup,
  exportExcelFindAllJSON
} from "/@/api/record/system/customer_group";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useUserStoreWidthOut} from "/@/store/modules/user";
//import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
const aoaToSheetXlsx=null
import {jsonToSheetXlsx} from "/@/components/Excel";
import {findAllByCusBendEq1} from "/@/api/record/system/customer_class_group";

const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option

const {
  createErrorModal,
  createConfirm,
  createWarningModal
} = useMessage()

const emit = defineEmits(['register', 'save'])
const tableData:any = ref([]);
const [register, {closeModal,setModalProps}] = useModalInner(async(data) => {
  await reloadCus()
})

const columns = [
  {
    title: '客户编码',
    dataIndex: 'custCode',
    ellipsis: true,
    slots: { customRender: 'custCode' },fixed: 'left'
  },
  {
    title: '客户全称',
    dataIndex: 'custName',
    ellipsis: true,
    slots: { customRender: 'custName' },fixed: 'left'
  },
  {
    title: '客户简称',
    dataIndex: 'custAbbname',
    ellipsis: true
  },
  // {
  //   title: '税号',
  //   dataIndex: 'custSregcode',
  //   ellipsis: true,
  // },
]

// 这是示例组件
const [registerTable, {reload,getColumns,setPagination,getSelectRows,getDataSource}] = useTable({
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  pagination: false,
  searchInfo: {
    accId: '',
    flag: '0',
  }
})

const thisCheckKey:any = ref('')
const pageParameter = reactive({
  searchConditon: {
    requirement: 'custCode',
    value: '',
  },
  showRulesSize: 'MIN',
  uniqueCustclass:'0',
  database: useCompanyOperateStoreWidthOut().getSchemaName,
  username: useUserStoreWidthOut().getUserInfo.username,
  flag:'1'
})
async function reloadCus(){
  pageParameter.page=1
  pageParameter.size=100000
  let totalSum=await findAll(pageParameter)
  tableData.value = totalSum.items.filter(v=>v.isDel=='1')
}

async function handleOk() {
  emit('save', unref([]));
  closeModal();
}

async function recoverList() {
  if(getSelectRows().length==0){
    confirm('至少选择一条数据!!')
    return false
  }
  getSelectRows().forEach(async (v)=>{
    await recoverCus(v.id)
    reloadCus()
  })
}

async function delList() {
  if(getSelectRows().length==0){
    confirm('至少选择一条数据!!')
    return false
  }
  createConfirm({
    iconType: 'warning',
    title: '提示',
    content: '数据将彻底删除,不可恢复。确定继续吗？',
    onOk: async () => {
      await delCusArrGroup(getSelectRows().map(v=>v.id).join(',')).then(()=>{
        reloadCus()
      })
    }
  })
}

//文件导出
async function exportExcel(){
  const exceljson = await exportExcelFindAllJSON();
  let list=getDataSource()
  let arr=[]
  list.forEach(v=>{
    let temp=exceljson.filter(a=>a.custCode==v.custCode)
    arr.push(temp[0])
  })
  jsonToSheetXlsx({
    data: arr,
    header: {
      custCode: '客户编码',
      custName: '客户名称',
      custAbbname: '客户简称',
      uniqueCustclass: '分类名称',
      custSregcode: '税号',
      manageType: '管理类型',
      uniqueCodeCcus: '母公司',
      uniqueCodeSupplier: '对应供应商',
      province: '区划(省-市-区)',
      address2: '详细地址',
      contacts: '联系人',
      telephone: '电话',
      cellPhoneNum: '手机',
      website: '网站',
      email: 'EMail',
      industryclassName: '行业性质',
      custBank: '开户银行',
      bankArea: '开户地',
      custAccount: '银行账户',
      bankCode: '银行行号',
    },
    filename: '客户信息标准导出.xlsx',
  });
}

function confirm(text) {
  createWarningModal({
    iconType: 'warning',
    title: '提示',
    content: text
  })
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
