<template>
  <BasicModal
    width="600px"
    class="spaceLogo"
    v-bind="$attrs"
    title="出纳签字"
    @ok="handleOk()"
    @cancel="handleClose()"
    :loading="modelLoadIng"
    :canFullscreen="false"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%;width: 540px;margin-left: 30px">
      <ul>
        <li>
          <AccountPicker theme="one" @reloadTable="codeChange" />
        </li>
        <li>
          <span class="special-v-span">凭证期间：</span>
          <a-select
            v-model:value="strDate"
            show-search
            @focus="focusStrDate"
            style="width: 190px;"
            @change="riqi=[]"
          >
            <a-select-option
              v-for="item in strDateList"
              :key="item.id"
              :value="item.value"
            >
              {{ item.value }}
            </a-select-option>
          </a-select>
          &emsp;<span class="special-v-span">凭证类别：</span>
          <a-select
            v-model:value="modelList['1'].variable.voucherType"
            placeholder=""
            style="width: 110px"
            :allowClear="true">
            <a-select-option value="" :selected="true">全部</a-select-option>
            <a-select-option v-for="(item,index) in typesList" :key="index"
                             :value="item.voucherTypeCode">{{ item.voucherTypeCode }}
            </a-select-option>
          </a-select>

        </li>
        <li>
          <span class="special-v-span">凭证日期：</span>
          <a-range-picker
            v-model:value="riqi"
            style="width: 416px;    "
            @change="timechange"
          />
        </li>
        <li><span class="special-v-span">凭证号：</span>
          <a-input v-model:value="modelList['1'].variable.voucherNumberStart"
                   :allowClear="true" placeholder="" style="width: 190px"/>
          <span>&emsp;~&emsp;</span>
          <a-input v-model:value="modelList['1'].variable.voucherNumberEnd" :allowClear="true"
                   placeholder="" style="width: 190px"/>
        </li>
        <li>
          <span class="special-v-span">制单人：</span>
          <a-select
            :allowClear="true"
            show-search
            v-model:value="modelList['1'].variable.preparedMan"
            placeholder=""
            style="width: 190px"
          >
            <a-select-option v-for="(item,index) in userList" :key="index"
                             :value="item.psnName">{{ item.psnName }}
            </a-select-option>
          </a-select>
          &emsp;<span class="special-v-span">出纳人：</span>
          <a-select
            :allowClear="true"
            show-search
            placeholder=""
            style="width: 110px"
            v-model:value="modelList['1'].variable.cashierMan"
          >
            <a-select-option v-for="(item,index) in userList" :key="index"
                             :value="item.psnName">{{ item.psnName }}
            </a-select-option>
          </a-select>
        </li>
        <li>
          <span class="right_span">分录显示：</span>
          <a-select
            :allowClear="true"
            v-model:value="modelList['1'].constant.showType"
            placeholder="明细/汇总"
            style="width: 104px">
            <a-select-option value="2">汇总</a-select-option>
            <a-select-option value="1">明细</a-select-option>
          </a-select>
        </li>
        <li>
          <a-checkbox v-model:checked="ishaveRjz" style="pointer-events: none">含本人制单凭证</a-checkbox>
        </li>
      </ul>
    </div>
  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {ref, unref, reactive, onMounted, watch, computed} from 'vue';
import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
import {PageWrapper} from '/@/components/Page';
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Checkbox as ACheckbox,
  message
} from "ant-design-vue";
import {SearchOutlined, PlusSquareOutlined} from '@ant-design/icons-vue';
import {useTabs} from '/@/hooks/web/useTabs';
import router from "/@/router";
import {Moment} from 'moment';
import {
  findCodeKmByPeriod,
  findMaxPingZhengQiJian,
  findMaxPingZhengQiJianMonth,
  findPeriodByAccontIdAndYaer
} from '/@/api/record/generalLedger/data';
import {
  currentAccountTypes,
  currentCyDatas,
  getAdInfoDatas,
  getAllAccAuths, filterAccListByAuth
} from "/@/api/record/system/financial-settings";
import {useUserStore} from "/@/store/modules/user";
import {
  getCurrentAccountName,
  hasBlank,
  pointMessage
} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useMessage} from "/@/hooks/web/useMessage";
import {psnFindByFlag} from "/@/api/psn/psn";
import AccountInfo from "/@/views/boozsoft/system/accvoucher/popup/AccountInfo.vue";
import Assist from "/@/views/boozsoft/system/accvoucher/popup/HelloAbc.vue";
import {findParameterValue, modifyParameterValue} from "/@/api/task-api/query-bus-api";
import {ObjTool, StrTool} from "/@/api/task-api/tools/universal-tools";
import {findAllFuzhuHesuanList} from "/@/api/record/system/fuzhu-hesuan";
//红字（无现金科目，请设置现金科目后再进行查询）
const {closeCurrent} = useTabs(router);
const emit = defineEmits(['register', 'save'])
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ACheckboxGroup = ACheckbox.Group

