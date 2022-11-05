<template>
  <div>
    <div class="app-container">

      <div class="app-container-head">
        <!--      <img src="/img/menu/index_dept.png" style="float: left;">-->
        <div class="container-head-title" style="float: left;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 40%;text-align: center;margin-top: 20px;">
          <b class="noneSpan" style="font-size: 26px;">银行对账及勾对</b>
          <div style="font-size: 14px;text-align: center;margin-top: 20px;">
          <span style="color: black;font-size: 14px;">
            日期：{{ startDate }}~{{ endDate }}
          </span>
          </div>
        </div>

        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="openQuery()"
          ><span>查询</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
          ><span>导入对账单</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
          ><span>自动匹配</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
          ><span>取消对账</span></button>
        </div>
      </div>
      <div style="clear:none"/>
      <div style="margin-top: -30px;margin-left: 90px;">
        <div style="display: inline-block;float: left;margin-top: -30px;">
          <AccountPicker readonly theme="three" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="display: inline-block;float: left;margin-left: 5px;font-weight: bold;">
          科目：
          <a-select v-model:value="kemu" @change="checkDate()" style="width: 240px;"
                    placeholder="请选择科目">
            <a-select-option v-for="item in codeKemu" :key="item.ccode" :value="item.ccode">
              {{ item.ccode }}-{{ item.ccodeName }}
            </a-select-option>
          </a-select>
          <span style="font-size: 14px;">&emsp;&emsp;状态：{{
              flag == '' ? '全部' : ''
            }}{{ flag == '0' ? '未对账' : '' }}{{ flag == '1' ? '已对账' : '' }}</span>
          <!--        <a-select v-model:value="year" @change="checkDate()" class="head-index-select" placeholder="请选择年度">
                    <a-select-option v-for="item in yearList" :key="item.accountYear" :value="item.accountYear">
                      {{ item.accountYear }}
                    </a-select-option>
                  </a-select>-->
          <!--        <a-select v-model:value="flag" @change="checkDate()" class="head-index-select" placeholder="请选择对账状态">
                    <a-select-option value="0">未对账</a-select-option>
                    <a-select-option value="1">已对账</a-select-option>
                    <a-select-option value="">全部</a-select-option>
                  </a-select>-->
        </div>
      </div>

      <div style="float: right; margin-left: 10px">
        <a-button class="ant-btn-me">
          <SyncOutlined :style="{ fontSize: '14px' }"/>
        </a-button>
        <a-popover class="ant-btn-default" placement="bottom">
          <template #content>
            <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                  :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
              v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
            <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                  :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
              v-if="pageParameter.showRulesSize==='MIN'"/></span>
          </template>
          <a-button>
            <SettingFilled :style="{ fontSize: '14px' }"/>
          </a-button>
        </a-popover>
        <a-popover class="ant-btn-default" placement="bottom">
          <a-button>
            <PicLeftOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <template #content>
              <span class="group-btn-span-special2" @click="clickFlag('')"
                    :style="flag==''?{backgroundColor: '#0096c7',color: 'white'}:''">&emsp;全&emsp;部 &emsp;<CheckOutlined
                v-if="flag==''"/></span><br/>
            <span class="group-btn-span-special2" @click="clickFlag('1')"
                  :style="flag=='1'?{backgroundColor: '#0096c7',color: 'white'}:''">&emsp;已对账 &emsp;<CheckOutlined
              v-if="flag=='1'"/></span><br/>
            <span class="group-btn-span-special2" @click="clickFlag('0')"
                  :style="flag=='0'?{backgroundColor: '#0096c7',color: 'white'}:''">&emsp;未对账 &emsp;<CheckOutlined
              v-if="flag=='0'"/></span>
          </template>
          <!--          <template #title>
                      <b>对账状态</b>
                    </template>-->
        </a-popover>
      </div>
      <div style="clear:both"/>

    </div>
    <div class="app-container" style="height: 330px;">
      <div style="text-align: center;">
        <span style="font-size: 24px;">单位日记账</span>
        <BasicTable
          class="a1 table1"
          :row-selection="{ type: 'checkbox', selectedRowKeys: selectedRowKeys1,getCheckboxProps:rowSelection1.getCheckboxProps, onSelect: condClick1 }"
          @selection-change="selectionChange1"
          @row-click="condClick1"
          @register="registerDayTable"
          :dataSource="tableData1"
          :scroll="{ y: 230 }"
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        >
          <template #md="{ record }"><span v-if="record.md!=0">{{
              toThousandFilter(record.md)
            }}</span></template>
          <template #mc="{ record }"><span v-if="record.mc!=0">{{
              toThousandFilter(record.mc)
            }}</span></template>
          <template #statementNum="{ record }">{{ record.statementNum }}</template>
        </BasicTable>
        <div class="pagination-text" v-show="showPaginationText1">
          共 {{paginationNumber1}} 条记录&nbsp;&nbsp; 每页 200 条
        </div>
      </div>

      <Query
        @save="saveQuery"
        @register="registerQueryPage"
      />

    </div>

    <div class="app-container" style="height: 330px;">
      <div style="text-align: center;">
        <span style="font-size: 24px;">银行对账单</span>
        <BasicTable
          class="a1 table1"
          :row-selection="{ type: 'checkbox', selectedRowKeys: selectedRowKeys,getCheckboxProps:rowSelection1.getCheckboxProps,onSelect: condClick }"
          @selection-change="selectionChange"
          @row-click="condClick"
          @register="registerTable"
          :dataSource="tableData"
          :scroll="{ y: 230 }"
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        >
          <template #jie="{ record }"><span v-if="record.jie!=0">{{
              toThousandFilter(record.jie)
            }}</span></template>
          <template #dai="{ record }"><span v-if="record.dai!=0">{{
              toThousandFilter(record.dai)
            }}</span></template>
          <template #flag="{ record }"> {{ record.flag === '1' ? '已对账' : '' }}</template>
          <template #statementNum="{ record }">{{ record.statementNum }}</template>
          <template #ccusId="{ record }">{{ record.ccusId }}{{ record.csupId }}</template>
        </BasicTable>
        <div class="pagination-text" v-show="showPaginationText">
          共 {{paginationNumber}} 条记录&nbsp;&nbsp; 每页 200 条
        </div>
      </div>
    </div>
  </div>
