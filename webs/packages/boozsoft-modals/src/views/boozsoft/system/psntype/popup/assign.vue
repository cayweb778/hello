<template>
  <BasicModal
    width="800px"
    v-bind="$attrs"
    title="人员类别引入"
    @ok="handleOk()"
    @register="register"
  >
    <div class="nc-open-content">
      <div class="open-content-up" style="height: 500px;margin-left:30px;margin-right:30px;">
        <div style="text-align: center;">
          <label>引用状态:</label>
          <a-select v-model:value="formItems.flag" @change="flagChange()" style="width: 50%;">
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="0">未引用</a-select-option>
            <a-select-option value="1">已引用</a-select-option>
          </a-select>
        </div>
        <br/>
        <BasicTable
          :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
          @row-click="condClick"
          :scroll="{ y: 400 }"
          @register="registerTable"
          class="tables"
        >
          <template #psnTypeCode="{ record }">{{ formatPsnTypeCode(record.psnTypeId) }}</template>
          <template #psnTypeName="{ record }">{{ formatPsnTypeName(record.psnTypeId) }}</template>
        </BasicTable>
      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {nextTick, onMounted, reactive, ref, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {Select as ASelect, Input as AInput, Checkbox as ACheckbox} from 'ant-design-vue'

const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
import {useMessage} from "/@/hooks/web/useMessage";
// import {columnProps} from "ant-design-vue/es/table/interface";
import {BasicTable, useTable} from "/@/components/Table";
import {getUnitListNoGroup} from "/@/api/record/group/im-unit";
// import {findByAccId} from "/@/api/record/system/group-psn-type-account";
import {psnTypeFindAll} from "/@/api/record/system/group-psn-type";

const {
  createErrorModal
} = useMessage()

const emit = defineEmits(['register', 'save'])

const formItems: any = ref({})
const accountList: any = ref([])
const defaultAdName = ref('')
const dynamicTenantId = ref('')
const [register, {closeModal}] = useModalInner((data) => {
  defaultAdName.value = data.defaultAdName
  dynamicTenantId.value = data.dynamicTenantId

  formItems.value.flag = '0'
  state.selectedRowKeys = []
  checkRow.value = []
  flagChange()
})

const columns = [
  {
    title: '类别编码',
    dataIndex: 'psnTypeCode',
    fixed: true,
    ellipsis: true,
    slots: {customRender: 'psnTypeCode'}
  },
  {
    title: '类别名称',
    dataIndex: 'psnTypeName',
    fixed: true,
    ellipsis: true,
    slots: {customRender: 'psnTypeName'}
  },
  {
    title: '分配时间',
    dataIndex: 'assignDate',
    ellipsis: true,
    fixed: true,
  },
]
// 这是示例组件
const [registerTable, {reload}] = useTable({
  // api: findByAccId,
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  pagination: false,
  searchInfo: {
    accId: '',
    flag: '0',
  }
})

const psnTypeList: any = ref([])
onMounted(async () => {
  const res = await psnTypeFindAll()
  psnTypeList.value = res.items
})

function formatPsnTypeCode(psnTypeId) {
  let str = psnTypeId
  psnTypeList.value.forEach(item => {
    if (item.uniqueCode == psnTypeId) {
      str = item.psnTypeCode
    }
  })
  return str
}

function formatPsnTypeName(psnTypeId) {
  let str = psnTypeId
  psnTypeList.value.forEach(item => {
    if (item.uniqueCode == psnTypeId) {
      str = item.psnTypeName
    }
  })
  return str
}

const condClick = () => {

}

//选中内容
type Key = '';

const state = reactive<{
  selectedRowKeys: Key[];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const checkRow: any = ref([])
const checkRowUser: any = ref([])
const onSelectChange = (selectedRowKeys, row) => {
  // console.log('selectedRowKeys changed: ', row);
  checkRow.value = []
  checkRowUser.value = []
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
  row.forEach(res => {
    psnTypeList.value.forEach(item => {
      if (res.psnTypeId == item.uniqueCode) {
        checkRowUser.value.push(item)
      }
    })
  })
};

function flagChange() {
  setTimeout(() => {
    reload({
      searchInfo: {
        accId: defaultAdName.value,
        flag: formItems.value.flag
      }
    })
  }, 1000)
}

async function handleOk() {
  if (checkRow.value.length > 0) {
    formItems.value.tableUser = checkRowUser.value
    formItems.value.table = checkRow.value
    emit('save', unref(formItems))
    closeModal()
    return true
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '引用',
      content: '请至少选择一条记录进行引用！'
    })
    return false
  }
}

</script>
<style scoped lang="less">
.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}

.nc-open-content {
  input:not(.ant-select-selection-search-input,.ant-input){
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
    padding-bottom: 5px;bu shu
    color: #535353;
  }

  .open-content-down {
    margin-top: 5%;

    .ant-tabs-tab-active::before {
      position: absolute;
      top: 0px;
      left: 0;
      width: 100%;
      border-top: 2px solid transparent;
      border-radius: 2px 2px 0 0;
      transition: all 0.3s;
      content: '';
      pointer-events: none;
      background-color: rgb(1, 143, 251);
    }
  }
}

  .search input {
    width: 100%;
    border: none !important;
  }
  </style>
  <style scoped lang="less">
  .tables :deep(td),
  .tables :deep(th) {
    font-size: 14px !important;
    padding: 0 8px !important;
}
</style>
