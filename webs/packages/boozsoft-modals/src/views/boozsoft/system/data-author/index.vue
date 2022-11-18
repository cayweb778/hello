<template>
  <div>
    <div class="app-container lcr-theme-div" >
      <div style="    display: inline-flex;">
        <div>
          <ProfileOutlined style="color: #0096c7;font-size: 50px;"/>
        </div>
        <div>
          <AccountPicker theme="three" @reloadTable="dynamicAdReload"/>
          <div style="display: inline-block;margin-left: 2.4%;margin-top: 2%;">
            <span style="font-weight: bold;color: #666666;">操作员：&emsp;</span>
            <a-select style="width: 120px;" v-model:value="saveDataModel.userUniqueCode"
                      @change="operChange">
              <template v-for="item in operList">
                <a-select-option :value="item.id">
                  {{ item.realName }}
                </a-select-option>
              </template>
            </a-select>
          </div>
        </div>
      </div>
      <div>
        <div style="margin-top: 5%;">  <b class="noneSpan" style="font-size: 24px;color: #0096c7;">数据权限分配</b></div>
      </div>
      <div>
        <div>
          <button type="button" class="ant-btn ant-btn-me"
                  :style="showOk?{}:{pointerEvents: 'none'}" @click="saveData">
            {{ activeKey == '1' ? '分配' : '撤销' }}
          </button>
          <button type="button" class="ant-btn ant-btn-me" v-if="activeKey=='2'"
                  @click="exportTable">导出
          </button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent()">退出</button>
        </div>
        <div>
          <div class="acttd-right-d-search">
            <a-select v-model:value="pageParameter.searchConditon.requirement"
                      class="acttdrd-search-select">
              <a-select-option :value="'dataCode'">数据编码</a-select-option>
              <a-select-option :value="'dataName'">数据名称</a-select-option>
            </a-select>

            <a-input-search
              style="width: 180px;"
              v-model:value="pageParameter.searchConditon.value"
              class="acttdrd-search-input"
              @search="onSearch"
            />
          </div>
          <div>
            <a-button class="ant-btn-me" @click="pageReload">
              <SyncOutlined :style="{ fontSize: '14px' }"/>
            </a-button>
            <a-popover placement="bottom">
              <template #content>
                          <span class="group-btn-span-special" @click="pageParameter.showRulesSize = 'MAX'"
                                :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                            <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
                            v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
                <span class="group-btn-span-special" @click="pageParameter.showRulesSize = 'MIN'"
                      :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                            <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
                  v-if="pageParameter.showRulesSize==='MIN'"/></span>
              </template>
              <a-button>
                <SettingFilled :style="{ fontSize: '14px' }"/>
              </a-button>
            </a-popover>
          </div>
        </div>
      </div>
    </div>
    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <LeftTree @select="handleSelect" @setting="settingBoos"/>
        <div style="width: calc(100% - 250px);display: inline-flex;justify-content: start;">
          <Tabs v-model:activeKey="activeKey" @change="handleSelect({keys: theCheckList})"
                tab-position="left"
                style="font-weight: bold;font-weight: bold;min-width: 90px;width: 90px; background-color: #f2f2f2;"
                :style="{height: (windowHeight+40) +'px'}">
            <TabPane key="2" tab="已分配"/>
            <TabPane key="1" tab="未分配"/>
          </Tabs>
          <BasicTable
            ref="tableRef"
            :row-selection="{ type: 'checkbox',fixed: true, selectedRowKeys: tableSelectedRowKeys, onSelect: onSelectChange,onSelectAll: onSelectChange2 }"
            :rowKey="r=>r.dataId"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :scroll="{ x: totalColumnWidth,y: windowHeight }"
            :loading="loadMark"
            @register="registerTable"
          >
            <template #isSee="{record}">
              <div v-if="isShow(record,'isSee')">
                <a-checkbox v-model:checked="record.isSee" @change="rowChange(record,true)"/>
              </div>
            </template>
          </BasicTable>
        </div>
      </PageWrapper>
    </div>
  </div>
</template>

