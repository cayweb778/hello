<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
<!--        <div class="container-head-title">
          <UnorderedListOutlined style="color: #0096c7"/>
          <b class="noneSpan">现金流量项目</b>
        </div>-->
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">现金流量项目</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="init()"
          ><span>初始化</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            v-if="isGroupFlg === '1'"
            ant-click-animating-without-extra-node="false"
            @click="openAddPage()"
          ><span>新增</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="editOpen()"
          ><span>编辑</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            v-if="isGroupFlg === '1'"
            ant-click-animating-without-extra-node="false"
            @click="delList()"
          ><span>删除</span></button>
          <!--        按年度校验科目是否末级-->
          <button
            type="button"
            class="ant-btn ant-btn-me "
            ant-click-animating-without-extra-node="false"
          ><span>校验科目</span></button>

          <button
            type="button"
            class="ant-btn ant-btn-me"
            v-if="isGroupFlg === '1'"
            @click="openExcel()"
          ><span>导入</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/one/home/welcome')"><span>退出</span></button>
        </div>
      </div>
      <div style="clear: none"/>
      <div style="margin-top: -30px;margin-left: 80px;">
        <div style="display: inline-block;float: left;margin-top: -25px;">
          <AccountPicker theme="three" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">共 {{totalData}} 条记录</div>
<!--        <div style="display: inline-block;float: left;font-size: 14px;padding-top: 5px;padding-left: 20px;">
          <span> 共{{totalData}}条记录</span>
        </div>-->
        <div style="float: right; margin-left: 10px">
          <a-button class="ant-btn-me">
            <SyncOutlined :style="{ fontSize: '14px' }" />
          </a-button>
          <a-popover placement="bottom">
            <template #content>
            <span @click="pageParameter.showRulesSize = 'MAX'"
                  :style="{backgroundColor: (pageParameter.showRulesSize==='MAX')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortDescendingOutlined
              :style="{ fontSize: '14px' }"/>&emsp;大号字体&emsp;<CheckOutlined v-if="pageParameter.showRulesSize==='MAX'"
                                                                            :style="{ fontSize: '14px' }"/></span><br/>
              <span @click="pageParameter.showRulesSize = 'MIN'"
                    :style="{backgroundColor: (pageParameter.showRulesSize==='MIN')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortAscendingOutlined
                :style="{ fontSize: '14px' }"/>&emsp;小号字体&emsp;<CheckOutlined v-if="pageParameter.showRulesSize==='MIN'"
                                                                              :style="{ fontSize: '14px' }"/></span>
            </template>
            <template #title>
              <b>设置表格字号</b>
            </template>
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
          <a-button>
            <UsbOutlined :style="{ fontSize: '14px' }" />
          </a-button>
          <a-button @click="exportExcel()">
            <PrinterOutlined :style="{ fontSize: '14px' }" />
          </a-button>

          <a-button>
            <FilterFilled :style="{ fontSize: '14px' }" />
          </a-button>
        </div>
        <div style="float: right; position: relative">
          <!--        <label style="font-size: 14px">检索条件：</label>-->
          <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
            <a-select-option style="font-size: 12px;" value="projectCode">项目编码</a-select-option>
            <a-select-option style="font-size: 12px;" value="projectName">项目名称</a-select-option>
            <a-select-option style="font-size: 12px;" value="projectType">类别编码</a-select-option>
            <a-select-option style="font-size: 12px;" value="projectTypeName">类别名称</a-select-option>
            <a-select-option style="font-size: 12px;" value="fangxaing">方向</a-select-option>
          </a-select>
          <!-- 搜索 -->
          <a-input-search
            v-model:value="formItems.selectValue"
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

      <PageWrapper dense content-full-height fixed-height content-class="flex" >
        <div class="bg-white" style="display: inline;float: left;margin-top: 0px;">
          <div class="w-1/4 xl:w-1/5" style="width: 250px;height: 100%;border: 1px #cccccc solid;margin-right: .2%;">
            <DeptTree   @select="handleSelect" v-model="pageParameter" />
          </div>
        </div>

        <div class="temp" style="display: inline;width: calc(100% - 250px);float: right;padding-left: 6px;">
          <BasicTable
                      ref="tableRef"
                      :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys,getCheckboxProps:rowSelection.getCheckboxProps, onChange: onSelectChange }"
                      @selection-change="selectionChange"
                      @row-click="rowClick"
                      :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
                      @register="registerTable"
                      :dataSource="tableData"
                      :scroll="{ x: totalColumnWidth,y: windowHeight }"
          >
            <template #projectName="{ record }" >
              <span style="float: left" >{{ record.projectName }}</span>
            </template>

