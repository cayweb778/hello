<template>
  <div>
    <div class="app-container">

      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">-->
        <div class="container-head-title" style="margin-left: 20px;">
          <b class="noneSpan"><UnorderedListOutlined />&nbsp;&nbsp;自定义项档案</b>
        </div>
        <!--      <div style="display: inline">
                <a-select v-model:value="definedCode" @change="changCode()" class="head-index-select" placeholder="请选择档案">
                  <a-select-option v-for="item in definedFileList" :key="item.definedCode" :value="item.definedCode">
                    ({{ item.definedCode }}){{ item.definedName }}
                  </a-select-option>
                </a-select>
              </div>-->
        <div class="ant-btn-group" style="float: right; margin-left: 10px">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openAdd()"
          ><span>增行</span></button>
<!--          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="editOpen()"
          ><span>修改</span></button>-->
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="delList()"
          ><span>删行</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="openInto()"
          ><span>引入</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="openAssign()"
          ><span>分配</span></button>
<!--          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="openExcel()"
          ><span>导入</span></button>-->
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="exportExcel()"
          ><span>导出</span></button>
<!--          <button
            type="button"
            class="ant-btn ant-btn-me"
          ><span>打印</span></button>-->
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="closeCurrent()"
          ><span>退出</span></button>
        </div>
      </div>

      <div style="clear: none"/>
      <div style="display: inline-block;float: left;margin-top: .2%;" class="select-div">
        &ensp;<span>所属组织：</span>
        <a-select v-model:value="originId" @change="changeOrigin()" style="width: 250px;margin-top: -10px;margin-left: 10px">
          <a-select-option v-for="item in organizeList" :key="item.uniqueCode">
            {{ item.orgSimpName }}
          </a-select-option>
        </a-select>
      </div>
      <div style="float: left; margin-left: 10px;line-height: 30px;">共 {{tableAllData.length}} 条记录</div>

      <div class="app-container-neck">
        <div style="float: right; margin-left: 10px">
          <a-button class="ant-btn-me">
            <SyncOutlined :style="{ fontSize: '14px' }" />
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
          <!--        <a-popover placement="bottom">
                    <a-button>
                      <SettingFilled :style="{ fontSize: '14px' }" />
                    </a-button>
                  </a-popover>-->
          <!--        <a-button>
                    <PicLeftOutlined :style="{ fontSize: '14px' }" />
                  </a-button>-->
          <!--        <a-button>
                    <EditFilled :style="{ fontSize: '14px' }" />
                  </a-button>-->
          <!--        <a-popover placement="bottom">
                  </a-popover>

                  <a-button>
                    <EditFilled :style="{ fontSize: '14px' }" />
                  </a-button>

                  <a-button @click="activeKey=!activeKey">
                    <PieChartFilled :style="{ fontSize: '14px' }" />
                  </a-button>
                  <a-button>
                    <FilterFilled :style="{ fontSize: '14px' }" />
                  </a-button>-->
        </div>
        <div style="float: right; position: relative">
          <!--        <label style="font-size: 14px">检索条件：</label>-->
          <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;" class="special_select">
            <a-select-option style="font-size: 12px;" value="1">编码</a-select-option>
            <a-select-option style="font-size: 12px;" value="2">名称</a-select-option>
          </a-select>
          <!-- 搜索 -->
          <a-input-search
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
      <Into @save="saveInto" @register="registerIntoPage"/>
      <Assign @save="saveAssign" @register="registerAssignPage"/>
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <div class="w-1/4 xl:w-1/5" style="width: 300px;min-height: 300px;border: 1px #cccccc solid;margin-right: .2%;margin-bottom: 58px;">
          <ClassTree @select="handleSelect" v-model="pageParameter"/>
        </div>
        <div style="width: calc(100% - 300px); float: right;margin-left: 5px;">
          <BasicTable
            :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            @register="registerTable"
            :data-source="tableData"
          >
            <template #model="{ record }">
              <span v-if="thisCheckKey.scope=='1'">通用</span>
              <span v-if="thisCheckKey.scope=='0' && thisCheckKey.model=='ZW'">总账</span>
              <span v-if="thisCheckKey.scope=='0' && thisCheckKey.model=='FA'">固定资产</span>
              <span v-if="thisCheckKey.scope=='0' && thisCheckKey.model=='STOCK'">存货</span>
            </template>
            <template #definedName="{ record }">
              {{ thisCheckKey.definedName }}
            </template>
            <template #shuXing="{ record }">
              <span v-if="thisCheckKey.shuxing=='1'">文本</span>
              <span v-if="thisCheckKey.shuxing=='2'">日期</span>
              <span v-if="thisCheckKey.shuxing=='3'">整数</span>
              <span v-if="thisCheckKey.shuxing=='4'">小数</span>
              <span v-if="thisCheckKey.shuxing=='5'">是否</span>
            </template>

            <template #recordName="{ record, index }">
              <span v-if="record.id == null || record.id==''">
                <a-input v-if="thisCheckKey.shuxing=='1'" v-model:value="record.recordName" style="width:75%"/>
                <a-date-picker v-if="thisCheckKey.shuxing=='2'" v-model:value="record.recordName" :locale="localeCn" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width:75%"/>
                <a-input-number v-if="thisCheckKey.shuxing=='3'" v-model:value="record.recordName" :min="0" :precision="0" style="width:75%"/>
                <a-input-number v-if="thisCheckKey.shuxing=='4'" v-model:value="record.recordName" :min="0" :precision="5" style="width:75%"/>
                &nbsp;
                <CheckOutlined @click="saveData(record)" />
                &nbsp;
                <CloseOutlined @click="delData(index)" />
              </span>
              <span v-else>{{ record.recordName }}</span>
            </template>

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

              <div v-if="thisCheckKey.shuxing!='5' || record.id == null || record.id==''">
                <a-popover placement="bottom">
                  <a-button style="padding: 0px 4px; height: 20px;">
                    <CaretDownFilled />
                  </a-button>
                  <template #content>
                    <!--              <p v-if="record.flag=='1'" style="cursor: pointer" @click="openEdit(record)"><FormOutlined /> 编辑</p>-->
                    <p v-if="record.flag=='0'" class="p_specifics" @click="editFlagData(record)" style="cursor: pointer">
                      <CheckCircleOutlined/>启用
                    </p>
                    <p v-if="record.flag=='1'" class="p_specifics" @click="editFlagData(record)" style="cursor: pointer">
                      <CloseCircleOutlined/>停用
                    </p>
                    <!--              <p v-if="record.flag=='0'" style="cursor: pointer" @click="del(record)"><DeleteOutlined /> 删除</p>-->
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
import { BasicTable, useTable } from '/@/components/Table'
import {PageWrapper} from '/@/components/Page'
import ClassTree from './ClassTree.vue'
import Edit from './popup/edit.vue'
import Excel from './popup/excel.vue'
import { useModal } from '/@/components/Modal'
import {onMounted, reactive, ref} from 'vue'
import Icon from '/@/components/Icon/index'
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
  FilterFilled,
  SortDescendingOutlined,
  CheckOutlined,
  SortAscendingOutlined,
  UnorderedListOutlined,
  CloseOutlined
} from '@ant-design/icons-vue'
import {Select as ASelect,Input as AInput,InputNumber as AInputNumber,DatePicker as ADatePicker,List as AList,Row as ARow,Col as ACol ,Card as ACard,Popover as APopover,Button as AButton,Tag as ATag } from 'ant-design-vue'
import {
  deleteDefinedRecord,
  editByFlag,
  excelDefinedRecord,
  findAllDefinedRecordList,
  findByName as findOriginByName,
  saveDefinedRecord as saveOriginDefinedRecord,
  saveDefinedRecord
} from "/@/api/record/system/origin-defined-record";
import {excelDefinedFile, getDefinedFileList} from "/@/api/record/system/origin-defined-file";
import {useMessage} from "/@/hooks/web/useMessage";
import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {getCurrentAccountName, getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import {message} from "ant-design-vue/lib/components";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
//import {add,pad} from "/@/views/boozsoft/gdzc/gu-ding-zi-chan-add/calculation";
const add=null
const pad=null
import localeCn from 'ant-design-vue/es/date-picker/locale/zh_CN';
import {getOrganizeAll} from "/@/api/record/group/im-organize";
import {
  findByOriginIdAndUniqueCode,
  findByUniqueCode, save
} from "/@/api/record/system/group-defined-record-account";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {
  findBukongRecordCode,
  findMaxRecordCode,
  findByName as findGroupByName,
  saveDefinedRecord as saveGroupDefinedRecord
} from "/@/api/record/system/group-defined-record";

const AListItem = AList.Item
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option

const {
  createSuccessModal,
  createErrorModal,
  createWarningModal,
  createConfirm
} = useMessage()

const {closeCurrent} = useTabs(router);

const formItems = ref({
  selectType: '1'
})

//查询组织
const organizeList:any = ref([])
const originId:any = ref('')
async function reloadOrigin(){
  organizeList.value = await getOrganizeAll()
  if (organizeList.value.length>0){
    originId.value = organizeList.value[0].uniqueCode
    pageParameter.originId = originId.value
  }
}
//改变组织
async function changeOrigin() {
  pageParameter.originId = originId.value
  await reloadDefinedFile()
  await changCode()
}

const tableData: any = ref([])
const tableAllData: any = ref([])

const definedCode = ref()
const definedFileList: any = ref([])
async function reloadDefinedFile(){
  definedFileList.value = await getDefinedFileList(originId.value)
  if (definedFileList.value.length>0){
    definedCode.value = definedFileList.value[0].definedCode
  }
}

const thisCheckKey:any = ref({})

function handleSelect(data) {
  if (null != data.definedCode) {
    thisCheckKey.value = data.item
    definedCode.value = data.definedCode
    // if (data.deptList.length > 0) classList.value = data.classList
    changCode()
  }
}

async function changCode() {
  isAdd.value = false
  tableAllData.value = await findAllDefinedRecordList(definedCode.value,originId.value)
  tableData.value = tableAllData.value
  await setPagination({
    total: tableData.value.length
  })
}

const CrudApi = {
  // list: findDefinedRecordList,
  columns: [
    {
      title: '功能模块',
      dataIndex: 'model',
      width: 100,
      ellipsis: true,
      align: 'left',
      slots: { customRender: 'model' }
    },
    {
      title: '自定义名称',
      dataIndex: 'definedName',
      width: 200,
      ellipsis: true,
      align: 'left',
      slots: { customRender: 'definedName' }
    },
    {
      title: '字段类型',
      dataIndex: 'shuXing',
      width: 150,
      ellipsis: true,
      slots: { customRender: 'shuXing' }
    },
    {
      title: '档案编码',
      dataIndex: 'recordCode',
      align: 'left',
      width: 200,
      ellipsis: true
    },
    {
      title: '档案名称',
      dataIndex: 'recordName',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      align: 'left',
      ellipsis: true,
      slots: { customRender: 'recordName' }
    },
    /*{
      title: '说明',
      dataIndex: 'remarks',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true
    },*/
    {
      title: '状态',
      dataIndex: 'flag',
      width: 100,
      ellipsis: true,
      slots: { customRender: 'flag' }
    }
  ],
  editData: {
    definedCode: '',
    recordCode: '',
    recordName: '',
    flag: '',
    remarks: ''
  }
}
// 这是示例组件
const [registerTable, {
  reload,
  getDataSource,
  setTableData,
  setPagination,
  getPaginationRef
}] = useTable({
  // api: async (params) => {
  //   return CrudApi.list
  // },
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  pagination: {
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['10', '25', '50', '100'],
    showTotal: t => `总共${t}条数据`
  },
  actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  },
  searchInfo: {
    definedCode:definedCode.value
  }
})
const [registerEditPage, { openModal: openEditPage }] = useModal()
const [registerExcelPage, { openModal: openExcelPage }] = useModal()
const val = {
  id: null,
  definedCode: '',
  recordCode: '',
  recordName: '',
  flag: '1',
  remarks: '',
  originId: ''
}