<script setup lang="ts">
import {getCurrentAccountName, hasBlank} from '/@/api/task-api/tast-bus-api';
import {PageWrapper} from '/@/components/Page'
import {BasicTable, useTable, TableAction} from '/@/components/Table';
import {useModal} from '/@/components/Modal';
import {onMounted, provide, reactive, ref, unref} from 'vue';

const {closeCurrent} = useTabs(router);
import {useMessage} from '/@/hooks/web/useMessage';
import {
  SettingFilled,
  SyncOutlined,
  SortAscendingOutlined, CheckOutlined, SortDescendingOutlined, ProfileOutlined
} from '@ant-design/icons-vue';
import {
  Col as ACol,
  Popover as APopover,
  Input as AInput,
  Select as ASelect,
  Checkbox as ACheckbox,
  Row as ARow,
  Popconfirm as APopconfirm,
  List as AList, message, Radio as ARadio, Table as ATable, Tabs
} from 'ant-design-vue'
import LeftTree from './popup/LeftTree.vue';

const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const AListItem = AList.Item
const ACheckboxGroup = ACheckbox.Group
const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
const TabPane = Tabs.TabPane
import {
  findAll
} from '/@/api/caozuoyuan/caozuoyuan';

import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {usePlatformsStore} from "/@/store/modules/platforms";
import {assemblyDynamicColumn, initDynamics} from "./data";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {findDbLanMuList, saveLanMuList} from "/@/api/record/system/accvoucher";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import {cloneDeep} from "lodash-es";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";

import {
  getTheMenuOfTheSpecifiedPlatform
} from "/@/views/boozsoft/system/operation-author/menu-data";
import {
  findUserAuthor,
  getArchivesAllDataList,
  getTheControlArchives,
  saveDataAuthor, saveDataAuthor2
} from "/@/api/caozuoyuan/data-author";
import {
  defaultV,
  sheet_from_array_of_arrays,
  Workbook,
  writeExcel
} from "/@/utils/boozsoft/excel/excel2";
//import XLSX from "xlsx-js-style";
const XLSX=null;
import LeftTreeOld from "/@/views/boozsoft/system/data-author/popup/LeftTree.vue";

const CrudApi = {
  columns: [
    {
      title: '数据编码',
      dataIndex: 'dataCode',
      ellipsis: true, width: 150, align: 'left'
      /* slots: {customRender: 'dataCode'},*/
    },
    {
      title: '数据名称',
      dataIndex: 'dataName',
      ellipsis: true,
      width: 300, align: 'left'
    }, {
      title: '数据唯一码',
      dataIndex: 'dataId', width: 200,
      ellipsis: true, align: 'left'
    }
  ],
};
const totalNumber = ref(0)
// 这是示例组件
const [registerTable, {setTableData, getColumns, setColumns, reload, getDataSource}] = useTable({
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: true,
  indexColumnProps: {width: 60, fixed: 'left'},
  /*  showTableSetting: true,*/
  /*  tableSetting: {
      redo: true,
      size: false,
      setting: true,
      fullScreen: false
    },*/
  pagination: false/*{
    pageSize: 100,
    showSizeChanger: true,
    pageSizeOptions: ['100', '150'],
    showTotal: (t) => {
      totalNumber.value = t
      return `共${t}条记录`
    }
  },*/
  /*  actionColumn: {
      width: 160,
      title: '操作',
      dataIndex: 'action',
      slots: {customRender: 'action'},
    },*/
});
const activeKey = ref('2');
const operList = ref([]);
const controlList = ref([]);
const menuList = ref([]);
const userDataModel = ref({});
const dbDataModel = ref({});
const tableList = ref([]);

const treeList = ref([])
const lastCheckList = ref([])

const loadMark = ref(false)
const showOk = ref(true)
const saveDataModel = reactive({
  id: null,
  ztUniqueCode: '',
  type: '',
  ztYear: '',
  archives: '',
  userUniqueCode: '',
  defaultLogin: '',
  ztCode: ''
})

