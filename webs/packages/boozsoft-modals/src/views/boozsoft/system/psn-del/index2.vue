<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img">-->
        <div class="container-head-title" style="margin-left: 20px;">
          <b class="noneSpan"><DeleteOutlined style="font-size: 26px;" />&nbsp;&nbsp;人员信息回收站</b>
        </div>
        <div class="ant-btn-group" style="float: right">
<!--          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="openAddPage()"
          ><span>新增</span></button>-->
<!--          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="editOpen()"
          ><span>修改</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="editOpenBatch()"
          ><span>批量修改</span></button>-->
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="recoverList()"
          ><span>还原</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="delList()"
          ><span>彻底删除</span></button>
<!--          <button
            type="button"
            v-if="!defaultPage"
            class="ant-btn ant-btn-me"
            @click="openExcel()"
          ><span>导入</span></button>-->
          <button
            type="button"
            class="ant-btn ant-btn-me"
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
      <div class="app-container-neck">
        <!--        <UnitChange v-if="!defaultPage" @reloadTable="dynamicAdReload" v-model="pageParameter"/>-->
        <div style="display: inline-block;float: left;font-size: 14px;">
          <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="float: left; margin-left: 10px;line-height: 30px;">共 {{psnList.length}} 条</div>
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
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
        </div>
        <div style="float: right; position: relative">
          <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
            <a-select-option style="font-size: 12px;" value="psnCode">人员编码</a-select-option>
            <a-select-option style="font-size: 12px;" value="psnName">人员姓名</a-select-option>
            <a-select-option style="font-size: 12px;" value="psnSex">性别</a-select-option>
            <a-select-option style="font-size: 12px;" value="documentType">证件类型</a-select-option>
            <a-select-option style="font-size: 12px;" value="documentCode">证件号码</a-select-option>
            <a-select-option style="font-size: 12px;" value="cellPhoneNum">手机</a-select-option>
            <a-select-option style="font-size: 12px;" value="psnEmail">Email</a-select-option>
            <a-select-option style="font-size: 12px;" value="delName">删除操作员</a-select-option>
            <a-select-option style="font-size: 12px;" value="delDate">删除时间</a-select-option>
          </a-select>
          <a-input-search
            placeholder=""
            style="width: 200px; border-radius: 4px"
            v-model:value="formItems.selectValue"
            @search="onSearch"
          />
        </div>
      </div>
    </div>
    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
          <BasicTable
            ref="tableRef"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
            :dataSource="tableData"
            @row-click="condClick"
            @register="registerTable"
            :loading="loadMark"
          >
            <template #psnSex="{ record }"> {{ formatPsnSex(record.psnSex) }}</template>
            <template #psnType="{ record }"> {{ formatPsnType(record.psnType) }}</template>
            <template #documentType="{ record }">
              <span v-if="record.documentType=='1'">居民身份证</span>
              <span v-if="record.documentType=='2'">港澳居民来往内地通行证</span>
              <span v-if="record.documentType=='3'">台湾居民来往大陆通行证</span>
              <span v-if="record.documentType=='4'">中国护照</span>
              <span v-if="record.documentType=='5'">外国护照</span>
            </template>
            <template #cellPhoneNum="{ record }">
              <span v-if="record.cellPhoneNum!=null&&record.cellPhoneNum!=''">{{plusStr(record.cellPhoneNum,3,4,'****')}}</span>
            </template>
            <template #psnEmail="{ record }">
              <span v-if="record.psnEmail!=null&&record.psnEmail!=''">{{plusStr(record.psnEmail,3,4,'****@***')}}</span>
            </template>
            <template #documentCode="{ record }">
              <span v-if="record.documentCode!=null&&record.documentCode!=''">{{plusStr(record.documentCode,3,4,'****')}}</span>
            </template>
            <template #bankAccount="{ record }">
              <span v-if="record.bankAccount!=null&&record.bankAccount!=''">{{plusStr(record.bankAccount,3,4,'****')}}</span>
            </template>

          </BasicTable>
      </PageWrapper>
    </div>
  </div>
