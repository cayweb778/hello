<template>
  <BasicModal width="900px" v-bind="$attrs" title="项目大类" @ok="handleOk()" @register="register">
    <div class="nc-open-content">
      <div class="open-content-up" style="height: 600px">
        <label>大类编码</label>
        <a-input :disabled="true" v-model:value="itemConfig.base.projectCateCode" @input.native="checkCateCode()" @blur="repeatCateCode()" placeholder style="width: 20%" />
        <label>大类名称</label>
        <a-input :disabled="true" v-model:value="itemConfig.base.projectCateName" placeholder style="width: 50%" />
        <br />
        <br />

        <div style="border-bottom: 1px solid rgb(1, 129, 226)">
          项目栏目设置
<!--          <a-button
            @click="createRow()"
            style="padding: 0px 4px; height: 20px;float: right;background-color: rgb(1, 129, 226);color: #fff"
          >
            <PlusOutlined />
          </a-button>-->
        </div>
        <div v-for="(row,rowIndex) in itemConfig.base.table"
         style="margin-top: 5px;"
         :class="'disableRow'"
        >
          <a-input v-model:value="row.cname" type="text" placeholder="标题" style="width: 15%" />&nbsp;
          <a-select v-model:value="row.ctype" placeholder="类型" style="width: 10%;">
            <a-select-option value="1">文本</a-select-option>
            <a-select-option value="2">整数</a-select-option>
            <a-select-option value="3">实数</a-select-option>
            <a-select-option value="4">日期</a-select-option>
            <a-select-option value="5">是否</a-select-option>
          </a-select>&nbsp;
          <a-input v-model:value="row.clength" @input.native="checkLength(row)" type="number" placeholder="长度" style="width: 10%;" />&nbsp;
          <a-select
            v-model:value="row.sourceType"
            placeholder="数据来源"
            style="width: 15%;"
            @change="changeSourceType(row)"
          >
            <a-select-option value="1">手工输入</a-select-option>
            <a-select-option value="2">系统档案</a-select-option>
          </a-select>&nbsp;
          <a-select
            v-model:value="row.sourceName"
            :disabled="row.disableSourceNameSelect"
            placeholder="对应档案"
            style="width: 15%;"
            @change="changeSourceName(row)"
          >
            <a-select-option
              v-for="baseInfo in baseInfoList"
              :key="baseInfo.baseName"
              :value="baseInfo.baseTable"
            >{{ baseInfo.baseName }}</a-select-option>
          </a-select>&nbsp;
          <a-select v-model:value="row.sourceColumn" :disabled="row.disableSourceNameSelect" placeholder="对应字段" style="width: 15%;">
            <a-select-option
              v-for="column in colunmList"
              :key="column.cname"
              :value="column.ctitle"
            >{{ column.cname }}</a-select-option>
          </a-select>&nbsp;
          <a-select v-model:value="row.islist" placeholder="是否显示" style="width: 10%;">
            <a-select-option value="1">显示</a-select-option>
            <a-select-option value="0">不显示</a-select-option>
          </a-select>&nbsp;&nbsp;
        </div>
        <div style="border-top: 1px solid rgb(1, 129, 226);margin-top: 30px;">
          <a-select v-model:value="itemConfig.base.successState" placeholder="审批状态" style="width:20%;">
            <a-select-option value="1">同意</a-select-option>
            <a-select-option value="0">不同意</a-select-option>
          </a-select>&nbsp;&nbsp;
          <label>审核信息</label>
          <a-input v-model:value="itemConfig.base.approveIdea" style="width: 60%" />
        </div>
      </div>
    </div>
  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import { onMounted, ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {
  PlusOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'
import projectCategoryItemConfigModelHelper
  from "./helper/projectCategoryItemConfigModelHelper"

import { columnFindCate,findByDatabaseUniqueCode,findProCateByCode } from '/@/api/sys_project_category/project_category'
import { baseInfoFindAll,columnIsFlagFindTable } from '/@/api/base-info/base-info'
import {Select as ASelect,Input as AInput  } from 'ant-design-vue'

const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option

const emit=defineEmits(['register'])

let changeBeforeModel:any = {}

async function handleOk() {
    emit('approveOk', unref(itemConfig.value.base))
    closeModal()
}
//查询集团管控状态
const dist:any = ref([])
async function findDatabase() {
  const res = await findByDatabaseUniqueCode()
  dist.value = res
}

const castRowsRenderData = (list: any) =>
  list.forEach((row: any) => {
    if (row.sourceType == '1') {
      row.disableSourceNameSelect = true
    } else {
      row.disableSourceNameSelect = false
    }
    if(row.sourceName!=""){
      reloadColumn(row.sourceName)
    }
  })


//基础档案列表
const baseInfoList: any = ref([])
async function reloadBaseInfo() {
  const res: any = await baseInfoFindAll()
  baseInfoList.value = res.items
}
onMounted(async () => {
  await reloadBaseInfo()
  await findDatabase()
})
//改变输入方式时
function changeSourceType(row: any) {
  const sourceType = row.sourceType
  if (sourceType == '2') {
    row.disableSourceNameSelect = false
  } else {
    row.sourceName=''
    row.sourceColumn=''
    row.disableSourceNameSelect = true
    colunmList.value = []
  }
}
//根据基础档案查询基础档案栏目
const colunmList:any = ref([])
async function reloadColumn(sourceName:any) {
  const res: any = await columnIsFlagFindTable(sourceName)
  colunmList.value = res
}
//改变对应档案时
function changeSourceName(row:any) {
  reloadColumn(row.sourceName)
}

//
const { createBaseRow1: createBase8Row, createBaseRow, createProjectCategoryItemConfig } = projectCategoryItemConfigModelHelper()

// let a=createProjectCategoryItemConfig()
const itemConfig: any = ref(createProjectCategoryItemConfig())

const [register, { closeModal }] = useModalInner((data) => {
  //给头部赋值
  itemConfig.value.base.id = data.data.id
  itemConfig.value.base.projectCateCode = data.data.projectCateCode
  itemConfig.value.base.projectCateName = data.data.projectCateName
  itemConfig.value.base.projectTable = data.data.projectTable
  itemConfig.value.base.flag = data.data.flag
  itemConfig.value.base.successState = data.data.successState
  itemConfig.value.base.applyDatabaseUniqueCode = data.data.applyDatabaseUniqueCode
  itemConfig.value.base.applyUser = data.data.applyUser
  itemConfig.value.base.applyDate = data.data.applyDate
  itemConfig.value.base.approveUser = data.data.approveUser
  itemConfig.value.base.approveDate = data.data.approveDate
  itemConfig.value.base.biandongMethod = data.data.biandongMethod
  itemConfig.value.base.flag = data.data.flag

  itemConfig.value.base.successState = '1'

  itemConfig.value.base.isControl = dist.value.isControl
  itemConfig.value.base.isKeyword = dist.value.isKeyword
  itemConfig.value.base.isOther = dist.value.isOther
  itemConfig.value.base.isAuto = dist.value.isAuto

  //给栏目赋值
  if (data.data.projectCateCode != null && data.data.projectCateCode != '') {

    columnFindCate(data.data.projectCateCode)
    .then(res => {
      itemConfig.value.base.table = res
      if (!res) {
        //如果没有值默认给8行
        itemConfig.value.base.table = createBase8Row()
      }
      castRowsRenderData(itemConfig.value.base.table)
      changeBeforeModel = JSON.parse(JSON.stringify(itemConfig.value))

    })

  } else {
    itemConfig.value.base.table = createBase8Row()
    castRowsRenderData(itemConfig.value.base.table)
    changeBeforeModel = JSON.parse(JSON.stringify(itemConfig.value))

  }

})
//添加行
function createRow() {
  // debugger
  itemConfig.value.base.table.push(createBaseRow())
  castRowsRenderData(itemConfig.value.base.table)
}
//删除行
function delRow(rowIndex: any) {
  itemConfig.value.base.table.splice(rowIndex, 1)
  //数据库删除
  // deleteColumn(row)
}

//验证项目大类编码输入格式
function checkCateCode(){
  if(itemConfig.value.base.projectCateCode !== null){
    itemConfig.value.base.projectCateCode = itemConfig.value.base.projectCateCode.replace(/[^\w\.\/]/ig,'')
  }
}
//验证大类编码输入类型
function checkLength(row:any){
  if(row.clength !== null){
    row.clength = (Number(row.clength)<=60?row.clength:60)
  }
}
//验证大类编码是否重复
async function repeatCateCode(){
  if(changeBeforeModel.base.projectCateCode!=undefined){
    return true
  }
  const res:any = await findProCateByCode(itemConfig.value.base.projectCateCode)
  if(res.length!=0){
    alert('项目大类编码已存在，请重新输入！')
    itemConfig.value.base.projectCateCode = ''
    return false
  }
  return true
}

</script>
<style lang="less" scoped>
  :deep(.disableRow input),:deep(.disableRow .ant-select){
    background: whitesmoke;
    color: #a9a7a7;
    pointer-events: none;
  }
</style>
<style lang="less" scoped>

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
      content: "";
      pointer-events: none;
      background-color: rgb(1, 143, 251);
    }
  }
}

.ant-select-selection-search-input {
  border-bottom: none;
  background-color: #276aa7 !important;
  color: white;
  text-align: center;
  border-radius: 4px !important;
}
</style>
