<template>
  <div ref="kuaiJiKeMuGridRef" class="kuaiJiKeMuGrid">
    <KuaiJiKeMuPicker
      ref="inputRef"
      :options="codeList"
      v-model="codeValue"
      v-model:open="openKuaiJiKeMuPicker"
      :inputEvents="inputEvents"
      @change="changeKuaiJiKeMu"
    >
      <template #icon>
        <div v-if="isFuZhuHeSuan" @mousedown="openFuZhuXiang" class="fuZhuBtn">[辅]</div>
      </template>
      <template #record>


        <div @mousedown="openRecord" class="fuZhuBtn2"> <SearchOutlined  class="searchIco" /></div>
      </template>
    </KuaiJiKeMuPicker>

    <CodePop @throwData="modalData" @register="registerCodePopPage" />
  </div>
</template>
<script lang="ts" setup>
  import { inject, onMounted, ref, nextTick, provide, computed } from 'vue';
  import KuaiJiKeMuPicker from '../../pickers/KuaiJiKeMuPicker/layouts/index.vue';
  import { waitLoadPingzheng } from '/@/components/pingzheng-fillin/layouts/table/columns/KuaiJiKeMu/layouts/content/funs';
  import { useModal } from '/@/components/Modal';
  import { SearchOutlined } from '@ant-design/icons-vue';
  import CodePop from '/@/views/boozsoft/system/acccode2/popup/modalPop.vue';
  const tableData = inject('tableData');
  const registerEvents = inject('registerEventFun');
  const codeList = inject('codeList');
  const getCodeInfo = inject('getCodeInfo');
  const pingzhengGridTextFuns = inject('pingzhengGridTextFuns');
  const refrechPingzhengGridTextFuns = inject('refrechPingzhengGridTextFuns');
  const inputRef = ref();
  const kuaiJiKeMuGridRef = ref();

  const openKuaiJiKeMuPicker = ref(false);
  const codeValue = pingzhengGridTextFuns.value.getKuaiJiKeMuInfoText();

  function modalData(e) {
    changeKuaiJiKeMu(e.ccode);
  }
  const emit = defineEmits(['change', 'close']);

  function changeKuaiJiKeMu(e) {
    nextTick(async () => {
      await waitLoadPingzheng(tableData.value.loading);
      emit('change', getCodeInfo(e));
    });
  }
  const [registerCodePopPage, { openModal: openCodePopPage }] = useModal();
  function openRecord() {
    openCodePopPage(true, {
      database: 'bjhzzkgl-001-2021',
      accId: 'bjhzzkgl-001',
    });
    console.log(12312321);
  }

  const isFuZhuHeSuan = computed(() => {
    return pingzhengGridParams.columnDefine.rowData.fuZhuHeSuan.value.length > 0;
  });
  const inputEvents = {
    keyup(e) {
      switch (e.key) {
        case 'ArrowLeft':
          registerEvents.left(e);
          break;
        case 'ArrowRight':
          registerEvents.right(e);
          break;
        case 'Enter':
          registerEvents.next();
          inputRef.value.blur();
          break;
        case 'Tab':
          registerEvents.next();
          break;
      }
    },
    keydown(e) {
      switch (e.key) {
        case 'ArrowUp':
          registerEvents.up();

          break;
        case 'ArrowDown':
          registerEvents.down();
          break;
      }
    },
    input() {
      openKuaiJiKeMuPicker.value = true;
    },
    // change(e) {
    //   if (e.target.value == '') {
    //     gridData.modelValue.value.code = '';
    //     gridData.modelValue.value.name = '';
    //   }
    // },
    focus() {
      nextTick(() => {
        kuaiJiKeMuGridRef.value.querySelector('textarea').select();
      });
    },
    close(e) {
      emit('close');
      // if (e.relatedTarget == null) {
      //   // openSelect.value=false
      //   setTimeout(() => {
      //     if (codeValue.value == '') {
      //       registerEvents.blur();
      //     }
      //   }, 200);
      // }
    },
    search() {
      openKuaiJiKeMuPicker.value = true;
    },
  };
  const openFuZhuXiang = inject('openFuZhuXiang');

  onMounted(() => {
    setTimeout(() => {
      inputRef.value.focus();
    }, 200);
  });
  const pingzhengGridParams = inject('pingzhengGridParams');

  function clearInput() {
    refrechPingzhengGridTextFuns();
    pingzhengGridParams.modelValue.columnDatas.kuaiJiKeMu.text = '';
    codeValue.value = '';
    pingzhengGridParams.registerGrid.rowData.fuZhuHeSuan.jieSuanCompany = null;
    pingzhengGridParams.registerGrid.rowData.fuZhuHeSuan.jieSuanMode = null;
    pingzhengGridParams.registerGrid.rowData.fuZhuHeSuan.piaoJuDate = null;
    pingzhengGridParams.registerGrid.rowData.fuZhuHeSuan.piaoJuNumber = null;
    pingzhengGridParams.registerGrid.rowData.fuZhuHeSuan.value.forEach((item) => {
      item.value.value = '';
    });

    pingzhengGridParams.columnDefine.requireModels = [
      'zhaiYao',
      'kuaiJiKeMu',
      'jieMoney',
      'daiMoney',
    ];
  }

  defineExpose({ focus, clearInput });
</script>
<style scoped>
  .kuaiJiKeMuGrid {
    height: 100%;
    position: relative;
  }
  .fuZhuBtn {
    cursor: pointer;
    position: absolute;
    right: 40px;
    top: 10px;
    z-index: 111110000;
  }
  .fuZhuBtn:hover {
    color: blue;
  }

  .fuZhuBtn2:hover {
    color: blue;
  }
  .fuZhuBtn2 {
    cursor: pointer;
    position: absolute;
    right: 10px;
    top: 10px;
    z-index: 111110000;
  }
</style>
