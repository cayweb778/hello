<template>
  <div>
    <div class="app-container">

      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">-->
<!--        <div class="container-head-title">
          <b class="noneSpan">资产负债表</b>
        </div>-->
        <div class="container-head-title" style="float: left;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 40%;text-align: center;margin-top: 20px;">
          <b class="noneSpan" style="font-size: 26px;">资产负债表</b>
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

        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openAdd()"
          ><span>生成报表</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="openBatchQueryAdd()"
          ><span>批量生成</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="editOpen()"
          ><span>查看</span></button>
          <a-popover placement="bottom">
            <a-button class="ant-btn ant-btn-me"><span>审核</span></a-button>
            <template #content>
              <span class="group-btn-span-special2 p_specifics" @click="approved()">审核</span><br/>
              <span class="group-btn-span-special2 p_specifics" @click="unApproved()">取消审核</span>
            </template>
          </a-popover>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="delList()"
          ><span>删除</span></button>
<!--          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="exportExcel()"
          ><span>导出</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="printDataList()"
          ><span>打印</span></button>-->
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="closeCurrent()"
          ><span>退出</span></button>
        </div>
      </div>

      <div style="clear: none"/>
      <div style="margin-top: -30px;margin-left: 90px;">
        <div style="display: inline-block;float: left;margin-top: -20px;">
          <AccountPicker theme="three" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="float: right; margin-left: 10px">
          <a-button class="ant-btn" @click="reloadTable()">
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
          <!--        <a-popover placement="bottom">
                    <a-button>
                      <PicLeftOutlined :style="{ fontSize: '14px' }" />
                    </a-button>
                  </a-popover>

                  <a-button>
                    <EditFilled :style="{ fontSize: '14px' }" />
                  </a-button>-->

          <!--            <a-button>
                        <PieChartFilled :style="{ fontSize: '14px' }" />
                      </a-button>-->
          <a-button>
            <MailOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <a-button @click="exportExcel()">
            <UsbOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <a-button @click="printDataList()">
            <PrinterOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
<!--          <a-button>
            <FilterFilled :style="{ fontSize: '14px' }"/>
          </a-button>-->
        </div>
        <div style="float: right; position: relative">
          <!--            <label style="font-size: 14px">检索条件：</label>-->
          <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
            <a-select-option style="font-size: 12px;" value="dataCode">报表编码</a-select-option>
            <a-select-option style="font-size: 12px;" value="titleName">模板名称</a-select-option>
            <a-select-option style="font-size: 12px;" value="iyear">年</a-select-option>
            <a-select-option style="font-size: 12px;" value="iperiod">月</a-select-option>
            <a-select-option style="font-size: 12px;" value="createUser">创建人</a-select-option>
          </a-select>
          <!-- 搜索 -->
          <a-input-search
            v-model:value="formItems.selectValue"
            placeholder=""
            style="width: 200px; border-radius: 4px"
            @search="onSearch"
          />
        </div>
        <div style="clear:both"/>
      </div>
    </div>
    <div class="app-container">
      <BasicTable
        ref="tableRef"
        :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys,getCheckboxProps:rowSelection.getCheckboxProps, onChange: onSelectChange }"
        @selection-change="selectionChange"
        @row-click="rowClick"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        :scroll="{ x: totalColumnWidth,y: windowHeight }"
        @register="registerTable"
        :dataSource="tableData"
        :loading="loadMark"
      >
        <template #flag="{ record }">
          <span v-if="record.id!=null && record.id!=''">
            <a-tag :color="record.flag === '1' ? 'green' : 'volcano'">
              {{ record.flag === '1' ? '已审核' : '未审核' }}
            </a-tag>
          </span>
        </template>
        <template #dataCode="{ record }">
          <span style="color: #0096c9;" @click="openEdit(record)">{{ record.dataCode }}</span>
        </template>
        <template #dataType="{ record }">
          <span v-if="record.dataType=='1'">月报</span>
          <span v-if="record.dataType=='2'">季报</span>
          <span v-if="record.dataType=='3'">年报</span>
        </template>
        <template #createDate="{ record }">
          <span v-if="record.createDate!='' && record.createDate!=null">
            {{ record.createDate.trim().substring(0, 10) }}
          </span>
        </template>
        <template #editDate="{ record }">
          <span v-if="record.editDate!='' && record.editDate!=null">
            {{ record.editDate.trim().substring(0, 10) }}
          </span>
        </template>

      </BasicTable>
      <div class="pagination-text" v-show="showPaginationText">
        共 {{paginationNumber}} 条记录&nbsp;&nbsp; 每页 200 条
      </div>
    </div>
    <Add
      @save="saveData"
      @register="registerAdd"
    />
    <Edit
      @save="saveData"
      @register="registerEdit"
    />
    <Query
      @save="addModelPage"
      @register="registerAddSelectPage"
    />
    <BatchQuery
      @save="addBatchData"
      @register="registerBatchQuery"
    />
    <BatchEdit
      @save="saveData"
      @register="registerBatchEdit"
    />

  </div>
</template>
<script setup="props, {emit}" lang="ts">
import {
  deleteReport,
  findByColumn,
  findByReportName,
  saveReport, saveReportData
} from '/@/api/record/system/report-data'

import { BasicTable, useTable } from '/@/components/Table'
import Add from './popup/add.vue'
import Edit from './popup/edit.vue'
import Query from './popup/query.vue'
import BatchQuery from './popup/batch-query.vue'
import BatchEdit from './popup/batch-edit.vue'
import {useModal} from '/@/components/Modal'
import {computed, onMounted, reactive, ref} from 'vue'
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
  EditOutlined, MailOutlined,
  ProfileOutlined,
  CalendarOutlined,
  PrinterOutlined,
  UsbOutlined
} from '@ant-design/icons-vue'

