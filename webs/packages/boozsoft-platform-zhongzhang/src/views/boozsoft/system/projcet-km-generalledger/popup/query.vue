<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="科目部门总账查询"
    @ok="handleOk()"
    @cancel="handleClose()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
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
                <span><font style="color: red">*</font>期&emsp;&emsp;间：</span>
                <a-select
                  v-model:value="strDate"
                  option-filter-prop="children"
                  style="width: 180px;text-align: center;"
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
                  option-filter-prop="children"
                  style="width: 180px;text-align: center;"
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
                <a-select style="width: 180px;text-align: center;" v-model:value="styleValue" @change="styleChange">
                  <a-select-option v-for="d in styleList" :value="d.cclass">
                    {{ d.cclass }}
                  </a-select-option>
                </a-select>
              </li>
              <li><span>科&emsp;&emsp;目：</span>
                <a-select
                  v-model:value="minKm"
                  show-search
                  option-filter-prop="children"
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
                  option-filter-prop="children"
                  style="width: 180px;text-align: center;"
                  allowClear="true"
                  :filter-option="filterOption"
                  @focus="handleFocusMaxKm"
                  @change="handleChangeMaxKm"
                >
                  <a-select-option v-for="d in maxkmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>币&emsp;&emsp;种：</span>
                <a-select style="width: 200px" v-model:value="bz">
                  <a-select-option  v-for="d in bzList" :value="d.id">
                    {{ d.currChName }}
                  </a-select-option>
                </a-select>
                <span>&emsp;字&emsp;号：</span>
                <a-select  style="width: 175px" v-model:value="fontSize">
                  <a-select-option value="MAX">
                    大号字体
                  </a-select-option>
                  <a-select-option value="MIN">
                    小号字体
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>科目性质：</span>
                <a-select  style="width: 185px" v-model:value="kmKj">
                  <a-select-option value="" label="全部" >全部</a-select-option>
                  <a-select-option value="CW" label="财务会计">财务会计</a-select-option>
                  <a-select-option value="YS" label="预算会计">预算会计</a-select-option>
                </a-select>
              </li>
              <li><span>余&emsp;&emsp;额：</span>
                <a-input
                  v-model:value="minYe"
                  style="width: 200px"
                  @blur="handleChangeYe"
                >

                </a-input>
                <span>&emsp;~&emsp;</span>
                <a-input
                  v-model:value="maxYe"
                  style="width: 200px"
                  @blur="handleChangeYe"
                >
                </a-input>
              </li>
              <li><span>余额方向：</span>
                <a-checkbox v-model:checked="jfye" style="width: 200px;text-align: left;">
                  借方余额
                </a-checkbox>
                <a-checkbox v-model:checked="dfye" style="width: 200px;text-align: left;">
                  贷方余额
                </a-checkbox>
              </li>

              <li>
                <span v-if="showProjectClass"><font style="color: red">*</font>项目大类：</span>
                <a-select
                  v-model:value="projectCate"
                  show-search
                  option-filter-prop="children"
                  style="width: 180px;text-align: center;"
                  v-if="showProjectClass"
                  :filter-option="filterOption"
                  @change="handleChangeProjectCate"
                >
                  <a-select-option v-for="d in projectCateList" :value="d.projectCateCode">
                    {{ d.projectCateName }}
                  </a-select-option>
                </a-select>

                <span>项目分类：</span>
                <a-select
                  v-model:value="projectClass"
                  show-search
                  option-filter-prop="children"
                  style="width: 180px;text-align: center;"
                  allowClear="true"
                  :filter-option="filterOption"
                  @change="handleChangeProjectClass"
                >
                  <a-select-option v-for="d in projectClassList" :value="d.uniqueCode">
                    {{ d.projectClassName }}
                  </a-select-option>
                </a-select>
              </li>
              <li><span>项&emsp;&emsp;目：</span>
                <a-select
                  v-model:value="project"
                  mode="multiple"
                  style="width: 430px"
                  :filter-option="filterOption"
                >
                  <a-select-option v-for="d in projectList" :value="d.uniqueCode">
                    {{ d.projectName }}
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
                <span>核算单位：</span>

                <a-select
                  mode="multiple"
                  style="width: 80%"
                  placeholder="可多选"
                  option-label-prop="label"
                >
                  <a-select-option value="湖北万亚" label="湖北万亚">
                    湖北万亚
                  </a-select-option>
                  <a-select-option value="北京希格" label="北京希格">
                    北京希格
                  </a-select-option>
                </a-select>
              </li>
              <li><span>期&emsp;&emsp;间：</span>
                <a-select default-value="2020-01" style="width: 218px;text-align: center" @change="handleChange">
                  <a-select-option value="2020-01">
                    2020-01
                  </a-select-option>
                  <a-select-option value="2020-02">
                    2020-02
                  </a-select-option>
                  <a-select-option value="2020-03" disabled>
                    2020-03
                  </a-select-option>
                  <a-select-option value="2020-04">
                    2020-04
                  </a-select-option>
                </a-select>
                <span>&emsp;~&emsp;</span>
                <a-select default-value="2020-01" style="width: 218px;text-align: center" @change="handleChange">
                  <a-select-option value="2020-01">
                    2020-01
                  </a-select-option>
                  <a-select-option value="2020-02">
                    2020-02
                  </a-select-option>
                  <a-select-option value="2020-03" disabled>
                    2020-03
                  </a-select-option>
                  <a-select-option value="2020-04">
                    2020-04
                  </a-select-option>
                </a-select>
              </li>
              <li><span>科&emsp;&emsp;目：</span>

                <a-select
                  show-search
                  placeholder="最小科目"
                  style="width: 218px"
                  :default-active-first-option="false"
                  :show-arrow="false"
                  :filter-option="false"
                  :not-found-content="null"
                  @change="handleChange"
                >
                  <a-select-option v-for="d in data" :key="d.value">
                    {{ d.text }}
                  </a-select-option>
                </a-select>
                <span>&emsp;~&emsp;</span>
                <a-select
                  show-search
                  placeholder="最大科目"
                  style="width: 218px"
                  :default-active-first-option="false"
                  :show-arrow="false"
                  :filter-option="true"
                  :not-found-content="null"
                  @change="handleChange"
                >
                  <a-select-option v-for="d in data" :key="d.value">
                    {{ d.text }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <a-radio-group  @change="onChange" style="width: 100%">
                  <a-radio :value="1">
                    <span>级次</span>
                    <a-select default-value="1" style="width: 150px;text-align: center" @change="handleChange">
                      <a-select-option value="1">
                        1
                      </a-select-option>
                      <a-select-option value="2">
                        2
                      </a-select-option>
                      <a-select-option value="3">
                        3
                      </a-select-option>
                      <a-select-option value="4">
                        4
                      </a-select-option>
                      <a-select-option value="5">
                        5
                      </a-select-option>
                      <a-select-option value="6">
                        6
                      </a-select-option>
                    </a-select>
                    <span>&emsp;~&emsp;</span>
                    <a-select default-value="1" style="width: 150px;text-align: center" @change="handleChange">
                      <a-select-option value="1">
                        1
                      </a-select-option>
                      <a-select-option value="2">
                        2
                      </a-select-option>
                      <a-select-option value="3">
                        3
                      </a-select-option>
                      <a-select-option value="4">
                        4
                      </a-select-option>
                      <a-select-option value="5">
                        5
                      </a-select-option>
                      <a-select-option value="6">
                        6
                      </a-select-option>
                    </a-select>
                  </a-radio>
                  <a-radio :value="2" style="margin-left: 50%;">
                    <span>末级科目</span>
                  </a-radio>
                </a-radio-group>
              </li>
              <li><span>科目类型：</span>
                <a-select
                  mode="multiple"
                  style="width: 80%"
                  placeholder="可多选"
                  option-label-prop="label"
                >
                  <a-select-option value="" label="全部">
                    全部
                  </a-select-option>
                  <a-select-option value="1" label="损益">
                    损益
                  </a-select-option>
                </a-select>
              </li>
              <li><span>样&emsp;&emsp;式：</span> <a-select default-value="1" style="width: 185px" @change="handleChange">
                <a-select-option value="1">
                  金额式
                </a-select-option>
                <a-select-option value="2">
                  数量金额式
                </a-select-option>
                <a-select-option value="3" >
                  外币金额式
                </a-select-option>
                <a-select-option value="4">
                  数量外币式
                </a-select-option>
              </a-select>
                <span>&emsp;字&emsp;&emsp;号：</span> <a-select default-value="1" style="width: 185px" @change="handleChange">
                  <a-select-option value="1">
                    大号字体
                  </a-select-option>
                  <a-select-option value="2">
                    小号字体
                  </a-select-option>
                </a-select>
              </li>
              <li><span>币&emsp;&emsp;种：</span> <a-select default-value="1" style="width: 200px" @change="handleChange">
                <a-select-option value="1">
                  人民币
                </a-select-option>
                <a-select-option value="2">
                  美元
                </a-select-option>
                <a-select-option value="3" disabled>
                  欧元
                </a-select-option>
                <a-select-option value="4">
                  港元
                </a-select-option>
              </a-select></li>
            </ul>
            <a-checkbox-group @change="onChange">
              <a-checkbox value="A" style="width: 200px">
                包含未记账
              </a-checkbox>
              <a-checkbox value="B" style="width: 200px">
                期间无发生显示累计
              </a-checkbox>
            </a-checkbox-group>
          </a-tab-pane>
          <a-tab-pane v-if="queryPlan!=null" key="1" tab="个人标准模式">
            <ul>
              <li>
                <AccountPicker theme="one" @reloadTable="dynamicAdReload"/>
              </li>
              <li>
                <span><font style="color: red">*</font>期&emsp;&emsp;间：</span>
                <a-select
                  v-model:value="strDate"
                  option-filter-prop="children"
                  style="width: 180px;text-align: center;"
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
                  option-filter-prop="children"
                  style="width: 180px;text-align: center;"
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
                <a-select style="width: 180px;text-align: center;" v-model:value="styleValue" @change="styleChange">
                  <a-select-option v-for="d in styleList" :value="d.cclass">
                    {{ d.cclass }}
                  </a-select-option>
                </a-select>
              </li>
              <li><span>科&emsp;&emsp;目：</span>
                <a-select
                  v-model:value="minKm"
                  show-search
                  option-filter-prop="children"
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
                  option-filter-prop="children"
                  style="width: 180px;text-align: center;"
                  allowClear="true"
                  :filter-option="filterOption"
                  @focus="handleFocusMaxKm"
                  @change="handleChangeMaxKm"
                >
                  <a-select-option v-for="d in maxkmList" :value="d.ccode">
                    {{ d.value }}
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>币&emsp;&emsp;种：</span>
                <a-select style="width: 200px" v-model:value="bz">
                  <a-select-option  v-for="d in bzList" :value="d.id">
                    {{ d.currChName }}
                  </a-select-option>
                </a-select>
                <span>&emsp;字&emsp;号：</span>
                <a-select  style="width: 175px" v-model:value="fontSize">
                  <a-select-option value="MAX">
                    大号字体
                  </a-select-option>
                  <a-select-option value="MIN">
                    小号字体
                  </a-select-option>
                </a-select>
              </li>
              <li>
                <span>科目性质：</span>
                <a-select  style="width: 185px" v-model:value="kmKj">
                  <a-select-option value="" label="全部" >全部</a-select-option>
                  <a-select-option value="CW" label="财务会计">财务会计</a-select-option>
                  <a-select-option value="YS" label="预算会计">预算会计</a-select-option>
                </a-select>
              </li>
              <li><span>余&emsp;&emsp;额：</span>
                <a-input
                  v-model:value="minYe"
                  style="width: 200px"
                  @blur="handleChangeYe"
                >

                </a-input>
                <span>&emsp;~&emsp;</span>
                <a-input
                  v-model:value="maxYe"
                  style="width: 200px"
                  @blur="handleChangeYe"
                >
                </a-input>
              </li>
              <li><span>余额方向：</span>
                <a-checkbox v-model:checked="jfye" style="width: 200px;text-align: left;">
                  借方余额
                </a-checkbox>
                <a-checkbox v-model:checked="dfye" style="width: 200px;text-align: left;">
                  贷方余额
                </a-checkbox>
              </li>

              <li>
                <span v-if="showProjectClass"><font style="color: red">*</font>项目大类：</span>
                <a-select
                  v-model:value="projectCate"
                  show-search
                  option-filter-prop="children"
                  style="width: 180px;text-align: center;"
                  v-if="showProjectClass"
                  :filter-option="filterOption"
                  @change="handleChangeProjectCate"
                >
                  <a-select-option v-for="d in projectCateList" :value="d.projectCateCode">
                    {{ d.projectCateName }}
                  </a-select-option>
                </a-select>

                <span>项目分类：</span>
                <a-select
                  v-model:value="projectClass"
                  show-search
                  option-filter-prop="children"
                  style="width: 180px;text-align: center;"
                  allowClear="true"
                  :filter-option="filterOption"
                  @change="handleChangeProjectClass"
                >
                  <a-select-option v-for="d in projectClassList" :value="d.uniqueCode">
                    {{ d.projectClassName }}
                  </a-select-option>
                </a-select>
              </li>
              <li><span>项&emsp;&emsp;目：</span>
                <a-select
                  v-model:value="project"
                  mode="multiple"
                  style="width: 430px"
                  :filter-option="filterOption"
                >
                  <a-select-option v-for="d in projectList" :value="d.uniqueCode">
                    {{ d.projectName }}
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
  Popover as APopover,
  Switch as ASwitch,
  Radio as ARadio,
  Upload as AUpload,
  Table as ATable,
  Tabs as ATabs, message
} from "ant-design-vue"
import CheckboxGroup from "ant-design-vue/lib/checkbox/Group";
import { useTabs } from '/@/hooks/web/useTabs';
import router from "/@/router";
import { Moment } from 'moment';
import {
  findPeriodByAccontId,
  findBzAll,
  findMaxJc,
  findDeptAll,
  findCodeKmDept, findProjectCategory, findProjectClass, findProject
} from '/@/api/record/generalLedger/data';
import {findByImgid} from "/@/api/record/eletronicInvoice_data/eletronic_invoice";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {finByParameterAccuracy} from "/@/api/record/km_mingxi/data";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {filterAccListByAuth, getAdInfoDatas, getAllAccAuths} from "/@/api/record/system/financial-settings";
import {useUserStore} from "/@/store/modules/user";
import {company_findAllStyleByUnique, findByAccStyle, findByStandardUnique} from "/@/api/accstandard/accstandard";
import {findByQueryPlan, saveQueryPlan} from "/@/api/query-plan/queryPlan";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {groupStandardAndTemplate} from "/@/api/codekemu/codekemu";
import {findByAccId} from "/@/api/record/system/account";
import {useNcLogger} from "/@/utils/boozsoft/record/recordUtils";

