<template>
  <BasicModal
    width="880px"
    class="spaceLogo"
    v-bind="$attrs"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
    :loading="modelLoadIng"
    loadingTip="查询参数初始化中"
  >
    <template #title>
      <div style="height: 30px;width: 100%;background-color: #5f375c;color: white;line-height: 30px;text-align: left;">
        <AppstoreOutlined  style="margin: 0 2px;font-size: 14px;"/>
        <span style="font-size: 13px"> 账簿</span>
      </div>
    </template>

    <div class="nc-open-content" style="height: 100%" >
      <SearchOutlined style="font-size: 24px;color: #0096c7;margin-top: 2px;margin-left: 40%;"/>
      <span style="line-height:30px;font-size: 24px;color: #0096c7;font-weight: bold;">&ensp;{{ openType=='yue'?'发生及余额表':'明细表' }}</span>
      <div class="open-content-up">
        <div class="ocup-position">
          <PicLeftOutlined/>
          查询方案
        </div>
        <a-tabs type="card" v-model:activeKey="defaultTabsKey" tabPosition="left" @change="tabsChange">
          <a-tab-pane key="1" tab="标准模式" style="overflow: hidden;">
            <ul style="width: 93%;margin-top: -20px;">
              <li>
                <div class="special-border-div">
                  <span style="color: #5a5a5a">业务范围</span>
                  <AccountPicker theme="three" @reloadTable="dynamicAdReload" style="display: block;text-align: center;"/>
                </div>
              </li>
              <li>
                <div class="special-border-div">
                  <span style="color: #5a5a5a">查询日期</span>
                  <div>
                    <div class="sbd-left">
                      <a-radio-group style="width: 100%" v-model:value="radiovalue" @change="radiochange">
                        <p>
                          <a-radio value="1"></a-radio>
                          <span>会计期间：</span>
                          <a-select
                            v-model:value="strDate"
                            show-search
                            :disabled="dateselflg"
                            style="width: 100px;"
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
                        </p>
                        <p v-if="openType=='mx'">
                          <a-radio value="2"></a-radio>
                          <span>制单日期：</span>
                          <a-range-picker
                            v-model:value="riqi"
                            style="width: 224px;"
                            :disabled="timeselflg"
                          />
                        </p>
                      </a-radio-group>
                    </div>
                    <div class="sbd-right">
                      <a-radio-group size="small" :disabled="radiovalue=='2'" @change="checkChange">
                        <a-radio-button value="1">上月</a-radio-button>
                        <a-radio-button value="2">本月</a-radio-button>
                        <a-radio-button value="3">本季</a-radio-button>
                        <a-radio-button value="4">本年</a-radio-button>
                      </a-radio-group>
                      <a-radio-group size="small" :disabled="radiovalue=='1'" @change="checkChange" v-if="openType=='mx'">
                        <a-radio-button value="5">今天</a-radio-button>
                        <a-radio-button value="6">昨天</a-radio-button>
                        <a-radio-button value="7">前天</a-radio-button>
                        <a-radio-button value="8">月末</a-radio-button>
                      </a-radio-group>
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
                        <a-select-option v-for="d in pzTypeList" :value="d" >
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
                        style="width: 200px;"
                        @change="handleChangeMinKm"
                        @focus="handleFocusMinKm"
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
                        style="width: 200px;"
                        @focus="handleFocusMaxKm"
                        :class="minkmList.length==0?'selectColorRed':'selectColorBlack'"
                      >
                        <a-select-option v-for="d in maxkmList" :value="d.ccode">
                          {{ d.value }}
                        </a-select-option>
                        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                      </a-select>
                    </div>
                    <div v-if="openType=='kmhz'" style="width: 50%;float: left;">
                      <span class="right_span">记账状态：</span>
                      <a-select style="width: 160px;text-align: center;" v-model:value="ibook">
                        <a-select-option value="">全部</a-select-option>
                        <a-select-option value="0">未记账</a-select-option>
                        <a-select-option value="1">已记账</a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                      </a-select>
                    </div>
                    <div v-if="openType=='kmhz'" style="width: 50%;float: left;">
                      <span class="right_span">制单人：</span>
                      <a-select style="width: 160px;text-align: center;" v-model:value="cbill">
                        <a-select-option v-for="d in cbillList" :value="d.cbill">
                          {{ d.cbill }}
                        </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                      </a-select>
                    </div>
                    <div v-if="openType=='kmhz'">
                      <span class="right_span">凭证编号：</span>
                      <a-input :allowClear="true" placeholder="" style="width: 80px" v-model:value="minInoId"/>
                      <span>&emsp;~&emsp;</span>
                      <a-input :allowClear="true" placeholder="" style="width: 80px"  v-model:value="maxInoId"/>
                    </div>
                    <div style="width: 50%;float: left;">
                      <a-radio-group style="width: 100%" v-model:value="jc">
                        <a-radio :value="1" style="width: 60%;float: left;display: flex;">级次</a-radio>
                        <span style="margin-left: 25px;">
                          <a-select
                            v-model:value="minJc"
                            default-value="1"
                            @focus="handleFocusMinCj"
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
                    <div style="width: 50%;">
                      <span class="right_span">包含未记账：</span>
                      <a-checkbox v-model:checked="ishaveRjz" style="display: inline-flex;">  </a-checkbox>
                    </div>
                  </div>
                </div>
              </li>
            </ul>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>
    <template #footer>
      <div style="height: 35px">
        <div style="float: right">
          <a-button @click="handleClose" shape="round" style="width: 100px">取消</a-button>
          <a-button @click="handleOk" v-if="!modelLoadIng" type="primary" shape="round"  style="width: 100px">查询</a-button>
        </div>
      </div>
    </template>
  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
