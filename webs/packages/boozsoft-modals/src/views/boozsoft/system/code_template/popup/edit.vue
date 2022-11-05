<template xmlns="">
  <BasicModal
    width="800px"
    v-bind="$attrs"
    title="会计科目导入模板"
    @ok="handleOk()"
    @cancel="closeOk()"
    @register="register"
  >
    <template v-if="openType=='add'" #title>
      <div style="display: flex;color: #0096c7;">
        <span style="line-height:40px;font-size: 28px;">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>
        </span>
        <span style="line-height:40px;font-size: 28px;margin-top:-5px;">
          &nbsp;&nbsp;会计科目导入模板
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/cus.png" style="height:60px;margin-right: 55px;"/>
      </div>
    </template>
    <template v-if="openType=='edit'" #title>
      <div style="display: flex;color: #0096c7;">
        <span style="line-height:40px;font-size: 28px;">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>
        </span>
        <span style="line-height:40px;font-size: 28px;margin-top:-5px;">
          &nbsp;&nbsp;会计科目导入模板
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/cus.png" style="height:60px;margin-right: 55px;"/>
      </div>
    </template>
    <template v-if="openType=='look'" #title>
      <div style="display: flex;color: #0096c7;">
        <span style="line-height:40px;font-size: 28px;">
          <FileSearchOutlined style="font-size: 34px;font-weight: bold"/>
        </span>
        <span style="line-height:40px;font-size: 28px;margin-top:-5px;">
          &nbsp;&nbsp;会计科目导入模板
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 40px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/cus.png" style="height:60px;margin-right: 55px;"/>
      </div>
    </template>

    <div :class="isEdit?'nc-open-content':'nc-open-show-content'" >
      <div class="open-content-up">
        <label>模板名称</label>
        <a-input @blur="findByName()" v-model:value="formItems.tname" placeholder="请输入模板名称" style="font-size: 16px;width: 200px;" />
        <label style="width: 200px">来源软件版本</label>
        <a-input placeholder="请输入来源软件版本" v-model:value="formItems.remark" style="font-size: 16px;width: 200px;" />
        <a-checkbox v-if="formItems.id!==undefined" v-model:checked="flag123">封存</a-checkbox>
        <br/><br/>

        <div style="border-bottom: 1px solid rgb(1, 129, 226)">
          格式设置
        </div>
        <div style="margin-top: 5px;">
          <label style="width: 30%;text-align: center;font-size: 18px;">系统字段名称</label>&nbsp;
          <label style="width: 30%;text-align: center;font-size: 18px;">其他软件字段名称</label>&nbsp;
        </div>
        <div style="margin-top: 5px;">
          <label style="width: 30%;padding-right: 110px;">类型</label>&nbsp;
          <a-input v-model:value="itype" type="text" placeholder="其他软件字段名称" style="width: 30%"/>&nbsp;<br>

          <label style="width: 30%;padding-right: 110px;">科目编码</label>&nbsp;
          <a-input v-model:value="iccode" type="text" placeholder="其他软件字段名称" style="width: 30%"/>&nbsp;<br>

          <label style="width: 30%;padding-right: 110px;">科目名称</label>&nbsp;
          <a-input v-model:value="iccodeName" type="text" placeholder="其他软件字段名称" style="width: 30%"/>&nbsp;<br>

          <label style="width: 30%;padding-right: 110px;">方向</label>&nbsp;
          <a-input v-model:value="ibprogerty" type="text" placeholder="其他软件字段名称" style="width: 30%"/>&nbsp;<br>

          <label  style="width: 30%;padding-right: 110px;">现金账</label>&nbsp;
          <a-input v-model:value="ibcash" type="text" placeholder="其他软件字段名称" style="width: 30%"/>&nbsp;<br>

          <label style="width: 30%;padding-right: 110px;">银行账</label>&nbsp;
          <a-input v-model:value="ibbank" type="text" placeholder="其他软件字段名称" style="width: 30%"/>&nbsp;<br>

          <label style="width: 30%;padding-right: 110px;">日记账</label>&nbsp;
          <a-input v-model:value="ibdaybook" type="text" placeholder="其他软件字段名称" style="width: 30%"/>&nbsp;<br>

          <label style="width: 30%;padding-right: 110px;">外币名称</label>&nbsp;
          <a-input v-model:value="iwb" type="text" placeholder="其他软件字段名称" style="width: 30%"/>&nbsp;<br>

          <label style="width: 30%;padding-right: 110px;">计量单位</label>&nbsp;
          <a-input v-model:value="ijl" type="text" placeholder="其他软件字段名称" style="width: 30%"/>&nbsp;<br>

          <label style="width: 30%;padding-right: 110px;">辅助账类型</label>&nbsp;
          <a-input v-model:value="ifuzhu" type="text" placeholder="其他软件字段名称" style="width: 30%"/>&nbsp;<br>

          <label style="width: 30%;padding-right: 110px;">受控系统</label>&nbsp;
          <a-input v-model:value="icontrolled" type="text" placeholder="其他软件字段名称" style="width: 30%"/>&nbsp;<br>
          <label style="width: 30%;padding-right: 110px;">封存</label>&nbsp;
          <a-input v-model:value="dataflag" type="text" placeholder="其他软件字段名称" style="width: 30%"/>&nbsp;
        </div>
      </div>
    </div>
    <template #footer>
      <div v-if="!isEdit">
        <a-button @click="closeOk()" type="primary">关闭</a-button>
      </div>
      <div v-if="isEdit">
        <a-button @click="closeOk()">取消</a-button>
        <a-button @click="handleOk()" type="primary">确认</a-button>
      </div>
    </template>
  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import { CaretDownOutlined,PlusCircleOutlined,FormOutlined,FileSearchOutlined } from '@ant-design/icons-vue'
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import { countByTname } from '/@/api/record/system/code-import-template'
import {useMessage} from "/@/hooks/web/useMessage";
import { Checkbox as ACheckbox } from 'ant-design-vue';

