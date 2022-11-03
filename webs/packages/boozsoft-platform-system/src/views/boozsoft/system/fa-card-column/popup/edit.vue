<template>
  <BasicModal
    width="560px"
    v-bind="$attrs"
    title="卡片栏目设置"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up" style="text-align: center;margin-top: 30px;">


<!--        <label style="white-space: nowrap"> 栏目名称：</label>
        <a-input v-model:value="formItems.columnName" @blur="checkRepet" placeholder=""
                 style="font-weight: 900"/> <br><br>-->


        <label style="white-space: nowrap">显示名称：</label>
        <a-input v-model:value="formItems.showName" @blur="checkNameRepet" placeholder=""
                 style="font-weight: 900"/>

        <br><br>
        <label style="white-space: nowrap">数据类型 ：</label>
        <a-select
          v-model:value="formItems.dataType"
          placeholder=""
          @change="changeType"
          style="font-weight: 900;width: 50%"
        >
          <a-select-option value="0">文本</a-select-option>
          <a-select-option value="1">整数</a-select-option>
          <a-select-option value="2">数值</a-select-option>
          <a-select-option value="3">日期</a-select-option>
        </a-select>
        <br><br>

        <label style="white-space: nowrap">字符长度：</label>
        <a-input v-model:value="formItems.charLength" disabled placeholder="" class="against-input-mark"
                 style="font-weight: 900"/>
        <br><br>

        <label style="white-space: nowrap">小数点：</label>
        <a-input v-model:value="formItems.decimalPlaces" disabled placeholder="" class="against-input-mark"
                 style="font-weight: 900"/>
      </div>
    </div>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import {inject, onMounted, ref, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {SavefaCardColumn} from "/@/api/record/gdzc/fa-card-column";
import {
  message,
  // need
  TreeSelect
} from 'ant-design-vue'
import {GetDeptTreeByFlag} from '/@/api/sys/dept'
import {psnFindByFlag} from '/@/api/psn/psn'
import {Input as AInput, Select as ASelect, Popover as APopover} from "ant-design-vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useMessage} from "/@/hooks/web/useMessage";
import {buildShortUUID} from "/@/utils/uuid";

const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const emit = defineEmits(['register', 'save'])

/* const formItems = ref({
  peopertyId: '',
  peopertyName: '',
  parentId: '',
  uniqueCodeUser: ''
})*/

const {
  createErrorModal
} = useMessage()

const formItems: any = ref({})

const treeData: any = ref([])

const psnList: any = ref({})

let changeBeforeModel: any = {}
const dynamicTenantId = ref()
const [register, {closeModal}] = useModalInner((data) => {
  dynamicTenantId.value = data.dynamicTenantId

  // 方式2
  formItems.value = {
    id: data.data.id,
    flag: data.data.flag,
    uniqueCode: data.data.uniqueCode,
    columnName: data.data.columnName,
    showName: data.data.showName,
    dataType: data.data.dataType,
    charLength: data.data.charLength,
    decimalPlaces: data.data.decimalPlaces,
    status: data.data.status,
  }

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
});
const reload = inject('reload2')
let isChanged: boolean = false

async function handleOk() {
  const id = formItems.value.peopertyId
  const name = formItems.value.peopertyName

  if (id == '') {
    message.error('属性编码不能为空')
  } else if (name == '') {
    message.error('属性名称不能为空')
  } else {
    formItems.value.uniqueCustclass = buildShortUUID()
    useRouteApi(SavefaCardColumn, {schemaName: dynamicTenantId})(formItems.value)

    closeModal()
    reload()
    return false
  }

  closeModal()
  return false
}


onMounted(async () => {
})

const handleChange = () => {
  console.log('selected ${key}')
}

const changeType = (data) => {
  console.log(data)
  if(data === '1'){
    formItems.value.charLength = '12'
    formItems.value.decimalPlaces = '2'
  }else if(data === '2'){
    formItems.value.charLength = '12'
    formItems.value.decimalPlaces = '2'
  }else if(data === '3'){
    formItems.value.charLength = '8'
    formItems.value.decimalPlaces = '2'
  }else if(data === '0'){
    formItems.value.charLength = '255'
    formItems.value.decimalPlaces = '2'
  }
}

const filterOption = (input: string, option: any) => {
  return option.props.value.toLowerCase().indexOf(input.toLowerCase()) >= 0
}

function handleFocus() {
}

function handleBlur() {
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
:deep(.vben-basic-title) {
  color: rgb(1, 129, 226) !important;
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
    text-align: left;
    width: 110px;
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