/********************************************* 公共参数 ********************************************************/
import {BasicModal, useModalInner} from '/@/components/Modal'
import {
  Checkbox as ACheckbox,
  DatePicker as ADatePicker,
  Input as AInput, message,
  Radio as ARadio,
  Select as ASelect,
  Tabs as ATabs
} from "ant-design-vue"
import {FileSearchOutlined, PicLeftOutlined, SearchOutlined,CaretDownOutlined,AppstoreOutlined} from '@ant-design/icons-vue';
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
import {useMessage} from "/@/hooks/web/useMessage";
import {reactive, ref, unref} from "vue";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {
  findVoucherTypeAuthorList,
  getThisAdInfoData
} from "/@/api/record/system/financial-settings";
import {findAllByAuthPeriod} from "/@/api/caozuoyuan/caozuoyuan";
import {hasBlank} from "/@/api/task-api/tast-bus-api";
import {findAllByUniqueCodeAndDatabaseNameAndIsCtrl} from "/@/api/record/system/group-dataSee";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {
  company_findByAuthorizationAndIyearKeMu,
  company_findByAuthorizationKeMu, companyfindAll, findByOrgCcode, findOrgCodeByIyearAndOrgUnique
} from "/@/api/codekemu/codekemu";
import {
  acctemplateFindByAccId,
  findAllByUniqueAccStandardAndTemplateIdAndCclass,
  findByTOrganization
} from "/@/api/acctemplate/acctemplate";
import {
  findAllStyleByUnique,
  findByAccStyle,
  findByStandardUnique
} from "/@/api/accstandard/accstandard";
import {findByAccId} from "/@/api/record/system/account";
import {findVoucherTypeAll} from "/@/api/record/system/voucher-type";
import {findMaxJc} from "/@/api/record/generalLedger/data";
import moment from "moment";
import dayjs from "dayjs";
import {getOrganizeAll, groupByOrgPeriodIyear} from "/@/api/record/group/im-organize";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
import router from "/@/router";
import {useTabs} from "/@/hooks/web/useTabs";
import {groupByCbill} from "/@/api/record/system/accvoucher";
import {saveLog} from "/@/api/record/system/group-sys-login-log";
import logger from "../../../../../../mock/logger";

