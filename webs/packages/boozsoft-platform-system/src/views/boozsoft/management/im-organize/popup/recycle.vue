<template>
  <BasicModal
    width="900px"
    v-bind="$attrs"
    title="回收站"
    @ok="handleOk()"
    @cancel="handleOk()"
    wrapClassName="head-title"
    @register="register"
  >
    <template #title>
      <div style="display: flex;height:30px;" class="vben-basic-title">
        <span style="font-size: 24px;line-height:30px;">
          <DeleteOutlined style="font-size: 28px;font-weight: bold"/>&nbsp;&nbsp;回收站
        </span>
      </div>
    </template>
    <div class="nc-open-content" style="margin:0;padding:0;">
      <div class="open-content-up" style="margin:0;padding:0;">
        <div
          style="background:#0096c7;padding:10px;border-radius: 2px;margin-bottom:10px;display: flex;justify-content : space-between;">
          <div class="a1">
            <Select v-model:value="searchModel.condition" class="special_select">
              <SelectOption style="font-size: 12px;" value="code">编码</SelectOption>
              <SelectOption style="font-size: 12px;" value="name">名称</SelectOption>
            </Select>
            <InputSearch
              placeholder=""
              v-model:value="searchModel.content"
              @search="onSearch"
            />
          </div>
          <div class="a2">
            <button
              type="button"
              class="ant-btn ant-btn-me"
              @click="recoverList()"
            ><span>还原</span></button>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              @click="delList()"
            ><span>彻底删除</span></button>
            <button
              type="button"
              class="ant-btn ant-btn-me"
              @click="exportExcel()"
            ><span>导出</span></button>
          </div>
        </div>

        <!--                @row-click="condClick"-->
        <div style="height: 350px;padding:0;display: flex;justify-content : space-between;">
          <BasicTable
            :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onSelect: onSelectChange,onSelectAll:onSelectAllChange  }"
            :scroll="{ y: 350 }"
            @register="registerTable"
            :rowKey='record=>record.id'
            :dataSource="tableData"
            class="tables"
          >
            <!--                <template #psnSex="{ record }"> {{ formatPsnSex(record.psnSex) }}</template>-->

          </BasicTable>
        </div>
      </div>
    </div>

    <template #footer>
      <div>
        <Button @click="handleOk()" type="primary">关闭</Button>
      </div>
    </template>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {nextTick, onMounted, reactive, ref, unref} from 'vue'
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import {
  DeleteOutlined
} from '@ant-design/icons-vue'
import {
  Select,
  Input,
  Button,
  message
} from 'ant-design-vue'

const InputSearch = Input.Search
const SelectOption = Select.Option
import {useMessage} from "/@/hooks/web/useMessage";
import {BasicTable, useTable} from "/@/components/Table";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {deletePsn, findPsnDelList, savePsn} from "/@/api/record/system/psn";
//import {aoaToSheetXlsx} from "/@/views/boozsoft/xian_jin_liu_liang/yin_hang_dui_zhang/yin_hang_dui_zhang_dan/excel/components/importexcel";
const aoaToSheetXlsx=null

const {
  createErrorModal,
  createConfirm,
  createWarningModal
} = useMessage()

const emit = defineEmits(['register', 'modify'])

const searchModel = reactive({
  condition: 'code',
  content: ''
})
const accountList: any = ref([])
const dynamicTenantId = ref('')
const tableData: any = ref([]);
const tableDataAll: any = ref([]);
const [register, {closeModal, setModalProps}] = useModalInner(async (data) => {
  dynamicTenantId.value = data.dynamicTenantId
  tableData.value = data.dataList.map(it => ({
    id: it.id,
    uniqueCode: it.uniqueCode,
    code: it.code,
    name: it.name,
    delName: it.delName,
    delDate: it.delDate
  }))
  tableDataAll.value = tableData.value
  state.selectedRowKeys = []
  checkRow.value = []
})

const columns = [
  {
    title: '代码',
    dataIndex: 'code',
    ellipsis: true,
    width: 100
  },
  {
    title: '名称',
    dataIndex: 'name',
    align: 'left',
    ellipsis: true,
    width: 200,
  },
  {
    title: '删除人',
    dataIndex: 'delName',
    width: 120,
    ellipsis: true
  },
  {
    title: '删除时间',
    dataIndex: 'delDate',
    width: 150,
    ellipsis: true
  }
]


// 这是示例组件
const [registerTable, {reload, getColumns, setPagination}] = useTable({
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  pagination: false
})


onMounted(async () => {
})

const condClick = () => {

}

