<template>
  <div class="app-container">
    <Edit @save="recordData.saveData" @register="registerEditPage"/>
    <BasicTable
      v-show="activeKey"
      class="w-4/5 xl:w-5/6"
      ref="tableRef"
      :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
      :scroll="{ x: totalColumnWidth,y: windowHeight }"
      :row-selection="{ type: 'radio',fixed: true,selectedRowKeys: tableSelectedRowKeys, onChange: onSelectChange }"
      @register="registerTable"
      @fetch-success="fetchSuccess"
    >
      <template #action="{ record }">
        <div>
          <Popover placement="bottom">
            <Button style="padding: 0px 4px; height: 20px;">
              <CaretDownFilled/>
            </Button>
            <template #content>
              <p class="p_specifics" @click="recordData.arr[1].fun(record,false)">
                <FormOutlined/>
                修改
              </p>
              <p class="p_specifics" @click="recordData.arr[2].fun(record)">
                <DeleteOutlined/>
                删除
              </p>
            </template>
          </Popover>
        </div>
      </template>
    </BasicTable>
    <div class="pagination-text" :style="{top: (windowHeight+50)+'px'}" v-show="showPaginationText">
      {{`共 ${tableTotal}条记录&emsp;每页 200 条`}}
    </div>
  </div>
</template>
<script setup lang="ts">
import {BasicTable, useTable} from '/@/components/Table'
import {useModal} from '/@/components/Modal'

import {computed, onMounted, ref, reactive, watch, inject, provide} from 'vue'
import {
  getOrganizeList,
  deleteOrganize,
  saveOrganize,
  getOrganizeAll
} from '/@/api/record/group/im-organize'
import {findAllIndustry, getGroupAll} from "/@/api/record/group/im-group";
import Edit from './popup/edit.vue'
import {cateFindStateFlag} from "/@/api/project_category/project_category";
import {useProjectStoreWidthOut} from '/@/store/modules/project'
import {getDeptListById} from "/@/api/record/system/dept";
import {psnFindAll} from "/@/api/psn/psn";
import {projectClassFindAll} from "/@/api/record/system/project_class";
import {
  CaretDownFilled,
  FormOutlined,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined, EllipsisOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled, EditOutlined, SortAscendingOutlined, CheckOutlined, SortDescendingOutlined
} from '@ant-design/icons-vue';
import {
  Select as ASelect, Input as AInput, Popover, Tag as ATag, Popconfirm as APopconfirm,
  Radio as ARadio, Checkbox as ACheckbox, List as AList, Row as ARow, Col as ACol, Card as ACard,
  Table as ATable, message,Button
} from 'ant-design-vue'
const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const AListItem = AList.Item

//选择项目大类
const cateCode: any = ref()
const currentCateCode = computed(() => cateCode.value)
const recordData=inject('recordData')

watch(currentCateCode, (abc) => {
  // projectStore.projectClassTree()
  reload({
    searchInfo: {}
  })
})

const showPaginationText:any = ref(false)
const tableTotal:any = ref(0)
async function fetchSuccess(data) {
  showPaginationText.value=true
  tableTotal.value = data.items.length
  if (data.items.length < 50) {
    for (let i = data.items.length; i < 50; i++) {
      data.items.push({})
    }
  }
  setTableData(data.items)
}

function createColumn({label,field}){
  return    {
    key: '0',
    title:label,
    dataIndex: field,
    align: 'center',
  }
}
// const arr=[
//   $$columnModel$$
// ]


function createCurdModel({api,columns}){
  return {
    list:api,
    columns,
    editData: {
      id: '',
      uniqueCode: '',
      orgCode: '',
      orgName: '',
      orgSimpName: '',
      uniqueGroupCode: '',
      orgSuperior: '',
      industryclassCode: '',
      uniqueCodeZone: '',
      countryId: '',
      createDate: '',
      personInCharge: '',
      uniqueAccStandard: '',
      telephone: '',
      address: '',
      ccodeLevel: '',
      remarks: '',
    }
  }
}
const CrudModel=createCurdModel({
  api: getOrganizeList,
  columns: [
    ...recordData.value.columns.map(it=>{
      return createColumn({label:it.label,field:it.field})
    })
  ]
})
const CrudApi = CrudModel
//项目分类
const proClassList = ref([])
const groupInfoList = ref([])
const normList = ref([])
const levelList = ref([])


