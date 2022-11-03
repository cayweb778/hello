<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="现金流量基础项目设置"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">

        <label>项目编码</label>
        <a-input v-model:value="formItems.projectCode" placeholder="项目编码" />
        <br/>
        <label>项目名称</label>
        <a-input v-model:value="formItems.projectName" placeholder="项目名称" />
        <br>

        <label>所属类别</label>
        <a-select v-model:value="formItems.projectType" placeholder="所属类别" style="width: 50%;">
          <a-select-option value="1">经营活动</a-select-option>
          <a-select-option value="2">投资活动</a-select-option>
          <a-select-option value="3">筹资活动</a-select-option>
          <a-select-option value="4">汇率活动</a-select-option>
          <a-select-option value="5">现金及现金等价物</a-select-option>
        </a-select>
        <br/>
        <label>方向</label>
        <a-select v-model:value="formItems.fangxiang" placeholder="方向" style="width: 50%;">
          <a-select-option value="1">流入</a-select-option>
          <a-select-option value="0">流出</a-select-option>
        </a-select>

      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import { Select as ASelect, Input as AInput, DatePicker as ADatePicker } from 'ant-design-vue';
const AInputSearch = AInput.Search;
const ASelectOption = ASelect.Option;

const emit=defineEmits([])

const formItems:any = ref({})

let changeBeforeModel:any = {}

const [register, { closeModal }] = useModalInner((data) => {
  // 方式2
  formItems.value.id = data.data.id
  formItems.value.projectCode = data.data.projectCode
  formItems.value.projectName = data.data.projectName
  formItems.value.projectType = data.data.projectType
  formItems.value.fangxiang = data.data.fangxiang

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged:boolean = false
async function handleOk() {
  isChanged = !(formItems.value.projectCode == changeBeforeModel.projectCode
    && formItems.value.projectName == changeBeforeModel.projectName
    && formItems.value.projectType == changeBeforeModel.projectType
    && formItems.value.fangxiang == changeBeforeModel.fangxiang)
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
