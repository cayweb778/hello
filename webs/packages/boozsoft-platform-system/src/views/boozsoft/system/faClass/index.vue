<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title">
          <pic-right-outlined />
          <b class="noneSpan">资产分类</b>
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
            v-if="!defaultPage"
            @click="openExcel"
          ><span>导入</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
          ><span>导出</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
          ><span>打印</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
          ><span>退出</span></button>
        </div>
      </div>
      <div class="app-container-neck">
        <!--        <UnitChange v-if="!defaultPage" @reloadTable="dynamicAdReload" v-model="pageParameter"/>-->
<!--        <div style="display: inline-block;float: left;font-size: 14px;width: 50%;">-->
<!--          <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>-->
<!--        </div>-->
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

          <!--          <a-button>
                      <EditFilled :style="{ fontSize: '14px' }" />
                    </a-button>-->

          <!--          <a-button >
                      <PieChartFilled :style="{ fontSize: '14px' }" />
                    </a-button>
                    <a-button>
                      <FilterFilled :style="{ fontSize: '14px' }" />
                    </a-button>-->
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

        <div style="width: calc(100% - 250px); float: right;margin-left: 5px;">
          <BasicTable class="w-3/4 xl:w-4/5"
                      ref="tableRef"
                      :loading="loadMark"
                      :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
                      :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
                      :scroll="{ x: totalColumnWidth,y: windowHeight }"
                      @row-click="condClick"
                      @register="registerTable">
            <template #createDate="{ record }"><span
              v-if="record.createDate!='' && record.createDate!=null">{{
                record.createDate.trim().split(' ')[0]
              }}</span></template>
            <!--      <template #flag="{ record }"> {{ formatFlag(record.flag) }} </template>-->
            <template #flag="{ record }">
          <span>
            <a-tag :color="record.flag === '1' ? 'green' : 'volcano'">
              {{ record.flag === '1' ? '启用' : '停用' }}
            </a-tag>
          </span>
            </template>
            <template #action="{ record }">
              <div>
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
import {deleteDept, saveDept, editFlag, excelDept} from '/@/api/record/system/dept'
import { GetDeptTree } from '/@/api/sys/dept'
import { BasicTable, useTable } from '/@/components/Table'
import EditPage from './popup/edit.vue'
import AddPage from './popup/save.vue'
import DeptTree from './DeptTree.vue'
import Excel from './popup/excel.vue'
import { useModal } from '/@/components/Modal'
import { PageWrapper } from '/@/components/Page'
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
  ReadOutlined, SafetyOutlined, RestOutlined,PicRightOutlined
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
    ellipsis: true
  },
  {
    title: '分类级次',
    dataIndex: 'faClassGrade',
    ellipsis: true,
    customCell: () => {		// 在此处可以修改单元格中的样式
      return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
    },
  },
  {
    title: '资产分类名称',
    dataIndex: 'faCclassName',
    ellipsis: true,
    align: 'left',
    customCell: () => {		// 在此处可以修改单元格中的样式
      return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
    },
  },
  // {
  //   title: '客户分类级次编码',
  //   dataIndex: 'faGradeCode',
  //   ellipsis: true,
  //   customCell: () => {		// 在此处可以修改单元格中的样式
  //     return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
  //   },
  // },
  {
    title: '上级ID',
    dataIndex: 'parentId',
    ellipsis: true,
    defaultHidden: true
  },
  {
    title: '是否末级分类',//（1是，0否）
    dataIndex: 'cusBend',
    ellipsis: true,
    customCell: () => {		// 在此处可以修改单元格中的样式
      return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
    },
  },
  {
    title: '状态',
    dataIndex: 'flag',
    ellipsis: true,
    slots: { customRender: 'flag' }
  },
  {
    title: '使用年限',//（月）
    dataIndex: 'faYears',
    ellipsis: true,
    align: 'left',
    customCell: () => {		// 在此处可以修改单元格中的样式
      return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
    },
  },
  {
    title: '残值率',
    dataIndex: 'faResRate',
    ellipsis: true,
    align: 'left',
    customCell: () => {		// 在此处可以修改单元格中的样式
      return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
    },
  },
  {
    title: '折旧方法',
    dataIndex: 'faDepMethod',
       ellipsis: true,
    align: 'left',
    customCell: () => {		// 在此处可以修改单元格中的样式
      return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
    },
  },
  {
    title: '摊销方法',
    dataIndex: 'faAmortization',
       ellipsis: true,
    align: 'left',
    customCell: () => {		// 在此处可以修改单元格中的样式
      return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
    },
  },
  {
    title: '资产属性',
    dataIndex: 'faProperty',
       ellipsis: true,
    align: 'left',
    customCell: () => {		// 在此处可以修改单元格中的样式
      return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
    },
  },
  {
    title: '卡片样式',
    dataIndex: 'faCardStyle',
       ellipsis: true,
    align: 'left',
    customCell: () => {		// 在此处可以修改单元格中的样式
      return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
    },
  },
  {
    title: '进项税率',
    dataIndex: 'faInputRate',
       ellipsis: true,
    align: 'left',
    customCell: () => {		// 在此处可以修改单元格中的样式
      return {style: {'text-align': 'left', '-webkit-line-clamp': 2}} // return 想要设置的样式
    },
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
  setTableData
}] = useTable({
  api: async (params) => {

    const aa= await GetFaClassAll()
    return aa
  },
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  },
  searchInfo: {
    id: thisCheckKey.value,
    flag: flag.value
  }
})
provide('reload',reload)
const [registerEditPage, { openModal: openEditPage }] = useModal()
const [registerSavePage, { openModal: openSavePage }] = useModal()
const [registerExcelPage, { openModal: openExcelPage }] = useModal()
const val = {
  id: null,
  uniqueCustclass: '',
  faClassId: '',
  faClassGrade: '',
  faCclassName: '',
  faGradeCode: '',
  parentId: '',
  cusBend: '',
  flag: '',
  faYears: '',
  faResRate: '',
  faDepMethod: '',
  faAmortization: '',
  faProperty: '',
  faCardStyle: '',
  faInputRate: '',
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
      reloadTable()
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
  reloadTable()
  checkRow.value = []
  state.selectedRowKeys = []
}
async function editFlagData(data:any) {
  isShowTree.value = false
  await useRouteApi(editFlag,{schemaName: dynamicTenantId})(data)
  isShowTree.value = true
  reloadTable()
}
function onSearch(){

}

const isShowTree = ref(true)

function reloadTable() {
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
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-GDZC.vue";
import {findDataBase} from "/@/api/record/system/account";
import {columnProps} from "ant-design-vue/es/table/interface";
import {assemblyDynamicColumn} from "/@/views/boozsoft/system/department/data";
import {psnFindAll} from "/@/api/psn/psn";
import {GetFaClassAll} from "/@/api/boozsoft/group/faClass";
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
  let res = await useRouteApi(GetDeptTree, {schemaName: dynamicTenantId})(data)
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
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style src="./global-menu-index.less" lang="less" scoped></style>
<style lang='less' scoped >
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
