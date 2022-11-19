<template>
  <div v-show="showEdit"
       style="position: absolute;left:0;top:0;z-index:100;width: 100%;height:100%;background:#b4c8e3;overflow:auto;">
    <div class="app-container" style="padding:10px;height: auto">
      <!--      <h1 style="padding:10px;height: 40px;background:#ffffff;text-align: center;font-size: 24px; ">
              利润表
            </h1>-->
      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">-->
        <div class="container-head-title" style="float: left;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 40%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 32px;">利润表</b>
        </div>

        <div class="ant-btn-group" style="display: flex;justify-content : flex-end;">
          <!--            <button
                        type="button"
                        class="ant-btn ant-btn-me"
                        @click="openPage"
                      ><span>生成</span></button>
                      <button
                        type="button"
                        class="ant-btn ant-btn-me"
                        @click="handleOk()"
                      ><span>保存</span></button>-->
          <!--          <a-popover class="ant-btn-default" placement="bottom">
                      <button
                        type="button"
                        class="ant-btn ant-btn-me"
                      ><span>导出</span></button>
                      <template #content>
                        <span @click="exportExcel()"
                              class="choose group-btn-span-special2 p_specifics">导出电子表格</span><br/>
                        <span @click="printDefaultPDF" class="choose group-btn-span-special2 p_specifics">导出PDF</span>
                      </template>
                    </a-popover>-->
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="exportExcel()"
          ><span>导出表格</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="printDefaultPDF"
          ><span>导出PDF</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="printData"
          ><span>打印</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="closed()"
          ><span>返回</span></button>
        </div>
      </div>

      <div style="clear: none"/>
      <div style="margin-top: -30px;margin-left: 90px;">
        <div style="display: inline-block;float: left;margin-top: -15px;">
          <AccountPicker theme="three" readonly/>
        </div>
        <div style="float: right;">
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
            <!--            <template #title>
                          <b>设置表格字号</b>
                        </template>-->
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
          <a-button class="ant-btn-me">
            <MailOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
        </div>
        <div style="clear:both"/>
      </div>
    </div>

    <div style="height:700px;overflow: auto !important;">
    <div class="app-container" style="padding:10px;height:1200px;overflow: auto !important;" v-for="item in itemConfig">
      <h1 style="text-align: center;font-size: 24px;margin-left:5%; ">
        利润表
      </h1>
      <div style="margin-left: 10%;margin-right: 10%;width: 80%;">
        <div style="float: left;padding-left: 50%;">{{ item.menu2 }}</div>
        <div style="float: right;">{{ item.menu6 }}</div>
      </div>
      <div style="margin-left: 10%;margin-right: 10%;display: flex;width: 80%;justify-content : space-between;">
        <div>{{ item.menu1 }}</div>
