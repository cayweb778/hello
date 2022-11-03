<template>
  <div style="height: 100%; position: relative">
    <ZhaiYaoPicker
      ref="pickerRef"
      v-model="inputText"
      :items="zhaiYaoList"
      :open="isOpenPicker"
      @change="pickerChange"
      @blur="pickerBlur()"
    />
  </div>
</template>
<script setup>
  import ZhaiYaoPicker from './commponents/ZhaiYao/components/ZhaiYaoPicker.vue';
  import { useRegiterEventNoEmit } from '/@/components/pingzheng-fillin/utils/regiterEvent';
  import {ref, onMounted, watch, nextTick, inject, provide} from 'vue';
  import { getZhaiYaoList } from '/@/components/pingzheng-fillin/layouts/table/columns/commponents/ZhaiYao/components/funs';

  const usePageRouterApi = inject('usePageRouterApi');

  const props = defineProps(['modelValue', 'registerEventFun']);

  const pickerRef = ref();
  const isOpenPicker = ref(false);
  const zhaiYaoList = ref([]);
  const registerEvents = ref();
  const inputText = ref('');

  function setGridValue(e) {
    props.modelValue.value = e;
    props.modelValue.columnDatas['zhaiYao'].text = e;
  }
  const pingzhengGridState=inject('pingzhengGridState');
  function pickerBlur(){
    console.log(pingzhengGridState)
    pingzhengGridState.data.blur()
  }

  function pickerChange(e) {
    inputText.value = e;

    isOpenPicker.value = false;
    nextTick(() => {
      setGridValue(e);
      registerEvents.value.next();
    });
  }

  watch(
    props,
    () => {
      props.modelValue.columnDatas['zhaiYao'].text = props.modelValue.value;
    },
    { immediate: true },
  );
  watch(inputText, () => {
    props.modelValue.columnDatas['zhaiYao'].text = inputText.value;
  });
  const registerEventFun = inject('registerEventFun');
  registerEvents.value=registerEventFun
  onMounted(async () => {
    pickerRef.value.focus();
    zhaiYaoList.value = await getZhaiYaoList(usePageRouterApi);
    inputText.value = props.modelValue.columnDatas['zhaiYao'].text;

    Object.assign(
      registerEventFun,
      useRegiterEventNoEmit({
        data: {
          setValue(e) {
            props.modelValue.value = e;
            props.modelValue.columnDatas['zhaiYao'].text = e;
            inputText.value = e;
          },
          rowIndex: null,
        },
      }),
    );
  });
</script>
<style scoped>
  td {
    border: solid 1px #828282 !important;
    padding: 0 5px;
  }

  :deep(.thisTable th),
  :deep(.thisTable td) {
    border: solid 1px #828282;
    padding: 0 !important;
  }

  :deep(.thisTable .ant-table-thead th) {
    background: #e3e2e2 !important;
    font-weight: 900;
  }
</style>
<style scoped>
  .kuaiJiKeMuSelect {
    width: auto !important;
    color: black;
    border-radius: 10px;
    box-shadow: 0 0 6px black;
  }

  .thisTable th,
  .thisTable td {
    border: solid 1px black;
    padding: 0 !important;
  }

  .hoverRow {
    background: red;
  }
</style>
