import {ref} from "vue";

export function usePingZhengModal() {
  const currentInstance=ref()
  return {
    registerPingZhengModal(instance){
      currentInstance.value=instance
    },
    openPingzhengModal(apiData,params) {
      currentInstance.value.exposed.openPingzhengModal(apiData,params)
    }
  }
}