</template>

<script setup="props, {emit}" lang="ts">
import {
  findAccountByKemuAndDate,
  findByKemuAndDate,
  findCodeKemuByBr,
  findYearByAccount,
  saveBankStatementByDuizhang,
} from '/@/api/record/system/bank-statement'

import {BasicTable, useTable} from '/@/components/Table'
import {Select as ASelect, Popover as APopover, Input as AInput} from 'ant-design-vue'

const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
import {useModal} from '/@/components/Modal'
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
  SortDescendingOutlined,
  SortAscendingOutlined,
  ProfileOutlined
} from '@ant-design/icons-vue'
import {nextTick, onMounted, reactive, ref} from "vue";
import {CrudApi, CrudDayApi} from "./data";
import {useMessage} from "/@/hooks/web/useMessage";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import Query from './popup/query.vue'
import {getCurrentAccountName, hasBlank} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";

const {
  createErrorModal,
  createConfirm
} = useMessage()
const kemu: any = ref('')
const year: any = ref('')
const flag: any = ref('0')

const dynamicTenantId = ref(getCurrentAccountName(true))
const defaultAdName = ref(useCompanyOperateStoreWidthOut().getSchemaName)
// 这是示例组件
const [registerTable, {reload, getDataSource, getColumns, setTableData,deleteSelectRowByKey}] = useTable({
  /*api: async (params) => {
    return useRouteApi(findByKemuAndDate, {schemaName: dynamicTenantId})({
      kemuCode: kemu.value,
      year: year.value,
      flag: flag.value
    })
  },*/
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: true,
  pagination: {
    pageSize: 200,
    // showSizeChanger: false,
    simple:true,
    showTotal: t => `共${t}条记录    每页200条`
  },
  searchInfo: {
    kemuCode: kemu.value,
    year: year.value,
    flag: flag.value
  }
})

