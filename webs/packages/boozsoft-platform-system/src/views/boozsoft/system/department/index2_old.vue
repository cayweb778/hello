<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img">-->
<!--        <div class="container-head-title" style="margin-left: 20px;">
          <b class="noneSpan"><TeamOutlined style="font-size: 26px;" />&nbsp;&nbsp;人员信息</b>
        </div>-->
        <div class="container-head-title" style="float: left;margin-top: 10px;">
          <b class="noneSpan" style="font-size: 60px;"><ProfileOutlined /></b>
        </div>
        <div class="container-head-title" style="padding-left: 35%;text-align: center;margin-top: 30px;">
          <b class="noneSpan" style="font-size: 36px;">人员信息</b>
        </div>
        <div class="ant-btn-group" style="float: right;">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            v-if="defaultPage"
            @click="openAddPage()"
          ><span>新增</span></button>
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
            @click="editOpen()"
          ><span>修改</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="editOpenBatch()"
          ><span>批改</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="delList()"
          ><span>删除</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            v-if="!defaultPage"
            @click="openInto()"
          ><span>引入</span></button>
          <button
            type="button"
            v-if="defaultPage"
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
          ><span>打印</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="closeCurrent()"
          ><span>退出</span></button>
<!--          <button
            type="button"
            class="ant-btn ant-btn-me"
            @click="openSelectPsn()"
          ><span>测试弹框</span></button>-->
        </div>
      </div>
      <div style="clear: none"/>
      <div style="margin-top: -30px;margin-left: 80px;">
        <div style="display: inline-block;float: left;margin-top: -25px;">
          <AccountPicker theme="three" @reloadTable="dynamicAdReload"/>
        </div>
        <div style="display: inline-block;position:absolute;top:85px;left:110px;font-size: 10px;color:#999999;">共 {{psnList.length}} 条记录</div>
        <div style="float: right; margin-left: 10px;">
          <a-button @click="reloadTable()">
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
                  <div style="width:665px">
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
                  </div>
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
                <SafetyOutlined/>&nbsp;&emsp;启用&emsp;&ensp;<CheckOutlined
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
          <a-popover class="ant-btn-default" placement="bottom">
            <template #content>
              <span class="group-btn-span-special2" @click="onChangeSwitch1('1')"
                    :style="typeFlag=='1'?{backgroundColor: '#0096c7',color: 'white'}:''" style="width: 160px;">
                按部门分类&emsp;&emsp;&emsp;<CheckOutlined
                v-if="typeFlag=='1'"/></span><br/>
              <span class="group-btn-span-special2" @click="onChangeSwitch1('2')"
                    :style="typeFlag=='2'?{backgroundColor: '#0096c7',color: 'white'}:''" style="width: 160px;">
                按人员类别分类&emsp;<CheckOutlined
                v-if="typeFlag=='2'"/></span>
            </template>
            <a-button>
              <InsertRowLeftOutlined :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
          <a-button title="回收站" v-if="defaultPage" @click="openPsnDel()">
            <DeleteOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
<!--                    <a-button @click="()=>{
                      if (!visible){ visible = true;reloadColumns()}
                      return false
                    }">
                      <FilterFilled :style="{ fontSize: '14px' }" />
                    </a-button>-->
        </div>
        <div style="float: right; position: relative">
          <!-- 搜索 -->
          <!--          <label style="font-size: 14px">检索条件：</label>-->
          <a-select v-model:value="formItems.selectType" style="width: 120px;font-size: 12px;"
                    class="special_select">
            <a-select-option style="font-size: 12px;" value="psnCode">人员编码</a-select-option>
            <a-select-option style="font-size: 12px;" value="psnName">人员姓名</a-select-option>
<!--            <a-select-option style="font-size: 12px;" value="uniqueCodeDept">部门名称</a-select-option>
            <a-select-option style="font-size: 12px;" value="uniquePsnType">人员类别</a-select-option>-->
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
      <div style="clear:both"/>
    </div>
    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <div class="w-1/4 xl:w-1/5" style="width: 250px;min-height: 250px;border: 1px #cccccc solid;margin-right: .2%;margin-bottom: 60px;">
          <DeptTree v-if="typeFlag=='1'" @select="handleSelect" v-model="pageParameter"/>
          <TypeTree v-if="typeFlag=='2'" @select="handleSelect" v-model="pageParameter"/>
        </div>
        <div style="width: calc(100% - 250px); float: right;margin-left: 5px;">
