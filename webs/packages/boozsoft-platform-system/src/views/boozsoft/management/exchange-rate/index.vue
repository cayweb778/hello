<template>
  <div>
    <div class="app-container">
      <div class="app-container-head"><img :src="getThisIndexImg()" class="container-head-img" draggable="false" >
        <div class="container-head-title">
          <b class="noneSpan">外币汇率设置</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openAddPage()"
          ><span>新增</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
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
          ><span>退出</span></button>
        </div>
      </div>
      <div class="app-container-neck">
        <div style="float: right; margin-left: 10px">
          <a-button>
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <a-popover class="ant-btn-default" placement="bottom">
            <template #content>
            <span class="group-btn-span-special2" @click="onChangeSwitch('')"
                  :style="flag==''?{backgroundColor: '#0096c7',color: 'white'}:''">
                <ReadOutlined/>&nbsp;&emsp;全部&emsp;&ensp;<CheckOutlined
              v-if="flag==''"/></span><br/>
              <span class="group-btn-span-special2" @click="onChangeSwitch('1')"
                    :style="flag=='1'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SafetyOutlined/>&nbsp;&emsp;正常&emsp;&ensp;<CheckOutlined
                v-if="flag=='1'"/></span><br/>
              <span class="group-btn-span-special2" @click="onChangeSwitch('0')"
                    :style="flag=='0'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <RestOutlined/>&nbsp;&emsp;停用&emsp;&ensp;<CheckOutlined
                v-if="flag=='0'"/></span>
            </template>
            <a-button>
              <PicLeftOutlined :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
          <a-button @click="()=>{
            if (!visible){ visible = true;reloadColumns()}
            return false
          }">
            <FilterFilled :style="{ fontSize: '14px' }"/>
          </a-button>
        </div>
        <div style="float: right; position: relative">
          <a-input-search
            placeholder=""
            style="width: 200px; border-radius: 4px"
            @search="onSearch"
          />
        </div>
      </div>
    </div>
    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <TypeTree class="w-1/4 xl:w-1/5" @select="handleSelect" v-model="pageParameter"/>
        <div style="width: calc(100% - 250px); float: right;margin-left: 5px;">
            内容
        </div>
        <Edit
          @save="saveData"
          @register="registerEditPage"
        />
        <BatchEdit
          @save="saveBatchData"
          @register="registerBatchEditPage"
        />
      </PageWrapper>
    </div>
  </div>
</template>
<script setup lang="ts">
import { useTable} from '/@/components/Table'
import {PageWrapper} from '/@/components/Page'
import {useRouteApi} from '/@/utils/boozsoft/datasource/datasourceUtil';
import TypeTree from './TypeTree.vue'
import Edit from './popup/edit.vue';
import BatchEdit from './popup/batch-edit.vue';
import {useModal} from '/@/components/Modal'
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
  PieChartFilled, ReadOutlined, SafetyOutlined, RestOutlined,
  FilterFilled, CheckOutlined, SortDescendingOutlined, SortAscendingOutlined, EditOutlined
} from '@ant-design/icons-vue'
import {onMounted, reactive, ref} from 'vue'
import {psnFindAll, deletePsn, savePsn, editFlag, excelPsn} from '/@/api/record/system/group-psn'
import {
  Input as AInput,
  Select as ASelect,
  Popover as APopover,
  Tabs as ATabs,
  DatePicker as ADatePicker,
  Tag as ATag,
  Popconfirm as APopconfirm,
  Checkbox as ACheckbox,
  Radio as ARadio,
  Table as ATable,
  Drawer as ADrawer,
  message
} from "ant-design-vue";

const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ATabPane = ATabs.TabPane
const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
import {
  getCurrentAccountName, getThisIndexImg,
} from "/@/api/task-api/tast-bus-api";
import {
  findDbLanMuList,
  saveLanMuList,
} from '/@/api/record/system/accvoucher'
import {cloneDeep} from "lodash-es";
import {initDynamics, assemblyDynamicColumn} from "./data";
import {getDeptListById} from "/@/api/record/system/dept";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  getThisAdInfoData,
} from "/@/api/record/system/financial-settings";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {columnProps} from "ant-design-vue/es/table/interface";

const formItems = ref({
  selectType: '1'
})

const {
  createConfirm,
  createWarningModal
} = useMessage()

const flag = ref('1')

