<template>
  <div class="dynamic-form">
    <BasicForm @register="register"  ref="formElRef">
      <template #bomId="{ model, field }">
        <Select placeholder="请选择" v-model:value="model[field]" :show-search="true"
                :filter-option="filterOption" ref="ccodeRef"
                @keyup.enter.native="focusNext(field)" @change="codeChangeMoreName"
                :options="dynamicSchemas.filter(it=>it.field===field)[0]['componentProps']['options'].map(it=>({value: it.stockNum,title: it.title,label: it.stockNum}))">
          <template #option="{ value: val, label ,title }">{{ title }}</template>
          <template #suffixIcon>
            <SearchOutlined @click="exec(field)"/>
          </template>
        </Select>
      </template>
      <template #unitId="{ model, field }">
        <Select placeholder="请选择" v-model:value="model[field]" :show-search="true"
                :filter-option="filterOption" ref="cdepcodeRef"
                @keyup.enter.native="focusNext(field)"
                :options="dynamicSchemas.filter(it=>it.field===field)[0]['componentProps']['options']">
          <template #option="{ value: val, label ,title }">{{ label }}</template>
          <template #suffixIcon>
            <SearchOutlined @click="exec(field)"/>
          </template>
        </Select>
      </template>
      <template #cdepcode="{ model, field }">
        <Select placeholder="请选择" v-model:value="model[field]" :show-search="true"
                :filter-option="filterOption" ref="cdepcodeRef"
                @keyup.enter.native="focusNext(field)"
                :options="dynamicSchemas.filter(it=>it.field===field)[0]['componentProps']['options']">
          <template #option="{ value: val, label ,title }">{{ title }}</template>
          <template #suffixIcon>
            <SearchOutlined @click="exec(field)"/>
          </template>
        </Select>
      </template>
      <template #cpersoncode="{ model, field }">
        <Select placeholder="请选择" v-model:value="model[field]" :show-search="true"
                :filter-option="filterOption" ref="cpersoncodeRef"
                @keyup.enter.native="focusNext(field)"
                :options="dynamicSchemas.filter(it=>it.field===field)[0]['componentProps']['options']">
          <template #option="{ value: val, label ,title }">{{ title }}</template>
          <template #suffixIcon>
            <SearchOutlined @click="exec(field)"/>
          </template>
        </Select>
      </template>
    </BasicForm>
  </div>
</template>
<script setup="props, { content } ,refs" lang="ts">
import {getCurrentInstance, ref, watch,inject} from 'vue';
import {BasicForm, useForm} from "/@/components/Form";
import {findStockCaiGouList,} from "/@/api/record/stock/stock-caigou";
import localeCn from 'ant-design-vue/es/date-picker/locale/zh_CN';
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {SearchOutlined} from '@ant-design/icons-vue';
import {Select} from "ant-design-vue";
import {hasBlank} from "/@/api/task-api/tast-bus-api";
import {JsonTool} from "/@/api/task-api/tools/universal-tools";

const emit = defineEmits(['register', 'open']);
const props = defineProps(['datasource', 'formDataFun', 'accId', 'readOnly'])

const formElRef = ref(null)
const ccodeRef = ref(null)
const ddateRef = ref(null)
const cwhcodeRef = ref(null)
const cvencodeJsRef = ref(null)
const cdepcodeRef = ref(null)
const cpersoncodeRef = ref(null)
const sgsppersonRef = ref(null)
const deliveryUserRef = ref(null)
const cmemoRef = ref(null)
const simpJlList = inject('simpJlList')
const manyJlList = inject('manyJlList')
function createItem(it) {
  return {
    field: it['field'],
    component: it['component'],
    label: it['label'] + "：",
    colProps: {
      span: it['component'] == 'DatePicker' ? 5 : 5,
    },
    required: it['required'],
    componentProps: ( it['component'] == 'Select'?{
      options: it['list'],
    }:it['component'] == 'Input'?{readonly: it['readonly']}:it['component'] == 'InputNumber'?(it['field']!='quantity'?{min: it['field']=='sunhaoLv'?0:1,max: 100}:{min: 0}):{}),
    slot: it['component'] == 'Select' ? it['field'] : null,
    show: it['isShow']
  }
}

