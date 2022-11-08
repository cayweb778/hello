<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">辅助核算定义</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openAdd()"
          ><span>新增</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="editOpen()"
          ><span>修改</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="delList()"
          ><span>删除</span></button>
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
      <div style="margin-top: -30px;margin-left: 80px;">
        <div style="display: inline-block;float: left;margin-top: -25px;">
          <AccountPicker theme="three" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="float: right; margin-left: 10px;">
          <a-button class="ant-btn-me" @click="reload()">
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
          <!--        <a-popover placement="bottom">
                    <a-button>
                      <SettingFilled :style="{ fontSize: '14px' }" />
                    </a-button>
                  </a-popover>-->
<!--        <a-button>
          <PicLeftOutlined :style="{ fontSize: '14px' }" />
        </a-button>-->
<!--        <a-button>
          <EditFilled :style="{ fontSize: '14px' }" />
        </a-button>-->
          <!--        <a-popover placement="bottom">
                  </a-popover>

                  <a-button>
                    <EditFilled :style="{ fontSize: '14px' }" />
                  </a-button>

                  <a-button @click="activeKey=!activeKey">
                    <PieChartFilled :style="{ fontSize: '14px' }" />
                  </a-button>
                  <a-button>
                    <FilterFilled :style="{ fontSize: '14px' }" />
                  </a-button>-->
        </div>
        <div style="float: right; position: relative">
          <!--        <label style="font-size: 14px">检索条件：</label>-->
          <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
            <a-select-option style="font-size: 12px;" value="1">编码</a-select-option>
            <a-select-option style="font-size: 12px;" value="2">名称</a-select-option>
            <a-select-option style="font-size: 12px;" value="3">数据来源</a-select-option>
            <a-select-option style="font-size: 12px;" value="4">对应凭证字段</a-select-option>
            <a-select-option style="font-size: 12px;" value="5">说明</a-select-option>
            <a-select-option style="font-size: 12px;" value="6">状态</a-select-option>
          </a-select>
          <!-- 搜索 -->
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
      <Edit
        @save="saveData"
        @register="registerEditPage"
      />
      <Excel
        @save="saveExcel"
        @register="registerExcelPage"
      />
      <BasicTable
        :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys,getCheckboxProps:rowSelection.getCheckboxProps, onChange: onSelectChange }"
        @selection-change="selectionChange"
        @row-click="rowClick"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        :scroll="{ y: windowHeight }"
        @register="registerTable"
        @fetch-success="fetchSuccess"
      >
        <template #flag="{ record }">
          <span v-if="record.flag!=='11'">
            <a-tag :color="record.flag === '1' ? 'green' : 'volcano'" >
              {{ record.flag === '1' ? '启用' : '停用' }}
            </a-tag>
          </span>
        </template>
        <template #bend="{ record }">
          <span v-if="record.flag!=='11'">
            {{ record.bend == '1' ? '末级档案' : '标准档案' }}
          </span>
        </template>
        <template #ctype="{ record }">
          <span v-if="record.flag!=='11'">
            {{ record.ctype == '1' ? '系统档案' : '自定义档案' }}
          </span>
        </template>
        <template #action="{ record }">
          <div v-if="record.flag!=='11'">
            <a-popover placement="bottom">
              <a-button style="padding: 0px 4px; height: 20px;">
                <CaretDownFilled />
              </a-button>
              <template #content>
                <p v-if="record.flag=='0'" class="p_specifics" @click="editFlagData(record)"
                   style="cursor: pointer">
                  <CheckCircleOutlined/>
                  启用
                </p>
                <p v-if="record.flag=='1'" class="p_specifics" @click="editFlagData(record)"
                   style="cursor: pointer">
                  <CloseCircleOutlined/>
                  停用
                </p>
              </template>
            </a-popover>
          </div>
        </template>
      </BasicTable>
      <div class="pagination-text" :style="{top: (windowHeight+35)+'px'}" v-show="showPaginationText">
        {{`共 ${showPageNum}条记录&emsp;每页 200 条`}}
      </div>
    </div>
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import { BasicTable, useTable } from '/@/components/Table'
import Edit from './popup/edit.vue'
import Excel from './popup/excel.vue'
import { useModal } from '/@/components/Modal'
import {onMounted, reactive, ref} from 'vue'
import Icon from '/@/components/Icon/index'
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
  UnorderedListOutlined,ProfileOutlined
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
  Tag as ATag,
  message
} from 'ant-design-vue'
import {
  deleteFuzhuHesuan, editByFlag,
  findFuzhuHesuanList,
  saveFuzhuHesuan,
  excelFuzhuHesuan, findAllFuzhuHesuanList
} from "/@/api/record/system/fuzhu-hesuan";
import {useMessage} from "/@/hooks/web/useMessage";
//import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
const aoaToSheetXlsx=null
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {getCurrentAccountName, getThisIndexImg, hasBlank} from "/@/api/task-api/tast-bus-api";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";

