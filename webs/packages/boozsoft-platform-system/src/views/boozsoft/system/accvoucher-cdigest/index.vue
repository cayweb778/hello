<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">常用摘要设置</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="openAddPage()"
          ><span>新增</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="editOpen()"
          ><span>修改</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="editOpenBatch()"
          ><span>批量修改</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="delList()"
          ><span>删除</span></button>
          <button
            type="button"
            v-if="!defaultPage"
            class="ant-btn ant-btn-me"
            @click="openExcel()"
          ><span>导入</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
          ><span>导出</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="closeCurrent()"
          ><span>退出</span></button>
        </div>
      </div>
      <div style="clear: none"/>
      <div style="margin-top: -30px;margin-left: 80px;">
        <div style="display: inline-block;float: left;margin-top: -25px;">
          <AccountPicker theme="three" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="float: right; margin-left: 10px;">
          <a-button @click="reloadTable()">
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
                          <b>设置表格字号</b></template>-->
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
        </div>
        <div style="float: right; position: relative">
          <!-- 搜索 -->
          <!--          <label style="font-size: 14px">检索条件：</label>-->
          <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;"
                    class="special_select">
            <a-select-option style="font-size: 12px;" value="1">编码</a-select-option>
            <a-select-option style="font-size: 12px;" value="2">常用摘要内容</a-select-option>
          </a-select>
          <a-input-search
            placeholder=""
            style="width: 200px; border-radius: 4px"
            @search="onSearch"
          />
        </div>
      </div>
      <div style="clear:both"/>
    </div>
    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <div class="w-1/4 xl:w-1/5" style="width: 250px;min-height: 250px;border: 1px #cccccc solid;margin-right: .2%;margin-bottom: 58px;">
          <ClassTree @select="handleSelect" v-model="pageParameter"/>
        </div>
        <div style="width: calc(100% - 250px); float: right;margin-left: 5px;">
        <BasicTable
          ref="tableRef"
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
          :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys,getCheckboxProps:rowSelection.getCheckboxProps, onChange: onSelectChange }"
          @selection-change="selectionChange"
          @row-click="rowClick"
          :scroll="{ x: totalColumnWidth,y: windowHeight }"
          @register="registerTable"
          @fetch-success="fetchSuccess"
          :loading="loadMark"
        >
          <template #ccode="{ record }">
            <a class="tableUStyle" @click="openEdit(record,false)">{{ record.ccode }}</a>
          </template>
          <template #content="{ record }">
            <a class="tableUStyle" @click="openEdit(record,false)">{{ record.content }}</a>
          </template>
          <template #classCode="{ record }"> {{ formatClassCode(record.classCode) }}</template>
        </BasicTable>
          <div class="pagination-text" :style="{top: (windowHeight+40)+'px'}" v-show="showPaginationText">
            {{`共 ${tableData.length}条记录&emsp;每页 200 条`}}
          </div>
        </div>
        <Edit
          @save="saveData"
          @register="registerEditPage"
        />
        <BatchEdit
          @save="saveBatchData"
          @register="registerBatchEditPage"
        />
        <Excel @save="saveExcel" @register="registerExcelPage"/>
      </PageWrapper>
    </div>
  </div>
</template>
<script setup lang="ts">
import {BasicTable, useTable} from '/@/components/Table'
import {PageWrapper} from '/@/components/Page'
import {useRouteApi} from '/@/utils/boozsoft/datasource/datasourceUtil';
import ClassTree from './ClassTree.vue'
import Edit from './popup/edit.vue';
import BatchEdit from './popup/batch-edit.vue';
import Excel from './popup/excel.vue'
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
  FilterFilled, CheckOutlined, SortDescendingOutlined, SortAscendingOutlined, EditOutlined,
  UnorderedListOutlined,ProfileOutlined
} from '@ant-design/icons-vue'
import {onMounted, reactive, ref} from 'vue'
import {
  findAllApi,
  findByIdApi,
  saveApi,
  deleteByIdApi,
  saveListApi
} from '/@/api/boozsoft/account/AccvoucherCdigest'
import {findAllApi as findClassAllApi} from '/@/api/boozsoft/account/AccvoucherCdigestClass'
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
  getCurrentAccountName, getThisIndexImg, hasBlank,
} from "/@/api/task-api/tast-bus-api";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  getThisAdInfoData,
} from "/@/api/record/system/financial-settings";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";

