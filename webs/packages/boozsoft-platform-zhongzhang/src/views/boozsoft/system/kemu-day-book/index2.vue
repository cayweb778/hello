<template>
  <div>
    <div class="app-container">
      <div class="app-container-top">
        <div style="width: 100%;height: 100px;">
          <div style="width: 40%;padding-top:10px;">
            <div>
              <AccountPicker theme="three" readonly="" @reloadTable="dynamicAdReload" style="margin-top: -5px;"/>
            </div>
            <div style="margin-top: 10px;margin-left: 5px;">
              科目：
              <a-select
                v-model:value="kemu"
                @change="queryChange"
                style="width: 300px">
                <a-select-option v-for="item in accountList" :key="item.id" :value="item.ccode">
                  {{ item.ccode }}-{{ item.ccodeName }}
                </a-select-option>
              </a-select>
              &emsp;&emsp;币种：<span style="color: black;font-weight: bold">{{ biZhong }}</span>
              &emsp;&emsp;样式：<span>
              {{
                showRules === 'J' ? '金额式' : ''
              }}{{ showRules === 'SJ' ? '外币式' : '' }}{{ showRules === 'WJ' ? '金额外币式' : '' }}
            </span>
            </div>
          </div>
          <div style="width: 53%;float: right;margin-top: -67px;">
            <span style="font-size: 24px;color: #0096c7;font-weight: bold;">科目日记账</span>
            <div class="ant-btn-group" style="float: right;margin-top: 5px;">
              <button
                type="button"
                class="ant-btn ant-btn-me"
                ant-click-animating-without-extra-node="false"
                @click="openAddPage()"
              ><span>查询</span></button>
              <button
                type="button"
                class="ant-btn ant-btn-me"
                @click="exportExcel()"
              ><span>导出</span></button>
              <button
                type="button"
                class="ant-btn ant-btn-me"
                @click="openPrint()"
              ><span>打印</span></button>
              <button
                type="button"
                class="ant-btn ant-btn-me"
                @click="closeCurrent()"
              ><span>退出</span></button>
            </div>
            <span style="display: block;color: black;font-size: 14px;">{{ thisInterval.length === 17 ? '期间' : '日期' }}：{{ thisInterval }}
            <span style="float: right;margin-top: 10px;">
              <div>
                <a-button class="ant-btn" @click="tableReload">
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
                           style="max-height: 500px;overflow-y: auto" class="lanmu-table">
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
                <a-button style="width: 120px;margin-bottom: 2px">栏目设置</a-button>
              </a-popconfirm>
              <br/>
              <span class="group-btn-span-special2" @click="showRulesSize = 'MAX'"
                    :style="showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
                v-if="showRulesSize==='MAX'"/></span><br/>
              <span class="group-btn-span-special2" @click="showRulesSize = 'MIN'"
                    :style="showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
                v-if="showRulesSize==='MIN'"/></span>
            </template>
                  <!--            <template #title>
                                <b>设置表格字号</b></template>-->
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
                <a-popover class="ant-btn-default" placement="bottom">
                  <template #content>
                  <span class="group-btn-span-special2" @click="showRules = 'J'"
                        :style="(showRules==='J')?{backgroundColor: '#0096c7',color: 'white',cursor: 'pointer'}:{cursor: 'pointer'}"><OrderedListOutlined
                    :style="{ fontSize: '14px' }"/>&emsp;金额式&nbsp;&emsp;<CheckOutlined
                    v-if="showRules==='J'" :style="{ fontSize: '14px' }"/></span><br/>
                    <span class="group-btn-span-special2" @click="showRules = 'SJ'"
                          :style="(showRules==='SJ')?{backgroundColor: '#0096c7',color: 'white',cursor: 'pointer'}:{cursor: 'pointer'}"><OrderedListOutlined
                      :style="{ fontSize: '14px' }"/>&emsp;外币式&nbsp;&emsp;<CheckOutlined
                      v-if="showRules==='SJ'" :style="{ fontSize: '14px' }"/></span><br/>
                    <span class="group-btn-span-special2" @click="showRules = 'WJ'"
                          :style="(showRules==='WJ')?{backgroundColor: '#0096c7',color: 'white',cursor: 'pointer'}:{cursor: 'pointer'}"><OrderedListOutlined
                      :style="{ fontSize: '14px' }"/>&emsp;金额外币式<CheckOutlined v-if="showRules==='WJ'"
                                                                               :style="{ fontSize: '14px' }"/></span><br/>
                  </template>
                </a-popover>
              </div>
            </span>
          </span>
          </div>
        </div>
      </div>
    </div>
    <div class="app-container">
      <div class="app-container-bottom" style="margin-top: -40px;">
        <Edit
          @save="saveData"
          @register="registerEditPage"
        />
        <Print
          @save="printData"
          @register="registerPrintPage"
        />
        <a-table
          ref="TableInfo"
          :columns="initPageBasicDataByColumns(showRules,false,reloadMark)"
          :rowClassName="tableRow"
          :data-source="dataArr"
          :loading="loadMark"
          :showIndexColumn="true"
          :bordered="true"
          size="middle"
          :class="showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
          :pagination="{ pageSize: 1000,simple: true}"
          :scroll="{ x: 'calc(700px + 50%)', y  : tableHeight }"
        >

          <template #pzNum="{ record }">
          <span v-if="record.key!='booz'">
            <a @click="showPingZheng(record)">{{ record.pzNum }}</a>
          </span>
            <span v-else>
            &emsp;
          </span>
          </template>
          <template #jieMoney="{ record }">
          <span v-if="record.key!='booz'">
             <a v-if="record.pzNum!=''" @click="showPingZheng(record)">{{ record.jieMoney }}</a>
            <span v-if="record.pzNum==''">{{ record.jieMoney }}</span>
          </span>
            <span v-else>
            &emsp;
          </span>
          </template>
          <template #daiMoney="{ record }">
          <span v-if="record.key!='booz'">
            <a v-if="record.pzNum!=''" @click="showPingZheng(record)">{{ record.daiMoney }}</a>
            <span v-if="record.pzNum==''">{{ record.daiMoney }}</span>
          </span>
            <span v-else>
            &emsp;
          </span>
          </template>

          <template #summary>
            <TableSummary fixed>
              <TableSummaryRow v-if="showRules=='J'" style="background-color: #cccccc;" >
                <TableSummaryCell class="nc-summary" :index="0" colspan="5" :align="'center'" style="border-right: none;">合计</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{ summaryTotals.jieMoney }}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{ summaryTotals.daiMoney }}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;"/>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{ summaryTotals.yueMoney }}</TableSummaryCell>
              </TableSummaryRow>
              <TableSummaryRow v-if="showRules=='SJ'" style="background-color: #cccccc;" >
                <TableSummaryCell class="nc-summary" :index="0" colspan="5" :align="'center'" style="border-right: none;">合计</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{ summaryTotals.jiewb }}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{ summaryTotals.daiwb }}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;"/>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{ summaryTotals.yuewb }}</TableSummaryCell>
              </TableSummaryRow>
              <TableSummaryRow v-if="showRules=='WJ'" style="background-color: #cccccc;" >
                <TableSummaryCell class="nc-summary" :index="0" colspan="5" :align="'center'" style="border-right: none;">合计</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{ summaryTotals.jiewb }}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{ summaryTotals.jieMoney }}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{ summaryTotals.daiwb }}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{ summaryTotals.daiMoney }}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;"/>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{ summaryTotals.yuewb }}</TableSummaryCell>
                <TableSummaryCell class="nc-summary" :index="0" :align="'right'" style="border-right: none;">{{ summaryTotals.yueMoney }}</TableSummaryCell>
              </TableSummaryRow>
            </TableSummary>
          </template>
        </a-table>
        <div class="pagination-text" :style="{top: (tableHeight+90)+'px'}" v-show="showPageNumber">
          {{`共 ${pageNumber}条记录&emsp;每页 1000 条`}}
        </div>
      </div>
    </div>
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import {
  initPageBasicDataByColumns,
  initPageBasicData,
  initDynamics,
  changeDefaultDynamics,
  initPageBasicDataMX,
} from './data'

