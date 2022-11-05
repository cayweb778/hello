<template>
  <div class="app-container">
      <div class="app-container-head"><img :src="getThisIndexImg()" class="container-head-img" draggable="false" >
      <div class="container-head-title">
        <b class="noneSpan">出纳凭证列表</b>
      </div>
      <div class="ant-btn-group">
        <button
          type="button"
          class="ant-btn ant-btn-me"
          @click="openQueryPage()"
        ><span>查询</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
          @click="openChangeSettlementPage"
        ><span>修改</span></button>
        <button
          @click="openCashierPage(true)"
          type="button"
          class="ant-btn ant-btn-me"
        ><span>签字</span></button>
        <button
          @click="openCashierPage(false)"
          type="button"
          class="ant-btn ant-btn-me"
        ><span>取消</span></button>

      </div>
      <div style="clear:both" />
      <div style="margin-top: .5%;">
        <div style="display: inline-block;float: left;margin-left: 1%;margin-top: .8%;font-size: 16px;" v-if="defaultPage">
          {{pageParameter.thisInterval.length===17?'期间':'日期'}}：<span style="color: black;font-weight: bold">{{ pageParameter.thisInterval }}</span>
          &emsp;&emsp;币种：<span style="color: black;font-weight: bold">{{pageParameter.biZhong}}</span>
          &emsp;&emsp;样式：<span style="color: black;font-weight: bold">{{pageParameter.queryMark=='1'?'明细':'汇总'}}</span>
        </div>
        <div style="display: inline-block;float: left;margin-left: 1%;font-size: 16px;" v-else>
          {{pageParameter.ifUnit?'单位':'公司'}}代码：
          <a-input-search
          placeholder=""
          v-model:value="pageParameter.companyCode"
          @search="pageSearch2"
          @focus="initCodeValue('close')"
          @blur="initCodeValue('set')"
          style="width: 100px;border-radius: 4px"
        />
          &emsp;&emsp;{{pageParameter.ifUnit?'单位':'公司'}}简称：<span style="color: black;font-weight: bold">{{pageParameter.companyName}}</span>
          <span v-if="!defaultPage" style="font-size: 16px;">&emsp;&emsp;{{pageParameter.thisInterval.length===17?'期间':'日期'}}：<span style="color: black;font-weight: bold">{{ pageParameter.thisInterval }}</span></span>
        </div>

        <div style="float: right; margin-left: 10px">
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
                          <EditOutlined class="editable-cell-icon-check"
                                          @click="save(record.key,record.min,record.max)"/>
                        </div>
                        <div v-else class="editable-cell-text-wrapper">
                          {{ text || ' ' }}
                          <EditOutlined class="editable-cell-icon" @click="edit(record.key)"/>
                          <span style="float: right;">{{ record.min + '~' + record.max }}</span>
                        </div>
                      </div>
                    </template>
                    <template #nameInput="{ text, record }">
                      <div class="editable-cell">
                        <div v-if="editableData[record.key]" class="editable-cell-input-wrapper">
                          <a-input type="text" v-model:value="editableData[record.key].nameNew"
                                   @pressEnter="saveName(record.key)" style="width: 100px"/>
                          <EditOutlined class="editable-cell-icon-check"
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
              <span class="group-btn-span">导出当前</span><br/>
              <span class="group-btn-span">条件导出</span>
            </template>
            <button
              type="button"
              class="ant-btn"
              ant-click-animating-without-extra-node="false"
            ><UsbOutlined /></button></a-popover>
          <a-popover placement="bottom">
            <template #content>
              <span class="group-btn-span">打印当前</span><br/>
              <span class="group-btn-span">条件打印</span>
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
        <div style="float: right; position: relative">
          <!-- 搜索 -->
          <span class="noneSpan" style="font-size: 16px">检索条件：</span>
          <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 120px;" class="special_select">
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
    <div style="clear:both" />
    <PageWrapper dense content-full-height fixed-height content-class="flex">
      <BasicTable
        ref="tableRef"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        :row-selection="{ type: 'checkbox',selectedRowKeys: tableSelectedRowKeys, onChange: onSelectChange }"
        :scroll="{ x: totalColumnWidth,y: windowHeight }"
        :rowKey='record=>record.uniqueCode'
        size="small"
        @register="registerTable"
      >
        <template #serialNumber="{record,index }">
          <span slot="serialNumber" slot-scope="text,record,index">
           {{index+1}}
          </span>
        </template>
        <template #csignAndInoId="{record,index }">
            <span>{{ record.csign }}-{{ record.inoId }}</span>
          <img v-if="record.ifrag != 0" :src="'/img/voucher/voucher_'+record.ifrag+'.png'" style="display: inline-block; margin-left: 2px; margin-bottom: 2px;">
        </template>

        <template #pjCsettle="{ record }">
           {{ getSettlementName(record.pjCsettle) }}
        </template>
        <template #mdmoney="{ record }">
          <span style="float: right">
           {{ money(record.md) }}
          </span>
        </template>
        <template #mcmoney="{ record }">
          <span style="float: right">
           {{ money(record.mc) }}
          </span>
        </template>
