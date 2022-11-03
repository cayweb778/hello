<template>
  <BasicModal
    title="现金余额表"
    v-bind="$attrs"
    width="800px"
    @cancel="handleClose()"
    @ok="handleOk()"
    @register="register"
  >
    <div class="nc-open-content" style="height: 100%">
      <div class="open-content-up">
        <div class="ocup-position"> 系统方案 </div>
        <div class="ocup-position"> 个人方案 </div>

        <a-tabs v-model:activeKey="activeKey" tabPosition="left" type="card" @change="tabChange">
          <a-tab-pane key="" tab="标准模式">
            <ul>
              <li>

                <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>

              </li>
              <li>
                <span>期间：</span>
                <span>
                    <a-select
                      v-model:value="strDate"
                      :disabled="dateselflg"
                      show-search
                      style="width: 180px;text-align: center;"
                      @change="handleChangeStrDate"
                      @focus="focusStrDate"
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
                        :disabled="dateselflg"
                        show-search
                        style="width: 180px;text-align: center;"
                        @change="handleChangeEndDate"
                        @focus="focusEndDate"
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
              </li>
<!--              <li>
                <span>科目类型：</span>
                <a-select v-model:value="styleValue" style="width: 400px;" @change="styleChange">
                  <a-select-option v-for="d in styleList" :value="d.cclass">
                    {{ d.cclass }}
                  </a-select-option>
                </a-select>
              </li>-->
              <li>
                <span>科目：</span>
                <a-select
                  v-model:value="km"
                  :filter-option="filterOption"
                  allowClear
                  show-search
                  style="width: 400px;"
                  @change="handleChangeMinKm"
                  @focus="handleFocusMinKm"
                >
                  <a-select-option v-for="d in minkmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>
<!--                <a-select
                  v-model:value="minKm"
                  :filter-option="filterOption"
                  allowClear
                  show-search
                  style="width: 180px;"
                  @change="handleChangeMinKm"
                  @focus="handleFocusMinKm"
                >
                  <a-select-option v-for="d in minkmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>
                <span>&emsp;~&emsp;</span>
                <a-select
                  v-model:value="maxKm"
                  :filter-option="filterOption"
                  allowClear
                  show-search
                  style="width: 180px;"
                  @focus="handleFocusMaxKm"
                >
                  <a-select-option v-for="d in maxkmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>-->
              </li>
