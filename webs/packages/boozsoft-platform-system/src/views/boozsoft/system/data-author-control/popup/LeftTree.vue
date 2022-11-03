<template>
  <div class="bg-white m-4 mr-0 overflow-hidden" :style="{height: windowHeight+'px'}">
    <div>
      <label>控制模块范围</label>
    </div>
    <div>
      <BasicTree
        title=""
        :click-row-to-expand="false"
        :tree-data="treeData"
        :replace-fields="{ key: 'key', title: 'name' }"
        :default-expand-all="true"
        v-model:selectedKeys:="selectedKeys"
        @select="handleSelect"
      />
    </div>
  </div>
</template>
<script setup lang="ts">
import {inject, onMounted, ref, watch} from 'vue';
import {BasicTree} from '/@/components/Tree';
import {Tabs,Tree,Checkbox} from 'ant-design-vue';
import {hasBlank} from "/@/api/task-api/tast-bus-api";

const TabPane = Tabs.TabPane;
const windowHeight = (window.innerHeight - 260)
const emit = defineEmits(['select'])
const activeKey = ref('2')
const treeData: any = ref([{key:'主数据',name:'主数据'},{key:'总账',name:'总账'},{key:'现金银行',name:'现金银行'},{key:'应收款',name:'应收款'},{key:'应付款',name:'应付款'},{key:'存货管理',name:'存货管理'},{key:'固定资产',name:'固定资产'}])
const treeData2: any = ref([])
const selectedKeys = ref<string[]>(['主数据'])
const treeList = inject('treeList'); //消费者
const treeList2 = inject('treeList2'); //消费者
const lastCheckList = inject('lastCheckList'); //消费者
onMounted(()=>handleSelect(selectedKeys.value))
function handleSelect(keys: string, e: any) {
  if (keys.length != 0){
    emit('select', keys[0])
  }
}
</script>
<style scoped lang="less">
.bg-white {
  width: 270px !important;
  min-height: 250px !important;
  border: 1px #cccccc solid;
  background: white !important;
  margin: 0%;
  margin-right: .2%;
  >div:nth-of-type(1){
    width: 100%;
    height: 25px;
    text-align: center;
    background-color: rgb(216, 216, 216);
    label{
      font-size: 14px;
      color: black;
      font-weight: bold;
      line-height: 25px;
      background-color: #d8d8d8;
    }
  }
  >div:nth-of-type(2){
    padding: 20px;
  }
}
</style>
