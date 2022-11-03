<template>
  <div>
  <div class="app-container">
      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false" >-->
      <div class="container-head-title" style="margin-left: 20px;">
        <b class="noneSpan"><UnorderedListOutlined style="font-size: 26px;" />&nbsp;&nbsp;集团-项目信息</b>
      </div>
      <!--      <div style="display: inline">
              项目大类<a-select v-model:value="cateCode" @change="useProjectStoreWidthOut().commitNum" class="head-index-select" placeholder="请选择项目大类">
                <a-select-option v-for="cate in cateList" :key="cate.projectCateCode" :value="cate.projectCateCode">
                  ({{ cate.projectCateCode }}){{ cate.projectCateName }}
                </a-select-option>
              </a-select>
            </div>-->

      <div
        class="ant-btn-group"
        style="float: right"
      >
        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="openApproveList()"
        ><span>生效审批</span></button>
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
          @click="editData()"
        ><span>编辑</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
          ant-click-animating-without-extra-node="false"
          @click="deleteData()"
        ><span>删除</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
          @click="openAssign()"
        ><span>分配</span></button>
        <button
          type="button"
          class="ant-btn ant-btn-me"
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
      </div>
      <div style="clear:both" />
      <div style="margin-top: 10px;">

        <div style="float: left;font-size: 16px;margin-left: 30px;margin-left: 20px;">
          项目样式：<a-select v-model:value="cateCode" @change="useProjectStoreWidthOut().commitNum" placeholder="请选择项目大类">
            <a-select-option v-for="cate in cateList" :key="cate.projectCateCode" :value="cate.projectCateCode">
              ({{ cate.projectCateCode }}){{ cate.projectCateName }}
            </a-select-option>
          </a-select>
        </div>
        <div style="float: left; margin-left: 10px;line-height: 30px;font-size: 14px;">共 {{tableDataAll.length}} 条记录</div>
        <div style="float: right; margin-left: 10px">
          <a-button @click="reloadTable()">
            <SyncOutlined :style="{ fontSize: '14px' }" />
          </a-button>
          <a-popover class="ant-btn-default" placement="bottom">
            <template #content>
              <a-popconfirm
                ok-text="确定"
                cancel-text="放弃"
                @confirm="confirm"
                @cancel="cancel">
                <template #icon><b>栏目设置</b><br></template>
                <template #title>
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
    </div>
    <div style="clear:both" />

  </div>
  <div class="app-container">
    <PageWrapper dense content-full-height fixed-height content-class="flex">
      <ClassTree class="w-1/4 xl:w-1/5" @select="handleSelect" v-model="pageParameter" />
      <div style="width: calc(100% - 250px); float: right;margin-left: 5px;">
        <BasicTable
          ref="tableRef"
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
          :scroll="{ x: totalColumnWidth,y: windowHeight }"
          :dataSource="tableData"
          :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
          @row-click="condClick"
          @register="registerTable"
          :loading="loadMark"
        >
          <template #successState="{ record }">
            <span v-if="record.successState == '1'">
              <a-tag color="green">已生效</a-tag>
            </span>
            <span v-if="record.successState == '0'">
              <a-tag color="warning">未生效</a-tag>
            </span>
            <span v-if="record.successState == '-1'">
              <a-tag color="volcano">已驳回</a-tag>
            </span>
          </template>
          <template #projectCode="{ record }">
            <a class="tableUStyle" @click="openEdit(record)">{{ record.projectCode }}</a>
          </template>
          <template #projectName="{ record }">
            <a class="tableUStyle" @click="openEdit(record)">{{ record.projectName }}</a>
          </template>
          <template #jiesuan="{ record }">
              <span>
                  {{ record.jiesuan === '1' ? '是' : '否' }}
              </span>
          </template>
          <template #projectClassCode="{ record }">
            {{ formatProjectClassCode(record.projectClassCode) }}
          </template>
          <template #psnInCharge="{ record }">
            {{ formatPsnInCharge(record.psnInCharge) }}
          </template>
<!--          <template #deptCode="{ record }"> {{ formatDeptCode(record.deptCode) }}</template>-->
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
      </div>
      <Edit
        @save="saveData"
        @register="registerEditPage"
      />
      <Excel
        @save="saveExcel"
        @register="registerExcelPage"
      />
      <ProjectDel @save="saveProjectDel" @register="registerProjectDelPage"/>
      <Assign @save="saveAssign" @register="registerAssignPage"/>
      <ApproveList @save="saveApproveList" @register="registerApproveListPage"/>
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
  ,SortDescendingOutlined,
  UnorderedListOutlined
} from '@ant-design/icons-vue'
import {computed, nextTick, onMounted, reactive, ref, watch} from 'vue'
import {
  getProjectList,
  deleteProject,
  saveProject,
  excelProject,
  deleteProjectList,
  findProjectByCateCode
} from '/@/api/record/system/group-project'
import Edit from './popup/edit.vue'
import Excel from './popup/excel.vue'
import {cateFindStateFlag} from "/@/api/group_project_category/project_category";
import { useProjectStoreWidthOut } from '/@/store/modules/group-project'
// import {getDeptListById} from "/@/api/record/system/dept";
import {getPsnList, psnFindAll} from "/@/api/record/system/group-psn";
import {projectClassFindAll} from "/@/api/record/system/group_project_class";
import {Select as ASelect,Input as AInput,Popover as APopover,Tag as ATag,   Radio as ARadio,
  Table as ATable,  Checkbox as ACheckbox,
  Popconfirm as APopconfirm,
  message, } from 'ant-design-vue'
