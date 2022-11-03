<template>
  <div class="app-container">
      <div class="app-container-head" style="text-align: center;"><img :src="getThisIndexImg()" class="container-head-img" draggable="false" >
      <div class="container-head-title">
        <b class="noneSpan">公司（单位）信息</b>
      </div>
        <div style="display: inline-block;">
          <AccountPicker theme="one" @reloadTable="dynamicAdReload" />
        </div>
      <div class="ant-btn-group" style="float: right">
        <button  class="ant-btn ant-btn-me" @click="closeCurrent(),router.push(`/`)">退出</button>
      </div>
    </div>
       <div class="nc-open-content" :style="{height:windowHeight+'px'}">
      <div>
        <RadioGroup name="radioGroup" :value="formItems.independent"
                       :style="{pointerEvents: 'none'}"
                       style="margin: 0;font-weight: bold;">
          <label style="margin-right: 2em;">财务管理模式</label>
          <Radio value="0">集团账套-组织</Radio>
          <Radio value="1">独立核算</Radio>
        </RadioGroup>
      </div>
      <div style="display: inline-flex;justify-content: space-between;width: 710px;margin-bottom: 2%;margin-left: 20%">
        <div style="width: 600px;padding-top: 2%;">
          <a-input
                   :value="formItems?.accName"
                   placeholder="公司（单位）名称"
                   style="font-size: 20px;width: 500px;margin-top: 2%;pointer-events: none"
          />
        </div>
        <Upload
          list-type="picture-card"
          accept=".png,.jpeg,.jpg"
          :show-upload-list="false"
          :disabled="true"
        >
          <img v-if="imageUrl" :src="imageUrl" alt="avatar"/>
          <div v-else>
            <LoadingOutlined v-if="loading"></LoadingOutlined>
            <PlusOutlined v-else></PlusOutlined>
            <img v-else src="../../../../assets/images/camera.png">
          </div>
        </Upload>
      </div>
      <br/>
      <div class="special-border-div">
        <span>基本信息</span>
        <div>
            <ul>
              <li>
                <span>公司代码:</span>
                <span>{{formItems?.coCode}}</span>
              </li>
              <li>
                <span>公司简称:</span>
                <span>{{formItems?.accNameCn}}</span>
              </li>
              <li>
                <span>上级单位:</span>
                <span>{{formItems?.corpCode}}</span>
              </li>
              <li>
                <span>所属组织:</span>
                <span>{{formatOrgnInCharge(formItems?.accGroup)}}</span>
              </li>
              <li>
                <span>税号:</span>
                <span>{{formItems?.taxCode}}</span>
              </li>
              <li>
                <span>国家（地区）:</span>
                <span>{{formatDqInCharge(formItems?.countryId)}}</span>
              </li>
              <li>
                <span>行政区划:</span>
                <span> {{formatXzInCharge(formItems?.uniqueCodeZone)}}</span>
              </li>
              <li>
                <span>所属行业:</span>
                <span>{{formatHyInCharge(formItems?.industryclassCode)}}</span>
              </li>
              <li>
                <span>联系人:</span>
                <span>{{formItems?.contacts}}</span>
              </li>
              <li>
                <span>联系电话:</span>
                <span>{{formItems?.telephone}}</span>
              </li>
              <li>
                <span>通讯地址:</span>
                <span>{{formItems?.address}}</span>
              </li>
              <li>
                <span>官方网站:</span>
                <span>{{formItems?.website}}</span>
              </li>
              <li>
                <span>简介:</span>
                <span>{{formItems?.remarks}}</span>
              </li>
            </ul>
        </div>
      </div>
      <div class="special-border-div">
        <span>会计期间</span>
        <div>
          <ul>
            <li>
              <span>年度开始日期:</span>
              <span>{{formItems?.yearStartDate}}</span>
            </li>
            <li>
              <span>年度结束日期:</span>
              <span>{{formItems?.yearEndDate}}</span>
            </li>
            <li>
              <span>会计期间数量:</span>
              <span>{{formItems?.periodNum}}</span>
            </li>
            <li>
              <span>季度期间数量:</span>
              <span>4</span>
            </li>
          </ul>
        </div>
      </div>
      <div class="special-border-div">
        <span>财务参数</span>
        <div>
          <ul>
            <li>
              <span>会计制度:</span>
              <span>{{formatStandInCharge(formItems?.accStandard)}}</span>
            </li>
            <li>
              <span>科目级次:</span>
              <span>{{formItems?.ccodeLevel}}</span>
            </li>
            <li>
              <span>默认凭证类别:</span>
              <span>{{formatTypesInCharge(formItems?.voucherTypeNum)}}</span>
            </li>
            <li>
              <span>本位币:</span>
              <span>{{formItems?.currency}} {{formItems?.currencyName}}</span>
            </li>
          </ul>
        </div>
      </div>
      <div class="special-border-div">
        <span>启用模块</span>
        <div>
          <ul>
            <li>
              <span>总账启用期间:</span>
              <span>{{formItems?.startPeriod}}</span>
            </li>
            <li>
              <span>固定资产（管理代码）启用期间:</span>
              <span></span>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { onMounted, ref} from 'vue'
