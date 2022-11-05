<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="人员分类"
    @ok="handleOk()"
    @register="register"
    :canFullscreen="false"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='0'">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;人员分类
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='1'">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;人员分类
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='2'">
          <FileSearchOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;人员分类
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 60px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/@/assets/images/person.png" style="height:100px;margin-right: 10px;"/>
      </div>
    </template>
    <div class="nc-open-content" >
      <div class="open-content-up" style="text-align: center;margin-top: 50px;">
        <label>分类编码:</label>
        <a-input v-model:value="formItems.psnTypeCode" placeholder="" />

        <br/><br/><br/>

        <label style="font-size: 18px;margin-left: 0;width:130px;">分类名称：</label>
        <a-input v-model:value="formItems.psnTypeName" placeholder="" class="abc" style="width: 60%;" />
        <span class="red_span">*</span>

      </div>
    </div>

    <template #footer>
      <div v-if="isState=='2'">
        <a-button @click="closeModal()" type="primary">关闭</a-button>
      </div>
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
import {Select as ASelect, Input as AInput, message} from "ant-design-vue"
import {
  PlusCircleOutlined,
  FormOutlined,
  FileSearchOutlined,
  CaretDownOutlined
} from '@ant-design/icons-vue'
import {useMessage} from "/@/hooks/web/useMessage";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {psnTypeFindAll,savePsnType} from "/@/api/record/system/origin-psn-type";

const {
  createErrorModal
} = useMessage()

const emit=defineEmits(['register','save'])

const formItems:any = ref({})

const isState = ref('0')
const saveClick:any = ref(false)

let changeBeforeModel:any = {}
const [register, { closeModal }] = useModalInner((data) => {
  saveClick.value=false
  isState.value = data.isState
  // 方式2
  formItems.value = {
    id: data.data.id,
    uniqueCode: data.data.uniqueCode,
    psnTypeCode: data.data.psnTypeCode,
    psnTypeName: data.data.psnTypeName,
    originId: data.data.originId
  }

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged:boolean = false
async function handleOk() {
  saveClick.value=true
  if(formItems.value.psnTypeCode==''|| formItems.value.psnTypeCode==null){
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '分类编码不能为空！'
    })
    saveClick.value=false
    return false
  }
  if(formItems.value.psnTypeName==''|| formItems.value.psnTypeName==null){
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '分类名称不能为空！'
    })
    saveClick.value=false
    return false
  }
  const res = await psnTypeFindAll(formItems.value.originId)
  const psnTypeList = res.items
  if (!formItems.value.psnTypeCode == changeBeforeModel.psnTypeCode && formItems.value.psnTypeCode!='' && formItems.value.psnTypeCode!=null){
    for (let i = 0; i < psnTypeList.length; i++) {
      const psnType = psnTypeList[i];
      if (psnType.psnTypeCode==formItems.value.psnTypeCode){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '分类编码重复不允许添加！'
        })
        saveClick.value=false
        return false
      }
    }
  }
  if (!formItems.value.psnTypeName == changeBeforeModel.psnTypeName && formItems.value.psnTypeName!='' && formItems.value.psnTypeName!=null){
    for (let i = 0; i < psnTypeList.length; i++) {
      const psnType = psnTypeList[i];
      if (psnType.psnTypeName==formItems.value.psnTypeName){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '分类名称重复不允许添加！'
        })
        saveClick.value=false
        return false
      }
    }
  }
  isChanged = !(formItems.value.uniqueCode == changeBeforeModel.uniqueCode
    && formItems.value.psnTypeCode == changeBeforeModel.psnTypeCode
    && formItems.value.psnTypeName == changeBeforeModel.psnTypeName)
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
  if(formItems.value.psnTypeCode==''|| formItems.value.psnTypeCode==null){
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '分类编码不能为空！'
    })
    saveClick.value=false
    return false
  }
  if(formItems.value.psnTypeName==''|| formItems.value.psnTypeName==null){
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '分类名称不能为空！'
    })
    saveClick.value=false
    return false
  }
  const res = await psnTypeFindAll(formItems.value.originId)
  const psnTypeList = res.items
  if (!formItems.value.psnTypeCode == changeBeforeModel.psnTypeCode && formItems.value.psnTypeCode!='' && formItems.value.psnTypeCode!=null){
    for (let i = 0; i < psnTypeList.length; i++) {
      const psnType = psnTypeList[i];
      if (psnType.psnTypeCode==formItems.value.psnTypeCode){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '分类编码重复不允许添加！'
        })
        saveClick.value=false
        return false
      }
    }
  }
  if (!formItems.value.psnTypeName == changeBeforeModel.psnTypeName && formItems.value.psnTypeName!='' && formItems.value.psnTypeName!=null){
    for (let i = 0; i < psnTypeList.length; i++) {
      const psnType = psnTypeList[i];
      if (psnType.psnTypeName==formItems.value.psnTypeName){
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '分类名称重复不允许添加！'
        })
        saveClick.value=false
        return false
      }
    }
  }
  isChanged = !(formItems.value.uniqueCode == changeBeforeModel.uniqueCode
    && formItems.value.psnTypeCode == changeBeforeModel.psnTypeCode
    && formItems.value.psnTypeName == changeBeforeModel.psnTypeName)
  if(isChanged){
    emit('save', unref(formItems))
    // closeModal()
    message.success('保存成功！')
    formItems.value = {}
    formItems.value.originId = changeBeforeModel.originId
    changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
    saveClick.value=false
    return true
  }
  // closeModal()
  // alert("没有改变过")
  saveClick.value=false
  return false
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
