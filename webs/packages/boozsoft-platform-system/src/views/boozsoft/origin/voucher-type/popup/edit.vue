<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="凭证类别"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
    >
      <div class="open-content-up" style="text-align: center;margin-top: 30px;">

        <label>编码：</label>
        <a-input v-model:value="formItems.voucherNum" @blur="checkNum()" placeholder="编码" />

        <br><br>
        <label>类别字简称：</label>
        <a-input v-model:value="formItems.voucherTypeCode" @blur="checkCode()" placeholder="类别字简称" />

        <br><br>
        <label>类别字名称：</label>
        <a-input v-model:value="formItems.voucherTypeName" @blur="checkName()" placeholder="类别字名称" />

      </div>
    </div>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {findVoucherTypeByCode, findVoucherTypeByName, findVoucherTypeByNum} from "/@/api/record/system/origin-voucher-type";
import {Select as ASelect,Input as AInput} from "ant-design-vue"
import {useMessage} from "/@/hooks/web/useMessage";

const {
  createErrorModal
} = useMessage()

const emit=defineEmits(['register','save'])

const formItems:any = ref({})

let changeBeforeModel:any = {}

const [register, { closeModal }] = useModalInner((data) => {
  // 方式2
  formItems.value = {
    id: data.data.id,
    voucherNum: data.data.voucherNum,
    voucherTypeCode: data.data.voucherTypeCode,
    voucherTypeName: data.data.voucherTypeName
  }

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged:boolean = false
async function handleOk() {
  isChanged = !(formItems.value.voucherNum == changeBeforeModel.voucherNum
    && formItems.value.voucherTypeCode == changeBeforeModel.voucherTypeCode
    && formItems.value.voucherTypeName == changeBeforeModel.voucherTypeName)
  if (formItems.value.voucherNum==undefined || formItems.value.voucherNum=='') {
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '请输入编码！'
    })
    // alert('请输入编码！')
    return false
  }
  if (formItems.value.voucherTypeCode==undefined || formItems.value.voucherTypeCode=='') {
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '请输入类别字简称！'
    })
    // alert('请输入类别字简称！')
    return false
  }
  if (formItems.value.voucherTypeName==undefined || formItems.value.voucherTypeName=='') {
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '请输类别字名称！'
    })
    // alert('请输类别字名称！')
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

async function checkNum(){
  if((changeBeforeModel.voucherNum!=undefined && changeBeforeModel.voucherNum!='') || changeBeforeModel.voucherNum==formItems.value.voucherNum){
    return false
  }
  if(formItems.value.voucherNum == ''){
    return false
  }
  const res = await findVoucherTypeByNum(formItems.value.voucherNum)
  if(res.length!=0){
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '编码已存在，请重新输入！'
    })
    // alert('编码已存在，请重新输入！')
    formItems.value.voucherNum = ''
    console.log(false)
    return false
  }
  return true
}

async function checkCode(){
  if((changeBeforeModel.voucherTypeCode!=undefined && changeBeforeModel.voucherTypeCode!='') || changeBeforeModel.voucherTypeCode==formItems.value.voucherTypeCode){
    return true
  }
  if(formItems.value.voucherTypeCode == ''){
    return false
  }
  const res = await findVoucherTypeByCode(formItems.value.voucherTypeCode)
  if(res.length!=0){
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '类别字简称已存在，请重新输入！'
    })
    // alert('类别字简称已存在，请重新输入！')
    formItems.value.voucherTypeCode = ''
    console.log(false)
    return false
  }
  return true
}
async function checkName(){
  if((changeBeforeModel.voucherTypeName!=undefined && changeBeforeModel.voucherTypeName!='') || changeBeforeModel.voucherTypeName==formItems.value.voucherTypeName){
    return true
  }
  if(formItems.value.voucherTypeName == ''){
    return false
  }
  const res = await findVoucherTypeByName(formItems.value.voucherTypeName)
  if(res.length!=0){
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '类别字名称已存在，请重新输入！'
    })
    // alert('类别字名称已存在，请重新输入！')
    formItems.value.voucherTypeName = ''
    console.log(false)
    return false
  }
  return true
}
</script>
<style scoped lang="less">
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
  input:not(.ant-select-selection-search-input,.ant-input) {
    width: 50%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  .ant-select-selector {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label {
    text-align: left;
    width: 110px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
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
</style>
