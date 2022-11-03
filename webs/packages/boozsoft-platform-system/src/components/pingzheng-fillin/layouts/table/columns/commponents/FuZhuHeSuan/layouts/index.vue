<template>
  <Index2 v-if="open && jieSuanFangshiList != null" v-bind="$attrs" />
</template>
<script setup>
  import Index2 from './index2.vue';
  import { getFuZhuHeSuanDefines } from '/@/components/pingzheng-fillin/layouts/table/columns/commponents/FuZhuHeSuan/hooks/fuZhuHeSuanTable';
  import { inject, provide, ref } from 'vue';

  const tableData = inject('tableData');
  const open = ref(false);
  const props = defineProps(['modelValue', 'rowDefine']);

  const requireFuZhuColumns = ref();
  const jieSuanFangshiList = ref();

  function isProjectCtr() {
    return datasourcePicker.value.params.settings.settingList.indexOf('projectClassCtl') != -1;
  }
  (async () => {
    jieSuanFangshiList.value = await tableData.value.repository.fuZhuHeSuan.findJieSuanFangShiAll();
    requireFuZhuColumns.value = await getFuZhuHeSuanDefines(props.modelValue.value, isProjectCtr);
    open.value = true;
  })();

  provide('jieSuanFangshiList', jieSuanFangshiList);
  provide('requireFuZhuColumns', requireFuZhuColumns);
  provide('props', props);
</script>
