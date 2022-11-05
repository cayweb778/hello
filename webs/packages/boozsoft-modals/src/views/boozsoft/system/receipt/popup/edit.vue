<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="财务收据"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
    >
      <div class="open-content-up" style="height: 600px">

        <label>收据编码</label>
        <a-input v-model:value="itemConfig.base.receCode" @blur="checkCode()" placeholder="收据编码" />
        <label>收据名称</label>
        <a-input v-model:value="itemConfig.base.receName" placeholder="收据名称" />
        <br/>
        <label>开票日期</label>
        <a-date-picker v-model:value="itemConfig.base.receDate" placeholder="开票日期" style="width: 35%" />
        <label>开票人</label>
        <a-input v-model:value="itemConfig.base.recePsn" placeholder="开票人" />
        <br/>
        <label>购方名称</label>
        <a-input v-model:value="itemConfig.base.buyName" placeholder="购方名称" />
        <label>销方名称</label>
        <a-input v-model:value="itemConfig.base.salesName" placeholder="销方名称" />
        <br/>
        <label>合计金额</label>
        <a-input v-model:value="itemConfig.base.totalAmount" placeholder="合计金额" />
        <br/><br/>

        <div style="border-bottom: 1px solid rgb(1, 129, 226);font-size: 16px;">
          开票详情
          <a-button @click="createRow()"
                    style="padding: 0px 4px; height: 20px;float: right;background-color: rgb(1, 129, 226);color: #fff">
            <PlusOutlined/>
          </a-button>
        </div>
        <div v-for="(row,rowIndex) in itemConfig.base.table" style="margin-top: 5px;">
          <a-input v-model:value="row.stockName" type="text" placeholder="商品品名及规格" style="width: 30%"/>&nbsp;
          <a-input type="number" v-model:value="row.stockNum" placeholder="数量" @blur="countAmout(row)" style="width: 15%"/>&nbsp;
          <a-input v-model:value="row.unit" type="text" placeholder="单位" style="width: 15%"/>&nbsp;
          <a-input type="number" v-model:value="row.price" placeholder="单价" @blur="countAmout(row)" style="width: 15%"/>&nbsp;
          <a-input type="number" v-model:value="row.amount" placeholder="总价" style="width: 15%"/>&nbsp;
          <DeleteOutlined @click="delRow(row,rowIndex)" style="color: rgb(1, 129, 226)"/>
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
import ReceiptItemConfigModelHelper from "./helper/receiptItemConfigModelHelper";
import {findByReceCode, findReceiptByUniqueCode} from "/@/api/record/system/receipt";

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

const {createBaseRow, createReceiptItemConfig} = ReceiptItemConfigModelHelper()
const itemConfig:any = ref(createReceiptItemConfig())

const [register, {closeModal}] = useModalInner((data) => {
  //给头部赋值
  itemConfig.value.base.id = data.data.id
  itemConfig.value.base.uniqueCode = data.data.uniqueCode
  itemConfig.value.base.receCode = data.data.receCode
  itemConfig.value.base.receName = data.data.receName
  itemConfig.value.base.receClass = data.data.receClass
  itemConfig.value.base.receDate = data.data.receDate
  itemConfig.value.base.buyName = data.data.buyName
  itemConfig.value.base.salesName = data.data.salesName
  itemConfig.value.base.recePsn = data.data.recePsn
  itemConfig.value.base.totalAmount = data.data.totalAmount
  itemConfig.value.base.vouchCode = data.data.vouchCode
  //给栏目赋值
  if (data.data.uniqueCode!=null&&data.data.uniqueCode!='') {
    findReceiptByUniqueCode(data.data.uniqueCode)
      .then(list => {
        itemConfig.value.base.table = list
        //如果没有值默认给一行
        if (!list){
          createRow()
        }
        changeBeforeModel = JSON.parse(JSON.stringify(itemConfig.value.base))
    })
  } else {
    itemConfig.value.base.table=[]
    createRow()
    changeBeforeModel = JSON.parse(JSON.stringify(itemConfig.value.base))
  }
  // changeBeforeModel = JSON.parse(JSON.stringify(itemConfig.value.base))
})
//添加行
function createRow() {
  itemConfig.value.base.table.push(createBaseRow())
}
//删除行
function delRow(row:any ,rowIndex:any) {
  console.log(row)
  itemConfig.value.base.table.splice(rowIndex, 1);
}

function countAmout(row:any){
  let num = 0
  if (row.stockNum!=''){
    num = parseFloat(row.stockNum)
  }
  let price = 0
  if (row.price!=''){
    price = parseFloat(row.price)
  }
  row.amount = (num*price).toFixed(2)
  let table = itemConfig.value.base.table
  let sum = 0
  table.forEach(function (res:any) {
    sum = sum + parseFloat(res.amount)
  })
  itemConfig.value.base.totalAmount = sum.toFixed(2)
}


async function checkCode(){
  if(changeBeforeModel.receCode!=undefined || changeBeforeModel.receCode==itemConfig.value.base.receCode){
    return true
  }
  if(itemConfig.value.base.receCode == ''){
    return false
  }
  const res = await findByReceCode(itemConfig.value.base.receCode)
  if(res.length!=0){
    alert('收据编码已存在，请重新输入！')
    itemConfig.value.base.receCode = ''
    console.log(false)
    return false
  }
  return true
}
</script>
<style lang="less" scoped>
:deep(.ant-calendar-picker-input.ant-input),:deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input){
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
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
