<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">
        <div class="container-head-title">
          <b class="noneSpan">卡片栏目设置</b>
        </div>
        <div class="ant-btn-group" style="float: right">

          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="editOpen()"
          ><span>修改</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
          ><span>退出</span></button>
        </div>
      </div>
      <div class="app-container-neck">
        <div style="display: inline-block;float: left;font-size: 14px;width: 50%;">
          <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
        </div>
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

            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>

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
          <a-input-search
            placeholder=""
            style="width: 200px; border-radius: 4px"
            @search="onSearch"
          />
        </div>
      </div>
    </div>
    <div class="app-container">
      <EditPage @save="saveData" @register="registerEditPage"/>
      <AddPage @save="saveData" @register="registerSavePage"/>
      <Excel @save="saveExcel" @register="registerExcelPage"/>
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <div v-if="!isShowTree"
             style="width: 250px;min-height: 250px;height: calc(100% - 170px);border: 1px #cccccc solid;background:white !important;margin-right: .2%;"
             ></div>
        <DeptTree v-if="isShowTree"  @select="handleSelect"
                  v-model="pageParameter"/>

        <div class="bg-white" style="width: calc(100% - 650px); float: right;margin-left: 5px;margin-top: -0.5px;">
          <BasicTable
                      ref="tableRef"
                      :loading="loadMark"
                      :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
                      :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
                      :scroll="{ x: totalColumnWidth,y: windowHeight }"
                      @row-click="condClick"
                      @register="registerTable">

            <template #columnName="{ record }" class="a-table-money-font-size a-table-font-arial">
              <span style="float: left" >{{ record.columnName }}</span>
            </template>

            <template #showName="{ record }" class="a-table-money-font-size a-table-font-arial">
              <span style="float: left" >{{ record.showName }}</span>
            </template>

            <template #dataType="{ record }">
              <span v-if="record.dataType === '0'">
                   文本
              </span>
              <span v-else-if="record.dataType === '1'">
                   整数
              </span>
              <span v-else-if="record.dataType === '2'">
                   数值
              </span>
              <span v-else>
                   日期
              </span>
            </template>
<!--                <template #showName="{ record }"> <template v-if="record.mdEdit == null">-->
<!--                  <span class="a-table-font-arial" :style="(record.showName < 0 )?{color: 'red'}:{}"> {{-->
<!--                      record.showName == 0 ? '' : record.showName-->
<!--                    }}-->
<!--                    <EditOutlined @click="record.mdEdit = true;record.oldMd=record.showName"/></span>-->
<!--                </template>-->
<!--                <template v-else>-->
<!--                  <a-input v-model:value="record.showName"-->
<!--                           :parser="value =>`${value}`.replace(/\$\s?|(,*)/g,'')"-->
<!--                           :precision="2"-->
<!--                           style="width: 90%;margin: -5px 0;height: 25px;line-height: 25px;"-->
<!--                           @keyup.enter="record.mdEdit = null;dbSave(record)"/>-->
<!--                  <CheckOutlined @click="record.mdEdit = null;dbSave(record);"/>-->
<!--                </template></template>-->

            <template #status="{ record }">
              <span>
                <a-tag :color="record.status === '1' ? 'green' : 'volcano'">
                  {{ record.status === '1' ? '启用' : '停用' }}
                </a-tag>
              </span>
            </template>
            <template #flag="{ record }">
              <span>
                <a-tag :color="record.flag === '1' ? 'green' : 'volcano'">
                  {{ record.flag === '1' ? '是' : '否' }}
                </a-tag>
              </span>
            </template>

            <template #action="{ record }">
              <div>
                <a-popover placement="bottom">
                  <a-button style="padding: 0px 4px; height: 20px;">
                    <CaretDownFilled/>
                  </a-button>
                  <template #content>
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
        </div>
      </PageWrapper>
    </div>
  </div>
</template>
<script setup lang="ts">
import {
  deleteDept,
  saveDept,
  editFlag,
  excelDept,
  GetfaCardColumnAll,
  SavefaCardColumn, DeletefaCardColumnBy
} from '/@/api/record/gdzc/fa-card-column'
import {BasicTable, useTable} from '/@/components/Table'
import EditPage from './popup/edit.vue'
import AddPage from './popup/save.vue'
import DeptTree from './DeptTree.vue'
import Excel from './popup/excel.vue'
import {useModal} from '/@/components/Modal'
import {PageWrapper} from '/@/components/Page'

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
  ReadOutlined, SafetyOutlined, RestOutlined
} from '@ant-design/icons-vue'
import {
  Input as AInput,
  Select as ASelect,
  Popover as APopover,
  Tag as ATag,
  message
} from "ant-design-vue";
import {provide, reactive, ref} from "vue";

