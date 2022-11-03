<template>
  <div class="app-container-node">

    <div style="clear:both"/>
    <BasicTable
      ref="tableRefNode"
      :row-selection="{ type: 'checkbox',selectedRowKeys: tableSelectedRowKeys, onChange: onSelectChange }"
      :class="props.modelValue.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
      :scroll="{ x: totalColumnWidth,y:  windowHeight}"
      size="small"
      style="width: 100%"
      @register="registerTable"
    >
      <template #serialNumber="{record,index }">
          <span slot="serialNumber" slot-scope="text,record,index">
           {{record.cdfine30}}
          </span>
      </template>

      <template #md="{ record }">
          <span style="float: right">
           {{ moneyformat(record.md) }}
          </span>
      </template>

      <template #mc="{ record }">
          <span style="float: right">
           {{ moneyformat(record.mc) }}
          </span>
      </template>

      <template #remainMoney="{ record }">
          <span style="float: right">
           {{ moneyformat(record.remainMoney) }}
          </span>
      </template>

      <template #hxStatue="{ record }">
          <span style="float: right">
           {{ moneyhxStatue(record.hxStatue) }}
          </span>
      </template>

    </BasicTable>
  </div>
</template>
<script setup lang="ts">
import {BasicTable, useTable} from '/@/components/Table'
import {
  DatePicker as ADatePicker, Select as ASelect, Input as AInput,
  Radio as ARadio, Table as ATable,
  message, Drawer as ADrawer
} from "ant-design-vue"
import {FilterFilled,UsbOutlined,PrinterOutlined} from '@ant-design/icons-vue'
import { useAccvoucherHxStore } from '/@/store/modules/accvoucher-hexiao'

import {onMounted, reactive, ref, watch,defineExpose } from "vue";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  getCurrentAccountName,
} from "/@/api/task-api/tast-bus-api";
import {findAll, finByParameterAccuracy} from '/@/api/record/km_mingxi/data';
import {changeDefaultDynamics, initDynamics,assemblyDynamicColumn} from "../data";
import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
import router from "/@/router";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useRoute} from "vue-router";
import {findCodeKmByPeriod} from "/@/api/record/generalLedger/data";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
import {findDbLanMuList, saveLanMuList} from '/@/api/record/system/accvoucher'
import {
  findAllAccvoucherHexiaoGys
} from '/@/api/record/system/write-off'
import {exportExcel} from "/@/api/record/generalLedger/excelExport";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const {createConfirm, createWarningModal} = useMessage();
// 全局常量
const glStore = useAccvoucherHxStore()
const props = defineProps(['modelValue',])

const targetKeys = ref([])

// 页面变量
const pageParameter = reactive({
  database: '',
  thisAdInfo: {},
  total: 0,
  page: '1',
  size: '8',
  reloadMark: false,
  queryMark: '1',
  showRulesSize: 'MIN',
  biZhong: '人民币',
  companyCode: '',
  ifUnit: false,
  companyName: '',
  condition: {},
  searchConditon: {
    requirement: 'inoId',
    value: '',
  },
  filterConditon: {
    amountMin: '',
    amountMax: '',
  },

  km: '',
  jpzDate: '',
  dpzDate: '',
  type: 'd',
})
const styleName = ref<String>("金额式")
// 科目名称标题
const titleName = ref<String>("")
// 会计科目
const kmList: any = ref([])
const showStyle = ref([
  {
    'name': '金额式',
    'value': 'J'
  }
])
const visible = ref(false);
const searchConditonList = ref([])
const lanMuData = {
  'accId': '',
  'menuName': '往来核销',
  'type': '',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username
}

// 表格参数
const loadMark = ref(false)
const windowWidth = (document.documentElement.clientWidth - 70)
const windowHeight = (window.innerHeight - (350))
const totalColumnWidth = ref(0)

const tableRefNode = ref(null)
const userName = useUserStoreWidthOut().getUserInfo.username
const userStore = useUserStore();
// 数据库模式名称
const database = ref(getCurrentAccountName(true))

const digestLanMuData = {
  'accId': database.value.substring(0, database.value.length - 5),
  'menuName': '往来核销',
  'type': '',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username
}

const CrudApi = {
  list: useRouteApi(findAllAccvoucherHexiaoGys,{schemaName:database.value}),
  columns: JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark)))
}
// 组件实例区
const [registerTable, {
  reload,
  setTableData,
  setColumns,
  getColumns,
  getPaginationRef,
  setPagination,
  getDataSource
}] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  loading: loadMark.value,
  immediate: false,
  canResize: true,
  showIndexColumn: false, //显示序号列
  pagination: {
    pageSize: 8,
    showSizeChanger: true,
    pageSizeOptions: ['8', '16', '32', '48'],
    showTotal: t => `总共${t}条数据`
  },
  searchInfo: pageParameter,
})

onMounted(async () => {
  //initPageParameter()
})

function getSelectData() {
  return tableSelectedRowObjs.value
}
const initPageParameter = async () => {
  pageParameter.km = props.modelValue.km
  pageParameter.queryMark = '1'
  pageParameter.condition = props.modelValue.condition
  pageParameter.searchConditon.requirement = props.modelValue.searchConditon.requirement
  pageParameter.filterConditon = props.modelValue.filterConditon

  pageParameter.maxKm = props.modelValue.maxKm
  pageParameter.endDate = props.modelValue.endDate
  pageParameter.strDate = props.modelValue.strDate
  pageParameter.ishaveRjz = props.modelValue.ishaveRjz

  pageParameter.styleValue = props.modelValue.styleValue
  pageParameter.digestStyle = props.modelValue.digestStyle
  resetDynamicColumnData()
}
defineExpose({initPageParameter,getSelectData})

