<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="账户信息"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">

        <label>账户名称</label>
        <a-input v-model:value="formItems.corpBankName" placeholder="请输入账户名称" style="font-size: 18px" />
        <label>账户类型</label>
        <a-select v-model:value="formItems.corpBankType" placeholder="现金/银行/支付宝/微信" style="width: 35%;">
          <a-select-option value="现金">现金</a-select-option>
          <a-select-option value="银行">银行</a-select-option>
          <a-select-option value="支付宝">支付宝</a-select-option>
          <a-select-option value="微信">微信</a-select-option>
          <a-select-option value="其他">其他</a-select-option>
        </a-select>
        <br>

        <label>账户编码</label>
        <a-input v-model:value="formItems.corpBankCode" @blur="checkCode()" placeholder="账号编码（不允许重复）" />
        <label>第三方账号</label>
        <a-input v-model:value="formItems.accountNum" placeholder="第三方账号" />
        <br>

      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {onMounted, ref, unref} from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {findByBankCode, findByDatabaseUniqueCode} from "/@/api/record/system/bank";
import {findCodeKemuByBr} from "/@/api/record/system/bank-statement";
import {Select as ASelect,Input as AInput, Cascader as ACascader} from 'ant-design-vue'
import {useMessage} from "/@/hooks/web/useMessage";
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option

const {
  createErrorModal
} = useMessage()

const emit=defineEmits([])

const formItems:any = ref({})

let changeBeforeModel:any = {}

//查询银行科目
const codeKemu:any = ref({})
async function findKemu() {
  const res = await findCodeKemuByBr()
  codeKemu.value = res
  console.log(codeKemu.value)
}
onMounted(async () => {
  await findKemu()
})

const [register, { closeModal }] = useModalInner((data) => {
  // 方式2
  formItems.value.id = data.data.id
  formItems.value.corpBankCode = data.data.corpBankCode
  formItems.value.corpBankName = data.data.corpBankName
  formItems.value.corpBankType = data.data.corpBankType
  formItems.value.bankCode = data.data.bankCode
  formItems.value.ccode = data.data.ccode
  formItems.value.accountNum = data.data.accountNum
  formItems.value.icash = data.data.icash
  formItems.value.ibank = data.data.ibank
  formItems.value.ibankStatement = data.data.ibankStatement

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged:boolean = false
async function handleOk() {
  isChanged = !(formItems.value.corpBankCode == changeBeforeModel.corpBankCode
    && formItems.value.corpBankName == changeBeforeModel.corpBankName
    && formItems.value.corpBankType == changeBeforeModel.corpBankType
    && formItems.value.bankCode == changeBeforeModel.bankCode
    && formItems.value.ccode == changeBeforeModel.ccode
    && formItems.value.accountNum == changeBeforeModel.accountNum
    && formItems.value.icash == changeBeforeModel.icash
    && formItems.value.ibank == changeBeforeModel.ibank
    && formItems.value.ibankStatement == changeBeforeModel.ibankStatement)
  if (formItems.value.corpBankName==undefined || formItems.value.corpBankName=='') {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '请输入账户名称！'
    })
    return false
  }
  if (formItems.value.corpBankCode==undefined || formItems.value.corpBankCode=='') {
    // alert('请输入银行编码！')
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '请输入账户编码！'
    })
    return false
  }
  if(isChanged){
    emit('save', unref(formItems))
    closeModal()
    return true
  }
  closeModal()
  // alert("没有改变过")
  return false
}

async function checkCode(){
  if((changeBeforeModel.bankCode!=undefined && changeBeforeModel.bankCode!='') || changeBeforeModel.bankCode==formItems.value.bankCode){
    return true
  }
  if(formItems.value.bankCode == ''){
    return false
  }
  const res = await findByBankCode(formItems.value.bankCode)
  if(res.length!=0){
    // alert('银行编码已存在，请重新输入！')
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '银行编码已存在，请重新输入！'
    })
    formItems.value.bankCode = ''
    console.log(false)
    return false
  }
  return true
}

</script>
<style lang="less" scoped>
:deep(.ant-input),:deep(.ant-calendar-picker-input.ant-input),:deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input){
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
}
:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none !important;
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

.nc-open-content {
  input {
    width: 35%;
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
    text-align: right;
    width: 110px;
    display: inline-block;
    padding: 5px 10px;
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