const formItems = ref({
  selectType: '1'
})

const {closeCurrent} = useTabs(router);

const {
  createConfirm,
  createWarningModal
} = useMessage()

const CrudApi = {
  list: [],
  columns: [
    {
      title: '编码',
      dataIndex: 'ccode',
      ellipsis: true,
      width: 100,
      slots: {customRender: 'ccode'}
    },
    {
      title: '常用摘要内容',
      dataIndex: 'content',
      ellipsis: true,
      customCell: () => {		// 在此处可以修改单元格中的样式
        return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
      },
      width: 400,
      align: 'left',
      slots: {customRender: 'content'}
    },
    {
      title: '所属分类',
      dataIndex: 'classCode',
      ellipsis: true,
      width: 200,
      align: 'left',
      slots: {customRender: 'classCode'}
    }
  ]
}

function formatClassCode(classCode: String) {
  let str = classCode
  classList.value.forEach(
    function (item: any) {
      if (item.classCode == classCode) {
        str = item.className
      }
    }
  )
  return str
}

const classList = ref([])

async function reloadClass() {
  const res: any = await useRouteApi(findClassAllApi, {schemaName: dynamicTenantId})()
  classList.value = res
}

const thisCheckKey = ref('')

function handleSelect(data) {
  if (null != data.classCode) {
    thisCheckKey.value = data.classCode
    // if (data.deptList.length > 0) classList.value = data.classList
    reloadTable()
  }
}

// 这是示例组件
const dynamicTenantId = ref(getCurrentAccountName(true))
const [registerTable, {
  reload,
  setTableData,
  getDataSource,
  setColumns,
  getColumns,
  getPaginationRef,
  setPagination,
  deleteSelectRowByKey
}] = useTable({
  api: async (params) => {
    // debugger
    // console.log(thisCheckKey.value)
    return await useRouteApi(findAllApi, {schemaName: dynamicTenantId})(params)
  },
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: true,
  pagination: {
    pageSize: 200,
    simple: true,
  },
  /*actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  }*/
})

// 部门带参数,给人员，人员翻页，带部门

const [registerEditPage, {openModal: openEditPage}] = useModal()
const [registerBatchEditPage, {openModal: openBatchEditPage}] = useModal()
const [registerExcelPage, {openModal: openExcelPage}] = useModal()
const val: any = {
  // id: '',
  ccode: '',
  content: '',
  classCode: ''
}
const defaultPage = ref(true)
onMounted(async () => {
  await reloadClass()
  // 进页面执行
  getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(res => {
    if (null != res) {
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn == '' ? res.accName : res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
  })
})

const openAddPage = () => {
  val.isEdit = true
  val.classCode = thisCheckKey.value=='0'?'':thisCheckKey.value
  openEditPage(true, {
    data: val,
    dynamicTenantId: dynamicTenantId.value,
    isState: '0'
  })
}
const openEdit = (data: any, flag) => {
  data.isEdit = flag
  openEditPage(true, {
    data: data,
    dynamicTenantId: dynamicTenantId.value,
    isState: '2'
  })
}

function condClick(data) {
}

const del = async (data: any) => {
  createConfirm({
    iconType: 'error',
    title: '警告',
    content: '删除后数据将不能恢复，你确认要删除吗?',
    onOk: async () => {
      await useRouteApi(deleteByIdApi, {schemaName: dynamicTenantId})(data)
      // alert('删除成功！')
      message.success('删除成功！')
      reloadTable()
      checkRow.value = []
    },
    onCancel: () => {
      return false
    }
  })

}

async function saveData(data: any) {
  await useRouteApi(saveApi, {schemaName: dynamicTenantId})(data)
  reloadTable()
}

function onSearch() {
}

const tableData = ref([])
function reloadTable() {
  reload({
    searchInfo: {
      classCode: thisCheckKey.value
    }
  })
  tableData.value = getDataSource().filter(item=>item.id!=null && item.id!='')
  checkRow.value = []
  state.selectedRowKeys = []
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
    checkRow.value[0].isEdit = true
    openEditPage(true, {
      data: checkRow.value[0],
      dynamicTenantId: dynamicTenantId.value,
      isState: '1'
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '编辑',
      content: '请选择一条进行编辑！'
    })
  }
}

