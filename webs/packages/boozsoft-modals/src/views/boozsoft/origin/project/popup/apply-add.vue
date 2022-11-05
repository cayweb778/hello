
<template>
  <BasicModal width="900px" v-bind="$attrs" title="项目信息" @ok="handleOk()" @register="register" :loading="loadMark" :canFullscreen="false">
    <template #title>
      <div style="display: flex;margin-top: 10px;" class="vben-basic-title">
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='0'">
          <PlusCircleOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;项目信息
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='1'">
          <FormOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;项目信息
        </span>
        <span style="line-height:40px;font-size: 28px;" v-if="isState=='2'">
          <FileSearchOutlined style="font-size: 34px;font-weight: bold"/>&nbsp;&nbsp;项目信息
        </span>
      </div>
      <div style="display: inline-block;position:absolute;right: 60px;top: 20px;z-index: 100000;background:#ffffff">
        <img src="/@/assets/images/project.png" style="height:80px;margin-right: 10px;"/>
      </div>
    </template>

    <div :class="isEdit?'nc-open-content':'nc-open-show-content'">
      <div class="open-content-up" style="margin-top: 30px;">
        <a-radio-group v-model:value="formItems.jiesuan">
          <a-radio value="0" style="width:120px;">
            <span style="color: #000000;font-weight: bold;font-size: 12px;">未结算</span>
          </a-radio>
          <a-radio value="1" style="width:120px;margin-left: -10px;">
            <span style="color: #000000;font-weight: bold;font-size: 12px;">已结算</span>
          </a-radio>
        </a-radio-group>
        <br/>

        <label style="font-size: 18px;margin-left: 0;width:170px;">项目名称：</label>
        <a-input @blur="checkName()" v-model:value="formItems.projectName" class="abc" :placeholder="isEdit?'请输入项目名称':''" style="width: 70%" />
        <span class="red_span">*</span>
        <br/><br/><br/>

        <label>项目编码：</label>
        <a-input v-model:value="formItems.projectCode" @blur="checkCode()" :placeholder="isEdit?'请输入项目编码':''"/>
        <span class="red_span">*</span>
        <label>项目分类：</label>
        <a-select
          v-model:value="formItems.projectClassCode"
          show-search
          :placeholder="isEdit?'请选择项目分类':''"
          style="width: 25%;border: none;"
          allow-clear
        >
          <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
          <a-select-option v-for="cls in classList" :key="cls.projectClassName" :value="cls.projectClassCode" >
            {{ cls.projectClassName }}
          </a-select-option>
        </a-select>
        <span class="red_span"></span>
        <br/>

