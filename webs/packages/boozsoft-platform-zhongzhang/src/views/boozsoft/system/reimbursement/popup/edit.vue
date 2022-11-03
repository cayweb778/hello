<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="报销单"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">

        <label>单据编码</label>
        <a-input v-model:value="formItems.reimCode" @blur="checkCode()" placeholder="单据编码" />
        <label>单据名称</label>
        <a-input v-model:value="formItems.reimName" placeholder="单据名称"/>
        <br>

        <label>部门</label>
        <a-input v-model:value="formItems.deptCode" placeholder="部门" />
        <label>员工</label>
        <a-input v-model:value="formItems.psnCode" placeholder="员工" />
        <br>

        <label>项目大类</label>
        <a-input v-model:value="formItems.projectCate" placeholder="项目大类" />
        <label>所属项目</label>
        <a-input v-model:value="formItems.projectCode" placeholder="所属项目" />
        <br>

        <label>单据日期</label>
        <a-date-picker v-model:value="formItems.reimDate" placeholder="单据日期" style="width: 35%" />
        <label>核算单位</label>
        <a-input v-model:value="formItems.corp" placeholder="核算单位" />
        <br>

        <label>费用名称</label>
        <a-input v-model:value="formItems.costName" placeholder="费用名称" />
        <label>费用金额</label>
        <a-input type="number" v-model:value="formItems.amount" placeholder="费用金额" />
        <br>

        <label>费用说明</label>
        <a-input v-model:value="formItems.costExplain" placeholder="费用说明" />
        <br>

      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {findByReimCode} from "/@/api/record/system/reimbursement";

const emit=defineEmits([])

const formItems:any = ref({})
let changeBeforeModel:any = {}
const [register, { closeModal }] = useModalInner((data) => {
  // 方式2
  formItems.value.id = data.data.id
  formItems.value.reimCode = data.data.reimCode
  formItems.value.reimName = data.data.reimName
  formItems.value.reimClass = data.data.reimClass
  formItems.value.reimDate = data.data.reimDate
  formItems.value.psnCode = data.data.psnCode
  formItems.value.deptCode = data.data.deptCode
  formItems.value.projectCate = data.data.projectCate
  formItems.value.projectCode = data.data.projectCode
  formItems.value.corp = data.data.corp
  formItems.value.costName = data.data.costName
  formItems.value.amount = data.data.amount
  formItems.value.costExplain = data.data.costExplain
  formItems.value.vouchCode = data.data.vouchCode

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged:boolean = false
async function handleOk() {
  isChanged = !(formItems.value.reimCode == changeBeforeModel.reimCode
    && formItems.value.reimName == changeBeforeModel.reimName
    && formItems.value.reimClass == changeBeforeModel.reimClass
    && formItems.value.reimDate == changeBeforeModel.reimDate
    && formItems.value.psnCode == changeBeforeModel.psnCode
    && formItems.value.deptCode == changeBeforeModel.deptCode
    && formItems.value.projectCate == changeBeforeModel.projectCate
    && formItems.value.projectCode == changeBeforeModel.projectCode
    && formItems.value.corp == changeBeforeModel.corp
    && formItems.value.costName == changeBeforeModel.costName
    && formItems.value.amount == changeBeforeModel.amount
    && formItems.value.costExplain == changeBeforeModel.costExplain
    && formItems.value.vouchCode == changeBeforeModel.vouchCode)
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
  if(changeBeforeModel.reimCode!=undefined || changeBeforeModel.reimCode==formItems.value.reimCode){
    return true
  }
  if(formItems.value.reimCode == ''){
    return false
  }
  const res = await findByReimCode(formItems.value.reimCode)
  if(res.length!=0){
    alert('单据编码已存在，请重新输入！')
    formItems.value.reimCode = ''
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