const openAdd = async () => {
  if (definedCode.value!='' && definedCode.value!=null && definedCode.value!='0' && definedCode.value!='ZW' && definedCode.value!='FA' && definedCode.value!='STOCK') {
    if (thisCheckKey.value.shuxing!='5') {
      if (!isAdd.value) {
        isAdd.value = true
        await maxCode()
        tableData.value.push({
          id: null,
          definedCode: definedCode.value,
          recordCode: recordCode.value,
          recordName: null,
          flag: '1',
          remarks: '',
          originId: originId.value
        })
        // console.log(tableData.value)
        setTableData([]) // 清空可能残留的数据
        setTableData(tableData.value)
        await setPagination({
          total: tableData.value.length
        })
      } else {
        createErrorModal({
          iconType: 'warning',
          title: '添加',
          content: '请先保存未完成档案信息在进行增行！'
        })
      }
    } else {
      createErrorModal({
        iconType: 'warning',
        title: '添加',
        content: '字段类型为“是否”时，系统自动添加档案，不允许手动添加！'
      })
    }
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '添加',
      content: '请先选择自定义项设置！'
    })
  }
}

const recordCode = ref('')
const isAdd = ref(false)
async function maxCode(){
  let str = ''
  const item1 = await findBukongRecordCode(definedCode.value,originId.value)
  if (item1!=null && item1.recordCode!=null && item1.recordCode!=''){
    str = item1.recordCode
  } else {
    const item2 = await findMaxRecordCode(definedCode.value,originId.value)
    if (item2 != null && item2.recordCode != null && item2.recordCode != '') {
      str = add(item2.recordCode, 1)+''
    } else {
      str = '1'
    }
  }
  recordCode.value = str
  // console.log(str)
  return str
}