<!--              <li>
                <a-radio-group v-model:value="jc" style="width: 100%">
                  <a-radio :value="1">
                    <span>级次</span>
                  </a-radio>
                  <a-select
                    v-model:value="minJc"
                    default-value="1"
                    style="width: 80px; text-align: center;margin-left: 7%;"
                    @focus="handleFocusMinCj"
                  >
                    <a-select-option v-for="d in minJcList" :value="d.value">
                      {{ d.label }}
                    </a-select-option>
                  </a-select>
                  <span>&emsp;~&emsp;</span>
                  <a-select
                    v-model:value="maxJc"
                    default-value="1"
                    style="width: 80px; text-align: center"
                    @focus="handleFocusMaxCj"
                  >
                    <a-select-option v-for="d in maxJcList" :value="d.value">
                      {{ d.label }}
                    </a-select-option>
                  </a-select>
                  <a-radio :value="2" style="margin-left: 19%;">
                    <span>末级科目</span>
                  </a-radio>
                </a-radio-group>
              </li>-->
              <li>
                <span>币种：</span>
                <a-select v-model:value="bz" style="width: 400px;">
                  <a-select-option v-for="d in bzList" :value="d.id">
                    {{ d.currChName }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>字号：</span>
                <a-select v-model:value="fontSize" style="width: 400px;">
                  <a-select-option value="MAX"> 大号字体 </a-select-option>
                  <a-select-option value="MIN"> 小号字体 </a-select-option>
                </a-select>
              </li>
              <li>
                <a-checkbox v-model:checked="ishaveRjz" style="width: 130px"> 包含未记账 </a-checkbox>
              </li>
            </ul>
          </a-tab-pane>
          <a-tab-pane key="0" tab="集团模式">
            <ul>
              <li>
                <span>核算单位：</span>

                <a-select
                  mode="multiple"
                  option-label-prop="label"
                  placeholder="可多选"
                  style="width: 80%"
                >
                  <a-select-option label="湖北万亚" value="湖北万亚"> 湖北万亚 </a-select-option>
                  <a-select-option label="北京希格" value="北京希格"> 北京希格 </a-select-option>
                </a-select>
              </li>
              <li
              ><span>期&emsp;&emsp;间：</span>
                <a-select
                  default-value="2020-01"
                  style="width: 218px; text-align: center"
                  @change="handleChange"
                >
                  <a-select-option value="2020-01"> 2020-01 </a-select-option>
                  <a-select-option value="2020-02"> 2020-02 </a-select-option>
                  <a-select-option disabled value="2020-03"> 2020-03 </a-select-option>
                  <a-select-option value="2020-04"> 2020-04 </a-select-option>
                </a-select>
                <span>&emsp;~&emsp;</span>
                <a-select
                  default-value="2020-01"
                  style="width: 218px; text-align: center"
                  @change="handleChange"
                >
                  <a-select-option value="2020-01"> 2020-01 </a-select-option>
                  <a-select-option value="2020-02"> 2020-02 </a-select-option>
                  <a-select-option disabled value="2020-03"> 2020-03 </a-select-option>
                  <a-select-option value="2020-04"> 2020-04 </a-select-option>
                </a-select>
              </li>
              <li
              ><span>科&emsp;&emsp;目：</span>
                <a-select
                  v-model:value="km"
                  :filter-option="filterOption"
                  allowClear
                  show-search
                  style="width: 400px;"
                  @change="handleChangeMinKm"
                  @focus="handleFocusMinKm"
                >
                  <a-select-option v-for="d in minkmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>
<!--                <a-select
                  :default-active-first-option="false"
                  :filter-option="false"
                  :not-found-content="null"
                  :show-arrow="false"
                  allowClear
                  placeholder="最小科目"
                  show-search
                  style="width: 218px"
                  @change="handleChange"
                >
                  <a-select-option v-for="d in data" :key="d.value">
                    {{ d.text }}
                  </a-select-option>
                </a-select>
                <span>&emsp;~&emsp;</span>
                <a-select
                  :default-active-first-option="false"
                  :filter-option="true"
                  :not-found-content="null"
                  :show-arrow="false"
                  allowClear
                  placeholder="最大科目"
                  show-search
                  style="width: 218px"
                  @change="handleChange"
                >
                  <a-select-option v-for="d in data" :key="d.value">
                    {{ d.text }}
                  </a-select-option>
                </a-select>-->
              </li>
<!--              <li>
                <a-radio-group style="width: 100%" @change="onChange">
                  <a-radio :value="1">
                    <span>级次</span>
                    <a-select
                      default-value="1"
                      style="width: 150px; text-align: center"
                      @change="handleChange"
                    >
                      <a-select-option value="1"> 1 </a-select-option>
                      <a-select-option value="2"> 2 </a-select-option>
                      <a-select-option value="3"> 3 </a-select-option>
                      <a-select-option value="4"> 4 </a-select-option>
                      <a-select-option value="5"> 5 </a-select-option>
                      <a-select-option value="6"> 6 </a-select-option>
                    </a-select>
                    <span>&emsp;~&emsp;</span>
                    <a-select
                      default-value="1"
                      style="width: 150px; text-align: center"
                      @change="handleChange"
                    >
                      <a-select-option value="1"> 1 </a-select-option>
                      <a-select-option value="2"> 2 </a-select-option>
                      <a-select-option value="3"> 3 </a-select-option>
                      <a-select-option value="4"> 4 </a-select-option>
                      <a-select-option value="5"> 5 </a-select-option>
                      <a-select-option value="6"> 6 </a-select-option>
                    </a-select>
                  </a-radio>
                  <a-radio :value="2" style="margin-left: 50%">
                    <span>末级科目</span>
                  </a-radio>
                </a-radio-group>
              </li>-->
<!--              <li
              ><span>科目类型：</span>
                <a-select
                  mode="multiple"
                  option-label-prop="label"
                  placeholder="可多选"
                  style="width: 80%"
                >
                  <a-select-option label="全部" value=""> 全部 </a-select-option>
                  <a-select-option label="损益" value="1"> 损益 </a-select-option>
                </a-select>
              </li>-->
              <li
              ><span>样&emsp;&emsp;式：</span>
                <a-select default-value="1" style="width: 185px" @change="handleChange">
                  <a-select-option value="1"> 金额式 </a-select-option>
                  <a-select-option value="2"> 数量金额式 </a-select-option>
                  <a-select-option value="3"> 外币金额式 </a-select-option>
                  <a-select-option value="4"> 数量外币式 </a-select-option>
                </a-select>
                <span>&emsp;字&emsp;&emsp;号：</span>
                <a-select default-value="1" style="width: 185px" @change="handleChange">
                  <a-select-option value="1"> 大号字体 </a-select-option>
                  <a-select-option value="2"> 小号字体 </a-select-option>
                </a-select>
              </li>
              <li
              ><span>币&emsp;&emsp;种：</span>
                <a-select default-value="1" style="width: 200px" @change="handleChange">
                  <a-select-option value="1"> 人民币 </a-select-option>
                  <a-select-option value="2"> 美元 </a-select-option>
                  <a-select-option disabled value="3"> 欧元 </a-select-option>
                  <a-select-option value="4"> 港元 </a-select-option>
                </a-select></li
              >
            </ul>
            <a-checkbox-group @change="onChange">
              <a-checkbox style="width: 200px" value="A"> 包含未记账 </a-checkbox>
              <a-checkbox style="width: 200px" value="B"> 期间无发生显示累计 </a-checkbox>
            </a-checkbox-group>
          </a-tab-pane>
          <a-tab-pane v-if="queryPlan!=null" key="1" tab="个人标准模式">
            <ul>
              <li>
                <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
              </li>
              <li>
                <span>期间：</span>
                <span>
                    <a-select
                      v-model:value="strDate"
                      :disabled="dateselflg"
                      show-search
                      style="width: 180px;text-align: center;"
                      @change="handleChangeStrDate"
                      @focus="focusStrDate"
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
                    :disabled="dateselflg"
                    show-search
                    style="width: 180px;text-align: center;"
                    @change="handleChangeEndDate"
                    @focus="focusEndDate"
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
              </li>
<!--              <li>
                <span>科目类型：</span>
                <a-select v-model:value="styleValue" style="width: 400px;" @change="styleChange">
                  <a-select-option v-for="d in styleList" :value="d.cclass">
                    {{ d.cclass }}
                  </a-select-option>
                </a-select>
              </li>-->
              <li>
                <span>科目：</span>
                <a-select
                  v-model:value="km"
                  :filter-option="filterOption"
                  allowClear
                  show-search
                  style="width: 400px;"
                  @change="handleChangeMinKm"
                  @focus="handleFocusMinKm"
                >
                  <a-select-option v-for="d in minkmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>
<!--                <a-select
                  v-model:value="minKm"
                  :filter-option="filterOption"
                  allowClear
                  show-search
                  style="width: 180px;"
                  @change="handleChangeMinKm"
                  @focus="handleFocusMinKm"
                >
                  <a-select-option v-for="d in minkmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>
                <span>&emsp;~&emsp;</span>
                <a-select
                  v-model:value="maxKm"
                  :filter-option="filterOption"
                  allowClear
                  show-search
                  style="width: 180px;"
                  @focus="handleFocusMaxKm"
                >
                  <a-select-option v-for="d in maxkmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>-->
              </li>
<!--              <li>
                <a-radio-group v-model:value="jc" style="width: 100%">
                  <a-radio :value="1">
                    <span>级次</span>
                  </a-radio>
                  <a-select
                    v-model:value="minJc"
                    default-value="1"
                    style="width: 80px; text-align: center;margin-left: 7%;"
                    @focus="handleFocusMinCj"
                  >
                    <a-select-option v-for="d in minJcList" :value="d.value">
                      {{ d.label }}
                    </a-select-option>
                  </a-select>
                  <span>&emsp;~&emsp;</span>
                  <a-select
                    v-model:value="maxJc"
                    default-value="1"
                    style="width: 80px; text-align: center"
                    @focus="handleFocusMaxCj"
                  >
                    <a-select-option v-for="d in maxJcList" :value="d.value">
                      {{ d.label }}
                    </a-select-option>
                  </a-select>
                  <a-radio :value="2" style="margin-left: 19%;">
                    <span>末级科目</span>
                  </a-radio>
                </a-radio-group>
              </li>-->
              <li>
                <span>币种：</span>
                <a-select v-model:value="bz" style="width: 400px;">
                  <a-select-option v-for="d in bzList" :value="d.id">
                    {{ d.currChName }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>字号：</span>
                <a-select v-model:value="fontSize" style="width: 400px;">
                  <a-select-option value="MAX"> 大号字体 </a-select-option>
                  <a-select-option value="MIN"> 小号字体 </a-select-option>
                </a-select>
              </li>
              <li>
                <a-checkbox v-model:checked="ishaveRjz" style="width: 130px"> 包含未记账 </a-checkbox>
              </li>
            </ul>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>
  </BasicModal>
</template>

<script lang="ts" setup="props, { content }">
import {ref, unref, onMounted, reactive} from 'vue';
import { BasicModal, useModalInner } from '/@/components/Modal';
import { PageWrapper } from '/@/components/Page';
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
  Tabs as ATabs,
  message,
} from 'ant-design-vue';
import { useTabs } from '/@/hooks/web/useTabs';
import router from '/@/router';
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
import {findByAccId} from "/@/api/record/system/account";
import {groupStandardAndTemplate} from "/@/api/codekemu/codekemu";
import {
  filterAccListByAuth, findAllXjOrLlList,
  getAdInfoDatas,
  getAllAccAuths
} from "/@/api/record/system/financial-settings";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";

const { closeCurrent } = useTabs(router);
const ARangePicker = ADatePicker.RangePicker;
const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const ARadioGroup = ARadio.Group;
const ARadioButton = ARadio.Button;
const ACheckboxGroup = ACheckbox.Group;
const ATabPane = ATabs.TabPane;
const emit=defineEmits(['register','save']);
const data = [];
const activeKey: any = ref('');
const formItems: any = ref({});
// 会计区间
const dateList: any = ref([]);
// 会计科目
const kmList: any = ref([]);
//级次
const jcList: any = ref([]);
//币种
const bzList: any = ref([]);
// const year = new Date().getFullYear();
const year = ref(useCompanyOperateStoreWidthOut().getLoginDate.slice(0, 4))
const month = parseInt(new Date().getMonth()) + 1;
const aaa = month < 10 ? '0' + month : month;
const endDate: any = ref<String>(year.value + '-' + aaa);
const strDate: any = ref<String>(year.year + '-01');
let endDateList: any = ref([]);
let strDateList: any = ref([]);
let minkmList: any = ref([]);
let maxkmList: any = ref([]);
const minKm: any = ref<string>();
const maxKm: any = ref<string>();
const km: any = ref<string>();

const jc = ref<number>(1);
let maxJcList: any = ref([]);
let minJcList: any = ref([]);
const minJc = ref<number>(1);
const maxJc = ref<number>(1);

const showStyle = ref<string>('J');
const fontSize = ref<string>('MIN');
const bz = ref<string>('');

const ishaveRjz = ref<boolean>(true);
const isShowQjlh = ref<boolean>(false);
const userStore:any = useUserStore();
// 期间是否可选
let dateselflg: any = ref(false);
// 日期是否可选
let timeselflg: any = ref(true);
const riqi: any = ref('');
const queryPlan: any = ref([]);
// 数据库模式名称
const database = ref(getCurrentAccountName(false));
const databaseTrue = ref(getCurrentAccountName(true));
let styleValue: any = ref('全部');
let styleList: any = ref([{
  cclass:'全部',
  flagYusuan:''
}]);
// 页面变量
const pageParameter = reactive({
  companyCode: '',
  companyName: '',
})

const [register, { closeModal, setModalProps }] = useModalInner((data) => {
  // 方式2
  formItems.value.openOne = data.data.openOne;
  setModalProps({ minHeight: 400 });
  getQueryPlan()
});
let isChanged = false;

function tabChange(val) {
  if(val===''){
    styleValue.value='全部'
    minKm.value=''
    maxKm.value=''
    jc.value=1
    minJc.value=1
    maxJc.value=1
    bz.value=''
  }else if(val==='1'){
    getQueryPlan()
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

async function timechange() {
  // 切换数据库
  dateselflg.value = riqi.value.length > 0;
  if (riqi.value.length > 0) {
    const start = timeformat(riqi.value[0]._d);
    // 会计科目
    const s = database.value+'-'+start.substring(0,4);
    kmList.value=await useRouteApi(findAllXjOrLlList,{schemaName:s})({ strDate: strDate.value, endDate: 'test', accId: database.value,userId:userStore.getUserInfo.id });
    kmList.value = kmList.value.filter(item=>{
      item.value = item.ccode + '-' + item.ccodeName
      return item.bend=='1' && item.bcash == '1'
    })
  }
}

function onChange() {}
async function handleOk() {
  if (strDate.value.length <= 0 && endDate.value <= 0 && riqi.value.length === 0) {
    message.error('请选择会计区间!');
    return;
  }
  formItems.value.strDate = strDate.value;
  formItems.value.endDate = endDate.value;

  formItems.value.minKm = minKm.value;
  formItems.value.maxKm = maxKm.value;
  formItems.value.km = km.value;

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

  formItems.value.ishaveRjz = ishaveRjz.value;
  formItems.value.isShowQjlh = isShowQjlh.value;
  // 过滤会计科目
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
  formItems.value.ishaveRjz = ishaveRjz.value;
  formItems.value.styleValue = styleValue.value;
  formItems.value.styleList = styleValue.value !== '全部' ? styleList.value.filter(s => s.cclass === styleValue.value) : styleList.value.filter(s => s.cclass !== styleValue.value);
  formItems.value.pageParameter = pageParameter;
  // formItems.value.dynamicTenantId = database.value+'-'+formItems.value.strDate.substring(0,4)
  formItems.value.dynamicTenantId = databaseTrue.value

  const queryPlanEntity = {
    accountId: database.value,
    owningMenuName: '现金余额表',
    owningPlan: activeKey.value,
    planPerson: userStore.getUserInfo.id,
    queryConditions: {
      ishaveRjz: formItems.value.ishaveRjz,
      bend: formItems.value.jc,
      strDate: formItems.value.strDate,
      endDate:formItems.value.endDate,
      minJc:formItems.value.minJc,
      maxJc:formItems.value.maxJc,
      minKm:formItems.value.minKm,
      maxKm:formItems.value.maxKm,
      km:formItems.value.km,
      timflg:'qj',
      bz:formItems.value.bz,
      fontSize:formItems.value.fontSize,
      styleValue:formItems.value.styleValue
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
    kmList.value=await useRouteApi(findAllXjOrLlList,{schemaName:s})({ strDate: strDate.value, endDate: 'test', accId: database.value,userId:userStore.getUserInfo.id });
    kmList.value = kmList.value.filter(item=>{
      item.value = item.ccode + '-' + item.ccodeName
      return item.bend=='1' && item.bcash == '1'
    })
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
    kmList.value=await useRouteApi(findAllXjOrLlList,{schemaName:s})({ strDate: strDate.value, endDate: 'test', accId: database.value,userId:userStore.getUserInfo.id });
    kmList.value = kmList.value.filter(item=>{
      item.value = item.ccode + '-' + item.ccodeName
      return item.bend=='1' && item.bcash == '1'
    })
  }
}
async function handleChange() {
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
  //获取焦点时 如果最小科目已选择 判断过滤小于最小科目的数据
  if (minJc.value) {
    maxJcList.value = jcList.value.filter((o) => o.value >= minJc.value);
  } else {
    maxJcList.value = jcList.value;
  }
};

// async function styleChange(val) {
//   minKm.value=''
//   maxKm.value=''
//   formItems.value.minKm = '';
//   formItems.value.maxKm = '';
//
//   const start = jc.value===1?strDate.value:timeformat(riqi.value[0]._d);
//   const s = database.value+'-'+start.substring(0,4);
//   // 重新获取科目类型
//   // let accstyle =await useRouteApi(company_findAllStyleByUnique,{schemaName:s})('');
//   if(val!=='全部'){
//     if(val!=='财务会计'&&val!=='预算会计'){
//       kmList.value=[]
//       formItems.value.kmList=[]
//       let list=await newfindAllCode(s,start);
//       kmList.value=list.filter(ck=>ck.cclass===val)
//       formItems.value.kmList = kmList.value
//     }else if(val=='财务会计'){
//       kmList.value=[]
//       formItems.value.kmList=[]
//       /*for (let i = 0; i < accstyle.length; i++) {
//         if(accstyle[i].flagYusuan!=='1'){
//           let list=await newfindAllCode(s,start);
//           for (let j = 0; j < list.length; j++) {
//             kmList.value.push({
//               ccode:list[j].ccode,
//               value:list[j].ccode+'-'+list[j].ccodeName,
//             });
//           }
//         }
//       }*/
//     }else if(val=='预算会计'){
//       kmList.value=[]
//       formItems.value.kmList=[]
//       /*for (let i = 0; i < accstyle.length; i++) {
//         if(accstyle[i].flagYusuan=='1'){
//           let list=await newfindAllCode(s,start);
//           for (let j = 0; j < list.length; j++) {
//             kmList.value.push({
//               ccode:list[j].ccode,
//               value:list[j].ccode+'-'+list[j].ccodeName,
//             });
//           }
//         }
//       }*/
//     }
//   }
//   else{
//     kmList.value= await newfindAllCode(s,start)
//   }
// }
async function newfindAllCode(s,start) {
  // 会计科目
  let list=await useRouteApi(findAllXjOrLlList,{schemaName:s})({ strDate: start, endDate: 'test', accId: database.value,userId:userStore.getUserInfo.id });
  list.value = list.value.filter(item=>{
    item.value = item.ccode + '-' + item.ccodeName
    return item.bend=='1' && item.bcash == '1'
  })
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
  findByDateBase()
  //加载数据
  const res = await findPeriodByAccontId(database.value);
  dateList.value = res.filter(item => item.iyear == year.value)
  //级次
  const num = await findMaxJc(database.value);
  for (let i = 1; i < num + 1; i++) {
    jcList.value.push({
      label: i,
      value: i,
    });
  }
  //币种
  bzList.value = await findBzAll();
  bzList.value.push({ id: '', currChName: '全部' });

  // 如果没有当前年度 获取 上年第一个区间
  const test = res.filter((o) => o.iyear == year.value);
  if (test.length == 0) {
    strDate.value = res[0].value;
    endDate.value = res[0].value;
  }
  await handleChangeEndDate();

  /******************************** 科目类型 *********************************/
    // 获取对应的账套科目 所属的 会计准则、科目模板
  /*let codelistall= await useRouteApi(groupStandardAndTemplate, {schemaName: databaseTrue})({databaseNum: databaseTrue.value,iyear:strDate.value.substring(0,4)});
  // 会计准则
  const accstandard= await findByStandardUnique(codelistall[0].uniqueAccStandard)
  let accStyleUnique= await findByAccStyle(accstandard.accStyleUnique)

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
  }*/
  /******************************** 科目类型 *********************************/
}
onMounted(async () => {
  AllCondition()
});
async function getQueryPlan() {
  let queryPlanIngo= await findByQueryPlan(database.value,'现金余额表')
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
    styleValue.value = JSON.parse(queryPlanIngo.queryConditions).styleValue
    fontSize.value = JSON.parse(queryPlanIngo.queryConditions).fontSize
  }
}

function dynamicAdReload(data) {
  database.value = data.accId
  databaseTrue.value = data.accountMode
  year.value = data.year
  AllCondition();
}
</script>
<style>
.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}
</style>
<style lang="less" scoped>
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
    height: 400px;
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

  label {
    text-align: right;
    width: 110px;
    display: inline-block;
    padding: 5px 10px;
  }

  .ant-radio-group {
    .ant-radio-wrapper {
      width: 70px;
      .ant-radio-input{
        border-color: slategrey;
      }
    }
    p:nth-of-type(2){
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