<!--            <template #projectType="{ record }"> {{ formatProjectType(record.projectType) }} </template>-->
            <template #fangxiang="{ record }"> {{ formatFangxiang(record.fangxiang) }} </template>
            <template #flag="{ record }">
              <span v-if="record.id!=null && record.id!=''">
                <a-tag :color="record.flag === '1' ? 'green' : 'volcano'">
                  {{ record.flag === '1' ? '启用' : '停用' }}
                </a-tag>
              </span>
            </template>

            <template #jcode="{ record }">
              <span style="float: left" >{{ record.jcode }}</span>
            </template>

            <template #dcode="{ record }">
              <span style="float: left" >{{ record.dcode }}</span>
            </template>

            <template #action="{ record }">
              <div>
                <a-popover placement="bottom">
                  <a-button style="padding: 0px 4px; height: 20px;">
                    <CaretDownFilled />
                  </a-button>
                  <template #content>
                    <p style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>
<!--                    <p v-if="record.flag=='0'" @click="editFlagData(record)" style="cursor: pointer"><CheckCircleOutlined /> 启用</p>
                    <p v-if="record.flag=='1'" @click="editFlagData(record)" style="cursor: pointer"><CloseCircleOutlined /> 停用</p>
                    <p style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>-->
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
<script setup="props, {emit}" lang="ts">
import {
  getProjectCashList,
  deleteProjectCash,
  saveProjectCash,
  editFlag,
  excelProjectCash,
  initData,
  isGroup,
  getTotalData,
  getTypeList
} from '/@/api/record/system/project-cash';
import { BasicTable, useTable } from '/@/components/Table'
import DeptTree from './DeptTree.vue'
import Edit from './popup/edit.vue'
import Excel from './popup/excel.vue'
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import { useModal } from '/@/components/Modal'
import {
  CaretDownFilled,
  FormOutlined,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  FilterFilled,SortDescendingOutlined,CheckOutlined,SortAscendingOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  UsbOutlined,PrinterOutlined,UnorderedListOutlined,
  ProfileOutlined
} from '@ant-design/icons-vue'
import {onMounted, reactive, ref} from "vue";
import {
  Select as ASelect,
  Input as AInput,
  Popover as APopover,
  Button as AButton,
  Tag as ATag,
  message
} from 'ant-design-vue'
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {getCurrentAccountName, hasBlank} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {useMessage} from "/@/hooks/web/useMessage";
import {aoaToSheetXlsx} from "/@/components/Excel";
import {findDataBase} from "/@/api/record/system/account";

const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option

const {closeCurrent} = useTabs(router);

const {
  createWarningModal
} = useMessage()

const formItems = ref({
  selectType: 'projectCode',
  selectValue: ''
})

const windowWidth = (document.documentElement.clientWidth - (70 + 280))
const windowHeight = (window.innerHeight - (300))
const totalColumnWidth = ref(0)

const dynamicTenantId = ref(getCurrentAccountName(true))
const CrudApi = {
  list: useRouteApi(getProjectCashList,{schemaName: dynamicTenantId}),
  columns: [
    {
      title: '项目编码',
      dataIndex: 'projectCode',
      width: 100,
      ellipsis: true
    },
    {
      title: '项目名称',
      dataIndex: 'projectName',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      width: 300,
      slots: { customRender: 'projectName' },
      ellipsis: true
    },
    {
      title: '类别编码',
      dataIndex: 'projectType',
      width: 100,
      ellipsis: true,
      // slots: { customRender: 'projectType' }
    },
    {
      title: '类别名称',
      dataIndex: 'projectTypeName',
      width: 200,
      ellipsis: true,
    },
    {
      title: '方向',
      dataIndex: 'fangxiang',
      width: 100,
      ellipsis: true,
      slots: { customRender: 'fangxiang' }
    },
    {
      title: '对应借方科目',
      dataIndex: 'jcode',
      ellipsis: true,
      width: 120,
      slots: { customRender: 'jcode' },
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
    },
    {
      title: '对应贷方科目',
      dataIndex: 'dcode',
      ellipsis: true,
      width: 120,
      slots: { customRender: 'dcode' },
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
    },
    {
      title: '状态',
      dataIndex: 'flag',
      width: 100,
      ellipsis: true,
      slots: { customRender: 'flag' }
    }
  ],
  editData: {
    id: '',
    projectCode: '',
    projectName: '',
    projectType: '',
    projectTypeName: '',
    fangxiang: '',
    flag: '1'
  }
}
/*function formatProjectType(projectType:any) {
  let str = projectType
  switch (projectType) {
    case '1':
      str = '经营活动'
      break
    case '2':
      str = '投资活动'
      break
    case '3':
      str = '筹资活动'
      break
    case '4':
      str = '汇率活动'
      break
    case '5':
      str = '现金及现金等价物'
      break
  }
  return str
}*/
function formatFangxiang(fangxiang:any) {
  let str = fangxiang
  switch (fangxiang) {
    case '1':
      str = '流入'
      break
    case '0':
      str = '流出'
      break
  }
  return str
}
const pageParameter:any = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '湖北万亚软件技术有限公司',
  ifUnit: false,
  total: 0,
  year: '2022',
  thisAdInfo: {}
})

