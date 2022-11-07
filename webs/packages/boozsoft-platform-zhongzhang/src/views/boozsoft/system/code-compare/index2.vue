<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title">
          <UnorderedListOutlined
            class="container-head-img"
            style="margin-left: 8px; margin-top: -6px; color: #0096c7"
          />
          <b class="noneSpan">科目对照</b>
        </div>

        <div class="ant-btn-group" data-v-a1ccd506="" style="float: right">
          <a-button class="ant-btn ant-btn-me" @click="openAddPage"><span>新建</span></a-button>
          <a-button class="ant-btn ant-btn-me" @click="editOpen"><span>修改</span></a-button>
          <a-button class="ant-btn ant-btn-me"><span>复制</span></a-button>
          <a-button class="ant-btn ant-btn-me" @click="delList"><span>删除</span></a-button>
          <a-button class="ant-btn ant-btn-me" @click="openExcel()"><span>导入</span></a-button>
          <a-button class="ant-btn ant-btn-me" @click="exportExcel()"><span>导出</span></a-button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/zhongZhang/home/welcome')"><span>退出</span></button>
        </div>
      </div>
      <div class="app-container-neck">
        <accountPicker v-if="!defaultPage" theme="one"  @reloadTable="dynamicAdReload" style="float: left;width: 60%;"/>
        <div style="float: right; margin-left: 10px">
          <a-button style="padding: 0px 12px !important;margin-right: 10px;">
            <SyncOutlined :style="{ fontSize: '14px' }" @click="reload()"/>
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
        </div>
        <div style="float: right; position: relative">
          <a-select v-model:value="formItems.selectType" style="width: 140px;font-size: 12px;" class="special_select">
            <a-select-option style="font-size: 12px;" value="sourceCode">财务会计科目编码</a-select-option>
            <a-select-option style="font-size: 12px;" value="sourceName">财务会计科目名称</a-select-option>
            <a-select-option style="font-size: 12px;" value="targetCode">预算会计科目编码</a-select-option>
            <a-select-option style="font-size: 12px;" value="targetName">预算会计科目名称</a-select-option>
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
      <Edit @save="saveData" @register="registerEditPage" />
      <Excel @save="saveExcel" @register="registerExcelPage" />
    </div>
    <div class="app-container">
      <BasicTable
        :row-selection="{ type: 'checkbox', selectedRowKeys: selectedRowKeys1, onChange: onSelectChange }"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        :rowKey="r=>r.sameSource"
        @register="registerTable">

      </BasicTable>
    </div>
  </div>
</template>
<script setup lang="ts">
import {
  findCodeCompareByIyear,
  deleteCodeCompare,
  saveCodeCompare,
  excelCodeCompare
} from '/@/api/record/system/code-compare'
import { BasicTable, useTable } from '/@/components/Table'
import Edit from './popup/edit.vue'
import Excel from './popup/excel.vue'
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import { useModal } from '/@/components/Modal'
import {
  UnorderedListOutlined,
  FormOutlined,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled,
  SortDescendingOutlined,
  CheckOutlined,
  SortAscendingOutlined
} from '@ant-design/icons-vue'
import {onMounted, reactive, ref} from 'vue'
import {
  Select as ASelect,
  Input as AInput,
  Popover as APopover,
  Button as AButton,
  message
} from 'ant-design-vue'
import {useMessage} from "/@/hooks/web/useMessage";
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {getCurrentAccountName,getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import {findPeriod} from "/@/api/record/generalLedger/data";
// import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
const aoaToSheetXlsx=null
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findAllByAccCountPartColumn, findDataBase} from "/@/api/record/system/account";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {columnProps} from "ant-design-vue/es/table/interface";

const { createConfirm,createWarningModal,createMessage } = useMessage();
const { closeCurrent } =useTabs();
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option
const {
  createSuccessModal,
  createErrorModal
} = useMessage()

const formItems = ref({
  selectType: 'sourceCode',
  selectValue: ''
})

//获取年度信息
const yearList:any = ref([])
const iyear:any = ref('2021')
async function reloadYear(){
  yearList.value = await findPeriod(pageParameter.companyCode)
  if (yearList.value.length>0) {
    iyear.value = yearList.value[0].iyear
    changeYear()
  }
}
//改变年度
function changeYear() {
  reload({
    searchInfo: {
      iyear:iyear.value
    }
  })
}

