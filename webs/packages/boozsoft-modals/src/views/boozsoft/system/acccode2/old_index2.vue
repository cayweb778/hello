<template>
  <div>
    <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 40%;text-align: center;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 30px;">会计科目</b>
        </div>
        <div class="ant-btn-group" style="float: right;">
          <button
            v-if="!ibookflg"
            style="float: left"
            type="button"
            class="ant-btn ant-btn-me"
            @click="editflg(edittext)"
          ><span>{{ edittext }}</span></button
          >
          <span v-if="roweditflg">
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
              @click="openAddPage('0')"
              v-if="independent"
            >
              <span>新增</span></button
            >
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
              @click="openAddPage('1')"
            >
              <span>新增下级</span></button>
            <button type="button" class="ant-btn ant-btn-me" @click="openRowEdit"><span>修改</span></button>
            <button type="button" class="ant-btn ant-btn-me" @click="delCode"><span>删除</span></button>
            <button type="button" class="ant-btn ant-btn-me" @click="editflag"><span>启&停</span></button>

            <!--           查询是否独立账套  是否独立账套（1）反之集团账套（0）-->
            <span v-if="independent">
              <button type="button" class="ant-btn ant-btn-me" @click="copyCodeView"
              ><span>复制</span></button
              >
              <button type="button" class="ant-btn ant-btn-me" @click="openImportExcel()"
              ><span>导入</span></button
              >
            </span>
            <!-- 公司：引入 -->
            <span v-if="!independent">
              <button type="button" class="ant-btn ant-btn-me" @click="openOrgKemu"><span>引入</span></button>
            </span>
          </span>
          <button type="button" class="ant-btn ant-btn-me" @click="codeContrastView"><span>对照</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(), deltask()"><span>退出</span></button>
        </div>
      </div>
      <div style="clear: none"/>
      <div style="margin-top: -30px;margin-left: 80px;">
        <div style="display: inline-block;float: left;margin-top: -25px;">
          <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">共 {{ tableTotal }} 条记录</div>
        <div style="display: inline-block;position:absolute;top:85px;left:45%;font-size: 10px;color:#999999;">
          科目级次：{{ jici }}
        </div>
        <div style="float: right; margin-left: 10px;">
          <a-button @click="pageReload">
            <SyncOutlined :style="{ fontSize: '14px' }" />
          </a-button>
          <a-popover placement="bottom" v-model:visible="visible3">
            <template #content>
              <DynamicColumn :defaultData="initDynamics()['J']" :dynamicData="dynamicColumnData" :lanmuInfo="lanMuData" @reload="reloadColumns"/>
              <span
                @click="pageParameter.showRulesSize = 'MAX'"
                :style="{
                  backgroundColor: pageParameter.showRulesSize === 'MAX' ? '#e3e0e0' : 'white',
                  cursor: 'pointer',
                }"
              >&emsp;&emsp;大号字体&emsp;<CheckOutlined
                v-if="pageParameter.showRulesSize === 'MAX'"
                :style="{ fontSize: '14px' }"
              />&emsp;</span
              ><br />
              <span
                @click="pageParameter.showRulesSize = 'MIN'"
                :style="{
                  backgroundColor: pageParameter.showRulesSize === 'MIN' ? '#e3e0e0' : 'white',
                  cursor: 'pointer',
                }"
              >&emsp;&emsp;小号字体&emsp;<CheckOutlined
                v-if="pageParameter.showRulesSize === 'MIN'"
                :style="{ fontSize: '14px' }"
              />&emsp;</span
              ><br />
            </template>
            <template #title>
              <b>设置表格字号</b>
            </template>
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }" />
            </a-button>
          </a-popover>
          <button
            title="导出表格"
            type="button"
            class="ant-btn"
            ant-click-animating-without-extra-node="false"
            @click="handleDownExcel()"
          >
            <UsbOutlined />
          </button>
          <button
            title="打印"
            type="button"
            class="ant-btn"
            ant-click-animating-without-extra-node="false"
            @click="openPrint()"
          >
            <PrinterOutlined />
          </button>
          <a-button title="回收站" @click="openCodeDel()">
            <DeleteOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
        </div>
        <div style="float: right; position: relative">
          <span style="float: left; position: relative">
            <a-select
              v-model:value="pageParameter.searchConditon.requirement"
              style="width: 130px"
              class="special_select"
            >
              <template v-for="item in searchConditonList">
                <a-select-option
                  v-if="
                    item.ifShow == true &&
                    item.title != '状态' &&
                    item.title.indexOf('账') < 0 &&
                    item.title != '数量核算' &&
                    item.title != '外币核算'
                  "
                  :value="item.dataIndex"
                >
                  {{ item.title }}
                </a-select-option>
              </template>
            </a-select>
            <a-input-search
              placeholder=""
              v-model:value="pageParameter.searchConditon.value"
              @search="pageSearch"
              style="width: 250px; border-radius: 4px"
            />
          </span>
        </div>
      </div>
      <div style="clear:both"/>
    </div>
    <div class="app-container">
      <div style="float: left; width: 104px; margin-top: 10px">
        <a-tabs v-model:activeKey="activeKey" @change="tabsCheck" tab-position="left" style="font-weight: bold">
          <a-tab-pane
            v-for="(item, i) in styleNamelist"
            :key="item.cclass"
            :tab="`${item.cclass !== '全部' ? i + ' ' + item.cclass : item.cclass}`"
          />
        </a-tabs>
      </div>
      <div style="width: calc(100% - 104px); float: right; margin-top: 10px">
        <BasicTable
          ref="tableRef"
          :loading="loading"
          :row-selection="{ type: 'checkbox'}"
          :class="
            pageParameter.showRulesSize == 'MAX' ? 'a-table-font-size-16' : 'a-table-font-size-12'
          "
          :scroll="{ x: totalColumnWidth, y: windowHeight }"
          size="small"
          @register="registerTable"
          @row-dbClick="rowDbClick"
          @fetch-success="fetchSuccess"
        >
          <template #ccode="{ record }">
            <u class="tableUStyle" @click="openEdit2(record)">{{ record.ccode }}</u>
          </template>
          <template #ccodeName="{ record }">
            <u class="tableUStyle" v-if="!hasBlank(record.ccodeName)" @click="openEdit2(record)">{{
                addspace(record.igrade, record.ccodeName)
              }}</u>
          </template>
          <template #bprogerty="{ record }">
            <span v-if="!hasBlank(record.bprogerty)">
               {{ record.bprogerty == '1' ? '借方' : '贷方' }}
            </span>
          </template>
          <template #currencyType="{ record }">
            <span
              v-if="
                record.currencyType !== '' &&
                record.currencyType !== null &&
                record.currencyType !== '0'
              "
            >
              {{ record.currencyType }}
            </span>
          </template>

          <template #lowerControl="{ record }">
            <span v-if="record.lowerControl == '1'">
              <CheckOutlined />
            </span>
          </template>
          <template #fuzhuControl="{ record }">
            <span v-if="record.fuzhuControl == '1'">
              <CheckOutlined />
            </span>
          </template>
          <template #bdaybook="{ record }">
            <span v-if="record.bdaybook == '1'">
              <CheckOutlined />
            </span>
          </template>
          <template #bcash="{ record }">
            <span v-if="record.bcash == '1'">
              <CheckOutlined />
            </span>
          </template>
          <template #bbank="{ record }">
            <span v-if="record.bbank == '1'">
              <CheckOutlined />
            </span>
          </template>
          <template #flag="{ record }">
            <a-tag v-if="record.flag!='11'" :color="record.flag === '1' ? 'green' : 'volcano'">
              {{ record.flag === '1' ? '启用' : '停用' }}
            </a-tag>
          </template>
          <template #action="{ record }">
            <div>
              <a-popover placement="bottom">
                <a-button style="padding: 0px 4px; height: 20px">
                  <CaretDownFilled />
                </a-button>
                <template #content>
                  <p
                    style="cursor: pointer"
                    @click="editflag(record.id, '1')"
                    v-if="record.flag == '0'"
                  ><CheckCircleOutlined /> 启用</p
                  >
                  <p
                    style="cursor: pointer"
                    @click="editflag(record.id, '0')"
                    v-if="record.flag == '1'"
                  ><CloseCircleOutlined /> 停用</p
                  >
                </template>
              </a-popover>
            </div>
          </template>
        </BasicTable>
        <div class="pagination-text" :style="{top: (windowHeight+60)+'px'}" v-show="showPaginationText">
          {{`共 ${tableTotal}条记录&emsp;每页 200 条`}}
        </div>
      </div>
    </div>

    <CodePop @throwData="modalData" @register="registerCodePopPage" />
    <ModalBend0Pop @throwData="modalBend0Data" @register="registerModalBend0Page" />
    <ModalAllPop @throwData="modalAllData" @register="registerCodeAllPopPage" />
    <AddPage @save="saveData" @closeOk="findAllData" @register="registerSavePage" />
    <EditPage @save="editData" @register="registerEditPage" />
    <ImprotExcel @save="saveExcelData" @register="registerExcelPage" />
    <Copy @save="saveCopyData" @register="registerCopyPage" />
    <Contrast @register="registerContrastPage" />
    <Look @register="registerLookPage" />
    <Print @save="loadPrint" @register="registerPrintPage" />
    <Kemu @save="pageReload" @register="registerKemuDelPage" />
    <BringOrgKemuModal @save="bringOrgKemuData" @register="registerOrgKemuPage" />
    <SimpleSaveModalPop @throwData="throwSimpleSaveData" @register="registerSimpleSavePage" />
    <a-modal v-model:visible="visible" title="信息" @ok="visibleOK">
      <p style="color: red">{{ nodelmsg }}。无法删除</p>
    </a-modal>
    <a-modal v-model:visible="contrastModel" title="选择对照年度" @ok="contrastModelOk">
      <br />
      <div style="margin-left: 30px">
        当前年度：
        <a-select v-model:value="thisiyear" style="width: 130px" class="special_select">
          <template v-for="item in accIdPeriodIyearList">
            <a-select-option :value="item">
              {{ item }}
            </a-select-option>
          </template>
        </a-select>
        &nbsp;&nbsp;&nbsp;&nbsp;对比年度：
        <a-select v-model:value="contrastiyear" style="width: 130px" class="special_select">
          <template v-for="item in accIdPeriodIyearList">
            <a-select-option :value="item">
              {{ item }}
            </a-select-option>
          </template>
        </a-select>
      </div>
      <br />
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import {findByAccStyle, findByStandardUnique} from '/@/api/accstandard/accstandard';
import {BasicTable, useTable} from '/@/components/Table';

