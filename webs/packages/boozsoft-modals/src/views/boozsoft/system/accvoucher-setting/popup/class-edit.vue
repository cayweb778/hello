<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="凭证分类"
    @ok="handleOk()"
    @register="register"
    :canFullscreen="false"
    wrapClassName="head-title"
  >
    <template #title>
      <div style="display: flex;margin-top: 10px;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='0'">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;凭证分类
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='1'">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;凭证分类
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='2'">
          <FileSearchOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;凭证分类
        </span>
      </div>
    </template>
    <div
      class="nc-open-content"
    >
      <div class="open-content-up" style="text-align: center;margin-top: 80px;">

        <label>分类编码:</label>
        <a-input v-model:value="formItems.classCode" :disabled="true" placeholder=""/>

        <br><br>
        <label>分类名称:</label>
        <a-input v-model:value="formItems.className" placeholder=""/>

      </div>
    </div>

    <template #footer>
      <div v-if="isState=='2'">
        <a-button @click="closeModal()" type="primary">关闭</a-button>
      </div>
      <div v-if="isState=='0'">
        <a-button @click="closeModal()">取消</a-button>
        <a-button @click="handleOk()" :disabled="saveClick">保存</a-button>
        <a-button @click="handleOkAdd()" :disabled="saveClick" type="primary">保存并新增</a-button>
      </div>
      <div v-if="isState=='1'">
        <a-button @click="closeModal()">放弃</a-button>
        <a-button @click="handleOk()" :disabled="saveClick" type="primary">保存</a-button>
      </div>
    </template>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import {ref, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {Select as ASelect, Input as AInput} from "ant-design-vue"
import {
  PlusCircleOutlined,
  FormOutlined,
  FileSearchOutlined,
  CaretDownOutlined
} from '@ant-design/icons-vue'
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {
  findAllApi,
  findBukongClassCode,
  findMaxClassCode, saveApi
} from "/@/api/boozsoft/account/AccvoucherSettingClass";
import {useMessage} from "/@/hooks/web/useMessage";
//import {mul} from "/@/views/boozsoft/gdzc/gu-ding-zi-chan-add/calculation";
const mul=null

const {
  createErrorModal
} = useMessage()

const emit = defineEmits(['register', 'save'])
const props = defineProps(['modelValue'])

const formItems: any = ref({})

let changeBeforeModel: any = {}

const dynamicTenantId = ref('')
const isState = ref('0')
const saveClick:any = ref(false)
const [register, {closeModal}] = useModalInner((data) => {
  saveClick.value=false
  dynamicTenantId.value = data.dynamicTenantId
  isState.value = data.isState
  // 方式2
  formItems.value = {
    id: data.data.id,
    classCode: data.data.classCode,
    className: data.data.className
  }

  if (data.isState=='0') {
    maxCode()
  }

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged: boolean = false

async function handleOk() {
  saveClick.value=true
  if (formItems.value.classCode == null || formItems.value.classCode == '') {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '分类编码不能为空！'
    })
    saveClick.value=false
    return false
  }
  if (formItems.value.className == null || formItems.value.className == '') {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '分类名称不能为空！'
    })
    saveClick.value=false
    return false
  }
  const classList = await useRouteApi(findAllApi, {schemaName: dynamicTenantId})({})
  for (let i = 0; i < classList.length; i++) {
    const item: any = classList[i];
    if (formItems.value.classCode != changeBeforeModel.classCode && item.classCode == formItems.value.classCode) {
      createErrorModal({
        iconType: 'warning',
        title: '删除',
        content: '分类编码重复不允许添加！'
      })
      formItems.value.ccode = ''
      saveClick.value=false
      return false
    }
    if (formItems.value.className != changeBeforeModel.className && item.className == formItems.value.className) {
      createErrorModal({
        iconType: 'warning',
        title: '删除',
        content: '分类名称重复不允许添加！'
      })
      formItems.value.content = ''
      saveClick.value=false
      return false
    }
  }
  isChanged = !(formItems.value.classCode == changeBeforeModel.classCode
    && formItems.value.className == changeBeforeModel.className)
  if (isChanged) {
    emit('save', unref(formItems))
    closeModal()
    saveClick.value=false
    return true
  }
  closeModal()
  // alert("没有改变过")
  saveClick.value=false
  return false
}

async function handleOkAdd() {
  saveClick.value=true
  if (formItems.value.classCode == null || formItems.value.classCode == '') {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '分类编码不能为空！'
    })
    saveClick.value=false
    return false
  }
  if (formItems.value.className == null || formItems.value.className == '') {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '分类名称不能为空！'
    })
    saveClick.value=false
    return false
  }
  const classList = await useRouteApi(findAllApi, {schemaName: dynamicTenantId})({})
  for (let i = 0; i < classList.length; i++) {
    const item: any = classList[i];
    if (item.classCode == formItems.value.classCode) {
      createErrorModal({
        iconType: 'warning',
        title: '删除',
        content: '分类编码重复不允许添加！'
      })
      formItems.value.ccode = ''
      saveClick.value=false
      return false
    }
    if (item.className == formItems.value.className) {
      createErrorModal({
        iconType: 'warning',
        title: '删除',
        content: '分类名称重复不允许添加！'
      })
      formItems.value.content = ''
      saveClick.value=false
      return false
    }
  }
  isChanged = !(formItems.value.classCode == changeBeforeModel.classCode
    && formItems.value.className == changeBeforeModel.className)
  if (isChanged) {
    emit('save', unref(formItems))
    // closeModal()
    // await useRouteApi(saveApi, {schemaName: dynamicTenantId})(formItems.value)
    formItems.value = {}
    await maxCode()
    saveClick.value=false
    return true
  }
  closeModal()
  saveClick.value=false
  // alert("没有改变过")
  return false
}

async function maxCode(){
  const item1 = await useRouteApi(findBukongClassCode,{schemaName: dynamicTenantId})({})
  if (item1!=null && item1.classCode!=null && item1.classCode!=''){
    formItems.value.classCode = pad(item1.classCode,3)
  } else {
    const item2 = await useRouteApi(findMaxClassCode, {schemaName: dynamicTenantId})({})
    if (item2 != null && item2.classCode != null && item2.classCode != '') {
      formItems.value.classCode = pad(add(item2.classCode, 1), 3)
    } else {
      formItems.value.classCode = '001'
    }
  }
}

/**
 * 字符串前补0
 * @param num
 * @param n
 */
function pad(num, n) {
  let len = num.toString().length;
  while(len < n) {
    num = "0" + num;
    len++;
  }
  return num;
}

/**
 * 加法
 * @param a
 * @param b
 */
function add(a, b) {
  let c, d, e;
  try {
    c = a.toString().split(".")[1].length;
  } catch (f) {
    c = 0;
  }
  try {
    d = b.toString().split(".")[1].length;
  } catch (f) {
    d = 0;
  }
  return e = Math.pow(10, Math.max(c, d)), (mul(a, e) + mul(b, e)) / e;
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

.is_show{
  pointer-events: none;
  cursor: default;
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
  margin-bottom: -60px !important;
}
</style>
