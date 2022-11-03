<template>
  <div class="app-container">
    <div class="app-container-top">
      <div class="app-container-head">
        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">
        <div class="container-head-title">
          <b class="noneSpan">辅助科目期初余额</b>
          <label style="font-size: 14px;;margin-left: 1em;">启用期间：</label><span
          style="font-size: 14px;font-weight: bold;margin-right: 1em;">{{ qujian }}</span>
        </div>

        <!--      <div style="display: inline">
                <span style="display: none; margin-left: 10px; font-size: 16px"><b>启用日期：
                  <a-date-picker v-model:value="begintime" disabled />
                </b></span>
              </div>-->

        <div class="ant-btn-group">
          <button v-if="ibookflg" type="button" class="ant-btn" @click="qcjzbtn('0')">
            <span>恢复记账</span></button>
          <button v-if="!ibookflg" type="button" class="ant-btn" @click="editflg(edittext)">
            <span>{{ edittext }}</span></button>
          <span v-if="roweditflg2">
          <button type="button" class="ant-btn" @click="importPop"><span>导入</span></button>
          <button type="button" class="ant-btn" @click="emptyAll2"><span>清空</span></button>
        </span>
          <button type="button" v-if="!ibookflg" class="ant-btn" @click="qcjzbtn('1')">
            <span>期初记账</span></button>
          <button type="button" class="ant-btn" @click="ssphbtn"><span>试算</span></button>
          <button type="button" class="ant-btn"><span>对账</span></button>
          <button type="button" class="ant-btn" @click="exportexcel"><span>导出</span></button>
          <button type="button" class="ant-btn"><span>打印</span></button>
        </div>
        <div style="clear:both"/>
        <div style="margin-top: .5%;">
          <div style="float: right; margin-left: 10px">
            <a-button class="ant-btn-default" @click="findAllInitialBalanceFuZhu">
              <SyncOutlined :style="{ fontSize: '14px' }"/>
            </a-button>
            <a-popover class="ant-btn-default" placement="bottom">
              <template #content>
                <a-popconfirm
                  ok-text="保存"
                  cancel-text="关闭"
                  @confirm="confirm"
                  @cancel="cancel">
                  <template #icon><b>栏目设置</b><br></template>
                  <template #title>
                    <div style="width: 640px">
                      <a-table bordered :data-source="dynamicColumnData" :columns="dynamicColumns"
                               childrenColumnName="children" :pagination="false"
                               style="max-height: 500px;overflow-y: auto" class="lanmu-table">
                        <template #checkBox="{ text, record }">
                          <a-checkbox v-model:checked="record.check" :disabled="record.isFixed"/>
                        </template>
                        <template #widthInput="{ text, record }">
                          <div class="editable-cell">
                            <div v-if="editableData[record.key]"
                                 class="editable-cell-input-wrapper">
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
                            <div v-if="editableData[record.key]"
                                 class="editable-cell-input-wrapper">
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
                    </div>
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
              <!--            <template #title>
                            <b>设置表格字号</b></template>-->
              <a-button>
                <SettingFilled :style="{ fontSize: '14px' }"/>
              </a-button>
            </a-popover>
            <a-button @click="()=>{if (!visible){ visible = true;}return false}">
              <FilterFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </div>
          <div style="float: right; position: relative">
            <a-select v-model:value="selectSearchValue" style="width: 120px"
                      @change="selectSearch2">
              <a-select-option value="ccode">科目编码</a-select-option>
              <a-select-option value="ccodeName">科目名称</a-select-option>
              <a-select-option value="cclass">余额方向</a-select-option>
              <a-select-option value="fuzhu">辅助项</a-select-option>
              <a-select-option value="menterage">计量单位</a-select-option>
              <a-select-option value="currencyType">外币币种</a-select-option>
            </a-select>
            <a-input-search placeholder="" v-model:value="inputsearchtext"
                            style="width: 200px; border-radius: 4px" @search="selectSearch"/>
          </div>
          <div style="float: left;width:560px;">
            <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
          </div>
          <div style="float: left;margin-left: 10px;margin-top: 6px;">