import {useUserStore} from "/@/store/modules/user";
import {findByUniqueCode} from "/@/api/record/system/group-project-account";

const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option
const {
  createErrorModal,
  createSuccessModal
} = useMessage()

const {closeCurrent} = useTabs(router);

const formItems = ref({
  selectType: 'projectCode',
  selectValue: ''
})
const tableData:any = ref([]);
const tableDataAll:any = ref([]);
//选择项目大类
const cateCode:any = ref('01')
const currentCateCode=computed(()=>useProjectStoreWidthOut().getNum)

watch(currentCateCode,async (abc) => {
  // projectStore.projectClassTree()

  /*reload({
    searchInfo:{
      projectCateCode:abc
    }
  })*/
  await reloadCurrentPage()
})
//根据基础档案查询基础档案栏目
const cateList:any = ref([])
async function reloadCate() {
  const res: any = await cateFindStateFlag()
  cateList.value = res
  // if (res.length>0) {
  //   cateCode.value = res[0].projectCateCode
  //   // console.log(currentCateCode)
  // }
}

const classCode:any = ref('')
async function handleSelect(keys: string) {
  // console.log("选择的数据是"+keys)
  /*classCode.value = keys
  reload({
    searchInfo:{
      projectCateCode:cateCode.value,
      projectClassCode:classCode.value
    }
  })*/
  classCode.value = keys
  if (classCode.value != null && classCode.value != '') {
    /*if (num==0 && classCode.value!='') {
      reloadTable()
    }*/
    loadMark.value = true
    cardList.value = tableDataAll.value.filter(item => {
      if (classCode.value != null && classCode.value != '' && classCode.value != '0') {
        return item.projectClassCode == classCode.value
      }
      return item
    })
    tableData.value = cardList.value
    await setPagination({
      total: tableData.value.length
    })
    loadMark.value = false
  }
}

const CrudApi = {
  list: getProjectList,
  columns: [
    {
      title: '生效状态',
      dataIndex: 'successState',
      width: 100,
      ellipsis: true,
      fixed: 'left',
      slots: {customRender: 'successState'}
    },
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
    /*{
      title: '所属部门',
      dataIndex: 'deptCode',
      ellipsis: true,
      slots: { customRender: 'deptCode' }
    }, */
    {
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
    /*{
      title: '是否生效',
      dataIndex: 'successState',
      ellipsis: true,
      slots: { customRender: 'successState' }
    },*/
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
      if (proClass.projectClassCode == projectClassCode && proClass.projectCateCode == cateCode.value) {
        // console.log(proClass)
        str = proClass.projectClassName
      }
    }
  )
  return str
}
//部门
/*const deptList = ref([])
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
}*/
//人员
const psnList:any = ref([])
async function reloadPsn() {
  const res:any = await getPsnList()
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
  await reloadCate()
  // await reloadDept()
  await reloadPsn()
  await reloadProClass()
  await resetDynamicColumnData()
  await reloadCurrentPage()
})
// 这是示例组件
const [registerTable, { reload ,setTableData,getDataSource,setColumns,getColumns,getPaginationRef,setPagination}] = useTable({
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
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['10', '25', '50', '100'],
    showTotal: t => `总共${t}条数据`
  },
  /*actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  },*/
  searchInfo:{
    projectCateCode:currentCateCode.value,
    projectClassCode:classCode.value,
  }
})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const [registerExcelPage, { openModal: openExcelPage }] = useModal()
const val = {
  id: '',
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
  itemCode: '',
  isState: '0'
}

const openAddPage = () => {
  if (cateList.value.length > 0) {
    val.projectCateCode = cateCode.value
    val.projectClassCode = classCode.value
    val.isEdit = true
    val.isState = '0'
    openEditPage(true, {
      data: val
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '提示',
      content: '暂无项目大类信息，请先添加项目大类档案！'
    })
  }
}
const openEdit = (data:any) => {
  data.isEdit=false
  data.isState = '2'
  openEditPage(true, {
    data
  })
}
const openExcel = () => {
  openExcelPage(true, {
    data: {
      projectCateCode:cateCode.value,
    }
  })
}
//导入Excel
const loadMark = ref(false)
async function saveExcel(data:any){
  loadMark.value = true
  await excelProject(data)
  await reloadTable()
  loadMark.value = false
}
const del = async(data:any) => {
  data.projectCateCode=cateCode.value
  await deleteProject(data)
  // alert('删除成功！')
  createSuccessModal({
    iconType: 'warning',
    title: '删除',
    content: '删除成功！'
  })
  await reloadTable()
}
async function saveData(data:any) {
  await saveProject(data)
  await reloadTable()
}

