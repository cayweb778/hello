<template>
  <BasicModal
    width="900px"
    :minHeight="500"
    :height="500"
    v-bind="$attrs"
    :closable="false"
    okText="开始引入"
    @ok="handleOk()"
    @register="register"
  >
    <template #title>
      <div style="display: flex;" class="vben-basic-title">
        <span style="line-height: 25px;font-size: 20px;">
          <SearchOutlined style="width:25px;margin-right: 10px;"/>
          引入组织会计科目
        </span>
      </div>
    </template>
    <Loading :loading="compState.loading" :absolute="compState.absolute" :tip="compState.tip" />
    <div style="background-color: #158eb8;height: 50px;padding-top: 10px;padding-left:10px;border-radius: 5px;">
      <div style="float: right;margin-right: 5px;">
        <button type="button" class="ant-btn ant-btn-me" @click="reload"><span>刷新</span></button>
      </div>
    </div>
    <div style="display: inline;width: 100%;float: right;margin-top: 5px;">
      <a-transfer
        v-model:target-keys="targetKeys"
        v-model:selected-keys="selectedKeys"
        :data-source="mockData"
        show-search
        :titles="['  未选择', '  已选择']"
        :filter-option="filterOption"
        :render="item => item.ccode+'_'+item.ccodeName"
      />
    </div>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
import {SearchOutlined} from '@ant-design/icons-vue';
import {ref, reactive, toRaw} from 'vue';
import {BasicModal, useModalInner} from '/@/components/Modal';
import {
  Transfer as ATransfer,
  Select as ASelect,
  Form as AForm,
  Input as AInput,
  Statistic as AStatistic,
} from 'ant-design-vue';
import {useMessage} from "/@/hooks/web/useMessage";
import {findAllByOrgCodeKeMu} from "/@/api/acctemplate/acctemplate";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {
  companyfindAll,
  findByOrgKemuCodeToCode
} from "/@/api/codekemu/codekemu";

/******************* 弹框加载中 **************************/
import { Loading } from '/@/components/Loading';
import {
  currentAccountTypes,
  filterAccListByAuth,
  getAdInfoDatas,
  getAllAccAuths
} from "/@/api/record/system/financial-settings";
import {findByAccIdAndIyear} from "/@/api/record/system/sys_data_auth_swith";
import TimeTool from "/@/boozsoft/components/SelectTimeTools/TimeTool.vue";
const loadingRef = ref(false);
const compState = reactive({
  absolute: false,
  loading: false,
  tip: '加载中...',
});
function openCompFullLoading() {
  openLoading(false);
}
function openLoading(absolute: boolean) {
  compState.absolute = absolute;
  compState.loading = true;
}
/*******************END**************************/

const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const AFormItem = AForm.Item;
const {createWarningModal} = useMessage()
const emit = defineEmits(['register']);
const orgUnique = ref('');
const iyear = ref('');
const database = ref('');
const targetKeys = ref([]);
const selectedKeys = ref([]);
const mockData = ref([]);
const ccodeFirst = ref('');


// 这是示例组件
const [register, {closeModal}] = useModalInner(async (data) => {
  orgUnique.value=data.orgUnique
  iyear.value=data.iyear
  database.value=data.database
  ccodeFirst.value=data.ccodeFirst

  mockData.value=[]
  targetKeys.value=[]
  selectedKeys.value=[]
  findByOrgKemu()
});

