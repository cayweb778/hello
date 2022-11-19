<template>
  <div>
    <div class="app-container">
      <ProfileOutlined style="color: #0096c7;font-size: 60px;margin-top: 10px;"/>&emsp;
      <div style="width: 33%;margin-top: 9px;">
        <AccountPicker theme="three" readonly="" @reloadTable="dynamicAdReload" style="margin-top: 20px;"/>
      </div>
      <div style="width: 29.5%;text-align: center;margin-top: 9px;">
        <span style="font-size: 24px;font-weight: bold;color:rgb(0 150 199)">现金流量录入</span><br>
        <span style="color:#666666;font-weight: bold;">期间：</span> <span style="color: #0f0f0f;font-weight: bold;">{{ pageParameter.thisInterval }}</span>

      </div>

      <div style="width: 33%;text-align: right;margin-left: 8px;" >
        <a-button
          class="actod-btn"
          ant-click-animating-without-extra-node="false"
          @click="openQueryPage()"
        ><span>查询</span></a-button>

        <a-button class="actod-btn" style="border-right: 1px solid #cccccc;" @click="closeCurrent(),router.push('/zhongZhang/home/welcome')"><span>退出</span></a-button>
        <p/>

        <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 150px;text-align:left;font-size: 12px;" class="special_select">
          <template v-for="item in searchConditonList.slice(1)">
            <a-select-option v-if="item.ifShow == true" :value="item.dataIndex">
              {{item.title}}
            </a-select-option>
          </template>
        </a-select>
        <a-input-search
          placeholder=""
          v-model:value="pageParameter.searchConditon.value"
          @search="pageSearch"
          style="width: 150px;border-radius: 4px;border: 1px #cccccc solid;"
        />

        <a-button class="ant-btn-default" @click="closeFilterV(),pageReload()" style="margin-left: 20px;">
          <SyncOutlined :style="{ fontSize: '14px' }" />
        </a-button>
        <a-popover class="ant-btn-default" placement="bottom">
          <template #content>
            <a-popconfirm
              ok-text="保存"
              cancel-text="关闭"
              @confirm="confirm"
              @cancel="cancel"
            >
              <template #icon><b>栏目设置</b><br></template>
              <template #title>
                <a-table bordered :data-source="dynamicColumnData" :columns="dynamicColumns"
                         childrenColumnName="children" :pagination="false" style="max-height: 500px;overflow-y: auto" class="lanmu-table">
                  <template #checkBox="{ text, record }">
                    <a-checkbox v-model:checked="record.check" :disabled="record.isFixed"/>
                  </template>
                  <template #widthInput="{ text, record }">
                    <div class="editable-cell">
                      <div v-if="editableData[record.key]" class="editable-cell-input-wrapper">
                        <a-input type="number" v-model:value="editableData[record.key].width"
                                 @pressEnter="save(record.key,record.min,record.max)" style="width: 80px"/>
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
                                   :disabled="record.align=='' || record.name=='本币金额' || record.name=='原币金额'
                                        || record.name=='借方' || record.name=='贷方' || record.name=='余额'">
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

        <a-popover class="ant-btn-default" placement="bottom">
          <template #content>
            <span class="group-btn-span-special">导出当前</span><br/>
            <span class="group-btn-span-special">条件导出</span>
          </template>
          <button
            type="button"
            class="ant-btn"
            ant-click-animating-without-extra-node="false"
          >
            <UsbOutlined/>
          </button>
        </a-popover>
        <a-popover class="ant-btn-default" placement="bottom">
          <template #content>
            <span class="group-btn-span-special">打印当前</span><br/>
            <span class="group-btn-span-special">条件打印</span>
          </template>
          <button
            type="button"
            class="ant-btn"
            ant-click-animating-without-extra-node="false"
          >
            <PrinterOutlined/>
          </button>

        </a-popover>
      </div>

    </div>

    <div class="app-container">
      <div class="app-container-bottom" >
        <BasicTable
          ref="tableRef"
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
          :row-selection="{ type: 'checkbox',selectedRowKeys: tableSelectedRowKeys, onChange: onSelectChange }"
          :scroll="{ x: totalColumnWidth,y: 250 }"
          :rowKey='record=>record.uniqueCode'
          size="small"
          @register="registerTable"
          @change="pageChange"
          @fetch-success="fetchSuccess"
          @row-click="loadFpList"
        >
          <template #serialNumber="{record,index }">
            <span slot="serialNumber" slot-scope="text,record,index">
             {{record.cdfine30}}
            </span>
          </template>

          <template #csignAndInoId="{record,index }">
          <span class="tableUStyle" @click="showPingZheng(record)">
