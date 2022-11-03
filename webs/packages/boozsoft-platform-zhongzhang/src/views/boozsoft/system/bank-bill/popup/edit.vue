<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="银行汇票"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">

        <label>汇票编号</label>
        <a-input v-model:value="formItems.billNum" placeholder="汇票编号" />
        <label>出票人全称</label>
        <a-input v-model:value="formItems.billName" placeholder="出票人全称" />
        <br>

        <label>出票日期</label>
        <a-date-picker v-model:value="formItems.billDate" placeholder="出票日期" format="YYYY-MM-DD" style="width: 35%;" />
        <label>汇票类型</label>
        <a-select v-model:value="formItems.billType" placeholder="汇票类型" style="width: 35%;">
          <a-select-option value="银行承兑">银行承兑</a-select-option>
          <a-select-option value="商业承兑">商业承兑</a-select-option>
        </a-select>
        <br>

        <label>出票人账号</label>
        <a-input v-model:value="formItems.billAccount" placeholder="出票人账号" />
        <label>付款行全称</label>
        <a-input v-model:value="formItems.bankName" placeholder="付款行全称" />
        <br>

        <label>出票金额</label>
        <a-input type="number" v-model:value="formItems.amount" placeholder="出票金额" />
        <label>汇票到期日</label>
        <a-date-picker v-model:value="formItems.dueDate" placeholder="汇票到期日" format="YYYY-MM-DD" style="width: 35%;" />
        <br>

        <label>承兑协议编号</label>
        <a-input v-model:value="formItems.transId" placeholder="承兑协议编号" />
        <label>付款行行号</label>
        <a-input v-model:value="formItems.payBankCode" placeholder="付款行行号" />
        <br>

        <label>付款人地址</label>
        <a-input v-model:value="formItems.payAddr" placeholder="付款人地址" />
        <label>付款人开户行</label>
        <a-input v-model:value="formItems.payBank" placeholder="付款人开户行" />
        <br>

        <label>付款人全称</label>
        <a-input v-model:value="formItems.payeeName" placeholder="付款人全称" />
        <label>付款人账号</label>
        <a-input v-model:value="formItems.payeeAccount" placeholder="付款人账号" />
        <br>

        <label>付款人开户行</label>
        <a-input v-model:value="formItems.payeeBank" placeholder="付款人开户行" />
        <label>密押代码</label>
        <a-input v-model:value="formItems.encryCode" placeholder="密押代码" />
        <br>

        <label>附言摘要</label>
        <a-input v-model:value="formItems.postscriptSummary" placeholder="附言摘要" />
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
  formItems.value.billDate = data.data.billDate
  formItems.value.billType = data.data.billType
  formItems.value.billNum = data.data.billNum
  formItems.value.billName = data.data.billName
  formItems.value.billClass = data.data.billClass
  formItems.value.billAccount = data.data.billAccount
  formItems.value.bankName = data.data.bankName
  formItems.value.amount = data.data.amount
  formItems.value.dueDate = data.data.dueDate
  formItems.value.transId = data.data.transId
  formItems.value.payBankCode = data.data.payBankCode
  formItems.value.payAddr = data.data.payAddr
  formItems.value.payBank = data.data.payBank
  formItems.value.payeeName = data.data.payeeName
  formItems.value.payeeAccount = data.data.payeeAccount
  formItems.value.payeeBank = data.data.payeeBank
  formItems.value.encryCode = data.data.encryCode
  formItems.value.postscriptSummary = data.data.postscriptSummary
  formItems.value.voucherNumber = data.data.voucherNumber

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged:boolean = false
async function handleOk() {
  isChanged = !(formItems.value.qrCode == changeBeforeModel.qrCode
    && formItems.value.billDate == changeBeforeModel.billDate
    && formItems.value.billType == changeBeforeModel.billType
    && formItems.value.billNum == changeBeforeModel.billNum
    && formItems.value.billName == changeBeforeModel.billName
    && formItems.value.billClass == changeBeforeModel.billClass
    && formItems.value.billAccount == changeBeforeModel.billAccount
    && formItems.value.bankName == changeBeforeModel.bankName
    && formItems.value.amount == changeBeforeModel.amount
    && formItems.value.dueDate == changeBeforeModel.dueDate
    && formItems.value.transId == changeBeforeModel.transId
    && formItems.value.payBankCode == changeBeforeModel.payBankCode
    && formItems.value.payAddr == changeBeforeModel.payAddr
    && formItems.value.payBank == changeBeforeModel.payBank
    && formItems.value.payeeName == changeBeforeModel.payeeName
    && formItems.value.payeeAccount == changeBeforeModel.payeeAccount
    && formItems.value.payeeBank == changeBeforeModel.payeeBank
    && formItems.value.encryCode == changeBeforeModel.encryCode
    && formItems.value.postscriptSummary == changeBeforeModel.postscriptSummary
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
