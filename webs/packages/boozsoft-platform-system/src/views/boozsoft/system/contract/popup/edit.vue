<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="合同信息"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%;overflow: auto;"
    >
      <div class="open-content-up">

        <label>合同编号</label>
        <a-input v-model:value="itemConfig.base.conNum" placeholder="合同编号" />
        <label>合同名称</label>
        <a-input v-model:value="itemConfig.base.conName" placeholder="合同名称" />
        <br>

        <label>签订日期</label>
        <a-date-picker v-model:value="itemConfig.base.conDate" placeholder="签订日期" format="YYYY-MM-DD" style="width: 35%;" />
        <label>生效日期</label>
        <a-date-picker v-model:value="itemConfig.base.shengxiaoDate" placeholder="生效日期" format="YYYY-MM-DD" style="width: 35%;" />
        <br>

        <label>开始日期</label>
        <a-date-picker v-model:value="itemConfig.base.startDate" placeholder="开始日期" format="YYYY-MM-DD" style="width: 35%;" />
        <label>结束日期</label>
        <a-date-picker v-model:value="itemConfig.base.endDate" placeholder="结束日期" format="YYYY-MM-DD" style="width: 35%;" />
        <br>

        <label>合同金额</label>
        <a-input type="number" v-model:value="itemConfig.base.amount" placeholder="合同金额" />
        <label>首付金额</label>
        <a-input type="number" v-model:value="itemConfig.base.firstAmount" placeholder="首付金额" />
        <br>

        <label>履行地点</label>
        <a-input v-model:value="itemConfig.base.transAddress" placeholder="履行地点" />
        <label>履行方式</label>
        <a-input v-model:value="itemConfig.base.transType" placeholder="履行方式" />
        <br>

        <label>履行期限</label>
        <a-input v-model:value="itemConfig.base.transTerm" placeholder="履行期限" />
        <label>是否结束</label>
        <a-select v-model:value="itemConfig.base.status" placeholder="是否结束" style="width: 35%;">
          <a-select-option value="1">是</a-select-option>
          <a-select-option value="0">否</a-select-option>
        </a-select>
        <br>

        <label>合同存放位置</label>
        <a-input v-model:value="itemConfig.base.saveAddress" placeholder="合同存放位置" />
        <label>合同保管人</label>
        <a-input v-model:value="itemConfig.base.safekeepPsn" placeholder="合同保管人" />
      </div>

      <div class="open-content-down" style="margin-top: 10px">
        <a-tabs
          v-model:activeKey="activeKey"
          type="card"
        >
          <a-tab-pane
            key="1"
            tab="合作方信息"
          >
            <div class="down-tab-content">
              <a-button @click="show()"
                        style="padding: 0px 4px; height: 20px;float: right;background-color: rgb(1, 129, 226);color: #fff">
                <PlusOutlined/>
              </a-button>
              <div>
                <label>甲方全称</label>
                <a-input v-model:value="itemConfig.base.jiaName" placeholder="甲方全称" />
                <label>甲方地址</label>
                <a-input v-model:value="itemConfig.base.jiaAddress" placeholder="甲方地址" />
                <br/>
                <label>甲方税号</label>
                <a-input v-model:value="itemConfig.base.jiaShuihao" placeholder="甲方税号" />
                <label>甲方负责人</label>
                <a-input v-model:value="itemConfig.base.jiaPerson" placeholder="甲方负责人" style="width: 15%;" />&nbsp;
                <a-input v-model:value="itemConfig.base.jiaPhone" placeholder="联系方式" style="width: 20%;" />
              </div>

              <div style="margin-top: 15px;">
                <label>乙方全称</label>
                <a-input v-model:value="itemConfig.base.yiName" placeholder="乙方全称" />
                <label>乙方地址</label>
                <a-input v-model:value="itemConfig.base.yiAddress" placeholder="乙方地址" />
                <br/>
                <label>乙方税号</label>
                <a-input v-model:value="itemConfig.base.yiShuihao" placeholder="乙方税号" />
                <label>乙方负责人</label>
                <a-input v-model:value="itemConfig.base.yiPerson" placeholder="乙方负责人" style="width: 15%;" />&nbsp;
                <a-input v-model:value="itemConfig.base.yiPhone" placeholder="联系方式" style="width: 20%;" />
              </div>

              <div style="margin-top: 15px;" v-if="bing">
                <label>丙方全称</label>
                <a-input v-model:value="itemConfig.base.bingName" placeholder="丙方全称" />
                <label>丙方地址</label>
                <a-input v-model:value="itemConfig.base.bingAddress" placeholder="丙方地址" />
                <br/>
                <label>丙方税号</label>
                <a-input v-model:value="itemConfig.base.bingShuihao" placeholder="丙方税号" />
                <label>丙方负责人</label>
                <a-input v-model:value="itemConfig.base.bingPerson" placeholder="丙方负责人" style="width: 15%;" />&nbsp;
                <a-input v-model:value="itemConfig.base.bingPhone" placeholder="联系方式" style="width: 20%;" />
              </div>

              <div style="margin-top: 15px;" v-if="ding">
                <label>丁方全称</label>
                <a-input v-model:value="itemConfig.base.dingName" placeholder="丁方全称" />
                <label>丁方地址</label>
                <a-input v-model:value="itemConfig.base.dingAddress" placeholder="丁方地址" />
                <br/>
                <label>丁方税号</label>
                <a-input v-model:value="itemConfig.base.dingShuihao" placeholder="丁方税号" />
                <label>丁方负责人</label>
                <a-input v-model:value="itemConfig.base.dingPerson" placeholder="丁方负责人" style="width: 15%;" />&nbsp;
                <a-input v-model:value="itemConfig.base.dingPhone" placeholder="联系方式" style="width: 20%;" />
              </div>
            </div>
          </a-tab-pane>

          <a-tab-pane
            key="2"
            tab="标的信息"
          >
            <div class="down-tab-content">
              <a-button @click="createRow()"
                    style="padding: 0px 4px; height: 20px;float: right;background-color: rgb(1, 129, 226);color: #fff">
                <PlusOutlined/>
              </a-button><br/>
              <div v-for="(row,rowIndex) in itemConfig.base.table" >
                <a-input v-model:value="row.biaodiCount" type="number" placeholder="数量" style="width: 10%"/>&nbsp;
                <a-input v-model:value="row.biaodiQuality" type="number" placeholder="质量" style="width: 10%"/>&nbsp;
                <a-input v-model:value="row.biaodiPrice" type="number" placeholder="价格" style="width: 10%"/>&nbsp;
                <a-input v-model:value="row.tiaokuan" type="text" placeholder="条款" style="width: 10%"/>&nbsp;
                <a-input v-model:value="row.zeren" type="text" placeholder="违约责任" style="width: 10%"/>&nbsp;
                <a-input v-model:value="row.transTerm" type="text" placeholder="履行期限" style="width: 10%"/>&nbsp;
                <a-input v-model:value="row.transAddress" type="text" placeholder="履行地点" style="width: 10%"/>&nbsp;
                <a-input v-model:value="row.transType" type="text" placeholder="履行方式" style="width: 10%"/>&nbsp;
                <a-input v-model:value="row.solution" type="text" placeholder="解决方案" style="width: 10%"/>&nbsp;&nbsp;
                <DeleteOutlined @click="delRow(row,rowIndex)" style="color: rgb(1, 129, 226)"/>
              </div>
            </div>
          </a-tab-pane>
        </a-tabs>
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
import contractItemConfigModelHelper
  from "./helper/contractItemConfigModelHelper";