const userStore = useUserStore();
const formItems: any = ref({})
let changeBeforeModel: any = {}
// 会计区间
const dateList: any = ref([])
// 会计科目
const kmList: any = ref([])

//币种
const bzList: any = ref([])
// 类别
const typesList: any = ref([])
// 人员
const userList: any = ref([])
// 过滤

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

const maxPingzhengQj = ref('')

const ishaveRjz = ref<boolean>(true)
const userId = userStore.getUserInfo.id
//查询条件
// 已授权账套列表
const accAuthList = ref([])
// 账套授权信息
const userAuthMap = ref([])

const defaultAdName = getCurrentAccountName(false)
const tempCode = ref(defaultAdName)
const defaultYear = getCurrentAccountName(true).split('-')[2]
const currentDynamicTenant = ref('')
const {createWarningModal} = useMessage();

const targetKeys: any = ref(['subjectNumber', 'direction', 'number', 'amount', 'wbAmount', 'reviewStatus', 'bookStatus', 'preparedMan'])
const selectedKeys: any = ref([])
const filterDatas = [
  {
    key: 'subjectNumber',
    title: `科目编码`
  }
  , {
    key: 'direction',
    title: `科目方向`
  },
  {
    key: 'number',
    title: `数量`
  },
  {
    key: 'amount',
    title: `本币金额`
  },
  {
    key: 'wbAmount',
    title: `外币金额`
  },
  {
    key: 'reviewStatus',
    title: `审核状态`
  },
  {
    key: 'bookStatus',
    title: `记账状态`
  },
  {
    key: 'preparedMan',
    title: `制单人`
  },
  {
    key: 'checkMan',
    title: `审核人`
  },
  {
    key: 'cashierMan',
    title: `出纳`
  }, {
    key: 'reviewMan',
    title: `主管`
  }, {
    key: 'bookMan',
    title: `记账人`
  }
]

const targetKeys2: any = ref([])
const startRendering = ref(false)
const selectedKeys2: any = ref([])
const filterDatas2 = [
  {
    key: 'cdeptId',
    title: `部门`
  }
  , {
    key: 'cpersonId',
    title: `员工`
  },
  {
    key: 'ccusId',
    title: `客户`
  },
  {
    key: 'csupId',
    title: `供应商`
  },
  {
    key: 'projectId',
    title: `项目`
  }
]


onMounted(async () => {
 userAuthMap.value = useCompanyOperateStoreWidthOut().getAuthorizeList
  accAuthList.value = filterAccListByAuth(useCompanyOperateStoreWidthOut().getAccountList, userAuthMap.value)
});

const getAdObjInfoByCoCode = (value, type) => {
  let list = accAuthList.value.filter(item => item[type] == value)
  return list.length > 0 ? list[0] : null
}

const [register, {closeModal, setModalProps}] = useModalInner((data) => {
  // 方式2
  formItems.value = data.data
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
  setModalProps({minHeight: 300});
})

