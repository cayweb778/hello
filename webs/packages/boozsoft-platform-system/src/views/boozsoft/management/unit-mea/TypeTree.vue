<template>
  <div class="bg-white mr-0 overflow-hidden">
    <div style="width: 100%;height: 50px;">
      <label style="font-size: 20px;line-height: 50px;margin-left: 20px;">计量单位</label>
      <div style="float: right;margin-right: 20px;margin-top: 10px;">
<!--        <a-popover placement="bottom">
          <a-button style="padding: 0px 4px; height: 20px;">
            <EllipsisOutlined/>
          </a-button>
          <template #content>
            <span class="p_specifics group-btn-span-special2" @click="openAddPage()"><PlusOutlined/> 添加</span><br/>
            <span class="p_specifics group-btn-span-special2" @click="del()"><DeleteOutlined/> 删除</span>
          </template>
        </a-popover>-->
      </div>
    </div>
    <BasicTree
      title=""
      :click-row-to-expand="false"
      :autoExpandParent="true"
      :tree-data="treeData"
      :replace-fields="{ key: 'unitCode', title: 'value' }"
      v-model:selectedKeys="selectedKeys"
      v-model:expandedKeys="expandedKeys"
      @select="handleSelect"
    />

  </div>

</template>
<script setup lang="ts">
import {computed, defineExpose, onMounted, ref, watch} from 'vue'

import { BasicTree } from '/@/components/Tree'
import { useModal } from '/@/components/Modal'

import {
  PlusOutlined,
  DeleteOutlined,
  EllipsisOutlined
} from '@ant-design/icons-vue'
import {deletePsn, findAllSysUnitOfMeaType} from '/@/api/group/unit-mea'
import {Popover as APopover, message} from "ant-design-vue";
import {useMessage} from "/@/hooks/web/useMessage";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";

const emit=defineEmits(['select'])
const props=defineProps(['modelValue'])
const treeData:any = ref([])
const selectedKeys = ref<string[]>([])
const expandedKeys = ref<string[]>([])
const list = ref([])
// 默认账套编码
let defalutCompany = getCurrentAccountName(true)
// 切换后账套编码
const dynamicCompany = ref(defalutCompany)

async function fetch(accId) {
  treeData.value = []
  let deptTree = []

  await findAllSysUnitOfMeaType({})
    .then(res=>{
      let deptTree2 = res
      list.value = deptTree2
      console.log(deptTree2)

      function a(deptTree:any) {
        deptTree2.forEach((item: any) => {
          item.value =  item.unitName
          item.unitCode =  'k3'+item.unitCode
        })
      }

      function b(deptTree: any) {
        let arr: any = []
        arr.push(deptTree[0].children.unitCode)
        return arr
      }

      a(deptTree)

      deptTree.push({id: '1', key: '1', unitCode: 'k1', value: '单计量', children: []})
      deptTree.push({id: '2', key: '2', unitCode: 'k2', value: '多计量', children: deptTree2})

      treeData.value.push({id: '0', key: '0', unitCode: '0', value: '计量单位', children: deptTree})
      // treeData.value = deptTree
      // let checks = b(deptTree)
      let checks = b(treeData.value)
      selectedKeys.value = ['k1']
      expandedKeys.value = ['k1']
    })

   emit('select', {unitCode: 'k1', deptList: deptTree})
}

function handleSelect(keys: string, e:any) {

  let keyStr = keys[0]+',';
  if (null != e.selectedNodes && e.selectedNodes.length > 0
    && null != e.selectedNodes[0].children &&  e.selectedNodes[0].children.length > 0){
    keyStr = getThisNodeStr(e.selectedNodes[0].children,keyStr)
  }
  emit('select', keys[0])
}

function getThisNodeStr(list,str) {
  for (const nods of list) {
    str+=(nods.typeCode+',')
    if (null != nods.children && nods.children.length > 0){
      str = getThisNodeStr(nods.children,str)
    }
  }
  return str
}

const val = {
  id: '',
  uniqueCode: '',
  deptCode: '',
  deptName: '',
  parentId: '',
  uniqueCodeUser: ''
}

const {
  createConfirm,
  createWarningModal
} = useMessage()

const [registerEditPage, { openModal: openEditPage }] = useModal()
const openAddPage = () => {
  openEditPage(true, {
    data: val
  })
}

onMounted(async () => {
  await fetch()
})


/*const totalChange = computed(() => props.modelValue.total)
let companyName = computed(() => props.modelValue.thisAdInfo.baseName)
watch(companyName, async () => {
    let a = props.modelValue.thisAdInfo
    companyName = props.modelValue.thisAdInfo.baseName
    if (Object.keys(a).length > 0) {
      dynamicCompany.value = a.accountMode
      await fetch()
    }
  }
)*/

defineExpose({fetch})

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
