<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">-->
<!--        <div class="container-head-title" style="margin-left: 20px;">
          <b class="noneSpan"><UnorderedListOutlined />&nbsp;&nbsp;职务档案</b>
        </div>-->
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">职务档案</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openAddPage()"
          ><span>新建</span></button>
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
            @click="openExcel()"
          ><span>导入</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="exportExcelNow()"
          ><span>导出</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openPrint()"
          ><span>打印</span></button>
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
        <div style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">共 {{deptList.length}} 条记录</div>
        <div style="float: right; margin-left: 10px">
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
                          <b>设置表格字号</b>
                        </template>-->
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
          <!--          <a-popover placement="bottom">
                      <a-button>
                        <PicLeftOutlined :style="{ fontSize: '14px' }" />
                      </a-button>
                    </a-popover>-->
          <a-popover class="ant-btn-default" placement="bottom">
            <template #content>
            <span class="group-btn-span-special2" @click="onChangeSwitch('')"
                  :style="flag==''?{backgroundColor: '#0096c7',color: 'white'}:''">
                <ReadOutlined/>&nbsp;&emsp;全部&emsp;&ensp;<CheckOutlined
              v-if="flag==''"/></span><br/>
              <span class="group-btn-span-special2" @click="onChangeSwitch('1')"
                    :style="flag=='1'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SafetyOutlined/>&nbsp;&emsp;启用&emsp;&ensp;<CheckOutlined
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

        </div>
        <div style="float: right; position: relative">
          <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
            <a-select-option style="font-size: 12px;" value="ecCode">分类编码</a-select-option>
            <a-select-option style="font-size: 12px;" value="ecName">分类名称</a-select-option>
          </a-select>
          <a-input-search
            placeholder=""
            style="width: 200px; border-radius: 4px"
            v-model:value="formItems.selectValue"
            @search="onSearch"
          />
        </div>
      </div>
      <div style="clear:both"/>
    </div>
    <div class="app-container">
      <EditPage @save="saveData" @register="registerEditPage"/>
      <AddPage @save="saveData" @register="registerSavePage"/>
      <Excel @save="saveExcel" @register="registerExcelPage"/>
      <Print @save="loadPrint" @register="registerPrintPage"/>
      <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <div class="w-1/4 xl:w-1/5" style="width: 250px;min-height: 250px;border: 1px #cccccc solid;margin-right: .2%;margin-bottom: 58px;">
        <DeptTree v-if="isShowTree" class="w-1/4 xl:w-1/5" @select="handleSelect" v-model="pageParameter"/>
        </div>

        <div style="width: calc(100% - 250px); float: right;margin-left: 5px;">
          <BasicTable
                      ref="tableRef"
                      :loading="loadMark"
                      :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
                      :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
                      :scroll="{ x: totalColumnWidth,y: windowHeight }"
                      :dataSource="tableData"
                      @row-click="condClick"
                      @register="registerTable">
            <template #createDate="{ record }">
              <span v-if="record.createDate!='' && record.createDate!=null">{{ record.createDate.trim().split(' ')[0] }}</span>
            </template>
            <template #ecName="{ record }">
              <span v-if="record.ecCode.length === 5"> &#8195;&#8195;{{ record.ecName }}</span>
              <span v-else-if="record.ecCode.length === 7"> &#8195;&#8195;&#8195;&#8195;{{ record.ecName  }}</span>
              <span v-else>{{ record.ecName  }}</span>
            </template>
            <template #parentId="{ record }">{{ formatParentId(record.parentId) }}</template>