const AListItem = AList.Item
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option

const {
  createSuccessModal,
  createErrorModal,
  createWarningModal,
  createConfirm
} = useMessage()

const {closeCurrent} = useTabs(router);

const formItems = ref({
  selectType: '1'
})
const windowHeight = (window.innerHeight - (300))
const dynamicTenantId = ref(getCurrentAccountName(true))
const defaultAdName = ref(useCompanyOperateStoreWidthOut().getSchemaName)
const CrudApi = {
  list: useRouteApi(findFuzhuHesuanList, {schemaName: dynamicTenantId}),
  columns: [
    {
      title: '编码',
      dataIndex: 'ccode',
      width: 100,
      ellipsis: true
    },
    {
      title: '名称',
      dataIndex: 'cname',
      width: 200,
      align: 'left',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true
    },
    {
      title: '数据来源',
      dataIndex: 'cankaoDuixiang',
      width: 200,
      align: 'left',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true
    },
    {
      title: '对应凭证字段',
      dataIndex: 'cfield',
      width: 150,
      align: 'left',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true,
      // slots: { customRender: 'cdfine' }
    },
    {
      title: '取值方式',
      dataIndex: 'bend',
      width: 150,
      ellipsis: true,
      slots: { customRender: 'bend' }
    },
    {
      title: '类型',
      dataIndex: 'ctype',
      width: 150,
      ellipsis: true,
      slots: { customRender: 'ctype' }
    },
    {
      title: '说明',
      dataIndex: 'remarks',
      align: 'left',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true
    },
    /*{
      title: '参照对象表名',
      dataIndex: 'cankaoDuixiangTable',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true
    },*/
    {
      title: '状态',
      dataIndex: 'flag',
      ellipsis: true,
      width: 100,
      slots: { customRender: 'flag' }
    },
  ],
  editData: {
    ccode: '',
    cname: '',
    cankaoDuixiang: '',
    cdfine: '',
    flag: '',
    remarks: '',
    cankaoDuixiangTable: ''
  }
}
// 这是示例组件
const [registerTable, {
  reload,
  getDataSource,
  setTableData,
  setPagination,
  getPaginationRef,
  deleteSelectRowByKey
}] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: true,
  pagination: {
    pageSize: 200,
    simple: true,
  },
  actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  }
})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const [registerExcelPage, { openModal: openExcelPage }] = useModal()

const showPaginationText:any = ref(false)
const showPageNum:any = ref(0)
function fetchSuccess(data) {
  showPageNum.value=data.items.length
  if (data.items.length < 50) {
    showPaginationText.value=true
    for (let i = data.items.length; i < 50; i++) {
      data.items.push({flag:'11'})
    }
  }
  setTableData(data.items)
}

