<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="凭证查询"
    @ok="handleOk()"
    :loading="loading"
    @cancel="handleClose()"
    @visible-change="openOrClose"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">
        <div class="ocup-position"> 系统方案 </div>
        <div class="ocup-position"> 个人方案 </div>
        <a-tabs v-model:activeKey="activeKey" tabPosition="left" type="card" @change="tabChange">
          <a-tab-pane key="" tab="标准模式">
            <ul>
              <li>
                <div class="special-border-div">
                  <span style="color: #5a5a5a">业务范围</span>
                  <AccountPicker theme="one" @reloadTable="dynamicAdReload" style="display: block;text-align: center;"/>
                </div>
              </li>
              <li>
                <div class="special-border-div">
                  <span style="color: #5a5a5a">查询日期</span>
                  <div>
                    <span><span style="color: red">*</span>期&emsp;&emsp;间：</span>
                    <a-select
                      v-model:value="strDate"
                      show-search
                      :disabled="dateselflg"
                      style="width: 200px"
                      @focus="focusStrDate"
                      @change="handleChangeStrDate"
                    >
                      <a-select-option
                        v-for="item in strDateList"
                        :key="item.id"
                        :value="item.value"
                      >
                        {{ item.value }}
                      </a-select-option>
                    </a-select>
                    <span>&emsp;~&emsp;</span>
                    <a-select
                      v-model:value="endDate"
                      show-search
                      :disabled="dateselflg"
                      style="width: 200px"
                      @focus="focusEndDate"
                      @change="handleChangeEndDate"
                    >
                      <a-select-option
                        v-for="item in endDateList"
                        :key="item.id"
                        :value="item.value"
                      >
                        {{ item.value }}
                      </a-select-option>
                    </a-select>
                  </div>
                </div>
              </li>
              <li>
                <div class="special-border-div">
                  <span style="color: #5a5a5a">查询条件</span>
                  <div style="display: inline-block;width: 96%;margin-left: 23px;">
                    <div style="width: 100%;float: left;">
                      <span>凭证编号：</span>
                      <a-input v-model:value="modelList['1'].variable.voucherNumberStart" :allowClear="true" placeholder="" style="width: 200px"/>
                      <span>&emsp;~&emsp;</span>
                      <a-input v-model:value="modelList['1'].variable.voucherNumberEnd" :allowClear="true" placeholder="" style="width: 200px"/>
                    </div>

                    <div style="width: 100%;float: left;">
                      <span>凭证类别：</span>
                      <a-select
                        v-model:value="modelList['1'].variable.voucherType"
                        placeholder=""
                        style="width: 200px"
                        :allowClear="true">
                        <a-select-option value="记">记</a-select-option>
                        <a-select-option value="转">转</a-select-option>
                      </a-select>
                      <span>&emsp;制 单 人：</span>
                      <a-select
                        :allowClear="true"
                        v-model:value="modelList['1'].variable.preparedMan"
                        placeholder=""
                        style="width: 200px"
                      >
                        <a-select-option value="un">未审核</a-select-option>
                        <a-select-option value="ok">已审核</a-select-option>
                        <a-select-option value="to">待审核</a-select-option>
                      </a-select>
                    </div>

                    <div style="width: 100%;float: left;">
                      <span>凭证摘要：</span>
                      <a-input v-model:value="modelList['1'].variable.summary" :allowClear="true"  placeholder="摘要" style="width: 400px"/>
                    </div>

                    <div style="width: 100%;float: left;">
                      <span>分配状态：</span>
                      <a-select
                        :allowClear="true"
                        v-model:value="modelList['1'].variable.preparedMan"
                        placeholder=""
                        style="width: 200px"
                      >
                        <a-select-option value="1">已分配</a-select-option>
                        <a-select-option value="0">未分配</a-select-option>
                      </a-select>

                      <span>&emsp;流量项目：</span>
                      <a-select
                        :allowClear="true"
                        v-model:value="modelList['1'].variable.preparedMan"
                        placeholder=""
                        style="width: 200px"
                      >
                        <a-select-option value="un">未审核</a-select-option>
                        <a-select-option value="ok">已审核</a-select-option>
                        <a-select-option value="to">待审核</a-select-option>
                      </a-select>
                    </div>


                    <div style="width: 100%;float: left;">
                      <span>凭证摘要：</span>
                      <a-input v-model:value="modelList['1'].variable.summary" :allowClear="true"  placeholder="摘要" style="width: 400px"/>

                      <a-checkbox v-model:checked="ishaveRjz" style="text-align: right;width: 200px;display: flex;">
                        包含未记账
                      </a-checkbox>
                    </div>

                    </div>
                </div>
              </li>
            </ul>
          </a-tab-pane>

          <a-tab-pane v-if="queryPlan!=null" key="1" tab="个人标准模式">
            <ul>
              <li>
                <div class="special-border-div">
                  <span style="color: #5a5a5a">业务范围</span>
                  <AccountPicker theme="one" @reloadTable="dynamicAdReload" style="display: block;text-align: center;"/>
                </div>
              </li>
              <li>
                <div class="special-border-div">
                  <span style="color: #5a5a5a">查询日期</span>
                  <div>
                    <span><span style="color: red">*</span>期&emsp;&emsp;间：</span>
                    <a-select
                      v-model:value="strDate"
                      show-search
                      :disabled="dateselflg"
                      style="width: 200px"
                      @focus="focusStrDate"
                      @change="handleChangeStrDate"
                    >
                      <a-select-option
                        v-for="item in strDateList"
                        :key="item.id"
                        :value="item.value"
                      >
                        {{ item.value }}
                      </a-select-option>
                    </a-select>
                    <span>&emsp;~&emsp;</span>
                    <a-select
                      v-model:value="endDate"
                      show-search
                      :disabled="dateselflg"
                      style="width: 200px"
                      @focus="focusEndDate"
                      @change="handleChangeEndDate"
                    >
                      <a-select-option
                        v-for="item in endDateList"
                        :key="item.id"
                        :value="item.value"
                      >
                        {{ item.value }}
                      </a-select-option>
                    </a-select>
                  </div>
                </div>
              </li>
              <li>
                <div class="special-border-div">
                  <span style="color: #5a5a5a">查询条件</span>
                  <div style="display: inline-block;width: 96%;margin-left: 23px;">
                    <div style="width: 100%;float: left;">
                      <span>凭证编号：</span>
                      <a-input v-model:value="modelList['1'].variable.voucherNumberStart" :allowClear="true" placeholder="" style="width: 200px"/>
                      <span>&emsp;~&emsp;</span>
                      <a-input v-model:value="modelList['1'].variable.voucherNumberEnd" :allowClear="true" placeholder="" style="width: 200px"/>
                    </div>

                    <div style="width: 100%;float: left;">
                      <span>凭证类别：</span>
                      <a-select
                        v-model:value="modelList['1'].variable.voucherType"
                        placeholder=""
                        style="width: 200px"
                        :allowClear="true">
                        <a-select-option value="记">记</a-select-option>
                        <a-select-option value="转">转</a-select-option>
                      </a-select>
                      <span>&emsp;制 单 人：</span>
                      <a-select
                        :allowClear="true"
                        v-model:value="modelList['1'].variable.preparedMan"
                        placeholder=""
                        style="width: 200px"
                      >
                        <a-select-option value="un">未审核</a-select-option>
                        <a-select-option value="ok">已审核</a-select-option>
                        <a-select-option value="to">待审核</a-select-option>
                      </a-select>
                    </div>

                    <div style="width: 100%;float: left;">
                      <span>凭证摘要：</span>
                      <a-input v-model:value="modelList['1'].variable.summary" :allowClear="true"  placeholder="摘要" style="width: 400px"/>
                    </div>

                    <div style="width: 100%;float: left;">
                      <span>分配状态：</span>
                      <a-select
                        :allowClear="true"
                        v-model:value="modelList['1'].variable.preparedMan"
                        placeholder=""
                        style="width: 200px"
                      >
                        <a-select-option value="1">已分配</a-select-option>
                        <a-select-option value="0">未分配</a-select-option>
                      </a-select>

                      <span>&emsp;流量项目：</span>
                      <a-select
                        :allowClear="true"
                        v-model:value="modelList['1'].variable.preparedMan"
                        placeholder=""
                        style="width: 200px"
                      >
                        <a-select-option value="un">未审核</a-select-option>
                        <a-select-option value="ok">已审核</a-select-option>
                        <a-select-option value="to">待审核</a-select-option>
                      </a-select>
                    </div>


                    <div style="width: 100%;float: left;">
                      <span>凭证摘要：</span>
                      <a-input v-model:value="modelList['1'].variable.summary" :allowClear="true"  placeholder="摘要" style="width: 400px"/>

                      <a-checkbox v-model:checked="ishaveRjz" style="text-align: right;width: 200px;display: flex;">
                        包含未记账
                      </a-checkbox>
                    </div>

                  </div>
                </div>
              </li>
            </ul>
          </a-tab-pane>

        </a-tabs>
      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {ref, unref, reactive,  onMounted} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {PageWrapper} from '/@/components/Page'
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Checkbox as ACheckbox,
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Upload as AUpload,
  Table as ATable,
  Popconfirm as APopconfirm,
  Tabs as ATabs, message,
} from "ant-design-vue"
import CheckboxGroup from "ant-design-vue/lib/checkbox/Group";
import {useTabs} from '/@/hooks/web/useTabs';
import router from "/@/router";
import {Moment} from 'moment';
import {
  findPeriodByAccontId,
  findBzAll,
  findMaxJc,
  findCodeKmByPeriod, findMaxPingZhengQiJian, findProjectCategory
} from '/@/api/record/generalLedger/data';
import {findByImgid} from "/@/api/record/eletronicInvoice_data/eletronic_invoice";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {currentCyDatas, findCorpBankAll, getAdInfoDatas,getAllAccAuths} from "/@/api/record/system/financial-settings";
import {useUserStore} from "/@/store/modules/user";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {findByQueryPlan, saveQueryPlan} from "/@/api/query-plan/queryPlan";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {groupStandardAndTemplate} from "/@/api/codekemu/codekemu";
import {findByAccStyle, findByStandardUnique} from "/@/api/accstandard/accstandard";
import {findByAccId} from "/@/api/record/system/account";