const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search

const {
  createConfirm
} = useMessage()

const flag = ref('1')

const columns: any = [
  {
    title: 'ID',
    dataIndex: 'id',
    defaultHidden: true,
    width: '3%',
    ellipsis: true
  },
  {
    title: '栏目名称',
    dataIndex: 'columnName',
    width: '15%',
    ellipsis: true,
    customHeaderCell: () => {		// 表头加粗
      return {style: {'font-weight':'bold'}} // return 想要设置的样式
    },
    customCell: () => {		// 在此处可以修改单元格中的样式
      return {style: {'text-align': 'center', '-webkit-line-clamp': 2}} // return 想要设置的样式
    },
    slots: {customRender: 'columnName'},
  },
  {
    title: '显示名称',
    dataIndex: 'showName',
    ellipsis: true,
    width: '15%',
    customHeaderCell: () => {		// 表头加粗
      return {style: {'font-weight':'bold'}} // return 想要设置的样式
    },
    slots: {customRender: 'showName'},

  },
  {
    title: '数据类型',//（1是，0否）
    dataIndex: 'dataType',
    ellipsis: true,
    width: '5%',
    customHeaderCell: () => {		// 表头加粗
      return {style: {'font-weight':'bold'}} // return 想要设置的样式
    },
    slots: {customRender: 'dataType'},
    customCell: () => {		// 在此处可以修改单元格中的样式
      return {style: {'text-align': 'center', '-webkit-line-clamp': 2}} // return 想要设置的样式
    },
  },
  {
    title: '字符长度',//(1.是;0否)
    dataIndex: 'charLength',
    ellipsis: true,
    width: '5%',
    customHeaderCell: () => {		// 表头加粗
      return {style: {'font-weight':'bold'}} // return 想要设置的样式
    },
    slots: {customRender: 'charLength'}
  },
  {
    title: '小数位',//(1.是;0否)
    dataIndex: 'decimalPlaces',
    ellipsis: true,
    width: '5%',
    customHeaderCell: () => {		// 表头加粗
      return {style: {'font-weight':'bold'}} // return 想要设置的样式
    },
    slots: {customRender: 'decimalPlaces'},
    customCell: () => {		// 在此处可以修改单元格中的样式
      return {style: {'text-align': 'center', '-webkit-line-clamp': 2}} // return 想要设置的样式
    },
  },
  {
    title: '状态',//(1.是;0否)
    dataIndex: 'status',
    ellipsis: true,
    width: '5%',
    customHeaderCell: () => {		// 表头加粗
      return {style: {'font-weight':'bold'}} // return 想要设置的样式
    },
    slots: {customRender: 'status'}
  },

];