const val = {
  ccode: '',
  cname: '',
  cankaoDuixiang: '',
  cdfine: '',
  flag: '',
  remarks: '',
  cankaoDuixiangTable: ''
}
const openAdd = () => {
  if (fuzhuHesuanList.value.length<40) {
    openEditPage(true, {
      data: val,
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value,
      isState: '0'
    })
  } else {
    // message.error('辅助核算定义最多不能超过三十个！');
    createErrorModal({
      iconType: 'success',
      title: '新增',
      content: '辅助核算定义最多不能超过四十个！！'
    })
  }
}
const openEdit = (data:any) => {
  openEditPage(true, {
    data: data,
    dynamicTenantId: dynamicTenantId.value,
    defaultAdName: defaultAdName.value,
    isState: '1'
  })
}

const openExcel = () => {
  if (fuzhuHesuanList.value.length<30) {
    openExcelPage(true, {
      data: {},
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value
    })
  } else {
    // message.error('辅助核算定义最多不能超过三十个！');
    createErrorModal({
      iconType: 'success',
      title: '导入',
      content: '辅助核算定义最多不能超过三十个！！'
    })
  }
}
//导入Excel
async function saveExcel(data:any) {
  await useRouteApi(excelFuzhuHesuan, {schemaName: dynamicTenantId})(data)
  await reloadTable()
}

const tableData = ref([])
async function reloadTable() {
  await reload()
  tableData.value = getDataSource()
  checkRow.value = []
  state.selectedRowKeys = []
}

//文件导出
async function exportExcel() {
  // debugger
  const arrHeader = ['编码','名称','数据来源','对应凭证字段','取值方式','类型','说明'];
  const columnList = ['ccode','cname','cankaoDuixiang','cfield','bend','ctype','remarks']
  const arrData = getDataSource().map(item=>{
    item.bend = item.bend=='1'?'末级档案':'标准档案'
    item.ctype = item.ctype=='1'?'系统档案':'自定义档案'
    return item
  }).map((item) => columnList.map(column=>item[column]));
  // console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: arrData,
    header: arrHeader,
    filename: '辅助核算定义.xlsx',
  });
}

const del = async(data:any) => {
  await useRouteApi(deleteFuzhuHesuan, {schemaName: dynamicTenantId})(data)
  // alert('删除成功！')
  createSuccessModal({
    iconType: 'success',
    title: '删除',
    content: '删除成功！'
  })
  await reloadTable()
}

async function saveData(data: any) {
  await useRouteApi(saveFuzhuHesuan, {schemaName: dynamicTenantId})(data)
  await reloadTable()
}

async function editFlagData(data: any) {
  await useRouteApi(editByFlag, {schemaName: dynamicTenantId})(data);
  await reloadTable()
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
      isState: '1'
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '修改',
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
          await useRouteApi(deleteFuzhuHesuan, {schemaName: dynamicTenantId})(item)
        }
        message.success('删除成功！')
        checkRow.value = []
        state.selectedRowKeys = []
        await reloadTable()
      },
      onCancel: () => {
        return false
      }
    })
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '删除',
      content: '请选择需要删除的内容！'
    })
    return false
  }
}

function onSearch() {
}

const fuzhuHesuanList = ref([])

async function reloadFuzhuHesuan() {
  fuzhuHesuanList.value = await useRouteApi(findAllFuzhuHesuanList, {schemaName: dynamicTenantId})({})
}

const pageParameter: any = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '北京希格科技',
  ifUnit: false,
})
const defaultPage = ref(false)
onMounted(async() => {
  await reloadFuzhuHesuan()
  // 进页面执行
  getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(res => {
    if (null != res && res.independent == 0) {
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
  })
})

const dynamicAdReload = async (obj) => {
  // const dataBase:any = await findDataBase(obj.accId,obj.year)
  dynamicTenantId.value = obj.accountMode
  defaultAdName.value = obj.accId
  let data: any = {}
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  let res: any = await useRouteApi(CrudApi.list, {schemaName: obj.accountMode})(data)
  tableData.value = res.items
  setTableData([]) // 清空可能残留的数据
  setTableData(res.items)
  // 底部分页信息
  pageParameter.thisAdInfo = {}
  pageParameter.thisAdInfo = obj
  pageParameter.total = res.total
  setPagination({total: res.total})
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
  .pagination-text {
    position: absolute;
    bottom: 6px;
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
