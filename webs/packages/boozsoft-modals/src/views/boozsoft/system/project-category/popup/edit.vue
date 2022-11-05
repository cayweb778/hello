<template>
  <BasicModal width="900px" v-bind="$attrs" title="项目栏目样式" @ok="handleOk()" @register="register" :canFullscreen="false">
    <template #title>
      <div style="display: flex;margin-top: 10px;margin-left: 10px;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='0'">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;项目栏目样式
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='1'">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;项目栏目样式
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='2'">
          <FileSearchOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;项目栏目样式
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 60px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/@/assets/images/project.png" style="height:80px;margin-right: 10px;"/>
      </div>
    </template>
    <div class="nc-open-content" >
      <div class="open-content-up" style="margin-top: 60px;">
        <div :class="isState=='2'?'is_show':''">
        <label>编码：</label>
        <a-input :disabled="true" v-model:value="itemConfig.base.projectCateCode" @input.native="checkCateCode()" @blur="repeatCateCode()" placeholder style="width: 20%" />
        <label>名称：</label>
        <a-input @blur="repeatCateName()" v-model:value="itemConfig.base.projectCateName" placeholder style="width: 50%" />
        <br />
        <br />
        </div>

        <div style="border-bottom: 1px solid #999999;">
          项目栏目设置
          <a-button v-if="isState!='2'" @click="createRow()" style="padding: 0px 4px; height: 20px;float: right;background-color: rgb(1, 129, 226);color: #fff" >
            <PlusOutlined/>
          </a-button>
        </div>
        <div style="height: 350px;overflow: auto !important;">
        <div v-for="(row,rowIndex) in itemConfig.base.table"
         style="margin-top: 5px;"
         :class="isState=='2'?'disableRow':rowIndex<8?'disableRow':''"
        >
          <a-input v-model:value="row.cname" type="text" placeholder="标题" style="width: 15%" />&nbsp;
          <a-select v-model:value="row.ctype" placeholder="类型" style="width: 10%;">
            <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
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
            <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
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
            <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
            <a-select-option
              v-for="baseInfo in baseInfoList"
              :key="baseInfo.baseName"
              :value="baseInfo.baseTable"
            >{{ baseInfo.baseName }}</a-select-option>
          </a-select>&nbsp;
          <a-select v-model:value="row.sourceColumn" :disabled="row.disableSourceNameSelect" placeholder="对应字段" style="width: 15%;">
            <a-select-option
              v-for="column in columnList"
              :key="column.cname"
              :value="column.ctitle"
            >{{ column.cname }}</a-select-option>
            <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
          </a-select>&nbsp;
          <a-select v-model:value="row.islist" placeholder="是否显示" style="width: 10%;">
            <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
            <a-select-option value="1">显示</a-select-option>
            <a-select-option value="0">不显示</a-select-option>
          </a-select>&nbsp;&nbsp;
          <DeleteOutlined v-if="!(rowIndex<8) && isState!='2'" @click="delRow(rowIndex)" style="color: rgb(1, 129, 226)" />
        </div>
        </div>
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
import { onMounted, ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {
  PlusCircleOutlined,
  FormOutlined,
  FileSearchOutlined,
  CaretDownOutlined,
  PlusOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'
import projectCategoryItemConfigModelHelper
  from "./helper/projectCategoryItemConfigModelHelper"

import {
  columnFindCate,
  findProCateByCode,
  findProCateByName,
  saveCate
} from '/@/api/project_category/project_category'
import { baseInfoFindAll,columnIsFlagFindTable } from '/@/api/base-info/base-info'
import {Select as ASelect, Input as AInput, message} from 'ant-design-vue'
import {useMessage} from "/@/hooks/web/useMessage";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";

const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option

const {
  createErrorModal
} = useMessage()

const emit=defineEmits(['register','save'])

let changeBeforeModel:any = {}

const isState = ref('0')
const saveClick:any = ref(false)

async function handleOk() {
  saveClick.value=true
  if (itemConfig.value.base.projectCateCode == null || itemConfig.value.base.projectCateCode == '') {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '编码不能为空！'
    })
    saveClick.value=false
    return false
  }
  if (itemConfig.value.base.projectCateName == null || itemConfig.value.base.projectCateName == '') {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '名称不能为空！'
    })
    saveClick.value=false
    return false
  }
  if (!(itemConfig.value.base.projectCateCode == changeBeforeModel.base.projectCateCode
    && itemConfig.value.base.projectCateName == changeBeforeModel.base.projectCateName
    && JSON.stringify(itemConfig.value.base.table) == JSON.stringify(changeBeforeModel.base.table))
  ) {
    emit('save', unref(itemConfig.value.base))
    closeModal()
    saveClick.value=false
    return true
  }
  console.log("没有改变过")
  closeModal()
  saveClick.value=false
  return false
}
async function handleOkAdd() {
  saveClick.value=true
  if (itemConfig.value.base.projectCateCode == null || itemConfig.value.base.projectCateCode == '') {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '编码不能为空！'
    })
    saveClick.value=false
    return false
  }
  if (itemConfig.value.base.projectCateName == null || itemConfig.value.base.projectCateName == '') {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '名称不能为空！'
    })
    saveClick.value=false
    return false
  }
  if (!(itemConfig.value.base.projectCateCode == changeBeforeModel.base.projectCateCode
    && itemConfig.value.base.projectCateName == changeBeforeModel.base.projectCateName
    && JSON.stringify(itemConfig.value.base.table) == JSON.stringify(changeBeforeModel.base.table))
  ) {
    // emit('save', unref(itemConfig.value.base))
    // closeModal()
    await useRouteApi(saveCate, {schemaName: dynamicTenantId})(itemConfig.value.base)
    message.success('保存成功！')
    itemConfig.value = changeBeforeModel
    saveClick.value=false
    return true
  }
  // console.log("没有改变过")
  // closeModal()
  message.success('保存成功！')
  itemConfig.value = changeBeforeModel
  saveClick.value=false
  return false
}
//查询集团管控状态
// const dist:any = ref([])
// async function findDatabase() {
//   const res = await findByDatabaseUniqueCode()
//   dist.value = res
// }

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
  // await findDatabase()
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
    columnList.value = []
  }
}
//根据基础档案查询基础档案栏目
const columnList: any = ref([])
async function reloadColumn(sourceName:any) {
  const res: any = await columnIsFlagFindTable(sourceName)
  columnList.value = res
}
//改变对应档案时
function changeSourceName(row:any) {
  reloadColumn(row.sourceName)
}

