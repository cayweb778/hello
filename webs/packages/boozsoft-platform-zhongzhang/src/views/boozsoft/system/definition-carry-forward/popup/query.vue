<template>
  <BasicModal
    destroyOnClose
    width="800px"
    class="spaceLogo"
    v-bind="$attrs"
    title="期间损益结转"
    @ok="handleOk()"
    @cancel="handleClose()"
    :loading="modelLoadIng"
    :canFullscreen="false"
    @visible-change="openOrClose"
    @register="register"
  >
    <template #title>
      <div style="display: flex;color: #0096c7;">
        <span style="line-height:40px;font-size: 28px;">
          <RotateRightOutlined style="font-size: 34px;font-weight: bold"/>
        </span>
        <span style="line-height:40px;font-size: 28px;margin-top:-5px;">
          &nbsp;&nbsp;期间损益结转
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="../../../../../assets/images/666.png" style="height:100px;margin-right: 55px;"/>
      </div>
    </template>
    <div class="nc-open-content" style="height: 100%">
      <div class="open-content-up">
        <br/>
        <label style="font-size: 18px;margin-left: 70px;width:150px;">
          <AccountPicker theme="one" @reloadTable="codeChange" />
        </label>
        <br/><br/><br/>
        <label style="margin-left: 70px;">结转期间：</label>
        <a-select
          v-model:value="modelList['1'].variable.period"
          show-search
          style="width: 150px;"
        >
          <a-select-option
            v-for="item in dateList"
            :key="item.id"
            :value="item.value"
          >{{ item.value }}
          </a-select-option>
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </a-select>
        <label style="margin-left: 70px;">凭证类型：</label>
        <a-select
          v-model:value="modelList['1'].variable.voucherType"
          placeholder=""
          style="width: 150px"
          :allowClear="true">
          <a-select-option v-for="(item,index) in typesList" :key="index" :value="item.voucherTypeCode">{{ item.voucherTypeCode }}</a-select-option>
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </a-select>
        <br/>
        <label style="margin-left: 70px;">科目级次：</label>
        <a-select show-search style="width: 150px;" v-model:value="modelList['1'].variable.carryOverlevel" :allowClear="true">
          <a-select-option value="1">一级科目</a-select-option>
          <a-select-option value="2">末级科目</a-select-option>
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </a-select>
        <br/>
        <label style="margin-left: 70px;">本年利润科目：</label>
        <a-select show-search style="width: 61%" v-model:value="modelList['1'].variable.yearProfitCode" :allowClear="true" :filter-option="filterOption">
          <a-select-option v-for="item in kmList" :key="item.ccode+'-'+item.ccodeName" :value="item.ccode">{{ item.ccode }} {{ item.ccodeName }}</a-select-option>
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </a-select>
        <label style="margin-left: 70px;">凭证摘要：</label>
        {{modelList['1'].variable.period || '结转期间'}}
        <a-input style="width: 412px;" v-model:value="digest"/>
        <br/>
        <label style="margin-left: 70px;">包含未记账：</label>
        <a-checkbox v-model:checked="ishaveRjz" style="width: 150px;margin-left: 0px;" />
      </div>
    </div>
  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import { DownloadOutlined,CloudUploadOutlined,CaretDownOutlined,EllipsisOutlined,RotateRightOutlined } from '@ant-design/icons-vue';
import {ref, unref, reactive, onMounted, watch, computed} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {
  Select as ASelect,
  Checkbox as ACheckbox,
  Input as AInput,
  Tabs as ATabs, message
} from "ant-design-vue"
import {useTabs} from '/@/hooks/web/useTabs';
import router from "/@/router";
import {Moment} from 'moment';
import {
  findPeriodByAccontId,
  findCodeKmByPeriod, findMaxPingZhengQiJian, findBzAll
} from '/@/api/record/generalLedger/data';
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {
  currentAccountTypes,
} from "/@/api/record/system/financial-settings";
import {useUserStore} from "/@/store/modules/user";
import {
  closeAnomaly,
  getCurrentAccountName,
  hasBlank,
  pointMessage
} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useMessage} from "/@/hooks/web/useMessage";
import {findParameterValue, modifyParameterValue} from "/@/api/task-api/query-bus-api";
import {ObjTool, StrTool} from "/@/api/task-api/tools/universal-tools";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {company_findByBenNianCode} from "/@/api/codekemu/codekemu";
import {findByAccIdAccStyle} from "/@/api/accstandard/accstandard";
import {findByAccIdSunYiAndQuanYi} from "/@/api/record/system/account";
const {closeCurrent} = useTabs(router);

