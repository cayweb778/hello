<template>
  <BasicModal width="800px" v-bind="$attrs"
              title="财务会计账套" @ok="handleOk()"
              :ok-text="isEdit?'开始复制':'开始复制'"
              :cancel-text="isLook?'关闭':'取消'"
              :showOkBtn="showNext" @register="register"
              :closeFunc="handleClose" :loading="modelLoadIng"
              :canFullscreen="false" :maskClosable="false" :loadingTip="loadingText">
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <PlusCircleOutlined v-if="!isEdit" style="font-size: 50px;color: #0096c7;"/>
        <FormOutlined v-else-if="isEdit && !isLook" style="font-size: 50px;color: #0096c7;"/>
        <FileSearchOutlined v-else style="font-size: 50px;color: #0096c7;"/>
        <span style="line-height:50px;font-size: 30px;color: #0096c7;">&ensp;财务会计账套</span>
      </div>
      <div style="display: inline-block;position:absolute;right: 50px;top: 20px;background:#ffffff">
        <img src="/@/assets/images/zt.png" style="height:100px;margin-right: 10px;"/>
      </div>
    </template>
    <div class="nc-open-content" :class="isLook?'nc-open-content-look':''" style="height: 100%">
      <div style="display: inline-flex;justify-content: space-between;width: 620px;margin:5%  8%">
        <div style="width: 600px;">
          <a-radio-group name="radioGroup" v-model:value="formItems.independent"
                         :style="{pointerEvents: 'none'}" style="margin: 0;">
            <a-radio style="font-weight: bold" value="1" v-if="mark == 1003">独立核算</a-radio>
            <a-radio style="font-weight: bold" value="0">集团账套</a-radio>
          </a-radio-group>
          <br/><br/>
          <span style="font-size: 18px;font-weight: bold;">来源账套:</span>
          <a-select
            v-model:value="formItems.id"
            placeholder=""
            style="margin-left: 3%;font-size: 20px;width: 380px;"
            @change="unitChange"
            class="special-bold-select"
            @keydown.enter.native="$refs.focus1.focus()">
            <a-select-option v-for="(item,index) in unitList" :key="index"
                             :value="item.id">{{ item.accNameFull }}
            </a-select-option>
          </a-select>
        </div>
      </div>
      <br/>
      <label>新账套代码：</label>
      <a-input v-model:value="formItems.coCode"  @keyup="inputLimit" @blur="checkCode()"
               placeholder="二位公司代码+一位数字字母"
               style="width: 28%;text-align: center" ref="focus1"
               @keydown.enter.native="$refs.focus2.focus()"/>
      <span class="red_span">⋆</span>
      <label>新账套名称：</label>
      <a-input v-model:value="formItems.accNameFull" placeholder=""
               style="width: 28%" ref="focus2" @keydown.enter.native="$refs.focus3.focus()"
      />
      <!--    @keydown.enter.native="$refs.focus19.focus(),formItems.accNameCn = formItems.accName,checkName()"   -->
      <span class="red_span">⋆</span>
<!--
      <label>所属集团组织：</label>
      <a-select
        v-model:value="formItems.accGroup"
        placeholder=""
        style="width: 28%;"
        allow-clear
        :disabled="true"
        ref="focus2" @keydown.enter.native="$refs.focus3.focus()"
      >
        <a-select-option v-for="(item,index) in organizeList"
                         :key="index"
                         :value="item.uniqueCode"
        >{{ item.orgName }}
        </a-select-option>
      </a-select>
      <span class="red_span"></span>
      <label>本位币：</label>
      <a-select v-model:value="formItems.currency" style="text-align: center;width: 28%" :disabled="true"
                ref="focus3" @keydown.enter.native="$refs.focus4.focus()">
        <a-select-option v-for="item in currencyList" :value="item.abbreviation">
          {{ item.currencyZhCnName }}
        </a-select-option>
      </a-select>
      <span class="red_span">⋆</span>
      <br/>
     -->
      <template v-if="formItems.independent == '1'">
        <label>年度开始日期：</label>
        <MonthPicker v-model:value="formItems.yearStartDate" value-format="YYYY-MM-DD"
                     format="YYYY-MM-DD" ref="focus3" @keydown.enter.native="$refs.focus5.focus()"
                     style="width: 28%;text-align-last: center;" @change="dateChange"
                     :disabled="isEdit"/>
        <span class="red_span">⋆</span>
        <label>年度结束日期：</label>
        <DatePicker v-model:value="formItems.yearEndDate" value-format="YYYY-MM-DD"
                    format="YYYY-MM-DD" style="width: 28%;pointer-events: none; cursor: default;text-align-last: center;"/>
        <br>
