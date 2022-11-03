<template>
  <div class="app-container">
    <div class="app-container-top">
      <div class="app-container-top-one">
        <div class="act-one-d-left"  style="width: 9%;position: relative;">
          <div>
            <ProfileOutlined style="color: white;font-size: 50px;color: #0096c7;position: absolute; top: 15px;"/>
          </div>
        </div>
        <div class="act-one-d-title">
          <span class="acto-d-title-span">凭证记账</span>
        </div>
        <div class="act-one-d-btn-group">
          <Button class="actod-btn" @click="openQueryPage()">查询</Button>
          <Button class="actod-btn" @click="openReviewPage(true)">记账</Button>
          <Button class="actod-btn" @click="openReviewPage(false)">取消</Button>
          <Button class="actod-btn actod-btn-last" @click="closeCurrent()">退出</Button>
        </div>
      </div>
      <div class="app-container-top-two">
        <div class="act-two-d-left"  style="position: relative;">
          <AccountPicker v-if="showThis" readonly="" theme="three" @reloadTable="pageReload2"  style="position: inherit;left: 25px;"/>
        </div>
        <div class="act-two-d-center"><span>期间：</span><span style="color: black;font-weight: bold">{{
            pageParameter.thisInterval
          }}</span>
        </div>
        <div class="act-two-d-right">
          <div class="acttd-right-d-search">
            <Select v-model:value="pageParameter.searchConditon.requirement"
                    class="acttdrd-search-select">
              <template v-for="item in searchConditonList.slice(1)">
                <SelectOption v-if="item.ifShow == true" :value="item.dataIndex">
                  {{ item.title }}
                </SelectOption>
              </template>
            </Select>
            <InputSearch
              class="acttdrd-search-input"
              @search="pageSearch"
              v-model:value="pageParameter.searchConditon.value"
            />
          </div>
          <div class="acttd-right-d-btns">
            <Button class="acttdrd-btn" @click="closeFilterV(),loadMark=true,pageReload()">
              <SyncOutlined :style="{ fontSize: '14px' }"/>
            </Button>
            <Popover placement="bottom">
              <template #content>
                <Popconfirm
                  ok-text="保存"
                  cancel-text="关闭"
                  @confirm="confirm"
                  placement="leftTop"
                  @cancel="cancel">
                  <template #icon><b>栏目设置</b><br></template>
                  <template #title>
                    <div style="width: 640px">
                      <Table bordered :data-source="dynamicColumnData" :columns="dynamicColumns"
                             childrenColumnName="children" :pagination="false"
                             style="max-height: 500px;overflow-y: auto" class="lanmu-table">
                        <template #checkBox="{ text, record }">
                          <Checkbox v-model:checked="record.check" :disabled="record.isFixed"/>
                        </template>
                        <template #widthInput="{ text, record }">
                          <div class="editable-cell">
                            <div v-if="editableData[record.key]"
                                 class="editable-cell-input-wrapper">
                              <Input type="number" v-model:value="editableData[record.key].width"
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
                            <div v-if="editableData[record.key]"
                                 class="editable-cell-input-wrapper">
                              <Input type="text" v-model:value="editableData[record.key].nameNew"
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
                          <RadioGroup default-value="a" size="small" v-model:value="record.align"
                                      :disabled="record.align==''">
                            <RadioButton value="left">
                              左
                            </RadioButton>
                            <RadioButton value="center">
                              中
                            </RadioButton>
                            <RadioButton value="right">
                              右
                            </RadioButton>
                          </RadioGroup>
                        </template>
                      </Table>
                    </div>
                  </template>
                  <Button style="width: 120px;margin-bottom: 2px">栏目设置</Button>
                </Popconfirm>
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
              <Button class="acttdrd-btn">
                <SettingFilled :style="{ fontSize: '14px' }"/>
              </Button>
            </Popover>
            <Popover placement="bottom">
              <template #content>
               <span class="group-btn-span-special2" @click="onChangeSwitch(false)"
                     :style="pageParameter.queryMark=='2'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;&emsp;汇总&emsp;&ensp;<CheckOutlined
                 v-if="pageParameter.queryMark=='2'"/></span><br/>
                <span class="group-btn-span-special2" @click="onChangeSwitch(true)"
                      :style="pageParameter.queryMark=='1'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;&emsp;明细&emsp;&ensp;<CheckOutlined
                  v-if="pageParameter.queryMark=='1'"/></span>
              </template>
              <Button class="acttdrd-btn">
                <PicLeftOutlined :style="{ fontSize: '14px' }"/>
              </Button>
            </Popover>
            <Popover placement="bottom">
              <template #content>
                <span class="group-btn-span-special"
                      @click="pingzhengExport('curr')">导出当前</span><br/>
                <span class="group-btn-span-special">条件导出</span>
              </template>
              <Button class="acttdrd-btn">
                <UsbOutlined :style="{ fontSize: '14px' }"/>
              </Button>
            </Popover>
            <Popover placement="bottom">
              <template #content>
                <span class="group-btn-span-special" @click="openPrintPage('1')">选择打印</span><br/>
                <span class="group-btn-span-special" @click="openPrintPage('3')">条件打印</span><br/>
                <span class="group-btn-span-special">打印列表</span>
              </template>
              <Button class="acttdrd-btn">
                <PrinterOutlined :style="{ fontSize: '14px' }"/>
              </Button>
            </Popover>
            <Button class="acttdrd-btn"
                    @click="()=>{if (!visible){visible = true;reloadColumns();}return false}">
              <FilterFilled :style="{ fontSize: '14px' }"/>
            </Button>
            <!--            <Button class="acttdrd-btn"
                                @click="()=>{showTree = !showTree;reloadColumns();return false}">
                          <FileSearchOutlined :style="{ fontSize: '14px' }"/>
                        </Button>-->
          </div>
        </div>
      </div>
    </div>
    <div class="app-container-bottom"  :style="{height: (windowHeight+60)+'px',marginTop: '5px'}">
      <div style="position: relative;">
        <BasicTable
          ref="tableRef"
          :loading="loadMark"
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
          :row-selection="{ type: 'checkbox',fixed: true,selectedRowKeys: tableSelectedRowKeys,   onSelect: onSelectChange,onSelectAll: onSelectChange2,getCheckboxProps: (record) => ({disabled: record.inoId == null}) }"
          :scroll="{ x: totalColumnWidth,y: windowHeight-10 }"
          :rowKey="r=>r.vouchUnCode===null?r.uniqueCode:r.vouchUnCode"
          size="small"
          @change="pageChange"
          @register="registerTable"
        >
          <template  #ibook="{ record }">
            <a-tag v-if="record.inoId!=null" :color="record.ibook === '1' ? 'green' : 'red'">
              {{record.ibook === '1' ? '已记账' :  '未记账' }}
            </a-tag>
          </template>
          <template #csignAndInoId="{record,index }">
          <span v-if="record.inoId!=null" style="color: blue;text-decoration: underline;cursor: pointer"
                @click="showPingZheng(record)">{{
              record.csign
            }}-{{
              voucherNoAutocomplete(record.inoId, voucherRuleSize)
            }}</span>&ensp;
            <a-tag v-if="record.ifrag != 0 && record.inoId!=null" :color="record.ifrag === '1' ? 'blue' : record.ifrag === '2' ? 'pink' : 'red'">
              {{ record.ifrag === '1' ? '作废' : record.ifrag === '2' ? '暂存' : record.ifrag === '3' ? '错误' : '错误' }}</a-tag>
          </template>
          <template #mdmoney="{ record }">
        <span class="a-table-money-font-size a-table-font-arial" :style="null!=record.md && record.md.startsWith('-')?{color: 'red'}:{}">
         {{ money(record.md) }}
        </span>
          </template>
          <template #mcmoney="{ record }">
        <span class="a-table-money-font-size a-table-font-arial" :style="null!=record.mc && record.mc.startsWith('-')?{color: 'red'}:{}">
         {{ money(record.mc) }}
        </span>
          </template>
        </BasicTable>
      <div class="pagination-text" :style="{top: (windowHeight+30)+'px'}" v-show="showPaginationText">
        {{`共 ${paginationNumber}条记录&emsp;每页 200 条`}}
      </div>
        </div>
        <a-drawer
          title="区间条件过滤"
          placement="right"
          :closable="true"
          v-if="visible"
          :mask="false"
          width="380px"
          :visible="visible"
          :get-container="false"
          :wrap-style="{ position: 'absolute' }"
          @close="visible=false,reloadColumns()"
        >
          <div :style="{height: (windowHeight-25)+'px',overflowY: 'auto'}">
            <div class="nc-drawer-special-div">
              <span>按照日期、凭证号和金额区间过滤</span>
            </div>
            <Collapse v-model:activeKey="collapseValue" :bordered="false">
              <CollapsePanel key="1" header="按日期范围">
                <span style="font-size: 12px;font-weight: bold">录入条件说明</span>
                <Tooltip placement="rightTop" :color="'#f50'">
                  <template #title>
                    <span>prompt text</span>
                  </template>
                  <ExclamationCircleOutlined
                    style="font-size: 14px;color: red;font-weight: bold;margin-left: 1em;"/>
                </Tooltip>
                <div style="display: inline-flex;width:100%;">
                  <div style="width: 48%">
                   <span style="font-size: 12px;clear:both">
                     开始日期：
                  </span>
                    <a-date-picker
                      v-model:value="pageParameter.filterConditon.dateMin"
                      format="YYYY-MM-DD"
                      :disabled-date="disabledDate"
                      style="width: 100%;font-size: 12px"
                    />
                  </div>
                  <div style="width: 48%;margin-left: 2%">
                  <span style="font-size: 12px;clear:both">
                     结束日期：
                  </span>
                    <a-date-picker
                      v-model:value="pageParameter.filterConditon.dateMax"
                      format="YYYY-MM-DD"
                      :disabled-date="disabledDate"
                      style="width: 100%;font-size: 12px;float: right"
                    />
                  </div>
                </div>
              </CollapsePanel>
              <CollapsePanel key="2" header="按凭证号范围">
                <span style="font-size: 12px;font-weight: bold">录入条件说明</span>
                <Tooltip placement="rightTop" :color="'#f50'">
                  <template #title>
                    <span>prompt text</span>
                  </template>
                  <ExclamationCircleOutlined
                    style="font-size: 14px;color: red;font-weight: bold;margin-left: 1em;"/>
                </Tooltip>
                <div style="display: inline-flex;width:100%;">
                  <div style="width: 48%">
                   <span style="font-size: 12px;clear:both">
                     起始凭证号：
                  </span>
                    <a-input v-model:value="pageParameter.filterConditon.pzNumberMin" placeholder=""
                             style="width: 100%;font-size: 12px"/>
                  </div>
                  <div style="width: 48%;margin-left: 2%">
                  <span style="font-size: 12px;clear:both">
                     结束凭证号：
                  </span>
                    <a-input v-model:value="pageParameter.filterConditon.pzNumberMax" placeholder=""
                             style="width: 100%;font-size: 12px;float: right"/>
                  </div>
                </div>
              </CollapsePanel>
              <CollapsePanel key="3" header="按金额范围" :disabled="false">
                <span style="font-size: 12px;font-weight: bold">录入条件说明</span>
                <Tooltip placement="rightTop" :color="'#f50'">
                  <template #title>
                    <span>prompt text</span>
                  </template>
                  <ExclamationCircleOutlined
                    style="font-size: 14px;color: red;font-weight: bold;margin-left: 1em;"/>
                </Tooltip>
                <br>
                <div style="display: inline-flex;width:100%;">
                  <div style="width: 48%">
                   <span style="font-size: 12px;clear:both">
                     最小金额：
                  </span>
                    <a-input v-model:value="pageParameter.filterConditon.amountMin" placeholder=""
                             style="width: 100%;font-size: 12px"/>
                  </div>
                  <div style="width: 48%;margin-left: 2%">
                  <span style="font-size: 12px;clear:both">
                     最大金额：
                  </span>
                    <a-input v-model:value="pageParameter.filterConditon.amountMax" placeholder=""
                             style="width: 100%;font-size: 12px;float: right"/>
                  </div>
                </div>
              </CollapsePanel>
            </Collapse>
            <br/>
            <span style="font-size: 14px;font-weight: bold">包含列</span><br/>
            <ul>
              <li>
                <span>凭证日期</span><br>
                <span>当前凭证列表范围内地凭证区间，一般小于当前凭证列表区间</span>
              </li>
              <li>
                <span>凭证号</span><br>
                <span>当前凭证列表的凭证号区间，一般小于当前凭证列表区间</span>
              </li>
              <li>
                <span>凭证金额</span><br>
                <span>根据当前显示样式：“明细”或“汇总” 方式，判断金额大小范围区间</span>
              </li>
            </ul>
            <a-button type="primary" style="float: right;margin-top: 10%" @click="filterSearch">
              过滤
            </a-button>
          </div>
        </a-drawer>
        <Query
          @save="loadPage"
          @register="registerQueryPage"
        />
        <Review
          @reload="pageReload(),unCheckTable()"
          @register="registerReviewPage"
        />
      </div>
  </div>
