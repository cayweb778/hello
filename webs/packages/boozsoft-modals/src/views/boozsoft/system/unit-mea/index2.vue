<template>
  <div>
    <div class="app-container lcr-theme-div">
      <div>
        <div>
          <ProfileOutlined style="color: #0096c7;font-size: 50px;"/>
        </div>
        <div> <AccountPicker theme="three"  @reloadTable="dynamicAdReload"/></div>
      </div>
      <div>
        <div>  <b class="noneSpan" style="font-size: 36px;line-height: 75px;color: #0096c7;">计量单位</b></div>
      </div>
      <div>
        <div>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="modalShow2()"
            v-if="isGroupFlg === '0'"
          ><span>引入</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            v-if="isGroupFlg === '1'"
            ant-click-animating-without-extra-node="false"
            @click="openAddPage()"
          ><span>新增</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="updateOne()"
            v-if="isGroupFlg === '1'"
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
          <!--          <a-popover class="ant-btn-default" placement="bottom" v-if="isGroupFlg === '1'">
                      <template #content>
                        <span class="group-btn-span-special" @click="openExcelOne()">单计量</span><br/>
                        <span class="group-btn-span-special" @click="openExcelTwo()">多计量</span>
                      </template>
                      <a-button type="button" class="ant-btn ant-btn-me">导入</a-button>
                    </a-popover>-->
          <button
            v-if="isGroupFlg === '1'"
            type="button"
            class="ant-btn ant-btn-me"
            @click="openExcelOne()"
          ><span>导入</span></button>
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
        <div>
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

          <a-button class="ant-btn-me" @click="modalShow">
            <DeleteOutlined :style="{ fontSize: '14px' }" />
          </a-button>
        </div>
      </div>
    </div>

    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <div  style="width: 250px;border: 1px #cccccc solid;margin-right: .1%;margin-bottom: 58px;">
          <TypeTree  @select="handleSelect" v-model="pageParameter" ref="mychild"/>
        </div>

        <div style="width: 55%;height:100%; float: right;margin-left: 5px;">
          <ModalPop @throwData="modalData" @register="registerModalPopPage" />
          <ModalPop2 @throwData="modalData2" @register="registerModalPopPage2" />

          <BasicTable
            ref="tableRef"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
            :scroll="{ y: windowHeight }"
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
                  {{ record.isMain != null ? record.isMain === 'true' ? '是' : '否' :'' }}
              </span>
            </template>

            <template #unitType="{ record }">
              {{ formatUnitType(record.unitType)}}
            </template>

            <template #conversionType="{ record }">
                {{ formatFx(record.conversionType)}}
            </template>

            <template #action="{ record }">
              <div v-if="record.unitName !=null">
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

          <div class="pagination-text" :style="{top: (windowHeight+45)+'px',right:'45%'}" >
            {{`共 ${tableTotal}条记录&emsp;每页 200 条`}}
          </div>

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
        <Excel @save="saveExcel" @register="registerExcelPage"/>
        <Excel1 @save="saveExcel1" @register="registerExcelPage1"/>

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
import ModalPop2 from './popup/modalPop2.vue';
import Excel from './popup/excel.vue'
import Excel1 from './popup/excel1.vue'

