<template>
  <div>
    <div class="app-container">
      <div class="app-container-head">
        <div class="container-head-title">
          <UnorderedListOutlined style="color: #0096c7"/>
          <b class="noneSpan">档案编码规则</b>
        </div>
        <div class="ant-btn-group" style="float: right">
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="doEdit()"
            v-if="!isEdit"
          ><span>编辑</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="backEdit()"
            v-if="isEdit"
          ><span>返回</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            v-if="isEdit"
            ant-click-animating-without-extra-node="false"
            @click="save()"
          ><span>保存</span></button>
          <button
            type="button"
            class="ant-btn ant-btn-me"
            ant-click-animating-without-extra-node="false"
            @click="closeCurrent(),router.push('/one/home/welcome')"
          ><span>退出</span></button>
        </div>
      </div>
      <div class="app-container-neck">

        <div style="float: right; margin-left: 10px">
          <a-button>
            <SyncOutlined :style="{ fontSize: '14px' }"/>
          </a-button>
          <a-popover class="ant-btn-default" placement="bottom">
            <template #content>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MAX'"
                    :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
              <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                    :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MIN'"/></span>
            </template>
            <!--            <template #title>
                          <b>设置表格字号</b></template>-->
            <a-button>
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </a-popover>
          <a-button @click="()=>{
            if (!visible){ visible = true;reloadColumns()}
            return false
          }">
            <FilterFilled :style="{ fontSize: '14px' }"/>
          </a-button>
        </div>
      </div>
    </div>
    <div class="app-container">
      <PageWrapper dense content-full-height fixed-height content-class="flex">
        <TypeTree class="w-1/4 xl:w-1/5" @select="handleSelect" v-model="pageParameter"/>
        <div class="a-container-content-one" style="background-color: white;padding: 50px 50px;width: 100%; ">
          <div class="acco-sideline" style="width: 90%;" >
            <span >设置规则</span>
            <div class="acco-sideline-content-one">

             <div style="width: 30%;padding-top: 25px">
                <span style="color: #aaa">编码方式：</span>
                <a-select  disabled style="width: 180px;float: right;background: white"  v-model:value="pageParameter.fileEncodingRules.codeWay">
                  <a-select-option v-for="d in initDynamics().codeWay" :value="d.key">
                    {{ d.name }}
                  </a-select-option>
                </a-select>
              </div>
              <div style="width: 30%;padding-top: 25px">
                <a-checkbox disabled checked="true">编号允许手动修改</a-checkbox>
              </div>
              <div style="width: 30%;padding-top: 25px">
                <a-checkbox disabled checked="true" >自动补空号</a-checkbox>
              </div>

              <div style="width: 30%;">
                <span style="color: #aaa"> 前 缀 一 ：</span>
                <a-select  :allowClear="true"  :disabled="!isEdit" style="width: 180px;float: right;" v-model:value="pageParameter.fileEncodingRules.prefixOne"  @select="selectOne">
                  <a-select-option v-for="d in prefixOneList" :value="d.key">
                    {{ d.name }}
                  </a-select-option>
                </a-select>
              </div>
              <div style="width: 25%;">
                <span style="color: #aaa">占位长度：</span>
                <a-select  :disabled="oneDisabled || !isEdit" style="width: 130px;float: right"  v-model:value="pageParameter.fileEncodingRules.prefixOneLength" @select="changeData('zw1')">
                  <a-select-option value="1">
                    1
                  </a-select-option>
                  <a-select-option value="2">
                    2
                  </a-select-option>
                  <a-select-option value="3">
                    3
                  </a-select-option>
                  <a-select-option value="4">
                    4
                  </a-select-option>
                  <a-select-option value="5">
                    5
                  </a-select-option>
                  <a-select-option value="6">
                    6
                  </a-select-option>
                  <a-select-option value="7">
                    7
                  </a-select-option>
                  <a-select-option value="8">
                    8
                  </a-select-option>
                </a-select>
              </div>
              <div style="width: 30%;">
                <span style="color: #aaa">自定义值：</span>
                <a-input  :disabled="oneDisabled || !isEdit" :maxlength="pageParameter.fileEncodingRules.prefixOneLength" v-model:value="pageParameter.fileEncodingRules.prefixOneCustomize" style="width: 130px;float: right" @change="changeData('zdy1')"/>
              </div>
              <div style="width: 15%;padding-left: 0px">
                <a-checkbox :disabled="!isEdit" v-model:checked="pageParameter.fileEncodingRules.prefixOneIs" @change="changeData">流水号参照</a-checkbox>
              </div>

              <div style="width: 30%;">
                <span style="color: #aaa"> 前 缀 二 ：</span>
                <a-select  :allowClear="true" :disabled="!isEdit" style="width: 180px;float: right" v-model:value="pageParameter.fileEncodingRules.prefixTwo" @select="selectTwo">
                  <a-select-option v-for="d in prefixTwoList" :value="d.key">
                    {{ d.name }}
                  </a-select-option>
                </a-select>
              </div>
              <div style="width: 25%;">
                <span style="color: #aaa">占位长度：</span>
                <a-select  :disabled="twoDisabled || !isEdit" style="width: 130px;float: right"  v-model:value="pageParameter.fileEncodingRules.prefixTwoLength" @select="changeData('zw2')">
                  <a-select-option value="1">
                    1
                  </a-select-option>
                  <a-select-option value="2">
                    2
                  </a-select-option>
                  <a-select-option value="3">
                    3
                  </a-select-option>
                  <a-select-option value="4">
                    4
                  </a-select-option>
                  <a-select-option value="5">
                    5
                  </a-select-option>
                  <a-select-option value="6">
                    6
                  </a-select-option>
                  <a-select-option value="7">
                    7
                  </a-select-option>
                  <a-select-option value="8">
                    8
                  </a-select-option>
                </a-select>
              </div>
              <div style="width: 30%;">
                <span style="color: #aaa">自定义值：</span>
                <a-input  :disabled="twoDisabled || !isEdit"  :maxlength="pageParameter.fileEncodingRules.prefixTwoLength" v-model:value="pageParameter.fileEncodingRules.prefixTwoCustomize" style="width: 130px;float: right" @change="changeData('zdy2')"/>
              </div>
              <div style="width: 15%;padding-left: 0px">
                <a-checkbox :disabled="!isEdit" v-model:checked="pageParameter.fileEncodingRules.prefixTwoIs" @change="changeData">流水号参照</a-checkbox>
              </div>

              <div style="width: 30%;">
                <span style="color: #aaa"> 前 缀 三 ：</span>
                <a-select  :allowClear="true"  :disabled="!isEdit" style="width: 180px;float: right" v-model:value="pageParameter.fileEncodingRules.prefixThree"  @select="selectThree">
                  <a-select-option v-for="d in prefixThreeList" :value="d.key">
                    {{ d.name }}
                  </a-select-option>
                </a-select>
              </div>
              <div style="width: 25%;">
                <span style="color: #aaa">占位长度：</span>
                <a-select   :disabled="threeDisabled || !isEdit"  style="width: 130px;float: right"  v-model:value="pageParameter.fileEncodingRules.prefixThreeLength" @select="changeData('zw3')">
                  <a-select-option value="1">
                    1
                  </a-select-option>
                  <a-select-option value="2">
                    2
                  </a-select-option>
                  <a-select-option value="3">
                    3
                  </a-select-option>
                  <a-select-option value="4">
                    4
                  </a-select-option>
                  <a-select-option value="5">
                    5
                  </a-select-option>
                  <a-select-option value="6">
                    6
                  </a-select-option>
                  <a-select-option value="7">
                    7
                  </a-select-option>
                  <a-select-option value="8">
                    8
                  </a-select-option>
                </a-select>
              </div>
              <div style="width: 30%;">
                <span style="color: #aaa">自定义值：</span>
                <a-input  :disabled="threeDisabled || !isEdit"  :maxlength="pageParameter.fileEncodingRules.prefixThreeLength"   v-model:value="pageParameter.fileEncodingRules.prefixThreeCustomize" style="width: 130px;float: right" @change="changeData('zdy3')"/>
              </div>
              <div style="width: 15%;padding-left: 0px">
                <a-checkbox :disabled="!isEdit" v-model:checked="pageParameter.fileEncodingRules.prefixThreeIs" @change="changeData">流水号参照</a-checkbox>
              </div>

              <div style="width: 30%;">
                <span style="color: #aaa">流水号长度：</span>
                <a-select   :disabled="!isEdit" style="width: 180px;float: right"  v-model:value="pageParameter.fileEncodingRules.serialNumLength" @select="changeData('lsh')">
                  <a-select-option value="2">
                    2
                  </a-select-option>
                  <a-select-option value="3">
                    3
                  </a-select-option>
                  <a-select-option value="4">
                    4
                  </a-select-option>
                  <a-select-option value="5">
                    5
                  </a-select-option>
                  <a-select-option value="6">
                    6
                  </a-select-option>
                  <a-select-option value="7">
                    7
                  </a-select-option>
                  <a-select-option value="8">
                    8
                  </a-select-option>
                </a-select>
              </div>
              <div style="width: 25%;">
                <span style="color: #aaa"> 起 始 值 ：</span>
                <a-input-number  :disabled="!isEdit" :min="1"  :max="maxStr" v-model:value="pageParameter.fileEncodingRules.serialNumStr" style="width: 130px;float: right" @change="changeData('qsz')"/>
              </div>
              <div style="width: 35%;">
              </div>

              <div style="width: 30%;">
                <span style="color: #aaa">分 隔 符：</span>
                <a-select  :disabled="!isEdit" style="width: 180px;float: right" v-model:value="pageParameter.fileEncodingRules.delimiter" @select="changeData('fgf')">
                  <a-select-option v-for="d in initDynamics().delimiter" :value="d.key">
                    {{ d.name }}
                  </a-select-option>
                </a-select>
              </div>
            </div>
          </div>

          <div class="acco-sideline" style="width: 90%;">
            <span>编码预览</span>
            <div class="acco-sideline-content-one">

              <div style="width: 30%;padding-top: 25px">
                <span> 编码组成 =</span>
                <a-input  disabled v-model:value="pOne" style="width: 180px;float: right;background-color: white;color: #1a1a1a" />
              </div>
              <div style="width: 30%;">
                <span style="font-size: 16px;font-weight: bold"> + </span>
                <a-input disabled v-model:value="pTwo" style="width: 180px;float: right;background-color: white;color: #1a1a1a" />
              </div>
              <div style="width: 30%;">
                <span style="font-size: 16px;font-weight: bold"> + </span>
                <a-input disabled v-model:value="pThree" style="width: 180px;float: right;background-color: white;color: #1a1a1a" />
              </div>
              <div style="width: 10%;padding-left: 0px">
                <span> <span style="font-size: 16px;font-weight: bold">+</span> 流水号 </span>
              </div>

              <div style="width: 40%;">
                <span >显示效果 ：</span>
                <a-input disabled v-model:value="showNum" style="width: 318px;float: right;background-color: white;color: #1a1a1a" />
              </div>
              <div style="width: 60%;padding-left: 0px">
                <span>#为自定义编码对应前缀从左到右切断的占位长度,不足则补零</span>
              </div>

            </div>
          </div>

        </div>
      </PageWrapper>
    </div>
  </div>
