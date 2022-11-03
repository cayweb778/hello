<template>
  <BasicModal
    width="500px"
    height="100px"
    v-bind="$attrs"
    title="资产分类"
    @ok="handleOk()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;资产类别
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="./012.png" style="height:126px;margin-right: 10px;"/>
      </div>
    </template>
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up" style="text-align: center;margin-top: 30px;">


        <label style="white-space: nowrap"> 资产分类编码：</label>
        <a-input v-model:value="formItems.faClassId" placeholder="" style="font-weight: 900"/>

        <br><br>
        <label style="white-space: nowrap">资产分类名称：</label>
        <a-input v-model:value="formItems.faCclassName" placeholder="" style="font-weight: 900"/>

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
          v-model:value="formItems.faProperty"
          style="width: 50%;font-weight: 900"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="treeData"
          placeholder="请选择资产属性"
          tree-default-expand-all
          allow-clear
        >
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </TreeSelect>
        <br><br>
        <label style="white-space: nowrap">折旧/摊销方法：</label>
        <TreeSelect
          v-model:value="formItems.faDepMethod"
          style="width: 50%;font-weight: 900"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="treeData"
          placeholder="请选择折旧/摊销方法"
          tree-default-expand-all
          allow-clear
        >
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </TreeSelect>
        <br><br>
        <label style="white-space: nowrap"> 使用年限(月数)：</label>
        <a-input v-model:value="formItems.faYears" placeholder="" style="font-weight: 900"/>

        <br><br>
        <label style="white-space: nowrap"> 残存值率%：</label>
        <a-input v-model:value="formItems.faResRate" placeholder="" style="font-weight: 900"/>

        <br><br>
        <label style="white-space: nowrap"> 进项税率：</label>
        <a-input v-model:value="formItems.faInputRate" placeholder="" style="font-weight: 900"/>

        <br><br>
        <label style="white-space: nowrap"> 卡片样式：</label>
        <TreeSelect
          v-model:value="formItems.faCardStyle"
          style="width: 50%;font-weight: 900"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="treeData"
          placeholder="请选择卡片样式"
          tree-default-expand-all
          allow-clear
        >
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </TreeSelect>
        <!--        <a-select v-model:value="formItems.parentId" placeholder="" style="width: 50%;" >-->
        <!--          <a-select-option value="0">请选择</a-select-option>-->
        <!--        </a-select>-->


      </div>
    </div>
    <template #footer>
      <a-button @click="closeModal()">取消</a-button>
      <a-button @click="handleOk(false)" :disabled="saveClick">保存</a-button>
      <a-button @click="handleOk(true)" :disabled="saveClick" type="primary">保存并新增</a-button>
    </template>
  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import {inject, onMounted, ref, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {
  // need
  TreeSelect
} from 'ant-design-vue'
import {GetDeptTreeByFlag} from '/@/api/sys/dept'
import {Input as AInput, Select as ASelect, Popover as APopover} from "ant-design-vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getDeptList} from "/@/api/record/system/dept";
import {useMessage} from "/@/hooks/web/useMessage";
import {SaveFaClass} from "/@/api/boozsoft/group/faClass";
import {buildShortUUID} from "/@/utils/uuid";
import {
  PlusCircleOutlined,
  CaretDownOutlined
} from '@ant-design/icons-vue'
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const reload = inject('reload')
const {
  createErrorModal
} = useMessage()

const emit = defineEmits(['register', 'save'])

const formItems: any = ref({})

const treeData: any = ref([])
let changeBeforeModel: any = {}
const dynamicTenantId = ref()
const [register, {closeModal}] = useModalInner((data) => {
  dynamicTenantId.value = data.dynamicTenantId
  useRouteApi(GetDeptTreeByFlag, {schemaName: dynamicTenantId})().then(res => {
    function a(deptTree: any) {
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
  // 方式2
  formItems.value = data.data
  formItems.value.parentId = ''
  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
})

let isChanged: boolean = false

async function handleOk(fls) {
  const id = formItems.value.faClassId
  const name = formItems.value.faCclassName

  if (id == '') {
    message.error('属性编码不能为空')
  } else if (name == '') {
    message.error('属性名称不能为空')
  } else {
    formItems.value.uniqueCustclass = buildShortUUID()
    await SaveFaClass(formItems.value)
    if(fls === true){
      //emit('save', unref(formItems))
      formItems.value = {}
      changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
    }else{
      //emit('save', unref(formItems))
      reload()
      closeModal()
    }
    // alert("没有改变过")
    return false
  }
}

onMounted(async () => {
  // await fetch()
})
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
