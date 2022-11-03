<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title">
          <UnorderedListOutlined style="color: #0096c7"/>
          <b class="noneSpan">组织-现金流量项目</b>
        </div>
        <div
          class="ant-btn-group"
          data-v-a1ccd506=""
          style="float: right"
        >
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="init()"
          ><span>初始化</span></button>
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
          ><span>编辑</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
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
            @click="openExcel()"
          ><span>导入</span></button>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/home/welcome')"><span>退出</span></button>
        </div>
      </div>
      <div class="app-container-neck">
        <div style="float: left; width: 50%;">
          <!--        <span><b>会计准则：{{tname}}</b></span>-->
          &nbsp;&nbsp;&nbsp;
          &nbsp;&nbsp;&nbsp;
          <span style="font-size: 16px"><b>组织：</b>
          <a-select
            v-model:value="pageParameter.orientation"
            option-filter-prop="children"
            show-search
            style="width: 25%;"
            @change="standardCheck"
          >
            <a-select-option
              v-for="item in orientationlist"
              :key="item.uniqueCode"
              :value="item.uniqueCode"
            >
            {{ item.orgName }}
          </a-select-option> </a-select>
        </span>&nbsp;&nbsp;&nbsp;
          <span style="font-size: 16px"><b>年度：</b>
            <a-select
              v-model:value="pageParameter.orgIyear"
              option-filter-prop="children"
              show-search
              style="width: 15%;"
              @change="standardCheck"
            >
            <a-select-option
              v-for="item in orgIyearList"
              :key="item"
              :value="item"
            >
            {{ item }}
          </a-select-option> </a-select>
          </span>&nbsp;&nbsp;&nbsp;


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
            <DeptTree   @select="handleSelect" v-model="pageParameter" ref="mychild"/>
          </div>
        </div>

        <div style="display: inline;width: calc(100% - 250px);float: right;padding-left: 6px;">
          <BasicTable
            ref="tableRef"
            :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            @row-click="condClick"
            :dataSource="cardList"
            @register="registerTable"
          >
            <template #projectName="{ record }" >
              <span style="float: left" >{{ record.projectName }}</span>
            </template>

            <template #projectType="{ record }"> {{ formatProjectType(record.projectType) }} </template>
            <template #fangxiang="{ record }"> {{ formatFangxiang(record.fangxiang) }} </template>
            <template #flag="{ record }">
              <span>
                <a-tag
                  :color="record.flag === '1' ? 'green' : 'volcano'"
                >
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
                    <p style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>
                    <p v-if="record.flag=='0'" @click="editFlagData(record)" style="cursor: pointer"><CheckCircleOutlined /> 启用</p>
                    <p v-if="record.flag=='1'" @click="editFlagData(record)" style="cursor: pointer"><CloseCircleOutlined /> 停用</p>
                    <p style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>
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
<script setup="props, {emit}" lang="ts">
import {getProjectCashList, deleteProjectCash, saveProjectCash,editFlag, excelProjectCash,getAccountList,initData,deleteProjectCash2, saveProjectCash2,editFlag2, excelProjectCash2} from '/@/api/record/system/origin-project-cash';
import { BasicTable, useTable } from '/@/components/Table'
import DeptTree from './DeptTree.vue'
import Edit from './popup/edit.vue'
import Excel from './popup/excel.vue'
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
  UsbOutlined,PrinterOutlined,UnorderedListOutlined
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
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {columnProps} from "ant-design-vue/es/table/interface";
import {useMessage} from "/@/hooks/web/useMessage";
import {aoaToSheetXlsx} from "/@/components/Excel";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";

import {
  findByUniqueCode,
  getOrganizeList,
  groupByOrgPeriodIyear
} from "/@/api/record/group/im-organize";
import {findAllByOrgCodeKeMu, findByTOrganization} from "/@/api/acctemplate/acctemplate";
import {useRoute} from "vue-router";

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

const CrudApi = {
  list: getProjectCashList,
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
      dataIndex: 'jieCode',
      ellipsis: true,
      width: 120,
      slots: { customRender: 'jieCode' },
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
    },
    {
      title: '对应贷方科目',
      dataIndex: 'daiCode',
      ellipsis: true,
      width: 120,
      slots: { customRender: 'daiCode' },
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
  projectType: '0',
  orientation: '',
  orgIyear: '',
  groupId: '',
  year: '',
  accStandard: '',
})

// 这是示例组件
const [registerTable, { reload,setTableData,setColumns,getColumns,getPaginationRef,setPagination,getDataSource }] = useTable({
/*
  api: CrudApi.list,
*/
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
  actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  }
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
      data: data
    })
  }
}
const openAddPage = () => {
  openEditPage(true, {
    data: val,
    year: pageParameter.orgIyear,
    showEdit: false,
    isEdit: false,
    accStandard: uniqueAccStandard.value,
    originId:  pageParameter.orientation
  })
}
const openEdit = (data:any) => {
  openEditPage(true, {
    data: data
  })
}
const del = async(data:any) => {
  await deleteProjectCash(data)
  let arr = await getAccountList(pageParameter.orientation)
  console.log(arr)
  arr.forEach(v=>{
    useRouteApi(deleteProjectCash2,{schemaName: v.accId+'-'+pageParameter.orgIyear})(data)
  })
  await reloadCurrentPage()
  message.success('删除成功！')
}

