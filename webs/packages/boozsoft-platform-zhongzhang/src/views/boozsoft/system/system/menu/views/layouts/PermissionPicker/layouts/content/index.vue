<template>
  <div style="width:80%">
      <CheckboxGroup v-model:value="value">
        <Table :pagination="false"
               :scroll="{ x: 300, y: 300 }"
               :row-selection="rowSelection"
               :dataSource="dataSource"
               :columns="columns">
          <template #bodyCell="obj">
            <template v-if="obj.column.key === 'pageName'">
              <a>
                {{ obj.record.pageName }}
              </a>
            </template>
            <template v-else>
              <Checkbox :value="obj.column.dataIndex+'-'+obj.index"></Checkbox>
            </template>
          </template>

        </Table>
      </CheckboxGroup>
    <div>
      <Button>查询权限</Button>
      <Button>功能及字段权限控制</Button>
      <Button @click="save">保存</Button>
      <Button>返回</Button>
    </div>
  </div>
</template>
<script setup lang="ts">
import {watch, ref} from 'vue'
import {Button, Checkbox, Table, Tree} from 'ant-design-vue'

const CheckboxGroup = Checkbox.Group
import {
  getPlatformPermissionPickerMockData
} from "/@/views/boozsoft/system/system/menu/views/layouts/PermissionPicker/datas";


const value = ref([])

const dataSource = ref()


const columns = ref()

const rowSelection = {
  onChange: (selectedRowKeys: string[], selectedRows) => {
    console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
  },
  getCheckboxProps: (record) => ({
    disabled: record.name === 'Disabled User', // Column configuration not to be checked
    name: record.name,
  }),
};
const emit = defineEmits(['register'])

emit('register', {
  reloadContentFun(tenantId, platformId) {
    const mockData = getPlatformPermissionPickerMockData(platformId)
    dataSource.value = mockData.data
    columns.value = mockData.columns
  }
})


function save() {
  console.log(value)
}
</script>