const dynamicSchemas = ref([])
const selectModel = ref({})
const attachData = ref({
  bstyle: '1',
  cunitid: '',
  baseQuantity: 0,
  bomVerStartDate: '',
  bomVerStopDate: '',
})
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
const isCheck = ref(false)
watch(() => props.datasource, async () => {
  props.formDataFun.getFormValue = () => {
    let e = getFieldsValue()
    return  assembledMerger(e)
  }
  props.formDataFun.setFormValue = (e) => {
    resetFields();
    setFieldsValue(e)
    isCheck.value = e['bcheck'] == '1'
    Object.keys(attachData.value).map(k=>!hasBlank(e[k]) && ( attachData.value[k] = e[k]))
    if (!hasBlank(e['bomId']))codeChangeMoreName(e['bomId'],e)
  }
  let showList = props.datasource.filter(it => (it.isShow) && it.component == 'Select')
  let q = showList.map(it => it.componentProps)
  q.push('operator')
  q.push('supplier')
  q.push('warehouse')
  let map = (await useRouteApi(findStockCaiGouList, {schemaName: props.accId})([...new Set(q)].join(',')))
  dynamicSchemas.value = props.datasource.filter(it => it.isShow).map(it => {
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

const codeChangeMoreName = (v,old) => {
   let o =  selectModel.value['stock'].filter(it=>it.stockNum === v)[0]
   old['bomName'] = o?.stockName || null
   setFieldsValue(assembleUnitList(old,o))
}

const assembleUnitList = (record,o) => {
  // 计量类型
  let unitList = []
  if (o.stockMeasurementType != '单计量' && !hasBlank(o?.stockMeasurementUnit) && record?.unitInfo == null) {
    let res = manyJlList.value.filter(it => it.id == o?.stockMeasurementUnit)[0]
    if (res != null) {
      let list = JsonTool.parseObj(res.detail) || []
      unitList = list.map(it => {
        it.value = it.id;
        it.label = it.unitName;
        it.ggxh = '';
        it.barcode = '';
        return it;
      })
      if (list.length > 0 && record.id == null) {
        record.unitId = (o?.stockMarketUnit ? o?.stockMarketUnit : list.filter(it => it.isMain == 'true')[0]?.id)
        record.cunitid = list[0].id
      }
    }
  }else if (o.stockMeasurementType == '单计量' &&  !hasBlank(o?.stockMeasurementUnit)){
    let res = simpJlList.value.filter(it => it.id == o?.stockMeasurementUnit)
    if (null !=  res && res.length >0){
       unitList = res.map(it=>{it.value=it.id;it.label=it.unitName;it.ggxh=o?.stockGgxh;it.barcode =o?.stockBarcode;return it;})
      record.unitId = unitList[0].value
      record.cunitid = unitList[0].value
    }
  }
  attachData.value.cunitid = record.cunitid
  dynamicSchemas.value.filter(it=>it.field=='unitId')[0]['componentProps']['options']=unitList
  return record
}

const assembledMerger = (r) => {
  if (!hasBlank(r.unitId) && !hasBlank(r.quantity)){
    if (attachData.value['cunitid'] === r.unitId){
      attachData.value['baseQuantity'] = parseFloat(r.quantity).toFixed(10)
    }else {
      let unitList = dynamicSchemas.value.filter(it=>it.field=='unitId')[0]['componentProps']['options'] || []
      let n = parseFloat(r.quantity)
      let b = 0
      let index =unitList.findIndex(it => it.id == r.unitId)
      if (index == 1 && unitList.length > 1) {
        b = parseFloat((n * parseFloat(unitList[1].conversionRate))).toFixed(10)
      } else if (index == 2 && unitList.length > 2) {
        b = parseFloat(((n * parseFloat(unitList[2].conversionRate)) / parseFloat(unitList[0].conversionRate))).toFixed(10)
      } else {
        b = n.toFixed(10)
      }
      attachData.value['baseQuantity'] = b
    }
  }
  Object.keys(attachData.value).map(k=>r[k]=attachData.value[k])
  return r
}

</script>
<style lang="less" scoped="scoped">
.dynamic-form{
  :deep(.ant-select-selector), :deep(.ant-picker), :deep(.ant-input-affix-wrapper) {
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
      margin-bottom: 0px;
    }
  }
  :deep(.ant-col-4){
    display: none;
  }
}
</style>
