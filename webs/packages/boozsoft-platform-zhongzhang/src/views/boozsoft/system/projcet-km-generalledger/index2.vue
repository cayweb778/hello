<template>
  <div class="app-container">
    <div class="app-container-top">
      <div class="app-container-head">
        <div class="container-head-title" style="float: none;text-align: center;display: revert;">
          <b class="noneSpan" >{{ titleName }}总账</b>
        </div>
        <div style="margin-top: .5%;">
          <div style="display: inline-block;margin-left: 1%;font-size: 14px;width: 44%">
            <div style="padding-top: 20px">
              <AccountPicker theme="three" readonly="" @reloadTable="dynamicAdReload"/>
            </div>
          </div>
          <div style="display: inline-block;font-size: 14px;text-align: center">
            &emsp;&emsp;<span style="font-size: 14px;color: #aaa">期间：</span><span style="color: black;font-weight: bold">{{ pageParameter.strDate }}~{{ pageParameter.endDate }}</span>
          </div>
          <div class="ant-btn-group">
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
              @click="openQueryPage()"
            ><span>查询</span></button>
            <a-popover placement="bottom">
              <template #content>
                <span class="group-btn-span-special" @click="exportExcelNow()">导出当前</span><br/>
                <span class="group-btn-span-special" @click="allPageImport">导出全部</span>
              </template>
              <button
                type="button"
                class="ant-btn ant-btn-me"
                ant-click-animating-without-extra-node="false"
              ><span>导出</span></button></a-popover>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
              @click="openPrint()"
            ><span>打印</span></button>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
              @click="closeCurrent(),router.push('/zhongZhang/home/welcome')"
            ><span>退出</span></button>
          </div>
        </div>
        <div style="clear:both" />
        <div style="margin-top: .5%;">
          <div style="display: inline-block;margin-left: 1%;font-size: 14px;width: 30%">
            <div style="display: inline-block;font-size: 14px;padding-top: 8px">
              <span style="font-size: 14px;color: #aaa;font-weight: bold">科目：</span>
              <a-select
                show-search
                placeholder="科目选择"
                option-filter-prop="children"
                style="width: 180px;font-size: 14px;"
                :filter-option="filterOption"
                @change="handleChangeMinKm"
                v-model:value="pageParameter.km"
              >
                <a-select-option   style="width: 180px;font-size: 14px;"  v-for="d in kmList" :value="d.ccode">
                  {{ d.value }}
                </a-select-option>
              </a-select>
            </div>
            &emsp;&emsp;<span style="font-size: 14px;color: #aaa">币种：</span><span style="font-size: 14px;font-weight: bold">{{ bzName }} </span>
            &emsp;&emsp;<span style="font-size: 14px;color: #aaa">样式：</span><span style="color: black;font-weight: bold">{{ styleName }}</span>
          </div>

          <div style="float: right; margin-left: 10px;margin-top: .5%;">
            <a-button class="ant-btn" @click="closeFilterV(),pageReload()">
              <SyncOutlined :style="{ fontSize: '14px' }" />
            </a-button>

            <a-popover placement="bottom">
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
                                            || record.name=='借方' || record.name=='贷方' || record.name=='余额'
                                            || record.name=='借方本币' || record.name=='贷方本币' || record.name=='借方原币'|| record.name=='贷方原币'">
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
                        :style="{backgroundColor: (pageParameter.queryMark==='J')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;
                    <OrderedListOutlined
                      :style="{ fontSize: '14px' }"/>&emsp;&emsp;金额式&nbsp;<CheckOutlined v-if="pageParameter.queryMark==='J'"
                                                                                         :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
                <span @click="onChangeSwitch('WJ')"
                      :style="{backgroundColor: (pageParameter.queryMark==='WJ')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;
                    <OrderedListOutlined
                      :style="{ fontSize: '14px' }"/>&emsp;外币金额式&nbsp;<CheckOutlined v-if="pageParameter.queryMark==='WJ'"
                                                                                     :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
              </template>
              <a-button>
                <PicLeftOutlined :style="{ fontSize: '14px' }" />
              </a-button>
            </a-popover>
            <a-button @click="()=>{
                if (!visible){ visible = true;reloadColumns()}
                return false
              }">
              <FilterFilled :style="{ fontSize: '14px' }" />
            </a-button>
          </div>
          <div style="float: right; position: relative;margin-top: .5%;">
            <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 130px;" class="special_select">
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
              style="width: 150px;border-radius: 4px"
            />
          </div>
        </div>
      </div>
    </div>
    <div  style="padding-top: 2px;background-color: rgba(255, 255, 255, 0.65) !important"/>
    <div class="app-container-bottom" >
      <BasicTable
        ref="tableRef"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        :scroll="{ x: totalColumnWidth,y: windowHeight }"
        size="small"
        @register="registerTable"
      >
        <template #qcJfnum="{ record }">
          <span >{{ formatNum(record.qcJfnum) }}</span>
        </template>
        <template #qcJfwb="{ record }">
          <span style="float: right">{{ formatData(record.qcJfwb) }}</span>
        </template>
        <template #qcJfMoney="{ record }">
          <span style="float: right" @click="goto()">{{ formatData(record.qcJfMoney) }}</span>
        </template>
        <template #qcDfnum="{ record }">
          <span >{{ formatNum(record.qcDfnum) }}</span>
        </template>
        <template #qcDfwb="{ record }">
          <span style="float: right" >{{ formatData(record.qcDfwb) }}</span>
        </template>
        <template #qcDfmoney="{ record }">
          <span style="float: right" @click="goto()">{{ formatData(record.qcDfmoney) }}</span>
        </template>

        <template #jfnum="{ record }">
          <span >{{ formatNum(record.jfnum) }}</span>
        </template>
        <template #jfwb="{ record }">
          <span style="float: right">{{ formatData(record.jfwb) }}</span>
        </template>
        <template #jfMoney="{ record }">
          <span style="float: right" @click="goto()">{{ formatData(record.jfMoney) }}</span>
        </template>
        <template #dfnum="{ record }">
          <span >{{ formatNum(record.dfnum) }}</span>
        </template>
        <template #dfwb="{ record }">
          <span style="float: right" >{{ formatData(record.dfwb) }}</span>
        </template>
        <template #dfmoney="{ record }">
          <span style="float: right" @click="goto()">{{ formatData(record.dfmoney) }}</span>
        </template>

        <template #ljJfnum="{ record }">
          <span >{{ formatNum(record.ljJfnum) }}</span>
        </template>
        <template #ljJfwb="{ record }">
          <span style="float: right">{{ formatData(record.ljJfwb) }}</span>
        </template>
        <template #ljJfMoney="{ record }">
          <span style="float: right" @click="goto()">{{ formatData(record.ljJfMoney) }}</span>
        </template>
        <template #ljDfnum="{ record }">
          <span >{{ formatNum(record.ljDfnum) }}</span>
        </template>
        <template #ljDfwb="{ record }">
          <span style="float: right">{{ formatData(record.ljDfwb) }}</span>
        </template>
        <template #ljDfmoney="{ record }">
          <span style="float: right" @click="goto()">{{ formatData(record.ljDfmoney) }}</span>
        </template>

        <template #qmJfnum="{ record }">
          <span >{{ formatNum(record.qmJfnum) }}</span>
        </template>
        <template #qmJfwb="{ record }">
          <span style="float: right">{{ formatData(record.qmJfwb) }}</span>
        </template>
        <template #qmJfMoney="{ record }">
          <span style="float: right" @click="goto()">{{ formatData(record.qmJfMoney) }}</span>
        </template>
        <template #qmDfnum="{ record }">
          <span >{{ formatNum(record.qmDfnum) }}</span>
        </template>
        <template #qmDfwb="{ record }">
          <span style="float: right">{{ formatData(record.qmDfwb) }}</span>
        </template>
        <template #qmDfmoney="{ record }">
          <span style="float: right" @click="goto()">{{ formatData(record.qmDfmoney) }}</span>
        </template>

      </BasicTable>
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
          余额区间：
            </span>
            <div>
              <a-input v-model:value="pageParameter.filterConditon.amountMin"  placeholder="" style="width: 95px;font-size: 10px"/>
              ~
              <a-input v-model:value="pageParameter.filterConditon.amountMax"  placeholder="" style="width: 95px;font-size: 10px;float: right"/>
            </div>
          </li>
          <li style="margin-top: 5%">

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
</template>
<script setup lang="ts">
import { BasicTable, useTable } from '/@/components/Table'
import { useModal } from '/@/components/Modal'
import { PageWrapper } from '/@/components/Page'