<!--            <template #uniqueCodeUser="{ record }">{{ formatUniqueCodeUser(record.uniqueCodeUser) }}</template>-->
            <template #flag="{ record }">
              <span v-if="record.flag!=null && record.flag!=''">
                <a-tag :color="record.flag === '1' ? 'green' : 'volcano'">
                  {{ record.flag === '1' ? '启用' : '停用' }}
                </a-tag>
              </span>
            </template>
            <template #action="{ record }">
              <div v-if="record.flag!=null && record.flag!=''">
                <a-popover placement="bottom">
                  <a-button style="padding: 0px 4px; height: 20px;">
                    <CaretDownFilled />
                  </a-button>
                  <template #content>
                    <!--              <p v-if="record.flag=='1'" style="cursor: pointer" @click="condClick(record)"><FormOutlined /> 编辑</p>-->
                    <p v-if="record.flag=='0'" class="p_specifics" style="cursor: pointer"
                       @click="editFlagData(record)">
                      <CheckCircleOutlined/>
                      启用
                    </p>
                    <p v-if="record.flag=='1'" class="p_specifics" style="cursor: pointer"
                       @click="editFlagData(record)">
                      <CloseCircleOutlined/>
                      停用
                    </p>
                  </template>
                </a-popover>
              </div>
            </template>
          </BasicTable>
          <div class="pagination-text" v-show="showPaginationText">
            共 {{paginationNumber}} 条记录&nbsp;&nbsp; 每页 200 条
          </div>
        </div>
      </PageWrapper>
    </div>
  </div>
</template>
<script setup lang="ts">
import {deleteDept, saveDept, editFlag, getDeptListById, GetDeptTree,excelVoucherType} from '/@/api/record/system/job-file'
import { BasicTable, useTable } from '/@/components/Table'
import EditPage from './popup/edit.vue'
import AddPage from './popup/save.vue'
import DeptTree from './DeptTree.vue'
import Print from './popup/print.vue'
import Excel from './popup/excel.vue'
import { useModal } from '/@/components/Modal'
import { PageWrapper } from '/@/components/Page'
import { Loading } from '/@/components/Loading';
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
  FilterFilled, SortAscendingOutlined, SortDescendingOutlined, CheckOutlined,
  ReadOutlined, SafetyOutlined, RestOutlined,UnorderedListOutlined,ProfileOutlined
} from '@ant-design/icons-vue'
import {
  Input as AInput,
  Select as ASelect,
  Popover as APopover,
  Tag as ATag,
  message
} from "ant-design-vue";
import {onMounted, reactive, ref} from "vue";

const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search

const {closeCurrent} = useTabs(router);

const {
  createConfirm
} = useMessage()

const flag = ref('1')

const formItems = ref({
  selectType: 'ecCode',
  selectValue: ''
})
const tableData:any = ref([]);
const tableDataAll:any = ref([]);

const showPaginationText = ref(false)
const paginationNumber = ref(0)

const compState = reactive({
  absolute: false,
  loading: false,
  tip: '加载中...',
});
function openCompFullLoading() {
  openLoading(false);
}
function openLoading(absolute: boolean) {
  compState.absolute = absolute;
  compState.loading = true;
}
const columns: any = [
  {
    title: 'ID',
    dataIndex: 'id',
    defaultHidden: true,
    ellipsis: true
  },
  {
    title: '职务编码',
    dataIndex: 'ecCode',
    ellipsis: true,
    width: '10%',
    customCell: () => {		// 在此处可以修改单元格中的样式
      return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
    },
  },
  {
    title: '职务名称',
    dataIndex: 'ecName',
    ellipsis: true,
    align: 'left',
    width: '20%',
    customCell: () => {		// 在此处可以修改单元格中的样式
      return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
    },
  },
  {
    title: '职务类别',
    dataIndex: 'pname',
    ellipsis: true,
    align: 'left',
    width: '20%',
    customCell: () => {		// 在此处可以修改单元格中的样式
      return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
    },
  },
  {
    title: '工作概要',
    dataIndex: 'workInfo',
    width: '20%',
    ellipsis: true,
    slots: {customRender: 'workInfo'}
  },
  {
    title: '职务级别',
    dataIndex: 'psnLevel',
    width: '10%',
    ellipsis: true,
    slots: {customRender: 'psnLevel'}
  },
  {
    title: '管理级别',
    dataIndex: 'mangeLevel',
    width: '20%',
    ellipsis: true,
    slots: {customRender: 'mangeLevel'}
  },
  {
    title: '设立日期',
    dataIndex: 'estDate',
    width: '10%',
    ellipsis: true,
    slots: {customRender: 'estDate'}
  },
  {
    title: '状态',
    dataIndex: 'flag',
    width: '10%',
    ellipsis: true,
    slots: { customRender: 'flag' }
  }
]
function formatFlag(flag:any) {
  let str = '启用'
  switch (flag) {
    case '1':
      str = '启用'
      break
    case '0':
      str = '停用'
      break
  }
  return str
}