<!--        <div>{{ item.menu2 }}</div>-->
        <div>{{ item.menu3 }}</div>
      </div>
      <div style="margin-left: 10%;margin-right: 10%;display: flex;width: 80%;">
        <table
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
          id="tableId" style="height: calc( 100% - 20px );">
          <thead>
          <tr class="dy_table_thead_tr even">
            <th>项目</th>
            <th style="width: 50px;">行次</th>
            <th style="width: 200px;">本月金额</th>
            <th style="width: 200px;">本年累计金额</th>
          </tr>
          </thead>
          <tbody>
          <tr class="odd" v-for="row in item.table">
            <td style="text-align: left;padding-left:1em;" :title="row.columnShowName">
              <span v-if="row.jici=='1' || row.jici==''">{{ row.columnShowName }}</span>
              <span v-if="row.jici=='2'" style="padding-left:2em;">{{ row.columnShowName }}</span>
              <span v-if="row.jici=='3'" style="padding-left:4em;">{{ row.columnShowName }}</span>
              <span v-if="row.jici=='4'" style="padding-left:6em;">{{ row.columnShowName }}</span>
            </td>
            <td style="width: 50px;">{{ row.hangci }}</td>
            <td style="width: 200px;text-align: right;">{{ toThousandFilter(row.benyueMoney) }}</td>
            <td style="width: 200px;text-align: right;">{{
                toThousandFilter(row.bennianLeijiMoney)
              }}
            </td>
          </tr>
          </tbody>
        </table>
      </div>
      <div style="margin-left: 10%;margin-right: 10%;display: flex;width: 80%;display: flex;justify-content : space-between">
        <div>{{ item.menu4 }}</div>
        <div>{{ item.menu5 }}</div>
      </div>

      <Query
        @save="saveData"
        @register="registerEditPage"
      />
    </div>
    </div>
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import Query from './query.vue'
import {useModal} from "/@/components/Modal";
import {computed, getCurrentInstance, onMounted, reactive, ref, unref} from "vue";
import {findByColumn} from "/@/api/record/system/report-data";
import {
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
  FilterFilled,
  SortDescendingOutlined,
  CheckOutlined,
  SortAscendingOutlined,
  MailOutlined,
  ProfileOutlined
} from '@ant-design/icons-vue'
import {
  Select as ASelect,
  Input as AInput,
  List as AList,
  Row as ARow,
  Col as ACol,
  Card as ACard,
  Popover as APopover,
  Button as AButton,
  Tag as ATag
} from 'ant-design-vue'
import {useAbcTemplateStoreWidthOut} from "/@/store/modules/abc";
import {tableStyle} from "/@/store/modules/abc-print";
//import {jsPDF} from "jspdf";
const jsPDF=null
import {getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useMessage} from "/@/hooks/web/useMessage";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";

const {
  createConfirm
} = useMessage()

const showEdit: any = ref(false)
const emit = defineEmits(['register', 'save', 'close'])

function initValue() {
  return {
    open(data) {
      showEdit.value=true
      saveData(data)
    },
    close(){
      showEdit.value=false
    },
    instance:getCurrentInstance()
  }
}
//注册传值事件
emit('register',initValue())
//关闭编辑页
function closed(){
  showEdit.value=false
  emit('close')
}
const [registerEditPage, {openModal: openEditPage}] = useModal()
const openPage = () => {
  openEditPage(true, {})
}

/*onMounted(async() => {
  openPage()
})*/

const itemConfig: any = ref([])
const zcTable: any = ref([])
const fzTable: any = ref([])
const table: any = ref([])

const dynamicTenantId = ref()
const defaultAdName = ref()
const year = ref()
async function saveData(data) {
  dynamicTenantId.value = data.dynamicTenantId
  defaultAdName.value = data.defaultAdName
  year.value = data.year
  getThisAdInfoData({'accId': data.defaultAdName}).then(res => {
    if (null != res && res.independent == 0) {
      pageParameter.companyName = res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
  })
  //表头
  itemConfig.value = JSON.parse(JSON.stringify(data.data))
  //表体
  for (const res of itemConfig.value) {
    res.zcTable = []
    res.fzTable = []
    res.table = []
    const columnList = await useRouteApi(findByColumn,{schemaName: dynamicTenantId})(res.id)
    res.table = columnList
  }
}

//保存方法
async function handleOk() {
  emit('save', unref(itemConfig))
  closed()
}

const pageParameter = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '北京希格科技',
  ifUnit: false,
})

