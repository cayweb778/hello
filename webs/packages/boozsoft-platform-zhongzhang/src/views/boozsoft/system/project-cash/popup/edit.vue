<template>
  <BasicModal
    width="900px"
    :height="400"
    :useWrapper="true"
    v-bind="$attrs"
    title="现金流量项目设置"
    @ok="handleOk()"
    @register="register"
  >

    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;" >
          <PlusCircleOutlined v-if="!showEdit" style="font-size: 34px;font-weight: bold"/>
          <FormOutlined v-if="showEdit" style="font-size: 34px;font-weight: bold"/>
            &nbsp;现金流量项目
        </span>

      </div>
      <div style="display: inline-block;position:absolute;right: 60px;top: 40px;z-index: 100000;background:#ffffff">
        <img src="../../../../../assets/images/012.png" style="height:80px;margin-right: 10px;"/>
      </div>
    </template>
    <div
      class="nc-open-content"
    >
      <div class="open-content-up" style="text-align: center;margin-top: 5%;">
        <div style="text-align: left;padding-left: 8%">
          <a-radio-group v-model:value="itemConfig.base.fangxiang" name="radioGroup">
            <a-radio  value="1">流入</a-radio>
            <a-radio value="0">流出</a-radio>
          </a-radio-group>
          <br/>
          <label style="font-size: 18px;margin-left: 0;width: 180px;">现金流量项目：</label>
          <a-input v-model:value="itemConfig.base.projectName" placeholder="现金流量项目名称"  :disabled="isEdit" style="width: 55%;"/>
          <span class="red_span">*</span>
        </div>
        <br/><br/>
        <div style="text-align: left;padding-left:  8%;">
          <label>编码：</label>
          <a-input v-model:value="itemConfig.base.projectCode" placeholder="现金流量项目编码"  :disabled="isEdit"  style="width: 30%;" />

          <label style="padding-left: 20px;">类别：</label>
          <a-select v-model:value="itemConfig.base.projectType" placeholder="所属类别" style="width: 30%;" :disabled="isEdit">
            <a-select-option v-for="item in typeList" :key="item.projectType" :value="item.projectTypeName">
              {{ item.projectType+'-'+item.projectTypeName }}
            </a-select-option>
          </a-select>
        </div>

        <br/>

        <div style="border-bottom: 1px solid rgb(1, 129, 226);padding-left: 2em;padding-right: 2em;">

          <span style="width: 35%;">&emsp;&emsp;</span>
          <a-button @click="createRow()" style="padding: 0px 4px; height: 22px;float: right;background-color: rgb(1, 129, 226);color: #fff">
            <PlusOutlined/>
          </a-button>
        </div>
        <div v-for="(row,rowIndex) in itemConfig.base.table" style="margin-top: 5px;">
          <label style="width: 130px;">对应借方科目：</label>
          <a-select
            v-model:value="row.jieCode"
            placeholder="对应借方科目"
            style="width: 27%;"
            show-search
            option-filter-prop="children"
            allow-clear>
            <a-select-option v-for="item in kemuList" :key="item.ccode" :value="item.ccode">
              {{ item.ccode }}-{{ item.ccodeName }}
            </a-select-option>
          </a-select>
          <a style="font-weight: bold;font-size: 18px;"><LinkOutlined v-if="isState!='2'" @click="openSelectDept('j',rowIndex)" /></a>
          <label style="width: 130px;">对应贷方科目：</label>
          <a-select
            v-model:value="row.daiCode"
            placeholder="对应贷方科目"
            style="width: 27%;"
            show-search
            option-filter-prop="children"
            allow-clear>
            <a-select-option v-for="item in kemuList" :key="item.ccode" :value="item.ccode">
              {{ item.ccode }}-{{ item.ccodeName }}
            </a-select-option>
          </a-select>
          <a style="font-weight: bold;font-size: 18px;"><LinkOutlined v-if="isState!='2'" @click="openSelectDept('d',rowIndex)" /></a>
          &nbsp;&nbsp;
          <DeleteOutlined @click="delRow(row,rowIndex)" style="color: rgb(1, 129, 226)"/>
        </div>
        <SelectDept @throwData="saveSelectDept" @register="registerSelectDeptPage"/>

      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {onMounted, ref, unref} from 'vue'
import { BasicModal, useModal, useModalInner } from '/@/components/Modal'
import {
  PlusOutlined,
  DeleteOutlined,LinkOutlined,PlusCircleOutlined,FormOutlined
} from '@ant-design/icons-vue'

import projectCashItemConfigModelHelper
  from "./helper/projectCashItemConfigModelHelper"
import {findYearByAccount} from "/@/api/record/system/bank-statement";
import {Input as AInput,Select as ASelect} from "ant-design-vue";
import {
  findCodeByIyear,
  findCodeByProjectCode,
  findCodeByYearAndBend,
  getTypeList
} from "/@/api/record/system/project-cash";
import {useCompanyOperateStoreWidthOut} from "/@/store/modules/operate-company";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const ARadioGroup = ARadio.Group
const emit=defineEmits(['register','save'])

