<script setup lang="ts">
import { defineComponent, getCurrentInstance, reactive, toRefs } from 'vue'
import { BasicTable, useTable, TableAction } from '/@/components/Table'
import { useModal } from '/@/components/Modal'
import Edit from './popup/edit.vue'
import type { BasicColumn } from '/@/components/Table/src/types/table.ts'
import { deleteAddress, getAddressListById, saveAddress } from '/@/api/record/system/address'
import { addressFindAll } from '/@/api/testrecord/test-record'
// addressFindAll().then(res => {
//   console.log(res)
//   alert(1)
// })
// CRUD接口
const CrudApi = {
  list: addressFindAll,
  columns: [
    { title: 'id', dataIndex: 'id', defaultHidden: true, ellipsis: true },
    // { title: '唯一标识', dataIndex: 'uniqueCode', ellipsis: true },
    { title: '省', dataIndex: 'province', ellipsis: true },
    { title: '市', dataIndex: 'city', ellipsis: true },
    { title: '区', dataIndex: 'district', ellipsis: true },
    { title: '街道', dataIndex: 'station', ellipsis: true },
    { title: '电话区号', dataIndex: 'areaCode', ellipsis: true },
    { title: '邮编', dataIndex: 'postcode', ellipsis: true }
  ],
  editData: {
    id: '',
    uniqueCode: '',
    province: '',
    city: '',
    district: '',
    station: '',
    areaCode: '',
    postcode: ''
  }
}
// @ts-ignore
const [registerTable, { instance, formInstance, reload }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  actionColumn: {
    width: 160,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  }
})
const [registerEditPage, { openModal: openEditPage }] = useModal()

const openAddPage = () => {
  openEditPage(true, {
    data: CrudApi.editData
  })
}
const openModifyPage = (a, b) => {
  openEditPage(true, {
    data: a
  })
}
const del = async(id) => {
  await deleteAddress(id)
  alert('删除成功！')
  reload()
}
const saveAddressMet = async(data) => {
  await saveAddress(data)
  reload(data)
}

</script>
<template>
  <div>
    <Edit
      @save="saveAddressMet"
      @register="registerEditPage"
    />
    <a-button type="primary" class="my-4" @click="openAddPage()">新增</a-button>
    <BasicTable @register="registerTable">
      <!--      <template
        #roleList="{ record }"
      >
&lt;!&ndash;        <span v-for="item in record.roleList">
          {{ item.roleName }}
        </span>&ndash;&gt;
      </template>-->
      <template #action="{ record }">

        <TableAction
          :width="'500px'"
          :actions="[
            {
              label: '编辑',
              icon: 'ic:outline-delete-outline',
              onClick: openModifyPage.bind(null, record),
            },{
              label: '删除',
              icon: 'ic:outline-delete-outline',
              onClick: del.bind(null, record.id)
            },
          ]"
        />
      </template>

    </BasicTable>
  </div>
</template>

<style>
.ant-tabs .ant-tabs-left-content {
  padding-left: 0 !important;
}

.card-container {
  background: #f5f5f5;
  overflow: hidden;
  padding: 24px;
}

.card-container > .ant-tabs-card > .ant-tabs-content {
  /*height: 120px;*/
  margin-top: -16px;
}

.card-container > .ant-tabs-card > .ant-tabs-content > .ant-tabs-tabpane {
  background: #fff;
  padding: 16px;
}

.card-container > .ant-tabs-card > .ant-tabs-bar {
  border-color: #fff;
}

.card-container > .ant-tabs-card > .ant-tabs-bar .ant-tabs-tab {
  border-color: transparent;
  background: transparent;
}

.card-container > .ant-tabs-card > .ant-tabs-bar .ant-tabs-tab-active {
  border-color: #fff;
  background: #fff;
}
</style>
