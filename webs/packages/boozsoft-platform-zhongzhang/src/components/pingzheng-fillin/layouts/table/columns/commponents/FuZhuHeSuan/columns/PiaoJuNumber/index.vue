<template>
  <input
    v-model="value"
    @input="inputInput"
    @keydown.left="emit('left')"
    @keydown.right="emit('right')"
    style="border-radius: 4px; border: solid 1px #d1d1d1; height: 100%; width: 90.5%"
    @change="emit('right')"
  />
  <ProfileOutlined style="margin-left: 10px; color: gray; font-size: 18px" />
</template>
<script setup>
  import { nextTick, ref } from 'vue';
  import { ProfileOutlined } from '@ant-design/icons-vue';
  const props = defineProps(['modelValue']);
  const emit = defineEmits(['left', 'right', 'update:modelValue']);
  const value = ref(props.modelValue);

  function inputInput(e) {
    nextTick(() => {
      value.value = e.target.value.replace(/[^0-9]/gi, '').slice(0, 10);
      emit('update:modelValue', value.value);
    });
  }
</script>