<!--            {{record.csign}}-{{  voucherNoAutocomplete(record.inoId, voucherRuleSize) }}-->
          </span>&ensp;
          </template>

          <template #cashProject="{ record }">
            <span style="float: right">
             {{ formeatData(record.cashProject) }}
            </span>
          </template>

          <template #mdmoney="{ record }">
            <span style="float: right">
             {{ money(record.md) }}
            </span>
          </template>
          <template #mcmoney="{ record }">
            <span style="float: right">
             {{ money(record.mc) }}
            </span>
          </template>

        </BasicTable>
        <div class="pagination-text" :style="{top: windowHeight-380+'px'}" v-show="showPageNumber">
          {{`共 ${pageNumber} 条记录&emsp;每页 200 条`}}
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
            <li>
              <span style="color: black;font-weight: bold">
              分配状态：
              </span>
              <div>
                <a-select
                  :allowClear="true"
                  placeholder=""
                  v-model:value="pageParameter.filterConditon.cashProject"
                  style="width: 200px"
                >
                  <a-select-option value="ok">已分配</a-select-option>
                  <a-select-option value="no">未分配</a-select-option>
                </a-select>
              </div>
            </li>

          </ul>
          <br/>
          <a-button type="primary"  style="float: right;" @click="filterSearch">
            <span style="font-size: 14px">开始</span>
          </a-button>
        </a-drawer>
        <Query
          @save="loadPage"
          @register="registerQueryPage"
        />

        <div  style="margin-top: 15px;background-color: whitesmoke;">
          <a-button @click="add" class="ant-btn-primary">
            保存
          </a-button>
          <span style="padding-left: 3px"></span>
          <a-button @click="fp"  class="ant-btn-primary">
            自动分配
          </a-button>

          <BasicTable
            ref="tableRef"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :row-selection="{ type: 'checkbox',selectedRowKeys: tableSelectedRowKeys, onChange: onSelectChange }"
            :scroll="{ x: totalColumnWidth,y: 250 }"
            :rowKey='record=>record.uniqueCode'
            size="small"
            @register="registerTable1"
            :dataSource="data"
          >
            <template #serialNumber="{record,index }">
            <span slot="serialNumber" slot-scope="text,record,index">
             {{record.cdfine30}}
            </span>
            </template>

            <template #csignAndInoId="{record,index }">
          <span class="tableUStyle" @click="showPingZheng(record)">
<!--            {{record.csign}}-{{  voucherNoAutocomplete(record.inoId, voucherRuleSize) }}-->
          </span>&ensp;
            </template>

            <template #cashProject="{ record }">
            <span style="float: right">
             {{ formeatData(record.cashProject) }}
            </span>
            </template>

            <template #mdmoney="{ record }">
            <span style="float: right">
             {{ money(record.md) }}
            </span>
            </template>
            <template #mcmoney="{ record }">
            <span style="float: right">
             {{ money(record.mc) }}
            </span>
            </template>

          </BasicTable>
          <div class="pagination-text"  v-show="showPageNumber">
            {{`共 ${pageNumber} 条记录&emsp;每页 200 条`}}
          </div>
