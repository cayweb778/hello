export function useOpenNotifyMessageModal(json,componentData) {

  async function openNotifyMessageModal(params2) {

    window.$wujie.bus.$emit('setShowPluginShadow',true)
    return new Promise(async (r, j) => {
      const aa = () => import("./index.vue")
      // const aa = () => import("../abc2.vue")
      const c = await aa()
      json.changeComponent(c.default)
      let isBack = false
      let data = null
      componentData.componentRef.value = c.default;
      componentData.componentParams = params2
      componentData.componentResult = function (d) {
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

  }

  return openNotifyMessageModal
}