</template>
<script setup lang="ts">
import {BasicTable, useTable} from '/@/components/Table'
import {PageWrapper} from '/@/components/Page'
import TypeTree from './TypeTree.vue'
import {useModal} from '/@/components/Modal'
import {
  CaretDownFilled,
  FormOutlined,
  DeleteOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  SettingFilled,
  SyncOutlined,
  PicLeftOutlined,
  EditFilled,
  PieChartFilled,
  FilterFilled, CheckOutlined, SortDescendingOutlined, SortAscendingOutlined, EditOutlined,PicRightOutlined,UnorderedListOutlined
} from '@ant-design/icons-vue'
import {onMounted, reactive, ref, watch} from 'vue'
import {
  Input as AInput,
  Select as ASelect,
  Popover as APopover,
  Tabs as ATabs,
  DatePicker as ADatePicker,
  Popconfirm as APopconfirm,
  Checkbox as ACheckbox,
  Radio as ARadio,
  Table as ATable,
  Drawer as ADrawer,
  message,
  Tag as ATag,
  InputNumber as AInputNumber
} from "ant-design-vue";
const ASelectOption = ASelect.Option
const AInputSearch = AInput.Search
const ATabPane = ATabs.TabPane
const ARadioButton = ARadio.Button
const ARadioGroup = ARadio.Group
import {
  getCurrentAccountName, getThisIndexImg,
} from "/@/api/task-api/tast-bus-api";
import {cloneDeep} from "lodash-es";
import {initDynamics,getQz} from "./data";
import {add,load} from "/@/api/group/FileEncodingRules";
import {useMessage} from "/@/hooks/web/useMessage";
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
const {closeCurrent} = useTabs(router);