onMounted(async () => {
  // resetDynamicColumnData()
  initTableWidth(getColumns())
});


const dynamicAdReload = async (e) => {
  saveDataModel.ztUniqueCode = e.accountMode
  saveDataModel.ztCode = e.coCode
  saveDataModel.ztYear = e.iyear
  // saveDataModel.ztStyle = e.target.ztStyle
  saveDataModel.id = null
  controlList.value = await getTheControlArchives({uniqueCode: e.accountMode})
  let keys = controlList.value.map(it => it.moduleName)
  treeList.value = ([...new Set(keys)]).map((s) => ({
    key: s,
    name: s,
    children: controlList.value.filter(it => it.moduleName == s).map(o => ({
      key: o.databaseName,
      name: o.dataName
    }))
  }))
  dbDataModel.value = {}
  if (operList.value.length == 0) {
    operList.value = ((await findAll()).items)
    if (operList.value.length > 0 && hasBlank(saveDataModel.userUniqueCode)) {
      saveDataModel.userUniqueCode = operList.value[1].id
      operChange(operList.value[1].id)
    }
  }
}

const operChange = async (v) => {
  if (!hasBlank(saveDataModel.ztUniqueCode) && !hasBlank(saveDataModel.ztYear) && !hasBlank(saveDataModel.userUniqueCode)) {
    loadMark.value = true
    let res = await findUserAuthor({
      userId: saveDataModel.userUniqueCode,
      uniqueCode: saveDataModel.ztUniqueCode,
    })
    if (null != res && Object.keys(res).length > 0) {
      userDataModel.value = res
      // let a = Object.keys(res)
      lastCheckList.value = JsonTool.parseProxy(res)
    } else {
      lastCheckList.value = []
      userDataModel.value = {}
    }
    loadMark.value = false
  }
}

async function saveData() {
  showOk.value = false
  if (hasBlank(saveDataModel.ztUniqueCode)) {
    createWarningModal({content: '请先选择授权账套！'})
    showOk.value = true
  } else if (hasBlank(saveDataModel.userUniqueCode)) {
    createWarningModal({content: '请选择操作员！'})
    showOk.value = true
  } else if (hasBlank(saveDataModel.archives)) {
    createWarningModal({content: '请选择授权模块！'})
    showOk.value = true
  } else if (tableSelectedRowKeys.value.length == 0) {
    createWarningModal({content: '请选择勾选操作行！'})
    showOk.value = true
  } else {
    saveDataModel.type = activeKey.value
    saveDataModel.defaultLogin = JsonTool.json(tableSelectedRowKeys.value)
    let res = await saveDataAuthor(saveDataModel)
    if (res == null) {
      message.success('操作成功！')
      operChange(saveDataModel.userUniqueCode)
    } else {
      message.error('操作失败！')
    }
    tableSelectedRowKeys.value = []
    showOk.value = true
  }

}

function onSearch() {
  loadMark.value = true
  if (hasBlank(pageParameter.searchConditon.value.trim())) {
    setTableData(tableList.value)
  } else {
    let v = pageParameter.searchConditon.value.trim()
    let k = pageParameter.searchConditon.requirement.trim()
    let searchAfter = tableList.value.filter(item => item[k].indexOf(v) != -1)
    setTableData(searchAfter)
  }
  loadMark.value = false
}

const searchConditonList = ref([])
const PlatformColumns = ref({})

const theCheckList = ref()
const isDirector = ref(false)
const handleSelect = async (e) => {
  let value = e.keys
  loadMark.value = true
  theCheckList.value = value
  tableSelectedRowKeys.value = []
  setTableData([])
  if (null == value || null == value[0]) {
    tableList.value = []
    isDirector.value = false
  } else {
    saveDataModel.archives = value[0]
    let j = controlList.value.filter(it => it.databaseName == saveDataModel.archives)[0]
    if (null == dbDataModel.value[value[0]]) dbDataModel.value[value[0]] = await getArchivesAllDataList({
      tableName: value[0],
      uniqueCode: saveDataModel.ztUniqueCode,
      query: JsonTool.json(j),
      year: saveDataModel.ztYear
    })
    let userList = (userDataModel.value[value[0]] || [])
    isDirector.value =  userList.length == 1 && userList[0].isDirector == '1'
    userList = userList.map(it => it.dataId)
    let list = dbDataModel.value[value[0]].filter(it => isDirector.value?false:activeKey.value == '1' ? (userList.indexOf(it.unique) == -1) : (userList.indexOf(it.unique) != -1))
      .map(it => ({
        dataCode: it.code,
        dataName: it.name,
        dataId: it.unique,
        pid: it?.relation || '0'
      }))
    setTableData(list)
    tableList.value = list
  }
  pageParameter.searchConditon.value = ''
  loadMark.value = false
}