//红字（无现金科目，请设置现金科目后再进行查询）
const {closeCurrent} = useTabs(router);
const userStore = useUserStore();
const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const ACheckboxGroup = ACheckbox.Group
const ATabPane = ATabs.TabPane
const data = []
const formItems: any = ref({})
let changeBeforeModel: any = {}
// 会计区间
const dateList: any = ref([])
// 会计科目
const kmList: any = ref([])
const queryPlan: any = ref([]);
// 页面变量
const pageParameter = reactive({
  companyCode: '',
  companyName: '',
})
//币种
const bzList: any = ref([])
// 过滤
const filterGroup: any = ref([])
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
let maxkmList: any = ref([])
const minKm = ref<string>()
const maxKm = ref<string>()

const jc = ref<string>()
const minJc = ref<string>()
const maxJc = ref<string>()

const showStyle = ref<string>('1')
const fontSize = ref<string>('1')
const bz = ref()

const maxPingzhengQj = ref('')

const ishaveRjz = ref<boolean>(true)
const isShowQjlh = ref<boolean>(false)
const userId = userStore.getUserInfo.id
//查询条件
const seach: any = ref({})
let loading: any = ref(true)
const [register, {closeModal, setModalProps, changeOkLoading}] = useModalInner((data) => {
  // 方式2
  formItems.value.openOne = data.data.openOne
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
  setModalProps({minHeight: 400});
  getQueryPlan()
  if(loading.value === true){
    changeOkLoading(true);
  }
})
// 数据库模式名称
const database = ref(getCurrentAccountName(false))

