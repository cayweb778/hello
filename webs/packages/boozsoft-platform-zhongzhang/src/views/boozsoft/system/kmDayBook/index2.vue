<template>
  <div class="app-container">
      <div class="app-container-head">
<!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false" >-->
        <div class="container-head-title" style="padding-left: 650px;">
          <b class="noneSpan">{{ titleName }}日记账</b>
        </div>
        <div class="ant-btn-group">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="openQueryPage()"
          ><span>查询</span></button>
          <a-popover placement="bottom">
            <template #content>
              <span class="group-btn-span">导出当前</span><br/>
              <span class="group-btn-span">条件导出</span>
            </template>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
            ><span>导出</span></button>
          </a-popover>
          <a-popover placement="bottom">
            <template #content>
              <span class="group-btn-span">打印当前</span><br/>
              <span class="group-btn-span">条件打印</span>
            </template>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              ant-click-animating-without-extra-node="false"
            ><span>打印</span></button>
          </a-popover>
          <button type="button" class="ant-btn ant-btn-me" @click="closeCurrent(),router.push('/zhongZhang/home/welcome')"><span>退出</span></button>
        </div>
        <div style="clear:both"/>
        <div>
          <div>
            <AccountPicker readonly="" theme="one" @reloadTable="dynamicAdReload"/>
          </div><br>
          <div style="display: table;float: left;font-size: 14px;">
            <span style="margin-left: 20px;">科目：</span>
            <a-select
              show-search
              placeholder="科目选择"
              option-filter-prop="children"
              style="width: 180px"
              :filter-option="filterOption"
              @change="handleChangeMinKm"
              v-model:value="pageParameter.km"
            >
              <a-select-option v-for="d in kmList" :value="d.ccode">
                {{ d.value }}
              </a-select-option>
            </a-select>
          </div>

          <div style="display: inline-block;float: left;margin-top: .8%;font-size: 14px;margin-left: 8px;">
            &emsp;&emsp;本位币：<span style="font-weight: bold">{{ bwb }} </span>
            &emsp;&emsp;样式：<span style="color: black;font-weight: bold">{{ styleName }}</span>
            <span style="color: black;font-size: 14px;padding-left: 170px;">
            期间：<span>{{ time.strDate }}~{{ time.endDate }}</span>
          </span>
          </div>

          <div style="float: right; margin-left: 10px">
            <a-button class="ant-btn" @click="closeFilterV(),pageReload()">
              <SyncOutlined :style="{ fontSize: '14px' }"/>
            </a-button>

            <a-popover placement="bottom">
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
                  <a-button style="width: 165px;border: none">栏目设置</a-button>
                </a-popconfirm>
                <br/>
                <a-popconfirm
                  ok-text="保存"
                  cancel-text="关闭"
                  @confirm="transferOk"
                  @cancel="transferCancel"
                >
                  <template #icon><b>摘要设置</b><br></template>
                  <template #title>
                    <a-transfer
                      :data-source="mockData"
                      :titles="['待选择', '已选择']"
                      :target-keys="targetKeys"
                      :selected-keys="selectedKeys"
                      :render="item => item.title"
                      :list-style="{
                      width: '250px',
                      height: '350px',
                    }"
                      @change="transferChange"
                      @selectChange="transferSelectChange"
                    />
                  </template>
                  <a-button style="width: 165px;border: none">摘要设置</a-button>
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

            <a-popover placement="bottom">
              <template #content>
              <span @click="onChangeSwitch('J')"
                    :style="{backgroundColor: (pageParameter.queryMark==='J')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;<OrderedListOutlined
                :style="{ fontSize: '14px' }"/>&emsp;&emsp;金额式&nbsp;<CheckOutlined v-if="pageParameter.queryMark==='J'"
                                                                                   :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
                <span @click="onChangeSwitch('SJ')"
                      :style="{backgroundColor: (pageParameter.queryMark==='SJ')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;<OrderedListOutlined
                  :style="{ fontSize: '14px' }"/>&emsp;数量金额式&nbsp;<CheckOutlined v-if="pageParameter.queryMark==='SJ'"
                                                                                 :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
                <span @click="onChangeSwitch('WJ')"
                      :style="{backgroundColor: (pageParameter.queryMark==='WJ')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;<OrderedListOutlined
                  :style="{ fontSize: '14px' }"/>&emsp;外币金额式&nbsp;<CheckOutlined v-if="pageParameter.queryMark==='WJ'"
                                                                                 :style="{ fontSize: '14px' }"/>&emsp;</span><br/>
                <span @click="onChangeSwitch('SWJ')"
                      :style="{backgroundColor: (pageParameter.queryMark==='SWJ')?'#e3e0e0':'white',cursor: 'pointer'}">&emsp;<OrderedListOutlined
                  :style="{ fontSize: '14px' }"/>&emsp;数量外币式&nbsp;<CheckOutlined v-if="pageParameter.queryMark==='SWJ'"
                                                                                 :style="{ fontSize: '14px' }"/>&emsp;</span><br/>

              </template>
              <a-button>
                <PicLeftOutlined :style="{ fontSize: '14px' }"/>
              </a-button>
            </a-popover>
            <a-button @click="()=>{
            if (!visible){ visible = true;reloadColumns()}
            return false
          }">
              <FilterFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </div>
          <div style="float: right; position: relative">
            <!-- 搜索 -->
            <a-select v-model:value="pageParameter.searchConditon.requirement" style="width: 130px;"
                      class="special_select">
              <template v-for="item in searchConditonList.slice(1)">
                <a-select-option v-if="item.ifShow == true" :value="item.dataIndex">
                  {{ item.title }}
                </a-select-option>
              </template>
            </a-select>
            <a-input-search
              placeholder=""
              v-model:value="pageParameter.searchConditon.value"
              @search="pageSearch"
              style="width: 150px;border-radius: 4px"
            />
          </div>
        </div>
      </div>
      <div style="clear:both"/>
    <div style="margin-top: 10px;">
      <BasicTable
        ref="tableRef"
        :rowClassName="tableRow"
        :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
        :scroll="{ x: totalColumnWidth,y: windowHeight }"
        size="small"
        @register="registerTable"
      >
        <template #md="{ record }">
          {{ moneyformat(record.md) }}
        </template>
        <template #mc="{ record }">
          {{ moneyformat(record.mc) }}
        </template>

        <template #yue="{ record }">
          {{ moneyformat(record.yue) }}
        </template>
        <template #ndS="{ record }">
          {{ numformat(record.ndS) }}
        </template>
        <template #ncS="{ record }">
          {{ numformat(record.ncS) }}
        </template>
        <template #yue_num="{ record }">
          {{ numformat(record.yue_num) }}
        </template>
        <template #nfrat_md="{ record }">
          {{ moneyformat(record.nfrat_md) }}
        </template>
        <template #nfrat_mc="{ record }">
          {{ moneyformat(record.nfrat_mc) }}
        </template>
        <template #yue_nfrat="{ record }">
          {{ moneyformat(record.yue_nfrat) }}
        </template>
        <template #synum="{ record }">
          {{ numformat(record.synum) }}
        </template>
        <template #sywb="{ record }">
          {{ moneyformat(record.sywb) }}
        </template>

        <template #symoney="{ record }">
          {{ moneyformat(record.symoney) }}
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
          <li>
            <span style="color: black;font-weight: bold">
              借方本币：
            </span>
            <div>
              <a-input v-model:value="pageParameter.filterConditon.amountMinJf" placeholder=""
                       style="width: 95px;font-size: 10px"/>
              ~
              <a-input v-model:value="pageParameter.filterConditon.amountMaxJf" placeholder=""
                       style="width: 95px;font-size: 10px;float: right"/>
            </div>
          </li>
          <li style="margin-top: 5%">
            <span style="color: black;font-weight: bold">
              贷方本币：
            </span>
            <div>
              <a-input v-model:value="pageParameter.filterConditon.amountMinDf" placeholder=""
                       style="width: 95px;font-size: 10px"/>
              ~
              <a-input v-model:value="pageParameter.filterConditon.amountMaxDf" placeholder=""
                       style="width: 95px;font-size: 10px;float: right"/>
            </div>
          </li>
          <li style="margin-top: 5%">
            <span style="color: black;font-weight: bold">
              余额本币：
            </span>
            <div>
              <a-input v-model:value="pageParameter.filterConditon.amountMinYe" placeholder=""
                       style="width: 95px;font-size: 10px"/>
              ~
              <a-input v-model:value="pageParameter.filterConditon.amountMaxYe" placeholder=""
                       style="width: 95px;font-size: 10px;float: right"/>
            </div>
          </li>
        </ul>
        <br/>
        <a-button type="primary" style="float: right;" @click="filterSearch">
          <span style="font-size: 14px">开始</span>
        </a-button>
      </a-drawer>
      <Query
        @save="loadPage"
        @register="registerQueryPage"
      />
    </div>
