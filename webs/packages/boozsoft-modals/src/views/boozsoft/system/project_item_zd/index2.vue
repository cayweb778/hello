<template>
  <div>
    <div class="app-container">
      <div class="app-container-head"><img :src="getThisIndexImg()" class="container-head-img" draggable="false" >
        <div class="container-head-title">
          <b class="noneSpan">项目大类指定设置</b>
        </div>
        <div class="ant-btn-group" data-v-a1ccd506="" style="float: right">
          <button type="button" class="ant-btn ant-btn-me" @click="toSave('1')" v-if="pageParameter.flag==='0'"><span>指定</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="toSave('0')" v-else><span>撤销</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="openImportExcel"><span>导入</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="openImportExcelView"><span>导出表格</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent,router.push('/zhongZhang/home/welcome')"><span>退出</span></button>
        </div>
      </div>

      <div class="app-container-neck">
        <div style="float:left;width: 600px;">
          <AccountPicker  theme="one" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="float: right; margin-left: 10px">
          <a-button style="padding: 0px 12px !important; margin-right: 10px" @click="reload">
            <SyncOutlined :style="{ fontSize: '14px' }" />
          </a-button>
        </div>
        <div style="float: left; position: relative">
          <label>项目大类</label>&nbsp;&nbsp;
          <a-select v-model:value="proItem" style="width: 240px;" class="special_select" @change="proItemCheck">
            <template v-for="item in proItemList">
              <a-select-option :value="item.id">
                {{ item.itemName }}
              </a-select-option>
            </template>
          </a-select>
        </div>
        <div style="float: right; position: relative">
          <!-- 搜索 -->
          <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 130px;" class="special_select">
            <template v-for="item in searchConditonList">
              <a-select-option :value="item.value">
                {{ item.title }}
              </a-select-option>
            </template>
          </a-select>
          <a-input-search
            placeholder=""
            v-model:value="pageParameter.searchConditon.value"
            @search="reload"
            style="width: 150px;border-radius: 4px"
          />
        </div>
      </div>
    </div>
    <div class="app-container">
      <div style="float: left;margin-top: 10px;">
        <a-tabs @change="tabsCheck" tab-position="left">
          <a-tab-pane v-for="(item, i) in typylist" :key="item.value" :tab="`${item.title}`"/>
        </a-tabs>
      </div>
      <div style="width: calc(100% - 143px); float: right;margin-top: 10px;">
        <BasicTable
          ref="tableRef"
          :class="'a-table-font-size-12'"
          :scroll="{ x: windowWidth,y: windowHeight }"
          :bordered="true"
          :row-selection="{ type: 'checkbox',selectedRowKeys: tableSelectedRowKeys, onChange: tableCheck }"
          size="small"
          @register="registerTable"
        >

          <template #ccode="{ record }">
            <span style="float: left;">{{ record.ccode}}</span>
          </template>
          <template #bprogerty="{ record }">
            <span v-if="record.bprogerty==='1'" >借</span>
            <span v-else >贷</span>
          </template>
          <template #ccodeName="{ record }">
            <span style="float: left;">{{ addspace(record.igrade,record.ccodeName) }}</span>
          </template>
        </BasicTable>
        <ImprotExcel @save="reload" @register="registerExcelPage"/>
      </div>
    </div>
    <a-table v-if="tableshow" :columns="columns" :data-source="data" :row-selection="rowSelection" />
  </div>

</template>
<script setup lang="ts">
import router from "/@/router";
import {BasicTable, useTable} from '/@/components/Table';
import {SyncOutlined,} from '@ant-design/icons-vue';
import {
  Checkbox as ACheckbox,
  Table as ATable,
  Modal as AModal,
  Input as AInput,
  Select as ASelect,
  Statistic as AStatistic,
  Tabs as ATabs,
} from 'ant-design-vue';
import {reactive, ref} from 'vue';
import {getCurrentAccountName,getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useTabs} from "/@/hooks/web/useTabs";
import {
  company_editCodePxjzOrCyfx,
  company_findAllCodePxjzOrCyfxOrderByAsc, company_findByFirstCcodeLength,
} from "/@/api/codekemu/codekemu";
import {useMessage} from "/@/hooks/web/useMessage";
import {exportExcel} from "/@/api/record/generalLedger/excelExport";
import {findYearByAccount} from "/@/api/record/system/bank-statement";
import {findDataBase} from "/@/api/record/system/account";
//import {aoaToSheetXlsx} from "/@/components/Excel";
const aoaToSheetXlsx=null
import {findProjectItemList} from "/@/api/record/system/project-item";
import {useModal} from "/@/components/Modal";
import ImprotExcel from './popup/improtExcel.vue';

