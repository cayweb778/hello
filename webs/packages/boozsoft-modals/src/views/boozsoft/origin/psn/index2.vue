<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false" >-->
        <div class="container-head-title" style="margin-left: 20px;">
          <b class="noneSpan"><TeamOutlined style="font-size: 26px;" />&nbsp;&nbsp;组织-人员信息</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            v-if="isAuto=='1'"
            @click="openAdd()"
          ><span>新增</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            v-if="isAuto=='0'"
            @click="openApplyList()"
          ><span>新增申请</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="editOpen()"
          ><span>修改</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="editOpenBatch()"
          ><span>批量修改</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="delList()"
          ><span>删除</span></button>
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
      </div>
      <div class="app-container-neck">
        <div style="display: inline-block;float: left;margin-top: .2%;" class="select-div">
          &ensp;<span>所属组织：</span>
          <a-select v-model:value="originId" @change="changeOrigin()" style="width: 250px;margin-top: -10px;margin-left: 10px">
            <a-select-option v-for="item in organizeList" :value="item.uniqueCode">
              {{ item.orgSimpName }}
            </a-select-option>
          </a-select>
        </div>
        <div style="float: left; margin-left: 30px;line-height: 30px;">共 {{psnList.length}} 条记录</div>
        <div style="float: right; margin-left: 10px">
          <a-button>
            <SyncOutlined :style="{ fontSize: '14px' }"/>
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
            <!--            <template #title>
                          <b>设置表格字号</b></template>-->
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
          <a-popover class="ant-btn-default" placement="bottom">
            <template #content>
            <span class="group-btn-span-special2" @click="onChangeSwitch('')"
                  :style="flag==''?{backgroundColor: '#0096c7',color: 'white'}:''">
                <ReadOutlined/>&nbsp;&emsp;全部&emsp;&ensp;<CheckOutlined
              v-if="flag==''"/></span><br/>
              <span class="group-btn-span-special2" @click="onChangeSwitch('1')"
                    :style="flag=='1'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SafetyOutlined/>&nbsp;&emsp;正常&emsp;&ensp;<CheckOutlined
                v-if="flag=='1'"/></span><br/>
              <span class="group-btn-span-special2" @click="onChangeSwitch('0')"
                    :style="flag=='0'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <RestOutlined/>&nbsp;&emsp;停用&emsp;&ensp;<CheckOutlined
                v-if="flag=='0'"/></span>
            </template>
            <a-button>
              <PicLeftOutlined :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
<!--          <a-button @click="()=>{
            if (!visible){ visible = true;reloadColumns()}
            return false
          }">
            <FilterFilled :style="{ fontSize: '14px' }"/>
          </a-button>-->
        </div>
        <div style="float: right; position: relative">
          <!-- 搜索 -->
          <!--          <label style="font-size: 14px">检索条件：</label>-->
          <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;"
                    class="special_select">
            <a-select-option style="font-size: 12px;" value="psnCode">人员编码</a-select-option>
            <a-select-option style="font-size: 12px;" value="psnName">人员姓名</a-select-option>
            <a-select-option style="font-size: 12px;" value="psnSex">性别</a-select-option>
            <a-select-option style="font-size: 12px;" value="documentType">证件类型</a-select-option>
            <a-select-option style="font-size: 12px;" value="countryId">国家(地区)</a-select-option>
            <a-select-option style="font-size: 12px;" value="psnAddress">地址</a-select-option>
            <a-select-option style="font-size: 12px;" value="cellPhoneNum">手机</a-select-option>
            <a-select-option style="font-size: 12px;" value="psnEmail">Email</a-select-option>
            <a-select-option style="font-size: 12px;" value="psnQq">钉钉</a-select-option>
            <a-select-option style="font-size: 12px;" value="psnWechat">微信</a-select-option>
            <a-select-option style="font-size: 12px;" value="documentCode">证件号码</a-select-option>
          </a-select>
          <a-input-search
            placeholder=""
            style="width: 200px; border-radius: 4px"
            v-model:value="formItems.selectValue"
            @search="onSearch"
          />
        </div>
      </div>
    </div>
    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <div class="w-1/4 xl:w-1/5" style="width: 250px;min-height: 250px;border: 1px #cccccc solid;margin-right: .2%;margin-bottom: 60px;">
          <TypeTree @select="handleSelect" v-model="pageParameter"/>
        </div>
        <div style="width: calc(100% - 250px); float: right;margin-left: 5px;">
          <BasicTable
            ref="tableRef"
            class="w-3/4 xl:w-4/5"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
            :scroll="{ x: totalColumnWidth,y: windowHeight }"
            :dataSource="tableData"
            @row-click="condClick"
            @register="registerTable"
            :loading="loadMark"
          >
            <template #psnCode="{ record }">
              <a class="tableUStyle" @click="openEdit(record,false)">{{ record.psnCode }}</a>
            </template>
            <template #psnName="{ record }">
              <a class="tableUStyle" @click="openEdit(record,false)">{{ record.psnName }}</a>
            </template>
            <template #psnSex="{ record }"> {{ formatPsnSex(record.psnSex) }}</template>
            <template #psnType="{ record }"> {{ formatPsnType(record.psnType) }}</template>
            <template #documentType="{ record }">
