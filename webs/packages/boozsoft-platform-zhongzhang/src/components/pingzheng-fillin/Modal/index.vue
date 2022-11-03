<template>
  <template v-if="openThis">
    <ModalLayout v-if="type=='modal'">
      <div class="pingZhengPageContainer" style="top:25px">
        <MainView></MainView>
      </div>
    </ModalLayout>
    <PageLayout v-if="type=='page'">
      <div class="pingZhengPageContainer"
           :style="{height:thisHeight+'px'}">
        <MainView></MainView>
      </div>
    </PageLayout>

  </template>

</template>
<script setup lang="ts">
import ModalLayout from './components/Modal/layout/ModalLayout.vue'
import PageLayout from './components/Modal/layout/PageLayout.vue'

import MainView from './layouts/index.vue'
import {defineAsyncComponent, getCurrentInstance, ref, provide} from "vue";
import {usePingZhengFillinStoreWidthOut} from "/@/store/modules/boozsoft/pingzheng-fillin";

const emit = defineEmits(['register'])
const thisHeight = window.innerHeight - 110
const openThis = ref(false)
const props = defineProps(['page'])
const type = ref('page')

function openPingzhengModal(apiData, params) {
  const {settings} = params
  const {title} = settings
  usePingZhengFillinStoreWidthOut().getApiDataModels[0].apiData = apiData
  usePingZhengFillinStoreWidthOut().getApiDataModels[0].title = title
  usePingZhengFillinStoreWidthOut().getApiDataModels[0].settings = {enableModelSave: true}
  type.value = 'modal'
  openThis.value = true
}


function closePingzhengModal(apiData, params) {
  openThis.value = false
}

emit('register', getCurrentInstance());
if (props.page == '') {
  openThis.value = true
}

provide('closePingzhengModal', closePingzhengModal)
defineExpose({openPingzhengModal})
</script>
<style>
.pingZhengPageContainer {
  border-radius: 4px;
  min-width: 1010px;
  width: calc(100% - 20px);
  height: calc(100% - 30px);
  overflow: hidden;
}
</style>