const formItems = ref({
  selectType: '1'
})

const checked =  ref(false)
const isEdit =  ref(false)
const fileType =  ref('1-1')
const {
  createConfirm,
  createWarningModal
} = useMessage()

const pageParameter = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '湖北万亚软件技术有限公司',
  ifUnit: false,
  total: 0,

  fileEncodingRules:{
    fileType: fileType.value,
    tableName: '',
    codeWay: '0',
    isManual: true,
    autoNum: true,
    serialNumLength: '2',
    serialNumStr: 1,
    delimiter: '1',
    prefixOne: '',
    prefixOneLength: '',
    prefixOneCustomize: '',
    prefixOneIs: false,
    prefixTwo: '',
    prefixTwoLength: '',
    prefixTwoCustomize: '',
    prefixTwoIs: false,
    prefixThree: '',
    prefixThreeLength: '',
    prefixThreeCustomize: '',
    prefixThreeIs: false,
    showRules: '',

  }
})

const pageParameter2 = reactive({
  showRulesSize: 'MIN',
  companyCode: '100',
  companyName: '湖北万亚软件技术有限公司',
  ifUnit: false,
  total: 0,

  fileEncodingRules2:{
    fileType: fileType.value,
    tableName: '',
    codeWay: '0',
    isManual: true,
    autoNum: true,
    serialNumLength: '2',
    serialNumStr: 1,
    delimiter: '1',
    prefixOne: '',
    prefixOneLength: '',
    prefixOneCustomize: '',
    prefixOneIs: false,
    prefixTwo: '',
    prefixTwoLength: '',
    prefixTwoCustomize: '',
    prefixTwoIs: false,
    prefixThree: '',
    prefixThreeLength: '',
    prefixThreeCustomize: '',
    prefixThreeIs: false,
    showRules: '',

  }
})
const fileEncodingRules2 = ref({
  fileType: fileType.value,
  tableName: '',
  codeWay: '0',
  isManual: true,
  autoNum: true,
  serialNumLength: '2',
  serialNumStr: 1,
  delimiter: '1',
  prefixOne: '',
  prefixOneLength: '',
  prefixOneCustomize: '',
  prefixOneIs: false,
  prefixTwo: '',
  prefixTwoLength: '',
  prefixTwoCustomize: '',
  prefixTwoIs: false,
  prefixThree: '',
  prefixThreeLength: '',
  prefixThreeCustomize: '',
  prefixThreeIs: false,
  showRules: '',

})

