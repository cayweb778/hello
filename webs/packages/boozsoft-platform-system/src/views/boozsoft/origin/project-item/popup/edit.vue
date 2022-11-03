<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="项目大类"
    @ok="handleOk()"
    @register="register"
    :canFullscreen="false"
    :loading="loadMark"
  >
    <template #title>
      <div style="display: flex;margin-top: 10px;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='0'">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;项目大类
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='1'">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;项目大类
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='2'">
          <FileSearchOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;项目大类
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 60px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/@/assets/images/project.png" style="height:80px;margin-right: 10px;"/>
      </div>
    </template>
    <div class="nc-open-content" :class="isState=='2'?'is_show':''">
      <div class="open-content-up" style="text-align: center;margin-top: 60px;">

        <label>项目栏目样式：</label>
        <a-select v-model:value="formItems.cateCode" placeholder="请选择项目样式" style="width: 50%;">
          <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
          <a-select-option v-for="cate in cateList" :key="cate.projectCateCode" :value="cate.projectCateCode">
            ({{ cate.projectCateCode }}){{ cate.projectCateName }}
          </a-select-option>
        </a-select>
        <span class="red_span">*</span>
        <br/>

        <label>大类编码：</label>
        <a-input v-model:value="formItems.itemCode" :disabled="isState=='1'" @blur="checkCode()" placeholder="大类编码"/>
        <span class="red_span">*</span>

        <br/><br/><br/>

        <label style="font-size: 18px;margin-left: 0;width:130px;">大类名称：</label>
        <a-input v-model:value="formItems.itemName" @blur="checkName()" placeholder="大类名称" class="abc" style="width: 60%;" />
        <span class="red_span">*</span>

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
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {Input as AInput,Select as ASelect,message} from 'ant-design-vue'
import {
  PlusCircleOutlined,
  FormOutlined,
  FileSearchOutlined,
  CaretDownOutlined
} from '@ant-design/icons-vue'
import {useMessage} from "/@/hooks/web/useMessage";
import {findByCode, findByName, saveProjectItem} from "/@/api/record/system/origin-project-item";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {cateFindStateFlag} from "/@/api/group_project_category/project_category";
const ASelectOption = ASelect.Option

const {
  createErrorModal
} = useMessage()

const emit = defineEmits(['register', 'save'])

const formItems: any = ref({})

const isState = ref('0')
const saveClick:any = ref(false)

let changeBeforeModel: any = {}

const cateList = ref([])
const loadMark = ref(false)
const [register, {closeModal}] = useModalInner(async(data: any) => {
  loadMark.value = true
  saveClick.value=false
  isState.value = data.isState
  cateList.value = await cateFindStateFlag()

  // 方式2
  formItems.value = {
    id: data.data.id,
    itemCode: data.data.itemCode,
    itemName: data.data.itemName,
    flag: data.data.flag,
    cateCode: data.data.cateCode,
    origin: data.data.origin
  }
  changeBeforeModel = JSON.parse(JSON.stringify(formItems))
  loadMark.value = false
})
let isChanged:boolean = false
async function handleOk() {
  saveClick.value=true
  if (formItems.value.itemCode==''){
    createErrorModal({
      iconType: 'warning',
      title: '保存失败',
      content: '大类编码不能为空！'
    })
    saveClick.value=false
    return false
  }
  if (formItems.value.itemName == '') {
    createErrorModal({
      iconType: 'warning',
      title: '保存失败',
      content: '大类名称不能为空！'
    })
    saveClick.value=false
    return false
  }
  if (formItems.value.cateCode == null ||formItems.value.cateCode == '') {
    createErrorModal({
      iconType: 'warning',
      title: '保存失败',
      content: '项目栏目样式不能为空，请先设置栏目样式！'
    })
    saveClick.value=false
    return false
  }
  isChanged = !(formItems.value.itemCode == changeBeforeModel._value.itemCode
    && formItems.value.itemName == changeBeforeModel._value.itemName
    && formItems.value.cateCode == changeBeforeModel._value.cateCode)
  console.log(isChanged)
  if (isChanged) {
    emit('save', unref(formItems))
    closeModal()
    saveClick.value=false
    return true
  }
  closeModal()
  saveClick.value=false
  // alert("没有改变过")
  return false
}

async function handleOkAdd() {
  saveClick.value=true
  if (formItems.value.itemCode==null || formItems.value.itemCode==''){
    createErrorModal({
      iconType: 'warning',
      title: '保存失败',
      content: '大类编码不能为空！'
    })
    saveClick.value=false
    return false
  }
  if (formItems.value.itemName==null || formItems.value.itemName == '') {
    createErrorModal({
      iconType: 'warning',
      title: '保存失败',
      content: '大类名称不能为空！'
    })
    saveClick.value=false
    return false
  }
  if (formItems.value.cateCode == null ||formItems.value.cateCode == '') {
    createErrorModal({
      iconType: 'warning',
      title: '保存失败',
      content: '项目栏目样式不能为空！'
    })
    saveClick.value=false
    return false
  }
  isChanged = !(formItems.value.itemCode == changeBeforeModel._value.itemCode
    && formItems.value.itemName == changeBeforeModel._value.itemName
    && formItems.value.cateCode == changeBeforeModel._value.cateCode)
  if (isChanged) {
    emit('save', unref(formItems))
    // closeModal()
    message.success('保存成功！')
    formItems.value = {}
    changeBeforeModel = JSON.parse(JSON.stringify(formItems))
    saveClick.value=false
    return true
  }
  // closeModal()
  message.success('保存成功！')
  formItems.value = {}
  changeBeforeModel = JSON.parse(JSON.stringify(formItems))
  saveClick.value=false
  // alert("没有改变过")
  return false
}

async function checkCode() {
  if ((changeBeforeModel._value.itemCode != undefined && changeBeforeModel._value.itemCode != '') && changeBeforeModel._value.itemCode == formItems.value.itemCode) {
    return true
  }
  if (formItems.value.itemCode == null || formItems.value.itemCode == '') {
    return true
  }
  const res = await findByCode(formItems.value.itemCode,formItems.value.originId)
  if (res.length != 0) {
    createErrorModal({
      iconType: 'warning',
      title: '输入失败',
      content: '大类编码已存在，请重新输入！'
    })
    formItems.value.itemCode = ''
    return false
  }
  return true
}

async function checkName() {
  if ((changeBeforeModel._value.itemName != undefined && changeBeforeModel._value.itemName != '') && changeBeforeModel._value.itemName == formItems.value.itemName) {
    return true
  }
  if (formItems.value.itemName == null || formItems.value.itemName == '') {
    return true
  }
  const res = await findByName(formItems.value.itemName,formItems.value.originId)
  if (res.length != 0) {
    createErrorModal({
      iconType: 'warning',
      title: '输入失败',
      content: '大类名称已存在，请重新输入！'
    })
    formItems.value.itemName = ''
    return false
  }
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
    width: 120px;
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
