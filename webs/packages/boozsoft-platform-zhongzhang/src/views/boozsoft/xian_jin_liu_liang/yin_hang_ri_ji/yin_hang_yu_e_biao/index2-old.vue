<template>
  <div class="app-container">
    <div class="app-container-top">
      <div class="app-container-head">
        <!--        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">-->
        <div class="container-head-title" style="padding-left: 40%;text-align: center;">
          <b class="noneSpan">现金银行余额表</b>
          <div style="font-size: 14px;text-align: center;margin-top: 20px;">
          <span style="color: black;font-size: 14px;">
            &emsp;&emsp;期间：<span style="color: black;font-weight: bold">{{
              time.strDate
            }}~{{ time.endDate }}</span>
          </span>
          </div>
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
          <button
            type="button"
            class="ant-btn ant-btn-me"
          ><span>退出</span></button>
        </div>
      </div>
      <div style="clear:both"/>
      <div>
        <div style="margin-top: -20px;">
          <div style="clear:both;display: flow-root;line-height: 25px;">
            <AccountPicker readonly theme="one" @reloadTable="dynamicAdReload"/>
          </div>
          <div
            style="display: inline-block;float: left;margin-top: .8%;font-size: 14px;margin-left: 1%;">
            本位币：<span style="font-size: 16px;font-weight: bold">{{ bwb }} </span>
            &emsp;&emsp;样式：<span style="color: black;font-weight: bold">{{ styleName }}</span>
            <!--            &emsp;&emsp;期间：<span style="color: black;font-weight: bold">{{ time.strDate }}~{{ time.endDate }}</span>-->
            &emsp;&emsp;累计：<span style="color: black;font-weight: bold"><a-checkbox
            v-model:checked="ljchecked" @change="ljchange"/></span>
          </div>

          <div style="float: right; margin-left: 10px">
            <a-button class="ant-btn" @click="closeFilterV(),pageReload()">
              <SyncOutlined :style="{ fontSize: '14px' }"/>
            </a-button>

            <a-popover class="ant-btn-default" placement="bottom">
              <template #content>
                <a-popconfirm
                  ok-text="保存"
                  cancel-text="关闭"
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
              <span class="group-btn-span-special2" @click="onChangeSwitch('J')"
                    :style="pageParameter.queryMark=='J'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;金额式&emsp;&ensp;<CheckOutlined
                v-if="pageParameter.queryMark=='J'"/></span><br/>
                <span class="group-btn-span-special2" @click="onChangeSwitch('SJ')"
                      :style="pageParameter.queryMark=='SJ'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;数量金额式<CheckOutlined
                  v-if="pageParameter.queryMark=='SJ'"/></span><br/>
                <span class="group-btn-span-special2" @click="onChangeSwitch('WJ')"
                      :style="pageParameter.queryMark=='WJ'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;外币金额式<CheckOutlined
                  v-if="pageParameter.queryMark=='WJ'"/></span><br/>
                <span class="group-btn-span-special2" @click="onChangeSwitch('SWJ')"
                      :style="pageParameter.queryMark=='SWJ'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;数量外币式<CheckOutlined
                  v-if="pageParameter.queryMark=='SWJ'"/></span><br/>

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
              <template v-for="item in searchConditonList">
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
    </div>
    <div class="app-container-bottom">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <BasicTable
          ref="tableRef"
          :class="pageParameter.showRulesSize=='MAX'?'a-table-font-size-16':'a-table-font-size-12'"
          :scroll="{ x: totalColumnWidth,y: windowHeight }"
          size="small"
          @register="registerTable"
        >
          <template #ccode="{ record }">
            <span v-if="record.ccodeName!=null">{{ record.ccode }}</span>
            <span v-if="record.ccodeName==null"> {{ record.ccode }}  </span>
          </template>
          <template #ccodeName="{ record }">{{ record.ccodeName }}</template>
          <template #qcMd="{ record }">
          <span v-if="record.qcMd!=0">
            <a style="cursor: pointer;" @click="goRouter(record.ccode)">
             {{ moneyformat(record.qcMd) }}
            </a>
          </span>
          </template>
          <template #qcMc="{ record }">
          <span v-if="record.qcMc!=0">
            <a style="cursor: pointer;" @click="goRouter(record.ccode)">
              {{ moneyformat(record.qcMc) }}
            </a>
          </span>
          </template>
          <template #qcNum="{ record }">
          <span v-if="record.qcNum!=0">
             <a style="cursor: pointer;" @click="goRouter(record.ccode)">
               {{ formatNum(record.qcNum) }}
            </a>
          </span>
          </template>
          <template #qcNfrat="{ record }">
          <span v-if="record.qcNfrat!=0">
             <a style="cursor: pointer;" @click="goRouter(record.ccode)">
               {{ moneyformat(record.qcNfrat) }}
            </a>
          </span>
          </template>

          <template #pzMd="{ record }">
          <span v-if="record.pzMd!=0">
            <a style="cursor: pointer;" @click="goRouter(record.ccode)">
               {{ moneyformat(record.pzMd) }}
            </a>
          </span>
          </template>
          <template #pzMc="{ record }">
          <span v-if="record.pzMc!=0">
            <a style="cursor: pointer;" @click="goRouter(record.ccode)">
               {{ moneyformat(record.pzMc) }}
            </a>
          </span>
          </template>
          <template #pzNum="{ record }">
          <span v-if="record.pzNum!=0">
             <a style="cursor: pointer;" @click="goRouter(record.ccode)">
              {{ formatNum(record.pzNum) }}
            </a>
          </span>
          </template>
          <template #pzNfrat="{ record }">
          <span v-if="record.pzNfrat!=0">
             <a style="cursor: pointer;" @click="goRouter(record.ccode)">
              {{ moneyformat(record.pzNfrat) }}
            </a>
          </span>
          </template>

          <template #ljMd="{ record }">
          <span v-if="record.ljMd!=0">
            <a style="cursor: pointer;" @click="goRouter(record.ccode)">
              {{ moneyformat(record.ljMd) }}
            </a>
          </span>
          </template>
          <template #ljMc="{ record }">
          <span v-if="record.ljMc!=0">
             <a style="cursor: pointer;" @click="goRouter(record.ccode)">
                {{ moneyformat(record.ljMc) }}
            </a>
          </span>
          </template>
          <template #ljNum="{ record }">
          <span v-if="record.ljNum!=0">
             <a style="cursor: pointer;" @click="goRouter(record.ccode)">
               {{ formatNum(record.ljNum) }}
            </a>
          </span>
          </template>
          <template #ljNfrat="{ record }">
          <span v-if="record.ljNfrat!=0">
            <a style="cursor: pointer;" @click="goRouter(record.ccode)">
               {{ moneyformat(record.ljNfrat) }}
            </a>
          </span>
          </template>

          <template #qmMd="{ record }">
          <span v-if="record.qmMd!=0">
             <a style="cursor: pointer;" @click="goRouter(record.ccode)">
               {{ moneyformat(record.qmMd) }}
            </a>
          </span>
          </template>
          <template #qmMc="{ record }">
          <span v-if="record.qmMc!=0">
            <a style="cursor: pointer;" @click="goRouter(record.ccode)">
               {{ moneyformat(record.qmMc) }}
            </a>
          </span>
          </template>
          <template #qmNum="{ record }">
          <span v-if="record.qmNum!=0">
           <a style="cursor: pointer;" @click="goRouter(record.ccode)">
               {{ formatNum(record.qmNum) }}
            </a>
          </span>
          </template>
          <template #qmNfrat="{ record }">
          <span v-if="record.qmNfrat!=0">
            <a style="cursor: pointer;" @click="goRouter(record.ccode)">
               {{ moneyformat(record.qmNfrat) }}
            </a>
          </span>
          </template>

        </BasicTable>
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
              <span>---期初余额--------------------</span>
              <span style="color: black;font-weight: bold">
              期初借方本币：
            </span>
              <div>
                <a-input v-model:value="pageParameter.filterConditon.amountMinQcMd" placeholder=""
                         style="width: 95px;font-size: 10px"/>
                ~
                <a-input v-model:value="pageParameter.filterConditon.amountMaxQcMd" placeholder=""
                         style="width: 95px;font-size: 10px;float: right"/>
              </div>
            </li>
            <li style="margin-top: 5%">
            <span style="color: black;font-weight: bold">
              期初贷方本币：
            </span>
              <div>
                <a-input v-model:value="pageParameter.filterConditon.amountMinQcMc" placeholder=""
                         style="width: 95px;font-size: 10px"/>
                ~
                <a-input v-model:value="pageParameter.filterConditon.amountMaxQcMc" placeholder=""
                         style="width: 95px;font-size: 10px;float: right"/>
              </div>
            </li>
            <li>
              <span>---本期发生--------------------</span>
              <span style="color: black;font-weight: bold">
              本期借方本币：
            </span>
              <div>
                <a-input v-model:value="pageParameter.filterConditon.amountMinPzMd" placeholder=""
                         style="width: 95px;font-size: 10px"/>
                ~
                <a-input v-model:value="pageParameter.filterConditon.amountMaxPzMd" placeholder=""
                         style="width: 95px;font-size: 10px;float: right"/>
              </div>
            </li>
            <li style="margin-top: 5%">
            <span style="color: black;font-weight: bold">
              本期贷方本币：
            </span>
              <div>
                <a-input v-model:value="pageParameter.filterConditon.amountMinPzMc" placeholder=""
                         style="width: 95px;font-size: 10px"/>
                ~
                <a-input v-model:value="pageParameter.filterConditon.amountMaxPzMd" placeholder=""
                         style="width: 95px;font-size: 10px;float: right"/>
              </div>
            </li>
            <li>
              <span>---期末余额--------------------</span>
              <span style="color: black;font-weight: bold">
              期末借方本币：
            </span>
              <div>
                <a-input v-model:value="pageParameter.filterConditon.amountMinQmMd" placeholder=""
                         style="width: 95px;font-size: 10px"/>
                ~
                <a-input v-model:value="pageParameter.filterConditon.amountMaxQmMd" placeholder=""
                         style="width: 95px;font-size: 10px;float: right"/>
              </div>
            </li>
            <li style="margin-top: 5%">
            <span style="color: black;font-weight: bold">
              期末贷方本币：
            </span>
              <div>
                <a-input v-model:value="pageParameter.filterConditon.amountMinQmMc" placeholder=""
                         style="width: 95px;font-size: 10px"/>
                ~
                <a-input v-model:value="pageParameter.filterConditon.amountMaxQmMc" placeholder=""
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
      </PageWrapper>
    </div>
  </div>