import {
  DatePicker as ADatePicker, Select as ASelect, Input as AInput, Popover as APopover, Switch as ASwitch,
  Radio as ARadio, Upload as AUpload, Pagination as APagination, Popconfirm as APopconfirm, Table as ATable,
  Checkbox as ACheckbox, message,Drawer as ADrawer
} from "ant-design-vue"
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";

const ARangePicker=ADatePicker.RangePicker
const ASelectOption=ASelect.Option
const AInputSearch=AInput.Search
const ARadioGroup=ARadio.Group
const ARadioButton=ARadio.Button
import {
  SortDescendingOutlined,
  SortAscendingOutlined,
  CaretDownFilled,
  FormOutlined,
  DeleteOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled,CheckOutlined,EditOutlined,SearchOutlined
} from '@ant-design/icons-vue'
import { breakNumTidy,findDbLanMuList,saveLanMuList } from '/@/api/record/system/accvoucher'
import { useKmProjectGlStore } from '/@/api/record/generalLedger/kmProjectGeneralLedger'

import {onMounted, reactive, ref} from "vue";
import moment from "moment";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import { initDynamics,assemblyDynamicColumn} from "./data";
import Query from "/@/views/boozsoft/system/projcet-km-generalledger/popup/query.vue";
import {cloneDeep} from "lodash-es";
import {saveVoucherData} from "/@/api/record/system/financial-settings";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  askTask,
  compareTime,
  findByFunctionModule,
  getCurrentAccountName,
  markAnomaly,
  offsetToStr
} from "/@/api/task-api/tast-bus-api";
import {
  findAllKmProject,
  findDeptsheetDate,
  findDeptsheetDateOne
} from "/@/api/record/generalLedger/data";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {finByParameterAccuracy} from "/@/api/record/km_mingxi/data";
import router from "/@/router";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
const { createConfirm,createWarningModal } = useMessage();
// 全局常量
const glStore = useKmProjectGlStore()

