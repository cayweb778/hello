<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="银行对账单"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">

        <label>会计科目</label>
        <a-input v-model:value="formItems.kemuCode" placeholder="会计科目" />
        <br/>
        <label>对账日期</label>
        <!-- <a-date-picker v-model:value="formItems.statementDate" placeholder="对账日期" format="YYYY-MM-DD" style="width: 50%;" /> -->
        <a-range-picker
          :placeholder="['开始月份', '结束月份']"
          show-time
          format="YYYY-MM"
          :value="value2"
          :mode="mode2"
          @panelChange="handlePanelChange"
          @change="handleChange"
        />

      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
const emit=defineEmits([])

const formItems:any = ref({})

const [register, { closeModal }] = useModalInner((data) => {
  // 方式2
  formItems.value.kemuCode = data.data.kemuCode
  formItems.value.date1 = data.data.date1
  formItems.value.date2 = data.data.date2
})
async function handleOk() {
  formItems.value.date1 = value2.value[0]
  formItems.value.date2 = value2.value[1]
  emit('check', unref(formItems))
  closeModal()
}
const mode2 = ref(['month', 'month'])
const value2 = ref([])
function handleChange(value1:any) {
  value2.value = value1;
}
function handlePanelChange(value1:any, mode:any) {
  value2.value = value1;
  mode2.value = [mode[0] === 'date' ? 'month' : mode[0], mode[1] === 'date' ? 'month' : mode[1]];
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