</template>
<script setup lang="ts">
import {BasicTable, useTable} from '/@/components/Table'
import {PageWrapper} from '/@/components/Page'
import {useRouteApi} from '/@/utils/boozsoft/datasource/datasourceUtil';
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
  PieChartFilled,ReadOutlined,SafetyOutlined,RestOutlined,
  FilterFilled, CheckOutlined, SortDescendingOutlined, SortAscendingOutlined, EditOutlined,
  TeamOutlined
} from '@ant-design/icons-vue'
import {onMounted, reactive, ref} from 'vue'
import {
  deletePsn,
  savePsn,
  editFlag,
  excelPsn,
  getPsnList,
  findPsnDelList
} from '/@/api/record/system/psn'
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
import {useMessage} from "/@/hooks/web/useMessage";
import {
  getThisAdInfoData,
} from "/@/api/record/system/financial-settings";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";

const {closeCurrent} = useTabs(router);

const formItems = ref({
  selectType: 'psnCode',
  selectValue: ''
})

const {
  createConfirm,
  createWarningModal
} = useMessage()

const flag = ref('1')
const tableData:any = ref([]);
const tableDataAll:any = ref([]);
const CrudApi = {
  list: [],
  columns: [
    {
      title: '人员编码',
      dataIndex: 'psnCode',
      width: 120,
      ellipsis: true,
      fixed: 'left',
      slots: {customRender: 'psnCode'}
    },
    {
      title: '人员姓名',
      dataIndex: 'psnName',
      width: 120,
      ellipsis: true,
      fixed: 'left',
      slots: {customRender: 'psnName'}
    },
    {
      title: '性别',
      dataIndex: 'psnSex',
      width: 100,
      ellipsis: true,
      slots: {customRender: 'psnSex'}
    },
    {
      title: '人员属性',
      dataIndex: 'psnType',
      ellipsis: true,
      width: 150,
      slots: {customRender: 'psnType'}
    },
    {
      title: '手机号',
      dataIndex: 'cellPhoneNum',
      width: 150,
      ellipsis: true,
      slots: {customRender: 'cellPhoneNum'}
    },
    {
      title: '邮箱',
      dataIndex: 'psnEmail',
      width: 150,
      ellipsis: true,
      slots: {customRender: 'psnEmail'}
    },
    {
      title: '证件类型',
      dataIndex: 'documentType',
      width: 150,
      ellipsis: true,
      slots: {customRender: 'documentType'}
    },
    {
      title: '证件号码',
      dataIndex: 'documentCode',
      width: 200,
      ellipsis: true,
      slots: {customRender: 'documentCode'}
    },
    {
      title: '入职日期',
      dataIndex: 'entryDate',
      width: 120,
      ellipsis: true
    },
    /*{
      title: '离职日期',
      dataIndex: 'leaveDate',
      width: 120,
      ellipsis: true
    },*/
    {
      title: '删除操作员',
      dataIndex: 'delName',
      width: 120,
      ellipsis: true
    },
    {
      title: '删除时间',
      dataIndex: 'delDate',
      width: 150,
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
  let str = '内部人员'
  switch (type) {
    case '1':
      str = '内部人员'
      break
    case '2':
      str = '外部人员'
      break
  }
  return str
}

// 这是示例组件
const dynamicTenantId = ref(getCurrentAccountName(true))
const [registerTable, {
  reload,
  setTableData,
  setColumns,
  getColumns,
  getPaginationRef,
  setPagination
}] = useTable({
  /*api: async (params) => {
    // debugger
    // console.log(thisCheckKey.value)
    params.flag = flag.value
    if (typeFlag.value =='1'){
      params.uniqueCodeDept = thisCheckKey.value
      params.uniquePsnType = null
    } else {
      params.uniqueCodeDept = null
      params.uniquePsnType = thisCheckKey.value
    }
    return await useRouteApi(psnFindAll, {schemaName: dynamicTenantId})(params)
  },*/
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  pagination: {
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['10', '25', '50', '100'],
    showTotal: t => `总共${t}条数据`
  },
  /*actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  }*/
})

const val: any = {
  id: null,
  uniqueCode: '',
  psnName: '',
  psnSex: '0',
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
  entryDate: null,
  psnBank: '',
  bankArea: '',
  bankAccount: '',
  bankNum: '',
  flag: '1'
}
const defaultPage = ref(true)
onMounted(async () => {
  // 进页面执行
  getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(res => {
    if (null != res /*&& res.independent == 0*/) {
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn == '' ? res.accName : res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
  })
})

const del = async (data: any) => {
  createConfirm({
    iconType: 'error',
    title: '警告',
    content: '删除后数据将不能恢复，你确认要删除吗?',
    onOk: async () => {
      await useRouteApi(deletePsn, {schemaName: dynamicTenantId})(data)
      // alert('删除成功！')
      message.success('删除成功！')
      await reloadTable()
      checkRow.value = []
    },
    onCancel: () => {
      return false
    }
  })

}

async function onSearch() {
  loadMark.value = true
  tableData.value = tableDataAll.value.filter(item => {
    //通过人员编码过滤
    if (formItems.value.selectType == 'psnCode' && formItems.value.selectValue != '') {
      return item.psnCode.indexOf(formItems.value.selectValue) != -1
    }
    //通过人员名称过滤
    if (formItems.value.selectType == 'psnName' && formItems.value.selectValue != '') {
      return item.psnName.indexOf(formItems.value.selectValue) != -1
    }
    //通过性别过滤
    if (formItems.value.selectType == 'psnSex' && formItems.value.selectValue != '') {
      if ('未知的性别'.indexOf(formItems.value.selectValue)!=-1) {
        return item.psnSex == '0' || item.psnSex.indexOf(formItems.value.selectValue) != -1
      }
      if (formItems.value.selectValue == '男') {
        return item.psnSex == '1' || item.psnSex.indexOf(formItems.value.selectValue) != -1
      }
      if (formItems.value.selectValue == '女') {
        return item.psnSex == '2' || item.psnSex.indexOf(formItems.value.selectValue) != -1
      }
      if ('未说明的性别'.indexOf(formItems.value.selectValue)!=-1) {
        return item.psnSex == '9' || item.psnSex.indexOf(formItems.value.selectValue) != -1
      }
      return item.psnSex.indexOf(formItems.value.selectValue) != -1
    }
    //通过证件类型过滤
    if (formItems.value.selectType == 'documentType' && formItems.value.selectValue != '') {
      if ('居民身份证'.indexOf(formItems.value.selectValue)!=-1) {
        return item.documentType == '1' || item.documentType.indexOf(formItems.value.selectValue) != -1
      }
      if ('港澳居民来往内地通行证'.indexOf(formItems.value.selectValue)!=-1) {
        return item.documentType == '2' || item.documentType.indexOf(formItems.value.selectValue) != -1
      }
      if ('台湾居民来往大陆通行证'.indexOf(formItems.value.selectValue)!=-1) {
        return item.documentType == '3' || item.documentType.indexOf(formItems.value.selectValue) != -1
      }
      if ('中国护照'.indexOf(formItems.value.selectValue)!=-1) {
        return item.documentType == '4' || item.documentType.indexOf(formItems.value.selectValue) != -1
      }
      if ('外国护照'.indexOf(formItems.value.selectValue)!=-1) {
        return item.documentType == '5' || item.documentType.indexOf(formItems.value.selectValue) != -1
      }
      return item.documentType.indexOf(formItems.value.selectValue) != -1
    }
    //通过国家过滤
    if (formItems.value.selectType == 'countryId' && formItems.value.selectValue != '') {
      return item.countryId.indexOf(formItems.value.selectValue) != -1
    }
    //通过地址过滤
    if (formItems.value.selectType == 'district' && formItems.value.selectValue != '') {
      return item.countryId.indexOf(formItems.value.selectValue) != -1
    }
    //通过手机过滤
    if (formItems.value.selectType == 'cellPhoneNum' && formItems.value.selectValue != '') {
      return item.cellPhoneNum.indexOf(formItems.value.selectValue) != -1
    }
    //通过Email过滤
    if (formItems.value.selectType == 'psnEmail' && formItems.value.selectValue != '') {
      return item.psnEmail.indexOf(formItems.value.selectValue) != -1
    }
    //通过钉钉过滤
    if (formItems.value.selectType == 'psnQq' && formItems.value.selectValue != '') {
      return item.psnQq.indexOf(formItems.value.selectValue) != -1
    }
    //通过微信过滤
    if (formItems.value.selectType == 'psnWechat' && formItems.value.selectValue != '') {
      return item.psnWechat.indexOf(formItems.value.selectValue) != -1
    }
    //通过证件号码过滤
    if (formItems.value.selectType == 'documentCode' && formItems.value.selectValue != '') {
      return item.documentCode.indexOf(formItems.value.selectValue) != -1
    }
    //通过删除操作员过滤
    if (formItems.value.selectType == 'delName' && formItems.value.selectValue != '') {
      return item.delName.indexOf(formItems.value.selectValue) != -1
    }
    //通过删除时间过滤
    if (formItems.value.selectType == 'delDate' && formItems.value.selectValue != '') {
      return item.delDate.indexOf(formItems.value.selectValue) != -1
    }
    return item
  })
  await setPagination({
    total: tableData.value.length
  })
  loadMark.value = false
}

function filterSearch() {
}

function condClick() {
}

const psnList:any = ref([])
async function reloadPsn(){
  loadMark.value = true
  const res = await useRouteApi(findPsnDelList,{schemaName: dynamicTenantId})({})
  psnList.value = res
  tableDataAll.value = res
  tableData.value = tableDataAll.value
  setPagination({total:tableData.value.length})
  loadMark.value = false
}
async function reloadTable() {
  await reloadPsn()
  /*if (typeFlag.value=='1') {
    reload({
      searchInfo: {
        uniqueCodeDept: thisCheckKey.value,
        flag: flag.value
      }
    })
  } else {
    reload({
      searchInfo: {
        uniquePsnType: thisCheckKey.value,
        flag: flag.value
      }
    })
  }*/
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
const onSelectChange = (selectedRowKeys, row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
};

async function recoverList() {
  if (checkRow.value.length > 0) {
    createConfirm({
      iconType: 'warning',
      title: '提示',
      content: '你确认要还原吗?',
      onOk: async () => {
        for (let i = 0; i < checkRow.value.length; i++) {
          const item = checkRow.value[i]
          item.isDel = "0"
          item.delName = null
          item.delDate = null
          await useRouteApi(savePsn, {schemaName: dynamicTenantId})(item)
          // await useRouteApi(deletePsn, {schemaName: dynamicTenantId})(item)
        }
        checkRow.value = []
        message.success('恢复成功！')
        await reloadTable()
      },
      onCancel: () => {
        return false
      }
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '恢复',
      content: '请选择需要还原的内容！'
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
          // await useRouteApi(savePsn, {schemaName: dynamicTenantId})(item)
          await useRouteApi(deletePsn, {schemaName: dynamicTenantId})(item)
        }
        checkRow.value = []
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

const pageParameter = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '湖北万亚软件技术有限公司',
  ifUnit: false,
  total: 0,
  thisAdInfo: {}
})

const loadMark = ref(false)
const dynamicAdReload = async (obj) => {
  // const dataBase: any = await findDataBase(obj.accId, obj.year)
  dynamicTenantId.value = obj.accountMode
  await reloadPsn()
  // 先获取部门组件查看是否存在部门信息
  let data: any = {}
  //data.uniqueCodeDept = thisCheckKey.value
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  data.flag = flag.value
  // let res = await useRouteApi(psnFindAll, {schemaName: dynamicTenantId})(data)
  let res = tableData.value
  // if (res != null && res.total> 0){
  loadMark.value = true
  setTableData([]) // 清空可能残留的数据
  setTableData(res)
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

/* 部分隐藏处理
** str 需要处理的字符串
** frontLen  保留的前几位
** endLen  保留的后几位
** cha  替换的字符串
*/
function plusXing(str, frontLen, endLen,cha) {
  let len = str.length - frontLen - endLen;
  let xing = '';
  for (let i = 0; i < len; i++) {
    xing += cha;
  }
  return str.substring(0, frontLen) + xing + str.substring(str.length - endLen);
}
function plusStr(str, frontLen, endLen,cha) {
  return str.substring(0, frontLen) + cha + str.substring(str.length - endLen);
}
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
  padding: 4px 8px !important;
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

:deep(.ant-table-thead) th{
  text-align: center !important;
  font-weight: bold;
  background-color: #f2f2f2 !important;
  border-color: #e5e7eb !important;
}

</style>
