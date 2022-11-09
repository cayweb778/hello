<template>
  <div>
    <div class="app-container">
      <ProfileOutlined style="color: #0096c7;font-size: 60px;margin-top: 10px;"/>&emsp;
      <div style="width: 33%;margin-top: 9px;">
        <AccountPicker theme="three" readonly="" @reloadTable="dynamicAdReload" style="margin-top: 2px;"/>
        <div style="margin-top: 12px;"/>
        <span style="color:#666666;font-weight: bold;margin-left: 6px;">科目：</span>
        <a-select
          show-search
          placeholder="科目选择"
          option-filter-prop="children"
          style="width: 250px;display: inline-block;"
          @change="handleChangeMinKm"
          v-model:value="pageParameter.km"
        >
          <a-select-option v-for="d in kmList" :value="d.ccode">
            {{ d.ccodeName }}
          </a-select-option>
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </a-select>
        <span style="color:#666666;font-weight: bold;margin-left: 30px;">显示样式：</span><span style="color: black;font-weight: bold">{{ styleName }}</span>
      </div>
      <div style="width: 29.5%;text-align: center;margin-top: 9px;">
        <span style="font-size: 24px;font-weight: bold;color:rgb(0 150 199)">{{ pageMode == '1' ? titleName : '科目' }}明细表</span><br>
        <span style="color:#666666;font-weight: bold;">期间：</span> <span style="color: #0f0f0f;font-weight: bold;">{{ time.strDate }} - {{ time.endDate }}</span>
      </div>
      <div style="width: 33%;text-align: right;" v-if="pageMode=='1'">
        <a-button
          class="actod-btn"
          ant-click-animating-without-extra-node="false"
          @click="openQueryPage()"
        ><span>查询</span></a-button>
        <a-popover placement="bottom">
          <template #content>
            <span class="group-btn-span" @click="thisPageImport">导出当前</span><br/>
            <span class="group-btn-span" @click="allPageImport">导出全部</span>
          </template>
          <a-button
            class="actod-btn"
            ant-click-animating-without-extra-node="false"
          ><span>导出表格</span></a-button>
        </a-popover>
        <a-popover placement="bottom">
          <template #content>
            <span class="group-btn-span" @click="thisPrint">打印当前</span><br/>
          </template>
          <a-button
            class="actod-btn"
            ant-click-animating-without-extra-node="false"
          ><span>打印</span></a-button>
        </a-popover>
        <a-button class="actod-btn" style="border-right: 1px solid #cccccc;" @click="closeCurrent(),router.push('/zhongZhang/home/welcome')"><span>退出</span></a-button>
        <p/>
        <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 130px;text-align: left;" class="special_select">
          <template v-for="item in searchConditonList.slice(1)">
            <a-select-option v-if="item.ifShow == true" :value="item.dataIndex">
              {{ item.title }}
            </a-select-option>
          </template>
        </a-select>
        <a-input-search
          placeholder=""
          v-model:value="pageParameter.searchConditon.value"
          @search="pageSearch"
          style="width: 150px;border-radius: 4px;border: 1px #cccccc solid;"
        />&emsp;
        <a-button class="ant-btn" @click="closeFilterV(),pageReload()">
          <SyncOutlined :style="{ fontSize: '14px' }"/>
        </a-button>
        <a-popover class="ant-btn-default" placement="bottom">
          <template #content>
            <a-popconfirm
              ok-text="确定"
              cancel-text="放弃"
              @confirm="confirm"
              @cancel="cancel">
              <template #icon><b>栏目设置</b><br></template>
              <template #title>
                <a-table bordered :data-source="dynamicColumnData" :columns="dynamicColumns"
                         childrenColumnName="children" :pagination="false"
                         style="max-height: 650px;overflow-y: auto" :class="'a-table-font-size-12'">
                  <template #checkBox="{ text, record }">
                    <a-checkbox v-model:checked="record.check" :disabled="record.isFixed"/>
                  </template>
                  <template #widthInput="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]" class="editable-cell-input-wrapper">
                        <a-input type="number" v-model:value="editableData[record.key].width"
                                 @pressEnter="save(record.key,record.min,record.max)"
                                 style="width: 80px"/>
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
                                   :disabled="record.align==''">
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
              <a-button style="width: 128px;margin-bottom: 2px">栏目设置</a-button>
            </a-popconfirm>
            <br/>
            <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                  :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white',width:'150px'}:''">
        <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
              v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
            <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                  :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white',width:'150px'}:''">
        <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
              v-if="pageParameter.showRulesSize==='MIN'"/></span>
          </template>
          <a-button>
            <SettingFilled :style="{ fontSize: '14px' }"/>
          </a-button>
        </a-popover>
        <a-popover placement="bottom">
          <template #content>
            <span class="group-btn-span-special2" @click="onChangeSwitch('J')"
                  :style="pageParameter.queryMark=='J'?{backgroundColor: '#0096c7',color: 'white',width:'150px'}:''">
              <SortDescendingOutlined/>&nbsp;金额式&emsp;&ensp;<CheckOutlined
              v-if="pageParameter.queryMark=='J'"/></span><br/>
                  <span class="group-btn-span-special2" @click="onChangeSwitch('SJ')"
                        :style="pageParameter.queryMark=='SJ'?{backgroundColor: '#0096c7',color: 'white',width:'150px'}:''">
              <SortAscendingOutlined/>&nbsp;数量金额式<CheckOutlined
                    v-if="pageParameter.queryMark=='SJ'"/></span><br/>
                  <span class="group-btn-span-special2" @click="onChangeSwitch('WJ')"
                        :style="pageParameter.queryMark=='WJ'?{backgroundColor: '#0096c7',color: 'white',width:'150px'}:''">
              <SortDescendingOutlined/>&nbsp;外币金额式<CheckOutlined
                    v-if="pageParameter.queryMark=='WJ'"/></span><br/>
                  <span class="group-btn-span-special2" @click="onChangeSwitch('SWJ')"
                        :style="pageParameter.queryMark=='SWJ'?{backgroundColor: '#0096c7',color: 'white',width:'150px'}:''">
              <SortAscendingOutlined/>&nbsp;数量外币式<CheckOutlined
                    v-if="pageParameter.queryMark=='SWJ'"/></span><br/>
                </template>
                <a-button>
            <PicLeftOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
        </a-popover>
      </div>
      <div style="width: 33%;text-align: right;" v-else>
        <a-button
          class="actod-btn"
          ant-click-animating-without-extra-node="false"
          @click="openQueryPage()"
        ><span>查询</span></a-button>
        <a-button class="actod-btn" style="border-right: 1px solid #cccccc;" @click="closeCurrent(),router.push('/zhongZhang/home/welcome')"><span>退出</span></a-button>
      </div>
    </div>
    <div class="app-container">
      <div class="app-container-bottom">
        <div v-if="pageMode=='1'" :style="{height: (windowHeight)+'px'}">
          <BasicTable
            ref="tableRef"
            :striped="true"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :scroll="{ x: totalColumnWidth,y: (pageParameter.queryMark=='J'?windowHeight:windowHeight-20) }"
            size="small"
            @register="registerTable"
            @fetch-success="fetchSuccess"
          >

            <template #inoId="{ record }">
              {{ record.inoId }}
            </template>
            <template #md="{ record }">
              <span class="a-table-money-font-size a-table-font-arial" style="color: black;font-weight: bold;">{{moneyformat(record.md)}}</span>
            </template>
            <template #mc="{ record }">
              <span class="a-table-money-font-size a-table-font-arial" style="color: black;font-weight: bold;">{{moneyformat(record.mc)}}</span>
            </template>
            <template #yue="{ record }">
              <span class="a-table-money-font-size a-table-font-arial" style="color: black;font-weight: bold;">{{moneyformat(record.yue)}}</span>
            </template>
            <template #ndS="{ record }">
              <span class="a-table-money-font-size a-table-font-arial" style="color: black;font-weight: bold;">{{moneyformat(record.ndS)}}</span>
            </template>
            <template #ncS="{ record }">
              <span class="a-table-money-font-size a-table-font-arial" style="color: black;font-weight: bold;">{{moneyformat(record.ncS)}}</span>
            </template>
            <template #yue_num="{ record }">
              <span class="a-table-money-font-size a-table-font-arial" style="color: black;font-weight: bold;">{{moneyformat(record.yue_num)}}</span>
            </template>
            <template #nfrat_md="{ record }">
              <span class="a-table-money-font-size a-table-font-arial" style="color: black;font-weight: bold;">{{moneyformat(record.nfrat_md)}}</span>
            </template>
            <template #nfrat_mc="{ record }">
              <span class="a-table-money-font-size a-table-font-arial" style="color: black;font-weight: bold;">{{moneyformat(record.nfrat_mc)}}</span>
            </template>
            <template #yue_nfrat="{ record }">
              <span class="a-table-money-font-size a-table-font-arial" style="color: black;font-weight: bold;">{{moneyformat(record.yue_nfrat)}}</span>
            </template>

            <template #summary>
              <TableSummary fixed>
                <TableSummaryRow style="background-color: #cccccc;">
                  <TableSummaryCell class="nc-summary" :index="0" :align="'left'" :colSpan="pageParameter.queryMark=='J'?4:pageParameter.queryMark=='SWJ'?6:5" style="border-right: none;">合计</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" v-if="pageParameter.queryMark=='SJ' || pageParameter.queryMark=='SWJ'" style="border-right: none;">{{summaryTotals.ndS}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" v-if="pageParameter.queryMark=='WJ' || pageParameter.queryMark=='SWJ'" style="border-right: none;">{{summaryTotals.nfrat_md}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.md}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" v-if="pageParameter.queryMark=='SJ' || pageParameter.queryMark=='SWJ'" style="border-right: none;">{{summaryTotals.ncS}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" v-if="pageParameter.queryMark=='WJ' || pageParameter.queryMark=='SWJ'" style="border-right: none;">{{summaryTotals.nfrat_mc}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.mc}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;"/>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" v-if="pageParameter.queryMark=='SJ' || pageParameter.queryMark=='SWJ'" style="border-right: none;">{{summaryTotals.yue_num}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" v-if="pageParameter.queryMark=='WJ' || pageParameter.queryMark=='SWJ'" style="border-right: none;">{{summaryTotals.yue_nfrat}}</TableSummaryCell>
                  <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.yue}}</TableSummaryCell>
                </TableSummaryRow>
              </TableSummary>
            </template>
          </BasicTable>
          <div class="pagination-text" :style="{top: (windowHeight+60)+'px'}" v-show="showPageNumber">
            {{`共 ${pageNumber} 条记录&emsp;每页 1000 条`}}
          </div>
        </div>
        <div :style="{height: (60+windowHeight)+'px',overflowY: 'auto'}" v-else>
          <TablePiece v-if="startRender" v-model="pageParameter" v-for="accId in queryAccIds" :pageAccName="showCurrentName(accId)" :pageAccId="accId" :exportData="exportData"/>
        </div>
      </div>
    </div>
    <Query @save="loadPage" @register="registerQueryPage" />
    <Print @save="loadPrint" @register="registerPrintPage"/>
    <a-modal v-model:visible="visibleModal" title="提示" @ok="visibleOK" cancelText="关闭" okText="导出表格">
      <p/>
      <a-radio-group style="width: 100%;margin-left: 50px;" v-model:value="radiovalue">
        <a-radio :value="1">
          <span>所有科目按页签显示</span>
        </a-radio>
        <a-radio :value="2" style="margin-left: 50px;">
          <span>所有科目显示在同一页签</span>
        </a-radio>
      </a-radio-group>
      <p/>
    </a-modal>
  </div>