async function reloadTable() {
  await reload({
    searchInfo: {
      projectCateCode: cateCode.value,
      projectClassCode: classCode.value
    }
  })
  checkRow.value = []
  state.selectedRowKeys = []
  await reloadCurrentPage()
}
const cardList:any = ref([])
async function reloadCurrentPage() {
  loadMark.value = true
  const res:any = await findProjectByCateCode(cateCode.value)
  tableDataAll.value = res
  cardList.value = tableDataAll.value.filter(item =>{
    if (classCode.value!=null && classCode.value!='' && classCode.value!='0') {
      return item.projectClassCode==classCode.value
    }
    return item
  })
  tableData.value = cardList.value
  await setPagination({
    total: tableData.value.length
  })
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
    return item
  })
  // console.log(a)
  // setDataSource(a)
  await setPagination({
    total: tableData.value.length
  })
  loadMark.value = false
}
function condClick(){}

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
  console.log(checkRow.value)
};
function editData(){
  if (checkRow.value.length==1) {
    checkRow.value[0].isEdit = true
    checkRow.value[0].isState = '1'
    openEditPage(true, {
      data: checkRow.value[0],
      isState: '1'
    })
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '修改失败',
      content: '只能选择一条进行修改！'
    })
  }
}
async function deleteData(){
  if (checkRow.value.length > 0) {
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      const res = await findByUniqueCode(item.uniqueCode)
      if (res.length > 0){
        createWarningModal({
          iconType: 'warning',
          title: '删除',
          content: '<span style="font-weight: bold;color: red;">[' + item.projectName + ']</span>项目信息已分配不允许删除！'
        })
        return false
      }
    }
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
          await saveProject(item)
        }
        // await deleteProjectList(checkRow.value)
        checkRow.value = []
        state.selectedRowKeys = []
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
  const columnList = await getFromEdit(cateCode.value)
  const arrHeader = cardList.value.map(item=>item.cname)
  const arrData = cardList.value.map((item) => columnList.map(column=>item[toHump(column.ctitle)]));
  // console.log(arrHeader)
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: arrData,
    header: arrHeader,
    filename: '项目档案.xlsx',
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
  getCurrentAccountName,getThisIndexImg
} from "/@/api/task-api/tast-bus-api";
import {cloneDeep} from "lodash-es";
import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
import {getFromEdit} from "/@/api/record/system/group-project";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
const { createConfirm,createWarningModal } = useMessage();
const pageParameter = reactive({
  showRulesSize: 'MIN',
})
const visible = ref(false);
const windowWidth  = (document.documentElement.clientWidth -(70+280))
const windowHeight  = (document.documentElement.clientHeight -(300))
const totalColumnWidth = ref(0)
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData:any = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});
const tableRef:any = ref(null)
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
    let arr:any = key.split('-');
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
    let arr:any = key.split('-');
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
    let arr:any = key.split('-');
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
    }catch (e:any) {
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
  let a:any = []
  a = getColumns()
  let newA = JSON.parse(JSON.stringify(CrudApi.columns))
  newA= assemblyDynamicColumn(dynamicColumnData.value,newA)
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
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })
  /*  // 去除操作咧宽
    total -= 100*/
  // debugger
  if (total > windowWidth) {
    let f = 0
    if (visible.value) f = 260
    totalColumnWidth.value = Number(windowWidth) - f
    tableRef.value.$el.style.setProperty('width', (windowWidth + 70 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 70) + 'px')
  }
}
const pageReload =  () =>{
  setTableData([]) // 清空可能残留的数据
  reload({
    searchInfo: pageParameter
  })
}
/*栏目设置end*/

//回收站
import ProjectDel from './popup/project-del.vue'
const [registerProjectDelPage, { openModal: openProjectDelPage }] = useModal()
const openProjectDel = () => {
  openProjectDelPage(true, {})
}
async function saveProjectDel(data){
  await reloadCurrentPage()
}

//分配
import Assign from './popup/assign.vue'
const [registerAssignPage, {openModal: openAssignPage}] = useModal()
const openAssign = () => {
  openAssignPage(true, {})
}

async function saveAssign(data) {
}

//生效审批
import ApproveList from './popup/approve-list.vue'
const [registerApproveListPage, {openModal: openApproveListPage}] = useModal()
const openApproveList = () => {
  openApproveListPage(true, {})
}

async function saveApproveList(data) {
}

</script>
<style src="./global-menu-index.less" lang="less" scoped></style>

<style lang="less" scoped>
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
  padding: 5px 8px !important;
  border-color: #aaaaaa !important;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 4px 8px !important;
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
