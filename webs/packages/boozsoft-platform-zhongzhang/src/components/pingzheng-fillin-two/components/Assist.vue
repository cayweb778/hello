<template>
  <BasicModal width="500px" v-bind="$attrs" :title="''"
              @ok="handleOk()"
              :show-ok-btn="showOk"
              :canFullscreen="false"
              :maskClosable="false"
              :draggable="false"
              @register="register">
    <template #title>
      <div style="height: 30px;width: 100%;background-color: #5f375c;color: white;line-height: 30px;">
        <AppstoreOutlined  style="margin: 0 2px;font-size: 14px;"/>
        <span style="font-size: 14px">辅助项</span>
      </div>
    </template>
    <div class="nc-query-open-content">


      <li v-if="showInput" v-for="hsKey in rowStrList ">
        <span>{{ (hesuanList?.filter(it=>it.key == blend(hsKey,true))[0]?.label || hsKey) }}{{(hsKey == 'mdF' || hsKey == 'nfrat' || hsKey == 'cunitPrice' || hsKey == 'number')?'':'核算'}}：&emsp;</span>
        <template v-if="hsKey == 'mdF' || hsKey == 'nfrat' || hsKey == 'cunitPrice' || hsKey == 'number'">
          <InputNumber v-model:value="formItems[hsKey]"   :ref="(e)=>resetRef(e,hsKey)" :precision="2" :controls="false" :min="hsKey == 'mdF'?0:null"
                  class="addonAfter-input" @keyup.enter.native="focusNext(hsKey)">
          </InputNumber>
        </template>
        <template v-else>
          <Select v-model:value="formItems[hsKey]"  :options="(hesuanList?.filter(it=>it.key == blend(hsKey,true))[0]?.list  || [])" :filter-option="filterOption"  show-search :ref="(e)=>resetRef(e,hsKey)"
                  class="addonAfter-input" @keyup.enter.native="focusNext(hsKey)">
            <template #option="{ value: val, label ,title }">{{ title }}</template>
          </Select>
        </template>
        <span>{{hsKey == 'nfrat'?`${showTitle(hsKey)}`:hsKey == 'number1'?`${showTitle(hsKey)}`:''}}</span>
      </li>
    </div>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
import {reactive, ref, unref} from 'vue';
import {BasicModal, useModalInner} from '/@/components/Modal';
import {hasBlank} from "/@/api/task-api/tast-bus-api";
import {AppstoreOutlined} from '@ant-design/icons-vue';
import {DatePicker, Select, Checkbox, InputNumber, message,Button} from 'ant-design-vue';
import {useMessage} from "/@/hooks/web/useMessage";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {BasicTable, useTable} from '/@/components/Table'
import {findKeyLabelAll} from "/@/api/record/system/fuZhuHeSuan";

const SelectOption = Select.Option;

const emit = defineEmits(['register', 'query']);

const {createWarningModal} = useMessage()

const showOk = ref(false);
const showInput = ref(false);

const formItems: any = ref({});
const dynamicTenant = ref(null)
const kemuInfo = ref(null)
const rowStrList = ref([])
const refList = ref({})


const [register, {closeModal, setModalProps}] = useModalInner(async (o) => {
  showOk.value=true
  formItems.value = o.row
  kemuInfo.value = o.info
  dynamicTenant.value = o.tenant
  // setModalProps({minHeight: 100});
  await assembleHs(o.info)
});

const [registerTable, {getDataSource, setTableData,getColumns,setColumns}] = useTable({
  columns: [
    {
      title: '金额',
      dataIndex: 'xyBillStyle',
      slots: {customRender: 'xyBillStyle'},ellipsis: true,
    },
  ],
  bordered: true,
  showIndexColumn: true,
  pagination: false
})

const filterOption = (input: string, option: any) => {
  return option.title.indexOf(input.toLowerCase()) >= 0;
};

const resetRef = (e,k) => {
  refList.value[k+'Ref'] = e
}

