<template>
  <Popover placement="bottom">
    <template #content>
      <div v-if="pageStore != null" style="height: 200px; overflow: scroll; width: 100px">
        <div v-for="it in inoIdInComputed" @click="goPage(it)">{{ it }}</div>
      </div>
    </template>
    <InputSearch
      v-on="inputEvents"
      placeholder="通过凭证号查找"
      style="width: 155px; height: 20px; padding-top: 0px"
    />
  </Popover>
</template>
<script setup>
  import { inject, ref, computed } from 'vue';
  import { Input, DatePicker, Select, Popover, Button, message } from 'ant-design-vue';
  const InputSearch = Input.Search;

  const viewModel = inject('viewModel');

  const pingzhengData = viewModel.value.instances[0].params.pingzhengData;
  const pageStore = ref();
  const showText = ref();

  const inoIdInComputed = computed(() => pageStore.value.inoIdIn);

  const getPageData = inject('getPageData');

  function goPage(it){
    getPageData().value.goShowData(it)
  }

  function searchData(e) {
    getPageData().value.goShowData(e)
  }
  const inputEvents = {
    click(e) {
      pageStore.value = pingzhengData.pageStore;
    },
    input(e) {},
    search(e){
      searchData(e)
    }
  };
</script>
