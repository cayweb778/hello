<template>
  <div>
    <div class="app-container">

      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">-->
        <div class="container-head-title" style="float: left;">
          <b class="noneSpan" style="font-size: 60px;"><TransactionOutlined /></b>
        </div>

        <div class="container-head-title" style="padding-left: 40%;text-align: center;">
          <b class="noneSpan">银行对账期初</b>
          <div style="font-size: 14px;text-align: center;margin-top: 20px;">
            <label>&emsp;&emsp;对账启用期间：</label>
            <a-select v-model:value="formItems.qiyongDate" :disabled="isCheck" @change="countKemuYue()"  style="width: 100px;">
              <a-select-option v-for="item in dateList" :key="item.iperiodNum" :value="item.iperiodNum">
                {{ item.iperiodNum }}
              </a-select-option>
            </a-select>
          </div>
        </div>

        <!--      <div style="display: inline;">
                <a-select v-model:value="formItems.iyear" class="head-index-select" placeholder="年度">
                  <a-select-option v-for="item in yearList" :key="item.accountYear" :value="item.accountYear">
                    {{ item.accountYear }}
                  </a-select-option>
                </a-select>
                <label style="font-size: 18px;margin-left: 30px;">对账期启用日期：</label>
                <a-date-picker v-model:value="formItems.qiyongDate" placeholder="对账期启用日期" style="width: 200px;" format="YYYY-MM-DD" />
              </div>-->
        <div class="ant-btn-group" style="float: right">
          <a-button
            class="ant-btn ant-btn-me"
            v-if="formItems.flag!='1'"
            @click="saveData('flag')"
          ><span>期初记账</span></a-button>
          <a-button
            class="ant-btn ant-btn-me"
            v-if="formItems.flag=='1'"
            @click="saveData('notFlag')"
          ><span>取消记账</span></a-button>

          <!-- 清空银行对账单和期初凭证+银行对账单期初头部 -->
          <a-button
            class="ant-btn ant-btn-me"
            v-if="formItems.flag!='1'"
            @click="saveData('clear')"
          ><span>清空数据</span></a-button>
          <a-button class="ant-btn ant-btn-me" @click="closeCurrent()">
            <span>退出</span></a-button>
          <!--        <button
                    type="button"
                    class="ant-btn"
                  ><span>导入</span></button>
                  <button
                    type="button"
                    class="ant-btn"
                  ><span>清空</span></button>
                  <button
                    type="button"
                    class="ant-btn"
                  ><span>导出</span></button>-->
        </div>
      </div>

      <div style="margin-left: 90px;">
      <AccountPicker theme="three" @reloadTable="dynamicAdReload" style="margin-top: -65px;" />
      <div class="app-container-neck" style="display: flex;justify-content : space-between;">
        <div style="width:auto; margin-left: 5px;display: flex;justify-content : space-between;">
          <div style="width:auto;">
<!--            <label>币种：{{ currencyName }}</label>-->
<!--            <a-select v-model:value="formItems.currencyId" :disabled="formItems.flag=='1'" placeholder="币种" style="width: 150px;">
              <a-select-option
                v-for="currency in currencyList"
                :key="currency.foreignName"
                :value="currency.foreignCode"
              >
                {{ currency.foreignName }}
              </a-select-option>
            </a-select>-->
            <label>对账单余额方向：{{ formItems.fangxiang=='1'?'借方':'贷方' }}</label>
            &emsp;&emsp;
            <label>年度：</label>
            <a-select v-model:value="formItems.iyear" @change="checkDate()" class="head-index-select" placeholder="年度">
              <a-select-option v-for="item in yearList" :key="item.accountYear" :value="item.accountYear">
                {{ item.accountYear }}
              </a-select-option>
            </a-select>
<!--            <a-select v-model:value="formItems.fangxiang" :disabled="formItems.flag=='1'"  placeholder="方向" @change="countYhMoney()" style="width: 80px;">
              <a-select-option value="1">借方</a-select-option>
              <a-select-option value="0">贷方</a-select-option>
            </a-select>-->
            <!--        <label>银行账户：</label>
                    <a-input placeholder="银行账户" style="width: 200px;"/>-->
            <!--        <label>对账单余额方向：</label>
                    <a-input placeholder="对账单余额方向" style="width: 200px;"/>-->
          </div>
        </div>
        <div style="width:auto; margin-left: 10px;text-align: right;">
          <a-button class="ant-btn-me">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
        </div>
      </div>
      </div>

    </div>

    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <div style="width: 250px;min-height: 250px;border: 1px #cccccc solid;margin-right: .2%;margin-bottom: 230px;padding-bottom: 0;background: #ffffff;">
          <div style="width: 100%; height: 32px;background-color: #d9d9d9;padding-top:5px;font-weight: bold;text-align: center;">
            银行科目
          </div>
          <!--        <label>银行科目：</label>
                  <a-select v-model:value="formItems.ccode" @change="checkDate()" placeholder="请选择科目" style="width: 150px;">
                    <a-select-option v-for="item in codeKemu" :key="item.ccode" :value="item.ccode">
                      {{ item.ccode }}-{{ item.ccodeName }}
                    </a-select-option>
                  </a-select>-->
          <BasicTree
            title=""
            :click-row-to-expand="false"
            :tree-data="treeData"
            :replace-fields="{ key: 'ccode', title: 'value' }"
            v-model:selectedKeys="selectedKeys"
            v-model:expandedKeys="expandedKeys"
            @select="handleSelect"
            style="font-size: 14px;font-weight: bold;margin-bottom: 0;"
          />
        </div>

        <div style="width: calc(100% - 250px); margin-left: 5px;">
          <div class="a-container-content" style="padding-bottom: 20px;background: #ffffff;">
