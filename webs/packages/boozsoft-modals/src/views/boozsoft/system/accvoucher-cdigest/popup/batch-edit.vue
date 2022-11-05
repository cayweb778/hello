<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="常用摘要批量修改"
    @ok="handleOk()"
    @register="register"
    :canFullscreen="false"
    wrapClassName="head-title"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;摘要分类
        </span>
      </div>
    </template>
    <div
      class="nc-open-content"
    >
      <div class="open-content-up" style="text-align: center;margin-top: 30px;">

        <label>所属分类:</label>
        <a-select v-model:value="formItems.classCode" placeholder="" style="width:50%;">
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
          <a-select-option v-for="item in classList" :key="item.classCode" :value="item.classCode">
            {{ item.className }}
          </a-select-option>
        </a-select>

      </div>
    </div>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import {ref, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {Select as ASelect, Input as AInput} from "ant-design-vue"
import {findAllApi as findClassAllApi} from '/@/api/boozsoft/account/AccvoucherCdigestClass'
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {
  FormOutlined,
  CaretDownOutlined
} from '@ant-design/icons-vue'

const ASelectOption = ASelect.Option

const emit = defineEmits(['register', 'save'])
const props = defineProps(['modelValue'])

const formItems: any = ref({})

let changeBeforeModel: any = {}

const classList = ref([])

const dynamicTenantId = ref('')
const [register, {closeModal}] = useModalInner((data) => {
  dynamicTenantId.value = data.dynamicTenantId
  //获取分类
  useRouteApi(findClassAllApi, {schemaName: dynamicTenantId})({}).then(res => {
    classList.value = res
  })
  // 方式2
  formItems.value = {
    classCode: data.data.classCode
  }

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged: boolean = false

async function handleOk() {
  isChanged = !(formItems.value.classCode == changeBeforeModel.classCode)
  if (isChanged) {
    emit('save', unref(formItems))
    closeModal()
    return true
  }
  closeModal()
  // alert("没有改变过")
  return false
}

</script>
<style scoped lang="less">
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
:deep(.vben-basic-title) {
  color: #0096c7 !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}

.is_show{
  pointer-events: none;
  cursor: default;
}

.nc-open-content {
  input {
    width: 50%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-size: 14px;
    font-weight: bold;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-size: 14px;
    font-weight: bold;
  }

  label {
    text-align: left;
    width: 110px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    color: #535353;
    font-size: 13px;
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

<style>
.head-title .scroll-container .scrollbar__wrap {
  margin-bottom: -120px !important;
}
</style>
