<template>
  <BasicModal
    width="890px"
    class="spaceLogo"
    v-bind="$attrs"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
    :loading="loadMark"
    loadingTip="查询参数初始化中"
  >
    <template #title>
      <div style="height: 30px;width: 100%;background-color: #5f375c;color: white;line-height: 30px;text-align: left;">
        <AppstoreOutlined  style="margin: 0 2px;font-size: 14px;"/>
        <span style="font-size: 13px">账簿</span>
      </div>
    </template>

    <div class="nc-open-content" style="height: 100%" >
      <SearchOutlined style="font-size: 24px;color: #0096c7;margin-top: 2px;margin-left: 40%;"/>
      <span style="line-height:30px;font-size: 24px;color: #0096c7;font-weight: bold;">&ensp;日记账</span>
      <div class="open-content-up">
        <div class="ocup-position">
          <PicLeftOutlined/>
          查询方案
        </div>
        <a-tabs type="card" v-model:activeKey="defaultTabsKey" tabPosition="left" @change="tabsChange">
          <a-tab-pane key="1" tab="标准模式">
            <ul style="width: 93%;margin-top: -20px;">
              <li>
                <div class="special-border-div">
                  <span style="color: #5a5a5a">业务范围</span>
                  <AccountPicker theme="one" @reloadTable="dynamicAdReload" style="display: block;"/>
                </div>
              </li>
              <li>
                <div class="special-border-div">
                  <span style="color: #5a5a5a">查询日期</span>
                  <div>
                    <div class="sbd-left">
                      <a-radio-group style="width: 100%" v-model:value="radiovalue" @change="radiochange">
                        <p>
                          <a-radio value="1">会计期间：</a-radio>
                          <a-select
                            v-model:value="strDate"
                            show-search
                            :disabled="dateselflg"
                            style="width: 200px;"
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
                        </p>
                        <p>
                          <a-radio value="2">制单日期：</a-radio>
                          <a-range-picker
                            v-model:value="riqi"
                            style="width: 400px;"
                            :disabled="timeselflg"
                          />
                        </p>
                      </a-radio-group>
                    </div>
                  </div>
                </div>
              </li>
              <li>
                <div class="special-border-div">
                  <span style="color: #5a5a5a">查询条件</span>
                  <div style="display: inline-block;width: 96%;margin-left: 23px;">
                    <div style="float: left;">
                      <span class="right_span">会计科目：</span>
                      <a-select
                        v-model:value="modelList['1'].variable.accountNum"
                        show-search
                        placeholder="所属科目"
                        style="width: 460px;"
                        :filter-option="filterOption"
                      >
                        <a-select-option v-for="d in minkmList" :value="d.ccode">
                          {{ d.ccode }} {{ d.ccodeName }}
                        </a-select-option>
                        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                      </a-select>
                    </div>
                    <div style="width: 50%;float: left;">
                      <span class="right_span">凭证摘要：</span>
                      <a-input v-model:value="modelList['1'].variable.summary" :allowClear="true" placeholder="摘要" style="width: 210px"/>
                    </div>
                    <div style="width: 50%;float: left;padding-left: 20px;">
                      <span class="right_span">凭证类别：</span>
                      <a-select
                        v-model:value="modelList['1'].variable.voucherType"
                        placeholder=""
                        style="width: 145px;text-align: center;"
                        :allowClear="true">
                        <a-select-option v-for="(item,index) in typesList" :key="index" :value="item.voucherTypeCode">{{ item.voucherTypeCode }}
                        </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                      </a-select>
                    </div>
                    <div style="float: left;">
                      <span class="right_span">本币金额：</span>
                      <a-input v-model:value="modelList['1'].variable.amountMin" :allowClear="true" placeholder="最小金额" style="width: 210px"/>
                      <span>&emsp;~&emsp;</span>
                      <a-input v-model:value="modelList['1'].variable.amountMax" :allowClear="true" placeholder="最大金额" style="width: 210px"/>
                    </div>

                    <div style="width: 50%;float: left;">
                      <span class="right_span">币种：</span>
                      &emsp;&nbsp;
                      <a-select style="width: 220px;" v-model:value="modelList['1'].constant.currency">
                        <a-select-option v-for="d in bzList" :key="d.id" :value="d.foreignName">
                          {{ d.foreignName }}
                        </a-select-option>
                        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                      </a-select>
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
import {PlusSquareOutlined,CaretDownOutlined,AppstoreOutlined} from '@ant-design/icons-vue'
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
  CheckboxGroup,
  Tabs as ATabs, message
} from "ant-design-vue"
import {useTabs} from '/@/hooks/web/useTabs';
import router from "/@/router";
import moment, {Moment} from 'moment';
import {
  findPeriodByAccontId,
  findBzAll,
  findMaxJc,
  findCodeKmByPeriod
} from '/@/api/record/generalLedger/data';
import {findByImgid} from "/@/api/record/eletronicInvoice_data/eletronic_invoice";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {
  currentAccountTypes,
  currentCyDatas,
  findAllXjOrLlList,
  findCorpBankAll
} from "/@/api/record/system/financial-settings";
import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";

