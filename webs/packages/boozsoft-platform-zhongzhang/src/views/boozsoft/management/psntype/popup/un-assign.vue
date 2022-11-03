<template>
  <BasicModal
    width="800px"
    v-bind="$attrs"
    title="取消人员类别分配"
    @ok="handleOk()"
    @register="register"
  >
    <div class="nc-open-content">
      <div class="open-content-up" style="height: 500px;margin-left:30px;margin-right:30px;">
        <div style="text-align: center;">
          <label>选择公司:</label>
          <a-select v-model:value="formItems.accId" @change="accIdChange()" style="width: 50%;">
            <a-select-option v-for="item in accountList" :key="item.accId" :value="item.accId">
              {{ item.coCode }}-{{ item.accNameCn }}
            </a-select-option>
          </a-select>
        </div>
        <br/>
        <BasicTable
          :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
          @row-click="condClick"
          :scroll="{ y: 350 }"
          @register="registerTable"
          class="tables"
        >
          <template #psnTypeCode="{ record }">{{ formatPsnTypeCode(record.psnTypeId) }}</template>
          <template #psnTypeName="{ record }">{{ formatPsnTypeName(record.psnTypeId) }}</template>
          <template #flag="{ record }">
            <span>
              <a-tag :color="record.flag === '1' ? 'volcano' : 'green'">
                {{ record.flag === '1' ? '已引入' : '未引入' }}
              </a-tag>
            </span>
          </template>
        </BasicTable>
      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {onMounted, reactive, ref, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {
  Select as ASelect,
  Input as AInput,
  Checkbox as ACheckbox,
  Tag as ATag
} from 'ant-design-vue'

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
const [register, {closeModal}] = useModalInner((data) => {
  accountList.value = []
  getUnitListNoGroup().then(res => {
    accountList.value = res
    formItems.value.accId = accountList.value[0].accId
    accIdChange()
  })
  state.selectedRowKeys = []
  checkRow.value = []
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
  {
    title: '引用状态',
    dataIndex: 'flag',
    ellipsis: true,
    fixed: true,
    slots: {customRender: 'flag'}
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
    flag: ''
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
// type Key = columnProps['id'];

const state = reactive<{
  // selectedRowKeys: Key[];
  loading: boolean;
}>({
  selectedRowKeys: [], // Check here to configure the default column
  loading: false,
});
const checkRow: any = ref([])
const onSelectChange = (selectedRowKeys, row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
};

function accIdChange() {
  reload({
    searchInfo: {
      accId: formItems.value.accId,
    }
  })
}

async function handleOk() {
  console.log(checkRow.value)
  if (checkRow.value.length > 0) {
    for (const item of checkRow.value) {
      if (item.flag == '1') {
        createErrorModal({
          iconType: 'warning',
          title: '取消分配',
          content: '已经引入的档案不能进行取消分配！'
        })
        return false
      }
    }
    formItems.value.table = checkRow.value
    emit('save', unref(formItems))
    closeModal()
    return true
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '取消分配',
      content: '请至少选择一条记录进行取消分配！'
    })
    return false
  }
}

</script>
<style lang="less">
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
  input {
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
