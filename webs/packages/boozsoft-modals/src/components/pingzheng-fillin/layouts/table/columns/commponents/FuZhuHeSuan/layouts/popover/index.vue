<template>
  <div>
    <FuZhuHeSuanRecord v-if="showRecordManager.value" />
    <Popover
      v-if="showPopover2"
      :getPopupContainer="usePopoverContainer.getContainer"
      @visibleChange="popoverChange"
      :visible="showPopover.value"
      overlayClassName="pingzhengPopover"
      trigger="contextmenu"
    >
      <div
        class="fuZhuHeSuanPopoverMount"
        style="position: absolute; top: 0; width: 100%; height: 100%"
      ></div>
      <template #content class="abcd">
        <div
          :ref="fuZhuXiangPopoverRef"
          class="fuZhuXiangPopoverRef"
          style="position: relative; width: 500px; overflow: auto; height: 300px"
        >
          <table style="width: 95%; margin: 0 auto">
            <!--            <thead style="background: #e3e2e2;text-align: center;font-family: 'PingFang SC';">-->
            <!--            <tr style="height:50px">-->
            <!--              <template v-for="item in fuZhuHeSuanTableModel.columns">-->
            <!--                <td v-if="item.enable">{{ item.label }}</td>-->
            <!--              </template>-->
            <!--            </tr>-->
            <!--            </thead>-->
            <thead>
              <tr>
                <td style="width: 17%"></td>
                <td style="width: 83%"></td>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, rowIndex) in fuZhuHeSuanTableModel.columns" style="height: 42px">
                <template v-if="item.enable">
                  <td style="font-family: 'PingFang SC'; padding-right: 5px">
                    <span style="color: red; margin-right: 3px">*</span>
                    <div
                      style="
                        text-align: justify;
                        text-align-last: justify;
                        display: inline-block;
                        width: 60px;
                      "
                      >{{ item.label }}</div
                    >
                  </td>
                  <td
                    v-if="item.enable"
                    :style="item.style"
                    style="position: relative; overflow: hidden"
                  >
                    <div style="height: 30px">
                      <jie-suan-fang-shi
                        v-if="item.key === 'jieSuanFangShi'"
                        :ref="(e) => (fuZhuHeSuanTableModel.row[0].target[rowIndex] = e)"
                        v-model="rowFuZhuHeSuanData"
                        @right="item.next()"
                        @change="item.next()"
                        @focus="showSearch2 = true"
                      />
                      <!--                  <Select v-if="item.key==='jieSuanFangShi'"-->
                      <!--                          :ref="(e)=>fuZhuHeSuanTableModel.row[0].target[rowIndex]=e"-->
                      <!--                          v-model:value="rowFuZhuHeSuanData.jieSuanMode" style="width:100%"-->
                      <!--                          @change="item.next()" @focus="showSearch2=true">-->
                      <!--                    <Option v-for="item2 in jieSuanFangshiList" :key="item2.key"-->
                      <!--                            :label="item2.label"-->
                      <!--                            :value="item2.key">{{ item2.label }}-->
                      <!--                    </Option>-->
                      <!--                  </Select>-->

                      <PiaoJuNumber
                        v-if="item.key === 'piaoJuHao'"
                        :ref="(e) => (fuZhuHeSuanTableModel.row[0].target[rowIndex] = e)"
                        @left="item.left()"
                        @right="item.next()"
                        v-model="rowFuZhuHeSuanData.piaoJuNumber"
                      />
                      <FuZhuHeSuanPiaoJuDate
                        v-if="item.key === 'piaoJuDate'"
                        v-model="rowFuZhuHeSuanData"
                        @left="item.left()"
                        @right="item.next()"
                        :ref="(e) => (fuZhuHeSuanTableModel.row[0].target[rowIndex] = e)"
                      />
                      <JieSuanDanWeiSelect
                        :list="item.list"
                        :ref="(e) => (fuZhuHeSuanTableModel.row[0].target[rowIndex] = e)"
                        v-model="rowFuZhuHeSuanData"
                        @left="item.left()"
                        @right="item.next()"
                        v-if="item.key === 'jieSuanDanWei'"
                      />

                      <FuZhuHeSuanProjectClassSelect
                        v-if="item.domain === 'fuZhuHeSuan' && item.key == 'fzItemClass'"
                        :ref="(e) => (fuZhuHeSuanTableModel.row[0].target[rowIndex] = e)"
                        :list="item.list"
                        v-model="item.from.value"
                        @left="item.left()"
                        @right="item.right()"
                        @change="item.next()"
                        @focus="showSearch2 = true"
                      />

                      <FuZhuHeSuanProjectSelect
                        v-if="item.domain === 'fuZhuHeSuan' && item.key == 'fzItem'"
                        :ref="(e) => (fuZhuHeSuanTableModel.row[0].target[rowIndex] = e)"
                        :list="item.list"
                        v-model="item.from.value"
                        :columns="fuZhuHeSuanTableModel.columns"
                        @left="item.left()"
                        @right="item.right()"
                        @change="item.next()"
                        @focus="showSearch2 = true"
                      />
                      <!-- 辅助核算列 -->
                      <DeptPicker
                        v-else-if="item.domain === 'fuZhuHeSuan' && item.key == 'fzDept'"
                        :ref="(e) => (fuZhuHeSuanTableModel.row[0].target[rowIndex] = e)"
                        :list="item.list"
                        v-model="item.from.value"
                        @left="item.left()"
                        @right="item.right()"
                        @change="item.next()"
                        @focus="showSearch2 = true"
                      />
                      <EmpPicker
                        v-else-if="item.domain === 'fuZhuHeSuan' && item.key == 'fzEmp'"
                        :ref="(e) => (fuZhuHeSuanTableModel.row[0].target[rowIndex] = e)"
                        :list="item.list"
                        v-model="item.from.value"
                        @left="item.left()"
                        @right="item.right()"
                        @change="item.next()"
                        @focus="showSearch2 = true"
                      />
                      <CustomerPicker
                        v-else-if="item.domain === 'fuZhuHeSuan' && item.key == 'fzCustom'"
                        :ref="(e) => (fuZhuHeSuanTableModel.row[0].target[rowIndex] = e)"
                        :list="item.list"
                        v-model="item.from.value"
                        @left="item.left()"
                        @right="item.right()"
                        @change="item.next()"
                        @focus="showSearch2 = true"
                      />
                      <GysPicker
                        v-else-if="item.domain === 'fuZhuHeSuan' && item.key == 'fzGys'"
                        :ref="(e) => (fuZhuHeSuanTableModel.row[0].target[rowIndex] = e)"
                        :list="item.list"
                        v-model="item.from.value"
                        @left="item.left()"
                        @right="item.right()"
                        @change="item.next()"
                        @focus="showSearch2 = true"
                      />
                      <!-- 辅助核算列 -->
                      <FuZhuHeSuanSelect
                        v-else-if="item.domain === 'fuZhuHeSuan'"
                        :ref="(e) => (fuZhuHeSuanTableModel.row[0].target[rowIndex] = e)"
                        :list="item.list"
                        v-model="item.from.value"
                        :item="item"
                        @left="item.left()"
                        @right="item.right()"
                        @change="item.next()"
                        @focus="showSearch2 = true"
                      />
                    </div>
                  </td>
                </template>
              </tr>
            </tbody>
          </table>
        </div>
        <div style="width: 100%; text-align: center">
          <!--          <Button>增加</Button>-->
          <!--          <Button>栏目</Button>-->
          <!--          <Button>全模糊匹配</Button>-->
          <!--          <Button @click="showRecordManager.value=true">管理</Button>-->
          <Button
            type="primary"
            danger
            ref="closeRef"
            @click="giveUp()"
            style="margin-right: 10px"
            @keydown.right="fuZhuHeSuanProvide.getNextFun()"
          >
            放弃
          </Button>
          <Button
            type="primary"
            ref="closeRef"
            @click="fuZhuXiangState.closePopover()"
            @keydown.right="fuZhuHeSuanProvide.getNextFun()"
          >
            确认 ↲
          </Button>
          <!--            <div style="position:absolute;right:0;bottom:0">恢复</div>-->
        </div>
      </template>
    </Popover>
  </div>