function tableRow(row: any, num: any) {
  return (row.cdigest == '本月合计' ? 'table-month-striped' : null)
    || (row.cdigest == '本年累计' ? 'table-year-striped' : null)
}
function moneyhxStatue(val:any) {
  return val === "1"?'已核销': '待核销'
}

function digestDynamicColumnData() {
  findDbLanMuList(digestLanMuData).then(res => {
    // 栏目列
    let dbList = res.items
    let arr = []
    if (dbList.length > 0) {
      for (let i = 0; i < dbList[0].key.split(',').length; i++) {
        arr.push(dbList[0].key.split(',')[i])
      }
    }
    targetKeys.value = arr
  })
}

const tableSelectedRowKeys = ref([])
const tableSelectedRowObjs = ref([])

const unCheckTable = ()=>{
  if (tableSelectedRowKeys.value.length > 0){
    tableSelectedRowKeys.value = []
    tableSelectedRowObjs.value = []
  }
}
// 页面函数区
const onSelectChange = (selectedRowKeys,obj) => {
  tableSelectedRowKeys.value = selectedRowKeys;
  tableSelectedRowObjs.value = obj;
};

const pageReload = () => {
  pageParameter.digestStyle = targetKeys.value.length > 0 ? targetKeys.value.join(',') : ''
  reload({
    searchInfo: pageParameter
  })
}

const onChangeSwitch = async (v) => { // 动态列
  pageParameter.queryMark = '1'
  resetDynamicColumnData()
  pageParameter.searchConditon.requirement = 'bprogerty'
  pageParameter.searchConditon.value = ''
}
const pageSearch = async (a) => {
  // 搜索前校验格式
  if ('' == pageParameter.searchConditon.requirement.trim()) {
    message.warn('请选择检索条件')
    return false
  }
  pageParameter.searchConditon.value = a
  // 校验完成后搜索
  pageReload()
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
const dynamicColumnData = ref([])
let dynamicColumnDataCopy = []

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
function resetDynamicColumnData() {
  // 先从数据查询是否已经设置
  lanMuData.accId = database.value.substring(0, database.value.length - 5)
  lanMuData.type = pageParameter.queryMark
  findDbLanMuList(lanMuData).then(res => {
    // 栏目列
    let dbList = res.items
    if (dbList.length > 0) {
      let statiList = initDynamics()['DATA'+pageParameter.queryMark]
      dbList = combineParameters(statiList, dbList)
      dynamicColumnData.value = dbList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(dbList))
    } else {
      let statiList = initDynamics()['DATA'+pageParameter.queryMark]
      dynamicColumnData.value = statiList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(statiList))
    }
    // 表格列
    reloadColumns()
    digestDynamicColumnData()
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
const reloadColumns = () => {
  let newA = JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark)))
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
    tableRefNode.value.$el.style.setProperty('width', (windowWidth + 20 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    tableRefNode.value.$el.style.setProperty('width', (total + 20) + 'px')
  }
}
/*栏目设置end*/

// 金额格式化
function moneyformat(data: any) {
  let str = ""
  if(data){
    // 千分位保留2位小数
    var source = String(parseFloat(data).toFixed(2)).split("."); //按小数点分成2部分
    source[0] = source[0].replace( new RegExp('(\\d)(?=(\\d{3})+$)' , 'ig'), "$1,");//只将整数部分进行都好分割
    str = source.join("." );//再将小数部分合并进来
  }
  return data == ('0.00' || '0')?'': str;
}
// 数量格式化
function numformat(data: any) {
  let str = ""
  if (data) {
    if (0 === data) {
      str = ""
    } else {
      var source = String(data.toFixed(pageParameter.numJd)).split("."); //按小数点分成2部分
      source[0] = source[0].replace(new RegExp('(\\d)(?=(\\d{3})+$)', 'ig'), "$1,");//只将整数部分进行都好分割
      str = source.join(".");
    }
  }
  return str;
}

</script>
<style lang="less" scoped>
.app-container-node {
  background: white;
  padding: 10px 20px;
  .ant-btn {
    border-color: #aaaaaa;
    font-size: 16px;
  }

  .ant-input-affix-wrapper {
    border-color: #aaaaaa;
  }

}

:deep(.ant-table-scroll) {
  overflow: hidden;
}

:deep(.ant-table-header), :deep(ant-table-body) {
  .ant-table-thead th {
    text-align: center;
    border-right: 1px solid #f0f0f0;

    .ant-table-column-title {
      color: black !important;
      font-weight: bold !important;
    }
  }
}

:deep(td) {
  padding: 5px 8px !important;
  border: 1px solid #a7a7a7 !important;
  border-left: none !important;
  border-bottom: none !important;
  color: black !important;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}

:deep(.ant-table-footer) .ant-table-body {
  overflow: hidden !important;
}

:deep(.ant-table-footer) {
  padding: 0;

  tr {
    font-weight: bold !important;
  }
}

.a-table-font-size-16 :deep(td), .a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
}

.a-table-font-size-12 :deep(td), .a-table-font-size-12 :deep(th) {
  font-size: 12px !important;
}

:deep(.table-striped) {
  background-color: azure;
}

:deep(.table-month-striped) {
  background-color: honeydew;
}

:deep(.table-year-striped) {
  background-color: lightyellow;
}
</style>
