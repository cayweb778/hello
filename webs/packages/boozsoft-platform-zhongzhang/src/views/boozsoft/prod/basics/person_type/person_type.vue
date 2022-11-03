<template>
  <div class="p-4">
    <div class="mb-4">
      <a-button class="mr-2" @click="handleLevel(2)">显示到第2级</a-button>
      <a-button class="mr-2" @click="handleLevel(1)">显示到第1级</a-button>
      <a-button class="mr-2" @click="handleSetCheckData">设置勾选数据</a-button>
      <a-button class="mr-2" @click="handleGetCheckData">获取勾选数据</a-button>
      <a-button class="mr-2" @click="handleSetSelectData">设置选中数据</a-button>
      <a-button class="mr-2" @click="handleGetSelectData">获取选中数据</a-button>

      <a-button class="mr-2" @click="handleSetExpandData">设置展开数据</a-button>
      <a-button class="mr-2" @click="handleGetExpandData">获取展开数据</a-button>
    </div>
    <div class="mb-4">
      <a-button class="mr-2" @click="appendNodeByKey(null)">添加根节点</a-button>
      <a-button class="mr-2" @click="appendNodeByKey('2-2')">添加在parent3内添加节点</a-button>
      <a-button class="mr-2" @click="deleteNodeByKey('2-2')">删除parent3节点</a-button>
      <a-button class="mr-2" @click="updateNodeByKey('1-1')">更新parent2节点</a-button>
    </div>
    <CollapseContainer title="函数操作" class="mr-4" :can-expan="false" :style="{ width: '20%',height: '800px',float: 'left' }">
      <BasicTree ref="treeRef" :tree-data="treeData" :checkable="true" />
    </CollapseContainer>
    <CollapseContainer title="函数操作" class="mr-4" :can-expan="false" :style="{ width: '20%',height: '800px',display: 'table-cell' }">
      <a-table
        :columns="columns"
        :data-source="data"
        :pagination="false"
        :scroll="{ y: 500 }"
      />
    </CollapseContainer>
  </div>

</template>
<script lang="ts">
import { defineComponent, reactive, ref, toRefs, unref } from 'vue'
import { BasicTree, TreeActionType } from '/@/components/Tree/index'
import { treeData } from './data'
import { CollapseContainer } from '/@/components/Container/index'
import { useMessage } from '/@/hooks/web/useMessage'
const columns = [
  {
    title: 'Name',
    dataIndex: 'name',
    width: 150
  },
  {
    title: 'Age',
    dataIndex: 'age',
    width: 150
  },
  {
    title: 'Address',
    dataIndex: 'address'
  }
]

const data = []
for (let i = 0; i < 100; i++) {
  data.push({
    key: i,
    name: `Edward King ${i}`,
    age: 32,
    address: `London, Park Lane no. ${i}`
  })
}

export default defineComponent({
  components: { BasicTree, CollapseContainer },
  setup() {
    const state = reactive({
      bbbb: '1232'
    })
    const treeRef = ref<Nullable<TreeActionType>>(null)
    const { createMessage } = useMessage()
    function getTree() {
      const tree = unref(treeRef)
      if (!tree) {
        throw new Error('tree is null!')
      }
      return tree
    }

    function handleLevel(level: number) {
      getTree().filterByLevel(level)
    }

    function handleSetCheckData() {
      getTree().setCheckedKeys(['0-0'])
    }

    function handleGetCheckData() {
      const keys = getTree().getCheckedKeys()
      createMessage.success(JSON.stringify(keys))
    }

    function handleSetSelectData() {
      getTree().setSelectedKeys(['0-0'])
    }

    function handleGetSelectData() {
      const keys = getTree().getSelectedKeys()
      createMessage.success(JSON.stringify(keys))
    }

    function handleSetExpandData() {
      getTree().setExpandedKeys(['0-0'])
    }

    function handleGetExpandData() {
      const keys = getTree().getExpandedKeys()
      createMessage.success(JSON.stringify(keys))
    }

    function appendNodeByKey(parentKey: string | null = null) {
      getTree().insertNodeByKey({
        parentKey: parentKey,
        node: {
          title: '新增节点',
          key: '2-2-2'
        },
        // 往后插入
        push: 'push'
        // 往前插入
        // push:'unshift'
      })
    }

    function deleteNodeByKey(key: string) {
      getTree().deleteNodeByKey(key)
    }

    function updateNodeByKey(key: string) {
      getTree().updateNodeByKey(key, {
        title: 'parent2-new'
      })
    }

    return {
      ...toRefs(state),
      abc() {
        state.bbbb = 'aaaa'
      },
      data,
      columns,
      treeData,
      treeRef,
      handleLevel,
      handleSetCheckData,
      handleGetCheckData,
      handleSetSelectData,
      handleGetSelectData,
      handleSetExpandData,
      handleGetExpandData,
      appendNodeByKey,
      deleteNodeByKey,
      updateNodeByKey
    }
  }

})
</script>
<style scoped>
:deep(.ant-table-body){
  max-height: 680px !important;
  overflow-y: scroll;
}
</style>
