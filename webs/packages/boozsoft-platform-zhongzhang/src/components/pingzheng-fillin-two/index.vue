<template>
  <div class="app-container">
    <div class="app-container-top lcr-theme-div">
      <div>
        <div>
          <CopyOutlined style="color: white;font-size: 50px;"/>
        </div>
        <div><AccountPicker theme="three" @reloadTable="dynamicAdReload" :readonly="status != 3?'':'false'"/></div>
      </div>
      <div></div>
      <div>
        <div>
          <Button class="actod-btn actod-btn-last" @click="pageEventWatch('sts')">保存并新增</Button>
          <Button class="actod-btn actod-btn-last" @click="pageEventWatch('save')">保存</Button>
          <Button class="actod-btn actod-btn-last" @click="pageEventWatch('givp')">放弃</Button>
          <Button class="actod-btn actod-btn-last" @click="pageEventWatch('tsave')">暂存</Button>
          <Button class="actod-btn actod-btn-last" @click="pageEventWatch('rowa')">增行</Button>
          <Button class="actod-btn actod-btn-last" @click="pageEventWatch('rowd')">删行</Button>
          <Button class="actod-btn actod-btn-last" @click="pageEventWatch('flow')">流量</Button>
          <Button class="actod-btn actod-btn-last" @click="pageEventWatch('route')">列表</Button>
          <Button class="actod-btn actod-btn-last" @click="pageEventWatch('out')">退出</Button>
        </div>
        <div :class="status != 3?'status-look':''">
          <div class="acttd-right-d-search">
            <InputSearch
              class="acttdrd-search-input"
              placeholder=""
              style="border-left:1px solid #c9c9c9;"
            />
<!--              @search="pageSearch"
              v-model:value="pageParameter.searchConditon.value"-->
          </div>
          <div class="acttd-right-d-btns">
            <Button class="acttdrd-btn">
              <SyncOutlined :style="{ fontSize: '14px' }"/>
            </Button>

            <Popover placement="bottom">
              <template #content>
                <Button style="width: 120px;margin-bottom: 2px" @click="openPrint">打印单据</Button>
                <br/>
                <Button style="width: 120px;margin-bottom: 2px" @click="toPrintPage">模版设置</Button>
              </template>
              <Button class="acttdrd-btn">
                <PrinterOutlined :style="{ fontSize: '14px' }"/>
              </Button>
            </Popover>
            <Popover placement="bottom" v-model:visible="visible">
              <template #content>
<!--                <Button style="width: 120px;margin-bottom: 2px" @click="openSetting">表头栏目设置</Button>
                <br/>
                <DynamicColumn  :defaultData="initDynamics()['DATA']" :dynamicData="dynamicColumnModel" :lanmuInfo="lanMuData" @reload="reloadColumns"/>
                <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                      :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
                  v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
                <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                      :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
                  v-if="pageParameter.showRulesSize==='MIN'"/></span>-->
              </template>
              <Button class="acttdrd-btn">
                <SettingFilled :style="{ fontSize: '14px' }"/>
              </Button>
            </Popover>
          </div>
        </div>
      </div>
    </div>
    <div class="app-container-bottom" :style="{height: (windowHeight+70)+'px'}">
      <div class="acb-head">
        <div class="acbgead-one">
          <div>
            <div class="acbgead-one-triangle">
              <div></div>
              <div>
              <span
                style="color: white;">{{
                  status == 0 ? '填制' : status == 1 ? '新增' : status == 2 ? '编辑' : '查看'
                }}</span>
              </div>
            </div>
            <div class="acbgead-one-changes" :class="status == 1 || status == 2 ?'status-look':''">
              <VerticalRightOutlined @click="contentSwitch('first')"/>&nbsp;
              <LeftOutlined @click="contentSwitch('prev')"/>&nbsp;
              <RightOutlined @click="contentSwitch('next')"/>&nbsp;
              <VerticalLeftOutlined @click="contentSwitch('tail')"/>
              <span v-if="status=='3'" class="anticon" style="margin-left: 1em">
