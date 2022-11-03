<template>
  <div class="app-container">
    <div class="app-container-top">
      <div class="app-container-head">
        <!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">-->
        <div class="container-head-title" style="float: left;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 40%;text-align: center;margin-top: 20px;">
          <b class="noneSpan" style="font-size: 26px;">余额调节表</b>
          <div style="font-size: 14px;text-align: center;margin-top: 20px;">
          <span style="color: black;font-size: 14px;">
            截止日期：{{ endTime }}
          </span>
          </div>
        </div>

        <div class="ant-btn-group" style="float: right">
          <a-button class="ant-btn ant-btn-me" @click="openQuery()"><span>查询</span></a-button>
          <a-button class="ant-btn ant-btn-me"><span>明细</span></a-button>
          <a-button class="ant-btn ant-btn-me" @click="excelData()"><span>导出</span></a-button>
          <a-button class="ant-btn ant-btn-me" @click="printData()"><span>打印</span></a-button>
          <a-button class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/home/welcome')">
            <span>退出</span></a-button>
        </div>
      </div>
      <div style="clear:both"/>
      <div>
        <div style="margin-top: -30px;margin-left: 90px;">
          <div style="display: inline-block;float: left;margin-top: -35px;">
            <AccountPicker readonly theme="three" @reloadTable="dynamicAdReload"/>
          </div>

          <div style="display: inline-block;float: left;margin-left: 5px;font-weight: bold;color: #666666;">
<!--            科目：
            <a-select v-model:value="kemu" @change="findYueTiaojie()" placeholder="请选择科目" allowClear
                      style="width: 200px;">
              <a-select-option v-for="item in codeKemu" :key="item.ccode" :value="item.ccode">
                {{ item.ccode }}-{{ item.ccodeName }}
              </a-select-option>
            </a-select>
            &emsp;-->
            本位币：{{ biZhong }}&emsp;
            <!--            截止日期：{{ endDate.format('YYYY-MM-DD') }}-->
            <!--            <a-date-picker
                          v-model:value="endDate"
                          placeholder="截止日期"
                          format="YYYY-MM-DD"
                          @change="findYueTiaojie()"
                        />-->
            <!--      &emsp;年度：<a-select v-model:value="year" @change="checkDate()" placeholder="请选择年度" style="width: 100px;">
                    <a-select-option v-for="item in yearList" :key="item.accountYear" :value="item.accountYear">
                      {{ item.accountYear }}
                    </a-select-option>
                  </a-select>-->
          </div>
        </div>

        <div class="app-container-neck">
          <div style="float: right; margin-left: 10px">
            <a-button class="ant-btn" @click="findYueTiaojie()">
              <SyncOutlined :style="{ fontSize: '14px' }"/>
            </a-button>
            <a-popover class="ant-btn-default" placement="bottom">
              <template #content>
                <a-popconfirm
                  placement="leftTop"
                  ok-text="保存"
                  cancel-text="关闭"
                  @confirm="confirm"
                  @cancel="cancel">
                  <template #icon><b>栏目设置</b><br></template>
                  <template #title>
                    <div style="width:650px">
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
            <!--        <a-popover placement="bottom">
                      <a-button>
                        <PicLeftOutlined :style="{ fontSize: '14px' }" />
                      </a-button>
                      <template #content>
                        <span @click="clickFlag('')">&emsp;全&emsp;部 &emsp;<CheckOutlined v-if="flag==''" /></span><br/>
                        <span @click="clickFlag('1')">&emsp;已对账 &emsp;<CheckOutlined v-if="flag=='1'" /></span><br/>
                        <span @click="clickFlag('0')">&emsp;未对账 &emsp;<CheckOutlined v-if="flag=='0'" /></span>
                      </template>
                      <template #title>
                        <b>对账状态</b>
                      </template>
                    </a-popover>-->

            <!--        <a-button>
                      <EditFilled :style="{ fontSize: '14px' }" />
                    </a-button>

                    <a-button>
                      <PieChartFilled :style="{ fontSize: '14px' }" />
                    </a-button>-->
