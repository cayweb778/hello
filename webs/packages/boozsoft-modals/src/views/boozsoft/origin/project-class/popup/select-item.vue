<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="选择项目大类"
    @ok="handleOk()"
    @register="register"
    :canFullscreen="false"
    wrapClassName="head-title-authPassword"
  >
    <template #title>
      <div style="display: flex;margin-top: 10px;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;选择项目大类
        </span>
      </div>
    </template>
    <div class="nc-open-content" style="text-align: center;margin-top: 60px;margin-bottom: 0">
      <div class="open-content-up">
        <label style="font-size: 18px;margin-left: 0;width:130px;">项目大类：</label>
        <a-select v-model:value="formItems.itemCode" class="abc" placeholder="请选择项目大类" style="width: 60%;">
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
          <a-select-option v-for="item in projectItemList" :key="item.itemCode" :value="item.itemCode">
            ({{ item.itemCode }}){{ item.itemName }}
          </a-select-option>
        </a-select>
      </div>
    </div>

    <template #footer>
      <div>
        <a-button @click="closeModal()">取消</a-button>
        <a-button @click="handleOk()" :disabled="saveClick" type="primary">确认</a-button>
      </div>
    </template>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {Input as AInput,Select as ASelect, message} from "ant-design-vue"
import {
  FormOutlined,CaretDownOutlined
} from '@ant-design/icons-vue'
import {useMessage} from "/@/hooks/web/useMessage";
import {getProjectItemList} from "/@/api/record/system/origin-project-item";

const ASelectOption = ASelect.Option

const {
  createErrorModal
} = useMessage()

const emit=defineEmits(['register','save'])

const formItems:any = ref({})

const isState = ref('0')
const saveClick:any = ref(false)
const projectItemList:any = ref([])
const [register, { closeModal }] = useModalInner(async (data) => {
  saveClick.value=false
  isState.value = data.isState

  const res:any = await getProjectItemList(data.originId)
  projectItemList.value = res
  if (projectItemList.value.length > 0) {
    formItems.value.itemCode = projectItemList.value[0].itemCode
  }
})

async function handleOk() {
  saveClick.value=true
  if(formItems.value.itemCode==''|| formItems.value.itemCode==null){
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '请选择项目大类！'
    })
    saveClick.value=false
    return false
  }
  emit('save', unref(formItems))
  closeModal()
  saveClick.value=false
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
}
</style>

<style>
.head-title-authPassword .scroll-container .scrollbar__wrap {
  margin-bottom: -120px !important;
}
</style>