const { closeCurrent } = useTabs(router);
const emit = defineEmits(['register', 'save'])
const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const ACheckboxGroup = ACheckbox.Group
const ATabPane = ATabs.TabPane
const userStore = useUserStore();
const busDate = useCompanyOperateStoreWidthOut().getLoginDate
const {createWarningModal} = useMessage();
const modelLoadIng = ref(false)
const companyOperateStore = useCompanyOperateStoreWidthOut()
const defaultTabsKey = ref('1')
const openType = ref('')
const databaseTrue = ref('')
const quarterList: any = ref([])
const cbillList: any = ref([]);
const riqi: any = ref([]);
const formItems: any = ref({});
// 记账状态
const ibook = ref('');
// 凭证编码 最大、最小
const minInoId: any = ref('');
const maxInoId: any = ref('');
const pageParameter = reactive({
  companyCode: '',
  companyName: '',
  accId: '',
})

const [register, {closeModal, setModalProps}] = useModalInner((data) => {
  formItems.value.openOne = data.data.openOne;
  openType.value=data.openType
  defaultTabsKey.value='1'
  setModalProps({ minHeight: 400 });
})
const tabsChange = (val) => {
  if(val=='2'){
    findByDateBase()
  }else if(val=='1'){
    findByCbill()
  }
}
async function styleChange(val) {
  minKm.value=''
  maxKm.value=''
  formItems.value.minKm = '';
  formItems.value.maxKm = '';

  findByKemu()
  if(val!=='全部'){
    setTimeout(()=>{
      let list=kmList.value.filter(ck=>ck.cclass===val)
      kmList.value=[]
      formItems.value.kmList=[]
      kmList.value=list
      formItems.value.kmList = list
    },200)
  }
  else{
    findByKemu()
  }
}
// 查询制单人
async function findByCbill() {
  cbillList.value= await useRouteApi(groupByCbill, {schemaName: databaseTrue})({strDate:strDate.value.replaceAll('-',''),endDate:endDate.value.replaceAll('-','')});
}
async function dynamicAdReload(data) {
  databaseTrue.value =data.accountMode
  // 获取账套信息
  getThisAdInfoData({ accId: data.accId }).then(async (res) => {
    pageParameter.accId=res.accId
    pageParameter.companyCode=res.coCode
    pageParameter.companyName=res.accName
    // 获取授权的期间
    await findAllByAuthPeriod(userStore.getUserInfo.id,res.coCode)
      .then((temp)=>{
        if(temp.length==0){
          strDate.value=''
          endDate.value=''
          dateList.value=[]
          return createWarningModal({content: '未进行期间授权,不能查询！'});
        }
        dateList.value=temp

        const busDates = busDate.split('-');
        // 查看当前业务日期与期间列表年度是否匹配
        let blist = dateList.value.filter(it => it.iyear == busDates[0])
        if (blist.length > 0) { // 存在与业务日期相匹配的年度 使用业务期间 反之使用最大年度期间
          let busQj = blist.filter(it => it.dateStart.startsWith(busDates[1]))[0]['value']
          strDate.value = busQj
          endDate.value = busQj
        }else{
          // 1、期间
          strDate.value=temp[0].value
          endDate.value=temp[0].value
        }

        // 会计科目类型
        findByAccStyleAll(data.accId)
        // 获取凭证类型权限
        findByPzTypeList()
        // 查询科目
        findByKemu()
        // 查询制单人
        findByCbill()
        // 级次
        findJici(data.accId,data.iyear)
      })
  })
}
const checkChange = (e) => {
  let v = e.target.value
  let dates = strDate.value.split('-')
  switch (v) {
    case '1':
      let the = parseInt(dates[1])
      if (the != 1) {
        strDate.value = dates[0] + '-' + (((the - 1) > 9) ? `${(the - 1)}` : `0${(the - 1)}`)
        endDate.value = strDate.value
      } else {
        message.info('暂无上月！')
      }
      break
    case '2':
      strDate.value = dates[0] + '-' + busDate.split('-')[1]
      endDate.value = strDate.value
      break
    case '3':
      let qList = quarterList.value.filter(it => it.iyear == dates[0])
      if (qList.length > 0) { // 存在季报
        let arr = qList.filter(it => {
          let min = moment(it.iyear + '-' + it.dateStart, 'YYYY-MM-DD')
          let max = moment(it.iyear + '-' + it.dateEnd, 'YYYY-MM-DD')
          let the = moment(strDate.value + '-01', 'YYYY-MM-DD')
          return min <= the && the <= max
        })
        strDate.value = arr[0].iyear + '-' + arr[0].dateStart.substring(0, 2)
        endDate.value = arr[0].iyear + '-' + arr[0].dateEnd.substring(0, 2)
      } else {
        let min = '01'
        let max = '03'
        let v = dates[1]
        switch (v) {
           case '04':case '05':case '06':
            min = '04'
            max = '06'
            break
          case '07' :case '08' :case '09':
            min = '07'
            max = '09'
            break
          case '10' :case '11' :case '12':
            min = '10'
            max = '12'
            break
        }
        strDate.value = dates[0] + '-' + min
        endDate.value = dates[0] + '-' + max
      }
      break
    case '4':
      let list = dateList.value.filter(it => it.iyear == dates[0])
      strDate.value = list[0].value
      endDate.value = list[list.length - 1].value
      break
    case '5':
      let d = dayjs(busDate, 'YYYY-MM-DD')
      riqi.value = [d, d]
      break
    case '6':
      let yday = dayjs(busDate, 'YYYY-MM-DD').add(-1, 'day')
      riqi.value = [yday, yday]
      break
    case '7':
      let yday1 = dayjs(busDate, 'YYYY-MM-DD').add(-2, 'day')
      riqi.value = [yday1, yday1]
      break
    case '8':
      let yday2 = dayjs(busDate, 'YYYY-MM-DD').endOf('month')
      riqi.value = [yday2, yday2]
      break
  }
}
function filterOption(input, option) {
  // 不是数字
  if(isNaN(input)){
    return option.key.toLowerCase().indexOf(input.toLowerCase()) >= 0;
  }else{
    return option.value.toLowerCase().startsWith(input.toLowerCase());
  }
}
function timeformat(dateData) {
  let date = new Date(dateData);
  let y = date.getFullYear();
  let m:any = date.getMonth() + 1;
  m = m < 10 ? '0' + m : m;
  let d:any = date.getDate();
  d = d < 10 ? '0' + d : d;
  return y + '-' + m + '-' + d;
}
async function handleOk() {
  if (defaultTabsKey.value == '1') {
    handleOk1()
  }else if (defaultTabsKey.value == '2') {
    handleOk2()
  }
}
async function handleClose() {
  if (null != formItems.value.openOne && formItems.value.openOne == 1) {
    await closeCurrent();
    router.push('/zhongZhang/home/welcome');
  }
  return true;
}
/********************************************* 公共参数 ********************************************************/
/********************************************* 标准模式 ********************************************************/
const radiovalue = ref('1');
// 日期是否可选
let timeselflg: any = ref(true);
// 期间是否可选
let dateselflg: any = ref(false);
const endDate:any = ref<String>("")
const strDate:any = ref<String>("")
// 1 是独立账套  2 集团账套
const independent: any = ref('');
// 账套会计区间
const dateList: any = ref([]);
let endDateList: any = ref([])
let strDateList: any = ref([])
let kmList: any = ref([])
let styleValue: any = ref('全部')
let pzType: any = ref('')
let styleList: any = ref([])
let pzTypeList: any = ref([])
let minkmList: any = ref([]);
let maxkmList: any = ref([]);
const minKm:any = ref<string>();
const maxKm:any = ref<string>();
let maxJcList: any = ref([]);
let minJcList: any = ref([]);
let jcList: any = ref([]);
const minJc:any = ref<number>(1);
const maxJc:any = ref<number>(1);
const jc = ref<number>(1);
const ishaveRjz = ref<boolean>(true);
const cbill: any = ref('');