<!--          <a-table
            style="margin-top: 1px;"
            :data-source="data"
            :columns="columns"
            :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
            :bordered="true"
            size="small"
            :pagination="false"
          >
            <template #num="{record,index }">
            <span slot="num" slot-scope="text,record,index">
             {{index+1}}
            </span>
            </template>
            <template v-for="col in ['money']" #[col]="{ text, record }" :key="col">
              <div style="text-align: right">
                <a-input
                  v-if="editableData2[record.num]"
                  v-model:value="editableData2[record.num][col]"
                  style="margin: -5px 0"
                />
                <template v-else>
                  {{ text }}
                </template>
              </div>
            </template>
            <template v-for="col in ['dccode']" #[col]="{ text, record }" :key="col">
              <div>
                <a-select
                  v-if="editableData2[record.num]"
                  v-model:value="editableData2[record.num][col]"
                  show-search
                  placeholder="请选择对方科目"
                  style="width: 200px"
                  :options="dkmList"
                  :filter-option="filterOption2"
                >
                </a-select>
                <template v-else>
                  {{ findCode(record.dccode) }}
                </template>
              </div>
            </template>
            <template v-for="col in ['projectname']" #[col]="{ text, record }" :key="col">
              <div>
                <a-select
                  v-if="editableData2[record.num]"
                  v-model:value="editableData2[record.num]['projectCode']"
                  show-search
                  placeholder="请选择现金流量项目"
                  style="width: 200px"
                  :options="pcList"
                  :filter-option="filterOption"
                  @change="handleChangePc(record.num, editableData2[record.num]['projectCode'])"
                >
                </a-select>
                <template v-else>
                  {{ findName(record.projectCode) }}
                </template>
              </div>
            </template>
            <template #operation="{ record }">
            <div class="editable-row-operations">
            <span v-if="editableData2[record.num]">
              <a @click="saveTable(record.num)">关闭</a>
              &lt;!&ndash;          <a-popconfirm title="是否取消?" @confirm="cancelTable(record.num)" >&ndash;&gt;
              &lt;!&ndash;            <a style="padding-left:5px;">取消</a>&ndash;&gt;
              &lt;!&ndash;          </a-popconfirm>&ndash;&gt;
            </span>
            <span v-else>
                <a-popover title="操作" trigger="hover">
                  <template #content>
                    <p>  <a @click="editTable(record.num)">编辑</a> </p>
                    <p>  <a @click="fzTable(record)">复制</a> </p>
                    <p>  <a @click="delTable(record.num)">删除</a> </p>
                  </template>
                  <a-button>
                    <SettingFilled :style="{ fontSize: '14px' }"/>
                  </a-button>
                </a-popover>
              </span>
              </div>
            </template>
          </a-table>-->

        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { BasicTable, useTable } from '/@/components/Table'
import { useModal } from '/@/components/Modal'
import { PageWrapper } from '/@/components/Page'

import {
  DatePicker as ADatePicker, Select as ASelect, Input as AInput, Popover as APopover, Switch as ASwitch,
  Radio as ARadio, Pagination as APagination, Popconfirm as APopconfirm, Table as ATable,
  Checkbox as ACheckbox, message,Drawer as ADrawer,Tag as ATag
} from "ant-design-vue"
const ARangePicker=ADatePicker.RangePicker
const ASelectOption=ASelect.Option
const AInputSearch=AInput.Search
const ARadioGroup=ARadio.Group
const ARadioButton=ARadio.Button
import {
  SortDescendingOutlined,
  SortAscendingOutlined,
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
  FilterFilled,CheckOutlined,EditOutlined,SearchOutlined,MacCommandOutlined,PrinterOutlined,UsbOutlined,UnlockTwoTone,LockTwoTone
} from '@ant-design/icons-vue'
import {
  findAllAccvoucherXianJin,
  breakNumTidy,
  findDbLanMuList,
  saveLanMuList,
  getFpInfo,
  getDistributeInfo, findAllAccvoucher,
} from '/@/api/record/system/accvoucher'
import { useAccvoucherXjStore } from '/@/store/modules/accvoucher-xianjin'
import {onMounted, reactive, ref} from "vue";
import moment from "moment";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {changeDefaultDynamics, initDynamics,assemblyDynamicColumn,voucherNoAutocomplete} from "./data";
import Query from "./popup/query.vue";
import {cloneDeep} from "lodash-es";
import {
  getThisAdInfoData,
} from "/@/api/record/system/financial-settings";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  askTask,
  compareTime, findAccCloseListByYaer,
  findByFunctionModule,
  getCurrentAccountName,
  offsetToStr, pointMessage,getThisIndexImg, hasBlank
} from "/@/api/task-api/tast-bus-api";
import {
  intervalWorking
} from "/@/views/boozsoft/system/accvoucher/data";
import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findProjectCash,saveFp} from "/@/api/record/generalLedger/data";
import AccountInfo from "/@/views/boozsoft/system/accvoucher/popup/AccountInfo.vue";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
import {toThousandFilter} from "/@/utils/calculation";

