<template>
  <div class="app-container">
      <div class="app-container-head"><img :src="getThisIndexImg()" class="container-head-img" draggable="false" >
      <div class="container-head-title">
        <b class="noneSpan">集团信息</b>
      </div>
      <div class="ant-btn-group" data-v-a1ccd506="" style="float: right">
        <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false" @click="openAddPage()"><span>新建</span></button>
<!--        <button type="button" class="ant-btn ant-btn-me"><span>导入</span></button>
        <button type="button" class="ant-btn ant-btn-me"><span>导出</span></button>
        <button type="button" class="ant-btn ant-btn-me" ant-click-animating-without-extra-node="false"><span>打印</span></button>-->
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
                <div style="width: 640px"><a-table bordered :data-source="dynamicColumnData" :columns="dynamicColumns"
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
                </a-table></div>
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
<!--        <a-button>
          <EditFilled :style="{ fontSize: '14px' }" />
        </a-button>-->
        <a-button @click=" activeKey = !activeKey,cardList = getDataSource();">
          <PieChartFilled :style="{ fontSize: '14px' }" />
        </a-button>
<!--
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
    <BasicTable
      class="w-4/5 xl:w-5/6"
      v-if="activeKey"
      ref="tableRef"
      :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
      :scroll="{ x: totalColumnWidth,y: windowHeight }"
      :row-selection="{ type: 'checkbox' ,fixed: true}" @register="registerTable">
      <template #groupName="{ record }"> <u class="tableUStyle" @click="openEdit(record,true)">{{ record.groupName}}</u> </template>
      <template #successState="{ record }">
          <span>
            <a-tag
              :color="record.flag === '1' ? 'green' : 'volcano'"
            >
              {{ record.flag === '1' ? '启用' : '停用' }}
            </a-tag>
          </span>
      </template>
      <template #industryclassCode="{ record }"> {{ formatHyInCharge(record.industryclassCode) }} </template>
      <template #uniqueCodeZone="{ record }"> {{ formatXzInCharge(record.uniqueCodeZone) }} </template>
      <template #countryId="{ record }"> {{ formatDqInCharge(record.countryId) }} </template>
      <template #icorp="{ record }"> {{ record.icorp=='1'?'公司':'非公司' }} </template>
      <template #website="{ record }"> <a class="tableUStyle" @click="toWeb(record.website)" >{{ record.website}}</a></template>
      <template #action="{ record }">
        <div>
          <a-popover placement="bottom">
            <a-button style="padding: 0px 4px; height: 20px;">
              <CaretDownFilled />
            </a-button>
            <template #content>
              <p style="cursor: pointer" @click="openEdit(record,false)"><FormOutlined /> 编辑</p>
              <p style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>
            </template>
          </a-popover>
        </div>
      </template>
    </BasicTable>
    <div
      v-if="!activeKey"
      :class="cardList"
    >
      <div :class="`${cardList}__content`">
        <!--        <div>ID:{{ item.id }}</div>-->
        <a-list style="height: 700px;overflow-y: scroll;overflow-x: hidden;">
          <a-row :gutter="16">
            <template
              v-for="(item) in cardList"
            >
              <a-col :span="8">
                <a-list-item style="width: 95%">
                  <a-card
                    :hoverable="true"
                    :class="`${cardList}__card`"
                    style="width: 100%"
                  >
                    <div class="abc" style="width: 100%;float: right;" @click="openEdit(item)">
                      <div style="width: 88px;height: 88px;display: inline-block">
                        <img  :src="item.beiyong1" alt="avatar"/>
                      </div>
                      <div style="width: calc(100% - 88px);display: inline-block;float: right;">
                        <div style="float: right; border: 1px solid rgb(1, 143, 251);border-radius: 10px;padding: 0 10px; margin-top: -5px;">
                          {{ item.groupCode }}
                        </div>
                        <div style="font-size: 24px;margin-top: 20px;">
                          {{ item.groupName }}
                        </div>
                      </div>
                      <div style="font-size: 14px;" >
                        行业性质：{{ formatHyInCharge(item.industryclassCode) }}
                      </div>
