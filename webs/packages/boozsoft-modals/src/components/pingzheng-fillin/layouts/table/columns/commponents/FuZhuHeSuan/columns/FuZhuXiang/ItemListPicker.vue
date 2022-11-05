<template>
  <Popover
    :get-popup-container="() => aaa"
    placement="bottom"
    overlayClassName="pingzhengPopover"
    :visible="props.visible"
    trigger="context-menu"
  >
    <template #content>
      <div style="width: 330px">
        <PingzhengItemList
          ref="vScroll"
          v-model:items="items"
          @change="emit('change', $event.label)"
        />
      </div>
    </template>
    <input v-model="inputText" class="fuZhuHeSuanInputClass" ref="inputRef" v-on="inputEvents" />
  </Popover>
</template>
<script setup>
  import { Popover } from 'ant-design-vue';
  import { ref, computed, watch, onMounted } from 'vue';
  import PingzhengItemList from '/@/components/pingzheng-fillin/components/PingzhengItemList/index.vue';
  import pinyin from 'js-pinyin';
  const inputRef = ref();
  const emit = defineEmits(['change', 'update:modelValue']);
  const props = defineProps(['visible', 'items', 'modelValue', 'inputEvents']);
  const aaa = document.body;

  const inputText = ref();

  const vScroll = ref();
  const propsModelValue = computed(() => props.modelValue);
  function getPingzhengListRef() {
    return vScroll.value.$el;
  }

  watch(propsModelValue, (e) => {
    inputText.value = e;
  });

  const propsItems = computed(() => props.items);
  watch(inputText, () => {
    console.log(2);
    emit('update:modelValue', inputText.value);
    items.value = propsItems.value
      .map((it) => ({ ...it, enable: false }))
      .filter((it) => {
        if (inputText.value == '') {
          return true;
        } else if (inputText.value == null) {
          return true;
        }
        return (
          contains(it.label, inputText.value) ||
          contains(it.key, inputText.value) ||
          pinyin.getCamelChars(it.label).indexOf(inputText.value.toUpperCase()) != -1
        );
      })
      .map((it) => {
        return {
          ...it,
          value: it.key,
          key: it.code,
        };
      });
    items.value[0].enable = true;
  });

  const contains = (str, str2) => str.indexOf(str2) !== -1;

  const items = ref([]);
  watch(
    propsItems,
    () => {
      items.value = propsItems.value
        .map((it) => ({ ...it, enable: false }))
        .filter((it) => {
          if (inputText.value == '') {
            return true;
          } else if (inputText.value == null) {
            return true;
          }
          return (
            contains(it.label, inputText.value) ||
            contains(it.key, inputText.value) ||
            pinyin.getCamelChars(it.label).indexOf(inputText.value.toUpperCase()) != -1
          );
        })
        .map((it) => {
          return {
            ...it,
            value: it.key,
            key: it.code,
          };
        });
      items.value[0].enable = true;
    },
    { immediate: true },
  );

  const modelValue = computed(() => props.modelValue);

  const start = ref(0);

  const inputEvents = {
    ...props.inputEvents,
    keydown(e) {
      switch (e.key) {
        case 'ArrowUp':
          console.log('up');
          items.value[start.value].enable = false;
          if (start.value == 0) {
            start.value = items.value.length;
            items.value[items.value.length - 1].enable = true;
          }
          items.value[--start.value].enable = true;
          getPingzhengListRef().scrollTop = start.value * 27 - 27 - 27;
          break;
        case 'ArrowDown':
          // console.log(items.value[++start.value]);
          items.value[start.value].enable = false;
          if (start.value == items.value.length - 1) {
            start.value = 0;
            items.value[0].enable = true;
          }
          items.value[++start.value].enable = true;
          getPingzhengListRef().scrollTop = start.value * 27 - 27;
          break;
        case 'Enter':
          function getCurrentItem() {
            return items.value[start.value];
          }
          emit('change', getCurrentItem().label);
          break;
        default:
          break;
      }
      props.inputEvents.keydown(e);
    },
  };

  watch(modelValue, (e) => {
    console.log(1);
    inputText.value = e;
  });
  defineExpose({
    focus() {
      inputRef.value.focus();
    },
  });
</script>
