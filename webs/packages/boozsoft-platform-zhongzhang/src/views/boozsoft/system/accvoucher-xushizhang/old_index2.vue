<template>
  <div class="app-container">
    <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
    <div class="app-container-top">
      <div style="width: 100%;margin-top: 5px;height: 100px;">
        <div style="width: 40%;padding-top:20px;">
          <div>
            <AccountPicker theme="one" readonly="" style="margin-top: -5px;"/>
          </div>
          <div style="margin-top: 5px;margin-left: 5px;">
            币种：<span style="color: black;font-weight: bold">{{pageParameter.biZhong}}</span>
            &emsp;&emsp;样式：<span style="color: black;font-weight: bold">{{pageParameter.queryMark=='1'?'金额式':'数量外币金额式'}}</span>
          </div>
        </div>
        <div style="width: 60%;float: right;margin-top: -77px;">
          <span style="font-size: 24px;color: #0096c7;font-weight: bold;margin-left: 50px;">序时账</span>
          <div class="ant-btn-group" style="float: right;margin-top: 5px;">
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
              @click="openQueryPage()"
            ><span>查询</span></button>
            <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/zhongZhang/home/welcome')"><span>退出</span></button>
          </div>
          <span style="display: block;color: black;font-size: 14px;">{{pageParameter.thisInterval.length===17?'期间':'日期'}}：{{ pageParameter.thisInterval }}
            <span style="float: right;margin-top: 5px;">
              <div >
                <!-- 搜索 -->
                <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 120px;" class="special_select">
                  <template v-for="item in searchConditonList">
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
                />&emsp;
                <a-button class="ant-btn" @click="closeFilterV(),pageReload()">
                  <SyncOutlined :style="{ fontSize: '14px' }" />
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
                        <div style="width: 640px"><a-table bordered :data-source="dynamicColumnData" :columns="dynamicColumns" :class="'a-table-font-size-12'"
                                                           childrenColumnName="children" :pagination="false" style="max-height: 650px;overflow-y: auto;">
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
                        </a-table></div>
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
                  <span @click="onChangeSwitch(true)"
                        :style="{backgroundColor: (pageParameter.queryMark=='1')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortAscendingOutlined
                    :style="{ fontSize: '14px' }"/>&emsp;金额式&emsp;<CheckOutlined v-if="pageParameter.queryMark=='1'" :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
                    <span @click="onChangeSwitch(false)"
                          :style="{backgroundColor: (pageParameter.queryMark=='2')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortDescendingOutlined
                      :style="{ fontSize: '14px' }"/>&emsp;数量外币金额式&emsp;<CheckOutlined v-if="pageParameter.queryMark=='2'" :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
                  </template>
                  <a-button>
                    <PicLeftOutlined :style="{ fontSize: '14px' }" />
                  </a-button>
                </a-popover>
                <a-popover placement="bottom">
                  <template #content>
                    <span class="group-btn-span" @click="exportExcelBtn('this')">导出当前</span><br/>
                    <span class="group-btn-span" @click="exportExcelBtn('all')">导出全部</span>
                  </template>
                  <button
                    type="button"
                    class="ant-btn"
                    ant-click-animating-without-extra-node="false"
                  ><UsbOutlined /></button></a-popover>
                <a-popover placement="bottom">
                  <template #content>
                    <span class="group-btn-span" @click="thisPrint">打印当前</span><br/>
                    <!--              <span class="group-btn-span">条件打印</span>-->
                  </template>
                  <button
                    type="button"
                    class="ant-btn"
                    ant-click-animating-without-extra-node="false"
                  ><PrinterOutlined /></button>

                </a-popover>
                <a-button @click="()=>{
                if (!visible){ visible = true;reloadColumns()}
                return false
              }">
                  <FilterFilled :style="{ fontSize: '14px' }" />
                </a-button>
              </div>
            </span>
          </span>
        </div>
      </div>
    </div>
    <div style="margin-top: -40px;" class="app-container-bottom">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <BasicTable
          ref="tableRef"
          :loading="loadMark"
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
          :row-selection="{ type: 'checkbox',selectedRowKeys: tableSelectedRowKeys, onChange: onSelectChange }"
          :scroll="{ x: totalColumnWidth,y: windowHeight }"
          :rowKey="r=>r.vouchUnCode===null?r.uniqueCode:r.vouchUnCode"
          size="small"
          @register="registerTable"
        >
          <template #serialNumber="{record,index }">
          <span slot="serialNumber" slot-scope="text,record,index">
           {{index+1}}
          </span>
          </template>

          <template #csignAndInoId="{record,index }">
            <span>{{ record.csign }}-{{ record.inoId.length==1?'000'+record.inoId:record.inoId.length==2?'00'+record.inoId:record.inoId.length==1?'0'+record.inoId:record.inoId }}</span>
            <img v-if="record.ifrag != 0" :src="'/img/voucher/voucher_'+record.ifrag+'.png'" style="display: inline-block; margin-left: 2px; margin-bottom: 2px;">
          </template>
          <template #wbmoney="{ record }">
          <span v-if="record.md!=='0.00'" style="float: right">
           {{moneyformat(record.nfratMd)}}
          </span>
            <span v-if="record.mc!=='0.00'" style="float: right">
           {{moneyformat(record.nfratMc)}}
          </span>
          </template>
          <template #num="{ record }">
           <span v-if="record.md!=='0.00'" style="float: right">
           {{numformat(record.ndS)}}
          </span>
            <span v-if="record.mc!=='0.00'" style="float: right">
           {{numformat(record.ncS)}}
          </span>
          </template>
          <template #mdmoney="{ record }">
          <span style="float: right">
           {{ moneyformat(record.md) }}
          </span>
          </template>
          <template #mcmoney="{ record }">
          <span style="float: right">
           {{ moneyformat(record.mc) }}
          </span>
          </template>
          <template #idoc="{ record }">
          <span style="float: right">
           {{ record.idoc }}
          </span>
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
          金额区间：
            </span>
              <div>
                <a-input v-model:value="pageParameter.filterConditon.amountMin"  placeholder="" style="width: 95px;font-size: 10px"/>
                ~
                <a-input v-model:value="pageParameter.filterConditon.amountMax"  placeholder="" style="width: 95px;font-size: 10px;float: right"/>
              </div>
            </li>
            <li style="margin-top: 5%">
            <span style="color: black;font-weight: bold">
          辅助核算：
            </span>
              <div>

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
        <Print @save="loadPrint" @register="registerPrintPage"/>
      </PageWrapper>
    </div>
  </div>
