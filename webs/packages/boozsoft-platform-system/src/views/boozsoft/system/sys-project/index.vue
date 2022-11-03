<template>
  <div class="app-container">
      <div class="app-container-head"><img :src="getThisIndexImg()" class="container-head-img" draggable="false" >
      <div class="container-head-title">
        <b class="noneSpan">项目信息</b>
      </div>
      <div style="display: inline">
        <a-select v-model:value="cateCode" class="head-index-select" placeholder="请选择项目大类">
          <a-select-option v-for="cate in cateList" :key="cate.projectCateCode" :value="cate.projectCateCode">
            ({{ cate.projectCateCode }}){{ cate.projectCateName }}
          </a-select-option>
        </a-select>
      </div>

      <div class="ant-btn-group" data-v-a1ccd506="" style="float: right">
        <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false" @click="openAddPage()"><span>新建</span></button>
        <button type="button" class="ant-btn ant-btn-me"><span>导入</span></button>
        <button type="button" class="ant-btn ant-btn-me"><span>导出</span></button>
        <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false"><span>打印</span></button>
      </div>
    </div>

    <div class="app-container-neck">
      <div style="float: right; margin-left: 10px">
        <a-button style="padding: 0px 12px !important;margin-right: 10px;">
          <SyncOutlined :style="{ fontSize: '14px' }" />
        </a-button>
        <a-popover placement="bottom">
          <template #content>
            <a-popconfirm
              ok-text="确定"
              cancel-text="放弃"
              @confirm="confirm"
              @cancel="cancel"
            >
              <template #icon><b>栏目设置</b><br></template>
              <template #title>
                <a-table bordered :data-source="dynamicColumnData" :columns="dynamicColumns"
                         childrenColumnName="children" :pagination="false">
                  <template #checkBox="{ text, record }">
                    <a-checkbox v-model:checked="record.check" :disabled="record.isFixed"/>
                  </template>
                  <template #widthInput="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]" class="editable-cell-input-wrapper">
                        <a-input type="number" v-model:value="editableData[record.key].width"
                                 @pressEnter="save(record.key,record.min,record.max)" style="width: 80px"/>
                        <EditOutlined class="editable-cell-icon-check"
                                      @click="save(record.key,record.min,record.max)"/>
                      </div>
                      <div v-else class="editable-cell-text-wrapper">
                        {{ text || ' ' }}
                        <EditOutlined class="editable-cell-icon" @click="edit(record.key)"/>
                        <span style="float: right;">{{ record.min + '~' + record.max }}</span>
                      </div>
                    </div>
                  </template>
                  <template #nameInput="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]" class="editable-cell-input-wrapper">
                        <a-input type="text" v-model:value="editableData[record.key].nameNew"
                                 @pressEnter="saveName(record.key)" style="width: 100px"/>
                        <EditOutlined class="editable-cell-icon-check"
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
              </template>
              <a-button style="width: 165px;border: none">栏目设置</a-button>
            </a-popconfirm>
            <br/>
            <span @click="pageParameter.showRulesSize = 'MAX'"
                  :style="{backgroundColor: (pageParameter.showRulesSize==='MAX')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortDescendingOutlined
              :style="{ fontSize: '14px' }"/>&emsp;大号字体&emsp;<CheckOutlined v-if="pageParameter.showRulesSize==='MAX'"
                                                                            :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
            <span @click="pageParameter.showRulesSize = 'MIN'"
                  :style="{backgroundColor: (pageParameter.showRulesSize==='MIN')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;&emsp;<SortAscendingOutlined
              :style="{ fontSize: '14px' }"/>&emsp;小号字体&emsp;<CheckOutlined v-if="pageParameter.showRulesSize==='MIN'"
                                                                            :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
          </template>
          <template #title>
            <b>设置表格字号</b>
          </template>
          <a-button>
            <SettingFilled :style="{ fontSize: '14px' }"/>
          </a-button>
        </a-popover>
<!--        <a-popover placement="bottom">
          <a-button>
            <PicLeftOutlined :style="{ fontSize: '14px' }" />
          </a-button>
        </a-popover>-->

        <a-button>
          <EditFilled :style="{ fontSize: '14px' }" />
        </a-button>

