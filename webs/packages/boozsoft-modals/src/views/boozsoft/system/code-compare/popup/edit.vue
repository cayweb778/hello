<template>
  <BasicModal
    width="700px"
    v-bind="$attrs"
    title="科目对照"
    @ok="handleOk()"
    @register="register"
  >
    <div
      class="nc-open-content"
      style="height: 100%">
      <div class="open-content-up" >
          <label>年度:</label>
        <a-input :disabled="true" v-model:value="iyear" style="text-align: center;width: 100px;" placeholder="" />

        <a-checkbox :disabled="true" v-model:checked="isame"/>辅助核算项必须一致

        <br><br>
       财务会计科目:
        &nbsp;&nbsp;&nbsp;&nbsp;
        科目分类：
          <a-select
            v-model:value="cclass"
            style="width: 30%"
            @change="handleChange"
          >
            <a-select-option v-for="d in cclasslist" :value="d">
              {{ d }}
            </a-select-option>
          </a-select>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <a-input-search v-model:value="inputValue" style="width: 25%;border: none;border-bottom: 1px solid #bdb9b9;" placeholder="编码/名称" @search="inputSearch"/>

        <a-table style="margin-top: 10px;" :dataSource="cwcodelist" :loading="loadMark"
                 :columns="[
                            { title: '科目编码',
                              dataIndex: 'ccode',
                            },
                            { title: '科目名称',
                              dataIndex: 'ccodeName',
                              ellipsis:true
                            },{ title: '科目类型',
                              dataIndex: 'cclass',
                              width:90,
                              align:'center'
                            },{ title: '辅助项',
                              dataIndex: 'fuzhu',
                              ellipsis:true
                            }
                          ]"
                 :class="'a-table-font-size-12'"
                 :rowKey="r=>r.sourceCode"
                 :pagination="false" :scroll="{ y: 170 }"
                 :row-selection="{ type: 'checkbox',selectedRowKeys: selectedRowKeys1, onChange: onSelectChange }"
        />
        <br/>
        <label>预算会计科目:</label>
        <a-select v-model:value="ysccode"
                  show-search
                  :dropdownStyle="{height:'140px'}"
                  :filter-option="filterOption"
                  @change="ysChange"
                  placeholder="预算会计科目"
                  style="width: 50%">
          <a-select-option v-for="item in yscodelist" :key="item.ccode+'_'+item.ccodeName" :value="item.ccode">
            {{ item.ccode }}-{{ item.ccodeName }}
          </a-select-option>
        </a-select>
        <br>
        &nbsp;&nbsp;&nbsp;辅助项：<span v-if="ysccodeFuzhu.length>0">{{ysccodeFuzhu}}</span>
      </div>
    </div>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import {ref,unref} from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {Select as ASelect,Checkbox as ACheckbox,Table as ATable,Input as AInput } from 'ant-design-vue'
import {useMessage} from "/@/hooks/web/useMessage";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {companyfindAll} from "/@/api/codekemu/codekemu";
import {findByAccId, findDataBase} from "/@/api/record/system/account";

const {createConfirm, createWarningModal, createMessage} = useMessage();
const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option
const {
  createErrorModal
} = useMessage()
const emit=defineEmits(['register','save'])
const inputValue:any = ref('')
const iyear:any = ref('')
const itype:any = ref('')
const sourceCode:any = ref('')
const ccodeDataList:any = ref([])
const selectedRowKeys1:any = ref([])
const selectedRowData:any = ref([])
const ysccode:any = ref('')
const formItems:any = ref({})
const cwcodelist:any = ref([])
const oldcwcodelist:any = ref([])
const yscodelist:any = ref([])
const cclasslist:any = ref([])
const ysccodeFuzhu:any = ref('')
const isame = ref(false)
const loadMark = ref(true)
const cclass = ref('')
const sameSource = ref('')
const dynamicTenantId = ref()
const accId = ref('')
const [register, { closeModal }] = useModalInner(async (data:any) => {
  ysccode.value=''
  cwcodelist.value=[]
  yscodelist.value=[]
  selectedRowKeys1.value=[]
  selectedRowData.value=[]

  dynamicTenantId.value = data.dynamicTenantId
  accId.value = data.accId
  iyear.value =data.iyear
  itype.value =data.type
  sourceCode.value =data.sourceCode
  ccodeDataList.value =data.data
  ysccodeFuzhu.value=[]
  selectedRowData.value=[]
  findAll()
})
async function handleChange(a,b) {
  let temp=oldcwcodelist.value
  cwcodelist.value=temp.filter(v=>v.cclass===a)
}