const { createConfirm,createWarningModal } = useMessage();
// 全局常量
const accvoucherStore = useAccvoucherXjStore()
// 页面变量
const pageParameter = reactive({
  queryMark: '1',
  showRulesSize: 'MIN',
  thisInterval: '2020.01 - 2021.12',
  biZhong: '人民币',
  companyCode: '100',
  ifUnit: false,
  companyName: '湖北万亚软件技术有限公司',
  condition: {},
  searchConditon: {
    requirement: 'inoId',
    value: '',
  },
  filterConditon: {
    amountMin: '',
    amountMax: '',
  },
})
//对方科目list
const dkmList = ref([])
//流量项目list
const pcList = ref([])
//现金科目条数
const countNum = ref(0)
//净利润
const jlr = ref(0.00)
let defalutCompanyCode = ''
const val = {
  openOne: 0
}
const pageChange = () => {
  pageReload()
}
const userStore = useUserStore();
const defaultPage = ref(true) // 默认为独立账套
const visible = ref(false);
const searchConditonList = ref([])
const lanMuData = {'accId':'','menuName':'现金流量录入','type':'',objects: '',username:useUserStoreWidthOut().getUserInfo.username}
// 表格参数 司马懿 用武 识别 挡风 赞比 竭力 刮骨
const loadMark = ref(false)
const tableSelectedRowKeys = ref([])
const tableSelectedRowObjs = ref([])
const windowWidth  = (document.documentElement.clientWidth -70)
const windowHeight  = (document.documentElement.clientHeight -350)
const totalColumnWidth = ref(0)
const defaultDbName = useCompanyOperateStoreWidthOut().getTenentName
const manipulateDbName = ref(getCurrentAccountName(true))
const CrudApi = reactive({
  list: useRouteApi(findAllAccvoucherXianJin,{schemaName:manipulateDbName}),
  columns: JSON.parse(JSON.stringify(accvoucherStore.getColumns(pageParameter.queryMark))),
})
const tableRef = ref(null)
const tableRef2 = ref(null)

// 组件实例区
const [registerTable, { reload,setColumns,getColumns,setTableData,getDataSource }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  loading: loadMark.value,
  immediate: false,
  canResize: true,
  /*  showSummary: true,
    summaryData: [],*/
  showIndexColumn: false , //显示序号列
  pagination: {
    pageSize: 200,
    simple:true
  },
  tableSetting: {
    // 是否显示刷新按钮
    redo: true,
    // 是否显示尺寸调整按钮
    size: true,
    // 是否显示字段调整按钮
    setting: true,
    // 是否显示全屏按钮
    fullScreen: true,
  }
})

const [registerInfo, {openModal: openInfoPageM}] = useModal()

const [registerQueryPage, { openModal: openQueryPageM }] = useModal()

onMounted( ()=>{
  // useCompanyOperateStoreWidthOut().commitSchemaName('bjxgkj-001');
  val.openOne = 1
  openQueryPageM(true, {
    data: val
  })
  loadMark.value = true
})

function money(data:any){
  let str = ""
  if(data){
    // 千分位保留2位小数
    var source = String(parseFloat(data).toFixed(2)).split("."); //按小数点分成2部分
    source[0] = source[0].replace( new RegExp('(\\d)(?=(\\d{3})+$)' , 'ig'), "$1,");//只将整数部分进行都好分割
    str = source.join("." );//再将小数部分合并进来
  }
  return data == ('0.00' || '0')?'': str;
}

function formeatData(val:any) {
  return  hasBlank(val)? '' : val === "1"?'已分配': '未分配'
}
const modify = (code) => {
  if (pageParameter.companyCode != code) {
    pageParameter.companyCode = code
    pageSearch()
  }
}
const openQueryPage = () => {
  val.openOne = 0
  openQueryPageM(true, {
    data: val
  })
}

