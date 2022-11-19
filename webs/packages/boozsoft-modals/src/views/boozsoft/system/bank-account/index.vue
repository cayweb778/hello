<template>
  <div>
    <div class="app-container">

      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">-->
<!--        <div class="container-head-title" style="margin-left: 20px">
          <b class="noneSpan"><UnorderedListOutlined style="font-size: 26px;" />&nbsp;&nbsp;账户档案表</b>
        </div>-->

        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">账户档案表</b>
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
            @click="openExcel()"
          ><span>导入</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="exportExcel()"
          ><span>导出</span></button>
<!--          <button
            type="button"
            class="ant-btn ant-btn-me"
          ><span>打印</span></button>-->
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
        <div style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">共 {{tableDataAll.length}} 条记录</div>

<!--      <div class="app-container-neck">
        <div style="display: inline-block;float: left;margin-left: 1%;font-size: 14px;">
          <AccountPicker theme="three" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="float: left; margin-left: 10px;line-height: 30px;">共 {{tableDataAll.length}} 条记录</div>-->
        <div style="float: right; margin-left: 10px">
          <a-button class="ant-btn-me" @click="reloadCurrentPage()">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <a-popover class="ant-btn-default" placement="bottom">
            <template #content>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                    :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                    :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined v-if="pageParameter.showRulesSize==='MIN'"/></span>
            </template>
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
          <a-popover class="ant-btn-default" placement="bottom">
            <template #content>
            <span class="group-btn-span-special2" @click="onChangeSwitch('')"
                  :style="flag==''?{backgroundColor: '#0096c7',color: 'white'}:''">
                <ReadOutlined/>&nbsp;&emsp;全部&emsp;&ensp;<CheckOutlined v-if="flag==''"/></span><br/>
              <span class="group-btn-span-special2" @click="onChangeSwitch('1')"
                    :style="flag=='1'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SafetyOutlined/>&nbsp;&emsp;启用&emsp;&ensp;<CheckOutlined v-if="flag=='1'"/></span><br/>
              <span class="group-btn-span-special2" @click="onChangeSwitch('0')"
                    :style="flag=='0'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <RestOutlined/>&nbsp;&emsp;停用&emsp;&ensp;<CheckOutlined
                v-if="flag=='0'"/></span>
            </template>
            <a-button>
              <PicLeftOutlined :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
<!--        <a-popover placement="bottom">
          <a-button>
            <PicLeftOutlined :style="{ fontSize: '14px' }" />
          </a-button>
        </a-popover>-->

          <!--        <a-button>
                    <EditFilled :style="{ fontSize: '14px' }" />
                  </a-button>-->

<!--          <a-button @click="activeKey=!activeKey">
            <PieChartFilled :style="{ fontSize: '14px' }" />
          </a-button>-->
<!--        <a-button>
          <FilterFilled :style="{ fontSize: '14px' }" />
        </a-button>-->
        </div>
        <div style="float: right; position: relative">
          <!-- 搜索 -->
          <!--        <a-input-search
                    placeholder=""
                    style="width: 200px; border-radius: 4px"
                    @search="onSearch"
                  />-->
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
        v-if="activeKey"
        :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys,getCheckboxProps:rowSelection.getCheckboxProps, onChange: onSelectChange }"
        @selection-change="selectionChange"
        @row-click="rowClick"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        :scroll="{ y: windowHeight }"
        @register="registerTable"
        :dataSource="tableData"
        :loading="loadMark"
      >
        <template #accStyle="{ record }">
