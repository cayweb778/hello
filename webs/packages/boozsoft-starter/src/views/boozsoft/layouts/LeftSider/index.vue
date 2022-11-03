<template>
    <SiderButton>
      <SideBar
          @changeApp="changeApp"
          @changeNcDashBoard="changeNcDashBoard"
          @close="showOpenButton"
      ></SideBar>
    </SiderButton>

</template>
<script setup lang="ts">
import SiderButton from './sitebar/openbutton.vue'
import SideBar from './sitebar/index.vue'

function changeApp() {

}

function changeNcDashBoard() {

}

function showOpenButton() {

}
</script>
<style>

</style>