</template>
<script setup lang="ts">
import {BasicTable, useTable} from '/@/components/Table'
import {useModal} from '/@/components/Modal'
import {PageWrapper} from '/@/components/Page'
import router from '/@/router';
import {usePingZhengFillinStoreWidthOut} from '/@/store/modules/boozsoft/pingzheng-fillin';
import {
  DatePicker as ADatePicker,
  Select,
  Input,
  Popover,
  Table ,
  Radio ,
  Popconfirm ,
  Button,
  message,Checkbox,
  Drawer as ADrawer, Collapse, Tooltip, Tag as ATag
} from "ant-design-vue"

const ARangePicker = ADatePicker.RangePicker
const SelectOption = Select.Option
const InputSearch = Input.Search
const RadioGroup = Radio.Group
const RadioButton = Radio.Button
const CollapsePanel = Collapse.Panel
import {
  SortDescendingOutlined,
  SortAscendingOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  FilterFilled,
  CheckOutlined,
  SearchOutlined,
  PrinterOutlined,
  UsbOutlined, EditOutlined,
  ExclamationCircleOutlined,ProfileOutlined
} from '@ant-design/icons-vue';
import {
  findAllAccvoucher,
  breakNumTidy,
  findDbLanMuList,
  saveLanMuList,
   startVoucherBookProcedural
} from '/@/api/record/system/accvoucher'
import {useAccvoucherStore} from '/@/store/modules/accvoucher'
import {onMounted, reactive, ref} from "vue";
import moment, {Moment} from "moment";
import {
  initDynamics,
  assemblyDynamicColumn,
  voucherNoAutocomplete,
  isRealNum,
  money,
  combineParameters,
  intervalWorking
} from "/@/views/boozsoft/system/accvoucher/data";
import Review from "/@/views/boozsoft/system/accvoucher-book/popup/book.vue";
import Query from "/@/views/boozsoft/system/accvoucher-book/popup/query.vue";
import {cloneDeep} from "lodash-es";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  getCurrentAccountName, hasBlank,
  getThisIndexImg
} from "/@/api/task-api/tast-bus-api";
import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useTabs} from "/@/hooks/web/useTabs";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";

