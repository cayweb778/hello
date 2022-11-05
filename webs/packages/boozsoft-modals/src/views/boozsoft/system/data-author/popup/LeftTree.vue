<template>
  <div class="bg-white m-4 mr-0 overflow-hidden" :style="{height: windowHeight+'px'}">
    <div>
      <label >授权模块</label>
    </div>
    <Tree
      :click-row-to-expand="false"
      :tree-data="treeData"
      :replace-fields="{ key: 'key', title: 'name' }"
      v-model:expandedKeys="expandedKeys"
      v-model:selectedKeys:="selectedKeys"
      v-model:checkedKeys="checkedKeys"
      @select="handleSelect"
    >
      <template v-slot:title="{ name, key }">
        <span>{{ name }}</span>
        <Checkbox class="ant-tree-checkbox checkbox-me" v-show="checkReg.test(key)" @click="cancelB" v-model:checked="checkboxModel[key]" :disabled="selectedKeys.indexOf(key) == - 1"  @change="e=>checkChange(e,key)" />
      </template>
    </Tree>
<!--      -->
  </div>
</template>
<script setup lang="ts">
import {inject, onMounted, ref,watch} from 'vue';
import {Tree, Checkbox, message} from 'ant-design-vue';
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {saveLanMuList} from "/@/api/record/system/accvoucher";
import {useMessage} from "/@/hooks/web/useMessage";
const windowHeight = (window.innerHeight - 260)
const emit=defineEmits(['select','setting'])
const treeData:any = ref([])
const selectedKeys = ref<string[]>([])
const checkedKeys = ref<string[]>([])
const checkboxModel = ref({})
const expandedKeys = ref<string[]>([])
const treeList = inject('treeList'); //消费者
const lastCheckList = inject('lastCheckList'); //消费者
const theCheckList = inject('theCheckList'); //消费者

const checkReg = new RegExp("[A-Za-z]+")

function handleSelect(keys: string, e:any) {
  if (expandedKeys.value.indexOf(keys[0]) == -1){
    selectedKeys.value = keys
    emit('select', {keys:keys} )
  }
}

watch(() => lastCheckList.value,async (lastValue, oldValue) => {
  let newValue =  Object.keys(lastValue)
  if (newValue.length == 0){
    selectedKeys.value = []
    checkboxModel.value = {}
    handleSelect(theCheckList.value)
  }else {
    if (theCheckList.value?.length > 0 && theCheckList.value[0] != null){
      selectedKeys.value = theCheckList.value
    }else {
      selectedKeys.value = [newValue[0]]
    }
    newValue.forEach((k,v)=>checkboxModel.value[k] = (lastValue[k].length == 1 && lastValue[k][0].isDirector == '1'))
    handleSelect(selectedKeys.value)
  }
});
// 加载
watch(() => treeList.value,async (newValue, oldValue) => {
  treeData.value =  treeList.value
  expandedKeys.value = treeList.value.map(it=>it.name)
});

/*
* 阻止默认父级事件
* */
const cancelB = (e) => {
  if (e.stopPropagation){
    e.stopPropagation()
  }else {
    e.cancelBubble = true
  }
}
const {createConfirm} = useMessage();
const checkChange = (e,k) => {
  let b = checkboxModel.value[k]
  createConfirm({
    iconType: 'warning',
    title: '模块主管设置',
    content: `是否将该操作员${b?'设置':'取消'}模块主管权限?`,
    onOk:  () => {
      emit('setting', {key:k,action: b})
    },
    onCancel:  () => {
      checkboxModel.value[k] = !b
    }
  });
}
onMounted(async () => {
})
</script>
<style scoped lang="less">
.bg-white{
  width: 250px !important;
  min-height: 250px !important;
  border: 1px #cccccc solid;
  background:white !important;
  margin: 0%;
  margin-right: .2%;
  >div:nth-of-type(1){
    width: 100%;
    height: 25px;
    text-align: center;
    background-color: rgb(216, 216, 216);
    label{
      font-size: 12px;
      color: black;
      font-weight: bold;
      line-height: 25px;
      background-color: #d8d8d8;
    }
  }

}
:deep(.ant-tree-treenode){
  width: 100%;
  .ant-tree-node-content-wrapper{
    width: 78%;
  }
  .ant-tree-checkbox{
    margin: 1px 8px 0 0;
    .ant-checkbox-inner{
      border: 1px solid #c9c9c9;
    }
  }
  .checkbox-me{
    float: right
  }

}

</style>
