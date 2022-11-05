<template>
  <div class="app-container">
    <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
    <div class="app-container-top">
      <div style="width: 100%;margin-top: 5px;height: 100px;">
        <div style="width: 40%;padding-top:20px;">
          <div>
            <AccountPicker theme="one" readonly="" @reloadTable="dynamicAdReload" style="margin-top: -5px;"/>
          </div>
          <div style="float: left;margin-left:6px;">
            <span style="font-size: 14px;">客户：</span>
            <a-select
              show-search
              placeholder="客户选择"
              option-filter-prop="children"
              style="width: 250px;display: inline-block;"
              @change="handleChangeMinKm"
              v-model:value="pageParameter.km"
            >
              <a-select-option v-for="d in kmList" :value="d.ccode">
                {{ d.value }}
              </a-select-option>
              <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
            </a-select>
          </div>
          <div style="margin-top: 5px;float: right;margin-right: 200px;">
            本位币：<span style="font-weight: bold">{{ bwb }} </span>
            &emsp;&emsp;样式：<span style="color: black;font-weight: bold">{{ styleName }}</span>
          </div>
        </div>
        <div style="width: 60%;float: right;margin-top: -77px;">
          <span style="font-size: 24px;color: #0096c7;font-weight: bold;">客户科目明细账</span>
          <div class="ant-btn-group" style="float: right;margin-top: 5px;">
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
              @click="openQueryPage()"
            ><span>查询</span></button>
            <a-popover placement="bottom">
              <template #content>
                <span class="group-btn-span" @click="thisPageImport">导出当前</span><br/>
                <span class="group-btn-span" @click="allPageImport">导出全部</span>
              </template>
              <button
                type="button"
                class="ant-btn ant-btn-me"
                ant-click-animating-without-extra-node="false"
              ><span>导出</span></button></a-popover>
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
          <span style="display: block;color: black;font-size: 14px;margin-left: 10px;">期间：{{ time.strDate }} - {{ time.endDate }}
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
                          :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
                      v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
                    <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                          :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
                      v-if="pageParameter.showRulesSize==='MIN'"/></span>
                  </template>
                  <!--            <template #title>
                                <b>设置表格字号</b></template>-->
                  <a-button>
                    <SettingFilled :style="{ fontSize: '14px' }"/>
                  </a-button>
                </a-popover>

                <a-popover placement="bottom">
                  <template #content>
              <span class="group-btn-span-special2" @click="onChangeSwitch('J')"
                    :style="pageParameter.queryMark=='J'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;金额式&emsp;&ensp;<CheckOutlined
                v-if="pageParameter.queryMark=='J'"/></span><br/>
                    <span class="group-btn-span-special2" @click="onChangeSwitch('SJ')"
                          :style="pageParameter.queryMark=='SJ'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;数量金额式<CheckOutlined
                      v-if="pageParameter.queryMark=='SJ'"/></span><br/>
                    <span class="group-btn-span-special2" @click="onChangeSwitch('WJ')"
                          :style="pageParameter.queryMark=='WJ'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;外币金额式<CheckOutlined
                      v-if="pageParameter.queryMark=='WJ'"/></span><br/>
                    <span class="group-btn-span-special2" @click="onChangeSwitch('SWJ')"
                          :style="pageParameter.queryMark=='SWJ'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;数量外币式<CheckOutlined
                      v-if="pageParameter.queryMark=='SWJ'"/></span><br/>
                  </template>
                  <a-button>
                    <PicLeftOutlined :style="{ fontSize: '14px' }"/>
                  </a-button>
                </a-popover>
                <a-button @click="()=>{
                    if (!visible){ visible = true;reloadColumns()}
                    return false
                  }">
                  <FilterFilled :style="{ fontSize: '14px' }"/>
                </a-button>
              </div>
            </span>
          </span>
        </div>
      </div>
    </div>
    <div class="app-container-bottom">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <BasicTable
          ref="tableRef"
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
          :scroll="{ x: totalColumnWidth,y: windowHeight }"
          size="small"
          @register="registerTable"
        >
          <template #md="{ record }">
            <span style="float: right">{{ moneyformat(record.md) }}</span>
          </template>
          <template #mc="{ record }">
            <span style="float: right">{{ moneyformat(record.mc) }}</span>
          </template>
          <template #yue="{ record }">
            <span style="float: right">{{ moneyformat(record.yue) }}</span>
          </template>
          <template #ndS="{ record }">
            {{ numformat(record.ndS) }}
          </template>
          <template #ncS="{ record }">
            {{ numformat(record.ncS) }}
          </template>
          <template #yue_num="{ record }">
            {{ numformat(record.yue_num) }}
          </template>
          <template #nfrat_md="{ record }">
            <span style="float: right">{{ moneyformat(record.nfrat_md) }}</span>
          </template>
          <template #nfrat_mc="{ record }">
            <span style="float: right">{{ moneyformat(record.nfrat_mc) }}</span>
          </template>
          <template #yue_nfrat="{ record }">
            <span style="float: right">{{ moneyformat(record.yue_nfrat) }}</span>
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
            <!--          <li style="margin-top: 5%">-->
            <!--            <span style="color: black;font-weight: bold">-->
            <!--              余额本币：-->
            <!--            </span>-->
            <!--            <div>-->
            <!--              <a-input v-model:value="pageParameter.filterConditon.amountMinYe"  placeholder="" style="width: 95px;font-size: 10px"/>-->
            <!--              ~-->
            <!--              <a-input v-model:value="pageParameter.filterConditon.amountMaxYe"  placeholder="" style="width: 95px;font-size: 10px;float: right"/>-->
            <!--            </div>-->
            <!--          </li>-->
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
    <a-modal v-model:visible="visibleModal" title="提示" @ok="visibleOK" cancelText="关闭" okText="导出表格">
      <p/>
      <a-radio-group style="width: 100%;margin-left: 50px;" v-model:value="radiovalue">
        <a-radio :value="1">
          <span>所有客户按页签显示</span>
        </a-radio>
        <a-radio :value="2" style="margin-left: 50px;">
          <span>所有客户显示在同一页签</span>
        </a-radio>
      </a-radio-group>
      <p/>
    </a-modal>
  </div>