async function getQueryPlan() {
  let queryPlanIngo= await findByQueryPlan(database.value,'现金流量录入')
  console.log(queryPlanIngo)
  if(queryPlanIngo!=null){
    queryPlan.value=queryPlanIngo

    ishaveRjz.value=JSON.parse(queryPlanIngo.queryConditions).ishaveRjz
    strDate.value=JSON.parse(queryPlanIngo.queryConditions).strDate
    endDate.value=JSON.parse(queryPlanIngo.queryConditions).endDate

    fontSize.value=JSON.parse(queryPlanIngo.queryConditions).fontSize
  }
}

const databaseTrue = ref(getCurrentAccountName(true));
async function dynamicAdReload(data) {
  if(databaseTrue.value != data.accId+'-'+data.year){
    database.value=data.accId
    databaseTrue.value =data.accId+'-'+data.year
    console.log(databaseTrue.value)
    changeOkLoading(true);
    loading.value  = true
    bz.value = '0'
    AllCondition();
  }
}

const year = new Date().getFullYear();
async function AllCondition() {
  findByDateBase()
  //加载数据
  dateList.value = await findPeriodByAccontId(database.value);
  //根据年度筛选对应区间


  // 如果没有当前年度 获取 上年第一个区间
  const test = dateList.value.filter((o) => o.iyear === year);
  console.log(test)
  console.log(dateList.value)

  if (test.length === 0) {
    strDate.value = dateList.value[0].value;
    endDate.value = dateList.value[0].value;
  }
  changeOkLoading(false);
  loading.value  = false
}
async function findByDateBase() {
  // 获取所有账套信息 并过滤已授权的账套
  let accList = await getAdInfoDatas()
  // 获取当前用户的权限根据用户信息汇率
  let test1 = await getAllAccAuths({'userId': userStore.getUserInfo.id})
  let test2 = filterAccListByAuth(accList, test1)
  let codes = getAdObjInfoByCoCode(database.value, 'accId',test2)
  if (codes != null) {
    pageParameter.companyCode = codes.coCode
    pageParameter.companyName = codes.accNameCn
  }
}

