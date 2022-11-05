<template>
  <slot></slot>
</template>
<script setup>
import {
  createPingzhengStore,
  usePingzhengCreator,
  usePingZhengStore,
} from '/@/components/pingzheng-fillin/hooks/usePingzheng';
  import { useCompanyOperateStoreWidthOut } from '/@/store/modules/operate-company';
  import { provide, ref } from 'vue';
  import { useApiRepository } from '/@/components/pingzheng-fillin/provide/funs';
  import { createPinia } from 'pinia';

  // debugger
  window.debuggerInfo.getCurrentTenant = () => {
    return useCompanyOperateStoreWidthOut().getCurrentAccountInfo;
  };

  // 凭证缓存
  const pingzhengCache = ref();

  // api仓库
  const apiRepository = useApiRepository();

  // 实例
  const instance = createPinia();

  // console.log(usePingzhengCreator().getExistPingzhengData())

  // 使用默认凭证数据源
  function useDefaultPingzhengData(){
    console.log(usePingzhengCreator().hasPingzhengData()
      ? usePingzhengCreator().getExistPingzhengData()
      : null)
    //
    // 如果检查到凭证源使用凭证源数据，否则创建新数据
    return usePingzhengCreator().hasPingzhengData()
      ? usePingzhengCreator().getExistPingzhengData()
      : null
  }

  // 凭证store
  const pingzhengStore = createPingzhengStore(instance,{
      pingzhengData: useDefaultPingzhengData()
    })
  pingzhengStore.apiRepository = apiRepository;

  // 凭证核心模型
  provide('pingzhengModel', pingzhengStore);
  provide('pingzhengCache', pingzhengCache);
</script>
