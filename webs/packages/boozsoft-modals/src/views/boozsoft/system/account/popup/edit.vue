<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="集团信息"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">

        <label>账套编码</label>
        <a-input v-model:value="formItems.accId" placeholder="请输入账套编码（不允许重复）" style="width: 30%" />
        <span>必填项</span>
        <label>账套名称</label>
        <a-input v-model:value="formItems.accName" placeholder="请输入账套名称（不允许重复）" style="width: 30%" />
        <span>必填项</span>
        <br>

        <label>会计准则</label>
        <a-input v-model:value="formItems.uniqueAccStandard" placeholder="" />
        <label>本位币种</label>
        <a-input v-model:value="formItems.abbreviation" placeholder="" />
        <br>

        <label>启用年度</label>
        <a-input v-model:value="formItems.iyear" placeholder="" />
        <label>启用月份</label>
        <a-input v-model:value="formItems.imonth" placeholder="" />
        <br>

        <label>账套管理员</label>
        <a-input v-model:value="formItems.uniqueUserCode" placeholder="" />
        <label>会计期间</label>
        <a-input v-model:value="formItems.iperiod" placeholder="" />
        <br>

        <label>默认语言</label>
        <a-input v-model:value="formItems.defaultLanguage" placeholder="" />
        <label>行业分类</label>
        <a-input v-model:value="formItems.industryClassCode" placeholder="" />
        <br>

        <label>行业信息</label>
        <a-input v-model:value="formItems.industryCode" placeholder="" />
        <label>科目级次</label>
        <a-input v-model:value="formItems.kemuJici" placeholder="" />
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

const [register, { closeModal }] = useModalInner((data) => {
  // 方式2
  formItems.value = data.data
})

async function handleOk() {
  emit('save', unref(formItems))
  closeModal()
}

</script>
<style lang="less" scoped>
:deep(.ant-calendar-picker-input.ant-input),:deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input){
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
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