<!--        <label>所属部门：</label>
        <a-tree-select
          v-model:value="formItems.deptCode"
          style="width: 25%"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="treeData"
          :placeholder="isEdit?'请选择部门':''"
          tree-default-expand-all
          allow-clear
          :disabled="true"
        >
          <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
        </a-tree-select>
        <span class="red_span"></span>-->
        <label>项目负责人：</label>
        <a-select
          v-model:value="formItems.psnInCharge"
          show-search
          :placeholder="isEdit?'请选择部门负责人':''"
          style="width: 25%;border: none"
          allow-clear
        >
          <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
          <a-select-option
            v-for="psn in psnList"
            :key="psn.psnName"
            :value="psn.uniqueCode"
          >{{ psn.psnCode }}-{{ psn.psnName }}
          </a-select-option>
        </a-select>
        <span class="red_span"></span>
        <br/>
        <label>上级项目：</label>
        <a-select v-model:value="formItems.menu1" @change="handleChange" show-search :filter-option="false" @search="handleSearch" allow-clear style="width: 60%;">
          <template #suffixIcon><CaretDownOutlined v-if="isState!='2'" style="color:#666666;" /></template>
          <a-select-option v-for="item in projectLists" :key="item.id" :value="item.id" >
            ({{ item.projectCode }}){{ item.projectName }}
          </a-select-option>
        </a-select>
      </div>

      <div class="open-content-down">
        <a-tabs v-model:activeKey="activeKey" type="card">
          <a-tab-pane key="1" tab="项目周期">
            <div class="down-tab-content">
              <label>开始日期：</label>
              <a-date-picker v-model:value="formItems.startDate" :placeholder="isEdit?'请输入开始日期':''" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 25%;"/>
              <span class="red_span"></span>
              <label>结束日期：</label>
              <a-date-picker v-model:value="formItems.endDate" :placeholder="isEdit?'请输入结束日期':''" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 25%;"/>
              <span class="red_span"></span>
              <br/><br/><br/>

              <!--              <label>开始年度：</label>
                            <a-date-picker v-model:value="formItems.menu1" :placeholder="isEdit?'请输入开始年度':''" format="YYYY" value-format="YYYY" style="width: 25%;"/>
                            <span class="red_span"></span>
                            <label>结束年度：</label>
                            <a-date-picker v-model:value="formItems.menu2" :placeholder="isEdit?'请输入结束年度':''" format="YYYY" value-format="YYYY" style="width: 25%;"/>
                            <span class="red_span"></span>-->
            </div>
          </a-tab-pane>
          <a-tab-pane key="2" tab="项目预算">
            <div class="down-tab-content">
              <label>项目总预算：</label>
              <a-input v-model:value="formItems.menu2" :placeholder="isEdit?'请输入项目总预算':''"/>
              <span class="red_span">*</span>
              <label>外拨经费：</label>
              <a-input v-model:value="formItems.menu3" :placeholder="isEdit?'请输入外拨经费':''"/>
              <span class="red_span">*</span>
              <br/>

              <a-radio-group v-model:value="formItems.menu4">
                <label>预算支出控制：</label>
                <a-radio value="0" style="width:120px;margin-left: -10px;">
                  不控制
                </a-radio>
                <a-radio value="1" style="width:120px;margin-left: -10px;">
                  严格控制
                </a-radio>
              </a-radio-group>
              <span class="red_span"></span>
            </div>
          </a-tab-pane>
          <a-tab-pane key="3" tab="资金来源">
            <div class="down-tab-content">
              <label>资金来源:</label>
              <a-input v-model:value="formItems.menu5" :placeholder="isEdit?'请输入资金来源':''"/>
            </div>
          </a-tab-pane>
          <a-tab-pane key="4" tab="主要任务">
            <div class="down-tab-content">
              <label>主要任务:</label>
              <a-input v-model:value="formItems.menu5" :placeholder="isEdit?'请输入主要任务':''"/>
            </div>
          </a-tab-pane>
          <a-tab-pane key="5" tab="设备清单">
            <div class="down-tab-content">
              <label>设备清单:</label>
              <a-input v-model:value="formItems.menu5" :placeholder="isEdit?'请输入设备清单':''"/>
            </div>
          </a-tab-pane>
          <!--          <a-tab-pane key="6" tab="相关文档">
                      <div class="down-tab-content">
                        相关文档
                      </div>
                    </a-tab-pane>-->
        </a-tabs>
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
:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
  font-weight: bold;
}

:deep(.ant-picker){
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  .ant-picker-input{
    border:none;
  }
  input {
    font-size: 14px;
    font-weight: bold;
  }
}

:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none !important;
}

:deep(.vben-basic-title) {
  color: #0096c7 !important;
}
.ant-modal-header{
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
    width: 25%;
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
    width: 160px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 2em;
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

.nc-open-show-content {

  input {
    width: 25%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
    font-weight: bold;
    font-size: 14px;
  }

  .abc{
    border-bottom: 2px solid #666666 !important;
    font-size: 18px;
  }

  pointer-events: none;
  cursor: default;

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
    width: 160px;
    display: inline-block;
    padding-top: 5px;
    padding-bottom: 5px;
    padding-left: 2em;
    color: #535353;
    font-size: 13px;
    margin-left: 1em;
    font-weight: bold;
  }

  .open-content-down {
    margin-top: 5%;

    :deep(.ant-tabs-tab) {
      pointer-events: auto;
    }

    .ant-tabs-tab-active::before {
      position: absolute;
      top: 0px;
      left: 0;
      width: 100%;
      border-top: 2px solid transparent;
      border-radius: 2px 2px 0 0;
      transition: all 0.3s;
      content: '';
      background-color: rgb(1, 143, 251);
    }
  }
}

