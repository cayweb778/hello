<template>
  <BasicDrawer v-bind="$attrs" title="角色权限配置" width="50%" @register="register">
    <div>
      <MenuPage ref="aaa" @register="registerMenuPage"/>
    </div>
  </BasicDrawer>
</template>
<script lang="ts">
import {computed, defineComponent, getCurrentInstance, nextTick, onMounted, ref, unref, watchEffect} from 'vue'
import {BasicDrawer, DrawerInstance, DrawerProps, useDrawerInner, UseDrawerInnerReturnType} from '/@/components/Drawer'
import MenuPage from './menu-page/menu.vue'
import {error} from "/@/utils/log";
import {isProdMode} from "/@/utils/env";
import {tryOnUnmounted} from "/@/utils/helper/vueHelper";
import {isFunction} from "/@/utils/is";
const useMenuPage=function (){
  let instance
  let data
  const register=(data)=>{
    data.state.roleId=15
    instance=data.instance
  }

  const openMenu=(data)=>{
    instance.setFdata(data)
  }
  return [register,{openMenu}]
}
export default defineComponent({
  components: {BasicDrawer, MenuPage},
  setup() {
    const [register] = useDrawerInner(data => data)
    const [registerMenuPage,{openMenu}]=useMenuPage()
    // onMounted(()=>openMenu({roleId:3}))
    return {
      register,
      registerMenuPage,
      aaa: ref([])
    }
  }
})
</script>
