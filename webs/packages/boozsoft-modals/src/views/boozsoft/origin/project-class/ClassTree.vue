<template>
  <div class="bg-white mr-0 overflow-hidden">
    <div style="width: 100%;height: 50px;">
      <label style="font-size: 20px;line-height: 50px;margin-left: 20px;">分类列表</label>
      <div style="float: right;margin-right: 20px;margin-top: 10px;">
<!--        <a style="cursor: pointer;color: blue;" @click="openAddPage()">
          <PlusOutlined/>
          添加</a>-->
      </div>
    </div>
    <BasicTree
      title=""
      :click-row-to-expand="false"
      :tree-data="treeData"
      :replace-fields="{ key: 'id', title: 'value' }"
      v-model:selectedKeys="selectedKeys"
      v-model:expandedKeys="expandedKeys"
      @select="handleSelect"
    />
    <ClassEdit
      @save="saveData"
      @register="registerEditPage"
    />
  </div>
</template>
<script setup lang="ts">
import { computed, onMounted, ref,  watch } from 'vue'

import { BasicTree } from '/@/components/Tree'
import ClassEdit from './popup/edit.vue'
import { useModal } from '/@/components/Modal'

import {
  PlusOutlined
} from '@ant-design/icons-vue'
import {findProClassTree, saveProClass} from '/@/api/record/system/origin-project-class'
import {useProjectStoreWidthOut} from "/@/store/modules/project";
import {useMessage} from "/@/hooks/web/useMessage";

const projectStore = useProjectStoreWidthOut()

const {
  createWarningModal
} = useMessage()

const emit = defineEmits(['select'])
const treeData: any = ref([])
const selectedKeys = ref<string[]>([])
const expandedKeys = ref<string[]>([])
const props = defineProps(['modelValue'])

async function fetch() {
  /*await projectStore.projectClassTree().then((data) => {
    treeData.value = data.value
  })*/
  const classTree = await findProClassTree(originId.value)

  function proClassTree(classTree: any) {
    classTree.forEach(
      (item: any) => {
        if (item.children != null) {
          proClassTree(item.children)
        }
        item.id = item.id
        item.value = '(' + item.projectClassCode + ')' + item.projectClassName
      })
  }

  function b(classTree: any) {
    let arr: any = []
    arr.push(classTree[0].id)
    return arr
  }

  proClassTree(classTree)
  treeData.value = []
  treeData.value.push({
    id: '0',
    key: '0',
    uniqueCode: '0',
    projectClassCode: '0',
    value: '全部',
    children: classTree
  })
  let checks = b(treeData.value)
  selectedKeys.value = checks
  expandedKeys.value = checks
  emit('select', checks[0])
}

function handleSelect(keys: string, e: any) {
  let keyStr = keys[0]+',';
  if (null != e.selectedNodes && e.selectedNodes.length > 0 && null != e.selectedNodes[0].children &&  e.selectedNodes[0].children.length > 0){
    keyStr = getThisNodeStr(e.selectedNodes[0].children,keyStr)
  }
  emit('select', keyStr.substring(0,keyStr.length-1))
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

const originId=computed(()=>props.modelValue.originId)
watch(originId, async () => {
  await fetch()
})

const val = {
  uniqueCode: '',
  projectClassCode: '',
  projectClassName: '',
  projectCateCode: '',
  parentId: '',
  originId: ''
}
const [registerEditPage, {openModal: openEditPage}] = useModal()
const openAddPage = async () => {
  val.originId = originId.value
  openEditPage(true, {
    data: val,
    isState: '0'
  })
}

onMounted(async () => {
  await fetch()
})
async function saveData(data:any) {
  await saveProClass(data)
  await fetch()
}
</script>
<style scoped>
.bg-white {
  width: 300px !important;
  min-height: 250px !important;
  height: calc(100% - 170px);
  border: 1px #cccccc solid;
  background: white !important;
  margin-right: .2%;
}
</style>
