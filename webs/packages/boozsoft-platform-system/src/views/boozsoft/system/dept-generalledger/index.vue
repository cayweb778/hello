<template>
  <Abcd></Abcd>
</template>
<script lang="ts">
import Abcd from './index2.vue'
import {defineComponent} from "vue";
export default defineComponent({
  name: '部门总账',
  components:{
    Abcd
  }
})
</script>
