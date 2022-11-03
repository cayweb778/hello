<template>
  <BasicModal
    v-bind="$attrs"
    title="编辑用户"
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
  { label: 'ID', show: false, field: 'id', colProps: { span: 12 }, component: 'Input' },
  { label: '部门', field: 'deptName', colProps: { span: 12 }, component: 'Select', componentProps: { options: deptOptions }, required: true },
  { label: '角色', field: 'roleName', colProps: { span: 12 }, component: 'Select', componentProps: { options: roleOptions }, required: true },
  { label: '真实名字', field: 'realName', colProps: { span: 12 }, component: 'Input' },
  { label: '名字', field: 'name', colProps: { span: 12 }, component: 'Input' },
  { label: '手机', field: 'phone', colProps: { span: 12 }, component: 'Input' },
  // {label: '生日', field: 'birthday', colProps: {span: 12}, component: 'Input'},
  // {label: '性别', field: 'sex', colProps: {span: 12}, component: 'Input'},
  // {label: '代码', field: 'code', colProps: {span: 12}, component: 'Input'},
  { label: '用户名', field: 'username', colProps: { span: 12 }, component: 'Input' },
  { label: '密码', field: 'password', colProps: { span: 12 }, component: 'Input' }
  // {label: '头像', field: 'avatar', colProps: {span: 12}, component: 'Input'},
  // {label: '邮箱', field: 'email', colProps: {span: 12}, component: 'Input'}
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
