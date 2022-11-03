<template>
  <div>
    <div class="app-container">

      <div class="app-container-head">
        <!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">-->
        <div class="container-head-title" style="float: left;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 40%;text-align: center;margin-top: 20px;">
          <b class="noneSpan" style="font-size: 26px;">银行对账单</b>
          <div style="font-size: 14px;text-align: center;margin-top: 10px;">
            <span style="font-size: 14px;font-weight: bold;color: #666666;">年度：</span>
            <a-select v-model:value="year" @change="checkDate()" style="width: 80px;" placeholder="年度">
              <template #suffixIcon><CalendarOutlined /></template>
              <a-select-option v-for="item in yearList" :key="item.accountYear" :value="item.accountYear">
                {{ item.accountYear }}
              </a-select-option>
            </a-select>
          </div>
        </div>

        <!--      <div style="display: inline">
                <a-select v-model:value="kemu" @change="checkDate()" class="head-index-select" placeholder="请选择科目">
                  <a-select-option v-for="item in codeKemu" :key="item.ccode" :value="item.ccode">
                    {{ item.ccode }}-{{ item.ccodeName }}
                  </a-select-option>
                </a-select>
                <a-select v-model:value="year" @change="checkDate()" class="head-index-select" placeholder="请选择年度">
                  <a-select-option v-for="item in yearList" :key="item.accountYear" :value="item.accountYear">
                    {{ item.accountYear }}
                  </a-select-option>
                </a-select>
              </div>-->

        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="openQuery()"
          ><span>查询</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openAddPage()"
          ><span>新建</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="editOpen()"
          ><span>修改</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="delList()"
          ><span>删除</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="openExcel()"
          ><span>导入</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="exportExcel()"
          ><span>导出</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="closeCurrent()"
          ><span>退出</span></button>
        </div>
      </div>

      <div style="clear: none"/>
      <div style="margin-top: -30px;margin-left: 90px;">
        <div style="display: inline-block;float: left;margin-top: -35px;">
          <AccountPicker readonly theme="three" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="display: inline-block;float: left;margin-left: 5px;font-weight: bold;">
          科目：
          <a-select v-model:value="kemu" @change="checkDate()" placeholder="请选择科目"
                    style="width: 300px;">
            <a-select-option v-for="item in codeKemu" :key="item.ccode" :value="item.ccode">
              {{ item.ccode }}-{{ item.ccodeName }}
            </a-select-option>
          </a-select>
          <span style="font-size: 14px;">&emsp;&emsp;本位币：{{ biZhong }}</span>
<!--          <span style="font-size: 14px;">&emsp;&emsp;状态：{{
              flag == '' ? '全部' : ''
            }}{{ flag == '0' ? '未对账' : '' }}{{ flag == '1' ? '已对账' : '' }}</span>-->
          <!--      &emsp;年度：<a-select v-model:value="year" @change="checkDate()" placeholder="请选择年度" style="width: 100px;">
                  <a-select-option v-for="item in yearList" :key="item.accountYear" :value="item.accountYear">
                    {{ item.accountYear }}
                  </a-select-option>
                </a-select>-->
        </div>

        <div style="float: right; margin-left: 10px">
          <a-button class="ant-btn-me">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <a-popover class="ant-btn-default" placement="bottom">
            <template #content>
              <a-popconfirm
                placement="leftTop"
                ok-text="确定"
                cancel-text="放弃"
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

          <!--        <a-button>
                    <EditFilled :style="{ fontSize: '14px' }" />
                  </a-button>

                  <a-button>
                    <PieChartFilled :style="{ fontSize: '14px' }" />
                  </a-button>-->
<!--          <a-button>
            <FilterFilled :style="{ fontSize: '14px' }"/>
          </a-button>-->
        </div>
        <div style="float: right; position: relative">
          <!--        <label style="font-size: 14px">检索条件：</label>-->
          <a-select v-model:value="formItems.selectType" style="width: 100px;font-size: 12px;"
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
            style="width: 150px; border-radius: 4px"
            @search="onSearch"
          />
        </div>
      </div>
      <div style="clear:both"/>
    </div>
    <div class="app-container">
      <Edit
        @save="saveData"
        @register="registerEditPage"
      />
      <Excel
        @save="saveExcel"
        @register="registerExcelPage"
      />
      <Query
        @save="saveQuery"
        @register="registerQueryPage"
      />
      <PageWrapper dense content-full-height fixed-height content-class="flex">