async function focusStrDate() {
  if (endDate.value) {
    strDateList.value = dateList.value.filter((o) => o.value <= endDate.value);
  } else {
    strDateList.value = dateList.value;
  }
}
async function focusEndDate() {
  if (strDate.value) {
    endDateList.value = dateList.value.filter((o) => o.value >= strDate.value);
  } else {
    endDateList.value = dateList.value;
  }
}
async function handleChangeStrDate() {

}
async function handleChangeEndDate() {

}
async function findByKemu() {
  kmList.value=[]
  let map={
    uniqueCode:databaseTrue.value,
    databaseName:'code_kemu',
    ctrl:'1',
  }
  let dataSwitch= await findAllByUniqueCodeAndDatabaseNameAndIsCtrl(map)
  if (dataSwitch.length > 0) {
    // 1是2否 按年度控制科目
    let isCtrlYear = dataSwitch[0].isCtrlYear
    if (isCtrlYear == '1') {
      await useRouteApi(company_findByAuthorizationAndIyearKeMu, {schemaName: databaseTrue})({tenantId: databaseTrue.value,iyear: strDate.value.split('-')[0],userId:userStore.getUserInfo.id})
        .then((res) => {
          res.forEach(a=>{
            let map={
              ccode:a.ccode,
              cclass:a.cclass,
              ccodeName:a.ccodeName,
              igrade:parseInt(a.igrade),
              bend:a.bend,
              value:a.ccode+'-'+a.ccodeName
            }
            kmList.value.push(map);
          })
        });
    } else {
      await useRouteApi(company_findByAuthorizationKeMu, {schemaName: databaseTrue})({
        tenantId: databaseTrue.value,
        userId:userStore.getUserInfo.id
      })
        .then((res) => {
          res.forEach(a=>{
            let map={
              ccode:a.ccode,
              cclass:a.cclass,
              ccodeName:a.ccodeName,
              igrade:parseInt(a.igrade),
              bend:a.bend,
              value:a.ccode+'-'+a.ccodeName
            }
            kmList.value.push(map);
          })
        });
    }
    if (kmList.value.length == 0) {
      return createWarningModal({content: '会计科目已被数据控制,请进行数据授权！'});
    }
  }else{
    findByKemuAll()
  }
}
// 所有科目
async function findByKemuAll() {
  kmList.value=[]
  // 账套科目
  let map={
    searchConditon: {
      requirement: 'ccode',
      value: '',
    },
    cclass:'全部',
    databasenum:databaseTrue.value,
    iyear:strDate.value.split('-')[0],
  }
  let accKemu=await useRouteApi(companyfindAll, { schemaName: databaseTrue })(map)
  accKemu.items.forEach(a=>{
    let map={
      ccode:a.ccode,
      cclass:a.cclass,
      ccodeName:a.ccodeName,
      igrade:parseInt(a.igrade),
      bend:a.bend,
      value:a.ccode+'-'+a.ccodeName
    }
    kmList.value.push(map);
  })
}
// 会计科目类型
async function findByAccStyleAll(accId) {
  const codelistall= await acctemplateFindByAccId(accId)
  const accstandard= await findByStandardUnique(codelistall.uniqueAccStandard)
  const accStyleUnique= await findByAccStyle(accstandard.accStyleUnique)
  styleList.value=[]
  styleValue.value='全部'
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
  // 获取账套信息，判断是否预算会计
  const datainfo = await findByAccId(accId);
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
}
// 获取凭证类型权限
async function findByPzTypeList() {
  let map={
    uniqueCode:databaseTrue.value,
    databaseName:'sys_voucher_type',
    ctrl:'1',
  }
  let dataSwitch= await findAllByUniqueCodeAndDatabaseNameAndIsCtrl(map)
  // 集团模式
  if (independent.value=='0'&&dataSwitch.length > 0) {
    let map={
      userId:userStore.getUserInfo.id,
      tenantId:databaseTrue.value
    }
    let temp= await useRouteApi(findVoucherTypeAuthorList, {schemaName: databaseTrue})(map);
    if(temp.length==0){
      return createWarningModal({content: '操作员未分配凭证类别操作权限，不能进行相关操作！'});
    }
    pzTypeList.value=[]
    pzTypeList.value.push('全部')
    temp.items.map(a=>a.voucherTypeCode).forEach(a=>{
      pzTypeList.value.push(a)
    })
    pzType.value=pzTypeList.value[0]
  }else{
    findpzTypeAll()
  }
}
// 所有凭证类别
async function findpzTypeAll(){
  let temp= await useRouteApi(findVoucherTypeAll, {schemaName: databaseTrue})('');
  if(temp.items.length==0){
    return createWarningModal({content: '未发现凭证类别,不能查询!'});
  }
  pzTypeList.value=[]
  pzTypeList.value.push('全部')
  temp.items.map(a=>a.voucherTypeCode).forEach(a=>{
    pzTypeList.value.push(a)
  })
  pzType.value=pzTypeList.value[0]
}
function handleChangeMinKm() {
  maxKm.value = '';
}
const handleFocusMinKm = () => {
  minkmList.value = kmList.value;
};
const handleFocusMaxKm = () => {
  //获取焦点时 如果最小科目已选择 判断过滤小于最小科目的数据
  if (minKm.value) {
    maxkmList.value = kmList.value.filter((o) => o.ccode >= minKm.value);
  } else {
    maxkmList.value = kmList.value;
  }
};
// 加载级次
async function findJici(accId,iyear) {
  const num = await findMaxJc(accId,iyear);
  for (let i = 1; i < num + 1; i++) {
    jcList.value.push({
      label: i,
      value: i,
    });
  }
}
const handleFocusMinCj = () => {
  //获取焦点时 如果最大科目已选择 则过滤大于最大科目的数据
  if (maxJc.value) {
    minJcList.value = jcList.value.filter((o) => o.value <= maxJc.value);
  } else {
    minJcList.value = jcList.value;
  }
};
const handleFocusMaxCj = () => {
  //获取焦点时 如果最小科目已选择 判断过滤小于最小科目的数据
  if (minJc.value) {
    maxJcList.value = jcList.value.filter((o) => o.value >= minJc.value);
  } else {
    maxJcList.value = jcList.value;
  }
};
function radiochange(val) {
  timeselflg.value = val.target.value === '1';
  dateselflg.value = val.target.value === '2';
}
async function handleOk1() {
  if (defaultTabsKey.value == '1') {
    formItems.value.queryMode = '1'
    formItems.value.querytype='gs'
    formItems.value.styleList = styleValue.value!=='全部'?styleList.value.filter(s=>s.cclass===styleValue.value):styleList.value.filter(s=>s.cclass!==styleValue.value);
  }
  if (strDate.value.split('-')[0] !== endDate.value.split('-')[0]) {
    return createWarningModal({content: '会计期间不允许跨年！'});
  }
  if (strDate.value.length <= 0 && endDate.value <= 0 && riqi.value.length === 0) {
    return createWarningModal({content: '请选择会计区间！'});
  }
  if(pzTypeList.value.length==0){
    return createWarningModal({content: '请选择凭证类别！'});
  }
  if(kmList.value.length==0){
    await findByKemu()
    return false
  }
  formItems.value.strDate = strDate.value;
  formItems.value.endDate = endDate.value;
  formItems.value.minKm = minKm.value;
  formItems.value.maxKm = maxKm.value;
  formItems.value.jc = jc.value;
  formItems.value.minJc = '1';
  formItems.value.maxJc = '1';
  if (jc.value === 1) {
    formItems.value.minJc = minJc.value;
    formItems.value.maxJc = maxJc.value;
  }
  formItems.value.bz = '全部';
  // 过滤会计科目
  let list = kmList.value;
  if (maxKm.value!==undefined && maxKm.value!=='') {
    list = list.filter((o) => parseInt(o.ccode) <= parseInt(maxKm.value));
  }
  if (minKm.value!==undefined && minKm.value!=='') {
    list = list.filter((o) => parseInt(o.ccode) >= parseInt(minKm.value));
  }

  if (jc.value === 2) {
    list = list.filter((o) => o.bend === '1');
  } else {
    if (minJc.value!==undefined && minJc.value!=='') {
      list = list.filter((o) => minJc.value <= o.igrade);
    }
    if (maxJc.value!==undefined && maxJc.value!=='') {
      list = list.filter((o) => o.igrade <= maxJc.value);
    }
  }
  let kemu:any=[]
  list.forEach(a=>{
    let temp:any={
      ccode:a.ccode,
      ccodeName:a.value,
      cclass:a.cclass
    }
    kemu.push(temp)
  })
  formItems.value.kmList = [...new Set(kemu)];
  formItems.value.riqi = riqi.value;
  formItems.value.ishaveRjz = ishaveRjz.value==true?'1':'0';
  formItems.value.styleValue = styleValue.value;
  formItems.value.pzType = pzType.value;
  formItems.value.bzName = '全部';
  formItems.value.timflg = radiovalue.value=='1'?'qj':'rq';
  formItems.value.minInoId = minInoId.value;
  formItems.value.maxInoId = maxInoId.value;
  formItems.value.ibook = ibook.value;
  formItems.value.cbill = cbill.value;
  /************** 记录操作日志 ****************/
  let txt=openType.value=='yue'?'发生及余额表':'明细表'
  let map={
    userId: useUserStoreWidthOut().getUserInfo.id,
    userName: useUserStoreWidthOut().getUserInfo.realName,
    optModule:'gl',
    optFunction:'总账',
    optRange:'1',// 1 账套
    uniqueCode:pageParameter.accId,
    optAction:'查询',
    optContent:'操作内容【'+txt+'查询】,账套代码【'+pageParameter.companyCode+'】,账套名称【'+pageParameter.companyName+'】,查询条件【查询日期：'+strDate.value+','+endDate.value+'】,' +
      '科目类型【'+styleValue.value+'】,凭证类别【'+pzType.value+'】,会计科目【'+minKm.value+','+maxKm.value+'】包含未记账【'+ishaveRjz.value+'】',
  }
  await saveLog(map)
  /************** 记录操作日志 END ****************/
  emit('save', {data: unref(formItems), accAuthList: accAuthList.value});
  closeModal();
  return true;
}
/********************************************* 标准模式 ********************************************************/
/********************************************* 集团模式 ********************************************************/
const orgList:any = ref([])
// 组织会计期间
const orgDateList: any = ref([]);
const naturalYear = ref('')
const thisOrgValue = ref('')
const maxJici = ref(0)
const accAuthList:any = ref([])
const accIds:any = ref([])
const orgDates = ref([])
const orgCodeTypeList:any = ref([])
const orgCodeList:any = ref([])