async function saveData(data:any) {
  await saveProjectCash(data)
  //循环账套添加
  let arr = await getAccountList(pageParameter.orientation)
  console.log(arr)
  arr.forEach(v=>{
    useRouteApi(saveProjectCash2,{schemaName: v.accId+'-'+pageParameter.orgIyear})(data)
  })
  await reloadCurrentPage()
}

async function editFlagData(data:any){
  await editFlag(data)
  let arr = await getAccountList(pageParameter.orientation)
  console.log(arr)
  arr.forEach(v=>{
    useRouteApi(editFlag2,{schemaName: v.accId+'-'+pageParameter.orgIyear})(data)
  })
  await reloadCurrentPage()
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
const checkRow:any = ref([])
const onSelectChange = (selectedRowKeys,row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
};
const editOpen = () => {
  if (checkRow.value.length==1) {
    openEditPage(true, {
      data: checkRow.value[0]
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
      await deleteProjectCash(item)
      let arr = await getAccountList(pageParameter.orientation)
      console.log(arr)
      arr.forEach(v=>{
        useRouteApi(deleteProjectCash2,{schemaName: v.accId+'-'+pageParameter.orgIyear})(item)
      })
    }
    checkRow.value = []
    await reloadCurrentPage()
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
  openExcelPage(true, {})
}

async function saveExcel(data:any){
  await excelProjectCash(data)
  let arr = await getAccountList(pageParameter.orientation)
  console.log(arr)
  arr.forEach(v=>{
    useRouteApi(excelProjectCash2,{schemaName: v.accId+'-'+pageParameter.orgIyear})(data)
  })
  await reloadCurrentPage()
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

const cardList:any = ref([])
async function reloadCurrentPage() {
  const res:any = await getProjectCashList(pageParameter)
  cardList.value = res
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


const route = useRoute();
const orientationlist = ref([]);
const orgIyearList = ref([]);
const templateId = ref(route.query.templateId);
const uniqueAccStandard = ref(route.query.uniqueAccStandard);
const mychild =  ref(null)
const findAllData = async () => {
  // 组织列表
  const oration =await getOrganizeList('');
  orientationlist.value=oration.items
  console.log(orientationlist.value)
  if(uniqueAccStandard.value===undefined){
    let findByTemplate=await findByTOrganization(orientationlist.value[0].uniqueCode)
    uniqueAccStandard.value=findByTemplate[0].uniqueAccStandard
    pageParameter.accStandard = uniqueAccStandard.value
    templateId.value=findByTemplate[0].id
    pageParameter.orientation=orientationlist.value[0].uniqueCode
  }
  // 组织期间表
  pageParameter.orgIyear = []
  orgIyearList.value= await groupByOrgPeriodIyear(pageParameter.orientation)
  if(orgIyearList.value.length>0){
    pageParameter.orgIyear = orgIyearList.value[0]
  }
  //刷新tree
  //刷新子页面list
  mychild.value.fetch()
  await reloadCurrentPage()
};
onMounted(async () => {
  findAllData();
});

const standardCheck = async () => {
  console.log(orientationlist.value)
  console.log(orientationlist.value.find(v=> v.uniqueCode === pageParameter.orientation))
  let findByTemplate=await findByTOrganization(orientationlist.value.find(v=> v.uniqueCode === pageParameter.orientation).uniqueCode)
  uniqueAccStandard.value=findByTemplate[0].uniqueAccStandard
  pageParameter.accStandard = uniqueAccStandard.value

  // 组织期间表
  pageParameter.orgIyear = []
  orgIyearList.value= await groupByOrgPeriodIyear(pageParameter.orientation)
  if(orgIyearList.value.length>0){
    pageParameter.orgIyear = orgIyearList.value[0]
  }
  //刷新tree
  //刷新子页面list
  mychild.value.fetch()
  await reloadCurrentPage()
}

async function handleSelect(key) {
  if (null != key) {
    pageParameter.projectType = key
    await reloadCurrentPage()
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
      await initData({
        groupId:pageParameter.orientation,
        year: pageParameter.year,
      })
      message.success("初始化完成!")
      await reloadCurrentPage()
    },
    onCancel: () => {
      return false
    }
  })
}
</script>
<style src="./global-menu-index.less" lang="less" scoped></style>

<style lang='less' scoped >
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
  padding: 5px 8px !important;
}
.bg-white{
  width: 250px ;
  min-height: 250px ;
  height: calc(100% - 228px);
  border: 1px #cccccc solid;
  background:white ;
  margin-top: -0.5%;
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
.app-container-head{
  padding-left: 5px;
}
</style>