import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useMessage} from "/@/hooks/web/useMessage";
import {findAllByCode, findByUserIdAndAccIdAndYear} from "/@/api/sys-acc-auth/sys-acc-anth";
import {findCodeByDayBook} from "/@/api/ri_ji_zhang/ri_ji_zhang";
import {saveLog} from "/@/api/record/system/group-sys-login-log";
const {
  createErrorModal
} = useMessage()

const pageParameter = reactive({
  companyCode: '100',
  companyName: '北京希格科技',
  ifUnit: false,
})
//红字（无现金科目，请设置现金科目后再进行查询）
const {closeCurrent} = useTabs(router);
const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const ACheckboxGroup = ACheckbox.Group
const ATabPane = ATabs.TabPane
const emit = defineEmits(['register', 'save'])
const data = []
const formItems: any = ref({})
let changeBeforeModel: any = {}
// 会计区间
const dateList: any = ref([])
// 会计科目
const kmList: any = ref([])
//级次
const jcList: any = ref([])
//币种
const bzList: any = ref([])
// 类别
const typesList: any = ref([])

const endDate: any = ref<String>("")
const strDate: any = ref<String>("")
let endDateList: any = ref([])
let strDateList: any = ref([])
let time: any = ref<Moment[]>([]);
// 日期是否可选
let timeselflg: any = ref(true);
// 期间是否可选
let dateselflg: any = ref(false);
let minkmList: any = ref([])
let maxkmList: any = ref([])
const minKm: any = ref<string>()
const maxKm: any = ref<string>()

const jc: any = ref<string>()
let maxJcList: any = ref([])
let minJcList: any = ref([])
const minJc: any = ref<string>()
const maxJc: any = ref<string>()

const showStyle = ref<string>('1')
const fontSize = ref<string>('1')
const bz = ref()

const ishaveRjz = ref<boolean>(true)
const isShowQjlh = ref<boolean>(false)
const userStore: any = useUserStore();
//查询条件
const seach: any = ref({})
let radiovalue: any = ref(1);
const loadMark = ref(false)

const [register, {closeModal, setModalProps}] = useModalInner((data) => {
  // 方式2
  formItems.value.openOne = data.data.openOne
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
  setModalProps({minHeight: 400});
})

function timeformat(dateData) {
  let date = new Date(dateData);
  let y = date.getFullYear();
  let m: any = date.getMonth() + 1;
  m = m < 10 ? '0' + m : m;
  let d: any = date.getDate();
  d = d < 10 ? '0' + d : d;
  return y + '-' + m + '-' + d;
}
// const riqi: any = ref([moment(new Date().getFullYear(), 'YYYY/MM/DD'), moment(new Date(), 'YYYY/MM/DD')]);
const riqi: any = ref([moment(new Date().getFullYear(), 'YYYY/MM/DD'), moment(useCompanyOperateStoreWidthOut().getLoginDate, 'YYYY/MM/DD')]);
// 数据库模式名称
const database = ref(getCurrentAccountName(false));
async function timechange() {
  // 切换数据库
  dateselflg.value = riqi.value.length > 0;
  if (riqi.value.length > 0) {
    const start = timeformat(riqi.value[0]._d);
    // 会计科目
    // const s = database.value+'-'+start.substring(0,4);
    kmList.value = await useRouteApi(findCodeKmByPeriod, {schemaName: dynamicTenantId})({
      strDate: start,
      endDate: 'test',
      accId: database.value,
      userId: userStore.getUserInfo.id
    });
  }
}

function radiochange(val) {
  kmList.value = [];
  timeselflg.value = val.target.value === 1;
  dateselflg.value = val.target.value === 2;
  if (val.target.value === 1) {
    riqi.value = '';
  } else {
    strDate.value = '';
    endDate.value = '';
  }
}

async function handleClose() {
  if (null != formItems.value.openOne && formItems.value.openOne == 1) {
    await closeCurrent()
    //router.push('/home/welcome')
  }
}