<!--            <span style="font-size: 16px"><b>年度：</b></span>
            <a-select v-model:value="iyearselected" style="width: 120px" @change="yearChange">
              <a-select-option :value="item.iyear" v-for="(item, i) in iyearlist">{{
                  item.iyear
                }}
              </a-select-option>
            </a-select>-->
            <label style="font-size: 14px">本位币：</label><span
            style="font-size: 14px;font-weight: bold;margin-right: 2em">{{ bwb }}</span>
          </div>
        </div>
      </div>
    </div>
    <div class="app-container-bottom" :style="{height: (windowHeight+60)+'px'}">
      <BasicTable
        ref="tableRef"
        :row-selection="{ type: 'checkbox' ,fixed: true}"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        :columns="tableColumns"
        :dataSource="tableData"
        :loading="loading"
        size="small"
        @register="registerTable"
        :pagination="{
          pageSize: 100,
          showSizeChanger: true,
          pageSizeOptions: ['50','100','200', '500', '1000'],
          showTotal: (t) => `总共${t}条数据`,
        }"
      >
        <template #ncnum="{ record }">
          <span v-if="record.ncnum > 1" style="float: center; margin-right: 10px">{{
              record.ncnum
            }}</span>
          <span v-if="record.ncnum === null && record.ndS > 0"
                style="float: center; margin-right: 10px">{{ record.ndS }}</span>
          <span v-if="record.ncnum === null && record.ncS > 0"
                style="float: center; margin-right: 10px">{{ record.ncS }}</span>
        </template>

        <template #bprogerty="{ record }">
          {{ record.bprogerty === '1' ? '借' : record.bprogerty === '0' ? '贷' : '' }}
        </template>
        <template #md="{ record }">
          <span v-if="record.md > 1" style="float: right; margin-right: 10px">{{
              money(record.md)
            }}</span>
        </template>
        <template #mc="{ record }">
          <span v-if="record.mc > 1" style="float: right; margin-right: 10px">{{
              money(record.mc)
            }}</span>
        </template>
        <template #nfrat="{ record }">
          <span v-if="record.nfrat > 1"
                style="float: right; margin-right: 10px">{{ money(record.nfrat) }}</span>
        </template>
        <template #caozuo="{ record }">
          <span v-if="record.flag==='1'">
             <a-button :disabled="!roweditflg2"
                       v-if="record.bend === '1'"
                       style="border-color: white;"
                       type="link"
                       @click="setModal1Visible2(record)">
                <EditOutlined/>
              </a-button>
          </span>
        </template>
      </BasicTable>
      <ImprotExcel @save="reloadProjects" @register="registerImportPage"/>
      <ThisEdit @register="registerEditPage" @save="findAllInitialBalanceFuZhu"
                :placement="'top'"></ThisEdit>
      <a-modal
        width="1500px"
        title="试算平衡"
        v-model:visible="modal1Visible"
        centered
        @ok="modal1Visible = false"
      >
        <font style="flot: right" size="5">{{ ssphtext }}</font>
        <br/>
        <a-table
          v-if="cwflag"
          :pagination="false"
          :columns="ssphcolumns"
          :data-source="ssphlist"
        />
        <a-table
          v-if="ysflag"
          :pagination="false"
          :columns="ssphcolumns"
          :data-source="ssphlist"
        />
        <font style="flot: right" size="5" v-if="ysflag">{{ ystext }}</font>
        <br/>
        <a-table
          v-if="ysflag"
          :pagination="false"
          :columns="ssphcolumns"
          :data-source="ssphlist2"
        />
      </a-modal>
      <a-modal
        title="选择账套"
        style="top: 20px"
        v-model:visible="modal3Visible"
        @ok="modalok"
      >
        <a-select
          v-model:value="selectDataBaseValue"
          show-search
          style="width: 100%"
        >
          <a-select-option v-for="item in databaseAll" :value="item.coCode">
            {{ item.coCode + '-' + item.accName }}
          </a-select-option>
        </a-select>
      </a-modal>
      <a-drawer
        title="过滤漏斗"
        placement="right"
        :closable="true"
        v-if="visible"
        :mask="false"
        :visible="visible"
        :get-container="false"
        :wrap-style="{ position: 'absolute' }"
        @close="visible=false"
      >
        <ul>
          <li>
            <span style="color: black;font-weight: bold">
              借方本币：
            </span>
            <div>
              <a-input v-model:value="pageParameter.filterConditon.amountMinMd" placeholder=""
                       style="width: 95px;font-size: 10px"/>
              ~
              <a-input v-model:value="pageParameter.filterConditon.amountMaxMd" placeholder=""
                       style="width: 95px;font-size: 10px;float: right"/>
            </div>
          </li>
          <li style="margin-top: 5%">
          <span style="color: black;font-weight: bold">
            贷方本币：
          </span>
            <div>
              <a-input v-model:value="pageParameter.filterConditon.amountMinMc" placeholder=""
                       style="width: 95px;font-size: 10px"/>
              ~
              <a-input v-model:value="pageParameter.filterConditon.amountMaxMc" placeholder=""
                       style="width: 95px;font-size: 10px;float: right"/>
            </div>
          </li>
        </ul>
        <br/>
        <a-button type="primary" style="float: right;" @click="filterSearch">
          <span style="font-size: 14px">开始</span>
        </a-button>
      </a-drawer>
    </div>
  </div>
</template>
<script setup lang="ts">
import {
  EditOutlined,
  CloseOutlined,
  CheckOutlined,
  CaretDownFilled,
  ExclamationCircleOutlined,
  FormOutlined,
  DeleteOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled,
  SortDescendingOutlined,
  SortAscendingOutlined,
  SearchOutlined,
  PrinterOutlined,
  UsbOutlined,
} from '@ant-design/icons-vue';
import {BasicTable, useTable} from '/@/components/Table';
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";

