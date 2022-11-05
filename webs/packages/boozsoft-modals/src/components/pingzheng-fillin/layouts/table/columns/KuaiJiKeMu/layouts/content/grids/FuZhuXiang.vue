<template>
  <div class="kuaiJiKeMuGrid">
    <FuZhuXiang @register="fuZhuXiangState.register" />
  </div>
</template>
<script lang="ts" setup>
  import { inject, onMounted } from 'vue';
  import { useFuZhuXiang } from '../../pickers/FuZhuXiangPicker/funs/index';

  const pingzhengGridState = inject('pingzhengGridState');
  const pingzhengGridParams = inject('pingzhengGridParams');

  const fuZhuXiangState = useFuZhuXiang({
    change(e) {},
    closeFun() {
      pingzhengGridState.data.blur();
    },
  });

  const { FuZhuXiang } = fuZhuXiangState;

  onMounted(async () => {
    fuZhuXiangState.openPopover(pingzhengGridParams.columnDefine.rowData.fuZhuHeSuan);
  });
</script>
<style scoped>
  .kuaiJiKeMuInput {
    text-align: left;
    position: absolute;
    z-index: 1000000;
    left: 0;
    top: 0;
  }

  .kuaiJiKeMuGrid {
    height: 100%;
    position: relative;
  }
</style>
