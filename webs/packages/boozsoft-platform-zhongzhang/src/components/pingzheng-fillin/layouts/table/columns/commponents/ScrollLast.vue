<template>
  <div  @scroll="abc">
    <slot></slot>
  </div>
</template>
<script setup>

const emit=defineEmits(['last'])
function isLast(e){
  return e.srcElement.scrollTop+e.srcElement.offsetHeight==e.srcElement.scrollHeight
}
function abc(e){
  isLast(e) && emit('last')
}

</script>