<!--            <a-button>
              <FilterFilled :style="{ fontSize: '14px' }"/>
            </a-button>-->
          </div>
          <div style="float: right; position: relative">
            <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;"
                      class="special_select">
              <a-select-option style="font-size: 12px;" value="1">日期</a-select-option>
              <a-select-option style="font-size: 12px;" value="2">结算方式</a-select-option>
              <a-select-option style="font-size: 12px;" value="3">票号</a-select-option>
              <a-select-option style="font-size: 12px;" value="4">对方单位</a-select-option>
              <a-select-option style="font-size: 12px;" value="5">摘要</a-select-option>
            </a-select>
            <!-- 搜索 -->
            <a-input-search
              placeholder=""
              style="width: 200px; border-radius: 4px"
              @search="onSearch"
            />
          </div>
        </div>
      </div>
    </div>
    <div class="app-container-bottom">
      <BasicTable
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        :scroll="{ x: totalColumnWidth,y: windowHeight }"
        @register="registerTable"
        :dataSource="tableData"
      >
        <template #dayYue="{ record }"><span
          v-if="record.dayYue!=0">{{ toThousandFilter(record.dayYue) }}</span></template>
        <template #statementYue="{ record }"><span
          v-if="record.statementYue!=0">{{ toThousandFilter(record.statementYue) }}</span>
        </template>
        <template #flag="{ record }"></template>

      </BasicTable>
      <div class="pagination-text" v-show="showPaginationText">
        共 {{paginationNumber}} 条记录&nbsp;&nbsp; 每页 200 条
      </div>
      <Query
        @save="saveQuery"
        @register="registerQueryPage"
      />
    </div>
  </div>
</template>

<script setup="props, {emit}" lang="ts">
import {
  findByKemuAndDate,
  findCodeKemuByBr,
  findYearByAccount,
} from '/@/api/record/system/bank-statement'

import {BasicTable, useTable} from '/@/components/Table'
import {
  Input as AInput,
  Select as ASelect,
  Popover as APopover,
  Tabs as ATabs,
  DatePicker as ADatePicker,
  Button as AButton,
  Popconfirm as APopconfirm,
  Checkbox as ACheckbox,
  Radio as ARadio,
  Table as ATable,
  Drawer as ADrawer,
  message
} from "ant-design-vue";

const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
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
  SortAscendingOutlined,
  SortDescendingOutlined,
  ProfileOutlined
} from '@ant-design/icons-vue'
import {nextTick, onMounted, reactive, ref} from "vue";
import {initDynamics, assemblyDynamicColumn} from "./data";
import Query from './popup/query.vue'
import {useModal} from "/@/components/Modal";

const {closeCurrent} = useTabs(router);

const {
  createErrorModal
} = useMessage()

const formItems = ref({
  selectType: '1'
})

const kemu: any = ref('')
const year: any = ref('')
const endDate: any = ref('')
const endTime: any = ref('')
const flag = ref('0')
const dynamicTenantId = ref(getCurrentAccountName(true))

const CrudApi = {
  list: findYueTiaojie,
  columns: [
    {
      title: '科目编码',
      dataIndex: 'ccode',
      ellipsis: true
    },
    {
      title: '科目名称',
      dataIndex: 'ccodeName',
      ellipsis: true
    },
    {
      title: '币种',
      dataIndex: 'bizhong',
      ellipsis: true
    },
    {
      title: '启用期间',
      dataIndex: 'qiyongDate',
      ellipsis: true
    },
    {
      title: '对账截止日期',
      dataIndex: 'jiezhiDate',
      ellipsis: true
    },
    {
      title: '日记账账面余额',
      dataIndex: 'dayYue',
      ellipsis: true
    },
    {
      title: '对账单账面余额',
      dataIndex: 'statementYue',
      ellipsis: true
    },
    {
      title: '调整后余额',
      dataIndex: 'yue',
      ellipsis: true
    },
    {
      title: '平衡情况',
      dataIndex: 'flag',
      ellipsis: true
    },
  ]
}

