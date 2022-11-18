<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">-->
<!--        <div class="container-head-title" style="margin-left: 40%">
          <b class="noneSpan">项目信息</b>
        </div>-->
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">项目信息</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            v-if="!defaultPage"
            @click="openApplyList()"
          ><span>新增申请</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            v-if="defaultPage"
            @click="openAddPage()"
          ><span>新建</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="editData()"
          ><span>修改</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="deleteData()"
          ><span>删除</span></button>

          <button
            type="button"
            class="ant-btn ant-btn-me"
            v-if="!defaultPage"
            @click="openInto()"
          ><span>引入</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            v-if="defaultPage"
            @click="openExcel()"
          ><span>导入</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="exportExcel()"
          ><span>导出</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
          ><span>打印</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="closeCurrent()"
          ><span>退出</span></button>
<!--          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="openSelectProject()"
          ><span>测试弹框</span></button>-->
        </div>
      </div>

      <div style="clear: none;"/>
      <div style="margin-top: -30px;margin-left: 80px;">
        <div style="display: inline-block;float: left;margin-top: -40px;">
          <AccountPicker theme="three" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="display: inline-block;float: left;margin-left: 15px;margin-top: 5px;">
          <span v-if="pageParameter.projectClassCtl=='1'">
            <span style="color:#666666;font-size: 14px;font-weight: bold;">项目大类：</span>
            <a-select v-model:value="pageParameter.itemCode" @change="changCate()" placeholder="请选择项目大类" style="min-width: 200px;">
              <a-select-option v-for="item in itemList" :key="item.itemCode" :value="item.itemCode">
                ({{ item.itemCode }}){{ item.itemName }}
              </a-select-option>
            </a-select>
          </span>
          <span v-if="pageParameter.projectClassCtl!='1'">
          <span style="color:#666666;font-size: 14px;font-weight: bold;">项目栏目样式：</span>
          <a-select v-model:value="cateCode" @change="changCate()" placeholder="请选择项目样式" style="min-width: 200px;">
            <a-select-option v-for="cate in cateList" :key="cate.projectCateCode" :value="cate.projectCateCode">
              ({{ cate.projectCateCode }}){{ cate.projectCateName }}
            </a-select-option>
          </a-select>
          </span>
        </div>
        <div style="float: left; margin-left: 20px;margin-bottom: -15px;padding-bottom: -10px;padding-top: 10px;line-height: 30px;font-size: 10px;color:#999999;">共 {{cardList.length}} 条记录</div>
        <div style="float: right; margin-left: 10px">
          <a-button @click="reloadTable()">
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <a-popover placement="bottom" v-model:visible="visible3">
            <template #content>
              <DynamicColumn :defaultData="initDynamics()['DATA']" :dynamicData="dynamicColumnData"
                             :lanmuInfo="lanMuData" @reload="reloadColumns"/>
              <span @click="pageParameter.showRulesSize = 'MAX'"
                    :style="{backgroundColor: (pageParameter.showRulesSize==='MAX')?'#e3e0e0':'white',cursor: 'pointer'}">
                &emsp;&emsp;
                大号字体&emsp;<CheckOutlined v-if="pageParameter.showRulesSize==='MAX'"
                                         :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
              <span @click="pageParameter.showRulesSize = 'MIN'"
                    :style="{backgroundColor: (pageParameter.showRulesSize==='MIN')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;小号字体&emsp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MIN'"
                :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
            </template>
            <template #title>
              <b>设置表格字号</b>
            </template>
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>

          <a-button title="回收站" @click="openProjectDel()">
            <DeleteOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <!--          <a-button>
                      <EditFilled :style="{ fontSize: '14px' }" />
                    </a-button>-->
          <!--          <a-popover placement="bottom">
                      <a-button>
                        <PicLeftOutlined :style="{ fontSize: '14px' }" />
                      </a-button>
                    </a-popover>

                    <a-button>
                      <EditFilled :style="{ fontSize: '14px' }" />
                    </a-button>

                    <a-button >
                      <PieChartFilled :style="{ fontSize: '14px' }" />
                    </a-button>
                    <a-button>
                      <FilterFilled :style="{ fontSize: '14px' }" />
                    </a-button>-->
        </div>
        <div style="float: right; position: relative">
          <!--          <label style="font-size: 14px">检索条件：</label>-->
          <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
            <a-select-option style="font-size: 12px;" value="projectCode">项目编码</a-select-option>
            <a-select-option style="font-size: 12px;" value="projectName">项目名称</a-select-option>
            <a-select-option style="font-size: 12px;" value="jiesuan">是否结算</a-select-option>
            <a-select-option style="font-size: 12px;" value="psnInCharge">项目负责人</a-select-option>
            <a-select-option style="font-size: 12px;" value="deptCode">部门名称</a-select-option>
          </a-select>
          <div style="float: right; position: relative">
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

      <div style="clear:both"/>
    </div>
    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <div class="w-1/4 xl:w-1/5" style="width: 250px;min-height: 250px;border: 1px #cccccc solid;margin-right: .2%;margin-bottom: 68px;">
        <ClassTree v-if="isShowTree" class="w-1/4 xl:w-1/5" @select="handleSelect" v-model="pageParameter" />
        </div>
        <div style="width: calc(100% - 250px); float: right;margin-left: 5px;">
          <BasicTable
            ref="tableRef"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :scroll="{ x: totalColumnWidth,y: windowHeight }"
            :dataSource="tableData"
            :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys,getCheckboxProps:rowSelection.getCheckboxProps, onChange: onSelectChange }"
            @selection-change="selectionChange"
            @row-click="rowClick"
            @register="registerTable"
            :loading="loadMark"
          >
            <template #projectCode="{ record }">
              <a class="tableUStyle" @click="openEdit(record)">{{ record.projectCode }}</a>
            </template>
            <template #projectName="{ record }">
              <a class="tableUStyle" @click="openEdit(record)">{{ record.projectName }}</a>
            </template>
            <template #jiesuan="{ record }">
              <span v-if="record.jiesuan!='' && record.jiesuan!=null">
                  {{ record.jiesuan === '1' ? '是' : '否' }}
              </span>
            </template>
            <template #successState="{ record }">
              <span>
                <a-tag :color="record.successState === '1' ? 'green' : 'volcano'">
                  {{ record.successState === '1' ? '已生效' : '未生效' }}
                </a-tag>
              </span>
            </template>
            <template #projectClassCode="{ record }">
              {{ formatProjectClassCode(record.projectClassCode) }}
            </template>
            <template #psnInCharge="{ record }">
              {{ formatPsnInCharge(record.psnInCharge) }}
            </template>
            <template #deptCode="{ record }"> {{ formatDeptCode(record.deptCode) }}</template>
            <template #startDate="{ record }">
              <span v-if="record.startDate!='' && record.startDate!=null">{{ record.startDate.trim().split(' ')[0] }}</span>
            </template>
            <template #endDate="{ record }">
              <span v-if="record.endDate!='' && record.endDate!=null">{{ record.endDate.trim().split(' ')[0] }}</span>
            </template>
            <!--        <template #action="{ record }">

                      <div>
                        <a-popover placement="bottom">
                          <a-button style="padding: 0px 4px; height: 20px;">
                            <CaretDownFilled />
                          </a-button>
                          <template #content>
                            <p style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>
                            <p style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>
                          </template>
                        </a-popover>
                      </div>
                    </template>-->

          </BasicTable>
          <div class="pagination-text" v-show="showPaginationText">
            共 {{paginationNumber}} 条记录&nbsp;&nbsp; 每页 200 条
          </div>
        </div>
        <Edit
          @save="saveData"
          @register="registerEditPage"
        />
        <Excel
          @save="saveExcel"
          @register="registerExcelPage"
        />
        <SelectProject @save="saveSelectProject" @register="registerSelectProjectPage"/>

        <ProjectDel @save="saveProjectDel" @register="registerProjectDelPage"/>
        <Into @save="saveInto" @register="registerIntoPage"/>
        <ApplyList @save="saveApplyList" @register="registerApplyListPage"/>
      </PageWrapper>
    </div>
  </div>