function formatFlag(flag: any) {
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

const thisCheckKey = ref('')

function handleSelect(key) {
  if (null != key) {
    thisCheckKey.value = key
    reloadTable()
  }
}

function reload() {

}

function setTableData() {

}

provide('openThisContent', (params) => {
  reload2({
    searchInfo: params
  })
})
// 这是示例组件
const dynamicTenantId = ref(getCurrentAccountName(true))
const [registerTable, {
  reload: reload2,
  getColumns,
  getPaginationRef,
  setPagination,
  getDataSource,
  setTableData: asadas
}] = useTable({
  api: async (params) => {
    const aa = await useRouteApi(GetfaCardColumnAll, {schemaName: dynamicTenantId})({})
    console.log(aa)
    if (params.data == null) {
      return aa
    }
    return aa.filter(it => it.flag == params.data.keys[0])
  },
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  actionColumn: {
    width: '5%',
    title: '操作',
    dataIndex: 'action',
    customHeaderCell: () => {		// 表头加粗
      return {style: {'font-weight':'bold'}} // return 想要设置的样式
    },
    slots: {customRender: 'action'}
  },
})


provide('getDataSource', getDataSource)
provide('reload2', reload2)

const [registerEditPage, {openModal: openEditPage}] = useModal()
const [registerSavePage, {openModal: openSavePage}] = useModal()
const [registerExcelPage, {openModal: openExcelPage}] = useModal()
const val = {
  id: null,
  peopertyId: '',
  peopertyName: '',
  flag: '1',
  status: '1',
  faDepMethod: '1',
}
const openAddPage = () => {
  openSavePage(true, {
    data: val,
    dynamicTenantId: dynamicTenantId.value
  })
}
const condClick = (data: any) => {
  /*openEditPage(true, {
    data: data,
    dynamicTenantId: dynamicTenantId.value
  })*/
}

function jiti(record) {
  if (record.faDepMethod == 0) {
    return '不计提'

  } else if (record.faDepMethod == 1) {
    return '新增次月开始计提'
  } else if (record.faDepMethod == 2) {
    return '新增当月开始计提'
  } else if (record.faDepMethod == 3) {
    return '一次性计提'
  }

}

const del = async (data: any) => {
  createConfirm({
    iconType: 'error',
    title: '警告',
    content: '删除后数据将不能恢复，你确认要删除吗?',
    onOk: async () => {
      isShowTree.value = false
      await useRouteApi(deleteDept, {schemaName: dynamicTenantId})(data)
      isShowTree.value = true
      // alert('删除成功！')
      message.success('删除成功！')
      reloadTable()
    },
    onCancel: () => {
      return false
    }
  })

}

async function saveData(data: any) {
  isShowTree.value = false
  await useRouteApi(saveDept, {schemaName: dynamicTenantId})(data)
  isShowTree.value = true
  reloadTable()
  checkRow.value = []
  state.selectedRowKeys = []
}


async function editFlagData(data: any) {
  isShowTree.value = false
  if (data.status == 1) {
    data.status = 0
  } else if (data.status == 0) {
    data.status = 1

  }
  await SavefaCardColumn(data)
  reload2()
  isShowTree.value = true
  reloadTable()
}

function onSearch() {

}

const isShowTree = ref(true)

function reloadTable() {
  console.log('reloadTable')

  reload({
    searchInfo: {
      id: thisCheckKey.value,
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

    if(checkRow.value[0].status != '1'){
      openEditPage(true, {
        data: checkRow.value[0],
        dynamicTenantId: dynamicTenantId.value
      })
    }else{
      createWarningModal({
        iconType: 'warning',
        title: '编辑',
        content: '已经启用不能编辑！'
      })
    }
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '编辑',
      content: '请选择一条进行编辑！'
    })
  }
}

async function delList() {
  console.log(checkRow.value)
  const ids = checkRow.value.map(it => it.id)
  await DeletefaCardColumnBy({
    ids
  })
  reload2()

  return
  if (checkRow.value.length > 0) {
    const psnList = await useRouteApi(psnFindAll, {schemaName: dynamicTenantId})()
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

//导入
const openExcel = () => {
  openExcelPage(true, {
    dynamicTenantId: dynamicTenantId.value
  })
}

async function saveExcel(data) {
  await useRouteApi(excelDept, {schemaName: dynamicTenantId})(data)
  message.success('导入成功！')
  reloadTable()
}

import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {getCurrentAccountName, getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useMessage} from "/@/hooks/web/useMessage";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {findDataBase} from "/@/api/record/system/account";
import {psnFindAll} from "/@/api/psn/psn";

import {columnProps} from "ant-design-vue/es/table/interface";

const defaultPage = ref(true)
const pageParameter: any = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '湖北万亚软件技术有限公司',
  ifUnit: false,
  total: 0,
  thisAdInfo: {}
})

function init() {
  getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(res => {
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
const windowHeight = (window.innerHeight - (350))
const tableRef: any = ref(null)
const totalColumnWidth = ref(0)

function initTableWidth() {
  let total = windowWidth
  if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
  // totalColumnWidth.value = total
  tableRef.value.$el.style.setProperty('width', (total + 70) + 'px')
}

const loadMark = ref(false)
const {createWarningModal} = useMessage();
const dynamicAdReload = async (obj) => {
  // const dataBase: any = await findDataBase(obj.accId, obj.year)
  dynamicTenantId.value = obj.accountMode
  let data: any = {}
  data.id = thisCheckKey.value
  data.flag = flag.value
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize

  /*}else {
    createWarningModal({title: '温馨提示',content: '暂无任何数据！'});
    pageParameter.thisAdInfo = {}
    pageParameter.total = -1
  }*/
  loadMark.value = false
}
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style src="./global-menu-index.less" lang="less" scoped></style>
<style lang='less' scoped>
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
.bg-white{
  width: 250px ;
  min-height: 250px ;
  height: calc(100% - 228px);
  border: 1px #cccccc solid;
  background:white ;
  margin-top: -0.5%;
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
