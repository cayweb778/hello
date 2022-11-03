<template>
  <BasicModal
    v-bind="$attrs"
    title="新增菜单"
    @ok="handleOk(getFieldsValue())"
    @register="register"
  >
    <BasicForm :model="model" @register="registerForm" />
  </BasicModal>
</template>
<script lang="ts">
import { defineComponent, reactive, ref, toRefs } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import { BasicForm, FormSchema, useForm } from '/@/components/Form/index'
import { saveJigou } from '/@/api/record/system/jigou'

const schemas: FormSchema[] = [
  { label: 'ID', show: false, field: 'id', component: 'Input', colProps: { span: 12 }},
  { label: '机构名称', field: 'deptName', component: 'Input', colProps: { span: 12 }},
  { label: '租户id', field: 'tenantId', component: 'Input', colProps: { span: 12 }},
  { label: '上级id', field: 'parentId', component: 'Input', colProps: { span: 12 }},
  { label: '部门全称', field: 'fullName', component: 'Input', colProps: { span: 12 }},
  { label: '排序', field: 'sort', component: 'Input', colProps: { span: 12 }},
  { label: '类型', field: 'deptType', component: 'Input', colProps: { span: 12 }},
  { label: '备注', field: 'remark', component: 'Input', colProps: { span: 12 }}
]

export default defineComponent({
  components: { BasicModal, BasicForm },

  setup(props, { emit, attrs }) {
    const state = reactive({})
    const modelRef = ref({})
    const [
      registerForm,
      {
        getFieldsValue
        // setFieldsValue,
        // setProps
      }
    ] = useForm({
      labelWidth: 120,
      schemas,
      showActionButtonGroup: false,
      actionColOptions: {
        span: 24
      }
    })

    const [register, { closeModal }] = useModalInner((data) => {
      // 方式2
      modelRef.value = {
        id: data.data.id,
        deptName: data.data.deptName,
        tenantId: data.data.tenantId,
        parentId: data.data.parentId,
        fullName: data.data.fullName,
        sort: data.data.sort,
        deptType: data.data.deptType,
        remark: data.data.remark

      }
    })
    return {
      async edit(data) {
        await saveJigou(data)
        reload(data)
      },
      ...toRefs(state), register, schemas, registerForm, model: modelRef,
      getFieldsValue,
      handleOk(data) {
        emit('save', data)
        closeModal()
      },
      handleCancel() {
      }
    }
  }
})
</script>