</template>
<script setup lang="ts">
import { BasicTable, useTable } from '/@/components/Table'
import { PageWrapper } from '/@/components/Page'
import ClassTree from './ClassTree.vue'

import { useModal } from '/@/components/Modal'
import {
  CaretDownFilled,
  FormOutlined,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled,EditOutlined
  ,SortAscendingOutlined,CheckOutlined
  ,SortDescendingOutlined,ProfileOutlined
} from '@ant-design/icons-vue'
import {computed, nextTick, onMounted, reactive, ref, watch} from 'vue'
import {
  getProjectList,
  deleteProject,
  saveProject,
  excelProject,
  deleteProjectList,
  findProjectByCateCode
} from '/@/api/record/system/project'
import Edit from './popup/edit.vue'
import Excel from './popup/excel.vue'
import {cateFindAll, cateFindStateFlag,columnFindCate} from "/@/api/group_project_category/project_category";
import { useProjectStoreWidthOut } from '/@/store/modules/project'
import {getDeptListById} from "/@/api/record/system/dept";
import {psnFindAll} from "/@/api/psn/psn";
import {GetProClassTree, projectClassFindAll} from "/@/api/record/system/project_class";
// import DynamicColumn from "/@/views/boozsoft/stock/stock_sales_add/component/DynamicColumn.vue";
const DynamicColumn=null
import {Select as ASelect,Input as AInput,Popover as APopover,Tag as ATag,   Radio as ARadio,
  Table as ATable,  Checkbox as ACheckbox,
  Popconfirm as APopconfirm,
  message, } from 'ant-design-vue'
