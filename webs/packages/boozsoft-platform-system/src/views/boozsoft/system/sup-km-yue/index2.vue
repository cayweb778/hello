<template>
  <div>
    <div class="app-container">
      <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
      <div class="app-container-top">
        <div style="width: 100%;margin-top: 5px;height: 100px;">
          <div style="width: 40%;padding-top:20px;">
            <div>
              <AccountPicker theme="one" readonly="" @reloadTable="dynamicAdReload" style="margin-top: -5px;"/>
            </div>
            <div style="float: left;margin-left:6px;margin-top: 10px">
              <span style="font-size: 14px;">供应商：</span>
              <a-select
                show-search
                placeholder="供应商选择"
                option-filter-prop="children"
                style="width: 150px;display: inline-block;"
                @change="handleChangeMinKm"
                v-model:value="pageParameter.minDept"
              >
                <a-select-option v-for="d in deptList" :value="d.uniqueCode">
                  {{ d.custName }}
                </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
              </a-select>
              &emsp;本位币：<span style="font-weight: bold">{{ bwb }} </span>
              &emsp;样式：<span style="color: black;font-weight: bold">{{ styleName }}</span>
            </div>
          </div>
          <div style="width: 60%;float: right;margin-top: -40px;">
            <span style="font-size: 24px;color: #0096c7;font-weight: bold;">供应商科目余额表</span>
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
            <span style="display: block;color: black;font-size: 14px;margin-left: 10px;margin-top: 5px;">期间：{{ time.strDate }} - {{ time.endDate }}
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
              </div>
            </span>
          </span>
          </div>
        </div>
      </div>
    </div>
    <div class="app-container" style="margin-top: -40px;">
      <div class="app-container-bottom">
        <BasicTable
            ref="tableRef"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :scroll="{ x: totalColumnWidth,y: windowHeight }"
            size="small"
            @register="registerTable"
            @fetch-success="fetchSuccess"
          >
            <template #ccode="{ record }">
          <span v-if="record.ccodeName!=null">
            {{record.ccode}}
          </span>
              <span v-if="record.ccodeName==null"> {{record.ccode}}  </span>
            </template>
            <template #ccodeName="{ record }">
              {{record.ccodeName}}
            </template>
            <template #qcMd="{ record }">
          <span v-if="record.qcMd!=0">
            <a style="cursor: pointer;" @click="goRouter(record)">{{ moneyformat(record.qcMd) }}</a>
          </span>
            </template>
            <template #qcMc="{ record }">
          <span v-if="record.qcMc!=0">
            <a style="cursor: pointer;" @click="goRouter(record)">{{ moneyformat(record.qcMc) }}</a>
          </span>
            </template>
            <template #qcNum="{ record }">
              <span v-if="record.qcNum!=0">{{ formatNum(record.qcNum) }}</span>
            </template>
            <template #qcNfrat="{ record }">
              <span v-if="record.qcNfrat!=0">{{ moneyformat(record.qcNfrat) }}</span>
            </template>

            <template #pzMd="{ record }">
          <span v-if="record.pzMd!=0">
            <a style="cursor: pointer;" @click="goRouter(record)">{{ moneyformat(record.pzMd) }}</a>
          </span>
            </template>
            <template #pzMc="{ record }">
          <span v-if="record.pzMc!=0">
            <a style="cursor: pointer;" @click="goRouter(record)">{{ moneyformat(record.pzMc) }}</a>
          </span>
            </template>
            <template #pzNum="{ record }">
          <span v-if="record.pzNum!=0">
            <a style="cursor: pointer;" @click="goRouter(record)">{{ formatNum(record.pzNum) }}</a>
          </span>
            </template>
            <template #pzNfrat="{ record }">
          <span v-if="record.pzNfrat!=0">
            <a style="cursor: pointer;" @click="goRouter(record)">{{ moneyformat(record.pzNfrat) }}</a>
          </span>
            </template>

            <template #ljMd="{ record }">
          <span v-if="record.ljMd!=0">
            <a style="cursor: pointer;" @click="goRouter(record)">{{ moneyformat(record.ljMd) }}</a>
          </span>
            </template>
            <template #ljMc="{ record }">
          <span v-if="record.ljMc!=0">
             <a style="cursor: pointer;" @click="goRouter(record)">{{ moneyformat(record.ljMc) }}</a>
          </span>
            </template>
            <template #ljNum="{ record }">
          <span v-if="record.ljNum!=0">
            <a style="cursor: pointer;" @click="goRouter(record)">{{ formatNum(record.ljNum) }}</a>
          </span>
            </template>
            <template #ljNfrat="{ record }">
          <span v-if="record.ljNfrat!=0">
            <a style="cursor: pointer;" @click="goRouter(record)">{{ moneyformat(record.ljNfrat) }}</a>
          </span>
            </template>

            <template #qmMd="{ record }">
          <span v-if="record.qmMd!=0">
            <a style="cursor: pointer;" @click="goRouter(record)">{{ moneyformat(record.qmMd) }}</a>
          </span>
            </template>
            <template #qmMc="{ record }">
          <span v-if="record.qmMc!=0">
            <a style="cursor: pointer;" @click="goRouter(record)">{{ moneyformat(record.qmMc) }}</a>
          </span>
            </template>
            <template #qmNum="{ record }">
          <span v-if="record.qmNum!=0">
            <a style="cursor: pointer;" @click="goRouter(record)">{{ formatNum(record.qmNum) }}</a>
          </span>
            </template>
            <template #qmNfrat="{ record }">
          <span v-if="record.qmNfrat!=0">
            <a style="cursor: pointer;" @click="goRouter(record)">{{ moneyformat(record.qmNfrat) }}</a>
          </span>
            </template>

            <template #summary>
            <TableSummary fixed>
              <TableSummaryRow style="background-color: #cccccc;" v-if="pageParameter.queryMark=='J'">
                <TableSummaryCell class="nc-summary" :index="0" :align="'left'" :colSpan="3" style="border-right: none;">合计</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcMd}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcMc}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzMd}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzMc}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmMd}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmMc}}</TableSummaryCell>
              </TableSummaryRow>

              <TableSummaryRow style="background-color: #cccccc;" v-if="pageParameter.queryMark=='SJ' || pageParameter.queryMark=='WJ'">
                <TableSummaryCell class="nc-summary" :index="0" :align="'left'" :colSpan="4" style="border-right: none;">合计</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" v-if="pageParameter.queryMark=='SJ'" :align="'right'" style="border-right: none;">{{summaryTotals.qcNum}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" v-if="pageParameter.queryMark=='WJ'" :align="'right'" style="border-right: none;">{{summaryTotals.qcNfrat}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcMd}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcMc}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" v-if="pageParameter.queryMark=='SJ'" :align="'right'" style="border-right: none;">{{summaryTotals.pzNum}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" v-if="pageParameter.queryMark=='WJ'" :align="'right'" style="border-right: none;">{{summaryTotals.pzNfrat}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzMd}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzMc}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" v-if="pageParameter.queryMark=='SJ'" :align="'right'" style="border-right: none;">{{summaryTotals.qmNum}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" v-if="pageParameter.queryMark=='WJ'" :align="'right'" style="border-right: none;">{{summaryTotals.qmNfrat}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmMd}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmMc}}</TableSummaryCell>
              </TableSummaryRow>

              <TableSummaryRow style="background-color: #cccccc;" v-if="pageParameter.queryMark=='SWJ'">
                <TableSummaryCell class="nc-summary" :index="0" :align="'left'" :colSpan="5" style="border-right: none;">合计</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcNum}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcNfrat}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcMd}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qcMc}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzNum}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzNfrat}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzMd}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.pzMc}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmNum}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmNfrat}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmMd}}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{summaryTotals.qmMc}}</TableSummaryCell>
              </TableSummaryRow>
            </TableSummary>
          </template>
          </BasicTable>
        <div class="pagination-text" :style="{top: (windowHeight+90)+'px'}" v-show="showPageNumber">
            {{`共 ${pageNumber}条记录&emsp;每页 1000 条`}}
          </div>
      </div>
    </div>
    <Query @save="loadPage" queryType="供应商科目余额表" @register="registerQueryPage" />
    <Print @save="loadPrint" @register="registerPrintPage"/>
    <a-modal v-model:visible="visibleModal" title="提示" @ok="visibleOK" cancelText="关闭" okText="导出表格">
      <p/>
      <a-radio-group style="width: 100%;margin-left: 50px;" v-model:value="radiovalue">
        <a-radio :value="1">
          <span>所有供应商按页签显示</span>
        </a-radio>
        <a-radio :value="2" style="margin-left: 50px;">
          <span>所有供应商显示在同一页签</span>
        </a-radio>
      </a-radio-group>
      <p/>
    </a-modal>
  </div>
