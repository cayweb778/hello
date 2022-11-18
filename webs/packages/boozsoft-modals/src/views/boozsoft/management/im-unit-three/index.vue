<template>
  <div>
    <div class="app-container">
      <div
        style="width: 50%;height: 100%;display: inline-block;float: left;font-size: 22px;line-height: 70px;font-weight: bold;color: #0096c7;">
          <AppstoreOutlined style="font-size: 36px;color: #0096c7;" class="container-head-img"/>
        <div class="container-head-title"  style="margin-top: 14px;">
          <b class="noneSpan">固定资产账套</b>
        </div>
        <span
          style="font-size: 12px;color: black;position: absolute;top: 50px;left: 25px">共{{
            totalNumber
          }}条记录</span>
      </div>
      <div style="width: 50%;height: 100%;display: inline-block">
        <div class="app-container-head">
          <div class="ant-btn-group" style="float: right">
            <button type="button" class="ant-btn ant-btn-me" @click="openAddPage()">新建</button>
            <button type="button" class="ant-btn ant-btn-me" @click="editBefore">修改</button>
            <button type="button" class="ant-btn ant-btn-me" @click="delBefore">删除</button>
            <button type="button" class="ant-btn ant-btn-me">备份</button>
            <button type="button" class="ant-btn ant-btn-me">恢复</button>
            <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent()">退出</button>
          </div>
        </div>
        <div class="app-container-neck">
          <div style="float: right; margin-left: 10px">
            <a-button style="padding: 0px 12px !important;font-size: 14px" @click="tableReload">
              <SyncOutlined :style="{ fontSize: '14px' }"/>
            </a-button>
            <a-popover class="ant-btn-default" placement="bottom">
              <template #content>
                <a-popconfirm
                  ok-text="保存"
                  cancel-text="关闭"
                  @confirm="confirm"
                  @cancel="cancel">
                  <template #icon><b style="color: #0096c7;margin-left: 2em;">
                    <InsertRowAboveOutlined/>
                    栏目设置</b><br></template>
                  <template #title>
                    <div style="width: 640px">
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
                                       @pressEnter="saveName(record.key)" style="width: 100px;"/>
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
              <a-button>
                <SettingFilled :style="{ fontSize: '14px' }"/>
              </a-button>
            </a-popover>
            <!--            <a-popover class="ant-btn-default" placement="bottom">-->
            <!--              <template #content>-->
            <!--            <span class="group-btn-span-special2" @click="onChangeSwitch('all')"-->
            <!--                  :style="pageParameter.queryMark=='all'?{backgroundColor: '#0096c7',color: 'white'}:''">-->
            <!--                <ReadOutlined/>&nbsp;&emsp;全部&emsp;&ensp;<CheckOutlined-->
            <!--              v-if="pageParameter.queryMark=='all'"/></span><br/>-->
            <!--                <span class="group-btn-span-special2" @click="onChangeSwitch('1')"-->
            <!--                      :style="pageParameter.queryMark=='1'?{backgroundColor: '#0096c7',color: 'white'}:''">-->
            <!--                <SafetyOutlined/>&nbsp;&emsp;正常&emsp;&ensp;<CheckOutlined-->
            <!--                  v-if="pageParameter.queryMark=='1'"/></span><br/>-->
            <!--                <span class="group-btn-span-special2" @click="onChangeSwitch('0')"-->
            <!--                      :style="pageParameter.queryMark=='0'?{backgroundColor: '#0096c7',color: 'white'}:''">-->
            <!--                  <RestOutlined/>&nbsp;&emsp;停用&emsp;&ensp;<CheckOutlined-->
            <!--                  v-if="pageParameter.queryMark=='0'"/></span>-->
            <!--              </template>-->
            <!--              <a-button>-->
            <!--                <PicLeftOutlined :style="{ fontSize: '14px' }"/>-->
            <!--              </a-button>-->
            <!--            </a-popover>-->
            <a-button title="回收站" @click="openRecycleDel">
              <DeleteOutlined :style="{ fontSize: '14px' }"/>
            </a-button>
          </div>

          <div class="act-two-d-right">
            <div class="acttd-right-d-search">
              <!--              <a-select v-model:value="pageParameter.searchConditon.requirement"
                                      class="acttdrd-search-select">
                              <template v-for="item in searchConditonList">
                                <a-select-option v-if="item.ifShow == true" :value="item.dataIndex">
                                  {{ item.title }}
                                </a-select-option>
                              </template>
                            </a-select>-->
              <a-input-search
                style="width: 180px;"
                class="acttdrd-search-input"
                @search="onSearch"
                v-model:value="pageParameter.searchConditon.value"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="app-container">
      <Edit @save="saveData" @register="registerEditPage"/>
      <Copy @save="copyData" @register="registerCopyPage"/>
      <Recycle @modify="recycleModify" @register="registerRecyclePage"/>
      <BasicTable
        class="w-4/5 xl:w-5/6"
        ref="tableRef"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        :scroll="{ x: totalColumnWidth,y: windowHeight }"
        :row-selection="{ type: 'radio',fixed: true,selectedRowKeys: tableSelectedRowKeys, onChange: onSelectChange }"
        @register="registerTable"
      >
        <template #flag="{ record }">
          <span>
            <a-tag :color="record.flag !== '1' ? 'green' : 'volcano'">
              {{ record.flag !== '1' ? '正常' : '停用' }}
            </a-tag>
          </span>
        </template>
        <template #faCode="{ record }">{{
            'FA' + record.faCode
          }}
        </template>
        <template #faAccName="{ record }"><u class="tableUStyle" @click="openEdit(record,true)">{{
            record.faAccName
          }}</u></template>
        <template #uniqueCode="{ record }"> {{ formatOrgnInCharge(record.uniqueCode) }}</template>
        <template #currencyType="{ record }"> {{
            formatCurrencyInCharge(record.currencyType)
          }}
        </template>
        <template #countryId="{ record }"> {{ formatDqInCharge(record.countryId) }}</template>
        <template #independent="{ record }"> {{
            record.independent == '1' ? '独立核算' : '集团账套'
          }}
        </template>
        <template #zhejiuCode="{ record }"> {{
            formatZheJiuInCharge(record.zhejiuCode)
          }}
        </template>
        <template #isZhejiu="{ record }"> {{
            record.isZhejiu == '1' ? '是' : '否'
          }}
        </template>

        <template #action="{ record }">
          <div>
            <a-popover placement="bottom">
              <a-button style="padding: 0px 4px; height: 20px;">
                <CaretDownFilled/>
              </a-button>
              <template #content>
                <p class="p_specifics" v-if="record.flag != '1'" @click="change(record,'1')">
                  正常
                </p>
                <p class="p_specifics" v-else @click="change(record,'0')">停用</p>
              </template>
            </a-popover>
          </div>
        </template>
      </BasicTable>
    </div>
  </div>