<!--              <span v-if="record.documentType=='1'">居民身份证</span>
              <span v-if="record.documentType=='2'">港澳居民来往内地通行证</span>
              <span v-if="record.documentType=='3'">台湾居民来往大陆通行证</span>
              <span v-if="record.documentType=='4'">中国护照</span>
              <span v-if="record.documentType=='5'">外国护照</span>-->
              {{ formatDocumentType(record.documentType) }}
            </template>
            <template #flag="{ record }">
            <span>
              <a-tag :color="record.flag === '1' ? 'green' : 'volcano'">
                {{ record.flag === '1' ? '正常' : '停用' }}
              </a-tag>
            </span>
            </template>
<!--            <template #uniqueCodeDept="{ record }"> {{ formatUniqueCodeDept(record.uniqueCodeDept) }}</template>-->
            <template #cellPhoneNum="{ record }">
              <span v-if="record.cellPhoneNum!=null&&record.cellPhoneNum!=''">{{plusStr(record.cellPhoneNum,3,4,'****')}}</span>
            </template>
            <template #psnEmail="{ record }">
              <span v-if="record.psnEmail!=null&&record.psnEmail!=''">{{plusStr(record.psnEmail,3,4,'****@***')}}</span>
            </template>
            <template #documentCode="{ record }">
              <span v-if="record.documentCode!=null&&record.documentCode!=''">
                <span v-if="record.documentCode.length>10">{{plusStr(record.documentCode,3,4,'******')}}</span>
                <span v-else>{{plusStr(record.documentCode,3,0,'******')}}</span>
              </span>
            </template>
            <template #bankAccount="{ record }">
              <span v-if="record.bankAccount!=null&&record.bankAccount!=''">
                <span v-if="record.bankAccount.length>10">{{plusStr(record.bankAccount,3,4,'******')}}</span>
                <span v-else>{{plusStr(record.documentCode,3,4,'******')}}</span>
              </span>
            </template>
            <template #action="{ record }">

              <div>
                <a-popover placement="bottom">
                  <a-button style="padding: 0px 4px; height: 20px;">
                    <CaretDownFilled/>
                  </a-button>
                  <template #content>
                    <!--                <p v-if="record.flag=='1'" style="cursor: pointer" @click="openEdit(record,true)"><FormOutlined /> 编辑</p>-->
                    <p v-if="record.flag=='0'" class="p_specifics" style="cursor: pointer" @click="editFlagData(record)">
                      <CheckCircleOutlined/>
                      正常
                    </p>
                    <p v-if="record.flag=='1'" class="p_specifics" style="cursor: pointer" @click="editFlagData(record)">
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
        <a-drawer
          title="过滤漏斗"
          placement="right"
          :closable="true"
          v-if="visible"
          :mask="false"
          :visible="visible"
          :get-container="false"
          :wrap-style="{ position: 'absolute' }"
          @close="visible=false,reloadColumns()"
        >
          <ul>
            <li style="margin-top: 5%">
            <span style="color: black;font-weight: bold">
          辅助核算：
            </span>
              <div>

              </div>
            </li>
          </ul>
          <br/>
          <a-button type="primary" style="float: right;" @click="filterSearch">
            <span style="font-size: 14px">开始</span>
          </a-button>
        </a-drawer>
        <Add
          @save="saveAddData"
          @register="registerAddPage"
        />
        <Edit
          @save="saveData"
          @register="registerEditPage"
        />
        <BatchEdit
          @save="saveBatchData"
          @register="registerBatchEditPage"
        />
        <Excel @save="saveExcel" @register="registerExcelPage"/>

        <Into @save="saveInto" @register="registerIntoPage"/>
        <Assign @save="saveAssign" @register="registerAssignPage"/>

        <ApplyList @save="saveApplyList" @register="registerApplyListPage"/>
      </PageWrapper>
    </div>
  </div>