</template>
<script setup lang="ts">
import {BasicTable, useTable} from '/@/components/Table'
import {useModal} from '/@/components/Modal'
import {PageWrapper} from '/@/components/Page'
import UnitChange from '../department/UnitChange.vue';

import {
  Divider as ADivider,
  DatePicker as ADatePicker, Select as ASelect, Input as AInput, Popover as APopover,
  Radio as ARadio, Popconfirm as APopconfirm, Table as ATable,
  Checkbox as ACheckbox, message, Drawer as ADrawer, Modal as AModal, Table
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
  FilterFilled, CheckOutlined, EditOutlined, CaretDownOutlined
} from '@ant-design/icons-vue'
import {findDbLanMuList, saveLanMuList} from '/@/api/record/system/accvoucher'
import {supKmYueGlStore} from '/@/api/record/sup-km-yue/supKmYueColumn'

import {onMounted, reactive, ref} from "vue";
import moment from "moment";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {initDynamics, assemblyDynamicColumn} from "./data";
import Query from "/@/views/boozsoft/system/km-sup-yue/popup/query.vue";
import Print from "/@/views/boozsoft/system/sup-km-yue/popup/print.vue";
import {cloneDeep} from "lodash-es";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  getThisIndexImg,
  getCurrentAccountName,
} from "/@/api/task-api/tast-bus-api";
import {findAll,exportAll,exportAll2} from '/@/api/record/sup-km-yue/data';
// 参数精度
import {finByParameterAccuracy} from '/@/api/record/km_mingxi/data';
import {useUserStoreWidthOut} from "/@/store/modules/user";
import router from "/@/router";
import {savelog} from "/@/api/record/log";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findByAccId} from "/@/api/record/system/account";
import {findPeriodByAccontId, findSupAll} from "/@/api/record/generalLedger/data";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useTabs} from "/@/hooks/web/useTabs";

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
const glStore = supKmYueGlStore()
const { closeCurrent } =useTabs();
// 页面变量
const pageParameter = reactive({
  thisAdInfo: {},
  total: 0,
  database: '',
  page: '',
  size: '',
  dynamicTenantId: '',
  queryMark: 'J',
  showRulesSize: 'MIN',
  condition: {},
  searchConditon: {
    requirement: 'ccode',
    value: '',
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
  bzName: '',
  openOne: '',
  styleValue: '',
  deptList: [],
  cusList: '',
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
const lanMuData = {'accId': '', 'menuName': '客户科目余额表', 'type': '', objects: '',username:useUserStoreWidthOut().getUserInfo.username}
// 表格参数
const loadMark = ref(false)
const tableSelectedRowKeys = ref([])
const tableSelectedRowObjs = ref([])
const windowWidth = (window.innerWidth - 70)
const windowHeight = (window.innerHeight - 370)
const totalColumnWidth = ref(0)
const deptList = ref([])
// 会计科目
const kmList: any = ref([])

const tableRef = ref(null)
const userName = useUserStoreWidthOut().getUserInfo.username
const dynamicTenantId = ref(getCurrentAccountName(true))
const accId = ref(getCurrentAccountName(false))
const accNameAll = ref('')
// 增加操作日志：1、修改2、删除3、停用、4查看、5导入、6导出、7打印
const logmap = ref({
  accId: dynamicTenantId.value,
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
  api: useRouteApi(CrudApi.list, {schemaName: dynamicTenantId}),
  columns: CrudApi.columns,
  bordered: true,
  loading: loadMark.value,
  immediate: false,
  canResize: true,
  showIndexColumn: true, //显示序号列
  pagination: {
    pageSize: 200,
    simple: true}
})
const [registerQueryPage, {openModal: openQueryPageM}] = useModal()
const [registerReviewPage, {openModal: openReviewPageM}] = useModal()

const showPageNumber=ref(false)
const pageNumber=ref(0)
function fetchSuccess(data) {
  pageNumber.value=data.items.length
  showPageNumber.value=true
  if(data.items.length<50){
    for (let i =  data.items.length; i < 50; i++) {
      data.items.push({ccode: ''})
    }
  }
  calculateTotal(data.items)
}
const summaryTotals = ref({})
const calculateTotal = (datalist) => {
  let list = JsonTool.parseProxy(datalist.filter(a=>a.ccode!=''))
  if (list.length == 0){
    summaryTotals.value = {}
    return false;
  }
  let qcMd = 0
  let qcMc = 0
  let qcNum = 0
  let qcNfrat = 0
  let pzMd = 0
  let pzMc = 0
  let pzNum = 0
  let pzNfrat = 0
  let qmMd = 0
  let qmMc = 0
  let qmNum = 0
  let qmNfrat = 0
  for (let i = 0; i < list.length; i++) {
    const e = list[i];
    qcMd += parseFloat(e.qcMd || '0')
    qcMc += parseFloat(e.qcMc || '0')
    qcNum += parseFloat(e.qcNum || '0')
    qcNfrat += parseFloat(e.qcNfrat || '0')

    pzMd += parseFloat(e.pzMd || '0')
    pzMc += parseFloat(e.pzMc || '0')
    pzNum += parseFloat(e.pzNum || '0')
    pzNfrat += parseFloat(e.pzNfrat || '0')

    qmMd += parseFloat(e.qmMd || '0')
    qmMc += parseFloat(e.qmMc || '0')
    qmNum += parseFloat(e.qmNum || '0')
    qmNfrat += parseFloat(e.qmNfrat || '0')
  }
  summaryTotals.value={
    qcMd: moneyformat(qcMd),
    qcMc: moneyformat(qcMc),
    pzMd: moneyformat(pzMd),
    pzMc: moneyformat(pzMc),
    qmMd: moneyformat(qmMd),
    qmMc: moneyformat(qmMc),
    qcNum: moneyformat(qcNum),
    qcNfrat: moneyformat(qcNfrat),
    pzNum: moneyformat(pzNum),
    pzNfrat: moneyformat(pzNfrat),
    qmNum: moneyformat(qmNum),
    qmNfrat: moneyformat(qmNfrat),
  }
}

const filterOption = (input: string, option: any) => {
  return option.children[0].children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

async function handleChangeMinKm(){
  closeFilterV()
  pageReload()
}
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
      // 供应商
      deptList.value = await useRouteApi(findSupAll,{schemaName:obj.accId+'-'+obj.year})('');
    }else {
      createWarningModal({title: '温馨提示',content: '暂无任何数据！'});
      pageParameter.thisAdInfo = {}
      pageParameter.total = -1
    }
  }
  loadMark.value = false
}

// 跳转科目明细账
async function goRouter(data) {
  pageParameter.km=data.ccode
  pageParameter.bzName=pageParameter.bz
  pageParameter.openOne='2'
  pageParameter.dynamicTenantId=dynamicTenantId.value
  router.push({
    path: '/zhongZhang/account-book/abf-supplier-contacts/abfsc-mxable',
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
  if (data == '' || data == null || parseFloat(data)==0) {
    return ''
  }
  return (+data || 0).toFixed(2).replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,')
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
    data: val
  })
  loadMark.value = true
  // 查询是否独立账套  是否独立账套（1）反之集团账套（0）
  const datainfo = await findByAccId(accId.value);
})