<!--          XJ现金、YH银行、ZFB支付宝、WX微信、QT其他-->
          <span v-if="record.accStyle=='XJ'">现金</span>
          <span v-if="record.accStyle=='YH'">银行</span>
          <span v-if="record.accStyle=='ZFB'">支付宝</span>
          <span v-if="record.accStyle=='WX'">微信</span>
          <span v-if="record.accStyle=='QT'">其他</span>
        </template>
        <template #status="{ record }">
            <span v-if="record.status!=null && record.status!=''">
              <a-tag :color="record.status === '1' ? 'green' : 'volcano'">
                {{ record.status === '1' ? '启用' : '停用' }}
              </a-tag>
            </span>
        </template>
        <template #action="{ record }">

          <div v-if="record.status!=null && record.status!=''">
            <a-popover placement="bottom">
              <a-button style="padding: 0px 4px; height: 20px;">
                <CaretDownFilled/>
              </a-button>
              <template #content>
                <!--              <p v-if="record.status=='1'" style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>-->
                <p v-if="record.status=='0'" class="p_specifics" style="cursor: pointer"
                   @click="editFlagData(record)">
                  <CheckCircleOutlined/>
                  启用
                </p>
                <p v-if="record.status=='1'" class="p_specifics" style="cursor: pointer"
                   @click="editFlagData(record)">
                  <CloseCircleOutlined/>
                  停用
                </p>
                <!--              <p style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>-->
              </template>
            </a-popover>
          </div>
        </template>

      </BasicTable>
      <div class="pagination-text" v-show="showPaginationText">
        共 {{paginationNumber}} 条记录&nbsp;&nbsp; 每页 200 条
      </div>
      <div
        v-if="!activeKey"
        :class="cardList"
      >
        <div :class="`${cardList}__content`">
          <!--        <div>ID:{{ item.id }}</div>-->
          <a-list style="height: 700px;overflow-y: scroll;overflow-x: hidden;">
            <a-row :gutter="16">
              <template
                v-for="(item) in cardList.filter(aa => aa.id!=null)"
              >
                <a-col :span="6">
                  <a-list-item style="width: 95%">
                    <a-card
                      :hoverable="true"
                      :class="`${cardList}__card`"
                      style="width: 100%"
                    >
                      <div style="width: 100%;float: right;">
                        <div :class="`${cardList}__card-title`">
                          <Icon
                            v-if="item.icon"
                            class="icon"
                            :icon="item.icon"
                            :color="item.color"
                          />
                          <!--                        {{ index+1 }}-->
                        </div>
                        <div style="float: right; border: 1px solid rgb(1, 143, 251);border-radius: 10px;padding: 0 10px; margin-top: -5px;">
                          <span v-if="item.accStyle=='XJ'">现金</span>
                          <span v-if="item.accStyle=='YH'">银行</span>
                          <span v-if="item.accStyle=='ZFB'">支付宝</span>
                          <span v-if="item.accStyle=='WX'">微信</span>
                          <span v-if="item.accStyle=='QT'">其他</span>
                        </div>
                        <div style="font-size: 24px;margin-top: 20px;">
                          {{ item.cname }}
                        </div>
                        <div style="float: right;margin-top: -20px;">
                          <a-popover placement="bottom" v-if="item.sysFlag!='1'">
                            <a-button style="padding: 0px 4px; height: 20px;margin-right: 10px;">
                              <EllipsisOutlined />
                            </a-button>
                            <template #content>
                              <p style="cursor: pointer" class="p_specifics" @click="openEdit(item)"><FormOutlined /> 修改</p>
                              <p style="cursor: pointer" class="p_specifics" @click="del(item)"><DeleteOutlined /> 删除</p>
                              <p v-if="item.flag=='0'" class="p_specifics" style="cursor: pointer" @click="editFlagData(item)">
                                <CheckCircleOutlined/>
                                启用
                              </p>
                              <p v-if="item.flag=='1'" class="p_specifics" style="cursor: pointer" @click="editFlagData(item)">
                                <CloseCircleOutlined/>
                                停用
                              </p>
                            </template>
                          </a-popover>
                        </div>
                      </div>
                    </a-card>
                  </a-list-item>
                </a-col>
              </template>
            </a-row>
          </a-list>
        </div>

      </div>
    </div>
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import { BasicTable, useTable } from '/@/components/Table'
import Edit from './popup/edit.vue'
import Excel from './popup/excel.vue'
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
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
  CheckOutlined,
  SortDescendingOutlined,
  SortAscendingOutlined,
  EllipsisOutlined,
  UnorderedListOutlined,
  ReadOutlined,
  SafetyOutlined,
  RestOutlined,ProfileOutlined
} from '@ant-design/icons-vue'
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Upload as AUpload,
  List as AList,
  Row as ARow,
  Menu as AMenu,
  Dropdown as ADropdown,
  Card as ACard,
  Col as ACol,
  Divider as ADivider,
  message,
  Tag as ATag
} from "ant-design-vue"
import {getCurrentAccountName, getThisIndexImg, hasBlank} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useMessage} from "/@/hooks/web/useMessage";
import {aoaToSheetXlsx} from "/@/components/Excel";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {
  deleteSysBankAccount,
  getSysBankAccountList,
  saveSysBankAccount,
  editFlag, excelSysBankAccount
} from "/@/api/record/system/sys-bank-account";