// 这是示例组件
const [registerDayTable, {
  reload: reload1,
  getDataSource: getDataSource1,
  getColumns: getColumns1,
  setTableData: setTableData1,
  deleteSelectRowByKey:deleteSelectRowByKey1
}] = useTable({
  /*api: async (params) => {
    return useRouteApi(findAccountByKemuAndDate, {schemaName: dynamicTenantId})({
      kemuCode: kemu.value,
      year: year.value,
      flag: flag.value
    })
  },*/
  columns: CrudDayApi.columns,
  bordered: true,
  showIndexColumn: true,
  pagination: {
    pageSize: 200,
    // showSizeChanger: false,
    simple:true,
    showTotal: t => `共${t}条记录    每页200条`
  },
  searchInfo: {
    kemuCode: kemu.value,
    year: year.value,
    flag: flag.value
  }
})

const tableData:any = ref([]);
const tableDataAll:any = ref([]);
const showPaginationText = ref(false)
const paginationNumber = ref(0)
async function reloadTable(){
  showPaginationText.value = false
  let len = 0
  const res = await useRouteApi(findByKemuAndDate, {schemaName: dynamicTenantId})({
    kemuCode: kemu.value,
    year: year.value,
    flag: flag.value
  })
  tableDataAll.value = res.items
  tableData.value = tableDataAll.value
  let num1 = tableDataAll.value.length%200
  if (num1<20) {
    let num = 20 - (tableDataAll.value.length % 20)
    for (let i = 0; i < num; i++) {
      tableData.value.push({})
    }
  }
  len = tableDataAll.value.length
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
  const res = await useRouteApi(findAccountByKemuAndDate, {schemaName: dynamicTenantId})({
    kemuCode: kemu.value,
    year: year.value,
    flag: flag.value
  })
  tableDataAll1.value = res.items
  tableData1.value = tableDataAll1.value
  let num1 = tableDataAll1.value.length%200
  if (num1<20) {
    let num = 20 - (tableDataAll1.value.length % 20)
    for (let i = 0; i < num; i++) {
      tableData1.value.push({})
    }
  }
  len = tableDataAll1.value.length
  paginationNumber1.value = len
  showPaginationText1.value = true
}

const codeKemu: any = ref({

})

async function reloadCurrentPage() {
  const res = await useRouteApi(findCodeKemuByBr, {schemaName: dynamicTenantId})({})
  codeKemu.value = res.filter(item => item.bend == '1')
  // console.log(codeKemu.value)
  /*if (codeKemu.value.length > 0) {
    kemu.value = codeKemu.value[0].ccode
  }*/
}

const yearList: any = ref({})

async function reloadYear() {
  const res = await findYearByAccount(defaultAdName.value)
  yearList.value = res
  // console.log(yearList.value)
  if (res.length > 0) {
    year.value = res[0].accountYear
  }
}

