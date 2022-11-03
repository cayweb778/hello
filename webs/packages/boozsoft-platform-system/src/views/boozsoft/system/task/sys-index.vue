<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <img :src="getThisIndexImg()" class="container-head-img" draggable="false">
        <div class="container-head-title">
          <b class="noneSpan">操作锁定管理</b>
        </div>
        <div
          class="ant-btn-group"
          data-v-a1ccd506=""
          style="float: right"
        >
          <button
            type="button"
            class="ant-btn"
            @click="delList()"
          ><span>清除任务</span></button>
          <button
            type="button"
            class="ant-btn"
            @click="delState()"
          ><span>清除异常</span></button>
        </div>
      </div>
      <div class="app-container-neck">
        <div style="float:left;color: red;">
          &nbsp;超过&nbsp;<a-input v-model:value="overtime.time" style="width: 80px;height: 25px;margin-top: 5px;" @pressEnter="enterOverTime"/>&nbsp;分钟，将设置成异常状态
        </div>
        <div style="float: right; margin-left: 10px">
          <a-button class="ant-btn-me" @click="reload()">
            <SyncOutlined :style="{ fontSize: '14px' }" />
          </a-button>
          <a-button>
            <FilterFilled :style="{ fontSize: '14px' }" />
          </a-button>
        </div>
        <div style="float: right; position: relative">
          <!-- 搜索 账套编码、名称、操作员、年度、状态-->
          <a-input-search
            v-model:value="pageParameter.searchValue"
            placeholder=""
            style="width: 200px; border-radius: 4px"
          />
        </div>
      </div>
    </div>
    <div class="app-container">
      <BasicTable
        :row-selection="{ type: 'checkbox', selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange }"
        @register="registerTable"
        @fetch-success="editState"
      >
        <template #state="{ record }">
          <a-tag :color="record.state === '1' ? 'green' : 'volcano'">
            {{ record.state === '1' ? '正常' : '异常' }}
          </a-tag>
        </template>

      </BasicTable>
    </div>
  </div>
