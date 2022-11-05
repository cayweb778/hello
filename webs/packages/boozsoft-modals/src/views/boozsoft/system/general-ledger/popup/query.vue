<template>
  <BasicModal
    width="880px"
    class="spaceLogo"
    v-bind="$attrs"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
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
      <span style="line-height:30px;font-size: 24px;color: #0096c7;font-weight: bold;">&ensp;总账</span>
      <div class="open-content-up">
        <div class="ocup-position">
          <PicLeftOutlined/>
          查询方案
        </div>
        <a-tabs type="card" v-model:activeKey="activeKey" tabPosition="left" @change="tabChange">
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
                      <span class="right_span">会计期间：</span>
                      <a-select
                        v-model:value="strDate"
                        show-search
                        style="width: 100px;"
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
                        style="width: 100px"
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
                </div>
              </li>
              <li>
                <div class="special-border-div">
                  <span style="color: #5a5a5a">查询条件</span>
                  <div style="display: inline-block;width: 96%;margin-left: 23px;">
                    <div style="width: 50%;float: left;">
                      <span class="right_span">科目类型：</span>
                      <a-select v-model:value="styleValue" style="width: 160px;text-align: center;" @change="styleChange">
                        <a-select-option v-for="d in styleList" :value="d.cclass">
                          {{ d.cclass }}
                        </a-select-option>
                        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                      </a-select>
                    </div>
                    <div style="width: 50%;float: left;">
                      <span class="right_span">凭证类别：</span>
                      <a-select v-model:value="pzType" style="width: 160px;text-align: center">
                        <a-select-option v-for="d in pzTypeList" :value="d">
                          {{ d }}
                        </a-select-option>
                        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                      </a-select>
                    </div>
                    <div style="float: left;">
                      <span class="right_span">科目：</span>
                      <a-select
                        v-model:value="minKm"
                        :filter-option="filterOption"
                        allowClear
                        option-filter-prop="children"
                        show-search
                        style="width: 210px;"
                        @change="handleChangeMinKm"
                        @focus="handleChangeMinKm"
                        :class="minkmList.length==0?'selectColorRed':'selectColorBlack'"
                      >
                        <a-select-option v-for="d in minkmList" :value="d.ccode">
                          {{ d.value }}
                        </a-select-option>
                        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                      </a-select>
                      <span>&emsp;-&emsp;</span>
                      <a-select
                        v-model:value="maxKm"
                        :filter-option="filterOption"
                        allowClear
                        option-filter-prop="children"
                        show-search
                        style="width: 210px;"
                        @focus="handleFocusMaxKm"
                        :class="minkmList.length==0?'selectColorRed':'selectColorBlack'"
                      >
                        <a-select-option v-for="d in maxkmList" :value="d.ccode">
                          {{ d.value }}
                        </a-select-option>
                        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                      </a-select>
                    </div>

                    <div >
                      <a-radio-group style="width: 100%" v-model:value="jc">
                        <a-radio :value="1" style="width: 60%;float: left;display: flex;">级次</a-radio>
                        <span style="margin-left: 25px;">
                          <a-select
                            v-model:value="minJc"
                            @change="handleChangeMinCj"
                            default-value="1"
                            style="width: 80px;margin-left: -41%"
                          >
                              <a-select-option v-for="d in minJcList" :value="d.value">
                                {{ d.label }}
                              </a-select-option>
                              <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                            </a-select>
                          <span>&emsp;-&emsp;</span>
                          <a-select
                            v-model:value="maxJc"
                            default-value="1"
                            @focus="handleFocusMaxCj"
                            style="width: 80px;"
                          >
                          <a-select-option v-for="d in maxJcList" :value="d.value">
                            {{ d.label }}
                          </a-select-option>
                          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                        </a-select>
                        </span>
                        <a-radio :value="2" style="position: absolute;margin-left: 25px;">
                          <span>末级科目</span>
                        </a-radio>
                      </a-radio-group>
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
import {ref, unref, onMounted, reactive} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {PageWrapper} from '/@/components/Page'
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Checkbox as ACheckbox,
  Radio as ARadio,
  Tabs as ATabs, message
} from "ant-design-vue"
import {
  AppstoreOutlined,
  CaretDownOutlined
} from '@ant-design/icons-vue'
import CheckboxGroup from "ant-design-vue/lib/checkbox/Group";
import { useTabs } from '/@/hooks/web/useTabs';
import router from "/@/router";
import moment, { Moment } from 'moment';
import {
  findPeriodByAccontId,
  findBzAll,
  findMaxJc,
  findCodeKmByPeriod
} from '/@/api/record/generalLedger/data';
import {findByImgid} from "/@/api/record/eletronicInvoice_data/eletronic_invoice";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {finByParameterAccuracy} from "/@/api/record/km_mingxi/data";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getCurrentAccountName, hasBlank} from "/@/api/task-api/tast-bus-api";
import {
  currentAccountTypes,
  getAdInfoDatas,
  getAllAccAuths
} from "/@/api/record/system/financial-settings";
import {
  company_findAllStyleByUnique,
  findAllStyleByUnique,
  findByAccStyle,
  findByStandardUnique
} from "/@/api/accstandard/accstandard";
import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {groupStandardAndTemplate} from "/@/api/codekemu/codekemu";
import {company_findByAccTypeAuth, findByAccId, findDataBase} from "/@/api/record/system/account";
import {findByQueryPlan, saveQueryPlan} from "/@/api/query-plan/queryPlan";
import {useNcLogger} from "/@/utils/boozsoft/record/recordUtils";
import CustomizeYear from "/@/boozsoft/components/CustomizeYear/CustomizeYear.vue";
import {findAllByUniqueAccStandardAndTemplateIdAndCclass, findByTOrganization} from "/@/api/acctemplate/acctemplate";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
import {findAvailablesOrg} from "/@/api/boozsoft/group/UsedForeignCurrency";
import {getOrganizeAll} from "/@/api/record/group/im-organize";
import {findByAccIdAndIyear} from "/@/api/record/system/sys_data_auth_swith";
import {saveLog} from "/@/api/record/system/group-sys-login-log";

