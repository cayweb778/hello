<template>
  <div
    v-show="props.visible"
    style="position: absolute; right: 0; top: 8px; width: 20px; height: 20px"
  >
    <Tooltip placement="bottom">
      <template #title>
        <div>
          <div v-html="props.value"></div>
        </div>
      </template>
      <MoreOutlined />
    </Tooltip>
  </div>
</template>
<script setup>
  import { Tooltip } from 'ant-design-vue';
  const props = defineProps(['value', 'visible']);
  import { MoreOutlined } from '@ant-design/icons-vue';
</script>