const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option
const {
  createErrorModal,
  createSuccessModal
} = useMessage()
const {closeCurrent} = useTabs(router);

const loadMark = ref(false)
const formItems = ref({
  selectType: 'projectCode',
  selectValue: ''
})
const tableData:any = ref([]);
const tableDataAll:any = ref([]);

const showPaginationText = ref(false)
const paginationNumber = ref(0)

//选择项目大类
const cateCode:any = ref('01')
const currentCateCode=computed(()=>useProjectStoreWidthOut().getNum)

const classCode = ref('')
let num = 0
async function handleSelect(keys) {
  lanMuData.value.changeNumber += 1
  // console.log("选择的数据是"+keys)
  // if (null != keys) {
  //   classCode.value = keys.uniqueCode
  //   reloadTable()
  //   reloadCurrentPage()
  // }
  classCode.value = keys
  if (classCode.value!=null && classCode.value!='') {
    /*if (num==0 && classCode.value!='') {
      reloadTable()
    }*/
    loadMark.value = true
    cardList.value = tableDataAll.value.filter(item =>{
      if (classCode.value!=null && classCode.value!='' && classCode.value!='0') {
        return item.projectClassCode==classCode.value
      }
      return item
    })
    tableData.value = cardList.value
    let num = 50-(tableData.value.length%50)
    for (let i=0;i<num;i++){
      tableData.value.push({})
    }
    await setPagination({
      total: tableData.value.length
    })
    loadMark.value = false
    visible3.value=false
  }
}

async function changCate(){
  isShowTree.value = false
  if (pageParameter.projectClassCtl=='1'){
    itemList.value.forEach(item=>{
      if (item.itemCode==pageParameter.itemCode){
        cateCode.value = item.cateCode
      }
    })
  }
  useProjectStoreWidthOut().commitNum(cateCode.value)
  await reloadCurrentPage()
  await nextTick(() => {
    isShowTree.value = true
  })
}

