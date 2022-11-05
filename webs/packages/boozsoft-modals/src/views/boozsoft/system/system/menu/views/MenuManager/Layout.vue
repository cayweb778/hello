<template>
  <div class="menu-manager-container">
    <div class="menu-manager-box">
      <div class="menu-manager-header">
        模块名：
        <Select class="platformSelect"
                placeholder="请选择平台"
                :options="platformList.map(it=>({key:it.id,label:it.name,value:it.id}))"
                :value="viewModel.currentPlatform.name"
                @change="viewModel.reload({
                  searchInfo: {
                    platformId: platformList.filter(it=>it.id==$event)[0].id
                  }
                })"/>
      </div>
      <div class="menu-manager-content">
        <div class="menu-manager-content-backbtn-box">
          <Button class="menu-manager-content-backbtn"
                  @click="viewModel.closePlatformManager()">返回
          </Button>
        </div>
        <slot></slot>
        <div style="text-align:center">
          <Button>更新权限</Button>
        </div>
      </div>
    </div>
    <div ref="menuDrawerRef"></div>
    <MenuDrawer @register="registerDrawer"
                :getContainer="menuDrawerRef"
                @success="handleSuccess"
                :width="'50%'"
    />
  </div>
</template>
<script setup>
import {computed, inject, onMounted, ref} from "vue";
import {useDrawer} from "../../../../../../../components/Drawer";
import {Select, Button, Modal, Checkbox, Radio, Tabs} from 'ant-design-vue'
const TabPane = Tabs.TabPane
const RadioGroup = Radio.Group
const Option = Select.Option
import MenuDrawer from "../../popup/MenuDrawer.vue";
import {GetMenuTree2, getMenuTreeByPlatformId} from '../../../../../../../api/sys/menu';
import {usePlatformsStoreWidthOut} from '/@/store/modules/platforms';

import {
  delRole,
  findAll,
  saveRole,
  findByRoleName,
} from '/@/api/record/sys-role/data';
import {
  getUserMenuTreeByPlatformId,
  getUserMenuTreeByPlatformId2
} from "../../../../../../../api/sys/user";

const platformList = computed(() => viewModel.value.platformList)
const menuDrawerRef = ref()
const abc = ref([])
const roleList = ref([])
const viewModel = inject('viewModel')

function updateOwnPlatform() {
  console.log(abc.value)
}

const thisRole = ref()
onMounted(async () => {
  // getUserMenuTreeByPlatformId
  // roleList.value = (await findByRoleName('出纳 ')).map(it => {
  //   return {
  //     key: it.id,
  //     label: it.roleName,
  //     value: it
  //   }
  // })
})


onMounted(async () => {

  abc.value = await usePlatformsStoreWidthOut().getPlatformListToNames()
  // reload({
  //   searchInfo: {
  //     platformId: viewModel.value.currentPlatform.id
  //   }
  // })
})
const platformId = ref({key: 'jack'})
const [registerDrawer, {openDrawer}] = useDrawer();

function handleEdit(record2) {
  openDrawer(true, {
    record: record2.meta.target,
    isUpdate: true,
  });
}

function handleDelete(record) {
  console.log(record);
}

function handleSuccess() {
  // reload();
}

function handleCreate() {
  openDrawer(true, {
    isUpdate: false,
  });
}


const showPlatformChoose = ref(true)

function platformChoose(platformId) {
  // reload({
  //   searchInfo: {
  //     platformId
  //   }
  // })
}


</script>
<style scoped>
.ant-table-wrapper {
  background: inherit !important

}

.menu-manager-container {
  height: 100%;
  width: 100%
}

.menu-manager-box {
  height: calc(100% - 100px)
}

.menu-manager-header {
  position: fixed;
  top: 60px;
  margin-left: 17px;
  text-align: left;
  height: 50px;
  width: 100%;
  border-radius: 20px
}

.platformSelect {
  margin-top: 8px !important
}

.menu-manager-content {
  width: 100%;
  height: 500px;
  position: relative;
}

.menu-manager-content-backbtn-box {
  right: 0;
  position: absolute;
}

.menu-manager-content-backbtn-box .menu-manager-content-backbtn {
  padding: 0px 20px;
  font-size: 10px;
  height: 20px;
}


</style>