</template>
<script setup lang="ts">
import {BasicTable, useTable} from '/@/components/Table'

const {closeCurrent} = useTabs(router);
import {useModal} from '/@/components/Modal'
import {
  CaretDownFilled,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  RestOutlined,
  SafetyOutlined,
  ReadOutlined,
  EditOutlined,
  SortAscendingOutlined,
  CheckOutlined, InsertRowAboveOutlined,
  SortDescendingOutlined, AppstoreOutlined, DeleteOutlined
} from '@ant-design/icons-vue'
import {computed, onMounted, ref, reactive, watch} from 'vue'
import {
  changeUnit,
  saveAssets,
  getAssetsList,
  delAssets,
  corpAvailable,
  getFaDelAll, reductionFa, deleteFaTrue
} from '/@/api/record/group/im-unit'
import Edit from './popup/edit.vue';
import Copy from './popup/copy.vue';
import {
  Select as ASelect, Input as AInput, Popover as APopover, Tag as ATag, Popconfirm as APopconfirm,
  Radio as ARadio, Checkbox as ACheckbox,
  Table as ATable, message
} from 'ant-design-vue'
import Recycle from '/@/views/boozsoft/management/im-organize/popup/recycle.vue';

const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option

const toWeb = (u) => {
  window.open('http://' + u)
}
const CrudApi = {
  list: getAssetsList,
  columns: [
    {
      title: '状态',
      dataIndex: 'flag',
      fixed: true,
      slots: {customRender: 'flag'}
    },
    {
      title: '账套代码',
      dataIndex: 'coCode',
      fixed: true,
    },
    /* {
       title: '管理代码',
       dataIndex: 'faCode',
       fixed: true,
       ellipsis: true,slots: {customRender: 'faCode'}
     },*/
    {
      title: '账套名称',
      dataIndex: 'faAccName',
      ellipsis: true,
      fixed: true,
      slots: {customRender: 'faAccName'}
    }, {
      title: '关联账套',
      dataIndex: 'associateCoCode',
      fixed: true,
    }, {
      title: '是否计提',
      dataIndex: 'isZhejiu',
      ellipsis: true,
      slots: {customRender: 'isZhejiu'}
    }, {
      title: '折旧方法',
      dataIndex: 'zhejiuCode',
      ellipsis: true,
      slots: {customRender: 'zhejiuCode'}
    },
    {
      title: '启用日期',
      dataIndex: 'startYearMonth',
      ellipsis: true,
      slots: {customRender: 'startYearMonth'}
    }, {
      title: '所属公司(单位)',
      dataIndex: 'uniqueCode',
      ellipsis: true,
      slots: {customRender: 'uniqueCode'}
    }, {
      title: '本位币',
      dataIndex: 'currencyType',
      ellipsis: true,
      slots: {customRender: 'currencyType'}
    }, {
      title: '国家(地区)',
      dataIndex: 'countryId',
      ellipsis: true,
      slots: {customRender: 'countryId'}
    }, {
      title: '管理性质',
      dataIndex: 'independent',
      ellipsis: true,
      slots: {customRender: 'independent'}
    }
  ],
  editData: {
    id: null,
    accNameCn: '',
    uniqueCode: '',
    faCode: '',
    faAccName: '',
    startYearMonth: '',
    countryId: '',
    currencyType: '',
    zhejiuCode: '1',
    zhejiuPeriod: '1',
    isZhejiu: '1',
    isFilledIn: '0',
    isNowZhidan: '0',
    isCreateZhejiu: '0',
    isYuanzhi: '1',
    isDzSettle: '1',
    isLeijizjtz: '1',
    isJcanzhi: '1',
  }
}