//批量修改数据
const editOpenBatch = () => {
  if (checkRow.value.length > 0) {
    checkRow.value[0].isEdit = true
    openBatchEditPage(true, {
      data: {
        classCode: ''
      },
      dynamicTenantId: dynamicTenantId.value
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '编辑',
      content: '请选择需要编辑的内容！'
    })
  }
}

async function saveBatchData(data) {
  checkRow.value.forEach(item => {
    if (data.classCode != null && data.classCode != '') {
      item.classCode = data.classCode
    }
    return item
  })
  await useRouteApi(saveListApi, {schemaName: dynamicTenantId})(checkRow.value)
  message.success('批量修改成功!')
  reloadTable()
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
          await useRouteApi(deleteByIdApi, {schemaName: dynamicTenantId})(item)
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

//文件导入
const openExcel = () => {
  openExcelPage(true, {
    dynamicTenantId: dynamicTenantId.value
  })
}

async function saveExcel(data) {
  await useRouteApi(saveListApi, {schemaName: dynamicTenantId})(data)
  message.success('导入成功!')
  reloadTable()
}

const pageParameter = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '湖北万亚软件技术有限公司',
  ifUnit: false,
  total: 0,
  thisAdInfo: {}
})

function init() {
  getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(res => {
    if (null != res) {
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
    initTableWidth()
  })
}

init()
const visible = ref(false);
const windowWidth = (window.innerWidth)
const windowHeight = (window.innerHeight - (310))
const tableRef: any = ref(null)
const totalColumnWidth = ref(0)

function initTableWidth() {
  let total = 800
  if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
  totalColumnWidth.value = total
  tableRef.value.$el.style.setProperty('width', (total + 70) + 'px')
}

const loadMark = ref(false)
const dynamicAdReload = async (obj) => {
  // const dataBase: any = await findDataBase(obj.accId, obj.year)
  dynamicTenantId.value = obj.accountMode
  await reloadClass()
  // 先获取部门组件查看是否存在部门信息
  let data: any = {}
  //data.uniqueCodeDept = thisCheckKey.value
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  let res = await useRouteApi(findAllApi, {schemaName: dynamicTenantId})(data)
  // if (res != null && res.total> 0){
  loadMark.value = true
  setTableData([]) // 清空可能残留的数据
  setTableData(res.items)
  // 底部分页信息
  // dynamicTenantId.value = obj.accId+'-'+obj.year
  pageParameter.thisAdInfo = {}
  pageParameter.thisAdInfo = obj
  pageParameter.total = res.total
  setPagination({total: res.total})
  /*}else {
    createWarningModal({title: '温馨提示',content: '暂无任何数据！'});
    pageParameter.thisAdInfo = {}
    pageParameter.total = -1
  }*/
  loadMark.value = false
}

const showPaginationText:any = ref(false)
function fetchSuccess(data) {
  if (data.items.length < 50) {
    showPaginationText.value=true
    for (let i = data.items.length; i < 50; i++) {
      data.items.push({})
    }
  }
  setTableData(data.items)
}
</script>
<style src="./global-menu-index.less" lang="less" scoped></style>
<style lang="less" scoped>
:deep(.vben-basic-table) {
  .ant-table-wrapper {
    .ant-table-measure-row{
      td{
        padding: 0!important;
      }
    }
  }
}

.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
  padding: 5px 8px !important;
  border-color: #aaaaaa !important;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 4px 8px !important;
  border-color: #aaaaaa !important;
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
  .pagination-text {
    position: absolute;
    bottom: 6px;
    right: 51%;
    font-size: 13px;
    color: black;
    z-index: 99999999;
  }
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

:deep(.vben-basic-table) .ant-pagination {
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
  margin: 0;
}

:deep(.vben-basic-table){
  min-height: 500px;
  height: calc(100% - 160px);
  //margin-bottom: 20px;
}

:deep(.ant-input),:deep(.ant-select),:deep(.ant-btn){
  border: 1px solid #c9c9c9;
}

</style>