// 页面变量
const pageParameter = reactive({
  queryMark: 'J',
  showRulesSize: 'MIN',
  condition: {},
  searchConditon: {
    requirement: 'cname',
    value: '',
  },
  filterConditon: {
    amountMin: '',
    amountMax: '',
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
  moji: 1,
  jd: 2,
  //币种
  bzName: '全部',
  bz:'0',

  dept: "",
  jfye: "",
  dfye: "",
  minYe: "",
  maxYe: "",
  minDept: "",
  maxDept: "",

  //科目类别权限
  userId: '',
  year: '',
  accId: '',
  isType: '',
  isCode: '',
  accType: '',
  //项目list
  project:[],
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

const deptList = ref([])

const val = {
  openOne: 0
}
const visible = ref(false);
const searchConditonList = ref([])
const lanMuData = {'accId':'','menuName':'科目项目总账','type':'',objects: '',username:useUserStoreWidthOut().getUserInfo.username}
// 表格参数
const loadMark = ref(false)
const tableSelectedRowKeys = ref([])
const tableSelectedRowObjs = ref([])
const windowWidth  = (document.documentElement.clientWidth -70)
const windowHeight  = (document.documentElement.clientHeight -350)
const totalColumnWidth = ref(0)
// 数据库模式名称
const databases = ref(getCurrentAccountName(false))
const database = ref(getCurrentAccountName(true));
const CrudApi = {
  list: useRouteApi(findAllKmProject,{schemaName:database}),
  columns: JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark)))
}
const tableRef = ref(null)
function getAccountName(){
  console.log(pageParameter.strDate.split("-")[0])
  return pageParameter.strDate.split("-")[0];
}
// 组件实例区
const [registerTable, { reload,setColumns,getColumns,getDataSource }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  loading: loadMark.value,
  immediate: false,
  canResize: true,
  /*  showSummary: true,
    summaryData: [],*/
  showIndexColumn: false , //显示序号列
  pagination:{ pageSize: 200,showSizeChanger: true, pageSizeOptions: ['200','500','1000','1500'],showTotal: t => `总共${t}条数据` },
  /*actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  } as　BasicTable*/
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
const [registerImportPage, { openModal: openImportPageM }] = useModal()
const [registerQueryPage, { openModal: openQueryPageM }] = useModal()
const [registerReviewPage, { openModal: openReviewPageM }] = useModal()

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
  //
}
const breakNumTidyBtn = async() => {
  breakNumTidy()
  pageReload()
}
const onChangeSwitch = async(v)=>{ // 动态列
  styleName.value = showStyle.value.filter(o=> o.value === v)[0].name
  pageParameter.queryMark = v
  resetDynamicColumnData()
  pageParameter.searchConditon.requirement = 'cname'
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
  let min  = pageParameter.filterConditon.amountMin.trim()
  let max  = pageParameter.filterConditon.amountMax.trim()
  if (''==min && max != '' || ''==max && min != ''){
    message.warn('请完善金额区间过滤条件！')
    return false
  }
  if (min != '' && max != '' && (!isRealNum(min) || !isRealNum(max) || parseFloat(min) == 0 || parseFloat(max) == 0)){
    message.warn('请输入数值类型值并且值不能为0！')
    closeFilterV()
    return false
  }else if (parseFloat(min) > parseFloat(max)){
    message.warn('金额区间最小值部门大于最大值！')
    closeFilterV()
    return false
  }
  // 校验完成后搜索
  pageReload()
}

const closeFilterV = ()=>{
  pageParameter.filterConditon.amountMin =''
  pageParameter.filterConditon.amountMax =''
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
          message.info("数据库同步成功！")
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
  thisCs.forEach(item=>{
    if (item.ifShow== null || item.ifShow)
      total+= parseInt(item.width)
  })
  if (total > windowWidth){
    let f = 0
    if (visible.value) f=260
    totalColumnWidth.value = Number(windowWidth)-f
    tableRef.value.$el.style.setProperty('width',(windowWidth+20-f)+'px')
  }else {
    if (visible.value && (windowWidth-260) < total) total-=(total-(windowWidth-260))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width',(total+20)+'px')
  }
}

/*集团查询页面 页面参数*/
const pageMode = ref('1')
const queryAccIds = ref([])
const accAuthList = ref([])
const accNameAll = ref('')
/*栏目设置end*/
const loadPage = (data)=>{
  accNameAll.value = data.pageParameter.accNameAll   // 账套全称
  // 回显筛选条件
  loadMark.value = false
  titleName.value  = data.kmList[0].ccodeName
  kmList.value  = data.kmList
  pageParameter.km = data.kmList[0].ccode
  pageParameter.km = data.kmList[0].ccode
  pageParameter.strDate = data.strDate.replaceAll("-",".")
  pageParameter.endDate = data.endDate.replaceAll("-",".")
  pageParameter.showRulesSize  = data.fontSize
  bzName.value = data.bzName
  pageParameter.bz = data.bz
  pageParameter.ibook = data.ishaveRjz
  pageParameter.minKm = data.kmList[0].ccode
  pageParameter.maxKm = data.kmList[data.kmList.length-1].ccode
  pageParameter.jd = data.jd
  database.value = data.pageParameter.database

  pageParameter.jfye = data.jfye
  pageParameter.dfye = data.dfye
  pageParameter.minYe = data.minYe
  pageParameter.maxYe = data.maxYe

  pageParameter.projectCate = data.projectCate
  pageParameter.projectClass = data.projectClass

  pageParameter.project = data.project

  //根据条件查询数据
  const dw = kmList.value.filter(o=> o.ccode === pageParameter.km)[0].menterage // 单位
  const wb = kmList.value.filter(o=> o.ccode === pageParameter.km)[0].currencyType //美元
  const kmStyle =  kmList.value.filter(o=> o.ccode === pageParameter.km)[0].cbookType //科目样式
  if(kmStyle){
    pageParameter.queryMark  = showStyle.value.filter(o=> o.name === kmStyle)[0].value //显示样式
    styleName.value = showStyle.value.filter(o=> o.name = kmStyle)[0].name
  }

  pageParameter.dw = dw
  pageParameter.wb = wb

  //权限过滤参数
  pageParameter.userId = data.userId
  pageParameter.year = pageParameter.strDate.split('.')[0]
  pageParameter.accId = data.pageParameter.accId
  pageParameter.isType = data.isType
  pageParameter.isCode = data.isCode
  pageParameter.accType = data.accType

  pageParameter.pcCode = data.projectCate
  pageParameter.pcClass = data.projectClass
  pageParameter.pList = ''
  pageParameter.projectName  = data.projectName
  if (data.openOne == 1){ // 第一次初始化 + 条件查询
    resetDynamicColumnData()
  }else { // 查询条件查询
    closeFilterV()
    pageParameter.searchConditon.value = ''
    pageReload()
  }
}

