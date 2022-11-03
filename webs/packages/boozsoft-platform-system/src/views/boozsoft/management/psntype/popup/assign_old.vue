<template>
  <BasicModal
    width="800px"
    v-bind="$attrs"
    title="人员类别分配"
    @ok="handleOk()"
    @register="register"
  >
    <div class="nc-open-content">
      <div class="open-content-up" style="height: 500px;margin-left:30px;margin-right:30px;">
        <div style="text-align: center;">
          <label>模糊过滤:</label>
          <a-input-search v-model:value="formItems.psnTypeCode" class="search" placeholder=""
                          style="width:40%;"/>
        </div>
        <br/>
        <BasicTable
          :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
          @row-click="condClick"
          :scroll="{ y: 300 }"
          @register="registerTable"
          class="tables"
        >
        </BasicTable>
        <br/>
        <a-checkbox :checked="formItems.addAccount" @change="onChange" style="width: 200px;">
          直接添加到公司档案
        </a-checkbox>
        <label v-if="formItems.addAccount">选择年度：</label>
        <a-select v-if="formItems.addAccount" v-model:value="formItems.iyear" style="width: 150px">
          <a-select-option v-for="item in yearList" :key="item.accountYear"
                           :value="item.accountYear">
            {{ item.accountYear }}
          </a-select-option>
        </a-select>
      </div>
    </div>

  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {nextTick, reactive, ref, unref} from 'vue'
import {BasicModal, useModalInner} from '/@/components/Modal'
import {Select as ASelect, Input as AInput, Checkbox as ACheckbox} from 'ant-design-vue'

const AInputSearch = AInput.Search
const ASelectOption = ASelect.Option
import {useMessage} from "/@/hooks/web/useMessage";
import {columnProps} from "ant-design-vue/es/table/interface";
import {BasicTable, useTable} from "/@/components/Table";
import {getUnitListNoGroup} from "/@/api/record/group/im-unit";
import {findYearByAccount} from "/@/api/record/system/bank-statement";

const {
  createErrorModal
} = useMessage()

const emit = defineEmits(['register', 'save'])

const formItems: any = ref({})
const [register, {closeModal}] = useModalInner((data) => {
  formItems.value.addAccount = false
  formItems.value.iyear = ''
})

const columns = [
  {
    title: '公司代码',
    dataIndex: 'coCode',
    fixed: true,
    ellipsis: true,
    width: '150px'
  },
  {
    title: '公司简称',
    dataIndex: 'accNameCn',
    ellipsis: true,
    fixed: true,
  },
]
// 这是示例组件
const [registerTable, {reload}] = useTable({
  api: getUnitListNoGroup,
  columns: columns,
  bordered: true,
  showIndexColumn: false,
  pagination: false,
})

const condClick = () => {

}

//选中内容
type Key = columnProps['id'];

const state = reactive<{
  selectedRowKeys: Key[];
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
  if (formItems.value.addAccount) {
    yearRecord()
  } else {
    formItems.value.iyear = ''
  }
};

function onChange(e) {
  formItems.value.addAccount = e.target.checked
  console.log('checked = ' + e.target.checked);
  if (formItems.value.addAccount) {
    yearRecord()
  } else {
    formItems.value.iyear = ''
  }
}

const yearList: any = ref([])

async function yearRecord() {
  if (checkRow.value.length > 0) {
    const res = await findYearByAccount(checkRow.value[0].accId)
    yearList.value = res
    if (yearList.value.length > 0) {
      formItems.value.iyear = yearList.value[0].accountYear
    }
  } else {
    yearList.value = []
    formItems.value.iyear = ''
  }
}

async function handleOk() {
  if (checkRow.value.length > 0) {
    if (formItems.value.addAccount && checkRow.value.length > 1) {
      createErrorModal({
        iconType: 'warning',
        title: '分配',
        content: '需要直接添加到公司档案时，每次只能选择一个公司进行分配！'
      })
      return false
    } else {
      formItems.value.table = checkRow.value
      emit('save', unref(formItems))
      closeModal()
      return true
    }
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '分配',
      content: '请至少选择一个公司进行分配！'
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
<style>
.tables td,
.tables th {
  font-size: 14px !important;
  padding: 0 8px !important;
}
</style>