const userStore = useUserStore();
const { closeCurrent } = useTabs(router);
const ARangePicker = ADatePicker.RangePicker
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const ACheckboxGroup = ACheckbox.Group
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
//借贷方余额
const jfye: any = ref<boolean>(false)
const dfye: any = ref<boolean>(false)
//借贷方余额
const minYe: any = ref<number>()
const maxYe: any = ref<number>()
//部门
const deptList: any = ref([])

const minDept = ref<string>()
const maxDept = ref<string>()

const endDate = ref<String>("")
const strDate = ref<String>("")
let endDateList: any = ref([])
let strDateList: any = ref([])

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
const bz = ref()
const jd = ref<number>(2)
const ishaveRjz = ref<boolean>(true)
const isShowQjlh = ref<boolean>(false)

const kmKj = ref<string>('')

const accType:any = ref([])
const isType = ref<string>('')
const isCode = ref<string>('')
const userId = ref<string>('')
let styleValue: any = ref('全部');
let styleList: any = ref([]);
//查询条件
const seach : any = ref({})
const year = new Date().getFullYear();

//项目大类
let projectCateList: any = ref([])
//项目分类
let projectClassList: any = ref([])
//项目
let projectList: any = ref([])
const projectCate = ref<string>('')
const projectClass = ref<string>('')
const project = ref([])

