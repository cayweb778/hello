<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <AppstoreOutlined style="font-size: 36px;color: #0096c7;" class="container-head-img"/>
        <div class="container-head-title2">
          <b class="noneSpan">公司(单位)列表</b>
        </div>
        <span
          style="font-size: 12px;color: black;position: absolute;top: 90px;left: 25px">共{{
            totalNumber
          }}条记录</span>
        <div class="ant-btn-group" style="float: right">
          <button type="button" class="ant-btn ant-btn-me" v-if="mark == 1003" @click="openAddPage()">新建</button>
          <button type="button" class="ant-btn ant-btn-me" v-if="mark == 1003" @click="editBefore">修改</button>
          <button type="button" class="ant-btn ant-btn-me" v-if="mark == 1003" @click="delBefore">删除</button>
          <!--          <button type="button" class="ant-btn ant-btn-me" @click="openAuthor()">授权</button>-->
          <button type="button" class="ant-btn ant-btn-me">备份</button>
          <button type="button" class="ant-btn ant-btn-me">恢复</button>
          <button type="button" class="ant-btn ant-btn-me"
                  @click="closeCurrent(),router.push(`/${mark == 1003?'system':'origin'}/home/welcome`)">
            退出
          </button>
          <!--          <button type="button" class="ant-btn ant-btn-me" @click="resetOrgCode">reset</button>-->
        </div>
      </div>
      <div class="app-container-neck">
        <!--        <div style="display: inline-block;float: left;margin-top: .2%;" class="select-div">
                  &ensp;<span>所属组织：</span>
                  <a-select v-model:value="pageParameter.accGroup" @change="pageChange"
                            style="width: 250px;margin-top: -10px;margin-left: 10px">
                    <a-select-option key="-1">全部公司(单位)</a-select-option>
                    <a-select-option key="0" v-if="mark == 1003">独立核算</a-select-option>
                    <a-select-option v-for="item in organizeList" :key="item.uniqueCode">
                      {{ item.orgSimpName }}
                    </a-select-option>
                  </a-select>
                </div>-->
        <div style="float: right; margin-left: 10px">
          <a-button style="padding: 0px 12px !important;font-size: 14px" @click="tableReload">
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
                  <div style="width: 640px">
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
          <a-button title="回收站" @click="openRecycleDel">
            <DeleteOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <!--          <a-popover class="ant-btn-default" placement="bottom">
                      <template #content>
                      <span class="group-btn-span-special2" @click="onChangeSwitch('all')"
                            :style="pageParameter.queryMark=='all'?{backgroundColor: '#0096c7',color: 'white'}:''">
                          <ReadOutlined/>&nbsp;&emsp;全部&emsp;&ensp;<CheckOutlined
                        v-if="pageParameter.queryMark=='all'"/></span><br/>
                        <span class="group-btn-span-special2" @click="onChangeSwitch('1')"
                              :style="pageParameter.queryMark=='1'?{backgroundColor: '#0096c7',color: 'white'}:''">
                          <SafetyOutlined/>&nbsp;&emsp;启用&emsp;&ensp;<CheckOutlined
                          v-if="pageParameter.queryMark=='1'"/></span><br/>
                        <span class="group-btn-span-special2" @click="onChangeSwitch('0')"
                              :style="pageParameter.queryMark=='0'?{backgroundColor: '#0096c7',color: 'white'}:''">
                            <RestOutlined/>&nbsp;&emsp;封存&emsp;&ensp;<CheckOutlined
                          v-if="pageParameter.queryMark=='0'"/></span>
                      </template>
                      <a-button>
                        <PicLeftOutlined :style="{ fontSize: '14px' }"/>
                      </a-button>
                    </a-popover>-->
        </div>
        <div class="act-two-d-right">
          <div class="acttd-right-d-search">
            <a-select v-model:value="pageParameter.searchConditon.requirement"
                      class="acttdrd-search-select">
              <template v-for="item in searchConditonList">
                <a-select-option v-if="item.ifShow == true" :value="item.dataIndex">
                  {{ item.title }}
                </a-select-option>
              </template>
            </a-select>
            <a-input-search
              style="width: 180px;"
              v-model:value="pageParameter.searchConditon.value"
              class="acttdrd-search-input"
              @search="onSearch"
            />
          </div>
        </div>
      </div>
    </div>
    <div class="app-container">
      <Recycle @modify="recycleModify" @register="registerRecyclePage"/>
      <Edit @save="saveData" @register="registerEditPage"/>
      <Authorization @register="registerAuthorPage"/>
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
            <a-tag :color="record.flag === '1' ? 'green' : 'volcano'">
              {{ record.flag === '1' ? '启用' : '封存' }}
            </a-tag>
          </span>
        </template>
        <template #accName="{ record }"><u class="tableUStyle"
                                           @click="openEdit(record,true)">{{ record.accName }}</u>
        </template>
        <template #accNameCn="{ record }"><u class="tableUStyle"
                                           @click="openEdit(record,true)">{{ record.accNameCn }}</u>
        </template>
        <template #accGroup="{ record }"> {{ formatOrgnInCharge(record.accGroup) }}</template>
        <template #industryclassCode="{ record }"> {{formatHyInCharge(record.industryclassCode) }}</template>
        <template #uniqueCodeZone="{ record }"> {{formatXzInCharge(record.uniqueCodeZone) }}</template>
        <template #corpCode="{ record }"> {{ formatDwInCharge(record.corpCode) }}</template>
        <template #countryId="{ record }"> {{ formatDqInCharge(record.countryId) }}</template>
        <template #website="{ record }"><a class="tableUStyle"
                                           @click="toWeb(record.website)">{{ record.website }}</a>
        </template>
        <template #action="{ record }">
          <div>
            <a-popover placement="bottom">
              <a-button style="padding: 0px 4px; height: 20px;">
                <CaretDownFilled/>
              </a-button>
              <template #content>
                <p class="p_specifics" v-if="record.flag != '1'" @click="change(record,'1')">
                  启用
                </p>
                <p class="p_specifics" v-else @click="change(record,'0')">封存</p>
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
  FormOutlined,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  RestOutlined,
  SafetyOutlined,
  ReadOutlined,
  EditOutlined,
  SortAscendingOutlined,
  CheckOutlined,
  SortDescendingOutlined, AppstoreOutlined
} from '@ant-design/icons-vue'
import {computed, onMounted, ref, reactive, watch} from 'vue'
import {
  getUnitList,
  deleteUnit,
  changeUnit,
  saveUnit,
  resetCode, getCorpDelAll, reductionCorp, deleteCorpTrue
} from '/@/api/record/group/im-unit'
import Edit from './popup/edit.vue';
import Authorization from './popup/Authorization.vue';
import Recycle from '/@/views/boozsoft/management/im-organize/popup/recycle.vue';
import {getDeptListById} from "/@/api/record/system/dept";
import {psnFindAll} from "/@/api/psn/psn";
import {projectClassFindAll} from "/@/api/record/system/project_class";
import {
  Select as ASelect, Input as AInput, Popover as APopover, Tag as ATag, Popconfirm as APopconfirm,
  Radio as ARadio, Checkbox as ACheckbox, Upload as AUpload,
  Table as ATable, message
} from 'ant-design-vue'