watch(currentCateCode,(abc)=> {
  // projectStore.projectClassTree()

  // reload({
  //   searchInfo:{
  //     projectCateCode:abc
  //
  //   }
  // })
})
//根据基础档案查询基础档案栏目
const cateList:any = ref([])
async function reloadCate() {
  await reloadItem()
  const res: any = await cateFindStateFlag({})
  cateList.value = res
  // if (res.length>0) {
  //   cateCode.value = res[0].projectCateCode
  //   // console.log(currentCateCode)
  // }
}
const itemList:any = ref([])
async function reloadItem() {
  if (pageParameter.projectClassCtl=='1') {
    const res: any = await useRouteApi(getProjectItemList, {schemaName: dynamicTenantId})({})
    itemList.value = res
    if (itemList.value.length>0){
      pageParameter.itemCode = itemList.value[0].itemCode
    }
  }
}
const defaultPage = ref(false)
onMounted(async() => {
  await reloadCate()
  // 进页面执行
  /*getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(res=>{
    if (null != res){
      pageParameter.projectClassCtl = res.projectClassCtl
      if (res.independent == 0) {
        defaultPage.value = false
      }
      pageParameter.companyName = res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
    resetDynamicColumnData()
  })*/
  // resetDynamicColumnData()
  await reloadCurrentPage()
})
const dynamicTenantId = ref(getCurrentAccountName(true))
const CrudApi = {
  list: async (params) => {
    return useRouteApi(getProjectList, {schemaName: dynamicTenantId})(params)
  },
  columns: [
    {
      title: '项目编码',
      dataIndex: 'projectCode',
      ellipsis: true,
      slots: { customRender: 'projectCode' }
    },
    {
      title: '项目名称',
      dataIndex: 'projectName',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true,
      slots: { customRender: 'projectName' }
    },
    {
      title: '项目分类',
      dataIndex: 'projectClassCode',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true,
      slots: { customRender: 'projectClassCode' }
    },
    {
      title: '所属部门',
      dataIndex: 'deptCode',
      ellipsis: true,
      slots: { customRender: 'deptCode' }
    },
    {
      title: '开始日期',
      dataIndex: 'startDate',
      ellipsis: true,
      slots: {customRender: 'startDate'}
    },
    {
      title: '结束日期',
      dataIndex: 'endDate',
      ellipsis: true,
      slots: {customRender: 'endDate'}
    },
    {
      title: '项目负责人',
      dataIndex: 'psnInCharge',
      ellipsis: true,
      slots: { customRender: 'psnInCharge' }
    },
    {
      title: '是否结算',
      dataIndex: 'jiesuan',
      ellipsis: true,
      slots: { customRender: 'jiesuan' }
    },
    /*  {
        title: '项目预算',
        dataIndex: 'menu1',
        ellipsis: true,
      },*/
    /*{
      title: '是否生效',
      dataIndex: 'successState',
      ellipsis: true,
      slots: { customRender: 'successState' }
    },*/
  ],
  editData: {
    id: null,
    uniqueCode: '',
    projectCode: '',
    projectName: '',
    projectClassCode: '',
    jiesuan: '',
    startDate: null,
    endDate: null,
    psnInCharge: '',
    deptCode: ''
  }
}
//项目分类
const proClassList:any = ref([])
async function reloadProClass() {
  const res: any = await useRouteApi(projectClassFindAll, {schemaName: dynamicTenantId})({itemCode:pageParameter.itemCode,projectClassCtl:pageParameter.projectClassCtl});
  /*proClassList.value = res.filter(item => {
    return item.projectCateCode == cateCode.value
  })*/
  proClassList.value = res
  // console.log(proClassList.value)
}
function formatProjectClassCode(projectClassCode:any){
  let str = projectClassCode
  // console.log(projectClassCode)
  proClassList.value.forEach(
    function (proClass:any) {
      if (proClass.projectClassCode == projectClassCode /*&& proClass.projectCateCode == cateCode.value*/) {
        // console.log(proClass)
        str = proClass.projectClassName
      }
    }
  )
  return str
}
//部门
const deptList:any = ref([])
async function reloadDept() {
  const res:any = await useRouteApi(getDeptListById,{schemaName: dynamicTenantId})({})
  deptList.value = res.items
  // console.log(deptList.value)
}
function formatDeptCode(deptCode:any){
  let str = deptCode
  // console.log(deptCode)
  deptList.value.forEach(
    function (dept:any) {
      if (dept.uniqueCode == deptCode){
        // console.log(dept)
        str = dept.deptName
      }
    }
  )
  return str
}
//人员
const psnList:any = ref([])
async function reloadPsn() {
  const res:any = await useRouteApi(getPsnList,{schemaName: dynamicTenantId})({})
  psnList.value = res
  // console.log(psnList.value)
}
function formatPsnInCharge(psnInCharge:any){
  let str = psnInCharge
  // console.log(psnInCharge)
  psnList.value.forEach(
    function (psn:any) {
      if (psn.uniqueCode == psnInCharge){
        // console.log(psn)
        str = psn.psnName
      }
    }
  )
  return str
}
onMounted(async() => {
  await reloadDept()
  await reloadPsn()
  await reloadProClass()
})
// 这是示例组件
const [registerTable, { reload ,setTableData,getDataSource,setColumns,getColumns,getPaginationRef,setPagination,deleteSelectRowByKey}] = useTable({
  /*api: async (params) => {
    params.projectCateCode = cateCode.value
    params.projectClassCode = classCode.value
    return useRouteApi(getProjectList, {schemaName: dynamicTenantId})(params)
  },*/
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: true,
  indexColumnProps: {width: 60,fixed: 'left'},
  pagination: {
    pageSize: 200,
    // showSizeChanger: false,
    simple:true,
    showTotal: t => `共${t}条记录    每页200条`
  },
  searchInfo:{
    projectCateCode:currentCateCode.value,
    projectClassCode:classCode.value,
  }
})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const [registerExcelPage, { openModal: openExcelPage }] = useModal()
const val = {
  id: null,
  uniqueCode: '',
  projectCode: '',
  projectName: '',
  projectCateCode: '',
  projectClassCode: '',
  jiesuan: '0',
  startDate: null,
  endDate: null,
  psnInCharge: '',
  deptCode: '',
  isEdit: true,
  projectClassCtl: '',
  itemCode: '',
  isState: '0'
}

