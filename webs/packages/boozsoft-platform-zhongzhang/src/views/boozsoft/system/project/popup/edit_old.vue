
<template>
  <BasicModal width="800px" v-bind="$attrs" title="项目信息" @ok="handleOk()" @register="register" :loading="loadMark">
    <div class="nc-open-content" style="height: 100%">
      <div class="open-content-up">
        <label>项目名称：</label>
        <a-input @blur="checkName()"
                 v-model:value="formItems.projectName"
                 placeholder="请输入项目名称"
                 style="font-size: 20px;width: 80%"
        />
        <span class="red_span">*</span>
        <br/>
        <br/>

        <label>项目编码：</label>
        <a-input v-model:value="formItems.projectCode" @blur="checkCode()" placeholder="请输入项目编码"/>
        <span class="red_span">*</span>
        <label>项目分类：</label>
        <a-select
          v-model:value="formItems.projectClassCode"
          show-search
          placeholder="请选择项目分类"
          style="width: 30%;border: none;"
          allow-clear
        >
          <a-select-option
            v-for="cls in classList"
            :key="cls.projectClassName"
            :value="cls.projectClassCode"
          >{{ cls.projectClassName }}
          </a-select-option>
        </a-select>
        <span class="red_span"></span>
        <br/>

        <label>开始日期：</label>
        <a-date-picker v-model:value="formItems.startDate" format="YYYY-MM-DD" style="width: 30%;"/>
        <span class="red_span"></span>
        <label>结束日期：</label>
        <a-date-picker v-model:value="formItems.endDate" format="YYYY-MM-DD" style="width: 30%;"/>
        <span class="red_span"></span>
        <br/>

        <label>所属部门：</label>
        <TreeSelect
          v-model:value="formItems.deptCode"
          style="width: 30%"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="treeData"
          placeholder="请选择部门"
          tree-default-expand-all
          allow-clear
        ></TreeSelect>
        <span class="red_span"></span>
        <label>项目负责人：</label>
        <a-select
          v-model:value="formItems.psnInCharge"
          show-search
          placeholder="请选择部门负责人"
          style="width: 30%;border: none"
          allow-clear
        >
          <a-select-option
            v-for="psn in psnList"
            :key="psn.psnName"
            :value="psn.uniqueCode"
          >{{ psn.psnName }}
          </a-select-option>
        </a-select>
        <span class="red_span"></span>
        <br/>
<!--        <label>是否结算：</label>-->
<!--        <a-select v-model:value="formItems.jiesuan" placeholder="是否结算" style="width: 30%;">
          <a-select-option value="1">是</a-select-option>
          <a-select-option value="0">否</a-select-option>
        </a-select>-->
        <a-radio-group v-model:value="formItems.jiesuan">
          <label>是否结算：</label>
          <a-radio value="1">
            是
          </a-radio>
          <a-radio value="0">
            否
          </a-radio>
        </a-radio-group>
        <span class="red_span"></span>
      </div>

      <div class="open-content-down" style="border-top: 1px solid #999999">
        <BasicForm
          :labelWidth="100"
          :schemas="schemas"
          :actionColOptions="{ span: 24 }"
          @submit="handleSubmit"
        />
      </div>
    </div>
  </BasicModal>
</template>

<style>
.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}
</style>
<style lang="less" scoped>
:deep(.ant-form-item-control button) {
  display: none;
}

.red_span {
  color: red;
  font-weight: bold;
  display: inline-block;
  width: 20px;
  text-align: right;
}
:deep(.ant-input-number-input) {
  width: 100%;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
}
:deep(.ant-calendar-picker-input.ant-input),
:deep(.ant-select-single:not(.ant-select-customize-input)
  .ant-select-selector
  .ant-select-selection-search-input) {
  border: none;
  width: 100%;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
}
.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}
:deep(.ant-input-number){
  width: 90%;
  border: none;
}
:deep(.nc-open-content input ){
  width: 100% !important;
}

:deep(.ant-select-single:not(.ant-select-customize-input)
  .ant-select-selector
  .ant-select-selection-search-input) {
  border: none !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}

