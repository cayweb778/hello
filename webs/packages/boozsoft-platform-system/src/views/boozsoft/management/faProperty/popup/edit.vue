<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="资产属性"
    @ok="handleOk()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;资产属性
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="../../../../../assets/images/012.png" style="height:80px;margin-right: 10px;"/>
      </div>
    </template>
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up" style="text-align: center;margin-top: 30px;">
        <div style="text-align: left;padding-left: 10%">
          <a-radio-group v-model:value="formItems.isAccrual" name="radioGroup" :disabled="isEdit">
            <a-radio  value="1">计提折旧</a-radio>
            <a-radio value="2">不计提</a-radio>
          </a-radio-group>
          <br/>
        </div>

        <label style="font-size: 18px;margin-left: 0;">属性名称：</label>
        <a-input v-model:value="formItems.peopertyName" @blur="checkNameRepet" placeholder=""
                 style="font-weight: 900"/>
        <span class="red_span">*</span>
        <br/><br/><br/>


        <label style="white-space: nowrap"> 资产属性编码：</label>
        <a-input v-model:value="formItems.peopertyId" @blur="checkRepet" placeholder=""
                 style="font-weight: 900"/>

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
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </a-select>

      </div>
    </div>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import {inject, onMounted, ref, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {SaveFaProperty} from "/@/api/boozsoft/group/faProperty";
import {
  message,
  // need
  TreeSelect
} from 'ant-design-vue'
import {
  FormOutlined,
  CaretDownOutlined
} from '@ant-design/icons-vue'
import {GetDeptTreeByFlag} from '/@/api/sys/dept'
import {psnFindByFlag} from '/@/api/psn/psn'
import {Input as AInput, Select as ASelect, Popover as APopover} from "ant-design-vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getDeptList} from "/@/api/record/system/dept";
import {useMessage} from "/@/hooks/web/useMessage";
import {buildShortUUID} from "/@/utils/uuid";

const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const emit = defineEmits(['register', 'save'])

/* const formItems = ref({
  peopertyId: '',
  peopertyName: '',
  parentId: '',
  uniqueCodeUser: ''
})*/

const {
  createErrorModal
} = useMessage()

const formItems: any = ref({})

const treeData: any = ref([])

const psnList: any = ref({})

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
  useRouteApi(psnFindByFlag, {schemaName: dynamicTenantId})().then((res: any) => {
    psnList.value = res/*.items*/
    console.log(psnList.value)
  })
  // 方式2
  formItems.value = {
    id: data.data.id,
    flag: data.data.flag,
    peopertyId: data.data.peopertyId,
    peopertyName: data.data.peopertyName,
    faDepMethod: data.data.faDepMethod,
  }

  changeBeforeModel = JSON.parse(JSON.stringify(formItems.value))
});
const reload = inject('reload2')
let isChanged: boolean = false

async function handleOk() {
  const id = formItems.value.peopertyId
  const name = formItems.value.peopertyName

  if (id == '') {
    message.error('属性编码不能为空')
  } else if (name == '') {
    message.error('属性名称不能为空')
  } else {
    formItems.value.uniqueCustclass = buildShortUUID()
    await SaveFaProperty(formItems.value)

    closeModal()
    reload()
    return false
  }

  // const res = await useRouteApi(getDeptList,{schemaName: dynamicTenantId})()
  // const deptList = res.items
  // if(formItems.value.peopertyId==''|| formItems.value.peopertyId==null){
  //   createErrorModal({
  //     iconType: 'warning',
  //     title: '提示',
  //     content: '属性编码不能为空！'
  //   })
  //   return false
  // }
  // if (formItems.value.peopertyName == '' || formItems.value.peopertyName == null) {
  //   createErrorModal({
  //     iconType: 'warning',
  //     title: '提示',
  //     content: '属性名称不能为空！'
  //   })
  //   return false
  // }
  // if (formItems.value.peopertyId != changeBeforeModel.peopertyId && formItems.value.peopertyId != '' && formItems.value.peopertyId != null) {
  //   for (let i = 0; i < deptList.length; i++) {
  //     const dept = deptList[i];
  //     if (dept.peopertyId == formItems.value.peopertyId) {
  //       createErrorModal({
  //         iconType: 'warning',
  //         title: '提示',
  //         content: '属性编码重复不允许添加！'
  //       })
  //       return false
  //     }
  //   }
  // }
  // if (formItems.value.peopertyName != changeBeforeModel.peopertyName && formItems.value.peopertyName != '' && formItems.value.peopertyName != null) {
  //   for (let i = 0; i < deptList.length; i++) {
  //     const dept = deptList[i];
  //     if (dept.peopertyName == formItems.value.peopertyName) {
  //       createErrorModal({
  //         iconType: 'warning',
  //         title: '提示',
  //         content: '属性名称重复不允许添加！'
  //       })
  //       return false
  //     }
  //   }
  // }
  // isChanged = !(formItems.value.uniqueCode == changeBeforeModel.uniqueCode
  //   && formItems.value.peopertyId == changeBeforeModel.peopertyId
  //   && formItems.value.peopertyName == changeBeforeModel.peopertyName
  //   && formItems.value.parentId == changeBeforeModel.parentId
  //   && formItems.value.uniqueCodeUser == changeBeforeModel.uniqueCodeUser)
  // if(isChanged){
  //   emit('save', unref(formItems))
  //   closeModal()
  //   return true
  // }
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
  color: #0096c7 !important;
  border:none !important;
}

.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
  border-top: none !important;
}
.ant-modal-header{
  border: none !important;
}
</style>
<style lang="less" scoped>
:deep(.vben-basic-title) {
  color: #0096c7 !important;
}
.ant-modal-header{
  border: none !important;
}


:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none !important;
}

.vben-basic-title {
  color: #0096c7 !important;
  border: none !important;
}

.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
  border-top: none !important;
}

.red_span {
  color: red;
  font-weight: bold;
  display: inline-block;
  width: 20px;
  text-align: right;
}

.nc-open-content {
  input {
    width: 50%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  .abc{
    border-bottom: 2px solid #666666 !important;
    font-size: 18px;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  label {
    text-align: left;
    width: 120px;
    display: inline-block;
    padding-top: 5px;
    padding-left: 5px;
    padding-bottom: 5px;
    color: #535353;
    font-size: 13px;
    margin-left: 1em;
    font-weight: bold;
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
