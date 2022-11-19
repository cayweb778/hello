<template>
  <div>
    <div class="app-container">
      <ProfileOutlined style="color: #0096c7;font-size: 60px;margin-top: 10px;"/>&emsp;
      <div style="width: 33%;margin-top: 9px;">
        <AccountPicker theme="one" readonly="" @reloadTable="dynamicAdReload" style="margin-top: 20px;"/>
      </div>
      <div style="width: 29.5%;text-align: center;margin-top: 9px;">
        <span style="font-size: 24px;font-weight: bold;color:rgb(0 150 199)">现金流量统计表</span><br>
        <span style="color:#666666;font-weight: bold;">期间：</span> <span style="color: #0f0f0f;font-weight: bold;">{{ pageParameter.thisInterval }}</span>

      </div>

      <div style="width: 33%;text-align: right;margin-left: 8px;" >
        <a-button
          class="actod-btne"
          ant-click-animating-without-extra-node="false"
          @click="openQueryPage()"
        ><span>查询</span></a-button>
        <a-popover placement="bottom">
          <template #content>
            <span class="group-btn-span-special" @click="exportExcelNow()">导出当前</span><br/>
            <span class="group-btn-span-special" >条件导出</span>
          </template>
          <a-button
            class="actod-btne"
            ant-click-animating-without-extra-node="false"
          ><span>导出</span></a-button></a-popover>
        <a-popover placement="bottom">
          <template #content>
            <span class="group-btn-span-special" @click="exportPdfNow()">打印当前</span><br/>
            <span class="group-btn-span-special">条件打印</span>
          </template>
          <a-button
            class="actod-btne"
            ant-click-animating-without-extra-node="false"
          ><span>打印</span></a-button>
        </a-popover>
        <a-button class="actod-btn" style="border-right: 1px solid #cccccc;" @click="closeCurrent(),router.push('/zhongZhang/home/welcome')"><span>退出</span></a-button>

        <p/>

        <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 150px;text-align:left;font-size: 12px;" class="special_select">
          <template v-for="item in searchConditonList.slice(1)">
            <a-select-option v-if="item.ifShow == true" :value="item.dataIndex">
              {{item.title}}
            </a-select-option>
          </template>
        </a-select>
        <a-input-search
          placeholder=""
          v-model:value="pageParameter.searchConditon.value"
          @search="pageSearch"
          style="width: 150px;border-radius: 4px;border: 1px #cccccc solid;"
        />

        <a-button class="ant-btn-default" @click="closeFilterV(),pageReload()">
          <SyncOutlined :style="{ fontSize: '14px' }" />
        </a-button>
        <a-popover class="ant-btn-default" placement="bottom">
          <template #content>
            <a-popconfirm
              ok-text="保存"
              cancel-text="关闭"
              @confirm="confirm"
              @cancel="cancel"
            >
              <template #icon><b>栏目设置</b><br></template>
              <template #title>
                <a-table bordered :data-source="dynamicColumnData" :columns="dynamicColumns"
                         childrenColumnName="children" :pagination="false" style="max-height: 500px;overflow-y: auto" class="lanmu-table">
                  <template #checkBox="{ text, record }">
                    <a-checkbox v-model:checked="record.check" :disabled="record.isFixed"/>
                  </template>
                  <template #widthInput="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]" class="editable-cell-input-wrapper">
                        <a-input type="number" v-model:value="editableData[record.key].width"
                                 @pressEnter="save(record.key,record.min,record.max)" style="width: 80px"/>
                        <check-outlined class="editable-cell-icon-check"
                                        @click="save(record.key,record.min,record.max)"/>
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined class="editable-cell-icon" @click="edit(record.key)"/>
                        <span style="float: right;">{{ record.min + '~' + record.max }}</span>
                      </div>
                    </div>
                  </template>
                  <template #nameInput="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]" class="editable-cell-input-wrapper">
                        <a-input type="text" v-model:value="editableData[record.key].nameNew"
                                 @pressEnter="saveName(record.key)" style="width: 100px"/>
                        <check-outlined class="editable-cell-icon-check"
                                        @click="saveName(record.key)"/>
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <edit-outlined class="editable-cell-icon" @click="edit(record.key)"/>
                      </div>
                    </div>
                  </template>
                  <template #alignRadio="{ text, record }">
                    <a-radio-group default-value="a" size="small" v-model:value="record.align"
                                   :disabled="record.align=='' || record.name=='本币金额' || record.name=='原币金额'
                                        || record.name=='借方' || record.name=='贷方' || record.name=='余额'">
                      <a-radio-button value="left">
                        左
                      </a-radio-button>
                      <a-radio-button value="center">
                        中
                      </a-radio-button>
                      <a-radio-button value="right">
                        右
                      </a-radio-button>
                    </a-radio-group>
                  </template>
                </a-table>
              </template>
              <a-button style="width: 120px;margin-bottom: 2px">栏目设置</a-button>
            </a-popconfirm>
            <br/>
            <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                  :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
              v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
            <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                  :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
              v-if="pageParameter.showRulesSize==='MIN'"/></span>
          </template>
          <a-button>
            <SettingFilled :style="{ fontSize: '14px' }"/>
          </a-button>
        </a-popover>


        <a-popover class="ant-btn-default" placement="bottom">
          <template #content>
            <span class="group-btn-span-special">导出当前</span><br/>
            <span class="group-btn-span-special">条件导出</span>
          </template>
          <button
            type="button"
            class="ant-btn"
            ant-click-animating-without-extra-node="false"
          >
            <UsbOutlined/>
          </button>
        </a-popover>
        <a-popover class="ant-btn-default" placement="bottom">
          <template #content>
            <span class="group-btn-span-special">打印当前</span><br/>
            <span class="group-btn-span-special">条件打印</span>
          </template>
          <button
            type="button"
            class="ant-btn"
            ant-click-animating-without-extra-node="false"
          >
            <PrinterOutlined/>
          </button>

        </a-popover>
      </div>

    </div>

    <div class="app-container">
      <div class="app-container-bottom" >
        <BasicTable
          ref="tableRef"
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
          :scroll="{ x: totalColumnWidth,y: windowHeight }"
          size="small"
          @register="registerTable"
          @fetch-success="fetchSuccess"
        >

          <template #number="{record,index }">
              <span slot="number" slot-scope="text,record,index">
               {{index+1}}
              </span>
          </template>

          <template #fx="{record,index }">
              <span slot="fx" slot-scope="text,record,index">
               {{fxFormat(record.fx)}}
              </span>
          </template>

          <template #money="{ record }">
            <span style="float: right" @click="goto()">{{ formatMoney(record) }}</span>
          </template>

          <template #summary>
            <TableSummary fixed>
              <TableSummaryRow style="background-color: #cccccc;" v-if="pageParameter.queryMark=='J'">
                <TableSummaryCell class="nc-summary" :index="0" :align="'left'" :colSpan="3" style="border-right: none;">合计</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcMd}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcMc}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzMd}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzMc}}</TableSummaryCell>
                <TableSummaryCell v-show="ljchecked" class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.ljMd}}</TableSummaryCell>
                <TableSummaryCell v-show="ljchecked" class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.ljMc}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmMd}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmMc}}</TableSummaryCell>
              </TableSummaryRow>
            </TableSummary>
          </template>
        </BasicTable>
        <div class="pagination-text" :style="{top: windowHeight+85+'px'}" v-show="showPageNumber">
          {{`共 ${pageNumber} 条记录&emsp;每页 1000 条`}}
        </div>
        <a-drawer
          title="过滤漏斗"
          placement="right"
          :closable="true"
          v-if="visible"
          :mask="false"
          :visible="visible"
          :get-container="false"
          :wrap-style="{ position: 'absolute' }"
          @close="visible=false,reloadColumns()"
        >
          <ul>
            <li>
                <span style="color: black;font-weight: bold">
                  借方本币：
                </span>
              <div>
                <a-input v-model:value="pageParameter.filterConditon.amountMinJf"  placeholder="" style="width: 95px;font-size: 10px"/>
                ~
                <a-input v-model:value="pageParameter.filterConditon.amountMaxJf"  placeholder="" style="width: 95px;font-size: 10px;float: right"/>
              </div>
            </li>
            <li style="margin-top: 5%">
                <span style="color: black;font-weight: bold">
                  贷方本币：
                </span>
              <div>
                <a-input v-model:value="pageParameter.filterConditon.amountMinDf"  placeholder="" style="width: 95px;font-size: 10px"/>
                ~
                <a-input v-model:value="pageParameter.filterConditon.amountMaxDf"  placeholder="" style="width: 95px;font-size: 10px;float: right"/>
              </div>
            </li>
            <li style="margin-top: 5%">
                <span style="color: black;font-weight: bold">
                  余额本币：
                </span>
              <div>
                <a-input v-model:value="pageParameter.filterConditon.amountMinYe"  placeholder="" style="width: 95px;font-size: 10px"/>
                ~
                <a-input v-model:value="pageParameter.filterConditon.amountMaxYe"  placeholder="" style="width: 95px;font-size: 10px;float: right"/>
              </div>
            </li>
          </ul>
          <br/>
          <a-button type="primary"  style="float: right;" @click="filterSearch">
            <span style="font-size: 14px">开始</span>
          </a-button>
        </a-drawer>
        <Query
          @save="loadPage"
          @register="registerQueryPage"
        />
      </div>
    </div>

  </div>