import {Select as ASelect,Input as AInput,List as AList,Row as ARow,Col as ACol ,
  Card as ACard,Popover as APopover,Button as AButton,Tag as ATag,
  Table as ATable,Checkbox as ACheckbox,
  Popconfirm as APopconfirm,message, Radio as ARadio
} from 'ant-design-vue'
import {useMessage} from "/@/hooks/web/useMessage";

const {closeCurrent} = useTabs(router);

const AListItem = AList.Item
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group

const recordName = 'ReportData'
// const {ncLogger} = useNcLogger({recordName})
// 日志
// ncLogger.info('进入了资产负债表页面')

const {
  createSuccessModal,
  createErrorModal
} = useMessage()

const formItems = ref({
  selectType: 'dataCode',
  selectValue: ''
})

const yearList: any = ref({})
async function reloadYear() {
  const res = await useRouteApi(findYearByAccount, {schemaName: dynamicTenantId})(defaultAdName.value)
  yearList.value = res
}

async function checkDate(){
  await reloadTable()
}

//打开编辑页
function openEditPage(){
  let thisInstance:any={}
  let thisOpen:any=():any=>{}
  return {
    register({instance,open}){
      thisInstance=instance
      thisOpen=open
    },
    open:(params)=>{
      thisOpen(params)
    }
  }
}
//打开编辑页
function openAddPage(){
  let thisInstance:any={}
  let thisOpen:any=():any=>{}
  return {
    register({instance,open}){
      thisInstance=instance
      thisOpen=open
    },
    open:(params)=>{
      thisOpen(params)
    }
  }
}
//打开编辑页
function openBatchEditPage(){
  let thisInstance:any={}
  let thisOpen:any=():any=>{}
  return {
    register({instance,open}){
      thisInstance=instance
      thisOpen=open
    },
    open:(params)=>{
      thisOpen(params)
    }
  }
}
//给编辑页设置注册事件
const {register:registerEdit,open :openEditPage2}=openEditPage()
const {register:registerAdd,open :openAddPage2}=openAddPage()
const {register:registerBatchEdit,open :openBatchEditPage2}=openBatchEditPage()
const dynamicTenantId = ref(getCurrentAccountName(true))
const defaultAdName = ref(useCompanyOperateStoreWidthOut().getSchemaName)
const year = ref('')
const CrudApi = {
  list: useRouteApi(findByReportName, {schemaName: dynamicTenantId}),
  columns: [
    {
      title: '审核状态',
      dataIndex: 'flag',
      ellipsis: true,
      slots: {customRender: 'flag'}
    },
    {
      title: '报表编码',
      dataIndex: 'dataCode',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2,'color': 'blue' }} // return 想要设置的样式
      },
      ellipsis: true,
      slots: { customRender: 'dataCode' }
    },
    {
      title: '模板名称',
      dataIndex: 'titleName',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
      },
      ellipsis: true
    },
    {
      title: '年',
      dataIndex: 'iyear',
      width: 120,
      ellipsis: true
    },
    {
      title: '季度',
      dataIndex: 'jidu',
      width: 120,
      ellipsis: true
    },
    {
      title: '月',
      dataIndex: 'iperiod',
      width: 120,
      ellipsis: true
    },
    {
      title: '类型',
      dataIndex: 'dataType',
      width: 120,
      ellipsis: true,
      slots: { customRender: 'dataType' }
    },
    {
      title: '创建人',
      dataIndex: 'createUser',
      width: 120,
      ellipsis: true
    },
    {
      title: '创建日期',
      dataIndex: 'createDate',
      width: 120,
      ellipsis: true,
      slots: {customRender: 'createDate'}
    },
    {
      title: '审核人',
      dataIndex: 'editUser',
      width: 120,
      ellipsis: true
    },
    {
      title: '审核日期',
      dataIndex: 'editDate',
      width: 120,
      ellipsis: true,
      slots: {customRender: 'editDate'}
    }
  ],
  editData: {
    reportName: '',
    templateId: '',
    templateName: '',
    accStandard: '',
    kemuTemplateId: '',
    titleName: '',
    dataCode: '',
    dataType: '',
    iyear: '',
    jidu: '',
    iperiod: '',
    idate: '',
    createUser: '',
    editUser: '',
    menu1: '',
    menu2: '',
    menu3: '',
    menu4: '',
    menu5: '',
    menu6: '',
  }
}
// 这是示例组件
const [registerTable, { reload,getDataSource,setTableData,getColumns,setColumns,getPaginationRef,setPagination,deleteSelectRowByKey }] = useTable({
  // api: CrudApi.list,
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
  // actionColumn: {
  //   width: 120,
  //   title: '操作',
  //   dataIndex: 'action',
  //   slots: { customRender: 'action' }
  // },
  searchInfo: {
    reportName: 'zcfzb'
  }
})

const [registerAddSelectPage, {openModal: openAddSelectPage}] = useModal()
const [registerBatchQuery, {openModal: openBatchQuery}] = useModal()
// const [registerEditPage, { openModal: openEditPage }] = useModal()
/*setTimeout(()=>{
  openEditPage2({
    data:{abc:1,dd:2} ,
    ok(){

    },
    cancel(){

    }
  })
},3000)*/
const val = {
  reportName: '',
  templateId: '',
  templateName: '',
  accStandard: '',
  kemuTemplateId: '',
  titleName: '',
  dataCode: '',
  dataType: '',
  iyear: '',
  jidu: '',
  iperiod: '',
  idate: '',
  createUser: '',
  editUser: '',
  menu1: '',
  menu2: '',
  menu3: '',
  menu4: '',
  menu5: '',
  menu6: '',
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
    disabled:  record.id==null
  }),
};
// 行勾选事件
const editAndDelBtnShow = ref(false);
function selectionChange(a) {
  editAndDelBtnShow.value=a.rows.filter(aa=>aa.id!=null).length>0?true:false
}