<!--        <a-button>
          <PieChartFilled :style="{ fontSize: '14px' }" />
        </a-button>
        <a-button>
          <FilterFilled :style="{ fontSize: '14px' }" />
        </a-button>-->
      </div>
      <div style="float: right; position: relative">
        <!-- 搜索 -->
        <a-input-search placeholder="" style="width: 200px; border-radius: 4px" @search="onSearch" />
      </div>
    </div>
    <Edit @save="saveData" @register="registerEditPage" />
    <Approve
      @approveOk="approveOkDate"
      @register="registerApprovePage"
    />
    <BasicTable
      class="w-4/5 xl:w-5/6"
      ref="tableRef"
      :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
      :scroll="{ x: totalColumnWidth,y: windowHeight }"
      :row-selection="{ type: 'checkbox' ,fixed: true}" @row-click="condClick" @register="registerTable">
      <template #jiesuan="{ record }">
          <span>
              {{ record.jiesuan === '1' ? '是' : '否' }}
          </span>
      </template>
      <template #successState="{ record }">
          <span>
            <a-tag
              :color="record.successState === '1' ? 'green' : 'volcano'"
            >
              {{ record.successState === '1' ? '已生效' : '未生效' }}
            </a-tag>
          </span>
      </template>
      <template #projectClassCode="{ record }"> {{ formatProjectClassCode(record.projectClassCode) }} </template>
      <template #psnInCharge="{ record }"> {{ formatPsnInCharge(record.psnInCharge) }} </template>
      <template #deptCode="{ record }"> {{ formatDeptCode(record.deptCode) }} </template>
      <template #action="{ record }">
        <div>
          <a-popover placement="bottom">
            <a-button style="padding: 0px 4px; height: 20px;">
              <CaretDownFilled />
            </a-button>
            <template #content>
              <p v-if="record.successState==='0'" style="cursor: pointer" @click="openApprove(record)"><FormOutlined /> 审批</p>
              <p style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>
              <p style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>
            </template>
          </a-popover>
        </div>
      </template>
    </BasicTable>

  </div>
</template>
<script setup lang="ts">
import { BasicTable, useTable } from '/@/components/Table'

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
  FilterFilled,EditOutlined,SortAscendingOutlined,CheckOutlined,SortDescendingOutlined
} from '@ant-design/icons-vue'
import {computed, onMounted, ref,reactive, watch} from 'vue'
import {getProjectList, deleteProject, sysSaveProject, approveProject} from '/@/api/record/system/sys_project'
import Edit from './popup/edit.vue'
import Approve from './popup/approve.vue'
import {cateFindStateFlag} from "/@/api/project_category/project_category";
import { useProjectStoreWidthOut } from '/@/store/modules/project'
import {getDeptListById} from "/@/api/record/system/dept";
import {psnFindAll} from "/@/api/psn/psn";
import {projectClassFindAll} from "/@/api/record/system/project_class";
import {Select as ASelect,Input as AInput,Popover as APopover,Tag as ATag,   Popconfirm as APopconfirm,
  Radio as ARadio,Checkbox as ACheckbox,
  Table as ATable,message } from 'ant-design-vue'
const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option

//选择项目大类
const cateCode:any = ref()
const currentCateCode=computed(()=>cateCode.value)

watch(currentCateCode,(abc)=>{
  // projectStore.projectClassTree()
  reload({
    searchInfo:{
      projectCateCode:abc
    }
  })
})
//根据基础档案查询基础档案栏目
const cateList:any = ref([])
async function reloadCate() {
  const res: any = await cateFindStateFlag()
  cateList.value = res
  if (res.length>0) {
    cateCode.value = res[0].projectCateCode
    // console.log(currentCateCode)
  }
}
onMounted(async() => {
  await reloadCate()
})