</template>
<script setup lang="ts">
import {BasicTable, useTable} from '/@/components/Table'
import {useModal} from '/@/components/Modal'

import {
  Checkbox as ACheckbox,
  DatePicker as ADatePicker,
  Drawer as ADrawer,
  Input as AInput,
  message,
  Modal as AModal,
  Popconfirm as APopconfirm,
  Popover as APopover,
  Radio as ARadio,
  Select as ASelect, Table,
  Table as ATable
} from "ant-design-vue"
import {
  ProfileOutlined,
  CaretDownOutlined,
  CheckOutlined,
  EditOutlined,
  FilterFilled,
  PicLeftOutlined,
  SettingFilled,
  SortAscendingOutlined,
  SortDescendingOutlined,
  SyncOutlined
} from '@ant-design/icons-vue'
import {delDigestLanMuList, findDbLanMuList, saveLanMuList} from '/@/api/record/system/accvoucher'
import {mxGlStore} from '/@/api/record/km_mingxi/generalLedger'

import {onMounted, reactive, ref} from "vue";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {assemblyDynamicColumn, initDynamics} from "./data";
import Query from "/@/views/boozsoft/system/book-balance/popup/queryAll2.vue";
import {cloneDeep} from "lodash-es";
import {useMessage} from "/@/hooks/web/useMessage";
import {getCurrentAccountName,} from "/@/api/task-api/tast-bus-api";
import {
  finByParameterAccuracy,
  findAll,
  findByCodeExportExcel,
  findByCodeExportExcel2
} from '/@/api/record/km_mingxi/data';
import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
import router from "/@/router";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findByAccId} from "/@/api/record/system/account";
import {useRoute} from "vue-router";
import TablePiece from "/@/views/boozsoft/system/kmmingxi/popup/TablePiece.vue";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useTabs} from "/@/hooks/web/useTabs";
import {useNcLogger} from "/@/utils/boozsoft/record/recordUtils";
import {exportExcel, exportExcel2} from "/@/api/record/generalLedger/excelExport";
import Print from "/@/views/boozsoft/system/kmmingxi/popup/print.vue";
/******************* 弹框加载中 **************************/
import {Loading} from '/@/components/Loading';
import {useNewPrint} from "/@/utils/boozsoft/print/print";
import {tableStyle} from "/@/store/modules/abc-print";
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
const glStore = mxGlStore()
const {closeCurrent} = useTabs();
const mockData = ref([
  {
    key: '0',
    title: '标准辅助核算项编码',
  },
  {
    key: '1',
    title: '标准辅助核算项名称',
  },
  {
    key: '2',
    title: '扩展辅助核算项编码',
  },
  {
    key: '3',
    title: '扩展辅助核算项名称',
  },
  {
    key: '4',
    title: '结算方式名称',
  },
  {
    key: '5',
    title: '结算日期',
  },
  {
    key: '6',
    title: '票据号',
  },
  {
    key: '7',
    title: '结算单位',
  }
])
const targetKeys = ref([])
const selectedKeys = ref([])
const recordName = 'SystemLogger'
const {ncLogger} = useNcLogger({recordName})