async function handleOk() {
  openCompFullLoading()
  if(targetKeys.value.length==0){
    return createWarningModal({ content: '请选择需要引入的科目!' });
  }
  let bringArr:any=[]
  for (let i = 0; i < targetKeys.value.length; i++) {
    let a=targetKeys.value[i]
    let temp=mockData.value.filter(b=>b.key===a)
    for (let j = 0; j < temp.length; j++) {
      let c:any=temp[j]
      if(c.superiorCcode!=='0'){
        // 1级科目编码
        let ccode1=c.superiorCcode.substring(0,ccodeFirst.value)
        // 上级科目 至 本科目
        let ccodeToccode=await findByOrgKemuCodeToCode(orgUnique.value,iyear.value,ccode1,c.ccode)
        ccodeToccode.forEach(ctc=>{
          bringArr.push(ctc)
        })
      }
      else{
        let orgKemu= await findAllByOrgCodeKeMu('全部',iyear.value,orgUnique.value)
        let byOrgKemu=orgKemu.filter(org=>org.ccode==c.ccode)
        bringArr.push(byOrgKemu[0])
      }
    }
  }
  // 数据去重
  var result:any = [];
  var obj = {};
  for(var i =0; i<bringArr.length; i++){
   if(!obj[bringArr[i].ccode]){
     bringArr[i].id=null
    result.push(bringArr[i]);
    obj[bringArr[i].ccode] = true;
   }
  }

  // 账套科目
  let map={
    searchConditon: {
      requirement: 'ccode',
      value: '',
    },
    cclass:'全部',
    databasenum:database.value,
    iyear:iyear.value,
  }
  let accKemu=await useRouteApi(companyfindAll, { schemaName: database })(map)

  let arr:any=[]
  for (let i = 0; i < result.length; i++) {
    if(result[i].bend=='0'){
      // 去除已经存在的非末级
      let temp=accKemu.items.filter(a=>a.ccode==result[i].ccode)
      if(temp.length==0){
        arr.push(result[i])
      }
    }else{
      arr.push(result[i])
    }
  }
  compState.loading = false;
  emit('save', toRaw(arr));
  closeModal();
}

// 组织科目
async function findByOrgKemu() {
  openCompFullLoading()
  let arr:any=[]
  let orgKemu= await findAllByOrgCodeKeMu('全部',iyear.value,orgUnique.value)
  // 账套科目
  let map={
    searchConditon: {
      requirement: 'ccode',
      value: '',
    },
    cclass:'全部',
    databasenum:database.value,
    iyear:iyear.value,
  }
  let accKemu=await useRouteApi(companyfindAll, { schemaName: database })(map)

  orgKemu.filter(a=>a.bend=='1').forEach((a,index)=>{
    // 账套中没有的科目
    let temp=accKemu.items.filter(b=>b.ccode==a.ccode)
    if(temp.length==0){
      arr.push({
        key:index,
        ccode:a.ccode,
        ccodeName:a.ccodeName,
        superiorCcode:a.superiorCcode,
      })
    }
  })
  mockData.value=arr
  compState.loading = false;
}

// 两个数组不相同的
function getArrNoEqual(arr1, arr2) {
  return arr1.concat(arr2).filter(function (v, i, arr) {
    return arr.indexOf(v) !== arr.lastIndexOf(v);
  });
}

function filterOption(input, option) {
  // 不是数字
  if(isNaN(input)){
    return option.ccodeName.indexOf(input) >= 0
  }else{
    return option.ccode.startsWith(input);
  }
}
</script>
<style>
.ant-modal-title {
  margin-top: -10px;
}

.ant-modal-header {
  height: 10px;
}

.scrollbar__view {
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
:deep(.ant-transfer-list) {
  display: flex;
  flex-direction: column;
  width: 430px;
  height: 440px;
  border: 1px solid #EEEEEE;
  border-radius: 2px;
}
:deep(.ant-table-thead th) {
  background-color: #d8d8d8 !important;
  font-weight: bold !important;
  border-left: 1px solid #d8d8d8 !important;
  border-bottom: 1px solid #d8d8d8 !important;
  border-top: 1px solid #d8d8d8 !important;
}

.bg-white {
  width: 220px;
  min-height: 462px;
  height: calc(100% - 462px);
  border: 1px #cccccc solid;
  background: white;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}

:deep(.ant-tree .ant-tree-node-content-wrapper.ant-tree-node-selected),
:deep(.ant-table-tbody > tr.ant-table-row-selected > td) {
  background-color: #1488b1;
  color: white;
}
</style>