//部门
const deptList:any = ref([])
async function reloadDept() {
  loadMark.value = true
  showPaginationText.value = false
  let len = 0
  const res:any = await useRouteApi(getDeptListById,{schemaName: dynamicTenantId})({})
  deptList.value = res.items
  if(deptList.value){
    tableDataAll.value = deptList.value.filter(item=> {
      if (thisCheckKey.value!=null && thisCheckKey.value!='0' && thisCheckKey.value!='undefined') {
        return thisCheckKey.value.indexOf(item.typeId)!=-1 && item.flag.indexOf(flag.value)!=-1
      }
      return item.flag.indexOf(flag.value)!=-1
    })
    tableData.value = tableDataAll.value
    let num = 50-(tableData.value.length%50)
    for (let i=0;i<num;i++){
      tableData.value.push({})
    }
    await setPagination({
      total: tableData.value.length
    })
  }
  len = deptList.value.filter(item=>item.id!=null && item.id!='').length
  paginationNumber.value = len
  showPaginationText.value = true
  loadMark.value = false
}
function formatParentId(parentId){
  let str = ''
  deptList.value.forEach(
    function (dept:any) {
      if (dept.id == parentId){
        str = dept.deptName
      }
    }
  )
  return str
}


const thisCheckKey = ref('')

function handleSelect(key) {
  if (null != key) {
    thisCheckKey.value = key
    reloadTable()
  }
}

// 这是示例组件
const dynamicTenantId = ref(getCurrentAccountName(true))
const [registerTable, {
  reload,
  getColumns,
  getPaginationRef,
  setPagination,
  getDataSource,
  setTableData
}] = useTable({
  /*api: async (params) => {
    return useRouteApi(GetDeptTree, {schemaName: dynamicTenantId})(params)
  },*/
  columns: columns,
  bordered: true,
  showIndexColumn: true,
  pagination: {
    pageSize: 200,
    // showSizeChanger: false,
    simple:true,
    showTotal: t => `共${t}条记录    每页200条`
  },
  /*pagination: {
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['10', '25', '50', '100'],
    showTotal: t => `总共${t}条数据`
  },*/
  actionColumn: {
    width: '10%',
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  },
  searchInfo: {
    id: thisCheckKey.value,
    flag: flag.value
  }
})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const [registerSavePage, { openModal: openSavePage }] = useModal()
const val = {
  id: null,
  parentId: '',
  deptCode: '',
  deptName: '',
  uniqueCodeUser: '',
  createDate: '',
  flag: ''
}
const openAddPage = () => {
  openSavePage(true, {
    data: val,
    dynamicTenantId: dynamicTenantId.value
  })
}
const condClick = (data:any) => {
  /*openEditPage(true, {
    data: data,
    dynamicTenantId: dynamicTenantId.value
  })*/
}

const del = async(data:any) => {
  createConfirm({
    iconType: 'error',
    title: '警告',
    content: '删除后数据将不能恢复，你确认要删除吗?',
    onOk: async() => {
      isShowTree.value = false
      await useRouteApi(deleteDept,{schemaName: dynamicTenantId})(data)
      isShowTree.value = true
      // alert('删除成功！')
      message.success('删除成功！')
      await reloadTable()
    },
    onCancel: () => {
      return false
    }
  })
  // isShowTree.value = false
  // await useRouteApi(deleteDept,{schemaName: dynamicTenantId})(data)
  // isShowTree.value = true
  // // alert('删除成功！')
  // message.success('删除成功！')
  // reloadTable()
}

async function saveData(data:any) {
  isShowTree.value = false
  await useRouteApi(saveDept, {schemaName: dynamicTenantId})(data)
  isShowTree.value = true
  await reloadTable()
  checkRow.value = []
  state.selectedRowKeys = []
}
async function editFlagData(data:any) {
  isShowTree.value = false
  await useRouteApi(editFlag,{schemaName: dynamicTenantId})(data)
  isShowTree.value = true
  await reloadTable()
}
async function onSearch(){
  loadMark.value = true
  tableData.value = tableDataAll.value.filter(item => {
    //通过部门编码过滤
    if (formItems.value.selectType == 'ecCode' && formItems.value.selectValue != '') {
      return item.ecCode.indexOf(formItems.value.selectValue) != -1
    }
    //通过部门名称过滤
    if (formItems.value.selectType == 'ecName' && formItems.value.selectValue != '') {
      return item.ecName.indexOf(formItems.value.selectValue) != -1
    }
    return item
  })
  let num = 50-(tableData.value.length%50)
  for (let i=0;i<num;i++){
    tableData.value.push({})
  }
  await setPagination({
    total: tableData.value.length
  })
  loadMark.value = false
}