const unCheckTable = ()=>{
  if (tableSelectedRowKeys.value.length > 0){
    tableSelectedRowKeys.value = []
    tableSelectedRowObjs.value = []
  }
}
// 页面函数区
const onSelectChange = (selectedRowKeys,obj) => {
  tableSelectedRowKeys.value = selectedRowKeys;
  tableSelectedRowObjs.value = obj;
};
const pageReload =  () =>{
  setTableData([]) // 清空可能残留的数据
  reload({
    searchInfo: pageParameter
  })
}
let voucherRuleSize = '4';
const showThis = ref(false)
const pageReload2 = async (obj) => {
  let data = JSON.parse(JSON.stringify(pageParameter))
  data.page = getPaginationRef().current
  data.size = getPaginationRef().pageSize
  loadMark.value = true
  let res = await useRouteApi(findAllAccvoucher, {schemaName: obj.accId + '-' + obj.year})(data)
  if (res != null && res.items.length > 0) {
    manipulateDbName.value = obj.accId + '-' + obj.year
    voucherRuleSize = obj.accvouchDec || '4'
    setTableData([]) // 清空可能残留的数据
    setTableData(res.items)
    // 底部分页信息
    setPagination({total: res.total})
  } else {
    createWarningModal({title: '温馨提示', content: '暂无任何数据！'});
  }
  loadMark.value = false
}

const pageReload3 = async () =>{
  setTableData([]) // 清空可能残留的数据
  let data = JSON.parse(JSON.stringify(pageParameter))
  data.page = 1
  data.pageSize = 5000
  let res  =  await useRouteApi(findAllAccvoucherXianJin,{schemaName:manipulateDbName.value})(data)
  if (res != null && res.items.length > 0)setTableData(res.items)
}

const breakNumTidyBtn = async() => {
  breakNumTidy()
  pageReload()
}
const onChangeSwitch = async(v)=>{ // 动态列
  pageParameter.queryMark = v?'1':'2'
  resetDynamicColumnData()
  pageParameter.searchConditon.requirement = 'inoId'
  pageParameter.searchConditon.value = ''
}
const  pageSearch = async ()=>{
  /* if (''==pageParameter.searchConditon.value.trim())return false*/
  // 搜索前校验格式
  if (''==pageParameter.searchConditon.requirement.trim()){
    message.warn('请选择检索条件')
    return false
  }
  // 校验完成后搜索
  closeFilterV()
  pageReload()
}

const  pageSearch2 = async ()=>{
  // 检查是否存在 查看是否拥有权限
  if ((defalutCompanyCode == pageParameter.companyCode && manipulateDbName.value == '')
    || pageParameter.companyCode == tempValue.value  && manipulateDbName.value != '') return false
  let accObj = accAuthList.value.filter(item=>item.coCode == pageParameter.companyCode)
  if (accObj.length == 0){
    message.warn('代码不存在停止切换！')
    pageParameter.companyCode = defalutCompanyCode
  }else if(accObj.length > 0){
    let iyear = pageParameter.thisInterval.substring(0,4)
    let authObj = userAuthMap.value.filter(item=>accObj[0].accId == item.accId && item.iyear == iyear)
    if (authObj.length == 0){
      message.warn('无该代码'+iyear+'年度数据权限！')
      pageParameter.companyCode = defalutCompanyCode
    }else {
      manipulateDbName.value = accObj[0].accId+'-'+iyear
      pageParameter.companyName = accObj[0].accNameCn

      pageReload3()
    }
  }
}

const tempValue = ref(defalutCompanyCode)

const initCodeValue = async (type)=>{
  if (type == 'set'){
    if ((pageParameter.companyCode == '' || pageParameter.companyCode != '' ) && manipulateDbName.value == ''){
      pageParameter.companyCode = defalutCompanyCode
    }else if (manipulateDbName.value != '' && pageParameter.companyCode == ''){
      pageParameter.companyCode =tempValue.value
    }
  }else {
    tempValue.value = pageParameter.companyCode
    pageParameter.companyCode = ''
  }
}

const  filterSearch = async ()=>{
  /* if (''==pageParameter.searchConditon.value.trim())return false*/
  // 搜索前校验格式
  let min  = pageParameter.filterConditon.amountMin.trim()
  let max  = pageParameter.filterConditon.amountMax.trim()
  if (''==min && max != '' || ''==max && min != ''){
    message.warn('请完善金额区间过滤条件！')
    return false
  }
  if (min != '' && max != '' && (!isRealNum(min) || !isRealNum(max) || parseFloat(min) == 0 || parseFloat(max) == 0)){
    message.warn('请输入数值类型值并且值不能为0！')
    closeFilterV()
    return false
  }else if (parseFloat(min) > parseFloat(max)){
    message.warn('金额区间最小值部门大于最大值！')
    closeFilterV()
    return false
  }
  // 校验完成后搜索
  pageReload()
}