<!--                      <div style="float: right;">
                        {{ item.sysFlag === '1' ? '系统模板' : '自定义模板' }}
                      </div>-->
                    </div>
                  </a-card>
                </a-list-item>
              </a-col>
            </template>
          </a-row>
        </a-list>
      </div>

    </div>
  </div>
</template>
<script setup lang="ts">
import { BasicTable, useTable } from '/@/components/Table'

import { useModal } from '/@/components/Modal'
import {
  CaretDownFilled,
  FormOutlined,
  DeleteOutlined,
  SettingFilled,PieChartFilled,
  SyncOutlined,
  PicLeftOutlined,
  EditFilled,
  FilterFilled,EditOutlined,SortAscendingOutlined,CheckOutlined,SortDescendingOutlined
} from '@ant-design/icons-vue';
import {computed, onMounted, ref,reactive, watch} from 'vue'
import {getGroupList, deleteGroup, saveGroup, findAllIndustry} from '/@/api/record/group/im-group'
import Edit from './popup/edit.vue'
import {cateFindStateFlag} from "/@/api/project_category/project_category";
import { useProjectStoreWidthOut } from '/@/store/modules/project'
import {getDeptListById} from "/@/api/record/system/dept";
import {psnFindAll} from "/@/api/psn/psn";
import {projectClassFindAll} from "/@/api/record/system/project_class";
import {Select as ASelect,Input as AInput,Popover as APopover,Tag as ATag,   Popconfirm as APopconfirm,
  Radio as ARadio,Checkbox as ACheckbox,List as AList,Row as ARow,Col as ACol ,Card as ACard,
  Table as ATable,message } from 'ant-design-vue'