<!--            <div style="text-align: center;font-size: 16px;font-weight: bold;">-->
            <div style="width: 100%; height: 32px;background-color: #d9d9d9;padding-top:5px;font-weight: bold;text-align: center;">
              总账对应银行科目期初余额：{{ toThousandFilter(qichuyue+'') }}元
            </div>
            <div style="display: flex;justify-content : space-between;">
            <div class="nc-open-content" style="width: 47%;background: #ffffff;margin-left: 2%;position: relative;">
              <div style="position: absolute;background: #ffffff; top: -15px;left: 20px;padding: 0 10px;">
                <label style="font-size: 14px;">银行日记账</label>
              </div>
              <div style="display: flex;justify-content : space-between;" :style="formItems.flag==1?{'padding-right': '30px'}:''">
                <label style="min-width:160px;">调整前余额：</label>
                <!--        <a-input v-if="dayBefore" :value="toThousandFilter(formItems.dayBeforeMoney)" readonly="readonly" @click="openEdit('dayBeforeMoney')" :precision="2" placeholder="调整前余额" style="width: 200px;"/>-->
                <span>
                  <a-input v-if="dayBefore" :value="toThousandFilter(formItems.statementBeforeMoney)" readonly="readonly" placeholder="调整前余额" style="width: 200px;text-align: right;"/>
                  <span v-if="formItems.flag!=1">
                    <span style="min-width: 30px;" v-if="dayBefore"><EditOutlined @click="dayBefore=false" style="width: 30px;"/></span>
                    <a-input-number v-if="!dayBefore" v-model:value="formItems.statementBeforeMoney" placeholder="调整前余额" @keyup.enter="saveData('dayBefore')" style="width: 200px;"/>
                    <span style="width: 30px;" v-if="!dayBefore"><CheckOutlined @click="saveData('dayBefore')" style="width: 30px;"/></span>
                  </span>
                </span>
              </div>
              <div style="display: flex;justify-content : space-between;padding-right: 30px;">
                <label style="min-width:160px;">加：银行已收企业未收：</label><!--对账单借-->
                <a-input v-model:value="yhJieMoney" readonly="readonly" placeholder="" style="width: 200px;text-align: right;"/>
              </div>
              <div style="display: flex;justify-content : space-between;padding-right: 30px;">
                <label style="min-width:160px;">减：银行已付企业未付：</label><!--对账单贷-->
                <a-input v-model:value="yhDaiMoney" readonly="readonly" placeholder="" style="width: 200px;text-align: right;"/>
              </div>
              <div style="display: flex;justify-content : space-between;padding-right: 30px;">
                <label style="min-width:160px;">调整后余额：</label><!--1+2-3-->
                <a-input v-model:value="yhYuMoney" readonly="readonly" placeholder="调整后余额" style="width: 200px;text-align: right;"/>
              </div>
            </div>
            <div class="nc-open-content" style="width: 47%;background: #ffffff;margin-right: 2%;position: relative">
              <div style="position: absolute;background: #ffffff; top: -15px;left: 20px;padding: 0 10px;">
                <label style="font-size: 14px;">单位日记账</label>
              </div>
              <div style="display: flex;justify-content : space-between;" :style="formItems.flag==1?{'padding-right': '30px'}:''">
                <label style="min-width:160px;">调整前余额：</label>
                <!--        <a-input v-model:value="formItems.statementBeforeMoney" readonly="readonly" @click="openEdit('statementBeforeMoney')" :precision="2" placeholder="调整前余额" style="width: 200px;"/>-->
                <span>
                  <a-input v-if="statementBefore" :value="toThousandFilter(formItems.dayBeforeMoney)" readonly="readonly" placeholder="调整前余额" style="width: 200px;text-align: right;"/>
                  <span v-if="formItems.flag!=1">
                    <span style="width: 30px;" v-if="statementBefore"><EditOutlined @click="statementBefore=false" style="width: 30px;"/></span>
                    <a-input-number v-if="!statementBefore" v-model:value="formItems.dayBeforeMoney" placeholder="调整前余额" @keyup.enter="saveData('statementBefore')" style="width: 200px;"/>
                    <span style="width: 30px;" v-if="!statementBefore"><CheckOutlined @click="saveData('statementBefore')" style="width: 30px;"/></span>
                  </span>
                  <span v-if="formItems.flag==1" style="width: 30px;"></span>
                </span>
              </div>
              <div style="display: flex;justify-content : space-between;padding-right: 30px;">
                <label style="min-width:160px;">加：企业已收银行未收：</label>
                <a-input v-model:value="dayJieMoney" readonly="readonly" placeholder="" style="width: 200px;text-align: right;"/>
              </div>
              <div style="display: flex;justify-content : space-between;padding-right: 30px;">
                <label style="min-width:160px;">减：企业已付银行未付：</label>
                <a-input v-model:value="dayDaiMoney" readonly="readonly" placeholder="" style="width: 200px;text-align: right;"/>
              </div>
              <div style="display: flex;justify-content : space-between;padding-right: 30px;">
                <label style="min-width:160px;">调整后余额：</label>
                <a-input v-model:value="dayYuMoney" readonly="readonly" placeholder="调整后余额" style="width: 200px;text-align: right;"/>
              </div>
            </div>
            </div>
          </div>

          <div class="a-container-content" style="margin-top: 10px;background: #ffffff;">
            <a-tabs v-model:activeKey="activeKey" type="card" tabPosition="top" @change="clickActive">
              <a-tab-pane key="1" tab="银行已收付企业未收付">

              </a-tab-pane>
              <a-tab-pane key="2" tab="企业已收付银行未收付">

              </a-tab-pane>
            </a-tabs>
            <div v-show="showTable.a1" ref="a1">
              <!-- 对账单余额方向：借方 -->
              <div v-if="formItems.fangxiang=='1'" style="margin-left: 30px;color:red;font-size: 13px;font-weight: bold;float:left;">银行借方等于企业贷方，请注意录入方向</div>
              <!-- 对账单余额方向：贷方 -->
              <div v-if="formItems.fangxiang=='0'" style="margin-left: 30px;color:red;font-size: 13px;font-weight: bold;float:left;">银行借方等于企业借方，请注意录入方向</div>
              <div v-if="formItems.flag!=1" class="ant-btn-group" style="float: right;margin-bottom: 10px;margin-right: 10px;">
                <a-button
                  class="ant-btn ant-btn-me"
                  @click="copyLastYearBankStatement()"
                ><span>结转上年</span></a-button>
                <a-button
                  class="ant-btn ant-btn-me"
                  @click="openBankStatementEditAdd()"
                ><span>添加</span></a-button>
                <a-button
                  class="ant-btn ant-btn-me"
                  @click="openBankStatementEditEdit()"
                ><span>修改</span></a-button>
                <a-button
                  class="ant-btn ant-btn-me"
                  @click="delList()"
                ><span>删除</span></a-button>
                <a-button
                  class="ant-btn ant-btn-me"
                  @click="openExcel()"
                ><span>导入</span></a-button>
                <a-button
                  class="ant-btn ant-btn-me"
                  @click="exportExcel()"
                ><span>导出</span></a-button>
              </div>
              <div style="clear: both"></div>
              <div class="temp">
              <BasicTable
                :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys,getCheckboxProps:rowSelection.getCheckboxProps, onChange: onSelectChange }"
                @selection-change="selectionChange"
                @row-click="rowClick"
                @register="registerTable"
                :dataSource="tableData"
                :scroll="{ x: totalColumnWidth,y: windowHeight }"
                :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
              >
                <template #jie="{ record }"><span v-if="record.jie!=0">{{ toThousandFilter(record.jie) }}</span></template>
                <template #dai="{ record }"><span v-if="record.dai!=0">{{ toThousandFilter(record.dai) }}</span></template>
              </BasicTable>
              <div class="pagination-text" v-show="showPaginationText">
                共 {{paginationNumber}} 条记录&nbsp;&nbsp; 每页 200 条
              </div>
              </div>
            </div>
            <div v-show="showTable.a2" ref="a2">
              <!-- 对账单余额方向：借方 -->
              <div v-if="formItems.fangxiang=='1'" style="margin-left: 30px;color:red;font-size: 13px;font-weight: bold;float:left;">企业贷方等于银行借方，请注意录入方向</div>
              <!-- 对账单余额方向：贷方 -->
              <div v-if="formItems.fangxiang=='0'" style="margin-left: 30px;color:red;font-size: 13px;font-weight: bold;float:left;">企业借方等于银行借方，请注意录入方向</div>
              <div v-if="formItems.flag!=1" class="ant-btn-group" style="float: right;margin-bottom: 10px;margin-right: 10px;">
                <a-button
                  class="ant-btn ant-btn-me"
                  @click="copyLastYearAcc()"
                ><span>结转上年</span></a-button>
                <a-button
                  class="ant-btn ant-btn-me"
                  @click="openAccAdd()"
                ><span>添加</span></a-button>
                <a-button
                  class="ant-btn ant-btn-me"
                  @click="openAccEdit()"
                ><span>修改</span></a-button>
                <a-button
                  class="ant-btn ant-btn-me"
                  @click="delAccList()"
                ><span>删除</span></a-button>
                <a-button
                  class="ant-btn ant-btn-me"
                  @click="openAccExcel()"
                ><span>导入</span></a-button>
                <a-button
                  class="ant-btn ant-btn-me"
                  @click="exportAccExcel()"
                ><span>导出</span></a-button>
              </div>
              <div style="clear: both"></div>
              <div class="temp">
              <BasicTable
                :row-selection="{ type: 'checkbox', selectedRowKeys: state1.selectedRowKeys,getCheckboxProps:rowSelection1.getCheckboxProps, onChange: onSelectChange1 }"
                @selection-change="selectionChange1"
                @row-click="rowClick1"
                @register="registerDayTable"
                :dataSource="tableData1"
                :scroll="{ x: totalColumnWidth,y: windowHeight }"
                :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
              >
                <template #md="{ record }">
                  <span v-if="record.md!=0">{{ toThousandFilter(record.md) }}</span></template>
                <template #mc="{ record }">
                  <span v-if="record.mc!=0">{{ toThousandFilter(record.mc) }}</span></template>
              </BasicTable>
              <div class="pagination-text" v-show="showPaginationText1">
                共 {{paginationNumber1}} 条记录&nbsp;&nbsp; 每页 200 条
              </div>
              </div>
            </div>
          </div>

          <Edit
            @save="saveData"
            @register="registerEditPage"
          />
          <Excel
            @save="saveExcel"
            @register="registerExcelPage"
          />
          <AccExcel
            @save="saveAccExcel"
            @register="registerAccExcelPage"
          />
          <BankStatementEdit
            @save="saveBankStatementData"
            @register="registerBankStatementPage"
          />
          <AccEdit
            @save="saveAccData"
            @register="registerAccPage"
          />
        </div>
      </PageWrapper>

    </div>
  </div>
