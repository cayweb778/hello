<template>
  <Layout>
    <!-- 格子文本 -->
    <GridText v-if="visible"  @click="!onlyShow &&(visible=false)"   :onlyShow="onlyShow" :showSlot="slots.showSlot" :showText="showText" :tooltipText="tooltipText" :isWhite="isWhite">
      <slot v-if="slots.showSlot" name="showSlot"></slot>
    </GridText>

    <!-- 格子控件 -->
    <GridControl v-else><slot/></GridControl>
  </Layout>
</template>
<script setup>
  import {
    computed,
    getCurrentInstance,
    inject,
    markRaw,
    nextTick,
    onMounted,
    provide,
    ref,
    useSlots,
  } from 'vue';
  import { useRegiterEvent } from '/@/components/pingzheng-fillin/utils/regiterEvent';

  import Layout from '/@/components/pingzheng-fillin/layouts/table/columns/commponents/PingzhengGrid/components/Layout.vue';
  import GridText
    from "/@/components/pingzheng-fillin/layouts/table/columns/commponents/PingzhengGrid/TextPoint.vue";
  import GridControl
    from "/@/components/pingzheng-fillin/layouts/table/columns/commponents/PingzhengGrid/GridContent.vue";
  const slots = useSlots();

  const emit = defineEmits(['register']);

  const tableData = inject('tableData');
  const props = defineProps([
    'modelValue',
    'registerGrid',
    'columnDefineKey',
    'columnDefine',
    'columnModelDefineKey',
  ]);

  const visible = ref(true);

  provide('visible',visible)

  const showText = computed(() => props.modelValue.columnDatas[props.columnDefineKey].text);
  const tooltipText = computed(() => props.modelValue.columnDatas[props.columnDefineKey].tooltip);
  const isWhite = computed(
    () => {
      return props.columnDefine.requireModels.indexOf(props.columnModelDefineKey) != -1
    },
  );
  const onlyShow = computed(() => tableData.value.settings.onlyShow || props.modelValue.readonly);

  const pingzhengGridState = {
    emit,
    data: {
      focus: function () {
        visible.value = false;
        nextTick(() => {});
      },
      blur: function () {
        visible.value = true;
      },
    },
  };
  const registerEventFun = markRaw(useRegiterEvent(pingzhengGridState));

  onMounted(() => {
    const instance = getCurrentInstance();
    props.registerGrid.target.value = markRaw(instance);
  });

  provide('pingzhengGridState', pingzhengGridState);
  provide('registerEventFun', registerEventFun);
  provide('pingzhengGridParams', props);
</script>
<style>
  .helloTextTest {
    font-weight: 900;
    word-break: break-all;
    height: 100%;
    text-align: left;
    position: absolute;
    z-index: 10000000;
    width: 100%;
  }
</style>