const {closeCurrent} = useTabs(router);
const {createConfirm, createWarningModal} = useMessage();
// 全局常量
const accvoucherStore = useAccvoucherStore()
// 页面变量
const pageParameter = reactive({
  queryMark: '1',
  showRulesSize: 'MIN',
  thisInterval: '2020-01 ~ 2021-12',
  biZhong: 'book',
  companyCode: '100',
  ifUnit: false,
  companyName: '湖北万亚软件技术有限公司',
  condition: {},
  searchConditon: {
    requirement: 'inoId',
    value: '',
  },
  filterConditon: {
    dateMin: '',
    dateMax: '',
    amountMin: '',
    amountMax: '',
    pzNumberMin: '',
    pzNumberMax: '',
  }
})
let defalutCompanyCode = ''
const val = ref({
  openOne: 0,
  total: 0
})
const tableMap = ref({})
const accIdList = ref({})

const userStore = useUserStore();
const defaultPage = ref(true) // 默认为独立账套
const visible = ref(false);
const searchConditonList = ref([])
const lanMuData = {
  'accId': '',
  'menuName': '凭证记账列表',
  'type': '',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username
}
// 表格参数
const loadMark = ref(false)
const tableSelectedRowKeys = ref([])
const tableSelectedRowObjs = ref([])
const windowWidth = (document.documentElement.clientWidth - 70)
const windowHeight = (window.innerHeight - 300)
const totalColumnWidth = ref(0)
const manipulateDbName = ref('')
const collapseValue = ref(['1'])
const CrudApi = reactive({
  columns: JSON.parse(JSON.stringify(accvoucherStore.getColumns(pageParameter.queryMark))).slice(0, 10)
})
const tableRef = ref(null)
// 组件实例区
const [registerTable, {
  setColumns,
  getColumns,
  getDataSource,
  setTableData,
  setPagination,
  getPaginationRef
}] = useTable({
  columns: CrudApi.columns,
  bordered: true,
  immediate: false,
  canResize: true,
  showIndexColumn: true, //显示序号列
  indexColumnProps: {width: 60,fixed: 'left'},
  pagination: {
    pageSize: 200,
    simple: true,
  }
})
const [registerQueryPage, {openModal: openQueryPageM}] = useModal()
const [registerReviewPage, {openModal: openReviewPageM}] = useModal()

