<template>
  <BasicModal
    destroyOnClose
    width="800px"
    v-bind="$attrs"
    title="明细账查询"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
  >
    <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
    <div class="nc-open-content" style="height: 100%">
        <div class="open-content-up">
          <div class="ocup-position"> 系统方案</div>
          <div class="ocup-position"> 个人方案</div>
          <a-tabs type="card" tabPosition="left" v-model:activeKey="currentTabsKey"
                  @change="tabChange">
            <a-tab-pane key="" tab="标准模式">
              <ul>
                <li>
                  <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
                </li>
                <li>
                  <TimeTool v-if="temp123" @reloadTable="timeReload" :databaseyearCopy="databaseyear" :yewuMonth="month"/>
                  <a-radio-group style="width: 100%;margin-left: -4%;" v-model:value="radiovalue" @change="radiochange">
                    <a-radio :value="1">
                      <span>期间：</span>
                    </a-radio>
                    <span style="margin-left: 10%">
                    <a-select
                      v-model:value="strDate"
                      show-search
                      :disabled="dateselflg"
                      option-filter-prop="children"
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
                      <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                  <span>&emsp;~&emsp;</span>
                  <a-select
                    v-model:value="endDate"
                    show-search
                    :disabled="dateselflg"
                    option-filter-prop="children"
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
                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                    </a-select>
                  </span>

                    <br><br>
                    <a-radio :value="2">
                      <span>日期：</span>
                    </a-radio>
                    <span style="margin-left: 10%">
                    <a-range-picker
                      style="width: 340px"
                      v-model:value="riqi"
                      :disabled="timeselflg"
                      @change="timechange"
                    />
                  </span>
                  </a-radio-group>
                </li>
                <li>
                  <span>科目类型：</span>
                  <a-select style="width: 130px;margin-left: 10px;" v-model:value="styleValue"
                            @change="styleChange">
                    <a-select-option v-for="d in styleList" :value="d">
                      {{ d }}
                    </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                  &nbsp;
                  <span>凭证类别：</span>
                  <a-select mode="tags" v-model:value="pzType" style="width: 130px;" :class="pzType=='未发现凭证类型授权'?'selectColorRed':'selectColorBlack'">
                    <a-select-option v-for="d in pzTypeList" :value="d" >
                      {{ d }}
                    </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
                <li><span>科目：</span>
                  <span style="margin-left: 2%;">
                  <a-select
                    v-model:value="minKm"
                    show-search
                    allowClear
                    option-filter-prop="children"
                    style="width: 150px;text-align: center;"
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
                    style="width: 150px;text-align: center;"
                    :filter-option="filterOption"
                    @focus="handleFocusMaxKm"
                  >
                  <a-select-option v-for="d in maxkmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                </a-select>
                </span>
                </li>
                <li>
                  <a-radio-group style="width: 100%;margin-left: -7%;" v-model:value="jc">
                    <a-radio :value="1">
                      <span>级次</span>
                    </a-radio>
                    <a-select
                      default-value="1"
                      v-model:value="minJc"
                      @focus="handleFocusMinCj"
                      style="width: 80px; text-align: center;margin-left: 10%;"
                    >
                      <a-select-option v-for="d in minJcList" :value="d.value">
                        {{ d.label }}
                      </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                    </a-select>
                    <span>&emsp;~&emsp;</span>
                    <a-select
                      default-value="1"
                      v-model:value="maxJc"
                      @focus="handleFocusMaxCj"
                      style="width: 80px; text-align: center;"
                    >
                      <a-select-option v-for="d in maxJcList" :value="d.value">
                        {{ d.label }}
                      </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                    </a-select>

                    <a-radio :value="2" style="margin-left: 9%;">
                      <span>末级科目</span>
                    </a-radio>
                  </a-radio-group>
                </li>
                <li>
                  <span>币种：</span>
                  <a-select v-model:value="bz" style="width: 160px;margin-left: 8px;">
                    <a-select-option v-for="d in bzList" :value="d.id">
                      {{ d.currChName }}
                    </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                  &nbsp;&nbsp;&nbsp;&nbsp;
                  <span>字号：</span>
                  <a-select v-model:value="fontSize" style="width: 110px;">
                    <a-select-option value="MAX"> 大号字体 </a-select-option>
                    <a-select-option value="MIN"> 小号字体 </a-select-option>
                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>

                <li>
                  <a-checkbox v-model:checked="ishaveRjz" style="width: 130px"> 包含未记账</a-checkbox>
                </li>
              </ul>
            </a-tab-pane>
            <a-tab-pane key="0" tab="集团模式">
              <ul>
                <li>
                  <span>选择组织：</span>
                  <a-select
                    style="width: 46%"
                    @change="orgSelectChange"
                    v-model:value="thisOrgValue"
                  >
                    <a-select-option :key="index" :value="item.uniqueCode"
                                     v-for="(item,index) in orgList">
                      {{ item.orgSimpName }}
                    </a-select-option>
                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                  <span style="margin-left: 1em">年度：</span>
                  <CustomizeYear :value="naturalYear" @CustomizeChange="customizeChange"
                                 readonly="false"/>
                </li>
                <li>
                  <span>选择公司代码：</span>
                  <a-select
                    mode="multiple"
                    style="width: 76%"
                    placeholder="可多选"
                    v-model:value="accIds"
                    option-label-prop="label"
                  >
                    <a-select-option :key="index" :value="item.coCode"
                                     v-for="(item,index) in accAuthList.filter(ite=>ite.independent != '1' && ite.accGroup == thisOrgValue)">
                      {{ item.coCode }}
                    </a-select-option>
                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
                <li><span>日&emsp;&emsp;期：</span>
                  <a-range-picker
                    style="width: 76%"
                    v-model:value="orgDates"
                  />
                </li>
                <li>
                  <span>科目类型：</span>
                  <a-select
                    style="width: 76%"
                    option-label-prop="label"
                    v-model:value="styleValue"
                    @change="orgCodeTypeSelectChange"
                  >
                    <a-select-option v-for="d in orgCodeTypeList" :value="d.cclass">
                      {{ d.cclass }}
                    </a-select-option>
                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
                <li><span>科&emsp;&emsp;目：</span>
                  <a-select
                    allowClear
                    show-search
                    placeholder="最小科目"
                    style="width: 180px"
                    :default-active-first-option="false"
                    :show-arrow="false"
                    :filter-option="false"
                    :not-found-content="null"
                    v-model:value="minKm"
                  >
                    <a-select-option v-for="d in orgCodeList" :value="d.ccode">
                      {{ d.ccode }} {{d.ccodeName}}
                    </a-select-option>
                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                  <span>&emsp;~&emsp;</span>
                  <a-select
                    show-search
                    allowClear
                    placeholder="最大科目"
                    style="width: 180px"
                    :default-active-first-option="false"
                    :show-arrow="false"
                    :filter-option="true"
                    :not-found-content="null"
                    v-model:value="maxKm"
                  >
                    <a-select-option v-for="d in orgCodeList" :value="d.ccode">
                      {{ d.ccode }} {{d.ccodeName}}
                    </a-select-option>
                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
                <li>
                  <a-radio-group v-model:value="jc" style="width: 100%">
                    <a-radio :value="1" style="margin-left: -7%;">
                      <span>级次</span>
                    </a-radio>
                    <a-select
                      v-model:value="minJc"
                      default-value="1"
                      style="width: 80px; text-align: center;margin-left: 7%;"
                      @focus="handleFocusMinCj"
                    >
                      <a-select-option :value="i" v-for="i in maxJici">{{i}}</a-select-option>
                      <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                    </a-select>
                    <span>&emsp;~&emsp;</span>
                    <a-select
                      v-model:value="maxJc"
                      default-value="1"
                      style="width: 80px; text-align: center"
                      @focus="handleFocusMaxCj"
                    >
                      <a-select-option :value="i" v-for="i in maxJici">{{i}}</a-select-option>
                      <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                    </a-select>
                    <a-radio :value="2" style="margin-left: 10%;">
                      <span>末级科目</span>
                    </a-radio>
                  </a-radio-group>
                </li>
                <li><span>样&emsp;&emsp;式：</span>
                  <a-select default-value="1" style="width: 155px" v-model:value="showStyle">
                    <a-select-option value="J"> 金额式</a-select-option>
                    <a-select-option value="SJ"> 数量金额式</a-select-option>
                    <a-select-option value="WJ"> 外币金额式</a-select-option>
                    <a-select-option value="SWJ"> 数量外币式</a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                  <span>&emsp;字&emsp;&emsp;号：</span>
                  <a-select default-value="MIN" style="width: 155px" v-model:value="fontSize" >
                    <a-select-option value="MAX"> 大号字体</a-select-option>
                    <a-select-option value="MIN"> 小号字体</a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
                <li>
                  <a-checkbox v-model:checked="ishaveRjz" style="width: 200px"> 包含未记账 </a-checkbox>
                  <a-checkbox  style="width: 200px"> 期间无发生显示累计 </a-checkbox>
                </li>
              </ul>
            </a-tab-pane>
            <a-tab-pane v-if="queryPlan!=null" key="1" tab="个人模式">
              <ul>
                <li>
                  <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
                </li>
                <li>
                  <TimeTool v-if="temp123" @reloadTable="timeReload" :databaseyearCopy="databaseyear" :yewuMonth="month"/>
                  <a-radio-group style="width: 100%;margin-left: -4%;" v-model:value="radiovalue" @change="radiochange">
                    <a-radio :value="1">
                      <span>期间：</span>
                    </a-radio>
                    <span style="margin-left: 10%">
                    <a-select
                      v-model:value="strDate"
                      show-search
                      :disabled="dateselflg"
                      option-filter-prop="children"
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
                    </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                  <span>&emsp;~&emsp;</span>
                  <a-select
                    v-model:value="endDate"
                    show-search
                    :disabled="dateselflg"
                    option-filter-prop="children"
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
                      </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                    </a-select>
                  </span>

                    <br><br>
                    <a-radio :value="2">
                      <span>日期：</span>
                    </a-radio>
                    <span style="margin-left: 10%">
                    <a-range-picker
                      style="width: 340px"
                      v-model:value="riqi"
                      :disabled="timeselflg"
                      @change="timechange"
                    />
                  </span>
                  </a-radio-group>
                </li>
                <li>
                  <span>科目类型：</span>
                  <a-select style="width: 130px;margin-left: 10px;" v-model:value="styleValue"
                            @change="styleChange">
                    <a-select-option v-for="d in styleList" :value="d">
                      {{ d }}
                    </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                  &nbsp;
                  <span>凭证类别：</span>
                  <a-select mode="tags" v-model:value="pzType" style="width: 130px;" :class="pzType=='未发现凭证类型授权'?'selectColorRed':'selectColorBlack'">
                    <a-select-option v-for="d in pzTypeList" :value="d" >
                      {{ d }}
                    </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>
                <li><span>科目：</span>
                  <span style="margin-left: 2%;">
                  <a-select
                    v-model:value="minKm"
                    show-search
                    allowClear
                    option-filter-prop="children"
                    style="width: 150px;text-align: center;"
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
                    style="width: 150px;text-align: center;"
                    :filter-option="filterOption"
                    @focus="handleFocusMaxKm"
                  >
                  <a-select-option v-for="d in maxkmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                </a-select>
                </span>
                </li>
                <li>
                  <a-radio-group style="width: 100%;margin-left: -7%;" v-model:value="jc">
                    <a-radio :value="1">
                      <span>级次</span>
                    </a-radio>
                    <a-select
                      default-value="1"
                      v-model:value="minJc"
                      @focus="handleFocusMinCj"
                      style="width: 80px; text-align: center;margin-left: 10%;"
                    >
                      <a-select-option v-for="d in minJcList" :value="d.value">
                        {{ d.label }}
                      </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                    </a-select>
                    <span>&emsp;~&emsp;</span>
                    <a-select
                      default-value="1"
                      v-model:value="maxJc"
                      @focus="handleFocusMaxCj"
                      style="width: 80px; text-align: center;"
                    >
                      <a-select-option v-for="d in maxJcList" :value="d.value">
                        {{ d.label }}
                      </a-select-option><template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                    </a-select>

                    <a-radio :value="2" style="margin-left: 9%;">
                      <span>末级科目</span>
                    </a-radio>
                  </a-radio-group>
                </li>
                <li>
                  <span>币种：</span>
                  <a-select v-model:value="bz" style="width: 160px;margin-left: 8px;">
                    <a-select-option v-for="d in bzList" :value="d.id">
                      {{ d.currChName }}
                    </a-select-option>
                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                  &nbsp;&nbsp;&nbsp;&nbsp;
                  <span>字号：</span>
                  <a-select v-model:value="fontSize" style="width: 110px;">
                    <a-select-option value="MAX"> 大号字体 </a-select-option>
                    <a-select-option value="MIN"> 小号字体 </a-select-option>
                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
                  </a-select>
                </li>

                <li>
                  <a-checkbox v-model:checked="ishaveRjz" style="width: 130px"> 包含未记账</a-checkbox>
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
import {ref, unref,  reactive} from 'vue';
import {BasicModal, useModalInner} from '/@/components/Modal';
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
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useTabs} from '/@/hooks/web/useTabs';
import router from '/@/router';
import moment, {Moment} from 'moment';
import {
  findPeriodByAccontId,
  findBzAll,
  findMaxJc,
  findCodeKmByPeriod,
} from '/@/api/record/generalLedger/data';
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getCurrentAccountName, hasBlank} from "/@/api/task-api/tast-bus-api";
import {useUserStore} from "/@/store/modules/user";
import {
  company_findAllStyleByUnique, findAllStyleByUnique,
  findByAccStyle,
  findByStandardUnique
} from "/@/api/accstandard/accstandard";
import {company_findByAccTypeAuth, findByAccId, findDataBase} from "/@/api/record/system/account";
import {groupStandardAndTemplate} from "/@/api/codekemu/codekemu";
import {
  currentAccountTypes, getAllAccAuths,
} from "/@/api/record/system/financial-settings";
import {findByQueryPlan, saveQueryPlan} from "/@/api/query-plan/queryPlan";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {getOrganizeAll} from "/@/api/record/group/im-organize";
import CustomizeYear from "/@/boozsoft/components/CustomizeYear/CustomizeYear.vue";
import {
  acctemplateFindByAccId,
  findAllByUniqueAccStandardAndTemplateIdAndCclass,
  findByTOrganization
} from "/@/api/acctemplate/acctemplate";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
import {findAvailablesOrg} from "/@/api/boozsoft/group/UsedForeignCurrency";
import {useMessage} from "/@/hooks/web/useMessage";
import TimeTool from "/@/boozsoft/components/SelectTimeTools/TimeTool.vue";
import dayjs, { Dayjs } from 'dayjs';