<!--                     <Tag :color="'volcano'" v-if="showTags.SH">已审核</Tag>
                -->
            </span>
            </div>
          </div>
          <div>
            <span style="font-size: 30px;font-weight: bold;" :style="{color: '#0096c7'}">记账凭证</span>
          </div>
          <div>
            <span style="font-size: 14px;color: rgb(102 102 102);">Ctrl + F 内容检索</span>
          </div>
        </div>
        <div class="acbgead-two" :style="status == 3?{ pointerEvents: 'none'}:{}">
             <div>
               <Select :options="[{value: '记',label: '记'}]" v-model:value="saveModel['csign']"  style="min-width: 60px;" />
               <span class="title-span">字&nbsp;第</span>
               <Input style="width: 100px;" v-model:value="saveModel['inoId']"/>
               <span class="title-span">号&ensp;附单据数：</span>
               <InputNumber style="width: 60px;"  v-model:value="saveModel['idoc']"/>
             </div>
             <div><span class="title-span">制单日期：</span><DatePicker  v-model:value="saveModel['dbillDate']" value-format="YYYY-MM-DD"/></div>
             <div><span class="title-span">本币：</span><span style="font-weight: bold;">人民币</span></div>
             <div>
               <ControlOutlined />
               <DollarCircleOutlined />
               <CalculatorOutlined />
               <FlagOutlined />
               <FundViewOutlined />
               <HddOutlined />
               <ExpandOutlined />
             </div>
        </div>
      </div>
      <div class="acb-centent">
        <BasicTable
          ref="tableRef"
          :loading="tableLoad"
          :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys,getCheckboxProps:rowSelection.getCheckboxProps, onChange: onSelectChange }"
          :scroll="{ x: 1290+50/*totalTableColumnWidth*/,y: windowHeight-(180 + (1*39))}"
          :clickToRowSelect="false"
          size="small"
          class="voucher-basic-table"
          @rowClick="numberClick"
          @register="registerTable"
        >
          <template #cdigest="{ record }">
            <template v-if="record.editCdigest">
              <Select
                v-model:value="record.tempCdigest"
                :open="record.sopen"
                :default-active-first-option="false"
                :not-found-content="null"
                :show-arrow="false"
                :filter-option="filterOption"
                :options="summaryOptions"
                :search-value="record.searchVal"
                show-search
                size="large"
                style="width: 100%;"
                class="cdigest"
                @search="(v)=>writeText(v,record)"
                @keyup="(e)=>summaryWatch(e,record)"
               />
            </template>
            <template v-else>
              <div @click="record.tempCdigest=record.cdigest,record.editCdigest = true;" style="text-align: left;"
                   :class="status == 1 || status == 2?'suspended-div':'status-look'">
                <span>{{ record.cdigest }}</span>
              </div>
            </template>
          </template>
         <template #ccode="{ record }">
            <template v-if="record.editCcode">
              <Select
                v-model:value="record.tempCcode"
                :default-active-first-option="true"
                :filter-option="filterOptionTwo"
                :not-found-content="null"
                :options="codeOptions"
                :show-arrow="false"
                show-search
                style="width: 100%;"
                size="large"
                class="ccode"
                @keyup.enter="focusNext(record,'ccode')"
              ></Select>
            </template>
            <template v-else>
              <div @click="record.tempCcode=record.ccode,record.editCcode = true;"  style="text-align: left;"
                   :class="status == 1 || status == 2?'suspended-div':'status-look'">
                <span>{{formatText(record.ccode,'code')}}</span>
              </div>
            </template>
          </template>

          <template #colum3="{ record }">
            <template v-if="record.editColum3 && record.isFu">
              <Input
                v-model:value="record.tempColum3"
                style="width:76%;"
                class="colum3"
                :readonly="true"
                @keyup.enter="hasBlank(record?.fuzhuStr)?openAssist(record):focusNext(record,'colum3')"
              />
              <SearchOutlined
                @click="openAssist(record)"/>
            </template>
            <template v-else>
              <div @click="record.tempColum3=record.colum3,record.editColum3 = true;"  style="text-align: left;"
                   :class="status == 1 || status == 2?'suspended-div':'status-look'">
                <span v-if="record.isFu && hasBlank(record?.fuzhuStr)" style="color: #e88f09;">待录入!</span>
                <span v-else>{{record?.fuzhuStr}}</span>
              </div>
            </template>
          </template>

          <template #colum4="{ record }">
                <span>{{record?.colum4}}</span>
          </template>

          <template #colum501="{ record }">
            <template v-if="record.editColum5">
              <InputNumber  class="colum5" :step="0.01"  v-model:value="record.tempColum5"  @keyup="(e)=>amountWatch(e,record,'colum5')"/>
            </template>
            <template v-else>
              <!-- @click="amountToggle(record,'colum5',true)"-->
              <div :class="status == 1 || status == 2?'suspended-div':'status-look'">
                <span>{{ record.colum501 }}</span>
              </div>
            </template>
          </template>

          <template #colum601="{ record }">
            <template v-if="record.editColum6">
              <InputNumber  class="colum6" :step="0.01" :min="-9999999999999.99" :max="9999999999999.99" v-model:value="record.tempColum6"  @keyup="(e)=>amountWatch(e,record,'colum6')" />
            </template>
            <template v-else>
              <!-- @click="amountToggle(record,'colum5',true)"-->
              <div :class="status == 1 || status == 2?'suspended-div':'status-look'">
                <span>{{ record.colum601 }}</span>
              </div>
            </template>
          </template>
          <template #summary>
            <TableSummary fixed>
              <TableSummaryRow >
                <TableSummaryCell :colSpan="6" :index="0" :align="'left'">
                  <span>&emsp;&emsp;合计：</span>
                  <span>{{totalModel.mc===totalModel.md && 0 != totalModel.mc?convertCurrency(totalModel.mc+''):''}}</span>
                </TableSummaryCell>
                <TableSummaryCell :index=5 :colSpan="15" :align="'right'">
                  <span>{{NumberTool.amountThousands(totalModel.mc+'')}}</span>
                </TableSummaryCell>
                <TableSummaryCell :index="21" :colSpan="15" :align="'right'">
                  <span>{{NumberTool.amountThousands(totalModel.md+'')}}</span>
                </TableSummaryCell>
               </TableSummaryRow>
            </TableSummary>
          </template>
        </BasicTable>
        <div class="acb-centent-tool" :style="{width: 1290+50+'px'}">
          <div><span>制单：</span><span></span>{{saveModel['cbill']}}</div>
        </div>
        <Assist
          @save="(r)=>focusNext(r,'colum3')"
          @register="registerAssist"
        />
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
/**************** 引用块 *****************/
/*********** 系统块 *************/
import {nextTick, onMounted, provide, reactive, ref, unref} from "vue";
import {Button, DatePicker, Input,InputNumber, message, Popconfirm, Popover, Select, Table, Tag} from "ant-design-vue";
import {
  CheckOutlined,
  LeftOutlined,
  PrinterOutlined,
  RightOutlined,
  SettingFilled,
  SortAscendingOutlined,
  SortDescendingOutlined,
  SyncOutlined,
  VerticalLeftOutlined,
  VerticalRightOutlined,CopyOutlined,
  ControlOutlined,DollarCircleOutlined,CalculatorOutlined,ExpandOutlined,FundViewOutlined,HddOutlined,FlagOutlined
} from '@ant-design/icons-vue';
import {BasicTable, useTable} from '/@/components/Table'
import {SearchOutlined} from '@ant-design/icons-vue';
/************* 系统块 ***************/
/*********** 业务块 *************/
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {findByLastCodeHierarchyNames} from "/@/api/codekemu/codekemu";
import {defRouteApi, useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {findAllByOrderByCcodeApi as findAllSummary} from "/@/api/boozsoft/account/AccvoucherCdigest";
import {findAllVoucherSummary,accvoucherSaves} from "/@/api/record/system/accvoucher";
import {hasBlank} from "/@/api/task-api/tast-bus-api";
import {JsonTool, NumberTool, ObjTool} from "/@/api/task-api/tools/universal-tools";
/*********** 业务块 *************/
/**************** 引用块 *****************/

/**************** 变量块 *****************/
const TableSummary = Table.Summary
const TableSummaryRow = Table.Summary.Row
const TableSummaryCell = Table.Summary.Cell

import {convertCurrency} from "/@/utils/boozsoft/moneyUtil";
import {useMessage} from "/@/hooks/web/useMessage";
import {useModal} from "/@/components/Modal";
import Assist from "/@/components/pingzheng-fillin-two/components/Assist.vue";
const {createWarningModal, createConfirm} = useMessage()
const windowHeight = (window.innerHeight - 300)
const busDate = useCompanyOperateStoreWidthOut().getLoginDate
const status = ref(1)
const InputSearch = Input.Search
let num = 0
const visible = ref(false)
const tableRef = ref(null)
const testColums = [
  {title: '摘要', dataIndex: 'cdigest', ellipsis: true,width:150, slots: {customRender: 'cdigest'}},
  {title: '会计科目', dataIndex: 'ccode', ellipsis: true,width:250, slots: {customRender: 'ccode'}},
  {title: '辅助核算', dataIndex: 'colum3', ellipsis: true, width:120,slots: {customRender: 'colum3'}},
  {title: '货币/金额',dataIndex: 'colum4', width:120,children:
      [{title: '汇率', dataIndex: 'colum41', ellipsis: true, width:120, slots: {customRender: 'colum41'}}]
  },
  {title: '借方金额（本币）',dataIndex: 'colum5', width:260,slots: {customRender: 'colum5'},children: [
    {title: '万', dataIndex: 'colum501', ellipsis: true,width:20, slots: {customRender: 'colum501'},customCell: (o) => {	return {style: {padding: '0px'}}} },
    {title: '千', dataIndex: 'colum502', ellipsis: true,width:20, slots: {customRender: 'colum502'},customCell: (o) => {	return {style: {padding: '0px'}}} },
    {title: '百', dataIndex: 'colum503', ellipsis: true,width:20, slots: {customRender: 'colum503'},customCell: (o) => {	return {style: {padding: '0px'}}} },
    {title: '十', dataIndex: 'colum504', ellipsis: true,width:20, slots: {customRender: 'colum504'},customCell: (o) => {	return {style: {padding: '0px'}}} },
    {title: '亿', dataIndex: 'colum505', ellipsis: true,width:20, slots: {customRender: 'colum505'},customCell: (o) => {	return {style: {padding: '0px'}}} },
    {title: '千', dataIndex: 'colum506', ellipsis: true,width:20, slots: {customRender: 'colum506'},customCell: (o) => {	return {style: {padding: '0px'}}} },
    {title: '百', dataIndex: 'colum507', ellipsis: true,width:20, slots: {customRender: 'colum507'},customCell: (o) => {	return {style: {padding: '0px'}}} },
    {title: '十', dataIndex: 'colum508', ellipsis: true,width:20, slots: {customRender: 'colum508'},customCell: (o) => {	return {style: {padding: '0px'}}} },
    {title: '万', dataIndex: 'colum509', ellipsis: true,width:20, slots: {customRender: 'colum509'},customCell: (o) => {	return {style: {padding: '0px'}}} },
    {title: '千', dataIndex: 'colum510', ellipsis: true,width:20, slots: {customRender: 'colum510'},customCell: (o) => {	return {style: {padding: '0px'}}} },
    {title: '百', dataIndex: 'colum511', ellipsis: true,width:20, slots: {customRender: 'colum511'},customCell: (o) => {	return {style: {padding: '0px'}}} },
    {title: '十', dataIndex: 'colum512', ellipsis: true,width:20, slots: {customRender: 'colum512'},customCell: (o) => {	return {style: {padding: '0px'}}} },
    {title: '元', dataIndex: 'colum513', ellipsis: true,width:20, slots: {customRender: 'colum513'},customCell: (o) => {	return {style: {padding: '0px'}}} },
    {title: '角', dataIndex: 'colum514', ellipsis: true,width:20, slots: {customRender: 'colum514'},customCell: (o) => {	return {style: {padding: '0px'}}} },
    {title: '分', dataIndex: 'colum515', ellipsis: true,width:20, slots: {customRender: 'colum515'},customCell: (o) => {	return {style: {padding: '0px'}}} },
  ]},
  {title: '贷方金额（本币）',dataIndex: 'colum6',width:260,children: [
      {title: '万', dataIndex: 'colum601', ellipsis: true, width:20,slots: {customRender: 'colum601'}, customCell: (o) => {	return {style: {padding: '0px'}} }},
      {title: '千', dataIndex: 'colum602', ellipsis: true, width:20,slots: {customRender: 'colum602'}, customCell: (o) => {	return {style: {padding: '0px'}} }},
      {title: '百', dataIndex: 'colum603', ellipsis: true, width:20,slots: {customRender: 'colum603'}, customCell: (o) => {	return {style: {padding: '0px'}} }},
      {title: '十', dataIndex: 'colum604', ellipsis: true, width:20,slots: {customRender: 'colum604'}, customCell: (o) => {	return {style: {padding: '0px'}} }},
      {title: '亿', dataIndex: 'colum605', ellipsis: true, width:20,slots: {customRender: 'colum605'}, customCell: (o) => {	return {style: {padding: '0px'}} }},
      {title: '千', dataIndex: 'colum606', ellipsis: true, width:20,slots: {customRender: 'colum606'}, customCell: (o) => {	return {style: {padding: '0px'}} }},
      {title: '百', dataIndex: 'colum607', ellipsis: true, width:20,slots: {customRender: 'colum607'}, customCell: (o) => {	return {style: {padding: '0px'}} }},
      {title: '十', dataIndex: 'colum608', ellipsis: true, width:20,slots: {customRender: 'colum608'}, customCell: (o) => {	return {style: {padding: '0px'}} }},
      {title: '万', dataIndex: 'colum609', ellipsis: true, width:20,slots: {customRender: 'colum609'}, customCell: (o) => {	return {style: {padding: '0px'}} }},
      {title: '千', dataIndex: 'colum610', ellipsis: true, width:20,slots: {customRender: 'colum610'}, customCell: (o) => {	return {style: {padding: '0px'}} }},
      {title: '百', dataIndex: 'colum611', ellipsis: true, width:20,slots: {customRender: 'colum611'}, customCell: (o) => {	return {style: {padding: '0px'}} }},
      {title: '十', dataIndex: 'colum612', ellipsis: true, width:20,slots: {customRender: 'colum612'}, customCell: (o) => {	return {style: {padding: '0px'}} }},
      {title: '元', dataIndex: 'colum613', ellipsis: true, width:20,slots: {customRender: 'colum613'}, customCell: (o) => {	return {style: {padding: '0px'}} }},
      {title: '角', dataIndex: 'colum614', ellipsis: true, width:20,slots: {customRender: 'colum614'}, customCell: (o) => {	return {style: {padding: '0px'}} }},
      {title: '分', dataIndex: 'colum615', ellipsis: true, width:20,slots: {customRender: 'colum615'}, customCell: (o) => {	return {style: {padding: '0px'}} }},
    ]}
]

const summaryOptions = ref([])
const codeOptions = ref([])

const totalModel = reactive({
  md: 0,
  mc: 0
})
const summaryModel = reactive({
  defaults: [],
  vouchers: []
})
const tableLoad = ref(false)
const codeAllList = ref([])

const dynamicTenant = ref(null)


const state = reactive<{
  selectedRowKeys: [];
  selectedRowObjs: [];
  loading: boolean;
}>({
  selectedRowKeys: [],
  selectedRowObjs: [],
  loading: false,
});

const rowDelData:any=ref([])

const saveModel = reactive({})

/**************** 变量块 *****************/

/**************** 方法块 *****************/
/************* 实例方法块 **************/
// 这是示例组件
const [registerTable, {reload, getDataSource, setTableData, setPagination, getPaginationRef, getColumns, setColumns}] = useTable({
  columns: testColums,
  dataSource: [{sopen:false,searchVal:null},{sopen:false,searchVal:null},{sopen:false,searchVal:null},{sopen:false,searchVal:null},{sopen:false,searchVal:null},{sopen:false,searchVal:null},{sopen:false,searchVal:null},{sopen:false,searchVal:null}],
  bordered: true,
  showIndexColumn: true,
  indexColumnProps: {fixed: true},
  pagination: false,
})


const [registerAssist, {openModal: opeAssistPageM}] = useModal()

/************* 实例方法块 **************/

onMounted(()=>{
  tableRef.value.$el.style.setProperty('width', 1290+20+50 + 'px')
})


const dynamicAdReload = async (obj) => {
  dynamicTenant.value = obj
  initData()
}

async function initData() {
  summaryModel.defaults = (await useRouteApi(findAllSummary, {schemaName: dynamicTenant.value?.accountMode})(null) || [])
  summaryModel.vouchers = (await useRouteApi(findAllVoucherSummary, {schemaName: dynamicTenant.value?.accountMode})({ iyear: busDate.substring(0,4)}) || [])
  codeAllList.value = (await useRouteApi(findByLastCodeHierarchyNames, {schemaName: dynamicTenant.value?.accountMode})({iyear: busDate.substring(0,4)}) || [])
  summaryOptions.value = unref(summaryModel.vouchers).map(s=>({value: s,label: s}))
  codeOptions.value = unref(codeAllList.value).map(o=>({value: o.uniqueCode,label:  o.uniqueCode+' '+o.ccodeName}))
}

/******************* top工具栏 ********************/
const pageEventWatch = async(action) => {
  switch (action) {
    case 'sts':
      break;
    case 'save':
      await dbInteraction('save')
      break;
    case 'rowa':
      rowEventWatch('add')
      break;
    case 'rowd':
      rowEventWatch('del')
      break;
  }
}

const outBefore = () => {

}

const rowEventWatch = (type) => {
  if (type == 'add'){
    let list = getDataSource()
    if (state.selectedRowKeys.length == 1){
      let selectIndex = list.findIndex(it => it.key === state.selectedRowKeys[0])
      list.splice(selectIndex, 0, {sopen:false,searchVal:null})
    }else {
      list.push({sopen:false,searchVal:null})
    }
    setTableData(list)
  }else if (type == 'del'){
    if (state.selectedRowKeys.length == 1) {
      let list:any = getDataSource()
      let selectIndex = list.findIndex(it => it.key === state.selectedRowKeys[0])
      // 修改状态下删除行
      if(status.value==2){
        rowDelData.value.push(list.filter(it => it.key === state.selectedRowKeys[0]))
      }
      list.splice(selectIndex, 1)
      totalCalculate(list)
      setTableData(list)
      state.selectedRowKeys = []
      state.selectedRowObjs = []
    }
    else {
      createWarningModal({
        title: '温馨提示',
        content: '请选择删除行次！'
      })
    }
  }
}
/******************* top工具栏 ********************/


/******************* table 表头业务 ********************/
const contentSwitch = (type) => {

}
/******************* table 表头业务 ********************/


/******************* table 表体业务 ********************/
const numberClick = (r,i,e) => {
  if (e.target.cellIndex > 5 && e.target.cellIndex < 20){
    amountToggle(r,'colum5',true)
  }else if ( e.target.cellIndex >= 20){
    amountToggle(r,'colum6',true)
  }
}

const focusNext =  (r, c,trN) => {
  // 得到当前临时标记
  let t = indexToUpper(c,0)
  Object.keys(r).map(i => {
    if (i.startsWith('edit')) r[i] = null
  })
  r[`${c}`] = r[`temp${t}`];
  tableDataRowChange(r, c)
  let list = getDataSource();
  let filters = ['colum3','colum4']
  if (r.isFu) filters =  ['colum4']
  let cols = getColumns().filter(it => it.title != '序号' && filters.indexOf(it.dataIndex) == -1)
  let index = list.findIndex(it => it.key == r.key)
  let nextC = cols[0].dataIndex // 获取下一个列位置
  if (index == list.length - 1 && cols[cols.length - 1].dataIndex == c) { // 最后一行最后一列回车追加
    list.push({editCdigest: true,sopen:false,searchVal:null})
    setTableData(list)
  } else {
    let cObj = cols[cols.findIndex(it => it.dataIndex == c) + 1]
    if (cObj != null) {
      nextC = cObj.dataIndex
      // 的到指定位置
      let nextMark = indexToUpper(nextC,0)
      r[`edit${nextMark}`] = true;
      r[`temp${nextMark}`] = r[`${nextC}`];
      if (nextC  == 'colum5' || nextC == 'colum6') amountToggle(r,nextC,true)
    } else { //之后一列时换行
      nextC = cols[0].dataIndex
      let nextMark = indexToUpper(nextC,0)
      ++index
      list[index][`edit${nextMark}`] = true
      list[index][`temp${nextMark}`] = list[index][`${nextC}`];
      if ((!hasBlank(r.colum5) || !hasBlank(r.colum6))){
        let {ce,fx} = balanceDifference(r,list)
        if (ce != 0){
          // if (hasBlank(r['pmark']))  r['pmark'] = uuid()
          if (!hasBlank(r.cdigest))list[index][`tempCdigest`] = r.cdigest
          list[index]['colum5']=0
          list[index]['colum6']=0
          list[index][fx] = ce
          if (trN != null)
            splitNumber(list[index],fx,trN.cells[fx=='colum5'?5:20])
          // list[index][`pmark`] = r['pmark']
        }
      }
      totalCalculate(list)
      setTableData(list)
    }
  }
  nextTick(() => {
    let arr = document.getElementsByClassName(nextC)
    let doms = nextC==='colum3'?arr[arr.length-1]:arr[arr.length-1]?.getElementsByTagName('input')[0]
    if (null != doms) doms.focus()
  })
}

const tableDataRowChange =  async (r,c) => {
  switch (c) {
    case 'ccode':
      r.isFu = !hasBlank(codeAllList.value.filter(it=>it.uniqueCode === r.ccode)[0]?.fuzhu)
      break;
  }
}

function indexToUpper(str,index) {
  return str.trim().replace(str[index], str[index].toUpperCase());
}

const amountToggle = (r,c,b) => {
  const ckey = indexToUpper(c,0)
  if (b){
    r[`temp${ckey}`] = r[`${c}`];
    r[`edit${ckey}`] = true
  }
  nextTick(()=>{
    let tdE = document.getElementsByClassName(c)[0]?.parentNode
    if (b){
      tdE.colSpan = 15
      let te = tdE
      for (let i = 0; i < 14; i++) {
        te.nextElementSibling.style.display = 'none'
        te = te.nextElementSibling
      }
    } else {
      tdE.colSpan = 0
      let te = tdE
      for (let i = 0; i < 14; i++) {
        te.nextElementSibling.style.display = ''
        te = te.nextElementSibling
      }
    }
  })
  if (!b){
    r[`edit${ckey}`] = false
    let tdE = document.getElementsByClassName(c)[0]?.parentNode
    if ( !hasBlank(r['ccode']) && null != r[`temp${ckey}`] && r[`temp${ckey}`] != 0){
      let trE = document.getElementsByClassName(c)[0]?.parentNode?.parentNode?.nextElementSibling
      r[`${c}`] = r[`temp${ckey}`];
      splitNumber(r,c,tdE)
      if (c=='colum5'){
        r[`colum6`] = ''
        splitNumber(r,'colum6',trE?.cells[20])
      }else {
        r[`colum5`] = ''
        splitNumber(r,'colum5',trE?.cells[5])
      }
      focusNext(r,c=='colum5'?'colum6':c,trE)
    }else {
      r[`${c}`] = ''
      r[`temp${ckey}`] = ''
      splitNumber(r,c,tdE)
      focusNext(r,c,null)
    }
  }
}

const splitNumber = (r,key,tdO) => {
  for (let i = 15; i > 0; i--)
    r[`${key}${i>9?i:'0'+i}`] = null
  let zV = r[key]
  const v = Math.abs(r[key])
  if (v != 0){ // 截取
    const vs = v.toFixed(2).split('.')
    r[`${key}${14}`] = vs[1][0]
    r[`${key}${15}`] = vs[1][1]
    let c =  vs[0].length
    for (let i = 13; i > (13-vs[0].length); i--) {
      r[`${key}${i>9?i:'0'+i}`] = vs[0][c-1]
      c--
    }
  }
  if (tdO != null){ // 上色
    let te = tdO
    te.style.color = zV >-1?'':'red'
    for (let i = 0; i < 14; i++) {
      te.nextElementSibling.style.color = zV >-1?'':'red'
      te = te.nextElementSibling
    }
  }
}

const summaryWatch = (a,r) => {
  if (a.code === 'Space'){
    r.sopen=false
    nextTick(()=>r.sopen=true)
  }else if(a.code === 'Enter' || a.code === 'NumpadEnter'){
    if (/*hasBlank(r['tempCdigest']) &&*/ !hasBlank(r.searchVal)){
      let v = r.searchVal.trim()
      summaryOptions.value.push({value: v,label: v})
      r['tempCdigest']=v
      r.searchVal = ''
    }
    focusNext(r,'cdigest')
  }
}

const amountWatch = (a,r,c) => {
  if (a.code === 'Equal'){
    if (!hasBlank(r.ccode)){
      let list = getDataSource();
      let {ce,fx} = balanceDifference(r,list)
      if (ce != 0){
        let key = indexToUpper(fx,0)
        r['temp'+key]=ce
      }
    }
  }else if(a.code === 'Enter' || a.code === 'NumpadEnter'){
    amountToggle(r,c,false)
  }
}

const writeText = (v,r) => {
  r.searchVal = v
}

const filterOption = (input: string, option: any) => {
  return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const filterOptionTwo = (input: string, option: any) => {
  return isNaN(input)?option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0:option.value.startsWith(input);
};

const formatText = (v,type) => {
  let text = v
  if (type === 'code'){
    let o = codeOptions.value.filter(it=>it.value == v)[0]
    if (o!=null)text = `${o.label}`
  }
  return text
}

const balanceDifference = (r,list) => {
  // 得到科目方向
  // const kmO = codeAllList.value.filter(it=>it.uniqueCode == r['ccode'])[0]
  // 试算平衡
  let ce = hasBlank(r['colum5'])? r['colum6']:r['colum5']
  // let arr = list.filter(it=> null != r['pmark'] && it.pmark == r['pmark'])
  let arr = list.filter(it=> !hasBlank(it.ccode) && (!hasBlank(it.colum5) || !hasBlank(it.colum6)))
  let fx = hasBlank(r['colum5'])?'colum5':'colum6'
  if (arr.length > 0){
    let mc = 0;
    let md = 0;
    arr.forEach((o,i)=>{
      mc+= (o['colum5'] || 0)
      md+= (o['colum6'] || 0)
    })
    console.log('mc:'+mc+'   md:'+md)
    if (mc !== md){
      ce = ( Math.abs(mc)>Math.abs(md))? mc - md:md-mc
      fx = Math.abs(mc)>Math.abs(md)?'colum6':'colum5'
    }else if (mc === md) {
      ce = 0
    }
  }
  return {ce: ce,fx: fx}
}

const totalCalculate = (list) => {
  let arr = list.filter(it=>!hasBlank(it.ccode) && (!hasBlank(it.colum5) || !hasBlank(it.colum6)))
  let mc = 0;
  let md = 0;
  arr.forEach((o,i)=>{
    mc+= (o['colum5'] || 0)
    md+= (o['colum6'] || 0)
  })
  totalModel.mc = mc
  totalModel.md = md
}

const onSelectChange = (selectedRowKeys,row) => {
  state.selectedRowKeys = selectedRowKeys;
  state.selectedRowObjs = row
};

const rowSelection = {
  getCheckboxProps: (record) => ({
    disabled:  record.cdigest==null
  }),
};


/******************* table 表体业务 ********************/

/******************* table 弹框业务 ********************/
const openAssist = (r) => {
  opeAssistPageM(true, {
    row: r,
    info: codeAllList.value.filter(it=>it.uniqueCode === r.ccode)[0],
    tenant:  dynamicTenant.value
  })
}
/******************* table 弹框业务 ********************/
const checkTheAssembly = async (action) => {
  if (hasBlank(saveModel['csign']) || hasBlank(saveModel['inoId']) || hasBlank(saveModel['idoc']) || hasBlank(saveModel['dbillDate'])){
    createWarningModal({
      title: '温馨提示',
      content: '表头：凭证字号、凭证号与附单据数、制单日期不能为空！'
    })
    return null
  }
  let list = JsonTool.parseProxy(getDataSource().filter(it=> !hasBlank(it['cdigest'] && !hasBlank(it['ccode']) && (!hasBlank(it['colum5']) || !hasBlank(it['colum6'])))))
  if (list.length < 2 || (list.length > 2 && totalModel.mc != totalModel.md)){
    createWarningModal({
      title: '温馨提示',
      content: '表体：凭证分录不得低于两行且合计借贷方金额必须对等！'
    })
    return null
  }
  let fuList = list.filter(it=>it.isFu && hasBlank(it.fuzhuStr))
  if (fuList.length > 0){
    let keys = fuList.map(it=>it.key)
    keys = keys.map(k=>getDataSource().findIndex(it=> it.key === k)).map(i=>i+=1)
    createWarningModal({
      title: '温馨提示',
        content: '表体：请先完善序号为【'+keys.join()+'】的辅助核算录入！'
    })
    return null
  }
  let dates =  saveModel['dbillDate'].split('-')
  let lastList = JsonTool.json(
    list.map((r,i)=>{
      r.vouchUnCode = r.key
      r.inid = (i+1)+''
      r.csign = saveModel['csign']
      r.inoId = saveModel['inoId']
      r.idoc = saveModel['idoc']
      r.dbillDate = saveModel['dbillDate']
      r.cbill = saveModel['cbill']
      r.iyear = dates[0]
      r.imonth = dates[1]
      r.iyperiod = dates[0]+''+dates[1]
      r.ifrag = ('tsave' === action?'2':'0')
      r.ccodeName = (codeAllList.value.filter(it=>it.ccode == r.ccode)[0]?.ccodeName || '')
      r.mc = r.colum5 || '0'
      r.md = r.colum6 || '0'
      let delkeys = ['sopen','searchVal','fuzhuStr','isFu','key']
      delkeys.push(...(Object.keys(r).filter(k=>['colum','edit','temp'].filter(s=>k.indexOf(s)!=-1).length>0)))
      return  ObjTool.dels(r,delkeys)
    })
  )
  return lastList
}
/******************* 检测 业务 ********************/

/******************* 检测 业务 ********************/

/******************* DB 业务 ********************/

async function dbInteraction(action) {
  switch (action) {
    case 'save':
      let json = await checkTheAssembly(action);
      if (null != json) {
        console.log(json)
        await useRouteApi(accvoucherSaves, {schemaName: dynamicTenant.value?.accountMode})({str: json})
        message.success('保存成功!')
      }
      break;
  }
}
/******************* DB 业务 ********************/


/**************** 方法块 *****************/
</script>
<style lang="less" scoped="scoped">
@import '/@/assets/styles/voucher-basic-table.less';
@Global-Border-Color: #c9c9c9; // 全局下划线颜色
@Global-Label-Color: #666666; // 全局#c9c9c9颜色
@Global-Comm-BcOrText-Color: #0096c7; // 全局#c9c9c9颜色
@Global-FROM-BC-Color: #eeeeee; // 全局#c9c9c9颜色
.app-container {
  padding: 10px;
  background-color: #b4c8e3;
  margin: 0;
  .lcr-theme-div{
    display: inline-flex;justify-content: space-between;width: 100%;height: 100px;
    >div:nth-of-type(1){
      width: 40%;
      position: relative;
      display: block;
      >div:nth-of-type(1){width: 64px;display: inline-block;text-align: center;    top: 12px;
        position: inherit
      }
      >div:nth-of-type(2){

        width: calc( 100% - 64px);display: inline-block;
        :deep(.account-picker){
          span:not(:nth-of-type(2)){
            color: white !important;
          }
        }
      }
    }
    >div:nth-of-type(2){
      width: 20%;text-align:center;
      >div:nth-of-type(1){
        color: @Global-Comm-BcOrText-Color;
        font-weight: bold;
        font-size: 24px;
      }
      >div:nth-of-type(2){margin-top: 14px;}
    }
    >div:nth-of-type(3){
      width: 40%;text-align: right;
      display: block;
      >div:nth-of-type(1){
        .actod-btn {
          color: @Global-Comm-BcOrText-Color;
          font-size: 14px;
          margin: 0 1px 0 0;
        }

        .actod-btn-last {
          border-right: 1px solid @Global-Border-Color;
        }

        .actod-btn:hover {
          background-color: @Global-Comm-BcOrText-Color;
          color: white;
          font-weight: bold;
          border: 1px solid white;
        }
      }
      >div:nth-of-type(2){
        display: inline-flex;justify-content: space-between;margin-top: 14px;
        .acttd-right-d-search {
          .acttdrd-search-select {
            width: 120px;

            :deep(.ant-select-selector) {
              border-color: @Global-Border-Color;
              border-radius: 2px 0 0 2px;
            }
          }

          .acttdrd-search-input {
            width: 180px;
            border-radius: 0 2px 2px 0;
            border-color: @Global-Border-Color;
            border-left: none!important;
          }
        }

        .acttd-right-d-btns {
          margin-left: 10px;

          .acttdrd-btn {
            color: @Global-Comm-BcOrText-Color;
            margin-left: 2px;
          }
          .acttdrd-btn:hover{
            border-color: #ffffff;
            color: #ffffff;
            background-color: @Global-Comm-BcOrText-Color;
          }
        }
      }
    }
  }
  .app-container-top {
    background-color: @Global-Comm-BcOrText-Color !important;
    border-radius: 5px 5px 0 0;
    padding: 10px;
  }

  .app-container-bottom {
    background-color: @Global-FROM-BC-Color;

    .acb-head {
      .acbgead-one {
        text-align: center;
        height: 60px;
        line-height: 60px;
        width: 100%;
        display: inline-flex;
        justify-content: space-between;
        >div:nth-of-type(1){width: 45%;text-align: left;
        }
        >div:nth-of-type(2){width: 10%;}
        >div:nth-of-type(3){width: 45%;text-align: right;padding-right: 70px;}

        .acbgead-one-triangle {
          > div:nth-of-type(1) {
            width: 0px;
            height: 0px;
            border-top: 60px solid #5141d7;
            border-right: 70px solid transparent;
            position: absolute;
          }

          > div:nth-of-type(2) {
            width: max-content;
            color: #fff;
            transform: rotate(-45deg) translateY(-2px) translateX(10px);
            position: absolute;
          }
        }

        .acbgead-one-changes {
          font-weight: bold;
          font-size: 24px;
          color: #666666;
          margin-left: 10%;
          > span {
            cursor: pointer;
          }
          > span:hover {
            color: black;
          }
        }
      }

      .acbgead-two {
        width: 100%;
        display: inline-flex;
        justify-content: space-between;
        padding: 20px 80px 0;
        line-height: 32px;
        :deep(.ant-input),
        :deep(.ant-input-number),
        :deep(.ant-select-selector) {
          text-align-all: center;
          text-align: center;
          border: none;
          width: 100%;
          font-weight: bold;
          border-bottom: 1px solid @Global-Border-Color!important;
          background-color: @Global-FROM-BC-Color;
          .ant-select-selection-item{
            font-weight: bold;
          }
        }
        :deep(.ant-picker-input){
          width: 100px;
          text-align-last: center;
          input{
            font-weight: bold;
          }
        }
        .title-span{
          color: #77777f;
        }
        >div:last-of-type{
          .anticon{
            font-size: 18px;
            margin: 2px;
          }
          .anticon:hover{
            cursor:pointer;
          }
        }
      }
    }

    .acb-centent {
      margin: 5px 4% 0;

      .acb-centent-tool{
        width: 100%;
        display: inline-flex;
       /* justify-content: space-around;*/
        padding: 5px;
        >div{
          span{
            font-size: 18px;
            font-weight: bold;
            color: #77777f;
          }
        }
      }
    }
  }

  .status-look {
    pointer-events: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
  }
  .suspended-div {
    width: 100%;
    height: 42px;
    .anticon-form {
      float: right;
      display: none;
    }
  }
  .suspended-div:hover {
    cursor: text;
    background-color: #e4e7e7;
    .anticon-form {
      display: block;
    }
  }
}
</style>
