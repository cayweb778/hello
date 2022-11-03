<template>
  <div>
    <div id="hello222222" ref="hello222" style="    position: absolute;
    left: 0px;
    top: 0px;
    width: 100%;
    height: 100%;
    z-index: 100000000;
    background: #0000004d;"></div>
    <Drawer
      v-model:visible="visible222"
      :get-container="hello222"
      placement="right" width="550" height="800" title="凭证列表"
      @close="isShow=false"
      :closable="false"
      :style="{ position: 'absolute' }"
    >

      <div>
        <div style="position:absolute;top:420px;right:10px">
          <img src="../assets/img_1.png"/>
        </div>
        <div v-for="item in pingzhengListData.rows"
             :style="{background:item.active?'#3798ffb3':''}">
          <div>
            日期：2020-12-12 凭证编号：记-001
            <span style="color:green">[{{ item.type }}]</span>
          </div>
          <div style="display:flex">
            <div
              style="width:100px;height:100px;border:solid 1px black;padding: 37px 0px 0px 28px;">
              {{ item.label }}
            </div>
            <div class="pingzheng-list-opeartor" style="margin-top:35px;margin-left:50px">
              <span
                @click="reloadPingZheng(toApiDataModel(item.data,{settings:{titleName:'查看凭证'}}))">[查看]</span>
              <span
                @click="reloadPingZheng(toApiDataModel(item.data,{settings:{titleName:'冲销凭证'}}))">[冲销]</span>
              <span
                @click="reloadPingZheng(toApiDataModel(item.data,{settings:{titleName:'复制凭证'}}))">[复制]</span>
              <span
                @click="reloadPingZheng(toApiDataModel(item.data,{settings:{titleName:'插入凭证'}}))">[插入]</span>
              <span @click="openChongXiao">[删除]</span>
            </div>

          </div>
          ------------------------------------------------------------<br>
        </div>
      </div>
    </Drawer>

  </div>
</template>
<script setup>
import {inject, onMounted, ref,computed} from 'vue';
import {Drawer} from 'ant-design-vue'
import {apiDataToShowModel, toEmptyModel} from '/@/components/pingzheng-fillin/hooks/models/datas/model';
import {pingzheng1} from '/@/components/pingzheng-fillin/hooks/models/datas/mock/morePingZheng/pingzheng1';
import {usePingZhengFillinStoreWidthOut} from '/@/store/modules/boozsoft/pingzheng-fillin';
import {creatApiRow} from "/@/components/pingzheng-fillin/hooks/models/apiData";
const props=defineProps(['show'])

const chongXiaoLocale = {
  add: '新增',
  save: '保存',
  zanChun: '暂存',
}
// const isShow = ref(false)
const isShow = computed(()=>props.show)
const visible222=ref(true)
const pingzhengListData = ref({
  options: {
    showChongxiao: false,
    showLook: false,
    showCopy: false,
    showInsert: false
  },
  rows: [
    {label: '凭证1', type: '辅助项、现金流量、数量单价、外币', data: pingzheng1},
    {label: '凭证2', type: '辅助项', active: true, data: pingzheng1},
    {label: '凭证3', type: '辅助项、现金流量、数量单价', data: pingzheng1},
    {label: '凭证4', type: '辅助项、现金流量、外币', data: pingzheng1},
    {label: '凭证5', type: '普通凭证', data: pingzheng1},
    {label: '凭证6', type: '普通凭证', data: pingzheng1},
    {label: '凭证7', type: '普通凭证', data: pingzheng1},
    {label: '凭证8', type: '普通凭证', data: pingzheng1},
    {label: '凭证9', type: '普通凭证', data: pingzheng1},
    {label: '凭证10', type: '普通凭证', data: pingzheng1},
    {label: '凭证11', type: '普通凭证', data: pingzheng1},
    {label: '凭证12', type: '普通凭证', data: pingzheng1},
    {label: '凭证13', type: '普通凭证', data: pingzheng1},
    {label: '凭证14', type: '普通凭证', data: pingzheng1},


  ]
})

function toApiDataModel(apiData, params) {

  const a= apiDataToShowModel(apiData, params)
  return a

}

function openChongXiao() {
  showChongxiao.value = true
}

const reloadPingZheng = inject('reloadPingZheng')
const hello222=ref()
function onDrawerLoaded(fun) {
  let i = 0
  const pingzhengDrawerInterval = setInterval(() => {
    if (i++ == 3000) {
      window.clearInterval(pingzhengDrawerInterval)
    }
    const pingzhengDrawer = document.getElementsByClassName('pingzhengDrawer')
    if (pingzhengDrawer != null) {
      window.clearInterval(pingzhengDrawerInterval)
      fun()
    }
  }, 1)
}

function resetPingZhengData(){
  const apiData=[]
  for(let i=0;i<25;i++){
    apiData.push(creatApiRow())
  }
  // console.log(JSON.stringify(toApiDataModel(apiData, {settings: {titleName: "记账凭证",typeLabel:'填制'}})))
  reloadPingZheng(toApiDataModel(apiData, {settings: {titleName: "记账凭证",typeLabel:'填制'}}))
}
onDrawerLoaded(() => {
  resetPingZhengData()
})


</script>
<style>
.pingzheng-list-opeartor span {
  color: steelblue;
  margin-right: 10px;
  cursor: pointer;
}
</style>
