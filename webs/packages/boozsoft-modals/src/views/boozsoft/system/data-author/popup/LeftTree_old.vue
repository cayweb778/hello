<template>
  <div class="bg-white m-4 mr-0 overflow-hidden" :style="{height: windowHeight+'px'}">
    <div style="width: 100%;height: 50px;text-align: center;">
      <label style="font-size: 20px;line-height: 50px;">授权模块</label>
    </div>
    <BasicTree
      :click-row-to-expand="false"
      :tree-data="treeData"
      :replace-fields="{ key: 'key', title: 'name' }"
      v-model:expandedKeys="expandedKeys"
      v-model:selectedKeys:="selectedKeys"
      v-model:checkedKeys="checkedKeys"
      @select="handleSelect"
    >
    </BasicTree>
  </div>
</template>
<script setup lang="ts">
import {inject, onMounted, ref,watch} from 'vue';
import { BasicTree } from '/@/components/Tree';

import {JsonTool} from "/@/api/task-api/tools/universal-tools";

const windowHeight = (window.innerHeight - 225)
const emit=defineEmits(['select'])
const treeData:any = ref([])
const selectedKeys = ref<string[]>([])
const checkedKeys = ref<string[]>([])
const expandedKeys = ref<string[]>([])
const treeList = inject('treeList'); //消费者
const lastCheckList = inject('lastCheckList'); //消费者
const theCheckList = inject('theCheckList'); //消费者
async function fetch() {
  // let list = [{key: 'all',name: '主数据',children: treeList}]
  console.log(treeList.value)
  // treeData.value = list
    expandedKeys.value = ['all']
  checkedKeys.value = []
  handleSelect(checkedKeys.value)
}

function handleSelect(keys: string, e:any) {
  if (expandedKeys.value.indexOf(keys[0]) == -1)
  emit('select', {keys:keys})
}

watch(() => lastCheckList.value,async (newValue, oldValue) => {
  checkedKeys.value = newValue
  if (theCheckList.value?.length > 0 && theCheckList.value[0] != null){
    selectedKeys.value = theCheckList.value
  }else {
    selectedKeys.value = [newValue[0]]
  }
  handleSelect(selectedKeys.value)
});

watch(() => treeList.value,async (newValue, oldValue) => {
  treeData.value =  treeList.value.map(it=>{
    it.slots = { title: 'titleMe'}
    return it;
  })
  debugger
    expandedKeys.value = treeList.value.map(it=>it.name)
});

onMounted(async () => {
})
</script>
<style scoped>
.bg-white{
  width: 250px !important;
  min-height: 250px !important;
  border: 1px #cccccc solid;
  background:white !important;
  margin: 0%;
  margin-right: .2%;
}
</style>
