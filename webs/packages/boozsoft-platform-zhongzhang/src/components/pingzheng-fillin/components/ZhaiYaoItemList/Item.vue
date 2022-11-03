<template>
  <li class="code-item1" style="padding:0;width: 100%;position:relative">
    <div style="display: flex;height:28px">
      <div style="border:solid black 1px;width:70vw">        {{props.text.label}}</div>
      <div style="border:solid black 1px;width:30vw">{{props.text.target.classCode}}</div>
    </div>
    <Tooltip>
      <template #title>
        {{props.text.label}}
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
  const text = computed(() => labelSlice.getLabel(props.text.label));
  const isMore = computed(() => labelSlice.hasGtLength(props.text.label));
</script>