import CodePop from './popup/modalPop.vue';
import ModalBend0Pop from './popup/modalBend0Pop.vue';
import ModalAllPop from './popup/modalAllPop.vue';
import AddPage from './popup/save.vue';
import EditPage from './popup/edit.vue';
import ImprotExcel from './popup/improtExcel.vue';
import Copy from './popup/copy.vue';
import Contrast from './popup/contrast.vue';
import Look from './popup/look.vue';
import Print from './popup/print.vue';
import Kemu from './popup/kemu-del.vue';
import BringOrgKemuModal from './popup/bringOrgKemuModal.vue';
import SimpleSaveModalPop from './popup/simpleSaveModalPop.vue';

import {computed, reactive, ref} from 'vue';
import {useModal} from '/@/components/Modal';
import {useRoute} from 'vue-router';
import {findTemplateByAccId} from '/@/api/acctemplate/acctemplate';
import {
  company_delAll,
  company_delCodekemu,
  company_delFindByCodekemu,
  company_editflag,
  company_findAllByIyear,
  company_findByPzCcodeStr,
  company_findByQcCcodeStr,
  company_saveAll,
  company_saveCodekemu,
  companyfindAll,
  groupStandardAndTemplate,
} from '/@/api/codekemu/codekemu';
import {findByAccId, findByAccIdIyearGroupBy, findDataBase,} from '/@/api/record/system/account';
import {assemblyDynamicColumn, initDynamics} from './data';
import {
  CaretDownFilled,
  CheckCircleOutlined,
  CheckOutlined,
  CloseCircleOutlined,
  DeleteOutlined,
  EditOutlined,
  PrinterOutlined,
  SettingFilled,
  SortAscendingOutlined,
  SortDescendingOutlined,
  SyncOutlined,
  UnorderedListOutlined,
  UsbOutlined,
  ProfileOutlined
} from '@ant-design/icons-vue';
import {findDbLanMuList, saveLanMuList} from '/@/api/record/system/accvoucher';
import {useUserStoreWidthOut} from '/@/store/modules/user';
import {
  delFunctionModule,
  editTaskTime,
  findByFunctionModule,
  saveTaskInfo,
} from '/@/api/subjectInitialBalance/subjectInitialBalance';
import {
  Button as AButton,
  Checkbox as ACheckbox,
  Input as AInput,
  message,
  Modal as AModal,
  Modal,
  Popconfirm as APopconfirm,
  Popover as APopover,
  Radio as ARadio,
  Select as ASelect,
  Statistic as AStatistic,
  Table as ATable,
  Tabs as ATabs,
  Tag as ATag,
} from 'ant-design-vue';

import {getCurrentAccountName, hasBlank} from '/@/api/task-api/tast-bus-api';
import {cloneDeep} from 'lodash-es';
import {useRouteApi} from '/@/utils/boozsoft/datasource/datasourceUtil';
import {kemuGlStore} from '/@/api/codekemu/kemuColumn';
import {useMessage} from '/@/hooks/web/useMessage';
import {getThisAdInfoData} from '/@/api/record/system/financial-settings';
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import {useCompanyOperateStoreWidthOut} from '/@/store/modules/operate-company';
import {useTabs} from '/@/hooks/web/useTabs';
import {useNcLogger} from '/@/utils/boozsoft/record/recordUtils';
/******************* 弹框加载中 **************************/
import {Loading} from '/@/components/Loading';
import {useNewPrint} from '/@/utils/boozsoft/print/print';
import {tableStyle} from '/@/store/modules/abc-print';
import {aoaToSheetXlsx} from '/@/components/Excel';
import {countByTname, save as saveCodeTemplate,} from '/@/api/record/system/code-import-template';
import {saveLog} from "/@/api/record/system/group-sys-login-log";
import {findByStockAccountAccId} from "/@/api/record/system/stock-account";
// import DynamicColumn from "/@/views/boozsoft/stock/stock_sales_add/component/DynamicColumn.vue";
const DynamicColumn=null

const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const ATabPane = ATabs.TabPane;
const ARadioButton = ARadio.Button;
const ARadioGroup = ARadio.Group;
const { closeCurrent } = useTabs();

const recordName = 'UsedForeignCurrency';
const { ncLogger } = useNcLogger({ recordName });
const route = useRoute();
const selectDataBaseValue = ref('');
const modal1Visible = ref(false);
const spinflag = ref(false);
// 文件导入
const fileList = ref([]);
// table加载
const loading = ref(true);
// 模板属于系统还是自定义
const sysflg = ref(route.query.tType);
// 会计准则下拉框回显
const local_reloadData = ref(route.query.data);
const accStandardList = ref([]);
const accStyleUnique = ref('');
const activeKey = ref('全部');
const standardSelected = ref(0);
const standardSelected2 = ref(0);
const standardName = ref('');
// 科目模板下拉框
const templateList = ref([]);
const templateSelected = ref(route.query.templateId + '_' + route.query.jici);
const templateId = ref(route.query.templateId);
const templateName = ref('');
const jici = ref(route.query.jici);
// 科目类型
const styleName = ref('');
const styleNamelist = ref([]);
const accNameAll = ref('');
// 账套是否预算会计
const ibudgetAccounting = ref('');
// 数据库模式名称
const orgUnique = ref('');
const accId = ref('');
const database = ref(getCurrentAccountName(true));
const accountList = computed(() => useCompanyOperateStoreWidthOut().getCurrentAccountInfo);
const databaseName = ref('');
const databaselist = ref([]);
// 是否非企业机构标识 1是
const icorp = ref('');
// 账套详细信息
const databaseinfo = ref('');
// 所有账套集合
const databaseAll = ref([]);