const getAdObjInfoByCoCode = (value, type,accList) => {
  let list = accList.filter(item => item[type] == value)
  return list.length > 0 ? list[0] : null
}

async function handleChangeStrDate() {
  // 开始日期变动  如果开始日期大于结束于日期 则重置结束日期
  if(endDate.value){
    if(strDate.value > endDate.value){
      endDate.value = strDate.value
    }
    userId.value = userStore.getUserInfo.id
  }
}
function timechange() {
  dateselflg.value = time.value.length > 0;
}
const emit=defineEmits(['register','save'])
async function handleChangeEndDate() {
  // 开始日期变动  如果结束日期小于开始日期 则重置开始日期
  if(strDate.value){
    if(strDate.value > endDate.value){
      strDate.value = endDate.value
    }
  }
}

async function focusStrDate() {
  /*if (endDate.value) {
    strDateList.value = dateList.value.filter(o => o.value <= endDate.value)
  } else {
    strDateList.value = dateList.value
  }*/
  strDateList.value = dateList.value
}

async function focusEndDate() {
  if (strDate.value) {
    endDateList.value = dateList.value.filter(o => o.value >= strDate.value)
  } else {
    endDateList.value = dateList.value
  }
}

const handleFocusMinKm = () => {
  //获取焦点时 如果最大科目已选择 则过滤大于最大科目的数据
  /*if (maxKm.value) {
    minkmList.value = kmList.value.filter(o => o.ccode <= maxKm.value)
  } else {
    minkmList.value = kmList.value
  }*/
  findCorpBankAll().then(res=>{
    minkmList.value =  res.items.filter(item=>item.icash == '1')
  });
};

const filterOption = (input: string, option: any) => {
  return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};
// 已授权账套列表
const accAuthList = ref([])
// 账套授权信息
const userAuthMap = ref([])
const defaultAdName = ref(getCurrentAccountName(false));

onMounted(async () => {
   AllCondition()
});

/*********Mr·Ye*********/
const defaultTabsKey = ref('1')
const whetherGroup = ref(false)
const activeKey: any = ref('');