import {useModal} from '/@/components/Modal';
import {
  findAllIperiodIyear,
  findByAccountInfo,
  findAllSubjectInitialBalance,
  findAllSubjectInitialBalanceFuZhu,
  findAllSubjectInitialBalanceFuZhuList,
  findByFunctionModule,
  saveSubjectInitialBalance,
  saveTaskInfo,
  delFunctionModule,
  delInitalBalance,
  findByIperiodFlag,
  findByAccvoucherIbook,
  findBySubjectInitalBalabceIbook,
  ssph,
  qcjz,
  qcjzsum,
  pzjzibook,
  emptyAll,
} from '/@/api/subjectInitialBalance/subjectInitialBalance';
import ThisEdit from './popup/edit.vue'
import {onMounted, ref, createVNode, reactive} from 'vue';
import ImprotExcel from './popup/import.vue';
import {jsonToSheetXlsx} from '/@/components/Excel';
import {useUserStore, useUserStoreWidthOut} from '/@/store/modules/user';
import {getCurrentAccountName, getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import {defRouteApi, useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {countByAccountMode, findByAccId, findByUserAccount} from "/@/api/record/system/account";
import UnitChange from '../department/UnitChange.vue';
import {groupStandardAndTemplate} from "/@/api/codekemu/codekemu";
import {getGroupListById} from "/@/api/record/group";
import {findAllSubjectInitialBalanceFuZhuListMingXi} from "/@/api/subjectInitialBalance/subjectInitialBalance";
import {findKeyLabelAll} from "/@/api/record/system/fuZhuHeSuan";

import {
  findYearMinDate,
  findYearMaxDate,
  findPeriodYears
} from '/@/api/record/generalLedger/data';
import {
  filterAccListByAuth,
  getAdInfoDatas,
  getAllAccAuths
} from "/@/api/record/system/financial-settings";
import {findTemplateByAccId} from "/@/api/acctemplate/acctemplate";
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Popover as APopover,
  Radio as ARadio,
  Pagination as APagination,
  Popconfirm as APopconfirm,
  Table as ATable,
  Checkbox as ACheckbox,
  Statistic as AStatistic, Modal as AModal,
  Drawer as ADrawer, Collapse, Tooltip, Tag as ATag,message
} from "ant-design-vue"
import {useMessage} from "/@/hooks/web/useMessage";
import {
  assemblyDynamicColumn,
  combineParameters,
  initDynamics
} from "/@/views/boozsoft/system/subjectInitialBalance/data";
import {findDbLanMuList, saveLanMuList} from "/@/api/record/system/accvoucher";
import {cloneDeep} from "lodash-es";
import {findPeriodByYaer} from "/@/api/sys_period/data";

const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const CollapsePanel = Collapse.Panel
const AStatisticCountdown = AStatistic.Countdown;
// 期初记账/恢复期初记账  显示
const ibookflg = ref(false);
// 计时数值
const deadline = ref(Date.now() + 1000 * 10);
// 借贷方金额
const md = ref('');
const mc = ref('');
// 借贷方数量
const numflg = ref(false);
const ncnum = ref(false);
// 外币金额
const nfrat = ref('');
const nfratflg = ref(false);
const visible = ref(false);

// 弹框2状态
const modal2Visible = ref(false);
const edittext = ref('开始编辑');
// 弹框1状态
const modal1Visible = ref(false);
// 试算平衡list
const ssphtext = ref('');
const ystext = ref('');
const ssphlist = ref([]);
const ssphlist2 = ref([]);
// 试算平衡表头
const ssphcolumns = ref([
  {
    title: '科目类型',
    dataIndex: 'name',
    width: 100
  },
  {
    title: '借方金额',
    dataIndex: 'md',
  },
  {
    title: '贷方金额',
    dataIndex: 'mc',
  },
  {
    title: '方向',
    dataIndex: 'fx',
    width: 50
  },
  {
    title: '余额',
    dataIndex: 'yue',
  },
  {
    title: '科目类型',
    dataIndex: 'name2',
    width: 100
  },
  {
    title: '借方金额',
    dataIndex: 'md2',
  },
  {
    title: '贷方金额',
    dataIndex: 'mc2',
  },
  {
    title: '方向',
    dataIndex: 'fx2',
    width: 50
  },
  {
    title: '余额',
    dataIndex: 'yue2',
  },
]);
// 单元格可编状态
const roweditflg2 = ref(false);
const loading = ref(false);
// 账套启用期间
const begintime = ref('');
// 账套启用期间
const iyearlist = ref([]);
// 选中年度
const iyearselected = ref('');
// 显示末级科目
const lastCode = ref(false);
const tableDataAll = ref([]);
const tableData = ref([]);
// 任务管理信息
const taskinfo = ref('');
// 期初弹框回调信息
const datainfo = ref('');
// 期初弹框回调信息
const types = ref('');
// 数据库模式名称
const database = ref(getCurrentAccountName(false));
const databaseTrue = ref(getCurrentAccountName(true));
const databaselist = ref([]);
const userinfo = ref({
  databaseNum: databaseTrue.value,
  username: useUserStoreWidthOut().getUserInfo.username,
  realName: useUserStoreWidthOut().getUserInfo.realName,
  name: useUserStoreWidthOut().getUserInfo.name,
  phone: useUserStoreWidthOut().getUserInfo.phone,
});
const userStore = useUserStore();
// 字体
const pageParameter = reactive({
  cclass: '全部',
  databasenum: databaseTrue.value,
  showRulesSize: 'MIN',
  queryMark: '1',
  filterConditon: {
    amountMinMc: '',
    amountMaxMc: '',
    amountMinMd: '',
    amountMaxMd: ''
  },
  uniqueAccStandard: '',
  templateId: '',
  activeKey: '',
  reloadMark: false,
  companyCode: '',
  companyName: '',
  ifUnit: false,
})
// 数量/外币 栏目是否显示
const menterage = ref(true)
const currencyType = ref(true)
const tableColumns = ref([
  {
    title: "操作",
    dataIndex: "caozuo",
    slots: {customRender: "caozuo"}
  },
  {
    title: "科目编码",
    dataIndex: "ccode",
    align: "left"
  },
  {
    title: "科目名称",
    dataIndex: "ccodeName",
    align: "left"
  },
  {
    title: "方向",
    dataIndex: "bprogerty",
    slots: {customRender: "bprogerty"},
    width: 50
  },
  {
    title: "辅助项",
    dataIndex: "fuzhu",
  },
  {
    title: "计量单位",
    dataIndex: "menterage",
    defaultHidden: menterage,
    width: 80
  },
  {
    title: "外币币种",
    dataIndex: "currencyType",
    defaultHidden: currencyType,
    width: 80
  },
  {
    title: "数量",
    dataIndex: "ncnum",
    defaultHidden: menterage,
    slots: {customRender: "ncnum"},
    width: 150
  },
  {
    title: "外币金额",
    dataIndex: "nfrat",
    slots: {customRender: "nfrat"},
    defaultHidden: currencyType,
  },
  {
    title: "本币借方金额",
    dataIndex: "md",
    slots: {customRender: "md"}
  },
  {
    title: "本币贷方金额",
    dataIndex: "mc",
    slots: {customRender: "mc"}
  }
])
const tableColumns2 = ref([
  {
    title: "操作",
    dataIndex: "caozuo",
    slots: {customRender: "caozuo"}
  },
  {
    title: "科目编码",
    dataIndex: "ccode",
    align: "left"
  },
  {
    title: "科目名称",
    dataIndex: "ccodeName",
    align: "left"
  },
  {
    title: "方向",
    dataIndex: "bprogerty",
    slots: {customRender: "bprogerty"},
  },
  {
    title: "辅助项",
    dataIndex: "fuzhu",
  },
  {
    title: "计量单位",
    dataIndex: "menterage",
    /*  defaultHidden: menterage,*/
  },
  {
    title: "外币币种",
    dataIndex: "currencyType",
    /*  defaultHidden: currencyType,*/
  },
  {
    title: '期初余额',
    dataIndex: 'jfy',
    children: [
      {
        title: '数量',
        dataIndex: 'sjnum',
        key: '7-1',
        align: 'right',
        slots: {customRender: 'sjnum'},
      },
      {
        title: '原币金额',
        dataIndex: 'sjwb',
        key: '7-2',
        align: 'right',
        slots: {customRender: 'sjwb'},
      },
      {
        title: '本币金额',
        dataIndex: 'sjmoney',
        key: '7-3',
        align: 'right',
        slots: {customRender: 'sjmoney'},
      }
    ],
  },
  {
    title: '累计借方',
    dataIndex: 'jf',
    children: [
      {
        title: '数量',
        dataIndex: 'sjnum',
        key: '8-1',
        align: 'right',
        slots: {customRender: 'sjnum'},
      },
      {
        title: '原币金额',
        dataIndex: 'sjwb',
        key: '8-2',
        align: 'right',
        slots: {customRender: 'sjwb'},
      },
      {
        title: '本币金额',
        dataIndex: 'sjmoney',
        key: '8-3',
        align: 'right',
        slots: {customRender: 'sjmoney'},
      }
    ],
  },
  {
    title: '累计贷方',
    dataIndex: 'df',
    children: [
      {
        title: '数量',
        dataIndex: 'sdnum',
        key: '9-1',
        align: 'right',
        slots: {customRender: 'sdnum'},
      },
      {
        title: '原币金额',
        dataIndex: 'sdwb',
        key: '9-2',
        align: 'right',
        slots: {customRender: 'sdwb'},
      },
      {
        title: '本币金额',
        dataIndex: 'sdmoney',
        key: '9-3',
        align: 'right',
        slots: {customRender: 'sdmoney'},
      }
    ],
  },
  {
    title: '年初余额',
    dataIndex: 'ye',
    children: [
      {
        title: '数量',
        dataIndex: 'synum',
        key: '10-1',
        align: 'right',
        slots: {customRender: 'synum'},
      },
      {
        title: '原币金额',
        dataIndex: 'sywb',
        key: '10-2',
        align: 'right',
        slots: {customRender: 'sywb'},
      },
      {
        title: '本币金额',
        dataIndex: 'symoney',
        key: '10-3',
        align: 'right',
        slots: {customRender: 'symoney'},
      }
    ],
  }
])
// 是否非企业机构标识 1是
const icorp = ref('')
// 公司码
const cocode = ref('')
// 账套详细信息
const databaseinfo = ref('')
// 1是 否独立账套
const independent = ref(true)
const cwflag = ref(false);
const ysflag = ref(false);
const modal3Visible = ref(false);
const selectDataBaseValue = ref('');
const selectSearchValue = ref('ccode');
const bwb = ref('');
// 所有账套集合
const databaseAll = ref([])
const inputsearchtext = ref('')
const templateSelected = ref('')
const standardSelected = ref('')

// 这是示例组件
const [registerImportPage, {openModal: openImprotPage}] = useModal();
const tableRef = ref(null)
const windowWidth = (document.documentElement.clientWidth - 70)
const windowHeight = (window.innerHeight - 300)
const qujian = ref('');
const totalColumnWidth = ref(0)
const [registerTable, {
  reload, setColumns,
  getColumns,
  getDataSource,
  setTableData,
  setPagination,
  getPaginationRef, getSelectRows
}] = useTable();
// 过滤漏斗
function filterSearch() {
  pageParameter.filterConditon.amountMinMd = pageParameter.filterConditon.amountMinMd === '' ? '0' : pageParameter.filterConditon.amountMinMd
  pageParameter.filterConditon.amountMaxMd = pageParameter.filterConditon.amountMaxMd === '' ? '0' : pageParameter.filterConditon.amountMaxMd
  pageParameter.filterConditon.amountMinMc = pageParameter.filterConditon.amountMinMc === '' ? '0' : pageParameter.filterConditon.amountMinMc
  pageParameter.filterConditon.amountMaxMc = pageParameter.filterConditon.amountMaxMc === '' ? '0' : pageParameter.filterConditon.amountMaxMc

  let all = tableDataAll.value
  tableData.value = all.filter((a) =>
    parseFloat(a.md) >= parseFloat(pageParameter.filterConditon.amountMinMd) && parseFloat(a.md) <= parseFloat(pageParameter.filterConditon.amountMaxMd)
    && parseFloat(a.mc) >= parseFloat(pageParameter.filterConditon.amountMinMc) && parseFloat(a.mc) <= parseFloat(pageParameter.filterConditon.amountMaxMc)
  );
}

function selectSearch2() {
  console.log('11111' + '[' + inputsearchtext.value + ']');
  if (inputsearchtext.value !== '') {
    console.log('11111' + '[' + inputsearchtext.value + ']确定数据不是空');
    findAllInitialBalanceFuZhu()
  }
}

// 检索条件
async function selectSearch() {
  let all = tableDataAll.value
  if (selectSearchValue.value === 'ccode') {
    tableData.value = all.filter((a) => a.ccode.indexOf(inputsearchtext.value) !== -1);
  }
  if (selectSearchValue.value === 'ccodeName') {
    tableData.value = all.filter((a) => a.ccodeName.indexOf(inputsearchtext.value) !== -1);
  }
  if (selectSearchValue.value === 'cclass') {
    tableData.value = all.filter((a) => a.cclass.indexOf(inputsearchtext.value) !== -1);
  }
  if (selectSearchValue.value === 'bprogerty') {
    tableData.value = all.filter((a) => a.bprogerty.indexOf(inputsearchtext.value) !== -1);
  }
  if (selectSearchValue.value === 'fuzhu') {
    tableData.value = all.filter((a) => a.fuzhu.indexOf(inputsearchtext.value) !== -1);
  }
  if (selectSearchValue.value === 'menterage') {
    tableData.value = all.filter((a) => a.menterage.indexOf(inputsearchtext.value) !== -1);
  }
  if (selectSearchValue.value === 'currencyType') {
    tableData.value = all.filter((a) => a.currencyType.indexOf(inputsearchtext.value) !== -1);
  }

  if (inputsearchtext.value === '') {
    findAllInitialBalanceFuZhu()
  }
}

async function modalok() {
  modal3Visible.value = false
}

async function emptyAllbtn(data) {
  if (data.accvouid !== null) {
    const databasenum = database.value + '-' + iyearselected.value
    await useRouteApi(emptyAll, {schemaName: databasenum})(data.accvouid)
    findAllInitialBalanceFuZhu();
  }
}

// 期初记账
async function qcjzbtn(ibook: any) {
  const databasenum = database.value + '-' + iyearselected.value
  if (ibook === '0') {
    // 查询本年凭证是否记账
    const pzibook = await useRouteApi(pzjzibook, {schemaName: databasenum})({
      iyear: iyearselected.value,
      databasenum: databasenum
    })
    if (pzibook > 0) {
      message.error('本年凭证已经记账，不能恢复期初余额记账。请取消凭证记账后再试！');
      return;
    }
  }
  ibookflg.value = !ibookflg.value;
  await useRouteApi(qcjz, {schemaName: databasenum})({
    iyear: iyearselected.value,
    ibook: ibook,
    databasenum: databasenum
  })
  findAllInitialBalanceFuZhu();
}

// 倒计时到时间触发
function onFinish() {
  console.log('30分钟到了');
}

// 试算平衡
async function ssphbtn() {
  ssphlist.value = []
  const databasenum = databaseTrue.value;
  const list1 = await useRouteApi(ssph, {schemaName: databasenum})({
    iyear: iyearselected.value,
    standardSelected: standardSelected.value,
    databasenum: databasenum
  })
  let a = list1.filter((li1) => li1.flag === '1');
  let b = list1.filter((li1) => li1.flag === '2');
  let max = a.length < b.length ? b.length : a.length
  for (let i = 0; i < max; i++) {
    ssphlist.value.push(
      {
        name: a[i].name,
        md: a[i].md,
        mc: a[i].mc,
        fx: a[i].fx,
        yue: a[i].yue,
        id: parseInt(a[i].id),
        name2: b[i].name,
        md2: b[i].md,
        mc2: b[i].mc,
        fx2: b[i].fx,
        yue2: b[i].yue,
      }
    )
  }

  let list3 = list1.filter((li1) => li1.flag === '5');
  if (list3[0].cwOrys === '0') {
    let cw = list1.filter((li1) => li1.flag === '3');
    ssphtext.value = '\xa0\xa0' + cw[0].name;
    cwflag.value = true
  } else {
    ssphlist2.value = []
    let aa = list1.filter((li1) => li1.flag === '11');
    let bb = list1.filter((li1) => li1.flag === '22');
    let max = aa.length < bb.length ? bb.blength : aa.length
    for (let i = 0; i < max; i++) {
      ssphlist2.value.push(
        {
          name: aa[i].name,
          md: aa[i].md,
          mc: aa[i].mc,
          fx: aa[i].fx,
          yue: aa[i].yue,
          id: parseInt(aa[i].id),
          name2: bb[i].name,
          md2: bb[i].md,
          mc2: bb[i].mc,
          fx2: bb[i].fx,
          yue2: bb[i].yue,
        }
      )
    }
    let cw = list1.filter((li1) => li1.flag === '3');
    let ys = list1.filter((li1) => li1.flag === '4');
    ssphtext.value = '\xa0\xa0' + cw[0].name;
    ystext.value = '\xa0\xa0' + ys[0].name;
    ysflag.value = true
  }
  modal1Visible.value = true;
}

// 导出excel
function exportexcel() {
  var list = []; // 没有单价金额/外币金额
  var list2 = []; // 有单价金额/外币金额
  for (let i = 0; i < tableDataAll.value.length; i++) {
    // 所有末级科目
    list.push({
      ccode: tableDataAll.value[i].ccode,
      ccodeName: tableDataAll.value[i].ccodeName,
      md: tableDataAll.value[i].md == 0 ? '' : tableDataAll.value[i].md,
      mc: tableDataAll.value[i].mc == 0 ? '' : tableDataAll.value[i].mc,
      num: tableDataAll.value[i].md > 0 ? tableDataAll.value[i].ndS == 0 ? '' : tableDataAll.value[i].ndS : tableDataAll.value[i].ncS == 0 ? '' : tableDataAll.value[i].ncS,
      lv: tableDataAll.value[i].mdF,
      wnmoney: tableDataAll.value[i].md > 0 ? tableDataAll.value[i].nfratMd == 0 ? '' : tableDataAll.value[i].nfratMd : tableDataAll.value[i].nfratMc == 0 ? '' : tableDataAll.value[i].nfratMc,
      pjCsettle: tableDataAll.value[i].pjCsettle,
      pjId: tableDataAll.value[i].pjId,
      pjDate: tableDataAll.value[i].pjDate,
      pjUnitName: tableDataAll.value[i].pjUnitName,
    });
  }


  list = list2.length > 0 ? list2 : list;
  jsonToSheetXlsx({
    data: list,
    header: {
      ccode: '科目编码',
      ccodeName: '科目名称',
      md: '借方金额',
      mc: '贷方金额',
      num: '数量',
      lv: '汇率',
      wnmoney: '原币金额',
      pjCsettle: '结算方式编码',
      pjId: '结算票据号',
      pjDate: '票据日期',
      pjUnitName: '对方单位名称',
    },
    filename: '期初余额导入模板.xlsx',
  });
}

// 导入弹框
const importPop = () => {
  const databasenum = databaseTrue.value;
  openImprotPage(true, {
    iyear: iyearselected.value,
    databasenum: databasenum,
  });
};

async function reloadProjects() {
  findAllInitialBalanceFuZhu();
}

// 清空处理
const emptyAll2 = () => {
  const rowinfo = getSelectRows();
  if (rowinfo.length === 0) {
    message.error('至少选择一条数据!');
    return false;
  }
  Modal.confirm({
    title: '确定清空吗?',
    icon: createVNode(ExclamationCircleOutlined),
    content: createVNode('div', {style: 'color:red;'}, '清除后不可恢复'),
    onOk() {
      const delArr = []; // 期初ID_科目编码_借方_贷方_年度
      for (let i = 0; i < rowinfo.length; i++) {
        var ccode = '';
        var accvouid = '';
        for (let j = 0; j < tableData.value.length; j++) {
          if (
            tableData.value[j].ccode.indexOf(rowinfo[i].ccode) > -1 &&
            tableData.value[j].bend === '1'
          ) {
            ccode = tableData.value[j].ccode;
            accvouid = tableData.value[j].accvouid;
          }
        }
        delArr.push(accvouid + '_' + ccode + '_' + rowinfo[i].md + '_' + rowinfo[i].mc);
      }
      del(uniq(delArr), iyearselected.value);
    },
    onCancel() {
      console.log('Cancel');
    },
    class: 'test',
  });
};

onMounted(async () => {
  console.log("页面加载执行方法");
});

const dynamicAdReload = async (obj) => {
  console.log("kaishi===========");
  console.log(obj);
  console.log("kaishi===========");
  database.value = obj.accId;
  databaseTrue.value = obj.accountMode;
  iyearselected.value = obj.year;

  // 获取对应的账套科目 所属的 会计准则、科目模板
  let codelistall = await useRouteApi(groupStandardAndTemplate, {schemaName: databaseTrue.value})({
    databaseNum: databaseTrue.value,
    iyear: iyearselected.value
  });
  if (codelistall.length > 0) {
    templateSelected.value = codelistall[0].templateId
    standardSelected.value = codelistall[0].uniqueAccStandard
  } else {
    console.log("账套下没有科目")
    // 根据账套信息表重新获取 会计准则、科目模板
    let tplateInfo = await findTemplateByAccId(database.value)
    templateSelected.value = tplateInfo.id
    standardSelected.value = tplateInfo.uniqueAccStandard
  }
  findAllInitialBalanceFuZhu();
  resetDynamicColumnData()
}

/**
 * 简单数组去重
 * @param array
 */
function uniq(array) {
  var temp = []; //一个新的临时数组
  for (var i = 0; i < array.length; i++) {
    if (temp.indexOf(array[i]) == -1) {
      temp.push(array[i]);
    }
  }
  return temp;
}

const del = async (delArr: any, iyear: any) => {
  const databasenum = databaseTrue.value;
  await useRouteApi(delInitalBalance, {schemaName: databasenum})({str: delArr, iyear: iyear})
  findAllInitialBalanceFuZhu();
};

// 提交弹框
const submitPop = async () => {
  var aa: any = true;
  const databasenum = databaseTrue.value;
  // 期初是否记账
  const qcjz = await useRouteApi(findBySubjectInitalBalabceIbook, {schemaName: databasenum})({
    iyear: iyearselected.value,
    databasenum: databasenum
  })
  if (qcjz !== 0) {
    aa = false;
    message.error('提示：期初已记账不能进行编辑');
  }
  // 1、判断当年会计期间是否结过账
  const iperio = await useRouteApi(findByIperiodFlag, {schemaName: databasenum})({
    iyear: iyearselected.value,
    databasenum: databasenum
  })
  if (iperio === 0) {
    // 下一步判断-2、判断当年凭证是否已记账
    const accvoucheribook = await useRouteApi(findByAccvoucherIbook, {schemaName: databasenum})({
      iyear: iyearselected.value,
      databasenum: databasenum
    })
    if (accvoucheribook === 0) {
      // 下一步判断-3、判断任务管理中是否存在记账任务
      const task1 = await useRouteApi(findByFunctionModule, {schemaName: databasenum})(iyearselected.value)
      // 没有任务；增加一条本人期初余额任务
      if (task1.length === 0) {
        aa = true;
      } else {
        for (let i = 0; i < task1.length; i++) {
          if (task1[i].functionModule === '凭证记账' && task1[i].caozuoUnique != null) {
            if (task1[i].state === '0') {
              aa = false;
              message.error(
                '发现操作员【' +
                task1[i].caozuoUnique +
                '】独占任务异常，请联系财务主管进行独占任务清理!'
              );
              break;
            } else {
              // 判断是不是自己；动态获取
              if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                aa = false;
                message.error(
                  '提示：任务冲突！操作员【' +
                  task1[i].caozuoUnique +
                  '】正在进行凭证记账操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                );
                break;
              }
            }
          }
          if (task1[i].functionModule === '月末结账' && task1[i].caozuoUnique != null) {
            if (task1[i].state === '0') {
              aa = false;
              message.error(
                '发现操作员【' +
                task1[i].caozuoUnique +
                '】独占任务异常，请联系财务主管进行独占任务清理!'
              );
              break;
            } else {
              // 判断是不是自己；动态获取
              if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                aa = false;
                message.error(
                  '提示：任务冲突！操作员【' +
                  task1[i].caozuoUnique +
                  '】正在进行月末结账处理，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                );
                break;
              }
            }
          }
          if (task1[i].functionModule === '科目编辑' && task1[i].caozuoUnique != null) {
            if (task1[i].state === '0') {
              aa = false;
              message.error(
                '发现操作员【' +
                task1[i].caozuoUnique +
                '】独占任务异常，请联系财务主管进行独占任务清理!'
              );
              break;
            } else {
              // 判断是不是自己；动态获取
              if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                aa = false;
                message.error(
                  '提示：任务冲突！操作员【' +
                  task1[i].caozuoUnique +
                  '】正在进行会计科目编辑操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                );
                break;
              }
            }
          }
          if (task1[i].functionModule === '转账生成' && task1[i].caozuoUnique != null) {
            if (task1[i].state === '0') {
              aa = false;
              message.error(
                '发现操作员【' +
                task1[i].caozuoUnique +
                '】独占任务异常，请联系财务主管进行独占任务清理!'
              );
              break;
            } else {
              // 判断是不是自己；动态获取
              if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                aa = false;
                message.error(
                  '提示：任务冲突！操作员【' +
                  task1[i].caozuoUnique +
                  '】正在进行转账生成编辑操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                );
                break;
              }
            }
          }
          if (task1[i].functionModule === '期初余额' && task1[i].caozuoUnique != null) {
            if (task1[i].state === '0') {
              aa = false;
              message.error(
                '发现操作员【' +
                task1[i].caozuoUnique +
                '】独占任务异常，请联系财务主管进行独占任务清理!'
              );
              break;
            } else {
              // 判断是不是自己；动态获取
              if (task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                aa = false;
                message.error(
                  '提示：任务冲突！操作员【' +
                  task1[i].caozuoUnique +
                  '】正在进行期初余额编辑操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                );
                break;
              }
            }
          }
        }
      }
    } else {
      aa = false;
      message.error('本年度凭证已记账，不能进行期初余额编辑，请取消凭证记账后再运行本功能');
    }
  } else {
    aa = false;
    message.error('本年已发生结账月份，不能进行期初余额编辑，请取消结账和记账后再运行本功能');
  }
  if (aa) {
    datainfo.value.md = md.value == '' ? '' : String(md.value).replace(/,/g, '');
    datainfo.value.mc = mc.value == '' ? '' : String(mc.value).replace(/,/g, '');
    datainfo.value.ndS = md.value == '' ? '' : ncnum.value;
    datainfo.value.ncS = mc.value == '' ? '' : ncnum.value;
    datainfo.value.nfrat = nfrat.value == '' ? '' : String(nfrat.value).replace(/,/g, '');
    modal2Visible.value = false;
    await useRouteApi(saveData, {schemaName: databasenum})(datainfo.value)
  }
};

const setModal1Visible3 = async (data: any) => {
  datainfo.value = data;
  modal2Visible.value = true;
  // 数量核算
  numflg.value = data.menterage !== '';
  // 外币金额
  nfratflg.value = data.currencyType !== '';

  md.value = data.md > 0 ? money(data.md) : '';
  mc.value = data.mc > 0 ? money(data.mc) : '';
  ncnum.value = data.ndS > 0 ? data.ndS : data.ncS;
  nfrat.value = data.nfrat > 0 ? money(data.nfrat) : '';
};

const setModal1Visible2 = async (data: any) => {
  datainfo.value = data;
  var fuzhus = xiushiFuZhu(data);
  const map = await useRouteApi(findAllSubjectInitialBalanceFuZhuList, {schemaName: databaseTrue.value})({
    iyear: iyearselected.value,
    ccode: data.ccode,
    databasenum: databaseTrue.value
  })
  let minDate = await findYearMinDate(database.value, iyearselected.value);
  minDate = findLastDate(minDate);
  openEditPage(true, {
    map: map, fuzhus: fuzhus, maxDate: minDate
  })
};

function findLastDate(date) {
  date = new Date(date);
  date = 0 + (date).getTime() - 1000 * 60 * 60 * 24;
  date = new Date(date);
  date = date.getFullYear() + "-" + (date.getMonth() > 9 ? (date.getMonth() + 1) : "0" + (date.getMonth() + 1)) + "-" + (date.getDate() > 9 ? (date.getDate()) : "0" + (date.getDate()));
  return date;
}

function findNextDate(date) {
  date = new Date(date);
  date = 0 + (date).getTime() + 1000 * 60 * 60 * 24;
  date = new Date(date);
  date = date.getFullYear() + "-" + (date.getMonth() > 9 ? (date.getMonth() + 1) : "0" + (date.getMonth() + 1)) + "-" + (date.getDate() > 9 ? (date.getDate()) : "0" + (date.getDate()));
  return date;
}

function xiushiFuZhu(data) {
  let fuzhus = [];
  if (data.bperson == '1') {
    fuzhus.push('fzEmp');
  }
  if (data.bdept == '1') {
    fuzhus.push('fzDept');
  }
  if (data.bcus == '1') {
    fuzhus.push('fzCustom');
  }
  if (data.bsup == '1') {
    fuzhus.push('fzGys');
  }
  if (data.bitem == '1') {
    fuzhus.push('fzItemClass');
  }
  if (data.bitem == '1') {
    fuzhus.push('fzItem');
  }
  if (data.cdfine1 == '1') {
    fuzhus.push('1');
  }
  if (data.cdfine2 == '1') {
    fuzhus.push('2');
  }
  if (data.cdfine3 == '1') {
    fuzhus.push('3');
  }
  if (data.cdfine4 == '1') {
    fuzhus.push('4');
  }
  if (data.cdfine5 == '1') {
    fuzhus.push('5');
  }
  if (data.cdfine6 == '1') {
    fuzhus.push('6');
  }
  if (data.cdfine7 == '1') {
    fuzhus.push('7');
  }
  if (data.cdfine8 == '1') {
    fuzhus.push('8');
  }
  if (data.cdfine9 == '1') {
    fuzhus.push('9');
  }
  if (data.cdfine10 == '1') {
    fuzhus.push('10');
  }
  if (data.cdfine11 == '1') {
    fuzhus.push('11');
  }
  if (data.cdfine12 == '1') {
    fuzhus.push('12');
  }
  if (data.cdfine13 == '1') {
    fuzhus.push('13');
  }
  if (data.cdfine14 == '1') {
    fuzhus.push('14');
  }
  if (data.cdfine15 == '1') {
    fuzhus.push('15');
  }
  if (data.cdfine16 == '1') {
    fuzhus.push('16');
  }
  if (data.cdfine17 == '1') {
    fuzhus.push('17');
  }
  if (data.cdfine18 == '1') {
    fuzhus.push('18');
  }
  if (data.cdfine19 == '1') {
    fuzhus.push('19');
  }
  if (data.cdfine20 == '1') {
    fuzhus.push('20');
  }
  if (data.cdfine21 == '1') {
    fuzhus.push('21');
  }
  if (data.cdfine22 == '1') {
    fuzhus.push('22');
  }
  if (data.cdfine23 == '1') {
    fuzhus.push('23');
  }
  if (data.cdfine24 == '1') {
    fuzhus.push('24');
  }
  if (data.cdfine25 == '1') {
    fuzhus.push('25');
  }
  if (data.cdfine26 == '1') {
    fuzhus.push('26');
  }
  if (data.cdfine27 == '1') {
    fuzhus.push('27');
  }
  if (data.cdfine28 == '1') {
    fuzhus.push('28');
  }
  if (data.cdfine29 == '1') {
    fuzhus.push('29');
  }
  if (data.cdfine30 == '1') {
    fuzhus.push('30');
  }
  var ss = '';
  for (let i = 0; i < fuzhus.length; i++) {
    if (i == 0) {
      ss = ss + fuzhus[i];
    } else {
      ss = ss + "," + fuzhus[i];
    }
  }
  return ss;
}

// 编辑期初前查询科目表是否锁定
const editflg = async (data: any) => {
  const databasenum = databaseTrue.value;
  if (data === '开始编辑') {
    var aa: any = true;
    // 期初是否记账
    const qcjz = await useRouteApi(findBySubjectInitalBalabceIbook, {schemaName: databasenum})({
      iyear: iyearselected.value,
      databasenum: databasenum
    })
    if (qcjz !== 0) {
      aa = false;
      message.error('提示：期初已记账不能进行编辑');
    }
    // 1、判断当年会计期间是否结过账
    const iperio = await useRouteApi(findByIperiodFlag, {schemaName: databasenum})({
      iyear: iyearselected.value,
      databasenum: databasenum
    })
    if (iperio === 0) {
      // 下一步判断-2、判断当年凭证是否已记账
      const accvoucheribook = await useRouteApi(findByAccvoucherIbook, {schemaName: databasenum})({
        iyear: iyearselected.value,
        databasenum: databasenum
      })
      if (accvoucheribook === 0) {
        // 下一步判断-3、判断任务管理中是否存在记账任务
        const task1 = await useRouteApi(findByFunctionModule, {schemaName: databasenum})({
          iyear: iyearselected.value,
          databasenum: databasenum
        })
        // 没有任务；增加一条本人期初余额任务
        if (task1.length === 0) {
          aa = true;
        } else {
          for (let i = 0; i < task1.length; i++) {
            if (task1[i].functionModule === '凭证记账' && task1[i].caozuoUnique != null) {
              if (task1[i].state === '0') {
                aa = false;
                message.error(
                  '发现操作员【' +
                  task1[i].caozuoUnique +
                  '】独占任务异常，请联系财务主管进行独占任务清理!'
                );
                break;
              } else {
                // 判断是不是自己；动态获取
                if (task1[i].caozuoUnique !== null && task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                  aa = false;
                  message.error(
                    '提示：任务冲突！操作员【' +
                    task1[i].caozuoUnique +
                    '】正在进行凭证记账操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                  );
                  break;
                }
              }
            }
            if (task1[i].functionModule === '月末结账' && task1[i].caozuoUnique != null) {
              if (task1[i].state === '0') {
                aa = false;
                message.error(
                  '发现操作员【' +
                  task1[i].caozuoUnique +
                  '】独占任务异常，请联系财务主管进行独占任务清理!'
                );
                break;
              } else {
                // 判断是不是自己；动态获取
                if (task1[i].caozuoUnique !== null && task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                  aa = false;
                  message.error(
                    '提示：任务冲突！操作员【' +
                    task1[i].caozuoUnique +
                    '】正在进行月末结账处理，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                  );
                  break;
                }
              }
            }
            if (task1[i].functionModule === '科目编辑' && task1[i].caozuoUnique != null) {
              if (task1[i].state === '0') {
                aa = false;
                message.error(
                  '发现操作员【' +
                  task1[i].caozuoUnique +
                  '】独占任务异常，请联系财务主管进行独占任务清理!'
                );
                break;
              } else {
                // 判断是不是自己；动态获取
                if (task1[i].caozuoUnique !== null && task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                  aa = false;
                  message.error(
                    '提示：任务冲突！操作员【' +
                    task1[i].caozuoUnique +
                    '】正在进行会计科目编辑操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                  );
                  break;
                }
              }
            }
            if (task1[i].functionModule === '转账生成' && task1[i].caozuoUnique != null) {
              if (task1[i].state === '0') {
                aa = false;
                message.error(
                  '发现操作员【' +
                  task1[i].caozuoUnique +
                  '】独占任务异常，请联系财务主管进行独占任务清理!'
                );
                break;
              } else {
                // 判断是不是自己；动态获取
                if (task1[i].caozuoUnique !== null && task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                  aa = false;
                  message.error(
                    '提示：任务冲突！操作员【' +
                    task1[i].caozuoUnique +
                    '】正在进行转账生成编辑操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                  );
                  break;
                }
              }
            }
            if (task1[i].functionModule === '期初余额' && task1[i].caozuoUnique != null) {
              taskinfo.value = task1[i];
              if (task1[i].state === '0') {
                aa = false;
                message.error(
                  '发现操作员【' +
                  task1[i].caozuoUnique +
                  '】独占任务异常，请联系财务主管进行独占任务清理!'
                );
                break;
              } else {
                // 判断是不是自己；动态获取
                if (task1[i].caozuoUnique !== null && task1[i].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
                  aa = false;
                  message.error(
                    '提示：任务冲突！操作员【' +
                    task1[i].caozuoUnique +
                    '】正在进行期初余额编辑操作，不能进行期初余额编辑，请销后再试，或联系财务主管清理该记账任务'
                  );
                  break;
                }
              }
            }
          }
        }
      } else {
        aa = false;
        message.error('本年度凭证已记账，不能进行期初余额编辑，请取消凭证记账后再运行本功能');
      }
    } else {
      aa = false;
      message.error('本年已发生结账月份，不能进行期初余额编辑，请取消结账和记账后再运行本功能');
    }

    if (aa) {
      // deadline.value = Date.now() + 1000 * 60 * 30; // 30分钟
      taskinfo.value = await useRouteApi(saveTaskInfo, {schemaName: databasenum})({
        id: taskinfo.value === '' ? null : taskinfo.value.id,
        iyear: iyearselected.value,
        functionModule: '期初余额',
        username: useUserStoreWidthOut().getUserInfo.id,
        databasenum: databasenum,
      })
      edittext.value = data === '开始编辑' ? '退出编辑' : '开始编辑';
      roweditflg2.value = true;
    }
  } else {
    deadline.value = '';
    edittext.value = data === '退出编辑' ? '开始编辑' : '退出编辑';
    await useRouteApi(delFunctionModule, {schemaName: databasenum})({
      id: taskinfo.value.id,
      databasenum: databasenum
    })
    taskinfo.value = '';
    roweditflg2.value = false;
  }
};

// 是否末级科目查询
const lastCodechecked = async (val) => {
  lastCode.value = val.target.checked;
  findAllInitialBalanceFuZhu();
};

const yearChange = async (val) => {
  iyearselected.value = val;
  findAllInitialBalanceFuZhu();
};

const saveData = async (data: any) => {
  const databasenum = databaseTrue.value;
  await useRouteApi(saveSubjectInitialBalance, {schemaName: databasenum})({
    params: data,
    accId: databaseTrue.value,
    userName: userinfo.value.username,
    databasenum: databasenum,
  })
  findAllInitialBalanceFuZhu();
};

// 获取期初余额
const findAllInitialBalanceFuZhu = async () => {
  // 从集团获取账套基本信息
  const datainfo = await findByAccId(database.value);
  let period = await findPeriodByYaer(database.value, iyearselected.value)
  qujian.value = period // 本位币
  if (null == period || period.substring(4, 6) == '01') {
    pageParameter.queryMark = '1'
  } else {
    pageParameter.queryMark = '2'
  }
  bwb.value = datainfo.currencyCh // 本位币
  //begintime.value = datainfo.accStartDate.split(' ')[0]; // 账套启用日期
  independent.value = datainfo.independent > 0 ? true : false;  // 1是独立账套 0是集团账套

  // 查询本年是否期初记账
  const qcjzsflg = await useRouteApi(qcjzsum, {schemaName: databaseTrue.value})({
    iyear: iyearselected.value,
    databasenum: databaseTrue.value
  })
  if (qcjzsflg > 0) {
    ibookflg.value = true;
  }

  const databasenum = databaseTrue.value;
  tableData.value = []
  tableDataAll.value = []
  loading.value = true;
  const a = await useRouteApi(findAllSubjectInitialBalanceFuZhu, {schemaName: databasenum})({
    iyear: iyearselected.value,
    lastCode: lastCode.value,
    databasenum: databasenum
  })
  tableDataAll.value = a.tablesData;
  tableData.value = a.tablesData;
  let tableColumns = a.tableColumns;
  if (tableColumns === 'all') {
    menterage.value = false
    currencyType.value = false
  }
  if (tableColumns === 'menterage') {
    menterage.value = false
  }
  if (tableColumns === 'currencyType') {
    currencyType.value = false
  }
  loading.value = false;
};

// 金额格式化
function money(val: any) {
  if (val == null) val = '';
  val = val.toString().replace(/\$|\,/g, '');
  if (isNaN(val)) {
    val = '0';
  }
  const sign = val === (val = Math.abs(val));
  val = Math.floor(val * 100 + 0.50000000001);
  let cents = val % 100;
  val = Math.floor(val / 100).toString();
  if (cents < 10) {
    cents = '0' + cents;
  }
  for (let i = 0; i < Math.floor((val.length - (1 + i)) / 3); i++) {
    val =
      val.substring(0, val.length - (4 * i + 3)) + ',' + val.substring(val.length - (4 * i + 3));
  }
  return (sign ? '' : '') + val + '.' + cents;
}

async function findAlliyear() {
  // 会计期间年度
  const res: any = await findAllIperiodIyear(database.value);
  iyearlist.value = res;
  iyearselected.value = res[0].iyear;
}

const getAdObjInfoByCoCode = (value, type, accList) => {
  let list = accList.filter(item => item[type] == value)
  return list.length > 0 ? list[0] : null
}

const [registerEditPage, {openModal: openEditPage}] = useModal()

/*start栏目设置*/
const onChangeSwitch = async (v) => { // 动态列
  pageParameter.queryMark = v
  resetDynamicColumnData()
}
const {createConfirm, createWarningModal} = useMessage();
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});
const lanMuData = {
  'accId': '',
  'menuName': '辅助科目期初余额',
  'type': '',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username
}
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
  lanMuData.type = pageParameter.queryMark == '1' ? '标准' : '累计'
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
    // pageReload()
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
  //let newA = JSON.parse(JSON.stringify(accvoucherStore.getColumns(pageParameter.queryMark)))
  let newA = JSON.parse(JSON.stringify(pageParameter.queryMark == '1' ? tableColumns.value : tableColumns2.value))
  newA = assemblyDynamicColumn(dynamicColumnData.value, newA)
  setColumns(newA)
  initTableWidth(newA)
}

function initTableWidth(thisCs) {
  let total = 60 + 60 // 选择列与序号列
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })
  if (total > windowWidth) {
    let f = 0
    if (visible.value) f = 380
    totalColumnWidth.value = Number(windowWidth) - f
    tableRef.value.$el.style.setProperty('width', (windowWidth + 62 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 380) < total) total -= (total - (windowWidth - 380))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 62) + 'px')
  }
}

/*栏目设置end*/
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
<style scoped>
.app-container {
  padding: 0px;
  margin: 10px 10px 5px;
}
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
}
</style>
