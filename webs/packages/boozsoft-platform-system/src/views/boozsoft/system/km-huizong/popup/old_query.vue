<template>
  <BasicModal
    destroyOnClose
    width="850px"
    v-bind="$attrs"
    title="科目汇总查询"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
  >
    <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
    <div class="nc-open-content" style="height: 100%">
        <div class="open-content-up">
          <div class="ocup-position"> 系统方案 </div>
          <div class="ocup-position"> 个人方案 </div>
          <a-tabs v-model:activeKey="activeKey" type="card" tabPosition="left" @change="tabChange">
            <a-tab-pane key="" tab="标准模式">
              <ul>
                <li>
                  <p>
                    <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
                  </p>
                </li>
                <li>
                  <a-radio-group style="width: 100%" v-model:value="radiovalue" @change="radiochange">
                    <p>
                      <a-radio :value="1">
                        <span>期间：</span>
                      </a-radio>
                      <span style="margin-left: 10%">
                      <a-select
                        v-model:value="strDate"
                        show-search
                        :disabled="dateselflg"
                        option-filter-prop="children"
                        style="width: 150px"
                        @focus="focusStrDate"
                        @change="handleChangeStrDate"
                      >
                      <a-select-option
                        v-for="item in strDateList"
                        :key="item.id"
                        :value="item.value"
                      >
                        {{ item.value }}
                      </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                    </a-select>
                      <span>&emsp;~&emsp;</span>
                      <a-select
                        v-model:value="endDate"
                        show-search
                        :disabled="dateselflg"
                        option-filter-prop="children"
                        style="width: 150px"
                        @focus="focusEndDate"
                        @change="handleChangeEndDate"
                      >
                      <a-select-option
                        v-for="item in endDateList"
                        :key="item.id"
                        :value="item.value"
                      >
                        {{ item.value }}
                      </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                    </a-select>
                    </span>
                    </p>
                    <p>
                      <a-radio :value="2">
                        <span>日期：</span>
                      </a-radio>
                      <span style="margin-left: 10%">
                      <a-range-picker
                        style="width: 350px"
                        v-model:value="riqi"
                        :disabled="timeselflg"
                      />
                    </span>
                    </p>
                  </a-radio-group>
                </li>
                <li>
                  <span>科目类型：</span>
                  <a-select style="width: 350px;margin-left: 7%;" v-model:value="styleValue" @change="styleChange">
                    <a-select-option v-for="d in styleList" :value="d.cclass">
                      {{ d.cclass }}
                    </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
                <li><span>科目：</span>
                  <a-select
                    v-model:value="minKm"
                    show-search
                    allowClear
                    option-filter-prop="children"
                    style="width: 155px;margin-left: 7.5%"
                    :filter-option="filterOption"
                    @focus="handleFocusMinKm"
                    @change="handleChangeMinKm"
                  >
                    <a-select-option v-for="d in minkmList" :value="d.ccode">
                      {{ d.value }}
                    </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                  <span>&emsp;~&emsp;</span>
                  <a-select
                    v-model:value="maxKm"
                    show-search
                    allowClear
                    option-filter-prop="children"
                    style="width: 155px"
                    :filter-option="filterOption"
                    @focus="handleFocusMaxKm"
                  >
                    <a-select-option v-for="d in maxkmList" :value="d.ccode">
                      {{ d.value }}
                    </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
                <li>
                  <a-radio-group style="width: 100%" v-model:value="jc">
                    <a-radio :value="1">
                      <span>级次</span>
                    </a-radio>
                    <a-select
                      default-value="1"
                      v-model:value="minJc"
                      @focus="handleFocusMinCj"
                      style="width: 155px; text-align: center;margin-left: 13%;"
                    >
                      <a-select-option value="1">1</a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                    </a-select>
                    <span>&emsp;~&emsp;</span>
                    <a-select
                      default-value="1"
                      v-model:value="maxJc"
                      @focus="handleFocusMaxCj"
                      style="width: 155px; text-align: center"
                    >
                      <a-select-option v-for="d in maxJcList" :value="d.value">
                        {{ d.label }}
                      </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                    </a-select>

                    <br><br>
                    <a-radio :value="2" >
                      <span>末级科目</span>
                    </a-radio>
                  </a-radio-group>
                </li>
                <li>
                  <span>币种：</span>
                  <a-select style="width: 350px;margin-left: 7%;" v-model:value="bz">
                    <a-select-option v-for="d in bzList" :value="d.id">
                      {{ d.currChName }}
                    </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
                <li>
                  <span>记账状态：</span>
                  <a-select style="width: 350px;margin-left: 7%;" v-model:value="ibook">
                    <a-select-option value=""> </a-select-option>
                    <a-select-option value="0">未记账</a-select-option>
                    <a-select-option value="1">已记账</a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
                <li>
                  <span>制单人：</span>
                  <a-select style="width: 350px;margin-left: 7%;" v-model:value="cbill">
                    <a-select-option v-for="d in cbillList" :value="d.cbill">
                      {{ d.cbill }}
                    </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
                <li>
                  <span>凭证类别：</span>
                  <a-select style="width: 350px;margin-left: 7%;" v-model:value="voucherTypes">
                    <a-select-option v-for="d in voucherTypesList" :value="d.voucherTypeCode">
                      {{ d.voucherTypeCode }}
                    </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
                <li>
                  <span>凭证编号：</span>
                  <span style="margin-left: 7%">
                 <a-input
                   v-model:value="minInoId"
                   :maxlength="4"
                   style="width: 160px; text-align: center"
                 />
                  <a-input
                    style="width: 30px; border-left: 0; pointer-events: none; background-color: #fff"
                    placeholder="~"
                    disabled
                  />
                  <a-input
                    v-model:value="maxInoId"
                    :maxlength="4"
                    style="width: 160px; text-align: center;"
                  />
                </span>

                </li>
                <li>
                  <span>字号：</span>
                  <a-select style="width: 350px;margin-left: 7%;" v-model:value="fontSize">
                    <a-select-option value="MAX"> 大号字体 </a-select-option>
                    <a-select-option value="MIN"> 小号字体 </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
              </ul>
            </a-tab-pane>
            <a-tab-pane key="0" tab="集团模式">

            </a-tab-pane>
            <a-tab-pane key="1" tab="个人标准模式" v-if="queryPlan!=null">
              <ul>
                <li>
                  <p>
                    <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
                  </p>
                </li>
                <li>
                  <a-radio-group style="width: 100%" v-model:value="radiovalue" @change="radiochange">
                    <p>
                      <a-radio :value="1">
                        <span>期间：</span>
                      </a-radio>
                      <span style="margin-left: 10%">
                      <a-select
                        v-model:value="strDate"
                        show-search
                        :disabled="dateselflg"
                        option-filter-prop="children"
                        style="width: 150px"
                        @focus="focusStrDate"
                        @change="handleChangeStrDate"
                      >
                      <a-select-option
                        v-for="item in strDateList"
                        :key="item.id"
                        :value="item.value"
                      >
                        {{ item.value }}
                      </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                    </a-select>
                      <span>&emsp;~&emsp;</span>
                      <a-select
                        v-model:value="endDate"
                        show-search
                        :disabled="dateselflg"
                        option-filter-prop="children"
                        style="width: 150px"
                        @focus="focusEndDate"
                        @change="handleChangeEndDate"
                      >
                      <a-select-option
                        v-for="item in endDateList"
                        :key="item.id"
                        :value="item.value"
                      >
                        {{ item.value }}
                      </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                    </a-select>
                    </span>
                    </p>
                    <p>
                      <a-radio :value="2">
                        <span>日期：</span>
                      </a-radio>
                      <span style="margin-left: 10%">
                      <a-range-picker
                        style="width: 350px"
                        v-model:value="riqi"
                        :disabled="timeselflg"
                      />
                    </span>
                    </p>
                  </a-radio-group>
                </li>
                <li>
                  <span>科目类型：</span>
                  <a-select style="width: 350px;margin-left: 7%;" v-model:value="styleValue" @change="styleChange">
                    <a-select-option v-for="d in styleList" :value="d.cclass">
                      {{ d.cclass }}
                    </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
                <li><span>科目：</span>
                  <a-select
                    v-model:value="minKm"
                    show-search
                    allowClear
                    option-filter-prop="children"
                    style="width: 155px;margin-left: 7.5%"
                    :filter-option="filterOption"
                    @focus="handleFocusMinKm"
                    @change="handleChangeMinKm"
                  >
                    <a-select-option v-for="d in minkmList" :value="d.ccode">
                      {{ d.value }}
                    </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                  <span>&emsp;~&emsp;</span>
                  <a-select
                    v-model:value="maxKm"
                    show-search
                    allowClear
                    option-filter-prop="children"
                    style="width: 155px"
                    :filter-option="filterOption"
                    @focus="handleFocusMaxKm"
                  >
                    <a-select-option v-for="d in maxkmList" :value="d.ccode">
                      {{ d.value }}
                    </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
                <li>
                  <a-radio-group style="width: 100%" v-model:value="jc">
                    <a-radio :value="1">
                      <span>级次</span>
                    </a-radio>
                    <a-select
                      default-value="1"
                      v-model:value="minJc"
                      @focus="handleFocusMinCj"
                      style="width: 155px; text-align: center;margin-left: 13%;"
                    >
                      <a-select-option value="1">1</a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                    </a-select>
                    <span>&emsp;~&emsp;</span>
                    <a-select
                      default-value="1"
                      v-model:value="maxJc"
                      @focus="handleFocusMaxCj"
                      style="width: 155px; text-align: center"
                    >
                      <a-select-option v-for="d in maxJcList" :value="d.value">
                        {{ d.label }}
                      </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                    </a-select>

                    <br><br>
                    <a-radio :value="2" >
                      <span>末级科目</span>
                    </a-radio>
                  </a-radio-group>
                </li>
                <li>
                  <span>币种：</span>
                  <a-select style="width: 350px;margin-left: 7%;" v-model:value="bz">
                    <a-select-option v-for="d in bzList" :value="d.id">
                      {{ d.currChName }}
                    </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
                <li>
                  <span>记账状态：</span>
                  <a-select style="width: 350px;margin-left: 7%;" v-model:value="ibook">
                    <a-select-option value=""> </a-select-option>
                    <a-select-option value="0">未记账</a-select-option>
                    <a-select-option value="1">已记账</a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
                <li>
                  <span>制单人：</span>
                  <a-select style="width: 350px;margin-left: 7%;" v-model:value="cbill">
                    <a-select-option v-for="d in cbillList" :value="d.cbill">
                      {{ d.cbill }}
                    </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
                <li>
                  <span>凭证类别：</span>
                  <a-select style="width: 350px;margin-left: 7%;" v-model:value="voucherTypes">
                    <a-select-option v-for="d in voucherTypesList" :value="d.voucherTypeCode">
                      {{ d.voucherTypeCode }}
                    </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
                <li>
                  <span>凭证编号：</span>
                  <span style="margin-left: 7%">
                 <a-input
                   v-model:value="minInoId"
                   :maxlength="4"
                   style="width: 160px; text-align: center"
                 />
                  <a-input
                    style="width: 30px; border-left: 0; pointer-events: none; background-color: #fff"
                    placeholder="~"
                    disabled
                  />
                  <a-input
                    v-model:value="maxInoId"
                    :maxlength="4"
                    style="width: 160px; text-align: center;"
                  />
                </span>

                </li>
                <li>
                  <span>字号：</span>
                  <a-select style="width: 350px;margin-left: 7%;" v-model:value="fontSize">
                    <a-select-option value="MAX"> 大号字体 </a-select-option>
                    <a-select-option value="MIN"> 小号字体 </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
              </ul>
            </a-tab-pane>
          </a-tabs>
        </div>
      </div>
  </BasicModal>