const {closeCurrent} = useTabs(router);
const ARangePicker = ADatePicker.RangePicker;
const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const ARadioGroup = ARadio.Group;
const ARadioButton = ARadio.Button;
const ACheckboxGroup = ACheckbox.Group;
const ATabPane = ATabs.TabPane;
const emit = defineEmits([]);
const data = [];
const formItems: any = ref({});
let changeBeforeModel: any = {};
// 会计区间
const dateList: any = ref([]);
// 会计科目
const kmList: any = ref([]);
//级次
const jcList: any = ref([]);
//币种
const bzList: any = ref([]);
const queryPlan: any = ref([]);
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

const ishaveRjz = ref<boolean>(true);
const isShowQjlh = ref<boolean>(false);
const userStore = useUserStore();
//查询条件
const seach: any = ref({});
// 期间是否可选
let dateselflg: any = ref(false);
// 日期是否可选
let timeselflg: any = ref(true);
const riqi: any = ref([dayjs(useCompanyOperateStoreWidthOut().getLoginDate, 'YYYY-MM-DD'), dayjs(useCompanyOperateStoreWidthOut().getLoginDate, 'YYYY-MM-DD')]);
let radiovalue: any = ref(1);
let styleValue: any = ref('全部');
let styleList: any = ref([]);
// 数据库模式名称
const databaseyear = ref('');
const database = ref(getCurrentAccountName(true));
const accId = ref(getCurrentAccountName(false));
const currentTabsKey = ref<string>('');
const accIds = ref([])
const thisOrgValue = ref('')
// 页面变量
const pageParameter = reactive({
  companyCode: '',
  companyName: '',
  accId: '',
  database: '',
  accNameAll: '',
})
const orgList = ref([])
const pzType: any = ref([]);
const pzTypeList: any = ref([]);

