<template>
  <BasicModal
    width="900px"
    class="spaceLogo"
    v-bind="$attrs"
    title="银行日记账查询"
    @ok="handleOk()"
    @cancel="handleClose()"
    :loading="loadMark"
    @register="register"
    :canFullscreen="false"
    :footer="null"
    loadingTip="查询参数初始化中"
  >
    <template #title>
      <div style="height: 30px;width: 100%;background-color: #5f375c;color: white;line-height: 30px;text-align: left;">
        <AppstoreOutlined  style="margin: 0 2px;font-size: 14px;"/>
        <span style="font-size: 14px">现金银行</span>
      </div>
    </template>
    <div style="display: inline-flex;justify-content: space-between;width: 100%;">
      <div style="width: calc(100% - 150px);height: 100%;">
        <div style="text-align: center;padding: 10px 0 5px;">
          <SearchOutlined style="font-size: 24px;color: #0096c7;margin-top: 2px"/>
          <span style="line-height:30px;font-size: 24px;color: #0096c7;font-weight: bold;">&ensp;银行日记账查询</span>
        </div>
        <div class="nc-open-content">
          <div class="open-content-up">
            <div class="border-div">
              <span>业务范围</span>
              <AccountPicker theme="three" @reloadTable="dynamicAdReload" style="text-align: center;"/>
            </div>
            <div class="border-div">
              <span>查询日期</span>
              <div style="margin-left: 14px;">
                <ul>
                  <li style="margin: 2% 0px;">
                    <div>
                      <a-radio-group style="width: 100%" v-model:value="radiovalue" @change="radiochange">
                        <a-radio value="1"><label style="text-align: left;">会计期间：</label></a-radio>
                        <a-select
                          v-model:value="strDate"
                          show-search
                          :disabled="dateselflg"
                          style="width: 125px;"
                          @focus="focusStrDate"
                          @change="handleChangeStrDate"
                        >
                          <a-select-option
                            v-for="item in strDateList"
                            :value="item.iyear+'-'+item.iperiodNum"
                          >
                            {{ item.iyear+'-'+item.iperiodNum }}
                          </a-select-option>
                          <template #suffixIcon><CaretDownOutlined style="color:#666666;"/></template>
                        </a-select>
                        <span>&nbsp;~&nbsp;</span>
                        <a-select
                          v-model:value="endDate"
                          show-search
                          :disabled="dateselflg"
                          style="width: 125px;"
                          @focus="focusEndDate"
                          @change="handleChangeEndDate"
                        >
                          <a-select-option
                            v-for="item in endDateList"
                            :value="item.iyear+'-'+item.iperiodNum"
                          >
                            {{ item.iyear+'-'+item.iperiodNum }}
                          </a-select-option>
                          <template #suffixIcon><CaretDownOutlined style="color:#666666;"/></template>
                        </a-select>
                        <a-radio-group size="small" :disabled="radiovalue=='2'" @change="checkChange" style="margin-left: 50px;">
                          <a-radio-button value="1" style="width: 50px;">上月</a-radio-button>
                          <a-radio-button value="2" style="width: 50px;">本月</a-radio-button>
                          <a-radio-button value="3" style="width: 50px;">本季</a-radio-button>
                          <a-radio-button value="4" style="width: 50px;">本年</a-radio-button>
                        </a-radio-group>
                        <br>
                        <a-radio value="2"><label style="text-align: left;">单据日期：</label></a-radio>
                        <a-date-picker style="width: 125px;" v-model:value="strDate2" format="YYYY-MM-DD" value-format="YYYY-MM-DD" :disabled="!dateselflg" @change="datePickerChange()"/>
                        <span>&nbsp;~&nbsp;</span>
                        <a-date-picker style="width: 125px;" v-model:value="endDate2" format="YYYY-MM-DD" value-format="YYYY-MM-DD" :disabled="!dateselflg" @change="datePickerChange()"/>
                        <a-radio-group size="small" :disabled="radiovalue=='1'" @change="checkChange" style="margin-left: 50px;">
                          <a-radio-button value="5" style="width: 50px;">今天</a-radio-button>
                          <a-radio-button value="6" style="width: 50px;">昨天</a-radio-button>
                          <a-radio-button value="7" style="width: 50px;">前天</a-radio-button>
                          <a-radio-button value="8" style="width: 50px;">月末</a-radio-button>
                        </a-radio-group>
                      </a-radio-group>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
            <div class="border-div">
              <span>查询条件</span>
              <div>
                <label style="width: 110px;">科目：&emsp;&emsp;</label>
                <a-select
                  v-model:value="formItems.kemu"
                  show-search
                  style="width: 210px;margin-left: 20px;"
                >
                  <a-select-option v-for="d in kmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;"/></template>
                </a-select>
                <label style="margin-left: 50px;width: 110px;">摘要：&emsp;&emsp;</label>
                <a-input v-model:value="formItems.summary" style="width: 190px;margin-left: 22px;"/>
              </div>
              <div>
                <label style="width: 110px;">金额：&emsp;&emsp;</label>
                <a-input v-model:value="strNum" placeholder="" style="width: 100px;margin-left: 20px;"/>
                <span>~</span>
                <a-input v-model:value="endNum" placeholder="" style="width: 100px"/>
                <label style="margin-left: 50px;width: 110px;">币种：&emsp;&emsp;</label>
                <a-select
                  v-model:value="formItems.currency"
                  show-search
                  style="width: 190px;text-align: center;margin-left: 22px;"
                >
                  <a-select-option v-for="d in bzList" :value="d.foreignCode">
                    {{ d.foreignName }}
                  </a-select-option>
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;"/></template>
                </a-select>
              </div>
              <div>
                <label style="width: 110px;">凭证类别：</label>
                <a-select
                  v-model:value="formItems.voucherType"
                  show-search
                  style="width: 210px;margin-left: 20px;"
                >
                  <a-select-option v-for="item in voucherTypeList" :key="item.voucherTypeCode" :value="item.voucherTypeCode" >
                    [{{ item.voucherTypeCode }}]-{{ item.voucherTypeName }}
                  </a-select-option>
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;"/></template>
                </a-select>
                <label style="margin-left: 80px;width: 110px;">&emsp;&emsp;&emsp;&emsp;</label>
                <a-checkbox v-model:checked="ishaveRjz" style="width: 200px">
                  包含未记账
                </a-checkbox>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="right-btns">
        <Button style="width: 100px;" shape="round" @click="handleOk"  type="primary">查询</Button>
        <Button style="width: 100px;margin-top: 10px" shape="round" @click="handleClose">取消</Button>
      </div>
    </div>
  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
