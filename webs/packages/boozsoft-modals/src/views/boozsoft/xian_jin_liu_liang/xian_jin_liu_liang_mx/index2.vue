<template>
  <div class="app-container">
    <div class="app-container-top">
      <div class="app-container-head">
        <div class="container-head-title" style="float: none;text-align: center;display: revert;margin-left:0px">
          <b class="noneSpan" >现金流量明细表</b>
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
              <span class="group-btn-span-special" >条件导出</span>
            </template>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
            ><span>导出</span></button></a-popover>
          <a-popover placement="bottom">
            <template #content>
              <span class="group-btn-span-special" @click="exportPdfNow()">打印当前</span><br/>
              <span class="group-btn-span-special">条件打印</span>
            </template>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
            ><span>打印</span></button>
          </a-popover>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="closeCurrent(),router.push('/zhongZhang/home/welcome')"
          ><span>退出</span></button>
        </div>
        <div style="clear:both" />
        <div style="margin-top: .1%;">
          <div style="display: inline-block;font-size: 14px;float: none;text-align: center;display: revert;">
            期间：<span style="color: black;font-weight: bold">{{ pageParameter.thisInterval }}</span>
          </div>
          <div style="display: inline-block;flfindAllPingZhengListoat: left;margin-left: 1%;font-size: 14px;width: 40%">
            <AccountPicker theme="one" readonly="" @reloadTable="dynamicAdReload"/>
          </div>

          <div style="float: right; margin-left: 10px">
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
            <a-button @click="()=>{if (!visible){visible = true;reloadColumns();}return false}">
              <FilterFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </div>
          <div style="float: right; position: relative">
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
              style="width: 200px;border-radius: 4px"
            />
          </div>
        </div>
      </div>
    </div>
    <div class="app-container-bottom" :style="{height: (windowHeight+60)+'px'}">
      <BasicTable
        ref="tableRef"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        :scroll="{ x: totalColumnWidth,y: windowHeight }"
        size="small"
        style="width: 100%;"
        @register="registerTable"
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
  UsbOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons-vue';
import { findAllAccvoucher, breakNumTidy,findDbLanMuList,saveLanMuList } from '/@/api/record/system/accvoucher'
import { useXjllStore } from '/@/api/record/xian_jin_liu_liang_mx/data'
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {onMounted, reactive, ref} from "vue";
import {
  intervalWorking
} from "/@/views/boozsoft/system/accvoucher/data";
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
import {findXjllMx} from "/@/api/record/generalLedger/data";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {finByParameterAccuracy} from "/@/api/record/km_mingxi/data";
import router from "/@/router";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import { exportExcel } from "/@/api/record/generalLedger/excelExport";
import AccountInfo from "/@/views/boozsoft/system/accvoucher/popup/AccountInfo.vue";
import {useTabs} from "/@/hooks/web/useTabs";
const {closeCurrent} = useTabs(router);

const { createConfirm,createWarningModal } = useMessage();
// 全局常量
const glStore = useXjllStore()

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
  },
  reloadMark: false,
  // 显示期间
  endDate: "",
  strDate: "",
  //显示未记账
  ibook: true,

  userId: '',
  year: '',
})

const styleName = ref<String>("金额式")
// 科目名称标题
const titleName = ref<String>("")
//显示未记账
const ibook = ref<boolean>(true)

const val = {
  openOne: 0
}
const visible = ref(false);
const searchConditonList = ref([])
const lanMuData = {'accId':'','menuName':'现金流量明细表','type':'',objects: '',username:useUserStoreWidthOut().getUserInfo.username}
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
  list: useRouteApi(findXjllMx,{schemaName:databasesName.value}),
  columns: JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark)))
}
const tableRef = ref(null)

// 组件实例区
const [registerTable, { reload,setColumns,getColumns,getDataSource, getPaginationRef,setPagination,setTableData }] = useTable({
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
const [registerQueryPage, { openModal: openQueryPageM }] = useModal()

onMounted( ()=>{
  val.openOne = 1
  openQueryPageM(true, {
    data: val
  })
  loadMark.value = true
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
  if(typeof(data) != "undefined" && typeof(data) != undefined && data != null){
    // 千分位保留2位小数
    var source = String(parseFloat(data).toFixed(2)).split("."); //按小数点分成2部分
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
  if(data === '3'){
    str = "净流入"
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
  pageParameter.queryMark = v
  resetDynamicColumnData()
  pageParameter.searchConditon.requirement = 'fx'
  pageParameter.searchConditon.value = ''
}
const  pageSearch = async ()=>{
  // 搜索前校验格式
  if (''==pageParameter.searchConditon.requirement.trim()){
    message.warn('请选择检索条件')
    return false
  }
  // 校验完成后搜索
  closeFilterV()
  pageReload()
}
const showThis = ref(false)
const pageReload2 = async (obj) => {
  let data = JSON.parse(JSON.stringify(pageParameter))
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  loadMark.value = true
  let res = await useRouteApi(findAllAccvoucher, {schemaName: obj.accId + '-' + obj.year})(data)
  if (res != null && res.items.length > 0) {
    //manipulateDbName.value = obj.accId + '-' + obj.year
    setTableData([]) // 清空可能残留的数据
    setTableData(res.items)
    // 底部分页信息
    setPagination({total: res.total})
  } else {
    createWarningModal({title: '温馨提示', content: '暂无任何数据！'});
  }
  loadMark.value = false
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

/*栏目设置end*/
const loadPage = (data)=>{
  // 回显筛选条件
  loadMark.value = false
  pageParameter.strDate = data.strDate.replaceAll("-",".")
  pageParameter.endDate = data.endDate.replaceAll("-",".")
  pageParameter.showRulesSize  = data.fontSize
  pageParameter.ibook = data.ishaveRjz
  databasesName.value = data.accountId
  if (data.strDate != '' && data.endDate != '') {
    pageParameter.thisInterval = data.strDate + ' ~ ' + data.endDate
  } else {
    pageParameter.thisInterval = data.dateStart + ' ~ ' + data.dateEnd
  }

  if (data.openOne == 1){ // 第一次初始化 + 条件查询
    resetDynamicColumnData()
  }else { // 查询条件查询
    closeFilterV()
    pageParameter.searchConditon.value = ''
    pageReload()
  }
  showThis.value = true
}

const dynamicAdReload = async (obj) =>{
  loadMark.value = false
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

  console.log(columns)
  console.log(data)
  console.log(keys)
  const sheet =[
    {
      title: '现金流量明细表',
      tHeader: ["期间："+ pageParameter.thisInterval],
      multiHeader: multiHeader,
      table: data,
      keys: keys,
      merges: merges,
      sheetName:  '现金流量明细表',
      cellStyle: cellStyle,
      rightrow:rightrow,
      leftrow:leftrow,
    },
  ]
  let name = pageParameter.companyName
  exportExcel(sheet, 'xlsx',name+'-现金流量明细表-'+pageParameter.strDate+'-'+pageParameter.endDate)
}

</script>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" ></style>

<style scoped>
.app-container-bottom {
  :deep(.ant-table-thead th) {
    background-color: #d8d8d8 !important;
    font-weight: bold !important;
    border-left: 1px solid #d8d8d8 !important;
    border-bottom: 1px solid #d8d8d8 !important;
    border-top: 1px solid #d8d8d8 !important;
  }
}
</style>
