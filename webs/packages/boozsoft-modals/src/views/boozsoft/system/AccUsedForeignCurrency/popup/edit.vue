<template>
  <BasicModal width="600px" v-bind="$attrs" title="常用外币" :canFullscreen="false"
              @ok="handleOk()" :closeFunc="handleClose" :showOkBtn="!isLook" @register="register"
              :ok-text="'保存'">
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <img src="/create.svg" style="width:25px;margin-right: 10px;"/>
        <span style="line-height: 25px;font-size: 16px;">常用外币</span>
      </div>
    </template>
    <div v-if="false" style="height: 500px">
      <BasicForm @register="registerForm"/>
    </div>
    <div v-else class="nc-open-content" style="height: 100%"
         :class="isLook?'nc-open-content-look':''">
        <div style="padding: 5%">
          <label style="font-size: 18px;margin-left: 0;">外币名称：</label>
          <a-select
            v-model:value="formItems.foreignName"
            show-search
            placeholder=""
            style="width: 60%;"
            :style="isEdit?{pointerEvents: 'none'}:''"
            allow-clear
            @change="codeChange"
          >
            <a-select-option v-for="item in currencyList" :value="item.currencyZhCnName"
                             :lable="item.currencyZhCnName">
              {{ item.currencyZhCnName }}
            </a-select-option>
          </a-select>
          <span class="red_span">⋆</span>
          <br/><br/>
          <label>币种国际代码：</label>
          <a-input v-model:value="formItems.foreignCode" placeholder=""/>
          <span class="red_span">⋆</span>
          <br/><br/>
          <label>货币单位：</label>
          <a-input v-model:value="formItems.currencyUnit" placeholder=""/>
          <span class="red_span">⋆</span>
          <br/><br/>
          <label>换算率：</label>
          <a-input v-model:value="formItems.foreignSimpName" placeholder=""/>
          <span class="red_span">⋆</span>
        </div>
    </div>
  </BasicModal>
</template>
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
:deep(.vben-basic-title) {
  color: rgb(1, 129, 226) !important;
}

.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
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
    width: 50%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  .abc{
    border-bottom: 2px solid #666666 !important;
    font-size: 18px;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  label {
    text-align: left;
    width: 110px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    color: #535353;
    font-size: 13px;
    margin-left: 1em;
    font-weight: bold;
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
let changeBeforeModel: any = {}
const [register, {closeModal, setModalProps}] = useModalInner(params => {
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

  // 1独立账套直接 读取 集团 币种信息表；0集团账套读取 组织-币种信息表
  // 2022年10月10日16:34:53 强制读取集团
  currencyList.value=[]
  // if(pageParameter.value.independent==='1'){
    findCurrencyTypeList().then(res => {
      currencyList.value = res.items
    })
  // }else{
  //   let map={ uniqueCode:pageParameter.value.accOrg }
  //   findAvailablesOrg(map).then(res=>{
  //     res.forEach(item=>{
  //       currencyList.value.push({abbreviation:item.foreignCode,currencyZhCnName:item.foreignName})
  //     })
  //   })
  // }
  // params.params
})


const resetForm = () => {
  formItems.value.id = null
  formItems.value.foreignCode = ''
  formItems.value.foreignName = ''
  formItems.value.foreignSimpName = ''
  formItems.value.beiyong1 = ''
  formItems.value.currencyUnit = ''
}

async function checkCode() {
  if ((changeBeforeModel.foreignCode != undefined && changeBeforeModel.foreignCode != '') || changeBeforeModel.foreignCode == formItems.value.foreignCode) {
    return true // 没有改动
  }
  let condition = {
    id: formItems.value.id,
    value: formItems.value.foreignCode,
    type: 'code',
  }
  const res = await useRouteApi(checkDuplicateAcc,{schemaName:pageParameter.value.tenantId})(condition)
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
const pageParameter = inject('pageParameter')
async function checkName() {
  if ((changeBeforeModel.foreignName != undefined && changeBeforeModel.foreignName != '') || changeBeforeModel.foreignName == formItems.value.foreignName) {
    return true
  }
  let condition = {
    id: formItems.value.id,
    value: formItems.value.foreignName,
    type: 'name',
  }
  const res = await useRouteApi(checkDuplicateAcc,{schemaName:pageParameter.value.tenantId})(condition)
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
  let list = currencyList.value.filter(item => item.currencyZhCnName == formItems.value.foreignName)
  if (list.length > 0) {
    formItems.value.foreignName = list[0].currencyZhCnName
    formItems.value.foreignCode = list[0].abbreviation
    formItems.value.foreignSimpName = list[0].fractionalCurrency
    formItems.value.beiyong1 = list[0].countryName
    formItems.value.currencyUnit = list[0].currencyUnit
  }
}

import {BasicForm, useForm} from '/@/components/Form/index'
import {checkDuplicateAcc, findAvailablesOrg} from "/@/api/boozsoft/group/UsedForeignCurrency";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";

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
