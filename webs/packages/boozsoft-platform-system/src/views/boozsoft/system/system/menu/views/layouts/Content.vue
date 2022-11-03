<template>
  <div class="ant-layout-content"
       style="padding-top: 10px; padding-left: 20px; padding-right: 20px;">
    <div>
      <div>
        <button type="button" class="ant-btn"><span>批量删除</span></button>
        <button type="button" class="ant-btn" style="margin-left: 10px;"><span>用户授权</span>
        </button>
        <span class="ant-input-search ant-input-affix-wrapper"
              style="margin-left: 20px; width: 200px;"><input type="text" placeholder="定位"
                                                              class="ant-input"><span
          class="ant-input-suffix"><i
          class="anticon anticon-search ant-input-search-icon"></i></span></span>
        <div><!-- react-empty: 45 --></div>
      </div>
    <div style="height: 300px;overflow: scroll">
      <Table :pagination="false" :row-selection="rowSelection" :dataSource="dataSource" :columns="columns"/>
    </div>


    </div>
  </div>
</template>
<script setup lang="ts">
import {watch, ref} from 'vue'
import {Table} from 'ant-design-vue'

const treeData = [
  {title: '账套主管组', key: '0-0',},
  {title: '采购组', key: '0-0',},
  {title: '委外组', key: '0-0',},
  {title: '库存组', key: '0-0',},
  {title: '财务组', key: '0-0',},
  {title: '销售组', key: '0-0',},
  {title: '出纳组', key: '0-0',},
  {title: '会计组', key: '0-0',},
  {title: '独立用户', key: '0-0',},
];

const dataSource = [
  {
    key: '1',
    name: '888',
    age: '账套主管组',
    address: '西湖区湖底公园1号',
  },
  {
    key: '2',
    name: 'ceshi',
    age: '账套主管组',
    address: '西湖区湖底公园1号',
  },
]
for(let i=0;i<20;i++){

  dataSource.push( {
    key: i,
    name: 'ceshi',
    age: '账套主管组',
    address: '西湖区湖底公园1号',
  })
}
const columns= [
  {
    title: '用户账号',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '用户姓名',
    dataIndex: 'age',
    key: 'age',
  },
  {
    title: '所属用户组',
    dataIndex: 'address',
    key: 'address',
  },  {
    title: '手机号',
    dataIndex: 'address3',
    key: 'address3',
  },  {
    title: '邮箱',
    dataIndex: 'address4',
    key: 'address4',
  },
]

const rowSelection = {
  onChange: (selectedRowKeys: string[], selectedRows) => {
    console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
  },
  getCheckboxProps: (record) => ({
    disabled: record.name === 'Disabled User', // Column configuration not to be checked
    name: record.name,
  }),
};


const
  expandedKeys = ref<string[]>(['0-0-0', '0-0-1']);
const selectedKeys = ref<string[]>(['0-0-0', '0-0-1']);
const checkedKeys = ref<string[]>(['0-0-0', '0-0-1']);
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
