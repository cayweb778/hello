<template>
  <Button v-for="it in buttonFuns" class="hello-btn" style="margin-right: -2px;font-size:15px" @click="it.fun">
    <span style="    color: rgb(0, 150, 199);font-weight:400;margin-right:5px">{{it.label}}</span>
  </Button>
</template>
<script setup>
import {computed} from 'vue'
import {Button
} from 'ant-design-vue'
const props=defineProps(['buttonFuns'])
const aaa=computed(()=>props.buttonFuns)
</script>