async function handleChangeStrDate() {
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
    let v:any = {
      strDate: strDate.value,
      endDate: endDate.value,
      accId: defaultAdName.value,
      userId: userStore.getUserInfo.id
    }
    kmList.value = await useRouteApi(findCodeKmByPeriod, {schemaName: dynamicTenantId})(v);
    timeselflg.value = true;
  }
}
/*function timechange() {
  dateselflg.value = time.value.length > 0;
}*/
async function handleChangeEndDate() {
  if (endDate.value === undefined) {
    strDate.value = '';
    timeselflg.value = false;
  }
  // 开始日期变动  如果结束日期小于开始日期 则重置开始日期
  if (strDate.value) {
    if (strDate.value > endDate.value) {
      strDate.value = '';
    }
    let v:any = {
      strDate: strDate.value,
      endDate: endDate.value,
      accId: defaultAdName.value,
      userId: userStore.getUserInfo.id
    }
    // 会计科目
    kmList.value = await useRouteApi(findCodeKmByPeriod, {schemaName: dynamicTenantId})(v);
    timeselflg.value = true;
  }
}

async function focusStrDate() {
  if (endDate.value) {
    strDateList.value = dateList.value.filter(o => o.value <= endDate.value)
  } else {
    strDateList.value = dateList.value
  }
}

async function focusEndDate() {
  if (strDate.value) {
    endDateList.value = dateList.value.filter(o => o.value >= strDate.value)
  } else {
    endDateList.value = dateList.value
  }
}

const handleFocusMinKm = async () => {
  //获取焦点时 如果最大科目已选择 则过滤大于最大科目的数据
  /*if (maxKm.value) {
    minkmList.value = kmList.value.filter(o => o.ccode <= maxKm.value)
  } else {
    minkmList.value = kmList.value
  }*/
  /*findCorpBankAll().then(res=>{
    minkmList.value =  res.items.filter(item=>item.icash == '1')
  });*/
  const res = await useRouteApi(findCodeByDayBook, {schemaName: dynamicTenantId})({})
  const sysAccAuth = await findByUserIdAndAccIdAndYear({userId:useUserStore().getUserInfo['id'],accId:defaultAdName.value,year:year.value})
  minkmList.value = []
  if (sysAccAuth.length>0) {
    if (sysAccAuth[0].supervisor=='1' || sysAccAuth[0].ccodeAll=='1') {
      minkmList.value = res.filter(item => item.bend == '1')
    } else {
      const codeList = await findAllByCode({userId:useUserStore().getUserInfo['id'],accId:defaultAdName.value,year:year.value})
      if (codeList.length>0){
        codeList.forEach(code=> {
          res.forEach(item => {
            if (item.ccode==code){
              minkmList.value.push(item)
            }
          })
        })
      }
    }
  }
  if (minkmList.value.length > 0) {
    modelList['1'].variable.accountNum = minkmList.value[0].ccode
  } else {
    modelList['1'].variable.accountNum = ''
    createErrorModal({
      iconType: 'warning',
      title: '警告',
      content: '没有授权的银行科目，请授权后重新查询！'
    })
  }
  /*minkmList.value = []
  minkmList.value = res.filter(item => item.bbank == '1' && item.bend == '1')
  if (minkmList.value.length > 0) {
    modelList['1'].variable.accountNum = minkmList.value[0].ccode
  }*/
};