onMounted(() => {
  val.value.openOne = 1
  openQueryPageM(true, {
    data: val.value
  })
  document.onkeydown = function (e) {
    let evn = e || event;
    let key = evn.keyCode;
    if (evn.ctrlKey && evn.altKey && key === 72 && tableSelectedRowKeys.value.length > 0) {
      openReviewPage(false)
    }
  }
})
// 实例函数区

const openQueryPage = () => {
  val.value.openOne = 0
  openQueryPageM(true, {
    data: val
  })
}

import {checkMeetSpecifyConditions} from "/@/views/boozsoft/system/accvoucher-book/bus";
import {getGlobalFinanceSettingInfo,assemblyPingZhengApproveCanshu} from "/@/api/record/system/financial-settings";

const openReviewPage = async (flag) => {
  // 必选
  if (tableSelectedRowKeys.value.length === 0) {
    message.warning('至少选择一张凭证！')
    return false;
  }
  // 开始校验
  let data = {
    scopeCondition: flag?1:0,
    operatorUserName: userStore.getUserInfo.realName,
    batchCondition: {},
    financialParameters: financialParameters.value,
    selectedRowKeys: [], thisInterval: pageParameter.thisInterval
  }
  data.selectedRowKeys = [...new Set(tableSelectedRowObjs.value.map(item => item.uniqueCode))]
  if (await checkMeetSpecifyConditions(data, analyzeTheYearAndAllMonths(tableSelectedRowObjs.value),flag,manipulateDbName.value)) {
    let res = null
    loadMark.value = true
    res = await useRouteApi(startVoucherBookProcedural, {schemaName: manipulateDbName.value})(data)
    loadMark.value = false
    if (null != res && Object.keys(res).length == 5) {
      openReviewPageM(true, {
        data: {
          'mark': flag,
          'voucherRuleSize': voucherRuleSize,
          'result': res
        }
      })
      if(res.successNumber > 0)
      pageReload()
    }
    tableSelectedRowKeys.value = []
    tableSelectedRowObjs.value = []
  }
}
const unCheckTable = () => {
  if (tableSelectedRowKeys.value.length > 0) {
    tableSelectedRowKeys.value = []
    tableSelectedRowObjs.value = []
  }
}
const onSelectChange = (record, selected, obj, nativeEvent) => {
  let selectedRowKeys = obj.map(it => pageParameter.queryMark == '1'?it.vouchUnCode:it.uniqueCode)
  if (pageParameter.queryMark == '1' && selectedRowKeys.length > 0) {
    if (selectedRowKeys.length > tableSelectedRowKeys.value.length) { // 选中
      let list = getDataSource()
      let codes = obj.map(item => item.uniqueCode);
      let b = [...new Set(codes)]
      tableSelectedRowObjs.value = list.filter(item => b.indexOf(item.uniqueCode) != -1)
    } else { // 取消勾选 先 找到被取消的
      let b = [...new Set(tableSelectedRowObjs.value.filter(item => selectedRowKeys.indexOf(item.vouchUnCode) == -1).map(item => item.uniqueCode))]
      tableSelectedRowObjs.value = tableSelectedRowObjs.value.filter(item => b.indexOf(item.uniqueCode) == -1)
    }
    tableSelectedRowKeys.value = tableSelectedRowObjs.value.map(item => item.vouchUnCode)
  } else {
    let list = getDataSource()
    tableSelectedRowObjs.value = list.filter(it => selectedRowKeys.indexOf(it.uniqueCode) != -1);
    tableSelectedRowKeys.value = selectedRowKeys;
  }
}

