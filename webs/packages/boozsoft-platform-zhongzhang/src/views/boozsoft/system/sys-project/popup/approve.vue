
<template>
  <BasicModal width="900px" v-bind="$attrs" title="项目信息" @ok="handleOk()" @register="register">
    <div class="nc-open-content" style="height: 100%">
      <div class="open-content-up">
        <a-input
          :disabled="true"
          v-model:value="formItems.projectName"
          placeholder="请输入项目名称"
          style="margin-left: 6%;"
        />
        <span>必填项</span>
        <label />
        <a-select :disabled="true"
          v-model:value="formItems.projectClassCode"
          show-search
          placeholder="请选择项目分类"
          option-filter-prop="label"
          style="width: 35%;border: none"
          allow-clear
        >
          <a-select-option
            v-for="cls in classList"
            :key="cls.projectClassName"
            :value="cls.uniqueCode"
          >{{ cls.projectClassName }}</a-select-option>
        </a-select>
        <br />
        <br />

        <label>项目编码</label>
        <a-input :disabled="true" v-model:value="formItems.projectCode" placeholder="请输入项目编码" style="width: 30%" />
        <span>必填项</span>
        <label>所属部门</label>
        <TreeSelect :disabled="true"
          v-model:value="formItems.deptCode"
          style="width: 35%"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="treeData"
          placeholder="请选择部门"
          tree-default-expand-all
          allow-clear
        ></TreeSelect>
        <br />

        <label>开始日期</label>
        <a-date-picker :disabled="true" v-model:value="formItems.startDate" format="YYYY-MM-DD" style="width: 35%;" />
        <label>结束日期</label>
        <a-date-picker :disabled="true" v-model:value="formItems.endDate" format="YYYY-MM-DD" style="width: 35%;" />
        <br />

        <label>项目负责人</label>
        <a-select :disabled="true"
          v-model:value="formItems.psnInCharge"
          show-search
          placeholder="请选择部门负责人"
          option-filter-prop="label"
          style="width: 35%;border: none"
          allow-clear
        >
          <a-select-option
            v-for="psn in psnList"
            :key="psn.psnName"
            :value="psn.uniqueCode"
          >{{ psn.psnName }}</a-select-option>
        </a-select>
        <label>是否结算</label>
        <a-select :disabled="true" v-model:value="formItems.jiesuan" placeholder="是否结算" style="width: 35%;">
          <a-select-option value="1">是</a-select-option>
          <a-select-option value="0">否</a-select-option>
        </a-select>
        <br />
      </div>

      <div class="open-content-down" style="border-top: 1px solid rgb(1, 129, 226)">
        <BasicForm
          :labelWidth="100"
          :schemas="schemas"
          :actionColOptions="{ span: 24 }"
          @submit="handleSubmit"
        />
      </div>
    </div>

    <div style="border-top: 1px solid rgb(1, 129, 226);margin-top: 30px;">
      <a-select v-model:value="formItems.successState" placeholder="审批状态" style="width:20%;">
        <a-select-option value="1">同意</a-select-option>
        <a-select-option value="0">不同意</a-select-option>
      </a-select>&nbsp;&nbsp;
      <label>审核信息</label>
      <a-input v-model:value="formItems.approveIdea" style="width: 60%" />
    </div>
  </BasicModal>
</template>


<style lang="less" scoped>
:deep(.ant-form-item-control button) {
  display: none;
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
import { useMessage } from "/@/hooks/web/useMessage";
import { useProjectStoreWidthOut } from "/@/store/modules/project";
import {Select as ASelect,Input as AInput,DatePicker as ADatePicker } from 'ant-design-vue'

const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option

const emit=defineEmits(['register'])

const formItems: any = ref({})

const treeData: any = ref([])

const psnList: any = ref([])

const classList: any = ref([])


//查询栏目列表
const schemas: any = ref([])
async function handleOk() {
  emit('approveOk', unref(formItems))
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

  const responseList = await getFromEdit(useProjectStoreWidthOut().getNum)

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
        label,
        colProps: {
          span: 12,
        },
        defaultValue:formItemsValue[field],
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
const [register, { closeModal }] = useModalInner(({data}) => {

  useProjectStoreWidthOut().bb({ treeData, psnList, classList, formItems, data });

  findColumn(data)
})
</script>