</template>
<script setup lang="ts">
import {BasicTable, useTable} from '/@/components/Table'
import {PageWrapper} from '/@/components/Page'
import {useRouteApi} from '/@/utils/boozsoft/datasource/datasourceUtil';
import TypeTree from './TypeTree.vue'
import Add from './popup/apply-add.vue';
import Edit from './popup/edit.vue';
import BatchEdit from './popup/batch-edit.vue';
import Excel from './popup/excel.vue'
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
  PieChartFilled, ReadOutlined, SafetyOutlined, RestOutlined,
  FilterFilled, CheckOutlined, SortDescendingOutlined, SortAscendingOutlined, EditOutlined,
  TeamOutlined
} from '@ant-design/icons-vue'
import {onMounted, reactive, ref} from 'vue'
import {psnFindAll, deletePsn, savePsn, editFlag, excelPsn,getPsnList} from '/@/api/record/system/origin-psn'
import {
  Input as AInput,
  Select as ASelect,
  Popover as APopover,
  Tabs as ATabs,
  DatePicker as ADatePicker,
  Tag as ATag,
  Popconfirm as APopconfirm,
  Checkbox as ACheckbox,
  Radio as ARadio,
  Table as ATable,
  Drawer as ADrawer,
  message
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
import {getDeptListById} from "/@/api/record/system/dept";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  getThisAdInfoData,
} from "/@/api/record/system/financial-settings";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {getOrganizeAll} from "/@/api/record/group/im-organize";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {
  findByOriginIdAndUniqueCode,
  findByUniqueCode,
  saveAccount
} from "/@/api/record/system/group-psn-account";
import {findDocumentTypeAll} from "/@/api/record/system/group-document-type";

const {closeCurrent} = useTabs(router);

const formItems = ref({
  selectType: 'psnCode',
  selectValue: ''
})

//查询组织
const organizeList:any = ref([])
const originId:any = ref('')
async function reloadOrigin(){
  organizeList.value = await getOrganizeAll()
  if (organizeList.value.length>0){
    originId.value = organizeList.value[0].uniqueCode
    pageParameter.originId = originId.value
    await changeOrigin()
  }
}
//改变组织
const isAuto:any = ref('0')
async function changeOrigin() {
  pageParameter.originId = originId.value
  await reloadTable()
  const permissionList = await findByUserIdAndOriginId(useUserStoreWidthOut().getUserInfo.id,originId.value,'psn')
  if (permissionList.length>0){
    isAuto.value = permissionList[0].isAuto
  } else {
    isAuto.value = '0'
  }
}

const {
  createConfirm,
  createWarningModal
} = useMessage()

