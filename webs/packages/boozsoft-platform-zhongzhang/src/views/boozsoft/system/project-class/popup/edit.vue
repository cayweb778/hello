<template>
  <BasicModal
    width="500px"
    v-bind="$attrs"
    title="项目分类"
    @ok="handleOk()"
    @register="register"
    :canFullscreen="false"
  >
    <template #title>
      <div style="display: flex;margin-top: 10px;margin-left: 10px;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='0'">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;项目分类
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='1'">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;项目分类
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='2'">
          <FileSearchOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;项目分类
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 60px;top: 10px;z-index: 100000;background:#ffffff">
        <img src="/@/assets/images/project.png" style="height:80px;margin-right: 10px;"/>
      </div>
    </template>
    <div class="nc-open-content" >
      <div class="open-content-up" style="text-align: center;margin-top: 50px;">
        <label>上级分类：</label>
        <TreeSelect
          v-model:value="formItems.parentId"
          style="width: 50%"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="treeData"
          placeholder="请选择上级分类"
          tree-default-expand-all
          allow-clear
          @change="changeParent()"
        >
          <template #suffixIcon><CaretDownOutlined style="color:#666666;" /></template>
        </TreeSelect>
        <span class="red_span"></span>
        <br/>

        <label>分类编码：</label>
        <a-input v-model:value="formItems.projectClassCode" :prefix="before" :disabled="isState=='1'" :minlength="2" :maxlength="2" placeholder="" @blur="checkClassCode()" style="width:50%;"/>
        <span class="red_span">*</span>
        <br/>
        <label></label><span style="color:#aaaaaa">编码规则：2-2-2-2</span>

        <br/><br/><br/>

        <label style="font-size: 18px;margin-left: 0;">分类名称：</label>
        <a-input v-model:value="formItems.projectClassName" placeholder="" class="abc" style="width: 65%;" />
        <span class="red_span">*</span>

<!--        <br><br>
        <label>大类编码：</label>
        <a-input :disabled="true" v-model:value="formItems.projectCateCode" placeholder="" />
        <span class="red_span"></span>-->
<!--        <a-select
          v-model:value="formItems.projectCateCode"
          show-search
          placeholder="请选择项目大类"
          option-filter-prop="children"
          style="width: 50%"
          allow-clear
        >
          <a-select-option v-for="cate in cateList" :key="cate.projectCateCode" :value="cate.projectCateCode">
            ({{ cate.projectCateCode }}){{ cate.projectCateName }}
          </a-select-option>
        </a-select>-->

      </div>
    </div>

    <template #footer>
      <div v-if="isState=='2'">
        <a-button @click="closeModal()" type="primary">关闭</a-button>
      </div>
      <div v-if="isState=='0'">
        <a-button @click="closeModal()">取消</a-button>
        <a-button @click="handleOk()" :disabled="saveClick">保存</a-button>
        <a-button @click="handleOkAdd()" :disabled="saveClick" type="primary">保存并新增</a-button>
      </div>
      <div v-if="isState=='1'">
        <a-button @click="closeModal()">放弃</a-button>
        <a-button @click="handleOk()" :disabled="saveClick" type="primary">保存</a-button>
      </div>
    </template>

  </BasicModal>
</template>
<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import {
  findProClassTree,
  findProClassByCode,
  GetProClassTree, findById, saveProClass, findByProjectCateCodeAndNotJiciOrderByProjectClassCode
} from '/@/api/record/system/project_class'
import { cateFindAll } from '/@/api/project_category/project_category'
import {Select as ASelect,Input as AInput,TreeSelect,message } from 'ant-design-vue'
import {
  PlusCircleOutlined,
  FormOutlined,
  FileSearchOutlined,
  CaretDownOutlined
} from '@ant-design/icons-vue'
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {useMessage} from "/@/hooks/web/useMessage";

const AInputSearch=AInput.Search
const ASelectOption=ASelect.Option
const {
  createErrorModal
} = useMessage()
const emit=defineEmits(['register','save'])

const formItems:any = ref({})

const treeData:any = ref([])

const cateList:any = ref([])

let changeBeforeModel:any = {}
const dynamicTenantId = ref()
const isState = ref('0')
const saveClick:any = ref(false)
const [register, { closeModal }] = useModalInner((data:any) => {
  saveClick.value=false
  dynamicTenantId.value = data.dynamicTenantId
  isState.value = data.isState
  useRouteApi(findByProjectCateCodeAndNotJiciOrderByProjectClassCode,{schemaName: dynamicTenantId})({projectCateCode:data.data.projectCateCode,projectClassCtl:data.data.projectClassCtl}).then(res=>{
    function a(deptTree:any) {
      deptTree.forEach(
          (item: any) => {
          if (item.children != null) {
            a(item.children)
          }
          item.title = '(' + item.projectClassCode + ')' + item.projectClassName
          item.value = item.id
          item.key = item.id
        })
    }
    a(res)
    // console.log(res)
    treeData.value = res
  })

  //选择项目大类
  /*cateFindAll().then((res:any)=>{
    cateList.value = res.items
    console.log(cateList.value)
  })*/

  // 方式2
  formItems.value = {
    id: data.data.id,
    uniqueCode: data.data.uniqueCode,
    projectClassCode: data.data.projectClassCode.substring(data.data.projectClassCode.length-2),
    projectClassName: data.data.projectClassName,
    projectCateCode: data.data.projectCateCode,
    projectClassCtl: data.data.projectClassCtl,
    parentId: data.data.parentId == 0 ? '' : data.data.parentId,
  }
  changeParent()
  changeBeforeModel = JSON.parse(JSON.stringify(formItems))
})
let isChanged:boolean = false
async function handleOk() {
  saveClick.value=true
  isChanged = !(formItems.value.uniqueCode == changeBeforeModel._value.uniqueCode
              &&formItems.value.projectClassCode == changeBeforeModel._value.projectClassCode
              &&formItems.value.projectClassName == changeBeforeModel._value.projectClassName
              &&formItems.value.projectCateCode == changeBeforeModel._value.projectCateCode
              &&formItems.value.parentId == changeBeforeModel._value.parentId)
  console.log(isChanged)
  if (formItems.value.projectClassCode==''){
    createErrorModal({
      iconType: 'success',
      title: '提示',
      content: '分类编码不能为空！'
    })
    // alert("分类编码不能为空！")
    saveClick.value=false
    return false
  }
  if (formItems.value.projectClassName==''){
    createErrorModal({
      iconType: 'success',
      title: '提示',
      content: '分类名称不能为空！'
    })
    // alert("分类名称不能为空！")
    saveClick.value=false
    return false
  }
  /*if (formItems.value.projectCateCode==''){
    createErrorModal({
      iconType: 'success',
      title: '提示',
      content: '大类编码不能为空！'
    })
    // alert("大类编码不能为空！")
    return false
  }*/
  if(isChanged){
    formItems.value.projectClassCode = before.value+formItems.value.projectClassCode
    emit('save', unref(formItems))
    closeModal()
    saveClick.value=false
    return true
  }
  closeModal()
  // alert("没有改变过")
  saveClick.value=false
  return false
}