//
const { createBaseRow1: createBase8Row, createBaseRow, createProjectCategoryItemConfig } = projectCategoryItemConfigModelHelper()

// let a=createProjectCategoryItemConfig()
const itemConfig: any = ref(createProjectCategoryItemConfig())
const dynamicTenantId = ref()
const [register, { closeModal }] = useModalInner((data) => {
  saveClick.value=false
  dynamicTenantId.value = data.dynamicTenantId
  isState.value = data.isState
  reloadBaseInfo()
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

  // itemConfig.value.base.isControl = dist.value.isControl
  // itemConfig.value.base.isKeyword = dist.value.isKeyword
  // itemConfig.value.base.isOther = dist.value.isOther
  // itemConfig.value.base.isAuto = dist.value.isAuto

  //给栏目赋值
  if (data.data.projectCateCode != null && data.data.projectCateCode != '') {

    useRouteApi(columnFindCate,{schemaName: dynamicTenantId})(data.data.projectCateCode)
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
  // console.log(itemConfig.value.base)
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
async function repeatCateCode() {
  /*if(changeBeforeModel.base.projectCateCode!=undefined){
    return true
  }*/
  if (changeBeforeModel.base.projectCateCode != itemConfig.value.base.projectCateCode && itemConfig.value.base.projectCateCode != null && itemConfig.value.base.projectCateCode != '') {
    const res: any = await useRouteApi(findProCateByCode, {schemaName: dynamicTenantId})(itemConfig.value.base.projectCateCode)
    if (res.length != 0) {
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '编码已存在，请重新输入！'
      })
      // alert('项目大类编码已存在，请重新输入！')
      itemConfig.value.base.projectCateCode = ''
      console.log(false)
      return false
    }
  }
  return true
}

//验证大类名称是否重复
async function repeatCateName() {
  /*if(changeBeforeModel.base.projectCateName!=undefined && changeBeforeModel.base.projectCateName==itemConfig.value.base.projectCateName){
    return true
  }*/
  if (changeBeforeModel.base.projectCateName != itemConfig.value.base.projectCateName && itemConfig.value.base.projectCateName != null && itemConfig.value.base.projectCateName != '') {
    const res: any = await useRouteApi(findProCateByName, {schemaName: dynamicTenantId})(itemConfig.value.base.projectCateName)
    if (res.length != 0) {
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '名称已存在，请重新输入！'
      })
      // alert('项目大类名称已存在，请重新输入！')
      itemConfig.value.base.projectCateName = ''
      console.log(false)
      return false
    }
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
:deep(.disableRow input), :deep(.disableRow .ant-select) {
  background: whitesmoke;
  color: #a9a7a7;
  pointer-events: none;
}
</style>
<style lang="less" scoped>

:deep(.vben-basic-title) {
  color: #0096c7 !important;
}
.ant-modal-header{
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

.is_show{
  pointer-events: none;
  cursor: default;
}

.nc-open-content {
  input {
    width: 35%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
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
    padding-left: 2em;
    color: #535353;
    font-weight: bold;
    font-size: 13px;
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