</template>
<script setup lang="ts">
import {BasicTable, useTable} from '/@/components/Table'
import {useModal} from '/@/components/Modal'
import {PageWrapper} from '/@/components/Page'

import {
  Divider as ADivider,
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Upload as AUpload,
  Pagination as APagination,
  Popconfirm as APopconfirm,
  Table as ATable,
  Checkbox as ACheckbox,
  message,
  Drawer as ADrawer
} from "ant-design-vue"
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
  PieChartFilled,
  FilterFilled, CheckOutlined, EditOutlined, SearchOutlined,
  OrderedListOutlined
} from '@ant-design/icons-vue'
import {findDbLanMuList, saveLanMuList} from '/@/api/record/system/accvoucher'
import {yueGlStore} from '/@/api/record/km_yue/generalLedger'

import {onMounted, reactive, ref} from "vue";
import {initDynamics, assemblyDynamicColumn} from "./data";
import Query from "./popup/query.vue";
import {cloneDeep} from "lodash-es";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  askTask,
  compareTime,
  findByFunctionModule,
  getCurrentAccountName,
  markAnomaly,
  offsetToStr,
  getThisIndexImg
} from "/@/api/task-api/tast-bus-api";
// import {findAll,} from '/@/api/record/km_yue/data';
import {finByParameterAccuracy} from '/@/api/record/km_mingxi/data';
import {useUserStoreWidthOut} from "/@/store/modules/user";
import router from "/@/router";
import {savelog} from "/@/api/record/log";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findByAccId} from "/@/api/record/system/account";
import {findCodeKmByPeriod} from "/@/api/record/generalLedger/data";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {
  findAccountByBankYue,
  findAccountByBankYueQichu,
  findAccountByBankYueLeiji
} from "/@/api/yin_hang_yu_e/yin_hang_yu_e";

