<template>
  <div class="bg-white mr-0 overflow-hidden">
    <BasicTree
      title=""
      :click-row-to-expand="false"
      :tree-data="treeData"
      :replace-fields="{ key: 'uniqueCode', title: 'value' }"
      v-model:selectedKeys="selectedKeys"
      v-model:expandedKeys="expandedKeys"
      @select="handleSelect"
    />
    <TypeEdit
      @save="saveData"
      @register="registerEditPage"
    />
  </div>

</template>
<script setup lang="ts">
import {computed, onMounted, ref, watch} from 'vue'

import { BasicTree } from '/@/components/Tree'
import TypeEdit from './popup/typeEdit.vue'
import { useModal } from '/@/components/Modal'

import {
  PlusOutlined
} from '@ant-design/icons-vue'
import { psnTypeFindAll,savePsnType } from '/@/api/record/system/group-psn-type'

const emit=defineEmits(['select'])
const props=defineProps(['modelValue'])
const treeData:any = ref([])
const selectedKeys = ref<string[]>([])
const expandedKeys = ref<string[]>([])

async function fetch() {
  let res = await psnTypeFindAll()
  let deptTree = res.items
  function a(deptTree:any) {
    deptTree.forEach((item: any) => {
      item.value = '(' + item.psnTypeCode + ')' + item.psnTypeName
    })
  }

  function b(deptTree: any) {
    let arr: any = []
    arr.push(deptTree[0].uniqueCode)
    return arr
  }

  a(deptTree)
  treeData.value.push({id: '0', key: '0', uniqueCode: '0', value: '外币币种', children: deptTree})
  // treeData.value = deptTree
  // let checks = b(deptTree)
  let checks = b(treeData.value)
  selectedKeys.value = checks
  expandedKeys.value = checks
  emit('select', {uniqueCode: checks[0], deptList: deptTree})
}

function handleSelect(keys: string, e:any) {
  let keyStr = keys[0]+',';
  if (null != e.selectedNodes && e.selectedNodes.length > 0
    && null != e.selectedNodes[0].props.children &&  e.selectedNodes[0].props.children.length > 0){
    keyStr = getThisNodeStr(e.selectedNodes[0].props.children,keyStr)
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
    data: val
  })
}

onMounted(async () => {
  await fetch()
})
async function saveData(data:any) {
  await savePsnType(data)
  await fetch()
}

</script>
<style scoped>
.bg-white{
  width: 250px !important;
  min-height: 250px !important;
  height: calc(100% - 160px);
  border: 1px #cccccc solid;
  background:white !important;
  margin-right: .2%;
}
</style>