// 年度选中
const iyearselected = ref(useCompanyOperateStoreWidthOut().getLoginDate.split('-')[0]);
const upYear = ref('');
const userinfo = ref({
  databaseNum: '',
  username: useUserStoreWidthOut().getUserInfo.username,
  realName: useUserStoreWidthOut().getUserInfo.realName,
  name: useUserStoreWidthOut().getUserInfo.name,
  phone: useUserStoreWidthOut().getUserInfo.phone,
});

//   显示
const ibookflg = ref(false);
const edittext = ref('编辑');
// 单元格可编状态
const roweditflg = ref(false);

const tableData = ref([]);
const tableDataAll = ref('');
// 任务管理信息
const taskinfo = ref('');

// 本年科目
const thisyearCode = ref([]);
// 上年科目
const upyearCode = ref([]);
const inputSearchtext = ref('');
// 页面变量
const pageParameter = reactive({
  cclass: '全部',
  databasenum: database.value,
  showRulesSize: 'MIN',
  queryMark: 'J',
  searchConditon: {
    requirement: 'ccode',
    value: '',
  },
  uniqueAccStandard: '',
  templateId: '',
  activeKey: '',
  reloadMark: false,
  companyCode: '',
  companyName: databaseName.value,
  ifUnit: false,
  total: 0,
  iyear: '',
});

const tableRef:any = ref(null);
const windowWidth = document.documentElement.clientWidth - 170;
const windowHeight = (window.innerHeight - (325))
const totalColumnWidth = ref(0);
const lanMuData = ref({
  accId: database,
  menuName: '会计科目',
  type: '列表',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username,
  changeNumber:0
})
const dynamicColumnData = ref({value:[]})
let dynamicColumnDataCopy = [];
const searchConditonList = ref([]);
const accIdPeriodIyearList = ref([]);
const contrastModel = ref(false);
const visible = ref(false);
const loadMark = ref(true);
const dynamicColumns = initDynamics().DEFAULT;
const editableData = reactive({});
const { createConfirm, createWarningModal, createMessage } = useMessage();
// 是否独立账套
const independent = ref(false);
const nodelmsg = ref('');
// 当前年度
const thisiyear = ref('');
// 对比年度
const contrastiyear = ref('');
const tableTotal = ref(0);

// 全局常量
const glStore = kemuGlStore();
const CrudApi = {
  list: companyfindAll,
  columns: JSON.parse(
    JSON.stringify(glStore.getColumns(pageParameter.queryMark)),
  ),
};

// 组件实例区
const [
  registerTable,
  {
    reload,
    setColumns,
    getColumns,
    getSelectRows,
    setTableData,
    getDataSource,
    getPaginationRef,
    clearSelectedRowKeys,
    updateTableData,
  },
] =useTable({
  api: useRouteApi(CrudApi.list, { schemaName: database }),
  columns: CrudApi.columns,
  bordered: true,
  loading: loadMark.value,
  showIndexColumn: true, //显示序号列
  indexColumnProps:{ fixed:true },
  pagination: {
    pageSize: 200,
    simple:true
  },
  searchInfo: pageParameter,
});

const [registerEditPage, { openModal: openEditPage }] = useModal();
const [registerSavePage, { openModal: openSavePage }] = useModal();
const [registerExcelPage, { openModal: openExcelPage }] = useModal();
const [registerCopyPage, { openModal: openCopyPage }] = useModal();
const [registerContrastPage, { openModal: openContrastPage }] = useModal();
const [registerLookPage, { openModal: openLookPage }] = useModal();
const [registerPrintPage, { openModal: openPrintPage }] = useModal();
const [registerCodePopPage, { openModal: openCodePopPage }] = useModal();
const [registerModalBend0Page, { openModal: openModalBend0Page }] = useModal();
const [registerCodeAllPopPage, { openModal: openCodeAllPopPage }] = useModal();
const [registerKemuDelPage, { openModal: openKemuDelPage }] = useModal();
const [registerOrgKemuPage, { openModal: openOrgKemuPage }] = useModal();
const [registerSimpleSavePage, { openModal: openSimpleSavePage }] = useModal();

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
// 简单增加弹窗
function simpleAddModalShow() {
  openSimpleSavePage(true, {
    database:database.value,
    iyear:iyearselected.value,
  })
}

// 简单增加回调
function throwSimpleSaveData(data) {
  console.log(data)
}
// 引入科目回调
async function bringOrgKemuData(data) {
  await useRouteApi(company_saveAll, { schemaName: database })(data)
    .then(async (a)=>{
      a.forEach(async (t)=>{
        let log='操作内容【引入会计科目】,账套代码【'+pageParameter.companyCode+'】,账套名称【'+pageParameter.companyName+'】,科目编码【'+t.ccode+'】,科目名称【'+t.ccodeName+'】'
        let logtime=new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ")
        /************** 记录操作日志 ****************/
        let map={
          loginTime:logtime,
          userId: useUserStoreWidthOut().getUserInfo.username,
          userName: useUserStoreWidthOut().getUserInfo.realName,
          optModule:'master_data',
          optFunction:'会计科目',
          optRange:'1',
          uniqueCode:accId.value,
          billDate:iyearselected.value,
          optAction:'引入',
          optContent:log,
        }
        await saveLog(map)
        /************** 记录操作日志 END ****************/
      })
    })
  pageReload()
}

// 集团账套引入组织会计科目
function openOrgKemu() {
  openOrgKemuPage(true, {
    orgUnique:orgUnique.value,
    iyear:iyearselected.value,
    database:database.value,
    ccodeFirst:jici.value.split('-')[0]
  })
}

function openCodeDel() {
  openKemuDelPage(true, {})
}

// 行-双击触发事件
function rowDbClick(record, index, event) {
  openEdit2(record);
}

const showPaginationText=ref(false)
function fetchSuccess(data) {
  tableTotal.value=data.total
  if(data.items.length<50){
    for (let i =  data.items.length; i < 50; i++) {
      data.items.push({flag: '11'})
    }
  }
  setTableData(data.items)
}
const pageSearch = async () => {
  pageParameter.cclass='全部'
  activeKey.value='全部'
  // 搜索前校验格式
  if ('' == pageParameter.searchConditon.requirement.trim()) {
    createConfirmPop('请选择检索条件');
    return false;
  }
  // 校验完成后搜索
  visible.value = false;
  reloadColumns();
  pageReload();
};

function addspace(igrade, str) {
  var space = '';
  for (let i = 1; i < igrade; i++) {
    space += '\xa0\xa0\xa0\xa0';
  }
  return space + str;
}

async function inputSearch() {
  if (inputSearchtext.value === '') {
    tableData.value = tableDataAll.value;
  } else {
    tableData.value = tableDataAll.value.filter(
      (o) =>
        o.ccode.indexOf(inputSearchtext.value) != -1 ||
        o.ccodeName.indexOf(inputSearchtext.value) != -1,
    );
  }
}
/****************************** 任务三剑客 ************************************/
// 离开当前。删除任务
// onBeforeRouteLeave(deltask)