</template>
<script setup="props" lang="ts">
import {BasicTree} from '/@/components/Tree'
import {PageWrapper} from '/@/components/Page'
import {
  Select as ASelect,
  Input as AInput,
  InputNumber as AInputNumber,
  Tabs as ATabs,
  Popover as APopover,
  DatePicker as ADatePicker,
  Button as AButton, message
} from 'ant-design-vue'

const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const ATabPane = ATabs.TabPane
import {nextTick, onMounted, reactive, ref} from "vue";
import {
  CaretDownFilled,
  FormOutlined,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled,
  CheckOutlined,
  EditOutlined,
  TransactionOutlined
} from '@ant-design/icons-vue'
import {
  deleteAccountList,
  deleteBankStatementList,
  excelAccount,
  excelBankStatement,
  findAccvoucherByIperiod,
  findAccvoucherByIperiodList,
  findBankStatementByKemuAndQichu, findByKemuAndDate,
  findCodeKemuByBr,
  findYearByAccount,
  saveAccvoucher,
  saveBankStatement
} from "/@/api/record/system/bank-statement"
import {BasicTable, useTable} from '/@/components/Table'
import {CrudApi, CrudDayApi} from "./data"
import {aoaToSheetXlsx} from "./excel/components/importexcel"
import Edit from './popup/edit.vue'
import Excel from './popup/excel.vue'
import AccExcel from './popup/acc_excel.vue'
import BankStatementEdit from './popup/bank_statement_edit.vue'
import AccEdit from './popup/acc_edit.vue'
import {useModal} from '/@/components/Modal'
import {
  findByYearAndKemu,
  // getBankStatementHeaderList,
  saveBankStatementHeader
} from "/@/api/record/system/bank-statement-header";
import {useMessage} from "/@/hooks/web/useMessage";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {currentCyDatas, getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {getCurrentAccountName, getThisIndexImg, hasBlank} from "/@/api/task-api/tast-bus-api";
import {findDataBase} from "/@/api/record/system/account";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findPeriodByAccontId} from "/@/api/record/generalLedger/data";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {
  findAccountByWeidazhang,
  findBankStatementByWeidazhang, findByKemuNotQichuAndDate
} from "/@/api/wei_da_zhang/wei_da_zhang";

const {
  createErrorModal,
  createSuccessModal,
  createConfirm
} = useMessage()

const {closeCurrent} = useTabs(router);

const visible = ref(false);
const windowWidth = (document.documentElement.clientWidth - (70 + 280))
// const windowHeight = (window.innerHeight)
const windowHeight = (window.innerHeight - (640))
const tableRef: any = ref(null)
const totalColumnWidth = ref(0)

function initTableWidth() {
  let total = windowWidth
  if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
  // totalColumnWidth.value = total
  tableRef.value.$el.style.setProperty('width', (total + 70) + 'px')
}

const isCheck=ref(false)
async function checkDateSelect() {
  const list1 = await useRouteApi(findByKemuAndDate, {schemaName: dynamicTenantId})({kemuCode:formItems.value.ccode, year:formItems.value.iyear,flag:''})
  const list2 = await useRouteApi(findAccvoucherByIperiod, {schemaName: dynamicTenantId})({ccode: formItems.value.ccode, iyear: formItems.value.iyear, iperiod: '20'})
  if (formItems.value.flag=='1'){
    isCheck.value = true
  } else {
    if ((formItems.value.dayBeforeMoney == null || formItems.value.dayBeforeMoney == '' || formItems.value.dayBeforeMoney == '0') &&
      (formItems.value.statementBeforeMoney == null || formItems.value.statementBeforeMoney == '' && formItems.value.statementBeforeMoney == '0')) {
      if (list1.items.length > 0 || list2.items.length > 0) {
        isCheck.value = true
      } else {
        isCheck.value = false
      }
    } else {
      isCheck.value = true
    }
  }
}

const dynamicTenantId = ref(getCurrentAccountName(true))
const formItems: any = ref({
  iyear: '',
  qiyongDate: '',
  ccode: '',
  flag: '',
  dayBeforeMoney: '',
  statementBeforeMoney: '',
  dayEndMoney: '',
  statementEndMoney: '',
  currencyId: '',
  fangxiang: ''
})
const yangshi = ref('1')
const dayBefore = ref(true)
const statementBefore = ref(true)
const activeKey = ref('1')
// 这是示例组件
const [registerTable, {
  reload,
  getDataSource,
  getColumns,
  setTableData,
  setColumns,
  getPaginationRef,
  setPagination,
  deleteSelectRowByKey
}] = useTable({
  // api: useRouteApi(CrudApi.list, {schemaName: dynamicTenantId}),
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: true,
  pagination: {
    pageSize: 200,
    // showSizeChanger: false,
    simple:true,
    showTotal: t => `共${t}条记录    每页200条`
  },
  /*searchInfo: {
    qichu: formItems.value.iyear + '00',
    kemuCode: formItems.value.ccode
  }*/
})

// 这是示例组件
const [registerDayTable, {
  reload:reload1,
  getDataSource:getDataSource1,
  getColumns:getColumns1,
  setTableData:setTableData1,
  setColumns:setColumns1,
  getPaginationRef:getPaginationRef1,
  setPagination:setPagination1,
  deleteSelectRowByKey:deleteSelectRowByKey1
}] = useTable({
  // api: useRouteApi(CrudDayApi.list, {schemaName: dynamicTenantId}),
  columns: CrudDayApi.columns,
  bordered: true,
  showIndexColumn: true,
  pagination: {
    pageSize: 200,
    // showSizeChanger: false,
    simple:true,
    showTotal: t => `共${t}条记录    每页200条`
  },
  /*searchInfo: {
    ccode: formItems.value.ccode,
    iyear: formItems.value.iyear,
    iperiod: '20'
  }*/
})

