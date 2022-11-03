<template>
  <div data-reactroot="" class="ant-layout">
    <Header></Header>

    <div class="ant-layout ant-layout-has-sider"
         style="background: rgb(255, 255, 255); height: 698px;">
      <LeftSider></LeftSider>
      <Content></Content>
      <PermissionPicker></PermissionPicker>
    </div>
    <Footer></Footer>
  </div>
</template>
<script setup>
import Header from './layouts/Header.vue'
import LeftSider from './layouts/LeftSider/index.vue'
import Content from './layouts/Content.vue'
import Sider2 from './layouts/Sider2.vue'
import Footer from './layouts/Footer.vue'
import PermissionPicker from './layouts/PermissionPicker/index.vue'


</script>
