<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="预算来源"
    @ok="handleOk()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <img src="/@/assets/images/create.svg" style="width:25px;margin-right: 10px;"/>
        <span style="line-height: 25px;font-size: 16px;">预算来源</span>
      </div>
    </template>
    <div
      class="nc-open-content"
    >
      <div class="open-content-up" style="text-align: center;margin-top: 30px;">

        <label style="font-size: 18px;margin-left: 0;">来源名称：</label>
        <a-input v-model:value="formItems.bsName" placeholder="预算来源名称" class="abc" style="width: 65%;" />
        <span class="red_span">*</span>
        <br/><br/><br/>

        <label>来源编码:</label>
        <a-input v-model:value="formItems.bsCode" @blur="checkCode()" placeholder="预算来源编码" />

      </div>
    </div>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {findByBsName, findBySettModes} from "/@/api/record/group/group-budget-source";
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
    bsCode: data.data.bsCode,
    bsName: data.data.bsName,
  }
  if(data.data.id){
    formItems.value.id = data.data.id
  }
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged:boolean = false
async function handleOk() {
  isChanged = !(formItems.value.bsCode == changeBeforeModel.bsCode
    && formItems.value.bsName == changeBeforeModel.bsName)
  if (formItems.value.bsCode==undefined || formItems.value.bsCode=='') {
    // alert('请输入预算来源编码！')
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '请输入预算来源编码！'
    })
    return false
  }
  if (formItems.value.bsName==undefined || formItems.value.bsName=='') {
    // alert('请输入预算来源名称！')
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '请输入预算来源名称！'
    })
    return false
  }
  checkCode()
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
  if((changeBeforeModel.bsCode!=undefined && changeBeforeModel.bsCode!='') || changeBeforeModel.bsCode==formItems.value.bsCode){
    return false
  }
  if(formItems.value.bsCode == ''){
    return false
  }
  const res = await findBySettModes(formItems.value.bsCode)
  if(res.length!=0){
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '预算来源编码已存在，请重新输入！'
    })
    // alert('预算来源编码已存在，请重新输入！')
    formItems.value.bsCode = ''
    return false
  }
  const res2 = await findByBsName(formItems.value.bsName)
  if(res2.length!=0){
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '预算来源名称已存在，请重新输入！'
    })
    // alert('预算来源编码已存在，请重新输入！')
    formItems.value.bsCode = ''
    return false
  }
  return true
}

async function checkName(){
  if((changeBeforeModel.bsName!=undefined && changeBeforeModel.bsName!='') || changeBeforeModel.bsName==formItems.value.bsName){
    return false
  }
  if(formItems.value.bsCode == ''){
    return false
  }
  const res2 = await findByBsName(formItems.value.bsName)
  if(res2.length!=0){
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '预算来源名称已存在，请重新输入！'
    })
    // alert('预算来源编码已存在，请重新输入！')
    formItems.value.bsCode = ''
    return false
  }
  return true
}
</script>
<style lang="less" scoped>
:deep(.vben-basic-title) {
  color: rgb(1, 129, 226) !important;
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
