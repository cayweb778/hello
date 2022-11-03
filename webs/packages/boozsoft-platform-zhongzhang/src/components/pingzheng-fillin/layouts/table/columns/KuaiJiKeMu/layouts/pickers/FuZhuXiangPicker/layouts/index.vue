<template>
  <div style="position: absolute; width: 100%; height: 100%">
    <!--    <EditPage @save="saveData" @register="registerEditPage"/>-->
    <KuaiJiKeMuManager />
    <Popover placement="bottom" :visible="true">
      <template #content>
        <div
          class="selectRef2"
          style="width: 270px; overflow: scroll; height: 300px; padding: 4px 8px; cursor: pointer"
        >
          <div ref="hello2222" class="asdasd">
            <div>辅助项{{ kuaiJiKeMuState }}</div>
            <div>部门: 技术部</div>
            <div>员工: 张三</div>

            <!--            <div style="text-align: center">关键字：{{ searchStr }},总数据:{{ codeList.length }}条</div>-->
            <div>
              <Button style="padding: 0 10px">增加</Button>
              <Button style="padding: 0 10px">栏目</Button>
              <Button style="padding: 0 10px">全模糊匹配</Button>
              <Button style="padding: 0 10px">全部</Button>
              <Button @click="visible = true" style="padding: 0 10px">
                <SettingOutlined />
                管理
              </Button>
            </div>
          </div>
        </div>
      </template>
      <div style="position: absolute; width: 62px; top: 0; height: 100%"> </div>
    </Popover>
  </div>
</template>
<script setup>
  import { computed, inject, nextTick, onMounted, ref, watch } from 'vue';
  import { Popover, Tooltip, Input, Select, Button, Radio, Drawer } from 'ant-design-vue';
  import {
    SearchOutlined,
    PlusOutlined,
    SettingOutlined,
    CloseOutlined,
    MoreOutlined,
  } from '@ant-design/icons-vue';
  import { BasicTable } from '/@/components/Table';
  import { VScroll } from '/@/components/VirtualScroll/index';
  import EditPage from '/@/views/boozsoft/system/acccode2/popup/edit.vue';
  import { useModal } from '/@/components/Modal';
  import KuaiJiKeMuManager from './KuaiJiKeMuManager.vue';
  import { searchFilter } from '/@/components/pingzheng-fillin/layouts/table/columns/commponents/KuaiJiKeMu/kuaiJiKeMuPopup';
  const emit = defineEmits(['close']);
  const vScroll = ref();
  const kuaiJiKeMuState = inject('kuaiJiKeMuState');
  const [registerEditPage, { openModal: openEditPage }] = useModal();
</script>
<style>
  .kuaiJiKeMuSelect {
    width: auto !important;
    color: black;
    border-radius: 10px;
    box-shadow: 0 0 6px black;
  }

  .kuaiJiKeMuSelect2 {
    display: none;
    width: auto !important;
    color: black;
    border-radius: 10px;
    box-shadow: 0 0 6px black;
  }

  .asdasd th,
  .asdasd td {
    border: solid 1px black;
    padding: 0 !important;
  }
</style>
<style scoped>
  :deep(.abc .ant-select-selector) {
    height: 100% !important;
  }

  td {
    border: solid 1px #828282;
    font-weight: 900;
  }
</style>
<style>
  .hoverRow {
    background: #bfeaff !important;
    color: white !important;
  }

  .enableRow {
    background: #b4c8e3;
    color: white !important;
  }

  .kuaiJiKeMuPopupContainer {
    position: fixed;
    left: 0px;
    top: 0px;
    width: 100%;
    height: 100%;
    z-index: 100000000;
    background: #0000004d;
  }

  .code-item {
    margin: 0;
    padding: 0;
    width: 100%;
    display: flex;
  }

  .code-item li {
    border: solid 1px black;
  }

  .code-item1 {
    width: 80px;
    font-weight: 900;
    padding-left: 6px !important;
    padding-top: 4px;
    text-align: left;
  }

  .code-item2 {
    width: 180px;
    font-weight: 900;
    padding-top: 5px;
  }

  .code-item3 {
    width: 170px;
    text-align: left;
    padding-left: 6px !important;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .code-item-title {
    font-weight: 900;
    text-align: center;
    background: #d2cfcf;
  }
</style>
