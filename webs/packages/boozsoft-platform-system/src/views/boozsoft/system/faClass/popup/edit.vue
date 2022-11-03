<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="资产分类"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up" style="text-align: center;margin-top: 30px;">


        <label style="white-space: nowrap"> 资产分类编码：</label>
        <a-input v-model:value="formItems.deptCode" placeholder="" style="font-weight: 900"/>

        <br><br>
        <label style="white-space: nowrap">资产分类名称：</label>
        <a-input v-model:value="formItems.deptName" placeholder=""  style="font-weight: 900"/>

        <br><br>
        <label style="white-space: nowrap">上级资产分类：</label>
        <TreeSelect
          v-model:value="formItems.parentId"
          style="width: 50% ;font-weight: 900"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="treeData"
          placeholder="请选择上级分类"
          tree-default-expand-all
          allow-clear
        >
        </TreeSelect>
        <br><br>
        <label style="white-space: nowrap">资产属性：</label>
        <TreeSelect
          v-model:value="formItems.parentId"
          style="width: 50%;font-weight: 900"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="treeData"
          placeholder="请选择资产属性"
          tree-default-expand-all
          allow-clear
        >
        </TreeSelect>
        <br><br>
        <label style="white-space: nowrap">折旧/摊销方法：</label>
        <TreeSelect
          v-model:value="formItems.parentId"
          style="width: 50%;font-weight: 900"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="treeData"
          placeholder="请选择折旧/摊销方法"
          tree-default-expand-all
          allow-clear
        >
        </TreeSelect>
        <br><br>
        <label style="white-space: nowrap"> 使用年限(月数)：</label>
        <a-input v-model:value="formItems.deptCode" placeholder="" style="font-weight: 900"/>

        <br><br>
        <label style="white-space: nowrap"> 残存值率%：</label>
        <a-input v-model:value="formItems.deptCode" placeholder="" style="font-weight: 900"/>

        <br><br>
        <label style="white-space: nowrap"> 进项税率：</label>
        <a-input v-model:value="formItems.deptCode" placeholder="" style="font-weight: 900"/>

        <br><br>
        <label style="white-space: nowrap"> 卡片样式：</label>
        <TreeSelect
          v-model:value="formItems.parentId"
          style="width: 50%;font-weight: 900"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="treeData"
          placeholder="请选择卡片样式"
          tree-default-expand-all
          allow-clear
        >
        </TreeSelect>
        <!--        <a-select v-model:value="formItems.parentId" placeholder="" style="width: 50%;" >-->
        <!--          <a-select-option value="0">请选择</a-select-option>-->
        <!--        </a-select>-->


      </div>
    </div>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import { onMounted, ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {
  // need
  TreeSelect
} from 'ant-design-vue'
import { GetDeptTreeByFlag } from '/@/api/sys/dept'
 import { psnFindByFlag } from '/@/api/psn/psn'
import {Input as AInput,Select as ASelect,Popover as APopover} from "ant-design-vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getDeptList} from "/@/api/record/system/dept";
import {useMessage} from "/@/hooks/web/useMessage";
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const emit=defineEmits(['register','save'])

/* const formItems = ref({
  deptCode: '',
  deptName: '',
  parentId: '',
  uniqueCodeUser: ''
})*/

const {
  createErrorModal
} = useMessage()

const formItems:any = ref({})

const treeData:any = ref([])

const psnList:any = ref({})

let changeBeforeModel:any = {}
const dynamicTenantId = ref()
const [register, { closeModal }] = useModalInner((data) => {
  dynamicTenantId.value = data.dynamicTenantId

  useRouteApi(GetDeptTreeByFlag,{schemaName: dynamicTenantId})().then(res=>{
    function a(deptTree:any) {
      deptTree.forEach(
          (item: any) => {
          if (item.children != null) {
            a(item.children)
          }
          item.title = '(' + item.deptCode + ')' + item.deptName
          item.value = item.id
          item.key = item.id
        })
    }
    a(res)
    treeData.value = res
  })
  useRouteApi(psnFindByFlag,{schemaName: dynamicTenantId})().then((res:any)=>{
    psnList.value = res/*.items*/
    console.log(psnList.value)
  })
  // 方式2
  formItems.value = {
    id: data.data.id,
    uniqueCode: data.data.uniqueCode,
    deptCode: data.data.deptCode,
    deptName: data.data.deptName,
    parentId: data.data.parentId == 0 ? '' : data.data.parentId,
    uniqueCodeUser: data.data.uniqueCodeUser
  }

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})
let isChanged:boolean = false
async function handleOk() {
  const res = await useRouteApi(getDeptList,{schemaName: dynamicTenantId})()
  const deptList = res.items
  if(formItems.value.deptCode==''|| formItems.value.deptCode==null){
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '部门编码不能为空！'
    })
    return false
  }
  if (formItems.value.deptName == '' || formItems.value.deptName == null) {
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '部门名称不能为空！'
    })
    return false
  }
  if (formItems.value.deptCode != changeBeforeModel.deptCode && formItems.value.deptCode != '' && formItems.value.deptCode != null) {
    for (let i = 0; i < deptList.length; i++) {
      const dept = deptList[i];
      if (dept.deptCode == formItems.value.deptCode) {
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '部门编码重复不允许添加！'
        })
        return false
      }
    }
  }
  if (formItems.value.deptName != changeBeforeModel.deptName && formItems.value.deptName != '' && formItems.value.deptName != null) {
    for (let i = 0; i < deptList.length; i++) {
      const dept = deptList[i];
      if (dept.deptName == formItems.value.deptName) {
        createErrorModal({
          iconType: 'warning',
          title: '提示',
          content: '部门名称重复不允许添加！'
        })
        return false
      }
    }
  }
  isChanged = !(formItems.value.uniqueCode == changeBeforeModel.uniqueCode
    && formItems.value.deptCode == changeBeforeModel.deptCode
    && formItems.value.deptName == changeBeforeModel.deptName
    && formItems.value.parentId == changeBeforeModel.parentId
    && formItems.value.uniqueCodeUser == changeBeforeModel.uniqueCodeUser)
  if(isChanged){
    emit('save', unref(formItems))
    closeModal()
    return true
  }
  closeModal()
  // alert("没有改变过")
  return false
}


onMounted(async () => {
})

const handleChange = () => {
  console.log('selected ${key}')
}
const filterOption = (input: string, option: any) => {
  return option.props.value.toLowerCase().indexOf(input.toLowerCase()) >= 0
}

function handleFocus() {
}

function handleBlur() {
}
</script>
<style>
.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}
</style>
<style lang="less" scoped>
:deep(.vben-basic-title) {
  color: rgb(1, 129, 226) !important;
}

:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none !important;
}

.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}

.nc-open-content {
  input:not(.ant-select-selection-search-input,.ant-input) {
    width: 50%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  .ant-input:focus {
    box-shadow: none;
  }

 :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label {
    text-align: left;
    width: 110px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    color: #535353;
  }

  .open-content-down {
    margin-top: 5%;

    .ant-tabs-tab-active::before {
      position: absolute;
      top: 0px;
      left: 0;
      width: 100%;
      border-top: 2px solid transparent;
      border-radius: 2px 2px 0 0;
      transition: all 0.3s;
      content: '';
      pointer-events: none;
      background-color: rgb(1, 143, 251);
    }
  }
}
</style>