/*:deep(.ant-tabs.ant-tabs-card .ant-tabs-card-bar .ant-tabs-tab) {
  height: 40px;
  margin: 0;
  margin-right: 2px;
  padding: 0 16px;
  line-height: 38px;
  background: #fafafa;
  border: 1px solid #f0f0f0;
  border-radius: 2px 2px 0 0;
  transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  font-weight: bold;
  font-size: 13px;
}*/

:deep(.ant-tabs-card).ant-tabs-top > .ant-tabs-nav  {
  height: 40px;
  font-weight: bold;
  font-size: 13px;
  .ant-tabs-tab-active,.ant-tabs-card.ant-tabs-top > div > .ant-tabs-nav .ant-tabs-tab-active {
    border-top: 2px solid rgb(1, 143, 251) !important;
  }
}

</style>
<script setup="props, {content}" lang="ts">
import { ref, unref } from 'vue'
import { BasicModal, useModalInner } from '/@/components/Modal'
import { BasicForm } from '/@/components/Form/index';
import {
  findByCode,
  findByName,
  getFromEdit,
  saveProject,
  findProjectByCateCode,
  findBukongProjectCode,
  findMaxProjectCode
} from "/@/api/record/system/group-project";
import {useMessage} from "/@/hooks/web/useMessage";
import {useProjectStoreWidthOut} from "/@/store/modules/origin-project";
import {
  Input as AInput,
  Select as ASelect,
  Popover as APopover,
  Tabs as ATabs,
  DatePicker as ADatePicker,
  Cascader as ACascader,
  TreeSelect as ATreeSelect,
  Radio as ARadio, message
} from "ant-design-vue";
import {
  PlusCircleOutlined,
  FormOutlined,
  FileSearchOutlined,
  CaretDownOutlined
} from '@ant-design/icons-vue'
import {useUserStoreWidthOut} from "/@/store/modules/user";
import {findByUserIdAndOriginId} from "/@/api/record/system/group-permission";
import {findByOriginId,save} from "/@/api/record/system/group-project-account";
import {saveProject as originSaveProject,
  findByCode as originFindByCode,
  findByName as originFindByName,
  findAllList
} from "/@/api/record/system/origin-project";
import {load} from "/@/api/group/FileEncodingRules";
//import {mul} from "/@/views/boozsoft/gdzc/gu-ding-zi-chan-add/calculation";
const mul=null

const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ATabPane = ATabs.TabPane
const ARadioGroup = ARadio.Group;

const {
  createErrorModal,
  createConfirm
} = useMessage()

const emit = defineEmits(['register', 'save'])

const activeKey = ref('1')

const formItems: any = ref({})

const treeData: any = ref([])

const psnList: any = ref([])

const classList: any = ref([])

const projectList: any = ref([])

const isState = ref('0')
const saveClick:any = ref(false)