const columns = [
  {
    title: '财务会计科目编码',
    dataIndex: 'sourceCode',
    ellipsis: true,
    width: "15%",
    customCell: () => {		// 在此处可以修改单元格中的样式
      return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
    },
  },
  {
    title: '财务会计科目名称',
    dataIndex: 'sourceName',
    ellipsis: true,
    customCell: () => {		// 在此处可以修改单元格中的样式
      return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
    },
  },
  {
    title: '预算会计科目编码',
    dataIndex: 'targetCode',
    ellipsis: true,
    width: "15%",
    customCell: () => {		// 在此处可以修改单元格中的样式
      return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
    },
  },
  {
    title: '预算会计科目名称',
    dataIndex: 'targetName',
    ellipsis: true,
    customCell: () => {		// 在此处可以修改单元格中的样式
      return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
    },
  },
]
const dynamicTenantId = ref(getCurrentAccountName(true))
const accId = ref(getCurrentAccountName(false))
// 这是示例组件
const [registerTable, { reload,getDataSource,setTableData,setColumns,getColumns,getPaginationRef,setPagination ,getSelectRows}] = useTable({
  api: useRouteApi(findCodeCompareByIyear,{schemaName: dynamicTenantId}),
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  searchInfo: {
    iyear:iyear.value
  }
})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const [registerExcelPage, { openModal: openExcelPage }] = useModal()
const val = {
  // id: '',
  iyear: '',
  sourceCode: '',
  targetCode: ''
}
const openAddPage = async () => {
  // 判断当前账套是否属于政府会计制度
  let temp=await findAllByAccCountPartColumn(accId.value)
  if(temp.accStyleUnique!=='6'){
    createConfirmPop('当前账套不是政府会计制度,不能进行操作')
    return false;
  }
  let sourceCode:any=[]
  getDataSource().forEach(v=>{
    sourceCode.push(v.sourceCode)
  })
  openEditPage(true, {
    data: val,
    iyear: iyear.value,
    dynamicTenantId: dynamicTenantId.value,
    sourceCode: sourceCode.join(','),
    type:'add',
    accId:accId.value
  })
}

const del = async(data:any) => {
  await useRouteApi(deleteCodeCompare,{schemaName: dynamicTenantId})(data.id)
  // alert('删除成功！')
  createSuccessModal({
    iconType: 'success',
    title: '删除',
    content: '删除成功！'
  })
  reload({
    searchInfo: {
      iyear:iyear.value
    }
  })
}

async function saveData(data:any) {
  await useRouteApi(saveCodeCompare,{schemaName: dynamicTenantId})(data)
  reload({
    searchInfo: {
      iyear:iyear.value
    }
  })
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
const selectedRowKeys1:any = ref([])
const selectedRowData:any = ref([])
const sameSource:any = ref([])
const checkRow:any = ref([])
const onSelectChange = (rowKeys,row) => {
  let b = [...new Set(row.map(item => item.sameSource))]
  sameSource.value=b
  selectedRowData.value=getDataSource().filter(item => b.indexOf(item.sameSource) != -1 )
  selectedRowKeys1.value=selectedRowData.value.map(item => item.sameSource)
  // console.log('selectedRowKeys changed: ', row);
  // state.selectedRowKeys = selectedRowKeys;
  // checkRow.value = row
};

const condClick = (data:any) => {
  openEditPage(true, {
    data: data,
    iyear: iyear.value,
    dynamicTenantId: dynamicTenantId.value
  })
}

const editOpen = async () => {
  // 判断当前账套是否属于政府会计制度
  let temp=await findAllByAccCountPartColumn(accId.value)
  if(temp.accStyleUnique!=='6'){
    createConfirmPop('当前账套不是政府会计制度,不能进行操作')
    return false;
  }
  if (sameSource.value.length==1) {
    let sourceCode:any=[]
    getDataSource().forEach(v=>{
      sourceCode.push(v.sourceCode)
    })
    openEditPage(true, {
      data: selectedRowData.value,
      iyear: iyear.value,
      dynamicTenantId: dynamicTenantId.value,
      sourceCode: sourceCode.join(','),
      type:'edit',
      accId:accId.value
    })
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '编辑',
      content: '请选择一条进行编辑！'
    })
  }
}

