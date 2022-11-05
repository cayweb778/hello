<template>
  <div style="    width: calc(100% - 20px);margin-top: 0px">
    <div class="collapse-container" style="margin-left: 13px; margin-bottom: 10px"
    >
      <span class="title" style="color:white">应用</span>
      <span class="icon__YImk icon dls-icon icon-chevron-up show-on-hover"
      data-role="icon"
      color="gray"
      type="subtle"
      style="font-size: 14px"></span>
    </div>
    <AppBox>
      <template v-for="it in appPlatfroms">
        <div style="position: relative">
          <Item
            :selected="it.selected"
            v-if="abc2(it)"
            @click="clickItem(it)"
            :isCloud="it.isCloud"
            :icon="it.icon"
            :isOutLink="it.isOutLink"
            :isTargetBlank="it.isTargetBlank"
          >
            {{ it.name }}
            <div v-if="it.name=='薪酬管理' ||it.name=='网上报销'|| it.name=='财税达'"
                 style="position: absolute;top:10px;right:10px">
<!--              <ReloadOutlined @click="layoutsStore.reloadIframe(it.name)"/>-->
            </div>

          </Item>
        </div>
      </template>
    </AppBox>
  </div>
</template>
<script setup lang="ts">
import AppBox from '../../components/appbox/index.vue';
import Item from '../../components/appbox/item.vue';

import {ref} from "vue";
import {
  ProjectOutlined,
  CloudServerOutlined,
  PayCircleOutlined,
  DesktopOutlined,
  RedEnvelopeOutlined,
  RotateRightOutlined,
  AppstoreOutlined,
  ClusterOutlined,
  UserAddOutlined,
  UnlockOutlined,
  DeleteOutlined,
  SnippetsOutlined,
  DatabaseOutlined,
  CloudUploadOutlined,
  CloudDownloadOutlined
} from '@ant-design/icons-vue';

const icons=[
  ProjectOutlined,
  CloudServerOutlined,
  PayCircleOutlined,
  DesktopOutlined,
  RedEnvelopeOutlined,
  RotateRightOutlined,
  AppstoreOutlined,
  ClusterOutlined,
  UserAddOutlined,
  UnlockOutlined,
  DeleteOutlined,
  SnippetsOutlined,
  DatabaseOutlined,
  CloudUploadOutlined,
  CloudDownloadOutlined
]


const appPlatfroms = ref([
  {id:1001,name:'总账'},
  {id:1006,name:'现金银行'},
  {id:20001,name:'固定资产'},
  {id:1099,icon:'project-outlined',name:'存货管理'},
  {id:1007,name:'应收账'},
  {id:20017,name:'应付账'},
  {id:10131200,name:'报表中心'},
  {id:1,name:'薪酬管理'},
  {id:1,name:'网上报销'},
  // {id:1,name:'电子票据'},
  // {id:1013,icon:'ProjectOutlined',name:'开发模块库'},
])
appPlatfroms.value.forEach((it,i)=>{
  it.icon =icons[i]
})

function abc2(item : any) {
  let a = true
  if (item.name.indexOf('主数据') != -1 || item.name.indexOf('机构') != -1 || item.name.indexOf('客户') != -1 || item.name.indexOf('供应商') != -1) {
    a = false
  }
  return a
}
function abc() {
  // useMultipleTabWithOutStore().closeAllTab(router);
  // router.push('/zhongZhang/home/welcome');

  // setTimeout(()=>{
  // },1000)
}
import WujieVue from "wujie-vue3";
const {bus}=WujieVue
function abdc(){
  bus.$emit("goPlatform",'11112321');
}

import {goZongZhang,goStock,goSystem,goGuDingZiChan, goPlatformById} from "@/views/boozsoft/layouts/LeftSider/sitebar/layouts/SiderGroup/funs";
import router from "@/router";
import {useCounterStore} from "@/stores/counter";

function toGoPlatformById(id : any){
  useCounterStore().setShowFooter(true)
  if(id==1006 ||id==1001 || id==10131200){
    goZongZhang(id)
  }else if(id==20001){
    goGuDingZiChan(id)
  }else if(id==1099 || id==20017 || id==1007){
    // router.push('/nc/gdzc')
    setTimeout(()=>{
      goStock(id)
    },1000)
  }else{
    goSystem(id)
  }
}
function clickItem(it : any){
  toGoPlatformById(it.id)
  // abc()
  if (it.name.indexOf('应付账') != -1) {
  // layoutsStore.switchPlatform({ id: appPlatfroms.value.filter(it=>it.name=='应收账')[0].id })
  }else{
  // layoutsStore.switchPlatform({ id: it.id })
  }
}
</script>
