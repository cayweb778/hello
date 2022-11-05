<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="银行对账单模板"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
    >
      <div class="open-content-up" style="height: 600px">

        <label style="width: 200px">模板名称</label>
        <a-input v-model:value="itemConfig.base.templateName" @blur="checkName()" placeholder="请输入模板名称" style="font-size: 16px" />
        <br/>
        <label style="width: 200px">银行对账单标题栏起始行</label>
        <a-input v-model:value="itemConfig.base.templateTitleStart" placeholder="银行对账单标题栏起始行" style="font-size: 16px" />
        <br/><br/>

        <div style="border-bottom: 1px solid rgb(1, 129, 226)">
          栏目设置
        </div>
        <div style="margin-top: 5px;">
          <label style="width: 30%;text-align: center;font-size: 18px;">系统字段名称</label>&nbsp;
          <label style="width: 30%;text-align: center;font-size: 18px;">银行对账单名称</label>&nbsp;
          <label style="width: 30%;text-align: center;font-size: 18px;">类型</label>&nbsp;
<!--          <label style="width: 25%;text-align: center;font-size: 18px;">样式</label>-->
        </div>
        <div v-for="(row) in itemConfig.base.table" style="margin-top: 5px;">
<!--          <a-input v-model:value="row.systemField" type="text" placeholder="系统字段名称" style="width: 25%;"/>-->
          <label style="width: 30%;padding-right: 20px;">{{ row.systemField }}:</label>&nbsp;
          <a-input v-model:value="row.billField" type="text" placeholder="银行对账单名称" style="width: 30%"/>&nbsp;
          <a-select v-model:value="row.billType" placeholder="类型" style="width: 30%;">
            <a-select-option value="文本">文本</a-select-option>
            <a-select-option value="日期">日期</a-select-option>
            <a-select-option value="金额">金额</a-select-option>
          </a-select>&nbsp;
<!--          <a-input v-model:value="row.billStyle" type="text" placeholder="样式" style="width: 25%"/>-->
        </div>

      </div>
    </div>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {
  PlusOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'
import bankTemplateItemConfigModelHelper
  from "./helper/bankTemplateItemConfigModelHelper"
import {findByColumn,findByName} from '/@/api/record/system/bank-template'

const emit=defineEmits([])
let changeBeforeModel:any = {}
let isChanged:boolean = false
async function handleOk() {
  isChanged = !(JSON.stringify(itemConfig.value.base)==JSON.stringify(changeBeforeModel))
  if(isChanged){
    emit('save', unref(itemConfig.value.base))
    closeModal()
    return true
  }
  closeModal()
  // alert("没有改变过")
  return false
}

const {createBaseRow,createBaseRow1, createBankTemplateItemConfig} = bankTemplateItemConfigModelHelper()
const itemConfig:any = ref(createBankTemplateItemConfig())
const [register, {closeModal}] = useModalInner((data) => {
  //给头部赋值
  itemConfig.value.base.id = data.data.id
  itemConfig.value.base.templateName = data.data.templateName
  itemConfig.value.base.templateEnName = data.data.templateEnName
  itemConfig.value.base.templateTitleStart = data.data.templateTitleStart
  itemConfig.value.base.flag = '1'
  //给栏目赋值
  if (data.data.id!=null&&data.data.id!='') {
    findByColumn(data.data.id)
      .then(list => {
        itemConfig.value.base.table = list
        //如果没有值默认给一行
        if (!list){
          itemConfig.value.base.table = createBaseRow1()
        }
        changeBeforeModel = JSON.parse(JSON.stringify(itemConfig.value.base))
    })
  } else {
    itemConfig.value.base.table = createBaseRow1()

    changeBeforeModel = JSON.parse(JSON.stringify(itemConfig.value.base))
  }
})

async function checkName(){
  if((changeBeforeModel.templateName!=undefined && changeBeforeModel.templateName!='') || changeBeforeModel.templateName==itemConfig.value.base.bankCode){
    return true
  }
  if(itemConfig.value.base.templateName == ''){
    return false
  }
  const res = await findByName(itemConfig.value.base.templateName)
  if(res.length!=0){
    alert('银行编码已存在，请重新输入！')
    itemConfig.value.base.templateName = ''
    console.log(false)
    return false
  }
  return true
}

</script>
<style lang="less">
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
    width: 35%;
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
    text-align: right;
    width: 110px;
    display: inline-block;
    padding: 5px 10px;
    font-size: 16px;
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
