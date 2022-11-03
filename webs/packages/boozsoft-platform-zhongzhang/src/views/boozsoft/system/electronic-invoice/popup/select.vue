<template>
  <BasicModal
    width="350px"
    title="关联凭证"
    v-bind="$attrs"
    @ok="handleOk()"
    :showOkBtn="isEdit"
    :showCancelBtn="true"
    @register="register"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;关联凭证
        </span>
      </div>

    </template>
    <div
      :class="isEdit?'nc-open-content':'nc-open-show-content'"
    >
      <div class="open-content-up" style="text-align: center;padding-top: 50px">

        <a-select style="width: 180px"
                  show-search
                  allowClear="true"
                  :filter-option="filterOption"
                  v-model:value="formItems.typeCode">
          <a-select-option  v-for="d in bzList" :value="d.uniqueCode">
            {{ getData(d.csign,d.inoId) }}
          </a-select-option>
        </a-select>
      </div>
    </div>
    <template #footer>
      <div v-if="isEdit">
        <a-button @click="handleOk()" type="primary">关联</a-button>
      </div>
    </template>
  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {onMounted, ref, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {
  Input as AInput,
  Select as ASelect,
  Popover as APopover,
  Tabs as ATabs,
  DatePicker as ADatePicker,
  Cascader as ACascader,
  Radio as ARadio,
  TreeSelect as ATreeSelect
} from "ant-design-vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {
  PlusCircleOutlined,
  CaretDownOutlined
} from '@ant-design/icons-vue'
import {useMessage} from "/@/hooks/web/useMessage";
import {findAllByIfrag} from '/@/api/record/system/accvoucher'
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";

const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ATabPane = ATabs.TabPane
const ARadioGroup = ARadio.Group
const ARadioButton = ARadio.Button
const emit = defineEmits(['register','save'])

const {
  createErrorModal
} = useMessage()

const formItems: any = ref({})

// 是否为修改
const isEdit = ref(true)
const edit = ref(false)
let changeBeforeModel: any = {}

let databasesName = ref(getCurrentAccountName(true))
const bzList: any = ref([])
const [register, {closeModal}] = useModalInner((data) => {
  edit.value = data.data.edit
  getPzList(data.accId)
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged: boolean = false

async function getPzList(accId) {
  let res = await useRouteApi(findAllByIfrag,{schemaName:accId})({})
  bzList.value = res.items
}
async function handleOk() {
  console.log(formItems.value.typeCode)
  emit('save', unref(formItems.value.typeCode))
  closeModal()
  return true
}

const activeKey = ref('1')


function getData(a,b) {
  return a+(b.padStart(3,'0'))
}

function handleFocus() {
}

function handleBlur() {
}

function handleChange() {
}

const filterOption = (input: string, option: any) => {
  return option.value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
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
    width: 120px;
    display: inline-block;
    padding-top: 5px;
    padding-left: 5px;
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