<!--          :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"-->
          <BasicTable
            ref="tableRef"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys,getCheckboxProps:rowSelection.getCheckboxProps, onChange: onSelectChange }"
            @selection-change="selectionChange"
            :scroll="{ x: totalColumnWidth,y: windowHeight }"
            :dataSource="tableData"
            @row-click="rowClick"
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
            <!--        <template #flag="{ record }"> {{ record.flag === '1' ? '启用' : '停用' }} </template>-->
            <template #flag="{ record }">
            <span v-if="record.flag!=null && record.flag!=''">
              <a-tag :color="record.flag == '1' ? 'green' : 'volcano'">
                {{ record.flag == '1' ? '启用' : '停用' }}
              </a-tag>
            </span>
            </template>
            <template #uniqueCodeDept="{ record }"> {{
                formatUniqueCodeDept(record.uniqueCodeDept)
              }}
            </template>
            <template #uniquePsnType="{ record }"> {{ formatUniquePsnType(record.uniquePsnType) }}</template>
            <template #documentType="{ record }">
<!--              <span v-if="record.documentType=='1'">居民身份证</span>
              <span v-if="record.documentType=='2'">港澳居民来往内地通行证</span>
              <span v-if="record.documentType=='3'">台湾居民来往大陆通行证</span>
              <span v-if="record.documentType=='4'">中国护照</span>
              <span v-if="record.documentType=='5'">外国护照</span>-->
              {{ formatDocumentType(record.documentType) }}
            </template>
            <template #cellPhoneNum="{ record }">
              <span v-if="record.cellPhoneNum!=null&&record.cellPhoneNum!=''">{{plusStr(record.cellPhoneNum,3,4,'****')}}</span>
            </template>
            <template #psnWechat="{ record }">
              <span v-if="record.psnWechat!=null&&record.psnWechat!=''">{{plusStr(record.psnWechat,3,4,'****')}}</span>
            </template>
            <template #psnQq="{ record }">
              <span v-if="record.psnQq!=null&&record.psnQq!=''">{{plusStr(record.psnQq,3,4,'****')}}</span>
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

              <div v-if="record.flag!=null && record.flag!=''">
                <a-popover placement="bottom">
                  <a-button style="padding: 0px 4px; height: 20px;">
                    <CaretDownFilled/>
                  </a-button>
                  <template #content>
                    <!--                <p v-if="record.flag=='1'" style="cursor: pointer" @click="openEdit(record,true)"><FormOutlined /> 编辑</p>-->
                    <p v-if="record.flag=='0'" class="p_specifics" style="cursor: pointer" @click="editFlagData(record)">
                      <CheckCircleOutlined/>
                      启用
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
        <Edit
          @save="saveData"
          @register="registerEditPage"
        />
        <BatchEdit
          @save="saveBatchData"
          @register="registerBatchEditPage"
        />
        <Excel @save="saveExcel" @register="registerExcelPage"/>

        <SelectPsn @save="saveSelectPsn" @register="registerSelectPsnPage"/>

        <PsnDel @save="savePsnDel" @register="registerPsnDelPage"/>

        <Into @save="saveInto" @register="registerIntoPage"/>

        <ApplyList @save="saveApplyList" @register="registerApplyListPage"/>
      </PageWrapper>
    </div>
  </div>