async function findByTaskModuleAndUserName() {
  const task1 = await useRouteApi(findByFunctionModule, { schemaName: database })({
    iyear: iyearselected.value,
  });
  let taskdata = task1.filter((t) => t.functionModule === '会计科目'&&t.caozuoUnique==useUserStoreWidthOut().getUserInfo.username);
  if(taskdata.length==0){
    createConfirmPop('锁定失效，请关闭当前任务，重新打开功能，锁定后再进行编辑！');
    return false
  }else{
    taskinfo.value=taskdata[0]
  }
  return true;
}
// 任务表
async function edittask() {
  // 首次
  if (taskinfo.value=='') {
    let task = await useRouteApi(saveTaskInfo, { schemaName: database })({
      id: null,
      iyear: iyearselected.value,
      functionModule: '会计科目',
      username: useUserStoreWidthOut().getUserInfo.id,
      databasenum: database.value,
      method: '编辑',
    });
    taskinfo.value = task;
  } else {
    let task = await useRouteApi(editTaskTime, { schemaName: database })({
      id: taskinfo.value.id,
    });
    taskinfo.value = task;
  }
  return true;
}
// 清除任务
async function deltask() {
  const task1 = await useRouteApi(findByFunctionModule, { schemaName: database })({
    iyear: iyearselected.value,
  });
  let taskdata = task1.filter((t) => t.functionModule === '会计科目'&&t.caozuoUnique==useUserStoreWidthOut().getUserInfo.username);
  if(taskdata.length>0){
    await useRouteApi(delFunctionModule, { schemaName: database })({
      id: taskdata[0].id,
      databasenum:'',
    });
    taskinfo.value = '';
  }
}
/****************************END**************************************/
// 科目对照
async function codeContrastView() {
  accIdPeriodIyearList.value = await findByAccIdIyearGroupBy(accId.value);
  thisiyear.value = iyearselected.value;
  contrastiyear.value = iyearselected.value;
  if(parseInt(thisiyear.value)==parseInt(contrastiyear.value)){
    return createWarningModal({ content: '没有下年期间,不能对照！' });
  }
  contrastModel.value = true;
}

async function contrastModelOk() {
  if(parseInt(thisiyear.value)==parseInt(contrastiyear.value)){
    return createWarningModal({ content: '同一年度,不能对照！' });
  }
  let thisiyearDataBase = '';
  let contrastiyearDataBase = '';
  // 重新查询账套编码信息
  await findDataBase(accId.value, thisiyear.value).then((item) => {
    thisiyearDataBase = item.accountMode;
  });
  // 重新查询账套编码信息
  await findDataBase(accId.value, contrastiyear.value).then((item) => {
    contrastiyearDataBase = item.accountMode;
  });
  thisyearCode.value = await useRouteApi(company_findAllByIyear, {
    schemaName: thisiyearDataBase,
  })({ iyear: iyearselected.value, databasenum: '' });
  upyearCode.value = await useRouteApi(company_findAllByIyear, {
    schemaName: contrastiyearDataBase,
  })({ iyear: iyearselected.value, databasenum: '' });
  // 对照页面弹框
  openContrastPage(true, {
    thisiyear: thisiyear.value,
    upiyear: contrastiyear.value,
    thisyearCode: thisyearCode.value,
    upyearCode: upyearCode.value,
    accNameAll: accNameAll.value,
  });
}

async function saveCopyData() {
  message.success('复制成功');
  edittask();
  findAllData();
}

async function copyCodeView() {
  openCopyPage(true, {
    accId:accId.value,
    data: iyearselected.value,
  });
}

async function editflg(data) {
  var aa: any = true;
  if (data === '编辑') {
    const task1 = await useRouteApi(findByFunctionModule, { schemaName: database })({
      iyear: iyearselected.value,
    });
    let taskdata = task1.length > 0 ? task1.filter((t) => t.functionModule === '会计科目') : '';
    if (taskdata.length > 0 && taskdata[0].caozuoUnique != null) {
      // 获取上一条记录
      taskinfo.value = taskdata[0];
      // 判断是不是自己；动态获取
      if (taskdata[0].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
        aa = false;
        createConfirmPop(
          '提示：任务冲突！操作员【' +
          taskdata[0].caozuoUnique +
          '】正在进行当前单位的会计科目编辑操作，本功能不支持并行操作，请销后再试，或联系财务主管清理该操作员的会计科目编辑任务',
        );
        return false;
      }
    }

    // 有无录入期初余额任务
    let taskQcData = task1.length > 0 ? task1.filter((t) => t.functionModule === '期初余额') : '';
    if (taskQcData.length > 0 && taskQcData[0].caozuoUnique != null) {
      // 判断是不是自己；动态获取
      if (taskQcData[0].caozuoUnique !== useUserStoreWidthOut().getUserInfo.username) {
        aa = false;
        createConfirmPop(
          '提示：任务冲突！操作员【' +
          taskQcData[0].caozuoUnique +
          '】正在进行当前单位的科目期初余额编辑操作，会计科目不能进行编辑操作，请销后再试，或联系财务主管清理该操作员的期初余额编辑任务',
        );
        return false;
      }
    }

    if (aa) {
      const modal = Modal.success({
        content: `独占任务已启用，操作完成后请点击返回菜单，及时退出独占锁定状态`,
      });
      setTimeout(() => {
        modal.destroy();
      }, 3000);
      edittask();
      // 显示按钮
      edittext.value = data === '编辑' ? '返回' : '编辑';
      roweditflg.value = true;
    }
  } else {
    edittext.value = data === '返回' ? '编辑' : '返回';
    deltask();
    roweditflg.value = false;
  }
}

const openLook = (data) => {
  const val = {
    standardUnique: standardSelected.value,
    templateID: templateSelected.value,
    jici: jici.value,
    iyear: iyearselected.value,
    styleName: styleNamelist.value,
    data: data,
    independent: independent.value,
    database: database.value,
    accId: accId.value,
  };
  openLookPage(true, {
    data: val,
  });
};

// 上传回调
async function saveExcelData() {
  findAllData();
}

const openImportExcel = async () => {
  var aa: any = true;
  const task1 = await useRouteApi(findByFunctionModule, { schemaName: database })(
    iyearselected.value,
  );
  if (task1.length === 0) {
    aa = true;
  } else {
    for (let i = 0; i < task1.length; i++) {
      if (task1[i].functionModule === '科目编辑' && task1[i].caozuoUnique != null) {
        aa = false;
        createConfirmPop(
          '任务冲突！操作员【' +
          task1[i].caozuoUnique +
          '】正在进行会计科目编辑操作，本功能不能多人同时操作，请销后再试，或联系财务主管清理该科目编辑任务',
        );
        return false;
      }
      if (task1[i].functionModule === '期初余额' && task1[i].caozuoUnique != null) {
        aa = false;
        createConfirmPop(
          '任务冲突！操作员【' +
          task1[i].caozuoUnique +
          '】正在进行期初余额编辑操作，本功能不能多人同时操作，请销后再试，或联系财务主管清理该科目编辑任务',
        );
        return false;
      }
      if (task1[i].functionModule === '新增凭证' && task1[i].caozuoUnique != null) {
        aa = false;
        createConfirmPop(
          '任务冲突！操作员【' +
          task1[i].caozuoUnique +
          '】正在进行新增凭证操作，不能进行会计科目编辑，请销后再试，或联系财务主管清理该新增凭证任务',
        );
        return false;
      }
      if (task1[i].functionModule === '凭证修改' && task1[i].caozuoUnique != null) {
        aa = false;
        createConfirmPop(
          '任务冲突！操作员【' +
          task1[i].caozuoUnique +
          '】正在进行凭证修改的操作，不能进行会计科目编辑，请销后再试，或联系财务主管清理该凭证修改任务',
        );
        return false;
      }
      if (task1[i].functionModule === '转账生成' && task1[i].caozuoUnique != null) {
        aa = false;
        createConfirmPop(
          '任务冲突！操作员【' +
          task1[i].caozuoUnique +
          '】正在进行转账生成操作，不能进行凭证导入，请销后再试，或联系财务主管清理转账生成任务',
        );
        return false;
      }
    }
  }
  if (aa) {
    let code_template = await countByTname('系统标准模板');
    if (code_template === 0) {
      openCompFullLoading();
      let savetemplate = {
        tname: '系统标准模板',
        remark: '财税达NC',
        ttype: 0,
        flag: 1,
        tjson:
          '[{"titel":"类型","value":"类型"},{"titel":"科目编码","value":"科目编码"},{"titel":"科目名称","value":"科目名称"},' +
          '{"titel":"方向","value":"方向"},{"titel":"现金账","value":"现金账"},{"titel":"银行账","value":"银行账"},{"titel":"日记账","value":"日记账"},' +
          '{"titel":"外币名称","value":"外币名称"},{"titel":"计量单位","value":"计量单位"},{"titel":"辅助账类型","value":"辅助账类型"},' +
          '{"titel":"受控系统","value":"受控系统"},{"titel":"封存","value":"封存"}]',
      };
      await saveCodeTemplate(savetemplate).then((item) => {
        compState.loading = false;
        openExcelPage(true, {
          data: {
            uniqueAccStandard: standardSelected.value,
            templateId: templateSelected.value,
            iyear: iyearselected.value,
            jici: jici.value,
            databasenum: database.value,
            databaseName: databaseName.value,
            pageParameter:pageParameter
          },
        });
      });
    } else {
      openExcelPage(true, {
        data: {
          uniqueAccStandard: standardSelected.value,
          templateId: templateSelected.value,
          iyear: iyearselected.value,
          jici: jici.value,
          databasenum: database.value,
          databaseName: databaseName.value,
          pageParameter:pageParameter
        },
      });
    }
  }
};