const openAddPage = () => {
  if (pageParameter.projectClassCtl=='1'){
    if (itemList.value==null || itemList.value.length==0){
      createWarningModal({
        iconType: 'warning',
        title: '提示',
        content: '暂无项目大类信息，请先添加项目大类档案！'
      })
      return false
    }
  }
  if (cateList.value.length > 0) {
    val.projectCateCode = cateCode.value
    val.projectClassCode = classCode.value
    val.projectClassCtl = pageParameter.projectClassCtl
    val.itemCode = pageParameter.itemCode
    val.isState = '0'
    openEditPage(true, {
      data: val
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '提示',
      content: '暂无项目栏目样式信息，请先添加项目栏目样式档案！'
    })
  }
}
const openEdit = (data:any) => {
  data.isEdit=false
  data.projectClassCtl = pageParameter.projectClassCtl
  data.isState = '2'
  openEditPage(true, {
    data
  })
}
const openExcel = () => {
  if (pageParameter.projectClassCtl=='1'){
    if (itemList.value==null || itemList.value.length==0){
      createWarningModal({
        iconType: 'warning',
        title: '提示',
        content: '暂无项目大类信息，请先添加项目大类档案！'
      })
      return false
    }
  }
  if (cateList.value.length > 0) {
    openExcelPage(true, {
      data: {
        projectCateCode: cateCode.value,
        projectClassCtl: pageParameter.projectClassCtl,
        itemCode: pageParameter.itemCode
      }
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '提示',
      content: '暂无项目栏目样式信息，请先添加项目栏目样式档案！'
    })
  }
}
//导入Excel
async function saveExcel(data:any){
  loadMark.value = true
  await useRouteApi(excelProject, {schemaName: dynamicTenantId})(data)
  reloadTable()
  loadMark.value = false
}
const del = async(data:any) => {
  data.projectCateCode=cateCode.value
  await useRouteApi(deleteProject,{schemaName: dynamicTenantId})(data)
  // alert('删除成功！')
  createSuccessModal({
    iconType: 'warning',
    title: '删除',
    content: '删除成功！'
  })
  reloadTable()
}
async function saveData(data:any) {
  await useRouteApi(saveProject, {schemaName: dynamicTenantId})(data)
  reloadTable()
}