<!--        <div class="w-1/4 xl:w-1/5" v-show="showTree" style="width: 200px;min-height: 200px;border: 1px #cccccc solid;margin-right: .2%;margin-bottom: 50px;">
          <div class="bg-white">
            <b style="font-size: 20px;line-height: 50px;margin-left: 20px;color: #0096c7;">时间</b>
            <BasicTree
              :click-row-to-expand="false"
              :tree-data="treeData"
              checkable
              :selectable="false"
              :replace-fields="{ key: 'value', title: 'title' }"
              v-model:checkedKeys="checkedKeys"
              v-model:expandedKeys="expandedKeys"
              @select="handleSelect"
            />
          </div>
        </div>-->
<!--        <div style="float: right;" :style="showTree?{width: 'calc(100% - 200px)'}:{width: '100%'}">-->
      <BasicTable
        :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys,getCheckboxProps:rowSelection.getCheckboxProps, onChange: onSelectChange }"
        @selection-change="selectionChange"
        @row-click="rowClick"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        :rowClassName="record => record.flag=='1' ? 'table-striped' : null"
        :scroll="{ x: totalColumnWidth,y: windowHeight }"
        @register="registerTable"
        :dataSource="tableData"
      >
        <template #settModes="{ record }">
          {{ formatSettModes(record.settModes) }}
        </template>
        <template #jie="{ record }">
          <span v-if="record.jie!=0">{{ toThousandFilter(record.jie) }}</span>
        </template>
        <template #dai="{ record }">
          <span v-if="record.dai!=0">{{toThousandFilter(record.dai) }}</span>
        </template>
        <template #flag="{ record }">
          <span v-if="record.id!=null && record.id!=''">
            <Tag :color="record.flag === '1' ? 'green' : 'volcano'">
              {{ record.flag === '1' ? '已对账' : '未对账' }}
            </Tag>
          </span>
        </template>
        <!--      <template #action="{ record }">

                <div>
                  <div v-if="showPopover">
                    <a-popover placement="bottom">
                      <a-button style="padding: 0px 4px; height: 20px;">
                        <CaretDownFilled />
                      </a-button>
                      <template #content>
                        <p style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>
                        <p style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>
                      </template>
                    </a-popover>
                  </div>
                </div>
              </template>-->

      </BasicTable>
          <div class="pagination-text" v-show="showPaginationText">
            共 {{paginationNumber}} 条记录&nbsp;&nbsp; 每页 200 条
          </div>
<!--        </div>-->
      </PageWrapper>
    </div>
  </div>
</template>

<script setup="props, {emit}" lang="ts">
import {BasicTree} from '/@/components/Tree'
import {PageWrapper} from '/@/components/Page'
import {
  // getBankStatementList,
  deleteBankStatement,
  saveBankStatement,
  // findByKemuAndDate,
  findCodeKemuByBr,
  findYearByAccount,
  excelBankStatement,
  deleteBankStatementList, findByKemuAndDate
} from '/@/api/record/system/bank-statement'

import {BasicTable, useTable} from '/@/components/Table'
import Edit from './popup/edit.vue'
import Excel from './popup/excel.vue'
import Query from './popup/query.vue'
import {
  Input as AInput,
  Select as ASelect,
  Popover as APopover,
  Tabs as ATabs,
  DatePicker as ADatePicker,
  Popconfirm as APopconfirm,
  Checkbox as ACheckbox,
  Radio as ARadio,
  Table as ATable,
  Drawer as ADrawer,
  message,
  Tag
} from "ant-design-vue";

const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
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
  EditOutlined,
  SortAscendingOutlined,
  SortDescendingOutlined,
  ProfileOutlined,
  CalendarOutlined
} from '@ant-design/icons-vue'
import {nextTick, onMounted, reactive, ref} from "vue";
import {CrudApi, initDynamics, assemblyDynamicColumn} from "./data";
import {aoaToSheetXlsx} from "./excel/components/importexcel"
// import {pushLog} from "/@/utils/logs/logs";
// import {pushTaskManager} from "/@/utils/taskmanager/taskmanager";
// import {pushCallbackManager} from "/@/utils/callback/callback";
const {
  createErrorModal
} = useMessage()

const {closeCurrent} = useTabs(router);

const formItems = ref({
  selectType: '1'
})

const kemu: any = ref('')
const year: any = ref('')
const showPopover = ref(true)
const flag = ref('0')
const dynamicTenantId = ref(getCurrentAccountName(true))

