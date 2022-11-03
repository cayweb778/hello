<template>
  <div class="bg-white m-4 mr-0 overflow-hidden" :style="{height: windowHeight+'px'}">
    <div style="width: 100%;height: 50px;">
      <label style="font-size: 20px;line-height: 50px;margin-left: 20px;">功能模块</label>
    </div>
    <BasicTree
      title=""
      :click-row-to-expand="false"
      :tree-data="treeData"
      :replace-fields="{ key: 'key', title: 'name' }"
      v-model:expandedKeys="expandedKeys"
      v-model:selectedKeys:="selectedKeys"
      v-model:checkedKeys="checkedKeys"
      @select="handleSelect"
      @check="handleCheck"
    />
<!--      checkable-->
  </div>
</template>
<script setup lang="ts">
import {inject, onMounted, ref,watch} from 'vue';
import { BasicTree } from '/@/components/Tree';
import {} from "@vue/runtime-core";

const windowHeight = (window.innerHeight - 225)
const emit=defineEmits(['select'])
const treeData:any = ref([])
const selectedKeys = ref<string[]>([])
const checkedKeys = ref<string[]>([])
const expandedKeys = ref<string[]>([])
const roleList = inject('roleList'); //消费者
const lastCheckList = inject('lastCheckList'); //消费者
async function fetch() {
  treeData.value = [/*{key: 'org',name: '组织管理'},*/{key: 'all',name: '全部',children: roleList}]
  expandedKeys.value = ['all']
  checkedKeys.value = []
  handleSelect(checkedKeys.value)
}

function handleSelect(keys: string, e:any) {
  emit('select', {keys:keys,checkLen:checkedKeys.value.length})
}

function handleCheck(key) {
  if (checkedKeys.value.length == (roleList.value.length  + 1))
    emit('select', {keys:[],checkLen:checkedKeys.value.length})
}

watch(() => lastCheckList.value,async (newValue, oldValue) => {
  checkedKeys.value = newValue
  selectedKeys.value = [newValue[0]]
  handleSelect(checkedKeys.value)
});

onMounted(async () => {
  await fetch()
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