const totalChange = computed(() => formItems.value.total)
watch(totalChange, async (a) => {
    if (a != 0) {
      modelLoadIng.value = false
      if (a > 0) {
        closeModal()
      } else {
        createWarningModal({title: '温馨提示', content: '暂无任何查询结果！'});
      }
      formItems.value.total = 0
    }
  }
)
const datebaseYear = ref('')
const reLifeQueryParameter = async (dynamicTenant) => {
  if (hasBlank(dynamicTenant)) return
  // 获取最大凭证区间
  let tenantName = dynamicTenant.split('-')[0] + '-' + dynamicTenant.split('-')[1]
  // 初始化已知自定义辅助核算
  let fzL = await useRouteApi(findAllFuzhuHesuanList, {schemaName: dynamicTenant})({})
  startRendering.value = true // 开始加载辅助项
  if (fzL.length > 0) fzL.forEach(item => filterDatas2.push({
    key: 'cdfine' + item.cdfine,
    title: item.cname
  }))
  //加载期间数据
  dateList.value = []
  dateList.value = await findPeriodByAccontIdAndYaer({'accontId':tenantName,iyear: datebaseYear.value});
  if (dateList.value.length > 0) {
    const qijian = await useRouteApi(findMaxPingZhengQiJianMonth, {schemaName: dynamicTenant})( datebaseYear.value) || '';
    // 初始化默认区间
    if (hasBlank(qijian)) {
      strDate.value = dateList.value[0].value
      endDate.value = dateList.value[dateList.value.length - 1].value
    } else {
      maxPingzhengQj.value = qijian.substring(0, 4) + '-' + qijian.substring(4, 6)
      strDate.value = maxPingzhengQj.value
      endDate.value = maxPingzhengQj.value
    }
    //await reLifeKmList(currentDynamicTenant.value)
    // 是否集团
    let thisAccObj = getAdObjInfoByCoCode(tenantName, 'accId')
    tempCode.value = thisAccObj.coCode
    if (null != thisAccObj) {
      let flag = thisAccObj.independent
      whetherGroup.value = (null == flag ? true : '0' == flag ? true : false)
      // 是否预算会计
      //if (thisAccObj.ibudgetAccounting ||　false)  modelList[defaultTabsKey.value].constant.nature = 'YS'
      // 获取币种信息
      /*  bzList.value = []
        await useRouteApi(currentCyDatas, {schemaName: dynamicTenant})({accId: tenantName}).then(res1 => {
          bzList.value = res1.items.filter(item => item.foreignCode.indexOf(thisAccObj.currency) == -1)
        });*/
      // 凭证类别
      typesList.value = []
      await useRouteApi(currentAccountTypes, {schemaName: dynamicTenant})({
        pageSize: 20,
        pageNumber: 1
      }).then(res => {
        typesList.value = res.items
      })
      // 人员
      await useRouteApi(psnFindByFlag, {schemaName: dynamicTenant})({}).then(res => {
        userList.value = res
      })
    }
  } else {
    await pointMessage({title: '温馨提示', content: '请先初始化该公司代码账套会计期间后在进行查询！', delay: true})
    modelList[defaultTabsKey.value].constant.companyCode = ''
    currentDynamicTenant.value = ''
    strDate.value = ''
    endDate.value = ''
    tempCode.value = ''
  }
}

async function reLifeKmList(dynamicTenant) {
  if (hasBlank(dynamicTenant)) return
  let tenantName = dynamicTenant.split('-')[0] + '-' + dynamicTenant.split('-')[1]
  kmList.value = await useRouteApi(findCodeKmByPeriod, {schemaName: dynamicTenant})({
    strDate: strDate.value,
    endDate: endDate.value,
    accId: tenantName,
    userId: userId
  });
}

const codeChange = async (value) => {
  let newName = value.accId
  // 查看是否存在 查询参数
  dbQueryJson.accountId = newName;
  datebaseYear.value = value.iyear
  modelList[defaultTabsKey.value].constant = defaultModel.constant
  modelList[defaultTabsKey.value].constant.companyCode = value
  modelList[defaultTabsKey.value].variable = defaultModel.variable
  dbQueryJson.id = null
  modelList[defaultTabsKey.value].constant.tenantId = value.accountMode
  currentDynamicTenant.value = value.accountMode
  reLifeQueryParameter(currentDynamicTenant.value)
}


async function handleChangeStrDate() {

  endDate.value = strDate.value

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
    await reLifeKmList(currentDynamicTenant.value)
    timeselflg.value = true;
  }
}

function timechange() {
  dateselflg.value = time.value.length > 0;
  strDate.value = ''
}

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
    // 会计科目
    await reLifeKmList(currentDynamicTenant.value)
    timeselflg.value = true;
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

const confirm = () => {

}
const cancel = () => {

}
/*********Mr·Ye*********/
const defaultTabsKey = ref('1')
const whetherGroup = ref(false)
const modelLoadIng = ref(false)

const defaultModel = {
  constant: {
    currency: '', tenantId: '',
    companyCode: '',
    queryType: '1',
    showType: '1',
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
      currency: '', tenantId: '',
      companyCode: '',
      queryType: '1',
      showType: '1',
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
  }
})