</template>

<script setup="props, { content }" lang="ts">
import {CaretDownOutlined} from '@ant-design/icons-vue'
import {ref, unref, onMounted, reactive} from 'vue';
import { BasicModal, useModalInner } from '/@/components/Modal';
import { PageWrapper } from '/@/components/Page';
import {
  Input as AInput,
  DatePicker as ADatePicker,
  Select as ASelect,
  Checkbox as ACheckbox,
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Upload as AUpload,
  Table as ATable,
  Tabs as ATabs,
  message,
} from 'ant-design-vue';
import { useTabs } from '/@/hooks/web/useTabs';
import router from '/@/router';
import moment, { Moment } from 'moment';
import {
  findPeriodByAccontId,
  findBzAll,
  findMaxJc,
  findCodeKmByPeriod,
} from '/@/api/record/generalLedger/data';
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {findByQueryPlan, saveQueryPlan} from "/@/api/query-plan/queryPlan";
import {useUserStore} from "/@/store/modules/user";
import {
  company_findAllStyleByUnique,
  findByAccStyle,
  findByStandardUnique
} from "/@/api/accstandard/accstandard";
import {findByAccId, findDataBase} from "/@/api/record/system/account";
import {groupStandardAndTemplate} from "/@/api/codekemu/codekemu";
import {
  filterAccListByAuth,
  getAdInfoDatas,
  getAllAccAuths,
  findGroupVoucherTypes
} from "/@/api/record/system/financial-settings";
import {groupByCbill} from "/@/api/record/system/accvoucher";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";

