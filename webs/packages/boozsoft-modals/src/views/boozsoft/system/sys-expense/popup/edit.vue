<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="账户档案"
    @ok="handleOk()"
    @register="register"
    :canFullscreen="false"
  >
    <template #title>
      <div style="display: flex;margin-top: 10px;margin-left: 10px" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='0'">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;费用档案
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='1'">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;费用档案
        </span>
      </div>
    </template>
    <div class="nc-open-content" >
      <div class="open-content-up" style="text-align: center;margin-top: 50px;">

        <label>费用编码:</label>
        <a-input v-model:value="formItems.ccode" :disabled="isState!='0'" @blur="checkCode()" placeholder="费用编码" />
        <span class="red_span">*</span>
        <br/><br/>

        <label>费用名称:</label>
        <a-input v-model:value="formItems.cname" :disabled="isState!='0'" @blur="checkName()" placeholder="费用名称" />
        <span class="red_span">*</span>
        <br/><br/>

        <label>费用类型:</label>
        <a-select v-model:value="formItems.ctype" style="width: 50%;">
          <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
<!--          CGFY采购费用、XSFY销售费用、SCFY生产费用、QTFY其他费用-->
          <a-select-option value="CGFY">采购费用</a-select-option>
          <a-select-option value="XSFY">销售费用</a-select-option>
          <a-select-option value="SCFY">生产费用</a-select-option>
          <a-select-option value="QTFY">其他费用</a-select-option>
        </a-select>
        <span class="red_span">*</span>
        <br/><br/>

        <label>税率:</label>
        <a-input v-model:value="formItems.itax" />
        <span class="red_span"></span>
        <br/><br/>

        <label>是否分摊:</label>
        <a-select v-model:value="formItems.isFentan" style="width: 50%;">
          <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
          <a-select-option value="1">是</a-select-option>
          <a-select-option value="0">否</a-select-option>
        </a-select>
        <span class="red_span"></span>
        <br/><br/>

        <label>分摊方式:</label>
        <a-select v-model:value="formItems.fentanType" style="width: 50%;">
          <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
          <a-select-option value="1">按金额</a-select-option>
          <a-select-option value="2">按数量</a-select-option>
        </a-select>
        <span class="red_span"></span>
        <br/><br/>

      </div>
    </div>

    <template #footer>
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
import {Select as ASelect,Input as AInput} from "ant-design-vue"
import {useMessage} from "/@/hooks/web/useMessage";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {
  PlusCircleOutlined,
  FormOutlined,
  FileSearchOutlined,
  CaretDownOutlined
} from '@ant-design/icons-vue'
import {
  findBukongCcode,
  findByCcode,
  findByCname,
} from "/@/api/record/system/sys-expense";
const ASelectOption = ASelect.Option

const {
  createErrorModal
} = useMessage()

const emit=defineEmits(['register','save'])

const formItems:any = ref({})

let changeBeforeModel:any = {}

const isState = ref('0')
const saveClick:any = ref(false)

const dynamicTenantId = ref()
const [register, { closeModal }] = useModalInner((data) => {
  saveClick.value=false
  dynamicTenantId.value = data.dynamicTenantId
  isState.value = data.isState
  // 方式2
  formItems.value = {
    id: data.data.id,
    ccode: data.data.ccode,
    cname: data.data.cname,
    ctype: data.data.ctype,
    itax: data.data.itax,
    isFentan: data.data.isFentan,
    fentanType: data.data.fentanType,
    flag: data.data.flag,
  }
  if (isState.value=='0'){
    maxCode()
  }

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})

async function maxCode(){
  const item1 = await useRouteApi(findBukongCcode,{schemaName: dynamicTenantId})({})
  if (item1!=null && item1.ccode!=null && item1.ccode!=''){
    formItems.value.ccode = pad(item1.ccode,3)
  } else {
    formItems.value.ccode = '001'
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

let isChanged:boolean = false
async function handleOk() {
  saveClick.value=true
  isChanged = !(formItems.value.ccode == changeBeforeModel.ccode
    && formItems.value.cname == changeBeforeModel.cname
    && formItems.value.ctype == changeBeforeModel.ctype
    && formItems.value.itax == changeBeforeModel.itax
    && formItems.value.isFentan == changeBeforeModel.isFentan
    && formItems.value.fentanType == changeBeforeModel.fentanType
    && formItems.value.flag == changeBeforeModel.flag)
  if (formItems.value.ccode==undefined || formItems.value.ccode=='') {
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '请输入费用编码！'
    })
    saveClick.value=false
    return false
  }
  if (formItems.value.cname==undefined || formItems.value.cname=='') {
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '请输入费用名称！'
    })
    saveClick.value=false
    return false
  }
  if (formItems.value.ctype==undefined || formItems.value.accStyle=='') {
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '请输入费用类型！'
    })
    saveClick.value=false
    return false
  }
  if(isChanged){
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
  isChanged = !(formItems.value.ccode == changeBeforeModel.ccode
    && formItems.value.cname == changeBeforeModel.cname
    && formItems.value.ctype == changeBeforeModel.ctype
    && formItems.value.itax == changeBeforeModel.itax
    && formItems.value.isFentan == changeBeforeModel.isFentan
    && formItems.value.fentanType == changeBeforeModel.fentanType
    && formItems.value.flag == changeBeforeModel.flag)
  if (formItems.value.ccode==undefined || formItems.value.ccode=='') {
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '请输入费用编码！'
    })
    saveClick.value=false
    return false
  }
  if (formItems.value.cname==undefined || formItems.value.cname=='') {
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '请输入费用名称！'
    })
    saveClick.value=false
    return false
  }
  if (formItems.value.ctype==undefined || formItems.value.accStyle=='') {
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '请输入费用类型！'
    })
    saveClick.value=false
    return false
  }
  if(isChanged){
    emit('save', unref(formItems))
    formItems.value = {
      status: '1'
    }
    changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
    await maxCode()
    // closeModal()
    saveClick.value=false
    return true
  }
  // closeModal()
  // alert("没有改变过")
  saveClick.value=false
  return false
}

async function checkCode(){
  if((changeBeforeModel.ccode!=undefined && changeBeforeModel.ccode!='') || changeBeforeModel.cname==formItems.value.ccode){
    return false
  }
  if(formItems.value.ccode == ''){
    return false
  }
  const res = await useRouteApi(findByCcode,{schemaName: dynamicTenantId})(formItems.value.ccode)
  if(res.length!=0){
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '费用编码已存在，请重新输入！'
    })
    formItems.value.ccode = ''
    console.log(false)
    return false
  }
  return true
}

async function checkName(){
  if((changeBeforeModel.cname!=undefined && changeBeforeModel.cname!='') || changeBeforeModel.cname==formItems.value.cname){
    return false
  }
  if(formItems.value.cname == ''){
    return false
  }
  const res = await useRouteApi(findByCname,{schemaName: dynamicTenantId})(formItems.value.cname)
  if(res.length!=0){
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '费用名称已存在，请重新输入！'
    })
    formItems.value.cname = ''
    console.log(false)
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