const thisCheckKey = ref('0')
//拼接显示
const pOne = ref('前缀一')
const pTwo = ref('前缀二')
const pThree = ref('前缀三')
const showNum = ref('#0001')
const prefixOneList = ref([])
const prefixTwoList = ref([])
const prefixThreeList = ref([])
const oneDisabled = ref(true)
const twoDisabled = ref(true)
const threeDisabled = ref(true)

const maxStr = ref('99')

const [registerEditPage, {openModal: openEditPage}] = useModal()
const [registerEditListPage, {openModal: openEditListPage}] = useModal()
const val: any = {
  id: '',
  flag: '',
  unitCode: '',
  unitName: '',
  typename: '',
}

// 默认账套编码
let defalutCompany = getCurrentAccountName(true)
// 切换后账套编码
const dynamicCompany = ref(defalutCompany)
const dynamicTenantId = ref(getCurrentAccountName(true))
const accId = ref(getCurrentAccountName(false))

watch(()=>pageParameter.fileEncodingRules.prefixOne,(newValue, oldValue)=>{//通过一个函数返回要监听的属性
  if(oldValue != ''  && newValue===undefined){
    pageParameter.fileEncodingRules.prefixOne = ''
    pageParameter.fileEncodingRules.prefixOneLength = ''
    pageParameter.fileEncodingRules.prefixOneCustomize = ''
    changeData('one')
  }
})