const flag = ref('1')
const tableData:any = ref([]);
const tableDataAll:any = ref([]);
const CrudApi = {
  list: [],
  columns: [
    {
      title: '状态',
      dataIndex: 'flag',
      width: 100,
      ellipsis: true,
      fixed: 'left',
      slots: {customRender: 'flag'}
    },
    {
      title: '人员工号',
      dataIndex: 'jobNum',
      width: 120,
      ellipsis: true
    },
    {
      title: '编码',
      dataIndex: 'psnCode',
      width: 120,
      ellipsis: true,
      fixed: 'left',
      slots: {customRender: 'psnCode'}
    },
    {
      title: '姓名',
      dataIndex: 'psnName',
      width: 120,
      ellipsis: true,
      fixed: 'left',
      slots: {customRender: 'psnName'}
    },
    {
      title: '性别',
      dataIndex: 'psnSex',
      width: 100,
      ellipsis: true,
      slots: {customRender: 'psnSex'}
    },
    /*{
      title: '所属部门',
      dataIndex: 'uniqueCodeDept',
      customCell: () => {		// 在此处可以修改单元格中的样式
        return { style: { 'text-align': 'left', '-webkit-line-clamp': 2 }} // return 想要设置的样式
      },
      ellipsis: true,
      width: 200,
      slots: { customRender: 'uniqueCodeDept' }
    }, */
    {
      title: '职务',
      dataIndex: 'psnPost',
      width: 100,
      ellipsis: true,
    },
    {
      title: '人员属性',
      dataIndex: 'psnType',
      ellipsis: true,
      width: 120,
      slots: {customRender: 'psnType'}
    },
    {
      title: '手机号',
      dataIndex: 'cellPhoneNum',
      width: 120,
      ellipsis: true,
      slots: {customRender: 'cellPhoneNum'}
    }, {
      title: '邮箱',
      dataIndex: 'psnEmail',
      width: 120,
      ellipsis: true,
      slots: {customRender: 'psnEmail'}
    },
    {
      title: '通讯地址',
      dataIndex: 'psnAddress',
      width: 200,
      ellipsis: true
    },
    {
      title: '证件类型',
      dataIndex: 'documentType',
      width: 150,
      ellipsis: true,
      slots: {customRender: 'documentType'}
    },
    {
      title: '证件号码',
      dataIndex: 'documentCode',
      width: 200,
      ellipsis: true,
      slots: {customRender: 'documentCode'}
    },
    {
      title: '开户银行',
      dataIndex: 'psnBank',
      width: 200,
      ellipsis: true
    },
    {
      title: '开户地',
      dataIndex: 'bankArea',
      width: 200,
      ellipsis: true
    },
    {
      title: '银行账户',
      dataIndex: 'bankAccount',
      width: 200,
      ellipsis: true,
      slots: {customRender: 'bankAccount'}
    },
    {
      title: '银行行号',
      dataIndex: 'bankNum',
      width: 200,
      ellipsis: true
    },
    {
      title: '入职日期',
      dataIndex: 'entryDate',
      width: 120,
      ellipsis: true
    },
    {
      title: '离职日期',
      dataIndex: 'leaveDate',
      width: 120,
      ellipsis: true
    }
  ],
  editData: {
    id: '',
    uniqueCode: '',
    psnCode: '',
    uniqueCodeDept: '',
    psnName: '',
    psnSex: '',
    psnType: '',
    psnPost: '',
    psnPhone: '',
    entryDate: ''
  }
}

function formatPsnSex(sex: String) {
  let str = '男'
  switch (sex) {
    case '0':
      str = '未知的性别'
      break
    case '1':
      str = '男'
      break
    case '2':
      str = '女'
      break
    case '9':
      str = '未说明的性别'
  }
  return str
}

function formatPsnType(type: any) {
  let str = '内部员工'
  switch (type) {
    case '1':
      str = '内部员工'
      break
    case '2':
      str = '外部员工'
      break
  }
  return str
}

const documentTypeList:any = ref([])
async function reloadDocumentType(){
  //证件类型
  documentTypeList.value = await findDocumentTypeAll()
}

function formatDocumentType(documentType) {
  let str = documentType
  documentTypeList.value.forEach(
    function (item: any) {
      if (item.ccode == documentType) {
        str = item.cname
      }
    }
  )
  return str
}

/*const deptList = ref([])
async function reloadDept() {
  const res:any = await useRouteApi(getDeptListById,{schemaName: dynamicTenantId})()
  deptList.value = res.items
}
function formatUniqueCodeDept(uniqueCodeDept:any) {
  let str = uniqueCodeDept
  deptList.value.forEach(
    function (dept:any) {
      if (dept.uniqueCode == uniqueCodeDept){
        str = dept.deptName
      }
    }
  )
  return str
}*/

const thisCheckKey = ref('')

function handleSelect(data) {
  if (null != data.uniqueCode) {
    thisCheckKey.value = data.uniqueCode
    // if (data.deptList.length > 0) deptList.value = data.deptList
    // reloadTable()
    loadMark.value = true
    tableDataAll.value = psnList.value.filter((item) => {
      if (thisCheckKey.value!=null && thisCheckKey.value!='0' && thisCheckKey.value!='undefined') {
        return thisCheckKey.value.indexOf(item.uniquePsnType)!=-1 && item.flag.indexOf(flag.value)!=-1
      }
      return item.flag.indexOf(flag.value)!=-1
    })
    tableData.value = tableDataAll.value
    setPagination({total:tableData.value.length})
    loadMark.value = false
  }
}