/********************************************* 公共参数 ********************************************************/
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import {
  message,
  Button,
  Checkbox as ACheckbox,
  DatePicker as ADatePicker,
  Input as AInput,
  Radio as ARadio,
  Select as ASelect,
  Tabs as ATabs
} from "ant-design-vue"
import {AppstoreOutlined, SearchOutlined,CaretDownOutlined,LinkOutlined} from '@ant-design/icons-vue';
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useUserStore} from "/@/store/modules/user";
import {useMessage} from "/@/hooks/web/useMessage";
import {ref} from "vue";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import router from "/@/router";
import {useTabs} from "/@/hooks/web/useTabs";
import {periodAllList} from "/@/api/record/group/im-unit";
import {hasBlank, pointMessage} from "/@/api/task-api/tast-bus-api";
import moment, {Moment} from 'moment';
import dayjs from "dayjs";
import { findAll as findSupAll} from "/@/api/record/supplier_data/supplier";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useAccountPickerCache} from "/@/store/modules/boozsoft/components/AccountPicker/cache";
import {findByStockAccId} from "/@/api/record/system/stock-account";
import {findCodeKemuByBr} from "/@/api/record/system/bank-statement";
import {currentCyDatas, findAllXjOrLlList} from "/@/api/record/system/financial-settings";
import {findVoucherTypeAll} from "/@/api/record/system/voucher-type";
import {findPeriodByAccontId} from "/@/api/record/generalLedger/data";

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
const companyOperateStore = useCompanyOperateStoreWidthOut()
const modelLoadIng = ref(false)
const openType = ref('')
const databaseTrue = ref('')
const dynamicAccId = ref('')
const formItems: any = ref({
  kemu:'',
  summary:'',
  currency:'',
  voucherType:''
});
//************************************* new ***************************
let radiovalue = ref('1');
const endDate:any = ref<String>("")
const strDate:any = ref<String>("")
const endDate2:any = ref<String>("")
const strDate2:any = ref<String>("")
let endDateList: any = ref([])
let strDateList: any = ref([])
let time: any = ref<Moment[]>([]);
// 日期是否可选
let timeselflg: any = ref(true);
const maxPingzhengQj = ref('')
// 期间是否可选
let dateselflg: any = ref(false);
// 会计区间
const dateList: any = ref([])
const quarterList: any = ref([])
//************************************* 查询条件 ***************************
const strNum: any = ref('')
const endNum: any = ref('')
const dataType: any = ref('')
const dataTypeList: any = ref([{title:'全部',value:''},{title:'已审核',value:'1'},{title:'未审核',value:'0'}])
const ishaveRjz = ref<boolean>(true)

