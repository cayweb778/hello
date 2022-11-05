<template>
  <div class="bg-white mr-0 overflow-hidden">
<!--    <div style="width: 100%;height: 50px;">
      <label style="font-size: 20px;line-height: 50px;margin-left: 20px;">职务类别</label>
      <div style="float: right;margin-right: 20px;margin-top: 10px;">
        <a-popover placement="bottom">
          <a-button style="padding: 0px 4px; height: 20px;">
            <EllipsisOutlined/>
          </a-button>
          <template #content>
            <span class="p_specifics group-btn-span-special2" @click="openAddPage()"><PlusOutlined/> 添加</span><br/>
            <span class="p_specifics group-btn-span-special2" @click="del()"><DeleteOutlined/> 删除</span><br/>
          </template>
        </a-popover>
      </div>
    </div>-->
    <div style="width: 100%; height: 32px;background-color: #d9d9d9;padding-top:5px;font-weight: bold;text-align: center;">
      职务类别
      <div style="float: right;margin-right: 20px;">
        <!--        <a style="color: black;font-size: 14px;" @click="openAddPage()"><PlusCircleTwoTone /></a>&emsp;-->
        <a-popover placement="bottom">
          <!--          <a-button style="padding: 0px 4px; height: 20px;">
                      <EllipsisOutlined/>
                    </a-button>-->
          <a style="font-size: 14px;"><FormOutlined/></a>
          <template #content>
            <span class="p_specifics group-btn-span-special2" @click="openAddPage()"><PlusOutlined/> 添加</span><br/>
            <span class="p_specifics group-btn-span-special2" @click="del()"><DeleteOutlined/> 删除</span><br/>
          </template>
        </a-popover>
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
    <DeptEdit
      @save="saveData"
      @register="registerEditPage"
    />
  </div>

</template>
<script setup lang="ts">
import {computed, defineExpose, onMounted, ref, watch} from 'vue'

import {BasicTree} from '/@/components/Tree'
import DeptEdit from './popup/daptEdit.vue'
import {useModal} from '/@/components/Modal'

import {
  PlusOutlined,DeleteOutlined,EllipsisOutlined,FormOutlined
} from '@ant-design/icons-vue'
import {
  saveJobType,
  GetDeptTreeByFlag,
  getDeptListById,
  delTypeById
} from '/@/api/record/system/job-file'
import {Popover as APopover, message} from "ant-design-vue";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";

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

async function fetch() {
  let deptTree = await useRouteApi(GetDeptTreeByFlag,{schemaName: dynamicCompany.value})({})
  if (deptTree.length == 0){
    const item={
      id: null,
      ecCode: '001',
      ecName: '通用类别',
      parentId: '0',
      flag: '1'
    }
  }
  console.log(deptTree)
  function a(deptTree: any) {
    deptTree.forEach((item: any) => {
      if (item.children != null) {
        a(item.children)
      }
      item.value = '[' + item.ecCode + ']  ' + item.ecName
    })
  }

  function b(deptTree: any) {
    let arr: any = []
    arr.push(deptTree[0].id)
    return arr
  }

  a(deptTree)
  // deb
  treeData.value = []
  treeData.value.push({id:'0',key:'',value: companyName,children: deptTree})

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
const [registerEditPage, {openModal: openEditPage}] = useModal()
const openAddPage = () => {
  val.check = selectedKeys.value[0]
  openEditPage(true, {
    data: val,
    dynamicTenantId: dynamicCompany.value
  })
}
 const del = async() => {
  val.check = selectedKeys.value[0]
  console.log(val.check)
  let a = await useRouteApi(delTypeById, {schemaName: dynamicCompany.value})(val.check)
   console.log(a)
  await fetch()
}

onMounted(async () => {
  await fetch()
})

async function saveData(data: any) {
  console.log(data)
  await useRouteApi(saveJobType, {schemaName: dynamicCompany.value})(data)
  await fetch()
}

const totalChange = computed(() => props.modelValue.total)
let companyName = computed(() => props.modelValue.thisAdInfo.baseName)
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

.p_specifics {
  text-align: center;
  width: 80px;
  padding: 3px 10px;
  margin-bottom: 0;
}

.p_specifics:hover {
  cursor: pointer;
  background-color: #0096c7;
  color: white;
}
</style>