</template>
<script setup lang="ts">
import {BasicTable, useTable} from '/@/components/Table'
import {useModal} from '/@/components/Modal'
import {PageWrapper} from '/@/components/Page'

import {
  Transfer as ATransfer,
  DatePicker as ADatePicker, Select as ASelect, Input as AInput, Popover as APopover, Switch as ASwitch,
  Radio as ARadio, Upload as AUpload, Pagination as APagination, Popconfirm as APopconfirm, Table as ATable,
  Checkbox as ACheckbox, message, Drawer as ADrawer
} from "ant-design-vue"
import {
  SortDescendingOutlined,
  SortAscendingOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  FilterFilled, CheckOutlined, OrderedListOutlined
} from '@ant-design/icons-vue'
import {delDigestLanMuList, findDbLanMuList, saveLanMuList} from '/@/api/record/system/accvoucher'

import {onMounted, reactive, ref} from "vue";
import moment from "moment";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {cloneDeep} from "lodash-es";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  getCurrentAccountName,
  getThisIndexImg
} from "/@/api/task-api/tast-bus-api";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import router from "/@/router";
import {savelog} from "/@/api/record/log";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findByAccId} from "/@/api/record/system/account";
import {useRoute} from "vue-router";
// ##############################
import {daybookGlStore} from '/@/api/record/km_day_book/generalLedger'
import {findAllDayBook, finByParameterAccuracy} from '/@/api/record/km_mingxi/data';
import {initDynamics, assemblyDynamicColumn} from "./data";
import Query from "/@/views/boozsoft/system/kmDayBook/popup/query.vue";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {findAll} from "/@/api/record/km_huizong/data";
import {useTabs} from "/@/hooks/web/useTabs";


