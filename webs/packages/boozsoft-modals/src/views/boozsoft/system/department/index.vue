<template>
  <MainView></MainView>
</template>
<script lang="ts">
import MainView from './index2.vue'
import {defineComponent} from "vue";

export default defineComponent({
  name: 'Department',
  components: {
    MainView
  },
  mounted() {
  }
})

</script>
