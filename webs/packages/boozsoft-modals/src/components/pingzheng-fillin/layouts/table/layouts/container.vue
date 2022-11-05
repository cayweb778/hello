<template>
  <div class="pingzheng_table_container">
    <PingZhengContentTag v-model="props.typeLabel" />
    <div style="color:red;position: absolute;top:12px;left:200px;font-size:20px" v-if="props.typeLabel!='填制' && props.typeLabel!='查看'">[{{props.typeLabel}}]</div>
    <div class="pingzheng_table_content">
      <slot></slot>
    </div>
  </div>
</template>
<script setup>
  import PingZhengContentTag from '/@/components/pingzheng-fillin/components/PingZhengContentTag.vue';
  const props = defineProps(['typeLabel']);
</script>
