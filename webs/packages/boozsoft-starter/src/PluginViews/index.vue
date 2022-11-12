<template>
  <Layout>
    <div style="display:flex">
      <div>
        <LeftSider/>
      </div>
      <div>
        <Content/>
      </div>
    </div>
  </Layout>

</template>
<script setup>
import Layout from './layout/index.vue'
import LeftSider from './LeftSider.vue'
import Content from './content/index.vue'
</script>