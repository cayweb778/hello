<template>
  <div class="bg-white m-4 mr-0 overflow-hidden">
    <div style="width: 100%;height: 50px;">
      <label style="font-size: 20px;line-height: 50px;margin-left: 20px;">单位部门列表</label>
      <div style="float: right;margin-right: 20px;margin-top: 10px;">
        <a style="cursor: pointer;color: blue;" @click="openAddPage()"><PlusOutlined /> 添加</a>
      </div>
    </div>
    <BasicTree
      title=""
      :click-row-to-expand="false"
      :tree-data="treeData"
      :replace-fields="{ key: 'id', title: 'value' }"
      v-model:selectedKeys="selectedKeys"
      @select="handleSelect"
    />
    <DeptEdit
      @save="saveData"
      @register="registerEditPage"
    />
  </div>

</template>
<script setup lang="ts">
import { onMounted, ref } from 'vue'

import { BasicTree } from '/@/components/Tree'
import DeptEdit from './popup/daptEdit.vue'
import { useModal } from '/@/components/Modal'

import {
  PlusOutlined
} from '@ant-design/icons-vue'
import { saveDept } from '/@/api/record/system/dept'
import { GetDeptTreeByFlag } from '/@/api/sys/dept'

const emit=defineEmits([])
const treeData:any = ref([])
const selectedKeys = ref<string[]>([])
async function fetch() {
  const deptTree = await GetDeptTreeByFlag()
  function a(deptTree:any) {
    deptTree.forEach((item: any)  => {
      if (item.children != null) {
        a(item.children)
      }
      item.value = '(' + item.deptCode + ')' + item.deptName
    })
  }
  function b(deptTree:any) {
    let arr = []
    arr.push(deptTree[0].id)
   return  arr
  }
  a(deptTree)
  // deb
  treeData.value = deptTree
  // treeData.value = (await getDeptList())
  let checks =  b(deptTree)
  selectedKeys.value = checks
  emit('select', checks[0])
}

function handleSelect(keys: string, e:any) {
  emit('select', keys[0])
  console.log(keys, e)
}

const val = {
  id: '',
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
  await saveDept(data)
  await fetch()
}
</script>
<style scoped>
.bg-white{
  width:20% !important;
  height: calc(100% - 180px);
  border: 1px #cccccc solid;
  background:white !important;
}
</style>