function initAuthCondition(adName, authList) {
  // 获取当前选中的年度
  modelList[defaultTabsKey.value].constant.thisAdInfo = accAuthList.value.filter(item => item.accId === adName)[0]
  let newList = authList.filter(item => item.accId == adName)
  if (newList.length == 0) return false;
  let obj = newList[0]
  if (newList.length > 1) {
    let startRq = modelList[defaultTabsKey.value].variable.periodStart
    let dateStart = modelList[defaultTabsKey.value].variable.dateStart
    let yearValue = ''
    if (startRq == '') {
      yearValue = dateStart.substring(0, 4)
    } else {
      yearValue = startRq.substring(0, 4)
    }
    obj = newList.filter(item => item.iyear == yearValue)[0]
  }
  if (obj.ccodeAll != '1') modelList[defaultTabsKey.value].authority.codes = JSON.parse(obj.codeListJson)
  if (obj.ccodeAll != '1') modelList[defaultTabsKey.value].authority.types = JSON.parse(obj.voucherTypeJson)
}

/*********Mr·Ye*********/
let isChanged: boolean = false

async function handleOk() {
  modelLoadIng.value = false
  if (accAuthList.value.length === 0) {
    message.error('请先获取账套查询权限!');
    return
  } else if (modelList[defaultTabsKey.value].constant.queryType == '1' && hasBlank(modelList[defaultTabsKey.value].constant.companyCode)) {
    message.error('请选择公司代码!');
    return
  } else if (dateList.value.length === 0) {
    message.error('请先初始化会计期间后再进行查询!');
    return
  }
  let startQj = strDate.value || ''
  let endQj = strDate.value || ''
  let startRq = null == riqi.value[0] ? '' : riqi.value[0].format('YYYY-MM-DD')
  let endRq = null == riqi.value[1] ? '' : riqi.value[1].format('YYYY-MM-DD')
  if ((startQj == '' && endQj == '') && riqi.value.length === 0) {
    message.error('请选择会计区间or日期期间!');
    return
  }
  if ((startQj != '' && endQj == '') || (startQj == '' && endQj != '')) {
    message.error('请完善会计区间');
    return
  } else if ((startQj != '' && endQj != '' && (startQj.substring(0, 4) != endQj.substring(0, 4)))) {
    message.error('暂不支持跨越年度会计区间查询');
    return
  } else if ((startRq != '' && endRq != '' && (startRq.substring(0, 4) != endRq.substring(0, 4)))) {
    message.error('暂不支持跨越年度日期区间查询');
    return
  }
  let pingS = modelList[defaultTabsKey.value].variable.voucherNumberStart
  let pingE = modelList[defaultTabsKey.value].variable.voucherNumberEnd
  if (pingS != '' || pingS != '') {
    if (pingS == '' || pingE == '') {
      message.error('请完善凭证编号区间条件!');
      return
    } else if (Number(pingE) < Number(pingS)) {
      message.error('凭证编号区间条件必须自小到大!');
      return
    }
  }
  let min = modelList[defaultTabsKey.value].variable.amountMin
  let max = modelList[defaultTabsKey.value].variable.amountMax
  if ((min != '' && max == '') || max != '' && min == '') {
    message.error('请完善本币金额区间条件!');
    return
  } else if (parseFloat(min) > parseFloat(max)) {
    message.error('本币金额区间条件必须自小到大!');
    return
  }
  let nMin = modelList[defaultTabsKey.value].variable.numberMin
  let nMax = modelList[defaultTabsKey.value].variable.numberMax
  if ((nMin != '' && nMax == '') || nMax != '' && nMin == '') {
    message.error('请完善数量区间条件!');
    return
  } else if (parseFloat(nMin) > parseFloat(nMax)) {
    message.error('数量条件必须自小到大!');
    return
  }

  let wbMin = modelList[defaultTabsKey.value].variable.wbAmountMin
  let wbMax = modelList[defaultTabsKey.value].variable.wbAmountMax
  if ((wbMin != '' && wbMax == '') || wbMax != '' && wbMin == '') {
    message.error('请完善外币金额区间条件!');
    return
  } else if (parseFloat(wbMin) > parseFloat(wbMax)) {
    message.error('外币金额区间条件必须自小到大!');
    return
  }

  modelList[defaultTabsKey.value].variable.periodStart = startQj
  modelList[defaultTabsKey.value].variable.periodEnd = endQj
  modelList[defaultTabsKey.value].variable.dateStart = startRq
  modelList[defaultTabsKey.value].variable.dateEnd = endRq
  // 存入自定义
  /*  if (defaultTabsKey.value == '1' || defaultTabsKey.value == '3') {
      if ((!hasBlank(dbQueryJson.id)) && modelNoChange('1')) { // 存在个人时不同步

      } else {
        modelList['3'] = modelList[defaultTabsKey.value]
        // 更新个人
        modifyDbPersonData(modelList['3']);
      }
    }*/
  modelList[defaultTabsKey.value].openOne = formItems.value.openOne
  modelList[defaultTabsKey.value].accAuthList = accAuthList.value
  modelList[defaultTabsKey.value].userAuthMap = userAuthMap.value
  modelLoadIng.value = true
  initAuthCondition(currentDynamicTenant.value.split('-')[0] + '-' + currentDynamicTenant.value.split('-')[1], userAuthMap.value)
  emit('save', unref(modelList[defaultTabsKey.value]))
  //closeModal()
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

let radiovalue = ref(1);
const riqi: any = ref([]);

// 过滤设置
const transferHandleChange = (nextTargetKeys: string[]) => {
  targetKeys.value = nextTargetKeys;
}
const transferHandleSelectChange = (sourceSelectedKeys: string[], targetSelectedKeys: string[]) => {
  selectedKeys.value = [...sourceSelectedKeys, ...targetSelectedKeys];
}
const transferHandleChange2 = (nextTargetKeys: string[]) => {
  targetKeys2.value = nextTargetKeys;
}
const transferHandleSelectChange2 = (sourceSelectedKeys: string[], targetSelectedKeys: string[]) => {
  selectedKeys2.value = [...sourceSelectedKeys, ...targetSelectedKeys];
}

const [registerInfo, {openModal: openInfoPageM}] = useModal()
const openSelection = () => {
  openInfoPageM(true, {
    data: {accAuthList: accAuthList.value}
  })
}
const dbQueryJson = reactive({ // 后台数据模型
  id: null,
  accountId: defaultAdName,
  owningMenuName: '凭证列表',
  owningPlan: '1',
  planPerson: userId,
  filterConditions: '',
  queryConditions: ''
})

/**
 * 针对与凭证查询条件清理无用参数
 */
const modifyDbPersonData = async (jsonData) => { // 异步更新后台最新记录
  let json = JSON.parse(JSON.stringify(jsonData))
  delete json['authority']
  delete json['constant']['thisAdInfo']
  json.variable = ObjTool.dels(json.variable, [])
  //dbQueryJson.owningPlan = json.constant.queryType
  dbQueryJson.filterConditions = JSON.stringify({
    'filter1': (targetKeys.value),
    'filter2': (targetKeys2.value)
  });
  dbQueryJson.queryConditions = JSON.stringify(json);
  modifyParameterValue(dbQueryJson).then(() => {
    message.success('查询参数后台同步成功！')
  }).catch(() => {
    message.warn('查询参数后台同步失败！')
  });
}

/**
 * 针对与凭证查询条件是否改变
 */
const modelNoChange = () => {
  let constant = modelList[defaultTabsKey.value].constant
  if (!hasBlank(dbQueryJson.queryConditions)) {
    let map = JSON.parse(dbQueryJson.queryConditions)
    let variableJSON = JSON.stringify(ObjTool.dels(modelList[defaultTabsKey.value].variable, []))
    let s = StrTool
    let flag = (s.eq(constant.companyCode, map.constant.companyCode))
    flag = flag && s.eq(constant.currency, map.constant.currency)
      && s.eq(variableJSON, JSON.stringify(map.variable))
    return flag;
  }
  return false;
}
/*辅助核算过滤框*/


</script>
<style src="../../../../../assets/styles/global-open.less" lang="less" scoped></style>
<style lang="less" scoped>
.nc-open-content {
  position: relative;

  .open-content-title {
    > div {
      display: inline-block;
    }

    > div:nth-of-type(1) {
      width: 190px;
      background-color: #efeeee;
      color: black;
      font-size: 20px;
      text-align: center;
      padding: 5px 10px
    }
  }

  ul {

    li {
      margin: 10px 0;
      color: #747272;

      .special-v-span {
        display: inline-flex;
        width: 100px;
        font-size: 14px;
      }


      .ant-select {
        font-size: 14px;
      }
    }
  }

  .open-content-foot {
    display: block;
    position: fixed;
    margin-top: 43px;
  }


  .ant-select-selection-search-input {
    border-bottom: none !important;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
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
</style>
