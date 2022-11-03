<template>
  <div class="ccaaa" style="position: absolute; width: 100%; height: 100%">
    <!--    <EditPage @save="saveData" @register="registerEditPage"/>-->
    <KuaiJiKeMuManager />
    <Popover placement="bottom" overlayClassName="pingzhengPopover" :getPopupContainer="()=>pageRootDom" :visible="true">
      <template #content>
        <div class="selectRef2">
          <div ref="hello2222" class="asdasd">

            <PingzhengCodePicker
              ref="vScroll"
              :items="filterCodeList"
              @change="change($event.key)"
              v-model="searchStr"
            />


          </div>
        </div>
      </template>
      <div style="position: absolute; width: 100%; top: 0; height: 100%"> </div>
    </Popover>
  </div>
</template>
<script setup>
import {
  computed,
  getCurrentInstance,
  inject,
  nextTick,
  onMounted, provide,
  ref,
  watch,
} from 'vue';
  import { Popover } from 'ant-design-vue';

  import { useModal } from '/@/components/Modal';
  import KuaiJiKeMuManager from './KuaiJiKeMuManager.vue';
  import PingzhengCodePicker from '/@/components/pingzheng-fillin/components/PingzhengCodePicker/index.vue';
  import { searchFilter } from '/@/components/pingzheng-fillin/layouts/table/columns/KuaiJiKeMu/funs/kuaiJiKeMuPopup';

const pageRootDom=inject('pageRootDom').value
  const emit = defineEmits(['close', 'register', 'change']);
  emit('register', getCurrentInstance());
  const vScroll = ref();

  const hello2222 = ref();

  const visible = ref(false);
  const openSelect = ref(false);

  const showFullData = ref(false);
  const props = defineProps(['items', 'searchStr']);
  const searchStr = computed(() => props.searchStr);
  const filterCodeList = ref([]);
  const codeList = computed(() => props.items);

  const selectRowIndex = ref(0);
  const searchFun = inject('searchFun');
  async function useCodeListFilter() {
    watch(searchStr, (e) => {
      filterCodeList.value = searchFilter(codeList, searchStr).map((it) => {
        return {
          ...it,
          label: it.key + ' ' + it.label,
        };
      });
      searchFun(filterCodeList);
      selectRowIndex.value = 0;
      showFullData.value = false;
      nextTick(() => {
        filterCodeList.value.forEach((it) => (it.enable = false));
        if (filterCodeList.value.length > 0) {
          filterCodeList.value[0].enable = true;
        }
      });
    });
    //
    // 显示所有
    watch(showFullData, () => {
      if (showFullData.value) {
        filterCodeList.value = codeList.value.map((it) => {
          return {
            ...it,
            label: it.key + ' ' + it.label,
          };
        });
        searchFun(filterCodeList);
      }
    });

    filterCodeList.value = searchFilter(codeList, searchStr).map((it) => {
      return {
        ...it,
        label: it.key + ' ' + it.label,
      };
    });

    searchFun(filterCodeList);
  }


  function openAaaa(it2) {
    const it = JSON.parse(JSON.stringify(it2.target));
    it.fuzhu = '';
    openEditPage(true, {
      data: {
        data: it,
        independent: true,
        iyear: '2021',
        jici: '4-2-2-2-2-2-2-2-2-2-2-2-2-2-2-2',
        standardUnique: '2',
        styleName: [
          {
            cclass: '资产',
            flagYusuan: null,
          },
          {
            cclass: '负债',
            flagYusuan: null,
          },
          {
            cclass: '共同',
            flagYusuan: null,
          },
          {
            cclass: '权益',
            flagYusuan: null,
          },
          {
            cclass: '成本',
            flagYusuan: null,
          },
          {
            cclass: '损益',
            flagYusuan: null,
          },
        ],
        templateID: '2',
      },
    });
  }

  // const setKuaiJiKeMuValue=inject('setKuaiJiKeMuValue')

  onMounted(async () => {
    useCodeListFilter().then();
  });

  function change(key) {
    emit('change', key);
  }
  function cancelAllEnable() {
    filterCodeList.value.forEach((item) => (item.enable = false));
  }

  function getLastItemIndex() {
    return filterCodeList.value.length - 1;
  }

  function getPingzhengListRef() {
    return vScroll.value.vScroll.$el;
  }

  function setScrollLast() {
    getPingzhengListRef().scrollTop = getPingzhengListRef().scrollHeight;
  }
  function subScroll(length) {
    getPingzhengListRef().scrollTop = getPingzhengListRef().scrollTop - length;
  }

  function addScroll(length) {
    getPingzhengListRef().scrollTop = getPingzhengListRef().scrollTop + length;
  }

  function enableItem(index) {
    filterCodeList.value[index].enable = true;
  }
  function getEnableItem() {
    return filterCodeList.value.filter((item) => item.enable)[0];
  }

  function waitCodeListGt0() {
    return new Promise((r) => {
      const inter = setInterval(() => {
        if (codeList.value.length > 0) {
          r();
          window.clearInterval(inter);
        }
      }, 10);
      // {r,j}
    });
  }

  defineExpose({
    async focus() {
      if (openSelect.value) {
        return;
      }
      showFullData.value = false;

      await waitCodeListGt0();
      codeList.value[0].enable = true;

      nextTick(() => (openSelect.value = true));
    },
    keyupUp() {
      cancelAllEnable();

      if (filterCodeList.value <= 1) {
        return;
      }
      if (selectRowIndex.value == 0) {
        selectRowIndex.value = getLastItemIndex();
        setScrollLast();
      } else {
        getPingzhengListRef().scrollTop = selectRowIndex.value * 27 - 27 - 27;
        // subScroll(27);
      }

      enableItem(selectRowIndex.value--);

    },
    keyupDown() {
      cancelAllEnable();
      if (filterCodeList.value <= 1) {
        return;
      }
      if (selectRowIndex.value == filterCodeList.value.length) {
        selectRowIndex.value = 0;
        getPingzhengListRef().scrollTop = 0;
      }
      enableItem(selectRowIndex.value++);

      getPingzhengListRef().scrollTop = selectRowIndex.value * 27 - 27;
      // selectRowIndex.value > 11 && addScroll(27);

    },
    keyupEnter() {
      change(getEnableItem().key);
    },
    openSelect,
    isOpenManager: visible,
    closeManager() {},
  });

  const [registerEditPage, { openModal: openEditPage }] = useModal();