const selections = ref([])
onMounted(async () => {
  await reloadCurrentPage()
  await reloadYear()
  await checkDate()
  statementNum.value = formatStatementNum()
  openQueryPage(true, {
    data: {
      openOne: '1'
    }
  })
})
const selectedRowKeys: any = ref([])
const rowSelection = {
  getCheckboxProps: (record) => ({
    disabled:  record.id==undefined
  }),
};
const rowSelection1 = {
  getCheckboxProps: (record) => ({
    disabled:  record.id==undefined
  }),
};
const editAndDelBtnShow = ref(false);
function selectionChange(a) {
  editAndDelBtnShow.value=a.rows.filter(aa=>aa.flag=='1').length>0?true:false
}
const editAndDelBtnShow1 = ref(false);
function selectionChange1(a) {
  editAndDelBtnShow1.value=a.rows.filter(aa=>aa.flag=='1').length>0?true:false
}
const checkRow: any = ref([])
const checkRow1: any = ref([])
//选择银行对账单
const condClick = (data: any) => {
  if(hasBlank(data.id)){
    deleteSelectRowByKey1(data.key)
  }
  let flag = true
  //选择
  if (!hasBlank(data.id)) {
    if (selectedRowKeys.value.indexOf(data.key) == -1) {
      if (checkRow.value.length == 0 && checkRow1.value.length == 0) {
        //第一次选择时需要排序
        let list: any = []
        list = getDataSource1()
        //方向为借
        if (data.jie != '' && data.jie != null && data.jie > 0) {
          const list1 = list.filter(item => item.md > 0)
          const list2 = list.filter(item => item.mc > 0)
          list = list1.concat(list2)
        }
        //方向为贷
        if (data.dai != '' && data.dai != null && data.dai > 0) {
          const list1 = list.filter(item => item.md > 0)
          const list2 = list.filter(item => item.mc > 0)
          list = list2.concat(list1)
        }
        //对方单位排序
        if (data.duifangUnit != null) {
          const list1 = list.filter(item => item.ccusId == data.duifangUnit || item.csupId == data.duifangUnit)
          const list2 = list.filter(item => item.ccusId != data.duifangUnit && item.csupId != data.duifangUnit)
          list = list2.concat(list1)
        }
        //票号排序
        if (data.piaohao != null) {
          const list1 = list.filter(item => item.pjId == data.piaohao)
          const list2 = list.filter(item => item.pjId != data.piaohao)
          list = list2.concat(list1)
        }
        setTableData1(list)
        /*reload1({
        searchInfo: {
          kemuCode: kemu.value,
          year: year.value,
          flag: '0',
          params: {
            jie: data.jie,
            dai: data.dai,
            duifangUnit: data.duifangUnit,
            piaohao: data.piaohao
          }
        }
      })*/
      }
      //判断对方单位
      let duifangUnit = ''
      //结算方式
      let settModes = ''
      //票号
      let piaohao = ''
      let sum = 0
      checkRow1.value.forEach(
        function (item) {
          if (item.md != null && item.md != '') {
            sum = sum + parseFloat(item.md)
          }
          if (item.mc != null && item.mc != '') {
            sum = sum - parseFloat(item.mc)
          }
          if ((item.ccusId != null && item.ccusId != '') || (item.csupId != null && item.csupId != '')) {
            if (item.ccusId == null || item.ccusId == '') {
              duifangUnit = item.csupId
            } else {
              duifangUnit = item.ccusId
            }
          }
          if (item.pjCsettle != '' && item.pjCsettle != null) {
            settModes = item.pjCsettle
          }
          if (item.pjId != '' && item.pjId != null) {
            piaohao = item.pjId
          }
        }
      )
      checkRow.value.forEach(
        function (item) {
          if (item.jie != null && item.jie != '') {
            sum = sum + parseFloat(item.jie)
          }
          if (item.dai != null && item.dai != '') {
            sum = sum - parseFloat(item.dai)
          }
          if (item.duifangUnit != '' && item.duifangUnit != null) {
            duifangUnit = item.duifangUnit
          }
          if (item.settModes != '' && item.settModes != null) {
            settModes = item.settModes
          }
          if (item.piaohao != '' && item.piaohao != null) {
            piaohao = item.piaohao
          }
        }
      )
      if (sum < 0 && parseFloat(data.jie) > 0) {
        createErrorModal({
          iconType: 'warning',
          title: '对账',
          content: '金额方向不一致，选择错误，请重新选择',
        })
        flag = false
        return false
      } else if (sum > 0 && parseFloat(data.dai) > 0) {
        createErrorModal({
          iconType: 'warning',
          title: '对账',
          content: '金额方向不一致，选择错误，请重新选择',
        })
        flag = false
        return false
      }
      //判断对方单位
      if (data.duifangUnit != null && data.duifangUnit != '') {
        if (duifangUnit == '') {
          duifangUnit = data.duifangUnit
        } else if (duifangUnit != data.duifangUnit) {
          //提示对方单位名称不一致，是否继续进行两清
          createConfirm({
            iconType: 'warning',
            title: '对账',
            content: '对方单位名称不一致，是否继续进行两清',
            onOk: async () => {
              selectedRowKeys.value.push(data.key)
              checkRow.value.push(data)
              data.statementNum = statementNum.value
              countJieDai()
            },
            onCancel: () => {
              return false
            }
          })
          flag = false
        }
      }
      //判断结算方式
      if (data.settModes != null && data.settModes != '') {
        if (settModes == '') {
          settModes = data.settModes
        } else if (settModes != data.settModes) {
          //提示结算方式不一致，是否继续进行两清
          createConfirm({
            iconType: 'warning',
            title: '对账',
            content: '结算方式不一致，是否继续进行两清',
            onOk: async () => {
              selectedRowKeys.value.push(data.key)
              checkRow.value.push(data)
              data.statementNum = statementNum.value
              countJieDai()
            },
            onCancel: () => {
              return false
            }
          })
          flag = false
        }
      }
      //判断票号
      if (data.piaohao != null && data.piaohao != '') {
        if (piaohao == '') {
          piaohao = data.piaohao
        } else if (piaohao != data.piaohao) {
          //提示票号内容不一致，是否继续进行两清
          createConfirm({
            iconType: 'warning',
            title: '对账',
            content: '票号内容不一致，是否继续进行两清',
            onOk: async () => {
              selectedRowKeys.value.push(data.key)
              checkRow.value.push(data)
              data.statementNum = statementNum.value
              countJieDai()
            },
            onCancel: () => {
              return false
            }
          })
          flag = false
        }
      }
      if (flag) {
        selectedRowKeys.value.push(data.key)
        checkRow.value.push(data)
        data.statementNum = statementNum.value
        countJieDai()
      }
    } else {//取消选择
      selectedRowKeys.value.splice(selectedRowKeys.value.indexOf(data.key), selectedRowKeys.value.indexOf(data.key) + 1)
      checkRow.value.splice(checkRow.value.indexOf(data), checkRow.value.indexOf(data) + 1)
      data.statementNum = ''
      countJieDai()
    }
  }
  // countJieDai()
}
const selectedRowKeys1: any = ref([])
//选择银行日记账
const condClick1 = (data: any) => {
  if(hasBlank(data.id)){
    deleteSelectRowByKey1(data.key)
  }
  let flag = true
  //选择
  if (!hasBlank(data.id)) {
    if (selectedRowKeys1.value.indexOf(data.key) == -1) {
      //选择
      if (checkRow.value.length == 0 && checkRow1.value.length == 0) {
        let duifang = ''
        if (duifang == '') {
          if (data.ccusId == null || data.ccusId == '') {
            duifang = data.csupId
          } else {
            duifang = data.ccusId
          }
        }
        //第一次选择时需要排序
        let list: any = []
        list = getDataSource()
        //方向为借
        //方向为借
        if (data.md != '' && data.md != null && data.md > 0) {
          const list1 = list.filter(item => item.jie > 0)
          const list2 = list.filter(item => item.dai > 0)
          list = list1.concat(list2)
        }
        //方向为贷
        if (data.mc != '' && data.mc != null && data.mc > 0) {
          const list1 = list.filter(item => item.jie > 0)
          const list2 = list.filter(item => item.dai > 0)
          list = list2.concat(list1)
        }
        //对方单位排序
        if (duifang != null) {
          const list1 = list.filter(item => item.duifangUnit == duifang)
          const list2 = list.filter(item => item.duifangUnit != duifang)
          list = list2.concat(list1)
        }
        //票号排序
        if (data.pjId != null) {
          const list1 = list.filter(item => item.piaohao == data.pjId)
          const list2 = list.filter(item => item.piaohao != data.pjId)
          list = list2.concat(list1)
        }
        setTableData(list)
        /*reload({
        searchInfo: {
          kemuCode: kemu.value,
          year: year.value,
          flag: '0',
          params: {
            jie: data.md,
            dai: data.mc,
            duifangUnit: duifang,
            piaohao: data.pjId
          }
        }
      })*/
      }
      //判断对方单位
      let duifangUnit = ''
      //结算方式
      let settModes = ''
      //票号
      let piaohao = ''
      let sum = 0
      checkRow1.value.forEach(
        function (item) {
          if (item.md != null && item.md != '') {
            sum = sum + parseFloat(item.md)
          }
          if (item.mc != null && item.mc != '') {
            sum = sum - parseFloat(item.mc)
          }
          if ((item.ccusId != null && item.ccusId != '') || (item.csupId != null && item.csupId != '')) {
            if (item.ccusId == null || item.ccusId == '') {
              duifangUnit = item.csupId
            } else {
              duifangUnit = item.ccusId
            }
          }
          if (item.pjCsettle != '' && item.pjCsettle != null) {
            settModes = item.pjCsettle
          }
          if (item.pjId != '' && item.pjId != null) {
            piaohao = item.pjId
          }
        }
      )
      checkRow.value.forEach(
        function (item) {
          if (item.jie != null && item.jie != '') {
            sum = sum + parseFloat(item.jie)
          }
          if (item.dai != null && item.dai != '') {
            sum = sum - parseFloat(item.dai)
          }
          if (item.duifangUnit != '' && item.duifangUnit != null) {
            duifangUnit = item.duifangUnit
          }
          if (item.settModes != '' && item.settModes != null) {
            settModes = item.settModes
          }
          if (item.piaohao != '' && item.piaohao != null) {
            piaohao = item.piaohao
          }
        }
      )
      if (sum < 0 && parseFloat(data.md) > 0) {
        createErrorModal({
          iconType: 'warning',
          title: '对账',
          content: '金额方向不一致，选择错误，请重新选择',
        })
        flag = false
        return false
      } else if (sum > 0 && parseFloat(data.mc) > 0) {
        createErrorModal({
          iconType: 'warning',
          title: '对账',
          content: '金额方向不一致，选择错误，请重新选择',
        })
        flag = false
        return false
      }
      //判断对方单位
      if ((data.ccusId != null && data.ccusId != '') || (data.csupId != null && data.csupId != '')) {
        if (duifangUnit == '') {
          if (data.ccusId == null || data.ccusId == '') {
            duifangUnit = data.csupId
          } else {
            duifangUnit = data.ccusId
          }
        } else if (duifangUnit != data.ccusId || duifangUnit != data.csupId) {
          //提示对方单位名称不一致，是否继续进行两清
          createConfirm({
            iconType: 'warning',
            title: '对账',
            content: '对方单位名称不一致，是否继续进行两清',
            onOk: async () => {
              selectedRowKeys1.value.push(data.key)
              checkRow1.value.push(data)
              data.statementNum = statementNum.value
              countJieDai()
            },
            onCancel: () => {
              return false
            }
          })
          flag = false
        }
      }
      //判断结算方式
      if (data.pjCsettle != null && data.pjCsettle != '') {
        if (settModes == '') {
          settModes = data.pjCsettle
        } else if (settModes != data.pjCsettle) {
          //提示结算方式不一致，是否继续进行两清
          createConfirm({
            iconType: 'warning',
            title: '对账',
            content: '结算方式不一致，是否继续进行两清',
            onOk: async () => {
              selectedRowKeys1.value.push(data.key)
              checkRow1.value.push(data)
              data.statementNum = statementNum.value
              countJieDai()
            },
            onCancel: () => {
              return false
            }
          })
          flag = false
        }
      }
      //判断票号
      if (data.pjId != null && data.pjId != '') {
        if (piaohao == '') {
          piaohao = data.pjId
        } else if (piaohao != data.pjId) {
          //提示票号内容不一致，是否继续进行两清
          createConfirm({
            iconType: 'warning',
            title: '对账',
            content: '票号内容不一致，是否继续进行两清',
            onOk: async () => {
              selectedRowKeys1.value.push(data.key)
              checkRow1.value.push(data)
              data.statementNum = statementNum.value
              countJieDai()
            },
            onCancel: () => {
              return false
            }
          })
          flag = false
        }
      }
      if (flag) {
        selectedRowKeys1.value.push(data.key)
        checkRow1.value.push(data)
        data.statementNum = statementNum.value
        countJieDai()
      }
    } else {//取消选择
      selectedRowKeys1.value.splice(selectedRowKeys1.value.indexOf(data.key), selectedRowKeys1.value.indexOf(data.key) + 1)
      checkRow1.value.splice(checkRow1.value.indexOf(data), checkRow1.value.indexOf(data) + 1)
      data.statementNum = ''
      countJieDai()
    }
  }
  // countJieDai()
}

