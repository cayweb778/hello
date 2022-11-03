<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title" style="float: left;">
          <b class="noneSpan" style="font-size: 60px;"><OneToOneOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 20px;">
          <b class="noneSpan" style="font-size: 26px;">期初余额试算平衡表</b>
          <p/>
          <span style="font-size: 14px;font-weight: bold;">
              年度：{{pageParameter.iyear}}
            </span>
        </div>
        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="excelThisData"
          ><span>导出</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="closeCurrent()"
          ><span>退出</span></button>
        </div>
      </div>
      <div style="clear: none"/>
      <div style="margin-top: -30px;margin-left: 80px;text-align: left;">
        <div style="display: inline-block;float: left;margin-left: 1%;margin-top: -20px;">
          <AccountPicker theme="three" readonly  @reloadTable="dynamicAdReload"/>
          <p/>
          <span style="font-weight: bold;">
            &nbsp;&nbsp;{{gongshi}}
          </span>
          &emsp;
          <Tag :color="msg=='1'?'green':'volcano'">{{ msg=='1'?'试算平衡':'试算不平衡' }}</Tag>
        </div>

        <div style="float: right; margin-left: 10px">
          <Button class="ant-btn-me">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </Button>
          <Popover class="ant-btn-default" placement="bottom">
            <template #content>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                    :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                    :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MIN'"/></span>
            </template>
            <Button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </Button>
          </Popover>
          <Popover class="ant-btn-default" placement="bottom">
            <template #content>
              <span class="group-btn-span-special2" @click="queryMarkChange('J')" :style="pageParameter.queryMark=='J'?{backgroundColor: '#0096c7',color: 'white'}:''">&nbsp;金额式&emsp;&ensp;<CheckOutlined v-if="pageParameter.queryMark=='J'"/></span><br/>
              <span class="group-btn-span-special2" @click="queryMarkChange('SJ')" :style="pageParameter.queryMark=='SJ'?{backgroundColor: '#0096c7',color: 'white'}:''">&nbsp;数量金额式&emsp;&ensp;<CheckOutlined v-if="pageParameter.queryMark=='SJ'"/></span><br/>
              <span class="group-btn-span-special2" @click="queryMarkChange('WJ')" :style="pageParameter.queryMark=='WJ'?{backgroundColor: '#0096c7',color: 'white'}:''">&nbsp;外币金额式&emsp;&ensp;<CheckOutlined v-if="pageParameter.queryMark=='WJ'"/></span>
            </template>
            <Button>
              <PicLeftOutlined :style="{ fontSize: '14px' }"/>
            </Button>
          </Popover>
        </div>
        <div style="float: right; position: relative">
          <Select v-model:value="pageParameter.searchData.searchType" style="width: 120px;font-size: 12px;" class="special_select">
            <SelectOption style="font-size: 12px;" value="ccode">科目编码</SelectOption>
            <SelectOption style="font-size: 12px;" value="ccodeName">科目名称</SelectOption>
          </Select>
          <!-- 搜索 -->
          <InputSearch
            v-model:value="pageParameter.searchData.searchValue"
            placeholder=""
            style="width: 200px; border-radius: 4px"
            @search="findBySsph"
          />
        </div>
      </div>
      <div style="clear:both"/>
    </div>
    <div class="app-container">
      <BasicTable
        ref="tableRef"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        :row-selection="{ type: 'checkbox' }"
        @register="registerTable"
        :dataSource="tableData"
        :scroll="{ x: totalColumnWidth,y: windowHeight }"
        :loading="loadMark"
      >
        <template #md="{ record }">{{  money(record.md) }}</template>
        <template #mc="{ record }">{{  money(record.mc) }}</template>

        <template #summary>
          <TableSummary fixed>
            <TableSummaryRow style="background-color: #cccccc;" v-if="pageParameter.queryMark=='J'">
              <TableSummaryCell class="nc-summary" :index="0" :colspan="5" :align="'center'" style="border-right: none;">合计</TableSummaryCell>
              <TableSummaryCell class="nc-summary" :index="1" :align="'right'" style="border-right: none;">{{ summaryTotals.md }}</TableSummaryCell>
              <TableSummaryCell class="nc-summary" :index="1" :align="'right'" style="border-right: none;">{{ summaryTotals.mc }}</TableSummaryCell>
            </TableSummaryRow>
            <TableSummaryRow style="background-color: #cccccc;" v-if="pageParameter.queryMark!=='J'">
              <TableSummaryCell class="nc-summary" :index="0" :colspan="6" :align="'center'" style="border-right: none;">合计</TableSummaryCell>
              <TableSummaryCell class="nc-summary" v-if="pageParameter.queryMark=='SJ'" :index="1" :align="'right'" style="border-right: none;">{{ summaryTotals.cnum }}</TableSummaryCell>
              <TableSummaryCell class="nc-summary" v-if="pageParameter.queryMark=='WJ'" :index="1" :align="'right'" style="border-right: none;">{{ summaryTotals.nfrat }}</TableSummaryCell>
              <TableSummaryCell class="nc-summary" :index="1" :align="'right'" style="border-right: none;">{{ summaryTotals.md }}</TableSummaryCell>
              <TableSummaryCell class="nc-summary" :index="1" :align="'right'" style="border-right: none;">{{ summaryTotals.mc }}</TableSummaryCell>
            </TableSummaryRow>
          </TableSummary>
        </template>
      </BasicTable>
    </div>
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import {
  PicLeftOutlined,
  OneToOneOutlined,
  CheckOutlined,
  SettingFilled,
  SortAscendingOutlined,
  SortDescendingOutlined,
  SyncOutlined
} from '@ant-design/icons-vue'
import {BasicTable,useTable} from '/@/components/Table';
import {Button, Popover, Radio as ARadio, Select, Table,Input,Tag,message} from "ant-design-vue"
import {onMounted, reactive, ref} from 'vue';
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {useRoute} from "vue-router";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {exportExcel3} from "/@/api/record/generalLedger/excelExport";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {newSsph, ssph} from "/@/api/subjectInitialBalance/subjectInitialBalance";
import {useMessage} from "/@/hooks/web/useMessage";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";