const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option
const AListItem=AList.Item
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
const cardList:any = ref([])
const activeKey = ref(true)
/*async function reloadCate() {
  const res: any = await cateFindStateFlag()
  cateList.value = res
  if (res.length>0) {
    cateCode.value = res[0].projectCateCode
    // console.log(currentCateCode)
  }
}*/
const toWeb = (u) => {
  window.open('http://'+u)
}
const CrudApi = {
  list: getGroupList,
  columns: [
    {
      title: '集团代码',
      dataIndex: 'groupCode',
      fixed: true,
      ellipsis: true
    },
    {
      title: '集团名称',
      dataIndex: 'groupName',
      ellipsis: true,
      fixed: true,
      slots: { customRender: 'groupName' }
    },
    {
      title: '所属品牌',
      dataIndex: 'brand',
      ellipsis: true,
    },
    {
      title: '所属行业',
      dataIndex: 'industryclassCode',
      ellipsis: true,
      slots: { customRender: 'industryclassCode' }
    },
    {
      title: '行政区划',
      dataIndex: 'uniqueCodeZone',
      ellipsis: true,
      slots: { customRender: 'uniqueCodeZone' }
    },
    {
      title: '国家(地区)',
      dataIndex: 'countryId',
      ellipsis: true,
      slots: { customRender: 'countryId' }
    },
    {
      title: '成立日期',
      dataIndex: 'createDate',
      ellipsis: true
    },
    {
      title: '负责人',
      dataIndex: 'contacts',
      ellipsis: true,
      slots: { customRender: 'contacts' }
    },
    {
      title: '联系电话',
      dataIndex: 'telephone',
      ellipsis: true,
    },
    {
      title: '地址',
      dataIndex: 'address',
      ellipsis: true,
    },
    {
      title: '官网',
      dataIndex: 'website',
      ellipsis: true,
      slots: { customRender: 'website' }

    }, {
      title: '集团性质',
      dataIndex: 'icorp',
      ellipsis: true,
      slots: { customRender: 'icorp' }
    },
    {
      title: '简介',
      dataIndex: 'remarks',
      ellipsis: true,
    },
  ],
  editData: {
    id: '',
    uniqueCode: '',
    groupCode: '',
    groupName: '',
    brand: '',
    industryclassCode: '',
    uniqueCodeZone: '',
    countryId: '',
    createDate: '',
    contacts: '',
    telephone: '',
    address: '',
    website: '',
    icorp: '',
    remarks: '',
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

function formatDqInCharge(psnInCharge:any){
  if ( psnInCharge == '' || null == psnInCharge) {
    return ""
  }else {
    let str =""
    countryList.value.forEach((item)=>{
      if (item.uniqueCode == psnInCharge)str = item.namech
    })
    return str
  }
}

function formatHyInCharge(psnInCharge:any){
  if ( psnInCharge == '' || null == psnInCharge) {
    return ""
  }else {
    let arr = JSON.parse(psnInCharge);
    let str = ""
    if (arr.length >= 1){
      industryList.value.forEach((item)=>{
        if (item.value == arr[0]){
          str += item.label
          if (item.children.length > 0){
            item.children.forEach((item1)=>{
              if (item1.value == arr[1]){
                str = str +' / '+ item1.label
              }
            })
          }
        }
      })
    }
    return str
  }
}
function formatXzInCharge(psnInCharge:any){
  if ( psnInCharge == '' || null == psnInCharge) {
    return ""
  }else {
    let arr = JSON.parse(psnInCharge);
    let str = ""
    if (arr.length >= 1){
      zoningList.value.forEach((item)=>{
        if (item.value == arr[0]){
          str += item.label
          if (item.children.length > 0){
            item.children.forEach((item1)=>{
              if (item1.value == arr[1]){
                str = str +' / '+ item1.label
                if (item1.children.length > 0){
                  item1.children.forEach((item2)=>{
                    if (item2.value == arr[2]){
                      str = str +' / '+ item2.label
                    }
                  })
                }
              }
            })
          }
        }
      })
    }
    return str
  }
}
const countryList = ref([])
const zoningList = ref([])
const industryList = ref([])
onMounted(async() => {
  //await reloadDept()
  // await reloadPsn()
  // await reloadProClass()
  //await reloadCate()
  resetDynamicColumnData()

  countryList.value = (await findAllCountry()).items
  zoningList.value = (await findAllProvince())
  industryList.value = (await findAllIndustry())

})
// 这是示例组件
const [registerTable, { reload,setTableData,setColumns,getColumns,getDataSource }] = useTable({
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
  }
})
const [registerEditPage, { openModal: openEditPage }] = useModal()

const openAddPage = () => {
  let data = CrudApi.editData
  data.isEdit = false
  data.countryList =  countryList.value
  data.zoningList =  zoningList.value
  data.industryList =  industryList.value
  openEditPage(true, {
    data: data
  })
}
const openEdit = (source:any,flag) => {
  let data =JSON.parse(JSON.stringify(source))
  data.isEdit = true
  data.isLook = flag
  data.countryList =  countryList.value
  data.zoningList =  zoningList.value
  data.industryList =  industryList.value
  openEditPage(true, {
    data
  })
}

const del = async(data:any) => {
  await deleteGroup(data)
  await pointMessage({title: '处理结果', content: '删除成功！',delay: true})
  reload()
}

async function saveData(data:any) {
  if (data.id == '')data.id = null
  await saveGroup(data)
  await pointMessage({title: '处理结果', content: '更新成功！',delay: true})
  reload()
}

function onSearch(){}
/**********************栏目设置**********************/
import {initDynamics,assemblyDynamicColumn} from "./data";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  findDbLanMuList,
  saveLanMuList,
} from '/@/api/record/system/accvoucher';
import {
  getCurrentAccountName, pointMessage,getThisIndexImg
} from "/@/api/task-api/tast-bus-api";
import {cloneDeep} from "lodash-es";
import {Message} from "postcss";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {findAllCountry} from "/@/api/record/costomer_data/customer";
import {findAllProvince} from "/@/api/record/system/zone";
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
const lanMuData = {'accId':'bjxgkj-001','menuName':'集团信息','type':'集团',objects: '',username:useUserStoreWidthOut().getUserInfo.username}
const confirm = (e: MouseEvent) => {
  // 询问
  createConfirm({
    iconType: 'warning',
    title: '栏目同步',
    content: '是否将刚才设置同步数据库!',
    onOk: async() => {
      // 调整数据库 列参数
      //lanMuData.accId = getCurrentAccountName(false)
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
  //lanMuData.accId = getCurrentAccountName(false)
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
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>

<style scoped>
.vben-basic-table {
  width: 99%;
}
:deep(.ant-card-body) {
  padding: 16px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
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
.tableUStyle {
  color: blue;
  cursor: pointer;
}
.tableUStyle:hover{
  color: red;
}
</style>