const userStore = useUserStore();
const { closeCurrent } = useTabs(router);
const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const ATabPane = ATabs.TabPane
const emit=defineEmits([])
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
const year = new Date().getFullYear();
const years = useCompanyOperateStoreWidthOut().getLoginDate.split('-')[0];
const month = useCompanyOperateStoreWidthOut().getLoginDate.split('-')[1];
const endDate = ref<String>(years+'-'+month)
const strDate = ref<String>(years + '-01');
let endDateList: any = ref([])
let strDateList: any = ref([])
const pzType: any = ref([]);

let loading: any = ref(true)

const pzTypeList: any = ref([]);
let minkmList: any = ref([])
let maxkmList: any = ref([])
const minKm = ref<string>()
const maxKm = ref<string>()

const jc = ref<number>(1)
let maxJcList: any = ref([])
let minJcList: any = ref([])
const minJc = ref<number>(1)
const maxJc = ref<number>(1)

const showStyle = ref<string>('J')
const fontSize = ref<string>('MIN')
const bz = ref('0')
const jd = ref<number>(2)
const ishaveRjz = ref<boolean>(true)
const isShowQjlh = ref<boolean>(false)

const kmKj = ref<string>('')
const isType = ref<string>('')
const isCode = ref<string>('')
const userId = ref<string>('')
const showStyleList = [
  {
    'name': '金额式',
    'value': 'J'
  },{
    'name': '数量金额式',
    'value': 'SJ'
  },{
    'name': '外币金额式',
    'value': 'WJ'
  },{
    'name': '数量外币式',
    'value': 'SWJ'
  }
]
let styleValue: any = ref('全部');
let styleList: any = ref([]);
const queryPlan: any = ref([]);
const activeKey: any = ref('1');
const databaseyear = ref(year);
// 页面变量
const pageParameter = reactive({
  companyCode: '',
  companyName: '',
})
//查询条件
const seach : any = ref({})