watch(()=>pageParameter.fileEncodingRules.prefixTwo,(newValue, oldValue)=>{//通过一个函数返回要监听的属性
  if(oldValue != ''  && newValue===undefined){
    pageParameter.fileEncodingRules.prefixTwo = ''
    pageParameter.fileEncodingRules.prefixTwoLength = ''
    pageParameter.fileEncodingRules.prefixTwoCustomize = ''
    changeData('two')
  }
})

watch(()=>pageParameter.fileEncodingRules.prefixThree,(newValue, oldValue)=>{//通过一个函数返回要监听的属性
  if(oldValue != ''  && newValue===undefined){
    pageParameter.fileEncodingRules.prefixThree = ''
    pageParameter.fileEncodingRules.prefixThreeLength = ''
    pageParameter.fileEncodingRules.prefixThreeCustomize = ''
    changeData('three')
  }
})

function selectOne(data) {
  if(data === '88'){
    oneDisabled.value =  true
    pageParameter.fileEncodingRules.prefixOneCustomize = ''
    //根据选中设置长度
    pageParameter.fileEncodingRules.prefixOneLength  = getQz(fileType.value).qz.find(v=> v.key === data).lenght
  }else{
    oneDisabled.value =  false
    pageParameter.fileEncodingRules.prefixOneLength = '2'
  }
  changeData('one')
}
function selectTwo(data) {
  if(data === '88'){
    twoDisabled.value =  true
    pageParameter.fileEncodingRules.prefixTwoCustomize = ''
    pageParameter.fileEncodingRules.prefixTwoLength  = getQz(fileType.value).qz.find(v=> v.key === data).lenght
  }else{
    twoDisabled.value =  false
    pageParameter.fileEncodingRules.prefixTwoLength = '2'
  }
  changeData('two')

}
function selectThree(data) {
  if(data === '88'){
    pageParameter.fileEncodingRules.prefixThreeCustomize = ''
    threeDisabled.value =  true
    pageParameter.fileEncodingRules.prefixThreeLength  = getQz(fileType.value).qz.find(v=> v.key === data).lenght
  }else{
    threeDisabled.value =  false
    pageParameter.fileEncodingRules.prefixThreeLength = '2'

  }
  changeData('three')
}
async function handleSelect(data) {
  if (null != data.key) {
    // pageParameter.fileEncodingRules = pageParameter2.fileEncodingRules2
    pageParameter.fileEncodingRules.fileType = data.key
    pOne.value = '前缀一'
    pTwo.value = '前缀二'
    pThree.value = '前缀三'
    showNum.value = '#0001'
    fileType.value = data.key
    console.log(data.key)
    if(fileType.value != '0') {
      if(getQz(fileType.value)){
        prefixOneList.value = getQz(fileType.value).qz
        prefixTwoList.value = getQz(fileType.value).qz
        prefixThreeList.value = getQz(fileType.value).qz
      }
      const d = await load(data.key)
      if(d.fileType){
        pageParameter.fileEncodingRules = d
        pageParameter.fileEncodingRules.isManual = d.isManual === 'true' ? true:false
        pageParameter.fileEncodingRules.autoNum = d.autoNum === 'true' ? true:false

        pageParameter.fileEncodingRules.prefixOneIs = d.prefixOneIs === 'true' ? true:false
        pageParameter.fileEncodingRules.prefixTwoIs = d.prefixTwoIs === 'true' ? true:false
        pageParameter.fileEncodingRules.prefixThreeIs = d.prefixThreeIs === 'true' ? true:false
        changeData('load')
      }else{
        //默认值 手动编码 无分隔符
        /*pageParameter.fileEncodingRules.codeWay = '1'
        pageParameter.fileEncodingRules.delimiter = '1'
        pageParameter.fileEncodingRules.serialNumLength = '3'
        pageParameter.fileEncodingRules.serialNumStr = '1'*/
        //pageParameter.fileEncodingRules = pageParameter2.fileEncodingRules2
        pageParameter.fileEncodingRules = {
          fileType: fileType.value,
          codeWay: '0',
          isManual: false,
          autoNum: false,
          serialNumLength: '2',
          serialNumStr: 1,
          delimiter: '1',
          prefixOne: '',
          prefixOneLength: '',
          prefixOneCustomize: '',
          prefixOneIs: false,
          prefixTwo: '',
          prefixTwoLength: '',
          prefixTwoCustomize: '',
          prefixTwoIs: false,
          prefixThree: '',
          prefixThreeLength: '',
          prefixThreeCustomize: '',
          prefixThreeIs: false,
        }
      }
    }else{
      prefixOneList.value = []
      prefixTwoList.value = []
      prefixThreeList.value = []
    }
  }
}

