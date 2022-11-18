<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title">
          <UnorderedListOutlined style="color: #0096c7"/>
          <b class="noneSpan">计量单位</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openAddPage()"
          ><span>新增</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="updateOne()"
          ><span>修改</span></button>
<!--          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="updateList()"
          ><span>批量修改</span></button>-->
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="delList()"
          ><span>删除</span></button>
<!--          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="openExcel()"
          ><span>导入</span></button>-->
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="exportExcelNow()"
          ><span>导出</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="closeCurrent(),router.push('/one/home/welcome')">
            <span>退出</span></button>
        </div>
      </div>
      <div class="app-container-neck">
        <div style="float: right; margin-left: 10px">
          <a-button>
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
                          <b>设置表格字号</b></template>-->
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>

<!--          <a-button class="ant-btn-me" @click="modalShow">
            <DeleteOutlined :style="{ fontSize: '14px' }" />
          </a-button>-->

        </div>

        <div style="float: right; position: relative">
          <!--        <label style="font-size: 14px">检索条件：</label>-->
          <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
            <a-select-option style="font-size: 12px;" value="unitName">计量单位名称</a-select-option>
          </a-select>
          <!-- 搜索 -->
          <a-input-search
            v-model:value="pageParameter.selectValue"
            placeholder=""
            style="width: 200px; border-radius: 4px"
            @search="onSearch"
          />
        </div>

      </div>
    </div>
    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <div class="w-1/4 xl:w-1/5" style="width: 250px;min-height: 250px;border: 1px #cccccc solid;margin-right: .2%;margin-bottom: 58px;">
          <TypeTree class="w-1/4 xl:w-1/5" @select="handleSelect" v-model="pageParameter" ref="mychild"/>

        </div>
        <div style="width: 55%; float: right;margin-left: 5px;">
          <ModalPop @throwData="modalData" @register="registerModalPopPage" />

          <BasicTable
            ref="tableRef"
            class="w-3/4 xl:w-4/5"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
            :scroll="{ x: totalColumnWidth,y: windowHeight }"
            @row-click="condClick"
            @register="registerTable"
          >
            <template #flag="{ record }">
              <a-tag color="success" v-if="record.flag === '1'"  slot="flag" slot-scope="text,record,index">
                {{ record.flag === '1' ? '启用' : '停用' }}

              </a-tag>
              <a-tag color="warning" v-else-if="record.flag === '0'"  slot="flag"  slot-scope="text,record,index">
                {{ record.flag === '1' ? '启用' : '停用' }}
              </a-tag>
            </template>

            <template #isMain="{ record }">
              <span>
                  {{ record.isMain === 'true' ? '是' : '否' }}
              </span>
            </template>


            <template #conversionType="{ record }">
                {{ formatFx(record.conversionType)}}
            </template>

            <template #action="{ record }">
              <div>
                <a-popover placement="bottom">
                  <a-button style="padding: 0px 4px; height: 20px;">
                    <CaretDownFilled/>
                  </a-button>
                  <template #content>
                    <!--                <p v-if="record.flag=='1'" style="cursor: pointer" @click="openEdit(record,true)"><FormOutlined /> 编辑</p>-->
                    <p v-if="record.flag=='0'" style="cursor: pointer" @click="editFlagData(record)">
                      <CheckCircleOutlined/>
                      启用
                    </p>
                    <p v-if="record.flag=='1'" style="cursor: pointer" @click="editFlagData(record)">
                      <CloseCircleOutlined/>
                      停用
                    </p>
                    <!--                <p style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>-->
                  </template>
                </a-popover>
              </div>
            </template>

          </BasicTable>
        </div>

        <Edit
          @save="saveData"
          @register="registerEditPage"
        />
        <Edits
          @save="saveData2"
          @register="registerEditsPage"
        />
        <EditList
          @save="saveDataList"
          @register="registerEditListPage"
        />
        <Select
          @save="saveSelect"
          @register="registerSelectPage"
        />
      </PageWrapper>
    </div>
  </div>
</template>
<script setup lang="ts">
import {BasicTable, useTable} from '/@/components/Table'
import {PageWrapper} from '/@/components/Page'
import {useRouteApi} from '/@/utils/boozsoft/datasource/datasourceUtil';
import TypeTree from './TypeTree.vue'
import Edit from './popup/edit.vue';
import Edits from './popup/edits.vue';
import Select from './popup/select.vue';
import ModalPop from './popup/modalPop.vue';