// 这是示例组件
const [registerTable, {
  reload,
  setTableData,
  setColumns,
  getColumns,
  getPaginationRef,
  setPagination
}] = useTable({
  /*api: async (params) => {
    // debugger
    // console.log(thisCheckKey.value)
    return await psnFindAll(params)
  },*/
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
    slots: {customRender: 'action'}
  },
  searchInfo: {
    uniquePsnType: thisCheckKey.value,
    flag: flag.value
  }
})

// 部门带参数,给人员，人员翻页，带部门
const [registerAddPage, {openModal: openAddPage}] = useModal()
const [registerEditPage, {openModal: openEditPage}] = useModal()
const [registerBatchEditPage, {openModal: openBatchEditPage}] = useModal()
const [registerExcelPage, {openModal: openExcelPage}] = useModal()
const val: any = {
  id: null,
  uniqueCode: '',
  psnName: '',
  psnSex: '0',
  psnType: '1',
  psnCode: '',
  uniqueCodeDept: '',
  jobNum: '',
  psnPost: '',
  uniquePsnType: '',
  cellPhoneNum: '',
  countryId: '',
  psnEmail: '',
  psnAddress: '',
  province: '',
  city: '',
  district: '',
  psnQq: '',
  psnWechat: '',
  documentType: '',
  documentCode: '',
  psnStation: '',
  entryDate: null,
  psnBank: '',
  bankArea: '',
  bankAccount: '',
  bankNum: '',
  flag: '1'
}

onMounted(async () => {
  await reloadOrigin()
  await reloadDocumentType()
  resetDynamicColumnData()
  await reloadPsn()
})

const openAdd = () => {
  val.isEdit = true
  openEditPage(true, {
    data: val,
    isState: '0'
  })
}
const openEdit = (data: any, flag) => {
  data.isEdit = flag
  openEditPage(true, {
    data: data,
    isState: '2'
  })
}
const del = async (data: any) => {
  await deletePsn(data)
  // alert('删除成功！')
  message.success('删除成功！')
  await reloadTable()
}

async function saveData(data: any) {
  await savePsn(data)
  await reloadTable()
}

async function saveAddData(data: any) {
  await reloadTable()
}

async function editFlagData(data: any) {
  await editFlag(data)
  await reloadTable()
}

async function onSearch() {
  loadMark.value = true
  tableData.value = tableDataAll.value.filter(item => {
    //通过人员编码过滤
    if (formItems.value.selectType == 'psnCode' && formItems.value.selectValue != '') {
      return item.psnCode.indexOf(formItems.value.selectValue) != -1
    }
    //通过人员名称过滤
    if (formItems.value.selectType == 'psnName' && formItems.value.selectValue != '') {
      return item.psnName.indexOf(formItems.value.selectValue) != -1
    }
    //通过性别过滤
    if (formItems.value.selectType == 'psnSex' && formItems.value.selectValue != '') {
      if ('未知的性别'.indexOf(formItems.value.selectValue)!=-1) {
        return item.psnSex == '0' || item.psnSex.indexOf(formItems.value.selectValue) != -1
      }
      if (formItems.value.selectValue == '男') {
        return item.psnSex == '1' || item.psnSex.indexOf(formItems.value.selectValue) != -1
      }
      if (formItems.value.selectValue == '女') {
        return item.psnSex == '2' || item.psnSex.indexOf(formItems.value.selectValue) != -1
      }
      if ('未说明的性别'.indexOf(formItems.value.selectValue)!=-1) {
        return item.psnSex == '9' || item.psnSex.indexOf(formItems.value.selectValue) != -1
      }
      return item.psnSex.indexOf(formItems.value.selectValue) != -1
    }
    //通过证件类型过滤
    if (formItems.value.selectType == 'documentType' && formItems.value.selectValue != '') {
      /*if ('居民身份证'.indexOf(formItems.value.selectValue)!=-1) {
        return item.documentType == '1' || item.documentType.indexOf(formItems.value.selectValue) != -1
      }
      if ('港澳居民来往内地通行证'.indexOf(formItems.value.selectValue)!=-1) {
        return item.documentType == '2' || item.documentType.indexOf(formItems.value.selectValue) != -1
      }
      if ('台湾居民来往大陆通行证'.indexOf(formItems.value.selectValue)!=-1) {
        return item.documentType == '3' || item.documentType.indexOf(formItems.value.selectValue) != -1
      }
      if ('中国护照'.indexOf(formItems.value.selectValue)!=-1) {
        return item.documentType == '4' || item.documentType.indexOf(formItems.value.selectValue) != -1
      }
      if ('外国护照'.indexOf(formItems.value.selectValue)!=-1) {
        return item.documentType == '5' || item.documentType.indexOf(formItems.value.selectValue) != -1
      }
      return item.documentType.indexOf(formItems.value.selectValue) != -1*/
      const b = documentTypeList.value.filter(bb => {
        return bb.cname.indexOf(formItems.value.selectValue) != -1
      })
      if(b.map(bb=>bb.ccode).filter(dd=>dd==item.documentType).length>0){
        return true
      }
      return item.documentType.indexOf(b.map(bb=>bb.ccode)) != -1
    }
    //通过国家过滤
    if (formItems.value.selectType == 'countryId' && formItems.value.selectValue != '') {
      return item.countryId.indexOf(formItems.value.selectValue) != -1
    }
    //通过地址过滤
    if (formItems.value.selectType == 'district' && formItems.value.selectValue != '') {
      return item.district.indexOf(formItems.value.selectValue) != -1
    }
    //通过手机过滤
    if (formItems.value.selectType == 'cellPhoneNum' && formItems.value.selectValue != '') {
      return item.cellPhoneNum.indexOf(formItems.value.selectValue) != -1
    }
    //通过Email过滤
    if (formItems.value.selectType == 'psnEmail' && formItems.value.selectValue != '') {
      return item.psnEmail.indexOf(formItems.value.selectValue) != -1
    }
    //通过钉钉过滤
    if (formItems.value.selectType == 'psnQq' && formItems.value.selectValue != '') {
      return item.psnQq.indexOf(formItems.value.selectValue) != -1
    }
    //通过微信过滤
    if (formItems.value.selectType == 'psnWechat' && formItems.value.selectValue != '') {
      return item.psnWechat.indexOf(formItems.value.selectValue) != -1
    }
    //通过证件号码过滤
    if (formItems.value.selectType == 'documentCode' && formItems.value.selectValue != '') {
      return item.documentCode.indexOf(formItems.value.selectValue) != -1
    }
    return item
  })
  await setPagination({
    total: tableData.value.length
  })
  loadMark.value = false
}