//生成标识号
const statementNum = ref()

function formatStatementNum() {
  let d = new Date();
  let year: any = d.getFullYear();
  let month: any = d.getMonth() + 1;
  let date: any = d.getDate();
  let hours: any = d.getHours();
  let minutes: any = d.getMinutes();
  let seconds: any = d.getSeconds();
  if (month <= 9)
    month = "0" + month;
  if (date <= 9)
    date = "0" + date;
  if (hours <= 9)
    hours = "0" + hours;
  if (minutes <= 9)
    minutes = "0" + minutes;
  if (seconds <= 9)
    seconds = "0" + seconds;
  let str = '' + year + month + date + hours + minutes + seconds;
  return str
}

//金额相符，是否两清？
function countJieDai() {
  let jie = 0
  let dai = 0
  let md = 0//凭证借
  let mc = 0//凭证贷
  checkRow.value.forEach(
    function (item) {
      if (item.jie != '' && item.jie != null) {
        jie = add(jie, item.jie)
      }
      if (item.dai != '' && item.dai != null) {
        dai = add(dai, item.dai)
      }
    }
  )
  checkRow1.value.forEach(
    function (item) {
      if (item.md != '' && item.md != null) {
        md = add(md, item.md)
      }
      if (item.mc != '' && item.mc != null) {
        mc = add(mc, item.mc)
      }
    }
  )
  console.log(jie + "======" + md)
  console.log(dai + "======" + mc)
  if (jie == md && dai == mc && checkRow.value.length > 0 && checkRow1.value.length > 0) {
    createConfirm({
      iconType: 'warning',
      title: '对账',
      content: '金额相符，是否两清？',
      onOk: async () => {
        //两清方法
        useRouteApi(saveBankStatementByDuizhang, {schemaName: dynamicTenantId})({
          bankStatement: checkRow.value,
          accvoucher: checkRow1.value
        })
        await checkDate()
        statementNum.value = formatStatementNum()
        selectedRowKeys.value = []
        checkRow.value = []
        selectedRowKeys1.value = []
        checkRow1.value = []
      },
      onCancel: () => {
        console.log("已取消")
        checkDate()
        statementNum.value = formatStatementNum()
        selectedRowKeys.value = []
        checkRow.value = []
        selectedRowKeys1.value = []
        checkRow1.value = []
      }
    })
  }
}