async function handleOkAdd() {
  saveClick.value=true
  isChanged = !(formItems.value.uniqueCode == changeBeforeModel._value.uniqueCode
    &&formItems.value.projectClassCode == changeBeforeModel._value.projectClassCode
    &&formItems.value.projectClassName == changeBeforeModel._value.projectClassName
    &&formItems.value.projectCateCode == changeBeforeModel._value.projectCateCode
    &&formItems.value.parentId == changeBeforeModel._value.parentId)
  console.log(isChanged)
  if (formItems.value.projectClassCode==''){
    createErrorModal({
      iconType: 'success',
      title: '提示',
      content: '分类编码不能为空！'
    })
    // alert("分类编码不能为空！")
    saveClick.value=false
    return false
  }
  if (formItems.value.projectClassName==''){
    createErrorModal({
      iconType: 'success',
      title: '提示',
      content: '分类名称不能为空！'
    })
    // alert("分类名称不能为空！")
    saveClick.value=false
    return false
  }
  /*if (formItems.value.projectCateCode==''){
    createErrorModal({
      iconType: 'success',
      title: '提示',
      content: '大类编码不能为空！'
    })
    // alert("大类编码不能为空！")
    return false
  }*/
  if(isChanged){
    formItems.value.projectClassCode = before.value+formItems.value.projectClassCode
    // emit('save', unref(formItems))
    // closeModal()
    await useRouteApi(saveProClass, {schemaName: dynamicTenantId})(formItems.value)
    message.success('保存成功！')
    formItems.value = changeBeforeModel
    saveClick.value=false
    return true
  }
  // closeModal()
  // alert("没有改变过")
  saveClick.value=false
  return false
}

const before = ref('')
async function changeParent(){
  if (formItems.value.parentId!=null && formItems.value.parentId!='' && formItems.value.parentId!=0) {
    const res = await useRouteApi(findById, {schemaName: dynamicTenantId})(formItems.value.parentId)
    before.value = res.projectClassCode
  } else {
    before.value = ''
  }
}

async function checkClassCode(){
  if (formItems.value.projectClassCode!=null && formItems.value.projectClassCode!='') {
    formItems.value.projectClassCode = pad(formItems.value.projectClassCode, 2)
  }
  /*if(changeBeforeModel._value.projectClassCode!=undefined && changeBeforeModel._value.projectClassCode!=''){
    return true
  }*/
  if (changeBeforeModel._value.projectClassCode != formItems.value.projectClassCode && formItems.value.projectClassCode != null && formItems.value.projectClassCode != '') {
    const res = await useRouteApi(findProClassByCode, {schemaName: dynamicTenantId})({
      projectClassCode: before.value + formItems.value.projectClassCode,
      projectCateCode: formItems.value.projectCateCode,
      projectClassCtl: formItems.value.projectClassCtl
    })
    if (res.length != 0) {
      createErrorModal({
        iconType: 'success',
        title: '提示',
        content: '项目分类编码已存在，请重新输入！'
      })
      // alert('项目分类编码已存在，请重新输入！')
      formItems.value.projectClassCode = ''
      console.log(false)
      return false
    }
  }
  return true
}

/**
 * 字符串前补0
 * @param num
 * @param n
 */
function pad(num, n) {
  let len = num.toString().length;
  while(len < n) {
    num = "0" + num;
    len++;
  }
  return num;
}

/*const filterOption = (input: string, option: any) => {
  return option.props.value.toLowerCase().indexOf(input.toLowerCase()) >= 0
}*/

</script>
<style>
.vben-basic-title {
  color: #0096c7 !important;
  border:none !important;
}

.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
  border-top: none !important;
}
.ant-modal-header{
  border: none !important;
}
</style>
<style lang="less" scoped>
:deep(.vben-basic-title) {
  color: #0096c7 !important;
}
.ant-modal-header{
  border: none !important;
}

:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none !important;
}

.vben-basic-title {
  color: #0096c7 !important;
  border: none !important;
}

.ant-modal-body {
  padding: 0px;
  border-bottom: 1px solid rgb(1, 129, 226);
  border-left: none;
  border-right: none;
  border-top: none !important;
}

.red_span {
  color: red;
  font-weight: bold;
  display: inline-block;
  width: 20px;
  text-align: right;
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
</style>