const onSelectChange2 = (selected, selectedRows, changeRows) => {
  if (selected) {
    tableSelectedRowObjs.value = selectedRows
    tableSelectedRowKeys.value = tableSelectedRowObjs.value.map(item => item.vouchUnCode)
  } else {
    tableSelectedRowObjs.value = []
    tableSelectedRowKeys.value = []
  }
}
const currentTableDatasource = ref([])
const paginationNumber = ref(0)
const showPaginationText = ref(false)
const pageReload = async (currentPageNum) => {
  let data = JSON.parse(JSON.stringify(pageParameter))
  // 两种处理方式
  showPaginationText.value = false
  if (true || pageParameter.condition.constant.queryType == '1') {
    data.page = null == currentPageNum ? getPaginationRef().current : currentPageNum
    data.size = getPaginationRef().pageSize
    let res = await useRouteApi(findAllAccvoucher, {schemaName: manipulateDbName.value })(data)
    if (res != null && res.items.length > 0) {
      setTableData([])
      loadMark.value = true
      // 关闭
      val.value.total = 1
      // 清空可能残留的数据
      currentTableDatasource.value = res.items
      let arr = res.items
      let num = 30 - arr.length
      if (num > 0) for (let i=0;i<num;i++) arr.push({})
      setTableData(arr)
      // 底部分页信息
      setPagination({total: res.total})
    } else {
      val.value.total = -1
    }
    loadMark.value = false
    paginationNumber.value = res?.total || 0
    if ( paginationNumber.value > 0)  showPaginationText.value = true
  } else {
    data.page = null == currentPageNum ? 1 : currentPageNum
    data.size = 20
    let accIds = pageParameter.condition.constant.accIds
    let tables = {}
    let len = 0
    for (let accId of accIds) {
      tables[accId] = await useRouteApi(findAllAccvoucher, {schemaName: accId + '-' + manipulateDbName.value.split('-')[2]})(data)
      if (tables[accId].total > 0) len++
    }
    if (len > 0) {
      tableMap.value = tables
      accIdList.value = pageParameter.condition.constant.accIds
      val.value.total = 1
    } else {
      val.value.total = -1
    }
    loadMark.value = false
  }
  showThis.value = true
}
const showThis = ref(false)
const pageReload2 = async (obj) => {
  return false
  let data = JSON.parse(JSON.stringify(pageParameter))
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  loadMark.value = true
  let res = await useRouteApi(findAllAccvoucher, {schemaName: obj.accId + '-' + manipulateDbName.value.split('-')[2]})(data)
  if (res != null && res.items.length > 0) {
    manipulateDbName.value = obj.accId + '-' + obj.year
    voucherRuleSize = obj.accvouchDec || '4'
    setTableData([]) // 清空可能残留的数据
    setTableData(res.items)
    // 底部分页信息
    setPagination({total: res.total})
  } else {
    createWarningModal({title: '温馨提示', content: '暂无任何数据！'});
  }
  loadMark.value = false
}