//打印
import {savePdf, useNewPrint} from "/@/utils/boozsoft/print/print";
async function printData() {
  useNewPrint({data: ['p', 'px', 'a4', true]}, (doc) => {
    itemConfig.value.forEach((item,index)=> {
      table.value = []
      table.value = item.table.map((column) => {
        const item1 = {}
        if (column.jici == '2') {
          item1[0] = '    ' + column.columnShowName
        } else if (column.jici == '3') {
          item1[0] = '        ' + column.columnShowName
        } else if (column.jici == '4') {
          item1[0] = '            ' + column.columnShowName
        } else {
          item1[0] = column.columnShowName
        }
        item1[1] = column.hangci
        item1[2] = toThousandFilter(column.benyueMoney)
        item1[3] = toThousandFilter(column.bennianLeijiMoney)
        return item1
      })
      doc.autoTable({
        head: [['利润表', '', '', ''],
          [item.menu2, '', '', item.menu6],
          [item.menu1, '', '', item.menu3],
          ['项目', '行次', '本月累计', '本年累计金额']],
        body: table.value,
        // startY: 60,
        styles: tableStyle(),
        margin: {left: 35},
        addPageContent: (data) => {
          //data.table.finalY 表格最大可容纳y轴坐标，超出时将根据设置决定是否另取一页.在页面需要分页时出现
          let tabHeigth = data.table.height,
            tabMarginTop = data.settings.margin.top,
            tabSize = data.table.finalY - tabMarginTop,//表格最大Y轴-表格顶部距离得到每页表格的最大值
            tabMarginLeft = data.settings.margin.left;
          if (data.table.finalY)//是否分页 有分页时才有该属性finalY
            if (data.pageNumber != Math.ceil(tabHeigth / tabSize)) return;//如果需要每一页都显示页尾则注释该行代码
          data.doc.setFontSize(10)
          doc.autoTableText(
            item.menu4,
            tabMarginLeft,
            data.cursor.y + 3,
            0
          );
          doc.autoTableText(
            item.menu5,
            data.cursor.x - 80,
            data.cursor.y + 3,
            0
          );
        },
        // didDrawPage(data) {
        //
        // },
        didParseCell(data) {
          // data.cell.styles.cellPadding = {top:10,left:2,right:4,bottom:9}
          data.cell.styles.cellPadding = {top: 4, left: 2, right: 1, bottom: 2}
          data.cell.styles.fillColor = [255, 255, 255]
          data.cell.styles.fontSize = 8

          if (data.section == 'head' && data.row.index == 0) {
            data.cell.styles.fontSize = 14
            data.cell.styles.bold = true
            data.cell.styles.lineColor = [255, 255, 255]
            // data.cell.colSpan = 4
            data.cell.styles.halign = 'center'
            if (data.column.index == 0) {
              data.cell.colSpan = 2
            }
          }
          if (data.section == 'head' && data.row.index == 1) {
            data.cell.styles.fontSize = 10
            data.cell.styles.lineColor = [255, 255, 255]
            data.cell.colSpan = 4
            data.cell.styles.halign = 'right'
          }
          if (data.section == 'head' && data.row.index == 2) {
            data.cell.styles.fontSize = 10
            data.cell.styles.lineColor = [255, 255, 255]
            if (data.column.index == 0) {
              data.cell.styles.halign = 'left'
            } else {
              data.cell.styles.halign = 'right'
            }
          }
          if (data.section == 'head' && data.row.index == 3) {
            data.cell.styles.cellPadding = {top: 7, left: 2, right: 2, bottom: 2}
            data.cell.styles.halign = 'center'
          }
          /*if (data.section == 'body' && data.column.index == 0) {
          if (itemConfig.value.table[data.row.index].jici == 2) {
            data.cell.styles.cellPadding = {top: 4, left: 10, right: 1, bottom: 2}
          }
          if (itemConfig.value.table[data.row.index].jici == 3) {
            data.cell.styles.cellPadding = {top: 4, left: 20, right: 1, bottom: 2}
          }
          if (itemConfig.value.table[data.row.index].jici == 4) {
            data.cell.styles.cellPadding = {top: 4, left: 30, right: 1, bottom: 2}
          }
        }*/

        },
        columnStyles: {
          0: {maxHeight: 10, cellWidth: 200, halign: 'left'},
          1: {cellWidth: 20, halign: 'center'},
          2: {cellWidth: 80, halign: 'right'},
          3: {cellWidth: 80, halign: 'right'},
        }
      })

      if (index < itemConfig.value.length - 1) {
        doc.addPage()
      }
    })

  })
}