const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option

//选择项目大类
const cateCode: any = ref()
const currentCateCode = computed(() => cateCode.value)

// 临时
const fileList = ref([])
const headers = {
  'Authorization': useUserStoreWidthOut().getToken,
  'datasource': window.localStorage.getItem('datasource')
}

watch(currentCateCode, (abc) => {
  // projectStore.projectClassTree()
  reload({
    searchInfo: {
      projectCateCode: abc
    }
  })
})
//根据基础档案查询基础档案栏目
const cateList: any = ref([])
/*async function reloadCate() {
  const res: any = await cateFindStateFlag()
  cateList.value = res
  if (res.length>0) {
    cateCode.value = res[0].projectCateCode
    // console.log(currentCateCode)
  }
}*/
const toWeb = (u) => {
  window.open('http://' + u)
}
const CrudApi = {
  list: getUnitList,
  columns: [
    /* {
       title: '状态',
       dataIndex: 'flag',
       fixed: true,
       slots: {customRender: 'flag'}
     },*/
    {
      title: '公司(单位)代码',
      dataIndex: 'coCode',
      fixed: true,
      ellipsis: true
    },
    {
      title: '公司(单位)名称',
      dataIndex: 'accName',
      ellipsis: true,
      fixed: true,
      slots: {customRender: 'accName'}
    },{
      title: '公司(单位)简称',
      dataIndex: 'accName',
      ellipsis: true,
      fixed: true,
      slots: {customRender: 'accNameCn'}
    },
    {
      title: '所属组织',
      dataIndex: 'accGroup',
      ellipsis: true,
      slots: {customRender: 'accGroup'}
    },
    {
      title: '所属行业',
      dataIndex: 'industryclassCode',
      ellipsis: true,
      slots: {customRender: 'industryclassCode'}
    },
    {
      title: '行政区划',
      dataIndex: 'uniqueCodeZone',
      ellipsis: true,
      slots: {customRender: 'uniqueCodeZone'}
    },
    {
      title: '上级单位',
      dataIndex: 'corpCode',
      ellipsis: true,
      slots: {customRender: 'corpCode'}
    },
    {
      title: '国家(地区)',
      dataIndex: 'countryId',
      ellipsis: true,
      slots: {customRender: 'countryId'}
    },
    {
      title: '税号',
      dataIndex: 'taxCode',
      ellipsis: true
    },
    {
      title: '联系人',
      dataIndex: 'contacts',
      ellipsis: true,
      slots: {customRender: 'contacts'}
    },
    {
      title: '联系电话',
      dataIndex: 'telephone',
      ellipsis: true,
    },
    {
      title: '通讯地址',
      dataIndex: 'address',
      ellipsis: true,
    },
    {
      title: '官网地址',
      dataIndex: 'website',
      ellipsis: true,
      slots: {customRender: 'website'}
    },
    {
      title: '说明',
      dataIndex: 'remarks',
      ellipsis: true,
    },
  ],
  editData: {
    id: '',
    coCode: '',
    accName: '',
    accId: '',
    accGroup: '',
    accNameCn: '',
    industryclassCode: '',
    uniqueCodeZone: '',
    corpCode: '',
    countryId: '',
    taxCode: '',
    contacts: '',
    telephone: '',
    address: '',
    website: '',
    remarks: '',
    independent: '1',
    accStandard: '',
    ccodeLevel: '',
    ibudgetAccStandard: '1',
    numberDec: '',
    unitPriceDec: '',
    rateDec: '',
    accvouchDec: '',
    currency: '',
    currencyName: '',
    currencyCh: '',
    voucherTypeNum: '',
    yearStartDate: '',
    yearEndDate: '',
    periodNum: '',
    startPeriod: '',
    invoiceLookUp: '',
    invoiceIdentifier: '',
    invoiceAddressPhone: '',
    invoiceBanks: '',
  }
}
//项目分类
const proClassList = ref([])

