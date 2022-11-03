<template>
  <div ref="thisSelectRef" class="thisSelectRef" style="height: 40px">
    <ItemListPicker
      ref="inputRef"
      v-model="showText"
      :items="propsList"
      :visible="showOpenSelect"
      :inputEvents="inputEvents"
      @change="selectBlur"
    />
    <DeptSelect @save="saveSelectDept" @register="registerSelectDeptPage" />
    <ProfileOutlined @click="clickRecord" style="margin-left: 10px; color: gray; font-size: 18px" />
  </div>
</template>
<script setup>
  import { ref, computed, watch, onMounted, inject } from 'vue';
  import pinyin from 'js-pinyin';

  import { ProfileOutlined } from '@ant-design/icons-vue';
  import DeptSelect from '/@/views/boozsoft/system/department/popup/select-dept.vue';
  import { useModal } from '../../../../../../../../Modal';
  import ItemListPicker from './ItemListPicker.vue';
  const emit = defineEmits(['change', 'focus', 'left', 'right']);
  const props = defineProps(['list', 'modelValue', 'columns']);
  const thisSelectRef = ref();
  const showOpenSelect = ref(false);
  const propsList = computed(() => props.list);

  const [registerSelectDeptPage, { openModal: openSelectDeptPage }] = useModal();
  const datasourcePicker = inject('datasourcePicker');
  function saveSelectDept(e) {
    selectBlur(e.deptName);
    console.log(e);
  }
  function clickRecord() {
    openSelectDeptPage(true, {
      dynamicTenantId: datasourcePicker.value.params.accountMode,
    });
  }

  const showText = ref('');
  watch(showText, () => {
    const arr = propsList.value.filter((item) => item.label == showText.value);
    if (arr.length > 0) {
      props.modelValue.value = arr[0].key;
    }
  });

  function selectBlur(e) {
    showText.value = e;
    emit('right');
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

  const filterList = ref();
  const inputEvents = {
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