import EditList from './popup/editList.vue';
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
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
  PieChartFilled,ProfileOutlined,
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
  findAllSysUnitOfMeaType, findAllSysUnitOfMeaList,excelVoucherType,excelVoucherType1,getExcelData
} from '/@/api/record/system/unit-mea'
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
import {aoaToSheetXlsx} from "/@/components/Excel";
import {isGroup} from "/@/api/record/system/project-cash";
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
      customHeaderCell: () => {		// 表头加粗
        return {style: {'font-weight':'bold'}} // return 想要设置的样式
      },
      slots: {customRender: 'flag'}
    },
    {
      title: '计量单位名称',
      dataIndex: 'unitName',
      width: 180,
      ellipsis: true,
      customHeaderCell: () => {		// 表头加粗
        return {style: {'font-weight':'bold'}} // return 想要设置的样式
      },
      slots: {customRender: 'unitName'}
    },
    {
      title: '类型',
      dataIndex: 'unitType',
      width: 180,
      ellipsis: true,
      customHeaderCell: () => {		// 表头加粗
        return {style: {'font-weight':'bold'}} // return 想要设置的样式
      },
      slots: {customRender: 'unitType'}
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
      customHeaderCell: () => {		// 表头加粗
        return {style: {'font-weight':'bold'}} // return 想要设置的样式
      },
      slots: {customRender: 'unitName'}
    },
    {
      title: '主计量',
      dataIndex: 'isMain',
      width: 80,
      ellipsis: true,
      customHeaderCell: () => {		// 表头加粗
        return {style: {'font-weight':'bold'}} // return 想要设置的样式
      },
      slots: {customRender: 'isMain'}
    },
    {
      title: '换算率',
      dataIndex: 'conversionRate',
      width: 120,
      ellipsis: true,
      customHeaderCell: () => {		// 表头加粗
        return {style: {'font-weight':'bold'}} // return 想要设置的样式
      },
      slots: {customRender: 'conversionRate'}
    },
/*    {
      title: '换算说明',
      dataIndex: 'conversionExplain',
      width: 180,
      ellipsis: true,
      customHeaderCell: () => {		// 表头加粗
        return {style: {'font-weight':'bold'}} // return 想要设置的样式
      },
      slots: {customRender: 'conversionExplain'
      }
    },*/
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
    unitType: '',
  }
}
const tableTotal = ref(0);

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
  if (null != data) {
    if(data.unitCode === 'k1'){
      //查询
      let d = await useRouteApi(findAll, {schemaName: dynamicTenantId.value})({unitName: pageParameter.selectValue})
      setColumns(CrudApi.columns);
      tableTotal.value = d.length
      if(d.length<50){
        for (let i =  d.length; i < 50; i++) {
          d.push({})
        }
      }
      setTableData(d);
      pageParameter.typeCode = 'k1'
    }else if(data.unitCode === 'k2'){
      let res = await useRouteApi(findAllSysUnitOfMeaType, {schemaName: dynamicTenantId})({unitName: pageParameter.selectValue})
      setColumns(CrudApi.columns);
      tableTotal.value = res.length
      if(res.length<50){
        for (let i =  res.length; i < 50; i++) {
          res.push({})
        }
      }
      setTableData(res);
      pageParameter.typeCode = 'k2'
    }else{
      let res = await useRouteApi(findAllSysUnitOfMeaList, {schemaName: dynamicTenantId.value})({
        manyCode: data.unitCode.replace('k3',''),
        unitName: pageParameter.selectValue
      })
      setColumns(CrudApi.columns2);
      tableTotal.value = res.length
      if(res.length<50){
        for (let i =  res.length; i < 50; i++) {
          res.push({})
        }
      }
      setTableData(res);
      pageParameter.typeCode = 'k3'
    }
    thisCheckKey.value = data.unitCode
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
  /*api: useRouteApi(findAll,{schemaName: dynamicTenantId}),*/
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: true,
  searchInfo: pageParameter,
  pagination: {
    pageSize: 200,
    simple:true
  },
  /*actionColumn: {
    width: 60,
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  }*/
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
  val.isEdit = false
  val.accId = dynamicTenantId.value
  openSelectPage(true, {
    data: val
  })

}

const del = async (data: any) => {
  await useRouteApi(deletePsn, {schemaName: dynamicTenantId.value})(data)
  message.success('删除成功！')
  reloadTable()
}

async function saveData(data: any) {

  reloadTable()
}
async function saveData2(data: any) {
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

  useRouteApi(saveList, {schemaName: dynamicTenantId.value})({
    typeCode:data.typeCode,
    ids: item
  }).then(v=>{
    console.log(v)
  })

  reloadTable()
}

