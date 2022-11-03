<template>
    <slot></slot>
</template>
<script setup>
import {provide,ref} from "vue";
import {createPinia} from "pinia";



provide('rowFuZhuHeSuanStore',createPinia())
</script>
\