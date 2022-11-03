<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="结算方式"
    @ok="handleOk()"
    @register="register"
    :canFullscreen="false"
  >
    <template #title>
      <div style="display: flex;margin-top: 10px;margin-left: 10px;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='0'">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;结算方式
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='1'">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;结算方式
        </span>
      </div>
    </template>
    <div class="nc-open-content" >
      <div class="open-content-up" style="text-align: center;margin-top: 30px;">

        <label>结算方式编码:</label>
        <a-input v-model:value="formItems.settModesCode" :disabled="isState!='0'" @blur="checkCode()" placeholder="结算方式编码" />
        <span class="red_span">*</span>

        <br/><br/><br/>
        <label style="font-size: 18px;margin-left: 0;width:130px;">结算方式名称：</label>
        <a-input v-model:value="formItems.settModesName" placeholder="结算方式名称" class="abc" style="width: 60%;" />
        <span class="red_span">*</span>

      </div>
    </div>

    <template #footer>
      <div v-if="isState=='0'">
        <a-button @click="closeModal()">取消</a-button>
        <a-button @click="handleOk()" :disabled="saveClick">保存</a-button>
        <a-button @click="handleOkAdd()" :disabled="saveClick" type="primary">保存并新增</a-button>
      </div>
      <div v-if="isState=='1'">
        <a-button @click="closeModal()">放弃</a-button>
        <a-button @click="handleOk()" :disabled="saveClick" type="primary">保存</a-button>
      </div>
    </template>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {findBySettModes} from "/@/api/record/system/sett-modes";
import {Select as ASelect,Input as AInput} from "ant-design-vue"
import {useMessage} from "/@/hooks/web/useMessage";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {
  PlusCircleOutlined,
  FormOutlined,
  FileSearchOutlined
} from '@ant-design/icons-vue'

const {
  createErrorModal
} = useMessage()

const emit=defineEmits(['register','save'])

const formItems:any = ref({})

let changeBeforeModel:any = {}

const isState = ref('0')
const saveClick:any = ref(false)

const dynamicTenantId = ref()
const [register, { closeModal }] = useModalInner((data) => {
  saveClick.value=false
  dynamicTenantId.value = data.dynamicTenantId
  isState.value = data.isState
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
  saveClick.value=true
  isChanged = !(formItems.value.settModesCode == changeBeforeModel.settModesCode
    && formItems.value.settModesName == changeBeforeModel.settModesName)
  if (formItems.value.settModesCode==undefined || formItems.value.settModesCode=='') {
    // alert('请输入结算方式编码！')
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '请输入结算方式编码！'
    })
    saveClick.value=false
    return false
  }
  if (formItems.value.settModesName==undefined || formItems.value.settModesName=='') {
    // alert('请输入结算方式名称！')
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '请输入结算方式名称！'
    })
    saveClick.value=false
    return false
  }
  if(isChanged){
    emit('save', unref(formItems))
    closeModal()
    saveClick.value=false
    return true
  }
  closeModal()
  // alert("没有改变过")
  saveClick.value=false
  return false
}

async function handleOkAdd() {
  saveClick.value=true
  isChanged = !(formItems.value.settModesCode == changeBeforeModel.settModesCode
    && formItems.value.settModesName == changeBeforeModel.settModesName)
  if (formItems.value.settModesCode==undefined || formItems.value.settModesCode=='') {
    // alert('请输入结算方式编码！')
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '请输入结算方式编码！'
    })
    saveClick.value=false
    return false
  }
  if (formItems.value.settModesName==undefined || formItems.value.settModesName=='') {
    // alert('请输入结算方式名称！')
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '请输入结算方式名称！'
    })
    saveClick.value=false
    return false
  }
  if(isChanged){
    emit('save', unref(formItems))
    formItems.value = {}
    changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
    // closeModal()
    saveClick.value=false
    return true
  }
  // closeModal()
  // alert("没有改变过")
  saveClick.value=false
  return false
}

async function checkCode(){
  if((changeBeforeModel.settModesCode!=undefined && changeBeforeModel.settModesCode!='') || changeBeforeModel.settModesCode==formItems.value.settModesCode){
    return false
  }
  if(formItems.value.settModesCode == ''){
    return false
  }
  const res = await useRouteApi(findBySettModes,{schemaName: dynamicTenantId})(formItems.value.settModesCode)
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
