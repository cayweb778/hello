<template>
  <div class="bg-white mr-0 overflow-hidden">
    <div style="width: 100%;height: 50px;">
      <label style="font-size: 20px;line-height: 50px;margin-left: 20px;">现金流量项目</label>
      <div style="float: right;margin-right: 20px;margin-top: 10px;">
      </div>
    </div>
    <BasicTree
      style="height: 550px;"
      title=""
      :click-row-to-expand="false"
      :tree-data="treeData"
      :replace-fields="{ key: 'id', title: 'value' }"
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
import {computed, defineExpose, onMounted, ref, watch} from 'vue'

import {BasicTree} from '/@/components/Tree'
import {useModal} from '/@/components/Modal'

import {
  PlusOutlined
} from '@ant-design/icons-vue'
import {saveDept,GetDeptTreeByFlag} from '/@/api/record/system/fa-zj'
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getTypeList} from "/@/api/record/system/origin-project-cash";

const emit = defineEmits(['select'])
const treeData: any = ref([])
const selectedKeys = ref<string[]>([])
const expandedKeys = ref<string[]>([])
const props = defineProps(['modelValue'])
// 默认账套编码
let defalutCompany = getCurrentAccountName(true)
// 切换后账套编码
const dynamicCompany = ref(defalutCompany)
// 默认账套编码  
let defalutCompanyCodeValue = ''

async function fetch(accId) {
  function a(deptTree: any) {
    deptTree.forEach((item: any) => {
      if (item.children != null) {
        a(item.children)
      }
      item.value = item.zjName
    })
  }

  function b(deptTree: any) {
    let arr: any = []
    arr.push(deptTree[0].id)
    return arr
  }

  treeData.value = []

  console.log(props.modelValue)
  //获取当前会计准则下类别list
  let list = await getTypeList({
    accStandard: props.modelValue.accStandard,
    originId:props.modelValue.orientation,
    iyear:props.modelValue.orgIyear
  })
  list.forEach(item=>{
    item.value = '[' + item.projectType + ']  ' + item.projectTypeName
    item.id = item.projectType
  })
  treeData.value.push({id:'0',key:'',value: '类别',children: list})

  let checks = b(treeData.value)
  selectedKeys.value = checks
  expandedKeys.value = checks
  emit('select', checks[0])
}

function handleSelect(keys: string, e: any) {
  let keyStr = keys[0]+',';

  if (null != e.selectedNodes && e.selectedNodes.length > 0
    && null != e.selectedNodes[0].children &&  e.selectedNodes[0].children.length > 0){
    keyStr = getThisNodeStr(e.selectedNodes[0].children,keyStr)
  }
  emit('select', keyStr.substring(0,keyStr.length-1))
  // emit('select', keys[0])
  // console.log(keys, e)
}

function getThisNodeStr(list,str) {
  for (const nods of list) {
    str+=(nods.id+',')
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
  uniqueCodeUser: '',
  check: ''
}

defineExpose({fetch})
</script>
<style scoped>
.bg-white {
  width: 250px !important;
  min-height: 250px !important;
  height: calc(100% - 170px);
  border: 1px #cccccc solid;
  background: white !important;
  margin-right: .2%;
}
</style>