const CrudApi = {
  list: getProjectList,
  columns: [
    {
      title: '项目编码',
      dataIndex: 'projectCode',
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
    },  {
      title: '开始日期',
      dataIndex: 'startDate',
      ellipsis: true
    },
    {
      title: '结束日期',
      dataIndex: 'endDate',
      ellipsis: true
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
    {
      title: '是否生效',
      dataIndex: 'successState',
      ellipsis: true,
      slots: { customRender: 'successState' }
    },
  ],
  editData: {
    id: '',
    uniqueCode: '',
    projectCode: '',
    projectName: '',
    projectClassCode: '',
    jiesuan: '',
    startDate: '',
    endDate: '',
    psnInCharge: '',
    deptCode: ''
  }
}
//项目分类
const proClassList = ref([])
async function reloadProClass() {
  const res:any = await projectClassFindAll();
  proClassList.value = res
  // console.log(proClassList.value)
}
function formatProjectClassCode(projectClassCode:any){
  let str = projectClassCode
  // console.log(projectClassCode)
  proClassList.value.forEach(
    function (proClass:any) {
      if (proClass.uniqueCode == projectClassCode){
        // console.log(proClass)
        str = proClass.projectClassName
      }
    }
  )
  return str
}
//部门
const deptList = ref([])
async function reloadDept() {
  const res:any = await getDeptListById()
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
const psnList = ref([])
async function reloadPsn() {
  const res:any = await psnFindAll()
  psnList.value = res.items
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
  resetDynamicColumnData()
})
// 这是示例组件
const [registerTable, { reload,setTableData,setColumns,getColumns }] = useTable({
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
  actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  },
  searchInfo:{
    projectCateCode:'01',
    projectClassCode:''
  }
})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const [registerApprovePage, { openModal: openApprovePage }] = useModal()
const val = {
  id: '',
  uniqueCode: '',
  projectCode: '',
  projectName: '',
  projectClassCode: '',
  jiesuan: '',
  startDate: '',
  endDate: '',
  psnInCharge: '',
  deptCode: ''
}