const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const ATabPane = ATabs.TabPane;
const database = ref(getCurrentAccountName(true));
const columns = [
  {title: '科目编码',dataIndex: 'ccode',key: 'ccode',slots: { customRender: 'ccode' },width:150},
  {title: '科目名称',dataIndex: 'ccodeName',key: 'ccodeName',slots: { customRender: 'ccodeName' },width:150},
  {title: '科目方向',dataIndex: 'bprogerty',key: 'bprogerty',slots: { customRender: 'bprogerty' },width:40},
  {title: '科目类型',dataIndex: 'cclass',key: 'cclass',width:50},
  {title: '辅助项',dataIndex: 'fuzhu',key: 'fuzhu',width:100}
];
const searchConditonList = ref([
  {title:'科目编码',value: 'ccode'},
  {title:'科目名称',value: 'ccodeName'},
  {title:'科目方向',value: 'bprogerty'},
  {title:'科目类型',value: 'cclass'},
])
const typylist=reactive([
  {title:'待指定科目',value:'0'},
  {title:'已指定科目',value:'1'},
])
const tableshow = ref(false);

const proItem = ref('')
const proItemList = ref([])
const tableSelectedRowKeys:any = ref([])
const tableSelectedRowObjs:any = ref([])
const tableRef = ref(null)
const windowWidth = (document.documentElement.clientWidth - 900)
const windowHeight = (window.innerHeight - (330))
const { closeCurrent } =useTabs();
const { createConfirm,createWarningModal,createMessage } = useMessage();
const pageParameter = reactive({
  searchConditon: {
    value:'',
    requirement:'ccode',
  },
  flag: '0',
  type: 'proItem',
  companyName: '',
  iyear: '',
  accId: '',
  proItem: '',
})
const [registerExcelPage, {openModal: openExcelPage}] = useModal();
// 这是示例组件
const [registerTable, { reload,getSelectRows,getDataSource,setTableData,getColumns,clearSelectedRowKeys,deleteSelectRowByKey }] = useTable({
  api: useRouteApi(company_findAllCodePxjzOrCyfxOrderByAsc,{schemaName: database}),
  columns: columns,
  isTreeTable: true,
  pagination:{ pageSize: 200,showSizeChanger: true, pageSizeOptions: ['200','500','1000','1500'],showTotal: t => `总共${t}条数据` },
  searchInfo: pageParameter
});
const dynamicAdReload = async (obj) => {
  database.value=obj.accId+'-'+obj.iyear
  pageParameter.companyName=obj.companyName
  pageParameter.iyear=obj.iyear
  pageParameter.accId=obj.accId

  let temp = await useRouteApi(findProjectItemList, { schemaName: database })('');
  proItemList.value=temp.items
  if(temp.items.length>0){
    proItem.value=temp.items[0].id
  }
  reload()
}
const tabsCheck = (data) => {
  tableSelectedRowObjs.value = [];
  tableSelectedRowKeys.value = [];
  pageParameter.flag=data
  pageParameter.proItem=proItem.value
  reload()
}