async function saveData(data:any) {
  if (data.recordName!=null && data.recordName!='') {
    isAdd.value = false
    const list = await findByUniqueCode(data.recordCode,data.definedCode)
    const list1 = list.filter(item=>item.originId==originId.value && item.ctype=='1')
    if (list1.length>0){
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '该档案已经分配给本组织，请使用“引入”菜单，直接引入该档案信息！'
      })
      return false
    }
    let dateTime = new_Date()
    data.successState = '1'
    data.originId = originId.value
    data.applyUser = useUserStoreWidthOut().getUserInfo.realName
    data.applyDate = dateTime
    data.approveUser = useUserStoreWidthOut().getUserInfo.realName
    data.approveDate = dateTime
    data.ctype = '1'
    //自动生效查询集团表是否有记录
    const res = await findGroupByName({
      definedCode: data.definedCode,
      recordName: data.recordName
    })
    if (res.length>0){
      //如果集团里有直接分配
      data.recordCode = res[0].recordCode
    } else {
      //往集团表插入数据
      await saveGroupDefinedRecord(data)
      const item1 = {
        id: list1[0].id,
        uniqueCode: data.recordCode,
        ctype: '1',
        originId: originId.value,
        assignUser: useUserStoreWidthOut().getUserInfo.id,
        assignDate: dateTime,
        flag: '1',
        acceptUser: useUserStoreWidthOut().getUserInfo.id,
        acceptDate: dateTime,
        definedCode: data.definedCode
      }
      await save(item1)
      await saveOriginDefinedRecord(data)
    }
    await saveDefinedRecord(data)
    await changCode()
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '档案名称不能为空！'
    })
  }
}