const tableData:any = ref([]);
const tableDataAll:any = ref([]);
const showPaginationText = ref(false)
const paginationNumber = ref(0)
async function reloadTable(){
  showPaginationText.value = false
  let len = 0
  const res = await useRouteApi(findBankStatementByKemuAndQichu,{schemaName: dynamicTenantId})({
    qichu: formItems.value.iyear + '00',
    kemuCode: formItems.value.ccode
  })
  tableDataAll.value = res.items
  tableData.value = tableDataAll.value
  len = tableDataAll.value.length
  let num1 = tableDataAll.value.length%200
  if (num1<20) {
    let num = 20 - (tableDataAll.value.length % 20)
    for (let i = 0; i < num; i++) {
      tableData.value.push({})
    }
  }
  paginationNumber.value = len
  showPaginationText.value = true
}
const tableData1:any = ref([]);
const tableDataAll1:any = ref([]);
const showPaginationText1 = ref(false)
const paginationNumber1 = ref(0)
async function reloadTable1(){
  showPaginationText1.value = false
  let len = 0
  const res = await useRouteApi(findAccvoucherByIperiod,{schemaName: dynamicTenantId})({
    ccode: formItems.value.ccode,
    iyear: formItems.value.iyear,
    iperiod: '20'
  })
  tableDataAll1.value = res.items
  tableData1.value = tableDataAll1.value
  len = tableDataAll1.value.length
  let num1 = tableDataAll1.value.length%200
  if (num1<20) {
    let num = 20 - (tableDataAll1.value.length % 20)
    for (let i = 0; i < num; i++) {
      tableData1.value.push({})
    }
  }
  paginationNumber1.value = len
  showPaginationText1.value = true
}

const [registerEditPage, {openModal: openEditPage}] = useModal()
const openEdit = (type) => {
  openEditPage(true, {
    data: formItems,
    type
  })
}
const [registerExcelPage, {openModal: openExcelPage}] = useModal()
const openExcel = () => {
  if (formItems.value.qiyongDate != undefined && formItems.value.qiyongDate != '') {
    let qiyong = ''
    dateList.value.forEach(item=>{
      if (formItems.value.qiyongDate==item.iperiodNum){
        qiyong = item.iyear+'-'+item.dateStart
      }
    })
    openExcelPage(true, {
      data: {
        year: formItems.value.iyear,
        qiyongDate: qiyong,
        kemuCode: formItems.value.ccode,
        dynamicTenantId: dynamicTenantId.value,
        defaultAdName: defaultAdName.value
      }
    })
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '',
      content: '请先选择启用日期！'
    })
  }
}
const [registerAccExcelPage, {openModal: openAccExcelPage}] = useModal()
const openAccExcel = () => {
  if (formItems.value.qiyongDate != undefined && formItems.value.qiyongDate != '') {
    let qiyong = ''
    dateList.value.forEach(item=>{
      if (formItems.value.qiyongDate==item.iperiodNum){
        qiyong = item.iyear+'-'+item.dateStart
      }
    })
    openAccExcelPage(true, {
      data: {
        year: formItems.value.iyear,
        qiyongDate: qiyong,
        kemuCode: formItems.value.ccode,
        dynamicTenantId: dynamicTenantId.value,
        defaultAdName: defaultAdName.value
      }
    })
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '',
      content: '请先选择启用日期！'
    })
  }
}

//导入Excel
async function saveExcel(data: any) {
  await useRouteApi(excelBankStatement, {schemaName: dynamicTenantId})(data)
  await reloadTable()
  /*await reload({
    searchInfo: {
      qichu: formItems.value.iyear + '00',
      kemuCode: formItems.value.ccode
    }
  })*/
  setTimeout(() => {
    countYhMoney()
  }, 1000)
}
//银行对账单结转上年
async function copyLastYearBankStatement(){
  createConfirm({
    iconType: 'error',
    title: '警告',
    content: '结转上年需要清空当年期初数据，你确认要结转上年吗?',
    onOk: async () => {
      let lastYear = parseInt(formItems.value.year) - 1
      const dataBase:any = await findDataBase(defaultAdName.value,lastYear)
      //删除本年期初
      const deleteList = await useRouteApi(findBankStatementByKemuAndQichu, {schemaName: dynamicTenantId})({
        qichu: formItems.value.iyear + '00',
        kemuCode: formItems.value.ccode
      })
      await useRouteApi(deleteBankStatementList, {schemaName: dynamicTenantId})(deleteList.items)
      //保存上年期初
      if (dataBase!=null && dataBase!="") {
        const bankStatementList = await useRouteApi(findBankStatementByWeidazhang, {schemaName: dataBase.accountMode})({
          kemuCode: formItems.value.ccode,
          year: lastYear,
          flag: '0'
        })
        bankStatementList.map(item => {
          item.id = null
          item.qichu = formItems.value.iyear + '00'
          item.iyear = formItems.value.iyear
          return item
        })
        await useRouteApi(excelBankStatement, {schemaName: dynamicTenantId})(bankStatementList)
      }
      //保存本年期初（启用期间以前的值排除期初）
      let qiyong = ''
      dateList.value.forEach(item=>{
        if (formItems.value.qiyongDate==item.iperiodNum){
          qiyong = item.iyear+'-'+item.dateStart
        }
      })
      const bankStatementYearList = await useRouteApi(findByKemuNotQichuAndDate, {schemaName: dynamicTenantId})({
        kemuCode: formItems.value.ccode,
        year: formItems.value.iyear,
        flag: '0',
        endDate: qiyong
      })
      bankStatementYearList.map(item => {
        item.id = null
        item.qichu = formItems.value.iyear + '00'
        item.iyear = formItems.value.iyear
        return item
      })
      await useRouteApi(excelBankStatement, {schemaName: dynamicTenantId})(bankStatementYearList)
      await reloadTable()
      /*await reload({
        searchInfo: {
          qichu: formItems.value.iyear + '00',
          kemuCode: formItems.value.ccode
        }
      })*/
      setTimeout(() => {
        countYhMoney()
      }, 1000)
    },
    onCancel: () => {
      return false
    }
  })
}

//导入凭证Excel
async function saveAccExcel(data: any) {
  await useRouteApi(excelAccount, {schemaName: dynamicTenantId})(data)
  await reloadTable1()
  /*await reload1({
    searchInfo: {
      ccode: formItems.value.ccode,
      iyear: formItems.value.iyear,
      iperiod: '20'
    }
  })*/
  setTimeout(() => {
    countYhMoney()
  }, 1000)
}

//日记账结转上年
async function copyLastYearAcc(){
  createConfirm({
    iconType: 'error',
    title: '警告',
    content: '结转上年需要清空当年期初数据，你确认要结转上年吗?',
    onOk: async () => {
      let lastYear = parseInt(formItems.value.year) - 1
      const dataBase:any = await findDataBase(defaultAdName.value,lastYear)
      //删除本年期初
      const deleteList = await useRouteApi(findAccvoucherByIperiod, {schemaName: dynamicTenantId})({
        ccode: formItems.value.ccode,
        iyear: formItems.value.iyear,
        iperiod: '20'
      })
      await useRouteApi(deleteAccountList, {schemaName: dynamicTenantId})(deleteList.items)
      //保存上年期初
      if (dataBase!=null && dataBase!="") {
        const accList = await useRouteApi(findAccountByWeidazhang, {schemaName: dataBase.accountMode})({
          kemuCode: formItems.value.ccode,
          year: lastYear,
          flag: '0'
        })
        accList.map(item => {
          item.id = null
          item.iperiod = '20'
          item.imonth = '20'
          item.iyear = formItems.value.iyear
          item.iyperiod = formItems.value.iyear + '20'
          return item
        })
        await useRouteApi(excelAccount, {schemaName: dynamicTenantId})(accList)
      }
      //保存本年期初
      let iperiod2 = 0
      if (formItems.value.qiyongDate!='') {
        iperiod2 = parseInt(formItems.value.qiyongDate) - 1
      }
      const accYearList = await useRouteApi(findAccvoucherByIperiodList, {schemaName: dynamicTenantId})({
        ccode: formItems.value.ccode,
        iyear: formItems.value.iyear,
        iperiod1: '00',
        iperiod2: pad(iperiod2,2)
      })
      accYearList.items.filter(item=>item.statementNum==null||item.statementNum=='').map(item => {
        item.id = null
        item.iperiod = '20'
        item.imonth = '20'
        item.iyear = formItems.value.iyear
        item.iyperiod = formItems.value.iyear + '20'
        return item
      })
      await useRouteApi(excelAccount, {schemaName: dynamicTenantId})(accYearList.items)
      await reloadTable1()
      /*await reload1({
        searchInfo: {
          ccode: formItems.value.ccode,
          iyear: formItems.value.iyear,
          iperiod: '20'
        }
      })*/
      setTimeout(() => {
        countYhMoney()
      }, 1000)
    },
    onCancel: () => {
      return false
    }
  })
}