</template>
<script setup lang="ts">
import { BasicTable, useTable } from '/@/components/Table'
import { useModal } from '/@/components/Modal'
import { PageWrapper } from '/@/components/Page'

import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Upload as AUpload,
  Pagination as APagination,
  Popconfirm as APopconfirm,
  Table as ATable,
  Checkbox as ACheckbox,
  message,
  Drawer as ADrawer,
  Table
} from "ant-design-vue"
const ARangePicker=ADatePicker.RangePicker
const ASelectOption=ASelect.Option
const AInputSearch=AInput.Search
const ARadioGroup=ARadio.Group
const ARadioButton=ARadio.Button
import {
  CaretDownFilled,
  FormOutlined,
  DeleteOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled,
  CheckOutlined,
  EditOutlined,
  SearchOutlined,
  SortDescendingOutlined,
  SortAscendingOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  PrinterOutlined,
  UsbOutlined,ProfileOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons-vue';
import { breakNumTidy,findDbLanMuList,saveLanMuList } from '/@/api/record/system/accvoucher'
import { useXJTjStore } from '/@/api/record/xian_jin_liu_liang_tj/data'
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {onMounted, reactive, ref} from "vue";
import moment from "moment";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import { initDynamics,assemblyDynamicColumn} from "./data";
import Query from "./popup/query.vue";
import {cloneDeep} from "lodash-es";
import {saveVoucherData} from "/@/api/record/system/financial-settings";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  askTask,
  compareTime,
  findByFunctionModule,
  getCurrentAccountName,
  markAnomaly,
  offsetToStr,
  getThisIndexImg
} from "/@/api/task-api/tast-bus-api";
import {findXjllTj} from "/@/api/record/generalLedger/data";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {finByParameterAccuracy} from "/@/api/record/km_mingxi/data";
import router from "/@/router";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import AccountInfo from "/@/views/boozsoft/system/accvoucher/popup/AccountInfo.vue";
import {useTabs} from "/@/hooks/web/useTabs";
import {exportExcel} from "/@/api/record/generalLedger/excelExport";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
const {closeCurrent} = useTabs(router);

