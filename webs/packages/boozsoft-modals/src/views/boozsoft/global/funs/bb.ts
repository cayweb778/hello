import {ref} from "vue";

function useCallbak(dyComponent, params) {
  let isBak = ref(false)
  let data = ref(null)
  dyComponent.componentParams = params

  dyComponent.componentResult = function (e) {
    isBak.value = true
    data.value = e
  }
  return {isBak, data}
}

export function waitOk(json){
  return new Promise(async (r, j) => {
    const clearInterVal = setInterval(() => {
      if (json.isBak.value) {
        r(json.data.value)
        window.clearInterval(clearInterVal)
      }
    }, 100)

  })
}
export async function awaitData(dyComponent, params){
  awaitData2(useCallbak(dyComponent, params))
}