</template>
<script setup lang="ts">
import { BasicTable, useTable } from '/@/components/Table'
import { useModal } from '/@/components/Modal'
import { PageWrapper } from '/@/components/Page'
import UnitChange from '../department/UnitChange.vue';
import {
  DatePicker as ADatePicker, Select as ASelect, Input as AInput, Popover as APopover,
  Radio as ARadio,Popconfirm as APopconfirm, Table as ATable,
  Checkbox as ACheckbox, message,Drawer as ADrawer,Modal as AModal
} from "ant-design-vue"
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
  FilterFilled,CheckOutlined,EditOutlined,CaretDownOutlined
} from '@ant-design/icons-vue'
import { findDbLanMuList,saveLanMuList } from '/@/api/record/system/accvoucher'
//////////////////////////////// START /////////////////////////////////////////
// 跟科目明细账用一样的表头
import { initDynamics,assemblyDynamicColumn} from "./data";
import { finByParameterAccuracy } from '/@/api/record/km_mingxi/data';
// 所用的单独查询方法、条件弹框
import { findAll,exportAll,exportAll2 } from '/@/api/record/cus-km-mx/data';
import Query from "/@/views/boozsoft/system/cus-km-mx/popup/query.vue";
import Print from "/@/views/boozsoft/system/cus-km-mx/popup/print.vue";
// 防止 名称重复；需要单独建立动态表头文件
import { cusKmMxGlStore } from '/@/api/record/cus-km-mx/cusKmMxColumn'

//////////////////////////////// END /////////////////////////////////////////

