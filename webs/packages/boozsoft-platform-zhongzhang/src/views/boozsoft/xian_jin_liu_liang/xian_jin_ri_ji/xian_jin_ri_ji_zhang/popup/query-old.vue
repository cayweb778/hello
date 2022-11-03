<template>
  <BasicModal
    destroyOnClose
    width="800px"
    v-bind="$attrs"
    title="现金日记账查询"
    @ok="handleOk()"
    @cancel="handleClose()"
    :loading="loadMark"
    @register="register"
  >
    <div class="nc-open-content" style="height: 480px !important;">
      <div class="open-content-up" style="height: 480px !important;">
        <div class="ocup-position"> 系统方案</div>
        <div class="ocup-position"> 个人方案</div>
        <a-tabs type="card" tabPosition="left" v-model:activeKey="defaultTabsKey" @change="tabsChange">
          <a-tab-pane key="1" tab="标准模式">
            <ul>
              <li>
                <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
              </li>
              <li>
                <a-radio-group style="width: 100%" v-model:value="radiovalue" @change="radiochange">
                  <a-radio :value="1" style="width: 110px">
                    <span>期间：</span>
                  </a-radio>
                  <span style="margin-left: 3%">
                    <a-select
                      v-model:value="strDate"
                      show-search
                      :disabled="dateselflg"
                      style="width: 150px;text-align: center;"
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
                    style="width: 150px;text-align: center;"
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
                  </span>

                  <br><br>
                  <a-radio :value="2" style="width: 110px">
                    <span>日期：</span>
                  </a-radio>
                  <span style="margin-left: 3%">
                    <a-range-picker
                      style="width: 340px"
                      v-model:value="riqi"
                      :disabled="timeselflg"
                      format="YYYY-MM-DD"
                      value-format="YYYY-MM-DD"
                      @change="timechange"
                    />
                  </span>
                </a-radio-group>
              </li>
              <li><span>科目：</span>
                <span style="margin-left: 2%;">
                <a-select
                  v-model:value="modelList['1'].variable.accountNum"
                  show-search
                  placeholder="所属科目"
                  style="width: 340px;"
                  :filter-option="filterOption"
                >
                  <a-select-option v-for="d in minkmList" :value="d.ccode">
                    {{ d.ccode }} {{ d.ccodeName }}
                  </a-select-option>
                </a-select>

                  <!--                <span>方向：</span>
                                  <a-select
                                    v-model:value="modelList['1'].variable.direction"
                                    allow-clear
                                    placeholder="全部"
                                    style="width: 150px"
                                  >
                                    <a-select-option value="">全部</a-select-option>
                                    <a-select-option value="md">借方</a-select-option>
                                    <a-select-option value="mc">贷方</a-select-option>
                                  </a-select>-->
                </span>
              </li>
              <li>
                <!--                <span>结算方式：</span>
                                <a-select
                                  v-model:value="jiesuan"
                                  show-search
                                  allow-clear
                                  placeholder="结算方式"
                                  style="width: 200px"
                                >
                                  <a-select-option v-for="d in minkmList" :value="d.ccode">
                                    {{ d.value }}
                                  </a-select-option>
                                </a-select>-->
                <span>摘要：</span>
                <a-input v-model:value="modelList['1'].variable.summary" placeholder="摘要"
                         style="width: 340px;margin-left: 2%;"/>
                <!--                <span>&emsp;科目性质：</span>
                                <a-select
                                  style="width: 200px"
                                  v-model:value="modelList['1'].constant.nature"
                                >
                                  <a-select-option value="" label="全部" />
                                  <a-select-option value="CW" label="财务会计"/>
                                  <a-select-option value="YS" label="预算会计"/>
                                </a-select>-->
              </li>
              <li><span>金额：</span>
                <span style="margin-left: 2%;">
                <a-input v-model:value="modelList['1'].variable.amountMin" placeholder="最小金额"
                         style="width: 150px"/>
                <span>&emsp;~&emsp;</span>
                <a-input v-model:value="modelList['1'].variable.amountMax" placeholder="最大金额"
                         style="width: 150px"/>
                </span>
              </li>
              <li><span>币种：</span>
                <a-select style="width: 340px;margin-left: 2%;" v-model:value="modelList['1'].constant.currency">
                  <a-select-option v-for="d in bzList" :key="d.id" :value="d.foreignName">
                    {{ d.foreignName }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>凭证类别：</span>
                <a-select
                  v-model:value="modelList['1'].variable.direction"
                  allow-clear
                  placeholder="全部"
                  style="width: 340px;margin-left: 2%;"
                >
                  <a-select-option value="">全部</a-select-option>
                  <a-select-option value="md">记</a-select-option>
                  <a-select-option value="mc">转</a-select-option>
                </a-select>
                <!--                <span>&emsp;字&emsp;&emsp;号：</span>
                                <a-select v-model:value="modelList['1'].constant.tableFontSize" style="width: 200px" @change="handleChange">
                                  <a-select-option value="MAX">
                                    大号字体
                                  </a-select-option>
                                  <a-select-option value="MIN">
                                    小号字体
                                  </a-select-option>
                                </a-select>-->
              </li>
              <!--              <li>
                              <div style="height:30px;"></div>
                            </li>-->
              <li>
                <a-checkbox v-model:checked="ishaveRjz" style="width: 200px">
                  包含未记账
                </a-checkbox>
              </li>
            </ul>
          </a-tab-pane>
          <a-tab-pane key="2" tab="集团模式">
          </a-tab-pane>
          <a-tab-pane key="3" tab="个人模式">
            <ul>
              <li>
                <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
              </li>
              <li>
                <a-radio-group style="width: 100%" v-model:value="radiovalue" @change="radiochange">
                  <a-radio :value="1" style="width: 110px">
                    <span>期间：</span>
                  </a-radio>
                  <span style="margin-left: 3%">
                    <a-select
                      v-model:value="strDate"
                      show-search
                      :disabled="dateselflg"
                      style="width: 150px;text-align: center;"
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
                    style="width: 150px;text-align: center;"
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
                  </span>

                  <br><br>
                  <a-radio :value="2" style="width: 110px">
                    <span>日期：</span>
                  </a-radio>
                  <span style="margin-left: 3%">
                    <a-range-picker
                      style="width: 340px"
                      v-model:value="riqi"
                      :disabled="timeselflg"
                      format="YYYY-MM-DD"
                      value-format="YYYY-MM-DD"
                      @change="timechange"
                    />
                  </span>
                </a-radio-group>
              </li>
              <li><span>科目：</span>
                <span style="margin-left: 2%;">
                <a-select
                  v-model:value="modelList['3'].variable.accountNum"
                  show-search
                  placeholder="所属科目"
                  style="width: 150px"
                  :filter-option="filterOption"
                >
                  <a-select-option v-for="d in minkmList" :value="d.ccode">
                    {{ d.ccode }} {{ d.ccodeName }}
                  </a-select-option>
                </a-select>

                <span>方向：</span>
                <a-select
                  v-model:value="modelList['3'].variable.direction"
                  allow-clear
                  placeholder="全部"
                  style="width: 150px"
                >
                  <a-select-option value="">全部</a-select-option>
                  <a-select-option value="md">借方</a-select-option>
                  <a-select-option value="mc">贷方</a-select-option>
                </a-select>
                </span>
              </li>
              <li>
                <span>摘要：</span>
                <a-input v-model:value="modelList['3'].variable.summary" placeholder="摘要"
                         style="width: 340px;margin-left: 2%;"/>
              </li>
              <li><span>金额：</span>
                <span style="margin-left: 2%;">
                <a-input v-model:value="modelList['3'].variable.amountMin" placeholder="最小金额"
                         style="width: 150px"/>
                <span>&emsp;~&emsp;</span>
                <a-input v-model:value="modelList['3'].variable.amountMax" placeholder="最大金额"
                         style="width: 150px"/>
                </span>
              </li>
              <li><span>币种：</span>
                <a-select style="width: 340px;margin-left: 2%;" v-model:value="modelList['3'].constant.currency">
                  <a-select-option v-for="d in bzList" :key="d.id" :value="d.foreignName">
                    {{ d.foreignName }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <div style="height:30px;"></div>
              </li>
              <li>
                <a-checkbox v-model:checked="ishaveRjz" style="width: 200px">
                  包含未记账
                </a-checkbox>
              </li>
            </ul>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>
    <template #footer>
      <div style="display: flex;justify-content : flex-end;">
        <div style="color: red;width:70%;font-size: 16px;text-align: center;">
          <span v-if="minkmList.length==0">未发现授权会计科目，请切换到其他年度或公司代码查询!</span>
        </div>
        <div>
          <a-button @click="handleClose()">取消</a-button>
          <a-button @click="handleOk()" type="primary">确认</a-button>
        </div>
      </div>
    </template>
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
import {currentCyDatas, findAllXjOrLlList, findCorpBankAll} from "/@/api/record/system/financial-settings";
import {useUserStore} from "/@/store/modules/user";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";

import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useMessage} from "/@/hooks/web/useMessage";
import {findAllByCode, findByUserIdAndAccIdAndYear} from "/@/api/sys-acc-auth/sys-acc-anth";
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
// const riqi: any = ref([moment(new Date().getFullYear(), 'YYYY/MM/DD'), moment(useCompanyOperateStoreWidthOut().getLoginDate, 'YYYY/MM/DD')]);
const riqi:any = ref()
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
  const res = await useRouteApi(findAllXjOrLlList, {schemaName: dynamicTenantId})({})
  minkmList.value = res.filter(item => item.bcash == '1' && item.bend == '1')
  /*const sysAccAuth = await findByUserIdAndAccIdAndYear({userId:useUserStore().getUserInfo['id'],accId:defaultAdName.value,year:year.value})
  minkmList.value = []
  if (sysAccAuth.length>0) {
    if (sysAccAuth[0].supervisor=='1' || sysAccAuth[0].ccodeAll=='1') {
      minkmList.value = res.filter(item => item.bcash == '1' && item.bend == '1')
    } else {
      const codeList = await findAllByCode({userId:useUserStore().getUserInfo['id'],accId:defaultAdName.value,year:year.value})
      if (codeList.length>0){
        codeList.forEach(code=> {
          res.forEach(item => {
            if (item.ccode==code && item.bcash == '1' && item.bend == '1'){
              minkmList.value.push(item)
            }
          })
        })
      }
    }
  }*/
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
  minkmList.value = res.filter(item => item.bcash == '1' && item.bend == '1')
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
const defaultAdName = ref(getCurrentAccountName(false))
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
  // let startRq = null == riqi.value[0]? '':riqi.value[0].format('YYYY-MM-DD')
  // let endRq = null == riqi.value[1]? '':riqi.value[1].format('YYYY-MM-DD')
  let startRq = null == riqi.value[0]? '':riqi.value[0]
  let endRq = null == riqi.value[1]? '':riqi.value[1]
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
  if (null == modelList[defaultTabsKey.value].variable.accountNum || modelList[defaultTabsKey.value].variable.accountNum == ''){
    message.error('请选择账户');
    return
  }

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
  emit('save', unref(modelList[defaultTabsKey.value]))
  closeModal()
  return true
}