const closeFilterV = ()=>{
  pageParameter.filterConditon.amountMin =''
  pageParameter.filterConditon.amountMax =''
  visible.value = false
  reloadColumns()
}

function isRealNum(val){
  // isNaN()函数 把空串 空格 以及NUll 按照0来处理 所以先去除，
  if(val === "" || val ==null){
    return false;
  }
  if(!isNaN(val)){
    //对于空数组和只有一个数值成员的数组或全是数字组成的字符串，isNaN返回false，例如：'123'、[]、[2]、['123'],isNaN返回false,   //所以如果不需要val包含这些特殊情况，则这个判断改写为if(!isNaN(val) && typeof val === 'number' )
    return true; 　　}　else{ 　　　　return false; 　　}
}

const analyzeTheYearAndAllMonths = (list:any)=>{
  let months = []
  if (list.length === 0) return{year: '',months: months}
  list.forEach(item=>{
    let thisMonth = item.dbillDate.split('-')[1]
    if (months.indexOf(thisMonth) == -1){
      months.push(thisMonth)
    }
  })
  return {
    year: list[0].dbillDate.split('-')[0],
    months: months
  }
}
/*start栏目设置*/
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});
const editableData2 = reactive({});

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

const cancel = (e: MouseEvent) => {
  // 恢复默认
  dynamicColumnData.value = []
  dynamicColumnData.value = dynamicColumnDataCopy
}


function resetDynamicColumnData() {
  // 先从数据查询是否已经设置
  lanMuData.accId = getCurrentAccountName(true)
  lanMuData.type = pageParameter.queryMark=='1'?'明细':'汇总'
  findDbLanMuList(lanMuData).then(res=>{
    // 栏目列
    let dbList = res.items
    if (dbList.length > 0){
      let statiList = initDynamics()['DATA'+pageParameter.queryMark]
      dbList = combineParameters(statiList,dbList)
      dynamicColumnData.value = dbList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(dbList))
    }else {
      let statiList = initDynamics()['DATA'+pageParameter.queryMark]
      dynamicColumnData.value = statiList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(statiList))
    }
    // 表格列
    reloadColumns()
    pageReload()
  })
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

const reloadColumns = ()=>{
  let a = []
  a = getColumns()
  let newA = JSON.parse(JSON.stringify(accvoucherStore.getColumns(pageParameter.queryMark)))
  newA= assemblyDynamicColumn(dynamicColumnData.value,newA)
  setColumns(newA)
  initTableWidth(newA)
  searchConditonList.value = newA
}

function initTableWidth(thisCs){
  let total = 60
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })
  if (total > windowWidth) {
    let f = 0
    if (visible.value) f = 260
    totalColumnWidth.value = Number(windowWidth) - f
    // tableRef.value.$el.style.setProperty('width', (windowWidth + 20 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    // tableRef.value.$el.style.setProperty('width', (total + 20) + 'px')
  }
}
/*栏目设置end*/

// 已授权账套列表
const accAuthList = ref([])
// 账套授权信息
const userAuthMap = ref([])
// 数据库模式名称
const database = ref(getCurrentAccountName(false))
const databaseTrue = ref(getCurrentAccountName(true))
const loadPage = async(data)=>{
  console.log(data.variable.accId)
  manipulateDbName.value = data.variable.database
  database.value = data.variable.accId
  databaseTrue.value = data.variable.database
  //流量项目对照
  let list  =  await useRouteApi(findProjectCash,{schemaName:databaseTrue.value})({accId:databaseTrue.value})
  pcList.value = list
  // 获取当前账套信息
  getThisAdInfoData({'accId': database.value}).then(res=>{
    if (null != res && res.independent == 0){
      defaultPage.value = false
      pageParameter.companyName = res.accNameCn
      defalutCompanyCode = res.coCode
      pageParameter.companyCode = res.coCode
      pageParameter.ifUnit = res.icorp == '1'
    }
    pageParameter.biZhong = data.constant.currency
    accAuthList.value = data.accAuthList
    userAuthMap.value = data.userAuthMap
    data.accAuthList = []
    data.userAuthMap = []
    pageParameter.condition = data
    if (data.variable.periodStart == '' && data.variable.periodEnd == ''){
      pageParameter.thisInterval = data.variable.dateStart.replace('-','.')+' - '+data.variable.dateEnd.replace('-','.')
    }else {
      pageParameter.thisInterval = data.variable.periodStart.replace('-','.')+' - '+data.variable.periodEnd.replace('-','.')
    }
    pageParameter.accId = database.value

    if (data.openOne == 1){ // 第一次初始化 + 条件查询
      resetDynamicColumnData()
    }else { // 查询条件查询
      closeFilterV()
      pageParameter.searchConditon.value = ''
      pageReload()
    }
  })
}

