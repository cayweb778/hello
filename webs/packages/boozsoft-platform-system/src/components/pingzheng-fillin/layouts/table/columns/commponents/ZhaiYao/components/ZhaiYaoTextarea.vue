<template>
  <div ref="inputRef">
    <TextArea
      ref="inputTextareaRef"
      class="inputRef kuaiJiKeMuInput zhaiYaoTextarea"
      style="word-break: break-word; font-weight: 900; padding: 0"
      v-model:value="inputText"
      @blur="helloBlur()"
      v-on="props.inputEvents"
    >
      <!--    <template #suffix>-->
      <!--      <SearchOutlined @click="inputEvents.search()" class="searchIco" />-->
      <!--    </template>-->
    </TextArea>
  </div>
</template>
<script setup>
  import { computed, onMounted, ref, watch } from 'vue';
  import { Input } from 'ant-design-vue';
  const TextArea = Input.TextArea;
  const inputTextareaRef = ref();
  const inputText = ref('');
  const emit = defineEmits(['updated:value', 'blur2']);
  const props = defineProps(['value', 'inputEvents']);
  const propsValue = computed(() => props.value);
  watch(propsValue, () => {
    inputText.value = propsValue.value;
  });
  watch(inputText, (e) => {
    // console.log('TextArea改变')
    emit('changeValue', e);
  });
  const inputRef = ref();
  onMounted(() => {
    inputRef.value.querySelector('textarea').onkeydown = function (e) {
      if (e.keyCode == 13) {
        window.event.returnValue = false;
        e.preventDefault();
      }
      e.target.value = e.target.value.replace('\n', '');
    };
    // inputRef.value.querySelector('textarea').onkeydown(()=>{
    //   console.log(111)
    // })
  });

  function helloBlur() {
    console.log(465456)
    emit('blur2');
  }

  defineExpose({
    focus() {
      inputTextareaRef.value.focus();
    },
    select() {
      inputRef.value.querySelector('textarea').select();
    },
  });
</script>