function formatOrgnInCharge(psnInCharge: any) {
  let str = psnInCharge
  unitList.value.forEach(
    function (psn: any) {
      if (psn.id == psnInCharge) {
        // console.log(psn)
        str = psn.coCode + ' ' + psn.accNameCn
      }
    }
  )
  return str
}

function formatCurrencyInCharge(psnInCharge: any) {
  let str = psnInCharge
  currencyList.value.forEach(
    function (psn: any) {
      if (psn.abbreviation == psnInCharge) {
        // console.log(psn)
        str = psn.currencyZhCnName
      }
    }
  )
  return str
}

function formatZheJiuInCharge(psnInCharge: any) {
  let str = psnInCharge
  zhejiuMethodList.value.forEach(
    function (psn: any) {
      if (psn.id == psnInCharge) {
        // console.log(psn)
        str = psn.zjName
      }
    }
  )
  return str
}

function formatDqInCharge(psnInCharge: any) {
  if (psnInCharge == '' || null == psnInCharge) {
    return ""
  } else {
    let str = ""
    countryList.value.forEach((item) => {
      if (item.uniqueCode == psnInCharge) str = item.namech
    })
    return str
  }
}

const currencyList = ref([])
const countryList = ref([])
const unitList = ref([])
const zhejiuMethodList = ref([])
const mark = usePlatformsStore().getCurrentPlatformId
const pageParameter = reactive({
  accGroup: '-1',
  showRulesSize: 'MIN',
  queryMark: 'all',
  pageMark: mark == 1003 ? '1' : '0',
  searchConditon: {
    requirement: 'faAccName',
    value: ''
  },
 userId:  mark == 1003 ? null : useUserStoreWidthOut().getUserInfo.id
})

onMounted(async () => {
  resetDynamicColumnData()
  currencyList.value = (await findCurrencyTypeList()).items
  countryList.value = (await findAllCountry()).items
  unitList.value = await corpAvailable()
  zhejiuMethodList.value = await GetDeptTreeByFlag()
  let auohr = (await findOrgAuthorsById({userId: useUserStoreWidthOut().getUserInfo.id})).map(it=>it.orgUniqueCode)
  if (mark != 1003) { // 只显示组织
    unitList.value = unitList.value.filter(it => !hasBlank(it?.accGroup) && auohr.indexOf(it?.accGroup) != -1)
  }
})
const totalNumber = ref(0)

