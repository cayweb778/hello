<template>
  <div class="bg-white mr-0 overflow-hidden">
<!--    <div style="width: 100%;height: 50px;">
      <label style="font-size: 20px;line-height: 50px;margin-left: 20px;">摘要分类</label>
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
      摘要分类
      <div style="float: right;margin-right: 20px;">
        <!--        <a style="color: black;font-size: 14px;" @click="openAddPage()"><PlusCircleTwoTone /></a>&emsp;-->
        <a-popover placement="bottom">
<!--          <a-button style="padding: 0px 4px; height: 20px;">
            <EllipsisOutlined/>
          </a-button>-->
          <a style="font-size: 14px;"><FormOutlined/></a>
          <template #content>
            <span class="p_specifics group-btn-span-special2" @click="openAddPage()"><PlusOutlined/> 添加</span><br/>
            <span class="p_specifics group-btn-span-special2" @click="openEdit()"><FormOutlined/> 修改</span><br/>
            <span class="p_specifics group-btn-span-special2" @click="del()"><DeleteOutlined/> 删除</span>
          </template>
        </a-popover>
      </div>
    </div>
    <BasicTree
      title=""
      :click-row-to-expand="false"
      :tree-data="treeData"
      :replace-fields="{ key: 'classCode', title: 'value' }"
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
import {computed, onMounted, ref, watch} from 'vue'

import {BasicTree} from '/@/components/Tree'
import ClassEdit from './popup/class-edit.vue'
import {useModal} from '/@/components/Modal'

import {
  PlusOutlined,
  DeleteOutlined,
  EllipsisOutlined,
  FormOutlined
} from '@ant-design/icons-vue'
import {
  findAllApi,
  findByIdApi,
  saveApi,
  deleteByIdApi
} from '/@/api/boozsoft/account/AccvoucherCdigestClass'
import {findAllApi as findCdigestAllApi} from '/@/api/boozsoft/account/AccvoucherCdigest'
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {Popover as APopover, message} from "ant-design-vue";
import {useMessage} from "/@/hooks/web/useMessage";

const {
  createConfirm,
  createWarningModal
} = useMessage()

const emit = defineEmits(['select'])
const props = defineProps(['modelValue'])
const treeData: any = ref([])
const selectedKeys = ref<string[]>([])
const expandedKeys = ref<string[]>([])
// 默认账套编码
let defalutCompany = getCurrentAccountName(true)
// 切换后账套编码
const dynamicCompany = ref(defalutCompany)
// 默认账套编码
let defalutCompanyCodeValue = ''
// 切换后账套编码
const list:any = ref([])

async function fetch() {
  let classTree = await useRouteApi(findAllApi, {schemaName: dynamicCompany.value})({})
  list.value = classTree
  if (list.value.length == 0){
    const item = {
      id: null,
      classCode: '001',
      className: '通用摘要分类',
    }
    const fenlei = await useRouteApi(saveApi, {schemaName: dynamicCompany.value})(item)
    list.value.push(fenlei)
    classTree.push(fenlei)
  }

  function a(classTree: any) {
    classTree.forEach((item: any) => {
      if (item.children != null) {
        a(item.children)
      }
      item.value = item.classCode + '  -  ' + item.className
    })
  }

  function b(deptTree: any) {
    let arr: any = []
    arr.push(deptTree[0].classCode)
    return arr
  }

  a(classTree)
  // treeData.value = deptTree
  treeData.value = []
  treeData.value.push({id: '0', key: '', classCode: '0', value: companyName, children: classTree})
  // let checks = b(deptTree)
  let checks = b(treeData.value)
  selectedKeys.value = checks
  expandedKeys.value = checks
  emit('select', {classCode: checks[0], classList: classTree})
}

function handleSelect(keys: string, e: any) {
  let keyStr = keys[0] + ',';
  if (null != e.selectedNodes && e.selectedNodes.length > 0
    && null != e.selectedNodes[0].children && e.selectedNodes[0].children.length > 0) {
    keyStr = getThisNodeStr(e.selectedNodes[0].children, keyStr)
  }
  emit('select', {classCode: keys[0]})
}

function getThisNodeStr(list, str) {
  for (const nods of list) {
    str += (nods.classCode + ',')
    if (null != nods.children && nods.children.length > 0) {
      str = getThisNodeStr(nods.children, str)
    }
  }
  return str
}

const val = {
  id: null,
  classCode: '',
  className: '',
}

const [registerEditPage, {openModal: openEditPage}] = useModal()
const openAddPage = () => {
  openEditPage(true, {
    data: val,
    dynamicTenantId: dynamicCompany.value,
    isState: '0'
  })
}

const openEdit = () => {
  if (selectedKeys.value.length > 0 && selectedKeys.value[0] != '0') {
    let items: any = list.value.filter((item: any) => item.classCode == selectedKeys.value[0])
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

onMounted(async () => {
  await fetch()
})

async function saveData(data: any) {
  await useRouteApi(saveApi, {schemaName: dynamicCompany.value})(data)
  await fetch()
}

const accList = ref([])

async function del() {
  console.log(selectedKeys.value)
  if (selectedKeys.value.length > 0 && selectedKeys.value[0] != '0') {
    let items: any = list.value.filter((item: any) => item.classCode == selectedKeys.value[0])
    const res = await useRouteApi(findCdigestAllApi, {schemaName: dynamicCompany.value})({})
    accList.value = res.items
    for (let i = 0; i < accList.value.length; i++) {
      const acc: any = accList.value[i];
      if (acc.classCode == items[0].classCode) {
        createWarningModal({
          iconType: 'warning',
          title: '删除',
          content: '<span style="font-weight: bold;color: red;">[' + items[0].className + ']</span>摘要分类中经已存在摘要，不能进行删除操作！'
        })
        return false
      }
    }
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        await useRouteApi(deleteByIdApi, {schemaName: dynamicCompany.value})(items[0])
        message.success('删除成功！')
        await fetch()
      },
      onCancel: () => {
        return false
      }
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '删除',
      content: '请选择需要删除的内容！'
    })
  }
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
  height: calc(100% - 185px);
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
