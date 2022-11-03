<template>
  <div class="app-container">
      <div class="app-container-head"><img :src="getThisIndexImg()" class="container-head-img" draggable="false" >
      <div class="container-head-title">
        <b class="noneSpan">凭证列表</b>
      </div>
      <div class="ant-btn-group">
        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="openQueryPage()"
        ><span>查询</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="ssss"
        ><span>新增</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="pingzhengEditor.openPingZhengEditEditer(tableSelectedRowObjs)"
        ><span>修改</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="accvoucherStore.verifyAccvoucherTemplateSum(()=>openImportPage())"
        ><span>导入</span></button>
        <a-popover placement="bottom">
          <template #content>
            <span class="group-btn-span-special" @click="openCashierPage(true)">出纳签字</span><br/>
            <span class="group-btn-span-special" @click="openCashierPage(false)">取消签字</span>
          </template>
          <button
            type="button"
            class="ant-btn ant-btn-me"
          ><span>出纳</span></button>
        </a-popover>
        <a-popover placement="bottom">
          <template #content>
            <span class="group-btn-span-special" @click="openReviewPage(true)">审核凭证</span><br/>
            <span class="group-btn-span-special" @click="openReviewPage(false)">弃审凭证</span>
          </template>
          <button
            type="button"
            class="ant-btn ant-btn-me"
          ><span>审核</span></button>
        </a-popover>
        <a-popover placement="bottom">
          <template #content>
            <span class="group-btn-span-special" @click="openSignPage(true)">主管签字</span><br/>
            <span class="group-btn-span-special" @click="openSignPage(false)">取消签字</span>
          </template>
          <button
            type="button"
            class="ant-btn ant-btn-me"
          ><span>主管</span></button>
        </a-popover>
        <button
          @click="openBookPage(true)"
          type="button"
          class="ant-btn ant-btn-me"
        ><span>记账</span></button>
        <a-popover placement="bottom" >
          <template #content>
            <span class="group-btn-span-special" @click="pingzhengEditor.openPingZhengChongXiangEditer(tableSelectedRowObjs)">&nbsp;冲销&emsp;&emsp;</span><br/>
            <span class="group-btn-span-special" @click="pingzhengEditor.openPingZhengCopyEditer(tableSelectedRowObjs)">&nbsp;复制&emsp;&emsp;</span><br/>
            <span class="group-btn-span-special" @click="startChangeStatus(true,1)">&nbsp;作废&emsp;&emsp;</span><br/>
            <span class="group-btn-span-special" @click="startChangeStatus(false,1)">&nbsp;取消作废</span><br/>
            <span class="group-btn-span-special" @click="startChangeStatus(true,3)">&nbsp;标错&emsp;&emsp;</span><br/>
            <span class="group-btn-span-special" @click="startChangeStatus(false,3)">&nbsp;取消标错</span><br/>
            <span class="group-btn-span-special"  @click="breakNumTidyBtn">&nbsp;整理&emsp;&emsp;</span><br/>
          </template>
          <button
            type="button"
            class="ant-btn ant-btn-me"
          ><span>更多</span></button>
        </a-popover>
        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="closeCurrent(),router.push('/home/welcome')"
        ><span>退出</span></button>
      </div>
      <div style="clear:both"/>
      <div style="margin-top: .5%;">
        <div style="display: inline-block;flfindAllPingZhengListoat: left;margin-left: 1%;font-size: 14px;width: 50%" >
          <AccountInfo
            @modify="modify"
            @register="registerInfo"
          />
          {{ pageParameter.ifUnit ? '单位' : '公司' }}代码：
          <a-input
            class="pz-unit-change"
            placeholder=""
            v-model:value="pageParameter.companyCode"
            @keyup.enter="pageSearch2"
            @keydown.space="openSelection"
            @focus="initCodeValue('close')"
            @blur="initCodeValue('set')"
          >
            <template #suffix>
              <SearchOutlined @click="openSelection"/>
            </template>
          </a-input>
          &emsp;&emsp;{{ pageParameter.ifUnit ? '单位' : '公司' }}简称：<span
          style="color: black;font-weight: bold">{{ pageParameter.companyName }}</span>