const columns = JSON.parse(JSON.stringify(accvoucherStore.getColumns('2')))

const data = ref([{},{},{},{},{},{},{},{},{},{}])
//点击的凭证唯一码
const uniqueCode = ref()

const loadFpList = async(record, index, event)=>{
  const a = {}
  let flg = '0'
  uniqueCode.value = record.uniqueCode;
  getDataSource().filter(v=> v.uniqueCode === record.uniqueCode).forEach((v)=>{
    if(record.cashProject === '1'){
      flg = '1'
    }
    a[v.ccode] = v.cdfine30;
  })
  const data2 = await useRouteApi(getDistributeInfo,{schemaName:databaseTrue.value})({
    uniqueCode: record.uniqueCode,
    num: a,
    cashProject: flg,
    accId: databaseTrue.value,
  });
  data.value = data2.cf
  //当前选中的对方科目
  dkmList.value = data2.dkmList
  //现金条数
  countNum.value = data2.ts
  //净利润
  jlr.value = data2.jlr
}
const editTable = (key: string) => {
  editableData2[key] = cloneDeep(data.value.filter(item => key === item.num)[0]);
};
const saveTable = (key: string) => {
  //验证参数
  if(!editableData2[key].money){
    message.error("请输入金额！")
    return
  }
  if(!editableData2[key].dccode){
    message.error("请选择对方科目！")
    return
  }
  if(!editableData2[key].projectCode){
    message.error("请选择现金流量科目！")
    return
  }
  Object.assign(data.value.filter(item => key === item.num)[0], editableData2[key]);
  delete editableData2[key];
};
const cancelTable = (key: string) => {
  delete editableData2[key];
};
const fzTable = (record:any) => {
  let d = JSON.parse(JSON.stringify(record))
  d.num =  data.value.length + 1
  data.value.push(d)
};
const delTable = (key: string) => {
  data.value.splice(key-1,1);
  //重新排序
  data.value.forEach((v,i)=>{
    v.num = i+1
  })
};

const handleChangePc = (key: string,value: string) => {
  const pc = pcList.value.filter(v=> v.value === value)[0]
  editableData2[key].projectCode = pc.value
  editableData2[key].projectnum = pc.value
  editableData2[key].projectName = pc.label
  editableData2[key].fx = "1" === pc.fx ?'流入':'流出'
  const d = data.value.filter(item => key === item.num)[0]
  d.projectCode = pc.value
  d.projectnum = pc.value
  d.projectName = pc.label
  d.fx = "1" === pc.fx ?'流入':'流出'
};

const filterOption = (input: string, option: any) => {
  return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};
const filterOption2 = (input: string, option: any) => {
  return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};
const findName = (value: string) => {
  const list = pcList.value.filter(v=> v.value === value)
  return list.length ===0 ? '' : list[0].label;
};
const findCode = (value: string) => {
  const list = dkmList.value.filter(v=> v.value === value)
  return list.length ===0 ? '' : list[0].label;
};
const add = async() => {
  //验证
  //条数
  if(data.value.length <= 0 && data.value.length < countNum.value){
    message.error("分配条数必须大于现金流量科目数量")
    return
  }
  //净利润
  let tjlr= 0.00;
  let datas = []
  data.value.forEach(v=>{
    tjlr = parseFloat(tjlr) + parseFloat(v.money)
    datas.push({
      //id: v.id,
      tenantId: databaseTrue.value,
      iyear: '2021',
      ccode: v.ccode,
      uniqueCode: v.uniqueCode,
      dccode: v.dccode,
      projectCode: v.projectCode,
      money: v.money,
    })
  })
  //现金流量总金额  非现金总金额
  console.log(jlr.value)
  console.log(tjlr)
  console.log(data.value)
  if(jlr.value != tjlr.toFixed(2)){
    message.error("现金流量项目金额与分录不相等")
    return
  }
  //jlr.value != '0.00'
  await useRouteApi(saveFp,{schemaName:databaseTrue.value})({
    list: JSON.stringify(datas),
  });
  pageReload()
};
const fp = async() => {
  const a = {}
  let flg = '0'
  getDataSource().filter(v=> v.uniqueCode === uniqueCode.value).forEach((v)=>{
    a[v.ccode] = v.cdfine30;
  })
  const data2 = await useRouteApi(getFpInfo,{schemaName:databaseTrue.value})({
    uniqueCode: uniqueCode.value,
    num: a,
    cashProject: flg,
    accId: databaseTrue.value,
  });
  data.value = data2.cf
  //当前选中的对方科目
  dkmList.value = data2.dkmList
  //现金条数
  countNum.value = data2.ts
  //净利润
  jlr.value = data2.jlr
};
const dynamicAdReload = async (obj) =>{
  if(pageParameter.strDate!==''){

  }
  loadMark.value = false
}