const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const {createConfirm, createWarningModal} = useMessage();
// 全局常量
const glStore = daybookGlStore()
const { closeCurrent } =useTabs();
const mockData = ref([
  {
    key: '0',
    title: '标准辅助核算项编码',
  },
  {
    key: '1',
    title: '标准辅助核算项名称',
  },
  {
    key: '2',
    title: '扩展辅助核算项编码',
  },
  {
    key: '3',
    title: '扩展辅助核算项名称',
  },
  {
    key: '4',
    title: '结算方式名称',
  },
  {
    key: '5',
    title: '结算日期',
  },
  {
    key: '6',
    title: '票据号',
  },
  {
    key: '7',
    title: '结算单位',
  }
])
const targetKeys = ref([])
const selectedKeys = ref([])
// 页面变量
const pageParameter = reactive({
  database: '',
  thisAdInfo: {},
  total: 0,
  page: '',
  size: '',
  dynamicTenantId:'',
  queryMark: 'J',
  showRulesSize: 'MIN',
  condition: {},
  searchConditon: {
    requirement: 'bprogerty',
    value: '',
  },
  filterConditon: {
    amountMinDf: '',
    amountMaxDf: '',
    amountMinJf: '',
    amountMaxJf: '',
    amountMinYe: '',
    amountMaxYe: '',
  },
  reloadMark: false,
  numJd: '2',  // 数量精度
  priceJd: '2',  // 金额精度
  lvJd: '2',  // 汇率精度
  moneyJd: '2',  // 金额精度
  wb: "",
  dw: "",
  km: "",
  minKm: "",
  maxKm: "",
  // 显示期间
  endDate: "",
  strDate: "",
  //显示未记账
  ishaveRjz: true,
  // 级次
  minJc: "1",
  maxJc: "1",
  bend: '',
  timflg: 'qj',
  bz: '',
  styleValue: '',
  parameteraccuracyJSON: '',
  digestStyle: '',
})
const styleName = ref<String>("金额式")
// 科目名称标题
const titleName = ref<String>("")
// 本位币
const bwb = ref<String>("")
// 会计科目
const kmList: any = ref([])
//币种名称
const bzName = ref<String>("")
const database = ref(getCurrentAccountName(true))
const accId = ref(getCurrentAccountName(false))
//显示未记账
const ibook = ref<boolean>(true)
const showStyle = ref([
  {
    'name': '金额式',
    'value': 'J'
  }, {
    'name': '数量金额式',
    'value': 'SJ'
  }, {
    'name': '外币金额式',
    'value': 'WJ'
  }, {
    'name': '数量外币式',
    'value': 'SWJ'
  }
])
const time = reactive(
  {
    endDate: "",
    strDate: ""
  }
)
const val = {
  openOne: 0
}
const visible = ref(false);
const searchConditonList = ref([])
const lanMuData = {
  'accId': '',
  'menuName': '日记账',
  'type': '',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username
}
const digestLanMuData = {
  'accId': accId.value,
  'menuName': '明细账摘要',
  'type': '',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username
}
// 表格参数
const loadMark = ref(false)
const tableSelectedRowKeys = ref([])
const tableSelectedRowObjs = ref([])
const windowWidth = (document.documentElement.clientWidth - 70)
const windowHeight = (window.innerHeight - (350))
const totalColumnWidth = ref(0)