// 这是示例组件
const [registerTable, { reload,setTableData,setColumns,getColumns,getPaginationRef,setPagination,getDataSource,deleteSelectRowByKey }] = useTable({
  // api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
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
  searchInfo: pageParameter,
  /*actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  }*/
})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const [registerExcelPage, { openModal: openExcelPage }] = useModal()
const val = {
  // id: '',
  projectCode: '',
  projectName: '',
  projectType: '',
  projectTypeName: '',
  fangxiang: '',
  flag: '1'
}
const condClick = (data:any,index:any, e:any) => {
  console.log(index)
  if (e.target.cellIndex == 1) {
    openEditPage(true, {
      data: data,
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value
    })
  }
}
const openAddPage = () => {
  openEditPage(true, {
    data: val,
    dynamicTenantId: dynamicTenantId.value,
    defaultAdName: defaultAdName.value,
    year: pageParameter.year,
    showEdit: false,
    isEdit: false
  })
}
const openEdit = (data:any) => {
  openEditPage(true, {
    data: data,
    dynamicTenantId: dynamicTenantId.value,
    defaultAdName: defaultAdName.value,
    year: pageParameter.year,
    accId: defaultAdName.value,
    showEdit: true,
    isEdit: isGroupFlg.value==='1'?false:true
  })
}
const del = async(data:any) => {
  await useRouteApi(deleteProjectCash,{schemaName: dynamicTenantId})(data)
  message.success('删除成功！')
  // alert('删除成功！')
  await reload()
}

async function saveData(data:any) {
  await useRouteApi(saveProjectCash,{schemaName: dynamicTenantId})(data)
  await reload()
}