// 查询组织科目分类
async function findByOrgStyle(orgId) {
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
  }
}
// 查询组织科目
async function findByOrgCode() {
  let temp= await findOrgCodeByIyearAndOrgUnique(naturalYear.value,thisOrgValue.value)
  temp.forEach(a=>{
    let map:any={
      ccode:a.ccode,
      cclass:a.cclass,
      igrade:parseInt(a.igrade),
      bend:a.bend,
      value:a.ccode+'-'+a.ccodeName
    }
    orgCodeList.value.push(map);
  })
}
// 根据分类查询组织科目
async function orgCodeTypeSelectChange() {
  if(styleValue.value=='全部'){
    orgCodeList.value=[]
    await findByOrgCode()
  }else {
    orgCodeList.value=[]
    let temp= await findOrgCodeByIyearAndOrgUnique(naturalYear.value,thisOrgValue.value)
    temp=temp.filter(a=>a.cclass==styleValue.value)
    temp.forEach(a=>{
      let map:any={
        ccode:a.ccode,
        cclass:a.cclass,
        igrade:parseInt(a.igrade),
        bend:a.bend,
        value:a.ccode+'-'+a.ccodeName
      }
      orgCodeList.value.push(map);
    })
  }
}

async function findByDateBase() {
  accAuthList.value = useCompanyOperateStoreWidthOut().getAccountAuthorizeList
  // 获取已授权的组织标识
  let accOrgAuthkeys = [...new Set(accAuthList.value.filter(item => !hasBlank(item.accGroup)).map(item => item.accGroup))]
  orgList.value = (await getOrganizeAll()).filter(item => accOrgAuthkeys.indexOf(item.uniqueCode) != -1)
  if (orgList.value.length > 0) {
    thisOrgValue.value = orgList.value[0]['uniqueCode']
    findByOrgStyle(orgList.value[0]['uniqueCode'])
    maxJici.value = parseInt(orgList.value[0]['jiciLength'] || 0)
    findByOrgDate()
  }
}
// 获取组织期间表
async function findByOrgDate() {
  let temp= await groupByOrgPeriodIyear(thisOrgValue.value)
  if(temp.length==0){
    return createWarningModal({content: '组织没有期间,无法查询！'});
  }
  orgDateList.value=temp
  naturalYear.value=temp[0]

  findByOrgCode()
}
const orgSelectChange = (v) => {
  accIds.value = []
  let orgObj =  orgList.value.filter(item=>item.uniqueCode == v)[0]
  maxJici.value = parseInt(orgObj['jiciLength'] || 1)
  findByOrgDate()
  findByOrgStyle(v)
}