//查询栏目列表
const schemas: any = ref([])
async function handleOk() {
  saveClick.value=true
  if (formItems.value.projectCode==null || formItems.value.projectCode==''){
    createErrorModal({
      iconType: 'success',
      title: '提示',
      content: '项目编码不能为空！'
    })
    // alert("项目编码不能为空！")
    saveClick.value=false
    return false
  }
  if (formItems.value.projectName==null || formItems.value.projectName==''){
    createErrorModal({
      iconType: 'success',
      title: '提示',
      content: '项目名称不能为空！'
    })
    // alert("项目名称不能为空！")
    saveClick.value=false
    return false
  }
  if (formItems.value.projectClassCode==null || formItems.value.projectClassCode==''){
    formItems.value.projectClassCode = '99'
  }
  if (formItems.value.jiesuan==null || formItems.value.jiesuan==''){
    formItems.value.jiesuan = '0'
  }
  const projectList = await findByOriginId(formItems.value.originId)
  const projectList1 = projectList.filter(item=>{
    return item.projectClassCode == formItems.value.projectClassCode && (item.projectCode==formItems.value.projectCode || item.projectName==formItems.value.projectName)
  })
  if(projectList1.length>0){
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '该项目已经分配给本账套，请使用“引入”菜单，直接引入该人员信息！！'
    })
    saveClick.value = false
    return false
  }
  //是否自动生效
  //不自动生效往集团表加入一条记录
  if (isAuto.value=='0') {
    await saveProject(formItems.value)
    emit('save', unref(formItems))
    closeModal()
    saveClick.value = false
  } else {
    //自动生效查询集团表是否有记录
    let project:any = formItems.value
    let list:any = []
    const list1 = await findByCode({projectCode: formItems.value.projectCode,projectClassCode: formItems.value.projectClassCode})
    const list2 = await findByName({projectCode: formItems.value.projectCode,projectClassCode: formItems.value.projectClassCode})
    list.push(list1)
    list.push(list2)
    for (let i = 0; i < list.length; i++) {
      project = list[i];
      project.isDel = '0'
      createConfirm({
        iconType: 'warning',
        title: '提示',
        content: '集团在已经存在相同的项目，是否直接添加到档案！',
        onOk: async () => {},
        onCancel: () => {
          saveClick.value=false
          return false
        }
      })
    }
    //往集团表插入数据
    project = await saveProject(formItems.value)
    //分配表插入数据
    let dateTime = new_Date()
    const item1 = {
      id: null,
      uniqueCode: project.uniqueCode,
      ctype: '1',
      originId: formItems.value.originId,
      assignUser: useUserStoreWidthOut().getUserInfo.id,
      assignDate: dateTime,
      flag: '1',
      acceptUser: useUserStoreWidthOut().getUserInfo.id,
      acceptDate: dateTime,
    }
    await save(item1)
    //组织表插入数据
    project.id = null
    await originSaveProject(project)
    emit('save', unref(formItems))
    closeModal()
    saveClick.value=false
    return true
  }
}

async function handleOkAdd() {
  saveClick.value=true
  if (formItems.value.projectCode==null || formItems.value.projectCode==''){
    createErrorModal({
      iconType: 'success',
      title: '提示',
      content: '项目编码不能为空！'
    })
    // alert("项目编码不能为空！")
    saveClick.value=false
    return false
  }
  if (formItems.value.projectName==null || formItems.value.projectName==''){
    createErrorModal({
      iconType: 'success',
      title: '提示',
      content: '项目名称不能为空！'
    })
    // alert("项目名称不能为空！")
    saveClick.value=false
    return false
  }
  if (formItems.value.projectClassCode==null || formItems.value.projectClassCode==''){
    formItems.value.projectClassCode = '99'
  }
  if (formItems.value.jiesuan==null || formItems.value.jiesuan==''){
    formItems.value.jiesuan = '0'
  }
  const projectList = await findByOriginId(formItems.value.originId)
  const projectList1 = projectList.filter(item=>{
    return item.projectClassCode == formItems.value.projectClassCode && (item.projectCode==formItems.value.projectCode || item.projectName==formItems.value.projectName)
  })
  if(projectList1.length>0){
    createErrorModal({
      iconType: 'warning',
      title: '提示',
      content: '该项目已经分配给本账套，请使用“引入”菜单，直接引入该人员信息！！'
    })
    saveClick.value = false
    return false
  }
  //是否自动生效
  //不自动生效往集团表加入一条记录
  if (isAuto.value=='0') {
    await saveProject(formItems.value)
    emit('save', unref(formItems))
    // closeModal()
    saveClick.value = false
  } else {
    //自动生效查询集团表是否有记录
    let project:any = formItems.value
    let list:any = []
    const list1 = await findByCode({projectCode: formItems.value.projectCode,projectClassCode: formItems.value.projectClassCode})
    const list2 = await findByName({projectCode: formItems.value.projectCode,projectClassCode: formItems.value.projectClassCode})
    list.push(list1)
    list.push(list2)
    for (let i = 0; i < list.length; i++) {
      project = list[i];
      project.isDel = '0'
      createConfirm({
        iconType: 'warning',
        title: '提示',
        content: '集团在已经存在相同的项目，是否直接添加到档案！',
        onOk: async () => {},
        onCancel: () => {
          saveClick.value=false
          return false
        }
      })
    }
    //往集团表插入数据
    project = await saveProject(formItems.value)
    //分配表插入数据
    let dateTime = new_Date()
    const item1 = {
      id: null,
      uniqueCode: project.uniqueCode,
      ctype: '1',
      originId: formItems.value.originId,
      assignUser: useUserStoreWidthOut().getUserInfo.id,
      assignDate: dateTime,
      flag: '1',
      acceptUser: useUserStoreWidthOut().getUserInfo.id,
      acceptDate: dateTime,
    }
    await save(item1)
    //组织表插入数据
    project.id = null
    await originSaveProject(project)
    emit('save', unref(formItems))
    formItems.value = changeBeforeModel
    // closeModal()
    saveClick.value=false
    return true
  }
}