const  ARangePicker=ADatePicker.RangePicker
const { closeCurrent } = useTabs(router);
const ASelectOption = ASelect.Option;
const ARadioGroup = ARadio.Group;
const ARadioButton = ARadio.Button;
const ATabPane = ATabs.TabPane;
const emit=defineEmits([]);
const data = [];
const activeKey: any = ref('');
const radiovalue: any = ref(1);
const formItems: any = ref({});
// 会计区间
const dateList: any = ref([]);
// 会计科目
const kmList: any = ref([]);
//级次
const jcList: any = ref([]);
//币种
const bzList: any = ref([]);
// 凭证类型
const voucherTypesList: any = ref([]);
const voucherTypes: any = ref('');
// 制单人
const cbillList: any = ref([]);
const cbill: any = ref('');
// 凭证编码 最大、最小
const minInoId: any = ref('');
const maxInoId: any = ref('');
// 记账状态
const ibook: any = ref('');


const month = useCompanyOperateStoreWidthOut().getLoginDate.split('-')[1];
const endDate = ref<String>('');
const strDate = ref<String>('');
let endDateList: any = ref([]);
let strDateList: any = ref([]);
let minkmList: any = ref([]);
let maxkmList: any = ref([]);
const minKm = ref<string>();
const maxKm = ref<string>();