//选中内容
const state = reactive<{
  selectedRowKeys: [];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const checkRow: any = ref([])
const onSelectChange = (selectedRowKeys, row, o) => {
  state.selectedRowKeys = o.map(it => it.id)
  checkRow.value = o
};
const onSelectAllChange = (t, row, o) => {
  if (t) {
    state.selectedRowKeys = o.map(it => it.id)
    checkRow.value = o
  } else {
    state.selectedRowKeys = []
    checkRow.value = []
  }
}


async function handleOk() {
  // emit('save', unref(formItems));
  closeModal();
}

async function onSearch() {
  tableData.value = tableDataAll.value.filter(item => {
    if (searchModel.condition == 'code' && searchModel.content != '') {
      return item.code.indexOf(searchModel.content) != -1
    }
    if (searchModel.condition == 'name' && searchModel.content != '') {
      return item.name.indexOf(searchModel.content) != -1
    }
    return item
  })
  await setPagination({
    total: tableData.value.length
  })
}

async function recoverList() {
  if (checkRow.value.length == 1) {
    createConfirm({
      iconType: 'warning',
      title: '提示',
      content: '你确认要还原吗?',
      onOk: async () => {
        emit('modify', {
          type: 'reduce',
          data: {uniqueCode: checkRow.value[0].uniqueCode, id: checkRow.value[0].id},
          callback: () => {
            close(checkRow.value[0].id)
            checkRow.value = [];
            message.success('恢复成功！')
          }
        })
      },
      onCancel: () => {
        return false
      }
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '恢复',
      content: '请选择一条需要还原的内容！'
    })
  }
}

const close = (id) => {
  tableData.value = tableData.value.filter(it => it.id != id)
  tableDataAll.value = tableData.value
}

async function delList() {
  if (checkRow.value.length == 1) {
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '删除后数据将不能恢复，你确认要删除吗?',
      onOk: async () => {
        emit('modify', {
          type: 'del',
          data: {uniqueCode: checkRow.value[0].uniqueCode, id: checkRow.value[0].id},
          callback: () => {
            close(checkRow.value[0].id)
            checkRow.value = [];
            message.success('删除成功！')
          }
        })
      },
      onCancel: () => {
        return false
      }
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '删除',
      content: '请选择一条需要删除的内容！'
    })
  }
}

//文件导出
function exportExcel() {
  emit('modify', {type: 'export'})
  /*const lanmu: any = getColumns()
  const list = []
  const arrData = list.map(item => {
    // item.psnSex=formatPsnSex(item.psnSex)
    item.psnType = item.psnType == '1' ? '内部人员' : '外部人员'
    item.cellPhoneNum = item.cellPhoneNum == '' ? '' : item.cellPhoneNum.length > 10 ? plusStr(item.cellPhoneNum, 3, 4, '****') : plusStr(item.cellPhoneNum, 3, 0, '******')
    return item
  }).map(item => lanmu.map(column => item[column.dataIndex]))
  aoaToSheetXlsx({
    data: arrData,
    header: lanmu.map(item => item.title),
    filename: '人员信息模板.xlsx',
  });*/
}

function plusStr(str, frontLen, endLen, cha) {
  return str.substring(0, frontLen) + cha + str.substring(str.length - endLen);
}
</script>
<style scoped lang="less">
.ant-modal-header {
  border: none !important;
}

.vben-basic-title {
  color: #0096c7 !important;
  border: none !important;
  margin-bottom: -20px !important;
}

:global(.ant-modal-body) {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
  margin-bottom: 0 !important;

  .scrollbar {
    padding: 0px;

    .scroll-container {
      margin-bottom: 0 !important;
    }
  }
}

.nc-open-content {
  .a1 {
    :deep(.ant-select-selector) {
      border: none;
      line-height: 32px;
      height: 32px;
      width: 130px;
      border-radius: 4px;
      text-align: left;
      border: 1px solid #c9c9c9;
      border-radius: 4px 0 0 4px;
    }

    :deep(.ant-input-search) {
      border: 1px solid #c9c9c9;
      border-left: none;
      width: 200px;
      border-radius: 0 4px 4px 0;
    }
  }

  input:not(.ant-select-selection-search-input,.ant-input) {
    width: 50%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  .ant-select-selector {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label {
    text-align: left;
    width: 110px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    color: #535353;
  }
}

.search input {
  width: 100%;
  border: none !important;
}

.tables :deep(td),
.tables :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}

.tables :deep(th) {
  text-align: center !important;
  font-weight: bold;
  background-color: #cccccc;
  line-height: 30px;
}

.bg-white {
  width: 250px !important;
  min-height: 385px !important;
  height: calc(100%);
  border: 1px #cccccc solid;
  background: white !important;
}

:deep(.ant-table-row-selected) td {
  background: #0096c7 !important;
}

:deep(.ant-tree-list) {
  margin: 0 !important;
  padding: 0 !important;
}
</style>