const emit = defineEmits(['register', 'save'])
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ATabPane = ATabs.TabPane

const userStore = useUserStore();

const {createWarningModal} = useMessage();

const bz: any = ref('全部')
const bzList: any = ref([])
const digest: any = ref('期间损益结转')
const ishaveRjz: any = ref(true)
const formItems: any = ref({})
let changeBeforeModel: any = {}
// 会计区间
const dateList: any = ref([])
const styleList: any = ref([])
// 会计科目
const kmList: any = ref([])
// 类别
const typesList: any = ref([])
const userId = userStore.getUserInfo?.id
const currentDynamicTenant = ref('')
const { createConfirm} = useMessage();
const defaultTabsKey = ref('1')
const whetherGroup = ref(false)
const modelLoadIng = ref(false)
const accId = ref(getCurrentAccountName(false))
const dynamicTenantId = ref(getCurrentAccountName(true))
const accYear = ref('')
const database_id = ref('')
const codeLevelFirst = ref('')


const [register, {closeModal, setModalProps}] = useModalInner((data) => {
  // 方式2
  formItems.value = data.data
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
  setModalProps({minHeight: 300});
})

// 判断 科目类型是否包含 权益、损益
async function isQiyi() {
  let a=true
  let QuanyiSunyiStyle=await useRouteApi(findByAccIdSunYiAndQuanYi, {schemaName: dynamicTenantId})(database_id.value);
  if(QuanyiSunyiStyle.length!==2){
    createConfirm({
      iconType: 'warning',
      title: '温馨提示',
      content: '此账套非企业会计制度,不能期间结转！',
      onOk: async () => {}
    });
    a=false
  }
  return a;
}

const codeChange = async (value) => {
  codeLevelFirst.value=value.target.ccodeLevel
  accYear.value=value.iyear
  accId.value=value.accId
  dynamicTenantId.value = value.accountMode
  database_id.value = value.target.id
  if(!isQiyi()){return}
  let newName = value.accId
  // 查看是否存在查询参数
  dbQueryJson.accountId = newName;
  // 获取已存在查询数据
  // let dbQueryData = await findParameterValue(dbQueryJson)
  // if (null != dbQueryData) { // 存在
  //   defaultTabsKey.value = '3'
  //   dbQueryJson.id = dbQueryData.id
  //   dbQueryJson.queryConditions = dbQueryData.queryConditions
  //   if (!hasBlank(dbQueryData.queryConditions)) {
  //     let queryMap = JSON.parse(dbQueryData.queryConditions)
  //     modelList["3"].constant = queryMap.constant
  //     modelList["3"].variable = queryMap.variable
  //   }
  // } else {
    // 清除条件
    modelList[defaultTabsKey.value].constant = defaultModel.constant
    modelList[defaultTabsKey.value].variable = defaultModel.variable
    dbQueryJson.id = null
  // }
  modelList[defaultTabsKey.value].constant.tenantId = value.accountMode
  currentDynamicTenant.value = value.accountMode
  dynamicTenantId.value=value.accountMode
  reLifeQueryParameter(currentDynamicTenant.value)

  //币种
  let currency=await useRouteApi(findBzAll, {schemaName: value.accountMode})('');
  bzList.value = currency.filter(c => c.currChName.indexOf('人民币') < 0);
  bzList.value.push({id: '', currChName: '全部'});
}

const reLifeQueryParameter = async (dynamicTenant) => {
  if (hasBlank(dynamicTenant)) return
  // 获取最大凭证区间
  let tenantName = dynamicTenant.split('-')[0] + '-' + dynamicTenant.split('-')[1]
  //加载期间数据
  dateList.value = []
  let temp = await findPeriodByAccontId(tenantName);
  dateList.value=temp.filter(v=>v.gl!=='1');
  if (dateList.value.length > 0) {
    const qijian = await useRouteApi(findMaxPingZhengQiJian, {schemaName: dynamicTenant})({}) || '';
    // 初始化默认区间
    if (hasBlank(qijian)) {
      modelList[defaultTabsKey.value].variable.period = dateList.value[0].value
    } else {
      // 获取已存在查询数据
      let dbQueryData = await findParameterValue(dbQueryJson)
      if(null==dbQueryData){
        modelList[defaultTabsKey.value].variable.period = qijian.substring(0, 4) + '-' + qijian.substring(4, 6)
      }
    }
    await reLifeKmList(currentDynamicTenant.value)
    await reLifeTypesList(currentDynamicTenant.value)
  } else {
    await pointMessage({title: '温馨提示', content: '请先初始化该公司代码账套会计期间后在进行查询！', delay: true})
    currentDynamicTenant.value = ''
  }
}

