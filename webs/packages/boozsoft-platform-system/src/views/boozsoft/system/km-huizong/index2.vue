<template>
  <div>
    <div class="app-container">
      <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
      <div class="app-container-top">
        <div style="width: 100%;margin-top: 5px;height: 100px;">
          <div style="width: 40%;padding-top:20px;">
            <div>
              <AccountPicker theme="three" readonly="" @reloadTable="dynamicAdReload" style="margin-top: -5px;"/>
            </div>
            <div style="margin-top: 5px;margin-left: 5px;">
              &nbsp;本位币：<span style="font-size: 14px;font-weight: bold">{{ bwb }} </span>
              &emsp;&emsp;样式：<span style="color: black;font-weight: bold">{{ styleName }}</span>
            </div>
          </div>
          <div style="width: 60%;float: right;margin-top: -77px;">
            <span style="font-size: 24px;color: #0096c7;font-weight: bold;margin-left: 20px;">科目汇总</span>
            <div class="ant-btn-group" style="float: right;margin-top: 5px;">
              <button
                type="button"
                class="ant-btn ant-btn-me"
                ant-click-animating-without-extra-node="false"
                @click="openQueryPage()"
              ><span>查询</span></button>
              <a-popover placement="bottom">
                <template #content>
                  <span class="group-btn-span" @click="exportExcelBtn('this')">导出当前</span><br/>
                  <span class="group-btn-span" @click="exportExcelBtn('all')">导出全部</span><br/>
                </template>
                <button
                  type="button"
                  class="ant-btn ant-btn-me"
                  ant-click-animating-without-extra-node="false"
                ><span>导出</span></button>
              </a-popover>
              <a-popover placement="bottom">
                <template #content>
                  <span class="group-btn-span" @click="thisPrint">打印当前</span><br/>
                </template>
                <button
                  type="button"
                  class="ant-btn ant-btn-me"
                  ant-click-animating-without-extra-node="false"
                ><span>打印</span></button>
              </a-popover>
              <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/zhongZhang/home/welcome')"><span>退出</span></button>
            </div>
            <span style="display: block;color: black;font-size: 14px;margin-top: 10px;">
            期间：<span>{{ time.strDate }}~{{ time.endDate }}</span>
            <span style="float: right;margin-top: 5px;">
              <div>
                <!-- 搜索 -->
                <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 130px;"
                          class="special_select">
                  <template v-for="item in searchConditonList">
                    <a-select-option v-if="item.ifShow == true" :value="item.dataIndex">
                      {{ item.title }}
                    </a-select-option>
                  </template>
                </a-select>
                <a-input-search
                  placeholder=""
                  v-model:value="pageParameter.searchConditon.value"
                  @search="pageSearch"
                  style="width: 150px;border-radius: 4px"
                />            &emsp;

                <a-button class="ant-btn" @click="closeFilterV(),pageReload()">
                  <SyncOutlined :style="{ fontSize: '14px' }"/>
                </a-button>

                <a-popover placement="bottom">
                  <template #content>
                    <a-popconfirm
                      ok-text="确定"
                      cancel-text="放弃"
                      @confirm="confirm"
                      @cancel="cancel"
                    >
                      <template #icon><b>栏目设置</b><br></template>
                      <template #title>
                        <a-table bordered :data-source="dynamicColumnData" :columns="dynamicColumns"
                                 childrenColumnName="children" :pagination="false" style="max-height: 500px;overflow-y: auto" :class="'a-table-font-size-12'">
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
                      <a-button style="width: 165px;border: none">栏目设置</a-button>
                    </a-popconfirm>
                    <br/>
                    <span @click="pageParameter.showRulesSize = 'MAX'"
                          :style="{backgroundColor: (pageParameter.showRulesSize==='MAX')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortDescendingOutlined
                      :style="{ fontSize: '14px' }"/>&emsp;大号字体&emsp;<CheckOutlined v-if="pageParameter.showRulesSize==='MAX'"
                                                                                    :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
                    <span @click="pageParameter.showRulesSize = 'MIN'"
                          :style="{backgroundColor: (pageParameter.showRulesSize==='MIN')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortAscendingOutlined
                      :style="{ fontSize: '14px' }"/>&emsp;小号字体&emsp;<CheckOutlined v-if="pageParameter.showRulesSize==='MIN'"
                                                                                    :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
                  </template>
                  <template #title>
                    <b>设置表格字号</b>
                  </template>
                  <a-button>
                    <SettingFilled :style="{ fontSize: '14px' }"/>
                  </a-button>
                </a-popover>
                <a-popover placement="bottom">
                  <template #content>
                    <span @click="onChangeSwitch('J')"
                          :style="{backgroundColor: (pageParameter.queryMark==='J')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;<OrderedListOutlined
                      :style="{ fontSize: '14px' }"/>&emsp;&emsp;金额式&nbsp;<CheckOutlined v-if="pageParameter.queryMark==='J'"
                                                                                         :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
                        <span @click="onChangeSwitch('SJ')"
                              :style="{backgroundColor: (pageParameter.queryMark==='SJ')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;<OrderedListOutlined
                          :style="{ fontSize: '14px' }"/>&emsp;数量金额式&nbsp;<CheckOutlined v-if="pageParameter.queryMark==='SJ'"
                                                                                         :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
                        <span @click="onChangeSwitch('WJ')"
                              :style="{backgroundColor: (pageParameter.queryMark==='WJ')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;<OrderedListOutlined
                          :style="{ fontSize: '14px' }"/>&emsp;外币金额式&nbsp;<CheckOutlined v-if="pageParameter.queryMark==='WJ'"
                                                                                         :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
                        <span @click="onChangeSwitch('SWJ')"
                              :style="{backgroundColor: (pageParameter.queryMark==='SWJ')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;<OrderedListOutlined
                          :style="{ fontSize: '14px' }"/>&emsp;数量外币式&nbsp;<CheckOutlined v-if="pageParameter.queryMark==='SWJ'"
                                                                                         :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
                  </template>
                </a-popover>
              </div>
            </span>
          </span>
          </div>
        </div>
      </div>
    </div>
    <div class="app-container">
      <div class="app-container-bottom">
        <PageWrapper dense content-full-height fixed-height content-class="flex">
          <BasicTable
            ref="tableRef"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :scroll="{ x: totalColumnWidth,y: windowHeight }"
            size="small"
            @register="registerTable"
            @fetch-success="fetchSuccess"
          >
            <template #ccode="{ record }">
              <span v-if="record.ccode!=''">
                 <span v-if="record.ccodeName!=null">{{record.ccode}}</span>
                  <span v-if="record.ccodeName==null"> {{record.ccode}}  </span>
              </span>
              <span>
                &emsp;
              </span>
            </template>
            <template #ccodeName="{ record }">
              <span v-if="record.ccode!=''">
                  {{record.ccodeName}}
              </span>
            </template>
            <template #md="{ record }">
              <span v-if="record.ccode!=''">
                  {{ moneyformat(record.md) }}
              </span>
            </template>
            <template #mc="{ record }">
              <span v-if="record.ccode!=''">
                  {{ moneyformat(record.mc) }}
              </span>
            </template>
            <template #ndS="{ record }">
              <span v-if="record.ccode!=''">
                  {{ formatNum(record.ndS) }}
              </span>
            </template>
            <template #ncS="{ record }">
              <span v-if="record.ccode!=''">
                  {{ formatNum(record.ncS) }}
              </span>
            </template>
            <template #nfrat_md="{ record }">
              <span v-if="record.ccode!=''">
                  {{ moneyformat(record.nfrat_md) }}
              </span>
            </template>
            <template #nfrat_mc="{ record }">
              <span v-if="record.ccode!=''">
                  {{ moneyformat(record.nfrat_mc) }}
              </span>
            </template>
            <template #summary>
              <TableSummary fixed>
                <TableSummaryRow v-if="pageParameter.queryMark=='J'" style="background-color: #cccccc;">
                  <TableSummaryCell class="nc-summary" :index="0" :align="'left'" :colSpan="4" style="border-right: none;">合计</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.md}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.mc}}</TableSummaryCell>
                </TableSummaryRow>

                <TableSummaryRow v-if="pageParameter.queryMark=='SJ'" style="background-color: #cccccc;">
                  <TableSummaryCell class="nc-summary" :index="0" :align="'left'" :colSpan="5" style="border-right: none;">合计</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.ndS}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.md}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.ncS}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.mc}}</TableSummaryCell>
                </TableSummaryRow>

                <TableSummaryRow v-if="pageParameter.queryMark=='WJ'" style="background-color: #cccccc;">
                  <TableSummaryCell class="nc-summary" :index="0" :align="'left'" :colSpan="5" style="border-right: none;">合计</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.nfrat_md}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.md}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.nfrat_mc}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.mc}}</TableSummaryCell>
                </TableSummaryRow>

                <TableSummaryRow v-if="pageParameter.queryMark=='SWJ'" style="background-color: #cccccc;">
                  <TableSummaryCell class="nc-summary" :index="0" :align="'left'" :colSpan="6" style="border-right: none;">合计</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.ndS}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.nfrat_md}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.md}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.ncS}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.nfrat_mc}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.mc}}</TableSummaryCell>
                </TableSummaryRow>
              </TableSummary>
            </template>
          </BasicTable>
          <div class="pagination-text" :style="{top: (pageParameter.queryMark=='J'?windowHeight+65:windowHeight+90)+'px'}" v-show="showPageNumber">
            {{`共 ${pageNumber}条记录&emsp;每页 1000 条`}}
          </div>
          <Query @save="loadPage" @register="registerQueryPage" />
          <Print @save="loadPrint" @register="registerPrintPage"/>
        </PageWrapper>
      </div>
    </div>
  </div>