// onMounted(async () => {
//   const res = await findByReportNameAndFlag("lrb")
//   const templateList: any = res.items
//   await saveData(templateList[0])
//   await printData()
// })

//导出PDF
async function printDefaultPDF() {
  let pdfName = '利润表_' + year.value + '年_'+pageParameter.companyName+'.pdf'
  savePdf(pdfName, {data: ['p', 'px', 'a4', true]}, (doc) => {
    itemConfig.value.forEach((item,index)=> {
      table.value = []
      table.value = item.table.map((column) => {
        const item1 = {}
        if (column.jici == '2') {
          item1[0] = '    ' + column.columnShowName
        } else if (column.jici == '3') {
          item1[0] = '        ' + column.columnShowName
        } else if (column.jici == '4') {
          item1[0] = '            ' + column.columnShowName
        } else {
          item1[0] = column.columnShowName
        }
        item1[1] = column.hangci
        item1[2] = toThousandFilter(column.benyueMoney)
        item1[3] = toThousandFilter(column.bennianLeijiMoney)
        return item1
      })
      doc.autoTable({
        head: [['利润表', '', '', ''],
          [item.menu2, '', '', item.menu6],
          [item.menu1, '', '', item.menu3],
          ['项目', '行次', '本月累计', '本年累计金额']],
        body: table.value,
        // startY: 60,
        styles: tableStyle(),
        margin: {left: 35},
        addPageContent: (data) => {
          //data.table.finalY 表格最大可容纳y轴坐标，超出时将根据设置决定是否另取一页.在页面需要分页时出现
          let tabHeigth = data.table.height,
            tabMarginTop = data.settings.margin.top,
            tabSize = data.table.finalY - tabMarginTop,//表格最大Y轴-表格顶部距离得到每页表格的最大值
            tabMarginLeft = data.settings.margin.left;
          if (data.table.finalY)//是否分页 有分页时才有该属性finalY
            if (data.pageNumber != Math.ceil(tabHeigth / tabSize)) return;//如果需要每一页都显示页尾则注释该行代码
          data.doc.setFontSize(10)
          doc.autoTableText(
            item.menu4,
            tabMarginLeft,
            data.cursor.y + 3,
            0
          );
          doc.autoTableText(
            item.menu5,
            data.cursor.x - 80,
            data.cursor.y + 3,
            0
          );
        },
        // didDrawPage(data) {
        //
        // },
        didParseCell(data) {
          // data.cell.styles.cellPadding = {top:10,left:2,right:4,bottom:9}
          data.cell.styles.cellPadding = {top: 4, left: 2, right: 1, bottom: 2}
          data.cell.styles.fillColor = [255, 255, 255]
          data.cell.styles.fontSize = 8

          if (data.section == 'head' && data.row.index == 0) {
            data.cell.styles.fontSize = 14
            data.cell.styles.bold = true
            data.cell.styles.lineColor = [255, 255, 255]
            // data.cell.colSpan = 4
            data.cell.styles.halign = 'center'
            if (data.column.index == 0) {
              data.cell.colSpan = 2
            }
          }
          if (data.section == 'head' && data.row.index == 1) {
            data.cell.styles.fontSize = 10
            data.cell.styles.lineColor = [255, 255, 255]
            data.cell.colSpan = 4
            data.cell.styles.halign = 'right'
          }
          if (data.section == 'head' && data.row.index == 2) {
            data.cell.styles.fontSize = 10
            data.cell.styles.lineColor = [255, 255, 255]
            if (data.column.index == 0) {
              data.cell.styles.halign = 'left'
            } else {
              data.cell.styles.halign = 'right'
            }
          }
          if (data.section == 'head' && data.row.index == 3) {
            data.cell.styles.cellPadding = {top: 7, left: 2, right: 2, bottom: 2}
            data.cell.styles.halign = 'center'
          }
          /*if (data.section == 'body' && data.column.index == 0) {
          if (itemConfig.value.table[data.row.index].jici == 2) {
            data.cell.styles.cellPadding = {top: 4, left: 10, right: 1, bottom: 2}
          }
          if (itemConfig.value.table[data.row.index].jici == 3) {
            data.cell.styles.cellPadding = {top: 4, left: 20, right: 1, bottom: 2}
          }
          if (itemConfig.value.table[data.row.index].jici == 4) {
            data.cell.styles.cellPadding = {top: 4, left: 30, right: 1, bottom: 2}
          }
        }*/

        },
        columnStyles: {
          0: {maxHeight: 10, cellWidth: 200, halign: 'left'},
          1: {cellWidth: 20, halign: 'center'},
          2: {cellWidth: 80, halign: 'right'},
          3: {cellWidth: 80, halign: 'right'},
        }
      })

      if (index < itemConfig.value.length - 1) {
        doc.addPage()
      }
    })

  })
}
/************************导出excel开始***************************************/
import XLSX from "xlsx-js-style";
import {
  sheet_from_array_of_arrays, Workbook,
  writeExcel, defaultV
} from "/@/utils/boozsoft/excel/excel2";

