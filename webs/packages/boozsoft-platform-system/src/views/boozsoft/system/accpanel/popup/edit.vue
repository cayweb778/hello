<template>
  <BasicModal
    destroyOnClose
    width="600px"
    v-bind="$attrs"
    title="编辑面板信息"
    @ok="handleOk()"
    @cancel="handleClose"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%"
    >
      <div class="open-content-up" style="margin-top: 20px;">
        <div style="text-align: center;">
          <label style="font-size: 18px;margin-left: 0;">显示名称：</label>
          <a-input v-model:value="formItems.panelName" class="abc" style="width: 65%" @blur="findByName123"/>
          <br/><br/>
          <span style="margin-left: -196px;">
            <label>取值方式：</label>
            <a-select v-model:value="formItems.panelDataRange" style="width: 150px">
              <a-select-option value="余额">余额</a-select-option>
              <a-select-option value="借方余额">借方余额</a-select-option>
              <a-select-option value="贷方余额">贷方余额</a-select-option>
              <a-select-option value="借方累计发生">借方累计发生</a-select-option>
              <a-select-option value="贷方累计发生">贷方累计发生</a-select-option>
            </a-select>
          </span>
            <br>
            <span style="margin-left: -196px;">
            <label>币种：</label>
              <a-select v-model:value="formItems.panelBzUnit" style="width: 150px">
                <a-select-option :value="item.foreignName" v-for="item in currencylist">{{item.foreignName}}</a-select-option>
            </a-select>
          </span>
            <br>
            <span style="margin-left: -196px;">
             <label>排序编号：</label>
            <a-select v-model:value="formItems.panelOrder" style="width: 150px">
              <a-select-option :value="item2" :key="item2" v-for="(item2, j) in order">
                {{item2}}
              </a-select-option>
            </a-select>
          </span>
          <br><br>
        </div>
        <span>
          <a-button type="link" @click="addRow">添加</a-button>
        </span>
        <a-table :scroll="{ y: 80 }" bordered :pagination=false :dataSource="tableDataSource" :columns="columns" :class="'a-table-font-size-12'">
          <template #ccode="{ record }">
            <a-select show-search v-model:value="record.ccode" :filter-option="filterOption" style="width: 300px;">
              <a-select-option :value="item2.ccode" :key="item2.ccode +'-'+ item2.ccodeName" v-for="(item2, j) in codelist">
                {{item2.ccode +'-'+ item2.ccodeName}}
              </a-select-option>
            </a-select>
            &nbsp;&nbsp;
            <a @click="openKemu(record)"><LinkOutlined/></a>
          </template>
          <template #fuhao="{ record }">
            <a-select v-model:value="record.fuhao" style="width: 100px">
              <a-select-option value="+">+</a-select-option>
              <a-select-option value="-">-</a-select-option>
            </a-select>
          </template>
          <template #caozuo="{ record }">
            <DeleteOutlined @click="delRow(record.ccode)" title="删除"/>
          </template>
        </a-table>
        <br>
      </div>
      <ModalAllPop @throwData="modalAllData" @register="registerCodeAllPopPage" />
    </div>
  </BasicModal>
</template>

<script setup="props, {content}" lang="ts">
import {reactive, ref, unref} from 'vue'
import {BasicModal, useModal, useModalInner} from '/@/components/Modal'
import { DeleteOutlined,LinkOutlined } from '@ant-design/icons-vue';
import {
  Table as ATable,
  Select as ASelect,
  Input as AInput,
} from 'ant-design-vue';
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {
  company_treeCode,
  findAllByCurrency,
  findByIyearOrderByCcode
} from "/@/api/codekemu/codekemu";
import {getCurrentAccountName, hasBlank} from "/@/api/task-api/tast-bus-api";
import {useUserStore, useUserStoreWidthOut} from "/@/store/modules/user";
import {useMessage} from "/@/hooks/web/useMessage";
import {countByPanelName} from "/@/api/record/accpanel/data";

import ModalAllPop from '/@/views/boozsoft/system/acccode2/popup/modalAllPop.vue';

