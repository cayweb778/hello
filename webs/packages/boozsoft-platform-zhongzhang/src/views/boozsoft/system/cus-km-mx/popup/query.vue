<template>
  <BasicModal
    :centered="true"
    width="850px"
    v-bind="$attrs"
    title="客户科目明细账查询"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
  >
    <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
    <div class="nc-open-content" style="height: 100%">
      <div class="open-content-up">
        <div class="ocup-position"> 系统方案 </div>
        <div class="ocup-position"> 个人方案 </div>
        <a-tabs type="card" tabPosition="left" style="height: 400px" v-model:activeKey="currentTabsKey" @change="tabChange">
          <a-tab-pane key="" tab="标准模式">
            <ul>
              <li>
                <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
              </li>
              <li>
                <TimeTool v-if="temp123" @reloadTable="timeReload" :databaseyearCopy="accYear" :yewuMonth="month"/>
                  <span>期间：</span>
                  <a-select
                    v-model:value="strDate"
                    show-search
                    option-filter-prop="children"
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
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                  <span>&emsp;~&emsp;</span>
                  <a-select
                    v-model:value="endDate"
                    show-search
                    option-filter-prop="children"
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
                  <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
              </li>
              <li>
                <span>科目类型：</span>
                <a-select v-model:value="styleValue" style="width: 170px;" @change="styleChange">
                  <a-select-option v-for="d in styleList" :value="d.cclass">
                    {{ d.cclass }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <span>凭证类别：</span>
                <a-select mode="tags" v-model:value="pzType" style="width: 174px;" :class="pzType=='未发现凭证类型授权'?'selectColorRed':'selectColorBlack'">
                  <a-select-option v-for="d in pzTypeList" :value="d" >
                    {{ d }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
              </li>
              <li>
                <span>科目：</span>
                <a-select
                  v-model:value="minKm"
                  show-search
                  allowClear=true
                  option-filter-prop="children"
                  style="width: 440px"
                  :filter-option="filterOption"
                  @focus="handleFocusMinKm"
                >
                  <a-select-option v-for="d in minkmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
              </li>

              <li><span>客户：</span>
                <a-select
                  v-model:value="minDept"
                  show-search
                  style="width: 200px"
                  allowClear=true
                  :filter-option="filterOption"
                  @change="handleChangeMinDept"
                >
                  <a-select-option v-for="d in deptList" :value="d.uniqueCode">
                    {{ d.label }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                <span>&emsp;~&emsp;</span>
                <a-select
                  v-model:value="maxDept"
                  show-search
                  style="width: 200px"
                  allowClear=true
                  :filter-option="filterOption"
                  @focus="handleFocusMaxDept"
                >
                  <a-select-option v-for="d in deptList" :value="d.uniqueCode">
                    {{ d.label }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
              </li>
              <li>
                <span>币种：</span>
                <a-select style="width: 200px" v-model:value="bz">
                  <a-select-option v-for="d in bzList" :value="d.id">
                    {{ d.currChName }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                <span>字号：</span>
                <a-select style="width: 200px" v-model:value="fontSize">
                  <a-select-option value="MAX"> 大号字体 </a-select-option>
                  <a-select-option value="MIN"> 小号字体 </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
              </li>
              <li>
                <a-checkbox v-model:checked="ishaveRjz" style="width: 130px"> 包含未记账 </a-checkbox>
              </li>
            </ul>
          </a-tab-pane>
          <a-tab-pane key="0" tab="集团模式">
<!--            <ul>-->
<!--              <li>-->
<!--                <span>核算单位：</span>-->

<!--                <a-select-->
<!--                  mode="multiple"-->
<!--                  style="width: 80%"-->
<!--                  placeholder="可多选"-->
<!--                  option-label-prop="label"-->
<!--                >-->
<!--                  <a-select-option value="湖北万亚" label="湖北万亚"> 湖北万亚 </a-select-option>-->
<!--                  <a-select-option value="北京希格" label="北京希格"> 北京希格 </a-select-option>-->
<!--                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>-->
<!--              </li>-->
<!--              <li-->
<!--              ><span>期&emsp;&emsp;间：</span>-->
<!--                <a-select-->
<!--                  default-value="2020-01"-->
<!--                  style="width: 218px; text-align: center"-->
<!--                  @change="handleChange"-->
<!--                >-->
<!--                  <a-select-option value="2020-01"> 2020-01 </a-select-option>-->
<!--                  <a-select-option value="2020-02"> 2020-02 </a-select-option>-->
<!--                  <a-select-option value="2020-03" disabled> 2020-03 </a-select-option>-->
<!--                  <a-select-option value="2020-04"> 2020-04 </a-select-option>-->
<!--                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>-->
<!--                <span>&emsp;~&emsp;</span>-->
<!--                <a-select-->
<!--                  default-value="2020-01"-->
<!--                  style="width: 218px; text-align: center"-->
<!--                  @change="handleChange"-->
<!--                >-->
<!--                  <a-select-option value="2020-01"> 2020-01 </a-select-option>-->
<!--                  <a-select-option value="2020-02"> 2020-02 </a-select-option>-->
<!--                  <a-select-option value="2020-03" disabled> 2020-03 </a-select-option>-->
<!--                  <a-select-option value="2020-04"> 2020-04 </a-select-option>-->
<!--                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>-->
<!--              </li>-->
<!--              <li-->
<!--              ><span>科&emsp;&emsp;目：</span>-->

<!--                <a-select-->
<!--                  allowClear=true-->
<!--                  show-search-->
<!--                  placeholder="最小科目"-->
<!--                  style="width: 218px"-->
<!--                  :default-active-first-option="false"-->
<!--                  :show-arrow="false"-->
<!--                  :filter-option="false"-->
<!--                  :not-found-content="null"-->
<!--                  @change="handleChange"-->
<!--                >-->
<!--                  <a-select-option v-for="d in data" :key="d.value">-->
<!--                    {{ d.text }}-->
<!--                  </a-select-option>-->
<!--                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>-->
<!--                <span>&emsp;~&emsp;</span>-->
<!--                <a-select-->
<!--                  show-search-->
<!--                  allowClear=true-->
<!--                  placeholder="最大科目"-->
<!--                  style="width: 218px"-->
<!--                  :default-active-first-option="false"-->
<!--                  :show-arrow="false"-->
<!--                  :filter-option="true"-->
<!--                  :not-found-content="null"-->
<!--                  @change="handleChange"-->
<!--                >-->
<!--                  <a-select-option v-for="d in data" :key="d.value">-->
<!--                    {{ d.text }}-->
<!--                  </a-select-option>-->
<!--                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>-->
<!--              </li>-->
<!--              <li>-->
<!--                <a-radio-group @change="onChange" style="width: 100%">-->
<!--                  <a-radio :value="1">-->
<!--                    <span>级次</span>-->
<!--                    <a-select-->
<!--                      default-value="1"-->
<!--                      style="width: 150px; text-align: center"-->
<!--                      @change="handleChange"-->
<!--                    >-->
<!--                      <a-select-option value="1"> 1 </a-select-option>-->
<!--                      <a-select-option value="2"> 2 </a-select-option>-->
<!--                      <a-select-option value="3"> 3 </a-select-option>-->
<!--                      <a-select-option value="4"> 4 </a-select-option>-->
<!--                      <a-select-option value="5"> 5 </a-select-option>-->
<!--                      <a-select-option value="6"> 6 </a-select-option>-->
<!--                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>-->
<!--                    <span>&emsp;~&emsp;</span>-->
<!--                    <a-select-->
<!--                      default-value="1"-->
<!--                      style="width: 150px; text-align: center"-->
<!--                      @change="handleChange"-->
<!--                    >-->
<!--                      <a-select-option value="1"> 1 </a-select-option>-->
<!--                      <a-select-option value="2"> 2 </a-select-option>-->
<!--                      <a-select-option value="3"> 3 </a-select-option>-->
<!--                      <a-select-option value="4"> 4 </a-select-option>-->
<!--                      <a-select-option value="5"> 5 </a-select-option>-->
<!--                      <a-select-option value="6"> 6 </a-select-option>-->
<!--                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>-->
<!--                  </a-radio>-->
<!--                  <a-radio :value="2" style="margin-left: 50%">-->
<!--                    <span>末级科目</span>-->
<!--                  </a-radio>-->
<!--                </a-radio-group>-->
<!--              </li>-->
<!--              <li-->
<!--              ><span>科目类型：</span>-->
<!--                <a-select-->
<!--                  mode="multiple"-->
<!--                  style="width: 80%"-->
<!--                  placeholder="可多选"-->
<!--                  option-label-prop="label"-->
<!--                >-->
<!--                  <a-select-option value="" label="全部"> 全部 </a-select-option>-->
<!--                  <a-select-option value="1" label="损益"> 损益 </a-select-option>-->
<!--                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>-->
<!--              </li>-->
<!--              <li-->
<!--              ><span>样&emsp;&emsp;式：</span>-->
<!--                <a-select default-value="1" style="width: 185px" @change="handleChange">-->
<!--                  <a-select-option value="1"> 金额式 </a-select-option>-->
<!--                  <a-select-option value="2"> 数量金额式 </a-select-option>-->
<!--                  <a-select-option value="3"> 外币金额式 </a-select-option>-->
<!--                  <a-select-option value="4"> 数量外币式 </a-select-option>-->
<!--                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>-->
<!--                <span>&emsp;字&emsp;&emsp;号：</span>-->
<!--                <a-select default-value="1" style="width: 185px" @change="handleChange">-->
<!--                  <a-select-option value="1"> 大号字体 </a-select-option>-->
<!--                  <a-select-option value="2"> 小号字体 </a-select-option>-->
<!--                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>-->
<!--              </li>-->
<!--              <li-->
<!--              ><span>币&emsp;&emsp;种：</span>-->
<!--                <a-select default-value="1" style="width: 200px" @change="handleChange">-->
<!--                  <a-select-option value="1"> 人民币 </a-select-option>-->
<!--                  <a-select-option value="2"> 美元 </a-select-option>-->
<!--                  <a-select-option value="3" disabled> 欧元 </a-select-option>-->
<!--                  <a-select-option value="4"> 港元 </a-select-option>-->
<!--                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select></li-->
<!--              >-->
<!--            </ul>-->
<!--            <a-checkbox-group @change="onChange">-->
<!--              <a-checkbox value="A" style="width: 200px"> 包含未记账 </a-checkbox>-->
<!--              <a-checkbox value="B" style="width: 200px"> 期间无发生显示累计 </a-checkbox>-->
<!--            </a-checkbox-group>-->
          </a-tab-pane>
          <a-tab-pane v-if="queryPlan!=null" key="1" tab="个人模式">
            <ul>
              <li>
                <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
              </li>
              <li>
                <TimeTool v-if="temp123" @reloadTable="timeReload" :databaseyearCopy="accYear" :yewuMonth="month"/>
                <span>期间：</span>
                <a-select
                  v-model:value="strDate"
                  show-search
                  option-filter-prop="children"
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
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                <span>&emsp;~&emsp;</span>
                <a-select
                  v-model:value="endDate"
                  show-search
                  option-filter-prop="children"
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
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
              </li>
              <li>
                <span>科目类型：</span>
                <a-select v-model:value="styleValue" style="width: 170px;" @change="styleChange">
                  <a-select-option v-for="d in styleList" :value="d.cclass">
                    {{ d.cclass }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <span>凭证类别：</span>
                <a-select mode="tags" v-model:value="pzType" style="width: 174px;" :class="pzType=='未发现凭证类型授权'?'selectColorRed':'selectColorBlack'">
                  <a-select-option v-for="d in pzTypeList" :value="d" >
                    {{ d }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
              </li>
              <li>
                <span>科目：</span>
                <a-select
                  v-model:value="minKm"
                  show-search
                  allowClear=true
                  option-filter-prop="children"
                  style="width: 440px"
                  :filter-option="filterOption"
                  @focus="handleFocusMinKm"
                >
                  <a-select-option v-for="d in minkmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
              </li>

              <li><span>客户：</span>
                <a-select
                  v-model:value="minDept"
                  show-search
                  style="width: 200px"
                  allowClear=true
                  :filter-option="filterOption"
                  @change="handleChangeMinDept"
                >
                  <a-select-option v-for="d in deptList" :value="d.uniqueCode">
                    {{ d.label }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                <span>&emsp;~&emsp;</span>
                <a-select
                  v-model:value="maxDept"
                  show-search
                  style="width: 200px"
                  allowClear=true
                  :filter-option="filterOption"
                  @focus="handleFocusMaxDept"
                >
                  <a-select-option v-for="d in deptList" :value="d.uniqueCode">
                    {{ d.label }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
              </li>
              <li>
                <span>币种：</span>
                <a-select style="width: 200px" v-model:value="bz">
                  <a-select-option v-for="d in bzList" :value="d.id">
                    {{ d.currChName }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                <span>字号：</span>
                <a-select style="width: 200px" v-model:value="fontSize">
                  <a-select-option value="MAX"> 大号字体 </a-select-option>
                  <a-select-option value="MIN"> 小号字体 </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
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

<script setup="props, { content }" lang="ts">
import {CaretDownOutlined} from '@ant-design/icons-vue'
import {ref, unref, onMounted, reactive} from 'vue';
import { BasicModal, useModalInner } from '/@/components/Modal';
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
  findCodeKmByPeriod, findCusAll,
} from '/@/api/record/generalLedger/data';
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {useUserStore} from "/@/store/modules/user";
import {
  findByAccStyle,
  findByStandardUnique
} from "/@/api/accstandard/accstandard";
import {company_findByAccTypeAuth, findByAccId, findDataBase} from "/@/api/record/system/account";
import {groupStandardAndTemplate} from "/@/api/codekemu/codekemu";
import {
  currentAccountTypes,
  filterAccListByAuth,
  getAdInfoDatas,
  getAllAccAuths
} from "/@/api/record/system/financial-settings";
import {findByQueryPlan, saveQueryPlan} from "/@/api/query-plan/queryPlan";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import TimeTool from "/@/boozsoft/components/SelectTimeTools/TimeTool.vue";
const { closeCurrent } = useTabs(router);
const ARangePicker = ADatePicker.RangePicker;
const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const ARadioGroup = ARadio.Group;
const ARadioButton = ARadio.Button;
const ATabPane = ATabs.TabPane;
const emit=defineEmits([]);
const data = [];
const formItems: any = ref({});
let changeBeforeModel: any = {};
// 会计区间
const dateList: any = ref([]);
// 会计科目
const kmList: any = ref([]);
//币种
const bzList: any = ref([]);
const currentTabsKey = ref<string>('');
const queryPlan: any = ref([]);

const month = useCompanyOperateStoreWidthOut().getLoginDate.split('-')[1];
const endDate = ref<String>('');
const strDate = ref<String>('');;
let endDateList: any = ref([]);
let strDateList: any = ref([]);

let minkmList: any = ref([]);
const minKm = ref<string>();
//部门
const deptList: any = ref([])
const minDept = ref<string>();
const maxDept = ref<string>();

const showStyle = ref<string>('J');
const fontSize = ref<string>('MIN');
const bz = ref<string>('');

const ishaveRjz = ref<boolean>(true);
const userStore = useUserStore();
//查询条件
const seach: any = ref({});
let styleValue: any = ref('全部');
let styleList: any = ref([]);
// 数据库模式名称
const database = ref(getCurrentAccountName(true));
const accId = ref(getCurrentAccountName(false));
const accYear = ref('');
// 页面变量
const pageParameter = reactive({
  companyCode: '',
  companyName: '',
  accId: '',
  database: '',
  accNameAll: '',
})

const pzType: any = ref([]);
const pzTypeList: any = ref([]);
const authSwithIsCcode=ref('');
const [register, { closeModal, setModalProps }] = useModalInner((data) => {
  // 方式2
  formItems.value.openOne = data.data.openOne;
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value));
  setModalProps({ minHeight: 400 });
  getQueryPlan()
});

/******************* 弹框加载中 **************************/
import { Loading } from '/@/components/Loading';
import {findByAccIdAndIyear} from "/@/api/record/system/sys_data_auth_swith";
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
    strDate.value=accYear.value + '-01'
    endDate.value=accYear.value + '-'+month
    styleValue.value='全部'
    minKm.value=''
    minDept.value=''
    maxDept.value=''
    bz.value=''
    fontSize.value='MIN'
    ishaveRjz.value=true
  }else if(val==='1'){
    getQueryPlan()
  }
}

let isChanged = false;
async function handleOk() {
  if(kmList.value.length===0){
    message.error('请先设置客户辅助科目!');
    return;
  }
  if(deptList.value.length===0){
    message.error('请先设置客户信息!');
    return;
  }
  if (strDate.value.length <= 0 && endDate.value <= 0) {
    message.error('请选择会计区间!');
    return;
  }
  formItems.value.strDate = strDate.value;
  formItems.value.endDate = endDate.value;
  formItems.value.minDept = minDept.value===undefined?deptList.value[0].uniqueCode:minDept.value;
  formItems.value.maxDept = maxDept.value===undefined?deptList.value[0].uniqueCode:maxDept.value;
  formItems.value.km = minKm.value===undefined?'':minKm.value;
  formItems.value.showStyle = showStyle.value;
  formItems.value.fontSize = fontSize.value;
  formItems.value.bzName = bzList.value.filter((o) => o.id === bz.value)[0].currChName;
  formItems.value.ishaveRjz = ishaveRjz.value;
  formItems.value.styleValue = styleValue.value;
  formItems.value.pageParameter = pageParameter;

  // 过滤部门
  let list = deptList.value;
  if (maxDept.value) {
    list = list.filter((o) => parseInt(o.uniqueCode) <= parseInt(maxDept.value));
  }
  if (minDept.value) {
    list = list.filter((o) => parseInt(o.uniqueCode) >= parseInt(minDept.value));
  }
  formItems.value.deptList = list;

  const queryPlanEntity={
    accountId:database.value,
    owningMenuName:'客户科目明细表',
    owningPlan:currentTabsKey.value,
    planPerson:userStore.getUserInfo.id,
    queryConditions:{
      strDate:formItems.value.strDate,
      endDate:formItems.value.endDate,
      styleValue:styleValue.value,
      minKm:formItems.value.km,
      minDept:formItems.value.minDept,
      maxDept:formItems.value.maxDept,
      bz:bz.value,
      fontSize:formItems.value.fontSize,
      ishaveRjz:formItems.value.ishaveRjz,
    }
  }
  await saveQueryPlan(queryPlanEntity);
  emit('save', unref(formItems));
  closeModal();
  return true;
}

async function styleChange(val) {
  minKm.value=''
  formItems.value.minKm = '';
  formItems.value.maxKm = '';
  minkmList.value=[]

  const start =strDate.value;
  // 重新获取科目类型
  // 获取对应的账套科目 所属的 会计准则、科目模板
  let codelistall= await useRouteApi(groupStandardAndTemplate, {schemaName: database.value})({databaseNum:database.value,iyear:strDate.value.split('-')[0]});
  const accstandard= await findByStandardUnique(codelistall[0].uniqueAccStandard)
  // 重新获取科目类型
  let accstyle= await findByAccStyle(accstandard.accStyleUnique)
  if(val=='全部'){
    let list=await newfindAllCode(database.value,start);
    minkmList.value=list
  }
  if(val!=='全部'){
    if(val!=='财务会计'&&val!=='预算会计'){
     let list=await newfindAllCode(database.value,start);
      minkmList.value=list.filter(ck=>ck.cclass===val)
    }else if(val=='财务会计'){
      for (let i = 0; i < accstyle.length; i++) {
        if(accstyle[i].flagYusuan!=='1'){
          let list=await newfindAllCode(database.value,start);
          for (let j = 0; j < list.length; j++) {
            minkmList.value.push({
              ccode:list[j].ccode,
              value:list[j].ccode+'-'+list[j].ccodeName,
            });
          }
        }
      }
    }else if(val=='预算会计'){
      for (let i = 0; i < accstyle.length; i++) {
        if(accstyle[i].flagYusuan=='1'){
          let list=await newfindAllCode(database.value,start);
          for (let j = 0; j < list.length; j++) {
            minkmList.value.push({
              ccode:list[j].ccode,
              value:list[j].ccode+'-'+list[j].ccodeName,
            });
          }
        }
      }
    }
  }
}
async function newfindAllCode(s,start) {
  // 会计科目
  let list=await useRouteApi(findCodeKmByPeriod,{schemaName:s})({ strDate: start, endDate: 'test', accId: accId.value,userId:userStore.getUserInfo.id });
  return list.filter(ck=>ck.bcus==='1' && ck.bend==='1');
}

async function handleClose() {
  if (null != formItems.value.openOne && formItems.value.openOne == 1) {
    await closeCurrent();
    router.push('/zhongZhang/home/welcome');
  }
}
const handleChangeMinDept = () => {
  // if(maxDept.value && maxDept.value < minDept.value){
  //   maxDept.value = minDept.value
  // }
};
const handleFocusMaxDept = () => {
  //获取焦点时 如果最小科目已选择 判断过滤小于最小部门的数据
  if(maxDept.value){
    deptList.value = deptList.value.filter( o => o.uniqueCode >= minDept.value)
  }else{
    deptList.value = deptList.value
  }
};

async function handleChangeStrDate() {
    // 会计科目
  // 开始日期变动  如果开始日期大于结束于日期 则重置结束日期
  if (endDate.value) {
    if (strDate.value > endDate.value) {
      endDate.value = '';
    }
    // 会计科目
    kmList.value=await useRouteApi(findCodeKmByPeriod,{schemaName:database.value})({ strDate: strDate.value, endDate: 'test', accId: accId.value,userId:userStore.getUserInfo.id });
  }
}
async function handleChangeEndDate() {
  // 开始日期变动  如果结束日期小于开始日期 则重置开始日期
  if (strDate.value) {
    if (strDate.value > endDate.value) {
      strDate.value = '';
    }
    // 会计科目
    let list=await useRouteApi(findCodeKmByPeriod,{schemaName:database.value})({ strDate: endDate.value, endDate: 'test', accId: accId.value,userId:userStore.getUserInfo.id });
   let list2= list.filter(function (x) {
      return x.bcus==='1' && x.bend==='1'
    })
    kmList.value=list2
  }
  if(authSwithIsCcode.value==='0'){
    if(kmList.value.length===0){
      minKm.value='未发现会计科目'
    }
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

const filterOption = (input: string, option: any) => {
  return option.children[0].children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};


/*************************************************************************************/
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
  let codes = getAdObjInfoByCoCode(accId.value, 'accId',test2)
  if (codes != null) {
    pageParameter.companyCode = codes.coCode
    pageParameter.companyName = codes.accNameCn
  }
}

async function AllCondition() {
  //币种
  let currency=await useRouteApi(findBzAll, {schemaName: database})('');
  bzList.value = currency.filter(c=>c.currChName.indexOf('人民币')<0);
  bzList.value.push({ id: '', currChName: '全部' });
  // 客户
  deptList.value = await useRouteApi(findCusAll,{schemaName:database.value})('');
  compState.loading=false
}

async function findByAccStyleAll(accId) {
  const codelistall= await useRouteApi(groupStandardAndTemplate, {schemaName: database})({databaseNum: database.value,iyear:strDate.value.substring(0,4)});
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
const temp123=ref(false)
async function dynamicAdReload(data) {
  openCompFullLoading()
  await findDataBase(data.accId,+data.year).then(async (item)=>{
    accId.value=item.accountId
    database.value =item.accountMode
    accYear.value =item.accountYear
    pageParameter.accId=item.accountId
    pageParameter.database=item.accountMode
    pageParameter.accNameAll=item.accName
    dateList.value=[]
    // 是否有个人查询记录
    getQueryPlan()
    //=================================== 按照查询循序 ==============================================
    // 重新加载年度
    dateList.value = await findPeriodByAccontId(item.accountId);
    // 1、期间
    strDate.value=accYear.value+'-01'
    endDate.value=accYear.value+'-'+month
    // 2、科目类型
    findByAccStyleAll(item.accountId)
    // 3、凭证类型
    findByPzTypeList()
    // 4、科目
    handleChangeEndDate();
    // 5 客户、币种
    AllCondition();
  })
  temp123.value=true
}
// 获取凭证类型权限
// 先判断是否主管：是主管 查询全部凭证类别【凭证类别表】
//               不是主管 判断 是否有 查询全部凭证类别权限
//                                  是 查询全部【凭证类别表】
//                                  不是 查询 【凭证类别权限授权表】
async function findByPzTypeList() {
  let newPzTypeList:any=[]
  // 凭证类型权限授权表
  let pztypeAuth =await useRouteApi(company_findByAccTypeAuth,{schemaName: database})({userId:userStore.getUserInfo.id,iyear:accYear.value})
  // 凭证类别表
  await useRouteApi(currentAccountTypes,{schemaName: database})('')
    .then(pztypeAll=>{
      pztypeAll.items.forEach(a=>{
        newPzTypeList.push(a.voucherTypeCode)
      })
    })


  // 数据权限表
  let authSwith= await useRouteApi(findByAccIdAndIyear,{schemaName: database})({accId:accId.value,iyear:accYear.value})
  // 没有开启 查询全部权限
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
    const list = test1.filter(v => v.iyear === strDate.value.split('-')[0] && v.accId === accId.value)
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
async function getQueryPlan() {
  let queryPlanIngo= await findByQueryPlan(database.value,'客户科目明细表')
  if(queryPlanIngo!=null){
    currentTabsKey.value='1'
    queryPlan.value=queryPlanIngo

    strDate.value=JSON.parse(queryPlanIngo.queryConditions).strDate
    endDate.value=JSON.parse(queryPlanIngo.queryConditions).endDate
    styleValue.value=JSON.parse(queryPlanIngo.queryConditions).styleValue
    minKm.value=JSON.parse(queryPlanIngo.queryConditions).minKm
    minDept.value=JSON.parse(queryPlanIngo.queryConditions).minDept
    maxDept.value=JSON.parse(queryPlanIngo.queryConditions).maxDept
    bz.value=JSON.parse(queryPlanIngo.queryConditions).bz
    fontSize.value=JSON.parse(queryPlanIngo.queryConditions).fontSize
    ishaveRjz.value=JSON.parse(queryPlanIngo.queryConditions).ishaveRjz
  }
}
/*************************************************************************************/
async function timeReload(data) {
  strDate.value=data.strDate
  endDate.value=data.endDate
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
    width: 110px;
    display: inline-block;
    padding: 5px 10px;
  }

  //.ant-radio-group {
  //  .ant-radio-wrapper {
  //    width: 70px;
  //    .ant-radio-input{
  //      border-color: slategrey;
  //    }
  //  }
  //  p:nth-of-type(2){
  //    margin-bottom: 0;
  //  }
  //}
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
