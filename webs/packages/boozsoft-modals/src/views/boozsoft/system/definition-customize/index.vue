<template>
  <div class="app-container">
    <div class="app-container-top lcr-theme-div">
      <div>
        <div>
          <ProfileOutlined style="font-size: 50px;color:rgb(0, 150, 199) "/>
        </div>
        <div>  <AccountPicker theme="three" @reloadTable="dynamicAdReload" /></div>
      </div>
      <div>
        <span style="font-size: 36px; color: rgb(0, 150, 199); font-weight: bold;">自定义结转</span>
      </div>
      <div>
        <div>
          <button
            type="button"
            class="ant-btn"
            @click="openAddPage()"><span>新增模板</span></button>
          <button
            type="button"
            class="ant-btn"
            v-show="!displayType"
            @click="performOperation('edit')"
          ><span>修改</span></button>
          <button
            type="button"
            class="ant-btn"
            v-show="!displayType"
            @click="performOperation('del')"
          ><span>删除</span></button>
          <button
            type="button"
            class="ant-btn"><span>打印</span></button>
          <button
            type="button"
            class="ant-btn"><span>导入</span></button>
          <button
            type="button"
            class="ant-btn"><span>导出</span></button>
        </div>
        <div >
          <div class="acttd-right-d-search">
            <a-input-search
              placeholder=""
              style="width: 200px; border-radius: 4px"
              @search="onSearch"
            />
          </div>
          <div class="acttd-right-d-btns">
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

              <a-button v-show="!displayType">
                <SettingFilled :style="{ fontSize: '14px' }"/>
              </a-button>
            </a-popover>
            <a-button @click="switchFun">
              <PieChartFilled :style="{ fontSize: '14px' }"/>
            </a-button>
          </div>
        </div>
      </div>
    </div>

    <div class="app-container-bottom">
      <EditPage @save="saveData" @register="registerEditPage" :dynamicTenantId="pageParameter.dynamicTenantId"/>
      <CustomizePlate v-if="displayType" v-on="{edit: openModifyPage,del: delData}"/>
      <CustomizeTable v-else v-on="{edit: openModifyPage,del: delData}"/>
    </div>
  </div>
</template>
<script setup="props, {emit}" lang="ts">

import {
  findAllCorresponds,
  carryoverSaveApi,
  carryoverDeleteApi,
} from '/@/api/record/system/definition-correspond';
import {nextTick, onMounted, provide, reactive, ref} from 'vue';
import {useModal} from "/@/components/Modal";
import EditPage from './popup/edit.vue';
import CustomizePlate from './popup/CustomizePlate.vue';
import CustomizeTable from './popup/CustomizeTable.vue';
import AddPage from './popup/save.vue'
import Query from './popup/query.vue'

import {saveBank} from "/@/api/record/system/bank";
import {useMessage} from "/@/hooks/web/useMessage";
import {
 PieChartFilled,SortAscendingOutlined,SortDescendingOutlined,CheckOutlined,SettingFilled,ProfileOutlined
} from '@ant-design/icons-vue';
import {
  Input as AInput,
  Button as AButton,Popover as APopover
} from "ant-design-vue";
import {useRouteApi} from "/@/utils/boozsoft/datasource/datasourceUtil";
import {getThisIndexImg} from "/@/api/task-api/tast-bus-api";
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
const AInputSearch= AInput.Search;
const {
  createConfirm,createWarningModal
} = useMessage()
const pageParameter = reactive({
  templateList: [],
  dynamicTenantId: '',
  showRulesSize: 'MIN'
})
const displayType = ref(true)
const tableSelectedRowObjs = ref([])
provide('pageParameter',pageParameter)
provide('tableSelectedRowObjs',tableSelectedRowObjs)

async function fetch() {
  const res = await useRouteApi(findAllCorresponds, {schemaName: pageParameter.dynamicTenantId})({carryOverModule: '2'})
  pageParameter.templateList = res
}
const [registerQueryPage, {openModal: openQueryPageM}] = useModal()
const [registerEditPage, {openModal: openEditPage}] = useModal()

const openAddPage = () => {
  openEditPage(true, {
    data: {}
  })
}
const openModifyPage = (data: any) => {
  openEditPage(true, {
    data: data
  })
}