const breakNumTidyBtn = async () => {
  breakNumTidy()
  pageReload()
}

const onChangeSwitch = async (v) => { // 动态列
  pageParameter.queryMark = v ? '1' : '2'
  pageParameter.searchConditon.requirement = 'inoId'
  pageParameter.searchConditon.value = ''
  loadMark.value = true
  resetDynamicColumnData()
  tableSelectedRowKeys.value = []
  tableSelectedRowObjs.value = []
}

const pageSearch = async () => {
  /* if (''==pageParameter.searchConditon.value.trim())return false*/
  // 搜索前校验格式
  if ('' == pageParameter.searchConditon.requirement.trim()) {
    message.warn('请选择检索条件')
    return false
  }
  // 校验完成后搜索
  loadMark.value = true
  closeFilterV()
  pageReload()
}

const filterSearch = async () => {
  /* if (''==pageParameter.searchConditon.value.trim())return false*/
  // 搜索前校验格式
  let min = pageParameter.filterConditon.amountMin.trim()
  let max = pageParameter.filterConditon.amountMax.trim()
  let pzMin = pageParameter.filterConditon.pzNumberMin.trim()
  let pzMax = pageParameter.filterConditon.pzNumberMax.trim()
  if ('' == min && max != '' || '' == max && min != '') {
    message.warn('请完善金额区间过滤条件！')
    return false
  }
  if (min != '' && max != '' && (!isRealNum(min) || !isRealNum(max) || parseFloat(min) == 0 || parseFloat(max) == 0)) {
    message.warn('请输入数值类型值并且值不能为0！')
    closeFilterV()
    return false
  } else if (parseFloat(min) > parseFloat(max)) {
    message.warn('金额区间最小值不能大于最大值！')
    closeFilterV()
    return false
  }
  if ('' == pzMin && pzMax != '' || '' == pzMax && pzMin != '') {
    message.warn('请完善凭证区间过滤条件！')
    return false
  }
  if (parseFloat(pzMin) > parseFloat(pzMax)) {
    message.warn('凭证区间最小值不能大于最大值！')
    closeFilterV()
    return false
  }

  // 校验完成后搜索
  pageReload()
}