//树形控件
const showTree = ref(true)
const treeData: any = ref([])
const checkedKeys: any = ref([])
const expandedKeys: any = ref([])
const monthList: any = ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12']
const showYearList = ref([])
async function fetch() {
  treeData.value = []
  yearList.value.forEach(item=>{
    let deptTree = monthList.map(i => ({
      'value': item.accountYear+'-'+i,
      'title': `${i}月`,
    }))
    treeData.value.push(...[{
      value: item.accountYear,
      title: item.accountYear+'年',
      children: deptTree
    }])
  })
  /*treeData.value = [{
    value: year.value,
    title: year.value+'年',
    children: deptTree
  }]*/
  checkedKeys.value = [year.value]
  expandedKeys.value = [year.value]
}
function handleSelect(keys: string, e: any) {
  let keyStr = keys[0]+',';
  if (null != e.selectedNodes && e.selectedNodes.length > 0
    && null != e.selectedNodes[0].children &&  e.selectedNodes[0].children.length > 0){
    keyStr = getThisNodeStr(e.selectedNodes[0].children,keyStr)
  }
  console.log(keyStr)
}

function getThisNodeStr(list,str) {
  for (const nods of list) {
    str+=(nods.value+',')
    if (null != nods.children && nods.children.length > 0){
      str = getThisNodeStr(nods.children,str)
    }
  }
  return str
}
// 这是示例组件
const [registerTable, {
  reload,
  getDataSource,
  setTableData,
  setColumns,
  getColumns,
  getPaginationRef,
  setPagination,
  deleteSelectRowByKey
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
  /*pagination: {
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['10', '30', '50', '100'],
    showTotal: t => `总共${t}条数据`
  },*/
  /*actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  },*/
  searchInfo: {
    kemuCode: '',
    year: '',
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
  const res = await useRouteApi(findByKemuAndDate,{schemaName: dynamicTenantId})({
    kemuCode: kemu.value,
    year: year.value,
    flag: flag.value
  })
  tableDataAll.value = res.items
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

function formatSettModes(settModes){
  let str = settModes
  settModesList.value.forEach(item=>{
    if (item.settModesCode == settModes){
      str = item.settModesName
    }
  })
  return str
}

async function clickFlag(str) {
  flag.value = str
  await reloadTable()
  /*reload({
    searchInfo: {
      kemuCode: kemu.value,
      year: year.value,
      flag: flag.value
    }
  })*/
}

/*setInterval(async ()=>{

  showPopover.value=false
    await reload()
      showPopover.value=true
},2000)*/
const [registerEditPage, {openModal: openEditPage}] = useModal()
const [registerExcelPage, {openModal: openExcelPage}] = useModal()
const [registerQueryPage, {openModal: openQueryPage}] = useModal()
const val = {
  // id: '',
  statementDate: '',
  kemuCode: kemu.value,
  duifangUnit: '',
  settModes: '',
  piaohao: '',
  jie: '',
  dai: '',
  fangxiang: '',
  flag: '0',
  statementNum: '',
  remarks: '',
  currencyId: ''
}

const openAddPage = () => {
  val.kemuCode = kemu.value
  openEditPage(true, {
    data: val,
    dynamicTenantId: dynamicTenantId.value,
    defaultAdName: defaultAdName.value,
    year: year.value,
    isState:'0'
  })
}
const openExcel = () => {
  openExcelPage(true, {
    data: {
      kemuCode: kemu.value,
      year: year.value,
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value
    }
  })
}
const openEdit = (data: any) => {
  openEditPage(true, {
    data: data,
    dynamicTenantId: dynamicTenantId.value,
    defaultAdName: defaultAdName.value,
    year: year.value,
    isState:'1'
  })
}

const codeKemu: any = ref({})

async function reloadCurrentPage() {
  codeKemu.value = []
  const res = await useRouteApi(findCodeKemuByBr, {schemaName: dynamicTenantId})({})
  codeKemu.value = res.filter(item => {
    item.value = item.ccode + '-' + item.ccodeName
    return item.bend == '1'
  })
  /*const sysAccAuth = await findByUserIdAndAccIdAndYear({userId:useUserStore().getUserInfo['id'],accId:defaultAdName.value,year:year.value})
  if (sysAccAuth.length>0) {
    if (sysAccAuth[0].supervisor=='1' || sysAccAuth[0].ccodeAll=='1') {
      codeKemu.value = []
      codeKemu.value = res.filter(item => {
        item.value = item.ccode + '-' + item.ccodeName
        return item.bend == '1'
      })
    } else {
      const codeList = await findAllByCode({userId:useUserStore().getUserInfo['id'],accId:defaultAdName.value,year:year.value})
      if (codeList.length>0){
        codeKemu.value = []
        codeList.forEach(code=>{
          res.forEach(item=>{
            if (item.ccode==code && item.bend == '1'){
              item.value = item.ccode + '-' + item.ccodeName
              codeKemu.value.push(item)
            }
          })
        })
      }
    }
  }*/
  // console.log(codeKemu.value)
  /*if (codeKemu.value.length>0) {
    kemu.value = codeKemu.value[0].ccode
  }*/
}

const defaultAdName = ref(useCompanyOperateStoreWidthOut().getSchemaName)
const yearList: any = ref({})

async function reloadYear() {
  const res = await useRouteApi(findYearByAccount, {schemaName: dynamicTenantId})(defaultAdName.value)
  yearList.value = res
  // console.log(yearList.value)
  /*if (res.length > 0) {
    year.value = res[0].accountYear
  }*/
}

const defaultPage = ref(false)
onMounted(async () => {
  await reloadCurrentPage()
  await reloadYear()
  await reloadCurrency()
  // await checkDate()
  openQueryPage(true, {
    data: {
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value,
      year: year.value,
      kemu: kemu.value,
      currency: currency.value,
      flag: flag.value,
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

function refershTable(fun) {
  (async function () {
    showPopover.value = false
    await fun()
    setTimeout(() => {
      showPopover.value = true
    }, 1000)
    // nextTick(()=>{
    //   showPopover.value=true
    // })
  })()
}


const del = async (data: any) => {
  await useRouteApi(deleteBankStatement, {schemaName: dynamicTenantId})(data)
  createErrorModal({
    iconType: 'warning',
    title: '删除',
    content: '删除成功！'
  })
  // alert('删除成功！')
  refershTable(async () => {
    await reloadTable()
    /*await reload(
      {
        searchInfo: {
          kemuCode: kemu.value,
          year: year.value,
          flag: flag.value
        }
      }
    )*/
  })

}
// pushLog('进入了银行对账单页面')
// pushTaskManager('进入了银行对账单页面')
// pushCallbackManager('进入了银行对账单页面')
async function saveData(data: any) {
  await useRouteApi(saveBankStatement, {schemaName: dynamicTenantId})(data)
  // pushLog('银行对账单保存成功')
  refershTable(async () => {
    await reloadTable()
    /*await reload(
      {
        searchInfo: {
          kemuCode: kemu.value,
          year: year.value,
          flag: flag.value
        }
      }
    )*/
  })
}

//导入Excel
async function saveExcel(data: any) {
  await useRouteApi(excelBankStatement, {schemaName: dynamicTenantId})(data)
  refershTable(async () => {
    await reloadTable()
    /*await reload(
      {
        searchInfo: {
          kemuCode: kemu.value,
          year: year.value,
          flag: flag.value
        }
      }
    )*/
  })
}

//选中内容
const state = reactive<{
  selectedRowKeys: [];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const checkRow: any = ref([])
const onSelectChange = (selectedRowKeys,row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row.filter(item=>item.id!=null)
};

const rowSelection = {
  getCheckboxProps: (record) => ({
    disabled:  record.id==undefined
  }),
};
// 行勾选事件
const editAndDelBtnShow = ref(false);
function selectionChange(a) {
  editAndDelBtnShow.value=a.rows.filter(aa=>aa.flag=='1').length>0?true:false
}

function rowClick(a) {
  if(hasBlank(a.id)){
    deleteSelectRowByKey(a.key)
  }
}

const editOpen = () => {
  if (checkRow.value.length == 1) {
    openEditPage(true, {
      data: checkRow.value[0],
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value,
      year: year.value,
      isState:'1'
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '编辑',
      content: '请选择一条进行编辑！'
    })
  }
}

//批量删除
async function delList() {
  if (checkRow.value.length > 0) {
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        await useRouteApi(deleteBankStatementList, {schemaName: dynamicTenantId})(checkRow.value)
        checkRow.value = []
        state.selectedRowKeys = []
        message.success('删除成功！')
        refershTable(async () => {
          await reloadTable()
          /*await reload(
            {
              searchInfo: {
                kemuCode: kemu.value,
                year: year.value,
                flag: flag.value
              }
            }
          )*/
        })
      },
      onCancel: () => {
        return false
      }
    })
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '删除失败',
      content: '请至少选择一条进行删除！'
    })
  }
}

//导出Excel
const arrHeader = CrudApi.columns.map((column) => column.title);

function exportExcel() {
  const arrData = getDataSource().map((item) => getColumns().map((column: any) => item[column.dataIndex]));
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: arrData,
    header: arrHeader,
    filename: '银行对账列表_'+pageParameter.companyName+'.xlsx',
  });
}

// async function checkDate(data:any) {
//   checked.value.kemuCode = data.kemuCode
//   checked.value.date1 = data.date1
//   checked.value.date2 = data.date2
//   console.log(checked.value)
//   reload(
//     {
//       searchInfo: {
//         kemuCode: data.kemuCode,
//         date1: data.date1,
//         date2: data.date2
//       }
//     }
//   )
// }
async function checkDate() {
  await reloadTable()
  /*await reload(
    {
      searchInfo: {
        kemuCode: kemu.value,
        year: year.value,
        flag: flag.value
      }
    }
  )*/
}

function toThousandFilter(num: any) {
  if (num == '' || num == null) {
    return ''
  }
  return (+num || 0).toFixed(2).replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,')
}

function onSearch() {
}

//获取币种
const biZhong = ref('')
const bzList:any = ref([])
const settModesList:any = ref([])

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
  settModesList.value = []
  useRouteApi(findSettModesByFlag,{schemaName: dynamicTenantId})({}).then(res=>{
    settModesList.value = res.items
  })
}

//查询功能
const openQuery = () => {
  openQueryPage(true, {
    data: {
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value,
      year: year.value,
      kemu: kemu.value,
      currency: currency.value,
      flag: flag.value,
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
  flag.value = data.flag
  pageParameter.showRulesSize = data.showRulesSize
  if (currency.value != null && currency.value != '') {
    bzList.value.forEach(item => {
      if (currency.value == item.foreignCode) {
        biZhong.value = item.foreignName
      }
    })
  }
  await reloadCurrency()
  await reloadTable()
  /*await reload({
    searchInfo: {
      kemuCode: kemu.value,
      year: year.value,
      flag: flag.value
    }
  })*/
}

/**********************栏目设置**********************/
import {useMessage} from "/@/hooks/web/useMessage";
import {
  findDbLanMuList,
  saveLanMuList,
} from '/@/api/record/system/accvoucher';
import {
  getCurrentAccountName, getThisIndexImg, hasBlank
} from "/@/api/task-api/tast-bus-api";
import UnitChange from '../../../system/department/UnitChange.vue';
import {cloneDeep} from "lodash-es";
import {currentCyDatas, getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {findDataBase} from "/@/api/record/system/account";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {findAllByCode, findByUserIdAndAccIdAndYear} from "/@/api/sys-acc-auth/sys-acc-anth";
import {findSettModesByFlag} from "/@/api/record/system/sett-modes";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";

const {createConfirm, createWarningModal} = useMessage();
const pageParameter: any = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '北京希格科技',
  ifUnit: false,
})
const visible = ref(false);
const windowWidth = (document.documentElement.clientWidth - (70 + 280))
const windowHeight = (window.innerHeight - (300))
const totalColumnWidth = ref(0)
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData: any = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});
const tableRef = ref(null)
const lanMuData = {
  'accId': '',
  'menuName': '银行对账单',
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
  year.value = obj.year
  pageParameter.companyName = obj.baseName
  await reloadCurrentPage()
  await reloadYear()
  await reloadCurrency()
  await fetch()
  await reloadTable()
  let data: any = {}
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  data.kemuCode = kemu.value
  data.year = year.value
  data.flag = flag.value
  /*let res: any = await useRouteApi(CrudApi.list, {schemaName: obj.accountMode})(data)
  setTableData([]) // 清空可能残留的数据
  setTableData(res.items)*/
  // 底部分页信息
  pageParameter.thisAdInfo = {}
  pageParameter.thisAdInfo = obj
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
  padding: 5px 8px !important;
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

.tableUStyle {
  color: blue;
  cursor: pointer;
}

.tableUStyle:hover {
  color: red;
}

:deep(.table-striped) {
  background-color: honeydew;
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
  position: relative;
  :deep(.pagination-text){
    position: absolute;
    bottom: 246px;
    right: 200px;
    font-size: 13px;
    color: black;
    z-index: 99999999;
  }
}

.bg-white {
  width: 200px !important;
  min-height: 200px !important;
  height: calc(100% - 190px);
  border: 1px #cccccc solid;
  background: #f1f1f1 !important;
  margin-right: .2%;
}
:deep(.ant-tree-list){
  background-color: #f1f1f1 !important;
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

:deep(.vben-basic-table) .ant-pagination {
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
  margin: 0;
}

:deep(.vben-basic-table){
  height: calc(100% - 160px);
  margin-bottom: 20px;
}
</style>
