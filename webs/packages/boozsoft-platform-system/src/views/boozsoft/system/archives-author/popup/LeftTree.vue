<template>
  <div class="bg-white m-4 mr-0 overflow-hidden">
    <Tabs type="card" size="small" :tabBarGutter="0" v-model:activeKey="activeKey" @change="tabsChange">
      <TabPane key="2" :style="activeKey=='2'?{height: (windowHeight)+'px',overflowY: 'auto'}:{}">
        <template #tab>&emsp;&emsp;&emsp;&ensp;集团账套&ensp;&emsp;&emsp;&emsp;</template>
<!--        <BasicTree
          :click-row-to-expand="false"
          :tree-data="treeData"
          :replace-fields="{ key: 'key', title: 'name' }"
          v-model:selectedKeys:="selectedKeys"
          @select="handleSelect"
        >
          <template #title="{ name, key }">
            <span  style="color: #1890ff">{{ name }}dsss</span>
          </template>
        </BasicTree>-->
        <ul class="imitate-tree">
          <li class="head"><span>名称</span><span>主管</span></li>
          <li class="body" v-for="item in treeData" @click="handleSelect([item.key])" :style="item.key == highlight?{/*backgroundColor: '#0098d0',*/color: '#0098d0', fontWeight: 'bold'}:{}">
            <span>{{item.name}}</span>
            <span><Checkbox  v-model:checked="item.supervisor" @change="(o)=>statusChange(o.target.checked?'1':'0',item.key)"/></span>
          </li>
        </ul>
      </TabPane>
      <TabPane key="1" :style="activeKey=='1'?{height: (windowHeight)+'px',overflowY: 'auto'}:{}">
        <template #tab>&emsp;&emsp;&emsp;&ensp;集团组织&ensp;&emsp;&emsp;&emsp;</template>
<!--        <BasicTree
          :click-row-to-expand="false"
          :tree-data="treeData2"
          :replace-fields="{ key: 'key', title: 'name' }"
          v-model:selectedKeys:="selectedKeys"
          @select="handleSelect">
        </BasicTree>-->
        <ul class="imitate-tree">
          <li class="head"><span>名称</span><span>主管</span></li>
          <li class="body" v-for="item in treeData2"  :style="item.key == highlight?{/*backgroundColor: '#0098d0',*/color: '#0098d0', fontWeight: 'bold'}:{}">
            <span @click="handleSelect([item.key])">{{item.name}}</span>
            <span><Checkbox v-model:checked="item.supervisor" @change="(o)=>statusChange(o.target.checked?'1':'0',item.key)"/></span>
          </li>
        </ul>
<!--        <Tree
          v-model:selectedKeys="selectedKeys"
          :replace-fields="{ key: 'key', title: 'name' }"
          :tree-data="treeData2"
          :field-names="fieldNames"
          @select="handleSelect"
        >
          <template #title="{ name, key}">
            <span  style="color: #1890ff">{{ name }}</span>
            <Checkbox @change="statusChange" style="float: right;"/>
          </template>
        </Tree>-->
      </TabPane>
    </Tabs>
  </div>
</template>
<script setup lang="ts">
import {inject, onMounted, ref, watch} from 'vue';
import {BasicTree} from '/@/components/Tree';
import {Tabs,Tree,Checkbox} from 'ant-design-vue';
import {hasBlank} from "/@/api/task-api/tast-bus-api";

const TabPane = Tabs.TabPane;
const windowHeight = (window.innerHeight - 225)
const emit = defineEmits(['select'])
const activeKey = ref('2')
const treeData: any = ref([])
const treeData2: any = ref([])
const selectedKeys = ref<string[]>([])
const treeList = inject('treeList'); //消费者
const treeList2 = inject('treeList2'); //消费者
const lastCheckList = inject('lastCheckList'); //消费者

const highlight = ref('')
function handleSelect(keys: string, e: any) {
  if (keys.length != 0){
    highlight.value = keys[0]
    emit('select', {obj: (activeKey.value == '1'?treeData2.value:treeData.value).filter(it=>it.key == highlight.value)[0], type: activeKey.value})
  }
}
watch(() => lastCheckList.value, async (newValue, oldValue) => {
  if (newValue.length > 0 && null != newValue[0]){
    treeData.value = treeList.value
    treeData2.value = treeList2.value
    // selectedKeys.value = [newValue[0]]
    handleSelect(newValue)
  }
});

/*onMounted(async () => {
  if (treeData.value.length > 0 && hasBlank( highlight.value)) handleSelect([treeData.value[0].key], null)
})*/

const tabsChange = (k) => {
  if (k == '2'){
    if (treeData.value.length > 0) handleSelect([treeData.value[0].key], null)
  }else {
    if (treeData2.value.length > 0) handleSelect([treeData2.value[0].key], null)
  }
}
const statusChange = (isS,key) => {
  handleSelect([key])
}
</script>
<style scoped lang="less">
.bg-white {
  width: 398px !important;
  min-height: 250px !important;
  border: 1px #cccccc solid;
  background: white !important;
  margin: 0%;
  margin-right: .2%;
  .imitate-tree{
    width: 100%;
    li{
      padding: 4px 0;
    }
    span{
      display: inline-block;
    }
    span:nth-of-type(1){
      width: calc( 100% - 90px );
    }
    span:nth-of-type(2){
      width: 90px;
      text-align: center;
    }
    .head{
      border-bottom: 1px solid black;
      span{
        text-align: center;
        font-weight: bold;
      }
    }
    .body{
      cursor: pointer;

      span:nth-of-type(1){
        padding-left: 2em;
      }
    }
  }
  :deep(.ant-tabs-tab-btn){
    //padding: 0 3em;
  }
}
</style>