// 这是示例组件
const [registerTable, {reload, setTableData, getDataSource, setColumns, getColumns}] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  pagination: {
    pageSize: 50,
    simple: true
  },
  /*  actionColumn: {
      width: 100,
      title: '操作',
      dataIndex: 'action',
      slots: {customRender: 'action'}
    },*/
  searchInfo: pageParameter
})
const [registerEditPage, {openModal: openEditPage}] = useModal()
const [registerCopyPage, {openModal: openCopyPage}] = useModal()
const [registerAuthorPage, {openModal: openAuthorPage}] = useModal()

const organizeList = ref([])
const openAddPage = () => {
  let data = CrudApi.editData
  data.isEdit = false
  data.countryList = countryList.value
  data.currencyList = currencyList.value
  data.unitList = unitList.value
  data.zhejiuMethodList = zhejiuMethodList.value
  openEditPage(true, {
    data: data
  })
}

const openCopy = () => {
  let data = CrudApi.editData
  data.isEdit = false
  data.currencyList = currencyList.value
  data.organizeList = organizeList.value
  data.numberDec = '2'
  data.unitPriceDec = '2'
  data.rateDec = '5'
  data.accvouchDec = '4'
  data.periodNum = '12'
  data.independent = '0'
  openCopyPage(true, {
    data: data
  })
}

const openEdit = (source: any, flag) => {
  let data = JSON.parse(JSON.stringify(source))
  data.isEdit = true
  data.isLook = flag
  data.countryList = countryList.value
  data.currencyList = currencyList.value
  data.unitList = unitList.value
  data.zhejiuMethodList = zhejiuMethodList.value
  openEditPage(true, {
    data
  })
}

const openAuthor = () => {
  let data = {}
  openAuthorPage(true, {
    data
  })
}

const del = async (data: any) => {
  await delAssets(data)
  await pointMessage({title: '处理结果', content: '删除成功！', delay: true})
  tableReload()
}

const change = async (data: any, value) => {
  let data1 = {
    id: data.id,
    flag: value
  }
  if (data.flag == value) return false
  await changeUnit(data1)
    .then(async (res) => {
      await pointMessage({title: '处理结果', content: '更新成功！', delay: true})
    }).catch(async () => {
      await pointMessage({title: '处理结果', content: '更新失败！', delay: true})
    }).finally(() => tableReload())
}

async function saveData(obj: any) {
  let data = obj.data
  if (data.id == '') data.id = null
  data = weedOut(data)
  let res = await saveAssets(data)
  obj.closeOpen()
  await pointMessage({title: '处理结果', content: '更新成功！', delay: true})
  writeLog(data.id == null?'新增':'修改', res, null)
  recrawlResetAssetsAccountCache()
  tableReload()
}

const weedOut = (data) => {
  return ObjTool.dels(data, ['unitList', 'currencyList', 'zhejiuMethodList', 'countryList', 'accNameCn'])
}

async function copyData(obj: any) {
  let data = obj.data
  if (data.id == '') data.id = null
  data = weedOut(data)
  await copyUnit(data)
  obj.closeOpen()

  await pointMessage({title: '处理结果', content: '更新成功！', delay: true})
  tableReload()
}


let searchDataSource = []

function onSearch() {
  if (searchDataSource.length == 0) searchDataSource = getDataSource()
  if (hasBlank(pageParameter.searchConditon.value.trim())) {
    setTableData(searchDataSource)
  } else {
    let v = pageParameter.searchConditon.value.trim()
    let searchAfter = searchDataSource.filter(item => item['coCode'].indexOf(v) != -1 || item['associateCoCode'].indexOf(v) != -1 || item['faAccName'].indexOf(v) != -1 || (formatOrgnInCharge(item['uniqueCode'])).indexOf(v) != -1)
    setTableData(searchAfter)
  }
}

function onFilter(v) {
  if (searchDataSource.length == 0) searchDataSource = getDataSource()
  if (v == 'all') {
    setTableData(searchDataSource)
  } else {
    let searchAfter = searchDataSource.filter(item => item['flag'] == v)
    setTableData(searchAfter)
  }
}