// 判断 凭证、期初、现金流量是否使用
async function verifyData(data) {
  // 判断 凭证、期初、现金流量是否使用
  const delFirstMsg = await useRouteApi(company_delFindByCodekemu, { schemaName: database })({
    ccode: data.ccode,
    iyear: iyearselected.value,
    databasenum: database.value,
  });

  if (delFirstMsg.code === '200') {
    return true;
  } else {
    if (delFirstMsg.msg === 'accvoucher' || delFirstMsg.msg === 'qc') {
      return false;
    } else {
      return true;
    }
  }
}

const openAddPage = async (flag) => {

  // 是0增加按钮 还是 1增加下级按钮
  if (flag === '1' && getSelectRows().length !== 1) {
    createConfirmPop('只能选择一个上级科目！');
    return false;
  }
  const val = {
    standardUnique: standardSelected.value,
    templateID: templateSelected.value,
    jici: jici.value,
    styleName: styleNamelist.value,
    iyear: iyearselected.value,
    independent: independent.value,
    database: database.value,
    accId: accId.value,
  };

  // 选中一个，增加页面自动显示上级
  let checkBoxCcodeInfo: any = '';
  if (getSelectRows().length === 1) {
    checkBoxCcodeInfo = getSelectRows();
  }

  // 是0增加按钮 还是 1增加下级按钮
  if (flag === '0') {
    openSavePage(true, {
      data: val,
      ccodeInfo: '',
      all: 'yes',
      ibudgetAccounting: ibudgetAccounting.value,
      superiorCcode:'0'
    });
  }
  else {
    if(!independent.value&&checkBoxCcodeInfo[0].lowerControl=='0'){
      return createConfirmPop('新增下级科目控制已开启,不能增加下级科目！')
    }
    // 末级科目
    if (getSelectRows().length === 1 && checkBoxCcodeInfo[0].bend === '1') {
      if (await verifyData(checkBoxCcodeInfo[0])) {
        // 判断 凭证、期初、现金流量是否使用
        openSavePage(true, {
          data: val,
          ccodeInfo: checkBoxCcodeInfo,
          all: 'yes',
          ibudgetAccounting: ibudgetAccounting.value,
          superiorCcode:'0'
        });
      } else {
        openSavePage(true, {
          data: val,
          ccodeInfo: checkBoxCcodeInfo,
          all: 'no', // 只能修改名称、编码；其他属性与上级科目一致
          ibudgetAccounting: ibudgetAccounting.value,
          superiorCcode:'0'
        });
      }
    }
    else {
      openSavePage(true, {
        data: val,
        ccodeInfo: checkBoxCcodeInfo,
        all: 'yes',
        ibudgetAccounting: ibudgetAccounting.value,
        superiorCcode:'0'
      });
    }
  }
};

const openEdit2 = async (data) => {
  const val = {
    standardUnique: standardSelected.value,
    templateID: templateSelected.value,
    jici: jici.value,
    iyear: iyearselected.value,
    styleName: styleNamelist.value,
    data: data,
    independent: independent.value,
    database: database.value,
    accId: accId.value,
  };
  if (edittext.value === '返回') {
    console.log('修改');
    if (await verifyData(data)) {
      openEditPage(true, {
        data: val,
        all: 'yes',
        ibudgetAccounting: ibudgetAccounting.value,
        isEdit: false,
        pageParameter:pageParameter
      });
    } else {
      createConfirm({
        iconType: 'warning',
        title: '警告',
        content: '此科目已做凭证或期初，原数据将会覆盖。确定继续操作吗？',
        onOk: async () => {
          openEditPage(true, {
            data: val,
            all: 'no', // 只能修改名称、编码；其他属性与上级科目一致
            ibudgetAccounting: ibudgetAccounting.value,
            isEdit: false,
            pageParameter:pageParameter
          });
        },
      });
    }
  } else {
    console.log('查看');
    openLookPage(true, {
      data: val,
      all: 'no', // 只能修改名称、编码；其他属性与上级科目一致
      ibudgetAccounting: ibudgetAccounting.value,
      isEdit: false,
    });
  }
};

const openRowEdit = async () => {
  if (getSelectRows().length == 0) {
    createConfirmPop('只能选择一条进行编辑！');
    return false;
  }

  const val = {
    standardUnique: standardSelected.value,
    templateID: templateSelected.value,
    jici: jici.value,
    iyear: iyearselected.value,
    styleName: styleNamelist.value,
    data: getSelectRows()[0],
    independent: independent.value,
    database: database.value,
    accId: accId.value,
  };
  if (await verifyData(getSelectRows()[0])) {
    openEditPage(true, {
      data: val,
      all: 'yes',
      ibudgetAccounting: ibudgetAccounting.value,
      isEdit: true,
      pageParameter:pageParameter
    });
  } else {
    createConfirm({
      iconType: 'warning',
      title: '警告',
      content: '此科目已做凭证或期初，原数据将会覆盖。确定继续操作吗？',
      onOk: async () => {
        openEditPage(true, {
          data: val,
          all: 'no', // 只能修改名称、编码；其他属性与上级科目一致
          ibudgetAccounting: ibudgetAccounting.value,
          isEdit: true,
          pageParameter:pageParameter
        });
      },
    });
  }
};
function  updateRow(index,data){
  Object.keys(data).forEach(it=>{
    updateTableData(index,it,data[it]);
  })
}
const tabsCheck = async (data: any) => {
  loading.value = true;
  activeKey.value = data;
  pageParameter.cclass = data;
  pageReload();
  loading.value = false;
};
const modalShow = () => {
  openCodePopPage(true, {
    database: database.value,
    accId: accId.value,
    iyear: iyearselected.value,
  });
}
const modalBend0Show = () => {
  openModalBend0Page(true, {
    database: database.value,
    accId: accId.value,
    iyear: iyearselected.value,
  });
}

const modalAllShow = () => {
  openCodeAllPopPage(true, {
    database: database.value,
    accId: accId.value,
    iyear: iyearselected.value,
  });
}