function reloadTable() {
  reload({
    searchInfo: {
      projectCateCode: cateCode.value,
      projectClassCode: classCode.value
    }
  })
  checkRow.value = []
  state.selectedRowKeys = []
  reloadCurrentPage()
}
const cardList:any = ref([])
async function reloadCurrentPage() {
  loadMark.value = true
  showPaginationText.value = false
  let len = 0
  const res:any = await useRouteApi(findProjectByCateCode,{schemaName: dynamicTenantId})({projectCateCode:cateCode.value,itemCode:pageParameter.itemCode,projectClassCtl:pageParameter.projectClassCtl})
  tableDataAll.value = res
  cardList.value = res.filter(item =>{
    if (classCode.value!=null && classCode.value!='' && classCode.value!='0') {
      return item.projectClassCode==classCode.value
    }
    return item
  })
  tableData.value = cardList.value
  let num = 50-(tableData.value.length%50)
  for (let i=0;i<num;i++){
    tableData.value.push({})
  }
  await setPagination({
    total: tableData.value.length
  })
  len = tableData.value.filter(item=>item.id!=null && item.id!='').length
  paginationNumber.value = len
  showPaginationText.value = true
  loadMark.value = false
}
async function onSearch() {
  loadMark.value = true
  // let a:any = []
  tableData.value = cardList.value.filter(item=> {
    //通过项目编码
    if (formItems.value.selectType=='projectCode' && formItems.value.selectValue!=''){
      return item.projectCode.indexOf(formItems.value.selectValue) != -1
    }
    //通过项目名称
    if (formItems.value.selectType=='projectName' && formItems.value.selectValue!=''){
      return item.projectName.indexOf(formItems.value.selectValue) != -1
    }
    //通过结算状态过滤
    if (formItems.value.selectType=='jiesuan' && formItems.value.selectValue!=''){
      if (formItems.value.selectValue=='是' || formItems.value.selectValue=='1'){
        return item.jiesuan=='1'
      }
      if (formItems.value.selectValue=='否' || formItems.value.selectValue=='0'){
        return item.jiesuan!='0'
      }
      return item.jiesuan.indexOf(formItems.value.selectValue) != -1
    }
    //通过项目负责人过滤
    if (formItems.value.selectType=='psnInCharge' && formItems.value.selectValue!=''){
      const b = psnList.value.filter(bb => {
        return bb.psnName.indexOf(formItems.value.selectValue) != -1
      })
      if(b.map(bb=>bb.uniqueCode).filter(dd=>dd==item.psnInCharge).length>0){
        return true
      }
      return item.psnInCharge.indexOf(b.map(bb=>bb.uniqueCode)) != -1
    }
    //通过部门名称过滤
    if (formItems.value.selectType=='deptCode' && formItems.value.selectValue!=''){
      const b = deptList.value.filter(bb => {
        return bb.deptName.indexOf(formItems.value.selectValue) != -1
      })
      if(b.map(bb=>bb.uniqueCode).filter(dd=>dd==item.psnInCharge).length>0){
        return true
      }
      return item.deptCode.indexOf(b.map(bb=>bb.uniqueCode)) != -1
    }
    return item
  })
  // console.log(a)
  // setDataSource(a)
  let num = 50-(tableData.value.length%50)
  for (let i=0;i<num;i++){
    tableData.value.push({})
  }
  await setPagination({
    total: tableData.value.length
  })
  loadMark.value = false
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

function editData(){
  if (checkRow.value.length==1) {
    checkRow.value[0].isEdit = true
    checkRow.value[0].isState = '1'
    checkRow.value[0].projectClassCtl = pageParameter.projectClassCtl
    openEditPage(true, {
      data: checkRow.value[0],
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '修改失败',
      content: '请选择一条进行修改！'
    })
  }
}
async function deleteData() {
  if (checkRow.value.length > 0) {
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '你确认要删除吗?',
      onOk: async () => {
        let dateTime = new_Date()
        for (let i = 0; i < checkRow.value.length; i++) {
          const item = checkRow.value[i]
          item.isDel = "1"
          item.delName = useUserStore().getUserInfo['realName']
          item.delDate = dateTime
          await useRouteApi(saveProject, {schemaName: dynamicTenantId})(item)
        }
        // await useRouteApi(deleteProjectList, {schemaName: dynamicTenantId})(checkRow.value)
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

//获取当年月日
function new_Date() {
  let dateTime
  let yy = new Date().getFullYear()
  let mm = new Date().getMonth() + 1 < 10 ? '0' + (new Date().getMonth()+ 1) : (new Date().getMonth()+ 1)
  let dd = new Date().getDate() < 10 ? '0' + new Date().getDate() : new Date().getDate()
  let hh = new Date().getHours() < 10 ? '0' + new Date().getHours() : new Date().getHours()
  let mf = new Date().getMinutes() < 10 ? '0' + new Date().getMinutes() : new Date().getMinutes()
  let ss = new Date().getSeconds() < 10 ? '0' + new Date().getSeconds() : new Date().getSeconds()
  dateTime = yy + '-' + mm + '-' + dd + ' ' + hh + ':' + mf + ':' + ss
  // console.log(dateTime)
  return dateTime
}

//文件导出
async function exportExcel() {
  loadMark.value = true
  await reloadCurrentPage()
  const columnList = await useRouteApi(columnFindCate,{schemaName: dynamicTenantId})(cateCode.value)
  const arrHeader = columnList.map(item=>item.cname)
  const arrData = cardList.value.map((item) => columnList.map(column=>item[toHump(column.ctitle)]));
  // console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: arrData,
    header: arrHeader,
    filename: '项目信息_'+pageParameter.companyName+'.xlsx',
  });
  loadMark.value = false
}
function toHump(name:any) {
  return name.replace(/\_(\w)/g, function(all:any, letter:any){
    return letter.toUpperCase();
  });
}

/**********************栏目设置**********************/
import {initDynamics,assemblyDynamicColumn} from "./data";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  findDbLanMuList,
  saveLanMuList,
} from '/@/api/record/system/accvoucher';
import {
  getCurrentAccountName, getThisIndexImg, hasBlank
} from "/@/api/task-api/tast-bus-api";
import {cloneDeep} from "lodash-es";
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
// import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
const aoaToSheetXlsx=null
// import {getFromEdit} from "/@/api/record/system/sys_project_category";
import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findDataBase} from "/@/api/record/system/account";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {getPsnList} from "/@/api/record/system/psn";
import {getProjectItemList} from "/@/api/record/system/project-item";