const kmheList = ['bnum','currency','bbank','bperson','bcus','bsup','bdept','bitem','bstock'/*,'ysYsly','ysZcgnfl','ysZfzcjjfl','ysBmzcjjfl'*/]
const hesuanList = ref({})
const assembleHs =async (info) => {
  let arr = []
  Object.keys(info).map(k=> info[k] == '1' && arr.push(k))
  let last = kmheList.filter(k=>arr.indexOf(k)!=-1)
  if (arr.join('').indexOf('cdfine') != -1){
    for (let i = 1; i <= 30; i++) {
        if (arr.indexOf('cdfine'+i) != -1)last.push('cdfine'+i)
    }
  }
  // 转换 凭证字段
  let fs = last.map(k=>{
    if (k=='bperson')k = 'fzEmp'
    if (k=='bcus')k = 'fzCustom'
    if (k=='bsup')k = 'fzGys'
    if (k=='bdept')k = 'fzDept'
    if (k=='bitem')k = 'fzItem'
    if (k.startsWith('cdfine')) k = k.replace('cdfine','')
    return k
  }).filter(k=>k.startsWith('fz') || k.startsWith('cdfine'))
  // 转换 凭证字段
  last = last.map(k=>{
      if (k=='bperson')k = 'cpersonId'
      if (k=='bcus')k = 'ccusId'
      if (k=='bsup')k = 'csupId'
      if (k=='bdept')k = 'cdeptId'
      if (k=='bitem')k = 'projectId'
      if (k=='currency')k = 'nfrat'
      if (k=='bnum')k = 'number'
      if (k=='bbank')k = 'pjCsettle'
      return k
  })
  if (last.indexOf('number') != -1) last.splice(1, 0, "cunitPrice")
  if (last.indexOf('nfrat') != -1) last.splice(3, 0, "mdF")
  if (last.indexOf('pjCsettle') != -1 ){
    if (dynamicTenant.value?.target?.isettlement=='1'){
      last.splice(4, 0, "pjId",'pjDate','pjUnitName')
    }else {
      last = last.filter(it=>it!='pjCsettle')
    }
  }
  rowStrList.value = last
  hesuanList.value = ((await useRouteApi(findKeyLabelAll,{schemaName:dynamicTenant.value.accountMode})({require: fs.join(),toTarget: 'false'})).map(it=>{
    it.list = it.list.map(i=>({
      value: i.key,
      label: i.label,
      title:  i.code+' '+i.label
    }))
    return it;
  }))
  hesuanList.value.push(...[{key:'number',label: '数量'},{key:'cunitPrice',label: '单价'},{key:'nfrat',label: '外币金额'},{key:'mdF',label: '汇率'},])
  if (last.indexOf('pjCsettle') != -1 && dynamicTenant.value?.target?.isettlement=='1') // 结算方式必录
    hesuanList.value.push(...[{key:'pjCsettle',label: '结算方式',list: []},{key:'pjId',label: '票号/结算号'},{key:'pjUnitName',label: '对方单位'},{key:'pjDate',label: '发生日期'}])
  showInput.value = true
}

const blend = (k,b) => {
  let x = k
  if (b){
      switch (k)
      {
        case 'cpersonId':x="fzEmp";
          break;
        case 'ccusId':x="fzCustom";
          break;
        case 'csupId':x="fzGys";
          break;
        case 'cdeptId':x="fzDept";
          break;
        case 'projectId':x="fzItem";
          break;
      }
  }else {
    switch (k)
    {
      case 'fzEmp':x="cpersonId";
        break;
      case 'fzCustom':x="ccusId";
        break;
      case 'fzGys':x="csupId";
        break;
      case 'fzDept':x="cdeptId";
        break;
      case 'fzItem':x="projectId";
        break;
    }
  }
  return x
}

const openParameter = ref({
  values: [],
  date: '',
  code: '',
})
async function handleOk() {
  if (rowStrList.value.filter(k=> hasBlank(formItems.value[k])).length > 0) {
    createWarningModal({title: '温馨提示', content: '请完善所有辅助核算项内容！'})
  } else {
    let list = rowStrList.value.filter(k=>k != 'nfrat' && k != 'mdF' && k!='number' && k!= 'cunitPrice').map(k=>hesuanList.value.filter(it=>it.key == blend(k,true))[0]?.list.filter(it=>it.value == formItems.value[k])[0]?.title)
    formItems.value['fuzhuStr'] = `数量:${formItems.value['number']}/${formItems.value['cunitPrice']}` + `其他:${list.join()}`
    formItems.value['wbTopStr'] = `${showTitle('nfrat')} / ${formItems.value['nfrat']}`
    formItems.value['wbDownStr'] = `${formItems.value['mdF']} %`
    emit('save',  unref(formItems.value));
    closeModal();
    return true;
  }
}
const showTitle = (t) => {
  let text = ''
  if (t == 'number'){
   text = kemuInfo.value['menterage']
  }else if(t == 'nfrat'){
    text =  kemuInfo.value['currencyType']
  }
  return text
}
async function handleClose() {
}

const focusNext = (t) => {
  let field = rowStrList.value[rowStrList.value.findIndex(it => it === t) + 1]
  if (null != field) refList.value[field + 'Ref'].focus()
}
</script>

<style lang="less" scoped="scoped">
@import '/@/assets/styles/redTitle-open.less';
.nc-query-open-content {
  text-align: center;
  padding: 2%;
  height: 100%;

  li{
    >span{
      display: inline-block;
      min-width: 100px;
      text-align: right;
    }
    >span:nth-of-type(2){
      display: inline-block;
      min-width: 80px;
      text-align: center;
    }
  }
  :deep(.ant-select-selector), :deep(.ant-picker), .ant-input,:deep(.ant-input-number-input-wrap) {
    border: none;
    border-bottom: 1px solid #c9c9c9;
  }

  > div {
    display: inline-block;
    margin-top: 2%;
  }
  .addonAfter-input{
    width: 55%;
    border: none;
    .ant-input{
      border: none;
      pointer-events: none;
      border-bottom: solid 1px rgb(191, 191, 191) !important;
    }
    .ant-input-group-addon{
      border-left: 1px solid #eeeeee;
      padding: 0 3px;
    }
    .ant-input-group-addon:hover{
      cursor: pointer;
      border-color: #0096c7;
    }
  }
}
</style>