<!--        <label>会计期间数量：</label>
        <a-select v-model:value="formItems.periodNum" @change="changePeriodNum"
                  style="text-align: center;width: 28%" :disabled="isEdit" ref="focus4" @keydown.enter.native="$refs.focus5.focus()">
          <a-select-option v-for="index in 5" :value="(index+11)+''">
            {{ index + 11 }}
          </a-select-option>
        </a-select>
        <span class="red_span">⋆</span>-->
      </template>
      <template v-else>
        <label>开始年度：</label>
        <DatePicker placeholder="" picker="year" v-model:value="startYaerValue"
                    format="YYYY"
                    style="width: 28%;text-align-last: center;"
                    :disabled="isEdit"
                    @change="panelChangeOne" ref="focus3"
                    @keydown.enter.native="$refs.focus5.focus()"/>

        <span class="red_span">⋆</span>
      </template>
      <label>启用期间：</label>
      <a-select v-model:value="formItems.startPeriod"
                style="text-align: center;width: 28%" :disabled="isEdit"  ref="focus5">
        <a-select-option v-for="index in dynamicInterval" :value="index">
          {{ index }}
        </a-select-option>
      </a-select>
      <span class="red_span">⋆</span>
<br/>
      <label>会计准则：</label>
      <a-select v-model:value="formItems.accStandard"  :disabled="true" style="width: 28%;text-align: center;"
                @change="standardChange">
        <a-select-option v-for="item in acountStandardList"
                         :value="item.id+''">
          {{ item.tname }}
        </a-select-option>
      </a-select>
      <span class="red_span"></span>
      <label>科目级次：</label>
      <span style="display: inline-block;color: black;width: 124px;text-align: center">{{
          levelPrefix || '选择准则'
        }}-</span>
      <a-input v-model:value="levelSuffix" placeholder="" ref="focus6" style="width: 12%"
               @keyup="importLimit" @blur="importLimit"/>
      <span class="red_span"></span>
      <br/>
<!--      <br/>
      <label>新公司简称：</label>
      <a-input v-model:value="formItems.accNameCn" @blur="checkName()" placeholder=""
               style="width: 28%" ref="focus18"
               @keydown.enter.native="$refs.focus19.focus()"/>
      <span class="red_span">⋆</span>
      <label>主管账户：</label>
      <a-select
        :allowClear="true"
        v-model:value="supervisors"
        show-search
        mode="multiple"
        placeholder=""
        ref="focus19"
        style="width: 28%"
      >
        <a-select-option v-for="(item,index) in userList" :key="index" :value="item.id">
          {{ item.realName }}
        </a-select-option>
      </a-select>-->
    </div>
  </BasicModal>
</template>
<style lang="less" scoped>

@import '/@/assets/styles/part-open.less';

:deep(.ant-calendar-picker-input.ant-input),
:deep(.ant-select-single:not(.ant-select-customize-input)
  .ant-select-selector
  .ant-select-selection-search-input), :deep(.ant-picker) {
  border: none;
  width: 100%;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  text-align: center;
}

:deep(.ant-select-single:not(.ant-select-customize-input)
  .ant-select-selector
  .ant-select-selection-search-input) {
  border: none !important;
}


.nc-open-content-look {
  pointer-events: none;
  cursor: default;
}

