<template>
  <div class="app-container">
    <div class="app-container-top">
        <div class="app-container-head">
          <div class="container-head-title" style="float: none;text-align: center;display: revert;margin-left:0px">
            <b class="noneSpan" >现金流量期初</b>
          </div>

          <div class="ant-btn-group" >
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
              @click="roweditflg=true"
              v-if="!roweditflg && isShow"
            ><span>开始编辑</span></button>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
              @click="roweditflg=false"
              v-if="roweditflg"
            ><span>退出编辑</span></button>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
              @click="openExcel()"
              v-if="isShow"
            ><span>导入</span></button>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
              @click="cleanAll()"
              v-if="isShow"
            ><span>清空</span></button>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              @click="exportExcel()"
              v-if="isShow"
            ><span>导出</span></button>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              @click="openPrint()"
              v-if="isShow"
            ><span>打印</span></button>
            <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/one/home/welcome')"><span>退出</span></button>
          </div>
          <div style="display: inline-block;font-size: 14px;float: none;text-align: center;display: revert;padding-top: 20px;padding-left: 3%;">
            &emsp;&emsp;<span style="font-size: 14px;">启用期间：</span><span style="color: black;font-weight: bold">{{fistQj}} </span>
          </div>
          <div style="clear:both" />

          <div style="margin-top: .1%;">
            <div style="display: inline-block;font-size: 14px;width: 50%">
              <AccountPicker theme="one"  @reloadTable="dynamicAdReload"/>
            </div>
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

            </div>
            <div style="float: right; position: relative">
              <!--        <label style="font-size: 14px">检索条件：</label>-->
              <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
                <a-select-option style="font-size: 12px;" value="projectCode">项目编码</a-select-option>
                <a-select-option style="font-size: 12px;" value="projectName">项目名称</a-select-option>
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
        </div>
      </div>

    <div class="app-container-bottom" style="padding-top: 5px;">
      <Edit
        @save="saveData"
        @register="registerEditPage"
      />
      <Excel
        @save="saveExcel"
        @register="registerExcelPage"
      />
      <Print @save="loadPrint" @register="registerPrintPage"/>
      <PageWrapper dense content-full-height fixed-height content-class="flex" >
        <div class="bg-white" style="display: inline;float: left;margin-top: 0px;">
          <div class="w-1/4 xl:w-1/5" style="width: 250px;height: 100%;border: 1px #cccccc solid;margin-right: .2%;">
            <DeptTree   @select="handleSelect" v-model="pageParameter" />
          </div>
        </div>

        <div style="display: inline;width: calc(100% - 250px);float: right;padding-left: 6px;">
          <BasicTable
                      ref="tableRef"
                      :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
                      :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
                      @row-click="condClick"
                      @register="registerTable"
          >
            <template #projectName="{ record }" >
              <span style="float: left" >{{ record.projectName }}</span>
            </template>

            <template #projectType="{ record }"> {{ formatProjectType(record.projectType) }} </template>
            <template #fangxiang="{ record }"> {{ formatFangxiang(record.fangxiang) }} </template>

            <template #beiyong1="{ record }">
              <template
                v-if="roweditflg ">
                <template v-if="record?.beiyong2">
                  <a-input-number v-model:value="record.beiyong1"
                                  :formatter="value =>`${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                                  :parser="value =>`${value}`.replace(/\$\s?|(,*)/g,'')"
                                  :precision="2"
                                  style="width: 90%;margin: -5px 0;height: 25px;line-height: 25px;"
                                  @keyup.enter="record.beiyong2 = null;dbSave(record)"/>
                  <CheckOutlined @click="record.beiyong2 = null;dbSave(record);"/>
                </template>
                <template v-else>
                  <span class="a-table-font-arial" :style="(record.beiyong1 < 0 )?{color: 'red'}:{}"> {{
                      record.beiyong1 == 0 ? '' : money(record.beiyong1)
                    }}
                     <EditOutlined @click="record.beiyong2 = true;record.beiyong1=record.beiyong1"/></span>
                </template>
              </template>
              <template v-else>
                <span class="a-table-font-arial" :style="(record.beiyong1 < 0 )?{color: 'red'}:{}">{{
                    record.beiyong1 == 0 ? '' : money(record.beiyong1)
                  }}</span>
              </template>
            </template>

          </BasicTable>
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
  saveMoney,
  delAll, excelProjectCashQc
} from '/@/api/record/system/project-cash';
import { BasicTable, useTable } from '/@/components/Table'
import DeptTree from './DeptTree.vue'
import Edit from './popup/edit.vue'
import Excel from './popup/excel.vue'
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import { useModal } from '/@/components/Modal'
import {
  EditOutlined,
  CaretDownOutlined,
  CheckOutlined,
  LinkOutlined,
  ExclamationCircleOutlined,
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
  SortAscendingOutlined,
  SearchOutlined,
  PrinterOutlined,
  UsbOutlined
} from '@ant-design/icons-vue';
import {onMounted, reactive, ref} from "vue";
import {
  Select as ASelect,
  Input as AInput,InputNumber as AInputNumber,
  Popover as APopover,
  Button as AButton,
  Tag as ATag,
  message
} from 'ant-design-vue'
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {useMessage} from "/@/hooks/web/useMessage";
import {aoaToSheetXlsx} from "/@/components/Excel";
import {findDataBase} from "/@/api/record/system/account";
import {useNewPrint} from "/@/utils/boozsoft/print/print";
import {tableStyle} from "/@/store/modules/abc-print";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import Print from "./popup/print.vue";
import {findPeriodByAccontId} from "/@/api/record/generalLedger/data";

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
// 单元格可编状态
const roweditflg = ref(false);
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
      title: '方向',
      dataIndex: 'fangxiang',
      width: 100,
      ellipsis: true,
      slots: { customRender: 'fangxiang' }
    },
    {
      title: '金额',
      dataIndex: 'beiyong1',
      width: 100,
      ellipsis: true,
      slots: { customRender: 'beiyong1' }
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
function formatProjectType(projectType:any) {
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
}
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
const [registerTable, { reload,setTableData,setColumns,getColumns,getPaginationRef,setPagination,getDataSource }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  pagination: {
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['10', '30', '50', '100'],
    showTotal: t => `总共${t}条数据`
  },
  searchInfo: pageParameter,
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
const checkRow:any = ref([])
const onSelectChange = (selectedRowKeys,row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
};
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
  await useRouteApi(excelProjectCashQc,{schemaName: dynamicTenantId})(data)
  await reload()
}

//文件导出
async function exportExcel() {
  const arrHeader = CrudApi.columns.map((column) => column.title);
  const arrData = getDataSource().map((item) => getColumns().map((column:any)=> {
    item.fangxiang = formatFangxiang(item.fangxiang)
    item.beiyong1 = money(item.beiyong1)
    return item[column.dataIndex]
  }));
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: arrData,
    header: arrHeader,
    filename: '现金流量期初.xlsx',
  });
}

