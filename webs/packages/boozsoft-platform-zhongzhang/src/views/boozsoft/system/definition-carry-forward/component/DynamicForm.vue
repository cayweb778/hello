<template>
  <div class="dynamic-form">
    <BasicForm @register="register"  ref="formElRef">
<!--      <template #cvencode="{ model, field }">-->
<!--        <Select :disabled="props.canzhao" placeholder="请选择" v-model:value="model[field]" ref="cvencodeRef" :show-search="true"-->
<!--                :filter-option="filterOption" @keyup.enter.native="focusNext(field)"-->
<!--                :options="dynamicSchemas.filter(it=>it.field===field)[0]['componentProps']['options']">-->
<!--          <template #option="{ value: val, label ,title }">{{ title }}</template>-->
<!--        </Select>-->
<!--      </template>-->
      <template #ddate="{ model, field }">
        <Select placeholder="请选择" v-model:value="model[field]" ref="ddateRef">
          <SelectOption :value="d.value" v-for="d in ddateList">{{ d.title }}</SelectOption>
        </Select>
      </template>
      <template #kmLevel="{ model, field }">
        <Select placeholder="请选择" v-model:value="model[field]" ref="kmLevelRef">
          <SelectOption :value="d.value" v-for="d in kmLevelList">{{ d.title }}</SelectOption>
        </Select>
      </template>
      <template #dataType="{ model, field }">
        <Select placeholder="请选择" v-model:value="model[field]" ref="dataTypeRef">
          <SelectOption :value="d.value" v-for="d in dataType">{{ d.title }}</SelectOption>
        </Select>
      </template>
      <template #lirunKm="{ model, field }">
        <Select placeholder="请选择" v-model:value="model[field]" ref="lirunKmRef">
          <SelectOption :value="d.value" v-for="d in lirunKm">{{ d.title }}</SelectOption>
        </Select>
      </template>
      <template #jz="{ model, field }">
        <Select placeholder="请选择" v-model:value="model[field]" ref="jzRef">
          <SelectOption :value="d.value" v-for="d in lirunKm">{{ d.title }}</SelectOption>
        </Select>
      </template>
    </BasicForm>
  </div>
</template>
<script setup="props, { content } ,refs" lang="ts">
import {getCurrentInstance, ref, watch} from 'vue';
import {BasicForm, useForm} from "/@/components/Form";
import {findStockCaiGouList,} from "/@/api/record/stock/stock-caigou";
import localeCn from 'ant-design-vue/es/date-picker/locale/zh_CN';
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {SearchOutlined} from '@ant-design/icons-vue';
import {Select, Input, message,DatePicker,InputNumber} from "ant-design-vue";
import {hasBlank} from "/@/api/task-api/tast-bus-api";

const SelectOption = Select.Option
const emit = defineEmits(['register', 'open']);
const props = defineProps(['datasource', 'formDataFun', 'accId','dynamicTenant'])
const { proxy:any } = getCurrentInstance()
const kmLevelList=ref([{title:'一级科目',value:'1'},{title:'末级科目',value:'0'}])
const ddateList=ref([{title:'202201',value:'202201'},{title:'202202',value:'202202'}])
const dataType=ref([{title:'全部',value:''},{title:'收入',value:'收入'},{title:'支出',value:'支出'}])
const lirunKm=ref([{title:'0001-测试0',value:'0'},{title:'0023-测试1',value:'1'},{title:'0002-测试2',value:'2'}])
const jz=ref([{title:'包含',value:'包含'},{title:'不包含',value:'不包含'}])
const defaultSchemas = [
  {
    field: 'field1',
    component: 'DatePicker',
    label: '单据日期：',
    colProps: {
      span: 4,
    },
    required: true,
    componentProps: {locale: localeCn},
  },
  {
    field: 'field2',
    component: 'Input',
    label: '单据编号：',
    required: true,
    colProps: {
      span: 6,
    }
  },
  {
    field: 'field3',
    component: 'Select',
    label: '供应商：',
    componentProps: {
      options: [],
    },
    required: true,
    colProps: {
      span: 6,
    }
  },
]
const formElRef = ref(null)
const ccodeRef = ref(null)
const ddateRef = ref(null)
const cwhcodeRef = ref(null)
const cdepcodeRef = ref(null)
const cpersoncodeRef = ref(null)
const sgsppersonRef = ref(null)
const cmemoRef = ref(null)


function createItem(it) {
  return {
    field: it['field'],
    component: it['component'],
    label: it['label'] + "：",
    colProps: {
      span: it['component'] == 'DatePicker' ? 5 : 5,
    },
    required: it['required'],
    componentProps: (it['component'] == 'DatePicker' ? {locale: localeCn,disabled: true} : it['component'] == 'Select'?{
      options: it['list'],
    }:it['component'] == 'Input'?{readonly: it['readonly']}:{}),
    slot: it['component'] == 'Select' ? it['field'] : null,
    show: it['isShow']
  }
}

const dynamicSchemas = ref([])
const selectModel = ref({})
const [register, {getFieldsValue, resetFields, setFieldsValue}] = useForm({
  labelWidth: 100,
  /*  actionColOptions: {
      span: 24,
    },*/
  showResetButton: false,
  showSubmitButton: false,
  schemas: dynamicSchemas,
  autoFocusFirstItem: false,
});

const filterOption = (input: string, option: any) => {
  return option.title.toLowerCase().indexOf(input.toLowerCase()) >= 0;
}
watch(() => props.datasource, async () => {
  props.formDataFun.getFormValue = () => getFieldsValue()
  props.formDataFun.setFormValue = (e) => {
    resetFields();
    setFieldsValue(e)
  }
  let showList = props.datasource.filter(it => (it.isShow) && it.component == 'Select')
  let q = showList.map(it => it.componentProps)
  q.push('operator','user','supplier','dept','warehouse')
  let map = (await useRouteApi(findStockCaiGouList, {schemaName: props.accId})([...new Set(q)].join(',')))
  dynamicSchemas.value = props.datasource.map(it => {
    if (it.component === 'Select') {
      it.list = map[it.componentProps]
    }
    return createItem(it)
  })
  selectModel.value = map
  props.formDataFun.getSelectMap = () => selectModel.value
})
const exec = (k) => {
  emit('open', k)
}

const focusNext = (t) => {
  let list = dynamicSchemas.value.filter(it => it.show && it.component == 'Select')
  let field = list[list.findIndex(it => it.field === t) + 1]?.field
  if (null != field) proxy.$refs[field + 'Ref'].focus()
}
</script>
<style lang="less" scoped="scoped">
.dynamic-form{
  :deep(.ant-select-selector), :deep(.ant-picker), :deep(.ant-input-affix-wrapper){
    border: none;
    border-bottom: 1px solid #c9c9c9;
    background-color: white;
    color: black;

    .ant-picker-input {
      > input {
        color: black;
      }
    }
  }
  :deep(.ant-input-number){
    width: 100%;
    border: none;
    border-bottom: 1px solid #c9c9c9;
    background-color: white;
    color: black;
  }
  :deep(.ant-col){
    margin-left: 1em;
    .ant-form-item-label{
      text-align: left;
      .ant-form-item-no-colon{
        font-weight: bold;
        color: #666666;
      }
    }

    .ant-form-item:not(.ant-form-item-with-help) {
      margin-bottom: 5px;
    }
  }
  :deep(.ant-col-4){
    display: none;
  }
}
</style>
