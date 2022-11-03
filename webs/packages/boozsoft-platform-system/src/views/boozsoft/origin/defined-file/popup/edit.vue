<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="自定义项设置"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up" style="text-align: center;margin-top: 30px;margin-bottom: 30px;">

        <label>自定义项设置编码：</label>
        <a-input v-model:value="formItems.definedCode" @blur="checkCode()" placeholder="自定义项设置编码"/>

        <br><br>
        <label>自定义项设置名称：</label>
        <a-input v-model:value="formItems.definedName" @blur="checkName()" placeholder="自定义项设置名称"/>
        <br><br>
        <label>说明：</label>
        <a-input v-model:value="formItems.remarks" placeholder="说明"/>

      </div>
    </div>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {Input as AInput } from 'ant-design-vue'
import {useMessage} from "/@/hooks/web/useMessage";
import {findByCode, findByName} from "/@/api/record/system/origin-defined-file";

const {
  createErrorModal
} = useMessage()

const emit = defineEmits(['register', 'save'])

const formItems: any = ref({})

let changeBeforeModel: any = {}

const [register, {closeModal}] = useModalInner((data: any) => {

  // 方式2
  formItems.value = {
    id: data.data.id,
    definedCode: data.data.definedCode,
    definedName: data.data.definedName,
    flag: '1',
    remarks: data.data.remarks,
    originId: data.originId
  }
  changeBeforeModel = JSON.parse(JSON.stringify(formItems))
  formItems.value.definedName = formItems.value.definedName.replace("[自定义]", "")
})
let isChanged:boolean = false
async function handleOk() {
  if (formItems.value.definedCode==''){
    createErrorModal({
      iconType: 'warning',
      title: '保存失败',
      content: '自定义项设置编码不能为空！'
    })
    return false
  }
  if (formItems.value.definedName == '') {
    createErrorModal({
      iconType: 'warning',
      title: '保存失败',
      content: '自定义项设置名称不能为空！'
    })
    return false
  }
  formItems.value.definedName = formItems.value.definedName.replace("[自定义]", "")
  formItems.value.definedName = "[自定义]" + formItems.value.definedName
  isChanged = !(formItems.value.definedCode == changeBeforeModel._value.definedCode
    && formItems.value.definedName == changeBeforeModel._value.definedName
    && formItems.value.remarks == changeBeforeModel._value.remarks)
  console.log(isChanged)
  if (isChanged) {
    emit('save', unref(formItems))
    closeModal()
    return true
  }
  closeModal()
  // alert("没有改变过")
  return false
}

async function checkCode() {
  if ((changeBeforeModel._value.definedCode != undefined && changeBeforeModel._value.definedCode != '') && changeBeforeModel._value.definedCode == formItems.value.definedCode) {
    return true
  }
  if (formItems.value.definedCode == null || formItems.value.definedCode == '') {
    return true
  }
  const res = await findByCode(formItems.value.definedCode,formItems.value.originId)
  if (res.length != 0) {
    createErrorModal({
      iconType: 'warning',
      title: '输入失败',
      content: '自定义项设置编码已存在，请重新输入！'
    })
    formItems.value.definedCode = ''
    return false
  }
  return true
}

async function checkName() {
  let definedName = formItems.value.definedName
  definedName = definedName.replace("[自定义]", "")
  definedName = "[自定义]" + definedName
  if ((changeBeforeModel._value.definedName != undefined && changeBeforeModel._value.definedName != '') && changeBeforeModel._value.definedName == definedName) {
    return true
  }
  if (formItems.value.definedName == null || formItems.value.definedName == '') {
    return true
  }
  const res = await findByName(definedName,formItems.value.originId)
  if (res.length != 0) {
    createErrorModal({
      iconType: 'warning',
      title: '输入失败',
      content: '自定义项设置名称已存在，请重新输入！'
    })
    formItems.value.definedName = ''
    return false
  }
  return true
}

</script>
<style>
.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}
</style>
<style lang="less" scoped>
:deep(.ant-calendar-picker-input.ant-input),
:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none;
  width: 100%;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
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

.nc-open-content {
  input:not(.ant-select-selection-search-input,.ant-input) {
    width: 50%;
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
    text-align: left;
    width: 150px;
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