import EditList from './popup/editList.vue';
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
  PieChartFilled,
  FilterFilled, CheckOutlined, SortDescendingOutlined, SortAscendingOutlined, EditOutlined,PicRightOutlined,UnorderedListOutlined
} from '@ant-design/icons-vue'
import {onMounted, reactive, ref} from 'vue'
import {
  findAll,
  editFlag,
  deletePsn,
  delMany,
  delManyList,
  savePsn,
  saveList,
  delType,
  findAllSysUnitOfMeaType, findAllSysUnitOfMeaList
} from '/@/api/group/unit-mea'
import {
  Input as AInput,
  Select as ASelect,
  Popover as APopover,
  Tabs as ATabs,
  DatePicker as ADatePicker,
  Popconfirm as APopconfirm,
  Checkbox as ACheckbox,
  Radio as ARadio,
  Table as ATable,
  Drawer as ADrawer,
  message,
  Tag as ATag
} from "ant-design-vue";

const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ATabPane = ATabs.TabPane
const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
import {
  getCurrentAccountName, getThisIndexImg,
} from "/@/api/task-api/tast-bus-api";
import {
  findDbLanMuList,
  saveLanMuList,
} from '/@/api/record/system/accvoucher'
import {cloneDeep} from "lodash-es";
import {initDynamics, assemblyDynamicColumn} from "./data";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  getThisAdInfoData,
} from "/@/api/record/system/financial-settings";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {columnProps} from "ant-design-vue/es/table/interface";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
//import {aoaToSheetXlsx} from "/@/components/Excel";
const aoaToSheetXlsx=null
const {closeCurrent} = useTabs(router);

const formItems = ref({
  selectType: 'unitName',
  selectValue: ''
})

const {
  createConfirm,
  createWarningModal
} = useMessage()

const CrudApi = {
  list: [],
  columns: [
    {
      title: 'id',
      dataIndex: 'id',
      width: 120,
      ifShow: false,
      ellipsis: true
    },
    {
      title: '状态',
      dataIndex: 'flag',
      width: 60,
      slots: {customRender: 'flag'}
    },
    {
      title: '计量单位名称',
      dataIndex: 'unitName',
      width: 180,
      ellipsis: true,
      slots: {customRender: 'unitName'}
    }
  ],
  columns2: [
    {
      title: 'id',
      dataIndex: 'id',
      width: 120,
      ifShow: false,
      ellipsis: true
    },
    {
      title: '计量单位名称',
      dataIndex: 'unitName',
      width: 180,
      ellipsis: true,
      slots: {customRender: 'unitName'}
    },
    {
      title: '主计量',
      dataIndex: 'isMain',
      width: 80,
      ellipsis: true,
      slots: {customRender: 'isMain'}
    },
    {
      title: '换算类型',
      dataIndex: 'conversionType',
      width: 120,
      ellipsis: true,
      slots: {customRender: 'conversionType'}
    },
    {
      title: '换算率',
      dataIndex: 'conversionRate',
      width: 120,
      ellipsis: true,
      slots: {customRender: 'conversionRate'}
    },
    {
      title: '换算说明',
      dataIndex: 'conversionExplain',
      width: 180,
      ellipsis: true,
      slots: {customRender: 'conversionExplain'}
    }
  ],
  editData: {
    id: '',
    flag: '',
    unitCode: '',
    unitName: '',
    typename: '',
    isMain: '',
    conversionType: '',
    conversionRate: '',
  }
}

const pageParameter = reactive({
  selectValue: '',
  typeCode: 'k1',
  flag: '',
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '湖北万亚软件技术有限公司',
})
const thisCheckKey = ref('0')