const [register, {closeModal, setModalProps,changeOkLoading}] = useModalInner((data) => {
  // 方式2
  formItems.value.openOne = data.data.openOne
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
  getQueryPlan()
  setModalProps({minHeight: 400});
})
let isChanged: boolean = false
//获取当前年月日
const nowDate = ()=>{
  const nowDate = new Date();
  const date = {
    year: nowDate.getFullYear(),
    month: nowDate.getMonth() + 1,
    date: nowDate.getDate(),
  }
  const newmonth = date.month>10?date.month:'0'+date.month
  const day = date.date>10?date.date:'0'+date.date
  return date.year + '-' + newmonth
}

const recordName = 'project-km-generalledger'
const {ncLogger} = useNcLogger({recordName})

async function handleOk() {
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

  formItems.value.jfye = jfye.value
  formItems.value.dfye = dfye.value
  formItems.value.minYe = minYe.value
  formItems.value.maxYe = maxYe.value

  //科目 类别权限
  formItems.value.accType = accType.value
  formItems.value.isType = isType.value
  formItems.value.isCode = isCode.value
  formItems.value.userId = userId.value

  formItems.value.projectCate = projectCate.value
  formItems.value.projectClass = projectClass.value
  formItems.value.project = project.value
  formItems.value.projectName = projectCateList.value.filter(v=> v.projectCateCode === projectCate.value)[0].projectCateName



  // 过滤会计科目
  let list = kmList.value
  if(maxKm.value){
    list = list.filter(o =>  o.ccode <= maxKm.value)
  }
  if(minKm.value){
    list = list.filter(o =>  o.ccode >= minKm.value)
  }

  formItems.value.kmList = list;
  formItems.value.pageParameter = pageParameter;
  const queryPlanEntity={
    accountId:database.value,
    owningMenuName:'科目项目表',
    owningPlan:activeKey.value,
    planPerson:userStore.getUserInfo.id,
    queryConditions:{
      ishaveRjz:formItems.value.ishaveRjz,
      bend:formItems.value.jc,
      strDate:formItems.value.strDate,
      endDate:formItems.value.endDate,
      minKm:formItems.value.minKm,
      maxKm:formItems.value.maxKm,
      projectCate: formItems.value.projectCate,
      projectClass: formItems.value.projectClass,
      project: formItems.value.project,
      projectName: formItems.value.projectName,
      bz:formItems.value.bz,
      fontSize:formItems.value.fontSize,
      styleValue:formItems.value.styleValue
    }
  }
  await saveQueryPlan(queryPlanEntity);
  //添加日志
  ncLogger.info("总账查询:"+JSON.stringify(queryPlanEntity))
  emit('save', unref(formItems))
  closeModal()
  return true
}
async function handleClose() {
  if (null != formItems.value.openOne && formItems.value.openOne == 1){
    await closeCurrent()
    router.push('/home/welcome')
  }
}
async function handleChangeYe() {
  //余额大小控制
  if(minYe.value > maxYe.value){
    maxYe.value = '';
  }
}
// 数据库模式名称
const databases = ref(getCurrentAccountName(false))
async function handleChangeStrDate() {
  // 开始日期变动  如果开始日期大于结束于日期 则重置结束日期
  if(endDate.value){
    if(strDate.value > endDate.value){
      endDate.value = strDate.value
    }
    userId.value = userStore.getUserInfo.id
    // 会计科目
    const s = databases.value+'-'+ strDate.value.split('-')[0];
    kmList.value = await useRouteApi(findCodeKmDept,{schemaName:s})({
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
    userId.value = userStore.getUserInfo.id
    // 会计科目
    const s = databases.value+'-'+ strDate.value.split('-')[0];
    kmList.value = await useRouteApi(findCodeKmDept,{schemaName:s})({
      strDate: strDate.value,
      endDate: endDate.value,
      accId: databases.value,
      userId: userId.value,
    });

  }
}
async function focusStrDate() {
  if(endDate.value){
    strDateList.value = dateList.value.filter( o => o.value <= endDate.value)
  }else{
    strDateList.value = dateList.value
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
    maxkmList.value = kmList.value
  }
};
const handleChangeMinDept = () => {
  if(maxDept.value && maxDept.value < minDept.value){
    maxDept.value = minDept.value
  }
};
const handleFocusMaxDept = () => {
  //获取焦点时 如果最小科目已选择 判断过滤小于最小部门的数据
  if(maxDept.value){
    deptList.value = deptList.value.filter( o => o.uniqueCode >= minDept.value)
  }else{
    deptList.value = deptList.value
  }
};
const filterOption = (input: string, option: any) => {
  return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};
const handleFocusMinCj = () => {
  //获取焦点时 如果最大科目已选择 则过滤大于最大科目的数据
  if(maxJc.value){
    minJcList.value = jcList.value.filter( o => o.value <= maxJc.value)
  }else{
    minJcList.value = jcList.value
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
const whetherGroup = ref(false)
// 数据库模式名称
const database = ref(getCurrentAccountName(false));
onMounted(async () => {
  AllCondition()
});

async function styleChange(val) {
  const s = databases.value+'-'+ strDate.value.split('-')[0];
  // 重新筛选科目类型
  let accstyle =await useRouteApi(company_findAllStyleByUnique,{schemaName:s})('');
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
const queryPlan: any = ref([]);
const activeKey: any = ref('');
// 页面变量
const pageParameter = reactive({
  companyCode: '',
  companyName: '',
})
let loading: any = ref(true)

const databaseTrue = ref(getCurrentAccountName(true));
async function dynamicAdReload(data) {
  database.value=data.accId
  databases.value=data.accId
  pageParameter.accId=data.accId
  pageParameter.database=data.accId+'-'+data.year
  pageParameter.accNameAll=data.target.accName
  if(databaseTrue.value != data.accId+'-'+data.year){
    databaseTrue.value =data.accId+'-'+data.year
    changeOkLoading(true);
    loading.value  = true
    bz.value = '0'
    AllCondition();
    getQueryPlan();
  }
}
const showProjectClass: any = ref(false);

async function AllCondition() {
  findByDateBase()
  //加载数据
  dateList.value =  await findPeriodByAccontId(database.value);
  // 设置默认期间
  // 如果没有当前年度 获取 上年第一个区间
  const test = dateList.value.filter((o) => o.iyear == year);
  if (test.length == 0) {
    strDate.value = dateList.value[0].value;
    endDate.value = dateList.value[0].value;
  }else{
    strDate.value = nowDate();
    endDate.value = nowDate();
  }
  //科目 类别权限
  const ss = await getAllAccAuths({'userId': userStore.getUserInfo.id})
  const list = ss.filter(v => v.iyear === strDate.value.split('-')[0] && v.accId === database.value)
  isType.value = list[0].accvocherType
  isCode.value = list[0].ccodeAll
  userId.value = userStore.getUserInfo.id
  // 会计科目
  kmList.value = await useRouteApi(findCodeKmDept,{schemaName:databaseTrue})({
    strDate: strDate.value,
    endDate: endDate.value,
    accId: databases.value,
    userId: userId.value,
  });

  //kmList 不存在 不让查询
  // if(kmList.value.length === 0){
  //   message.error('您没有科目权限！')
  //   return false
  // }
  minkmList.value = kmList.value


  //项目大类
  projectCateList.value = await useRouteApi(findProjectCategory,{schemaName:databaseTrue})({});
  //项目分类
  //projectClassList.value = await useRouteApi(findProjectClass,{schemaName:databaseTrue})({});

  console.log(projectClassList.value)
  //项目
  //projectList.value = await useRouteApi(findProjectCategory,{schemaName:s});

  //币种
  bzList.value = await useRouteApi(findBzAll,{schemaName:databaseTrue.value})()
  bzList.value.push({ id: '0', currChName: '全部',standCurr:'0' });

  // 设置默认选中
  const  sel = bzList.value.filter(o=> o.standCurr === '1')
  if(sel.length > 0){
    bz.value = sel[0].id
  }

  //计算精度
  const jdlist = await useRouteApi(finByParameterAccuracy,{schemaName:getCurrentAccountName(true)})('')
  const jds = jdlist.filter(o=> o.paraNum === '数量');
  if(jds.length > 0){
    jd.value = jds[0].decimalPlaces;
  }
  //查找
  let da = await findByAccId(database.value);
  if(da){
    if(da.projectClassCtl === '1'){
      showProjectClass.value =  true
    }
  }
  // 是否集团
  let flag = useCompanyOperateStoreWidthOut().getAccountList[0].independent
  whetherGroup.value = (null == flag ? true : '0' == flag ? true : false)

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

  changeOkLoading(false);
  loading.value  = false

}
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
async function getQueryPlan() {
  let queryPlanIngo= await findByQueryPlan(database.value,'科目项目表')
  console.log(queryPlanIngo)
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

async function handleChangeProjectCate (){
  //项目大类下项目分类
  projectClassList.value = await useRouteApi(findProjectClass,{schemaName:databaseTrue.value})({
    projectCate: projectCate.value,
  });

  //项目大类下项目
  projectList.value = await useRouteApi(findProject,{schemaName:databaseTrue.value})({
    projectCate: projectCate.value,
  });
  projectClass.value = ''
}
const handleChangeProjectClass = () => {
  project.value = ''
  //过滤分类下项目
  projectList.value = projectList.value.filter(v=> v.project_class_code === projectClass.value)
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