const settingBoos = async (e)=>{
  e.ztUniqueCode = saveDataModel.ztUniqueCode
  e.userUniqueCode = saveDataModel.userUniqueCode
  let res = await saveDataAuthor2(e)
  if (res == null) {
    message.success('操作成功！')
    operChange(saveDataModel.userUniqueCode)
  } else {
    message.error('操作失败！')
  }
}

const pageReload = () => {
}
const tableSelectedRowKeys = ref([])
const onSelectChange = (record, selected, obj, nativeEvent) => {
  loadMark.value = true
  let oldList = tableSelectedRowKeys.value
  let difference = getCurrentAssociation(record, selected)
  if (selected) {
    oldList.push(...difference)
    tableSelectedRowKeys.value = [...new Set(oldList)]
  } else {
    tableSelectedRowKeys.value = obj.filter(it => difference.indexOf(it.dataId) == -1).map(it => it.dataId)
  }
  // 获取下级
  loadMark.value = false
}
const onSelectChange2 = (selected, selectedRows, changeRows) => {
  loadMark.value = true
  if (selected) {
    tableSelectedRowKeys.value = selectedRows.map(item => item.dataId)
  } else {
    tableSelectedRowKeys.value = []
  }
  loadMark.value = false
}

function getCurrentAssociation(obj, fl) {
  // 获取自动行的下级
  function getDown(codes) {
    let list = []
    let b = tableList.value.filter(it => codes.indexOf(it.pid) != -1)
    if (b.length > 0) {
      let ds = b.map(it => it.dataId)
      list.push(...ds)
      list.push(...(getDown(saveDataModel.archives == 'code_kemu' ? b.map(it => it.dataCode) : ds)))
    }
    return list
  }
  // 获取自动行的上级
  function getUp(upId) {
    let list = []
    let a = tableList.value.filter(it => upId == (saveDataModel.archives == 'code_kemu' ? it['dataCode'] : it['dataId']))
    if (a.length > 0) {
      list = a.map(it => it.dataId);
      let b = a.filter(it => it.pid != '0').map(it => it.pid)
      if (b.length > 0) list.push(... getUp(b[0]))
    }
    return list
  }
  let list = [obj.dataId]
  let codes = [obj.dataCode]
  let downs = getDown(saveDataModel.archives == 'code_kemu' ? codes : list)
  let ups = obj.pid == '0' ? [] : getUp(obj.pid)
  if(downs.length > 0) list.push(...downs)
  if (ups.length > 0 && fl) list.push(...ups)
  return [...new Set(list)]
}

/*start栏目设置*/
const pageParameter = reactive({
  showRulesSize: 'MIN',
  searchConditon: {
    requirement: 'dataName',
    value: ''
  },
})
const mark = usePlatformsStore().getCurrentPlatformId
const visible = ref(false);
const windowWidth = (window.innerWidth - (70))
const windowHeight = (window.innerHeight - (300))
const totalColumnWidth = ref(0)
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData: any = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});
const tableRef: any = ref(null)
const lanMuData = {
  'accId': 'postgres',
  'menuName': '操作权限设置',
  'type': '列表',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username
}
const {createConfirm, createWarningModal} = useMessage();
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
  /*let a: any = []
  a = getColumns()
  let last = a.pop()*/
  let newA = JSON.parse(JSON.stringify(CrudApi.columns))
  newA = assemblyDynamicColumn(dynamicColumnData.value, newA)
  // newA.push(last)
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
    tableRef.value.$el.style.setProperty('width', (windowWidth + 70 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 70) + 'px')
  }
}