import {findAllIndustry} from '/@/api/record/group/im-group'
const {closeCurrent} = useTabs(router);
import {useMessage} from "/@/hooks/web/useMessage";
import {findAllCountry} from "/@/api/record/costomer_data/customer";
import {findAllProvince} from "/@/api/record/system/zone";
onMounted( async() => {
  message.loading("加载数据中")
  organizeList.value = await getOrganizeAll()
  await initBasisTabAccoutData().then(res => {
    acountStandardList.value = res.acountStandardList
  })
  pingZhengTypeList.value = await findGroupVoucherTypes({})
  countryList.value = (await findAllCountry()).items
   zoningList.value = (await findAllProvince())
  industryList.value = (await findAllIndustry())

})
const mark = usePlatformsStore().getCurrentPlatformId
const pageReload = async (data) => {
    formItems.value = data
/*    formItems.value.industryclassCode = formItems.value.industryclassCode != '' ? JSON.parse(formItems.value.industryclassCode) : []
    formItems.value.uniqueCodeZone = formItems.value.uniqueCodeZone != '' ? JSON.parse(formItems.value.uniqueCodeZone) : []*/
    if (formItems.value.beiyong1 != null && formItems.value.beiyong1.length > 22) imageUrl.value = formItems.value.beiyong1
    changeBeforeModel = JSON.parse(JSON.stringify(formItems))
  console.log(changeBeforeModel)
}
function formatDqInCharge(psnInCharge: any) {
  if (psnInCharge == '' || null == psnInCharge) {
    return ""
  } else {
    let str = ""
    countryList.value.forEach((item) => {
      if (item.uniqueCode == psnInCharge) str = item.namech
    })
    return str
  }
}
function formatXzInCharge(arr: any) {
  if (arr == '' || null == arr) {
    return ""
  } else {
    let str = ""
    if (arr.length >= 1) {
      zoningList.value.forEach((item) => {
        if (item.value == arr[0]) {
          str += item.label
          if (item.children.length > 0) {
            item.children.forEach((item1) => {
              if (item1.value == arr[1]) {
                str = str + ' / ' + item1.label
                if (item1.children.length > 0) {
                  item1.children.forEach((item2) => {
                    if (item2.value == arr[2]) {
                      str = str + ' / ' + item2.label
                    }
                  })
                }
              }
            })
          }
        }
      })
    }
    return str
  }
}

function formatHyInCharge(arr: any) {
  if (arr == '' || null == arr) {
    return ""
  } else {
    let str = ""
    if (arr.length >= 1) {
      industryList.value.forEach((item) => {
        if (item.value == arr[0]) {
          str += item.label
          if (item.children.length > 0) {
            item.children.forEach((item1) => {
              if (item1.value == arr[1]) {
                str = str + ' / ' + item1.label
              }
            })
          }
        }
      })
    }
    return str
  }
}
function formatOrgnInCharge(psnInCharge: any) {
  let str = psnInCharge
  organizeList.value.forEach(
    function (psn: any) {
      if (psn.uniqueCode == psnInCharge) {
        str = psn.orgName
      }
    }
  )
  return str
}
function formatStandInCharge(psnInCharge: any) {
  let str = psnInCharge
  acountStandardList.value.forEach(
    function (psn: any) {
      if (psn.id == psnInCharge) {
        str = psn.tname
      }
    }
  )
  return str
}
function formatTypesInCharge(psnInCharge: any) {
  let str = psnInCharge
  pingZhengTypeList.value.forEach(
    function (psn: any) {
      if (psn.id == psnInCharge) {
        str = psn.voucherTypeName
      }
    }
  )
  return str
}
import {getThisIndexImg} from "/@/api/task-api/tast-bus-api";
const windowHeight = (window.innerHeight+140)

import {
  LoadingOutlined, PlusOutlined
} from '@ant-design/icons-vue';
import {
  Input as AInput,
  Upload, message, Radio
} from 'ant-design-vue';
const RadioGroup = Radio.Group
import {useTabs} from "/@/hooks/web/useTabs";
import router from "/@/router";
import {usePlatformsStore} from "/@/store/modules/platforms";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {getOrganizeAll} from "/@/api/record/group/im-organize";
import {
  findGroupVoucherTypes,
  initBasisTabAccoutData
} from "/@/api/record/system/financial-settings";

const AInputSearch = AInput.Search
const formItems: any = ref({})

const psnList: any = ref([])

const {createWarningModal} = useMessage();

const isLook = ref(false)
let changeBeforeModel: any = {}

// 所属行业
const industryList = ref([])
// 所以组织列表
const organizeList = ref([])
// 凭证类别
const pingZhengTypeList = ref([])
// 会计制度
const acountStandardList = ref([])
// 行政区划
const zoningList = ref([])
// 国家
const countryList = ref([])
const loading = ref<boolean>(false);
const imageUrl = ref<string>('');
const dynamicAdReload = async (e) => {
  await  pageReload(e.target)
}
</script>
<style src="../../../../assets/styles/global-menu-index.less" lang="less" scoped="scoped"></style>
<style lang="less" scoped="scoped">
.nc-open-content {
  color: #747475;
  width: 80%;
  margin-left: 10%;
  margin-top: 3%;
  .special-border-div {
    position: relative;
    border: 1px #acabab solid;
    margin: 20px 1%;

    > span {
      position: absolute;
      top: -12px;
      left: 50px;
      font-size: 15px;
      background: white;
      padding: 0 15px;
    }

    > div {
      margin: 15px;
      ul{
        li{
          display: inline-block;
          width: 20%;
          font-weight: bold;
          min-width: 240px;
          span:nth-of-type(1){
            color: #81817d;
          }
          span:nth-of-type(2){
            color: black;
            margin-left: 2em;
          }
        }
      }
    }
  }
  .ant-input {
    border: none !important;
    border-bottom: 1px solid #bdb9b9 !important;
  }
  :deep(.ant-upload-picture-card-wrapper) {
    width: 88px;

    .ant-upload-select-picture-card {
      width: 280px;
      height: 80px;
      margin-right: 0;
      margin-bottom: 0;
      .ant-upload {
        //padding: 0;
      }
    }
  }
}
</style>
