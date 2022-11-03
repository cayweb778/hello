<template>
  <div >
    <div>当前授权用户组：  财务组</div>
    <div style="height:50px;border:solid 1px black">
      <div>公司：</div>
      <div>代码：</div>
    </div>
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
</template>
<script setup lang="ts">
import {watch, ref, provide, inject} from 'vue'
import {Table,Tree} from 'ant-design-vue'
const treeData = [
  {title: '开发模块库', key: '1013',},
  {title: '报表中心', key: '10131200',},
  {title: '现金银行', key: '1006',},
  {title: '应付账', key: '20017',},
  {title: '应收账', key: '1007',},
  {title: '总账', key: '1001',},
  {title: 'NC桌面', key: '20006',},
  {title: '操作员', key: '10133100',},
  {title: '清除锁定', key: '10133200',},
  {title: '集团管理', key: '1003',},
  {title: '操作日志', key: '10133300',},
  {title: '组织管理', key: '1014',},
  {title: '任务中心', key: '10133400',},
  {title: '电子票据', key: '20003',},
  {title: '固定资产', key: '20001',},
  {title: '华夏CRM', key: '20002',},
  {title: '网上报销', key: '20005',},
  {title: '薪酬管理', key: '20013',},
  {title: 'five', key: '20011',},
  {title: '供应商', key: '20010',},
  {title: '主数据', key: '20007',},
  {title: '客户', key: '20009',},
  {title: '机构', key: '20008',},
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
for (let i = 0; i < 20; i++) {

  dataSource.push({
    key: i,
    name: 'ceshi',
    age: '账套主管组',
    address: '西湖区湖底公园1号',
  })
}

const columns = [
  {title: '发送邮件', dataIndex: 'name1', key: 'name1',},
  {title: '发布', dataIndex: 'name2', key: 'name2',},
  {title: '核销', dataIndex: 'name3', key: 'name3',},
  {title: '操作', dataIndex: 'name4', key: 'name4',},
  {title: '新增', dataIndex: 'name5', key: 'name5',},
  {title: '修改', dataIndex: 'name6', key: 'name6',},
  {title: '插入', dataIndex: 'name7', key: 'name7',},
  {title: '编辑', dataIndex: 'name8', key: 'name8',},
  {title: '删除', dataIndex: 'name9', key: 'name9',},
  {title: '查看', dataIndex: 'name10', key: 'name10',},
  {title: '打印', dataIndex: 'name11', key: 'name11',},
  {title: '打印模板设置', dataIndex: 'name12', key: 'name12',},
  {title: '审核', dataIndex: 'name13', key: 'name13',},
  {title: '弃审', dataIndex: 'name14', key: 'name14',},
  {title: '出纳签字', dataIndex: 'name15', key: 'name15',},
  {title: '作废', dataIndex: 'name16', key: 'name16',},
  {title: '设置', dataIndex: 'name17', key: 'name17',},
  {title: '导出', dataIndex: 'name18', key: 'name18',},
  {title: '附件查看', dataIndex: 'name19', key: 'name19',},
  {title: '附件编辑', dataIndex: 'name20', key: 'name20',},
  {title: '消息', dataIndex: 'name21', key: 'name21',},
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
const emit=defineEmits(['selected'])
watch(selectedKeys, () => {
  emit('selected',selectedKeys.value[0])
});
watch(checkedKeys, () => {
  console.log('checkedKeys', checkedKeys);
});


const viewModels=inject('viewModels')

</script>