// 下划线转换驼峰
function toHump(name:any) {
  return name.replace(/\_(\w)/g, function(all:any, letter:any){
    console.log(all)
    return letter.toUpperCase();
  });
}

const { createMessage } = useMessage();
const handleSubmit = (values: any) => {
  createMessage.success('click search,values:' + JSON.stringify(values));
}

let changeBeforeModel:any = {}
const loadMark = ref(false)
// 是否为修改
const isEdit = ref(true)
const isAuto:any = ref('0')
const [register, { closeModal }] = useModalInner(async ({data}) => {
  saveClick.value=false
  loadMark.value = true
  isState.value = data.isState
  if (null != data.isEdit) isEdit.value = data.isEdit
  const permissionList = await findByUserIdAndOriginId(useUserStoreWidthOut().getUserInfo.id,data.originId,'project')
  if (permissionList.length>0){
    isAuto.value = permissionList[0].isAuto
  } else {
    isAuto.value = '0'
  }

  await useProjectStoreWidthOut().bb({treeData, psnList, classList, formItems, data});
  const res:any = await findAllList(data.originId)
  projectList.value = res
  projectLists.value = projectList.value
  let dateTime = new_Date()
  formItems.value.successState = isAuto.value
  formItems.value.applyUser = useUserStoreWidthOut().getUserInfo.realName
  formItems.value.applyDate = dateTime
  formItems.value.ctype = '1'
  if (isState.value=='0') {
    await getMaxCode()
  }
  changeBeforeModel = JSON.parse(JSON.stringify(formItems))
  loadMark.value = false
})