async function checkDate() {
  await reloadTable()
  await reloadTable1()
  /*await reload(
    {
      searchInfo: {
        kemuCode: kemu.value,
        year: year.value,
        flag: flag.value
      }
    }
  )
  await reload1(
    {
      searchInfo: {
        kemuCode: kemu.value,
        year: year.value,
        flag: flag.value
      }
    }
  )*/
}

function clickFlag(str) {
  flag.value = str
  checkDate()
}

function toThousandFilter(num: any) {
  if (num == '' || num == null) {
    return ''
  }
  return (+num || 0).toFixed(2).replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,')
}

function onSearch() {
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

// function abcFun(a,b,c,d){
//   if(abc.value.indexOf(a.key)==-1){
//     abc.value.push(a.key)
//   }else{
//     abc.value.splice(abc.value.indexOf(a.key),abc.value.indexOf(a.key)+1)
//   }
// }

//查询功能
const [registerQueryPage, {openModal: openQueryPage}] = useModal()
const openQuery = () => {
  openQueryPage(true, {
    data: {
      openOne: '0'
    }
  })
}
const currency = ref("")
const startDate = ref('')
const endDate = ref('')
async function saveQuery(data) {
  dynamicTenantId.value = data.dynamicTenantId
  defaultAdName.value = data.defaultAdName
  year.value = data.year
  kemu.value = data.kemu
  startDate.value = data.ddate1
  endDate.value = data.ddate2
  await reloadCurrentPage()
  await reloadYear()
  await checkDate()
  statementNum.value = formatStatementNum()
}

const pageParameter: any = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '北京希格科技',
  ifUnit: false,
})

