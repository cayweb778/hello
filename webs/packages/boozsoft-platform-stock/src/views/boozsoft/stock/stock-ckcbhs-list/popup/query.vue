<template>
  <BasicModal
    width="600px"
    :height="300"
    class="spaceLogo"
    v-bind="$attrs"
    title="入库单查询"
    :scroll="false"
    @ok="handleOk()"
    @cancel="handleClose()"
    :canFullscreen="false"
    :footer="null"
    @visible-change="openOrClose"
    @register="register"
    :loading="modelLoadIng"
    loadingTip="查询参数初始化中"
  >
    <template #title>
      <div style="height: 30px;width: 100%;background-color: #5f375c;color: white;line-height: 30px;">
        <AppstoreOutlined  style="margin: 0 2px"/>
        <span style="font-size: 13px">成本核算</span>
      </div>
    </template>

    <div style="display: inline-flex;justify-content: space-between;width: 100%;">
      <div style="width: calc(100% - 150px);height: 100%;">
        <div style="display: flex;margin-top: 10px;text-align: center;">
          <ProfileTwoTone style="font-size: 30px;color: #0096c7;margin-left: 4em;"/>
          <span style="line-height:30px;font-size: 24px;color: #0096c7;font-weight: bold;">&ensp;自动成本核算</span>
        </div>
        <div class="nc-open-content">
          <div class="open-content-up">
            <div class="border-div">
              <span>业务范围</span>
              <AccountPicker style="text-align: center;" theme="three" @reloadTable="dynamicAdReload"/>
            </div>
            <div class="border-div">
              <span>查询条件</span>
              <div style="margin-left: 72px;">
                <ul>
                  <li style="margin: 2% 0px;">
                    <label style="text-align: left"><font style="color: red">*</font>核算期间：</label>
                    <a-select
                      :allowClear="true"
                      v-model:value="formItems.riqi"
                      style="width: 60%;margin-left: 28px;"
                      ref="select"
                    >
                      <a-select-option v-for="data in rqList" :key="data.id" :value="data.stockYear+'-'+ data.stockMonth">{{ data.stockYear+'-'+ data.stockMonth}}</a-select-option>
                    </a-select>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div style="width: 150px;background-color: #f1f1f1;padding: 10% 4%;height: 250px;">
        <Button style="width: 100px;" shape="round" @click="handleOk"  type="primary">确定</Button>
        <Button style="width: 100px;margin-top: 10px" shape="round" @click="handleClose">放弃</Button>
      </div>
    </div>

    <StockInfiModalPop @throwData="stockAdd" @register="registerStockInfoModalPage" />
    <StockCangKuModalPop @throwData="throwStockCangKuData" @register="registerStockCangKuModalPage" />
    <StockClassModalPop @throwData="stockClassAdd" @register="registerStockClassModalPage" />

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {ref, unref, reactive, onMounted, watch, computed} from 'vue'
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Checkbox as ACheckbox,
  Radio as ARadio,
  Popconfirm as APopconfirm,
  Tabs as ATabs, message, Transfer as ATransfer,Button
} from "ant-design-vue"
import {
  PlusSquareOutlined,
  FileSearchOutlined,
  SearchOutlined,AppstoreOutlined,ProfileTwoTone,
  PicLeftOutlined, PaperClipOutlined
} from '@ant-design/icons-vue';
import {useTabs} from '/@/hooks/web/useTabs';
import router from "/@/router";
import moment, {Moment} from 'moment';

import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";

import {useUserStore} from "/@/store/modules/user";
import {
  hasBlank,
  pointMessage
} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useMessage} from "/@/hooks/web/useMessage";
import Assist from "/@/views/boozsoft/global/popup/accvoucher/HelloAbc.vue";
import {findParameterValue, modifyParameterValue} from "/@/api/task-api/query-bus-api";
import {ObjTool, StrTool} from "/@/api/task-api/tools/universal-tools";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker-STOCK.vue";
import StockInfiModalPop from "/@/views/boozsoft/stock/stock_info/popup/stockInfoModalPop.vue";
import StockCangKuModalPop from '/@/views/boozsoft/stock/stock_cangku/popup/stockCangKuModalPop.vue';
import StockClassModalPop from '/@/views/boozsoft/stock/stock_class/popup/classAllModalPop.vue';