//根据编码规则获取编码
async function getMaxCode() {
  // 读取编码规则-项目
  let str = ""
  let temp = await load('1-4')
  // console.log(temp)
  if(temp.id==null){
    return false
  }
  if(temp.prefixOneIs=='true'){
    if (temp.prefixOne=='4-0'){
      //项目大类
      let projectCode = ''
      if (formItems.value.itemCode==null || formItems.value.itemCode==''){
        projectCode = pad(0,temp.prefixOneLength)
      } else {
        projectCode = formItems.value.itemCode.length >= temp.prefixOneLength?
          formItems.value.itemCode.substring(0,temp.prefixOneLength) : pad(formItems.value.itemCode,temp.prefixOneLength)
      }
      str = str + projectCode
    }
    if (temp.prefixOne=='4-1'){
      //项目分类
      let projectCode = ''
      if (formItems.value.projectClassCode==null || formItems.value.projectClassCode==''){
        projectCode = pad(0,temp.prefixOneLength)
      } else {
        projectCode = formItems.value.projectClassCode.length >= temp.prefixOneLength?
          formItems.value.projectClassCode.substring(0,temp.prefixOneLength) : pad(formItems.value.projectClassCode,temp.prefixOneLength)
      }
      str = str + projectCode
    }
    if (temp.prefixOne=='88'){
      //日期(年月)
      let yy = new Date().getFullYear()
      let mm = new Date().getMonth() + 1 < 10 ? '0' + (new Date().getMonth()+ 1) : (new Date().getMonth()+ 1)
      str = str + yy + mm
    }
    if (temp.prefixOne=='99'){
      //手动录入
      str = str + pad(temp.prefixOneCustomize,temp.prefixOneLength)
    }
    //分隔符
    if (temp.delimiter=='2'){
      str = str + '.'
    } else if (temp.delimiter=='3'){
      str = str + '-'
    }
  }
  if(temp.prefixTwoIs=='true'){
    if (temp.prefixTwo=='4-0'){
      //项目大类
      let projectCode = ''
      if (formItems.value.itemCode==null || formItems.value.itemCode==''){
        projectCode = pad(0,temp.prefixOneLength)
      } else {
        projectCode = formItems.value.itemCode.length >= temp.prefixTwoLength?
          formItems.value.itemCode.substring(0,temp.prefixTwoLength) : pad(formItems.value.itemCode,temp.prefixTwoLength)
      }
      str = str + projectCode
    }
    if (temp.prefixTwo=='4-1'){
      //项目分类
      let projectCode = ''
      if (formItems.value.projectClassCode==null || formItems.value.projectClassCode==''){
        projectCode = pad(0,temp.prefixTwoLength)
      } else {
        projectCode = formItems.value.projectClassCode.length >= temp.prefixTwoLength?
          formItems.value.projectClassCode.substring(0,temp.prefixTwoLength) : pad(formItems.value.projectClassCode,temp.prefixTwoLength)
      }
      str = str + projectCode
    }
    if (temp.prefixTwo=='88'){
      //日期(年月)
      let yy = new Date().getFullYear()
      let mm = new Date().getMonth() + 1 < 10 ? '0' + (new Date().getMonth()+ 1) : (new Date().getMonth()+ 1)
      str = str + yy + mm
    }
    if (temp.prefixTwo=='99'){
      //手动录入
      str = str + pad(temp.prefixTwoCustomize,temp.prefixTwoLength)
    }
    //分隔符
    if (temp.delimiter=='2'){
      str = str + '.'
    } else if (temp.delimiter=='3'){
      str = str + '-'
    }
  }
  if(temp.prefixThreeIs=='true'){
    if (temp.prefixThree=='4-0'){
      //项目大类
      let projectCode = ''
      if (formItems.value.itemCode==null || formItems.value.itemCode==''){
        projectCode = pad(0,temp.prefixThreeLength)
      } else {
        projectCode = formItems.value.itemCode.length >= temp.prefixThreeLength?
          formItems.value.itemCode.substring(0,temp.prefixThreeLength) : pad(formItems.value.itemCode,temp.prefixThreeLength)
      }
      str = str + projectCode
    }
    if (temp.prefixThree=='4-1'){
      //项目分类
      let projectCode = ''
      if (formItems.value.projectClassCode==null || formItems.value.projectClassCode==''){
        projectCode = pad(0,temp.prefixThreeLength)
      } else {
        projectCode = formItems.value.projectClassCode.length >= temp.prefixThreeLength?
          formItems.value.projectClassCode.substring(0,temp.prefixThreeLength) : pad(formItems.value.projectClassCode,temp.prefixThreeLength)
      }
      str = str + projectCode
    }
    if (temp.prefixThree=='88'){
      //日期(年月)
      let yy = new Date().getFullYear()
      let mm = new Date().getMonth() + 1 < 10 ? '0' + (new Date().getMonth()+ 1) : (new Date().getMonth()+ 1)
      str = str + yy + mm
    }
    if (temp.prefixThree=='99'){
      //手动录入
      str = str + pad(temp.prefixThreeCustomize,temp.prefixThreeLength)
    }
    //分隔符
    if (temp.delimiter=='2'){
      str = str + '.'
    } else if (temp.delimiter=='3'){
      str = str + '-'
    }
  }
  // console.log(temp.autoNum)
  if(temp.autoNum=='false') {
    const project = await findBukongProjectCode(temp.serialNumLength,add(str.length,temp.serialNumLength),str.length,str)
    if (project.projectCode != null && project.projectCode != '') {
      str = str + pad(project.projectCode, temp.serialNumLength)
    } else {
      str = str + pad(temp.serialNumStr, temp.serialNumLength)
    }
  } else {
    const project = await findMaxProjectCode(temp.serialNumLength,add(str.length,temp.serialNumLength),str.length,str)
    if (project.projectCode != null && project.projectCode != '') {
      str = str + pad(add(project.projectCode,1), temp.serialNumLength)
    } else {
      str = str + pad(temp.serialNumStr, temp.serialNumLength)
    }
  }
  formItems.value.projectCode = str
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

/**
 * 加法
 * @param a
 * @param b
 */
function add(a, b) {
  let c, d, e;
  try {
    c = a.toString().split(".")[1].length;
  } catch (f) {
    c = 0;
  }
  try {
    d = b.toString().split(".")[1].length;
  } catch (f) {
    d = 0;
  }
  return e = Math.pow(10, Math.max(c, d)), (mul(a, e) + mul(b, e)) / e;
}

//获取当年月日
function new_Date() {
  let dateTime
  let yy = new Date().getFullYear()
  let mm = new Date().getMonth() + 1 < 10 ? '0' + (new Date().getMonth()+ 1) : (new Date().getMonth()+ 1)
  let dd = new Date().getDate() < 10 ? '0' + new Date().getDate() : new Date().getDate()
  let hh = new Date().getHours() < 10 ? '0' + new Date().getHours() : new Date().getHours()
  let mf = new Date().getMinutes() < 10 ? '0' + new Date().getMinutes() : new Date().getMinutes()
  let ss = new Date().getSeconds() < 10 ? '0' + new Date().getSeconds() : new Date().getSeconds()
  dateTime = yy + '-' + mm + '-' + dd + ' ' + hh + ':' + mf + ':' + ss
  // console.log(dateTime)
  return dateTime
}

async function checkCode() {
  /*if((changeBeforeModel._value.projectCode!=undefined && changeBeforeModel._value.projectCode!='') || changeBeforeModel._value.projectCode==formItems.value.projectCode){
      return true
  }*/
  if (changeBeforeModel._value.projectCode != formItems.value.projectCode && formItems.value.projectCode != null && formItems.value.projectCode != '') {
    const res = await originFindByCode(formItems.value)
    if (res.length != 0) {
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '项目编码已存在，请重新输入！'
      })
      // alert('项目编码已存在，请重新输入！')
      formItems.value.projectCode = ''
      console.log(false)
      return false
    }
  }
  return true
}
async function checkName() {
  /*if((changeBeforeModel._value.projectName!=undefined && changeBeforeModel._value.projectName!='') || changeBeforeModel._value.projectName==formItems.value.projectName){
    return true
  }*/
  if (changeBeforeModel._value.projectName != formItems.value.projectName && formItems.value.projectName != null && formItems.value.projectName != '') {
    const res = await originFindByName(formItems.value)
    if (res.length != 0) {
      createErrorModal({
        iconType: 'warning',
        title: '提示',
        content: '项目名称已存在，请重新输入！'
      })
      // alert('项目名称已存在，请重新输入！')
      formItems.value.projectName = ''
      console.log(false)
      return false
    }
  }
  return true
}

//搜索项目
const projectLists = ref([])
function handleSearch(value) {
  projectLists.value = projectList.value.filter(item => {
    if (value!=null && value!='') {
      return item.projectCode.slice(0, value.length) == value || item.projectCode.indexOf(value) != -1
    }
    return item
  })
}

function handleChange(value) {
  formItems.value.menu1 = value;
  projectLists.value = projectList.value.filter(item => {
    if (value!=null && value!='') {
      return item.uniqueCode.slice(0, value.length) == value
    }
    return item
  })
}
</script>
