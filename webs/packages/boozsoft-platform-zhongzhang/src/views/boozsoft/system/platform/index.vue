<template>
  <BasicTable @register="registerTable">
    <template #toolbar>
      <a-button type="primary" @click="handleCreate"> 新增菜单 </a-button>

    </template>
    <template #action="{ record }">
      <TableAction
        :actions="[
            {
              icon: 'clarity:note-edit-line',
              onClick: handleEdit.bind(null, record),
            },
            {
              icon: 'ant-design:delete-outlined',
              color: 'error',
              popConfirm: {
                title: '是否确认删除',
                confirm: handleDelete.bind(null, record),
              },
            },
          ]"
      />
    </template>
  </BasicTable>
  <MenuDrawer @register="registerDrawer" @success="handleSuccess" />
</template>
<script setup>
import {GetMenuTree} from "/@/api/sys/menu";
import {TableAction,BasicTable} from "../../../../components/Table";
import {onMounted, ref} from "vue";
import {useTable} from "/@/components/Table";
import {searchFormSchema,columns} from "./menu.data";
import {useDrawer} from "../../../../components/Drawer";
import MenuDrawer from "./MenuDrawer.vue";

const [registerTable, { reload }] = useTable({
  title: '平台列表',
  api: GetMenuTree,
  columns,
  formConfig: {
    labelWidth: 120,
    schemas: searchFormSchema,
  },
  // pagination: false,
  striped: false,
  useSearchForm: true,
  showTableSetting: true,
  bordered: true,
  showIndexColumn: false,
  canResize: false,
  actionColumn: {
    width: 80,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' },
    fixed: undefined,
  },
});
const  platformId=ref({key:'jack'})
const [registerDrawer, { openDrawer }] = useDrawer();

function handleEdit(record) {
  openDrawer(true, {
    record,
    isUpdate: true,
  });
}

function handleDelete(record) {
  console.log(record);
}

function handleSuccess() {
  reload();
}
function handleCreate() {
  openDrawer(true, {
    isUpdate: false,
  });
}



</script>