<!--          <AccountPicker v-if="showThis" theme="one" @reloadTable="pageReload2" class="account-picker-c"/>-->
          <div style="display: inline-block;float: right">
            {{ pageParameter.thisInterval.length === 17 ? '期间' : '日期' }}：<span style="color: black;font-weight: bold">{{ intervalWorking(pageParameter.thisInterval) }}</span>
          </div>
        </div>
        <div style="float: right; margin-left: 10px">
          <a-button class="ant-btn" @click="closeFilterV(),loadMark=true,pageReload()">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <a-popover placement="bottom">
            <template #content>
              <ColumnSetting @reloadColumn="reloadColumns" :dynamicColumnData="dynamicColumnData" :dynamicColumns="dynamicColumns" :lanMuData="lanMuData" :dynamicColumnDataCopy="dynamicColumnDataCopy" />
<!--              <a-popconfirm
                ok-text="确定"
                cancel-text="放弃"
                @confirm="confirm"
                @cancel="cancel">
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
                <a-button style="width: 165px;border: none">栏目设置</a-button>
              </a-popconfirm>-->
              <br/>
              <span @click="pageParameter.showRulesSize = 'MAX'"
                    :style="{backgroundColor: (pageParameter.showRulesSize==='MAX')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortDescendingOutlined
                :style="{ fontSize: '14px' }"/>&emsp;大号字体&emsp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MAX'"
                :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
              <span @click="pageParameter.showRulesSize = 'MIN'"
                    :style="{backgroundColor: (pageParameter.showRulesSize==='MIN')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortAscendingOutlined
                :style="{ fontSize: '14px' }"/>&emsp;小号字体&emsp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MIN'"
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
              <span @click="onChangeSwitch(false)"
                    :style="{backgroundColor: (pageParameter.queryMark=='2')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortDescendingOutlined
                :style="{ fontSize: '14px' }"/>&emsp;汇总&emsp;<CheckOutlined
                v-if="pageParameter.queryMark=='2'"
                :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
              <span @click="onChangeSwitch(true)"
                    :style="{backgroundColor: (pageParameter.queryMark=='1')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortAscendingOutlined
                :style="{ fontSize: '14px' }"/>&emsp;明细&emsp;<CheckOutlined
                v-if="pageParameter.queryMark=='1'"
                :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
            </template>
            <a-button>
              <PicLeftOutlined :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
          <a-popover placement="bottom">
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
          <a-popover placement="bottom">
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
          <a-button @click="()=>{
            if (!visible){ visible = true;reloadColumns();              }
            return false
          }">
            <FilterFilled :style="{ fontSize: '14px' }"/>
          </a-button>
        </div>
        <div style="float: right; position: relative">
          <!-- 搜索 -->
          <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 120px;"
                    class="special_select">
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
            style="width: 200px;border-radius: 4px"
          />
        </div>
      </div>
    </div>
    <div style="clear:both"/>
    <PageWrapper dense content-full-height fixed-height content-class="flex">
        <BasicTable
          ref="tableRef"
          :loading="loadMark"
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
          :row-selection="{ type: 'checkbox',fixed: true,selectedRowKeys: tableSelectedRowKeys, onChange: onSelectChange }"
          :scroll="{ x: totalColumnWidth,y: windowHeight }"
          :rowKey="r=>r.vouchUnCode===null?r.uniqueCode:r.vouchUnCode"
          size="small"
          @change="pageChange"
          @register="registerTable"
        >
          <template #csignAndInoId="{record,index }">
          <span style="color: blue;text-decoration: underline;cursor: pointer"
                @click="showPingZheng(record)">{{
              record.csign
            }}-{{
              voucherNoAutocomplete(record.inoId,4)
            }}</span>
            <img v-if="record.ifrag != 0" :src="'/img/voucher/voucher_'+record.ifrag+'.png'"
                 style="display: inline-block; margin-left: 2px; margin-bottom: 2px;">
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
        </BasicTable>
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
          <div :style="{height: (windowHeight)+'px',overflowY: 'auto'}">
            <div class="nc-drawer-special-div">
              <span>按照日期、凭证号和金额区间过滤</span>
            </div>
            <Collapse v-model:activeKey="collapseValue" :bordered="false">
              <CollapsePanel key="1" header="按日期范围">
                <span style="font-size: 12px;font-weight: bold">录入条件说明</span><Tooltip placement="rightTop" :color="'#f50'">
                <template #title>
                  <span>prompt text</span>
                </template>
                <ExclamationCircleOutlined style="font-size: 14px;color: red;font-weight: bold;margin-left: 1em;"/>
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
                  </div >
                  <div style="width: 48%;margin-left: 2%">
                  <span style="font-size: 12px;clear:both">
                     结束日期：
                  </span>
                    <!--                  <a-date-picker
                                        v-model:value="pageParameter.filterConditon.dateMin"
                                        format="YYYY-MM-DD"
                                        :disabled-date="disabledDate('end')"
                                      />-->
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
                <span style="font-size: 12px;font-weight: bold">录入条件说明</span><Tooltip placement="rightTop" :color="'#f50'">
                <template #title>
                  <span>prompt text</span>
                </template>
                <ExclamationCircleOutlined style="font-size: 14px;color: red;font-weight: bold;margin-left: 1em;"/>
              </Tooltip>
                <div style="display: inline-flex;width:100%;">
                  <div style="width: 48%">
                   <span style="font-size: 12px;clear:both">
                     起始凭证号：
                  </span>
                    <a-input v-model:value="pageParameter.filterConditon.pzNumberMin" placeholder=""
                             style="width: 100%;font-size: 12px"/>
                  </div >
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
                <span style="font-size: 12px;font-weight: bold">录入条件说明</span><Tooltip placement="rightTop" :color="'#f50'">
                <template #title>
                  <span>prompt text</span>
                </template>
                <ExclamationCircleOutlined style="font-size: 14px;color: red;font-weight: bold;margin-left: 1em;"/>
              </Tooltip><br>
                <div style="display: inline-flex;width:100%;">
                  <div style="width: 48%">
                   <span style="font-size: 12px;clear:both">
                     最小金额：
                  </span>
                    <a-input v-model:value="pageParameter.filterConditon.amountMin" placeholder=""
                             style="width: 100%;font-size: 12px"/>
                  </div >
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
      <Import
        @reload="pageReload(),unCheckTable()"
        @register="registerImportPage"
      />
      <Query
        @save="loadPage"
        @register="registerQueryPage"
      />
      <Sign
        @reload="pageReload(),unCheckTable()"
        @register="registerSignPage"
      />
      <Cashier
        @reload="pageReload(),unCheckTable()"
        @register="registerCashierPage"
      />
      <Review
        @reload="pageReload(),unCheckTable()"
        @register="registerReviewPage"
      />
      <Book
        @reload="pageReload(),unCheckTable()"
        @register="registerBookPage"
      />
    </PageWrapper>
  </div>