async function handleOk2() {
  if (accIds.value.length === 0) {
    return createWarningModal({content: '至少选择一家核算单位！'});
  }
  if (orgDates.value.length === 0) {
    return createWarningModal({content: '请选择会计区间！'});
  }else{
    if(parseInt(timeformat(orgDates.value[0]).split('-')[0])!==parseInt(timeformat(orgDates.value[1]).split('-')[0])){
      return createWarningModal({content: '会计区间不允许跨年！'});
    }
  }
  if (strDate.value.split('-')[0] !== endDate.value.split('-')[0]) {
    return createWarningModal({content: '会计期间不允许跨年！'});
  }
  if (orgCodeList.value.length == 0) {
    return createWarningModal({content: '组织中没有会计科目,不能进行查询！'});
  }
  formItems.value.strDate = timeformat(orgDates.value[0]).replaceAll('-','');
  formItems.value.endDate = timeformat(orgDates.value[1]).replaceAll('-','');
  formItems.value.minKm = minKm.value;
  formItems.value.maxKm = maxKm.value;

  formItems.value.jc = jc.value;
  formItems.value.minJc = '1';
  formItems.value.maxJc = '1';
  if (jc.value === 1) {
    formItems.value.minJc = minJc.value;
    formItems.value.maxJc = maxJc.value;
  }
  formItems.value.bz = '全部';
  // 过滤会计科目
  let list = kmList.value;
  if (maxKm.value!==undefined && maxKm.value!=='') {
    list = list.filter((o) => parseInt(o.ccode) <= parseInt(maxKm.value));
  }
  if (minKm.value!==undefined && minKm.value!=='') {
    list = list.filter((o) => parseInt(o.ccode) >= parseInt(minKm.value));
  }

  if (jc.value === 2) {
    list = list.filter((o) => o.bend === '1');
  } else {
    if (minJc.value!==undefined && minJc.value!=='') {
      list = list.filter((o) => minJc.value <= o.igrade);
    }
    if (maxJc.value!==undefined && maxJc.value!=='') {
      list = list.filter((o) => o.igrade <= maxJc.value);
    }
  }
  let kemu:any=[]
  list.forEach(a=>{
    let temp:any={
      ccode:a.ccode,
      ccodeName:a.ccodeName,
      cclass:a.cclass
    }
    kemu.push(temp)
  })

  formItems.value.kmList = [...new Set(kemu)];
  formItems.value.riqi = riqi.value;
  formItems.value.ishaveRjz = ishaveRjz.value==true?'1':'0';
  formItems.value.styleValue = styleValue.value;
  formItems.value.pzType = "";
  formItems.value.bzName = '全部';
  formItems.value.querytype='jt'
  formItems.value.queryMode = '0'
  let test:any=accAuthList.value.filter(ite=>ite.independent != '1')
  formItems.value.accIds = test.filter(v=>accIds.value.indexOf(v.coCode)!==-1).map(item=>item.accId)
  formItems.value.styleList = styleValue.value!=='全部'?orgCodeTypeList.value.filter(s=>s.cclass===styleValue.value):orgCodeTypeList.value.filter(s=>s.cclass!==styleValue.value);
  formItems.value.timflg = radiovalue.value=='1'?'qj':'rq';
  emit('save', {data: unref(formItems), accAuthList: accAuthList.value});
  closeModal();
  return true;
}
/********************************************* 集团模式 ********************************************************/
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
