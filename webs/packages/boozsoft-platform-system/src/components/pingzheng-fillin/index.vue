<template>
  <PageLayout>
    <PzDataProvide>
      <PzLayout/>
    </PzDataProvide>
  </PageLayout>

  <!--  <debugger-info />-->
</template>
<script setup lang="ts">
import PageLayout from './layouts/components/PageLayout.vue';
import PzDataProvide from './provide/index.vue';
import PzLayout from './layouts/index.vue';
// import {computed, provide} from "vue";

// const props = defineProps(['pingzhengData'])
// const pingzhengData = computed(() => props.pingzhengData)
// provide('pingzhengData', pingzhengData)
</script>