const [register, {closeModal, setModalProps, changeOkLoading}] = useModalInner((data) => {
  // 方式2
  formItems.value.openOne = data.data.openOne
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
  // getQueryPlan()
  setModalProps({minHeight: 400});
  if(loading.value === true){
    changeOkLoading(true);
  }
})
let isChanged: boolean = false

//获取当前年月
const nowDate = ()=>{
  const nowDate = new Date();
  const date = {
    year: nowDate.getFullYear(),
    month: nowDate.getMonth() + 1,
    date: nowDate.getDate(),
  }
  const newmonth = date.month>10?date.month:date.month
  return date.year + '-' + newmonth
}
const recordName = 'general-ledger'
const {ncLogger} = useNcLogger({recordName})
// 数据库模式名称
const databases = ref(getCurrentAccountName(false))
const accId = ref(getCurrentAccountName(true))
async function handleOk() {

  if (activeKey.value !== '0') {
    formItems.value.queryMode = '1'
    formItems.value.querytype='gs'
    formItems.value.styleList = styleValue.value!=='全部'?styleList.value.filter(s=>s.cclass===styleValue.value):styleList.value.filter(s=>s.cclass!==styleValue.value);
  }else{
    // 集团模式时
    if (accIds.value.length === 0) {
      message.error('至少选择一家核算单位!');
      return;
    }
    if (orgDates.value.length === 0) {
      message.error('请选择查询日期!');
      return;
    }
    formItems.value.queryMode = '0'
    let test=accAuthList.value.filter(ite=>ite.independent != '1')
    formItems.value.accIds = test.filter(v=>accIds.value.indexOf(v.coCode)!==-1).map(item=>item.accId)
    strDate.value=timeformat(orgDates.value[0]._d)
    endDate.value=timeformat(orgDates.value[1]._d)
    formItems.value.styleList = styleValue.value!=='全部'?orgCodeTypeList.value.filter(s=>s===styleValue.value):orgCodeTypeList.value.filter(s=>s!==styleValue.value);
    formItems.value.querytype='jt'
  }

  if(strDate.value.length <= 0 && endDate.value <= 0 ){
    message.error('请选择会计区间!');
    return
  }
  //kmList 不存在 不让查询
  if(kmList.value.length === 0){
    message.error('您没有科目权限！')
    return false
  }
  formItems.value.strDate = strDate.value
  formItems.value.endDate = endDate.value

  formItems.value.minKm = minKm.value
  formItems.value.maxKm = maxKm.value

  formItems.value.jc = jc.value
  if(jc.value === 1){
    formItems.value.minJc = minJc.value
    formItems.value.maxJc = maxJc.value
  }

  formItems.value.showStyle = showStyle.value
  formItems.value.fontSize = fontSize.value
  formItems.value.bz = bz.value
  formItems.value.bzName = bzList.value.filter(o=> o.id === bz.value)[0].currChName
  formItems.value.bzName = formItems.value.bzName.replace('(本位币)','')
  formItems.value.ishaveRjz = ishaveRjz.value
  formItems.value.isShowQjlh = isShowQjlh.value
  formItems.value.jd = jd.value

  let test=accAuthList.value.filter(ite=>ite.independent != '1')
  formItems.value.accIds = test.filter(v=>accIds.value.indexOf(v.coCode)!==-1).map(item=>item.accId)

  //科目 类别权限
  formItems.value.isType = isType.value
  formItems.value.isCode = isCode.value
  formItems.value.userId = userId.value

  formItems.value.accId = databases.value
  // 过滤会计科目
  let list = kmList.value

  if(maxKm.value){
    list = list.filter(o =>  o.ccode <= maxKm.value)
  }
  if(minKm.value){
    list = list.filter(o =>  o.ccode >= minKm.value)
  }

  if(jc.value === 2){
    list = list.filter(o =>  o.bend === '1')
  }else{
    if(minJc.value){
      list = list.filter(o =>  minJc.value <= o.igrade)
    }
    if(maxJc.value){
      list = list.filter(o =>  o.igrade <= maxJc.value)
    }
  }
  formItems.value.kmList = list;
  formItems.value.styleList = styleValue.value!=='全部'?styleList.value.filter(s=>s.cclass===styleValue.value):styleList.value.filter(s=>s.cclass!==styleValue.value);
  formItems.value.pageParameter = pageParameter;
  formItems.value.styleValue = styleValue.value;
  formItems.value.pzType = pzType.value;

  const queryPlanEntity={
    accountId:databaseTrue.value,
    owningMenuName:'总账',
    owningPlan:activeKey.value,
    planPerson:userStore.getUserInfo.id,
    queryConditions:{
      ishaveRjz:formItems.value.ishaveRjz,
      bend:formItems.value.jc,
      strDate:formItems.value.strDate,
      endDate:formItems.value.endDate,
      minJc:formItems.value.minJc,
      maxJc:formItems.value.maxJc,
      minKm:formItems.value.minKm,
      maxKm:formItems.value.maxKm,
      bz:formItems.value.bz,
      fontSize:formItems.value.fontSize,
      styleValue:formItems.value.styleValue,
      pzType:pzType.value
    }
  }
  // await saveQueryPlan(queryPlanEntity);
  /************** 记录操作日志 ****************/
  let map={
    userId: useUserStoreWidthOut().getUserInfo.id,
    userName: useUserStoreWidthOut().getUserInfo.realName,
    optModule:'gl',
    optFunction:'总账',
    optRange:'1',// 1 账套
    uniqueCode:accId.value,
    optAction:'查询',
    optContent:'操作内容【总账查询】,账套代码【'+pageParameter.companyCode+'】,账套名称【'+pageParameter.companyName+'】,查询条件【查询日期：'+strDate.value+','+endDate.value+'】,' +
      '科目类型【'+styleValue.value+'】,凭证类别【'+pzType.value+'】,会计科目【'+minKm.value+','+maxKm.value+'】',
  }
  await saveLog(map)
  /************** 记录操作日志 END ****************/
  //添加日志
  // ncLogger.info("总账查询:"+JSON.stringify(queryPlanEntity))
  emit('save', {data: unref(formItems), accAuthList: accAuthList.value});
  closeModal()
  return true
}