async function findAll() {
  loadMark.value=true
  cwcodelist.value=[]
  // 获取科目
  let map={
    cclass:'全部',
    databasenum:dynamicTenantId.value,
    searchConditon:{
      requirement:'',
      value:''
    }
  }
  let findcodelist=await useRouteApi(companyfindAll,{schemaName: dynamicTenantId})(map)
  if(findcodelist.items.length>0){
    let pxjz= findcodelist.items.filter(v=>v.pxjz==='1')
    let yusuan= findcodelist.items.filter(v=>v.yusuan==='1')

    let dataCheck=[]
    let dataAllCheck=[]
    let delNoTableCheck:any=[]
    let targetCode:any=''
    if(itype.value==='edit'){
      dataCheck=sourceCode.value.split(',')
      dataAllCheck=ccodeDataList.value.map(v=>v.sourceCode)
      targetCode=[...new Set(ccodeDataList.value.map(v=>v.targetCode))]
      ysccode.value=targetCode.join(',')
      sameSource.value=[...new Set(ccodeDataList.value.map(v=>v.sameSource))].join(',')
      // 修改：去除表格没有选中的数据
      delNoTableCheck=dataCheck.filter(v=>dataAllCheck.indexOf(v)==-1)
      pxjz=pxjz.filter(v=>delNoTableCheck.indexOf(v.ccode)==-1)
      dataAllCheck.forEach(v=>{
        const index = pxjz.findIndex((item) => {
          return item.ccode === v;
        })
        const data = pxjz.find((item) => {
          return item.ccode === v;
        })
        selectedRowKeys1.value.push(index)
        selectedRowData.value.push(data)
      })
    }
    pxjz.forEach((v,index)=>{
      cclasslist.value.push(v.cclass)
      // add:去除已经选过的
      if(itype.value==='add'){
        if(sourceCode.value.split(',').indexOf(v.ccode)<0){
          cwcodelist.value.push(
            {
              key:v.id,
              id:v.id,
              ccode:v.ccode,
              ccodeName:v.ccodeName,
              cclass:v.cclass,
              fuzhu:v.fuzhu,
            }
          )
        }
      }
      else{
        cwcodelist.value.push(
          {
            key:v.ccode,
            id:v.id,
            ccode:v.ccode,
            ccodeName:v.ccodeName,
            cclass:v.cclass,
            fuzhu:v.fuzhu,
          }
        )
      }
    })
    yscodelist.value=yusuan
  }
  // 重新查询账套编码信息
  await findByAccId(accId.value).then(item=>{
    isame.value=item.isame==='1'?true:false
    // console.log(JSON.stringify(item))
  })

  cclasslist.value=[...new Set(cclasslist.value)]
  oldcwcodelist.value=cwcodelist.value
  if(cclasslist.value.length>0){
    cclass.value=cclasslist.value[0]
    let temp=cwcodelist.value
    cwcodelist.value=temp.filter(v=>v.cclass===cclass.value)
  }
  loadMark.value=false
}

function findByCoodeList(cclass) {
  let temp=cwcodelist.value
  cwcodelist.value=temp.filter(v=>v.cclass===cclass)
}

function inputSearch() {
  loadMark.value=true
  let temp=oldcwcodelist.value
  if(inputValue.value===''){
    cwcodelist.value=temp
  }else{
    cwcodelist.value=temp.filter(v=>v.ccode.startsWith(inputValue.value) || v.ccodeName.startsWith(inputValue.value))
  }
  loadMark.value=false
}
const onSelectChange = (a,b) => {
  selectedRowKeys1.value=a
  selectedRowData.value=b
}
function createConfirmPop(text) {
  createConfirm({
    iconType: 'warning',
    title: '警告',
    content: text,
    onOk: async() => {}
  })
}
function ysChange() {
  let temp=yscodelist.value.filter(v=>v.ccode===ysccode.value)
  ysccodeFuzhu.value=temp.map(item=>item.fuzhu).join(',')
}
async function handleOk() {
  if(selectedRowKeys1.value.length===0){
    createConfirmPop('至少选择一条财务会计科目')
    return false;
  }
  if(ysccode.value===''){
    createConfirmPop('请选择预算科目')
    return false;
  }
  // 辅助核算项必须一致
  if(isame.value){
    let notfuzhuequal:any=[]
    // 预算科目辅助项
    let ysfuzhuArr=ysccodeFuzhu.value.split(',')
    // 财务科目辅助项
    let cwfuzhuList=selectedRowData.value
    ysfuzhuArr.forEach(ys=>{
      if(ys!==''){
        cwfuzhuList.forEach(cw=>{
          let fuzhu=cw.fuzhu.split(',')
          // 财务科目辅助项 必须 大于等于 预算科目辅助项
          if(fuzhu.length >= ysfuzhuArr.length){
            fuzhu.forEach(f=>{
              let temp=ysfuzhuArr.filter(a=>a===f)
              if(temp.length==0){
                notfuzhuequal.push(cw.ccode)
              }
            })
          }else{
            notfuzhuequal.push(cw.ccode)
          }
        })
      }
    })
    let temp=[...new Set(notfuzhuequal)]
    if(temp.length>0){
      createConfirmPop('【'+temp.join(',')+'】与预算科目辅助项不一致!!')
      return false
    }
  }
  let idlist:any=[]
  selectedRowData.value.forEach(v=>{
    idlist.push(v.ccode)
  })
  formItems.value.sourceCode=idlist.join(',')
  formItems.value.targetCode=ysccode.value
  formItems.value.iyear=iyear.value
  formItems.value.sameSource=sameSource.value
  emit('save', unref(formItems))
  closeModal()
  return true
}
function handleCancel(){
  return true;
}

const filterOption = (input: string, option: any) => {
  return option.key.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};
</script>
<style lang="less" scoped>
.vben-basic-title {
  color: rgb(1, 129, 226) !important;
}
:deep(.ant-checkbox) {
  border: 1px solid #2f2a2a;
  border-radius: 4px;
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

 :deep(.ant-select-selector) {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
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

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
}
</style>