const openQueryPage = () => {
  val.openOne = 0
  openQueryPageM(true, {
    data: val
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

const closeFilterV = () => {
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
  lanMuData.accId = getCurrentAccountName(false)
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
const loadPage = async (data)=>{
  // ++++++++++++ 复写公司代码 +++++++++++++++++++
  pageParameter.companyCode=data.pageParameter.companyCode
  pageParameter.companyName=data.pageParameter.companyName
  pageParameter.database=data.pageParameter.database
  accId.value=data.pageParameter.accId
  dynamicTenantId.value=data.pageParameter.database
  accNameAll.value=data.pageParameter.accNameAll
  // ++++++++++++ 复写公司代码 +++++++++++++++++++
  // 查询是否独立账套  是否独立账套（1）反之集团账套（0）
  const datainfo = await findByAccId(accId.value);
  bwb.value = datainfo.currencyName

  // 参数规则
  let ParameteRule= await useRouteApi(finByParameterAccuracy,{schemaName:dynamicTenantId})('')
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

  // console.log(JSON.stringify(pageParameter)+'>>>>结束')
  if (data.openOne == 1){ // 第一次初始化 + 条件查询
    resetDynamicColumnData()
  }else { // 查询条件查询
    closeFilterV()
    pageParameter.searchConditon.value = ''
    pageReload()
  }
}

/******************* 弹框加载中 **************************/
import { Loading } from '/@/components/Loading';
import {exportExcel, exportExcel2} from "/@/api/record/generalLedger/excelExport";
import {useNewPrint} from "/@/utils/boozsoft/print/print";
import {tableStyle} from "/@/store/modules/abc-print";
import {company_findByCcode} from "/@/api/codekemu/codekemu";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
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
const visibleModal=ref(false)
const radiovalue=ref(1)

// 导出当前
function thisPageImport(){
  openCompFullLoading()
  const data = JSON.parse(JSON.stringify(getDataSource()))
  const data2:any=data.splice(0,getPaginationRef().pageSize)
  let columns:any = ljchecked.value?getColumns():getColumns().filter(v=>v.title!=='累计发生')
  let ccodeName=deptList.value.filter(v=>v.uniqueCode===pageParameter.minDept)[0].label
  const title = ['供应商：'+ccodeName+'              期间：'+ time.strDate +' - '+ time.endDate +'         单位：元']
  if(pageParameter.queryMark === 'J'){
    let a=ljchecked.value?9:7
    for (let i = 0; i < a; i++) {
      title.push('')
    }
  }else if(pageParameter.queryMark === 'SJ' || pageParameter.queryMark === 'WJ'){
    for (let i = 0; i < 11; i++) {
      title.push('')
    }
  }else if(pageParameter.queryMark === 'SWJ'){
    for (let i = 0; i < 15; i++) {
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
    v.ccodeName=v.ccodeName===null?'':v.ccodeName
    v.foreignCurrency=v.foreignCurrency===null?'':v.foreignCurrency
    v.unitMeasurement=v.unitMeasurement===null?'':v.unitMeasurement
    v.ljMc=v.ljMc===0?'':moneyformat(v.ljMc)
    v.ljMd=v.ljMd===0?'':moneyformat(v.ljMd)
    v.ljNfrat=v.ljNfrat===0?'':moneyformat(v.ljNfrat)
    v.ljNum=v.ljNum===0?'':moneyformat(v.ljNum)

    v.pzMc=v.pzMc===0?'':moneyformat(v.pzMc)
    v.pzMd=v.pzMd===0?'':moneyformat(v.pzMd)
    v.pzNfrat=v.pzNfrat===0?'':moneyformat(v.pzNfrat)
    v.pzNum=v.pzNum===0?'':formatNum(v.pzNum)
    v.qcMc=v.qcMc===0?'':moneyformat(v.qcMc)
    v.qcMd=v.qcMd===0?'':moneyformat(v.qcMd)
    v.qcNfrat=v.qcNfrat===0?'':moneyformat(v.qcNfrat)
    v.qcNum=v.qcNum===0?'':formatNum(v.qcNum)
    v.qmMc=v.qmMc===0?'':moneyformat(v.qmMc)
    v.qmMd=v.qmMd===0?'':moneyformat(v.qmMd)
    v.qmNfrat=v.qmNfrat===0?'':moneyformat(v.qmNfrat)
    v.qmNum=v.qmNum===0?'':formatNum(v.qmNum)
  })
  //样式靠右列
  let rightrow:any=[]
  //样式靠左列
  let leftrow:any=['A','B']
  if(pageParameter.queryMark === 'J'){
    rightrow=['C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  }else if(pageParameter.queryMark === 'SJ' || pageParameter.queryMark === 'WJ'){
    leftrow=['A','B','C']
    rightrow=['D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  }else{
    leftrow=['A','B','C','D']
    rightrow=['E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  }
  const sheet =[
    {
      title: '供应商科目余额表',
      tHeader: title,
      multiHeader: multiHeader,
      table: data2,
      keys: keys,
      merges: merges,
      sheetName: ccodeName,
      cellStyle: cellStyle,
      rightrow:rightrow,
      leftrow:leftrow,
    },
  ]
  exportExcel(sheet, 'xlsx',accNameAll.value+'-供应商科目余额表-'+time.strDate+'-'+time.endDate)
  compState.loading = false;
}

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
  let columns:any = ljchecked.value?getColumns():getColumns().filter(v=>v.title!=='累计发生')
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
  let leftrow:any=['A','B']
  if(pageParameter.queryMark === 'J'){
    rightrow=['C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  }else if(pageParameter.queryMark === 'SJ' || pageParameter.queryMark === 'WJ'){
    leftrow=['A','B','C']
    rightrow=['D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  }else{
    leftrow=['A','B','C','D']
    rightrow=['E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
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
          let a=ljchecked.value?9:7
          for (let i = 0; i < a; i++) {
            title.push('')
          }
        }else if(pageParameter.queryMark === 'SJ' || pageParameter.queryMark === 'WJ'){
          for (let i = 0; i < 11; i++) {
            title.push('')
          }
        }else if(pageParameter.queryMark === 'SWJ'){
          for (let i = 0; i < 15; i++) {
            title.push('')
          }
        }

        mxlist.forEach(v=>{
          v.ccodeName=v.ccodeName===null?'':v.ccodeName
          v.foreignCurrency=v.foreignCurrency===null?'':v.foreignCurrency
          v.unitMeasurement=v.unitMeasurement===null?'':v.unitMeasurement
          v.ljMc=v.ljMc===0?'':moneyformat(v.ljMc)
          v.ljMd=v.ljMd===0?'':moneyformat(v.ljMd)
          v.ljNfrat=v.ljNfrat===0?'':moneyformat(v.ljNfrat)
          v.ljNum=v.ljNum===0?'':moneyformat(v.ljNum)

          v.pzMc=v.pzMc===0?'':moneyformat(v.pzMc)
          v.pzMd=v.pzMd===0?'':moneyformat(v.pzMd)
          v.pzNfrat=v.pzNfrat===0?'':moneyformat(v.pzNfrat)
          v.pzNum=v.pzNum===0?'':formatNum(v.pzNum)
          v.qcMc=v.qcMc===0?'':moneyformat(v.qcMc)
          v.qcMd=v.qcMd===0?'':moneyformat(v.qcMd)
          v.qcNfrat=v.qcNfrat===0?'':moneyformat(v.qcNfrat)
          v.qcNum=v.qcNum===0?'':formatNum(v.qcNum)
          v.qmMc=v.qmMc===0?'':moneyformat(v.qmMc)
          v.qmMd=v.qmMd===0?'':moneyformat(v.qmMd)
          v.qmNfrat=v.qmNfrat===0?'':moneyformat(v.qmNfrat)
          v.qmNum=v.qmNum===0?'':formatNum(v.qmNum)
        })
        sheet.push(
          {
            title: '供应商科目余额表',
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
  exportExcel(sheet, 'xlsx',accNameAll.value+'-供应商科目余额表-'+time.strDate+'-'+time.endDate)
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
  let temp1:any=[{title: '供应商编码',key:'temp1',dataIndex:'temp1'},{title: '供应商名称',key:'temp2',dataIndex:'temp1'}]
  let temp2=ljchecked.value?getColumns():getColumns().filter(v=>v.title!=='累计发生')
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
  let leftrow:any=['A','B']
  if(pageParameter.queryMark === 'J'){
    rightrow=['C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  }else if(pageParameter.queryMark === 'SJ' || pageParameter.queryMark === 'WJ'){
    leftrow=['A','B','C','D']
    rightrow=['F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  }else{
    leftrow=['A','B','C','D']
    rightrow=['G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
  }
  pageParameter.cusList=deptList.value.map(v=>v.uniqueCode).join(',')
  await useRouteApi(exportAll2,{schemaName:dynamicTenantId})(pageParameter)
    .then(list=>{
      const data = list
      data.forEach(v=>{
        v.ccodeName=v.ccodeName===null?'':v.ccodeName
        v.foreignCurrency=v.foreignCurrency===null?'':v.foreignCurrency
        v.unitMeasurement=v.unitMeasurement===null?'':v.unitMeasurement
        v.ljMc=v.ljMc===0?'':moneyformat(v.ljMc)
        v.ljMd=v.ljMd===0?'':moneyformat(v.ljMd)
        v.ljNfrat=v.ljNfrat===0?'':moneyformat(v.ljNfrat)
        v.ljNum=v.ljNum===0?'':moneyformat(v.ljNum)

        v.pzMc=v.pzMc===0?'':moneyformat(v.pzMc)
        v.pzMd=v.pzMd===0?'':moneyformat(v.pzMd)
        v.pzNfrat=v.pzNfrat===0?'':moneyformat(v.pzNfrat)
        v.pzNum=v.pzNum===0?'':formatNum(v.pzNum)
        v.qcMc=v.qcMc===0?'':moneyformat(v.qcMc)
        v.qcMd=v.qcMd===0?'':moneyformat(v.qcMd)
        v.qcNfrat=v.qcNfrat===0?'':moneyformat(v.qcNfrat)
        v.qcNum=v.qcNum===0?'':formatNum(v.qcNum)
        v.qmMc=v.qmMc===0?'':moneyformat(v.qmMc)
        v.qmMd=v.qmMd===0?'':moneyformat(v.qmMd)
        v.qmNfrat=v.qmNfrat===0?'':moneyformat(v.qmNfrat)
        v.qmNum=v.qmNum===0?'':formatNum(v.qmNum)
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
      exportExcel2(sheet, 'xlsx',accNameAll.value+'-供应商科目余额表-'+time.strDate+'-'+time.endDate)
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
  if(obj.printUser){
    printUser = '制表人：' + userName
  }
  let printBz = ''
  if(obj.printBz){
    printBz = '币种：人民币'
  }
  let ccodeName=deptList.value.filter(v=>v.uniqueCode===pageParameter.minDept)[0].label
  if(pageParameter.queryMark==='J'){
    let qcMdMoney,qcMcMoney,pzMdMoney,pzMcMoney,qmMdMoney,qmMcMoney,ljMdMoney,ljMcMoney = 0
    let printList: any = []
    data.forEach((item,index) => {
      let item1 = {}
      item1[0]=item.ccode==null?'':setString(item.ccode,15)
      item1[1]=item.ccodeName==null?'':setString(item.ccodeName,35)
      item1[2]=item.qcMd==0?'':moneyformat(item.qcMd)
      item1[3]=item.qcMc==0?'':moneyformat(item.qcMc)
      item1[4]=item.pzMd==0?'':moneyformat(item.pzMd)
      item1[5]=item.pzMc==0?'':moneyformat(item.pzMc)
      item1[6]=item.qmMd==0?'':moneyformat(item.qmMd)
      item1[7]=item.qmMc==0?'':moneyformat(item.qmMc)
      printList.push(item1)

      if (item.ccode!='合计'){
        qcMdMoney = add(qcMdMoney,item.qcMd)
        qcMcMoney = add(qcMcMoney,item.qcMc)
        pzMdMoney = add(pzMdMoney,item.pzMd)
        pzMcMoney = add(pzMcMoney,item.pzMc)
        qmMdMoney = add(qmMdMoney,item.qmMd)
        qmMcMoney = add(qmMcMoney,item.qmMc)

        if ((index)>0 && (index)%25==0){
          let item2 = {}
          item2[0]=''
          item2[1] = '承前页'
          item2[2]=moneyformat(qcMdMoney)
          item2[3]=moneyformat(qcMcMoney)
          item2[4]=moneyformat(pzMdMoney)
          item2[5]=moneyformat(pzMcMoney)
          item2[6]=moneyformat(qmMdMoney)
          item2[7]=moneyformat(qmMcMoney)
          printList.push(item2)
        }
        else {
          qcMdMoney = 0
          qcMcMoney = 0
          pzMdMoney = 0
          pzMcMoney = 0
          qmMdMoney = 0
          qmMcMoney = 0
          if ((index)>0 && (index)%25==0 && index<data.length-1) {
            let item2 = {}
            item2[0]=''
            item2[1]='承前页'
            item2[2]=item.qcMd==0?'':moneyformat(item.qcMd)
            item2[3]=item.qcMc==0?'':moneyformat(item.qcMc)
            item2[4]=item.pzMd==0?'':moneyformat(item.pzMd)
            item2[5]=item.pzMc==0?'':moneyformat(item.pzMc)
            item2[6]=item.qmMd==0?'':moneyformat(item.qmMd)
            item2[7]=item.qmMc==0?'':moneyformat(item.qmMc)
            printList.push(item2)
          }
        }
      }
    })
    for (let i=0; i<printList.length%26; i++){
      let item1 = {}
      item1[0] = ''
      item1[1] = ''
      item1[2] = ''
      item1[3] = ''
      item1[4] = ''
      item1[5] = ''
      item1[6] = ''
      item1[7] = ''
      printList.push(item1)
    }
    let num = Math.ceil(printList.length/26)
    useNewPrint({data: ['l', 'px', 'a4', true]}, (doc) => {
      loadMark.value = false
      compState.loading = false;
      doc.autoTable({
        head: [['', '', '', '供应商科目余额表', '', '', '', ''],
          ['供应商：' + ccodeName, '', '', '期间:' + time.strDate+' - '+time.endDate, '','',printBz,'单位：元'],
          ['科目编码', '科目名称', '期初余额','', '本期发生', '','期末余额', ''],
          ['', '', '本币借方','本币贷方','本币借方','本币贷方','本币借方','本币贷方']],
        body: printList,
        // startY: 60,
        styles: tableStyle(),
        margin: {
          left: 35,
          top: 20,
          bottom: 20
        },
        addPageContent: (data) => {
          let tabHeigth = data.table.height,
            tabMarginTop = data.settings.margin.top,
            tabSize = data.table.finalY - tabMarginTop,//表格最大Y轴-表格顶部距离得到每页表格的最大值
            tabMarginLeft = data.settings.margin.left;
          if (data.table.finalY)//是否分页 有分页时才有该属性finalY
            if (data.pageNumber != Math.ceil(tabHeigth / tabSize)) return;//如果需要每一页都显示页尾则注释该行代码
          data.doc.setFontSize(9)
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
              data.cell.colSpan = 2
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
            } else if (data.column.index == 3) {
              data.cell.styles.cellPadding = { left: 13}
              data.cell.colSpan = 2
              data.cell.styles.halign = 'left'
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
            if (data.column.index == 2 || data.column.index == 4 || data.column.index == 6){
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
          0: {maxHeight: 10, cellWidth: 60, halign: 'left'},
          1: {cellWidth: 130, halign: 'left'},
          2: {cellWidth: 65, halign: 'right'},
          3: {cellWidth: 65, halign: 'right'},
          4: {cellWidth: 65, halign: 'right'},
          5: {cellWidth: 65, halign: 'right'},
          6: {cellWidth: 65, halign: 'right'},
          7: {cellWidth: 65, halign: 'right'},
        }
      })
    })
  }
  if(pageParameter.queryMark==='SJ'){
    let qcMdMoney,qcMcMoney,pzMdMoney,pzMcMoney,qmMdMoney,qmMcMoney,qcNum,pzNum,qmNum = 0
    let printList: any = []
    data.forEach((item,index) => {
      let item1 = {}
      item1[0]=item.ccode==null?'':setString(item.ccode,15)
      item1[1]=item.ccodeName==null?'':setString(item.ccodeName,35)
      item1[2]=item.unitMeasurement
      item1[3]=item.qcMd!==0?'借':'贷'
      item1[4]=item.qcNum==0?'':item.qcNum
      item1[5]=item.qcMd==0?moneyformat(item.qcMc):moneyformat(item.qcMd)
      item1[6]=item.pzNum==0?'':item.pzNum
      item1[7]=item.pzMd==0?'':moneyformat(item.pzMd)
      item1[8]=item.pzMc==0?'':moneyformat(item.pzMc)
      item1[9]=item.qmMd!==0?'借':'贷'
      item1[10]=item.qmNum==0?'':item.qmNum
      item1[11]=item.qmMc==0?moneyformat(item.qmMd):moneyformat(item.qmMc)
      printList.push(item1)

      if (item.ccode!='合计'){
        qcNum = add(qcNum,item.qcNum)
        qcMdMoney = add(qcMdMoney,item.qcMd)
        qcMcMoney = add(qcMcMoney,item.qcMc)
        pzNum = add(pzNum,item.pzNum)
        pzMdMoney = add(pzMdMoney,item.pzMd)
        pzMcMoney = add(pzMcMoney,item.pzMc)
        qmNum = add(qmNum,item.qmNum)
        qmMdMoney = add(qmMdMoney,item.qmMd)
        qmMcMoney = add(qmMcMoney,item.qmMc)

        let a=add(qcMdMoney,qcMcMoney)
        let b=add(qmMdMoney,qmMcMoney)

        if ((index)>0 && (index)%25==0){
          let item2 = {}
          item2[0]=''
          item2[1] = '承前页'
          item2[2] = item.unitMeasurement
          item2[3] = ''
          item2[4]=qcNum==0?'':qcNum
          item2[5]=moneyformat(a)
          item2[6]=pzNum==0?'':pzNum
          item2[7]=moneyformat(pzMdMoney)
          item2[8]=moneyformat(pzMcMoney)
          item2[9]=''
          item2[10]=qmNum==0?'':qmNum
          item2[11]=moneyformat(b)
          printList.push(item2)
        }
        else {
          qcMdMoney = 0
          qcMcMoney = 0
          pzMdMoney = 0
          pzMcMoney = 0
          qmMdMoney = 0
          qmMcMoney = 0
          qcNum = 0
          pzNum = 0
          qmNum = 0
          if ((index)>0 && (index)%25==0 && index<data.length-1) {
            let item2 = {}
            item2[0]=''
            item2[1]='承前页'
            item2[2]=item.unitMeasurement
            item2[3]=item.qcMd!==0?'借':'贷'
            item2[4]=item.qcNum==0?'':item.qcNum
            item2[5]=item.qcMd==0?moneyformat(item.qcMc):moneyformat(item.qcMd)
            item2[6]=item.pzNum==0?'':item.pzNum
            item2[7]=item.pzMd==0?'':moneyformat(item.pzMd)
            item2[8]=item.pzMc==0?'':moneyformat(item.pzMc)
            item2[9]=item.qmMd!==0?'借':'贷'
            item2[10]=item.qmNum==0?'':item.qmNum
            item2[11]=item.qmMc==0?moneyformat(item.qmMd):moneyformat(item.qmMc)
            printList.push(item2)
          }
        }
      }
    })
    for (let i=0; i<printList.length%26; i++){
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
    let num = Math.ceil(printList.length/26)
    useNewPrint({data: ['l', 'px', 'a4', true]}, (doc) => {
      loadMark.value = false
      compState.loading = false;
      doc.autoTable({
        head: [['', '', '', '供应商科目余额表', '', '', '', '', '', '', '', ''],
          ['供应商：' + ccodeName, '', '', '期间:' + time.strDate+' - '+time.endDate, '','','','', printBz, '','' , '单位：元'],
          ['科目编码', '科目名称','单位', '期初余额','','', '本期发生', '','','期末余额', '',''],
          ['', '','','方向', '数量','金额','数量','本币借方','本币贷方','方向','数量','金额']],
        body: printList,
        // startY: 60,
        styles: tableStyle(),
        margin: {
          left: 35,
          top: 20,
          bottom: 20
        },
        addPageContent: (data) => {
          let tabHeigth = data.table.height,
            tabMarginTop = data.settings.margin.top,
            tabSize = data.table.finalY - tabMarginTop,//表格最大Y轴-表格顶部距离得到每页表格的最大值
            tabMarginLeft = data.settings.margin.left;
          if (data.table.finalY)//是否分页 有分页时才有该属性finalY
            if (data.pageNumber != Math.ceil(tabHeigth / tabSize)) return;//如果需要每一页都显示页尾则注释该行代码
          data.doc.setFontSize(9)
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
            } else if (data.column.index == 3) {
              data.cell.colSpan = 4
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
            if (data.column.index == 0 || data.column.index == 1 || data.column.index == 2){
              data.cell.styles.valign = 'middle'
              data.cell.rowSpan=2
            }
            if (data.column.index == 3 || data.column.index == 6 || data.column.index == 9){
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
          0: {maxHeight: 10, cellWidth: 60, halign: 'left'},
          1: {cellWidth: 130, halign: 'left'},
          2: {cellWidth: 20, halign: 'right'},
          3: {cellWidth: 20, halign: 'center'},
          4: {cellWidth: 30, halign: 'right'},
          5: {cellWidth: 60, halign: 'right'},
          6: {cellWidth: 30, halign: 'right'},
          7: {cellWidth: 60, halign: 'right'},
          8: {cellWidth: 60, halign: 'right'},
          9: {cellWidth: 20, halign: 'center'},
          10: {cellWidth: 30, halign: 'right'},
          11: {cellWidth: 60, halign: 'right'},
        }
      })
    })
  }
  if(pageParameter.queryMark==='WJ'){
    let qcMdMoney,qcMcMoney,pzMdMoney,pzMcMoney,qmMdMoney,qmMcMoney,qcNfrat,pzNfrat,qmNfrat = 0
    let printList: any = []
    data.forEach((item,index) => {
      let item1 = {}
      item1[0]=item.ccode==null?'':setString(item.ccode,15)
      item1[1]=item.ccodeName==null?'':setString(item.ccodeName,40)
      item1[2]=item.qcMd!==0?'借':'贷'
      item1[3]=item.qcNfrat==0?'':moneyformat(item.qcNfrat)
      item1[4]=item.qcMd==0?moneyformat(item.qcMc):moneyformat(item.qcMd)
      item1[5]=item.pzNfrat==0?'':moneyformat(item.pzNfrat)
      item1[6]=item.pzMd==0?'':moneyformat(item.pzMd)
      item1[7]=item.pzMc==0?'':moneyformat(item.pzMc)
      item1[8]=item.qmMd!==0?'借':'贷'
      item1[9]=item.qmNfrat==0?'':moneyformat(item.qmNfrat)
      item1[10]=item.qmMc==0?moneyformat(item.qmMd):moneyformat(item.qmMc)
      printList.push(item1)

      if (item.ccode!='合计'){
        qcNfrat = add(qcNfrat,item.qcNfrat)
        qcMdMoney = add(qcMdMoney,item.qcMd)
        qcMcMoney = add(qcMcMoney,item.qcMc)
        pzNfrat = add(pzNfrat,item.pzNfrat)
        pzMdMoney = add(pzMdMoney,item.pzMd)
        pzMcMoney = add(pzMcMoney,item.pzMc)
        qmNfrat = add(qmNfrat,item.qmNfrat)
        qmMdMoney = add(qmMdMoney,item.qmMd)
        qmMcMoney = add(qmMcMoney,item.qmMc)

        let a=add(qcMdMoney,qcMcMoney)
        let b=add(qmMdMoney,qmMcMoney)

        if ((index)>0 && (index)%25==0){
          let item2 = {}
          item2[0]=''
          item2[1] = '承前页'
          item2[2] = ''
          item2[3]=qcNfrat==0?'':qcNfrat
          item2[4]=moneyformat(a)
          item2[5]=pzNfrat==0?'':pzNfrat
          item2[6]=moneyformat(pzMdMoney)
          item2[7]=moneyformat(pzMcMoney)
          item2[8]=''
          item2[9]=qmNfrat==0?'':qmNfrat
          item2[10]=moneyformat(b)
          printList.push(item2)
        }
        else {
          qcMdMoney = 0
          qcMcMoney = 0
          pzMdMoney = 0
          pzMcMoney = 0
          qmMdMoney = 0
          qmMcMoney = 0
          qcNfrat = 0
          pzNfrat = 0
          qmNfrat = 0
          if ((index)>0 && (index)%25==0 && index<data.length-1) {
            let item2 = {}
            item2[0]=''
            item2[1]='承前页'
            item2[2]=item.qcMd!==0?'借':'贷'
            item2[3]=item.qcNfrat==0?'':moneyformat(item.qcNfrat)
            item2[4]=item.qcMd==0?moneyformat(item.qcMc):moneyformat(item.qcMd)
            item2[5]=item.pzNfrat==0?'':moneyformat(item.pzNfrat)
            item2[6]=item.pzMd==0?'':moneyformat(item.pzMd)
            item2[7]=item.pzMc==0?'':moneyformat(item.pzMc)
            item2[8]=item.qmMd!==0?'借':'贷'
            item2[9]=item.qmNfrat==0?'':moneyformat(item.qmNfrat)
            item2[10]=item.qmMc==0?moneyformat(item.qmMd):moneyformat(item.qmMc)
            printList.push(item2)
          }
        }
      }
    })
    for (let i=0; i<printList.length%26; i++){
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
    let num = Math.ceil(printList.length/26)

    // 查询科目外币名称
    // let code= await useRouteApi(company_findByCcode,{schemaName: dynamicTenantId})({ccode:pageParameter.km})
    // let currencyType=''
    // if(code.currencyType!==''){
    //   currencyType='(外币：'+code.currencyType+')'
    // }
    useNewPrint({data: ['l', 'px', 'a4', true]}, (doc) => {
      loadMark.value = false
      compState.loading = false;
      doc.autoTable({
        head: [['', '', '供应商科目余额表', '', '', '', '', '', '', '', ''],
          ['供应商：' + ccodeName, '', '期间:' + time.strDate+' - '+time.endDate, '','','','', '', printBz,'' , '单位：元'],
          ['科目编码', '科目名称', '期初余额','','', '本期发生', '','','期末余额', '',''],
          ['', '','方向', '原币','金额','原币','本币借方','本币贷方','方向','原币','金额']],
        body: printList,
        // startY: 60,
        styles: tableStyle(),
        margin: {
          left: 30,
          top: 20,
          bottom: 20
        },
        addPageContent: (data) => {
          let tabHeigth = data.table.height,
            tabMarginTop = data.settings.margin.top,
            tabSize = data.table.finalY - tabMarginTop,//表格最大Y轴-表格顶部距离得到每页表格的最大值
            tabMarginLeft = data.settings.margin.left;
          if (data.table.finalY)//是否分页 有分页时才有该属性finalY
            if (data.pageNumber != Math.ceil(tabHeigth / tabSize)) return;//如果需要每一页都显示页尾则注释该行代码
          data.doc.setFontSize(9)
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
            if (data.column.index == 2) {
              data.cell.colSpan = 4
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
              data.cell.colSpan = 4
              data.cell.styles.halign = 'right'
            } else if (data.column.index == 8) {
              data.cell.colSpan = 2
              data.cell.styles.halign = 'right'
            }else {
              data.cell.styles.halign = 'right'
            }
          }
          if (data.section == 'head' && data.row.index == 2) {
            data.cell.styles.fontSize = 10
            data.cell.styles.cellPadding = {top: 4, left: 2, right: 2, bottom: 3}
            data.cell.styles.fontStyle = 'bold'
            data.cell.styles.fillColor = [230, 230, 230]
            data.cell.styles.halign = 'center'
            if (data.column.index == 0 || data.column.index == 1 ){
              data.cell.styles.valign = 'middle'
              data.cell.rowSpan=2
            }
            if (data.column.index == 2 || data.column.index == 5 || data.column.index == 8){
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
              data.cell.styles.fillColor = [240, 240, 240]
            }
            if (data.column.index == 3 || data.column.index == 4 || data.column.index == 5 || data.column.index == 6 || data.column.index == 7 || data.column.index == 9 || data.column.index == 10){
              data.cell.styles.fontSize = 8
            }
          }
        },
        columnStyles: {
          0: {maxHeight: 10, cellWidth: 50, halign: 'left'},
          1: {cellWidth: 150, halign: 'left'},
          2: {cellWidth: 20, halign: 'center'},
          3: {cellWidth: 50, halign: 'right'},
          4: {cellWidth: 50, halign: 'right'},
          5: {cellWidth: 50, halign: 'right'},
          6: {cellWidth: 50, halign: 'right'},
          7: {cellWidth: 50, halign: 'right'},
          8: {cellWidth: 20, halign: 'center'},
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
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
<style scoped>
:deep(.ant-popover-inner){
  width: 170px;
}
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
  margin-top: -6px;
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
