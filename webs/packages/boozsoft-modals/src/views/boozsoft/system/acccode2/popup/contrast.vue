<template>
  <BasicModal
    width="900px"
    :height=420
    v-bind="$attrs"
    title="科目对照"
    @ok="closeModal()"
    @register="register"
  >
    <div class="nc-open-content" style="height: 100%; overflow: hidden">
      <div class="open-content-up" style="font-weight: bolder; margin-top: -4px">
        <div>
          <a @click="cykmExcel">差异科目导出</a>
        </div>
        <span style="float: left;margin-left: 20%;font-size: 18px;">当前年度：{{ thisiyear}}</span>
        <span style="float: right;margin-right: 20%;font-size: 18px;">对比年度：{{upiyear}}</span>
        <a-table style="float: left;" :loading="loading1" :columns="tableColumns" :data-source="codelist" bordered :pagination="false" :scroll="{ x: 100, y: 320 }" :rowClassName="tableRow" :class="'a-table-font-size-12'" />
      </div>
    </div>
    <template #footer>
      <a-button @click="closeModal()" type="primary">关闭</a-button>
    </template>
  </BasicModal>
</template>
<script setup="props, { content }" lang="ts">
import {
  Input as AInput,
  Select as ASelect,
  Statistic as AStatistic,
  Table as ATable,
  Tabs as ATabs
} from 'ant-design-vue';
import {ref} from 'vue';
import {BasicModal, useModalInner} from '/@/components/Modal';
import {aoaToSheetXlsx} from "/@/components/Excel";

const ASelectOption = ASelect.Option;
const AInputSearch = AInput.Search;
const AStatisticCountdown = AStatistic.Countdown;
const ATabPane = ATabs.TabPane;
const emit=defineEmits(['register'])
const tableColumns = [
  {
    title: '科目编码',
    align: 'left',
    dataIndex: 'ccode2',
    width: 160,
  },
  {
    title: '科目名称',
    dataIndex: 'ccodeName2',
    align: 'left',
    width: 160,ellipsis: true,
  },
  {
    title: '方向',
    align: 'center',
    dataIndex: 'bprogerty2',
    width: 58,
  },
  {
    title: '类型',
    dataIndex: 'cclass2',
    width: 50,
  },
  {
    title: '科目编码',
    align: 'left',
    dataIndex: 'ccode',
    width: 160,
  },
  {
    title: '科目名称',
    dataIndex: 'ccodeName',
    align: 'left',
    width: 160,ellipsis: true,
  },
  {
    title: '方向',
    align: 'center',
    dataIndex: 'bprogerty',
    width: 58,
  },
  {
    title: '类型',
    dataIndex: 'cclass',
    width: 50,
  },
];
const codelist:any = ref([])    // 左侧为本年，右侧为上年
const loading1 = ref(true)
const thisiyear = ref('')
const upiyear = ref('')
const accNameAll = ref('')

const [register, {closeModal}] = useModalInner((data) => {
  loading1.value = true
  codelist.value=[]
  thisiyear.value=data.thisiyear
  upiyear.value=data.upiyear
  accNameAll.value=data.accNameAll

  let thisyearCodelist = data.thisyearCode
  let upyearCodelist = data.upyearCode
  // 当前年度科目数量大于对比年度
  if(thisyearCodelist.length > upyearCodelist.length){
    thisyearCodelist.forEach(v=>{
      let temp={
        ccode2:v.ccode,
        ccodeName2:v.ccodeName,
        bprogerty2:v.bprogerty==='1'?'借':'贷',
        cclass2:v.cclass,
        ccode:'',
        ccodeName:'',
        bprogerty:'',
        cclass:'',
        color:'1',
      }
      let updata=upyearCodelist.filter(u=>u.ccode===v.ccode);
      if(updata.length>0){
        temp.ccode=updata[0].ccode
        temp.ccodeName=updata[0].ccodeName
        temp.bprogerty=updata[0].bprogerty==='1'?'借':'贷'
        temp.cclass=updata[0].cclass
        temp.color='1'
      }else{
        temp.color='0'
      }
      codelist.value.push(temp)
    })
  }
  else{
    upyearCodelist.forEach(v=>{
      let temp={
        ccode:v.ccode,
        ccodeName:v.ccodeName,
        bprogerty:v.bprogerty==='1'?'借':'贷',
        cclass:v.cclass,
        ccode2:'',
        ccodeName2:'',
        bprogerty2:'',
        cclass2:'',
        color:'1',
      }
      let thisdata=thisyearCodelist.filter(u=>u.ccode===v.ccode);
      if(thisdata.length>0){
        temp.ccode2=thisdata[0].ccode
        temp.ccodeName2=thisdata[0].ccodeName
        temp.bprogerty2=thisdata[0].bprogerty==='1'?'借':'贷'
        temp.cclass2=thisdata[0].cclass
        temp.color='1'
      }else{
        temp.color='0'
      }
      codelist.value.push(temp)
    })
  }
  loading1.value = false
});
function tableRow(row: any, num: any) {
  let str = ''
  if (row.color == '0'){
    str = 'table-BGC-red'
  }
  return str
}
function cykmExcel() {
  let tempColumn=tableColumns.map(item=>item.title)
  let tempData=codelist.value.filter(item=>item.color==='0')
  let data:any=[]
  if(tempData.length>0){
    tempData.forEach(v=>{
      let temp:any=[]
      temp[0]=v.ccode2
      temp[1]=v.ccodeName2
      temp[2]=v.bprogerty2
      temp[3]=v.cclass2
      temp[4]=v.ccode
      temp[5]=v.ccodeName
      temp[6]=v.bprogerty
      temp[7]=v.cclass
      data.push(temp)
    })
  }
  aoaToSheetXlsx({
    data: data,
    header: tempColumn,
    filename: '科目对照_'+accNameAll.value+'【差异科目】.xlsx',
  });
}
</script>
<style lang="less" scoped>
:deep(.ant-table-thead th) {
  background-color: #d8d8d8 !important;
  font-weight: bold !important;
  border-left: 1px solid #d8d8d8 !important;
  border-bottom: 1px solid #d8d8d8 !important;
  border-top: 1px solid #d8d8d8 !important;
}

:deep(.table-BGC-red) td {
  background-color: #d01616;
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

.nc-open-content {
  input {
    width: 35%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  .ant-input:focus {
    box-shadow: none;
  }

  :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #ffffff !important;
  }

  label {
    text-align: right;
    width: 110px;
    display: inline-block;
    padding: 5px 10px;
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

.ant-modal-content {
  margin-top: 200px;
}


.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}
</style>