/**********************栏目设置**********************/
import {initDynamics, assemblyDynamicColumn} from "./data";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  findDbLanMuList,
  saveLanMuList,
} from '/@/api/record/system/accvoucher';
import {hasBlank, pointMessage} from "/@/api/task-api/tast-bus-api";
import {cloneDeep} from "lodash-es";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {findAllCountry} from "/@/api/record/costomer_data/customer";
import {usePlatformsStore} from "/@/store/modules/platforms";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {
  findCurrencyTypeList,
} from "/@/api/record/system/financial-settings";
import {ObjTool} from "/@/api/task-api/tools/universal-tools";
import {GetDeptTreeByFlag} from "/@/api/record/group/fa-zj";
import {
  recrawlResetAssetsAccountCache
} from "/@/boozsoft/components/AccountPicker/utils/accountUtils";
// import {
//   aoaToSheetXlsx
// } from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
const aoaToSheetXlsx=null
import {saveLog} from "/@/api/record/system/group-sys-login-log";
import {findOrgAuthorsById} from "/@/api/record/group/im-organize";

const {createConfirm, createWarningModal} = useMessage();
const searchConditonList = ref([])

const visible = ref(false);
const windowWidth = (window.innerWidth - (70/*+280*/))
const windowHeight = (window.innerHeight - (350))
const totalColumnWidth = ref(0)
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});
const tableRef = ref(null)
const lanMuData = {
  'accId': 'postgres',
  'menuName': '固定资产账套列表',
  'type': '集团',
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
      //lanMuData.accId = getCurrentAccountName(false)
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
    let arr = key.split('-');
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
    let arr = key.split('-');
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
    let arr = key.split('-');
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
    } catch (e) {
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
  let a = []
  a = getColumns()
  let last = a.pop()
  let newA = JSON.parse(JSON.stringify(CrudApi.columns))
  newA = assemblyDynamicColumn(dynamicColumnData.value, newA)
  // newA.push(last)
  setColumns(newA)
  initTableWidth(newA)
  searchConditonList.value = newA
}