const ACheckboxGroup = ACheckbox.Group
const isEdit:any = ref(true)
const flag123:any = ref(false)
const formItems:any = ref({})
const openType:any = ref('')
const itype:any = ref('')
const iccode:any = ref('')
const iccodeName:any = ref('')
const ibprogerty:any = ref('')
const ibcash:any = ref('')
const ibbank:any = ref('')
const ibdaybook:any = ref('')
const iwb:any = ref('')
const ijl:any = ref('')
const ifuzhu:any = ref('')
const icontrolled:any = ref('')
const dataflag:any = ref('')
const oldname:any = ref('')
const jsondata:any = [
  {titel:'类型',value:''},
  {titel:'科目编码',value:''},
  {titel:'科目名称',value:''},
  {titel:'方向',value:''},
  {titel:'现金账',value:''},
  {titel:'银行账',value:''},
  {titel:'日记账',value:''},
  {titel:'外币名称',value:''},
  {titel:'计量单位',value:''},
  {titel:'辅助账类型',value:''},
  {titel:'受控系统',value:''},
  {titel:'封存',value:''}
]

const { createErrorModal } = useMessage()
const emit=defineEmits([])

async function findByName() {
  if(oldname.value===''){
    findByName2()
  }else if(oldname.value!==formItems.value.tname){
    findByName2()
  }
}

async function findByName2() {
  let temp= await countByTname(formItems.value.tname)
  if(temp>0){
    createErrorPop('模板名称已存在,不能重复!')
    return false;
  }
  return true
}

async function handleOk() {
  if(formItems.value.tname===''){
    createErrorPop('模板名称不能为空！')
    return false
  }else{
    findByName()
  }
  if(formItems.value.remark===''){
    createErrorPop('来源软件版本不能为空！')
    return false
  }
  if(itype.value==='' && iccode.value==='' && iccodeName.value==='' && ibprogerty.value==='' && ibcash.value==='' && ibbank.value==='' && ibdaybook.value==='' &&
    iwb.value==='' && ijl.value==='' && ifuzhu.value==='' && icontrolled.value===''&& dataflag.value===''){
    createErrorPop('字段名称不能都为空！')
    return false
  }

  jsondata[0].value=itype.value
  jsondata[1].value=iccode.value
  jsondata[2].value=iccodeName.value
  jsondata[3].value=ibprogerty.value
  jsondata[4].value=ibcash.value
  jsondata[5].value=ibbank.value
  jsondata[6].value=ibdaybook.value
  jsondata[7].value=iwb.value
  jsondata[8].value=ijl.value
  jsondata[9].value=ifuzhu.value
  jsondata[10].value=icontrolled.value
  jsondata[11].value=dataflag.value

  formItems.value.tjson=JSON.stringify(jsondata)
  formItems.value.flag=flag123.value?0:1
  formItems.value.ttype=1
  emit('save', unref(formItems))
  closeModal()
  return true
}
function closeOk() {
  closeModal();
  return true
}
const [register, {closeModal}] = useModalInner((data) => {
  isEdit.value=data.isEdit
  itype.value=''
  iccode.value=''
  iccodeName.value=''
  ibprogerty.value=''
  ibcash.value=''
  ibbank.value=''
  ibdaybook.value=''
  iwb.value=''
  ijl.value=''
  ifuzhu.value=''
  icontrolled.value=''
  dataflag.value=''
  flag123.value=false
  openType.value=data.type

  formItems.value.id=undefined
  formItems.value.tname=''
  formItems.value.remark=''

  if(data.data!==''){
    formItems.value.id=data.data.id
    formItems.value.tname=data.data.tname
    formItems.value.remark=data.data.remark
    flag123.value=data.data.flag>0?false:true
    formItems.value.ttype=data.data.ttype
    oldname.value=data.data.tname

    var jsarr=eval(data.data.tjson);
    itype.value=jsarr[0].value
    iccode.value=jsarr[1].value
    iccodeName.value=jsarr[2].value
    ibprogerty.value=jsarr[3].value
    ibcash.value=jsarr[4].value
    ibbank.value=jsarr[5].value
    ibdaybook.value=jsarr[6].value
    iwb.value=jsarr[7].value
    ijl.value=jsarr[8].value
    ifuzhu.value=jsarr[9].value
    icontrolled.value=jsarr[10].value
    dataflag.value=jsarr[11].value
  }else{
    formItems.value.flag=1
  }
})

function createErrorPop(text) {
  createErrorModal({
    iconType: 'warning',
    title: '提示',
    content: text
  })
  return false
}
</script>
<style>
.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid #0096c7;
  border-left: none;
  border-right: none;
  border-top: none !important;
}
.ant-modal-header{
  border: none !important;
}
</style>
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
    width: 35%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  .ant-select-selector {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  label {
    text-align: right;
    width: 110px;
    display: inline-block;
    padding: 5px 10px;
    font-size: 16px;
  }
}
.nc-open-show-content {
  input {
    width: 35%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }

  .ant-select-selector {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }
  pointer-events: none;
  cursor: default;
  label {
    text-align: right;
    width: 110px;
    display: inline-block;
    padding: 5px 10px;
    font-size: 16px;
  }
}
</style>