function timeformat(dateData) {
  let date = new Date(dateData);
  let y = date.getFullYear();
  let m = date.getMonth() + 1;
  m = m < 10 ? '0' + m : m;
  let d = date.getDate();
  d = d < 10 ? '0' + d : d;
  return y + '-' + m ;
}

async function handleClose() {
  if (null != formItems.value.openOne && formItems.value.openOne == 1) {
    await closeCurrent();
    router.push('/zhongZhang/home/welcome');
  }
}

async function handleChangeStrDate() {
  // 开始日期变动  如果开始日期大于结束于日期 则重置结束日期
  if(endDate.value){
    if(strDate.value > endDate.value){
      endDate.value = strDate.value
    }
    userId.value = userStore.getUserInfo.id
    // 会计科目
    kmList.value = await useRouteApi(findCodeKmByPeriod,{schemaName:accId.value})({
      strDate: strDate.value,
      endDate: endDate.value,
      accId: databases.value,
      userId: userId.value,
    });
  }
}
async function handleChangeEndDate() {
  // 开始日期变动  如果结束日期小于开始日期 则重置开始日期
  if(strDate.value){
    if(strDate.value > endDate.value){
      strDate.value = endDate.value
    }
    // 会计科目
    userId.value = userStore.getUserInfo.id
    // 会计科目
    kmList.value = await useRouteApi(findCodeKmByPeriod,{schemaName:accId.value})({
      strDate: strDate.value,
      endDate: endDate.value,
      accId: databases.value,
      userId: userId.value,
    });
  }
}

