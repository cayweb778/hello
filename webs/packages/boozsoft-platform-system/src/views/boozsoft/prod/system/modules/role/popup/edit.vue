<template>
  <BasicModal
    v-bind="$attrs"
    title="编辑角色"
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
import { saveRole } from '/@/api/record/system/role'

const schemas: FormSchema[] = [
  //
  // {
  //   label: 'id', field: 'id', component: 'Input', colProps: {
  //     span: 12
  //   }
  // },
  {
    label: '租户ID', field: 'tenantId', component: 'Input', colProps: {
      span: 12
    }
  },
  // { label:'租金编号', field: 'rentId',component: 'Input',colProps: {
  //     span: 12
  // } },
  {
    label: '角色名称', field: 'roleName', component: 'Input', colProps: {
      span: 12
    }
  },
  {
    label: '分类', field: 'sort', component: 'Input', colProps: {
      span: 12
    }
  },
  {
    label: '角色别名', field: 'roleAlias', component: 'Input', colProps: {
      span: 12
    }
  },
  {
    label: '是否删除', field: 'isDeleted', component: 'Input', colProps: {
      span: 12
    }
  }
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
        rentId: data.data.rentId,
        tenantId: data.data.tenantId,
        roleName: data.data.roleName,
        sort: data.data.sort,
        roleAlias: data.data.roleAlias,
        isDeleted: data.data.isDeleted
      }
    })
    return {
      async edit(data) {
        await  saveRole(data)
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
