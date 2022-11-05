<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="资产属性"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up" style="text-align: center;margin-top: 30px;">


        <label style="white-space: nowrap"> 资产属性编码：</label>
        <a-input v-model:value="formItems.peopertyId" @blur="checkRepet" placeholder=""
                 style="font-weight: 900"/>

        <br><br>
        <label style="white-space: nowrap">资产属性名称：</label>
        <a-input v-model:value="formItems.peopertyName" @blur="checkNameRepet" placeholder=""
                 style="font-weight: 900"/>

        <br><br>
        <label style="white-space: nowrap">计提方式 ：</label>
        <a-select
          v-model:value="formItems.faDepMethod"
          placeholder=""
          style="font-weight: 900;width: 50%"
        >
          <a-select-option value="0">不计提</a-select-option>
          <a-select-option value="1">新增次月开始计提</a-select-option>
          <a-select-option value="2">新增当月开始计提</a-select-option>
          <a-select-option value="3">一次性计提</a-select-option>
        </a-select>
        <br><br>
        <label style="white-space: nowrap">是否计提：</label>
        <a-select
          v-model:value="formItems.flag"
          placeholder=""
          style="font-weight: 900;width: 50%"
        >
          <a-select-option value="0">否</a-select-option>
          <a-select-option value="1">是</a-select-option>
        </a-select>

      </div>
    </div>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import {inject, onMounted, provide, ref, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {
  message,
  // need
  TreeSelect
} from 'ant-design-vue'
import {GetDeptTreeByFlag} from '/@/api/sys/dept'
import {Input as AInput, Select as ASelect, Popover as APopover,} from "ant-design-vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useMessage} from "/@/hooks/web/useMessage";
import {SaveFaProperty} from "/@/api/record/system/fa-property";
import {buildShortUUID} from "/@/utils/uuid";

const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search


const getDataSource = inject('getDataSource')
const reload = inject('reload2')


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
          item.title = '(' + item.peopertyId + ')' + item.peopertyName
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

onMounted(async () => {
  // await fetch()
})

async function handleOk() {
  const id = formItems.value.peopertyId
  const name = formItems.value.peopertyName

  if (id == '') {
    message.error('分类编码不能为空')
  } else if (name == '') {
    message.error('分类名称不能为空')
  } else {
    formItems.value.uniqueCustclass = buildShortUUID()
    useRouteApi(SaveFaProperty, {schemaName: dynamicTenantId})(formItems.value)
    closeModal()
    reload()
    return false
  }

}

function checkRepet() {
  const id = formItems.value.peopertyId
  const ids = getDataSource().map(it => it.peopertyId)
  const isRepet = ids.filter(it => it == id).length > 0
  if (isRepet) {
    message.error('属性编码不允许重复')
    formItems.value.peopertyId = ''
  }
  //

}

function checkNameRepet() {
  const id = formItems.value.peopertyName
  const ids = getDataSource().map(it => it.peopertyName)
  const isRepet = ids.filter(it => it == id).length > 0
  if (isRepet) {
    message.error('属性名称不允许重复')
    formItems.value.peopertyName = ''
  }

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
  input {
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