.nc-open-content {
  color: #666666;

  input {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  .special-bold-select {
    :deep(.ant-select-selector) {
      border: none;
      border-color: black !important;
      border-width: 2px !important;
    }
  }

  :deep(.ant-upload-picture-card-wrapper) {
    width: 88px;

    .ant-upload-select-picture-card {
      width: 280px;
      height: 80px;
      margin-right: 0;
      margin-bottom: 0;

      .ant-upload {
        //padding: 0;
      }
    }
  }

  :deep(.ant-cascader-input) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label:not(.ant-radio-wrapper) {
    text-align: left;
    width: 120px;
    display: inline-block;
    padding: 6px 10px;
    margin-left: 15px;
    font-weight: bold;
  }

  .red_span {
    color: red;
    font-weight: bold;
    display: inline-block;
    width: 20px;
    text-align: right;
  }

  :deep(.ant-calendar-picker-input) {
    background: white;
    color: black;
  }
}
</style>
<script setup="props, {content}" lang="ts">
import {computed, ref, unref, watch} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {useMessage} from "/@/hooks/web/useMessage";
import {
  findByCode, findByCode2,
  findByName, findLastValByCoCode,
  getUnitAvailables
} from "/@/api/record/group/im-unit";
import {
  Select as ASelect,
  Input as AInput,
  DatePicker,
  Radio as ARadio, Tabs,
  Checkbox as ACheckbox, Cascader, Upload, message
} from 'ant-design-vue';
import {
  findCurrencyTypeList, findGroupVoucherTypes,
  initBasisTabAccoutData
} from "/@/api/record/system/financial-settings";
import moment from "moment";
import {hasBlank} from "/@/api/task-api/tast-bus-api";

const MonthPicker = DatePicker.MonthPicker
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const ARadioGroup = ARadio.Group
const Textarea = AInput.TextArea
const TabPane = Tabs.TabPane
import {
  LoadingOutlined, PlusOutlined, PlusCircleOutlined, FormOutlined, FileSearchOutlined
} from '@ant-design/icons-vue';
import {importImg} from "/@/api/record/group/im-group";

const {createMessage} = useMessage();
const emit = defineEmits(['register', 'save'])

const formItems: any = ref({})
const isOrg: any = ref(true) //是否集团

const psnList: any = ref([])
const userList = ref([])
const supervisors = ref([])
const loadingText = ref('请稍等...')
const unitCoCode = ref('')
const {createWarningModal} = useMessage();
const inputLimit = (v) => {
  v.currentTarget.value = v.currentTarget.value.replace(/[^\w\.\/]/ig, '').toUpperCase().replace(/I/ig, '').replace(/O/ig, '');
  if ('' != unitCoCode.value && !v.currentTarget.value.startsWith(unitCoCode.value)) v.currentTarget.value = unitCoCode.value + v.currentTarget.value.substring(0, 1)
  if (v.currentTarget.value.length > 3) v.currentTarget.value = v.currentTarget.value.substring(0, 3)
  return v
}
async function handleClose() {
  showNext.value = true
  yearShowOne.value = false;
  return true
}


async function handleOk() {
    showNext.value = false
  if (hasBlank(formItems.value.id)) {
    createWarningModal({content: '来源财务账套为必选项！'})
    showNext.value = true
  } else if (formItems.value.independent == '0' && hasBlank(formItems.value.accGroup)) {
    createWarningModal({content: '财务账套所属组织为必选项！'})
    showNext.value = true
  } else {
    // 当为独立账套时 必填
    formItems.value.ccodeLevel = splitLevel(levelPrefix.value, levelSuffix.value)
    if (hasBlank(formItems.value.accStandard)) {
      createWarningModal({content: '财务账套会计准则为必选项！'})
      showNext.value = true
      return false;
    } else if (hasBlank(formItems.value.ccodeLevel)) {
      createWarningModal({content: '财务账套科目级次为必选项！'})
      showNext.value = true
      return false;
    }
    if (hasBlank(formItems.value.currency)) {
      createWarningModal({content: '财务账套本位币为必选项！'})
      showNext.value = true
    } else if (hasBlank(formItems.value.voucherTypeNum)) {
      createWarningModal({content: '财务账套凭证类别为必选项！'})
      showNext.value = true
    } else if (formItems.value.coCode.length != 3 || !formItems.value.coCode.startsWith(unitCoCode.value) || (!(/^[a-zA-Z0-9]+$/g.test(formItems.value.coCode)))) {
      createWarningModal({content: '公司（单位）编码长度只能为三位且只能为数字或字母！'})
      showNext.value = true
    } else if (hasBlank(formItems.value.coCode) || hasBlank(formItems.value.accNameFull)){
      createWarningModal({content: '财务账套新代码、新全称与简称为必填项！'})
      showNext.value = true
    }else if (hasBlank(formItems.value.yearStartDate)) {
      createWarningModal({content: '财务账套年度开始日期为必选项！'})
      showNext.value = true
    } else if (hasBlank(formItems.value.startPeriod)) {
      createWarningModal({content: '财务账套启用期间为必选项！'})
      showNext.value = true
    }  else {
      formItems.value.voucherTypeOtherNums = formItems.value.voucherTypeOtherNums.length !== 0 ? JSON.stringify(formItems.value.voucherTypeOtherNums) : ''
      modelLoadIng.value = true
      if (!isEdit.value) loadingText.value = '复制账套中...正在等待数据初始化...请耐心等待复制完成!'
      if (supervisors.value.length > 0) formItems.value.beiyong3 = JsonTool.json(supervisors.value)
       emit('save', {
        closeOpen() {
          showNext.value = true
          modelLoadIng.value = false
          closeModal()
        },
        data: unref(formItems.value)
      })
    }
  }
  return false;
}

import {usePlatformsStore} from "/@/store/modules/platforms";
import {JsonTool, StrTool} from "/@/api/task-api/tools/universal-tools";
import {findAllByGroupSysUserFlag} from "/@/api/record/accpanel/data";
import {findPeriodYears} from "/@/api/record/generalLedger/data";

const mark = usePlatformsStore().getCurrentPlatformId
const modelLoadIng = ref(true)
/*const totalChange=computed(()=>formItems.value.total)
watch(totalChange, async (a)=>{
    if (a != 0){
      modelLoadIng.value = false
      if (a > 0) {
        closeModal()
        message.info(isEdit.value?'修改公司成功！':'建账成功！')
      }else {
        createWarningModal({title: '温馨提示',content: isEdit.value?'修改公司失败！':'建账失败！'});
      }
      formItems.value.total = 0
    }
  }
)*/
const showNext = ref(true)
const isEdit = ref(true)
const isLook = ref(false)
const organizeList = ref([])
const unitList = ref([])

const focus1 = ref()
const focus2 = ref()
const focus3 = ref()
const focus4 = ref()
const focus5 = ref()
const focus6 = ref()
const focus7 = ref()
const focus8 = ref()
const focus9 = ref()
const focus10 = ref()
const focus11 = ref()
const focus12 = ref()
const focus13 = ref()
const focus14 = ref()
const focus15 = ref()
const focus16 = ref()
const focus17 = ref()
const focus18 = ref()
const focus19 = ref()
const focus20 = ref()
const focus21 = ref()
const focus22 = ref()
const focus23 = ref()
let changeBeforeModel: any = {}
const countryList = ref([])
const zoningList = ref([])
const industryList = ref([])
const upList = ref([])
const [register, {closeModal, setModalProps}] = useModalInner(async ({data}) => {
  await initBasisData()
  resetForm()
  isEdit.value = data.isEdit
  isLook.value = data.isLook
  acountStandardList.value = data.acountStandardList
  levelList.value = data.levelList
  currencyList.value = data.currencyList
  organizeList.value = data.organizeList
  data.organizeList = []
  data.acountStandardList = []
  data.currencyList = []
  data.levelList = []
  formItems.value = data
  formItems.value.id = ''
  formItems.value.voucherTypeOtherNums = !hasBlank(formItems.value.voucherTypeOtherNums) ? JSON.parse(formItems.value.voucherTypeOtherNums) : []
  supervisors.value = !hasBlank(formItems.value.beiyong3) ? JSON.parse(formItems.value.beiyong3) : []
  if (!isEdit.value) {

  } else {
    startYaerValue.value = formItems.value.yearStartDate.substring(0, 4)
    standardChange(formItems.value.accStandard)
  }
  changeBeforeModel = JSON.parse(JSON.stringify(formItems))
  setModalProps({minHeight: 320});
  modelLoadIng.value = false
  if (isLook.value) showNext.value = false
})

const resetForm = () => {
  formItems.value.id = ''
  formItems.value.accId = ''
  formItems.value.accName = ''
  formItems.value.coCode = ''
  formItems.value.accGroup = ''
  formItems.value.accNameCn = ''
  formItems.value.accNameFull = ''
  formItems.value.industryclassCode = []
  formItems.value.uniqueCodeZone = []
  formItems.value.corpCode = ''
  formItems.value.countryId = '0001'
  formItems.value.taxCode = ''
  formItems.value.contacts = ''
  formItems.value.telephone = ''
  formItems.value.address = ''
  formItems.value.website = ''
  formItems.value.remarks = ''
  formItems.value.independent = '0'
  formItems.value.accStandard = ''
  formItems.value.ccodeLevel = ''
  formItems.value.ibudgetAccStandard = '1'
  formItems.value.numberDec = '2'
  formItems.value.unitPriceDec = '2'
  formItems.value.rateDec = '5'
  formItems.value.accvouchDec = '4'
  formItems.value.periodNum = '12'
  formItems.value.currency = 'CNY'
  formItems.value.currencyName = '人民币'
  formItems.value.currencyCh = 'RMB'
  formItems.value.voucherTypeNum = '1'
  formItems.value.voucherTypeOtherNums = []
  formItems.value.yearStartDate = ''
  formItems.value.yearEndDate = ''
  formItems.value.startPeriod = ''
  dynamicInterval.value = []
  levelPrefix.value = ''
  levelSuffix.value = ''
  startYaerValue.value = ''
  unitCoCode.value = ''
}

async function checkCode() {
  formItems.value.coCode = formItems.value.coCode.trim()
  if ((changeBeforeModel._value.coCode != undefined && changeBeforeModel._value.coCode != '') || changeBeforeModel._value.coCode == formItems.value.coCode) {
    return true
  }
  const res = await findByCode2({id: '', coCode: formItems.value.coCode})
  if (res != 0) {
    createWarningModal({content: '账套代码已存在，请重新输入！'})
    formItems.value.coCode = unitCoCode.value
    return false
  }
  return true
}


const acountStandardList = ref([])
const levelList = ref([])
const pingZhengTypeList = ref([])
const currencyList = ref([])

const dynamicInterval = ref([])

watch(
  () => formItems.value.currency,
  () => {
    if (null != formItems.value.currency && formItems.value.currency != '') {
      let list = currencyList.value.filter(item => item.abbreviation == formItems.value.currency)
      if (list.length > 0) {
        formItems.value.currencyName = list[0].currencyZhCnName
        formItems.value.currencyCh = list[0].currencySymbol
      }
    }
  }
);

const initBasisData = async () => {
  /* if (acountStandardList.value.length == 0 || levelList.value.length == 0) {
   await  initBasisTabAccoutData().then(res => {
       acountStandardList.value = []
       levelList.value = []
       acountStandardList.value = res.acountStandardList
       levelList.value = res.levelList
     })
   }*/
  if (pingZhengTypeList.value.length == 0) {
    findGroupVoucherTypes().then(res => {
      pingZhengTypeList.value = res
    })
  }
  /* if (currencyList.value.length == 0) {
     findCurrencyTypeList().then(res => {
       currencyList.value = res.items
     })
   }*/
  if (userList.value.length == 0) {
    findAllByGroupSysUserFlag().then(res => {
      userList.value = res
    })
  }
  unitList.value = (await getUnitAvailables()).filter(it=>!hasBlank(it?.accStandard) && !hasBlank(it?.startPeriod))
  if (mark != 1003){ // 只显示组织
    unitList.value = unitList.value.filter(it=>!hasBlank(it?.accGroup))
  }
}

const dateChange = () => {
  if (!hasBlank(formItems.value.yearStartDate)) {
    let startDate = (formItems.value.yearStartDate.substring(0, 7)) + '-01'
    let d2 = new Date(startDate);
    d2.setFullYear(d2.getFullYear() + 1);
    d2.setDate(d2.getDate() - 1);
    formItems.value.yearEndDate = moment(d2).format('YYYY-MM-DD')
    formItems.value.yearStartDate = moment(startDate).format('YYYY-MM-DD')
  } else {
    formItems.value.yearEndDate = ''
    formItems.value.yearEndDate = ''
  }
  changePeriodNum()
}

const changePeriodNum = () => {
  formItems.value.startPeriod = ''
  if (formItems.value.yearStartDate != null) {
    let startDate = formItems.value.yearStartDate
    let periodNum = formItems.value.periodNum
    dynamicInterval.value = MonthsBetw(startDate, periodNum)
  } else {
    dynamicInterval.value = []
  }
}

const disabled = ref<boolean>(false);

interface MockData {
  key: string;
  title: string;
  description: string;
  disabled: boolean;
}

const mockData: MockData[] = [];
for (let i = 0; i < 20; i++) {
  mockData.push({
    key: i.toString(),
    title: `content${i + 1}`,
    description: `description of content${i + 1}`,
    disabled: i % 3 < 1,
  });
}
const oriTargetKeys = mockData.filter(item => +item.key % 3 > 1).map(item => item.key);
const targetKeys = ref<string[]>(oriTargetKeys);

const selectedKeys = ref<string[]>(['1', '4']);

function MonthsBetw(startDate, size) {
  const year = startDate.substring(0, 4)
  let i = 1
  let list = []
  while (i <= parseInt(size)) {
    let num = i;
    if (i < 10) {
      num = '0' + num
    }
    list.push(year + num)
    i++
  }
  return list
}

const yearShowOne = ref(false)

function openChangeOne(status) {
  if (status) {
    yearShowOne.value = true
  }
}

// 得到年份选择器的值
function panelChangeOne(value) {
  startYaerValue.value = value;
  yearShowOne.value = false;
  // 获取所属组织开始期间
    formItems.value.yearStartDate = value.format('YYYY') + '-' + (isEdit.value ? formItems.value.yearStartDate.substring(5, 10) : '01-01')
    formItems.value.yearEndDate = value.format('YYYY') + '-' + (isEdit.value ? formItems.value.yearEndDate.substring(5, 10) : '12-31')
    changePeriodNum()
}

const levelPrefix = ref('')
const levelSuffix = ref('')
const startYaerValue = ref('')
const standardChange = async (val) => {
  let obj = acountStandardList.value.filter(item => item.id == val)
  if (obj.length > 0) {
    levelPrefix.value = obj[0].tjici/*.replace(/-/g,"")*/
    if (formItems.value.ccodeLevel != '' && isEdit.value) {
      levelSuffix.value = formItems.value.ccodeLevel.replace(obj[0].tjici, '').replace(/-/g, "").split('').join('-')
    }
  }
}
const splitLevel = (a, b) => {
  let c = a
  if (b != '')
    c = c + '-' + b
  return c
}
const importLimit = (e) => {
  e.target.value = e.target.value.replace(/[^123456789]/g, "")
  if (!hasBlank(e.target.value)) e.target.value = e.target.value.split('').join('-')
  levelSuffix.value = e.target.value
}
const orgChange = () => {
  // 初始化 组织的值
  let obj = organizeList.value.filter((item) => item.uniqueCode == formItems.value.accGroup)[0]
  if (null != obj && formItems.value.independent != '1') {
    formItems.value.accStandard = obj.uniqueAccStandard

    formItems.value.yearStartDate = hasBlank(obj.yearStartDate) ? startYaerValue.value + '-01-01' : startYaerValue.value + '-' + obj.yearStartDate
    formItems.value.yearEndDate = hasBlank(obj.yearEndDate) ? startYaerValue.value + '-12-31' : startYaerValue.value + '-' + obj.yearEndDate
    formItems.value.periodNum = hasBlank(obj.periodNum) ? '12' : obj.periodNum

    formItems.value.numberDec = hasBlank(obj.numberDec) ? '2' : obj.numberDec
    formItems.value.unitPriceDec = hasBlank(obj.numberDec) ? '2' : obj.unitPriceDec
    formItems.value.rateDec = hasBlank(obj.numberDec) ? '5' : obj.rateDec
    formItems.value.accvouchDec = hasBlank(obj.numberDec) ? '4' : obj.accvouchDec
    formItems.value.voucherTypeNum = hasBlank(obj.voucherTypeNum) ? '' : obj.voucherTypeNum
    formItems.value.voucherTypeOtherNums = hasBlank(obj.voucherTypeOtherNums) ? [] : JSON.parse(obj.voucherTypeNum)
    if (hasBlank(formItems.value.ccodeLevel)) formItems.value.ccodeLevel = obj.ccodeLevel
    if (!hasBlank(formItems.value.accStandard)) standardChange(formItems.value.accStandard)
    if (hasBlank(obj.currency)) {
      formItems.value.currency = 'CNY'
    }
  }
}
const unitChange = (v) => {
  let thisUnit =  unitList.value.filter(it=>it.id == v)[0]

  formItems.value.independent = thisUnit?.independent
  formItems.value.accName = thisUnit?.accName
  formItems.value.ibudgetAccStandard = '1'

  formItems.value.currency =  thisUnit?.currency
  formItems.value.currencyCn = thisUnit?.currencyCn
  formItems.value.currencyName = thisUnit?.currencyName
  formItems.value.accStandard = thisUnit?.accStandard
  formItems.value.ccodeLevel = thisUnit?.ccodeLevel
  levelPrefix.value = thisUnit?.ccodeLevel
  formItems.value.voucherTypeNum = thisUnit?.voucherTypeNum

  formItems.value.yearStartDate = thisUnit?.yearStartDate
  formItems.value.yearEndDate = thisUnit?.yearEndDate
  formItems.value.periodNum = thisUnit?.periodNum

  if (!hasBlank(thisUnit?.accGroup)){
    formItems.value.accGroup = thisUnit?.accGroup
    startYaerValue.value = thisUnit?.startPeriod.substring(0,4)
    isOrg.value = true
    orgChange()
  }else {
    isOrg.value = false
    formItems.value.accGroup = ''
    startYaerValue.value = ''
  }
  changePeriodNum()
  formItems.value.startPeriod = thisUnit?.startPeriod
  unitCoCode.value = thisUnit?.coCode.substring(0,2)
  getVar(v, unitCoCode.value)
}

async function getVar(id, coCode) {
  let s = await findLastValByCoCode({id: id, coCode: coCode})
  if (s != null) formItems.value.coCode = coCode + s
}


async function checkName() {
  formItems.value.accName = StrTool.closeSpecialChars3(formItems.value.accName)
  formItems.value.accNameCn = StrTool.closeSpecialChars3(formItems.value.accNameCn)
  if ((changeBeforeModel._value.accNameCn != undefined && changeBeforeModel._value.accNameCn != '') || changeBeforeModel._value.accNameCn == formItems.value.accNameCn) {
    return true
  }
  const res = await findByName(formItems.value)
  if (res != 0) {
    createWarningModal({content: '集团简称已存在，请重新输入！'})
    formItems.value.accNameCn = ''
    return false
  }
  return true
}
</script>