async function handleSelect(data) {
  console.log(data)
  if (null != data) {
    if(data === 'k1'){
      //查询
      let d = await findAll({unitName: pageParameter.selectValue})
      setColumns(CrudApi.columns);
      setTableData(d);
      pageParameter.typeCode = 'k1'
    }else if(data === 'k2'){
      let res = await findAllSysUnitOfMeaType({unitName: pageParameter.selectValue})
      setColumns(CrudApi.columns);
      setTableData(res);
      pageParameter.typeCode = 'k2'
    }else{
      let res = await findAllSysUnitOfMeaList({
        manyCode: data.replace('k3',''),
        unitName: pageParameter.selectValue
      })
      setColumns(CrudApi.columns2);
      setTableData(res);
      pageParameter.typeCode = 'k3'
    }
    thisCheckKey.value = data
  }
}
// 默认账套编码
let defalutCompany = getCurrentAccountName(true)
// 切换后账套编码
const dynamicCompany = ref(defalutCompany)
const dynamicTenantId = ref(getCurrentAccountName(true))
// 这是示例组件
const [registerTable, {
  reload,
  setTableData,
  setColumns,
  getColumns,
  getPaginationRef,
  setPagination,
  getDataSource
}] = useTable({
/*  api: async (params) => {
    return await findAll({
      typeCode: thisCheckKey.value,
      typeName: ''
    })
  },*/
  api: findAll,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  searchInfo: pageParameter,
  pagination: {
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['10', '30', '50', '100'],
    showTotal: t => `总共${t}条数据`
  },
  actionColumn: {
    width: 60,
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  }
})

const [registerEditPage, {openModal: openEditPage}] = useModal()
const [registerEditsPage, {openModal: openEditsPage}] = useModal()
const [registerEditListPage, {openModal: openEditListPage}] = useModal()
const [registerSelectPage, {openModal: openSelectPage}] = useModal()


const val: any = {
  id: '',
  flag: '',
  unitCode: '',
  unitName: '',
  typename: '',

}


const openAddPage = () => {
  //判断是单 还是双
  val.isEdit = true
  openSelectPage(true, {
    data: val
  })

  /*if(pageParameter.typeCode === 'k1'){
    val.isEdit = true
    openEditPage(true, {
      data: val
    })
  }else if(pageParameter.typeCode.includes('k2')){
    val.isEdit = true
    val.accId =  dynamicTenantId.value
    openEditsPage(true, {
      data: val,
      accId: dynamicTenantId.value
    })
  }else{
    message.info('请选择计量后添加！')
  }*/
}

const del = async (data: any) => {
  await deletePsn(data)
  message.success('删除成功！')
  reloadTable()
}

async function saveData(data: any) {
  const d = await savePsn(data)
  if(d === '计量单位名称不能重复!'){
    message.error(d)
  }
  reloadTable()
}
async function saveDataList(data: any) {
  if (checkRow.value.length > 0) {
    val.edit = true
    val.data = checkRow.value
    openEditListPage(true, {
      data: val
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '修改',
      content: '请选择需要修改的内容！'
    })
  }
  let item = []
  for (let i = 0; i < checkRow.value.length; i++) {
    item.push(checkRow.value[i].id)
  }

  await saveList({
    typeCode:data.typeCode,
    ids: item
  })

  reloadTable()
}

async function saveSelect(data: any) {
  console.log(data)
  if(data === 'k1'){
    val.isEdit = true
    openEditPage(true, {
      data: val
    })
  }else if(data.includes('k2')){
    val.isEdit = true
    val.accId =  dynamicTenantId.value
    openEditsPage(true, {
      data: val,
      accId: dynamicTenantId.value
    })
  }else{
    message.info('请选择计量后添加！')
  }
}

async function editFlagData(data: any) {
  await editFlag(data)
  reloadTable()
}

async function onSearch() {

  handleSelect(pageParameter.typeCode)
}

function filterSearch() {
}