async function editFlagData(data:any){
  await useRouteApi(editFlag,{schemaName: dynamicTenantId})(data)
  await reload()
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
  if (checkRow.value.length==1) {
    openEditPage(true, {
      data: checkRow.value[0],
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value,
      year: pageParameter.year,
      accId: defaultAdName.value,
      showEdit: true,
      isEdit: isGroupFlg.value==='1'?false:true
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
  if (checkRow.value.length>0) {
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      await useRouteApi(deleteProjectCash,{schemaName: dynamicTenantId})(item)
    }
    checkRow.value = []
    message.success('删除成功！')
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
async function saveExcel(data:any){
  await useRouteApi(excelProjectCash,{schemaName: dynamicTenantId})(data)
  await reload()
}

//文件导出
async function exportExcel() {
  await reloadCurrentPage()
  const arrHeader = CrudApi.columns.map((column) => column.title);
  const arrData = cardList.value.map((item) => getColumns().map((column:any)=> {
    if (item.fangxiang=='1'){
      item.fangxiang='流入'
    } else if (item.fangxiang=='0'){
      item.fangxiang='流出'
    }
    if (item.flag=='1' || item.flag=='启用'){
      item.flag='启用'
    } else {
      item.flag='停用'
    }
    return item[column.dataIndex]
  }));
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: arrData,
    header: arrHeader,
    filename: '现金流量项目.xlsx',
  });
}

const tableData:any = ref([]);
const tableDataAll:any = ref([]);

const showPaginationText = ref(false)
const paginationNumber = ref(0)

const cardList:any = ref([])
async function reloadCurrentPage() {
  showPaginationText.value = false
  let len = 0
  const res:any = await useRouteApi(getProjectCashList,{schemaName: dynamicTenantId})(pageParameter)
  cardList.value = res
  tableDataAll.value = cardList.value
  tableData.value = tableDataAll.value
  let num = 50-(tableData.value.length%50)
  for (let i=0;i<num;i++){
    tableData.value.push({})
  }
  setPagination({total:tableData.value.length})
  len = tableDataAll.value.filter(item=>item.id!=null && item.id!='').length
  paginationNumber.value = len
  showPaginationText.value = true
}
//搜索
async function onSearch(){
  await reloadCurrentPage()
  let a:any = []
  a = cardList.value.filter(item=> {
    //通过项目编码
    if (formItems.value.selectType=='projectCode' && formItems.value.selectValue!=''){
      return item.projectCode.indexOf(formItems.value.selectValue) != -1
    }
    //通过项目名称
    if (formItems.value.selectType=='projectName' && formItems.value.selectValue!=''){
      return item.projectName.indexOf(formItems.value.selectValue) != -1
    }
    //通过类别编码
    if (formItems.value.selectType=='projectType' && formItems.value.selectValue!=''){
      return item.projectType.indexOf(formItems.value.selectValue) != -1
    }
    //通过类别名称
    if (formItems.value.selectType=='projectTypeName' && formItems.value.selectValue!=''){
      return item.projectTypeName.indexOf(formItems.value.selectValue) != -1
    }
    //通过方向过滤
    if (formItems.value.selectType=='fangxiang' && formItems.value.selectValue!=''){
      if (formItems.value.selectValue=='流入'){
        return item.fangxiang=='1'
      }
      if (formItems.value.selectValue=='流出'){
        return item.fangxiang!='1'
      }
      return item.fangxiang.indexOf(formItems.value.selectValue) != -1
    }
    return item
  })
  // console.log(a)
  // setDataSource(a)
  setTableData([])
  setTableData(a)
}

const defaultPage = ref(false)
const totalData = ref('0')
onMounted(async() => {
  // 进页面执行
  getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(res=>{
    if (null != res && res.independent == 0){
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
  })

  totalData.value = await useRouteApi(getTotalData,{schemaName: dynamicTenantId})({})

})
const defaultAdName = ref(useCompanyOperateStoreWidthOut().getSchemaName)
const isGroupFlg = ref('1')
const dynamicAdReload = async (obj) =>{
  const dataBase:any = await findDataBase(obj.accId,obj.year)
  let data:any = {}
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  pageParameter.year = obj.year
  defaultAdName.value = obj.accId
  dynamicTenantId.value = dataBase.accountMode
  await reloadCurrentPage()
  // let res = await useRouteApi(getProjectCashList,{schemaName: dynamicTenantId})(pageParameter)
  // setTableData([]) // 清空可能残留的数据
  // setTableData(res)
  // 底部分页信息
  pageParameter.thisAdInfo = {}
  pageParameter.thisAdInfo = obj
/*  pageParameter.total = res.total
  setPagination({total:res.total})*/

  //查询当前账套是独立状态还是集团账套 然后显示对象数据  分别格式化数据
  isGroupFlg.value = await useRouteApi(isGroup,{schemaName: dynamicTenantId})({
    accId: obj.accId,
    type: obj.target.ztStyle
  })

  totalData.value = await useRouteApi(getTotalData,{schemaName: dynamicTenantId})({})
}

const thisCheckKey = ref('')

function handleSelect(key) {
  if (null != key) {
    thisCheckKey.value = key
    pageParameter.projectType = key
    reload()
  }
}

const {
  createConfirm
} = useMessage()

const init = async() => {
  createConfirm({
    iconType: 'error',
    title: '警告',
    content: '初始化数据会将之前数据删除?',
    onOk: async() => {
      //初始化数据
      await useRouteApi(initData,{schemaName: dynamicTenantId})({
        flgs: isGroupFlg.value,
        accId:defaultAdName.value,
        year: pageParameter.year
      })
      message.success("初始化完成!")
      reload()
    },
    onCancel: () => {
      return false
    }
  })
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

a {
  color: #0096c7;
  text-decoration: none;
  cursor: pointer;
}

.bg-white {
  width: 250px;
  height: calc(100% - 230px);
  border: 1px #cccccc solid;
  background: white;
  margin-top: -0.5%;
  text-align: left;
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
}

.temp{
  position: relative;
  .pagination-text {
    position: absolute;
    bottom: 26px;
    right: 200px;
    font-size: 13px;
    color: black;
    z-index: 99999999;
  }
}

:deep(.ant-table-thead) th {
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
  margin-top: 0px;
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
}

:deep(.vben-basic-table) {
  min-height: 500px;
  height: calc(100% - 160px);
  margin-bottom: 20px;
}

:deep(.ant-input), :deep(.ant-select), :deep(.ant-btn) {
  border: 1px solid #c9c9c9;
}

</style>
