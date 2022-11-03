<template>
  <div v-if="startPopup" style="height: 100%; z-index: 11111111111111">
    <div style="height: 100%">
      <ShowTextarea v-model="pingzhengGridText" :visible="true" />
    </div>
    <FuZhuXiang ref="mainRef" v-model="fuZhuHeSuanData" />
  </div>
</template>
<script lang="ts" setup>
  import FuZhuXiang from '../../../../commponents/FuZhuHeSuan/index.vue';
  import { computed, getCurrentInstance, inject, onMounted, provide, ref } from 'vue';
  import ShowTextarea from '/@/components/pingzheng-fillin/layouts/table/columns/KuaiJiKeMu/layouts/content/components/ShowTextarea.vue';

  const emit = defineEmits(['close', 'register']);
  const startPopup = ref();
  const mainRef = ref();
  const fuZhuHeSuanData = ref();
  const closeFun = ref();
  const usePopoverContainer = inject('usePopoverContainer');
  const pingzhengGridParams = inject('pingzhengGridParams');
  const gridData = inject('gridData');
  const pingzhengGridText = computed(() => gridData.modelValue.columnDatas['kuaiJiKeMu'].text);
  const fuZhuXiangState = {
    instance: getCurrentInstance(),
    closeFun,
    isOpen() {
      return startPopup.value;
    },
    triggerPopover() {
      startPopup.value ? this.closePopover() : this.openPopover();
    },
    openPopover($fuZhuHeSuanData) {
      fuZhuHeSuanData.value = $fuZhuHeSuanData;
      startPopup.value = true;
      // nextTick(() => mainRef.value.focus())
    },
    closeShade() {
      // startPopup.value = false;
      usePopoverContainer.close();
    },
    closePopover() {
      fuZhuXiangState.closeShade();
      pingzhengGridParams.modelValue.next();
      usePopoverContainer.close();
      this.closeFun.value();
    },
    focus() {},
    mainRef,
  };
  provide('fuZhuXiangState', fuZhuXiangState);
  onMounted(() => {
    emit('register', fuZhuXiangState);
  });
</script>