async function delList() {
  // 判断当前账套是否属于政府会计制度
  let temp=await findAllByAccCountPartColumn(accId.value)
  if(temp.accStyleUnique!=='6'){
    createConfirmPop('当前账套不是政府会计制度,不能进行操作')
    return false;
  }
  if (sameSource.value.length>0) {
    // for (let i = 0; i < checkRow.value.length; i++) {
    //   const item = checkRow.value[i]
      await useRouteApi(deleteCodeCompare,{schemaName: dynamicTenantId})(sameSource.value.join(','))
    // }
   /* createSuccessModal({
      iconType: 'success',
      title: '删除',
      content: '删除成功！'
    })*/
    message.success('删除成功！')
    reload({
      searchInfo: {
        iyear:iyear.value
      }
    })
    return true
  } else{
    createErrorModal({
      iconType: 'warning',
      title: '删除',
      content: '请选择需要删除的内容！'
    })
    return false
  }
}
//文件导入
const openExcel = async () => {
  // 判断当前账套是否属于政府会计制度
  let temp=await findAllByAccCountPartColumn(accId.value)
  if(temp.accStyleUnique!=='6'){
    createConfirmPop('当前账套不是政府会计制度,不能进行操作')
    return false;
  }
  openExcelPage(true, {
    iyear: iyear.value,
    dynamicTenantId: dynamicTenantId.value
  })
}
async function saveExcel(data){
  await useRouteApi(excelCodeCompare,{schemaName: dynamicTenantId})(data)
  reload({
    searchInfo: {
      iyear:iyear.value
    }
  })
}

//文件导出
async function exportExcel() {
  // debugger
  const arrHeader = ['财务会计科目编码','财务会计科目名称','预算会计科目编码','预算会计科目名称'];
  const columnList = ['sourceCode','sourceName','targetCode','targetName']
  const arrData = getDataSource().map((item) => columnList.map(column=>item[column]));
  // console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: arrData,
    header: arrHeader,
    filename: pageParameter.companyName+'_'+iyear.value+'年_'+'科目对照表.xlsx',
  });
}

const cardList:any = ref([])
async function reloadCurrentPage() {
  const res = await useRouteApi(findCodeCompareByIyear,{schemaName: dynamicTenantId})({iyear: iyear.value})
  cardList.value = res.items
}

function onSearch(){
  reloadCurrentPage()
  let a:any = []
  a = cardList.value.filter(item=> {
    //通过财务会计科目编码
    if (formItems.value.selectType=='sourceCode' && formItems.value.selectValue!=''){
      return item.sourceCode.slice(0, formItems.value.selectValue.length)==formItems.value.selectValue
    }
    //通过财务会计科目名称
    if (formItems.value.selectType=='sourceName' && formItems.value.selectValue!=''){
      return item.sourceName.indexOf(formItems.value.selectValue) != -1
    }
    //预算会计科目编码
    if (formItems.value.selectType=='targetCode' && formItems.value.selectValue!=''){
      return item.targetCode.slice(0, formItems.value.selectValue.length)==formItems.value.selectValue
    }
    //通过预算会计科目名称
    if (formItems.value.selectType=='targetName' && formItems.value.selectValue!=''){
      return item.targetName.indexOf(formItems.value.selectValue) != -1
    }
    return item
  })
  // console.log(a)
  // setDataSource(a)
  setTableData([])
  setTableData(a)
}

const pageParameter:any = reactive({
  showRulesSize: 'MIN',
  companyCode: '102',
  companyName: '公司一',
  ifUnit: false,
})
const defaultPage = ref(false)
onMounted(async() => {
  await reloadYear()
  // 进页面执行
  getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(res=>{
    if (null != res && res.independent == 0){
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
  })
})
const dynamicAdReload = async (obj) =>{
  const dataBase:any = await findDataBase(obj.accId,obj.year)
  let data: any = {}
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  data.iyear = iyear.value
  let res: any = await useRouteApi(findCodeCompareByIyear, {schemaName: dataBase.accountMode})(data)
  setTableData([]) // 清空可能残留的数据
  setTableData(res.items)
  // 底部分页信息
  dynamicTenantId.value = dataBase.accountMode
  accId.value = dataBase.accountId
  pageParameter.thisAdInfo = {}
  pageParameter.thisAdInfo = obj
  pageParameter.total = res.total
  setPagination({total: res.total})
}

function createConfirmPop(text) {
  createConfirm({
    iconType: 'warning',
    title: '警告',
    content: text,
    onOk: async() => {}
  })
}
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" ></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" ></style>
<style src="../../../../assets/styles/global-container-index.less" lang="less" ></style>
<style>
/*分页那栏颜色*/
.ant-table-wrapper{
  padding: 0 !important;
  background: #f2f2f2 !important;
}
</style>
<style scoped lang="less">
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
</style>