const dynamicAdReload = async (obj) => {
  dynamicTenantId.value = obj.accountMode
  defaultAdName.value = obj.accId
  year.value = obj.year
  await reloadCurrentPage()
  await reloadYear()
  await checkDate()
  statementNum.value = formatStatementNum()
  kemu.value = obj.kemu
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

/*:deep(.table1 .ant-table ){
height:300px !important;
}*/
/*:deep(.table1 .ant-table-body){
  height:auto !important;
  max-height: none !important;;
}
:deep(.a1 table thead th:nth-of-type(1) span){
  display: none
}*/

.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
}

.app-container:nth-of-type(2) {
  /*padding: 0px;
  margin: 5px 10px;
  background: #b4c8e3 !important;*/
  background-color: #f2f2f2;
  padding: 0px;
  margin: 5px 10px;
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

.app-container:nth-of-type(3) {
  /*padding: 0px;
  margin: 5px 10px;
  background: #b4c8e3 !important;*/
  background-color: #f2f2f2;
  padding: 0px;
  margin: 5px 10px;
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

:deep(.ant-pagination) {
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
  margin: 0 !important;
}

:deep(.vben-basic-table){
  height: calc(100% - 160px);
  margin-bottom: 20px;
}

:deep(.ant-input),:deep(.ant-select),:deep(.ant-btn){
  border: 1px solid #c9c9c9;
}
</style>
