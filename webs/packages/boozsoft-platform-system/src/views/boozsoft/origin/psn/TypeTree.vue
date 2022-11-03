<template>
  <div class="bg-white mr-0 overflow-hidden">
    <div style="width: 100%;height: 50px;">
      <label style="font-size: 20px;line-height: 50px;margin-left: 20px;">人员分类</label>
<!--      <div style="float: right;margin-right: 20px;margin-top: 10px;">
        <a style="cursor: pointer;color: blue;" @click="openAddPage()"><PlusOutlined /> 添加</a>
      </div>-->
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
import { psnTypeFindAll,savePsnType } from '/@/api/record/system/origin-psn-type'

const emit=defineEmits(['select'])
const props=defineProps(['modelValue'])
const treeData:any = ref([])
const selectedKeys = ref<string[]>([])
const expandedKeys = ref<string[]>([])

async function fetch() {
  treeData.value = []
  let res = await psnTypeFindAll(originId.value)
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
  treeData.value = []
  treeData.value.push({id: '0', key: '0', uniqueCode: '0', value: '全部', children: deptTree})
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

const originId=computed(()=>props.modelValue.originId)
watch(originId, async () => {
  await fetch()
})

const val = {
  id: null,
  uniqueCode: '',
  deptCode: '',
  deptName: '',
  parentId: '',
  uniqueCodeUser: '',
  originId: ''
}

const [registerEditPage, { openModal: openEditPage }] = useModal()
const openAddPage = () => {
  val.originId = originId.value
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
