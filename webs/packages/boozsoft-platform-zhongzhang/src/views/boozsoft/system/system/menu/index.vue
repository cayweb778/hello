<template>
  <LayoutView>
    <template #header>
      <RadioGroup  v-model:value="radioValue" button-style="solid">
        <RadioButton value="a">角色权限</RadioButton>
        <RadioButton value="b">用户权限</RadioButton>
      </RadioGroup>
    </template>
    <MainView></MainView>
  </LayoutView>
</template>
<script setup>
import LayoutView from "./layout/LayoutView.vue"
import Header from "./layout/header/index.vue"
import MainView from './views/MainView.vue'
import {ref} from 'vue'
import {Radio} from 'ant-design-vue'
const RadioGroup=Radio.Group
const RadioButton=Radio.Button
const radioValue=ref('a')
</script>