//修改银行对账单
const [registerBankStatementPage, {openModal: openBankStatementEditPage}] = useModal()
const openBankStatementEditEdit = () => {
  if (checkRow.value.length > 0) {
    let qiyong = ''
    dateList.value.forEach(item=>{
      if (formItems.value.qiyongDate==item.iperiodNum){
        qiyong = item.iyear+'-'+item.dateStart
      }
    })
    openBankStatementEditPage(true, {
      data: checkRow.value[0],
      // qiyongDate: formItems.value.qiyongDate,
      qiyongDate: qiyong,
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value,
      year: formItems.value.iyear,
      isState:'1'
    })
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '修改',
      content: '请选择一条进行修改！'
    })
  }
}
const openBankStatementEditAdd = () => {
  let qiyong = ''
  dateList.value.forEach(item=>{
    if (formItems.value.qiyongDate==item.iperiodNum){
      qiyong = item.iyear+'-'+item.dateStart
    }
  })
  openBankStatementEditPage(true, {
    data: {
      statementDate: null,
      kemuCode: formItems.value.ccode,
      duifangUnit: '',
      settModes: '',
      piaohao: '',
      jie: '',
      dai: '',
      fangxiang: '',
      flag: '0',
      statementNum: '',
      remarks: '',
      currencyId: '',
      qichu: formItems.value.iyear + '00'
    },
    // qiyongDate: formItems.value.qiyongDate,
    qiyongDate: qiyong,
    dynamicTenantId: dynamicTenantId.value,
    defaultAdName: defaultAdName.value,
    year: formItems.value.iyear,
    isState:'0'
  })
}

//修改保存银行对账单信息
async function saveBankStatementData(data) {
  await useRouteApi(saveBankStatement, {schemaName: dynamicTenantId})(data)
  await reloadTable()
  /*await reload({
    searchInfo: {
      qichu: formItems.value.iyear + '00',
      kemuCode: formItems.value.ccode
    }
  })*/
  setTimeout(() => {
    countYhMoney()
  }, 1000)
}

//修改单位日记账
const [registerAccPage, {openModal: openAccEditPage}] = useModal()
const openAccAdd = () => {
  let ccodeName = ''
  /*codeKemu.value.forEach(item=>{
    if (formItems.value.ccode == item.ccode){
      ccodeName = item.ccodeName
    }
  })*/
  openAccEditPage(true, {
    data: {
      dbillDate: null,
      csign: '',
      inoId: '',
      cdigest: '',
      ccode: formItems.value.ccode,
      ccodeName: ccodeName,
      md: '',
      mc: '',
      pjCsettle: '',
      pjId: '',
      pjDate: '',
      pjUnitName: '',
      iyear: formItems.value.iyear,
      iperiod: '20',
      ifrag: '0',
      iyperiod: formItems.value.iyear + '20',
    },
    qiyongDate: formItems.value.qiyongDate,
    dynamicTenantId: dynamicTenantId.value,
    defaultAdName: defaultAdName.value,
    isState:'0'
  })
}
const openAccEdit = () => {
  if (checkRow1.value.length == 1) {
    openAccEditPage(true, {
      data: checkRow1.value[0],
      qiyongDate: formItems.value.qiyongDate,
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value,
      isState:'1'
    })
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '修改',
      content: '请选择一条进行修改！'
    })
  }
}

async function saveAccData(data) {
  await useRouteApi(saveAccvoucher, {schemaName: dynamicTenantId})(data)
  await reloadTable1()
  /*await reload1({
    searchInfo: {
      ccode: formItems.value.ccode,
      iyear: formItems.value.iyear,
      iperiod: '20'
    }
  })*/
  setTimeout(() => {
    countYhMoney()
  }, 1000)
}

const defaultAdName = ref(useCompanyOperateStoreWidthOut().getSchemaName)
const yearList:any = ref({})
async function reloadYear() {
  const res = await findYearByAccount(defaultAdName.value)
  yearList.value = res
  if (res.length>0 && (formItems.value.iyear=='' || formItems.value.iyear==undefined)) {
    formItems.value.iyear = res[0].accountYear
  }
  setTimeout(()=>{
    countYhMoney()
  },1000)
}
const showTable = ref({
  a1: true,
  a2: false
})

async function clickActive() {
  if (activeKey.value == '1') {
    showTable.value.a1 = true
    showTable.value.a2 = false
  } else {
    showTable.value.a1 = false
    showTable.value.a2 = true
  }
}

/*const codeKemu:any = ref({})
async function reloadKemuPage() {
  const res = await useRouteApi(findCodeKemuByBr, {schemaName: dynamicTenantId})()
  codeKemu.value = res.filter(item=>{
    return item.bend=='1'
  })
  // console.log(codeKemu.value)
  if (codeKemu.value.length>0 && (formItems.value.ccode=='' || formItems.value.ccode == null)) {
    formItems.value.ccode = codeKemu.value[0].ccode
  }
}*/

const currencyList: any = ref([]);
const currencyId = ref()

async function reloadCurrency() {
  currencyList.value = [];
  useCompanyOperateStoreWidthOut().getAccountList.forEach(item => {
    if (item.accId == defaultAdName.value) {
      if (item.currency != '' && item.currency != null) {
        currencyList.value = [{
          accountId: defaultAdName.value,
          foreignCode: item.currency,
          foreignName: item.currencyName
        }]
        formItems.value.currencyId = item.currency
        currencyName.value = item.currencyName
        currencyId.value = item.currency
        formItems.value.fangxiang = item.statementFangxiang==null?'1':item.statementFangxiang
      }
      // formItems.value.qiyongDate = item.accStartDate.slice(0,10)
      if (item.startPeriod != '' && item.startPeriod != null) {
        if (formItems.value.qiyongDate == '' || formItems.value.qiyongDate == null) {
          formItems.value.qiyongDate = item.startPeriod.slice(4)
        }
      }
    }
  })
  useRouteApi(currentCyDatas, {schemaName: dynamicTenantId})({accId: defaultAdName.value}).then(res => {
    currencyList.value.push(...res.items)
  });
}

//获取会计区间
const dateList: any = ref([])

async function reloadPeriod() {
  const res = await findPeriodByAccontId(defaultAdName.value)
  dateList.value = res.filter(item => item.iyear==formItems.value.iyear)
  if (formItems.value.qiyongDate != '' && dateList.value.length > 0) {
    formItems.value.qiyongDate = dateList.value[0].iperiodNum
  }
}