const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const AListItem = AList.Item
const AMenuItem = AMenu.Item

const {closeCurrent} = useTabs(router);

const {
  createConfirm,
  createWarningModal
} = useMessage()

const flag = ref('1')

const windowHeight = (window.innerHeight - (300))

const CrudApi = {
  // list: findSettModesAll,
  columns: [
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      ellipsis: true,
      slots: {customRender: 'status'}
    },
    {
      title: '账户名称',
      dataIndex: 'cname',
      align: 'left',
      // width: 200,
      ellipsis: true
    },
    {
      title: '账户类型',
      dataIndex: 'accStyle',
      align: 'left',
      width: 100,
      ellipsis: true,
      slots: {customRender: 'accStyle'}
    },
    {
      title: '开户银行',
      dataIndex: 'accBank',
      align: 'left',
      width: 200,
      ellipsis: true,
    },
    {
      title: '银行账号',
      dataIndex: 'accNumber',
      align: 'left',
      width: 200,
      ellipsis: true,
    },
    {
      title: '备注',
      dataIndex: 'memo',
      align: 'left',
      // width: 200,
      ellipsis: true,
    }
  ],
  editData: {
    id: null,
    ccode: '',
    cname: '',
    accStyle: '',
    accBank: '',
    accNumber: '',
    status: '',
    memo: '',
  }
}

const dynamicTenantId = ref(getCurrentAccountName(true))
// 这是示例组件
const [registerTable, { reload,getDataSource,setTableData,setColumns,getColumns,getPaginationRef,setPagination,deleteSelectRowByKey }] = useTable({
  // api: useRouteApi(CrudApi.list,{schemaName: dynamicTenantId}),
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
  actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  }
})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const val = {
  id: null,
  ccode: '',
  cname: '',
  accStyle: '',
  accBank: '',
  accNumber: '',
  status: '1',
  memo: '',
}

const openAddPage = () => {
  openEditPage(true, {
    data: val,
    dynamicTenantId: dynamicTenantId.value,
    isState: '0'
  })
}
const openEdit = (data:any) => {
  openEditPage(true, {
    data: data,
    dynamicTenantId: dynamicTenantId.value,
    isState: '1'
  })
}
const del = async(data:any) => {
  createConfirm({
    iconType: 'error',
    title: '警告',
    content: '删除后数据将不能恢复，你确认要删除吗?',
    onOk: async () => {
      await useRouteApi(deleteSysBankAccount, {schemaName: dynamicTenantId})(data)
      await reloadCurrentPage()
      message.success('删除成功！')
      // alert('删除成功！')
      // await reload()
      checkRow.value = []
      state.selectedRowKeys = []
    },
    onCancel: () => {
      return false
    }
  })
}
const activeKey = ref(true)

const showPaginationText = ref(false)
const paginationNumber = ref(0)

const cardList:any = ref([])
const tableData:any = ref([])
const tableDataAll:any = ref([])
async function reloadCurrentPage() {
  showPaginationText.value = false
  let len = 0
  const res = await useRouteApi(getSysBankAccountList,{schemaName: dynamicTenantId})({})
  tableDataAll.value = res
  if (flag.value=='1'){
    cardList.value = tableDataAll.value.filter(item => item.status=='1')
  } else if(flag.value=='0'){
    cardList.value = tableDataAll.value.filter(item => item.status=='0')
  } else {
    cardList.value = tableDataAll.value
  }
  tableData.value = cardList.value
  let num = 25-(cardList.value.length%25)
  for (let i=0;i<num;i++){
    tableData.value.push({})
  }
  setPagination({total:cardList.value.length})
  len = tableData.value.filter(item=>item.id!=null && item.id!='').length
  paginationNumber.value = len
  showPaginationText.value = true
  // console.log(cardList.value)
}

