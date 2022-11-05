<template>
  <div class="bg-white mr-0 overflow-hidden">
<!--    <div style="width: 100%;height: 50px;">
      <label style="font-size: 20px;line-height: 50px;margin-left: 20px;">人员分类</label>
      <div style="float: right;margin-right: 20px;margin-top: 10px;">
        <a-popover placement="bottom">
          <a-button style="padding: 0px 4px; height: 20px;">
            <EllipsisOutlined/>
          </a-button>
          <template #content>
            <span class="p_specifics group-btn-span-special2" @click="openAddPage()"><PlusOutlined/> 添加</span><br/>
            <span class="p_specifics group-btn-span-special2" @click="openEdit()"><FormOutlined/> 修改</span><br/>
            <span class="p_specifics group-btn-span-special2" @click="del()"><DeleteOutlined/> 删除</span>
          </template>
        </a-popover>
      </div>
    </div>-->
    <div style="width: 100%; height: 32px;background-color: #d9d9d9;padding-top:5px;font-weight: bold;text-align: center;">
      人员分类
      <div style="float: right;">
        <a style="color: black;font-size: 14px;" @click="openAddPage()"><PlusCircleTwoTone /></a>&emsp;
      </div>
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
  PlusOutlined,
  EllipsisOutlined,
  FormOutlined,
  DeleteOutlined,
  PlusCircleTwoTone
} from '@ant-design/icons-vue'
import {deletePsnType, savePsnType} from '/@/api/record/system/psn-type'
import {psnTypeFindAll} from "/@/api/psn-type/psn-type";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {Popover as APopover, message} from "ant-design-vue";
import {useMessage} from "/@/hooks/web/useMessage";
import {psnFindAll} from "/@/api/psn/psn";
import {
  findByPsnTypeIdAndAccId,
  findByTenantIdAndUniqueCode, save,
  saveList
} from "/@/api/record/system/group-psn-type-account";

const {
  createConfirm,
  createWarningModal
} = useMessage()

const emit=defineEmits(['select'])
const props=defineProps(['modelValue'])
const treeData:any = ref([])
const selectedKeys = ref<string[]>([])
const expandedKeys = ref<string[]>([])

// 默认账套编码
let defalutCompany = getCurrentAccountName(true)
// 切换后账套编码
const dynamicCompany = ref(defalutCompany)
// 默认账套编码
let defalutCompanyCodeValue = ''
// 切换后账套编码
const list = ref([])

async function fetch() {
  let res = await useRouteApi(psnTypeFindAll,{schemaName: dynamicCompany.value})({})
  let deptTree = res.items
  list.value = deptTree

  function a(deptTree:any) {
    deptTree.forEach((item: any) => {
      item.value = '[' + item.psnTypeCode + ']  ' + item.psnTypeName
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
  // emit('select', {uniqueCode: keyStr.substring(0,keyStr.length-1),deptList: []} )
  emit('select', {uniqueCode: keys[0],deptList: []} )
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

const val = {
  id: null,
  uniqueCode: '',
  psnTypeCode: '',
  psnTypeName: ''
}

const [registerEditPage, { openModal: openEditPage }] = useModal()
const openAddPage = () => {
  openEditPage(true, {
    data: val,
    dynamicTenantId: dynamicCompany.value,
    isState: '0'
  })
}

const totalChange=computed(()=>props.modelValue.total)
let companyName = computed(async () => {
  let a = props.modelValue.thisAdInfo
  companyName = props.modelValue.thisAdInfo.baseName
  if (Object.keys(a).length > 0) {
    dynamicCompany.value = a.accountMode
    accId.value = a.accId
    // dynamicCompany.value = a.accId + '-' + defalutCompany.split('-')[2]
    await fetch()
  }
  return props.modelValue.thisAdInfo.baseName
})
const accId = ref()
watch(companyName, async () => {
    let a = props.modelValue.thisAdInfo
    companyName = props.modelValue.thisAdInfo.baseName
    if (Object.keys(a).length > 0) {
      dynamicCompany.value = a.accountMode
      accId.value = a.accId
      // dynamicCompany.value = a.accId + '-' + defalutCompany.split('-')[2]
      await fetch()
    }
  }
)

onMounted(async () => {
  await fetch()
})
async function saveData(data:any) {
  await useRouteApi(savePsnType,{schemaName: dynamicCompany.value})(data)
  await fetch()
}

function openEdit(){
  if (selectedKeys.value.length > 0 && selectedKeys.value[0] != '0') {
    let items: any = list.value.filter((item: any) => item.uniqueCode == selectedKeys.value[0])
    openEditPage(true, {
      data: items[0],
      dynamicTenantId: dynamicCompany.value,
      isState: '1'
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '提示',
      content: '请选择需要修改的内容！'
    })
  }
}

async function del() {
  if (selectedKeys.value.length > 0 && selectedKeys.value[0] != '0') {
    let items: any = list.value.filter((item: any) => item.uniqueCode == selectedKeys.value[0])
    const psnList = await useRouteApi(psnFindAll, {schemaName: dynamicCompany.value})({})
    for (let a = 0; a < psnList.items.length; a++) {
      const psn = psnList.items[a]
      if (items.uniqueCode == '9999') {
        createWarningModal({
          iconType: 'warning',
          title: '删除',
          content: '编码为“9999”的人员类别为系统预制类别不允许删除！'
        })
        return false
      }
      if (psn.uniquePsnType == items.uniqueCode) {
        createWarningModal({
          iconType: 'warning',
          title: '删除',
          content: '<span style="font-weight: bold;color: red;">[' + items.psnTypeName + ']</span>人员类别中经已存在人员，不能进行删除操作！'
        })
        return false
      }
    }
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        const item1 = await findByTenantIdAndUniqueCode(dynamicCompany.value,items.uniqueCode)
        if (item1.length>0) {
          item1[0].flag = '0'
          item1[0].acceptUser = ''
          item1[0].acceptDate = ''
          await save(item1[0])
        }
        await useRouteApi(deletePsnType, {schemaName: dynamicCompany.value})(items)
        message.success('删除成功！')
        await fetch()
        return true
      },
      onCancel: () => {
        return false
      }
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '提示',
      content: '请选择需要修改的内容！'
    })
  }
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
  padding-bottom: 20px;
}
</style>