const tableRef = ref(null)
const userName = useUserStoreWidthOut().getUserInfo.username
// 增加操作日志：1、修改2、删除3、停用、4查看、5导入、6导出、7打印
const logmap = ref({
  accId: database.value,
  username: useUserStoreWidthOut().getUserInfo.username,
  flag: '4',
  text: '',
});

const CrudApi = {
  list: findAllDayBook,
  columns: JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark)))
}
const selectData=ref([])

// 组件实例区
const [registerTable, { reload,setTableData,setColumns,getColumns,getPaginationRef,setPagination }] = useTable({
  api: useRouteApi(CrudApi.list, {schemaName: database}),
  columns: CrudApi.columns,
  bordered: true,
  loading: loadMark.value,
  immediate: false,
  canResize: true,
  showIndexColumn: false, //显示序号列
  pagination: {
    pageSize: 200,
    showSizeChanger: true,
    pageSizeOptions: ['200', '500', '1000', '1500'],
    showTotal: t => `总共${t}条数据`
  },
  searchInfo: pageParameter,
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
const route = useRoute();
const routemsg = ref(route.query);
const [registerQueryPage, {openModal: openQueryPageM}] = useModal()
const [registerReviewPage, {openModal: openReviewPageM}] = useModal()
onMounted(async () => {
  if (JSON.stringify(routemsg.value).length === 2) {
    val.openOne = 1
    openQueryPageM(true, {
      data: val
    })
    loadMark.value = true
    // 查询是否独立账套  是否独立账套（1）反之集团账套（0）
    const datainfo = await findByAccId(accId.value);
    bwb.value = datainfo.currencyCh
  } else {
    pageParameter.ishaveRjz = routemsg.value.ishaveRjz
    pageParameter.bend = routemsg.value.jc
    pageParameter.strDate = routemsg.value.strDate.replaceAll("-", "")
    pageParameter.endDate = routemsg.value.endDate.replaceAll("-", "")
    pageParameter.maxJc = routemsg.value.maxJc
    pageParameter.minJc = routemsg.value.minJc
    pageParameter.km = routemsg.value.km
    pageParameter.timflg = 'qj'
    pageParameter.bz = routemsg.value.bzName
    // 用于显示
    time.strDate = routemsg.value.strDate.replaceAll("-", ".")
    time.endDate = routemsg.value.endDate.replaceAll("-", ".")

    let ParameteRule= await useRouteApi(finByParameterAccuracy,{schemaName:database})('')
    pageParameter.parameteraccuracyJSON = JSON.stringify(ParameteRule)
    resetDynamicColumnData()
  }
})

function tableRow(row:any,num:any){
  return  (row.cdigest=='本日合计' ? 'table-day-striped' : null) || (row.cdigest=='本月合计' ? 'table-month-striped' : null)
    || (row.cdigest=='本年累计' ? 'table-year-striped' : null)
    || (row.number%2==0 ? 'table-odd-striped' : null)
}

 // 选项在两栏之间转移时的回调函数
function transferChange(a,b) {
  targetKeys.value = a
}
 // 选中项发生改变时的回调函数
function transferSelectChange(a,b) {
  selectedKeys.value=[...a,...b]
}

const transferOk=()=>{
  delDigestLanMuList(digestLanMuData)
  let arr=[
    {
    check:'true',
    pKey:'',
    key:targetKeys.value.join(','),
    name:'',
    nameNew:'',
    width:'',
    align:''
  }]
  // 调整数据库 列参数
  digestLanMuData.objects = JSON.stringify(arr)
  saveLanMuList(digestLanMuData).then(res => {
    message.success("数据库同步成功！")
    resetDynamicColumnData()
  })
}
// 取消
const transferCancel = () => {
  targetKeys.value = []
  selectedKeys.value = []
}

function digestDynamicColumnData() {
  findDbLanMuList(digestLanMuData).then(res => {
    // 栏目列
    let dbList = res.items
    let arr = []
    if (dbList.length > 0) {
      for (let i = 0; i < dbList[0].key.split(',').length; i++) {
        arr.push(dbList[0].key.split(',')[i])
      }
    }
    targetKeys.value=arr
  })
}

const openQueryPage = () => {
  val.openOne = 0
  openQueryPageM(true, {
    data: val
  })
}

// 页面函数区
const onSelectChange = (selectedRowKeys, obj) => {
  tableSelectedRowKeys.value = selectedRowKeys;
  tableSelectedRowObjs.value = obj;
};
const pageReload = () => {
  pageParameter.digestStyle=targetKeys.value.length>0?targetKeys.value.join(','):''
  reload({
    searchInfo: pageParameter
  })
}

const onChangeSwitch = async (v) => { // 动态列
  styleName.value = showStyle.value.filter(o => o.value === v)[0].name
  pageParameter.queryMark = v
  resetDynamicColumnData()
  pageParameter.searchConditon.requirement = 'bprogerty'
  pageParameter.searchConditon.value = ''
}
const pageSearch = async () => {
  // 搜索前校验格式
  if ('' == pageParameter.searchConditon.requirement.trim()) {
    message.warn('请选择检索条件')
    return false
  }
  // 校验完成后搜索
  closeFilterV()
  pageReload()
}

const filterSearch = async () => {
  /* if (''==pageParameter.searchConditon.value.trim())return false*/
  // 搜索前校验格式
  let min = pageParameter.filterConditon.amountMinJf.trim()
  let max = pageParameter.filterConditon.amountMaxJf.trim()
  if (max != '' || min != '') {
    if (min != '' && max != '' && (!isRealNum(min) || !isRealNum(max) || parseFloat(min) == 0 || parseFloat(max) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(min) > parseFloat(max)) {
      message.warn('金额区间最小值部门大于最大值！')
      closeFilterV()
      return false
    }
  }

  let min2 = pageParameter.filterConditon.amountMinDf.trim()
  let max2 = pageParameter.filterConditon.amountMaxDf.trim()
  if (max2 != '' || min2 != '') {
    if (min2 != '' && max2 != '' && (!isRealNum(min2) || !isRealNum(max2) || parseFloat(min2) == 0 || parseFloat(max2) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(min2) > parseFloat(max2)) {
      message.warn('金额区间最小值部门大于最大值！')
      closeFilterV()
      return false
    }
  }

  let min3 = pageParameter.filterConditon.amountMinYe.trim()
  let max3 = pageParameter.filterConditon.amountMaxYe.trim()
  if (max != '' || min3 != '') {
    if (min3 != '' && max3 != '' && (!isRealNum(min3) || !isRealNum(max3) || parseFloat(min3) == 0 || parseFloat(max3) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(min3) > parseFloat(max3)) {
      message.warn('金额区间最小值部门大于最大值！')
      closeFilterV()
      return false
    }
  }

  // 校验完成后搜索
  pageReload()
}

const closeFilterV = () => {
  pageParameter.filterConditon.amountMinDf = ''
  pageParameter.filterConditon.amountMaxDf = ''
  pageParameter.filterConditon.amountMinJf = ''
  pageParameter.filterConditon.amountMaxJf = ''
  pageParameter.filterConditon.amountMinYe = ''
  pageParameter.filterConditon.amountMaxYe = ''
  visible.value = false
  reloadColumns()
}

function isRealNum(val) {
  // isNaN()函数 把空串 空格 以及NUll 按照0来处理 所以先去除，
  if (val === "" || val == null) {
    return false;
  }
  if (!isNaN(val)) {
    //对于空数组和只有一个数值成员的数组或全是数字组成的字符串，isNaN返回false，例如：'123'、[]、[2]、['123'],isNaN返回false,   //所以如果不需要val包含这些特殊情况，则这个判断改写为if(!isNaN(val) && typeof val === 'number' )
    return true;
  } else {
    return false;
  }
}

/*start栏目设置*/
const dynamicColumns = initDynamics().DEFAULT
const dynamicColumnData = ref([])
let dynamicColumnDataCopy = []
const editableData = reactive({});

const confirm = (e: MouseEvent) => {
  // 询问
  createConfirm({
    iconType: 'warning',
    title: '栏目同步',
    content: '是否将刚才设置同步数据库!',
    onOk: async () => {
      // 调整数据库 列参数
      lanMuData.accId = accId.value
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

  //对子节点处理  过滤有子节点并且变动 添加到a
  lanMuList.forEach((item, index) => {
    if (item.children) {
      let b = item.children.filter(item2 => {
        try {
          copyList[index].children.forEach(item3 => {
            if (item2.key === item3.key && item2.name == item3.name) {
              if (item2.nameNew != item3.nameNew || item2.width != item3.width || item2.check != item3.check || item2.align != item3.align)
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
      b.forEach(item => {
        a.push(item);
      })
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
  lanMuData.accId = accId.value
  lanMuData.type = pageParameter.queryMark
  findDbLanMuList(lanMuData).then(res => {
    // 栏目列
    let dbList = res.items
    if (dbList.length > 0) {
      let statiList = initDynamics()[pageParameter.queryMark]
      dbList = combineParameters(statiList, dbList)
      dynamicColumnData.value = dbList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(dbList))
    } else {
      let statiList = initDynamics()[pageParameter.queryMark]
      dynamicColumnData.value = statiList
      dynamicColumnDataCopy = JSON.parse(JSON.stringify(statiList))
    }
    // 表格列
    reloadColumns()
    digestDynamicColumnData()
    pageReload()
  })
}

function combineParameters(staticList: any, dbList: any) {
  staticList.forEach(item => {
    dbList.forEach(item2 => {
      if (item.key === item2.key && item.name === item2.name) {
        item.nameNew = item2.nameNew
        item.width = parseInt(item2.width)
        item.check = item2.check == 'true'
        item.align = item2.align
      } else {
        //对子节点处理
        if (item.children) {
          item.children.forEach(item3 => {
            if (item3.key === item2.key && item3.name === item2.name) {
              item3.nameNew = item2.nameNew
              item3.width = parseInt(item2.width)
              item3.check = item2.check == 'true'
              item3.align = item2.align
            }
          })
        }
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

const reloadColumns = () => {
  let a = []
  a = getColumns()
  let newA = JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark)))
  newA = assemblyDynamicColumn(dynamicColumnData.value, newA)
  setColumns(newA)
  initTableWidth(newA)
  //多表头 添加子节点筛选
  let seachList = []
  newA.forEach(item => {
    if (item.children) {
      item.children.forEach(item2 => {
        item2.title = item.title + item2.title
        seachList.push(item2)
      })
    } else {
      seachList.push(item)
    }
  })
  searchConditonList.value = seachList
}

function initTableWidth(thisCs) {
  let total = 60
  thisCs.forEach(item => {
    if (item.ifShow == null || item.ifShow)
      total += parseInt(item.width)
  })
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

// 格式化  Tue Jun 08 2021 16:57:19 GMT+0800 (中国标准时间)
function timeformat(dateData) {
  let date = new Date(dateData);
  let y = date.getFullYear();
  let m = date.getMonth() + 1;
  m = m < 10 ? '0' + m : m;
  let d = date.getDate();
  d = d < 10 ? '0' + d : d;
  return y + '-' + m + '-' + d;
}

/*栏目设置end*/
const loadPage = async (data) => {
  // console.log(JSON.stringify(data)+'>>>>传值')

  // 参数规则
  let ParameteRule= await useRouteApi(finByParameterAccuracy,{schemaName:database})('')
  pageParameter.parameteraccuracyJSON = JSON.stringify(ParameteRule)

  // 回显筛选条件
  loadMark.value = false
  kmList.value = data.kmList
  pageParameter.km = data.minKm
  pageParameter.strDate = data.strDate.replaceAll("-", "")
  pageParameter.endDate = data.endDate.replaceAll("-", "")
  pageParameter.showRulesSize = data.fontSize
  bzName.value = data.bzName
  pageParameter.ishaveRjz = data.ishaveRjz
  pageParameter.minJc = data.minJc
  pageParameter.maxJc = data.maxJc
  pageParameter.bend = data.jc
  pageParameter.bz = data.bzName
  pageParameter.styleValue = data.styleValue
  pageParameter.timflg = data.timflg

  pageParameter.database = data.pageParameter.database
  accId.value=data.pageParameter.accId
  database.value=data.pageParameter.database

  // 查询是否独立账套  是否独立账套（1）反之集团账套（0）
  const datainfo = await findByAccId(accId.value);
  bwb.value = datainfo.currencyCh

  if (data.jc === '1') {
    pageParameter.km = data.minKm === undefined ? data.kmList[0].ccode : data.minKm; // 起始科目
  } else {
    pageParameter.km = data.kmList[0].ccode;
  }

  let findBuccodeName = data.kmList.filter(c => c.ccode === pageParameter.km)[0].ccodeName
  titleName.value = findBuccodeName
  time.strDate = data.strDate.replaceAll("-", ".")
  time.endDate = data.endDate.replaceAll("-", ".")

  // 外币金额式
  if(data.bzName!=='全部'){ pageParameter.queryMark = 'WJ';styleName.value='外币金额式' }

  logmap.value.text =
    useUserStoreWidthOut().getUserInfo.username +
    '进行科目日记账查询。查询条件【开始期间：' +
    pageParameter.strDate +
    ',结束期间：' +
    pageParameter.endDate +
    ',开始级次：' +
    pageParameter.minJc +
    ',结束级次：' +
    pageParameter.maxJc +
    ',开始科目：' +
    pageParameter.minKm +
    ',结束科目：' +
    pageParameter.minKm +
    ',是否末级：' +
    pageParameter.bend +
    ',是否包含未记账：' +
    pageParameter.ishaveRjz +
    ',币种类型：' +
    pageParameter.bend +
    '】';
  // 添加操作日志
  await savelog(logmap.value);
  if (data.openOne == 1) { // 第一次初始化 + 条件查询
    resetDynamicColumnData()
  } else { // 查询条件查询
    closeFilterV()
    pageParameter.searchConditon.value = ''
    pageReload()
  }
}

async function handleChangeMinKm() {
  titleName.value = kmList.value.filter(o => o.ccode === pageParameter.km)[0].ccodeName
  const dw = kmList.value.filter(o => o.ccode === pageParameter.km)[0].menterage // 单位
  const wb = kmList.value.filter(o => o.ccode === pageParameter.km)[0].currencyType //美元
  pageParameter.dw = dw
  pageParameter.wb = wb

  const test = kmList.value.filter((km) => km.ccode === pageParameter.km);
  if (test.length > 0) {
    if (test.bnum === '1' && test.currency === '1') {
      pageParameter.queryMark = 'SWJ';
    } else if (test.bnum === '1') {
      pageParameter.queryMark = 'SJ';
    } else if (test.currency === '1') {
      pageParameter.queryMark = 'WJ';
    } else {
      pageParameter.queryMark = 'J'
    }
  }
  styleName.value = showStyle.value.filter(o => o.value === pageParameter.queryMark)[0].name

  closeFilterV()
  pageReload()
}

// 金额格式化
function moneyformat(data: any) {
  let str = ""
  if(data){
    // 千分位保留2位小数
    var source = String(data.toFixed(pageParameter.moneyJd)).split("."); //按小数点分成2部分
    source[0] = source[0].replace( new RegExp('(\\d)(?=(\\d{3})+$)' , 'ig'), "$1,");//只将整数部分进行都好分割
    str = source.join("." );//再将小数部分合并进来
  }
  return str;
}
// 数量格式化
function numformat(data:any){
  let str = ""
  if(data){
    if(0 === data){
      str = ""
    }else {
      var source = String(data.toFixed(pageParameter.numJd)).split("."); //按小数点分成2部分
      source[0] = source[0].replace( new RegExp('(\\d)(?=(\\d{3})+$)' , 'ig'), "$1,");//只将整数部分进行都好分割
      str = source.join("." );
    }
  }
  return str;
}

const dynamicAdReload = async (obj) =>{
  // 先获取部门组件查看是否存在部门信息
  pageParameter.page= getPaginationRef().current
  pageParameter.size= getPaginationRef().pageSize
  if(pageParameter.strDate!==''){
    let res  =  await useRouteApi(findAllDayBook,{schemaName: obj.accId+'-'+obj.year})(pageParameter)
    if (res != null && res.total> 0){
      loadMark.value = true
      setTableData([]) // 清空可能残留的数据
      setTableData(res.items)
      // 底部分页信息
      database.value = obj.accId+'-'+obj.year
      pageParameter.thisAdInfo = {}
      pageParameter.thisAdInfo = obj
      pageParameter.total = res.total
      setPagination({total:res.total})
      // 会计科目
      // kmList.value=await useRouteApi(findCodeKmByPeriod,{schemaName:s})({ strDate: endDate.value, endDate: 'test', accId: database.value,userId:userStore.getUserInfo.id });
    }
    else {
      createWarningModal({title: '温馨提示',content: '暂无任何数据！'});
      pageParameter.thisAdInfo = {}
      pageParameter.total = -1
    }
  }
  loadMark.value = false
}
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
<style src="../../../../assets/styles/global-container-index.less" lang="less" scoped></style>
<style lang="less" scoped>
.app-container {
  height: 900px;
}

:deep(.ant-table-scroll) {
  overflow: hidden;
}

:deep(.ant-table-header), :deep(ant-table-body) {
  .ant-table-thead th {
    text-align: center;
    border-right: 1px solid #f0f0f0;

    .ant-table-column-title {
      color: black !important;
      font-weight: bold !important;
    }
  }
}

:deep(td) {
  padding: 5px 8px !important;
  border: 1px solid #a7a7a7 !important;
  border-left: none !important;
  border-bottom: none !important;
  color: black !important;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}

:deep(.ant-table-footer) .ant-table-body {
  overflow: hidden !important;
}

:deep(.ant-table-footer) {
  padding: 0;

  tr {
    font-weight: bold !important;
  }
}

.a-table-font-size-16 :deep(td), .a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
}

.a-table-font-size-12 :deep(td), .a-table-font-size-12 :deep(th) {
  font-size: 12px !important;
}

:deep(.table-striped) {
  background-color: azure;
}

:deep(.table-day-striped) {
  background-color: #faf3ef;
}
:deep(.table-month-striped) {
  background-color: honeydew;
}
:deep(.table-year-striped)  {
  background-color: lightyellow;
}
:deep(.table-odd-striped)  {
  background-color: #fafafa;
}
</style>
