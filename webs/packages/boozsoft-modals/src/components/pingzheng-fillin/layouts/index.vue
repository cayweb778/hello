<template>
  <Layout appear radius>
    <LayoutHeader v-model="pingzhengStore.pingzhengEditorViewModel" />
    <LayoutContent>
      <MultipleTable
        v-if="isMultipleTable"
        :items="
          pingzhengStore.pingzhengEditorViewModel.value.instances.map(
            (it) => it.params.pingzhengData,
          )
        "
      />
      <TableContent
        v-else
        v-model="pingzhengStore.pingzhengEditorViewModel.value.instances[0].params.pingzhengData"
      />
    </LayoutContent>
  </Layout>
</template>
<script lang="ts" setup>
  import { computed, inject, provide } from 'vue';
  import Layout from '/@/components/pingzheng-fillin/components/Layout/index.vue';
  import LayoutContent from './content/index.vue';
  import {
    usePingzhengHeader,
    LayoutHeader,
  } from '/@/components/pingzheng-fillin/layouts/header/index';
  import TableContent from './table/index.vue';
  import MultipleTable from './table/components/MultipleTable.vue';
  import { createPinia } from 'pinia';
  import { usePingZhengStore } from '/@/components/pingzheng-fillin/hooks/usePingzheng';

  const { registerHeader, openPingzhengHeader } = usePingzhengHeader();

  function usePageable() {
    let instance = null;
    return {
      register(e) {
        instance = e;
      },
      getPageData() {
        return instance;
      },
    };
  }
  const { register: registerPageData, getPageData } = usePageable();

  // 凭证核心业务
  const pingzhengStore = inject('pingzhengModel');

  // 是否是双凭证表
  const isMultipleTable = computed(
    () => pingzhengStore.pingzhengEditorViewModel.value.instances.length > 1,
  );

  provide('viewModel', pingzhengStore.pingzhengEditorViewModel);
  provide('registerPageData', registerPageData);
  provide('getPageData', getPageData);
  provide('reloadPingZheng', pingzhengStore.reloadPingZheng);
  provide('openPingzhengHeader', openPingzhengHeader);
  provide('onlyShow', null);
  provide('noApi', null);

  window.debuggerInfo.pingzheng.pingzhengModel = pingzhengStore;
</script>
<style lang="less">
  @import '../styles/pingzheng.less';
  @import '../styles/table.css';
</style>
