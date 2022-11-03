<template>
  <BasicModal width="920px" v-bind="$attrs"
              title="存货管理账套" @ok="handleOk()"
              :ok-text="isEdit?'保存':'开始建账'"
              :cancel-text="isLook?'关闭':!isEdit?'放弃':'取消'"
              :showOkBtn="showNext" @register="register"
              :closeFunc="handleClose" :loading="modelLoadIng"
              :canFullscreen="false" :maskClosable="false" :loadingTip="loadingText">
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <PlusCircleOutlined v-if="!isEdit" style="font-size: 50px;color: #0096c7;"/>
        <FormOutlined v-else-if="isEdit && !isLook" style="font-size: 50px;color: #0096c7;"/>
        <FileSearchOutlined v-else style="font-size: 50px;color: #0096c7;"/>
        <span style="line-height:50px;font-size: 30px;color: #0096c7;">&ensp;存货管理账套</span>
      </div>
      <div style="display: inline-block;position:absolute;right: 50px;top: 20px;background:#ffffff">
        <img src="/@/assets/images/zt.png" style="height:100px;margin-right: 10px;"/>
      </div>
    </template>
    <div class="nc-open-content" :class="isLook?'nc-open-content-look':''" style="height: 100%">
      <div
        style="display: inline-flex;justify-content: space-between;width: 625px;margin:3%  14% 5%">
        <div style="width: 600px;">
          <a-radio-group name="radioGroup" v-model:value="formItems.flag" :disabled="!isEdit"
                         style="margin: 0;">
            <a-radio style="font-weight: bold" value="1">正常</a-radio>
            <a-radio style="font-weight: bold" value="0">封存</a-radio>
          </a-radio-group>
          <br/><br/>
          <span style="font-size: 18px;font-weight: bold;">账套名称:</span>
          <a-input v-model:value="formItems.stockAccName" placeholder=""         :disabled="isEdit || formItems.associateCoCode == formItems.coCode "
                   style="margin-left: 3%;font-size: 20px;width: 380px;font-weight: bold;border-bottom: 3px #666666 solid !important;"
                   @keydown.enter.native="$refs.focus1.focus()"/>
          <span class="red_span">⋆</span>
        </div>
      </div>
      <br/>
      <label>所属公司(单位)代码：</label>
      <a-select
        v-model:value="formItems.uniqueCode"
        placeholder=""
        style="width: 25%;"
        allow-clear
        :disabled="!isLook && isEdit"
        @change="unitChange"
        ref="focus1" @keydown.enter.native="$refs.focus2.focus()"
      >
        <a-select-option v-for="(item,index) in unitList"
                         :key="index"
                         :value="item.id">{{ item.coCode }} {{ item.accNameCn }}
        </a-select-option>
      </a-select>
      <span class="red_span">⋆</span>
      <label>所属公司(单位)全称：</label>
      <a-input v-model:value="formItems.stockAccId" :disabled="true" placeholder=""
               style="width: 25%"/>
      <span class="red_span">⋆</span>
      <br/>
      <label>账套代码：</label>
      <a-input v-model:value="formItems.coCode" @keyup="inputLimit" @blur="checkCode()"
               placeholder="二位公司代码+一位数字字母"
               style="width: 25%;text-align: center" :disabled="isLook || isEdit" ref="focus2"
               @keydown.enter.native="$refs.focus3.focus()"/>
      <span class="red_span">⋆</span>
      <label>关联财务会计账套：</label>
      <a-select
        v-model:value="formItems.associateCoCode"
        placeholder=""
        style="width: 25%;"
        allow-clear
        :disabled="isEdit || formItems.associateCoCode == formItems.coCode "
        ref="focus3" @keydown.enter.native="$refs.focus5.focus()"
      >
        <a-select-option v-for="(item,index) in getAccountList(formItems.uniqueCode)"
                         :key="index"
                         :value="item.coCode">{{ item.coCode }} {{ item.accNameFull }}
        </a-select-option>
      </a-select>
      <span class="red_span"></span>
      <br/>
      <label>启用日期：</label>
      <DatePicker picker="month" v-model:value="formItems.startDate" :disabled="!isLook && isEdit"
                  value-format="YYYY-MM" ref="focus5" @keydown.enter.native="$refs.focus6.focus()"
                  format="YYYY年MM月" style="text-align-last: center;width: 25%"/>
      <span class="red_span">⋆</span>
      <label>成本计价方式：</label>
      <a-select v-model:value="formItems.jjfs" style="text-align: center;width: 25%"
                ref="focus6" @keydown.enter.native="$refs.focus7.focus()">
        <a-select-option value="1">实时计价</a-select-option>
        <a-select-option value="0">定时计价</a-select-option>
      </a-select>
      <span class="red_span"></span>
      <br/>
      <label>本位币：</label>
      <a-select v-model:value="formItems.currency" style="text-align: center;width: 25%"
                :disabled="!isLook && isEdit"
                ref="focus7" @keydown.enter.native="$refs.focus8.focus()">
        <a-select-option v-for="item in currencyList" :value="item.abbreviation">
          {{ item.currencyZhCnName }}
        </a-select-option>
      </a-select>
      <span class="red_span">⋆</span>
      <label>国家(地区)：</label>
      <a-select
        v-model:value="formItems.country"
        show-search
        placeholder=""
        style="width: 25%;border: none;text-align: center"
        allow-clear
        ref="focus8"
      >
        <a-select-option
          v-for="psn in countryList"
          :key="psn.id"
          :value="psn.uniqueCode"
        >{{ psn.namech }}
        </a-select-option>
      </a-select>
      <span class="red_span">⋆</span>
      <br/>
    </div>
  </BasicModal>
