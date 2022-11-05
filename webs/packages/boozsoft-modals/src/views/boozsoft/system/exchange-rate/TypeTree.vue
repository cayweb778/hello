<template>
  <div class="bg-white mr-0 overflow-hidden">
    <div data-v-17ef9f20="" style="width: 100%; height: 32px; background-color: rgb(217, 217, 217); padding-top: 5px; font-weight: bold; text-align: center;"> 币种列表 </div>
    <BasicTree
      title=""
      :click-row-to-expand="false"
      :tree-data="treeData"
      :replace-fields="{ key: 'uniqueCode', title: 'value' }"
      v-model:selectedKeys="selectedKeys"
      v-model:expandedKeys="expandedKeys"
      @select="handleSelect"
    />
  </div>

</template>
<script setup lang="ts">
import { inject, onMounted, ref} from 'vue'

import { BasicTree } from '/@/components/Tree'
import { useModal } from '/@/components/Modal'

import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {findAvailablesAcc} from "/@/api/boozsoft/group/UsedForeignCurrency";
import {findAllWbhvInfo} from "/@/api/record/system/financial-settings";
import {useMessage} from "/@/hooks/web/useMessage";

const emit=defineEmits(['select'])
const props=defineProps(['modelValue'])

const accountInfo = inject('accountInfo')
const treeData:any = ref([])
const selectedKeys = ref<string[]>([])
const expandedKeys = ref<string[]>([])

const {
  createWarningModal
} = useMessage()
async function fetch() {
  // 获取所以常用外币
  let currList = await useRouteApi(findAvailablesAcc,{schemaName: accountInfo.value.accountMode})({})
  let infoList = await useRouteApi(findAllWbhvInfo,{schemaName: accountInfo.value.accountMode})({iyear: accountInfo.value.iyear})
  if (currList.length > 0){
    if (infoList.length > 0){
      let deptTree = infoList
      // 获取所有常用外币
      function a(deptTree:any) {
        deptTree.forEach((item: any) => {
          item.uniqueCode = item.foreignCode
          item.value =  item.foreignCode + ' ' + item.foreignCode
        })
      }
      a(deptTree)
      treeData.value = [{ uniqueCode: '0', value: '外币币种', children: deptTree}]
      selectedKeys.value = [treeData.value[0]['children'][0].uniqueCode]
      expandedKeys.value =  [treeData.value[0].uniqueCode]
      emit('select', {uniqueCode: treeData.value[0]['children'][0].uniqueCode,currencyList: currList,infoList: infoList,treeReload: fetch})
    }else {
      treeData.value = [{ uniqueCode: '0', value: '外币币种', children: []}]
      emit('select', {uniqueCode: '',currencyList: currList,infoList: [],treeReload: fetch})
    }
  }else {
    createWarningModal({title:'温馨提示',content: '请先前往常用外币菜单进行添加！'})
    emit('select', {uniqueCode: '',currencyList: [],infoList: [],treeReload: fetch})
  }
}

function handleSelect(keys: string, e:any) {
  if (keys[0] != '0'){
    emit('select', {uniqueCode: keys[0]} )
  }
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
