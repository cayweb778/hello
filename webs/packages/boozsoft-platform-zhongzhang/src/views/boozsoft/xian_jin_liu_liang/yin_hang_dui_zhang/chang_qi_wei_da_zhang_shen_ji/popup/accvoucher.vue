<template>
  <div class="temp">
  <BasicTable
    :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
    @register="registerTable"
    :loading="loadMark"
    :scroll="{ x: totalColumnWidth,y: windowHeight }"
    :dataSource="tableData"
  >
    <template #md="{ record }">
      <a v-if="record.md!=0">{{ toThousandFilter(record.md) }}</a>
    </template>
    <template #mc="{ record }">
      <a v-if="record.mc!=0">{{ toThousandFilter(record.mc) }}</a>
    </template>
    <template #inoId="{ record }">
      <span v-if="record.id!=null && record.id!=''">
        <a>{{ record.csign }}-{{ formatInoId(record.inoId) }}</a>
      </span>
    </template>

  </BasicTable>
    <div class="pagination-text" v-show="showPaginationText">
      共 {{paginationNumber}} 条记录&nbsp;&nbsp; 每页 200 条
    </div>
  </div>
</template>

<script setup="props, {emit}" lang="ts">
import {BasicTable, useTable} from '/@/components/Table'

import {computed, onMounted, reactive, ref, watch,inject} from "vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {findAccountByKemuAndDate} from "/@/api/wei_da_zhang/wei_da_zhang";

const kemu: any = ref('')
const year: any = ref('')
const endDate: any = ref('')
const flag = ref('0')
const dynamicTenantId = ref(getCurrentAccountName(true))

const windowHeight = (window.innerHeight - (290))
const totalColumnWidth = ref(0)

const CrudApi = {
  list: findAccountByKemuAndDate,
  columns: [
    {
      title: '制单日期',
      dataIndex: 'dbillDate',
      width: 100,
      ellipsis: true
    },
    {
      title: '凭证类别',
      dataIndex: 'csign',
      defaultHidden: true,
      ellipsis: true
    },
    {
      title: '凭证编号',
      dataIndex: 'inoId',
      width: 100,
      // defaultHidden: true,
      ellipsis: true,
      slots: {customRender: 'inoId'}
    },
    {
      title: '摘要',
      dataIndex: 'cdigest',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return {style: {'text-align': 'left'}} // return 想要设置的样式
      },
      align: 'left',
      ellipsis: true
    },
    {
      title: '科目编码',
      dataIndex: 'ccode',
      defaultHidden: true,
      ellipsis: true
    },
    {
      title: '科目名称',
      dataIndex: 'ccodeName',
      defaultHidden: true,
      ellipsis: true
    },
    {
      title: '结算方式编码',
      dataIndex: 'pjCsettle',
      width: 120,
      ellipsis: true
    },
    {
      title: '票据日期',
      dataIndex: 'pjDate',
      width: 120,
      ellipsis: true
    },
    {
      title: '票号',
      dataIndex: 'pjId',
      width: 120,
      ellipsis: true
    },
    {
      title: '对方单位',
      dataIndex: 'pjUnitName',
      // defaultHidden: true,
      ellipsis: true
    },
    {
      title: '借方金额',
      dataIndex: 'md',
      width: 150,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return {style: {'text-align': 'right', '-webkit-line-clamp': 2}} // return 想要设置的样式
      },
      align: 'right',
      ellipsis: true,
      slots: {customRender: 'md'}
    },
    {
      title: '贷方金额',
      dataIndex: 'mc',
      width: 150,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return {style: {'text-align': 'right', '-webkit-line-clamp': 2}} // return 想要设置的样式
      },
      align: 'right',
      ellipsis: true,
      slots: {customRender: 'mc'}
    },
  ]
}

