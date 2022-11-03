<template>
  <div class="bg-white mr-0 overflow-hidden">
<!--    <div style="width: 100%;height: 50px;">
      <label style="font-size: 20px;line-height: 50px;margin-left: 20px;">自定义项设置</label>
      <div style="float: right;margin-right: 20px;margin-top: 10px;">
        <a-popover placement="bottom">
          <a-button style="padding: 0px 4px; height: 20px;">
            <EllipsisOutlined/>
          </a-button>
          <template #content>
            <span class="p_specifics group-btn-span-special2" v-if="definedCode" @click="openAddPage()"><PlusOutlined/> 添加</span><br v-if="definedCode" />
            <span class="p_specifics group-btn-span-special2" v-if="definedCode" @click="openEdit()"><FormOutlined/> 修改</span><br v-if="definedCode" />
            <span class="p_specifics group-btn-span-special2" @click="del()"><DeleteOutlined/> 删除</span><br/>
            <span class="p_specifics group-btn-span-special2" v-if="!definedCode" @click="openInto()"><ShareAltOutlined/> 引用</span><br v-if="!definedCode" />
            <span class="p_specifics group-btn-span-special2" @click="editFlagData()"><CloseCircleOutlined/> 停用</span><br/>
            <span class="p_specifics group-btn-span-special2" @click="openQiyong()"><CheckCircleOutlined/> 启用</span>
          </template>
        </a-popover>
      </div>
    </div>-->
    <div style="width: 100%; height: 32px;background-color: #d9d9d9;padding-top:5px;font-weight: bold;text-align: center;">
      自定义项设置
      <div style="float: right;margin-right: 20px;">
<!--        <a style="color: black;font-size: 14px;" @click="openAddPage()"><PlusCircleTwoTone /></a>&emsp;-->
        <a-popover placement="bottom">
<!--          <a-button style="padding: 0px 4px; height: 20px;">
            <EllipsisOutlined/>
          </a-button>-->
          <a style="font-size: 14px;"><FormOutlined/></a>
          <template #content>
            <div style="width: 80px;" class="p_specifics group-btn-span-special2" v-if="definedCode" @click="openAddPage()"><PlusOutlined/> 添加</div>
            <div style="width: 80px;" class="p_specifics group-btn-span-special2" v-if="definedCode" @click="openEdit()"><FormOutlined/> 修改</div>
            <div style="width: 80px;" class="p_specifics group-btn-span-special2" @click="del()"><DeleteOutlined/> 删除</div>
            <div style="width: 80px;" class="p_specifics group-btn-span-special2" v-if="!definedCode" @click="openInto()"><ShareAltOutlined/> 引用</div>
            <div style="width: 80px;" class="p_specifics group-btn-span-special2" @click="editFlagData()"><CloseCircleOutlined/> 停用</div>
            <div style="width: 80px;" class="p_specifics group-btn-span-special2" @click="openQiyong()"><CheckCircleOutlined/> 启用</div>
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
    <Into @save="saveInto" @register="registerIntoPage"/>
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
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {Popover as APopover, message} from "ant-design-vue";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  deleteDefinedFile, editByFlag,
  findAllDefinedFileList, findByModelList,
  saveDefinedFile
} from "/@/api/record/system/defined-file";
import {
  deleteDefinedRecord,
  findAllDefinedRecordList,
  saveDefinedRecord
} from "/@/api/record/system/defined-record";

import {findByTenantIdAndUniqueCode, save} from "/@/api/record/system/group-defined-file-account";

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
const list = ref([])

async function fetch() {
  let classTree = await useRouteApi(findAllDefinedFileList, {schemaName: dynamicCompany.value})({})
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
  let classTree0 = await useRouteApi(findByModelList, {schemaName: dynamicCompany.value})({scope:'1',model:''})
  let classTree1 = await useRouteApi(findByModelList, {schemaName: dynamicCompany.value})({scope:'0',model:'ZW'})
  let classTree2 = await useRouteApi(findByModelList, {schemaName: dynamicCompany.value})({scope:'0',model:'FA'})
  let classTree3 = await useRouteApi(findByModelList, {schemaName: dynamicCompany.value})({scope:'0',model:'STOCK'})
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
    dynamicTenantId: dynamicCompany.value,
    isState: '0'
  })
}