const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const {createConfirm, createWarningModal} = useMessage();
// 全局常量
const glStore = yueGlStore()

// 页面变量
const pageParameter: any = reactive({
  thisAdInfo: {},
  total: 0,
  page: '',
  size: '',
  dynamicTenantId: '',
  openOne: '',
  companyCode: '',
  companyName: '',
  queryMark: 'J',
  showRulesSize: 'MIN',
  condition: {},
  searchConditon: {
    requirement: 'ccode',
    value: '',
  },
  filterConditon: {
    amountMaxQcMd: '',
    amountMinQcMd: '',
    amountMaxQcMc: '',
    amountMinQcMc: '',

    amountMaxPzMd: '',
    amountMinPzMd: '',
    amountMaxPzMc: '',
    amountMinPzMc: '',

    amountMaxQmMd: '',
    amountMinQmMd: '',
    amountMaxQmMc: '',
    amountMinQmMc: '',
  },
  reloadMark: false,
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
  bend: '1',
  jc: '1',
  numJd: '2',  // 数量精度
  priceJd: '2',  // 金额精度
  lvJd: '2',  // 汇率精度
  moneyJd: '2',  // 金额精度
  timflg: 'qj',
  bz: '',
  bzName: '全部',
  styleValue: '',
  styleList: '',
  parameteraccuracyJSON: ''
})

