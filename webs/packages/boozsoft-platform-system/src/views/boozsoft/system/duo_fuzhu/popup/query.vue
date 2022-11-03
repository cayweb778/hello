<template>
  <BasicModal
    :centered="true"
    width="850px"
    v-bind="$attrs"
    title="科目辅助余额表查询"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
  >
    <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
    <div class="nc-open-content" style="height: 100%">
      <div class="open-content-up">
        <div class="ocup-position"> 系统方案 </div>
        <div class="ocup-position"> 个人方案 </div>
        <a-tabs
          type="card"
          tabPosition="left"
          style="height: 450px"
          v-model:activeKey="currentTabsKey"
          @change="tabChange"
        >
          <a-tab-pane key="" tab="标准模式">
            <ul>
              <li>
                <AccountPicker theme="one" @reloadTable="dynamicAdReload" />
              </li>
              <li>
                <TimeTool @reloadTable="timeReload" />
                <span>期间：</span>
                <a-select
                  v-model:value="strDate"
                  show-search
                  option-filter-prop="children"
                  style="width: 200px"
                  @focus="focusStrDate"
                >
                  <a-select-option v-for="item in strDateList" :key="item.id" :value="item.value">
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
                >
                  <a-select-option v-for="item in endDateList" :key="item.id" :value="item.value">
                    {{ item.value }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
              </li>
              <li>
                <span>科目类型：</span>
                <a-select v-model:value="styleValue" style="width: 170px" @change="styleChange">
                  <a-select-option v-for="d in styleList" :value="d">
                    {{ d }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <span>凭证类别：</span>
                <a-select
                  v-model:value="pzType"
                  style="width: 174px"
                  :class="pzType == '未发现凭证类型授权' ? 'selectColorRed' : 'selectColorBlack'"
                >
                  <a-select-option v-for="d in pzTypeList" :value="d">
                    {{ d }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
              </li>
              <li>
                <span>科目：</span>
                <a-select
                  v-model:value="minKm"
                  show-search
                  allowClear="true"
                  option-filter-prop="children"
                  style="width: 440px"
                  :filter-option="filterOption"
                  @focus="handleFocusMinKm"
                >
                  <a-select-option v-for="d in minkmList" :value="d.ccode">
                    {{ d.ccode }}-{{ d.ccodeName }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
              </li>

              <li
                ><span>辅助项：</span>
                <div
                  style="
                    position: relative;
                    background-color: rgb(255, 255, 255);
                    border: 1px solid rgb(153, 153, 153);
                    padding: 10px 20px;
                    margin-top: -22px;
                    width: 75%;
                    margin-left: 20%;
                  "
                >
                  <div style="margin-left: -40px">
                    <a-checkbox v-model:value="deptCheck" @change="deptCheckFun">部门</a-checkbox>
                    <a-select
                      v-model:value="deptCheckList"
                      v-model:disabled="deptCheckDisabled"
                      show-search
                      style="width: 290px; margin-left: 32px"
                      mode="multiple"
                      :filter-option="filterOption"
                      :maxTagCount="2"
                    >
                      <a-select-option
                        v-for="item in deptDataList"
                        :key="item.uniqueCode"
                        :value="item.uniqueCode"
                      >
                        {{ item.deptName }}
                      </a-select-option>
                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                    <br />
                    <a-checkbox v-model:value="psnCheck" @change="psnCheckFun">个人</a-checkbox>
                    <a-select
                      v-model:value="psnCheckList"
                      v-model:disabled="psnCheckDisabled"
                      show-search
                      style="width: 290px; margin-left: 32px"
                      mode="multiple"
                      :filter-option="filterOption"
                      :maxTagCount="2"
                    >
                      <a-select-option
                        v-for="item in psnDataList"
                        :key="item.uniqueCode"
                        :value="item.uniqueCode"
                      >
                        {{ item.psnName }}
                      </a-select-option>
                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                    <br />
                    <a-checkbox v-model:value="cusCheck" @change="cusCheckFun">客户</a-checkbox>
                    <a-select
                      v-model:value="cusCheckList"
                      v-model:disabled="cusCheckDisabled"
                      show-search
                      :filter-option="filterOption"
                      style="width: 290px; margin-left: 32px"
                      mode="multiple"
                      :maxTagCount="1"
                    >
                      <a-select-option
                        v-for="item in cusDataList"
                        :key="item.uniqueCode"
                        :value="item.uniqueCode"
                      >
                        {{ item.custName }}
                      </a-select-option>
                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                    <br />
                    <a-checkbox
                      v-model:value="supCheck"
                      @change="supCheckFun"
                      style="margin-left: 15px"
                      >供应商</a-checkbox
                    >
                    <a-select
                      v-model:value="supCheckList"
                      v-model:disabled="supCheckDisabled"
                      show-search
                      style="width: 290px; margin-left: 17px"
                      mode="multiple"
                      :maxTagCount="1"
                      :filter-option="filterOption"
                    >
                      <a-select-option
                        v-for="item in supDataList"
                        :key="item.uniqueCode"
                        :value="item.uniqueCode"
                      >
                        {{ item.supName }}
                      </a-select-option>
                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                    <br />
                    <a-checkbox v-model:value="proCheck" @change="proCheckFun">项目</a-checkbox>
                    <a-select
                      v-model:value="proCheckList"
                      v-model:disabled="proCheckDisabled"
                      show-search
                      style="width: 290px; margin-left: 32px"
                      mode="multiple"
                      :maxTagCount="1"
                      :filter-option="filterOption"
                    >
                      <a-select-option
                        v-for="item in proDataList"
                        :key="item.uniqueCode"
                        :value="item.uniqueCode"
                      >
                        {{ item.projectName }}
                      </a-select-option>
                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                  </div>
                </div>
              </li>
              <li>
                <a-checkbox v-model:checked="ishaveRjz" style="width: 130px">
                  包含未记账
                </a-checkbox>
              </li>
            </ul>
          </a-tab-pane>
          <a-tab-pane key="0" tab="集团模式" />
          <a-tab-pane v-if="queryPlan != null" key="1" tab="个人模式">
            <ul>
              <li>
                <AccountPicker theme="one" @reloadTable="dynamicAdReload" />
              </li>
              <li>
                <TimeTool @reloadTable="timeReload" />
                <span>期间：</span>
                <a-select
                  v-model:value="strDate"
                  show-search
                  option-filter-prop="children"
                  style="width: 200px"
                  @focus="focusStrDate"
                >
                  <a-select-option v-for="item in strDateList" :key="item.id" :value="item.value">
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
                >
                  <a-select-option v-for="item in endDateList" :key="item.id" :value="item.value">
                    {{ item.value }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
              </li>
              <li>
                <span>科目类型：</span>
                <a-select v-model:value="styleValue" style="width: 170px" @change="styleChange">
                  <a-select-option v-for="d in styleList" :value="d">
                    {{ d }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <span>凭证类别：</span>
                <a-select
                  v-model:value="pzType"
                  style="width: 174px"
                  :class="pzType == '未发现凭证类型授权' ? 'selectColorRed' : 'selectColorBlack'"
                >
                  <a-select-option v-for="d in pzTypeList" :value="d">
                    {{ d }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
              </li>
              <li>
                <span>科目：</span>
                <a-select
                  v-model:value="minKm"
                  show-search
                  allowClear="true"
                  option-filter-prop="children"
                  style="width: 440px"
                  :filter-option="filterOption"
                  @focus="handleFocusMinKm"
                >
                  <a-select-option v-for="d in minkmList" :value="d.ccode">
                    {{ d.ccode }}-{{ d.ccodeName }}
                  </a-select-option>
                <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
              </li>

              <li
                ><span>辅助项：</span>
                <div
                  style="
                    position: relative;
                    background-color: rgb(255, 255, 255);
                    border: 1px solid rgb(153, 153, 153);
                    padding: 10px 20px;
                    margin-top: -22px;
                    width: 75%;
                    margin-left: 20%;
                  "
                >
                  <div style="margin-left: -40px">
                    <a-checkbox v-model:value="deptCheck" @change="deptCheckFun">部门</a-checkbox>
                    <a-select
                      v-model:value="deptCheckList"
                      v-model:disabled="deptCheckDisabled"
                      show-search
                      style="width: 290px; margin-left: 32px"
                      mode="multiple"
                      :filter-option="filterOption"
                      :maxTagCount="2"
                    >
                      <a-select-option
                        v-for="item in deptDataList"
                        :key="item.uniqueCode"
                        :value="item.uniqueCode"
                      >
                        {{ item.deptName }}
                      </a-select-option>
                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                    <br />
                    <a-checkbox v-model:value="psnCheck" @change="psnCheckFun">个人</a-checkbox>
                    <a-select
                      v-model:value="psnCheckList"
                      v-model:disabled="psnCheckDisabled"
                      show-search
                      style="width: 290px; margin-left: 32px"
                      mode="multiple"
                      :filter-option="filterOption"
                      :maxTagCount="2"
                    >
                      <a-select-option
                        v-for="item in psnDataList"
                        :key="item.uniqueCode"
                        :value="item.uniqueCode"
                      >
                        {{ item.psnName }}
                      </a-select-option>
                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                    <br />
                    <a-checkbox v-model:value="cusCheck" @change="cusCheckFun">客户</a-checkbox>
                    <a-select
                      v-model:value="cusCheckList"
                      v-model:disabled="cusCheckDisabled"
                      show-search
                      :filter-option="filterOption"
                      style="width: 290px; margin-left: 32px"
                      mode="multiple"
                      :maxTagCount="1"
                    >
                      <a-select-option
                        v-for="item in cusDataList"
                        :key="item.uniqueCode"
                        :value="item.uniqueCode"
                      >
                        {{ item.custName }}
                      </a-select-option>
                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                    <br />
                    <a-checkbox
                      v-model:value="supCheck"
                      @change="supCheckFun"
                      style="margin-left: 15px"
                      >供应商</a-checkbox
                    >
                    <a-select
                      v-model:value="supCheckList"
                      v-model:disabled="supCheckDisabled"
                      show-search
                      style="width: 290px; margin-left: 17px"
                      mode="multiple"
                      :maxTagCount="1"
                      :filter-option="filterOption"
                    >
                      <a-select-option
                        v-for="item in supDataList"
                        :key="item.uniqueCode"
                        :value="item.uniqueCode"
                      >
                        {{ item.supName }}
                      </a-select-option>
                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                    <br />
                    <a-checkbox v-model:value="proCheck" @change="proCheckFun">项目</a-checkbox>
                    <a-select
                      v-model:value="proCheckList"
                      v-model:disabled="proCheckDisabled"
                      show-search
                      style="width: 290px; margin-left: 32px"
                      mode="multiple"
                      :maxTagCount="1"
                      :filter-option="filterOption"
                    >
                      <a-select-option
                        v-for="item in proDataList"
                        :key="item.uniqueCode"
                        :value="item.uniqueCode"
                      >
                        {{ item.projectName }}
                      </a-select-option>
                    <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template></a-select>
                  </div>
                </div>
              </li>
              <li>
                <a-checkbox v-model:checked="ishaveRjz" style="width: 130px">
                  包含未记账
                </a-checkbox>
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
  import { ref, unref, onMounted, reactive } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import {
    DatePicker as ADatePicker,
    Select as ASelect,
    Input as AInput,
    Checkbox as ACheckbox,
    Radio as ARadio,
    Tabs as ATabs,
    Row as ARow,
    Col as ACol,
    message,
  } from 'ant-design-vue';
  import { useTabs } from '/@/hooks/web/useTabs';
  import router from '/@/router';
  import {
    findPeriodByAccontId,
    findBzAll,
    findCodeKmByPeriod,
    findCusAll,
  } from '/@/api/record/generalLedger/data';
  import { useRouteApi } from '/@/utils/boozsoft/datasource/datasourceUtil';
  import { getCurrentAccountName } from '/@/api/task-api/tast-bus-api';
  import { useUserStore } from '/@/store/modules/user';
  import { findByAccStyle, findByStandardUnique } from '/@/api/accstandard/accstandard';
  import {
    company_findByAccTypeAuth,
    findByAccId,
    findDataBase,
  } from '/@/api/record/system/account';
  import { companyfindAll, groupStandardAndTemplate } from '/@/api/codekemu/codekemu';
  import {
    currentAccountTypes,
    filterAccListByAuth,
    getAdInfoDatas,
    getAllAccAuths,
  } from '/@/api/record/system/financial-settings';
  import { findByQueryPlan, saveQueryPlan } from '/@/api/query-plan/queryPlan';
  import AccountPicker from '/@/boozsoft/components/AccountPicker/AccountPicker.vue';
  import TimeTool from '/@/boozsoft/components/SelectTimeTools/TimeTool.vue';
  const { closeCurrent } = useTabs(router);
  const ARangePicker = ADatePicker.RangePicker;
  const ASelectOption = ASelect.Option;
  const AInputSearch = AInput.Search;
  const ARadioGroup = ARadio.Group;
  const ACheckboxGroup = ACheckbox.Group;
  const ARadioButton = ARadio.Button;
  const ATabPane = ATabs.TabPane;
  const emit = defineEmits([]);
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

  const year = useCompanyOperateStoreWidthOut().getLoginDate.split('-')[0];
  const month = useCompanyOperateStoreWidthOut().getLoginDate.split('-')[1];
  const aaa = month;

  const endDate = ref<String>(year + '-' + aaa);
  const strDate = ref<String>(year + '-01');
  let endDateList: any = ref([]);
  let strDateList: any = ref([]);

  let minkmList: any = ref([]);
  const minKm = ref<string>();
  //部门
  const deptList: any = ref([]);
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
  });

  const pzType: any = ref('记');
  const pzTypeList: any = ref([]);
  const authSwithIsCcode = ref('');

  // ++++++++++++++++++++++++++++START+++++++++++++++++++++++++++++
  // ++++++++++++++++++++++++++ 辅助项check +++++++++++++++++++++++++++++++
  const deptCheck = ref(false);
  const psnCheck = ref(false);
  const cusCheck = ref(false);
  const supCheck = ref(false);
  const proCheck = ref(false);
  // ++++++++++++++++++++++++++ 辅助项选中值 +++++++++++++++++++++++++++++++
  const deptCheckList = ref([]);
  const psnCheckList = ref([]);
  const cusCheckList = ref([]);
  const supCheckList = ref([]);
  const proCheckList = ref([]);
  // ++++++++++++++++++++++++++ 辅助项 select 禁用状态 +++++++++++++++++++++++++++++++
  const deptCheckDisabled = ref(true);
  const psnCheckDisabled = ref(true);
  const cusCheckDisabled = ref(true);
  const supCheckDisabled = ref(true);
  const proCheckDisabled = ref(true);
  // ++++++++++++++++++++++++++++++++ 辅助项档案 +++++++++++++++++++++++++++++++++++++++++++
  const deptDataList = ref([]);
  const psnDataList = ref([]);
  const cusDataList = ref([]);
  const supDataList = ref([]);
  const proDataList = ref([]);
  // ++++++++++++++++++++++++++++END+++++++++++++++++++++++++++++

  const [register, { closeModal, setModalProps }] = useModalInner((data) => {
    // 方式2
    formItems.value.openOne = data.data.openOne;
    changeBeforeModel = JSON.parse(JSON.stringify(formItems.value));
    setModalProps({ minHeight: 400 });
  });
  /******************* 弹框加载中 **************************/
  import { Loading } from '/@/components/Loading';
  import { findByAccIdAndIyear } from '/@/api/record/system/sys_data_auth_swith';
  import { useCompanyOperateStoreWidthOut } from '/@/store/modules/operate-company';
  import { getDeptListById } from '/@/api/record/system/dept';
  import { getPsnList } from '/@/api/record/system/psn';
  import { findAll, findAllOrderByCustCode } from '/@/api/record/costomer_data/customer';
  // import { findAllOrderBySupCode } from '/@/api/record/supplier_data/supplier';
  function findAllOrderBySupCode(){

  }
  import { findAllProject } from '/@/api/record/system/project';
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
    if (val === '') {
      strDate.value = year + '-01';
      endDate.value = year + '-' + aaa;
      styleValue.value = '全部';
      minKm.value = '';
      minDept.value = '';
      maxDept.value = '';
      bz.value = '';
      fontSize.value = 'MIN';
      ishaveRjz.value = true;
    } else if (val === '1') {
      getQueryPlan();
    }
  }

  let isChanged = false;
  async function handleOk() {
    if (strDate.value.length <= 0 && endDate.value.length <= 0) {
      message.error('请选择会计期间!');
      return;
    }
    if (kmList.value.length == 0) {
      message.error('没有科目不能进行查询!');
      return;
    }
    if (
      !deptCheck.value &&
      !psnCheck.value &&
      !cusCheck.value &&
      !supCheck.value &&
      !proCheck.value
    ) {
      message.error('至少选择一种辅助项!');
      return;
    }

    let temp: any = [];
    if (deptCheck.value) {
      temp.push('部门');
    }
    if (psnCheck.value) {
      temp.push('个人');
    }
    if (cusCheck.value) {
      temp.push('客户');
    }
    if (supCheck.value) {
      temp.push('供应商');
    }
    if (proCheck.value) {
      temp.push('项目');
    }

    let temp2: any = []; //临时存放符合勾选辅助项的科目
    let fuzhuCode = kmList.value.filter((v) => v.fuzhu !== '');
    fuzhuCode.forEach((item) => {
      // 辅助项个数必须大于等于 所选辅助项个数
      if (item.fuzhu.split(',').length >= temp.length) {
        let b = temp;
        let aa = item.fuzhu.split(',').filter((t) => b.indexOf(t) > -1);
        if (aa.length == b.length) {
          temp2.push(item);
        }
      }
    });
    if (temp2.length == 0) {
      minKm.value = '';
      finCodeAll();
      message.error('没有符合辅助项的科目!');
      return;
    } else {
      kmList.value = temp2;
    }
    formItems.value.strDate = strDate.value;
    formItems.value.endDate = endDate.value;
    formItems.value.showStyle = 'J';
    formItems.value.pzType = pzType.value;
    formItems.value.ishaveRjz = ishaveRjz.value;
    formItems.value.styleValue = styleValue.value;
    formItems.value.pageParameter = pageParameter;
    formItems.value.fuzhuCheck = temp2;
    formItems.value.fuzhu = temp;
    let map = {
      dept: {
        flag: deptCheck.value,
        data: deptCheckList.value,
      },
      psn: {
        flag: psnCheck.value,
        data: psnCheckList.value,
      },
      cus: {
        flag: cusCheck.value,
        data: cusCheckList.value,
      },
      sup: {
        flag: supCheck.value,
        data: supCheckList.value,
      },
      pro: {
        flag: proCheck.value,
        data: proCheckList.value,
      },
      fuzhu: temp,
    };
    formItems.value.map = map;
    formItems.value.km =
      minKm.value === undefined || minKm.value === '' ? kmList.value[0].ccode : minKm.value;
    formItems.value.kmList = kmList.value;
    // const queryPlanEntity = {
    //   accountId: database.value,
    //   owningMenuName: '科目辅助余额表',
    //   owningPlan: currentTabsKey.value,
    //   planPerson: userStore.getUserInfo.id,
    //   queryConditions: {
    //     strDate: formItems.value.strDate,
    //     endDate: formItems.value.endDate,
    //     styleValue: styleValue.value,
    //     minKm: formItems.value.km,
    //     fontSize: fontSize.value,
    //     ishaveRjz: ishaveRjz.value,
    //     kmList: kmList.value,
    //     fuzhumap: map,
    //   },
    // };
    // await saveQueryPlan(queryPlanEntity);
    emit('save', unref(formItems));
    closeModal();
    return true;
  }

  async function styleChange(val) {
    minKm.value = '';
    formItems.value.minKm = '';
    formItems.value.maxKm = '';
    minkmList.value = [];

    const start = strDate.value;
    // 重新获取科目类型
    // 获取对应的账套科目 所属的 会计准则、科目模板
    let codelistall = await useRouteApi(groupStandardAndTemplate, { schemaName: database.value })({
      databaseNum: database.value,
      iyear: strDate.value.split('-')[0],
    });
    const accstandard = await findByStandardUnique(codelistall[0].uniqueAccStandard);
    // 重新获取科目类型
    let accstyle = await findByAccStyle(accstandard.accStyleUnique);
    if (val == '全部') {
      let list = await newfindAllCode(database.value, start);
      minkmList.value = list;
    }
    if (val !== '全部') {
      if (val !== '财务会计' && val !== '预算会计') {
        let list = await newfindAllCode(database.value, start);
        minkmList.value = list.filter((ck) => ck.cclass === val);
      } else if (val == '财务会计') {
        for (let i = 0; i < accstyle.length; i++) {
          if (accstyle[i].flagYusuan !== '1') {
            let list = await newfindAllCode(database.value, start);
            for (let j = 0; j < list.length; j++) {
              minkmList.value.push({
                ccode: list[j].ccode,
                value: list[j].ccode + '-' + list[j].ccodeName,
              });
            }
          }
        }
      } else if (val == '预算会计') {
        for (let i = 0; i < accstyle.length; i++) {
          if (accstyle[i].flagYusuan == '1') {
            let list = await newfindAllCode(database.value, start);
            for (let j = 0; j < list.length; j++) {
              minkmList.value.push({
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
    let list = await useRouteApi(findCodeKmByPeriod, { schemaName: s })({
      strDate: start,
      endDate: 'test',
      accId: accId.value,
      userId: userStore.getUserInfo.id,
    });
    return list.filter((ck) => ck.bcus === '1' && ck.bend === '1');
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
    if (maxDept.value) {
      deptList.value = deptList.value.filter((o) => o.uniqueCode >= minDept.value);
    } else {
      deptList.value = deptList.value;
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
      kmList.value = await useRouteApi(findCodeKmByPeriod, { schemaName: database.value })({
        strDate: strDate.value,
        endDate: 'test',
        accId: accId.value,
        userId: userStore.getUserInfo.id,
      });
    }
  }
  async function handleChangeEndDate() {
    // 开始日期变动  如果结束日期小于开始日期 则重置开始日期
    if (strDate.value) {
      if (strDate.value > endDate.value) {
        strDate.value = '';
      }
      // 会计科目
      let list = await useRouteApi(findCodeKmByPeriod, { schemaName: database.value })({
        strDate: endDate.value,
        endDate: 'test',
        accId: accId.value,
        userId: userStore.getUserInfo.id,
      });
      let list2 = list.filter(function (x) {
        return x.bcus === '1' && x.bend === '1';
      });
      kmList.value = list2;
    }
    if (authSwithIsCcode.value === '0') {
      if (kmList.value.length === 0) {
        minKm.value = '未发现会计科目';
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
  const getAdObjInfoByCoCode = (value, type, accList) => {
    let list = accList.filter((item) => item[type] == value);
    return list.length > 0 ? list[0] : null;
  };
  async function findByDateBase() {
    // 获取所有账套信息 并过滤已授权的账套
    let accList = await getAdInfoDatas();
    // 获取当前用户的权限根据用户信息汇率
    let test1 = await getAllAccAuths({ userId: userStore.getUserInfo.id });
    let test2 = filterAccListByAuth(accList, test1);
    let codes = getAdObjInfoByCoCode(accId.value, 'accId', test2);
    if (codes != null) {
      pageParameter.companyCode = codes.coCode;
      pageParameter.companyName = codes.accNameCn;
    }
  }
  // 期间
  async function findAllPeriod() {
    dateList.value = await findPeriodByAccontId(accId.value);
    // 如果没有当前年度 获取 上年第一个区间
    const test = dateList.value.filter((o) => o.iyear == year);
    if (test.length == 0) {
      strDate.value = dateList.value[0].value;
      endDate.value = dateList.value[0].value;
    }
  }
  async function AllCondition() {
    //币种
    let currency = await useRouteApi(findBzAll, { schemaName: database })('');
    bzList.value = currency.filter((c) => c.currChName.indexOf('人民币') < 0);
    bzList.value.push({ id: '', currChName: '全部' });
    // 客户
    deptList.value = await useRouteApi(findCusAll, { schemaName: database.value })('');
    compState.loading = false;
  }

  async function findByAccStyleAll() {
    styleList.value = [];
    /******************************** 科目类型 *********************************/
    // 获取对应的账套科目 所属的 会计准则、科目模板
    let codelistall = await useRouteApi(groupStandardAndTemplate, { schemaName: database.value })({
      databaseNum: database.value,
      iyear: strDate.value.split('-')[0],
    });
    const accstandard = await findByStandardUnique(codelistall[0].uniqueAccStandard);
    // 重新获取科目类型
    let accStyleUnique = await findByAccStyle(accstandard.accStyleUnique);
    styleList.value.push('全部');
    for (let i = 0; i < accStyleUnique.length; i++) {
      styleList.value.push(accStyleUnique[i].cclass);
    }
    // 获取账套信息，判断是否预算会计
    const datainfo = await findByAccId(accId.value);
    if (datainfo.ibudgetAccounting === '1') {
      // 是
      styleList.value.push('财务会计');
      styleList.value.push('预算会计');
    }
    /******************************** 科目类型 **********g***********************/
  }
  let count = 0;
  async function dynamicAdReload(data) {
    ++count;
    if (count == 2) {
      //
      return false;
    }
    openCompFullLoading();
    await findDataBase(data.accId, +data.year).then((item) => {
      console.log(item);
      accId.value = item.accountId;
      database.value = item.accountMode;
      accYear.value = item.accountYear;
      pageParameter.accId = item.accountId;
      pageParameter.database = item.accountMode;

      kmList.value = [];
      styleList.value = [];
      getQueryPlan();
      findByPzTypeList();
      // 期间
      findAllPeriod();
      // 类型
      findByAccStyleAll();
      finCodeAll();
      AllCondition();
    });
  }

  // 查询公司所有科目
  async function finCodeAll() {
    let map = {
      page: '1',
      size: '20000',
      cclass: styleValue.value,
      databasenum: database.value,
      searchConditon: {
        requirement: '',
        value: '',
      },
    };
    // 会计科目
    let temp = await useRouteApi(companyfindAll, { schemaName: database.value })(map);
    kmList.value = temp.items;
  }
  // 获取凭证类型权限
  // 先判断是否主管：是主管 查询全部凭证类别【凭证类别表】
  //               不是主管 判断 是否有 查询全部凭证类别权限
  //                                  是 查询全部【凭证类别表】
  //                                  不是 查询 【凭证类别权限授权表】
  async function findByPzTypeList() {
    let newPzTypeList: any = [];
    // 凭证类型权限授权表
    let pztypeAuth = await useRouteApi(company_findByAccTypeAuth, { schemaName: database })({
      userId: userStore.getUserInfo.id,
      iyear: accYear.value,
    });
    // 凭证类别表
    await useRouteApi(currentAccountTypes, { schemaName: database })('').then((pztypeAll) => {
      pztypeAll.items.forEach((a) => {
        newPzTypeList.push(a.voucherTypeCode);
      });
    });

    // 数据权限表
    let authSwith = await useRouteApi(findByAccIdAndIyear, { schemaName: database })({
      accId: accId.value,
      iyear: accYear.value,
    });
    // 没有开启 查询全部权限
    if (authSwith !== '不存在' && authSwith.isVoucherType == '0') {
      authSwithIsCcode.value = authSwith.isCcode;

      let test1 = await getAllAccAuths({ userId: userStore.getUserInfo.id });
      const list = test1.filter(
        (v) => v.iyear === strDate.value.split('-')[0] && v.accId === database.value,
      );
      if (list.length > 0) {
        // 1是0否有主管权限
        if (list[0].supervisor == '0') {
          // 1是0否有查询所有凭证类型权限
          if (list[0].accvocherType == '0') {
            if (pztypeAuth.length == 0) {
              pzType.value = '未发现凭证类型授权';
              return false;
            }
            newPzTypeList = [];
            pztypeAuth.forEach((a) => {
              newPzTypeList.push(a.accvocherType);
            });
          }
        }
      }
      if (list.length == 0) {
        pzType.value = '未发现凭证类型授权';
        return false;
      }
    } else {
      let test1 = await getAllAccAuths({ userId: userStore.getUserInfo.id });
      const list = test1.filter(
        (v) => v.iyear === strDate.value.split('-')[0] && v.accId === accId.value,
      );
      if (list.length > 0) {
        // 1是0否有主管权限
        if (list[0].supervisor == '0') {
          // 1是0否有查询所有凭证类型权限
          if (list[0].accvocherType == '0') {
            if (pztypeAuth.length == 0) {
              pzType.value = '未发现凭证类型授权';
              return false;
            }
            newPzTypeList = [];
            pztypeAuth.forEach((a) => {
              newPzTypeList.push(a.accvocherType);
            });
          }
        }
      }
      if (list.length == 0) {
        pzType.value = '未发现凭证类型授权';
        return false;
      }
    }
    pzTypeList.value = newPzTypeList;
    return true;
  }
  async function getQueryPlan() {
    let queryPlanIngo = await findByQueryPlan(database.value, '科目辅助余额表');
    if (queryPlanIngo != null) {
      currentTabsKey.value = '1';
      queryPlan.value = queryPlanIngo;

      strDate.value = JSON.parse(queryPlanIngo.queryConditions).strDate;
      endDate.value = JSON.parse(queryPlanIngo.queryConditions).endDate;
      styleValue.value = JSON.parse(queryPlanIngo.queryConditions).styleValue;
      minKm.value = JSON.parse(queryPlanIngo.queryConditions).minKm;
      fontSize.value = JSON.parse(queryPlanIngo.queryConditions).fontSize;
      ishaveRjz.value = JSON.parse(queryPlanIngo.queryConditions).ishaveRjz;

      deptCheck.value = JSON.parse(queryPlanIngo.queryConditions).fuzhumap.dept.flag;
      psnCheck.value = JSON.parse(queryPlanIngo.queryConditions).fuzhumap.psn.flag;
      cusCheck.value = JSON.parse(queryPlanIngo.queryConditions).fuzhumap.cus.flag;
      supCheck.value = JSON.parse(queryPlanIngo.queryConditions).fuzhumap.sup.flag;
      proCheck.value = JSON.parse(queryPlanIngo.queryConditions).fuzhumap.pro.flag;

      deptCheckList.value = JSON.parse(queryPlanIngo.queryConditions).fuzhumap.dept.data;
      psnCheckList.value = JSON.parse(queryPlanIngo.queryConditions).fuzhumap.psn.data;
      cusCheckList.value = JSON.parse(queryPlanIngo.queryConditions).fuzhumap.cus.data;
      supCheckList.value = JSON.parse(queryPlanIngo.queryConditions).fuzhumap.sup.data;
      proCheckList.value = JSON.parse(queryPlanIngo.queryConditions).fuzhumap.pro.data;
    }
  }
  async function timeReload(data) {
    strDate.value = data.strDate;
    endDate.value = data.endDate;
  }
  //js切割字符串
  function setString(str, len) {
    var strlen = 0;
    var s = '';
    for (var i = 0; i < str.length; i++) {
      if (str.charCodeAt(i) > 128) {
        strlen += 2;
      } else {
        strlen += 1.2;
      }
      s += str.charAt(i);
      if (strlen >= len) {
        return s + '...';
      }
    }
    return s;
  }
  // +++++++++++++++++++++++ 辅助项触发事件 +++++++++++++++++++++++
  async function deptCheckFun(a) {
    deptCheck.value = a.target.checked;
    deptCheckDisabled.value = !a.target.checked;
    deptDataList.value = [];
    if (a.target.checked) {
      const res: any = await useRouteApi(getDeptListById, { schemaName: database })({});
      deptDataList.value = res.items;
    } else {
      deptCheckList.value = [];
    }
  }
  async function psnCheckFun(a) {
    psnCheck.value = a.target.checked;
    psnCheckDisabled.value = !a.target.checked;
    psnDataList.value = [];
    if (a.target.checked) {
      const res: any = await useRouteApi(getPsnList, { schemaName: database })({});
      psnDataList.value = res;
    } else {
      psnCheckList.value = [];
    }
  }
  async function cusCheckFun(a) {
    cusCheck.value = a.target.checked;
    cusCheckDisabled.value = !a.target.checked;
    cusDataList.value = [];
    if (a.target.checked) {
      const res: any = await useRouteApi(findAllOrderByCustCode, { schemaName: database })({});
      cusDataList.value = res;
    } else {
      cusCheckList.value = [];
    }
  }
  async function supCheckFun(a) {
    supCheck.value = a.target.checked;
    supCheckDisabled.value = !a.target.checked;
    supDataList.value = [];
    if (a.target.checked) {
      const res: any = await useRouteApi(findAllOrderBySupCode, { schemaName: database })({});
      supDataList.value = res;
    } else {
      supCheckList.value = [];
    }
  }
  async function proCheckFun(a) {
    proCheck.value = a.target.checked;
    proCheckDisabled.value = !a.target.checked;
    proDataList.value = [];
    if (a.target.checked) {
      const res: any = await useRouteApi(findAllProject, { schemaName: database })({});
      proDataList.value = res;
    } else {
      proCheckList.value = [];
    }
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
        padding: 5px 10px;
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

          > span:nth-of-type(1),
          .right_span {
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

    :deep(.ant-select-selector),
    :deep(.ant-calendar-picker-input) {
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
