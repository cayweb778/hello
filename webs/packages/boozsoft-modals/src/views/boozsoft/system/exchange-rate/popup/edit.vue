<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="汇率设置"
    @ok="handleOk()"
    :showCancelBtn="true"
    :canFullscreen="false"
    @register="register"
  >
    <div
      style="height: 100%"
      :class="true?'nc-open-content':'nc-open-show-content'"
    >
      <div class="open-content-up">
        <label>外币币种：</label>
        <a-select
          v-model:value="formItems.localCode"
          show-search
          placeholder=""
          style="width: 30%"
          :style="isEdit?{pointerEvents: 'none'}:{}"
          @change="handleChange"
        >
          <a-select-option v-for="c in showCurrencyList" :value="c.foreignName">
            {{ c.foreignName }}
          </a-select-option>
        </a-select>
        <span class="red_span"></span>
        <label>国际代码：</label>
        <a-input v-model:value="formItems.foreignCode" style="pointer-events: none" placeholder=""/>
        <span class="red_span"></span>
        <br>

        <label>汇率小数位精度：</label>
        <a-select v-model:value="formItems.decimalNum" placeholder="" style="width: 30%;">
          <a-select-option value="0">0</a-select-option>
          <a-select-option value="1">1</a-select-option>
          <a-select-option value="2">2</a-select-option>
          <a-select-option value="3">3</a-select-option>
          <a-select-option value="4">4</a-select-option>
          <a-select-option value="5">5</a-select-option>
          <a-select-option value="6">6</a-select-option>
          <a-select-option value="7">7</a-select-option>
          <a-select-option value="8">8</a-select-option>
          <a-select-option value="9">9</a-select-option>
        </a-select>
        <span class="red_span"></span>
        <label>最大误差：</label>
        <a-input v-model:value="formItems.errorValue" placeholder="2"/>
        <span class="red_span"></span>
        <br>

        <label>计算方式：</label>
        <a-select v-model:value="formItems.comMethod" placeholder="" style="width: 30%;">
          <a-select-option value="*">原币*汇率=本位币</a-select-option>
          <a-select-option value="/">原币/汇率=本位币</a-select-option>
        </a-select>
        <span class="red_span"></span>

        <label>取值方式：</label>
        <a-select default-value=""  v-model:value="formItems.valueMethod"  style="width: 30%;">
          <a-select-option value="VOUCHER">
            距离凭证日期最近
          </a-select-option>
          <a-select-option value="SYSTEM">
            距离系统日期最近
          </a-select-option>
        </a-select>
        <span class="red_span"></span>
        <br>
        <label>汇率区间：</label>
        <a-select  v-model:value="formItems.rateStyle"  style="width: 30%;">
          <a-select-option value="MONTH">按月设置</a-select-option>
          <a-select-option value="DAY">按日设置</a-select-option>
        </a-select>
        <span class="red_span"></span>
        <label>预置期间：</label>
        <a-select  v-model:value="formItems.beiyong1"  style="width: 30%;">
          <a-select-option value="YEAR">年</a-select-option>
          <a-select-option value="MONTH">年月</a-select-option>
        </a-select>
        <span class="red_span"></span>
      </div>
      <br/>
      <a-checkbox v-model:checked="preset" style="width: 160px">预置月份或天数</a-checkbox>
    </div>
  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {inject, ref, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {
  Input as AInput,
  Select as ASelect,
  Tabs as ATabs,
  Checkbox as ACheckbox,
} from "ant-design-vue";
import {useMessage} from "/@/hooks/web/useMessage";
import {hasBlank} from "/@/api/task-api/tast-bus-api";

const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ATabPane = ATabs.TabPane
const emit = defineEmits(['register','save'])

const {
  createWarningModal
} = useMessage()

const formItems: any = ref({})

const treeData: any = ref([])

const psnTypeList: any = ref([])

// 是否为修改
const isEdit = ref(false)
const preset = ref(true)

let changeBeforeModel: any = {}
const accountInfo = inject('accountInfo')
const currencyList = inject('currencyList')
const infoList = inject('infoList')
const showCurrencyList = ref([])
// 常用外币列表
const [register, {closeModal}] = useModalInner((data) => {
  // 方式2
  formItems.value = data.data
  if (hasBlank(data.data?.id)){
    isEdit.value = false
    let keys = infoList.value.map(item=>item.foreignCode)
    showCurrencyList.value = currencyList.value.filter(item=>keys.indexOf(item.foreignCode) == -1)
    resetForm()
    // 获取精度设置
  }else {
    isEdit.value = true
    showCurrencyList.value = currencyList.value
  }
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged: boolean = false

async function handleOk() {
  if (hasBlank(formItems.value.localCode)){
    createWarningModal({title:'温馨提示',content:'外币币种为必选项！'})
  }else if(hasBlank(formItems.value.decimalNum)){
    createWarningModal({title:'温馨提示',content:'汇率小数位精度为必选项！'})
  }else if(hasBlank(formItems.value.errorValue)){
    createWarningModal({title:'温馨提示',content:'最大误差为必填项！'})
  }else if(hasBlank(formItems.value.comMethod)){
    createWarningModal({title:'温馨提示',content:'汇率小数位精度为必选项！'})
  }else if(hasBlank(formItems.value.valueMethod)){
    createWarningModal({title:'温馨提示',content:'取值方式为必选项！'})
  }else if(hasBlank(formItems.value.rateStyle)){
    createWarningModal({title:'温馨提示',content:'汇率区间为必选项！'})
  }else if(hasBlank(formItems.value.beiyong1)){
    createWarningModal({title:'温馨提示',content:'预置期间为必选项！'})
  }else {
    formItems.value.beiyong2 = preset.value?'1':'0'
    isChanged = !(formItems.value.id == changeBeforeModel.id
      && formItems.value.decimalNum == changeBeforeModel.decimalNum
      && formItems.value.foreignCode == changeBeforeModel.foreignCode
      && formItems.value.localCode == changeBeforeModel.localCode
      && formItems.value.valueMethod == changeBeforeModel.valueMethod
      && formItems.value.rateStyle == changeBeforeModel.rateStyle
      && formItems.value.errorValue == changeBeforeModel.errorValue
      && formItems.value.beiyong1 == changeBeforeModel.beiyong1
      && formItems.value.beiyong2 == changeBeforeModel.beiyong2
    )
    if (isChanged) {
      emit('save', unref(formItems))
      closeModal()
      return true
    }else {
      createWarningModal({title:'温馨提示',content:'暂未发现任何修改！'})
    }
  }
  return false
}
const handleChange = (v) => {
  formItems.value.foreignCode =  currencyList.value.filter(item=>item.foreignName == v)[0].foreignCode
}
const resetForm = () => {
  formItems.value.comMethod = '*'
  formItems.value.decimalNum = '2'
  formItems.value.valueMethod = 'VOUCHER'
  formItems.value.rateStyle = 'MONTH'
  formItems.value.beiyong1 = 'YEAR'
  formItems.value.beiyong2 = '1'
}
</script>
<style src="../../../../../assets/styles/global-open.less" lang="less" scoped></style>
<style lang="less" scoped>
:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
}

:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none !important;
}

:deep(.vben-basic-title) {
  color: rgb(1, 129, 226) !important;
}

:deep(.ant-modal-body) {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}

.red_span {
  color: red;
  font-weight: bold;
  display: inline-block;
  width: 20px;
  text-align: right;
}

.nc-open-content {

  input {
    width: 30%;
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

  label {
    text-align: left;
    width: 150px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 2em;
    color: #535353;
  }

  .open-content-down {
    margin-top: 5%;

    .ant-tabs-tab-active::before {
      position: absolute;
      top: 0px;
      left: 0;
      width: 100%;
      border-top: 2px solid transparent;
      border-radius: 2px 2px 0 0;
      transition: all 0.3s;
      content: '';
      pointer-events: none;
      background-color: rgb(1, 143, 251);
    }
  }
}

.nc-open-show-content {

  input {
    width: 30%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  pointer-events: none;
  cursor: default;

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label {
    text-align: left;
    width: 150px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 2em;
    color: #535353;
  }

  .open-content-down {
    margin-top: 5%;

    :deep(.ant-tabs-tab) {
      pointer-events: auto;
    }

    .ant-tabs-tab-active::before {
      position: absolute;
      top: 0px;
      left: 0;
      width: 100%;
      border-top: 2px solid transparent;
      border-radius: 2px 2px 0 0;
      transition: all 0.3s;
      content: '';
      background-color: rgb(1, 143, 251);
    }
  }
}
</style>