const [register, {closeModal, setModalProps}] = useModalInner((data) => {
  // 方式2
  formItems.value.openOne = data.data.openOne;
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value));
  setModalProps({minHeight: 400});
});
let isChanged = false;
const authSwithIsCcode=ref('');
/******************* 弹框加载中 **************************/
import { Loading } from '/@/components/Loading';
import {findByAccIdAndIyear} from "/@/api/record/system/sys_data_auth_swith";
import logger from "../../../../../../mock/logger";
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
  if (val === '') {             //----------------------------标准
    ishaveRjz.value = true
    jc.value = 1
    radiovalue.value = 1
    strDate.value = databaseyear.value + '-' + month
    endDate.value = databaseyear.value + '-' + month
    minJc.value = 1
    maxJc.value = 1
    minKm.value = ''
    maxKm.value = ''
    bz.value = ''
    styleValue.value = '全部'
    fontSize.value = 'MIN'
  } else if (val === '1') {     //----------------------------个人
    getQueryPlan()
  }else if (val === '0'){       //----------------------------集团
    if ('' == naturalYear.value){
      naturalYear.value = new Date().getFullYear()+''
      customizeChange(naturalYear.value)
      findByDateBase()
    }
  }
}

function timeformat(dateData) {
  let date = new Date(dateData);
  let y = date.getFullYear();
  let m = date.getMonth() + 1;
  m = m < 10 ? '0' + m : m;
  let d = date.getDate();
  d = d < 10 ? '0' + d : d;
  return y + '-' + m + '-' + d;
}