// onMounted(async () => {
  // await reloadYear()
  // await reloadKemuPage()
  // await fetch()
  // await reloadCurrency()
  /*if (formItems.value.iyear != null && formItems.value.iyear != '' && formItems.value.ccode != null && formItems.value.ccode != '') {
    const res = await useRouteApi(findByYearAndKemu, {schemaName: dynamicTenantId})({
      year: formItems.value.iyear,
      kemuCode: formItems.value.ccode
    })
    if (res.length > 0) {
      // formItems.value = res[0]
      formItems.value.id = res[0].id
      formItems.value.iyear = res[0].iyear
      formItems.value.qiyongDate = res[0].qiyongDate
      formItems.value.ccode = res[0].ccode
      formItems.value.flag = res[0].flag
      formItems.value.dayBeforeMoney = res[0].dayBeforeMoney
      formItems.value.statementBeforeMoney = res[0].statementBeforeMoney
      formItems.value.currencyId = res[0].currencyId
      formItems.value.dayEndMoney = res[0].dayEndMoney
      formItems.value.statementEndMoney = res[0].statementEndMoney
      formItems.value.fangxiang = res[0].fangxiang
    } else {
      formItems.value.id = null
      formItems.value.qiyongDate = ''
      formItems.value.dayBeforeMoney = ''
      formItems.value.statementBeforeMoney = ''
      formItems.value.dayEndMoney = ''
      formItems.value.statementEndMoney = ''
      formItems.value.fangxiang = '1'
      formItems.value.flag = '0'
    }
    await reloadCurrency()
    await reloadPeriod()
    await reload({
      searchInfo: {
        qichu: formItems.value.iyear + '00',
        kemuCode: formItems.value.ccode
      }
    })
    await reload1({
      searchInfo: {
        ccode: formItems.value.ccode,
        iyear: formItems.value.iyear,
        iperiod: '20'
      }
    })
    setTimeout(() => {
      countYhMoney()
    }, 1000)
  }*/
// })

//导出Excel
const arrHeader = CrudApi.columns.map((column) => column.title);

function exportExcel() {
  const arrData = getDataSource().filter(item=>item.id!=null).map((item) => getColumns().map((column: any) => item[column.dataIndex]));
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: arrData,
    header: arrHeader,
    filename: '银行对账期初列表_'+pageParameter.companyName+'.xlsx',
  });
}

function exportAccExcel() {
  const arrHeader1 = CrudDayApi.columns.map((column) => column.title);
  const arrData1 = getDataSource1().filter(item=>item.id!=null).map((item) => getColumns1().map((column: any) => item[column.dataIndex]));
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: arrData1,
    header: arrHeader1,
    filename: '单位日记账期初_'+pageParameter.companyName+'.xlsx',
  });
}

function pad(num, n) {
  var len = num.toString().length;
  while(len < n) {
    num = "0" + num;
    len++;
  }
  return num;
}
//计算科目余额
const qichuyue = ref(0)
async function countKemuYue() {
  let iperiod2 = 0
  if (formItems.value.qiyongDate!='') {
    iperiod2 = parseInt(formItems.value.qiyongDate) - 1
  }
  const arrData1 = await useRouteApi(findAccvoucherByIperiodList, {schemaName: dynamicTenantId})({
    ccode: formItems.value.ccode,
    iyear: formItems.value.iyear,
    iperiod1: '00',
    iperiod2: pad(iperiod2,2)
  })
  let jieSum1 = 0
  let daiSum1 = 0
  arrData1.items.forEach(
    function (item) {
      if (item.md != '') {
        jieSum1 = add(item.md, jieSum1)
      }
      if (item.mc != '') {
        daiSum1 = add(item.mc, daiSum1)
      }
    }
  )
  // let qichuyue = 0
  kemuList.value.forEach(res=>{
    if (res.ccode==formItems.value.ccode) {
      if (res.bprogerty == '1') {
        qichuyue.value = sub(jieSum1, daiSum1)
      } else {
        qichuyue.value = sub(daiSum1, jieSum1)
      }
    }
  })
  // console.log(qichuyue.value)
  return qichuyue.value;
}

async function saveData(data) {
  //修改单位日记账调整前余额
  if (data == 'dayBefore') {
    dayBefore.value = true
  }
  //修改银行对账单调整前余额
  else if (data == 'statementBefore') {
    statementBefore.value = true
  }
  //期初记账
  else if (data == 'flag') {
    if (formItems.value.statementEndMoney != formItems.value.dayEndMoney) {
      createErrorModal({
        iconType: 'warning',
        title: '记账失败',
        content: '银行日记账调整后余额必须与单位日记账调整后余额相等，不能记账！'
      })
      return false
    }
    // let qichuyue = await extracted();
    if (formItems.value.dayEndMoney != qichuyue.value) {
      createErrorModal({
        iconType: 'warning',
        title: '记账失败',
        content: '调整后余额与科目期初余额' + toThousandFilter(qichuyue.value + '') + '不相等，不能记账！'
      })
      return false
    }
    formItems.value.flag = '1'
  }
  //取消记账
  else if (data == 'notFlag') {
    formItems.value.flag = '0'
  } else if (data == 'clear') {
    formItems.value.dayBeforeMoney = null
    formItems.value.dayEndMoney = null
    formItems.value.statementBeforeMoney = null
    formItems.value.statementEndMoney = null
  }
  // formItems.value = data
  if (formItems.value.iyear != null && formItems.value.iyear != '' && formItems.value.ccode != null && formItems.value.ccode != '') {
    if (formItems.value.flag == '' || formItems.value.flag == null) {
      formItems.value.flag = '0'
    }
    setTimeout(() => {
      countYhMoney()
    }, 1000)
    // await saveBankStatementHeader(data)
    await useRouteApi(saveBankStatementHeader, {schemaName: dynamicTenantId})(formItems.value)
    const res = await useRouteApi(findByYearAndKemu, {schemaName: dynamicTenantId})({
      year: formItems.value.iyear,
      kemuCode: formItems.value.ccode
    })
    if (res.length > 0) {
      // formItems.value = res[0]
      formItems.value.id = res[0].id
    } else {
      formItems.value.id = null
    }
  }
}

//选中内容
const state = reactive<{
  selectedRowKeys: [];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const checkRow: any = ref([])
const onSelectChange = (selectedRowKeys,row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row.filter(item=>item.id!=null)
};

const rowSelection = {
  getCheckboxProps: (record) => ({
    disabled:  record.id==undefined
  }),
};
// 行勾选事件
const editAndDelBtnShow = ref(false);
function selectionChange(a) {
  editAndDelBtnShow.value=a.rows.filter(aa=>aa.flag=='1').length>0?true:false
}

function rowClick(a) {
  if(hasBlank(a.id)){
    deleteSelectRowByKey(a.key)
  }
}

//选中内容
const state1 = reactive<{
  selectedRowKeys: [];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const checkRow1: any = ref([])
const onSelectChange1 = (selectedRowKeys,row) => {
  // console.log('selectedRowKeys changed: ', row);
  state1.selectedRowKeys = selectedRowKeys;
  checkRow1.value = row.filter(item=>item.id!=null)
};

const rowSelection1 = {
  getCheckboxProps: (record) => ({
    disabled:  record.id==undefined
  }),
};
// 行勾选事件
const editAndDelBtnShow1 = ref(false);
function selectionChange1(a) {
  editAndDelBtnShow1.value=a.rows.filter(aa=>aa.flag=='1').length>0?true:false
}

function rowClick1(a) {
  if(hasBlank(a.id)){
    deleteSelectRowByKey1(a.key)
  }
}

//批量删除
async function delList() {
  if (checkRow.value.length > 0) {
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        await useRouteApi(deleteBankStatementList, {schemaName: dynamicTenantId})(checkRow.value)
        await reloadTable()
        /*await reload({
          searchInfo: {
            kemuCode: formItems.value.ccode,
            qichu: formItems.value.iyear + '00',
          }
        })*/
        checkRow.value = []
        state.selectedRowKeys = []
        message.success('删除成功！')
        setTimeout(() => {
          countYhMoney()
        }, 1000)
      },
      onCancel: () => {
        return false
      }
    })
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '删除失败',
      content: '请至少选择一条进行删除！'
    })
  }
}

