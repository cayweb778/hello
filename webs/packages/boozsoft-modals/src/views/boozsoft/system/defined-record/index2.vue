<template>
  <div>
    <div class="app-container">

      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">-->
<!--        <div class="container-head-title" style="margin-left: 20px;">
          <b class="noneSpan"><UnorderedListOutlined />&nbsp;&nbsp;自定义项档案</b>
        </div>-->
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">自定义项档案</b>
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
<!--          <button
            type="button"
            class="ant-btn ant-btn-me"
          ><span>打印</span></button>-->
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="closeCurrent()"
          ><span>退出</span></button>
<!--          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="openSelectRecord()"
          ><span>测试弹框</span></button>-->
        </div>
      </div>

      <div style="clear: none"/>
      <div style="margin-top: -30px;margin-left: 80px;">
        <div style="display: inline-block;float: left;margin-top: -25px;">
          <AccountPicker theme="three" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">共 {{tableAllData.length}} 条记录</div>
        <div style="float: right; margin-left: 10px;">
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
            <!--            <template #title>
                          <b>设置表格字号</b>
                        </template>-->
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
      <div style="clear:both"/>

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
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <div class="w-1/4 xl:w-1/5" style="width: 300px;min-height: 300px;border: 1px #cccccc solid;margin-right: .2%;margin-bottom: 58px;">
          <ClassTree @select="handleSelect" v-model="pageParameter"/>
        </div>
        <div style="width: calc(100% - 300px); float: right;margin-left: 5px;">
          <BasicTable
            :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys,getCheckboxProps:rowSelection.getCheckboxProps, onChange: onSelectChange }"
            @selection-change="selectionChange"
            @row-click="rowClick"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :scroll="{ y: windowHeight }"
            style="width: 900px;"
            @register="registerTable"
            :data-source="tableData"
          >
            <template #model="{ record, index }">
              <span v-if="(record.id != null && record.id!='') || (tableData[index-1].id!=null && thisCheckKey.shuxing!='5' && isAdd==true)">
              <span v-if="thisCheckKey.scope=='1'">通用</span>
              <span v-if="thisCheckKey.scope=='0' && thisCheckKey.model=='ZW'">总账</span>
              <span v-if="thisCheckKey.scope=='0' && thisCheckKey.model=='FA'">固定资产</span>
              <span v-if="thisCheckKey.scope=='0' && thisCheckKey.model=='STOCK'">存货</span>
              </span>
            </template>
            <template #definedName="{ record, index }">
              <span v-if="(record.id != null && record.id!='') || (tableData[index-1].id!=null && thisCheckKey.shuxing!='5' && isAdd==true)">
              {{ thisCheckKey.definedName }}
              </span>
            </template>
            <template #shuXing="{ record, index }">
              <span v-if="(record.id != null && record.id!='') || (tableData[index-1].id!=null && thisCheckKey.shuxing!='5' && isAdd==true)">
              <span v-if="thisCheckKey.shuxing=='1'">文本</span>
              <span v-if="thisCheckKey.shuxing=='2'">日期</span>
              <span v-if="thisCheckKey.shuxing=='3'">整数</span>
              <span v-if="thisCheckKey.shuxing=='4'">小数</span>
              <span v-if="thisCheckKey.shuxing=='5'">是否</span>
              </span>
            </template>

            <template #recordName="{ record, index }">
              <span v-if="(record.id == null || record.id=='') && (tableData[index-1].id!=null && thisCheckKey.shuxing!='5' && isAdd==true)">
                <a-input v-if="thisCheckKey.shuxing=='1'" v-model:value="record.recordName" @blur="checkName(record)" style="width:75%"/>
                <a-date-picker v-if="thisCheckKey.shuxing=='2'" v-model:value="record.recordName" @blur="checkName(record)" :locale="localeCn" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width:75%"/>
                <a-input-number v-if="thisCheckKey.shuxing=='3'" v-model:value="record.recordName" @blur="checkName(record)" :min="0" :precision="0" style="width:75%"/>
                <a-input-number v-if="thisCheckKey.shuxing=='4'" v-model:value="record.recordName" @blur="checkName(record)" :min="0" :precision="5" style="width:75%"/>
                &nbsp;
                <CheckOutlined @click="saveData(record)" />
                &nbsp;
                <CloseOutlined @click="delData(index)" />
              </span>
              <span v-else>{{ record.recordName }}</span>
            </template>

            <template #flag="{ record }">
              <span v-if="record.flag!=null && record.flag!=''">
                <a-tag
                  :color="record.flag === '1' ? 'green' : 'volcano'"
                >
                  {{ record.flag === '1' ? '启用' : '停用' }}
                </a-tag>
              </span>
            </template>
            <template #action="{ record }">

              <div v-if="(thisCheckKey.shuxing!='5' || record.id == null || record.id=='') && record.flag!=null && record.flag!=''">
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
          <div class="pagination-text" v-show="showPaginationText">
            共 {{paginationNumber}} 条记录&nbsp;&nbsp; 每页 200 条
          </div>
        </div>
      </PageWrapper>

      <SelectRecode @save="saveSelectRecord" @register="registerSelectRecordPage"/>
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
  CloseOutlined,ProfileOutlined
} from '@ant-design/icons-vue'
import {Select as ASelect,Input as AInput,InputNumber as AInputNumber,DatePicker as ADatePicker,List as AList,Row as ARow,Col as ACol ,Card as ACard,Popover as APopover,Button as AButton,Tag as ATag } from 'ant-design-vue'
import {
  deleteDefinedRecord,
  editByFlag,
  excelDefinedRecord,
  findAllDefinedRecordList,
  findBukongRecordCode, findByName,
  findDefinedRecordList,
  findMaxRecordCode,
  saveDefinedRecord
} from "/@/api/record/system/defined-record";
import {excelDefinedFile, getDefinedFileList} from "/@/api/record/system/defined-file";
import {useMessage} from "/@/hooks/web/useMessage";
//import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
const aoaToSheetXlsx=null
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";
import {getCurrentAccountName, getThisIndexImg, hasBlank} from "/@/api/task-api/tast-bus-api";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {message} from "ant-design-vue/lib/components";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import localeCn from 'ant-design-vue/es/date-picker/locale/zh_CN';
import {
  findBukongRecordCode as findBukongGroupRecordCode,
  findMaxRecordCode as findMaxGroupRecordCode,
  findByName as findGroupByName,
  saveDefinedRecord as saveGroupDefinedRecord
} from "/@/api/record/system/group-defined-record";
import {
  findByName as findOriginByName,
  saveDefinedRecord as saveOriginDefinedRecord
} from "/@/api/record/system/origin-defined-record";
import {findByUniqueCode, save,findByTenantIdAndUniqueCode} from "/@/api/record/system/group-defined-record-account";

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