import Edit from './popup/query.vue'
import Print from './popup/print.vue'
import {useModal} from '/@/components/Modal'
import {PageWrapper} from '/@/components/Page'
import {computed, reactive, ref, onMounted, onBeforeUnmount, watch} from 'vue';
import {cloneDeep} from 'lodash-es';
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Upload as AUpload,
  Table as ATable,
  Checkbox as ACheckbox,
  Popconfirm as APopconfirm, Table
} from "ant-design-vue"

const ARangePicker = ADatePicker.RangePicker
const ACheckboxGroup = ACheckbox.Group
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const TableSummary = Table.Summary
const TableSummaryRow = Table.Summary.Row
const TableSummaryCell = Table.Summary.Cell
import {
  SortDescendingOutlined,
  SortAscendingOutlined,
  CheckOutlined,
  OrderedListOutlined,
  FormOutlined,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  PieChartFilled, FilterFilled,
  EditOutlined
} from '@ant-design/icons-vue'

import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {getCurrentAccountName, getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {usePingZhengFillinStoreWidthOut} from "/@/store/modules/boozsoft/pingzheng-fillin";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findByUniqueCodeOrderByInid} from "/@/api/record/system/accvoucher";

const {closeCurrent} = useTabs(router);

const {createErrorModal} = useMessage()

const pageParameter = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '北京希格科技',
  ifUnit: false,
})

/* 对接 查看凭证*/
const pingzhengEditor = usePingZhengFillinStoreWidthOut()
const showPingZheng = async (row: any) => {
  if (row.uniqueCode != null && row.uniqueCode != '') {
    const res = await useRouteApi(findByUniqueCodeOrderByInid, {schemaName: dynamicTenantId})(row.uniqueCode)
    pingzhengEditor.openPingZhengShowEditer(res)
  }
}

const [registerPrintPage, {openModal: openPrintPage}] = useModal()
const openPrint = () => {
  openPrintPage(true, {
    data: {
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value,
      year: year.value,
    }
  })
}
import {useNewPrint} from "/@/utils/boozsoft/print/print";
import {tableStyle} from "/@/store/modules/abc-print";

