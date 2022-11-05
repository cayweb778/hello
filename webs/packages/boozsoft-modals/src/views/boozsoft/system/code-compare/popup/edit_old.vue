<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="科目对照"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">

        <label>年度:</label>
        <a-input :disabled="true" v-model:value="formItems.iyear" placeholder="" />

        <br><br>
        <label>财务会计科目:</label>
<!--        <a-input v-model:value="formItems.sourceCode" placeholder="" />-->
        <a-select v-model:value="formItems.sourceCode"
                  show-search
                  :filter-option="false"
                  :dropdownStyle="{height:'190px'}"
                  placeholder="财务会计科目"
                  @search="handleSearch"
                  @change="handleChange"
                  style="width: 50%">
          <a-select-option v-for="item in sourceList" :key="item.ccode" :value="item.ccode">
            {{ item.ccode }}-{{ item.ccodeName }}
          </a-select-option>
        </a-select>

        <br><br>
        <label>预算会计科目:</label>
<!--        <a-input v-model:value="formItems.targetCode" placeholder="" />-->
        <a-select v-model:value="formItems.targetCode"
                  show-search
                  :filter-option="false"
                  :dropdownStyle="{height:'140px'}"
                  placeholder="预算会计科目"
                  @search="targetHandleSearch"
                  @change="targetHandleChange"
                  style="width: 50%">
          <a-select-option v-for="item in targetList" :key="item.ccode" :value="item.ccode">
            {{ item.ccode }}-{{ item.ccodeName }}
          </a-select-option>
        </a-select>

      </div>
    </div>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import {onMounted, ref, unref} from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {Select as ASelect,Input as AInput } from 'ant-design-vue'
import {useMessage} from "/@/hooks/web/useMessage";
import {findCodeByYearAndBend} from "/@/api/record/system/project-cash";
import {findCodeCompareByIyear} from "/@/api/record/system/code-compare";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";

const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option

const {
  createErrorModal
} = useMessage()

const emit=defineEmits(['register','save'])

const formItems:any = ref({})

let changeBeforeModel:any = {}

const kemuList:any = ref([])
const sourcekemuList:any = ref([])
const targetkemuList:any = ref([])
const dynamicTenantId = ref()
const [register, { closeModal }] = useModalInner((data:any) => {
  dynamicTenantId.value = data.dynamicTenantId
  useRouteApi(findCodeByYearAndBend,{schemaName: dynamicTenantId})(data.iyear).then(res=>{
    kemuList.value = res
    sourcekemuList.value = kemuList.value.filter(item=>{
      return item.yusuan!='1'
    }).map(item=>{
      const item1:any = {}
      item1.ccode = item.ccode
      item1.ccodeName = item.ccodeName
      return item1
    })
    targetkemuList.value = kemuList.value.filter(item=>{
      return item.yusuan=='1' && item.bend=='1'
    }).map(item=>{
      const item1:any = {}
      item1.ccode = item.ccode
      item1.ccodeName = item.ccodeName
      return item1
    })
    sourceList.value = sourcekemuList.value
    targetList.value = targetkemuList.value
  })

  // 方式2
  formItems.value = {
    id: data.data.id,
    iyear: data.iyear,
    sourceCode: data.data.sourceCode,
    targetCode: data.data.targetCode,
  }
  changeBeforeModel = JSON.parse(JSON.stringify(formItems))
})
//财务会计科目搜索
const sourceList = ref([])
function handleSearch(value) {
  sourceList.value = sourcekemuList.value.filter(item=>{
    return item.ccode.slice(0, value.length)==value
  })
  // debugger
  // console.log(sourceList.value)
}
function handleChange(value) {
  console.log(value);
  formItems.value.sourceCode = value;
  checkSourceCode()
  sourceList.value = sourcekemuList.value.filter(item=>{
    return item.ccode.slice(0, value.length)==value
  })
}
//预算会计科目搜索
const targetList = ref([])
function targetHandleSearch(value) {
  targetList.value = targetkemuList.value.filter(item=>{
    return item.ccode.slice(0, value.length)==value
  })
  // debugger
  // console.log(targetList.value)
}
function targetHandleChange(value) {
  console.log(value);
  formItems.value.targetCode = value;
  targetList.value = targetkemuList.value.filter(item=>{
    return item.ccode.slice(0, value.length)==value
  })
}

let isChanged:boolean = false
async function handleOk() {
  isChanged = !(formItems.value.iyear == changeBeforeModel._value.iyear
              &&formItems.value.sourceCode == changeBeforeModel._value.sourceCode
              &&formItems.value.targetCode == changeBeforeModel._value.targetCode)
  console.log(isChanged)
  if (formItems.value.iyear=='' || formItems.value.iyear==null){
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '年度不能为空！'
    })
    return false
  }
  if (formItems.value.sourceCode=='' || formItems.value.sourceCode==null){
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '财务会计科目不能为空！'
    })
    return false
  }
  if (formItems.value.targetCode=='' || formItems.value.targetCode==null){
    createErrorModal({
      iconType: 'error',
      title: '提示',
      content: '预算会计科目不能为空！'
    })
    return false
  }
  if(isChanged){
    emit('save', unref(formItems))
    closeModal()
    return true
  }
  closeModal()
  return false
}

async function checkSourceCode(){
  if(changeBeforeModel._value.sourceCode!=null && changeBeforeModel._value.sourceCode!='' && formItems.value.sourceCode!='' && formItems.value.sourceCode!=null){
    return true
  }
  const res = await useRouteApi(findCodeCompareByIyear,{schemaName: dynamicTenantId})({iyear: formItems.value.iyear})
  if(res.items.length>0){
    let list:any = []
    res.items.forEach(item=>{
      if (item.sourceCode.slice(0, formItems.value.sourceCode.length)==formItems.value.sourceCode){
        list.push(item)
      }
      else if (formItems.value.sourceCode.slice(0, item.sourceCode.length)==item.sourceCode){
        list.push(item)
      }
    })
    if (list.length>0) {
      createErrorModal({
        iconType: 'error',
        title: '提示',
        content: '财务会计科目不能重复！'
      })
      formItems.value.sourceCode = ''
      console.log(false)
      return false
    }
  }
  return true
}
/*const jici=[4,2,3,1]
const existCode=[]
const allCode=[

].filter(item=>{
  let bol=false
  existCode.forEach(item2=>{
      item.indexOf('')
  })
  return bol
})*/
</script>
<style lang="less" scoped>
:deep(.ant-calendar-picker-input.ant-input),
:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none;
  width: 100%;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
}
:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none !important;
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
    width: 50%;
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