const closeFilterV = () => {
  pageParameter.filterConditon.amountMin = ''
  pageParameter.filterConditon.amountMax = ''
  visible.value = false
  reloadColumns()
}


const analyzeTheYearAndAllMonths = (list: any) => {
  let months = []
  if (list.length === 0) return {year: '', months: months}
  list.forEach(item => {
    let thisMonth = item.dbillDate.split('-')[1]
    if (months.indexOf(thisMonth) == -1) {
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
    onOk: async () => {
      // 调整数据库 列参数
      lanMuData.accId = getCurrentAccountName(false)
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
  lanMuData.type = pageParameter.queryMark == '1' ? '明细' : '汇总'
  findDbLanMuList(lanMuData).then(res => {
    // 栏目列
    let dbList = res.items
    if (dbList.length > 0) {
      let statiList = initDynamics()['DATA' + pageParameter.queryMark]
      dbList = combineParameters(statiList, dbList)
      dynamicColumnData.value = dbList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(dbList))
    } else {
      let statiList = initDynamics()['DATA' + pageParameter.queryMark]
      dynamicColumnData.value = statiList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(statiList))
    }
    // 表格列
    reloadColumns()
    pageReload()
  })
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
  if (defaultPage.value) return false
  let newA = JSON.parse(JSON.stringify(accvoucherStore.getColumns(pageParameter.queryMark)))
  newA = assemblyDynamicColumn(dynamicColumnData.value, newA)
  setColumns(newA)
  initTableWidth(newA)
  searchConditonList.value = newA
}

function initTableWidth(thisCs) {
  let total = 60 + 50 // 选择列与序号列
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })
  if (total > windowWidth) {
    let f = 0
    if (visible.value) f = 380
    totalColumnWidth.value = Number(windowWidth) - f
    tableRef.value.$el.style.setProperty('width', (windowWidth + 42 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 380) < total) total -= (total - (windowWidth - 380))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 42) + 'px')
  }
}