import dayjs from "dayjs";
import {
  findAllStock, findAllStockClass,
} from "/@/api/record/stock/stock_taking";
import {
  findRiqiList
} from "/@/api/record/stock/stock_cost";
import {findAll as sotckCangKuAll} from "/@/api/record/stock/stock-cangku";

//红字（无现金科目，请设置现金科目后再进行查询）
const {closeCurrent} = useTabs(router);
const emit = defineEmits(['register', 'save'])

const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const ACheckboxGroup = ACheckbox.Group
const ATabPane = ATabs.TabPane

const userStore = useUserStore();
const formItems: any = ref({
  type: '1',
  scope: '1',
})
let changeBeforeModel: any = {}
// 会计区间
const dateList: any = ref([])
const quarterList: any = ref([])

// 类别
const typesList: any = ref([])
// 样式
const styleList = [
  {
    'name': '数量式',
    'value': 'J'
  },{
    'name': '数量金额式',
    'value': 'SJ'
  }
]

const typeList = [
  {
    'name': '全部',
    'value': '5'
  },{
    'name': '未审核',
    'value': '0'
  },
  {
    'name': '已审核',
    'value': '1'
  }
]
const endDate = ref<String>("")
const strDate = ref<String>("")
let endDateList: any = ref([])
let strDateList: any = ref([])
let time: any = ref<Moment[]>([]);
// 日期是否可选
let timeselflg: any = ref(true);
// 期间是否可选
let dateselflg: any = ref(false);
let minkmList: any = ref([])
const minKm = ref<string>()
const maxKm = ref<string>()

const busDate = useCompanyOperateStoreWidthOut().getLoginDate
const maxPingzhengQj = ref('')
const ifCrossYear = ref('1')

const ishaveRjz = ref<boolean>(true)
const isShowQjlh = ref<boolean>(false)
const userId = userStore.getUserInfo.id
//查询条件
// 已授权账套列表
const accAuthList = ref([])

const currentDynamicTenant = ref('')
const {createWarningModal} = useMessage();

const targetKeys: any = ref(['subjectNumber', 'direction', 'number', 'amount', 'wbAmount', 'reviewStatus', 'bookStatus', 'preparedMan'])
const selectedKeys: any = ref([])

const targetKeys2: any = ref([])
const startRendering = ref(false)
const selectedKeys2: any = ref([])

const companyOperateStore = useCompanyOperateStoreWidthOut()


const [register, {closeModal, setModalProps}] = useModalInner((data) => {
  // 方式2
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
  setModalProps({minHeight: 400});

})


let count = 0

async function reLifeKmList(dynamicTenant, sameYear) {
  if (hasBlank(dynamicTenant)) return
  let tenantName = dynamicTenant.split('-')[0] + '-' + dynamicTenant.split('-')[1]
}

async function reLifeEtcList(dynamicTenant) {


}

const oldStrYear = ref('')
const stockFlag = ref()

async function handleChangeStrDate() {
  endDate.value = strDate.value
  if (defaultTabsKey.value != '2') { // 年度发生变化
    let theY = strDate.value.split('-')[0]
    if (!hasBlank(oldStrYear.value) && oldStrYear.value != theY) await reLifeKmList(currentDynamicTenant.value, '1')
    oldStrYear.value = theY
  }
  return false;
  if (strDate.value === undefined) {
    endDate.value = '';
    timeselflg.value = false;
  }
  // 开始日期变动  如果开始日期大于结束于日期 则重置结束日期
  if (endDate.value) {
    if (strDate.value > endDate.value) {
      endDate.value = '';
    }
    // 会计科目
    await reLifeKmList(currentDynamicTenant.value, '1')
    timeselflg.value = true;
  }
}


/*********Mr·Ye*********/
const defaultTabsKey = ref('1')
const modelLoadIng = ref(false)