<!--        <template #action="{ record }">
          <div>
            <a-popover placement="bottom">
              <a-button style="padding: 0px 4px; height: 20px;">
                <CaretDownFilled />
              </a-button>
              <template #content>
                <p style="cursor: pointer" @click="del()"><DeleteOutlined /> 删除</p>
              </template>
            </a-popover>
          </div>
        </template>-->
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
      <Edit
        @reload="pageReload(),unCheckTable()"
        @register="registerEditPage"
      />

      <Cashier
        @reload="pageReload(),unCheckTable()"
        @register="registerCashierPage"
      />
    </PageWrapper>

  </div>
</template>
<script setup lang="ts">
import { BasicTable, useTable } from '/@/components/Table'
import { useModal } from '/@/components/Modal'
import { PageWrapper } from '/@/components/Page'
import {
  DatePicker as ADatePicker, Select as ASelect, Input as AInput, Popover as APopover, Switch as ASwitch,
  Radio as ARadio, Upload as AUpload, Pagination as APagination, Popconfirm as APopconfirm, Table as ATable,
  Checkbox as ACheckbox, message,Drawer as ADrawer,Button as AButton
} from "ant-design-vue"
const ARangePicker=ADatePicker.RangePicker
const ASelectOption=ASelect.Option
const AInputSearch=AInput.Search
const ARadioGroup=ARadio.Group
const ARadioButton=ARadio.Button
import {
  SortDescendingOutlined,
  SortAscendingOutlined,
  CaretDownFilled,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  FilterFilled,CheckOutlined,PrinterOutlined,UsbOutlined,EditOutlined
} from '@ant-design/icons-vue'
import {
  findAllCashierAccvoucher,
  breakNumTidy,
  findDbLanMuList,
  saveLanMuList,
} from '/@/api/record/system/accvoucher'
import { useAccvoucherStore } from '/@/store/modules/accvoucher-cashier'
import {onMounted, reactive, ref} from "vue";
import moment from "moment";
import {initDynamics,assemblyDynamicColumn} from "./data";
import Cashier from "/@/views/boozsoft/xian_jin_liu_liang/chu_na_ping_zheng_qian_zi/ping_zheng_lie_biao/popup/cashier.vue";
import Query from "/@/views/boozsoft/xian_jin_liu_liang/chu_na_ping_zheng_qian_zi/ping_zheng_lie_biao/popup/query.vue";
import Edit from "/@/views/boozsoft/xian_jin_liu_liang/chu_na_ping_zheng_qian_zi/ping_zheng_lie_biao/popup/edit.vue";
import {cloneDeep} from "lodash-es";
import {
  getThisAdInfoData,
} from "/@/api/record/system/financial-settings";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  getCurrentAccountName,getThisIndexImg
} from "/@/api/task-api/tast-bus-api";
import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findSettModesAll} from "/@/api/record/system/sett-modes";
const { createConfirm,createWarningModal } = useMessage();
// 全局常量
const accvoucherStore=useAccvoucherStore()

