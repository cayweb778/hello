import {defineStore} from 'pinia';
import {store} from '/@/store';
import {nextTick, ref, shallowRef} from "vue";
import {awaitData, awaitData2, waitOk} from "/@/views/boozsoft/global/funs/bb";


function createdComponentData(componentInstance) {

  let isBak = ref(false)
  let data = ref(null)

  let json = {
    ref: ref(),
    component: shallowRef(componentInstance),
    componentParams: 1,
    componentResult: function (e) {
      isBak.value = true
      data.value = e
    },
    onCancel(e) {
      data.value = {
        code:1051,
        message:'取消了选择'
      }
      window.$wujie.bus.$emit('setCurrentPluginName', '')
      isBak.value = true
    },
    onOk(e) {
      data.value = e
      window.$wujie.bus.$emit('setCurrentPluginName', '')
      isBak.value = true
    },
    fnOpen() {
      isBak.value = false
      data.value = null
      json.ref.value.open()
    },
    waitOk: async () => await waitOk({isBak, data})
  }
  return json
}

export const usePluginStore = defineStore({
  id: 'pluginStore',
  state: (): any => ({
    components: [],
    // Reference components
    componentsReferenceMap: {},
    componentReference: {
      SYSTEM_MESSAGE: ['SYSTEM_MESSAGE', () => import('/@/views/boozsoft/global/funs/modules/index.vue')],
      DEPT: ['dept', ()=>import('/@/views/boozsoft/global/dept/index.vue')],
      PSN: ['dept', ()=>import('/@/views/boozsoft/global/psn/index.vue')],
      CUSTOM: ['custom',()=>import( '/@/views/boozsoft/global/custom/index.vue')],
      GYS: ['gys', ()=>import('/@/views/boozsoft/global/gys/index.vue')],
      // d:['dept', '../dept/index.vue'],
    }
  }),
  getters: {
    getComponentDatas: (state) => state.components
  },
  actions: {
    async getOrCreateComponent(name2) {
      const [name, path] = this.componentReference[name2]
      const componentInstance = (await (() => import(path))()).default
      if (this.componentsReferenceMap.dept == null) {
        // Create Component Data
        const componentData = createdComponentData(componentInstance)
        // add component
        this.components.push(componentData)
        // add component reference
        this.componentsReferenceMap[name] = componentData

      }
      await nextTick(() => {
      })

      return this.componentsReferenceMap[name]
    },
    setShowPluginShadow() {
      window.$wujie.bus.$emit('setCurrentPluginName', 'ncModals')
    },

    async useNcModalData(fnComponentName, params) {
      const BaseModal = {
        DEPT: 'DEPT',
        PSN: 'PSN',
        CUSTOM: 'CUSTOM',
        GYS: 'GYS',
        SYSTEM_MESSAGE: 'SYSTEM_MESSAGE'
      }
      const componentName = fnComponentName({To:BaseModal})
      this.setShowPluginShadow()
      // Get or Create a Component
      const dyComponent = await this.getOrCreateComponent(componentName)
      // setInterval(()=>{
      //   console.log(dyComponent.ref)
      // },1000)
      // open component

      dyComponent.fnOpen(params)
      // wait data return .....
      return await dyComponent.waitOk()
    },

  }
});

// Need to be used outside the setup
export function usePluginStoreWidthOut() {
  return usePluginStore(store);
}