//部门
const deptList = ref([])

async function reloadDept() {
  const res: any = await getDeptListById()
  deptList.value = res.items
  // console.log(deptList.value)
}

function formatDeptCode(deptCode: any) {
  let str = deptCode
  // console.log(deptCode)
  deptList.value.forEach(
    function (dept: any) {
      if (dept.uniqueCode == deptCode) {
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
  const res: any = await psnFindAll()
  psnList.value = res.items
  // console.log(psnList.value)
}

function formatGroupInCharge(psnInCharge: any) {
  let str = psnInCharge
  groupInfoList.value.forEach(
    function (psn: any) {
      if (psn.uniqueCode == psnInCharge) {
        // console.log(psn)
        str = psn.groupName
      }
    }
  )
  return str
}

function formatNormInCharge(psnInCharge: any) {
  let str = psnInCharge
  normList.value.forEach(
    function (psn: any) {
      if (psn.uniqueAccStandard == psnInCharge) {
        // console.log(psn)
        str = psn.accStandardName
      }
    }
  )
  return str
}

function formatPsnInCharge(psnInCharge: any) {
  let str = psnInCharge
  // console.log(psnInCharge)
  psnList.value.forEach(
    function (psn: any) {
      if (psn.uniqueCode == psnInCharge) {
        // console.log(psn)
        str = psn.psnName
      }
    }
  )
  return str
}

function formatDqInCharge(psnInCharge: any) {
  if (psnInCharge == '' || null == psnInCharge) {
    return ""
  } else {
    let str = ""
    countryList.value.forEach((item) => {
      if (item.uniqueCode == psnInCharge) str = item.namech
    })
    return str
  }
}

function formatHyInCharge(psnInCharge: any) {
  if (psnInCharge == '' || null == psnInCharge) {
    return ""
  } else {
    let arr = JSON.parse(psnInCharge);
    let str = ""
    if (arr.length >= 1) {
      industryList.value.forEach((item) => {
        if (item.value == arr[0]) {
          str += item.label
          if (item.children.length > 0) {
            item.children.forEach((item1) => {
              if (item1.value == arr[1]) {
                str = str + ' / ' + item1.label
              }
            })
          }
        }
      })
    }
    return str
  }
}

function formatXzInCharge(psnInCharge: any) {
  if (psnInCharge == '' || null == psnInCharge) {
    return ""
  } else {
    let arr = JSON.parse(psnInCharge);
    let str = ""
    if (arr.length >= 1) {
      zoningList.value.forEach((item) => {
        if (item.value == arr[0]) {
          str += item.label
          if (item.children.length > 0) {
            item.children.forEach((item1) => {
              if (item1.value == arr[1]) {
                str = str + ' / ' + item1.label
                if (item1.children.length > 0) {
                  item1.children.forEach((item2) => {
                    if (item2.value == arr[2]) {
                      str = str + ' / ' + item2.label
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

function formatZzInCharge(psnInCharge: any) {
  if (psnInCharge == '' || null == psnInCharge) {
    return ""
  } else {
    let str = ""
    getDataSource().forEach((item) => {
      if (item.uniqueCode == psnInCharge) str = item.orgSimpName
    })
    return str
  }
}

const countryList = ref([])
const zoningList = ref([])
const industryList = ref([])
onMounted(async () => {

})



const cardList: any = ref([])
const activeKey = ref(true)
// 这是示例组件
const useTableParams = useTable({
  api: recordData.value.crud[0],
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: true,
  pagination: {
    pageSize: 200,
    simple: true,
  },
/*  actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  },*/
  searchInfo: {}
})
recordData.value.useTableParams=useTableParams
const [registerTable, {reload, setTableData, getDataSource, setColumns, getColumns}]=useTableParams
const [registerEditPage, {openModal: openEditPage}] = useModal()
recordData.value.openEditPage=openEditPage
const openAddPage = () => {
  let data = CrudApi.editData
  data.isEdit = false
  data.groupInfoList = groupInfoList.value
  data.normList = normList.value
  data.levelList = levelList.value
  data.countryList = countryList.value
  data.zoningList = zoningList.value
  data.industryList = industryList.value
  data.numberDec = '2'
  data.unitPriceDec = '2'
  data.rateDec = '5'
  data.accvouchDec = '4'
  data.periodNum = '12'
  data.upList = getDataSource()
  openEditPage(true, {
    data: data
  })
}
const openEdit = (source: any, flag) => {
  let data = JSON.parse(JSON.stringify(source))
  data.isEdit = true
  data.isLook = flag
  data.groupInfoList = groupInfoList.value
  data.normList = normList.value
  data.levelList = levelList.value
  data.countryList = countryList.value
  data.zoningList = zoningList.value
  data.industryList = industryList.value
  data.upList = getDataSource()
  openEditPage(true, {
    data
  })
}
const del = async (data: any) => {
  await deleteOrganize(data).then(async (res) => {
    if (null != res && res.isOk == false) {
      await pointMessage({
        title: '处理结果',
        content: `删除失败,该组织下已经建立了${res.type == 'D02' ? '公司' : '下级组织'},无法进行删除！`,
        delay: true
      })
    } else {
      reload()
      await cardReload()
      await pointMessage({title: '处理结果', content: '删除成功！', delay: true})
    }
  }).catch(async () => await pointMessage({title: '处理结果', content: '删除失败！', delay: true}))
}

async function saveData(data: any) {
  if (data.id == '') data.id = null
  await saveOrganize(data).then(async (res) => {
    await pointMessage({title: '处理结果', content: '更新成功！', delay: true})
    await reload()
    await cardReload()
  }).catch(async () => await pointMessage({title: '处理结果', content: '更新失败！', delay: true}))
}

async function cardReload() {
  if (!activeKey.value) cardList.value = await getOrganizeAll()
}

function onSearch() {
}

/**********************栏目设置**********************/
import {initDynamics, assemblyDynamicColumn} from "./data/data";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  findDbLanMuList,
  saveLanMuList,
} from '/@/api/record/system/accvoucher';
import {
  pointMessage,
} from "/@/api/task-api/tast-bus-api";
import {cloneDeep} from "lodash-es";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {findAllCountry} from "/@/api/record/costomer_data/customer";
import {findAllProvince} from "/@/api/record/system/zone";
import {initBasisTabAccoutData} from "/@/api/record/system/financial-settings";
const pageParameter =inject('pageParameter')
const {createConfirm, createWarningModal} = useMessage();
const visible = ref(false);
const windowWidth = (document.documentElement.clientWidth - (70/*+280*/))
const windowHeight = (window.innerHeight - (320))
const totalColumnWidth = ref(0)
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});
const tableRef = ref(null)
const lanMuData = {
  'accId': 'postgre',
  'menuName': '组织信息',
  'type': '集团',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username
}
const confirm = (e: MouseEvent) => {
  // 询问
  createConfirm({
    iconType: 'warning',
    title: '栏目同步',
    content: '是否将刚才设置同步数据库!',
    onOk: async () => {
      // 调整数据库 列参数
      //lanMuData.accId = getCurrentAccountName(false)
      lanMuData.objects = JSON.stringify(filterModifyData(dynamicColumnData.value, dynamicColumnDataCopy))
      if (lanMuData.objects == '[]') {
        createWarningModal({title: '温馨提示', content: '请先做修改后再进行确认同步数据库！'})
      } else {
        saveLanMuList(lanMuData).then(res => {
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

function filterModifyData(lanMuList: any, copyList) {
  let a = lanMuList.filter(item => {
    try {
      copyList.forEach(item2 => {
        if (item.key === item2.key && item.name == item2.name) {
          if (item.nameNew != item2.nameNew || item.width != item2.width || item.check != item2.check || item.align != item2.align)
            throw new Error('ok')
        }
      })
      return false
    } catch (e) {
      if (e.message == 'ok') {
        return true
      } else {
        return false
      }
    }
  })
  return a;
}

function combineParameters(staticList: any, dbList: any) {
  staticList.forEach(item => {
    dbList.forEach(item2 => {
      if (item.key === item2.key && item.name === item2.name) {
        item.nameNew = item2.nameNew
        item.width = parseInt(item2.width)
        item.check = item2.check == 'true'
        item.align = item2.align
      }
    })
  })
  return staticList
}

const reloadColumns = () => {
  let a = []
  a = getColumns()
  let last = a.pop()
  let newA = JSON.parse(JSON.stringify(CrudApi.columns))
  newA = assemblyDynamicColumn(dynamicColumnData.value, newA)
  newA.push(last)
  setColumns(newA)
  initTableWidth(newA)
  //searchConditonList.value = newA
}

function resetDynamicColumnData() {
  // 先从数据查询是否已经设置
  //lanMuData.accId = getCurrentAccountName(false)
  findDbLanMuList(lanMuData).then(res => {
    // 栏目列
    let dbList = res.items
    if (dbList.length > 0) {
      let statiList = initDynamics()['DATA']
      dbList = combineParameters(statiList, dbList)
      dynamicColumnData.value = dbList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(dbList))
    } else {
      let statiList = initDynamics()['DATA']
      dynamicColumnData.value = statiList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(statiList))
    }
    // 表格列
    reloadColumns()
  })
}

function initTableWidth(thisCs) {
  let total = 60
  // debugger
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })
  /*  // 去除操作咧宽
    total -= 100*/
  if (total > windowWidth) {
    let f = 0
    if (visible.value) f = 260
    totalColumnWidth.value = Number(windowWidth) - f
    tableRef.value.$el.style.setProperty('width', (windowWidth + 20 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 20) + 'px')
  }
}

/*栏目设置end*/
const tableSelectedRowKeys = ref([])
recordData.value.tableSelectedRowKeys=tableSelectedRowKeys
const tableSelectedRowObjs = ref([])
recordData.value.tableSelectedRowObjs=tableSelectedRowObjs
const onSelectChange = (selectedRowKeys, objs) => {
  tableSelectedRowKeys.value = selectedRowKeys
  tableSelectedRowObjs.value = objs
};
const editBefore = () => {
  if (tableSelectedRowKeys.value.length == 0) {
    message.info("请选择需要进行修改的行且只能是一行！")
  } else {
    openEdit(tableSelectedRowObjs.value[0], false)
  }
}
const delBefore = async () => {
  if (tableSelectedRowKeys.value.length == 0) {
    message.info("请至少选择一行进行删除！")
  } else {
    for (let i = 0; i < tableSelectedRowObjs.value.length; i++) {
      let entity = tableSelectedRowObjs.value[i]
      let flag = false
      await deleteOrganize(entity).then(async (res) => {
        if (null != res && res.isOk == false) {
          await pointMessage({
            title: '处理结果',
            content: `删除失败,该组织下已经建立了${res.type == 'D02' ? '公司' : '下级组织'},无法进行删除！`,
            delay: true
          })
          flag = true
        } else {
          reload()
          await pointMessage({title: '处理结果', content: '删除成功！', delay: true})
        }
      }).catch(async () => {
        flag = true
        await pointMessage({title: '处理结果', content: '删除失败！', delay: true})
      })
      if (flag) break
    }
  }
}
watch(activeKey, async (v) => {
  if (!v && getDataSource().length != cardList.value.length) await cardReload()
})
</script>
<style src="../../../../assets/styles/generate-code-record-style/record.less" lang="less" scoped></style>
<style lang="less" scoped>
@import '/@/assets/styles/alone-basic-table.less';
:deep(.vben-basic-table) .ant-pagination {
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
}
.app-container {
  position: relative;
  .pagination-text{
    position: absolute;
    bottom: 9px;
    right: 30%;
    font-size: 13px;
    color: black;
    z-index: 99999999;
  }
}
:deep(.ant-table-thead) th {
  text-align: center !important;
  font-weight: bold;
  //background-color: #f2f2f2 !important;
  background-color: #cccccc !important;
  border-color: #aaaaaa !important;
}
</style>