import {findByBiaodi} from "/@/api/record/system/contract";

const emit=defineEmits([])
let changeBeforeModel:any = {}
let isChanged:boolean = false

const {createBaseRow, createContractItemConfig} = contractItemConfigModelHelper()
const itemConfig:any = ref(createContractItemConfig())

//添加行
function createRow() {
  itemConfig.value.base.table.push(createBaseRow())
}
//删除行
function delRow(row:any ,rowIndex:any) {
  console.log(row)
  itemConfig.value.base.table.splice(rowIndex, 1);
}

const bing = ref(false)
const ding = ref(false)
const [register, { closeModal }] = useModalInner((data) => {
  // 给合同赋值
  itemConfig.value.base.id = data.data.id
  itemConfig.value.base.conDate = data.data.conDate
  itemConfig.value.base.conNum = data.data.conNum
  itemConfig.value.base.shengxiaoDate = data.data.shengxiaoDate
  itemConfig.value.base.startDate = data.data.startDate
  itemConfig.value.base.endDate = data.data.endDate
  itemConfig.value.base.conName = data.data.conName
  itemConfig.value.base.amount = data.data.amount
  itemConfig.value.base.firstAmount = data.data.firstAmount
  itemConfig.value.base.transAddress = data.data.transAddress
  itemConfig.value.base.transType = data.data.transType
  itemConfig.value.base.transTerm = data.data.transTerm
  itemConfig.value.base.status = data.data.status
  itemConfig.value.base.saveAddress = data.data.saveAddress
  itemConfig.value.base.safekeepPsn = data.data.safekeepPsn
  itemConfig.value.base.jiaName = data.data.jiaName
  itemConfig.value.base.jiaAddress = data.data.jiaAddress
  itemConfig.value.base.jiaShuihao = data.data.jiaShuihao
  itemConfig.value.base.jiaPerson = data.data.jiaPerson
  itemConfig.value.base.jiaPhone = data.data.jiaPhone
  itemConfig.value.base.yiName = data.data.yiName
  itemConfig.value.base.yiAddress = data.data.yiAddress
  itemConfig.value.base.yiShuihao = data.data.yiShuihao
  itemConfig.value.base.yiPerson = data.data.yiPerson
  itemConfig.value.base.yiPhone = data.data.yiPhone
  itemConfig.value.base.bingName = data.data.bingName
  itemConfig.value.base.bingAddress = data.data.bingAddress
  itemConfig.value.base.bingShuihao = data.data.bingShuihao
  itemConfig.value.base.bingPerson = data.data.bingPerson
  itemConfig.value.base.bingPhone = data.data.bingPhone
  itemConfig.value.base.dingName = data.data.dingName
  itemConfig.value.base.dingAddress = data.data.dingAddress
  itemConfig.value.base.dingShuihao = data.data.dingShuihao
  itemConfig.value.base.dingPerson = data.data.dingPerson
  itemConfig.value.base.dingPhone = data.data.dingPhone
  itemConfig.value.base.vouchCode = data.data.vouchCode

  if(data.data.bingName==null || data.data.bingName==''){
    bing.value = false
  } else {
    bing.value = true
  }
  if(data.data.dingName==null || data.data.dingName==''){
    ding.value = false
  } else {
    ding.value = true
  }

  //给合同标的赋值
  if (data.data.id!=null&&data.data.id!='') {
    findByBiaodi(data.data.id).then(list => {
      itemConfig.value.base.table = list
      //如果没有值默认给一行
      if (list.length==0){
        createRow()
      }
      changeBeforeModel = JSON.parse(JSON.stringify(itemConfig.value.base))
    })
  } else {
    itemConfig.value.base.table=[]
    createRow()
    changeBeforeModel = JSON.parse(JSON.stringify(itemConfig.value.base))
  }
})
function show(){
  if (bing.value == false) {
    bing.value = true
  } else {
    ding.value = true
  }
}

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

const activeKey = ref('1')

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
