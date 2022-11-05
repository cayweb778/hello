<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="银行支票"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">

        <label>支票编号</label>
        <a-input v-model:value="formItems.checkNum" placeholder="支票编号" />
        <label>支票银行</label>
        <a-input v-model:value="formItems.checkName" placeholder="支票银行" />
        <br>

        <label>出票日期</label>
        <a-date-picker v-model:value="formItems.checkDate" placeholder="出票日期" format="YYYY-MM-DD" style="width: 35%;" />
        <label>支票类型</label>
        <a-select v-model:value="formItems.checkType" placeholder="支票类型" style="width: 35%;">
          <a-select-option value="现金">现金</a-select-option>
          <a-select-option value="转账">转账</a-select-option>
          <a-select-option value="旅行">旅行</a-select-option>
          <a-select-option value="划线">划线</a-select-option>
          <a-select-option value="银行">银行</a-select-option>
          <a-select-option value="其他">其他</a-select-option>
        </a-select>
        <br>

        <label>付款行名称</label>
        <a-input v-model:value="formItems.payName" placeholder="付款行名称" />
        <label>出票人账号</label>
        <a-input v-model:value="formItems.payAccount" placeholder="出票人账号" />
        <br>

        <label>收款人全称</label>
        <a-input v-model:value="formItems.payeeName" placeholder="收款人全称" />
        <label>金额</label>
        <a-input type="number" v-model:value="formItems.amount" placeholder="金额" />
        <br>

        <label>用途</label>
        <a-input v-model:value="formItems.purpose" placeholder="用途" />
        <label>密押代码</label>
        <a-input v-model:value="formItems.encryCode" placeholder="密押代码" />
        <br>

      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'

const emit=defineEmits([])

const formItems:any = ref({})

let changeBeforeModel:any = {}

const [register, { closeModal }] = useModalInner((data) => {
  // 方式2
  formItems.value.id = data.data.id
  formItems.value.qrCode = data.data.qrCode
  formItems.value.checkDate = data.data.checkDate
  formItems.value.checkType = data.data.checkType
  formItems.value.checkNum = data.data.checkNum
  formItems.value.checkName = data.data.checkName
  formItems.value.payName = data.data.payName
  formItems.value.payAccount = data.data.payAccount
  formItems.value.payeeName = data.data.payeeName
  formItems.value.purpose = data.data.purpose
  formItems.value.amount = data.data.amount
  formItems.value.encryCode = data.data.encryCode
  formItems.value.voucherNumber = data.data.voucherNumber

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged:boolean = false
async function handleOk() {
  isChanged = !(formItems.value.qrCode == changeBeforeModel.qrCode
    && formItems.value.checkDate == changeBeforeModel.checkDate
    && formItems.value.checkType == changeBeforeModel.checkType
    && formItems.value.checkNum == changeBeforeModel.checkNum
    && formItems.value.checkName == changeBeforeModel.checkName
    && formItems.value.payName == changeBeforeModel.payName
    && formItems.value.payAccount == changeBeforeModel.payAccount
    && formItems.value.payeeName == changeBeforeModel.payeeName
    && formItems.value.purpose == changeBeforeModel.purpose
    && formItems.value.amount == changeBeforeModel.amount
    && formItems.value.encryCode == changeBeforeModel.encryCode
    && formItems.value.voucherNumber == changeBeforeModel.voucherNumber)
  if(isChanged){
    emit('save', unref(formItems))
    closeModal()
    return true
  }
  closeModal()
  // alert("没有改变过")
  return false
}

</script>
<style lang="less" scoped>
:deep(.ant-calendar-picker-input.ant-input),:deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input){
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
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