const defaultModel= {
  constant: {
    /*   tableStyle: 'J',
       tableFontSize: 'MAX',*/
    currency: '人民币元',
    /*    nature: '',
        unaccounted: true,
        showCumulative: false*/
  },
  authority: {codes: [],types: []},
  variable: {
    periodStart: '',
    periodEnd: '',
    dateStart: '',
    dateEnd: '',
    voucherNumberStart: '',
    voucherNumberEnd: '',
    voucherType: '',
    summary: '',
    reviewStatus:'',
    bookStatus:'',
    subjectNumber: '',
    direction:'',
    amountMax: '',
    amountMin: '',
    numberMax: '',
    numberMin: '',
    wbAmountMax: '',
    wbAmountMin: '',
    billNumberStart: '',
    billNumberEnd: '',
    preparedMan: '',
    cashierMan: '',
    reviewMan: '',
    bookMan: '',
  }
}
const modelList = reactive({'1': {constant: {
      /*     tableStyle: 'J',
           tableFontSize: 'MAX',*/
      currency: '人民币元',
      /*     nature: '',
           unaccounted: true,
           showCumulative: false*/
    },
    authority: {codes: [],types: []},
    variable: {
      periodStart: '',
      periodEnd: '',
      dateStart: '',
      dateEnd: '',
      voucherNumberStart: '',
      voucherNumberEnd: '',
      voucherType: '',
      summary: '',
      reviewStatus:'',
      bookStatus:'',
      subjectNumber: '',
      direction:'',
      amountMax: '',
      amountMin: '',
      numberMax: '',
      numberMin: '',
      wbAmountMax: '',
      wbAmountMin: '',
      billNumberStart: '',
      billNumberEnd: '',
      preparedMan: '',
      cashierMan: '',
      reviewMan: '',
      bookMan: '',
    }},'2':{},'3':{
    constant: {
      tableStyle: 'J',
      tableFontSize: 'MAX',
      currency: '人民币元',
      nature: '',
      unaccounted: true,
      showCumulative: false
    },
    authority: {codes: [],types: []},
    variable: {
      periodStart: '',
      periodEnd: '',
      dateStart: '',
      dateEnd: '',
      accountNum: '',
      summary: '',
      direction: '',
      amountMax: '',
      amountMin: '',
    }
  },'4':{}})

const openOrClose = (visible:boolean)=>{
  // 初始化选中
  if (visible &&  null != formItems.value.openOne && Object.keys(modelList['3']).length != 0){
    //默认赋值
    defaultTabsKey.value = '3'
  }
}
const tabsChange  = (value)=>{
  /* if (value == '1'){
     modelList[defaultTabsKey.value] =  defaultmodelList[defaultTabsKey.value]
   }else if (value == '2'){
     modelList[defaultTabsKey.value] = modelList[value]
   }else if (value == '3'){
     modelList[defaultTabsKey.value] = modelList[value]
   }else if (value == '4'){
     modelList[defaultTabsKey.value]= modelList[value]
   }*/
}

function filterAccListByAuth(acclist,authList) {
  return acclist.filter(item=>{
    for(let i = 0;i < authList.length;i++){
      if (item.accId ==  authList[i].accId){
        return true
      }
    }
    return  false;
  })
}
function initAuthCondition(adName,authList) {
  // 获取当前选中的年度
  let newList = authList.filter(item=> item.accId == adName)
  if (newList.length == 0)return false;
  let obj = newList[0]
  if (newList.length > 1){
    let startRq =  modelList[defaultTabsKey.value].variable.periodStart
    let dateStart =  modelList[defaultTabsKey.value].variable.dateStart
    let yearValue = ''
    if (startRq == ''){
      yearValue  = dateStart.substring(0,4)
    }else {
      yearValue  = startRq.substring(0,4)
    }
    obj = newList.filter(item => item.iyear == yearValue)[0]
  }
  if (obj.ccodeAll != '1')modelList[defaultTabsKey.value].authority.codes = JSON.parse(obj.codeListJson)
  if (obj.ccodeAll != '1')modelList[defaultTabsKey.value].authority.types = JSON.parse(obj.voucherTypeJson)
}
/*********Mr·Ye*********/
let isChanged: boolean = false

