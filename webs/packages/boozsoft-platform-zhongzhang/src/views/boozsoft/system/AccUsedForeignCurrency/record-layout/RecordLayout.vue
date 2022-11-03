<template>
  <div>
    <div class="app-container lcr-theme-div">
      <div>
        <div>
          <ProfileOutlined style="color: #0096c7;font-size: 50px;"/>
        </div>
        <div> <AccountPicker theme="three" @reloadTable="dynamicAdReload"/></div>
      </div>
      <div>
        <div style="margin-top: 5%;">  <b class="noneSpan" style="font-size: 26px;color: #0096c7;">常用外币</b></div>
<!--        <div></div>-->
      </div>
      <div>
        <div>
          <template v-for="it in recordData.arr">
            <button v-if="it.enable"
                    @click="it.fun"
                    class="ant-btn ant-btn-me" v-text="it.label"/>
          </template>
        </div>
        <div>
          <div>
          </div>
          <div>
            <Button @click="pgaeReload()">
              <SyncOutlined :style="{ fontSize: '14px' }"/>
            </Button>
            <Popover v-if="recordData.activeKey" placement="bottom">
              <template #content>
              <span class="group-btn-span-special2"
                    @click="pageParameter.showRulesSize = 'MAX'"
                    :style="pageParameter.showRulesSize==='MAX'?{backgroundColor: '#0096c7',color: 'white'}:''">
              <SortDescendingOutlined/>&nbsp;大号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MAX'"/></span><br/>
                <span class="group-btn-span-special2" @click="pageParameter.showRulesSize = 'MIN'"
                      :style="pageParameter.showRulesSize==='MIN'?{backgroundColor: '#0096c7',color: 'white'}:''">
              <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined v-if="pageParameter.showRulesSize==='MIN'"/></span>
              </template>
              <Button @click="pgaeReload">
                <SettingFilled :style="{ fontSize: '14px' }"/>
              </Button>
            </Popover>
          </div>
        </div>
      </div>
    </div>

    <Edit @save="recordData.saveData" @register="recordData.registerEditPage"/>
    <div v-if="isLoad">
      <slot></slot>
    </div>
  </div>
</template>
<script setup lang="ts">
import {inject, provide, reactive, ref} from 'vue'
import AccountPicker from "/@/boozsoft/components/AccountPicker/AccountPicker.vue";
import {
  SettingFilled,
  SyncOutlined,
  PieChartFilled, SortAscendingOutlined, SortDescendingOutlined, CheckOutlined,ProfileOutlined
} from '@ant-design/icons-vue';
import {Popover, Button} from 'ant-design-vue'
import Edit from '../popup/edit.vue'
import {findByAccId, findDataBase} from "/@/api/record/system/account";
import {getThisAdInfoData} from "/@/api/record/system/financial-settings";

const pageParameter:any = inject('pageParameter')
const recordData:any = inject('recordData')
const isLoad = ref(false)
const pgaeReload = () => {
  if (null != recordData.value?.useTableParams){
    const {reload} = recordData.value?.useTableParams[1]
    reload()
  }
}
const dynamicAdReload = async (obj) => {
  // 重新查询账套编码信息
  await findDataBase(obj.accId,obj.year).then(item=>{
    pageParameter.value.tenantId = item.accountMode
    isLoad.value = true
    // 进页面执行
    getThisAdInfoData({'accId': item.accountId}).then(res=>{
      if(res.independent>0){
        console.log('独立账套')
      }else{
        pageParameter.value.independent = '0'
        pageParameter.value.accOrg = res.accGroup
        console.log('集团账套')
      }
    })
    pgaeReload()
  })
}
</script>
<style scope src="../../../../../assets/styles/generate-code-record-style/record.less" lang="less">
</style>