const openAddPage = () => {
  openEditPage(true, {
    data: {val,
      projectCateCode: cateCode.value
    }
  })
}
const openEdit = (data:any) => {
  openEditPage(true, {
    data
  })
}
const openApprove = (data:any) => {
  openApprovePage(true, {
    data: data
  })
}
const del = async(data:any) => {
  data.projectCateCode=cateCode.value
  await deleteProject(data)
  alert('删除成功！')
  reload()
}
async function saveData(data:any) {
  await sysSaveProject(data)
  reload()
}
async function approveOkDate(data:any) {
  await approveProject(data)
  reload()
}
function onSearch(){}
function condClick(){}
/**********************栏目设置**********************/
import {initDynamics,assemblyDynamicColumn} from "./data";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  findDbLanMuList,
  saveLanMuList,
} from '/@/api/record/system/accvoucher';
import {
  getCurrentAccountName,getThisIndexImg
} from "/@/api/task-api/tast-bus-api";
import {cloneDeep} from "lodash-es";
import {useUserStoreWidthOut} from "/@/store/modules/user";
const { createConfirm,createWarningModal } = useMessage();
const pageParameter = reactive({
  showRulesSize: 'MIN',
})
const visible = ref(false);
const windowWidth  = (document.documentElement.clientWidth -(70/*+280*/))
const windowHeight = (window.innerHeight - (350))
const totalColumnWidth = ref(0)
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});
const tableRef = ref(null)
const lanMuData = {'accId':'','menuName':'项目信息','type':'集团',objects: '',username:useUserStoreWidthOut().getUserInfo.username}
const confirm = (e: MouseEvent) => {
  // 询问
  createConfirm({
    iconType: 'warning',
    title: '栏目同步',
    content: '是否将刚才设置同步数据库!',
    onOk: async() => {
      // 调整数据库 列参数
      lanMuData.accId = getCurrentAccountName(false)
      lanMuData.objects = JSON.stringify(filterModifyData(dynamicColumnData.value, dynamicColumnDataCopy))
      if (lanMuData.objects == '[]'){
        createWarningModal({content: '请先做修改后再进行确认同步数据库！'})
      }else {
        saveLanMuList(lanMuData).then(res=>{
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
    let arr = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      editableData[key] = cloneDeep(dynamicColumnData.value[one].children.filter(item => key === item.key)[0]);
    } else {
      let two = parseInt(arr[1] - 1)
      editableData[key] = cloneDeep(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0]);
    }
  } else {
    editableData[key] = cloneDeep(dynamicColumnData.value.filter(item => key === item.key)[0]);
  }
}
const save = (key: string, min: number, max: number) => {
  editableData[key].width = editableData[key].width > max ? max : editableData[key].width < min ? min : editableData[key].width
  if (key.toString().indexOf('-') != -1) {
    let arr = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
    } else {
      let two = parseInt(arr[1] - 1)
      Object.assign(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0], editableData[key]);
    }
  } else {
    Object.assign(dynamicColumnData.value.filter(item => key === item.key)[0], editableData[key]);
  }
  delete editableData[key];
}
const saveName = (key: string) => {
  if (key.toString().indexOf('-') != -1) {
    let arr = key.split('-');
    let one = parseInt(arr[0])
    if (arr.length == 2) {
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
      Object.assign(dynamicColumnData.value[one].children.filter(item => key === item.key)[0], editableData[key]);
    } else {
      let two = parseInt(arr[1] - 1)
      Object.assign(dynamicColumnData.value[one].children[two].children.filter(item => key === item.key)[0], editableData[key]);
    }
  } else {
    Object.assign(dynamicColumnData.value.filter(item => key === item.key)[0], editableData[key]);
  }
  delete editableData[key];
}
function filterModifyData(lanMuList:any,copyList) {
  let a =  lanMuList.filter(item=> {
    try {
      copyList.forEach(item2=>{
        if (item.key === item2.key && item.name == item2.name){
          if (item.nameNew != item2.nameNew || item.width != item2.width || item.check != item2.check || item.align != item2.align)
            throw new Error('ok')
        }
      })
      return false
    }catch (e) {
      if (e.message == 'ok'){
        return  true
      }else {
        return  false
      }
    }
  })
  return a;
}
function combineParameters(staticList:any,dbList:any) {
  staticList.forEach(item=>{
    dbList.forEach(item2=>{
      if (item.key === item2.key && item.name === item2.name){
        item.nameNew = item2.nameNew
        item.width = parseInt(item2.width)
        item.check = item2.check == 'true'
        item.align = item2.align
      }
    })
  })
  return staticList
}
const reloadColumns = ()=>{
  let a = []
  a = getColumns()
  let last = a.pop()
  let newA = JSON.parse(JSON.stringify(CrudApi.columns))
  newA= assemblyDynamicColumn(dynamicColumnData.value,newA)
  newA.push(last)
  setColumns(newA)
  initTableWidth(newA)
  //searchConditonList.value = newA
}
function resetDynamicColumnData() {
  // 先从数据查询是否已经设置
  lanMuData.accId = getCurrentAccountName(false)
  findDbLanMuList(lanMuData).then(res=>{
    // 栏目列
    let dbList = res.items
    if (dbList.length > 0){
      let statiList = initDynamics()['DATA']
      dbList = combineParameters(statiList,dbList)
      dynamicColumnData.value = dbList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(dbList))
    }else {
      let statiList = initDynamics()['DATA']
      dynamicColumnData.value = statiList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(statiList))
    }
    // 表格列
    reloadColumns()
    //pageReload()
  })
}
function initTableWidth(thisCs){
  let total = 60
  // debugger
  thisCs.forEach(item=>{
    if (item.ifShow== null || item.ifShow)
      total+= parseInt(item.width)
  })
  /*  // 去除操作咧宽
    total -= 100*/
  if (total > windowWidth){
    let f = 0
    if (visible.value) f=260
    totalColumnWidth.value = Number(windowWidth)-f
    tableRef.value.$el.style.setProperty('width',(windowWidth+20-f)+'px')
  }else {
    if (visible.value && (windowWidth-260) < total) total-=(total-(windowWidth-260))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width',(total+20)+'px')
  }
}
const pageReload =  () =>{
  setTableData([]) // 清空可能残留的数据
  reload({
    searchInfo: pageParameter
  })
}
/*栏目设置end*/
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>

<style scoped>
.vben-basic-table {
  width: 99%;
}
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
