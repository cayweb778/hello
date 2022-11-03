<template>
  <div style="height: 100%; position: relative">
    <template v-if="!isFuZhuXiang">
      <KuaiJiKeMu ref="kuaiJiKeMuRef" v-if="codeList != null" v-on="kuaiJiKeMuEvents" />
    </template>
    <FuZhuXiang v-else />
  </div>
</template>
<script setup>
  import FuZhuXiang from './grids/FuZhuXiang.vue';
  import KuaiJiKeMu from './grids/KuaiJiKeMu.vue';
  import { provide, ref, inject, onMounted, nextTick, watch } from 'vue';
  import { useCodeInfo, usePingzhengGridText } from './funs';
  import { hasDebuggerInfoEnable } from '../../../../../../../../utils/boozsoft/boozsoftDebuggerInfo/pingzhengFillin';
  import { createKuaJiKeMuDebuggerInfo } from './debuggerInfo/funs';
  const pingzhengGridParams = inject('pingzhengGridParams');
  const registerEvents = inject('registerEventFun');
  const tableData = inject('tableData');

  const gridData = defineProps(['modelValue']);

  const isFuZhuXiang = ref(false);

  const codeList = ref();

  const kuaiJiKeMuRef = ref();

  const pingzhengGridTextFuns = ref(usePingzhengGridText(gridData.modelValue));

  function refrechPingzhengGridTextFuns() {
    pingzhengGridTextFuns.value = usePingzhengGridText(gridData.modelValue);
  }
  provide('refrechPingzhengGridTextFuns', refrechPingzhengGridTextFuns);

  const { setCodeInfo, setCodeInfo2, clearInfo, codeNotEmpty, getCodeInfo, hasFuZhuXiang } =
    useCodeInfo(
      gridData,
      pingzhengGridTextFuns,
      pingzhengGridParams,
      isFuZhuXiang,
      registerEvents,
      codeList,
    );

  function kuaiJiKeMuFocus() {
    console.log(kuaiJiKeMuRef.value);
    debugger;
    kuaiJiKeMuRef.value.focus();
  }

  const kuaiJiKeMuEvents = {
    async change(codeInfo) {
      setCodeInfo(codeInfo);

      if (await hasFuZhuXiang(pingzhengGridParams)) {
        isFuZhuXiang.value = true;
      } else {
        registerEvents.blur();
      }
    },
    close() {
      registerEvents.blur();
    },
  };

  function openFuZhuXiang() {
    isFuZhuXiang.value = true;
  }

  async function openKuaiJiKeMu() {
    gridData.modelValue.columnDatas.kuaiJiKeMu.text = '';
    pingzhengGridTextFuns.value.setKuaiJiKeMuInfoText('');
    isFuZhuXiang.value = false;

    await new Promise((r) => {
      nextTick(() => {
        r();
      });
    });
  }

  function clearKuaiJiKeMuInput() {
    kuaiJiKeMuRef.value.clearInput();
  }

  provide('openFuZhuXiang', openFuZhuXiang);
  provide('openKuaiJiKeMu', openKuaiJiKeMu);
  provide('clearKuaiJiKeMuInput', clearKuaiJiKeMuInput);
  const abc=inject('datasourcePicker');
  const pingzhengModel = inject('pingzhengModel');
  onMounted(async () => {
    codeList.value = await pingzhengModel.apiRepository.findKuaiJiKeMuAll(abc);

    codeNotEmpty() && setCodeInfo2(getCodeInfo(gridData.modelValue.value.code));

    hasDebuggerInfoEnable() && createKuaJiKeMuDebuggerInfo(gridData, pingzhengGridParams);
  });

  provide('codeList', codeList);
  provide('getCodeInfo', getCodeInfo);
  provide('codeNotEmpty', codeNotEmpty);
  provide('setCodeInfo', setCodeInfo);
  provide('clearInfo', clearInfo);
  provide('pingzhengGridTextFuns', pingzhengGridTextFuns);
  provide('pingzhengGridParams', pingzhengGridParams);
  provide('isFuZhuXiang', isFuZhuXiang);
  provide('hasFuZhuXiang', hasFuZhuXiang);
  provide('gridData', gridData);
  provide('kuaiJiKeMuFocus', kuaiJiKeMuFocus);
</script>