async function focusEndDate() {
  if(strDate.value){
    endDateList.value = dateList.value.filter( o => o.value >= strDate.value)
  }else{
    endDateList.value = dateList.value
  }
}

const handleChangeMinKm = () => {
  if(maxKm.value && maxKm.value < minKm.value){
    maxKm.value = minKm.value
  }
};
const handleFocusMaxKm = () => {
  //获取焦点时 如果最小科目已选择 判断过滤小于最小科目的数据
  if(minKm.value){
    maxkmList.value = kmList.value.filter( o => o.ccode >= minKm.value)
  }else{
    maxkmList.value = minkmList.value
  }
};

const filterOption = (input: string, option: any) => {
  return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
}

const handleChangeMinCj = () => {
  if(maxJc.value && maxJc.value < minJc.value){
    maxJc.value = minJc.value
  }
};

const handleFocusMaxCj = () => {
  //获取焦点时 如果最小科目已选择 判断过滤小于最小科目的数据
  if(minJc.value){
    maxJcList.value = jcList.value.filter( o => o.value >= minJc.value)
  }else{
    maxJcList.value = jcList.value
  }
};
const handleChangeJc = () => {
  // 过滤会计科目
  let list = minkmList.value
  if(jc.value === 2){
    list = list.filter(o =>  o.bend === '1')
  }else{
    if(minJc.value){
      list = list.filter(o =>  minJc.value <= o.igrade)
    }
    if(maxJc.value){
      list = list.filter(o =>  o.igrade <= maxJc.value)
    }
  }
  formItems.value.kmList = list;
};

const whetherGroup = ref(false)
// 数据库模式名称
const database = ref(getCurrentAccountName(false))

async function newfindAllCode(s,start) {
  // 会计科目
  let list=await useRouteApi(findCodeKmByPeriod,{schemaName:accId.value})({ strDate: start, endDate: 'test', accId: database.value,userId:userStore.getUserInfo.id });
  return list;
}
function tabChange(val) {
  if(val===''){             //----------------------------标准
    ishaveRjz.value = true
    jc.value = 1
    strDate.value = year + '-01'
    endDate.value = year + '-' + month
    minJc.value = 1
    maxJc.value = 1
    minKm.value = ''
    maxKm.value = ''
    bz.value = '0'
    styleValue.value = '全部'
    fontSize.value = 'MIN'
  }else if(val==='1'){     //----------------------------个人
    getQueryPlan()
  }else if (val === '0'){       //----------------------------集团
    if ('' == naturalYear.value){
      naturalYear.value = new Date().getFullYear()+''
      customizeChange(naturalYear.value)
      findByDateBase()
    }
  }
}

async function styleChange(val) {
  const s = databases.value+'-'+ strDate.value.split('-')[0];
  // 重新筛选科目类型
  let accstyle =await useRouteApi(company_findAllStyleByUnique,{schemaName:accId.value})('');
  if(val!=='全部'){
    minKm.value = ''
    maxKm.value = ''
    kmList.value = []
    maxkmList.value = []
    if(val!=='财务会计'&&val!=='预算会计'){
      kmList.value = minkmList.value.filter(ck=>ck.cclass===val)
      maxkmList.value =  kmList.value
    }else if(val=='财务会计'){
      const culist = accstyle.filter(ck=>ck.flagYusuan != '1')
      kmList.value = minkmList.value.filter(ck=> culist.findIndex(v=> v.cclass ===ck.cclass) >= 0)
      maxkmList.value = kmList.value
    }else if(val=='预算会计'){
      const yslist = accstyle.filter(ck=>ck.flagYusuan === '1')
      kmList.value = minkmList.value.filter(ck=> yslist.findIndex(v=> v.cclass ===ck.cclass) >= 0)
      maxkmList.value = kmList.value
    }
  }else{
    kmList.value = minkmList.value
    maxkmList.value =  minkmList.value
  }

}

const databaseTrue = ref(getCurrentAccountName(true));