import {onMounted, reactive, ref} from "vue";
import moment from "moment";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {cloneDeep} from "lodash-es";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  getThisIndexImg,
  getCurrentAccountName,
} from "/@/api/task-api/tast-bus-api";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import router from "/@/router";
import {savelog} from "/@/api/record/log";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findByAccId} from "/@/api/record/system/account";
import {useRoute} from "vue-router";
import {findCusAll, findPeriodByAccontId} from "/@/api/record/generalLedger/data";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useTabs} from "/@/hooks/web/useTabs";
import {exportExcel, exportExcel2} from "/@/api/record/generalLedger/excelExport";
const ARangePicker=ADatePicker.RangePicker
const ASelectOption=ASelect.Option
const AInputSearch=AInput.Search
const ARadioGroup=ARadio.Group
const ARadioButton=ARadio.Button
const { createConfirm,createWarningModal } = useMessage();
// 全局常量
const cusmxglStore = cusKmMxGlStore()
const { closeCurrent } =useTabs();
// 页面变量
const pageParameter = reactive({
  database: '',
  thisAdInfo: {},
  total: 0,
  page: '',
  size: '',
  queryMark: 'J',
  showRulesSize: 'MIN',
  condition: {},
  searchConditon: {
    requirement: 'ccode',
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
  companyCode: '',
  companyName: '',
  reloadMark: false,
  numJd: '2',  // 数量精度
  priceJd: '2',  // 金额精度
  lvJd: '2',  // 汇率精度
  moneyJd: '2',  // 金额精度
  wb: "",
  dw: "",
  minDept: "",
  maxDept: "",
  km: "",
  // 显示期间
  endDate: "",
  strDate: "",
  //显示未记账
  ishaveRjz: true,
  bz: '',
  styleValue: '',
  cusList: ''
})

const styleName = ref<String>("金额式")
// 本位币
const bwb = ref<String>("")
// 会计科目
const kmList: any = ref([])
// 部门
const deptList: any = ref([])
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
const lanMuData = {'accId':'','menuName':'客户科目明细账','type':'',objects: '',username:useUserStoreWidthOut().getUserInfo.username}
// 表格参数
const loadMark = ref(false)
const tableSelectedRowKeys = ref([])
const tableSelectedRowObjs = ref([])
const windowWidth  = (document.documentElement.clientWidth -70)
const windowHeight  = (document.documentElement.clientHeight -350)
const totalColumnWidth = ref(0)
const dynamicTenantId = ref(getCurrentAccountName(true))
const accId = ref(getCurrentAccountName(false))
const accNameAll: any = ref('')

const tableRef = ref(null)
const userName = useUserStoreWidthOut().getUserInfo.username
// 增加操作日志：1、修改2、删除3、停用、4查看、5导入、6导出、7打印
const logmap = ref({
  accId: dynamicTenantId.value,
  username: useUserStoreWidthOut().getUserInfo.username,
  flag: '4',
  text: '',
});
const CrudApi = {
  list: findAll,
  columns: JSON.parse(JSON.stringify(cusmxglStore.getColumns(pageParameter.queryMark)))
}

// 组件实例区
const [registerTable, { reload,setTableData,setColumns,getColumns,getPaginationRef,setPagination,getDataSource }] = useTable({
  api: useRouteApi(CrudApi.list,{schemaName:dynamicTenantId}),
  columns: CrudApi.columns,
  bordered: true,
  loading: loadMark.value,
  immediate: false,
  canResize: true,
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
const route = useRoute();
const routemsg = ref(route.query);
const [registerQueryPage, { openModal: openQueryPageM }] = useModal()
const [registerReviewPage, { openModal: openReviewPageM }] = useModal()

onMounted( async ()=>{
  if (JSON.stringify(routemsg.value).length === 2) {
    val.openOne = 1
    openQueryPageM(true, {
      data: val
    })
    loadMark.value = true
  }
  else{
    dynamicTenantId.value=routemsg.value.dynamicTenantId
    pageParameter.ishaveRjz=routemsg.value.ishaveRjz
    pageParameter.strDate=routemsg.value.strDate.replaceAll("-","")
    pageParameter.endDate=routemsg.value.endDate.replaceAll("-","")
    pageParameter.km=routemsg.value.km
    pageParameter.bz=routemsg.value.bz
    pageParameter.minDept=routemsg.value.minDept
    pageParameter.styleValue=routemsg.value.styleValue
    pageParameter.companyCode=routemsg.value.companyCode
    pageParameter.companyName=routemsg.value.companyName
    // 客户
    deptList.value = await useRouteApi(findCusAll,{schemaName:routemsg.value.dynamicTenantId})('');
    // 用于显示
    time.strDate=routemsg.value.strDate.replaceAll("-",".")
    time.endDate=routemsg.value.endDate.replaceAll("-",".")
    resetDynamicColumnData()
  }
})

const filterOption = (input: string, option: any) => {
  return option.children[0].children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};
// +++++++++++++++++++++ 复写公司代码 +++++++++++++++++++++++++
const dynamicAdReload = async (obj) =>{
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
      dynamicTenantId.value = obj.accId+'-'+obj.year
      pageParameter.thisAdInfo = {}
      pageParameter.thisAdInfo = obj
      pageParameter.total = res.total
      setPagination({total:res.total})
      deptList.value=await useRouteApi(findCusAll,{schemaName:obj.accId+'-'+obj.year})('');
    }
    else {
      createWarningModal({title: '温馨提示',content: '暂无任何数据！'});
      pageParameter.thisAdInfo = {}
      pageParameter.total = -1
    }
  }
  loadMark.value = false
}
// +++++++++++++++++++++ 复写公司代码 +++++++++++++++++++++++++

function tableRow(row:any,num:any){
  return  (row.cdigest=='当前合计' ? 'table-month-striped' : null)
    || (row.cdigest=='总计' ? 'table-year-striped' : null)
    || (row.number%2==0 ? 'table-odd-striped' : null)
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

const onChangeSwitch = async(v)=>{ // 动态列
  styleName.value = showStyle.value.filter(o=> o.value === v)[0].name
  pageParameter.queryMark = v
  resetDynamicColumnData()
  pageParameter.searchConditon.requirement = 'ccode'
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
      lanMuData.accId = dynamicTenantId.value
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
  lanMuData.accId = dynamicTenantId.value
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
  let newA = JSON.parse(JSON.stringify(cusmxglStore.getColumns(pageParameter.queryMark)))
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
const loadPage = async (data)=>{
  // ++++++++++++ 复写公司代码 +++++++++++++++++++
  pageParameter.companyCode=data.pageParameter.companyCode
  pageParameter.companyName=data.pageParameter.companyName
  pageParameter.database=data.pageParameter.database
  accId.value=data.pageParameter.accId
  dynamicTenantId.value=data.pageParameter.database
  accNameAll.value=data.pageParameter.accNameAll
  // 查询是否独立账套  是否独立账套（1）反之集团账套（0）
  const datainfo = await findByAccId(data.pageParameter.accId);
  bwb.value=datainfo.currencyName

  // ++++++++++++ 复写公司代码 +++++++++++++++++++
  // 参数规则
  let ParameteRule= await useRouteApi(finByParameterAccuracy,{schemaName: dynamicTenantId})('')
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


  // 回显筛选条件
  loadMark.value = false
  deptList.value  = data.deptList
  pageParameter.minDept = data.minDept
  pageParameter.maxDept = data.maxDept
  pageParameter.km = data.km
  pageParameter.strDate = data.strDate.replaceAll("-","")
  pageParameter.endDate = data.endDate.replaceAll("-","")
  pageParameter.showRulesSize  = data.fontSize
  pageParameter.bz = data.bzName
  pageParameter.ishaveRjz = data.ishaveRjz
  pageParameter.styleValue = data.styleValue
  time.strDate=data.strDate.replaceAll("-",".")
  time.endDate=data.endDate.replaceAll("-",".")
  // 外币金额式
  if(data.bzName!=='全部'){ pageParameter.queryMark = 'WJ';styleName.value='外币金额式' }

  logmap.value.text =
    useUserStoreWidthOut().getUserInfo.username +
    '进行科目部门科目明细账查询。查询条件【开始期间：' +
    pageParameter.strDate +
    ',结束期间：' +
    pageParameter.endDate +
    ',部门：' +
    pageParameter.endDate +
    ',是否包含未记账：' +
    pageParameter.ishaveRjz +
    ',币种类型：' +
    pageParameter.bz +
    '】';
  // 添加操作日志
  await savelog(logmap.value);
  // console.log(JSON.stringify(pageParameter)+'>>>>结束')
  if (data.openOne == 1){ // 第一次初始化 + 条件查询
    resetDynamicColumnData()
  }else { // 查询条件查询
    closeFilterV()
    pageParameter.searchConditon.value = ''
    pageReload()
  }
}
async function handleChangeMinKm(){
  closeFilterV()
  pageReload()
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
function numformat(data:any){
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

/******************* 弹框加载中 **************************/
import { Loading } from '/@/components/Loading';
import {useNewPrint} from "/@/utils/boozsoft/print/print";
import {tableStyle} from "/@/store/modules/abc-print";
import {company_findByCcode} from "/@/api/codekemu/codekemu";
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
function thisPageImport() {
  openCompFullLoading()
  const data = JSON.parse(JSON.stringify(getDataSource()))
  const data2:any=data.splice(0,getPaginationRef().pageSize)
  let columns:any = getColumns()
  let ccodeName=deptList.value.filter(v=>v.uniqueCode===pageParameter.minDept)[0].label
  const title = ['客户：'+ccodeName+'              期间：'+ time.strDate +' - '+ time.endDate +'         单位：元']
  if(pageParameter.queryMark === 'J'){
    for (let i = 0; i < 8; i++) {
      title.push('')
    }
  }else if(pageParameter.queryMark === 'SJ' || pageParameter.queryMark === 'WJ'){
    for (let i = 0; i < 12; i++) {
      title.push('')
    }
  }else if(pageParameter.queryMark === 'SWJ'){
    for (let i = 0; i < 16; i++) {
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
    v.ccode=v.ccode==null?'': v.ccode
    v.ccodeName=v.ccodeName==null?'': v.ccodeName
    v.dbillDate=v.dbillDate==null?'': v.dbillDate
    v.foreignCurrency=v.foreignCurrency==null?'':v.foreignCurrency
    v.inoId=v.inoId==null?'': v.inoId
    v.mc=v.mc==null||v.mc==0?'': moneyformat(v.mc)
    v.md=v.md==null||v.md==0?'': moneyformat(v.md)
    v.ncS=v.ncS==null||v.ncS==0?'': v.ncS
    v.ndS=v.ndS==null||v.ndS==0?'': v.ndS
    v.nfrat_mc=v.nfrat_mc==null||v.nfrat_mc==0?'': moneyformat(v.nfrat_mc)
    v.nfrat_md=v.nfrat_md==null||v.nfrat_md==0?'': moneyformat(v.nfrat_md)
    v.temp1=v.temp1==null?'': v.temp1
    v.temp2=v.temp2==null?'': v.temp2
    v.unitMeasurement=v.unitMeasurement==null?'': v.unitMeasurement
    v.yue=v.yue==null||v.yue==0?'': moneyformat(v.yue)
    v.yue_nfrat=v.yue_nfrat==null||v.yue_nfrat==0?'': moneyformat(v.yue_nfrat)
    v.yue_num=v.yue_num==null||v.yue_num==0?'': v.yue_num
  })
  //样式靠右列
  let rightrow:any=[]
  //样式靠左列
  let leftrow:any=[]
  if(pageParameter.queryMark === 'J'){
    leftrow=['A','B','C','D','E']
    rightrow=['F','G','I']
  }else if(pageParameter.queryMark === 'SJ' || pageParameter.queryMark === 'WJ'){
    leftrow=['A','B','C','D','E',]
    rightrow=['G','H','I','J','L','M']
  }else{
    leftrow=['A','B','C','D','E']
    rightrow=['H','I','J','K','L','M','O','P','Q']
  }
  const sheet =[
    {
      title: '客户科目明细账',
      tHeader: title,
      multiHeader: multiHeader,
      table: data2,
      keys: keys,
      merges: merges,
      sheetName: pageParameter.km+'-'+ccodeName,
      cellStyle: cellStyle,
      rightrow:rightrow,
      leftrow:leftrow,
    },
  ]
  exportExcel(sheet, 'xlsx',accNameAll.value+'-客户科目明细账-'+time.strDate+'-'+time.endDate)
  compState.loading = false;
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
    companyExportView()
  }
  // 单页签
  else{
    onlySheetExport()
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
  let columns:any = getColumns()
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
    rightrow=['F','G','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  }else if(pageParameter.queryMark === 'SJ' || pageParameter.queryMark === 'WJ'){
    leftrow=['A','B','C','D','E','F']
    rightrow=['G','H','I','J','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  }else{
    leftrow=['A','B','C','D','E','F','G']
    rightrow=['H','I','J','K','L','M','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  }
  pageParameter.cusList=deptList.value.map(v=>v.uniqueCode).join(',')
  await useRouteApi(exportAll,{schemaName:dynamicTenantId})(pageParameter)
  .then(list=>{
    for (var key in list) {
      let ccode=list[key].ccode
      let ccodeName=list[key].ccodeName
      const mxlist=list[key].mxlist
      const title = ['                                                期间：'+ time.strDate +' - '+ time.endDate +'                           单位：元']
      if(pageParameter.queryMark === 'J'){
        for (let i = 0; i < 8; i++) {
          title.push('')
        }
      }else if(pageParameter.queryMark === 'SJ' || pageParameter.queryMark === 'WJ'){
        for (let i = 0; i < 12; i++) {
          title.push('')
        }
      }else if(pageParameter.queryMark === 'SWJ'){
        for (let i = 0; i < 16; i++) {
          title.push('')
        }
      }
      mxlist.forEach(v=>{
        v.ccode=v.ccode==null?'': v.ccode
        v.ccodeName=v.ccodeName==null?'': v.ccodeName
        v.dbillDate=v.dbillDate==null?'': v.dbillDate
        v.foreignCurrency=v.foreignCurrency==null?'':v.foreignCurrency
        v.inoId=v.inoId==null?'': v.inoId
        v.mc=v.mc==null||v.mc==0?'': moneyformat(v.mc)
        v.md=v.md==null||v.md==0?'': moneyformat(v.md)
        v.ncS=v.ncS==null||v.ncS==0?'': v.ncS
        v.ndS=v.ndS==null||v.ndS==0?'': v.ndS
        v.nfrat_mc=v.nfrat_mc==null||v.nfrat_mc==0?'': moneyformat(v.nfrat_mc)
        v.nfrat_md=v.nfrat_md==null||v.nfrat_md==0?'': moneyformat(v.nfrat_md)
        v.temp1=v.temp1==null?'': v.temp1
        v.temp2=v.temp2==null?'': v.temp2
        v.unitMeasurement=v.unitMeasurement==null?'': v.unitMeasurement
        v.yue=v.yue==null||v.yue==0?'': moneyformat(v.yue)
        v.yue_nfrat=v.yue_nfrat==null||v.yue_nfrat==0?'': moneyformat(v.yue_nfrat)
        v.yue_num=v.yue_num==null||v.yue_num==0?'': v.yue_num
      })
      sheet.push(
        {
          title: '客户科目明细账',
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
  })
  exportExcel(sheet, 'xlsx',accNameAll.value+'-客户科目明细账-'+time.strDate+'-'+time.endDate)
  compState.loading = false;
}
async function onlySheetExport() {
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
  let temp1:any=[{title: '客户编码',key:'temp3',dataIndex:'temp3'},{title: '客户名称',key:'temp2',dataIndex:'temp1'}]
  let temp2=getColumns()
  let columns=temp1.concat(temp2)
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
      merges.push('`' + cell[flg] + '1:' + cell[flg + v.children.length - 1] + '1`');
      flg = flg + v.children.length
    } else {
      multiHeader[0].push(v.title)
      multiHeader[1].push('')
      keys.push(v.key)
      //合并上下单元格
      merges.push('`' + cell[flg] + '1:' + cell[flg] + '2`');
      flg = flg + 1
    }
  })
  //样式靠右列
  let rightrow:any=[]
  //样式靠左列
  let leftrow:any=['A','B','C','D','E','F','G']
  if(pageParameter.queryMark === 'J'){
    rightrow=['H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  }else if(pageParameter.queryMark === 'SJ' || pageParameter.queryMark === 'WJ'){
    leftrow=['A','B','C','D','E','F','G']
    rightrow=['I','J','K','L','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  }else{
    leftrow=['A','B','C','D','E','F','G']
    rightrow=['J','K','L','M','N','O','Q','R','S','T','U','V','W','X','Y','Z']
  }
  pageParameter.cusList=deptList.value.map(v=>v.uniqueCode).join(',')
  await useRouteApi(exportAll2,{schemaName:dynamicTenantId})(pageParameter)
  .then(list=>{
    const data = list
    data.forEach(v=>{
      v.ccode=v.ccode==null?'': v.ccode
      v.ccodeName=v.ccodeName==null?'': v.ccodeName
      v.dbillDate=v.dbillDate==null?'': v.dbillDate
      v.foreignCurrency=v.foreignCurrency==null?'':v.foreignCurrency
      v.inoId=v.inoId==null?'': v.inoId
      v.mc=v.mc==null||v.mc==0?'': moneyformat(v.mc)
      v.md=v.md==null||v.md==0?'': moneyformat(v.md)
      v.ncS=v.ncS==null||v.ncS==0?'': v.ncS
      v.ndS=v.ndS==null||v.ndS==0?'': v.ndS
      v.nfrat_mc=v.nfrat_mc==null||v.nfrat_mc==0?'': moneyformat(v.nfrat_mc)
      v.nfrat_md=v.nfrat_md==null||v.nfrat_md==0?'': moneyformat(v.nfrat_md)
      v.temp1=v.temp1==null?'': v.temp1
      v.temp2=v.temp2==null?'': v.temp2
      v.unitMeasurement=v.unitMeasurement==null?'': v.unitMeasurement
      v.yue=v.yue==null||v.yue==0?'': moneyformat(v.yue)
      v.yue_nfrat=v.yue_nfrat==null||v.yue_nfrat==0?'': moneyformat(v.yue_nfrat)
      v.yue_num=v.yue_num==null||v.yue_num==0?'': v.yue_num
    })
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
    exportExcel2(sheet, 'xlsx',accNameAll.value+'-客户科目明细账-'+time.strDate+'-'+time.endDate)
    compState.loading = false;
  })
}

// 打印当前页
const [registerPrintPage, {openModal: openPrintPage}] = useModal()
const thisPrint = () => {
  openPrintPage(true, {
    data: ''
  })
}

// 打印弹框回调
const loadPrint = async (obj) => {
  openCompFullLoading()
  const data = JSON.parse(JSON.stringify(getDataSource()))
  let printUser = ''
  if (obj.printUser) {
    printUser = '制表人：' + userName
  }
  let printBz = ''
  if (obj.printBz) {
    printBz = '币种：人民币'
  }
  let ccodeName=deptList.value.filter(v=>v.uniqueCode===pageParameter.minDept)[0].label

  if(pageParameter.queryMark==='J'){
    let md,mc,yue = 0
    let printList: any = []
    data.forEach((item,index) => {
      let item1 = {}
      item1[0]=item.dbillDate==null?'':item.dbillDate
      item1[1]=item.inoId==null?'':item.inoId
      item1[2]=item.ccode==null?'':setString(item.ccode,17)
      item1[3]=item.ccodeName==null?'':setString(item.ccodeName,17)
      item1[4]=item.cdigest==null?'':setString(item.cdigest,17)
      item1[5]=item.md==null || item.md==0?'':moneyformat(item.md)
      item1[6]=item.mc==null || item.mc==0?'':moneyformat(item.mc)
      item1[7]=item.bprogerty==null?'':item.bprogerty
      item1[8]=item.yue==0?'':moneyformat(item.yue)
      printList.push(item1)

      if (item.ccode!='合计'){
        md = add(md,item.md)
        mc = add(mc,item.mc)
        yue = add(yue,item.yue)

        if ((index)>0 && (index)%26==0){
          let item2 = {}
          item2[0]=''
          item2[1] = '承前页'
          item2[2]=''
          item2[3]=''
          item2[4]=''
          item2[5]=toThousandFilter(md)
          item2[6]=toThousandFilter(mc)
          item2[7]=''
          item2[8]=toThousandFilter(yue)
          printList.push(item2)
        }
        else {
          md = 0
          mc = 0
          yue = 0
          if ((index)>0 && (index)%26==0 && index<data.length-1) {
            let item2 = {}
            item2[0]=''
            item2[1]='承前页'
            item2[2]=''
            item2[3]=''
            item2[4]=''
            item2[5]=item.md==0?'':moneyformat(item.md)
            item2[6]=item.mc==0?'':moneyformat(item.mc)
            item2[7]=''
            item2[8]=item.yue==0?'':moneyformat(item.yue)
            printList.push(item2)
          }
        }
      }
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
      compState.loading = false
      doc.autoTable({
        head: [['', '', '客户科目明细账', '', '', '', '', '', ''],
          ['客户：' + ccodeName,'', '期间:' + time.strDate+' - '+time.endDate,'', '', '',printBz,'','单位：元'],
          ['期间', '凭证号','科目编码','科目名称', '摘要', '借方', '贷方', '方向', '余额']],
        body: printList,
        // startY: 60,
        styles: tableStyle(),
        margin: {
          left: 35,
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
              data.cell.colSpan = 3
              data.cell.styles.halign = 'right'
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
              data.cell.styles.cellPadding = {right: 12}
              data.cell.colSpan = 3
              data.cell.styles.halign = 'right'
            }
            else if (data.column.index == 6) {
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
          1: {cellWidth: 50, halign: 'left'},
          2: {cellWidth: 80, halign: 'left'},
          3: {cellWidth: 80, halign: 'left'},
          4: {cellWidth: 100, halign: 'left'},
          5: {cellWidth: 65, halign: 'right'},
          6: {cellWidth: 65, halign: 'right'},
          7: {cellWidth: 20, halign: 'center'},
          8: {cellWidth: 65, halign: 'right'},
        }
      })
    })
  }
  if(pageParameter.queryMark==='SJ'){
    let md,mc,yue,ndS,ncS,yue_num = 0
    let printList: any = []
    data.forEach((item,index) => {
      let item1 = {}
      item1[0]=item.dbillDate==null?'':item.dbillDate
      item1[1]=item.inoId==null?'':item.inoId
      item1[2]=item.ccode==null?'':setString(item.ccode,15)
      item1[3]=item.ccodeName==null?'':setString(item.ccodeName,14)
      item1[4]=item.cdigest==null?'':setString(item.cdigest,17)
      item1[5]=item.foreignCurrency==null?'':item.foreignCurrency
      item1[6]=item.ndS==0?'':item.ndS
      item1[7]=item.md==null || item.md==0?'':moneyformat(item.md)
      item1[8]=item.ncS==0?'':item.ncS
      item1[9]=item.mc==null || item.mc==0?'':moneyformat(item.mc)
      item1[10]=item.yue_num==0?'':item.yue_num
      item1[11]=item.yue==0?'':moneyformat(item.yue)
      printList.push(item1)

      if (item.ccode!='合计'){
        md = add(md,item.md)
        mc = add(mc,item.mc)
        yue = add(yue,item.yue)
        ndS = add(ndS,item.ndS)
        ncS = add(ncS,item.ncS)
        yue_num = add(yue_num,item.yue_num)

        if ((index)>0 && (index)%24==0){
          let item2 = {}
          item2[0]=''
          item2[1] = '承前页'
          item2[2]=''
          item2[3]=''
          item2[4]=''
          item2[5]=''
          item2[6]=toThousandFilter(ndS)=='0.00'?'':toThousandFilter(ndS)
          item2[7]=toThousandFilter(md)
          item2[8]=toThousandFilter(ncS)=='0.00'?'':toThousandFilter(ncS)
          item2[9]=toThousandFilter(mc)
          item2[10]=toThousandFilter(yue_num)
          item2[11]=toThousandFilter(yue)
          printList.push(item2)
        }
        else {
          md = 0
          mc = 0
          yue = 0
          if ((index)>0 && (index)%24==0 && index<data.length-1) {
            let item2 = {}
            item2[0]=''
            item2[1]='承前页'
            item2[2]=''
            item2[3]=''
            item2[4]=''
            item2[5]=item.foreignCurrency==null?'':item.foreignCurrency
            item2[6]=item.ndS==0?'':item.ndS
            item2[7]=item.md==null || item.md==0?'':moneyformat(item.md)
            item2[8]=item.ncS==0?'':item.ncS
            item2[9]=item.mc==null || item.mc==0?'':moneyformat(item.mc)
            item2[10]=item.yue_num==0?'':item.yue_num
            item2[11]=item.yue==0?'':moneyformat(item.yue)
            printList.push(item2)
          }
        }
      }
    })
    for (let i=0; i<printList.length%24; i++){
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
      printList.push(item1)
    }
    let num = Math.ceil(printList.length/24)
    useNewPrint({data: ['l', 'px', 'a4', true]}, (doc) => {
      loadMark.value = false
      compState.loading = false
      doc.autoTable({
        head: [['', '', '', '客户科目明细账', '', '', '', '', '', '', '', ''],
          ['客户：' + ccodeName,'', '','期间:'+ time.strDate+' - '+time.endDate, '', '', '', '', printBz,'','','单位：元'],
          ['期间', '凭证号','科目编码','科目名称', '摘要','单位', '借方', '', '贷方', '','余额',''],
          ['', '','','', '','','数量','本币金额','数量','本币金额','数量','本币金额']],
        body: printList,
        styles: tableStyle(),
        margin: {
          left: 35,
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

          if (data.section == 'head' && data.row.index == 0) {
            data.cell.styles.fontSize = 20
            data.cell.styles.fontStyle = 'bold'
            data.cell.styles.lineColor = [255, 255, 255]
            if (data.column.index == 3) {
              data.cell.colSpan = 4
              data.cell.styles.halign = 'right'
            }
          }
          if (data.section == 'head' && data.row.index == 1) {
            data.cell.styles.fontSize = 10
            data.cell.styles.fontStyle = 'bold'
            data.cell.styles.lineColor = [255, 255, 255]
            if (data.column.index == 0) {
              data.cell.colSpan = 3
              data.cell.styles.halign = 'left'
            } else if (data.column.index == 3) {
              data.cell.colSpan = 4
              data.cell.styles.halign = 'right'
            } else if (data.column.index == 8) {
              data.cell.colSpan =2
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
            if (data.column.index == 0 || data.column.index == 1 || data.column.index == 2
              || data.column.index == 3|| data.column.index == 4|| data.column.index == 5){
              data.cell.styles.valign = 'middle'
              data.cell.rowSpan=2
            }
            if (data.column.index == 6 || data.column.index == 8 || data.column.index == 10){
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
              data.cell.styles.fillColor = [240, 240, 240]
            }
            if (data.row.raw[2] == '本月合计') {
              data.cell.styles.fontStyle = 'bold'
            }
            if (data.row.raw[2] == '本年累计') {
              data.cell.styles.fontStyle = 'bold'
            }
          }
        },
        columnStyles: {
          0: {maxHeight: 10,cellWidth: 50, halign: 'center'},
          1: {cellWidth: 30, halign: 'left'},
          2: {cellWidth: 60, halign: 'left'},
          3: {cellWidth: 60, halign: 'left'},
          4: {cellWidth: 80, halign: 'left'},
          5: {cellWidth: 20, halign: 'right'},
          6: {cellWidth: 30, halign: 'right'},
          7: {cellWidth: 60, halign: 'right'},
          8: {cellWidth: 30, halign: 'right'},
          9: {cellWidth: 60, halign: 'right'},
          10: {cellWidth: 30, halign: 'right'},
          11: {cellWidth: 60, halign: 'right'},
        }
      })
    })
  }
  if(pageParameter.queryMark==='WJ'){
    let md,mc,yue,nfrat_md,nfrat_mc,yue_nfrat = 0
    let printList: any = []
    data.forEach((item,index) => {
      let item1 = {}
      item1[0]=item.dbillDate==null?'':item.dbillDate
      item1[1]=item.inoId==null?'':item.inoId
      item1[2]=item.ccode==null?'':setString(item.ccode,15)
      item1[3]=item.ccodeName==null?'':setString(item.ccodeName,14)
      item1[4]=item.cdigest==null?'':setString(item.cdigest,17)
      item1[5]=item.nfrat_md==0?'':item.nfrat_md
      item1[6]=item.md==null || item.md==0?'':moneyformat(item.md)
      item1[7]=item.nfrat_mc==0?'':item.nfrat_mc
      item1[8]=item.mc==null || item.mc==0?'':moneyformat(item.mc)
      item1[9]=item.yue_nfrat==0?'':item.yue_nfrat
      item1[10]=item.yue==0?'':moneyformat(item.yue)
      printList.push(item1)

      if (item.ccode!='合计'){
        md = add(md,item.md)
        mc = add(mc,item.mc)
        yue = add(yue,item.yue)
        nfrat_md = add(nfrat_md,item.nfrat_md)
        nfrat_mc = add(nfrat_mc,item.nfrat_mc)
        yue_nfrat = add(yue_nfrat,item.yue_nfrat)

        if ((index)>0 && (index)%24==0){
          let item2 = {}
          item2[0]=''
          item2[1] = '承前页'
          item2[2]=''
          item2[3]=''
          item2[4]=''
          item2[5]=toThousandFilter(nfrat_md)=='0.00'?'':toThousandFilter(nfrat_md)
          item2[6]=toThousandFilter(md)
          item2[7]=toThousandFilter(nfrat_mc)=='0.00'?'':toThousandFilter(nfrat_mc)
          item2[8]=toThousandFilter(mc)
          item2[9]=toThousandFilter(yue_nfrat)
          item2[10]=toThousandFilter(yue)
          printList.push(item2)
        }
        else {
          md = 0
          mc = 0
          yue = 0
          if ((index)>0 && (index)%24==0 && index<data.length-1) {
            let item2 = {}
            item2[0]=''
            item2[1]='承前页'
            item2[2]=''
            item2[3]=''
            item2[4]=''
            item2[5]=item.nfrat_md==0?'':item.nfrat_md
            item2[6]=item.md==null || item.md==0?'':moneyformat(item.md)
            item2[7]=item.nfrat_mc==0?'':item.nfrat_mc
            item2[8]=item.mc==null || item.mc==0?'':moneyformat(item.mc)
            item2[9]=item.yue_nfrat==0?'':item.yue_nfrat
            item2[10]=item.yue==0?'':moneyformat(item.yue)
            printList.push(item2)
          }
        }
      }
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
      printList.push(item1)
    }
    let num = Math.ceil(printList.length/25)
    // 查询科目外币名称
    // let code= await useRouteApi(company_findByCcode,{schemaName: dynamicTenantId})({ccode:pageParameter.km})
    // let currencyType=''
    // if(code.currencyType!==''){
    //   currencyType='(外币：'+code.currencyType+')'
    // }

    useNewPrint({data: ['l', 'px', 'a4', true]}, (doc) => {
      loadMark.value = false
      compState.loading = false
      doc.autoTable({
        head: [['', '', '', '', '客户科目明细账', '', '', '', '', '', ''],
          ['客户：' + ccodeName,'', '','','期间:'+ time.strDate+' - '+time.endDate, '', '', printBz,'','','单位：元'],
          ['期间', '凭证号','科目编码','科目名称', '摘要', '借方', '', '贷方', '','余额',''],
          ['', '','','', '','原币金额','本币金额','原币金额','本币金额','原币金额','本币金额']],
        body: printList,
        styles: tableStyle(),
        margin: {
          left: 35,
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

          if (data.section == 'head' && data.row.index == 0) {
            data.cell.styles.fontSize = 20
            data.cell.styles.fontStyle = 'bold'
            data.cell.styles.lineColor = [255, 255, 255]
            if (data.column.index == 4) {
              data.cell.colSpan = 3
              data.cell.styles.halign = 'left'
            }
          }
          if (data.section == 'head' && data.row.index == 1) {
            data.cell.styles.fontSize = 10
            data.cell.styles.fontStyle = 'bold'
            data.cell.styles.lineColor = [255, 255, 255]
            if (data.column.index == 0) {
              data.cell.colSpan = 3
              data.cell.styles.halign = 'left'
            } else if (data.column.index == 4) {
              data.cell.styles.cellPadding = {left: 12}
              data.cell.colSpan = 3
              data.cell.styles.halign = 'left'
            } else if (data.column.index == 7) {
              data.cell.colSpan =2
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
            if (data.column.index == 0 || data.column.index == 1 || data.column.index == 2
              || data.column.index == 3|| data.column.index == 4){
              data.cell.styles.valign = 'middle'
              data.cell.rowSpan=2
            }
            if (data.column.index == 5 || data.column.index == 7 || data.column.index == 9){
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
              data.cell.styles.fillColor = [240, 240, 240]
            }
            if (data.column.index == 5 || data.column.index == 6 || data.column.index == 7 || data.column.index == 9 ||data.column.index == 8 || data.column.index == 10){
              data.cell.styles.fontSize = 8
            }
          }
        },
        columnStyles: {
          0: {maxHeight: 10,cellWidth: 50, halign: 'center'},
          1: {cellWidth: 30, halign: 'left'},
          2: {cellWidth: 50, halign: 'left'},
          3: {cellWidth: 60, halign: 'left'},
          4: {cellWidth: 80, halign: 'left'},
          5: {cellWidth: 50, halign: 'right'},
          6: {cellWidth: 50, halign: 'right'},
          7: {cellWidth: 50, halign: 'right'},
          8: {cellWidth: 50, halign: 'right'},
          9: {cellWidth: 50, halign: 'right'},
          10: {cellWidth: 50, halign: 'right'},
        }
      })
    })
  }
  if(pageParameter.queryMark==='SWJ'){message.warn('暂不支持 数量外币式 打印!');compState.loading = false;return false;}
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
//金额格式化
function toThousandFilter(num: any) {
  if (num == '' || num == null) {
    return ''
  }
  return (+num || 0).toFixed(2).replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,')
}
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
<style scoped>
.group-btn-span-special2{
  width: 130px;
}
.app-container-top{
  height: 90px;
}
:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
  font-weight: bold;
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
:deep(.table-month-striped) {
  background-color: honeydew;
}
:deep(.table-year-striped)  {
  background-color: lightyellow;
}
:deep(.table-odd-striped){
  background-color: #efedea;
}
</style>