const styleName = ref<String>("金额式")
// 科目名称标题
const titleName = ref<String>("")
// 本位币
const bwb = ref<String>("")
//币种名称
const bzName = ref<String>("")
//显示未记账
const ibook = ref<boolean>(true)
const ljchecked = ref<boolean>(false)
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
  'menuName': '银行余额表',
  'type': '',
  objects: '',
  username: useUserStoreWidthOut().getUserInfo.username
}
// 表格参数
const loadMark = ref(false)
const tableSelectedRowKeys = ref([])
const tableSelectedRowObjs = ref([])
const windowWidth = (document.documentElement.clientWidth - 70)
const windowHeight = (window.innerHeight - 350)
const totalColumnWidth = ref(0)

const tableRef: any = ref(null)
const userName = useUserStoreWidthOut().getUserInfo.username
const database = ref(getCurrentAccountName(true));
// 增加操作日志：1、修改2、删除3、停用、4查看、5导入、6导出、7打印
const logmap = ref({
  accId: database,
  username: useUserStoreWidthOut().getUserInfo.username,
  flag: '4',
  text: '',
});
const CrudApi = {
  list: findAll,
  columns: JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark)))
}

async function findAll() {
  const res = await useRouteApi(findAccountByBankYue, {schemaName: database})(pageParameter)
  const benqiList = res.filter(item => {
    if (pageParameter.km != null && pageParameter.km != '') {
      return item.ccode == pageParameter.km
    }
    return item
  })
  const qichuList = await useRouteApi(findAccountByBankYueQichu, {schemaName: database})(pageParameter)
  const leijiList = await useRouteApi(findAccountByBankYueLeiji, {schemaName: database})(pageParameter)
  const list: any = []
  const sumItem: any = {}
  sumItem.ccode = '合计'
  sumItem.qcNum = '0'
  sumItem.qcNfrat = '0'
  sumItem.qcMd = '0'
  sumItem.qcMc = '0'
  sumItem.pzNum = '0'
  sumItem.pzNfrat = '0'
  sumItem.pzMd = '0'
  sumItem.pzMc = '0'
  sumItem.qmNum = '0'
  sumItem.qmNfrat = '0'
  sumItem.qmMd = '0'
  sumItem.qmMc = '0'
  sumItem.ljNum = '0'
  sumItem.ljNfrat = '0'
  sumItem.ljMd = '0'
  sumItem.ljMc = '0'
  for (let i = 0; i < benqiList.length; i++) {
    const item: any = {}
    const benqi = benqiList[i]
    item.ccode = benqi.ccode
    item.ccodeName = benqi.ccodeName
    item.unitMeasurement = benqi.unitMeasurement
    item.foreignCurrency = benqi.foreignCurrency
    const fangxiang = benqi.bprogerty
    //期初
    for (let j = 0; j < qichuList.length; j++) {
      const qichu = qichuList[j]
      if (benqi.ccode == qichu.ccode) {
        if (item.unitMeasurement == '' || item.unitMeasurement == null) {
          item.unitMeasurement = qichu.unitMeasurement
        }
        if (item.foreignCurrency == '' || item.foreignCurrency == null) {
          item.foreignCurrency = qichu.foreignCurrency
        }
        //根据方向写计算公式
        if (fangxiang == '1') {
          item.qcNum = sub(qichu.ndS, qichu.ncS)
          item.qcNfrat = sub(qichu.nfratMd, qichu.nfratMc)
        } else {
          item.qcNum = sub(qichu.ncS, qichu.ndS)
          item.qcNfrat = sub(qichu.nfratMc, qichu.nfratMd)
        }
        if (qichu.md > qichu.mc) {
          item.qcMd = sub(qichu.md, qichu.mc)
          item.qcMc = '0'
        } else {
          item.qcMd = '0'
          item.qcMc = sub(qichu.mc, qichu.md)
        }
      }
    }
    //本期
    item.pzMd = benqi.md
    item.pzMc = benqi.mc
    //根据方向写计算公式
    if (fangxiang == '1') {
      item.pzNum = sub(benqi.ndS, benqi.ncS)
      item.pzNfrat = sub(benqi.nfratMd, benqi.nfratMc)
    } else {
      item.pzNum = sub(benqi.ncS, benqi.ndS)
      item.pzNfrat = sub(benqi.nfratMc, benqi.nfratMd)
    }
    //期末
    item.qmNum = add(item.qcNum, item.pzNum)
    item.qmNfrat = add(item.qcNfrat, item.pzNfrat)
    item.qmMd = sub(add(item.pzMd, item.qcMd), add(item.pzMc, item.qcMc))
    item.qmMc = sub(add(item.pzMc, item.qcMc), add(item.pzMd, item.qcMd))
    if (item.qmMd > 0) {
      item.qmMc = '0'
    } else {
      item.qmMd = '0'
    }
    //累计
    for (let j = 0; j < leijiList.length; j++) {
      const leiji = leijiList[j]
      if (benqi.ccode == leiji.ccode) {
        if (item.unitMeasurement == '' || item.unitMeasurement == null) {
          item.unitMeasurement = leiji.unitMeasurement
        }
        if (item.foreignCurrency == '' || item.foreignCurrency == null) {
          item.foreignCurrency = leiji.foreignCurrency
        }
        //根据方向写计算公式
        if (fangxiang == '1') {
          item.ljNum = sub(leiji.ndS, leiji.ncS)
          item.ljNfrat = sub(leiji.nfratMd, leiji.nfratMc)
        } else {
          item.ljNum = sub(leiji.ncS, leiji.ndS)
          item.ljNfrat = sub(leiji.nfratMc, leiji.nfratMd)
        }
        if (leiji.md > leiji.mc) {
          item.ljMd = sub(leiji.md, leiji.mc)
          item.ljMc = '0'
        } else {
          item.ljMd = '0'
          item.ljMc = sub(leiji.mc, leiji.md)
        }
      }
    }
    //合计
    sumItem.qcNum = add(sumItem.qcNum, item.qcNum == '' ? 0 : item.qcNum)
    sumItem.qcNfrat = add(sumItem.qcNfrat, item.qcNfrat == '' ? 0 : item.qcNfrat)
    sumItem.qcMd = add(sumItem.qcMd, item.qcMd == '' ? 0 : item.qcMd)
    sumItem.qcMc = add(sumItem.qcMc, item.qcMc == '' ? 0 : item.qcMc)
    sumItem.pzNum = add(sumItem.pzNum, item.pzNum == '' ? 0 : item.pzNum)
    sumItem.pzNfrat = add(sumItem.pzNfrat, item.pzNfrat == '' ? 0 : item.pzNfrat)
    sumItem.pzMd = add(sumItem.pzMd, item.pzMd == '' ? 0 : item.pzMd)
    sumItem.pzMc = add(sumItem.pzMc, item.pzMc == '' ? 0 : item.pzMc)
    sumItem.qmNum = add(sumItem.qmNum, item.qmNum == '' ? 0 : item.qmNum)
    sumItem.qmNfrat = add(sumItem.qmNfrat, item.qmNfrat == '' ? 0 : item.qmNfrat)
    sumItem.qmMd = add(sumItem.qmMd, item.qmMd == '' ? 0 : item.qmMd)
    sumItem.qmMc = add(sumItem.qmMc, item.qmMc == '' ? 0 : item.qmMc)
    sumItem.ljNum = add(sumItem.ljNum, item.ljNum == '' ? 0 : item.ljNum)
    sumItem.ljNfrat = add(sumItem.ljNfrat, item.ljNfrat == '' ? 0 : item.ljNfrat)
    sumItem.ljMd = add(sumItem.ljMd, item.ljMd == '' ? 0 : item.ljMd)
    sumItem.ljMc = add(sumItem.ljMc, item.ljMc == '' ? 0 : item.ljMc)
    list.push(item)
  }
  list.push(sumItem)
  return list
}