const jc = ref<number>(1);
let maxJcList: any = ref([]);
let minJcList: any = ref([]);
const minJc = ref<number>(1);
const maxJc = ref<number>(1);

const showStyle = ref<string>('J');
const fontSize = ref<string>('MIN');
const bz = ref<string>('');

const isShowQjlh = ref<boolean>(false);
const userStore = useUserStore();
// 期间是否可选
let dateselflg: any = ref(false);
// 日期是否可选
let timeselflg: any = ref(true);
const riqi: any = ref<Moment[]>([moment(new Date().getFullYear(), 'YYYY/MM/DD'), moment(new Date(), 'YYYY/MM/DD')]);
const queryPlan: any = ref([]);
// 数据库模式名称
const databaseyear = ref('');
const database = ref(getCurrentAccountName(false));
const databaseTrue = ref(getCurrentAccountName(true));
let styleValue: any = ref('全部');
let styleList: any = ref([]);
// 页面变量
const pageParameter = reactive({
  companyCode: '',
  companyName: '',
  accId: '',
  database: '',
  accNameAll: '',
})

const [register, { closeModal, setModalProps }] = useModalInner((data) => {
  // 方式2
  formItems.value.openOne = data.data.openOne;
  setModalProps({ minHeight: 400 });
  getQueryPlan()
});
let isChanged = false;
/******************* 弹框加载中 **************************/
import { Loading } from '/@/components/Loading';
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
const loadingRef = ref(false);
const compState = reactive({
  absolute: false,
  loading: false,
  tip: '加载中...',
});
function openCompFullLoading() {
  openLoading(false);
}
function openLoading(absolute: boolean) {
  compState.absolute = absolute;
  compState.loading = true;
}
/*******************END**************************/
function tabChange(val) {
  if(val===''){
    jc.value=1
    strDate.value = databaseyear.value + '-' + month
    endDate.value = databaseyear.value + '-' + month
    minJc.value=1
    maxJc.value=1
    minKm.value=''
    maxKm.value=''
    bz.value=''
    styleValue.value='全部'
    fontSize.value='MIN'
    ibook.value=''
    cbill.value=''
    voucherTypes.value=''
    minInoId.value=''
    maxInoId.value=''
  }else if(val==='1'){
    getQueryPlan()
  }
}

