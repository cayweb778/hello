<template>
  <BasicModal
    width="900px"
    :minHeight="500"
    :height="500"
    v-bind="$attrs"
    title="计量单位"
    @ok="handleOk()"
    @cancel="handleOk()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height: 25px;font-size: 20px;">
          <SearchOutlined style="width:25px;margin-right: 10px;"/>
          计量单位
        </span>
      </div>
    </template>
    <div style="background-color: #158eb8;height: 50px;padding-top: 10px;padding-left:10px;border-radius: 5px;">
      <a-select style="width: 120px;" v-model:value="pageParameter.searchConditon.requirement">
        <a-select-option value="ecCode">计量单位名称</a-select-option>
        <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
      </a-select>
      <a-input-search placeholder="" v-model:value="pageParameter.selectValue" style="width: 200px; border-radius: 4px" @search="query()"/>
      <div style="float: right;margin-right: 5px;">
        <button type="button" class="ant-btn ant-btn-me" @click="add"><span>引入</span></button>
        <button type="button" class="ant-btn ant-btn-me" @click="query"><span>刷新</span></button>
      </div>
    </div>

    <div class="bg-white" style="display: inline;width: 30%;float: left;margin-top: 5px;">
      <div style="width: 100%; height: 26px;text-align: center;background-color: rgb(216 216 216);">
        <label style="font-size: 14px;font-weight: bold;">计量单位</label>
      </div>
      <BasicTree
        title=""
        :click-row-to-expand="false"
        :autoExpandParent="true"
        :tree-data="treeDatas"
        :replace-fields="{ key: 'unitCode', title: 'value' }"
        v-model:treeselectedKeys="treeselectedKeys"
        v-model:treeexpandedKeys="treeexpandedKeys"
        @select="handleSelect"
      />
    </div>

    <div style="display: inline;width: 69.5%;float: right;margin-top: 5px;">
      <BasicTable @register="registerTable" :scroll="{ y: 400 }"
                  :class="'a-table-font-size-12'"
                  :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
                  @row-dbClick="rowDbClick">
        <template #flag="{ record }">
          <a-tag color="success" v-if="record.flag === '1'"  slot="flag" slot-scope="text,record,index">
            {{ record.flag === '1' ? '启用' : '停用' }}

          </a-tag>
          <a-tag color="warning" v-else-if="record.flag === '0'"  slot="flag"  slot-scope="text,record,index">
            {{ record.flag === '1' ? '启用' : '停用' }}
          </a-tag>
        </template>

        <template #isMain="{ record }">
              <span>
                  {{ record.isMain === 'true' ? '是' : '否' }}
              </span>
        </template>


        <template #conversionType="{ record }">
          {{ formatFx(record.conversionType)}}
        </template>
      </BasicTable>
    </div>

    <template #footer>
      <a-button @click="handleOk()" type="primary">关闭</a-button>
    </template>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
import AddPage from './edit.vue';
import {BasicTable, useTable} from '/@/components/Table';
import { BasicTree } from '/@/components/Tree';
import { SearchOutlined,CaretDownOutlined,DeleteOutlined  } from '@ant-design/icons-vue';
import { onMounted, reactive, ref, toRaw, unref } from 'vue';
import {BasicModal, useModal, useModalInner} from '/@/components/Modal';
import {
  Select as ASelect,
  Form as AForm,
  Input as AInput,
  Statistic as AStatistic, message,
} from 'ant-design-vue';
import {getCurrentAccountName} from "/@/api/task-api/tast-bus-api";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useMessage} from "/@/hooks/web/useMessage";
import {findAllByAccCountPartColumn} from "/@/api/record/system/account";
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {
  deleteList,
  reductionList,
} from '/@/api/record/group/dept-class'

import {
  editFlag,
  deletePsn,
  delMany,
  delManyList,
  savePsn,
  saveList,
  delType,
  findAllSysUnitOfMeaType, findAllSysUnitOfMeaList
} from '/@/api/group/unit-mea'

import {
  findInAll,
  findInsAll,
  introduceData,
  introduceDatas
} from '/@/api/record/system/unit-mea'

import {} from "/@/api/record/system/zf-class";
const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const AFormItem = AForm.Item;
const { createErrorModal } = useMessage()
const emit=defineEmits(['register']);
const database = ref(getCurrentAccountName(true));
const accId = ref('');
const loading = ref(true);
const treeData:any = ref([]);
const selectedKeys2:any = ref([]);
const expandedKeys:any = ref([]);
const dataSource:any = ref([]);