const {createBaseRow, createProjectCashItemConfig} = projectCashItemConfigModelHelper()
const itemConfig:any = ref(createProjectCashItemConfig())

//添加行
function createRow() {
  itemConfig.value.base.table.push(createBaseRow())
}
//删除行
function delRow(row:any ,rowIndex:any) {
  itemConfig.value.base.table.splice(rowIndex, 1);
}

const yearList:any = ref({})
const year:any = ref('2021')
const accId:any = ref('')
const defaultAdName = ref(useCompanyOperateStoreWidthOut().getSchemaName)
async function reloadYear() {
  const res = await findYearByAccount(defaultAdName.value)
  yearList.value = res
  // console.log(yearList.value)
  if (res.length>0) {
    year.value = res[0].accountYear
    await reloadCode()
  }
}

const kemuList:any = ref([])
async function reloadCode() {
  kemuList.value = await useRouteApi(findCodeByYearAndBend,{schemaName: dynamicTenantId})(year.value)
}

const formItems:any = ref({})

let changeBeforeModel:any = {}
/*onMounted(async() => {
  await reloadYear()
})*/

const dynamicTenantId = ref()
const typeList = ref([])
const isEdit = ref(false)
const showEdit = ref(false)
const [register, { closeModal }] = useModalInner((data) => {
  defaultAdName.value = data.defaultAdName
  dynamicTenantId.value = data.dynamicTenantId
  year.value = data.year
  reloadCode()
  isEdit.value = data.isEdit

  showEdit.value = data.showEdit

  //获取当前会计准则下类别list
  useRouteApi(getTypeList,{schemaName: dynamicTenantId})().then(res => {
    typeList.value = res
  })
  // 方式2
  itemConfig.value.base.id = data.data.id
  itemConfig.value.base.projectCode = data.data.projectCode
  itemConfig.value.base.projectName = data.data.projectName
  itemConfig.value.base.projectType = data.data.projectType
  itemConfig.value.base.projectTypeName = data.data.projectTypeName
  itemConfig.value.base.fangxiang = data.data.fangxiang
  itemConfig.value.base.flag = data.data.flag

  //给对应科目列表赋值
  if (data.data.projectCode != null && data.data.projectCode != '') {
    useRouteApi(findCodeByProjectCode,{schemaName: dynamicTenantId})({projectCode:itemConfig.value.base.projectCode, iyear:year.value}).then(res => {
      itemConfig.value.base.table = res
      if (res.length==0) {
        //如果没有值默认给1行
        createRow()
        changeBeforeModel = JSON.parse(JSON.stringify(itemConfig.value.base))
      }
    })
  } else {
    itemConfig.value.base.table=[]
    createRow()
    changeBeforeModel = JSON.parse(JSON.stringify(itemConfig.value.base))
  }
})
let isChanged:boolean = false
async function handleOk() {
  itemConfig.value.base.table.forEach((row: any) => {
    row.iyear=year.value
    row.projectCode = itemConfig.value.base.projectCode
  })
  itemConfig.value.base.iyear =year.value
  itemConfig.value.base.projectTypeName = typeList.value.find(v=> v.projectType === itemConfig.value.base.projectType).projectTypeName
  console.log(itemConfig.value.base)
  isChanged = !(itemConfig.value.base.projectCode == changeBeforeModel.projectCode
    && itemConfig.value.base.projectName == changeBeforeModel.projectName
    && itemConfig.value.base.projectType == changeBeforeModel.projectType
    && itemConfig.value.base.projectTypeName == changeBeforeModel.projectTypeName
    && itemConfig.value.base.fangxiang == changeBeforeModel.fangxiang
    && JSON.stringify(itemConfig.value.base.table)==JSON.stringify(changeBeforeModel.table))
  if(isChanged){
    emit('save', unref(itemConfig.value.base))
    closeModal()
    return true
  }
  closeModal()
  return false
}

import SelectDept from '../../acccode2/popup/modalAllPop.vue'
import {Radio as ARadio} from "ant-design-vue/lib/components";
const [registerSelectDeptPage, { openModal: openSelectDeptPage }] = useModal()
const rowIndex = ref(0)
const fx = ref('j')
const openSelectDept = (f,i) => {
  //记录打开行
  fx.value = f
  rowIndex.value = parseInt(i)
  openSelectDeptPage(true, {
    database: dynamicTenantId.value,
    accId: defaultAdName.value,
    iyear: year.value
  })
}
function saveSelectDept(data){
  console.log(data)
  console.log(itemConfig.value.base.table)
  if(fx.value === 'j'){
    itemConfig.value.base.table[rowIndex.value].jieCode = data.ccode
  }else{
    itemConfig.value.base.table[rowIndex.value].daiCode = data.ccode
  }
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
    width: 80px;
    display: inline-block;
    padding-top: 5px;
    padding-left: 5px;
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


/deep/.ant-select-disabled > .ant-select-selector{
  color: #252525 !important;
  background: white !important;
}

input:disabled{
  background: white !important;
  color: #252525 !important;
}

</style>