// 日间radio触发事件
function radiochange(val) {
  timeselflg.value=val.target.value === 1
  dateselflg.value=val.target.value !== 1
}
// 日期格式化
function timeformat(dateData) {
  let date = new Date(dateData);
  let y = date.getFullYear();
  let m = date.getMonth() + 1;
  m = m < 10 ? '0' + m : m;
  let d = date.getDate();
  d = d < 10 ? '0' + d : d;
  return y + '-' + m + '-' + d;
}


async function handleOk() {
  if (strDate.value.length <= 0 && endDate.value <= 0 && riqi.value.length === 0) {
    message.error('请选择会计区间!');
    return;
  }
  formItems.value.strDate = strDate.value;
  formItems.value.endDate = endDate.value;
  if(radiovalue.value===2){
    let str=timeformat(riqi.value[0]._d).replaceAll('-','')
    let end=timeformat(riqi.value[1]._d).replaceAll('-','')
    formItems.value.strDate = str
    formItems.value.endDate = end
  }

  formItems.value.minKm = minKm.value;
  formItems.value.maxKm = maxKm.value;

  formItems.value.jc = jc.value;
  formItems.value.minJc = '1';
  formItems.value.maxJc = '10';
  if (jc.value === 1) {
    formItems.value.minJc = minJc.value;
    formItems.value.maxJc = maxJc.value;
  }
  formItems.value.showStyle = showStyle.value;
  formItems.value.fontSize = fontSize.value;
  formItems.value.bz = bz.value;
  formItems.value.bzName = bzList.value.filter((o) => o.id === bz.value)[0].currChName;

  formItems.value.isShowQjlh = isShowQjlh.value;
  // 过滤会计科目
  let list = kmList.value;
  if (maxKm.value) {
    list = list.filter((o) => o.ccode <= maxKm.value);
  }
  if (minKm.value) {
    list = list.filter((o) => o.ccode >= minKm.value);
  }

  if (jc.value === 2) {
    list = list.filter((o) => o.bend === '1');
  } else {
    if (minJc.value) {
      list = list.filter((o) => minJc.value <= o.igrade);
    }
    if (maxJc.value) {
      list = list.filter((o) => o.igrade <= maxJc.value);
    }
  }
  formItems.value.kmList = list;
  formItems.value.riqi = riqi.value;
  formItems.value.styleValue = styleValue.value;
  formItems.value.styleList = styleValue.value!=='全部'?styleList.value.filter(s=>s.cclass===styleValue.value):styleList.value.filter(s=>s.cclass!==styleValue.value);
  formItems.value.pageParameter = pageParameter;
  formItems.value.ibook = ibook.value;
  formItems.value.cbill = cbill.value;
  formItems.value.voucherTypes = voucherTypes.value;
  formItems.value.minInoId = minInoId.value;
  formItems.value.maxInoId = maxInoId.value;
  formItems.value.pageParameter = pageParameter;

  const queryPlanEntity={
    accountId:database.value,
    owningMenuName:'科目汇总表',
    owningPlan:activeKey.value,
    planPerson:userStore.getUserInfo.id,
    queryConditions:{
      bend:formItems.value.jc,
      strDate:formItems.value.strDate,
      endDate:formItems.value.endDate,
      minJc:formItems.value.minJc,
      maxJc:formItems.value.maxJc,
      minKm:formItems.value.minKm,
      maxKm:formItems.value.maxKm,
      timflg:'qj',
      bz:formItems.value.bz,
      fontSize:formItems.value.fontSize,
      styleValue:formItems.value.styleValue,
      ibook:formItems.value.ibook,
      cbill:formItems.value.cbill,
      voucherTypes:formItems.value.voucherTypes,
      minInoId:formItems.value.minInoId,
      maxInoId:formItems.value.maxInoId
    }
  }
  await saveQueryPlan(queryPlanEntity);
  emit('save', unref(formItems));
  closeModal();
  return true;
}
async function handleClose() {
  if (null != formItems.value.openOne && formItems.value.openOne == 1) {
    await closeCurrent();
    router.push('/zhongZhang/home/welcome');
  }
  return true;
}

