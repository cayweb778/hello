<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="结算方式"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
    >
      <div class="open-content-up" style="text-align: center;margin-top: 30px;">

        <label>结算方式编码:</label>
        <a-input v-model:value="formItems.settModesCode" @blur="checkCode()" placeholder="结算方式编码" />

        <br><br>
        <label>结算方式名称:</label>
        <a-input v-model:value="formItems.settModesName" placeholder="结算方式名称" />

      </div>
    </div>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {findBySettModes} from "/@/api/record/system/group-sett-modes";
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
    settModesCode: data.data.settModesCode,
    settModesName: data.data.settModesName,
  }

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged:boolean = false
async function handleOk() {
  isChanged = !(formItems.value.settModesCode == changeBeforeModel.settModesCode
    && formItems.value.settModesName == changeBeforeModel.settModesName)
  if (formItems.value.settModesCode==undefined || formItems.value.settModesCode=='') {
    // alert('请输入结算方式编码！')
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '请输入结算方式编码！'
    })
    return false
  }
  if (formItems.value.settModesName==undefined || formItems.value.settModesName=='') {
    // alert('请输入结算方式名称！')
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '请输入结算方式名称！'
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
  if((changeBeforeModel.settModesCode!=undefined && changeBeforeModel.settModesCode!='') || changeBeforeModel.settModesCode==formItems.value.settModesCode){
    return false
  }
  if(formItems.value.settModesCode == ''){
    return false
  }
  const res = await findBySettModes(formItems.value.settModesCode)
  if(res.length!=0){
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '结算方式编码已存在，请重新输入！'
    })
    // alert('结算方式编码已存在，请重新输入！')
    formItems.value.settModesCode = ''
    console.log(false)
    return false
  }
  return true
}
</script>
<style lang="less">
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
    width: 130px;
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