</template>
<script setup lang="ts">
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import { BasicTable, useTable } from '/@/components/Table'
import { useModal } from '/@/components/Modal'
import { PageWrapper } from '/@/components/Page'
import router from '/@/router';
import {usePingZhengFillinStoreWidthOut} from '/@/store/modules/boozsoft/pingzheng-fillin';
import {
  DatePicker as ADatePicker, Select as ASelect, Input as AInput, Popover as APopover, Switch as ASwitch,
  Radio as ARadio, Pagination as APagination, Popconfirm as APopconfirm, Table as ATable,
  Checkbox as ACheckbox, message,Drawer as ADrawer
} from "ant-design-vue"

import {
  SortDescendingOutlined,
  SortAscendingOutlined,
  CaretDownFilled,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled,CheckOutlined,EditOutlined,SearchOutlined,MacCommandOutlined,PrinterOutlined,UsbOutlined,UnlockTwoTone,LockTwoTone
} from '@ant-design/icons-vue';
import {
  findAllXuShiZhangList,
  breakNumTidy,
  findDbLanMuList,
  saveLanMuList,
   startVoucherStatusProcedural,startVoucherCloseStatusProcedural
} from '/@/api/record/system/accvoucher'
import { useAccvoucherXSZStore } from '/@/store/modules/accvoucher-xushizhang'
import {onMounted, reactive, ref} from "vue";
import {initDynamics,assemblyDynamicColumn} from "./data";
import AccountInfo from "/@/views/boozsoft/system/accvoucher-xushizhang/popup/AccountInfo.vue";
import Query from "/@/views/boozsoft/system/accvoucher-xushizhang/popup/query.vue";
import Print from "/@/views/boozsoft/system/accvoucher-xushizhang/popup/print.vue";
import {cloneDeep} from "lodash-es";
import {
  getAdInfoDatas,
  getThisAdInfoData,
  initBasisTabData,
  saveVoucherData
} from "/@/api/record/system/financial-settings";
import {useMessage} from "/@/hooks/web/useMessage";
import Template from "/@/views/boozsoft/system/report-template-zcfzb/template.vue";
import {
  askTask,
  compareTime, findAccCloseListByYaer,
  findByFunctionModule,
  getCurrentAccountName,
  offsetToStr, pointMessage,getThisIndexImg
} from "/@/api/task-api/tast-bus-api";
import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {finByParameterAccuracy} from "/@/api/record/km_mingxi/data";
import {useTabs} from "/@/hooks/web/useTabs";
import {exportExcel, exportExcel2} from "/@/api/record/generalLedger/excelExport";
import {useNewPrint} from "/@/utils/boozsoft/print/print";
import {tableStyle} from "/@/store/modules/abc-print";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";