onMounted( ()=>{
  AllCondition();
})
async function dynamicAdReload(data) {
  database.value=data.accId
  pageParameter.accId=data.accId
  pageParameter.database=data.accId+'-'+data.year
  databaseyear.value=data.year
  pageParameter.accNameAll=data.target.accName
  if(databaseTrue.value != data.accId+'-'+data.year){
    changeOkLoading(true);
    loading.value  = true
    bz.value = '0'
    AllCondition();
  }
  databaseTrue.value =data.accId+'-'+data.year

}
async function AllCondition() {
  findByDateBase()
  //加载数据
  dateList.value = await findPeriodByAccontId(database.value);
  // 设置默认期间
  // 如果没有当前年度 获取 上年第一个区间
  const test = dateList.value.filter((o) => o.iyear == year);
  if (test.length == 0) {
    strDate.value = dateList.value[0].value;
    endDate.value = dateList.value[0].value;
  }

  userId.value = userStore.getUserInfo.id
  // 会计科目
  kmList.value = await useRouteApi(findCodeKmByPeriod,{schemaName:databaseTrue.value})({
    strDate: strDate.value,
    endDate: endDate.value,
    accId: database.value,
    userId: userId.value,
  });
  minkmList.value = kmList.value

  //币种
  bzList.value = await useRouteApi(findBzAll,{schemaName:databaseTrue.value})()
  bzList.value.push({ id: '0', currChName: '全部',standCurr:'0' });


  // 设置默认选中
  const  sel = bzList.value.filter(o=> o.standCurr === '1')
  if(sel.length > 0){
    bz.value = sel[0].id
  }

  //级次
  jcList.value = []
  const num = await findMaxJc(database.value,databaseyear.value);
  for (let i = 1; i < num + 1; i++) {
    jcList.value.push({
      label: i,
      value: i,
    });
  }

  //计算精度
  const jdlist = await useRouteApi(finByParameterAccuracy,{schemaName:databaseTrue.value})('')
  const jds = jdlist.filter(o=> o.paraNum === '数量');
  if(jds.length > 0){
    jd.value = jds[0].decimalPlaces;
  }

  //kmList 不存在 不让查询
  if(kmList.value.length === 0){
    message.error('您没有科目权限！')
    return false
  }

  const codelistall = await useRouteApi(groupStandardAndTemplate, {schemaName: databaseTrue})({databaseNum: databaseTrue.value,iyear:strDate.value.substring(0,4)});
  if(codelistall.length > 0){
    const accstandard= await findByStandardUnique(codelistall[0].uniqueAccStandard)
    const accStyleUnique= await findByAccStyle(accstandard.accStyleUnique)
    styleList.value=[]
    styleList.value.push({
      cclass:'全部',
      flagYusuan:''
    })
    for (let i = 0; i < accStyleUnique.length; i++) {
      styleList.value.push({
        cclass:accStyleUnique[i].cclass,
        flagYusuan:accStyleUnique[i].flagYusuan
      })
    }
    styleValue.value = '全部'
  }

  // 获取账套信息，判断是否预算会计
  const datainfo = await findByAccId(database.value);
  if(datainfo.ibudgetAccounting==='1'){   // 是
    styleList.value.push({
      cclass:'财务会计',
      flagYusuan:'1'
    })
    styleList.value.push({
      cclass:'预算会计',
      flagYusuan:'1'
    })
  }
  changeOkLoading(false);
  loading.value  = false
}
const getAdObjInfoByCoCode = (value, type,accList) => {
  let list = accList.filter(item => item[type] == value)
  return list.length > 0 ? list[0] : null
}

