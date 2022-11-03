<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="资产组"
    @ok="handleOk()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;">
          <PlusCircleOutlined v-if="!isEdit" style="font-size: 34px;font-weight: bold"/>
          <FormOutlined v-if="isEdit" style="font-size: 34px;font-weight: bold;"/> &nbsp;&nbsp;资产组
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="../../../../../assets/images/012.png" style="height:80px;margin-right: 10px;"/>
      </div>
    </template>
    <div
      class="nc-open-content"
    >
      <div class="open-content-up" style="text-align: center;margin-top: 30px;">

        <label style="font-size: 18px;margin-left: 0;">组名称：</label>
        <a-input v-model:value="formItems.bsName" placeholder="资产组名称" class="abc" style="width: 65%;" />
        <span class="red_span">*</span>
        <br/><br/><br/>

        <label>组编码:</label>
        <a-input v-model:value="formItems.bsCode" @blur="checkCode()" placeholder="资产组编码" />

      </div>
    </div>
    <template #footer>
      <a-button @click="closeModal()">取消</a-button>
      <a-button v-if="isEdit" @click="handleOk(false)" type="primary" :disabled="saveClick">确定</a-button>
      <a-button v-if="!isEdit" @click="handleOk(false)" :disabled="saveClick">保存</a-button>
      <a-button v-if="!isEdit" @click="handleOk(true)" :disabled="saveClick" type="primary">保存并新增</a-button>
    </template>
  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {findBySettModes,findByBsName,editFlag} from "/@/api/record/group/fa-asset-group";
import {Select as ASelect,Input as AInput} from "ant-design-vue"
import {useMessage} from "/@/hooks/web/useMessage";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {
  PlusCircleOutlined,
  FormOutlined,
  CaretDownOutlined
} from '@ant-design/icons-vue'
const {
  createErrorModal
} = useMessage()

const emit=defineEmits(['register','save'])

const formItems:any = ref({})

let changeBeforeModel:any = {}
const isEdit:any = ref(false)

const database:any = ref({})
const [register, { closeModal }] = useModalInner((data) => {
  // 方式2
  formItems.value = {
    bsCode: data.data.bsCode,
    bsName: data.data.bsName,
  }
  database.value = data.data.database
  if(data.data.id){
    formItems.value.id = data.data.id
    isEdit.value = true

  }
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged:boolean = false
async function handleOk(fls) {
  isChanged = !(formItems.value.bsCode == changeBeforeModel.bsCode
    && formItems.value.bsName == changeBeforeModel.bsName)
  if (formItems.value.bsCode==undefined || formItems.value.bsCode=='') {
    // alert('请输入资产组编码！')
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '请输入资产组编码！'
    })
    return false
  }
  if (formItems.value.bsName==undefined || formItems.value.bsName=='') {
    // alert('请输入资产组名称！')
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '请输入资产组名称！'
    })
    return false
  }

  if(isChanged){
    if(fls === true){
      emit('save', unref(formItems))
      formItems.value = {}
      changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
    }else{
      emit('save', unref(formItems))
      closeModal()
    }
    return true
  }
}

async function checkCode(){
  if((changeBeforeModel.bsCode!=undefined && changeBeforeModel.bsCode!='') || changeBeforeModel.bsCode==formItems.value.bsCode){
    return false
  }
  if(formItems.value.bsCode == ''){
    return false
  }
  console.log(database.value)
  const res = await useRouteApi(findBySettModes,{schemaName: database})(formItems.value.bsCode)
  console.log(res)
  if(res.length!=0){
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '资产组编码已存在，请重新输入！'
    })
    // alert('资产组编码已存在，请重新输入！')
    formItems.value.bsCode = ''
    console.log(false)
    return false
  }
  return true
}
async function checkName(){
  if((changeBeforeModel.bsName!=undefined && changeBeforeModel.bsName!='') || changeBeforeModel.bsName==formItems.value.bsName){
    return false
  }
  if(formItems.value.bsName == ''){
    return false
  }
  console.log(database.value)
  const res2 = await useRouteApi(findByBsName,{schemaName: database})(formItems.value.bsName)
  if(res2.length!=0){
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '资产组名称已存在，请重新输入！'
    })
    // alert('资产组编码已存在，请重新输入！')
    formItems.value.bsCode = ''
    return false
  }
  return true
}
</script>
<style>
.vben-basic-title {
  color: #0096c7 !important;
  border:none !important;
}

.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
  border-top: none !important;
}
.ant-modal-header{
  border: none !important;
}
</style>
<style lang="less" scoped>
:deep(.vben-basic-title) {
  color: #0096c7 !important;
}
.ant-modal-header{
  border: none !important;
}


:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none !important;
}

.vben-basic-title {
  color: #0096c7 !important;
  border: none !important;
}

.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
  border-top: none !important;
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
    width: 120px;
    display: inline-block;
    padding-top: 5px;
    padding-left: 5px;
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