const exportData=ref({
    map:{}
})
// 页面变量
const pageParameter:any = reactive({
  database: '',
  thisAdInfo: {},
  total: 0,
  page: '',
  size: '',
  companyCode: '',
  companyName: '',
  queryMark: 'J',
  showRulesSize: 'MIN',
  condition: {},
  searchConditon: {
    requirement: 'cdigest',
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
  numJd: '2',  // 数量精度
  priceJd: '2',  // 金额精度
  lvJd: '2',  // 汇率精度
  moneyJd: '2',  // 金额精度
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
  timflg: 'qj',
  bz: '',
  styleValue: '',
  digestStyle: '',
  kmList: [],
  pzType: '',
})
const styleName = ref<String>("金额式")
// 科目名称标题
const titleName = ref<String>("")
// 本位币
const bwb = ref<String>("")
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
const startRender = ref(false);
const searchConditonList = ref([])
const lanMuData = {
  'accId': '',
  'menuName': '明细账',
  'type': '',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username
}

// 表格参数
const loadMark = ref(false)
const tableSelectedRowKeys = ref([])
const tableSelectedRowObjs = ref([])
const windowWidth = (window.innerWidth - 70)
const windowHeight = (window.innerHeight - (327))
const totalColumnWidth = ref(0)

const tableRef = ref(null)
const userName = useUserStoreWidthOut().getUserInfo.username
const userStore = useUserStore();
// 数据库模式名称
const database = ref(getCurrentAccountName(true));
const accId = ref(getCurrentAccountName(false));

const visibleModal=ref(false)
const radiovalue=ref(1)

const digestLanMuData = {
  'accId': database.value.substring(0, database.value.length - 5),
  'menuName': '明细账摘要',
  'type': '',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username
}
// 增加操作日志：1、修改2、删除3、停用、4查看、5导入、6导出、7打印
const logmap = ref({
  accId: database.value,
  username: useUserStoreWidthOut().getUserInfo.username,
  flag: '4',
  text: '',
});
const CrudApi = {
  list: findAll,
  columns: JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark)))
}

