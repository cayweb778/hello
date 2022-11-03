<template>
  <LayoutView>
    <template #header>
      <Header/>
    </template>
    <MainView></MainView>
  </LayoutView>

</template>
<script setup>
import MainView from './views/MainView.vue'
import Header from './views/Header.vue'
import LayoutView from '../system/menu/layout/LayoutView.vue'
</script>
