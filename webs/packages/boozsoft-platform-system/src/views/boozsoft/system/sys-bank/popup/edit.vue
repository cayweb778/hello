<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="银行档案"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">

        <label>银行名称</label>
        <a-input v-model:value="formItems.bankName" placeholder="请输入银行名称" style="font-size: 18px" />
        <label>银行账号</label>
        <a-input v-model:value="formItems.bankAccount" placeholder="请输入银行账号" style="font-size: 18px" />
        <br>

        <label>银行编码</label>
        <a-input v-model:value="formItems.bankCode" @blur="checkCode()" placeholder="银行编码（不允许重复）" />
        <label>币种</label>
        <a-select v-model:value="formItems.currency" placeholder="币种" style="width: 35%;">
          <a-select-option value="1">单币种</a-select-option>
          <a-select-option value="2">多币种</a-select-option>
        </a-select>
        <br>

        <label>开户地</label>
        <a-input v-model:value="formItems.bankArea" placeholder="开户地" />
        <label>行号</label>
        <a-input v-model:value="formItems.bankNum" placeholder="行号" />
        <br>

        <label>对应会计科目</label>
        <a-select v-model:value="formItems.kemuCode" @change="checkKemu()" placeholder="对应会计科目（不允许重复）" style="width: 35%;">
          <a-select-option v-for="item in codeKemu" :key="item.ccode" :value="item.ccode">
            {{ item.ccode }}-{{ item.ccodeName }}
          </a-select-option>
        </a-select>

      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {onMounted, ref, unref} from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {findByBankCode, findByDatabaseUniqueCode} from "/@/api/record/system/bank";
import {findCodeKemuByBr} from "/@/api/record/system/bank-statement";

const emit=defineEmits([])

const formItems:any = ref({})

let changeBeforeModel:any = {}

//查询集团管控状态
const dist:any = ref([])
async function findDatabase() {
  const res = await findByDatabaseUniqueCode()
  dist.value = res
}
//查询银行科目
const codeKemu:any = ref({})
async function findKemu() {
  const res = await findCodeKemuByBr()
  codeKemu.value = res
  console.log(codeKemu.value)
}
onMounted(async () => {
  await findDatabase()
  await findKemu()
})

const [register, { closeModal }] = useModalInner((data) => {
  // 方式2
  formItems.value.id = data.data.id
  formItems.value.bankCode = data.data.bankCode
  formItems.value.bankName = data.data.bankName
  formItems.value.bankAccount = data.data.bankAccount
  formItems.value.currency = data.data.currency
  formItems.value.bankArea = data.data.bankArea
  formItems.value.bankNum = data.data.bankNum
  formItems.value.kemuCode = data.data.kemuCode
  formItems.value.successState = data.data.successState
  formItems.value.applyDatabaseUniqueCode = data.data.applyDatabaseUniqueCode
  formItems.value.applyUser = data.data.applyUser
  formItems.value.applyDate = data.data.applyDate
  formItems.value.approveUser = data.data.approveUser
  formItems.value.approveDate = data.data.approveDate
  formItems.value.biandongMethod = data.data.biandongMethod

  formItems.value.isControl = dist.isControl
  formItems.value.isKeyword = dist.isKeyword
  formItems.value.isOther = dist.isOther
  formItems.value.isAuto = dist.isAuto

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged:boolean = false
async function handleOk() {
  isChanged = !(formItems.value.bankCode == changeBeforeModel.bankCode
    && formItems.value.bankName == changeBeforeModel.bankName
    && formItems.value.bankAccount == changeBeforeModel.bankAccount
    && formItems.value.currency == changeBeforeModel.currency
    && formItems.value.bankArea == changeBeforeModel.bankArea
    && formItems.value.bankNum == changeBeforeModel.bankNum
    && formItems.value.kemuCode == changeBeforeModel.kemuCode
    && formItems.value.successState == changeBeforeModel.successState
    && formItems.value.applyDatabaseUniqueCode == changeBeforeModel.applyDatabaseUniqueCode
    && formItems.value.applyUser == changeBeforeModel.applyUser
    && formItems.value.applyDate == changeBeforeModel.applyDate
    && formItems.value.approveUser == changeBeforeModel.approveUser
    && formItems.value.approveDate == changeBeforeModel.approveDate
    && formItems.value.biandongMethod == changeBeforeModel.biandongMethod)
  if (formItems.value.bankName==undefined || formItems.value.bankName=='') {
    alert('请输入银行名称！')
    return false
  }
  if (formItems.value.bankCode==undefined || formItems.value.bankCode=='') {
    alert('请输入银行编码！')
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
    alert('银行编码已存在，请重新输入！')
    formItems.value.bankCode = ''
    console.log(false)
    return false
  }
  return true
}

async function checkKemu(){
  if((changeBeforeModel.kemuCode!=undefined && changeBeforeModel.kemuCode!='') || changeBeforeModel.bankCode==formItems.value.kemuCode){
    return true
  }
  if(formItems.value.kemuCode == ''){
    return false
  }
  const res = await findByKemuCode(formItems.value.kemuCode)
  if(res.length!=0){
    alert('银行科目已存在，请重新输入！')
    formItems.value.bankCode = ''
    console.log(false)
    return false
  }
  return true
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