const accAuthList = ref([])
async function findByDateBase() {

  accAuthList.value = useCompanyOperateStoreWidthOut().getAccountAuthorizeList
  // 获取已授权的组织标识
  let accOrgAuthkeys = [...new Set(accAuthList.value.filter(item => !hasBlank(item.accGroup)).map(item => item.accGroup))]
  orgList.value = (await getOrganizeAll()).filter(item => accOrgAuthkeys.indexOf(item.uniqueCode) != -1)
  if (orgList.value.length > 0) {
    thisOrgValue.value = orgList.value[0]['uniqueCode']
    loadOrgInterrelatedData(orgList.value[0]['uniqueCode'])
    maxJici.value = parseInt(orgList.value[0]['jiciLength'] || 0)
  }
  let codes = getAdObjInfoByCoCode(database.value, 'accId', accAuthList.value)
  if (codes != null) {
    pageParameter.companyCode = codes.coCode
    pageParameter.companyName = codes.accNameCn
  }

  // 获取所有账套信息 并过滤已授权的账套
  let accList = await getAdInfoDatas()
  // 获取当前用户的权限根据用户信息汇率
  let test1 = await getAllAccAuths({'userId': userStore.getUserInfo.id})

  //科目 类别权限
  const list = test1.filter(v => v.iyear === strDate.value.split('-')[0] && v.accId === database.value)
  console.log(list)
  if(list.length > 0){
    isType.value = list[0].accvocherType
    isCode.value = list[0].ccodeAll
  }
  // 获取凭证类型权限
  findByPzTypeList()
}
async function getQueryPlan() {
  let queryPlanIngo= await findByQueryPlan(databaseTrue.value,'总账')
  if(queryPlanIngo!=null){
    activeKey.value='1'
    queryPlan.value=queryPlanIngo

    ishaveRjz.value=JSON.parse(queryPlanIngo.queryConditions).ishaveRjz
    jc.value=JSON.parse(queryPlanIngo.queryConditions).bend
    strDate.value=JSON.parse(queryPlanIngo.queryConditions).strDate
    endDate.value=JSON.parse(queryPlanIngo.queryConditions).endDate
    minJc.value=JSON.parse(queryPlanIngo.queryConditions).minJc
    maxJc.value=JSON.parse(queryPlanIngo.queryConditions).maxJc
    minKm.value=JSON.parse(queryPlanIngo.queryConditions).minKm
    maxKm.value=JSON.parse(queryPlanIngo.queryConditions).maxKm
    bz.value=JSON.parse(queryPlanIngo.queryConditions).bz
    styleValue.value=JSON.parse(queryPlanIngo.queryConditions).styleValue
    fontSize.value=JSON.parse(queryPlanIngo.queryConditions).fontSize
  }
}

/*********************************** 集团模式 *****************************************/
const accIds = ref([])
const orgList = ref([])
const orgCodeTypeList = ref([])
const orgCodeList = ref([])
const orgCodeAllList = ref([])
const orgCurrencyList = ref([])
const maxJici = ref(0)
const thisOrgValue = ref('')
const dates = ref([])
const naturalYear = ref('')
const orgDates = ref([])
const yearShowOne = ref(false)
const orgSelectChange = (v) => {
  accIds.value = []
  let orgObj =  orgList.value.filter(item=>item.uniqueCode == v)[0]
  maxJici.value = parseInt(orgObj['jiciLength'] || 1)
  loadOrgInterrelatedData(orgObj?.uniqueCode)
}
async function loadOrgInterrelatedData(orgId:string) {
  // 初始化组织所属 科目类型 科目 常用币种
  let template=await findByTOrganization(orgId)
  if(template.length>0){
    // 查询会计准则
    let accStyleUnique= await findByStandardUnique(template[0].uniqueAccStandard)
    // 获取 科目类型
    let accStyle = await findAllStyleByUnique(accStyleUnique.accStyleUnique)
    orgCodeTypeList.value=[]
    orgCodeTypeList.value.push({
      cclass:'全部',
      flagYusuan:''
    })
    accStyle.forEach(v=>{
      orgCodeTypeList.value.push({
        cclass:v.cclass,
        flagYusuan:v.flagYusuan
      })
    })
    orgCodeAllList.value=await findAllByUniqueAccStandardAndTemplateIdAndCclass(
      template[0].uniqueAccStandard,
      template[0].id,
      '全部'
    );
    orgCodeList.value = JsonTool.parseProxy(orgCodeAllList.value)
  }
  // 获取组织常用外币
  orgCurrencyList.value = await findAvailablesOrg({'uniqueCode':thisOrgValue.value})
}
const riqi: any = ref<Moment[]>([moment(new Date().getFullYear(), 'YYYY/MM/DD'), moment(new Date(), 'YYYY/MM/DD')]);
function customizeChange(year) {
  orgDates.value = riqi.value
  dates.value = [moment(year + '-01-01', 'YYYY-MM-DD'), moment((parseInt(year) + 1) + '-01-01', 'YYYY-MM-DD')]
}
const orgCodeTypeSelectChange = (v) => {
  orgCodeList.value = v=='全部'?JsonTool.parseProxy(orgCodeAllList.value):JsonTool.parseProxy(orgCodeAllList.value.filter(item=>item['cclass']==v ))
}

