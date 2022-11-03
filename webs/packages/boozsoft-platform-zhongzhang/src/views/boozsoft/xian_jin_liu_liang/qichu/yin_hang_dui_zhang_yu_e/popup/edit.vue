<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="调整前余额"
    @ok="handleOk()"
    @register="register"
  >
    <div class="nc-open-content">
      <div class="open-content-up">
        <div v-if="type=='dayBeforeMoney'">
          <label style="width: 200px;">单位日记账调整前余额</label>
          <a-input type="number" v-model:value="formItems.dayBeforeMoney" placeholder="单位日记账调整前金额" style="width: 50%;" />
        </div>
        <div v-if="type=='statementBeforeMoney'">
          <label style="width: 200px;">银行对账单调整前余额</label>
          <a-input type="number" v-model:value="formItems.statementBeforeMoney" placeholder="银行对账单调整前金额" style="width: 50%;" />
        </div>
      </div>
    </div>
  </BasicModal>
</template>

<script setup="props, { content }" lang="ts">
  import { ref, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { Input as AInput } from 'ant-design-vue';
  const emit=defineEmits(['register','save']);

  const formItems: any = ref({});
  const type:any = ref()
  const title:any = ref()

  const [register, { closeModal }] = useModalInner((data) => {
    // 方式2
    formItems.value.id = data.data.id
    formItems.value.iyear = data.data.iyear
    formItems.value.qiyongDate = data.data.qiyongDate
    formItems.value.ccode = data.data.ccode
    formItems.value.flag = data.data.flag
    formItems.value.dayBeforeMoney = data.data.dayBeforeMoney
    formItems.value.statementBeforeMoney = data.data.statementBeforeMoney
    type.value = data.type
  });
  async function handleOk() {
    if (formItems.value.flag=='' || formItems.value.flag==undefined){
      formItems.value.flag = 1
    }
    emit('save', unref(formItems));
    closeModal();
  }
</script>
<style lang="less" scoped>
  :deep(.ant-calendar-picker-input.ant-input),
  :deep(.ant-select-single:not(.ant-select-customize-input)
      .ant-select-selector
      .ant-select-selection-search-input) {
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