</template>
<script setup>
  import { watch, inject, computed, ref, provide, onMounted, createVNode } from 'vue';

  import locale from 'ant-design-vue/es/date-picker/locale/zh_CN';
  import {
    Input,
    Popover,
    Select,
    Radio,
    Tabs,
    DatePicker,
    Button,
    Tooltip,
    Drawer,
    Modal,
  } from 'ant-design-vue';
  import JieSuanFangShi from '../../columns/JieSuanFangShi/index.vue';
  import JieSuanDanWeiSelect from '../../columns/JieSuanDanWei/index.vue';
  // import FuZhuHeSuanProjectClassSelect from '../../columns/FuZhuXiang/fuZhuHeSuanSelect.vue';
  import FuZhuHeSuanProjectClassSelect from '../../columns/ProjectClass/index.vue';
  import FuZhuHeSuanProjectSelect from '../../columns/Project/index.vue';
  // import FuZhuHeSuanProjectClassSelect from '../../columns/ProjectClass/index.vue';
  import FuZhuHeSuanSelect from '../../columns/FuZhuXiang/fuZhuHeSuanSelect.vue';
  import DeptPicker from '../../columns/FuZhuXiang/DeptPicker.vue';
  import EmpPicker from '../../columns/FuZhuXiang/EmpPicker.vue';
  import CustomerPicker from '../../columns/FuZhuXiang/CustomerPicker.vue';
  import GysPicker from '../../columns/FuZhuXiang/GysPicker.vue';
  import FuZhuHeSuanRecord from '../../columns/FuZhuXiang/fuZhuHeSuanRecord.vue';
  import FuZhuHeSuanPiaoJuDate from '../../columns/PiaoJuDate/index.vue';
  import PiaoJuNumber from '../../columns/PiaoJuNumber/index.vue';
  import { useFuZhuHeSuanDefineStore } from '/@/store/modules/boozsoft/account/pingzheng-model/fuZhuHeSuan/fuZhuHeSuanStore';
  import { useCompanyOperateStore } from '../../../../../../../../../store/modules/operate-company';
  import { ExclamationCircleOutlined } from '@ant-design/icons-vue';

  const kuaiJiKeMuFocus = inject('kuaiJiKeMuFocus');
  const helloRef2 = ref();
  const fuZhuXiangPopoverRef = ref();
  provide('fuZhuXiangPopoverRef', fuZhuXiangPopoverRef);
  const showPopover2 = ref(false);

  const rowFuZhuHeSuanStore = inject('rowFuZhuHeSuanStore');
  const fuZhuHeSuanProvide = useFuZhuHeSuanDefineStore(rowFuZhuHeSuanStore);
  const showPopover = computed(() => fuZhuHeSuanProvide.getShowPopover);
  const showRecordManager = computed(() => fuZhuHeSuanProvide.getShowRecordManager);
  const fuZhuHeSuanTableModel = computed(() => fuZhuHeSuanProvide.getFuZhuHeSuanTableModel);
  const rowFuZhuHeSuanData = computed(() => fuZhuHeSuanProvide.getRowFuZhuHeSuanData);
  const openKuaiJiKeMu = inject('openKuaiJiKeMu');
  const clearKuaiJiKeMuInput = inject('clearKuaiJiKeMuInput');

  function giveUp() {
    Modal.confirm({
      title: '确定清空吗?',
      icon: createVNode(ExclamationCircleOutlined),
      content: createVNode('div', { style: 'color:red;' }, '清除后将不保留此次辅助项编辑！'),
      async onOk() {
        fuZhuXiangState.closeShade();
        // openKuaiJiKeMu();
        await openKuaiJiKeMu();
        clearKuaiJiKeMuInput();
      },
      onCancel() {
        console.log('Cancel');
      },
      class: 'test',
    });
  }

  function popoverChange() {
    if (showRecordManager.value.value) {
      showPopover.value.value = true;
      showRecordManager.value.value = false;
    }
  }

  const fuZhuXiangState = inject('fuZhuXiangState');

  const usePopoverContainer = inject('usePopoverContainer');
  watch(
    showPopover,
    () => {
      if (showPopover.value) {
        usePopoverContainer.open();
      } else {
        usePopoverContainer.close();
      }
    },
    { immediate: true },
  );

  const datasourcePicker = inject('datasourcePicker');

  onMounted(() => {
    // const accountSettingInfo = useCompanyOperateStore().getCurrentAccountSettingInfo;
    // console.log(accountSettingInfo);
    showPopover2.value = true;
  });
</script>
<style scoped>
  td {
    /*border: solid 1px #828282 !important;*/
  }

  tbody td * {
    position: relative !important;
    overflow: hidden !important;
  }

  .left {
    display: flex;
    flex-direction: column;
  }
</style>