/*栏目设置end*/


const rowChange = (e, m) => {
  if (m) {
    if (!e.isSee) {
      let arr = Object.keys(e).slice(4)
      for (let i = 0; i < arr.length; i++) {
        if (e[arr[i]]) e[arr[i]] = false
      }
    }
  } else {
    e.isSee = true
  }
}
/*************** 导出 ****************/
const exportTable = () => {
  let list = getDataSource()
  if (list.length == 0) {
    createWarningModal({content: '暂未发现符合到处条件的数据！'})
    return;
  }
  exportExcel()
}

function exportExcel() {
  let printList: any = getDataSource()
  let hs = ['数据编码', '数据名称', '数据唯一码']
  const sheet = [
    {
      tHeader: hs,
      table: printList,
      keys: ['dataCode', 'dataName', 'dataId'],
      cellStyle: [
        {
          cell: 'A1',
          font: {
            color: {rgb: "FF0000"},
          },
        },
        {
          cell: 'B1',
          font: {
            color: {rgb: "000000"},
          },
        },
        {
          cell: 'C1',
          font: {
            color: {rgb: "FF0000"},
          },
        },
        {
          cell: 'D1',
          font: {
            color: {rgb: "FF0000"},
          },
        },
        {
          cell: 'E1',
          font: {
            color: {rgb: "FF0000"},
          },
        },
        {
          cell: 'F1',
          font: {
            color: {rgb: "FF0000"},
          },
        }, {
          cell: 'G1',
          font: {
            color: {rgb: "FF0000"},
          },
        }, {
          cell: 'H1',
          font: {
            color: {rgb: "FF0000"},
          },
        }, {
          cell: 'I1',
          font: {
            color: {rgb: "FF0000"},
          },
        }, {
          cell: 'J1',
          font: {
            color: {rgb: "FF0000"},
          },
        }, {
          cell: 'K1',
          font: {
            color: {rgb: "FF0000"},
          },
        }, {
          cell: 'L1',
          font: {
            color: {rgb: "FF0000"},
          },
        },
      ],
      colWidth: [20, 40, 30]
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
    const ws = sheet_from_array_of_arrays(data);
    if (merges && merges.length > 0) {
      if (!ws['!merges']) ws['!merges'] = [];
      merges.forEach(merge => {
        ws['!merges'].push(XLSX.utils.decode_range(merge))
      })
    }

    ws['!cols'] = colWidth.map(i => {
      return {wch: i}
    })

    // 添加工作表
    wb.SheetNames.push(sheetName);
    wb.Sheets[sheetName] = ws;
    let dataInfo = wb.Sheets[wb.SheetNames[index]];


    // 单个样式
    (function () {
      if (!cellStyle || cellStyle.length <= 0) {
        return
      }
      cellStyle.forEach(s => {
        const {border, font, alignment, fill} = s;
        /*(dataInfo[s?.cell]).s = {
          border: border === {} ? border : border || globalStyle.border,
          font: font || globalStyle.font,
          alignment: alignment || globalStyle.alignment,
          fill: fill || globalStyle.fill
        }*/
      });
    })();
  })
  // 类型默认为xlsx
  const bookType = 'xlsx'
  writeExcel(wb, bookType, `${saveDataModel.ztCode} ${saveDataModel.ztYear} ${(operList.value.filter(t => t.id == saveDataModel.userUniqueCode))[0].realName} ${treeList.value.filter(t => t.key == saveDataModel.archives)[0].name}档案授权详情`)
}

/*************** 导出 ****************/

provide('treeList', treeList)
provide('lastCheckList', lastCheckList)
provide('theCheckList', theCheckList)
</script>
<style lang="less" scoped>
@import '/@/assets/styles/global-group-org-style.less';
:deep(.ant-select-selector){
  border-color: #c9c9c9 !important;
}
</style>