function condClick() {
}
const mychild =  ref(null)
async function reloadTable() {
  console.log(pageParameter)
  if (null != pageParameter.typeCode) {
    if(pageParameter.typeCode.includes('k1')){
      //查询
      let d = await findAll({unitName: pageParameter.selectValue})
      setColumns(CrudApi.columns);
      setTableData(d);
      pageParameter.typeCode = 'k1'
    }else if(pageParameter.typeCode.includes('k2')){
      let res = await findAllSysUnitOfMeaType({unitName: pageParameter.selectValue})
      setColumns(CrudApi.columns);
      setTableData(res);
      pageParameter.typeCode = 'k2'
    }else if(pageParameter.typeCode.includes('k3')){
      let res = await findAllSysUnitOfMeaList({
        manyCode: thisCheckKey.value.replace('k3',''),
        unitName: pageParameter.selectValue
      })
      setColumns(CrudApi.columns2);
      setTableData(res);
      pageParameter.typeCode = 'k3'
    }else{
      reload()
    }
  }
  //刷新tree
  //刷新子页面list
  mychild.value.fetch(dynamicTenantId.value)
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

async function delList() {
  if (checkRow.value.length > 0) {
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        let item = []
        for (let i = 0; i < checkRow.value.length; i++) {
            item.push(checkRow.value[i].id)
        }
        if(item.length > 0){

          if(pageParameter.typeCode === 'k1'){
            await deletePsn({ids:item})
          }else if(pageParameter.typeCode === 'k2'){
            await delMany({ids:item})
          }else{
            await delManyList({ids:item})
          }
          message.success('删除成功！')
        }else{
          message.error('请选择至少一条数据！')
        }
        checkRow.value = []
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
//单选修改
async function updateOne() {
  if (checkRow.value.length > 0 && checkRow.value.length < 2) {

    if(pageParameter.typeCode === 'k1'){
      val.edit = true
      val.data = checkRow.value[0]
      openEditPage(true, {
        data: val
      })
    }else{
      val.edit = true
      val.data = checkRow.value[0]
      openEditsPage(true, {
        data: val
      })
    }
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '修改',
      content: '请选择一条需要修改的内容！'
    })
  }
}

/**********************栏目设置**********************/
/*start栏目设置*/

const visible = ref(false);
const windowWidth = (document.documentElement.clientWidth - (70 + 280))
const windowHeight = (window.innerHeight - (350))
const totalColumnWidth = ref(0)
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData: any = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});
const tableRef: any = ref(null)
const lanMuData = {
  'accId': '',
  'menuName': '计量单位',
  'type': '列表',
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
      lanMuData.accId = getCurrentAccountName(false)
      lanMuData.objects = JSON.stringify(filterModifyData(dynamicColumnData.value, dynamicColumnDataCopy))
      if (lanMuData.objects == '[]') {
        createWarningModal({content: '请先做修改后再进行确认同步数据库！'})
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
    let arr: any = key.split('-');
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
    let arr: any = key.split('-');
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
    let arr: any = key.split('-');
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
    } catch (e: any) {
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
  let a: any = []
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
  lanMuData.accId = getCurrentAccountName(false)
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
    //pageReload()
  })
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
    if (visible.value) f = 260
    totalColumnWidth.value = Number(windowWidth) - f
    tableRef.value.$el.style.setProperty('width', (windowWidth + 70 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 70) + 'px')
  }
}

const pageReload = () => {
  setTableData([]) // 清空可能残留的数据
  reload({
    searchInfo: pageParameter
  })
}


const formatFx = (o: any) => {
  let str = ''
  if(o === '1'){
    str = '固定换算率'
  }else if(o === '2'){
    str = '浮动换算率'
  }
  return str;
};
/*栏目设置end*/

const exportExcelNow = async () => {
  const data = JSON.parse(JSON.stringify(getDataSource()))
  // debugger
  let arrHeader
  let columnList
  if(pageParameter.typeCode === 'k3'){
    arrHeader = ['计量单位名称','主计量','换算类型','换算率','换算说明'];
    columnList = ['unitName','isMain','conversionType','conversionRate','conversionExplain']
  }else{
    arrHeader = ['状态','计量单位名称'];
    columnList = ['flag','unitName']
  }

  data.forEach(v=>{
    v.flag = v.flag === '0' ? '停用':'启用'
    v.isMain = v.isMain === 'true' ? '是':'否'
    v.conversionType = v.conversionType === '1' ? '固定换算率':'浮动换算率'
  })
  const arrData = data.map((item) => columnList.map(column=>item[column]));
  // 保证data顺序与header一致
  aoaToSheetXlsx({
    data: arrData,
    header: arrHeader,
    filename: '计量单位.xlsx',
  });
}

const [registerModalPopPage, { openModal: openMoalPopPage }] = useModal();

const modalShow = () => {
  openMoalPopPage(true, {
    database: dynamicTenantId.value,
  });
}

// 弹选回调
const modalData = async (data) => {
  console.log(data)
  //刷新子页面list
  mychild.value.fetch(dynamicTenantId.value)
  await reloadTable()
}
</script>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>

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
</style>
