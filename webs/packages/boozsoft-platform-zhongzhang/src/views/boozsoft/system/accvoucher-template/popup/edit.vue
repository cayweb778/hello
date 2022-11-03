<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%;overflow: hidden;"
    >
      <div class="open-content-up" style="margin-top: 10px;">
        <label>模板名称</label>
        <a-input v-model:value.trim="formItems.templateName" autocomplete="off" placeholder="模板名称" />
        <label>模板类型</label>
        <a-select v-model:value="formItems.templateType"  style="width: 120px;text-align: center"><a-select-option value="0" selected>简约</a-select-option><a-select-option value="1">标准</a-select-option></a-select>
        <a-divider style="border-color: #7cb305">请按照其他财务软件导出的凭证表，进行设置</a-divider>
        <ul style="margin-left: 10%;">
          <li><div class="ul-li-div ul-li-text" >系统字段名称</div><div class="ul-li-div ul-li-text">自定义字段名称</div><div class="ul-li-div ul-li-text"  style="font-size: 18px;">字段类型<a-button value="small" type="primary" style="margin: 0 2px;" @click="addRow()">+</a-button></div></li>
        </ul>
        <ul class="ul-special">
          <li v-for="(item,index) in (defaultList)" class="ul-li">
            <div class="ul-li-div"><a-input autocomplete="off" v-model:value="item.systemFieldName" readonly="" /></div>
            <div class="ul-li-div"><a-input autocomplete="off" v-model:value="item.customerFieldName" /></div>
            <div class="ul-li-div">
              <a-select  v-model:value="item.fieldType" class="head-content-select" style="width: 120px">
                <a-select-option value="TEXT">文本</a-select-option>
                <a-select-option value="NUMBER">整数</a-select-option>
                <a-select-option value="MONEY">金额</a-select-option>
                <a-select-option value="DATE">日期</a-select-option>
                <a-select-option value="BOOLEAN">是否</a-select-option>
              </a-select>
              <a-button value="small" type="primary" style="margin: 0 2px;" @click="delRow(index)">✖</a-button>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import { ref, unref ,watch} from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import { message } from 'ant-design-vue'
import {useMessage} from "/@/hooks/web/useMessage";
import { voucherFieldsApi } from '/@/api/record/system/accvoucher_template'
const {
  createConfirm,createWarningModal
} = useMessage()

import {DatePicker as ADatePicker,Select as ASelect,Input as AInput,Popover as APopover,Switch as ASwitch,
  Radio as ARadio,Upload as AUpload,List as AList,Row as ARow,Menu as AMenu,Dropdown as ADropdown,Card as ACard
  ,Col as ACol,Divider as ADivider} from "ant-design-vue"
const ARangePicker=ADatePicker.RangePicker
const ASelectOption=ASelect.Option
const AInputSearch=AInput.Search
const ARadioGroup=ARadio.Group
const AListItem=AList.Item
const AMenuItem=AMenu.Item

const emit=defineEmits(['register'])
const formItems:any = ref({})
const defaultList= ref([])

const [register, { closeModal,setModalProps }] = useModalInner(data => {
  // 修改时赋值
  formItems.value = data.data
  if (null != formItems.value.id && formItems.value.id != ''){
    defaultList.value = JSON.parse(formItems.value.entryList)
  }else {
      defaultList.value = reloadList(formItems.value.templateType)
  }
  setModalProps({ title: '凭证导入模板' ,minHeight: 600});
})

async function handleOk() {
  if (formItems.value.templateName === '') {
    message.error('模板名称不能为空!')
    return false
  } else {
    try{
      let entrys =  defaultList.value
      entrys.forEach((item)=>{
        if (item.systemFieldName == ''){
          throw new Error('系统字段名称为必填项！')
        }
      })
      formItems.value.entryList = JSON.stringify(defaultList.value)
      emit('save', unref(formItems))
      closeModal()
    }catch (e) {
      createWarningModal({content: e.message});
    }
  }
}
function reloadList(type) {
 return voucherFieldsApi({templateType: type})
}

function delRow(index) {
  defaultList.value.splice(index, 1)
}

function addRow() {
  // 获取最后一条数据
  let lastIndex = defaultList.value.length <= 0 ? -1 : defaultList.value.length - 1
  let number = 1;
  if (lastIndex != -1){
    let lastSName = defaultList.value[lastIndex].systemFieldName
    if (lastSName.indexOf('辅助项') != -1){
      number = parseInt(lastSName.replace('辅助项',''))+1
    }
  }
  // 不是
  if( number!=1  ){
    // 判断是否超过30可用
    let usedIndexs = []
    defaultList.value.forEach(item=>{
      if (item.systemFieldName.startsWith("辅助项") && parseFloat(item.systemFieldName.replace('辅助项','')).toString() != "NaN"){
        usedIndexs.push(parseFloat(item.systemFieldName.replace('辅助项','')))
      }
    })
    if (usedIndexs.length > 29){
      message.error("系统辅助项字段不得超过30个！")
      return false;
    }else {
      for (let i=1;i<=30;i++)
      {
        if (usedIndexs.indexOf(i) == -1){
          number = i;
          defaultList.value.push({customerFieldName:'',systemFieldName:'辅助项'+number,systemFieldNum: 'cdfine'+number,fieldType: 'TEXT'})
          return
        }
      }
    }
  }
  defaultList.value.push({customerFieldName:'',systemFieldName:'辅助项'+number,systemFieldNum: 'cdfine'+number,fieldType: 'TEXT'})
}
watch(
  () => formItems.value.templateType,
  (newValue, oldValue) => {
    if (oldValue != null){
      if(newValue!=oldValue){
        defaultList.value = reloadList(newValue)
      }
    }
  }
)
</script>
<style lang="less" scoped>

:deep(.ant-calendar-picker-input.ant-input),:deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input){
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
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
.scrollbar__view{
    min-height: 600px;
    height: 100%!important;
}
.nc-open-content {
  input {
    width: 35%;
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
    text-align: right;
    width: 110px;
    display: inline-block;
    padding: 5px 10px;
  }

  .open-content-down {
    margin-top: 5%;
  }
  .ul-li-div {
    display: inline-block;
    width: 30%;
    text-align: center;
    input {
      width: 200px;
    }
  }
  .ul-li-text{
    font-size: 18px;
    color: #0000FF;
  }
  .ul-special{
    margin-left: 10%;
    min-height: 400px;
    max-height: 454px;
    overflow-y: auto;
  }

}
</style>
