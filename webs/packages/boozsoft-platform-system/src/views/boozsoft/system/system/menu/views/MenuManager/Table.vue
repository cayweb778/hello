<template>
  <BasicTable
    class="w-4/5 xl:w-5/6"
    ref="tableRef"
    :scroll="{y: 400 }"
    :row-selection="{type: 'checkbox' ,fixed: true}"
    style="width:100%" @register="registerTable">
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
</template>
<script setup>

import {getUserMenuTreeByPlatformId2} from "../../../../../../../api/sys/user";
import {columns, searchFormSchema} from "../../data/menu.data";
import {useTable} from "../../../../../../../components/Table";
import {TableAction, BasicTable} from "../../../../../../../components/Table";
import {inject, onMounted} from "vue";
import {usePlatformsStoreWidthOut} from "/@/store/modules/platforms";

const viewModel = inject('viewModel')
const [registerTable, {reload}] = useTable({
  title: '菜单列表',
  api: getUserMenuTreeByPlatformId2,
  columns,
  pagination: {pageSize: 20},
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
  immediate: false,
  actionColumn: {
    width: 80,
    title: '操作',
    dataIndex: 'action',
    slots: {customRender: 'action'},
    fixed: undefined,
  },

});

viewModel.value.reload=reload

onMounted(async () => {

  abc.value = await usePlatformsStoreWidthOut().getPlatformListToNames()
  reload({
    searchInfo: {
      platformId: viewModel.value.currentPlatform.id
    }
  })
})

function handleEdit(record2) {
  // openDrawer(true, {
  //   record: record2.meta.target,
  //   isUpdate: true,
  // });
}

function handleDelete(record) {
  console.log(record);
}
</script>