</template>
<script setup lang="ts">
import {BasicTable, useTable} from '/@/components/Table'
import {PageWrapper} from '/@/components/Page'
import {useRouteApi} from '/@/utils/boozsoft/datasource/datasourceUtil';
import DeptTree from './DeptTree.vue'
import TypeTree from './TypeTree.vue'
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
  PieChartFilled,ReadOutlined,SafetyOutlined,RestOutlined,
  FilterFilled, CheckOutlined, SortDescendingOutlined, SortAscendingOutlined, EditOutlined,
  TeamOutlined,ProfileOutlined,InsertRowLeftOutlined
} from '@ant-design/icons-vue'
import {psnFindAll} from '/@/api/psn/psn'
import {onMounted, reactive, ref} from 'vue'
import {
  deletePsn,
  savePsn,
  editFlag,
  excelPsn,
  getPsnList,
  findAllByCpersonId
} from '/@/api/record/system/psn'
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
  getCurrentAccountName, getThisIndexImg, hasBlank,
} from "/@/api/task-api/tast-bus-api";
import {
  findDbLanMuList,
  saveLanMuList,
} from '/@/api/record/system/accvoucher'
import {cloneDeep} from "lodash-es";
import {initDynamics, assemblyDynamicColumn} from "./data";
import {getDeptList} from "/@/api/record/system/dept";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  getThisAdInfoData,
} from "/@/api/record/system/financial-settings";
import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-DATA.vue";
import {findDataBase} from "/@/api/record/system/account";
import {usePlatformsStore} from "/@/store/modules/platforms";
import {psnTypeFindAll} from "/@/api/psn-type/psn-type";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";

const {closeCurrent} = useTabs(router);

const formItems = ref({
  selectType: 'psnCode',
  selectValue: ''
})

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
      ellipsis: true,
      fixed: 'left',
      slots: {customRender: 'flag'}
    },
    {
      title: '编码',
      dataIndex: 'psnCode',
      ellipsis: true,
      fixed: 'left',
      slots: {customRender: 'psnCode'}
    },
    {
      title: '姓名',
      dataIndex: 'psnName',
      ellipsis: true,
      fixed: 'left',
      slots: {customRender: 'psnName'}
    },
    {
      title: '工号',
      dataIndex: 'jobNum',
      ellipsis: true
    },
    {
      title: '性别',
      dataIndex: 'psnSex',
      ellipsis: true,
      slots: {customRender: 'psnSex'}
    },
    {
      title: '部门名称',
      dataIndex: 'uniqueCodeDept',
      ellipsis: true,
      slots: {customRender: 'uniqueCodeDept'}
    },
    {
      title: '人员类别',
      dataIndex: 'uniquePsnType',
      ellipsis: true,
      slots: {customRender: 'uniquePsnType'}
    },
    {
      title: '职务',
      dataIndex: 'psnPost',
      ellipsis: true,
    },
    {
      title: '手机号',
      dataIndex: 'cellPhoneNum',
      ellipsis: true,
      slots: {customRender: 'cellPhoneNum'}
    },
    {
      title: '微信',
      dataIndex: 'psnWechat',
      ellipsis: true,
      slots: {customRender: 'psnWechat'}
    },
    {
      title: 'Email',
      dataIndex: 'psnEmail',
      ellipsis: true,
      slots: {customRender: 'psnEmail'}
    },
    {
      title: '钉钉',
      dataIndex: 'psnQq',
      ellipsis: true,
      slots: {customRender: 'psnQq'}
    },
    {
      title: '证件类型',
      dataIndex: 'documentType',
      ellipsis: true,
      slots: {customRender: 'documentType'}
    },
    {
      title: '证件号码',
      dataIndex: 'documentCode',
      ellipsis: true,
      slots: {customRender: 'documentCode'}
    },
    {
      title: '出生日期',
      dataIndex: 'birthDate',
      ellipsis: true,
    },
    {
      title: '行政区划',
      dataIndex: 'province',
      ellipsis: true,
    },
    {
      title: '开户银行',
      dataIndex: 'psnBank',
      ellipsis: true
    },
    {
      title: '开户地',
      dataIndex: 'bankArea',
      ellipsis: true
    },
    {
      title: '银行账号',
      dataIndex: 'bankAccount',
      ellipsis: true,
      slots: {customRender: 'bankAccount'}
    },
    {
      title: '银行行号',
      dataIndex: 'bankNum',
      ellipsis: true
    },
    {
      title: '管理类型',
      dataIndex: 'psnType',
      ellipsis: true
    },
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
  let str = ''
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
  let str = ''
  switch (type) {
    case '1':
      str = '内部人员'
      break
    case '2':
      str = '外部人员'
      break
  }
  return str
}

const deptList = ref([])

async function reloadDept() {
  const res: any = await useRouteApi(getDeptList, {schemaName: dynamicTenantId})({})
  deptList.value = res.items
}

function formatUniqueCodeDept(uniqueCodeDept: any) {
  let str = uniqueCodeDept
  deptList.value.forEach(
    function (dept: any) {
      if (dept.uniqueCode == uniqueCodeDept) {
        str = dept.deptName
      }
    }
  )
  return str
}

