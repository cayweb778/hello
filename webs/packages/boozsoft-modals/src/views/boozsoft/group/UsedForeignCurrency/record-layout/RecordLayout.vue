<template>
  <div>
    <div class="app-container">
      <div class="app-container-head"><img :src="getThisIndexImg()" class="container-head-img"
                                           draggable="false">
        <div class="container-head-title"><b class="noneSpan">常用外币</b></div>
        <div class="ant-btn-group" data-v-a1ccd506="" style="float: right">
          <template v-for="it in recordData.arr">
            <button v-if="it.enable"
                    @click="it.fun"
                    class="ant-btn ant-btn-me" v-text="it.label"/>
          </template>
        </div>
      </div>
      <div class="app-container-neck">
        <div style="float: right; margin-left: 10px">
          <Button @click="reload">
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
                <SortAscendingOutlined/>&nbsp;小号字体&ensp;<CheckOutlined
                v-if="pageParameter.showRulesSize==='MIN'"/></span>
            </template>
            <Button @click="reload">
              <SettingFilled :style="{ fontSize: '14px' }"/>
            </Button>
          </Popover>
          <!--          <Button @click="recordData.activeKey = !recordData.activeKey">
                      <PieChartFilled :style="{ fontSize: '14px' }"/>
                    </Button>-->
        </div>
      </div>
    </div>
    <Edit @save="recordData.saveData" @register="recordData.registerEditPage"/>
    <slot></slot>
  </div>
</template>
<script setup lang="ts">
import {getThisIndexImg} from '/@/api/task-api/tast-bus-api';
import {inject, provide, reactive} from 'vue'
import {
  SettingFilled,
  SyncOutlined,
  PieChartFilled, SortAscendingOutlined, SortDescendingOutlined, CheckOutlined
} from '@ant-design/icons-vue';
import {Popover, Button} from 'ant-design-vue'
import Edit from '../popup/edit.vue'

const pageParameter = inject('pageParameter')
const recordData = inject('recordData')
const reload = () => {
  const {reload} = recordData.value.useTableParams[1]
  reload()
}
</script>
<style scope src="../../../../../assets/styles/generate-code-record-style/record.less" lang="less">
</style>
