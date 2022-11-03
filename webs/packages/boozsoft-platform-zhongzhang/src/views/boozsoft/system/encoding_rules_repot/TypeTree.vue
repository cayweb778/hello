<template>
  <div class="bg-white mr-0 overflow-hidden">
    <div style="width: 100%;height: 40px;">
      <label style="font-size: 20px;line-height: 50px;margin-left: 20px;">主数据档案</label>
<!--      <div style="float: right;margin-right: 20px;margin-top: 10px;">
        <a style="cursor: pointer;color: blue;" @click="openAddPage()"><PlusOutlined /> 添加</a>
      </div>-->
    </div>
    <BasicTree
      title=""
      :click-row-to-expand="false"
      :autoExpandParent="true"
      :tree-data="treeData"
      :replace-fields="{ key: 'key', title: 'value' }"
      v-model:selectedKeys="selectedKeys"
      v-model:expandedKeys="expandedKeys"
      @select="handleSelect"
    />
  </div>

</template>
<script setup lang="ts">
import {computed, onMounted, ref, watch} from 'vue'

import { BasicTree } from '/@/components/Tree'
import { useModal } from '/@/components/Modal'
import {initDynamics,getQz} from "./data";

import {
  PlusOutlined
} from '@ant-design/icons-vue'

const emit=defineEmits(['select'])
const props=defineProps(['modelValue'])
const treeData:any = ref([])
const selectedKeys = ref<string[]>(['1-1'])
const expandedKeys = ref<string[]>([])

async function fetch() {
  //默认选中第一个
  let res = initDynamics().daList
  let deptTree = res
  treeData.value.push({id: '0', key: '0', value: '全部档案', children: deptTree})
  selectedKeys.value = ['1-1']
  expandedKeys.value = ['1-1']
  emit('select', {key: '1-1', deptList: deptTree})
}

function handleSelect(keys: string, e:any) {
  let keyStr = keys[0]+',';
  if (null != e.selectedNodes && e.selectedNodes.length > 0
    && null != e.selectedNodes[0].children &&  e.selectedNodes[0].children.length > 0){
    keyStr = getThisNodeStr(e.selectedNodes[0].children,keyStr)
  }
  let s = keyStr.substring(0,keyStr.length-1)
  if(typeof(s) == "undefined" || s.indexOf(',')>0) {
    s = '0'
  }
  emit('select', {key: s,deptList: []} )
}

function getThisNodeStr(list,str) {
  for (const nods of list) {
    str+=(nods.key+',')
    if (null != nods.children && nods.children.length > 0){
      str = getThisNodeStr(nods.children,str)
    }
  }
  return str
}


onMounted(async () => {
  await fetch()
})

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