function doEdit() {
  if(isEdit.value === true){
    isEdit.value = false
  }else{
    isEdit.value = true
  }
}
function backEdit() {
  if(isEdit.value === false){
    isEdit.value = true
  }else{
    isEdit.value = false
  }
}

//编码预览
function changeData(data) {
  if(typeof(pageParameter.fileEncodingRules.prefixOneIs) === 'undefined' || typeof(pageParameter.fileEncodingRules.prefixTwoIs) === 'undefined' || typeof(pageParameter.fileEncodingRules.prefixThreeIs) === 'undefined'){
    return
  }
  //修改长度超过自定义长度 则置空
  if(data === 'zw1'){
    if(pageParameter.fileEncodingRules.prefixOneCustomize.length > pageParameter.fileEncodingRules.prefixOneLength){
      pageParameter.fileEncodingRules.prefixOneCustomize = ''
    }
  }
  if(data === 'zw2'){
    if(pageParameter.fileEncodingRules.prefixTwoCustomize.length > pageParameter.fileEncodingRules.prefixTwoLength){
      pageParameter.fileEncodingRules.prefixTwoCustomize = ''
    }
  }
  if(data === 'zw3'){
    if(pageParameter.fileEncodingRules.prefixThreeCustomize.length > pageParameter.fileEncodingRules.prefixThreeLength){
      pageParameter.fileEncodingRules.prefixThreeCustomize = ''
    }
  }

  //修改触发  如果是前缀先判断是否勾选
  showNum.value = ''
  let s = ''
  let sufix = '*'
  if(pageParameter.fileEncodingRules.prefixOneIs === true){
    const d = prefixOneList.value.find(v=> pageParameter.fileEncodingRules.prefixOne === v.key)
    if(d){
      if(d.key === '99'){
        //判断是不是自定义 是自定义
        oneDisabled.value = false
        pOne.value = pageParameter.fileEncodingRules.prefixOneCustomize
        s = s+pageParameter.fileEncodingRules.prefixOneCustomize.padStart(pageParameter.fileEncodingRules.prefixOneLength, '#')+sufix
      }else{
        //区分001、日期
        if(d.key === '88'){
          // 获取当前日期
          var date = new Date();
          // 获取当前月份
          var nowMonth = date.getMonth() + 1;
          // 对月份进行处理，1-9月在前面添加一个“0”
          if (nowMonth >= 1 && nowMonth <= 9) {
            nowMonth = "0" + nowMonth;
          }
          pOne.value = d.name
          s = s+(date.getFullYear() + nowMonth).padStart(pageParameter.fileEncodingRules.prefixOneLength, '#')+sufix
        }else{
          //根据占位长度生成
          oneDisabled.value = false
          pOne.value = d.name
          s = s+'1'.padStart(pageParameter.fileEncodingRules.prefixOneLength, '0')+sufix
        }
      }
    }
  }else{
    pOne.value = '前缀一'
    const d = prefixOneList.value.find(v=> pageParameter.fileEncodingRules.prefixOne === v.key)
    if(d){
      //判断是不是自定义 是自定义
      if(d.key != '88') {
        oneDisabled.value = false
      }
    }
  }

  if(pageParameter.fileEncodingRules.prefixTwoIs === true){
    const d = prefixTwoList.value.find(v=> pageParameter.fileEncodingRules.prefixTwo === v.key)
    //判断是不是自定义 是自定义
    if(d){
      if(d.key === '99'){
        twoDisabled.value = false
        pTwo.value = pageParameter.fileEncodingRules.prefixTwoCustomize
        s = s+pageParameter.fileEncodingRules.prefixTwoCustomize.padStart(pageParameter.fileEncodingRules.prefixTwoLength, '#')+sufix
      }else{
        //区分001、日期
        if(d.key === '88'){
          // 获取当前日期
          var date = new Date();
          // 获取当前月份
          var nowMonth = date.getMonth() + 1;
          // 对月份进行处理，1-9月在前面添加一个“0”
          if (nowMonth >= 1 && nowMonth <= 9) {
            nowMonth = "0" + nowMonth;
          }
          pTwo.value = d.name
          s = s+(date.getFullYear() + nowMonth).padStart(pageParameter.fileEncodingRules.prefixTwoLength, '#')+sufix
        }else{
          //根据占位长度生成
          twoDisabled.value = false
          pTwo.value = d.name
          s = s+'1'.padStart(pageParameter.fileEncodingRules.prefixTwoLength, '0')+sufix
        }
      }
    }
  }else{
    pTwo.value = '前缀二'
    const d = prefixTwoList.value.find(v=> pageParameter.fileEncodingRules.prefixTwo === v.key)
    if(d){
      //判断是不是自定义 是自定义
      if(d.key != '88'){
        twoDisabled.value = false
      }
    }
  }

  if(pageParameter.fileEncodingRules.prefixThreeIs === true){
    const d = prefixThreeList.value.find(v=> pageParameter.fileEncodingRules.prefixThree === v.key)
    //判断是不是自定义 是自定义
    if(d){
      if(d.key === '99'){
        threeDisabled.value = false
        pThree.value = pageParameter.fileEncodingRules.prefixThreeCustomize
        s = s+pageParameter.fileEncodingRules.prefixThreeCustomize.padStart(pageParameter.fileEncodingRules.prefixThreeLength, '#')+sufix
      }else{
        //区分001、日期
        if(d.key === '88'){
          // 获取当前日期
          var date = new Date();
          // 获取当前月份
          var nowMonth = date.getMonth() + 1;
          // 对月份进行处理，1-9月在前面添加一个“0”
          if (nowMonth >= 1 && nowMonth <= 9) {
            nowMonth = "0" + nowMonth;
          }
          pThree.value = d.name
          s = s+(date.getFullYear() + nowMonth).padStart(pageParameter.fileEncodingRules.prefixThreeLength, '#')+sufix
        }else{
          //根据占位长度生成
          threeDisabled.value = false
          pThree.value = d.name
          s = s+'1'.padStart(pageParameter.fileEncodingRules.prefixThreeLength, '0')+sufix
        }
      }
    }
  }else{
    pThree.value = '前缀三'
    const d = prefixThreeList.value.find(v=> pageParameter.fileEncodingRules.prefixThree === v.key)
    if(d){
      //判断是不是自定义 是自定义
      if(d.key != '88'){
        threeDisabled.value = false
      }
    }
  }

  //修改流水号长度 设置最大值
  if(data === 'lsh'){
    let num = '9'.padEnd(pageParameter.fileEncodingRules.serialNumLength, '9')
    maxStr.value = num
  }
  //后缀数值 001
  const strNum = ''+pageParameter.fileEncodingRules.serialNumStr
  let num = strNum.padStart(pageParameter.fileEncodingRules.serialNumLength, '0')
  if(pageParameter.fileEncodingRules.delimiter != 1){
    const item = initDynamics().delimiter.find(v=>v.key === pageParameter.fileEncodingRules.delimiter)
    showNum.value = (s + num).replaceAll('*', item.name)
    pageParameter.fileEncodingRules.showRules = s.replaceAll('*', item.name)

  }else{
    showNum.value = (s + num).replaceAll('*','')
    pageParameter.fileEncodingRules.showRules = s.replaceAll('*','')
  }
}