const {createConfirm, createWarningModal} = useMessage();
const pageParameter: any = reactive({
  showRulesSize: 'MIN',
  companyCode: '',
  companyName: '',
  ifUnit: false,
  total: 0,
  projectClassCtl: '',
  itemCode: '',
  thisAdInfo: {}
})
/******************************************************************************************栏目设置**************************************************************************************************/
const windowWidth = (document.documentElement.clientWidth - (70 + 280))
const windowHeight = (window.innerHeight - (300))
const totalColumnWidth = ref(0)
const dynamicColumnData = ref({value: []})
const tableRef: any = ref(null)
const visible3 = ref(false);
const lanMuData = ref({
  accId: dynamicTenantId.value,
  menuName: '项目信息',
  type: '列表',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username,
  changeNumber: 0
})
const reloadColumns = () => {
  let newA = JSON.parse(JSON.stringify(CrudApi.columns))
  newA = assemblyDynamicColumn(dynamicColumnData.value.value, newA)
  setColumns(newA)
  initTableWidth(newA)
}
function initTableWidth(thisCs) {
  let total = 60
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })
  /*  // 去除操作咧宽
    total -= 100*/
  if (total > windowWidth) {
    let f = 0
    if (visible3.value) f = 260
    totalColumnWidth.value = Number(windowWidth) - f
    tableRef.value.$el.style.setProperty('width', (windowWidth + 40 - f) + 'px')
  } else {
    if (visible3.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 40) + 'px')
  }
}
/*栏目设置end*/
const isShowTree = ref(true)
const originId = ref('')
const dynamicAdReload = async (obj) => {
  // const dataBase: any = await findDataBase(obj.accId, obj.year)
  visible3.value=true
  loadMark.value = true
  dynamicTenantId.value = obj.accountMode
  pageParameter.companyName = obj.baseName
  const account:any = await getThisAdInfoData({'accId': obj.accId})
  if (null != account){
    originId.value = account.accGroup
    pageParameter.projectClassCtl = account.projectClassCtl
    if (account.independent == 0) {
      defaultPage.value = false
    } else {
      defaultPage.value = true
    }
    // pageParameter.companyName = account.accNameCn
    pageParameter.companyCode = account.coCode
    pageParameter.ifUnit = account.icorp == '1'
  }
  isShowTree.value = false
  const cate = await cateFindStateFlag()
  cateList.value = cate
  if (pageParameter.projectClassCtl=='1'){
    const items: any = await useRouteApi(getProjectItemList,{schemaName: dynamicTenantId})({})
    itemList.value = items
    if (itemList.value==null || itemList.value.length==0){
      isShowTree.value = true
      loadMark.value = false
      pageParameter.thisAdInfo = {}
      pageParameter.thisAdInfo = obj
      pageParameter.total = -1
      createWarningModal({title: '温馨提示', content: '暂无项目大类信息，请先添加项目大类档案！'});
      return false
    } else {
      pageParameter.itemCode = itemList.value[0].itemCode
    }
  }
  if (cateList.value != null && cateList.value.length > 0) {
    await reloadDept()
    await reloadPsn()
    await reloadProClass()
    isShowTree.value = true
    // cateList.value = cate
    cateCode.value = cateList.value[0].projectCateCode
    await reloadCurrentPage()
    /*let data: any = {}
    data.page = getPaginationRef().current
    data.size = getPaginationRef().pageSize
    data.projectCateCode = cateCode.value
    data.projectClassCtl = pageParameter.projectClassCtl
    data.itemCode = pageParameter.itemCode
    let res: any = await useRouteApi(findProjectByCateCode, {schemaName: obj.accountMode})(data)
    cardList.value = res.items
    setTableData([]) // 清空可能残留的数据
    setTableData(res.items)*/
    // 底部分页信息
    // useProjectStoreWidthOut().commitDynamicTenantId(obj.accountMode)
    pageParameter.thisAdInfo = {}
    pageParameter.thisAdInfo = obj
    // pageParameter.total = res.total
    // setPagination({total: res.total})
    pageParameter.total = tableData.value.length
    setPagination({total: tableData.value.length})
    loadMark.value = false
  } else {
    isShowTree.value = true
    loadMark.value = false
    pageParameter.thisAdInfo = {}
    pageParameter.thisAdInfo = obj
    pageParameter.total = -1
    createWarningModal({title: '温馨提示', content: '暂无项目样式信息，请先添加项目样式档案！'});
  }
}

