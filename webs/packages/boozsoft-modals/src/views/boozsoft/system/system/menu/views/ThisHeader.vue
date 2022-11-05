<template>
  <div>
    <RadioGroup  v-model:value="radioValue" button-style="solid">
      <RadioButton value="a">角色权限</RadioButton>
      <RadioButton value="b">用户权限</RadioButton>
    </RadioGroup>
  </div>
</template>
<script setup>
import {ref} from 'vue'
import {Radio} from 'ant-design-vue'
const RadioGroup=Radio.Group
const RadioButton=Radio.Button
const radioValue=ref('a')
</script>
