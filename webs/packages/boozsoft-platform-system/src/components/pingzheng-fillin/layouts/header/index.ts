import {nextTick, ref} from "vue";
export { default as LayoutHeader } from './index.vue';
export function usePingzhengHeader() {
    const instance = ref()
    let openHeader=null
    const params={
        registerHeader({instance:$instance,openHeader:$openHeader}) {
            instance.value = $instance
            openHeader=$openHeader
        },
        openPingzhengHeader($pingzhengViewModel) {
            nextTick(()=>{
                openHeader($pingzhengViewModel)
            })
            // instance.value.openHeader()
        }
    }
    return params
}