const toSave = async (flag) => {
  if(tableSelectedRowObjs.value.length===0){
    createConfirmPop('至少选择一条会计科目进行操作')
    return false;
  }
  let ccode:any=[]
  let list=getSelectRows()
  list.forEach(v=>{
    ccode.push(v.ccode)
  })

  let temp=flag==='1'?proItem.value:''
  let map={
    data:tableSelectedRowObjs.value.map(v=>v.ccode).join(','),
    type:'proItem',
    flag:temp
  }
  await useRouteApi(company_editCodePxjzOrCyfx,{schemaName: database})(map)
  .then(()=>{
    reload()
  })
}
function tableCheck(selectedRowKeys,obj) {
  if(selectedRowKeys.length !=getDataSource().length){
    if (selectedRowKeys.length > tableSelectedRowKeys.value.length) { // 选中
      let b = [...new Set(obj.map(item => item.ccode))]
      // 去重
      let notequal= getArrDifference(selectedRowKeys,tableSelectedRowKeys.value)
      if(notequal.length>0){
        let b=getDataSource().filter(item => item.key==notequal[0])[0].ccode
        let zhuijia= getDataSource().filter(item => item.ccode.startsWith(b))
        zhuijia.forEach(zj=>{
          tableSelectedRowObjs.value.push(zj)
          tableSelectedRowKeys.value.push(zj.key)
        })
      }else{
        tableSelectedRowObjs.value = getDataSource().filter(item => item.ccode.startsWith(b))
        tableSelectedRowKeys.value = tableSelectedRowObjs.value.map(item => item.key)
      }
    }else{  // 取消勾选 先 找到被取消的
      let notequal= getArrDifference(selectedRowKeys,tableSelectedRowKeys.value)
      if(notequal.length>0){
        let temp=getDataSource().filter(item => item.key==notequal[0])[0].ccode
        let zhuijia= getDataSource().filter(item => item.ccode.startsWith(temp)).map(it=>it.ccode)
        tableSelectedRowObjs.value =   tableSelectedRowObjs.value.filter(tt=>zhuijia.indexOf(tt.ccode) == -1)
        tableSelectedRowKeys.value = tableSelectedRowObjs.value.map(item => item.key)
      }
    }
  }else{
    tableSelectedRowKeys.value = selectedRowKeys;
    tableSelectedRowObjs.value=getDataSource().filter(v=>v.key.indexOf(selectedRowKeys))
  }
}
// 两个数组去重
function getArrDifference(arr1, arr2) {
  if(arr2.length==0) return [];
  return arr1.concat(arr2).filter(function(v, i, arr) {
    return arr.indexOf(v) === arr.lastIndexOf(v);
  });
}

function openImportExcelView() {
  let data:any=[]
  getDataSource().forEach(v=>{
    let test:any=[]
    test[0]=v.ccode
    test[1]=v.ccodeName
    test[2]=v.bprogerty==='1'?'借':'贷'
    test[3]=v.cclass
    test[4]=v.fuzhu
    data.push(test)
  })
  aoaToSheetXlsx({
    data: data,
    header: ['科目编码','科目名称','科目方向','科目类型','辅助项'],
    filename: pageParameter.companyName+'-平行记账科目-'+pageParameter.iyear+'.xlsx',
  });
}

function createConfirmPop(text) {
  createConfirm({
    iconType: 'warning',
    title: '警告',
    content: text,
    onOk: async() => {}
  })
}

// 复制上年已选的平行记账科目：编码、名称一致 修改本年科目 平行记账状态
const copyCode = async () => {
  let accperiod=await findYearByAccount(pageParameter.accId)
  let filterperiod =accperiod.filter(v=>parseInt(v.accountYear) < parseInt(pageParameter.iyear) )
  if(filterperiod.length===0){
    createConfirmPop('当前公司没有上年区间,不能复制!')
    return false;
  }else{
  }
}
function addspace(igrade, str) {
  var space = '';
  for (let i = 1; i < igrade; i++) {
    space += '\xa0\xa0\xa0\xa0';
  }
  return space + str;
}

function proItemCheck() {
  if(pageParameter.flag==='1'){
    tabsCheck('1')
  }
}

const openImportExcel = async () => {
  openExcelPage(true, {
    data: {
      database:database.value,
      proItem:proItem.value
    },
  });
}
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
<style src="../../../../assets/styles/global-container-index.less" lang="less" scoped></style>
<style>
/*分页那栏颜色*/
.ant-table-wrapper{
  padding: 0 !important;
  background: #f2f2f2 !important;
}
</style>
<style scoped>
.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
}
/*第二段背景色长度*/
.app-container:nth-of-type(2) {
  background-color: #f2f2f2;
  padding: 0px;
  margin: 5px 10px;
  height: 710px;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}
:deep(.ant-calendar-picker-input.ant-input),
:deep(.ant-select-single:not(.ant-select-customize-input)
      .ant-select-selector
      .ant-select-selection-search-input) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
}
.tableUStyle {
  color: blue;
  cursor: pointer;
  text-decoration:none;
}
.tableUStyle:hover{
  color: red;
}
</style>