const tableData:any = ref([]);
const tableDataAll:any = ref([]);
const showPaginationText = ref(false)
const paginationNumber = ref(0)
async function findYueTiaojie() {
  showPaginationText.value = false
  let len = 0
  const list: any = []
  if (endDate.value != null && endDate.value != '') {
    endTime.value = endDate.value
    const res = await useRouteApi(findAccountByYuetiaojie, {schemaName: dynamicTenantId})({endDate: endDate.value})
    const accList = res.filter(item => {
      if (kemu.value != null && kemu.value != '') {
        return item.ccode == kemu.value
      }
      return item
    })
    for (let i = 0; i < accList.length; i++) {
      const item: any = {}
      const acc = accList[i]
      item.ccode = acc.ccode
      item.ccodeName = acc.ccodeName
      item.bizhong = acc.foreignCurrency
      item.jiezhiDate = endDate.value
      const qichu = await useRouteApi(findByYearAndKemu, {schemaName: dynamicTenantId})({
        year: endDate.value,
        kemuCode: acc.ccode
      })
      const bankDuizhangList = await useRouteApi(findBankStatementByYuetiaojie, {schemaName: dynamicTenantId})({
        ccode: acc.ccode,
        endDate: endDate.value
      })
      let jieSum = 0;
      let daiSum = 0;
      bankDuizhangList.forEach(bank => {
        if (bank.jie != '' && bank.jie != null) {
          jieSum = add(jieSum, bank.jie)
        }
        if (bank.dai != '' && bank.dai != null) {
          daiSum = add(daiSum, bank.dai)
        }
      })
      let daySum = 0;
      let bankStatementSum = 0;
      if (qichu.length > 0) {
        if (item.bizhong == null || item.bizhong == '') {
          item.bizhong = qichu.currencyId
        }
        item.qiyongDate = endDate.value.substring(0,4) + '.' + qichu[0].qiyongDate
        let dayEndMoney = 0;
        let statementEndMoney = 0;
        if (qichu[0].dayEndMoney != null && qichu[0].dayEndMoney != '') {
          dayEndMoney = qichu[0].dayEndMoney
        }
        if (qichu[0].statementEndMoney != null && qichu[0].statementEndMoney != '') {
          statementEndMoney = qichu[0].statementEndMoney
        }
        // 日记账账面余额=调整后余额+借-贷（科目借方）
        daySum = add(dayEndMoney, sub(acc.md, acc.mc));
        if (qichu[0].fangxaing == '1') {
          // daySum = add(dayEndMoney, sub(acc.md, acc.mc));
          // 对账单账面余额=调整后余额+贷-借（对账单余额方向为借时方向）
          bankStatementSum = add(statementEndMoney, sub(daiSum,jieSum));
        } else {
          // daySum = add(dayEndMoney, sub(acc.mc, acc.md));
          // 对账单账面余额=调整后余额+借-贷（对账单余额方向为贷时方向）
          bankStatementSum = add(statementEndMoney, sub(jieSum,daiSum));
        }
      } else {
        item.qiyongDate = endDate.value.substring(0,4) + '.' + '01'
        if (acc.bprogerty == '1') {
          daySum = sub(acc.md, acc.mc)
          bankStatementSum = sub(jieSum, daiSum)
        } else {
          daySum = sub(acc.mc, acc.md)
          bankStatementSum = sub(daiSum, jieSum)
        }
      }
      item.dayYue = toThousandFilter(daySum)
      item.statementYue = toThousandFilter(bankStatementSum)
      if (daySum == bankStatementSum && daySum != 0) {
        item.yue = toThousandFilter(daySum)
        item.flag = '平衡'
      } else if (daySum != 0 || bankStatementSum != 0) {
        item.yue = toThousandFilter('')
        item.flag = '不平衡'
      } else {
        item.yue = toThousandFilter('')
        item.flag = '未知'
      }
      list.push(item)
    }
  }
  tableDataAll.value = list
  tableData.value = tableDataAll.value
  len = tableDataAll.value.length
  let num1 = tableDataAll.value.length%200
  if (num1<50) {
    let num = 50 - (tableDataAll.value.length % 50)
    for (let i = 0; i < num; i++) {
      tableData.value.push({})
    }
  }
  paginationNumber.value = len
  showPaginationText.value = true
  // setTableData([]) // 清空可能残留的数据
  // setTableData(list)
}

// 这是示例组件
const [registerTable, {
  reload,
  getDataSource,
  setTableData,
  setColumns,
  getColumns,
  getPaginationRef,
  setPagination
}] = useTable({
  // api: useRouteApi(CrudApi.list, {schemaName: dynamicTenantId}),
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: true,
  indexColumnProps:{ fixed:true },
  pagination: {
    pageSize: 200,
    // showSizeChanger: false,
    simple:true,
    showTotal: t => `共${t}条记录    每页200条`
  },
})

// function clickFlag(str) {
//   flag.value = str
//   reload({
//     searchInfo: {
//       kemuCode: kemu.value,
//       year: year.value,
//       flag: flag.value
//     }
//   })
// }

const codeKemu: any = ref({})