const handleFocusMaxKm = () => {
  //获取焦点时 如果最小科目已选择 判断过滤小于最小科目的数据
  if (minKm.value) {
    maxkmList.value = kmList.value.filter(o => o.ccode >= minKm.value)
  } else {
    maxkmList.value = kmList.value
  }
};
const filterOption = (input: string, option: any) => {
  return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const handleFocusMinCj = () => {
  //获取焦点时 如果最大科目已选择 则过滤大于最大科目的数据
  if (maxJc.value) {
    minJcList.value = jcList.value.filter(o => o.value <= maxJc.value)
  } else {
    minJcList.value = jcList.value
  }
};
const handleFocusMaxCj = () => {
  //获取焦点时 如果最小科目已选择 判断过滤小于最小科目的数据
  if (minJc.value) {
    maxJcList.value = jcList.value.filter(o => o.value >= minJc.value)
  } else {
    maxJcList.value = jcList.value
  }
};
const defaultAdName = ref(useCompanyOperateStoreWidthOut().getSchemaName)
/*const getThisSchemaName = () => {
  return JSON.parse(window.localStorage.getItem('datasource')).schemaName
}*/
onMounted(async () => {
  // await handleFocusMinKm()
  //加载数据
  const res = await findPeriodByAccontId(defaultAdName.value);
  dateList.value = res.filter(item => item.iyear == year.value)
  //级次
  const num = await findMaxJc(defaultAdName.value, year.value);
  for (let i = 1; i < num + 1; i++) {
    jcList.value.push([{
      'label': i,
      'value': i,
    }]);
  }
  //币种
  bzList.value = []
  useCompanyOperateStoreWidthOut().getAccountList.forEach(item => {
    if (item.accId == defaultAdName.value && item.currency != '' && item.currency != null) {
      bzList.value = [{
        accountId: defaultAdName.value,
        foreignCode: item.currency,
        foreignName: item.currencyName
      }]
      modelList['1'].constant.currency = item.currencyName
    }
  })
  useRouteApi(currentCyDatas, {schemaName: dynamicTenantId})({accId: defaultAdName.value}).then(res => {
    bzList.value.push(...res.items)
    // bzList.value = res.items/*.filter(item=>item.foreignName.indexOf('人民币') == -1)*/
  });
  console.log(dateList.value)
  // 初始化默认区间
  strDate.value = dateList.value[0].value
  endDate.value = dateList.value[dateList.value.length-1].value
  // 是否集团
  let flag = useCompanyOperateStoreWidthOut().getAccountList[0].independent
  whetherGroup.value = (null == flag ? true : '0' == flag ? true : false)
  // 是否预算会计
  if (useCompanyOperateStoreWidthOut().getAccountList[0].ibudgetAccounting ||　false)  modelList[defaultTabsKey.value].constant.nature = 'YS'

  // 现金账户   findCorpBankAll()
  // 金币 currentCyDatas({accId: getThisSchemaName()})
});

function handleChange() {
}

function onChange() {
}

/*********Mr·Ye*********/
const defaultTabsKey = ref('1')
const whetherGroup = ref(false)

const dynamicTenantId = ref(getCurrentAccountName(true))
const defaultModel = {
  constant: {
    tableStyle: 'J',
    tableFontSize: 'MIN',
    currency: '人民币元',
    nature: '',
    unaccounted: true,
    showCumulative: false,
    dynamicTenantId: dynamicTenantId.value
  },
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
}
const modelList = reactive({'1': {constant: {
      tableStyle: 'J',
      tableFontSize: 'MIN',
      currency: '人民币元',
      nature: '',
      unaccounted: true,
      showCumulative: false,
      dynamicTenantId: dynamicTenantId.value
    },
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
    }},'2':{},'3':{
    constant: {
      tableStyle: 'J',
      tableFontSize: 'MIN',
      currency: '人民币元',
      nature: '',
      unaccounted: true,
      showCumulative: false,
      dynamicTenantId: dynamicTenantId.value
    },
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

/*********Mr·Ye*********/
let isChanged: boolean = false

async function handleOk() {
  let startQj = strDate.value || ''
  let endQj = endDate.value || ''
  let startRq = null == riqi.value[0]? '':riqi.value[0].format('YYYY-MM-DD')
  let endRq = null == riqi.value[1]? '':riqi.value[1].format('YYYY-MM-DD')
  if (strDate.value.length <= 0 && endDate.value <= 0 && riqi.value.length === 0) {
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
  // if (null == modelList[defaultTabsKey.value].variable.accountNum || modelList[defaultTabsKey.value].variable.accountNum == ''){
  //   message.error('请选择账户');
  //   return
  // }

  if ((modelList[defaultTabsKey.value].variable.amountMin != '' && modelList[defaultTabsKey.value].variable.amountMax =='' ) || modelList[defaultTabsKey.value].variable.amountMax != '' && modelList[defaultTabsKey.value].variable.amountMin =='' ){
    message.error('请完善金额区间条件!');
    return
  }

  formItems.value.strDate = strDate.value
  formItems.value.endDate = endDate.value
  formItems.value.minKm = minKm.value
  formItems.value.maxKm = maxKm.value
  formItems.value.jc = jc.value
  if (jc.value === 1) {
    formItems.value.minJc = minJc.value
    formItems.value.maxJc = maxJc.value
  }
  formItems.value.showStyle = showStyle.value
  formItems.value.fontSize = fontSize.value
  formItems.value.bz = bz.value
  formItems.value.ishaveRjz = ishaveRjz.value
  formItems.value.isShowQjlh = isShowQjlh.value
  // console.log(formItems.value)

  modelList[defaultTabsKey.value].constant.unaccounted = ishaveRjz.value
  modelList[defaultTabsKey.value].constant.showCumulative = isShowQjlh.value
  modelList[defaultTabsKey.value].constant.dynamicTenantId = dynamicTenantId.value
  modelList[defaultTabsKey.value].constant.defaultAdName = defaultAdName.value
  modelList[defaultTabsKey.value].constant.year = year.value
  modelList[defaultTabsKey.value].variable.periodStart = startQj
  modelList[defaultTabsKey.value].variable.periodEnd = endQj
  modelList[defaultTabsKey.value].variable.dateStart = startRq
  modelList[defaultTabsKey.value].variable.dateEnd = endRq
  modelList[defaultTabsKey.value].variable.unaccounted = ishaveRjz.value
  // 存入自定义
  if (defaultTabsKey.value == '1' || defaultTabsKey.value == '3') {
    modelList['3'] = modelList[defaultTabsKey.value]
    modelList['3'].variable.summary = ''
    modelList['3'].variable.direction = ''
    modelList['3'].variable.amountMax = ''
    modelList['3'].variable.amountMin = ''
  }
  modelList[defaultTabsKey.value].accountList = minkmList.value

  /************** 记录操作日志 ****************/
  let map={
    userId: useUserStoreWidthOut().getUserInfo.id,
    userName: useUserStoreWidthOut().getUserInfo.realName,
    optModule:'gl',
    optFunction:'总账',
    optRange:'1',// 1 账套
    uniqueCode:pageParameter.companyCode,
    optAction:'查询',
    optContent:'操作内容【日记账查询】,账套代码【'+pageParameter.companyCode+'】,账套名称【'+pageParameter.companyName+'】,查询条件【查询日期：'+strDate.value+','+endDate.value+'】,' +
      '科目编码【'+modelList['1'].variable.accountNum+'】,科目方向【'+modelList['1'].variable.direction+'】,币种【'+modelList['1'].constant.currency+'】,' +
      '本币金额【'+modelList['1'].variable.amountMin+','+modelList['1'].variable.amountMax+'】,凭证摘要【'+modelList['1'].variable.summary+'】'
  }
  await saveLog(map)
  emit('save', unref(modelList[defaultTabsKey.value]))
  closeModal()
  return true
}

const year = ref(useCompanyOperateStoreWidthOut().getLoginDate.slice(0, 4))
const dynamicAdReload = async (obj) => {
  radiovalue.value='1'
  loadMark.value = true
  dynamicTenantId.value = obj.accountMode
  defaultAdName.value = obj.accId
  year.value = obj.year
  pageParameter.companyCode = obj.coCode
  pageParameter.companyName = obj.companyName

  await handleFocusMinKm()
  //加载数据
  const res = await findPeriodByAccontId(obj.accId);
  dateList.value = res.filter(item => item.iyear == year.value)
  //级次
  const num = await findMaxJc(defaultAdName.value, year.value);
  for (let i = 1; i < num + 1; i++) {
    jcList.value.push([{
      'label': i,
      'value': i,
    }]);
  }
  // 凭证类别
  typesList.value = []
  await useRouteApi(currentAccountTypes, {schemaName: dynamicTenantId})({
    pageSize: 20,
    pageNumber: 1
  }).then(res => {
    typesList.value = res.items
  })
  //币种
  bzList.value = []
  useCompanyOperateStoreWidthOut().getAccountList.forEach(item => {
    if (item.accId == defaultAdName.value && item.currency != '' && item.currency != null) {
      bzList.value = [{
        accountId: defaultAdName.value,
        foreignCode: item.currency,
        foreignName: item.currencyName
      }]
      modelList['1'].constant.currency = item.currencyName
    }
  })
  useRouteApi(currentCyDatas, {schemaName: dynamicTenantId})({accId: defaultAdName.value}).then(res => {
    bzList.value.push(...res.items)
    // bzList.value = res.items/*.filter(item=>item.foreignName.indexOf('人民币') == -1)*/
  });
  // 初始化默认区间
  strDate.value = dateList.value[0].value
  endDate.value = dateList.value[dateList.value.length - 1].value
  // 是否集团
  let flag = useCompanyOperateStoreWidthOut().getAccountList[0].independent
  whetherGroup.value = (null == flag ? true : '0' == flag ? true : false)
  // 是否预算会计
  if (useCompanyOperateStoreWidthOut().getAccountList[0].ibudgetAccounting || false) modelList[defaultTabsKey.value].constant.nature = 'YS'
  loadMark.value = false
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
          //width: 90px !important;
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
    overflow-y: hidden;
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

  :deep(.ant-select-selector), :deep(.ant-input-affix-wrapper){
    border: none !important;
    border-bottom: solid 1px rgb(191, 191, 191) !important;
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
        width: 100%;

        .ant-radio-group {
          .ant-radio-wrapper {
            width: 21% ;
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
