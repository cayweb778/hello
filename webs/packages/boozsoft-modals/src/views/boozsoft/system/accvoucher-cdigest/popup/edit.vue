<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="常用摘要设置"
    @ok="handleOk()"
    @register="register"
    :canFullscreen="false"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='0'">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;常用凭证设置
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='1'">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;常用凭证设置
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='2'">
          <FileSearchOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;常用凭证设置
        </span>
      </div>
    </template>
    <div class="nc-open-content">
      <div class="open-content-up" :class="isState=='2'?'is_show':''" style="text-align: center;margin-top: 30px;">

        <label>编码:</label>
        <a-input v-model:value="formItems.ccode" :disabled="true" placeholder=""/>

        <br/><br/>
        <label>常用摘要内容:</label>
        <a-input v-model:value="formItems.content" placeholder=""/>
        <br/><br/>
        <label>所属分类:</label>
        <a-select v-model:value="formItems.classCode" placeholder="" style="width:50%;">
          <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
          <a-select-option v-for="item in classList" :key="item.classCode" :value="item.classCode">
            {{ item.className }}
          </a-select-option>
        </a-select>

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
import {findAllApi as findClassAllApi} from '/@/api/boozsoft/account/AccvoucherCdigestClass'
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findAllApi, findBukongCcode, findMaxCcode} from "/@/api/boozsoft/account/AccvoucherCdigest";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  PlusCircleOutlined,
  FormOutlined,
  FileSearchOutlined,
  CaretDownOutlined
} from '@ant-design/icons-vue'
//import {mul} from "/@/views/boozsoft/gdzc/gu-ding-zi-chan-add/calculation";
const mul=null

const ASelectOption = ASelect.Option

const emit = defineEmits(['register', 'save'])
const props = defineProps(['modelValue'])

const {
  createErrorModal
} = useMessage()

const formItems: any = ref({})

let changeBeforeModel: any = {}

const classList = ref([])

const dynamicTenantId = ref('')
const isState = ref('0')
const saveClick:any = ref(false)
const [register, {closeModal}] = useModalInner((data) => {
  saveClick.value=false
  dynamicTenantId.value = data.dynamicTenantId
  isState.value = data.isState
  //获取分类
  useRouteApi(findClassAllApi, {schemaName: dynamicTenantId})({}).then(res => {
    classList.value = res
  })
  // 方式2
  formItems.value = {
    id: data.data.id,
    ccode: data.data.ccode,
    content: data.data.content,
    classCode: data.data.classCode
  }

  if (data.isState=='0') {
    maxCode()
  }

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged: boolean = false

async function handleOk() {
  saveClick.value=true
  if (formItems.value.ccode == null || formItems.value.ccode == '') {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '编码不能为空！'
    })
    saveClick.value=false
    return false
  }
  if (formItems.value.content == null || formItems.value.content == '') {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '常用摘要内容不能为空！'
    })
    saveClick.value=false
    return false
  }
  const res = await useRouteApi(findAllApi, {schemaName: dynamicTenantId})({})
  const accList = res.items
  for (let i = 0; i < accList.length; i++) {
    const item: any = accList[i];
    if (formItems.value.ccode != changeBeforeModel.ccode && item.ccode == formItems.value.ccode) {
      createErrorModal({
        iconType: 'warning',
        title: '删除',
        content: '编码重复不允许添加！'
      })
      formItems.value.ccode = ''
      saveClick.value=false
      return false
    }
    if (formItems.value.content != changeBeforeModel.content && item.content == formItems.value.content) {
      createErrorModal({
        iconType: 'warning',
        title: '删除',
        content: '常用摘要内容重复不允许添加！'
      })
      formItems.value.content = ''
      saveClick.value=false
      return false
    }
  }
  if (formItems.value.classCode == '' || formItems.value.classCode == null) {
    formItems.value.classCode = '9999'
  }
  isChanged = !(formItems.value.ccode == changeBeforeModel.ccode
    && formItems.value.content == changeBeforeModel.content
    && formItems.value.classCode == changeBeforeModel.classCode)
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
  if (formItems.value.ccode == null || formItems.value.ccode == '') {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '编码不能为空！'
    })
    saveClick.value=false
    return false
  }
  if (formItems.value.content == null || formItems.value.content == '') {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '常用摘要内容不能为空！'
    })
    saveClick.value=false
    return false
  }
  const res = await useRouteApi(findAllApi, {schemaName: dynamicTenantId})({})
  const accList = res.items
  for (let i = 0; i < accList.length; i++) {
    const item: any = accList[i];
    if (formItems.value.ccode != changeBeforeModel.ccode && item.ccode == formItems.value.ccode) {
      createErrorModal({
        iconType: 'warning',
        title: '删除',
        content: '编码重复不允许添加！'
      })
      formItems.value.ccode = ''
      saveClick.value=false
      return false
    }
    if (formItems.value.content != changeBeforeModel.content && item.content == formItems.value.content) {
      createErrorModal({
        iconType: 'warning',
        title: '删除',
        content: '常用摘要内容重复不允许添加！'
      })
      formItems.value.content = ''
      saveClick.value=false
      return false
    }
  }
  if (formItems.value.classCode == '' || formItems.value.classCode == null) {
    formItems.value.classCode = '9999'
  }
  isChanged = !(formItems.value.ccode == changeBeforeModel.ccode
    && formItems.value.content == changeBeforeModel.content
    && formItems.value.classCode == changeBeforeModel.classCode)
  if (isChanged) {
    emit('save', unref(formItems))
    // closeModal()
    formItems.value = {}
    formItems.value.table=[]
    await maxCode()
    saveClick.value=false
    return true
  }
  // closeModal()
  // alert("没有改变过")
  saveClick.value=false
  return false
}

async function maxCode(){
  const item1 = await useRouteApi(findBukongCcode,{schemaName: dynamicTenantId})({})
  if (item1!=null && item1.ccode!=null && item1.ccode!=''){
    formItems.value.ccode = item1.ccode
  } else {
    const item2 = await useRouteApi(findMaxCcode, {schemaName: dynamicTenantId})({})
    if (item2 != null && item2.ccode != null && item2.ccode != '') {
      formItems.value.ccode = add(item2.ccode, 1)
    } else {
      formItems.value.ccode = '1'
    }
  }
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
