<template>
  <li class="col-serial"
      @mouseenter="tableData.settings.onlyShow?'':showSettingMouseenter(rowIndex)"
      style="width:63px">
    <Popover trigger="contextmenu" :visible="row.visible" placement="bottom">
      <div @click="tableData.settings.onlyShow?'':row.visible=!row.visible"
           class="pingzhengRowSetting2">
        <div @click="tableData.settings.onlyShow?'':openShowSetting(rowIndex)"
             class="pingzhengRowSetting" style="height: 52px"
             v-show="!tableData.settings.onlyShow && rowDefines[rowIndex].showSetting">
          <SettingFilled style="cursor:pointer;margin-top:19px;"/>
        </div>
        <span
          v-show="tableData.settings.onlyShow || !rowDefines[rowIndex].showSetting"
          :style="{color:row.isEmpty?'gray':''}"
          style="display:inline-block;margin-top:16px;" v-text="rowIndex + 1"/>
      </div>
      <template #content>
        <div style="width:100px">
          <ul class="settingUl" style="margin:0">
            <li style="display: flex;justify-content: space-between"
                @click="tableData.addRow(rowIndex)">
              <div>增行</div>
              <div>alt + 1</div>
            </li>
            <li style="display: flex;justify-content: space-between"
                @click="tableData.delRow()">
              <div>减行</div>
              <div> alt + 2</div>
            </li>
            <li style="display: flex;justify-content: space-between">
              <div>复制行</div>
              <div>alt + 3</div>
            </li>
            <li style="display: flex;justify-content: space-between">
              <div>插入行</div>
              <div>alt + 4</div>
            </li>
          </ul>
        </div>
      </template>
    </Popover>
  </li>
</template>
<script setup>
import {inject, computed, onMounted} from "vue";
import {Popover} from 'ant-design-vue'

import {SettingFilled} from '@ant-design/icons-vue'

const props = defineProps(['rowIndex', 'row'])

const tableData = inject('tableData')
const rowIndex = computed(() => props.rowIndex)
const row = computed(() => props.row)
const rowDefines = computed(() => tableData.value.rowDefines)

function showSettingMouseenter() {
  rowDefines.value.forEach(item => item.showSetting = false)
  rowDefines.value[rowIndex.value].showSetting = true
  if (rowDefines.value[rowIndex.value].showSettingDelayEvent != null) {
    window.clearTimeout(rowDefines.value[rowIndex.value].showSettingDelayEvent)
    rowDefines.value[rowIndex.value].showSettingDelayEvent = null
  }
}

row.value.showSettingMouseenter = showSettingMouseenter

function openShowSetting() {

}
onMounted(()=>{
  document.onkeydown = function (e) {
    if (!tableData.value.settings.onlyShow) {
      if (e.altKey && e.key == '1') {
        tableData.value.addRow(0)
      } else if (e.altKey && e.key == '2') {
        tableData.value.delRow()
      } else if (e.altKey && e.key == '3') {
      } else if (e.altKey && e.key == '4') {
      }
    }

  }
})



// function showSettingMouseleave(rowIndex) {
//   rowDefines.value[rowIndex].showSettingDelayEvent = setTimeout(() => {
//     rowDefines.value[rowIndex].showSetting = false
//   }, 2000)
// }

</script>