// 组件实例区
const [registerTable, {
  reload,
  setTableData,
  setColumns,
  getColumns,
  getPaginationRef,
  setPagination,getDataSource
}] = useTable({
  api: useRouteApi(CrudApi.list, {schemaName: database}),
  columns: CrudApi.columns,
  bordered: true,
  loading: loadMark.value,
  immediate: false,
  canResize: true,
  showIndexColumn: true, //显示序号列
  pagination: {
    pageSize: 1000,
    simple:true
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
const route = useRoute();
const routemsg = ref(route.query);
const [registerQueryPage, {openModal: openQueryPageM}] = useModal()
const [registerReviewPage, {openModal: openReviewPageM}] = useModal()
onMounted(async () => {
  if (JSON.stringify(routemsg.value).length === 2) {
    val.openOne = 1
    openQueryPageM(true, {
      data: val,
      openType:'mx'
    })
    loadMark.value = true
  } else {
    pageParameter.ishaveRjz = routemsg.value.ishaveRjz
    pageParameter.bend = routemsg.value.jc
    pageParameter.strDate = routemsg.value.strDate
    pageParameter.endDate = routemsg.value.endDate
    pageParameter.maxJc = routemsg.value.maxJc
    pageParameter.minJc = routemsg.value.minJc
    pageParameter.km = routemsg.value.km
    pageParameter.timflg = 'qj'
    pageParameter.pzType = ''
    pageParameter.bz = routemsg.value.bzName
    pageParameter.database = routemsg.value.database
    database.value=routemsg.value.database
    // 用于显示
    time.strDate = routemsg.value.strDate
    time.endDate = routemsg.value.endDate
    resetDynamicColumnData()
  }
})

const showPageNumber=ref(false)
const pageNumber=ref(0)
function fetchSuccess(data) {
  showPageNumber.value=true
  pageNumber.value=data.items.length
  calculateTotal(data.items)
  if(data.items.length<50){
    for (let i =  data.items.length; i < 50; i++) {
      data.items.push({})
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
  let yue = 0
  let ndS = 0
  let ncS = 0
  let yue_num = 0
  let nfrat_md = 0
  let nfrat_mc = 0
  let yue_nfrat = 0
  for (let i = 0; i < list.length; i++) {
    const e = list[i];
    md += parseFloat(e.md || '0')
    mc += parseFloat(e.mc || '0')
    yue += parseFloat(e.yue || '0')
    ndS += parseFloat(e.ndS || '0')
    ncS += parseFloat(e.ncS || '0')
    yue_num += parseFloat(e.yue_num || '0')
    nfrat_md += parseFloat(e.nfrat_md || '0')
    nfrat_mc += parseFloat(e.nfrat_mc || '0')
    yue_nfrat += parseFloat(e.yue_nfrat || '0')
  }
  summaryTotals.value={
    md: toThousandFilter(md),
    mc: toThousandFilter(mc),
    yue: toThousandFilter(yue),
    ndS: toThousandFilter(ndS),
    ncS: toThousandFilter(ncS),
    yue_num: toThousandFilter(yue_num),
    nfrat_md: toThousandFilter(nfrat_md),
    yue_nfrat: toThousandFilter(yue_nfrat),
  }
}
const dynamicAdReload = async (obj) => {
  accNameAll.value = obj.companyName   // 账套全称
  bwb.value = obj.target.currencyName
  if (JSON.stringify(routemsg.value).length === 2) {
    // 先获取部门组件查看是否存在部门信息
    pageParameter.page = getPaginationRef().current
    pageParameter.size = getPaginationRef().pageSize
    loadMark.value = false
  }
}

function tableRow(row: any, num: any) {
  return (row.cdigest == '本月合计' ? 'table-month-striped' : null)
    || (row.cdigest == '本年累计' ? 'table-year-striped' : null)
    || (row.number % 2 == 0 ? 'table-odd-striped' : null)
}

// 选项在两栏之间转移时的回调函数
function transferChange(a, b) {
  targetKeys.value = a
}

// 选中项发生改变时的回调函数
function transferSelectChange(a, b) {
  selectedKeys.value = [...a, ...b]
}

const transferOk = () => {
  delDigestLanMuList(digestLanMuData)
  let arr = [
    {
      check: 'true',
      pKey: '',
      key: targetKeys.value.join(','),
      name: '',
      nameNew: '',
      width: '',
      align: ''
    }]
  // 调整数据库 列参数
  digestLanMuData.objects = JSON.stringify(arr)
  saveLanMuList(digestLanMuData).then(res => {
    message.success("数据库同步成功！")
    resetDynamicColumnData()
  })
}
// 取消
const transferCancel = () => {
  targetKeys.value = []
  selectedKeys.value = []
}

function digestDynamicColumnData() {
  findDbLanMuList(digestLanMuData).then(res => {
    // 栏目列
    let dbList = res.items
    let arr = []
    if (dbList.length > 0) {
      for (let i = 0; i < dbList[0].key.split(',').length; i++) {
        arr.push(dbList[0].key.split(',')[i])
      }
    }
    targetKeys.value = arr
  })
}

const openQueryPage = () => {
  val.openOne = 0
  openQueryPageM(true, {
    data: val,
    openType:'mx'
  })
}

// 页面函数区
const onSelectChange = (selectedRowKeys, obj) => {
  tableSelectedRowKeys.value = selectedRowKeys;
  tableSelectedRowObjs.value = obj;
};
const pageReload = () => {
  pageParameter.digestStyle = targetKeys.value.length > 0 ? targetKeys.value.join(',') : ''
  reload({
    searchInfo: pageParameter
  })
}

const onChangeSwitch = async (v) => { // 动态列
  pageParameter.queryMark = v
  styleName.value = showStyle.value.filter(o => o.value === v)[0].name
  resetDynamicColumnData()
  if (pageMode.value == '0') return false
  pageParameter.searchConditon.requirement = 'bprogerty'
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
  /* if (''==pageParameter.searchConditon.value.trim())return false*/
  // 搜索前校验格式
  let min = pageParameter.filterConditon.amountMinJf.trim()
  let max = pageParameter.filterConditon.amountMaxJf.trim()
  if (max != '' || min != '') {
    if (min != '' && max != '' && (!isRealNum(min) || !isRealNum(max) || parseFloat(min) == 0 || parseFloat(max) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(min) > parseFloat(max)) {
      message.warn('金额区间最小值部门大于最大值！')
      closeFilterV()
      return false
    }
  }

  let min2 = pageParameter.filterConditon.amountMinDf.trim()
  let max2 = pageParameter.filterConditon.amountMaxDf.trim()
  if (max2 != '' || min2 != '') {
    if (min2 != '' && max2 != '' && (!isRealNum(min2) || !isRealNum(max2) || parseFloat(min2) == 0 || parseFloat(max2) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(min2) > parseFloat(max2)) {
      message.warn('金额区间最小值部门大于最大值！')
      closeFilterV()
      return false
    }
  }

  let min3 = pageParameter.filterConditon.amountMinYe.trim()
  let max3 = pageParameter.filterConditon.amountMaxYe.trim()
  if (max != '' || min3 != '') {
    if (min3 != '' && max3 != '' && (!isRealNum(min3) || !isRealNum(max3) || parseFloat(min3) == 0 || parseFloat(max3) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(min3) > parseFloat(max3)) {
      message.warn('金额区间最小值部门大于最大值！')
      closeFilterV()
      return false
    }
  }

  // 校验完成后搜索
  pageReload()
}

const closeFilterV = () => {
  pageParameter.filterConditon.amountMinDf = ''
  pageParameter.filterConditon.amountMaxDf = ''
  pageParameter.filterConditon.amountMinJf = ''
  pageParameter.filterConditon.amountMaxJf = ''
  pageParameter.filterConditon.amountMinYe = ''
  pageParameter.filterConditon.amountMaxYe = ''
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
      lanMuData.accId =
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

function resetDynamicColumnData() {
  // 先从数据查询是否已经设置
  lanMuData.accId = database.value.substring(0, database.value.length - 5)
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
  let newA = JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark)))
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
    // tableRef.value.$el.style.setProperty('width', (windowWidth + 20 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    // tableRef.value.$el.style.setProperty('width', (total + 20) + 'px')
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
/*集团查询页面 页面参数*/
const pageMode = ref('1')
const queryAccIds = ref([])
const accAuthList = ref([])
const accNameAll = ref('')

const loadPage = async (map) => {
  let data = map.data
  accAuthList.value = map.accAuthList
  pageMode.value = data.queryMode

  // 回显筛选条件
  loadMark.value = false
  // 参数规则
  let ParameteRule = await useRouteApi(finByParameterAccuracy, {schemaName: database.value})('')
  if (ParameteRule.length > 0) {
    for (let i = 0; i < ParameteRule.length; i++) {
      if (ParameteRule[i].paraName === '汇率') {
        pageParameter.lvJd = ParameteRule[i].decimalPlaces
      }
      if (ParameteRule[i].paraName === '单价') {
        pageParameter.priceJd = ParameteRule[i].decimalPlaces
      }
      if (ParameteRule[i].paraName === '数量') {
        pageParameter.numJd = ParameteRule[i].decimalPlaces
      }
      if (ParameteRule[i].paraName === '金额') {
        pageParameter.moneyJd = ParameteRule[i].decimalPlaces
      }
    }
  }
  kmList.value = data.kmList
  pageParameter.strDate = data.strDate.replaceAll("-", "")
  pageParameter.endDate = data.endDate.replaceAll("-", "")
  pageParameter.showRulesSize = data.fontSize
  bzName.value = data.bzName
  pageParameter.ishaveRjz = data.ishaveRjz
  pageParameter.minJc = data.minJc
  pageParameter.maxJc = data.maxJc
  pageParameter.bend = data.jc
  pageParameter.bz = data.bzName
  pageParameter.styleValue = data.styleValue
  pageParameter.timflg = data.timflg
  pageParameter.kmList = data.kmList
  pageParameter.pzType = data.pzType
  time.strDate = data.strDate.replaceAll("-", ".")
  time.endDate = data.endDate.replaceAll("-", ".")

  if (data.jc === '1') {
    pageParameter.km = data.minKm === undefined ? data.kmList[0].ccode : data.minKm; // 起始科目
  } else {
    pageParameter.km = data.kmList[0].ccode;
  }

  let findBuccodeName = data.kmList.filter(c => c.ccode === pageParameter.km)[0].ccodeName
  titleName.value = findBuccodeName
  if (pageMode.value == '1') {
  //   pageParameter.searchConditon.value = ''
  //   resetDynamicColumnData()
  //   closeFilterV()
  } else {
    queryAccIds.value = data.accIds
    // 字页面
    if (data.openOne == 1) {
      startRender.value = true
      pageParameter.reloadMark = false
    } else {
      startRender.value = false
      pageParameter.reloadMark = true
      startRender.value = true
    }
    resetDynamicColumnData()
  }
  pageReload()
  resetDynamicColumnData()
}

async function handleChangeMinKm() {
  titleName.value = kmList.value.filter(o => o.ccode === pageParameter.km)[0].ccodeName
  const dw = kmList.value.filter(o => o.ccode === pageParameter.km)[0].menterage // 单位
  const wb = kmList.value.filter(o => o.ccode === pageParameter.km)[0].currencyType //美元
  pageParameter.dw = dw
  pageParameter.wb = wb

  const test = kmList.value.filter((km) => km.ccode === pageParameter.km);
  if (test.length > 0) {
    if (test.bnum === '1' && test.currency === '1') {
      pageParameter.queryMark = 'SWJ';
    } else if (test.bnum === '1') {
      pageParameter.queryMark = 'SJ';
    } else if (test.currency === '1') {
      pageParameter.queryMark = 'WJ';
    } else {
      pageParameter.queryMark = 'J'
    }
  }
  styleName.value = showStyle.value.filter(o => o.value === pageParameter.queryMark)[0].name
  closeFilterV()
  pageReload()
}

// 金额格式化
function moneyformat(data: any) {
  let str = ""
  if (data) {
    // 千分位保留2位小数
    var source = String(data.toFixed(pageParameter.moneyJd)).split("."); //按小数点分成2部分
    source[0] = source[0].replace(new RegExp('(\\d)(?=(\\d{3})+$)', 'ig'), "$1,");//只将整数部分进行都好分割
    str = source.join(".");//再将小数部分合并进来
  }
  return str;
}

// 数量格式化
function numformat(data: any) {
  let str = ""
  if (data) {
    if (0 === data) {
      str = ""
    } else {
      var source = String(data.toFixed(pageParameter.numJd)).split("."); //按小数点分成2部分
      source[0] = source[0].replace(new RegExp('(\\d)(?=(\\d{3})+$)', 'ig'), "$1,");//只将整数部分进行都好分割
      str = source.join(".");
    }
  }
  return str;
}

const showCurrentName = (v) => {
  let arr = accAuthList.value.filter(item => item.accId == v)
  if (arr.length > 0) {
    return arr[0].accNameCn
  }
  return ''
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
// 导出当前
function thisPageImport(){
  // 当前页： getPaginationRef().current
  // 当前条： getPaginationRef().pageSize
  const data = JSON.parse(JSON.stringify(getDataSource()))
  const data2:any=data.splice(0,getPaginationRef().pageSize)
  let columns:any = getColumns().slice(3)
  //根据columns 格式化导出excel数据
  let name = accNameAll.value
  const title = ['科目：'+pageParameter.km+'-'+titleName.value+'              期间：'+ time.strDate +' - '+ time.endDate +'         单位：元']
  if(pageParameter.queryMark === 'J'){
    for (let i = 0; i < 6; i++) {
      title.push('')
    }
  }else if(pageParameter.queryMark === 'SJ'){
    for (let i = 0; i < 10; i++) {
      title.push('')
    }
  }else if(pageParameter.queryMark === 'WJ'){
    for (let i = 0; i < 10; i++) {
      title.push('')
    }
  }else if(pageParameter.queryMark === 'SWJ'){
    for (let i = 0; i < 14; i++) {
      title.push('')
    }
  }
  const multiHeader:any = [[],[]]
  const keys:any = []
  const merges:any = []
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
      },
    }
  ]
  const cell = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  let flg = 0;
  columns.forEach((v,index)=>{
    //多级表头
    if(v.children){
      multiHeader[0].push(v.title)
      for (let i = 0; i < v.children.length; i++) {
        if(i < v.children.length-1){
          multiHeader[0].push('')
        }
        multiHeader[1].push(v.children[i].title)
        keys.push(v.children[i].dataIndex)
      }
      //根据子节点数量合并单元格 5 56  6 78 7 9 10
      if(flg === 0){
        flg = index
      }
      merges.push('`' + cell[flg] + '3:' + cell[flg + v.children.length-1] +'3`');
      flg = flg + v.children.length
    }else{
      multiHeader[0].push(v.title)
      multiHeader[1].push('')
      keys.push(v.key)
      //合并上下单元格
      merges.push('`' + cell[flg] + '3:' + cell[flg] +'4`');
      flg = flg + 1
    }
  })
  merges.push('`A2:' + cell[keys.length-1] +'2`');
  data2.forEach(v=>{
    v.ccode=v.ccode===null?'':v.ccode
    v.ccodeName=v.ccodeName===null?'':v.ccodeName
    v.dbillDate=v.dbillDate===null?'':v.dbillDate
    v.foreignCurrency=v.foreignCurrency===null?'':v.foreignCurrency
    v.unitMeasurement=v.unitMeasurement===null?'':v.unitMeasurement
    v.iyperiod=v.iyperiod===null?'':v.iyperiod
    v.inoId=v.inoId===null?'':v.inoId
    v.ljmc=v.ljmc===null?'':moneyformat(v.ljmc)
    v.ljmd=v.ljmd===null?'':moneyformat(v.ljmd)
    v.mc=v.mc===null?'':moneyformat(v.mc)
    v.md=v.md===null?'':moneyformat(v.md)
    v.mdF=v.mdF===null?'':moneyformat(v.mdF)
    v.ncS=v.ncS===null?'':moneyformat(v.ncS)
    v.ncnum=v.ncnum===null?'':numformat(v.ncnum)
    v.ndS=v.ndS===null?'':numformat(v.ndS)
    v.nfrat_mc=v.nfrat_mc===null?'':moneyformat(v.nfrat_mc)
    v.nfrat_md=v.nfrat_md===null?'':moneyformat(v.nfrat_md)
    v.yue=v.yue===null?'':moneyformat(v.yue)
    v.yue_nfrat=v.yue_nfrat===null?'':moneyformat(v.yue_nfrat)
    v.yue_num=v.yue_num===null?'':numformat(v.yue_num)
  })
  //样式靠右列
  let rightrow:any=[]
  //样式靠左列
  let leftrow:any=['A','B','C','D','E']
  if(pageParameter.queryMark === 'J'){
    rightrow=['F','G','I']
  }else if(pageParameter.queryMark === 'SJ'){
    rightrow=['E','I','F','H','K','L']
  }else if(pageParameter.queryMark === 'WJ'){
    rightrow=['F','H','K','E','J','G','I','J','L']
  }else{
    rightrow=['G','F','H','K','O','J','N','L','P']
  }
  const sheet =[
    {
      title: titleName.value+'科目明细账',
      tHeader: title,
      multiHeader: multiHeader,
      table: data2,
      keys: keys,
      merges: merges,
      sheetName: 'sheet1',
      cellStyle: cellStyle,
      rightrow:rightrow,
      leftrow:leftrow,
    },
  ]
  exportExcel(sheet, 'xlsx','科目明细账_'+time.strDate+'-'+time.endDate+'_'+accNameAll.value)
}
// 导出全部
function allPageImport(){
  visibleModal.value=true
}
// 导出全部-导出
async function visibleOK() {
  // 多科目页签
  if(radiovalue.value===1){
    await companyExportView()
  }
  // 单页签
  else{
   await onlySheetExport()
  }
}
async function onlySheetExport() {
  openCompFullLoading()
  let code=kmList.value.map(v=>v.ccode)
  let map={ code:code.join(','),pageParameter: pageParameter}
  await useRouteApi(findByCodeExportExcel2, {schemaName: database})(map)
  .then(item=>{
    const data = item.list
    let columns:any = getColumns().slice(1)
    // 根据columns 格式化导出excel数据
    let name = accNameAll.value
    const title = ['                                                                             期间：'+ time.strDate +' - '+ time.endDate +'                                        单位：元']
    if(pageParameter.queryMark === 'J'){
      for (let i = 0; i < 6; i++) {
        title.push('')
      }
    }else if(pageParameter.queryMark === 'SJ'){
      for (let i = 0; i < 10; i++) {
        title.push('')
      }
    }else if(pageParameter.queryMark === 'WJ'){
      for (let i = 0; i < 10; i++) {
        title.push('')
      }
    }else if(pageParameter.queryMark === 'SWJ'){
      for (let i = 0; i < 14; i++) {
        title.push('')
      }
    }
    const multiHeader:any = [[],[]]
    const keys:any = []
    const merges:any = []
    const cell = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
    let flg = 0;
    columns.forEach((v,index)=>{
      //多级表头
      if(v.children){
        multiHeader[0].push(v.title)
        for (let i = 0; i < v.children.length; i++) {
          if(i < v.children.length-1){
            multiHeader[0].push('')
          }
          multiHeader[1].push(v.children[i].title)
          keys.push(v.children[i].dataIndex)
        }
        //根据子节点数量合并单元格 5 56  6 78 7 9 10
        if(flg === 0){
          flg = index
        }
        merges.push('`' + cell[flg] + '1:' + cell[flg + v.children.length-1] +'1`');
        flg = flg + v.children.length
      }else{
        multiHeader[0].push(v.title)
        multiHeader[1].push('')
        keys.push(v.key)
        //合并上下单元格
        merges.push('`' + cell[flg] + '1:' + cell[flg] +'2`');
        flg = flg + 1
      }
    })
    //merges.push('`A2:' + cell[keys.length-1] +'2`');
    data.forEach((v,index)=>{
      v.ccode=v.ccode===null?'':v.ccode
      v.ccodeName=v.ccodeName===null?'':v.ccodeName
      v.number=v.number===null?index+1:index+1
      v.dbillDate=v.dbillDate===null?'':v.dbillDate
      v.foreignCurrency=v.foreignCurrency===null?'':v.foreignCurrency
      v.iyperiod=v.iyperiod===null?'':v.iyperiod
      v.inoId=v.inoId===null?'':v.inoId
      v.ljmc=v.ljmc===null?'':moneyformat(v.ljmc)
      v.ljmd=v.ljmd===null?'':moneyformat(v.ljmd)
      v.mc=v.mc===null?'':moneyformat(v.mc)
      v.md=v.md===null?'':moneyformat(v.md)
      v.mdF=v.mdF===null?'':moneyformat(v.mdF)
      v.ncS=v.ncS===null?'':moneyformat(v.ncS)
      v.ncnum=v.ncnum===null?'':numformat(v.ncnum)
      v.ndS=v.ndS===null?'':numformat(v.ndS)
      v.nfrat_mc=v.nfrat_mc===null?'':moneyformat(v.nfrat_mc)
      v.nfrat_md=v.nfrat_md===null?'':moneyformat(v.nfrat_md)
      v.yue=v.yue===null?'':moneyformat(v.yue)
      v.yue_nfrat=v.yue_nfrat===null?'':moneyformat(v.yue_nfrat)
      v.yue_num=v.yue_num===null?'':numformat(v.yue_num)
    })
    //样式靠右列
    let rightrow:any=[]
    //样式靠左列
    let leftrow:any=['A','B','C','D','E']
    if(pageParameter.queryMark === 'J'){
      rightrow=['F','G','I']
    }else if(pageParameter.queryMark === 'SJ'){
      rightrow=['E','I','F','H','K','L']
    }else if(pageParameter.queryMark === 'WJ'){
      rightrow=['F','H','K','E','J','G','I','J','L']
    }else{
      rightrow=['G','F','H','K','O','J','N','L','P']
    }
    const sheet =[
      {
        title: [],
        tHeader: [],
        multiHeader: multiHeader,
        table: data,
        keys: keys,
        merges: merges,
        sheetName: 'sheet1',
        cellStyle: [],
        rightrow:rightrow,
        leftrow:leftrow,
      },
    ]
    exportExcel2(sheet, 'xlsx','科目明细账_'+time.strDate+'-'+time.endDate+'_'+accNameAll.value)
    compState.loading = false;
  })
}

const exportExcelBtn = () => {
  if(pageMode.value==='1'){
    // companyExportView()
  }else{
    jtImportExcelView()
  }
}
async function companyExportView() {
  openCompFullLoading()
  //根据columns 格式化导出excel数据
  const multiHeader: any = [[], []]
  const keys: any = []
  const merges: any = []
  const cellStyle = [
    {
      cell: 'A1',
      font: {
        sz: 16,
        color: {rgb: "000000"},
        bold: true,
      },
      fill: {
        fgColor: {rgb: "ffffff"},
      }
    }
  ]
  const cell = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']
  let flg = 0;
  const columns:any = getColumns().slice(1)
  columns.forEach((v, index) => {
    //多级表头
    if (v.children) {
      multiHeader[0].push(v.title)
      for (let i = 0; i < v.children.length; i++) {
        if (i < v.children.length - 1) {
          multiHeader[0].push('')
        }
        multiHeader[1].push(v.children[i].title)
        keys.push(v.children[i].dataIndex)
      }
      //根据子节点数量合并单元格 5 56  6 78 7 9 10
      if (flg === 0) {
        flg = index
      }
      merges.push('`' + cell[flg] + '3:' + cell[flg + v.children.length - 1] + '3`');
      flg = flg + v.children.length
    } else {
      multiHeader[0].push(v.title)
      multiHeader[1].push('')
      keys.push(v.key)
      //合并上下单元格
      merges.push('`' + cell[flg] + '3:' + cell[flg] + '4`');
      flg = flg + 1
    }
  })
  merges.push('`A2:' + cell[keys.length - 1] + '2`');

  var sheet: any = []
  //样式靠右列
  let rightrow:any=[]
  //样式靠左列
  let leftrow:any=['A','B','C','D','E']
  if(pageParameter.queryMark === 'J'){
    rightrow=['F','G','I']
  }else if(pageParameter.queryMark === 'SJ'){
    rightrow=['E','I','F','H','K','L']
  }else if(pageParameter.queryMark === 'WJ'){
    rightrow=['F','H','K','E','J','G','I','J','L']
  }else{
    rightrow=['G','F','H','K','O','J','N','L','P']
  }

  let code=kmList.value.map(v=>v.ccode)
  let map={ code:code.join(','),pageParameter: pageParameter}
  await useRouteApi(findByCodeExportExcel, {schemaName: database})(map)
  .then(item=>{
    let list=item.list
    for (var key in list) {
      let ccode=list[key].ccode
      let ccodeName=list[key].ccodeName
      const mxlist=list[key].mxlist
      const title = ['                                                期间：'+ time.strDate +' - '+ time.endDate +'                           单位：元']
      if(pageParameter.queryMark === 'J'){
        for (let i = 0; i < 8; i++) {
          title.push('')
        }
      }else if(pageParameter.queryMark === 'SJ'){
        for (let i = 0; i < 12; i++) {
          title.push('')
        }
      }else if(pageParameter.queryMark === 'WJ'){
        for (let i = 0; i < 12; i++) {
          title.push('')
        }
      }else if(pageParameter.queryMark === 'SWJ'){
        for (let i = 0; i < 16; i++) {
          title.push('')
        }
      }
      mxlist.forEach(v=>{
        v.ccode=v.ccode===null?'':v.ccode
        v.ccodeName=v.ccodeName===null?'':v.ccodeName
        v.dbillDate=v.dbillDate===null?'':v.dbillDate
        v.foreignCurrency=v.foreignCurrency===null?'':v.foreignCurrency
        v.unitMeasurement=v.unitMeasurement===null?'':v.unitMeasurement
        v.iyperiod=v.iyperiod===null?'':v.iyperiod
        v.inoId=v.inoId===null?'':v.inoId
        v.ljmc=v.ljmc===null?'':moneyformat(v.ljmc)
        v.ljmd=v.ljmd===null?'':moneyformat(v.ljmd)
        v.mc=v.mc===null?'':moneyformat(v.mc)
        v.md=v.md===null?'':moneyformat(v.md)
        v.mdF=v.mdF===null?'':moneyformat(v.mdF)
        v.ncS=v.ncS===null?'':moneyformat(v.ncS)
        v.ncnum=v.ncnum===null?'':numformat(v.ncnum)
        v.ndS=v.ndS===null?'':numformat(v.ndS)
        v.nfrat_mc=v.nfrat_mc===null?'':moneyformat(v.nfrat_mc)
        v.nfrat_md=v.nfrat_md===null?'':moneyformat(v.nfrat_md)
        v.yue=v.yue===null?'':moneyformat(v.yue)
        v.yue_nfrat=v.yue_nfrat===null?'':moneyformat(v.yue_nfrat)
        v.yue_num=v.yue_num===null?'':numformat(v.yue_num)
      })
      sheet.push(
        {
          title: ccodeName+'科目明细账',
          tHeader: title,
          multiHeader: multiHeader,
          table: mxlist,
          keys: keys,
          merges: merges,
          sheetName: ccode+'-'+ccodeName,
          cellStyle: cellStyle,
          rightrow:rightrow,
          leftrow:leftrow,
        },
      )
    }
    compState.loading = false
  })
  exportExcel(sheet, 'xlsx','科目明细账_'+time.strDate+'-'+time.endDate+'_'+accNameAll.value)
}
const jtImportExcelView = () => {
  let data:any=exportData.value['map']
  //根据columns 格式化导出excel数据
  const multiHeader:any = [[],[]]
  const keys:any = []
  const merges:any = []
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
    }
  ]
  const cell = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  let flg = 0;
  const columns=getColumns().slice(1)
  columns.forEach((v,index)=>{
    //多级表头
    if(v.children){
      multiHeader[0].push(v.title)
      for (let i = 0; i < v.children.length; i++) {
        if(i < v.children.length-1){
          multiHeader[0].push('')
        }
        multiHeader[1].push(v.children[i].title)
        keys.push(v.children[i].dataIndex)
      }
      //根据子节点数量合并单元格 5 56  6 78 7 9 10
      if(flg === 0){
        flg = index
      }
      merges.push('`' + cell[flg] + '3:' + cell[flg + v.children.length-1] +'3`');
      flg = flg + v.children.length
    }else{
      multiHeader[0].push(v.title)
      multiHeader[1].push('')
      keys.push(v.key)
      //合并上下单元格
      merges.push('`' + cell[flg] + '3:' + cell[flg] +'4`');
      flg = flg + 1
    }
  })
  merges.push('`A2:' + cell[keys.length-1] +'2`');

  var sheet:any=[]
  for (var key in data) {
    //筛选条件
    const title = [ "公司名称："+key+'   日期：'+time.strDate+' - '+time.strDate ]
    let tabledata=JSON.parse(JSON.stringify(data[key]))
    tabledata.forEach(v=>{
      v.ccode=v.ccode===null?'':v.ccode
      v.ccodeName=v.ccodeName===null?'':v.ccodeName
      v.dbillDate=v.dbillDate===null?'':v.dbillDate
      v.foreignCurrency=v.foreignCurrency===null?'':v.foreignCurrency
      v.unitMeasurement=v.unitMeasurement===null?'':v.unitMeasurement
      v.iyperiod=v.iyperiod===null?'':v.iyperiod
      v.inoId=v.inoId===null?'':v.inoId
      v.ljmc=v.ljmc===null?'':moneyformat(v.ljmc)
      v.ljmd=v.ljmd===null?'':moneyformat(v.ljmd)
      v.mc=v.mc===null?'':moneyformat(v.mc)
      v.md=v.md===null?'':moneyformat(v.md)
      v.mdF=v.mdF===null?'':moneyformat(v.mdF)
      v.ncS=v.ncS===null?'':moneyformat(v.ncS)
      v.ncnum=v.ncnum===null?'':numformat(v.ncnum)
      v.ndS=v.ndS===null?'':numformat(v.ndS)
      v.nfrat_mc=v.nfrat_mc===null?'':moneyformat(v.nfrat_mc)
      v.nfrat_md=v.nfrat_md===null?'':moneyformat(v.nfrat_md)
      v.yue=v.yue===null?'':moneyformat(v.yue)
      v.yue_nfrat=v.yue_nfrat===null?'':moneyformat(v.yue_nfrat)
      v.yue_num=v.yue_num===null?'':numformat(v.yue_num)
    })

    //样式靠右列
    let rightrow:any=[]
    //样式靠左列
    let leftrow:any=['D']
    if(pageParameter.queryMark === 'J'){
      rightrow=['E','F','H']
    }else if(pageParameter.queryMark === 'SJ'){
      rightrow=['G','I','L']
    }else if(pageParameter.queryMark === 'WJ'){
      rightrow=['F','G','H','I','K','L']
    }else{
      rightrow=['H','I','K','L','O','P']
    }
    sheet.push(
      {
        title: titleName.value+'科目明细账',
        tHeader: title,
        multiHeader: multiHeader,
        table: tabledata,
        keys: keys,
        merges: merges,
        sheetName: key+'-科目明细账',
        cellStyle: cellStyle,
        rightrow:rightrow,
        leftrow:leftrow,
      },
    )
  }
  exportExcel(sheet, 'xlsx','科目明细账_'+time.strDate+'-'+time.endDate)
}

// 打印当前页
const [registerPrintPage, {openModal: openPrintPage}] = useModal()
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
const loadPrint = (obj) => {
  openCompFullLoading()
  const data = JSON.parse(JSON.stringify(getDataSource()))
  let printUser = ''
  if(obj.printUser){
    printUser = '制表人：' + userName
  }
  let printBz = ''
  if(obj.printBz){
    printBz = '币种：人民币'
  }
  let printList: any = []
  data.forEach((item,index) => {
    let item1 = {}
    item1[0] = item.dbillDate==null?'':item.dbillDate
    item1[1] = item.inoId==null?'':item.inoId
    item1[2] = setString(item.cdigest,27)
    item1[3] = item.md==null?'':item.md==0?'':moneyformat(item.md)
    item1[4] = item.mc==null?'':item.mc==0?'':moneyformat(item.mc)
    item1[5] = item.bprogerty
    item1[6] = moneyformat(item.yue)
    printList.push(item1)
  })
  for (let i=0; i<printList.length%27; i++){
    let item1 = {}
    item1[0] = ''
    item1[1] = ''
    item1[2] = ''
    item1[3] = ''
    item1[4] = ''
    item1[5] = ''
    item1[6] = ''
    printList.push(item1)
  }

  let num = Math.ceil(printList.length/27)
  useNewPrint({data: ['l', 'px', 'a4', true]}, (doc) => {
    loadMark.value = false
    compState.loading = false
    doc.autoTable({
      head: [['', '', titleName.value+'科目明细表', '', '', '', ''],
        ['会计科目：' + pageParameter.km+'-'+titleName.value,'', '期间:' + time.strDate+' - '+time.endDate,'',printBz,'','单位：元'],
        ['期间', '凭证号', '摘要', '借方', '贷方', '方向', '余额']],
      body: printList,
      // startY: 60,
      styles: tableStyle(),
      margin: {
        left: 60,
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
        doc.autoTableText(
          '核算单位：' + accNameAll.value,
          tabMarginLeft,
          data.cursor.y + 3,
          0
        );
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
            data.cell.colSpan = 4
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
            data.cell.styles.cellPadding = {top: 3, left: 2, right: 20, bottom: 2}
            data.cell.colSpan = 2
            data.cell.styles.halign = 'right'
          }
          else if (data.column.index == 4) {
            data.cell.colSpan = 2
            data.cell.styles.halign = 'right'
          } else {
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
        1: {cellWidth: 50, halign: 'center'},
        2: {cellWidth: 150, halign: 'left'},
        3: {cellWidth: 80, halign: 'right'},
        4: {cellWidth: 80, halign: 'right'},
        5: {cellWidth: 30, halign: 'center'},
        6: {cellWidth: 80, halign: 'right'},
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
  padding: 10px 5px;
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

