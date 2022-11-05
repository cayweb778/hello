<template>
  <li class="code-item1" style="width: 100%;position:relative">{{ text }}
    <Tooltip>
      <template #title>
        {{props.text}}
      </template>
      <MoreOutlined style="position: absolute;right:5px;top:8px;font-weight:900" v-if="isMore" />
    </Tooltip>
  </li>
</template>
<script setup>
  import { computed } from 'vue';
  import { useLabelSlice } from './funs';
  import { MoreOutlined } from '@ant-design/icons-vue';

  const props = defineProps(['text']);

  import { Tooltip } from 'ant-design-vue';

  const labelSlice = useLabelSlice(34);
  const text = computed(() => labelSlice.getLabel(props.text));
  const isMore = computed(() => labelSlice.hasGtLength(props.text));
</script>
