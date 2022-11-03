<template>
  <div class="voucher-list-tree">
    <Tabs type="card" size="small" :tabBarGutter="0" v-model:activeKey="activeKey">
      <TabPane key="1" :style="activeKey=='1'?{height: (windowHeight-285)+'px'}:{}">
        <template #tab>&emsp;年度&emsp;</template>
        <div style="text-align: center;height: 50px;background-color: #f1f1f1;padding: 9px 0;"
             v-if="!props.isOrg">
          <Button @click="oper('1','add')">新增</Button>&ensp;
          <Button @click="oper('1','del')">删除</Button>
        </div>
        <BasicTree
          title=""
          :tree-data="treeData"
          :replace-fields="{ key: 'value', title: 'title' }"
          v-model:checkedKeys="checkedKeys"
          v-model:expandedKeys="expandedKeys"
          v-model:selectedKeys="selectedKeys"
          :height="400"
          checkable
          @select="handleSelect"
        />
      </TabPane>
      <TabPane key="2" :style="activeKey=='2'?{height: (windowHeight-285)+'px'}:{}">
        <template #tab>&emsp;季度&emsp;</template>
        <div style="text-align: center;height: 40px;line-height: 40px;background-color: #f1f1f1">
          <Select style="width:100px;text-align: center;" v-model:value="compParameter.year"
                  @change="yearChange">
            <SelectOption v-for="i in yearList" :value="i">{{ i }}年</SelectOption>
          </Select>
        </div>
        <div style="text-align: center;height: 50px;background-color: #f1f1f1;padding: 9px 0;"
             v-if="!props.isOrg">
          <Button @click="oper('2','add')">新增</Button>&ensp;
          <Button @click="oper('2','del')">删除</Button>
        </div>
        <BasicTree
          title=""
          v-model:checkedKeys="checkedKeys2"
          v-model:expandedKeys="expandedKeys2"
          v-model:selectedKeys="selectedKeys2"
          :replace-fields="{ key: 'value', title: 'title' }"
          checkable
          :height="300"
          :tree-data="treeData2"
        />
      </TabPane>
    </Tabs>
  </div>
</template>
<script setup lang="ts">
import {onMounted, ref, watch, inject} from "vue";
import {BasicTree} from '/@/components/Tree';
import {Select, Tabs, Button, message} from 'ant-design-vue';
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {delAccTheYearPeriod, saveAccNextYearPeriod} from "/@/api/record/group/im-organize";
import {useMessage} from "/@/hooks/web/useMessage";

const TabPane = Tabs.TabPane
const SelectOption = Select.Option
const emit = defineEmits(['select', 'quarter'])
const props = defineProps(['isOrg', 'coCode'])

const compParameter = ref({
  year: '2021',
})

const activeKey = ref('1')
const treeData: any = ref([])
const checkedKeys: any = ref([])
const selectedKeys: any = ref([])
const expandedKeys: any = ref([])

const treeData2: any = ref([])
const selectedKeys2: any = ref([])
const checkedKeys2: any = ref([])
const expandedKeys2: any = ref([])
const windowHeight = (window.innerHeight)
const yearList = inject('yearList')
const quarterList = inject('quarterList')
const showYearList = ref([])
let reQ = false; // 是否再次查询

watch(() => yearList.value, async () => {
  if (activeKey.value == '2') {
    fetch2()
  } else {
    fetch()
  }
})
watch(() => activeKey.value, async () => {
  if (activeKey.value == '2') await fetch2()
  checkedKeys.value = []
  checkedKeys2.value = []
  if (!reQ) {
    emit('select', {type: (activeKey.value == 1 ? 'year' : 'quarter'), reload: true})
  } else {
    reQ = false
  }

})

async function fetch() {
  treeData.value = yearList.value.map(i => ({
    'value': i,
    'title': `${i}年`,
  }))
  if (treeData.value.length > 0)
    handleSelect([treeData.value[treeData.value.length - 1].value])
}

