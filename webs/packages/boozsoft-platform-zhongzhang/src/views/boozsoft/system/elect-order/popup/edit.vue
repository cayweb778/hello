<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="电子回单"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">

        <label>交易流水号</label>
        <a-input v-model:value="formItems.serialNumber" placeholder="交易流水号" />
        <label>回单日期</label>
        <a-date-picker v-model:value="formItems.electOderDate" placeholder="回单日期" format="YYYY-MM-DD" style="width: 35%;" />
        <br>

        <label>回单类型</label>
        <a-select v-model:value="formItems.electOderType" placeholder="回单类型" style="width: 35%;">
          <a-select-option value="收款">收款</a-select-option>
          <a-select-option value="付款">付款</a-select-option>
          <a-select-option value="费用">费用</a-select-option>
          <a-select-option value="税款">税款</a-select-option>
          <a-select-option value="社保">社保</a-select-option>
          <a-select-option value="其他">其他</a-select-option>
        </a-select>
        <label>回单样式</label>
        <a-select v-model:value="formItems.electOderStyle" placeholder="回单样式" style="width: 35%;">
          <a-select-option value="电子">电子</a-select-option>
          <a-select-option value="纸质">纸质</a-select-option>
        </a-select>
        <br>

        <label>交易类型</label>
        <a-select v-model:value="formItems.electOderTrans" placeholder="交易类型" style="width: 35%;">
          <a-select-option value="收入">收入</a-select-option>
          <a-select-option value="支出">支出</a-select-option>
        </a-select>
        <label>币种</label>
        <a-select v-model:value="formItems.currency" placeholder="币种" style="width: 35%;">
          <a-select-option v-for="currency in currencyList" :key="currency.currencyZhCnName" :value="currency.uniqueCode">
            {{ currency.countryName }}-{{ currency.currencyZhCnName }}-{{ currency.currencySymbol }}
          </a-select-option>
        </a-select>
        <br>

        <label>金额</label>
        <a-input type="number" v-model:value="formItems.amount" placeholder="金额" />
        <label>手续费</label>
        <a-input type="number" v-model:value="formItems.commission" placeholder="手续费" />
        <br/>

        <label>凭证号</label>
        <a-input v-model:value="formItems.transId" placeholder="凭证号" />
        <label>付款人全称</label>
        <a-input v-model:value="formItems.payName" placeholder="付款人全称" />
        <br>

        <label>付款人账号</label>
        <a-input v-model:value="formItems.payAccount" placeholder="付款人账号" />
        <label>付款人开户行</label>
        <a-input v-model:value="formItems.payBank" placeholder="付款人开户行" />
        <br>

        <label>收款人全称</label>
        <a-input v-model:value="formItems.payeeName" placeholder="收款人全称" />
        <label>收款人账号</label>
        <a-input v-model:value="formItems.payeeAccount" placeholder="收款人账号" />
        <br>

        <label>收款人开户行</label>
        <a-input v-model:value="formItems.payeeBank" placeholder="收款人开户行" />
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
import { findAll } from '/@/api/record/system/currency'

const emit=defineEmits([])

const formItems:any = ref({})

const currencyList:any = ref([])

let changeBeforeModel:any = {}

const [register, { closeModal }] = useModalInner((data) => {
  // 方式2
  formItems.value.id = data.data.id
  formItems.value.electOderDate = data.data.electOderDate
  formItems.value.electOderType = data.data.electOderType
  formItems.value.electOderStyle = data.data.electOderStyle
  formItems.value.electOderTrans = data.data.electOderTrans
  formItems.value.electClass = data.data.electClass
  formItems.value.serialNumber = data.data.serialNumber
  formItems.value.currency = data.data.currency
  formItems.value.amount = data.data.amount
  formItems.value.commission = data.data.commission
  formItems.value.transId = data.data.transId
  formItems.value.payName = data.data.payName
  formItems.value.payAccount = data.data.payAccount
  formItems.value.payBank = data.data.payBank
  formItems.value.payeeName = data.data.payeeName
  formItems.value.payeeAccount = data.data.payeeAccount
  formItems.value.payeeBank = data.data.payeeBank
  formItems.value.postscriptSummary = data.data.postscriptSummary
  formItems.value.voucherNumber = data.data.voucherNumber

  //币种信息
  currencyList.value = []
  findAll().then(res=>{
    currencyList.value = res.items
  })

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged:boolean = false
async function handleOk() {
  isChanged = !(formItems.value.electOderDate == changeBeforeModel.electOderDate
    && formItems.value.electOderType == changeBeforeModel.electOderType
    && formItems.value.electOderStyle == changeBeforeModel.electOderStyle
    && formItems.value.electOderTrans == changeBeforeModel.electOderTrans
    && formItems.value.electClass == changeBeforeModel.electClass
    && formItems.value.serialNumber == changeBeforeModel.serialNumber
    && formItems.value.currency == changeBeforeModel.currency
    && formItems.value.amount == changeBeforeModel.amount
    && formItems.value.commission == changeBeforeModel.commission
    && formItems.value.transId == changeBeforeModel.transId
    && formItems.value.payName == changeBeforeModel.payName
    && formItems.value.payAccount == changeBeforeModel.payAccount
    && formItems.value.payBank == changeBeforeModel.payBank
    && formItems.value.payeeName == changeBeforeModel.payeeName
    && formItems.value.payeeAccount == changeBeforeModel.payeeAccount
    && formItems.value.payeeBank == changeBeforeModel.payeeBank
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