async function saveSelect(data: any) {


  reloadTable()
}

async function editFlagData(data: any) {
  await useRouteApi(editFlag, {schemaName: dynamicTenantId.value})(data)
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
  if (null != pageParameter.typeCode) {
    if(pageParameter.typeCode.includes('k1')){
      //查询
      let d = await useRouteApi(findAll, {schemaName: dynamicTenantId.value})({unitName: pageParameter.selectValue})
      setColumns(CrudApi.columns);
      tableTotal.value = d.length
      if(d.length<50){
        for (let i =  d.length; i < 50; i++) {
          d.push({})
        }
      }
      setTableData(d);
      pageParameter.typeCode = 'k1'
    }else if(pageParameter.typeCode.includes('k2')){
      let res = await useRouteApi(findAllSysUnitOfMeaType, {schemaName: dynamicTenantId.value})({unitName: pageParameter.selectValue})
      setColumns(CrudApi.columns);
      tableTotal.value = res.length
      if(res.length<50){
        for (let i =  res.length; i < 50; i++) {
          res.push({})
        }
      }
      setTableData(res);
      pageParameter.typeCode = 'k2'
    }else if(pageParameter.typeCode.includes('k3')){
      let res = await useRouteApi(findAllSysUnitOfMeaList, {schemaName: dynamicTenantId.value})({
        manyCode: thisCheckKey.value.replace('k3',''),
        unitName: pageParameter.selectValue
      })
      setColumns(CrudApi.columns2);
      tableTotal.value = res.length
      if(res.length<50){
        for (let i =  res.length; i < 50; i++) {
          res.push({})
        }
      }
      setTableData(res);
      pageParameter.typeCode = 'k3'
    }else{
      //reload()
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
      content: '你确认要删除吗?',
      onOk: async () => {
        let item = []
        for (let i = 0; i < checkRow.value.length; i++) {
            item.push(checkRow.value[i].id)
        }
        if(item.length > 0){

          if(pageParameter.typeCode === 'k1'){
            await useRouteApi(deletePsn, {schemaName: dynamicTenantId.value})({ids:item})
          }else if(pageParameter.typeCode === 'k2'){
            await useRouteApi(delMany, {schemaName: dynamicTenantId.value})({ids:item})
          }else{
            await useRouteApi(delManyList, {schemaName: dynamicTenantId.value})({ids:item})
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
      val.accId = dynamicTenantId.value
/*      openEditPage(true, {
        data: val,
        isEdit: true
      })*/
      val.typeCode = 'k1'
      openSelectPage(true, {
        data: val,
        typeCode: 'k1',
        isEdit: true
      })
    }else{
      val.edit = true
      val.data = checkRow.value[0]
      val.accId = dynamicTenantId.value
      val.typeCode = 'k2'

      /*openEditsPage(true, {
       data: val,
       isEdit: true
     })*/
      openSelectPage(true, {
        data: val,
        isEdit: true
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

const isGroupFlg = ref('1')
const accName = ref('1')
const dynamicAdReload = async (obj) => {
  dynamicTenantId.value = obj.accId+'-'+obj.year
  accName.value = obj.companyName
  await reloadTable()
  //ZZ,GD,CH
  //查询当前账套是独立状态还是集团账套 然后显示对象数据  分别格式化数据
  isGroupFlg.value = await useRouteApi(isGroup,{schemaName: dynamicTenantId})({
    accId: obj.accId,
    type: obj.target.ztStyle
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
const formatUnitType = (o: any) => {
  let str = ''
  if(o === '1'){
    str = '重量'
  }else if(o === '2'){
    str = '数量'
  }else if(o === '3'){
    str = '面积'
  }else if(o === '4'){
    str = '长度'
  }else if(o === '5'){
    str = '体积'
  }else if(o === '6'){
    str = '容积'
  }
  return str;
};
/*栏目设置end*/

const exportExcelNow = async () => {
  let arrHeader
  let columnList
  if(pageParameter.typeCode === 'k1'){

    const data = JSON.parse(JSON.stringify(getDataSource()))
    arrHeader = ['计量单位名称','计量类型',];
    columnList = ['unitName','unitType']
    data.forEach(v=>{
      v.flag = v.flag === '0' ? '停用':'启用'
      v.isMain = v.isMain === 'true' ? '是':'否'
      v.conversionType = v.conversionType === '1' ? '固定换算率':'浮动换算率'
      v.unitType = formatUnitType(v.unitType)
    })
    const arrData = data.map((item) => columnList.map(column=>item[column]));
    // 保证data顺序与header一致
    aoaToSheetXlsx({
      data: arrData,
      header: arrHeader,
      filename: '单计量单位_'+ accName.value +'.xlsx',
    });
  }else{
    const arrHeader = ['计量单位组名称', '计量类型', '主计量','计量1名称','换算率1','计量2名称','换算率2'];
    columnList = ['str1','str2','str3','str4','str5','str6','str7']
    //查询信息后导出
    const data = await useRouteApi(getExcelData,{schemaName: dynamicTenantId})({})
    data.forEach(v=>{
      v.str2 = formatUnitType(v.str2)
    })
    const arrData = data.map((item) => columnList.map(column=>item[column]));
    // 保证data顺序与header一致
    aoaToSheetXlsx({
      data: arrData,
      header: arrHeader,
      filename: '多计量单位_'+ accName.value +'.xlsx',
    });

  }


}

const [registerModalPopPage, { openModal: openMoalPopPage }] = useModal();
const [registerModalPopPage2, { openModal: openMoalPopPage2 }] = useModal();

const modalShow = () => {
  openMoalPopPage(true, {
    database: dynamicTenantId.value,
  });
}

// 弹选回调
const modalData = async (data) => {
  console.log(data)

  await reloadTable()
}

const modalShow2 = () => {
 openMoalPopPage2(true, {
    database: dynamicTenantId.value,
  });
}

// 弹选回调
const modalData2 = async (data) => {
  console.log(data)
  await reloadTable()
}

//导入Excel
const [registerExcelPage, { openModal: openExcelPage }] = useModal()
const [registerExcelPage1, { openModal: openExcelPage1 }] = useModal()

const openExcelOne = async () => {
  openExcelPage(true, {
    data: {
      dynamicTenantId: dynamicTenantId.value
    }
  })
}

async function saveExcel(data:any) {

  await reloadTable()
}
const openExcelTwo = async () => {
  openExcelPage1(true, {
    data: {
      dynamicTenantId: dynamicTenantId.value
    }
  })
}
async function saveExcel1(data:any) {
  //刷新子页面list
  mychild.value.fetch(dynamicTenantId.value)
  await reloadTable()
}
</script>
<style src="./global-menu-index.less" lang="less" scoped></style>

<style lang="less" scoped>
.lcr-theme-div{
  display: inline-flex;justify-content: space-between;width: 99%;height: 100px;
  >div:nth-of-type(1){
    width: 40%;
    position: relative;
    >div:nth-of-type(1){width: 64px;display: inline-block;text-align: center;    top: 12px;
      position: inherit
    }
    >div:nth-of-type(2){
      width: calc( 100% - 64px);display: inline-block;
    }
  }
  >div:nth-of-type(2){
    width: 20%;text-align:center;
    >div:nth-of-type(2){margin-top: 14px;}
  }
  >div:nth-of-type(3){
    width: 40%;text-align: right;

    >div:nth-of-type(1){
      .ant-btn-me {
        color: #0096c7;
      }
    }
    >div:nth-of-type(2){
      display: inline-flex;justify-content: space-between;margin-top: 15px;
    }
  }
}
a {
  color: #0096c7;
  text-decoration: none;
  cursor: pointer;
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
  margin: 0px 10px;
  background: #b4c8e3 !important;
  position: relative;
  .pagination-text {
    position: absolute;
    bottom: 6px;
    right: 10%;
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