const modalData = (data) => {
  console.log(data)
}
const modalBend0Data = (data) => {
  console.log(data)
}
const modalAllData = (data) => {
  console.log(data)
}
const saveData = async (data: any) => {
  if(await findByTaskModuleAndUserName() && await edittask()){
    createConfirmPop('添加下级会计科目成功！')
    await useRouteApi(company_saveCodekemu, { schemaName: database })({params: data,iyear: iyearselected.value})
      .then(async (a)=>{
        let log='操作内容【新增会计科目】,账套代码【'+pageParameter.companyCode+'】,账套名称【'+pageParameter.companyName+'】,科目编码【'+data.ccode+'】,科目名称【'+data.ccodeName+'】'
        let logtime=new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ")
        /************** 记录操作日志 ****************/
        let map={
          loginTime:logtime,
          userId: useUserStoreWidthOut().getUserInfo.username,
          userName: useUserStoreWidthOut().getUserInfo.realName,
          optModule:'master_data',
          optFunction:'会计科目',
          optRange:'1',
          uniqueCode:accId.value,
          billDate:iyearselected.value,
          optAction:'新增',
          optContent:log,
        }
        await saveLog(map)
        /************** 记录操作日志 END ****************/
      })
    if (data.closeflag === 'add') {
      const val = {
        standardUnique: standardSelected.value,
        templateID: templateSelected.value,
        jici: jici.value,
        styleName: styleNamelist.value,
        iyear: iyearselected.value,
        independent: independent.value,
        database: database.value,
        accId: accId.value,
      };
      openSavePage(true, {
        data: val,
        ccodeInfo: '',
        all: 'yes',
        ibudgetAccounting: ibudgetAccounting.value,
        superiorCcode: data.superiorCcode,
      });
    }
    findAllData()
  }
};

const editData = async (data: any) => {
  if(await findByTaskModuleAndUserName() && await edittask()){
    await useRouteApi(company_saveCodekemu, { schemaName: database })({
      params: data,
      iyear: iyearselected.value
    })
    // 刷新当前行
    data.fuzhu=data.fuzhuhesuanName
    updateRow(data.index,data)
    clearSelectedRowKeys()
  }
};

// sort到序
function quickSort(arr) {
  if (arr.length <= 1) return arr;
  var Temp = Math.floor(arr.length / 2);
  var left: any = [],
    right: any = [];
  var num = arr.splice(Temp, 1)[0];
  for (var i = 0; i < arr.length; i++) {
    if (arr[i] >= num) {
      left.push(arr[i]);
    } else {
      right.push(arr[i]);
    }
  }
  return quickSort(left).concat(num, quickSort(right));
}

const delCode = async () => {
  if (getSelectRows().length == 0) {
    message.error('至少选择一条！');
    return false;
  }

  // 是否直接清空表
  let delAll = false;
  // 查询凭证
  let accvoucherCode = await useRouteApi(company_findByPzCcodeStr, {
    schemaName: database.value,
  })({ iyear: iyearselected.value });
  // 期初
  let qcCode = await useRouteApi(company_findByQcCcodeStr, { schemaName: database.value })({
    iyear: iyearselected.value,
  });
  // 以上5项都为空，直接清空表
  if (accvoucherCode.length === 0 && qcCode.length === 0) {
    delAll = true;
  }

  let codenum: any = [];
  let mojicodenum: any = []; // 末级科目不能删除
  createConfirm({
    iconType: 'error',
    title: '警告',
    content: '删除后数据不能恢复，你确认要删除吗？',
    onOk: async () => {
      for (let i = 0; i < getSelectRows().length; i++) {
        codenum.push(getSelectRows()[i].ccode);
        if(getSelectRows()[i].bend==='0'){
          mojicodenum.push(getSelectRows()[i].ccode)
        }
      }
      if (!delAll) {
        let nodel: any = [];
        let yesdel: any = [];
        let nodel2: any = [];
        let pz: any = getArrEqual(accvoucherCode, codenum);
        let qc: any = getArrEqual(qcCode, codenum);
        if (pz.length > 0) {
          return createConfirmPop(setString(Array.from(new Set(pz)).toString(), 60) + '会计科目已经在凭证中使用，不能删除！')
        }
        if (qc.length > 0) {
          return createConfirmPop(setString(Array.from(new Set(qc)).toString(), 60) + '会计科目已经期初余额中使用，不能删除！')
        }
        nodel2 = Array.from(new Set(nodel.concat(pz).concat(qc)));
        // 去除没有做账的科目
        yesdel = getArrNoEqual(codenum, nodel2);
        if (yesdel.length > 0) {
          // 去除非末级科目
          yesdel = getArrNoEqual(mojicodenum, yesdel);
          let temp = quickSort(yesdel);
          await useRouteApi(company_delCodekemu, { schemaName: database.value })({
            id: temp.join(','),
            iyear: iyearselected.value,
            userName: useUserStoreWidthOut().getUserInfo.realName,
            independent: independent.value,
          })
            .then(async (info: any) => {
              // 埋点-操作日志
              for (let i = 0; i < temp.length; i++) {
                let logtime=new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ")
                /************** 记录操作日志 ****************/
                let map={
                  loginTime:logtime,
                  userId: useUserStoreWidthOut().getUserInfo.username,
                  userName: useUserStoreWidthOut().getUserInfo.realName,
                  optModule:'master_data',
                  optFunction:'会计科目',
                  optRange:'1',
                  uniqueCode:accId.value,
                  billDate:iyearselected.value,
                  optAction:'删除',
                  optContent:'操作内容【删除会计科目】,帐套代码【'+pageParameter.companyCode+'】,帐套名称【'+pageParameter.companyName+'】,年度【'+iyearselected.value+'】,科目编码【'+temp[i]+'】',
                }
                await saveLog(map)
                /************** 记录操作日志 END ****************/
              }
            });

          createConfirmPop('会计科目删除成功！')
          findAllData();
          console.log('有发生或期初,挨个清空');
        }
      }
      else{
        let map={
          iyear:iyearselected.value,
          list:JSON.stringify(getSelectRows()),
          independent: independent.value,
          userName: useUserStoreWidthOut().getUserInfo.realName,
        }

        await useRouteApi(company_delAll, { schemaName: database.value })(map).then(async (data)=>{
          if(data.length>0){
            createConfirm({
              iconType: 'warning',
              title: '警告',
              content: data+'不是末级科目,不能删除!!',
              onOk: async () => {
                findAllData();
              },
            });
            // 埋点-操作日志
            let arr=getSelectRows().map(a=>a.ccode).filter(a=>data.indexOf(a)==-1)
            if(arr.length>0){
              // 埋点-操作日志
              for (let i = 0; i < arr.length; i++) {
                let logtime=new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ")
                /************** 记录操作日志 ****************/
                let map={
                  loginTime:logtime,
                  userId: useUserStoreWidthOut().getUserInfo.username,
                  userName: useUserStoreWidthOut().getUserInfo.realName,
                  optModule:'master_data',
                  optFunction:'会计科目',
                  optRange:'1',
                  uniqueCode:accId.value,
                  billDate: iyearselected.value,
                  optAction:'删除',
                  optContent:'操作内容【删除会计科目】,帐套代码【'+pageParameter.companyCode+'】,帐套名称【'+pageParameter.companyName+'】,年度【'+iyearselected.value+'】,科目编码【'+arr[i]+'】',
                }
                await saveLog(map)
                /************** 记录操作日志 END ****************/
                console.log(map,'aaa')
              }
            }
          }
          else{
            // 埋点-操作日志
            let arr=[...new Set(getSelectRows().map(a=>a.ccode))]
            if(arr.length>0){
              // 埋点-操作日志
              for (let i = 0; i < arr.length; i++) {
                let logtime=new Date( +new Date() + 8 * 3600 * 1000 ).toJSON().substr(0,19).replace("T"," ")
                /************** 记录操作日志 ****************/
                let map={
                  loginTime:logtime,
                  userId: useUserStoreWidthOut().getUserInfo.username,
                  userName: useUserStoreWidthOut().getUserInfo.realName,
                  optModule:'master_data',
                  optFunction:'会计科目',
                  optRange:'1',
                  uniqueCode:accId.value,
                  billDate:iyearselected.value,
                  optAction:'删除',
                  optContent:'操作内容【删除会计科目】,帐套代码【'+pageParameter.companyCode+'】,帐套名称【'+pageParameter.companyName+'】,年度【'+iyearselected.value+'】,科目编码【'+arr[i]+'】',
                }
                await saveLog(map)
                /************** 记录操作日志 END ****************/
                console.log(map,'bbb')
              }
            }
            createConfirmPop('会计科目删除成功！')
            findAllData();
          }
        })
        console.log('没有发生或期初,直接清空');
      }
    },
  });
};