const [registerModalPopPage, {openModal: openMoalPopPage}] = useModal();

const [register, {closeModal, setModalProps}] = useModalInner((data) => {
  formItems.value.openOne = data.data.openOne;
  openType.value=data.openType
  setModalProps({ minHeight: 475 });
  if (hasBlank(dataType.value)){
    dataType.value = ''
  }
})
const iyear:any = ref('')
const dynamicAdReload = async (obj) =>{
  databaseTrue.value=obj.accountMode
  dynamicAccId.value=obj.accId
  iyear.value = obj.iyear
  modelLoadIng.value=true

  formItems.value.kemu = ''
  formItems.value.currency = ''
  formItems.value.voucherType = ''

  await reloadKemu()
  await reloadCurrency()
  await reloadVoucherType()

  useAccountPickerCache().set(obj)

  await reLifeQueryParameter(obj.accountMode,obj.target.id)
  modelLoadIng.value=false
}

//获取银行科目
const kmList:any = ref([])
async function reloadKemu(){
  kmList.value = []
  const res = await useRouteApi(findAllXjOrLlList, {schemaName: databaseTrue})({})
  kmList.value = res.filter(item => {
    item.value = item.ccode + '-' + item.ccodeName
    return item.bbank == '1' && item.bend == '1'
  })
  if (kmList.value.length > 0) {
    if (formItems.value.kemu == null || formItems.value.kemu == '') {
      formItems.value.kemu = kmList.value[0].ccode
    }
  }
}

//获取币种
const bzList: any = ref([])
const bz:any = ref('')
async function reloadCurrency() {
  bzList.value = [];
  useCompanyOperateStoreWidthOut().getAccountList.forEach(item => {
    if (item.accId == dynamicAccId.value && item.currency != '' && item.currency != null) {
      bzList.value = [{
        accountId: dynamicAccId.value,
        foreignCode: item.currency,
        foreignName: item.currencyName
      }]
      formItems.value.bz = item.currency
    }
  })
  useRouteApi(currentCyDatas, {schemaName: databaseTrue})({accId: dynamicAccId.value}).then(res => {
    bzList.value.push(...res.items)
    // bzList.value = res.items/*.filter(item=>item.foreignName.indexOf('人民币') == -1)*/
  });
}

//获取凭证类别
const voucherTypeList:any = ref([])
async function reloadVoucherType(){
  const res = await useRouteApi(findVoucherTypeAll, {schemaName: databaseTrue})({})
  voucherTypeList.value = res.items
}