function rowClick(a) {
  if(hasBlank(a.id)){
    deleteSelectRowByKey(a.key)
  }
}

const openAdd = () => {
  openAddSelectPage(true, {
    data: {},
    dynamicTenantId: dynamicTenantId.value,
    defaultAdName: defaultAdName.value,
    year: year.value
  })
}
const addModelPage = (data) => {
  data.ynamicTenantId = dynamicTenantId.value
  data.defaultAdName = defaultAdName.value
  data.year = year.value
  openAddPage2({
    data: data,
    dynamicTenantId: dynamicTenantId.value,
    defaultAdName: defaultAdName.value,
    year: year.value
  })
}
const openEdit = (data:any) => {
  data.ynamicTenantId = dynamicTenantId.value
  data.defaultAdName = defaultAdName.value
  data.year = year.value
  openEditPage2({
    data: data,
    dynamicTenantId: dynamicTenantId.value,
    defaultAdName: defaultAdName.value,
    year: year.value
  })
}
const editOpen = (data:any) => {
  if (checkRow.value.length>0) {
    checkRow.value[0].ynamicTenantId = dynamicTenantId.value
    checkRow.value[0].defaultAdName = defaultAdName.value
    checkRow.value[0].year = year.value
    openBatchEditPage2({
      data: checkRow.value,
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value,
      year: year.value
    })
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '查看',
      content: '请选择一条进行查看！'
    })
  }
}
const del = async(data:any) => {
  await useRouteApi(deleteReport, {schemaName: dynamicTenantId})(data.id)
  await reloadCurrentPage()
  // alert('删除成功！')
  createSuccessModal({
    iconType: 'success',
    title: '删除',
    content: '删除成功！'
  })
  await reloadTable()
}

const tableData:any = ref([]);
const tableDataAll:any = ref([]);
const showPaginationText = ref(false)
const paginationNumber = ref(0)
async function reloadTable() {
  /*reload({
    searchInfo: {
      reportName: 'zcfzb'
    }
  })*/
  showPaginationText.value = false
  let len = 0
  const res = await useRouteApi(findByReportName, {schemaName: dynamicTenantId})({reportName: 'zcfzb',iyear:year.value})
  tableDataAll.value = res.items
  tableData.value = tableDataAll.value
  let num1 = tableDataAll.value.length%200
  if (num1<50) {
    let num = 50 - (tableDataAll.value.length % 50)
    for (let i = 0; i < num; i++) {
      tableData.value.push({})
    }
  }
  len = tableDataAll.value.filter(item=>item.id!=null).length
  paginationNumber.value = len
  showPaginationText.value = true
  checkRow.value = []
  state.selectedRowKeys = []
}

async function delList() {
  if (checkRow.value.length > 0) {
    let num = 0
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      if (item.flag == '1') {
        num++
        createErrorModal({
          iconType: 'warning',
          title: '警告',
          content: '已审核的不能进行删除！'
        })
        return false
      }
    }
    if (num == 0) {
      createConfirm({
        iconType: 'error',
        title: '警告',
        content: '删除后数据将不能恢复，你确认要删除吗?',
        onOk: async () => {
          for (let i = 0; i < checkRow.value.length; i++) {
            const item = checkRow.value[i]
            await useRouteApi(deleteReport, {schemaName: dynamicTenantId})(item.id)
          }
          message.success('删除成功！')
          checkRow.value = []
          state.selectedRowKeys = []
          await reloadCurrentPage()
          await reloadTable()
        },
        onCancel: () => {
          return false
        }
      })
    }
  } else{
    createErrorModal({
      iconType: 'warning',
      title: '警告',
      content: '请选择需要删除的内容！'
    })
    return false
  }
}

//审核
async function approved(){
  if (checkRow.value.length > 0) {
    let num = 0
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      if (item.flag == '1') {
        num++
        createErrorModal({
          iconType: 'warning',
          title: '审核',
          content: '已有审核项不能进行审核！'
        })
        return false
      }
    }
    if (num == 0) {
      let dateTime = new_Date()
      for (let i = 0; i < checkRow.value.length; i++) {
        const item = checkRow.value[i]
        item.flag = '1'
        item.editDate = dateTime
        item.editUser = useUserStore().getUserInfo['realName']
        await useRouteApi(saveReportData, {schemaName: dynamicTenantId})(item)
      }
    }
    state.selectedRowKeys = []
    checkRow.value = []
    await reloadTable()
    return true
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '审核',
      content: '请选择需要审核的内容！'
    })
    return false
  }
}

//取消审核
async function unApproved(){
  if (checkRow.value.length > 0) {
    let num = 0
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      if (item.flag != '1') {
        num++
        createErrorModal({
          iconType: 'warning',
          title: '取消审核',
          content: '已有未审核项不能进行取消审核！'
        })
        return false
      }
    }
    if (num == 0) {
      for (let i = 0; i < checkRow.value.length; i++) {
        const item = checkRow.value[i]
        item.flag = '0'
        item.editDate = ''
        item.editUser = ''
        await useRouteApi(saveReportData, {schemaName: dynamicTenantId})(item)
      }
    }
    state.selectedRowKeys = []
    checkRow.value = []
    await reloadTable()
    return true
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '取消审核',
      content: '请选择需要取消审核的内容！'
    })
    return false
  }
}