async function timechange() {
  // 切换数据库
  dateselflg.value = riqi.value.length > 0;
  if (riqi.value.length > 0) {
    const start = timeformat(riqi.value[0]._d);
    // 会计科目
    kmList.value = await useRouteApi(findCodeKmByPeriod, {schemaName: database.value})({
      strDate: start,
      endDate: 'test',
      accId: accId.value,
      userId: userStore.getUserInfo.id
    });
  }
}

function radiochange(val) {
  kmList.value = [];
  timeselflg.value = val.target.value === 1;
  dateselflg.value = val.target.value === 2;
  if (val.target.value === 1) {
    let a=parseInt(new Date().getMonth())+1
    let month=a<10?'0'+a:a
    strDate.value=dateList.value[0].value
    endDate.value=dateList.value.filter(v=>v.iperiodNum===String(month))[0].value
    riqi.value = '';
  } else {
    strDate.value = '';
    endDate.value = '';
  }
}

async function handleOk() {
  // if(minkmList.value.length==0){
  //   return createConfirmPop('没有科目,请添加科目后进行查询!');
  // }
  if (currentTabsKey.value !== '0') {
    formItems.value.queryMode = '1'
  } else {
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
    strDate.value=timeformat(orgDates.value[0])
    endDate.value=timeformat(orgDates.value[1])
    radiovalue.value='2'
 }
   if (strDate.value.length <= 0 && endDate.value.length <= 0 && riqi.value.length === 0) {
    message.error('请选择会计区间!');
    return;
  }
  formItems.value.strDate = strDate.value;
  formItems.value.endDate = endDate.value;
  formItems.value.timflg = radiovalue.value === 2 ? 'rq' : 'qj';
  if (radiovalue.value === 2) {
    formItems.value.strDate = timeformat(riqi.value[0]);
    formItems.value.endDate = timeformat(riqi.value[1]);
  }

  formItems.value.minKm = minKm.value;
  formItems.value.maxKm = maxKm.value;

  formItems.value.jc = jc.value;
  formItems.value.minJc = '1';
  formItems.value.maxJc = '1';
  if (jc.value === 1) {
    formItems.value.minJc = minJc.value;
    formItems.value.maxJc = maxJc.value;
  }
  formItems.value.showStyle = showStyle.value;
  formItems.value.fontSize = fontSize.value;
  formItems.value.bz = bz.value;
  formItems.value.bzName = bzList.value.filter((o) => o.id === bz.value)[0].currChName;

  formItems.value.ishaveRjz = ishaveRjz.value==true?'1':'0';
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
  formItems.value.styleValue = styleValue.value;
  formItems.value.pageParameter = pageParameter;
  formItems.value.pzType = pzType.value;

  const queryPlanEntity = {
    accountId: accId.value,
    owningMenuName: '明细表',
    owningPlan: currentTabsKey.value,
    planPerson: userStore.getUserInfo.id,
    queryConditions: {
      ishaveRjz: formItems.value.ishaveRjz,
      bend: formItems.value.jc,
      strDate: formItems.value.strDate,
      endDate: formItems.value.endDate,
      minJc: formItems.value.minJc,
      maxJc: formItems.value.maxJc,
      minKm: formItems.value.minKm,
      maxKm: formItems.value.maxKm,
      timflg: radiovalue.value === 2 ? 'rq' : 'qj',
      bz: formItems.value.bz,
      fontSize: formItems.value.fontSize,
      styleValue: styleValue.value,
      jc: jc.value,
      pzType:pzType.value
    }
  }
  await saveQueryPlan(queryPlanEntity);
  emit('save', {data: unref(formItems), accAuthList: accAuthList.value});
  closeModal();
  return true;
}