const reLifeQueryParameter = async (dynamicTenant,id) => {
  // 加载DB所有会计期间与季报数据
  // dateList.value = await periodAllList({id: id});
  dateList.value = await findPeriodByAccontId(dynamicAccId.value);
  let list = dateList.value.filter(it => it.iyear == iyear.value)
  if (dateList.value.length > 0) {
    const busDates = busDate.split('-');
    // 查看当前业务日期与期间列表年度是否匹配
    let blist = dateList.value.filter(it => it.iyear == busDates[0])
    if (blist.length > 0) { // 存在与业务日期相匹配的年度 使用业务期间 反之使用最大年度期间
      let busQj = blist.filter(it => it.iperiodNum == (busDates[1]))[0]
      if (maxPingzhengQj.value==null || maxPingzhengQj.value=='') {
        maxPingzhengQj.value = (busQj.iyear + '' + busQj.iperiodNum)
      }
      if (strDate.value==null || strDate.value=='') {
        strDate.value = (busQj.iyear + '-' + busQj.iperiodNum)
      }
      if (endDate.value==null || endDate.value=='') {
        endDate.value = (busQj.iyear + '-' + busQj.iperiodNum)
      }
    } else {
      // 获取最大凭证期间
      const qijian = ''
      if (hasBlank(qijian)) {
        if (maxPingzhengQj.value==null || maxPingzhengQj.value=='') {
          maxPingzhengQj.value = ''
        }
        if (strDate.value==null || strDate.value=='') {
          strDate.value = list[0].value
        }
        if (endDate.value==null || endDate.value=='') {
          endDate.value = list[list.length - 1].value
        }
      } else {
        if (maxPingzhengQj.value==null || maxPingzhengQj.value=='') {
          maxPingzhengQj.value = qijian.substring(0, 4) + '-' + qijian.substring(4, 6)
        }
        if (strDate.value==null || strDate.value=='') {
          strDate.value = maxPingzhengQj.value
        }
        if (endDate.value==null || endDate.value=='') {
          endDate.value = maxPingzhengQj.value
        }
      }
    }
  } else {
    await pointMessage({title: '温馨提示', content: '请先进行账套年度授权后再进行查询操作！', delay: true})
    strDate.value = ''
    endDate.value = ''
  }
}

function radiochange(val) {
  timeselflg.value = val.target.value === '1';
  dateselflg.value = val.target.value === '2';
  if (val.target.value === '1') {
    strDate2.value = ''
    endDate2.value = ''
    strDate.value = maxPingzhengQj.value;
    endDate.value = maxPingzhengQj.value;
  } else {
    strDate.value = '';
    endDate.value = '';
  }
}
async function focusStrDate() {
  strDateList.value = dateList.value
}
async function focusEndDate() {
  if (strDate.value) {
    endDateList.value = dateList.value.filter(o => o.iyear+''+o.iperiodNum >= strDate.value)
  } else {
    endDateList.value = dateList.value
  }
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
    timeselflg.value = true;
  }
}
function timechange() {
  dateselflg.value = time.value.length > 0;
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
  }
}
function datePickerChange(){
  if(!hasBlank(strDate2.value)&&!hasBlank(endDate2.value)){
    let strYear=timeformat(strDate2.value).split('-')[0]
    let endYear=timeformat(endDate2.value).split('-')[0]
    if(parseInt(strYear)!==parseInt(endYear)){
      strDate2.value=null;endDate2.value=null
      return message.error('不允许跨年！')
    }
  }
}
const checkChange = (e) => {
  let v = e.target.value
  let dates = [strDate.value.substring(0,4),strDate.value.substring(5,7)]
  switch (v) {
    case '1':
      let the = parseInt(dates[1])
      if (the != 1) {
        strDate.value = dates[0] +'-' + (((the - 1) > 9) ? `${(the - 1)}` : `0${(the - 1)}`)
        endDate.value = strDate.value
      } else {
        message.info('暂无上月！')
      }
      break
    case '2':
      strDate.value = dates[0] +'-' + busDate.split('-')[1]
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
        strDate.value = dates[0]+'-' +  min
        endDate.value = dates[0]+'-' +  max
      }
      break
    case '4':
      let list = dateList.value.filter(it => it.iyear == dates[0])
      strDate.value = dates[0]+'-'+list[0].iperiodNum
      endDate.value = dates[0]+'-'+list[list.length - 1].iperiodNum
      break
    case '5':
      strDate2.value=dayjs(busDate, 'YYYY-MM-DD')
      endDate2.value=dayjs(busDate, 'YYYY-MM-DD')
      break
    case '6':
      let yday = dayjs(busDate, 'YYYY-MM-DD').add(-1, 'day')
      strDate2.value=yday
      endDate2.value=yday
      break
    case '7':
      let yday1 = dayjs(busDate, 'YYYY-MM-DD').add(-2, 'day')
      strDate2.value=yday1
      endDate2.value=yday1
      break
    case '8':
      let yday2 = dayjs(busDate, 'YYYY-MM-DD').endOf('month')
      strDate2.value=yday2
      endDate2.value=yday2
      break
  }
}