</template>
<script setup lang="ts">
import {BasicTable, useTable} from '/@/components/Table'
import {useModal} from '/@/components/Modal'
import {PageWrapper} from '/@/components/Page'

import {
  Checkbox as ACheckbox,
  DatePicker as ADatePicker,
  Drawer as ADrawer,
  Input as AInput,
  message,
  Popconfirm as APopconfirm,
  Popover as APopover,
  Radio as ARadio,
  Select as ASelect, Table,
  Table as ATable
} from "ant-design-vue"
import {
  CheckOutlined,
  EditOutlined,
  FilterFilled,
  OrderedListOutlined,
  PicLeftOutlined,
  SettingFilled,
  SortAscendingOutlined,
  SortDescendingOutlined,
  SyncOutlined
} from '@ant-design/icons-vue'
import {findDbLanMuList, saveLanMuList} from '/@/api/record/system/accvoucher'
import {huizongGlStore} from '/@/api/record/km_huizong/generalLedger'

import {onMounted, reactive, ref} from "vue";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {assemblyDynamicColumn, initDynamics} from "./data";
import Query from "./popup/query.vue";
import Print from "/@/views/boozsoft/system/km-huizong/popup/print.vue";
import {cloneDeep} from "lodash-es";
import {useMessage} from "/@/hooks/web/useMessage";
import {getCurrentAccountName,} from "/@/api/task-api/tast-bus-api";
import {findAll} from '/@/api/record/km_huizong/data';
import {finByParameterAccuracy} from '/@/api/record/km_mingxi/data';
import {useUserStoreWidthOut} from "/@/store/modules/user";
import router from "/@/router";
import {savelog} from "/@/api/record/log";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findByAccId} from "/@/api/record/system/account";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useTabs} from "/@/hooks/web/useTabs";
import {exportExcel2} from "/@/api/record/generalLedger/excelExport";
import {useNewPrint} from "/@/utils/boozsoft/print/print";
import {tableStyle} from "/@/store/modules/abc-print";
/******************* 弹框加载中 **************************/
import {Loading} from '/@/components/Loading';
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
import {toThousandFilter} from "/@/utils/calculation";