async function reloadProClass() {
  const res: any = await projectClassFindAll();
  proClassList.value = res
  // console.log(proClassList.value)
}

function formatProjectClassCode(projectClassCode: any) {
  let str = projectClassCode
  // console.log(projectClassCode)
  proClassList.value.forEach(
    function (proClass: any) {
      if (proClass.uniqueCode == projectClassCode) {
        // console.log(proClass)
        str = proClass.projectClassName
      }
    }
  )
  return str
}

//部门
const deptList = ref([])

async function reloadDept() {
  const res: any = await getDeptListById()
  deptList.value = res.items
  // console.log(deptList.value)
}

function formatDeptCode(deptCode: any) {
  let str = deptCode
  // console.log(deptCode)
  deptList.value.forEach(
    function (dept: any) {
      if (dept.uniqueCode == deptCode) {
        // console.log(dept)
        str = dept.deptName
      }
    }
  )
  return str
}

//人员
const psnList = ref([])

async function reloadPsn() {
  const res: any = await psnFindAll()
  psnList.value = res.items
  // console.log(psnList.value)
}

function formatPsnInCharge(psnInCharge: any) {
  let str = psnInCharge
  // console.log(psnInCharge)
  psnList.value.forEach(
    function (psn: any) {
      if (psn.uniqueCode == psnInCharge) {
        // console.log(psn)
        str = psn.psnName
      }
    }
  )
  return str
}

