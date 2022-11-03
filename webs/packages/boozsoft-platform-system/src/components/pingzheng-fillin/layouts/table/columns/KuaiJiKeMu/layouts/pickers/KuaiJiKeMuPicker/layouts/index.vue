<template>
  <div style="height: 100%">
    <div @mousedown="setLockBlur">
      <slot name="icon"></slot>
    </div>
    <div @mousedown="setLockBlur">
      <slot name="record"></slot>
    </div>
    <TextArea ref="inputRef" class="kuaiJiKeMuInput" v-model:value="searchStr" v-on="inputEvents">
<!--      <template #suffix>-->
<!--        <SearchOutlined class="searchIco" />-->
<!--        &lt;!&ndash;      <SearchOutlined @click="inputEvents.search()" class="searchIco" />&ndash;&gt;-->
<!--      </template>-->
    </TextArea>

    <div v-if="startPopup">
      <Content
        ref="mainRef"
        :searchStr="searchStr"
        :items="props.options"
        @change="changeFun2"
        v-bind="$attrs"
      />
    </div>
  </div>
</template>
<script setup>
  import { getCurrentInstance, nextTick, ref, watch, computed, h, provide, inject } from 'vue';
  import { Input } from 'ant-design-vue';
  import Content from './content.vue';
  import { useUseKuaiJikeMuInputEvents } from '/@/components/pingzheng-fillin/layouts/table/columns/KuaiJiKeMu/layouts/pickers/KuaiJiKeMuPicker/layouts/funs';

  const emit = defineEmits(['change', 'close', 'register', 'update:open']);
  const props = defineProps(['modelValue', 'open', 'inputEvents', 'options']);

  const inputRef = ref();
  const mainRef = ref();

  const searchStr = ref(props.modelValue);
  const startPopup = ref(false);

  const isOpen = computed(() => props.open);
  const modelValue = computed(() => props.modelValue);
  const TextArea = Input.TextArea;

  const funs = {
    instance: getCurrentInstance(),
    changeFun: ref(),
    isOpen() {
      return startPopup.value;
    },
    triggerPopover() {
      startPopup.value ? this.closePopover() : this.openPopover();
    },
    openPopover() {
      startPopup.value = true;
      nextTick(() => mainRef.value.focus());
    },
    closePopover() {
      startPopup.value = false;
    },
    focus() {},
    mainRef,
  };
  const isFuZhuXiang = inject('isFuZhuXiang');
  const lockBlur = ref(false);

  function setLockBlur() {
    lockBlur.value = true;
  }
  provide('setLockBlur',setLockBlur)

  function changeFun2($event) {
    lockBlur.value = true;
    if (props.modelValue && props.modelValue.split(' ')[0] == $event) {
      isFuZhuXiang.value = true;
      return;
    }
    emit('change', $event);
    // funs.changeFun.value($event);
    funs.closePopover();
    emit('update:open', false);
    // emit('update:modelValue', $event);
  }

  watch(modelValue, (e) => (searchStr.value = e));

  watch(isOpen, (e) => {
    if (e) {
      funs.openPopover();
    } else {
      funs.closePopover();
    }
  });

  const { useKuaiJikeMuInputEvents } = useUseKuaiJikeMuInputEvents(lockBlur, mainRef, funs);

  const inputEvents = useKuaiJikeMuInputEvents(props.inputEvents);

  function searchFun(e) {
    if (e.value.length == 0) {
      if (searchStr.value == null) {
        searchStr.value = '';
      }
      searchStr.value = searchStr.value.slice(0, searchStr.value.length - 1);
      // notification.open({
      //   key: 'noCode',
      //   message: '会计科目警告',
      //   description: '未检索到数据.',
      //   icon: h(WarningOutlined, { style: 'color: #e1c73b' }),
      // });
    }
  }
  provide('searchFun', searchFun);

  defineExpose({
    focus() {
      inputRef.value.focus();
    },
  });
</script>
<style>
  .kuaiJiKeMuInput {
    text-align: left;
    font-weight: 900;
    padding: 0;
    position: absolute;
    z-index: 1000000;
    height: 100% !important;
    left: 0;
    top: 0;
  }
</style>
