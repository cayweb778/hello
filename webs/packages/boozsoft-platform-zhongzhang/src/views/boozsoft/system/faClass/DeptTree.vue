<template>
  <div class="bg-white mr-0 overflow-hidden">
    <div style="width: 100%;height: 50px;">
      <label style="font-size: 20px;line-height: 50px;margin-left: 20px;">资产分类列表</label>
<!--      <div style="float: right;margin-right: 20px;margin-top: 10px;">-->
<!--        <a style="cursor: pointer;color: blue;" @click="openAddPage()">-->
<!--          <PlusOutlined/>-->
<!--          添加</a>-->
<!--      </div>-->
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
import {computed, onMounted, ref, watch} from 'vue'

import {BasicTree} from '/@/components/Tree'
import DeptEdit from './popup/daptEdit.vue'
import {useModal} from '/@/components/Modal'

import {
  PlusOutlined
} from '@ant-design/icons-vue'
import {saveDept} from '/@/api/record/system/dept'
import {GetDeptTreeByFlag} from '/@/api/sys/dept'
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
  let deptTree = await useRouteApi(GetDeptTreeByFlag, {schemaName: dynamicCompany.value})()

  function a(deptTree: any) {
    deptTree.forEach((item: any) => {
      if (item.children != null) {
        a(item.children)
      }
      item.value = '[' + item.deptCode + ']  ' + item.deptName
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
  // treeData.value.push(...deptTree)
  // treeData.value = (await getDeptList())
  // let checks = b(deptTree)
  let checks = b(treeData.value)
  selectedKeys.value = checks
  expandedKeys.value = checks
  emit('select', checks[0])
}

function handleSelect(keys: string, e: any) {
  emit('select', keys[0])
  console.log(keys, e)
}

const val = {
  id: null,
  uniqueCode: '',
  deptCode: '',
  deptName: '',
  parentId: '',
  uniqueCodeUser: ''
}
const [registerEditPage, {openModal: openEditPage}] = useModal()
const openAddPage = () => {
  openEditPage(true, {
    data: val,
    dynamicTenantId: dynamicCompany.value
  })
}

onMounted(async () => {
  await fetch()
})

async function saveData(data: any) {
  await useRouteApi(saveDept, {schemaName: dynamicCompany.value})(data)
  await fetch()
}

const totalChange = computed(() => props.modelValue.total)
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
)
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