//获取当年月日
function new_Date() {
  let dateTime
  let yy = new Date().getFullYear()
  let mm = new Date().getMonth() + 1 < 10 ? '0' + new Date().getMonth() + 1 : new Date().getMonth() + 1
  let dd = new Date().getDate() < 10 ? '0' + new Date().getDate() : new Date().getDate()
  let hh = new Date().getHours()
  let mf = new Date().getMinutes() < 10 ? '0' + new Date().getMinutes() : new Date().getMinutes()
  let ss = new Date().getSeconds() < 10 ? '0' + new Date().getSeconds() : new Date().getSeconds()
  dateTime = yy + '-' + mm + '-' + dd + ' ' + hh + ':' + mf + ':' + ss
  console.log(dateTime)
  return dateTime
}

const cardList:any = ref([])
async function reloadCurrentPage() {
  const res = await useRouteApi(findByReportName,{schemaName: dynamicTenantId})({reportName: 'zcfzb',iyear:year.value})
  cardList.value = res.items
  // console.log(cardList.value)
}
const defaultPage = ref(false)
onMounted(async() => {
  await reloadCurrentPage()
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

async function saveData(data: any) {
  await useRouteApi(saveReport, {schemaName: dynamicTenantId})(data)
  await reloadCurrentPage()
  await reloadTable()
}

//批量生成
const openBatchQueryAdd = () => {
  openBatchQuery(true, {
    data: {},
    dynamicTenantId: dynamicTenantId.value,
    defaultAdName: defaultAdName.value,
    year: year.value
  })
}

async function addBatchData(data: any) {
  message.success('批量生成成功！')
  await reloadCurrentPage()
  await reloadTable()
}

//批量打印
import {useNewPrint} from "/@/utils/boozsoft/print/print";
const loadMark = ref(false)
async function printDataList() {
  if (checkRow.value.length > 0) {
    loadMark.value = true
    for (const item of checkRow.value) {
      item.zcTable = []
      item.fzTable = []
      item.table = []
      if (item.id != '' && item.id != null) {
        const columnList = await useRouteApi(findByColumn, {schemaName: dynamicTenantId})(item.id)
        item.zcTable = columnList.filter(column => {
          return column.columnType == 'zc'
        })
        item.fzTable = columnList.filter(column => {
          return column.columnType == 'fz'
        })
      }
    }
    useNewPrint({data: ['p', 'px', 'a4', true]}, (doc) => {
      checkRow.value.forEach((item, index) => {
        const table: any = ref([])
        table.value = item.zcTable.map((column, num) => {
          const item1 = {}
          if (column.jici == '2') {
            item1[0] = '    ' + column.columnShowName
          } else if (column.jici == '3') {
            item1[0] = '        ' + column.columnShowName
          } else if (column.jici == '4') {
            item1[0] = '            ' + column.columnShowName
          } else {
            item1[0] = column.columnShowName
          }
          item1[1] = item.zcTable[num].hangci
          item1[2] = toThousandFilter(item.zcTable[num].qimoMoney)
          item1[3] = toThousandFilter(item.zcTable[num].nianchuMoney)
          if (item.fzTable[num].jici == '2') {
            item1[4] = '    ' + item.fzTable[num].columnShowName
          } else if (item.fzTable[num].jici == '3') {
            item1[4] = '        ' + item.fzTable[num].columnShowName
          } else if (item.fzTable[num].jici == '4') {
            item1[4] = '            ' + item.fzTable[num].columnShowName
          } else {
            item1[4] = item.fzTable[num].columnShowName
          }
          item1[5] = item.fzTable[num].hangci
          item1[6] = toThousandFilter(item.fzTable[num].qimoMoney)
          item1[7] = toThousandFilter(item.fzTable[num].nianchuMoney)
          return item1
        })
        loadMark.value = false
        doc.autoTable({
          head: [['资产负债表', '', '', '', '', '', '', ''],
            ['', '', '', item.menu2, '', '', '', item.menu6],
            [item.menu1, '', '', '', '', '', '', item.menu3],
            ['项目', '行次', '期末余额', '年初余额', '项目', '行次', '期末余额', '年初余额']],
          body: table.value,
          // startY: 70,
          styles: tableStyle(),
          margin: {left: 30},
          addPageContent: (data) => {

            //data.table.finalY 表格最大可容纳y轴坐标，超出时将根据设置决定是否另取一页.在页面需要分页时出现
            let tabHeigth = data.table.height,
              tabMarginTop = data.settings.margin.top,
              tabSize = data.table.finalY - tabMarginTop,//表格最大Y轴-表格顶部距离得到每页表格的最大值
              tabMarginLeft = data.settings.margin.left;
            if (data.table.finalY)//是否分页 有分页时才有该属性finalY
              if (data.pageNumber != Math.ceil(tabHeigth / tabSize)) return;//如果需要每一页都显示页尾则注释该行代码
            //data.cursor.y ,data.cursor.x:表格最后一个单元坐标
            data.doc.setFontSize(10)

            doc.autoTableText(
              item.menu4,
              tabMarginLeft,
              data.cursor.y + 3,
              0
            );
            doc.autoTableText(
              item.menu5,
              data.cursor.x - 80,
              data.cursor.y + 3,
              0
            );
          },
          // didDrawPage(data) {
          //
          // },
          didParseCell(data) {
            // data.cell.styles.cellPadding = {top:10,left:2,right:4,bottom:9}
            data.cell.styles.cellPadding = {top: 4, left: 2, right: 1, bottom: 2}
            data.cell.styles.fillColor = [255, 255, 255]
            data.cell.styles.fontSize = 8

            if (data.section == 'head' && data.row.index == 0) {
              data.cell.styles.fontSize = 14
              data.cell.styles.bold = true
              data.cell.styles.lineColor = [255, 255, 255]
              data.cell.colSpan = 8
              data.cell.styles.halign = 'center'
            }
            if (data.section == 'head' && data.row.index == 1) {
              data.cell.styles.fontSize = 10
              data.cell.styles.lineColor = [255, 255, 255]
              // data.cell.colSpan = 8
              data.cell.styles.halign = 'right'
              if (data.column.index == 3) {
                data.cell.colSpan = 2
                data.cell.styles.halign = 'center'
              }
            }
            if (data.section == 'head' && data.row.index == 2) {
              data.cell.styles.fontSize = 10
              data.cell.styles.lineColor = [255, 255, 255]
              if (data.column.index == 0) {
                data.cell.colSpan = 4
                data.cell.styles.halign = 'left'
              } else {
                data.cell.styles.halign = 'right'
              }
            }
            if (data.section == 'head' && data.row.index == 3) {
              data.cell.styles.cellPadding = {top: 7, left: 2, right: 2, bottom: 2}
              data.cell.styles.halign = 'center'
            }
          },
          columnStyles: {
            0: {maxHeight: 10, cellWidth: 60, halign: 'left'},
            1: {cellWidth: 20, halign: 'center'},
            2: {cellWidth: 58, halign: 'right'},
            3: {cellWidth: 58, halign: 'right'},
            4: {cellWidth: 60, halign: 'left'},
            5: {cellWidth: 20, halign: 'center'},
            6: {cellWidth: 58, halign: 'right'},
            7: {cellWidth: 58, halign: 'right'}
          }
        })
        if (index < checkRow.value.length - 1) {
          doc.addPage()
        }
      })
    })
    // savePdf('a.pdf',{
    //   data:['p', 'px', 'a4', true]
    // },(doc) => {
    //   checkRow.value.forEach((item, index) => {
    //     const table: any = ref([])
    //     table.value = item.zcTable.map((column, num) => {
    //       const item1 = {}
    //       if (column.jici == '2') {
    //         item1[0] = '    ' + column.columnShowName
    //       } else if (column.jici == '3') {
    //         item1[0] = '        ' + column.columnShowName
    //       } else if (column.jici == '4') {
    //         item1[0] = '            ' + column.columnShowName
    //       } else {
    //         item1[0] = column.columnShowName
    //       }
    //       item1[1] = item.zcTable[num].hangci
    //       item1[2] = toThousandFilter(item.zcTable[num].qimoMoney)
    //       item1[3] = toThousandFilter(item.zcTable[num].nianchuMoney)
    //       if (item.fzTable[num].jici == '2') {
    //         item1[4] = '    ' + item.fzTable[num].columnShowName
    //       } else if (item.fzTable[num].jici == '3') {
    //         item1[4] = '        ' + item.fzTable[num].columnShowName
    //       } else if (item.fzTable[num].jici == '4') {
    //         item1[4] = '            ' + item.fzTable[num].columnShowName
    //       } else {
    //         item1[4] = item.fzTable[num].columnShowName
    //       }
    //       item1[5] = item.fzTable[num].hangci
    //       item1[6] = toThousandFilter(item.fzTable[num].qimoMoney)
    //       item1[7] = toThousandFilter(item.fzTable[num].nianchuMoney)
    //       return item1
    //     })
    //     doc.autoTable({
    //       head: [['资产负债表', '', '', '', '', '', '', ''],
    //         [item.menu6, '', '', '', '', '', '', ''],
    //         [item.menu1, '', '', item.menu2, '', '', '', item.menu3],
    //         ['项目', '行次', '期末余额', '年初余额', '项目', '行次', '期末余额', '年初余额']],
    //       body: table.value,
    //       // startY: 70,
    //       styles: tableStyle(),
    //       margin: {left: 30},
    //       addPageContent: (data) => {
    //
    //         //data.table.finalY 表格最大可容纳y轴坐标，超出时将根据设置决定是否另取一页.在页面需要分页时出现
    //         let tabHeigth = data.table.height,
    //           tabMarginTop = data.settings.margin.top,
    //           tabSize = data.table.finalY - tabMarginTop,//表格最大Y轴-表格顶部距离得到每页表格的最大值
    //           tabMarginLeft = data.settings.margin.left;
    //         if (data.table.finalY)//是否分页 有分页时才有该属性finalY
    //           if (data.pageNumber != Math.ceil(tabHeigth / tabSize)) return;//如果需要每一页都显示页尾则注释该行代码
    //         //data.cursor.y ,data.cursor.x:表格最后一个单元坐标
    //         data.doc.setFontSize(10)
    //
    //         doc.autoTableText(
    //           item.menu4,
    //           tabMarginLeft,
    //           data.cursor.y + 3,
    //           0
    //         );
    //         doc.autoTableText(
    //           item.menu5,
    //           data.cursor.x - 65,
    //           data.cursor.y + 3,
    //           0
    //         );
    //       },
    //       // didDrawPage(data) {
    //       //
    //       // },
    //       didParseCell(data) {
    //         // data.cell.styles.cellPadding = {top:10,left:2,right:4,bottom:9}
    //         data.cell.styles.cellPadding = {top: 4, left: 2, right: 1, bottom: 2}
    //         data.cell.styles.fillColor = [255, 255, 255]
    //         data.cell.styles.fontSize = 8
    //
    //         if (data.section == 'head' && data.row.index == 0) {
    //           data.cell.styles.fontSize = 14
    //           data.cell.styles.bold = true
    //           data.cell.styles.lineColor = [255, 255, 255]
    //           data.cell.colSpan = 8
    //           data.cell.styles.halign = 'center'
    //         }
    //         if (data.section == 'head' && data.row.index == 1) {
    //           data.cell.styles.fontSize = 10
    //           data.cell.styles.lineColor = [255, 255, 255]
    //           data.cell.colSpan = 8
    //           data.cell.styles.halign = 'right'
    //         }
    //         if (data.section == 'head' && data.row.index == 2) {
    //           data.cell.styles.fontSize = 10
    //           data.cell.styles.lineColor = [255, 255, 255]
    //           if (data.column.index == 0) {
    //             data.cell.colSpan = 3
    //             data.cell.styles.halign = 'left'
    //           } else if (data.column.index == 3) {
    //             data.cell.colSpan = 2
    //             data.cell.styles.halign = 'center'
    //           } else {
    //             data.cell.styles.halign = 'right'
    //           }
    //         }
    //         if (data.section == 'head' && data.row.index == 3) {
    //           data.cell.styles.cellPadding = {top: 7, left: 2, right: 2, bottom: 2}
    //           data.cell.styles.halign = 'center'
    //         }
    //         /*if (data.section == 'body' && data.column.index == 0) {
    //           if (itemConfig.value.zcTable[data.row.index].jici == 2) {
    //             data.cell.styles.cellPadding = {top: 4, left: 10, right: 1, bottom: 2}
    //           }
    //           if (itemConfig.value.zcTable[data.row.index].jici == 3) {
    //             data.cell.styles.cellPadding = {top: 4, left: 20, right: 1, bottom: 2}
    //           }
    //           if (itemConfig.value.zcTable[data.row.index].jici == 4) {
    //             data.cell.styles.cellPadding = {top: 4, left: 30, right: 1, bottom: 2}
    //           }
    //         }*/
    //
    //         /*if (data.section == 'body' && data.column.index == 4) {
    //           if (itemConfig.value.fzTable[data.row.index].jici == 2) {
    //             data.cell.styles.cellPadding = {top: 4, left: 10, right: 1, bottom: 2}
    //           }
    //           if (itemConfig.value.fzTable[data.row.index].jici == 3) {
    //             data.cell.styles.cellPadding = {top: 4, left: 20, right: 1, bottom: 2}
    //           }
    //           if (itemConfig.value.fzTable[data.row.index].jici == 4) {
    //             data.cell.styles.cellPadding = {top: 4, left: 30, right: 1, bottom: 2}
    //           }
    //         }*/
    //       },
    //       columnStyles: {
    //         0: {maxHeight: 10, cellWidth: 60, halign: 'left'},
    //         1: {cellWidth: 20, halign: 'center'},
    //         2: {cellWidth: 58, halign: 'right'},
    //         3: {cellWidth: 58, halign: 'right'},
    //         4: {cellWidth: 60, halign: 'left'},
    //         5: {cellWidth: 20, halign: 'center'},
    //         6: {cellWidth: 58, halign: 'right'},
    //         7: {cellWidth: 58, halign: 'right'}
    //       }
    //     })
    //     if (index < checkRow.value.length - 1) {
    //       doc.addPage()
    //     }
    //   })
    // })
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '打印',
      content: '请选择需要打印的内容！'
    })
    return false
  }
}