const InputSearch = Input.Search
const SelectOption = Select.Option
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const TableSummary = Table.Summary
const TableSummaryRow = Table.Summary.Row
const TableSummaryCell = Table.Summary.Cell
const {createConfirm, createWarningModal, createMessage} = useMessage();
const {closeCurrent} = useTabs(router);
const route:any = useRoute();
const tableRef:any = ref(null)
const loadMark:any = ref(false)
const menterage:any = ref(true)
const currencyType:any = ref(true)
const windowWidth = (window.innerWidth - 70)
const windowHeight = (window.innerHeight - 320)
const totalColumnWidth = ref(0)
const gongshi = ref('')
const msg = ref('')
const iyear = ref('')
const standardSelected = ref('')
const databaseTrue = ref(getCurrentAccountName(true));
const pageParameter: any = reactive({
  companyName: 'J',
  queryMark: 'J',
  iyear: '',
  standardSelected: '',
  searchData:{
    searchType:'ccode',
    searchValue:'',
  }
})
const tableData:any = ref([])
const columns:any = ref([
  {title: '科目编码',dataIndex: 'ccode',key: 'ccode',align: 'left',ellipsis: true,defaultHidden:false},
  {title: '科目名称',dataIndex: 'ccodeName',key: 'ccodeName',align: 'left',ellipsis: true,defaultHidden:false},
  {title: '科目分类',dataIndex: 'cclass',key: 'cclass',align: 'center',width:80,ellipsis: true,defaultHidden:false},
  {title: '计量单位',dataIndex: 'menterage',key: 'menterage',align: 'center',width:80,ellipsis: true,defaultHidden:menterage},
  {title: '数量',dataIndex: 'cnum',key: 'cnum',align: 'center',width:80,ellipsis: true,defaultHidden:menterage},
  {title: '外币币种',dataIndex: 'currencyType',key: 'currencyType',align: 'center',width:80,ellipsis: true,defaultHidden:currencyType},
  {title: '外币金额',dataIndex: 'nfrat',key: 'nfrat',align: 'center',width:80,ellipsis: true,defaultHidden:currencyType},
  {title: '期初借方',dataIndex: 'md',key: 'md',align: 'right',ellipsis: true,slots: {customRender: "md"},defaultHidden:false},
  {title: '期初贷方',dataIndex: 'mc',key: 'mc',align: 'right',ellipsis: true,slots: {customRender: "mc"},defaultHidden:false},
])