const psnTypeList = ref([])

async function reloadPsnType() {
  const res: any = await useRouteApi(psnTypeFindAll, {schemaName: dynamicTenantId})({})
  psnTypeList.value = res.items
}

function formatUniquePsnType(uniquePsnType) {
  let str = uniquePsnType
  psnTypeList.value.forEach(
    function (item: any) {
      if (item.uniqueCode == uniquePsnType) {
        str = item.psnTypeName
      }
    }
  )
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

const thisCheckKey = ref('')

function handleSelect(data) {
  if (null != data.uniqueCode) {
    thisCheckKey.value = data.uniqueCode
    // if (data.deptList.length > 0) deptList.value = data.deptList
    // reloadTable()
    loadMark.value = true
    if (typeFlag.value=='1') {
      tableDataAll.value = psnList.value.filter((item) => {
        if (thisCheckKey.value!=null && thisCheckKey.value!='0' && thisCheckKey.value!='undefined') {
          return thisCheckKey.value.indexOf(item.uniqueCodeDept)!=-1 && item.flag.indexOf(flag.value)!=-1
        }
        return item.flag.indexOf(flag.value)!=-1
      })
    } else {
      tableDataAll.value = psnList.value.filter((item) => {
        if (thisCheckKey.value!=null && thisCheckKey.value!='0' && thisCheckKey.value!='undefined') {
          return thisCheckKey.value.indexOf(item.uniquePsnType)!=-1 && item.flag.indexOf(flag.value)!=-1
        }
        return item.flag.indexOf(flag.value)!=-1
      })
    }
    tableData.value = tableDataAll.value
    let num = 550-(tableData.value.length%50)
    for (let i=0;i<num;i++){
      tableData.value.push({})
    }
    setPagination({total:tableData.value.length})
    loadMark.value = false
  }
}

// 这是示例组件
const dynamicTenantId = ref(getCurrentAccountName(true))
const [registerTable, {
  reload,
  setTableData,
  setColumns,
  getColumns,
  getPaginationRef,
  setPagination,
  deleteSelectRowByKey
}] = useTable({
  /*api: async (params) => {
    // debugger
    // console.log(thisCheckKey.value)
    params.flag = flag.value
    if (typeFlag.value =='1'){
      params.uniqueCodeDept = thisCheckKey.value
      params.uniquePsnType = null
    } else {
      params.uniqueCodeDept = null
      params.uniquePsnType = thisCheckKey.value
    }
    return await useRouteApi(psnFindAll, {schemaName: dynamicTenantId})(params)
  },*/
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: true,
  /*pagination: {
    pageSize: 50,
    showSizeChanger: true,
    pageSizeOptions: ['10', '25', '50', '100'],
    showTotal: t => `总共${t}条数据`
  },*/
  pagination: {
    pageSize: 200,
    showSizeChanger: false,
    // simple:true,
    showTotal: t => `共${t}条记录    每页200条`
  },
  actionColumn: {
    width: 100,
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  },
  searchInfo: {
    uniqueCodeDept: thisCheckKey.value,
    flag: flag.value
  }
})

// 部门带参数,给人员，人员翻页，带部门

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
const defaultPage = ref(false)
onMounted(async () => {
  resetDynamicColumnData()
  await reloadDept()
  await reloadPsnType()
  await reloadDocumentType()
  // 进页面执行
  /*getThisAdInfoData({'accId': getCurrentAccountName(false)}).then(res => {
    if (null != res && res.independent == 0) {
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn == '' ? res.accName : res.accNameCn
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
    resetDynamicColumnData()
  })*/
})

const openAddPage = () => {
  val.isEdit = true
  openEditPage(true, {
    data: val,
    dynamicTenantId: dynamicTenantId.value,
    isState: '0'
  })
}
const openEdit = (data: any, flag) => {
  data.isEdit = flag
  openEditPage(true, {
    data: data,
    dynamicTenantId: dynamicTenantId.value,
    isState: '2'
  })
}
const del = async (data: any) => {
  createConfirm({
    iconType: 'error',
    title: '警告',
    content: '删除后数据将不能恢复，你确认要删除吗?',
    onOk: async () => {
      await useRouteApi(deletePsn, {schemaName: dynamicTenantId})(data)
      // alert('删除成功！')
      message.success('删除成功！')
      await reloadTable()
      checkRow.value = []
    },
    onCancel: () => {
      return false
    }
  })

}

async function saveData(data: any) {
  await useRouteApi(savePsn, {schemaName: dynamicTenantId})(data)
  await reloadTable()
}

async function editFlagData(data: any) {
  await useRouteApi(editFlag, {schemaName: dynamicTenantId})(data)
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
  let num = 50-(tableData.value.length%50)
  for (let i=0;i<num;i++){
    tableData.value.push({})
  }
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
  const res = await useRouteApi(getPsnList,{schemaName: dynamicTenantId})({})
  psnList.value = res
  if (typeFlag.value=='1') {
    tableDataAll.value = res.filter((item) => {
      if (thisCheckKey.value!=null && thisCheckKey.value!='0' && thisCheckKey.value!='undefined') {
        return thisCheckKey.value.indexOf(item.uniqueCodeDept)!=-1 && item.flag.indexOf(flag.value)!=-1
      }
      return item.flag.indexOf(flag.value)!=-1
    })
  } else {
    tableDataAll.value = res.filter((item) => {
      if (thisCheckKey.value!=null && thisCheckKey.value!='0' && thisCheckKey.value!='undefined') {
        return thisCheckKey.value.indexOf(item.uniquePsnType)!=-1 && item.flag.indexOf(flag.value)!=-1
      }
      return item.flag.indexOf(flag.value)!=-1
    })
  }
  tableData.value = tableDataAll.value
  let num = 50-(tableData.value.length%50)
  for (let i=0;i<num;i++){
    tableData.value.push({})
  }
  setPagination({total:tableData.value.length})
  loadMark.value = false
}
async function reloadTable() {
  await reloadPsn()
  /*if (typeFlag.value=='1') {
    reload({
      searchInfo: {
        uniqueCodeDept: thisCheckKey.value,
        flag: flag.value
      }
    })
  } else {
    reload({
      searchInfo: {
        uniquePsnType: thisCheckKey.value,
        flag: flag.value
      }
    })
  }*/
  checkRow.value = []
  state.selectedRowKeys = []
}

function onChangeSwitch(str) {
  flag.value = str
  reloadPsn()
}

const typeFlag = ref('1')
function onChangeSwitch1(str) {
  typeFlag.value = str
  loadMark.value = true
  if (typeFlag.value=='1') {
    tableDataAll.value = psnList.value.filter((item) => {
      if (thisCheckKey.value!=null && thisCheckKey.value!='0' && thisCheckKey.value!='undefined') {
        return thisCheckKey.value.indexOf(item.uniqueCodeDept)!=-1 && item.flag.indexOf(flag.value)!=-1
      }
      return item.flag.indexOf(flag.value)!=-1
    })
  } else {
    tableDataAll.value = psnList.value.filter((item) => {
      if (thisCheckKey.value!=null && thisCheckKey.value!='0' && thisCheckKey.value!='undefined') {
        return thisCheckKey.value.indexOf(item.uniquePsnType)!=-1 && item.flag.indexOf(flag.value)!=-1
      }
      return item.flag.indexOf(flag.value)!=-1
    })
  }
  tableData.value = tableDataAll.value
  setPagination({total:tableData.value.length})
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

const editOpen = () => {
  if (checkRow.value.length == 1) {
    checkRow.value[0].isEdit = true
    openEditPage(true, {
      data: checkRow.value[0],
      dynamicTenantId: dynamicTenantId.value,
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
        bankNum: '',
        flag: ''
      },
      dynamicTenantId: dynamicTenantId.value
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
    if (data.uniqueCodeDept != null && data.uniqueCodeDept != '') {
      item.uniqueCodeDept = data.uniqueCodeDept
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
    if (data.flag != null && data.flag != '') {
      item.flag = data.flag
    }
    return item
  })
  await useRouteApi(excelPsn, {schemaName: dynamicTenantId})(checkRow.value)
  message.success('批量修改成功!')
  await reloadTable()
}

async function delList() {
  if (checkRow.value.length > 0) {
    for (let i = 0; i < checkRow.value.length; i++) {
      const item = checkRow.value[i]
      const res = await useRouteApi(findAllByCpersonId,{schemaName: dynamicTenantId})(item.uniqueCode)
      if(res.length > 0){
        createWarningModal({
          iconType: 'warning',
          title: '提示',
          content: '<span style="font-weight: bold;color: red;">['+item.psnName+']</span> 已经在会计凭证表中使用，不能删除，可对该人员进行停用处理！'
        })
        return false
      }
    }
    //独立账套删除（该删除标志）
    if (defaultPage.value) {
      let dateTime = new_Date()
      createConfirm({
        iconType: 'error',
        title: '警告',
        content: '你确认要删除吗?',
        onOk: async () => {
          for (let i = 0; i < checkRow.value.length; i++) {
            const item = checkRow.value[i]
            item.isDel = "1"
            item.delName = useUserStore().getUserInfo['realName']
            item.delDate = dateTime
            await useRouteApi(savePsn, {schemaName: dynamicTenantId})(item)
            // await useRouteApi(deletePsn, {schemaName: dynamicTenantId})(item)
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
      //集团账套删除（删除档案、并修改分配表状态）
      createConfirm({
        iconType: 'error',
        title: '警告',
        content: '删除后数据将不能恢复，你确认要删除吗?',
        onOk: async () => {
          for (let i = 0; i < checkRow.value.length; i++) {
            const item = checkRow.value[i]
            const item1 = await findByTenantIdAndUniqueCode(dynamicTenantId.value,item.uniqueCode)
            if (item1.length>0) {
              item1[0].flag = '0'
              item1[0].acceptUser = ''
              item1[0].acceptDate = ''
              await saveAccount(item1[0])
            }
            await useRouteApi(deletePsn, {schemaName: dynamicTenantId})(item)
          }
          checkRow.value = []
          message.success('删除成功！')
          await reloadTable()
        },
        onCancel: () => {
          return false
        }
      })
    }
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '删除',
      content: '请选择需要删除的内容！'
    })
  }
}

//打开回收站
/*function openPsnDel(){
  router.push({
    path: '/one/home/psn-del',
  });
}*/

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

//文件导入
const openExcel = () => {
  openExcelPage(true, {
    dynamicTenantId: dynamicTenantId.value
  })
}

//文件导出
function exportExcel(){
  const lanmu:any = getColumns().filter(item=>item.ifShow==true)
  // console.log(lanmu)
  const list = tableDataAll.value.filter(item=>item.id!=null)
  const arrData = list.map(item => {
    item.flag = item.flag=='1'?'启用':'停用'
    item.psnSex=formatPsnSex(item.psnSex)
    item.uniqueCodeDept=formatUniqueCodeDept(item.uniqueCodeDept)
    item.uniquePsnType=formatUniquePsnType(item.uniquePsnType)
    item.psnType=item.psnType=='1'?'内部人员':'外部人员'
    item.documentCode = item.documentCode==null?'':item.documentCode==''?'':item.documentCode.length>10?plusStr(item.documentCode,3,4,'******'):plusStr(item.documentCode,3,0,'******')
    item.cellPhoneNum = item.cellPhoneNum==null?'':item.cellPhoneNum==''?'':item.cellPhoneNum.length>10?plusStr(item.cellPhoneNum,3,4,'****'):plusStr(item.cellPhoneNum,3,0,'******')
    item.psnEmail = item.psnEmail==null?'':item.psnEmail==''?'':item.psnEmail.length>10?plusStr(item.psnEmail,3,4,'****@***'):plusStr(item.psnEmail,3,0,'****@***')
    item.bankAccount = item.bankAccount==null?'':item.bankAccount==''?'':item.bankAccount.length>10?plusStr(item.bankAccount,3,4,'******'):plusStr(item.bankAccount,3,0,'******')
    item.psnWechat = item.psnWechat==null?'':item.psnWechat==''?'':item.psnWechat.length>10?plusStr(item.psnWechat,3,4,'******'):plusStr(item.psnWechat,3,0,'******')
    item.psnQq = item.psnQq==null?'':item.psnQq==''?'':item.psnQq.length>10?plusStr(item.psnQq,3,4,'******'):plusStr(item.psnQq,3,0,'******')
    return item
  }).map(item=>lanmu.map(column=>item[column.dataIndex]))
  aoaToSheetXlsx({
    data: arrData,
    header: lanmu.map(item=>item.title),
    filename: '人员信息_'+pageParameter.companyName+'.xlsx',
  });
}

async function saveExcel(data) {
  loadMark.value = true
  await useRouteApi(excelPsn, {schemaName: dynamicTenantId})(data)
  message.success('导入成功!')
  await reloadTable()
  loadMark.value = false
}

/**********************栏目设置**********************/
/*start栏目设置*/
const mark = usePlatformsStore().getCurrentPlatformId
const pageParameter = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '湖北万亚软件技术有限公司',
  ifUnit: false,
  total: 0,
  thisAdInfo: {}
})
const visible = ref(false);
const windowWidth = (document.documentElement.clientWidth - (70 + 280))
const windowHeight = (window.innerHeight - (310))
const totalColumnWidth = ref(0)
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData: any = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});
const tableRef: any = ref(null)
const lanMuData = {
  'accId': '',
  'menuName': '部门人员',
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

const pageReload = () => {
  setTableData([]) // 清空可能残留的数据
  reload({
    searchInfo: pageParameter
  })
}
/*栏目设置end*/
const loadMark = ref(false)
const originId = ref('')
const dynamicAdReload = async (obj) => {
  // const dataBase: any = await findDataBase(obj.accId, obj.year)
  dynamicTenantId.value = obj.accountMode
  pageParameter.companyName = obj.baseName
  getThisAdInfoData({'accId': obj.accId}).then(item => {
    originId.value = item.accGroup
    if (item.independent == 0) {
      defaultPage.value = false
    } else {
      defaultPage.value = true
    }
  })
  await reloadDept()
  await reloadPsnType()
  await reloadPsn()
  // 先获取部门组件查看是否存在部门信息
  let data: any = {}
  //data.uniqueCodeDept = thisCheckKey.value
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  data.flag = flag.value
  // let res = await useRouteApi(psnFindAll, {schemaName: dynamicTenantId})(data)
  let res = tableData.value
  // if (res != null && res.total> 0){
  loadMark.value = true
  setTableData([]) // 清空可能残留的数据
  setTableData(res)
  // 底部分页信息
  // dynamicTenantId.value = obj.accId+'-'+obj.year
  pageParameter.thisAdInfo = {}
  pageParameter.thisAdInfo = obj
  pageParameter.total = res.total
  setPagination({total: res.total})
  /*}else {
    createWarningModal({title: '温馨提示',content: '暂无任何数据！'});
    pageParameter.thisAdInfo = {}
    pageParameter.total = -1
  }*/
  loadMark.value = false
}

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

import SelectPsn from './popup/select-psn.vue'
const [registerSelectPsnPage, { openModal: openSelectPsnPage }] = useModal()
const openSelectPsn = () => {
  openSelectPsnPage(true, {
    dynamicTenantId: dynamicTenantId.value
  })
}
const psn:any = ref()
function saveSelectPsn(data){
  psn.value = data
}

import PsnDel from './popup/psn-del.vue'
import {findDocumentTypeAll} from "/@/api/record/system/group-document-type";
import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
const [registerPsnDelPage, { openModal: openPsnDelPage }] = useModal()
const openPsnDel = () => {
  openPsnDelPage(true, {
    dynamicTenantId: dynamicTenantId.value
  })
}
async function savePsnDel(data){
  await reloadPsn()
}

//引入
import Into from './popup/into.vue'
const [registerIntoPage, {openModal: openIntoPage}] = useModal()
const openInto = () => {
  openIntoPage(true, {
    dynamicTenantId: dynamicTenantId.value
  })
}

async function saveInto(data) {
  await reloadPsn()
}

//新增申请
import ApplyList from './popup/apply-list.vue'
import {findByTenantIdAndUniqueCode,save as saveAccount} from "/@/api/record/system/group-psn-account";
const [registerApplyListPage, {openModal: openApplyListPage}] = useModal()
const openApplyList = () => {
  openApplyListPage(true, {
    originId: originId.value,
    dynamicTenantId: dynamicTenantId.value
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
  //margin-bottom: 20px;
}

:deep(.ant-input),:deep(.ant-select),:deep(.ant-btn){
  border: 1px solid #c9c9c9;
}

</style>