function filterSearch() {
}

function condClick() {
}

/*const thisCheckKey = ref('')
function handleSelect(data){
  if (null != data.uniqueCode){
    thisCheckKey.value = data.uniqueCode
    if ( data.deptList.length > 0) deptList.value = data.deptList
    reloadTable()
  }
}*/
const psnList:any = ref([])
async function reloadPsn(){
  loadMark.value = true
  const res = await getPsnList({originId: originId.value})
  psnList.value = res
  tableDataAll.value = res.filter((item) => {
    if (thisCheckKey.value!=null && thisCheckKey.value!='0' && thisCheckKey.value!='undefined') {
      return thisCheckKey.value.indexOf(item.uniquePsnType)!=-1 && item.flag.indexOf(flag.value)!=-1
    }
    return item.flag.indexOf(flag.value)!=-1
  })
  tableData.value = tableDataAll.value
  setPagination({total:tableData.value.length})
  loadMark.value = false
}
async function reloadTable() {
  await reloadPsn()
  /*reload({
    searchInfo: {
      uniquePsnType: thisCheckKey.value,
      flag: flag.value
    }
  })*/
  checkRow.value = []
  state.selectedRowKeys = []
}

function onChangeSwitch(str) {
  flag.value = str
  reloadTable()
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
    checkRow.value[0].isEdit = true
    openEditPage(true, {
      data: checkRow.value[0],
      isState: '1'
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
  if (checkRow.value.length > 0) {
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      const res = await findByUniqueCode(item.uniqueCode)
      if (res.length > 0){
        const list = res.filter(aa=> aa.ctype=='2' && aa.originId==originId.value)
        if (list.length>0) {
          createWarningModal({
            iconType: 'warning',
            title: '删除',
            content: '人员信息已分配不允许删除！'
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
          const item1 = await findByOriginIdAndUniqueCode(originId.value,item.uniqueCode)
          if (item1.length>0) {
            item1[0].flag = '0'
            item1[0].acceptUser = ''
            item1[0].acceptDate = ''
            await saveAccount(item1[0])
          }
          await deletePsn(item)
        }
        checkRow.value = []
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

//文件导入
const openExcel = () => {
  openExcelPage(true, {})
}
const loadMark = ref(false)
async function saveExcel(data) {
  loadMark.value = true
  await excelPsn(data)
  await reloadTable()
  loadMark.value = false
}

//批量修改数据
const editOpenBatch = () => {
  if (checkRow.value.length > 0) {
    checkRow.value[0].isEdit = true
    openBatchEditPage(true, {
      data: {
        psnType: '',
        psnSex: '',
        uniquePsnType: '',
        uniqueCodeDept: '',
        province: '',
        psnAddress: '',
        countryId: '',
        documentType: '',
        entryDate: null,
        leaveDate: null,
        psnBank: '',
        bankArea: '',
        bankNum: ''
      },
      originId: originId
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '编辑',
      content: '请选择需要编辑的内容！'
    })
  }
}

async function saveBatchData(data) {
  loadMark.value = true
  checkRow.value.forEach(item => {
    if (data.psnType != null && data.psnType != '') {
      item.psnType = data.psnType
    }
    if (data.psnSex != null && data.psnSex != '') {
      item.psnSex = data.psnSex
    }
    if (data.uniquePsnType != null && data.uniquePsnType != '') {
      item.uniquePsnType = data.uniquePsnType
    }
    if (data.province != null && data.province != '') {
      item.province = data.province
    }
    if (data.psnAddress != null && data.psnAddress != '') {
      item.psnAddress = data.psnAddress
    }
    if (data.countryId != null && data.countryId != '') {
      item.countryId = data.countryId
    }
    if (data.documentType != null && data.documentType != '') {
      item.documentType = data.documentType
    }
    if (data.entryDate != null && data.entryDate != '') {
      item.entryDate = data.entryDate
    }
    if (data.leaveDate != null && data.leaveDate != '') {
      item.leaveDate = data.leaveDate
    }
    if (data.psnBank != null && data.psnBank != '') {
      item.psnBank = data.psnBank
    }
    if (data.bankArea != null && data.bankArea != '') {
      item.bankArea = data.bankArea
    }
    if (data.bankNum != null && data.bankNum != '') {
      item.bankNum = data.bankNum
    }
    return item
  })
  await excelPsn(checkRow.value)
  loadMark.value = false
  message.success('批量修改成功!')
  await reloadTable()
}

/**********************栏目设置**********************/
/*start栏目设置*/
const pageParameter = reactive({
  showRulesSize: 'MIN',
  total: 0,
  thisAdInfo: {},
  originId: ''
})
const visible = ref(false);
const windowWidth = (document.documentElement.clientWidth - (70 + 280))
const windowHeight = (window.innerHeight - (300))
const totalColumnWidth = ref(0)
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData: any = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});
const tableRef: any = ref(null)
const lanMuData = {
  'accId': '',
  'menuName': '集团人员',
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
    let arr: any = key.split('-');
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
    let arr: any = key.split('-');
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
/*栏目设置end*/

/* 部分隐藏处理
** str 需要处理的字符串
** frontLen  保留的前几位
** endLen  保留的后几位
** cha  替换的字符串
*/
function plusXing(str, frontLen, endLen,cha) {
  let len = str.length - frontLen - endLen;
  let xing = '';
  for (let i = 0; i < len; i++) {
    xing += cha;
  }
  return str.substring(0, frontLen) + xing + str.substring(str.length - endLen);
}
function plusStr(str, frontLen, endLen,cha) {
  return str.substring(0, frontLen) + cha + str.substring(str.length - endLen);
}

//引入
import Into from './popup/into.vue'
import Assign from './popup/assign.vue'
const [registerIntoPage, {openModal: openIntoPage}] = useModal()
const openInto = () => {
  openIntoPage(true, {
    originId: originId.value
  })
}

async function saveInto(data) {
  await reloadPsn()
}

//分配
const [registerAssignPage, {openModal: openAssignPage}] = useModal()
const openAssign = () => {
  openAssignPage(true, {
    originId: originId.value
  })
}

async function saveAssign(data) {
}

//新增申请
import ApplyList from './popup/apply-list.vue'
import {findByUserIdAndOriginId} from "/@/api/record/system/group-permission";
const [registerApplyListPage, {openModal: openApplyListPage}] = useModal()
const openApplyList = () => {
  openApplyListPage(true, {
    originId: originId.value
  })
}

async function saveApplyList(data) {
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