const [registerTable, {
  reload,
  setColumns,
  getDataSource,
  setTableData,
  getSelectRows,
  getColumns
}] = useTable({
  columns: columns,
  bordered: true,
  showIndexColumn: true,
  indexColumnProps:{ fixed:true },
  pagination: {
    pageSize: 1000,
    simple:true
  },
});
// 金额格式化
function money(val: any) {
  if (val == null || val=='' || parseFloat(val)==0) return;
  val = val.toString().replace(/\$|\,\-/g, '');
  if (isNaN(val)) {
    val = '0';
  }
  let fs = val.indexOf('-') != -1
  const sign = val === (val = Math.abs(val));
  val = Math.floor(val * 100 + 0.50000000001);
  let cents:any = val % 100;
  val = Math.floor(val / 100).toString();
  if (cents < 10) {
    cents = '0' + cents;
  }
  for (let i = 0; i < Math.floor((val.length - (1 + i)) / 3); i++) {
    val =
      val.substring(0, val.length - (4 * i + 3)) + ',' + val.substring(val.length - (4 * i + 3));
  }
  return (sign ? '' : '') + (fs ? '-' : '') + val + '.' + cents;
}
onMounted(async () => {
  if(route.query.iyear==undefined){
  return createConfirm({
      iconType: 'warning',
      title: '警告',
      content: '请在科目期初中操作',
      onOk: async () => {
        closeCurrent()
      },
      onCancel: async () => {
        closeCurrent()
      }
    })
  }
  pageParameter.iyear=route.query.iyear
  pageParameter.standardSelected=route.query.standardSelected
  databaseTrue.value=route.query.databaseTrue

  loadMark.value=true
  setTimeout(()=>{
    findBySsph()
    initTableWidth()
  },500)
})
const dynamicAdReload = async (obj) => {
  pageParameter.companyName=obj.companyName
  // databaseTrue.value=obj.accountMode
  // iyear.value=obj.iyear
  // // 获取对应的账套科目 所属的 会计准则、科目模板
  // let codelistall = await useRouteApi(groupStandardAndTemplate, {schemaName: databaseTrue})({
  //   databaseNum: obj.accountMode,
  //   iyear: obj.iyear
  // });
  // standardSelected.value=codelistall[0]?.uniqueAccStandard
}
function initTableWidth() {
  let total = 60 + 60+1200 // 选择列与序号列
  if (total > windowWidth) {
    let f = 0
    totalColumnWidth.value = Number(windowWidth) - f
    tableRef.value.$el.style.setProperty('width', (windowWidth + 62 - f) + 'px')
  } else {
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 62) + 'px')
  }
}
async function findBySsph() {
  loadMark.value=true
  let data=await useRouteApi(newSsph, {schemaName: databaseTrue.value})(pageParameter)
  tableData.value=data.list
  msg.value=data.msg
  gongshi.value=data.gongshi
  loadMark.value=false
  calculateTotal(data.list)
  if(data.list.length<50){
    for (let i =  data.list.length; i < 50; i++) {
      tableData.value.push({})
    }
  }
}
const summaryTotals = ref({})
const calculateTotal = (datalist) => {
  let list = JsonTool.parseProxy(datalist)
  if (list.length == 0){
    summaryTotals.value = {}
    return false;
  }
  let md = 0
  let mc = 0
  let cnum = 0
  let nfrat = 0
  for (let i = 0; i < list.length; i++) {
    const e = list[i];
    md += parseFloat(e.md || '0')
    mc += parseFloat(e.mc || '0')
    cnum += parseFloat(e.cnum || '0')
    nfrat += parseFloat(e.nfrat || '0')
  }
  summaryTotals.value={
    md: money(md),
    mc: money(mc),
    cnum: money(cnum),
    nfrat: money(nfrat),
  }
}
async function queryMarkChange(val) {
  pageParameter.queryMark=val
  menterage.value=false
  currencyType.value=false
  loadMark.value=true

  if(val=='SJ'){
    menterage.value=true
  }else if(val=='WJ'){
    currencyType.value=true
  }
  let columList=getColumns()
  for (let i = 0; i < columList.length; i++) {
    let tt:any=columList[i]
    if(tt.dataIndex=='cnum' || tt.dataIndex=='menterage'){
      tt.defaultHidden=!menterage.value
      tt.ifShow=menterage.value
    }
    if(tt.dataIndex=='nfrat' || tt.dataIndex=='currencyType'){
      tt.defaultHidden=!currencyType.value
      tt.ifShow=currencyType.value
    }
  }
  setTimeout(()=>{
    setColumns(columList)
    loadMark.value=false
  },800)
}
function excelThisData() {
  const arrData:any =tableData.value
  const columns:any = getColumns().filter(a=>a.defaultHidden==false)
  if(arrData.length==0)return message.error('没有数据,无法导出!!')
  const multiHeader:any = [[]]
  const keys:any = []
  //样式靠右列
  let rightrow:any=[]
  //样式靠左列
  let leftrow:any=[]

  const cell = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  columns.forEach((v,index)=>{
    if(v.title.indexOf('数量')!=-1||v.title.indexOf('期初')!=-1||v.title.indexOf('金额')!=-1){
      rightrow.push(cell[index])
    }else{
      leftrow.push(cell[index])
    }
    multiHeader[0].push(v.title)
    keys.push(v.key)
  })
  let list:any=[]
  arrData.filter(a=>a.bcheck!=='11').forEach((a,index)=>{
    a.md=parseFloat(a.md)==0?'':money(a.md)
    a.mc=parseFloat(a.mc)==0?'':money(a.mc)
    a.cnum=parseFloat(a.cnum)==0?'':money(a.cnum)
    a.nfrat=parseFloat(a.nfrat)==0?'':money(a.nfrat)
    list.push(a)
  })
  const sheet =[
    {
      multiHeader: multiHeader,
      table: list,
      keys: keys,
      sheetName: 'sheet1',
      cellStyle: [],
      rightrow:rightrow,
      leftrow:leftrow,
    },
  ]
  exportExcel3(sheet, 'xlsx','期初余额试算平衡表_'+pageParameter.companyName)
}
</script>
<style src="./global-menu-index.less" lang="less" scoped="scoped"></style>
<style scoped lang="less">
@import "./global-menu-index.less";
:deep(.nc-summary){
  font-weight: bold;
  background-color: #cccccc!important;;
  border-right-color: #cccccc!important;
}
:deep(.ant-card-body) {
  padding: 16px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
}