const userName = useUserStoreWidthOut().getUserInfo.username
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
    minKm: kmList.value[0].ccode,
    maxKm:  kmList.value[0].ccode,
    timflg: 'qj',
    showStyle: pageParameter.queryMark,
    fontSize: pageParameter.showRulesSize,
    bz: bzName.value,
    bzName:'全部',
    jc: pageParameter.moji,
    riqi: '',
    parameteraccuracyJSON: JSON.stringify(await useRouteApi(finByParameterAccuracy,{schemaName:getCurrentAccountName(true)})('')),
  }
  router.push({
    path: '/account-book/ab-kemuzhang/abk-mxtable',
    query: json,
  });
}

async function handleChangeMinDept(){
  titleName.value = kmList.value.filter(o=> o.ccode === pageParameter.km)[0].ccodeName
  const dw = kmList.value.filter(o=> o.ccode === pageParameter.km)[0].menterage // 单位
  const wb = kmList.value.filter(o=> o.ccode === pageParameter.km)[0].currencyType //美元
  pageParameter.dw = dw
  pageParameter.wb = wb
  const kmStyle =  kmList.value.filter(o=> o.ccode === pageParameter.km)[0].cbookType //科目样式
  if(kmStyle){
    pageParameter.queryMark  = showStyle.value.filter(o=> o.name === kmStyle)[0].value //显示样式
  }else {
    pageParameter.queryMark  = 'J';
  }
  closeFilterV()
  pageReload()
}

const dynamicAdReload = async (obj) =>{

  loadMark.value = false
}

const exportExcelNow = async () => {
  const data = JSON.parse(JSON.stringify(getDataSource()))
  const columns = getColumns()
  columns.splice(0,1);
  //筛选条件
  const title = [
    "币种："+ bzName.value +"   样式："+ styleName.value +"   期间："+ pageParameter.strDate +'-'+ pageParameter.endDate +" ",
  ]
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
      },
    },
    {
      cell: 'A2',
      font: {
        sz: 12,
        color: { rgb: "000000" },
      },
      fill: {
        fgColor: { rgb: "ffffff" },
      },
    },
  ]
  const cell = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','AA','AB']
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
  //merges.push('`A2:' + cell[keys.length-1] +'2`');

  //数量金额格式化调整
  data.forEach(v=>{

    v.qcJfnum = formatNum(v.qcJfnum )
    v.qcJfwb = formatData(v.qcJfwb )
    v.qcJfMoney = formatData(v.qcJfMoney )

    v.qcDfnum = formatNum(v.qcDfnum )
    v.qcDfwb = formatData(v.qcDfwb )
    v.qcDfmoney = formatData(v.qcDfmoney )

    v.jfnum = formatNum(v.jfnum )
    v.jfwb = formatData(v.jfwb )
    v.jfMoney = formatData(v.jfMoney )

    v.dfnum = formatNum(v.dfnum )
    v.dfwb = formatData(v.dfwb )
    v.dfmoney = formatData(v.dfmoney)

    v.ljJfnum = formatNum(v.ljJfnum )
    v.ljJfwb = formatData(v.ljJfwb )
    v.ljJfMoney = formatData(v.ljJfMoney )

    v.ljDfnum = formatNum(v.ljDfnum )
    v.ljDfwb = formatData(v.ljDfwb )
    v.ljDfmoney = formatData(v.ljDfmoney )

    v.qmJfnum = formatNum(v.qmJfnum )
    v.qmJfwb = formatData(v.qmJfwb )
    v.qmJfMoney = formatData(v.qmJfMoney )

    v.qmDfwb = formatData(v.qmDfwb )
    v.qmDfnum = formatNum(v.qmDfnum )
    v.qmDfmoney = formatData(v.qmDfmoney )

  })
  //样式靠右列
  let rightrow=[]
  //样式靠左列
  let leftrow=[]
  if(pageParameter.queryMark === 'J'){
    rightrow=['C','D','E','F','G','H','I','J']
    leftrow =['A','B']
  }else if(pageParameter.queryMark === 'SJ'){
    rightrow=['E','G','I' ,'K','M','O','Q','S']
    leftrow =['A','B']
  }else if(pageParameter.queryMark === 'WJ'){
    rightrow=['E','G','I' ,'K','M','O','Q','S']
    leftrow =['A','B']
  }else{
    rightrow=['F','G','H','I','J','L','M','O','P','R','S','U','V','X','Y','Z','AA','AB']
    leftrow =['A','B']
  }

  const sheet =[
    {
      title: pageParameter.projectName+'-项目总账',
      tHeader: title,
      multiHeader: multiHeader,
      table: data,
      keys: keys,
      merges: merges,
      sheetName: '项目总账',
      cellStyle: cellStyle,
      rightrow:rightrow,
      leftrow:leftrow,
    },
  ]
  let name = pageParameter.companyName
  exportExcel(sheet, 'xlsx',name+'-项目总账-'+pageParameter.strDate+'-'+pageParameter.endDate)
}
const openPrint = () => {
  openPrintPage(true, {
    data: {
      dynamicTenantId: database.value,
      defaultAdName: useCompanyOperateStoreWidthOut().getSchemaName,
      year: pageParameter.strDate.substring(0,4),
    }
  })
}

