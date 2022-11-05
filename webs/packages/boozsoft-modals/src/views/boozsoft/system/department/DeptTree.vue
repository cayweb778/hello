<template>
  <div class="bg-white mr-0 overflow-hidden">
<!--    <div style="width: 100%;height: 50px;">
      <label style="font-size: 20px;line-height: 50px;margin-left: 20px;">部门列表</label>
      <div style="float: right;margin-right: 20px;margin-top: 10px;">
        <a style="cursor: pointer;color: blue;" @click="openAddPage()"><PlusOutlined /> 添加</a>
      </div>
    </div>-->
    <div style="width: 100%; height: 32px;background-color: #d9d9d9;padding-top:5px;font-weight: bold;text-align: center;">
      部门列表
      <div style="float: right;">
        <a style="color: black;font-size: 14px;" @click="openAddPage()"><PlusCircleTwoTone /></a>&emsp;
      </div>
    </div>
    <BasicTree
      title=""
      :click-row-to-expand="false"
      :tree-data="treeData"
      :replace-fields="{ key: 'uniqueCode', title: 'value' }"
      v-model:selectedKeys="selectedKeys"
      v-model:expandedKeys="expandedKeys"
      @select="handleSelect"
    />
    <DeptEdit
      @save="saveData"
      @register="registerEditPage"
    />
  </div>

</template>
<script setup lang="ts">
import {computed, onMounted, ref, watch} from 'vue'

import { BasicTree } from '/@/components/Tree'
import DeptEdit from './popup/daptEdit.vue'
import { useModal } from '/@/components/Modal'

import {
  PlusOutlined,
  PlusCircleTwoTone
} from '@ant-design/icons-vue'
import { saveDept } from '/@/api/record/system/dept'
import { GetDeptTreeByFlag } from '/@/api/sys/dept'
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {psnFindAll} from "/@/api/psn/psn";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";

const emit=defineEmits(['select'])
const props=defineProps(['modelValue'])
const treeData:any = ref([])
const selectedKeys = ref<string[]>([])
const expandedKeys = ref<string[]>([])
// 默认账套编码
let defalutCompany = getCurrentAccountName(true)
// 切换后账套编码
const dynamicCompany = ref(defalutCompany)
// 默认账套编码
let defalutCompanyCodeValue = ''
// 切换后账套编码
async function fetch() {
  let deptTree = await useRouteApi(GetDeptTreeByFlag,{schemaName: dynamicCompany.value})({})
  function a(deptTree:any) {
    deptTree.forEach((item: any)  => {
      if (item.children != null) {
        a(item.children)
      }
      item.value = '[' + item.deptCode + ']  ' + item.deptName
    })
  }
  function b(deptTree:any) {
    let arr:any = []
    arr.push(deptTree[0].uniqueCode)
    return  arr
  }
  a(deptTree)
  // treeData.value = deptTree
  treeData.value = []
  treeData.value.push({id:'0',key:'',uniqueCode:'0',value: '全部',children: deptTree})
  // let checks = b(deptTree)
  let checks = b(treeData.value)
  selectedKeys.value = checks
  expandedKeys.value = checks
  emit('select', {uniqueCode: checks[0], deptList: deptTree})
}

function handleSelect(keys: string, e:any) {
  let keyStr = keys[0]+',';
  if (null != e.selectedNodes && e.selectedNodes.length > 0
    && null != e.selectedNodes[0].children &&  e.selectedNodes[0].children.length > 0){
    keyStr = getThisNodeStr(e.selectedNodes[0].children,keyStr)
  }
  emit('select', {uniqueCode: keyStr.substring(0,keyStr.length-1),deptList: []} )
}

function getThisNodeStr(list,str) {
  for (const nods of list) {
    str+=(nods.uniqueCode+',')
    if (null != nods.children && nods.children.length > 0){
      str = getThisNodeStr(nods.children,str)
    }
  }
  return str
}

const val = {
  id: null,
  uniqueCode: '',
  deptCode: '',
  deptName: '',
  parentId: '',
  uniqueCodeUser: ''
}

const [registerEditPage, { openModal: openEditPage }] = useModal()
const openAddPage = () => {
  openEditPage(true, {
    data: val,
    dynamicTenantId: dynamicCompany.value
  })
}

onMounted(async () => {
  await fetch()
})
async function saveData(data:any) {
  await useRouteApi(saveDept,{schemaName: dynamicCompany.value})(data)
  await fetch()
}

const totalChange=computed(()=>props.modelValue.total)
let companyName = computed(async () => {
  let a = props.modelValue.thisAdInfo
  companyName = props.modelValue.thisAdInfo.baseName
  if (Object.keys(a).length > 0) {
    dynamicCompany.value = a.accountMode
    // dynamicCompany.value = a.accId + '-' + defalutCompany.split('-')[2]
    await fetch()
  }
  return props.modelValue.thisAdInfo.baseName
})
watch(companyName, async () => {
    let a = props.modelValue.thisAdInfo
    companyName = props.modelValue.thisAdInfo.baseName
    if (Object.keys(a).length > 0) {
      dynamicCompany.value = a.accountMode
      // dynamicCompany.value = a.accId + '-' + defalutCompany.split('-')[2]
      await fetch()
    }
  }
)
</script>
<style scoped>
.bg-white{
  width: 250px !important;
  min-height: 250px !important;
  height: calc(100% - 170px);
  border: 1px #cccccc solid;
  background:white !important;
  margin-right: .2%;
  padding-bottom: 20px;
}
</style>