async function styleChange(val) {
  minKm.value = ''
  maxKm.value = ''
  formItems.value.minKm = '';
  formItems.value.maxKm = '';

  const start = jc.value === 1 ? strDate.value : timeformat(riqi.value[0]._d);
  // 重新获取科目类型
  let accstyle = await useRouteApi(company_findAllStyleByUnique, {schemaName: database.value})('');
  if (val !== '全部') {
    if (val !== '财务会计' && val !== '预算会计') {
      kmList.value = []
      formItems.value.kmList = []
      let list = await newfindAllCode(database.value, start);
      kmList.value = list.filter(ck => ck.cclass === val)
      formItems.value.kmList = kmList.value
    } else if (val == '财务会计') {
      kmList.value = []
      formItems.value.kmList = []
      for (let i = 0; i < accstyle.length; i++) {
        if (accstyle[i].flagYusuan !== '1') {
          let list = await newfindAllCode(database.value, start);
          for (let j = 0; j < list.length; j++) {
            kmList.value.push({
              ccode: list[j].ccode,
              value: list[j].ccode + '-' + list[j].ccodeName,
            });
          }
        }
      }
    } else if (val == '预算会计') {
      kmList.value = []
      formItems.value.kmList = []
      for (let i = 0; i < accstyle.length; i++) {
        if (accstyle[i].flagYusuan == '1') {
          let list = await newfindAllCode(database.value, start);
          for (let j = 0; j < list.length; j++) {
            kmList.value.push({
              ccode: list[j].ccode,
              value: list[j].ccode + '-' + list[j].ccodeName,
            });
          }
        }
      }
    }
  }
}

