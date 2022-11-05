<template>
  <BasicModal
    destroyOnClose
    width="800px"
    v-bind="$attrs"
    title="科目多栏账查询"
    @ok="handleOk()"
    :loading="loading"
    @cancel="handleClose()"
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
                <TimeTool @reloadTable="timeReload" />

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
                      @change="handleChangeStrDate"
                    >
                    <a-select-option
                      v-for="item in dateList"
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
                <a-select style="width: 110px;text-align: center;" v-model:value="styleValue" @change="styleChange">
                  <a-select-option v-for="d in styleList" :value="d.cclass">
                    {{ d.cclass }}
                  </a-select-option>
                </a-select>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <span>凭证类别：</span>
                <a-select mode="tags" v-model:value="pzType" style="width: 174px;" :class="pzType=='未发现凭证类型授权'?'selectColorRed':'selectColorBlack'">
                  <a-select-option v-for="d in pzTypeList" :value="d" >
                    {{ d }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>科&emsp;&emsp;目：</span>
                <a-select
                  v-model:value="minKm"
                  show-search
                  style="width: 180px;text-align: center;"
                  allowClear="true"
                  :filter-option="filterOption"
                  @change="handleChangeMinKm"
                >
                  <a-select-option v-for="d in kmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>币&emsp;&emsp;种：</span>
                <a-select style="width: 120px" v-model:value="bz">
                  <a-select-option  v-for="d in bzList" :value="d.id">
                    {{ d.currChName }}
                  </a-select-option>
                </a-select>
                <span>&emsp;字&emsp;&emsp;号：</span>
                <a-select  style="width: 170px" v-model:value="fontSize">
                  <a-select-option value="MAX">
                    大号字体
                  </a-select-option>
                  <a-select-option value="MIN">
                    小号字体
                  </a-select-option>
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
                </a-select>
                <span style="margin-left: 1em">年度：</span>
                <CustomizeYear :value="naturalYear" @CustomizeChange="customizeChange"
                               readonly="false"/>
              </li>
              <li>
                <span>公司代码：</span>
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
                </a-select>
              </li>
              <li>
                <span><font style="color: red">*</font>期&emsp;&emsp;间：</span>
                <a-select
                  v-model:value="strDate"
                  style="width: 180px;text-align: center;"
                  show-search
                  option-filter-prop="children"
                  @change="handleChangeStrDate"
                >
                  <a-select-option
                    v-for="item in dateList"
                    :key="item.id"
                    :value="item.value"
                  >
                    {{ item.value }}
                  </a-select-option>
                </a-select>
                <span>&emsp;~&emsp;</span>
                <a-select
                  v-model:value="endDate"
                  style="width: 180px;text-align: center;"
                  show-search
                  option-filter-prop="children"
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
              </li>
              <li>
                <span>科目类型：</span>
                <a-select style="width: 110px;text-align: center;" v-model:value="styleValue" @change="styleChange">
                  <a-select-option v-for="d in styleList" :value="d.cclass">
                    {{ d.cclass }}
                  </a-select-option>
                </a-select>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <span>凭证类别：</span>
                <a-select mode="tags" v-model:value="pzType" style="width: 174px;" :class="pzType=='未发现凭证类型授权'?'selectColorRed':'selectColorBlack'">
                  <a-select-option v-for="d in pzTypeList" :value="d">
                    {{ d }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>科&emsp;&emsp;目：</span>
                <a-select
                  v-model:value="minKm"
                  show-search
                  style="width: 180px;text-align: center;"
                  allowClear="true"
                  :filter-option="filterOption"
                  @change="handleChangeMinKm"
                >
                  <a-select-option v-for="d in kmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>
                <span>&emsp;~&emsp;</span>
                <a-select
                  v-model:value="maxKm"
                  show-search
                  style="width: 180px;text-align: center;"
                  allowClear="true"
                  :filter-option="filterOption"
                  @focus="handleFocusMaxKm"
                >
                  <a-select-option v-for="d in maxkmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <a-radio-group  style="width: 100%" v-model:value="jc">
                  <a-radio :value="1" style="margin-left: -7%;">
                    <span>级次</span>
                  </a-radio>
                  <a-select
                    default-value="1"
                    v-model:value="minJc"
                    @change="handleChangeMinCj"
                    style="width: 80px; text-align: center;margin-left: 7%;">
                    <a-select-option v-for="d in jcList"  :value="d.value">
                      {{ d.label }}
                    </a-select-option>
                  </a-select>
                  <span>&emsp;~&emsp;</span>
                  <a-select
                    default-value="1"
                    v-model:value="maxJc"
                    @focus="handleFocusMaxCj"
                    style="width: 80px; text-align: center">
                    <a-select-option v-for="d in maxJcList"  :value="d.value">
                      {{ d.label }}
                    </a-select-option>
                  </a-select>

                  <a-radio :value="2" style="margin-left: 10%;">
                    <span>末级科目</span>
                  </a-radio>
                </a-radio-group>
              </li>
              <li>
                <span>币&emsp;&emsp;种：</span>
                <a-select style="width: 120px" v-model:value="bz">
                  <a-select-option  v-for="d in bzList" :value="d.id">
                    {{ d.currChName }}
                  </a-select-option>
                </a-select>
                <span>&emsp;字&emsp;&emsp;号：</span>
                <a-select  style="width: 170px" v-model:value="fontSize">
                  <a-select-option value="MAX">
                    大号字体
                  </a-select-option>
                  <a-select-option value="MIN">
                    小号字体
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <a-checkbox v-model:checked="ishaveRjz" style="width: 130px"> 包含未记账 </a-checkbox>
              </li>
            </ul>
          </a-tab-pane>
          <a-tab-pane v-if="queryPlan!=null" key="1" tab="个人标准模式">
            <ul>
              <li>
                <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
              </li>
              <li>
                <TimeTool @reloadTable="timeReload" />

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
                      @change="handleChangeStrDate"
                    >
                    <a-select-option
                      v-for="item in dateList"
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
                <a-select style="width: 110px;text-align: center;" v-model:value="styleValue" @change="styleChange">
                  <a-select-option v-for="d in styleList" :value="d.cclass">
                    {{ d.cclass }}
                  </a-select-option>
                </a-select>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <span>凭证类别：</span>
                <a-select mode="tags" v-model:value="pzType" style="width: 174px;" :class="pzType=='未发现凭证类型授权'?'selectColorRed':'selectColorBlack'">
                  <a-select-option v-for="d in pzTypeList" :value="d" >
                    {{ d }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>科&emsp;&emsp;目：</span>
                <a-select
                  v-model:value="minKm"
                  show-search
                  style="width: 180px;text-align: center;"
                  allowClear="true"
                  :filter-option="filterOption"
                  @change="handleChangeMinKm"
                >
                  <a-select-option v-for="d in kmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>币&emsp;&emsp;种：</span>
                <a-select style="width: 120px" v-model:value="bz">
                  <a-select-option  v-for="d in bzList" :value="d.id">
                    {{ d.currChName }}
                  </a-select-option>
                </a-select>
                <span>&emsp;字&emsp;&emsp;号：</span>
                <a-select  style="width: 170px" v-model:value="fontSize">
                  <a-select-option value="MAX">
                    大号字体
                  </a-select-option>
                  <a-select-option value="MIN">
                    小号字体
                  </a-select-option>
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
    <template #footer>
      <div style="height: 35px">

        <div style="float: right">
          <a-button @click="showSetting" v-if="!loading" type="primary">多栏设置</a-button>
          <a-button @click="handleClose">取消</a-button>
          <a-button @click="handleOk" v-if="!loading" type="primary">确定</a-button>
        </div>
      </div>
    </template>
    <MultiSeeting
      @save="loadPage"
      @register="registerSeetingPage"
    />
  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {ref, unref, onMounted, reactive} from 'vue'
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import {PageWrapper} from '/@/components/Page'
import {
  DatePicker as ADatePicker,
  Select as ASelect,
  Input as AInput,
  Checkbox as ACheckbox,
  Radio as ARadio,
  Tabs as ATabs, message
} from "ant-design-vue"
import CheckboxGroup from "ant-design-vue/lib/checkbox/Group";
import { useTabs } from '/@/hooks/web/useTabs';
import router from "/@/router";
import moment, { Moment } from 'moment';
import {
  findPeriodByAccontId,
  findBzAll,
  findMaxJc,
  findCodeKmByPeriod, saveMultiSet,loadMultiSet
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
import {useUserStore} from "/@/store/modules/user";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import MultiSeeting from "./seeting.vue";
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
import dayjs from "dayjs";

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

// 期间是否可选
let dateselflg: any = ref(false);
// 日期是否可选
let timeselflg: any = ref(true);
let radiovalue: any = ref(1);
let styleValue: any = ref('全部');
let styleList: any = ref([]);
const queryPlan: any = ref([]);
const activeKey: any = ref('');
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
  getQueryPlan()
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
  //选中科目
  if(minKm.value){
    formItems.value.kmName =kmList.value.filter(o =>  o.ccode === minKm.value)[0].value
  }else{
    message.error("请选择科目！")
    return false
  }

  //查询是否包含设置过该科目
  await useRouteApi(loadMultiSet,{schemaName:databaseTrue})({km:minKm.value})
    .then(v=>{
      if(v.length > 0 ){
        list = v
      }else{
        list = kmList.value
        // 过滤会计科目 去掉自身
        if(minKm.value){
          list = list.filter(o =>  o.ccode.includes(minKm.value) && o.ccode != minKm.value)
        }
      }
    });
  //末级科目
  if(list.length === 0){
    message.error("请选择多栏设置！")
    return false
  }
  formItems.value.kmList = list;
  formItems.value.styleList = styleValue.value!=='全部'?styleList.value.filter(s=>s.cclass===styleValue.value):styleList.value.filter(s=>s.cclass!==styleValue.value);
  formItems.value.pageParameter = pageParameter;
  formItems.value.styleValue = styleValue.value;
  formItems.value.pzType = pzType.value;

  const queryPlanEntity={
    accountId:databaseTrue.value,
    owningMenuName:'科目多栏账',
    owningPlan:activeKey.value,
    planPerson:userStore.getUserInfo.id,
    queryConditions:{
      ishaveRjz:formItems.value.ishaveRjz,
      bend:formItems.value.jc,
      strDate:formItems.value.strDate,
      endDate:formItems.value.endDate,
      minKm:formItems.value.minKm,
      bz:formItems.value.bz,
      fontSize:formItems.value.fontSize,
      styleValue:formItems.value.styleValue,
      pzType:pzType.value
    }
  }
  await saveQueryPlan(queryPlanEntity);
  //添加日志
  ncLogger.info("科目多栏账查询:"+JSON.stringify(queryPlanEntity))
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
};

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
  let list = kmList.value
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
  databaseTrue.value =data.accId+'-'+data.year
  pageParameter.accId=data.accId
  pageParameter.database=data.accId+'-'+data.year
  databaseyear.value=data.year
  pageParameter.accNameAll=data.target.accName
  console.log(pageParameter.accNameAll)
  if(databaseTrue.value != data.accId+'-'+data.year){
    changeOkLoading(true);
    loading.value  = true
    bz.value = '0'
    AllCondition();
  }
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
  bzList.value.push({ id: '0', currChName: '全部'  });


  // 设置默认选中
  const  sel = bzList.value.filter(o=> o.standCurr === '1')
  if(sel.length > 0){
    bz.value = sel[0].id
  }

  //级次
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
  isType.value = list[0].accvocherType
  isCode.value = list[0].ccodeAll

  // 获取凭证类型权限
  findByPzTypeList()
}
async function getQueryPlan() {
  let queryPlanIngo= await findByQueryPlan(databaseTrue.value,'科目多栏账')
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

const [registerSeetingPage, { openModal: openSeetingPage }] = useModal()

const showSetting = async() => {
  //查询是否包含设置过该科目
  await useRouteApi(loadMultiSet,{schemaName:databaseTrue})({km:minKm.value})
    .then(v=>{
      let list = []
      if(v.length > 0 ){
        list = v
      }else{
        list = kmList.value
        // 过滤会计科目
        if(minKm.value){
          list = list.filter(o =>  o.ccode.includes(minKm.value) && o.ccode != minKm.value)
        }else{
          message.error("请选择科目！")
        }
      }
      openSeetingPage(true, {
        data: list,
        accId: databaseTrue,
        km: minKm.value,
      })
    });
}

const loadPage = () => {

}

</script>
<style lang="less" scoped>

.ant-btn-group{
  display: inline-block;
}

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
        margin: 15px 0;

        span {
          font-size: 14px;
          color: #747272;
        }

        > span:nth-of-type(1), .right_span {
          display: inline-block;
          width: 100px;
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