const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const TableSummary = Table.Summary
const TableSummaryRow = Table.Summary.Row
const TableSummaryCell = Table.Summary.Cell
const {createConfirm, createWarningModal} = useMessage();
// 全局常量
const glStore = huizongGlStore()
const { closeCurrent } =useTabs();
const accNameAll=ref('')
// 页面变量
const pageParameter = reactive({
  database: '',
  thisAdInfo: {},
  total: 0,
  page: '',
  size: '',
  dynamicTenantId:'',
  openOne: '',
  companyCode: '',
  companyName: '',
  queryMark: 'J',
  showRulesSize: 'MIN',
  condition: {},
  searchConditon: {
    requirement: 'ccode',
    value: '',
  },
  filterConditon: {
    amountMaxQcMd: '',
    amountMinQcMd: '',
    amountMaxQcMc: '',
    amountMinQcMc: '',
  },
  reloadMark: false,
  wb: "",
  dw: "",
  km: "",
  minKm: "",
  maxKm: "",
  // 显示期间
  endDate: "",
  strDate: "",
  //显示未记账
  ishaveRjz: true,
  // 级次
  minJc: "1",
  maxJc: "1",
  bend: '1',
  jc: '1',
  numJd: '2',  // 数量精度
  priceJd: '2',  // 金额精度
  lvJd: '2',  // 汇率精度
  moneyJd: '2',  // 金额精度
  timflg: 'qj',
  bz: '',
  bzName:'全部',
  styleValue:'',
  styleList:'',
  ibook:'',
  cbill:'',
  voucherTypes:'',
  minInoId:'',
  maxInoId:'',
})

const styleName = ref<String>("金额式")
// 科目名称标题
const titleName = ref<String>("")
// 本位币
const bwb = ref<String>("")
//币种名称
const bzName = ref<String>("")
//显示未记账
const ibook = ref<boolean>(true)
const ljchecked = ref<boolean>(false)
const showStyle = ref([
  {
    'name': '金额式',
    'value': 'J'
  }, {
    'name': '数量金额式',
    'value': 'SJ'
  }, {
    'name': '外币金额式',
    'value': 'WJ'
  }, {
    'name': '数量外币式',
    'value': 'SWJ'
  }
])
const time = reactive(
  {
    endDate: "",
    strDate: ""
  }
)
const val = {
  openOne: 0
}
const visible = ref(false);
const searchConditonList = ref([])
const lanMuData = {'accId': '', 'menuName': '科目汇总表', 'type': '', objects: '',username:useUserStoreWidthOut().getUserInfo.username}
// 表格参数
const loadMark = ref(false)
const tableSelectedRowKeys = ref([])
const tableSelectedRowObjs = ref([])
const windowWidth = (document.documentElement.clientWidth - 70)
const windowHeight = (window.innerHeight - (340))
const totalColumnWidth = ref(0)

const tableRef = ref(null)
const userName = useUserStoreWidthOut().getUserInfo.username
const database = ref(getCurrentAccountName(true));
const accId = ref(getCurrentAccountName(false));
// 增加操作日志：1、修改2、删除3、停用、4查看、5导入、6导出、7打印
const logmap = ref({
  accId: database,
  username: useUserStoreWidthOut().getUserInfo.username,
  flag: '4',
  text: '',
});
const CrudApi = {
  list: findAll,
  columns: JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark)))
}


// 组件实例区
const [registerTable, { reload,setTableData,setColumns,getColumns,getPaginationRef,setPagination,getDataSource }] = useTable({
  api: useRouteApi(CrudApi.list, {schemaName: database}),
  columns: CrudApi.columns,
  bordered: true,
  loading: loadMark.value,
  immediate: false,
  canResize: true,
  showIndexColumn: true, //显示序号列
  pagination: {
    pageSize: 1000,
    simple: true,
  },
  searchInfo: pageParameter,
  tableSetting: {
    // 是否显示刷新按钮
    redo: true,
    // 是否显示尺寸调整按钮
    size: true,
    // 是否显示字段调整按钮
    setting: true,
    // 是否显示全屏按钮
    fullScreen: true,
  }
})
const [registerQueryPage, {openModal: openQueryPageM}] = useModal()
const [registerReviewPage, {openModal: openReviewPageM}] = useModal()
const [registerPrintPage, {openModal: openPrintPage}] = useModal()