async function checkName(record) {
  if (record.recordName == null || record.recordName == '') {
    return true
  }
  const res = await findOriginByName({
    definedCode: record.definedCode,
    recordName: record.recordName,
    originId: originId.value,
  })
  if (res.length != 0) {
    createErrorModal({
      iconType: 'warning',
      title: '输入失败',
      content: '档案名称已存在，请重新输入！'
    })
    record.recordName = ''
    return false
  }
  return true
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

const openEdit = (data:any) => {
  openEditPage(true, {
    data: data
  })
}

const openExcel = () => {
  if (definedCode.value!='' && definedCode.value!=null) {
    if (thisCheckKey.value.shuxing != '5') {
      openExcelPage(true, {
        data: {
          definedCode: definedCode.value,
          originId: originId.value
        }
      })
    } else {
      createErrorModal({
        iconType: 'warning',
        title: '添加',
        content: '字段类型为“是否”时，系统自动添加档案，不允许手动导入！'
      })
    }
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '添加',
      content: '请先选择自定义项设置！'
    })
  }
}
//导入Excel
async function saveExcel(data:any) {
  await excelDefinedRecord(data)
  /*await reload(
    {
      searchInfo: {
        definedCode: definedCode.value
      }
    }
  )*/
  await changCode()
}

//文件导出
async function exportExcel() {
  // debugger
  if (definedCode.value!='' && definedCode.value!=null) {
    const arrHeader = ['编码', '名称'];
    const columnList = ['recordCode', 'recordName']
    const arrData = tableData.value.map((item) => columnList.map(column => item[column]));
    // console.log(arrHeader)
    // 保证data顺序与header一致
    aoaToSheetXlsx({
      data: arrData,
      header: arrHeader,
      filename: definedCode.value + '自定义项档案.xlsx',
    });
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '导出',
      content: '请先选择自定义项设置！'
    })
  }
}

