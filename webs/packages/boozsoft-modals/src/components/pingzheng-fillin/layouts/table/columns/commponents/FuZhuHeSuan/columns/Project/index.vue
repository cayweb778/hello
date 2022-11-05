<template>
  <div ref="thisSelectRef" style="height: 40px">
    <Popover
      :get-popup-container="() => aaa"
      placement="bottom"
      overlayClassName="pingzhengPopover"
      :visible="showOpenSelect"
      trigger="context-menu"
    >
      <template #content>
        <div style="width: 185px">
          <ProjectPicker :items="filterList" v-model="showText" @change="selectBlur($event)" />
        </div>
      </template>
      <input v-model="showText" class="fuZhuHeSuanInputClass" ref="inputRef" v-on="inputEvents" />
      <ProfileOutlined @click="openA" style="margin-left: 10px; color: gray; font-size: 18px" />
    </Popover>

    <ProjectSelect @save="saveSelectProject" @register="registerSelectProjectPage" />
  </div>
</template>
<script setup>
  import { ref, computed, watch, onMounted, inject } from 'vue';
  import { Popover } from 'ant-design-vue';
  import { ProfileOutlined } from '@ant-design/icons-vue';
  import ProjectSelect from '/@/views/boozsoft/system/project/popup/select-project.vue';
  import ProjectPicker from './Picker/index.vue';
  import { useModal } from '../../../../../../../../Modal';
  const emit = defineEmits(['change', 'focus', 'left', 'right']);
  const props = defineProps(['list', 'modelValue', 'columns']);
  const thisSelectRef = ref();
  const showOpenSelect = ref(false);
  const propsList = computed(() => props.list);
  const [registerSelectProjectPage, { openModal: openSelectProjectPage }] = useModal();
  const aaa = document.body;

  const showText = ref('');
  watch(showText, () => {
    const arr = propsList.value.filter((item) => item.label == showText.value);
    if (arr.length > 0) {
      props.modelValue.value = arr[0].key;
    }
  });

  function saveSelectProject(e) {
    showText.value = e.projectName;
  }
  const datasourcePicker = inject('datasourcePicker');
  function openA() {
    openSelectProjectPage(true, {
      dynamicTenantId: datasourcePicker.value.params.accountMode,
      projectClassCtl: getFzItemClass().from.value.value,
    });
  }

  function selectBlur(e) {
    showText.value = e;
  }

  function inputBlur() {
    setTimeout(() => {
      showOpenSelect.value = false;
    }, 200);
  }

  const inputRef = ref();
  defineExpose({
    focus() {
      showOpenSelect.value = true;
      inputRef.value.focus();
    },
  });

  function helloLeft() {
    emit('left');
  }
  function filterListByClass(classCode) {
    return propsList.value.filter((it) => it.target.itemCode == classCode);
  }

  function getFzItemClass() {
    return props.columns.filter((it) => it.key == 'fzItemClass')[0];
  }

  const filterList = ref();
  const inputEvents = {
    focus() {

      filterList.value = filterListByClass(getFzItemClass().from.value.value);
    },
    blur() {
      inputBlur();
    },
    click() {
      showOpenSelect.value = true;
    },
    keydown(e) {
      switch (e.key) {
        case 'ArrowLeft':
          helloLeft();
          e.preventDefault();
          break;
        case 'ArrowRight':
          emit('right');
          e.preventDefault();
          break;
        case ' ':
          var e = window.event || event;
          if (e.preventDefault) {
            e.preventDefault();
          } else {
            window.event.returnValue = false;
          }
          showOpenSelect.value = !showOpenSelect.value;
          e.preventDefault();
          break;
      }
    },
  };
  onMounted(() => {
    const arr = props.list.filter((item) => item.key == props.modelValue.value);
    if (arr.length > 0) {
      showText.value = arr[0].label;
    }
  });
</script>
<style>
  .kuaiJiKeMuSelect {
    width: auto !important;
    color: black;
    width: 200px;
    border-radius: 10px;
    box-shadow: 0 0 6px black;
  }

  .fuZhuHeSuanInputRef {
    position: absolute;
    z-index: 1000000;
    left: 0;
    width: 100%;
    height: 40px;
  }
</style>
