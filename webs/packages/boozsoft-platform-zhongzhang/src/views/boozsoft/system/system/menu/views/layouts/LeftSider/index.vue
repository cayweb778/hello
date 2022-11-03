<template>
  <div class="ant-layout-sider"
       style="background: rgb(255, 255, 255); padding-top: 10px; padding-left: 20px; padding-right: 15px; border-right: 1px solid rgb(226, 226, 226); flex: 0 0 240px; max-width: 240px; min-width: 240px; width: 240px;">
    <div class="ant-layout-sider-children">
      <div><span class="ant-input-search ant-input-affix-wrapper"
                 style="width: 120px; margin-top: 0px;">
        <input type="text" placeholder="定位"
               class="ant-input">
        <span class="ant-input-suffix"><i class="anticon anticon-search ant-input-search-icon"></i></span></span>
        <button type="button" class="ant-btn" disabled="" style="margin-left: 10px;">
          <span>组授权</span></button>
        <div><!-- react-empty: 25 --></div>
        <div class="tree-div" style="height: 648px;">
          <Tree
            v-model:expandedKeys="expandedKeys"
            v-model:selectedKeys="selectedKeys"
            v-model:checkedKeys="checkedKeys"
            checkable
            :tree-data="treeData"
          >
            <template #title="{ title, key }">
              <span v-if="key === '0-0-1-0'" style="color: #1890ff">{{ title }}</span>
              <template v-else>{{ title }}</template>
            </template>
          </Tree>

        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import {watch,ref} from 'vue'
import {Tree} from 'ant-design-vue'
const arr = [
  {name: '账套主管组'},
  {name: '采购组'},
  {name: '委外组'},
  {name: '库存组'},
  {name: '财务组'},
  {name: '销售组'},
  {name: '出纳组'},
  {name: '会计组'},
  {name: '独立用户'},
]
const treeData = [
  {title: '账套主管组',key: '0-1',},
  {title: '采购组',key: '0-2',},
  {title: '委外组',key: '0-3',},
  {title: '库存组',key: '0-4',},
  {title: '财务组',key: '0-5',},
  {title: '销售组',key: '0-6',},
  {title: '出纳组',key: '0-7',},
  {title: '会计组',key: '0-8',},
  {title: '独立用户',key: '0-9',},
];

const expandedKeys = ref<string[]>(['0-1', '0-1']);
const selectedKeys = ref<string[]>(['0-1', '0-1']);
const checkedKeys = ref<string[]>(['0-1', '0-1']);
watch(expandedKeys, () => {
  console.log('expandedKeys', expandedKeys);
});
watch(selectedKeys, () => {
  console.log('selectedKeys', selectedKeys);
});
watch(checkedKeys, () => {
  console.log('checkedKeys', checkedKeys);
});
</script>
