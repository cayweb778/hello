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
        <Select placeholder="请选择" v-model:value="model[field]" ref="ddateRef"
                @change="ddateChenck" :filter-option="filterOption"
                :options="dynamicSchemas.filter(it=>it.field===field)[0]['componentProps']['options']"
        >
          <template #option="{ value: val, label ,title }">{{ title }}</template>
        </Select>
      </template>
      <template #kmLevel="{ model, field }">
        <Select placeholder="请选择" v-model:value="model[field]" ref="kmLevelRef"
                @change="kmLevelChenck"
        >
          <SelectOption :value="d.value" v-for="d in kmLevelList">{{ d.title }}</SelectOption>
        </Select>
      </template>
      <template #dataType="{ model, field }">
        <Select placeholder="请选择" v-model:value="model[field]" ref="dataTypeRef"
                @change="dataTypeChenck"
        >
          <SelectOption :value="d.value" v-for="d in dataType">{{ d.title }}</SelectOption>
        </Select>
      </template>
      <template #lirunKm="{ model, field }">
        <Select placeholder="请选择" v-model:value="model[field]" ref="lirunKmRef"
                :show-search="true" :filter-option="filterOption" @change="lirunKmChenck"
                :options="dynamicSchemas.filter(it=>it.field===field)[0]['componentProps']['options']"
        >
          <template #option="{ value: val, label ,title }">{{ title }}</template>
        </Select>
      </template>
      <template #jz="{ model, field }">
        <Select placeholder="请选择" v-model:value="model[field]" ref="jzRef">
          <SelectOption :value="d.value" v-for="d in jz">{{ d.title }}</SelectOption>
        </Select>
      </template>
    </BasicForm>
  </div>
</template>
<script setup="props, { content } ,refs" lang="ts">
import {getCurrentInstance, ref, watch} from 'vue';
import {BasicForm, useForm} from "/@/components/Form";
import localeCn from 'ant-design-vue/es/date-picker/locale/zh_CN';
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {Input, Select,message} from "ant-design-vue";
import {findAllDataByAccountId} from "/@/api/sys_period/data";
import {company_findByIyearCcod} from "/@/api/codekemu/codekemu";

const SelectOption = Select.Option
const emit = defineEmits(['register', 'open']);
const props = defineProps(['datasource', 'formDataFun', 'accId','dynamicTenant','iyear','thisDate'])
const { proxy:any } = getCurrentInstance()
const kmLevelList=ref([{title:'一级科目',value:'1'},{title:'末级科目',value:'0'}])
const dataType=ref([{title:'全部',value:''},{title:'收入',value:'1'},{title:'支出',value:'0'}])
const jz=ref([{title:'是',value:'1'},{title:'否',value:'0'}])
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
const selectModel:any = ref({})
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
  let periodList = (await findAllDataByAccountId({accId:props.accId}))
  let kemu = await useRouteApi(company_findByIyearCcod, { schemaName: props.dynamicTenant })({iyear:props.iyear});
  dynamicSchemas.value = props.datasource.map(it => {
    if (it.component === 'Select') {
      it.list = it.field=='ddate'?periodList:kemu
    }
    return createItem(it)
  })
  let arr={
    periodList:periodList,
    kemu:kemu,
    kmLevelList:kmLevelList.value,
    dataType:dataType.value,
    jz:jz.value
  }
  selectModel.value =arr
  props.formDataFun.getSelectMap = () => selectModel.value
})

const kmLevelChenck = async (k) => {
  emit('kmLevelThrow', k)
}
const dataTypeChenck = async (k) => {
  emit('dataTypeThrow', k)
}
const lirunKmChenck = async (k) => {
  emit('lirunKmThrow', k)
}
const ddateChenck = async (k) => {
  emit('ddateThrow', k)
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