const { createConfirm,createWarningModal } = useMessage();
// 全局常量
const glStore = useXJTjStore()
const TableSummary = Table.Summary
const TableSummaryRow = Table.Summary.Row
const TableSummaryCell = Table.Summary.Cell
// 页面变量
const pageParameter = reactive({
  thisInterval:'',
  companyCode: '100',
  companyName: '湖北万亚软件技术有限公司',
  queryMark: 'J',
  showRulesSize: 'MIN',
  condition: {},
  searchConditon: {
    requirement: 'fx',
    value: '',
  },
  filterConditon: {
    amountMinDf: '',
    amountMaxDf: '',
    amountMinJf: '',
    amountMaxJf: '',
    amountMinYe: '',
    amountMaxYe: '',
  },
  reloadMark: false,
  km: "",
  minKm:"" ,
  maxKm: "",
  // 显示期间
  endDate: "",
  strDate: "",
  //显示未记账
  ibook: true,
  // 级次
  minJc: "1",
  maxJc: "1",
  moji: 1,
  jd: 2,
  dw: '',
  wb: '',

  //科目类别权限
  userId: '',
  year: '',
  accId: '',
  isType: '',
  isCode: '',
})

const styleName = ref<String>("金额式")
// 科目名称标题
const titleName = ref<String>("")
// 会计科目
const kmList: any = ref([])
//币种名称
const bzName = ref<String>("")
//显示未记账
const ibook = ref<boolean>(true)
const showStyle = ref([
  {
    'name': '金额式',
    'value': 'J'
  },{
    'name': '数量金额式',
    'value': 'SJ'
  },{
    'name': '外币金额式',
    'value': 'WJ'
  },{
    'name': '数量外币式',
    'value': 'SWJ'
  }
])