// 获取凭证类型权限
// 先判断是否主管：是主管 查询全部凭证类别【凭证类别表】
//               不是主管 判断 是否有 查询全部凭证类别权限
//                                  是 查询全部【凭证类别表】
//                                  不是 查询 【凭证类别权限授权表】
const authSwithIsCcode=ref('');
async function findByPzTypeList() {
  let newPzTypeList:any=[]
  // 凭证类型权限授权表
  let pztypeAuth =await useRouteApi(company_findByAccTypeAuth,{schemaName: databaseTrue})({userId:userStore.getUserInfo.id,iyear:databaseyear.value})
  // 凭证类别表
  await useRouteApi(currentAccountTypes,{schemaName: databaseTrue})('')
    .then(pztypeAll=>{
      pztypeAll.items.forEach(a=>{
        newPzTypeList.push(a.voucherTypeCode)
      })
    })


  // 数据权限表
  let authSwith= await useRouteApi(findByAccIdAndIyear,{schemaName: databaseTrue})({accId:database.value,iyear:databaseyear.value})
  // 没有开启 查询全部权限
  console.log(authSwith)
  if(authSwith!=='不存在' && authSwith.isVoucherType=='0'){
    authSwithIsCcode.value=authSwith.isCcode

    let test1 = await getAllAccAuths({'userId': userStore.getUserInfo.id})
    const list = test1.filter(v => v.iyear === strDate.value.split('-')[0] && v.accId === database.value)
    if(list.length>0){
      // 1是0否有主管权限
      if(list[0].supervisor =='0'){
        // 1是0否有查询所有凭证类型权限
        if(list[0].accvocherType =='0'){
          if(pztypeAuth.length==0 ){
            pzType.value='未发现凭证类型授权'
            return false
          }
          newPzTypeList=[]
          pztypeAuth.forEach(a=>{
            newPzTypeList.push(a.accvocherType)
          })
        }
      }
    }

    if(list.length==0 ){
      pzType.value='未发现凭证类型授权'
      return false
    }
  }
  else{
    let test1 = await getAllAccAuths({'userId': userStore.getUserInfo.id})
    const list = test1.filter(v => v.iyear === strDate.value.split('-')[0] && v.accId === database.value)
    if(list.length>0){
      // 1是0否有主管权限
      if(list[0].supervisor =='0'){
        // 1是0否有查询所有凭证类型权限
        if(list[0].accvocherType =='0'){
          if(pztypeAuth.length==0 ){
            pzType.value='未发现凭证类型授权'
            return false
          }
          newPzTypeList=[]
          pztypeAuth.forEach(a=>{
            newPzTypeList.push(a.accvocherType)
          })
        }
      }
    }
    if(list.length==0 ){
      pzType.value='未发现凭证类型授权'
      return false
    }
  }
  pzTypeList.value=newPzTypeList
  return true
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

  :deep(.ant-select-selector), :deep(.ant-input-affix-wrapper){
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
            width: 37% !important;
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
