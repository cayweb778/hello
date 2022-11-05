<template>
  <div :class="{ enablethis: true, thisDiv: true, isError: isError }" style="height: 100%">
   
    <Tooltip v-model:visible="showTip" trigger="contextmenu" placement="bottom">
      <template #title>
        <div v-html="showHtml"> </div>
      </template>
    </Tooltip>
    <FuZhuHeSuanPopover />
  </div>
</template>
<script setup>
  import { useRegiterEvent } from '/@/components/pingzheng-fillin/utils/regiterEvent';
  import { watch, computed, onMounted, ref, inject } from 'vue';
  import { Tooltip } from 'ant-design-vue';
  import { useModal } from '/@/components/Modal';
  import EditPage from '/@/views/boozsoft/system/acccode2/popup/edit.vue';
  import FuZhuHeSuanPopover from '/@/components/pingzheng-fillin/layouts/table/columns/commponents/FuZhuHeSuan/layouts/popover/index.vue';
  import { useFuZhuHeSuanDefineStore } from '/@/store/modules/boozsoft/account/pingzheng-model/fuZhuHeSuan/fuZhuHeSuanStore';
  import { addInfo } from '/@/utils/boozsoft/boozsoftDebuggerInfo/pingzhengFillin';
  import {
    defineFuZhuXiangModel,
    useFuZhuXiangText,
  } from '/@/components/pingzheng-fillin/layouts/table/columns/commponents/FuZhuHeSuan/layouts/funs';

  const emit = defineEmits(['register']);

  const rowDefines = inject('rowDefines');
  const rowFuZhuHeSuanStore = inject('rowFuZhuHeSuanStore');
  const pingzhengGridParams = inject('pingzhengGridParams');
  const tableData = inject('tableData');
  const pingzhengGridTextFuns = inject('pingzhengGridTextFuns');

  const rowDefine = rowDefines[pingzhengGridParams.columnDefine.rowIndex];
  const fuZhuHeSuanStore = useFuZhuHeSuanDefineStore(rowFuZhuHeSuanStore);

  const props = inject('props');

  const showSearch = ref(true);
  const showTip = ref(false);

  const jieSuanFangshiList = inject('jieSuanFangshiList');
  const requireFuZhuColumns = inject('requireFuZhuColumns');

  const rowFuZhuHeSuanData = computed(() => props.modelValue);

  const isFuZhuHeSuanEnable = computed(() => rowDefine.requireModels.indexOf('fuZhuHeSuan') != -1);

  const fuZhuHeSuanTableModel = defineFuZhuXiangModel(requireFuZhuColumns, props);

  const columnModelsComputeds = {
    jieSuanFangShi: computed(() => fuZhuHeSuanTableModel.value.columnModels.models[0]),
    fuZhuXiang: computed(() => fuZhuHeSuanTableModel.value.columnModels.models[1]),
  };

  const { showText, showHtml } = useFuZhuXiangText(
    props,
    jieSuanFangshiList,
    columnModelsComputeds,
  );

  const registerEvents = useRegiterEvent({
    emit,
    data: {
      focus() {
        // setTimeout(() => {
        //   inputRef.value.focus();
        // }, 1000);
        // visible.value=true
      },
      requireModels: null,
      rowIndex: null,
      setRequireModels: null,
      setBiZhong: null,
      setUnit: null,
    },
  });

  const isError = computed(() => {
    const errInfo = [];
    if (isFuZhuHeSuanEnable.value) {
      if (props.modelValue.jieSuanMode !== null) {
        if (props.modelValue.jieSuanMode === '') {
          errInfo.push(['未填写结算方式']);
        }
        if (props.modelValue.piaoJuDate === '') {
          errInfo.push(['未填写票据日期']);
        }
        if (props.modelValue.piaoJuNumber === '') {
          errInfo.push(['未填写票据号']);
        }
      }
      props.modelValue.value.forEach((item) => {
        if (item.value === '') {
          errInfo.push(['未填写' + item.label + '辅助核算']);
        }
      });
      return errInfo.length > 0;
      return true;
    } else {
      return false;
    }
  });

  const [registerEditPage, { openModal: openEditPage }] = useModal();

  function saveData() {}

  function nextFun(e) {
    var e = window.event || event;
    if (e.preventDefault) {
      e.preventDefault();
    } else {
      window.event.returnValue = false;
    }
    registerEvents.next();
    pingzhengGridParams.modelValue.next();
    fuZhuHeSuanStore.setShowPopover(false);
  }

  const inputEvents = {
    mouseenter: function () {
      showTip.value = true;
      // if (!visible.value) {
      //   // showTip.value = true
      // }
    },
    mouseleave: function () {
      showTip.value = false;
    },
    keydown(e) {
      switch (e.key) {
        case 'ArrowUp':
          console.warn('未开发');
          // keydownUp(e)
          break;
        case 'ArrowDown':
          console.warn('未开发');
          // keydownUp(e)
          break;
        case 'ArrowLeft':
          registerEvents.left(e);
          e.preventDefault();
          break;
        case 'ArrowRight':
          registerEvents.right(e);
          e.preventDefault();
          break;
        case 'Tab':
          abcdd(e);
          break;
        case 'Enter':
          e.preventDefault();
          setTimeout(() => {
            registerEvents.next();
          }, 200);
          e.preventDefault();
          break;
        case ' ':
          showTip.value = false;
          var e = window.event || event;
          if (e.preventDefault) {
            e.preventDefault();
          } else {
            window.event.returnValue = false;
          }
          inputEvents.searchPage();
          e.preventDefault();
          break;
      }
    },
    focus: function () {},
    blur: function () {
      debugger;
      setTimeout(() => (showSearch.value = false), 300);
    },
    searchPage() {
      setTimeout(() => {
        fuZhuHeSuanStore.setShowPopover(true);
      }, 200);
    },
  };

  watch(showText, () => pingzhengGridTextFuns.value.setFuZhuXiangText(showText.value));

  onMounted(async () => {
    fuZhuHeSuanStore.setShowPopover(true);
  });

  fuZhuHeSuanStore.setFuZhuHeSuanTableModel(fuZhuHeSuanTableModel);
  fuZhuHeSuanStore.setRowFuZhuHeSuanData(rowFuZhuHeSuanData);
  fuZhuHeSuanStore.setNextFun(nextFun);

  addInfo(pingzhengGridParams.columnDefine.rowIndex, {
    fuZhuHeSuan: {
      fuZhuHeSuanStore,
      fuZhuHeSuanTableModel,
      requireFuZhuColumns,
    },
  });
</script>

<style lang="less">
  @import '../../../styles/fuZhuHeSuan.less';
</style>
<style scoped>
  :deep(.inputRef) {
    float: left;
    height: 100% !important;
    padding-right: 20px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    cursor: pointer;
  }

  :deep(.fuZhuText) {
    position: relative;
    width: 100%;
    height: 100% !important;
  }

  :deep(input) {
    height: 100% !important;
  }

  .searchIco {
    position: absolute;
    top: 8px;
    right: 3px;
    cursor: pointer;
  }
</style>
<style>
  .fuZhuHeSuanInputClass {
    width:350px;
    border-radius: 4px;
    border: solid 1px #d1d1d1;
    height: 30px;
  }
</style>