function formatOrgnInCharge(psnInCharge: any) {
  let str = psnInCharge
  // console.log(psnInCharge)
  organizeList.value.forEach(
    function (psn: any) {
      if (psn.uniqueCode == psnInCharge) {
        // console.log(psn)
        str = psn.orgName
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

function formatHyInCharge(psnInCharge: any) {
  if (psnInCharge == '' || null == psnInCharge) {
    return ""
  } else {
    let arr = JSON.parse(psnInCharge);
    let str = ""
    if (arr.length >= 1) {
      industryList.value.forEach((item) => {
        if (item.value == arr[0]) {
          str += item.label
          if (item.children.length > 0) {
            item.children.forEach((item1) => {
              if (item1.value == arr[1]) {
                str = str + ' / ' + item1.label
              }
            })
          }
        }
      })
    }
    return str
  }
}

function formatXzInCharge(psnInCharge: any) {
  if (psnInCharge == '' || null == psnInCharge) {
    return ""
  } else {
    let arr = JSON.parse(psnInCharge);
    let str = ""
    if (arr.length >= 1) {
      zoningList.value.forEach((item) => {
        if (item.value == arr[0]) {
          str += item.label
          if (item.children.length > 0) {
            item.children.forEach((item1) => {
              if (item1.value == arr[1]) {
                str = str + ' / ' + item1.label
                if (item1.children.length > 0) {
                  item1.children.forEach((item2) => {
                    if (item2.value == arr[2]) {
                      str = str + ' / ' + item2.label
                    }
                  })
                }
              }
            })
          }
        }
      })
    }
    return str
  }
}

function formatDwInCharge(psnInCharge: any) {
  if (psnInCharge == '' || null == psnInCharge) {
    return ""
  } else {
    let str = ""
    countryList.value.forEach((item) => {
      if (item.uniqueCode == psnInCharge) str = item.accNameCn
    })
    return str
  }
}

const countryList = ref([])
const zoningList = ref([])
const industryList = ref([])
onMounted(async () => {
  //await reloadDept()
  // await reloadPsn()
  // await reloadProClass()
  //await reloadCate()
  resetDynamicColumnData()
  organizeList.value = await getOrganizeAll()
  countryList.value = (await findAllCountry()).items
  zoningList.value = (await findAllProvince())
  industryList.value = (await findAllIndustry())
})
const totalNumber = ref(0)
const mark = usePlatformsStore().getCurrentPlatformId
const pageParameter = reactive({
  accGroup: '-1',
  showRulesSize: 'MIN',
  queryMark: 'all',
  pageMark: mark == 1003 ? '1' : '0',
  userId:  mark == 1003 ? null : useUserStoreWidthOut().getUserInfo.id,
  searchConditon: {
    requirement: 'accName',
    value: ''
  }
})
// 这是示例组件
const [registerTable, {reload, setTableData, getDataSource, setColumns, getColumns}] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  pagination: {
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['10', '30', '50', '100'],
    showTotal: (t) => {
      totalNumber.value = t
      return `共${t}条记录`
    }
  },
 /* actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  },*/
  searchInfo: pageParameter
})
const [registerEditPage, {openModal: openEditPage}] = useModal()
const [registerAuthorPage, {openModal: openAuthorPage}] = useModal()

const organizeList = ref([])
const openAddPage = () => {
  let data = CrudApi.editData
  data.isEdit = false
  data.organizeList = organizeList.value
  data.countryList = countryList.value
  data.zoningList = zoningList.value
  data.industryList = industryList.value
  data.upList = getDataSource()
  data.numberDec = '2'
  data.unitPriceDec = '2'
  data.rateDec = '5'
  data.accvouchDec = '4'
  data.periodNum = '12'
  data.independent = '0'
  openEditPage(true, {
    data: data
  })
}
const openEdit = (source: any, flag) => {
  let data = JSON.parse(JSON.stringify(source))
  data.isEdit = true
  data.isLook = flag
  data.organizeList = organizeList.value
  data.countryList = countryList.value
  data.zoningList = zoningList.value
  data.industryList = industryList.value
  data.upList = getDataSource()
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
  //await deleteUnit(data)
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
import {ACCOUNT_LIST_KEY, AUTHORIZE_LIST_KEY} from '/@/enums/cacheEnum';

async function saveData(obj: any) {
  let data = obj.data
  if (data.id == '') data.id = null
  data = weedOut(data)
  let res = await saveUnit(data)
  // 写日志
  writeLog(data.id == null ? '新增' : '修改', res, null)
  obj.closeOpen()
  await pointMessage({title: '处理结果', content: '更新成功！', delay: true})
  tableReload()
}


const weedOut = (data) => {
  return ObjTool.dels(data, ['upList', 'organizeList', 'countryList', 'zoningList', 'isEdit', 'industryList', 'voucherTypeOtherNums'])
}

let searchDataSource = []

function onSearch() {
  if (searchDataSource.length == 0) searchDataSource = getDataSource()
  if (hasBlank(pageParameter.searchConditon.value.trim())) {
    setTableData(searchDataSource)
  } else {
    let searchAfter = searchDataSource.filter(item => hasBlank(item[pageParameter.searchConditon.requirement]) ? false : (geItValue(item, pageParameter.searchConditon.requirement)).indexOf(pageParameter.searchConditon.value.trim()) != -1)
    setTableData(searchAfter)
  }
}

const geItValue = (o, key) => {
  if (key == 'industryclassCode' || key == 'accGroup' || key == 'uniqueCodeZone' || key == 'countryId' || key == 'corpCode') {
    let text = ''
    switch (key) {
      case 'industryclassCode':
        text = formatHyInCharge(o[key])
        break;
      case 'accGroup':
        text = formatOrgnInCharge(o[key])
        break;
      case 'uniqueCodeZone':
        text = formatXzInCharge(o[key])
        break;
      case 'countryId':
        text = formatDqInCharge(o[key])
        break;
      case 'corpCode':
        text = formatDwInCharge(o[key])
        break;
    }
    return text
  } else {
    return o[key]
  }
}

/**********************栏目设置**********************/
import {initDynamics, assemblyDynamicColumn} from "./data";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  findDbLanMuList,
  saveLanMuList,
} from '/@/api/record/system/accvoucher';
import {
  getCurrentAccountName, hasBlank, pointMessage, getThisIndexImg
} from "/@/api/task-api/tast-bus-api";
import {cloneDeep} from "lodash-es";
import {Message} from "postcss";
import {
  deleteOrganizeTrue,
  getOrganizeAll, getOrganizeDelAll,
  reductionOrganize
} from "/@/api/record/group/im-organize";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {findAllCountry} from "/@/api/record/costomer_data/customer";
import {findAllProvince} from "/@/api/record/system/zone";
import {findAllIndustry} from "/@/api/record/group/im-group";
import {usePlatformsStore} from "/@/store/modules/platforms";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {ObjTool} from "/@/api/task-api/tools/universal-tools";
import {accTemplateDel, codekemuDel, findByTOrganization} from "/@/api/acctemplate/acctemplate";
import {saveLog} from "/@/api/record/system/group-sys-login-log";
//import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
const aoaToSheetXlsx=null


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
  'menuName': '公司信息列表',
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

const pageChange = (value) => {
  let data = {'accGroup': value}
  reload({
    searchInfo: pageParameter
  })
}

const tableReload = () => {
  reload({
    searchInfo: pageParameter
  })
}
const onChangeSwitch = (value) => {
  pageParameter.queryMark = value;
  tableReload()
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
      title: '公司（单位）删除',
      content: '您确定要删除所选的公司（单位）吗?',
      onOk: async () => {
        // createWarningModal({content: '请先做修改后再进行确认同步数据库！'})
        let entity = tableSelectedRowObjs.value[0]
        // await busUnit(entity).then(async (res) => {
        entity.delName = lanMuData.username
        await deleteUnit(entity).then(async (res) => {
          if (null != res && res.isOk == false) {
            await pointMessage({
              title: '处理结果',
              content: `删除失败,该公司（单位）下已经建立了${res.type == '02' ? '账套' : '下级公司（单位）或工厂'},无法进行删除！`,
              delay: true
            })
          } else {
            writeLog("删除", entity, null)
            await pointMessage({title: '处理结果', content: '删除成功！', delay: true})
          }
        }).catch(async () => {
          await pointMessage({title: '处理结果', content: '删除失败！', delay: true})
        }).finally(() => tableReload())
      }
    });
  }
}