async function fetch2() {
  const num = quarterList.value.length
  let list = []
  let arr = ['一','二','三','四','五','六']
  for(let i = 1;i<=num;i++) list.push({value: `0${i}`,title: `第${arr[i-1]}季度`,disableCheckbox: i<5?true:false })
  let exps = []
  treeData2.value = list
  expandedKeys2.value = exps
  compParameter.value.year = treeData2.value[treeData2.value.length - 1].value
 /* handleSelect([compParameter.value.year])*/
}

function handleSelect(keys: string, e: any) {
  if (keys.length > 0)
  emit('select', {type: (activeKey.value == 1 ? 'year' : 'quarter'), value: keys[0]})
}

const oper = (v, type) => {
  if (v == '1'){
    if (type == 'add') {
      startAdd(v)
    } else {
      startDel(v)
    }
  }else {
    if (type == 'add') {
      let len = treeData2.value.length
      if (len < 6){
        treeData2.value.push({value: `0${len+1}`,title: `第${len==4?'五':'六'}季度`,disableCheckbox: false })
        emit('quarter', {type:'add', len: 1})
      }else {
        message.success('系统最大支持六个季度！')
      }
    } else {
      if (checkedKeys2.value.length == 0) {
        message.warning('至少选择一个季度进行删除！')
      }else {
        treeData2.value = treeData2.value.filter(it=>checkedKeys2.value.indexOf(it.value) == -1)
        emit('quarter', {type:'del', len: checkedKeys2.value.length})
        checkedKeys2.value = []
      }
    }

  }
}
const {createConfirm} = useMessage();

async function startAdd(v) {
  // 询问
  let year = treeData.value[treeData.value.length - 1].value
  createConfirm({
    iconType: 'warning',
    title: '期间设置',
    content: `您确定要新增${parseInt(year) + 1}年度会计期间吗?`,
    onOk: async () => {
      let res = await saveAccNextYearPeriod({unique: props.coCode, iyear: year, type: '1'})
      message.success('新增成功！')
      emit('select', {type: v,value: treeData.value[treeData.value.length-1].value,reload: true})
    }, onCancel: async () => {
    }
  });
}

async function startDel(v) {
  if (checkedKeys.value.length != 1) {
    message.warning('只能选择一个年度进行删除！')
  } else if (treeData.value[0].value == checkedKeys.value[0]) {
    message.warning('组织启用年不得进行删除！')
  } else {
    let year = checkedKeys.value[0]
    createConfirm({
      iconType: 'warning',
      title: '期间设置',
      content: `您确定要删除${year}年度会计期间吗?`,
      onOk: async () => {
        let res = await delAccTheYearPeriod({unique: props.coCode, iyear: year, type: '1'})
        message.success('删除成功！')
        emit('select', {type: v,value: treeData.value[treeData.value.length-2].value,reload: true})
      }, onCancel: async () => {
      }
    });
  }
}

const yearChange = (v) => {
  fetch2()
  handleSelect([], [])
}
</script>
<style lang="less" scoped>
.voucher-list-tree {
  display: inline-block;
  height: calc(100%);
  min-width: 181px;
  float: left;

  :deep(.ant-select-selector) {
    background-color: #f1f1f1;
    border: none;
    border-bottom: solid 1px rgb(191, 191, 191) !important;
  }

  :deep(.ant-tabs-nav) {
    margin-bottom: 0px;

    .ant-tabs-tab-active {
      background-color: #f1f1f1;
      border-top: 2px solid #0096c7;
      border-bottom: none;
    }
  }

  :deep(.ant-tabs-content) {
    padding: 0 10px 10px;
    background-color: #f1f1f1;

    .scroll-container {
      border: 2px groove #cac2c2;
      background-color: #f1f1f1;
      height: calc(100% - 60px) !important;

      .ant-tree-list {
        background-color: #f1f1f1 !important;
      }
    }
  }
}
</style>