const val = {
  openOne: 0
}
const visible = ref(false);
const searchConditonList = ref([])
const lanMuData = {'accId':'','menuName':'现金流量统计','type':'',objects: '',username:useUserStoreWidthOut().getUserInfo.username}
// 表格参数
const loadMark = ref(false)
const tableSelectedRowKeys = ref([])
const tableSelectedRowObjs = ref([])
const windowWidth  = (document.documentElement.clientWidth -70)
const windowHeight  = (document.documentElement.clientHeight -350)
const totalColumnWidth = ref(0)
// 数据库模式名称
const databases = ref(getCurrentAccountName(false))
let databasesName = ref(getCurrentAccountName(true))
const CrudApi = {
  list: useRouteApi(findXjllTj,{schemaName:databasesName.value}),
  columns: JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark)))
}
const tableRef = ref(null)

// 组件实例区
const [registerTable, { reload,setTableData,setColumns,getColumns,getPaginationRef,setPagination,getDataSource }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  loading: loadMark.value,
  immediate: false,
  canResize: true,
  /*  showSummary: true,
    summaryData: [],*/
  showIndexColumn: false , //显示序号列
  pagination: {
    pageSize: 1000,
    simple:true
  },
  searchInfo: pageParameter
})
const [registerQueryPage, { openModal: openQueryPageM }] = useModal()

onMounted( ()=>{
  /*useCompanyOperateStoreWidthOut().commitSchemaName(
    JSON.parse(window.localStorage.getItem('datasource')).schemaName + '-' + '2021'
  );*/
  val.openOne = 1
  openQueryPageM(true, {
    data: val
  })
  loadMark.value = true
  // setTimeout(async ()=>{ pageReload()},1000)
})
// 实例函数区
function money(val:any) { // 金额格式化
  if(val==null)val=''
  val = val.toString().replace(/\$|\,/g, '')
  if (isNaN(val)) {
    val = '0'
  }
  const sign = (val === (val = Math.abs(val)))
  val = Math.floor(val * 100 + 0.50000000001)
  let cents: string = (val % 100) +''
  val = Math.floor(val / 100).toString()
  if (cents < 10) {
    cents = '0' + cents
  }
  for (let i = 0; i < Math.floor((val.length - (1 + i)) / 3); i++) {
    val = val.substring(0, val.length - (4 * i + 3)) + ',' + val.substring(val.length - (4 * i + 3))
  }
  let re = (((sign) ? '' : '') + val + '.' + cents)
  return re == ('0.00' || '0')?'': re
}
function formatData(data:any){
  let str = ""
  if(data){
    // 千分位保留2位小数
    var source = String(data.toFixed(2)).split("."); //按小数点分成2部分
    source[0] = source[0].replace( new RegExp('(\\d)(?=(\\d{3})+$)' , 'ig'), "$1,");//只将整数部分进行都好分割
    str = source.join("." );//再将小数部分合并进来
  }
  return str;
}
function fxFormat(data:any){
  let str = "流出"
  if(data === '1'){
    str = "流入"
  }
  return str;
}
function formatMoney(data:any){
  let str = ""
  if(data.fx === '1'){
    str = money(data.md)
  }else if(data.fx === '0'){
    str = money(data.mc)
  }else{
    str = money(data.money)
  }
  return str;
}

const modify = (code) => {
  if (pageParameter.companyCode != code) {
    pageParameter.companyCode = code
    pageSearch()
  }
}
function formatNum(data:any){
  let str = ""
  if(data){
    if(0 === data){
      str = ""
    }else {
      var source = String(data.toFixed(pageParameter.jd)).split("."); //按小数点分成2部分
      source[0] = source[0].replace( new RegExp('(\\d)(?=(\\d{3})+$)' , 'ig'), "$1,");//只将整数部分进行都好分割
      str = source.join("." );
    }
  }
  return str;
}
const openQueryPage = () => {
  val.openOne = 0
  openQueryPageM(true, {
    data: val
  })
}
// 页面函数区
const onSelectChange = (selectedRowKeys,obj) => {
  tableSelectedRowKeys.value = selectedRowKeys;
  tableSelectedRowObjs.value = obj;
};
const pageReload =  () =>{
  reload({
    searchInfo: pageParameter
  })
}
const breakNumTidyBtn = async() => {
  breakNumTidy()
  pageReload()
}
const [registerInfo, {openModal: openInfoPageM}] = useModal()