function onChangeSwitch(str){
  flag.value = str
  if (flag.value=='1'){
    cardList.value = tableDataAll.value.filter(item => item.status=='1')
  } else if(flag.value=='0'){
    cardList.value = tableDataAll.value.filter(item => item.status=='0')
  } else {
    cardList.value = tableDataAll.value
  }
  tableData.value = cardList.value
  let num = 25-(cardList.value.length%25)
  for (let i=0;i<num;i++){
    tableData.value.push({})
  }
}

onMounted(async() => {
  await reloadCurrentPage()
})
async function saveData(data:any) {
  await useRouteApi(saveSysBankAccount, {schemaName: dynamicTenantId})(data)
  await reloadCurrentPage()
  // await reload()
  checkRow.value = []
  state.selectedRowKeys = []
}

async function editFlagData(data: any) {
  await useRouteApi(editFlag, {schemaName: dynamicTenantId})(data)
  await reloadCurrentPage()
  // await reload()
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
  editAndDelBtnShow.value=a.rows.filter(aa=>aa.bcheck=='1').length>0?true:false
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

async function delList() {
  if (checkRow.value.length > 0) {
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        for (let i = 0; i < checkRow.value.length; i++) {
          const item = checkRow.value[i]
          await useRouteApi(deleteSysBankAccount, {schemaName: dynamicTenantId})(item)
        }
        checkRow.value = []
        state.selectedRowKeys = []
        message.success('删除成功！')
        await reload()
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

function onSearch() {
}

const pageParameter: any = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '北京希格科技',
  ifUnit: false,
})

//导入Excel
const [registerExcelPage, { openModal: openExcelPage }] = useModal()
const openExcel = () => {
  openExcelPage(true, {
    data: {},
    dynamicTenantId: dynamicTenantId.value
  })
}

async function saveExcel(data:any) {
  await useRouteApi(excelSysBankAccount, {schemaName: dynamicTenantId})(data)
  await reloadCurrentPage()
  if (activeKey.value == true) {
    await reload()
  }
}

//文件导出
async function exportExcel() {
  // debugger
  const arrHeader = ['账户名称','账户类型','开户银行','银行账号','备注'];
  const columnList = ['cname','accStyle','accBank','accNumber','memo']
  const arrData = cardList.value.map(item => {
    //XJ现金、YH银行、ZFB支付宝、WX微信、QT其他
    if (item.accStyle=='XJ') {
      item.value = '现金'
    } else if (item.accStyle=='YH') {
      item.value = '银行'
    } else if (item.accStyle=='ZFB') {
      item.value = '支付宝'
    } else if (item.accStyle=='WX') {
      item.value = '微信'
    } else if (item.accStyle=='QT') {
      item.value = '其他'
    }
    return item
  }).map((item) => columnList.map(column=>item[column]));
  // console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: arrData,
    header: arrHeader,
    filename: '账户档案_'+pageParameter.companyName+'.xlsx',
  });
}

const loadMark = ref(false)
const dynamicAdReload = async (obj) => {
  // const dataBase:any = await findDataBase(obj.accId,obj.year)
  dynamicTenantId.value = obj.accountMode
  pageParameter.companyName = obj.baseName
  await reloadCurrentPage()
  if (activeKey.value == true) {
    loadMark.value = true
    checkRow.value = []
    state.selectedRowKeys = []
    setTableData([]) // 清空可能残留的数据
    setTableData(cardList.value)
    // 底部分页信息
    pageParameter.thisAdInfo = {}
    pageParameter.thisAdInfo = obj
    pageParameter.total = cardList.length
    setPagination({total: cardList.length})
  }
  loadMark.value = false
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
