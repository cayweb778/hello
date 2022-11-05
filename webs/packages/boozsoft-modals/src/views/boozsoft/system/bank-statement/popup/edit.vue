<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="银行对账单"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">

        <label>日期</label>
        <a-date-picker v-model:value="formItems.statementDate" placeholder="对账日期" format="YYYY-MM-DD" style="width: 35%;" />
        <label>会计科目</label>
        <a-input :disabled="true" v-model:value="formItems.kemuCode" placeholder="会计科目" />
        <br>

        <label>结算方式</label>
        <a-input v-model:value="formItems.settModes" placeholder="结算方式" />
        <label>票号</label>
        <a-input v-model:value="formItems.piaohao" placeholder="票号" />
        <br>

        <label>借方金额</label>
        <a-input type="number" v-model:value="formItems.jie" placeholder="借方金额" />
        <label>贷方金额</label>
        <a-input type="number" v-model:value="formItems.dai" placeholder="贷方金额" />
        <br>

        <label>币种</label>
        <a-select v-model:value="formItems.currencyId" placeholder="币种" style="width: 35%;">
          <a-select-option v-for="currency in currencyList" :key="currency.currencyZhCnName" :value="currency.uniqueCode">
            {{ currency.countryName }}-{{ currency.currencyZhCnName }}-{{ currency.currencySymbol }}
          </a-select-option>
        </a-select>
        <label>摘要</label>
        <a-input v-model:value="formItems.remarks" placeholder="摘要" />
        <br>

      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {findAll} from "/@/api/record/system/currency";

const emit=defineEmits([])

const formItems:any = ref({})

const currencyList:any = ref([])

let changeBeforeModel:any = {}

const [register, { closeModal }] = useModalInner((data) => {
  //币种信息
  currencyList.value = []
  findAll().then(res=>{
    currencyList.value = res.items
  })

  // 方式2
  formItems.value.id = data.data.id
  formItems.value.statementDate = data.data.statementDate
  formItems.value.kemuCode = data.data.kemuCode
  formItems.value.duifangUnit = data.data.duifangUnit
  formItems.value.settModes = data.data.settModes
  formItems.value.piaohao = data.data.piaohao
  formItems.value.jie = data.data.jie
  formItems.value.dai = data.data.dai
  formItems.value.fangxiang = data.data.fangxiang
  formItems.value.flag = data.data.flag
  formItems.value.statementNum = data.data.statementNum
  formItems.value.remarks = data.data.remarks
  formItems.value.currencyId = data.data.currencyId

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged:boolean = false
async function handleOk() {
  isChanged = !(formItems.value.statementDate == changeBeforeModel.statementDate
    && formItems.value.kemuCode == changeBeforeModel.kemuCode
    && formItems.value.yewuType == changeBeforeModel.yewuType
    && formItems.value.piaohao == changeBeforeModel.piaohao
    && formItems.value.jie == changeBeforeModel.jie
    && formItems.value.dai == changeBeforeModel.dai
    && formItems.value.fangxiang == changeBeforeModel.fangxiang
    && formItems.value.flag == changeBeforeModel.flag
    && formItems.value.statementNum == changeBeforeModel.statementNum
    && formItems.value.remarks == changeBeforeModel.remarks
    && formItems.value.currencyId == changeBeforeModel.currencyId)
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