//批量凭证银行期初删除
async function delAccList() {
  if (checkRow1.value.length > 0) {
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        await useRouteApi(deleteAccountList, {schemaName: dynamicTenantId})(checkRow1.value)
        await reloadTable1()
        /*await reload1({
          searchInfo: {
            ccode: formItems.value.ccode,
            iyear: formItems.value.iyear,
            iperiod: '20'
          }
        })*/
        checkRow1.value = []
        state1.selectedRowKeys = []
        message.success('删除成功！')
        setTimeout(() => {
          countYhMoney()
        }, 1000)
      },
      onCancel: () => {
        return false
      }
    })
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '删除失败',
      content: '请至少选择一条进行删除！'
    })
  }
}

const yhJieMoney = ref()
const yhDaiMoney = ref()
const yhYuMoney = ref()
const dayJieMoney = ref()
const dayDaiMoney = ref()
const dayYuMoney = ref()

async function countYhMoney() {
  await countKemuYue()
  await checkDateSelect()
  const arrData = await useRouteApi(CrudApi.list, {schemaName: dynamicTenantId})({
    kemuCode: formItems.value.ccode,
    qichu: formItems.value.iyear + '00'
  });
  let jieSum = 0
  let daiSum = 0
  arrData.items.forEach(
    function (item) {
      if (item.jie != '') {
        jieSum = add(item.jie, jieSum)
      }
      if (item.dai != '') {
        daiSum = add(item.dai, daiSum)
      }
    }
  )
  let statementBeforeMoney = 0;
  if (formItems.value.statementBeforeMoney != '' && formItems.value.statementBeforeMoney != undefined) {
    statementBeforeMoney = formItems.value.statementBeforeMoney
  }
  yhJieMoney.value = toThousandFilter(jieSum.toFixed(2))
  yhDaiMoney.value = toThousandFilter(daiSum.toFixed(2))
  if (formItems.value.fangxiang == '1') {
    yhYuMoney.value = toThousandFilter(sub(add(statementBeforeMoney, jieSum), daiSum).toFixed(2))
    formItems.value.statementEndMoney = sub(add(statementBeforeMoney, jieSum), daiSum)
  } else {
    yhYuMoney.value = toThousandFilter(add(sub(statementBeforeMoney, jieSum), daiSum).toFixed(2))
    formItems.value.statementEndMoney = add(sub(statementBeforeMoney, jieSum), daiSum)
  }

  const arrData1 = await useRouteApi(CrudDayApi.list, {schemaName: dynamicTenantId})({
    ccode: formItems.value.ccode,
    iyear: formItems.value.iyear,
    iperiod: '20'
  });
  let jieSum1 = 0
  let daiSum1 = 0
  arrData1.items.forEach(
    function (item) {
      if (item.md != '') {
        jieSum1 = add(item.md, jieSum1)
      }
      if (item.mc != '') {
        daiSum1 = add(item.mc, daiSum1)
      }
    }
  )
  let dayBeforeMoney = 0;
  if (formItems.value.dayBeforeMoney != '' && formItems.value.dayBeforeMoney != undefined) {
    dayBeforeMoney = formItems.value.dayBeforeMoney
  }
  dayJieMoney.value = toThousandFilter(jieSum1.toFixed(2))
  dayDaiMoney.value = toThousandFilter(daiSum1.toFixed(2))

  if (formItems.value.fangxiang == '1') {
    dayYuMoney.value = toThousandFilter(sub(add(dayBeforeMoney, jieSum1), daiSum1).toFixed(2))
    formItems.value.dayEndMoney = sub(add(dayBeforeMoney, jieSum1), daiSum1)
  } else {
    dayYuMoney.value = toThousandFilter(add(sub(dayBeforeMoney, jieSum1), daiSum1).toFixed(2))
    formItems.value.dayEndMoney = add(sub(dayBeforeMoney, jieSum1), daiSum1)
  }
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

//金额格式化
function toThousandFilter(num: any) {
  if (num == '' || num == null) {
    return ''
  }
  // NumberTool.amountThousands()
  return (+num || 0).toFixed(2).replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,')
}

async function checkDate() {
  if (formItems.value.iyear != null && formItems.value.iyear != '' && formItems.value.ccode != null && formItems.value.ccode != '') {
    const res = await useRouteApi(findByYearAndKemu, {schemaName: dynamicTenantId})({
      year: formItems.value.iyear,
      kemuCode: formItems.value.ccode
    })
    if (res.length > 0) {
      // formItems.value = res[0]
      formItems.value.id = res[0].id
      formItems.value.iyear = res[0].iyear
      formItems.value.qiyongDate = res[0].qiyongDate
      formItems.value.ccode = res[0].ccode
      formItems.value.flag = res[0].flag
      formItems.value.dayBeforeMoney = res[0].dayBeforeMoney
      formItems.value.statementBeforeMoney = res[0].statementBeforeMoney
      formItems.value.currencyId = res[0].currencyId
      formItems.value.dayEndMoney = res[0].dayEndMoney
      formItems.value.statementEndMoney = res[0].statementEndMoney
      // formItems.value.fangxiang = res[0].fangxiang
    } else {
      formItems.value.id = null
      if (formItems.value.qiyongDate==null || formItems.value.qiyongDate=='') {
        formItems.value.qiyongDate = '01'
      }
      formItems.value.dayBeforeMoney = ''
      formItems.value.statementBeforeMoney = ''
      formItems.value.dayEndMoney = ''
      formItems.value.statementEndMoney = ''
      // formItems.value.fangxiang = '1'
      formItems.value.flag = '0'
    }
    if (formItems.value.currencyId == '' || formItems.value.currencyId == null) {
      formItems.value.currencyId = currencyId.value
    }
    await reloadTable()
    await reloadTable1()
    /*await reload({
      searchInfo: {
        qichu: formItems.value.iyear + '00',
        kemuCode: formItems.value.ccode
      }
    })
    await reload1({
      searchInfo: {
        ccode: formItems.value.ccode,
        iyear: formItems.value.iyear,
        iperiod: '20'
      }
    })*/
    setTimeout(() => {
      countYhMoney()
    }, 1000)
  }
}

function onSearch() {
}

const defaultPage = ref(false)
const pageParameter: any = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '北京希格科技',
  ifUnit: false,
})
onMounted(async () => {
  // 进页面执行
  getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(res => {
    if (null != res && res.independent == 0) {
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
  })
})

const treeData: any = ref([])
const kemuList: any = ref([])
const selectedKeys = ref<string[]>([])
const expandedKeys = ref<string[]>([])

async function fetch() {
  const res = await useRouteApi(findCodeKemuByBr, {schemaName: dynamicTenantId})({})
  const codeKemuTree = res.filter(item => {
    return item.bend == '1'
  })
  kemuList.value = codeKemuTree
  if (codeKemuTree.length == 0) {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '该账套没有银行科目，请添加银行科目有继续操作！'
    })
    // await closeCurrent()
    // await router.push('/home/welcome')
    // return false
  }

  function a(codeKemuTree: any) {
    codeKemuTree.forEach((item: any) => {
      item.value = item.ccode + ' - ' + item.ccodeName
    })
  }

  function b(codeKemuTree: any) {
    let arr: any = []
    arr.push(codeKemuTree[0].ccode)
    return arr
  }

  a(codeKemuTree)
  treeData.value = []
  treeData.value.push({id: '0', key: '0', ccode: '0', value: '银行科目', children: codeKemuTree})
  selectedKeys.value = b(codeKemuTree)
  expandedKeys.value = b(treeData.value)
  formItems.value.ccode = selectedKeys.value[0]
}