async function reloadCurrentPage() {
  const res = await useRouteApi(findCodeKemuByBr, {schemaName: dynamicTenantId})({})
  codeKemu.value = res.filter(item => {
    return item.bend == '1'
  })
  // console.log(codeKemu.value)
  // if (codeKemu.value.length>0) {
  //   kemu.value = codeKemu.value[0].ccode
  // }
}

const defaultAdName = ref(useCompanyOperateStoreWidthOut().getSchemaName)
const yearList: any = ref({})

async function reloadYear() {
  const res = await useRouteApi(findYearByAccount, {schemaName: dynamicTenantId})(defaultAdName.value)
  yearList.value = res
  // console.log(yearList.value)
  if (res.length > 0) {
    year.value = res[0].accountYear
  }
}

const defaultPage = ref(false)
onMounted(async () => {
  await reloadCurrentPage()
  await reloadYear()
  await reloadCurrency()
  openQueryPage(true, {
    data: {
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value,
      year: year.value,
      kemu: kemu.value,
      currency: currency.value,
      endDate: useCompanyOperateStoreWidthOut().getLoginDate,
      showRulesSize: pageParameter.showRulesSize,
      openOne: '1'
    }
  })

  // 进页面执行
  getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(res => {
    if (null != res && res.independent == 0) {
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
    resetDynamicColumnData()
  })
})

//选中内容
const state = reactive<{
  selectedRowKeys: [];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const checkRow = ref([])
const onSelectChange = (selectedRowKeys, row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
};

//获取币种
const biZhong = ref('')
const bzList:any = ref([])

async function reloadCurrency() {
  bzList.value = [];
  useCompanyOperateStoreWidthOut().getAccountList.forEach(item => {
    if (item.accId == defaultAdName.value && item.currency != '' && item.currency != null) {
      bzList.value = [{
        accountId: defaultAdName.value,
        foreignCode: item.currency,
        foreignName: item.currencyName
      }]
      biZhong.value = item.currencyName
    }
  })
  useRouteApi(currentCyDatas, {schemaName: dynamicTenantId})({accId: defaultAdName.value}).then(res => {
    bzList.value.push(...res.items)
  });
}

//查询功能
const [registerQueryPage, {openModal: openQueryPage}] = useModal()
const openQuery = () => {
  openQueryPage(true, {
    data: {
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value,
      year: year.value,
      kemu: kemu.value,
      currency: currency.value,
      endDate: endDate.value,
      showRulesSize: pageParameter.showRulesSize,
      openOne: '0'
    }
  })
}
const currency = ref("")

async function saveQuery(data) {
  dynamicTenantId.value = data.dynamicTenantId
  defaultAdName.value = data.defaultAdName
  year.value = data.year
  kemu.value = data.kemu
  currency.value = data.currency
  endDate.value = data.endDate
  pageParameter.showRulesSize = data.showRulesSize
  console.log(currency.value)
  console.log(biZhong.value)
  if (currency.value != null && currency.value != '') {
    bzList.value.forEach(item => {
      if (currency.value == item.foreignCode) {
        biZhong.value = item.foreignName
      }
    })
  }
  await findYueTiaojie()
}

function toThousandFilter(num: any) {
  if (num == '' || num == null) {
    return ''
  }
  return (+num || 0).toFixed(2).replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,')
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

function onSearch() {
}

//打印
import {useNewPrint} from "/@/utils/boozsoft/print/print";
import {tableStyle} from "/@/store/modules/abc-print";
import {
  defaultV,
  sheet_from_array_of_arrays,
  Workbook,
  writeExcel
} from "/@/utils/boozsoft/excel/excel2";
import XLSX from "xlsx-js-style";
const loadMark = ref(false)
function printData(){
  // console.log("打印成功！")
  loadMark.value = true
  let printList: any = []
  const arrData = tableData.value.filter(item=>item.ccode!=null && item.ccode!='')
  let kemu=''
  arrData.forEach(item=>{
    let item1 = {}
    item1[0] = item.ccode
    item1[1] = item.ccodeName
    item1[2] = item.qiyongDate
    item1[3] = item.jiezhiDate
    item1[4] = item.dayYue
    item1[5] = item.statementYue
    item1[6] = item.yue
    item1[7] = item.flag
    printList.push(item1)
  })
  let n = 27-printList.length%27
  for (let i=0; i<n; i++){
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
  let num = Math.ceil(printList.length/27)
  useNewPrint({data: ['l', 'px', 'a4', true]}, (doc) => {
    loadMark.value = false
    doc.autoTable({
      head: [['余额调节表','', '', '',  '', '', '', ''],
        ['核算单位：'+pageParameter.companyName, '', '截止日期:'+endTime.value, '', '', '', '', '币种：人民币'],
        ['科目编码', '科目名称', '启用日期', '对账截至日期', '日记账账面余额', '对账单账面余额', '调整后余额', '平衡情况']],
      body: printList,
      // startY: 60,
      styles: tableStyle(),
      margin: {
        left: 30,
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
        /*doc.autoTableText(
          '核算单位：'+pageParameter.companyName,
          tabMarginLeft,
          data.cursor.y + 3,
          0
        );*/
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
        // data.cell.styles.bold = false

        if (data.section == 'head' && data.row.index == 0) {

          data.cell.styles.fontSize = 20
          data.cell.styles.fontStyle = 'bold'
          data.cell.styles.lineColor = [255, 255, 255]
          if (data.column.index == 0) {
            data.cell.colSpan = 9
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
          } else if (data.column.index == 2) {
            data.cell.colSpan = 3
            data.cell.styles.cellPadding = {top: 2,left: 12, right: 2, bottom: 0}
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
            data.cell.styles.fillColor = [240, 240, 240]
          }
        }
      },
      columnStyles: {
        0: {maxHeight: 10, cellWidth: 50, halign: 'left'},
        1: {cellWidth: 130, halign: 'left'},
        2: {cellWidth: 60, halign: 'center'},
        3: {cellWidth: 60, halign: 'center'},
        4: {cellWidth: 70, halign: 'right'},
        5: {cellWidth: 70, halign: 'right'},
        6: {cellWidth: 70, halign: 'right'},
        7: {cellWidth: 50, halign: 'center'},
      }
    })
  })
}
//导出
const excelData = () => {
  // console.log("导出成功！")
  let list: any = []
  const arrData = tableData.value.filter(item=>item.ccode!=null && item.ccode!='')
  let kemu=''
  arrData.forEach(item=>{
    let item1 = {}
    item1[0] = item.ccode
    item1[1] = item.ccodeName
    item1[2] = item.qiyongDate
    item1[3] = item.jiezhiDate
    item1[4] = item.dayYue
    item1[5] = item.statementYue
    item1[6] = item.yue
    item1[7] = item.flag
    list.push(item1)
  })
  const sheet:any = [
    {
      title: '余额调节表',
      multiHeader: [
        ['核算单位：'+pageParameter.companyName, '', '截止日期:'+endTime.value, '', '', '', '币种：人民币', ''],
        ['科目编码', '科目名称', '启用期间', '对账截至日期', '日记账账面余额', '对账单账面余额', '调整后余额', '平衡情况']
      ],
      table: list,
      // foot: ['核算单位：' + pageParameter.companyName, '', '', '', '', '', '', ''],
      keys: [0, 1, 2, 3, 4, 5, 6, 7],
      merges: ['A2:B2', 'C2:E2', 'G2:H2'],
      sheetName: '余额调节表',
      cellStyle: [
        {
          cell: 'A1',
          font: {
            name: '宋体',
            sz: 14,
            color: {rgb: "000000"},
            bold: true,
          },
          border: {color: {rgb: "ffffff"}}
        },
      ],
      colWidth: [10, 30, 12, 12, 12, 12, 12, 12]
    }
  ]
  // 处理数据前
  if (!sheet || sheet.length <= 0) {
    this.onError('Table data cannot be empty')
    return
  }
  const wb:any = Workbook()
  sheet.forEach((item, index) => {
    let {
      // 标题
      title,
      // 表头
      tHeader,
      // 多级表头
      multiHeader,
      // 表格数据
      table,
      // 表格底部数据
      foot,
      // 合并项
      merges,
      // 数据键值
      keys,
      // 列宽
      colWidth,
      // 表名
      sheetName,
      // 全局样式
      globalStyle,
      // 单元格样式
      cellStyle
    } = item
    sheetName = sheetName || defaultV.sheetName
    // 默认全局样式覆盖
    const dgStyle = defaultV.globalStyle
    if (globalStyle) {
      Object.keys(dgStyle).forEach(key => {
        globalStyle[key] = {...dgStyle[key], ...globalStyle[key]}
      })
    } else {
      globalStyle = dgStyle
    }
    // 处理标题格式
    if (title || title === '') {
      // 取表头、多级表头中的最大值
      const tHeaderLength = tHeader && tHeader.length || 0
      const multiHeaderLength = multiHeader && Math.max(...multiHeader.map(m => m.length)) || 0
      const titleLength:any = Math.max(tHeaderLength, multiHeaderLength, keys.length)
      // 第一个元素为title，剩余以空字符串填充
      title = [title].concat(Array(titleLength - 1).fill(''))
      // 处理标题的合并\
      const cell = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']
      let mergeSecond = 'A1'
      if (titleLength > 26) {
        const one = parseInt(titleLength) / 26
        const two = titleLength % 26
        mergeSecond = cell[one - 1] + cell[two - 1] + '1'
      } else {
        mergeSecond = cell[titleLength - 1] + '1'
      }
      const titleMerge = `A1:${mergeSecond}`
      if (!merges) {
        merges = [titleMerge]
      } else {
        if (merges.indexOf(titleMerge) === -1) {
          merges.push(titleMerge)
        }
      }
    }
    //表头对应字段
    let data = table.map(v => keys.map(j => v[j]))
    // 多级表头
    if (multiHeader) {
      // 倒序循环
      for (let i = multiHeader.length - 1; i >= 0; i--) {
        data.unshift(multiHeader[i]);
      }
    }
    tHeader && data.unshift(tHeader);
    title && data.unshift(title);
    //表格底部对应字段
    /*if (foot || foot === '') {
      foot && data.push(foot);
      const str = ('A' + data.length) + (':H' + data.length)
      merges.push(str)
    }*/
    const ws = sheet_from_array_of_arrays(data,'');
    if (merges && merges.length > 0) {
      if (!ws['!merges']) ws['!merges'] = [];
      merges.forEach(merge => {
        ws['!merges'].push(XLSX.utils.decode_range(merge))
      })
    }
    // 如果没有列宽则自适应
    if (!colWidth) {
      // 基准比例，以12为标准
      const benchmarkRate = globalStyle.font.sz && globalStyle.font.sz / 12 || 1
      // 空字符长度
      const nullstr = 10 * benchmarkRate + 2
      // 单个中文字符长度
      const chinese = 2 * benchmarkRate
      // 单个非中文字符长度
      const nChinese = benchmarkRate
      //设置worksheet每列的最大宽度,并+2调整一点列宽
      const sheetColWidth = data.map(row => row.map(val => {
        //先判断是否为null/undefined
        if (!val) {
          return {
            'wch': nullstr
          };
        } else {
          const strArr = val.toString().split('')
          const pattern = new RegExp("[\u4E00-\u9FA5]+")
          let re = strArr.map(str => {
            // 是否为中文
            if (pattern.test(str)) {
              return chinese
            } else {
              return nChinese
            }
          })
          re = re.reduce((total, r) => total + r, 0)
          return {
            'wch': re + 2
          };
        }
      }))
      /*以第一行为初始值*/
      let result = sheetColWidth[0];
      for (let i = 1; i < sheetColWidth.length; i++) {
        for (let j = 0; j < sheetColWidth[i].length; j++) {
          if (result[j]['wch'] < sheetColWidth[i][j]['wch']) {
            result[j]['wch'] = sheetColWidth[i][j]['wch'];
          }
        }
      }
      ws['!cols'] = result;
    } else {
      ws['!cols'] = colWidth.map(i => {
        return {wch: i}
      })
    }

    // 添加工作表
    wb.SheetNames.push(sheetName);
    wb.Sheets[sheetName] = ws;
    let dataInfo = wb.Sheets[wb.SheetNames[index]];

    //全局样式
    (function () {
      Object.keys(dataInfo).forEach(i => {
        if (i == '!ref' || i == '!merges' || i == '!cols') {
        } else {
          // debugger
          dataInfo[i.toString()].s = globalStyle;
          const {border, font, alignment, fill} = globalStyle;
          if (i.substring(1) == '1') {
            dataInfo[i.toString()].s = {
              border: {},
              font: font || globalStyle.font,
              alignment: alignment || globalStyle.alignment,
              fill: fill || globalStyle.fill
            }
          } else if (i.substring(1) == '2') {
            dataInfo[i.toString()].s = {
              border: {},
              font: {
                name: '宋体',
                sz: 10,
                color: {rgb: "000000"},
                bold: true,
              },
              fill: fill || globalStyle.fill,
              alignment: {
                horizontal: "left",
              }
            }
            if (i == 'C2') {
              dataInfo[i.toString()].s = {
                border: {},
                font: {
                  name: '宋体',
                  sz: 10,
                  color: {rgb: "000000"},
                  bold: true,
                },
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "center",
                }
              }
            }
            if (i == 'G2') {
              dataInfo[i.toString()].s = {
                border: {},
                font: {
                  name: '宋体',
                  sz: 10,
                  color: {rgb: "000000"},
                  bold: true,
                },
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "right",
                }
              }
            }
          } else if (i.substring(1) == '3') {
            dataInfo[i.toString()].s = {
              border: border === {} ? border : border || globalStyle.border,
              font: {
                name: '宋体',
                sz: 10,
                color: {rgb: "000000"},
                bold: true,
              },
              alignment: alignment || globalStyle.alignment,
              fill: {fgColor: {rgb: "cccccc"}}
            }
          } else if (i.substring(0, 1) == 'A') {
            dataInfo[i.toString()].s = {
              border: border === {} ? border : border || globalStyle.border,
              font: font || globalStyle.font,
              fill: fill || globalStyle.fill,
              alignment: {
                horizontal: "left",
              }
            }
          } else if (i.substring(0, 1) == 'B') {
            dataInfo[i.toString()].s = {
              border: border === {} ? border : border || globalStyle.border,
              font: font || globalStyle.font,
              fill: fill || globalStyle.fill,
              alignment: {
                horizontal: "left",
              }
            }
          } else if (i.substring(0, 1) == 'E' || i.substring(0, 1) == 'F' || i.substring(0, 1) == 'G') {
            dataInfo[i.toString()].s = {
              border: border === {} ? border : border || globalStyle.border,
              font: font || globalStyle.font,
              fill: fill || globalStyle.fill,
              alignment: {
                horizontal: "right",
              }
            }
          }
          /*if (foot.length > 0) {
            if (i.substring(1) == data.length) {
              dataInfo[i.toString()].s = {
                border: {},
                font: {
                  name: '宋体',
                  sz: 10,
                  color: {rgb: "000000"},
                  bold: true,
                },
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "left",
                }
              }
            }
          }*/
        }
      });
    })();


    // 单个样式
    (function () {
      if (!cellStyle || cellStyle.length <= 0) {
        return
      }
      cellStyle.forEach(s => {
        const {border, font, alignment, fill} = s;
        dataInfo[s.cell].s = {
          border: border === {} ? border : border || globalStyle.border,
          font: font || globalStyle.font,
          alignment: alignment || globalStyle.alignment,
          fill: fill || globalStyle.fill
        }
      });
    })();
  })

  const bookType = 'xlsx'
  // 类型默认为xlsx
  writeExcel(wb, bookType, '余额调节表_'+pageParameter.companyName )
}

/**********************栏目设置**********************/
import {useMessage} from "/@/hooks/web/useMessage";
import {
  findDbLanMuList,
  saveLanMuList,
} from '/@/api/record/system/accvoucher';
import {
  getCurrentAccountName, getThisIndexImg
} from "/@/api/task-api/tast-bus-api";
import UnitChange from '../../../system/department/UnitChange.vue';
import {cloneDeep} from "lodash-es";
import {currentCyDatas, getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {findDataBase} from "/@/api/record/system/account";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {
  findAccountByYuetiaojie,
  findBankStatementByYuetiaojie
} from "/@/api/yu_e_tiaojie_biao/yu_e_tiaojie_biao";
import {findByYearAndKemu} from "/@/api/record/system/bank-statement-header";
import moment from "moment";

const {createConfirm, createWarningModal} = useMessage();
const pageParameter: any = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '北京希格科技',
  ifUnit: false,
})
const visible = ref(false);
const windowWidth = (document.documentElement.clientWidth - (70 + 280))
const windowHeight = (window.innerHeight - (290))
const totalColumnWidth = ref(0)
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData: any = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});
const tableRef = ref(null)
const lanMuData = {
  'accId': '',
  'menuName': '余额调节表',
  'type': '账套',
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
const cancel = (e: MouseEvent) => {
  // 恢复默认
  dynamicColumnData.value = []
  dynamicColumnData.value = dynamicColumnDataCopy
}
const edit = (key: string) => {
  if (key.toString().indexOf('-') != -1) {
    let arr: any = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      editableData[key] = cloneDeep(dynamicColumnData.value[one].children.filter(item => key === item.key)[0]);
    } else {
      let two = parseInt(arr[1]) - 1
      editableData[key] = cloneDeep(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0]);
    }
  } else {
    editableData[key] = cloneDeep(dynamicColumnData.value.filter(item => key === item.key)[0]);
  }
}
const save = (key: string, min: number, max: number) => {
  editableData[key].width = editableData[key].width > max ? max : editableData[key].width < min ? min : editableData[key].width
  if (key.toString().indexOf('-') != -1) {
    let arr: any = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
    } else {
      let two = parseInt(arr[1]) - 1
      Object.assign(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0], editableData[key]);
    }
  } else {
    Object.assign(dynamicColumnData.value.filter(item => key === item.key)[0], editableData[key]);
  }
  delete editableData[key];
}
const saveName = (key: string) => {
  if (key.toString().indexOf('-') != -1) {
    let arr: any = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
    } else {
      let two = parseInt(arr[1]) - 1
      Object.assign(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0], editableData[key]);
    }
  } else {
    Object.assign(dynamicColumnData.value.filter(item => key === item.key)[0], editableData[key]);
  }
  delete editableData[key];
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
    } catch (e: any) {
      if (e.message == 'ok') {
        return true
      } else {
        return false
      }
    }
  })
  return a;
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
  let a: any = []
  a = getColumns()
  let newA = JSON.parse(JSON.stringify(CrudApi.columns))
  newA = assemblyDynamicColumn(dynamicColumnData.value, newA)
  setColumns(newA)
  initTableWidth(newA)
  //searchConditonList.value = newA
}

