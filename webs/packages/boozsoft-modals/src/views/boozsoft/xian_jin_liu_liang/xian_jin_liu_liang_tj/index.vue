<template>
  <Abcd></Abcd>
</template>
<script lang="ts">
import Abcd from './index2.vue'
import {defineComponent} from "vue";
export default defineComponent({
  name: '现金流量统计表',
  components:{
    Abcd
  }
})
</script>