async function handleOk() {
  let startQj = strDate.value || ''
  let endQj = endDate.value || ''
  let startRq = null == riqi.value[0]? '':riqi.value[0].format('YYYY-MM-DD')
  let endRq = null == riqi.value[1]? '':riqi.value[1].format('YYYY-MM-DD')
  if ((startQj == '' &&  endQj == '') && riqi .value.length === 0) {
    message.error('请选择会计区间or日期期间!');
    return
  }
  if ((startQj != '' &&  endQj == '') || (startQj == '' &&  endQj != '')  ) {
    message.error('请完善会计区间');
    return
  }else if ((startQj != '' && endQj != '' && (startQj.substring(0,4) != endQj.substring(0,4)))){
    message.error('暂不支持跨越年度会计区间查询');
    return
  }else if ((startRq != '' && endRq != '' && (startRq.substring(0,4) != endRq.substring(0,4)))){
    message.error('暂不支持跨越年度日期区间查询');
    return
  }

  let pingS = modelList[defaultTabsKey.value].variable.voucherNumberStart
  let pingE = modelList[defaultTabsKey.value].variable.voucherNumberEnd
  if (pingS != '' || pingS !='' ){
    if(pingS == '' || pingE == ''){
      message.error('请完善凭证编号区间条件!');
      return
    }else if(Number(pingE) < Number(pingS)){
      message.error('凭证编号区间条件必须自小到大!');
      return
    }
  }
  let min = modelList[defaultTabsKey.value].variable.amountMin
  let max = modelList[defaultTabsKey.value].variable.amountMax
  if ((min != '' && max =='' ) || max != '' && min =='' ){
    message.error('请完善本币金额区间条件!');
    return
  }else if (parseFloat(min) > parseFloat(max)){
    message.error('本币金额区间条件必须自小到大!');
    return
  }
  let nMin = modelList[defaultTabsKey.value].variable.numberMin
  let nMax = modelList[defaultTabsKey.value].variable.numberMax
  if ((nMin != '' && nMax =='' ) || nMax != '' && nMin =='' ){
    message.error('请完善数量区间条件!');
    return
  }else if (parseFloat(nMin) > parseFloat(nMax)){
    message.error('数量条件必须自小到大!');
    return
  }

  let wbMin = modelList[defaultTabsKey.value].variable.wbAmountMin
  let wbMax = modelList[defaultTabsKey.value].variable.wbAmountMax
  if ((wbMin != '' && wbMax =='' ) || wbMax != '' && wbMin =='' ){
    message.error('请完善外币金额区间条件!');
    return
  }else if (parseFloat(wbMin) > parseFloat(wbMax)){
    message.error('外币金额区间条件必须自小到大!');
    return
  }

  modelList[defaultTabsKey.value].variable.periodStart = startQj
  modelList[defaultTabsKey.value].variable.periodEnd = endQj
  modelList[defaultTabsKey.value].variable.dateStart = startRq
  modelList[defaultTabsKey.value].variable.dateEnd = endRq

  modelList[defaultTabsKey.value].openOne = formItems.value.openOne
  modelList[defaultTabsKey.value].accAuthList = accAuthList.value
  modelList[defaultTabsKey.value].userAuthMap = userAuthMap.value

  modelList[defaultTabsKey.value].variable.accId = database.value
  modelList[defaultTabsKey.value].variable.database = databaseTrue.value

  initAuthCondition(defaultAdName,userAuthMap.value)

  formItems.value.pageParameter = pageParameter;
  formItems.value.accountId = databaseTrue.value
  const queryPlanEntity={
    accountId:database.value,
    owningMenuName:'现金流量',
    owningPlan:activeKey.value,
    planPerson:userStore.getUserInfo.id,
    queryConditions:{
      ishaveRjz:formItems.value.ishaveRjz,
      bend:formItems.value.jc,
      strDate:formItems.value.strDate,
      endDate:formItems.value.endDate,
      dateStart:formItems.value.dateStart,
      dateEnd:formItems.value.dateEnd,
      timflg:'qj',
      fontSize:formItems.value.fontSize,
      styleValue:formItems.value.styleValue
    }
  }
  await saveQueryPlan(queryPlanEntity);
  emit('save', unref(modelList[defaultTabsKey.value]))
  closeModal()
  return true
}
async function handleClose() {
  if (null != formItems.value.openOne && formItems.value.openOne == 1) {
    await closeCurrent()
    // router.push('/home/welcome')
  }
  closeModal()
}
// 查询