function handleOk() {
  let strTime=radiovalue.value=='1'?strDate.value:strDate2.value
  let endTime=radiovalue.value=='1'?endDate.value:endDate2.value
  if (strTime!=null && endTime!=null && strTime.length <= 0 && endTime.length <= 0) {
    message.error('请选择会计区间或日期期间!');
    return
  }
  if (strTime.substring(0,4)!=endTime.substring(0,4)){
    message.error('暂不支持跨越年度会计区间查询！');
    return
  }
  if (formItems.value.kemu==null || formItems.value.kemu==''){
    message.error('请选择科目！');
    return
  }

  let map={
    strDate:strTime,
    endDate:endTime,
    strNum:strNum.value,
    endNum:endNum.value,
    kemu: formItems.value.kemu,
    currency: formItems.value.currency,
    accountList: kmList.value,
    dynamicTenantId:databaseTrue.value,
    dynamicAccId: dynamicAccId.value,
    year: strTime.split('-')[0],
    periodStart: strDate.value,
    periodEnd: endDate.value,
    dateStart: strDate2.value,
    dateEnd: endDate2.value,
    accountNum: formItems.value.kemu,
    summary: formItems.value.summary,
    unaccounted: ishaveRjz.value
  }
  console.log(map)
  emit('save', map);
  closeModal();
}

async function handleClose() {
  if (null != formItems.value.openOne && formItems.value.openOne == 1) {
    await closeCurrent();
    await router.push('/home/welcome');
  }else{
    closeModal();
  }
  return true;
}
// 日期格式化
function timeformat(dateData) {
  let date:any = new Date(dateData);
  let y:any = date.getFullYear();
  let m:any = date.getMonth() + 1;
  m = m < 10 ? '0' + m : m;
  let d:any = date.getDate();
  d = d < 10 ? '0' + d : d;
  return y + '-' + m + '-' + d;
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
  :deep(.ant-select-selector),:deep(.ant-input),:deep(.ant-picker), :deep(.ant-input-affix-wrapper) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    background: none;
  }
  .border-div {
    position: relative;
    border: 1px #a29f9f solid;
    margin: 20px 10px;
    padding: 2.5%;
    text-align: left;

    > span {
      display: block;
      text-align: center;
      background-color: white;
      position: absolute;
      left: 50px;
      top: -10px;
      color: #888888;
      font-size: 12px;
      font-weight: bold;
    }
    :deep(.account-picker){
      >div{
        text-align: left;
      }
    }
  }

}

.right-btns{
  width: 150px;background-color: #f1f1f1;padding: 10% 4%;height: 475px;
  :deep(.ant-btn-primary:hover){
    border: 1px solid #5f375c;
  }
}
:global(.ant-modal-header) {
  padding: 10px 0 !important;
}
:global(.ant-modal-close-x){
  height: 30px !important;
  color: white;
}

:deep(.ant-radio-button-wrapper){
  color: #0096c7;
}
</style>