function printData(obj) {
  loadMark.value = true
  let qijian = obj.strDate.replace(/-/g, '.') + " - " + obj.endDate.replace(/-/g, '.')
  let printList: any = []
  let kemuName = kemu.value
  let wb = ''
  accountList.value.forEach((res: any) => {
    if (res.ccode == kemu.value) {
      kemuName = res.ccode + '-' + res.ccodeName
      if (res.currency == '1') {
        wb = res.currencyType
      }
    }
  })
  let printUser = ''
  if(obj.printUser){
    printUser = '制表人：' + useUserStore().getUserInfo['name']
  }
  let printBz = ''
  if(obj.printBz){
    printBz = '币种：'+biZhong.value
  }
  if (showRules.value == 'J') {//金额式
    let jieDayMoney = 0
    let daiDayMoney = 0
    dataArr.value.forEach((item,index) => {
      let item1 = {}
      item1[0] = item.idate
      item1[1] = item.pzNum
      item1[2] = setString(item.zhaiyao,39)
      item1[3] = setString(item.dfkemu,19)
      item1[4] = item.jieMoney
      item1[5] = item.daiMoney
      item1[6] = item.fangxiang
      item1[7] = item.yueMoney
      printList.push(item1)

      if (item.zhaiyao!='本日合计' && item.zhaiyao!='本月合计' && item.zhaiyao!='本年累计'){
        let jieMoney = item.jieMoney==''?0:item.jieMoney.replace(/,/g, '')
        let daiMoney = item.daiMoney==''?0:item.daiMoney.replace(/,/g, '')
        jieDayMoney = add(jieDayMoney,jieMoney)
        daiDayMoney = add(daiDayMoney,daiMoney)
        if ((index)>0 && (index)%26==0){
          let item2 = {}
          item2[0] = ''
          item2[1] = ''
          item2[2] = '承前页'
          item2[3] = ''
          item2[4] = toThousandFilter(jieDayMoney)
          item2[5] = toThousandFilter(daiDayMoney)
          item2[6] = item.fangxiang
          item2[7] = item.yueMoney
          printList.push(item2)
        }
      } else {
        jieDayMoney = 0
        daiDayMoney = 0
        if ((index)>0 && (index)%26==0 && index<dataArr.value.length-1) {
          let item2 = {}
          item2[0] = ''
          item2[1] = ''
          item2[2] = '承前页'
          item2[3] = ''
          item2[4] = item.jieMoney
          item2[5] = item.daiMoney
          item2[6] = item.fangxiang
          item2[7] = item.yueMoney
          printList.push(item2)
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
      printList.push(item1)
    }
    let num = Math.ceil(printList.length/27)
    useNewPrint({data: ['l', 'px', 'a4', true]}, (doc) => {
      loadMark.value = false
      doc.autoTable({
        head: [['', '', '科目日记账', '', '', '', '', ''],
          ['会计科目：' + kemuName, '', '', '期间:' + qijian, '', printBz, '', '单位：元'],
          ['日期', '凭证字号', '摘要', '对方科目', '借方', '贷方', '方向', '余额']],
        body: printList,
        // startY: 60,
        styles: tableStyle(),
        margin: {
          left: 25,
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
            '核算单位：' + pageParameter.companyName,
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
          // data.cell.styles.bold = false

          if (data.section == 'head' && data.row.index == 0) {

            data.cell.styles.fontSize = 20
            data.cell.styles.fontStyle = 'bold'
            data.cell.styles.lineColor = [255, 255, 255]
            if (data.column.index == 2) {
              data.cell.colSpan = 4
              data.cell.styles.halign = 'center'
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
              data.cell.styles.cellPadding = {top: 3, left: 5, right: 2, bottom: 2}
              data.cell.colSpan = 2
              data.cell.styles.halign = 'left'
            } else if (data.column.index == 5) {
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
          0: {maxHeight: 10, cellWidth: 50, halign: 'left'},
          1: {cellWidth: 40, halign: 'center'},
          2: {cellWidth: 150, halign: 'left'},
          3: {cellWidth: 80, halign: 'left'},
          4: {cellWidth: 80, halign: 'right'},
          5: {cellWidth: 80, halign: 'right'},
          6: {cellWidth: 20, halign: 'center'},
          7: {cellWidth: 80, halign: 'right'},
        }
      })
    })
  } else if(showRules.value == 'SJ') {//外币式
    if (wb==''){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '该现金科目没有外币核算，不能进行打印！'
      })
      loadMark.value = false
      return false
    }
    printBz = '币种：'+wb
    let jieDayMoney = 0
    let daiDayMoney = 0
    dataArr.value.forEach((item,index) => {
      let item1 = {}
      item1[0] = item.idate
      item1[1] = item.pzNum
      item1[2] = setString(item.zhaiyao,39)
      item1[3] = setString(item.dfkemu,19)
      item1[4] = item.jiewb
      item1[5] = item.daiwb
      item1[6] = item.fangxiang
      item1[7] = item.yuewb
      printList.push(item1)

      if (item.zhaiyao!='本日合计' && item.zhaiyao!='本月合计' && item.zhaiyao!='本年累计'){
        let jieMoney = item.jiewb==''?0:item.jiewb.replace(/,/g, '')
        let daiMoney = item.daiwb==''?0:item.daiwb.replace(/,/g, '')
        jieDayMoney = add(jieDayMoney,jieMoney)
        daiDayMoney = add(daiDayMoney,daiMoney)
        if ((index)>0 && (index)%26==0){
          let item2 = {}
          item2[0] = ''
          item2[1] = ''
          item2[2] = '承前页'
          item2[3] = ''
          item2[4] = toThousandFilter(jieDayMoney)
          item2[5] = toThousandFilter(daiDayMoney)
          item2[6] = item.fangxiang
          item2[7] = item.yuewb
          printList.push(item2)
        }
      } else {
        jieDayMoney = 0
        daiDayMoney = 0
        if ((index)>0 && (index)%26==0 && index<dataArr.value.length-1) {
          let item2 = {}
          item2[0] = ''
          item2[1] = ''
          item2[2] = '承前页'
          item2[3] = ''
          item2[4] = item.jiewb
          item2[5] = item.daiwb
          item2[6] = item.fangxiang
          item2[7] = item.yuewb
          printList.push(item2)
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
      printList.push(item1)
    }
    let num = Math.ceil(printList.length/27)
    useNewPrint({data: ['l', 'px', 'a4', true]}, (doc) => {
      loadMark.value = false
      doc.autoTable({
        head: [['', '', '科目日记账', '', '', '', '', ''],
          ['会计科目：' + kemuName, '', '', '期间:' + qijian, '', printBz, '', '单位：'+wb],
          ['日期', '凭证字号', '摘要', '对方科目', '外币借方', '外币贷方', '方向', '外币余额']],
        body: printList,
        // startY: 60,
        styles: tableStyle(),
        margin: {
          left: 25,
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
            '核算单位：' + pageParameter.companyName,
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
          // data.cell.styles.bold = false

          if (data.section == 'head' && data.row.index == 0) {

            data.cell.styles.fontSize = 20
            data.cell.styles.fontStyle = 'bold'
            data.cell.styles.lineColor = [255, 255, 255]
            if (data.column.index == 2) {
              data.cell.colSpan = 4
              data.cell.styles.halign = 'center'
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
              data.cell.styles.cellPadding = {top: 3, left: 5, right: 2, bottom: 2}
              data.cell.colSpan = 2
              data.cell.styles.halign = 'left'
            } else if (data.column.index == 5) {
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
          0: {maxHeight: 10, cellWidth: 50, halign: 'left'},
          1: {cellWidth: 40, halign: 'center'},
          2: {cellWidth: 150, halign: 'left'},
          3: {cellWidth: 80, halign: 'left'},
          4: {cellWidth: 80, halign: 'right'},
          5: {cellWidth: 80, halign: 'right'},
          6: {cellWidth: 20, halign: 'center'},
          7: {cellWidth: 80, halign: 'right'},
        }
      })
    })
  } else if(showRules.value == 'WJ') {//外币金额式
    let jieDayMoney = 0
    let daiDayMoney = 0
    let wbJieDayMoney = 0
    let wbDaiDayMoney = 0
    dataArr.value.forEach((item,index) => {
      let item1 = {}
      item1[0] = item.idate
      item1[1] = item.pzNum
      item1[2] = setString(item.zhaiyao,17)
      item1[3] = setString(item.dfkemu,15)
      item1[4] = item.jieMoney
      item1[5] = item.jiewb
      item1[6] = item.daiMoney
      item1[7] = item.daiwb
      item1[8] = item.fangxiang
      item1[9] = item.yueMoney
      item1[10] = item.yuewb
      printList.push(item1)

      if (item.zhaiyao!='本日合计' && item.zhaiyao!='本月合计' && item.zhaiyao!='本年累计'){
        let jieMoney = item.jieMoney==''?0:item.jieMoney.replace(/,/g, '')
        let daiMoney = item.daiMoney==''?0:item.daiMoney.replace(/,/g, '')
        jieDayMoney = add(jieDayMoney,jieMoney)
        daiDayMoney = add(daiDayMoney,daiMoney)
        let wbJieMoney = item.jiewb==''?0:item.jiewb.replace(/,/g, '')
        let wbDaiMoney = item.daiwb==''?0:item.daiwb.replace(/,/g, '')
        wbJieDayMoney = add(wbJieDayMoney,wbJieMoney)
        wbDaiDayMoney = add(wbDaiDayMoney,wbDaiMoney)
        if ((index)>0 && (index)%25==0){
          let item2 = {}
          item2[0] = ''
          item2[1] = ''
          item2[2] = '承前页'
          item2[3] = ''
          item2[4] = toThousandFilter(jieDayMoney)
          item2[5] = toThousandFilter(wbJieDayMoney)
          item2[6] = toThousandFilter(daiDayMoney)
          item2[7] = toThousandFilter(wbDaiDayMoney)
          item2[8] = item.fangxiang
          item2[9] = item.yueMoney
          item2[10] = item.yuewb
          printList.push(item2)
        }
      } else {
        jieDayMoney = 0
        daiDayMoney = 0
        if ((index)>0 && (index)%25==0 && index<dataArr.value.length-1) {
          let item2 = {}
          item2[0] = ''
          item2[1] = ''
          item2[2] = '承前页'
          item2[3] = ''
          item2[4] = item.jieMoney
          item2[5] = item.jiewb
          item2[6] = item.daiMoney
          item2[7] = item.daiwb
          item2[8] = item.fangxiang
          item2[9] = item.yueMoney
          item2[10] = item.yuewb
          printList.push(item2)
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
      printList.push(item1)
    }
    let num = Math.ceil(printList.length/26)
    useNewPrint({data: ['l', 'px', 'a4', true]}, (doc) => {
      loadMark.value = false
      doc.autoTable({
        head: [['', '', '', '', '科目日记账', '', '', '', '','',''],
          ['会计科目：' + kemuName, '', '', '', '期间:' + qijian, '', '', '', printBz,'','单位：元'],
          ['日期', '凭证字号', '摘要', '对方科目', '借方','', '贷方','', '余额', '',''],
          ['', '', '', '', '本币','外币', '本币','外币', '方向', '本币','外币']],
        body: printList,
        // startY: 60,
        styles: tableStyle(),
        margin: {
          left: 25,
          top: 20,
          bottom: 20
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
            '核算单位：' + pageParameter.companyName,
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
          // data.cell.styles.bold = false

          if (data.section == 'head' && data.row.index == 0) {

            data.cell.styles.fontSize = 20
            data.cell.styles.fontStyle = 'bold'
            data.cell.styles.lineColor = [255, 255, 255]
            if (data.column.index == 4) {
              data.cell.colSpan = 3
              data.cell.styles.halign = 'center'
            }
          }
          if (data.section == 'head' && data.row.index == 1) {
            data.cell.styles.fontSize = 10
            data.cell.styles.fontStyle = 'bold'
            data.cell.styles.lineColor = [255, 255, 255]
            if (data.column.index == 0) {
              data.cell.colSpan = 4
              data.cell.styles.halign = 'left'
            } else if (data.column.index == 4) {
              data.cell.colSpan = 3
              data.cell.styles.halign = 'center'
            } else if (data.column.index == 8) {
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
            if (data.column.index == 0 || data.column.index == 1 || data.column.index == 2 || data.column.index == 3){
              data.cell.styles.valign = 'middle'
              data.cell.rowSpan=2
            }
            if (data.column.index == 4 || data.column.index == 6){
              data.cell.colSpan=2
            }
            if (data.column.index == 8){
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
          0: {maxHeight: 10, cellWidth: 45, halign: 'left'},
          1: {cellWidth: 35, halign: 'center'},
          2: {cellWidth: 80, halign: 'left'},
          3: {cellWidth: 50, halign: 'left'},
          4: {cellWidth: 60, halign: 'right'},
          5: {cellWidth: 60, halign: 'right'},
          6: {cellWidth: 60, halign: 'right'},
          7: {cellWidth: 60, halign: 'right'},
          8: {cellWidth: 20, halign: 'center'},
          9: {cellWidth: 60, halign: 'right'},
          10: {cellWidth: 60, halign: 'right'},
        }
      })
    })
  }
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
  return (+num || 0).toFixed(2).replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,')
}

const tableHeight = (window.innerHeight - 350)
const [registerEditPage, {openModal: openEditPage}] = useModal()
const val = {
  id: '',
  qrCode: '',
  checkDate: '',
  checkType: '',
  checkNum: '',
  checkName: '',
  payName: '',
  payAccount: '',
  payeeName: '',
  purpose: '',
  amount: '',
  encryCode: '',
  voucherNumber: '',
  openOne: 0
}
const showMingXi = ref(true);
const loadMark = ref(true);
const reloadMark = ref(false);
const showRules = ref('J')
const showRulesSize = ref('MIN')
const kemu = ref('')
const thisInterval = ref('2020-01 ~ 2021-12')
const biZhong = ref('人民币')
const dataArr = ref([
  {
    key: '',
    number: '',
    idate: '',
    pzNum: '',
    zhaiyao: '',
    jiesuan: '',
    piaohao: '',
    jieMoney: '',
    jiewb: '',
    daiMoney: '',
    daiwb: '',
    fangxiang: '',
    yueMoney: '',
    yuewb: '',
    dfkemu: '',
    dfUnit: ''
  }
]);
const condClick = (data: any, index: any, e: any) => {
  console.log(index)
  if (e.target.cellIndex == 1) {
    openEditPage(true, {
      data: data
    })
  }
}
const openAddPage = () => {
  val.openOne = 0
  openEditPage(true, {
    data: val
  })
}
const TableInfo = ref(null)

const openEdit = (data: any) => {
  openEditPage(true, {
    data: data
  })
}
onMounted(() => {
  val.openOne = 1
  openEditPage(true, {
    data: val
  })
  loadMark.value = true
})
const del = async (data: any) => {
  alert('删除成功！')
}
const accountList = ref([])
const thisQueryCondition: any = ref([])
const year = ref(useCompanyOperateStoreWidthOut().getLoginDate.slice(0, 4))

const showPageNumber=ref(false)
const pageNumber=ref(0)
async function saveData(data: any) {
  // 查询条件
  loadMark.value = true
  dynamicTenantId.value = data.constant.dynamicTenantId
  defaultAdName.value = data.constant.defaultAdName
  year.value = data.constant.year
  biZhong.value = data.constant.currency
  kemu.value = data.variable.accountNum
  showRulesSize.value = data.constant.tableFontSize
  thisInterval.value = ('' == data.variable.periodStart) ?
    data.variable.dateStart + ' ~ ' + data.variable.dateEnd : data.variable.periodStart + ' ~ ' + data.variable.periodEnd
  data.ccode = data.variable.accountNum
  accountList.value = data.accountList
  data.accountList = null
  dataArr.value = await initPageBasicData(data, dynamicTenantId.value)
  pageNumber.value=dataArr.value.length
  showPageNumber.value=true
  if(dataArr.value.length<50){
    for (let i =  dataArr.value.length; i < 50; i++) {
      dataArr.value.push({
        key: 'booz',
        number: dataArr.value.length+1,
        idate: '',
        pzNum: '',
        zhaiyao: '',
        jiesuan: '',
        piaohao: '',
        jieMoney: '',
        jiewb: '',
        daiMoney: '',
        daiwb: '',
        fangxiang: '',
        yueMoney: '',
        yuewb: '',
        dfkemu: '',
        dfUnit: ''
      })
    }
  }

  calculateTotal()
  thisQueryCondition.value = data
  getThisAdInfoData({'accId': defaultAdName.value}).then(res => {
    if (null != res) {
      pageParameter.companyName = res.accName
    }
  })
  loadMark.value = false
}

const summaryTotals = ref({})
const calculateTotal = () => {
  let list = dataArr.value.filter(a=>a.key!='booz')
  if (list.length == 0){
    summaryTotals.value = {}
    return false;
  }
  let jiewb = 0
  let daiwb = 0
  let jieMoney = 0
  let daiMoney = 0
  let yueMoney = 0
  let yuewb = 0
  for (let i = 0; i < list.length; i++) {
    const e = list[i];
    jieMoney += parseFloat(e.jieMoney || '0')
    daiMoney += parseFloat(e.daiMoney || '0')
    yueMoney += parseFloat(e.yueMoney || '0')
    jiewb += parseFloat(e.jiewb || '0')
    daiwb += parseFloat(e.daiwb || '0')
    yuewb += parseFloat(e.yuewb || '0')
  }
  summaryTotals.value={
    jiewb: formatData(jiewb),
    daiwb: formatData(daiwb),
    yuewb: formatData(yuewb),
    jieMoney: formatData(jieMoney),
    daiMoney: formatData(daiMoney),
    yueMoney: formatData(yueMoney)
  }
}
function formatData(data:any){
  if(data==''||data==null||parseFloat(data)==0){return }
  let str = ""
  if(data){
    // 千分位保留2位小数
    var source = String(data.toFixed(2)).split("."); //按小数点分成2部分
    source[0] = source[0].replace( new RegExp('(\\d)(?=(\\d{3})+$)' , 'ig'), "$1,");//只将整数部分进行都好分割
    str = source.join("." );//再将小数部分合并进来
  }
  return str;
}
const queryChange = async () => {
  if (null !== thisQueryCondition.value) {
    thisQueryCondition.value.ccode = kemu.value
    thisQueryCondition.value.variable.accountNum = kemu.value
    dataArr.value = await initPageBasicData(thisQueryCondition.value, dynamicTenantId.value)
  }
}

async function onSearch(data: any) {
// 查询条件
}

const dynamicColumns = initDynamics().DEFAULT

const dynamicColumnData: any = ref(initDynamics().DATA)

const count = computed(() => dynamicColumnData.value.length + 1);
const editableData = reactive({});

const edit = (key: string) => {
  if (key.toString().indexOf('-') != -1) {
    let arr: any = key.split('-');
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
    let arr: any = key.split('-');
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
    let arr: any = key.split('-');
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
const confirm = (e: MouseEvent) => {
  // 调整数据库 列参数
  changeDefaultDynamics(dynamicColumnData.value)
  // 重新获取数据
  tableReload()
};

function tableRow(row: any, num: any) {
  let str = ''
  if (row.zhaiyao == '本日合计'){
    str = 'table-day-striped'
  } else if (row.zhaiyao == '本月合计'){
    str = 'table-month-striped'
  } else if (row.zhaiyao == '本年累计'){
    str = 'table-year-striped'
  }
  if (num % 2 === 1) {
    str = str + ' table-striped'
  }
  return str
  /*return (row.zhaiyao == '本日合计' ? 'table-day-striped' : null)
    || (row.zhaiyao == '本月合计' ? 'table-month-striped' : null)
    || (row.zhaiyao == '本年累计' ? 'table-year-striped' : null)
    || (num % 2 === 1 ? 'table-striped' : null)*/
}

const cancel = (e: MouseEvent) => {
  // 恢复默认
  dynamicColumnData.value = []
  dynamicColumnData.value = initDynamics().DATA
};

const tableReload = () => {
  loadMark.value = true
  reloadMark.value = !reloadMark.value
  setTimeout(() => {
    loadMark.value = false
  }, 1000)
}

//导出Excel
import XLSX from "xlsx-js-style";
import {
  sheet_from_array_of_arrays, Workbook,
  writeExcel, defaultV
} from "/@/utils/boozsoft/excel/excel2";
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {useUserStore} from "/@/store/modules/user";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {useMessage} from "/@/hooks/web/useMessage";
async function exportExcel() {
  let kemuName = kemu.value
  let waibi = ''
  accountList.value.forEach((res: any) => {
    if (res.ccode == kemu.value) {
      kemuName = res.ccode + '-' + res.ccodeName
      if (res.currency == '1') {
        waibi = res.currencyType
      }
    }
  })
  let list: any = []
  if (showRules.value == 'J') {//金额式
    dataArr.value.forEach(item => {
      let item1 = {}
      item1[0] = item.idate
      item1[1] = item.pzNum
      item1[2] = item.zhaiyao
      item1[3] = item.dfkemu
      item1[4] = item.jieMoney
      item1[5] = item.daiMoney
      item1[6] = item.fangxiang
      item1[7] = item.yueMoney
      list.push(item1)
    })
    let qijian = thisInterval.value.replace(/-/g, '.').replace('~', ' - ')
    const sheet = [
      {
        title: '科目日记账',
        multiHeader: [
          ['会计科目：' + kemuName, '', '期间:' + qijian,'', '', '', '单位：元', ''],
          ['日期', '凭证字号', '摘要', '对方科目', '借方', '贷方', '方向', '余额']
        ],
        table: list,
        foot: ['核算单位：' + pageParameter.companyName, '', '', '', '', '', '', ''],
        keys: [0, 1, 2, 3, 4, 5, 6, 7],
        merges: ['A2:B2','C2:F2','G2:H2'],
        sheetName: '科目日记账',
        cellStyle: [
          {
            cell: 'A1',
            font: {
              name: '宋体',
              sz: 14,
              color: {rgb: "000000"},
              bold: true,
            },
            /*fill: {
            fgColor: { rgb: "ff7e00" },
          }*/
            border: {color: {rgb: "ffffff"}}
          },
        ],
        colWidth: [8, 8, 25, 12, 12, 12, 5, 12]
      }
    ]
    // 处理数据前
    if (!sheet || sheet.length <= 0) {
      this.onError('Table data cannot be empty')
      return
    }
    const wb = Workbook()
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
        const titleLength = Math.max(tHeaderLength, multiHeaderLength, keys.length)
        // 第一个元素为title，剩余以空字符串填充
        title = [title].concat(Array(titleLength - 1).fill(''))
        // 处理标题的合并\
        const cell = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']
        let mergeSecond = 'A1'
        if (titleLength > 26) {
          const one = parseInt(titleLength / 26)
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
      if (foot || foot === '') {
        foot && data.push(foot);
        const str = ('A' + data.length) + (':H' + data.length)
        merges.push(str)
      }
      const ws = sheet_from_array_of_arrays(data);
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
                  color: { rgb: "000000" },
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
                    color: { rgb: "000000" },
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
                    color: { rgb: "000000" },
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
                  color: { rgb: "000000" },
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
            } else if (i.substring(0, 1) == 'C') {
              dataInfo[i.toString()].s = {
                border: border === {} ? border : border || globalStyle.border,
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "left",
                }
              }
            } else if (i.substring(0, 1) == 'D') {
              dataInfo[i.toString()].s = {
                border: border === {} ? border : border || globalStyle.border,
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "left",
                }
              }
            } else if (i.substring(0, 1) == 'E') {
              dataInfo[i.toString()].s = {
                border: border === {} ? border : border || globalStyle.border,
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "right",
                }
              }
            } else if (i.substring(0, 1) == 'F') {
              dataInfo[i.toString()].s = {
                border: border === {} ? border : border || globalStyle.border,
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "right",
                }
              }
            } else if (i.substring(0, 1) == 'H') {
              dataInfo[i.toString()].s = {
                border: border === {} ? border : border || globalStyle.border,
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "right",
                }
              }
            }
            if (foot.length > 0) {
              if (i.substring(1) == data.length) {
                dataInfo[i.toString()].s = {
                  border: {},
                  font: {
                    name: '宋体',
                    sz: 10,
                    color: { rgb: "000000" },
                    bold: true,
                  },
                  fill: fill || globalStyle.fill,
                  alignment: {
                    horizontal: "left",
                  }
                }
              }
            }
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
    writeExcel(wb, bookType, pageParameter.companyName + '_' + thisInterval.value + '_科目日记账')
  } else if(showRules.value == 'SJ') {//外币式
    if (waibi==''){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '该银行科目没有外币核算，不能进行导出！'
      })
      loadMark.value = false
      return false
    }
    dataArr.value.forEach(item => {
      let item1 = {}
      item1[0] = item.idate
      item1[1] = item.pzNum
      item1[2] = item.zhaiyao
      item1[3] = item.dfkemu
      item1[4] = item.jiewb
      item1[5] = item.daiwb
      item1[6] = item.fangxiang
      item1[7] = item.yuewb
      list.push(item1)
    })
    let qijian = thisInterval.value.replace(/-/g, '.').replace('~', ' - ')
    const sheet = [
      {
        title: '科目日记账',
        multiHeader: [
          ['会计科目：' + kemuName, '', '', '期间:' + qijian, '', '', '单位：'+waibi, ''],
          ['日期', '凭证字号', '摘要', '对方科目', '外币借方', '外币贷方', '方向', '外币余额']
        ],
        table: list,
        foot: ['核算单位：' + pageParameter.companyName, '', '', '', '', '', '', ''],
        keys: [0, 1, 2, 3, 4, 5, 6, 7, 8],
        merges: ['A2:C2','C2:F2','G2:H2'],
        sheetName: '科目日记账',
        cellStyle: [
          {
            cell: 'A1',
            font: {
              name: '宋体',
              sz: 14,
              color: {rgb: "000000"},
              bold: true,
            },
            /*fill: {
            fgColor: { rgb: "ff7e00" },
          }*/
            border: {color: {rgb: "ffffff"}}
          },
        ],
        colWidth: [8, 8, 25, 12, 12, 12, 12, 5, 12]
      }
    ]
    // 处理数据前
    if (!sheet || sheet.length <= 0) {
      this.onError('Table data cannot be empty')
      return
    }
    const wb = Workbook()
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
        const titleLength = Math.max(tHeaderLength, multiHeaderLength, keys.length)
        // 第一个元素为title，剩余以空字符串填充
        title = [title].concat(Array(titleLength - 1).fill(''))
        // 处理标题的合并\
        const cell = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']
        let mergeSecond = 'A1'
        if (titleLength > 26) {
          const one = parseInt(titleLength / 26)
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
      if (foot || foot === '') {
        foot && data.push(foot);
        const str = ('A' + data.length) + (':H' + data.length)
        merges.push(str)
      }
      const ws = sheet_from_array_of_arrays(data);
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
                  color: { rgb: "000000" },
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
                    color: { rgb: "000000" },
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
                    color: { rgb: "000000" },
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
                  color: { rgb: "000000" },
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
            } else if (i.substring(0, 1) == 'C') {
              dataInfo[i.toString()].s = {
                border: border === {} ? border : border || globalStyle.border,
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "left",
                }
              }
            } else if (i.substring(0, 1) == 'D') {
              dataInfo[i.toString()].s = {
                border: border === {} ? border : border || globalStyle.border,
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "left",
                }
              }
            } else if (i.substring(0, 1) == 'E') {
              dataInfo[i.toString()].s = {
                border: border === {} ? border : border || globalStyle.border,
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "right",
                }
              }
            } else if (i.substring(0, 1) == 'F') {
              dataInfo[i.toString()].s = {
                border: border === {} ? border : border || globalStyle.border,
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "right",
                }
              }
            } else if (i.substring(0, 1) == 'H') {
              dataInfo[i.toString()].s = {
                border: border === {} ? border : border || globalStyle.border,
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "right",
                }
              }
            }
            if (foot.length > 0) {
              if (i.substring(1) == data.length) {
                dataInfo[i.toString()].s = {
                  border: {},
                  font: {
                    name: '宋体',
                    sz: 10,
                    color: { rgb: "000000" },
                    bold: true,
                  },
                  fill: fill || globalStyle.fill,
                  alignment: {
                    horizontal: "left",
                  }
                }
              }
            }
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
    writeExcel(wb, bookType, pageParameter.companyName + '_' + thisInterval.value + '_科目日记账')
  } else if(showRules.value == 'WJ') {//外币金额式
    dataArr.value.forEach(item => {
      let item1 = {}
      item1[0] = item.idate
      item1[1] = item.pzNum
      item1[2] = item.zhaiyao
      item1[3] = item.dfkemu
      item1[4] = item.jieMoney
      item1[5] = item.jiewb
      item1[6] = item.daiMoney
      item1[7] = item.daiwb
      item1[8] = item.fangxiang
      item1[9] = item.yueMoney
      item1[10] = item.yuewb
      list.push(item1)
    })
    let qijian = thisInterval.value.replace(/-/g, '.').replace('~', ' - ')
    const sheet = [
      {
        title: '科目日记账',
        multiHeader: [
          ['会计科目：' + kemuName, '', '', '', '期间:' + qijian, '', '', '', '单位：元', '', ''],
          ['日期', '凭证字号', '摘要', '对方科目', '借方','', '贷方', '', '方向', '余额', ''],
          ['', '', '', '', '本币','外币', '本币', '外币', '方向', '本币', '外币']
        ],
        table: list,
        foot: ['核算单位：' + pageParameter.companyName, '', '', '', '', '', '', '', '', '', ''],
        keys: [0, 1, 2, 3, 4, 5, 6, 7, 8,9,10],
        merges: ['A2:D2', 'E2:F2', 'I2:K2','A3:A4','B3:B4','C3:C4','D3:D4','E3:F3','G3:H3','I3:K3'],
        sheetName: '科目日记账',
        cellStyle: [
          {
            cell: 'A1',
            font: {
              name: '宋体',
              sz: 14,
              color: {rgb: "000000"},
              bold: true,
            },
            /*fill: {
            fgColor: { rgb: "ff7e00" },
          }*/
            border: {color: {rgb: "ffffff"}}
          },
        ],
        colWidth: [8, 8, 25, 12, 12, 12, 12, 12, 5, 12,12]
      }
    ]
    // 处理数据前
    if (!sheet || sheet.length <= 0) {
      this.onError('Table data cannot be empty')
      return
    }
    const wb = Workbook()
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
        const titleLength = Math.max(tHeaderLength, multiHeaderLength, keys.length)
        // 第一个元素为title，剩余以空字符串填充
        title = [title].concat(Array(titleLength - 1).fill(''))
        // 处理标题的合并\
        const cell = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']
        let mergeSecond = 'A1'
        if (titleLength > 26) {
          const one = parseInt(titleLength / 26)
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
      if (foot || foot === '') {
        foot && data.push(foot);
        const str = ('A' + data.length) + (':K' + data.length)
        merges.push(str)
      }
      const ws = sheet_from_array_of_arrays(data);
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
              if (i == 'E2') {
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
              if (i == 'I2') {
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
            } else if (i.substring(1) == '3' || i.substring(1) == '4') {
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
            } else if (i.substring(0, 1) == 'A' || i.substring(0, 1) == 'C' || i.substring(0, 1) == 'D') {
              dataInfo[i.toString()].s = {
                border: border === {} ? border : border || globalStyle.border,
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "left",
                }
              }
            } else if (i.substring(0, 1) == 'E' || i.substring(0, 1) == 'F' || i.substring(0, 1) == 'G' || i.substring(0, 1) == 'H' || i.substring(0, 1) == 'J' || i.substring(0, 1) == 'K') {
              dataInfo[i.toString()].s = {
                border: border === {} ? border : border || globalStyle.border,
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "right",
                }
              }
            }
            if (foot.length > 0) {
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
            }
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
    writeExcel(wb, bookType, pageParameter.companyName + '_' + thisInterval.value + '_科目日记账')
  }
}

const dynamicTenantId = ref(getCurrentAccountName(true))
const defaultAdName = ref(useCompanyOperateStoreWidthOut().getSchemaName)
const dynamicAdReload = async (obj) => {
  dynamicTenantId.value = obj.accountMode
  defaultAdName.value = obj.accId
  pageParameter.companyName = obj.companyName
}
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
<style scoped>
:deep(.ant-pagination) {
  margin-top: 0px;
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
}
:deep(.ant-popover-inner){
  width: 170px;
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