function resetDynamicColumnData() {
  // 先从数据查询是否已经设置
  //lanMuData.accId = getCurrentAccountName(false)
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
  // debugger
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

const tableReload = () => {
  reload({
    searchInfo: pageParameter
  })
}
const onChangeSwitch = (value) => {
  pageParameter.queryMark = value;
  /*tableReload()*/
  onFilter(value)
}
/*栏目设置end*/
const tableSelectedRowKeys = ref([])
const tableSelectedRowObjs = ref([])
const onSelectChange = (selectedRowKeys, objs) => {
  tableSelectedRowKeys.value = selectedRowKeys
  tableSelectedRowObjs.value = objs
};
const editBefore = () => {
  if (tableSelectedRowKeys.value.length == 0) {
    message.info("请选择需要进行修改的行且只能是一行！")
  } else {
    openEdit(tableSelectedRowObjs.value[0], false)
  }
}

const delBefore = async () => {
  if (tableSelectedRowKeys.value.length != 1) {
    message.info("请至少并只能选择一行进行删除！")
  } else {
    createConfirm({
      iconType: 'warning',
      title: '固定资产账删除',
      content: '您确定要删除所选的固定资产账吗?',
      onOk: async () => {
        // createWarningModal({content: '请先做修改后再进行确认同步数据库！'})
        let entity = tableSelectedRowObjs.value[0]
        entity.delName = lanMuData.username
        await delAssets(entity).then(async (res) => {
          if (null != res && res.isOk == false) {
          } else {
            await pointMessage({title: '处理结果', content: '删除成功！', delay: true})
            tableSelectedRowObjs.value = []
            tableSelectedRowKeys.value = []
            recrawlResetAssetsAccountCache()
            writeLog("删除", entity, null)
          }
        }).catch(async () => {
          await pointMessage({title: '处理结果', content: '删除失败！', delay: true})
        }).finally(() => tableReload())
      }
    });
  }
}
const [registerRecyclePage, {openModal: openRecyclePage}] = useModal()
const recycleList = ref([])
const openRecycleDel = async () => {
  // 获取回收站数据
  recycleList.value = await getFaDelAll()
  if (recycleList.value.length == 0) {
    await pointMessage({title: '温馨提起', content: '当前回收站为空！', delay: true})
  } else {
    openRecyclePage(true, {
      dynamicTenantId: '',
      dataList: recycleList.value.map(it => ({
        id: it.id,
        code: it.coCode,
        name: it.faAccName,
        delName: it.delName,
        delDate: it.delDate
      }))
    })
  }
}
const recycleModify = async (e) => {
  if (e.type == 'reduce') {
    const data = e.data
    let model = recycleList.value.filter(it => it.id == e.data.id)[0]
    await reductionFa(data)
    e.callback()
    writeLog("还原", model, null)
    tableReload()
  } else if (e.type == 'del') {
    const data = e.data
    let model = recycleList.value.filter(it => it.id == e.data.id)[0]
    await deleteFaTrue(data).then(async (res) => {
      if (null != res && res.isOk == false) {
        await pointMessage({
          title: '处理结果',
          content: `删除失败,该组织下已经建立了${res.type == 'D02' ? '公司' : '下级组织'},无法进行删除！`,
          delay: true
        })
      } else {
        e.callback()
        writeLog("移除", model, null)
      }
    }).catch(async () => await pointMessage({title: '处理结果', content: '删除失败！', delay: true}))
  } else {
    startExport('del')
  }
}

function startExport(type) {
  // console.log(arrHeader)
  // 保证data顺序与header一致
  let name = '自定义项设置'
  let dataList = []
  let keys = CrudApi.columns.map(it => it.dataIndex)
  let titleList = CrudApi.columns.map(it => it.title)
  if (type == 'table') {
    name = '固定资产账套列表'
    dataList = getDataSource().map((item) => keys.map(column => transformText(column, item[column])));
  } else {
    name = '固定资产账套回收站列表'
    titleList.push(...['删除人', '删除时间'])
    keys.push(...['delName', 'delDate'])
    dataList = recycleList.value.map((item) => keys.map(column => transformText(column, item[column])));
  }
  if (dataList.length == 0) {
    createWarningModal({title: '温馨提示', content: '暂未发现可进行导出的数据！'})
  } else {
    aoaToSheetXlsx({
      data: dataList,
      header: titleList,
      filename: name + '.xlsx',
    });
  }
}

/************ 日志 *************/
async function writeLog(action, a, content) {
  /************** 记录操作日志 ****************/
  if (action == '修改') {
    let old = getDataSource().filter(it => it.id == a.id)[0]
    let keys = (Object.keys(old)).filter(k => old[k] != a[k] && k != 'key')
    let text = `操作内容【修改固定资产账套】,`+ '账套代码【' + a.coCode + '】,账套名称【' + a.faAccName + '】,'
    for (let i = 0; i < keys.length; i++) {
      const k = keys[i];
      let t = CrudApi.columns.filter(t => t.dataIndex == k)[0]?.title
      text += `${t}【${transformText(k, old[k])},${transformText(k, a[k])}】;`
    }
    content = text.substring(0, text.length - 1)
  }
  let map = {
    loginTime: new Date(+new Date() + 8 * 3600 * 1000).toJSON().substr(0, 19).replace("T", " "),
    userId: useUserStoreWidthOut().getUserInfo.username,
    userName: useUserStoreWidthOut().getUserInfo.realName,
    optModule: 'group',
    optFunction: '固定资产账套',
    optRange: '2',
    uniqueCode: '',
    optAction: action,
    optContent: content || `操作内容【${action}固定资产账套】,` + '账套代码【' + a.coCode + '】,账套名称【' + a.faAccName + '】',
  }
  await saveLog(map)
}

function transformText(key, value) {
  let text = value
  switch (key) {
    case "flag":
      text = value != '1' ? '正常' : '停用'
      break;
    case "uniqueCode":
      text = formatOrgnInCharge(value)
      break;
    case "currencyType":
      text = formatCurrencyInCharge(value)
      break;
    case "countryId":
      text = formatDqInCharge(value)
      break;
    case "zhejiuCode":
      text = formatZheJiuInCharge(value)
      break;
    case "isZhejiu":
      text = value == '1' ? '是' : '否'
      break;
    case "independent":
      text = value == '1' ? '独立核算' : '集团账套'
      break;
  }
  return text
}

/************ 日志 *************/

</script>
<style lang="less" scoped>
@import '/@/assets/styles/global-group-org-style.less';
</style>