const openEdit = () => {
  if (selectedKeys.value.length > 0 && selectedKeys.value[0] != '0' && selectedKeys.value[0] != 'ZW' && selectedKeys.value[0] != 'FA' && selectedKeys.value[0] != 'STOCK') {
    let items: any = list.value.filter((item: any) => item.definedCode == selectedKeys.value[0])
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
  await useRouteApi(saveDefinedFile, {schemaName: dynamicCompany.value})(data)
  if (data.shuxing=='5'){
    const item1 = {
      definedCode: data.definedCode,
      recordCode: '0',
      recordName: '否',
      flag: '1',
      remarks: ''
    }
    await useRouteApi(saveDefinedRecord, {schemaName: dynamicCompany.value})(item1)
    const item2 = {
      definedCode: data.definedCode,
      recordCode: '1',
      recordName: '是',
      flag: '1',
      remarks: ''
    }
    await useRouteApi(saveDefinedRecord, {schemaName: dynamicCompany.value})(item2)
  }
  await fetch()
}

const fileList = ref([])

async function del() {
  if (selectedKeys.value.length > 0 && selectedKeys.value[0] != '0' && selectedKeys.value[0] != 'ZW' && selectedKeys.value[0] != 'FA' && selectedKeys.value[0] != 'STOCK') {
    let items: any = list.value.filter((item: any) => item.definedCode == selectedKeys.value[0])
    if (items[0].shuxing!='5') {
      const res = await useRouteApi(findAllDefinedRecordList, {schemaName: dynamicCompany.value})(items[0].definedCode)
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
          const item1 = await findByTenantIdAndUniqueCode(dynamicCompany.value,items[0].definedCode)
          if (item1.length>0) {
            item1[0].flag = '0'
            item1[0].acceptUser = ''
            item1[0].acceptDate = ''
            await save(item1[0])
          }
          await useRouteApi(deleteDefinedFile, {schemaName: dynamicCompany.value})(items[0])
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
          const res = await useRouteApi(findAllDefinedRecordList, {schemaName: dynamicCompany.value})(items[0].definedCode)
          for (let i = 0; i < res.length; i++) {
            const aa = res[i]
            await useRouteApi(deleteDefinedRecord, {schemaName: dynamicCompany.value})(aa)
          }
          await useRouteApi(deleteDefinedFile, {schemaName: dynamicCompany.value})(items[0])
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
    await useRouteApi(editByFlag, {schemaName: dynamicCompany.value})(items[0]);
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
    dynamicTenantId: dynamicCompany.value
  })
}
//启用方法
async function qiyongData(data){
  // console.log(data)
  for (let i = 0; i < data.length; i++){
    const item = data[i]
    await useRouteApi(editByFlag, {schemaName: dynamicCompany.value})(item);
  }
  await fetch()
}

const totalChange = computed(() => props.modelValue.total)
let companyName = computed(() => props.modelValue.thisAdInfo.baseName)
const definedCode = ref(false)
watch(companyName, async () => {
    let a = props.modelValue.thisAdInfo
    companyName = props.modelValue.thisAdInfo.baseName
    definedCode.value = props.modelValue.definedCode
    if (Object.keys(a).length > 0) {
      dynamicCompany.value = a.accountMode
      // dynamicCompany.value = a.accId + '-' + defalutCompany.split('-')[2]
      await fetch()
    }
  }
)

//引入
import Into from './popup/defined-file-into.vue'
const [registerIntoPage, {openModal: openIntoPage}] = useModal()
const openInto = () => {
  openIntoPage(true, {
    dynamicTenantId: dynamicCompany.value,
  })
}

async function saveInto(data) {
  await fetch()
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