//批量导出Excel
import XLSX from "xlsx-js-style";
import {
  sheet_from_array_of_arrays, Workbook,
  writeExcel, defaultV
} from "/@/utils/boozsoft/excel/excel2";

async function exportExcel() {
  if (checkRow.value.length > 0) {
    const sheet: any = []
    for (const item of checkRow.value) {
      const table: any = ref([])
      item.zcTable = []
      item.fzTable = []
      item.table = []
      if (item.id != '' && item.id != null) {
        const columnList = await useRouteApi(findByColumn, {schemaName: dynamicTenantId})(item.id)
        item.zcTable = columnList.filter(column => {
          return column.columnType == 'zc'
        })
        item.fzTable = columnList.filter(column => {
          return column.columnType == 'fz'
        })
        table.value = item.zcTable.map((column, num) => {
          const item1 = {}
          if (column.jici == '2') {
            item1[0] = '    ' + column.columnShowName
          } else if (column.jici == '3') {
            item1[0] = '        ' + column.columnShowName
          } else if (column.jici == '4') {
            item1[0] = '            ' + column.columnShowName
          } else {
            item1[0] = column.columnShowName
          }
          item1[1] = item.zcTable[num].hangci
          item1[2] = toThousandFilter(item.zcTable[num].qimoMoney)
          item1[3] = toThousandFilter(item.zcTable[num].nianchuMoney)
          if (item.fzTable[num].jici == '2') {
            item1[4] = '    ' + item.fzTable[num].columnShowName
          } else if (item.fzTable[num].jici == '3') {
            item1[4] = '        ' + item.fzTable[num].columnShowName
          } else if (item.fzTable[num].jici == '4') {
            item1[4] = '            ' + item.fzTable[num].columnShowName
          } else {
            item1[4] = item.fzTable[num].columnShowName
          }
          item1[5] = item.fzTable[num].hangci
          item1[6] = toThousandFilter(item.fzTable[num].qimoMoney)
          item1[7] = toThousandFilter(item.fzTable[num].nianchuMoney)
          return item1
        })
      }
      sheet.push({
        title: '资产负债表',
        multiHeader: [
          ['', '', item.menu2, '', '', '', '', item.menu6],
          [item.menu1, '', '', '', '', '', '', item.menu3],
          ['项目', '行次', '期末余额', '年初余额', '项目', '行次', '期末余额', '年初余额']
        ],
        table: table.value,
        foot: [item.menu4, item.menu5, '', '', '', '', '', ''],
        keys: [0, 1, 2, 3, 4, 5, 6, 7],
        merges: ['A3:D3', 'C2:F2'],
        sheetName: item.dataCode,
        cellStyle: [
          {
            cell: 'A1',
            font: {
              sz: 14,
              color: {rgb: "000000"},
              bold: true,
            },
            /*fill: {
              fgColor: { rgb: "ff7e00" },
            }*/
            border: {color: {rgb: "ffffff"}}
          },
        ],
        colWidth: [15, 3, 10, 10, 15, 3, 10, 10]
      })
    }
    // 处理数据前
    if (!sheet || sheet.length <= 0) {
      this.onError('Table data cannot be empty')
      return
    }
    const wb:any = Workbook()
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
      // 处理标题格式
      if (title || title === '') {
        // 取表头、多级表头中的最大值
        const tHeaderLength = tHeader && tHeader.length || 0
        const multiHeaderLength = multiHeader && Math.max(...multiHeader.map(m => m.length)) || 0
        const titleLength:any = Math.max(tHeaderLength, multiHeaderLength, keys.length)
        // 第一个元素为title，剩余以空字符串填充
        title = [title].concat(Array(titleLength - 1).fill(''))
        // 处理标题的合并\
        const cell = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']
        let mergeSecond = 'A1'
        if (titleLength > 26) {
          const one = parseInt(titleLength) / 26
          const two = titleLength % 26
          mergeSecond = cell[one - 1] + cell[two - 1] + '1'
        } else {
          mergeSecond = cell[titleLength - 1] + '1'
        }
        const titleMerge = `A1:${mergeSecond}`
        if (!merges) {
          merges = [titleMerge]
        } else {
          if (merges.indexOf(titleMerge) === -1) {
            merges.push(titleMerge)
          }
        }
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
      //表格底部对应字段
      if (foot || foot === '') {
        foot && data.push(foot);
        const str = ('B' + data.length) + (':H' + data.length)
        merges.push(str)
      }
      const ws:any = sheet_from_array_of_arrays(data,'');
      if (merges && merges.length > 0) {
        if (!ws['!merges']) ws['!merges'] = [];
        merges.forEach(merge => {
          ws['!merges'].push(XLSX.utils.decode_range(merge))
        })
      }
      // 如果没有列宽则自适应
      if (!colWidth) {
        // 基准比例，以12为标准
        const benchmarkRate = globalStyle.font.sz && globalStyle.font.sz / 12 || 1
        // 空字符长度
        const nullstr = 10 * benchmarkRate + 2
        // 单个中文字符长度
        const chinese = 2 * benchmarkRate
        // 单个非中文字符长度
        const nChinese = benchmarkRate
        //设置worksheet每列的最大宽度,并+2调整一点列宽
        const sheetColWidth = data.map(row => row.map(val => {
          //先判断是否为null/undefined
          if (!val) {
            return {
              'wch': nullstr
            };
          } else {
            const strArr = val.toString().split('')
            const pattern = new RegExp("[\u4E00-\u9FA5]+")
            let re = strArr.map(str => {
              // 是否为中文
              if (pattern.test(str)) {
                return chinese
              } else {
                return nChinese
              }
            })
            re = re.reduce((total, r) => total + r, 0)
            return {
              'wch': re + 2
            };
          }
        }))
        /*以第一行为初始值*/
        let result = sheetColWidth[0];
        for (let i = 1; i < sheetColWidth.length; i++) {
          for (let j = 0; j < sheetColWidth[i].length; j++) {
            if (result[j]['wch'] < sheetColWidth[i][j]['wch']) {
              result[j]['wch'] = sheetColWidth[i][j]['wch'];
            }
          }
        }
        ws['!cols'] = result;
      } else {
        ws['!cols'] = colWidth.map(i => {
          return {wch: i}
        })
      }

      // 添加工作表
      wb.SheetNames.push(sheetName);
      wb.Sheets[sheetName] = ws;
      let dataInfo = wb.Sheets[wb.SheetNames[index]];

      //全局样式
      (function () {
        Object.keys(dataInfo).forEach(i => {
          if (i == '!ref' || i == '!merges' || i == '!cols') {
          } else {
            dataInfo[i.toString()].s = globalStyle;
            const {border, font, alignment, fill} = globalStyle;
            if (i.substring(1) == '1') {
              dataInfo[i.toString()].s = {
                border: {},
                font: font || globalStyle.font,
                alignment: alignment || globalStyle.alignment,
                fill: fill || globalStyle.fill
              }
            } else if (i.substring(1) == '2') {
              dataInfo[i.toString()].s = {
                border: {},
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "right",
                }
              }
              if (i == 'C2') {
                dataInfo[i.toString()].s = {
                  border: {},
                  font: font || globalStyle.font,
                  fill: fill || globalStyle.fill,
                  alignment: alignment || globalStyle.alignment,
                }
              }
            } else if (i.substring(1) == '3') {
              dataInfo[i.toString()].s = {
                border: {},
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "left",
                }
              }
              if (i == 'H3') {
                dataInfo[i.toString()].s = {
                  border: {},
                  font: font || globalStyle.font,
                  fill: fill || globalStyle.fill,
                  alignment: {
                    horizontal: "right",
                  }
                }
              }
            } else if (i.substring(1) == '4') {
              dataInfo[i.toString()].s = {
                border: border === {} ? border : border || globalStyle.border,
                font: font || globalStyle.font,
                alignment: alignment || globalStyle.alignment,
                fill: {fgColor: {rgb: "cccccc"}}
              }
            } else if (i.substring(0, 1) == 'A') {
              dataInfo[i.toString()].s = {
                border: border === {} ? border : border || globalStyle.border,
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "left",
                }
              }
            } else if (i.substring(0, 1) == 'C') {
              dataInfo[i.toString()].s = {
                border: border === {} ? border : border || globalStyle.border,
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "right",
                }
              }
            } else if (i.substring(0, 1) == 'D') {
              dataInfo[i.toString()].s = {
                border: border === {} ? border : border || globalStyle.border,
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "right",
                }
              }
            } else if (i.substring(0, 1) == 'E') {
              dataInfo[i.toString()].s = {
                border: border === {} ? border : border || globalStyle.border,
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "left",
                }
              }
            } else if (i.substring(0, 1) == 'G') {
              dataInfo[i.toString()].s = {
                border: border === {} ? border : border || globalStyle.border,
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "right",
                }
              }
            } else if (i.substring(0, 1) == 'H') {
              dataInfo[i.toString()].s = {
                border: border === {} ? border : border || globalStyle.border,
                font: font || globalStyle.font,
                fill: fill || globalStyle.fill,
                alignment: {
                  horizontal: "right",
                }
              }
            }
            if (foot.length > 0) {
              if (i.substring(1) == data.length) {
                dataInfo[i.toString()].s = {
                  border: {},
                  font: font || globalStyle.font,
                  fill: fill || globalStyle.fill,
                  alignment: {
                    horizontal: "left",
                  }
                }
                if (i == 'B' + data.length) {
                  dataInfo[i.toString()].s = {
                    border: {},
                    font: font || globalStyle.font,
                    fill: fill || globalStyle.fill,
                    alignment: {
                      horizontal: "right",
                    }
                  }
                }
              }
            }
          }
        });
      })();

      // 单个样式
      (function () {
        if (!cellStyle || cellStyle.length <= 0) {
          return
        }
        cellStyle.forEach(s => {
          const {border, font, alignment, fill} = s;
          dataInfo[s.cell].s = {
            border: border === {} ? border : border || globalStyle.border,
            font: font || globalStyle.font,
            alignment: alignment || globalStyle.alignment,
            fill: fill || globalStyle.fill
          }
        });
      })();
    })

    const bookType = 'xlsx'
    // 类型默认为xlsx
    writeExcel(wb, bookType, '资产负债表_' + year.value + '年' + '_'+pageParameter.companyName)
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '导出',
      content: '请选择需要导出的内容！'
    })
    return false
  }
}