const bookType= 'xlsx'

function exportExcel () {
  const sheet:any = []
  itemConfig.value.forEach((item,index)=> {
    table.value = []
    table.value = item.table.map((column) => {
      const item1 = {}
      if (column.jici == '2') {
        item1[0] = '    ' + column.columnShowName
      } else if (column.jici == '3') {
        item1[0] = '        ' + column.columnShowName
      } else if (column.jici == '4') {
        item1[0] = '            ' + column.columnShowName
      } else {
        item1[0] = column.columnShowName
      }
      item1[1] = column.hangci
      item1[2] = toThousandFilter(column.benyueMoney)
      item1[3] = toThousandFilter(column.bennianLeijiMoney)
      return item1
    })
    sheet.push({
      title: '利润表',
      multiHeader: [
        [item.menu2, '', '', item.menu6],
        [item.menu1, '', '', item.menu3],
        ['项目', '行次', '本月累计', '本年累计金额']
      ],
      table: table.value,
      foot: [item.menu4, item.menu5, '', ''],
      keys: [0, 1, 2, 3],
      merges: ['A2:B2'],
      sheetName: item.dataCode,
      cellStyle: [
        {
          cell: 'A1',
          font: {
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
      colWidth: [30, 3, 12, 12]
    })
  })
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
        globalStyle[key] = { ...dgStyle[key], ...globalStyle[key] }
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
      const cell=['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z']
      let mergeSecond='A1'
      if(titleLength>26){
        const one=parseInt(titleLength/26)
        const two=titleLength%26
        mergeSecond=cell[one-1]+cell[two-1]+'1'
      }else{
        mergeSecond = cell[titleLength-1]+'1'
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
      const str = ('B'+data.length)+(':D'+data.length)
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
        return { wch: i }
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
          if (i.substring(1)=='1'){
            dataInfo[i.toString()].s = {
              border: {},
              font: font || globalStyle.font,
              alignment: alignment || globalStyle.alignment,
              fill: fill || globalStyle.fill
            }
          } else if (i.substring(1)=='2'){
            dataInfo[i.toString()].s = {
              border: {},
              font: font || globalStyle.font,
              fill: fill || globalStyle.fill,
              alignment: {
                horizontal: "right",
              }
            }
          } else if (i.substring(1)=='3'){
            dataInfo[i.toString()].s = {
              border: {},
              font: font || globalStyle.font,
              fill: fill || globalStyle.fill,
              alignment: {
                horizontal: "left",
              }
            }
            if (i=='D3'){
              dataInfo[i.toString()].s = {
                border: {},
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "right",
                }
              }
            }
          } else if (i.substring(1)=='4') {
            dataInfo[i.toString()].s = {
              border: border === {} ? border : border || globalStyle.border,
              font: font || globalStyle.font,
              alignment: alignment || globalStyle.alignment,
              fill: {fgColor: {rgb: "cccccc"}}
            }
          } else if (i.substring(0,1)=='A') {
            dataInfo[i.toString()].s = {
              border: border === {} ? border : border || globalStyle.border,
              font: font || globalStyle.font,
              fill: fill || globalStyle.fill,
              alignment: {
                horizontal: "left",
              }
            }
          } else if (i.substring(0,1)=='C') {
            dataInfo[i.toString()].s = {
              border: border === {} ? border : border || globalStyle.border,
              font: font || globalStyle.font,
              fill: fill || globalStyle.fill,
              alignment: {
                horizontal: "right",
              }
            }
          } else if (i.substring(0,1)=='D') {
            dataInfo[i.toString()].s = {
              border: border === {} ? border : border || globalStyle.border,
              font: font || globalStyle.font,
              fill: fill || globalStyle.fill,
              alignment: {
                horizontal: "right",
              }
            }
          }
          if (foot.length>0) {
            if (i.substring(1) == data.length) {
              dataInfo[i.toString()].s = {
                border: {},
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "left",
                }
              }
              if (i == 'B' + data.length) {
                dataInfo[i.toString()].s = {
                  border: {},
                  font: font || globalStyle.font,
                  fill: fill || globalStyle.fill,
                  alignment: {
                    horizontal: "right",
                  }
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
  // 类型默认为xlsx
  writeExcel(wb, bookType, '利润表_' + year.value + '年_'+pageParameter.companyName)
}
/************************导出excel结束**************************************/

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
function toThousandFilter(num:any) {
  if (num=='' || num==null){
    return ''
  }
  return (+num || 0).toFixed(2).replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,')
}
</script>

<style lang="less" scope>
@import "../../../../../assets/styles/layui.less";
@import "../../../../../assets/styles/theme.less";
@import "../global-menu-index.less";
</style>
<style type="text/css">

.even {
  background: #FCFCFC;
}

.odd {
  background: #FFFFFF;
}

td:hover {
  background: none;
}

td .bjgs {
  display: none;
}

td:hover .bjgs {
  display: block;
  position: absolute;
  top: 8px;
  left: 130px;
  /*background-color: whitesmoke;*/
  color: royalblue;
}

#tableId,#tableId1 {

  table-layout: fixed; /* 只有定义了表格的布局算法为fixed，下面td的定义才能起作用。 */
}

#tableId th,#tableId td,#tableId1 th,#tableId1 td {
  width: 100%;
  word-break: keep-all; /* 不换行 */
  white-space: nowrap; /* 不换行 */
  overflow: hidden; /* 内容超出宽度时隐藏超出部分的内容 */
  text-overflow: ellipsis; /* 当对象内文本溢出时显示省略标记(...) ；需与overflow:hidden;一起使用。*/
}

#tableId > thead,
#tableId > tbody > tr,
#tableId1 > thead,
#tableId1 > tbody > tr {
  display: table;
  width: 100%;
  height: 30px;
  table-layout: fixed; /**表格列的宽度由表格宽度决定，不由内容决定*/
  text-align: center;
}

#tableId th, #tableId1 th {
  border: 1px solid #6f696f;
}

#tableId tr td, #tableId1 tr td {
  border: 1px solid #6f696f;
  border-top: hidden;
}

a {
  cursor: pointer;
}

/*td缩进*/
.indent {
  text-indent: 30px;
}

.a-table-font-size-16 td,
.a-table-font-size-16 th {
  font-size: 16px !important;
  /*padding: 5px 8px !important;*/
}

.a-table-font-size-12 td,
.a-table-font-size-12 th {
  font-size: 14px !important;
  /*padding: 2px 8px !important;*/
}

.abc:hover {
  color: blue; /*DiV背景颜色*/
}

.choose {
  line-height: 30px;
  width: 120px !important;
}
.choose:hover {
  color: blue; /*DiV背景颜色*/
}
</style>