/*栏目设置end*/
// 已授权账套列表
const accAuthList = ref([])
// 账套授权信息
const userAuthMap = ref([])
const financialParameters = ref({})
let voucherRuleSize = '4';

const loadPage = async (data) => {
  // 获取当前账套信息
  if (data.constant.queryType == '1') { // 账套
    let res = data.constant.thisAdInfo
    manipulateDbName.value = data.constant.tenantId
    if (null != res /*&& res.independent == 0*/) { // 集团账套
      defaultPage.value = false
      pageParameter.ifUnit = res.icorp == '1'
      voucherRuleSize = res.accvouchDec || '4'
      let settings =  await getGlobalFinanceSettingInfo( manipulateDbName.value.substring(0, manipulateDbName.value.length-5))
      financialParameters.value =  assemblyPingZhengApproveCanshu(settings)
    } else {
      return
    }
  } else { // 集团

  }
  //pageParameter.biZhong = data.constant.currency
  accAuthList.value = data.accAuthList
  userAuthMap.value = data.userAuthMap
  data.accAuthList = []
  data.userAuthMap = []
  data.constant.thisAdInfo = {}
  pageParameter.condition = data
  pageParameter.queryMark = data.constant.showType
  if (data.variable.periodStart == '' && data.variable.periodEnd == '') {
    pageParameter.thisInterval = data.variable.dateStart + ' ~ ' + data.variable.dateEnd
  } else {
    pageParameter.thisInterval = data.variable.periodStart + ' ~ ' + data.variable.periodEnd
  }
  loadMark.value = true
  if (data.openOne == 1) { // 第一次初始化 + 条件查询
    resetDynamicColumnData()
  } else { // 查询条件查询
    closeFilterV()
    pageParameter.searchConditon.value = ''
    pageReload()
  }
}
/**
 *  根据指定条件  账套列表
 * */
const pingzhengEditor = usePingZhengFillinStoreWidthOut()
/*账套弹框*/
const [registerInfo, {openModal: openInfoPageM}] = useModal()
const openSelection = () => {
  openInfoPageM(true, {
    data: {accAuthList: accAuthList.value}
  })
}

/* 对接 查看凭证*/
const showPingZheng = (obj: any) => {
  if (null != obj) {
    let b = obj.uniqueCode
    let thisTableSelectedRowObjs = getDataSource().filter(item => item.uniqueCode == b)
    pingzhengEditor.openPingZhengShowEditer(thisTableSelectedRowObjs)
  }
}

const pageChange = (a) => {
  // 分页改变
  loadMark.value = true
  pageReload(a.current)
}
/*动态加载*/
/*const pageC=ref(1)
onMounted(() => {
  const selector = document.querySelector('.a-table-font-size-16  .ant-table-body')
  selector.addEventListener('scroll', (e) => {
    function isLast(){
      return e.srcElement.scrollTop+e.srcElement.offsetHeight==e.srcElement.scrollHeight
    }
    if(isLast()){
      setTableData(currentTableDatasource.value.slice(0, ++pageC.value*30))
      console.log('触发底部')
    }
  })
})*/
/*抽屉*/

const disabledDate = (current) => {
  // 获取区间最小区间
  let variable = pageParameter.condition.variable
  let min
  let max
  if (!hasBlank(variable.periodStart)) {
    min = moment(variable.periodStart + '-01', 'YYYY-MM-DD')
    max = moment(variable.periodEnd, 'YYYY-MM-DD').endOf('month')
  } else {
    min = moment(variable.dateStart, 'YYYY-MM-DD')
    max = moment(variable.dateEnd, 'YYYY-MM-DD')
  }
  return current < min || current > max
};
</script>
<style scoped="scoped" lang="less">
@import '/@/assets/styles/pingzheng-menu-index.less';
:deep(.account-picker){
  >div{
    top: -22px;
  }
}
:deep(.vben-basic-table) .ant-pagination {
  padding: 0 10px 10px;
}
:deep(.pagination-text){
  position: absolute;
  bottom: 10px;
  right: 200px;
  font-size: 13px;
  color: black;
  z-index: 50;
}
</style>