async function reLifeKmList(dynamicTenant) {
  if (hasBlank(dynamicTenant)) return

  let ccode=await useRouteApi(company_findByBenNianCode,{schemaName: dynamicTenant})('')
  let findByCcode=ccode.filter(v=>v.ccodeName==='本年利润')
  if(findByCcode.length==0){
    kmList.value = ccode
  }else{
    kmList.value=findByCcode
  }
  modelList['1'].variable.yearProfitCode=kmList.value.length>0?kmList.value[0].ccode+'-'+kmList.value[0].ccodeName:''
  defaultModel.variable.yearProfitCode=kmList.value.length>0?kmList.value[0].ccode+'-'+kmList.value[0].ccodeName:''
}

async function reLifeTypesList(dynamicTenant) {
  if (hasBlank(dynamicTenant)) return
  typesList.value = []
  await useRouteApi(currentAccountTypes, {schemaName: dynamicTenant})({
    pageSize: 20,
    pageNumber: 1
  }).then(res => {
    typesList.value = res.items
  })
}

const defaultModel = {
  constant: {
    tenantId: '',
    thisAdInfo: {}
  },
  variable: {
    period: '',
    voucherType: '',
    carryOverlevel: '2',
    incomeAndExpenditure: '',
    yearProfitCode:'',
  }
}
const modelList = reactive({
  '1': {
    constant: {
      tenantId: '',
      thisAdInfo: {}
    },
    variable: {
      period: '',
      voucherType: '',
      carryOverlevel: '2',
      incomeAndExpenditure: '',
      yearProfitCode:'',
    }
  },
  '2': {
  },
  '3': {
    constant: {
      tenantId: '',
      thisAdInfo: {}
    },
    variable: {
      period: '',
      voucherType: '',
      carryOverlevel: '2',
      incomeAndExpenditure: '',
      yearProfitCode:'',
    }
  },
  '4': {}
})

const openOrClose = (visible: boolean) => {
  // 初始化选中
  if (visible && null != formItems.value.openOne && Object.keys(modelList['3']).length != 0) {
    //默认赋值
    defaultTabsKey.value = '3'
  }
}
const dbQueryJson = reactive({ // 后台数据模型
  id: null,
  accountId: currentDynamicTenant.value,
  owningMenuName: '期间损益结转',
  owningPlan: '1',
  planPerson: userId,
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
    let flag =  (s.eq(constant.tenantId, map.constant.tenantId) && s.eq(variableJSON, JSON.stringify(map.variable)))
    return flag;
  }
  return false;
}
/*辅助核算过滤框*/
async function handleOk() {
  let QuanyiSunyiStyle=await useRouteApi(findByAccIdSunYiAndQuanYi, {schemaName: dynamicTenantId})(database_id.value);
  if(QuanyiSunyiStyle.length!==2){
    createConfirm({
      iconType: 'warning',
      title: '温馨提示',
      content: '此账套非企业会计制度,不能期间结转！',
      onOk: async () => {}
    });
    return
  }

  await findByStyle()
  modelLoadIng.value = false
  if (hasBlank(currentDynamicTenant.value)) {
    message.error('请选择公司代码!');
    return
  }else if (dateList.value.length === 0) {
    message.error('请先初始化会计期间后再进行查询操作!');
    return
  }

  if (hasBlank(digest.value)){
    createWarningModal({title: '温馨提示',content: '凭证摘要为必填项'})
  }else if (hasBlank(modelList[defaultTabsKey.value].variable.period)){
    createWarningModal({title: '温馨提示',content: '结转期间为必选项'})
  }else if (hasBlank(modelList[defaultTabsKey.value].variable.carryOverlevel)){
    createWarningModal({title: '温馨提示',content: '结转科目级次为必选项'})
  }else if (hasBlank(modelList[defaultTabsKey.value].variable.yearProfitCode)){
    createWarningModal({title: '温馨提示',content: '本年利润科目为必选项'})
  }else{
    if (defaultTabsKey.value == '1' || defaultTabsKey.value == '3') {
      if ((!hasBlank(dbQueryJson.id)) && modelNoChange('1')) { // 存在个人时不同步

      } else {
        modelList['3'] = modelList[defaultTabsKey.value]
        // modifyDbPersonData(modelList['3']);
      }
    }

    modelList[defaultTabsKey.value].openOne = formItems.value.openOne
    modelLoadIng.value = true
    modelList[defaultTabsKey.value].styleList = styleList.value
    modelList[defaultTabsKey.value].bz = bz.value
    modelList[defaultTabsKey.value].digest = digest.value
    modelList[defaultTabsKey.value].ishaveRjz = ishaveRjz.value===true?'1':'0'
    let code=kmList.value.filter(item=>item.ccode===modelList[defaultTabsKey.value].variable.yearProfitCode.split('-')[0])
    // 利润科目方向
    modelList[defaultTabsKey.value].lrCodeBprogerty = code[0].bprogerty
    modelList[defaultTabsKey.value].codeLevelFirst = codeLevelFirst.value
    emit('save', unref(modelList[defaultTabsKey.value]))
    return true
  }
}
// 监听页面反馈
const totalChange = computed(() => formItems.value.total)
watch(totalChange, async (a) => {
    if (a != 0) {
      modelLoadIng.value = false
      /*if (a > 0) {*/
      closeModal()
      /* } else {
         createWarningModal({title: '温馨提示', content: '暂无任何查询结果！'});
       }*/
      formItems.value.total = 0
    }
  }
)
async function handleClose() {
  if (null != formItems.value.openOne && formItems.value.openOne == 1) {
    await closeCurrent()
    router.push('/zhongZhang/home/welcome');
  }
  return true;
}

 async function findByStyle() {
   styleList.value= await useRouteApi(findByAccIdAccStyle, {schemaName: dynamicTenantId.value})(accId.value)
}

