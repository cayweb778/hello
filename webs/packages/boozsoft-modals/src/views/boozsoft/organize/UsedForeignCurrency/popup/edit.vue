<template>
  <BasicModal width="600px" v-bind="$attrs" title="常用外币" :canFullscreen="false"
              @ok="handleOk()" :closeFunc="handleClose" :showOkBtn="!isLook" @register="register"
              :ok-text="'保存'">
    <div v-if="false" style="height: 500px">
      <BasicForm @register="registerForm"/>
    </div>
    <div v-else class="nc-open-content" style="height: 100%"
         :class="isLook?'nc-open-content-look':''">
      <div style="padding: 5%">
        <label>所属组织:</label>
        <a-select
          v-model:value="formItems.orgUniqueCode"
          show-search
          placeholder=""
          style="width: 60%;border: none"
          :style="isEdit?{pointerEvents: 'none'}:''"
          allow-clear
        >
          <a-select-option v-for="(item,index) in organizeList"
                           :key="index"
                           :value="item.uniqueCode"
          >{{ item.orgSimpName }}
          </a-select-option>
        </a-select>
        <label>外币代码:</label>
        <a-select
          v-model:value="formItems.foreignCode"
          show-search
          placeholder=""
          style="width: 60%;border: none"
          :style="isEdit?{pointerEvents: 'none'}:''"
          allow-clear
          @change="codeChange"
        >
          <a-select-option v-for="item in currencyList" :value="item.abbreviation"
                           :lable="item.abbreviation">
            {{ item.abbreviation }}<span role="img"
                                         :aria-label="item.currencyZhCnName">-{{
              item.currencyZhCnName
            }}</span>
          </a-select-option>
        </a-select>
        <span class="red_span">⋆</span>
        <br/>
<!--        <label>所属国家(地区):</label>-->
<!--        <a-input v-model:value="formItems.beiyong1" placeholder=""-->
<!--                 style="width: 60%;pointer-events: none"/>-->
<!--        <span class="red_span">⋆</span>-->
<!--        <br/>-->
        <label>外币名称:</label>
        <a-input v-model:value="formItems.foreignName" placeholder="" style="width: 60%"/>
        <span class="red_span">⋆</span>
        <br/>
        <label>货币单位:</label>
        <a-input v-model:value="formItems.currencyUnit" placeholder="" style="width: 60%"/>
        <span class="red_span">⋆</span>
        <br/>
        <label>外币换算规则:</label>
        <a-input v-model:value="formItems.foreignSimpName" placeholder="" style="width: 60%"/>
        <span class="red_span"></span>
      </div>
    </div>
  </BasicModal>
</template>
<style src="../../../../../assets/styles/global-open.less" lang="less" scoped></style>
<style lang="less" scoped>
:deep(.ant-form-item-control button) {
  display: none;
}

:deep(.ant-input-number-input) {
  width: 100%;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
}

:deep(.ant-calendar-picker-input.ant-input),
:deep(.ant-select-single:not(.ant-select-customize-input)
  .ant-select-selector
  .ant-select-selection-search-input) {
  border: none;
  width: 100%;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
}

:deep(.ant-input-number) {
  width: 90%;
  border: none;
}