async function save() {
  pageParameter.fileEncodingRules.fileType = fileType.value
  pageParameter.fileEncodingRules.tableName = getQz(fileType.value).tableName
  await add(pageParameter.fileEncodingRules)
  message.success('保存成功！')
}

onMounted(async () => {
  handleSelect({key:'1-1'})
})

</script>
<style src="../../../../assets/styles/encoding-rules.less" lang="less" scoped></style>
<style src="../../../../assets/styles/global-menu-index-block.less" lang="less" scoped></style>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped></style>

<style lang="less" scoped>
.a-table-font-size-16 :deep(td),
.a-table-font-size-16 :deep(th) {
  font-size: 16px !important;
  padding: 5px 8px !important;
}

.a-table-font-size-12 :deep(td),
.a-table-font-size-12 :deep(th) {
  font-size: 14px !important;
  padding: 2px 8px !important;
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

:deep(.ant-calendar-picker-input.ant-input), :deep(.ant-select-single:not(.ant-select-customize-input) .ant-select-selector .ant-select-selection-search-input) {
  border: none;
  border-bottom: solid 1px rgb(191, 191, 191) !important;
  width: 100%;
}

:deep(.ant-select-single:not(.ant-select-customize-input)
.ant-select-selector
.ant-select-selection-search-input) {
  border: none !important;
}

.a-container-content-one {
  input {
    width: 30%;
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }
  .ant-select-disabled > .ant-select-selector{
    color: #252525 !important;
    background: white !important;
  }
}
/deep/.ant-select-disabled > .ant-select-selector{
  color: #252525 !important;
}
/*input:disabled{
  opacity: 1;
  background-color: white;
}
/deep/.ant-input-number-disabled{
  color: #252525 !important;
  background: white !important;
}*/
input:disabled{
  color: #252525 !important;
}
/deep/.ant-input-number-disabled{
  color: #252525 !important;
}
</style>