function handleChangeMinKm() {
  maxKm.value = '';
}
async function handleChangeStrDate() {
  // 切换数据库
  const s = database.value+'-'+strDate.value.substring(0,4);
    // 会计科目
  // 开始日期变动  如果开始日期大于结束于日期 则重置结束日期
  if (endDate.value) {
    if (strDate.value > endDate.value) {
      endDate.value = '';
    }
    // 会计科目
    kmList.value=await useRouteApi(findCodeKmByPeriod,{schemaName:s})({ strDate: strDate.value, endDate: 'test', accId: database.value,userId:userStore.getUserInfo.id });
  }
}
async function handleChangeEndDate() {
  // 切换数据库
  const s = database.value+'-'+endDate.value.substring(0,4);
  // 开始日期变动  如果结束日期小于开始日期 则重置开始日期
  if (strDate.value) {
    if (strDate.value > endDate.value) {
      strDate.value = '';
    }
    // 会计科目
    kmList.value=await useRouteApi(findCodeKmByPeriod,{schemaName:s})({ strDate: endDate.value, endDate: 'test', accId: database.value,userId:userStore.getUserInfo.id });
  }
}
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
const filterOption = (input: string, option: any) => {
  return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const handleFocusMinCj = () => {
  //获取焦点时 如果最大科目已选择 则过滤大于最大科目的数据
  if (maxJc.value) {
    minJcList.value = jcList.value.filter((o) => o.value <= maxJc.value);
  } else {
    minJcList.value = jcList.value;
  }
};
const handleFocusMaxCj = () => {
  maxJcList.value=[]
  //获取焦点时 如果最小科目已选择 判断过滤小于最小科目的数据
  if (minJc.value) {
    maxJcList.value = jcList.value.filter((o) => o.value >= minJc.value);
  } else {
    maxJcList.value = jcList.value;
  }
};

async function styleChange(val) {
  minKm.value=''
  maxKm.value=''
  formItems.value.minKm = '';
  formItems.value.maxKm = '';

  const start = jc.value===1?strDate.value:timeformat(riqi.value[0]._d);
  const s = database.value+'-'+start.substring(0,4);
  // 重新获取科目类型
  let accstyle =await useRouteApi(company_findAllStyleByUnique,{schemaName:s})('');
  if(val!=='全部'){
    if(val!=='财务会计'&&val!=='预算会计'){
      kmList.value=[]
      formItems.value.kmList=[]
      let list=await newfindAllCode(s,start);
      kmList.value=list.filter(ck=>ck.cclass===val)
      formItems.value.kmList = kmList.value
    }else if(val=='财务会计'){
      kmList.value=[]
      formItems.value.kmList=[]
      for (let i = 0; i < accstyle.length; i++) {
        if(accstyle[i].flagYusuan!=='1'){
          let list=await newfindAllCode(s,start);
          for (let j = 0; j < list.length; j++) {
            kmList.value.push({
              ccode:list[j].ccode,
              value:list[j].ccode+'-'+list[j].ccodeName,
            });
          }
        }
      }
    }else if(val=='预算会计'){
      kmList.value=[]
      formItems.value.kmList=[]
      for (let i = 0; i < accstyle.length; i++) {
        if(accstyle[i].flagYusuan=='1'){
          let list=await newfindAllCode(s,start);
          for (let j = 0; j < list.length; j++) {
            kmList.value.push({
              ccode:list[j].ccode,
              value:list[j].ccode+'-'+list[j].ccodeName,
            });
          }
        }
      }
    }
  }
  else{ kmList.value= await newfindAllCode(s,start) }
}
async function newfindAllCode(s,start) {
  // 会计科目
  let list=await useRouteApi(findCodeKmByPeriod,{schemaName:s})({ strDate: start, endDate: 'test', accId: database.value,userId:userStore.getUserInfo.id });
  return list;
}

/************************************************************************/
const getAdObjInfoByCoCode = (value, type,accList) => {
  let list = accList.filter(item => item[type] == value)
  return list.length > 0 ? list[0] : null
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
async function AllCondition() {
  jcList.value=[]
  styleList.value=[]
  findByDateBase()
  //加载数据
  dateList.value = await findPeriodByAccontId(database.value);
  //级次
  const num = await findMaxJc(database.value,databaseyear.value);
  for (let i = 1; i < num + 1; i++) {
    jcList.value.push({
      label: i,
      value: i,
    });
  }
  //币种
  bzList.value = await useRouteApi(findBzAll, {schemaName: databaseTrue})('');
  bzList.value.push({ id: '', currChName: '全部' });
  compState.loading = false;
}

async function findByAccStyleAll() {
  styleList.value=[]
  /******************************** 科目类型 *********************************/
    // 获取对应的账套科目 所属的 会计准则、科目模板
  let codelistall= await useRouteApi(groupStandardAndTemplate, {schemaName: databaseTrue})({databaseNum: databaseTrue.value,iyear:strDate.value.substring(0,4)});
  // 会计准则
  const accstandard= await findByStandardUnique(codelistall[0].uniqueAccStandard)
  let accStyleUnique= await findByAccStyle(accstandard.accStyleUnique)
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
  const datainfo = await findByAccId(database.value);
  if(datainfo.ibudgetAccounting==='1'){   // 是
    styleList.value.push('财务会计')
    styleList.value.push('预算会计')
  }
  /******************************** 科目类型 *********************************/
}

async function getQueryPlan() {
  let queryPlanIngo= await findByQueryPlan(database.value,'科目汇总表')
  // console.log(queryPlanIngo)
  if(queryPlanIngo!=null){
    activeKey.value='1'
    queryPlan.value=queryPlanIngo

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
    ibook.value=JSON.parse(queryPlanIngo.queryConditions).ibook
    cbill.value=JSON.parse(queryPlanIngo.queryConditions).cbill
    voucherTypes.value=JSON.parse(queryPlanIngo.queryConditions).voucherTypes
    minInoId.value=JSON.parse(queryPlanIngo.queryConditions).minInoId
    maxInoId.value=JSON.parse(queryPlanIngo.queryConditions).maxInoId
  }
}
async function dynamicAdReload(data) {
  // openCompFullLoading()
  // 查询凭证类别
  voucherTypesList.value=await findGroupVoucherTypes('')
  // 重新查询账套编码信息
  await findDataBase(data.accId,+data.year).then(async (item)=> {
    database.value = item.accountId
    databaseTrue.value = item.accountMode
    databaseyear.value = data.year
    pageParameter.accId = item.accountId
    pageParameter.database = item.accountMode
    pageParameter.accNameAll = item.accName
    cbillList.value= await useRouteApi(groupByCbill, {schemaName: databaseTrue})({strDate:strDate.value.replaceAll('-',''),endDate:endDate.value.replaceAll('-','')});
    findByAccStyleAll()
    handleChangeEndDate();
    AllCondition();
  })
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
      width: 170px;
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

  :deep(.ant-select-selector), :deep(.ant-calendar-picker-input) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label {
    text-align: right;
    //width: 110px;
    display: inline-block;
    padding: 5px 10px;
  }
}

:deep(.ant-tabs-left-bar) {
  margin-right: 0px !important;
}

:deep(.ant-tabs-left) {
  .ant-tabs-tab {
    width: 170px;
    font-weight: bold;
  }
  .ant-tabs-tab-active {
    background-color: #65cbec !important;
    color: rgba(0, 0, 0, 0.85) !important;
  }

  .ant-tabs-tabpane-active {
    padding-left: 0 !important;
  }

  .ant-tabs-tab:nth-of-type(1) {
    margin-top: 35px;
  }
  .ant-tabs-tab:nth-of-type(3) {
    margin-top: 105px !important;
  }
}
</style>
