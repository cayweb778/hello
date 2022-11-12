<template>
  <div style="postion:fixed;left:calc((100vw - 440px )/ 2);top:calc((100vh - 338px )/ 2 - 2evh);">
    <Empty
        image="https://gw.alipayobjects.com/mdn/miniapp_social/afts/img/A*pevERLJC9v0AAAAAAAAAAABjAQAAAQ/original"
        :image-style="{
      height: '260px',
    }"
    >
      <template #description>
      <span>
        模块未加载，请联系管理员
        <a href="#API">详情</a>
      </span>
      </template>
      <Button type="primary">重试</Button>
    </Empty>
    </div>
</template>
<script setup>
import {Empty,Button} from 'ant-design-vue'
</script>