const onChangeSwitch = async(v)=>{ // 动态列
  styleName.value = showStyle.value.filter(o=> o.value === v)[0].name
  pageParameter.queryMark = v
  resetDynamicColumnData()
  pageParameter.searchConditon.requirement = 'fx'
  pageParameter.searchConditon.value = ''
}
const  pageSearch = async ()=>{
  /* if (''==pageParameter.searchConditon.value.trim())return false*/
  // 搜索前校验格式
  if (''==pageParameter.searchConditon.requirement.trim()){
    message.warn('请选择检索条件')
    return false
  }
  // 校验完成后搜索
  closeFilterV()
  pageReload()
}

const  filterSearch = async ()=>{
  /* if (''==pageParameter.searchConditon.value.trim())return false*/
  // 搜索前校验格式
  let min  = pageParameter.filterConditon.amountMinJf.trim()
  let max  = pageParameter.filterConditon.amountMaxJf.trim()
  if (max != '' ||  min != ''){
    if (min != '' && max != '' && (!isRealNum(min) || !isRealNum(max) || parseFloat(min) == 0 || parseFloat(max) == 0)){
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    }else if (parseFloat(min) > parseFloat(max)){
      message.warn('金额区间最小值部门大于最大值！')
      closeFilterV()
      return false
    }
  }

  let min2  = pageParameter.filterConditon.amountMinDf.trim()
  let max2  = pageParameter.filterConditon.amountMaxDf.trim()
  if (max2 != '' ||  min2 != ''){
    if (min2 != '' && max2 != '' && (!isRealNum(min2) || !isRealNum(max2) || parseFloat(min2) == 0 || parseFloat(max2) == 0)){
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    }else if (parseFloat(min2) > parseFloat(max2)){
      message.warn('金额区间最小值部门大于最大值！')
      closeFilterV()
      return false
    }
  }

  let min3  = pageParameter.filterConditon.amountMinYe.trim()
  let max3  = pageParameter.filterConditon.amountMaxYe.trim()
  if (max != '' ||  min3 != ''){
    if (min3 != '' && max3 != '' && (!isRealNum(min3) || !isRealNum(max3) || parseFloat(min3) == 0 || parseFloat(max3) == 0)){
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    }else if (parseFloat(min3) > parseFloat(max3)){
      message.warn('金额区间最小值部门大于最大值！')
      closeFilterV()
      return false
    }
  }

  // 校验完成后搜索
  pageReload()
}

const closeFilterV = ()=>{
  pageParameter.filterConditon.amountMinDf = ''
  pageParameter.filterConditon.amountMaxDf = ''
  pageParameter.filterConditon.amountMinJf = ''
  pageParameter.filterConditon.amountMaxJf = ''
  pageParameter.filterConditon.amountMinYe = ''
  pageParameter.filterConditon.amountMaxYe = ''
  visible.value = false
  reloadColumns()
}

function isRealNum(val){
  // isNaN()函数 把空串 空格 以及NUll 按照0来处理 所以先去除，
  if(val === "" || val ==null){
    return false;
  }
  if(!isNaN(val)){
    //对于空数组和只有一个数值成员的数组或全是数字组成的字符串，isNaN返回false，例如：'123'、[]、[2]、['123'],isNaN返回false,   //所以如果不需要val包含这些特殊情况，则这个判断改写为if(!isNaN(val) && typeof val === 'number' )
    return true; 　　}　else{ 　　　　return false; 　　}
}

/*start栏目设置*/
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});

const confirm = (e: MouseEvent) => {
  // 询问
  createConfirm({
    iconType: 'warning',
    title: '栏目同步',
    content: '是否将刚才设置同步数据库!',
    onOk: async() => {
      // 调整数据库 列参数
      lanMuData.accId = getCurrentAccountName(false)
      lanMuData.objects = JSON.stringify(filterModifyData(dynamicColumnData.value, dynamicColumnDataCopy))
      if (lanMuData.objects == '[]'){
        createWarningModal({content: '请先做修改后再进行确认同步数据库！'})
      }else {
        saveLanMuList(lanMuData).then(res=>{
          message.success("数据库同步成功！")
        })
        // 重新赋值
        dynamicColumnDataCopy = JSON.parse(JSON.stringify(dynamicColumnData.value))
      }
    }
  });
  // 重新获取数据
  reloadColumns()
}

