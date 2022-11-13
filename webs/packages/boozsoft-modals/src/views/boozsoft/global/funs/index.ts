// import {usePlatformsStoreWidthOut} from "/@/store/modules/platforms";
//
// window?.$wujie.bus.$on("openDept", function (id) {
//
// });
//
// window?.$wujie.bus.$on("openEmp", function (id) {
// });
//
// // 主应用
// const NcModal={
//   openDept(){
//
//   },
//   openEmp(){
//
//   },
//   openCustomer(){
//
//   }
// }
//
//
// // 子应用
// const result=await NcModal.openDept({
//
// })


import {ref, shallowRef} from "vue";
import abc1 from "../abc1.vue";
import {
  useOpenNotifyMessageModal
} from "/@/views/boozsoft/global/funs/modules/notifyMessage";

export function defineNcModals(json) {
  // 跳转平台监听
  // window?.$wujie.bus.$on("openModal", function (callbak) {
  //   console.log(888880000)
  //   // console.log(callbak())
  //   return 'aabcc'
  // });
  const componentData = {
    componentRef: ref(abc1),
    componentParams: 1,
    componentResult: function () {

    }
  }
  const abc = {
    componentData,
    openNotifyMessageModal: useOpenNotifyMessageModal(json,componentData),
    async openDeptModal(params2) {

      return new Promise(async (r, j) => {
        const aa = () => import("../dept/index.vue")
        // const aa = () => import("../abc2.vue")
        const c = await aa()

        let isBack = false
        let data = null
        json.changeComponent( c.default)
        abc.componentData.componentParams = params2
        abc.componentData.componentResult = function (d) {
          isBack = true
          data = d
        }


        const sadsadasdda = setInterval(() => {
          if (isBack) {
            console.log(3213123213)
            r(data)
            window.clearInterval(sadsadasdda)
          }
        }, 100)

      })

    },

    async openPsnModal(params2) {

      return new Promise(async (r, j) => {
        const aa = () => import("../psn/index.vue")
        // const aa = () => import("../abc2.vue")
        const c = await aa()

        let isBack = false
        let data = null

        json.changeComponent( c.default)
        abc.componentData.componentParams = params2
        abc.componentData.componentResult = function (d) {
          isBack = true
          data = d
        }


        const sadsadasdda = setInterval(() => {
          if (isBack) {
            console.log(3213123213)
            r(data)
            window.clearInterval(sadsadasdda)
          }
        }, 100)

      })

    },
    async openCustomModal(params2) {


      return new Promise(async (r, j) => {
        const aa = () => import("../custom/index.vue")
        // const aa = () => import("../abc2.vue")
        const c = await aa()

        let isBack = false
        let data = null


        json.changeComponent( c.default)
        abc.componentData.componentParams = params2
        abc.componentData.componentResult = function (d) {
          isBack = true
          data = d
        }


        const sadsadasdda = setInterval(() => {
          if (isBack) {
            console.log(3213123213)
            r(data)
            window.clearInterval(sadsadasdda)
          }
        }, 100)

      })

    },
    async openGysModal(params2) {

      return new Promise(async (r, j) => {
        const aa = () => import("../gys/index.vue")
        // const aa = () => import("../abc2.vue")
        const c = await aa()

        let isBack = false
        let data = null


        json.changeComponent( c.default)
        abc.componentData.componentParams = params2
        abc.componentData.componentResult = function (d) {
          isBack = true
          data = d
        }


        const sadsadasdda = setInterval(() => {
          if (isBack) {
            console.log(3213123213)
            r(data)
            window.clearInterval(sadsadasdda)
          }
        }, 100)

      })

    },
    abc() {

    }
  }
  return abc
}