async function saveData(data: any) {
  await useRouteApi(carryoverSaveApi,{schemaName:pageParameter.dynamicTenantId})(data)
  await fetch()
}

async function delData(data: any) {
  createConfirm({
    iconType: 'warning',
    title: '模板删除',
    content: '请确认是否删除【'+ data.templateName +'模板】操作!',
    onOk: async() => {
      await useRouteApi(carryoverDeleteApi,{schemaName:pageParameter.dynamicTenantId})({id: data.id})
      await fetch()
    }
  });
}
async function onSearch(){

}
const switchFun = () => {
  displayType.value = !displayType.value
}
const dynamicAdReload = async (obj) => {
  pageParameter.dynamicTenantId = obj.accountMode
  await fetch()
}

function loadPage() {

}
function toAccVoucher(item) {
  openQueryPageM(true, {
    data: item,
    dynamicTenantId:pageParameter.dynamicTenantId
  })
}
const performOperation = (type) => {
  if (tableSelectedRowObjs.value.length == 0){
    createWarningModal({title: '温馨提示',content:'请选择一条数据'})
  }else if (type == 'edit'){
    openModifyPage(tableSelectedRowObjs.value[0])
  }else{
    delData(tableSelectedRowObjs.value[0])
  }
}
</script>
<style src="../../../../assets/styles/global-menu-index.less" lang="less"></style>
<style scoped lang="less">
:deep(.ant-card-body) {
  padding: 0px;
  border-left: 2px solid rgb(1, 143, 251);
  box-shadow: rgb(72 113 140) -3px 1px 7px -1px;
}
@Global-Border-Color: #c9c9c9; // 全局下划线颜色
@Global-Label-Color: #666666; // 全局#c9c9c9颜色
@Global-Comm-BcOrText-Color: #0096c7; // 全局#c9c9c9颜色
@Global-FROM-BC-Color: #eeeeee; // 全局#c9c9c9颜色
.lcr-theme-div{
  display: inline-flex;justify-content: space-between;width: 100%;height: 100px;
  >div:nth-of-type(1){
    width: 40%;
    position: relative;
    display: block;
    >div:nth-of-type(1){width: 64px;display: inline-block;text-align: center;    top: 12px;
      position: inherit
    }
    >div:nth-of-type(2){

      width: calc( 100% - 64px);display: inline-block;
    /*    :deep(.account-picker){
          span:not(:nth-of-type(2)){
            color: white !important;
          }
        }*/
    }
  }
  >div:nth-of-type(2){
    width: 20%;text-align:center;
    >div:nth-of-type(1){
      color: @Global-Comm-BcOrText-Color;
      font-weight: bold;
      font-size: 24px;
    }
    >div:nth-of-type(2){margin-top: 14px;}
  }
  >div:nth-of-type(3){
    width: 40%;text-align: right;
    display: block;
    >div:nth-of-type(1){
      .actod-btn {
        color: @Global-Comm-BcOrText-Color;
        font-size: 14px;
        margin: 0 1px 0 0;
      }

      .actod-btn-last {
        border-right: 1px solid @Global-Border-Color;
      }

      .actod-btn:hover {
        background-color: @Global-Comm-BcOrText-Color;
        color: white;
        font-weight: bold;
        border: 1px solid white;
      }
    }
    >div:nth-of-type(2){
      display: inline-flex;justify-content: space-between;margin-top: 15px;
      .acttd-right-d-search {
        .acttdrd-search-select {
          width: 120px;

          :deep(.ant-select-selector) {
            border-color: @Global-Border-Color;
            border-radius: 2px 0 0 2px;
          }
        }

        .acttdrd-search-input {
          width: 180px;
          border-radius: 0 2px 2px 0;
          border-color: @Global-Border-Color;
          border-left: none!important;
        }
      }

      .acttd-right-d-btns {
        margin-left: 10px;

        .acttdrd-btn {
          color: @Global-Comm-BcOrText-Color;
          margin-left: 2px;
        }
        .acttdrd-btn:hover{
          border-color: #ffffff;
          color: #ffffff;
          background-color: @Global-Comm-BcOrText-Color;
        }
      }
    }
  }
}
.app-container {
  padding: 0px;
  margin: 10px 10px 5px;
}
</style>