const CrudApi = {
  list: [],
  columns: [
    {
      title: '状态',
      dataIndex: 'flag',
      width: 100,
      slots: {customRender: 'flag'}
    },
    {
      title: '人员工号',
      dataIndex: 'jobNum',
      width: 120,
      ellipsis: true
    },
    {
      title: '编码',
      dataIndex: 'psnCode',
      width: 120,
      ellipsis: true,
      fixed: 'left',
      slots: {customRender: 'psnCode'}
    },
    {
      title: '姓名',
      dataIndex: 'psnName',
      width: 120,
      ellipsis: true,
      fixed: 'left',
      slots: {customRender: 'psnName'}
    }, {
      title: '性别',
      dataIndex: 'psnSex',
      width: 100,
      ellipsis: true,
      slots: {customRender: 'psnSex'}
    },
    /*{
      title: '所属部门',
      dataIndex: 'uniqueCodeDept',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true,
      width: 200,
      slots: { customRender: 'uniqueCodeDept' }
    }, */
    {
      title: '职务',
      dataIndex: 'psnPost',
      width: 100,
      ellipsis: true,
    },
    {
      title: '人员属性',
      dataIndex: 'psnType',
      ellipsis: true,
      width: 120,
      slots: {customRender: 'psnType'}
    },
    {
      title: '手机号',
      dataIndex: 'cellPhoneNum',
      width: 120,
      ellipsis: true
    }, {
      title: '邮箱',
      dataIndex: 'psnEmail',
      width: 120,
      ellipsis: true
    },
    {
      title: '通讯地址',
      dataIndex: 'psnAddress',
      width: 200,
      ellipsis: true
    },
    {
      title: '证件类型',
      dataIndex: 'documentType',
      width: 150,
      ellipsis: true
    }, {
      title: '证件号码',
      dataIndex: 'documentCode',
      width: 200,
      ellipsis: true
    },
    {
      title: '开户银行',
      dataIndex: 'psnBank',
      width: 200,
      ellipsis: true
    }, {
      title: '开户地',
      dataIndex: 'bankArea',
      width: 200,
      ellipsis: true
    }, {
      title: '银行账户',
      dataIndex: 'bankAccount',
      width: 200,
      ellipsis: true
    }, {
      title: '银行行号',
      dataIndex: 'bankNum',
      width: 200,
      ellipsis: true
    }, {
      title: '入职日期',
      dataIndex: 'entryDate',
      width: 120,
      ellipsis: true
    }, {
      title: '离职日期',
      dataIndex: 'leaveDate',
      width: 120,
      ellipsis: true
    }
  ],
  editData: {
    id: '',
    uniqueCode: '',
    psnCode: '',
    uniqueCodeDept: '',
    psnName: '',
    psnSex: '',
    psnType: '',
    psnPost: '',
    psnPhone: '',
    entryDate: ''
  }
}

function formatPsnSex(sex: String) {
  let str = '男'
  switch (sex) {
    case '0':
      str = '未知的性别'
      break
    case '1':
      str = '男'
      break
    case '2':
      str = '女'
      break
    case '9':
      str = '未说明的性别'
  }
  return str
}

function formatPsnType(type: any) {
  let str = '男'
  switch (type) {
    case '1':
      str = '内部员工'
      break
    case '2':
      str = '外部员工'
      break
  }
  return str
}

/*const deptList = ref([])
async function reloadDept() {
  const res:any = await useRouteApi(getDeptListById,{schemaName: dynamicTenantId})()
  deptList.value = res.items
}
function formatUniqueCodeDept(uniqueCodeDept:any) {
  let str = uniqueCodeDept
  deptList.value.forEach(
    function (dept:any) {
      if (dept.uniqueCode == uniqueCodeDept){
        str = dept.deptName
      }
    }
  )
  return str
}*/
const thisCheckKey = ref('')

function handleSelect(data) {
  if (null != data.uniqueCode) {
    thisCheckKey.value = data.uniqueCode
    // if ( data.deptList.length > 0) deptList.value = data.deptList
    reloadTable()
  }
}

// 这是示例组件
const [registerTable, {
  reload,
  setTableData,
  setColumns,
  getColumns,
  getPaginationRef,
  setPagination
}] = useTable({
  api: async (params) => {
    // debugger
    // console.log(thisCheckKey.value)
    return await psnFindAll(params)
  },
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  pagination: {
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['10', '30', '50', '100'],
    showTotal: t => `总共${t}条数据`
  },
  actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  },
  searchInfo: {
    uniquePsnType: thisCheckKey.value,
    flag: flag.value
  }
})

// 部门带参数,给人员，人员翻页，带部门

const [registerEditPage, {openModal: openEditPage}] = useModal()
const [registerBatchEditPage, {openModal: openBatchEditPage}] = useModal()
const val: any = {
  id: null,
  uniqueCode: '',
  psnName: '',
  psnSex: '1',
  psnType: '1',
  psnCode: '',
  uniqueCodeDept: '',
  jobNum: '',
  psnPost: '',
  uniquePsnType: '',
  cellPhoneNum: '',
  countryId: '',
  psnEmail: '',
  psnAddress: '',
  province: '',
  city: '',
  district: '',
  psnQq: '',
  psnWechat: '',
  documentType: '',
  documentCode: '',
  psnStation: '',
  entryDate: '',
  psnBank: '',
  bankArea: '',
  bankAccount: '',
  bankNum: ''
}