const CrudApi = {
  list: [],
  columns: [
    {
      title: 'id',
      dataIndex: 'id',
      width: 120,
      ifShow: false,
      ellipsis: true
    },
    {
      title: '状态',
      dataIndex: 'flag',
      width: 60,
      slots: {customRender: 'flag'}
    },
    {
      title: '计量单位名称',
      dataIndex: 'unitName',
      width: 180,
      ellipsis: true,
      slots: {customRender: 'unitName'}
    }
  ],
  columns2: [
    {
      title: 'id',
      dataIndex: 'id',
      width: 120,
      ifShow: false,
      ellipsis: true
    },
    {
      title: '计量单位名称',
      dataIndex: 'unitName',
      width: 180,
      ellipsis: true,
      slots: {customRender: 'unitName'}
    },
    {
      title: '主计量',
      dataIndex: 'isMain',
      width: 80,
      ellipsis: true,
      slots: {customRender: 'isMain'}
    },
    {
      title: '换算类型',
      dataIndex: 'conversionType',
      width: 120,
      ellipsis: true,
      slots: {customRender: 'conversionType'}
    },
    {
      title: '换算率',
      dataIndex: 'conversionRate',
      width: 120,
      ellipsis: true,
      slots: {customRender: 'conversionRate'}
    },
    {
      title: '换算说明',
      dataIndex: 'conversionExplain',
      width: 180,
      ellipsis: true,
      slots: {customRender: 'conversionExplain'}
    }
  ],
  editData: {
    id: '',
    flag: '',
    unitCode: '',
    unitName: '',
    typename: '',
    isMain: '',
    conversionType: '',
    conversionRate: '',
  }
}

const selectedRowKeys:any = ref([]);
const selectedRowData:any = ref({});
const styleList:any = ref([]);

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
};

const pageParameter = reactive({
  showRulesSize: 'MIN',
  companyCode: '',
  companyName: '',
  ifUnit: false,
  total: 0,
  thisAdInfo: {},
  thisLeftKey: '',
  flag:'1',
  database: database.value,
  accId: accId.value,
  username: useUserStoreWidthOut().getUserInfo.username,
  uniqueCustclass:'',
  selectValue:'',
  typeCode:'k1',
  searchConditon: {
    requirement: 'ecCode',
    value: '',
  },
})

const [registerSavePage, { openModal: openSavePage }] = useModal();
// 这是示例组件
const [registerTable, { reload,getSelectRows,getDataSource,setTableData,setColumns }] = useTable({
  api: useRouteApi(findInAll,{schemaName: database}),
  columns: CrudApi.columns,
  loading: loading.value,
  bordered: true,
  pagination:{ pageSize: 50,showSizeChanger: true, pageSizeOptions: ['50','100'],showTotal: t => `总共${t}条数据` },
  searchInfo: pageParameter
});
const [register, { closeModal }] = useModalInner( async (data) => {
  database.value=data.database
  reload()
  console.log(data)
  fetch(database.value)
});


async function handleOk() {
  if(selectedRowData.value!==''){
    emit('throwData', unref(selectedRowData.value[0]));
    closeModal();
  }
}

function rowDbClick(data) {

}

const val = {
  id: '',
  uniqueCode: '',
  custName: '',
  uniqueCustclass: '',
  custCode: '',
  custAbbname: '',
  custSregcode: '',
  manageType: '',
  uniqueCodeCcus: '',
  uniqueCodeSupplier: '',
  address1: '',
  contacts: '',
  address2: '',
  telephone: '',
  countryName: '',
  cellPhoneNum: '',
  website: '',
  email: '',
  industryclassName: '',
  successState: '',
};
async function openAddPage() {
  let temp=await findAllByAccCountPartColumn(accId.value)
  openSavePage(true, {
    data: val,
    type: 'add',
    defaultPage:temp.independent=='1'?true:false, // 是否独立账套
    database:database.value,
    isEdit:true
  });
}


//js切割字符串
function setString(str, len) {
  var strlen = 0;
  var s = '';
  for (var i = 0; i < str.length; i++) {
    if (str.charCodeAt(i) > 128) {
      strlen += 2;
    } else {
      strlen += 1.2;
    }
    s += str.charAt(i);
    if (strlen >= len) {
      return s + '...';
    }
  }
  return s;
}