function resetDynamicColumnData() {
  // 先从数据查询是否已经设置
  lanMuData.accId = getCurrentAccountName(false)
  findDbLanMuList(lanMuData).then(res => {
    // 栏目列
    let dbList = res.items
    if (dbList.length > 0) {
      let statiList = initDynamics()['DATA']
      dbList = combineParameters(statiList, dbList)
      dynamicColumnData.value = dbList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(dbList))
    } else {
      let statiList = initDynamics()['DATA']
      dynamicColumnData.value = statiList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(statiList))
    }
    // 表格列
    reloadColumns()
    //pageReload()
  })
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
    // tableRef.value.$el.style.setProperty('width',(windowWidth+20-f)+'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    // tableRef.value.$el.style.setProperty('width',(total+20)+'px')
  }
}

const pageReload = () => {
  setTableData([]) // 清空可能残留的数据
  reload({
    searchInfo: pageParameter
  })
}
/*栏目设置end*/

const dynamicAdReload = async (obj) => {
  // const dataBase:any = await findDataBase(obj.accId,obj.year)
  dynamicTenantId.value = obj.accountMode
  defaultAdName.value = obj.accId
  await reloadCurrentPage()
  await reloadYear()
  await reloadCurrency()
  await findYueTiaojie()
  // let data: any = {}
  // data.page = getPaginationRef().current
  // data.size = getPaginationRef().pageSize
  // data.kemuCode = kemu.value
  // data.year = year.value
  // data.flag = flag.value
  // let res: any = await useRouteApi(CrudApi.list, {schemaName: dataBase.accountMode})(data)
  // setTableData([]) // 清空可能残留的数据
  // setTableData(res.items)
  // // 底部分页信息
  // pageParameter.thisAdInfo = {}
  // pageParameter.thisAdInfo = obj
  // pageParameter.total = res.total
  // setPagination({total: res.total})
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

:deep(.ant-card-body) {
  padding: 16px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
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

.app-container {
  background-color: #f2f2f2;
  padding: 0px;
  margin: 10px 10px 0;
  position: relative;
  :deep(.pagination-text){
    position: absolute;
    bottom: 6px;
    right: 200px;
    font-size: 13px;
    color: black;
    z-index: 99999999;
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
  min-height: 500px;
}

:deep(.vben-basic-table) .ant-pagination {
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
  margin: 0;
}

:deep(.vben-basic-table){
  min-height: 500px;
  height: calc(100% - 160px);
  margin-bottom: 20px;
}

:deep(.ant-input),:deep(.ant-select),:deep(.ant-btn){
  border: 1px solid #c9c9c9;
}

:deep(.nc-summary) {
  font-size: 14px;
  font-weight: bold;
  width: 100%;
  background-color: #cccccc;
  border:none !important;
}
</style>