const year = ref(useCompanyOperateStoreWidthOut().getLoginDate.slice(0, 4))
const dynamicAdReload = async (obj) => {
  loadMark.value = true
  dynamicTenantId.value = obj.accountMode
  defaultAdName.value = obj.accId
  year.value = obj.year

  if (useCompanyOperateStoreWidthOut().getLoginDate.slice(0, 4)>obj.year) {
    riqi.value = [obj.year + '-01-01', obj.year + '-12-31']
  } else {
    riqi.value = [obj.year + '-01-01', useCompanyOperateStoreWidthOut().getLoginDate]
  }

  await handleFocusMinKm()
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
:deep(.ant-modal) {
  top: 20px;
}

.nc-open-content {
  position: relative;

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

    .ocup-position {
      position: absolute;
      left: 0;
      width: 180px;
      background-color: #0096c7;
      color: white;
      font-size: 16px;
      text-align: center;
      padding: 5px 10px;
    }

    .ocup-position:nth-of-type(1) {
      top: 0px;
    }

    .ocup-position:nth-of-type(2) {
      top: 190px;
    }

    ul {
      padding: 10px 30px;

      li {
        margin: 10px 0;

        span {
          font-size: 14px;
          color: #747272;
        }

        > span:nth-of-type(1), .right_span {
          display: inline-block;
          width: 120px;
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
    height: 450px;
  }

  .ant-select-selection-search-input {
    border-bottom: none !important;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector), :deep(.ant-calendar-picker-input), :deep(.ant-input) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label {
    text-align: right;
    width: 110px;
    display: inline-block;
    padding: 5px 10px;
  }

  .ant-radio-group {
    .ant-radio-wrapper {
      width: 70px;

      .ant-radio-input {
        border-color: slategrey;
      }
    }

    p:nth-of-type(2) {
      margin-bottom: 0;
    }
  }
}


:deep(.ant-tabs-left-bar) {
  margin-right: 0px !important;
}

:deep(.ant-tabs-left) {
  .ant-tabs-tab {
    width: 180px;
    font-weight: bold;
    text-align: center;
    margin-bottom: 0px !important;
  }

  .ant-tabs-tab-active {
    background-color: #65cbec !important;
    color: rgba(0, 0, 0, 0.85) !important;
  }

  .ant-tabs-tab:nth-of-type(1) {
    margin-top: 35px;
  }

  .ant-tabs-tab:nth-of-type(3) {
    margin-top: 110px;
  }
}

</style>