function toThousandFilter(num: any) {
  if (num == '' || num == null) {
    return ''
  }
  return (+num || 0).toFixed(2).replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,')
}

function onSearch() {
  let a: any = []
  a = cardList.value.filter(item => {
    //通过报表编码过滤
    if (formItems.value.selectType == 'dataCode' && formItems.value.selectValue != '') {
      return item.dataCode.indexOf(formItems.value.selectValue) != -1
    }
    //通过报表名称过滤
    if (formItems.value.selectType == 'titleName' && formItems.value.selectValue != '') {
      return item.titleName.indexOf(formItems.value.selectValue) != -1
    }
    //通过年过滤
    if (formItems.value.selectType == 'iyear' && formItems.value.selectValue != '') {
      return item.iyear.indexOf(formItems.value.selectValue) != -1
    }
    //通过月过滤
    if (formItems.value.selectType=='iperiod' && formItems.value.selectValue!=''){
      return item.iperiod.indexOf(formItems.value.selectValue) != -1
    }
    //通过创建人过滤
    if (formItems.value.selectType=='createUser' && formItems.value.selectValue!=''){
      return item.createUser.indexOf(formItems.value.selectValue) != -1
    }
    //通过状态过滤
    if (formItems.value.selectType=='dataType' && formItems.value.selectValue!=''){
      if (formItems.value.selectValue=='月报'){
        return item.dataType=='1'
      }
      if (formItems.value.selectValue=='季报'){
        return item.dataType=='2'
      }
      if (formItems.value.selectValue=='年报'){
        return item.dataType=='3'
      }
      return item.dataType.indexOf(formItems.value.selectValue) != -1
    }
    return item
  })
  // console.log(a)
  // setDataSource(a)
  setTableData([])
  setTableData(a)
}