</template>
<style lang="less" scoped>
@import '/@/assets/styles/part-open.less';
:deep(.ant-calendar-picker-input.ant-input),
:deep(.ant-select-single:not(.ant-select-customize-input)
  .ant-select-selector
  .ant-select-selection-search-input), :deep(.ant-picker), :deep(.ant-input-number) {
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

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  :deep(.ant-select-selector), :deep(.ant-input-number-input), :deep(.ant-picker-input) input {
    font-weight: bold;
  }

  label:not(.ant-radio-wrapper) {
    text-align: left;
    width: 180px;
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
  findByNameCode,
  findByUniqueCodeStock, getAccountAvailable,
} from "/@/api/record/group/im-unit";
import {
  Select as ASelect,
  Input as AInput,
  InputNumber,
  DatePicker,
  Radio as ARadio, Tabs,
  message
} from 'ant-design-vue';
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

const {createMessage} = useMessage();
const emit = defineEmits(['register', 'save'])

const formItems: any = ref({})
const isOrg: any = ref(true) //是否集团

const loadingText = ref('请稍等...')
const {createWarningModal} = useMessage();

async function handleClose() {
  showNext.value = true
  return true
}

function getAccountList(v) {
  return accountList.value.filter(it => it.uniqueCode == v)
}
async function handleOk() {
  showNext.value = false
  if (hasBlank(formItems.value.stockAccName)) {
    createWarningModal({content: '账套名称为必填项！'})
    showNext.value = true
  } else if (hasBlank(formItems.value.uniqueCode)) {
    createWarningModal({content: '所属公司(单位)代码为必选项！'})
    showNext.value = true
  }  else if (formItems.value.coCode.length != 3 || !formItems.value.coCode.startsWith(unitCoCode.value)) {
    createWarningModal({content: '公司（单位）编码长度只能为三位且二位公司代码【' + unitCoCode.value + '】+一位数字字母！'})
    showNext.value = true
  }else if (hasBlank(formItems.value.startDate)) {
    createWarningModal({content: '启用日期为必选项！'})
    showNext.value = true
  } else if (hasBlank(formItems.value.currency)) {
    createWarningModal({content: '本位币为必选项！'})
    showNext.value = true
  } else if (hasBlank(formItems.value.country)) {
    createWarningModal({content: '国家（地区）为必选项！'})
    showNext.value = true
  } else {
    // 当为独立账套时 必填
    modelLoadIng.value = true
    if (!isEdit.value) loadingText.value = '建账中...正在等待数据初始化...请耐心等待创建完成!'
    console.log(formItems.value)
    emit('save', {
      closeOpen() {
        showNext.value = true
        modelLoadIng.value = false
        closeModal()
      },
      data: unref(formItems.value)
    })
  }
  return false;
}

import {usePlatformsStore} from "/@/store/modules/platforms";

const mark = usePlatformsStore().getCurrentPlatformId
const modelLoadIng = ref(true)
const showNext = ref(true)
const isEdit = ref(true)
const isLook = ref(false)
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
const currencyList = ref([])
const accountList = ref([])
const [register, {closeModal, setModalProps}] = useModalInner(async ({data}) => {
  isEdit.value = data.isEdit
  isLook.value = data.isLook
  countryList.value = data.countryList
  currencyList.value = data.currencyList
  unitList.value = data.unitList
  data.currencyList = []
  data.countryList = []
  data.unitList = []
  formItems.value = data
  changeBeforeModel = JSON.parse(JSON.stringify(formItems))
  if (!isEdit.value) {
    let fArr = await findByUniqueCodeStock() || []
    accountList.value = await getAccountAvailable() || []
    if (fArr.length > 0) unitList.value = unitList.value.filter(it => fArr.indexOf(it.accId) == -1)
    if (countryList.value.length > 0 && hasBlank(formItems.value.country)) {
      let iarr = countryList.value.filter(it => it.namech.indexOf('中国') != -1).map(it => it.uniqueCode)
      if (iarr.length > 0) formItems.value.country = iarr[0]
    }
    if (currencyList.value.length > 0 && hasBlank(formItems.value.currency)) {
      let iarr = currencyList.value.filter(it => it.currencyZhCnName.indexOf('人民币') != -1).map(it => it.abbreviation)
      if (iarr.length > 0) formItems.value.currency = iarr[0]
    }
    formItems.value.flag = '1'
    formItems.value.jjfs = '1'
  } else {
    unitChange(formItems.value.uniqueCode)
  }
  setModalProps({minHeight: 320});
  modelLoadIng.value = false
  if (isLook.value) showNext.value = false
})

async function checkName() {
  formItems.value.stockAccName = formItems.value.stockAccName.trim()
  if (hasBlank(formItems.value.stockAccName)) return false;
  if ((changeBeforeModel._value.stockAccName != undefined && changeBeforeModel._value.stockAccName != '') || changeBeforeModel._value.stockAccName == formItems.value.stockAccName) {
    return true
  }
  let data = {
    id: formItems.value.id,
    accNameCn: formItems.value.stockAccName,
  }
  const res = await findByNameCode(data)
  if (res != 0) {
    createWarningModal({content: '管理名称已存在，请重新输入！'})
    formItems.value.stockAccName = ''
    return false
  }
  return true
}
const unitCoCode = ref('')
const inputLimit = (v) => {
  v.currentTarget.value = v.currentTarget.value.replace(/[^\w\.\/]/ig, '').toUpperCase().replace(/I/ig, '').replace(/O/ig, '');
  if ('' != unitCoCode.value && !v.currentTarget.value.startsWith(unitCoCode.value)) v.currentTarget.value = unitCoCode.value
  if (v.currentTarget.value.length > 3) v.currentTarget.value = v.currentTarget.value.substring(0, 3)
  return v
}
const unitChange = (v) => {
  if (!hasBlank(v)) {
    let thisUnit = unitList.value.filter(it => it.id == v)[0]
    formItems.value.stockAccId = thisUnit?.accName
    unitCoCode.value = (thisUnit?.coCode || '').toUpperCase()
    if (!isEdit.value) formItems.value.coCode = (thisUnit?.coCode || '').toUpperCase()
  } else {
    formItems.value.stockAccId = ''
  }
}

async function checkCode() {
  if (hasBlank(formItems.value.coCode)) return false;
  if ((changeBeforeModel._value.coCode != undefined && changeBeforeModel._value.coCode != '') || changeBeforeModel._value.coCode == formItems.value.coCode) {
    return true
  }
  let data = {
    id: formItems.value.id,
    code: formItems.value.coCode,
    // accId: formItems.value.uniqueCode
  }
  const res = await findByNameCode(data)
  if (res != 0) {
    createWarningModal({content: '账套代码已存在，请重新输入！'})
    formItems.value.coCode = unitCoCode.value
    return false
  }
  let arr = (getAccountList(formItems.value.uniqueCode)).filter(it => it.coCode == formItems.value.coCode)
  if (arr.length > 0){
    formItems.value.associateCoCode = arr[0].coCode
    formItems.value.stockAccName = arr[0].accNameFull
  }else{
    formItems.value.associateCoCode = ''
  }
  return true
}
</script>