// 两个数组相同的
function getArrEqual(arr1, arr2) {
  let newArr = [];
  for (let i = 0; i < arr2.length; i++) {
    for (let j = 0; j < arr1.length; j++) {
      if (arr1[j] === arr2[i]) {
        newArr.push(arr1[j]);
      }
    }
  }
  return newArr;
}
// 两个数组不相同的
function getArrNoEqual(arr1, arr2) {
  return arr1.concat(arr2).filter(function (v, i, arr) {
    return arr.indexOf(v) === arr.lastIndexOf(v);
  });
}

const findAllData = async () => {
  showPaginationText.value=false
  clearSelectedRowKeys()
  openCompFullLoading();
  // 查询是否独立账套  是否独立账套（1）反之集团账套（0）
  let datainfo = await findByAccId(accId.value);
  datainfo=hasBlank(datainfo)?await findByStockAccountAccId(accId.value):datainfo
  independent.value = datainfo.independent > 0 ? true : false;
  styleNamelist.value = [];
  setTableData([]);
  // 获取对应的账套科目 所属的 会计准则、科目模板
  let codelistall = await useRouteApi(groupStandardAndTemplate, { schemaName: database })({
    databaseNum: database.value,
    iyear: iyearselected.value,
  });
  if (codelistall.length > 0) {
    templateSelected.value = codelistall[0].templateId;
    standardSelected.value = codelistall[0].uniqueAccStandard;
  } else {
    console.log('账套下没有科目');
    // 根据账套信息表重新获取 会计准则、科目模板
    let tplateInfo = await findTemplateByAccId(accId.value);
    tplateInfo=hasBlank(tplateInfo)?await findByStockAccountAccId(accId.value):tplateInfo
    templateSelected.value = tplateInfo.id;
    standardSelected.value = tplateInfo.uniqueAccStandard;
  }
  // 会计准则
  const accstandard = await findByStandardUnique(standardSelected.value);
  if(accstandard==''){
    compState.loading = false
    return createWarningModal({ content: '当前账套没有会计准则，无法查询！' });
  }
  let accStyleUnique = await findByAccStyle(accstandard.accStyleUnique);
  if(accStyleUnique==''){
    compState.loading = false;
    return createWarningModal({ content: '当前账套没有科目类型，无法查询！' });
  }
  if (styleNamelist.value.length === 0) {
    styleNamelist.value.push({
      cclass: '全部',
      flagYusuan: '',
    });
    for (let i = 0; i < accStyleUnique.length; i++) {
      styleNamelist.value.push({
        cclass: accStyleUnique[i].cclass,
        flagYusuan: accStyleUnique[i].flagYusuan,
        order: accStyleUnique[i].order,
      });
    }
  }
  pageParameter.uniqueAccStandard = standardSelected.value;
  pageParameter.templateId = templateSelected.value;
  pageParameter.activeKey = activeKey.value;
  pageParameter.iyear=iyearselected.value
  activeKey.value = pageParameter.activeKey;
  pageReload();
  compState.loading = false;
  showPaginationText.value=true
};

const upYearCheck = () => {
  upYear.value = iyearselected.value;
};

const visible3=ref(false)
const dynamicAdReload = async (obj) => {
  deltask();
  if (obj !== undefined) {
    pageParameter.companyCode = obj.coCode;
    pageParameter.companyName = obj.baseName;
    databaseName.value = obj.baseName;
    // 重新查询账套编码信息
    await findDataBase(obj.accId, obj.year).then((item) => {
      database.value = item.accountMode;
      accId.value = item.accountId;
      iyearselected.value = item.accountYear;
      jici.value = item.jici;
      accNameAll.value = item.accName;
      // 进页面执行
      getThisAdInfoData({ accId: item.accountId }).then((res) => {
        orgUnique.value=res.accGroup
        ibudgetAccounting.value = res.ibudgetAccounting !== '1' ? '0' : '1';
      });
    });
  }
  visible3.value = true
  setTimeout(()=>
      visible3.value = false
    ,100)
  lanMuData.value.changeNumber+=1
  findAllData();
  edittext.value = '编辑';
  roweditflg.value = false;
};
const reloadColumns = () => {
  let newA = JSON.parse(JSON.stringify(CrudApi.columns))
  newA = assemblyDynamicColumn(dynamicColumnData.value.value, newA)
  setColumns(newA)
  initTableWidth(newA)
  pageReload()
  //多表头 添加子节点筛选
  let seachList = [];
  newA.forEach((item) => {
    if (item.children) {
      item.children.forEach((item2) => {
        item2.title = item.title + item2.title;
        seachList.push(item2);
      });
    } else {
      seachList.push(item);
    }
  });
  searchConditonList.value = seachList;
}
function initTableWidth(thisCs) {
  let total = 60
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })
  /*  // 去除操作咧宽
    total -= 100*/
  if (total > windowWidth) {
    let f = 0
    if (visible.value) f = 260
    totalColumnWidth.value = Number(windowWidth) - f
    tableRef.value.$el.style.setProperty('width', (windowWidth + 40 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 40) + 'px')
  }
}

const pageReload = () => {
  reload({
    searchInfo: pageParameter,
  });
};
async function handleDownExcel() {
  const data = await useRouteApi(CrudApi.list, { schemaName: database })(pageParameter)
  const multiHeader:any = [];
  const keys:any = [];
  getColumns()
    .filter((a) => a.title !== '状态' && a.title !== '操作')
    .forEach((v) => {
      multiHeader.push(v.title);
      keys.push(v.dataIndex);
    });
  let tabledata: any = [];
  data.items.forEach((v) => {
    v.bprogerty = v.bprogerty === '1' ? '借' : '贷';
    v.bcashEquivalence = v.bcashEquivalence === '1' ? '是' : '';
    v.fuzhuControl = v.fuzhuControl === '1' ? '是' : '';
    v.lowerControl = v.lowerControl === '1' ? '是' : '';
    v.bbank = v.bbank === '1' ? '是' : '';
    v.bcash = v.bcash === '1' ? '是' : '';
    v.bnum = v.bnum === '1' ? '是' : '';
    v.currency = v.currency === '1' ? '是' : '';
    v.bdaybook = v.bdaybook === '1' ? '是' : '';

    let test: any = [];
    test[0] = v.cclass;
    test[1] = v.ccode;
    test[2] = v.ccodeName;
    test[3] = v.bprogerty;
    test[4] = v.bcash;
    test[5] = v.bbank;
    test[6] = v.bdaybook;
    test[7] = v.currencyType;
    test[8] = v.menterage;
    test[9] = v.fuzhu;
    test[10] = v.fuzhuControl;
    test[11] = v.flag!=='1'?'是':'';
    tabledata.push(test);
  });
  const columns2 = [
    '类型',
    '科目编码',
    '科目名称',
    '方向',
    '现金账',
    '银行账',
    '日记账',
    '外币名称',
    '计量单位',
    '辅助账类型',
    '受控系统',
    '封存'
  ];
  aoaToSheetXlsx({
    data: tabledata,
    header: columns2,
    filename: accNameAll.value + '-会计科目-' + iyearselected.value + '.xlsx',
  });
}

function createConfirmPop(text) {
  createWarningModal({
    iconType: 'warning',
    title: '警告',
    content: text,
    onOk: async () => {},
  });
}