:deep(.nc-open-content input ) {
  width: 100% !important;
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
  color: #747475;

  input {
    width: 32%;
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

  label {
    text-align: left;
    width: 120px;
    display: inline-block;
    padding: 6px 10px;
    margin-left: 15px;font-weight: bold;
  }

  .red_span {
    color: red;
    font-weight: bold;
    display: inline-block;
    width: 20px;
    text-align: right;
  }
}

:deep(.ant-select-disabled) {
  .ant-select-selector {
    background-color: white !important;
    color: black !important;
  }
}

.nc-border-div {
  position: relative;
  border: 1px solid #494949;
  margin: 3% 0;

  .nc-border-div-span {
    min-width: 80px;
    background-color: white;
    position: absolute;
    top: -12px;
    left: 50px;
    display: block;
    text-align: center;
    color: black;
    font-weight: bold;
  }

  .nc-border-div-content {
    padding: 10px;
    min-height: 40px;
  }
}
</style>
<script setup="props, {content}" lang="ts">
import {inject, reactive, ref, unref, watch} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {useMessage} from "/@/hooks/web/useMessage";
import {
  Select as ASelect,
  Input as AInput,
  Form
} from 'ant-design-vue'

import {findCurrencyTypeList} from "/@/api/record/system/financial-settings";
import {hasBlank} from "/@/api/task-api/tast-bus-api";

const Textarea = AInput.TextArea
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const {createMessage} = useMessage();
const emit = defineEmits(['register', 'save'])


const arr2 = ref([
  {field: 'name', label: '栏目名称'},
  {field: 'name2', label: '栏目名称2'},
  {field: 'name2', label: '栏目名称2'},
  {field: 'name2', label: '栏目名称2'},
  {field: 'name2', label: '栏目名称2'},
  {field: 'name2', label: '栏目名称2'},
  {field: 'name2', label: '栏目名称2'},
  {field: 'name2', label: '栏目名称2'},
  {field: 'name2', label: '栏目名称2'},
  {field: 'name2', label: '栏目名称2'},
])
const formItems: any = ref({})

const {createWarningModal} = useMessage();
const isStepOne = ref(true)

async function handleClose() {
  if (isStepOne.value) {
    return true;
  } else {
    isStepOne.value = true
    return false
  }
}

async function handleOk() {
  let changeAction = !(formItems.value.foreignCode == changeBeforeModel.foreignCode
    && formItems.value.foreignName == changeBeforeModel.foreignName && formItems.value.foreignSimpName == changeBeforeModel.foreignSimpName
  )
  if (!changeAction){
    createWarningModal({title: '温馨提醒', content: "暂未发现改变！"});
  }else if (hasBlank(formItems.value.orgUniqueCode)) {
    createWarningModal({title: '温馨提醒', content: "所属组织为必选项！"});
  }else if (hasBlank(formItems.value.foreignCode)) {
    createWarningModal({title: '温馨提醒', content: "外币国际代码为必选项！"});
  } else if (hasBlank(formItems.value.foreignName)) {
    createWarningModal({title: '温馨提醒', content: "外币名称为必选项！"});
  } else {
    if (await checkCode() && await checkName()) {
      emit('save', formItems.value)
      closeModal()
    }
  }
}

const isEdit = ref(false)
const isLook = ref(false)
const organizeList = inject('orgList')
  let changeBeforeModel: any = {}
const [register, {closeModal, setModalProps}] = useModalInner(async (params) => {
  /* if(params==2){
     resetFields() // 新增
   }else{
     setFieldsValue(params.params) // 修改
   }*/
  if(params==2) {
    resetForm()
  }else {
    changeBeforeModel = JsonTool.parseProxy(params.params)
    formItems.value = params.params
    isEdit.value = true
  }
  if (currencyList.value.length == 0) {
    findCurrencyTypeList().then(res => {
      currencyList.value = res.items
    })
  }
})


const resetForm = () => {
  formItems.value.id = null
  // formItems.value.orgUniqueCode = ''
  formItems.value.foreignCode = ''
  formItems.value.foreignName = ''
  formItems.value.foreignSimpName = ''
  formItems.value.beiyong1 = ''
}

async function checkCode() {
  if ((changeBeforeModel.foreignCode != undefined && changeBeforeModel.foreignCode != '') || changeBeforeModel.foreignCode == formItems.value.foreignCode) {
    return true // 没有改动
  }
  let condition = {
    id: formItems.value.id,
    value: formItems.value.foreignCode,
    type: 'code',
    uniqueCode: formItems.value.orgUniqueCode
  }
  const res = await checkDuplicateOrg(condition)
  if (res != 0) {
    if (res == -1) {
      createWarningModal({content: '请求参数不符合系统标准！'})
    } else {
      createWarningModal({content: '外币国际编码系统已存在，请重新选择！'})
    }
    resetForm()
    return false
  }
  return true
}

async function checkName() {
  if ((changeBeforeModel.foreignName != undefined && changeBeforeModel.foreignName != '') || changeBeforeModel.foreignName == formItems.value.foreignName) {
    return true
  }
  let condition = {
    id: formItems.value.id,
    value: formItems.value.foreignName,
    type: 'name',
    uniqueCode: formItems.value.orgUniqueCode
  }
  const res = await checkDuplicateOrg(condition)
  if (res != 0) {
    if (res == -1) {
      createWarningModal({content: '请求参数不符合系统标准！'})
    } else {
      createWarningModal({content: '外币名称已存在，请重新输入！'})
    }
    formItems.value.foreignName = ''
    return false
  }
  return true
}

const currencyList = ref([])

const codeChange = () => {
  if (null != formItems.value.foreignCode && formItems.value.foreignCode != '') {
    let list = currencyList.value.filter(item => item.abbreviation == formItems.value.foreignCode)
    if (list.length > 0) {
      formItems.value.foreignName = list[0].currencyZhCnName
      formItems.value.foreignSimpName = list[0].fractionalCurrency
      formItems.value.beiyong1 = list[0].countryName
      formItems.value.currencyUnit = list[0].currencyUnit
    }
  }
}

import {BasicForm, useForm} from '/@/components/Form/index'
import {checkDuplicateOrg} from "/@/api/boozsoft/group/UsedForeignCurrency";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
import {getOrganizeAll} from "/@/api/record/group/im-organize";

const recordData = inject(['recordData'])
const [registerForm, {
  getFieldsValue,
  setFieldsValue,
  updateSchema,
  resetFields,
  validate
}] = useForm({
  labelWidth: 100,
  schemas: recordData.value.columns.map(item => {
    return {
      field: item.field,
      label: item.label,
      component: 'Input',
    }
  }),
  showActionButtonGroup: false,
  actionColOptions: {
    span: 23
  }
})

</script>