// 这是示例组件
const [registerTable, {reload,getDataSource,getColumns}] = useTable({
  /*api: async (params) => {
    return useRouteApi(findAccountByKemuAndDate, {schemaName: dynamicTenantId})({
      kemuCode: kemu.value,
      year: year.value,
      flag: flag.value,
      endDate: endDate.value
    })
  },*/
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

const tableData:any = ref([]);
const tableDataAll:any = ref([]);
const showPaginationText = ref(false)
const paginationNumber = ref(0)
async function reloadTable(){
  showPaginationText.value = false
  let len = 0
  tableData.value =[]
  tableDataAll.value =[]
  if (kemu.value!=null && kemu.value!='' && year.value!=null && year.value!='' && endDate.value!=null && endDate.value!='') {
    const res = await useRouteApi(findAccountByKemuAndDate, {schemaName: dynamicTenantId})({
      kemuCode: kemu.value,
      year: year.value,
      flag: flag.value,
      endDate: endDate.value
    })
    tableDataAll.value = res.items
  }
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
}

function formatInoId(inoId) {
  let str = inoId
  if (inoId != null && inoId != '') {
    if (inoId.length == 1) {
      str = '000' + inoId
    }
    if (inoId.length == 2) {
      str = '00' + inoId
    }
    if (inoId.length == 3) {
      str = '0' + inoId
    }
  }
  return str
}

const pageParameter: any = reactive({
  showRulesSize: 'MIN',
  companyCode: '',
  companyName: '',
  ifUnit: false,
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

const props = defineProps(['modelValue'])
const baseName = computed(() => props.modelValue.thisAdInfo)
watch(baseName, async () => {
  dynamicTenantId.value = props.modelValue.thisAdInfo.accountMode
  year.value = props.modelValue.thisAdInfo.iyear
  kemu.value = props.modelValue.kemu
  pageParameter.showRulesSize = props.modelValue.showRulesSize
  await reloadTable()
})

const kemuCode = computed(() => props.modelValue.kemu)
watch(kemuCode, async () => {
  dynamicTenantId.value = props.modelValue.thisAdInfo.accountMode
  year.value = props.modelValue.thisAdInfo.iyear
  kemu.value = props.modelValue.kemu
  endDate.value = props.modelValue.endDate
  pageParameter.showRulesSize = props.modelValue.showRulesSize
  pageParameter.companyName = props.modelValue.companyName
  await reloadTable()
})

const time = computed(() => props.modelValue.endDate)
watch(time, async () => {
  dynamicTenantId.value = props.modelValue.thisAdInfo.accountMode
  year.value = props.modelValue.thisAdInfo.iyear
  kemu.value = props.modelValue.kemu
  endDate.value = props.modelValue.endDate
  pageParameter.showRulesSize = props.modelValue.showRulesSize
  pageParameter.companyName = props.modelValue.companyName
  await reloadTable()
})

const showRulesSize = computed(() => props.modelValue.showRulesSize)
watch(showRulesSize, async () => {
  pageParameter.showRulesSize = props.modelValue.showRulesSize
})

function toThousandFilter(num: any) {
  if (num == '' || num == null) {
    return ''
  }
  return (+num || 0).toFixed(2).replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,')
}

const funs:any = inject('fun')
onMounted(async () => {
  await reloadTable()
  funs.value.exportF = ()=>{
    excelData()
  }
  funs.value.printF = ()=>{
    printData()
  }
})
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
  const arrData = getDataSource().filter(item=>item.id!=null && item.id!='')
  let kemu=''
  arrData.forEach(item=>{
    let item1 = {}
    kemu = item.ccode+'-'+item.ccodeName
    item1[0] = item.dbillDate
    item1[1] = item.csign+'-'+formatInoId(item.inoId)
    item1[2] = setString(item.cdigest,27)
    item1[3] = item.pjCsettle
    item1[4] = item.pjDate
    item1[5] = item.pjId
    item1[6] = setString(item.pjUnitName,15)
    item1[7] = item.md==0?'':toThousandFilter(item.md)
    item1[8] = item.mc==0?'':toThousandFilter(item.mc)
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
    item1[8] = ''
    printList.push(item1)
  }
  let num = Math.ceil(printList.length/27)
  useNewPrint({data: ['l', 'px', 'a4', true]}, (doc) => {
    loadMark.value = false
    doc.autoTable({
      head: [['银行对账-企业未达账项','', '', '',  '', '', '', '', ''],
        ['会计科目：'+kemu, '', '', '截止日期:'+endDate.value, '', '', '', '', '币种：人民币'],
        ['制单日期', '凭证编号', '摘要', '结算方式', '票据日期', '票号', '对方单位', '借方金额', '贷方金额']],
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
        doc.autoTableText(
          '核算单位：'+pageParameter.companyName,
          tabMarginLeft,
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
            data.cell.colSpan = 3
            data.cell.styles.halign = 'left'
          } else if (data.column.index == 3) {
            data.cell.colSpan = 3
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
        0: {maxHeight: 10, cellWidth: 45, halign: 'left'},
        1: {cellWidth: 35, halign: 'center'},
        2: {cellWidth: 110, halign: 'left'},
        3: {cellWidth: 50, halign: 'left'},
        4: {cellWidth: 50, halign: 'left'},
        5: {cellWidth: 50, halign: 'left'},
        6: {cellWidth: 70, halign: 'left'},
        7: {cellWidth: 80, halign: 'right'},
        8: {cellWidth: 80, halign: 'right'},
      }
    })
  })
}
//导出
const excelData = () => {
  // console.log("导出成功！")
  let list: any = []
  const arrData = getDataSource().filter(item=>item.id!=null && item.id!='')
  let kemu=''
  arrData.forEach(item=>{
    let item1 = {}
    kemu = item.ccode+'-'+item.ccodeName
    item1[0] = item.dbillDate
    item1[1] = item.csign+'-'+formatInoId(item.inoId)
    item1[2] = item.cdigest
    item1[3] = item.pjCsettle
    item1[4] = item.pjDate
    item1[5] = item.pjId
    item1[6] = item.pjUnitName
    item1[7] = item.md==0?'':toThousandFilter(item.md)
    item1[8] = item.mc==0?'':toThousandFilter(item.mc)
    list.push(item1)
  })
  const sheet:any = [
    {
      title: '银行对账-企业未达账项',
      multiHeader: [
        ['会计科目：' + kemu, '', '', '截止日期:'+endDate.value, '', '', '币种：人民币', '', ''],
        ['制单日期', '凭证编号', '摘要', '结算方式', '票据日期', '票号', '对方单位', '借方金额', '贷方金额']
      ],
      table: list,
      foot: ['核算单位：' + pageParameter.companyName, '', '', '', '', '', '', '', ''],
      keys: [0, 1, 2, 3, 4, 5, 6, 7, 8],
      merges: ['A2:C2', 'D2:F2', 'G2:I2'],
      sheetName: '银行对账-企业未达账项',
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
      colWidth: [8, 8, 25, 12, 12, 12, 12, 12, 12]
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
    if (foot || foot === '') {
      foot && data.push(foot);
      const str = ('A' + data.length) + (':I' + data.length)
      merges.push(str)
    }
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
            if (i == 'D2') {
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
          } else if (i.substring(0, 1) == 'C' || i.substring(0, 1) == 'D' || i.substring(0, 1) == 'E' || i.substring(0, 1) == 'F' || i.substring(0, 1) == 'G') {
            dataInfo[i.toString()].s = {
              border: border === {} ? border : border || globalStyle.border,
              font: font || globalStyle.font,
              fill: fill || globalStyle.fill,
              alignment: {
                horizontal: "left",
              }
            }
          } else if (i.substring(0, 1) == 'H' || i.substring(0, 1) == 'I') {
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
  writeExcel(wb, bookType, '银行对账-企业未达账项_'+pageParameter.companyName )
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

</script>

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

:deep(.ant-pagination) {
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

:deep(.nc-summary) {
  font-size: 14px;
  font-weight: bold;
  width: 100%;
  background-color: #cccccc;
  border:none !important;
}
</style>
