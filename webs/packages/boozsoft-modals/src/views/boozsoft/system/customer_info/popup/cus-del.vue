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
        <span style="font-size: 20px;line-height:30px;">
          <DeleteOutlined style="font-size: 20px;font-weight: bold"/>&nbsp;&nbsp;回收站 － 客户信息
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
                @search="reload"
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
                :scroll="{ y: 230 }"
                :bordered="true"
                @register="registerTable"
                @fetch-success="fetchsuccess"
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
import {BasicModal, useModalInner} from '/@/components/Modal'
import {DeleteOutlined} from '@ant-design/icons-vue'
import {Button as AButton, Input as AInput, Select as ASelect,} from 'ant-design-vue'
import {useMessage} from "/@/hooks/web/useMessage";
import {BasicTable, useTable} from "/@/components/Table";
import {delCusArr, findAllIsDel, recoverIsDel} from "/@/api/record/costomer_data/customer";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {saveLog} from "/@/api/record/system/group-sys-login-log";
import {aoaToSheetXlsx} from "/@/components/Excel";


const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const database = ref('');
const accountInfo = ref({});
const {
  createErrorModal,
  createConfirm,
  createWarningModal
} = useMessage()

const emit = defineEmits(['register', 'save'])
const [register, {closeModal,setModalProps}] = useModalInner(async(data) => {
  database.value=data.database
  accountInfo.value=data.accountInfo
  reload()
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
const pageParameter = reactive({
  searchConditon: {
    requirement: 'custCode',
    value: '',
  },
})
// 这是示例组件
const [registerTable, {reload,getColumns,setPagination,getSelectRows,getDataSource}] = useTable({
  api: useRouteApi(findAllIsDel,{schemaName:database}),
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  pagination:{ pageSize: 50,showSizeChanger: true, pageSizeOptions: ['50','100','300','500','800'],showTotal: t => `总共${t}条数据` },
  searchInfo: pageParameter
})
async function fetchsuccess(data) {
  await setPagination({
    total: getDataSource().length
  })
}

const thisCheckKey:any = ref('')
async function recoverList() {
  if(getSelectRows().length==0){
    confirm('至少选择一条数据!!')
    return false
  }
  createConfirm({
    iconType: 'warning',
    title: '提示',
    content: '你确认要还原数据吗？',
    onOk: async() => {
      await useRouteApi(recoverIsDel,{schemaName:database})({id:getSelectRows().map(a=>a.id).join(',')})
        .then(async (a)=>{
          // 埋点-操作日志
          getSelectRows().forEach(async (v)=>{
            // 埋点-操作日志
            let logs='操作内容【还原客户信息】,账套代码【'+accountInfo.value.coCode+'】,账套名称【'+accountInfo.value.companyName+'】,客户编码【'+v.custCode+'】,客户全称【'+v.custName+'】,客户简称【'+v.custAbbname+'】'
            /************** 记录操作日志 ****************/
            let map={
              loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
              userId: useUserStoreWidthOut().getUserInfo.username,
              userName: useUserStoreWidthOut().getUserInfo.realName,
              optModule:'master_data',
              optFunction:'客户',
              optRange:'1',
              uniqueCode:database.value,
              optAction:'还原',
              optContent:logs,
            }
            await saveLog(map)
            /************** 记录操作日志 ****************/
          })
          reload()
        })
    }
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
      await useRouteApi(delCusArr,{schemaName: database})({id:getSelectRows().map(a=>a.id).join(',')})
        .then(async (a)=>{
          // 埋点-操作日志
          getSelectRows().forEach(async (v)=>{
            // 埋点-操作日志
            let logs='操作内容【删除客户信息】,账套代码【'+accountInfo.value.coCode+'】,账套名称【'+accountInfo.value.companyName+'】,客户编码【'+v.custCode+'】,客户全称【'+v.custName+'】,客户简称【'+v.custAbbname+'】'
            /************** 记录操作日志 ****************/
            let map={
              loginTime:new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," "),
              userId: useUserStoreWidthOut().getUserInfo.username,
              userName: useUserStoreWidthOut().getUserInfo.realName,
              optModule:'master_data',
              optFunction:'客户',
              optRange:'1',
              uniqueCode:database.value,
              optAction:'删除',
              optContent:logs,
            }
            await saveLog(map)
            /************** 记录操作日志 ****************/
          })
          reload()
        })
    }
  })
}
// 导出Excel
const exportExcel123 = async () => {
  const data = JSON.parse(JSON.stringify(getDataSource()))
  const columns = [
    {title: '客户编码',key: 'custCode'},
    {title: '客户全称',key: 'custName'},
    {title: '客户简称',key: 'custAbbname'},
    {title: '税号',key: 'custSregcode'},
    {title: '分类名称',key: 'uniqueCustclassName'},
    {title: '母公司',key: 'parentName'},
    {title: '对应客户',key: 'supName'},
    {title: '区划(省-市-区)',key: 'province'},
    {title: '详细地址',key: 'address2'},
    {title: '电话',key: 'telephone'},
    {title: '手机',key: 'cellPhoneNum'},
    {title: '网站',key: 'website'},
    {title: 'EMail',key: 'email'},
    {title: '行业性质',key: 'industryclassName'},
    {title: '开户银行',key: 'custBank'},
    {title: '开户地',key: 'bankArea'},
    {title: '银行账户',key: 'custAccount'},
    {title: '银行行号',key: 'bankCode'}
  ]
  const dataArr:any=[]
  data.forEach(v=>{
    let province=v.province=='' || v.province==null?'':v.province+'-';
    let city=v.city=='' || v.city==null?'':v.city+'-';
    let area=v.area=='' || v.area==null?'':province+city+v.area;
    let temp:any=[]
    temp[0]=v.custCode,
      temp[1]=v.custName,
      temp[2]=v.custAbbname,
      temp[3]=v.custSregcode,
      temp[4]=v.uniqueCustclassName,
      temp[5]=v.parentName,
      temp[6]=v.supName,
      temp[7]=area,
      temp[8]=v.address2,
      temp[9]=v.telephone,
      temp[10]=v.cellPhoneNum,
      temp[11]=v.website,
      temp[12]=v.email,
      temp[13]=v.industryclassName,
      temp[14]=v.custBank,
      temp[15]=v.bankArea,
      temp[16]=v.custAccount,
      temp[17]=v.bankCode
    dataArr.push(temp)
  })
  aoaToSheetXlsx({
    data: dataArr,
    header: columns.map(item=>item.title),
    filename: accountInfo.value.companyName+'-客户信息.xlsx',
  });
}
async function handleOk() {
  emit('save', unref([]));
  closeModal();
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