const showPageNumber=ref(false)
const pageNumber=ref(0)
function fetchSuccess(data) {
  let list=data.items
  calculateTotal(list)
  pageNumber.value=data.items.length
  showPageNumber.value=true
  if(data.items.length<50){
    for (let i =  data.items.length; i < 50; i++) {
      data.items.push({ccodeName: null,ccode:'',md:0,mc:0,ndS:0,ncS:0,nfrat_md:0,nfrat_mc:0})
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
  let ndS = 0
  let ncS = 0
  let nfrat_md = 0
  let nfrat_mc = 0
  for (let i = 0; i < list.length; i++) {
    const e = list[i];
    md += parseFloat(e.md || '0')
    mc += parseFloat(e.mc || '0')
    ndS += parseFloat(e.ndS || '0')
    ncS += parseFloat(e.ncS || '0')
    nfrat_md += parseFloat(e.nfrat_md || '0')
    nfrat_mc += parseFloat(e.nfrat_mc || '0')
  }
  summaryTotals.value={
    md: toThousandFilter(md),
    mc: toThousandFilter(mc),
    ndS: toThousandFilter(ndS),
    ncS: toThousandFilter(ncS),
    nfrat_md: toThousandFilter(nfrat_md),
    nfrat_mc: toThousandFilter(nfrat_mc),
  }
}

const loadingRef = ref(false);
const compState = reactive({
  absolute: false,
  loading: false,
  tip: '加载中...',
});
function openCompFullLoading() {
  openLoading(false);
}
function openLoading(absolute: boolean) {
  compState.absolute = absolute;
  compState.loading = true;
}
/*******************END**************************/

function tableRow(row:any,num:any){
  return  (row.cclass.indexOf('小计')>0 ? 'table-month-striped' : null)
    || (row.cclass=='合计' ? 'table-year-striped' : null)
    || (row.number%2==0 ? 'table-odd-striped' : null)
}
// 跳转科目明细账
async function goRouter(ccode) {
  pageParameter.km=ccode
  pageParameter.bzName=pageParameter.bz
  pageParameter.openOne='2'
  pageParameter.dynamicTenantId=database.value
  pageParameter.strDate=time.strDate
  pageParameter.endDate=time.endDate
  router.push({
    path: '/zhongZhang/account-book/ab-kemuzhang/abk-mxtable',
    query: pageParameter,
  });
}
// 累计列是否显示
function ljchange(a){
  ljchecked.value=a.target.checked
  reloadColumns()
}

// 金额格式化
function moneyformat(data: any) {
  let str = ""
  if(data){
    // 千分位保留2位小数
    var source = String(data.toFixed(pageParameter.moneyJd)).split("."); //按小数点分成2部分
    source[0] = source[0].replace( new RegExp('(\\d)(?=(\\d{3})+$)' , 'ig'), "$1,");//只将整数部分进行都好分割
    str = source.join("." );//再将小数部分合并进来
  }
  return str;
}
// 数量格式化
function formatNum(data:any){
  let str = ""
  if(data){
    if(0 === data){
      str = ""
    }else {
      var source = String(data.toFixed(pageParameter.numJd)).split("."); //按小数点分成2部分
      source[0] = source[0].replace( new RegExp('(\\d)(?=(\\d{3})+$)' , 'ig'), "$1,");//只将整数部分进行都好分割
      str = source.join("." );
    }
  }
  return str;
}

onMounted(async () => {
  val.openOne = 1
  openQueryPageM(true, {
    data: val,
    openType:'kmhz'
  })
  loadMark.value = true
})

const openQueryPage = () => {
  val.openOne = 0
  openQueryPageM(true, {
    data: val,
    openType:'kmhz'
  })
}

// 页面函数区
const onSelectChange = (selectedRowKeys, obj) => {
  tableSelectedRowKeys.value = selectedRowKeys;
  tableSelectedRowObjs.value = obj;
};
const pageReload = () => {
  reload({
    searchInfo: pageParameter
  })
}

const onChangeSwitch = async (v) => { // 动态列
  styleName.value = showStyle.value.filter(o => o.value === v)[0].name
  pageParameter.queryMark = v
  resetDynamicColumnData()
  pageParameter.searchConditon.requirement = 'ccode'
  pageParameter.searchConditon.value = ''
}
const pageSearch = async () => {
  // 搜索前校验格式
  if ('' == pageParameter.searchConditon.requirement.trim()) {
    message.warn('请选择检索条件')
    return false
  }
  // 校验完成后搜索
  closeFilterV()
  pageReload()
}

const filterSearch = async () => {
  // 期初
  let qcmdmin = pageParameter.filterConditon.amountMinQcMd.trim()
  let qcmdmax = pageParameter.filterConditon.amountMaxQcMd.trim()
  if (qcmdmax != '' || qcmdmin != '') {
    if (qcmdmin != '' && qcmdmax != '' && (!isRealNum(qcmdmin) || !isRealNum(qcmdmax) || parseFloat(qcmdmin) == 0 || parseFloat(qcmdmax) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(qcmdmin) > parseFloat(qcmdmax)) {
      message.warn('金额区间最小值不能大于最大值！')
      closeFilterV()
      return false
    }
  }

  let qcmcmin = pageParameter.filterConditon.amountMinQcMc.trim()
  let qcmcmax = pageParameter.filterConditon.amountMaxQcMc.trim()
  if (qcmcmax != '' || qcmcmin != '') {
    if (qcmcmin != '' && qcmcmax != '' && (!isRealNum(qcmcmin) || !isRealNum(qcmcmax) || parseFloat(qcmcmin) == 0 || parseFloat(qcmcmax) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(qcmcmin) > parseFloat(qcmcmax)) {
      message.warn('金额区间最小值不能大于最大值！')
      closeFilterV()
      return false
    }
  }
  // 凭证
  let pzmdmin = pageParameter.filterConditon.amountMinPzMd.trim()
  let pzmdmax = pageParameter.filterConditon.amountMaxPzMd.trim()
  if (pzmdmax != '' || pzmdmin != '') {
    if (pzmdmin != '' && pzmdmax != '' && (!isRealNum(pzmdmin) || !isRealNum(pzmdmax) || parseFloat(pzmdmin) == 0 || parseFloat(pzmdmax) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(pzmdmin) > parseFloat(pzmdmax)) {
      message.warn('金额区间最小值不能大于最大值！')
      closeFilterV()
      return false
    }
  }
  let pzmcmin = pageParameter.filterConditon.amountMinPzMc.trim()
  let pzmcmax = pageParameter.filterConditon.amountMaxPzMc.trim()
  if (pzmcmax != '' || pzmcmin != '') {
    if (pzmcmin != '' && pzmcmax != '' && (!isRealNum(pzmcmin) || !isRealNum(pzmcmax) || parseFloat(pzmcmin) == 0 || parseFloat(pzmcmax) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(pzmcmin) > parseFloat(pzmcmax)) {
      message.warn('金额区间最小值不能大于最大值！')
      closeFilterV()
      return false
    }
  }
  // 期末
  let qmmdmin = pageParameter.filterConditon.amountMinQmMd.trim()
  let qmmdmax = pageParameter.filterConditon.amountMaxQmMd.trim()
  if (qmmdmax != '' || qmmdmin != '') {
    if (qmmdmin != '' && qmmdmax != '' && (!isRealNum(qmmdmin) || !isRealNum(qmmdmax) || parseFloat(qmmdmin) == 0 || parseFloat(qmmdmax) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(qmmdmin) > parseFloat(qmmdmax)) {
      message.warn('金额区间最小值不能大于最大值！')
      closeFilterV()
      return false
    }
  }
  let qmmcmin = pageParameter.filterConditon.amountMinQmMc.trim()
  let qmmcmax = pageParameter.filterConditon.amountMaxQmMc.trim()
  if (qmmcmax != '' || qmmcmin != '') {
    if (qmmcmin != '' && qmmcmax != '' && (!isRealNum(qmmcmin) || !isRealNum(qmmcmax) || parseFloat(qmmcmin) == 0 || parseFloat(qmmcmax) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(qmmcmin) > parseFloat(qmmcmax)) {
      message.warn('金额区间最小值不能大于最大值！')
      closeFilterV()
      return false
    }
  }

  // 校验完成后搜索
  pageReload()
}

const closeFilterV = () => {
  pageParameter.filterConditon.amountMaxQcMd = ''
  pageParameter.filterConditon.amountMinQcMd = ''
  pageParameter.filterConditon.amountMaxQcMc = ''
  pageParameter.filterConditon.amountMinQcMc = ''

  pageParameter.filterConditon.amountMaxPzMd = ''
  pageParameter.filterConditon.amountMinPzMd = ''
  pageParameter.filterConditon.amountMaxPzMc = ''
  pageParameter.filterConditon.amountMinPzMc = ''

  pageParameter.filterConditon.amountMaxQmMd = ''
  pageParameter.filterConditon.amountMinQmMd = ''
  pageParameter.filterConditon.amountMaxQmMc = ''
  pageParameter.filterConditon.amountMinQmMc = ''

  visible.value = false
  reloadColumns()
}

function isRealNum(val) {
  // isNaN()函数 把空串 空格 以及NUll 按照0来处理 所以先去除，
  if (val === "" || val == null) {
    return false;
  }
  if (!isNaN(val)) {
    //对于空数组和只有一个数值成员的数组或全是数字组成的字符串，isNaN返回false，例如：'123'、[]、[2]、['123'],isNaN返回false,   //所以如果不需要val包含这些特殊情况，则这个判断改写为if(!isNaN(val) && typeof val === 'number' )
    return true;
  } else {
    return false;
  }
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
    onOk: async () => {
      // 调整数据库 列参数
      lanMuData.accId = database.value.substring(0,database.value.length-5)
      lanMuData.objects = JSON.stringify(filterModifyData(dynamicColumnData.value, dynamicColumnDataCopy))
      if (lanMuData.objects == '[]') {
        createWarningModal({content: '请先做修改后再进行确认同步数据库！'})
      } else {
        saveLanMuList(lanMuData).then(res => {
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

function filterModifyData(lanMuList: any, copyList) {
  let a = lanMuList.filter(item => {
    try {
      copyList.forEach(item2 => {
        if (item.key === item2.key && item.name == item2.name) {
          if (item.nameNew != item2.nameNew || item.width != item2.width || item.check != item2.check || item.align != item2.align)
            throw new Error('ok')
        }
      })
      return false
    } catch (e) {
      if (e.message == 'ok') {
        return true
      } else {
        return false
      }
    }
  })

  //对子节点处理  过滤有子节点并且变动 添加到a
  lanMuList.forEach((item, index) => {
    if (item.children) {
      let b = item.children.filter(item2 => {
        try {
          copyList[index].children.forEach(item3 => {
            if (item2.key === item3.key && item2.name == item3.name) {
              if (item2.nameNew != item3.nameNew || item2.width != item3.width || item2.check != item3.check || item2.align != item3.align)
                throw new Error('ok')
            }
          })
          return false
        } catch (e) {
          if (e.message == 'ok') {
            return true
          } else {
            return false
          }
        }
      })
      b.forEach(item => {
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


async function resetDynamicColumnData() {
  // 先从数据查询是否已经设置
  lanMuData.accId = database.value.substring(0,database.value.length-5)
  lanMuData.type = pageParameter.queryMark
  findDbLanMuList(lanMuData).then(res => {
    // 栏目列
    let dbList = res.items
    if (dbList.length > 0) {
      let statiList = initDynamics()[pageParameter.queryMark]
      dbList = combineParameters(statiList, dbList)
      dynamicColumnData.value = dbList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(dbList))
    } else {
      let statiList = initDynamics()[pageParameter.queryMark]
      dynamicColumnData.value = statiList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(statiList))
    }
    // 表格列
    reloadColumns()
    pageReload()
  })
}

function combineParameters(staticList: any, dbList: any) {
  staticList.forEach(item => {
    dbList.forEach(item2 => {
      if (item.key === item2.key && item.name === item2.name) {
        item.nameNew = item2.nameNew
        item.width = parseInt(item2.width)
        item.check = item2.check == 'true'
        item.align = item2.align
      } else {
        //对子节点处理
        if (item.children) {
          item.children.forEach(item3 => {
            if (item3.key === item2.key && item3.name === item2.name) {
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

const reloadColumns = () => {
  let a = []
  a = getColumns()
  let newA = JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark,ljchecked.value)))
  newA = assemblyDynamicColumn(dynamicColumnData.value, newA)
  setColumns(newA)
  initTableWidth(newA)
  //多表头 添加子节点筛选
  let seachList = []
  newA.forEach(item => {
    if (item.children) {
      item.children.forEach(item2 => {
        item2.title = item.title + item2.title
        seachList.push(item2)
      })
    } else {
      seachList.push(item)
    }
  })
  searchConditonList.value = seachList
}

function initTableWidth(thisCs) {
  let total = 60
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })
  if (total > windowWidth) {
    let f = 0
    if (visible.value) f = 260
    totalColumnWidth.value = Number(windowWidth) - f
    tableRef.value.$el.style.setProperty('width', (windowWidth + 20 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 20) + 'px')
  }
}

// 格式化  Tue Jun 08 2021 16:57:19 GMT+0800 (中国标准时间)
function timeformat(dateData) {
  let date = new Date(dateData);
  let y = date.getFullYear();
  let m = date.getMonth() + 1;
  m = m < 10 ? '0' + m : m;
  let d = date.getDate();
  d = d < 10 ? '0' + d : d;
  return y + '-' + m + '-' + d;
}

/*栏目设置end*/
const loadPage = async (map) => {
  let data = map.data
  // 回显筛选条件
  loadMark.value = false
  // 参数规则
  let ParameteRule= await useRouteApi(finByParameterAccuracy,{schemaName: database})('')
  if(ParameteRule.length>0){
    for (let i = 0; i < ParameteRule.length; i++) {
      if(ParameteRule[i].paraName==='汇率'){
        pageParameter.lvJd=ParameteRule[i].decimalPlaces
      }
      if(ParameteRule[i].paraName==='单价'){
        pageParameter.priceJd=ParameteRule[i].decimalPlaces
      }
      if(ParameteRule[i].paraName==='数量'){
        pageParameter.numJd=ParameteRule[i].decimalPlaces
      }
      if(ParameteRule[i].paraName==='金额'){
        pageParameter.moneyJd=ParameteRule[i].decimalPlaces
      }
    }
  }

  pageParameter.minKm = data.minKm === undefined ? '空' : data.minKm
  pageParameter.maxKm = data.maxKm === undefined ? '空' : data.maxKm
  pageParameter.strDate = data.strDate.replace("-", "")
  pageParameter.endDate = data.endDate.replace("-", "")
  pageParameter.showRulesSize = data.fontSize
  bzName.value = data.bzName
  pageParameter.ishaveRjz = data.ishaveRjz
  pageParameter.minJc = data.minJc
  pageParameter.maxJc = data.maxJc
  pageParameter.bend = data.jc
  pageParameter.bz = data.bzName
  pageParameter.styleValue = data.styleValue
  pageParameter.ibook = data.ibook
  pageParameter.cbill = data.cbill
  pageParameter.voucherTypes = data.pzType
  pageParameter.minInoId = data.minInoId
  pageParameter.maxInoId = data.maxInoId
  pageParameter.styleList = JSON.stringify(data.styleList)

  time.strDate = data.strDate.replace("-", ".")
  time.endDate = data.endDate.replace("-", ".")
  time.endDate = data.endDate.replace("-", ".")

  // 外币金额式
  if(data.bzName!=='全部'){ pageParameter.queryMark = 'WJ';styleName.value='外币金额式' }

  if (data.openOne == 1) { // 第一次初始化 + 条件查询
    resetDynamicColumnData()
  } else { // 查询条件查询
    closeFilterV()
    pageParameter.searchConditon.value = ''
    pageReload()
  }
}
const dynamicAdReload = async (obj) =>{
  console.log(obj)
  accNameAll.value = obj.companyName   // 账套全称
  bwb.value = obj.target.currencyName
  // 先获取部门组件查看是否存在部门信息
  pageParameter.page= getPaginationRef().current
  pageParameter.size= getPaginationRef().pageSize
  if(pageParameter.strDate!==''){
    let res  =  await useRouteApi(findAll,{schemaName: obj.accId+'-'+obj.year})(pageParameter)
    if (res != null && res.total> 0){
      loadMark.value = true
      setTableData([]) // 清空可能残留的数据
      setTableData(res.items)
      // 底部分页信息
      database.value = obj.accId+'-'+obj.year
      pageParameter.thisAdInfo = {}
      pageParameter.thisAdInfo = obj
      pageParameter.total = res.total
      setPagination({total:res.total})
    }
    else {
      createWarningModal({title: '温馨提示',content: '暂无任何数据！'});
      pageParameter.thisAdInfo = {}
      pageParameter.total = -1
    }
  }
  loadMark.value = false
}

const exportExcelBtn = async (flag) => {
  // 当前页显示多少条
  let pageSize:any=getPaginationRef().pageSize
  const data = JSON.parse(JSON.stringify(getDataSource()))
  let data2:any=data.splice(0,pageSize)
  let columns:any=getColumns()
  if(flag==='all'){
    pageParameter.page= getPaginationRef().current
    pageParameter.size= getPaginationRef().pageSize*10000
    let res  =  await useRouteApi(findAll,{schemaName: database})(pageParameter)
    data2=res.items
  }

  const multiHeader:any = [[],[]]
  const keys:any = []
  const merges:any = []
  const cell = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  let flg:any = 0;
  columns.forEach((v,index)=>{
    multiHeader[0].push(v.title)
    multiHeader[1].push('')
    keys.push(v.key)
    //合并上下单元格
    merges.push('`' + cell[flg] + '1:' + cell[flg] +'2`');
    flg = flg + 1
  })

  data2.forEach(v=>{
    v.ccode=v.ccode==null?'':v.ccode
    v.ccodeName=v.ccodeName==null?'':v.ccodeName
    v.md=moneyformat(v.md)
    v.mc=moneyformat(v.mc)
  })
  //样式靠右列
  let rightrow=['D','E']
  //样式靠左列
  let leftrow=['B','C']

  const sheet =[
    {
      title: [],
      tHeader: [],
      multiHeader: multiHeader,
      table: data2,
      keys: keys,
      merges: merges,
      sheetName: 'sheet1',
      cellStyle: [],
      rightrow:rightrow,
      leftrow:leftrow,
    },
  ]
  exportExcel2(sheet, 'xlsx',accNameAll.value+'-科目汇总-'+time.strDate+' - '+time.endDate)
}

const thisPrint = () => {
  openPrintPage(true, {
    data: {
      dynamicTenantId: database.value,
      defaultAdName: useCompanyOperateStoreWidthOut().getSchemaName,
      year: time.strDate.substring(0,4),
    }
  })
}
// 打印弹框回调
const loadPrint = async (obj) => {
 let temp1= [...new Set(getDataSource().filter(v=>v.zcsum!==undefined).map(v=>v.zcsum))].toString()
 let temp2= [...new Set(getDataSource().filter(v=>v.zfnum!==undefined).map(v=>v.zfnum))].toString()

  let zcnum=temp1===''?'0':temp1
  let zfnum=temp2===''?'0':temp2
  openCompFullLoading()
  const data = JSON.parse(JSON.stringify(getDataSource()))
  let printUser = ''
  if(obj.printUser){
    printUser = '制表人：' + userName
  }
  // let printBz = ''
  // if(obj.printBz){
  //   printBz = '币种：'+bwb.value
  // }
  let printList: any = []
  data.forEach((item,index) => {
    let item1 = {}
    item1[0] = item.cclass
    item1[1] = item.ccode==null?'':item.ccode
    item1[2] = item.ccodeName==null?'':setString(item.ccodeName,27)
    item1[3] = item.md==null?'':item.md==0?'':moneyformat(item.md)
    item1[4] = item.mc==null?'':item.mc==0?'':moneyformat(item.mc)
    printList.push(item1)
  })
  for (let i=0; i<printList.length%27; i++){
    let item1 = {}
    item1[0] = ''
    item1[1] = ''
    item1[2] = ''
    item1[3] = ''
    item1[4] = ''
    printList.push(item1)
  }

  let num = Math.ceil(printList.length/27)
  useNewPrint({data: ['p', 'px', 'a4', true]}, (doc) => {
    loadMark.value = false
    compState.loading = false
    doc.autoTable({
      head: [['', '', '科目汇总表', '', ''],
        ['核算单位：'+accNameAll.value ,'', '期间：' + time.strDate+' - '+time.endDate,'','凭证数量:'+zcnum+' 作废:'+zfnum],
        ['科目类别', '科目编码', '科目名称', '借方发生', '贷方发生']],
      body: printList,
      // startY: 60,
      styles: tableStyle(),
      margin: {
        left: 40,
        top: 30,
      },
      addPageContent: (data) => {
        //data.table.finalY 表格最大可容纳y轴坐标，超出时将根据设置决定是否另取一页.在页面需要分页时出现
        let tabHeigth = data.table.height,
          tabMarginTop = data.settings.margin.top,
          tabSize = data.table.finalY - tabMarginTop,//表格最大Y轴-表格顶部距离得到每页表格的最大值
          tabMarginLeft = data.settings.margin.left;
        if (data.table.finalY)//是否分页 有分页时才有该属性finalY
          if (data.pageNumber != Math.ceil(tabHeigth / tabSize)) return;//如果需要每一页都显示页尾则注释该行代码
        //data.cursor.y ,data.cursor.x:表格最后一个单元坐标
        data.doc.setFontSize(9)
        // data.doc.setFont('fuhuiR', 'bold')
        // doc.autoTableText(
        //   '核算单位：' + accNameAll.value,
        //   tabMarginLeft,
        //   data.cursor.y + 3,
        //   0
        // );
        doc.autoTableText(
          printUser,
          data.cursor.x/2-25,
          data.cursor.y + 3,
          0
        );
        doc.autoTableText(
          '第'+data.doc.getCurrentPageInfo().pageNumber+'页/共'+num+'页',
          // '第'+data.doc.getCurrentPageInfo().pageNumber+'页',
          data.cursor.x - 50,
          data.cursor.y + 3,
          0
        );
      },
      didParseCell(data) {
        data.cell.styles.cellPadding = {top: 2, left: 2, right: 2, bottom: 2}
        data.cell.styles.fillColor = [255, 255, 255]
        data.cell.styles.fontSize = 9
        data.cell.styles.lineColor = [150, 150, 150]
        // data.cell.styles.bold = false

        if (data.section == 'head' && data.row.index == 0) {

          data.cell.styles.fontSize = 20
          data.cell.styles.fontStyle = 'bold'
          data.cell.styles.lineColor = [255, 255, 255]
          if (data.column.index == 2) {
            data.cell.colSpan = 2
            data.cell.styles.halign = 'center'
          }
        }
        if (data.section == 'head' && data.row.index == 1) {
          data.cell.styles.fontSize = 10
          data.cell.styles.fontStyle = 'bold'
          data.cell.styles.lineColor = [255, 255, 255]
          if (data.column.index == 0) {
            data.cell.colSpan = 2
            data.cell.styles.halign = 'left'
          } else if (data.column.index == 2) {
            data.cell.colSpan = 2
            data.cell.styles.halign = 'center'
          }
          else {
            data.cell.styles.halign = 'right'
          }
        }
        if (data.section == 'head' && data.row.index == 2) {
          data.cell.styles.fontSize = 10
          data.cell.styles.cellPadding = {top: 4, left: 2, right: 2, bottom: 3}
          data.cell.styles.fontStyle = 'bold'
          data.cell.styles.fillColor = [230, 230, 230]
          data.cell.styles.halign = 'center'
        }
        if (data.section == 'body'){
          if (data.row.index%2==1) {
            data.cell.styles.fillColor = [240,240,240]
          }
          if (data.row.raw[0] == '本月合计' || data.row.raw[0] == '本年累计') {
            data.cell.styles.fontStyle = 'bold'
          }
        }
      },
      columnStyles: {
        0: {maxHeight: 10,cellWidth: 50, halign: 'center'},
        1: {cellWidth: 70, halign: 'left'},
        2: {cellWidth: 80, halign: 'left'},
        3: {cellWidth: 90, halign: 'right'},
        4: {cellWidth: 90, halign: 'right'},
      }
    })
  })
}
//js切割字符串
function setString(str, len) {
  var strlen = 0;
  var s = "";
  for (var i = 0; i < str.length; i++) {
    if (str.charCodeAt(i) > 128) {
      strlen += 2;
    } else {
      strlen += 1.2;
    }
    s += str.charAt(i);
    if (strlen >= len) {
      return s+"...";
    }
  }
  return s;
}
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
<style scoped>
:deep(.vben-basic-table .ant-pagination) {
  margin-top: 0px;
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
}
:deep(.nc-summary){
  font-weight: bold;
  background-color: #cccccc!important;;
  border-right-color: #cccccc!important;
}

.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
}

.app-container:nth-of-type(2) {
  padding: 0px;
  margin: 0px 10px;
  background: #b4c8e3 !important;
  margin-top: -45px;
  position: relative;
.pagination-text{
  position: absolute;
  bottom: 9px;
  right: 11%;
  font-size: 13px;
  color: black;
  z-index: 99999999;
}
}
.app-container-top{
  height: 120px;
}
.app-container-bottom {
:deep(.ant-table-thead th) {
  background-color: #d8d8d8 !important;
  font-weight: bold !important;
  border-left: 1px solid #d8d8d8 !important;
  border-bottom: 1px solid #d8d8d8 !important;
  border-top: 1px solid #d8d8d8 !important;
}
}

:deep(.table-month-striped) {
  background-color: honeydew;
}
:deep(.table-year-striped)  {
  background-color: lightyellow;
}
:deep(.table-odd-striped)  {
  background-color: #fafafa;
}

.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 14px !important;
  padding: 5px 8px !important;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 13px !important;
  padding: 2px 8px !important;
}
.a-table-font-size-16 :deep(td) .a-table-money-font-size{
  font-size: 14px !important;
  color: #0096c7;
}
.a-table-font-size-12 :deep(td) .a-table-money-font-size{
  font-size: 13px !important;
  color: #0096c7;
}

:deep(.a-table-font-arial){
  font-family: Arial!important;
}
.group-btn-span-special2{
  width: 130px;
}
:deep(.ant-checkbox){
  border: 1px solid #2f2a2a;
  border-radius: 4px;
}
</style>
