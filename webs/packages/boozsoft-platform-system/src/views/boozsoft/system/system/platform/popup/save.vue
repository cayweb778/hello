<template>
  <BasicModal
    width="400px"
    v-bind="$attrs"
    title="部门档案"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up">

        <label>部门编码</label>
        <a-input v-model:value="formItems.deptCode" placeholder=""/>

        <br><br>
        <label>部门名称</label>
        <a-input v-model:value="formItems.deptName" placeholder=""/>

        <br><br>
        <label>上级部门</label>
        <TreeSelect
          v-model:value="formItems.parentId"
          style="width: 50%"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="treeData"
          placeholder="请选择上级部门"
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
import {Input as AInput,Select as ASelect,Popover as APopover} from "ant-design-vue";
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search

const emit=defineEmits([])

const formItems:any = ref({})

const treeData:any = ref([])

const [register, { closeModal }] = useModalInner((data) => {
  GetDeptTreeByFlag().then(res=>{
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
  // 方式2
  formItems.value = data.data
  formItems.value.parentId = ''
})

async function handleOk() {
  emit('save', unref(formItems))
  closeModal()
}

onMounted(async () => {
  // await fetch()
})
</script>
<style lang="less" scoped>
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
    text-align: right;
    width: 110px;
    display: inline-block;
    padding: 5px 10px;
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
