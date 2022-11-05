<template>
  <BasicModal
    v-bind="$attrs"
    title="新增集团档案"
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
const schemas: FormSchema[] = [
  {
    field: 'groupId',
    component: 'Input',
    label: '集团唯一码',
    colProps: {
      span: 12
    }
  }, {
    field: 'groupName',
    component: 'Input',
    label: '集团名称',
    colProps: {
      span: 12
    }
  }, {
    field: 'groupExplain',
    component: 'Input',
    label: '集团简介',
    colProps: {
      span: 12
    }

  }, {
    field: 'addr',
    component: 'Input',
    label: '集团地址',
    colProps: {
      span: 12
    }

  }, {
    field: 'tel',
    component: 'Input',
    label: '电话',
    colProps: {
      span: 12
    }

  }, {
    field: 'fax',
    component: 'Input',
    label: '传真',
    colProps: {
      span: 12
    }

  }, {
    field: 'www',
    component: 'Input',
    label: '网址',
    colProps: {
      span: 12
    }

  }, {
    field: 'email',
    component: 'Input',
    label: '邮箱',
    colProps: {
      span: 12
    }

  }, {
    field: 'wachatAccounts',
    component: 'Input',
    label: '微信公众号',
    colProps: {
      span: 12
    }

  }
]
export default defineComponent({
  components: { BasicModal, BasicForm },

  setup(props, { emit, attrs }) {
    const state = reactive({

    })
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
        groupId: data.data.groupId,
        groupName: data.data.groupName,
        groupExplain: data.data.groupExplain,
        addr: data.data.addr,
        tel: data.data.tel,
        fax: data.data.fax,
        www: data.data.www,
        email: data.data.email,
        wachatAccounts: data.data.wachatAccounts
      }
    })
    return { ...toRefs(state), register, schemas, registerForm, model: modelRef,
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
