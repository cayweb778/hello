<template>
<div>
  <div style="display: flex;color:white;width:calc(100% - 7px)">
    <div style="background:#bebebe;width:35%;border:solid 1px gray;border-right:0;border-bottom:0;color:black;text-align: center;font-weight: 900">科目编码</div>
    <div style="background:#bebebe;width:65%;border:solid 1px gray;color:black;border-bottom:0;text-align: center;font-weight: 900">科目名称</div>
  </div>
  <VScroll ref="vScroll" :bench="16" :itemHeight="27" :maxHeight="250" :items="items">
    <template #default="{ item: it, index: rowIndex }">
      <ul
        class="code-item"
        @mousedown="mousedownHello($event,it)"
        @mouseenter="it.hover = true"
        @mouseleave="it.hover = false"
        :class="getClass(it,rowIndex)"
      >
        <Item :text="it" />
      </ul>
    </template>
  </VScroll>
  <div style="display: flex;color:white;justify-content: space-between;margin-top:10px" @mousedown="setLockBlur">
      <div style="background:#5996ff;width:25%;border:solid 1px black;padding:2px 10px;border-radius:5px" @mousedown="openRecord"><SearchOutlined></SearchOutlined>选择科目</div>
      <div style="width:40%;"></div>
      <div style="background:#5996ff;width:25%;border:solid 1px black;padding:2px 10px;border-radius:5px" @mousedown="simpleAddModalShow"><SearchOutlined></SearchOutlined>新增科目</div>
      <div style="width:10%;"></div>
  </div>
  <CodePop @throwData="modalData" @register="registerCodePopPage" />
  <SimpleSaveModalPop @throwData="throwSimpleSaveData" @register="registerSimpleSavePage" />
</div>


</template>
<script setup>
  import { VScroll } from '/@/components/VirtualScroll/index';
  import {
    SearchOutlined, PlusOutlined, SettingOutlined, CloseOutlined
  } from '@ant-design/icons-vue'

  import {watch, computed, inject, nextTick,ref} from 'vue';
  import Item from './Item.vue';
  import {useModal} from "../../../Modal";
  import CodePop from '/@/views/boozsoft/system/acccode2/popup/modalPop.vue';
  import SimpleSaveModalPop from '/@/views/boozsoft/system/acccode2/popup/simpleSaveModalPop.vue';
  import {useCompanyOperateStoreWidthOut} from "../../../../store/modules/operate-company";
  import {waitLoadPingzheng} from "../../layouts/table/columns/KuaiJiKeMu/layouts/content/funs";

  const props = defineProps(['modelValue', 'items']);
  const [registerSimpleSavePage, { openModal: openSimpleSavePage }] = useModal();
  const emit = defineEmits(['change']);
  const items = computed(() => {
    return props.items == null ? [] : props.items;
  });
  const length = computed(() => {
    return items.value.length;
  });

  const companyOperateStore=useCompanyOperateStoreWidthOut()

  const [registerCodePopPage, { openModal: openCodePopPage }] = useModal();

  const getCodeInfo=inject('getCodeInfo')

  // 简单增加回调
  function throwSimpleSaveData(data) {
    console.log(data)
  }
  const tableData=inject('tableData')
  function changeKuaiJiKeMu(e) {
    nextTick(async () => {
      await waitLoadPingzheng(tableData.value.loading);
      emit('change', {key:e});
      // inputEvents.blur(e);
    });
  }
  function modalData(e) {
    changeKuaiJiKeMu(e.ccode);
  }

  // Simple Add Modal
  function simpleAddModalShow() {

    openSimpleSavePage(true, {
      database: companyOperateStore.getAccountPickerInfo.accountMode,
      accId: companyOperateStore.getAccountPickerInfo.accId,
      iyear: companyOperateStore.getAccountPickerInfo.iyear,
    })
  }
  const setLockBlur=inject('setLockBlur')

  // Code Record
  function openRecord() {

    openCodePopPage(true, {
      database: companyOperateStore.getAccountPickerInfo.accountMode,
      accId: companyOperateStore.getAccountPickerInfo.accId,

      iyear: companyOperateStore.getAccountPickerInfo.iyear,
    });
  }

  function mousedownHello(e,it){
    if(e.button!==2){
      emit('change', it)
    }
  }

  function getClass(it,rowIndex){
    return { hoverRow2: it.hover, enableRow2: it.enable,isDouble:rowIndex%2===0 }
  }


  watch(length, () => {
    if (length.value !== 0) {
      items.value[0].hover = true;
      items.value[0].enable = true;
    }
  });

  const vScroll=ref()
  defineExpose({
    vScroll
  })
</script>
<style>
.code-item{
  padding:0 !important;
  margin-top:0 !important;
}
  .hoverRow2 {
    background: #bfeaff ;
    color: gray;
  }
  .enableRow2 {
    background: #0aa699 !important;
    color: white;
  }
  .isDouble{
    background:#ebebeb
  }
</style>
