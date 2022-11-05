<script setup lang="ts">
import { defineComponent, reactive } from 'vue'
import { getAdministrativeZoneAll } from '/@/api/admin/administrativeZone'
import {
  BasicTable,
  useTable,
  TableAction,
  BasicColumn,
  ActionItem,
  EditTableHeaderIcon,
  EditRecordRow
} from '/@/components/Table'

// const columns: BasicColumn[] = AdministrativeZonecColumns()
const data: any[] = [
  {
    id: '',
    province: '',
    city: '',
    district: '',
    station: '',
    population: '',
    area: '',
    administrativeDivisionCode: '',
    areaCode: '',
    lng: '',
    lat: ''
  }
]
const state = reactive({
  data: data
})

const props = {
  columns: columns,
  showIndexColumn: false,
  dataSource: state.data,
  actionColumn: {
    width: 160,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' }
  },
  pagination: false
}
const [registerTable, { getDataSource, setProps }] = useTable(props)
getAdministrativeZoneAll().then(res => {
  props.dataSource = res
  setProps(props)
})

function handleEdit(record: EditRecordRow) {
  alert(1)
  record.onEdit?.(true)
}

function handleCancel(record: EditRecordRow) {
  record.onEdit?.(false)
  if (record.isNew) {
    const data = getDataSource()
    const index = data.findIndex((item) => item.key === record.key)
    data.splice(index, 1)
  }
}

function handleSave(record: EditRecordRow) {
  record.onEdit?.(false, true)
}

function handleAdd() {
  const data = getDataSource()
  const addRow: EditRecordRow = {
    name: '',
    no: '',
    dept: '',
    editable: true,
    isNew: true
  }
  data.push(addRow)
}

function createActions(record: EditRecordRow, column: BasicColumn): ActionItem[] {
  if (!record.editable) {
    return [
      {
        label: '编辑',
        onClick: handleEdit.bind(null, record)
      },
      {
        label: '删除'
      }
    ]
  }
  return [
    {
      label: '保存',
      onClick: handleSave.bind(null, record, column)
    },
    {
      label: '取消',
      popConfirm: {
        title: '是否取消编辑',
        confirm: handleCancel.bind(null, record, column)
      }
    }
  ]
}
</script>

<template>
  <div>
    <BasicTable @register="registerTable">
      <template #action="{ record, column }">
        <TableAction :actions="createActions(record, column)" />
      </template>
    </BasicTable>
    <a-button block class="mt-5" type="dashed" @click="handleAdd">新增成员</a-button>
  </div>
</template>