import SelectProject from './popup/select-project.vue'
const [registerSelectProjectPage, { openModal: openSelectProjectPage }] = useModal()
const openSelectProject = () => {
  openSelectProjectPage(true, {
    dynamicTenantId: dynamicTenantId.value,
    projectClassCtl: pageParameter.projectClassCtl,
  })
}
const project:any = ref()
function saveSelectProject(data){
  project.value = data
}

import ProjectDel from './popup/project-del.vue'
const [registerProjectDelPage, { openModal: openProjectDelPage }] = useModal()
const openProjectDel = () => {
  openProjectDelPage(true, {
    dynamicTenantId: dynamicTenantId.value
  })
}
async function saveProjectDel(data){
  await reloadCurrentPage()
}

//引用
import Into from './popup/into.vue'
const [registerIntoPage, {openModal: openIntoPage}] = useModal()
const openInto= () => {
  openIntoPage(true, {
    originId: originId.value,
    dynamicTenantId: dynamicTenantId.value,
    projectClassCtl: pageParameter.projectClassCtl,
    itemCode: pageParameter.itemCode,
  })
}

async function saveInto(data) {
  await reloadCurrentPage()
}

//新增申请
import ApplyList from './popup/apply-list.vue'
const [registerApplyListPage, {openModal: openApplyListPage}] = useModal()
const openApplyList = () => {
  openApplyListPage(true, {
    originId: originId.value,
    dynamicTenantId: dynamicTenantId.value,
    projectClassCtl: pageParameter.projectClassCtl,
    itemCode: pageParameter.itemCode,
    cateCode: cateCode.value
  })
}

async function saveApplyList(data) {
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
    bottom: 240px;
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
  margin-bottom: 20px;
}

:deep(.ant-input),:deep(.ant-select),:deep(.ant-btn){
  border: 1px solid #c9c9c9;
}

</style>