const currencyName = ref("")
function handleSelect(keys: string) {
  if (keys[0] != '0') {
    formItems.value.ccode = keys[0]
    kemuList.value.forEach(item=>{
      if(item.ccode == formItems.value.ccode) {
        if (item.currency == '1') {
          formItems.value.currencyId = item.currencyType
        }
      }
    })
    currencyList.value.forEach(item=>{
      if (item.foreignCode==formItems.value.currencyId){
        currencyName.value=item.foreignName
      }
    })
    checkDate()
  }
}

const dynamicAdReload = async (obj) => {
  // const dataBase:any = await findDataBase(obj.accId,obj.year)
  dynamicTenantId.value = obj.accountMode
  defaultAdName.value = obj.accId
  formItems.value.iyear = obj.year
  pageParameter.companyName = obj.baseName
  await reloadYear()
  // await reloadKemuPage()
  await fetch()
  // await reloadCurrency()
  if (formItems.value.iyear != null && formItems.value.iyear != '' && formItems.value.ccode != null && formItems.value.ccode != '') {
    const res = await useRouteApi(findByYearAndKemu, {schemaName: dynamicTenantId})({
      year: formItems.value.iyear,
      kemuCode: formItems.value.ccode
    })
    if (res.length > 0) {
      // formItems.value = res[0]
      formItems.value.id = res[0].id
      formItems.value.iyear = res[0].iyear
      formItems.value.qiyongDate = res[0].qiyongDate
      formItems.value.ccode = res[0].ccode
      formItems.value.flag = res[0].flag
      formItems.value.dayBeforeMoney = res[0].dayBeforeMoney
      formItems.value.statementBeforeMoney = res[0].statementBeforeMoney
      formItems.value.currencyId = res[0].currencyId
      formItems.value.dayEndMoney = res[0].dayEndMoney
      formItems.value.statementEndMoney = res[0].statementEndMoney
      // formItems.value.fangxiang = res[0].fangxiang
    } else {
      formItems.value.id = null
      if (formItems.value.qiyongDate==null || formItems.value.qiyongDate=='') {
        formItems.value.qiyongDate = '01'
      }
      formItems.value.dayBeforeMoney = ''
      formItems.value.statementBeforeMoney = ''
      formItems.value.dayEndMoney = ''
      formItems.value.statementEndMoney = ''
      // formItems.value.fangxiang = '1'
      formItems.value.flag = '0'
    }
    await reloadCurrency()
    await reloadPeriod()
    await reloadTable()
    await reloadTable1()
    /*await reload({
      searchInfo: {
        qichu: formItems.value.iyear + '00',
        kemuCode: formItems.value.ccode
      }
    })
    await reload1({
      searchInfo: {
        ccode: formItems.value.ccode,
        iyear: formItems.value.iyear,
        iperiod: '20'
      }
    })*/
    setTimeout(() => {
      countYhMoney()
    }, 1000)
  }

  /*let data: any = {}
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  data.qiyongDate = formItems.value.qiyongDate
  data.kemuCode = formItems.value.ccode
  let res: any = await useRouteApi(CrudApi.list, {schemaName: dataBase.accountMode})(data)
  setTableData([]) // 清空可能残留的数据
  setTableData(res.items)
  // 底部分页信息
  pageParameter.thisAdInfo = {}
  pageParameter.thisAdInfo = obj
  pageParameter.total = res.total
  setPagination({total: res.total})*/
}
</script>
<style src="./global-menu-index.less" lang="less" scoped></style>
<style scoped lang="less">
:deep(.vben-basic-table) {
  .ant-table-wrapper {
    .ant-table-measure-row{
      td{
        padding: 0!important;
      }
    }
  }
}

.nc-open-content {
  border: 1px solid #999999;
  padding: 10px 20px;
  margin-top: 20px;
  height: 160px;
}

input {
  width: 35%;
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
  font-weight: bold;
  font-size: 14px;
}

:deep(.ant-input-number){
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;

  .ant-input-number-handler-wrap{
    display:none;
  }
  input{
    font-size: 14px;
    font-weight: bold;
    font-family: Arial !important;
  }
}

.nc-open-content {
  :deep(.ant-input) {
    font-size: 13px !important;
    color: #0096c7 !important;
    font-family: 'Helvetica';
  }
}

label {
  font-weight: bold;
  font-size: 13px;
}

:deep(.ant-card-body) {
  padding: 16px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
}

:deep(.ant-select-selector) {
  border: none !important;
  border-bottom: 1px solid #bdb9b9 !important;
  font-weight: bold;
  font-size: 14px;
}

:deep(.ant-calendar-picker-input.ant-input),
:deep(.ant-select-single:not(.ant-select-customize-input)
      .ant-select-selector
      .ant-select-selection-search-input) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
  font-weight: bold;
  font-size: 14px;
}

.ant-btn {
  color: #0096c7;
}

:deep(.ant-tabs.ant-tabs-card .ant-tabs-card-bar .ant-tabs-tab) {
  font-weight: bold;
  font-size: 13px;
}

.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
}

.app-container:nth-of-type(2) {
  padding: 0px;
  margin: 5px 10px;
  background: #b4c8e3 !important;
}

.temp{
  position: relative;
  .pagination-text {
    position: absolute;
    bottom: 6px;
    right: 200px;
    font-size: 13px;
    color: black;
    z-index: 99999999;
  }
}

.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
  border-color: #aaaaaa !important;
  font-weight: 550;
  color: #000000 !important;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 13px !important;
  padding: 2px 8px !important;
  border-color: #aaaaaa !important;
  font-weight: 550;
  color: #000000 !important;
}

:deep(.ant-tabs-card).ant-tabs-top > .ant-tabs-nav  {
  height: 40px;
  font-weight: bold;
  font-size: 13px;
  background-color: #d9d9d9 !important;
  .ant-tabs-tab-active,.ant-tabs-card.ant-tabs-top > div > .ant-tabs-nav .ant-tabs-tab-active {
    border-top: 2px solid rgb(1, 143, 251) !important;
    background-color: #d9d9d9 !important;
  }
  .ant-tabs-tab,.ant-tabs-card.ant-tabs-top > div > .ant-tabs-nav .ant-tabs-tab {
    background-color: #d9d9d9 !important;
  }
}

:deep(.ant-table-thead) th{
  text-align: center !important;
  font-weight: bold;
  //background-color: #f2f2f2 !important;
  background-color: #cccccc !important;
  border-color: #aaaaaa !important;
}

:deep(.vben-basic-table) .ant-table-wrapper {
  padding: 0px;
}

:deep(.vben-basic-table) .ant-table {
  width: 100%;
  margin: 0;
  overflow-x: hidden;
  height: calc(100% - 160px);
}

:deep(.vben-basic-table) .ant-pagination {
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
  margin: 0;
}

:deep(.vben-basic-table){
  height: calc(100% - 160px);
  margin-bottom: 20px;
}
</style>
