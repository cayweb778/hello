<template>
  <div class="bg-white mr-0 overflow-hidden">
<!--    <div style="width: 100%;height: 50px;">
      <label style="font-size: 20px;line-height: 50px;margin-left: 20px;">分类列表</label>
      <div style="float: right;margin-right: 20px;margin-top: 10px;">
        <a style="cursor: pointer;color: blue;" @click="openAddPage()">
          <PlusOutlined/>
          添加</a>
      </div>
    </div>-->
    <div style="width: 100%; height: 32px;background-color: #d9d9d9;padding-top:5px;font-weight: bold;text-align: center;">
      分类列表
      <div style="float: right;">
        <a style="color: black;font-size: 14px;" @click="openAddPage()"><PlusCircleTwoTone /></a>&emsp;
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
import ClassEdit from './popup/classEdit.vue'
import { useModal } from '/@/components/Modal'

import {
  PlusOutlined,PlusCircleTwoTone
} from '@ant-design/icons-vue'
import {findProClassTree, saveProClass} from '/@/api/record/system/project_class'
import {useProjectStoreWidthOut} from "/@/store/modules/project";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {cateFindStateFlag} from "/@/api/project_category/project_category";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {useMessage} from "/@/hooks/web/useMessage";
import {getProjectItemList} from "/@/api/record/system/project-item";

const projectStore = useProjectStoreWidthOut()
// import {projectStore} from "/@/store/modules/project";

const {
  createWarningModal
} = useMessage()

const emit = defineEmits(['select'])
const treeData: any = ref([])
const selectedKeys = ref<string[]>([])
const expandedKeys = ref<string[]>([])
const props = defineProps(['modelValue'])
/*const treeData: any = ref([])
const selectedKeys = ref<string[]>([])
const expandedKeys = ref<string[]>([])*/
// 默认账套编码
let defalutCompany = getCurrentAccountName(true)
// 切换后账套编码
const dynamicCompany = ref(defalutCompany)
// 默认账套编码
let defalutCompanyCodeValue = ''

// 切换后账套编码
async function fetch() {
  /*await projectStore.projectClassTree().then((data) => {
    treeData.value = data.value
  })*/
  const classTree = await useRouteApi(findProClassTree, {schemaName: dynamicCompany.value})({projectCateCode:itemCode.value,projectClassCtl:projectClassCtl.value})

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
  // treeData.value = classTree
  treeData.value = []
  treeData.value.push({
    id: '0',
    key: '0',
    uniqueCode: '0',
    projectClassCode: '0',
    value: companyName,
    children: classTree
  })
  // let checks = b(classTree)
  let checks = b(treeData.value)
  selectedKeys.value = checks
  expandedKeys.value = checks
  emit('select', checks[0])
}

/*const totalChange = computed(() => props.modelValue.total)
let companyName = computed(() => props.modelValue.thisAdInfo.baseName)
watch(totalChange, async () => {
    let a = props.modelValue.thisAdInfo
    companyName = props.modelValue.thisAdInfo.baseName
    if (Object.keys(a).length > 0) {
      dynamicCompany.value = a.accountMode
      // dynamicCompany.value = a.accId + '-' + defalutCompany.split('-')[2]
      await fetch()
    }
  }
)*/

const itemCode = ref('')
const projectClassCtl = ref('')
const totalChange = computed(async () => {
  let a = props.modelValue.thisAdInfo
  companyName = props.modelValue.thisAdInfo.baseName
  itemCode.value = props.modelValue.itemCode
  projectClassCtl.value = props.modelValue.projectClassCtl
  if (Object.keys(a).length > 0) {
    dynamicCompany.value = a.accountMode
    // dynamicCompany.value = a.accId + '-' + defalutCompany.split('-')[2]
    await fetch()
  }
  return props.modelValue.thisAdInfo
})
let companyName = computed(() => props.modelValue.thisAdInfo.baseName)
watch(totalChange, async () => {
    let a = props.modelValue.thisAdInfo
    companyName = props.modelValue.thisAdInfo.baseName
    itemCode.value = props.modelValue.itemCode
    projectClassCtl.value = props.modelValue.projectClassCtl
    if (Object.keys(a).length > 0) {
      dynamicCompany.value = a.accountMode
      // dynamicCompany.value = a.accId + '-' + defalutCompany.split('-')[2]
      await fetch()
    }
  }
)

/*const currentCateCode = computed(() => projectStore.getNum)
watch(currentCateCode, (abc) => {
  console.log(abc)
  fetch()
})*/

/*const dynamicTenantId=computed(()=>projectStore.getDynamicTenantId)
watch(dynamicTenantId,(dynamic)=>{
  console.log(dynamic)
  fetch()
})*/

function handleSelect(keys: string, e: any) {
  let keyStr = keys[0]+',';
  if (null != e.selectedNodes && e.selectedNodes.length > 0 && null != e.selectedNodes[0].children &&  e.selectedNodes[0].children.length > 0){
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
  uniqueCode: '',
  projectClassCode: '',
  projectClassName: '',
  projectCateCode: '',
  parentId: '',
  projectClassCtl: ''
}
const [registerEditPage, {openModal: openEditPage}] = useModal()
const openAddPage = async () => {
  // const cateList: any = await useRouteApi(cateFindStateFlag, {schemaName: dynamicCompany})()
  // if (cateList.length > 0) {
  if (projectClassCtl.value=='1'){
    const itemList: any = await useRouteApi(getProjectItemList,{schemaName: dynamicCompany})({})
    if (itemList==null || itemList.length==0){
      createWarningModal({
        iconType: 'warning',
        title: '提示',
        content: '暂无项目大类信息，请先添加项目大类档案！'
      })
      return false
    }
  }
  val.projectCateCode = itemCode.value
  val.projectClassCtl = projectClassCtl.value
    openEditPage(true, {
      data: val,
      // projectCateCode: currentCateCode,
      dynamicTenantId: dynamicCompany.value
    })
  // } else {
  //   createWarningModal({
  //     iconType: 'warning',
  //     title: '提示',
  //     content: '暂无项目大类信息，请先添加项目大类档案！'
  //   })
  // }
}

onMounted(async () => {
  await fetch()
})
async function saveData(data:any) {
  await useRouteApi(saveProClass, {schemaName: dynamicCompany})(data)
  await fetch()
}
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