.ant-btn-me {
  color: #0096c7;
}

.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
  border-color: #aaaaaa !important;
  height: 25px;
  color: black;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 13px !important;
  padding: 2px 8px !important;
  border-color: #aaaaaa !important;
  height: 25px;
  color: black;
}

.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
}

.app-container:nth-of-type(2) {
  padding: 0px;
  margin: 5px 10px;
  background: #b4c8e3 !important;
  margin-top: -6px;
  position: relative;
  .pagination-text{
    position: absolute;
    bottom: 6px;
    right: 10%;
    font-size: 13px;
    color: black;
    z-index: 99999999;
  }
}

:deep(.ant-table-thead) th {
  text-align: center !important;
  font-weight: bold;
  //background-color: #f2f2f2 !important;
  background-color: #cccccc !important;
  border-color: #aaaaaa !important;
}

:deep(.vben-basic-table) .ant-table-wrapper {
  padding: 0px;
}

:deep(.vben-basic-table) .ant-table {
  width: 100%;
  margin: 0;
  overflow-x: hidden;
  height: calc(100% - 160px);
  min-height: 500px;
}

:deep(.vben-basic-table) .ant-pagination {
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
  margin: 0 !important;
}

:deep(.vben-basic-table) {
  min-height: 500px;
  height: calc(100% - 160px);
  margin-bottom: 20px;
}

:deep(.ant-input), :deep(.ant-select), :deep(.ant-btn) {
  border: 1px solid #c9c9c9;
}

.lcr-theme-div {
  display: inline-flex;
  justify-content: space-between;
  width: 99%;
  height: 100px;

  > div:nth-of-type(1) {
    width: 40%;

    > div:nth-of-type(1) {
      width: 64px;
      display: inline-block;
      text-align: center;
      vertical-align: super;
    }

    > div:nth-of-type(2) {
      width: calc(100% - 64px);
      display: inline-block;

      > div:nth-of-type(2) {
        margin-top: 14px;
      }
    }
  }

  > div:nth-of-type(2) {
    width: 20%;
    text-align: center;

    > div:nth-of-type(2) {
      margin-top: 14px;
    }
  }

  > div:nth-of-type(3) {
    width: 40%;
    text-align: right;

    > div:nth-of-type(2) {
      display: inline-flex;
      justify-content: space-between;
      margin-top: 14px;
    }
  }
}

.tableUStyle {
  color: #0798c8;
  cursor: pointer;
  text-decoration: none;
}

.tableUStyle:hover {
  color: #b4c8e3;
}

</style>
