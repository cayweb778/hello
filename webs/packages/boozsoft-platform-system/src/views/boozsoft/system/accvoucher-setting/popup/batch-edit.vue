<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="常用凭证批量修改"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
    >
      <div class="open-content-up" style="text-align: center;margin-top: 30px;">

        <label>凭证类别:</label>
        <a-input v-model:value="formItems.ctype" placeholder=""/>
        <br><br>
        <label>附单据数:</label>
        <a-input type="number" v-model:value="formItems.cnum" placeholder=""/>
        <br><br>
        <label>所属分类:</label>
        <a-select v-model:value="formItems.classCode" placeholder="" style="width:50%;">
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
import {findAllApi as findClassAllApi} from '/@/api/boozsoft/account/AccvoucherSettingClass'
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";

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
  useRouteApi(findClassAllApi, {schemaName: dynamicTenantId})().then(res => {
    classList.value = res
  })
  // 方式2
  formItems.value = {
    ctype: data.data.ctype,
    cnum: data.data.cnum,
    classCode: data.data.classCode
  }

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged: boolean = false

async function handleOk() {
  isChanged = !(formItems.value.ctype == changeBeforeModel.remarks
    && formItems.value.cnum == changeBeforeModel.ctype
    && formItems.value.classCode == changeBeforeModel.classCode)
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

  .ant-select-selector {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label {
    text-align: left;
    width: 80px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    color: #535353;
    margin-left: 2em;
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