const ARangePicker=ADatePicker.RangePicker
const ASelectOption=ASelect.Option
const AInputSearch=AInput.Search
const ARadioGroup=ARadio.Group
const ARadioButton=ARadio.Button
const { createConfirm,createWarningModal } = useMessage();
// 全局常量
const accvoucherStore=useAccvoucherXSZStore()
const { closeCurrent } =useTabs();
// 页面变量
const pageParameter = reactive({
  priceJd: '2',
  lvJd: '2',
  moneyJd: '2',
  numJd: '2',
  queryMark: '1',
  showRulesSize: 'MIN',
  thisInterval: '2020-01 ~ 2021-12',
  biZhong: '人民币',
  companyCode: '100',
  ifUnit: false,
  companyName: '湖北万亚软件技术有限公司',
  condition: {},
  searchConditon: {
    requirement: 'inoId',
    value: '',
  },
  filterConditon: {
    amountMin: '',
    amountMax: '',
  }
})
let defalutCompanyCode = ''
const val = ref({
  openOne: 0,
  total: 0
})

const userStore = useUserStore();
const defaultPage = ref(true) // 默认为独立账套
const visible = ref(false);
const searchConditonList = ref([])
const lanMuData = {'accId':'','menuName':'序时账','type':'',objects: '',username:useUserStoreWidthOut().getUserInfo.username}
// 表格参数
const loadMark = ref(false)
const tableSelectedRowKeys = ref([])
const tableSelectedRowObjs = ref([])
const windowWidth  = (document.documentElement.clientWidth -70)
const windowHeight  = (document.documentElement.clientHeight -320)
const totalColumnWidth = ref(0)
const defaultDbName = getCurrentAccountName(true)
const defaultYear = defaultDbName.split('-')[2]
const manipulateDbName = ref('')
const CrudApi = reactive({
  list: useRouteApi(findAllXuShiZhangList,{schemaName:manipulateDbName.value ==''?defaultDbName:manipulateDbName.value}),
  columns: JSON.parse(JSON.stringify(accvoucherStore.getColumns(pageParameter.queryMark)))
})
const tableRef = ref(null)
// 组件实例区
const [registerTable, { setColumns,getColumns,getDataSource,setTableData,setPagination,getPaginationRef }] = useTable({
  columns: CrudApi.columns,
  bordered: true,
  immediate: false,
  canResize: true,
  showIndexColumn: false , //显示序号列
  pagination:{ pageSize: 200,showSizeChanger: true, pageSizeOptions: ['200','520','1000','1500'],showTotal: t => `总共${t}条数据`, },
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
const [registerPrintPage, {openModal: openPrintPage}] = useModal()

/******************* 弹框加载中 **************************/
import { Loading } from '/@/components/Loading';
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

onMounted( ()=>{
  val.value.openOne = 1
  openQueryPageM(true, {
    data: val.value
  })
})

const openQueryPage = () => {
  val.value.openOne = 0
  openQueryPageM(true, {
    data: val
  })
}

const unCheckTable = ()=>{
  if (tableSelectedRowKeys.value.length > 0){
    tableSelectedRowKeys.value = []
    tableSelectedRowObjs.value = []
  }
}
// 页面函数区
const onSelectChange = (selectedRowKeys,obj) => {
  if (pageParameter.queryMark == '1'){
    if (obj.length>0){
     let b =  obj[0].uniqueCode
      tableSelectedRowObjs.value = getDataSource().filter(item=>item.uniqueCode == b)
      tableSelectedRowKeys.value = tableSelectedRowObjs.value.map(item=>item.vouchUnCode)
    }
  }else {
    tableSelectedRowObjs.value = obj;
    tableSelectedRowKeys.value = selectedRowKeys;
  }
};
  const pageReload = async () =>{
  let data = JSON.parse(JSON.stringify(pageParameter))
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  let res  =  await useRouteApi(findAllXuShiZhangList,{schemaName: manipulateDbName.value+'-'+defaultYear})(data)
  if (res != null && res.items.length > 0){
    setTableData([])
    loadMark.value = true
    // 关闭
    val.value.total = 1
     // 清空可能残留的数据
    setTableData(res.items)
    // 底部分页信息
    setPagination({total:res.total})
    loadMark.value = false
  }else {
    val.value.total = -1
  }
 /* reload({
    searchInfo: pageParameter
  })*/
}
const pageReload2 = async (obj,iyear) =>{
  let data = JSON.parse(JSON.stringify(pageParameter))
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  let res  =  await useRouteApi(findAllXuShiZhangList,{schemaName: obj.accId+'-'+iyear})(data)
  if (res != null && res.items.length > 0){
    loadMark.value = true
    manipulateDbName.value = obj.accId+'-'+iyear
    pageParameter.companyName = obj.accNameCn == ''?obj.accName:obj.accNameCn
    setTableData([]) // 清空可能残留的数据
    setTableData(res.items)
    // 底部分页信息
    setPagination({total:res.total})
  }else {
    pageParameter.companyCode = defalutCompanyCode
    createWarningModal({title: '温馨提示',content: '暂无任何数据！'});
  }
  loadMark.value = false
}

const breakNumTidyBtn = async() => {
  breakNumTidy()
  pageReload()
}
const onChangeSwitch = async(v)=>{ // 动态列
  pageParameter.queryMark = v?'1':'2'
  pageParameter.searchConditon.requirement = 'inoId'
  pageParameter.searchConditon.value = ''
  loadMark.value = true
  resetDynamicColumnData()
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

const  pageSearch2 = async ()=>{
  // 检查是否存在 查看是否拥有权限 待完善
  if ((pageParameter.companyCode == tempValue.value)) return false
  let accObj = accAuthList.value.filter(item=>item.coCode == pageParameter.companyCode)
  if (accObj.length == 0){
    message.warn('代码不存在停止切换！')
    pageParameter.companyCode = defalutCompanyCode
  }else if(accObj.length > 0){
    let iyear = pageParameter.thisInterval.substring(0,4)
    let authObj = userAuthMap.value.filter(item=>accObj[0].accId == item.accId && item.iyear == iyear)
    if (authObj.length == 0){
      message.warn('无该代码'+iyear+'年度数据权限！')
      pageParameter.companyCode = defalutCompanyCode
    }else {
      pageReload2(accObj[0],iyear)
    }
  }
}

const tempValue = ref(defalutCompanyCode)

const initCodeValue = async (type)=>{
  if (type == 'set'){
    if ((pageParameter.companyCode == '' || pageParameter.companyCode != '' ) && manipulateDbName.value == ''){
      pageParameter.companyCode = defalutCompanyCode
    }else if (manipulateDbName.value != '' && pageParameter.companyCode == ''){
      pageParameter.companyCode =tempValue.value
    }
  }else {
    tempValue.value = pageParameter.companyCode
    pageParameter.companyCode = ''
  }
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

const analyzeTheYearAndAllMonths = (list:any)=>{
  let months = []
  if (list.length === 0) return{year: '',months: months}
  list.forEach(item=>{
    let thisMonth = item.dbillDate.split('-')[1]
    if (months.indexOf(thisMonth) == -1){
      months.push(thisMonth)
    }
  })
  return {
    year: list[0].dbillDate.split('-')[0],
    months: months
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
   lanMuData.type = pageParameter.queryMark=='1'?'金额式':'数量外币金额式'
   findDbLanMuList(lanMuData).then(res=>{
    // 栏目列
    let dbList = res.items
    if (dbList.length > 0){
      let statiList = initDynamics()['DATA'+pageParameter.queryMark]
      dbList = combineParameters(statiList,dbList)
      dynamicColumnData.value = dbList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(dbList))
    }else {
      let statiList = initDynamics()['DATA'+pageParameter.queryMark]
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
  let newA = JSON.parse(JSON.stringify(accvoucherStore.getColumns(pageParameter.queryMark)))
  newA= assemblyDynamicColumn(dynamicColumnData.value,newA)
  setColumns(newA)
  initTableWidth(newA)
  searchConditonList.value = newA
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
    // tableRef.value.$el.style.setProperty('width',(windowWidth+20-f)+'px')
  }else {
    if (visible.value && (windowWidth-260) < total) total-=(total-(windowWidth-260))
    totalColumnWidth.value = total
    // tableRef.value.$el.style.setProperty('width',(total+20)+'px')
  }
}
/*栏目设置end*/
/*start 作废 or 标错*/

const startChangeStatus = async (flag,number) => {
  let msg = ''
  if (number === 1 && tableSelectedRowKeys.value.length === 0){
    createWarningModal({title: '开始操作前检测', content: "请选择需要"+(flag?'':'取消')+"作废的凭证！"})
  }else if (number === 1 && !flag && tableSelectedRowObjs.value.filter(item=>item.ifrag == 1).length === 0){
    createWarningModal({title: '开始操作前检测', content: '选择已被作废的凭证才能进行取消作废操作！'})
    tableSelectedRowObjs.value = []
    tableSelectedRowKeys.value = []
  }else if (number === 3 && tableSelectedRowKeys.value.length === 0){
    createWarningModal({title: '开始操作前检测', content: "请选择需要"+(flag?'':'取消')+"标错的凭证！",closable: true})
  }else if (number === 3 && !flag && tableSelectedRowObjs.value.filter(item=>item.ifrag == 3).length === 0){
    createWarningModal({title: '开始操作前检测', content: '选择已被标错的凭证才能进行取消标错操作！'})
    tableSelectedRowObjs.value = []
    tableSelectedRowKeys.value = []
  }else {
    let yearMonthsObj = analyzeTheYearAndAllMonths(tableSelectedRowObjs.value)
    // 判断当前年月 选择的月份是否已结账
    // 获取指定年度 的所有结账数据
    let closeList = await findAccCloseListByYaer({iyear: yearMonthsObj.year})
    let closeMonth = []
    closeList.forEach(item => {
      if (yearMonthsObj.months.indexOf(item.imonth) != -1 && item.gl == '1') {
        closeMonth.push(item.imonth)
      }
    })
    if (closeMonth.length > 0) {
      createWarningModal({title: '开始操作前检测', content: '凭证' + closeMonth.toString() + '月份已经结账，不能进行'+flag?'':'取消'+number == 1?'作废':'标错'+'凭证操作！'})
      return false
    }
    let res = await findByFunctionModule({iyear: yearMonthsObj.year})
    let checkMenu = ['月末结账']
    for (let i=0;i<res.length;i++)
    {
      let item = res[i]
      if (checkMenu.indexOf(item.functionModule) != -1 && item.state == '1') {
        // 校验时间
        if (item.functionModule == '月末结账' && item.time != '' && !compareTime(offsetToStr(item.time))
          && yearMonthsObj.months.indexOf(item.imonth) != -1) { // 月末处理单独
          msg = '提示：任务冲突！操作员【' +
            item.caozuoUnique +
            '】正在进行' + item.imonth + '月份' + item.functionModule + '处理!不能进行凭证'+flag?'':'取消'+number == 1?'作废':'标错'+'凭证操作，请销后再试，或联系财务主管清理该结账任务后再进行相关操作!'
          break;
        }
      } else if (item.functionModule == '月末结账' && item.time != '' && compareTime(offsetToStr(item.time))) {
        msg = '提示：发现操作员【' +
          item.caozuoUnique +
          '】正在进行' + item.imonth + '月份' + item.functionModule + '操作已经超过30分钟!可能发生任务异常导致任务超时，是否强制清除结账任务？'
        if (await askTask({msg: msg, taskId: item.id, year: yearMonthsObj.year})){
          msg = ''
          continue
        }else {
          return false
        }
      }
    }
    if (msg != '') {
      createWarningModal({title: '开始操作前检测', content: msg})
      return false
    }
    let data1 = {
      operatorUserName: '',
      selectedRowKeys: [],
      setValue: number
    }
    data1.operatorUserName = userStore.getUserInfo.realName
    data1.selectedRowKeys = tableSelectedRowKeys.value
    if (flag){
      msg = await startVoucherStatusProcedural(data1)
    }else {
      msg = await startVoucherCloseStatusProcedural(data1)
    }
    await pointMessage({title: '处理结果', content: msg,delay: true})
    setTimeout(()=>{
      unCheckTable()
      pageReload()
    },1000)
  }
}

/*作废 or 标错 end*/
// 已授权账套列表
const accAuthList = ref([])
// 账套授权信息
const userAuthMap = ref([])
const accNameAll=ref('')

const loadPage = async(data)=>{
  manipulateDbName.value =data.pageParameter.database
  accNameAll.value=data.pageParameter.accNameAll
  // 参数规则
  let ParameteRule= await useRouteApi(finByParameterAccuracy,{schemaName:manipulateDbName})('')
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

  // 获取当前账套信息
    let res = data.constant.thisAdInfo
    if (null != res /*&& res.independent == 0*/){ // 集团账套
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn == ''?res.accName:res.accNameCn
      defalutCompanyCode = res.coCode
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
      manipulateDbName.value = res.accId
    }else { //独立账套
      return
    }
    pageParameter.biZhong = data.constant.currency
    accAuthList.value = data.accAuthList
    userAuthMap.value = data.userAuthMap
    data.accAuthList = []
    data.userAuthMap = []
    data.constant.thisAdInfo = {}
    pageParameter.condition = data
    if (data.variable.periodStart == '' && data.variable.periodEnd == ''){
      pageParameter.thisInterval = data.variable.dateStart+' ~ '+data.variable.dateEnd
    }else {
      pageParameter.thisInterval = data.variable.periodStart+' ~ '+data.variable.periodEnd
    }
  loadMark.value = true
    if (data.openOne == 1){ // 第一次初始化 + 条件查询
      resetDynamicColumnData()
      loadMark.value = false
    }else { // 查询条件查询
      closeFilterV()
      pageParameter.searchConditon.value = ''
      pageReload()
    }
}
const pingzhengEditor=usePingZhengFillinStoreWidthOut()
/*账套弹框*/
const [registerInfo, { openModal: openInfoPageM }] = useModal()
const openSelection =()=>{
  openInfoPageM(true, {
    data: {accAuthList:accAuthList.value}
  })
}
const modify = (code)=>{
  if (pageParameter.companyCode != code) {
    pageParameter.companyCode = code
    pageSearch2()
  }
}
// 金额格式化
function moneyformat(data: any) {

  let str = ""
  if(parseFloat(data)!=0 && data){
    // 千分位保留2位小数
    var source = String(parseFloat(data).toFixed(pageParameter.moneyJd)).split("."); //按小数点分成2部分
    source[0] = source[0].replace( new RegExp('(\\d)(?=(\\d{3})+$)' , 'ig'), "$1,");//只将整数部分进行都好分割
    str = source.join("." );//再将小数部分合并进来
  }
  return str;
}
// 数量格式化
function numformat(data:any){
  let str = ""
  if(parseFloat(data)!=0 && data){
    if(0 === data){
      str = ""
    }else {
      var source = String(parseFloat(data).toFixed(pageParameter.numJd)).split("."); //按小数点分成2部分
      // 数量暂时不格式化；如需要，放开
      // source[0] = source[0].replace( new RegExp('(\\d)(?=(\\d{3})+$)' , 'ig'), "$1,");//只将整数部分进行都好分割
      str = source.join("." );
    }
  }
  return str;
}

const exportExcelBtn = async (flag) => {
  // 当前页显示多少条
  let pageSize:any=getPaginationRef().pageSize
  const data = JSON.parse(JSON.stringify(getDataSource()))
  let data2:any=data.splice(0,pageSize)

  if(flag==='all'){
    let data3 = JSON.parse(JSON.stringify(pageParameter))
    data3.page = getPaginationRef().current
    data3.size = getPaginationRef().pageSize*10000
    let res  =  await useRouteApi(findAllXuShiZhangList,{schemaName: manipulateDbName.value+'-'+defaultYear})(data3)
    data2=res.items
  }
  let columns = [
    {title:'制单日期',key:'dbillDate'},
    {title:'凭证类型',key:'csign'},
    {title:'凭证编码',key:'inoId'},
    {title:'摘要',key:'cdigest'},
    {title:'附单据数',key:'idoc'},
    {title:'科目编码',key:'ccode'},
    {title:'科目名称',key:'ccodeName'},
    {title:'外币名称',key:'foreignCurrency'},
    {title:'数量',key:'ndS'},
    {title:'原币',key:'nfratMd'},
    {title:'金额',key:'md'}
  ]

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
    v.inoId=v.inoId.length==1?'000'+v.inoId:v.inoId.length==2?'00'+v.inoId:v.inoId.length==1?'0'+v.inoId:v.inoId
    v.idoc=v.idoc==null?'':v.idoc
    let wb='人民币';
    if(v.foreignCurrency!=null && v.foreignCurrency!=''){ wb=v.foreignCurrency }
    v.foreignCurrency=wb
    v.ndS=v.mc!='0.00'?v.ncS==null||v.ncS==''?'':numformat(v.ncS):v.ndS==null||v.ndS==''?'':numformat(v.ndS)
    v.nfratMd=v.mc!='0.00'?v.nfratMc==null||v.nfratMc==''?'':moneyformat(v.nfratMc):v.nfratMd==null||v.nfratMd==''?'':moneyformat(v.nfratMd)
    v.md=v.mc=='0.00'?moneyformat(v.md):moneyformat(v.mc)
  })

  //样式靠右列
  let rightrow:any=[]
  //样式靠左列
  let leftrow:any=[]
  if(pageParameter.queryMark === '1'){
    rightrow=['I','J','K']
    leftrow=['B','C','D','E','F','G','H']
  }else if(pageParameter.queryMark === '2'){
    rightrow=['I','L','M','N']
    leftrow=['C','E','F']
  }

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
  exportExcel2(sheet, 'xlsx',accNameAll.value+'-科目序时账-'+pageParameter.thisInterval)
}

const thisPrint = () => {
  openPrintPage(true, {
    data: {
      dynamicTenantId: manipulateDbName.value,
      defaultAdName: useCompanyOperateStoreWidthOut().getSchemaName,
      year: '',
    }
  })
}
// 打印弹框回调
const userName = useUserStoreWidthOut().getUserInfo.username
const loadPrint = (obj) => {
  openCompFullLoading()
  const data = JSON.parse(JSON.stringify(getDataSource()))
  let printUser = ''
  if(obj.printUser){
    printUser = '制表人：' + userName
  }
  let printBz = ''
  // if(obj.printBz){
  //   printBz = '币种：人民币'
  // }
  let printList: any = []
  data.forEach((item,index) => {
    let item1 = {}
    item1[0] = item.dbillDate==null?'':item.dbillDate
    item1[1] = item.csign==null?'':item.csign
    item1[2] = item.inoId==null?'':item.inoId.length==1?'000'+item.inoId:item.inoId.length==2?'00'+item.inoId:item.inoId.length==1?'0'+item.inoId:item.inoId
    item1[3] = item.cdigest==null?'':setString(item.cdigest,20)
    item1[4] = item.idoc==null?'':item.idoc
    item1[5] = item.ccode
    item1[6] = item.ccodeName==null?'':setString(item.ccodeName,20)
    let wb='人民币';
    if(item.foreignCurrency!=null && item.foreignCurrency!=''){ wb=item.foreignCurrency }
    item1[7] = wb
    item1[8] = item.mc!='0.00'?item.ncS==null||item.ncS==''?'':numformat(item.ncS):item.ndS==null||item.ndS==''?'':numformat(item.ndS)
    item1[9] = item.mc!='0.00'?item.nfratMc==null||item.nfratMc==''?'':moneyformat(item.nfratMc):item.nfratMd==null||item.nfratMd==''?'':moneyformat(item.nfratMd)
    item1[10] = item.mc=='0.00'?moneyformat('123456987'):moneyformat('123456987')
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
    item1[9] = ''
    item1[10] = ''
    printList.push(item1)
  }

  let num = Math.ceil(printList.length/27)
  useNewPrint({data: ['l', 'px', 'a4', true]}, (doc) => {
    loadMark.value = false
    compState.loading = false

    let str= pageParameter.thisInterval.split('~')[0].replaceAll('-','.')
    let end= pageParameter.thisInterval.split('~')[1].replaceAll('-','.')
    doc.autoTable({
      head: [['', '','','','序时账', '', '', '', '', '',''],
        ['核算单位：'+ accNameAll.value,'', '','','期间：'+str+' - '+end,'','','','','','单位：元'],
        ['制单日期', '类型', '凭证编码', '摘要', '附单数', '科目编码', '科目名称','币种','数量','原币','金额']],
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
            data.cell.colSpan = 4
            data.cell.styles.halign = 'left'
          } else if (data.column.index == 2) {
            data.cell.styles.cellPadding = {top: 3, left: 2, right: 20, bottom: 2}
            data.cell.colSpan = 2
            data.cell.styles.halign = 'right'
          }
          else if (data.column.index == 4) {
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
        1: {cellWidth: 20, halign: 'center'},
        2: {cellWidth: 40, halign: 'center'},
        3: {cellWidth: 80, halign: 'left'},
        4: {cellWidth: 30, halign: 'left'},
        5: {cellWidth: 80, halign: 'left'},
        6: {cellWidth: 80, halign: 'left'},
        7: {cellWidth: 40, halign: 'center'},
        8: {cellWidth: 30, halign: 'right'},
        9: {cellWidth: 60, halign: 'right'},
        10: {cellWidth: 60, halign: 'right'},
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
<style>
.ant-table-wrapper{
  padding: 0 !important;
  background: #f2f2f2 !important;
}
</style>
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
</style>
