<template>
  <div class="bg-white mr-0 overflow-hidden">
    <div style="width: 100%;height: 50px;">
      <label style="font-size: 20px;line-height: 50px;margin-left: 20px;">自定义项设置</label>
      <div style="float: right;margin-right: 20px;margin-top: 10px;">
        <a-popover placement="bottom">
          <a-button style="padding: 0px 4px; height: 20px;">
            <EllipsisOutlined/>
          </a-button>
          <template #content>
            <span class="p_specifics group-btn-span-special2" @click="openAddPage()"><PlusOutlined/> 添加</span><br/>
            <span class="p_specifics group-btn-span-special2" @click="openEdit()"><FormOutlined/> 修改</span><br/>
            <span class="p_specifics group-btn-span-special2" @click="del()"><DeleteOutlined/> 删除</span><br/>
            <span class="p_specifics group-btn-span-special2" @click="openAssign()"><ShareAltOutlined/> 分配</span><br/>
            <span class="p_specifics group-btn-span-special2" @click="editFlagData()"><CloseCircleOutlined/> 停用</span><br/>
            <span class="p_specifics group-btn-span-special2" @click="openQiyong()"><CheckCircleOutlined/> 启用</span>
          </template>
        </a-popover>
      </div>
    </div>
    <BasicTree
      title=""
      :click-row-to-expand="false"
      :tree-data="treeData"
      :replace-fields="{ key: 'definedCode', title: 'value' }"
      v-model:selectedKeys="selectedKeys"
      v-model:expandedKeys="expandedKeys"
      @select="handleSelect"
    />
    <ClassEdit
      @save="saveData"
      @register="registerEditPage"
    />
    <Qiyong
      @save="qiyongData"
      @register="registerQiyongPage"
    />
    <Assign @save="saveAssign" @register="registerAssignPage"/>
  </div>

</template>
<script setup lang="ts">
import {computed, onMounted, ref, watch} from 'vue'

import {BasicTree} from '/@/components/Tree'
import ClassEdit from './popup/class-edit.vue'
import Qiyong from './popup/qiyong.vue'
import {useModal} from '/@/components/Modal'

import {
  PlusOutlined,
  DeleteOutlined,
  EllipsisOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  FormOutlined,
  ShareAltOutlined
} from '@ant-design/icons-vue'
import {Popover as APopover, message} from "ant-design-vue";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  deleteDefinedFile, editByFlag,
  findAllDefinedFileList, findByModelList,
  saveDefinedFile
} from "/@/api/record/system/group-defined-file";
import {
  deleteDefinedRecord,
  findAllDefinedRecordList,
  saveDefinedRecord
} from "/@/api/record/system/group-defined-record";
import {findByUniqueCode} from "/@/api/record/system/group-defined-file-account";

const {
  createConfirm,
  createWarningModal
} = useMessage()

const emit = defineEmits(['select'])
const props = defineProps(['modelValue'])
const treeData: any = ref([])
const selectedKeys = ref<string[]>([])
const expandedKeys = ref<string[]>([])

const list = ref([])

async function fetch() {
  let classTree = await findAllDefinedFileList()
  list.value = classTree

  function a(classTree: any) {
    classTree.forEach((item: any) => {
      if (item.children != null) {
        a(item.children)
      }
      let str = ''
      if (item.shuxing=='1'){
        str='文本'
      } else if (item.shuxing=='2'){
        str='日期'
      } else if (item.shuxing=='3'){
        str='整数'
      } else if (item.shuxing=='4'){
        str='小数'
      } else if (item.shuxing=='5'){
        str='是否'
      }
      item.value = item.definedCode + '  -  ['+ str + ']'+ item.definedName
      item.disabled = item.flag=='1'?false:true
    })
  }

  function b(deptTree: any) {
    let arr: any = []
    arr.push(deptTree[0].definedCode)
    return arr
  }

  a(classTree)
  let classTree0 = await findByModelList({scope:'1',model:''})
  let classTree1 = await findByModelList({scope:'0',model:'ZW'})
  let classTree2 = await findByModelList({scope:'0',model:'FA'})
  let classTree3 = await findByModelList({scope:'0',model:'STOCK'})
  a(classTree0)
  a(classTree1)
  a(classTree2)
  a(classTree3)
  // treeData.value = deptTree
  treeData.value = []
  // treeData.value.push({id: '0', key: '', definedCode: '0', value: companyName, children: classTree})
  // treeData.value.push({id: '0', key: '', definedCode: '0', value: '全部', children: classTree})
  treeData.value.push({id: '0', key: '', definedCode: '0', value: '通用', children: classTree0})
  treeData.value.push({id: '2', key: '', definedCode: 'ZW', value: '科目辅助核算项', children: classTree1})
  treeData.value.push({id: '3', key: '', definedCode: 'FA', value: '固定资产卡片自定义项', children: classTree2})
  treeData.value.push({id: '4', key: '', definedCode: 'STOCK', value: '存货档案自定义项', children: classTree3})
  // treeData.value = classTree
  // let checks = b(deptTree)
  let checks
  if (classTree.length>0) {
    checks = b(classTree)
  } else {
    checks = b(treeData.value)
  }
  selectedKeys.value = checks
  expandedKeys.value = b(treeData.value)
  let items: any = list.value.filter((item: any) => item.definedCode == selectedKeys.value[0])
  emit('select', {definedCode: checks[0], item: items[0]})
}

