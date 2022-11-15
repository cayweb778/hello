// // import {usePlatformsStoreWidthOut} from "/@/store/modules/platforms";
// //
// // window?.$wujie.bus.$on("openDept", function (id) {
// //
// // });
// //
// // window?.$wujie.bus.$on("openEmp", function (id) {
// // });
// //
// // // 主应用
// // const NcModal={
// //   openDept(){
// //
// //   },
// //   openEmp(){
// //
// //   },
// //   openCustomer(){
// //
// //   }
// // }
// //
// //
// // // 子应用
// // const result=await NcModal.openDept({
// //
// // })
//
//
// import {ref, shallowRef} from "vue";
// import {awaitData, awaitData2} from "/@/views/boozsoft/global/funs/bb";
//
//
// async function getOrCreateComponent(arr, cmpts, name2) {
//   const arr = {
//     dept: ['dept', '../dept/index.vue'],
//     psn: ['dept', '../psn/index.vue'],
//     custom: ['custom', '../custom/index.vue'],
//     gys: ['gys', '../gys/index.vue'],
//     // d:['dept', '../dept/index.vue'],
//   }
//   const [name, path] = arr[name2]
//   const aa = () => import(path)
//   // const aa = () => import("../abc2.vue")
//   const c = await aa()
//   let json = {
//     component: c.default,
//     componentParams: 1,
//     componentResult: function () {
//
//     }
//   }
//   if (cmpts.value.dept == null) {
//     arr.push(json)
//     cmpts.value[name] = json
//     return cmpts.value[name]
//   }
// }
//
//
// async function useNcModalComponents(components, componentsMap, componentName) {
//
//   const dyComponent = await getOrCreateComponent(components, componentsMap,componentName)
//
//   async function openModalAndWaitData(params) {
//     window.$wujie.bus.$emit('setShowPluginShadow', true)
//     return await awaitData(dyComponent, params)
//   }
//
//   return {
//     openModalAndWaitData
//   }
// }
//
// function useModals2() {
//   const components = ref({})
//   const cmpts = ref({})
//
//
//   return {
//     components,
//     async execModal(componentName, params) {
//       const ncModalComponent = await useNcModalComponents(components, cmpts, componentName)
//       return await ncModalComponent.openModalAndWaitData(params)
//     }
//   }
// }
//
//
// export function defineNcModals() {
//   const {execModal, arr} = useModals2()
//   const fns = {
//     async openNotifyMessageModal(params) {
//       return await execModal('dept', params)
//     },
//     async openDeptModal(params) {
//       return await execModal('dept', params)
//     },
//     async openPsnModal(params) {
//
//       return await execModal('psn', params)
//
//     },
//     async openCustomModal(params) {
//       return await execModal('custom', params)
//
//     },
//     async openGysModal(params) {
//       return await execModal('gys', params)
//     },
//
//   }
//   return {arr, fns}
// }