function filterModifyData(lanMuList:any,copyList) {
  let a =  lanMuList.filter(item=> {
    try {
      copyList.forEach(item2=>{
        if (item.key === item2.key && item.name == item2.name){
          if (item.nameNew != item2.nameNew || item.width != item2.width || item.check != item2.check || item.align != item2.align)
            throw new Error('ok')
        }
      })
      return false
    }catch (e) {
      if (e.message == 'ok'){
        return  true
      }else {
        return  false
      }
    }
  })

  //对子节点处理  过滤有子节点并且变动 添加到a
  lanMuList.forEach((item, index)=>{
    if(item.children){
      let b = item.children.filter(item2=>{
        try {
          copyList[index].children.forEach(item3=>{
            if (item2.key === item3.key && item2.name == item3.name){
              if (item2.nameNew != item3.nameNew || item2.width != item3.width || item2.check != item3.check || item2.align != item3.align)
                throw new Error('ok')
            }
          })
          return false
        }catch (e) {
          if (e.message == 'ok'){
            return  true
          }else {
            return  false
          }
        }
      })
      b.forEach(item=>{
        a.push(item);
      })
    }
  })
  return a;
}

const cancel = (e: MouseEvent) => {
  // 恢复默认
  dynamicColumnData.value = []
  dynamicColumnData.value = dynamicColumnDataCopy
}

function resetDynamicColumnData() {
  // 先从数据查询是否已经设置
  lanMuData.accId = getCurrentAccountName(false)
  lanMuData.type = pageParameter.queryMark
  findDbLanMuList(lanMuData).then(res=>{
    // 栏目列
    let dbList = res.items
    if (dbList.length > 0){
      let statiList = initDynamics()[pageParameter.queryMark]
      dbList = combineParameters(statiList,dbList)
      dynamicColumnData.value = dbList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(dbList))
    }else {
      let statiList = initDynamics()[pageParameter.queryMark]
      dynamicColumnData.value = statiList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(statiList))
    }
    // 表格列
    reloadColumns()
    pageReload()
  })
}

function combineParameters(staticList:any,dbList:any) {
  staticList.forEach(item=>{
    dbList.forEach(item2=>{
      if (item.key === item2.key && item.name === item2.name){
        item.nameNew = item2.nameNew
        item.width = parseInt(item2.width)
        item.check = item2.check == 'true'
        item.align = item2.align
      }else{
        //对子节点处理
        if(item.children){
          item.children.forEach(item3=>{
            if (item3.key === item2.key && item3.name === item2.name){
              item3.nameNew = item2.nameNew
              item3.width = parseInt(item2.width)
              item3.check = item2.check == 'true'
              item3.align = item2.align
            }
          })
        }
      }
    })
  })
  return staticList
}

const edit = (key: string) => {
  if (key.toString().indexOf('-') != -1) {
    let arr = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      editableData[key] = cloneDeep(dynamicColumnData.value[one].children.filter(item => key === item.key)[0]);
    } else {
      let two = parseInt(arr[1] - 1)
      editableData[key] = cloneDeep(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0]);
    }
  } else {
    editableData[key] = cloneDeep(dynamicColumnData.value.filter(item => key === item.key)[0]);
  }
}

const save = (key: string, min: number, max: number) => {
  editableData[key].width = editableData[key].width > max ? max : editableData[key].width < min ? min : editableData[key].width
  if (key.toString().indexOf('-') != -1) {
    let arr = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
    } else {
      let two = parseInt(arr[1] - 1)
      Object.assign(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0], editableData[key]);
    }
  } else {
    Object.assign(dynamicColumnData.value.filter(item => key === item.key)[0], editableData[key]);
  }
  delete editableData[key];
}

const saveName = (key: string) => {
  if (key.toString().indexOf('-') != -1) {
    let arr = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
    } else {
      let two = parseInt(arr[1] - 1)
      Object.assign(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0], editableData[key]);
    }
  } else {
    Object.assign(dynamicColumnData.value.filter(item => key === item.key)[0], editableData[key]);
  }
  delete editableData[key];
}