/*const pageParameter = reactive({
  showRulesSize: 'MIN',
})*/

/**********************栏目设置**********************/
import {initDynamics,assemblyDynamicColumn} from "./data";
// import {useMessage} from "/@/hooks/web/useMessage";
import {
  findDbLanMuList,
  saveLanMuList,
} from '/@/api/record/system/accvoucher';
import {
  getCurrentAccountName, getThisIndexImg, hasBlank
} from "/@/api/task-api/tast-bus-api";
import {cloneDeep} from "lodash-es";
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {findDataBase} from "/@/api/record/system/account";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useAbcTemplateStoreWidthOut} from "/@/store/modules/abc";
import {tableStyle} from "/@/store/modules/abc-print";
import {useNcLogger} from "/@/utils/boozsoft/record/recordUtils";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {findYearByAccount} from "/@/api/record/system/bank-statement";

const {createConfirm, createWarningModal} = useMessage();
const pageParameter: any = reactive({
  showRulesSize: 'MIN',
  companyCode: '102',
  companyName: '公司一',
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
const tableRef:any = ref(null)
const lanMuData = {
  'accId': '',
  'menuName': '财务报表',
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
  if (total > windowWidth) {
    let f = 0
    if (visible.value) f = 260
    totalColumnWidth.value = Number(windowWidth) - f
    tableRef.value.$el.style.setProperty('width',(windowWidth+20-f)+'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width',(total+20)+'px')
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
  await reloadYear()
  await reloadTable()
  let data: any = {}
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  data.reportName = 'zcfzb'
  // let res: any = await useRouteApi(CrudApi.list, {schemaName: obj.accountMode})(data)
  // setTableData([]) // 清空可能残留的数据
  // setTableData(res.items)
  checkRow.value = []
  state.selectedRowKeys = []
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
    bottom: 6px;
    right: 200px;
    font-size: 13px;
    color: black;
    z-index: 99;
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