import { Loading } from '/@/components/Loading';
import {exportExcel2} from "/@/api/record/generalLedger/excelExport";

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
const loadPrint = (obj) => {
  openCompFullLoading()
  const data = JSON.parse(JSON.stringify(getDataSource()))
  let printList: any = []
  let printUser = ''
  if(obj.printUser){
    printUser = '制表人：' + userName
  }
  let printBz = ''
  if(obj.printBz){
    printBz = '币种：'+ bzName.value
  }
  let printKm = '科目: '+pageParameter.km + '-' + titleName.value
  let title = titleName.value +'总账'
  //根据当前样式调整

  ///金额  数量金额 外币金额  外币数量
  if(pageParameter.queryMark === 'J'){
    //格式化数据
    data.forEach((item,index) => {
      let item1 = {}
      item1[0] = item.ccode
      item1[1] = item.cname.length > 20?item.cname.substring(0,20)+'...':item.cname
      item1[2] = formatData(item.qcJfMoney)
      item1[3] = formatData(item.qcDfmoney)
      item1[4] = formatData(item.jfMoney)
      item1[5] = formatData(item.dfmoney)
      item1[6] = formatData(item.ljJfMoney)
      item1[7] = formatData(item.ljDfmoney)
      item1[8] = formatData(item.qmJfMoney)
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
      item1[7] = ''
      item1[8] = ''
      printList.push(item1)
    }
    let num = Math.ceil(printList.length/27)
    useNewPrint({data: ['l', 'px', 'a4', true]}, (doc) => {
      loadMark.value = false
      doc.autoTable({
        head: [['', '', '', '项目总账', '', '','', '',''],
          [printKm, '', '','期间:' + pageParameter.strDate+'~'+pageParameter.endDate, '','','', printBz, '单位：元'],
          ['编码', '名称', '期初', '', '借方', '贷方','累计借方', '累计贷方', '期末', ''],
          ['', '', '借方本币', '贷方本币','借方本币', '贷方本币','借方本币', '贷方本币', '借方本币','贷方本币']],
        body: printList,
        // startY: 60,
        styles: tableStyle(),
        margin: {
          left: 30,
          top: 20,
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
          data.cell.styles.cellPadding = {top: 3, left: 2, right: 2, bottom: 2}
          data.cell.styles.fillColor = [255, 255, 255]
          data.cell.styles.fontSize = 9
          data.cell.styles.lineColor = [150, 150, 150]
          // data.cell.styles.bold = false
          // data.cell.styles.fontStyle = 'bold'

          if (data.section == 'head' && data.row.index == 0) {

            data.cell.styles.fontSize = 20
            data.cell.styles.fontStyle = 'bold'
            data.cell.styles.lineColor = [255, 255, 255]
            if (data.column.index == 3) {
              data.cell.colSpan = 3
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
            }  else if (data.column.index == 3) {
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

            if (data.column.index == 0 || data.column.index == 1){
              data.cell.styles.valign = 'middle'
              data.cell.rowSpan=2
            }
            if (data.column.index == 2 || data.column.index == 7){
              data.cell.colSpan=2
            }
            if (data.column.index == 3 || data.column.index == 4 || data.column.index == 5 || data.column.index == 6){
              data.cell.colSpan=1
            }

          }
          if (data.section == 'head' && data.row.index == 3) {
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

          }
        },
        columnStyles: {
          0: {maxHeight: 10,cellWidth: 50, halign: 'left'},
          1: {cellWidth: 130, halign: 'left'},
          2: {cellWidth: 55, halign: 'right'},
          3: {cellWidth: 55, halign: 'right'},
          4: {cellWidth: 55, halign: 'right'},
          5: {cellWidth: 55, halign: 'right'},
          6: {cellWidth: 55, halign: 'right'},
          7: {cellWidth: 55, halign: 'right'},
          8: {cellWidth: 55, halign: 'right'},
        }
      })
      compState.loading = false
    })
  }else if(pageParameter.queryMark === 'SJ'){
    console.log(pageParameter.queryMark)
    //格式化数据
    data.forEach((item,index) => {
      let item1 = {}
      item1[0] = item.num
      item1[1] = item.name
      item1[2] = item.dw
      item1[3] = formatNum(item.sjnum)
      item1[4] = formatData(item.sjmoney)
      item1[5] = formatNum(item.sdnum)
      item1[6] = formatData(item.sdmoney)
      item1[7] = item.fx
      item1[8] = formatNum(item.synum)
      item1[9] = formatData(item.symoney)
      printList.push(item1)
    })

    for (let i=0; i<printList.length%25; i++){
      let item1 = {}
      item1[0] = ''
      item1[1] = ''
      item1[2] = ''
      item1[3] = ''
      item1[4] = ''
      item1[5] = ''
      item1[6] = ''
      item1[7] = ''
      item1[8] = ''
      item1[9] = ''
      printList.push(item1)
    }
    let num = Math.ceil(printList.length/25)
    useNewPrint({data: ['l', 'px', 'a4', true]}, (doc) => {
      loadMark.value = false
      doc.autoTable({
        head: [['', '', '', '', title, '', '', '', '', ''],
          [printKm, '', '','','期间:' + pageParameter.strDate+'~'+pageParameter.endDate, '','','', printBz, '单位：元'],
          ['编码', '名称', '期初', '', '借方', '贷方','累计借方', '累计贷方', '期末', ''],
          ['', '', '借方本币', '贷方本币','借方本币', '贷方本币','借方本币', '贷方本币', '借方本币','贷方本币']],
        body: printList,
        // startY: 60,
        styles: tableStyle(),
        margin: {
          left: 50,
          top: 20,
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
          data.cell.styles.cellPadding = {top: 3, left: 2, right: 2, bottom: 2}
          data.cell.styles.fillColor = [255, 255, 255]
          data.cell.styles.fontSize = 9
          data.cell.styles.lineColor = [150, 150, 150]
          // data.cell.styles.bold = false

          if (data.section == 'head' && data.row.index == 0) {

            data.cell.styles.fontSize = 20
            data.cell.styles.fontStyle = 'bold'
            data.cell.styles.lineColor = [255, 255, 255]
            if (data.column.index == 4) {
              data.cell.colSpan = 3
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
            } else if (data.column.index == 4) {
              data.cell.colSpan = 3
              data.cell.styles.halign = 'center'
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
            if (data.column.index == 0 || data.column.index == 1 || data.column.index == 2|| data.column.index == 7){
              data.cell.styles.valign = 'middle'
              data.cell.rowSpan=2
            }
            if (data.column.index == 3 || data.column.index == 5 || data.column.index == 8){
              data.cell.colSpan=2
            }

          }
          if (data.section == 'head' && data.row.index == 3) {
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
          }
        },
        columnStyles: {
          0: {maxHeight: 10,cellWidth: 40, halign: 'center'},
          1: {cellWidth: 40, halign: 'center'},
          2: {cellWidth: 40, halign: 'center'},
          3: {cellWidth: 65, halign: 'right'},
          4: {cellWidth: 65, halign: 'right'},
          5: {cellWidth: 65, halign: 'right'},
          6: {cellWidth: 65, halign: 'right'},
          7: {cellWidth: 35, halign: 'center'},
          8: {cellWidth: 65, halign: 'right'},
          9: {cellWidth: 65, halign: 'right'},

        }
      })
    })
  }else if(pageParameter.queryMark === 'WJ'){
    //格式化数据
    data.forEach((item,index) => {
      let item1 = {}
      item1[0] = item.ccode
      item1[1] = item.cname
      item1[2] = item.wb
      item1[3] = formatData(item.sjwb)
      item1[4] = formatData(item.sjmoney)
      item1[5] = formatData(item.sdwb)
      item1[6] = formatData(item.sdmoney)
      item1[7] = item.fx
      item1[8] = formatData(item.sywb)
      item1[9] = formatData(item.symoney)
      printList.push(item1)
    })
    for (let i=0; i<printList.length%25; i++){
      let item1 = {}
      item1[0] = ''
      item1[1] = ''
      item1[2] = ''
      item1[3] = ''
      item1[4] = ''
      item1[5] = ''
      item1[6] = ''
      item1[7] = ''
      item1[8] = ''
      item1[9] = ''
      printList.push(item1)
    }
    let num = Math.ceil(printList.length/25)
    useNewPrint({data: ['l', 'px', 'a4', true]}, (doc) => {
      loadMark.value = false
      doc.autoTable({
        head: [['', '', '', '', title, '', '', '', '', ''],
          [printKm, '', '','','期间:' + pageParameter.strDate+'~'+pageParameter.endDate, '','','', printBz, '单位：元'],
          ['期间', '摘要', '原币名称', '借方', '', '贷方','', '方向', '金额', ''],
          ['', '', '', '原币金额', '本币金额','原币金额', '本币金额','', '原币金额', '本币金额']],
        body: printList,
        // startY: 60,
        styles: tableStyle(),
        margin: {
          left: 50,
          top: 20,
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
          data.cell.styles.cellPadding = {top: 3, left: 2, right: 2, bottom: 2}
          data.cell.styles.fillColor = [255, 255, 255]
          data.cell.styles.fontSize = 9
          data.cell.styles.lineColor = [150, 150, 150]
          // data.cell.styles.bold = false

          if (data.section == 'head' && data.row.index == 0) {

            data.cell.styles.fontSize = 20
            data.cell.styles.fontStyle = 'bold'
            data.cell.styles.lineColor = [255, 255, 255]
            if (data.column.index == 4) {
              data.cell.colSpan = 3
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
            } else if (data.column.index == 4) {
              data.cell.colSpan = 3
              data.cell.styles.halign = 'center'
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
            if (data.column.index == 0 || data.column.index == 1 || data.column.index == 2|| data.column.index == 7){
              data.cell.styles.valign = 'middle'
              data.cell.rowSpan=2
            }
            if (data.column.index == 3 || data.column.index == 5 || data.column.index == 8){
              data.cell.colSpan=2
            }

          }
          if (data.section == 'head' && data.row.index == 3) {
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
          }
        },
        columnStyles: {
          0: {maxHeight: 10,cellWidth: 40, halign: 'center'},
          1: {cellWidth: 40, halign: 'center'},
          2: {cellWidth: 40, halign: 'center'},
          3: {cellWidth: 65, halign: 'right'},
          4: {cellWidth: 65, halign: 'right'},
          5: {cellWidth: 65, halign: 'right'},
          6: {cellWidth: 65, halign: 'right'},
          7: {cellWidth: 35, halign: 'center'},
          8: {cellWidth: 65, halign: 'right'},
          9: {cellWidth: 65, halign: 'right'},
        }
      })
    })
  }else if(pageParameter.queryMark === 'SWJ'){
    //格式化数据
    data.forEach((item,index) => {
      let item1 = {}
      item1[0] = item.num
      item1[1] = item.name
      item1[2] = item.dw
      item1[3] = item.wb
      item1[4] = formatNum(item.sjnum)
      item1[5] = formatData(item.sjwb)
      item1[6] = formatData(item.sjmoney)
      item1[7] = formatNum(item.sdnum)
      item1[8] = formatData(item.sdwb)
      item1[9] = formatData(item.sdmoney)
      item1[10] = item.fx
      item1[11] =  formatNum(item.synum)
      item1[12] = formatData(item.sywb)
      item1[13] = formatData(item.symoney)
      printList.push(item1)
    })

    for (let i=0; i<printList.length%25; i++){
      let item1 = {}
      item1[0] = ''
      item1[1] = ''
      item1[2] = ''
      item1[3] = ''
      item1[4] = ''
      item1[5] = ''
      item1[6] = ''
      item1[7] = ''
      item1[8] = ''
      item1[9] = ''
      item1[10] = ''
      item1[11] = ''
      item1[12] = ''
      item1[13] = ''
      printList.push(item1)
    }
    let num = Math.ceil(printList.length/25)
    useNewPrint({data: ['l', 'px', 'a4', true]}, (doc) => {
      loadMark.value = false
      doc.autoTable({
        head: [['', '', '', '', '', '',title, '', '','', '', '', '', '',],
          [printKm, '', '','','','','期间:' + pageParameter.strDate+'~'+pageParameter.endDate, '','','','','', printBz, '单位：元'],
          ['期间', '摘要', '计量单位', '原币名称', '借方','','','贷方','','','方向','余额','',''],
          ['', '', '', '', '数量','原币金额','本币金额','数量','原币金额','本币金额','方向','数量','原币金额','本币金额']],
        body: printList,
        // startY: 60,
        styles: tableStyle(),
        margin: {
          left: 20,
          top: 20,
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
          data.cell.styles.cellPadding = {top: 3, left: 2, right: 2, bottom: 2}
          data.cell.styles.fillColor = [255, 255, 255]
          data.cell.styles.fontSize = 9
          data.cell.styles.lineColor = [150, 150, 150]
          // data.cell.styles.bold = false

          if (data.section == 'head' && data.row.index == 0) {

            data.cell.styles.fontSize = 20
            data.cell.styles.fontStyle = 'bold'
            data.cell.styles.lineColor = [255, 255, 255]
            if (data.column.index == 0) {
              data.cell.colSpan = 2
              data.cell.styles.halign = 'left'
            }
            if (data.column.index == 6) {
              data.cell.colSpan = 3
              data.cell.styles.halign = 'center'
            }
          }
          if (data.section == 'head' && data.row.index == 1) {
            data.cell.styles.fontSize = 10
            data.cell.styles.fontStyle = 'bold'
            data.cell.styles.lineColor = [255, 255, 255]
            if (data.column.index == 0) {
              data.cell.colSpan = 3
              data.cell.styles.halign = 'left'
            } else if (data.column.index == 6) {
              data.cell.colSpan = 3
              data.cell.styles.halign = 'center'
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
            if (data.column.index == 0 || data.column.index == 1 || data.column.index == 2 || data.column.index == 3|| data.column.index == 10){
              data.cell.styles.valign = 'middle'
              data.cell.rowSpan=2
            }
            if (data.column.index == 4 || data.column.index == 7|| data.column.index == 11){
              data.cell.colSpan=3
            }
          }
          if (data.section == 'head' && data.row.index == 3) {
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

          }
        },
        columnStyles: {
          0: {maxHeight: 10,cellWidth: 30, halign: 'center'},
          1: {cellWidth: 30, halign: 'center'},
          2: {cellWidth: 30, halign: 'center'},
          3: {cellWidth: 30, halign: 'center'},
          4: {cellWidth: 50, halign: 'center'},
          5: {cellWidth: 50, halign: 'right'},
          6: {cellWidth: 50, halign: 'right'},
          7: {cellWidth: 50, halign: 'right'},
          8: {cellWidth: 50, halign: 'right'},
          9: {cellWidth: 50, halign: 'right'},
          10: {cellWidth: 20, halign: 'center'},
          11: {cellWidth: 50, halign: 'right'},
          12: {cellWidth: 50, halign: 'right'},
          13: {cellWidth: 50, halign: 'right'},
        }
      })
    })
  }

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
//加
function add(a, b) {
  let c, d, e;
  try {
    c = a.toString().split(".")[1].length;
  } catch (f) {
    c = 0;
  }
  try {
    d = b.toString().split(".")[1].length;
  } catch (f) {
    d = 0;
  }
  return e = Math.pow(10, Math.max(c, d)), (a * e + b * e) / e;
}
//减
function sub(a, b) {
  let c, d, e;
  try {
    c = a.toString().split(".")[1].length;
  } catch (f) {
    c = 0;
  }
  try {
    d = b.toString().split(".")[1].length;
  } catch (f) {
    d = 0;
  }
  return e = Math.pow(10, Math.max(c, d)), (a * e - b * e) / e;
}

const visibleModal=ref(false)
const radiovalue=ref(1)

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
  const columns = getColumns()
  columns.splice(1, 0, {
    dataIndex: "dname",
    key: "dname",
    title: "部门名称",
  })
  columns.splice(1, 0, {
    dataIndex: "dcode",
    key: "dcode",
    title: "部门编码",
  })
  columns.splice(0,1);
  console.log(columns)
  //筛选条件
  const title = [
    "部门:"+ pageParameter.km +'-'+ titleName.value +"   币种："+ bzName.value +"   样式："+ styleName.value +"   期间："+ pageParameter.strDate +'-'+ pageParameter.endDate +" ",
  ]
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

  //根据columns 格式化导出excel数据
  //41 43 43 44
  const multiHeader = [[],[]]
  const keys = []
  const merges = []
  const cellStyle = []
  const cell = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','AA','AB']
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
  //样式靠右列
  let rightrow=[]
  //样式靠左列
  let leftrow=[]
  if(pageParameter.queryMark === 'J'){
    rightrow=['C','D','E','F','G','H','I','J']
    leftrow =['A','B']
  }else if(pageParameter.queryMark === 'SJ'){
    rightrow=['E','G','I' ,'K','M','O','Q','S']
    leftrow =['A','B']
  }else if(pageParameter.queryMark === 'WJ'){
    rightrow=['E','G','I' ,'K','M','O','Q','S']
    leftrow =['A','B']
  }else{
    rightrow=['F','G','H','I','J','L','M','O','P','R','S','U','V','X','Y','Z','AA','AB']
    leftrow =['A','B']
  }

  await useRouteApi(findDeptsheetDateOne, {schemaName: database})(pageParameter)
    .then(item=>{

      //数量金额格式化调整
      item.forEach(v=>{
        v.qcJfnum = formatNum(v.qcJfnum )
        v.qcJfwb = formatData(v.qcJfwb )
        v.qcJfMoney = formatData(v.qcJfMoney )

        v.qcDfnum = formatNum(v.qcDfnum )
        v.qcDfwb = formatData(v.qcDfwb )
        v.qcDfmoney = formatData(v.qcDfmoney )

        v.jfnum = formatNum(v.jfnum )
        v.jfwb = formatData(v.jfwb )
        v.jfMoney = formatData(v.jfMoney )

        v.dfnum = formatNum(v.dfnum )
        v.dfwb = formatData(v.dfwb )
        v.dfmoney = formatData(v.dfmoney)

        v.ljJfnum = formatNum(v.ljJfnum )
        v.ljJfwb = formatData(v.ljJfwb )
        v.ljJfMoney = formatData(v.ljJfMoney )

        v.ljDfnum = formatNum(v.ljDfnum )
        v.ljDfwb = formatData(v.ljDfwb )
        v.ljDfmoney = formatData(v.ljDfmoney )

        v.qmJfnum = formatNum(v.qmJfnum )
        v.qmJfwb = formatData(v.qmJfwb )
        v.qmJfMoney = formatData(v.qmJfMoney )

        v.qmDfwb = formatData(v.qmDfwb )
        v.qmDfnum = formatNum(v.qmDfnum )
        v.qmDfmoney = formatData(v.qmDfmoney )
      })

      const sheet =[
        {
          title: titleName.value,
          tHeader: title,
          multiHeader: multiHeader,
          table: item,
          keys: keys,
          merges: merges,
          sheetName: '全部',
          cellStyle: cellStyle,
          rightrow:rightrow,
          leftrow:leftrow,
        },
      ]
      let name = pageParameter.companyName
      exportExcel2(sheet, 'xlsx',name+'-总账-'+pageParameter.strDate+'-'+pageParameter.endDate)
    })

}

async function companyExportView() {
  //const data = JSON.parse(JSON.stringify(getDataSource()))
  //查询数据 多sheet页
  const columns = getColumns()
  columns.splice(0,1);
  //筛选条件
  const title = [
    "科目:"+ pageParameter.km +'-'+ titleName.value +"   币种："+ bzName.value +"   样式："+ styleName.value +"   期间："+ pageParameter.strDate +'-'+ pageParameter.endDate +" ",
  ]
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

  //根据columns 格式化导出excel数据
  const multiHeader = [[],[]]
  const keys = []
  const merges = []
  const cellStyle = []
  const cell = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','AA','AB']
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
  //样式靠右列
  let rightrow=[]
  //样式靠左列
  let leftrow=[]
  if(pageParameter.queryMark === 'J'){
    rightrow=['C','D','E','F','G','H','I','J']
    leftrow =['A','B']
  }else if(pageParameter.queryMark === 'SJ'){
    rightrow=['E','G','I' ,'K','M','O','Q','S']
    leftrow =['A','B']
  }else if(pageParameter.queryMark === 'WJ'){
    rightrow=['E','G','I' ,'K','M','O','Q','S']
    leftrow =['A','B']
  }else{
    rightrow=['F','G','H','I','J','L','M','O','P','R','S','U','V','X','Y','Z','AA','AB']
    leftrow =['A','B']
  }
  await useRouteApi(findDeptsheetDate, {schemaName: database})(pageParameter)
    .then(item=>{
      const sheet =[]
      for(var key in item){//遍历json对象的每个key/value对,p为key
        if(item[key].length > 2){
          //数量金额格式化调整
          item[key].forEach(v=>{
            v.qcJfnum = formatNum(v.qcJfnum )
            v.qcJfwb = formatData(v.qcJfwb )
            v.qcJfMoney = formatData(v.qcJfMoney )

            v.qcDfnum = formatNum(v.qcDfnum )
            v.qcDfwb = formatData(v.qcDfwb )
            v.qcDfmoney = formatData(v.qcDfmoney )

            v.jfnum = formatNum(v.jfnum )
            v.jfwb = formatData(v.jfwb )
            v.jfMoney = formatData(v.jfMoney )

            v.dfnum = formatNum(v.dfnum )
            v.dfwb = formatData(v.dfwb )
            v.dfmoney = formatData(v.dfmoney)

            v.ljJfnum = formatNum(v.ljJfnum )
            v.ljJfwb = formatData(v.ljJfwb )
            v.ljJfMoney = formatData(v.ljJfMoney )

            v.ljDfnum = formatNum(v.ljDfnum )
            v.ljDfwb = formatData(v.ljDfwb )
            v.ljDfmoney = formatData(v.ljDfmoney )

            v.qmJfnum = formatNum(v.qmJfnum )
            v.qmJfwb = formatData(v.qmJfwb )
            v.qmJfMoney = formatData(v.qmJfMoney )

            v.qmDfwb = formatData(v.qmDfwb )
            v.qmDfnum = formatNum(v.qmDfnum )
            v.qmDfmoney = formatData(v.qmDfmoney )
          })
          sheet.push({
            title: titleName.value,
            tHeader: title,
            multiHeader: multiHeader,
            table: item[key],
            keys: keys,
            merges: merges,
            sheetName: key,
            cellStyle: cellStyle,
            rightrow:rightrow,
            leftrow:leftrow,
          })
        }
      }
      let name = pageParameter.companyName
      exportExcel2(sheet, 'xlsx',name+'-总账-'+pageParameter.strDate+'-'+pageParameter.endDate)
    })

}

</script>

<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style scoped>/*针对当前页面特有样式*/
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
  padding: 5px 8px !important;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}

.a-table-font-size-16 :deep(td) .a-table-money-font-size {
  font-size: 14px !important;
  color: black;
}

.a-table-font-size-12 :deep(td) .a-table-money-font-size {
  font-size: 13px !important;
  color: black;
}

:deep(.a-table-font-arial) {
  font-family: Arial !important;
}

:deep(.pz-unit-change) {
  width: 50px;
  border-radius: 4px;
  text-align: center;
  font-weight: bold;
  color: black;
  pointer-events: none;
  border: none;
}

.pz-unit-change :deep(.ant-input) {
  font-weight: bold;
  color: black;
}
.app-container {
  padding:  0px;
  margin: 10px 10px 5px;
}
.account-picker-c {
  width: 40%;
  display: inline-block;
  font-size: 14px
}
</style>