let radiovalue = ref(1);
const riqi: any = ref([]);
function radiochange(val) {
  kmList.value = [];
  timeselflg.value = val.target.value === 1;
  dateselflg.value = val.target.value === 2;
  if (val.target.value === 1) {
    riqi.value = '';
    strDate.value = maxPingzhengQj.value;
    endDate.value = maxPingzhengQj.value;
  } else {
    strDate.value = '';
    endDate.value = '';
  }
}
</script>
<style lang="less" scoped>
@import '/@/assets/styles/redTitle-open.less';
:deep(.ant-select-selection-item){
  font-weight: bold;
}
.nc-open-content {
  background-image: url(/@/assets/images/homes/bg-pattern.png);
  background-repeat: no-repeat;
  background-position: 66% 8%;
  background-size: contain;
  position: relative;
  text-align: left;

  .open-content-title {
    > div {
      display: inline-block;
    }

    > div:nth-of-type(1) {
      width: 200px;
      background-color: #efeeee;
      color: black;
      font-size: 20px;
      text-align: center;
      padding: 5px 10px
    }
  }

  .open-content-up {
    position: relative;
    top: 15px;

    .ocup-position {
      position: absolute;
      left: 0;
      width: 170px;
      background-color: #65cbec;
      color: white;
      font-size: 16px;
      text-align: left;
      padding: 5px 10px;
    }

    .ocup-position:nth-of-type(1) {
      top: 0px;
    }

    .ocup-position:nth-of-type(2) {
      top: 190px;
    }

    ul {
      padding: 10px;

      li {
        margin: 10px 0;

        //span {
        //  font-size: 14px;
        //  color: #5a5a5a;
        //}

        .right_span{
          display: inline-block;
          width: 90px !important;
          color: #5a5a5a;
          font-weight: bold;
        }

        .ant-select {
          font-size: 14px;
        }
      }


    }
  }

  .open-content-foot {
    display: block;
    position: fixed;
    margin-top: 43px;
  }

  .ant-tabs-tabpane-active {
    overflow-y: auto;
    height: 400px;
  }

  .ant-select-selection-search-input {
    border-bottom: none !important;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-input) {
    background: none !important;
  }

  :deep(.ant-select-selector), :deep(.ant-input-affix-wrapper),:deep(.ant-input){
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    background: none;
  }

  label:not(.ant-radio-button-wrapper) {
    text-align: right;
    width: 110px;
    display: inline-block;
    padding: 5px 10px;
  }

  .ant-radio-group {
    .ant-radio-wrapper {
      //width: 70px;

      .ant-radio-input {
        border-color: slategrey;
      }
    }

    p:nth-of-type(2) {
      margin-bottom: 0;
    }
  }

  :deep(.ant-picker-range) {
    background: none;
    border: none;
    border-bottom: 1px solid #bdb9b9;

    input {
      text-align: center;
    }
  }

  .special-border-div {
    position: relative;
    border: 1px #acabab solid;
    margin: 20px 0;
    text-align: left;

    .spanclass{
      font-weight: bold;
      color: #5a5a5a;
    }
    > span {
      position: absolute;
      top: -12px;
      left: 50px;
      font-size: 12px;
      background: white;
      padding: 0 15px;
      font-weight: bold;
    }

    > div {
      margin: 15px;
      color: black;
      display: flex;

      .sbd-left {
        width: 60%;

        .ant-radio-group {
          .ant-radio-wrapper {
            width: 11% !important;
            text-align: left;

            .ant-radio-input {
              border-color: slategrey;
            }
          }
        }
      }

      .sbd-right {
        width: 40%;
        padding: 3% 4% 0;
        .ant-radio-button-wrapper {
          color: #0096c7;
        }
      }

    }
  }
}

:deep(.ant-tabs-left-bar) {
  margin-right: 0px !important;
}

:deep(.ant-tabs-left) {
  .ant-tabs-tab {
    width: 170px;
    font-weight: bold;
    display: inline-block;
  }

  .ant-tabs-tab-active {
    color: #65cbec !important;
    .ant-tabs-tab-btn:after{
      content: '√';
      margin-left: 5px;
    }
  }

  .ant-tabs-tabpane-active {
    padding-left: 0 !important;
  }

  .ant-tabs-tab:nth-of-type(1) {
    margin-top: 35px;
  }

  /*  .ant-tabs-tab:nth-of-type(3) {
      margin-top: 110px !important;
    }*/
}
:deep(.ant-cascader-input){
  border: none;
  border-bottom: 1px solid #bdb9b9;
}
</style>