function createErrorPop(text) {
  createErrorModal({
    iconType: 'warning',
    title: '提示',
    content: text
  })
  return false
}

const {
  createConfirm,
  createWarningModal
} = useMessage()


function formatFlag(flag:any) {
  let str = '启用'
  switch (flag) {
    case '1':
      str = '启用'
      break
    case '0':
      str = '停用'
      break
  }
  return str
}

const formatFx = (o: any) => {
  let str = ''
  if(o === '1'){
    str = '固定换算率'
  }else if(o === '2'){
    str = '浮动换算率'
  }
  return str;
};

const treeselectedKeys = ref<string[]>([])
const treeexpandedKeys = ref<string[]>([])
const treeDatas = ref([])
async function fetch(accId) {
  treeDatas.value = []
  let deptTree = []

  deptTree.push({id: '1', key: '1', unitCode: 'k1', value: '单计量', children: []})
  deptTree.push({id: '2', key: '2', unitCode: 'k2', value: '多计量', children: []})

  treeDatas.value.push({id: '0', key: '0', unitCode: '0', value: '计量单位', children: deptTree})
  treeselectedKeys.value = ['k1']
  treeexpandedKeys.value = ['k1']
}

async function handleSelect(keys) {
  let data = keys[0];
  if (null != data) {
    if(data === 'k1'){
      //查询
      let d = await useRouteApi(findInAll, {schemaName: database})({unitName: pageParameter.selectValue})
      setColumns(CrudApi.columns);
      setTableData(d);
      pageParameter.typeCode = 'k1'
    }else if(data === 'k2'){
      let res = await useRouteApi(findInsAll, {schemaName: database})({unitName: pageParameter.selectValue})
      setColumns(CrudApi.columns);
      setTableData(res);
      pageParameter.typeCode = 'k2'
    }else{
      let res = await useRouteApi(findAllSysUnitOfMeaList, {schemaName: database.value})({
        manyCode: data.replace('k3',''),
        unitName: pageParameter.selectValue
      })
      setColumns(CrudApi.columns2);
      setTableData(res);
      pageParameter.typeCode = 'k3'
    }
  }
}

async function query() {
  reload()
}
async function add() {
  if (checkRow.value.length > 0) {
    createConfirm({
      iconType: 'error',
      title: '警告',
      content: '你确认要引用选中数据吗?',
      onOk: async () => {
        let arr = []
        for (let i = 0; i < checkRow.value.length; i++) {
          arr.push(checkRow.value[i])
        }
        //判断引入类型 单 多
        console.log(pageParameter.typeCode)
        if(pageParameter.typeCode === 'k1'){
          await introduceData(checkRow.value)

        }else{
          await introduceDatas(checkRow.value)
        }
        checkRow.value = []
        state.selectedRowKeys = []
        message.success('引用成功！')
        await reload()
        await fetch()
      },
      onCancel: () => {
        return false
      }
    })
  } else {
    createWarningModal({
      iconType: 'warning',
      title: '引用',
      content: '请选择需要引用的数据！'
    })
  }
}
</script>
<style>
.ant-modal-title{
  margin-top: -10px;
}
.ant-modal-header{
  height: 10px;
}
.scrollbar__view{
  overflow-y: hidden;
}
.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}

.ant-modal-body {
  padding: 0px;
  border: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
}

.scroll-container .scrollbar__wrap {
  margin-bottom: -5px !important;
}
</style>
<style lang="less" scoped>
:deep(.ant-table-thead th) {
  background-color: #d8d8d8 !important;
  font-weight: bold !important;
  border-left: 1px solid #d8d8d8 !important;
  border-bottom: 1px solid #d8d8d8 !important;
  border-top: 1px solid #d8d8d8 !important;
}
.bg-white{
  width: 220px ;
  min-height: 462px ;
  height: calc(100% - 462px);
  border: 1px #cccccc solid;
  background:white ;
}
.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}
:deep(.ant-tree .ant-tree-node-content-wrapper.ant-tree-node-selected),
:deep(.ant-table-tbody > tr.ant-table-row-selected > td){
  background-color: #1488b1;
  color: white;
}
</style>