const isShowTree = ref(true)

async function reloadTable() {
  await reloadDept()
  /*reload({
    searchInfo: {
      id: thisCheckKey.value,
      flag: flag.value
    }
  })*/
  checkRow.value = []
  state.selectedRowKeys = []
}

function onChangeSwitch(str) {
  flag.value = str
  reloadTable()
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
const onSelectChange = (selectedRowKeys, row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row.filter(item=>item.id!=null)
};

const editOpen = () => {
  if (checkRow.value.length == 1) {
    openEditPage(true, {
      data: checkRow.value[0],
      dynamicTenantId: dynamicTenantId.value
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
    const psnList = await useRouteApi(psnFindAll, {schemaName: dynamicTenantId})({})
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      for (let a = 0; a < psnList.items.length; a++) {
        const psn = psnList.items[a]
        if (psn.uniqueCode == '9999') {
          createWarningModal({
            iconType: 'warning',
            title: '删除',
            content: '编码为“9999”的部门为系统预制部门不允许删除！'
          })
          return false
        }
        if (psn.uniqueCodeDept == item.uniqueCode) {
          createWarningModal({
            iconType: 'warning',
            title: '删除',
            content: '<span style="font-weight: bold;color: red;">[' + item.deptName + ']</span>部门中经已存在人员，不能进行删除操作！'
          })
          return false
        }
      }
    }
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        isShowTree.value = false
        for (let i = 0; i < checkRow.value.length; i++) {
          const item = checkRow.value[i]
          await useRouteApi(deleteDept, {schemaName: dynamicTenantId})(item)
        }
        isShowTree.value = true
        checkRow.value = []
        state.selectedRowKeys = []
        message.success('删除成功！')
        await reloadTable()
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

import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {getCurrentAccountName, getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useMessage} from "/@/hooks/web/useMessage";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {findDataBase} from "/@/api/record/system/account";
import {psnFindAll} from "/@/api/psn/psn";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {aoaToSheetXlsx} from "/@/components/Excel";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {useNewPrint} from "/@/utils/boozsoft/print/print";
import {tableStyle} from "/@/store/modules/abc-print";
const defaultPage = ref(true)
const pageParameter:any = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '湖北万亚软件技术有限公司',
  ifUnit: false,
  total: 0,
  thisAdInfo: {}
})
function init() {
  getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(res=>{
    if (null != res /*&& res.independent == 0*/) {
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
const windowWidth = (document.documentElement.clientWidth - (70 + 280))
const windowHeight = (window.innerHeight - (300))
const tableRef: any = ref(null)
const totalColumnWidth = ref(0)

function initTableWidth() {
  let total = windowWidth
  if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
  // totalColumnWidth.value = total
  tableRef.value.$el.style.setProperty('width', (total + 70) + 'px')
}
const accNameAll =  ref(null)
const loadMark = ref(false)
const {createWarningModal} = useMessage();
const dynamicAdReload = async (obj) => {
  // const dataBase: any = await findDataBase(obj.accId, obj.year)
  dynamicTenantId.value = obj.accountMode
  accNameAll.value = obj.target.accName
  await reloadDept()
  let data: any = {}
  data.id = thisCheckKey.value
  data.flag = flag.value
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  // let res = await useRouteApi(GetDeptTree, {schemaName: dynamicTenantId})(data)
  let res = tableData.value
  // if (res != null && res.length > 0){
  loadMark.value = true
  setTableData([]) // 清空可能残留的数据
  setTableData(res)
    // 底部分页信息
    pageParameter.thisAdInfo = obj
    pageParameter.total = res.length
    setPagination({total:res.length})
  /*}else {
    createWarningModal({title: '温馨提示',content: '暂无任何数据！'});
    pageParameter.thisAdInfo = {}
    pageParameter.total = -1
  }*/
  loadMark.value = false
}

const exportExcelNow = async () => {
  const data = JSON.parse(JSON.stringify(getDataSource()))
  // debugger
  const arrHeader = ['职务编码','职务名称','职务类别','工作概要','职务级别','管理级别','设立日期','状态'];
  const columnList = ['ecCode','ecName','pname','workInfo','psnLevel','mangeLevel','estDate','flag']
  data.forEach(v=>{
    v.flag = formatFlag(v.flag )
  })
  const arrData = data.map((item) => columnList.map(column=>item[column]));
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: arrData,
    header: arrHeader,
    filename: '职务档案.xlsx',
  });
}
//导入Excel
const [registerExcelPage, { openModal: openExcelPage }] = useModal()
const openExcel = () => {
  openExcelPage(true, {
    data: {
      dynamicTenantId: dynamicTenantId.value
    }
  })
}
const mychild =  ref(null)
async function saveExcel(data:any) {
  await useRouteApi(excelVoucherType, {schemaName: dynamicTenantId})(data)
  await reloadDept()
}
const [registerPrintPage, {openModal: openPrintPage}] = useModal()
const openPrint = () => {
  openPrintPage(true, {
    data: {
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: useCompanyOperateStoreWidthOut().getSchemaName,
      year: '2021',
    }
  })
}

const userName = useUserStoreWidthOut().getUserInfo.username
const loadPrint = (obj) => {
  openCompFullLoading()
  const data = JSON.parse(JSON.stringify(getDataSource()))
  let printList: any = []
  let printUser = ''
  if(obj.printUser){
    printUser = '制表人：'+ userName
  }

  let title = '职务档案'
  //格式化数据
  data.forEach((item,index) => {
    let item1 = {}
    item1[0] = item.ecCode
    item1[1] = item.ecName
    item1[2] = item.pname
    item1[3] = item.workInfo
    item1[4] = item.psnLevel
    item1[5] = item.mangeLevel
    item1[6] = item.estDate
    item1[7] = formatFlag(item.flag)
    printList.push(item1)

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
      head: [['','', '', title,'', '', '',''],
        ['核算单位:'+accNameAll.value, '', '', '','', '', '',''],
        ['职务编码','职务名称','职务类别','工作概要','职务级别','管理级别','设立日期','状态']],
      body: printList,
      // startY: 60,
      styles: tableStyle(),
      margin: {
        left: 60,
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
        /*doc.autoTableText(
          '核算单位：' ,
          tabMarginLeft,
          data.cursor.y + 3,
          0
        );*/
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
        //data.cell.styles.fontStyle = 'bold'

        if (data.section == 'head' && data.row.index == 0) {
          data.cell.styles.fontSize = 20
          data.cell.styles.fontStyle = 'bold'
          data.cell.styles.lineColor = [255, 255, 255]
          if (data.column.index == 3) {
            data.cell.colSpan = 2
            data.cell.styles.halign = 'left'
          }
        }
        if (data.section == 'head' && data.row.index == 1) {
          data.cell.styles.fontSize = 10
          data.cell.styles.fontStyle = 'bold'
          data.cell.styles.lineColor = [255, 255, 255]
          data.cell.colSpan = 2
          data.cell.styles.halign = 'left'
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
            data.cell.styles.fillColor = [240,240,240]
          }

        }
      },
      columnStyles: {
        0: {maxHeight: 10,cellWidth: 50, halign: 'left'},
        1: {cellWidth: 90, halign: 'left'},
        2: {cellWidth: 90, halign: 'left'},
        3: {cellWidth: 90, halign: 'left'},
        4: {cellWidth: 60, halign: 'center'},
        5: {cellWidth: 60, halign: 'center'},
        6: {cellWidth: 50, halign: 'center'},
        7: {cellWidth: 50, halign: 'center'},
      }
    })
    compState.loading = false
  })

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
  font-size: 14px !important;
  padding: 2px 8px !important;
  border-color: #aaaaaa !important;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 13px !important;
  padding: 2px 8px !important;
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
  :deep(.pagination-text){
    position: absolute;
    bottom: 240px;
    right: 200px;
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