const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const emit = defineEmits(['register']);
const {createConfirm, createWarningModal} = useMessage();
const userStore = useUserStore();
const sysuserlist=ref([]) // 操作员
const codelist=ref([])
const currencylist=ref([])
const database=ref(getCurrentAccountName(true))
const accId=ref('')
const order:any=ref( [])
const tableDataSource:any=ref( [])
const columns:any=ref( [
  {
    title: '会计科目',
    dataIndex: 'ccode',
    key: 'ccode',
    align:'center',
    slots: { customRender: 'ccode' },
  },
  {
    title: '符号',
    dataIndex: 'fuhao',
    width:150,
    key: 'fuhao',
    align:'center',
    slots: { customRender: 'fuhao' },
  },{
    title: '操作',
    dataIndex: 'caozuo',
    key: 'caozuo',
    width:50,
    align:'center',
    slots: { customRender: 'caozuo' },
  },
])
const formItems:any = ref({
  id: '',
  caozuoUnique: '',
  panelIyear: '',
  panelName: '',
  panelFormula: '',
  panelDataRange: '',
  panelBz: '',
  panelBzUnit: '',
  panelFlag: '',
  panelOrder: '',
  splitCode: '',
  splitFuhao: '',
})
const oldName=ref('')
const [registerCodeAllPopPage, { openModal: openCodeAllPopPage }] = useModal();
const [register, { closeModal }] = useModalInner(async(data) => {
  handleClose()
  // 赋值
  if(data.data!==''){
    formItems.value.id = data.data.id
    formItems.value.panelName = data.data.panelName
    formItems.value.panelDataRange =data.data.panelDataRange
    formItems.value.panelBz = data.data.panelBz
    formItems.value.panelBzUnit = data.data.panelBzUnit
    formItems.value.panelOrder = data.data.panelOrder
    oldName.value=data.data.panelName
    tableDataSource.value=[]
    if(data.data.panelFormula!==null){
      for (let i = 0; i < data.data.splitCode.split(",").length; i++) {
        tableDataSource.value.push({
          key:i+'a',
          ccode:data.data.splitCode.split(",")[i],
          fuhao:data.data.splitFuhao.split(",")[i]
        })
      }
    }
  }
  else{
    formItems.value.panelDataRange='余额'
    formItems.value.panelOrder=data.maxorder
    formItems.value.id=null
  }

  formItems.value.caozuoUnique = data.caozuoUnique
  formItems.value.panelIyear = data.panelIyear
  formItems.value.panelFlag = data.panelFlag
  database.value=data.database
  accId.value=data.accId
  order.value=data.order
  // 获取所有国币-读取常用外币表
  await useRouteApi(findAllByCurrency, {schemaName: database})('').then((res) => {
    currencylist.value = res;
  });

  // 会计科目
  await useRouteApi(findByIyearOrderByCcode,{schemaName:database})({iyear:data.panelIyear}).then((res) => {
    codelist.value = res;
  });
})

const rowKey=ref('')
function openKemu(rowData) {
  rowKey.value=rowData.key
  openCodeAllPopPage(true, {
    database: database.value,
    accId: accId.value,
    iyear: formItems.value.panelIyear,
  })
}

function modalAllData(data) {
  let temp=tableDataSource.value.filter(a=>a.key==rowKey.value)
  temp[0].ccode=data.ccode
}


async function addRow() {
  tableDataSource.value.push({
    key:tableDataSource.value.length+1,
    ccode:'',
    fuhao:'+'
  })
}
function delRow(data) {
  var index = tableDataSource.value.findIndex(item => {
    if ( item.ccode == data) {
      return true;
    }
  })
  tableDataSource.value.splice(index,1)
}
async function handleOk() {
  if(formItems.value.panelName===''){
   return createConfirmPop('请填写显示名称')
  }
  if(tableDataSource.value.length===0){
    return createConfirmPop('请选择科目')
  }
  if(oldName.value!==''){
    if(oldName.value !== formItems.value.panelName){
      if(!await findByName()){
        return false;
      }
    }
  }

  var a=true
  var panelFormula='';
  var splitCode:any=[];
  var splitFuhao:any=[];
 tableDataSource.value.findIndex(item => {
    if ( item.ccode == '') {
      createConfirmPop('请选择科目')
      a=false;
      return;
    }
     if ( item.fuhao == '') {
       createConfirmPop('请选择符号')
       a=false;
       return;
     }
     splitCode.push(item.ccode)
     splitFuhao.push(item.fuhao)
     panelFormula+=item.ccode+item.fuhao
  })

  if(a){
    formItems.value.splitCode = splitCode.toString()
    formItems.value.splitFuhao = splitFuhao.toString()
    formItems.value.panelType = 'zz'
    formItems.value.panelFormula = panelFormula.substring(0,panelFormula.length-1)
    emit('save', unref(formItems))
    closeModal()
    return true;
  }
}
const filterOption = (input: string, option: any) => {
  // 不是数字
  if(isNaN(input)){
    return option.key.toLowerCase().indexOf(input.toLowerCase()) >= 0;
  }else{
    return option.value.toLowerCase().startsWith(input.toLowerCase());
  }
};
function handleClose() {
  formItems.value.panelName=''
  formItems.value.panelDataRange='余额'
  formItems.value.panelBzUnit=''
  formItems.value.panelOrder='1'
  tableDataSource.value=[]
}
function createConfirmPop(text) {
  createConfirm({
    iconType: 'warning',
    title: '警告',
    content: text,
    onOk: async () => {}
  })
}

async function findByName123() {
  if(oldName.value===''){
    findByName()
  }else{
    if(oldName.value!==formItems.value.panelName){
      findByName()
    }
  }
}
async function findByName() {
  let a=true;
  let item= await useRouteApi(countByPanelName,{schemaName:database})({name:formItems.value.panelName})
  if(item>0){
    a=false;
    createConfirmPop('名称已存在，请重新输入！')
  }
  return a;
}
</script>
<style lang="less" scoped>
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
    font-weight: bold;
    font-size: 14px;
  }

  .abc{
    border-bottom: 2px solid #666666 !important;
    font-size: 18px;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  label {
    text-align: left;
    width: 110px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    color: #535353;
    font-size: 13px;
    margin-left: 1em;
    font-weight: bold;
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

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}
</style>
