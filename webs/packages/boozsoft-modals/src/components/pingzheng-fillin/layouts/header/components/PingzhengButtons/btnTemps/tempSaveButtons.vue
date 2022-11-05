<template>
  <div class="pingzhengDefaultButton">
    <div class="pingzhengDefaultButtonFlex">
      <Button class="hello-btn" style="width: 90px; margin-right: -2px" @click="showPingzhengList">
        <span
          style="color: rgb(0, 150, 199); font-weight: 400"
          v-text="'凭证列表'"
          class="pingzhengDefaultButtonAdd"
        ></span>
      </Button>
      <Button class="helloButton" @click="buttonEvents.backEdit">新增</Button>
      <Button class="helloButton" @click="buttonEvents.openEditPage">修改</Button>
      <Button class="helloButton" @click="buttonEvents.deletePingzheng">删除</Button>
      <!--            <Button class="helloButton" @click="emit('ok')" danger>放弃</Button>-->
      <Popover placement="bottom">
        <Button class="hello-btn" style="margin-right: -2px; font-size: 15px"
        ><span style="color: rgb(0, 150, 199); font-weight: 400">更多</span>
        </Button>
        <template #content>
          <div style="width: 120px; overflow: hidden">
            <span class="group-btn-span-special" @click="emit('ok')">&nbsp;冲销</span><br />


            <span class="group-btn-span-special" @click="buttonEvents.openAddPage({ settings: { typeLabel: '冲销' } })"
            >&nbsp;冲销</span
            ><br />

            <span class="group-btn-span-special" @click="buttonEvents.openAddPage({ settings: { typeLabel: '复制' } })"
            >&nbsp;复制</span
            ><br />


            <span class="group-btn-span-special" @click="openInsertPage(true)"
            >&nbsp;插入保存</span
            ><br />
            <span class="group-btn-span-special" @click="openSignPage(false)">&nbsp;替换</span
            ><br />
            <span
              class="group-btn-span-special"
              @click="pingzhengEditor.openPingZhengChongXiangEditer(tableSelectedRowObjs)"
            >&nbsp;复制&emsp;&emsp;</span
            ><br />
            <span
              class="group-btn-span-special"
              @click="pingzhengEditor.openPingZhengCopyEditer(tableSelectedRowObjs)"
            >&nbsp;冲销&emsp;&emsp;</span
            ><br />
            <span class="group-btn-span-special" @click="startChangeStatus(true, 1)"
            >&nbsp;作废&emsp;&emsp;</span
            ><br />
            <span class="group-btn-span-special" @click="startChangeStatus(false, 1)"
            >&nbsp;审核</span
            ><br />
            <span class="group-btn-span-special" @click="startChangeStatus(true, 3)"
            >&nbsp;出纳签字&emsp;&emsp;</span
            ><br />
            <span class="group-btn-span-special" @click="startChangeStatus(false, 3)"
            >&nbsp;主管签字</span
            ><br />
            <span class="group-btn-span-special" @click="breakNumTidyBtn"
            >&nbsp;记账&emsp;&emsp;</span
            ><br />
            <span class="group-btn-span-special" @click="delBtn"
            >&nbsp;保存为常用凭证&emsp;&emsp;</span
            ><br />
          </div>
        </template>
      </Popover>
      <Button class="helloButton" @click="exit">退出 </Button>

      <!--      <Popover placement="bottom">-->
      <!--        <Button class="helloButton" @click="emit('ok')">更多</Button>-->
      <!--        <template #content>-->
      <!--          <Button class="helloButton" @click="abc()">流量</Button>-->
      <!--        </template>-->
      <!--      </Popover>-->
    </div>
  </div>
</template>
<script setup>
import { Button, Popover } from 'ant-design-vue';

const viewModel = inject('viewModel');
const tableData = viewModel.value.instances[0].params.pingzhengData;
const pingzhengData = viewModel.value.instances[0].params.pingzhengData;
import { inject, onMounted, ref } from 'vue';
import {
  useButtonEvents,
  useKeyEvents,
} from '/@/components/pingzheng-fillin/layouts/header/components/PingzhengButtons/btnTemps/funs';
import router from "/@/router";
import {useTabs} from "/@/hooks/web/useTabs";

const showAddLoading = ref(false);
const showSaveLoading = ref(false);

const emit = defineEmits(['']);

const { thisOkFun, thisSaveFun, thisTempSave } = useKeyEvents(
  showAddLoading,
  showSaveLoading,
  emit,
);
const buttonEvents = useButtonEvents(
  viewModel.value.instances[0].params,
  tableData,
  viewModel,
  pingzhengData,
);

function showPingzhengList() {
  router.push('/zhongZhang/vouchers/manage-voucher/list-voucher')
}

onMounted(() => {
  document.addEventListener('keydown', function (e) {
    if (e.altKey && e.key == '\\') {
      thisSaveFun();
    }
    if (e.altKey && e.key == 'Z') {
      thisTempSave();
    }
    if (e.altKey && e.key == 'L') {
      console.log(333);
    }
    if (e.altKey && e.key == 'n') {
      thisOkFun();
    }
  });
});
function exit() {
  const { closeCurrent } = useTabs(router);
  closeCurrent();
}

</script>
<style>
.helloButton {
  font-size: 15px;
  margin-right: -2px;
  color: rgb(0, 150, 199);
}

.helloButton:hover {
  color: white;
}
</style>