const reloadColumns = ()=>{
  let a = []
  a = getColumns()
  let newA = JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark)))
  newA= assemblyDynamicColumn(dynamicColumnData.value,newA)
  setColumns(newA)
  initTableWidth(newA)
  //多表头 添加子节点筛选
  let seachList = []
  newA.forEach(item=>{
    if(item.children){
      item.children.forEach(item2=>{
        item2.title = item.title + item2.title
        seachList.push(item2)
      })
    }else{
      seachList.push(item)
    }
  })
  searchConditonList.value = seachList
}

function initTableWidth(thisCs){
  let total = 60
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })
  if (total > windowWidth) {
    let f = 0
    if (visible.value) f = 260
    totalColumnWidth.value = Number(windowWidth) - f
    // tableRef.value.$el.style.setProperty('width', (windowWidth + 20 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    // tableRef.value.$el.style.setProperty('width', (total + 20) + 'px')
  }
}

/*栏目设置end*/
const loadPage = (data)=>{
  // 回显筛选条件
  loadMark.value = false
  pageParameter.strDate = data.strDate.replaceAll("-",".")
  pageParameter.endDate = data.endDate.replaceAll("-",".")
  pageParameter.showRulesSize  = data.fontSize
  bzName.value = data.bzName
  pageParameter.ibook = data.ishaveRjz
  databasesName.value = data.accountId
  if (data.strDate != '' && data.endDate != '') {
    pageParameter.thisInterval = data.strDate.replace('-','.') + ' ~ ' + data.endDate.replace('-','.')
  } else {
    pageParameter.thisInterval = data.dateStart.replace('-','.') + ' ~ ' + data.dateEnd.replace('-','.')
  }

  if (data.openOne == 1){ // 第一次初始化 + 条件查询
    resetDynamicColumnData()
  }else { // 查询条件查询
    closeFilterV()
    pageParameter.searchConditon.value = ''
    pageReload()
  }
}

const userName = useUserStoreWidthOut().getUserInfo.username
// 数据库模式名称
const database = ref(useCompanyOperateStoreWidthOut().getSchemaName);
const goto = async () => {
  const kmStyle =  kmList.value.filter(o=> o.ccode === pageParameter.km)[0].cbookType //科目样式
  if(kmStyle){
    pageParameter.queryMark  = showStyle.value.filter(o=> o.name === kmStyle)[0].value //显示样式
  }else {
    pageParameter.queryMark  = 'J';
  }
  const json = {
    ishaveRjz: ibook.value,
    strDate: pageParameter.strDate.replaceAll(".","-"),
    endDate: pageParameter.endDate.replaceAll(".","-"),
    minJc: pageParameter.minJc,
    maxJc: pageParameter.maxJc,
    km: kmList.value[0].ccode,
    maxKm:  kmList.value[0].ccode,
    timflg: 'qj',
    showStyle: pageParameter.queryMark,
    fontSize: pageParameter.showRulesSize,
    bz: bzName.value,
    bzName:'全部',
    jc: pageParameter.moji,
    riqi: '',
    parameteraccuracyJSON: JSON.stringify(await useRouteApi(finByParameterAccuracy,{schemaName:getCurrentAccountName(true)})(''))
  }
  router.push({
    path: '/account-book/ab-kemuzhang/abk-mxtable',
    query: json,
  });
}


