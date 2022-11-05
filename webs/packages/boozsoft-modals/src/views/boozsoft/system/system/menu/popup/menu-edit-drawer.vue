<template>
  <BasicDrawer v-bind="$attrs" title="编辑" width="50%" @register="register">
    <div>
      <BasicForm @register="registerForm" />
    </div>
  </BasicDrawer>
</template>
<script lang="ts">
import { computed, defineComponent, getCurrentInstance, nextTick, onMounted, ref, unref, watchEffect } from 'vue'
import { BasicDrawer, DrawerInstance, DrawerProps, useDrawerInner, UseDrawerInnerReturnType } from '/@/components/Drawer'
import { error } from '/@/utils/log'
import { isProdMode } from '/@/utils/env'
import { tryOnUnmounted } from '/@/utils/helper/vueHelper'
import { isFunction } from '/@/utils/is'
import { BasicForm, useForm } from '/@/components/Form'
import {MenuSchemas} from './data/columns.ts'
const useMenuPage = function() {
  let instance
  let data
  const register = (data) => {
    data.state.roleId = 15
    instance = data.instance
  }

  const openMenu = (data) => {
    instance.setFdata(data)
  }
  return [register, { openMenu }]
}
export default defineComponent({
  components: { BasicDrawer, BasicForm },
  setup() {
    const [register] = useDrawerInner(data => data)
    const [registerMenuPage, { openMenu }] = useMenuPage()
    // onMounted(()=>openMenu({roleId:3}))
    const [registerForm, { validate, setProps }] = useForm({
      labelCol: {
        span: 7
      },
      wrapperCol: {
        span: 10
      },
      schemas:MenuSchemas,
      showActionButtonGroup: false,
      // submitButtonOptions: {
      //   text: '提交'
      // },
      submitFunc: async() => {}
    })
    return {
      register,
      registerForm,
      registerMenuPage,
      aaa: ref([])
    }
  }
})
</script>