const del = async(data:any) => {
  await deleteDefinedRecord(data)
  // await deleteCate(id, data)
  // alert('删除成功！')
  createSuccessModal({
    iconType: 'success',
    title: '删除',
    content: '删除成功！'
  })
  /*await reload(
    {
      searchInfo: {
        definedCode: definedCode.value
      }
    }
  )*/
  await changCode()
}

onMounted(async() => {
  await reloadOrigin()
  await reloadDefinedFile()
  await reload(
    {
      searchInfo: {
        definedCode: definedCode.value
      }
    }
  )
})

async function delData(num) {
  isAdd.value = false
  tableData.value.splice(num,1)
  setTableData([]) // 清空可能残留的数据
  setTableData(tableData.value)
  await setPagination({
    total: tableData.value.length
  })
}

async function editFlagData(data: any) {
  await editByFlag(data);
  /*await reload(
    {
      searchInfo: {
        definedCode: definedCode.value
      }
    }
  )*/
  await changCode()
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
const onSelectChange = (selectedRowKeys, row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
};

const editOpen = () => {
  if (checkRow.value.length == 1) {
    openEditPage(true, {
      data: checkRow.value[0]
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '修改',
      content: '请选择一条进行编辑！'
    })
  }
}

async function delList() {
  if (checkRow.value.length > 0) {
    if (thisCheckKey.value.shuxing!='5') {
      for (let i = 0; i < checkRow.value.length; i++) {
        const item = checkRow.value[i]
        const res = await findByUniqueCode(item.recordCode,definedCode.value)
        if (res.length > 0) {
          const list = res.filter(aa => aa.ctype == '2' && aa.originId == originId.value)
          if (list.length > 0) {
            createWarningModal({
              iconType: 'warning',
              title: '删除',
              content: '自定义档案已分配不允许删除！'
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
          for (let i = 0; i < checkRow.value.length; i++) {
            const item = checkRow.value[i]
            const item1 = await findByOriginIdAndUniqueCode(originId.value,item.recordCode,definedCode.value)
            if (item1.length>0) {
              item1[0].flag = '0'
              item1[0].acceptUser = ''
              item1[0].acceptDate = ''
              await save(item1[0])
            }
            // console.log(item.id)
            if (item.id!=null && item.id!='') {
              await deleteDefinedRecord(item)
            }
          }
          message.success('删除成功！')
          checkRow.value = []
          state.selectedRowKeys = []
          // await reload()
          await changCode()
        },
        onCancel: () => {
          return false
        }
      })
    } else {
      createErrorModal({
        iconType: 'warning',
        title: '删除',
        content: '字段类型为“是否”时，系统自动添加档案，不允许手动删除！'
      })
    }
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '删除',
      content: '请选择需要删除的内容！'
    })
    return false
  }
}

function onSearch() {
}

const pageParameter: any = reactive({
  showRulesSize: 'MIN',
  total: 0,
  thisAdInfo: {}
})

//引入
import Into from './popup/into.vue'
const [registerIntoPage, {openModal: openIntoPage}] = useModal()
const openInto = () => {
  openIntoPage(true, {
    originId: originId.value,
    definedCode: definedCode.value,
  })
}

async function saveInto(data) {
  await changCode()
}

//分配
import Assign from './popup/assign.vue'
const [registerAssignPage, {openModal: openAssignPage}] = useModal()
const openAssign = () => {
  openAssignPage(true, {
    originId: originId.value,
    definedCode: definedCode.value,
  })
}

const loadMark = ref(false);
async function saveAssign(data) {
}

</script>
<style src="./global-menu-index.less" lang="less" scoped></style>
<style scoped lang="less">
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
  background-color: #f2f2f2 !important;
  border-color: #e5e7eb !important;
}

:deep(.ant-picker){
  border: none !important;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  input {
    font-size: 14px;
    font-weight: bold;
    border: none !important;
  }
}
</style>