const exportExcelNow = async () => {
  const data = JSON.parse(JSON.stringify(getDataSource()))
  const columns = getColumns()
  //筛选条件
  const title = [
    "期间："+ pageParameter.thisInterval,
  ]
  //根据columns 格式化导出excel数据
  const multiHeader = [[],[]]
  const keys = []
  const merges = []
  const cellStyle = [
    {
      cell: 'A1',
      font: {
        sz: 16,
        color: { rgb: "000000" },
        bold: true,
      },
      fill: {
        fgColor: { rgb: "ffffff" },
      }
    },
  ]
  const cell = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  let flg = 0;
  merges.push('`A2:' + cell[keys.length-1] +'2`');
  columns.forEach((v,index)=>{
    multiHeader[0].push(v.title)
    multiHeader[1].push('')
    keys.push(v.key)
    //合并上下单元格
    merges.push('`' + cell[flg] + '3:' + cell[flg] +'4`');
    flg = flg + 1
  })
  //数量金额格式化调整
  let number = 0;
  data.forEach(v=>{
    v.number = number+1
    number = v.number
    v.money = formatMoney(v)
    v.xjcode = v.xjcode ? v.xjcode : ""
    v.xjname = v.xjname ? v.xjname : ""
    v.dbillDate = v.dbillDate ? v.dbillDate : ""
    v.inoId = v.inoId ? v.inoId : ""
    v.cdigest = v.cdigest ? v.cdigest : ""
    v.fx = fxFormat(v.fx)
  })

  //样式靠右列
  let rightrow=[]
  //样式靠左列
  let leftrow=[]
  if(pageParameter.queryMark === 'J'){
    rightrow=['F']
  }else if(pageParameter.queryMark === 'SJ'){
    rightrow=['F','H','K']
  }else if(pageParameter.queryMark === 'WJ'){
    rightrow=['F','H','K','E','J']
  }else{
    rightrow=['G','F','H','K','O','J','N']
  }

  const sheet =[
    {
      title: '现金流量统计表',
      tHeader: ["期间："+ pageParameter.thisInterval],
      multiHeader: multiHeader,
      table: data,
      keys: keys,
      merges: merges,
      sheetName:  '现金流量统计表',
      cellStyle: cellStyle,
      rightrow:rightrow,
      leftrow:leftrow,
    },
  ]
  let name = pageParameter.companyName
  exportExcel(sheet, 'xlsx',name+'-现金流量统计表-'+pageParameter.strDate+'-'+pageParameter.endDate)
}

const showPageNumber=ref(false)
const pageNumber=ref(0)
function fetchSuccess(data) {
  console.log(data)
  let list=data.items.filter(a=>a.ccode.indexOf('小计')!=-1)
  pageNumber.value=data.items.length
  showPageNumber.value=true
  //calculateTotal(list)
  if(data.items.length<50){
    for (let i =  data.items.length; i < 50; i++) {
      data.items.push({ccode:'temp',})
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
  let qcMd = 0
  let qcMc = 0
  let qcNum = 0
  let qcNfrat = 0
  let pzMd = 0

  for (let i = 0; i < list.length; i++) {
    const e = list[i];

  }
  summaryTotals.value={

  }
}
</script>
<style scoped lang="less">
@import '/@/assets/styles/part-open.less';
@import '/@/assets/styles/global-menu-index1.less';
:deep(.ant-select){
  border: none;
  background-color: #f1f1f1;
  color: black;
}

:deep(.ant-select:not(.ant-select-customize-input) .ant-select-selector){
  border: 1px #cccccc solid;
  height: 33px;
}

// ***************  button样式  ***************
.actod-btn {
  color: @Global-Comm-BcOrText-Color;
  font-size: 14px;
  border-color: @Global-Border-Color;
  border-right: none;
}

.actod-btn-last {
  border-right: 1px solid @Global-Border-Color;
}

.actod-btn:hover {
  background-color: @Global-Comm-BcOrText-Color;
  color: white;
}
// ***************  button样式  ***************
:deep(.vben-basic-table .ant-pagination) {
  margin-top: 0px;
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
}
.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 10px;
  margin: 10px 10px 5px;
  display: inline-flex;
  width: 99%;
  border-radius: 5px;
}

.app-container:nth-of-type(2) {
  padding: 0px;
  margin: 0px 10px;
  background: #b4c8e3 !important;
  margin-top: -6px;
  position: relative;
  .pagination-text{
    position: absolute;
    bottom: 9px;
    right: 12%;
    font-size: 13px;
    color: black;
    z-index: 99999999;
  }
}
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 14% !important;
  padding: 2px 8px !important;
  height: 25px;
  color: black;
}
.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 13px !important;
  padding: 2px 8px !important;
  height: 25px;
  color: black;
}

.bg-white{
  border: 1px #cccccc solid;
  background:white ;
  margin-top: 10px;
}
:deep(.ant-tree .ant-tree-node-content-wrapper.ant-tree-node-selected),
:deep(.ant-table-tbody > tr.ant-table-row-selected > td){
  background-color: #1488b1;
  color: white;
}
// 合计行
:deep(.nc-summary){
  font-weight: bold;
  border-bottom-color: #9e9e9e;
  background-color: #cccccc!important;;
  border-right-color: #cccccc!important;
}
</style>