const defaultModel = {
  constant: {
    currency: '',
    tenantId: '',
    companyCode: '',
    queryType: '1',
    showType: '2',
    thisAdInfo: {}
  },
  authority: {codes: [], types: []},
  variable: {
    periodStart: '',
    periodEnd: '',
    dateStart: '',
    dateEnd: '',
    voucherNumberStart: '',
    voucherNumberEnd: '',
    voucherType: '',
    summary: '',
    reviewStatus: '',
    bookStatus: '',
    subjectNumber: '',
    direction: '',
    amountMax: '',
    amountMin: '',
    numberMax: '',
    numberMin: '',
    wbAmountMax: '',
    wbAmountMin: '',
    /*    billNumberStart: '',
        billNumberEnd: '',*/
    preparedMan: '',
    checkMan: '',
    cashierMan: '',
    reviewMan: '',
    bookMan: '',
    accounting: {},
    assists: {}
  }
}
const modelList = reactive({
  '1': {
    constant: {
      currency: '',
      tenantId: '',
      thisAdInfo: {},
      queryType: '1',
      showType: '2',
      companyCode: ''
    },
    authority: {codes: [], types: []},
    variable: {
      periodStart: '',
      periodEnd: '',
      dateStart: '',
      dateEnd: '',
      voucherNumberStart: '',
      voucherNumberEnd: '',
      voucherType: '',
      summary: '',
      reviewStatus: '',
      bookStatus: '',
      subjectNumber: '',
      direction: '',
      amountMax: '',
      amountMin: '',
      numberMax: '',
      numberMin: '',
      wbAmountMax: '',
      wbAmountMin: '',
      /*  billNumberStart: '',
        billNumberEnd: '',*/
      preparedMan: '',
      checkMan: '',
      cashierMan: '',
      reviewMan: '',
      bookMan: '',
      accounting: {},
      assists: {},
    }
  },
})

const openOrClose = (visible: boolean) => {
    defaultTabsKey.value = '1'
}
const tabsChange = (value) => {
  if (value == '1') {
    modelList[defaultTabsKey.value] = defaultModel
  } else if (value == '2') {
    modelList[defaultTabsKey.value] = modelList[value]
  } else if (value == '3') {
    modelList[defaultTabsKey.value] = modelList[value]
  } else if (value == '4') {
    modelList[defaultTabsKey.value] = modelList[value]
  }
  if ((value == '1' || value == '3' || value == '2') && modelList[defaultTabsKey.value].constant.companyCode == '') {
    modelList[defaultTabsKey.value].constant.companyCode = defaultModel.constant.companyCode
    modelList[defaultTabsKey.value].constant.tenantId = defaultModel.constant.tenantId
  }
}


/*********Mr·Ye*********/
let isChanged: boolean = false

async function handleOk() {
  modelLoadIng.value = false
  if(formItems.scope == '1'){
    if ((formItems.value.cwhcode??true) == true){
      message.error('请先选择存货仓库!');
      return
    }
  }
  formItems.value.accId = currentDynamicTenant.value
  formItems.value.iyear = iyear.value
  //启用日期标记
  formItems.value.flg = stockFlag.value
  formItems.value.rkBcheck = rkBcheck.value
  formItems.value.ckBcheck = ckBcheck.value
  formItems.value.hsFlg = hsFlg.value
  emit('save', unref(formItems.value))
  closeModal()
  return true
}

async function handleClose() {
  if (null != formItems.value.openOne && formItems.value.openOne == 1) {
    await closeCurrent()
    router.push('/zhongZhang/home/welcome')
  }
  closeModal()
}

// 查询

let radiovalue = ref('2');
const riqi: any = ref([]);


const dbQueryJson = reactive({ // 后台数据模型
  id: null,
  accountId: '',
  owningMenuName: '库存台账列表',
  owningPlan: '1',
  planPerson: userId,
  filterConditions: '',
  queryConditions: ''
})

const getAssistNameByKey = (key, list) => {
  let arr = list.filter(item => key == item.key)
  return arr.length > 0 ? arr[0].title : '未知'
}