// 直接修改科目状态
async function editflag() {
  let bend0=getSelectRows().filter(a=>a.bend=='0'&&a.flag=='1').map(a=>a.ccode)
  if(bend0.length>0){
    return createWarningModal({ content: bend0+'非末级科目不能停用！' });
  }
  // 启用改成停用
  let flag0=[...new Set(getSelectRows().filter(a=>a.flag=='1').map(a=>a.ccode))].join(',')
  let flag1=[...new Set(getSelectRows().filter(a=>a.flag=='0').map(a=>a.ccode))].join(',')
  console.log(flag0)
  console.log(flag1)
  let map={
    id: flag0, flag: '0',iyear:iyearselected.value
  }
  let map2={
    id: flag1, flag: '1',iyear:iyearselected.value
  }
  await useRouteApi(company_editflag, { schemaName: database })(map)
  await useRouteApi(company_editflag, { schemaName: database })(map2)
  pageReload()
  clearSelectedRowKeys()
}

function visibleOK() {
  visible.value = false;
  findAllData();
}
const loadPrint = (obj) => {
  const data = JSON.parse(JSON.stringify(getDataSource()));
  let printList: any = [];

  let printUser = '';
  if (obj.printUser) {
    printUser = '打印人：' + useUserStoreWidthOut().getUserInfo.username;
  }
  let printBz = '';
  if (obj.printBz) {
    printBz = '币种：人民币';
  }

  data.forEach((item, index) => {
    let item1 = {};
    item1[0] = item.igrade;
    item1[1] = item.ccode;
    item1[2] = item.ccodeName;
    item1[3] = item.bprogerty === '1' ? '借' : '贷';
    item1[4] = item.cclass;
    item1[5] = item.fuzhu;
    item1[6] = item.menterage;
    item1[7] = item.currencyType;
    printList.push(item1);
  });
  for (let i = 0; i < printList.length % 27; i++) {
    let item1 = {};
    item1[0] = '';
    item1[1] = '';
    item1[2] = '';
    item1[3] = '';
    item1[4] = '';
    item1[5] = '';
    item1[6] = '';
    item1[7] = '';
    printList.push(item1);
  }
  let num = Math.ceil(printList.length / 27);
  useNewPrint({ data: ['l', 'px', 'a4', true] }, (doc) => {
    loadMark.value = false;
    doc.autoTable({
      head: [
        ['', '', '', '会计科目', '', '', '', ''],
        ['核算单位：' + accNameAll.value, '', '', '年度：' + iyearselected.value, '', '', '', ''],
        ['级次', '科目编码', '科目名称', '方向', '类型', '辅助项', '计量单位', '外币币种'],
      ],
      body: printList,
      // startY: 60,
      styles: tableStyle(),
      margin: {
        left: 40,
        top: 20,
      },
      addPageContent: (data) => {
        //data.table.finalY 表格最大可容纳y轴坐标，超出时将根据设置决定是否另取一页.在页面需要分页时出现
        let tabHeigth = data.table.height,
          tabMarginTop = data.settings.margin.top,
          tabSize = data.table.finalY - tabMarginTop, //表格最大Y轴-表格顶部距离得到每页表格的最大值
          tabMarginLeft = data.settings.margin.left;
        if (data.table.finalY)
          if (data.pageNumber != Math.ceil(tabHeigth / tabSize))
            //是否分页 有分页时才有该属性finalY
            return; //如果需要每一页都显示页尾则注释该行代码
        //data.cursor.y ,data.cursor.x:表格最后一个单元坐标
        data.doc.setFontSize(9);
        // data.doc.setFont('fuhuiR', 'bold')
        // doc.autoTableText(
        //   '核算单位：' + accNameAll.value,
        //   tabMarginLeft,
        //   data.cursor.y + 3,
        //   0
        // );
        doc.autoTableText(printUser, data.cursor.x / 2 - 25, data.cursor.y + 3, 0);
        doc.autoTableText(
          '第' + data.doc.getCurrentPageInfo().pageNumber + '页/共' + num + '页',
          // '第'+data.doc.getCurrentPageInfo().pageNumber+'页',
          data.cursor.x - 50,
          data.cursor.y + 3,
          0,
        );
      },
      didParseCell(data) {
        data.cell.styles.cellPadding = { top: 3, left: 2, right: 2, bottom: 2 };
        data.cell.styles.fillColor = [255, 255, 255];
        data.cell.styles.fontSize = 9;
        data.cell.styles.lineColor = [150, 150, 150];
        // data.cell.styles.bold = false

        if (data.section == 'head' && data.row.index == 0) {
          data.cell.styles.fontSize = 20;
          data.cell.styles.fontStyle = 'bold';
          data.cell.styles.lineColor = [255, 255, 255];
          if (data.column.index == 3) {
            data.cell.colSpan = 2;
            data.cell.styles.halign = 'center';
          }
        }
        if (data.section == 'head' && data.row.index == 1) {
          data.cell.styles.fontSize = 10;
          data.cell.styles.fontStyle = 'bold';
          data.cell.styles.lineColor = [255, 255, 255];
          if (data.column.index == 0) {
            data.cell.colSpan = 3;
            data.cell.styles.halign = 'left';
          } else if (data.column.index == 3) {
            data.cell.colSpan = 2;
            data.cell.styles.halign = 'center';
          }
        }
        if (data.section == 'head' && data.row.index == 2) {
          data.cell.styles.fontSize = 10;
          data.cell.styles.cellPadding = { top: 4, left: 2, right: 2, bottom: 3 };
          data.cell.styles.fontStyle = 'bold';
          data.cell.styles.fillColor = [230, 230, 230];
          data.cell.styles.halign = 'center';
        }
        if (data.section == 'body') {
          if (data.row.index % 2 == 1) {
            data.cell.styles.fillColor = [240, 240, 240];
          }
          if (data.row.raw[0].indexOf('小计') > -1 || data.row.raw[0] == '合计') {
            data.cell.styles.fontStyle = 'bold';
          }
        }
      },
      columnStyles: {
        0: { maxHeight: 10, cellWidth: 30, halign: 'center' },
        1: { cellWidth: 100, halign: 'left' },
        2: { cellWidth: 150, halign: 'left' },
        3: { cellWidth: 30, halign: 'center' },
        4: { cellWidth: 50, halign: 'center' },
        5: { cellWidth: 100, halign: 'center' },
        6: { cellWidth: 50, halign: 'center' },
        7: { cellWidth: 50, halign: 'center' },
      },
    });
  });
};
//js切割字符串
function setString(str, len) {
  var strlen = 0;
  var s = '';
  for (var i = 0; i < str.length; i++) {
    if (str.charCodeAt(i) > 128) {
      strlen += 2;
    } else {
      strlen += 1.2;
    }
    s += str.charAt(i);
    if (strlen >= len) {
      return s + '...';
    }
  }
  return s;
}
const openPrint = () => {
  openPrintPage(true, {
    data: {
      dynamicTenantId: database.value,
      defaultAdName: useCompanyOperateStoreWidthOut().getSchemaName,
      year: '2021',
    },
  });
};
</script>
<style src="./global-menu-index.less" lang="less" scoped></style>
<style scoped>
:deep(.vben-basic-table) .ant-pagination {
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
}
.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
}
/*第二段背景色长度*/
.app-container:nth-of-type(2) {
  background-color: #f2f2f2;
  padding: 0px;
  margin: 5px 10px;
  height: 1800px;
  position: relative;
.pagination-text {
  position: absolute;
  bottom: 6px;
  right: 10%;
  font-size: 13px;
  color: black;
  z-index: 99999999;
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

.tableUStyle {
  color: #0096c7;
  cursor: pointer;
  text-decoration: none;
}
.tableUStyle:hover {
  color: red;
}
:deep(.ant-table-thead) th {
  text-align: center !important;
  font-weight: bold;
  background-color: #f2f2f2 !important;
  border-color: #e5e7eb !important;
}
</style>
