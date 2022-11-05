<template>
  <div style="position:fixed;background:gray;top:0;left:0;width:100%;height:100%;z-index:5">
    <div style="display: flex">
      <div style="width:400px;min-width:400px">
        <LeftSider @selected="hello"></LeftSider>
      </div>
      <div style="width:calc(100% - 400px);padding:20px">
        <Content @register="register"></Content>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import LeftSider from './layouts/LeftSider/index.vue'
import Content from './layouts/content/index.vue'
import {onMounted, provide} from "vue";

function useContent(){
  let reloadContentFun=null
  return {
    register(fun){
      reloadContentFun=fun.reloadContentFun
    },
    reloadContent(tenantId,platformId){
      reloadContentFun(tenantId,platformId)
    }
  }
}
const {register,reloadContent}=useContent()
provide('viewModels',{
  reloadContent
})



function hello(e){
  reloadContent('bjxgkj-001-2021',e)
}
onMounted(()=>{
  reloadContent('bjxgkj-001-2021',1)
})
</script>