const cardList:any = ref([])
async function reloadCurrentPage() {
  reload()
}
//搜索
async function onSearch(){
  console.log(formItems.value.selectValue)
  if(formItems.value.selectValue.length > 0){
    let a:any = []
    a = getDataSource().filter(item=> {
      //通过项目编码
      if (formItems.value.selectType=='projectCode' && formItems.value.selectValue!=''){
        return item.projectCode.indexOf(formItems.value.selectValue) != -1
      }
      //通过项目名称
      if (formItems.value.selectType=='projectName' && formItems.value.selectValue!=''){
        return item.projectName.indexOf(formItems.value.selectValue) != -1
      }
      return item
    })
    setTableData([])
    setTableData(a)
  }else{
    reload()
  }
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
const isShow = ref(false)
const fistQj = ref('')
const dynamicAdReload = async (obj) =>{
  const dataBase:any = await findDataBase(obj.accId,obj.year)
  let data:any = {}
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  pageParameter.year = obj.year
  defaultAdName.value = obj.accId
  dynamicTenantId.value = dataBase.accountMode
  let res = await useRouteApi(getProjectCashList,{schemaName: dynamicTenantId})(pageParameter)
  setTableData([]) // 清空可能残留的数据
  setTableData(res)
  // 底部分页信息
  pageParameter.thisAdInfo = {}
  pageParameter.thisAdInfo = obj
/*  pageParameter.total = res.total
  setPagination({total:res.total})*/

  //查询当前账套启用期间  如果没有中途启用则提示隐藏按钮
  let arr = await findPeriodByAccontId(obj.accId);
  let first = arr[0]
  fistQj.value = first.value
  if('01' === first.iperiodNum){
    message.error('当前账套无现金流量期初！')
  }else{
    isShow.value = true
  }
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
function money(val: any) {
  if (val == null) val = '';
  val = val.toString().replace(/\$|\,\-/g, '');
  if (isNaN(val)) {
    val = '0';
  }
  let fs = val.indexOf('-') != -1
  const sign = val === (val = Math.abs(val));
  val = Math.floor(val * 100 + 0.50000000001);
  let cents = val % 100;
  val = Math.floor(val / 100).toString();
  if (cents < 10) {
    cents = '0' + cents;
  }
  for (let i = 0; i < Math.floor((val.length - (1 + i)) / 3); i++) {
    val =
      val.substring(0, val.length - (4 * i + 3)) + ',' + val.substring(val.length - (4 * i + 3));
  }
  return (sign ? '' : '') + (fs ? '-' : '') + val + '.' + cents;
}

const dbSave = async (o) => {
  await useRouteApi(saveMoney,{schemaName: dynamicTenantId})(o)
  message.info("同步成功")
}

const cleanAll = async (o) => {
  createConfirm({
    iconType: 'error',
    title: '警告',
    content: '清空数据会将之前数据全部删除?',
    onOk: async() => {
      //初始化数据
      await useRouteApi(delAll,{schemaName: dynamicTenantId})({})
      message.success("清空成功!")
      reload()
    },
    onCancel: () => {
      return false
    }
  })
}

const [registerPrintPage, {openModal: openPrintPage}] = useModal()

const openPrint = () => {
  openPrintPage(true, {
    data: {
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: useCompanyOperateStoreWidthOut().getSchemaName,
      year: '',
    }
  })
}
const loadingRef = ref(false);
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
const userName = useUserStoreWidthOut().getUserInfo.username
const loadMark = ref(false)
const loadPrint = (obj) => {
  openCompFullLoading()
  const data = JSON.parse(JSON.stringify(getDataSource()))
  let printList: any = []
  let printUser = ''
  if(obj.printUser){
    printUser = '制表人：' + userName
  }
  let printBz = ''

  let printKm = '科目: '+pageParameter.km + '-现金流量期初'
  let title = '现金流量期初'
  //根据当前样式调整
  ///项目编码  项目名称 方向  金额
  //格式化数据
  data.forEach((item,index) => {
    let item1 = {}
    item1[0] = item.projectCode
    item1[1] = item.projectName
    item1[2] = formatFangxiang(item.fangxiang)
    item1[3] = money(item.beiyong1)
    printList.push(item1)
  })
  for (let i=0; i<printList.length%38; i++){
    let item1 = {}
    item1[0] = ''
    item1[1] = ''
    item1[2] = ''
    item1[3] = ''
    printList.push(item1)
  }
  let num = Math.ceil(printList.length/38)
  useNewPrint({data: ['p', 'px', 'a4', true]}, (doc) => {
    loadMark.value = false
    doc.autoTable({
      head: [['', title,'' , '',],
        ['',  '期间:'+ '2022-05', '', ''],
        ['项目编码', '项目名称', '方向', '金额']],
      body: printList,
      // startY: 60,
      styles: tableStyle(),
      margin: {
        left: 50,
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
        doc.autoTableText(
          '核算单位：' + '',
          tabMarginLeft,
          data.cursor.y + 3,
          0
        );
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
        // data.cell.styles.fontStyle = 'bold'
        if (data.section == 'head' && data.row.index == 0) {
          data.cell.styles.fontSize = 20
          data.cell.styles.fontStyle = 'bold'
          data.cell.styles.lineColor = [255, 255, 255]
          if (data.column.index == 1) {
            data.cell.colSpan = 2
            data.cell.styles.halign = 'center'
          }
        }
        if (data.section == 'head' && data.row.index == 1) {
          data.cell.styles.fontSize = 10
          data.cell.styles.fontStyle = 'bold'
          data.cell.styles.lineColor = [255, 255, 255]
          if (data.column.index == 0) {

          } else if (data.column.index == 1) {
            data.cell.colSpan = 2
            data.cell.styles.halign = 'center'
          }  else {
            data.cell.styles.halign = 'right'
          }
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
        0: {maxHeight: 10,cellWidth: 60, halign: 'center'},
        1: {cellWidth: 150, halign: 'left'},
        2: {cellWidth: 60, halign: 'center'},
        3: {cellWidth: 80, halign: 'right'},
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

function formatData(data:any){
  let str = ""
  console.log(data)
  if(data){
    // 千分位保留2位小数
    var source = String(parseInt(data).toFixed(2)).split("."); //按小数点分成2部分
    source[0] = source[0].replace( new RegExp('(\\d)(?=(\\d{3})+$)' , 'ig'), "$1,");//只将整数部分进行都好分割
    str = source.join("." );//再将小数部分合并进来
  }
  return str;
}
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
<style scoped>
.app-container-bottom {
:deep(.ant-table-thead th) {
  background-color: #d8d8d8 !important;
  font-weight: bold !important;
  border-left: 1px solid #d8d8d8 !important;
  border-bottom: 1px solid #d8d8d8 !important;
  border-top: 1px solid #d8d8d8 !important;
}
}
</style>