.nc-open-content {
  input {
    width: 30%;
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
    text-align: left;
    width: 130px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 2em;
    color: #535353;
  }

  :deep(.ant-form-item-no-colon) {
    text-align: left;
    width: 110px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 1em;
    color: #535353;
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
</style>
<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {
  // need
  TreeSelect
} from 'ant-design-vue'
import { BasicForm } from '/@/components/Form/index';
import { getFromEdit } from "/@/api/record/system/sys_project_category";
import {useMessage} from "/@/hooks/web/useMessage";
// import { projectStore } from "/@/store/modules/project";
import {findByCode, findByName} from "/@/api/record/system/project";
import {useProjectStoreWidthOut} from "/@/store/modules/project";
import {Select as ASelect, Input as AInput, DatePicker as ADatePicker,Radio as ARadio} from 'ant-design-vue'
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findProClassByCode} from "/@/api/record/system/project_class";

const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
const ARadioGroup = ARadio.Group

const {
  createErrorModal
} = useMessage()

const emit = defineEmits(['register', 'save'])

const formItems: any = ref({})

const treeData: any = ref([])

const psnList: any = ref([])

const classList: any = ref([])


//查询栏目列表
const schemas: any = ref([])
async function handleOk() {
  if (formItems.value.projectClassCode==null || formItems.value.projectClassCode==''){
    formItems.value.projectClassCode = '9999'
  }
  if (formItems.value.jiesuan==null || formItems.value.jiesuan==''){
    formItems.value.jiesuan = '0'
  }
  emit('save', unref(formItems))
  closeModal()
}

// 下划线转换驼峰
function toHump(name:any) {
    return name.replace(/\_(\w)/g, function(all:any, letter:any){
      console.log(all)
        return letter.toUpperCase();
    });
}
async function findColumn(data: any) {

  const responseList = await useRouteApi(getFromEdit,{schemaName: useProjectStoreWidthOut().getDynamicTenantId})(useProjectStoreWidthOut().getNum)

  responseList.forEach(function(responseItem: any, itemIndex: any) {
    //
    if (itemIndex > 7) {
      schemas.value = []
      console.log(schemas.value)

      const { ctitle, shuxing: component, cname: label } = responseItem
      const field=toHump(ctitle)
      const formItemsValue = formItems.value
      formItemsValue[field]=data[field]

      // debugger
      // console.log(field + "========" + formItemsValue[field])
      schemas.value.push({
        field,
        component,
        label: label + '：',
        colProps: {
          span: 12,
        },
        defaultValue: formItemsValue[field],
        componentProps: {
          // style:'width:200px ',
          placeholder: label,
          onChange: (newValue: any) => formItemsValue[field] = newValue
        },
      })

    }


  })


}

const { createMessage } = useMessage();
const handleSubmit = (values: any) => {
  createMessage.success('click search,values:' + JSON.stringify(values));
}

let changeBeforeModel:any = {}
const loadMark = ref(false)
const [register, { closeModal }] = useModalInner(async ({data}) => {
  loadMark.value = true

  await useProjectStoreWidthOut().bb({treeData, psnList, classList, formItems, data});
  // projectStore.bb({ treeData, psnList, classList, formItems, data });
  await findColumn(data)
  changeBeforeModel = JSON.parse(JSON.stringify(formItems))
  loadMark.value = false
})

async function checkCode() {
  /*if((changeBeforeModel._value.projectCode!=undefined && changeBeforeModel._value.projectCode!='') || changeBeforeModel._value.projectCode==formItems.value.projectCode){
      return true
  }*/
  if (changeBeforeModel._value.projectCode != formItems.value.projectCode && formItems.value.projectCode != null && formItems.value.projectCode != '') {
    const res = await useRouteApi(findByCode, {schemaName: useProjectStoreWidthOut().getDynamicTenantId})(formItems.value)
    if (res.length != 0) {
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '项目编码已存在，请重新输入！'
      })
      // alert('项目编码已存在，请重新输入！')
      formItems.value.projectCode = ''
      console.log(false)
      return false
    }
  }
  return true
}
async function checkName() {
  /*if((changeBeforeModel._value.projectName!=undefined && changeBeforeModel._value.projectName!='') || changeBeforeModel._value.projectName==formItems.value.projectName){
    return true
  }*/
  if (changeBeforeModel._value.projectName != formItems.value.projectName && formItems.value.projectName != null && formItems.value.projectName != '') {
    const res = await useRouteApi(findByName, {schemaName: useProjectStoreWidthOut().getDynamicTenantId})(formItems.value)
    if (res.length != 0) {
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '项目名称已存在，请重新输入！'
      })
      // alert('项目名称已存在，请重新输入！')
      formItems.value.projectName = ''
      console.log(false)
      return false
    }
  }
  return true
}
</script>