// 页面变量
const pageParameter = reactive({
  queryMark: '1',
  showRulesSize: 'MIN',
  thisInterval: '2020-01 ~ 2021-12',
  biZhong: '人民币',
  companyCode: '100',
  ifUnit: false,
  companyName: '',
  condition: {},
  filterMark: 'all', // bcash, bbank ,bcash_equivalence
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
const val = {
  openOne: 0
}
const userStore = useUserStore();
const defaultPage = ref(false)
const visible = ref(false);
const searchConditonList = ref([])
const lanMuData = {'accId':'','menuName':'出纳凭证列表','type':'',objects: '',username:useUserStoreWidthOut().getUserInfo.username}
// 表格参数
const loadMark = ref(false)
const tableSelectedRowKeys = ref([])
const tableSelectedRowObjs = ref([])
const windowWidth  = (document.documentElement.clientWidth -70)
const windowHeight  = (document.documentElement.clientHeight -350)
const totalColumnWidth = ref(0)
const defaultDbName = 'bjxgkj-001-2021' || getCurrentAccountName(true)
const manipulateDbName = ref('')
const CrudApi = reactive({
  list: useRouteApi(findAllCashierAccvoucher,{schemaName:manipulateDbName.value ==''?defaultDbName:manipulateDbName.value}),
  columns: JSON.parse(JSON.stringify(accvoucherStore.getColumns(pageParameter.queryMark))),
})
const tableRef = ref(null)

// 组件实例区
const [registerTable, { reload,setColumns,getColumns,setTableData }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  loading: loadMark.value,
  immediate: false,
  canResize: true,
  /*  showSummary: true,
    summaryData: [],*/
    showIndexColumn: false , //显示序号列
  pagination:{ pageSize: 200,showSizeChanger: true, pageSizeOptions: ['200','520','1000','1500'],showTotal: t => `总共${t}条数据` },
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
const [registerQueryPage, { openModal: openQueryPageM }] = useModal()
const [registerEditPage, { openModal: openEditPageM }] = useModal()
const [registerCashierPage, { openModal: openCashierPageM }] = useModal()


onMounted( async ()=>{
  val.openOne = 1
  openQueryPageM(true, {
    data: val
  })
  loadMark.value = true
  // 加载结算方式
  settlementMethodList.value =  (await findSettModesAll()).items
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
function getSettlementName(val){
  if (settlementMethodList.value == 0 || null == val || val == ''){
    return "";
  }else {
    let list =  settlementMethodList.value.filter(item=>item.settModesCode == val)
    return  list.length>0?list[0].settModesName:""
  }
}

const openQueryPage = () => {
  val.openOne = 0
  openQueryPageM(true, {
    data: val
  })
}

const openCashierPage = (flag) =>{
  openCashierPageM(true, {
    data: {'selectedRowKeys':tableSelectedRowKeys.value,'yearMonths': analyzeTheYearAndAllMonths(tableSelectedRowObjs.value),'mark': flag}
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
  tableSelectedRowKeys.value = selectedRowKeys;
  tableSelectedRowObjs.value = obj;
};
const pageReload =  () =>{
  setTableData([]) // 清空可能残留的数据
  reload({
    searchInfo: pageParameter
  })
}
const pageReload2 = async () =>{
  setTableData([]) // 清空可能残留的数据
  let data = JSON.parse(JSON.stringify(pageParameter))
  data.page = 1
  data.pageSize = 200
  let res  =  await useRouteApi(findAllCashierAccvoucher,{schemaName:manipulateDbName.value})(data)
  if (res != null && res.items.length > 0)setTableData(res.items)
}
const breakNumTidyBtn = async() => {
  breakNumTidy()
  pageReload()
}
const onChangeSwitch = async(v)=>{ // 动态列
  pageParameter.queryMark = v?'1':'2'
  resetDynamicColumnData()
  pageParameter.searchConditon.requirement = 'inoId'
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

const  pageSearch2 = async ()=>{
  // 检查是否存在 查看是否拥有权限
  if ((defalutCompanyCode == pageParameter.companyCode && manipulateDbName.value == '')
   || pageParameter.companyCode == tempValue.value  && manipulateDbName.value != '') return false
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
      manipulateDbName.value = accObj[0].accId+'-'+iyear
      pageParameter.companyName = accObj[0].accNameCn
      pageReload2()
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
   lanMuData.type = pageParameter.queryMark=='1'?'明细':'汇总'
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
    tableRef.value.$el.style.setProperty('width',(windowWidth+20-f)+'px')
  }else {
    if (visible.value && (windowWidth-260) < total) total-=(total-(windowWidth-260))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width',(total+20)+'px')
  }
}
/*栏目设置end*/
/*出纳凭证填写结算信息 添加 已审核 或 已记账 不能写*/
const settlementMethodList = ref([])
const  openChangeSettlementPage = ()=>{
  // 单次填制只能选中一条
  if (tableSelectedRowKeys.value.length === 0 || tableSelectedRowKeys.value.length > 1){
    createWarningModal({content: '请选择要进行修改的出纳凭证<br/>(<span style="color: red">注</span>：只能选中一条！)！'})
    unCheckTable()
  }else {
      // 检查是否已经审核 或 记账
      let rowData = JSON.parse(JSON.stringify(tableSelectedRowObjs.value[0]))
      if ((null != rowData.ccheck && rowData.ccheck != '') || rowData.ibook == '1'){
        createWarningModal({content: '无法进行对“<span style="color: red">已审核</span>”或“<span style="color: red">已记账</span>”的出纳凭证进行修改！'})
        unCheckTable()
      }else { // 打开修改弹框
        rowData.settlementMethodList = settlementMethodList.value
        openEditPageM(true, {
          data: rowData
        })
      }
  }
}

/*出纳凭证填写结算信息 添加 已审核 或 已记账 不能写*/
// 已授权账套列表
const accAuthList = ref([])
// 账套授权信息
const userAuthMap = ref([])
const loadPage = (data)=>{
  // 获取当前账套信息
  getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(res=>{
    if (null != res && res.independent == 0){
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn
      defalutCompanyCode = res.coCode
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
    pageParameter.biZhong = data.constant.currency
    accAuthList.value = data.accAuthList
    userAuthMap.value = data.userAuthMap
    data.accAuthList = []
    data.userAuthMap = []
    pageParameter.condition = data
    if (data.variable.periodStart == '' && data.variable.periodEnd == ''){
      pageParameter.thisInterval = data.variable.dateStart+' ~ '+data.variable.dateEnd
    }else {
      pageParameter.thisInterval = data.variable.periodStart+' ~ '+data.variable.periodEnd
    }
    if (data.openOne == 1){ // 第一次初始化 + 条件查询
      resetDynamicColumnData()
    }else { // 查询条件查询
      closeFilterV()
      pageParameter.searchConditon.value = ''
      pageReload()
    }
  })
}
</script>

<style src="/@/assets/styles/global-menu-index-block.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style src="/@/assets/styles/global-menu-index.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style scoped>
.vben-basic-table{
  /*width:99%*/
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