</script>
<style>
  .kuaiJiKeMuSelect {
    width: auto !important;
    color: black;
    border-radius: 10px;
    box-shadow: 0 0 6px black;
  }

  .kuaiJiKeMuSelect2 {
    display: none;
    width: auto !important;
    color: black;
    border-radius: 10px;
    box-shadow: 0 0 6px black;
  }

  .asdasd th,
  .asdasd td {
    border: solid 1px black;
    padding: 0 !important;
  }
</style>
<style scoped>
  :deep(.abc .ant-select-selector) {
    height: 100% !important;
  }

  td {
    border: solid 1px #828282;
    font-weight: 900;
  }
</style>
<style>
  .hoverRow {
    background: #00579124 !important;
    color: #005791 !important;
  }

  .enableRow {
    background: #00579124;
    color: #005791 !important;
  }

  .kuaiJiKeMuPopupContainer {
    position: fixed;
    left: 0px;
    top: 0px;
    width: 100%;
    height: 100%;
    z-index: 100000000;
    background: #0000004d;
  }

  .code-item {
    margin: 0;
    padding: 0;
    width: 100%;
    display: flex;
  }

  .code-item li {
    font-family: 'Microsoft YaHei';
    font-weight: 300;
    /*border: solid 1px black;*/
  }

  .code-item1 {
    width: 80px;
    font-weight: 900;
    padding-left: 6px !important;
    padding-top: 4px;
    text-align: left;
  }

  .code-item2 {
    width: 180px;
    font-weight: 900;
    padding-top: 5px;
  }

  .code-item3 {
    width: 170px;
    text-align: left;
    padding-left: 6px !important;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .code-item-title {
    font-weight: 900;
    text-align: center;
    background: #d2cfcf;
  }
</style>
<style>
  .pingzhengPopover {
    padding-top: 0 !important;
    z-index:1
  }

  .pingzhengPopover .ant-popover-arrow {
    display: none !important;
  }
  .selectRef2 {
    width: 400px; height: 320px; padding: 4px 0px; cursor: pointer
  }
</style>
