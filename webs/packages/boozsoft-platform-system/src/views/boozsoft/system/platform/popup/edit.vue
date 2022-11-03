<script lang="ts">
import {defineComponent, getCurrentInstance, nextTick, onMounted, reactive, ref, toRefs, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {BasicForm, FormSchema, useForm} from '/@/components/Form/index'
import {saveMenu} from '/@/api/record/system/menu'
import {DirectorySchemas, MenuSchemas, ButtonSchemas} from "/@/views/boozsoft/prod/system/modules/permission/popup/data/columns";


export default defineComponent({
  name: 'MenuEdit',
  components: {BasicModal, BasicForm},

  setup(props, {emit, attrs}) {
    const state = reactive({
      model: {},
      activeKey: 'directory',
      formInstance: {},
      DirectorySchemas,
      MenuSchemas,
      ButtonSchemas
    })
    // const [
    //   registerForm,
    //   {
    //     getFieldsValue,
    //     validateFields,
    //     setFieldsValue,
    //     setProps,
    //     appendSchemaByField,
    //     getSchemas,
    //     updateSchema
    //     // setFieldsValue,
    //     // setProps
    //   }
    // ] = useForm({
    //   labelWidth: 120,
    //   showActionButtonGroup: false,
    //   actionColOptions: {
    //     span: 24
    //   }
    // })
    const forms = {
      formRef: ref(null)
    }

    const [register, {closeModal}] = useModalInner(async (data) => {
      await nextTick();
      if (data.category == 0) {
        state.activeKey = 'directory'
      } else if (data.category == 1) {
        state.activeKey = 'menu'
      }
      await nextTick()
      let formInstance = state.formInstance = forms['formRef']
      formInstance = unref(formInstance)
      // console.log(1)
      // debugger
      // formInstance.setFieldsValue({category: data.category})
      // console.log(2)
      // debugger
      //
      await nextTick();
        formInstance.setFieldsValue({
          parentId: data.parentId,
          category: data.category
        })
      // console.log(3)
      // debugger
    })
    return {
      ...forms,
      ...toRefs(state), register,
      // registerForm,
      // getFieldsValue,
      handleOk(data) {
        state.formInstance.validateFields().then(res => {
          emit('save', data)
          closeModal()
        })

      },
      handleCancel() {
      }
    }
  }
})

</script>

<template>
  <BasicModal
    v-bind="$attrs"
    title="编辑菜单"
    @ok="handleOk(formInstance.getFieldsValue())"
    @register="register"
  >
    <a-tabs v-model:activeKey="activeKey" hide-add type="editable-card">
      <a-tab-pane :key="'directory'" :tab="'directory'">
        <BasicForm v-if="activeKey=='directory'" ref="formRef" :schemas="DirectorySchemas"/>
      </a-tab-pane>
      <a-tab-pane :key="'menu'" :tab="'menu'">
        <BasicForm v-if="activeKey=='menu'" ref="formRef" :showActionButtonGroup="false" :schemas="MenuSchemas"/>
      </a-tab-pane>
      <a-tab-pane :key="'button'" :tab="'button'">
        <BasicForm v-if="activeKey=='button'" ref="formRef" :schemas="ButtonSchemas"/>
      </a-tab-pane>
    </a-tabs>
    <!--    <a-tabs v-model="activeKey" tab-position="top" type="card">-->
    <!--      <a-tab-pane :key="'directory'" :tab="'目录'">-->
    <!--        <BasicForm @register="registerForm"/>-->
    <!--      </a-tab-pane>-->
    <!--      <a-tab-pane :key="'menu'" :tab="'菜单'">-->
    <!--       12312-->
    <!--      </a-tab-pane>-->
    <!--      <a-tab-pane :key="'button'" :tab="'按钮'">-->
    <!--        1212312-->
    <!--      </a-tab-pane>-->

  </BasicModal>
</template>
<style lang="less" scoped>
:deep(.ant-tabs-bar.ant-tabs-top-bar.ant-tabs-card-bar) {
  display: none;
}
</style>