const windowHeight = (window.innerHeight - (300))

const formItems = ref({
  selectType: '1'
})

const tableData: any = ref([])
const tableAllData: any = ref([])

const showPaginationText = ref(false)
const paginationNumber = ref(0)

const definedCode = ref()
const definedFileList: any = ref([])
async function reloadDefinedFile(){
  definedFileList.value = await useRouteApi(getDefinedFileList, {schemaName: dynamicTenantId})({})
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
  showPaginationText.value = false
  let len = 0
  /*reload(
    {
      searchInfo: {
        definedCode: definedCode.value
      }
    }
  )*/
  tableAllData.value = await useRouteApi(findAllDefinedRecordList,{schemaName: dynamicTenantId})(definedCode.value)
  tableData.value = tableAllData.value
  let num = 50-(tableAllData.value.length%50)
  for (let i=0;i<num;i++){
    tableData.value.push({})
  }
  await setPagination({
    total: tableData.value.length
  })
  len = tableData.value.filter(item=>item.id!=null && item.id!='').length
  paginationNumber.value = len
  showPaginationText.value = true
}

const dynamicTenantId = ref(getCurrentAccountName(true))
const defaultAdName = ref(useCompanyOperateStoreWidthOut().getSchemaName)
const CrudApi = {
  // list: findDefinedRecordList,
  columns: [
    /*{
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
    },*/
    {
      title: '字段类型',
      dataIndex: 'shuXing',
      width: 100,
      ellipsis: true,
      slots: { customRender: 'shuXing' }
    },
    {
      title: '档案编码',
      dataIndex: 'recordCode',
      align: 'left',
      width: 100,
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
      width: 80,
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
  getPaginationRef,
  deleteSelectRowByKey
}] = useTable({
  // api: async (params) => {
  //   return await useRouteApi(CrudApi.list, {schemaName: dynamicTenantId})({definedCode: definedCode.value})
  // },
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: true,
  indexColumnProps:{ fixed:true },
  pagination: {
    pageSize: 200,
    // showSizeChanger: false,
    simple:true,
    showTotal: t => `共${t}条记录    每页200条`
  },
  /*pagination: {
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['10', '25', '50', '100'],
    showTotal: t => `总共${t}条数据`
  },*/
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
  remarks: ''
}

const openAdd = async () => {
  if (definedCode.value!='' && definedCode.value!=null) {
    if (thisCheckKey.value.shuxing!='5') {
      if (!isAdd.value) {
        isAdd.value = true
        /*val.definedCode = definedCode.value
      openEditPage(true, {
        data: val,
        dynamicTenantId: dynamicTenantId.value,
        defaultAdName: defaultAdName.value
      })*/
        if (defaultPage.value) {
          await maxCode()
        } else {
          await maxGroupCode()
        }
        let list:any = []
        tableData.value = []
        list = tableAllData.value.filter(item=>item.id!=null && item.id!='')
        console.log(tableAllData.value)
        list.push({
          id: null,
          definedCode: definedCode.value,
          recordCode: recordCode.value,
          recordName: null,
          flag: '1',
          remarks: ''
        })
        tableData.value = list
        let num = 50-(tableData.value.length%50)
        for (let i=0;i<num;i++){
          tableData.value.push({})
        }
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
  const item1 = await useRouteApi(findBukongRecordCode,{schemaName: dynamicTenantId})(definedCode.value)
  if (item1!=null && item1.recordCode!=null && item1.recordCode!=''){
    str = item1.recordCode
  } else {
    const item2 = await useRouteApi(findMaxRecordCode, {schemaName: dynamicTenantId})(definedCode.value)
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

async function maxGroupCode(){
  let str = ''
  const item1 = await findBukongGroupRecordCode(definedCode.value)
  if (item1!=null && item1.recordCode!=null && item1.recordCode!=''){
    str = item1.recordCode
  } else {
    const item2 = await findMaxGroupRecordCode(definedCode.value)
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

/**
 * 字符串前补0
 * @param num
 * @param n
 */
function pad(num, n) {
  let len = num.toString().length;
  while(len < n) {
    num = "0" + num;
    len++;
  }
  return num;
}

/**
 * 加法
 * @param a
 * @param b
 */
function add(a, b) {
  let c, d, e;
  try {
    c = a.toString().split(".")[1].length;
  } catch (f) {
    c = 0;
  }
  try {
    d = b.toString().split(".")[1].length;
  } catch (f) {
    d = 0;
  }
  return e = Math.pow(10, Math.max(c, d)), (mul(a, e) + mul(b, e)) / e;
}

/**
 * 乘法
 * @param a
 * @param b
 */
function mul(a, b) {
  let c = 0,
    d = a.toString(),
    e = b.toString();
  try {
    c += d.split(".")[1].length;
  } catch (f) {}
  try {
    c += e.split(".")[1].length;
  } catch (f) {}
  return Number(d.replace(".", "")) * Number(e.replace(".", "")) / Math.pow(10, c);
}

async function saveData(data:any) {
  if (data.recordName!=null && data.recordName!='') {
    isAdd.value = false
    //独立账套
    if (defaultPage.value) {
      await useRouteApi(saveDefinedRecord, {schemaName: dynamicTenantId})(data)
    } else {//集团账套
      const list = await findByUniqueCode(data.recordCode,data.definedCode)
      const list1 = list.filter(item=>item.originId==originId.value && item.ctype=='1')
      const list2 = list.filter(item=>item.tenantId==dynamicTenantId.value && item.ctype=='2')
      if (list2.length>0){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '该档案已经分配给本账套，请使用“引入”菜单，直接引入该档案信息！'
        })
        return false
      }
      let dateTime = new_Date()
      data.successState = '1'
      data.originId = originId.value
      data.tenantId = dynamicTenantId.value
      data.applyUser = useUserStoreWidthOut().getUserInfo.realName
      data.applyDate = dateTime
      data.approveUser = useUserStoreWidthOut().getUserInfo.realName
      data.approveDate = dateTime
      data.ctype = '2'
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
      }
      //分配表插入数据
      if (list1.length==0) {
        const item1 = {
          id: null,
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
      } else {
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
      }
      const item2 = {
        id: null,
        uniqueCode: data.recordCode,
        ctype: '2',
        originId: originId.value,
        tenantId: dynamicTenantId.value,
        assignUser: useUserStoreWidthOut().getUserInfo.id,
        assignDate: dateTime,
        flag: '1',
        acceptUser: useUserStoreWidthOut().getUserInfo.id,
        acceptDate: dateTime,
        definedCode: data.definedCode
      }
      await save(item2)
      //组织表插入数据
      const res1 = await findOriginByName({
        definedCode: data.definedCode,
        recordName: data.recordName,
        originId: originId.value,
      })
      if (res1.length>0) {
        await saveOriginDefinedRecord(data)
      }
      //账套表插入数据
      await useRouteApi(saveDefinedRecord, {schemaName: dynamicTenantId})(data)
    }
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
  const res = await useRouteApi(findByName, {schemaName: dynamicTenantId})({
    definedCode: record.definedCode,
    recordName: record.recordName
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
    data: data,
    dynamicTenantId: dynamicTenantId.value,
    defaultAdName: defaultAdName.value
  })
}

const openExcel = () => {
  if (definedCode.value!='' && definedCode.value!=null) {
    if (thisCheckKey.value.shuxing != '5') {
      openExcelPage(true, {
        data: {
          definedCode: definedCode.value,
        },
        dynamicTenantId: dynamicTenantId.value,
        defaultAdName: defaultAdName.value
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
  await useRouteApi(excelDefinedRecord, {schemaName: dynamicTenantId})(data)
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
  await useRouteApi(deleteDefinedRecord, {schemaName: dynamicTenantId})(data)
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
  await useRouteApi(editByFlag, {schemaName: dynamicTenantId})(data);
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

const editOpen = () => {
  if (checkRow.value.length == 1) {
    openEditPage(true, {
      data: checkRow.value[0],
      dynamicTenantId: dynamicTenantId.value,
      defaultAdName: defaultAdName.value
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
      createConfirm({
        iconType: 'error',
        title: '警告',
        content: '删除后数据将不能恢复，你确认要删除吗?',
        onOk: async () => {
          for (let i = 0; i < checkRow.value.length; i++) {
            const item = checkRow.value[i]
            console.log(item.id)
            if (item.id!=null && item.id!='') {
              const item1 = await findByTenantIdAndUniqueCode(dynamicTenantId.value,item.recordCode,item.definedCode)
              if (item1.length>0) {
                item1[0].flag = '0'
                item1[0].acceptUser = ''
                item1[0].acceptDate = ''
                await save(item1[0])
              }
              await useRouteApi(deleteDefinedRecord, {schemaName: dynamicTenantId})(item)
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
  companyCode: '100',
  companyName: '北京希格科技',
  ifUnit: false,
  total: 0,
  thisAdInfo: {},
  defaultAdName: '',
  originId: ''
})
const defaultPage = ref(false)
/*onMounted(async() => {
  // 进页面执行
  getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(res => {
    if (null != res && res.independent == 0) {
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
  })
})*/

const originId = ref('')
const dynamicAdReload = async (obj) => {
  // const dataBase:any = await findDataBase(obj.accId,obj.year)
  dynamicTenantId.value = obj.accountMode
  defaultAdName.value = obj.accId
  getThisAdInfoData({'accId': obj.accId}).then(item => {
    originId.value = item.accGroup
    pageParameter.originId = item.accGroup
    if (item.independent == 0) {
      defaultPage.value = false
    } else {
      defaultPage.value = true
    }
    pageParameter.definedCode = definedCode.value
  })
  await reloadDefinedFile()
  let data: any = {}
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  data.definedCode = definedCode.value
  // let res: any = await useRouteApi(findAllDefinedRecordList, {schemaName: obj.accountMode})(data)
  // setTableData([]) // 清空可能残留的数据
  // setTableData(res)
  // 底部分页信息
  pageParameter.thisAdInfo = {}
  pageParameter.thisAdInfo = obj
  // pageParameter.total = res.length
  // setPagination({total: res.length})
}

import SelectRecode from './popup/select-record.vue'
const [registerSelectRecordPage, { openModal: openSelectRecordPage }] = useModal()
const openSelectRecord = () => {
  openSelectRecordPage(true, {
    dynamicTenantId: dynamicTenantId.value,
    definedCode: definedCode.value
  })
}
const record:any = ref()
function saveSelectRecord(data){
  record.value = data
}

//引入
import Into from './popup/into.vue'
import {useUserStoreWidthOut} from "/@/store/modules/user";
const [registerIntoPage, {openModal: openIntoPage}] = useModal()
const openInto = () => {
  openIntoPage(true, {
    dynamicTenantId: dynamicTenantId.value,
    definedCode: definedCode.value,
  })
}

async function saveInto(data) {
  await changCode()
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
    right: 50%;
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
  margin: 0
}

:deep(.vben-basic-table){
  min-height: 500px;
  height: calc(100% - 160px);
  //margin-bottom: 20px;
}

:deep(.ant-input),:deep(.ant-select),:deep(.ant-btn){
  border: 1px solid #c9c9c9;
}

</style>
