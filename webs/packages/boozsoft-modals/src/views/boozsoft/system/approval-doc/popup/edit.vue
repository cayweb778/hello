<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="批文信息"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">

        <label>批文文号</label>
        <a-input v-model:value="formItems.docCode" placeholder="批文文号" />
        <label>批文名称</label>
        <a-input v-model:value="formItems.docName" placeholder="批文名称" />
        <br>

        <label>批准日期</label>
        <a-date-picker v-model:value="formItems.pizhunDate" placeholder="批准日期" format="YYYY-MM-DD" style="width: 35%;" />
        <label>是否涉密</label>
        <a-select v-model:value="formItems.isSecrecy" placeholder="是否涉密" style="width: 35%;">
          <a-select-option value="1">是</a-select-option>
          <a-select-option value="0">否</a-select-option>
        </a-select>
        <br>

        <label>开始日期</label>
        <a-date-picker v-model:value="formItems.startDate" placeholder="开始日期" format="YYYY-MM-DD" style="width: 35%;" />
        <label>结束日期</label>
        <a-date-picker v-model:value="formItems.endDate" placeholder="结束日期" format="YYYY-MM-DD" style="width: 35%;" />
        <br>

        <label>颁发机构全称</label>
        <a-input v-model:value="formItems.awardName" placeholder="颁发机构全称" />
        <label>颁发日期</label>
        <a-date-picker v-model:value="formItems.awardDate" placeholder="颁发日期" format="YYYY-MM-DD" style="width: 35%;" />
        <br>

        <label>批文内容</label>
        <a-input v-model:value="formItems.docContent" placeholder="批文内容" />
        <label>存放位置</label>
        <a-input v-model:value="formItems.docAddress" placeholder="存放位置" />
        <br>

        <label>保管人</label>
        <a-input v-model:value="formItems.psnCode" placeholder="保管人" />
        <label>备注</label>
        <a-input v-model:value="formItems.remark" placeholder="备注" />
        <br>

      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'

const emit=defineEmits([])

const formItems:any = ref({})

let changeBeforeModel:any = {}

const [register, { closeModal }] = useModalInner((data) => {
  // 方式2
  formItems.value.id = data.data.id
  formItems.value.docCode = data.data.docCode
  formItems.value.docName = data.data.docName
  formItems.value.pizhunDate = data.data.pizhunDate
  formItems.value.startDate = data.data.startDate
  formItems.value.endDate = data.data.endDate
  formItems.value.isSecrecy = data.data.isSecrecy
  formItems.value.awardName = data.data.awardName
  formItems.value.awardDate = data.data.awardDate
  formItems.value.docContent = data.data.docContent
  formItems.value.docAddress = data.data.docAddress
  formItems.value.psnCode = data.data.psnCode
  formItems.value.remark = data.data.remark
  formItems.value.vouchCode = data.data.vouchCode

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged:boolean = false
async function handleOk() {
  isChanged = !(formItems.value.docCode == changeBeforeModel.docCode
    && formItems.value.docName == changeBeforeModel.docName
    && formItems.value.pizhunDate == changeBeforeModel.pizhunDate
    && formItems.value.startDate == changeBeforeModel.startDate
    && formItems.value.endDate == changeBeforeModel.endDate
    && formItems.value.isSecrecy == changeBeforeModel.isSecrecy
    && formItems.value.awardName == changeBeforeModel.awardName
    && formItems.value.awardDate == changeBeforeModel.awardDate
    && formItems.value.docContent == changeBeforeModel.docContent
    && formItems.value.docAddress == changeBeforeModel.docAddress
    && formItems.value.psnCode == changeBeforeModel.psnCode
    && formItems.value.remark == changeBeforeModel.remark
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
