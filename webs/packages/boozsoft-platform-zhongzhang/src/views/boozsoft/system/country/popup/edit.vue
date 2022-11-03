<template>
  <BasicModal
    v-bind="$attrs"
    title="编辑档案"
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
import { saveUser } from '/@/api/record/system/user'
import { GetJigouTree } from '/@/api/sys/jigou'
import { getRoleListById } from '/@/api/record/system/role'
// GetJigouTree

const deptOptions: LabelValueOptions = [
  {
    label: '财务部门务',
    value: '1'
  },
  {
    label: '技术部门',
    value: '2'
  }
]
const aaa = GetJigouTree

// getRoleListById
const roleOptions: LabelValueOptions = [
  {
    label: '超级管理员',
    value: '1'
  },
  {
    label: '财务',
    value: '2'
  }
]
getRoleListById().then(res => {
  console.log(res)
})
const schemas: FormSchema[] = [
  { label: 'ID', show: false, field: 'id', component: 'Input', colProps: { span: 12 }},
  { label: '国家编码(例:USA)', field: 'countryId', colProps: { span: 12 }, component: 'Input' },
  { label: '英文名称', field: 'nameen', colProps: { span: 12 }, component: 'Input' },
  { label: '中文名称', field: 'namech', colProps: { span: 12 }, component: 'Input' },
  { label: '排序', field: 'num', colProps: { span: 12 }, component: 'Input'}
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
        roleName: data.data.roleName,
        realName: data.data.realName,
        name: data.data.name,
        phone: data.data.phone,
        birthday: data.data.birthday,
        sex: data.data.sex,
        code: data.data.code,
        username: data.data.username,
        password: data.data.password,
        avatar: data.data.avatar,
        email: data.data.email
      }
    })
    return {
      async edit(data) {
        console.log(data)
        await saveUser(data)
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
