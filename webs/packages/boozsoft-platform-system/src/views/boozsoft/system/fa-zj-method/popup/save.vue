<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="折旧方法"
    @ok="handleOk()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;折旧方法
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="../../../../../assets/images/012.png" style="height:80px;margin-right: 10px;"/>
      </div>
    </template>
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up" style="text-align: center;margin-top: 30px;">

        <label style="font-size: 18px;margin-left: 0;">方法名称：</label>
        <a-input v-model:value="formItems.ecName" placeholder="" class="abc" style="width: 65%;" />
        <span class="red_span">*</span>
        <br/><br/><br/>

        <label>上级方法：</label>
        <TreeSelect
          v-model:value="formItems.parentId"
          style="width: 50%"
          @change="changeFl"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="treeData"
          placeholder="请选择上级方法"
          tree-default-expand-all
          allow-clear
        >
        </TreeSelect>
        <br><br>

        <label>方法编码：</label>
        <a-input v-model:value="formItems.ecCode" @blur="changeCode" :maxlength="codeLength" placeholder="编码规范：3-2-2-2-2"/>


      </div>
    </div>
    <template #footer>
      <a-button @click="closeModal()">取消</a-button>
      <a-button @click="handleOk(false)" :disabled="saveClick">保存</a-button>
      <a-button @click="handleOk(true)" :disabled="saveClick" type="primary">保存并新增</a-button>
    </template>
  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import { onMounted, ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {
  // need
  TreeSelect
} from 'ant-design-vue'
import {
  PlusCircleOutlined,
  CaretDownOutlined
} from '@ant-design/icons-vue'
import {Input as AInput,Select as ASelect,Popover as APopover} from "ant-design-vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getDeptList, GetDeptTreeByFlag,getMaxCode} from '/@/api/record/system/fa-ac'

import {useMessage} from "/@/hooks/web/useMessage";
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search

const {
  createErrorModal
} = useMessage()

const emit=defineEmits(['register','save'])

const formItems:any = ref({})

const treeData:any = ref([])
let changeBeforeModel:any = {}
const dynamicTenantId = ref()
const [register, { closeModal }] = useModalInner((data) => {
  dynamicTenantId.value = data.dynamicTenantId
  useRouteApi(GetDeptTreeByFlag,{schemaName: dynamicTenantId})().then(res=>{
    function a(deptTree:any) {
      deptTree.forEach(
          (item: any) => {
          if (item.children != null) {
            a(item.children)
          }
          item.title = '(' + item.ecCode + ')' + item.ecName
          item.value = item.id
          item.bend = item.bend
          item.key = item.id
        })
    }
    a(res)
    treeData.value = res
  })
  // 方式2
  formItems.value = {}
  formItems.value.parentId = ''
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
const flg = ref()
const codeLength:any = ref(3)
async function changeFl() {
  //根据选中 去限制方法编码输入长度
  //3-2-2-2-2
  let str
  treeForeach(treeData.value,str)
  const a = flg.value
  if(formItems.value.parentId){
    if(a.length === 3){
      codeLength.value = 5
    }else if(a.length === 5){
      codeLength.value = 7
    }else if(a.length === 7){
      codeLength.value = 9
    }else if(a.length === 9){
      codeLength.value = 11
    }
    //请求改编码下最大code
    const res = await useRouteApi(getMaxCode,{schemaName: dynamicTenantId})(formItems.value.parentId)
    if(res != '0'){
      formItems.value.ecCode = res
    }else{
      formItems.value.ecCode = flg.value
      const le = formItems.value.ecCode.length
      if(le%2 === 0){
        //偶数
        formItems.value.ecCode = formItems.value.ecCode.padEnd(codeLength.value ,'1')
      }else{
        //奇数
        formItems.value.ecCode = formItems.value.ecCode.padEnd(codeLength.value ,'01')
      }
    }
  }else{
    if(formItems.value.ecCode.length >3){
      formItems.value.ecCode = ''
    }
  }
}

async function treeForeach(tree,str) {
  tree.forEach(data=>{
    if(data.id === formItems.value.parentId){
      flg.value = data.ecCode
      str = data.id
      return
    }
    data.children && treeForeach(data.children,str)
  })
}
const vid = ref('0')
async function treeForeach2(tree,str) {
  vid.value = '0'
  tree.forEach(data=>{
    if(data.key === str){
      vid.value = data.id
      return
    }
    data.children && treeForeach2(data.children,str)
  })
}
async function changeCode() {
  //根据输入值判断 3 重置上级
  if(formItems.value.ecCode != '' && formItems.value.ecCode.length === 3){
    formItems.value.parentId = ''
    codeLength.value = 3
  }else if(formItems.value.ecCode != '' && formItems.value.ecCode.length === 5 || formItems.value.ecCode.length === 7){
    //5 7  判断有无  有选中  无 重置自己
    //截取前3位  截取前5位  formItems.value.ecCode.length - 2
    treeForeach2(treeData.value,formItems.value.ecCode.substring(0,formItems.value.ecCode.length - 2))
    if(vid.value === '0'){
      formItems.value.ecCode = ''
    }else{
      formItems.value.parentId = vid.value
    }
  }else{
    //其他长度 直接重置
    formItems.value.ecCode = ''
  }
}



let isChanged:boolean = false
async function handleOk(fls) {
  const res = await getDeptList()
  const deptList = res.items
  if(formItems.value.ecCode==''|| formItems.value.ecCode==null){
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '方法编码不能为空！'
    })
    return false
  }
  if (formItems.value.ecName == '' || formItems.value.ecName == null) {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '方法名称不能为空！'
    })
    return false
  }
  if (formItems.value.ecCode != changeBeforeModel.ecCode && formItems.value.ecCode != '' && formItems.value.ecCode != null) {
    for (let i = 0; i < deptList.length; i++) {
      const dept = deptList[i];
      if (dept.ecCode == formItems.value.ecCode) {
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '方法编码重复不允许添加！'
        })
        return false
      }
    }
  }
  if (formItems.value.ecName != changeBeforeModel.ecName && formItems.value.ecName != '' && formItems.value.ecName != null) {
    for (let i = 0; i < deptList.length; i++) {
      const dept = deptList[i];
      if (dept.ecName == formItems.value.ecName) {
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '方法名称重复不允许添加！'
        })
        return false
      }
    }
  }
  isChanged = !(formItems.value.uniqueCode == changeBeforeModel.uniqueCode
    && formItems.value.ecCode == changeBeforeModel.ecCode
    && formItems.value.ecName == changeBeforeModel.ecName
    && formItems.value.parentId == changeBeforeModel.parentId
    && formItems.value.uniqueCodeUser == changeBeforeModel.uniqueCodeUser)
  if(isChanged){
    if(formItems.value.parentId){
      treeFind(treeData.value,formItems.value.parentId)
      if(findTree.value.bend){
        formItems.value.bend = findTree.value.bend
      }else{
        formItems.value.bend = '0'
      }
    }

    if(fls === true){
      emit('save', unref(formItems))
      formItems.value = {}
      changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
    }else{
      emit('save', unref(formItems))
      closeModal()
    }
  }
}
const findTree = ref()
async function treeFind(tree,str) {
  tree.forEach(data=>{
    if(data.id === str){
      findTree.value = data
      return
    }
    data.children && treeFind(data.children,str)
  })
}

onMounted(async () => {
  // await fetch()
})
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
    width: 120px;
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
</style>
