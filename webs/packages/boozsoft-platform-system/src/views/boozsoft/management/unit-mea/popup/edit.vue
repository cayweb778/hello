<template>
  <BasicModal
    width="400px"
    v-bind="$attrs"
    title="计量单位"
    @ok="handleOk()"
    :showOkBtn="isEdit"
    :showCancelBtn="true"
    @register="register"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;计量单位
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="../../../../../assets/images/012.png" style="height:80px;margin-right: 10px;"/>
      </div>
    </template>
    <div
      :class="isEdit?'nc-open-content':'nc-open-show-content'"
    >
      <div class="open-content-up" style="text-align: center;padding-top: 50px">
        <label style="font-size: 18px;margin-left: 0;">计量单位：</label>
        <a-input v-model:value="formItems.unitName" placeholder="请输入计量单位名称" class="abc" style="width: 55%;" />
        <span class="red_span">*</span>
      </div>
    </div>
    <template #footer>
      <div v-if="isEdit">
        <a-button @click="closeModal()">取消</a-button>
        <a-button @click="handleOk(false)" type="primary">保存</a-button>
        <a-button @click="handleOk(true)" type="primary">保存并新增</a-button>
      </div>
    </template>
  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {ref, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {
  Input as AInput,
  Select as ASelect,
  Popover as APopover,
  Tabs as ATabs,
  DatePicker as ADatePicker,
  Cascader as ACascader,
  TreeSelect as ATreeSelect
} from "ant-design-vue";
import {
  PlusCircleOutlined,
  CaretDownOutlined
} from '@ant-design/icons-vue'
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";

import {useMessage} from "/@/hooks/web/useMessage";

const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ATabPane = ATabs.TabPane
const emit = defineEmits(['register','save'])

const {
  createErrorModal
} = useMessage()

const formItems: any = ref({
  typeCode:'k1'
})

// 是否为修改
const isEdit = ref(true)
const edit = ref(false)
let changeBeforeModel: any = {}
const typesList: any = ref([{
  typeCode: 'k1',
  typeName: '单计量',
},{
  typeCode: 'k2',
  typeName: '多计量',
}])
const [register, {closeModal}] = useModalInner((data) => {
  //获取分类list 
  getTypes()
  edit.value = data.data.edit
  if(edit.value){
    formItems.value = data.data.data
  }
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged: boolean = false

const typeList: any = ref([])
async function getTypes() {
  //let res = await findAllSysUnitOfMeaType()
  //typeList.value = res

}

async function handleOk(flg) {

  if (formItems.value.unitName==null || formItems.value.unitName==''){
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '计量单位名称不能为空！'
    })
    return false
  }
  if(flg){
    emit('save', unref(formItems))
    formItems.value = {}
  }else{
    emit('save', unref(formItems))
    closeModal()
    return true
  }
}

const activeKey = ref('1')

function filterOption() {
}

function handleFocus() {
}

function handleBlur() {
}

function handleChange() {
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
