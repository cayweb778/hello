<template>
  <BasicModal
    v-bind="$attrs"
    title="币种信息"
    @ok="onSubmit"
    @register="register"
  >
    <a-form :model="formState">
      <div
        class="nc-open-content"
        style="height: 100%"
      >
        <div class="open-content-up">
          <label>国家名称</label>
          <a-input v-model:value="formState.countryName" placeholder="国家名称" /><br>
          <label>中文名称</label>
          <a-input v-model:value="formState.currencyZhCnName" placeholder="中文名称" /><br>
          <label>英文名称</label>
          <a-input v-model:value="formState.currencyEnName" placeholder="英文名称" /><br>
          <label>外币符号</label>
          <a-input v-model:value="formState.currencySymbol" placeholder="外币符号" /><br>
          <label>国际代码</label>
          <a-input v-model:value="formState.abbreviation" placeholder="国际代码" /><br>
          <label>换算率</label>
          <a-input v-model:value="formState.fractionalCurrency" placeholder="换算率" /><br>
          <label>排序</label>
          <a-input v-model:value="formState.num" type="number" placeholder="排序" /><br>
        </div>
      </div>
    </a-form>
  </BasicModal>
</template>
<script setup lang="ts">
import { reactive, toRaw, ref,  unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'

const emit=defineEmits([])

const formState = reactive({
  countryName: '',
  currencyZhCnName: '',
  currencyEnName: '',
  currencySymbol: '',
  abbreviation: '',
  fractionalCurrency: '',
  num: '',
  id: '0'
})
// 赋值
const [register, { closeModal }] = useModalInner((data) => {
  formState.countryName = data.data.countryName,
  formState.currencyZhCnName = data.data.currencyZhCnName,
  formState.currencyEnName = data.data.currencyEnName,
  formState.currencySymbol = data.data.currencySymbol,
  formState.abbreviation = data.data.abbreviation,
  formState.fractionalCurrency = data.data.fractionalCurrency,
  formState.num = data.data.num,
  formState.id = data.data.id
})

const onSubmit = async() => {
  emit('save', toRaw(formState))
  closeModal()
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
    width: 35%;
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
    text-align: right;
    width: 90px;
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