async function newfindAllCode(s, start) {
  // 会计科目
  let list = await useRouteApi(findCodeKmByPeriod, {schemaName: s})({
    strDate: start,
    endDate: 'test',
    accId: accId.value,
    userId: userStore.getUserInfo.id
  });
  return list;
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
  // 会计科目
  kmList.value = await useRouteApi(findCodeKmByPeriod, {schemaName: database.value})({
    strDate: strDate.value,
    endDate: 'test',
    accId: accId.value,
    userId: userStore.getUserInfo.id
  });
  if(authSwithIsCcode.value==='0'){
    if(kmList.value.length===0){
      minKm.value='未发现会计科目'
      maxKm.value='未发现会计科目'
    }
  }
  compState.loading = false;
}

async function handleChangeEndDate() {
  // 开始日期变动  如果结束日期小于开始日期 则重置开始日期
  if (strDate.value) {
    if (strDate.value > endDate.value) {
      strDate.value = '';
    }
    // 会计科目
    kmList.value = await useRouteApi(findCodeKmByPeriod, {schemaName: database.value})({
      strDate: endDate.value,
      endDate: 'test',
      accId: accId.value,
      userId: userStore.getUserInfo.id
    });
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

function filterOption(input: string, option: any) {
  return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
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
/************************************************************************/
const getAdObjInfoByCoCode = (value, type, accList) => {
  let list = accList.filter(item => item[type] == value)
  return list.length > 0 ? list[0] : null
}
const accAuthList = ref([])

async function findByDateBase() {
  // 获取所有账套信息 并过滤已授权的账套
  // 获取当前用户的权限根据用户信息汇率
  accAuthList.value = useCompanyOperateStoreWidthOut().getAccountAuthorizeList
  // 获取已授权的组织标识
  let accOrgAuthkeys = [...new Set(accAuthList.value.filter(item => !hasBlank(item.accGroup)).map(item => item.accGroup))]
  orgList.value = (await getOrganizeAll()).filter(item => accOrgAuthkeys.indexOf(item.uniqueCode) != -1)
  if (orgList.value.length > 0) {
    thisOrgValue.value = orgList.value[0]['uniqueCode']
    loadOrgInterrelatedData(orgList.value[0]['uniqueCode'])
    maxJici.value = parseInt(orgList.value[0]['jiciLength'] || 0)
  }
  let codes = getAdObjInfoByCoCode(accId.value, 'accId', accAuthList.value)
  if (codes != null) {
    pageParameter.companyCode = codes.coCode
    pageParameter.companyName = codes.accNameCn
  }

}

async function findByAccStyleAll(database) {
  const codelistall= await acctemplateFindByAccId(database)
  const accstandard= await findByStandardUnique(codelistall.uniqueAccStandard)
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
  const datainfo = await findByAccId(accId.value);
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
  compState.loading = false;
}
const handleChange = () => {

}
const onChange = () => {

}
const {createConfirm, createWarningModal, createMessage} = useMessage();

const temp123=ref(false)
async function dynamicAdReload(data) {
  openCompFullLoading()
  await findDataBase(data.accId,+data.year).then(async (item)=>{
    accId.value=item.accountId
    database.value=item.accountMode
    databaseyear.value=data.year
    pageParameter.accId=item.accountId
    pageParameter.database=item.accountMode
    pageParameter.accNameAll=data.target.accName
    pzTypeList.value=[]
    pzType.value=[]
    dateList.value=[]
    jcList.value=[]
    bzList.value=[]

    // 是否有个人查询记录
    getQueryPlan()
    //=================================== 按照查询循序 ==============================================
    // 重新加载年度
    dateList.value = await findPeriodByAccontId(item.accountId);
    // 1、期间
    strDate.value=databaseyear.value+'-01'
    endDate.value=databaseyear.value+'-'+month
    // 2、科目类型
    findByAccStyleAll(item.accountId)
    // 3、凭证类型
    findByPzTypeList()
    // 4、科目
    handleChangeStrDate();
    // 5、重新加载级次
    const num = await findMaxJc(item.accountId,data.year);
    for (let i = 1; i < num + 1; i++) {
      jcList.value.push({
        label: i,
        value: i,
      });
    }

    // 6、重新加载币种
    bzList.value = await useRouteApi(findBzAll, {schemaName: item.accountMode})('');
    bzList.value.push({ id: '', currChName: '全部' });
  })
  temp123.value=true
}
// 获取凭证类型权限
// 先判断是否主管：是主管 查询全部凭证类别【凭证类别表】
//               不是主管 判断 是否有 查询全部凭证类别权限
//                                  是 查询全部【凭证类别表】
//                                  不是 查询 【凭证类别权限授权表】
async function findByPzTypeList() {
  pzTypeList.value=[]
  pzType.value=[]

  let newPzTypeList:any=[]
  // 凭证类型权限授权表
  let pztypeAuth =await useRouteApi(company_findByAccTypeAuth,{schemaName: database})({userId:userStore.getUserInfo.id,iyear:databaseyear.value})
  // 凭证类别表
  await useRouteApi(currentAccountTypes,{schemaName: database})('')
    .then(pztypeAll=>{
      pztypeAll.items.forEach(a=>{
        newPzTypeList.push(a.voucherTypeCode)
      })
    })


  // 数据权限表
  let authSwith= await useRouteApi(findByAccIdAndIyear,{schemaName: database})({accId:accId.value,iyear:databaseyear.value})
  // 没有开启 查询全部权限
  if(authSwith!=='不存在' && authSwith.isVoucherType=='0'){
    authSwithIsCcode.value=authSwith.isCcode
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
  let queryPlanIngo = await findByQueryPlan(accId.value, '明细表')
  if (queryPlanIngo != null) {
    currentTabsKey.value = '1'
    queryPlan.value = queryPlanIngo

    ishaveRjz.value = JSON.parse(queryPlanIngo.queryConditions).ishaveRjz
    jc.value =JSON.parse(queryPlanIngo.queryConditions).jc
    strDate.value = JSON.parse(queryPlanIngo.queryConditions).strDate
    endDate.value = JSON.parse(queryPlanIngo.queryConditions).endDate
    let a=strDate.value.split('-').length
    if(a===3){
      radiovalue.value=2
      dateselflg.value=true
      timeselflg.value=false
      strDate.value=''
      endDate.value=''
    }
    minJc.value = JSON.parse(queryPlanIngo.queryConditions).minJc
    maxJc.value = JSON.parse(queryPlanIngo.queryConditions).maxJc
    minKm.value = JSON.parse(queryPlanIngo.queryConditions).minKm
    maxKm.value = JSON.parse(queryPlanIngo.queryConditions).maxKm
    bz.value = JSON.parse(queryPlanIngo.queryConditions).bz
    styleValue.value = JSON.parse(queryPlanIngo.queryConditions).styleValue
    fontSize.value = JSON.parse(queryPlanIngo.queryConditions).fontSize
    pzType.value = JSON.parse(queryPlanIngo.queryConditions).pzType
  }
}
/************************ 组织 ************************/
const dates = ref([])
const naturalYear = ref('')
const orgDates = ref([])
const yearShowOne = ref(false)

const orgCodeTypeList = ref([])
const orgCodeList = ref([])
const orgCodeAllList = ref([])
const orgCurrencyList = ref([])
const maxJici = ref(0)

const disabledDate = (current) => {
  if (!dates.value || dates.value.length === 0) {
    return false;
  }
  let min = dates.value[0]
  let max = dates.value[1]
  return current < min || current > max
};

function customizeChange(year) {
  orgDates.value = riqi.value
  dates.value = [moment(year + '-01-01', 'YYYY-MM-DD'), moment((parseInt(year) + 1) + '-01-01', 'YYYY-MM-DD')]
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
    orgCodeTypeList.value.push('全部')
    accStyle.forEach(v=>{
      orgCodeTypeList.value.push(v.cclass)
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
const orgSelectChange = (v) => {
  accIds.value = []
  let orgObj =  orgList.value.filter(item=>item.uniqueCode == v)[0]
  maxJici.value = parseInt(orgObj['jiciLength'] || 1)
  loadOrgInterrelatedData(orgObj?.uniqueCode)
}
const orgCodeTypeSelectChange = (v) => {
  orgCodeList.value = v=='全部'?JsonTool.parseProxy(orgCodeAllList.value):JsonTool.parseProxy(orgCodeAllList.value.filter(item=>item['cclass']==v ))
}
/************************ 组织 ************************/

function timeReload(data) {
  if(radiovalue.value===1){
    strDate.value=data.strDate
    endDate.value=data.endDate
  }else{
    let lastDay=getLastDay(data.endDate.split('-')[0],data.endDate.split('-')[1])
    riqi.value = [dayjs(data.strDate+'-01', 'YYYY-MM-DD'), dayjs(data.endDate+'-'+lastDay, 'YYYY-MM-DD')]
  }
}
//获得某月的最后一天
function getLastDay(year,month) {
  var new_year = year;    //取当前的年份
  var new_month = month++;//取下一个月的第一天，方便计算（最后一天不固定）
  if(month>12) {
    new_month -=12;        //月份减
    new_year++;            //年份增
  }
  var new_date = new Date(new_year,new_month,1);                //取当年当月中的第一天
  return (new Date(new_date.getTime()-1000*60*60*24)).getDate();//获取当月最后一天日期
}

function createConfirmPop(text) {
  createConfirm({
    iconType: 'warning',
    title: '警告',
    content: text,
    onOk: async() => {},
  })
  return false;
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