const showPageNumber=ref(false)
const pageNumber=ref(0)
function fetchSuccess(data) {
  let list=data.items.filter(a=>a.ccode.indexOf('小计')!=-1)
  pageNumber.value=data.items.length
  showPageNumber.value=true
  calculateTotal(list)
  if(data.items.length<10){
    for (let i =  data.items.length; i < 10; i++) {
      data.items.push({})
    }
  }
}

const summaryTotals = ref({})
const calculateTotal = (datalist) => {
  let list = JsonTool.parseProxy(datalist)
  if (list.length == 0){
    summaryTotals.value = {}
    return false;
  }
  let qcMd = 0
  let qcMc = 0
  let qcNum = 0
  let qcNfrat = 0
  let pzMd = 0

  for (let i = 0; i < list.length; i++) {
    const e = list[i];


  }
  summaryTotals.value={


  }
}

const [registerTable1, {
  reload: reload1,
  getDataSource: getDataSource1,
  setTableData: setTableData1,
  setPagination: setPagination1,
  getPaginationRef: getPaginationRef1,
  getColumns: getColumns1,
  setColumns: setColumns1
}] = useTable({
  // api: CrudApi.list,
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  pagination: {
    pageSize: 200,
    simple:true
  },
  /*actionColumn: {
    width: 120,
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'}
  }*/
})

</script>
<style scoped lang="less">
@import '/@/assets/styles/part-open.less';
@import '/@/assets/styles/global-menu-index1.less';
:deep(.ant-select){
  border: none;
  background-color: #f1f1f1;
  color: black;
}

:deep(.ant-select:not(.ant-select-customize-input) .ant-select-selector){
  border: 1px #cccccc solid;
  height: 33px;
}

// ***************  button样式  ***************
.actod-btn {
  color: @Global-Comm-BcOrText-Color;
  font-size: 14px;
  border-color: @Global-Border-Color;
  border-right: none;
}

.actod-btn-last {
  border-right: 1px solid @Global-Border-Color;
}

.actod-btn:hover {
  background-color: @Global-Comm-BcOrText-Color;
  color: white;
}
// ***************  button样式  ***************
:deep(.vben-basic-table .ant-pagination) {
  margin-top: 0px;
  background-color: #cccccc;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
}
.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 10px;
  margin: 10px 10px 5px;
  display: inline-flex;
  width: 99%;
  border-radius: 5px;
}

.app-container:nth-of-type(2) {
  padding: 0px;
  margin: 0px 10px;
  background: #b4c8e3 !important;
  margin-top: 0px;
  position: relative;
  .pagination-text{
    position: absolute;
    bottom: 9px;
    right: 12%;
    font-size: 13px;
    color: black;
    z-index: 99999999;
  }
}
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 14% !important;
  padding: 2px 8px !important;
  height: 25px;
  color: black;
}
.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 13px !important;
  padding: 2px 8px !important;
  height: 25px;
  color: black;
}

.bg-white{
  border: 1px #cccccc solid;
  background:white ;
  margin-top: 10px;
}
:deep(.ant-tree .ant-tree-node-content-wrapper.ant-tree-node-selected),
:deep(.ant-table-tbody > tr.ant-table-row-selected > td){
  background-color: #1488b1;
  color: white;
}
// 合计行
:deep(.nc-summary){
  font-weight: bold;
  border-bottom-color: #9e9e9e;
  background-color: #cccccc!important;;
  border-right-color: #cccccc!important;
}

</style>