</template>
<script setup lang="ts">
import {BasicTable, useTable} from '/@/components/Table'
import {useModal} from '/@/components/Modal'
import {PageWrapper} from '/@/components/Page'
import router from '/@/router';
import {usePingZhengFillinStoreWidthOut} from '/@/store/modules/boozsoft/pingzheng-fillin';
import AccountInfo from "/@/views/boozsoft/system/accvoucher/popup/AccountInfo.vue";
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Pagination as APagination,
  Popconfirm as APopconfirm,
  Table as ATable,
  Checkbox as ACheckbox,
  message,
  Drawer as ADrawer,Collapse,Tooltip
} from "ant-design-vue"

const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const CollapsePanel = Collapse.Panel
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
  FilterFilled,
  CheckOutlined,
  EditOutlined,
  SearchOutlined,
  MacCommandOutlined,
  PrinterOutlined,
  UsbOutlined,
  UnlockTwoTone,
  LockTwoTone,ExclamationCircleOutlined
} from '@ant-design/icons-vue';
import {
  findAllAccvoucher,
  breakNumTidy,
  findDbLanMuList,
  saveLanMuList,
  startVoucherStatusProcedural, startVoucherCloseStatusProcedural
} from '/@/api/record/system/accvoucher'
import {useAccvoucherStore} from '/@/store/modules/accvoucher'
import { onMounted, reactive, ref} from "vue";
import moment, {Moment} from "moment";
import { initDynamics, assemblyDynamicColumn,voucherNoAutocomplete} from "./data";
import Import from "/@/views/boozsoft/system/accvoucher/popup/import.vue";
import Review from "/@/views/boozsoft/system/accvoucher/popup/review.vue";
import Cashier from "/@/views/boozsoft/system/accvoucher/popup/cashier.vue";
import Query from "/@/views/boozsoft/system/accvoucher/popup/query.vue";
import Sign from "/@/views/boozsoft/system/accvoucher/popup/sign.vue";
import Book from "/@/views/boozsoft/system/accvoucher/popup/book.vue";
import {cloneDeep} from "lodash-es";
import {
  getAdInfoDatas,
  getThisAdInfoData,
  initBasisTabData,
  saveVoucherData
} from "/@/api/record/system/financial-settings";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  askTask,
  compareTime, findAccCloseListByYaer,
  findByFunctionModule,
  getCurrentAccountName, hasBlank,
  offsetToStr, pointMessage,getThisIndexImg
} from "/@/api/task-api/tast-bus-api";
import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useTabs} from "/@/hooks/web/useTabs";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import ColumnSetting from "./popup/ColumnSetting.vue";
const {closeCurrent} = useTabs(router);
const {createConfirm, createWarningModal} = useMessage();
// 全局常量
const accvoucherStore = useAccvoucherStore()
// 页面变量
const pageParameter = reactive({
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
  'menuName': '凭证列表',
  'type': '',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username
}
// 表格参数
const loadMark = ref(false)
const tableSelectedRowKeys = ref([])
const tableSelectedRowObjs = ref([])
const windowWidth = (document.documentElement.clientWidth - 70)
const windowHeight = (window.innerHeight - 320)
const totalColumnWidth = ref(0)
const defaultDbName = getCurrentAccountName(true)
const defaultYear = defaultDbName.split('-')[2]
const manipulateDbName = ref('')
const collapseValue = ref(['1'])
const CrudApi = reactive({
  list: useRouteApi(findAllAccvoucher, {schemaName: manipulateDbName.value == '' ? defaultDbName : manipulateDbName.value}),
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
  /*  showSummary: true,
    summaryData: [],*/
  showIndexColumn: true, //显示序号列
  pagination: {
    pageSize: 200,
    showSizeChanger: true,
    pageSizeOptions: ['200', '500', '1000'],
    showTotal: t => `总共${t}条数据`,
  },
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
const [registerImportPage, {openModal: openImportPageM}] = useModal()
const [registerQueryPage, {openModal: openQueryPageM}] = useModal()
const [registerCashierPage, {openModal: openCashierPageM}] = useModal()
const [registerReviewPage, {openModal: openReviewPageM}] = useModal()
const [registerSignPage, {openModal: openSignPageM}] = useModal()
const [registerBookPage, {openModal: openBookPageM}] = useModal()

onMounted(() => {
  val.value.openOne = 1
  openQueryPageM(true, {
    data: val.value
  })
  document.onkeydown = function (e) {
    let evn = e || event;
    let key = evn.keyCode;
    if (evn.ctrlKey && evn.altKey && key === 72 /*&& tableSelectedRowKeys.value.length > 0*/) {
      openBookPage(false)
    }
  }
})
// 实例函数区
function money(val: any) { // 金额格式化
  if (val == null) val = ''
  val = val.toString().replace(/\$|\,/g, '')
  if (isNaN(val)) {
    val = '0'
  }
  const sign = (val === (val = Math.abs(val)))
  val = Math.floor(val * 100 + 0.50000000001)
  let cents: string = (val % 100) + ''
  val = Math.floor(val / 100).toString()
  if (cents < 10) {
    cents = '0' + cents
  }
  for (let i = 0; i < Math.floor((val.length - (1 + i)) / 3); i++) {
    val = val.substring(0, val.length - (4 * i + 3)) + ',' + val.substring(val.length - (4 * i + 3))
  }
  let re = (((sign) ? '' : '') + val + '.' + cents)
  return re == ('0.00' || '0') ? '' : re
}

const openImportPage = () => {
  let data = {schemaName: manipulateDbName.value + '-' + defaultYear}
  openImportPageM(true, {
    data: data
  })
}
const openQueryPage = () => {
  val.value.openOne = 0
  openQueryPageM(true, {
    data: val
  })
}
const openReviewPage = (flag) => {
  openReviewPageM(true, {
    data: {
      'selectedRowKeys': tableSelectedRowKeys.value,
      'yearMonths': analyzeTheYearAndAllMonths(tableSelectedRowObjs.value),
      'mark': flag,
       'accId': manipulateDbName.value + '-' + defaultYear
    }
  })
}

const openCashierPage = (flag) => {
  openCashierPageM(true, {
    data: {
      'selectedRowKeys': tableSelectedRowKeys.value,
      'yearMonths': analyzeTheYearAndAllMonths(tableSelectedRowObjs.value),
      'mark': flag
      , 'accId': manipulateDbName.value + '-' + defaultYear
    }
  })
}


const openSignPage = (flag) => {
  openSignPageM(true, {
    data: {
      'selectedRowKeys': tableSelectedRowKeys.value,
      'yearMonths': analyzeTheYearAndAllMonths(tableSelectedRowObjs.value),
      'mark': flag, 'accId': manipulateDbName.value + '-' + defaultYear
    }
  })
}

const openBookPage = (flag) => {
  openBookPageM(true, {
    data: {
      'selectedRowKeys': tableSelectedRowKeys.value,
      'yearMonths': analyzeTheYearAndAllMonths(tableSelectedRowObjs.value),
      'mark': flag, 'accId': manipulateDbName.value + '-' + defaultYear
    }
  })
}


const unCheckTable = () => {
  if (tableSelectedRowKeys.value.length > 0) {
    tableSelectedRowKeys.value = []
    tableSelectedRowObjs.value = []
  }
}
// 页面函数区
const onSelectChange = (selectedRowKeys, obj) => {
  if (pageParameter.queryMark == '1') {
    if (obj.length > 0) {
      let b = obj[0].uniqueCode
      tableSelectedRowObjs.value = getDataSource().filter(item => item.uniqueCode == b)
      tableSelectedRowKeys.value = tableSelectedRowObjs.value.map(item => item.vouchUnCode)
    }
  } else {
    tableSelectedRowObjs.value = obj;
    tableSelectedRowKeys.value = selectedRowKeys;
  }
};
const currentTableDatasource = ref([])
const pageReload = async (currentPageNum) => {
  let data = JSON.parse(JSON.stringify(pageParameter))
  // 两种处理方式
  if (true || pageParameter.condition.constant.queryType == '1') {
    data.page = null == currentPageNum ? getPaginationRef().current : currentPageNum
    data.size = getPaginationRef().pageSize
    let res = await useRouteApi(findAllAccvoucher, {schemaName: manipulateDbName.value + '-' + defaultYear})(data)
    if (res != null && res.items.length > 0) {
      setTableData([])
      loadMark.value = true
      // 关闭
      val.value.total = 1
      // 清空可能残留的数据
      currentTableDatasource.value = res.items
      setTableData(res.items)
      // 底部分页信息
      setPagination({total: res.total})
    } else {
      val.value.total = -1
    }
    loadMark.value = false
  } else {
    data.page = null == currentPageNum ? 1 : currentPageNum
    data.size = 20
    let accIds = pageParameter.condition.constant.accIds
    let tables = {}
    let len = 0
    for (let accId of accIds) {
      tables[accId] = await useRouteApi(findAllAccvoucher, {schemaName: accId + '-' + defaultYear})(data)
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
  let data = JSON.parse(JSON.stringify(pageParameter))
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  loadMark.value = true
  let res = await useRouteApi(findAllAccvoucher, {schemaName: obj.accId + '-' + obj.year})(data)
  if (res != null && res.items.length > 0) {
    manipulateDbName.value = obj.accId + '-' + obj.year
    pageParameter.companyName = obj.accNameCn == '' ? obj.accName : obj.accNameCn
    setTableData([]) // 清空可能残留的数据
    setTableData(res.items)
    // 底部分页信息
    setPagination({total: res.total})
  } else {
    pageParameter.companyCode = tempValue.value || defalutCompanyCode
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
}

const pageSearch = async () => {
  /* if (''==pageParameter.searchConditon.value.trim())return false*/
  // 搜索前校验格式
  if ('' == pageParameter.searchConditon.requirement.trim()) {
    message.warn('请选择检索条件')
    return false
  }
  // 校验完成后搜索
  loadMark.value=true
  closeFilterV()
  pageReload()
}

const pageSearch2 = async () => {
  // 检查是否存在 查看是否拥有权限 待完善
  if ((pageParameter.companyCode == tempValue.value)) return false
  let accObj = accAuthList.value.filter(item => item.coCode == pageParameter.companyCode)
  if (accObj.length == 0) {
    message.warn('代码不存在停止切换！')
    pageParameter.companyCode = defalutCompanyCode
  } else if (accObj.length > 0) {
    let iyear = pageParameter.thisInterval.substring(0, 4)
    let authObj = userAuthMap.value.filter(item => accObj[0].accId == item.accId && item.iyear == iyear)
    if (authObj.length == 0) {
      message.warn('无该代码' + iyear + '年度数据权限！')
      pageParameter.companyCode = defalutCompanyCode
    } else {
      let data = {accId: accObj[0].accId,year: iyear,accNameCn: accObj[0].accNameCn}
      pageReload2(data)
    }
  }
}

const tempValue = ref(defalutCompanyCode)

const initCodeValue = async (type) => {
  if (type == 'set') {
    if ((pageParameter.companyCode == '' || pageParameter.companyCode != '') && manipulateDbName.value == '') {
      pageParameter.companyCode = defalutCompanyCode
    } else if (manipulateDbName.value != '' && pageParameter.companyCode == '') {
      pageParameter.companyCode = tempValue.value
    }
  } else {
    tempValue.value = pageParameter.companyCode
    pageParameter.companyCode = ''
  }
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
const dynamicColumnDataCopy = ref([])
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
      dynamicColumnDataCopy.value = JSON.parse(JSON.stringify(dbList))
    } else {
      let statiList = initDynamics()['DATA' + pageParameter.queryMark]
      dynamicColumnData.value = statiList
      dynamicColumnDataCopy.value = JSON.parse(JSON.stringify(statiList))
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
      }
    })
  })
  return staticList
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
  let total = 60+50 // 选择列与序号列
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })
  if (total > windowWidth) {
    let f = 0
    if (visible.value) f = 380
    totalColumnWidth.value = Number(windowWidth) - f
    tableRef.value.$el.style.setProperty('width', (windowWidth + 20 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 380) < total) total -= (total - (windowWidth - 380))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 20) + 'px')
  }
}