function handleSelect(keys: string, e: any) {
  let items: any = list.value.filter((item: any) => item.definedCode == selectedKeys.value[0])
  emit('select', {definedCode: keys[0],item: items[0]})
}

function getThisNodeStr(list, str) {
  for (const nods of list) {
    str += (nods.definedCode + ',')
    if (null != nods.children && nods.children.length > 0) {
      str = getThisNodeStr(nods.children, str)
    }
  }
  return str
}

const val = {
  id: null,
  definedCode: '',
  definedName: '',
  flag: '1',
  remarks: '',
  shuxing: '',
  ctype: '',
  numWeishu: '',
  scope: '1',
  model: '',
}

const [registerEditPage, {openModal: openEditPage}] = useModal()
const openAddPage = () => {
  openEditPage(true, {
    data: val,
    isState: '0'
  })
}

const openEdit = () => {
  if (selectedKeys.value.length > 0 && selectedKeys.value[0] != '0' && selectedKeys.value[0] != 'ZW' && selectedKeys.value[0] != 'FA' && selectedKeys.value[0] != 'STOCK') {
    let items: any = list.value.filter((item: any) => item.definedCode == selectedKeys.value[0])
    openEditPage(true, {
      data: items[0],
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
  await saveDefinedFile(data)
  if (data.shuxing=='5'){
    const item1 = {
      definedCode: data.definedCode,
      recordCode: '0',
      recordName: '否',
      flag: '1',
      remarks: ''
    }
    await saveDefinedRecord(item1)
    const item2 = {
      definedCode: data.definedCode,
      recordCode: '1',
      recordName: '是',
      flag: '1',
      remarks: ''
    }
    await saveDefinedRecord(item2)
  }
  await fetch()
}

const fileList = ref([])

async function del() {
  if (selectedKeys.value.length > 0 && selectedKeys.value[0] != '0' && selectedKeys.value[0] != 'ZW' && selectedKeys.value[0] != 'FA' && selectedKeys.value[0] != 'STOCK') {
    let items: any = list.value.filter((item: any) => item.definedCode == selectedKeys.value[0])
    const res = await findByUniqueCode(items[0].definedCode)
    if (res.length > 0){
      createWarningModal({
        iconType: 'warning',
        title: '删除',
        content: '自定义项已分配不允许删除！'
      })
      return false
    }
    if (items[0].shuxing!='5') {
      const res = await findAllDefinedRecordList(items[0].definedCode)
      fileList.value = res
      for (let i = 0; i < fileList.value.length; i++) {
        const file: any = fileList.value[i];
        console.log(file)
        if (file.definedCode == items[0].definedCode) {
          createWarningModal({
            iconType: 'warning',
            title: '删除',
            content: '<span style="font-weight: bold;color: red;">[' + items[0].definedName + ']</span>自定义设置中经已存在档案，不能进行删除操作！'
          })
          return false
        }
      }
      createConfirm({
        iconType: 'error',
        title: '警告',
        content: '删除后数据将不能恢复，你确认要删除吗?',
        onOk: async () => {
          await deleteDefinedFile(items[0])
          message.success('删除成功！')
          await fetch()
        },
        onCancel: () => {
          return false
        }
      })
    } else {
      createConfirm({
        iconType: 'error',
        title: '警告',
        content: '删除后数据将不能恢复，你确认要删除吗?',
        onOk: async () => {
          const res = await findAllDefinedRecordList(items[0].definedCode)
          for (let i = 0; i < res.length; i++) {
            const aa = res[i]
            await deleteDefinedRecord(aa)
          }
          await deleteDefinedFile(items[0])
          message.success('删除成功！')
          await fetch()
        },
        onCancel: () => {
          return false
        }
      })
    }
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '删除',
      content: '请选择需要删除的内容！'
    })
  }
}
//停用方法
async function editFlagData() {
  // console.log(selectedKeys.value)
  if (selectedKeys.value.length > 0 && selectedKeys.value[0] != '0') {
    let items: any = list.value.filter((item: any) => item.definedCode == selectedKeys.value[0])
    await editByFlag(items[0]);
    await fetch()
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '停用',
      content: '请选择需要停用的内容！'
    })
  }
}

const [registerQiyongPage, {openModal: openQiyongPage}] = useModal()
const openQiyong = () => {
  openQiyongPage(true, {
    data: val,
  })
}
//启用方法
async function qiyongData(data){
  // console.log(data)
  for (let i = 0; i < data.length; i++){
    const item = data[i]
    await editByFlag(item);
  }
  await fetch()
}

//分配
import Assign from './popup/defined-file-assign.vue'
const [registerAssignPage, {openModal: openAssignPage}] = useModal()
const openAssign = () => {
  openAssignPage(true, {})
}

async function saveAssign(data) {
}

</script>
<style scoped>
.bg-white {
  width: 300px !important;
  min-height: 300px !important;
  height: calc(100% - 170px);
  border: 1px #cccccc solid;
  background: white !important;
  margin-right: .2%;
}
</style>