</template>
<script setup="props, {emit}" lang="ts">
import {
  getTaskList,
  deleteByState,
  deleteList,
  GroupAccuracyOverTime,
  saveGroupAccuracyOverTime,
  editGroupAccuracyOverTime,
  delAcclotList,
  editAcclotStateList
} from '/@/api/record/system/task'
import { BasicTable, useTable } from '/@/components/Table'
import { useModal } from '/@/components/Modal'
import {
  CaretDownFilled,
  FormOutlined,
  DeleteOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled
} from '@ant-design/icons-vue'
import {Tag as ATag,Select as ASelect,Popover as APopover,Input as AInput,Button as AButton, message} from 'ant-design-vue'
import {columnProps} from "ant-design-vue/es/table/interface";
import {reactive, ref,onMounted} from "vue";
import {useMessage} from "/@/hooks/web/useMessage";
import {
  getCurrentAccountName, hasBlank, pointMessage, getThisIndexImg
} from "/@/api/task-api/tast-bus-api";
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option
const overtime=reactive({
  id:'',
  time:'30'
})
const pageParameter = reactive({
  searchValue: '',
  accGroup:'',
  overtime:'0'
})
const {
  createErrorModal,
  createSuccessModal,
  createConfirm
} = useMessage()
const CrudApi = {
  list: getTaskList,
  columns: [
    {
      title: 'ID',
      dataIndex: 'id',
      defaultHidden: true,
      ellipsis: true
    },
    {
      title: '公司代码',
      dataIndex: 'companyCode',
      ellipsis: true,
      width: 80,
    },
    {
      title: '账套名称',
      dataIndex: 'companyName',
      ellipsis: true,
      align:'left'
    },
    {
      title: '操作员',
      dataIndex: 'caozuoUnique',
      ellipsis: true,
      align:'left'
    },
    {
      title: '最后操作时间',
      dataIndex: 'time',
      ellipsis: true,
      align:'left'
    },
    {
      title: '功能模块',
      dataIndex: 'functionModule',
      ellipsis: true,
      align:'left'
    },
    {
      title: '操作动作',
      dataIndex: 'method',
      ellipsis: true,
      align:'left'
    },
    {
      title: '档案编码',
      dataIndex: 'recordNum',
      ellipsis: true,
      align:'left'
    },
    {
      title: '任务年度',
      dataIndex: 'iyear',
      ellipsis: true,
      width: 80
    },
    {
      title: '任务月份',
      dataIndex: 'imonth',
      ellipsis: true,
      width: 80
    },
    {
      title: '状态',
      dataIndex: 'state',
      ellipsis: true,
      width: 80,
      slots: { customRender: 'state' }
    }
  ],
  editData: {
    id: '',
    caozuoUnique: '',
    time: '',
    functionModule: '',
    recordNum: '',
    state: '',
    iyear: '',
    imonth: '',
    method: ''
  }
}
// 这是示例组件
const [registerTable, { reload,getDataSource }] = useTable({
  api: CrudApi.list,
  columns: CrudApi.columns,
  bordered: true,
  showIndexColumn: false,
  pagination: {
    pageSize: 200,
    showSizeChanger: true,
    pageSizeOptions: ['200', '500', '1000', '1500'],
    showTotal: t => `总共${t}条数据`
  },
  searchInfo: pageParameter
})
const val = {
  id: '',
  caozuoUnique: '',
  time: '',
  functionModule: '',
  recordNum: '',
  state: '',
  iyear: '',
  imonth: '',
  method: ''
}
onMounted(async ()=>{
  let test=await GroupAccuracyOverTime()
  if(test!==''){
    overtime.id=test.id
    overtime.time=test.decimalPlaces
    pageParameter.overtime=test.decimalPlaces
  }else{
    await saveGroupAccuracyOverTime(overtime.time)
  }
  reload()
})
async function editState() {
  let idlist=[]
  let data=getDataSource()
  let findByData=data.filter(v=>v.state==='0')
  if(findByData.length>0){
    findByData.forEach(v=>{
      idlist.push(v.id)
    })
  }
  if(idlist.length>0){
    await editAcclotStateList(idlist.join(','))
  }
}
function add0(m){return m<10?'0'+m:m }
function timeformat(shijianchuo)
{
//shijianchuo是整数，否则要parseInt转换
  var time = new Date(shijianchuo);
  var y = time.getFullYear();
  var m = time.getMonth()+1;
  var d = time.getDate();
  var h = time.getHours();
  var mm = time.getMinutes();
  var s = time.getSeconds();
  return y+'-'+add0(m)+'-'+add0(d)+' '+add0(h)+':'+add0(mm)+':'+add0(s);
}
async function delState() {
  await deleteByState()
  createSuccessModal({
    iconType: 'success',
    title: '清除异常',
    content: '清除异常成功！'
  })
  reload()
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
const checkRow = ref([])
const onSelectChange = (selectedRowKeys,row) => {
  // console.log('selectedRowKeys changed: ', row);
  state.selectedRowKeys = selectedRowKeys;
  checkRow.value = row
};
//批量删除
async function delList(){
  let num = 0
  if (checkRow.value.length>0) {
    checkRow.value.forEach(
      function (item){
        if (item.state==1){
          num++
        }
      }
    )
    if (num>0){
      createConfirm({
        iconType: 'warning',
        title: '清除任务',
        content: '清除任务可能会影响当前用户的正常操作，确定要清除吗?',
        onOk: async() => {
          await deleteList(checkRow.value)
          reload()
        }
      })
    } else {
      await deleteList(checkRow.value)
      reload()
    }
  } else {
    createErrorModal({
      iconType: 'warning',
      title: '清除任务',
      content: '请至少选择一条记录进行清除！'
    })
  }
}

// 超时回车键
const enterOverTime = async () => {
  if(overtime.time!==''){
      await editGroupAccuracyOverTime(overtime.id,overtime.time)
      message.success('设置成功')
    reload()
  }
}
</script>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style><style lang='less' scoped ></style>
<style scoped lang="less">
:deep(.ant-card-body) {
  padding: 16px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
}
.app-container:nth-of-type(1) {
  background-color: #f2f2f2;
  padding: 10px 5px;
  margin: 10px 10px 5px;
}
.app-container:nth-of-type(2) {
  padding: 0px;
  margin: 5px 10px;
  background: #b4c8e3 !important;
}
</style>