onMounted(async () => {
  resetDynamicColumnData()
})

const openAddPage = () => {
  val.isEdit = true
  openEditPage(true, {
    data: val
  })
}
const openEdit = (data: any, flag) => {
  data.isEdit = flag
  openEditPage(true, {
    data: data
  })
}
const del = async (data: any) => {
  await deletePsn(data)
  // alert('删除成功！')
  message.success('删除成功！')
  reloadTable()
}

async function saveData(data: any) {
  await savePsn(data)
  reloadTable()
}

async function editFlagData(data: any) {
  await editFlag(data)
  reloadTable()
}

function onSearch() {
}

function filterSearch() {
}

function condClick() {
}

/*const thisCheckKey = ref('')
function handleSelect(data){
  if (null != data.uniqueCode){
    thisCheckKey.value = data.uniqueCode
    if ( data.deptList.length > 0) deptList.value = data.deptList
    reloadTable()
  }
}*/
function reloadTable() {
  reload({
    searchInfo: {
      uniquePsnType: thisCheckKey.value,
      flag: flag.value
    }
  })
  checkRow.value = []
  state.selectedRowKeys = []
}

function onChangeSwitch(str) {
  flag.value = str
  reloadTable()
}

//选中内容
type Key = columnProps['id'];

const state = reactive<{
  selectedRowKeys: Key[];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const checkRow: any = ref([])
const onSelectChange = (selectedRowKeys, row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
};
const editOpen = () => {
  if (checkRow.value.length == 1) {
    checkRow.value[0].isEdit = true
    openEditPage(true, {
      data: checkRow.value[0]
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '编辑',
      content: '请选择一条进行编辑！'
    })
  }
}

async function delList() {
  if (checkRow.value.length > 0) {
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        for (let i = 0; i < checkRow.value.length; i++) {
          const item = checkRow.value[i]
          await deletePsn(item)
        }
        checkRow.value = []
        message.success('删除成功！')
        reloadTable()
      },
      onCancel: () => {
        return false
      }
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '删除',
      content: '请选择需要删除的内容！'
    })
  }
}



async function saveExcel(data) {
  await excelPsn(data)
}

async function saveBatchData(data) {
  checkRow.value.forEach(item => {
    if (data.psnType != null && data.psnType != '') {
      item.psnType = data.psnType
    }
    if (data.psnSex != null && data.psnSex != '') {
      item.psnSex = data.psnSex
    }
    if (data.uniquePsnType != null && data.uniquePsnType != '') {
      item.uniquePsnType = data.uniquePsnType
    }
    if (data.province != null && data.province != '') {
      item.province = data.province
    }
    if (data.psnAddress != null && data.psnAddress != '') {
      item.psnAddress = data.psnAddress
    }
    if (data.countryId != null && data.countryId != '') {
      item.countryId = data.countryId
    }
    if (data.documentType != null && data.documentType != '') {
      item.documentType = data.documentType
    }
    if (data.entryDate != null && data.entryDate != '') {
      item.entryDate = data.entryDate
    }
    if (data.leaveDate != null && data.leaveDate != '') {
      item.leaveDate = data.leaveDate
    }
    if (data.psnBank != null && data.psnBank != '') {
      item.psnBank = data.psnBank
    }
    if (data.bankArea != null && data.bankArea != '') {
      item.bankArea = data.bankArea
    }
    if (data.bankNum != null && data.bankNum != '') {
      item.bankNum = data.bankNum
    }
    return item
  })
  await excelPsn(checkRow.value)
  message.success('批量修改成功!')
  reloadTable()
}

/**********************栏目设置**********************/
/*start栏目设置*/
const pageParameter = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '湖北万亚软件技术有限公司',
  ifUnit: false,
  total: 0,
  thisAdInfo: {}
})
const visible = ref(false);
const windowWidth = (document.documentElement.clientWidth - (70 + 280))
const windowHeight = (window.innerHeight - (350))
const totalColumnWidth = ref(0)
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData: any = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});
const tableRef: any = ref(null)
const lanMuData = {
  'accId': '',
  'menuName': '集团人员',
  'type': '列表',
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
  let a: any = []
  a = getColumns()
  let last = a.pop()
  let newA = JSON.parse(JSON.stringify(CrudApi.columns))
  newA = assemblyDynamicColumn(dynamicColumnData.value, newA)
  newA.push(last)
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

const pageReload = () => {
  setTableData([]) // 清空可能残留的数据
  reload({
    searchInfo: pageParameter
  })
}
/*栏目设置end*/
</script>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
<style lang="less" scoped>
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
  padding: 5px 8px !important;
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