const [registerStockCangKuModalPage, { openModal: openStockCangKuModalPage }] = useModal();
const [registerStockInfoModalPage, {openModal: openStockInfoModalPage}] = useModal();
const [registerStockClassModalPage, {openModal: openStockClassModalPage}] = useModal();

const openCkSelectContent = async (record, type) => {
  //仓库
  openStockCangKuModalPage(true,{
    database:currentDynamicTenant.value,
  })
}
const openChSelectContent = async (record, type) => {
  //存货
  openStockInfoModalPage(true, {
    database: currentDynamicTenant.value,
  })
}
const openChClassSelectContent = async (record, type) => {
  //存货分类
  openStockClassModalPage(true, {
    database: currentDynamicTenant.value,
  })
}

const stockClassAdd = (o) => {
  console.log(o)
  formItems.value.stockClass = o[0].uniqueStockclass
}
const stockAdd = (o) => {
  console.log(o)
  formItems.value.stockNum = o[0].stockNum
}
function throwStockCangKuData(data,ckFlag) {
  console.log(data)
  // 独立仓库
  if (ckFlag === '1') {
    formItems.value.cwhcode = data[0].num
  }else{
// 级别仓库
  }
}

const chList:any = ref([]);
const searchData:any = ref([]);
const ckList: any = ref([]);
const rqList: any = ref([]);
const chcList: any = ref([]);
const qiFlag: any = ref([]);

async function initData() {
  //仓库
  //searchData.value = await useRouteApi(findSearchAll, {schemaName: currentDynamicTenant})('')

  let temp=await useRouteApi(sotckCangKuAll,{schemaName: currentDynamicTenant})({searchConditon: {
      requirement: 'ckNum',
      value: '',
    }})
  ckList.value=[]
  temp.items.forEach(a=>{
    ckList.value.push({cangkuId:a.id,cangkuName:a.ckName})
  })

  //存货
  chList.value = await useRouteApi(findAllStock,{schemaName: currentDynamicTenant})({})
  //核算日期
  rqList.value = await useRouteApi(findRiqiList,{schemaName: currentDynamicTenant})({
    iyear: iyear.value,
     id: id.value
  })
  //启用日期标记
  qiFlag.value = rqList.value.stockFlag
  //存货分类
  chcList.value = await useRouteApi(findAllStockClass,{schemaName: currentDynamicTenant})({})
}
const id: any = ref([]);
const iyear: any = ref('2022');
const rkBcheck = ref('')
const ckBcheck = ref('')
const hsFlg = ref('')
const dynamicAdReload = async (obj) =>{
  currentDynamicTenant.value=obj.accountMode
  iyear.value=obj.year
  formItems.riqi = ''
  console.log(obj)
  id.value = obj.target.id
  rkBcheck.value = obj.target?.kcCgrkCheck
  ckBcheck.value = obj.target?.kcXsckCheck
  hsFlg.value = obj.target?.kcCostAccounting
  initData()
}
</script>
<style lang="less" scoped>

:deep(.ant-checkbox){
  margin-top: -8px;
}
.nc-open-content {
  background-image: url(/@/assets/images/homes/bg-pattern.png);
  background-repeat: no-repeat;
  background-position: 66% 8%;
  background-size: contain;
  position: relative;
  :deep(.ant-select-selector), :deep(.ant-input-affix-wrapper) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    background: none;
  }
  .border-div {
    position: relative;
    border: 1px #a29f9f solid;
    margin: 20px 10px;
    padding: 2.5%;

    > span {
      display: block;
      width: 80px;
      text-align: center;
      background-color: white;
      position: absolute;
      left: 50px;
      top: -10px;
      color: #888888;
      font-size: 12px;
      font-weight: bold;
    }
  }
}
:global(.ant-modal-header) {
  padding: 10px 0 !important;
}
:global(.ant-modal-content) {
  height: 300px;
  scroll: no;
}

:global(.ant-modal-close-x){
  height: 30px !important;
  color: white;
}

</style>