// 组件实例区
const [registerTable, {
  reload,
  setTableData,
  setColumns,
  getColumns,
  getPaginationRef,
  setPagination
}] = useTable({
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
const [registerQueryPage, {openModal: openQueryPageM}] = useModal()
const [registerReviewPage, {openModal: openReviewPageM}] = useModal()

// 跳转科目明细账
async function goRouter(ccode) {
  pageParameter.km = ccode
  pageParameter.bzName = pageParameter.bz
  pageParameter.openOne = '2'
  pageParameter.dynamicTenantId = database.value
  pageParameter.strDate = time.strDate
  pageParameter.endDate = time.endDate
  router.push({
    path: '/account-book/ab-kemuzhang/abk-mxtable',
    query: pageParameter,
  });
}

// 累计列是否显示
function ljchange(a) {
  ljchecked.value = a.target.checked
  reloadColumns()
}

// 金额格式化
function moneyformat(data: any) {
  let str = ""
  if (data) {
    // 千分位保留2位小数
    var source = String(data.toFixed(pageParameter.moneyJd)).split("."); //按小数点分成2部分
    source[0] = source[0].replace(new RegExp('(\\d)(?=(\\d{3})+$)', 'ig'), "$1,");//只将整数部分进行都好分割
    str = source.join(".");//再将小数部分合并进来
  }
  return str;
}

// 数量格式化
function formatNum(data: any) {
  let str = ""
  if (data) {
    if (0 === data) {
      str = ""
    } else {
      var source = String(data.toFixed(pageParameter.numJd)).split("."); //按小数点分成2部分
      source[0] = source[0].replace(new RegExp('(\\d)(?=(\\d{3})+$)', 'ig'), "$1,");//只将整数部分进行都好分割
      str = source.join(".");
    }
  }
  return str;
}

onMounted(async () => {
  val.openOne = 1
  openQueryPageM(true, {
    data: val
  })
  loadMark.value = true
  // 查询是否独立账套  是否独立账套（1）反之集团账套（0）
  const datainfo = await findByAccId(database.value.substring(0, database.value.length - 5));
  bwb.value = datainfo.currencyCh
})

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
  reload({
    searchInfo: pageParameter
  })
}

const onChangeSwitch = async (v) => { // 动态列
  styleName.value = showStyle.value.filter(o => o.value === v)[0].name
  pageParameter.queryMark = v
  resetDynamicColumnData()
  pageParameter.searchConditon.requirement = 'ccode'
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
  // 期初
  let qcmdmin = pageParameter.filterConditon.amountMinQcMd.trim()
  let qcmdmax = pageParameter.filterConditon.amountMaxQcMd.trim()
  if (qcmdmax != '' || qcmdmin != '') {
    if (qcmdmin != '' && qcmdmax != '' && (!isRealNum(qcmdmin) || !isRealNum(qcmdmax) || parseFloat(qcmdmin) == 0 || parseFloat(qcmdmax) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(qcmdmin) > parseFloat(qcmdmax)) {
      message.warn('金额区间最小值不能大于最大值！')
      closeFilterV()
      return false
    }
  }

  let qcmcmin = pageParameter.filterConditon.amountMinQcMc.trim()
  let qcmcmax = pageParameter.filterConditon.amountMaxQcMc.trim()
  if (qcmcmax != '' || qcmcmin != '') {
    if (qcmcmin != '' && qcmcmax != '' && (!isRealNum(qcmcmin) || !isRealNum(qcmcmax) || parseFloat(qcmcmin) == 0 || parseFloat(qcmcmax) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(qcmcmin) > parseFloat(qcmcmax)) {
      message.warn('金额区间最小值不能大于最大值！')
      closeFilterV()
      return false
    }
  }
  // 凭证
  let pzmdmin = pageParameter.filterConditon.amountMinPzMd.trim()
  let pzmdmax = pageParameter.filterConditon.amountMaxPzMd.trim()
  if (pzmdmax != '' || pzmdmin != '') {
    if (pzmdmin != '' && pzmdmax != '' && (!isRealNum(pzmdmin) || !isRealNum(pzmdmax) || parseFloat(pzmdmin) == 0 || parseFloat(pzmdmax) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(pzmdmin) > parseFloat(pzmdmax)) {
      message.warn('金额区间最小值不能大于最大值！')
      closeFilterV()
      return false
    }
  }
  let pzmcmin = pageParameter.filterConditon.amountMinPzMc.trim()
  let pzmcmax = pageParameter.filterConditon.amountMaxPzMc.trim()
  if (pzmcmax != '' || pzmcmin != '') {
    if (pzmcmin != '' && pzmcmax != '' && (!isRealNum(pzmcmin) || !isRealNum(pzmcmax) || parseFloat(pzmcmin) == 0 || parseFloat(pzmcmax) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(pzmcmin) > parseFloat(pzmcmax)) {
      message.warn('金额区间最小值不能大于最大值！')
      closeFilterV()
      return false
    }
  }
  // 期末
  let qmmdmin = pageParameter.filterConditon.amountMinQmMd.trim()
  let qmmdmax = pageParameter.filterConditon.amountMaxQmMd.trim()
  if (qmmdmax != '' || qmmdmin != '') {
    if (qmmdmin != '' && qmmdmax != '' && (!isRealNum(qmmdmin) || !isRealNum(qmmdmax) || parseFloat(qmmdmin) == 0 || parseFloat(qmmdmax) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(qmmdmin) > parseFloat(qmmdmax)) {
      message.warn('金额区间最小值不能大于最大值！')
      closeFilterV()
      return false
    }
  }
  let qmmcmin = pageParameter.filterConditon.amountMinQmMc.trim()
  let qmmcmax = pageParameter.filterConditon.amountMaxQmMc.trim()
  if (qmmcmax != '' || qmmcmin != '') {
    if (qmmcmin != '' && qmmcmax != '' && (!isRealNum(qmmcmin) || !isRealNum(qmmcmax) || parseFloat(qmmcmin) == 0 || parseFloat(qmmcmax) == 0)) {
      message.warn('请输入数值类型值并且值不能为0！')
      closeFilterV()
      return false
    } else if (parseFloat(qmmcmin) > parseFloat(qmmcmax)) {
      message.warn('金额区间最小值不能大于最大值！')
      closeFilterV()
      return false
    }
  }

  // 校验完成后搜索
  pageReload()
}

const closeFilterV = () => {
  pageParameter.filterConditon.amountMaxQcMd = ''
  pageParameter.filterConditon.amountMinQcMd = ''
  pageParameter.filterConditon.amountMaxQcMc = ''
  pageParameter.filterConditon.amountMinQcMc = ''

  pageParameter.filterConditon.amountMaxPzMd = ''
  pageParameter.filterConditon.amountMinPzMd = ''
  pageParameter.filterConditon.amountMaxPzMc = ''
  pageParameter.filterConditon.amountMinPzMc = ''

  pageParameter.filterConditon.amountMaxQmMd = ''
  pageParameter.filterConditon.amountMinQmMd = ''
  pageParameter.filterConditon.amountMaxQmMc = ''
  pageParameter.filterConditon.amountMinQmMc = ''

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
const dynamicColumnData: any = ref([])
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
      lanMuData.accId = database.value.substring(0, database.value.length - 5)
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
    } catch (e: any) {
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
        } catch (e: any) {
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


async function resetDynamicColumnData() {
  // 先从数据查询是否已经设置
  lanMuData.accId = database.value.substring(0, database.value.length - 5)
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

const reloadColumns = () => {
  let a: any = []
  a = getColumns()
  let newA = JSON.parse(JSON.stringify(glStore.getColumns(pageParameter.queryMark, ljchecked.value)))
  newA = assemblyDynamicColumn(dynamicColumnData.value, newA)
  setColumns(newA)
  initTableWidth(newA)
  //多表头 添加子节点筛选
  let seachList: any = []
  newA.forEach(item => {
    if (item.children) {
      item.children.forEach((item2: any) => {
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
    tableRef.value.$el.style.setProperty('width', (windowWidth + 70 - f) + 'px')
  } else {
    if (visible.value && (windowWidth - 260) < total) total -= (total - (windowWidth - 260))
    totalColumnWidth.value = total
    tableRef.value.$el.style.setProperty('width', (total + 70) + 'px')
  }
}

// 格式化  Tue Jun 08 2021 16:57:19 GMT+0800 (中国标准时间)
function timeformat(dateData) {
  let date = new Date(dateData);
  let y = date.getFullYear();
  let m: any = date.getMonth() + 1;
  m = m < 10 ? '0' + m : m;
  let d: any = date.getDate();
  d = d < 10 ? '0' + d : d;
  return y + '-' + m + '-' + d;
}

/*栏目设置end*/
const loadPage = async (data) => {
  // 回显筛选条件
  loadMark.value = false
  // 参数规则
  database.value = data.dynamicTenantId
  let ParameteRule = await useRouteApi(finByParameterAccuracy, {schemaName: database})('')
  if (ParameteRule.length > 0) {
    for (let i = 0; i < ParameteRule.length; i++) {
      if (ParameteRule[i].paraName === '汇率') {
        pageParameter.lvJd = ParameteRule[i].decimalPlaces
      }
      if (ParameteRule[i].paraName === '单价') {
        pageParameter.priceJd = ParameteRule[i].decimalPlaces
      }
      if (ParameteRule[i].paraName === '数量') {
        pageParameter.numJd = ParameteRule[i].decimalPlaces
      }
      if (ParameteRule[i].paraName === '金额') {
        pageParameter.moneyJd = ParameteRule[i].decimalPlaces
      }
    }
  }

  // ++++++++++++ 复写公司代码 +++++++++++++++++++
  pageParameter.companyCode = data.pageParameter.companyCode
  pageParameter.companyName = data.pageParameter.companyName
  // ++++++++++++ 复写公司代码 +++++++++++++++++++

  pageParameter.minKm = data.minKm === undefined ? '空' : data.minKm
  pageParameter.maxKm = data.maxKm === undefined ? '空' : data.maxKm
  pageParameter.km = data.km
  pageParameter.strDate = data.strDate.replace("-", "")
  pageParameter.endDate = data.endDate.replace("-", "")
  pageParameter.showRulesSize = data.fontSize
  bzName.value = data.bzName
  pageParameter.ishaveRjz = data.ishaveRjz
  pageParameter.minJc = data.minJc
  pageParameter.maxJc = data.maxJc
  pageParameter.bend = data.jc
  pageParameter.bz = data.bzName
  pageParameter.styleValue = data.styleValue
  pageParameter.styleList = JSON.stringify(data.styleList)
  time.strDate = data.strDate.replace("-", ".")
  time.endDate = data.endDate.replace("-", ".")
  time.endDate = data.endDate.replace("-", ".")

  // 外币金额式
  if (data.bzName !== '全部') {
    pageParameter.queryMark = 'WJ';
    styleName.value = '外币金额式'
  }

  logmap.value.text =
    useUserStoreWidthOut().getUserInfo.username +
    '进行科目余额表查询。查询条件【开始期间：' +
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
  // console.log(JSON.stringify(pageParameter)+'>>>>结束')
  if (data.openOne == 1) { // 第一次初始化 + 条件查询
    resetDynamicColumnData()
  } else { // 查询条件查询
    closeFilterV()
    pageParameter.searchConditon.value = ''
    pageReload()
  }
}
const dynamicAdReload = async (obj) => {
  // 先获取部门组件查看是否存在部门信息
  pageParameter.page = getPaginationRef().current
  pageParameter.size = getPaginationRef().pageSize
  console.log(pageParameter)
  if (pageParameter.strDate !== '') {
    database.value = obj.accountMode
    pageReload()
    // let res  =  await useRouteApi(findAll,{schemaName: obj.accId+'-'+obj.year})(pageParameter)
    // if (res != null && res.total> 0){
    loadMark.value = true
    // setTableData([]) // 清空可能残留的数据
    // setTableData(res.items)
    // 底部分页信息
    // database.value = obj.accId+'-'+obj.year
    pageParameter.thisAdInfo = {}
    pageParameter.thisAdInfo = obj
    // pageParameter.total = res.total
    // setPagination({total:res.total})
    // 会计科目
    // kmList.value=await useRouteApi(findCodeKmByPeriod,{schemaName:s})({ strDate: endDate.value, endDate: 'test', accId: database.value,userId:userStore.getUserInfo.id });
    // }
    // else {
    //   createWarningModal({title: '温馨提示',content: '暂无任何数据！'});
    //   pageParameter.thisAdInfo = {}
    //   pageParameter.total = -1
    // }
  }
  loadMark.value = false
}

//加
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
  return e = Math.pow(10, Math.max(c, d)), (a * e + b * e) / e;
}

//减
function sub(a, b) {
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
  return e = Math.pow(10, Math.max(c, d)), (a * e - b * e) / e;
}
</script>
<style src="../../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style src="../../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>
<style scoped>
.vben-basic-table {
  /*width:99%*/
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

.app-container {
  padding: 0px;
  margin: 10px 10px 5px;
}

.group-btn-span-special2 {
  width: 130px;
}
</style>