const filterOption = (input: string, option: any) => {
  return option.key.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};
</script>
<style>
.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid #0096c7;
  border-left: none;
  border-right: none;
  border-top: none !important;
}
.ant-modal-header{
  border: none !important;
}
</style>
<style lang="less" scoped>
:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
  font-weight: bold;
}

:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none !important;
}

.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}

.red_span {
  color: red;
  font-weight: bold;
  display: inline-block;
  width: 20px;
  text-align: right;
}

.nc-open-content {

  input {
    width: 30%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  .abc{
    border-bottom: 2px solid #666666 !important;
    font-size: 18px;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  label {
    text-align: left;
    width: 120px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 2em;
    color: #535353;
    font-size: 13px;
    margin-left: 2em;
    font-weight: bold;
  }

  .open-content-down {
    margin-top: 5%;

    .ant-tabs-tab-active::before {
      position: absolute;
      top: 0px;
      left: 0;
      width: 100%;
      border-top: 2px solid transparent;
      border-radius: 2px 2px 0 0;
      transition: all 0.3s;
      content: '';
      pointer-events: none;
      background-color: rgb(1, 143, 251);
    }
  }
}

.nc-open-show-content {

  input {
    width: 30%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  .abc{
    border-bottom: 2px solid #666666 !important;
    font-size: 18px;
  }

  pointer-events: none;
  cursor: default;

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  label {
    text-align: left;
    width: 120px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 2em;
    color: #535353;
    font-size: 13px;
    margin-left: 2em;
    font-weight: bold;
  }

  .open-content-down {
    margin-top: 5%;

    :deep(.ant-tabs-tab) {
      pointer-events: auto;
    }

    .ant-tabs-tab-active::before {
      position: absolute;
      top: 0px;
      left: 0;
      width: 100%;
      border-top: 2px solid transparent;
      border-radius: 2px 2px 0 0;
      transition: all 0.3s;
      content: '';
      background-color: rgb(1, 143, 251);
    }
  }
}

:deep(.ant-tabs.ant-tabs-card .ant-tabs-card-bar .ant-tabs-tab) {
  height: 40px;
  margin: 0;
  margin-right: 2px;
  padding: 0 16px;
  line-height: 38px;
  background: #fafafa;
  border: 1px solid #f0f0f0;
  border-radius: 2px 2px 0 0;
  transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  font-weight: bold;
  font-size: 13px;
}

</style>