const resetOrgCode = async () => {
  let data = {
    id: '1451420156315176960',
    startPeriod: '2021',
  }
  await resetCode(data)
}
const [registerRecyclePage, { openModal: openRecyclePage }] = useModal()
const recycleList = ref([])
const openRecycleDel = async () => {
  // 获取回收站数据
  recycleList.value = await getCorpDelAll()
  if (recycleList.value.length == 0){
    await pointMessage({title: '温馨提起', content: '当前回收站为空！', delay: true})
  }else {
    openRecyclePage(true, {
      dynamicTenantId: '',
      dataList: recycleList.value.map(it=>({id: it.id,code: it.coCode,name: it.accNameCn,delName: it.delName,delDate: it.delDate}))
    })
  }
}
const recycleModify = async (e) => {
  if (e.type == 'reduce'){
    const data = e.data
    let model = recycleList.value.filter(it=>it.id == e.data.id)[0]
    await reductionCorp(data)
    e.callback()
    writeLog("还原",model,null)
    tableReload()
  }else if (e.type == 'del'){
    const data = e.data
    let model = recycleList.value.filter(it=>it.id == e.data.id)[0]
    await deleteCorpTrue(data).then(async (res) => {
      if (null != res && res.isOk == false) {
        await pointMessage({
          title: '处理结果',
          content: `删除失败,该组织下已经建立了${res.type == 'D02' ? '公司' : '下级组织'},无法进行删除！`,
          delay: true
        })
      } else {
        writeLog("移除",model,null)
        e.callback()
      }
    }).catch(async () => await pointMessage({title: '处理结果', content: '删除失败！', delay: true}))
  }else {
    startExport('del')
  }
}
function startExport(type) {
  // console.log(arrHeader)
  // 保证data顺序与header一致
  let name = '自定义项设置'
  let dataList = []
  let keys = CrudApi.columns.map(it=>it.dataIndex)
  let titleList = CrudApi.columns.map(it=>it.title)
  if (type=='table'){
    name = '公司(单位)列表'
    dataList =  getDataSource().map((item) => keys.map(column=>transformText(column,item[column])));
  }else {
    name = '公司(单位)回收站列表'
    titleList.push(...['删除人','删除时间'])
    keys.push(...['delName','delDate'])
    dataList =  recycleList.value.map((item) => keys.map(column=>transformText(column,item[column])));
  }
  if (dataList.length == 0){
    createWarningModal({title: '温馨提示', content: '暂未发现可进行导出的数据！'})
  }else {
    aoaToSheetXlsx({
      data: dataList,
      header: titleList,
      filename: name+'.xlsx',
    });
  }
}
/************ 日志 *************/
async function writeLog(action, a, content) {
  /************** 记录操作日志 ****************/
  if (action == '修改') {
  let old = getDataSource().filter(it => it.id == a.id)[0]
    let keys = (Object.keys(old)).filter(k => old[k] != a[k] && k != 'key')
    let text = `操作内容【修改公司(单位)】,`+ '公司(单位)代码【' + a.coCode + '】,公司(单位)名称【' + a.accName + '】,'
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
    optFunction: '公司(单位)',
    optRange: '2',
    uniqueCode: '',
    optAction: action,
    optContent: content || `操作内容【${action}公司(单位)】,` + '公司(单位)代码【' + a.coCode + '】,公司(单位)名称【' + a.accName + '】,公司(单位)简称【' + a.accNameCn + '】',
  }
  await saveLog(map)
}

function transformText(key, value) {
  let text = value
  switch (key) {
    case "accGroup":
      text = formatOrgnInCharge(value)
      break;
    case "industryclassCode":
      text = formatHyInCharge(value)
      break;
    case "uniqueCodeZone":
      text = formatXzInCharge(value)
      break;
    case "corpCode":
      text = formatDwInCharge(value)
      break;
    case "countryId":
      text = formatDqInCharge(value)
      break;
  }
  return text
}
/************ 日志 *************/
</script>
<style lang="less" scoped>
@import '/@/assets/styles/global-menu-index.less';
</style>
<style scoped>
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
  padding: 5px 8px !important;
}

.a-table-font-size-16 :deep(th), .a-table-font-size-12 :deep(th) {
  text-align: center !important;
  font-weight: bold;
  background-color: #f1f1f1;
}

.tableUStyle {
  color: #0798c8;
  cursor: pointer;
  text-decoration: none;
}

.tableUStyle:hover {
  color: #b4c8e3;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
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
</style>