/*栏目设置end*/
/*start 作废 or 标错*/

const startChangeStatus = async (flag, number) => {
  let msg = ''
  if (number === 1 && tableSelectedRowKeys.value.length === 0) {
    createWarningModal({title: '开始操作前检测', content: "请选择需要" + (flag ? '' : '取消') + "作废的凭证！"})
  } else if (number === 1 && !flag && tableSelectedRowObjs.value.filter(item => item.ifrag == 1).length === 0) {
    createWarningModal({title: '开始操作前检测', content: '选择已被作废的凭证才能进行取消作废操作！'})
    tableSelectedRowObjs.value = []
    tableSelectedRowKeys.value = []
  } else if (number === 3 && tableSelectedRowKeys.value.length === 0) {
    createWarningModal({
      title: '开始操作前检测',
      content: "请选择需要" + (flag ? '' : '取消') + "标错的凭证！",
      closable: true
    })
  } else if (number === 3 && !flag && tableSelectedRowObjs.value.filter(item => item.ifrag == 3).length === 0) {
    createWarningModal({title: '开始操作前检测', content: '选择已被标错的凭证才能进行取消标错操作！'})
    tableSelectedRowObjs.value = []
    tableSelectedRowKeys.value = []
  } else {
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
      createWarningModal({
        title: '开始操作前检测',
        content: '凭证' + closeMonth.toString() + '月份已经结账，不能进行' + flag ? '' : '取消' + number == 1 ? '作废' : '标错' + '凭证操作！'
      })
      return false
    }
    let res = await findByFunctionModule({iyear: yearMonthsObj.year})
    let checkMenu = ['月末结账']
    for (let i = 0; i < res.length; i++) {
      let item = res[i]
      if (checkMenu.indexOf(item.functionModule) != -1 && item.state == '1') {
        // 校验时间
        if (item.functionModule == '月末结账' && item.time != '' && !compareTime(offsetToStr(item.time))
          && yearMonthsObj.months.indexOf(item.imonth) != -1) { // 月末处理单独
          msg = '提示：任务冲突！操作员【' +
          item.caozuoUnique +
          '】正在进行' + item.imonth + '月份' + item.functionModule + '处理!不能进行凭证' + flag ? '' : '取消' + number == 1 ? '作废' : '标错' + '凭证操作，请销后再试，或联系财务主管清理该结账任务后再进行相关操作!'
          break;
        }
      } else if (item.functionModule == '月末结账' && item.time != '' && compareTime(offsetToStr(item.time))) {
        msg = '提示：发现操作员【' +
          item.caozuoUnique +
          '】正在进行' + item.imonth + '月份' + item.functionModule + '操作已经超过30分钟!可能发生任务异常导致任务超时，是否强制清除结账任务？'
        if (await askTask({msg: msg, taskId: item.id, year: yearMonthsObj.year})) {
          msg = ''
          continue
        } else {
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
    if (flag) {
      msg = await startVoucherStatusProcedural(data1)
    } else {
      msg = await startVoucherCloseStatusProcedural(data1)
    }
    await pointMessage({title: '处理结果', content: msg, delay: true})
    setTimeout(() => {
      unCheckTable()
      pageReload()
    }, 1000)
  }
}

/*作废 or 标错 end*/
// 已授权账套列表
const accAuthList = ref([])
// 账套授权信息
const userAuthMap = ref([])
const loadPage = (data) => {
  // 获取当前账套信息
  if (data.constant.queryType == '1') { // 账套
    let res = data.constant.thisAdInfo
    if (null != res /*&& res.independent == 0*/) { // 集团账套
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn == '' ? res.accName : res.accNameCn
      defalutCompanyCode = res.coCode
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
      manipulateDbName.value = res.accId
    } else {
      return
    }
  } else { // 集团

  }
  pageParameter.biZhong = data.constant.currency
  accAuthList.value = data.accAuthList
  userAuthMap.value = data.userAuthMap
  data.accAuthList = []
  data.userAuthMap = []
  data.constant.thisAdInfo = {}
  pageParameter.condition = data
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
const getACSetNameOrCode = (mark: string, accList: any, markType, valueType) => {
  let arr = accList.filter(item => mark == item[markType])
  if (arr.length > 0) {
    return arr[0][valueType]
  }
  return null
}

const pingzhengEditor = usePingZhengFillinStoreWidthOut()
/*账套弹框*/
const [registerInfo, {openModal: openInfoPageM}] = useModal()
const openSelection = () => {
  openInfoPageM(true, {
    data: {accAuthList: accAuthList.value}
  })
}
const modify = (code) => {
  if (pageParameter.companyCode != code) {
    pageParameter.companyCode = code
    pageSearch2()
  }
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
  let variable =  pageParameter.condition.variable
  let min
  let max
    if (!hasBlank(variable.periodStart)){
        min = moment(variable.periodStart+'-01', 'YYYY-MM-DD')
        max = moment(variable.periodEnd, 'YYYY-MM-DD').endOf('month')
    }else{
        min = moment(variable.dateStart, 'YYYY-MM-DD')
        max = moment(variable.dateEnd, 'YYYY-MM-DD')
    }
    return  current < min || current > max
};

const intervalWorking = (val) => {
  return val.replace(/-/g,'.').replace(/~/g,'-')
}

</script>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
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
:deep(.pz-unit-change) {
  width: 70px;border-radius: 4px;text-align: center;font-weight: bold; color: black;
  pointer-events: none;
  border: none;
}
.pz-unit-change :deep(.ant-input){
  font-weight: bold;
  color: black;
}
.account-picker-c{
  width: 40%;display: inline-block;font-size: 14px
}
</style>
